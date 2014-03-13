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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JComponent;

/**
 * ColorChooserLabel
 *
 * @author Minagawa,Kazushi
 */
public class ColorChooserComp extends JComponent implements MouseListener, MouseMotionListener {
    
    public static final String SELECTED_COLOR = "selectedColor";
    
    private Color[] colors;
    
    private Color[] colorStart;
    
    private Dimension size;
    
    private Color strokeColor = Color.DARK_GRAY;
    
    private int strokeWidth = 2;
    
    private PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);
    
    private Color selected;
    
    private int index = -1;
    
    /**
     * Creates a new progress panel with default values
     */
    public ColorChooserComp() {
        colorStart = ClientContext.getColorArray("color.set.default.start");
        colors = ClientContext.getColorArray("color.set.default.end");
        size = ClientContext.getDimension("colorCooserComp.default.size");
        strokeWidth = ClientContext.getInt("colorChooserComp.stroke.width");
        this.setPreferredSize(new Dimension(2*size.width*colors.length + size.width, 2*size.height));
        this.addMouseListener(ColorChooserComp.this);
        this.addMouseMotionListener(ColorChooserComp.this);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    /**
     * Creates a new progress panel with default values
     */
    public ColorChooserComp(Dimension size, Color[] colors) {
        this.size = size;
        this.colors = colors;
        this.setPreferredSize(new Dimension(2*size.width*colors.length + size.width, 2*size.height));
        this.addMouseListener(ColorChooserComp.this);
        this.addMouseMotionListener(ColorChooserComp.this);
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
    
    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(prop, l);
    }
    
    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(prop, l);
    }
    
    public Color getSelectedColor() {
        return selected;
    }
    
    public void setSelectedColor(Color selected) {
        this.selected = selected;
        boundSupport.firePropertyChange(SELECTED_COLOR, null, this.selected);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX() / size.width;
        int mod = x % 2;
        if (mod != 0) {
            index = x / 2;
        }
        repaint();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        int x = e.getX() / size.width;
        int mod = x % 2;
        if (mod != 0) {
            index = x / 2;
        }
        repaint();
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        index = -1;
        repaint();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX() / size.width;
        int mod = x % 2;
        if (mod != 0) {
            index = x / 2;
        }
        if (index >= 0 && index < colors.length) {
            setSelectedColor(colors[index]);
        }
    }
    
    @Override
    public void paintComponent(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g;
        
        double dx = size.getWidth()*2;
        double offsetX = size.getWidth();
        double offsetY = (this.getPreferredSize().getHeight() - size.getHeight())/2;
        
        BasicStroke stroke = new BasicStroke(strokeWidth);
        
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(rh);
        
        for (int i=0; i < colors.length; i++) {
            double x = offsetX + i * dx;
            double y = offsetY;
            Ellipse2D.Double body = new Ellipse2D.Double(x, y, size.getWidth(), size.getHeight());
            GradientPaint lightToDark = new GradientPaint((int)x, (int)y, colorStart[i], (int)x + size.width, (int)y + size.height, colors[i]);
            g2.setPaint(lightToDark);
            g2.fill(body);
            if (i == index) {
                g2.setColor(strokeColor);
                g2.setStroke(stroke);
                g2.draw(body);
                //g2.setColor(colors[i]);
                //g2.fill(body);
                //GradientPaint lightToDark = new GradientPaint((int)x, (int)y, Color.LIGHT_GRAY, (int)x, (int)y + size.height, colors[i]);
                //g2.setPaint(lightToDark);
                //g2.fill(body);
                //g2.setColor(Color.DARK_GRAY);
                //g2.setStroke(new BasicStroke(2));
                //g2.draw(body);
            } //else {
            //g2.setColor(colors[i]);
            //g2.fill(body);
            //}
        }
    }
}