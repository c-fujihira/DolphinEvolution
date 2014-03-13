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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import open.dolphin.util.PropatyGetter;

/**
 * About FXML Controller class
 * @author Chikara Fujihira <fujihirach@sandi.co.jp>
 */
public class AboutController extends AnchorPane implements Initializable {

    @FXML
    TextArea comment;
    @FXML
    Text productName;
    @FXML
    Text companyName;

    //- 親インスタンス
    private Stage mainStage;
    private Evolution application;
    
    private String outText;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        comment.setText("");
        outText = "";
        
        PropatyGetter prpGt = new PropatyGetter();
        productName.setText(prpGt.get("product.name"));
        companyName.setText(prpGt.get("product.companyname"));

        this.setComment();
    }

    public void setApp(Evolution application){
        this.application = application;
    }

    public void setStage(Stage mainStage) {
        this.mainStage = mainStage;
    }
    
    public void setComment() {

        try {
            Class c = this.getClass();
            URL url = c.getResource("/resources/docs/about.txt");
            try (InputStream is = url.openStream(); 
                    BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
                while (br.ready()) {
                    String addString = br.readLine();
                    addString = addString.replace("%JRE_VERSION%", System.getProperty("java.version"));
                    addString = addString.replace("%PRODUCT_VERSION%", ClientContext.getProductName() + " " + ClientContext.getVersion());
                    outText += addString + "\n";
                }
            }
        } catch (FileNotFoundException e) {
            Logger.getLogger(AboutController.class.getName()).
                    log(Level.SEVERE, "Load Error.", e);
        } catch (IOException e) {
            Logger.getLogger(AboutController.class.getName()).
                    log(Level.SEVERE, "Load Error.", e);
        }
        comment.setText(outText);
    }

    public void close(ActionEvent event) {
        this.application.Dialog.close();
    }
}
