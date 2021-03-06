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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import open.dolphin.client.AbstractMainComponent;
import open.dolphin.client.AutoKanjiListener;
import open.dolphin.client.ChartEventHandler;
import open.dolphin.client.ClientContext;
import open.dolphin.client.GUIConst;
import open.dolphin.client.ReflectAction;
import open.dolphin.delegater.PVTDelegater;
import open.dolphin.impl.server.PVTReceptionLink;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModelUtils;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.project.Project;
import open.dolphin.table.ColumnSpec;
import open.dolphin.table.ColumnSpecHelper;
import open.dolphin.table.ListTableModel;
import open.dolphin.table.ListTableSorter;
import open.dolphin.table.StripeTableCellRenderer;
import open.dolphin.util.AgeCalculator;
import org.apache.commons.lang.time.DurationFormatUtils;

/**
 * 受付リスト
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class WatingListImpl extends AbstractMainComponent implements PropertyChangeListener {

    public static final ImageIcon OPEN_ICON = ClientContext.getImageIconArias("icon_karte_open_state_small");
    public static final ImageIcon NETWORK_ICON = ClientContext.getImageIconArias("icon_karte_open_someone_small");

    // Window Title
    private static final String NAME = "受付リスト";

    // 担当分のみを表示するかどうかの preference key
    private static final String ASSIGNED_ONLY = "assignedOnly";

    // 修正送信アイコンの配列インデックス
    private static final int INDEX_MODIFY_SEND_ICON = 1;

    // 担当医未定の ORCA 医師ID
    private static final String UN_ASSIGNED_ID = "18080";

    // 受付キャンセルカラー
    private static final Color CANCEL_PVT_COLOR = new Color(128, 128, 128);

    // 来院テーブルのカラム名
    private static final String[] COLUMN_NAMES = {
        "受付", "患者ID", "来院時間", "氏名", "性別", "保険",
        "生年月日", "担当医", "診療科", "予約", "メモ", "状態"};

    // 来院テーブルのカラムメソッド
    private static final String[] PROPERTY_NAMES = {
        "getNumber", "getPatientId", "getPvtDateTrimDate", "getPatientName", "getPatientGenderDesc", "getFirstInsurance",
        "getPatientAgeBirthday", "getDoctorName", "getDeptName", "getAppointment", "getMemo", "getStateInteger"};

    // 来院テーブルのクラス名
    private static final Class[] COLUMN_CLASSES = {
        Integer.class, String.class, String.class, String.class, String.class, String.class,
        String.class, String.class, String.class, String.class, String.class, Integer.class};

    // 来院テーブルのカラム幅
    private static final int[] COLUMN_WIDTH = {
        20, 80, 60, 100, 40, 130,
        130, 50, 60, 40, 80, 30};

    // 年齢生年月日メソッド 
    private final String[] AGE_METHOD = {"getPatientAgeBirthday", "getPatientBirthday"};

    // カラム仕様名
    private static final String COLUMN_SPEC_NAME = "pvtTable.column.spec";

    // state Clumn Identifier
    private static final String COLUMN_IDENTIFIER_STATE = "stateColumn";

    // カラム仕様ヘルパー
    private ColumnSpecHelper columnHelper;

    // 受付時間カラム
    private int visitedTimeColumn;

    // 性別カラム
    private int sexColumn;

    // 年齢表示カラム
    private int ageColumn;

    // 来院情報テーブルのメモカラム
    private int memoColumn;

    // 来院情報テーブルのステータスカラム
    private int stateColumn;

    // 受付番号カラム
    private int numberColumn;

    // PVT Table 
    private JTable pvtTable;

    // Table Model
    private ListTableModel<PatientVisitModel> pvtTableModel;

    // TableSorter
    private ListTableSorter sorter;

    // 性別レンダラフラグ 
    private boolean sexRenderer;

    // 年齢表示 
    private boolean ageDisplay;

    // 選択されている行を保存
    private int selectedRow;

    // 選択されている行を保存
    private int[] selectedRows;

    // View class
    private WatingListView view;

    // 更新時刻フォーマッタ
    private SimpleDateFormat timeFormatter;

    // Chart State
    private final Integer[] chartBitArray = {
        new Integer(PatientVisitModel.BIT_OPEN),
        new Integer(PatientVisitModel.BIT_MODIFY_CLAIM),
        new Integer(PatientVisitModel.BIT_SAVE_CLAIM)};

    // Chart State を表示するアイコン
    private final ImageIcon[] chartIconArray = {
        OPEN_ICON,
        ClientContext.getImageIconArias("icon_karte_modified_small"),
        ClientContext.getImageIconArias("icon_sent_claim_small")};

    // State ComboBox
    private final Integer[] userBitArray = {0, 3, 4, 5, 6};
    private final ImageIcon[] userIconArray = {
        null,
        ClientContext.getImageIconArias("icon_under_treatment_small"),
        ClientContext.getImageIconArias("icon_emergency_small"),
        ClientContext.getImageIconArias("icon_under_shopping_small"),
        ClientContext.getImageIconArias("icon_cancel_small")
    };

    private ImageIcon modifySendIcon;

    // Status　情報　メインウィンドウの左下に表示される内容
    private String statusInfo;

    // State 設定用のcombobox model
    private BitAndIconPair[] stateComboArray;

    // State 設定用のcombobox
    private JComboBox stateCmb;

    private AbstractAction copyAction;

    // 受付数・待ち時間の更新間隔
    private static final int intervalSync = 60;

    // pvtUpdateTask
    private ScheduledExecutorService executor;
    private ScheduledFuture schedule;
    private Runnable timerTask;

    // pvtCount
    private int totalPvtCount;
    private int waitingPvtCount;
    private Date waitingPvtDate;

    // PatientVisitModelの全部
    private List<PatientVisitModel> pvtList;

    // pvt delegater
    private PVTDelegater pvtDelegater;

    // Commet staff
    private final String clientUUID;
    private final ChartEventHandler cel;
    private final String orcaId;

    /**
     * Creates new WatingList
     */
    public WatingListImpl() {
        setName(NAME);
        cel = ChartEventHandler.getInstance();
        clientUUID = cel.getClientUUID();
        orcaId = Project.getUserModel().getOrcaId();
    }

    /**
     * プログラムを開始する。
     */
    @Override
    public void start() {

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    setup();
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(WatingListImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        view.initFxResouce();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    initComponents();
                    connect();
                    startSyncMode();
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            Logger.getLogger(WatingListImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void setup() {

        // ColumnSpecHelperを準備する
        columnHelper = new ColumnSpecHelper(COLUMN_SPEC_NAME,
                COLUMN_NAMES, PROPERTY_NAMES, COLUMN_CLASSES, COLUMN_WIDTH);
        columnHelper.loadProperty();

        // Scan して age, memo, state カラムを設定する
        visitedTimeColumn = columnHelper.getColumnPosition("getPvtDateTrimDate");
        sexColumn = columnHelper.getColumnPosition("getPatientGenderDesc");
        ageColumn = columnHelper.getColumnPositionEndsWith("Birthday");
        memoColumn = columnHelper.getColumnPosition("getMemo");
        stateColumn = columnHelper.getColumnPosition("getStateInteger");
        numberColumn = columnHelper.getColumnPosition("getNumber");

        // 修正送信アイコンを決める
        if (Project.getBoolean("change.icon.modify.send", true)) {
            modifySendIcon = ClientContext.getImageIconArias("icon_karte_modified_small");
        } else {
            modifySendIcon = ClientContext.getImageIconArias("icon_sent_claim_small");
        }
        chartIconArray[INDEX_MODIFY_SEND_ICON] = modifySendIcon;

        stateComboArray = new BitAndIconPair[userBitArray.length];
        for (int i = 0; i < userBitArray.length; i++) {
            stateComboArray[i] = new BitAndIconPair(userBitArray[i], userIconArray[i]);
        }
        stateCmb = new JComboBox(stateComboArray);
        ComboBoxRenderer renderer = new ComboBoxRenderer();
        renderer.setPreferredSize(new Dimension(30, ClientContext.getHigherRowHeight()));
        stateCmb.setRenderer(renderer);
        stateCmb.setMaximumRowCount(userBitArray.length);

        sexRenderer = Project.getBoolean("sexRenderer", false);
        ageDisplay = Project.getBoolean("ageDisplay", true);
        timeFormatter = new SimpleDateFormat("HH:mm");

        executor = Executors.newSingleThreadScheduledExecutor();

        pvtDelegater = PVTDelegater.getInstance();

        // 来院リスト
        pvtList = new ArrayList<>();
        view = new WatingListView();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                countPvt();
                updatePvtInfo();
            }
        });
    }

    /**
     * GUI コンポーネントを初期化しレアイアウトする。
     */
    private void initComponents() {

        // View クラスを生成しこのプラグインの UI とする
        setUI(view);

        view.setTextComePvt("");
        view.setTextNowTime("");
        view.setTextWaitPvt("");
        view.setTextWaitTime("");

        pvtTable = view.getTable();

        // ColumnSpecHelperにテーブルを設定する
        columnHelper.setTable(pvtTable);

        // View のテーブルモデルを置き換える
        String[] columnNames;
        columnNames = columnHelper.getTableModelColumnNames();
        String[] methods;
        methods = columnHelper.getTableModelColumnMethods();
        Class[] cls;
        cls = columnHelper.getTableModelColumnClasses();

        pvtTableModel = new ListTableModel<PatientVisitModel>(columnNames, 0, methods, cls) {

            @Override
            public boolean isCellEditable(int row, int col) {

                boolean canEdit = true;

                // メモか状態カラムの場合
                canEdit = canEdit && ((col == memoColumn) || (col == stateColumn));

                // null でない場合
                canEdit = canEdit && (getObject(row) != null);

                if (!canEdit) {
                    return false;
                }

                // statusをチェックする
                PatientVisitModel pvt = getObject(row);

                if (pvt.getStateBit(PatientVisitModel.BIT_CANCEL)) {
                    // cancel case
                    canEdit = false;

                } else {
                    for (Integer chartBitArray1 : chartBitArray) {
                        if (pvt.getStateBit(chartBitArray1)) {
                            canEdit = false;
                            break;
                        }
                    }
                }

                if (canEdit) {
                    int index = 0;
                    for (int i = 1; i < userBitArray.length; i++) {
                        if (pvt.getStateBit(userBitArray[i])) {
                            index = i;
                            break;
                        }
                    }
                    stateCmb.setSelectedIndex(index);
                }
                return canEdit;
            }

            @Override
            public Object getValueAt(int row, int col) {

                Object ret = null;

                if (col == ageColumn && ageDisplay) {

                    PatientVisitModel p = getObject(row);

                    if (p != null) {
                        int showMonth = Project.getInt("ageToNeedMonth", 6);
                        ret = AgeCalculator.getAgeAndBirthday(p.getPatientModel().getBirthday(), showMonth);
                    }
                } else {

                    ret = super.getValueAt(row, col);
                }

                return ret;
            }

            @Override
            public void setValueAt(Object value, int row, int col) {

                // ここはsorterから取得したらダメ
                //final PatientVisitModel pvt = (PatientVisitModel) sorter.getObject(row);
                final PatientVisitModel pvt = pvtTableModel.getObject(row);

                if (pvt == null || value == null) {
                    return;
                }

                // Memo
                if (col == memoColumn) {
                    String memo = ((String) value).trim();

                    if (pvt.getMemo() != null && pvt.getMemo().trim().equals(memo.trim())) {
                        return; //データが変更していないので
                    }
                    if (pvt.getMemo() == null && memo.trim().length() == 0) {
                        return; // データが変更していないので
                    }
                    pvt.setMemo(memo);
                    cel.publishPvtState(pvt);

                } else if (col == stateColumn) {

                    // State ComboBox の value
                    BitAndIconPair pair = (BitAndIconPair) value;
                    int theBit = pair.getBit().intValue();

                    if (theBit == PatientVisitModel.BIT_CANCEL) {

                        stateCmb.hidePopup();   // add funabashi リストが消えない対応

                        Object[] cstOptions;
                        cstOptions = new Object[]{"はい", "いいえ"};

                        StringBuilder sb = new StringBuilder(pvt.getPatientName());
                        sb.append("様の受付を取り消しますか?");
                        String msg = sb.toString();

                        int select = JOptionPane.showOptionDialog(
                                SwingUtilities.getWindowAncestor(pvtTable),
                                msg,
                                ClientContext.getFrameTitle(getName()),
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                ClientContext.getImageIcon("os_cancel_16.png"),
                                cstOptions, "はい");

                        System.err.println("select=" + select);

                        if (select != 0) {
                            return;
                        }
                    }

                    int oldState = pvt.getState();

                    // unset all
                    pvt.setState(0);

                    // set the bit
                    if (theBit != 0) {
                        pvt.setStateBit(theBit, true);
                    }

                    if (pvt.getState() == oldState) {
                        return; //データが変更していないので
                    }

                    cel.publishPvtState(pvt);
                }
            }
        };

        // sorter組み込み
        sorter = new ListTableSorter(pvtTableModel);
        pvtTable.setModel(sorter);
        sorter.setTableHeader(pvtTable.getTableHeader());

        // 選択モード
        pvtTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Memo 欄 clickCountToStart=1
        JTextField tf = new JTextField();
        tf.addFocusListener(AutoKanjiListener.getInstance());
        DefaultCellEditor de = new DefaultCellEditor(tf);
        de.setClickCountToStart(1);
        pvtTable.getColumnModel().getColumn(memoColumn).setCellEditor(de);

        // 性別レンダラを生成する
        MaleFemaleRenderer sRenderer = new MaleFemaleRenderer();
        sRenderer.setTable(pvtTable);

        // Center Renderer
        CenterRenderer centerRenderer = new CenterRenderer();
        centerRenderer.setTable(pvtTable);

        List<ColumnSpec> columnSpecs = columnHelper.getColumnSpecs();
        for (int i = 0; i < columnSpecs.size(); i++) {

            if (i == visitedTimeColumn || i == sexColumn) {
                pvtTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);

            } else if (i == stateColumn) {
                // カルテ(PVT)状態レンダラ
                KarteStateRenderer renderer = new KarteStateRenderer();
                renderer.setTable(pvtTable);
                renderer.setHorizontalAlignment(JLabel.CENTER);
                pvtTable.getColumnModel().getColumn(i).setCellRenderer(renderer);

            } else {
                pvtTable.getColumnModel().getColumn(i).setCellRenderer(sRenderer);
            }
        }

        // PVT状態設定エディタ
        pvtTable.getColumnModel().getColumn(stateColumn).setCellEditor(new DefaultCellEditor(stateCmb));
        pvtTable.getColumnModel().getColumn(stateColumn).setIdentifier(COLUMN_IDENTIFIER_STATE);

        // 受付リストをダブルクリック時、カルテオープン
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        pvtTable.getInputMap(JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(enter, "solve");
        pvtTable.getActionMap().put("solve", new EnterAction());

        // カラム幅更新
        columnHelper.updateColumnWidth();

        // 行高
        if (ClientContext.isWin()) {
            pvtTable.setRowHeight(ClientContext.getMoreHigherRowHeight());
        } else {
            pvtTable.setRowHeight(ClientContext.getHigherRowHeight());
        }
    }

    private class EnterAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            openKartes();
        }
    }

    /**
     * コンポーネントにイベントハンドラーを登録し相互に接続する。
     */
    private void connect() {

        // ColumnHelperでカラム変更関連イベントを設定する
        columnHelper.connect();

        // 来院リストテーブル 選択
        pvtTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting() == false) {
                    selectedRow = pvtTable.getSelectedRow();
                    controlMenu();
                }
            }
        });

        // 来院リストテーブル ダブルクリック
        view.getTable().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openKarte();
                }
            }
        });

        // コンテキストメニューを登録する
        view.getTable().addMouseListener(new ContextListener());

        // 靴のアイコンをクリックした時来院情報を検索する
        //- JFX対応
        view.getBtn().setOnAction(new javafx.event.EventHandler<javafx.event.ActionEvent>() {
            @Override
            public void handle(javafx.event.ActionEvent event) {
                getFullPvt();
            }
        });

        //- Copy 機能を実装する
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
        copyAction = new AbstractAction("コピー") {

            @Override
            public void actionPerformed(ActionEvent ae) {
                copyRow();
            }
        };
        pvtTable.getInputMap().put(copy, "Copy");
        pvtTable.getActionMap().put("Copy", copyAction);
    }

    // comet long polling機能を設定する
    private void startSyncMode() {
        setStatusInfo();
        getFullPvt();
        cel.addPropertyChangeListener(this);
        timerTask = new UpdatePvtInfoTask();
        restartTimer();
        updatePvtInfo();
        countPvt();
        enter();
    }

    /**
     * タイマーをリスタートする。
     */
    private void restartTimer() {

        if (schedule != null && !schedule.isCancelled()) {
            if (!schedule.cancel(true)) {
                return;
            }
        }

        // 同期モードでは毎分０秒に待ち患者数を更新する
        GregorianCalendar now = new GregorianCalendar();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(now.getTime());
        gc.clear(GregorianCalendar.SECOND);
        gc.clear(GregorianCalendar.MILLISECOND);
        gc.add(GregorianCalendar.MINUTE, 1);
        long delay = gc.getTimeInMillis() - now.getTimeInMillis();
        long interval = intervalSync * 1000;

        schedule = executor.scheduleWithFixedDelay(timerTask, delay, interval, TimeUnit.MILLISECONDS);
    }

    /**
     * メインウインドウのタブで受付リストに切り替わった時 コールされる。
     */
    @Override
    public void enter() {
        controlMenu();
        getContext().getStatusLabel().setText(statusInfo);
//        getContext().getStatusLabel().setText("");
    }

    /**
     * プログラムを終了する。
     */
    @Override
    public void stop() {

        // ColumnSpecsを保存する
        if (columnHelper != null) {
            columnHelper.saveProperty();
        }
        // ChartStateListenerから除去する
        cel.removePropertyChangeListener(this);
    }

    /**
     * 性別レンダラかどうかを返す。
     *
     * @return 性別レンダラの時 true
     */
    public boolean isSexRenderer() {
        return sexRenderer;
    }

    /**
     * レンダラをトグルで切り替える。
     */
    public void switchRenderere() {
        sexRenderer = !sexRenderer;
        Project.setBoolean("sexRenderer", sexRenderer);
        if (pvtTable != null) {
            pvtTableModel.fireTableDataChanged();
        }
    }

    /**
     * 年齢表示をオンオフする。
     */
    public void switchAgeDisplay() {
        if (pvtTable != null) {
            ageDisplay = !ageDisplay;
            Project.setBoolean("ageDisplay", ageDisplay);
            String method = ageDisplay ? AGE_METHOD[0] : AGE_METHOD[1];
            pvtTableModel.setProperty(method, ageColumn);
            List<ColumnSpec> columnSpecs = columnHelper.getColumnSpecs();
            for (int i = 0; i < columnSpecs.size(); i++) {
                ColumnSpec cs = columnSpecs.get(i);
                String test = cs.getMethod();
                if (test.toLowerCase().endsWith("birthday")) {
                    cs.setMethod(method);
                    break;
                }
            }
        }
    }

    /**
     * テーブル及び靴アイコンの enable/diable 制御
     *
     * @param busy pvt 検索中は true
     */
    private void setBusy(final boolean busy) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (busy) {
                    view.getBtn().setDisable(true);
                    if (getContext().getCurrentComponent() == getUI()) {
                        getContext().block();
                        getContext().getProgressBar().setIndeterminate(true);
                    }
                    selectedRow = pvtTable.getSelectedRow();
                } else {
                    view.getBtn().setDisable(false);
                    if (getContext().getCurrentComponent() == getUI()) {
                        getContext().unblock();
                        getContext().getProgressBar().setIndeterminate(false);
                        getContext().getProgressBar().setValue(0);
                    }
                    pvtTable.getSelectionModel().addSelectionInterval(selectedRow, selectedRow);
                }
            }
        });
    }

    /**
     * 選択されている来院情報を設定返す
     *
     * @return 選択されている来院情報
     */
    public PatientVisitModel getSelectedPvt() {
        selectedRow = pvtTable.getSelectedRow();
        return (PatientVisitModel) sorter.getObject(selectedRow);
    }

    /**
     * 選択されている来院情報を複数設定返す
     *
     * @return 選択されている来院情報
     */
    public List<PatientVisitModel> getSelectedPvts() {
        selectedRows = pvtTable.getSelectedRows();
        List<PatientVisitModel> list = new LinkedList<>();
        for (int i = 0; i < selectedRows.length; i++) {
            list.add((PatientVisitModel) sorter.getObject(selectedRows[i]));
        }
        return list;
    }

    /**
     * カルテオープンメニューを制御する。
     */
    private void controlMenu() {
        PatientVisitModel pvt = getSelectedPvt();
        boolean enabled = canOpen(pvt);
        getContext().enabledAction(GUIConst.ACTION_OPEN_KARTE, enabled);
    }

    public void openKarte() {
        getContext().setTrace("WatingList");

        PatientVisitModel pvt = getSelectedPvt();
        if (pvt == null) {
            return;
        }
        getContext().openKarte(pvt);
    }

    public void openKartes() {
        getContext().setTrace("WatingList");

        List<PatientVisitModel> pvt = getSelectedPvts();
        if (pvt == null) {
            return;
        }
        for (int i = 0; i < pvt.size(); i++) {
            getContext().openKarte(pvt.get(i));
        }
    }

    /**
     * カルテを開くことが可能かどうかを返す
     *
     * @return 開くことが可能な時 true
     */
    private boolean canOpen(PatientVisitModel pvt) {

        if (pvt == null) {
            return false;
        }
        // Cancelなら開けない
        if (pvt.getStateBit(PatientVisitModel.BIT_CANCEL)) {
            return false;
        }
        return !pvt.getStateBit(PatientVisitModel.BIT_OPEN);
    }

    /**
     * 受付リストのコンテキストメニュークラス。
     */
    private class ContextListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            mabeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            mabeShowPopup(e);
        }

        public void mabeShowPopup(MouseEvent e) {

            if (e.isPopupTrigger()) {

                final JPopupMenu contextMenu = new JPopupMenu();
                String pop3 = "偶数奇数レンダラを使用する";
                String pop4 = "性別レンダラを使用する";
                String pop5 = "年齢表示";
                String pop6 = "担当分のみ表示";
                String pop7 = "修正送信を注意アイコンにする";

                int row = pvtTable.rowAtPoint(e.getPoint());
                PatientVisitModel obj = getSelectedPvt();

                if (row == selectedRow && obj != null && !obj.getStateBit(PatientVisitModel.BIT_CANCEL)) {
                    String pop1 = "カルテを開く";
                    contextMenu.add(new JMenuItem(
                            new ReflectAction(pop1, WatingListImpl.this, "openKarte")));
                    contextMenu.addSeparator();
                    contextMenu.add(new JMenuItem(copyAction));

                    // pvt削除は誰も開いていない場合のみ
                    if (obj.getPatientModel().getOwnerUUID() == null) {
                        contextMenu.add(new JMenuItem(
                                new ReflectAction("受付削除", WatingListImpl.this, "removePvt")));
                    }
                    contextMenu.addSeparator();
                }

                // pvt cancelのundo
                if (row == selectedRow && obj != null && obj.getStateBit(PatientVisitModel.BIT_CANCEL)) {
                    contextMenu.add(new JMenuItem(
                            new ReflectAction("キャンセル取消", WatingListImpl.this, "undoCancelPvt")));
                    contextMenu.addSeparator();
                }

                JRadioButtonMenuItem oddEven = new JRadioButtonMenuItem(
                        new ReflectAction(pop3, WatingListImpl.this, "switchRenderere"));
                JRadioButtonMenuItem sex = new JRadioButtonMenuItem(
                        new ReflectAction(pop4, WatingListImpl.this, "switchRenderere"));
                ButtonGroup bg = new ButtonGroup();
                bg.add(oddEven);
                bg.add(sex);
                contextMenu.add(oddEven);
                contextMenu.add(sex);
                if (sexRenderer) {
                    sex.setSelected(true);
                } else {
                    oddEven.setSelected(true);
                }

                JCheckBoxMenuItem item = new JCheckBoxMenuItem(pop5);
                contextMenu.add(item);
                item.setSelected(ageDisplay);
                item.addActionListener(
                        EventHandler.create(ActionListener.class, WatingListImpl.this, "switchAgeDisplay"));

                // 担当分のみ表示: getOrcaId() != nullでメニュー
                if (orcaId != null) {
                    contextMenu.addSeparator();

                    // 担当分のみ表示
                    JCheckBoxMenuItem item2 = new JCheckBoxMenuItem(pop6);
                    contextMenu.add(item2);
                    item2.setSelected(isAssignedOnly());
                    item2.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent ae) {
                            boolean now = isAssignedOnly();
                            Project.setBoolean(ASSIGNED_ONLY, !now);
                            filterPatients();
                        }
                    });
                }

                // 修正送信を注意アイコンにする ON/OF default = ON
                JCheckBoxMenuItem item3 = new JCheckBoxMenuItem(pop7);
                contextMenu.add(item3);
                item3.setSelected(Project.getBoolean("change.icon.modify.send", true));
                item3.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        boolean curIcon = Project.getBoolean("change.icon.modify.send", true);
                        boolean change = !curIcon;
                        Project.setBoolean("change.icon.modify.send", change);
                        changeModiSendIcon();
                    }
                });

                JMenu menu = columnHelper.createMenuItem();
                contextMenu.add(menu);

                contextMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    /**
     * 修正送信アイコンを決める
     *
     * @param change
     */
    private void changeModiSendIcon() {

        // 修正送信アイコンを決める
        if (Project.getBoolean("change.icon.modify.send", true)) {
            modifySendIcon = ClientContext.getImageIconArias("icon_karte_modified_small");
        } else {
            modifySendIcon = ClientContext.getImageIconArias("icon_sent_claim_small");
        }
        chartIconArray[INDEX_MODIFY_SEND_ICON] = modifySendIcon;

        // 表示を更新する
        pvtTableModel.fireTableDataChanged();
    }

    /**
     * 選択されている行をコピーする。
     */
    public void copyRow() {

        StringBuilder sb = new StringBuilder();
        int numRows = pvtTable.getSelectedRowCount();
        int[] rowsSelected = pvtTable.getSelectedRows();
        int numColumns = pvtTable.getColumnCount();

        for (int i = 0; i < numRows; i++) {
            if (sorter.getObject(rowsSelected[i]) != null) {
                StringBuilder s = new StringBuilder();
                for (int col = 0; col < numColumns; col++) {
                    Object o = pvtTable.getValueAt(rowsSelected[i], col);
                    if (o != null) {
                        s.append(o.toString());
                    }
                    s.append(",");
                }
                if (s.length() > 0) {
                    s.setLength(s.length() - 1);
                }
                sb.append(s.toString()).append("\n");
            }
        }
        if (sb.length() > 0) {
            StringSelection stsel = new StringSelection(sb.toString());
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stsel, stsel);
        }
    }

    /**
     * 選択した患者の受付キャンセルをundoする。masuda
     */
    public void undoCancelPvt() {

        final PatientVisitModel pvtModel = getSelectedPvt();

        // ダイアログを表示し確認する
        StringBuilder sb = new StringBuilder(pvtModel.getPatientName());
        sb.append("様の受付キャンセルを取り消しますか?");
        if (!showCancelDialog(sb.toString())) {
            return;
        }

        // updateStateする。
        pvtModel.setStateBit(PatientVisitModel.BIT_CANCEL, false);
        cel.publishPvtState(pvtModel);
    }

    /**
     * 選択した患者の受付を削除する。masuda
     */
    public void removePvt() {

        final PatientVisitModel pvtModel = getSelectedPvt();

        // ダイアログを表示し確認する
        StringBuilder sb = new StringBuilder(pvtModel.getPatientName());
        sb.append("様の受付を削除しますか?");
        if (!showCancelDialog(sb.toString())) {
            return;
        }

        cel.publishPvtDelete(pvtModel);
    }

    private boolean showCancelDialog(String msg) {

        final String[] cstOptions;
        cstOptions = new String[]{"はい", "いいえ"};

        int select = JOptionPane.showOptionDialog(
                SwingUtilities.getWindowAncestor(pvtTable),
                msg,
                ClientContext.getFrameTitle(getName()),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                ClientContext.getImageIconArias("icon_caution"),
                cstOptions, cstOptions[1]);
        return (select == 0);
    }

    private class BitAndIconPair {

        private final Integer bit;
        private final ImageIcon icon;

        public BitAndIconPair(Integer bit, ImageIcon icon) {
            this.bit = bit;
            this.icon = icon;
        }

        public Integer getBit() {
            return bit;
        }

        public ImageIcon getIcon() {
            return icon;
        }
    }

// 左下のstatus infoを設定する
    private void setStatusInfo() {

        StringBuilder sb = new StringBuilder();
        sb.append("更新間隔: ");
        sb.append(intervalSync);
        sb.append("秒 ");
        sb.append("同期");
        statusInfo = sb.toString();
    }

    // 更新時間・待ち人数などを設定する
    private void updatePvtInfo() {

        String waitingTime = "00:00";
        Date now = new Date();

        //- 現在時刻
        view.setTextNowTime(timeFormatter.format(now));
        //- 来院数
        view.setTextComePvt(String.valueOf(totalPvtCount));
        //- 待ち
        view.setTextWaitPvt(String.valueOf(waitingPvtCount));
        //- 待時間
        if (waitingPvtDate != null && now.after(waitingPvtDate)) {
            waitingTime = DurationFormatUtils.formatPeriod(waitingPvtDate.getTime(), now.getTime(), "HH:mm");
        }
        view.setTextWaitTime(waitingTime);
    }

    /**
     * 来院数，待人数，待時間表示, modified by masuda
     */
    private void countPvt() {

        waitingPvtCount = 0;
        totalPvtCount = 0;
        waitingPvtDate = null;

        List<PatientVisitModel> dataList = pvtTableModel.getDataProvider();

        for (int i = 0; i < dataList.size(); i++) {
            PatientVisitModel pvt = dataList.get(i);
            if (!pvt.getStateBit(PatientVisitModel.BIT_SAVE_CLAIM)
                    && !pvt.getStateBit(PatientVisitModel.BIT_MODIFY_CLAIM)
                    && !pvt.getStateBit(PatientVisitModel.BIT_CANCEL)) {
                // 診察未終了レコードをカウント，最初に見つかった未終了レコードの時間から待ち時間を計算
                ++waitingPvtCount;
                if (waitingPvtDate == null) {
                    waitingPvtDate = ModelUtils.getDateTimeAsObject(pvt.getPvtDate());
                }
            }
            if (!pvt.getStateBit(PatientVisitModel.BIT_CANCEL)) {
                ++totalPvtCount;
            }
        }
    }

    //- 最終行を表示する
    //- javaFX Threadへ変更
    private void showLastRow() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int lastRow = pvtTableModel.getObjectCount() - 1;
                pvtTable.scrollRectToVisible(pvtTable.getCellRect(lastRow, 0, true));
            }
        });
    }

    //- javaFX Threadへ変更
    private class UpdatePvtInfoTask implements Runnable {

        private final boolean fullPvt;

        public UpdatePvtInfoTask() {
            fullPvt = Project.getBoolean("pvt.timer.fullpvt", false);
        }

        @Override
        public void run() {
            if (fullPvt) {
                view.getKutuBtn().doClick();
            } else {
                // 同期時は時刻と患者数を更新するのみ
                countPvt();
                updatePvtInfo();
            }
        }
    }

    private void getFullPvt() {
        //- pvtを全取得する
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setBusy(true);
                try {
                    List<PatientVisitModel> ret = pvtDelegater.getPvtList();
                    if (ret != null && ret.size() > 0) {
                        pvtList = ret;
                    }
                    //- フィルタリング
                    filterPatients();
                    //- 最終行までスクロール
                    showLastRow();
                    updatePvtInfo();
                    countPvt();
                } catch (Exception e) {
                }
                setBusy(false);
            }
        });
    }

    // 受付番号を振り、フィルタリングしてtableModelに設定する
    private void filterPatients() {

        final List<PatientVisitModel> list = new ArrayList<>();

        if (isAssignedOnly() && pvtList != null) {
            for (PatientVisitModel pvt : pvtList) {
                String doctorId = pvt.getDoctorId();
                if (doctorId == null || doctorId.equals(orcaId) || doctorId.equals(UN_ASSIGNED_ID)) {
                    list.add(pvt);
                }
            }
        } else if (pvtList != null) {
            list.addAll(pvtList);
        }

        for (int i = 0; i < list.size(); ++i) {
            PatientVisitModel pvt = list.get(i);
            pvt.setNumber(i + 1);
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                pvtTableModel.setDataProvider(list);
            }
        });
    }

    private boolean isAssignedOnly() {
        return Project.getBoolean(ASSIGNED_ONLY, false);
    }

    //minagawa^ propertyhangeに変更   
    @Override
    public void propertyChange(PropertyChangeEvent pce) {

        if (!pce.getPropertyName().equals(ChartEventHandler.CHART_EVENT_PROP)) {
            return;
        }

        ChartEventModel evt = (ChartEventModel) pce.getNewValue();
        int eventType = evt.getEventType();
        List<PatientVisitModel> tableDataList = pvtTableModel.getDataProvider();

        switch (eventType) {
            case ChartEventModel.PVT_ADD:
                PatientVisitModel model = evt.getPatientVisitModel();
                if (model == null) {
                    break;
                }
                pvtList.add(model);
                filterPatients();
                // 選択中の行を保存
                // 保存した選択中の行を選択状態にする
                int sRow = selectedRow;
                pvtTable.getSelectionModel().addSelectionInterval(sRow, sRow);
                // 追加した行は見えるようにスクロールする
                showLastRow();

                // ORCAクラウド接続
                String receptKind = Project.getString(Project.CLAIM_SENDER);
                if (receptKind != null && !receptKind.equals("client")) {
                    PVTReceptionLink link = new PVTReceptionLink();
                    if (Project.getBoolean("reception.csvlink", false)) {
                        link.receptionCSVLink(model);
                    }
                    if (Project.getBoolean("reception.csvlink2", false)) {
                        link.receptionCSVLink2(model);
                    }
                    if (Project.getBoolean("reception.csvlink3", false)) {
                        link.receptionCSVLink3(model);
                    }
                    if (Project.getBoolean("reception.xmllink", false)) {
                        link.receptionXMLLink(model);
                    }
                    if (Project.getBoolean("reception.link", false)) {
                        link.receptionLink(model);
                    }
                }

                break;

            case ChartEventModel.PVT_STATE:
                // pvtListを更新
                for (PatientVisitModel pvt : pvtList) {
                    if (pvt.getId() == evt.getPvtPk()) {
                        // 更新する
                        pvt.setState(evt.getState());
                        pvt.setByomeiCount(evt.getByomeiCount());
                        pvt.setByomeiCountToday(evt.getByomeiCountToday());
                        pvt.setMemo(evt.getMemo());
                    }
                    if (pvt.getPatientModel().getId() == evt.getPtPk()) {
                        String ownerUUID = evt.getOwnerUUID();
                        pvt.setStateBit(PatientVisitModel.BIT_OPEN, ownerUUID != null);
                        pvt.getPatientModel().setOwnerUUID(evt.getOwnerUUID());
                    }
                }
                for (int row = 0; row < tableDataList.size(); ++row) {
                    PatientVisitModel pvt = tableDataList.get(row);
                    if (pvt.getId() == evt.getPvtPk()
                            || pvt.getPatientModel().getId() == evt.getPtPk()) {
                        pvtTableModel.fireTableRowsUpdated(row, row);
                    }
                }
                break;
            case ChartEventModel.PVT_DELETE:
                // pvtListから削除
                PatientVisitModel toRemove = null;
                for (PatientVisitModel pvt : pvtList) {
                    if (evt.getPvtPk() == pvt.getId()) {
                        toRemove = pvt;
                        break;
                    }
                }
                if (toRemove != null) {
                    pvtList.remove(toRemove);
                }

                // 該当するpvtを削除し受付番号を振りなおす
                int counter = 0;
                toRemove = null;
                for (PatientVisitModel pm : tableDataList) {
                    if (pm.getId() == evt.getPvtPk()) {
                        toRemove = pm;
                    } else {
                        pm.setNumber(++counter);
                    }
                }
                if (toRemove != null) {
                    pvtTableModel.delete(toRemove);
                }
                break;

            case ChartEventModel.PVT_RENEW:
                // 日付が変わるとCMD_RENEWが送信される。pvtListをサーバーから取得する
                getFullPvt();
                break;

            case ChartEventModel.PVT_MERGE:
                // 同じ時刻のPVTで、PVTには追加されず、患者情報や保険情報の更新のみの場合
                // pvtListに変更
                PatientVisitModel toMerge = evt.getPatientVisitModel();
                for (int i = 0; i < pvtList.size(); ++i) {
                    PatientVisitModel pvt = pvtList.get(i);
                    if (pvt.getId() == evt.getPvtPk()) {
                        // 受付番号を継承
                        int num = pvt.getNumber();
                        toMerge.setNumber(num);
                        pvtList.set(i, toMerge);
                    }
                }
                // tableModelに変更
                for (int row = 0; row < tableDataList.size(); ++row) {
                    PatientVisitModel pvt = tableDataList.get(row);
                    if (pvt.getId() == evt.getPvtPk()) {
                        // 選択中の行を保存
                        sRow = selectedRow;
                        pvtTableModel.setObject(row, toMerge);
                        // 保存した選択中の行を選択状態にする
                        pvtTable.getSelectionModel().addSelectionInterval(sRow, sRow);
                        break;
                    }
                }
                break;

            case ChartEventModel.PM_MERGE:
                // 患者モデルに変更があった場合
                // pvtListに変更
                PatientModel pm = evt.getPatientModel();
                long pk = pm.getId();
                for (PatientVisitModel pvt : pvtList) {
                    if (pvt.getPatientModel().getId() == pk) {
                        pvt.setPatientModel(pm);
                    }
                }
                break;
        }

        // PvtInfoを更新する
        updatePvtInfo();
        countPvt();
    }

    /**
     * KarteStateRenderer カルテ（チャート）の状態をレンダリングするクラス。
     */
    private class KarteStateRenderer extends StripeTableCellRenderer {

        /**
         * Creates new IconRenderer
         */
        public KarteStateRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {

            super.getTableCellRendererComponent(table, value, isSelected, isFocused, row, col);

            PatientVisitModel pvt = (PatientVisitModel) sorter.getObject(row);
            Color fore = (pvt != null && pvt.getStateBit(PatientVisitModel.BIT_CANCEL))
                    ? CANCEL_PVT_COLOR
                    : table.getForeground();
            this.setForeground(fore);

            // 選択状態の場合はStripeTableCellRendererの配色を上書きしない
            if (pvt != null && !isSelected) {
                if (isSexRenderer()) {
                    switch (pvt.getPatientModel().getGender()) {
                        case IInfoModel.MALE:
                            this.setBackground(GUIConst.TABLE_MALE_COLOR);
                            break;
                        case IInfoModel.FEMALE:
                            this.setBackground(GUIConst.TABLE_FEMALE_COLOR);
                            break;
                    }
                }
            }

            boolean bStateColumn = (pvtTable.getColumnModel().getColumn(col).getIdentifier() != null
                    && pvtTable.getColumnModel().getColumn(col).getIdentifier().equals(COLUMN_IDENTIFIER_STATE));

            if (value != null && bStateColumn) {

                ImageIcon icon = null;

                // 最初に chart bit をテストする
                for (int i = 0; i < chartBitArray.length; i++) {
                    if (pvt.getStateBit(chartBitArray[i])) {
                        if (i == PatientVisitModel.BIT_OPEN
                                && !clientUUID.equals(pvt.getPatientModel().getOwnerUUID())) {
                            icon = NETWORK_ICON;
                        } else {
                            icon = chartIconArray[i];
                        }
                        break;
                    }
                }

                // user bit をテストする
                if (icon == null) {

                    // bit 0 はパス
                    for (int i = 1; i < userBitArray.length; i++) {
                        if (pvt.getStateBit(userBitArray[i])) {
                            icon = userIconArray[i];
                            break;
                        }
                    }
                }

                this.setIcon(icon);
                this.setText("");

            } else {
                setIcon(null);
                this.setText(value == null ? "" : value.toString());
            }
            return this;
        }
    }

    /**
     * KarteStateRenderer カルテ（チャート）の状態をレンダリングするクラス。
     */
    private class MaleFemaleRenderer extends StripeTableCellRenderer {

        public MaleFemaleRenderer() {
            super();
        }

        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value,
                boolean isSelected,
                boolean isFocused,
                int row, int col) {

            super.getTableCellRendererComponent(table, value, isSelected, isFocused, row, col);

            PatientVisitModel pvt = (PatientVisitModel) sorter.getObject(row);

            if (pvt != null) {
                if (pvt.getStateBit(PatientVisitModel.BIT_CANCEL)) {
                    this.setForeground(CANCEL_PVT_COLOR);
                } else {
                    // 選択状態の場合はStripeTableCellRendererの配色を上書きしない
                    if (isSexRenderer() && !isSelected) {
                        switch (pvt.getPatientModel().getGender()) {
                            case IInfoModel.MALE:
                                this.setBackground(GUIConst.TABLE_MALE_COLOR);
                                break;
                            case IInfoModel.FEMALE:
                                this.setBackground(GUIConst.TABLE_FEMALE_COLOR);
                                break;
                        }
                    }
                }
            }

            return this;
        }
    }

    private class CenterRenderer extends MaleFemaleRenderer {

        public CenterRenderer() {
            super();
            this.setHorizontalAlignment(JLabel.CENTER);
        }
    }

    /**
     * Iconを表示するJComboBox Renderer.
     */
    private class ComboBoxRenderer extends JLabel implements ListCellRenderer {

        public ComboBoxRenderer() {
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
        }

        @Override
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {

            BitAndIconPair pair = (BitAndIconPair) value;

            setIcon(pair.getIcon());
            return this;
        }
    }
}
