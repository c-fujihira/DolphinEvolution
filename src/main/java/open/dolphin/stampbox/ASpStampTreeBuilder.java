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
import java.util.LinkedList;
import java.util.List;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.ModuleInfoBean;

/**
 * DefaultStampTreeBuilder
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class ASpStampTreeBuilder extends AbstractStampTreeBuilder {

    /**
     * Control staffs
     */
    private StampTreeNode rootNode;
    private StampTreeNode node;
    private ModuleInfoBean info;
    private LinkedList<StampTreeNode> linkedList;
    private List<StampTree> products;

    private boolean DEBUG;

    // Goddy conversion
    private final boolean goddyConversion = true;
    private boolean shidoParsing;
    private boolean zaitakuParsing;

    /**
     * Creates new DefaultStampTreeBuilder
     */
    public ASpStampTreeBuilder() {
    }

    /**
     * Returns the product of this builder
     *
     * @return vector that contains StampTree instances
     */
    @Override
    public List<StampTree> getProduct() {
        return products;
    }

    @Override
    public void buildStart() {
        products = new ArrayList<>();
        if (DEBUG) {
            ClientContext.getBootLogger().debug("Build StampTree start");
        }
    }

    @Override
    public void buildRoot(String name, String entity) {
        // New root
        if (DEBUG) {
            ClientContext.getBootLogger().debug("Root=" + name);
        }
        // Goddy conversion.
        if (name.equals("指導") && goddyConversion) {
            name = "指導・在宅";
            shidoParsing = true;
        } else if (name.equals("在宅") && goddyConversion) {
            zaitakuParsing = true;
            return;
        }
        //--------------------------------------
        linkedList = new LinkedList<>();

        // TreeInfo を rootNode に保存する
        TreeInfo treeInfo = new TreeInfo();
        treeInfo.setName(name);
        treeInfo.setEntity(entity);
        rootNode = new StampTreeNode(treeInfo);
        linkedList.addFirst(rootNode);
    }

    @Override
    public void buildNode(String name) {
        // New node
        if (DEBUG) {
            ClientContext.getBootLogger().debug("Node=" + name);
        }

        node = new StampTreeNode(name);
        getCurrentNode().add(node);

        // Add the new node to be current node
        linkedList.addFirst(node);
    }

    @Override
    public void buildStampInfo(String name,
            String role,
            String entity,
            String editable,
            String memo,
            String id) {

        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(",");
            sb.append(role);
            sb.append(",");
            sb.append(entity);
            sb.append(",");
            sb.append(editable);
            sb.append(",");
            sb.append(memo);
            sb.append(",");
            sb.append(id);
            ClientContext.getBootLogger().debug(sb.toString());
        }

        // ASP Tree なのでエディタから発行を無視する
        if (name.equals("エディタから発行...") && (id == null) && (role.equals("p"))) {
            return;
        }

        info = new ModuleInfoBean();
        info.setStampName(name);
        info.setStampRole(role);
        info.setEntity(entity);
        if (editable != null) {
            info.setEditable(Boolean.valueOf(editable).booleanValue());
        }
        if (memo != null) {
            info.setStampMemo(memo);
        }
        if (id != null) {
            info.setStampId(id);
        }

        // StampInfo から TreeNode を生成し現在のノードへ追加する
        node = new StampTreeNode(info);
        getCurrentNode().add(node);
    }

    @Override
    public void buildNodeEnd() {
        if (DEBUG) {
            ClientContext.getBootLogger().debug("End node");
        }
        linkedList.removeFirst();
    }

    @Override
    public void buildRootEnd() {

        if (shidoParsing && goddyConversion) {
            shidoParsing = false;
            return;
        }

        StampTree tree = new StampTree(new StampTreeModel(rootNode));
        products.add(tree);

        if (DEBUG) {
            int pCount = products.size();
            ClientContext.getBootLogger().debug("End root " + "count=" + pCount);
        }
    }

    @Override
    public void buildEnd() {
        if (DEBUG) {
            ClientContext.getBootLogger().debug("Build end");
        }
    }

    private StampTreeNode getCurrentNode() {
        return (StampTreeNode) linkedList.getFirst();
    }

}
