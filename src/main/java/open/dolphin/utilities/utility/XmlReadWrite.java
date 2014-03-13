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

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import open.dolphin.utilities.common.XML;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * XMLライブラリクラス
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class XmlReadWrite extends XML {

    /**
     * コンストラクタ
     */
    public XmlReadWrite() {
        super();
    }

    /**
     * XMLの解析
     *
     * @param xml XMLデータ
     * @return 成功/失敗
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public boolean analize(String xml, String charset) throws ParserConfigurationException, SAXException, IOException {
        boolean ret = analizeXML(xml, charset);
        if (ret) {
            ret = getRoot().hasChildNodes();
        }
        return ret;
    }

    /**
     * ルートの取得
     *
     * @return 要素
     */
    public Element getRoot() {
        return getRootElement();
    }

    /**
     * 要素の数の取得
     *
     * @param ele 要素
     * @return 要素数
     */
    public int getEleNum(Element ele) {
        return getElementNum(ele);
    }

    /**
     * 要素の取得
     *
     * @param parent 親要素
     * @param idx 要素インデックス
     * @return 要素
     */
    public Element getEle(Element parent, int idx) {
        return getElement(parent, idx);
    }

    /**
     * 要素値の取得
     *
     * @param ele 要素
     * @return 要素値
     */
    public String getEleVal(Element ele) {
        return getElementValue(ele);
    }

    /**
     * 属性値の取得
     *
     * @param ele 要素
     * @param atrb 属性
     * @return 属性値
     */
    public String getAtrbValue(Element ele, String atrb) {
        return getAttributeValue(ele, atrb);
    }

    /**
     * XMLの作成
     *
     * @param root ルート
     * @throws ParserConfigurationException
     */
    public void create(String root) throws ParserConfigurationException {
        createXML(root);
    }

    /**
     * XMLの保存
     *
     * @param path ファイルパス
     * @param encoding エンコード
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void save(String path, String encoding) throws TransformerConfigurationException, TransformerException {
        saveXML(path, encoding);
    }

    /**
     * 要素の追加
     *
     * @param parentElement 親要素
     * @param child 子の要素文字列
     * @param text 子の要素値
     */
    public void addElement(Element parentElement, String child, String text) {
        appendChildElement(parentElement, child, text);
    }

    /**
     * デバッグ情報の有無設定
     *
     * @param dbg デバッグ情報の有無
     */
    public void debug(boolean dbg) {
        setDebug(dbg);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
}
