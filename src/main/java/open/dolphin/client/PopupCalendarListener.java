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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import open.dolphin.infomodel.SimpleDate;

/**
 * PopupCalendarListener
 * (予定カルテ対応)
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified by masuda, Masuda Naika
 */
public class PopupCalendarListener extends MouseAdapter implements PropertyChangeListener {

    private static final int[] defaultRange = {-12, 0};
    private JPopupMenu popup;
    private int[] range;
//minagawa^ 予定カルテ    
    private SimpleDate[] acceptRange;
//minagawa$    
    protected JTextField tf;
    
    public PopupCalendarListener(JTextField tf) {
        this(tf, defaultRange);
    }
    
    public PopupCalendarListener(JTextField tf, int[] range) {
        this.tf = tf;
        this.range = range;
        tf.addMouseListener(PopupCalendarListener.this);
    }
    
//minagawa^ 予定カルテ    
    public PopupCalendarListener(JTextField tf, int[] range, SimpleDate[] acceptRange) {
        this.tf = tf;
        this.range = range;
        this.acceptRange = acceptRange;
        tf.addMouseListener(PopupCalendarListener.this);
    }
//minagawa$    
    
    public void setValue(SimpleDate sd) {
        tf.setText(SimpleDate.simpleDateToMmldate(sd));
    }

    @Override
    public final void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public final void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {
            popup = new JPopupMenu();
            CalendarCardPanel cc = new CalendarCardPanel(ClientContext.getEventColorTable());
            cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
            cc.setCalendarRange(range);
//minagawa^ 予定カルテ            
            cc.setAcceptRange(acceptRange);
//minagawa$            
            popup.insert(cc, 0);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    @Override
    public final void propertyChange(PropertyChangeEvent e) {
        if (e.getPropertyName().equals(CalendarCardPanel.PICKED_DATE)) {
            SimpleDate sd = (SimpleDate)e.getNewValue();
            setValue(sd);
            popup.setVisible(false);
            popup = null;
        }
    }
}
