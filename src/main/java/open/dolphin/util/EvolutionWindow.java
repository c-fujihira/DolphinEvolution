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
package open.dolphin.util;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import open.dolphin.client.AboutDolphin;
import open.dolphin.client.AddUser;
import open.dolphin.client.BlockGlass;
import open.dolphin.client.ChangeProfile;
import open.dolphin.client.Chart;
import open.dolphin.client.ChartEventHandler;
import open.dolphin.client.ChartImpl;
import open.dolphin.client.ClaimMessageListener;
import open.dolphin.client.ClientContext;
import open.dolphin.client.EditorFrame;
import open.dolphin.client.Evolution;
import open.dolphin.client.GUIConst;
import open.dolphin.client.GUIFactory;
import open.dolphin.client.ImageBox;
import open.dolphin.client.AuditController;
import open.dolphin.client.KarteEditor;
import open.dolphin.client.MainComponent;
import open.dolphin.client.MainService;
import open.dolphin.client.MainView;
import open.dolphin.client.MainWindow;
import open.dolphin.client.MmlMessageListener;
import open.dolphin.client.NewKarte;
import open.dolphin.delegater.OrcaDelegater;
import open.dolphin.delegater.OrcaDelegaterFactory;
import open.dolphin.delegater.StampDelegater;
import open.dolphin.helper.MenuSupport;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.helper.WindowSupport;
import open.dolphin.impl.schedule.PatientScheduleImpl;
import open.dolphin.infomodel.FacilityModel;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.infomodel.StampTreeModel;
import open.dolphin.plugin.PluginLoader;
import open.dolphin.project.Project;
import open.dolphin.project.ProjectSettingDialog;
import open.dolphin.relay.PVTRelayProxy;
import open.dolphin.server.PVTServer;
import open.dolphin.stampbox.StampBoxPlugin;
import open.dolphin.utilities.control.MyJTabbedPane;

/**
 * Evolution MainWindow Class
 *
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class EvolutionWindow implements MainWindow {

    private Stage stage;
    private Scene scene;

    //- MainFrame
    private JFrame frame;
    private final JFXPanel jfxPanel1;
    private final JFXPanel jfxPanel2;

    //- ViewController
    private final JPanel panel0;
    //- ListView
    private final JPanel panel1;
    //- KarteView
    private final JPanel panel2;
    //- CtrlView
    private final JPanel panel3;
    //- コントロールパネル用
    private final JTabbedPane tabPane;
    //- スタンプボックス
    private final JScrollPane scrollPane1;
    //- シェーマボックス
    private final JScrollPane scrollPane2;

    //- |-----------------------------------| 
    //- | View Controller (Panel0)          |
    //- |-----------------------------------| SplitPanel1
    //- | KarteView         |  ListView     | SplitPanel2
    //- | (Panel2)          |  (Panel1)     | 
    //- |                   |---------------| SplitPanel3
    //- |                   | CtrlView      | 
    //- |                   | (Panel3)      | 
    //- |                   |               |
    //- |-----------------------------------|
    private JSplitPane splitPane1;
    private JSplitPane splitPane2;
    private JSplitPane splitPane3;

    public Evolution application;

    //- 通知ウィンドウ
    public MainView mainView;
    private WindowSupport windowSupport;
    private BlockGlass blockGlass;

    // 受付受信サーバ
    private PVTServer pvtServer;

    // CLAIM リスナ
    private ClaimMessageListener sendClaim;

    // MML リスナ
    private MmlMessageListener sendMml;

    //- 状態変化リスナー
    private ChartEventHandler scl;

    //- 状態制御
    private StateManager stateMgr;

    //- Mediator
    private Mediator mediator;
    private HashMap<String, MainService> providers;

    //- 排他処理用UUID
    private String clientUUID;

    //- StampTreeList
    public List<IStampTreeModel> treeList;
    //- StampBox
    private StampBoxPlugin stampBox;

    //- カルテタブ
    private final MyJTabbedPane baseTabPane = new MyJTabbedPane();

    //- 最初の受付リスト患者カルテ表示判定用
    private boolean firstFlag = true;

    //- カルテタブ表示分の患者ID保持用
    private final List<String> patientIdList = new ArrayList<>();

    //- カルテタブのロック解除用情報保持用
    private final Map<String, PatientVisitModel> patientIdMap = new HashMap<>();
    //- カルテタブ閉じる処理時の情報保持用
    private final Map<String, ChartImpl> chartImplMap = new HashMap<>();

    //- プリンターセットアップ
    private PageFormat pageFormat;

    //- timerTask 関連
    private javax.swing.Timer taskTimer;
    private ProgressMonitor monitor;
    private int delayCount;
    private final int maxEstimation = 120 * 1000; // 120 秒
    private final int delay = 300; // 300 mmsec

    private CardLayout subWindowLayout;

    public EvolutionWindow() {
        //- プラグインのプロバイダマップを生成する
        providers = new HashMap<>();

        //- ベースウィンドウ
        frame = new JFrame(ClientContext.getProductName() + " " + ClientContext.getVersion());
        frame.setState(JFrame.NORMAL);

        panel0 = new JPanel();
        panel1 = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();

        tabPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

        scrollPane1 = new JScrollPane();
        scrollPane2 = new JScrollPane();
        jfxPanel1 = new JFXPanel();
        jfxPanel2 = new JFXPanel();

        subWindowLayout = new CardLayout();
    }

    public void loadResource(final List<IStampTreeModel> result) {

        ClientContext.getBootLogger().info("main window load resources.");

        //- StaeMagrを使用してメインウインドウの状態を制御する
        stateMgr = new StateManager();
        stateMgr.processLogin(true);

        //- プロバイダマップを生成
        startServices();

        //- ログインユーザーの StampTree を読み込む
        stampBox = new StampBoxPlugin();
        stampBox.setEvoWindow(EvolutionWindow.this);
        stampBox.setStampTreeModels(result);
        stampBox.start();
        providers.put("stampBox", stampBox);

        //- シェーマボックスを読み込む
        ImageBox imgBox = new ImageBox();
        imgBox.start();

        getScrollPane1().setViewportView(stampBox.getContentPane());
        //- スタンプボックススクロール量変更
        getScrollPane1().getVerticalScrollBar().setUnitIncrement(40);
        getScrollPane1().getVerticalScrollBar().setBlockIncrement(40);

        getScrollPane2().setViewportView(imgBox.getContentPane());
        //- シェーマボックススクロール量変更
        getScrollPane2().getVerticalScrollBar().setUnitIncrement(40);
        getScrollPane2().getVerticalScrollBar().setBlockIncrement(40);

        getTabPanel().addTab("コントロールパネル", makeJfxPanel(getJfxPanel1()));
        getTabPanel().addTab("スタンプボックス", getScrollPane1());
        getTabPanel().addTab("シェーマボックス", getScrollPane2());

    }

    public void initComponents() {

        //- Closing処理はリスナーで行う
        getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getFrame().setMinimumSize(new Dimension(1200, 700));

        //- 最大化
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle rect = env.getMaximumWindowBounds();
        getFrame().setBounds(rect);
        getFrame().setSize(rect.width, rect.height);

        // Panel3 レイアウト
        getPanel3().setLayout(new BoxLayout(getPanel3(), BoxLayout.Y_AXIS));
        getPanel0().setLayout(new GridLayout(1, 1));
        getJfxPanel2().setLayout(new GridLayout(1, 1));
        getPanel0().add(getJfxPanel2());
        getPanel3().add(tabPane);

        //- 仕切り線サイズ, 仕切り位置, リサイズ時倍率 (ListView/ControlPanel)
        splitPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, getPanel1(), getPanel3());
        AncestorListener ancListener = new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent ancestorEvent) {
            }

            @Override
            public void ancestorMoved(AncestorEvent ancestorEvent) {
                //- debug
                if (Boolean.valueOf(Project.getString("karte.split.reserve"))) {
                    Project.setInt("karte.split.reserve.position", application.evoWindow.getSplitPane3().getDividerLocation());
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent ancestorEvent) {
            }
        };
        panel2.addAncestorListener(ancListener);
        splitPane2.addAncestorListener(ancListener);
        //- 仕切り線サイズ, 仕切り位置, リサイズ時倍率 (KarteView/ListView/ControlPanel)
        splitPane3 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, getPanel2(), getSplitPane2());

        //- 仕切り線サイズ, 仕切り位置, リサイズ時倍率 (CtrlViewPanel/KarteView/ListView/ControlPanel)
        splitPane1 = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, getPanel0(), getSplitPane3());

        getSplitPane1().setOneTouchExpandable(false);
        getSplitPane2().setOneTouchExpandable(false);
        getSplitPane3().setOneTouchExpandable(false);

        getFrame().getContentPane().add(getSplitPane1());
        getFrame().setLocationRelativeTo(null);

        //- Mediator を設定する
        mediator = new Mediator(this);

        //- BlockGlass を設定する
        blockGlass = new BlockGlass();
        getFrame().setGlassPane(blockGlass);

        //- 通知ウィンドウの生成
        StringBuilder sb = new StringBuilder();
        sb.append("ログイン ");
        sb.append(Project.getUserModel().getCommonName());
        sb.append("  ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d(EEE) HH:mm");
        sb.append(sdf.format(new Date()));
        String loginInfo = sb.toString();
        mainView = new MainView();
        mainView.getDateLbl().setText(loginInfo);
        mainView.setOpaque(true);

        //- mainViewの初期サイズ
        mainView.getMainPanel().setPreferredSize(new Dimension(getPanel1().getSize().width, getPanel1().getSize().height));
        getPanel1().add(mainView);

        //- Set SplitPane
        splitPane1.setDividerSize(0);
        splitPane1.setDividerLocation(40);
        splitPane2.setDividerSize(5);
        splitPane2.setDividerLocation(305);
        splitPane3.setDividerSize(5);
        splitPane3.setDividerLocation(getFrame().getWidth() - 481);
        splitPane3.setResizeWeight(1.0);

        // タブペインに格納する Plugin をロードする
        List<MainComponent> list = new ArrayList<>(3);
        PluginLoader<MainComponent> loader = PluginLoader.load(MainComponent.class);
        Iterator<MainComponent> iter = loader.iterator();

        // mainWindow のタブに、受付リスト、患者検索 ... の純に格納する
        while (iter.hasNext()) {
            MainComponent plugin = iter.next();
            if (plugin instanceof PatientScheduleImpl) {
                if (!Project.getBoolean(Project.USE_SCHEDULE_KARTE)) {
                    plugin.stop();
                    continue;
                }
            }
            list.add(plugin);
        }
        ClientContext.getBootLogger().info("main window plugin did load");

        // プラグインプロバイダに格納する
        // index=0 のプラグイン（受付リスト）は起動する
        int index = 0;
        for (MainComponent plugin : list) {
            if (index == 0) {
                plugin.setContext(this);
                plugin.start();
                mainView.getTabbedPane().addTab(plugin.getName(), plugin.getUI());
                providers.put(String.valueOf(index), plugin);
                mediator.addChain(plugin);
            } else {
                mainView.getTabbedPane().addTab(plugin.getName(), plugin.getUI());
                providers.put(String.valueOf(index), plugin);
            }
            index++;
        }
        list.clear();

        // タブの切り替えで plugin.enter() をコールする
        mainView.getTabbedPane().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                mainView.getStatusLbl().setText("");
                int index = mainView.getTabbedPane().getSelectedIndex();
                MainComponent plugin = (MainComponent) providers.get(String.valueOf(index));
                if (plugin.getContext() == null) {
                    plugin.setContext(EvolutionWindow.this);
                    plugin.start();
                    mainView.getTabbedPane().setComponentAt(index, plugin.getUI());
                } else {
                    plugin.enter();
                }
                mediator.addChain(plugin);
            }
        });

        //- WindowListener登録
        getFrame().addWindowListener(createWindowListener());
        getFrame().addComponentListener(createComponentListener());
        splitPane1.addPropertyChangeListener(createPropatyChangeListener());
        splitPane2.addPropertyChangeListener(createPropatyChangeListener());
        splitPane3.addPropertyChangeListener(createPropatyChangeListener());

        macListener();
    }

    public void windowMaximum() {
        //- 最大化
        this.frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void setWinCtrlMacro() {
        MainComponent plugin = (MainComponent) providers.get(String.valueOf(0));
        plugin.setCtrlView(application.dispWinCtrl);
    }

    public void macListener() {

        //- Mac 対応
        if (ClientContext.isMac()) {
            com.apple.eawt.Application fApplication = com.apple.eawt.Application.getApplication();

            //- About
            fApplication.setAboutHandler(new com.apple.eawt.AboutHandler() {
                @Override
                public void handleAbout(com.apple.eawt.AppEvent.AboutEvent ae) {
                    System.out.println("about");
                    showAbout();
                }
            });

            //- Preference
            fApplication.setPreferencesHandler(new com.apple.eawt.PreferencesHandler() {
                @Override
                public void handlePreferences(com.apple.eawt.AppEvent.PreferencesEvent pe) {
                    System.out.println("preference");
                    doPreference();
                }
            });

            //- Quit
            fApplication.setQuitHandler(new com.apple.eawt.QuitHandler() {
                @Override
                public void handleQuitRequestWith(com.apple.eawt.AppEvent.QuitEvent qe, com.apple.eawt.QuitResponse qr) {
                    qr.cancelQuit();
                    processExit(1);
                    System.out.println("from mac evowin exit.");
                }
            });
        }
    }

    private WindowListener createWindowListener() {
        WindowListener listener = (new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                processExit(0);
            }
        });
        return listener;
    }

    private ComponentListener createComponentListener() {
        ComponentListener listener = (new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
//                System.out.println("Sp1 ->" + getSplitPane1().getDividerLocation());
//                System.out.println("Sp2 ->" + getSplitPane2().getDividerLocation());
//                System.out.println("Sp3 ->" + getSplitPane3().getDividerLocation());
//                System.out.println("frame W->" + getFrame().getWidth());
//                System.out.println("frame H->" + getFrame().getHeight());
//                System.out.println("size1 w -> " + getPanel1().getSize().width);
//                System.out.println("size1 h -> " + getPanel1().getSize().height);
//                System.out.println("size2 w -> " + getPanel2().getSize().width);
//                System.out.println("size2 h -> " + getPanel2().getSize().height);
//                System.out.println("size3 w -> " + getPanel3().getSize().width);
//                System.out.println("size3 h -> " + getPanel3().getSize().height);
                mainView.getMainPanel().setPreferredSize(
                        new Dimension(getPanel1().getSize().width, getPanel1().getSize().height));
            }
        });
        return listener;
    }

    public PropertyChangeListener createPropatyChangeListener() {
        PropertyChangeListener listener = (new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent changeEvent) {
//                System.out.println("Sp1 ->" + getSplitPane1().getDividerLocation());
//                System.out.println("Sp2 ->" + getSplitPane2().getDividerLocation());
//                System.out.println("Sp3 ->" + getSplitPane3().getDividerLocation());
//                System.out.println("frame W->" + getFrame().getWidth());
//                System.out.println("frame H->" + getFrame().getHeight());
//                System.out.println("size1 w -> " + getPanel1().getSize().width);
//                System.out.println("size1 h -> " + getPanel1().getSize().height);
//                System.out.println("size2 w -> " + getPanel2().getSize().width);
//                System.out.println("size2 h -> " + getPanel2().getSize().height);
//                System.out.println("size3 w -> " + getPanel3().getSize().width);
//                System.out.println("size3 h -> " + getPanel3().getSize().height);
                mainView.getMainPanel().setPreferredSize(
                        new Dimension(getPanel1().getSize().width, getPanel1().getSize().height));
            }
        });
        return listener;
    }

    public CardLayout getSubWindowLayout() {
        return subWindowLayout;
    }

    public void setSubWindowLayout(CardLayout ci) {
        subWindowLayout = ci;
    }

    public JTabbedPane getTabPanel() {
        return tabPane;
    }

    public JScrollPane getScrollPane1() {
        return scrollPane1;
    }

    public JScrollPane getScrollPane2() {
        return scrollPane2;
    }

    public JSplitPane getSplitPane1() {
        return splitPane1;
    }

    public JSplitPane getSplitPane2() {
        return splitPane2;
    }

    public JSplitPane getSplitPane3() {
        return splitPane3;
    }

    public JFXPanel getJfxPanel1() {
        return jfxPanel1;
    }

    public JFXPanel getJfxPanel2() {
        return jfxPanel2;
    }

    public JPanel getPanel0() {
        return panel0;
    }

    public JPanel getPanel1() {
        return panel1;
    }

    public JPanel getPanel2() {
        return panel2;
    }

    public JPanel getPanel3() {
        return panel3;
    }

    public JFrame getFrame() {
        return frame;
    }

    public void setFrame(JFrame frame) {
        this.frame = frame;
    }

    public void setApp(Evolution app) {
        application = app;
    }

    public void addTabPane(MyJTabbedPane tabPane) {
        panel2.add(tabPane.getTabbedPane());
    }

    public void reFleshJFrame0() {
        //- リフレッシュ不具合対応
        panel0.repaint();
        panel0.revalidate();
    }

    public void reFleshJFrame1() {
        //- リフレッシュ不具合対応
        panel1.repaint();
        panel1.revalidate();
    }

    public void reFleshJFrame2() {
        //- リフレッシュ不具合対応
        panel2.repaint();
        panel2.revalidate();
    }

    public void reFleshJFrame3() {
        //- リフレッシュ不具合対応
        panel3.repaint();
        panel3.revalidate();
    }

    public Evolution getInstance() {
        return application;
    }

    @Override
    public HashMap<String, MainService> getProviders() {
        return providers;
    }

    @Override
    public void setProviders(HashMap<String, MainService> providers) {
        this.providers = providers;
    }

    @Override
    public JMenuBar getMenuBar() {
        return windowSupport.getMenuBar();
    }

    @Override
    public MenuSupport getMenuSupport() {
        return mediator;
    }

    @Override
    public void registerActions(ActionMap actions) {
        mediator.registerActions(actions);
    }

    @Override
    public Action getAction(String name) {
        return mediator.getAction(name);
    }

    @Override
    public void enabledAction(String name, boolean b) {
        mediator.enabledAction(name, b);
    }

    @Override
    public void openKarte(PatientVisitModel pvt) {

        if (Boolean.valueOf(Project.getString("karte.view"))) {
            application.dispWinCtrl.setWinCtrlKarteSet();
        }

        if (pvt == null) {
            return;
        }
        if (pvt.getStateBit(PatientVisitModel.BIT_CANCEL)) {
            return;
        }

        boolean opened = false;

        // まだ開いていない場合
        boolean readOnly = Project.isReadOnly();

        long ptId = pvt.getPatientModel().getId();
        //- ChartEventModelから対象を検索
        for (PatientVisitModel model : patientIdMap.values()) {
            if (ptId == model.getPatientModel().getId()) {
                opened = true;
                //- カルテ再指定
                baseTabPane.selectTab(ptId);
                break;
            }
        }

        for (ChartImpl chart : ChartImpl.getAllChart()) {
            if (chart.getPatient().getId() == ptId) {
                chart.getFrame().setExtendedState(java.awt.Frame.NORMAL);
                chart.getFrame().toFront();
                opened = true;
                break;
            }
        }

        for (Chart ef : EditorFrame.getAllEditorFrames()) {
            if (ef.getPatient().getId() == ptId) {
                ef.getFrame().setExtendedState(java.awt.Frame.NORMAL);
                ef.getFrame().toFront();
                break;
            }
        }

        if (opened) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        if (!readOnly) {
            if (pvt.getPatientModel().getOwnerUUID() != null && !patientIdList.contains(pvt.getPatientId())) {
                // ダイアログで確認する
                String ptName = pvt.getPatientName();
                String[] options = {"閲覧のみ", "ロック解除", GUIFactory.getCancelButtonText()};
                String msg = ptName + " 様のカルテは他の端末で編集中です。\n"
                        + "ロック解除は編集中の端末がクラッシュした場合等に使用してください。";

                int val = JOptionPane.showOptionDialog(
                        getFrame(), msg, ClientContext.getFrameTitle("カルテオープン"),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                switch (val) {
                    case 0:     // 閲覧のみは編集不可で所有権を設定しない
                        readOnly = true;
                        break;

                    case 1:     // 強制的に編集するときは所有権横取り ロック解除
                        pvt.getPatientModel().setOwnerUUID(Evolution.getInstance().getClientUUID());
                        break;

                    case 2:     // キャンセル
                    case JOptionPane.CLOSED_OPTION:
                        return;
                }
            } else {
                // 誰も開いていないときは自分が所有者
                pvt.getPatientModel().setOwnerUUID(Evolution.getInstance().getClientUUID());
            }
        }
        PluginLoader<Chart> loader = PluginLoader.load(Chart.class);
        Iterator<Chart> iter = loader.iterator();

        Chart chart = null;
        if (iter.hasNext()) {
            chart = iter.next();
        }

        if (chart != null) {
            chart.setContext(this);
            chart.setPatientVisit(pvt);
            chart.setReadOnly(readOnly);
            chart.setApp(application);
            chart.setBaseTabPane(baseTabPane);
            chart.setFirstFlag(firstFlag);
            chart.setPatientIdList(patientIdList);
            chart.setPatientIdMap(patientIdMap);
            chart.setChartImplMap(chartImplMap);
            chart.setChartEventHandler(scl);
            chart.start();
        }
        reFleshJFrame2();

        // publish state
        scl.publishKarteOpened(pvt);

        firstFlag = false;
    }

    @Override
    public void addNewPatient() {
        PluginLoader<NewKarte> loader = PluginLoader.load(NewKarte.class
        );
        Iterator<NewKarte> iter = loader.iterator();

        if (iter.hasNext()) {
            NewKarte newKarte = iter.next();
            newKarte.setContext(this);
            newKarte.start();
        }
    }

    @Override
    public void block() {
        blockGlass.block();
    }

    @Override
    public void unblock() {
        blockGlass.unblock();
    }

    @Override
    public BlockGlass getGlassPane() {
        return blockGlass;
    }

    @Override
    public MainService getPlugin(String name) {
        return providers.get(name);
    }

    @Override
    public void showStampBox() {
        //- Do Nothing
    }

    /**
     * シェーマボックスを表示する。
     */
    @Override
    public void showSchemaBox() {
        ImageBox imageBox = new ImageBox();
        imageBox.setContext(this);
        imageBox.show();
    }

    @Override
    public JLabel getStatusLabel() {
        return mainView.getStatusLbl();
    }

    @Override
    public JProgressBar getProgressBar() {
        return mainView.getProgressBar();
    }

    @Override
    public JLabel getDateLabel() {
        return mainView.getDateLbl();
    }

    @Override
    public JTabbedPane getTabbedPane() {
        return mainView.getTabbedPane();
    }

    @Override
    public Component getCurrentComponent() {
        return getTabbedPane().getSelectedComponent();

    }

    @Override
    public PageFormat getPageFormat() {
        if (pageFormat == null) {
            PrinterJob printJob = PrinterJob.getPrinterJob();
            if (printJob != null) {
                pageFormat = printJob.defaultPage();
            }
        }
        return pageFormat;
    }

    private void startSendMml() {
        PluginLoader<MmlMessageListener> loader = PluginLoader.load(MmlMessageListener.class
        );
        Iterator<MmlMessageListener> iter = loader.iterator();

        if (iter.hasNext()) {
            sendMml = iter.next();
            sendMml.setContext(this);
            // 出力先ディレクトリ
            sendMml.setCSGWPath(Project.getString(Project.SEND_MML_DIRECTORY));
            sendMml.start();
            providers.put("sendMml", sendMml);
            ClientContext.getBootLogger().info("sendMml did  start");
        }
    }

    private void startHttpServer() {
        String test = Project.getString(Project.CLAIM_BIND_ADDRESS);
        if (test == null || test.equals("")) {
            return;
        }
        int port = Project.getInt("visit.http.port");
        String ctx = Project.getString("visit.http.context");
        try {
            InetAddress addr = InetAddress.getByName(test);
            InetSocketAddress socket = new InetSocketAddress(addr, port);
            HttpServer server = HttpServer.create(socket, 0);
            server.createContext(ctx, new HttpDolphinHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
            ClientContext.getBootLogger().info("HTTP server is binded at " + socket.getHostName());
        } catch (IOException e) {
            e.printStackTrace(System.err);

        }
    }

    /**
     * PVTServer を開始する。
     */
    private void startPvtServer() {
        PluginLoader<PVTServer> loader = PluginLoader.load(PVTServer.class
        );
        Iterator<PVTServer> iter = loader.iterator();

        if (iter.hasNext()) {
            pvtServer = iter.next();
            pvtServer.setContext(this);
            pvtServer.setBindAddress(Project.getString(Project.CLAIM_BIND_ADDRESS));
            pvtServer.start();
            providers.put("pvtServer", pvtServer);
            ClientContext.getBootLogger().info("pvtServer did  start");
        }
    }

    /**
     * CLAIM 送信を開始する。
     */
    private
            void startSendClaim() {
        PluginLoader<ClaimMessageListener> loader = PluginLoader.load(ClaimMessageListener.class
        );
        Iterator<ClaimMessageListener> iter = loader.iterator();

        if (iter.hasNext()) {
            sendClaim = iter.next();
            sendClaim.setContext(this);
            sendClaim.start();
            providers.put("sendClaim", sendClaim);
            ClientContext.getBootLogger().info("sendClaim did  start");
        }
    }

    /**
     * 起動時のバックグラウンドで実行されるべきタスクを行う。
     */
    public void startServices() {

        //- ChartStateListenerを開始する
        scl = ChartEventHandler.getInstance();
        scl.start();

        //- プラグインのプロバイダマップを生成する
        setProviders(new HashMap<String, MainService>());

        //- MML送信を生成する
        if (Project.getBoolean(Project.SEND_MML) && Project.getString(Project.SEND_MML_DIRECTORY) != null) {
            startSendMml();
            Project.setBoolean(GUIConst.SEND_MML_IS_RUNNING, true);
        } else {
            Project.setBoolean(GUIConst.SEND_MML_IS_RUNNING, false);
        }

        //- 受付リレー
        if (Project.getBoolean(Project.PVT_RELAY) && Project.getString(Project.PVT_RELAY_DIRECTORY) != null) {
            PVTRelayProxy pvtRelay = new PVTRelayProxy();
            scl.addPropertyChangeListener(pvtRelay);
            Project.setBoolean(GUIConst.PVT_RELAY_IS_RUNNING, true);
        } else {
            Project.setBoolean(GUIConst.PVT_RELAY_IS_RUNNING, false);
        }

        //- HTTP Server
        if (Project.getBoolean("visit.use")) {
            startHttpServer();
        }
    }

    public String getClientUUID() {
        return clientUUID;
    }

    public void setClientUUID(String clientUUID) {
        this.clientUUID = clientUUID;
    }

    public MyJTabbedPane getBaseTabbedPane() {
        return baseTabPane;
    }

    protected class Mediator extends MenuSupport {

        public Mediator(Object owner) {
            super(owner);
        }

        // global property の制御
        @Override
        public void menuSelected(MenuEvent e) {
        }

        @Override
        public void registerActions(ActionMap actions) {
            super.registerActions(actions);
        }
    }

    /**
     * MainWindowState
     */
    abstract class MainWindowState {

        public MainWindowState() {
        }

        public abstract void enter();

        public abstract boolean isLogin();
    }

    /**
     * LoginState
     */
    class LoginState extends MainWindowState {

        public LoginState() {
        }

        @Override
        public boolean isLogin() {
            return true;
        }

        @Override
        public void enter() {
        }
    }

    /**
     * LogoffState
     */
    class LogoffState extends MainWindowState {

        public LogoffState() {
        }

        @Override
        public boolean isLogin() {
            return false;
        }

        @Override
        public void enter() {
        }
    }

    /**
     * StateManager
     */
    class StateManager {

        private final MainWindowState loginState = new LoginState();
        private final MainWindowState logoffState = new LogoffState();
        private MainWindowState currentState = logoffState;

        public StateManager() {
        }

        public boolean isLogin() {
            return currentState.isLogin();
        }

        public void processLogin(boolean b) {
            currentState = b ? loginState : logoffState;
            currentState.enter();
        }
    }

    class HttpDolphinHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange t) throws IOException {
            InputStream is = t.getRequestBody();
            InputStreamReader r = new InputStreamReader(is, "UTF-8");
            String text;
            try (BufferedReader br = new BufferedReader(r)) {
                String line;
                StringBuilder buf = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    buf.append(line);
                }
                text = buf.toString();
                String response = "This is the response";
                t.sendResponseHeaders(200, response.length());
                try (OutputStream os = t.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }

            // Input text Karte
            if (text == null || text.equals("")) {
                return;
            }

            List<KarteEditor> list = KarteEditor.getAllKarte();
            if (!list.isEmpty()) {
                KarteEditor karte = list.get(0);
                karte.addDictation(text);
            }
        }
    }

    public MainWindow getContext() {
        return this;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        return scene;
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    /**
     * プリンターをセットアップする。
     */
    public void printerSetup() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();

                try {
                    PrinterJob pj = PrinterJob.getPrinterJob();
                    pageFormat = pj.pageDialog(aset);
                } catch (HeadlessException e) {
                    e.printStackTrace(System.err);
                }
            }
        });
    }

    /**
     * 環境設定を行う。
     */
    public void doPreference() {
        ProjectSettingDialog sd = new ProjectSettingDialog();
        sd.setLoginState(stateMgr.isLogin());
        sd.setProject("karteSetting");
        sd.setFrame(getFrame());
        sd.start();
    }

    /**
     * ユーザのパスワードを変更する。
     */
    public void changePassword() {

        PluginLoader<ChangeProfile> loader = PluginLoader.load(ChangeProfile.class);
        Iterator<ChangeProfile> iter = loader.iterator();
        if (iter.hasNext()) {
            ChangeProfile cp = iter.next();
            cp.setContext(getContext());
            cp.setFrame(getFrame());
            cp.start();
        }
    }

    /**
     * 施設情報を編集する。管理者メニュー。
     */
    public void editFacilityInfo() {

        PluginLoader<AddUser> loader = PluginLoader.load(AddUser.class);
        Iterator<AddUser> iter = loader.iterator();
        if (iter.hasNext()) {
            AddUser au = iter.next();
            au.setContext(getContext());
            au.setFrame(getFrame());
            au.start();
        }
    }

    /**
     * 保険医療機関コードとJMARIコードを取得する
     */
    public void fetchFacilityCode() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                OrcaDelegater odl = OrcaDelegaterFactory.create();
                try {
                    String line;
                    String insCode;
                    String jmari;
                    line = odl.getFacilityCodeBy1001();
                    insCode = line.substring(0, 10);
                    jmari = line.substring(10);
                    Project.setString(Project.FACILITY_CODE_OF_INSURNCE_SYSTEM, insCode);
                    Project.setString(Project.JMARI_CODE, jmari);
                    showReadFacilityCodeResults();
                } catch (Exception e) {
                    showReadFacilityCodeError(e);
                }
            }
        });
    }

    private void showReadFacilityCodeResults() {

        String[] msg = new String[3];
        msg[0] = "医療機関コードの読み込みに成功しました。";
        msg[1] = "保険医療機関コード: " + Project.getString(Project.FACILITY_CODE_OF_INSURNCE_SYSTEM);
        msg[2] = "JMARIコード: " + Project.getString(Project.JMARI_CODE);

        JOptionPane.showMessageDialog(getFrame(),
                msg, ClientContext.getFrameTitle("医療機関コード読み込み"),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showReadFacilityCodeError(Throwable e) {

        String[] msg = new String[3];
        msg[0] = "医療機関コードの読み込みに失敗しました。";
        msg[1] = e.getMessage();

        JOptionPane.showMessageDialog(getFrame(),
                msg, ClientContext.getFrameTitle("医療機関コード読み込み"),
                JOptionPane.WARNING_MESSAGE);
    }

    private void initAndShowGUI() throws IOException {
        final JFXPanel jfxPanel = new JFXPanel();

        Platform.runLater(new Runnable() {
            Parent root;
            AuditController auditCtr = null;
            
            @Override
            public void run() {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/resources/fxml/Audit.fxml"));
                    root = (Parent)fxmlLoader.load();
                    auditCtr = (AuditController) fxmlLoader.getController();
                } catch (IOException ex) {
                    Logger.getLogger(EvolutionWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
                

                Scene scene = new Scene(root);
                jfxPanel.setScene(scene);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {

                        final JDialog dialog = new JDialog(getFrame(), "カルテ更新履歴一覧出力", true);
                        auditCtr.setDialog(dialog);
                        dialog.getContentPane().add(jfxPanel, BorderLayout.CENTER);
                        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                        dialog.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                dialog.setVisible(false);
                                dialog.dispose();
                            }
                        });

                        dialog.pack();
                        dialog.setResizable(false);
                        Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
                        int x = (size.width) / 3;
                        int y = (size.height) / 5;
                        dialog.setLocation(x, y);
                        dialog.setVisible(true);
                    }
                });
            }
        });
    }

    /**
     * カルテ履歴一覧出力画面を表示する
     */
    public void patientInfoOutput() {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    initAndShowGUI();
                } catch (IOException ex) {
                    ex.getStackTrace();
                }
            }
        });

//        //- ダイアログ表示
//        String fxml = "/resources/fxml/Audit.fxml";
//        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
//        loader.setBuilderFactory(new JavaFXBuilderFactory());
//        loader.setLocation(EvolutionWindow.class.getResource(fxml));
//        try {
//            loader.load();
//
//        } catch (IOException ex) {
//            Logger.getLogger(AuditController.class.getName()).log(Level.SEVERE, "Template Error.", ex);
//        }
//
//        Parent root = loader.getRoot();
//        AuditController ctrl = (AuditController)loader.getController();
//
//        //- サイズ変更不可
//        Stage stage = new Stage();
//        stage.setResizable(false);
//        stage.initModality(Modality.APPLICATION_MODAL);
//        stage.initOwner(getStage());
//        stage.setScene(new Scene(root));
//        stage.setTitle("カルテ更新履歴一覧出力");
//
//        ctrl.setApp(this.application);
//        ctrl.setStage(stage);
//
//        //- ダイアログ・クローズまで待つ
//        stage.showAndWait();
//        //- リソース解放
//        if (stage.isShowing()) {
//            stage.close();
//        }
//        
//        stage = null;
    }

    /**
     * About を表示する。
     */
    public void showAbout() {
        AboutDolphin about = new AboutDolphin();
        about.setFrame(getFrame());
        about.start();
    }

    public boolean doLoadStampTree() {
        try {
            //- ログインユーザーの PK
            long userPk = Project.getUserModel().getId();

            // ユーザーのStampTreeを検索する
            StampDelegater stampDel = new StampDelegater();
            treeList = stampDel.getTrees(userPk);

            // User用のStampTreeが存在しない新規ユーザの場合、そのTreeを生成する
            boolean hasTree = false;
            if (treeList != null && treeList.size() > 0) {
                for (IStampTreeModel tree : treeList) {
                    if (tree != null) {
                        long id = tree.getUserModel().getId();
                        if (id == userPk && tree instanceof open.dolphin.infomodel.StampTreeModel) { // 注意
                            hasTree = true;
                            break;
                        }
                    }
                }
            }

            //- 新規ユーザでデータベースに個人用のStampTreeが存在しなかった場合
            if (!hasTree) {
                ClientContext.getBootLogger().info("新規ユーザー、スタンプツリーをリソースから構築");

                BufferedReader reader;
                IStampTreeModel tm;
                try (InputStream in = ClientContext.getResourceAsStream("stamptree-seed.xml")) {
                    reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    String treeXml = sb.toString();
                    tm = new open.dolphin.infomodel.StampTreeModel();
                    tm.setUserModel(Project.getUserModel());
                    tm.setName(ClientContext.getString("stampTree.personal.box.name"));
                    tm.setDescription(ClientContext.getString("stampTree.personal.box.tooltip"));
                    FacilityModel facility = Project.getUserModel().getFacilityModel();
                    tm.setPartyName(facility.getFacilityName());
                    String url = facility.getUrl();
                    if (url != null) {
                        tm.setUrl(url);
                    }
                    tm.setTreeXml(treeXml);
                }
                reader.close();

                //- 先勝ちの制御を行うため sysnc する
                //- 一度登録する
                String pkAndVersion = stampDel.syncTree(tm);
                String[] params = pkAndVersion.split(",");
                tm.setId(Long.parseLong(params[0]));
                ((StampTreeModel) tm).setVersionNumber(params[1]);

                //- リストの先頭へ追加する
                treeList.add(0, tm);
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public void processExit(final int flag) {
//
//        if (isDirty()) {
//            alertDirty();
//            return;
//        }

        //- 一括終了時のステータスクリア、かつ未保存ファイル存在時チェック
        boolean retValue = setAllChartKarteClosedStatus();
        if (!retValue) {
            return;
        }

        // Stamp 保存
        final IStampTreeModel treeTosave = stampBox.getUsersTreeTosave();

        SimpleWorker worker = new SimpleWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                ClientContext.getBootLogger().info("stampTask doInBackground");
                // Stamp 保存
                StampDelegater dl = new StampDelegater();
                dl.putTree(treeTosave);
                return null;
            }

            @Override
            protected void succeeded(Void result) {

                ClientContext.getBootLogger().info("stampTask succeeded");
                shutdown();
                frame.setVisible(false);
                ClientContext.getBootLogger().info("メインウィンドウを終了します");

                if (flag == 0) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            application.setScene(getScene());
                            application.setStage(getStage());
                            application.loginDisplay();
                            ClientContext.getBootLogger().info("WindowClose.");
                        }
                    });
                }
                if (flag == 1) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    application.setScene(getScene());
                                    application.setStage(getStage());
                                    application.loginDisplay();
                                    ClientContext.getBootLogger().info("WindowClose.");
                                }
                            });
                        }
                    });
                }
            }

            @Override
            protected void failed(Throwable cause) {
                String test = (cause != null && cause.getMessage() != null) ? cause.getMessage() : null;
                //if (cause instanceof FirstCommitWinException) {
                if (test != null && test.indexOf("First Commit Win") >= 0) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            doFirstCommitWinAlert(treeTosave);
                        }
                    });

                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            doStoppingAlert();
                        }
                    });
                }
                ClientContext.getBootLogger().warn("stampTask failed");
                ClientContext.getBootLogger().warn(cause);
            }

            @Override
            protected void startProgress() {
                delayCount = 0;
                taskTimer.start();
            }

            @Override
            protected void stopProgress() {
                if (taskTimer.isRunning()) {
                    taskTimer.stop();
                }
                monitor.close();
                taskTimer = null;
                monitor = null;
            }
        };

        String message = "環境保存";
        String note = "環境を保存しています...";
        Component c = getFrame();
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delayCount++;
                monitor.setProgress(delayCount);
            }
        });

        worker.execute();
    }

    private boolean isDirty() {

        // 未保存のカルテがある場合は警告しリターンする
        // カルテを保存または破棄してから再度実行する
        boolean dirty = false;

        // Chart を調べる
        List<ChartImpl> allChart = ChartImpl.getAllChart();
        if (allChart != null && allChart.size() > 0) {
            for (ChartImpl chart : allChart) {
                if (chart.isDirty()) {
                    dirty = true;
                    break;
                }
            }
        }

        // 保存してないものがあればリターンする
        if (dirty) {
            return true;
        }

        // EditorFrameのチェックを行う
        java.util.List<Chart> allEditorFrames = EditorFrame.getAllEditorFrames();
        if (allEditorFrames != null && allEditorFrames.size() > 0) {
            for (Chart chart : allEditorFrames) {
                if (chart.isDirty()) {
                    dirty = true;
                    break;
                } else {
                    chart.close();
                }
            }
        }

        return dirty;
    }

    private void alertDirty() {
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        String msg0 = resource.getString("exitDolphin.msg0"); // 未保存のドキュメントがあります。;
        String msg1 = resource.getString("exitDolphin.msg1"); // 保存または破棄した後に再度実行してください。;
        String taskTitle = resource.getString("exitDolphin.taskTitle");
        JOptionPane.showMessageDialog(
                (Component) null,
                new Object[]{msg0, msg1},
                ClientContext.getFrameTitle(taskTitle),
                JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean setAllChartKarteClosedStatus() {

        try {
            // ウィンドウが閉じられた時に開いていたカルテを閉じてステータスを解除する
            for (Entry<String, ChartImpl> entry : chartImplMap.entrySet()) {
                String patientId = entry.getKey();
                //masuda^ この患者のEditorFrameが開いたままなら、インスペクタを閉じられないようにする
                List<Chart> editorFrames = EditorFrame.getAllEditorFrames();
                if (editorFrames != null && !editorFrames.isEmpty()) {
                    long ptId = Long.parseLong(patientId);
                    for (Chart chart : editorFrames) {
                        long id = Long.parseLong(chart.getPatient().getPatientId());
                        if (ptId == id) {
                            // よくわからないEditorFrameが残っていて、Frameがぬるぽのときがあるので
                            try {
                                // 最小化してたらFrameを再表示させる
                                chart.getFrame().setExtendedState(Frame.NORMAL);
                                //JOptionPane.showMessageDialog(this.getFrame(),
                                String title = ClientContext.getFrameTitle("インスペクタ");
                                JOptionPane.showMessageDialog(chart.getFrame(),
                                        "インスペクタを閉じる前にカルテエディタを閉じてください。",
                                        title, JOptionPane.WARNING_MESSAGE);
                                return false;
                            } catch (HeadlessException e) {
                            }
                        }
                    }
                }

                if (!patientIdMap.containsKey(patientId)) {
                    continue;
                }
                ChartImpl chart = entry.getValue();

                int i = 0;
                for (; i < baseTabPane.getTabbedPane().getTabCount(); i++) {
                    String title = baseTabPane.getTabbedPane().getTitleAt(i);
                    if (title.equals(patientId)) {
                        break;
                    }
                }

                if (!chart.isDirty()) {
                    // リストから削除し状態変化を通知する
                    PatientVisitModel model = patientIdMap.get(patientId);
                    ChartEventHandler lscl = ChartEventHandler.getInstance();
                    lscl.publishKarteClosed(model);
                    // メモ内容保存
                    chart.getPatientInspector().dispose();
                    // カルテタブ削除
                    baseTabPane.getTabbedPane().removeTabAt(i);
                    // カルテタブ表示用患者IDリストからID削除
                    patientIdList.remove(patientId);
                    patientIdMap.remove(patientId);
                    continue;
                }

                baseTabPane.getTabbedPane().setSelectedIndex(i);
                if (chart.getKarteEditor() != null) {
                    KarteEditor editor = chart.getKarteEditor();
                    String ret = chart.closeForEvo();
                    // 保存時併用禁忌有無のチェック処理後にカルテタブ閉じる処理を行う
                    if (!editor.isCancelFlag() && ret.equals("0") || ret.equals("3")) {
                        tabClosingFunc(patientId, chart, i);
                        // 保存ダイアログが破棄で無くかつ保存時併用禁忌有無のチェック処理が取消場合、何もしない
                    } else if (editor.isCancelFlag() && !ret.equals("1")) {
                        return false;
                        // 破棄時カルテタブを閉じる処理を行う
                    } else if (ret.equals("1")) {
                        tabClosingFunc(patientId, chart, i);
                        // 取消時、何もせずリターン
                    } else if (ret.equals("2")) {
                        return false;
                    }
                } else {
                    String ret = chart.closeForEvo();
                    // 保存時併用禁忌有無のチェック処理後にカルテタブ閉じる処理を行う
                    if (ret.equals("0") || ret.equals("3")) {
                        tabClosingFunc(patientId, chart, i);
                        // 保存ダイアログが破棄で無くかつ保存時併用禁忌有無のチェック処理が取消場合、何もしない
                    } else if (!ret.equals("1")) {
                        return false;
                        // 破棄時カルテタブを閉じる処理を行う
                    } else if (ret.equals("1")) {
                        tabClosingFunc(patientId, chart, i);
                        // 取消時、何もせずリターン
                    } else if (ret.equals("2")) {
                        return false;
                    }
                }
            }
            chartImplMap.clear();
            return true;
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
            return false;
        }
    }

    private void shutdown() {

        // ChartEvenrHandler 終了
        try {
            if (scl != null) {
                scl.stop();
            }
        } catch (Exception e) {
            //
        }

        if (providers != null) {

            try {
                Iterator iter = providers.values().iterator();
                while (iter != null && iter.hasNext()) {
                    MainService pl = (MainService) iter.next();
                    pl.stop();
                }
                Project.saveUserDefaults();

            } catch (Exception e) {
                e.printStackTrace(System.err);
                ClientContext.getBootLogger().warn(e.toString());
            }
        }
    }

    /**
     * 先勝ち制御アラート
     */
    private void doFirstCommitWinAlert(IStampTreeModel treeTosave) {
        String[] options = {"終 了", "強制書き込み", GUIFactory.getCancelButtonText()};
        StringBuilder sb = new StringBuilder();
        sb.append("スタンプツリーは他の端末によって先に保存されています。").append("\n");
        sb.append("強制書き込みを選択すると先のツリーを上書きします。");
        String msg = sb.toString();
        String title = ClientContext.getFrameTitle("環境保存");

        int option = JOptionPane.showOptionDialog(
                getFrame(), msg, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

        switch (option) {
            case 0:
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ClientContext.getBootLogger().info("ShutDown app.");
                        shutdown();
                        frame.setVisible(false);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                ClientContext.getBootLogger().info("Restart Fx Menu.");
                                application.setScene(getScene());
                                application.setStage(getStage());
                                application.loginDisplay();
                                ClientContext.getBootLogger().info("WindowClose.");
                            }
                        });
                    }
                });

                break;

            case 1:
                syncTreeAndShutDown(treeTosave);
                break;

            case 2:
                break;
        }
    }

    /**
     * StampTree 強制保存
     *
     * @param treeTosave
     */
    private void syncTreeAndShutDown(final IStampTreeModel treeTosave) {

        SimpleWorker worker = new SimpleWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                ClientContext.getBootLogger().info("stampTask doInBackground");
                // Stamp 保存
                StampDelegater dl = new StampDelegater();
                dl.forceSyncTree(treeTosave);
                return null;
            }

            @Override
            protected void succeeded(Void result) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        ClientContext.getBootLogger().info("stampTask succeeded");
                        shutdown();
                        frame.setVisible(false);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                application.setScene(getScene());
                                application.setStage(getStage());
                                application.loginDisplay();
                                ClientContext.getBootLogger().info("WindowClose.");
                            }
                        });
                    }
                });
            }

            @Override
            protected void failed(Throwable cause) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        doStoppingAlert();
                    }
                });
                ClientContext.getBootLogger().warn("stampTask failed");
                ClientContext.getBootLogger().warn(cause);
            }

            @Override
            protected void startProgress() {
                delayCount = 0;
                taskTimer.start();
            }

            @Override
            protected void stopProgress() {
                taskTimer.stop();
                monitor.close();
                taskTimer = null;
                monitor = null;
            }
        };

        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        String message = resource.getString("exitDolphin.taskTitle");
        String note = resource.getString("exitDolphin.savingNote");
        Component c = getFrame();
        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);

        taskTimer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delayCount++;
                monitor.setProgress(delayCount);
            }
        });

        worker.execute();
    }

    public void tryLoadStampTree() {
        boolean tree = doLoadStampTree();
        if (!tree) {
            ClientContext.getPart11Logger().info("[Stamp] Server Connection error.");
            treeList = null;
        } else {
            ClientContext.getPart11Logger().info("[Stamp] Get StampTree !");
            loadResource(treeList);
        }
    }

    /**
     * 終了処理中にエラーが生じた場合の警告をダイアログを表示する。
     *
     * @param errorTask エラーが生じたタスク
     * @return ユーザの選択値
     */
    private void doStoppingAlert() {
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        String msg1 = resource.getString("exitDolphin.err.msg1");
        String msg2 = resource.getString("exitDolphin.err.msg2");
        String msg3 = resource.getString("exitDolphin.err.msg3");
        String msg4 = resource.getString("exitDolphin.err.msg4");
        Object message = new Object[]{msg1, msg2, msg3, msg4};

        // 終了する
        String exitOption = resource.getString("exitDolphin.exitOption");

        // キャンセルする
        String cancelOption = resource.getString("exitDolphin.cancelOption");

        // 環境保存
        String taskTitle = resource.getString("exitDolphin.taskTitle");

        String title = ClientContext.getFrameTitle(taskTitle);

        String[] options = new String[]{cancelOption, exitOption};

        int option = JOptionPane.showOptionDialog(
                null, message, title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null, options, options[0]);

        if (option == 1) {
            shutdown();
        }
    }

    protected Component makePanel(JPanel param) {
        JPanel panel = new JPanel(false);
        JScrollPane jScrollPane1 = new JScrollPane();
        jScrollPane1.getViewport().add(param, null);
        panel.add(jScrollPane1, null);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(jScrollPane1);
        return panel;
    }

    protected Component makeJfxPanel(JFXPanel param) {
        JPanel panel = new JPanel(false);
        JScrollPane jScrollPane1 = new JScrollPane();
        jScrollPane1.getViewport().add(param, null);
        panel.add(jScrollPane1, null);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(jScrollPane1);
        return panel;
    }

    private void tabClosingFunc(String patientId, ChartImpl chart, int index) {
        // カルテタブ削除   
        baseTabPane.getTabbedPane().removeTabAt(index);
        // カルテタブ表示用患者IDリストからID削除
        patientIdList.remove(patientIdList.indexOf(patientId));
        // リストから削除し状態変化を通知する
        ChartEventHandler sc = ChartEventHandler.getInstance();
        sc.publishKarteClosed(patientIdMap.get(patientId));
        // メモ内容保存
        chart.getPatientInspector().dispose();
        patientIdMap.remove(patientId);
    }
}
