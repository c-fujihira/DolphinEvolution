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
package open.dolphin.client;

import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 * ImageEntry
 *
 * @author  Kazushi Minagawa, Digital globe, Inc.
 */
public class ImageEntry implements Serializable {
    
    private String confirmDate;
    
    private String title;
    
    private String medicalRole;
    
    private String contentType;
    
    private ImageIcon imageIcon;
    
    private long id;
    
    private String url;
    
    private String fileName;
    
    private String path;
    
    private int numImages = 1;
    
    private int width;
    
    private int height;

    private boolean dicomFileIsSOP;

    private long lastModified;
    
    private boolean directrory;
    
    
    /** Creates a new instance of ImageEntry */
    public ImageEntry() {
    }
    
    public String getConfirmDate() {
        return confirmDate;
    }
    
    public void setConfirmDate(String val) {
        confirmDate = val;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String val) {
        title = val;
    }
    
    public String getMedicalRole() {
        return medicalRole;
    }
    
    public void setMedicalRole(String val) {
        medicalRole = val;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String val) {
        contentType = val;
    }
    
    public ImageIcon getImageIcon() {
        return imageIcon;
    }
    
    public void setImageIcon(ImageIcon val) {
        imageIcon = val;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long val) {
        id = val;
    }
    
    /**
     * @param url The url to set.
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * @return Returns the url.
     */
    public String getUrl() {
        return url;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getNumImages() {
        return numImages;
    }

    public void setNumImages(int numImages) {
        this.numImages = numImages;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public boolean isDicomFileIsSOP() {
        return dicomFileIsSOP;
    }

    public void setDicomFileIsSOP(boolean dicomFileIsSOP) {
        this.dicomFileIsSOP = dicomFileIsSOP;
    }

    public boolean isDirectrory() {
        return directrory;
    }

    public void setDirectrory(boolean directrory) {
        this.directrory = directrory;
    }
}
