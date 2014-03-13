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

import java.awt.*;
import java.beans.PropertyChangeEvent;
import javax.swing.*;

/**
 * Chart plugin で共通に利用するステータスパネル。
 *
 * @author  Kazushi Minagawa
 */
public class StatusPanel extends JPanel implements IStatusPanel {
    
    private static final int DEFAULT_HEIGHT = 23;
    
    private JLabel messageLable;
    private JProgressBar progressBar;
    private JLabel leftLabel;
    private JLabel rightLabel;
    private JLabel timelabel;
    private boolean useTime = true;
    
    public StatusPanel() {
        this(true);
    }
    
    /**
     * Creates a new instance of StatusPanel
     */
    public StatusPanel(boolean useTime) {
        
        this.useTime = useTime;
        
        messageLable = new JLabel("");

        progressBar = new JProgressBar();
        progressBar.setPreferredSize(new Dimension(100, 14));
        progressBar.setMinimumSize(new Dimension(100, 14));
        progressBar.setMaximumSize(new Dimension(100, 14));

        leftLabel = new JLabel("");

        rightLabel = new JLabel("");
        
        Font font = GUIFactory.createSmallFont();
        leftLabel.setFont(font);
        rightLabel.setFont(font);
        
        leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        if (useTime) {
            timelabel = new JLabel("経過時間: 00 秒");
            timelabel.setFont(font);
            timelabel.setHorizontalAlignment(SwingConstants.CENTER);
        }
        
        JPanel info = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        info.add(progressBar);
        info.add(Box.createHorizontalStrut(3));
        info.add(leftLabel);
        info.add(new SeparatorPanel());
        info.add(rightLabel);
        if (useTime) {
            info.add(new SeparatorPanel());
            info.add(timelabel);
        }
        info.add(Box.createHorizontalStrut(11));
        this.setLayout(new BorderLayout());
        this.add(info, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(getWidth(), DEFAULT_HEIGHT));
    }
    
    @Override
    public void setMessage(String msg) {
        messageLable.setText(msg);
    }
    
    private BlockGlass getBlock() {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null && window instanceof JFrame) {
            JFrame frame = (JFrame) window;
            Component cmp = frame.getGlassPane();
            if (cmp != null && cmp instanceof BlockGlass) {
                return (BlockGlass) cmp;
            }
        }
        return null;
    }
    
    private void start() {
        BlockGlass glass = getBlock();
        if (glass != null) {
            glass.block();
        }
        progressBar.setIndeterminate(true);
    }
    
    private void stop() {
        BlockGlass glass = getBlock();
        if (glass != null) {
            glass.unblock();
        }
        progressBar.setIndeterminate(false);
        progressBar.setValue(0);
    }
    
    @Override
    public void setRightInfo(String info) {
        rightLabel.setText(info);
    }
    
    @Override
    public void setLeftInfo(String info) {
        leftLabel.setText(info);
    }
    
    @Override
    public void setTimeInfo(long time) {
        if (useTime) {
            StringBuilder sb = new StringBuilder();
            sb.append("経過時間: ");
            sb.append(time);
            sb.append(" 秒");
            timelabel.setText(sb.toString());
        }
    }
    
    @Override
    public JProgressBar getProgressBar() {
        return progressBar;
    }
        
    @Override
    public void propertyChange(PropertyChangeEvent e) {
                
        String propertyName = e.getPropertyName();

        if ("started".equals(propertyName)) {
            this.start();

        } else if ("done".equals(propertyName)) {
            this.stop();
        }
    }
}

