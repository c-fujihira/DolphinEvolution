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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentListener;
import open.dolphin.client.ClientContext;
import open.dolphin.project.Project;

/**
 * ComponentMemory
 *
 * @author Kazushi Minagawa
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class ComponentMemory implements ComponentListener {

    private final Component target;
    private final Point defaultLocation;
    private final Dimension defaultSise;
    private String name;
    private final boolean report = true;

    public ComponentMemory(Component target, Point loc, Dimension size, Object object) {
        this.target = target;
        this.defaultLocation = loc;
        this.defaultSise = size;
        if (object != null) {
            this.name = object.getClass().getName();
        }
        target.setLocation(this.defaultLocation);
        target.setSize(this.defaultSise);
        target.addComponentListener(ComponentMemory.this);
    }

    @Override
    public void componentMoved(java.awt.event.ComponentEvent e) {
        Point loc = target.getLocation();
        Project.setInt(name + "_x", loc.x);
        Project.setInt(name + "_y", loc.y);
        if (report) {
            StringBuilder buf = new StringBuilder();
            buf.append(name);
            buf.append(" loc=(");
            buf.append(loc.x);
            buf.append(",");
            buf.append(loc.y);
            buf.append(")");
        }
    }

    @Override
    public void componentResized(java.awt.event.ComponentEvent e) {
        int width = target.getWidth();
        int height = target.getHeight();
        Project.setInt(name + "_width", width);
        Project.setInt(name + "_height", height);
        if (report) {
            StringBuilder buf = new StringBuilder();
            buf.append(name);
            buf.append(" size=(");
            buf.append(width);
            buf.append(",");
            buf.append(height);
            buf.append(")");
        }
    }

    @Override
    public void componentShown(java.awt.event.ComponentEvent e) {
    }

    @Override
    public void componentHidden(java.awt.event.ComponentEvent e) {
    }

    public void setToPreferenceBounds() {
        int x = Project.getInt(name + "_x", defaultLocation.x);
        int y = Project.getInt(name + "_y", defaultLocation.y);
        int width = Project.getInt(name + "_width", defaultSise.width);
        int height = Project.getInt(name + "_height", defaultSise.height);
        target.setBounds(x, y, width, height);
    }

    public void putCenter() {
        if (ClientContext.isMac()) {
            putCenter(3);
        } else {
            putCenter(2);
        }
    }

    public void putCenter(int n) {
        n = n != 0 ? n : 2;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = target.getSize();
        int x = (screenSize.width - size.width) / 2;
        int y = (screenSize.height - size.height) / n;
        target.setBounds(x, y, size.width, size.height);
    }
}
