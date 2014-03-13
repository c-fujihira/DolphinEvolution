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
package open.dolphin.client;

import java.util.GregorianCalendar;
import open.dolphin.infomodel.AppointmentModel;

/**
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class MedicalEvent {
    
    private int year;
    private int month;
    private int day;
    private int dayOfWeek;
    private String displayDate;
    private boolean today;
    private boolean outOfMonth;
    private String medicalCode;
    private AppointmentModel appo;
    
    /** Creates a new instance of MedicalEventEntry */
    public MedicalEvent() {
    }
    
    public MedicalEvent(int year, int month, int day, int dayOfWeek) {
        this();    
        this.year = year;
        this.month = month;
        this.day = day;
        this.dayOfWeek = dayOfWeek;
        setDisplayDate(); 
    }
    
    public int getYear() {
        return year;
    }
    
    public int getMonth() {
        return month;
    }
    
    public int getDay() {
        return day;
    }
    
    public int getDayOfWeek() {
        return dayOfWeek;
    }
    
    public boolean isOutOfMonth() {
        return outOfMonth;
    }
    
    public void setOutOfMonth(boolean b) {
        outOfMonth = b;
    }
    
    public boolean isToday() {
        return today;
    }
    
    public void setToday(boolean b) {
        today = b;
    }
    
    public String getMedicalCode() {
        return medicalCode;
    }
    
    public void setMedicalCode(String val) {
        medicalCode = val;
    }
    
    public boolean before(GregorianCalendar gc) {
        GregorianCalendar me = new GregorianCalendar(year, month, day);
        return me.before(gc);
    }
    
    public AppointmentModel getAppointEntry() {
        return appo;
    }
    
    public void setAppointEntry(AppointmentModel val) {
        appo = val;
    }
    
    public String getAppointmentName() {
        return appo == null ? null : appo.getName();
    }
    
    @Override
    public String toString() {
        return String.valueOf(day);
    }
    
    public String getDisplayDate() {
        return displayDate;
    }
    
    private void setDisplayDate() {
        StringBuilder buf = new StringBuilder();
        String val = String.valueOf(year);
        buf.append(val);
        buf.append("-");
        val = String.valueOf(month+1);
        if (val.length() == 1) {
            buf.append("0");
        }
        buf.append(val);
        buf.append("-");
        val = String.valueOf(day);
        if (val.length() == 1) {
            buf.append("0");
        }
        buf.append(val);
        
        displayDate =  buf.toString();
    }    
}