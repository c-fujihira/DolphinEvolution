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
package open.dolphin.impl.doc;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import open.dolphin.helper.UserDocumentHelper;
import open.dolphin.infomodel.PatientFileModel;

/**
 * PatientFileTableTransferHandler
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PatientFileTableTransferHandler extends TransferHandler {

    // From StackOverFlow
    private static DataFlavor nixFileDataFlavor;

    static {
        try {
            nixFileDataFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
        } catch (Exception e) {
        }
    }

    private PatientDocFile context;

    public PatientFileTableTransferHandler(PatientDocFile context) {
        this.context = context;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTable sourceTable = (JTable) c;
        int row = sourceTable.getSelectedRow();
        List<PatientFileModel> list = context.getDataList();

        if (list == null || row >= list.size() || row < 0) {
            return null;
        }

        PatientFileModel model = list.get(row);

//        int col = sourceTable.getSelectedColumn();
//        if (row != -1 && col != -1) {
//            ImageEntry entry = (ImageEntry) sourceTable.getValueAt(row, col);
//            if (entry != null) {
//                File f = new File(entry.getPath());
//                File[] files = new File[1];
//                files[0] = f;
//                Transferable tr = new FileListTransferable(files);
//                return tr;
//            } else {
//                return null;
//            }
//        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport support) {

        if (!canImport(support)) {
            return false;
        }

        try {
            // Drag & Drop されたファイルのリストを得る
            Transferable t = support.getTransferable();

            java.util.List<File> files = null;

            if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                files = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

            } //jdk1.6 nix
            else if (support.isDataFlavorSupported(nixFileDataFlavor)) {
                files = getDropedFiles((String) t.getTransferData(nixFileDataFlavor));
            }

            List<File> allFiles = new ArrayList<File>();

            for (File file : files) {

                if (file.isDirectory() || file.getName().startsWith(".") || (file.length() == 0L)) {
                    continue;
                }

                String name = file.getName();
                int index = name.indexOf(".");
                String ext = null;
                if (index > 0) {
                    ext = name.substring(index + 1);
                }

                if (ext != null && (!UserDocumentHelper.isImage(ext))) {
                    allFiles.add(file);
                }
            }

            context.fileDropped(allFiles);

            return true;

        } catch (UnsupportedFlavorException ufe) {
            ufe.printStackTrace(System.err);

        } catch (IOException ieo) {
            ieo.printStackTrace(System.err);

        }
        return false;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        boolean canImport = true;
        canImport = canImport && support.isDrop();
        boolean isFile = (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || support.isDataFlavorSupported(nixFileDataFlavor));
        canImport = canImport && isFile;
        return canImport;
    }

    private List<File> getDropedFiles(String data) {

        List<File> files = new ArrayList<File>(2);

        for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
            String token = st.nextToken().trim();

            if (token.startsWith("#") || token.isEmpty()) {
                // comment line, by RFC 2483
                continue;
            }
            try {
                files.add(new File(new URI(token)));

            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        return files;
    }
}
