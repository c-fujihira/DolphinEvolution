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
package open.dolphin.impl.server;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import open.dolphin.client.ClientContext;
import open.dolphin.delegater.PVTDelegater1;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.project.Project;
import open.dolphin.util.Log;

/**
 * PVTSender
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class PVTSender implements Runnable {

    private final List queue = new LinkedList();
    private Thread senderThread;

    public void startService() {
        senderThread = new Thread(this);
        senderThread.setPriority(Thread.NORM_PRIORITY);
        senderThread.start();
    }

    public void stopService() {
        if (senderThread != null) {
            Thread t = senderThread;
            senderThread = null;
            t.interrupt();
        }
    }

    public void processPvt(String pvtXml) {
        synchronized (queue) {
            queue.add(pvtXml);
            queue.notify();
        }
    }

    private void addPvt(String pvtXml) {
        BufferedReader r = new BufferedReader(new StringReader(pvtXml));
        PVTBuilder builder = new PVTBuilder();
        builder.parse(r);
        PatientVisitModel model = builder.getProduct();

        PVTDelegater1 pdl = new PVTDelegater1();
        try {
            pdl.addPvt(model);
            Log.outputOperLogOper(null, Log.LOG_LEVEL_0, "受付：", model.getPatientId());
        } catch (Exception e) {
        }

//s.oh^ 受付連携
        // ORCAローカル接続
        String receptKind = Project.getString(Project.CLAIM_SENDER);
        if (receptKind != null && receptKind.equals("client")) {
            PVTReceptionLink link = new PVTReceptionLink();
            if (Project.getBoolean("reception.csvlink", false)) {
                link.receptionCSVLink(model);
            }
            if (Project.getBoolean("reception.csvlink2", false)) {
                link.receptionCSVLink2(model);
            }
            if (Project.getBoolean("reception.csvlink3", false)) {
                link.receptionCSVLink3(model);
            }
            if (Project.getBoolean("reception.xmllink", false)) {
                link.receptionXMLLink(model);
            }
            if (Project.getBoolean("reception.link", false)) {
                link.receptionLink(model);
            }
            if (Project.getBoolean("receipt.link", false)) {
                link.receiptLink(model);
            }
//s.oh^ 2013/11/13 Claimの横流し
            if (Project.getBoolean("receipt.claim.write", false)) {
                link.claimWrite(pvtXml);
            }
//s.oh$
        }
//s.oh$
    }

    private String getPvt() throws InterruptedException {
        synchronized (queue) {
            while (queue.isEmpty()) {
                queue.wait();
            }
        }
        return (String) queue.remove(0);
    }

    @Override
    public void run() {

        Thread thisThread = Thread.currentThread();

        while (thisThread == senderThread) {
            try {
                String pvtXml = getPvt();
                Log.outputOperLogOper(null, Log.LOG_LEVEL_0, "ORCAからCLAIM受信");
                Log.outputFuncLog(Log.LOG_LEVEL_3, Log.FUNCTIONLOG_KIND_INFORMATION, pvtXml);
                addPvt(pvtXml);
            } catch (InterruptedException e) {
                ClientContext.getPvtLogger().warn("PVT Sender interrupted");
            }
        }
    }
}
