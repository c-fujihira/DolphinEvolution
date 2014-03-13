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
package open.dolphin.impl.scheam;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JButton;
import javax.swing.JToggleButton;

/**
 * Button Action 登録
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class ButtonAction {

    private SchemaCanvas canvas;
    private StateMgr stateMgr;

    public ButtonAction(SchemaEditorImpl context) {
        this.canvas = context.getCanvas();
        this.stateMgr = context.getStateMgr();
    }

    public void register(JToggleButton button, String name, Cursor cursor) {
        button.addActionListener(new ButtonActionListener(name, cursor));
    }

    public void register(JButton button, String name, Cursor cursor) {
        button.addActionListener(new ButtonActionListener(name, cursor));
    }

    private class ButtonActionListener implements ActionListener {

        private Cursor cursor;
        private String name;

        public ButtonActionListener(String name, Cursor cursor) {
            super();
            this.cursor = cursor;
            this.name = name;
        }

        public void actionPerformed(ActionEvent e) {
            try {
                if (cursor != null) {
                    canvas.setCursor(cursor);
                }
                Method method = stateMgr.getClass().getMethod(name, (Class[]) null);
                method.invoke(stateMgr, (Object[]) null);
            } catch (NoSuchMethodException ex) {
            } catch (SecurityException ex) {
            } catch (IllegalAccessException ex) {
                System.out.println(ex);
            } catch (IllegalArgumentException ex) {
                System.out.println(ex);
            } catch (InvocationTargetException ex) {
                System.out.println(ex);
            }
        }

    }
}
