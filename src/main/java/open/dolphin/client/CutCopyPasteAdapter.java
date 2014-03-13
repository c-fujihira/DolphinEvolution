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

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.JTextComponent;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public class CutCopyPasteAdapter extends MouseAdapter {

    private static final String CUT = "カット";
    private static final String COPY = "コピー";
    private static final String PASTE = "ペースト";

    private static CutCopyPasteAdapter instance = new CutCopyPasteAdapter();
    
    private CutCopyPasteAdapter() {
    }

    public static CutCopyPasteAdapter getInstance() {
        return instance;
    }

    private void mabeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {

            JTextComponent tc = (JTextComponent)e.getSource();

            JPopupMenu pop = new JPopupMenu();

            JMenuItem cutItem = new JMenuItem(new DefaultEditorKit.CutAction());
            cutItem.setText(CUT);
            cutItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            pop.add(cutItem);

            JMenuItem copyItem = new JMenuItem(new DefaultEditorKit.CopyAction());
            copyItem.setText(COPY);
            copyItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            pop.add(copyItem);

            JMenuItem pasteItem = new JMenuItem(new DefaultEditorKit.PasteAction());
            pasteItem.setText(PASTE);
            pasteItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
            pop.add(pasteItem);

            boolean hasSelection = tc.getSelectionStart()!=tc.getSelectionEnd() ? true : false;

            cutItem.setEnabled(tc.isEditable() && hasSelection);

            copyItem.setEnabled(hasSelection);

            Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            boolean canPaste = true;
            canPaste = canPaste && (t!=null);
            canPaste = canPaste && (t!=null && (t.isDataFlavorSupported(DataFlavor.stringFlavor) ||
                                                t.isDataFlavorSupported(OrderListTransferable.orderListFlavor)));
            pasteItem.setEnabled(canPaste);
            
            pop.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mabeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mabeShowPopup(e);
    }
}
