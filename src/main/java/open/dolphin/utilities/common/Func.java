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
package open.dolphin.utilities.common;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

/**
 * 共有関数クラス
 *
 * @author Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class Func {

    /**
     * 画像のリサイズ
     *
     * @param srcImage 元画像
     * @param dstWidth 横幅
     * @param dstHeight 高さ
     * @return
     */
    public BufferedImage rescaleImage(BufferedImage srcImage, int dstWidth, int dstHeight) {
        BufferedImage dstImage = null;
        if (srcImage.getColorModel() instanceof IndexColorModel) {
            dstImage = new BufferedImage(dstWidth, dstHeight, srcImage.getType(), (IndexColorModel) srcImage.getColorModel());
        } else {
            if (srcImage.getType() == 0) {
                dstImage = new BufferedImage(dstWidth, dstHeight, BufferedImage.TYPE_4BYTE_ABGR_PRE);
            } else {
                dstImage = new BufferedImage(dstWidth, dstHeight, srcImage.getType());
            }
        }

        double x = (double) dstWidth / srcImage.getWidth();
        double y = (double) dstHeight / srcImage.getHeight();
        AffineTransform af = AffineTransform.getScaleInstance(x, y);

        if (dstImage.getColorModel().hasAlpha() && dstImage.getColorModel() instanceof IndexColorModel) {
            int pixel = ((IndexColorModel) dstImage.getColorModel()).getTransparentPixel();
            for (int i = 0; i < dstImage.getWidth(); ++i) {
                for (int j = 0; j < dstImage.getHeight(); ++j) {
                    dstImage.setRGB(i, j, pixel);
                }
            }
        }

        Graphics2D g2 = (Graphics2D) dstImage.createGraphics();
        g2.drawImage(srcImage, af, null);
        g2.dispose();

        return dstImage;
    }
}
