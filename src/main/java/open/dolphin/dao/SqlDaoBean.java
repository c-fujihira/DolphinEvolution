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

import java.sql.*;
import java.util.Collection;
import open.dolphin.project.Project;

/**
 * SqlDaoBean
 *
 * @author Kazushi Minagawa Modified by masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class SqlDaoBean extends DaoBean {

    private static final String DRIVER = "org.postgresql.Driver";
    private static final int PORT = 5432;
    private static final String DATABASE = "orca";
    private static final String USER = "orca";
    private static final String PASSWD = "";

    String dataBase;
    String driver;
    boolean trace = true;

    /**
     * Creates a new instance of SqlDaoBean
     */
    public SqlDaoBean() {
        setDriver(DRIVER);
        //this.setHost(Project.getClaimAddress());
        this.setHost(Project.getString(Project.CLAIM_ADDRESS));
        this.setPort(PORT);
        this.setDatabase(DATABASE);
        this.setUser(USER);
        this.setPasswd(PASSWD);
        this.setHospNum();
    }

//masuda^   ORCA 4.6対応など
    protected static final String ORCA_DB_VER45 = "040500-1";
    protected static final String ORCA_DB_VER46 = "040600-1";
    protected static final String ORCA_DB_VER47 = "040700-1";

    private static int hospNum;
    private static String dbVersion;

    protected String getOrcaDbVersion() {
        return dbVersion;
    }

    protected int getHospNum() {
        return hospNum;
    }

    // ORCAのデータベースバージョンとhospNumを取得する
    protected final void setHospNum() {

        if (dbVersion != null) {
            return;
        }

        Connection con = null;
        Statement st = null;
        String sql;
        hospNum = 1;
        String jmari = Project.getString(Project.JMARI_CODE);

        StringBuilder sb = new StringBuilder();
        sb.append("select hospnum, kanritbl from tbl_syskanri where kanricd='1001' and kanritbl like '%");
        sb.append(jmari);
        sb.append("%'");
        sql = sb.toString();
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                hospNum = rs.getInt(1);
            }
        } catch (Exception e) {
            processError(e);
            closeConnection(con);
            closeStatement(st);
        }

        sql = "select version from tbl_dbkanri where kanricd='ORCADB00'";
        try {
            con = getConnection();
            st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                dbVersion = rs.getString(1);
            }
        } catch (Exception e) {
            processError(e);
            closeConnection(con);
            closeStatement(st);
        }
    }

    // ORCAのptidを取得する
    protected long getOrcaPtID(String patientId) {

        long ptid = 0;

        final String sql = "select ptid from tbl_ptnum where hospnum = ? and ptnum = ?";
        Connection con = null;
        PreparedStatement ps;

        try {
            con = getConnection();
            ps = con.prepareStatement(sql);
            ps.setInt(1, getHospNum());
            ps.setString(2, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ptid = rs.getLong(1);
                }
            }
            ps.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            processError(e);
            closeConnection(con);
        } finally {
            closeConnection(con);
        }

        return ptid;
    }

    // srycdのListからカンマ区切りの文字列を作る
    protected String getCodes(Collection<String> srycdList) {

        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String srycd : srycdList) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append(addSingleQuote(srycd));
        }
        return sb.toString();
    }

    //masuda$
    public String getDriver() {
        return driver;
    }

    public final void setDriver(String driver) {

        this.driver = driver;

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException cnfe) {
            logger.warn("Couldn't find the driver!");
            logger.warn("Let's print a stack trace, and exit.");
            cnfe.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public String getDatabase() {
        return dataBase;
    }

    public final void setDatabase(String base) {
        dataBase = base;
    }

    protected String getURL() {
        StringBuilder buf = new StringBuilder();
        buf.append("jdbc:postgresql://");
        buf.append(host);
        buf.append(":");
        buf.append(port);
        buf.append("/");
        buf.append(dataBase);
        return buf.toString();
    }

    public boolean getTrace() {
        return trace;
    }

    public void setTrace(boolean b) {
        trace = b;
    }

    public Connection getConnection() throws Exception {
        return DriverManager.getConnection(getURL(), user, passwd);
    }

    public String addSingleQuote(String s) {
        StringBuilder buf = new StringBuilder();
        buf.append("'");
        buf.append(s);
        buf.append("'");
        return buf.toString();
    }

    /**
     * To make sql statement ('xxxx',)<br>
     * @param s
     * @return 
     */
    public String addSingleQuoteComa(String s) {
        StringBuilder buf = new StringBuilder();
        buf.append("'");
        buf.append(s);
        buf.append("',");
        return buf.toString();
    }

    public void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    protected void debug(String msg) {
        logger.debug(msg);
    }

    protected void printTrace(String msg) {
        if (trace) {
            logger.debug(msg);
        }
    }

    protected void rollback(Connection con) {
        if (con != null) {
            try {
                con.rollback();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }
}
