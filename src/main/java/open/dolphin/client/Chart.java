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

import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import open.dolphin.infomodel.DocumentModel;
import open.dolphin.infomodel.KarteBean;
import open.dolphin.infomodel.ModuleInfoBean;
import open.dolphin.infomodel.PVTHealthInsuranceModel;
import open.dolphin.infomodel.PatientModel;
import open.dolphin.infomodel.PatientVisitModel;
import open.dolphin.utilities.control.MyJTabbedPane;

/**
 *
 * @author Kazushi Minagawa.
 */
public interface Chart extends MainTool {
    
    public enum NewKarteOption {BROWSER_NEW, BROWSER_COPY_NEW, BROWSER_MODIFY, EDITOR_NEW, EDITOR_COPY_NEW, EDITOR_MODIFY};
    
    public enum NewKarteMode {EMPTY_NEW, APPLY_RP, ALL_COPY};
    
    public KarteBean getKarte();
    
    public void setKarte(KarteBean karte);
    
    public PatientModel getPatient();
    
    public PatientVisitModel getPatientVisit();
    
    public void setPatientVisit(PatientVisitModel model);
    
    public int getChartState();
    
    public void setChartState(int state);
    
    public boolean isReadOnly();
    
    public void setReadOnly(boolean b);
    
    public void close();
    
    public JFrame getFrame();
    
    public IStatusPanel getStatusPanel();
    
    public void setStatusPanel(IStatusPanel statusPanel);
    
    public ChartMediator getChartMediator();
    
    public void enabledAction(String name, boolean enabled);
    
    public DocumentHistory getDocumentHistory();
    
    public void showDocument(int index);
    
    public boolean isDirty();
    
    public PVTHealthInsuranceModel[] getHealthInsurances();

    public PVTHealthInsuranceModel getHealthInsuranceToApply(String uuid);

    //---------------------------------------------------------------
    // CLAIM 再送信機能のため追加
    public DocumentModel getKarteModelToEdit(NewKarteParams params);
    public DocumentModel getKarteModelToEdit(DocumentModel oldModel, NewKarteParams params);
    public DocumentModel getKarteModelToEdit(DocumentModel oldModel);
    public ClaimMessageListener getCLAIMListener();
    public boolean isSendClaim();
    //---------------------------------------------------------------
    public MmlMessageListener getMMLListener();

    //public boolean isSendMml();
    //---------------------------------------------------------------
    // 検体検査オーダーのために追加
    public boolean isSendLabtest();
    //---------------------------------------------------------------
    
    //---------------------------------------------------------------
    // Ppane でも病名スタンプを受け入れる。このリストにaddで追加。
    // 傷病名タブが選択された時に中味の病歴を追加。
    //---------------------------------------------------------------
    public void addDroppedDiagnosis(ModuleInfoBean bean);
    public List<ModuleInfoBean> getDroppedDiagnosisList();
    
    public void setApp(Evolution application);
    public Evolution getApp();
    public void setBaseTabPane(MyJTabbedPane tabpane);
    public void setFirstFlag(boolean firstFlag);
    public void setPatientIdList(List<String> patientIdList);
    public void setPatientIdMap(Map<String, PatientVisitModel> patientIdMap);
    public void setChartImplMap(Map<String, ChartImpl> chartImplMap);
    public void setChartEventHandler(ChartEventHandler scl);
}
