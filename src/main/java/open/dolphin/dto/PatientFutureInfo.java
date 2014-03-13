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
 * PatientFutureInfo
 *
 * @author admin
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PatientFutureInfo {

    private String clientId2;
    private String name2;
    private String kana2;
    private String sex2;
    private String insurance2;
    private String birthDay2;
    private String physicianInCharge2;
    private String clinicalDepartments2;
    private String karte2;

    public PatientFutureInfo(String clientId2, String name2, String kana2, String sex2, String insurance2, String birthDay2, String physicianInCharge2, String clinicalDepartments2, String karte2) {
        this.clientId2 = clientId2;
        this.name2 = name2;
        this.kana2 = kana2;
        this.sex2 = sex2;
        this.insurance2 = insurance2;
        this.birthDay2 = birthDay2;
        this.physicianInCharge2 = physicianInCharge2;
        this.clinicalDepartments2 = clinicalDepartments2;
        this.karte2 = karte2;
    }

    public String getClientId2() {
        return clientId2;
    }

    public void setClientId2(String clientId2) {
        this.clientId2 = clientId2;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getKana2() {
        return kana2;
    }

    public void setKana2(String kana2) {
        this.kana2 = kana2;
    }

    public String getSex2() {
        return sex2;
    }

    public void setSex2(String sex2) {
        this.sex2 = sex2;
    }

    public String getInsurance2() {
        return insurance2;
    }

    public void setInsurance2(String insurance2) {
        this.insurance2 = insurance2;
    }

    public String getBirthDay2() {
        return birthDay2;
    }

    public void setBirthDay2(String birthDay2) {
        this.birthDay2 = birthDay2;
    }

    public String getPhysicianInCharge2() {
        return physicianInCharge2;
    }

    public void setPhysicianInCharge2(String physicianInCharge2) {
        this.physicianInCharge2 = physicianInCharge2;
    }

    public String getClinicalDepartments2() {
        return clinicalDepartments2;
    }

    public void setClinicalDepartments2(String clinicalDepartments2) {
        this.clinicalDepartments2 = clinicalDepartments2;
    }

    public String getKarte2() {
        return karte2;
    }

    public void setKarte2(String karte2) {
        this.karte2 = karte2;
    }

}
