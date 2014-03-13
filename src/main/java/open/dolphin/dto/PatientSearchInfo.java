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
package open.dolphin.dto;

/**
 * ClientInfo
 *
 * @author admin
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PatientSearchInfo {

    private String clientId1;
    private String name1;
    private String kana1;
    private String sex1;
    private String birthDay1;
    private String receiveDay1;
    private String status1;

    public PatientSearchInfo(String clientId, String name, String kana, String sex, String birthDay, String receiveDay, String status) {
        this.clientId1 = clientId;
        this.name1 = name;
        this.kana1 = kana;
        this.sex1 = sex;
        this.birthDay1 = birthDay;
        this.receiveDay1 = receiveDay;
        this.status1 = status;
    }

    public String getClientId1() {
        return clientId1;
    }

    public void setClientId1(String clientId1) {
        this.clientId1 = clientId1;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getKana1() {
        return kana1;
    }

    public void setKana1(String kana1) {
        this.kana1 = kana1;
    }

    public String getSex1() {
        return sex1;
    }

    public void setSex1(String sex1) {
        this.sex1 = sex1;
    }

    public String getBirthDay1() {
        return birthDay1;
    }

    public void setBirthDay1(String birthDay1) {
        this.birthDay1 = birthDay1;
    }

    public String getReceiveDay1() {
        return receiveDay1;
    }

    public void setReceiveDay1(String receiveDay1) {
        this.receiveDay1 = receiveDay1;
    }

    public String getStatus1() {
        return status1;
    }

    public void setStatus1(String status1) {
        this.status1 = status1;
    }

}
