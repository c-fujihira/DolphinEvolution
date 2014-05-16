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
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import open.dolphin.project.Project;
import open.dolphin.util.EvolutionWindow;

/**
 * FXML Controller class
 *
 * @author Chikara Fujihira <fujihirach@sandi.co.jp>
 */
public class ControlPanelController implements Initializable {

    //- 親インスタンス
    private Stage mainStage;
    private Evolution application;
    private EvolutionWindow mainWindow;

    @FXML
    WebView webView;

    @FXML
    Text evoDisp;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        getWebResponce();
    }

    public void setApp(Evolution application) {
        this.application = application;
    }

    public Evolution getApplication() {
        return application;
    }

    public void setMainWindow(EvolutionWindow window) {
        this.mainWindow = window;
    }

    public EvolutionWindow getMainWindow() {
        return mainWindow;
    }

    public void setStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public Stage getStage() {
        return mainStage;
    }

    //- ページ設定
    public void execPrinterSetup() {
        mainWindow.printerSetup();
    }

    //- 環境設定
    public void execPreference() {
        mainWindow.doPreference();
    }

    //- プロフィール変更
    public void execChangePassword() {
        mainWindow.changePassword();
    }

    //- 施設情報編集
    public void execEditFacilityInfo() {
        mainWindow.editFacilityInfo();
    }

    //- 保険医療機関コード読込
    public void execFetchFacilityCode() {
        mainWindow.fetchFacilityCode();
    }

    //- 情報
    public void execShowAbout() {
        mainWindow.showAbout();
    }

    //- 監査対応用カルテ履歴一覧PDF出力
    public void execPatientInfoOutput() {
        mainWindow.patientInfoOutput();
    }

    //- 終了
    public void execExit() {
        mainWindow.processExit(1);
    }

    //- Web画面取得
    public void getWebResponce() {
        //java.net.CookieHandler.setDefault(null);
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);
        Map<String, List<String>> headers = new LinkedHashMap<>();
        URI uri = URI.create(Project.getEvoUrl());
        headers.put("Set-Cookie", Arrays.asList("name=value"));
        try {        
            java.net.CookieHandler.getDefault().put(uri, headers);
        } catch (IOException ex) {
            Logger.getLogger(ControlPanelController.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setProperty("https.protocols", "SSLv3,SSLv2Hello");
        System.setProperty("jsse.enableSNIExtension", "false");
        webView.getEngine().load(Project.getEvoUrl());
        evoDisp.setText(Project.getEvoDisp());
    }
}
