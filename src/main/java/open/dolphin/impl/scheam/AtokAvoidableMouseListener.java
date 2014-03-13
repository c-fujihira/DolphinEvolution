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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * ATOK 2011 On でショートカットでツールを選んだあと 最初の mousePressed が ATOK に取られて無視されるのの
 * workaround
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class AtokAvoidableMouseListener implements MouseListener, MouseMotionListener {

    private final StateMgr stateMgr;
    private boolean pressed = false;

    public AtokAvoidableMouseListener(StateMgr stateMgr) {
        this.stateMgr = stateMgr;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        stateMgr.setMouseEvent(e);
        stateMgr.mouseClicked(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        pressed = true;
        stateMgr.setMouseEvent(e);
        stateMgr.mouseDown(e.getPoint());
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // mousePressed されないで released された
        if (!pressed) {
            mousePressed(e);
            mouseClicked(e);
        }
        pressed = false;
        stateMgr.setMouseEvent(e);
        stateMgr.mouseUp(e.getPoint());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // mousePressed されないで drag された
        if (!pressed) {
            mousePressed(e);
        }
        stateMgr.mouseDragged(e.getPoint());
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
