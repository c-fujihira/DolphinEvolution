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
package open.dolphin.delegater;

import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * サーバーステータス取得デリゲータ
 *
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class SystemRestDelegater extends BusinessDelegater implements SystemDelegater {

    @Override
    public String getServerStatus() throws Exception {

        String path = "/system/status";
        ClientResponse<String> response;

        try {
            ClientRequest request = getRequest(path);
            request.accept(MediaType.TEXT_PLAIN);
            response = request.get(String.class);
        } catch (Exception e) {
            System.out.println(e.toString());
            return "diseased";
        }
        return getString(response);
    }
}
