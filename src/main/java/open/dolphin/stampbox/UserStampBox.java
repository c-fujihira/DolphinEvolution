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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import javax.swing.DropMode;
import open.dolphin.infomodel.IInfoModel;

/**
 * UserStampBox
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class UserStampBox extends AbstractStampBox {

    private static final String BOX_INFO = "個人用スタンプボックス";

    /**
     * テキストスタンプのタブ番号
     */
    private int textIndex;

    /**
     * パススタンプのタブ番号
     */
    private int pathIndex;

    /**
     * ORCA セットのタブ番号
     */
    private int orcaIndex;

    /**
     * StampBox を構築する。
     */
    @Override
    protected void buildStampBox() {

        try {
            List<StampTree> userTrees;
            try (BufferedReader reader = new BufferedReader(new StringReader(stampTreeModel.getTreeXml()))) {
                DefaultStampTreeBuilder builder = new DefaultStampTreeBuilder();
                StampTreeDirector director = new StampTreeDirector(builder);
                userTrees = director.build(reader);
            }

            stampTreeModel.setTreeXml(null);
            stampTreeModel.setTreeBytes(null);

            // StampTreeへ設定するPopupMenuとTransferHandlerを生成する
            StampTreePopupAdapter popAdapter = new StampTreePopupAdapter();
            StampTreeTransferHandler transferHandler = new StampTreeTransferHandler();

            // StampBox(TabbedPane) へリスト順に格納する
            // 一つのtabへ一つのtreeが対応
            int index = 0;
            for (StampTree stampTree : userTrees) {
                stampTree.setUserTree(true);
                stampTree.setTransferHandler(transferHandler);
                stampTree.setDropMode(DropMode.INSERT);         // INSERT
                stampTree.setStampBox(getContext());
                StampTreePanel treePanel = new StampTreePanel(stampTree);
                this.addTab(stampTree.getTreeName(), treePanel);

                // Text、Path、ORCA のタブ番号を保存する
                if (stampTree.getEntity().equals(IInfoModel.ENTITY_TEXT)) {
                    textIndex = index;
                    stampTree.addMouseListener(popAdapter);
                } else if (stampTree.getEntity().equals(IInfoModel.ENTITY_PATH)) {
                    pathIndex = index;
                    stampTree.addMouseListener(popAdapter);
                } else if (stampTree.getEntity().equals(IInfoModel.ENTITY_ORCA)) {
                    orcaIndex = index;
                } else {
                    stampTree.addMouseListener(popAdapter);
                }

                index++;
            }

        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    /**
     * 引数のタブ番号に対応するStampTreeにエディタから発行があるかどうかを返す。
     *
     * @param index タブ番号
     * @return エディタから発行がある場合に true
     */
    @Override
    public boolean isHasEditor(int index) {
        return (index != textIndex && index != pathIndex && index != orcaIndex);
    }

    @Override
    public void setHasNoEditorEnabled(boolean b) {
        this.setEnabledAt(textIndex, b);
        this.setEnabledAt(pathIndex, b);
        this.setEnabledAt(orcaIndex, b);
    }

    @Override
    public String getInfo() {
        return BOX_INFO;
    }
}
