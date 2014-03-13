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
import java.util.ArrayList;
import javax.ws.rs.core.MediaType;
import open.dolphin.converter.UserModelConverter;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.UserList;
import open.dolphin.infomodel.UserModel;
import open.dolphin.util.Log;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * User関連のBusiness Delegaterクラス
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class UserDelegater extends BusinessDelegater {

    public UserModel login(String fid, String uid, String password) throws Exception {

        // User PK
        StringBuilder sb = new StringBuilder();
        sb.append(fid);
        sb.append(IInfoModel.COMPOSITE_KEY_MAKER);
        sb.append(uid);
        String userPK = sb.toString();

        // PATH
        String path = "/user/" + userPK;
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // GET
        ClientRequest request = getRequest(path, userPK, password);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        BufferedReader br = getReader(response);

        // UserModel
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel user = mapper.readValue(br, UserModel.class);
        br.close();

        //20130225
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "INFO", user.getUserId() + "/" + user.getCommonName() + "/" + user.getFacilityModel().getFacilityName());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));
//        if (false) {
//            System.err.println(user.getUserId());
//            System.err.println(user.getCommonName());
//            System.err.println(user.getFacilityModel().getFacilityName());
//        }

        return user;
    }

    public UserModel getUser(String userPK) throws Exception {

        // PATH
        String path = "/user/" + userPK;
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);
        BufferedReader br = getReader(response);

        // UserModel
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserModel user = mapper.readValue(br, UserModel.class);
        br.close();
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

//        if (false) {
//            System.err.println(user.getUserId());
//            System.err.println(user.getCommonName());
//            System.err.println(user.getFacilityModel().getFacilityName());
//        }
        return user;
    }

    public ArrayList<UserModel> getAllUser() throws Exception {

        // PATH
        String path = "/user";
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);
        // GET
        ClientRequest request = getRequest(path);
        request.accept(MediaType.APPLICATION_JSON);
        ClientResponse<String> response = request.get(String.class);

        // Wrapper
        BufferedReader br = getReader(response);
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        UserList list = mapper.readValue(br, UserList.class);
        br.close();
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        // List
        return (ArrayList) list.getList();
    }

    public int addUser(UserModel userModel) throws Exception {

        // PATH
        String path = "/user";
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);
        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(userModel);

        // JSON
        ObjectMapper mapper = new ObjectMapper();
        // 2013/06/24
        mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes(UTF8);

        // POST
        ClientRequest request = getRequest(path);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);

        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON, json);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        // Count
        String entityStr = getString(response);
        int cnt = Integer.parseInt(entityStr);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RET", String.valueOf(cnt));

        return cnt;
    }

    public int updateUser(UserModel userModel) throws Exception {

        // PATH
        String path = "/user";
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(userModel);

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

        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON, json);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        // Count
        String entityStr = getString(response);
        int cnt = Integer.parseInt(entityStr);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RET", String.valueOf(cnt));

        return cnt;
    }

    public int deleteUser(String uid) throws Exception {

        // PATH
        String path = "/user/" + uid;
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // DELETE
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.delete(String.class);

        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RET", String.valueOf(1));

        // Count
        return 1;
    }

    public int updateFacility(UserModel user) throws Exception {

        // PATH
        String path = "/user/facility";
        Log.outputFuncLog(Log.LOG_LEVEL_0, "I", path);

        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(user);

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

        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "REQ", request.getUri().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "PRM", MediaType.APPLICATION_JSON, json);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RES", String.valueOf(response.getStatus()), response.getResponseStatus().toString());
        Log.outputFuncLog(Log.LOG_LEVEL_5, "I", "ENT", getString(response));

        // Count
        String entityStr = getString(response);
        int cnt = Integer.parseInt(entityStr);
        Log.outputFuncLog(Log.LOG_LEVEL_3, "I", "RET", String.valueOf(cnt));

        return cnt;
    }
}
