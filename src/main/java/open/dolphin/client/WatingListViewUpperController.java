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
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;
import open.dolphin.project.Project;

/**
 * WatingListView Upper Controller
 * @author Chikara Fujihira <fujihirach@sandi.co.jp>
 */
public class WatingListViewUpperController implements Initializable {

    @FXML
    Button btn;
    @FXML
    Text nowTime;
    @FXML
    Text comePvt;
    @FXML
    Text waitPvt;
    @FXML
    Text waitTime;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btn.setTooltip(new Tooltip("患者受付一覧を更新します"));
        //- Init karteView
        Project.setString("karte.view", "false");
        Project.setString("karte.tool", "false");
    }
    
    public Button getBtn(){
        return btn;
    }
    
    public Text getTextNowTime(){
        return nowTime;
    }

    public void setTextNowTime(String msg){
        this.nowTime.setText(msg);
    }
    
    public Text getTextComePvt(){
        return comePvt;
    }

    public void setTextComePvt(String msg){
        this.comePvt.setText(msg);
    }
    
    public Text getTextWaitPvt(){
        return waitPvt;
    }

    public void setTextWaitPvt(String msg){
        this.waitPvt.setText(msg);
    }

    public Text getTextWaitTime(){
        return waitTime;
    }
    
    public void setTextWaitTime(String msg){
        this.waitTime.setText(msg);
    }
}
