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
package open.dolphin.client;

import open.dolphin.stampbox.StampTreeNode;
import open.dolphin.stampbox.StampTree;
import open.dolphin.stampbox.StampBoxPlugin;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.project.Project;

/**
 * SOAペインのコードヘルパークラス。
 *
 * @author Kazyshi Minagawa
 */
public class SOACodeHelper extends AbstractCodeHelper {
    
    /**
     * Creates a new instance of CodeHelper
     */
    public SOACodeHelper(KartePane pPane, ChartMediator mediator) {
        super(pPane, mediator);
    }
    
    @Override
    protected void buildPopup(String text) {
        
        if (Project.getString(IInfoModel.ENTITY_TEXT).startsWith(text.toLowerCase())) {
            buildEntityPopup(IInfoModel.ENTITY_TEXT);
            
        } else {
            buildMatchPopup(text);
        }
    }
    
    protected void buildMatchPopup(String text) {
        
        StampBoxPlugin stampBox = mediator.getStampBox();
        StampTree tree = stampBox.getStampTree(IInfoModel.ENTITY_TEXT);
        if (tree == null) {
            return;
        }
        
        popup = new JPopupMenu();
        
        //
        // メニューのスタックを生成する
        //
        LinkedList menus = new LinkedList();
        menus.addFirst(popup);
        
        //
        // 親ノードのスタックを生成する
        //
        LinkedList parents = new LinkedList();
        
        //
        // Stamp の名前がキーワードで始まり、それが１個以上あるものを補完メニューに加える
        //
        pattern = Pattern.compile("^" + text + ".*");
        
        DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
        
        Enumeration e = rootNode.preorderEnumeration();
        
        if (e != null) {
            
            e.nextElement(); // consume root
            
            while (e.hasMoreElements()) {
                
                //
                // 調査対象のノードを得る
                //
                StampTreeNode node = (StampTreeNode) e.nextElement();
                
                //
                // その親を得る
                //
                StampTreeNode parent = (StampTreeNode) node.getParent();
                
                //
                // 親がリストに含まれているかどうか
                //
                int index = parents.indexOf(parent);
                if (index > -1) {
                    //
                    // 自分の親がインデックス=0になるまでポップする
                    //
                    for (int i = 0; i < index; i++) {
                        parents.removeFirst();
                        menus.removeFirst();
                    }
                    
                    if (!node.isLeaf()) {
                        //
                        // フォルダの場合
                        //
                        String folderName = node.getUserObject().toString();
                        JMenu subMenu = new JMenu(folderName);
                        if (menus.getFirst() instanceof JPopupMenu) {
                            ((JPopupMenu) menus.getFirst()).add(subMenu);
                        } else {
                            ((JMenu) menus.getFirst()).add(subMenu);
                        }
                        menus.addFirst(subMenu);
                        parents.addFirst(node);
                        JMenuItem item = new JMenuItem(folderName);
                        item.setIcon(icon);
                        subMenu.add(item);
                        addActionListner(item, node);
                        
                    } else {
                        ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                        String completion = info.getStampName();
                        JMenuItem item = new JMenuItem(completion);
                        addActionListner(item, node);
                        if (menus.getFirst() instanceof JPopupMenu) {
                            ((JPopupMenu) menus.getFirst()).add(item);
                        } else {
                            ((JMenu) menus.getFirst()).add(item);
                        }
                    }
                    
                } else {
                    //
                    // 含まれていないのでマッチ検査が必要
                    //
                    if (!node.isLeaf()) {
                        //
                        // フォルダの場合
                        //
                        String completion = node.getUserObject().toString();
                        Matcher matcher = pattern.matcher(completion);
                        if (matcher.matches()) {
                            //
                            // マッチした場合はカレントメニューへ加える
                            // 自分がカレントメニューになる
                            // 親リストに自分を加える
                            String folderName = node.getUserObject().toString();
                            JMenu subMenu = new JMenu(folderName);
                            if (menus.getFirst() instanceof JPopupMenu) {
                                ((JPopupMenu) menus.getFirst()).add(subMenu);
                            } else {
                                ((JMenu) menus.getFirst()).add(subMenu);
                            }
                            menus.addFirst(subMenu);
                            parents.addFirst(node);
                            
                            //
                            // フォルダ選択のアイテムを生成しサブメニューの要素にする
                            //
                            JMenuItem item = new JMenuItem(folderName);
                            item.setIcon(icon);
                            subMenu.add(item);
                            addActionListner(item, node);
                        }
                        
                    } else {
                        //
                        // 葉の場合
                        //
                        ModuleInfoBean info = (ModuleInfoBean) node.getUserObject();
                        String completion = info.getStampName();
                        Matcher matcher = pattern.matcher(completion);
                        
                        if (matcher.matches()) {
                            //
                            // 一致した場合
                            //
                            JMenuItem item = new JMenuItem(completion);
                            addActionListner(item, node);
                            if (menus.getFirst() instanceof JPopupMenu) {
                                ((JPopupMenu) menus.getFirst()).add(item);
                            } else {
                                ((JMenu) menus.getFirst()).add(item);
                            }
                        }
                    }
                }
            }
        }
    }
}
