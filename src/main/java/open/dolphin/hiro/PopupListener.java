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
package open.dolphin.hiro;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import open.dolphin.client.CalendarCardPanel;
import open.dolphin.client.ClientContext;
import open.dolphin.infomodel.SimpleDate;

/* Created 2010/07/02 */
/**
 * テキストフィールドへ日付を入力するためのカレンダーポップアップメニュークラス
 *
 * @author Masato
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PopupListener extends MouseAdapter implements PropertyChangeListener {

    /**
     * ポップアップメニュー
     */
    private JPopupMenu popup;
    /**
     * ターゲットのテキストフィールド
     */
    private final JTextField tf;
    /**
     * カレンダーの表示期間 開始月
     */
    int start;
    /**
     * カレンダーの表示期間 最終月
     */
    int end;

    /**
     * コンストラクタ
     *
     * @param tf
     */
    public PopupListener(JTextField tf) {
        this.tf = tf;
        tf.addMouseListener(this);
        this.start = -6;
        this.end = 6;
    }

    /**
     * コンストラクタ
     *
     * @param tf
     * @param start 表示期間開始月
     * @param end 表示期間最終月
     */
    public PopupListener(JTextField tf, int start, int end) {
        this.tf = tf;
        tf.addMouseListener(this);
        this.start = start;
        this.end = end;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        maybeShowPopup(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        maybeShowPopup(e);
    }

    /**
     * カレンダーをポップアップ表示する。
     *
     * @param e MouseEvent
     */
    private void maybeShowPopup(MouseEvent e) {

        if (e.isPopupTrigger()) {
            popup = new JPopupMenu();
            CalendarCardPanel cc = new CalendarCardPanel(ClientContext.getEventColorTable());
            cc.addPropertyChangeListener(CalendarCardPanel.PICKED_DATE, this);
            cc.setCalendarRange(new int[]{start, end});
            popup.insert(cc, 0);
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * テキストフィールドにカレンダーの値を設定し、カレンダーを閉じる。
     *
     * @param e PropertyChangeEvent
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        if (CalendarCardPanel.PICKED_DATE.equals(e.getPropertyName())) {
            SimpleDate sd = (SimpleDate) e.getNewValue();
            tf.setText(SimpleDate.simpleDateToMmldate(sd));
            popup.setVisible(false);
            popup = null;
        }
    }

    /**
     * テキストフィールドを返す。
     *
     * @return JTextField
     */
    public JTextField getTextField() {
        return tf;
    }
}
