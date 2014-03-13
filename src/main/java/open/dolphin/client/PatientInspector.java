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

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.PatientModel;

/**
 *
 * @author Kazushi Minagawa.
 */
public final class PatientInspector {

    // 個々のインスペクタ
    // 患者基本情報
    private BasicInfoInspector basicInfoInspector;

    // 来院歴
    private PatientVisitInspector patientVisitInspector;

    // 患者メモ
    private MemoInspector memoInspector;

    // 文書履歴
    private DocumentHistory docHistory;

    // アレルギ
    private AllergyInspector allergyInspector;

    // 身長体重
    private PhysicalInspector physicalInspector;

    // インスペクタを格納するタブペイン View
    private JTabbedPane tabbedPane;

    // このクラスのコンテナパネル View
    private JPanel container;

    // Context このインスペクタの親コンテキスト
    private ChartImpl context;

    private JPanel ptSubPanel;

    /**
     * 患者インスペクタクラスを生成する。
     *
     * @param context インスペクタの親コンテキスト
     */
    public PatientInspector(ChartImpl context) {

        // このインスペクタが格納される Chart Object
        this.context = context;

        ptSubPanel = new JPanel();
        ptSubPanel.setLayout(new BoxLayout(ptSubPanel , BoxLayout.Y_AXIS));

        // GUI を初期化する
        initComponents();
    }

    public void dispose() {
        // List をクリアする
        docHistory.clear();
        allergyInspector.clear();
        physicalInspector.clear();
        memoInspector.save();
    }

    public JPanel getPtSubPanel() {
        return ptSubPanel;
    }

    public void setPtSubPanel(JPanel panel) {
        ptSubPanel = panel;
    }

    /**
     * コンテキストを返す。
     *
     * @return
     */
    public ChartImpl getContext() {
        return context;
    }

    /**
     * コンテキストを設定する。
     *
     * @param context
     */
    public void setContext(ChartImpl context) {
        this.context = context;
    }

    /**
     * 患者カルテを返す。
     *
     * @return 患者カルテ
     */
    public KarteBean getKarte() {
        return context.getKarte();
    }

    /**
     * 患者を返す。
     *
     * @return 患者
     */
    public PatientModel getPatient() {
        return context.getKarte().getPatientModel();
    }

    /**
     * 基本情報インスペクタを返す。
     *
     * @return 基本情報インスペクタ
     */
    public BasicInfoInspector getBasicInfoInspector() {
        return basicInfoInspector;
    }

    /**
     * 来院歴インスペクタを返す。
     *
     * @return 来院歴インスペクタ
     */
    public PatientVisitInspector getPatientVisitInspector() {
        return patientVisitInspector;
    }

    /**
     * 患者メモインスペクタを返す。
     *
     * @return 患者メモインスペクタ
     */
    public MemoInspector getMemoInspector() {
        return memoInspector;
    }

    /**
     * 文書履歴インスペクタを返す。
     *
     * @return 文書履歴インスペクタ
     */
    public DocumentHistory getDocumentHistory() {
        return docHistory;
    }

    /**
     * レイアウトのためにインスペクタのコンテナパネルを返す。
     *
     * @return インスペクタのコンテナパネル
     */
    public JPanel getPanel() {
        return container;
    }

    public void initComponents() {

        // 来院歴
//        String pvtTitle = ClientContext.getString("patientInspector.pvt.title");
        // 文書履歴
        String docHistoryTitle = ClientContext.getString("patientInspector.docHistory.title");
        // アレルギ
//        String allergyTitle = ClientContext.getString("patientInspector.allergy.title");
        // 身長体重
        String physicalTitle = ClientContext.getString("patientInspector.physical.title");
        // メモ
//        String memoTitle = ClientContext.getString("patientInspector.memo.title");
//        String topInspector = Project.getString("topInspector", "メモ");
//        String secondInspector = Project.getString("secondInspector", "カレンダ");
//        String thirdInspector = Project.getString("thirdInspector", "文書履歴");
//        String forthInspector = Project.getString("forthInspector", "アレルギ");

        // 各インスペクタを生成する
        basicInfoInspector = new BasicInfoInspector(context);
        patientVisitInspector = new PatientVisitInspector(context);
        memoInspector = new MemoInspector(context);
        memoInspector.getPanel().setBorder(BorderFactory.createEtchedBorder());
        docHistory = new DocumentHistory(getContext());
        allergyInspector = new AllergyInspector(context);
        physicalInspector = new PhysicalInspector(context);

        // タブパネルへ格納する(文書履歴、健康保険、アレルギ、身長体重はタブパネルで切り替え表示する)
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab(docHistoryTitle, docHistory.getPanel());

        int prefW = 260;
        int prefW2 = 260;
        if (ClientContext.isMac()) {
            prefW2 += 20;
        }
        basicInfoInspector.getPanel().setPreferredSize(new Dimension(prefW2, 40));

        // cut & try
        memoInspector.getPanel().setPreferredSize(new Dimension(prefW, 100));
        docHistory.getPanel().setPreferredSize(new Dimension(prefW, 250));
        physicalInspector.getPanel().setPreferredSize(new Dimension(prefW, 80));
        tabbedPane.setPreferredSize(new Dimension(prefW, 200));
        allergyInspector.getPanel().setPreferredSize(new Dimension(prefW, 80));

        getPtSubPanel().add(basicInfoInspector.getPanel());
        getPtSubPanel().add(getPatientVisitInspector().getPanel());
        getPtSubPanel().add(memoInspector.getPanel());
        tabbedPane.addTab(physicalTitle, physicalInspector.getPanel());
        getPtSubPanel().add(tabbedPane);
        getPtSubPanel().add(allergyInspector.getPanel());
        getPtSubPanel().setSize(new Dimension(prefW, 370));
    }

}
