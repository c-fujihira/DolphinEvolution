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

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * ConnectionTest FXML Controller Class
 * @author Chikara Fujihira <fujihirach@sandi.co.jp>
 */
public class ConnectionTestController extends AnchorPane implements Initializable {

    @FXML
    TextArea comment;

    //- 親インスタンス
    private Stage mainStage;
    private Evolution application;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setApp(Evolution application){
        this.application = application;
    }

    public void setStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
    
    public void run() {
    }
    
    public void close() {
        this.application.Dialog.close();
    }
}
