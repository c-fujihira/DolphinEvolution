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
package open.dolphin.order;

import java.util.HashMap;

/**
 * MML Table Dictionary class.
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class MMLTable {

    /**
     * Creates new MMLTable
     */
    public MMLTable() {
    }

    private static final HashMap<String, String> claimClassCode;

    static {
        claimClassCode = new HashMap<String, String>(45, 0.75f);
        claimClassCode.put("110", "初診");
        claimClassCode.put("120", "再診(再診)");
        claimClassCode.put("122", "再診(外来管理加算)");
        claimClassCode.put("123", "再診(時間外)");
        claimClassCode.put("124", "再診(休日)");
        claimClassCode.put("125", "再診(深夜)");
        claimClassCode.put("130", "指導");
        claimClassCode.put("140", "在宅");
        claimClassCode.put("210", "投薬(内服・頓服・調剤)(入院外)");
        claimClassCode.put("211", "投薬(内服・頓服・調剤)(院内)");
        claimClassCode.put("212", "投薬(内服・頓服・調剤)(院外)");
        claimClassCode.put("230", "投薬(外用・調剤)(入院外)");
        claimClassCode.put("231", "投薬(外用・調剤)(院内)");
        claimClassCode.put("232", "投薬(外用・調剤)(院外)");
        claimClassCode.put("240", "投薬(調剤)(入院)");
        claimClassCode.put("250", "投薬(処方)");
        claimClassCode.put("260", "投薬(麻毒)");
        claimClassCode.put("270", "投薬(調基)");
        claimClassCode.put("300", "注射(生物学的製剤・精密持続点滴・麻薬)");
        claimClassCode.put("310", "注射(皮下筋肉内)");
        claimClassCode.put("320", "注射(静脈内)");
        claimClassCode.put("330", "注射(その他)");
        claimClassCode.put("311", "注射(皮下筋肉内)");
        claimClassCode.put("321", "注射(静脈内)");
        claimClassCode.put("331", "注射(その他)");
        claimClassCode.put("400", "処置");
        claimClassCode.put("500", "手術(手術)");
        claimClassCode.put("502", "手術(輸血)");
        claimClassCode.put("503", "手術(ギプス)");
        claimClassCode.put("540", "麻酔");
        claimClassCode.put("600", "検査");
        claimClassCode.put("700", "画像診断");
        claimClassCode.put("800", "その他");
        claimClassCode.put("903", "入院(入院料)");
        claimClassCode.put("906", "入院(外泊)");
        claimClassCode.put("910", "入院(入院時医学管理料)");
        claimClassCode.put("920", "入院(特定入院料・その他)");
        claimClassCode.put("970", "入院(食事療養)");
        claimClassCode.put("971", "入院(標準負担額)");
    }

    public static String getClaimClassCodeName(String key) {
        return (String) claimClassCode.get(key);
    }

    private static final HashMap<String, String> departmentCode;

    static {

        departmentCode = new HashMap<String, String>(40, 1.0f);
        departmentCode.put("内科", "01");
        departmentCode.put("精神科", "02");
        departmentCode.put("神経科", "03");
        departmentCode.put("神経内科", "04");
        departmentCode.put("呼吸器科", "05");
        departmentCode.put("消化器科", "06");
        departmentCode.put("胃腸科", "07");
        departmentCode.put("循環器科", "08");
        departmentCode.put("小児科", "09");
        departmentCode.put("外科", "10");
        departmentCode.put("整形外科", "11");
        departmentCode.put("形成外科", "12");
        departmentCode.put("美容外科", "13");
        departmentCode.put("脳神経外科", "14");
        departmentCode.put("呼吸器外科", "15");
        departmentCode.put("心臓血管外科", "16");
        departmentCode.put("小児外科", "17");
        departmentCode.put("皮膚ひ尿器科", "18");
        departmentCode.put("皮膚科", "19");
        departmentCode.put("ひ尿器科", "20");
        departmentCode.put("泌尿器", "20");
        departmentCode.put("性病科", "21");
        departmentCode.put("こう門科", "22");
        departmentCode.put("産婦人科", "23");
        departmentCode.put("産科", "24");
        departmentCode.put("婦人科", "25");
        departmentCode.put("眼科", "26");
        departmentCode.put("耳鼻いんこう科", "27");
        departmentCode.put("気管食道科", "28");
        departmentCode.put("理学診療科", "29");
        departmentCode.put("放射線科", "30");
        departmentCode.put("麻酔科", "31");
        departmentCode.put("人工透析科", "32");
        departmentCode.put("心療内科", "33");
        departmentCode.put("アレルギー", "34");
        departmentCode.put("リウマチ ", "35");
        departmentCode.put("リハビリ", "36");
        departmentCode.put("鍼灸", "A1");
    }

    public static String getDepartmentCode(String key) {
        return (String) departmentCode.get(key);
    }
}
