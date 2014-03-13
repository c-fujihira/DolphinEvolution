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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * リソースデータから PopupMenu を生成するクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public class PopupMenuFactory {
    
    private PopupMenuFactory () {
    }
    
    /**
     * リソースとターゲットオブジェクトから PopupMenu を生成して返す。
     * @param resource リソース名
     * @target メソッドを実行するオブジェクト
     */
    public static JPopupMenu create(String resource, Object target, boolean canCopy, boolean canPaste) {
        
        JPopupMenu popMenu = new JPopupMenu ();
        
        String[] itemLine = ClientContext.getStringArray(resource + ".items");
        String[] methodLine = ClientContext.getStringArray(resource + ".methods");
        
        for (int i = 0; i < itemLine.length; i++) {
            
            String name = itemLine[i];
            String method = methodLine[i];
            
            if (name.equals("-")) {
                popMenu.addSeparator();
            }
            else {
                ReflectAction action = new ReflectAction(name, target, method);
                JMenuItem item = new JMenuItem(action);
                popMenu.add(item);
                
                if (method.equals("copy")) {
                    action.setEnabled(canCopy);
                } else if (method.equals("paste")) {
                    action.setEnabled(canPaste);
                }
            }
        }
        return popMenu;
    }
}


