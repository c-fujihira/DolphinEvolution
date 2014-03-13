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
package open.dolphin.client;

import java.awt.Window;
import javax.swing.*;

/**
 * SaveDialog
 * (予定カルテ対応)
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 */
public abstract class AbstractSaveDialog {
    
    protected static final String[] PRINT_COUNT 
            = {"0", "1",  "2",  "3",  "4", "5"};
    protected static final String[] TITLE_LIST = {"経過記録", "予定", "処方", "処置", "検査", "画像", "指導"};
    protected static final String TITLE = "ドキュメント保存";
    protected static final String TMP_SAVE = "仮保存";
    
    // 親Window
    protected Window parent;
    
    // ダイアログ
    protected JDialog dialog;
    
    // キャンセルボタン
    protected JButton cancelButton;
    
    // 仮保存ボタン
    protected JButton tmpButton;
    
    // 文書タイトル
    protected JTextField titleField;
    protected JComboBox titleCombo;
    
    // 印刷枚数Combo
    protected JComboBox printCombo;
    
    // 診療科を表示するラベル
    protected JLabel departmentLabel;
    
    // CLAIM 送信 ChckBox
    protected JCheckBox sendClaim;
    
    // CLAIM送信Action
    protected AbstractAction sendClaimAction;
    
    // 戻り値のSaveParams
    protected SaveParamsM value; 

    // 入力値のSaveParams
    protected SaveParamsM enterParams;
    
    public void setWindowParent(Window parent) {
        this.parent = parent;
    }
    
    public void start() {
        dialog.setVisible(true);
    }
    
    public SaveParamsM getValue() {
        return value;
    }
    
    public abstract void setValue(SaveParamsM params);
}