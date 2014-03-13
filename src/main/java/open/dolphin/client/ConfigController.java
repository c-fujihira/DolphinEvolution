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

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import open.dolphin.project.Project;
import open.dolphin.project.ProjectStub;

/**
 * Config FXML Controller Class
 *
 * @author Chikara Fujihira <fujihirach@sandi.co.jp>
 */
public class ConfigController extends AnchorPane implements Initializable {

    //- 保存ステータス
    @FXML
    Text status;

    //- サーバ
    //- 医療機関ID
    @FXML
    TextField svJmariCode;
    //- ユーザーID
    @FXML
    TextField svUserId;
    //- ベースURI
    @FXML
    TextField svBaseURI;

    //- レセコン
    //- CLAIM(請求データ)送信設定 - 診療行為送信 
    @FXML
    RadioButton receClaimTrans;
    @FXML
    RadioButton recClaimNotTrans;
    //- レセコン設定 機種
    @FXML
    ChoiceBox recClaimModel;
    //- CLAIM診療科コード
    @FXML
    CheckBox recClaimCode;
    //- 医療機関ID
    @FXML
    TextField recJpnCode;

    //- カルテ/インスペクタ/画面表示位置
    //- プラットフォーム制御
    //- 位置と大きさを記憶する
    @FXML
    RadioButton insPlatform;
    @FXML
    RadioButton insPosition;
    //- シェーマデータ
    @FXML
    RadioButton insCoolEditor;
    @FXML
    RadioButton insSimpleEditor;
    //- 予定カルテ
    @FXML
    CheckBox insKarteSchedule;

    //- 文書/カルテ    
    //- 文書履歴
    @FXML
    RadioButton docAscending;
    @FXML
    RadioButton docDescending;
    //- 自動文書取得数
    @FXML
    ChoiceBox docAutoGetnum;
    //- スクロール方向
    @FXML
    RadioButton docPerpendicular;
    @FXML
    RadioButton docLevel;
    //- 文書抽出時間
    @FXML
    ChoiceBox docIntVal;

    //- 傷病名/表示順
    @FXML
    RadioButton docDicknessAscending;
    @FXML
    RadioButton docSicknessDescending;
    //- 抽出時間
    @FXML
    ChoiceBox docSicknessTime;
    @FXML
    CheckBox docSicknessActive;
    //- 転帰入力
    @FXML
    CheckBox docOutcomeDate;
    //- 入力する日
    @FXML
    ChoiceBox docEnddate;

    //- 診療行為/カルテ保存時に設定するタイトル
    //- カルテの先頭15文字を使用する
    @FXML
    CheckBox medResHead;
    //- 経過記録
    @FXML
    TextField medResProgressnote;

    //- 診療行為送信のデフォルトチェック/保存時
    @FXML
    RadioButton medResSaveTransmit;
    @FXML
    RadioButton medResSaveNoTransmit;
    //- 修正時
    @FXML
    RadioButton medResTransmitMod; // Modify_Transmit
    @FXML
    RadioButton medResTransmitNoMod; // Modify_NoTrasmit
    //- 予定カルテの場合
    @FXML
    RadioButton medResScheduleTransmit;
    @FXML
    RadioButton medResScheduleNotTransmit;

    //- 保存ボタン押下時の診療行為送信(除く予定カルテ)
    @FXML
    RadioButton medResTemporarySelect;
    @FXML
    RadioButton medResTemporaryNotTransmit;

    //- 併用禁忌チェック
    @FXML
    CheckBox medResCombined;

    //- 適用保険カラーリング
    //- 実費
    @FXML
    CheckBox medResCostPrice;
    //- 労災
    @FXML
    CheckBox medResAccident;
    //- 自賠責
    @FXML
    CheckBox medResInsurance;

    //- その他 新規カルテ作成時
    //- 確認ダイアログ
    @FXML
    CheckBox etcDialog;
    //- 作成方法
    @FXML
    RadioButton etcAltogether;
    @FXML
    RadioButton etcLastPrescription;
    @FXML
    RadioButton etcBlankNewKarte;
    //- 配置方法
    @FXML
    RadioButton etcAnotherWindow;
    @FXML
    RadioButton etcTabPanel;

    //- カルテ保存時
    //- 編集ウィンドウを自動的に閉じる
    @FXML
    CheckBox etcClosesAutomatically;
    //- 確認ダイアログを表示しない
    @FXML
    CheckBox etcKarteSaveDialog;
    //- 印刷枚数
    @FXML
    TextField etcPrintingSheets;
    //- 動作
    @FXML
    RadioButton etcOperationAllcopy;
    @FXML
    RadioButton etcOperationPrescription;
    //- 年齢
    @FXML
    TextField etcAgeOfTheMoon;
    //- 2号カルテ
    @FXML
    RadioButton etcFontSmall;
    @FXML
    RadioButton etcFontMiddle;
    @FXML
    RadioButton etcFontLarge;

    //- 印刷/印刷方法
    @FXML
    RadioButton printWindows;
    @FXML
    RadioButton printWindowsMac;
    //- 印刷ダイアログ
    @FXML
    CheckBox printView;
    //- PDF
    @FXML
    CheckBox pdfView;
    @FXML
    TextField pdfViewTextSize;
    //- ラボテストのリスト印刷
    @FXML
    CheckBox laboPrintConvPdf;

    //- スタンプ/スタンプ動作設定
    //- スタンプ上にDnDした場合
    @FXML
    RadioButton stampReplaces;
    @FXML
    RadioButton stampWarning;
    //- DnD時にスタンプの間隔をあける
    @FXML
    CheckBox stampDnd;
    //- 検体検査の項目を折りたたみ表示する
    @FXML
    CheckBox stampSpecimen;
    //- スタンプ展開時にスタンプ名を表示する
    @FXML
    CheckBox stampStampname;

    //- スタンプ動作設定
    //- 錠剤
    @FXML
    TextField stampTablet;
    //- 水薬
    @FXML
    TextField stampLiquidmedicine;
    //- 散役
    @FXML
    TextField stampPowder;
    //- カプセル
    @FXML
    TextField stampCapsule;
    //- 処方日数
    @FXML
    TextField stampPrescriptiondays;

    //- 処方/同じ用法にまとめる
    @FXML
    CheckBox stampDirection;
    //- スタンプエディタのボタンタイプ
    @FXML
    RadioButton stampIcon;
    @FXML
    RadioButton stampText;
    //- マスター検索
    //- マスター項目をカラーリングする
    @FXML
    CheckBox stampMaster;
    //- 検体検査
    //- 同じ検体検査をまとめる
    @FXML
    CheckBox mergeWithLabotest;

    //- 紹介状/診療情報提供書
    //- 宛先敬称
    @FXML
    RadioButton introGokika;
    @FXML
    RadioButton introGozisi;
    @FXML
    RadioButton introNothing;
    //- PDF印刷時のあいさつ文
    @FXML
    CheckBox introLetterPdf;
    //- 電話番号の出力
    @FXML
    RadioButton lerrerTelOufPdfOn;
    @FXML
    RadioButton lerrerTelOufPdfOff;

    //- ブレイン文書
    //- 宛先敬称
    @FXML
    RadioButton introLetterOn;
    @FXML
    RadioButton introLetterOff;
    //- 診断書
    //- フォントサイズ
    @FXML
    RadioButton introLetterSmall;
    @FXML
    RadioButton introLetterBig;

    //- 出力先
    @FXML
    TextField introOutput;
    @FXML
    RadioButton introSetDirNameOutPut;

    //- コード/修飾キー   
    @FXML
    RadioButton codeControl;
    @FXML
    RadioButton codeMeta;
    //- スタンプ箱のキーワード
    //- テキスト
    @FXML
    TextField codeTx;
    //- 汎用
    @FXML
    TextField codeGen;
    //- 処置
    @FXML
    TextField codeTr;
    //- 放射線
    @FXML
    TextField codeRad;
    //- 生体検査
    @FXML
    TextField codePhy;
    //- 注射
    @FXML
    TextField codeInj;
    //- 診断料
    @FXML
    TextField codeBase;
    //- ORCA
    @FXML
    TextField codeOrca;
    //- パス
    @FXML
    TextField codePat;
    //- その他
    @FXML
    TextField codeOth;
    //- 手術
    @FXML
    TextField codeSur;
    //- 生体検査
    @FXML
    TextField codeLab;
    //- 細菌検査
    @FXML
    TextField codeBac;
    //- 処方
    @FXML
    TextField codeRp;
    //- 指導・在宅
    @FXML
    TextField codeIns;

    //- リレー等/MML出力
    //- カルテ保存時にMML出力を行う
    @FXML
    CheckBox relayMml;
    //- 出力先ディレクトリ
    @FXML
    TextField relayMmlOutput;
    //- 選択
    @FXML
    Button relayMmlSelect;
    //- バージョン
    @FXML
    RadioButton relayMmlVersion;
    @FXML
    RadioButton relayMmlVersionThree;
    //- PDF出力/保存時にPDF出力を行う
    @FXML
    CheckBox relayPdfCheck;
    //- 出力先ディレクトリ
    @FXML
    TextField relayPdfOutput;
    //- 選択
    @FXML
    Button relayPdfSelect;
    //- MML出力
    //このマシンで受付情報をリレーする
    @FXML
    CheckBox relayRelay;
    //- MML出力
    @FXML
    TextField relayOutput;
    //- 選択
    @FXML
    Button relaySelect;
    //- エンコーディング
    @FXML
    RadioButton relayEncUtf;
    @FXML
    RadioButton relayEncShift;
    @FXML
    RadioButton relayEncEuc;

    //- 読み込みファイル
    @FXML
    Label choiceFile;
    
    //- Evolution
    //- 表示名
    @FXML
    TextField evoDsp;
    //- URL
    @FXML
    TextField evoUrl;

    //- 親インスタンス
    private Stage mainStage;
    private Evolution application;

    ClientContextStub stub;

    public ConfigController() {
        this.stub = new ClientContextStub();
    }

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        status.setText("");

        //サーバタブ
        //完了・キャンセル・適用
        //jmaricode
        svJmariCode.setText(Project.getString(Project.FACILITY_ID));
        //userID 
        svUserId.setText(Project.getString(Project.USER_ID));
        //ServerURI
        svBaseURI.setText(Project.getString(Project.SERVER_URI));

        recClaimModel.setItems(FXCollections.observableArrayList("日医標準レセコン(ORCA)"));
        //レセコン
        //機種
        setRecClaimModelChoiceBox();
        //JPNコード
        setRecJpnCodeTextField();
        //CLAIM診療科コード
        setClaimCodeCheckBox();
        //CLAIM送信
        setReceClaimTransRadioButton();
        //カルテ/インスペクタ
        //画面表示
        setInsPlatformRadioButton();
        //シェーマエディタ
        setInsSimpleEditorRadioButton();
        //予定カルテ
        setInsKarteScheduleCheckBox();
        //カルテ/文書
        //表示順
        setDocAscendingRadioButton();
        //自動文書取得数
        setDocAutoGetnumChoiceBox();
        //スクロール方向
        setDocPerpendicularRadioButton();
        //文書抽出期間
        setDocIntValChoiceBox();
        //傷病名
        //表示順
        setDocDicknessAscendingRadioButton();
        //抽出期間
        setDocSicknessTimeChoiceBox();
        //アクティブ病名のみ使用する
        setDocSicknessActiveCheckBox();
        //転帰入力
        setDocOutcomeDateCheckBox();
        //終了日を自動入力する
        setDocEnddateChoiceBox();

        //診療行為
        //先頭の15文字を使用する
        setMedResHeadCheckBox();
        //経過記録
        setMedResProgressnoteTextField();
        //保存時
        setMedResSaveTransmitRadioButton();
        //修正時
        setMedResTransmitModRadioButton();
        //予定カルテ
        setMedResScheduleTransmitRadioButton();
        //仮保存ボタン押下時
        setMedResTemporarySelectRadioBotton();
        //併用禁忌
        setMedResCombinedCheckBox();
        //実費
        setMedResCostPriceCheckBox();
        //労災
        setMedResAccidentCheckBox();
        //自賠責
        setMedResInsuranceCheckBox();

        //新規カルテ作成時/確認ダイアログを表示しない
        setEtcDialogCheckBox();
        //作成方法
        setEtcAltogetherRadioButton();
        //配置方法
        setEtcTabPanelRadioButton();
        //カルテ保存時/編集ウィンドウを自動的に閉じる
        setEtcClosesAutomaticallyCheckBox();
        //確認ダイアログ
        setEtcKarteSaveDialogCheckBox();
        //印刷枚数
        setEtcPrintingSheetsTextField();
        //動作
        setEtcOperationAllcopyRadioButton();
        //月例を表示する年齢
        setEtcAgeOfTheMoonTextField();
        //文字サイズ
        setEtcFontSizeRadioButton();

        //- 印刷方法/種類
        setPrintRadioButton();
        //- 印刷ダイアログ
        setPrintViewCheckBox();
        //- 印刷/Windows/Mac印刷をした場合、印刷せずにPDFの表示を行う
        setPdfViewCheckBox();
        //- 印刷/デフォルト文字サイズ
        setPdfViewTextSizeTextField();
        //- 印刷/ラボテストのリスト印刷
        setLabTestPdfConCheckBox();

        //スタンプ/スタンプ上にDnDした場合のメソッド
        setStampReplacesRadioButton();
        //間隔を空けるのメソッド
        setStampDndCheckBox();
        //折りたたみ表示のメソッド
        setStampSpecimenCheckBox();
        //カルテ展開時にスタンプ名を表示する
        setStampStampnameCheckBox();
        //錠剤の場合のメソッド
        setStampTabletTextField();
        //水薬の場合のメソッド
        setStampLiquidmedicineTextField();
        //散薬の場合のメソッド
        setStampPowderTextField();
        //カプセルの場合のメソッド
        setStampCapsuleTextField();
        //処方日数のメソッド
        setStampPrescriptiondaysTextField();
        //処方/同じ用法をまとめるメソッド
        setStampDirectionCheckBox();
        //マスター項目をカラーリングする
        setStampMasterCheckBox();
        //同じ検体検査をまとめる
        setMergeWithLaboTestCheckBox();
        //スタンプエディタのボタンメソッド
        setStampTextRadioButton();

        //宛先継承
        setIntroGokikaRadioButton();
        //PDF印刷時の挨拶文
        setIntroLetterPdfCheckBox();
        //電話番号出力
        setIntroLetterTelRadioButton();
        //プレイン文書
        setIntroLetterOnRadioButton();
        //診断書/フォントサイズ
        setIntroLetterFontSize();
        //出力先ディレクトリ
        setIntroOutputTextField();

        //修飾キー
        setCodeControlRadioButton();
        //スタンプ箱のキーワードのメソッド
        setCodesTextField();

        //MML出力
        setRelayMmlCheckBox();
        //MML出力先ディレクトリ
        setRelayMmlOutputTextField();
        //バージョン
        setRelayMmlVersionRadioButton();
        //PDF出力
        setRelayPdfCheckCheckBox();
        //PDF出力先ディレクトリ
        setRelayPdfOutputTextField();
        //受付情報リレー
        setRelayRelayCheckBox();
        //受付情報出力先ディレクトリ
        setRelayOutputTextField();
        //エンコーディング
        setRelayEncRadioButton();
        
        //- Evolution
        //- 表示名
        setEvoDispTextField();
        //- URL
        setEvoUrlTextField();
        
    }

    //レセコン
    // CLAIM(請求データ)送信設定 - 診療行為送信
    public void setReceClaimTransRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.SEND_CLAIM))) {
            receClaimTrans.setSelected(true);
        } else {
            recClaimNotTrans.setSelected(true);
        }
    }

    //- 機種
    public void setRecClaimModelChoiceBox() {
        recClaimModel.getSelectionModel().clearSelection();
        recClaimModel.getItems().clear();
        recClaimModel.getItems().add("日医標準レセコン(ORCA)");
        recClaimModel.setValue("日医標準レセコン(ORCA)");
    }

    //デフォルト01を使用する
    public void setClaimCodeCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.CLAIM_01))) {
            recClaimCode.setSelected(true);
        }
    }

    //JPNコード
    public void setRecJpnCodeTextField() {
        String jmariPattern = "[0-9]{0,12}$";
        String exp = "";
        String str = Project.getString(Project.JMARI_CODE);

        try {
            Pattern p = Pattern.compile(jmariPattern);
            Matcher m = p.matcher(str);
            if (m.find()) {
                exp = m.group();
            }
        } catch (Exception e) {
            System.out.println("JMARI_CODE Match Error." + e.toString());
        }

        recJpnCode.setText(exp);
    }

    public void setInsPlatformRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.LOCATION_BY_PLATFORM))) {
            insPlatform.setSelected(true);
        } else {
            insPosition.setSelected(true);
        }
    }

    //シェーマエディタ
    public void setInsSimpleEditorRadioButton() {
        if (Project.getString(Project.SCEMA_EDITOR).equals("simple")) {
            insSimpleEditor.setSelected(true);
        } else {
            insCoolEditor.setSelected(true);
        }
    }

    //予定カルテを使用する
    public void setInsKarteScheduleCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.CONTAINER))) {
            insKarteSchedule.setSelected(true);
        } else {
            insKarteSchedule.setSelected(false);
        }
    }

    //文書/カルテ
    //文書履歴
    public void setDocAscendingRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.DOC_HISTORY_ASCENDING))) {
            docAscending.setSelected(true);
        } else {
            docDescending.setSelected(true);
        }
    }

    //自動文書取得数
    public void setDocAutoGetnumChoiceBox() {
        docAutoGetnum.getSelectionModel().clearSelection();
        docAutoGetnum.getItems().clear();
        docAutoGetnum.getItems().add("1");
        docAutoGetnum.getItems().add("2");
        docAutoGetnum.getItems().add("3");
        docAutoGetnum.getItems().add("4");
        docAutoGetnum.getItems().add("5");
        docAutoGetnum.getItems().add("6");
        docAutoGetnum.getItems().add("7");
        docAutoGetnum.getItems().add("8");
        docAutoGetnum.getItems().add("9");
        docAutoGetnum.getItems().add("10");
        docAutoGetnum.setValue(Project.getString(Project.DOC_HISTORY_FETCHCOUNT));
    }

    //スクロール方向
    public void setDocPerpendicularRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.KARTE_SCROLL_DIRECTION))) {
            docPerpendicular.setSelected(true);
        } else {
            docLevel.setSelected(true);
        }
    }

    //抽出期間
    public void setDocIntValChoiceBox() {
        String period = "0";
        docIntVal.getSelectionModel().clearSelection();
        docIntVal.getItems().clear();
        docIntVal.getItems().add("全て");
        docIntVal.getItems().add("1年");
        docIntVal.getItems().add("2年");
        docIntVal.getItems().add("3年");
        docIntVal.getItems().add("4年");
        docIntVal.getItems().add("5年");
        switch (Project.getString(Project.DOC_HISTORY_PERIOD)) {
            case "-12":
                period = "1年";
                break;
            case "-24":
                period = "2年";
                break;
            case "-36":
                period = "3年";
                break;
            case "-48":
                period = "4年";
                break;
            case "-60":
                period = "5年";
                break;
            case "-120":
                period = "全て";
                break;
        }
        docIntVal.setValue(period);
    }

    //傷病名/表示順
    public void setDocDicknessAscendingRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.DIAGNOSIS_ASCENDING))) {
            docDicknessAscending.setSelected(true);
        } else {
            docSicknessDescending.setSelected(true);
        }
    }

    //抽出時間
    public void setDocSicknessTimeChoiceBox() {
        String setItem = "1年";
        docSicknessTime.getSelectionModel().clearSelection();
        docSicknessTime.getItems().clear();
        docSicknessTime.getItems().add("全て");
        docSicknessTime.getItems().add("1年");
        docSicknessTime.getItems().add("2年");
        docSicknessTime.getItems().add("3年");
        docSicknessTime.getItems().add("5年");
        switch (Project.getString(Project.DIAGNOSIS_PERIOD)) {
            case "-12":
                setItem = "1年"; // -12
                break;
            case "-24":
                setItem = "2年"; // -24
                break;
            case "-36":
                setItem = "3年"; // -36
                break;
            case "-60":
                setItem = "5年"; // -60
                break;
            case "0":
                setItem = "全て"; // 0
                break;
        }
        docSicknessTime.setValue(setItem);
    }

    //アクティブ病名のみ使用する
    public void setDocSicknessActiveCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.DIAGNOSIS_ACTIVE_ONLY))) {
            docSicknessActive.setSelected(true);
        } else {
            docSicknessActive.setSelected(false);
        }
    }

    //転帰入力時
    public void setDocOutcomeDateCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.DIAGNOSIS_AUTO_OUTCOME_INPUT))) {
            docOutcomeDate.setSelected(true);
            docEnddate.setDisable(false);
        } else {
            docOutcomeDate.setSelected(false);
            docEnddate.setDisable(true);
        }
    }

    //disable
    public void setDocEnddateDisable() {
        if (docOutcomeDate.isSelected()) {
            docEnddate.setDisable(false);
        } else {
            docEnddate.setDisable(true);
        }
    }

    //入力する日
    public void setDocEnddateChoiceBox() {
        docEnddate.getSelectionModel().clearSelection();
        docEnddate.getItems().clear();
        docEnddate.getItems().add("-1");
        docEnddate.getItems().add("-2");
        docEnddate.getItems().add("-3");
        docEnddate.getItems().add("-4");
        docEnddate.getItems().add("-5");
        docEnddate.getItems().add("-6");
        docEnddate.getItems().add("-7");
        docEnddate.getItems().add("-8");
        docEnddate.getItems().add("-9");
        docEnddate.getItems().add("-10");
        docEnddate.getItems().add("-11");
        docEnddate.getItems().add("-12");
        docEnddate.getItems().add("-13");
        docEnddate.getItems().add("-14");
        docEnddate.getItems().add("-15");
        docEnddate.getItems().add("-16");
        docEnddate.getItems().add("-17");
        docEnddate.getItems().add("-18");
        docEnddate.getItems().add("-19");
        docEnddate.getItems().add("-20");
        docEnddate.getItems().add("-21");
        docEnddate.getItems().add("-22");
        docEnddate.getItems().add("-23");
        docEnddate.getItems().add("-24");
        docEnddate.getItems().add("-25");
        docEnddate.getItems().add("-26");
        docEnddate.getItems().add("-27");
        docEnddate.getItems().add("-28");
        docEnddate.getItems().add("-29");
        docEnddate.getItems().add("-30");
        docEnddate.getItems().add("-31");
        docEnddate.setValue(Project.getString(Project.OFFSET_OUTCOME_DATE));
    }

    //診療行為/カルテ保存時の設定するタイトル
    //カルテの先頭15文字を使用する
    public void setMedResHeadCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.KARTE_USE_TOP15_AS_TITLE))) {
            medResHead.setSelected(true);
            medResProgressnote.setDisable(true);
        } else {
            medResHead.setSelected(false);
            medResProgressnote.setDisable(false);
        }
    }

    //デフォルトタイトルdisable
    public void setMedResHeadDisable() {
        if (medResHead.isSelected()) {
            medResProgressnote.setDisable(true);
        } else {
            medResProgressnote.setDisable(false);
        }
    }

    //タイトル
    public void setMedResProgressnoteTextField() {
        medResProgressnote.setText(Project.getString(Project.KARTE_DEFAULT_TITLE));
    }

    //デフォルトチェック設定/保存時
    public void setMedResSaveTransmitRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.SEND_CLAIM_SAVE))) {
            medResSaveTransmit.setSelected(true);
        } else {
            medResSaveNoTransmit.setSelected(true);
        }
    }

    //修正時
    public void setMedResTransmitModRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.SEND_CLAIM_MODIFY))) {
            medResTransmitMod.setSelected(true);
        } else {
            medResTransmitNoMod.setSelected(true);
        }
    }

    //予定カルテ
    public void setMedResScheduleTransmitRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.SEND_CLAIM_EDIT_FROM_SCHEDULE))) {
            medResScheduleTransmit.setSelected(true);
        } else {
            medResScheduleNotTransmit.setSelected(true);
        }
    }

    //仮保存ボタン押下時
    public void setMedResTemporarySelectRadioBotton() {
        if (Boolean.valueOf(Project.getString(Project.SEND_CLAIM_DEPENDS_ON_CHECK_AT_TMP))) {
            medResTemporarySelect.setSelected(true);
        } else {
            medResTemporaryNotTransmit.setSelected(true);
        }
    }

    //併用禁忌チェック
    public void setMedResCombinedCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.INTERACTION_CHECK))) {
            medResCombined.setSelected(true);
        } else {
            medResCombined.setSelected(false);
        }
    }

    //適用保険
    public void setMedResCostPriceCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.INSURANCE_SELF))) {
            medResCostPrice.setSelected(true);
        } else {
            medResCostPrice.setSelected(false);
        }
    }

    //労災
    public void setMedResAccidentCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.INSURANCE_ROSAI_PREFIX))) {
            medResAccident.setSelected(true);
        } else {
            medResAccident.setSelected(false);
        }
    }

    //自賠責
    public void setMedResInsuranceCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.INSURANCE_JIBAISEKI_PREFIX))) {
            medResInsurance.setSelected(true);
        } else {
            medResInsurance.setSelected(false);
        }
    }

    //その他/新規カルテ
    public void setEtcDialogCheckBox() {
        if (!Boolean.valueOf(Project.getString(Project.KARTE_SHOW_CONFIRM_AT_NEW))) {
            etcDialog.setSelected(true);
            etcAltogether.setDisable(false);
            etcLastPrescription.setDisable(false);
            etcBlankNewKarte.setDisable(false);
            etcTabPanel.setDisable(false);
            etcAnotherWindow.setDisable(false);
        } else {
            etcDialog.setSelected(false);
            etcAltogether.setDisable(true);
            etcLastPrescription.setDisable(true);
            etcBlankNewKarte.setDisable(true);
            etcTabPanel.setDisable(true);
            etcAnotherWindow.setDisable(true);
        }
    }

    //新規カルテ作成時、作成方法、配置方法
    public void setEtcAltogetherDisable() {
        if (etcDialog.isSelected()) {
            etcAltogether.setDisable(false);
            etcLastPrescription.setDisable(false);
            etcBlankNewKarte.setDisable(false);
            etcTabPanel.setDisable(false);
            etcAnotherWindow.setDisable(false);
        } else {
            etcAltogether.setDisable(true);
            etcLastPrescription.setDisable(true);
            etcBlankNewKarte.setDisable(true);
            etcTabPanel.setDisable(true);
            etcAnotherWindow.setDisable(true);
        }
    }

    //作成方法
    public void setEtcAltogetherRadioButton() {
        switch (Project.getString(Project.KARTE_CREATE_MODE)) {
            case "2":
                etcAltogether.setSelected(true);
                break;
            case "1":
                etcLastPrescription.setSelected(true);
                break;
            case "0":
                etcBlankNewKarte.setSelected(true);
                break;
        }
    }

    //配置方法
    public void setEtcTabPanelRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.KARTE_PLACE_MODE))) {
            etcAnotherWindow.setSelected(true);
        } else {
            etcTabPanel.setSelected(true);
        }
    }

    //カルテ保存時/編集ウィンドウを自動的に閉じる
    public void setEtcClosesAutomaticallyCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.KARTE_AUTO_CLOSE_AFTER_SAVE))) {
            etcClosesAutomatically.setSelected(true);
        } else {
            etcClosesAutomatically.setSelected(false);
        }
    }

    //確認ダイアログ
    public void setEtcKarteSaveDialogCheckBox() {
        if (!Boolean.valueOf(Project.getString(Project.KARTE_SHOW_CONFIRM_AT_SAVE))) {
            etcKarteSaveDialog.setSelected(true);
            etcPrintingSheets.setDisable(false);
            etcOperationAllcopy.setDisable(false);
            etcOperationPrescription.setDisable(false);
        } else {
            etcKarteSaveDialog.setSelected(false);
            etcPrintingSheets.setDisable(true);
            etcOperationAllcopy.setDisable(true);
            etcOperationPrescription.setDisable(true);
        }
    }

    //確認ダイアログ、動作disable
    public void setEtcKarteSaveDialogDisable() {
        if (!etcKarteSaveDialog.isSelected()) {
            etcPrintingSheets.setDisable(false);
            etcOperationAllcopy.setDisable(false);
            etcOperationPrescription.setDisable(false);
        } else {
            etcPrintingSheets.setDisable(true);
            etcOperationAllcopy.setDisable(true);
            etcOperationPrescription.setDisable(true);
        }
    }

    //印刷枚数、動作disable
    public void setEtcPrintingSheetsDisable() {
        if (Boolean.valueOf(Boolean.toString(etcKarteSaveDialog.isSelected()))) {
            etcPrintingSheets.setDisable(false);
            etcOperationAllcopy.setDisable(false);
            etcOperationPrescription.setDisable(false);
        } else {
            etcPrintingSheets.setDisable(true);
            etcOperationAllcopy.setDisable(true);
            etcOperationPrescription.setDisable(true);
        }
    }

    //印刷枚数
    public void setEtcPrintingSheetsTextField() {
        etcPrintingSheets.setText(Project.getString(Project.KARTE_PRINT_COUNT));
    }

    //動作
    public void setEtcOperationAllcopyRadioButton() {
        switch (Project.getString(Project.KARTE_SAVE_ACTION)) {
            case "0":
                etcOperationAllcopy.setSelected(true);
                break;
            case "1":
                etcOperationPrescription.setSelected(true);
                break;
        }
    }

    //- 年齢
    public void setEtcAgeOfTheMoonTextField() {
        etcAgeOfTheMoon.setText(Project.getString(Project.KARTE_AGE_TO_NEED_MONTH));
    }

    //- 文字サイズ
    public void setEtcFontSizeRadioButton() {
        switch (Project.getString(Project.FONT_SIZE)) {
            case "12":
                etcFontSmall.setSelected(true);
                break;
            case "14":
                etcFontMiddle.setSelected(true);
                break;
            case "16":
                etcFontLarge.setSelected(true);
                break;
        }
    }

    //印刷
    //印刷方法
    public void setPrintRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.KARTE_PRINT_PDF))) {
            printWindowsMac.setSelected(true);
        } else {
            printWindows.setSelected(true);
        }
    }

    //Windows/Mac印刷を選択した場合、印刷ダイアログを表示しない
    public void setPrintViewCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.KARTE_PRINT_DIRECT))) {
            printView.setSelected(true);
        }
    }
    
    //Windows/Mac印刷を選択した場合、印刷せずにPDFを出力する
    public void setPdfViewCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.KARTE_PRINT_SHOWPDF))) {
            pdfView.setSelected(true);
        }
    }

    //デフォルト文字サイズ
    public void setPdfViewTextSizeTextField() {
        pdfViewTextSize.setText(Project.getString(Project.KARTE_PRINT_PDF_TEXTSIZE));
    }

    //ラボテストのリスト印刷
    public void setLabTestPdfConCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.LABO_PRINT_SHOWPDF))) {
            laboPrintConvPdf.setSelected(true);
        }
    }
    
    //スタンプ/スタンプ動作設定
    //スタンプ上にDnDした場合
    public void setStampReplacesRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.STAMP_REPLACE))) {
            stampReplaces.setSelected(true);
        } else {
            stampWarning.setSelected(true);
        }
    }

    //DnD時にスタンプの間隔を空ける
    public void setStampDndCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.STAMP_SPACE))) {
            stampDnd.setSelected(true);
        } else {
            stampDnd.setSelected(false);
        }
    }

    //検体検査の項目を折りたたみ表示する
    public void setStampSpecimenCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.LABTEST_FOLD))) {
            stampSpecimen.setSelected(true);
        } else {
            stampSpecimen.setSelected(false);
        }
    }

    //カルテ展開時にスタンプ名を表示する
    public void setStampStampnameCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.DEFAULT_STAMP_NAME))) {
            stampStampname.setSelected(true);
        } else {
            stampStampname.setSelected(false);
        }
    }

    //スタンプ動作設定/錠剤の場合
    public void setStampTabletTextField() {
        stampTablet.setText(Project.getString(Project.DEFAULT_ZYOZAI_NUM));
    }

    //水薬の場合
    public void setStampLiquidmedicineTextField() {
        stampLiquidmedicine.setText(Project.getString(Project.DEFAULT_MIZUYAKU_NUM));
    }

    //散薬の場合
    public void setStampPowderTextField() {
        stampPowder.setText(Project.getString(Project.DEFAULT_SANYAKU_NUM));
    }

    //カプセルの場合
    public void setStampCapsuleTextField() {
        stampCapsule.setText(Project.getString(Project.DEFAULT_CAPSULE_NUM));
    }

    //処方日数
    public void setStampPrescriptiondaysTextField() {
        stampPrescriptiondays.setText(Project.getString(Project.DEFAULT_RP_NUM));
    }

    //処方/同じ用法にまとめる
    public void setStampDirectionCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.KARTE_MERGE_RP_WITH_SAME_ADMIN))) {
            stampDirection.setSelected(true);
        } else {
            stampDirection.setSelected(false);
        }
    }

    //マスター項目をカラーリングする
    public void setStampMasterCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.MASTER_SEARCH_ITEM_COLORING))) {
            stampMaster.setSelected(true);
        } else {
            stampMaster.setSelected(false);
        }
    }
    
    //検体検査/同じ検体検査をまとめる
    public void setMergeWithLaboTestCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.KARTE_MERGE_WITH_LABTEST))) {
            mergeWithLabotest.setSelected(true);
        } else {
            mergeWithLabotest.setSelected(false);
        }
    }

    //スタンプエディタのボタン
    public void setStampTextRadioButton() {
        if (Project.getString(Project.STAMP_EDITOR_BUTTON_TYPE).equals("icon")) {
            stampIcon.setSelected(true);
        } else {
            stampText.setSelected(true);
        }
    }

    //紹介状/診療情報提供書
    //宛先継承
    public void setIntroGokikaRadioButton() {
        switch (Project.getString(Project.LETTER_ATESAKI_TITLE)) {
            case "御机下":
                introGokika.setSelected(true);
                break;
            case "御侍史":
                introGozisi.setSelected(true);
                break;
            case "無し":
                introNothing.setSelected(true);
                break;
        }
    }

    //PDF挨拶文
    public void setIntroLetterPdfCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.LETTER_INCLUDE_GREETINGS))) {
            introLetterPdf.setSelected(true);
        } else {
            introLetterPdf.setSelected(false);
        }
    }
    
    //電話番号出力
    public void setIntroLetterTelRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.LETTER_TELEPHONE_OUTPUTPDF))) {
            lerrerTelOufPdfOn.setSelected(true);
        } else {
            lerrerTelOufPdfOff.setSelected(true);
        }
    }

    //プレイン文書/宛先敬称
    public void setIntroLetterOnRadioButton() {
        if (Boolean.valueOf(Project.getString(Project.PLAIN_PRINT_PATIENT_NAME))) {
            introLetterOn.setSelected(true);
        } else {
            introLetterOff.setSelected(true);
        }
    }

    //診断書/フォントサイズ
    public void setIntroLetterFontSize() {
        switch (Project.getString(Project.SHINDANSYO_FONT_SIZE)) {
            case "large":
                introLetterBig.setSelected(true);
                break;
            case "small":
                introLetterSmall.setSelected(true);
                break;
        }
    }

    //出力先
    public void setIntroOutputTextField() {
        if (Project.getString(Project.LOCATION_PDF) == null
                || Project.getString(Project.LOCATION_PDF).isEmpty()) {
            String pdfDir = "pdf";
            if (ClientContext.isMac() || ClientContext.isLinux()) {
                introOutput.setText(this.stub.getBaseDirectory() + "/" + pdfDir);
            } else {
                introOutput.setText(this.stub.getBaseDirectory() + "\\" + pdfDir);
            }
        } else {
            introOutput.setText(Project.getString(Project.LOCATION_PDF));
        }
    }

    //コード
    //修飾キー+スペース=補完ポップアップ
    public void setCodeControlRadioButton() {
        switch (Project.getString(Project.ENTITY_CODE_MODEFIER)) {
            case "ctrl":
                codeControl.setSelected(true);
                break;
            case "meta":
                codeMeta.setSelected(true);
                break;
        }
    }

    //スタンプ箱のキーワード
    public void setCodesTextField() {
        codeTx.setText(Project.getString(Project.ENTITY_CODE_TEXT));
        codeGen.setText(Project.getString(Project.ENTITY_CODE_GENERAL_ORDER));
        codeTr.setText(Project.getString(Project.ENTITY_CODE_TREATMEN));
        codeRad.setText(Project.getString(Project.ENTITY_CODE_RADIOLOGY_ORDER));
        codePhy.setText(Project.getString(Project.ENTITY_CODE_PHYSIOLOGY_ORDER));
        codeInj.setText(Project.getString(Project.ENTITY_CODE_INJECTION_ORDER));
        codeBase.setText(Project.getString(Project.ENTITY_CODE_BASE_CHARGE_ORDER));
        codeOrca.setText(Project.getString(Project.ENTITY_CODE_ORCA));
        codePat.setText(Project.getString(Project.ENTITY_CODE_PATH));
        codeOth.setText(Project.getString(Project.ENTITY_CODE_OTHER_ORDER));
        codeSur.setText(Project.getString(Project.ENTITY_CODE_SURGERY_ORDER));
        codeLab.setText(Project.getString(Project.ENTITY_CODE_LABO_TEST));
        codeBac.setText(Project.getString(Project.ENTITY_CODE_BACTERIA_ORDER));
        codeRp.setText(Project.getString(Project.ENTITY_CODE_MED_ORDER));
        codeIns.setText(Project.getString(Project.ENTITY_CODE_INSTRACTION_CHARGE_ORDER));
    }

    //リレー等
    //MML出力
    public void setRelayMmlCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.SEND_MML))) {
            relayMml.setSelected(true);
            relayMmlOutput.setDisable(false);
            relayMmlVersion.setDisable(false);
            relayMmlVersionThree.setDisable(false);
            relayMmlSelect.setDisable(false);
        } else {
            relayMml.setSelected(false);
            relayMmlOutput.setDisable(true);
            relayMmlVersion.setDisable(true);
            relayMmlVersionThree.setDisable(true);
            relayMmlSelect.setDisable(true);
        }
    }

    //active
    public void setRelayIntroOutputDisable() {
        if (relayMml.isSelected()) {
            relayMmlOutput.setDisable(false);
            relayMmlVersion.setDisable(false);
            relayMmlVersionThree.setDisable(false);
            relayMmlSelect.setDisable(false);
        } else {
            relayMmlOutput.setDisable(true);
            relayMmlVersion.setDisable(true);
            relayMmlVersionThree.setDisable(true);
            relayMmlSelect.setDisable(true);
        }
    }

    //出力先ディレクトリ
    public void setRelayMmlOutputTextField() {
        relayMmlOutput.setText(Project.getString(Project.SEND_MML_DIRECTORY));
    }
    //バージョン

    public void setRelayMmlVersionRadioButton() {
        switch (Project.getString(Project.MML_VERSION)) {
            case "230":
                relayMmlVersion.setSelected(true);
                break;
            case "300":
                relayMmlVersionThree.setSelected(true);
                break;
        }
    }

    //カルテPDF出力
    public void setRelayPdfCheckCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.KARTE_PDF_SEND_AT_SAVE))) {
            relayPdfCheck.setSelected(true);
            relayPdfOutput.setDisable(false);
            relayPdfSelect.setDisable(false);
        } else {
            relayPdfCheck.setSelected(false);
            relayPdfOutput.setDisable(true);
            relayPdfSelect.setDisable(true);
        }
    }

    //出力先ディレクトリDisable
    public void setRelayKartePdfSendDirDisable() {
        if (Boolean.valueOf(Boolean.toString(relayPdfCheck.isSelected()))) {
            relayPdfOutput.setDisable(false);
            relayPdfSelect.setDisable(false);
        } else {
            relayPdfOutput.setDisable(true);
            relayPdfSelect.setDisable(true);
        }
    }

    //出力ディレクトリ
    public void setRelayPdfOutputTextField() {
        relayPdfOutput.setText(Project.getString(Project.KARTE_PDF_SEND_DIRECTORY));
    }

    //受付リレー
    public void setRelayRelayCheckBox() {
        if (Boolean.valueOf(Project.getString(Project.PVT_RELAY))) {
            relayRelay.setSelected(true);
            relayOutput.setDisable(false);
            relayEncUtf.setDisable(false);
            relayEncShift.setDisable(false);
            relayEncEuc.setDisable(false);
            relaySelect.setDisable(false);
        } else {
            relayRelay.setSelected(false);
            relayOutput.setDisable(true);
            relayEncUtf.setDisable(true);
            relayEncShift.setDisable(true);
            relayEncEuc.setDisable(true);
            relaySelect.setDisable(true);
        }
    }

    //受付情報出力Disable
    public void setPvtRelayDirectoryDisable() {
        if (relayRelay.isSelected()) {
            relayOutput.setDisable(false);
            relayEncUtf.setDisable(false);
            relayEncShift.setDisable(false);
            relayEncEuc.setDisable(false);
            relaySelect.setDisable(false);
        } else {
            relayOutput.setDisable(true);
            relayEncUtf.setDisable(true);
            relayEncShift.setDisable(true);
            relayEncEuc.setDisable(true);
            relaySelect.setDisable(true);
        }
    }

    //出力
    public void setRelayOutputTextField() {
        relayOutput.setText(Project.getString(Project.PVT_RELAY_DIRECTORY));
    }

    //エンコーディング
    public void setRelayEncRadioButton() {
        switch (Project.getString(Project.PVT_RELAY_ENCODING)) {
            case "utf8":
                relayEncUtf.setSelected(true);
                break;
            case "shiftjis":
                relayEncShift.setSelected(true);
                break;
            case "eucjp":
                relayEncEuc.setSelected(true);
                break;
        }
    }
    
    //- Evolution
    //- 表示名
    public void setEvoDispTextField() {
        evoDsp.setText(Project.getString(Project.EVO_DISP));
    }
    //- URL
    public void setEvoUrlTextField() {
        evoUrl.setText(Project.getString(Project.EVO_URL));
    }

    public void setApp(Evolution application) {
        this.application = application;
    }

    public Stage getStage() {
        return mainStage;
    }

    public void setStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void close(ActionEvent event) {
        application.Dialog.close();
    }

    public void applyAndClose(ActionEvent event) {

        //- 未使用設定
        Project.setString(Project.PVT_TIMER_CHECK, "false");
        Project.setString("karte.printCount", "0"); // オリジナル混入
        Project.setString("claimEncoding", "UTF-8"); // オリジナル混入

        //サーバー
        Project.setString(Project.FACILITY_ID, svJmariCode.getText());
        Project.setString(Project.USER_ID, svUserId.getText());
        Project.setString(Project.SERVER_URI, svBaseURI.getText());

        //レセコン
        Project.setString(Project.CLAIM_HOST_NAME, recClaimModel.getSelectionModel().getSelectedItem().toString());
        Project.setString(Project.SEND_CLAIM, String.valueOf(receClaimTrans.isSelected()));

        String jmariPattern = "[0-9]{0,12}$";
        String exp = "";
        String str = recJpnCode.getText();

        try {
            Pattern p = Pattern.compile(jmariPattern);
            Matcher m = p.matcher(str);
            if (m.find()) {
                exp = m.group();
            }
        } catch (Exception e) {
            System.out.println("JMARI_CODE Match Error." + e.toString());
        }

        Project.setString(Project.JMARI_CODE, "JPN" + exp);
        Project.setString(Project.CLAIM_01, String.valueOf(recClaimCode.isSelected()));

        //カルテ/インスペクタ
        //プラットフォーム
        Project.setString(Project.LOCATION_BY_PLATFORM, String.valueOf(insPlatform.isSelected()));

        //シェーマエディタ
        if (insSimpleEditor.isSelected()) {
            Project.setString(Project.SCEMA_EDITOR, "simple");
        } else {
            Project.setString(Project.SCEMA_EDITOR, "cool");
        }

        //予定カルテ機能を使用する
        Project.setString(Project.CONTAINER, String.valueOf(insKarteSchedule.isSelected()));

        Project.setString(Project.DOC_HISTORY_ASCENDING, String.valueOf(docAscending.isSelected()));

        //自動文書取得数
        Project.setString(Project.DOC_HISTORY_FETCHCOUNT, docAutoGetnum.getSelectionModel().getSelectedItem().toString());

        //スクロール方向
        Project.setString(Project.KARTE_SCROLL_DIRECTION, String.valueOf(docPerpendicular.isSelected()));

        //抽出期間
        String history = "0";
        switch (docIntVal.getSelectionModel().getSelectedItem().toString()) {
            case "1年":
                history = "-12";
                break;
            case "2年":
                history = "-24";
                break;
            case "3年":
                history = "-36";
                break;
            case "4年":
                history = "-48";
                break;
            case "5年":
                history = "-60";
                break;
            case "全て":
                history = "-120";
                break;
        }
        Project.setString(Project.DOC_HISTORY_PERIOD, history);

        //カルテ/表示順
        Project.setString(Project.DIAGNOSIS_ASCENDING, String.valueOf(docDicknessAscending.isSelected()));

        //抽出期間
        String sickness = "-12";
        switch (docSicknessTime.getSelectionModel().getSelectedItem().toString()) {
            case "1年":
                sickness = "-12";
                break;
            case "2年":
                sickness = "-24";
                break;
            case "3年":
                sickness = "-36";
                break;
            case "5年":
                sickness = "-60";
                break;
            case "全て":
                sickness = "0";
                break;
        }
        Project.setString(Project.DIAGNOSIS_PERIOD, sickness);

        //アクティブ病名のみ使用する
        Project.setString(Project.DIAGNOSIS_ACTIVE_ONLY, String.valueOf(docSicknessActive.isSelected()));

        //転帰入力
        Project.setString(Project.DIAGNOSIS_AUTO_OUTCOME_INPUT, String.valueOf(docOutcomeDate.isSelected()));

        //終了日を自動入力する
        Project.setString(Project.OFFSET_OUTCOME_DATE, docEnddate.getSelectionModel().getSelectedItem().toString());

        //診療行為/カルテの先頭15文字を使用する
        Project.setString(Project.KARTE_USE_TOP15_AS_TITLE, String.valueOf(medResHead.isSelected()));

        //経過記録
        Project.setString(Project.KARTE_DEFAULT_TITLE, medResProgressnote.getText());

        //デフォルトチェック設定/保存時
        Project.setString(Project.SEND_CLAIM_SAVE, String.valueOf(medResSaveTransmit.isSelected()));

        //修正時
        Project.setString(Project.SEND_CLAIM_MODIFY, String.valueOf(medResTransmitMod.isSelected()));

        //予定カルテ
        Project.setString(Project.SEND_CLAIM_EDIT_FROM_SCHEDULE, String.valueOf(medResScheduleTransmit.isSelected()));

        //仮保存ボタン押下時
        Project.setString(Project.SEND_CLAIM_DEPENDS_ON_CHECK_AT_TMP, String.valueOf(medResTemporarySelect.isSelected()));

        //併用禁忌
        Project.setString(Project.INTERACTION_CHECK, String.valueOf(medResCombined.isSelected()));

        //実費
        Project.setString(Project.INSURANCE_SELF, String.valueOf(medResCostPrice.isSelected()));

        //労災
        Project.setString(Project.INSURANCE_ROSAI_PREFIX, String.valueOf(medResAccident.isSelected()));

        //自賠責
        Project.setString(Project.INSURANCE_JIBAISEKI_PREFIX, String.valueOf(medResInsurance.isSelected()));

        //新規カルテ作成時/確認ダイアログを表示しない
        if(etcDialog.isSelected()) {
            Project.setString(Project.KARTE_SHOW_CONFIRM_AT_NEW, "false");
        } else {
            Project.setString(Project.KARTE_SHOW_CONFIRM_AT_NEW, "true");
        }

        //作成方法
        if (etcAltogether.isSelected()) {
            Project.setString(Project.KARTE_CREATE_MODE, "2");
        } else if (etcLastPrescription.isSelected()) {
            Project.setString(Project.KARTE_CREATE_MODE, "1");
        } else if (etcBlankNewKarte.isSelected()) {
            Project.setString(Project.KARTE_CREATE_MODE, "0");
        }

        //配置方法
        if(etcTabPanel.isSelected()) {
            Project.setString(Project.KARTE_PLACE_MODE, "false");
        } else {
            Project.setString(Project.KARTE_PLACE_MODE, "true");
        }

        //カルテ保存時/編集ウィンドウを自動的に閉じる
        Project.setString(Project.KARTE_AUTO_CLOSE_AFTER_SAVE, String.valueOf(etcClosesAutomatically.isSelected()));

        //確認ダイアログ
        if(etcKarteSaveDialog.isSelected()) {
            Project.setString(Project.KARTE_SHOW_CONFIRM_AT_SAVE, "false");
        } else {
            Project.setString(Project.KARTE_SHOW_CONFIRM_AT_SAVE, "true");
        }

        //印刷枚数
        Project.setString(Project.KARTE_PRINT_COUNT, etcPrintingSheets.getText());

        //動作
        if (etcOperationAllcopy.isSelected()) {
            Project.setString(Project.KARTE_SAVE_ACTION, "0");
        } else if (etcOperationPrescription.isSelected()) {
            Project.setString(Project.KARTE_SAVE_ACTION, "1");
        }

        //月例を表示する年齢
        Project.setString(Project.KARTE_AGE_TO_NEED_MONTH, etcAgeOfTheMoon.getText());

        //文字サイズ
        if (etcFontLarge.isSelected()) {
            Project.setString(Project.FONT_SIZE, "16");
        } else if (etcFontMiddle.isSelected()) {
            Project.setString(Project.FONT_SIZE, "14");
        } else if (etcFontSmall.isSelected()) {
            Project.setString(Project.FONT_SIZE, "12");
        }

        //印刷方法/種類
        Project.setString(Project.KARTE_PRINT_PDF, String.valueOf(printWindowsMac.isSelected()));

        //印刷ダイアログ
        Project.setString(Project.KARTE_PRINT_DIRECT, String.valueOf(printView.isSelected()));

        //Windows/Mac印刷をした場合、印刷せずにPDFの表示を行う
        Project.setString(Project.KARTE_PRINT_SHOWPDF, String.valueOf(pdfView.isSelected()));
        
        //デフォルト文字サイズ
        Project.setString(Project.KARTE_PRINT_PDF_TEXTSIZE, pdfViewTextSize.getText());
        
        //ラボテストのリスト印刷
        Project.setString(Project.LABO_PRINT_SHOWPDF, String.valueOf(laboPrintConvPdf.isSelected()));
        
        //スタンプ/スタンプ上にDnDした場合
        Project.setString(Project.STAMP_REPLACE, String.valueOf(stampReplaces.isSelected()));

        //間隔を空ける
        Project.setString(Project.STAMP_SPACE, String.valueOf(stampDnd.isSelected()));

        //折りたたみ表示
        Project.setString(Project.LABTEST_FOLD, String.valueOf(stampSpecimen.isSelected()));

        //カルテ展開時にスタンプ名を表示する
        Project.setString(Project.DEFAULT_STAMP_NAME, String.valueOf(stampStampname.isSelected()));

        //スタンプ動作設定/錠剤の場合
        Project.setString(Project.DEFAULT_ZYOZAI_NUM, stampTablet.getText());
        //水薬の場合
        Project.setString(Project.DEFAULT_MIZUYAKU_NUM, stampLiquidmedicine.getText());
        //散薬の場合
        Project.setString(Project.DEFAULT_SANYAKU_NUM, stampPowder.getText());
        //カプセルの場合
        Project.setString(Project.DEFAULT_CAPSULE_NUM, stampCapsule.getText());
        //処方日数
        Project.setString(Project.DEFAULT_RP_NUM, stampPrescriptiondays.getText());

        //処方/同じ用法にまとめる
        Project.setString(Project.KARTE_MERGE_RP_WITH_SAME_ADMIN, String.valueOf(stampDirection.isSelected()));

        //マスター項目
        Project.setString(Project.MASTER_SEARCH_ITEM_COLORING, String.valueOf(stampMaster.isSelected()));

        //同じ検体検査をまとめる
        Project.setString(Project.KARTE_MERGE_WITH_LABTEST, String.valueOf(mergeWithLabotest.isSelected()));
        
        //スタンプエディタのボタン
        if (stampText.isSelected()) {
            Project.setString(Project.STAMP_EDITOR_BUTTON_TYPE, "text");
        } else {
            Project.setString(Project.STAMP_EDITOR_BUTTON_TYPE, "icon");
        }

        //宛先継承
        if (introGokika.isSelected()) {
            Project.setString(Project.LETTER_ATESAKI_TITLE, "御机下");
        } else if (introGozisi.isSelected()) {
            Project.setString(Project.LETTER_ATESAKI_TITLE, "御侍史");
        } else if (introNothing.isSelected()) {
            Project.setString(Project.LETTER_ATESAKI_TITLE, "無し");
        }

        //PDF印刷時の挨拶文
        Project.setString(Project.LETTER_INCLUDE_GREETINGS, String.valueOf(introLetterPdf.isSelected()));

        //プレイン文書
        Project.setString(Project.PLAIN_PRINT_PATIENT_NAME, String.valueOf(introLetterOn.isSelected()));
        
        //電話番号出力
        Project.setString(Project.LETTER_TELEPHONE_OUTPUTPDF, String.valueOf(lerrerTelOufPdfOn.isSelected()));

        //診断書/フォントサイズ
        if (introLetterBig.isSelected()) {
            Project.setString(Project.SHINDANSYO_FONT_SIZE, "large");
        } else if (introLetterSmall.isSelected()) {
            Project.setString(Project.SHINDANSYO_FONT_SIZE, "small");
        }

        //出力先ディレクトリ
        if (Project.getString(Project.LOCATION_PDF) == null
                || Project.getString(Project.LOCATION_PDF).isEmpty()) {
            String pdfDir = "pdf";
            if (ClientContext.isMac() || ClientContext.isLinux()) {
                Project.setString(Project.LOCATION_PDF, this.stub.getBaseDirectory() + "/" + pdfDir);
            } else {
                Project.setString(Project.LOCATION_PDF, this.stub.getBaseDirectory() + "\\" + pdfDir);
            }
        } else {
            Project.setString(Project.LOCATION_PDF, introOutput.getText());
        }

        //コードタブ/修飾キー
        if ((codeControl.isSelected())) {
            Project.setString(Project.ENTITY_CODE_MODEFIER, "ctrl");
        } else {
            Project.setString(Project.ENTITY_CODE_MODEFIER, "meta");
        }

        //スタンプ箱のキーワード類
        Project.setString(Project.ENTITY_CODE_TEXT, codeTx.getText());
        Project.setString(Project.ENTITY_CODE_GENERAL_ORDER, codeGen.getText());
        Project.setString(Project.ENTITY_CODE_TREATMEN, codeTr.getText());
        Project.setString(Project.ENTITY_CODE_RADIOLOGY_ORDER, codeRad.getText());
        Project.setString(Project.ENTITY_CODE_PHYSIOLOGY_ORDER, codePhy.getText());
        Project.setString(Project.ENTITY_CODE_INJECTION_ORDER, codeInj.getText());
        Project.setString(Project.ENTITY_CODE_BASE_CHARGE_ORDER, codeBase.getText());
        Project.setString(Project.ENTITY_CODE_ORCA, codeOrca.getText());
        Project.setString(Project.ENTITY_CODE_PATH, codePat.getText());
        Project.setString(Project.ENTITY_CODE_OTHER_ORDER, codeOth.getText());
        Project.setString(Project.ENTITY_CODE_SURGERY_ORDER, codeSur.getText());
        Project.setString(Project.ENTITY_CODE_LABO_TEST, codeLab.getText());
        Project.setString(Project.ENTITY_CODE_BACTERIA_ORDER, codeBac.getText());
        Project.setString(Project.ENTITY_CODE_MED_ORDER, codeRp.getText());
        Project.setString(Project.ENTITY_CODE_INSTRACTION_CHARGE_ORDER, codeIns.getText());

        //リレー等/MML出力
        Project.setString(Project.SEND_MML, String.valueOf(relayMml.isSelected()));

        //MML出力先ディレクトリ
        if (relayMmlOutput.getText() != null && !relayMmlOutput.getText().isEmpty()) {
            Project.setString(Project.SEND_MML_DIRECTORY, relayMmlOutput.getText());
        } else {
            Project.setString(Project.SEND_MML_DIRECTORY, "");
        }

        //バージョン
        if (relayMmlVersionThree.isSelected()) {
            Project.setString(Project.MML_VERSION, "300");
        } else {
            Project.setString(Project.MML_VERSION, "230");
        }

        //カルテの保存時にPDF出力を行う
        Project.setString(Project.KARTE_PDF_SEND_AT_SAVE, String.valueOf(relayPdfCheck.isSelected()));

        //PDF出力先ディレクトリ
        Project.setString(Project.KARTE_PDF_SEND_DIRECTORY, relayPdfOutput.getText());

        //受付情報/リレー
        Project.setString(Project.PVT_RELAY, String.valueOf(relayRelay.isSelected()));

        //受付情報ディレクトリ
        Project.setString(Project.PVT_RELAY_DIRECTORY, relayOutput.getText());

        //エンコーディング
        if (relayEncShift.isSelected()) {
            Project.setString(Project.PVT_RELAY_ENCODING, "shiftjis");
        } else if (relayEncEuc.isSelected()) {
            Project.setString(Project.PVT_RELAY_ENCODING, "eucjp");
        } else {
            Project.setString(Project.PVT_RELAY_ENCODING, "utf8");
        }
        
        //- Evolution
        //- 表示名
        Project.setString(Project.EVO_DISP, evoDsp.getText());
        //- URL
        Project.setString(Project.EVO_URL, evoUrl.getText());

        //- 設定保存&読み直し
        Project.saveUserDefaults();
        Project.setProjectStub(new ProjectStub());
        application.Dialog.close();
    }

    //ディレクトリのメソッドたち
    //出力先ディレクトリ
    public void introSetDirNameOutPut() {
        DirectoryChooser file = new DirectoryChooser();
        file.setTitle("選択");
        File importFile = file.showDialog(null);
        if (importFile != null) {
            introOutput.setText(importFile.getPath().toString());
        }
    }

    //MML出力の出力先ディレクトリ
    public void setDirNameRelayMmlOutput() {
        DirectoryChooser dir = new DirectoryChooser();
        dir.setTitle("出力先ディレクトリ選択");
        File importFile = dir.showDialog(null);
        if (importFile != null) {
            relayMmlOutput.setText(importFile.getPath().toString());
        }
    }

    //カルテPDF出力先ディレクトリ
    public void setDirNameRelayPdfOutput() {
        DirectoryChooser dir = new DirectoryChooser();
        dir.setTitle("カルテPDF出力先ディレクトリ選択");
        File importFile = dir.showDialog(null);
        if (importFile != null) {
            relayPdfOutput.setText(importFile.getPath().toString());
        }
    }

    //受付情報出力先ディレクトリ
    public void setDirNameRelayOutput() {
        DirectoryChooser dir = new DirectoryChooser();
        dir.setTitle("受付情報出力先ディレクトリ選択");
        File importFile = dir.showDialog(null);
        if (importFile != null) {
            relayOutput.setText(importFile.getPath().toString());
        }
    }

}
