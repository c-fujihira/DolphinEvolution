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

import java.awt.Color;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledEditorKit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import open.dolphin.helper.MenuSupport;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.project.Project;
import open.dolphin.stampbox.StampBoxPlugin;
import open.dolphin.stampbox.StampTree;
import open.dolphin.stampbox.StmapTreeMenuBuilder;
import open.dolphin.stampbox.StmapTreePopupBuilder;

/**
 * Mediator class to control Karte Window Menu.
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ChartMediator extends MenuSupport implements UndoableEditListener, ActionListener {
    
    public enum CompState{NONE, SOA, SOA_TEXT, SCHEMA, P, P_TEXT, STAMP};
    
    private static final int[] FONT_SIZE = {8, 10, 12, 18, 24, 36, 48};

    private static final String PERMANENT_FOCUS_OWNER = "permanentFocusOwner";

    private int curSize;    // FONT_SIZEのindex

    // ChartPlugin
    private final Chart chart;

    // Current KarteTransferHandler
    private IKarteTransferHandler curKarteTransherHandler;

    // CurrentComponent
    private JComponent currentFocusOwner;

    // Undo Manager
    private final UndoManager undoManager;
    private Action undoAction;
    private Action redoAction;
    private final FocusPropertyChangeListener fpcl;

    private static int defaultFontSizeIndex() {

        int size = Project.getInt("karte.font.size.default");
        for (int i = 0; i < FONT_SIZE.length; i++) {
            if (FONT_SIZE[i] == size) {
                return i;
            }
        }
        return 1;
    }

    public ChartMediator(Object owner) {

        super(owner);
        chart = (Chart) owner;

        fpcl = new FocusPropertyChangeListener();
        KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addPropertyChangeListener(fpcl);

        undoManager = new UndoManager();

        // fontsize
        curSize = defaultFontSizeIndex();
    }

    class FocusPropertyChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent e) {
            String prop = e.getPropertyName();
            if (prop.equals(PERMANENT_FOCUS_OWNER)) {
                Component comp = (Component) e.getNewValue();
                if (comp != null && (comp instanceof JComponent)) {
                    JComponent jc = (JComponent) comp;
                    Object obj = jc.getClientProperty("karteCompositor");
                    currentFocusOwner = obj != null ? jc : currentFocusOwner;
                    TransferHandler tr = jc.getTransferHandler();
                    if (tr != null && (tr instanceof IKarteTransferHandler)) {
                        setCurKarteTransferHandler((IKarteTransferHandler) tr);
                    }
                }
            }
        }
    }

    public void setCurKarteTransferHandler(IKarteTransferHandler newTrans) {
        //System.err.println("setCurKarteTransferHandler did enter");
        IKarteTransferHandler old = this.curKarteTransherHandler;
        this.curKarteTransherHandler = newTrans;
        if (curKarteTransherHandler == old) {
            return;
        }
        if (old != null) {
            old.exit(getActions());
        }
        enabledAction(GUIConst.ACTION_CUT, false);
        enabledAction(GUIConst.ACTION_COPY, false);
        enabledAction(GUIConst.ACTION_PASTE, false);
        enabledAction(GUIConst.ACTION_UNDO, false);
        enabledAction(GUIConst.ACTION_REDO, false);
        enabledAction(GUIConst.ACTION_INSERT_TEXT, false);
        enabledAction(GUIConst.ACTION_INSERT_SCHEMA, false);
        enabledAction(GUIConst.ACTION_ATTACHMENT, false);
        enabledAction(GUIConst.ACTION_INSERT_STAMP, false);
        if (curKarteTransherHandler != null) {
            curKarteTransherHandler.enter(getActions());
        }
    }

    @Override
    public void registerActions(ActionMap map) {

        super.registerActions(map);

        undoAction = map.get(GUIConst.ACTION_UNDO);
        redoAction = map.get(GUIConst.ACTION_REDO);

        // 昇順降順を Preference から取得し設定しておく
        boolean asc = Project.getBoolean(Project.DOC_HISTORY_ASCENDING, false);
        if (asc) {
            Action a = map.get(GUIConst.ACTION_ASCENDING);
            JRadioButtonMenuItem rdi = (JRadioButtonMenuItem) a.getValue("menuItem");
            rdi.setSelected(true);
        } else {
            Action desc = map.get(GUIConst.ACTION_DESCENDING);
            JRadioButtonMenuItem rdi = (JRadioButtonMenuItem) desc.getValue("menuItem");
            rdi.setSelected(true);
        }
//        //
//        boolean showModified = Project.getBoolean(Project.DOC_HISTORY_SHOWMODIFIED);
//        Action a = map.get(GUIConst.ACTION_SHOW_MODIFIED);
//        JCheckBoxMenuItem chk = (JCheckBoxMenuItem)a.getValue("menuItem");
//        chk.setSelected(showModified);
    }

    public void dispose() {
        KeyboardFocusManager focusManager
                = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.removePropertyChangeListener(fpcl);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    public JComponent getCurrentComponent() {
        //return (curKarteTransherHandler!=null) ? curKarteTransherHandler.getComponent() : null;
        return currentFocusOwner;
    }

    public JComponent getCurrentFocusOwner() {
        return currentFocusOwner;
    }

    /**
     * メニューリスナの実装。 挿入及びテキストメニューが選択された時の処理を行う
     *
     * @param e
     */
    @Override
    public void menuSelected(MenuEvent e) {

        // 挿入とテキストメニューにリスナが登録されている
        JMenu selectedMenu = (JMenu) e.getSource();
        String cmd = selectedMenu.getActionCommand();
        //-----------------------------------
        // 挿入メニューの時
        // StampBox のツリーをメニューにする
        //-----------------------------------
        switch (cmd) {
            case GUIConst.MENU_INSERT:
                selectedMenu.removeAll();
                // 添付メニューを追加する
                selectedMenu.add(createAttachmentMenu());
                selectedMenu.addSeparator();
                // StampBox の全ツリーを取得する
                List<StampTree> trees = getStampBox().getAllTrees();
                if(trees == null){
                    return;
                }
                // ツリーをイテレートする
                for (StampTree tree : trees) {

                    // ツリーのエンティティを取得する
                    String entity = tree.getEntity();
                    switch (entity) {
                        case IInfoModel.ENTITY_DIAGNOSIS:
                            // 傷病名の時、傷病名メニューを構築し追加する
                            selectedMenu.add(createDiagnosisMenu(tree));
                            selectedMenu.addSeparator();
                            break;
                        case IInfoModel.ENTITY_TEXT:
                            // テキストの時、テキストメニューを構築し追加する
                            selectedMenu.add(createTextMenu(tree));
                            selectedMenu.addSeparator();
                            break;
                        default:
                            // 通常のPオーダの時
                            selectedMenu.add(createStampMenu(tree));
                            break;
                    }
                }

                // 
                break;
            case GUIConst.MENU_TEXT:
                //-----------------------------------
                // テキストメニューの場合、スタイルを制御する
                //-----------------------------------
                adjustStyleMenu();
                break;
        }
    }

    @Override
    public void menuDeselected(MenuEvent e) {
    }

    @Override
    public void menuCanceled(MenuEvent e) {
    }

    private JMenuItem createAttachmentMenu() {

        boolean enabled = false;
        KartePane kartePane;
        if (getChain() instanceof KarteEditor) {
            KarteEditor editor = (KarteEditor) getChain();
            kartePane = editor.getSOAPane();
            enabled = (kartePane.getTextPane().isEditable());
        }

        getAction(GUIConst.ACTION_ATTACHMENT).setEnabled(enabled);
        return new JMenuItem(getAction(GUIConst.ACTION_ATTACHMENT));
    }

    /**
     * フォーマット関連メニューを調整する。
     *
     * @param kartePane
     */
    private void adjustStyleMenu() {

        boolean enabled = false;
        KartePane kartePane;
        if (getChain() instanceof KarteEditor) {
            KarteEditor editor = (KarteEditor) getChain();
            kartePane = editor.getSOAPane();
            enabled = (kartePane.getTextPane().isEditable());
        }

        // サブメニューを制御する
        getAction("size").setEnabled(enabled);
        getAction("style").setEnabled(enabled);
        getAction("justify").setEnabled(enabled);
        getAction("color").setEnabled(enabled);

        // メニューアイテムを制御する
        //getAction(GUIConst.ACTION_RESET_STYLE).setEnabled(enabled);
        getAction("fontRed").setEnabled(enabled);
        getAction("fontOrange").setEnabled(enabled);
        getAction("fontYellow").setEnabled(enabled);
        getAction("fontGreen").setEnabled(enabled);
        getAction("fontBlue").setEnabled(enabled);
        getAction("fontPurple").setEnabled(enabled);
        getAction("fontGray").setEnabled(enabled);

        getAction("fontLarger").setEnabled(enabled);
        getAction("fontSmaller").setEnabled(enabled);
        getAction("fontStandard").setEnabled(enabled);

        getAction("fontBold").setEnabled(enabled);
        getAction("fontItalic").setEnabled(enabled);
        getAction("fontUnderline").setEnabled(enabled);

        getAction("leftJustify").setEnabled(enabled);
        getAction("centerJustify").setEnabled(enabled);
        getAction("rightJustify").setEnabled(enabled);
    }

    /**
     * スタンプTreeから傷病名メニューを構築する。
     *
     * @param insertMenu テキストメニュー
     */
    private JMenu createDiagnosisMenu(StampTree stampTree) {

        //
        // chainの先頭がDiagnosisDocumentの時のみ使用可能とする
        //
        JMenu myMenu;
        DiagnosisDocument diagnosis = null;
        boolean enabled = false;
        Object obj = getChain();
        if (obj instanceof DiagnosisDocument) {
            diagnosis = (DiagnosisDocument) obj;
            enabled = true;
        }

        if (!enabled) {
            // cjainの先頭がDiagnosisでない場合はめにゅーをdisableにする
            myMenu = new JMenu(stampTree.getTreeName());
            myMenu.setEnabled(false);

        } else {
            // 傷病名Tree、テーブル、ハンドラからメニューを構築する
            JComponent comp = diagnosis.getDiagnosisTable();
            TransferHandler handler = comp.getTransferHandler();
            StmapTreeMenuBuilder builder = new StmapTreeMenuBuilder();
            myMenu = builder.build(stampTree, comp, handler);
        }
        return myMenu;
    }

    /**
     * スタンプTreeからテキストメニューを構築する。
     *
     * @param insertMenu テキストメニュー
     */
    private JMenu createTextMenu(StampTree stampTree) {

        // chain の先頭が KarteEditor でかつ SOAane が編集可の場合のみメニューが使える
        JMenu myMenu;
        boolean enabled = false;
        KartePane kartePane = null;
        Object obj = getChain();
        if (obj instanceof KarteEditor) {
            KarteEditor editor = (KarteEditor) obj;
            kartePane = editor.getSOAPane();
            if (kartePane != null) {
                enabled = (kartePane.getTextPane().isEditable());
            }
        }

        if (!enabled) {
            myMenu = new JMenu(stampTree.getTreeName());
            myMenu.setEnabled(false);

        } else {
            //
            // TextTree、JTextPane、handler からメニューを構築する
            // PPane にも落とさなければならない TODO
            //JComponent comp = kartePane.getTextPane();
            // TransferHandler handler = comp.getTransferHandler();

            // 2007-03-31
            // 直近でフォーカスを得ているコンポーネント(JTextPan）へ挿入する
            //
            JComponent comp = getCurrentComponent();
            if (comp == null) {
                comp = kartePane.getTextPane();
            }
            TransferHandler handler = comp.getTransferHandler();
            StmapTreeMenuBuilder builder = new StmapTreeMenuBuilder();
            myMenu = builder.build(stampTree, comp, handler);
        }

        return myMenu;
    }

    /**
     * スタンプメニューを構築する。
     *
     * @param insertMenu スタンプメニュー
     */
    private JMenu createStampMenu(StampTree stampTree) {

        // chain の先頭が KarteEditor でかつ Pane が編集可の場合のみメニューが使える
        JMenu myMenu;
        boolean enabled = false;
        KartePane kartePane = null;
        Object obj = getChain();
        if (obj instanceof KarteEditor) {
            KarteEditor editor = (KarteEditor) obj;
            kartePane = editor.getPPane();
            if (kartePane != null) {
                enabled = (kartePane.getTextPane().isEditable());
            }
        }

        if (!enabled) {
            myMenu = new JMenu(stampTree.getTreeName());
            myMenu.setEnabled(false);

        } else {
            // StampTree、JTextPane、Handler からメニューを構築する
            JComponent comp = kartePane.getTextPane();
            TransferHandler handler = comp.getTransferHandler();
            StmapTreeMenuBuilder builder = new StmapTreeMenuBuilder();
            myMenu = builder.build(stampTree, comp, handler);
        }
        return myMenu;
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * 引数のポップアップメニューへ傷病名メニューを追加する。
     *
     * @param popup 傷病名メニューを追加するポップアップメニュー
     */
    public void addDiseaseMenu(JPopupMenu popup) {

        // Chainの先頭がDiagnosisDocumentの時のみ追加する
        boolean enabled = false;
        DiagnosisDocument diagnosis = null;
        Object obj = getChain();
        if (obj instanceof DiagnosisDocument) {
            diagnosis = (DiagnosisDocument) obj;
            enabled = true;
        }

        StampTree stampTree = getStampBox().getStampTree(IInfoModel.ENTITY_DIAGNOSIS);

        if (stampTree != null) {

            if (!enabled) {
                JMenu myMenu = new JMenu(stampTree.getTreeName());
                myMenu.setEnabled(false);
                popup.add(myMenu);
            } else {
                if (diagnosis.getDiagnosisTable() != null) {
                    JComponent comp;
                    comp = diagnosis.getDiagnosisTable();
                    TransferHandler handler = comp.getTransferHandler();
                    StmapTreePopupBuilder builder = new StmapTreePopupBuilder();
                    builder.build(stampTree, popup, comp, handler);
                }
            }
        }
    }

    /**
     * 引数のポップアップメニューへテキストメニューを追加する。
     *
     * @param popup テキストメニューを追加するポップアップメニュー
     */
    public void addTextMenu(JPopupMenu popup) {

        boolean enabled = false;
        KartePane kartePane = null;
        Object obj = getChain();
        if (obj instanceof KarteEditor) {
            KarteEditor editor = (KarteEditor) obj;
            kartePane = editor.getSOAPane();
            if (kartePane != null) {
                enabled = (kartePane.getTextPane().isEditable());
            }
        }

        StampTree stampTree = null;
        try{
            StampBoxPlugin stampBoxPlugin = getStampBox();
            if(stampBoxPlugin != null){
                stampTree = stampBoxPlugin.getStampTree(IInfoModel.ENTITY_TEXT);
            }
        }catch(Exception e){
            System.out.println(e.getStackTrace());
        }

        // ASP スタンプボックスで entity に対応する Tree がない場合がある
        if (stampTree != null) {

            if (!enabled) {
                JMenu myMenu = new JMenu(stampTree.getTreeName());
                myMenu.setEnabled(false);
                popup.add(myMenu);
            } else {
//s.oh^ 2013/03/15 不具合修正(テキストスタンプを追加できない)
             //minagawa^ LSC 1.4 bug fix 元へ返す... テキストスタンプが所見欄にしか張り付かない 2013/06/24
                JComponent comp = getCurrentComponent();
                if (comp == null) {
                    if (kartePane != null) {
                        comp = kartePane.getTextPane();
                    }
                }
                TransferHandler handler;
                if (comp.getTransferHandler() != null) {
                    handler = comp.getTransferHandler();
                    StmapTreePopupBuilder builder = new StmapTreePopupBuilder();
                    builder.build(stampTree, popup, comp, handler);
                }
            }
        }
    }

    /**
     * PPane のコンテキストメニューまたはツールバーの stampIcon へスタンプメニューを追加する。
     *
     * @param menu Ppane のコンテキストメニュー
     * @param kartePane PPnae
     */
    public void addStampMenu(JPopupMenu menu, final KartePane kartePane) {

        // 引数のPaneがPかつ編集可の時のみ追加する
        // コンテキストメニューなのでこれはOK
        if (kartePane != null && kartePane.getMyRole().equals(IInfoModel.ROLE_P) && kartePane.getTextPane().isEditable()) {

            StampBoxPlugin stampBox = getStampBox();
            if(stampBox == null){
                return;
            }

            List<StampTree> trees = stampBox.getAllTrees();

            StmapTreeMenuBuilder builder = new StmapTreeMenuBuilder();
            JComponent cmp = kartePane.getTextPane();
            TransferHandler handler = cmp.getTransferHandler();

            // StampBox内の全Treeをイテレートする
            for (StampTree stampTree : trees) {

                // 傷病名とテキストは別に作成するのでスキップする
                String entity = stampTree.getEntity();
                if (entity.equals(IInfoModel.ENTITY_DIAGNOSIS) || entity.equals(IInfoModel.ENTITY_TEXT)) {
                    continue;
                }

                JMenu subMenu = builder.build(stampTree, cmp, handler);
                menu.add(subMenu);
            }
        }
    }

    /**
     * 引数のポップアップメニューへスタンプメニューを追加する。 このメソッドはツールバーの stamp icon の actionPerformed
     * からコールされる。
     *
     * @param popup
     */
    public void addStampMenu(JPopupMenu popup) {

        boolean enabled = false;
        KartePane kartePane = null;
        Object obj = getChain();
        if (obj instanceof KarteEditor) {
            KarteEditor editor = (KarteEditor) obj;
            kartePane = editor.getPPane();
            if (kartePane != null) {
                enabled = (kartePane.getTextPane().isEditable());
            }
        }

        if (enabled) {
            addStampMenu(popup, kartePane);
        }
    }

    public StampTree getStampTree(String entity) {

        StampTree stampTree = getStampBox().getStampTree(entity);
        return stampTree;
    }

    public StampBoxPlugin getStampBox() {
        return (StampBoxPlugin) chart.getContext().getPlugin("stampBox");
    }

    public boolean hasTree(String entity) {
        StampBoxPlugin stBox = (StampBoxPlugin) chart.getContext().getPlugin("stampBox");
        StampTree tree = stBox.getStampTree(entity);
        return tree != null;
    }

    public void applyInsurance(PVTHealthInsuranceModel hm) {

        Object target = getChain();
        if (target != null) {
            try {
                Method m = target.getClass().getMethod("applyInsurance", new Class[]{hm.getClass()});
                m.invoke(target, new Object[]{hm});

            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                //e.printStackTrace();
            }
        }
    }

    //-----------------------------------------------------------------------------
    @Override
    public void cut() {
        JComponent focusOwner = getCurrentFocusOwner();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get(TransferHandler.getCutAction().getValue(Action.NAME));
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
                setCurKarteTransferHandler(null);
            }
        }
    }

    @Override
    public void copy() {
        JComponent focusOwner = getCurrentFocusOwner();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get(TransferHandler.getCopyAction().getValue(Action.NAME));
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    @Override
    public void paste() {
        JComponent focusOwner = getCurrentFocusOwner();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get(TransferHandler.getPasteAction().getValue(Action.NAME));
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    public void resetStyle() {
        JComponent focusOwner = getCurrentFocusOwner();
        if (focusOwner != null && focusOwner instanceof JTextPane) {
            JTextPane pane = (JTextPane) focusOwner;
            pane.setCharacterAttributes(SimpleAttributeSet.EMPTY, true);
        }
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
        updateUndoAction();
        updateRedoAction();
    }

    public void undo() {
        try {
            undoManager.undo();

        } catch (CannotUndoException ex) {
            ex.printStackTrace(System.err);
        }
        updateUndoAction();
        updateRedoAction();
    }

    public void redo() {
        try {
            undoManager.redo();
        } catch (CannotRedoException ex) {
            ex.printStackTrace(System.err);
        }
        updateRedoAction();
        updateUndoAction();
    }

    private void updateUndoAction() {

        if (undoManager.canUndo()) {
            undoAction.setEnabled(true);
        } else {
            undoAction.setEnabled(false);
        }
    }

    private void updateRedoAction() {

        if (undoManager.canRedo()) {
            redoAction.setEnabled(true);
        } else {
            redoAction.setEnabled(false);
        }
    }

    public void fontLarger() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            if (curSize < 6) {
                curSize++;
            }
            int size = FONT_SIZE[curSize];
            Action a = focusOwner.getActionMap().get("font-size-" + size);
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    public void fontSmaller() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            if (curSize > 0) {
                curSize--;
            }
            int size = FONT_SIZE[curSize];
            Action a = focusOwner.getActionMap().get("font-size-" + size);
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    public void fontStandard() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            curSize = defaultFontSizeIndex();
            int size = FONT_SIZE[curSize];
            Action a = focusOwner.getActionMap().get("font-size-" + size);
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
            enabledAction("fontSmaller", true);
            enabledAction("fontLarger", true);
        }
    }

    public void fontBold() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("font-bold");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    public void fontItalic() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("font-italic");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    public void fontUnderline() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("font-underline");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    public void leftJustify() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("left-justify");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    public void centerJustify() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("center-justify");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    public void rightJustify() {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = focusOwner.getActionMap().get("right-justify");
            if (a != null) {
                a.actionPerformed(new ActionEvent(focusOwner,
                        ActionEvent.ACTION_PERFORMED,
                        null));
            }
        }
    }

    private void colorAction(Color color) {
        JComponent focusOwner = getCurrentComponent();
        if (focusOwner != null) {
            Action a = new StyledEditorKit.ForegroundAction("color", color);
            a.actionPerformed(new ActionEvent(focusOwner,
                    ActionEvent.ACTION_PERFORMED,
                    "foreground"));
        }
    }

    public void fontRed() {
        colorAction(ClientContext.getColor("color.set.default.red"));
    }

    public void fontOrange() {
        colorAction(ClientContext.getColor("color.set.default.orange"));
    }

    public void fontYellow() {
        colorAction(ClientContext.getColor("color.set.default.yellow"));
    }

    public void fontGreen() {
        colorAction(ClientContext.getColor("color.set.default.green"));
    }

    public void fontBlue() {
        colorAction(ClientContext.getColor("color.set.default.blue"));
    }

    public void fontPurple() {
        colorAction(ClientContext.getColor("color.set.default.purpule"));
    }

    public void fontGray() {
        colorAction(ClientContext.getColor("color.set.default.gray"));
    }

    public void fontBlack() {
        colorAction(Color.BLACK);
    }
}
