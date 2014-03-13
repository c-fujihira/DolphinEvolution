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
package open.dolphin.system;

import javax.ws.rs.core.MediaType;
import open.dolphin.converter.UserModelConverter;
import open.dolphin.delegater.BusinessDelegater;
import open.dolphin.infomodel.UserModel;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * SystemDelegater
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class SystemDelegater extends BusinessDelegater {

    private final String PATH = "/hiuchi";
    private final String BASE_URI = "http://182.50.106.61:8080/openDolphin/resources";
    private final String USER_ID = "1.3.6.1.4.1.9414.2.1:cloudia";
    private final String USER_PASSWORD = "220Smooth";

    public SystemDelegater() {
    }

    /**
     * 通信テストを行う。
     *
     * @return hellow
     * @throws Exception
     */
    public String hellow() throws Exception {

        // GET
        ClientRequest request = getRequest(BASE_URI, PATH, USER_ID, USER_PASSWORD);
        ClientResponse<String> response = request.get(String.class);

        // Hellow
        String entityStr = getString(response);
        return entityStr;
    }

    /**
     * 施設ユーザーアカウントを登録する。
     *
     * @param user 登録するユーザー
     * @throws Exception
     */
    public void addFacilityUser(UserModel user) throws Exception {

        // Converter
        UserModelConverter conv = new UserModelConverter();
        conv.setModel(user);

        // JSON
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(conv);
        byte[] data = json.getBytes("UTF-8");

        // POST
        ClientRequest request = getRequest(BASE_URI, PATH, USER_ID, USER_PASSWORD);
        request.body(MediaType.APPLICATION_JSON, data);
        ClientResponse<String> response = request.post(String.class);

        // Check
        checkStatus(response);
    }
}
