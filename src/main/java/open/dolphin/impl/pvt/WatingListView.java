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
package open.dolphin.impl.pvt;

import java.awt.BorderLayout;
import java.io.IOException;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import open.dolphin.client.WatingListViewUpperController;

/**
 * WatingListView (For JavaFX)
 *
 * @author masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class WatingListView extends JFXPanel {

    private final JButton kutuBtn;
    private final JLabel pvtInfoLbl;
    private final RowTipsTable pvtTable;

    private final JFXPanel IconDesc;

    private final JFXPanel panel;
    public WatingListViewUpperController wtlvCtrl;

    public WatingListView() {
        kutuBtn = new JButton();
        pvtInfoLbl = new JLabel();
        panel = new JFXPanel();
        pvtTable = new RowTipsTable();
        IconDesc = new JFXPanel();
        initResouce();
    }

    public void initFxResouce() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/fxml/WatingListViewUpper.fxml"));
            Parent root = (Parent) loader.load();
            wtlvCtrl = (WatingListViewUpperController) loader.getController();
            IconDesc.setScene(new Scene(root, 450, 65));

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public void initResouce() {
        JScrollPane scroll = new JScrollPane(pvtTable);
        panel.add(IconDesc);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        setLayout(new BorderLayout(0, 0));
        add(panel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public JButton getKutuBtn() {
        return kutuBtn;
    }

    public JTable getTable() {
        return pvtTable;
    }

    public JLabel getPvtInfoLbl() {
        return pvtInfoLbl;
    }

    /**
     * 現在時刻表示 TextObjectの取得
     *
     * @return
     */
    public Text getTextNowTime() {
        return wtlvCtrl.getTextNowTime();
    }

    /**
     * 現在時刻表示 TextObjectへ入力
     *
     * @param msg
     */
    public void setTextNowTime(String msg) {
        wtlvCtrl.setTextNowTime(msg);
    }

    /**
     * 来院数表示 TextObjectの取得
     *
     * @return
     */
    public Text getTextComePvt() {
        return wtlvCtrl.getTextComePvt();
    }

    /**
     * 来院数表示 TextObjectへ入力
     *
     * @param msg
     */
    public void setTextComePvt(String msg) {
        wtlvCtrl.setTextComePvt(msg);
    }

    /**
     * 患者待ち数 TextObjectの取得
     *
     * @return
     */
    public Text getTextWaitPvt() {
        return wtlvCtrl.getTextWaitPvt();
    }

    /**
     * 患者待ち数 TextObjectへ入力
     *
     * @param msg
     */
    public void setTextWaitPvt(String msg) {
        wtlvCtrl.setTextWaitPvt(msg);
    }

    /**
     * 待ち時間　TextObjectの取得
     *
     * @return
     */
    public Text getTextWaitTime() {
        return wtlvCtrl.getTextWaitTime();
    }

    /**
     * 待ち時間　TextObjectへ入力
     *
     * @param msg
     */
    public void setTextWaitTime(String msg) {
        wtlvCtrl.setTextWaitTime(msg);
    }

    /**
     * 一覧更新ボタンオブジェクト取得
     *
     * @return
     */
    public Button getBtn() {
        return wtlvCtrl.getBtn();
    }
}
