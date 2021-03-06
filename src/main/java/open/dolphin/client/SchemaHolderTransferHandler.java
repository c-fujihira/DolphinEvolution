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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import open.dolphin.infomodel.SchemaModel;


/**
 * SchemaHolderTransferHandler
 * 
 * @author Kazushi Minagawa
 *
 */
public class SchemaHolderTransferHandler extends TransferHandler implements IKarteTransferHandler {

    private KartePane soaPane;
    private SchemaHolder schemaHolder;

    public SchemaHolderTransferHandler(KartePane soaPane, SchemaHolder sh) {
        this.soaPane = soaPane;
        this.schemaHolder = sh;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        SchemaHolder source = (SchemaHolder) c;
        KartePane context = source.getKartePane();
        context.setDrragedStamp(new ComponentHolder[]{source});
        context.setDraggedCount(1);
        SchemaModel schema = source.getSchema();
        SchemaList list = new SchemaList();
        list.schemaList = new SchemaModel[]{schema};
        Transferable tr = new SchemaListTransferable(list);
        return tr;
    }

    @Override
	public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        SchemaHolder test = (SchemaHolder) c;
        KartePane context = test.getKartePane();
        if (action == MOVE &&
                context.getDrragedStamp() != null &&
                context.getDraggedCount() == context.getDroppedCount()) {
            context.removeSchema(test); // TODO 
        }
        context.setDrragedStamp(null);
        context.setDraggedCount(0);
        context.setDroppedCount(0);
    }

    @Override
//minagawa^ Paste problem 2013/04/14 不具合修正(スタンプが消える)
//    public boolean canImport(JComponent c, DataFlavor[] flavors) {
//        return false;
//    }
    public boolean canImport(TransferHandler.TransferSupport support) {
        return false;
    }

    /**
     * スタンプをクリップボードへ転送する。
     */
    @Override
    public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
        SchemaHolder sh = (SchemaHolder) comp;
        Transferable tr = createTransferable(comp);
        clip.setContents(tr, null);
        if (action == MOVE) {
            KartePane kartePane = sh.getKartePane();
            if (kartePane.getTextPane().isEditable()) {
                kartePane.removeSchema(sh);
            }
        }
    }

    @Override
    public JComponent getComponent() {
        return schemaHolder;
    }

    @Override
    public void enter(ActionMap map) {
        schemaHolder.setSelected(true);
        map.get(GUIConst.ACTION_COPY).setEnabled(true);
        boolean caCunt = (soaPane.getTextPane().isEditable());
        map.get(GUIConst.ACTION_CUT).setEnabled(caCunt);
        map.get(GUIConst.ACTION_PASTE).setEnabled(false);
    }

    @Override
    public void exit(ActionMap map) {
        schemaHolder.setSelected(false);
    }
}
