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
package open.dolphin.utilities.main;

import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import open.dolphin.utilities.control.AccordionPanel;

/**
 * メインクラス
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class App {

    App(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        java.awt.GraphicsEnvironment env = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
        java.awt.Rectangle deskBounds = env.getMaximumWindowBounds();
        frame.setBounds((deskBounds.width - 400) / 2, (deskBounds.height - 200) / 2, 400, 200);
        frame.setTitle("lutilities-1.0.jar");

        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append("Name : lutilities").append("<br>");
        sb.append("Version : 1.0").append("<br>");
        sb.append("License : Copyright (C) Life Sciences Computing Corporation.").append("<br>");
        sb.append("[Function]").append("<br>");
        sb.append("&nbsp;+ XML read write").append("<br>");
        sb.append("&nbsp;+ HTTP connection").append("<br>");
        sb.append("&nbsp;+ Dicom library").append("<br>");
        sb.append("&nbsp;+ FCR link").append("<br>");
        sb.append("&nbsp;+ Clipboard").append("<br>");
        sb.append("</html>");
        JLabel label = new JLabel(sb.toString());
        panel.add(label);
        //frame.add(label);
        AccordionPanel panel2 = new AccordionPanel();
        panel2.addComponent("Test", new JPanel());
        frame.add(panel2);

        frame.setVisible(true);
    }

    public static void main(String[] args) {
        App app = new App(args);
    }
}
