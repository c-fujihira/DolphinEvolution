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
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import open.dolphin.impl.scheam.SchemaEditorImpl;

/**
 * PencilState
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PencilState extends PolygonState {

    public PencilState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {
        // drag の時は fill しない
        properties.setIsFill(false);
        start = p;
        end = null;
        first = true;
        shape = new GeneralPath();
    }

    @Override
    public void mouseUp(Point p) {

        if (shape.getBounds().width != 0 || shape.getBounds().height != 0) {
            undoMgr.storeDraw();
            addPathShape((GeneralPath) shape);
        }
        shape = null;
    }

    /**
     * クリックならドット，ALT が押されていたらランダムに６ドット
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        undoMgr.storeDraw();

        // ドットの時は fill する
        properties.setIsFill(true);

        float r = properties.getLineWidth() / 2 + 1; //ドットの半径
        if (e.isAltDown()) {
            //ランダムドット産生
            GeneralPath gp = new GeneralPath();
            java.util.Random rnd = new java.util.Random();
            gp.moveTo(start.x, start.y);
            gp.lineTo(start.x, start.y);
            for (int i = 0; i < 5; i++) {
                double t = (rnd.nextFloat() + (double) i) * Math.PI * 2 / 5;
                gp.moveTo(start.x + (float) (2.5 * r * Math.sin(t)), start.y + (float) (2.5 * r * Math.cos(t)));
                gp.lineTo(start.x + (float) (2.5 * r * Math.sin(t)), start.y + (float) (2.5 * r * Math.cos(t)));
            }
            addPathShape(gp);

        } else {
            //普通にドット
            Ellipse2D dot = new Ellipse2D.Double();
            dot.setFrameFromCenter(start.x, start.y, start.x + r, start.y + r);
            addAreaShape(dot);
        }
        canvas.repaint();
    }
}
