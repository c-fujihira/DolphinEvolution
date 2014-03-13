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
package open.dolphin.impl.scheam;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import open.dolphin.infomodel.ExtRefModel;
import open.dolphin.infomodel.SchemaModel;

/**
 * DrawTest
 * 
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class DrawTest {

    public static void main (String[] args) {
        new DrawTest().startup();
    }

    protected void startup() {

        //boolean QUAQUA = true;
        boolean QUAQUA = false;

        if (QUAQUA) {
            try {
                UIManager.setLookAndFeel("ch.randelshofer.quaqua.QuaquaLookAndFeel");
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                Logger.getLogger(DrawTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        SchemaEditorImpl editor = new SchemaEditorImpl();
        SchemaModel schema = new SchemaModel();
        String sample1 = "/open/dolphin/impl/scheam/resources/Sample-square.JPG";
        String sample2 = "/open/dolphin/impl/scheam/resources/Sample-large.JPG";
        String sample3 = "/open/dolphin/impl/scheam/resources/Sample-landscape.JPG";
        String sample4 = "/open/dolphin/impl/scheam/resources/Sample-portrait.JPG";

        InputStream in = getClass().getResourceAsStream(sample2);

        byte[] buf = null;
        try {
            int n = in.available();
            buf = new byte[n];
            for(int i=0; i<n; i++) buf[i] = (byte) in.read();
        } catch (IOException ex) {
        }
        schema.setIcon(new ImageIcon(buf));

        ExtRefModel ref = new ExtRefModel();
        ref.setContentType("image/jpeg");
        ref.setTitle("Schema Image");
        schema.setExtRefModel(ref);
        schema.setFileName("Test");
        ref.setHref("Test");

        editor.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
//                System.out.println("oldValue = " + evt.getOldValue());
//                System.out.println("newValue = " + evt.getNewValue());
            }
        });

        editor.setSchema(schema);
        editor.setEditable(true);
        editor.start();
    }
}
