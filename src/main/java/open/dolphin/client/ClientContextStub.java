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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicTextPaneUI;
import open.dolphin.exception.DolphinException;
import open.dolphin.infomodel.DepartmentModel;
import open.dolphin.infomodel.DiagnosisCategoryModel;
import open.dolphin.infomodel.DiagnosisOutcomeModel;
import open.dolphin.infomodel.LicenseModel;
import open.dolphin.util.PropatyGetter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Dolphin Client のコンテキストクラス。
 *
 * @author  Kazushi Minagawa, Digital Globe, Inc.
 */
public final class ClientContextStub {

    private HashMap<String, Color> eventColorTable;
    private LinkedHashMap<String, String> toolProviders;

    private String pathToEvo;

    public PropatyGetter prpGt = new PropatyGetter();

    /**
     * ClientContextStub オブジェクト生成
     */
    public ClientContextStub() {

        try {

            //- OpenDolphinPro 設定引き継ぎ
            StringBuilder oldSb = new StringBuilder();
            oldSb.append(System.getProperty("user.home"));
            oldSb.append(File.separator);
            oldSb.append(getString("product.old.name"));
            File checkOld = new File(oldSb.toString());

            if (checkOld.exists()) {
                pathToEvo = oldSb.toString();
            } else {
                //- user.home に Dolphin directoryを生成する
                pathToEvo = createDirectory(System.getProperty("user.home"),
                        getString("client.context.directory"));
            }

            createDirectory(pathToEvo, getString("client.context.directory.log"));
            createDirectory(pathToEvo, getString("client.context.directory.pdf"));
            createDirectory(pathToEvo, getString("client.context.directory.schema"));
            createDirectory(pathToEvo, getString("client.context.directory.odt_template"));
            createDirectory(pathToEvo, getString("client.context.directory.temp"));
            String pathToSetting = createDirectory(pathToEvo, getString("client.context.directory.setting"));

            //- Log4J のコンフィグレーションを行う
            File log4jProp = new File(pathToSetting, getString("client.context.log4j.file"));

            if (log4jProp.exists()) {
                PropertyConfigurator.configure(log4jProp.getPath());
            } else {
                Properties prop = new Properties();
                try (BufferedInputStream in
                        = new BufferedInputStream(getResourceAsStream(getString("client.context.log4j.file")))) {
                    prop.load(in);
                }
                prop.setProperty("log4j.appender.bootAppender.File", pathToLogFile(getString("client.context.log4j.file.boot")));
                prop.setProperty("log4j.appender.part11Appender.File", pathToLogFile(getString("client.context.log4j.file.part11")));
                prop.setProperty("log4j.appender.delegaterAppender.File", pathToLogFile(getString("client.context.log4j.file.delegater")));
                prop.setProperty("log4j.appender.pvtAppender.File", pathToLogFile(getString("client.context.log4j.file.pvt")));
                prop.setProperty("log4j.appender.labTestAppender.File", pathToLogFile(getString("client.context.log4j.file.labTest")));
                prop.setProperty("log4j.appender.claimAppender.File", pathToLogFile(getString("client.context.log4j.file.claim")));
                prop.setProperty("log4j.appender.mmlAppender.File", pathToLogFile(getString("client.context.log4j.file.mml")));
                try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(log4jProp))) {
                    prop.store(out, getVersion());
                }
                PropertyConfigurator.configure(prop);
            }

            //- WARN Levelでのログ出力
            getBootLogger().setLevel(Level.ALL);

            //- 基本情報を出力する
            getBootLogger().info("boot.time = " + DateFormat.getDateTimeInstance().format(new Date()));
            getBootLogger().info("os.name = " + System.getProperty("os.name"));
            getBootLogger().info("java.version = " + System.getProperty("java.version"));
            getBootLogger().info("version = " + getVersion());
            getBootLogger().info("base.directory = " + getBaseDirectory());
            getBootLogger().info("setting.directory = " + getSettingDirectory());
            getBootLogger().info("log.directory = " + getLogDirectory());
            getBootLogger().info("pdf.directory = " + getPDFDirectory());
            getBootLogger().info("schema.directory = " + getSchemaDirectory());
            getBootLogger().info("temp.directory = " + getTempDirectory());
            getBootLogger().info("log.config.file = " + getString("client.context.log4j.file"));
            getBootLogger().info("veleocity.log.file = " + getString("client.context.log4j.file.velocity"));

            //- Velocityを初期化する
            StringBuilder sb = new StringBuilder();
            sb.append(getLogDirectory());
            sb.append(File.separator);
            sb.append(getString("client.context.log4j.file.velocity"));
            Velocity.setProperty("runtime.log", sb.toString());
            Velocity.init();
            getBootLogger().debug("Velocity did initialize");

        } catch (DolphinException | IOException e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    private String createDirectory(String parent, String child) throws DolphinException {

        File dir = new File(parent, child);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                StringBuilder sb = new StringBuilder();
                sb.append("ディレクトリ");
                sb.append(parent).append(File.separator).append(child);
                sb.append("を作成できません。");
                throw new DolphinException(sb.toString());
            }
        }
        return dir.getPath();
    }

    private String pathToLogFile(String logFile) {
        StringBuilder sb = new StringBuilder();
        if (isMac() || isLinux()) {
            sb.append(getLogDirectory()).append("/").append(logFile);
        } else {
            sb.append(getLogDirectory()).append("\\").append(logFile);
        }
        return sb.toString();
    }

    public LinkedHashMap<String, String> getToolProviders() {
        return toolProviders;
    }

    public VelocityContext getVelocityContext() {
        return new VelocityContext();
    }

    public ResourceBundle getBundle(Class cls) {
        String path = getClassResource(cls);
        // No cache
        return ResourceBundle.getBundle(path, Locale.getDefault(), cls.getClassLoader(), new ResourceBundle.Control() {

            @Override
            public long getTimeToLive(String baseName, Locale locale) {
                return ResourceBundle.Control.TTL_DONT_CACHE;
            }
        });
    }

    private static String getClassResource(Class cls) {
        StringBuilder sb = new StringBuilder();
        String clsName = cls.getName();
        int index = clsName.lastIndexOf(".");
        sb.append(clsName.subSequence(0, index));
        sb.append(".resources.");
        sb.append(clsName.substring(index + 1));
        return sb.toString();
    }

    public Logger getBootLogger() {
        return Logger.getLogger(getString("client.context.logger.name.boot"));
    }

    public Logger getPart11Logger() {
        return Logger.getLogger(getString("client.context.logger.name.part11"));
    }

    public Logger getClaimLogger() {
        return Logger.getLogger(getString("client.context.logger.name.claim"));
    }

    public Logger getMmlLogger() {
        return Logger.getLogger(getString("client.context.logger.name.mml"));
    }

    public Logger getPvtLogger() {
        return Logger.getLogger(getString("client.context.logger.name.pvt"));
    }

    public Logger getDelegaterLogger() {
        return Logger.getLogger(getString("client.context.logger.name.delegater"));
    }

    public Logger getLaboTestLogger() {
        return Logger.getLogger(getString("client.context.logger.name.labTest"));
    }

    public boolean isMac() {
        return System.getProperty("os.name").
                toLowerCase().startsWith(getString("client.context.os.mac"));
    }

    public boolean isWin() {
        return System.getProperty("os.name").
                toLowerCase().startsWith(getString("client.context.os.win"));
    }

    public boolean isLinux() {
        return System.getProperty("os.name").
                toLowerCase().startsWith(getString("client.context.os.linux"));
    }

    private String getLocation(String dirName) {
        StringBuilder sb = new StringBuilder();
        sb.append(getBaseDirectory()).append(File.separator).append(dirName);
        return sb.toString();
    }

    public String getBaseDirectory() {
        return pathToEvo;
    }

    public String getSettingDirectory() {
        return getLocation(getString("client.context.directory.setting"));
    }

    public String getLogDirectory() {
        return getLocation(getString("client.context.directory.log"));
    }

    public String getPDFDirectory() {
        return getLocation(getString("client.context.directory.pdf"));
    }

    public String getProductName() {
        return getString("product.name");
    }

    public String getSchemaDirectory() {
        return getLocation(getString("client.context.directory.schema"));
    }

    public String getDefaultSchemaDirectory() {
        return getString("client.context.schema.path");
    }

    public String getOdtTemplateDirectory() {
        return getLocation(getString("client.context.directory.odt_template"));
    }

    public String getTempDirectory() {
        return getLocation(getString("client.context.directory.temp"));
    }

    //-----------------------------------------------------------
    public String getVersion() {
        return getString("product.version");
    }

    public String getFrameTitle(String title) {
        try {
            String resTitle = getString(title);
            if (resTitle != null) {
                title = resTitle;
            }
        } catch (Exception e) {
        }
        StringBuilder buf = new StringBuilder();
        buf.append(title);
        buf.append(getString("client.context.title.concat"));
        buf.append(getString("product.name"));
        buf.append(getString("client.context.title.concat"));
        buf.append(getString("product.version"));
        return buf.toString();
    }

    public URL getResource(String name) {
        if (!name.startsWith("/")) {
            name = getString("client.context.resource.path") + name;
        }
        return this.getClass().getResource(name);
    }

    public URL getImageResource(String name) {
        if (!name.startsWith("/")) {
            name = getString("client.context.image.path") + name;
        }
        return this.getClass().getResource(name);
    }

    public InputStream getResourceAsStream(String name) {
        if (!name.startsWith("/")) {
            name = getString("client.context.resource.path") + name;
        }
        return this.getClass().getResourceAsStream(name);
    }

    public InputStream getTemplateAsStream(String name) {
        if (!name.startsWith("/")) {
            name = getString("client.context.template.path") + name;
        }
        return this.getClass().getResourceAsStream(name);
    }

    public ImageIcon getImageIcon(String name) {
        if (name != null) {
            return new ImageIcon(getImageResource(name));
        }
        return null;
    }

    public ImageIcon getSchemaIcon(String name) {
        if (!name.startsWith("/")) {
            name = prpGt.get("client.context.schema.path") + name;
        }
        return new ImageIcon(this.getClass().getResource(name));
    }

    public LicenseModel[] getLicenseModel() {
        String[] desc = getStringArray("licenseDesc");
        String[] code = getStringArray("license");
        String codeSys = getString("licenseCodeSys");
        LicenseModel[] ret = new LicenseModel[desc.length];
        LicenseModel model;
        for (int i = 0; i < desc.length; i++) {
            model = new LicenseModel();
            model.setLicense(code[i]);
            model.setLicenseDesc(desc[i]);
            model.setLicenseCodeSys(codeSys);
            ret[i] = model;
        }
        return ret;
    }

    public DepartmentModel[] getDepartmentModel() {
        String[] desc = getStringArray("departmentDesc");
        String[] code = getStringArray("department");
        String codeSys = getString("departmentCodeSys");
        DepartmentModel[] ret = new DepartmentModel[desc.length];
        DepartmentModel model;
        for (int i = 0; i < desc.length; i++) {
            model = new DepartmentModel();
            model.setDepartment(code[i]);
            model.setDepartmentDesc(desc[i]);
            model.setDepartmentCodeSys(codeSys);
            ret[i] = model;
        }
        return ret;
    }

    public DiagnosisOutcomeModel[] getDiagnosisOutcomeModel() {
        String[] desc = getStringArray("diagnosis.outcomeDesc");
        String[] code = getStringArray("diagnosis.outcome");
        String codeSys = getString("diagnosis.outcomeCodeSys");
        DiagnosisOutcomeModel[] ret = new DiagnosisOutcomeModel[desc.length];
        DiagnosisOutcomeModel model;
        for (int i = 0; i < desc.length; i++) {
            model = new DiagnosisOutcomeModel();
            model.setOutcome(code[i]);
            model.setOutcomeDesc(desc[i]);
            model.setOutcomeCodeSys(codeSys);
            ret[i] = model;
        }
        return ret;
    }

    public DiagnosisCategoryModel[] getDiagnosisCategoryModel() {
        String[] desc = getStringArray("diagnosis.outcomeDesc");
        String[] code = getStringArray("diagnosis.outcome");
        String[] codeSys = getStringArray("diagnosis.outcomeCodeSys");
        DiagnosisCategoryModel[] ret = new DiagnosisCategoryModel[desc.length];
        DiagnosisCategoryModel model;
        for (int i = 0; i < desc.length; i++) {
            model = new DiagnosisCategoryModel();
            model.setDiagnosisCategory(code[i]);
            model.setDiagnosisCategoryDesc(desc[i]);
            model.setDiagnosisCategoryCodeSys(codeSys[i]);
            ret[i] = model;
        }
        return ret;
    }

    public NameValuePair[] getNameValuePair(String key) {
        NameValuePair[] ret;
        String[] code = getStringArray(key + ".value");
        String[] name = getStringArray(key + ".name");
        int len = code.length;
        ret = new NameValuePair[len];

        for (int i = 0; i < len; i++) {
            ret[i] = new NameValuePair(name[i], code[i]);
        }
        return ret;
    }

    public HashMap<String, Color> getEventColorTable() {
        if (eventColorTable == null) {
            setupEventColorTable();
        }
        return eventColorTable;
    }

    private void setupEventColorTable() {
        // イベントカラーを定義する
        eventColorTable = new HashMap<>(10, 0.75f);
        eventColorTable.put("TODAY", getColor("color.TODAY_BACK"));
        eventColorTable.put("BIRTHDAY", getColor("color.BIRTHDAY_BACK"));
        eventColorTable.put("PVT", getColor("color.PVT"));
        eventColorTable.put("DOC_HISTORY", getColor("color.PVT"));
    }

    public String getString(String key) {
        return prpGt.get(key);
    }

    public String[] getStringArray(String key) {
        String line = getString(key);
        return line.split(",");
    }

    public int getInt(String key) {
        return Integer.parseInt(getString(key));
    }

    public int[] getIntArray(String key) {
        String[] obj = getStringArray(key);
        int[] ret = new int[obj.length];
        for (int i = 0; i < obj.length; i++) {
            ret[i] = Integer.parseInt(obj[i]);
        }
        return ret;
    }

    public long getLong(String key) {
        return Long.parseLong(getString(key));
    }

    public long[] getLongArray(String key) {
        String[] obj = getStringArray(key);
        long[] ret = new long[obj.length];
        for (int i = 0; i < obj.length; i++) {
            ret[i] = Long.parseLong(obj[i]);
        }
        return ret;
    }

    public float getFloat(String key) {
        return Float.parseFloat(getString(key));
    }

    public float[] getFloatArray(String key) {
        String[] obj = getStringArray(key);
        float[] ret = new float[obj.length];
        for (int i = 0; i < obj.length; i++) {
            ret[i] = Float.parseFloat(obj[i]);
        }
        return ret;
    }

    public double getDouble(String key) {
        return Double.parseDouble(getString(key));
    }

    public double[] getDoubleArray(String key) {
        String[] obj = getStringArray(key);
        double[] ret = new double[obj.length];
        for (int i = 0; i < obj.length; i++) {
            ret[i] = Double.parseDouble(obj[i]);
        }
        return ret;
    }

    public boolean getBoolean(String key) {
        return Boolean.valueOf(getString(key)).booleanValue();
    }

    public boolean[] getBooleanArray(String key) {
        String[] obj = getStringArray(key);
        boolean[] ret = new boolean[obj.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = Boolean.valueOf(obj[i]).booleanValue();
        }
        return ret;
    }

    public Point lgetPoint(String name) {
        int[] data = getIntArray(name);
        return new Point(data[0], data[1]);
    }

    public Dimension getDimension(String name) {
        int[] data = getIntArray(name);
        return new Dimension(data[0], data[1]);
    }

    public Insets getInsets(String name) {
        int[] data = getIntArray(name);
        return new Insets(data[0], data[1], data[2], data[3]);
    }

    public Color getColor(String key) {
        int[] data = getIntArray(key);
        return new Color(data[0], data[1], data[2]);
    }

    public Color[] getColorArray(String key) {
        int[] data = getIntArray(key);
        int cnt = data.length / 3;
        Color[] ret = new Color[cnt];
        for (int i = 0; i < cnt; i++) {
            int bias = i * 3;
            ret[i] = new Color(data[bias], data[bias + 1], data[bias + 2]);
        }
        return ret;
    }

    public Class[] getClassArray(String name) {
        String[] clsStr = getStringArray(name);
        Class[] ret = new Class[clsStr.length];
        try {
            for (int i = 0; i < clsStr.length; i++) {
                ret[i] = Class.forName(clsStr[i]);
            }
            return ret;

        } catch (ClassNotFoundException e) {
            e.printStackTrace(System.err);
        }
        return null;
    }

    public int getHigherRowHeight() {
        return 20;
    }

    public int getMoreHigherRowHeight() {
        if (isMac()) {
            return 20;
        }
        String nimbus = "javax.swing.plaf.metal.MetalLookAndFeel";
        String laf = UIManager.getLookAndFeel().getClass().getName();
        if (laf.equals(nimbus)) {
            return 25;
        }

        return 20;
    }

    private void listJars(List list, File dir) {
        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                listJars(list, file);
            } else if (file.isFile()) {
                String path = file.getPath();
                if (path.toLowerCase().endsWith(".jar")) {
                    list.add(path);
                }
            }
        }
    }

    /**
     * LookAndFeel、フォント、Mac メニューバー使用を設定する。
     */
    public void setupUI() {

        try {
            Font font = null;
            int size = 13;
            if (isWin() || isLinux()) {
                size = isLinux() ? 13 : 12;
            }

            if (isMac()) {
                System.setProperty("apple.laf.useScreenMenuBar", String.valueOf(true));
                UIManager.put("OptionPane.cancelButtonText", "キャンセル");
                UIManager.put("OptionPane.okButtonText", "OK");
                font = new Font("Hiragino Kaku Gothic", Font.PLAIN, size);

            } else if (isWin()) {
                font = new Font("MSGothic", Font.PLAIN, size);

            } else {
                font = new Font("Lucida Grande", Font.PLAIN, size);
            }

            if (isWin() || isLinux()) {
                //- 強制 Metal
//                if (Project.getString("lookAndFeel") != null) {
//                    UIManager.setLookAndFeel(Project.getString("lookAndFeel"));
//                } else {
//                    // Default=NimbusLookAndFeel
//                    String nimbusCls = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
//                    UIManager.setLookAndFeel(nimbusCls);
//                }

                String nimbusCls = "javax.swing.plaf.metal.MetalLookAndFeel";
                UIManager.setLookAndFeel(nimbusCls);
            }

            // ToolBarの Dropdown menu制御
            UIManager.put("PopupMenu.consumeEventOnClose", Boolean.TRUE);

            // Font 設定
            FontUIResource fontUIResource = new FontUIResource(font);
            UIManager.put("Label.font", fontUIResource);
            UIManager.put("Button.font", fontUIResource);
            UIManager.put("ToggleButton.font", fontUIResource);
            UIManager.put("Menu.font", fontUIResource);
            UIManager.put("MenuItem.font", fontUIResource);
            UIManager.put("CheckBox.font", fontUIResource);
            UIManager.put("CheckBoxMenuItem.font", fontUIResource);
            UIManager.put("RadioButton.font", fontUIResource);
            UIManager.put("RadioButtonMenuItem.font", fontUIResource);
            UIManager.put("ToolBar.font", fontUIResource);
            UIManager.put("ComboBox.font", fontUIResource);
            UIManager.put("TabbedPane.font", fontUIResource);
            UIManager.put("TitledBorder.font", fontUIResource);
            UIManager.put("List.font", fontUIResource);
//minagawa^ mak jdk7 で : が表示されない            
//UIManager.put("TextField.font", fontUIResource);
//UIManager.put("TextArea.font", fontUIResource);
//minagawa$            
            UIManager.put("TextPane.font", fontUIResource);
//masuda先生^ tweet            
            if (UIManager.getLookAndFeel().getName().toLowerCase().startsWith("nimbus")) {
                UIManager.put("TextPaneUI", BasicTextPaneUI.class.getName());
                UIManager.put("TextPane.selectionBackground", new Color(57, 105, 138));
                UIManager.put("TextPane.selectionForeground", Color.WHITE);
                //UIManager.put("TextPane.border", new EmptyBorder(4, 6, 4, 6));
                UIManager.put("TextPane.border", new EmptyBorder(2, 2, 2, 2));
            }
//masuda$                 
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace(System.err);
            getBootLogger().warn(e.getMessage());
        }
//        // List up fonts
//        String fonts[] = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
//        for (int i = 0; i < fonts.length; i++) {
//            System.out.println(fonts[i]);
//        }

//        for (java.util.Map.Entry<?, ?> entry : UIManager.getDefaults().entrySet()) {
//            System.err.println(UIManager.get(entry.getKey()));
//        }
//        font = new Font("SansSerif", Font.PLAIN, size);
//        } else {
//            //font = new Font("Hiragino Kaku Gothic", Font.PLAIN, size);
//            font = new Font("Lucida Grande", Font.PLAIN, size);
//        }
//        font = new Font("Lucida Grande", Font.PLAIN, size);
//        for (java.util.Map.Entry<?, ?> entry : UIManager.getDefaults().entrySet()) {
//            if (entry.getKey().toString().toLowerCase().endsWith("font")) {
//                System.err.println(entry.getKey());
//                UIManager.put(entry.getKey(), fontUIResource);
//            }
//        }
        getBootLogger().debug("デフォルトのフォントを変更しました。");
    }
    
//minagawa^ Icon Server
    public ImageIcon getImageIconArias(String name) {
        return this.getImageIcon(getString(name));
    }
//minagawa$    
}