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
package open.dolphin.impl.scheam;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import open.dolphin.impl.scheam.holder.DrawingHolder;
import open.dolphin.impl.scheam.schemastate.*;

/**
 * StateMgr
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class StateMgr {

    private final SchemaEditorImpl context;
    private final SchemaEditorProperties properties;
    private final ArrayList<DrawingHolder> drawingList;
    private final UndoMgr undoMgr;

    private final AbstractState selectState;
    private final AbstractState lineState;
    private final AbstractState rectState;
    private final AbstractState ellipseState;
    private final AbstractState polygonState;
    private final AbstractState pencilState;
    private final AbstractState eraserState;
    private final AbstractState textState;
    private final RotationState rotationState;
    private AbstractState curState;
    private final AbstractState clippingState;

    private final DotsState dotsState;
    private final ExpandState expandState;

    public StateMgr(SchemaEditorImpl context) {
        this.context = context;
        this.drawingList = context.getDrawingList();
        this.properties = context.getProperties();
        this.undoMgr = context.getUndoMgr();

        selectState = new SelectState(context);
        lineState = new LineState(context);
        rectState = new RectState(context);
        ellipseState = new EllipseState(context);
        polygonState = new PolygonState(context);
        pencilState = new PencilState(context);
        eraserState = new EraserState(context);
        textState = new TextState(context);

        rotationState = new RotationState(context);
        expandState = new ExpandState(context);
        clippingState = new ClippingState(context);
        dotsState = new DotsState(context);

        curState = rectState;
    }

    // ツールボタンから呼ばれる methods
    public void startSelect() {
        curState = selectState;
    }

    public void startLine() {
        curState = lineState;
        properties.setIsFill(false);
    }

    public void startRect() {
        curState = rectState;
        properties.setIsFill(false);
    }

    public void startEllipse() {
        curState = ellipseState;
        properties.setIsFill(false);
    }

    public void startPolygon() {
        curState = polygonState;
        properties.setIsFill(false);
    }

    public void startRectFill() {
        curState = rectState;
        properties.setIsFill(true);
    }

    public void startEllipseFill() {
        curState = ellipseState;
        properties.setIsFill(true);
    }

    public void startPolygonFill() {
        curState = polygonState;
        properties.setIsFill(true);
    }

    public void startPencil() {
        curState = pencilState;
        properties.setIsFill(false);
    }

    public void startEraser() {
        curState = eraserState;
        properties.setIsFill(false);
    }

    public void startText() {
        curState = textState;
        properties.setIsFill(false);
    }

    public void startClipping() {
        curState = clippingState;
    }

    public void startNetSparse() {
        curState = dotsState;
        dotsState.setDots(DotsState.NET_SPARSE);
    }

    public void startNetMedium() {
        curState = dotsState;
        dotsState.setDots(DotsState.NET_MEDIUM);
    }

    public void startNetDense() {
        curState = dotsState;
        dotsState.setDots(DotsState.NET_DENSE);
    }

    public void startDotsSparse() {
        curState = dotsState;
        dotsState.setDots(DotsState.DOTS_SPARSE);
    }

    public void startDotsMedium() {
        curState = dotsState;
        dotsState.setDots(DotsState.DOTS_MEDIUM);
    }

    public void startDotsDense() {
        curState = dotsState;
        dotsState.setDots(DotsState.DOTS_DENSE);
    }

    // mouseListener から呼ばれる methods
    public void mouseDown(Point p) {
        curState.mouseDown(p);
    }

    public void mouseDragged(Point p) {
        curState.mouseDragged(p);
    }

    public void mouseUp(Point p) {
        curState.mouseUp(p);
    }

    public void mouseClicked(MouseEvent e) {
        curState.mouseClicked(e);
    }

    public void setMouseEvent(MouseEvent e) {
        curState.setMouseEvent(e);
    }

    /**
     * SchemaCanvas から呼ばれる
     *
     * @param g2d
     */
    public void draw(Graphics2D g2d) {
        // canvas を全部書き直す
        for (DrawingHolder d : drawingList) {
            d.draw(g2d);
        }
        // 現在の色，ストロークなどを設定
        properties.setGraphicsState(g2d);
        curState.draw(g2d);
    }

    // 押すだけの State （マウス drag したりしない処理）
    // curState.draw が呼ばれるので，その前の state は mouseUp で必ず shape = null しておく必要がある
    public void undo() {
        undoMgr.undo();
        context.getCanvas().repaint();
    }

    public void redo() {
        undoMgr.redo();
        context.getCanvas().repaint();
    }

    public void clear() {
        undoMgr.storeDraw();
        drawingList.clear();
        context.getCanvas().repaint();
    }

    public void rotateRight() {
        properties.setIsRightRotation(true);
        rotationState.rotate();
    }

    public void rotateLeft() {
        properties.setIsRightRotation(false);
        rotationState.rotate();
    }

    public void expand() {
        expandState.expand();
    }
}
