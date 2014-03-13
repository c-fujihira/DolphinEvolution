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
package open.dolphin.impl.lbtest;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.LabTestRowObject;
import open.dolphin.infomodel.LabTestValueObject;
import open.dolphin.table.StripeTableCellRenderer;

/**
 * LabTestRenderer
 *
 * @author Kazushi Minagawa, Digital Globe, Inc
 * @author modified by masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class LabTestRenderer extends StripeTableCellRenderer {

    private Color penCol;

    private static final Color specimenColor = ClientContext.getColor("labotest.color.specimen");

    public LabTestRenderer() {
        setOpaque(true);
        setBackground(Color.white);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table,
            Object value, boolean isSelected, boolean hasFocus, int row, int column) {

        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        //-------------------------------------------------------
        if (value != null) {

            LabTestRowObject rowObj = (LabTestRowObject) value;

////masuda^   検体名の場合
//            String specimenName = rowObj.getSpecimenName();
//            if (specimenName != null) {
//                setText(specimenName);
//                setBackground(specimenColor);
//                setForeground(Color.BLACK);
//                return this;
//            }
////masuda$
//            
            if (column == 0) {

                // テスト項目名(単位）を表示する
                penCol = Color.black;
//masuda^   項目名は選択すると白抜きにする
                if (!isSelected) {
                    setForeground(penCol);
                }
//masuda$
                setText(rowObj.nameWithUnit());
                String toolTip = rowObj.getNormalValue() != null ? rowObj.getNormalValue() : "";
                setToolTipText(toolTip);

            } else {

                // column-1番目の値オブジェクトwp取り出す
                LabTestValueObject valueObj = rowObj.getLabTestValueObjectAt(column - 1);

                String text = valueObj != null ? valueObj.getValue() : "";
                String flag = valueObj != null ? valueObj.getOut() : null;
                String toolTip = valueObj != null ? valueObj.concatComment() : "";

                if (flag != null && flag.startsWith("H")) {
                    penCol = Color.RED;
                } else if (flag != null && flag.startsWith("L")) {
                    penCol = Color.BLUE;
                } else if (toolTip != null && (!toolTip.equals(""))) {
                    penCol = Color.MAGENTA;
                } else {
                    penCol = Color.black;
                }

                setForeground(penCol);
                setText(text);
                setToolTipText(toolTip);
            }

        } else {
            penCol = Color.black;
            setForeground(penCol);
            setText("");
            setToolTipText("");
        }
        //-------------------------------------------------------

        return this;
    }
}
