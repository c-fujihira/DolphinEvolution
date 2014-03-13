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
package open.dolphin.message;

import java.io.*;
import open.dolphin.client.ClientContext;
import open.dolphin.project.Project;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * DMLを任意のMessage に翻訳するクラス
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class MessageBuilder {
    //public class MessageBuilder implements IMessageBuilder {

    private static final String ENCODING = "SHIFT_JIS";

    /**
     * テンプレートファイル
     */
    private String templateFile;

    /**
     * テンプレートファイルのエンコーディング
     */
    private String encoding = ENCODING;

    private final Logger logger;

    public MessageBuilder() {
        logger = ClientContext.getBootLogger();
        logger.debug("MessageBuilder constracted");
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    //@Override
//    public String build(String dml) {
//
//        String ret = null;
//
//        try {
//            // Document root をVelocity 変数にセットする
//            SAXBuilder sbuilder = new SAXBuilder();
//            Document root = sbuilder.build(new BufferedReader(new StringReader(dml)));
//            VelocityContext context = ClientContext.getVelocityContext();
//            context.put("root", root);
//
//            // Merge する
//            StringWriter sw = new StringWriter();
//            BufferedWriter bw = new BufferedWriter(sw);
//            InputStream instream = ClientContext.getTemplateAsStream(templateFile);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(instream, encoding));
//            Velocity.evaluate(context, bw, "MessageBuilder", reader);
//            bw.flush();
//            bw.close();
//
//            ret = sw.toString();
//
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//        }
//
//        return ret;
//    }
    public String build(Object helper) {

        logger.debug("MessageBuilder build");

        String ret = null;
        String name = helper.getClass().getName();
        int index = name.lastIndexOf('.');
        name = name.substring(index + 1);
        StringBuilder sb = new StringBuilder();
        sb.append(name.substring(0, 1).toLowerCase());
        sb.append(name.substring(1));
        name = sb.toString();

        try {
            logger.debug("MessageBuilder try");
            VelocityContext context = ClientContext.getVelocityContext();
            context.put(name, helper);

            // このスタンプのテンプレートファイルを得る
            String tFile;
            if (Project.getBoolean(Project.CLAIM_01)) {
                tFile = name + "_01.vm";
            } else {
                tFile = name + ".vm";
            }
            logger.debug("template file = " + tFile);

            // Merge する
            StringWriter sw = new StringWriter();
            BufferedReader reader;
            try (BufferedWriter bw = new BufferedWriter(sw)) {
                InputStream instream = ClientContext.getTemplateAsStream(tFile);
                reader = new BufferedReader(new InputStreamReader(instream, encoding));
                Velocity.evaluate(context, bw, name, reader);
                logger.debug("Velocity.evaluated");
                bw.flush();
            }
            reader.close();

            ret = sw.toString();

        } catch (IOException | ParseErrorException | MethodInvocationException | ResourceNotFoundException e) {
            logger.warn(e);
        }

        return ret;
    }
}
