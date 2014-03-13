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
package open.dolphin.utilities.utility;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.*;
import java.io.IOException;

/**
 * クリップボードクラス
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class Clipboard {

    /**
     * コンストラクタ
     */
    public Clipboard() {
    }

    /**
     * 文字列をクリップボードへセット
     *
     * @param str 文字列
     */
    public void setClipboardString(String str) {
        Toolkit tool = Toolkit.getDefaultToolkit();
        java.awt.datatransfer.Clipboard clip = tool.getSystemClipboard();
        StringSelection selection = new StringSelection(str);
        clip.setContents(selection, selection);
    }

    /**
     * クリップボードから文字列を取得
     *
     * @return 文字列
     */
    public String getClipboardString() {
        Toolkit tool = Toolkit.getDefaultToolkit();
        java.awt.datatransfer.Clipboard clip = tool.getSystemClipboard();
        try {
            return (String) clip.getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * クリップボードへ画像をセット
     *
     * @param img 画像
     */
    public void setClipboardImage(Image img) {
        Toolkit tool = Toolkit.getDefaultToolkit();
        java.awt.datatransfer.Clipboard clip = tool.getSystemClipboard();
        ImageSelection selection = new ImageSelection(img);
        clip.setContents(selection, selection);
    }

    /**
     * クリップボードから画像を取得
     *
     * @return 画像
     */
    public Image getClipboardImage() {
        Toolkit tool = Toolkit.getDefaultToolkit();
        java.awt.datatransfer.Clipboard clip = tool.getSystemClipboard();
        try {
            return (Image) clip.getData(DataFlavor.imageFlavor);
        } catch (UnsupportedFlavorException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}

/**
 * ImageSelectionクラス
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 */
class ImageSelection implements Transferable, ClipboardOwner {

    protected Image img;

    /**
     * コンストラクタ
     *
     * @param image 画像
     */
    public ImageSelection(Image image) {
        img = image;
    }

    /**
     * 対応フレーバーの取得
     *
     * @return フレーバー
     */
    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }

    /**
     * フレーバーのチェック
     *
     * @param flavor フレーバー
     * @return サポート結果
     */
    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return DataFlavor.imageFlavor.equals(flavor);
    }

    /**
     * 画像の取得
     *
     * @param flavor フレーバー
     * @return 画像
     * @throws UnsupportedFlavorException
     * @throws IOException
     */
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (DataFlavor.imageFlavor.equals(flavor)) {
            return img;
        }
        throw new UnsupportedFlavorException(flavor);
    }

    /**
     * クリップボードデータの破棄
     *
     * @param clipboard
     * @param contents
     */
    @Override
    public void lostOwnership(java.awt.datatransfer.Clipboard clipboard, Transferable contents) {
        img = null;
    }
}
