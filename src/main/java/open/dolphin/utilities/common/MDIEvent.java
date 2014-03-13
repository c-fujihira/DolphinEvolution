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
package open.dolphin.utilities.common;

import javax.swing.DefaultDesktopManager;
import javax.swing.JComponent;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

/**
 * MDIEvent
 *
 * @author oh
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class MDIEvent {

    InternalFrameListenerEx listener;
    DesktopManagerEx manager;

    public MDIEvent() {
        listener = null;
        manager = null;
    }

    public InternalFrameListener getListener() {
        if (listener == null) {
            listener = new InternalFrameListenerEx();
        }
        return listener;
    }

    public DefaultDesktopManager getManager() {
        if (manager == null) {
            manager = new DesktopManagerEx();
        }
        return manager;
    }

    public static void main(String[] args) {
        new MDIEvent();
    }
}

class InternalFrameListenerEx implements InternalFrameListener {

    InternalFrameListenerEx() {

    }

    @Override
    public void internalFrameOpened(InternalFrameEvent e) {
        JInternalFrame f = e.getInternalFrame();
        System.out.println(f.getTitle() + " internalFrameOpened");
    }

    @Override
    public void internalFrameClosing(InternalFrameEvent e) {
        JInternalFrame f = e.getInternalFrame();
        System.out.println(f.getTitle() + " internalFrameClosing");
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        JInternalFrame f = e.getInternalFrame();
        System.out.println(f.getTitle() + " internalFrameClosed");
    }

    @Override
    public void internalFrameIconified(InternalFrameEvent e) {
        JInternalFrame f = e.getInternalFrame();
        System.out.println(f.getTitle() + " internalFrameIconified");
    }

    @Override
    public void internalFrameDeiconified(InternalFrameEvent e) {
        JInternalFrame f = e.getInternalFrame();
        System.out.println(f.getTitle() + " internalFrameDeiconified");
    }

    @Override
    public void internalFrameActivated(InternalFrameEvent e) {
        JInternalFrame f = e.getInternalFrame();
        System.out.println(f.getTitle() + " internalFrameActivated");
    }

    @Override
    public void internalFrameDeactivated(InternalFrameEvent e) {
        JInternalFrame f = e.getInternalFrame();
        System.out.println(f.getTitle() + " internalFrameDeactivated");
    }

}

class DesktopManagerEx extends DefaultDesktopManager {

    DesktopManagerEx() {

    }

    @Override
    public void openFrame(JInternalFrame f) {
        System.out.println("openFrame " + f.getTitle());
        super.openFrame(f);
    }

    @Override
    public void closeFrame(JInternalFrame f) {
        System.out.println("closeFrame " + f.getTitle());
        super.closeFrame(f);
    }

    @Override
    public void maximizeFrame(JInternalFrame f) {
        System.out.println("maximizeFrame " + f.getTitle());
        super.maximizeFrame(f);
    }

    @Override
    public void minimizeFrame(JInternalFrame f) {
        System.out.println("minimizeFrame " + f.getTitle());
        super.minimizeFrame(f);
    }

    @Override
    public void iconifyFrame(JInternalFrame f) {
        System.out.println("iconifyFrame " + f.getTitle());
        super.iconifyFrame(f);
    }

    @Override
    public void deiconifyFrame(JInternalFrame f) {
        System.out.println("deiconifyFrame " + f.getTitle());
        super.deiconifyFrame(f);
    }

    @Override
    public void activateFrame(JInternalFrame f) {
        System.out.println("activateFrame " + f.getTitle());
        super.activateFrame(f);
    }

    @Override
    public void deactivateFrame(JInternalFrame f) {
        System.out.println("deactivateFrame " + f.getTitle());
        super.deactivateFrame(f);
    }

    @Override
    public void beginDraggingFrame(JComponent c) {
        JInternalFrame f = null;
        if (c instanceof JInternalFrame.JDesktopIcon) {
            f = ((JInternalFrame.JDesktopIcon) c).getInternalFrame();
        } else {
            f = (JInternalFrame) c;
        }
        System.out.println("beginDraggingFrame " + f.getTitle());
        super.beginDraggingFrame(c);
    }

    @Override
    public void dragFrame(JComponent c, int newX, int newY) {
        JInternalFrame f = null;
        if (c instanceof JInternalFrame.JDesktopIcon) {
            f = ((JInternalFrame.JDesktopIcon) c).getInternalFrame();
        } else {
            f = (JInternalFrame) c;
        }
        System.out.println("dragFrame " + f.getTitle() + " " + newX + " " + newY);
        super.dragFrame(c, newX, newY);
    }

    @Override
    public void endDraggingFrame(JComponent c) {
        JInternalFrame f = null;
        if (c instanceof JInternalFrame.JDesktopIcon) {
            f = ((JInternalFrame.JDesktopIcon) c).getInternalFrame();
        } else {
            f = (JInternalFrame) c;
        }
        System.out.println("endDraggingFrame " + f.getTitle());
        super.endDraggingFrame(c);
    }

    @Override
    public void beginResizingFrame(JComponent c, int direction) {
        JInternalFrame f = (JInternalFrame) c;
        System.out.println("beginResizingFrame " + f.getTitle() + " " + direction);
        super.beginResizingFrame(c, direction);
    }

    @Override
    public void resizeFrame(JComponent c, int newX, int newY, int newW, int newH) {
        JInternalFrame f = (JInternalFrame) c;
        System.out.println("resizeFrame " + f.getTitle() + " " + newX + " " + newY + " " + newW + " " + newH);
        super.resizeFrame(f, newX, newY, newW, newH);
    }

    @Override
    public void endResizingFrame(JComponent c) {
        JInternalFrame f = (JInternalFrame) c;
        System.out.println("endResizingFrame " + f.getTitle());
        super.endResizingFrame(f);
    }

    @Override
    public void setBoundsForFrame(JComponent c, int newX, int newY, int newW, int newH) {
        JInternalFrame f = null;
        if (c instanceof JInternalFrame.JDesktopIcon) {
            JInternalFrame.JDesktopIcon icon = (JInternalFrame.JDesktopIcon) c;
            f = icon.getInternalFrame();
            System.out.println("setBoundsForFrame(icon) " + f.getTitle() + " " + newX + " " + newY + " " + newW + " " + newH);
        } else {
            f = (JInternalFrame) c;
            System.out.println("setBoundsForFrame " + f.getTitle() + " " + newX + " " + newY + " " + newW + " " + newH);
        }
        super.setBoundsForFrame(c, newX, newY, newW, newH);
    }
}
