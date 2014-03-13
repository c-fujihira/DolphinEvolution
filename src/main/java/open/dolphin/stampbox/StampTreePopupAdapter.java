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
package open.dolphin.stampbox;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import open.dolphin.client.LocalStampTreeNodeTransferable;
import open.dolphin.client.OrderList;
import open.dolphin.client.OrderListTransferable;
import open.dolphin.client.ReflectAction;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.InfoModelTransferable;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.ModuleModel;

/**
 * StampTreePopupAdapter
 *
 * @author Kazushi Minagawa
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class StampTreePopupAdapter extends MouseAdapter {

    private static final String[] POP_MENUS = {"新規フォルダ", "名称変更", "-", "削 除"};
    private static final String[] POP_METHODS = {"createNewFolder", "renameNode", "-", "deleteNode"};

    public StampTreePopupAdapter() {
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        maybePopup(evt);
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
        maybePopup(evt);
    }

    private void maybePopup(MouseEvent evt) {

        if (evt.isPopupTrigger()) {

            // イベントソースの StampTree を取得する
            StampTree tree = (StampTree) evt.getSource();
            int x = evt.getX();
            int y = evt.getY();

            // クリック位置へのパスを得る
            TreePath destPath = tree.getPathForLocation(x, y);
            if (destPath == null) {
                return;
            }

            // クリック位置の Node を得る
            StampTreeNode node = (StampTreeNode) destPath.getLastPathComponent();

            // Copy
            boolean canCopy = true;

            // エディタから発行...はコピーできない
            if (node.isLeaf()) {
                // Leaf なので StampInfo 　を得る
                ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();

                // Editable
                if (!info.isEditable()) {
                    //Toolkit.getDefaultToolkit().beep();
                    //return;
                    canCopy = false;
                }
            }

            // Paste は厄介
            boolean canPaste = canPaste(tree.getEntity());

            // Popupする
            JPopupMenu popup = createPopuoMenu(tree, canCopy, canPaste);
            popup.show(evt.getComponent(), x, y);
        }
    }

    private JPopupMenu createPopuoMenu(final JTree tree, boolean canCopy, boolean canPaste) {

        AbstractAction copy = new AbstractAction("コピー") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Action a = tree.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
                if (a != null) {
                    a.actionPerformed(new ActionEvent(tree,
                            ActionEvent.ACTION_PERFORMED,
                            null));
                }
            }
        };
        copy.setEnabled(canCopy);

        AbstractAction paste = new AbstractAction("ペースト") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                Action a = tree.getActionMap().get(TransferHandler.getPasteAction().getValue(Action.NAME));
                if (a != null) {
                    a.actionPerformed(new ActionEvent(tree,
                            ActionEvent.ACTION_PERFORMED,
                            null));
                }
            }
        };
        paste.setEnabled(canPaste);

        JPopupMenu popMenu = new JPopupMenu();
        popMenu.add(new JMenuItem(copy));
        popMenu.add(new JMenuItem(paste));
        popMenu.addSeparator();

        for (int i = 0; i < POP_MENUS.length; i++) {

            String name = POP_MENUS[i];
            String method = POP_METHODS[i];

            if (name.equals("-")) {
                popMenu.addSeparator();
            } else {
                ReflectAction action = new ReflectAction(name, (Object) tree, method);
                JMenuItem item = new JMenuItem(action);
                popMenu.add(item);
            }
        }
        return popMenu;
    }

    /**
     * クリップボードのコンテントがPaste可能かどうかを返す。
     *
     * @param targetEntity ペースト先のentity
     * @return 可能な時 true
     */
    private boolean canPaste(String targetEntity) {

        // Clipboard内のTransferable
        Transferable tr = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
        if (tr == null) {
            return false;
        }

        // カルテペインからのペースト
        if (tr.isDataFlavorSupported(OrderListTransferable.orderListFlavor)) {
            return canPasteOrder(tr, targetEntity);
        }

        // Textペースト
        if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)
                && (targetEntity.equals(IInfoModel.ENTITY_TEXT) || targetEntity.equals(IInfoModel.ENTITY_PATH))) {
            return true;
        }

        // 病名ペースト
        if (tr.isDataFlavorSupported(InfoModelTransferable.infoModelFlavor)) {
            boolean pasteOk = (targetEntity.equals(IInfoModel.ENTITY_DIAGNOSIS));
            pasteOk = pasteOk || (targetEntity.equals(IInfoModel.ENTITY_PATH));
            return pasteOk;
        }

        // StampTreeNodeペースト
        if (tr.isDataFlavorSupported(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor)) {
            return canPasteTreeNode(tr, targetEntity);
        }

        return false;
    }

    /**
     * オーダーがペースト可能かどうかを返す。
     *
     * @param tr オーダーを保持しているTransferable
     * @param targetEntity ペースト先のentity
     * @return 可能な時 true
     */
    private boolean canPasteOrder(Transferable tr, String targetEntity) {
        try {
            OrderList list = (OrderList) tr.getTransferData(OrderListTransferable.orderListFlavor);
            ModuleModel pasteStamp = list.orderList[0];   // ToDo multiple drag & drop
            String pasteEntity = pasteStamp.getModuleInfoBean().getEntity(); // testStamp
            // 同一entity
            boolean match = pasteEntity.equals(targetEntity);

            // 受けてがパスの場合
            match = match || (targetEntity.equals(IInfoModel.ENTITY_PATH));

            return match;

        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace(System.err);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }

        return false;
    }

    /**
     * StampTreeNodeがペースト可能かどうかを返す。
     *
     * @param tr StampTreeNodeを保持しているTransferable
     * @param targetEntity ペースト先のentity
     * @return 可能な時 true
     */
    private boolean canPasteTreeNode(Transferable tr, String targetEntity) {
        try {
            StampTreeNode test = (StampTreeNode) tr.getTransferData(LocalStampTreeNodeTransferable.localStampTreeNodeFlavor);

            // 葉以外はfalse
            if (!test.isLeaf()) {
                return false;
            }
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) test;
            StampTreeNode root = (StampTreeNode) node.getRoot();

            Object o = root.getUserObject();

            if (o != null && o instanceof TreeInfo) {
                TreeInfo info = (TreeInfo) o;
                return entityMatch(info.getEntity(), targetEntity);
            } else {
                return false;
            }
        } catch (UnsupportedFlavorException ex) {
            ex.printStackTrace(System.err);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        return false;
    }

    /**
     * Entity間のマッチングを返す。
     *
     * @param pasteEntity ペーストするentity
     * @param targetEntity ペースト先のentity
     * @return ペースト可能な時 true
     */
    private boolean entityMatch(String pasteEntity, String targetEntity) {

        // 同一entity
        boolean match = pasteEntity.equals(targetEntity);

        // 受けてがパスの場合
        match = match || (targetEntity.equals(IInfoModel.ENTITY_PATH));

        // 検体検査 -> （生体検査 | 細菌検査）
        match = match || (pasteEntity.equals(IInfoModel.ENTITY_LABO_TEST)
                && (targetEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER) || targetEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER)));

        // 生体検査 -> （検体検査 | 細菌検査）
        match = match || (pasteEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER)
                && (targetEntity.equals(IInfoModel.ENTITY_LABO_TEST) || targetEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER)));

        // 細菌検査 -> （検体検査 | 生体検査）
        match = match || (pasteEntity.equals(IInfoModel.ENTITY_BACTERIA_ORDER)
                && (targetEntity.equals(IInfoModel.ENTITY_LABO_TEST) || targetEntity.equals(IInfoModel.ENTITY_PHYSIOLOGY_ORDER)));

        return match;
    }
}
