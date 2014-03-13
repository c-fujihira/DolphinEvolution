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
package open.dolphin.helper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * SimpleWorker
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 * @param <T>
 * @param <Void>
 */
public abstract class SimpleWorker<T, Void> extends SwingWorker<T, Void> {

    private static final String STATE = "state";
    private static final String PROGRESS = "progress";
    PropertyChangeListener pcl;

    public SimpleWorker() {

        pcl = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent pce) {
                switch (pce.getPropertyName()) {
                    case STATE:
                        if (SwingWorker.StateValue.STARTED == pce.getNewValue()) {
                            startProgress();
                        } else if (SwingWorker.StateValue.DONE == pce.getNewValue()) {
                            stopProgress();
                            SimpleWorker.this.removePropertyChangeListener(pcl);
                        }   break;
                    case PROGRESS:
                        progress(((Integer) pce.getNewValue()).intValue());
                        break;
                }
            }
        };
        this.addPropertyChangeListener(pcl);
    }

    protected void startProgress() {
    }

    protected void stopProgress() {
    }

    protected void progress(int value) {
    }

    protected void succeeded(T result) {
    }

    protected void cancelled() {
    }

    protected void failed(Throwable cause) {
    }

    protected void interrupted(Throwable cause) {
    }

    @Override
    protected void done() {

        if (isCancelled()) {
            cancelled();
            return;
        }

        try {

            succeeded((T) get());

        } catch (InterruptedException ex) {
            interrupted(ex);

        } catch (ExecutionException ex) {
            failed(ex);
        }
    }
}
