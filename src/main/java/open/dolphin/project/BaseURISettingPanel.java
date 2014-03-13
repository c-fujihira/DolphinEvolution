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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ServerInfo;
import open.dolphin.system.AddFacilityDialog;

/**
 * Base URI を設定する画面
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class BaseURISettingPanel extends AbstractSettingPanel {

    private String ipAddressPattern = "[A-Za-z0-9.\\-_:/]*";
    private static final String ID = "hostSetting";
    private static final String TITLE = "サーバ";
//minagawa^ Icon Server    
    //- private static final String ICON = "ntwrk_16.gif";
    private static final String ICON = "icon_server_settings_small";
//minagawa$    

    // View
    private BaseURISettingView view;

    // 画面用のモデル
    private BaseURISettingPanel.ServerModel model;

    private BaseURISettingPanel.StateMgr stateMgr;

    public BaseURISettingPanel() {
        this.setId(ID);
        this.setTitle(TITLE);
        this.setIcon(ICON);
    }

    /**
     * サーバ設定画面を開始する。
     */
    @Override
    public void start() {

        // 画面モデルを生成し初期化する
        model = new BaseURISettingPanel.ServerModel();
        model.populate(getProjectStub());

        // GUI を生成する
        initComponents();

        // コンテナで表示される
        bindModelToView();
    }

    /**
     * GUI コンポーネントを初期化する。
     */
    private void initComponents() {

        view = new BaseURISettingView();

        // アカウント作成可視
        view.getAccountMakerView().setVisible(true);

        setUI(view);

        // コンポーネントのリスナ接続を行う
        connect();
    }

    /**
     * コンポーネントのリスナ接続を行う。
     */
    private void connect() {

        stateMgr = new BaseURISettingPanel.StateMgr();

        // TextField へ入力または削除があった場合、cutState へ checkState() を送る
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

        // Text fields
        final JTextField facilityIdField = view.getConnectionSettingView().getFacilityIdFld();
        final JTextField userIdField = view.getConnectionSettingView().getUserIdFld();
        final JTextField baseURIField = view.getConnectionSettingView().getBaseURIFld();

        // addDocumentListener
        facilityIdField.getDocument().addDocumentListener(dl);
        userIdField.getDocument().addDocumentListener(dl);
        baseURIField.getDocument().addDocumentListener(dl);

        // IME OFF FocusAdapter
        facilityIdField.addFocusListener(AutoRomanListener.getInstance());
        userIdField.addFocusListener(AutoRomanListener.getInstance());
        baseURIField.addFocusListener(AutoRomanListener.getInstance());

        // Focus をサイクルさせる
        facilityIdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userIdField.requestFocus();
            }
        });

        userIdField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                baseURIField.requestFocus();
            }
        });

        baseURIField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                facilityIdField.requestFocus();
            }
        });

        // DolphinPro 以外は disabled
        baseURIField.setEnabled(true);

        // aacount
        // aacount登録ボタンがクリックされたら自身をPropertyChangeListener にし
        // ダイアログを別スレッドでスタートさせる
//        if (ClientContext.is5mTest()) {
//            view.getAccountMakerView().getAccountMakeBtn().addActionListener(new ActionListener() {
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    make5TestAccount();
//                }
//            });
//        }
        // ログインしている状態の場合、この設定はできないようにする
        if (isLoginState()) {
            userIdField.setEnabled(false);
            baseURIField.setEnabled(false);
            facilityIdField.setEnabled(false);
//            if (ClientContext.is5mTest()) {
//                view.getAccountMakerView().getAccountMakeBtn().setEnabled(false);
//            }
        }
    }

    /**
     * Model 値を表示する。
     */
    private void bindModelToView() {

        // 施設ID
        String val = model.getFacilityId();
        val = val != null ? val : "";
        view.getConnectionSettingView().getFacilityIdFld().setText(val);

        // userId
        val = model.getUserId();
        val = val != null ? val : "";
        view.getConnectionSettingView().getUserIdFld().setText(val);

        // base URI
        val = model.getBaseURI();
        val = val != null ? val : "";
        view.getConnectionSettingView().getBaseURIFld().setText(val);
    }

    /**
     * Viewの値をモデルへ設定する。
     */
    private void bindViewToModel() {
        model.setFacilityId(view.getConnectionSettingView().getFacilityIdFld().getText().trim());
        model.setUserId(view.getConnectionSettingView().getUserIdFld().getText().trim());
        model.setBaseURI(view.getConnectionSettingView().getBaseURIFld().getText().trim());
    }

    /**
     * 5分間評価用のアカウントを作成する。
     */
    public void make5TestAccount() {
        AddFacilityDialog af = new AddFacilityDialog();
        PropertyChangeListener pl = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                newAccount((ServerInfo) evt.getNewValue());
            }
        };
        af.addPropertyChangeListener(AddFacilityDialog.ACCOUNT_INFO, pl);
        Thread t = new Thread(af);
        t.setPriority(Thread.NORM_PRIORITY);
        t.start();
    }

    /**
     * 管理者登録ダイアログの結果を受け取り情報を表示する。
     *
     * @param info
     */
    public void newAccount(ServerInfo info) {
        if (info != null) {
            view.getConnectionSettingView().getFacilityIdFld().setText(info.getFacilityId());
            view.getConnectionSettingView().getUserIdFld().setText(info.getAdminId());
        }
    }

    /**
     * 設定値を保存する。
     */
    @Override
    public void save() {

        // ViewToModel
        bindViewToModel();

        // Store Model to ProjectStub
        model.restore(getProjectStub());
    }

    /**
     * サーバ画面設定用のモデルクラス。
     */
    class ServerModel {

        private String facilityId;
        private String userId;
        private String baseURI;

        public ServerModel() {
        }

        /**
         * ProjectStub からポピュレイトする。
         */
        public void populate(ProjectStub stub) {

            // 施設IDを設定する
            setFacilityId(stub.getFacilityId());

            // userId設定する
            setUserId(stub.getUserId());

            // baseURI を設定する
            setBaseURI(stub.getServerURI());
        }

        /**
         * ProjectStubへリストアする。
         */
        public void restore(ProjectStub stub) {

            // 施設IDを保存する
            stub.setFacilityId(getFacilityId());    // 1.3.6.1.4.1.9414.2.xxx

            // ユーザIDを保存する
            stub.setUserId(getUserId());            // local userId

            // baseURIを保存する
            stub.setServerURI(getBaseURI());
        }

        public String getBaseURI() {
            return baseURI;
        }

        public void setBaseURI(String ipAddress) {
            this.baseURI = ipAddress;
        }

        public String getFacilityId() {
            return facilityId;
        }

        public void setFacilityId(String facilityId) {
            this.facilityId = facilityId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }
    }

    /**
     * Mediator 的 StateMgr クラス。
     */
    class StateMgr {

        public void checkState() {

            AbstractSettingPanel.State newState = isValid()
                    ? AbstractSettingPanel.State.VALID_STATE
                    : AbstractSettingPanel.State.INVALID_STATE;
            if (newState != state) {
                setState(newState);
            }
        }

        private boolean isValid() {
            boolean ok = true;
            ok = ok && (!view.getConnectionSettingView().getFacilityIdFld().getText().trim().equals(""));
            ok = ok && (!view.getConnectionSettingView().getUserIdFld().getText().trim().equals(""));
            ok = ok && (!view.getConnectionSettingView().getBaseURIFld().getText().trim().equals(""));
            return ok;
        }
    }
}
