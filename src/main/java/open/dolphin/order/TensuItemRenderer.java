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
package open.dolphin.order;

import java.awt.Color;
import java.awt.Component;
import java.util.regex.Pattern;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import open.dolphin.infomodel.ClaimConst;
import open.dolphin.infomodel.TensuMaster;
import open.dolphin.table.ListTableModel;

/**
 * TensuItemRenderer
 *
 * @author Kazushi Minagawa.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class TensuItemRenderer extends JLabel implements TableCellRenderer {

    private static final Color THEC_COLOR = new Color(204, 255, 102);
    private static final Color MEDICINE_COLOR = new Color(255, 204, 0);
    private static final Color MATERIAL_COLOR = new Color(153, 204, 255);
    private static final Color OTHER_COLOR = new Color(255, 255, 255);

    private Pattern passPattern;
    private Pattern shinkuPattern;

    public TensuItemRenderer(Pattern passPattern, Pattern shinkuPattern) {
        setOpaque(true);
        this.passPattern = passPattern;
        this.shinkuPattern = shinkuPattern;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (isSelected) {
            setBackground(table.getSelectionBackground());
            setForeground(table.getSelectionForeground());

        } else {

            setForeground(table.getForeground());

            ListTableModel<TensuMaster> tm = (ListTableModel<TensuMaster>) table.getModel();
            TensuMaster item = tm.getObject(row);

            if (item != null) {

                String slot = item.getSlot();

                if (passPattern != null && passPattern.matcher(slot).find()) {

                    String srycd = item.getSrycd();

                    if (srycd.startsWith(ClaimConst.SYUGI_CODE_START)
                            && shinkuPattern != null
                            && shinkuPattern.matcher(item.getSrysyukbn()).find()) {
                        setBackground(THEC_COLOR);

                    } else if (srycd.startsWith(ClaimConst.YAKUZAI_CODE_START)) {
                        //内用1、外用6、注射薬4
                        String ykzkbn = item.getYkzkbn();

                        if (ykzkbn.equals(ClaimConst.YKZ_KBN_NAIYO)) {
                            setBackground(MEDICINE_COLOR);

                        } else if (ykzkbn.equals(ClaimConst.YKZ_KBN_INJECTION)) {
                            setBackground(MEDICINE_COLOR);

                        } else if (ykzkbn.equals(ClaimConst.YKZ_KBN_GAIYO)) {
                            setBackground(MEDICINE_COLOR);

                        } else {
                            setBackground(OTHER_COLOR);
                        }

                    } else if (srycd.startsWith(ClaimConst.ZAIRYO_CODE_START)) {
                        setBackground(MATERIAL_COLOR);

                    } else if (srycd.startsWith(ClaimConst.ADMIN_CODE_START)) {
                        setBackground(OTHER_COLOR);

                    } else if (srycd.startsWith(ClaimConst.RBUI_CODE_START)) {
                        setBackground(THEC_COLOR);

                    } else {
                        setBackground(OTHER_COLOR);
                    }

                } else {
                    setBackground(OTHER_COLOR);
                }

            } else {
                setBackground(OTHER_COLOR);
            }
        }

        //-------------------------------------------------------
        if (value != null) {

            if (value instanceof java.lang.String) {
                this.setText((String) value);

            } else {
                this.setText(value.toString());
            }

        } else {
            this.setText("");
        }
        //-------------------------------------------------------

        return this;
    }
}
