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
package open.dolphin.utilities.control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.*;

/**
 * ツールチップ拡張クラス
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class ToolTipEx extends JToolTip {

    private Color foreColor;
    private Color backColor;

    /**
     * コンストラクタ
     *
     * @param fore 文字色
     * @param back 背景色
     */
    public ToolTipEx(Color fore, Color back) {
        super();
        foreColor = fore;
        backColor = back;
    }

    /**
     * 画像ツールチップの作成
     *
     * @param icon アイコン
     * @return ツールチップ
     */
    public JToolTip CreateImageToolTip(ImageIcon icon) {
        final JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        LookAndFeel.installColorsAndFont(iconLabel, "ToolTip.background", "ToolTip.foreground", "ToolTip.font");
        JToolTip tooltip = new JToolTip() {
            @Override
            public Dimension getPreferredSize() {
                return getLayout().preferredLayoutSize(this);
            }

            @Override
            public void setTipText(final String tipText) {
                String oldValue = iconLabel.getText();
                iconLabel.setText(tipText);
                firePropertyChange("tiptext", oldValue, tipText);
            }
        };
        tooltip.setComponent(this);
        tooltip.setLayout(new BorderLayout());
        tooltip.add(iconLabel);
        tooltip.setForeground(foreColor);
        tooltip.setBackground(backColor);
        return tooltip;
    }

    /**
     * 描画
     *
     * @param g グラフィック
     */
    @Override
    public void paint(Graphics g) {
        setForeground(foreColor);
        setBackground(backColor);
        super.paint(g);
    }

    public static void main(String[] args) {
        //JFrame frame = new JFrame();
        //JScrollPane pane = new JScrollPane(new JTable() {
        //    @Override
        //    public JToolTip createToolTip() {
        //        if(アイコン) {
        //            ImageIconEx icon = new ImageIconEx();
        //            ToolTipEx tool = new ToolTipEx(Color.BLACK, Color.WHITE);
        //            return tool.CreateImageToolTip(icon.getIcon());
        //        }else if(テキスト) {
        //            return new ToolTipEx(Color.BLACK, Color.WHITE);
        //        }
        //        return null;
        //    }
        //
        //    @Override
        //    public String getToolTipText(MouseEvent e) {
        //        String ret = null;
        //        if(テキスト) {
        //            Object obj = this.getModel().getValueAt(rowAtPoint(e.getPoint()), columnAtPoint(e.getPoint()));
        //            if(obj instanceof ImageIconEx) {
        //                ImageIconEx icon = (ImageIconEx)this.getModel().getValueAt(rowAtPoint(e.getPoint()), columnAtPoint(e.getPoint()));
        //                ret = icon.getText();
        //            }else{
        //                ret = (String)this.getModel().getValueAt(rowAtPoint(e.getPoint()), columnAtPoint(e.getPoint()));
        //                if(ret.isEmpty()) return null;
        //            }
        //        }else if(アイコン) {
        //            ret = "";
        //        }
        //        return  ret;
        //    }
        //});
    }
}
