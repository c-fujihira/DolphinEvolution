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
package open.dolphin.letter;

import com.lowagie.text.Font;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import java.util.Date;
import open.dolphin.infomodel.LetterModule;
import open.dolphin.infomodel.ModelUtils;

/**
 * AbstractLetterPDFMaker
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public abstract class AbstractLetterPDFMaker {

    protected static final String EXT_PDF = ".pdf";
    protected static final String HEISEI_MIN_W3 = "HeiseiMin-W3";
    protected static final String UNIJIS_UCS2_HW_H = "UniJIS-UCS2-HW-H";

    protected static final String ERROR_IO = "ファイル IO エラー";
    protected static final String ERROR_PDF = "PDF 生成エラー";

    protected static final int TOP_MARGIN = 50;  //75;
    protected static final int LEFT_MARGIN = 50;    //75;
    protected static final int BOTTOM_MARGIN = 50;  //75;
    protected static final int RIGHT_MARGIN = 50;   //75;

    protected static final int TITLE_FONT_SIZE = 14;
    protected static final int BODY_FONT_SIZE = 11;    //12;

    protected static final float CELL_PADDING = 8.0f;

    protected String documentDir;
    protected String pathToPDF;
    protected LetterModule model;
    protected int marginLeft = LEFT_MARGIN;
    protected int marginRight = RIGHT_MARGIN;
    protected int marginTop = TOP_MARGIN;
    protected int marginBottom = BOTTOM_MARGIN;

    protected BaseFont baseFont;
    protected Font titleFont;
    protected Font bodyFont;
    protected int titleFontSize = TITLE_FONT_SIZE;
    protected int bodyFontSize = BODY_FONT_SIZE;

    // PDF 生成
    public abstract String create();

    protected String getDateString(Date d) {
        return ModelUtils.getDateAsFormatString(d, "yyyy年M月d日");
    }

    protected String getDateString(String date) {
        Date d = ModelUtils.getDateAsObject(date);
        return ModelUtils.getDateAsFormatString(d, "yyyy年M月d日");
    }

    protected PdfPCell createNoBorderCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, bodyFont));
        cell.setBorder(0);
        cell.setPadding(CELL_PADDING);
        return cell;
    }

    protected String getSexString(String sex) {
        //return ModelUtils.getGenderDesc(sex);
        return sex;
    }

    public LetterModule getModel() {
        return model;
    }

    public void setModel(LetterModule model) {
        this.model = model;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(int marginleft) {
        this.marginLeft = marginleft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public int getTitleFontSize() {
        return titleFontSize;
    }

    public void setTitleFontSize(int titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    public int getBodyFontSize() {
        return bodyFontSize;
    }

    public void setBodyFontSize(int bodyFontSize) {
        this.bodyFontSize = bodyFontSize;
    }

    public String getDocumentDir() {
        return documentDir;
    }

    public void setDocumentDir(String documentDir) {
        this.documentDir = documentDir;
    }

    public String getPathToPDF() {
        return pathToPDF;
    }

    public void setPathToPDF(String fileName) {
        this.pathToPDF = fileName;
    }
}
