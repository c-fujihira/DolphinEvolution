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
package open.dolphin.impl.img;

import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import open.dolphin.client.ClientContext;
import open.dolphin.client.ImageEntry;
import open.dolphin.exception.DolphinException;
import open.dolphin.helper.FileListTransferable;
import open.dolphin.util.Log;

/**
 * SchemaHolderTransferHandler
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class ImageTableTransferHandler extends TransferHandler {

    // From StackOverFlow
    private static DataFlavor nixFileDataFlavor;

    static {
        try {
            nixFileDataFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
        } catch (Exception e) {
        }
    }

    private final AbstractBrowser context;
    private JTable sourceTable;

    public ImageTableTransferHandler(AbstractBrowser context) {
        this.context = context;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        sourceTable = (JTable) c;
        int row = sourceTable.getSelectedRow();
        int col = sourceTable.getSelectedColumn();
        if (row != -1 && col != -1) {
            ImageEntry entry = (ImageEntry) sourceTable.getValueAt(row, col);
            if (entry != null) {
                File f = new File(entry.getPath());
                File[] files = new File[1];
                files[0] = f;
                Transferable tr = new FileListTransferable(files);
                return tr;
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY_OR_MOVE;
    }

    @Override
    protected void exportDone(JComponent c, Transferable data, int action) {
        sourceTable = null;
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

            List<File> allFiles = new ArrayList<>();

            for (File file : files) {
                if (!file.isDirectory()) {
                    String name = file.getName();
                    if (name.startsWith(".")) {
                        continue;
                    }

                    if (file.length() == 0L) {
                        continue;
                    }

                    allFiles.add(file);

                } else {
                    listAll(file, allFiles);
                }
            }

            if (allFiles.size() > 0) {
                parseFiles(allFiles);
            }
            sourceTable = null;

            return true;

        } catch (UnsupportedFlavorException | IOException ufe) {
            ufe.printStackTrace(System.err);
        }
        sourceTable = null;
        return false;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport support) {
        boolean canImport = true;
        canImport = canImport && support.isDrop();
        canImport = canImport && ((JTable) support.getComponent() != sourceTable);
        boolean isFile = (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor) || support.isDataFlavorSupported(nixFileDataFlavor));
        canImport = canImport && isFile;
        return canImport;
    }

    private void parseFiles(final List<File> imageFiles) {

        final javax.swing.SwingWorker worker = new javax.swing.SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                String baseDir = context.getImageBase();
                if (baseDir == null) {
                    return null;
                }

                String patientId = context.getContext().getPatient().getPatientId();
                StringBuilder sb = new StringBuilder();
                sb.append(baseDir).append(File.separator).append(patientId);
                String dirStr = sb.toString();
//s.oh^ 他プロセス連携
                if (context instanceof DefaultBrowserEx) {
                    dirStr = ((DefaultBrowserEx) context).getNowLocation();
                }
//s.oh$
                File dir = new File(dirStr);
                if (!dir.exists()) {
                    boolean ok = dir.mkdirs();
                    if (!ok) {
                        throw new DolphinException("画像用のディレクトリを作成できません。");
                    }
                    dir.setExecutable(true, false);
                    dir.setWritable(true, false);
                }

                for (File src : imageFiles) {
                    File dest = new File(dirStr, src.getName());
//s.oh^ 機能改善
                    boolean save = true;
                    if (dest.exists()) {
                        int ret = JOptionPane.showConfirmDialog(null, "この場所には同じ名前のファイルが既にあります。上書きしますか？", ClientContext.getString("productString"), JOptionPane.OK_CANCEL_OPTION);
                        if (ret != JOptionPane.OK_OPTION) {
                            save = false;
                        }
                    }
                    if (save) {
                        FileChannel out;
                        try (FileChannel in = (new FileInputStream(src)).getChannel()) {
                            out = (new FileOutputStream(dest)).getChannel();
                            in.transferTo(0, src.length(), out);
                        }
                        out.close();
                        dest.setLastModified(src.lastModified());
                    }
//s.oh$
                }

                if (context.dropIsMove()) {
                    while (imageFiles.size() > 0) {
                        File delete = imageFiles.remove(0);
                        delete.delete();
                    }
                }

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
//s.oh^ 他プロセス連携
                    //context.scan(context.getImgLocation());
                    if (context instanceof DefaultBrowserEx) {
                        context.scan(((DefaultBrowserEx) context).getNowLocation());
                    } else {
                        context.scan(context.getImgLocation());
                    }
//s.oh$
                } catch (InterruptedException ex) {
                    ClientContext.getBootLogger().warn(ex);
                } catch (ExecutionException ex) {
                    ClientContext.getBootLogger().warn(ex);
                    Window parent = SwingUtilities.getWindowAncestor(context.getUI());
                    String message = "ファイルをコピーできません。\n" + ex.getMessage();
                    String title = ClientContext.getFrameTitle(context.getTitle());
                    JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, title, message);
                }
            }
        };

        worker.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getNewValue().equals(SwingWorker.StateValue.STARTED)) {
                    //context.getProgressBar().setIndeterminate(true);
                } else if (evt.getNewValue().equals(SwingWorker.StateValue.DONE)) {
                    //context.getProgressBar().setIndeterminate(false);
                    //context.getProgressBar().setValue(0);
                    worker.removePropertyChangeListener(this);
                }
            }
        });

        worker.execute();
    }

    private void listAll(File dir, List<File> list) {

        File[] files = dir.listFiles();

        for (File f : files) {
            if (f.isDirectory()) {
                listAll(f, list);
            } else {
                list.add(f);
            }
        }
    }

    private List<File> getDropedFiles(String data) {

        List<File> files = new ArrayList<>(2);

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
