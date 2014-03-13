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

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.print.PageFormat;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Date;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import open.dolphin.client.*;
import open.dolphin.delegater.LetterDelegater;
import open.dolphin.helper.DBTask;
import open.dolphin.infomodel.*;
import open.dolphin.project.Project;
import open.dolphin.util.Log;
import org.apache.log4j.Level;

/**
 * MedicalCertificateImpl
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class MedicalCertificateImpl extends AbstractChartDocument implements Letter {

    protected static final String TITLE = "診断書";
    protected static final String ITEM_DISEASE = "disease";
    protected static final String TEXT_INFORMED_CONTENT = "informedContent";

    protected LetterModule model;
    protected MedicalCertificateView view;
    private boolean listenerIsAdded;

    protected LetterStateMgr stateMgr;
    protected boolean DEBUG;

    private boolean modify;
    protected MedicalCertificateViewer viewer;

    /**
     * Creates a new instance of LetterDocument
     */
    public MedicalCertificateImpl() {
        setTitle(TITLE);
        DEBUG = (ClientContext.getBootLogger().getLevel() == Level.DEBUG);
    }

    //minagawa^ LSC Test    
    public LetterModule getModel() {
        return model;
    }

    public void setModel(LetterModule m) {
        this.model = m;
    }

    public boolean isModify() {
        return modify;
    }

    public void setModify(boolean b) {
        modify = b;
    }
//minagawa$

    @Override
    public void modelToView(LetterModule m) {

        if (view == null) {
            view = new MedicalCertificateView();
        }

        // 患者氏名
        LetterHelper.setModelValue(view.getPatientNameFld(), m.getPatientName());

        // 患者生年月日
        //String val = LetterHelper.getBirdayWithAge(m.getPatientBirthday(), m.getPatientAge());
        String val = LetterHelper.getDateString(m.getPatientBirthday());
        LetterHelper.setModelValue(view.getPatientBirthday(), val);

        // 患者性別
        LetterHelper.setModelValue(view.getSexFld(), m.getPatientGender());

        // 患者住所
        LetterHelper.setModelValue(view.getPatientAddress(), m.getPatientAddress());

        // 確定日
//minagawa^ LSC 1.4 bug fix 文書の印刷日付 2013/06/24
        //String dateStr = LetterHelper.getDateAsString(m.getConfirmed());
        String dateStr = LetterHelper.getDateAsString(m.getStarted());
//minagawa$
        LetterHelper.setModelValue(view.getConfirmedFld(), dateStr);

        // 病院住所
        val = LetterHelper.getAddressWithZipCode(m.getConsultantAddress(), m.getConsultantZipCode());
        LetterHelper.setModelValue(view.getHospitalAddressFld(), val);

        // 病院名
        LetterHelper.setModelValue(view.getHospitalNameFld(), m.getConsultantHospital());

        // 病院電話
        LetterHelper.setModelValue(view.getHospitalTelephoneFld(), m.getConsultantTelephone());

        // 医師
        LetterHelper.setModelValue(view.getDoctorNameFld(), m.getConsultantDoctor());

        //----------------------------------------------------------------------
        // 病名
        String value = model.getItemValue(ITEM_DISEASE);
        if (value != null) {
            LetterHelper.setModelValue(view.getDiseaseFld(), value);
        }

        // Informed
        String text = model.getTextValue(TEXT_INFORMED_CONTENT);
        if (text != null) {
            LetterHelper.setModelValue(view.getInformedContent(), text);
        }
    }

    @Override
    // 2013/06/24
    //public void viewToModel() {
    public void viewToModel(boolean save) {

//minagawa^ LSC 1.4 bug fix 文書の印刷日付 2013/06/24
//        long savedId = model.getId();
//        model.setId(0L);
//        model.setLinkId(savedId);
//
//        Date d = new Date();
//        model.setConfirmed(d);
//        model.setRecorded(d);
//        model.setKarteBean(getContext().getKarte());
//        model.setUserModel(Project.getUserModel());
//        model.setStatus(IInfoModel.STATUS_FINAL);
        if (save) {
            if (model.getId() == 0L) {
                // 新規作成で保存 日時を現時刻で再設定する
                Date d = new Date();
                this.model.setConfirmed(d);
                this.model.setRecorded(d);
                this.model.setStarted(d);
            } else {
                // 修正で保存
                Date d = new Date();
                model.setConfirmed(d);              // 確定日
                model.setRecorded(d);               // 記録日
                model.setLinkId(model.getId());     // LinkId
                model.setId(0L);                    // id=0L -> 常に新規保存 persit される、元のモデルは削除される（要変更）
            }
        }
//minagawa$  

        // 患者情報、差し出し人側はtartでmodelに設定済
        // 傷病名
        String value = LetterHelper.getFieldValue(view.getDiseaseFld());
        model.addLetterItem(new LetterItem(ITEM_DISEASE, value));

        // Informed
        String informed = LetterHelper.getAreaValue(view.getInformedContent());
        if (informed != null) {
            LetterText text = new LetterText();
            text.setName(TEXT_INFORMED_CONTENT);
            text.setTextValue(informed);
            model.addLetterText(text);
        }

        // Title
        StringBuilder sb = new StringBuilder();
        sb.append(TITLE).append(":").append(value);
        model.setTitle(sb.toString());
    }

    @Override
    public void start() {
//minagawa^ LSC Test
        if (this.model == null) {
            this.model = new LetterModule();

            // Handle Class
            this.model.setHandleClass(MedicalCertificateViewer.class.getName());
            this.model.setLetterType(IInfoModel.MEDICAL_CERTIFICATE);

            // 確定日等
            Date d = new Date();
            this.model.setConfirmed(d);
            this.model.setRecorded(d);
            this.model.setStarted(d);
            this.model.setStatus(IInfoModel.STATUS_FINAL);
            this.model.setKarteBean(getContext().getKarte());
            this.model.setUserModel(Project.getUserModel());

            // 患者情報
            PatientModel patient = getContext().getPatient();
            this.model.setPatientId(patient.getPatientId());
            this.model.setPatientName(patient.getFullName());
            this.model.setPatientKana(patient.getKanaName());
            this.model.setPatientGender(patient.getGenderDesc());
            this.model.setPatientBirthday(patient.getBirthday());
            this.model.setPatientAge(ModelUtils.getAge(patient.getBirthday()));
            if (patient.getSimpleAddressModel() != null) {
                this.model.setPatientAddress(patient.getSimpleAddressModel().getAddress());
            }
            this.model.setPatientTelephone(patient.getTelephone());

            // 病院
            UserModel user = Project.getUserModel();
            this.model.setConsultantHospital(user.getFacilityModel().getFacilityName());
            this.model.setConsultantDoctor(user.getCommonName());
            this.model.setConsultantDept(user.getDepartmentModel().getDepartmentDesc());
            this.model.setConsultantTelephone(user.getFacilityModel().getTelephone());
            this.model.setConsultantFax(user.getFacilityModel().getFacsimile());
            this.model.setConsultantZipCode(user.getFacilityModel().getZipCode());
            this.model.setConsultantAddress(user.getFacilityModel().getAddress());
        }
//minagawa$
        // view を生成
        this.view = new MedicalCertificateView();
// minagawa 中央へ位置するように変更 ^        
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.add(this.view);
        JScrollPane scroller = new JScrollPane(p);
        getUI().setLayout(new BorderLayout());
        getUI().add(scroller, BorderLayout.CENTER);
//        JScrollPane scroller = new JScrollPane(this.view);
//        getUI().setLayout(new BorderLayout());
//        getUI().add(scroller);
// minagawa $

        modelToView(this.model);
        setEditables(true);
        setListeners();

//minagawa^ LSC 1.4 bug fix : Mac JDK7 bug マックの上下キー問題 2013/06/24
        if (ClientContext.isMac()) {
            new MacInputFixer().fix(view.getInformedContent());
        }
//minagawa$         

        stateMgr = new LetterStateMgr(this);
//minagawa^ LSC Test        
        this.enter();
//minagawa$        

//s.oh^ 文書の必要事項対応
        //view.getDiseaseFld().setBackground(Color.YELLOW);
        //view.getInformedContent().setBackground(Color.YELLOW);
//s.oh$
    }

    @Override
    public void stop() {
    }

    @Override
    public void save() {

        viewToModel(true);

        DBTask task = new DBTask<Boolean, Void>(getContext()) {

            @Override
            protected Boolean doInBackground() throws Exception {

                LetterDelegater ddl = new LetterDelegater();
                long result = ddl.saveOrUpdateLetter(model);
                model.setId(result);
                return true;
            }

            @Override
            protected void succeeded(Boolean result) {
//minagawa^ Chartの close box 押下で保存する場合、保存終了を通知しておしまい。                    
                // 2013/04/19
                if (boundSupport != null) {
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "診断書", "保存成功", "インスペクタの終了");
                    setChartDocDidSave(true);
                    return;
                }
//minagawa$                
// minagawa 紹介状等の履歴に遷移 ^                
                getContext().getDocumentHistory().getLetterHistory();
//                getContext().getDocumentHistory().getDocumentHistory();
// minagawa $
                stateMgr.processSavedEvent();

                ((ChartImpl) getContext()).getKarteSplitPane().setBottomComponent(null);
                ((ChartImpl) getContext()).getKarteSplitPane().revalidate();
                Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_INFORMATION, "診断書", "保存成功");
            }
        };

        task.execute();
    }

    @Override
    public void enter() {
        super.enter();
        if (stateMgr != null) {
            stateMgr.enter();
        }
    }

    /**
     * カルテを修正する。
     */
    public void modifyKarte() {
        viewer.modifyKarte();
    }

    @Override
    public void print() {

        if (this.model == null) {
            return;
        }

        viewToModel(false);

        StringBuilder sb = new StringBuilder();
        sb.append("PDFファイルを作成しますか?");

        int option = JOptionPane.showOptionDialog(
                getContext().getFrame(),
                sb.toString(),
                ClientContext.getFrameTitle("診断書印刷"),
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{"PDF作成", "フォーム印刷", GUIFactory.getCancelButtonText()},
                "PDF作成");
        Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_OTHER, ClientContext.getFrameTitle("診断書印刷"), sb.toString());

        if (option == 0) {
            Log.outputOperLogDlg(getContext(), Log.LOG_LEVEL_0, "PDF作成");
            makePDF();
        } else if (option == 1) {
            Log.outputOperLogDlg(getContext(), Log.LOG_LEVEL_0, "フォーム印刷");
            PageFormat pageFormat = getContext().getContext().getPageFormat();
            String name = getContext().getPatient().getFullName();
            Panel2 panel = (Panel2) this.view;
            panel.printPanel(pageFormat, 1, false, name, 0, true);
        } else {
            Log.outputOperLogDlg(getContext(), Log.LOG_LEVEL_0, "取消し");
        }
    }

    @Override
    public void makePDF() {

        if (this.model == null) {
            return;
        }

        SwingWorker w = new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {
                MedicalCertificatePDFMaker pdf = new MedicalCertificatePDFMaker();
                pdf.setDocumentDir(Project.getString(Project.LOCATION_PDF));
                pdf.setModel(model);
                return pdf.create();
            }

            @Override
            protected void done() {
                String err = null;
                try {
                    //minagawa^ jdk7                   
                    //String pathToPDF = get();
                    //Desktop.getDesktop().open(new File(pathToPDF));
                    URI uri = Paths.get(get()).toUri();
                    Desktop.getDesktop().browse(uri);
//minagawa$         
                } catch (IOException ex) {
                    err = "PDFファイルに関連づけされたアプリケーションを起動できません。";
                } catch (InterruptedException ex) {
                } catch (Throwable ex) {
                    err = ex.getMessage();
                }

                if (err != null) {
                    Window parent = SwingUtilities.getWindowAncestor(getContext().getFrame());
                    JOptionPane.showMessageDialog(parent, err, ClientContext.getFrameTitle("PDF作成"), JOptionPane.WARNING_MESSAGE);
                    Log.outputFuncLog(Log.LOG_LEVEL_0, Log.FUNCTIONLOG_KIND_WARNING, ClientContext.getFrameTitle("PDF作成"), err);
                }
            }
        };

        w.execute();
    }

    @Override
    public boolean isDirty() {
        if (stateMgr != null) {
            return stateMgr.isDirtyState();
        } else {
            return super.isDirty();
        }
    }

//    public void modifyKarte() {
//        stateMgr.processModifyKarteEvent();
//    }
    @Override
    public void setEditables(boolean b) {
        view.getDiseaseFld().setEditable(b);
        view.getInformedContent().setEditable(b);
    }

    @Override
    public void setListeners() {

        if (listenerIsAdded) {
            return;
        }

        DocumentListener dl = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }

            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                stateMgr.processDirtyEvent();
            }
        };

        ChartMediator med = getContext().getChartMediator();

        // 傷病名
        view.getDiseaseFld().getDocument().addDocumentListener(dl);
        view.getDiseaseFld().addFocusListener(AutoKanjiListener.getInstance());
        view.getDiseaseFld().setTransferHandler(new BundleTransferHandler(med, view.getDiseaseFld()));
        view.getDiseaseFld().addMouseListener(CutCopyPasteAdapter.getInstance());

        // Informed
        view.getInformedContent().getDocument().addDocumentListener(dl);
        view.getInformedContent().addFocusListener(AutoKanjiListener.getInstance());
        view.getInformedContent().setTransferHandler(new BundleTransferHandler(med, view.getInformedContent()));
        view.getInformedContent().addMouseListener(CutCopyPasteAdapter.getInstance());

//        // 診断日
//        PopupCalendarListener pl = new PopupCalendarListener(view.getConfirmedFld());
//        view.getConfirmedFld().getDocument().addDocumentListener(dl);
        listenerIsAdded = true;
    }

    @Override
    public boolean letterIsDirty() {
//minagawa^ LSC Test        
        boolean dirty = (LetterHelper.getFieldValue(view.getDiseaseFld()) != null);
        dirty = dirty || (LetterHelper.getAreaValue(view.getInformedContent()) != null);
        return dirty;
//minagawa$        
    }

    public MedicalCertificateViewer getViewer() {
        return viewer;
    }

    public void setViewer(MedicalCertificateViewer viewer) {
        this.viewer = viewer;
    }

}
