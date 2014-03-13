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
package open.dolphin.util;

import java.beans.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * BeanUtils
 *
 * @author Kazushi Minagawa.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class BeanUtils {

    private static final String UTF8 = "UTF-8";

    public static String beanToXml(Object bean) {

        String ret = null;
        try {
            ret = new String(getXMLBytes(bean), UTF8);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace(System.err);
        }
        return ret;
    }

    public static Object xmlToBean(String beanXml) {

        Object ret;

        // XMLDecode
        try {
            byte[] bytes = beanXml.getBytes(UTF8);

            XMLDecoder d = new XMLDecoder(
                    new BufferedInputStream(
                            new ByteArrayInputStream(bytes)));

            ret = d.readObject();
        } catch (Exception e) {
            ret = null;
            e.printStackTrace(System.err);
        }

        return ret;
    }

    public static byte[] getXMLBytes(Object bean) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));
        e.writeObject(bean);
        e.close();
        return bo.toByteArray();
    }

    public static byte[] xmlEncode(Object bean) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder(new BufferedOutputStream(bo));

        //masuda^   java.sql.Dateとjava.sql.TimestampがxmlEncodeで失敗する
        DatePersistenceDelegate dpd = new DatePersistenceDelegate();
        e.setPersistenceDelegate(java.sql.Date.class, dpd);
        TimestampPersistenceDelegate tpd = new TimestampPersistenceDelegate();
        e.setPersistenceDelegate(java.sql.Timestamp.class, tpd);
        //masuda$

        e.writeObject(bean);
        e.close();
        return bo.toByteArray();
    }

    public static Object xmlDecode(byte[] bytes) {

        XMLDecoder d = new XMLDecoder(
                new BufferedInputStream(
                        new ByteArrayInputStream(bytes)));
        return d.readObject();
    }

    //masuda^   http://forums.sun.com/thread.jspa?threadID=427879
    private static class DatePersistenceDelegate extends PersistenceDelegate {

        @Override
        protected Expression instantiate(Object oldInstance, Encoder out) {
            java.sql.Date date = (java.sql.Date) oldInstance;
            long time = Long.valueOf(date.getTime());
            return new Expression(date, date.getClass(), "new", new Object[]{time});
        }
    }

    private static class TimestampPersistenceDelegate extends PersistenceDelegate {

        @Override
        protected Expression instantiate(Object oldInstance, Encoder out) {
            java.sql.Timestamp date = (java.sql.Timestamp) oldInstance;
            long time = Long.valueOf(date.getTime());
            return new Expression(date, date.getClass(), "new", new Object[]{time});
        }
    }
//masuda$
}
