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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.PatientModelConverter;
import static open.dolphin.delegater.BusinessDelegater.UTF8;
import open.dolphin.dto.PatientSearchSpec;
import open.dolphin.infomodel.HealthInsuranceModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientList;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.util.BeanUtils;
import open.dolphin.util.Log;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * 患者関連のBusiness Delegaterクラス
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 *
 */
public final class PatientDelegater extends BusinessDelegater {

    private static final String BASE_RESOURCE = "/patient/";
    private static final String NAME_RESOURCE = "/patient/name/";
    private static final String KANA_RESOURCE = "/patient/kana/";
    private static final String ID_RESOURCE = "/patient/id/";
    private static final String DIGIT_RESOURCE = "/patient/digit/";
    private static final String PVT_DATE_RESOURCE = "/patient/pvt/";
//minagawa^ 仮保存カルテ取得対応
    private static final String TMP_KARTE_RESOURCE = "/patient/documents/status";
//minagawa$

    /**
     * 患者を追加する。
     *
     * @param patient 追加する患者
     * @return PK
     * @throws java.lang.Exception
     */
    public long addPatient(PatientModel patient) throws Exception {

        // Converter
        PatientModelConverter conv = new PatientModelConverter();
        conv.setModel(patient);

        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);

        // POST
        ClientRequest request = getRequest(BASE_RESOURCE);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);

        // PK
        String entityStr = getString(response);
        long pk = Long.parseLong(entityStr);
        return pk;
    }

    /**
     * 患者を検索する。
     *
     * @param pid 患者ID
     * @return PatientModel
     * @throws java.lang.Exception
     */
    public PatientModel getPatientById(String pid) throws Exception {

        // PATH
        String path = ID_RESOURCE;
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        PatientModel patient;
        try (BufferedReader br = getReader(response)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            patient = mapper.readValue(br, PatientModel.class);
        }

        //20130225
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));
        return patient;
    }

    /**
     * 患者を検索する。
     *
     * @param spec PatientSearchSpec 検索仕様
     * @return PatientModel の Collection
     * @throws java.lang.Exception
     */
    public Collection getPatients(PatientSearchSpec spec) throws Exception {

        StringBuilder sb = new StringBuilder();

        switch (spec.getCode()) {

            case PatientSearchSpec.NAME_SEARCH:
                sb.append(NAME_RESOURCE);
                sb.append(spec.getName());
                break;

            case PatientSearchSpec.KANA_SEARCH:
                sb.append(KANA_RESOURCE);
                sb.append(spec.getName());
                break;

            case PatientSearchSpec.DIGIT_SEARCH:
                sb.append(DIGIT_RESOURCE);
                sb.append(spec.getDigit());
                break;

            case PatientSearchSpec.DATE_SEARCH:
                sb.append(PVT_DATE_RESOURCE);
                sb.append(spec.getDigit());
                break;
        }

        String path = sb.toString();
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);

        //20130225
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));
        PatientList list;
        try (BufferedReader br = getReader(response)) {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            list = mapper.readValue(br, PatientList.class);
        }

        // Decode
        if (list != null && list.getList() != null) {
            List<PatientModel> inList = list.getList();
            for (PatientModel pm : inList) {
                decodeHealthInsurance(pm);
            }
            return inList;

        } else {
            return null;
        }
    }

    /**
     * 患者を更新する。
     *
     * @param patient 更新する患者
     * @return 更新数
     * @throws java.lang.Exception
     */
    public int updatePatient(PatientModel patient) throws Exception {

        // PATH
        String path = BASE_RESOURCE;
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // Converter
        PatientModelConverter conv = new PatientModelConverter();
        conv.setModel(patient);

        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);

        // PUT
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.put(String.class);

        //20130225
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON, json);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        // Count
        String entityStr = getString(response);
        return Integer.parseInt(entityStr);
    }

//minagawa^ 仮保存カルテ取得対応
    public List getTmpKarte() throws Exception {

        // PATH
        String path = TMP_KARTE_RESOURCE;

        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_OCTET_STREAM);
        ClientResponse<InputStream> response = request.get(InputStream.class);

        BufferedReader br;
        br = new BufferedReader(new InputStreamReader(response.getEntity(), UTF8));
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        PatientModel[] patients = mapper.readValue(br, PatientModel[].class);
        br.close();
        
        List<PatientModel> list = new ArrayList();
        if (patients!=null && patients.length>0) {
            for (PatientModel pm : patients) {
                decodeHealthInsurance(pm);
                list.add(pm);
            }
        }
        return list;
        
    }
//minagawa$

    /**
     * バイナリの健康保険データをオブジェクトにデコードする。
     */
    private void decodeHealthInsurance(PatientModel patient) {

        // Health Insurance を変換をする beanXML2PVT
        Collection<HealthInsuranceModel> c = patient.getHealthInsurances();

        if (c != null && c.size() > 0) {

            for (HealthInsuranceModel model : c) {
                try {
                    // byte[] を XMLDecord
                    PVTHealthInsuranceModel hModel = (PVTHealthInsuranceModel) BeanUtils.xmlDecode(model.getBeanBytes());
                    patient.addPvtHealthInsurance(hModel);
                } catch (Exception e) {
                    Log.outputFuncLog(Log.LOG_LEVEL_0, "E", e.toString());
                    e.printStackTrace(System.err);
                }
            }

            c.clear();
            patient.setHealthInsurances(null);
        }
    }
}
