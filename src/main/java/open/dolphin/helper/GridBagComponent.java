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
package open.dolphin.helper;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.List;

/**
 * GridBagComponent
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class GridBagComponent {

    private Component component;

    private int row;

    private int col;

    private int rowSpan = 1;

    private int colSpan = 1;

    private int anchor;

    private int fill = GridBagConstraints.NONE;

    private double rowWeight = 0.0;

    private double colWeight = 0.0;

    /**
     * Creates a new instance of GridBagComponent
     */
    public GridBagComponent() {
    }

    public static void setConstrain(Component comp, int row, int col, int rowSpan, int colSpan, int anchor, int fill, List<GridBagComponent> list) {
        GridBagComponent gbc = new GridBagComponent();
        gbc.setComponent(comp);
        gbc.setRow(row);
        gbc.setCol(col);
        gbc.setRowSpan(rowSpan);
        gbc.setColSpan(colSpan);
        gbc.setAnchor(anchor);
        gbc.setFill(fill);
        list.add(gbc);
    }

    public static void setConstrain(Component comp, int row, int col, int rowSpan, int colSpan, int anchor, int fill, double roww, double colw, List<GridBagComponent> list) {
        GridBagComponent gbc = new GridBagComponent();
        gbc.setComponent(comp);
        gbc.setRow(row);
        gbc.setCol(col);
        gbc.setRowSpan(rowSpan);
        gbc.setColSpan(colSpan);
        gbc.setAnchor(anchor);
        gbc.setFill(fill);
        gbc.setRowWeight(roww);
        gbc.setColWeight(colw);
        list.add(gbc);
    }

    public Component getComponent() {
        return component;
    }

    public void setComponent(Component component) {
        this.component = component;
    }

    public void setRowCol(int row, int col) {
        setRow(row);
        setCol(col);
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public int getAnchor() {
        return anchor;
    }

    public void setAnchor(int anchor) {
        this.anchor = anchor;
    }

    public int getFill() {
        return fill;
    }

    public void setFill(int fill) {
        this.fill = fill;
    }

    public double getRowWeight() {
        return rowWeight;
    }

    public void setRowWeight(double rowWeight) {
        this.rowWeight = rowWeight;
    }

    public double getColWeight() {
        return colWeight;
    }

    public void setColWeight(double colWeight) {
        this.colWeight = colWeight;
    }
}
