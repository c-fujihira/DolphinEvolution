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
import java.net.MalformedURLException;
import open.dolphin.utilities.common.HTTP;

/**
 * HTTPライブラリクラス
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class HttpConnect extends HTTP {

    /**
     * コンストラクタ
     */
    public HttpConnect() {
        super();
    }

    /**
     * GET
     *
     * @param target 接続URLとパラメータ
     * @param request GET/POST
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public String httpGET(String target, String request) throws MalformedURLException, IOException {
        connectHttp(target, request, false, "", 0);
        //if(getLastResponseCode() != HttpURLConnection.HTTP_OK) return "";
        return recvHttp(true);
    }

    /**
     * GET
     *
     * @param target 接続URLとパラメータ
     * @param request GET/POST
     * @param data 送信データ
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public String httpPOST(String target, String request, String data) throws MalformedURLException, IOException {
        connectHttp(target, request, false, "", 0);
        setPostData(data);
        return recvHttp(true);
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
     * Charsetの取得
     *
     * @return Charaset
     */
    public String getCharName() {
        return getCharset();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    }
}
