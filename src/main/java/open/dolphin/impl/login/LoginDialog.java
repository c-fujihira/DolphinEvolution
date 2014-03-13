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
package open.dolphin.impl.login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.AutoRomanListener;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIFactory;
import open.dolphin.delegater.UserDelegater;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;
import open.dolphin.util.Log;

/**
 * ログインダイアログクラス
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class LoginDialog extends AbstractLoginDialog {

    private LoginPanel view;
    private StateMgr stateMgr;
    private boolean loginFlag;

    /**
     * Creates new LoginService
     */
    public LoginDialog() {
    }

    @Override
    protected void tryLogin() {

        if (loginFlag) {
            return;
        } else {
            loginFlag = true;
        }

        // User 情報を取得するためのデリゲータを得る
        if (userDlg == null) {
            userDlg = new UserDelegater();
        }

        // トライ出来る最大回数を得る
        if (maxTryCount == 0) {
            maxTryCount = ClientContext.getInt("loginDialog.maxTryCount");
        }

        ClientContext.getPart11Logger().info("認証を開始します");

        // 試行回数 += 1
        tryCount++;

        //Task
        worker = new SimpleWorker<UserModel, Void>() {

            @Override
            protected UserModel doInBackground() throws Exception {
                String fid = Project.getFacilityId();
                String uid = view.getUserIdField().getText().trim();
                String password = new String(view.getPasswordField().getPassword());
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, "ログインを開始します... ");
                Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, "ユーザーID：", uid);
                Log.outputFuncLog(Log.LOG_LEVEL_4, Log.FUNCTIONLOG_KIND_INFORMATION, "パスワード：", password);
                Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, "医療機関ID：", fid);
                UserModel userModel = userDlg.login(fid, uid, password);
                return userModel;
            }

            @Override
            protected void succeeded(UserModel userModel) {

                if (userModel != null) {
                    // 5分間テストの場合、有効期間をテストする
//                    if (isTestUser(userModel) && isExpired(userModel)) {
//                        // 評価終了
//                        showTestExpiredError();
//                        setResult(LoginStatus.NOT_AUTHENTICATED); 
//                        
//                    } else {

                    // 認証成功
                    String time = ModelUtils.getDateTimeAsString(new Date());
                    StringBuilder sb = new StringBuilder();
                    sb.append(time).append(":");
                    sb.append(userModel.getUserId()).append(" がログインしました");
                    ClientContext.getPart11Logger().info(sb.toString());
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, sb.toString());

                        //----------------------------------
                    // ユーザモデルを ProjectStub へ保存する
                    //----------------------------------
                    Project.getProjectStub().setUserModel(userModel);
                    Project.getProjectStub().setFacilityId(userModel.getFacilityModel().getFacilityId());
                    Project.getProjectStub().setUserId(userModel.idAsLocal());  // facilityId無し

                    setResult(LoginStatus.AUTHENTICATED);
//                    }

                } else {
                    if (tryCount <= maxTryCount) {
                        showUserIdPasswordError();

                    } else {
                        showTryOutError();
                        setResult(LoginStatus.NOT_AUTHENTICATED);
                    }
                    loginFlag = false;
                }
            }

            @Override
            protected void failed(java.lang.Throwable cause) {
                ClientContext.getPart11Logger().warn("Task failed");
                ClientContext.getPart11Logger().warn(cause.getCause());
                ClientContext.getPart11Logger().warn(cause.getMessage());

                if (tryCount <= maxTryCount) {
                    //showMessageDialog(cause.getMessage());
                    showMessageDialog("ログイン情報が間違っています");
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, "ログイン情報が間違っています");

                } else {
                    showTryOutError();
                    setResult(LoginStatus.NOT_AUTHENTICATED);
                }
                loginFlag = false;
            }
        };

        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                    setBusy(false);
                    worker.removePropertyChangeListener(this);
                } else if (SwingWorker.StateValue.STARTED == evt.getNewValue()) {
                    setBusy(true);
                }
            }
        });

        worker.execute();
    }

    /**
     * GUI を構築する。
     */
    @Override
    protected JPanel createComponents() {

        view = new LoginPanel();
//minagawa^ mac jdk7        
        view.getCancelBtn().setText(GUIFactory.getCancelButtonText());
//minagawa$
        // イベント接続を行う
        connect();

//s.oh^ RSS対応
        view.getLogoLabel().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showRSSInfo();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
//s.oh$

        return view;
    }

    /**
     * イベント接続を行う。
     */
    private void connect() {

        // Mediator ライクな StateMgr
        stateMgr = new StateMgr();

        // フィールドにリスナを登録する
        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                stateMgr.checkButtons();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                stateMgr.checkButtons();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                stateMgr.checkButtons();
            }
        };

        JTextField userIdField = view.getUserIdField();
        userIdField.getDocument().addDocumentListener(dl);
        userIdField.addFocusListener(AutoRomanListener.getInstance());
        userIdField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.onUserIdAction();
            }
        });

        JPasswordField passwdField = view.getPasswordField();
        passwdField.getDocument().addDocumentListener(dl);
        passwdField.addFocusListener(AutoRomanListener.getInstance());
        passwdField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stateMgr.onPasswordAction();
            }
        });
    }

    @Override
    protected JButton getLoginButton() {
        return view.getLoginBtn();
    }

    @Override
    protected JButton getCancelButton() {
        return view.getCancelBtn();
    }

    @Override
    protected JButton getSettingButton() {
        return view.getSettingBtn();
    }

    @Override
    protected JProgressBar getProgressBar() {
        return view.getProgressBar();
    }

    /**
     * モデルを表示する。
     */
    @Override
    protected void doWindowOpened() {
        String uid = Project.getUserId();
        if (uid != null && (!uid.equals(""))) {
            view.getUserIdField().setText(uid);
            view.getPasswordField().requestFocus();
        }
    }

    /**
     * 設定ダイアログから通知を受ける。 有効なプロジェクトでればユーザIDをフィールドに設定しパスワードフィールドにフォーカスする。
     *
     */
    @Override
    public void setNewParams(Boolean newValue) {
        boolean valid = newValue.booleanValue();
        if (valid) {
            doWindowOpened();
        }
    }

    /**
     * ログインボタンを制御する簡易 StateMgr クラス。
     */
    class StateMgr {

        private boolean okState;

        public StateMgr() {
        }

        /**
         * ログインボタンの enable/disable を制御する。
         */
        public void checkButtons() {
            boolean newOKState = true;
            newOKState = newOKState && (!view.getUserIdField().getText().equals(""));
            newOKState = newOKState && (view.getPasswordField().getPassword().length > 0);

            if (newOKState != okState) {
                view.getLoginBtn().setEnabled(newOKState);
                okState = newOKState;
            }
        }

        /**
         * UserId フィールドでリターンきーが押された時の処理を行う。
         */
        public void onUserIdAction() {
            view.getPasswordField().requestFocus();
        }

        /**
         * Password フィールドでリターンきーが押された時の処理を行う。
         */
        public void onPasswordAction() {
            if (view.getUserIdField().getText().equals("")) {
                view.getUserIdField().requestFocus();

            } else if (view.getPasswordField().getPassword().length != 0 && okState) {
                view.getLoginBtn().doClick();
            }
        }
    }
}
