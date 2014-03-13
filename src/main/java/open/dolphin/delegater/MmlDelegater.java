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

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

/**
 * MmlDelegater
 *
 * @author kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class MmlDelegater extends BusinessDelegater {

    public void dumpPatientDiagnosisToMML(String fid) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/patient/").append(fid);
        String path = sb.toString();

        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);

        checkStatus(response);
    }

    public void dumpDocumentToMML(String fid) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/document/").append(fid);
        String path = sb.toString();

        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);

        checkStatus(response);
    }

    //-------------------------------------------------------------------------
    public void dumpAllCollection(String fid) throws RuntimeException {

        try {
            //dumpPatientToJSON(fid);
            //dumpMemoToJSON(fid);
            //dumpDiseaseToJSON(fid);
            //dumpObservationToJSON(fid);
            dumpKarteToJSON(fid);
            //dumpLetterToJSON(fid);
            //dumpLabtestToJSON(fid);

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    //-------------------------------------------------------------------------
    // Patient
    //-------------------------------------------------------------------------
    public void dumpPatientToJSON(String fid) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/patient/list/").append(fid);
        String path = sb.toString();

        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);

        String[] pks = getString(response).split(",");

        for (String str : pks) {

            sb = new StringBuilder();
            sb.append("/mml/patient/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);

            String json = getString(response);
            System.err.println(json);
        }
    }

    //-------------------------------------------------------------------------
    // Disease
    //-------------------------------------------------------------------------
    public void dumpDiseaseToJSON(String fid) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/disease/list/").append(fid);
        String path = sb.toString();

        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);

        String[] pks = getString(response).split(",");

        for (String str : pks) {

            sb = new StringBuilder();
            sb.append("/mml/disease/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);

            String json = getString(response);
            System.err.println(json);
        }
    }

    //-------------------------------------------------------------------------
    // Memo
    //-------------------------------------------------------------------------
    public void dumpMemoToJSON(String fid) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/memo/list/").append(fid);
        String path = sb.toString();

        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);

        String[] pks = getString(response).split(",");

        for (String str : pks) {

            sb = new StringBuilder();
            sb.append("/mml/memo/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);

            String json = getString(response);
            System.err.println(json);
        }
    }

    //-------------------------------------------------------------------------
    // Observation
    //-------------------------------------------------------------------------
    public void dumpObservationToJSON(String fid) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/observation/list/").append(fid);
        String path = sb.toString();

        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);

        String[] pks = getString(response).split(",");

        for (String str : pks) {

            sb = new StringBuilder();
            sb.append("/mml/observation/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);

            String json = getString(response);
            System.err.println(json);
        }
    }

    //-------------------------------------------------------------------------
    // Karte
    //-------------------------------------------------------------------------
    public void dumpKarteToJSON(String fid) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/karte/list/").append(fid);
        String path = sb.toString();

        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);

        checkStatus(response);

        String[] pks = getString(response).split(",");
        System.err.println("doc count = " + pks.length);

        int cnt = 1;
        for (String str : pks) {

            sb = new StringBuilder();
            sb.append("/mml/karte/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);

            String json = getString(response);
            sb = new StringBuilder();
            sb.append(cnt++).append(":").append(json);
            System.err.println(sb.toString());
            if (cnt == 10) {
                break;
            }
        }
    }

    //-------------------------------------------------------------------------
    // Letter
    //-------------------------------------------------------------------------
    public void dumpLetterToJSON(String fid) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/letter/list/").append(fid);
        String path = sb.toString();

        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);

        String[] pks = getString(response).split(",");

        for (String str : pks) {

            sb = new StringBuilder();
            sb.append("/mml/letter/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);

            String json = getString(response);
            System.err.println(json);
        }
    }

    //-------------------------------------------------------------------------
    // Labtest
    //-------------------------------------------------------------------------
    public void dumpLabtestToJSON(String fid) throws Exception {

        // PATH
        StringBuilder sb = new StringBuilder();
        sb.append("/mml/labtest/list/").append(fid);
        String path = sb.toString();

        // GET
        ClientRequest request = getRequest(path);
        ClientResponse<String> response = request.get(String.class);

        String[] pks = getString(response).split(",");

        for (String str : pks) {

            sb = new StringBuilder();
            sb.append("/mml/labtest/json/").append(str);
            path = sb.toString();

            // GET
            request = getRequest(path);
            response = request.get(String.class);

            String json = getString(response);
            System.err.println(json);
        }
    }
}
