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

import java.util.concurrent.Callable;
import javafx.embed.swing.JFXPanel;

/**
 * Main Window プラグインの抽象クラス。
 * 来院リスト、患者検索、ラボレシーバ等
 * 具象クラスは start()、stop() を実装する。
 */
public abstract class AbstractMainComponent implements MainComponent {
    
    private String name;
    
    private String icon;
    
    private MainWindow context;
    
    private JFXPanel ui;
    
    private DisplayCtrlController dispWinCtrl;
    
    public AbstractMainComponent() {
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String getIcon() {
        return icon;
    }
    
    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    @Override
    public MainWindow getContext() {
        return context;
    }
    
    @Override
    public void setContext(MainWindow context) {
        this.context = context;
    }
    
    @Override
    public JFXPanel getUI() {
        return ui;
    }
    
    @Override
    public void setUI(JFXPanel ui) {
        this.ui = ui;
    }
    
    @Override
    public void enter() {
    }
    
    @Override
    public Callable<Boolean> getStartingTask() {
        return null;
    }
    
    @Override
    public Callable<Boolean> getStoppingTask() {
        return null;
    }
    
    @Override
    public abstract void start();
    
    @Override
    public abstract void stop();
    
    @Override
    public void setCtrlView(DisplayCtrlController ctrl) {
        this.dispWinCtrl = ctrl;
    }
    
    @Override
    public DisplayCtrlController getCtrlView() {
        return dispWinCtrl;
    }
    
}
