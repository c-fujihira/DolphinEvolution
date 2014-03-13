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

import open.dolphin.infomodel.PVTHealthInsuranceModel;


/**
 * NewKarteParams
 *
 * @author  Kazushi Minagawa
 */
public final class NewKarteParams {
    
    // ベースのカルテがあるかどうか、タブ及びEditorFrameの別、修正かどうか
    private Chart.NewKarteOption option;
    
    // 空白、全コピー、前回処方適用のフラグ
    private Chart.NewKarteMode createMode;
    
    // 診療科
    private String deptName;
    
    // 診療科コード
    private String departmentCode;
    
    // 健康保険
    private Object[] insurances;
    
    // 初期化時に選択する保険
    private int initialSelectedInsurance;
    
    // ダイアログでユーザが選択した保険
    private PVTHealthInsuranceModel insurance;
    
    // EditorFrame で編集するかどうかのフラグ 
    private boolean openFrame;
    
    // 生成するドキュメントの種類
    // 2号カルテ、シングル、紹介状等
    private String docType;
    
    // 不明
    private String groupId;
    
    
    /** Creates a new instance of NewKarteParams */
    public NewKarteParams(Chart.NewKarteOption option) {
        this.option = option;
    }
    
    public Chart.NewKarteOption getOption() {
        return option;
    }
    
    public String getDocType() {
        return docType;
    }
    
    public String setDocType(String docType) {
        return this.docType = docType;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String val) {
        groupId = val;
    }
    
    public String getDepartmentName() {
        return deptName;
    }

    public void setDepartmentName(String val) {
        deptName = val;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }
    
    public Object[] getInsurances() {
        return insurances;
    }
    
    public void setInsurances(Object[] ins) {
        insurances = ins;
    }
    
    public PVTHealthInsuranceModel getPVTHealthInsurance() {
        return insurance;
    }
    
    public void setPVTHealthInsurance(PVTHealthInsuranceModel val) {
        insurance = val;
    }
    
    public void setOpenFrame(boolean openFrame) {
        this.openFrame = openFrame;
    }
    
    public boolean isOpenFrame() {
        return openFrame;
    }
    
    public Chart.NewKarteMode getCreateMode() {
        return createMode;
    }
    
    public void setCreateMode(Chart.NewKarteMode createMode) {
        this.createMode = createMode;
    }
    
    public int getInitialSelectedInsurance() {
        return initialSelectedInsurance;
    }
    
    public void setInitialSelectedInsurance(int index) {
        initialSelectedInsurance = index;
    }
}