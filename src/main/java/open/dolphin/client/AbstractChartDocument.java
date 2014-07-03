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

import java.awt.Window;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import open.dolphin.delegater.LetterDelegater;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.letter.LetterImpl;
import open.dolphin.letter.MedicalCertificateImpl;
import open.dolphin.letter.Reply1Impl;
import open.dolphin.letter.Reply2Impl;
import open.dolphin.project.Project;
import open.dolphin.util.Log;

/**
 * チャートドキュメントのルートクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractChartDocument implements ChartDocument {
        
    private static final String[] CHART_MENUS = {
//minagawa^ LSC Test        
        GUIConst.ACTION_NEW_KARTE,GUIConst.ACTION_NEW_DOCUMENT,
//minagawa$        
        GUIConst.ACTION_OPEN_KARTE, GUIConst.ACTION_SAVE, GUIConst.ACTION_DELETE, GUIConst.ACTION_PRINT, GUIConst.ACTION_MODIFY_KARTE,
        GUIConst.ACTION_CHANGE_NUM_OF_DATES_ALL,GUIConst.ACTION_CREATE_PRISCRIPTION,GUIConst.ACTION_SEND_CLAIM,GUIConst.ACTION_CHECK_INTERACTION,GUIConst.ACTION_ASCENDING, GUIConst.ACTION_DESCENDING, GUIConst.ACTION_SHOW_MODIFIED,
        GUIConst.ACTION_INSERT_TEXT, GUIConst.ACTION_INSERT_SCHEMA, GUIConst.ACTION_ATTACHMENT, GUIConst.ACTION_INSERT_STAMP,GUIConst.ACTION_SELECT_INSURANCE,
        GUIConst.ACTION_CUT, GUIConst.ACTION_COPY, GUIConst.ACTION_PASTE, GUIConst.ACTION_UNDO, GUIConst.ACTION_REDO
    }; 
    // GUIConst.ACTION_SEND_CLAIM 元町皮ふ科
    // GUIConst.ACTION_CREATE_PRISCRIPTION Hiro Clinic porting
    // GUIConst.ACTION_CHECK_INTERACTION Masuda-Naika
    
    private Chart chartContext;
    private String title;
    private JPanel ui;
    private boolean dirty;
    
    /** Creates new DefaultChartDocument */
    public AbstractChartDocument() {
        setUI(new JPanel());
    }
    
//minagawa^ 保存時に確認ダイアログを表示せず、saveAll した時の対応   
    protected PropertyChangeSupport boundSupport;
    private boolean chartDocDidSave;
    
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport==null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport!=null) {
            boundSupport.removePropertyChangeListener(prop, l);
        }
    }
    
    @Override
    public boolean isChartDocDidSave() {
        return chartDocDidSave;
    }
    
    @Override
    public void setChartDocDidSave(boolean b) {
        chartDocDidSave = b;
        if (boundSupport!=null) {
            boundSupport.firePropertyChange(CHART_DOC_DID_SAVE, !chartDocDidSave, chartDocDidSave);
        }
    }
 //minagawa$
    
    @Override
    public String getTitle() {
        return title;
    }
    
    @Override
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public ImageIcon getIconInfo(Chart chart) {
        return null;
    }
    
    @Override
    public Chart getContext() {
        return chartContext;
    }
    
    @Override
    public void setContext(Chart chart) {
        this.chartContext = chart;
    }
    
    @Override
    public abstract void start();
    
    @Override
    public abstract void stop();
    
    @Override
    public void enter() {
        // status パネルの表示内容をクリアする
        if (chartContext.getStatusPanel()!=null) {
            chartContext.getStatusPanel().setMessage("");
        }
        // responder chain の先頭になる
        getContext().getChartMediator().addChain(this);

        // 全てのメニューを disabled にする
        disableMenus();
//minagawa^ LSC Test 新規カルテはdisabled       
//        // 新規カルテと新規文書のActionを制御する
//        getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, !isReadOnly());
        if(getContext() instanceof ChartImpl){
            ChartImpl impl = (ChartImpl)getContext();
            if(impl.getKarteSplitPane().getTopComponent() == null || impl.getKarteSplitPane().getBottomComponent() == null){
                getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, !isReadOnly());
            }    
        }
//minagawa$        
    }
    
    @Override
    public JPanel getUI() {
        return ui;
    }
    
    public final void setUI(JPanel ui) {
        this.ui = ui;
    }
    
    @Override
    public void print() {}
    
    @Override
    public boolean isDirty() {
        return dirty;
    }
    
    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    public boolean isReadOnly() {
        return chartContext.isReadOnly();
    }
    
    public void disableMenus() {
        // このウインドウに関連する全てのメニューをdisableにする
        ChartMediator mediator = getContext().getChartMediator();
        mediator.disableMenus(CHART_MENUS);
    }
    
    /**
     * 共通の警告表示を行う。
     * @param message
     */
    protected void warning(String title, String message) {
        Window parent = SwingUtilities.getWindowAncestor(getUI());
        JOptionPane.showMessageDialog(parent, message, ClientContext.getFrameTitle(title), JOptionPane.WARNING_MESSAGE);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, ClientContext.getFrameTitle(title), message);
    }

    @Override
    public void save(){
        ChartImpl impl = (ChartImpl) getContext();
        List<UnsavedDocument> localDirtyList = impl.evoDirtyList();
        for (UnsavedDocument doc : localDirtyList) {
            
            ChartDocument chart = doc.getDoc();
            if (chart instanceof KarteEditor) {
                KarteEditor obj = (KarteEditor) doc.getDoc();
                obj.save();
                if (!obj.isCancelFlag()) {
                    impl.evoDirtyListClean(chart);
                }
                return;
            } else {
                if (Boolean.valueOf(Project.getString(Project.KARTE_SPLIT_SELECT))) {
                    ((ChartImpl) getContext()).getKarteSplitPane().setBottomComponent(null);
                } else {
                    ((ChartImpl) getContext()).getKarteSplitPane().setTopComponent(null);
                }
                
                if (chart instanceof LetterImpl) {
                    LetterImpl obj = (LetterImpl) doc.getDoc();
                    obj.viewToModel(true);
                    LetterModule model = obj.getModel();
                    LetterDelegater ddl = new LetterDelegater();
                    long result = 0;
                    try {
                        result = ddl.saveOrUpdateLetter(model);
                    } catch (Exception ex) {
                        Logger.getLogger(LetterImpl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                    model.setId(result);
                    if (boundSupport != null) {
                        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "診療情報提供書", "保存成功", "インスペクタの終了");
                        setChartDocDidSave(true);
                        return;
                    }

                    getContext().getDocumentHistory().getLetterHistory();
                    obj.stateMgr.processSavedEvent();

                    ((ChartImpl) getContext()).getKarteSplitPane().revalidate();
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "診療情報提供書", "保存成功");
                    impl.evoDirtyListClean(chart);

                    return;
                } else if (chart instanceof MedicalCertificateImpl) {
                    MedicalCertificateImpl obj = (MedicalCertificateImpl) doc.getDoc();
                    obj.viewToModel(true);
                    LetterModule model = obj.getModel();
                    LetterDelegater ddl = new LetterDelegater();
                    long result = 0;
                    try {
                        result = ddl.saveOrUpdateLetter(model);
                    } catch (Exception ex) {
                        Logger.getLogger(MedicalCertificateImpl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                    model.setId(result);
                    if (boundSupport != null) {
                        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "診断書", "保存成功", "インスペクタの終了");
                        setChartDocDidSave(true);
                        return;
                    }

                    getContext().getDocumentHistory().getLetterHistory();
                    obj.stateMgr.processSavedEvent();

                    ((ChartImpl) getContext()).getKarteSplitPane().revalidate();
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "診断書", "保存成功");
                    impl.evoDirtyListClean(chart);

                    return;
                } else if (chart instanceof Reply1Impl) {
                    Reply1Impl obj = (Reply1Impl) doc.getDoc();
                    obj.viewToModel(true);
                    LetterModule model = obj.getModel();
                    LetterDelegater ddl = new LetterDelegater();
                    long result = 0;
                    try {
                        result = ddl.saveOrUpdateLetter(model);
                    } catch (Exception ex) {
                        Logger.getLogger(Reply1Impl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                    model.setId(result);
                    if (boundSupport != null) {
                        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "紹介患者経過報告書", "保存成功", "インスペクタの終了");
                        setChartDocDidSave(true);
                        return;
                    }

                    getContext().getDocumentHistory().getLetterHistory();
                    obj.stateMgr.processSavedEvent();

                    ((ChartImpl) getContext()).getKarteSplitPane().revalidate();
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "紹介患者経過報告書", "保存成功");
                    impl.evoDirtyListClean(chart);

                    return;
                } else if (chart instanceof Reply2Impl) {
                    Reply2Impl obj = (Reply2Impl) doc.getDoc();
                    obj.viewToModel(true);
                    LetterModule model = obj.getModel();
                    LetterDelegater ddl = new LetterDelegater();
                    long result = 0;
                    try {
                        result = ddl.saveOrUpdateLetter(model);
                    } catch (Exception ex) {
                        Logger.getLogger(Reply2Impl.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    }
                    model.setId(result);
                    if (boundSupport != null) {
                        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "ご　報　告", "保存成功", "インスペクタの終了");
                        setChartDocDidSave(true);
                        return;
                    }

                    getContext().getDocumentHistory().getLetterHistory();
                    obj.stateMgr.processSavedEvent();

                    ((ChartImpl) getContext()).getKarteSplitPane().revalidate();
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "ご　報　告", "保存成功");
                    impl.evoDirtyListClean(chart);

                    return;
                }
            }
        }
    }

}