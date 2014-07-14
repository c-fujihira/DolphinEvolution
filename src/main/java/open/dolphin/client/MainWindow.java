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

import java.awt.Component;
import java.awt.print.PageFormat;
import java.util.HashMap;
import javax.swing.*;
import open.dolphin.helper.MenuSupport;
import open.dolphin.infomodel.PatientVisitModel;

/**
 * アプリケーションのメインウインドウインターフェイスクラス。
 *
 * @author Minagawa, Kazushi. Digital Globe, Inc.
 */
public interface MainWindow {
    
    public HashMap<String, MainService> getProviders();
    
    public void setProviders(HashMap<String, MainService> providers);
    
    public JMenuBar getMenuBar();
    
    public MenuSupport getMenuSupport();
    
    public void registerActions(ActionMap actions);
    
    public Action getAction(String name);
    
    public void enabledAction(String name, boolean b);
    
    public void openKarte(PatientVisitModel pvt);
    
    public void addNewPatient();
    
    public void block();
    
    public void unblock();
    
    public BlockGlass getGlassPane();
    
    public MainService getPlugin(String name);
    
    public PageFormat getPageFormat();
    
    public void showStampBox();
    
    public void showSchemaBox();

    public JLabel getStatusLabel();

    public JProgressBar getProgressBar();

    public JLabel getDateLabel();

    public JTabbedPane getTabbedPane();

    public Component getCurrentComponent();
    
    public void setTrace(String trace);
}
