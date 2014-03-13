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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import open.dolphin.impl.scheam.SchemaEditorImpl;
import open.dolphin.impl.scheam.holder.DrawingHolder;

/**
 * 一部を選択して切り抜く
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class ClippingState extends AbstractState {

    public ClippingState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {
        shape = new Rectangle2D.Double();
        start = p;
        end = null;
        first = true;
    }

    @Override
    public void mouseDragged(Point p) {
        end = p;
        ((Rectangle2D) shape).setFrameFromDiagonal(start, end);
        canvas.repaint();
    }

    @Override
    public void mouseUp(Point p) {
        if (end == null) {
            return;
        }

        BufferedImage src = canvas.getBaseImage();

        int x;
        int y;
        int width;
        int height;

        // はみ出した場合の対応
        start.x = rounding(start.x, src.getWidth());
        end.x = rounding(end.x, src.getWidth());
        start.y = rounding(start.y, src.getHeight());
        end.y = rounding(end.y, src.getHeight());

        // 反対からドラッグした場合対応
        x = (start.x < end.x) ? start.x : end.x;
        y = (start.y < end.y) ? start.y : end.y;
        width = Math.abs(start.x - end.x);
        height = Math.abs(start.y - end.y);

        // 切り抜きの大きさがなければ abort
        if (width == 0 || height == 0) {
            shape = null;
            canvas.repaint();
            return;
        }

        undoMgr.storeClipping(-x, -y);

        //DrawingHolder
        for (DrawingHolder h : drawingList) {
            h.translate(-x, -y);
        }

        BufferedImage dist = src.getSubimage(x, y, width, height);

        canvas.setBaseImage(dist);
        context.recomputeViewBounds(dist);

        shape = null;
    }

    /**
     * はみ出した分の修正
     *
     * @param num
     * @param limit
     * @return
     */
    private int rounding(int num, int limit) {
        int n = num;
        if (n < 0) {
            n = 0;
        } else if (n >= limit) {
            n = limit - 1;
        }
        return n;
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
