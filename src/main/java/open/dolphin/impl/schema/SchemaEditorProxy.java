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
package open.dolphin.impl.schema;

import java.beans.PropertyChangeListener;
import open.dolphin.client.SchemaEditor;
import open.dolphin.infomodel.SchemaModel;
import open.dolphin.project.Project;

/**
 * SchemaEditorProxy
 *
 * @author kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class SchemaEditorProxy implements SchemaEditor {

    private SchemaEditor editor;

    ;

    @Override
    public void setEditable(boolean b) {
        getEditor().setEditable(b);
    }

    @Override
    public void setSchema(SchemaModel model) {
        getEditor().setSchema(model);
    }

    @Override
    public void start() {
        getEditor().start();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        getEditor().addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        getEditor().removePropertyChangeListener(l);
    }

    // Factory
    private SchemaEditor getEditor() {
        if (editor == null) {
            // Projectに指定されているブラウザを生成する
            String name = Project.getString("schema.editor.name");

            if (name == null || name.equals("cool")) {
                editor = (SchemaEditor) create("open.dolphin.impl.scheam.SchemaEditorImpl"); // Scheam

            } else {
                editor = (SchemaEditor) create("open.dolphin.impl.schema.SchemaEditorImpl"); // Schema
            }
        }
        return editor;
    }

    private Object create(String clsName) {
        try {
            return Class.forName(clsName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }
}
