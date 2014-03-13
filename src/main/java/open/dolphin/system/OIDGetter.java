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
package open.dolphin.system;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import open.dolphin.client.ClientContext;
import open.dolphin.util.Log;

/**
 * OIDRequester
 *
 * @author Minagawa,Kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class OIDGetter extends JPanel {

    public static final String NEXT_OID_PROP = "nextOidProp";
    private static final String PROGRESS_NOTE = "通信テストをしています...";
    private static final String SUCCESS_NOTE = "通信に成功しました。次項ボタンをクリックし次に進むことができます。";
    private static final String TASK_TITLE = "通信テスト";

    private String helloReply;
    private PropertyChangeSupport boundSupport = new PropertyChangeSupport(this);
    private OidTask task;
    private PropertyChangeListener pl;

    private JProgressBar bar;
    private JDialog progressDialog;

    private JButton comTest = new JButton(TASK_TITLE);

    public OIDGetter() {
        initialize();
        connect();
    }

    public String getHelloReply() {
        return helloReply;
    }

    public void setHelloReply(String oid) {
        helloReply = oid;
        boundSupport.firePropertyChange(NEXT_OID_PROP, "", helloReply);
    }

    public void addOidPropertyListener(PropertyChangeListener l) {
        boundSupport.addPropertyChangeListener(NEXT_OID_PROP, l);
    }

    public void removeOidPropertyListener(PropertyChangeListener l) {
        boundSupport.removePropertyChangeListener(NEXT_OID_PROP, l);
    }

    private void initialize() {

        try {
            InputStream in = this.getClass().getResourceAsStream("/open/dolphin/system/account-make-info.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            JTextArea infoArea = new JTextArea();
            infoArea.setEditable(false);
            infoArea.setLineWrap(true);
            infoArea.setMargin(new Insets(10, 10, 10, 10));
            infoArea.setText(sb.toString());
            JScrollPane scroller = new JScrollPane(infoArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            btnPanel.add(new JLabel("次のボタンをクリックし、通信できるかどうか確認してください。"));
            btnPanel.add(comTest);

            this.setLayout(new BorderLayout());
            this.add(scroller, BorderLayout.CENTER);
            this.add(btnPanel, BorderLayout.SOUTH);

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void connect() {
        comTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doTest();
            }
        });
    }

    private void doTest() {

        task = new OidTask();
        pl = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("state".equals(evt.getPropertyName())) {
                    if (SwingWorker.StateValue.DONE == evt.getNewValue()) {
                        stopProgress();
                    } else if (SwingWorker.StateValue.STARTED == evt.getNewValue()) {
                        startProgress();
                    }
                }
            }
        };
        task.addPropertyChangeListener(pl);

        task.execute();
    }

    private void startProgress() {
        bar = new JProgressBar(0, 100);
        Object[] message = new Object[]{PROGRESS_NOTE, bar};
        JButton cancel = new JButton((String) UIManager.get("OptionPane.cancelButtonText"));
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                task.cancel(true);
            }
        });
        JOptionPane pane = new JOptionPane(
                message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                null,
                new Object[]{cancel});

        String title = ClientContext.getFrameTitle(TASK_TITLE);
        Component c = SwingUtilities.getWindowAncestor(this);
        progressDialog = pane.createDialog(c, title);
        progressDialog.setModal(false);
        bar.setIndeterminate(true);
        progressDialog.setVisible(true);
    }

    private void stopProgress() {
        task.removePropertyChangeListener(pl);
    }

    class OidTask extends SwingWorker<String, Void> {

        @Override
        protected String doInBackground() throws Exception {
            SystemDelegater sdl = new SystemDelegater();
            String ret = sdl.hellow();
            return ret;
        }

        @Override
        public void done() {
            bar.setIndeterminate(false);
            progressDialog.setVisible(false);
            if (isCancelled()) {
                return;
            }
            try {
                String result = get();
                succeeded(result);
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            } catch (ExecutionException ex) {
                failed(ex);
            }
        }

        protected void succeeded(String result) {
            Window myParent = SwingUtilities.getWindowAncestor(OIDGetter.this);
            String title = ClientContext.getFrameTitle(TASK_TITLE);
            JOptionPane.showMessageDialog(myParent, SUCCESS_NOTE, title, JOptionPane.INFORMATION_MESSAGE);
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, title, SUCCESS_NOTE);
            setHelloReply(result);
        }

        protected void failed(Throwable cause) {
            String errMsg = cause.getMessage();
            Window myParent = SwingUtilities.getWindowAncestor(OIDGetter.this);
            String title = ClientContext.getFrameTitle(TASK_TITLE);
            JOptionPane.showMessageDialog(myParent, errMsg, title, JOptionPane.WARNING_MESSAGE);
            Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, title, errMsg);
            setHelloReply(null);
        }
    }
}
