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
package open.dolphin.project;

import java.awt.Color;
import java.awt.Rectangle;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.util.Enumeration;
import java.util.Properties;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.exception.DolphinException;
import open.dolphin.infomodel.UserModel;

/**
 * プロジェクト情報管理クラス
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class ProjectStub implements java.io.Serializable {

    // デフォルトのプロジェクト名
    private final String DEFAULT_PROJECT_NAME = "DolphinEvolution";

    // OpenDolphin のデフォルト施設ID
    private final String DEFAULT_FACILITY_ID = "1.3.6.1.4.1.9414.70.1";

    // REST context
    private final String REST_BASE_RESOURCE = "/openDolphin/resources";

    // 有効な設定ファイルかどうか
    private boolean valid;

    // ログインユーザー
    private UserModel userModel;

    // ユーザー設定
    private Properties userDefaults;

    // デフォルトプロパティ値
    private Properties applicationDefaults;

    // ユーザー設定を上書きするプロパティ
    private Properties customDefaults;

    // REST baseURI = ServerURI + REST_BASE_RESOURCE
    private String baseURI;

    // ヒロクリニック 医療機関基本情報(ORCA登録)
    private String basicInfo;

    /**
     * ProjectStub を生成する。
     */
    public ProjectStub() {

        try {
            // デフォルトプロパティを読み込む
            InputStream in = ClientContext.getResourceAsStream("defaults.properties");
            try (BufferedInputStream bin = new BufferedInputStream(in)) {
                applicationDefaults = new Properties();
                applicationDefaults.load(bin);
                bin.close();
                in.close();
            }

            // User Default を生成する
            userDefaults = applicationDefaults;

            // サーバ接続に強制
            applicationDefaults.put("claim.sender", "server");

            // 設定ファイルを読み込む(旧設定フォルダがあれば使用する)
            File parent = getSettingDirectory();
            File target = new File(parent, "user-defaults.properties");
            if (target.exists()) {
                loadProperties(userDefaults, "user-defaults.properties");
            }

            //-----------------------------------------
            // 上書き設定を読み込む: custom.properties
            //-----------------------------------------
            StringBuilder sb = new StringBuilder();
            sb.append(ClientContext.getSettingDirectory());
            sb.append(File.separator);
            sb.append("custom.properties");
            File f = new File(sb.toString());
            if (f.exists()) {
                FileInputStream fin = new FileInputStream(f);
                InputStreamReader inr = new InputStreamReader(fin, "JISAutoDetect");
                BufferedReader br = new BufferedReader(inr);
                customDefaults = new Properties();
                customDefaults.load(br);
                br.close();

                // UserDefaultへ設定する
                Enumeration e = customDefaults.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    String val = customDefaults.getProperty(key);
                    userDefaults.setProperty(key, val);
//s.oh^ 文字コード対応
                    String keyTmp = key;
                    byte[] bt = keyTmp.getBytes();
                    int idx = -1;
                    for (int i = 0; i < bt.length; i++) {
                        if (bt[i] < 0) {
                            idx = i;
                        } else {
                            break;
                        }
                    }
                    if (idx >= 0) {
                        key = new String(bt, idx + 1, bt.length - (idx + 1));
                        customDefaults.put(key, val);
                        userDefaults.setProperty(key, val);
                    }
//s.oh$
                }
            }

        } catch (IOException e) {
            e.printStackTrace(System.err);
        }

    }

    /**
     * 設定ファイルが有効かどうかを返す。
     *
     * @return 有効な時 true
     */
    public boolean isValid() {
        boolean ok = true;
        ok = ok && (getFacilityId() != null);
        ok = ok && (getUserId() != null);
        ok = ok && (getBaseURI() != null);
        valid = ok;
        return valid;
    }

    /**
     * プロジェクト名を取得する
     *
     * @return
     */
    public String getName() {
        return getString(Project.PROJECT_NAME, DEFAULT_PROJECT_NAME);
    }

    /**
     * プロジェクト名を設定する
     *
     * @param projectName
     */
    public void setName(String projectName) {
        setString(Project.PROJECT_NAME, projectName);
    }

    /**
     * ログインユーザ情報を取得する
     *
     * @return Dolphinサーバに登録されているユーザ情報
     */
    public UserModel getUserModel() {
        return userModel;
    }

    /**
     * ログインユーザ情報を設定する
     *
     * @param userModel ログイン時にDolphinサーバから取得した情報
     */
    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    //サーバ
    /**
     * ログイン画面用のFacilityIDを返す
     *
     * @return Stub FacilityID
     */
    public String getFacilityId() {
        return getString(Project.FACILITY_ID, DEFAULT_FACILITY_ID);
    }

    /**
     * ログイン画面用のFacilityIDを設定する
     *
     * @param facilityId
     *
     */
    public void setFacilityId(String facilityId) {
        setString(Project.FACILITY_ID, facilityId);
    }

    /**
     * ログイン画面用のUserIDを返す
     *
     * @return Stub userId
     */
    public String getUserId() {
        return getString(Project.USER_ID, null);
    }

    /**
     * ログイン画面用のUserIDを設定する
     *
     * @param userId
     */
    public void setUserId(String userId) {
        setString(Project.USER_ID, userId);
    }

    /**
     * 接続先URIを返す
     *
     * @return URI
     */
    public String getServerURI() {
        return getString(Project.SERVER_URI, null);
    }

    /**
     * 接続先URIを設定する
     *
     * @param uri
     */
    public void setServerURI(String uri) {
        setString(Project.SERVER_URI, uri);
        baseURI = null;
    }

    /**
     * REST の base URI を返す
     *
     * @return ServerURI + resource context
     */
    public String getBaseURI() {

        if (baseURI == null) {
            StringBuilder sb = new StringBuilder();
            String test = getServerURI();
            if (test != null) {
                if (test.endsWith("/")) {
                    int len = test.length();
                    test = test.substring(0, len - 1);
                }
                sb.append(test);
                sb.append(REST_BASE_RESOURCE);
                baseURI = sb.toString();
            }
        }
        return baseURI;
    }

    //レセコン
    //(ORCA)
    public String getmodel1() {
        return getString(Project.CLAIM_HOST_NAME);
    }

    /**
     * ログイン画面用のJPNCodeを設定する
     *
     * @param model1
     *
     */
    public void setmodel1(String model1) {
        setString(Project.CLAIM_HOST_NAME, model1);
    }

    /* ログイン画面用のSENDCLAIMを返す
     * @return Stub sendclaim
     */
    public String getsendClaim() {
        return getString(Project.SEND_CLAIM);
    }

    /**
     * ログイン画面用のSENDCLAIMを設定する
     *
     * @param sendClaim
     *
     */
    public void setsendClaim(String sendClaim) {
        setString(Project.SEND_CLAIM, sendClaim);
    }

    /* ログイン画面用のデフォルト01を返す
     * @return Stub code
     */
    public String getcode01() {
        return getString(Project.CLAIM_01);
    }

    /**
     * ログイン画面用のCodeを設定する
     *
     * @param bl
     *
     */
    public void setcode01(String bl) {
        setString(Project.CLAIM_01, bl);
    }
    /* ログイン画面用のJmaricodeを返す
     * @return Stub jmaricode
     */

    public String getJPNCode() {
        return getString(Project.JMARI_CODE);
    }

    /**
     * ログイン画面用のJPNCodeを設定する
     *
     * @param JPNCode *
     */
    public void setJPNCode(String JPNCode) {
        setString(Project.JMARI_CODE, JPNCode);
    }

    /**
     * Evolution システム連携名
     *
     * @return evoDisp
     */
    public String getEvoDisp() {
        return getString(Project.EVO_DISP);
    }

    /**
     * Evolution システム連携URL
     *
     * @return evoUrl
     */
    public String getEvoUrl() {
        return getString(Project.EVO_URL);
    }

    //カルテ/インスペクタ
    //プラットフォーム
     /* ログイン画面用のplatformを返す
     * @return Stub platform
     */
    public String getplatform() {
        return getString(Project.LOCATION_BY_PLATFORM);
    }

    /**
     * ログイン画面用のJPNCodeを設定する
     *
     * @param PLATFORM
     *
     */
    public void setplatform(String PLATFORM) {
        setString(Project.LOCATION_BY_PLATFORM, PLATFORM);
    }

    //シェーマエディタ
    /* ログイン画面用のを返す
     * @return Stub platform
     */
    public String geteditor() {
        return getString(Project.SCEMA_EDITOR);
    }

    /**
     * ログイン画面用のJPNCodeを設定する
     *
     * @param EDITOR
     *
     */
    public void seteditor(String EDITOR) {
        setString(Project.SCEMA_EDITOR, EDITOR);
    }

    //シェーマエディタ
             /* ログイン画面用のを返す
     * @return Stub platform
     */
    public String getcontainer() {
        return getString(Project.CONTAINER);
    }

    /**
     * ログイン画面用を設定する
     *
     * @param val
     */
    public void setcontainer(String val) {
        setString(Project.CONTAINER, val);
    }

    //カルテ/文書タブ
    public String getAscending() {
        return getString(Project.DOC_HISTORY_ASCENDING);
    }

    /**
     * ログイン画面用の昇順・降順を設定する
     *
     * @param ASCENDING
     */
    public void setAscending(String ASCENDING) {
        setString(Project.DOC_HISTORY_ASCENDING, ASCENDING);
    }

    //自動文書取得数
    public String getAutoMatic() {
        return getString(Project.DOC_HISTORY_FETCHCOUNT);
    }

    /**
     * ログイン画面用の昇順・降順を設定する
     *
     * @param AUTOMATIC
     */
    public void setAutoMatic(String AUTOMATIC) {
        setString(Project.DOC_HISTORY_FETCHCOUNT, AUTOMATIC);
    }

    //スクロール方向
    public String getScrolling() {
        return getString(Project.KARTE_SCROLL_DIRECTION);
    }

    /**
     * ログイン画面用のスクロール方向を設定する
     *
     * @param SCROLLING
     */
    public void setScrolling(String SCROLLING) {
        setString(Project.KARTE_SCROLL_DIRECTION, SCROLLING);
    }

    //抽出期間
    public String getTime() {
        return getString(Project.DOC_HISTORY_PERIOD);
    }

    /**
     * ログイン画面用のスクロール方向を設定する
     *
     * @param TIME
     */
    public void setTime(String TIME) {
        setString(Project.DOC_HISTORY_PERIOD, TIME);
    }

    //カルテ/傷病名
    //表示順
    public String getSequence() {
        return getString(Project.DIAGNOSIS_ASCENDING);
    }

    /**
     * ログイン画面用のスクロール方向を設定する
     *
     * @param SEQUENCE
     */
    public void setSequence(String SEQUENCE) {
        setString(Project.DIAGNOSIS_ASCENDING, SEQUENCE);
    }

    public String getSicknessTime() {
        return getString(Project.DIAGNOSIS_PERIOD);
    }

    /**
     * ログイン画面用のスクロール方向を設定する
     *
     * @param SICKNESSTIME
     */
    public void setSicknessTime(String SICKNESSTIME) {
        setString(Project.DIAGNOSIS_PERIOD, SICKNESSTIME);
    }

    public String getSicknessActive() {
        return getString(Project.DIAGNOSIS_ACTIVE_ONLY);
    }

    /**
     * ログイン画面用のスクロール方向を設定する
     *
     * @param SICKNESSACTIVE
     */
    public void setSicknessActive(String SICKNESSACTIVE) {
        setString(Project.DIAGNOSIS_ACTIVE_ONLY, SICKNESSACTIVE);
    }

    public String getOutcomeDate() {
        return getString(Project.DIAGNOSIS_AUTO_OUTCOME_INPUT);
    }

    /**
     * ログイン画面用の転帰入力を設定する
     *
     * @param OUTCOMEDATE
     */
    public void setOutcomedate(String OUTCOMEDATE) {
        setString(Project.DIAGNOSIS_AUTO_OUTCOME_INPUT, OUTCOMEDATE);
    }

    public String getEndDate() {
        return getString(Project.OFFSET_OUTCOME_DATE);
    }

    /**
     * ログイン画面用の転帰入力を設定する
     *
     * @param ENDDATE
     */
    public void setEndDate(String ENDDATE) {
        setString(Project.OFFSET_OUTCOME_DATE, ENDDATE);
    }

    //カルテ/診療行為
    public String getHead15() {
        return getString(Project.KARTE_USE_TOP15_AS_TITLE);
    }

    /**
     * ログイン画面用の転帰入力を設定する
     *
     * @param HEAD15
     */
    public void setHead15(String HEAD15) {
        setString(Project.KARTE_USE_TOP15_AS_TITLE, HEAD15);
    }

    public String getProgressNote() {
        return getString(Project.KARTE_DEFAULT_TITLE);
    }

    /**
     * ログイン画面用の転帰入力を設定する
     *
     * @param PROGRESSNOTE
     */
    public void setProgressNote(String PROGRESSNOTE) {
        setString(Project.KARTE_DEFAULT_TITLE, PROGRESSNOTE);
    }

    public String getSendClaimSave() {
        return getString(Project.SEND_CLAIM_SAVE);
    }

    /**
     * ログイン画面用のカルテ保存時の設定する
     *
     * @param ClaimSave
     */
    public void setSendClaimSave(String ClaimSave) {
        setString(Project.SEND_CLAIM_SAVE, ClaimSave);
    }

    public String getSendClaimModify() {
        return getString(Project.SEND_CLAIM_MODIFY);
    }

    /**
     * ログイン画面用のカルテ修正時の設定する
     *
     * @param ClaimModify
     */
    public void setSendClaimModify(String ClaimModify) {
        setString(Project.SEND_CLAIM_MODIFY, ClaimModify);
    }

    public String getSendClaim_Schedule() {
        return getString(Project.SEND_CLAIM_EDIT_FROM_SCHEDULE);
    }

    /**
     * 予定カルテの設定する
     *
     * @param ClaimSchedele
     */
    public void setSendClaim_Schedule(String ClaimSchedele) {
        setString(Project.SEND_CLAIM_EDIT_FROM_SCHEDULE, ClaimSchedele);
    }

    public String getCheck_TMP() {
        return getString(Project.SEND_CLAIM_DEPENDS_ON_CHECK_AT_TMP);
    }

    /**
     * 仮保存ボタン押下時の設定する
     *
     * @param Check_TMP
     */
    public void setCheck_TMP(String Check_TMP) {
        setString(Project.SEND_CLAIM_DEPENDS_ON_CHECK_AT_TMP, Check_TMP);
    }

    public String getCombined() {
        return getString(Project.INTERACTION_CHECK);
    }

    /**
     * 併用禁忌の設定する
     *
     * @param combined
     */
    public void setCombined(String combined) {
        setString(Project.INTERACTION_CHECK, combined);
    }

    public String getcostprice() {
        return getString(Project.INSURANCE_SELF);
    }

    /**
     * 実費の設定する
     *
     * @param COSTPRICE
     */
    public void setcostprice(String COSTPRICE) {
        setString(Project.INSURANCE_SELF, COSTPRICE);
    }

    public String getAccident() {
        return getString(Project.INSURANCE_ROSAI_PREFIX);
    }

    /**
     * 労災の設定する
     *
     * @param accident
     */
    public void setAccident(String accident) {
        setString(Project.INSURANCE_ROSAI_PREFIX, accident);
    }

    public String getInsurance() {
        return getString(Project.INSURANCE_JIBAISEKI_PREFIX);
    }

    /**
     * 自賠責の設定する
     *
     * @param INSURANCE
     */
    public void setInsurance(String INSURANCE) {
        setString(Project.INSURANCE_JIBAISEKI_PREFIX, INSURANCE);
    }

    //カルテ/その他タブ
    public String getDialog() {
        return getString(Project.KARTE_SHOW_CONFIRM_AT_NEW);
    }

    /**
     * 確認ダイアログの表示の設定する
     *
     * @param DIALOG
     */
    public void setDialog(String DIALOG) {
        setString(Project.KARTE_SHOW_CONFIRM_AT_NEW, DIALOG);
    }

    public String getCreation() {
        return getString(Project.KARTE_CREATE_MODE);
    }

    /**
     * 確認ダイアログの表示の設定する
     *
     * @param CREATE
     */
    public void setCreation(String CREATE) {
        setString(Project.KARTE_CREATE_MODE, CREATE);
    }

    public String getArrangement() {
        return getString(Project.KARTE_PLACE_MODE);
    }

    /**
     * 確認ダイアログの表示の設定する
     *
     * @param PLACE_MODE
     */
    public void setArrangement(String PLACE_MODE) {
        setString(Project.KARTE_PLACE_MODE, PLACE_MODE);
    }

    //カルテ保存時
    public String getCloses_Automatically() {
        return getString(Project.KARTE_AUTO_CLOSE_AFTER_SAVE);
    }

    /**
     * 編集ウィンドウを自動的に閉じる設定する
     *
     * @param Closes_Automatically_Dialog
     */
    public void setCloses_Automatically(String Closes_Automatically_Dialog) {
        setString(Project.KARTE_AUTO_CLOSE_AFTER_SAVE, Closes_Automatically_Dialog);
    }

    public String getKarte_Save_Dialog() {
        return getString(Project.KARTE_SHOW_CONFIRM_AT_SAVE);
    }

    /**
     * 編集ウィンドウを自動的に閉じる設定する
     *
     * @param KARTE_SAVE_DIALOG
     */
    public void setKarte_Save_Dialog(String KARTE_SAVE_DIALOG) {
        setString(Project.KARTE_SHOW_CONFIRM_AT_SAVE, KARTE_SAVE_DIALOG);
    }

    public String getPrinting_Sheets() {
        return getString(Project.KARTE_PRINT_COUNT);
    }

    /**
     * 編集ウィンドウを自動的に閉じる設定する
     *
     * @param PRINTING_SHEETS
     */
    public void setPrinting_Sheets(String PRINTING_SHEETS) {
        setString(Project.KARTE_PRINT_COUNT, PRINTING_SHEETS);
    }

    public String getOperation() {
        return getString(Project.KARTE_SAVE_ACTION);
    }

    /**
     * 編集ウィンドウを自動的に閉じる設定する
     *
     * @param OPARATION
     */
    public void setOperation(String OPARATION) {
        setString(Project.KARTE_SAVE_ACTION, OPARATION);
    }

    //年齢
    public String getAge_Of_The_Moon() {
        return getString(Project.KARTE_AGE_TO_NEED_MONTH);
    }

    /**
     * 編集ウィンドウを自動的に閉じる設定する
     *
     * @param AGE_OF_THE_MOON
     */
    public void setAge_Of_The_Moon(String AGE_OF_THE_MOON) {
        setString(Project.KARTE_AGE_TO_NEED_MONTH, AGE_OF_THE_MOON);
    }

    //2号カルテ
    public String getFontSize() {
        return getString(Project.FONT_SIZE);
    }

    /**
     * 文字サイズの設定する
     *
     * @param FONT_SIZE
     */
    public void setFontSize(String FONT_SIZE) {
        setString(Project.FONT_SIZE, FONT_SIZE);
    }

    //印刷方法
    public String getPrint() {
        return getString(Project.KARTE_PRINT_PDF);
    }

    /**
     * 印刷の種類の設定する
     *
     * @param Print_PDF
     */
    public void setPrint(String Print_PDF) {
        setString(Project.KARTE_PRINT_PDF, Print_PDF);
    }

    public String getPrintView() {
        return getString(Project.KARTE_PRINT_DIRECT);
    }

    /**
     * 印刷ダイアログ設定する
     *
     * @param PrintView
     */
    public void setPrintView(String PrintView) {
        setString(Project.KARTE_PRINT_DIRECT, PrintView);
    }

    //スタンプ/スタンプ動作設定
    public String getSTAMP_REPLACE() {
        return getString(Project.STAMP_REPLACE);
    }

    /**
     * DnDした場合の設定する
     *
     * @param REPLACE
     */
    public void setSTAMP_REPLACE(String REPLACE) {
        setString(Project.STAMP_REPLACE, REPLACE);
    }

    public String getSTAMP_DnD() {
        return getString(Project.STAMP_SPACE);
    }

    /**
     * 間隔を空ける設定する
     *
     * @param SPECE
     */
    public void setSTAMP_DnD(String SPECE) {
        setString(Project.STAMP_SPACE, SPECE);
    }

    public String getLabtest_Fold() {
        return getString(Project.LABTEST_FOLD);
    }

    /**
     * 折りたたみ表示の設定する
     *
     * @param FOLD
     */
    public void setLabtest_Fold(String FOLD) {
        setString(Project.LABTEST_FOLD, FOLD);
    }

    public String getStamp_Name() {
        return getString(Project.DEFAULT_STAMP_NAME);
    }

    /**
     * スタンプ名を表示する設定する
     *
     * @param STAMP_NAME
     */
    public void setStamp_Name(String STAMP_NAME) {
        setString(Project.DEFAULT_STAMP_NAME, STAMP_NAME);
    }

    //スタンプエディタのデフォルト数量
    public String getStamp_Tablet() {
        return getString(Project.DEFAULT_ZYOZAI_NUM);
    }

    /**
     * 錠剤の場合の設定する
     *
     * @param STAMP_TABLET
     */
    public void setStamp_Tablet(String STAMP_TABLET) {
        setString(Project.DEFAULT_ZYOZAI_NUM, STAMP_TABLET);
    }

    public String getStamp_Liquidmedicine() {
        return getString(Project.DEFAULT_MIZUYAKU_NUM);
    }

    /**
     * 水薬の場合の設定する
     *
     * @param STAMP_LIQUIDMEDICINE
     */
    public void setStamp_Liquidmedicine(String STAMP_LIQUIDMEDICINE) {
        setString(Project.DEFAULT_MIZUYAKU_NUM, STAMP_LIQUIDMEDICINE);
    }

    public String getStamp_Powder() {
        return getString(Project.DEFAULT_SANYAKU_NUM);
    }

    /**
     * 散薬の場合の設定する
     *
     * @param STAMP_POWDER
     */
    public void setStamp_Powder(String STAMP_POWDER) {
        setString(Project.DEFAULT_SANYAKU_NUM, STAMP_POWDER);
    }

    public String getStamp_Capsule() {
        return getString(Project.DEFAULT_CAPSULE_NUM);
    }

    /**
     * カプセルの場合の設定する
     *
     * @param STAMP_CAPCEL
     */
    public void setSTAMP_CAPCEL(String STAMP_CAPCEL) {
        setString(Project.DEFAULT_CAPSULE_NUM, STAMP_CAPCEL);
    }

    public String getStamp_Prescriptiondays() {
        return getString(Project.DEFAULT_RP_NUM);
    }

    /**
     * カプセルの場合の設定する
     *
     * @param STAMP_PRESCRIPTIONDAYS
     */
    public void setStamp_Prescriptiondays(String STAMP_PRESCRIPTIONDAYS) {
        setString(Project.DEFAULT_RP_NUM, STAMP_PRESCRIPTIONDAYS);
    }

    //処方
    public String getStamp_Direction() {
        return getString(Project.KARTE_MERGE_RP_WITH_SAME_ADMIN);
    }

    /**
     * 同じ用法にまとめる設定する
     *
     * @param STAMP_DIRECTION
     */
    public void setStamp_Direction(String STAMP_DIRECTION) {
        setString(Project.KARTE_MERGE_RP_WITH_SAME_ADMIN, STAMP_DIRECTION);
    }

    //マスター検索
    public String getStamp_Master() {
        return getString(Project.MASTER_SEARCH_ITEM_COLORING);
    }

    /**
     * マスター項目のカラーリングを設定する
     *
     * @param STAMP_MASTER
     */
    public void setStamp_Master(String STAMP_MASTER) {
        setString(Project.MASTER_SEARCH_ITEM_COLORING, STAMP_MASTER);
    }

    //スタンプエディタのボタンタイプ
    public String getSTAMP_EDITOR_BUTTON_TYPE() {
        return getString(Project.STAMP_EDITOR_BUTTON_TYPE);
    }

    /**
     * スタンプエディタボタンを設定する
     *
     * @param STAMP_EDITOR_BUTTON
     */
    public void setSTAMP_EDITOR_BUTTON_TYPE(String STAMP_EDITOR_BUTTON) {
        setString(Project.STAMP_EDITOR_BUTTON_TYPE, STAMP_EDITOR_BUTTON);
    }

    //紹介状/診療情報提供所
    public String getLETTER_ATESAKI_TITLE() {
        return getString(Project.LETTER_ATESAKI_TITLE);
    }

    /**
     * 宛先継承の設定をする
     *
     * @param ATESAKI_TITLE
     */
    public void setLETTER_ATESAKI_TITLE(String ATESAKI_TITLE) {
        setString(Project.LETTER_ATESAKI_TITLE, ATESAKI_TITLE);
    }

    public String getLETTER_INCLUDE_GREETINGS() {
        return getString(Project.LETTER_INCLUDE_GREETINGS);
    }

    /**
     * 宛先継承の設定をする
     *
     * @param INCLUDE_GREETINGS
     */
    public void setLETTER_INCLUDE_GREETINGS(String INCLUDE_GREETINGS) {
        setString(Project.LETTER_INCLUDE_GREETINGS, INCLUDE_GREETINGS);
    }

    //プレイン文書
    public String getPLAIN_PRINT_PATIENT_NAME() {
        return getString(Project.PLAIN_PRINT_PATIENT_NAME);
    }

    /**
     * 宛先継承の設定をする
     *
     * @param PATIENT_NAME
     */
    public void setPLAIN_PRINT_PATIENT_NAME(String PATIENT_NAME) {
        setString(Project.PLAIN_PRINT_PATIENT_NAME, PATIENT_NAME);
    }

    //診断書
    public String getSHINDANSYO_FONT_SIZE() {
        return getString(Project.SHINDANSYO_FONT_SIZE);
    }

    /**
     * フォントサイズの設定をする
     *
     * @param SHIDANSYO
     */
    public void setSHINDANSYO_FONT_SIZE(String SHIDANSYO) {
        setString(Project.PLAIN_PRINT_PATIENT_NAME, SHIDANSYO);
    }

    //出力先
    public String getLOCATION_PDF() {
        return getString(Project.LOCATION_PDF);
    }

    /**
     * 出力先の設定をする
     *
     * @param PDF_SEND
     */
    public void setLOCATION_PDF(String PDF_SEND) {
        setString(Project.LOCATION_PDF, PDF_SEND);
    }

    //コードタブ/修飾キー
    public String getCODE_MODEFIER() {
        return getString(Project.ENTITY_CODE_MODEFIER);
    }

    /**
     * 修飾キーの設定をする
     *
     * @param code_modefier
     */
    public void setCODE_MODEFIER(String code_modefier) {
        setString(Project.ENTITY_CODE_MODEFIER, code_modefier);
    }

    //スタンプ箱のキーワード/テキスト
    public String getENTITY_CODE_TEXT() {
        return getString(Project.ENTITY_CODE_TEXT);
    }

    /**
     * テキストのキーワードの設定をする
     *
     * @param code_text
     */
    public void setENTITY_CODE_TEXT(String code_text) {
        setString(Project.ENTITY_CODE_TEXT, code_text);
    }

    //パスのキーワード
    public String getENTITY_CODE_PATH() {
        return getString(Project.ENTITY_CODE_PATH);
    }

    /**
     * パスのキーワードの設定をする
     *
     * @param code_path
     */
    public void setENTITY_CODE_PATH(String code_path) {
        setString(Project.ENTITY_CODE_PATH, code_path);
    }

    //汎用のキーワード
    public String getENTITY_CODE_GENERAL_ORDER() {
        return getString(Project.ENTITY_CODE_GENERAL_ORDER);
    }

    /**
     * 汎用のキーワードの設定をする
     *
     * @param code_general
     */
    public void setENTITY_CODE_GENERAL_ORDER(String code_general) {
        setString(Project.ENTITY_CODE_GENERAL_ORDER, code_general);
    }

    //その他のキーワード
    public String getENTITY_CODE_OTHER_ORDER() {
        return getString(Project.ENTITY_CODE_OTHER_ORDER);
    }

    /**
     * その他のキーワードの設定をする
     *
     * @param code_other
     */
    public void setENTITY_CODE_OTHER_ORDER(String code_other) {
        setString(Project.ENTITY_CODE_OTHER_ORDER, code_other);
    }

    //処置のキーワード
    public String getENTITY_CODE_TREATMEN() {
        return getString(Project.ENTITY_CODE_TREATMEN);
    }

    /**
     * その他のキーワードの設定をする
     *
     * @param code_treatmen
     */
    public void setENTITY_CODE_TREATMEN(String code_treatmen) {
        setString(Project.ENTITY_CODE_TREATMEN, code_treatmen);
    }

    //手術
    public String getENTITY_CODE_SURGERY_ORDER() {
        return getString(Project.ENTITY_CODE_SURGERY_ORDER);
    }

    /**
     * 手術のキーワードの設定をする
     *
     * @param code_surgery
     */
    public void setENTITY_CODE_SURGERY_ORDER(String code_surgery) {
        setString(Project.ENTITY_CODE_SURGERY_ORDER, code_surgery);
    }

    //放射線
    public String getENTITY_CODE_RADIOLOGY_ORDER() {
        return getString(Project.ENTITY_CODE_RADIOLOGY_ORDER);
    }

    /**
     * 放射線のキーワードの設定をする
     *
     * @param code_radilogy
     */
    public void setENTITY_CODE_RADIOLOGY_ORDER(String code_radilogy) {
        setString(Project.ENTITY_CODE_RADIOLOGY_ORDER, code_radilogy);
    }

    //検体検査
    public String getENTITY_CODE_LABO_TEST() {
        return getString(Project.ENTITY_CODE_LABO_TEST);
    }

    /**
     * 検体検査のキーワードの設定をする
     *
     * @param code_labo
     */
    public void setENTITY_CODE_LABO_TEST(String code_labo) {
        setString(Project.ENTITY_CODE_LABO_TEST, code_labo);
    }

    //生体検査
    public String getENTITY_CODE_PHYSIOLOGY_ORDER() {
        return getString(Project.ENTITY_CODE_PHYSIOLOGY_ORDER);
    }

    /**
     * 生体検査のキーワードの設定をする
     *
     * @param code_physiology
     */
    public void setENTITY_CODE_PHYSIOLOGY_ORDER(String code_physiology) {
        setString(Project.ENTITY_CODE_PHYSIOLOGY_ORDER, code_physiology);
    }

    //細菌検査
    public String getENTITY_CODE_BACTERIA_ORDER() {
        return getString(Project.ENTITY_CODE_BACTERIA_ORDER);
    }

    /**
     * 細菌検査のキーワードの設定をする
     *
     * @param code_bacteria
     */
    public void setENTITY_CODE_BACTERIA_ORDER(String code_bacteria) {
        setString(Project.ENTITY_CODE_BACTERIA_ORDER, code_bacteria);
    }

    //注射
    public String getENTITY_CODE_INJECTION_ORDER() {
        return getString(Project.ENTITY_CODE_INJECTION_ORDER);
    }

    /**
     * 注射のキーワードの設定をする
     *
     * @param code_injection
     */
    public void setENTITY_CODE_INJECTION_ORDER(String code_injection) {
        setString(Project.ENTITY_CODE_INJECTION_ORDER, code_injection);
    }

    //処方
    public String getENTITY_CODE_MED_ORDER() {
        return getString(Project.ENTITY_CODE_MED_ORDER);
    }

    /**
     * 処方のキーワードの設定をする
     *
     * @param code_med
     */
    public void setENTITY_CODE_MED_ORDER(String code_med) {
        setString(Project.ENTITY_CODE_MED_ORDER, code_med);
    }

    //診断料
    public String getENTITY_CODE_BASE_CHARGE_ORDER() {
        return getString(Project.ENTITY_CODE_BASE_CHARGE_ORDER);
    }

    /**
     * 診断料のキーワードの設定をする
     *
     * @param code_base
     */
    public void setENTITY_CODE_BASE_CHARGE_ORDER(String code_base) {
        setString(Project.ENTITY_CODE_BASE_CHARGE_ORDER, code_base);
    }

    //指導・在宅
    public String getENTITY_CODE_INSTRACTION_CHARGE_ORDER() {
        return getString(Project.ENTITY_CODE_INSTRACTION_CHARGE_ORDER);
    }

    /**
     * 指導・在宅のキーワードの設定をする
     *
     * @param code_instraction
     */
    public void setENTITY_CODE_INSTRACTION_CHARGE_ORDER(String code_instraction) {
        setString(Project.ENTITY_CODE_INSTRACTION_CHARGE_ORDER, code_instraction);
    }

    //ORCA
    public String getENTITY_CODE_ORCA() {
        return getString(Project.ENTITY_CODE_ORCA);
    }

    /**
     * 指導・在宅のキーワードの設定をする
     *
     * @param code_orca
     */
    public void setENTITY_CODE_ORCA(String code_orca) {
        setString(Project.ENTITY_CODE_ORCA, code_orca);
    }

    //リレー等/MML出力
    public String getSEND_MML() {
        return getString(Project.SEND_MML);
    }

    /**
     * カルテ保存時にMML出力を行う設定をする
     *
     * @param relay_mml
     */
    public void setSEND_MML(String relay_mml) {
        setString(Project.SEND_MML, relay_mml);
    }

    //出力先ディレクトリ
    public String getSEND_MML_DIRECTORY() {
        return getString(Project.SEND_MML_DIRECTORY);
    }

    /**
     * カルテ保存時にMML出力を行う設定をする
     *
     * @param relay_mml_directory
     */
    public void setSEND_MML_DIRECOTRY(String relay_mml_directory) {
        setString(Project.SEND_MML_DIRECTORY, relay_mml_directory);
    }

    //バージョン
    public String getSEND_MML_VERSION() {
        return getString(Project.MML_VERSION);
    }

    /**
     * カルテ保存時にMML出力を行う設定をする
     *
     * @param relay_mml_version
     */
    public void setSEND_MML_VERSION(String relay_mml_version) {
        setString(Project.MML_VERSION, relay_mml_version);
    }

    //カルテPDF出力
    public String getKARTE_PDF_SEND_AT_SAVE() {
        return getString(Project.KARTE_PDF_SEND_AT_SAVE);
    }

    /**
     * カルテ保存時にMML出力を行う設定をする
     *
     * @param relay_PDF_save
     */
    public void setKARTE_PDF_SEND_AT_SAVE(String relay_PDF_save) {
        setString(Project.KARTE_PDF_SEND_AT_SAVE, relay_PDF_save);
    }

    //出力先ディレクトリ
    public String getKARTE_PDF_SEND_DIRECTORY() {
        return getString(Project.KARTE_PDF_SEND_DIRECTORY);
    }

    /**
     * カルテ保存時にMML出力を行う設定をする
     *
     * @param relay_PDF_directory
     */
    public void setKARTE_PDF_SEND_DIRECTORY(String relay_PDF_directory) {
        setString(Project.KARTE_PDF_SEND_DIRECTORY, relay_PDF_directory);
    }

    //受付リレー
    public String getPVT_RELAY() {
        return getString(Project.PVT_RELAY);
    }

    /**
     * 受付情報のリレーを設定をする
     *
     * @param relay_pvt
     */
    public void setPVT_RELAY(String relay_pvt) {
        setString(Project.PVT_RELAY, relay_pvt);
    }

    //出力先ディレクトリ
    public String getPVT_RELAY_DIRECTORY() {
        return getString(Project.PVT_RELAY_DIRECTORY);
    }

    /**
     * 受付情報のリレーを設定をする
     *
     * @param relay_pvt_directory
     */
    public void setPVT_RELAY_DIRECTORY(String relay_pvt_directory) {
        setString(Project.PVT_RELAY_DIRECTORY, relay_pvt_directory);
    }

    public String getPVT_RELAY_ENCODING() {
        return getString(Project.PVT_RELAY_ENCODING);
    }

    /**
     * 受付情報のリレーを設定をする
     *
     * @param relay_pvt_encoding
     */
    public void setPVT_RELAY_ENCODING(String relay_pvt_encoding) {
        setString(Project.PVT_RELAY_ENCODING, relay_pvt_encoding);
    }

    /**
     * GetUserDefaults
     *
     * @return
     */
    public Properties getUserDefaults() {
        return userDefaults;
    }

    /**
     * (Application) Defaults
     *
     * @return
     */
    public Properties getApplicationDefaults() {
        return applicationDefaults;
    }

    /**
     * loadProperties
     *
     * @param prop
     * @param name
     */
    public void loadProperties(Properties prop, String name) {

        FileInputStream fin;
        BufferedInputStream bin = null;

        try {
            File parent = getSettingDirectory();
            File target = new File(parent, name);
            fin = new FileInputStream(target);
            bin = new BufferedInputStream(fin);
            prop.load(bin);

        } catch (FileNotFoundException fe) {

        } catch (IOException ie) {
            ie.printStackTrace(System.err);

        } finally {
            closeStream(bin);
        }
    }

    /**
     * storeProperties
     *
     * @param prop
     * @param name
     */
    public void storeProperties(Properties prop, String name) {

        BufferedOutputStream fout = null;

        try {
            File parent = getSettingDirectory();
            File target = new File(parent, name);

            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new DolphinException("ディレクトリを作成できません。 " + parent);
                }
            }

            fout = new BufferedOutputStream(new FileOutputStream(target));
            prop.store(fout, "1.0");

        } catch (IOException e) {
            e.printStackTrace(System.err);
        } finally {
            closeStream(fout);
        }
    }

    /**
     * loadPropertiesAsObject
     *
     * @param name
     * @return Properties
     */
    public Properties loadPropertiesAsObject(String name) {

        XMLDecoder d;
        Properties ret = null;

        try {
            File parent = getSettingDirectory();
            File target = new File(parent, name);
            d = new XMLDecoder(
                    new BufferedInputStream(
                            new FileInputStream(target)));
            ret = (Properties) d.readObject();
            d.close();

        } catch (FileNotFoundException fe) {
        }

        return ret;
    }

    /**
     * storePropertiesAsObject
     *
     * @param prop
     * @param name
     */
    public void storePropertiesAsObject(Properties prop, String name) {

        XMLEncoder e;

        File parent = getSettingDirectory();
        File target = new File(parent, name);
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                throw new DolphinException("ディレクトリを作成できません。 " + parent);
            }
        }

        try {
            e = new XMLEncoder(
                    new BufferedOutputStream(
                            new FileOutputStream(target)));
            e.writeObject(prop);
            e.close();

        } catch (FileNotFoundException fe) {

        }
    }

    /**
     * deleteSettingFile
     *
     * @param fileName
     * @return
     */
    public boolean deleteSettingFile(String fileName) {
        File delete = new File(getSettingDirectory(), fileName);
        return delete.delete();
    }

    private void closeStream(Closeable st) {

        if (st != null) {
            try {
                st.close();
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            }
        }
    }

    /**
     * Propertyを保存する
     */
    public void saveUserDefaults() {

        BufferedOutputStream fout = null;

        try {
            //-------------------------------
            // 個別設定をremoveしてから保存する
            //-------------------------------
            Properties toSave;
            synchronized (userDefaults) {
                toSave = (Properties) userDefaults.clone();
            }
            if (customDefaults != null) {
                Enumeration e = customDefaults.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    toSave.remove(key);
                }
            }
            //--------------------------------
            // 稼働状況も除く
            //--------------------------------
            toSave.remove(GUIConst.PVT_SERVER_IS_RUNNING);      // PVTServer is running
            //toSave.remove(GUIConst.BIND_ADDRESS_PVT_SERVER);    // Bind address for PVTServer
            toSave.remove(GUIConst.SEND_CLAIM_IS_RUNNING);      // Send claim is running
            //toSave.remove(GUIConst.ADDRESS_CLAIM);              // Claim server address
            toSave.remove(GUIConst.SEND_MML_IS_RUNNING);        // Send MML
            //toSave.remove(GUIConst.CSGW_PATH);                  // Path for MML data
            toSave.remove(GUIConst.PVT_RELAY_IS_RUNNING);
            //--------------------------------
            File parent = getSettingDirectory();
            if (!parent.exists()) {
                if (!parent.mkdirs()) {
                    throw new DolphinException("ディレクトリを作成できません。 " + parent);
                }
            }
            File target = new File(parent, "user-defaults.properties");
            fout = new BufferedOutputStream(new FileOutputStream(target));
            toSave.store(fout, "1.0");

        } catch (IOException e) {
            e.printStackTrace(System.err);
        } finally {
            closeStream(fout);
        }
    }

    private File getOldSettingDirectory() {

        //- 旧設定の有無
        String userHome = System.getProperty("user.home");
        String oldAppID = ClientContext.getString("product.old.name");
        String oldVendorId = ClientContext.getString("product.old.vender.id");
        File ret;

        StringBuilder sb = new StringBuilder();
        if (ClientContext.isWin()) {
            File appDataDir = null;
            try {
                String appDataEnv = System.getenv("APPDATA");
                if ((appDataEnv != null) && (appDataEnv.length() > 0)) {
                    appDataDir = new File(appDataEnv);
                }
            } catch (SecurityException ignore) {
            }
            if ((appDataDir != null) && appDataDir.isDirectory()) {
                // APPDATA\vendorId\appId\ -> APPDATA\appId\vendorId\
                sb.append(oldVendorId).append(File.separator).append(oldAppID).append(File.separator);
                ret = new File(appDataDir, sb.toString());
            } else {
                // userHome\Application Data\vendorId\appId\ -> userHome\Application Data\appId\vendorId\
                sb.append(userHome).append(File.separator);
                sb.append("Application Data").append(File.separator);
                sb.append(oldVendorId).append(File.separator);
                sb.append(oldAppID).append(File.separator);
                ret = new File(sb.toString());
            }

        } else if (ClientContext.isMac()) {
            sb.append(userHome).append(File.separator);
            sb.append("Library/Application Support/");
            sb.append(oldAppID).append(File.separator);
            ret = new File(sb.toString());
        } else {
            sb.append(userHome).append(File.separator).append(".").append(oldAppID).append(File.separator);
            ret = new File(sb.toString());
        }

        return ret;
    }

    private File getSettingDirectory() {

        String userHome = System.getProperty("user.home");
        String appId = ClientContext.getString("product.name");
        String vendorId = ClientContext.getString("product.vender.id");
        File ret;

        //- OpenDolphinPro 設定引き継ぎ
        String oldAppId = ClientContext.getString("product.old.id");
        String oldVendorId = ClientContext.getString("product.old.vender.id");

        StringBuilder sb = new StringBuilder();
        StringBuilder oldSb = new StringBuilder();

        if (ClientContext.isWin()) {
            File appDataDir = null;
            try {
                String appDataEnv = System.getenv("APPDATA");
                if ((appDataEnv != null) && (appDataEnv.length() > 0)) {
                    appDataDir = new File(appDataEnv);
                }
            } catch (SecurityException ignore) {
            }
            if ((appDataDir != null) && appDataDir.isDirectory()) {
                // APPDATA\vendorId\appId\
                sb.append(vendorId).append(File.separator).append(appId).append(File.separator);
                ret = new File(appDataDir, sb.toString());

                //- OldCoonfig
                oldSb.append(oldVendorId);
                oldSb.append(File.separator);
                oldSb.append(oldAppId);
                oldSb.append(File.separator);
                File checkOld = new File(appDataDir.toString() + File.separator + oldSb.toString());
                if (checkOld.exists()) {
                    ret = new File(appDataDir, oldSb.toString());
                }
            } else {
                // userHome\Application Data\vendorId\appId\
                sb.append(userHome).append(File.separator);
                sb.append("Application Data").append(File.separator);
                sb.append(vendorId).append(File.separator);
                sb.append(appId).append(File.separator);
                ret = new File(sb.toString());

                //- OldCoonfig
                oldSb.append(userHome).append(File.separator);
                oldSb.append("Application Data").append(File.separator);
                oldSb.append(oldVendorId).append(File.separator);
                oldSb.append(oldAppId).append(File.separator);
                File checkOld = new File(oldSb.toString());
                if (checkOld.exists()) {
                    ret = new File(oldSb.toString());
                }
            }

        } else if (ClientContext.isMac()) {
            sb.append(userHome).append(File.separator);
            sb.append("Library/Application Support/");
            sb.append(appId).append(File.separator);
            ret = new File(sb.toString());

            //- OldCoonfig
            oldSb.append(userHome).append(File.separator);
            oldSb.append("Library/Application Support/");
            oldSb.append(oldAppId).append(File.separator);
            File checkOld = new File(oldSb.toString());
            if (checkOld.exists()) {
                ret = new File(oldSb.toString());
            }
        } else {
            sb.append(userHome).append(File.separator).append(".").append(appId).append(File.separator);
            ret = new File(sb.toString());

            //- OldCoonfig
            oldSb.append(userHome).append(File.separator).append(".").append(oldAppId).append(File.separator);
            File checkOld = new File(oldSb.toString());
            if (checkOld.exists()) {
                ret = new File(oldSb.toString());
            }
        }

        return ret;
    }

    private String arrayToLine(String[] arr) {
        StringBuilder sb = new StringBuilder();
        for (String val : arr) {
            sb.append(val);
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    private String rectToLine(Rectangle rect) {
        StringBuilder sb = new StringBuilder();
        sb.append(rect.x).append(",");
        sb.append(rect.y).append(",");
        sb.append(rect.width).append(",");
        sb.append(rect.height);
        return sb.toString();
    }

    private String colorToLine(Color color) {
        StringBuilder sb = new StringBuilder();
        sb.append(color.getRed()).append(",");
        sb.append(color.getGreen()).append(",");
        sb.append(color.getBlue());
        return sb.toString();
    }

    public String getString(String key) {
        return getUserDefaults().getProperty(key);
    }

    public String getString(String key, String defStr) {
        return getUserDefaults().getProperty(key, defStr);
    }

    public void setString(String key, String value) {
        getUserDefaults().setProperty(key, value);
    }

    public String[] getStringArray(String key) {
        String line = getUserDefaults().getProperty(key);
        return line != null ? line.split("\\s*,\\s*") : null;
    }

    public String[] getStringArray(String key, String[] defStr) {
        String line = getUserDefaults().getProperty(key, arrayToLine(defStr));
        return line.split("\\s*,\\s*");
    }

    public void setStringArray(String key, String[] value) {
        getUserDefaults().setProperty(key, arrayToLine(value));
    }

    public int getInt(String key) {
        String val = getString(key);
        return val != null ? Integer.parseInt(val) : 0;
    }

    public int getInt(String key, int defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Integer.parseInt(val);
    }

    public void setInt(String key, int value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public float getFloat(String key) {
        String val = getString(key);
        return val != null ? Float.parseFloat(val) : 0L;
    }

    public float getFloat(String key, float defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Float.parseFloat(val);
    }

    public void setFloat(String key, float value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public double getDouble(String key) {
        String val = getString(key);
        return val != null ? Double.parseDouble(val) : 0D;
    }

    public double getDouble(String key, double defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Double.parseDouble(val);
    }

    public void setDouble(String key, double value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public boolean getBoolean(String key) {
        String val = getString(key);
        return val != null ? Boolean.parseBoolean(val) : false;
    }

    public boolean getBoolean(String key, boolean defVal) {
        String val = getString(key, String.valueOf(defVal));
        return Boolean.parseBoolean(val);
    }

    public void setBoolean(String key, boolean value) {
        getUserDefaults().setProperty(key, String.valueOf(value));
    }

    public Rectangle getRectangle(String key) {
        String line = getString(key);
        if (line != null) {
            String[] cmp = line.split("\\s*,\\s*");
            int x = Integer.parseInt(cmp[0]);
            int y = Integer.parseInt(cmp[1]);
            int width = Integer.parseInt(cmp[2]);
            int height = Integer.parseInt(cmp[3]);
            return new Rectangle(x, y, width, height);
        } else {
            return null;
        }
    }

    public Rectangle getRectangle(String key, Rectangle defRect) {
        String line = getString(key, rectToLine(defRect));
        String[] cmp = line.split("\\s*,\\s*");
        int x = Integer.parseInt(cmp[0]);
        int y = Integer.parseInt(cmp[1]);
        int width = Integer.parseInt(cmp[2]);
        int height = Integer.parseInt(cmp[3]);
        return new Rectangle(x, y, width, height);
    }

    public void setRectangle(String key, Rectangle value) {
        getUserDefaults().setProperty(key, rectToLine(value));
    }

    public Color getColor(String key) {
        String line = getString(key);
        if (line != null) {
            String[] cmp = line.split("\\s*,\\s*");
            int r = Integer.parseInt(cmp[0]);
            int g = Integer.parseInt(cmp[1]);
            int b = Integer.parseInt(cmp[2]);
            return new Color(r, g, b);
        } else {
            return null;
        }
    }

    public Color getColor(String key, Color defVal) {
        String line = getString(key, colorToLine(defVal));
        String[] cmp = line.split("\\s*,\\s*");
        int r = Integer.parseInt(cmp[0]);
        int g = Integer.parseInt(cmp[1]);
        int b = Integer.parseInt(cmp[2]);
        return new Color(r, g, b);
    }

    public void setColor(String key, Color value) {
        getUserDefaults().setProperty(key, colorToLine(value));
    }

    public String getDefaultString(String key) {
        return getApplicationDefaults().getProperty(key);
    }

    public String[] getDefaultStringArray(String key) {
        String line = getApplicationDefaults().getProperty(key);
        return line != null ? line.split("\\s*,\\s*") : null;
    }

    public int getDefaultInt(String key) {
        String val = getDefaultString(key);
        return val != null ? Integer.parseInt(val) : 0;
    }

    public float getDefaultFloat(String key) {
        String val = getDefaultString(key);
        return val != null ? Float.parseFloat(val) : 0L;
    }

    public double getDefaultDouble(String key) {
        String val = getDefaultString(key);
        return val != null ? Double.parseDouble(val) : 0D;
    }

    public boolean getDefaultBoolean(String key) {
        String val = getDefaultString(key);
        return val != null ? Boolean.parseBoolean(val) : false;
    }

    public Rectangle getDefaultRectangle(String key) {
        String line = getDefaultString(key);
        if (line != null) {
            String[] cmp = line.split("\\s*,\\s*");
            int x = Integer.parseInt(cmp[0]);
            int y = Integer.parseInt(cmp[1]);
            int width = Integer.parseInt(cmp[2]);
            int height = Integer.parseInt(cmp[3]);
            return new Rectangle(x, y, width, height);
        } else {
            return null;
        }
    }

    public Color getDefaultColor(String key) {
        String line = getDefaultString(key);
        if (line != null) {
            String[] cmp = line.split("\\s*,\\s*");
            int r = Integer.parseInt(cmp[0]);
            int g = Integer.parseInt(cmp[1]);
            int b = Integer.parseInt(cmp[2]);
            return new Color(r, g, b);
        } else {
            return null;
        }
    }

    //新宿ヒロクリニック 処方箋印刷^      
    /**
     * 医療機関基本情報を返す。
     *
     * @return 医療機関基本情報 String
     */
    // @002 2010/07/16
    public String getBasicInfo() {
        return basicInfo;
    }

    /**
     * 医療機関基本情報を設定する。
     *
     * @param basicInfo 医療機関基本情報
     */
    // @002 2010/07/16
    public void setBasicInfo(String basicInfo) {
        this.basicInfo = basicInfo;
    }

}
