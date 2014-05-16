/*
 * Copyright (C) 2014 S&I Co.,Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp.
 * 825 Sylk BLDG., 1-Yamashita-Cho, Naka-Ku, Kanagawa-Ken, Yokohama-City, JAPAN.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 3 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA.
 * 
 * (R)OpenDolphin version 2.4, Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp. 
 * (R)OpenDolphin comes with ABSOLUTELY NO WARRANTY; for details see the GNU General 
 * Public License, version 3 (GPLv3) This is free software, and you are welcome to redistribute 
 * it under certain conditions; see the GPLv3 for details.
 */
package open.dolphin.delegater;

//import com.ning.http.client.SimpleAsyncHttpClient;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.ws.rs.core.MediaType;
import open.dolphin.client.ChartEventHandler;
import open.dolphin.client.ClientContext;
import open.dolphin.client.Evolution;
import open.dolphin.converter.ChartEventModelConverter;
import static open.dolphin.delegater.BusinessDelegater.CLIENTVERSION;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.project.Project;
import open.dolphin.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.DefaultHttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * State変化関連のデレゲータ オリジナル: Git Hub Masuda-Naika OpenDolphin-2.3.8m 上記の劣化版コピー:
 * minagawa JSON のハンドリングを reasteasyのjacksonで行う
 * resteasyクライントが非同期通信をサポートしていないのでAsyncHttpClientを使用する
 *
 * @author masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class ChartEventDelegater extends BusinessDelegater {

    private static final String RES_CE = "/chartEvent";
    private static final String SUBSCRIBE_PATH = RES_CE + "/subscribe";
    private static final String PUT_EVENT_PATH = RES_CE + "/event";

    private static final String ACCEPT = "Accept";
    private static final String CLINET_UUID = "clientUUID";
    private static final int CONNECTION_TIMEOUT = 20 * 1000;

    private static final boolean debug = false;
    private static final ChartEventDelegater instance = new ChartEventDelegater();
    ;
    
    //private static AsyncScheme asyncScheme;
    private DefaultHttpAsyncClient httpAsyncClient;

//minagawa^ 2013/08/29
    private ExecutorService asyncExecutor;
//minagawa$

//    static {
//        try {
//            X509TrustManager xtm = new X509TrustManager() {
//
//                @Override
//                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
//                }
//
//                @Override
//                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
//                }
//
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    return null;
//                }
//            };
//        
//            X509HostnameVerifier verifier = new X509HostnameVerifier() {
//
//                @Override
//                public void verify(String string, SSLSocket ssls) throws IOException {
//                }
//
//                @Override
//                public void verify(String string, X509Certificate xc) throws SSLException {
//                }
//
//                @Override
//                public void verify(String string, String[] strings, String[] strings1) throws SSLException {
//                }
//
//                @Override
//                public boolean verify(String string, SSLSession ssls) {
//                    return true;
//                }
//            };
//            
//            SSLContext ctx = SSLContext.getInstance("TLS");
//            ctx.init(null, new TrustManager[]{xtm}, null);
//            
//            SSLLayeringStrategy sst = new SSLLayeringStrategy(ctx, verifier);
//            asyncScheme = new AsyncScheme("https", 443, sst);
//            
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//        }
//    }
    private ChartEventDelegater() {
    }

    public static ChartEventDelegater getInstance() {
        return instance;
    }

    public int putChartEvent(ChartEventModel evt) throws Exception {

        // Converter
        ChartEventModelConverter conv = new ChartEventModelConverter();
        conv.setModel(evt);
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", evt.getIssuerUUID());

        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);

        // PUT
        ClientRequest request = getRequest(PUT_EVENT_PATH);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);

        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON, json);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        // cnt
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }

    public Future<HttpResponse> subscribe() throws Exception {
        //public Future<Response> subscribe() throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append(Project.getBaseURI());
        sb.append(SUBSCRIBE_PATH);
        String path = sb.toString();
//        
//        SimpleAsyncHttpClient async = new SimpleAsyncHttpClient.Builder()
//                            .setUrl(path)
//                            .addHeader(ACCEPT, MediaType.APPLICATION_JSON)
//                            .addHeader(USER_NAME, Project.getUserModel().getUserId())
//                            .addHeader(PASSWORD, Project.getUserModel().getPassword())
//                            .addHeader(CLINET_UUID, Dolphin.getInstance().getClientUUID())
//                            .setRequestTimeoutInMs(Integer.MAX_VALUE)
//                        .build();
//        
        //Future<Response> future = async.get();
        //return future;

//        DefaultHttpAsyncClient httpclient = new DefaultHttpAsyncClient();
//        if (ClientContext.isOpenDolphin()) {
//            httpclient = wrappClient2(httpclient);
//        }
//        HttpParams params = httpclient.getParams();
//        HttpConnectionParams.setConnectionTimeout(params, 20*1000);
//        HttpConnectionParams.setSoTimeout(params, Integer.MAX_VALUE);
//        httpclient.start();
        HttpGet request = new HttpGet(path);
        request.addHeader(ACCEPT, MediaType.APPLICATION_JSON);
        request.setHeader(USER_NAME, Project.getUserModel().getUserId());
        request.setHeader(PASSWORD, Project.getUserModel().getPassword());
        request.setHeader(CLINET_UUID, Evolution.getInstance().getClientUUID());
        request.setHeader(CLIENTVERSION, ClientContext.getProductName() + "_" + ClientContext.getVersion() + "_" + Project.getClientBuild());

//        final Future<HttpResponse> future = getHttpAsyncClient().execute(request, new FutureCallback<HttpResponse>() { 
//
//            @Override
//            public void completed(HttpResponse response) { 
//            } 
//
//            @Override
//            public void failed(Exception e) { 
//                e.printStackTrace(System.err); 
//            } 
//
//            @Override
//            public void cancelled() { 
//                System.out.println("cancelled"); 
//            } 
//        });
        Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, "REQ", request.toString());
        if (Project.getUserModel() != null) {
            Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, "PRM", MediaType.APPLICATION_JSON, Project.getUserModel().getUserId(), Project.getUserModel().getPassword(), (ChartEventHandler.getInstance() == null) ? "null" : ChartEventHandler.getInstance().getClientUUID());
        }

        Future<HttpResponse> future = getHttpAsyncClient().execute(request, null);

        return future;
    }

//minagawa^ 2013/08/29
    public Future<DResponse> subscribe2() throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getBaseURI());
        sb.append(SUBSCRIBE_PATH);
        String path = sb.toString();
        if (asyncExecutor == null) {
            asyncExecutor = Executors.newSingleThreadExecutor();
        }

        DRequest request = new DRequest(new URL(path));
        Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, "REQ", request.toString());
        if (Project.getUserModel() != null) {
            Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, "PRM", Project.getUserModel().getUserId(), Project.getUserModel().getPassword(), (ChartEventHandler.getInstance() == null) ? "null" : ChartEventHandler.getInstance().getClientUUID());
        }
        Future<DResponse> future = asyncExecutor.submit(request);
        return future;
    }
//minagawa$

    @Override
    protected void debug(int status, String entity) {
        if (debug || DEBUG) {
            super.debug(status, entity);
        }
    }

    private DefaultHttpAsyncClient getHttpAsyncClient() {

        if (httpAsyncClient != null) {
            return httpAsyncClient;
        }

//        if (!ClientContext.isOpenDolphin()) {
        try {
            httpAsyncClient = new DefaultHttpAsyncClient();
            HttpParams params = httpAsyncClient.getParams();
            HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(params, Integer.MAX_VALUE);
            httpAsyncClient.start();
        } catch (IOReactorException e) {
            e.printStackTrace(System.err);
        }

//        } else {
//
//            try {
//                X509TrustManager xtm = new X509TrustManager() {
//                    @Override
//                    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
//                    }
//
//                    @Override
//                    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
//                    }
//
//                    @Override
//                    public X509Certificate[] getAcceptedIssuers() {
//                        return null;
//                    }
//                };
//
//                X509HostnameVerifier verifier = new X509HostnameVerifier() {
//                    @Override
//                    public void verify(String string, SSLSocket ssls) throws IOException {
//                    }
//
//                    @Override
//                    public void verify(String string, X509Certificate xc) throws SSLException {
//                    }
//
//                    @Override
//                    public void verify(String string, String[] strings, String[] strings1) throws SSLException {
//                    }
//
//                    @Override
//                    public boolean verify(String string, SSLSession ssls) {
//                        return true;
//                    }
//                };
//
//                SSLContext ctx = SSLContext.getInstance("TLS");
//                ctx.init(null, new TrustManager[]{xtm}, null);
//
//                SSLLayeringStrategy sst = new SSLLayeringStrategy(ctx, verifier);
//                AsyncScheme asyncScheme = new AsyncScheme("https", 443, sst);
//
//                DefaultHttpAsyncClient base = new DefaultHttpAsyncClient();
//                ClientAsyncConnectionManager ccm = base.getConnectionManager();
//                AsyncSchemeRegistry sr = ccm.getSchemeRegistry();
//                sr.register(asyncScheme);
//
//                httpAsyncClient = new DefaultHttpAsyncClient(ccm);
//                HttpParams params = httpAsyncClient.getParams();
//                HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
//                HttpConnectionParams.setSoTimeout(params, Integer.MAX_VALUE);
//                httpAsyncClient.start();
//
//            } catch (NoSuchAlgorithmException | KeyManagementException | IOReactorException e) {
//                e.printStackTrace(System.err);
//            }
//        }
        return httpAsyncClient;
    }
}
