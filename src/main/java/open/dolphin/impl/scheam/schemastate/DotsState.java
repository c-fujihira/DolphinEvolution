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
package open.dolphin.impl.scheam.schemastate;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import open.dolphin.impl.scheam.SchemaEditorImpl;

/**
 * DotsState
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class DotsState extends AbstractState {

    // 塗りの texture
    public static final int DOTS_SPARSE = 1;
    public static final int DOTS_MEDIUM = 2;
    public static final int DOTS_DENSE = 3;
    public static final int NET_SPARSE = 4;
    public static final int NET_MEDIUM = 5;
    public static final int NET_DENSE = 6;

    private int density; // 選択範囲にいくつ点を打つか
    private int interval; // 領域の辺長当たり何本線を引くか

    public DotsState(SchemaEditorImpl context) {
        super(context);
    }

    public void setDots(int dots) {
        if (dots == DOTS_SPARSE) {
            density = 10;
        } else if (dots == DOTS_MEDIUM) {
            density = 20;
        } else if (dots == DOTS_DENSE) {
            density = 50;
        } else {
            density = 0;
        }

        if (dots == NET_SPARSE) {
            interval = 3;
        } else if (dots == NET_MEDIUM) {
            interval = 6;
        } else if (dots == NET_DENSE) {
            interval = 12;
        } else {
            interval = 0;
        }

        properties.setIsFill(false);
    }

    @Override
    public void mouseDown(Point p) {
        start = p;
        end = null;
        first = true;
        shape = new GeneralPath();
    }

    @Override
    public void mouseDragged(Point p) {
        end = p;
        if (first) {
            ((GeneralPath) shape).moveTo(start.x, start.y);
            ((GeneralPath) shape).lineTo(end.x, end.y);
            first = false;
        } else {
            ((GeneralPath) shape).lineTo(end.x, end.y);
        }
        canvas.repaint();
        start = end;
    }

    @Override
    public void mouseUp(Point p) {

        if (shape.getBounds().width != 0 && shape.getBounds().height != 0) {

            undoMgr.storeDraw();
            ((GeneralPath) shape).closePath();

            if (density != 0) {
                addPathShape(getRandomDotsPath());
            } else {
                addPathShape(getReticularPath());
            }

        }
        // null にしておかないと undo の時などに描画されてしまう
        shape = null;
        canvas.repaint();
    }

    /**
     * 網状に線を引いた　GeneralPath を作る
     *
     * @return
     */
    private GeneralPath getReticularPath() {
        GeneralPath netShape = new GeneralPath();
        Rectangle r = shape.getBounds();

        int dx = ((r.width + r.height) / 2) / interval;
        if (dx == 0) {
            return null;
        }

        int gap = 0;
        for (int y = r.y; y < r.y + r.height; y++) {
            for (int x = r.x; x < r.x + r.width + dx; x += dx) {
                int px = x + (gap % dx);
                if (shape.contains(px, y)) {
                    netShape.moveTo(px, y);
                    netShape.lineTo(px, y);
                }
                px = x - (gap % dx);
                if (shape.contains(px, y)) {
                    netShape.moveTo(px, y);
                    netShape.lineTo(px, y);
                }
            }
            gap++;
        }

        return netShape;
    }

    /**
     * ランダムにドットをうった GeneralPath を作る
     *
     * @return
     */
    private GeneralPath getRandomDotsPath() {

        GeneralPath dotsShape = new GeneralPath();

        // shape の外側を覆う Rectangle の中でランダムドットを作成
        Rectangle r = shape.getBounds();
        java.util.Random rnd = new java.util.Random();

        // 作った点の座標を保持する
        ArrayList<Point> point = new ArrayList<Point>();

        int maxDistance = Math.min(r.width, r.height);
        // int dotsNumber = r.width * r.height * density/10000;
        int dotsNumber = density;

        // ランダムな点を，できるだけ既存の点から遠くなるように打つ
        while (point.size() < dotsNumber) {
            boolean done = false;
            int d;
            for (d = maxDistance; d > 0; d--) {
                // 10回ためして見つからなかったら d を下げる
                for (int i = 0; i < 10; i++) {
                    double x = r.x + r.width * rnd.nextFloat();
                    double y = r.y + r.height * rnd.nextFloat();

                    if (shape.contains(x, y) && getMinimumDistance(point, x, y) >= d) {
                        point.add(new Point((int) x, (int) y));
                        maxDistance = d;
                        done = true;
                        break;
                    }
                }
                if (done) {
                    break;
                }
            }
            // d <= 0 ならもう点を打つ場所はない
            if (d <= 0) {
                break;
            }
        }
        // dot をうつ
        for (Point pt : point) {
            dotsShape.moveTo(pt.x, pt.y);
            dotsShape.lineTo(pt.x, pt.y);
        }
        return dotsShape;
    }

    /**
     * list の各点と (x,y) との距離を調べて，最小の距離を返す
     *
     * @param list
     * @param x
     * @param y
     * @return
     */
    private int getMinimumDistance(ArrayList<Point> list, double x, double y) {
        double min2 = Double.MAX_VALUE;
        double dist2 = 0;
        double dx = 0;
        double dy = 0;

        for (Point p : list) {
            dx = p.x - x;
            dy = p.y - y;
            dist2 = dx * dx + dy * dy;
            min2 = (int) Math.min(min2, dist2);
        }
        return (int) Math.sqrt(min2);
    }

    @Override
    public void draw(Graphics2D g2d) {

        if (shape == null) {
            return;
        }

        g2d.setStroke(properties.getOutlineStroke());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        g2d.setPaint(Color.gray);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.draw(shape);
    }

}
