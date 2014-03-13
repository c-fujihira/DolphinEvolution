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
package open.dolphin.impl.labrcv;

import open.dolphin.infomodel.NLaboModule;
import open.dolphin.infomodel.PatientModel;

/**
 * NLaboImportSummary
 *
 * @author kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class NLaboImportSummary {

    private String laboCode;

    private String patientId;

    private String patientName;

    private String patientSex;

    private String sampleDate;

    private String numOfTestItems;

    private String status;

    private String result;

    private NLaboModule module;

    private PatientModel patient;

    private String karteId;
    private String karteName;
    private String karteKanaName;
    private String karteSex;
    private String karteBirthday;

    private boolean opened;

    /**
     * @return the laboCode
     */
    public String getLaboCode() {
        return laboCode;
    }

    /**
     * @param laboCode the laboCode to set
     */
    public void setLaboCode(String laboCode) {
        this.laboCode = laboCode;
    }

    /**
     * @return the patientId
     */
    public String getPatientId() {
        return patientId;
    }

    /**
     * @param patientId the patientId to set
     */
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    /**
     * @return the patientName
     */
    public String getPatientName() {
        return patientName;
    }

    /**
     * @param patientName the patientName to set
     */
    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    /**
     * @return the patientSex
     */
    public String getPatientSex() {
        return patientSex;
    }

    /**
     * @param patientSex the patientSex to set
     */
    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    /**
     * @return the sampleDate
     */
    public String getSampleDate() {
        return sampleDate;
    }

    /**
     * @param sampleDate the sampleDate to set
     */
    public void setSampleDate(String sampleDate) {
        this.sampleDate = sampleDate;
    }

    /**
     * @return the numOfTestItems
     */
    public String getNumOfTestItems() {
        return numOfTestItems;
    }

    /**
     * @param numOfTestItems the numOfTestItems to set
     */
    public void setNumOfTestItems(String numOfTestItems) {
        this.numOfTestItems = numOfTestItems;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return the patient
     */
    public PatientModel getPatient() {
        return patient;
    }

    /**
     * @param patient the patient to set
     */
    public void setPatient(PatientModel patient) {
        this.patient = patient;
    }

    /**
     * @return the module
     */
    public NLaboModule getModule() {
        return module;
    }

    /**
     * @param module the module to set
     */
    public void setModule(NLaboModule module) {
        this.module = module;
    }

    /**
     * @return the karteName
     */
    public String getKarteName() {
        return karteName;
    }

    /**
     * @param karteName the karteName to set
     */
    public void setKarteName(String karteName) {
        this.karteName = karteName;
    }

    /**
     * @return the karteKanaName
     */
    public String getKarteKanaName() {
        return karteKanaName;
    }

    /**
     * @param karteKanaName the karteKanaName to set
     */
    public void setKarteKanaName(String karteKanaName) {
        this.karteKanaName = karteKanaName;
    }

    /**
     * @return the karteSex
     */
    public String getKarteSex() {
        return karteSex;
    }

    /**
     * @param karteSex the karteSex to set
     */
    public void setKarteSex(String karteSex) {
        this.karteSex = karteSex;
    }

    /**
     * @return the karteBirthday
     */
    public String getKarteBirthday() {
        return karteBirthday;
    }

    /**
     * @param karteBirthday the karteBirthday to set
     */
    public void setKarteBirthday(String karteBirthday) {
        this.karteBirthday = karteBirthday;
    }

    /**
     * @return the karteId
     */
    public String getKarteId() {
        return karteId;
    }

    /**
     * @param karteId the karteId to set
     */
    public void setKarteId(String karteId) {
        this.karteId = karteId;
    }

    /**
     * @return the opened
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * @param opened the opened to set
     */
    public void setOpened(boolean opened) {
        this.opened = opened;
    }
}
