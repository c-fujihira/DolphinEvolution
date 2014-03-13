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
package open.dolphin.relay;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingWorker;
import open.dolphin.client.ChartEventHandler;
import open.dolphin.infomodel.ChartEventModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.project.Project;

/**
 * フクダ電子心電図ファイリングFEV-70に患者情報を送る
 *
 * @author masuda, Masuda Naika
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class FEV70Relay implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        if (!pce.getPropertyName().equals(ChartEventHandler.CHART_EVENT_PROP)) {
            return;
        }

        ChartEventModel evt = (ChartEventModel) pce.getNewValue();

        if (evt.getEventType() != ChartEventModel.PVT_ADD) {
            return;
        }

        final PatientVisitModel model = evt.getPatientVisitModel();
        if (model == null) {
            return;
        }

        SwingWorker worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                doRelay(model);
                return null;
            }
        };

        worker.execute();
    }

    private void doRelay(PatientVisitModel model) {

        try {
            // shared path
            String sharePath = Project.getString(Project.PVT_RELAY_DIRECTORY);

            if (!sharePath.endsWith(File.separator)) {
                sharePath = sharePath + File.separator;
            }

            // CSV data
            String patientId = model.getPatientModel().getPatientId();
            String patientName = model.getPatientModel().getFullName();
            String patientSex = "1";
            if (model.getPatientModel().getGender().toLowerCase().startsWith("f")) {
                patientSex = "2";
            }
            String patientBD = model.getPatientBirthday().replace("-", "/");

            StringBuilder sb = new StringBuilder();
            sb.append(patientId);
            sb.append(",");
            sb.append(patientName);
            sb.append(",");
            sb.append(patientSex);
            sb.append(",");
            sb.append(patientBD);
            sb.append(",,,,,,,,\n");
//minagawa^ mac jdk7            
//            String fileName = sharePath + "ID_" + patientId;
//            File oldFile = new File(fileName + ".cs_");
//            if (oldFile.exists()) {
//                oldFile.delete();
//            }
//            FileOutputStream fos = new FileOutputStream(fileName + ".cs_");
//            OutputStreamWriter osw = new OutputStreamWriter(fos);
//            BufferedWriter bw = new BufferedWriter(osw);
//            bw.write(sb.toString());
//            bw.close();
//            osw.close();
//            oldFile = new File(fileName + ".CSV");
//            if (oldFile.exists()) {
//                oldFile.delete();
//            }
//            File objFile = new File(fileName + ".cs_");
//            objFile.renameTo(new File(fileName + ".CSV"));
            String pvtData = sb.toString();
            List<String> lineData = new ArrayList<>();
            lineData.add(pvtData);

            String fileName = "ID_" + patientId;
            String tmpName = fileName + ".cs_";
            String destName = fileName + ".CSV";

            Path destPath = Paths.get(sharePath, tmpName);
            Files.write(destPath, lineData, Charset.forName("SHIT-JIS"));
            Files.move(destPath, destPath.resolveSibling(destName));
//minagawa$            
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
