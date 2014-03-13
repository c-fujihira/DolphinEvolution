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

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;
import javax.swing.AbstractAction;
import javax.swing.Icon;

/**
 *
 * @author Kazushi Minagawa.
 */
public class ReflectAction extends AbstractAction {
    
    private static final long serialVersionUID = 4592935637937407137L;
    
    private Object target;
    private String method;
    
    
    public ReflectAction(String text) {
        super(text);
    }
    
    public void setTarget(Object target) {
        this.target = target;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public ReflectAction(String text, Object target, String method) {
        super(text);
        this.target = target;
        this.method = method;
    }
    
    public ReflectAction(String text, Icon icon, Object target, String method) {
        super(text, icon);
        this.target = target;
        this.method = method;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        
        try {
            Method mth = target.getClass().getMethod(method, (Class[]) null);
            mth.invoke(target, (Object[])null);
            
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
}