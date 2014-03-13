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
package open.dolphin.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * MenuSupport
 *
 * @author Minagawa,Kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class MenuSupport implements MenuListener {

    private ActionMap actions;
    private Object[] chains;

    public MenuSupport(Object owner) {
        Object[] chs = new Object[3];
        chs[1] = MenuSupport.this;
        chs[2] = owner;
        setChains(chs);
    }

    @Override
    public void menuSelected(MenuEvent e) {
    }

    @Override
    public void menuDeselected(MenuEvent e) {
    }

    @Override
    public void menuCanceled(MenuEvent e) {
    }

    public void registerActions(ActionMap actions) {
        this.actions = actions;
    }

    public Action getAction(String name) {
        if (actions != null) {
            return actions.get(name);
        }
        return null;
    }

    public ActionMap getActions() {
        return actions;
    }

    public void disableAllMenus() {
        if (actions != null) {
            Object[] keys = actions.keys();
            for (Object o : keys) {
                actions.get(o).setEnabled(false);
            }
        }
    }

    public void disableMenus(String[] menus) {
        if (actions != null && menus != null) {
            for (String name : menus) {
                Action action = actions.get(name);
                if (action != null) {
                    action.setEnabled(false);
                }
            }
        }
    }

    public void enableMenus(String[] menus) {
        if (actions != null && menus != null) {
            for (String name : menus) {
                Action action = actions.get(name);
                if (action != null) {
                    action.setEnabled(true);
                }
            }
        }
    }

    public void enabledAction(String name, boolean enabled) {
        if (actions != null) {
            Action action = actions.get(name);
            if (action != null) {
                action.setEnabled(enabled);
            }
        }
    }

    public final void setChains(Object[] chains) {
        this.chains = chains;
    }

    public Object[] getChains() {
        return chains;
    }

    public void addChain(Object obj) {
        // 最初のターゲットに設定する
        chains[0] = obj;
    }

    public Object getChain() {
        // 最初のターゲットを返す
        return chains[0];
    }

    /**
     * chain にそってリフレクションでメソッドを実行する。 メソッドを実行するオブジェクトがあればそこで終了する。
     * メソッドを実行するオブジェクトが存在しない場合もそこで終了する。 コマンドチェインパターンのリフレクション版。
     *
     * @param method
     * @return メソッドが実行された時 true
     */
    public boolean sendToChain(String method) {

        boolean handled = false;

        if (chains != null) {

            for (Object target : chains) {

                if (target != null) {
                    try {
                        Method mth = target.getClass().getMethod(method, (Class[])null);
                        mth.invoke(target, (Object[])null);
                        handled = true;
                        break;
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        // この target では実行できない
                    }
                }
            }
        }

        return handled;
    }

    public boolean sendToChain(String method, String arg) {

        boolean handled = false;

        if (chains != null) {

            for (Object target : chains) {

                if (target != null) {
                    try {
                        Method mth = target.getClass().getMethod(method, new Class[]{String.class});
                        mth.invoke(target, new Object[]{arg});
                        handled = true;
                        break;
                    } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        // この target では実行できない
                    }
                }
            }
        }

        return handled;
    }

    public void cut() {
    }

    public void copy() {
    }

    public void paste() {
    }
}
