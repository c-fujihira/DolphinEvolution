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
package open.dolphin.impl.schema;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Line2DHolder
 *
 * @author Minagawa, Kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class Line2DHolder implements DrawingHolder {

    private final Line2D.Double line2D;
    private final Stroke stroke;
    private final Paint paint;
    private final AlphaComposite ac;
    private boolean p1;
    private boolean p2;

    public Line2DHolder(Line2D.Double line2D, Stroke stroke, Paint paint, AlphaComposite ac) {
        this.line2D = line2D;
        this.stroke = stroke;
        this.paint = paint;
        this.ac = ac;
    }

    @Override
    public boolean contains(Point p) {

        p1 = false;
        p2 = false;

        Rectangle2D r = getRectangle2D(p);

        if (isAtP1(r)) {
            p1 = true;
            return true;
        }

        if (isAtP2(r)) {
            p2 = true;
            return true;
        }
        return line2D.intersects(r);
    }

    @Override
    public void draw(Graphics2D g2d) {

        Stroke saveStroke = g2d.getStroke();
        Paint savePaint = g2d.getPaint();
        Composite saveComposite = g2d.getComposite();

        g2d.setStroke(stroke);
        g2d.setPaint(paint);
        g2d.setComposite(ac);
        g2d.draw(line2D);

        g2d.setStroke(saveStroke);
        g2d.setPaint(savePaint);
        g2d.setComposite(saveComposite);
    }

    @Override
    public void translate(double x, double y) {

        double x1 = line2D.getX1();
        double y1 = line2D.getY1();
        double x2 = line2D.getX2();
        double y2 = line2D.getY2();

        if (p1) {
            x1 = line2D.getX1() + x;
            y1 = line2D.getY1() + y;
        } else if (p2) {
            x2 = line2D.getX2() + x;
            y2 = line2D.getY2() + y;
        } else {
            x1 = line2D.getX1() + x;
            y1 = line2D.getY1() + y;
            x2 = line2D.getX2() + x;
            y2 = line2D.getY2() + y;
        }

        line2D.setLine(x1, y1, x2, y2);
    }

    private boolean isAtP1(Rectangle2D r) {
        return r.contains(line2D.getP1());
    }

    private boolean isAtP2(Rectangle2D r) {
        return r.contains(line2D.getP2());
    }

    private Rectangle2D getRectangle2D(Point p) {
        Rectangle2D r = new Rectangle2D.Double();
        r.setFrameFromDiagonal(p.getX() - 3, p.getY() - 3, p.getX() + 3, p.getY() + 3);
        return r;
    }
}
