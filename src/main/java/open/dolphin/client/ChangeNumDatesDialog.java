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

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.EventHandler;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 */
public final class ChangeNumDatesDialog {

    private final JButton chagneBtn;
    private final JButton cancelBtn;
    private ChangeNumDatesView view;
    private final JDialog dialog;
    private final PropertyChangeSupport boundSupport;

    public ChangeNumDatesDialog(JFrame parent, PropertyChangeListener pcl) {

        // view
        view = new ChangeNumDatesView();
        String pattern = "^[1-9][0-9]*$";
        RegexConstrainedDocument numReg = new RegexConstrainedDocument(pattern);
        view.getNumDatesFld().setDocument(numReg);

        // OK button
        chagneBtn = new JButton("変更");
        chagneBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, ChangeNumDatesDialog.this, "doOk"));
        chagneBtn.setEnabled(false);

        // Cancel Button
//minagawa^ mac jdk7        
//        String buttonText =  (String)UIManager.get("OptionPane.cancelButtonText");
        String buttonText =  GUIFactory.getCancelButtonText();
//minagawa$        
        cancelBtn = new JButton(buttonText);
        cancelBtn.addActionListener((ActionListener) EventHandler.create(ActionListener.class, ChangeNumDatesDialog.this, "doCancel"));

        // Listener
        view.getNumDatesFld().getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent de) {
                checkInput();
            }
            @Override
            public void removeUpdate(DocumentEvent de) {
                checkInput();
            }
            @Override
            public void changedUpdate(DocumentEvent de) {
            }
        });

        //- IME Off
        view.getNumDatesFld().addFocusListener(AutoRomanListener.getInstance());
        
        Object[] options = new Object[]{chagneBtn, cancelBtn};

        JOptionPane jop = new JOptionPane(
                view,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                options,
                chagneBtn);

        dialog = jop.createDialog(parent, ClientContext.getFrameTitle("処方日数変更"));
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                view.getNumDatesFld().requestFocus();
            }
            @Override
            public void windowClosing(WindowEvent e) {
                doCancel();
            }
        });

        boundSupport = new PropertyChangeSupport(this);
        boundSupport.addPropertyChangeListener(pcl);
    }

    public void show() {
        dialog.setVisible(true);
    }

    public void doOk() {
        try {
            int number = Integer.parseInt(view.getNumDatesFld().getText().trim());
            boundSupport.firePropertyChange("newNumDates", -1, number);
            close();
        } catch (Throwable e) {
            e.printStackTrace(System.err);
        }
    }

    public void doCancel() {
        boundSupport.firePropertyChange("newNumDates", -1, 0);
        close();
    }

    private void close() {
        dialog.setVisible(false);
        dialog.dispose();
    }

    private void checkInput() {
        String test = view.getNumDatesFld().getText().trim();
        boolean ok = true;
        ok = ok && (!test.equals(""));
        chagneBtn.setEnabled(ok);
    }
}
