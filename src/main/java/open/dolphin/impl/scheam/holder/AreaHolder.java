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
import java.awt.geom.Area;
import open.dolphin.impl.scheam.schemahelper.SchemaUtils;

/**
 * AreaHolder
 *
 * @author kazm
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class AreaHolder implements DrawingHolder {

    private Area area;
    private Stroke stroke;
    private Paint paint;
    private AlphaComposite ac;
    private boolean fill;

    public AreaHolder() {
    }

    public AreaHolder(Area area, Stroke stroke, Paint paint, AlphaComposite ac, boolean fill) {
        this.area = area;
        this.stroke = stroke;
        this.paint = paint;
        this.ac = ac;
        this.fill = fill;
    }

    public Shape getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
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
        // 細すぎるとつかめないので，近くだったらつかめるようにする
        if (area.getBounds().width < 5 || area.getBounds().height < 5) {
            return SchemaUtils.isNear(area, p, 5);
        } else {
            return area.contains(p);
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setStroke(stroke);
        g2d.setComposite(ac);
        g2d.setPaint(paint);

        if (fill) {
            g2d.fill(area);
        } else {
            g2d.draw(area);
        }
    }

    @Override
    public void translate(double x, double y) {
        AffineTransform trans = AffineTransform.getTranslateInstance(x, y);
        area.transform(trans);
    }

    @Override
    public void rotate(double theta) {
        AffineTransform rotate = AffineTransform.getRotateInstance(theta);
        area.transform(rotate);
    }

    @Override
    public void expand(double sx, double sy) {
        // stroke の拡大
        BasicStroke s = (BasicStroke) stroke;
        float w = (float) (s.getLineWidth() * sx); // 常に sx = sy なので
        stroke = new BasicStroke(w, s.getEndCap(), s.getLineJoin(), s.getMiterLimit(), s.getDashArray(), s.getDashPhase());

        // 形状の拡大
        AffineTransform expand = AffineTransform.getScaleInstance(sx, sy);
        area.transform(expand);
    }
}
