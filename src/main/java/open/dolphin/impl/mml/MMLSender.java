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
package open.dolphin.impl.mml;

import java.io.*;
import open.dolphin.client.Chart;
import open.dolphin.client.ClientContext;
import open.dolphin.client.IKarteSender;
import open.dolphin.client.MmlMessageEvent;
import open.dolphin.client.MmlMessageListener;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.project.Project;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * MMLSender
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class MMLSender implements IKarteSender {

    private Chart context;

    private MmlMessageListener mmlListener;

    @Override
    public Chart getContext() {
        return context;
    }

    @Override
    public void setContext(Chart context) {
        this.context = context;
    }

    @Override
    public void prepare(DocumentModel data) {
        if (data.getDocInfoModel().isSendMml()) {
            mmlListener = context.getMMLListener();
        }
    }

    @Override
    public void send(DocumentModel model) {

        if ((!model.getDocInfoModel().isSendMml()) || mmlListener == null) {
            return;
        }

        // MML Message を生成する
        MMLHelper23 mb = new MMLHelper23();
        mb.setDocument(model);
        mb.setUserModel(Project.getUserModel());
        mb.setPatientId(context.getPatient().getPatientId());
        mb.buildText();

        // 患者情報
        PatientHelper ph = new PatientHelper();
        ph.setPatient(getContext().getPatient());
        ph.setFacility(Project.getUserModel().getFacilityModel().getFacilityName());

        try {
            VelocityContext vct = ClientContext.getVelocityContext();
            vct.put("mmlHelper", mb);
            vct.put("patientHelper", ph);

            // このスタンプのテンプレートファイルを得る
            String templateFile = "mml2.3Helper.vm";

            // Merge する
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            InputStream instream = ClientContext.getTemplateAsStream(templateFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(instream, "UTF-8"));
            Velocity.evaluate(vct, bw, "mml", reader);
            bw.flush();
            bw.close();
            reader.close();
            String mml = sw.toString();
            //System.err.println(mml);

            // debug出力を行う
            if (ClientContext.getMmlLogger() != null) {
                ClientContext.getMmlLogger().debug(mml);
            }

            MmlMessageEvent mevt = new MmlMessageEvent(this);
            mevt.setGroupId(mb.getDocId());
            mevt.setMmlInstance(mml);
            if (mb.getSchema() != null) {
                mevt.setSchema(mb.getSchema());
            }
            mmlListener.mmlMessageEvent(mevt);

            if (Project.getBoolean(Project.JOIN_AREA_NETWORK)) {
                // TODO
            }

        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
