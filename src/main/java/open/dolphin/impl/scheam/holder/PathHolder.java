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
package open.dolphin.impl.scheam.holder;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;

/**
 * PathHolder
 * 
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PathHolder implements DrawingHolder {
    
    private GeneralPath path;
    private Stroke stroke;
    private Paint paint;
    private AlphaComposite ac;
    private boolean fill = false;
    

    public PathHolder() {
    }
    
    public PathHolder(GeneralPath path, Stroke stroke, Paint paint, AlphaComposite ac) {
        this.path = path;
        this.stroke = stroke;
        this.paint = paint;
        this.ac = ac;
    }
    
    public Shape getPath() {
        return this.path;
    }

    public void setPath(GeneralPath path) {
        this.path = path;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint color) {
        this.paint = color;
    }

    public boolean isFill() {
        return fill;
    }

    public void setFill(boolean fill) {
        this.fill = fill;
    }

    public Stroke getStroke() {
        return stroke;
    }

    public void setStroke(Stroke stroke) {
        this.stroke = stroke;
    }
    
    public AlphaComposite getAlphaComposite() {
        return ac;
    }
    
    public void setAlphaComposite(AlphaComposite ac) {
        this.ac = ac;
    }
    
    @Override
    public boolean contains(Point p) {
        PathIterator iter = path.getPathIterator(null);
        double[] coords = new double[6];
        int seg;
        double x; double y;

        // segment の各点のうち，p と 5 ドット未満に近いものがあれば contains と判断する
        while(!iter.isDone()) {
            seg = iter.currentSegment(coords);
            if (seg == PathIterator.SEG_MOVETO || seg == PathIterator.SEG_LINETO) {
                x = coords[0];
                y = coords[1];
                if (-5 < (p.x - x) && (p.x - x) < 5 && -5 < (p.y - y) && (p.y - y) < 5) {
                    return true;
                }
            }
            iter.next();
        }
        return false;
    }
    
    @Override
    public void draw(Graphics2D g2d) {
                
        g2d.setComposite(ac);
        g2d.setPaint(paint);
        g2d.setStroke(stroke);

        g2d.draw(path);
    }
    
    @Override
    public void translate(double x, double y) {
        AffineTransform trans = AffineTransform.getTranslateInstance(x, y);
        path.transform(trans);
    }
    
    @Override
    public void rotate(double theta) {
        AffineTransform rotate = AffineTransform.getRotateInstance(theta);
        path.transform(rotate);
    }

    @Override
    public void expand(double sx, double sy) {
        // stroke の拡大
        BasicStroke s = (BasicStroke) stroke;
        float w = (float) (s.getLineWidth() * sx); // 常に sx = sy なので
        stroke = new BasicStroke( w, s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());

        // 形状の拡大
        AffineTransform expand = AffineTransform.getScaleInstance(sx, sy);
        path.transform(expand);
    }
}
