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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.project.Project;

/**
 * SaveDialog
 * (予定カルテ対応)
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 */
public final class SaveDialogSchedule extends AbstractSaveDialog {
    
    private static final String CHK_TITLE_SEND_WHEN_SCHEDULE = "診療行為を送信する";
    private static final String TOOLTIP_DEPENDS_ON_CHECK = "診療行為の送信はチェックボックスに従います。";
    
//s.oh^ 2013/12/12 予定カルテのオーダー対応
    // LabTest 送信
    private JCheckBox sendLabtest;
//s.oh$

    public SaveDialogSchedule() {
    }
    
    /**
     * コンポーネントにSaveParamsの値を設定する。
     */
    @Override
    public void setValue(SaveParamsM params) {
        
        enterParams = params;
        
        JPanel contentPanel = createComponent();

        Object[] options = new Object[]{tmpButton, cancelButton};

        JOptionPane jop = new JOptionPane(
                contentPanel,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                tmpButton);

        dialog = jop.createDialog(parent, ClientContext.getFrameTitle(TITLE));
        
        // Titleを表示する
//masuda^ 修正元のタイトルもコンボボックスに入れる   
        String[] titles = new String[]{params.getOldTitle(), params.getTitle()};
//masuda$        
        for (String str : titles) {
            if (str != null && (!str.equals("") && (!str.equals("経過記録")))) {
                titleCombo.insertItemAt(str, 0);
            }
        }
        titleCombo.setSelectedIndex(0);
        
        // 診療科を表示する
        // 受付情報からの診療科を設定する
        String val = params.getDepartment();
        if (val != null) {
            String[] depts = val.split("\\s*,\\s*");
            if (depts[0] != null) {
                departmentLabel.setText(depts[0]);
            } else {
                departmentLabel.setText(val);
            }
        }
        
        // 印刷部数選択
        int count = params.getPrintCount();
        if (count != -1) {
            printCombo.setSelectedItem(String.valueOf(count));
            
        } else {
            printCombo.setEnabled(false);
        }

        //--------------------------------
        // CLAIM 送信をチェックする
        //--------------------------------
        boolean sendEnabled = params.isSendEnabled();
        sendClaimAction.setEnabled(sendEnabled);
        if (sendEnabled && params.isSendClaim()) {
            sendClaim.doClick();
        }
        
//s.oh^ 2013/12/12 予定カルテのオーダー対応
        //-------------------------------
        // 検体検査オーダー送信
        //-------------------------------
        sendLabtest.setSelected(params.isSendLabtest() && params.isHasLabtest());
        sendLabtest.setEnabled((sendEnabled && params.isHasLabtest()));
//s.oh$
        
        checkTitle();
        
        controlButton();
    }

    /**
     * GUIコンポーネントを初期化する。
     */
    private JPanel createComponent() {
                
        // content
        JPanel content = new JPanel();
        content.setLayout(new GridLayout(0, 1));
        
        // 文書Title
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titleCombo = new JComboBox(TITLE_LIST);
        titleCombo.setPreferredSize(new Dimension(220, titleCombo.getPreferredSize().height));
        titleCombo.setMaximumSize(titleCombo.getPreferredSize());
        titleCombo.setEditable(true);
        p.add(new JLabel("タイトル:"));
        p.add(titleCombo);
        content.add(p);
        
        // ComboBox のエディタコンポーネントへリスナを設定する
        titleField = (JTextField)titleCombo.getEditor().getEditorComponent();
        titleField.addFocusListener(AutoKanjiListener.getInstance());
        titleField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                checkTitle();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                checkTitle();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                checkTitle();
            }
        });
        
        // 診療科、印刷部数を表示するラベルとパネルを生成する
        JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        departmentLabel = new JLabel();
        p1.add(new JLabel("診療科:"));
        p1.add(departmentLabel);
        
        p1.add(Box.createRigidArea(new Dimension(11, 0)));
        
        // Print
        printCombo = new JComboBox(PRINT_COUNT);
        printCombo.setSelectedIndex(1);
        p1.add(new JLabel("印刷部数:"));
        p1.add(printCombo);
        
        content.add(p1);
        
        //---------------------------
        // CLAIM 送信ありなし
        //--------------------------- 
        sendClaimAction = new AbstractAction(CHK_TITLE_SEND_WHEN_SCHEDULE) {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        sendClaim = new JCheckBox();
        sendClaim.setAction(sendClaimAction);
        JPanel p5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p5.add(sendClaim);
        content.add(p5);
        
//s.oh^ 2013/12/12 予定カルテのオーダー対応
        //---------------------------
        // 検体検査オーダー送信ありなし
        //---------------------------
        sendLabtest = new JCheckBox("検体検査オーダー");
        if (Project.getBoolean(Project.SEND_LABTEST)) {
            JPanel p6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p6.add(sendLabtest);
            content.add(p6);
        }
//s.oh$
        
        // Cancel Button
        String buttonText =  (String)UIManager.get("OptionPane.cancelButtonText");
        cancelButton = new JButton(buttonText);
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                value = null;
                close();
            }
        });
        
        // 仮保存 button
        tmpButton = new JButton(TMP_SAVE);
        tmpButton.setToolTipText(TOOLTIP_DEPENDS_ON_CHECK);
        tmpButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // 戻り値のSaveparamsを生成する
                value = viewToModel();
                if (value != null) {
                    close();
                }
            }
        });
        tmpButton.setEnabled(false);
        
        return content;
    }
    
    private void close() {
        dialog.setVisible(false);
        dialog.dispose();
    }
    
    private void controlButton() {
        tmpButton.setEnabled(true);
    }
    
    /**
     * タイトルフィールドの有効性をチェックする。
     */
    private void checkTitle() {    
        boolean hasTitle = !titleField.getText().trim().isEmpty();
        if (hasTitle) {
            controlButton();
        } else {
            tmpButton.setEnabled(false);
        }
    }

    private SaveParamsM viewToModel() {
        
        // 戻り値のSaveparamsを生成する
        SaveParamsM model = new SaveParamsM();
        
        // 戻り値の整理 仮ボタンが押された時     1
        int returnOption = SaveParamsM.SAVE_AS_TMP;
        
        // 開始時と終了時のオプションでKarteEditorで制御する
        model.setEnterOption(enterParams.getEnterOption());
        model.setReturnOption(returnOption);
        // Title候補
        String titleCand = "";
        
        switch (returnOption) {
                
            case SaveParamsM.SAVE_AS_TMP:
                // 仮ボタンが押された時
                model.setTmpSave(true);                                 // 仮保存
                model.setSendClaim(sendClaim.isSelected());             // CLAIM送信->CheckBox
                model.setClaimDate(enterParams.getClaimDate());         // CLAIM送信日
//s.oh^ 2013/12/12 予定カルテのオーダー対応
                //model.setSendLabtest(false);                            // Lab.Test送信->false 互換性を確保..
                model.setSendLabtest(sendLabtest.isSelected());         // Lab.Test送信->CheckBox
//s.oh$
                model.setAllowPatientRef(false);                        // MML->送信しない
                model.setAllowClinicRef(false);                         // MML->送信しない
                model.setSendMML(false);
                titleCand = "仮保存";
                break;
        }
        
        // 文書タイトルを取得する
        String val = (String)titleCombo.getSelectedItem();
        val = (val.isEmpty()) ? titleCand : val;
        model.setTitle(val);
        
        // Department
        val = departmentLabel.getText();
        model.setDepartment(val);
        
        // 印刷部数を取得する
        int count = Integer.parseInt((String)printCombo.getSelectedItem());
        model.setPrintCount(count);
        
        return model;
    }
}