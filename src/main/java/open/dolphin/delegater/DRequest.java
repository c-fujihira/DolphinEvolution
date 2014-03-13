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

import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import javax.ws.rs.core.MediaType;
import open.dolphin.client.Evolution;
import open.dolphin.project.Project;

/**
 * DRequest
 *
 * @author kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class DRequest implements Callable {

    private static final String ACCEPT = "Accept";
    private static final String CLINET_UUID = "clientUUID";
    private static final String USER_NAME = "userName";
    private static final String PASSWORD = "password";

    private final URL request;

    public DRequest(URL request) {
        this.request = request;
    }

    @Override
    public DResponse call() throws Exception {
        URLConnection conn = request.openConnection();
        conn.setRequestProperty(ACCEPT, MediaType.APPLICATION_JSON);
        conn.setRequestProperty(USER_NAME, Project.getUserModel().getUserId());
        conn.setRequestProperty(PASSWORD, Project.getUserModel().getPassword());
        conn.setRequestProperty(CLINET_UUID, Evolution.getInstance().getClientUUID());
        conn.setDoInput(true);
        conn.setReadTimeout(Integer.MAX_VALUE);
        return new DResponse(conn.getInputStream());
    }
}
