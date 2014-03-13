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
 * LabReceiverInfo
 *
 * @author admin
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class LabReceiverInfo {

    private String lab3;
    private String clientId3;
    private String kana3;
    private String karteKana3;
    private String sex3;
    private String karteSex3;
    private String sampleGetDay3;
    private String register3;
    private String status3;

    public LabReceiverInfo(String lab3, String clientId3, String kana3, String karteKana3, String sex3, String karteSex3, String sampleGetDay3, String register3, String status3) {
        this.lab3 = lab3;
        this.clientId3 = clientId3;
        this.kana3 = kana3;
        this.karteKana3 = karteKana3;
        this.sex3 = sex3;
        this.karteSex3 = karteSex3;
        this.sampleGetDay3 = sampleGetDay3;
        this.register3 = register3;
        this.status3 = status3;
    }

    public String getLab3() {
        return lab3;
    }

    public void setLab3(String lab3) {
        this.lab3 = lab3;
    }

    public String getClientId3() {
        return clientId3;
    }

    public void setClientId3(String clientId3) {
        this.clientId3 = clientId3;
    }

    public String getKana3() {
        return kana3;
    }

    public void setKana3(String kana3) {
        this.kana3 = kana3;
    }

    public String getKarteKana3() {
        return karteKana3;
    }

    public void setKarteKana3(String karteKana3) {
        this.karteKana3 = karteKana3;
    }

    public String getSex3() {
        return sex3;
    }

    public void setSex3(String sex3) {
        this.sex3 = sex3;
    }

    public String getKarteSex3() {
        return karteSex3;
    }

    public void setKarteSex3(String karteSex3) {
        this.karteSex3 = karteSex3;
    }

    public String getSampleGetDay3() {
        return sampleGetDay3;
    }

    public void setSampleGetDay3(String sampleGetDay3) {
        this.sampleGetDay3 = sampleGetDay3;
    }

    public String getRegister3() {
        return register3;
    }

    public void setRegister3(String register3) {
        this.register3 = register3;
    }

    public String getStatus3() {
        return status3;
    }

    public void setStatus3(String status3) {
        this.status3 = status3;
    }

}
