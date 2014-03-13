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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JTabbedPane;
import javax.swing.tree.DefaultMutableTreeNode;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.IStampTreeModel;
import open.dolphin.infomodel.ModuleInfoBean;

/**
 * AbstractStampBox
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public abstract class AbstractStampBox extends JTabbedPane implements IStampBox {

    protected IStampTreeModel stampTreeModel;
    protected StampBoxPlugin context;

    /**
     * Creates new StampBoxPlugin
     */
    public AbstractStampBox() {
    }

    @Override
    public StampBoxPlugin getContext() {
        return context;
    }

    @Override
    public void setContext(StampBoxPlugin plugin) {
        context = plugin;
    }

    @Override
    public IStampTreeModel getStampTreeModel() {
        return stampTreeModel;
    }

    @Override
    public void setStampTreeModel(IStampTreeModel stampTreeModel) {
        this.stampTreeModel = stampTreeModel;
    }

    protected abstract void buildStampBox();

    /**
     * 引数のカテゴリに対応するTreeを返す。
     *
     * @param category Treeのカテゴリ
     * @return カテゴリにマッチするStampTree
     */
    @Override
    public StampTree getStampTree(String entity) {
        int count = this.getTabCount();
        boolean found = false;
        StampTree tree = null;
        for (int i = 0; i < count; i++) {
            StampTreePanel panel = (StampTreePanel) this.getComponentAt(i);
            tree = panel.getTree();
            if (entity.equals(tree.getEntity())) {
                found = true;
                break;
            }
        }

        return found ? tree : null;
    }

    @Override
    public StampTree getStampTree(int index) {
        if (index >= 0 && index < this.getTabCount()) {
            StampTreePanel panel = (StampTreePanel) this.getComponentAt(index);
            return panel.getTree();
        }
        return null;
    }

    @Override
    public boolean isHasEditor(int index) {
        return false;
    }

    @Override
    public void setHasNoEditorEnabled(boolean b) {
    }

    /**
     * スタンプボックスに含まれる全treeのTreeInfoリストを返す。
     *
     * @return TreeInfoのリスト
     */
    @Override
    public List<TreeInfo> getAllTreeInfos() {
        List<TreeInfo> ret = new ArrayList<TreeInfo>();
        int cnt = this.getTabCount();
        for (int i = 0; i < cnt; i++) {
            StampTreePanel tp = (StampTreePanel) this.getComponent(i);
            StampTree tree = tp.getTree();
            TreeInfo info = tree.getTreeInfo();
            ret.add(info);
        }
        return ret;
    }

    /**
     * スタンプボックスに含まれる全treeを返す。
     *
     * @return StampTreeのリスト
     */
    @Override
    public List<StampTree> getAllTrees() {
        List<StampTree> ret = new ArrayList<StampTree>();
        int cnt = this.getTabCount();
        for (int i = 0; i < cnt; i++) {
            StampTreePanel tp = (StampTreePanel) this.getComponent(i);
            StampTree tree = tp.getTree();
            ret.add(tree);
        }
        return ret;
    }

    /**
     * スタンプボックスに含まれる病名以外のStampTreeを返す。
     *
     * @return StampTreeのリスト
     */
    public List<StampTree> getAllPTrees() {

        List<StampTree> ret = new ArrayList<StampTree>();
        int cnt = this.getTabCount();

        for (int i = 0; i < cnt; i++) {
            StampTreePanel tp = (StampTreePanel) this.getComponent(i);
            StampTree tree = tp.getTree();
            //
            // 病名StampTree はスキップする
            //
            if (tree.getEntity().equals(IInfoModel.ENTITY_DIAGNOSIS)) {
                continue;
            } else {
                ret.add(tree);
            }
        }

        return ret;
    }

    /**
     * 引数のエンティティ配下にある全てのスタンプを返す。 これはメニュー等で使用する。
     *
     * @param entity Treeのエンティティ
     * @return 全てのスタンプのリスト
     */
    @Override
    public List<ModuleInfoBean> getAllStamps(String entity) {

        StampTree tree = getStampTree(entity);
        if (tree != null) {
            List<ModuleInfoBean> ret = new ArrayList<ModuleInfoBean>();
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
            Enumeration e = rootNode.preorderEnumeration();
            while (e.hasMoreElements()) {
                StampTreeNode node = (StampTreeNode) e.nextElement();
                if (node.isLeaf()) {
                    ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                    ret.add(info);
                }
            }
            return ret;
        }

        return null;
    }

    @Override
    public List<String> getEntities() {
        List<String> ret = new ArrayList<String>();
        List<TreeInfo> infos = getAllTreeInfos();
        for (TreeInfo ti : infos) {
            ret.add(ti.getEntity());
        }
        return ret;
    }

    @Override
    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(stampTreeModel.getName());
        sb.append(" ");
        sb.append(stampTreeModel.getPartyName());
        if (sb.length() > 16) {
            sb.setLength(12);
            sb.append("...");
        }
        return sb.toString();
    }
}
