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
package open.dolphin.project;

import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JPanel;
import open.dolphin.client.ClientContext;
import org.apache.log4j.Logger;

/**
 * AbstractSettingPanel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public abstract class AbstractSettingPanel {

    public static final String STATE_PROP = "stateProp";

    public enum State {

        NONE_STATE, VALID_STATE, INVALID_STATE
    };

//s.oh^ 機能改善
    private static final int UI_WIDTH_MIN = 10;
    private static final int UI_HEIGHT_MIN = 10;
//s.oh$

    private ProjectSettingDialog context;
    private ProjectStub projectStub;
    private PropertyChangeSupport boundSupport;
    protected AbstractSettingPanel.State state = AbstractSettingPanel.State.NONE_STATE;
    private JPanel ui;
    private boolean loginState;
    private String title;
    private String icon;
    private String id;

    Logger logger;

    /**
     * Creates a new instance of SettingPanel
     */
    public AbstractSettingPanel() {
        ui = new JPanel();
        logger = ClientContext.getBootLogger();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ProjectSettingDialog getContext() {
        return context;
    }

    public void setContext(ProjectSettingDialog context) {
        this.context = context;
        this.addPropertyChangeListener(STATE_PROP, context);
        this.setLogInState(context.getLoginState());
    }

    public boolean isLoginState() {
        return loginState;
    }

    public void setLogInState(boolean login) {
        loginState = login;
    }

    public JPanel getUI() {
        return ui;
    }

    public void setUI(JPanel p) {
        ui = p;
//s.oh^ 機能改善
        if (ui != null) {
            ui.setMinimumSize(new Dimension(UI_WIDTH_MIN, UI_HEIGHT_MIN));
        }
//s.oh$
    }

    public abstract void start();

    public abstract void save();

    public void addPropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(prop, l);
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener l) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(prop, l);
    }

    public ProjectStub getProjectStub() {
        return projectStub;
    }

    public void setProjectStub(ProjectStub projectStub) {
        this.projectStub = projectStub;
    }

    protected void setState(AbstractSettingPanel.State state) {
        this.state = state;
        boundSupport.firePropertyChange(STATE_PROP, null, this.state);
    }

    protected AbstractSettingPanel.State getState() {
        return state;
    }
}
