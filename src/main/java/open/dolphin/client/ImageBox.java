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

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import open.dolphin.helper.ComponentMemory;

/**
 * ImageBox
 *
 * @author Minagawa,Kazushi
 */
public class ImageBox extends AbstractMainTool {

    private static final String DEFAULT_TAB = "基本セット";
    private static final int DEFAULT_COLUMN_COUNT = 3;
    private static final int DEFAULT_IMAGE_WIDTH = 120;
    private static final int DEFAULT_IMAGE_HEIGHT = 120;
    private static final String[] DEFAULT_IMAGE_SUFFIX = {".jpg", ".png"};

    private String imageLocation;
    private JTabbedPane tabbedPane;
    private JButton refreshBtn;
    private int columnCount = DEFAULT_COLUMN_COUNT;
    private int imageWidth = DEFAULT_IMAGE_WIDTH;
    private int imageHeight = DEFAULT_IMAGE_HEIGHT;
    private String[] suffix = DEFAULT_IMAGE_SUFFIX;

    private JPanel content;
    private JDialog frame;

    private int systemSchemaIndex;

    public ImageBox() {
    }

    @Override
    public void start() {
        initComponent();
        connect();
        setImageLocation(ClientContext.getSchemaDirectory());
    }

    @Override
    public void stop() {
        if (tabbedPane != null) {
            int cnt = tabbedPane.getTabCount();
            for (int i = 0; i < cnt; i++) {
                ImagePalette ip = (ImagePalette) tabbedPane.getComponentAt(i);
                if (ip != null) {
                    ip.dispose();
                }
            }
        }
        frame.dispose();
    }

    public JDialog getFrame() {
        return frame;
    }

    public void toFront() {
        if (frame != null) {
            if (!frame.isVisible()) {
                frame.setVisible(true);
            }
            frame.toFront();
        }
    }

    public String getImageLocation() {
        return imageLocation;
    }

    public void setImageLocation(String loc) {

        this.imageLocation = loc;

        SwingWorker worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                createImagePalettes();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    tabbedPane.addChangeListener(new ChangeListener() {
                        @Override
                        public void stateChanged(ChangeEvent ce) {
                            Component cmp = tabbedPane.getSelectedComponent();
                            if (cmp == null) {
                                ImagePalette sysTable = new ImagePalette(null, columnCount, imageWidth, imageHeight);
                                sysTable.setupDefaultSchema();
                                tabbedPane.setComponentAt(systemSchemaIndex, sysTable);
                            }
                        }
                    });
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        };

        worker.execute();
    }

    public void refresh() {
        tabbedPane.removeAll();
        systemSchemaIndex = 0;
        setImageLocation(ClientContext.getSchemaDirectory());
    }

    private void initComponent() {

        // TabbedPane を生成する
        tabbedPane = new JTabbedPane();

        // コンテントパネルを生成する
        content = new JPanel(new BorderLayout());
        
        // 更新ボタンを生成する
        refreshBtn = new JButton(ClientContext.getImageIconArias("icon_refresh"));
        refreshBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, this, "refresh"));
        refreshBtn.setToolTipText("シェーマリストを更新します");
        refreshBtn.setPreferredSize(new Dimension(35, 35));
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnPanel.add(refreshBtn);

        // 全体を配置する
        content.add(btnPanel, BorderLayout.NORTH);
        content.add(tabbedPane, BorderLayout.CENTER);
        content.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
    }

    private void connect() {
    }

    public JPanel getContentPane() {
        return content;
    }

    public void createImagePalettes() {

        Path imageDir = Paths.get(imageLocation);

        final java.util.List<Path> paths;
        paths = new ArrayList<>();
        try {
            DirectoryStream<Path> ds = Files.newDirectoryStream(imageDir);
            for (Path p : ds) {
                if (Files.isDirectory(p)) {
                    paths.add(p);
                }
            }
        } catch (IOException e) {
        }

        if (!paths.isEmpty()) {
            for (Path path : paths) {
                String tabName = path.getFileName().toString();
                ImagePalette imageTable = new ImagePalette(null, columnCount, imageWidth, imageHeight);
                imageTable.setImageSuffix(suffix);
                imageTable.setImageDirectory(path);   // refresh
                tabbedPane.addTab(tabName, imageTable);
            }
            systemSchemaIndex = paths.size();
            tabbedPane.addTab(DEFAULT_TAB, null);
        }
        if (systemSchemaIndex == 0) {
            ImagePalette sysTable = new ImagePalette(null, columnCount, imageWidth, imageHeight);
            sysTable.setupDefaultSchema();
            tabbedPane.addTab(DEFAULT_TAB, sysTable);
        }
//        
//        File baseDir = new File(imageLocation);
//        if (baseDir.exists() && baseDir.isDirectory()) {
//            
//            File[] directories = listDirectories(baseDir);
//            if (directories != null && directories.length > 0) {
//                for (int i = 0; i < directories.length; i++) {
//                    String tabName = directories[i].getName();
//                    ImagePalette imageTable = new ImagePalette(null, columnCount, imageWidth, imageHeight);
//                    imageTable.setImageSuffix(suffix);
//                    imageTable.setImageDirectory(directories[i]);   // refresh
//                    tabbedPane.addTab(tabName, imageTable);
//                }
//                systemSchemaIndex = directories.length;
//                tabbedPane.addTab(DEFAULT_TAB, null);
//            } 
//        }
//
//        if (systemSchemaIndex==0) {
//            ImagePalette sysTable = new ImagePalette(null, columnCount, imageWidth, imageHeight);
//            sysTable.setupDefaultSchema();
//            tabbedPane.addTab(DEFAULT_TAB, sysTable);
//        }
    }
    
//    private File[] listDirectories(File dir) {
//        DirectoryFilter filter = new DirectoryFilter();
//        File[] directories = dir.listFiles(filter);
//        return directories;
//    }
    
    public void processWindowClosing() {
        stop();
    }

    /**
     * @param columnCount The columnCount to set.
     */
    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    /**
     * @return Returns the columnCount.
     */
    public int getColumnCount() {
        return columnCount;
    }

    /**
     * @param imageWidth The imageWidth to set.
     */
    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    /**
     * @return Returns the imageWidth.
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * @param imageHeight The imageHeight to set.
     */
    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    /**
     * @return Returns the imageHeight.
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * @param suffix The suffix to set.
     */
    public void setSuffix(String[] suffix) {
        this.suffix = suffix;
    }

    /**
     * @return Returns the suffix.
     */
    public String[] getSuffix() {
        return suffix;
    }

    class DirectoryFilter implements FileFilter {

        @Override
        public boolean accept(File path) {
            return path.isDirectory();
        }
    }
}
