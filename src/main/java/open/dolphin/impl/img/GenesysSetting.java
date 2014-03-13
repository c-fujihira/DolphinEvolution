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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.*;
import open.dolphin.client.ClientContext;

/**
 * GenesysSetting
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class GenesysSetting {

    private AbstractBrowser context;
    private Component parent;
    private GenesysConfigView view;
    private JDialog dialog;
    private Properties properties;

    public GenesysSetting(AbstractBrowser context, Component parent) {
        this.context = context;
        this.parent = parent;
        this.properties = context.properties;
    }

//    private String selectApp() {
//        JFileChooser chooser = new JFileChooser();
//        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        int returnVal = chooser.showOpenDialog(null);
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            String app = chooser.getSelectedFile().getPath();
//            return app;
//        }
//        return null;
//    }
    private ActionMap getAction(ResourceBundle resource) {

        ActionMap ret = new ActionMap();

        String text = resource.getString("setBaseDirectory.Action.text");
        AbstractAction setBaseDirectory = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    String dir = chooser.getSelectedFile().getPath();
                    view.getBaseDirFld().setText(dir);
                }
            }
        };
        ret.put("setBaseDirectory", setBaseDirectory);

        text = resource.getString("saveProperties.Action.text");
        AbstractAction saveProperties = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {

                // Base Directory
                setProperty("baseDir", view.getBaseDirFld());

                // Drop動作
                String dropAction = view.getCopyRadio().isSelected() ? "copy" : "move";
                properties.setProperty(AbstractBrowser.PROP_DROP_ACTION, dropAction);

                // カラム数
                String spiVal = view.getColumnSpinner().getValue().toString();
                properties.setProperty(AbstractBrowser.PROP_COLUMN_COUNT, spiVal);

                // ソート属性
                String sortAttr = view.getFilenameRadio().isSelected() ? "filename" : "lastModified";
                properties.setProperty(AbstractBrowser.PROP_SORT_ATTR, sortAttr);

                // ソート順
                String sortOrder = view.getAscRadio().isSelected() ? "asc" : "desc";
                properties.setProperty(AbstractBrowser.PROP_SORT_ORDER, sortOrder);

                // genesysServer
                setProperty("genesysServer", view.getGenesysServerFld());

                dialog.setVisible(false);
                dialog.dispose();
            }
        };
        ret.put("saveProperties", saveProperties);

        text = resource.getString("cancel.Action.text");
        AbstractAction cancel = new AbstractAction(text) {
            @Override
            public void actionPerformed(ActionEvent ae) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        };
        ret.put("cancel", cancel);

        return ret;
    }

    public void start() {

        view = new GenesysConfigView();

        // Base directory
        String baseDir = properties.getProperty(DefaultBrowser.PROP_BASE_DIR);
        view.getBaseDirFld().setText(baseDir);

        // Drop action
        boolean move = context.dropIsMove();
        view.getCopyRadio().setSelected(!move);
        view.getMoveRadio().setSelected(move);

        // カラム数
        int spiInt = context.columnCount();
        view.getColumnSpinner().setValue(new Integer(spiInt));

        // ソート属性
        boolean last = context.sortIsLastModified();
        view.getFilenameRadio().setSelected(!last);
        view.getLastRadio().setSelected(last);

        // ソート順
        boolean desc = context.sortIsDescending();
        view.getAscRadio().setSelected(!desc);
        view.getDescRadio().setSelected(desc);

        // Button group
        ButtonGroup actionRgp = new ButtonGroup();
        actionRgp.add(view.getCopyRadio());
        actionRgp.add(view.getMoveRadio());

        ButtonGroup attrRgp = new ButtonGroup();
        attrRgp.add(view.getFilenameRadio());
        attrRgp.add(view.getLastRadio());

        ButtonGroup orderRgp = new ButtonGroup();
        orderRgp.add(view.getAscRadio());
        orderRgp.add(view.getDescRadio());

        // GeneSys
        view.getGenesysServerFld().setText(properties.getProperty("genesysServer"));
        boolean enabled = true; //ClientContext.isWin();
        view.getGenesysServerFld().setEnabled(enabled);

        // Inject Actions
        ResourceBundle resource = ClientContext.getBundle(this.getClass());
        ActionMap map = getAction(resource);

        view.getBaseDirBtn().setAction(map.get("setBaseDirectory"));

        JButton saveBtn = new JButton();
        saveBtn.setAction(map.get("saveProperties"));

        JButton cancelBtn = new JButton();
        cancelBtn.setAction(map.get("cancel"));

        Object[] options = new Object[]{saveBtn, cancelBtn};

        JOptionPane jop = new JOptionPane(
                view,
                JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                saveBtn);

        dialog = jop.createDialog(parent, ClientContext.getFrameTitle("Genesys設定"));
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dialog.setVisible(false);
                dialog.dispose();
            }
        });
        dialog.setVisible(true);
    }

    private void setProperty(String name, JTextField tf) {
        String value = tf.getText().trim();
        if (!value.equals("")) {
            properties.setProperty(name, value);
        } else if (properties.containsKey(name)) {
            properties.remove(name);
        }
    }
}
