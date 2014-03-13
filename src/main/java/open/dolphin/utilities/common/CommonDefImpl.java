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

/**
 * 共通定義インターフェース
 *
 * @author S.Oh@Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public interface CommonDefImpl {

    // Dicom
    public static final String FILEFMT_BMP = "bmp";
    public static final String FILEFMT_JPG = "jpg";
    public static final String FILEFMT_PNG = "png";
    public static final String FILEFMT_GIF = "gif";
    public static final String FILEFMT_DCM = "dcm";
    public static final int FILEHEADERSIZE = 14;
    public static final int INFOHEADERSIZE = 40;
    public static final int HEADERSIZE = FILEHEADERSIZE + INFOHEADERSIZE;
    public static final short UL = 0x554c;
    public static final short OB = 0x4f42;
    public static final short OW = 0x4f57;
    public static final short UN = 0x554e;
    public static final short SQ = 0x5351;

    // Http
    public static final String CONTENTTYPE_HTML = "text/html";
    public static final String CONTENTTYPE_XML = "text/xml";
    public static final String CONTENTTYPE_TEXT = "text/plain";
    public static final String CONTENTTYPE_GIF = "text/gif";
    public static final String CONTENTTYPE_JPEG = "text/jpeg";
    public static final String CONTENTTYPE_MPEG = "text/mpeg";
    public static final String REQUESTMETHOD_GET = "GET";
    public static final String REQUESTMETHOD_POST = "POST";
    public static final String PROTOCOL_HTTP = "http";
    public static final String CHARSET_DEFAULT = "UTF-8";

    // SocketConnect
    public static final String CHARSET_SHIFTJIS = "Shift_JIS";

    // ORCA
    public static final String ORCAAPI_VER_47 = "47";
}
