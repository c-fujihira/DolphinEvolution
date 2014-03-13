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

import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import open.dolphin.client.Chart;
import open.dolphin.client.ChartDocument;
import open.dolphin.client.ClientContext;
import open.dolphin.project.Project;
import open.dolphin.util.Log;

/**
 * ImageBrowser plugin の proxy class
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class ImageBrowserProxy implements ChartDocument {

    private ChartDocument browser;

    public ImageBrowserProxy() {
    }

    @Override
    public void start() {
        getBrowser().start();
        // menu control
        enter();
    }

    @Override
    public void stop() {
        getBrowser().stop();
    }

    @Override
    public String getTitle() {
        return getBrowser().getTitle();
    }

    @Override
    public void setTitle(String title) {
        getBrowser().setTitle(title);
    }

    @Override
    public ImageIcon getIconInfo(Chart ctx) {
        return getBrowser().getIconInfo(ctx);
    }

    @Override
    public Chart getContext() {
        return getBrowser().getContext();
    }

    @Override
    public void setContext(Chart ctx) {
        getBrowser().setContext(ctx);
    }

    @Override
    public JPanel getUI() {
        return getBrowser().getUI();
    }

    @Override
    public void enter() {
        getBrowser().enter();
    }

    @Override
    public void save() {
        getBrowser().save();
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, getTitle(), "保存成功。");
    }

    @Override
    public void print() {
        getBrowser().print();
    }

    @Override
    public boolean isDirty() {
        return getBrowser().isDirty();
    }

    @Override
    public void setDirty(boolean dirty) {
        getBrowser().setDirty(dirty);
    }

    private ChartDocument getBrowser() {
        if (browser == null) {
            // Projectに指定されているブラウザを生成する
            String name = Project.getString("image.browser.name");
            boolean win = ClientContext.isWin();
            //win = true; // test with Mac

            if (win && name != null && name.equals("genesys")) {
                browser = (ChartDocument) create("open.dolphin.impl.img.GenesysBrowser");

            } else if (win && name != null && name.equals("unitea")) {
                browser = (ChartDocument) create("open.dolphin.impl.img.UniteaBrowser");

            } else if (win && name != null && name.equals("tfs")) {
                browser = (ChartDocument) create("open.dolphin.impl.img.TFSBrowser");

//s.oh^ FCR連携/他プロセス連携/ジェネシス連携
                // FCR連携
            } else if (name != null && name.equals("fcr")) {
                browser = (ChartDocument) create("open.dolphin.impl.img.FCRBrowser");

                // 他プロセス連携
            } else if (name != null && name.equals("defaultex")) {
                browser = (ChartDocument) create("open.dolphin.impl.img.DefaultBrowserEx");

                // ジェネシス連携
            } else if (win && name != null && name.equals("genephin")) {
                browser = (ChartDocument) create("open.dolphin.impl.genesys.GenesysLinkDocument");

//s.oh$
            } else {
                browser = (ChartDocument) create("open.dolphin.impl.img.DefaultBrowser");
            }
        }
        return browser;
    }

    private Object create(String clsName) {
        try {
            return Class.forName(clsName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    @Override
    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        getBrowser().addPropertyChangeListener(prop, l);
    }

    @Override
    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        getBrowser().removePropertyChangeListener(prop, l);
    }

    @Override
    public boolean isChartDocDidSave() {
        return getBrowser().isChartDocDidSave();
    }

    @Override
    public void setChartDocDidSave(boolean b) {
        getBrowser().setChartDocDidSave(b);
    }
}
