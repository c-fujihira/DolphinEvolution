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
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import open.dolphin.impl.scheam.SchemaCanvas;
import open.dolphin.impl.scheam.SchemaEditorImpl;
import open.dolphin.impl.scheam.SchemaEditorProperties;
import open.dolphin.impl.scheam.UndoMgr;
import open.dolphin.impl.scheam.holder.*;

/**
 * stateMgr から指定された state に応じて描画する
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public abstract class AbstractState {

    protected SchemaEditorImpl context;
    protected SchemaEditorProperties properties;
    protected SchemaCanvas canvas;
    protected ArrayList<DrawingHolder> drawingList;
    protected UndoMgr undoMgr;

    protected Shape shape;
    protected boolean first;
    protected Point start;
    protected Point end;

    public AbstractState() {
    }

    public AbstractState(SchemaEditorImpl context) {
        this();
        this.context = context;
        this.canvas = context.getCanvas();
        this.drawingList = context.getDrawingList();
        this.properties = context.getProperties();
        this.undoMgr = context.getUndoMgr();
    }

    public abstract void mouseDown(Point p);

    public abstract void mouseDragged(Point p);

    // mouseUp で必ず shape = null すること！
    public abstract void mouseUp(Point p);

    // 必要に応じて override する
    public void mouseClicked(MouseEvent e) {
    }

    public void setMouseEvent(MouseEvent e) {
    }

    /**
     * mouseDown から mouseUp まで，drag されている間の途中経過を描く SchemaCanvas の paintComponent
     * から StateMgr 経由で呼ばれる
     */
    public void draw(Graphics2D g2d) {
        if (shape != null) {
            if (properties.isFill()) {
                g2d.fill(shape);
            } else {
                g2d.draw(shape);
            }
        }
    }

    /**
     * DrawingHolder を drawingList に加える
     *
     * @param DrawingHolder s
     */
    public void addShape(DrawingHolder s) {
        drawingList.add(s);
    }

    /**
     * Shape を AreaHolder に入れて drawingList に加える
     *
     * @param Shape shape
     */
    public void addAreaShape(Shape shape) {
        AreaHolder sh = new AreaHolder(new Area(shape),
                properties.getStroke(),
                properties.getFillColor(),
                properties.getAlphaComposite(),
                properties.isFill());
        addShape(sh);
    }

    /**
     * Shape を Line2DHolder に入れて drawingList に加える
     *
     * @param shape
     */
    public void addLineShape(Line2D.Double shape) {
        Line2DHolder sh = new Line2DHolder(shape,
                properties.getStroke(),
                properties.getFillColor(),
                properties.getAlphaComposite());
        addShape(sh);
    }

    /**
     * Shape を PathHolder に入れて drawingList に加える
     *
     * @param shape
     */
    public void addPathShape(Shape shape) {
        PathHolder sh = new PathHolder(new GeneralPath(shape),
                properties.getStroke(),
                properties.getFillColor(),
                properties.getAlphaComposite());
        addShape(sh);
    }

    /**
     * Shape を TextHolder に入れて drawingList に加える
     *
     * @param shape
     */
    public void addTextShape(Shape shape) {
        AreaHolder sh = new TextHolder(new Area(shape),
                properties.getTextStroke(),
                properties.getTextColor(),
                properties.getTextComposite(),
                true);
        addShape(sh);
    }

    /**
     * 最後に追加した絵を list から除去する
     */
    public void removeLastShape() {
        if (!drawingList.isEmpty()) {
            drawingList.remove(drawingList.size() - 1);
        }
    }
}
