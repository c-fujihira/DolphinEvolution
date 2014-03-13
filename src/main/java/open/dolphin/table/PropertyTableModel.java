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
package open.dolphin.table;

import java.lang.reflect.Method;
import javax.swing.table.AbstractTableModel;

/**
 * PropertyTableModel
 *
 * @author Minagawa,Kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PropertyTableModel extends AbstractTableModel {

    private static final String LABEL_ITEM = "項目";
    private static final String LABEL_VALUE = "値";

    private String[] columnNames;
    private String[] attrNames;
    private String[] methodNames;
    private Object target;

    public PropertyTableModel(String[] columnNames, String[] attrNames, String[] methodNames) {
        super();
        this.columnNames = columnNames;
        this.attrNames = attrNames;
        this.methodNames = methodNames;
    }

    public PropertyTableModel(String[] attrNames, String[] methodNames) {
        this(new String[]{LABEL_ITEM, LABEL_VALUE}, attrNames, methodNames);
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return attrNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {

        if (col == 0) {
            return attrNames[row];
        }

        Object retObj = null;

        if (target != null && methodNames != null) {

            try {
                Method targetMethod = target.getClass().getMethod(methodNames[row], (Class[]) null);
                retObj = targetMethod.invoke(target, (Object[]) null);

            } catch (Exception e) {
            }
        }
        return retObj;
    }

    public void setObject(Object o) {
        this.target = o;
        this.fireTableDataChanged();
    }
}
