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
package open.dolphin.utilities.utility;

import open.dolphin.utilities.common.OrcaApi;

/**
 * OrcaConnect
 *
 * @author Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class OrcaConnect extends OrcaApi {

    /**
     * コンストラクタ
     */
    public OrcaConnect(String host, String port, String user, String pass, String ver) {
        super();
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
        if (ver != null) {
            this.ver = ver;
        } else {
            this.ver = ORCAAPI_VER_47;
        }
        setVerInfo();
    }

    /**
     * ORCAの受付リストの取得
     *
     * @param date 日付
     * @return
     */
    public String getOrcaAcceptListAll(String date) {
        //OrcaConnect orca = new OrcaConnect("172.31.210.193", "8000", "ormaster", "ormaster123", "47");
        //String str = orca.getOrcaAcceptListAll("2013-06-06");
        //String str = diseaseget("10001", "2013-06");
        return acceptlst(date, "", "", "", OrcaConnect.KIND_03);
    }

    /**
     * ORCAの受付情報の削除
     *
     * @param patientID 患者ID
     * @param acceptID 受付ID
     * @param departCode 診療科コード
     * @param physicianCode ドクターコード
     * @return
     */
    public String deleteOrcaAccept(String patientID, String acceptID, String departCode, String physicianCode) {
        return acceptmod(patientID, acceptID, departCode, physicianCode, OrcaConnect.KIND_02);
    }
}
