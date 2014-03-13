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

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import open.dolphin.project.Project;
import open.dolphin.project.ProjectStub;
import open.dolphin.util.EvolutionWindow;

/**
 * Dolphin Evolution Main Class
 *
 * @author Chikara Fujihira <fujihirach@sandi.co.jp>
 */
public class Evolution extends Application {

    public Stage stage;
    public Scene scene;
    public Stage Dialog;

    //- メインウィンドウ
    public EvolutionWindow evoWindow;
    public Evolution application;

    //- 組み込みコントローラー
    public ControlPanelController mainWinCtrl;
    public DisplayCtrlController dispWinCtrl;

    //- インスタンス生成
    private static final Evolution instance = new Evolution();

    //- 排他処理用UUID
    private static String clientUUID;
    private LoginController ctrl;
    private MultiUserLoginController mltUserCtrl;
    private MultiFacilityLoginController mltFaciCtrl;
    private MultiFaciUserLoginController mltFaciUserCtrl;

    public void Evolution() {
    }

    @Override
    public void start(Stage stage) throws Exception {

        try {

            //- 排他処理用UUID生成
            clientUUID = UUID.randomUUID().toString();

            //- ClientContext生成
            ClientContextStub stub = new ClientContextStub();
            ClientContext.setClientContextStub(stub);

            //- Mac対応
            macListener();

            //- プロジェクトスタブ生成
            Project.setProjectStub(new ProjectStub());

            //- Default設定読み込み
            Project.getUserDefaults();

            //- 一時設定保存
            Project.saveUserDefaults();

            // Project作成後、Look&Feel を設定する
            stub.setupUI();

            //- ログイン画面生成
            this.stage = stage;
            String title = ClientContext.getProductName() + " " + ClientContext.getVersion();

            //- ログインディスプレイ呼出
            stage.setTitle(title);
            loginDisplay();

            //- サイズ変更しない
            stage.setResizable(false);
            stage.show();

        } catch (Exception ex) {
            Logger.getLogger(Evolution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     *
     * @param args the command line arguments
     */
    public void main(String[] args) {
        Application.launch(Evolution.class, (java.lang.String[]) null);
    }

    private Initializable replaceSceneContent(String fxml, int x, int y) throws Exception {

        FXMLLoader loader = new FXMLLoader();

        try {
            AnchorPane page;
            InputStream in = Evolution.class.getResourceAsStream(fxml);
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(Evolution.class.getResource(fxml));
            page = (AnchorPane) loader.load(in);
            scene = new Scene(page, x, y);
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (IOException ex) {
            Logger.getLogger(Evolution.class.getName()).log(Level.SEVERE, null, ex);
        }

        return (Initializable) loader.getController();
    }

    public void macListener() {

        //- Mac 対応
        if (ClientContext.isMac()) {

            com.apple.eawt.Application fApplication = com.apple.eawt.Application.getApplication();

            //- About
            fApplication.setAboutHandler(new com.apple.eawt.AboutHandler() {
                @Override
                public void handleAbout(com.apple.eawt.AppEvent.AboutEvent ae) {
                    System.out.println("about");
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            aboutDisplay();
                        }
                    });
                }
            });

            //- Preference
            fApplication.setPreferencesHandler(new com.apple.eawt.PreferencesHandler() {
                @Override
                public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent pe) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("preference");
                            configDisplay();
                        }
                    });
                }
            });

            //- Quit
            fApplication.setQuitHandler(new com.apple.eawt.QuitHandler() {
                @Override
                public void handleQuitRequestWith(com.apple.eawt.AppEvent.QuitEvent qe, com.apple.eawt.QuitResponse qr) {
                    qr.cancelQuit();
                    if (evoWindow == null || !evoWindow.getFrame().isVisible()) {
                        System.out.println("from mac handle evo exit.");
                        System.exit(0);
                    }
                }
            });
        }
    }

    /**
     * Login画面 呼び出し
     */
    public void loginDisplay() {

        //- Mac用 Menu追加
        macListener();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

        try {
            //- リセットの為、一旦Hide
            stage.hide();

            ProjectStub projectStub = Project.getProjectStub();
            String loginSet = projectStub.getString("login.set", null);

            if (loginSet != null && loginSet.equals("multi-facility-user")) {
                if(mltFaciUserCtrl == null){
                    mltFaciUserCtrl = (MultiFaciUserLoginController) replaceSceneContent("/resources/fxml/Login-multi-faciUser.fxml", 630, 325);
                }
                //- UserId へフォーカス
                mltFaciUserCtrl.userIdForcus();
                mltFaciUserCtrl.setStage(this.stage);
                mltFaciUserCtrl.setApp(this);
            } else if (loginSet != null && loginSet.equals("multi-facility")) {
                if(mltFaciCtrl == null){
                    mltFaciCtrl = (MultiFacilityLoginController) replaceSceneContent("/resources/fxml/Login-multi-facility.fxml", 630, 325);
                }
                //- UserId へフォーカス
                mltFaciCtrl.userIdForcus();
                mltFaciCtrl.setStage(this.stage);
                mltFaciCtrl.setApp(this);
            } else if (loginSet != null && loginSet.equals("multi-user")) {
                if(mltUserCtrl == null){
                    mltUserCtrl = (MultiUserLoginController) replaceSceneContent("/resources/fxml/Login-multi-user.fxml", 630, 325);
                }
                //- UserId へフォーカス
                mltUserCtrl.userIdForcus();
                mltUserCtrl.setStage(this.stage);
                mltUserCtrl.setApp(this);
            } else {
                if(ctrl == null){
                    ctrl = (LoginController) replaceSceneContent("/resources/fxml/Login.fxml", 630, 325);
                }
                //- UserId へフォーカス
                ctrl.userIdForcus();
                ctrl.setStage(this.stage);
                ctrl.setApp(this);
            }
            //- サイズ変更不可
            stage.setResizable(false);
            this.stage.setTitle(ClientContext.getProductName() + " " + ClientContext.getVersion() + " - ログイン");
            stage.show();
        } catch (Exception ex) {
            Logger.getLogger(Evolution.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * AboutDisplay呼出
     */
    public void aboutDisplay() {

        //- ダイアログ表示
        String fxml = "/resources/fxml/About.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Evolution.class.getResource(fxml));

        try {
            loader.load();

        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).
                    log(Level.SEVERE, "Template Error.", ex);
        }

        Parent root = loader.getRoot();
        AboutController ctrl;
        ctrl = loader.getController();

        //- サイズ変更不可
        Dialog = new Stage();
        Dialog.setResizable(false);
        Dialog.initModality(Modality.APPLICATION_MODAL);
        Dialog.setScene(new Scene(root));
        Dialog.setTitle(ClientContext.getProductName() + " " + ClientContext.getVersion() + "  について");

        ctrl.setApp(this);
        ctrl.setStage(Dialog);

        //- ダイアログ・クローズまで待つ
        Dialog.showAndWait();

        //- リソース解放
        if (Dialog.isShowing()) {
            Dialog.close();
        }
        Dialog = null;
    }

    /**
     * 設定画面 呼び出し
     */
    public void configDisplay() {

        try {
            //- ダイアログ表示
            String fxml = "/resources/fxml/Config.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(Evolution.class.getResource(fxml));
            loader.load();
            Parent root = loader.getRoot();
            ConfigController ctrl;
            ctrl = loader.getController();

            //- サイズ変更不可
            Dialog = new Stage();
            Dialog.setResizable(false);
            Dialog.initModality(Modality.APPLICATION_MODAL);
            Dialog.setScene(new Scene(root));
            Dialog.setTitle(ClientContext.getProductName() + " " + ClientContext.getVersion() + " - 設定");

            ctrl.setApp(this);
            ctrl.setStage(Dialog);

            //- ダイアログ・クローズまで待つ
            Dialog.showAndWait();

            //- リソース解放
            if (Dialog.isShowing()) {
                Dialog.close();
            }
            Dialog = null;

        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).
                    log(Level.SEVERE, "Template Error.", ex);
        }
    }

    /**
     * MainWindow呼び出し
     */
    public void mainDisplay() {

        //- 認証処理後、いったんhide
        stage.hide();
        ClientContext.getBootLogger().debug("main window create.");
        evoWindow = new EvolutionWindow();
        evoWindow.setApp(Evolution.this);
        evoWindow.tryLoadStampTree();
        evoWindow.setScene(scene);
        evoWindow.setStage(stage);
        evoWindow.initComponents();

        //- Window設定
        try {
            ClientContext.getBootLogger().debug("main window load FX resources.");
            FXMLLoader ctrlPanelLoader = new FXMLLoader(getClass().getResource("/resources/fxml/ControlPanel.fxml"));
            Parent ctrlRoot = (Parent) ctrlPanelLoader.load();

            FXMLLoader dispPanelLoader = new FXMLLoader(getClass().getResource("/resources/fxml/DisplayCtrl.fxml"));
            Parent dispRoot = (Parent) dispPanelLoader.load();

            mainWinCtrl = (ControlPanelController) ctrlPanelLoader.getController();
            mainWinCtrl.setMainWindow(evoWindow);
            mainWinCtrl.setApp(Evolution.this);
            dispWinCtrl = (DisplayCtrlController) dispPanelLoader.getController();
            dispWinCtrl.setApp(Evolution.this);
            evoWindow.getJfxPanel1().setScene(new Scene(ctrlRoot, 410, 425));
            evoWindow.getJfxPanel2().setScene(new Scene(dispRoot, 900, 40));
            evoWindow.setWinCtrlMacro();
        } catch (IOException ex) {
            Logger.getLogger(Evolution.class.getName()).log(Level.SEVERE, null, ex);
        }
        ClientContext.getBootLogger().debug("main window repaint.");
        evoWindow.windowMaximum();
        dispWinCtrl.setWinCtrlReset();
        evoWindow.getFrame().setVisible(true);
        evoWindow.reFleshJFrame0();
        evoWindow.reFleshJFrame1();
        evoWindow.reFleshJFrame2();
        evoWindow.reFleshJFrame3();
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * getInstance
     *
     * @return instance
     */
    public static Evolution getInstance() {
        return instance;
    }

    /**
     * getClientUUID
     *
     * @return clientUUID
     */
    public String getClientUUID() {
        return clientUUID;
    }

}
