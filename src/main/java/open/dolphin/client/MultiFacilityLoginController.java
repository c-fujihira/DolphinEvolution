/*
 * Copyright (C) 2013 S&I Co.,Ltd.
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

package open.dolphin.client;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import open.dolphin.delegater.UserDelegater;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.UserModel;
import open.dolphin.project.Project;
import open.dolphin.project.ProjectStub;
import open.dolphin.util.BlinkText;
import open.dolphin.util.EvolutionWindow;

/**
 * Login multi-facility FXML Controller Class
 *
 * @author Manabu Nishimura <nishimurama@sandi.co.jp>
 */
public class MultiFacilityLoginController extends AnchorPane implements Initializable {

    @FXML
    ChoiceBox<String> multiFacility;
    @FXML
    TextField userId;
    @FXML
    PasswordField pwd;
    @FXML
    Text status;
    @FXML
    ProgressIndicator progressIndi;

    @FXML
    Button buttonAbout;
    @FXML
    Button exitButton;
    @FXML
    Button configButton;
    @FXML
    Button loginButton;
    
    @FXML
    Text clVer;
    @FXML
    Text svVer;

    //- 親インスタンス
    private Stage mainStage;
    private Evolution application;
    private EvolutionWindow mainWindow;

    //- 認証制御用
    protected UserDelegater userDlg;
    protected int tryCount;
    protected int maxTryCount;

    //- 認証結果のプロパティ
    protected ILoginDialog.LoginStatus result;
    protected PropertyChangeSupport boundSupport;

    private String facility = null;
    private ProjectStub projectStub = null;          

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        status.setText("");
        userId.setText(Project.getProjectStub().getUserId());
        buttonAbout.setText(ClientContext.getProductName() + "について");

        projectStub = Project.getProjectStub();
        final String[] facilityNameArray = projectStub.getString("login.set.facility.name", null).split(",");
        final String[] facilityIdArray = projectStub.getString("login.set.facility.id", null).split(",");
        final String[] baseUriArray = projectStub.getString("login.set.base.uri", null).split(",");
        final String[] jmariCodeArray = projectStub.getString("login.set.jmari.code", null).split(",");
//        final String[] claimAddressArray = projectStub.getString("login.set.orca.address", null).split(",");
//        final String[] claimPortArray = projectStub.getString("login.set.orca.port", null).split(",");
        multiFacility.getItems().addAll(facilityNameArray);
        multiFacility.valueProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String t, String t1) {
                for (int i = 0; i < facilityNameArray.length; i++) {
                    if (facilityNameArray[i].equals(t1)) {
                        facility = facilityIdArray[i];
                        projectStub.setFacilityId(facilityIdArray[i]);
                        projectStub.setServerURI(baseUriArray[i]);
                        projectStub.setJPNCode(jmariCodeArray[i]);
//                        Project.setString(Project.CLAIM_ADDRESS, claimAddressArray[i]);
//                        Project.setInt(Project.CLAIM_PORT, Integer.parseInt(claimPortArray[i]));
                    }
                }
            }
        });
        facility = facilityIdArray[0];
        multiFacility.setValue(facilityNameArray[0]);
        projectStub.setFacilityId(facilityIdArray[0]);
        projectStub.setServerURI(baseUriArray[0]);
        projectStub.setJPNCode(jmariCodeArray[0]);
//        Project.setString(Project.CLAIM_ADDRESS, claimAddressArray[0]);
//        Project.setInt(Project.CLAIM_PORT, Integer.parseInt(claimPortArray[0]));
    }

    public void setMainWindow(EvolutionWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    public EvolutionWindow getMainWindow() {
        return mainWindow;
    }

    public void setApp(Evolution application) {
        this.application = application;
    }

    public void setStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void exitLogin(ActionEvent event) {
        //- 終了
        System.exit(0);
    }

    public Stage getStage() {
        return mainStage;
    }

    /**
     * ログイン
     *
     * @param event
     */
    public void execLogin(ActionEvent event) {

        //- Operation無効
        lockOperation();

        //- User情報を取得するためのデリゲータを得る
        if (userDlg == null) {
            userDlg = new UserDelegater();
        }

        //- トライ出来る最大回数を得る
        if (maxTryCount == 0) {
            maxTryCount = ClientContext.getInt("loginDialog.maxTryCount");
        }

        //- 試行回数 +1
        tryCount++;

        ClientContext.getPart11Logger().info("Challenge Authentication is " + "\"" + userId.getText() + "\"");
        tryLogin();
    }

    public void pwdOnLogin(KeyEvent ke) {

        if (ke.getCode().equals(KeyCode.ENTER)) {

            //- Operation無効
            lockOperation();

            //- User情報を取得するためのデリゲータを得る
            if (userDlg == null) {
                userDlg = new UserDelegater();
            }

            //- トライ出来る最大回数を得る
            if (maxTryCount == 0) {
                maxTryCount = ClientContext.getInt("loginDialog.maxTryCount");
            }

            //- 試行回数 +1
            tryCount++;

            ClientContext.getPart11Logger().info("Challenge Authentication is " + "\"" + userId.getText() + "\"");
            tryLogin();

        }
    }

    /**
     * ログイン試行
     *
     * @return
     */
    public Task<Boolean> doLogin() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws RuntimeException, Exception {
                String fid = facility;
                String uid = userId.getText();
                String password = pwd.getText();
                Boolean result = Boolean.FALSE;
                try {
                    UserModel userModel = userDlg.login(fid, uid, password);
                    if (userModel != null) {
                        String time = ModelUtils.getDateTimeAsString(new Date());
                        StringBuilder sb = new StringBuilder();
                        sb.append(time).append(":");
                        sb.append(userModel.getUserId()).append(" がログインしました");
                        ClientContext.getPart11Logger().info(sb.toString());
                        Project.getProjectStub().setUserModel(userModel);
                        Project.getProjectStub().setFacilityId(userModel.getFacilityModel().getFacilityId());
                        Project.getProjectStub().setUserId(userModel.idAsLocal());
                        result = Boolean.TRUE;
                    }
                } catch (Exception e) {
                    ClientContext.getPart11Logger().error(e);
                }
                return result;
            }
        };
    }

    /**
     * ログイン実行(BackGround)
     */
    public void tryLogin() {
        final Task<Boolean> login = doLogin();
        login.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                if (login.getValue()) {
                    //- Load StampTree & CreateMainWindow
                    application.mainDisplay();
                    unLockOperation();
                    pwd.clear();
                    return;
                }

                //- ログイン試行回数を超えたら強制終了(ロックアウト)
                if (tryCount >= maxTryCount) {
                    showTryOutError();
                    return;
                }

                //- ErrCode401以外は、サーバ接続エラー
                if (userDlg.getStatusCode() == 401) {
                    showUserIdPasswordError();
                } else {
                    showConnectionError();
                }
            }
        });
        login.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {

                //- ログイン試行回数を超えたら強制終了(ロックアウト)
                if (tryCount >= maxTryCount) {
                    showTryOutError();
                    return;
                }

                //- ErrCode401以外は、サーバ接続エラー
                if (userDlg.getStatusCode() == 401) {
                    showUserIdPasswordError();
                } else {
                    showConnectionError();
                }
            }
        });
        new Thread(login).start();
    }

    /**
     * コントロールボタンのロック
     */
    public void lockOperation() {
        //- ボタン有効
        buttonAbout.setDisable(true);
        exitButton.setDisable(true);
        configButton.setDisable(true);
        loginButton.setDisable(true);
        //- ログインID,Pass有効
        userId.setDisable(true);
        pwd.setDisable(true);
        //- プログレス非表示
        progressIndi.setVisible(true);
    }

    /**
     * コントロールボタンのアンロック
     */
    public void unLockOperation() {
        //- ボタン有効
        buttonAbout.setDisable(false);
        exitButton.setDisable(false);
        configButton.setDisable(false);
        loginButton.setDisable(false);
        //- ログインID,Pass有効
        userId.setDisable(false);
        pwd.setDisable(false);
        //- プログレス表示
        progressIndi.setVisible(false);
    }

    /**
     * ログイン試行上限エラー
     *
     * @return
     */
    public Task<Boolean> taskShowTryOutError() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                Boolean result = Boolean.TRUE;
                String err = "ログイン試行回数の上限を超えました";
                status.setFill(Color.RED);
                status.setText(err);
                ClientContext.getPart11Logger().error(err);
                BlinkText bt = new BlinkText(status);
                bt.run();
                sleep(3000);
                return result;
            }
        };
    }

    public void showTryOutError() {
        final Task<Boolean> listner = taskShowTryOutError();
        listner.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                System.exit(0);
            }
        });
        new Thread(listner).start();
    }

    /**
     * ログインエラー表示
     *
     * @return
     */
    public Task<Boolean> taskShowUserIdPasswordError() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                String err = "ログインID、またはパスワードが違います";
                status.setFill(Color.RED);
                status.setText(err);
                ClientContext.getPart11Logger().warn(err);
                BlinkText bt = new BlinkText(status);
                bt.run();
                sleep(3000);
                return true;
            }
        };
    }

    public void showUserIdPasswordError() {
        final Task<Boolean> listner = taskShowUserIdPasswordError();
        listner.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                unLockOperation();
                userIdForcus();
            }
        });
        new Thread(listner).start();
    }

    /**
     * サーバ接続エラー表示
     *
     * @return
     */
    public Task<Boolean> taskShowConnectionError() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                String err = "サーバへの接続に失敗しました";
                status.setFill(Color.RED);
                status.setText(err);
                ClientContext.getPart11Logger().error(err);
                BlinkText bt = new BlinkText(status);
                bt.run();
                sleep(3000);
                return true;
            }
        };
    }

    public void showConnectionError() {
        final Task<Boolean> listner = taskShowConnectionError();
        listner.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                unLockOperation();
                userIdForcus();
            }
        });
        new Thread(listner).start();
    }

    /**
     * USERID NextForcus
     *
     * @param ke
     */
    public void userIdOnKey(KeyEvent ke) {

        if (ke.getCode().equals(KeyCode.ENTER)) {
            pwd.requestFocus();
        }
    }

    /**
     * Passord NextAction
     *
     * @param ke
     */
    public void pwdOnKey(KeyEvent ke) {

        if (ke.getCode().equals(KeyCode.ENTER)) {
            loginButton.requestFocus();
        }
    }

    /**
     * UserID TextField Forcus
     */
    public void userIdForcus() {
        userId.requestFocus();
    }

    /**
     * 認証が成功したかどうかを返す
     *
     * @return true 認証が成功した場合
     */
    public ILoginDialog.LoginStatus getResult() {
        return result;
    }

    /**
     * APP説明 呼び出し
     *
     * @param event
     * @throws IOException
     * @throws Exception
     */
    public void execAbout(ActionEvent event) throws IOException, Exception {
        application.aboutDisplay();
    }

    /**
     * 設定画面 呼び出し
     *
     * @param event
     * @throws IOException
     * @throws Exception
     */
    public void execConfig(ActionEvent event) throws IOException, Exception {
        application.configDisplay();
        userId.setText(Project.getProjectStub().getUserId());
    }
    
    public void setServerBuild(String txt) {
        svVer.setText(txt);
    }
    
    public void setClientBuild(String txt) {
        clVer.setText(txt);
    }
}
