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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 * Core Java Foundation Class by Kim topley.
 */
public class ColorFillIcon implements Icon {

    /** Creates new ColorFillIcon */
    public ColorFillIcon(Color fill, int width, int height, int borderSize) {
        super();
        
        this.fillColor = fill;
        this.width = width;
        this.height = height;
        this.borderSize = borderSize;
        this.shadow = Color.black;
        this.fillWidth = width - 2 * borderSize;
        this.fillHeight = height - 2 * borderSize;
    }
    
    public ColorFillIcon(Color fill, int size) {
        this(fill, size, size, BORDER_SIZE);
    }
    
    public ColorFillIcon(Color fill) {
        this(fill, DEFAULT_SIZE, DEFAULT_SIZE, BORDER_SIZE);
    }
    
    public void setShadow(Color c) {
        shadow = c;
    }
    
    public void setFillColor(Color c) {
        fillColor = c;
    }
    
    @Override
    public int getIconWidth() {
        return width;
    }
    
    @Override
    public int getIconHeight() {
        return height;
    }
    
    @Override
    public void paintIcon(Component comp, Graphics g, int x, int y) {
        Color c = g.getColor();
        
        if(borderSize > 0) {
            g.setColor(shadow);
            for (int i = 0; i < borderSize; i++) {
                g.drawRect(x + i, y + i,
                           width - 2 * i - 1, height - 2 * i -1);
            }
        }
        
        g.setColor(fillColor);
        g.fillRect(x + borderSize, y + borderSize, fillWidth, fillHeight);
        g.setColor(c);
    }
    
    protected int width;
    protected int height;
    protected Color fillColor;
    protected Color shadow;
    protected int borderSize;
    protected int fillHeight;
    protected int fillWidth;
    
    public static final int BORDER_SIZE = 2;
    public static final int DEFAULT_SIZE = 32;

}
