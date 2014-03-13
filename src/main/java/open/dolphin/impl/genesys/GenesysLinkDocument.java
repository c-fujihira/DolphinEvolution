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
package open.dolphin.impl.genesys;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import open.dolphin.client.AbstractChartDocument;
import open.dolphin.client.GUIConst;
import open.dolphin.project.Project;

/**
 * ジェネシス連携
 *
 * @author Life Sciences Computing Corporation.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class GenesysLinkDocument extends AbstractChartDocument {

    private static final String TITLE = "Genesys";
    private static final String APPLICATION_NAME = "dolphin";
    private static final String KEY_TITLE = "genesys.browser.title";
    private static final String KEY_SERVER = "genesys.browser.server";
    public static final String KEY_GENESYSBROWSER = "genesys.browser";
    public static final String VAL_GENESYS = "genesys";
    private GenesysLinkPanel splitPane;

    public GenesysLinkDocument() {
        String title = Project.getString(KEY_TITLE);
        if (title == null || title.length() <= 0) {
            setTitle(TITLE);
        } else {
            setTitle(title);
        }
    }

    @Override
    public void start() {
        initialize();
    }

    @Override
    public void stop() {
    }

    @Override
    public void enter() {
        super.enter();
        getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, false);
    }

    private void initialize() {
        splitPane = new GenesysLinkPanel(Project.getString(KEY_SERVER), Project.getUserId(), getContext().getPatient().getPatientId(), APPLICATION_NAME);
        getUI().setLayout(new BorderLayout());
        getUI().add(splitPane, BorderLayout.CENTER);
        getUI().setBorder(BorderFactory.createEmptyBorder(12, 12, 11, 11));
        splitPane.reDividerLocation(getUI().getHeight());
    }
}
