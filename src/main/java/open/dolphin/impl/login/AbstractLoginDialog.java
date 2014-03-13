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
package open.dolphin.impl.login;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.*;
import open.dolphin.client.BlockGlass;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ILoginDialog;
import open.dolphin.delegater.UserDelegater;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.project.Project;
import open.dolphin.project.ProjectSettingDialog;
import open.dolphin.util.Log;
import open.dolphin.utilities.control.RssReaderPane;

/**
 * ログインダイアログクラス
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public abstract class AbstractLoginDialog implements ILoginDialog {

    protected JDialog dialog;
    protected BlockGlass blockGlass;

    // 認証制御用
    protected UserDelegater userDlg;
    protected int tryCount;
    protected int maxTryCount;
    protected SimpleWorker worker;

    // 認証結果のプロパティ
    protected LoginStatus result;
    protected PropertyChangeSupport boundSupport;

    protected Action loginAction;
    protected Action cancelAction;
    protected Action settingAction;

    /**
     * Creates new LoginService
     */
    public AbstractLoginDialog() {
        boundSupport = new PropertyChangeSupport(this);
    }

    /**
     * 認証結果プロパティリスナを登録する。
     *
     * @param prop
     * @param listener 登録する認証結果リスナ
     */
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        boundSupport.addPropertyChangeListener(prop, listener);
    }

    /**
     * 認証結果プロパティリスナを登録する。
     *
     * @param prop
     * @param listener 削除する認証結果リスナ
     */
    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        boundSupport.removePropertyChangeListener(prop, listener);
    }

    /**
     * 認証が成功したかどうかを返す。
     *
     * @return true 認証が成功した場合
     */
    public LoginStatus getResult() {
        return result;
    }

    public void setResult(LoginStatus value) {
        this.result = value;
        boundSupport.firePropertyChange("LOGIN_PROP", -100, this.result);
    }

    /**
     * 警告メッセージを表示する。
     *
     * @param msg 表示するメッセージ
     */
    protected void showMessageDialog(String msg) {
        String title = dialog.getTitle();
        JOptionPane.showMessageDialog(null, msg, title, JOptionPane.WARNING_MESSAGE);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, title, msg);
    }

    /**
     * ログイン画面を開始する。
     */
    @Override
    public void start() {

        //-------------------------
        // GUI を構築しモデルを表示する
        //-------------------------
        JPanel content = createComponents();

        loginAction = new AbstractAction("ログイン") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                tryLogin();
            }
        };
        loginAction.setEnabled(false);
        getLoginButton().setAction(loginAction);

        cancelAction = new AbstractAction("キャンセル") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                doCancel();
            }
        };
        getCancelButton().setAction(cancelAction);

        settingAction = new AbstractAction("設 定") {
            @Override
            public void actionPerformed(ActionEvent ae) {
                doSetting();
            }
        };
        getSettingButton().setAction(settingAction);

        String title = ClientContext.getString("loginDialog.title");
        String windowTitle = ClientContext.getFrameTitle(title);
        dialog = new JDialog((Frame) null, windowTitle, true);
        dialog.setTitle(windowTitle);
        dialog.getRootPane().setDefaultButton(getLoginButton());
        blockGlass = new BlockGlass();
        dialog.setGlassPane(blockGlass);
        dialog.getContentPane().add(content);

        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                doCancel();
            }

            @Override
            public void windowOpened(WindowEvent e) {
                doWindowOpened();
            }
        });

        //-------------------------------------
        // 中央へ表示する。（EDT からコールされている）
        //-------------------------------------
        dialog.pack();
        int width = dialog.getWidth();
        int height = dialog.getHeight();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int n = ClientContext.isMac() ? 3 : 2;
        int left = (screen.width - width) / 2;
        int top = (screen.height - height) / n;
        dialog.setLocation(left, top);
        dialog.setVisible(true);
    }

    /**
     * ログインダイアログをクローズする。
     */
    @Override
    public void close() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    /**
     * ログインをキャンセルする。
     */
    public void doCancel() {
        setResult(LoginStatus.CANCELD);
    }

    protected void setBusy(boolean busy) {
        if (busy) {
            blockGlass.block();
            getProgressBar().setIndeterminate(true);
        } else {
            blockGlass.unblock();
            getProgressBar().setIndeterminate(false);
            getProgressBar().setValue(0);
        }
        loginAction.setEnabled(!busy);
        cancelAction.setEnabled(!busy);
        settingAction.setEnabled(!busy);
    }

    protected void showUserIdPasswordError() {
        StringBuilder sb = new StringBuilder();
        sb.append("認証に失敗しました。");
        sb.append("\n");
        sb.append("ユーザーIDまたはパスワードが違います。");
        String msg = sb.toString();
        showMessageDialog(msg);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, msg);
        ClientContext.getPart11Logger().warn(msg);
    }

    protected void showTryOutError() {
        StringBuilder sb = new StringBuilder();
        sb.append("認証に規定の回数失敗しました。");
        sb.append("\n");
        sb.append("アプリケーションを終了します。");
        String msg = sb.toString();
        showMessageDialog(msg);
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, msg);
        ClientContext.getPart11Logger().warn(msg);
    }

    protected void showTestExpiredError() {
        StringBuilder sb = new StringBuilder();
        sb.append("評価期間が終了しました。");
        sb.append("\n");
        sb.append("ご利用ありがとうございました。");
        String msg = sb.toString();
        showMessageDialog(msg);
        ClientContext.getPart11Logger().warn(msg);
    }

//    protected boolean isTestUser(UserModel user) {
//        boolean test = ClientContext.is5mTest();
//        test = test && user.getMemberType().equals("ASP_TESTER");
//        return test;
//    }
//    protected boolean isExpired(UserModel user) {
//        
//        // 登録日を取得する
//        Date registered = user.getRegisteredDate();
//        
//        // テスト期間を取得する 単位は月数
//        int testPeriod = ClientContext.getInt("loginDialog.asp.testPeriod");
//        
//        // 登録日にテスト期間を加える
//        GregorianCalendar gc = new GregorianCalendar();
//        gc.setTime(registered);
//        gc.add(Calendar.MONTH, testPeriod);
//        
//        // 今日のを取得する
//        GregorianCalendar today = new GregorianCalendar();
//        
//        // gc が今日以前?
//        return gc.before(today);
//    }
    protected abstract void tryLogin();

    protected abstract JPanel createComponents();

    protected abstract void doWindowOpened();

    protected abstract JButton getLoginButton();

    protected abstract JButton getCancelButton();

    protected abstract JButton getSettingButton();

    protected abstract JProgressBar getProgressBar();

    public abstract void setNewParams(Boolean newValue);

    /**
     * 設定ボタンがおされた時、設定画面を開始する。
     */
    @Override
    public void doSetting() {

        blockGlass.block();

        ProjectSettingDialog sd = new ProjectSettingDialog();
        PropertyChangeListener pl = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                blockGlass.unblock();
                setNewParams((Boolean) evt.getNewValue());
            }
        };
        sd.addPropertyChangeListener("SETTING_PROP", pl);
        sd.setLoginState(false);
        sd.start();
    }

//s.oh^ RSS対応
    protected void showRSSInfo() {
        String rss = null;
        //"http://www.lscc.co.jp/rss/rss_dolphin.xml";
        rss = Project.getString("dolphin.rss");
        if (rss == null || rss.length() <= 0) {
            return;
        }

        RssReaderPane rssPane = new RssReaderPane();

        JDialog diag = new JDialog(new JFrame(), "Dolphin RSS", true);
        diag.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //dialog.getRootPane().setDefaultButton(done);
        //dialog.setPreferredSize(new Dimension(500, 500));

        diag.setContentPane(rssPane.createRssPane(rss));
        diag.pack();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int n = ClientContext.isMac() ? 3 : 2;
        int x = (screen.width - diag.getPreferredSize().width) / 2;
        int y = (screen.height - diag.getPreferredSize().height) / n;
        diag.setLocation(x, y);
        diag.setVisible(true);
    }
//s.oh$
}
