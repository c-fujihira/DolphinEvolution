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

import java.awt.Color;

/**
 *
 * @author Kazushi Minagawa.
 */
public class GUIConst {
    
    //
    // client package で使用する定数
    //
    // メニュー関連
    public static final String SHOW_SCHEMA_BOX = "showSchemaBox";
    public static final String MENU_KARTE = "カルテ";
    public static final String MENU_TEXT = "テキスト";
    public static final String MENU_SCHEMA = "シェーマ";
    public static final String MENU_STAMP = "スタンプ";
    public static final String MENU_INSERT = "挿 入";
    public static final String MENU_INSURANCE = "保険選択";

    public static final String ACTION_NEW_KARTE = "newKarte";
    public static final String ACTION_NEW_DOCUMENT = "newDocument";
    public static final String ACTION_OPEN_KARTE = "openKarte";
    public static final String ACTION_CLOSE = "close";
    public static final String ACTION_SAVE = "save";
    public static final String ACTION_DELETE = "delete";
    public static final String ACTION_PRINTER_SETUP = "printerSetup";
    public static final String ACTION_PRINT = "print";
    public static final String ACTION_PROCESS_EXIT = "processExit";
   
    public static final String ACTION_MODIFY_KARTE = "modifyKarte";
    public static final String ACTION_UNDO = "undo";
    public static final String ACTION_REDO = "redo";
    public static final String ACTION_CUT = "cut";
    public static final String ACTION_COPY = "copy";
    public static final String ACTION_PASTE = "paste";

// 元町皮ふ科^
    public static final String ACTION_SEND_CLAIM = "sendClaim";
    
// Hiro Clinic^ 処方箋印刷
    public static final String ACTION_CREATE_PRISCRIPTION = "createPrescription";
    
//masuda^
    public static final String ACTION_CHECK_INTERACTION = "checkInteraction";

    public static final String ACTION_ASCENDING = "ascending";
    public static final String ACTION_DESCENDING = "descending";
    public static final String ACTION_SHOW_MODIFIED = "showModified";
    public static final String ACTION_SET_KARTE_ENVIROMENT = "setKarteEnviroment";
    
//minagawa LookAndFeeL^
    public static final String ACTION_NIMBUS_LOOK_AND_FEEL = "nimbusLookAndFeel";
    public static final String ACTION_NATIVE_LOOK_AND_FEEL = "nativeLookAndFeel";
//minagawa$    
    
    public static final String ACTION_INSERT_DISEASE = "insertDisease";
    public static final String ACTION_INSERT_TEXT = "insertText";
    public static final String ACTION_INSERT_SCHEMA = "insertSchema";
    public static final String ACTION_ATTACHMENT = "attachment";
    public static final String ACTION_INSERT_STAMP = "insertStamp";
    public static final String ACTION_SELECT_INSURANCE = "selectInsurance";
    public static final String ACTION_CHANGE_NUM_OF_DATES_ALL = "changeNumOfDatesAll";
//s.oh^ テキストの挿入 2013/08/12
    public static final String ACTION_SOAPANE_INSERTTEXT_DIR = "soapane.inserttext.dir";
    public static final String ACTION_PPANE_INSERTTEXT_DIR = "ppane.inserttext.dir";
//s.oh$
    public static final String ACTION_OTHERPROCESS_ICON = "otherprocessicon.link";

    
    public static final String ACTION_SIZE = "size";
    public static final String ACTION_FONT_LARGER = "fontLarger";
    public static final String ACTION_FONT_SMALLER = "fontSmaller";
    public static final String ACTION_FONT_STANDARD = "fontStandard";
    
    public static final String ACTION_STYLE = "style";
    public static final String ACTION_FONT_BOLD = "fontBold";
    public static final String ACTION_FONT_ITALIC = "fontItalic";
    public static final String ACTION_FONT_UNDERLINE = "fontUnderline";
    
    public static final String ACTION_JUSTIFY = "justify";
    public static final String ACTION_LEFT_JUSTIFY = "leftJustify";
    public static final String ACTION_CENTER_JUSTIFY = "centerJustify";
    public static final String ACTION_RIGHT_JUSTIFY = "rightJustify";
    
    public static final String ACTION_COLOR = "color";
    public static final String ACTION_FONT_RED = "fontRed";
    public static final String ACTION_FONT_ORANGE = "fontOrange";
    public static final String ACTION_FONT_YELLOW = "fontYellow";
    public static final String ACTION_FONT_GREEN = "fontGreen";
    public static final String ACTION_FONT_BLUE = "fontBlue";
    public static final String ACTION_FONT_PURPLE = "fontPurple";
    public static final String ACTION_FONT_GRAY = "fontGray";
    public static final String ACTION_FONT_BLACK = "fontBlack";
    
    public static final String ACTION_RESET_STYLE = "resetStyle";
    
    public static final String ACTION_SHOW_STAMPBOX = "showStampBox";
    public static final String ACTION_NEW_PATIENT = "addNewPatient";
    public static final String ACTION_SHOW_SCHEMABOX = "showSchemaBox";
    public static final String ACTION_CHANGE_PASSWORD = "changePassword";
    public static final String ACTION_EDIT_FACILITY_INFO = "editFacilityInfo";
    public static final String ACTION_ADD_USER = "addUser";
//minagaw^ 保険医療機関コード読み込み
    public static final String ACTION_FETCH_FACILITY_CODE = "fetchFacilityCode";
    public static final String ACTION_CONFIRM_RUN = "confirmRun";
    public static final String ACTION_SOFTWARE_UPDATE = "update1";

    public static final String ACTION_BROWS_DOLPHIN = "browseDolphinSupport";
    public static final String ACTION_BROWS_DOLPHIN_PROJECT = "browseDolphinProject";
    public static final String ACTION_BROWS_MEDXML = "browseMedXml";
    public static final String ACTION_SHOW_ABOUT = "showAbout";
    
    // Role
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    
    // Icon
    public static final String ICON_ATTACHMENT = "paperclip.png";
    public static final String ICON_ATTACHMENT_SMALL = "paperclip_small.png";
    public static final String ICON_TEMP_SAVE_SMALL = "tack_small.png";
    
    // Color
    // (予定カルテ対応)
    //public static final Color TEMP_SAVE_KARTE_COLOR = new Color(0,0,153);   // Drak blue
    public static final Color TEMP_SAVE_KARTE_BK_COLOR = new Color(0,0,153);   // Drak blue
    public static final Color TEMP_SAVE_KARTE_FORE_COLOR = Color.white;         // white
    public static final Color SCHEDULE_KARTE_BK_COLOR = new Color(250, 191, 19);        // 250 G：191 B：19
    public static final Color SCHEDULE_KARTE_FORE_COLOR = new Color(0, 0, 0);        // 0, 85, 162
    
    // Stamp Holder
    public static final Color STAMP_HOLDER_FOREGROUND = new Color(20, 20, 140);
    public static final Color STAMP_HOLDER_BACKGROUND = new Color(0, 0, 0, 0); // 透明
    public static final Color STAMP_HOLDER_SELECTED_BORDER = new Color(255, 0, 153);
    public static final Color STAMP_HOLDER_NON_SELECTED_BORDER = new Color(0, 0, 0,0); // 透明
    
    // Table 奇数偶数カラー
    // JTableレンダラ用の男性カラー  ClientContext.getColor("watingList.color.male")
    public static final Color TABLE_MALE_COLOR = new Color(237,243,254);
    
    // JTableレンダラ用の女性カラー ClientContext.getColor("watingList.color.female")
    public static final Color TABLE_FEMALE_COLOR = new Color(254,221,242);
    
    // JTableレンダラ用の奇数カラー
    public static final Color TABLE_ODD_COLOR = ClientContext.getColor("color.odd");
    
    // JTableレンダラ用の偶数カラー 
    public static final Color TABLE_EVEN_COLOR = ClientContext.getColor("color.even");
    
    
//    // 環境設定後のサービス開始停止関連
    public static final String PVT_SERVER_IS_RUNNING = "runtime.pvtServer";
//    public static final String BIND_ADDRESS_PVT_SERVER = "runtime.pvtServerBindAddress";
    public static final String SEND_CLAIM_IS_RUNNING = "runtime.sendClaim";
//    public static final String ADDRESS_CLAIM = "runtime.claimAddress";
//    
//    // 受付リレー
    public static final String PVT_RELAY_IS_RUNNING = "runtime.pvt.relay";
//    public static final String PVT_RELAY_DIRECTORY = "runtime.pvt.relay.directory";
//    public static final String PVT_RELAY_ENCODING = "runtime.pvt.relay.encoding";
//    
//    // MML出力
    public static final String SEND_MML_IS_RUNNING = "runtime.sendMml";
//    public static final String CSGW_PATH = "runtime.csgwPath";
    
}
