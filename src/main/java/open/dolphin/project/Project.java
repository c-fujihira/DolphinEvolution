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
import java.io.File;
import java.util.Properties;
import open.dolphin.infomodel.ID;
import open.dolphin.infomodel.UserModel;

/**
 * プロジェクト情報管理クラス。
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class Project {

    //- Prpject Name
    public static final String PROJECT_NAME = "name";

    //- USER
    public static final String USER_TYPE = "userType";
    public static final String FACILITY_NAME = "facilityName";
    public static final String FACILITY_ID = "facilityId";
    public static final String USER_ID = "userId";
    public static final String SERVER_URI = "baseURI";

    // CLAIM
    // 2012-07  claim.sender=(client | server) client=client送信, server=server送信
    public static final String CLAIM_SENDER = "claim.sender";
    public static final String SEND_CLAIM = "sendClaim";
    public static final String SEND_CLAIM_SAVE = "sendClaimSave";
    public static final String SEND_CLAIM_TMP = "sendClaimTmp";
    public static final String SEND_CLAIM_MODIFY = "sendClaimModify";
    public static final String SEND_DIAGNOSIS = "sendDiagnosis";
    public static final String CLAIM_HOST_NAME = "claimHostName";
    public static final String CLAIM_VERSION = "claimVersion";
    public static final String CLAIM_ENCODING = "claimEncoding";
    public static final String CLAIM_ADDRESS = "claimAddress";
    public static final String CLAIM_PORT = "claimPort";
    public static final String USE_AS_PVT_SERVER = "useAsPVTServer";
    public static final String JMARI_CODE = "jmariCode";
    public static final String CLAIM_BIND_ADDRESS = "BIND_ADDRESS";
    public static final String CLAIM_01 = "CLAIM01";
    public static final String INTERACTION_CHECK = "interaction.check";
//minagawa^ 定期チェック    
    public static final String PVT_TIMER_CHECK = "pvt.timer.check";
//minagawa$    
//minagawa^ 予定カルテ    (予定カルテ対応)
    public static final String SEND_CLAIM_EDIT_FROM_SCHEDULE = "send.claim.edit.from.schedule";
    public static final String SEND_CLAIM_WHEN_SCHEDULE = "send.claim.when.schedule";
    public static final String SEND_CLAIM_DEPENDS_ON_CHECK_AT_TMP = "send.claim.depends.on.check.at.tmp";
    public static final String USE_SCHEDULE_KARTE = "use.schedule.karte";
//minagawa$    
    // Labtest
    public static final String SEND_LABTEST = "order.labtest.send";
    public static final String SEND_LABTEST_SYSTEM = "order.labtest.system";
    public static final String SEND_LABTEST_PATH = "order.labtest.path";
    public static final String SEND_LABTEST_FACILITY_ID = "order.labtest.facility.id";

    // Area Network
    public static final String JOIN_AREA_NETWORK = "joinAreaNetwork";
    public static final String AREA_NETWORK_NAME = "jareaNetworkName";
    public static final String AREA_NETWORK_FACILITY_ID = "jareaNetworkFacilityId";
    public static final String AREA_NETWORK_CREATOR_ID = "jareaNetworkCreatorId";

    // MML
    public static final String SEND_MML = "mml.send";
    public static final String MML_VERSION = "mml.version";
    public static final String MML_ENCODING = "mml.encoding";
    public static final String SEND_MML_ADDRESS = "mml.address";
    public static final String SEND_MML_DIRECTORY = "mml.directory";
    public static final String SEND_MML_PROTOCOL = "mml.protocol";

    // ソフトウェア更新
    public static final String USE_PROXY = "useProxy";
    public static final String PROXY_HOST = "proxyHost";
    public static final String PROXY_PORT = "proxyPort";
    public static final String LAST_MODIFIED = "lastModify";

    // インスペクタのメモ位置
    public static final String INSPECTOR_MEMO_LOCATION = "inspectorMemoLocation";

    // インスペクタ配置
    public static final String TOP_INSPECTOR = "topInspector";
    public static final String SECOND_INSPECTOR = "secondInspector";
    public static final String THIRD_INSPECTOR = "thirdInspector";
    public static final String FORTH_INSPECTOR = "forthInspector";
    public static final String LOCATION_BY_PLATFORM = "locationByPlatform";
    public static final String SCEMA_EDITOR = "schema.editor.name";
    public static final String CONTAINER = "container";

    // 文書履歴
    public static final String DOC_HISTORY_ASCENDING = "docHistory.ascending";
    public static final String DOC_HISTORY_SHOWMODIFIED = "docHistory.showModified";
    public static final String DOC_HISTORY_FETCHCOUNT = "docHistory.fetchCount";
    public static final String DOC_HISTORY_PERIOD = "docHistory.period";
    public static final String KARTE_SCROLL_DIRECTION = "karte.scroll.direction";
    public static final String DOUBLE_KARTE = "karte.double";

    // 病名
    public static final String DIAGNOSIS_ASCENDING = "diagnosis.ascending";
    public static final String DIAGNOSIS_PERIOD = "diagnosis.period";
    public static final String OFFSET_OUTCOME_DATE = "diagnosis.offsetOutcomeDate";
    public static final String DIAGNOSIS_AUTO_OUTCOME_INPUT = "autoOutcomeInput";
    public static final String DIAGNOSIS_ACTIVE_ONLY = "diagnosis.activeOnly";

    // 検体検査
    public static final String LABOTEST_PERIOD = "laboTest.period";
    public static final String LABTEST_FOLD = "laboFold";

    // 処方
    public static final String RP_OUT = "rp.out";

    // カルテ
    public static final String KARTE_USE_TOP15_AS_TITLE = "useTop15AsTitle";
    public static final String KARTE_DEFAULT_TITLE = "defaultKarteTitle";
    public static final String KARTE_SHOW_CONFIRM_AT_NEW = "karte.showConfirmAtNew";
    public static final String KARTE_CREATE_MODE = "karte.createMode";
    public static final String KARTE_PLACE_MODE = "karte.placeMode";
    public static final String KARTE_SHOW_CONFIRM_AT_SAVE = "karte.showConfirmAtSave";
    public static final String KARTE_PRINT_COUNT = "karte.print.count";
    public static final String KARTE_SAVE_ACTION = "karte.saveAction";
    public static final String KARTE_AUTO_CLOSE_AFTER_SAVE = "karte.auto.close";
    public static final String KARTE_AGE_TO_NEED_MONTH = "ageToNeedMonth";
    public static final String KARTE_MERGE_RP_WITH_SAME_ADMIN = "merge.rp.with.sameAdmin";
//s.oh^ 2014/01/27 同じ検体検査をまとめる
    public static final String KARTE_MERGE_WITH_LABTEST = "merge.with.labtest";
//s.oh$

    public static final String KARTE_PDF_SEND_AT_SAVE = "karte.pdf.send.at.save";
    public static final String KARTE_PDF_SEND_DIRECTORY = "karte.pdf.send.directory";

//s.oh^ 2013/02/07 印刷対応
    public static final String KARTE_PRINT_DIRECT = "karte.print.direct";
    public static final String KARTE_PRINT_PDF = "karte.print.pdf";
//s.oh^ 2013/06/24 印刷対応
    public static final String KARTE_PRINT_SHOWPDF = "karte.print.showpdf";
//s.oh$
//s.oh$
//s.oh^ 2013/09/12 PDF印刷文字サイズ
    public static final String KARTE_PRINT_PDF_TEXTSIZE = "karte.print.pdf.textsize";
    public static final String LABO_PRINT_SHOWPDF = "labo.print.showpdf";
//s.oh$

    // Stamp
    public static final String STAMP_REPLACE = "replaceStamp";
    public static final String STAMP_SPACE = "stampSpace";

    // StampEditor
    public static final String STAMP_EDITOR_BUTTON_TYPE = "stamp.editor.buttonType";
    public static final String DEFAULT_ZYOZAI_NUM = "defaultZyozaiNum";
    public static final String DEFAULT_MIZUYAKU_NUM = "defaultMizuyakuNum";
    public static final String DEFAULT_SANYAKU_NUM = "defaultSanyakuNum";
    public static final String DEFAULT_CAPSULE_NUM = "defaultCapsuleNum";
    public static final String DEFAULT_RP_NUM = "defaultRpNum";
    public static final String DEFAULT_RP_OUT = "rp.out";
    public static final String ORDER_TABLE_CLICK_COUNT_TO_START = "order.table.clickCountToStart";
    public static final String MASTER_SEARCH_REALTIME = "masterSearch.realTime";
    public static final String MASTER_SEARCH_PARTIAL_MATCH = "masterSearch.partialMatch";
    public static final String MASTER_SEARCH_ITEM_COLORING = "masterItemColoring";

    // 紹介状等
    public static final String LETTER_ATESAKI_TITLE = "letter.atesaki.title";
    public static final String LETTER_INCLUDE_GREETINGS = "letter.greetings.include";
    public static final String PLAIN_PRINT_PATIENT_NAME = "plain.print.patinet.name";
    public static final String LOCATION_PDF = "pdfStore"; // PDF 出力ディレクトリー
    public static final String SHINDANSYO_FONT_SIZE = "sindansyo.font.size";
//s.oh^ 2013/11/26 文書の電話出力対応
    public static final String LETTER_TELEPHONE_OUTPUTPDF = "letter.telephone.outputpdf";
//s.oh$

    // 医療資格
    private static final String LICENSE_DOCTOR = "doctor";

    // 受付 Relay
    public static final String PVT_RELAY = "pvt.relay";
    public static final String PVT_RELAY_DIRECTORY = "pvt.relay.directory";
    public static final String PVT_RELAY_ENCODING = "pvt.relay.encoding";
    public static final String PVT_RELAY_NAME = "pvt.relay.name";   // lsc,fev....

    //Hiro Clinic 処方せん出力のための保険医療機関コード
    public static final String FACILITY_CODE_OF_INSURNCE_SYSTEM = "facility.code.insurance.system";

    //適用保険のカラーリング
    public static final String INSURANCE_SELF = "insurance.self";
    public static final String INSURANCE_ROSAI_PREFIX = "insurance.rosai.prefix";
    public static final String INSURANCE_JIBAISEKI_PREFIX = "insurance.jibaiseki.prefix";

    //2号カルテ/文字サイズ
    public static final String FONT_SIZE = "karte.font.size.default";
    //カルテ展開時にスタンプ名を表示する
    public static final String DEFAULT_STAMP_NAME = "karte.show.stampName";

    //修飾キー
    public static final String ENTITY_CODE_MODEFIER = "modifier";
    //キーワード
    public static final String ENTITY_CODE_TEXT = "text";
    //パス
    public static final String ENTITY_CODE_PATH = "path";
    //汎用
    public static final String ENTITY_CODE_GENERAL_ORDER = "generalOrder";
    //その他
    public static final String ENTITY_CODE_OTHER_ORDER = "otherOrder";
    //処置
    public static final String ENTITY_CODE_TREATMEN = "treatmentOrder";
    //手術
    public static final String ENTITY_CODE_SURGERY_ORDER = "surgeryOrder";
    //放射線
    public static final String ENTITY_CODE_RADIOLOGY_ORDER = "radiologyOrder";
    //検体検査
    public static final String ENTITY_CODE_LABO_TEST = "testOrder";
    //生体検査
    public static final String ENTITY_CODE_PHYSIOLOGY_ORDER = "physiologyOrder";
    //細菌検査
    public static final String ENTITY_CODE_BACTERIA_ORDER = "bacteriaOrder";
    //注射
    public static final String ENTITY_CODE_INJECTION_ORDER = "injectionOrder";
    //処方
    public static final String ENTITY_CODE_MED_ORDER = "medOrder";
    //診断料
    public static final String ENTITY_CODE_BASE_CHARGE_ORDER = "baseChargeOrder";
    //指導・在宅
    public static final String ENTITY_CODE_INSTRACTION_CHARGE_ORDER = "instractionChargeOrder";
    //ORCA
    public static final String ENTITY_CODE_ORCA = "orcaSet";

    //- Evolution 
    //- 表示名
    public static final String EVO_DISP = "evoDisp";
    //- URL
    public static final String EVO_URL = "evoUrl";

    // 切り株
    private static ProjectStub stub;

    /**
     * Creates new Project
     */
    public Project() {
    }

    public static void setProjectStub(ProjectStub p) {
        stub = p;
    }

    public static ProjectStub getProjectStub() {
        return stub;
    }

    public static boolean isValid() {
        return stub.isValid();
    }

    public static UserModel getUserModel() {
        return stub.getUserModel();
    }

    public static void setUserModel(UserModel value) {
        stub.setUserModel(value);
    }

    public static String getFacilityId() {
        return stub.getFacilityId();
    }

    public static String getUserId() {
        return stub.getUserId();
    }

    public static boolean isReadOnly() {
        String licenseCode = stub.getUserModel().getLicenseModel().getLicense();
        return !licenseCode.equals(LICENSE_DOCTOR);
    }

    public static String getEvoDisp() {
        return stub.getEvoDisp();
    }

    public static String getEvoUrl() {
        return stub.getEvoUrl();
    }

    public static String getBaseURI() {
        return stub.getBaseURI();
    }
    
    public static String getServerURI() {
        return stub.getServerURI();
    }

    //- 有効なアドレスかどうか
    private static boolean claimAddressIsValid() {
        // null empty のみチェック ToDo
        String host = stub.getString(CLAIM_ADDRESS);
        return (host != null && (!host.equals("")));
    }

    /**
     * ProjectFactoryを返す。
     *
     * @return Project毎に異なる部分の情報を生成するためのFactory
     */
    public static AbstractProjectFactory getProjectFactory() {
        return AbstractProjectFactory.getProjectFactory(stub.getName());
    }

    /**
     * 地域連携用の患者MasterIdを返す。
     *
     * @param pid
     * @return 地域連携で使用する患者MasterId
     */
    public static ID getMasterId(String pid) {
        String fid = Project.getString(Project.AREA_NETWORK_FACILITY_ID);   //stub.getAreaNetworkFacilityId();
        return getProjectFactory().createMasterId(pid, fid);
    }

    /**
     * CLAIM送信に使用する患者MasterIdを返す。地域連携ルールと異なるため。
     *
     * @param pid
     * @return
     */
    public static ID getClaimMasterId(String pid) {
        return new ID(pid, "facility", "MML0024");
    }

    /**
     * CSGW(Client Side Gate Way)へのパスを返す。
     *
     * @return
     */
    public static String getCSGWPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Project.getString(Project.SEND_MML_DIRECTORY));
        sb.append(File.separator);
        sb.append(Project.getString(Project.JMARI_CODE));
        return sb.toString();
    }

    public static Properties getUserDefaults() {
        return stub.getUserDefaults();
    }

    public static void saveUserDefaults() {
        stub.saveUserDefaults();
    }

    public static void loadProperties(Properties prop, String name) {
        stub.loadProperties(prop, name);
    }

    public static void storeProperties(Properties prop, String name) {
        stub.storeProperties(prop, name);
    }

    public static Properties loadPropertiesAsObject(String name) {
        return stub.loadPropertiesAsObject(name);
    }

    public static void storePropertiesAsObject(Properties prop, String name) {
        stub.storePropertiesAsObject(prop, name);
    }

    public static boolean deleteSettingFile(String file) {
        return stub.deleteSettingFile(file);
    }

    public static String getString(String key) {
        return stub.getString(key);
    }

    public static String getString(String key, String defStr) {
        return stub.getString(key, defStr);
    }

    public static void setString(String key, String value) {
        stub.setString(key, value);
    }

    public static String[] getStringArray(String key) {
        return stub.getStringArray(key);
    }

    public static String[] getStringArray(String key, String[] defStr) {
        return stub.getStringArray(key, defStr);
    }

    public static void setStringArray(String key, String[] value) {
        stub.setStringArray(key, value);
    }

    public static Rectangle getRectangle(String key) {
        return stub.getRectangle(key);
    }

    public static Rectangle getRectangle(String key, Rectangle defRect) {
        return stub.getRectangle(key, defRect);
    }

    public static void setRectangle(String key, Rectangle value) {
        stub.setRectangle(key, value);
    }

    public static Color getColor(String key) {
        return stub.getColor(key);
    }

    public static Color getColor(String key, Color defVal) {
        return stub.getColor(key, defVal);
    }

    public static void setColor(String key, Color value) {
        stub.setColor(key, value);
    }

    public static int getInt(String key) {
        return stub.getInt(key);
    }

    public static int getInt(String key, int defVal) {
        return stub.getInt(key, defVal);
    }

    public static void setInt(String key, int value) {
        stub.setInt(key, value);
    }

    public static float getFloat(String key) {
        return stub.getFloat(key);
    }

    public static float getFloat(String key, float defVal) {
        return stub.getFloat(key, defVal);
    }

    public static void setFloat(String key, float value) {
        stub.setFloat(key, value);
    }

    public static double getDouble(String key) {
        return stub.getDouble(key);
    }

    public static double getDouble(String key, double defVal) {
        return stub.getDouble(key, defVal);
    }

    public static void setDouble(String key, double value) {
        stub.setDouble(key, value);
    }

    public static boolean getBoolean(String key) {
        return stub.getBoolean(key);
    }

    public static boolean getBoolean(String key, boolean defVal) {
        return stub.getBoolean(key, defVal);
    }

    public static void setBoolean(String key, boolean value) {
        stub.setBoolean(key, value);
    }

    public static String getDefaultString(String key) {
        return stub.getDefaultString(key);
    }

    public static String[] getDefaultStringArray(String key) {
        return stub.getDefaultStringArray(key);
    }

    public static Rectangle getDefaultRectangle(String key) {
        return stub.getDefaultRectangle(key);
    }

    public static Color getDefaultColor(String key) {
        return stub.getDefaultColor(key);
    }

    public static int getDefaultInt(String key) {
        return stub.getDefaultInt(key);
    }

    public static float getDefaultFloat(String key) {
        return stub.getDefaultFloat(key);
    }

    public static double getDefaultDouble(String key) {
        return stub.getDefaultDouble(key);
    }

    public static boolean getDefaultBoolean(String key) {
        return stub.getDefaultBoolean(key);
    }

//新宿ヒロクリニック 処方せん印刷^ Propertiesへ保存するように変更
    /**
     * スタブに設定されている医療機関基本情報を返却する
     *
     * @return 医療機関基本情報 String
     */
    // @002 2010/07/16
    public static String getBasicInfo() {
        return stub.getString(FACILITY_CODE_OF_INSURNCE_SYSTEM);
    }

    /**
     * スタブに医療機関基本情報を設定
     *
     * @param basicInfo 医療機関基本情報
     */
    // @002 2010/07/16
    public static void setBasicInfo(String basicInfo) {
        stub.setString(FACILITY_CODE_OF_INSURNCE_SYSTEM, basicInfo);
    }
//新宿ヒロクリニック$    
}
