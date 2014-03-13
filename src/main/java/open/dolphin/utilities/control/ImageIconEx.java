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
package open.dolphin.utilities.control;

import java.awt.Image;
import javax.swing.ImageIcon;

/**
 * テーブルに表示するImageIconクラス
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class ImageIconEx {

    public static final int MAX_ICONSIZE = 100;

    private ImageIcon imgIcon;
    private String imgText;
    private int iconMaxSize;

    /**
     * コンストラクタ
     */
    public ImageIconEx() {
        imgIcon = null;
        imgText = null;
        iconMaxSize = MAX_ICONSIZE;
    }

    /**
     * アイコンの最大サイズのセット
     *
     * @param size サイズ
     */
    public void setIconMaxSize(int size) {
        iconMaxSize = size;
    }

    /**
     * アイコンの最大サイズの取得
     *
     * @return サイズ
     */
    public int getIconMaxSize() {
        return iconMaxSize;
    }

    /**
     * アイコンのセット
     *
     * @param path アイコンのパス
     */
    public void setIcon(String path) {
        ImageIcon icon = new ImageIcon(path);
        setIcon(icon);
    }

    /**
     * アイコンのセット
     *
     * @param icon アイコン
     */
    public void setIcon(ImageIcon icon) {
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();
        float scale = 0f;
        if (width >= height) {
            scale = (float) getIconMaxSize() / (float) width;
            width = getIconMaxSize();
            height = (int) ((float) height * scale);
        } else {
            scale = (float) getIconMaxSize() / (float) height;
            width = (int) ((float) width * scale);
            height = getIconMaxSize();
        }
        imgIcon = new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    /**
     * アイコンの取得
     *
     * @return アイコン
     */
    public ImageIcon getIcon() {
        return imgIcon;
    }

    /**
     * テキストのセット
     *
     * @param text テキスト
     */
    public void setText(String text) {
        imgText = text;
    }

    /**
     * テキストの取得
     *
     * @return テキスト
     */
    public String getText() {
        return imgText;
    }

    public static void main(String[] args) {
        //ImageIconEx img = new ImageIconEx();
        //img.setIcon(ImageIcon);
        //img.setText(テキスト);
    }
}
