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
import java.io.StringReader;
import java.util.List;

/**
 * AspStampBox
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class AspStampBox extends AbstractStampBox {

    /**
     * Creates new StampBoxPlugin
     */
    public AspStampBox() {
    }

    @Override
    protected void buildStampBox() {

        try {
            // Build stampTree
            BufferedReader reader = new BufferedReader(new StringReader(stampTreeModel.getTreeXml()));
            ASpStampTreeBuilder builder = new ASpStampTreeBuilder();
            StampTreeDirector director = new StampTreeDirector(builder);
            List<StampTree> aspTrees = director.build(reader);
            reader.close();
            stampTreeModel.setTreeXml(null);

            // StampTreeに設定するポップアップメニューとトランスファーハンドラーを生成する
            AspStampTreeTransferHandler transferHandler = new AspStampTreeTransferHandler();

            // StampBox(TabbedPane) へリスト順に格納する
            for (StampTree stampTree : aspTrees) {
                stampTree.setTransferHandler(transferHandler);
                stampTree.setAsp(true);
                stampTree.setStampBox(getContext());
                StampTreePanel treePanel = new StampTreePanel(stampTree);
                this.addTab(stampTree.getTreeName(), treePanel);
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
