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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import open.dolphin.impl.scheam.SchemaEditorImpl;
import open.dolphin.impl.scheam.holder.DrawingHolder;

/**
 * RotationState
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class RotationState extends AbstractState {

    public RotationState(SchemaEditorImpl context) {
        super(context);
    }

    public void rotate() {

        undoMgr.storeRotate(properties.isRightRotation() ? Math.PI / 2 : -Math.PI / 2);

        // baseImage の rotate
        BufferedImage src = canvas.getBaseImage();
        // 90度回転なので height-width を入れ替える
        BufferedImage dist = new BufferedImage(src.getHeight(), src.getWidth(), src.getType());

        //DrawingHolder の回転
        for (DrawingHolder h : drawingList) {
            if (properties.isRightRotation()) {
                // 原点中心に 90度右回転 → 右方向に幅の分移動
                h.rotate(Math.PI / 2);
                h.translate(dist.getWidth(), 0);
            } else {
                // 原点中心に 90度左回転 → 下方向に高さ分移動
                h.rotate(-Math.PI / 2);
                h.translate(0, dist.getHeight());
            }
        }

        AffineTransform rotate;
        // baseImage の transform は src の立場で考える orz
        if (properties.isRightRotation()) {
            // 原点中心に 90度右回転 → 上方向（src から見て）に高さ分移動
            rotate = AffineTransform.getQuadrantRotateInstance(1);
            rotate.concatenate(AffineTransform.getTranslateInstance(0, -src.getHeight()));
        } else {
            // 原点中心に 90度左回転 → 左方向（src から見て）に幅の分移動
            rotate = AffineTransform.getQuadrantRotateInstance(-1);
            rotate.concatenate(AffineTransform.getTranslateInstance(-src.getWidth(), 0));
        }

        Graphics2D g = dist.createGraphics();
        g.setTransform(rotate);
        g.drawImage(src, null, 0, 0);
        g.dispose();
        // 回転したイメージを canvas にセットし直す
        canvas.setBaseImage(dist);

        // 縦横が変わるので，canvas の大きさ変更が必要
        context.recomputeViewBounds(dist);
    }

    @Override
    public void mouseDown(Point p) {
    }

    @Override
    public void mouseDragged(Point p) {
    }

    @Override
    public void mouseUp(Point p) {
    }
}
