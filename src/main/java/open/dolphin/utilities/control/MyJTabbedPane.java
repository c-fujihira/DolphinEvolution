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
package open.dolphin.utilities.control;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import open.dolphin.client.ChartEventHandler;
import open.dolphin.client.ChartImpl;
import open.dolphin.client.KarteEditor;
import open.dolphin.impl.labrcv.NLabTestImportView;
import open.dolphin.impl.psearch.PatientSearchView;
import open.dolphin.impl.pvt.WatingListView;
import open.dolphin.infomodel.PatientVisitModel;

/**
 * MyJTabbedPane
 *
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class MyJTabbedPane{

    private static final Icon icon = new CloseTabIcon();

    // Variables declaration - do not modify                     
    private final DnDTabbedPane dndTabbedPane = new DnDTabbedPane();
    // カルテタブ表示分の患者ID保持用
    private List<String> patientIdList = null;
    // カルテタブのロック解除用情報保持用
    private Map<String, PatientVisitModel> patientIdMap;
    // カルテタブのロック解除用情報保持用
    private Map<String, ChartImpl> chartImplMap;
    //- カルテ実体
    private ChartImpl owner;

    public void addTab(String title, final JComponent c) {

        // Add the tab to the pane without any label
        dndTabbedPane.addTab(title, c);

        // Create a FlowLayout that will space things 5px apart
        FlowLayout f = new FlowLayout(FlowLayout.CENTER, 5, 0);

        // Make a small JPanel with the layout and make it non-opaque
        JPanel pnlTab = new JPanel(f);
        pnlTab.setOpaque(false);

        // Add a JLabel with title and the left-side tab icon
        JLabel lblTitle = new JLabel(title);

        // Create a JButton for the close tab button
        JButton btnClose = new JButton(icon);
        btnClose.setBorder(BorderFactory.createEmptyBorder());

        // Put the panel together
        pnlTab.add(lblTitle);
        pnlTab.add(btnClose);

        // Add a thin border to keep the image below the top edge of the tab
        // when the tab is selected
        pnlTab.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

        // Now assign the component for the tab
        dndTabbedPane.setTabComponentAt(dndTabbedPane.indexOfComponent(c), pnlTab);

        // Add the listener that removes the tab
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // The component parameter must be declared "final" so that it can be
                // referenced in the anonymous listener class like this.
                String patientId = dndTabbedPane.getTitleAt(dndTabbedPane.indexOfComponent(c));
                ChartImpl chart = chartImplMap.get(patientId);
                if (!chart.isDirty()) {
                    tabClosingFunc(patientId, chart, c);
                    return;
                }

                if (chart.getKarteEditor() != null) {
                    KarteEditor editor = chart.getKarteEditor();
                    String ret = chart.closeForEvo();
                    if (!editor.isCancelFlag() && ret.equals("0")) {
                        // 保存ダイアログが保存かつ保存時併用禁忌有無のチェック処理が無視の場合、カルテタブ閉じる処理を行う
                        tabClosingFunc(patientId, chart, c);
                    } else if (ret.equals("1")) {
                        // 破棄時カルテタブを閉じる処理を行う
                        tabClosingFunc(patientId, chart, c);
                    } else if (ret.equals("2")) {
                        // キャンセル時、何もしない
                    } else if (ret.equals("3")){
                        // dirtyListが無い時カルテタブを閉じる処理を行う
                        tabClosingFunc(patientId, chart, c);
                    }
                } else {
                    String ret = chart.closeForEvo();
                    KarteEditor editor = chart.getKarteEditor();
                    // 保存時併用禁忌有無のチェック処理後にカルテタブ閉じる処理を行う
                    if (editor != null && !editor.isCancelFlag() && ret.equals("0")) {
                        tabClosingFunc(patientId, chart, c);
                    } else if (ret.equals("1")) {
                        // 破棄時カルテタブを閉じる処理を行う
                        tabClosingFunc(patientId, chart, c);
                    } else if (ret.equals("2")) {
                        // キャンセル時、何もしない
                    }
                }
            }
        };
        btnClose.addActionListener(listener);

        // Optionally bring the new tab to the front
        dndTabbedPane.setSelectedComponent(c);

        //-------------------------------------------------------------
        // Bonus: Adding a <Ctrl-W> keystroke binding to close the tab
        //-------------------------------------------------------------
        AbstractAction closeTabAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String patientId = dndTabbedPane.getTitleAt(dndTabbedPane.indexOfComponent(c));
                ChartImpl chart = chartImplMap.get(patientId);
                if (!chart.isDirty()) {
                    tabClosingFunc(patientId, chart, c);
                    return;
                }

                if (chart.getKarteEditor() != null) {
                    KarteEditor editor = chart.getKarteEditor();
                    String ret = chart.closeForEvo();
                    if (!editor.isCancelFlag() && ret.equals("0")) {
                        // 保存ダイアログが保存かつ保存時併用禁忌有無のチェック処理が無視の場合、カルテタブ閉じる処理を行う
                        tabClosingFunc(patientId, chart, c);
                    } else if (ret.equals("1")) {
                        // 破棄時カルテタブを閉じる処理を行う
                        tabClosingFunc(patientId, chart, c);
                    } else if (ret.equals("2")) {
                        // キャンセル時、何もしない
                    } else if (ret.equals("3")){
                        // dirtyListが無い時カルテタブを閉じる処理を行う
                        tabClosingFunc(patientId, chart, c);
                    }
                } else {
                    String ret = chart.closeForEvo();
                    KarteEditor editor = chart.getKarteEditor();
                    // 保存時併用禁忌有無のチェック処理後にカルテタブ閉じる処理を行う
                    if (editor != null && !editor.isCancelFlag() && ret.equals("0")) {
                        tabClosingFunc(patientId, chart, c);
                    } else if (ret.equals("1")) {
                        // 破棄時カルテタブを閉じる処理を行う
                        tabClosingFunc(patientId, chart, c);
                    } else if (ret.equals("2")) {
                        // キャンセル時、何もしない
                    }
                }
            }
        };

        //-------------------------------------------------------------
        // Bonus: Adding a <Ctrl-D> keystroke binding to back the tab
        //-------------------------------------------------------------
        AbstractAction foreTabAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentIndex = dndTabbedPane.indexOfComponent(c);
                if (currentIndex == 0) {
                    dndTabbedPane.setSelectedIndex(dndTabbedPane.getTabCount() - 1);
                } else {
                    dndTabbedPane.setSelectedIndex(currentIndex - 1);
                }
            }
        };

        //-------------------------------------------------------------
        // Bonus: Adding a <Ctrl-K> keystroke binding to fowarding the tab
        //-------------------------------------------------------------
        AbstractAction jumpTabAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int currentIndex = dndTabbedPane.indexOfComponent(c);
                if (currentIndex == dndTabbedPane.getTabCount() - 1) {
                    dndTabbedPane.setSelectedIndex(0);
                } else {
                    dndTabbedPane.setSelectedIndex(currentIndex + 1);
                }
            }
        };

        // Create a keystroke
        KeyStroke controlW = KeyStroke.getKeyStroke("control W");
        KeyStroke controlD = KeyStroke.getKeyStroke("control D");
        KeyStroke controlK = KeyStroke.getKeyStroke("control K");

        // Get the appropriate input map using the JComponent constants.
        // This one works well when the component is a container. 
        InputMap inputMap = c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);

        // Add the key binding for the keystroke to the action name
        inputMap.put(controlW, "closeTab");
        inputMap.put(controlD, "foreTab");
        inputMap.put(controlK, "jumpTab");

        // Now add a single binding for the action name to the anonymous action
        c.getActionMap().put("closeTab", closeTabAction);
        c.getActionMap().put("foreTab", foreTabAction);
        c.getActionMap().put("jumpTab", jumpTabAction);

//        dndTabbedPane.setSelectedIndex(dndTabbedPane.getTabCount() - 1);
    }

    public void selectTab(long id) {

        try {
            for (int i = 0; i < dndTabbedPane.getTabCount(); i++) {
                String pvId = patientIdList.get(dndTabbedPane.indexOfComponent(dndTabbedPane.getComponentAt(i)));
                PatientVisitModel pvt = patientIdMap.get(pvId);
                if (id == pvt.getPatientModel().getId()) {
                    dndTabbedPane.setSelectedIndex(dndTabbedPane.indexOfComponent(dndTabbedPane.getComponentAt(i)));
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void tabClosingFunc(String patientId, ChartImpl chart, JComponent c) {
        // カルテタブ表示用患者IDリストからID削除
        patientIdList.remove(patientIdList.indexOf(patientId));
        // カルテタブ削除   
        dndTabbedPane.removeTabAt(dndTabbedPane.indexOfComponent(c));
        // リストから削除し状態変化を通知する
        ChartEventHandler scl = ChartEventHandler.getInstance();
        scl.publishKarteClosed(patientIdMap.get(patientId));
        // メモ内容保存
        chart.getPatientInspector().dispose();
        patientIdMap.remove(patientId);
        chartImplMap.remove(patientId);
        // カルテを閉じた後にフォーカスを再度テーブルリストにセット
//        setTableListFocus(chart);
        // カルテがすべて閉じられた場合には受付リストを表示させ、リストへフォーカスする
        if(dndTabbedPane.getTabCount() == 0){
            if(owner.getApp().evoWindow.getTrace().equals("WatingList")){
                owner.getApp().evoWindow.mainView.getTabbedPane().setSelectedIndex(0);
                int row, col;
                WatingListView wview = (WatingListView) owner.getApp().evoWindow.mainView.getTabbedPane().getComponentAt(0);
                wview.getTable().setRowSelectionAllowed(true);
                row = wview.getTable().getSelectedRow() < 0 ? 0 : wview.getTable().getSelectedRow();
                col = wview.getTable().getSelectedColumn() < 0 ? 0 : wview.getTable().getSelectedColumn();
    //            wview.getTable().changeSelection(row, col, false, false);
                if(wview.getTable().getRowCount() > 0){
                    wview.getTable().setRowSelectionInterval(row, row);
                }
                wview.getTable().requestFocusInWindow();
            }else if(owner.getApp().evoWindow.getTrace().equals("PatientSearch")){
                owner.getApp().evoWindow.mainView.getTabbedPane().setSelectedIndex(1);
            }else if(owner.getApp().evoWindow.getTrace().equals("NLaboTestImporter")){
                owner.getApp().evoWindow.mainView.getTabbedPane().setSelectedIndex(2);
            }
        }
    }
    
    private void setTableListFocus(ChartImpl chart){
        int currentTab = chart.getContext().getTabbedPane().getSelectedIndex();
        int row, col;
        switch (currentTab) {
            case 0:
                WatingListView wview = (WatingListView)chart.getContext().getTabbedPane().getComponentAt(currentTab);
                wview.getTable().requestFocus();
                wview.getTable().setRowSelectionAllowed(true);
                row = wview.getTable().getSelectedRow();
                col = wview.getTable().getSelectedColumn();
                wview.getTable().changeSelection(row, col, false, false);
                break;
            case 1:
                PatientSearchView pview = (PatientSearchView)chart.getContext().getTabbedPane().getComponentAt(currentTab);
                pview.getTable().requestFocus();
                pview.getTable().setRowSelectionAllowed(true);
                row = pview.getTable().getSelectedRow();
                col = pview.getTable().getSelectedColumn();
                pview.getTable().changeSelection(row, col, false, false);
                break;
            case 2:
                NLabTestImportView nview = (NLabTestImportView)chart.getContext().getTabbedPane().getComponentAt(currentTab);
                nview.getTable().requestFocus();
                nview.getTable().setRowSelectionAllowed(true);
                row = nview.getTable().getSelectedRow();
                col = nview.getTable().getSelectedColumn();
                nview.getTable().changeSelection(row, col, false, false);
                break;
        }
    }
    
    private static class CloseTabIcon implements Icon {

        private final int width;
        private final int height;

        public CloseTabIcon() {
            width = 16;
            height = 16;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.translate(x, y);
            g.setColor(Color.BLACK);
            g.drawLine(4, 4, 11, 11);
            g.drawLine(4, 5, 10, 11);
            g.drawLine(5, 4, 11, 10);
            g.drawLine(11, 4, 4, 11);
            g.drawLine(11, 5, 5, 11);
            g.drawLine(10, 4, 4, 10);
            g.translate(-x, -y);
        }

        @Override
        public int getIconWidth() {
            return width;
        }

        @Override
        public int getIconHeight() {
            return height;
        }
    }

    public void setPatientIdList(List<String> patientIdList) {
        this.patientIdList = patientIdList;
    }

    public void setPatientIdMap(Map<String, PatientVisitModel> patientIdMap) {
        this.patientIdMap = patientIdMap;
    }

    public Map<String, ChartImpl> getChartImplMap() {
        return chartImplMap;
    }

    public void setChartImplMap(Map<String, ChartImpl> chartImplMap) {
        this.chartImplMap = chartImplMap;
    }

    public void setOwner(ChartImpl ci) {
        owner = ci;
    }

    public DnDTabbedPane getTabbedPane() {
        return dndTabbedPane;
    }

}
