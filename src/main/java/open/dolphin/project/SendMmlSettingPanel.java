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
package open.dolphin.project;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.GUIFactory;
import open.dolphin.helper.GridBagBuilder;

/**
 * SendMmlSettingPanel
 * 
 * @author Kazushi Minagawa Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class SendMmlSettingPanel extends AbstractSettingPanel {
    
    private static final String ID = "mmlSetting";
    private static final String TITLE = "MML出力";
//minagawa^ Icon Server    
    //private static final String ICON = "cd_16.gif";
    private static final String ICON = "icon_mml_settings_small";
//minagawa$    
    
    // MML送信関係コンポーネント
    private JRadioButton sendMML;
    private JRadioButton sendNoMML;
    private JRadioButton mml3;
    private JRadioButton mml23;
    private JTextField shareDirectory;
    private JButton dirSettingBtn;
    
    private MmlModel model;
    
    private StateMgr stateMgr;
    
    public SendMmlSettingPanel() {
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }
    
    /**
     * MML出力を開始する。
     */
    @Override
    public void start() {
        
        // モデルを生成する
        model = new MmlModel();
        
        // GUI
        initComponents();
        
        // populate
        model.populate(getProjectStub());
    }
    
    /**
     * 保存する。
     */
    @Override
    public void save() {
        model.restore(getProjectStub());
    }
    
    /**
     * GUIを構築する。
     */
    private void initComponents() {
        
        // 生成
        ButtonGroup bg = new ButtonGroup();
        sendMML = GUIFactory.createRadioButton("行う", null, bg);
        sendNoMML = GUIFactory.createRadioButton("行わない", null, bg);
        bg = new ButtonGroup();
        mml3 = GUIFactory.createRadioButton("3.0", null, bg);
        mml23 = GUIFactory.createRadioButton("2.3", null, bg);
        mml23.setSelected(true);
        shareDirectory = GUIFactory.createTextField(12, null, null, null);
        dirSettingBtn = new JButton("出力先設定...");
        
        // No Support
        mml3.setEnabled(false);
        
        // レイアウト
        GridBagBuilder gbl = new GridBagBuilder("MML(XML)出力");
        gbl.add(GUIFactory.createRadioPanel(new JRadioButton[]{sendMML,sendNoMML}), 0, 0, 2, 1, GridBagConstraints.CENTER);
        gbl.add(new JLabel("MML バージョン:", SwingConstants.RIGHT),	                 0, 1, 1, 1, GridBagConstraints.EAST);
        gbl.add(GUIFactory.createRadioPanel(new JRadioButton[]{mml23,mml3}),        1, 1, 1, 1, GridBagConstraints.WEST);
        gbl.add(dirSettingBtn,                                                      0, 2, 1, 1, GridBagConstraints.EAST);
        gbl.add(shareDirectory,                                                     1, 2, 1, 1, GridBagConstraints.WEST);
        
        JPanel content = gbl.getProduct();
        
        // 全体をレイアウトする
        gbl = new GridBagBuilder();
        gbl.add(content,        0, 0, GridBagConstraints.HORIZONTAL, 1.0, 0.0);
        gbl.add(new JLabel(""), 0, 1, GridBagConstraints.BOTH,       1.0, 1.0);
        
        setUI(gbl.getProduct());
        
    }
    
    public void connect() {
        
        stateMgr = new StateMgr();
        
        // MML送信ボタンがクリックされたら State check を行う
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.controlSendMml();
            }
        };
        sendMML.addActionListener(al);
        sendNoMML.addActionListener(al);
        
        // テキストフィールドのイベントがあったら　State check を行う
        //DocumentListener dl = ProxyDocumentListener.create(stateMgr, "checkState");
        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                stateMgr.checkState();
            }
        };
        //uploaderServer.getDocument().addDocumentListener(dl);
        shareDirectory.getDocument().addDocumentListener(dl);
        
        //
        // IME OFF FocusAdapter
        //
        //uploaderServer.addFocusListener(AutoRomanListener.getInstance());
        shareDirectory.addFocusListener(AutoRomanListener.getInstance());

        dirSettingBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doDirectorySetting();
            }

        });
        
        stateMgr.controlSendMml();
        
    }

    // MML の出力先ディレクトリを指定する
    private void doDirectorySetting() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String dir = chooser.getSelectedFile().getPath();
            shareDirectory.setText(dir);
        }
    }

    class MmlModel {
        
        public void populate(ProjectStub stub) {
            
            boolean sending = Project.getBoolean(Project.SEND_MML); //stub.getSendMML();
            sendNoMML.setSelected(!sending);
            sendMML.setSelected(sending);
            
            mml23.setSelected(true);
            
            // 送信ディレクトリ
            String val = Project.getString(Project.SEND_MML_DIRECTORY); //stub.getUploadShareDirectory();
            if (val != null && ! val.equals("")) {
                shareDirectory.setText(val);
            }
            
            connect();
        }
        
        public void restore(ProjectStub stub) {
            // センター送信
            boolean b = sendMML.isSelected();
            Project.setBoolean(Project.SEND_MML, b);
            
            // MML バージョン
            Project.setString(Project.MML_VERSION, "230");
            
            // 共有ディレクトリ
            String val = shareDirectory.getText().trim();
            if (! val.equals("")) {
                if (val.endsWith(File.separator)) {
                    val = val.substring(0, val.length()-1);
                }
                Project.setString(Project.SEND_MML_DIRECTORY, val);
            }
        }
    }
    
    class StateMgr {
        
        public void checkState() {
            
            AbstractSettingPanel.State newState = isValid()
            ? AbstractSettingPanel.State.VALID_STATE
                    : AbstractSettingPanel.State.INVALID_STATE;
            if (newState != state) {
                setState(newState);
            }
        }
        
        public void controlSendMml() {
            boolean b = sendMML.isSelected();
            mml23.setEnabled(b);
            shareDirectory.setEnabled(b);
            this.checkState();
        }
        
        protected boolean isValid() {
            if (sendMML.isSelected()) {
                boolean ok = true;
                ok = ok && (!shareDirectory.getText().trim().equals(""));
                return ok;
            } else {
                return true;
            }
        }
    }
}
