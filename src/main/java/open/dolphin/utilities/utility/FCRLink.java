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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * FCR連携
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class FCRLink {

    private static final String FILE_NAME = "Ekarte.xml";
    private static final String ELEMENT_ROOT = "imageinfo";
    private static final String ELEMENT_PATIENTID = "patientid";
    private static final String ELEMENT_STUDYDATE = "studydate";
    private static final String ELEMENT_MODE = "mode";
    private static final String ELEMENT_STARTSTUDYDATE = "startstudydate";
    private static final String ELEMENT_ENDSTUDYDATE = "endstudydate";
    private static final String MODE_IMAGE = "0";
    private static final String MODE_LIST = "1";
    private static final String XML_ENCODE = "Shift_JIS";

    private final String fullPath;

    /**
     * コンストラクタ
     *
     * @param path 出力パス
     */
    public FCRLink(String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        if (path.endsWith(File.separator) == false) {
            sb.append(File.separator).append(FILE_NAME);
        } else {
            sb.append(FILE_NAME);
        }
        fullPath = sb.toString();
    }

    /**
     * リストの表示
     *
     * @param pID 患者ID
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void linkList(String pID) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        //outputXmlForFCR(pID, "", MODE_LIST);
        outputTextForFCR(pID, "", MODE_LIST);
    }

    /**
     * 画像の表示
     *
     * @param pID 患者ID
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void linkImage(String pID) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        //outputXmlForFCR(pID, "", MODE_IMAGE);
        outputTextForFCR(pID, "", MODE_IMAGE);
    }

    /**
     * 全画像の表示
     *
     * @param pID 患者ID
     * @param date 日付
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void linkImage(String pID, String date) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        //outputXmlForFCR(pID, date, MODE_IMAGE);
        outputTextForFCR(pID, date, MODE_IMAGE);
    }

    /**
     * XMLファイルの作成
     *
     * @param pID 患者ID
     * @param date 日付
     * @param mode モード
     * @throws ParserConfigurationException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    private void outputXmlForFCR(String pID, String date, String mode) throws ParserConfigurationException, TransformerConfigurationException, TransformerException {
        XmlReadWrite xml = new XmlReadWrite();
        xml.create(ELEMENT_ROOT);
        xml.addElement(xml.getRoot(), ELEMENT_PATIENTID, pID);
        xml.addElement(xml.getRoot(), ELEMENT_STUDYDATE, date);
        xml.addElement(xml.getRoot(), ELEMENT_MODE, mode);
        xml.addElement(xml.getRoot(), ELEMENT_STARTSTUDYDATE, "");
        xml.addElement(xml.getRoot(), ELEMENT_ENDSTUDYDATE, "");
        xml.save(fullPath, XML_ENCODE);
    }

    /**
     * XMLファイルの作成
     *
     * @param pID 患者ID
     * @param date 日付
     * @param mode モード
     */
    private void outputTextForFCR(String pID, String date, String mode) {
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            PrintWriter pw = null;
            StringBuilder sb = new StringBuilder();
            File file = new File(fullPath);
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, XML_ENCODE);
            pw = new PrintWriter(osw);
            sb.append("<?xml version=\"1.0\" encoding=\"").append(XML_ENCODE).append("\"?>");
            sb.append("<imageinfo>");
            sb.append("<patientid>").append(pID).append("</patientid>");
            if (date == null || date.length() <= 0) {
                sb.append("<studydate/>");
            } else {
                sb.append("<studydate>").append(date).append("</studydate>");
            }
            sb.append("<mode>").append(mode).append("</mode>");
            sb.append("<startstudydate/>");
            sb.append("<endstudydate/>");
            sb.append("</imageinfo>");
            pw.write(sb.toString());
            pw.close();
        } catch (UnsupportedEncodingException | FileNotFoundException ex) {
            Logger.getLogger(FCRLink.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(FCRLink.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                osw.close();
            } catch (IOException ex) {
                Logger.getLogger(FCRLink.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
