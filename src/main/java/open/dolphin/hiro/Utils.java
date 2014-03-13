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
package open.dolphin.hiro;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import open.dolphin.infomodel.IInfoModel;

/**
 * Utils
 *
 * @author Masato
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class Utils {

    /**
     * ボタンに設定されているActionCommand と パラメータvalue が合致すれば、選択状態にする。
     *
     * @param group ButtonGroup
     * @param value String
     */
    public static void setBtnValue(ButtonGroup group, String value) {
        for (Enumeration<AbstractButton> e = group.getElements(); e.hasMoreElements();) {
            AbstractButton btn = e.nextElement();
            if (btn.getActionCommand().equals(value)) {
                group.setSelected(btn.getModel(), true);
                break;
            }
        }
    }

    /**
     * 文字列を指定されている日付型に変換する。 変換できない、または変換エラーの場合 null を返す。
     *
     * @param target 文字列
     * @return 日付(型：yyyy-MM-dd)
     */
    public static Date chkDate(String target) {
        Date ret = null;
        try {
            SimpleDateFormat f = getDateFormat();
            f.setLenient(false); // 厳密にチェックする
            if ((target) != null && !"".equals(target)) {
                Date date = f.parse(target);
//                System.out.println("Parse String to Date : " + date);
                ret = date;
            }
        } catch (Exception e) {
//            System.err.println("Exception : input string = " + target);
            e.printStackTrace(System.err);
        }
        return ret;
    }

    /**
     * 日付フォーマット
     */
    //private static SimpleDateFormat dateFormat;
    /**
     * 日付フォーマットを生成し返す。
     *
     * @return SimpleDateFormat 日付フォーマット(yyyy-MM-dd)
     */
    public static SimpleDateFormat getDateFormat() {
//        try {
//            if (dateFormat == null) {
//                dateFormat = new SimpleDateFormat(IInfoModel.DATE_WITHOUT_TIME);
//                dateFormat.setLenient(false);
//            }
//        } catch (Exception e) {
//        }
//        return dateFormat;
        return new SimpleDateFormat(IInfoModel.DATE_WITHOUT_TIME);
    }
}
