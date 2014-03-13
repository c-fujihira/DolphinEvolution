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

import java.awt.Point;
import open.dolphin.impl.scheam.SchemaEditorImpl;
import open.dolphin.impl.scheam.holder.DrawingHolder;

/**
 * SelectState
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class SelectState extends AbstractState {

    private DrawingHolder moving;
    private Point temp; // ドラッグ途中で使う

    public SelectState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {
        moving = findDrawing(p);
        if (moving != null) {
            start = p;
            temp = p;
            end = null;
        }
    }

    @Override
    public void mouseDragged(Point p) {
        if (moving != null) {
            end = p;
            moving.translate(end.getX() - temp.getX(), end.getY() - temp.getY());
            canvas.repaint();
            temp = end;
        }
    }

    @Override
    public void mouseUp(Point p) {
        if (moving != null) {
            undoMgr.storeMove(moving, end.getX() - start.getX(), end.getY() - start.getY());
        }
    }

    /**
     * マウス位置の DrawingHolder を返す
     *
     * @param p
     * @return
     */
    public DrawingHolder findDrawing(Point p) {
        DrawingHolder found = null;
        int cnt = drawingList.size();
        if (cnt > 0) {
            for (int i = cnt; i > 0; i--) {
                DrawingHolder d = drawingList.get(i - 1);
                if (d.contains(p)) {
                    found = d;
                    break;
                }
            }
        }
        return found;
    }
}
