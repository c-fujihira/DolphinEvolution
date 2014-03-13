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

import java.io.*;
import java.net.*;

/**
 * SOCKETクラス
 *
 * @author Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class SOCKET extends AbstractCommonFunc {

    protected Socket clientSocket;
    protected OutputStreamWriter clientOsw;
    protected BufferedWriter clientBw;
    protected InputStream clientIs;
    protected InputStreamReader clientIsr;
    protected BufferedReader clientBr;
    protected DataOutputStream clientDos;

    /**
     * コンストラクタ
     */
    protected SOCKET() {
        super();
        Init();
    }

    /**
     * 初期化
     */
    @Override
    void Init() {
    }

    /**
     * ソケット接続
     *
     * @param host ホスト名 or IPアドレス
     * @param port ポート番号
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    protected Socket createClientSocket(String host, int port) throws UnknownHostException, IOException {
        // ソケットの生成
        clientSocket = new Socket(host, port);
        return clientSocket;
    }

    /**
     * 送信
     *
     * @param data 送信するデータ
     * @throws IOException
     */
    protected void sendClientData(String data) throws IOException {
        if (clientSocket == null || data == null) {
            return;
        }
        // 送信する
        //clientOsw = new OutputStreamWriter(clientSocket.getOutputStream());
        //clientBw = new BufferedWriter(clientOsw);
        //clientBw.write(data);
        //clientBw.flush();
        clientDos = new DataOutputStream(clientSocket.getOutputStream());
        clientBr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        clientDos.writeBytes(data);
    }

    /**
     * 受信
     *
     * @param encode エンコード
     * @return 受信したデータ
     * @throws IOException
     */
    protected String recvClientData(String encode) throws IOException {
        // 受信する
        //clientIs = clientSocket.getInputStream();
        //clientIsr = new InputStreamReader(clientIs, encode);
        //clientBr = new BufferedReader(clientIsr);
        //// 受信できるまで待機
        //while(clientIs.available() == 0);
        //// 受信した内容を出力
        //char[] ret = new char[clientIs.available()];
        //clientBr.read(ret);
        if (clientBr == null) {
            return null;
        }
        StringBuilder ret = new StringBuilder();
        String tmp;
        while ((tmp = clientBr.readLine()) != null) {
            ret.append(tmp);
        }

        return ret.toString();
    }

    protected String recv() throws IOException {
        StringBuilder ret = new StringBuilder();
        String tmp;
        while ((tmp = clientBr.readLine()) != null) {
            ret.append(tmp);
        }
        clientDos.close();
        clientBr.close();
        clientSocket.close();
        return ret.toString();
    }

    /**
     * 終了
     *
     * @throws IOException
     */
    protected void closeClientSocket() throws IOException {
        //clientBw.close();
        //clientOsw.close();
        //clientBr.close();
        //clientIsr.close();
        //clientSocket.close();
        clientDos.close();
        clientBr.close();
        clientSocket.close();
    }

    protected void createServerSocket(int port) throws IOException {
        // ソケットの生成
        ServerSocket serverSocket = new ServerSocket(port);
        boolean endFlag = false;
        Socket socket = null;
        while (!endFlag) {
            // クライアントからの接続を待機(接続時に新たなSocketを返す)
            // 接続があるまで処理はブロックされるため、複数のクライアントからの接続を受付する
            // ためにはスレッドを使用する。
            socket = serverSocket.accept();
            // データの受信
            InputStreamReader isr = new InputStreamReader(socket.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String ret = br.readLine();
            br.close();
            socket.close();
            if (ret.startsWith("[end]")) {
                // データの返信
                //OutputStream os = socket.getOutputStream();
                //PrintStream ps = new PrintStream(os);
                //ps.println("END");
                serverSocket.close();
                endFlag = true;
            }
        }
    }
}
