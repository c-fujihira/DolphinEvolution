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
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import open.dolphin.impl.scheam.SchemaEditorImpl;
import open.dolphin.impl.scheam.holder.AreaHolder;
import open.dolphin.impl.scheam.holder.PathHolder;

/**
 * EraserState
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class EraserState extends AbstractState {

    private GeneralPath gpath;
    private Stroke eraserStroke = properties.getEraserStroke();

    public EraserState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {
        start = p;
        end = null;
        first = true;
        gpath = null;
    }

    @Override
    public void mouseDragged(Point p) {
        end = p;
        if (first) {
            gpath = new GeneralPath();
            gpath.moveTo(start.x, start.y);
            gpath.lineTo(end.x, end.y);
            first = false;
        } else {
            gpath.lineTo(end.x, end.y);
        }
        canvas.repaint();
        start = end;
    }

    @Override
    public void mouseUp(Point p) {
        undoMgr.storeDraw();

        end = p;
        // ドラッグした場合
        if (gpath != null) {
            PathHolder sh = new PathHolder(new GeneralPath(gpath), eraserStroke, Color.WHITE, AlphaComposite.SrcOver);
            addShape(sh);
            gpath = null;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // クリックの場合
        Ellipse2D whiteSpot = new Ellipse2D.Double();
        whiteSpot.setFrameFromCenter(start.x, start.y, start.x + 8.0f, start.y + 8.0f);
        AreaHolder sh = new AreaHolder(new Area(whiteSpot), eraserStroke, Color.WHITE, AlphaComposite.SrcOver, true);
        addShape(sh);
        canvas.repaint();
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (gpath == null) {
            return;
        }

        g2d.setStroke(eraserStroke);
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setPaint(Color.WHITE);

        g2d.draw(gpath);
    }
}
