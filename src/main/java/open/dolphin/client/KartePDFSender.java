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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import open.dolphin.helper.UserDocumentHelper;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.letter.KartePDFMaker;
import open.dolphin.project.Project;
import open.dolphin.util.Log;

/**
 *
 * @author kazushi Minagawa
 */
public class KartePDFSender implements IKarteSender {
    
    private Chart context;
    private boolean send;
    private String pathToOutput;

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
        send = Project.getBoolean(Project.KARTE_PDF_SEND_AT_SAVE);
        if (!send) {
            return;
        }
        // 出力先を取得
        String outputDir = Project.getString(Project.KARTE_PDF_SEND_DIRECTORY);
        String title = "カルテ";
        String ext = ".pdf";
        String ptName = context.getPatient().getFullName();
        ptName = ptName.replace(" ", "").replace("　", "");
        Date d = new Date();
        pathToOutput = UserDocumentHelper.createPathToDocument(outputDir, title, ext, ptName, d);
    }

    @Override
    public void send(DocumentModel data) {
        if (send) {
//s.oh^ 2013/09/05
            if(pathToOutput == null) {
                String msg1 = "PDFファイルの保存に失敗しました。";
                String msg2 = "PDFの保存先を確認してください。";
                Object obj = new String[]{msg1, msg2};
                JOptionPane.showMessageDialog(null, obj, ClientContext.getString("productString"), JOptionPane.ERROR_MESSAGE);
                Log.outputOperLogDlg(getContext(), Log.LOG_LEVEL_0, msg1, msg2);
            }else if(send) {
//s.oh$
                List<DocumentModel> list = new ArrayList<DocumentModel>(1);
                list.add(data);
                KartePDFMaker maker = new KartePDFMaker();
                maker.setContext(getContext());
                maker.setDocumentList(list);
                maker.setAscending(true);
                maker.makePDF(pathToOutput);
            }
        }
    }
}
