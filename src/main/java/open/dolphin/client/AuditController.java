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
package open.dolphin.client;

import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JDialog;
import open.dolphin.delegater.DocumentDelegater;
import open.dolphin.delegater.PatientDelegater;
import open.dolphin.dto.DocumentSearchSpec;
import open.dolphin.dto.PatientSearchSpec;
import open.dolphin.dto.Person;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.project.Project;
import open.dolphin.util.AgeCalculater;
import open.dolphin.util.Log;
import open.dolphin.util.StringTool;

/**
 * AuditCtrl Controller class
 *
 * @author manabu nishimura <nishimurama@sandi.co.jp>
 */
public class AuditController implements Initializable {

    @FXML
    ComboBox searchType;
    @FXML
    TextField searchText;
    @FXML
    TableView searchResult;
    @FXML
    ComboBox yearTerm;
    @FXML
    TableColumn<Person, Boolean> invited;
    @FXML
    TableColumn id;
    @FXML
    TableColumn name;
    @FXML
    TableColumn nameKana;
    @FXML
    TableColumn sex;
    @FXML
    TableColumn birthday;
    @FXML
    TableColumn visitDay;
    @FXML
    Label searchCount;
    @FXML
    ProgressIndicator progressIndi;
    @FXML
    Button searchBtn;
    @FXML
    Button cancelBtn;
    @FXML
    Button outputBtn;
    @FXML
    ComboBox outputType;
    @FXML
    Label outputDir;
    // 全選択ボタン
    Button btn = null;

    JDialog dialog = null;

    private Stage mainStage;
    private Evolution application;
//    private final int SPLIT2_HIGHT = 305;
//    private final int SPLIT3_WIDTH = 481;

//    final Image imageAccept = new Image(getClass().getResourceAsStream("/resources/images/os_accept_32.png"));
//    final Image imageCancel = new Image(getClass().getResourceAsStream("/resources/images/os_cancel_32.png"));
    boolean allChoiceFlg = false;

    LinkedList<InnerBean> outputList = null;
    
    ClientContextStub stub;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.stub = new ClientContextStub();

        searchCount.setText("0件");

        String outputDIR = null;
        if (ClientContext.isMac()) {
            outputDIR = "~/";
        } else {
            outputDIR = this.stub.getBaseDirectory() + "\\";
        }
        outputDir.setText(outputDIR);

        List<String> list = new ArrayList<>();
        list.add("患者ID");
        list.add("カ ナ");
        ObservableList obList = FXCollections.observableList(list);
        searchType.getItems().clear();
        searchType.setItems(obList);
        searchType.getSelectionModel().select(0);

        list = new ArrayList<>();
        list.add("1年");
        list.add("2年");
        list.add("3年");
        list.add("4年");
        list.add("5年");
        list.add("すべて");
        obList = FXCollections.observableList(list);
        yearTerm.getItems().clear();
        yearTerm.setItems(obList);
        yearTerm.getSelectionModel().select(0);

        list = new ArrayList<>();
        list.add("PDF形式");
        list.add("CSV形式");
        obList = FXCollections.observableList(list);
        outputType.getItems().clear();
        outputType.setItems(obList);
        outputType.getSelectionModel().select(0);

        //"選択" column
        btn = new Button("全選択");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ObservableList<Person> temp = searchResult.getItems();
                ObservableList<Person> work = FXCollections.observableList(new LinkedList<Person>());
                for (int i = 0; i < temp.size(); i++) {
                    Person person = temp.get(i);
                    person.invitedProperty().set(!allChoiceFlg);
                    work.add(person);
                }
                allChoiceFlg = !allChoiceFlg;
                String disp = allChoiceFlg ? "全解除" : "全選択";
                btn.setText(disp);
                searchResult.setItems(work);
            }
        });
//        bt.setGraphic(new ImageView(imageAccept));
        invited.setGraphic(btn);
        invited.setCellValueFactory(new PropertyValueFactory("invited"));
        invited.setCellFactory(new Callback<TableColumn<Person, Boolean>, TableCell<Person, Boolean>>() {
            @Override
            public TableCell<Person, Boolean> call(TableColumn<Person, Boolean> p) {
                return new CheckBoxTableCell<>();
            }
        });
        id.setCellValueFactory(new PropertyValueFactory("id"));
        name.setCellValueFactory(new PropertyValueFactory("name"));
        nameKana.setCellValueFactory(new PropertyValueFactory("nameKana"));
        sex.setCellValueFactory(new PropertyValueFactory("sex"));
        birthday.setCellValueFactory(new PropertyValueFactory("birthday"));
        visitDay.setCellValueFactory(new PropertyValueFactory("visitDay"));

        searchResult.setEditable(true);
    }

    public void setStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public Stage getStage() {
        return mainStage;
    }

    public void setApp(Evolution application) {
        this.application = application;
    }

    public Evolution getApplication() {
        return application;
    }

    public void close() {
        if (dialog != null) {
            dialog.setVisible(false);
            dialog.dispose();
        }
    }

    public void pdfOutput() {
        // 一つでも患者が選択されていなければ終了
        if (!checkChoice()) {
            return;
        }
        //- Operation無効
        lockOperation();
        tryOutput();
    }

    /**
     * 出力実行(BackGround)
     */
    public void tryOutput() {
        final Task<Boolean> output = doOutput();
        output.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                if (output.getValue()) {
                    // PDF出力処理
                    output();
                    unLockOperation();
                }
            }
        });
        output.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
//                System.exit(0);
            }
        });
        new Thread(output).start();
    }

    /**
     * 出力処理
     *
     * @return
     */
    public Task<Boolean> doOutput() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws RuntimeException, Exception {
                try {
                    outputList = new LinkedList<>();

                    //- Operation無効
                    lockOperation();

                    int reversalTerm = getReversalTerm();
                    int past = Project.getInt(Project.DOC_HISTORY_PERIOD, -reversalTerm);
                    GregorianCalendar today = new GregorianCalendar();
                    today.add(GregorianCalendar.MONTH, past);
                    today.clear(Calendar.HOUR_OF_DAY);
                    today.clear(Calendar.MINUTE);
                    today.clear(Calendar.SECOND);
                    today.clear(Calendar.MILLISECOND);

                    ObservableList<Person> temp = searchResult.getItems();
                    ObservableList<Person> work = FXCollections.observableList(new LinkedList<Person>());
                    for (int i = 0; i < temp.size(); i++) {
                        Person person = temp.get(i);
                        if (!person.invitedProperty().get()) {
                            continue;
                        }
                        // 検索パラメータセットのDTOを生成する
                        DocumentSearchSpec spec = new DocumentSearchSpec();
                        spec.setDocType(IInfoModel.DOCTYPE_KARTE);		// 文書タイプ
                        spec.setFromDate(today.getTime());			// 抽出期間開始
                        spec.setToDate(today.getTime());			// 抽出期間終了
                        spec.setIncludeModifid(true);                           // 修正履歴
                        spec.setCode(DocumentSearchSpec.DOCTYPE_SEARCH);	// 検索タイプ
                        spec.setAscending(true);

                        DocumentDelegater ddl = new DocumentDelegater();
                        List<KarteBean> result = (List<KarteBean>) ddl.getKarteBeans(person.getPatientId(), today.getTime());

                        // 個人ごとのカルテ履歴情報を保持するリストへ追加
                        InnerBean bean = new InnerBean();
                        bean.setPerson(person);
                        bean.setResult(result);
                        outputList.add(bean);
                    }
                    return true;
                } catch (Exception ex) {
                    Logger.getLogger(AuditController.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            }
        };
    }

    private void output() {
        String str = (String) outputType.getValue();
        switch (str) {
            case "PDF形式":
                makePDF();
                break;
            case "CSV形式":
                makeCSV();
                break;
        }
    }

    public void search() {
        //- Operation無効
        lockOperation();
        trySearch();
    }

    /**
     * 検索実行(BackGround)
     */
    public void trySearch() {
        final Task<Boolean> search = doSearch();
        search.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                if (search.getValue()) {
                    //- Load StampTree & CreateMainWindow
                    unLockOperation();
                    updateStatusLabel();
                    clearAllChoice();
                }
            }
        });
        search.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                unLockOperation();
            }
        });
        new Thread(search).start();
    }

    /**
     * 検索処理
     *
     * @return
     */
    public Task<Boolean> doSearch() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws RuntimeException, Exception {
                try {
                    // 全角スペースをkill
                    String text = searchText.getText().replaceAll("　", " ");
                    if (text == null || text.isEmpty()) {
                        return true;
                    }

                    PatientSearchSpec spec = new PatientSearchSpec();

                    if (isDate(text)) {
                        spec.setCode(PatientSearchSpec.DATE_SEARCH);
                        spec.setDigit(text);

                    } else if (StringTool.startsWithKatakana(text)) {
                        spec.setCode(PatientSearchSpec.KANA_SEARCH);
                        spec.setName(text);

                    } else if (StringTool.startsWithHiragana(text)) {
                        text = StringTool.hiraganaToKatakana(text);
                        spec.setCode(PatientSearchSpec.KANA_SEARCH);
                        spec.setName(text);

                    } else if (isNameAddress(text)) {
                        spec.setCode(PatientSearchSpec.NAME_SEARCH);
                        spec.setName(text);

                    } else {
                        if (Project.getBoolean("zero.paddings.id.search", false)) {
                            int len = text.length();
                            int paddings = Project.getInt("patient.id.length", 0) - len;
                            StringBuilder sb = new StringBuilder();
                            for (int i = 0; i < paddings; i++) {
                                sb.append("0");
                            }
                            sb.append(text);
                            text = sb.toString();
                        }

                        spec.setCode(PatientSearchSpec.DIGIT_SEARCH);
                        spec.setDigit(text);
                    }

                    final PatientSearchSpec searchSpec = spec;
                    PatientDelegater pdl = new PatientDelegater();

                    Collection result = pdl.getPatients(searchSpec);
                    List<PatientModel> list = (List<PatientModel>) result;
                    ObservableList<Person> plist = FXCollections.observableArrayList();
                    if (list != null && list.size() > 0) {
                        for (PatientModel model : list) {
                            // 性別の日本語名称取得
                            String sex = (model.getGender() != null) ? (model.getGender().toLowerCase().startsWith("m") ? "男" : "女") : "";
                            // 数え年の計算・取得
                            int showMonth = Project.getInt("ageToNeedMonth", 6);
                            String ret = AgeCalculater.getAgeAndBirthday(model.getBirthday(), showMonth);
                            Person person = new Person(false, model.getPatientId(), model.getFullName(), model.getKanaName(), sex, ret, model.getPvtDate(), model.getId());
                            plist.add(person);
                        }
                        sortList(plist);
                        searchResult.setItems(plist);
                        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "患者数：", String.valueOf(list.size()));
                    } else {
                        searchResult.getItems().clear();
                        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "患者数：", "0");
                    }
                    return true;
                } catch (Exception ex) {
                    Logger.getLogger(AuditController.class.getName()).log(Level.SEVERE, null, ex);
                    return false;
                }
            }
        };
    }

    // ステータスラベルに検索件数を表示
    private void updateStatusLabel() {
        int count = searchResult.getItems().size();
        String msg = String.valueOf(count) + "件";
        searchCount.setText(msg);
    }

    private boolean isDate(String text) {
        boolean maybe = false;
        if (text != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.parse(text);
                maybe = true;

            } catch (ParseException e) {
            }
        }

        return maybe;
    }

    private boolean isNameAddress(String text) {
        boolean maybe = false;
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                char c = text.charAt(i);
                if (Character.getType(c) == Character.OTHER_LETTER) {
                    maybe = true;
                    break;
                }
            }
        }
        return maybe;
    }

    private void sortList(ObservableList<Person> list) {
        int ret = 0;
        if (searchType.getValue() != null && searchType.getValue().equals("患者ID")) {
            ret = 0;
        } else {
            ret = 1;
        }

        switch (ret) {
            case 0:
                Comparator c = new Comparator<Person>() {
                    @Override
                    public int compare(Person o1, Person o2) {
                        return o1.idProperty().get().compareTo(o2.idProperty().get());
                    }
                };
                Collections.sort(list, c);
                break;
            case 1:
                Comparator c2 = new Comparator<Person>() {
                    @Override
                    public int compare(Person p1, Person p2) {
                        String kana1 = p1.nameKanaProperty().get();
                        String kana2 = p2.nameKanaProperty().get();
                        if (kana1 != null && kana2 != null) {
                            return p1.nameKanaProperty().get().compareTo(p2.nameKanaProperty().get());
                        } else if (kana1 != null && kana2 == null) {
                            return -1;
                        } else if (kana1 == null && kana2 != null) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                };
                Collections.sort(list, c2);
                break;
        }

    }

    private int getReversalTerm() {
        int ret = 12;
        String str = (String) yearTerm.getValue();
        switch (str) {
            case "1年":
                ret *= 1;
                break;
            case "2年":
                ret *= 2;
                break;
            case "3年":
                ret *= 3;
                break;
            case "4年":
                ret *= 4;
                break;
            case "5年":
                ret *= 5;
                break;
            case "すべて":
                ret *= 10000;
                break;
        }
        return ret;
    }

    //CheckBoxTableCell for creating a CheckBox in a table cell
    public static class CheckBoxTableCell<S, T> extends TableCell<S, T> {

        private final CheckBox checkBox;
        private ObservableValue<T> ov;

        public CheckBoxTableCell() {
            this.checkBox = new CheckBox();
            this.checkBox.setAlignment(Pos.CENTER);
            setAlignment(Pos.CENTER);
            setGraphic(checkBox);
        }

        @Override
        public void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                setGraphic(checkBox);
                if (ov instanceof BooleanProperty) {
                    checkBox.selectedProperty().unbindBidirectional((BooleanProperty) ov);
                }

                ov = getTableColumn().getCellObservableValue(getIndex());
                if (ov instanceof BooleanProperty) {
                    checkBox.selectedProperty().bindBidirectional((BooleanProperty) ov);
                }
            }
        }
    }

    /**
     * コントロールボタンのロック
     */
    public void lockOperation() {
        //- ボタン無効
        searchType.setDisable(true);
        searchText.setDisable(true);
        searchResult.setDisable(true);
        yearTerm.setDisable(true);
        searchBtn.setDisable(true);
        cancelBtn.setDisable(true);
        outputBtn.setDisable(true);
        outputType.setDisable(true);
        //- プログレス表示
        progressIndi.setVisible(true);
    }

    /**
     * コントロールボタンのアンロック
     */
    public void unLockOperation() {
        //- ボタン有効
        searchType.setDisable(false);
        searchText.setDisable(false);
        searchResult.setDisable(false);
        yearTerm.setDisable(false);
        searchBtn.setDisable(false);
        cancelBtn.setDisable(false);
        outputBtn.setDisable(false);
        outputType.setDisable(false);
        //- プログレス表示
        progressIndi.setVisible(false);
    }

    private void clearAllChoice() {
        searchText.clear();
        allChoiceFlg = false;
        btn.setText("全選択");
    }

    class InnerBean {

        Person person = null;
        List<KarteBean> result = null;

        public Person getPerson() {
            return person;
        }

        public void setPerson(Person person) {
            this.person = person;
        }

        public List<KarteBean> getResult() {
            return result;
        }

        public void setResult(List<KarteBean> result) {
            this.result = result;
        }
    }

    private boolean checkChoice() {
        boolean flg = false;
        ObservableList<Person> temp = searchResult.getItems();
        for (int i = 0; i < temp.size(); i++) {
            Person person = temp.get(i);
            if (person.invitedProperty().get()) {
                flg = true;
                break;
            }
        }
        return flg;
    }

    private void makePDF() {

        //- 上下左右
        Document doc = new Document(PageSize.A4, 20.0F, 20.0F, 40.0F, 40.0F);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "カルテ更新履歴一覧_" + sdf.format(new java.util.Date()) + ".pdf";

            //出力先(アウトプットストリーム)の生成
            FileOutputStream fos = new FileOutputStream(outputDir.getText() + fileName);
            PdfWriter pdfwriter = PdfWriter.getInstance(doc, fos);
            Font font_header = new Font(BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", false), 15.0F, 1);
            Font font_g11 = new Font(BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", false), 11.0F);
            Font font_g10 = new Font(BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", false), 10.0F);
            //- レコード毎のフォント
            Font font_m8 = new Font(BaseFont.createFont("HeiseiMin-W3", "UniJIS-UCS2-HW-H", false), 8.0F);
            Font font_underline_11 = new Font(BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", false), 11.0F, 4);
            Font font_red_11 = new Font(BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", false), 11.0F);
            font_red_11.setColor(new Color(255, 0, 0));
            Font font_empty = new Font(BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", false), 9.0F);
            font_empty.setColor(new Color(255, 255, 255));

            Paragraph para_NF = new Paragraph(5, "\r\n", new Font(BaseFont.createFont("HeiseiKakuGo-W5", "UniJIS-UCS2-H", false), 13, Font.NORMAL));
            para_NF.setAlignment(Element.ALIGN_CENTER);

            // ログインユーザを作成者に設定
            String author = Project.getProjectStub().getUserModel().getCommonName();
            doc.addAuthor(author);
            doc.addSubject("患者別カルテ更新履歴一覧");

            HeaderFooter header = new HeaderFooter(new Phrase("患者別カルテ更新履歴一覧", font_header), false);
            header.setAlignment(1);
            doc.setHeader(header);

            HeaderFooter footer = new HeaderFooter(new Phrase("--"), new Phrase("--"));
            footer.setAlignment(1);
            footer.setBorder(0);
            doc.setFooter(footer);

            doc.open();

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy'年'MM'月'dd'日' HH'時'mm'分'");
            String today = sdf1.format(new java.util.Date());
            Paragraph para_0 = new Paragraph("作成日時：" + today, font_g11);
            para_0.setAlignment(2);
            doc.add(para_0);
            Paragraph para_1 = new Paragraph("作成者：" + author, font_g11);
            para_1.setAlignment(2);
            doc.add(para_1);
            doc.add(new Paragraph(""));
            // 改行
            doc.add(para_NF);
            doc.add(para_NF);

            for (int cnt = 0; cnt < outputList.size(); cnt++) {

                InnerBean bean = outputList.get(cnt);
                Person person = bean.getPerson();

                Paragraph para_2 = new Paragraph("患者ID：" + person.idProperty().get(), font_underline_11);
                para_2.setAlignment(0);
                doc.add(para_2);
                Paragraph para_3 = new Paragraph("患者氏名：" + person.nameProperty().get(), font_underline_11);
                para_3.setAlignment(0);
                doc.add(para_3);
                Paragraph para_4 = new Paragraph("患者カナ：" + person.nameKanaProperty().get(), font_underline_11);
                para_4.setAlignment(0);
                doc.add(para_4);
                Paragraph para_5 = new Paragraph("性別：" + person.sexProperty().get(), font_underline_11);
                para_5.setAlignment(0);
                doc.add(para_5);
                Paragraph para_6 = new Paragraph("生年月日：" + person.birthdayProperty().get(), font_underline_11);
                para_6.setAlignment(0);
                doc.add(para_6);

                Table karteHistoryTable = new Table(5);
                karteHistoryTable.setWidth(100.0F);
                int[] uriage_table_width = {25, 20, 30, 20, 25};
                karteHistoryTable.setWidths(uriage_table_width);
                //karteHistoryTable.setDefaultHorizontalAlignment(1);
                //karteHistoryTable.setDefaultVerticalAlignment(5);
                karteHistoryTable.setPadding(3.0F);
                karteHistoryTable.setSpacing(0.0F);
                karteHistoryTable.setBorderColor(new Color(0, 0, 0));

                Cell cell_01 = new Cell(new Phrase("カルテ作成日", font_g10));
                cell_01.setGrayFill(0.8F);
                cell_01.setHorizontalAlignment(Element.ALIGN_CENTER);
                Cell cell_11 = new Cell(new Phrase("カルテ作成者", font_g10));
                cell_11.setGrayFill(0.8F);
                cell_11.setHorizontalAlignment(Element.ALIGN_CENTER);
                Cell cell_21 = new Cell(new Phrase("内容", font_g10));
                cell_21.setGrayFill(0.8F);
                cell_21.setHorizontalAlignment(Element.ALIGN_CENTER);
                Cell cell_31 = new Cell(new Phrase("最終更新者", font_g10));
                cell_31.setGrayFill(0.8F);
                cell_31.setHorizontalAlignment(Element.ALIGN_CENTER);
                Cell cell_41 = new Cell(new Phrase("最終更新日時", font_g10));
                cell_41.setGrayFill(0.8F);
                cell_41.setHorizontalAlignment(Element.ALIGN_CENTER);

                karteHistoryTable.addCell(cell_01);
                karteHistoryTable.addCell(cell_11);
                karteHistoryTable.addCell(cell_21);
                karteHistoryTable.addCell(cell_31);
                karteHistoryTable.addCell(cell_41);

                List<KarteBean> list = bean.getResult();
                KarteBean karteInfo = list.get(0);
                List<DocInfoModel> docInfoList = karteInfo.getDocInfoList();

                //- １ページあたりのカウント数
                int stepCount = 22;
                int tempCount = 0;
                int pageCount = 0;

                if (docInfoList != null) {
                    for (int i = 0; i < docInfoList.size(); ++i) {
                        DocInfoModel docInfo = docInfoList.get(i);
                        Cell cell = new Cell(new Phrase(docInfo.getFirstConfirmDateTime(), font_m8));
                        cell.setHorizontalAlignment(0);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        karteHistoryTable.addCell(cell);
                        cell = new Cell(new Phrase(docInfo.getAssignedDoctorName(), font_m8));
                        cell.setHorizontalAlignment(0);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        karteHistoryTable.addCell(cell);
                        //- 改行コード除去
                        String addTitle = docInfo.getTitle();
                        addTitle = addTitle.replace("\r\n", "");
                        addTitle = addTitle.replace("\n", "");
                        cell = new Cell(new Phrase(addTitle, font_m8));
                        cell.setHorizontalAlignment(0);
                        karteHistoryTable.addCell(cell);
                        cell = new Cell(new Phrase(docInfo.getPurpose(), font_m8));
                        cell.setHorizontalAlignment(0);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        karteHistoryTable.addCell(cell);
                        cell = new Cell(new Phrase(docInfo.getConfirmDateTime(), font_m8));
                        cell.setHorizontalAlignment(0);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        karteHistoryTable.addCell(cell);

                        if (stepCount == tempCount) {
                            if(pageCount == 0) {
                                stepCount += 5;
                                pageCount++;
                            }
                            tempCount = 0;
                            doc.add(karteHistoryTable);
                            doc.newPage();
                            karteHistoryTable.deleteAllRows();
                            karteHistoryTable.addCell(cell_01);
                            karteHistoryTable.addCell(cell_11);
                            karteHistoryTable.addCell(cell_21);
                            karteHistoryTable.addCell(cell_31);
                            karteHistoryTable.addCell(cell_41);
                        } else {
                            tempCount++;
                        }
                    }

//                    Cell Empty_Cell = new Cell(new Phrase("empty", font_empty));
//                    for (int i = docInfoList.size(); i < docInfoList.size() + 4; ++i) {
//                        for (int j = 0; j < 4; ++j) {
//                            karteHistoryTable.addCell(Empty_Cell);
//                        }
//                    }
//
//            Cell cell_goukei = new Cell(new Phrase("合計", font_g10));
//            cell_goukei.setGrayFill(0.8F);
//            cell_goukei.setColspan(3);
//            karteHistoryTable.addCell(cell_goukei);
//            Cell cell_sum = new Cell(new Phrase("136,900", font_m10));
//            cell_sum.setHorizontalAlignment(2);
//            karteHistoryTable.addCell(cell_sum);
                    doc.add(karteHistoryTable);
                    doc.newPage();

                } else {
                    // 改行
                    doc.add(para_NF);

                    Paragraph noData = new Paragraph("カルテ更新履歴なし", font_m8);
                    noData.setAlignment(0);
                    doc.add(noData);
                    doc.newPage();
                }
            }
        } catch (DocumentException | IOException e) {
            Logger.getLogger(AuditController.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            doc.close();
        }
    }

    private void makeCSV() {

        BufferedWriter bw = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "カルテ更新履歴一覧_" + sdf.format(new java.util.Date()) + ".csv";
            File csv = new File(outputDir.getText() + fileName); // CSVデータファイル
            // 追記モード
            bw = new BufferedWriter(new FileWriter(csv, true));
            bw.write("カルテ作成日,カルテ作成者,内容,最終更新者,最終更新日時,患者ID,患者氏名,患者カナ,性別,生年月日");
            bw.newLine();

            for (int cnt = 0; cnt < outputList.size(); cnt++) {
                InnerBean bean = outputList.get(cnt);
                Person person = bean.getPerson();
                StringBuilder personStrBuf = new StringBuilder();
                personStrBuf.append(person.idProperty().get()); // 患者ID
                personStrBuf.append(",");
                personStrBuf.append(person.nameProperty().get()); // 患者氏名
                personStrBuf.append(",");
                personStrBuf.append(person.nameKanaProperty().get()); // 患者カナ
                personStrBuf.append(",");
                personStrBuf.append(person.sexProperty().get()); // 性別
                personStrBuf.append(",");
                personStrBuf.append(person.birthdayProperty().get()); // 生年月日

                StringBuffer docStrBuf = null;
                List<KarteBean> list = bean.getResult();
                KarteBean karteInfo = list.get(0);
                List<DocInfoModel> docInfoList = karteInfo.getDocInfoList();
                if (docInfoList != null) {
                    for (int i = 0; i < docInfoList.size(); ++i) {
                        docStrBuf = new StringBuffer();
                        DocInfoModel docInfo = docInfoList.get(i);
                        docStrBuf.append(docInfo.getFirstConfirmDateTime());
                        docStrBuf.append(",");
                        docStrBuf.append(docInfo.getAssignedDoctorName());
                        docStrBuf.append(",");
                        docStrBuf.append(docInfo.getTitle().replace("\n", ""));
                        docStrBuf.append(",");
                        docStrBuf.append(docInfo.getPurpose());
                        docStrBuf.append(",");
                        docStrBuf.append(docInfo.getConfirmDateTime());

                        bw.write(docStrBuf.toString() + "," + personStrBuf.toString());
                        bw.newLine();
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AuditController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AuditController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(AuditController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public JDialog getDialog() {
        return dialog;
    }

    public void setDialog(JDialog dialog) {
        this.dialog = dialog;
    }

}
