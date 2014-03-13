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

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 他プロセス連携
 *
 * @author Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class OtherProcessLink {

    /**
     * コンストラクタ
     */
    public OtherProcessLink() {
    }

    /**
     * URL連携
     *
     * @param url URL
     * @return
     */
    public boolean linkURL(String url) {
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.browse(new URI(url));
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(OtherProcessLink.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    /**
     * ファイル連携
     *
     * @param command コマンド
     * @return
     */
    public boolean linkFile(String command) {
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(command);
        } catch (IOException ex) {
            Logger.getLogger(OtherProcessLink.class.getName()).log(Level.SEVERE, null, ex);
        }

        return true;
    }

    /**
     * TCP/IP:Exe連携
     *
     * @param data データ
     * @param host ホスト名
     * @param int ポート番号
     * @return
     */
    public boolean linkTCPToExe(String data, String host, int port) {
        KickerConnect kc = new KickerConnect(host, port);
        try {

            boolean b = kc.exeStart(kc.createQueryString(data));

            if (b) {
                System.out.print("OK");
            } else {
                String err = kc.getErrorMessage();
                System.out.print(err);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return true;
    }

    /**
     * TCP/IP:Exe連携
     *
     * @param data データ
     * @param host ホスト名
     * @param int ポート番号
     * @param file ファイルパス
     * @return
     */
    public boolean linkTCPToFile(String data, String host, int port, String file) {
        KickerConnect kc = new KickerConnect(host, port);
        try {

            boolean b = kc.saveFile(file, data); // ファイル保存

            if (b) {
                System.out.print("OK");
            } else {
                String err = kc.getErrorMessage();
                System.out.print(err);
            }
        } catch (Exception ex) {
        }
        return true;
    }
}
