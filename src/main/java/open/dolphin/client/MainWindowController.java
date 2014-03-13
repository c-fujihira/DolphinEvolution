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
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import open.dolphin.delegater.PVTDelegater;
import open.dolphin.dto.LabReceiverInfo;
import open.dolphin.dto.PatientFutureInfo;
import open.dolphin.dto.PatientSearchInfo;
import open.dolphin.dto.ReceptInfo;
import open.dolphin.infomodel.PatientVisitModel;
import org.apache.commons.lang.StringUtils;

/**
 * MainWindow FXML Controller class
 *
 * @author Chikara Fujihira <fujihirach@sandi.co.jp>
 */
public class MainWindowController implements Initializable {

    @FXML
    AnchorPane KarteWindow;
    @FXML
    AnchorPane ListnerWindow;
    @FXML
    AnchorPane ToolWindow;
    
    JFrame frame;

    //- 受付リスト
    @FXML
    TableView<ReceptInfo> ReceptView;
    @FXML
    private TableColumn<ReceptInfo, String> recept;
    @FXML
    private TableColumn<ReceptInfo, String> visitTime;
    @FXML
    private TableColumn<ReceptInfo, String> clientId;
    @FXML
    private TableColumn<ReceptInfo, String> name;
    @FXML
    private TableColumn<ReceptInfo, String> sex;
    @FXML
    private TableColumn<ReceptInfo, String> insurance;
    @FXML
    private TableColumn<ReceptInfo, String> birthDay;
    @FXML
    private TableColumn<ReceptInfo, String> physicianInCharge;
    @FXML
    private TableColumn<ReceptInfo, String> clinicalDepartments;
    @FXML
    private TableColumn<ReceptInfo, String> reservation;
    @FXML
    private TableColumn<ReceptInfo, String> memo;
    @FXML
    private TableColumn<ReceptInfo, String> status;
    @FXML
    Text ReceptStatus;

    //- 患者検索
    @FXML
    TableView<PatientSearchInfo> PatientSearchView;
    @FXML
    private TableColumn<PatientSearchInfo, String> clientId1;
    @FXML
    private TableColumn<PatientSearchInfo, String> kana1;
    @FXML
    private TableColumn<PatientSearchInfo, String> name1;
    @FXML
    private TableColumn<PatientSearchInfo, String> sex1;
    @FXML
    private TableColumn<PatientSearchInfo, String> birthDay1;
    @FXML
    private TableColumn<PatientSearchInfo, String> receiveDay1;
    @FXML
    private TableColumn<PatientSearchInfo, String> status1;

    //- 患者予定
    @FXML
    TableView PatientFutureView;
    @FXML
    private TableColumn<PatientFutureInfo, String> clientId2;
    @FXML
    private TableColumn<PatientFutureInfo, String> name2;
    @FXML
    private TableColumn<PatientFutureInfo, String> kana2;
    @FXML
    private TableColumn<PatientFutureInfo, String> sex2;
    @FXML
    private TableColumn<PatientFutureInfo, String> insurance2;
    @FXML
    private TableColumn<PatientFutureInfo, String> birthDay2;
    @FXML
    private TableColumn<PatientFutureInfo, String> physicianInCharge2;
    @FXML
    private TableColumn<PatientFutureInfo, String> clinicalDepartments2;
    @FXML
    private TableColumn<PatientFutureInfo, String> karte2;

    //- ラボレシーバー
    @FXML
    TableView LabRecieverView;
    @FXML
    private TableColumn<LabReceiverInfo, String> lab3;
    @FXML
    private TableColumn<LabReceiverInfo, String> clientId3;
    @FXML
    private TableColumn<LabReceiverInfo, String> kana3;
    @FXML
    private TableColumn<LabReceiverInfo, String> karteKana3;
    @FXML
    private TableColumn<LabReceiverInfo, String> sex3;
    @FXML
    private TableColumn<LabReceiverInfo, String> karteSex3;
    @FXML
    private TableColumn<LabReceiverInfo, String> sampleGetDay3;
    @FXML
    private TableColumn<LabReceiverInfo, String> register3;
    @FXML
    private TableColumn<LabReceiverInfo, String> status3;
    //- 読み込みファイル
    @FXML Label choiceFile;

    //- 受付リスト、予定患者などを保持するタブペイン
    @FXML TabPane mainTab;

    AnchorPane kartePane = new AnchorPane();
    TabPane karteTabPane = new TabPane();
    AnchorPane kartePane1 = new AnchorPane();
    TabPane karteTabPane1 = new TabPane();
    AnchorPane kartePane2 = new AnchorPane();
    TabPane karteTabPane2 = new TabPane();
    AnchorPane kartePane3 = new AnchorPane();
    TabPane karteTabPane3 = new TabPane();
    AnchorPane scheamWindow;

    public boolean stopFlag;
    Image folderIcon = null;
    List<ReceptInfo> receptList = new ArrayList<>();
    List<PatientSearchInfo> patientSearchList = new ArrayList<>();
    
    JTabbedPane baseTabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

    @FXML private TextField dateField;

    //- 親インスタンス
    private Stage mainStage;
    private Evolution application;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        //- Init TableView
        ReceptView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        PatientSearchView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        PatientFutureView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        LabRecieverView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

//        mainTab.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
//                SingleSelectionModel<Tab> selectionModel = mainTab.getSelectionModel();
//                if(mainTab.getTabs() != null){
//                    if(selectionModel.isSelected(0)){
//                        karteTabPane.getTabs().clear();
//                    }
//                }
//            }
//        }); 

        // 受付テーブル用画面変数とテーブルカラムのバインド
        TableColumn colId = new TableColumn("ID");
        recept.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("recept"));
        visitTime.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("visitTime"));
        tableCellAlignRight(visitTime);
        clientId.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("clientId"));
        name.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("name"));
        sex.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("sex"));
        tableCellAlignCenter(sex);
        insurance.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("insurance"));
        birthDay.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("birthDay"));
        physicianInCharge.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("physicianInCharge"));
        clinicalDepartments.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("clinicalDepartments"));
        reservation.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("reservation"));
        memo.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("memo"));
        status.setCellValueFactory(new PropertyValueFactory<ReceptInfo, String>("status"));
        tableCellImageAlignCenter(status);
        // 受付リスト初期表示データの取得
        ReceptView.getItems().setAll(fetchDataFromServer());

        // 受付リストの行選択時アクション(個人用カルテタブ作成)
        ReceptView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        System.out.println("Double clicked");
                        ReceptInfo selectedUser = ((TableView<ReceptInfo>) mouseEvent.getSource()).getSelectionModel().getSelectedItem();
                        // すでにカルテ上に同一人物がいる場合にはリターン
                        for (ReceptInfo info : receptList) {
                            if (info.getName().equals(selectedUser.getName())) {
                                return;
                            }
                        }
                        System.out.println(selectedUser.getClientId());
                        receptList.add(selectedUser);
                        // タブの右クリックメニュー登録
                        final ContextMenu contextMenu = new ContextMenu();
                        MenuItem item1 = new MenuItem("保存");
                        item1.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Reserve Karte：保存");
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        MenuItem item2 = new MenuItem("閉じて保存");
                        item2.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Close Tab and Preservation：閉じて保存");
                                karteTabPane.getTabs().remove(karteTabPane.getSelectionModel().getSelectedItem());
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        MenuItem item3 = new MenuItem("閉じる");
                        item3.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Close Tab：閉じる");
                                karteTabPane.getTabs().remove(karteTabPane.getSelectionModel().getSelectedItem());
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        contextMenu.getItems().addAll(item1, item2, item3);

                        Tab tab = new Tab(selectedUser.getName());
                        tab.setOnClosed(new EventHandler<Event>() {
                            @Override
                            public void handle(Event t) {
                                Tab tab = (Tab) t.getSource();
                                for (int i = 0; i < receptList.size(); i++) {
                                    if (tab.getText().equals(receptList.get(i).getName())) {
                                        receptList.remove(i);
                                    }
                                }
                                System.out.println("Closed!");
                            }
                        });
                        tab.setContextMenu(contextMenu); // Right-click mouse button menu
                        try {
                            // Loading content on demand
                            Parent root = (Parent) new FXMLLoader().load(this.getClass().getResource("/resources/fxml/Karte.fxml").openStream());
                            tab.setContent(root);
                            karteTabPane.getSelectionModel().select(tab);
                            karteTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
                            karteTabPane.getTabs().add(tab);
                            karteTabPane.setPrefSize(kartePane.getPrefWidth(), kartePane.getPrefHeight());
                            kartePane.getChildren().retainAll();
                            kartePane.getChildren().add(karteTabPane);
                        } catch (IOException ex) {
                            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }
        });

        // 患者テーブル用画面変数とテーブルカラムのバインド
        clientId1.setCellValueFactory(new PropertyValueFactory<PatientSearchInfo, String>("clientId1"));
        name1.setCellValueFactory(new PropertyValueFactory<PatientSearchInfo, String>("name1"));
        kana1.setCellValueFactory(new PropertyValueFactory<PatientSearchInfo, String>("kana1"));
        sex1.setCellValueFactory(new PropertyValueFactory<PatientSearchInfo, String>("sex1"));
        birthDay1.setCellValueFactory(new PropertyValueFactory<PatientSearchInfo, String>("birthDay1"));
        receiveDay1.setCellValueFactory(new PropertyValueFactory<PatientSearchInfo, String>("receiveDay1"));
        status1.setCellValueFactory(new PropertyValueFactory<PatientSearchInfo, String>("status1"));
        // dummyデータの登録
        PatientSearchView.getItems().setAll(fetchDataFromPatientInfo());

        // 患者テーブルの行選択時アクション(個人用カルテタブ作成)
        PatientSearchView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        System.out.println("Double clicked");
                        PatientSearchInfo selectedUser = ((TableView<PatientSearchInfo>) mouseEvent.getSource()).getSelectionModel().getSelectedItem();
                        // すでにカルテ上に同一人物がいる場合にはリターン
                        for (PatientSearchInfo info : patientSearchList) {
                            if (info.getName1().equals(selectedUser.getName1())) {
                                return;
                            }
                        }
                        System.out.println(selectedUser.getKana1());
                        patientSearchList.add(selectedUser);
                        // タブの右クリックメニュー登録
                        final ContextMenu contextMenu = new ContextMenu();
                        MenuItem item1 = new MenuItem("保存");
                        item1.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Reserve Karte：保存");
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        MenuItem item2 = new MenuItem("閉じて保存");
                        item2.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Close Tab and Preservation：閉じて保存");
                                karteTabPane1.getTabs().remove(karteTabPane1.getSelectionModel().getSelectedItem());
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        MenuItem item3 = new MenuItem("閉じる");
                        item3.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Close Tab：閉じる");
                                karteTabPane1.getTabs().remove(karteTabPane1.getSelectionModel().getSelectedItem());
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        contextMenu.getItems().addAll(item1, item2, item3);

                        Tab tab = new Tab(selectedUser.getName1());
                        tab.setOnClosed(new EventHandler<Event>() {
                            @Override
                            public void handle(Event t) {
                                Tab tab = (Tab) t.getSource();
                                for (int i = 0; i < patientSearchList.size(); i++) {
                                    if (tab.getText().equals(patientSearchList.get(i).getName1())) {
                                        patientSearchList.remove(i);
                                    }
                                }
                                System.out.println("Closed!");
                            }
                        });
                        tab.setContextMenu(contextMenu); // Right-click mouse button menu
                        try {
                            // Loading content on demand
                            Parent root = (Parent) new FXMLLoader().load(this.getClass().getResource("/resources/fxml/Karte.fxml").openStream());
                            tab.setContent(root);
                            karteTabPane1.getSelectionModel().select(tab);
                            karteTabPane1.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
                            karteTabPane1.getTabs().add(tab);
                            karteTabPane1.setPrefSize(kartePane1.getPrefWidth(), kartePane1.getPrefHeight());
                            kartePane1.getChildren().retainAll();
                            kartePane1.getChildren().add(karteTabPane1);
                        } catch (IOException ex) {
                            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }
        });

        // 予定患者テーブル用画面変数とテーブルカラムのバインド
        clientId2.setCellValueFactory(new PropertyValueFactory<PatientFutureInfo, String>("clientId2"));
        name2.setCellValueFactory(new PropertyValueFactory<PatientFutureInfo, String>("name2"));
        kana2.setCellValueFactory(new PropertyValueFactory<PatientFutureInfo, String>("kana2"));
        insurance2.setCellValueFactory(new PropertyValueFactory<PatientFutureInfo, String>("insurance2"));
        sex2.setCellValueFactory(new PropertyValueFactory<PatientFutureInfo, String>("sex2"));
        birthDay2.setCellValueFactory(new PropertyValueFactory<PatientFutureInfo, String>("birthDay2"));
        physicianInCharge2.setCellValueFactory(new PropertyValueFactory<PatientFutureInfo, String>("physicianInCharge2"));
        clinicalDepartments2.setCellValueFactory(new PropertyValueFactory<PatientFutureInfo, String>("clinicalDepartments2"));
        karte2.setCellValueFactory(new PropertyValueFactory<PatientFutureInfo, String>("karte2"));
        // dummyデータの登録
        PatientFutureView.getItems().setAll(fetchDataFromPatientFutureInfo());

        // 予定患者テーブルの行選択時アクション(個人用カルテタブ作成)
        PatientFutureView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        System.out.println("Double clicked");
                        PatientFutureInfo selectedUser = ((TableView<PatientFutureInfo>) mouseEvent.getSource()).getSelectionModel().getSelectedItem();
                        System.out.println(selectedUser.getName2());
                        // タブの右クリックメニュー登録
                        final ContextMenu contextMenu = new ContextMenu();
                        MenuItem item1 = new MenuItem("保存");
                        item1.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Reserve Karte：保存");
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        MenuItem item2 = new MenuItem("閉じて保存");
                        item2.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Close Tab and Preservation：閉じて保存");
                                karteTabPane2.getTabs().remove(karteTabPane2.getSelectionModel().getSelectedItem());
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        MenuItem item3 = new MenuItem("閉じる");
                        item3.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Close Tab：閉じる");
                                karteTabPane2.getTabs().remove(karteTabPane2.getSelectionModel().getSelectedItem());
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        contextMenu.getItems().addAll(item1, item2, item3);

                        Tab tab = new Tab(selectedUser.getName2());
                        tab.setContextMenu(contextMenu); // Right-click mouse button menu
                        try {
                            // Loading content on demand
                            Parent root = (Parent) new FXMLLoader().load(this.getClass().getResource("/resources/fxml/Karte.fxml").openStream());
                            tab.setContent(root);
                            karteTabPane2.getSelectionModel().select(tab);
                            karteTabPane2.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
                            karteTabPane2.getTabs().add(tab);
                            karteTabPane2.setPrefSize(kartePane2.getPrefWidth(), kartePane2.getPrefHeight());
                            kartePane2.getChildren().retainAll();
                            kartePane2.getChildren().add(karteTabPane2);
                        } catch (IOException ex) {
                            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }
        });

        // ラボレシーバ・テーブル用画面変数とテーブルカラムのバインド
        lab3.setCellValueFactory(new PropertyValueFactory<LabReceiverInfo, String>("lab3"));
        clientId3.setCellValueFactory(new PropertyValueFactory<LabReceiverInfo, String>("clientId3"));
        kana3.setCellValueFactory(new PropertyValueFactory<LabReceiverInfo, String>("kana3"));
        karteKana3.setCellValueFactory(new PropertyValueFactory<LabReceiverInfo, String>("karteKana3"));
        sex3.setCellValueFactory(new PropertyValueFactory<LabReceiverInfo, String>("sex3"));
        karteSex3.setCellValueFactory(new PropertyValueFactory<LabReceiverInfo, String>("karteSex3"));
        sampleGetDay3.setCellValueFactory(new PropertyValueFactory<LabReceiverInfo, String>("sampleGetDay3"));
        register3.setCellValueFactory(new PropertyValueFactory<LabReceiverInfo, String>("register3"));
        status3.setCellValueFactory(new PropertyValueFactory<LabReceiverInfo, String>("status3"));
        // dummyデータの登録
        LabRecieverView.getItems().setAll(fetchDataFromLabRecieverInfo());

        // ラボレシーバ・テーブルの行選択時アクション(個人用カルテタブ作成)
        LabRecieverView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        System.out.println("Double clicked");
                        LabReceiverInfo selectedUser = ((TableView<LabReceiverInfo>) mouseEvent.getSource()).getSelectionModel().getSelectedItem();
                        System.out.println(selectedUser.getKana3());
                        // タブの右クリックメニュー登録
                        final ContextMenu contextMenu = new ContextMenu();
                        MenuItem item1 = new MenuItem("保存");
                        item1.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Reserve Karte：保存");
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        MenuItem item2 = new MenuItem("閉じて保存");
                        item2.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Close Tab and Preservation：閉じて保存");
                                karteTabPane3.getTabs().remove(karteTabPane3.getSelectionModel().getSelectedItem());
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        MenuItem item3 = new MenuItem("閉じる");
                        item3.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent e) {
                                System.out.println("Close Tab：閉じる");
                                karteTabPane3.getTabs().remove(karteTabPane3.getSelectionModel().getSelectedItem());
                                // 保存処理実行
                                // e.getSource();
                            }
                        });
                        contextMenu.getItems().addAll(item1, item2, item3);

                        Tab tab = new Tab(selectedUser.getKana3());
                        tab.setContextMenu(contextMenu); // Right-click mouse button menu
                        try {
                            // Loading content on demand
                            Parent root = (Parent) new FXMLLoader().load(this.getClass().getResource("/resources/fxml/Karte.fxml").openStream());
                            tab.setContent(root);
                            karteTabPane3.getSelectionModel().select(tab);
                            karteTabPane3.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
                            karteTabPane3.getTabs().add(tab);
                            karteTabPane3.setPrefSize(kartePane3.getPrefWidth(), kartePane3.getPrefHeight());
                            kartePane3.getChildren().retainAll();
                            kartePane3.getChildren().add(karteTabPane3);
                        } catch (IOException ex) {
                            Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                }
            }
        });

        // 受付リストに5秒ごとに更新を行うように設定
        Timer exeTimer = new Timer();
        Calendar cal = Calendar.getInstance();
        final int sec = cal.get(Calendar.SECOND);
        int delay = (60 - sec) * 1000;
        int interval = 5 * 1000;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!stopFlag) {
                    System.out.println("this is called every 5 seconds on UI thread");
                    receptUpdate();
                } else {
                    this.cancel();
                }
            }
        };
        exeTimer.schedule(task, delay, interval);

    }

    public void setApp(Evolution application) {
        this.application = application;
    }

    public void setStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public void close(ActionEvent event) {
        stopFlag = true;
        this.frame.setVisible(false);
        this.application.loginDisplay();
    }

//    public void addWindow(ActionEvent event) {
//        Tab tab = new Tab();
//        tab.setText("aaaa1");
//        mainTab.getTabs().add(tab);
//        //- KarteWindow.getChildren().addAll(tabPane);
//    }

    private ObservableList<ReceptInfo> fetchDataFromServer() {
        // 受付リスト表示用
        List<ReceptInfo> list = new ArrayList<>();
        PVTDelegater delegater = PVTDelegater.getInstance();
        List<PatientVisitModel> pList = delegater.getPvtList();
        if (pList != null) {
            for (PatientVisitModel model : pList) {
                ReceptInfo bean = new ReceptInfo(String.valueOf(model.getNumber()), model.getPvtDate(), model.getPatientModel().getPatientId(), model.getPatientModel().getFullName(), model.getPatientModel().getGender(), model.getHealthInsuranceInfo(), model.getPatientModel().getBirthday(), model.getDoctorName(), model.getDeptName(), model.getAppointment(), model.getMemo(), String.valueOf(model.getState()));
                list.add(bean);
            }
        } else {
            // dummyデータ登録 ※開発完了時には削除
            list.add(new ReceptInfo("1", "14:23", "234-48", "西村しん", "男", "国保", "1900/01/11", "山本真一", "胃腸科", "", "email@softbank.com", "0"));
            list.add(new ReceptInfo("2", "09:45", "213-48", "山田さよう", "男", "労災", "1900/12/11", "いのもと順他", "脳外科", "", "email@kddi.com", "2"));
            list.add(new ReceptInfo("3", "12:11", "118-48", "橋本ひん", "女", "自責", "1900/11/11", "金本高", "産婦人科", "", "email@google.com", "3"));
            list.add(new ReceptInfo("4", "01:55", "034-48", "山下うえ", "女", "自責", "1900/10/11", "谷じんに", "皮膚科", "", "email@microsofut.com", "4"));
            list.add(new ReceptInfo("5", "12:30", "083-48", "花田こおる", "男", "任保", "1900/04/11", "高本さん", "産婦人科", "", "email@apple.com", "8"));
        }
        return FXCollections.observableList(list);
    }

    private ObservableList<PatientSearchInfo> fetchDataFromPatientInfo() {
        List<PatientSearchInfo> list = new ArrayList<>();
        list.add(new PatientSearchInfo("1", "西村しん", "ﾆｼﾑﾗｼﾝ", "男", "1900/01/11", "09/08", "良"));
        list.add(new PatientSearchInfo("2", "香取信吾", "ｶﾄﾘｼﾝｺﾞ", "男", "1900/11/11", "01/08", "良"));
        list.add(new PatientSearchInfo("3", "木村卓也", "ｷﾑﾗﾀｸﾔ", "男", "1900/04/11", "07/31", ""));
        list.add(new PatientSearchInfo("4", "草薙強", "ｸｻﾅｷﾞﾂﾖｼ", "男", "1900/05/11", "07/04", "良"));
        list.add(new PatientSearchInfo("5", "稲垣京子", "ﾅﾅｶﾞｷｷｮｳｺ", "女", "1900/12/11", "03/08", "不可"));
        return FXCollections.observableList(list);
    }

    private ObservableList<PatientFutureInfo> fetchDataFromPatientFutureInfo() {
        List<PatientFutureInfo> list = new ArrayList<>();
        list.add(new PatientFutureInfo("1", "西村しん", "ﾆｼﾑﾗｼﾝ", "男", "任保", "1900/01/11", "山本真一", "胃腸科", "3"));
        list.add(new PatientFutureInfo("2", "香取信吾", "ｶﾄﾘｼﾝｺﾞ", "男", "自責", "1900/11/11", "北本ゆう", "皮膚科", "9"));
        list.add(new PatientFutureInfo("3", "木村卓也", "ｷﾑﾗﾀｸﾔ", "男", "国保", "1900/04/11", "野方大", "胃腸科", "2"));
        list.add(new PatientFutureInfo("4", "草薙強", "ｸｻﾅｷﾞﾂﾖｼ", "男", "任保", "1900/05/11", "山真", "内科", "8"));
        list.add(new PatientFutureInfo("5", "稲垣京子", "ﾅﾅｶﾞｷｷｮｳｺ", "女", "自責", "1900/12/11", "深意", "眼科", "4"));
        return FXCollections.observableList(list);
    }

    private ObservableList<LabReceiverInfo> fetchDataFromLabRecieverInfo() {
        List<LabReceiverInfo> list = new ArrayList<>();
        list.add(new LabReceiverInfo("kyoto-lab", "1", "ﾆｼﾑﾗｼﾝ", "ｼﾝｿﾞｳｹﾝｻ", "男", "1900/01/11", "山本真一", "山田", "3"));
        list.add(new LabReceiverInfo("cicago-lab", "2", "ｶﾄﾘｼﾝｺﾞ", "ﾉｳｹﾞｶ", "男", "1900/11/11", "北本ゆう", "川田", "9"));
        list.add(new LabReceiverInfo("osaka-lab", "3", "ｷﾑﾗﾀｸﾔ", "ｶﾝｿﾞｳﾏﾜﾘ", "男", "1900/04/11", "野方大", "上本", "2"));
        list.add(new LabReceiverInfo("kyoto-lab", "4", "ｸｻﾅｷﾞﾂﾖｼ", "ｹﾞｶ", "男", "1900/05/11", "山真", "澤田", "8"));
        list.add(new LabReceiverInfo("soul-lab", "5", "ﾅﾅｶﾞｷｷｮｳｺ", "ﾅｲｶ", "女", "1900/12/11", "深意", "金田", "4"));
        return FXCollections.observableList(list);
    }

    @FXML
    protected void receptUpdate() {
        ReceptView.getItems().setAll(fetchDataFromServer());
    }

    @FXML
    protected void updatePatientFuture() {
        PatientFutureView.getItems().setAll(fetchDataFromPatientFutureInfo());
    }

    @FXML
    protected void fileChoose() {
        FileChooser fc = new FileChooser();
        fc.setTitle("select file");
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        fc.getExtensionFilters().add(new ExtensionFilter("すべてのファイル", "*.*"));

        File f = fc.showOpenDialog(null);
        if (f != null) {
            try {
                Path path = f.toPath();
                choiceFile.setText(path.getFileName().toString());
                for (String s : Files.readAllLines(path, Charset.forName("SJIS"))) {
                    System.out.println(s);
                }
            } catch (IOException ex) {
                Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    protected void registerInspectionResult() {
        System.out.println("検査結果登録処理の実施ロジック！");
    }

    @FXML
    protected void fileClear() {
        if (StringUtils.isNotEmpty(choiceFile.getText())) {
            choiceFile.setText("未選択");
        }
    }

    /**
     * align text of the cell to right in the specified column
     *
     * @param col table column to be aligned to right
     */
    private void tableCellAlignRight(TableColumn col) {
        col.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell cell = new TableCell() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        if (item != null) {
                            setText(item.toString());
                        }
                    }
                };
                cell.setAlignment(Pos.BOTTOM_RIGHT);
                return cell;
            }
        });
    }

    /**
     * align text of the cell to center in the specified column
     *
     * @param col table column to be aligned to center
     */
    private void tableCellAlignCenter(TableColumn col) {
        col.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell cell = new TableCell() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        if (item != null) {
                            setText(item.toString());
                        }
                    }
                };
                cell.setAlignment(Pos.BOTTOM_CENTER);
                return cell;
            }
        });
    }

    /**
     * align Image of the cell to center in the specified column
     *
     * @param col table column to be aligned to center
     */
    private void tableCellImageAlignCenter(TableColumn col) {
        col.setCellFactory(new Callback<TableColumn, TableCell>() {
            @Override
            public TableCell call(TableColumn param) {
                TableCell cell = new TableCell() {
                    @Override
                    public void updateItem(Object item, boolean empty) {
                        String imagesPath = "/resources/images/";

                        super.updateItem(item, empty);
                        if (!empty) {
                            switch (item.toString()) {
                                case "0":
                                    setTooltip(new Tooltip("カルテ閲覧中"));
                                    folderIcon = new Image(imagesPath + "open_16.gif");
                                    break;
                                case "1":
                                    setTooltip(new Tooltip("カルテ修正/再保存"));
                                    folderIcon = new Image(imagesPath + "open_16.gif");
                                    break;
                                case "2":
                                    setTooltip(new Tooltip("カルテ修正/再保存"));
                                    folderIcon = new Image(imagesPath + "sinfo_16.gif");
                                    break;
                                case "3":
                                    setTooltip(new Tooltip("検査/処置中"));
                                    folderIcon = new Image(imagesPath + "os_cog_16.png");
                                    break;
                                case "4":
                                    setTooltip(new Tooltip("急患"));
                                    folderIcon = new Image(imagesPath + "fastf_16.gif");
                                    break;
                                case "5":
                                    setTooltip(new Tooltip("外出中"));
                                    folderIcon = new Image(imagesPath + "open_16.gif");
                                    break;
                                case "6":
                                    setTooltip(new Tooltip("キャンセル"));
                                    folderIcon = new Image(imagesPath + "cancl_16.gif");
                                    break;
                                case "8":
                                    setTooltip(new Tooltip("診療終了"));
                                    folderIcon = new Image(imagesPath + "flag_16.gif");
                                    break;
                            }
                            setGraphic(new ImageView(folderIcon));
                        }
                    }
                };
                cell.setAlignment(Pos.BOTTOM_CENTER);
                return cell;
            }
        });
    }
}
