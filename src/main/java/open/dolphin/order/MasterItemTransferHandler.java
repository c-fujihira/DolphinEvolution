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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import open.dolphin.table.ListTableModel;

/**
 * MasterItemTransferHandler
 *
 * @author Minagawa,Kazushi. Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class MasterItemTransferHandler extends TransferHandler {

    private final DataFlavor masterItemFlavor = MasterItemTransferable.masterItemFlavor;

    private JTable sourceTable;
    private MasterItem dragItem;
    private boolean shouldRemove;

    @Override
    protected Transferable createTransferable(JComponent c) {
        sourceTable = (JTable) c;
        ListTableModel<MasterItem> tableModel = (ListTableModel<MasterItem>) sourceTable.getModel();
        int fromIndex = sourceTable.getSelectedRow();
        dragItem = tableModel.getObject(fromIndex);
        return dragItem != null ? new MasterItemTransferable(dragItem) : null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {

        if (!canImport(support)) {
            return false;
        }

        try {
            JTable.DropLocation dl = (JTable.DropLocation) support.getDropLocation();
            int toIndex = dl.getRow();
            if (dl.isInsertRow() && toIndex > -1) {
                Transferable t = support.getTransferable();
                MasterItem dropItem = (MasterItem) t.getTransferData(masterItemFlavor);
                JTable dropTable = (JTable) support.getComponent();
                ListTableModel<MasterItem> tableModel = (ListTableModel<MasterItem>) dropTable.getModel();
                shouldRemove = dropTable == sourceTable;

                if (toIndex < tableModel.getObjectCount()) {
                    tableModel.addObject(toIndex, dropItem);
                } else {
                    tableModel.addObject(dropItem);
                }

                return true;
            }
        } catch (UnsupportedFlavorException | IOException ioe) {
            ioe.printStackTrace(System.err);
        }

        return false;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        if (action == MOVE && shouldRemove && (dragItem != null)) {
            ListTableModel<MasterItem> tableModel = (ListTableModel<MasterItem>) sourceTable.getModel();
            tableModel.delete(dragItem);
        }
        shouldRemove = false;
        dragItem = null;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        return (support.isDrop() && support.isDataFlavorSupported(masterItemFlavor));
    }
}
