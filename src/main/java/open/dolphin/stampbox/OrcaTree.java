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
package open.dolphin.stampbox;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import open.dolphin.client.ClientContext;
import open.dolphin.delegater.OrcaDelegater;
import open.dolphin.delegater.OrcaDelegaterFactory;
import open.dolphin.helper.SimpleWorker;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.OrcaInputCd;
import open.dolphin.project.Project;
import open.dolphin.util.Log;

/**
 * ORCA StampTree クラス
 *
 * @author Kazushi Minagawa
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class OrcaTree extends StampTree {

    private static final String MONITOR_TITLE = "ORCAセット検索";

    /**
     * ORCA 入力セットをフェッチしたかどうかのフラグ
     */
    private boolean fetched;

    private ProgressMonitor monitor;
    private Timer taskTimer;
    private int delayCount;

    /**
     * Creates a new instance of OrcaTree
     */
    public OrcaTree(TreeModel model) {
        super(model);
    }

    /**
     * ORCA 入力セットをフェッチしたかどうかを返す。
     *
     * @return 取得済みのとき true
     */
    public boolean isFetched() {
        return fetched;
    }

    /**
     * ORCA 入力セットをフェッチしたかどうかを設定する。
     *
     * @param fetched 取得済みのとき true
     */
    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }

    /**
     * StampBox のタブでこのTreeが選択された時コールされる。
     */
    @Override
    public void enter() {

        // ORCAへアクセスできないケース及び取得済みを追加^
//        if ((!Project.canAccessToOrca()) || fetched) {
//            return;
//        }
        // ORCAへアクセスできないケースを追加$
        if (SwingUtilities.isEventDispatchThread()) {
            fetchOrcaSet();
        } else {
            fetchOrcaSet2();
        }
    }

    /**
     * ORCA の入力セットを取得しTreeに加える。
     */
    private void fetchOrcaSet2() {

        try {
            //SqlOrcaSetDao dao = new SqlOrcaSetDao();
            OrcaDelegater dao = OrcaDelegaterFactory.create();
            ArrayList<OrcaInputCd> inputSet = dao.getOrcaInputSet();
            StampTreeNode root = (StampTreeNode) this.getModel().getRoot();

            for (OrcaInputCd set : inputSet) {
                ModuleInfoBean stampInfo = set.getStampInfo();
                StampTreeNode node = new StampTreeNode(stampInfo);
                root.add(node);
            }

            DefaultTreeModel model = (DefaultTreeModel) this.getModel();
            model.reload(root);

            setFetched(true);

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * ORCA の入力セットを取得しTreeに加える。
     */
    private void fetchOrcaSet() {

        String message = MONITOR_TITLE;
        String note = "入力セットを検索しています...  ";
        final Component c = SwingUtilities.getWindowAncestor(this);
        int maxEstimation = 60 * 1000;
        int delay = 300;

        final SimpleWorker worker = new SimpleWorker<List<OrcaInputCd>, Void>() {

            @Override
            protected List<OrcaInputCd> doInBackground() throws Exception {
                //SqlOrcaSetDao dao = new SqlOrcaSetDao();
                OrcaDelegater dao = OrcaDelegaterFactory.create();
                List<OrcaInputCd> result = dao.getOrcaInputSet();
                return result;
//                if (dao.isNoError()) {
//                    return result;
//                } else {
//                    throw new Exception(dao.getErrorMessage());
//                }
            }

            @Override
            protected void succeeded(List<OrcaInputCd> result) {
                processResult(result);
            }

            @Override
            protected void failed(Throwable e) {
                String title = ClientContext.getFrameTitle(MONITOR_TITLE);
                JOptionPane.showMessageDialog(c, e.getMessage(), title, JOptionPane.WARNING_MESSAGE);
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, title, e.getMessage());
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

        monitor = new ProgressMonitor(c, message, note, 0, maxEstimation / delay);
        taskTimer = new Timer(delay, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delayCount++;

                if (monitor.isCanceled() && (!worker.isCancelled())) {
                    worker.cancel(true);

                } else {
                    monitor.setProgress(delayCount);
                }
            }
        });

        worker.execute();
    }

    /**
     * ORCAセットのStampTreeを構築する。
     */
    private void processResult(List<OrcaInputCd> inputSet) {

        if (inputSet == null || inputSet.isEmpty()) {
            return;
        }

        StampTreeNode root = (StampTreeNode) this.getModel().getRoot();

        // 重複したORCA情報をリストに加えないようにするために検査後、追加
        for (OrcaInputCd set : inputSet) {
            boolean flag = true;
            for (int i = 0; i < root.getChildCount(); i++) {
                StampTreeNode listTreeNode = (StampTreeNode) root.getChildAt(i);
                ModuleInfoBean listStampInfo = listTreeNode.getStampInfo();
                if (listStampInfo.getStampName().equals(set.getStampInfo().getStampName())) {
                    flag = false;
                    break;
                }
            }
            // 同一情報のスタンプがない場合にはリストに追加
            if (flag) {
                ModuleInfoBean stampInfo = set.getStampInfo();
                StampTreeNode node = new StampTreeNode(stampInfo);
                root.add(node);
            }
        }

        DefaultTreeModel model = (DefaultTreeModel) this.getModel();
        model.reload(root);

        setFetched(true);
    }
}
