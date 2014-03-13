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
 * PatientSearchSpec
 *
 * @author Minagawa,Kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PatientSearchSpec extends DolphinDTO {

    private static final long serialVersionUID = -3192512318678902328L;
    public static final int ALL_SEARCH = 0;
    public static final int ID_SEARCH = 1;
    public static final int NAME_SEARCH = 2;
    public static final int KANA_SEARCH = 3;
    public static final int ROMAN_SEARCH = 4;
    public static final int TELEPHONE_SEARCH = 5;
    public static final int ZIPCODE_SEARCH = 6;
    public static final int ADDRESS_SEARCH = 7;
    public static final int EMAIL_SEARCH = 8;
    public static final int OTHERID_SEARCH = 9;
    public static final int DIGIT_SEARCH = 10;
    public static final int DATE_SEARCH = 11;
    private int code;
    private String patientId;
    private String name;
    private String kana;
    private String roman;
    private String telephone;
    private String zipCode;
    private String address;
    private String email;
    private String otherId;
    private String otherIdClass;
    private String otherIdCodeSys;
    private String digit;

    /**
     * @param code The code to set.
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * @return Returns the code.
     */
    public int getCode() {
        return code;
    }

    /**
     * @param patientId The patientId to set.
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * @return Returns the patientId.
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param kana The kana to set.
     */
    public void setKana(String kana) {
        this.kana = kana;
    }

    /**
     * @return Returns the kana.
     */
    public String getKana() {
        return kana;
    }

    /**
     * @param roman The roman to set.
     */
    public void setRoman(String roman) {
        this.roman = roman;
    }

    /**
     * @return Returns the roman.
     */
    public String getRoman() {
        return roman;
    }

    /**
     * @param telephone The telephone to set.
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * @return Returns the telephone.
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * @param zipCode The zipCode to set.
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * @return Returns the zipCode.
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * @param address The address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return Returns the address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param email The email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Returns the email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param otherId The otherId to set.
     */
    public void setOtherId(String otherId) {
        this.otherId = otherId;
    }

    /**
     * @return Returns the otherId.
     */
    public String getOtherId() {
        return otherId;
    }

    /**
     * @param otherIdClass The otherIdClass to set.
     */
    public void setOtherIdClass(String otherIdClass) {
        this.otherIdClass = otherIdClass;
    }

    /**
     * @return Returns the otherIdClass.
     */
    public String getOtherIdClass() {
        return otherIdClass;
    }

    /**
     * @param otherIdCodeSys The otherIdCodeSys to set.
     */
    public void setOtherIdCodeSys(String otherIdCodeSys) {
        this.otherIdCodeSys = otherIdCodeSys;
    }

    /**
     * @return Returns the otherIdCodeSys.
     */
    public String getOtherIdCodeSys() {
        return otherIdCodeSys;
    }

    /**
     * @param digit The digit to set.
     */
    public void setDigit(String digit) {
        this.digit = digit;
    }

    /**
     * @return Returns the digit.
     */
    public String getDigit() {
        return digit;
    }
}
