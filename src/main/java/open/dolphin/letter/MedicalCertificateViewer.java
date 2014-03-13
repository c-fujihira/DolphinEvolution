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
package open.dolphin.letter;

import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import open.dolphin.client.Chart;
import open.dolphin.client.ChartImpl;
import open.dolphin.client.DocumentViewer;
import open.dolphin.delegater.LetterDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.DocInfoModel;
import open.dolphin.infomodel.LetterModule;

/**
 * MedicalCertificateViewer
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class MedicalCertificateViewer extends MedicalCertificateImpl implements DocumentViewer {

    private long docPK;

    @Override
    public void start() {
        stateMgr = new LetterStateMgr(this);
        this.enter();
    }

    @Override
    public void historyPeriodChanged() {
        stateMgr.processEmptyEvent();
        getContext().showDocument(0);
    }

    @Override
    public void showDocuments(DocInfoModel[] docs, JScrollPane scroller) {

        if (docs == null || docs.length == 0) {
            stateMgr.processEmptyEvent();
            return;
        }

        DocInfoModel docInfo = docs[0];
        docPK = docInfo.getDocPk();

        if (docPK == 0L) {
            return;
        }

        LetterGetTask task = new LetterGetTask(getContext(), docPK, scroller);

        task.execute();
    }

    public void modifyKarte() {
//minagawa^ LSC Test
        if (docPK == 0L) {
            return;
        }

        final MedicalCertificateViewer vw = this;
        DBTask task = new DBTask<LetterModule, Void>(getContext()) {

            @Override
            protected Object doInBackground() throws Exception {
                LetterDelegater ddl = new LetterDelegater();
                LetterModule letter = ddl.getLetter(docPK);
                return letter;
            }

            @Override
            protected void succeeded(LetterModule lm) {
                if (lm == null) {
                    return;
                }
                MedicalCertificateImpl editor = new MedicalCertificateImpl();
                editor.setModel(lm);
                editor.setModify(true);
                editor.setContext(getContext());
                editor.start();
                ChartImpl chart = (ChartImpl) getContext();
                StringBuilder sb = new StringBuilder();
                sb.append("修正").append("(").append(editor.getTitle()).append(")");
                chart.addChartDocument(editor, sb.toString());
                editor.setViewer(vw);
            }
        };
        task.execute();
//minagawa$        
    }

    class LetterGetTask extends DBTask<LetterModule, Void> {

        private long letterPk;
        private JScrollPane scroller;

        public LetterGetTask(Chart app, long letterPk, JScrollPane scroller) {
            super(app);
            this.letterPk = letterPk;
            this.scroller = scroller;
        }

        @Override
        protected LetterModule doInBackground() throws Exception {
            LetterDelegater ddl = new LetterDelegater();
            LetterModule letter = ddl.getLetter(letterPk);
            return letter;
        }

        @Override
        protected void succeeded(LetterModule letter) {
            model = letter;
            modelToView(model);
            setEditables(false);
//minagawa^ センターへ表示             
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
            p.add(view);
            scroller.setViewportView(p);
//minagawa$    
            stateMgr.processCleanEvent();
            getContext().showDocument(0);
        }
    }
}
