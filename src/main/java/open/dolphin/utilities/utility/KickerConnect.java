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

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

/**
 * KickerConnect
 *
 * @author funabashi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class KickerConnect {

    private String responseStr;
    private String errorMessage;
    private String host;
    private int port;

    private int timeout = 10000;    // タイムアウト値（デフォルト：10秒）

    private static final String OK_RESPONSE = "OK";

    private static final String EXE_CONNECT_STR = "EXE|";
    private static final String URL_CONNECT_STR = "URL|";
    private static final String BROWSER_CONNECT_STR = "BRW|";
    private static final String FILESAVE_CONNECT_STR = "FSV|";

    /**
     * ラストエラーメッセージを格納する
     *
     * @return エラーメッセージ
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * タイムアウト値を取得する
     *
     * @return タイムアウト値（ミリ秒）
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * タイムアウト値を設定する
     *
     * @param val タイムアウト値（ミリ秒）
     */
    public void setTimeout(int val) {
        this.timeout = val;
    }

    /**
     * コンストラクタ
     *
     * @param host ホスト
     * @param port ポート番号
     */
    public KickerConnect(String host, int port) {
        this.responseStr = null;
        this.errorMessage = "";
        this.host = host;
        this.port = port;

    }

    public KickerConnect(String host, int port, int timeout) {
        this.responseStr = null;
        this.errorMessage = "";
        this.host = host;
        this.port = port;
        this.timeout = timeout;

    }

    public boolean browserStart(String queryString) {
        return appStart(BROWSER_CONNECT_STR, queryString);
    }

    public boolean urlStart(String queryString) {
        return appStart(URL_CONNECT_STR, queryString);
    }

    public boolean exeStart(String queryString) {
        return appStart(EXE_CONNECT_STR, queryString);
    }

    public boolean saveFile(String fname, String statement) {
        String buf = fname + "|" + statement;
        return appStart(FILESAVE_CONNECT_STR, buf);
    }

    private boolean appStart(String command, String queryString) {

        responseStr = "";
        Socket socket = null;
        //InputStreamReader reader = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;
        InputStream is = null;
        boolean bRet = false;

        try {
            socket = new Socket(host, port);
            socket.setSoTimeout(this.timeout);
            System.out.println("接続しました:" + socket.getRemoteSocketAddress() + " - timeout:" + socket.getSoTimeout());

            is = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"));
            //
            writer.write(command);
            writer.write(queryString);
            writer.flush();

            int time = 0;
            String totaldata = "";
            while (is.available() >= 0) {
                if (is.available() == 0) {
                    if (totaldata.length() > 0) {
                        break;  // data取得済み
                    }

                    //System.err.print((is.available()) + ":・・・");
                    if (time++ > this.timeout / 100) {
                        errorMessage = "タイムアウトが発生しました";
                        System.err.println("timeout - move()");
                        break;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignor) {
                    }
                    continue;
                }
                char[] data = new char[is.available()];
                reader.read(data, 0, is.available());
                System.out.println("戻り値：");
                System.out.print(data);
                totaldata += new String(data);
            }
            if (totaldata.length() > 0) {
                String res = totaldata;
                if (res.equals(OK_RESPONSE)) {
                    responseStr = res;
                    bRet = true;
                } else {
                    errorMessage = res;
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: " + host);
            System.err.println(e);
            errorMessage = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println(e);
            errorMessage = e.getMessage();
            e.printStackTrace();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
            }
        }
        //System.out.println("切断されました " + socket.getRemoteSocketAddress());

        return bRet;
    }

    public String createQueryString(String patientID) {
        StringBuilder sb = new StringBuilder();

        // 患者ID
        if (patientID != null) {
            sb.append("pid=");
            sb.append(patientID);
        }

        return sb.toString();

    }

    public static void main(String[] args) {

        int port = 2101;    // 接続先ポート番号
        String host = "172.31.200.193"; // 接続先IPアドレス

        KickerConnect con = new KickerConnect(host, port);
        con.timeout = 10000;    // タイムアウト値
        try {

            boolean b = con.exeStart(con.createQueryString("1234567")); // EXE起動（パラメタのみ送信）
            //boolean b = con.browserStart(con.createQueryString("12345")); // ブラウザ起動（パラメタのみ送信）
            //boolean b = con.urlStart("http://lscc.co.jp/?id=\"test\""); // ブラウザ起動（URLごと送信）
            //boolean b = con.saveFile("\\\\172.31.10.140\\a\\dddddd.txt", "1,2,3,4,5,6,7,8,9,,10" ); // ファイル保存

            if (b) {
                System.out.print("OK------!");
            } else {
                String err = con.getErrorMessage();
                System.out.print(err);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
