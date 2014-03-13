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
package open.dolphin.dao;

import open.dolphin.client.ClientContext;
import org.apache.log4j.Logger;

/**
 * DaoBean
 *
 * @author Kazushi Minagawa
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class DaoBean {

    public static final int TT_NONE = 10;
    public static final int TT_NO_ERROR = 0;
    public static final int TT_CONNECTION_ERROR = -1;
    public static final int TT_DATABASE_ERROR = -2;
    public static final int TT_UNKNOWN_ERROR = -3;

    protected String host;
    protected int port;
    protected String user;
    protected String passwd;

    protected int errorCode;
    protected String errorMessage;

    protected Logger logger;

    /**
     * Creates a new instance of DaoBean
     */
    public DaoBean() {
        logger = ClientContext.getBootLogger();
    }

    public final String getHost() {
        return host;
    }

    public final void setHost(String host) {
        this.host = host;
    }

    public final int getPort() {
        return port;
    }

    public final void setPort(int port) {
        this.port = port;
    }

    public final String getUser() {
        return user;
    }

    public final void setUser(String user) {
        this.user = user;
    }

    public final String getPasswd() {
        return passwd;
    }

    public final void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public final boolean isNoError() {
        return errorCode == TT_NO_ERROR ? true : false;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * 例外を解析しエラーコードとエラーメッセージを設定する。
     *
     * @param e Exception
     */
    protected void processError(Exception e) {

        logger.warn(e);

        StringBuilder sb = new StringBuilder();

        if (e instanceof org.postgresql.util.PSQLException) {
            setErrorCode(TT_CONNECTION_ERROR);
            sb.append("サーバに接続できません。ネットワーク環境をお確かめください。");
            sb.append("\n");
            sb.append(appenExceptionInfo(e));
            setErrorMessage(sb.toString());

        } else if (e instanceof java.sql.SQLException) {
            setErrorCode(TT_DATABASE_ERROR);
            sb.append("データベースアクセスエラー");
            sb.append("\n");
            sb.append(appenExceptionInfo(e));
            setErrorMessage(sb.toString());
        } else {
            setErrorCode(TT_UNKNOWN_ERROR);
            sb.append("アプリケーションエラー");
            sb.append("\n");
            sb.append(appenExceptionInfo(e));
            setErrorMessage(sb.toString());
        }
    }

    /**
     * 例外の持つ情報を加える。
     *
     * @param e 例外
     */
    protected String appenExceptionInfo(Exception e) {

        StringBuilder sb = new StringBuilder();
        sb.append("例外クラス: ");
        sb.append(e.getClass().getName());
        sb.append("\n");
        if (e.getCause() != null && e.getCause().getMessage() != null) {
            sb.append("原因: ");
            sb.append(e.getCause().getMessage());
            sb.append("\n");
        }
        if (e.getMessage() != null) {
            sb.append("内容: ");
            sb.append(e.getMessage());
        }

        return sb.toString();
    }
}
