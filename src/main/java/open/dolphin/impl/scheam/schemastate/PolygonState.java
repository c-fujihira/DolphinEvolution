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

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import open.dolphin.impl.scheam.SchemaEditorImpl;

/**
 * PolygonState
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PolygonState extends AbstractState {

    private final Stroke outlineStroke = properties.getOutlineStroke();

    public PolygonState(SchemaEditorImpl context) {
        super(context);
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
            addAreaShape((GeneralPath) shape);
        }
        // null にしておかないと undo の時などに描画されてしまう
        shape = null;
        canvas.repaint();
    }

    /**
     * polygon の時は，途中経過も線の方がわかりやすい
     *
     * @param g2d
     */
    @Override
    public void draw(Graphics2D g2d) {

        if (shape != null) {
            if (properties.isFill()) {
                g2d.setStroke(outlineStroke);
            }
            g2d.draw(shape);
        }
    }
}
