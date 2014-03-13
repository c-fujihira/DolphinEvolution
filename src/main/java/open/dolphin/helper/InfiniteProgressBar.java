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

import java.awt.*;
import javax.swing.*;
import open.dolphin.client.ClientContext;

/**
 * InfiniteProgressBar
 *
 * @author Kazushi Minagawa.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class InfiniteProgressBar {

    private final InfiniteProgrressBarView view;
    private final JDialog dialog;

    public InfiniteProgressBar(String title, String msg, Component cmp) {
        view = new InfiniteProgrressBarView();
        view.getMsgLbl().setText(msg);
        view.getCancelBtn().setText((String) UIManager.get("OptionPane.cancelButtonText"));
        view.getCancelBtn().setEnabled(false);
        view.setOpaque(true);

        Frame frame = null;
        if (cmp != null) {
            Window w = SwingUtilities.getWindowAncestor(cmp);
            frame = (Frame) w;
            dialog = new JDialog(frame, ClientContext.getFrameTitle(title), false);
        } else {
            dialog = new JDialog(new JFrame(), ClientContext.getFrameTitle(title), false);
        }
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setAlwaysOnTop(true);
        dialog.setResizable(false);
        dialog.setContentPane(view);
        dialog.pack();

        int x, y;
        if (frame != null) {
            x = (frame.getSize().width - dialog.getSize().width) / 2;
            y = (frame.getSize().height - dialog.getSize().height) / 2;
            x += frame.getLocation().x;
            y += frame.getLocation().y;
        } else {
            int n = ClientContext.isMac() ? 3 : 2;
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            x = (screenSize.width - dialog.getPreferredSize().width) / 2;
            y = (screenSize.height - dialog.getPreferredSize().height) / n;
        }
        dialog.setLocation(x, y);
    }

    public void start() {
        view.getProgressBar().setIndeterminate(true);
        dialog.setVisible(true);
    }

    public void stop() {
        view.getProgressBar().setIndeterminate(false);
        view.getProgressBar().setValue(0);
        dialog.setVisible(false);
        dialog.dispose();
    }
}
