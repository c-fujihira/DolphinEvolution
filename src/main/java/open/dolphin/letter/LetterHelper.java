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
package open.dolphin.letter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import open.dolphin.infomodel.ModelUtils;

/**
 * LetterHelper
 *
 * @author Kazushi, Minagawa. Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class LetterHelper {

    protected static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    //protected static final SimpleDateFormat SDF = new SimpleDateFormat(SIMPLE_DATE_FORMAT);

    protected static void setModelValue(JTextField tf, String value) {
        if (value != null) {
            tf.setText(value);
        } //s.oh^ 2013/10/07 紹介状不具合
        else {
            tf.setText("");
        }
//s.oh$
    }

    protected static void setModelValue(JTextArea ta, String value) {
        if (value != null) {
            ta.setText(value);
        } //s.oh^ 2013/10/07 紹介状不具合
        else {
            ta.setText("");
        }
//s.oh$
    }

    protected static void setModelValue(JLabel lbl, String value) {
        if (value != null) {
            lbl.setText(value);
        }
    }

    protected static String getFieldValue(JTextField tf) {
        String ret = tf.getText().trim();
        if (!ret.equals("")) {
            return ret;
        }
        return null;
    }

    protected static String getAreaValue(JTextArea ta) {
        String ret = ta.getText().trim();
        if (!ret.equals("")) {
            return ret;
        }
        return null;
    }

    protected static String getLabelValue(JLabel lbl) {
        String ret = lbl.getText().trim();
        if (!ret.equals("")) {
            return ret;
        }
        return null;
    }

    protected static String getDateAsString(Date date, String patterm) {
        SimpleDateFormat sdf = new SimpleDateFormat(patterm);
        return sdf.format(date);
    }

    protected static String getDateAsString(Date date) {
        return getDateAsString(date, "yyyy年M月d日");
    }

    protected static Date getSimpleDateFromString(String dateStr) {
        try {
            SimpleDateFormat SDF = new SimpleDateFormat(SIMPLE_DATE_FORMAT);
            Date ret = SDF.parse(dateStr);
            return ret;
        } catch (ParseException ex) {
            Logger.getLogger(LetterHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    protected static String getDateString(String mmlDate) {
        Date d = ModelUtils.getDateAsObject(mmlDate);
        return getDateAsString(d);
    }

    protected static String getBirdayWithAge(String birthday, String age) {
        StringBuilder sb = new StringBuilder();
        sb.append(birthday);
        sb.append(" (");
        sb.append(age);
        sb.append(" 歳)");
        return sb.toString();
    }

    protected static String getAddressWithZipCode(String address, String zip) {
        StringBuilder sb = new StringBuilder();
        sb.append(zip);
        sb.append(" ");
        sb.append(address);
        return sb.toString();
    }
}
