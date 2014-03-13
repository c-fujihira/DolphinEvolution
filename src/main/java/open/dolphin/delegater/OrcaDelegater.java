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
package open.dolphin.delegater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import open.dolphin.infomodel.*;

/**
 * OrcaDelegater
 *
 * @author Kazushi Minagawa.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public interface OrcaDelegater {

    // 保険医療機関コードとJMARIコードを取得する
    public String getFacilityCodeBy1001() throws Exception;

    // 併用禁忌をチェックする
    public List<DrugInteractionModel> checkInteraction(Collection<String> drug1, Collection<String> drug2) throws Exception;

    // 点数マスター検索
    public List<TensuMaster> getTensuMasterByShinku(String shinku, String now) throws Exception;

    // 点数マスター検索
    public List<TensuMaster> getTensuMasterByName(String name, String now, boolean partialMatch) throws Exception;

    // 点数マスター検索
    public List<TensuMaster> getTensuMasterByCode(String regExp, String now) throws Exception;

    // 点数マスター検索
    public List<TensuMaster> getTensuMasterByTen(String ten, String now) throws Exception;

    // 病名マスター検索
    public List<DiseaseEntry> getDiseaseByName(String name, String now, boolean partialMatch) throws Exception;

    // 一般名を検索する
    public String getGeneralName(String code) throws Exception;

    // ORCA入力セット検索
    public ArrayList<OrcaInputCd> getOrcaInputSet() throws Exception;

    // 入力セットをスタンプとして返す
    public ArrayList<ModuleModel> getStamp(ModuleInfoBean inputSetInfo) throws Exception;

    // 病名インポート
    public ArrayList<RegisteredDiagnosisModel> getOrcaDisease(String patientId, String from, String to, Boolean ascend) throws Exception;

    // Active病名検索
    public ArrayList<RegisteredDiagnosisModel> getActiveOrcaDisease(String patientId, boolean asc) throws Exception;
}
