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
package open.dolphin.project;

import java.awt.Window;
import open.dolphin.client.AboutDialog;
import open.dolphin.client.ClientContext;
import open.dolphin.client.SaveDialog;
import open.dolphin.client.SaveParams;
import open.dolphin.infomodel.ID;

/**
 * プロジェクトに依存するオブジェクトを生成するファクトリクラス
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class DolphinFactory extends AbstractProjectFactory {

    protected String csgwPath;

    /**
     * Creates new Project
     */
    public DolphinFactory() {
    }

    /**
     * 地域連携用の患者のMasterIdを返す。
     */
    @Override
    public ID createMasterId(String pid, String facilityId) {
        return new ID(pid, "facility", facilityId);
    }

    /**
     * CSGW(Client Side Gate Way) のパスを返す。
     *
     * @param uploaderAddress MMLアップローダのIP Address
     * @param share Samba 共有ディレクトリ
     * @param facilityId 連携用の施設ID
     */
    @Override
    public String createCSGWPath(String uploaderAddress, String share, String facilityId) {
        if (csgwPath == null) {
            if (ClientContext.isWin()) {
                StringBuilder sb = new StringBuilder();
                sb.append("¥¥¥¥");
                sb.append(uploaderAddress);
                sb.append("¥¥");
                sb.append(share);
                sb.append("¥¥");
                sb.append(facilityId);
                csgwPath = sb.toString();
            } else if (ClientContext.isMac()) {
                StringBuilder sb = new StringBuilder();
                sb.append("smb://");
                sb.append(uploaderAddress);
                sb.append("/");
                sb.append(share);
                sb.append("/");
                sb.append(facilityId);
                csgwPath = sb.toString();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("/");
                sb.append(uploaderAddress);
                sb.append("/");
                sb.append(share);
                sb.append("/");
                sb.append(facilityId);
                csgwPath = sb.toString();
            }
        }
        return csgwPath;
    }

    @Override
    public Object createAboutDialog() {
        String title = ClientContext.getFrameTitle("アバウト");
        return new AboutDialog(null, title, "splash.jpg");
    }

    @Override
    public Object createSaveDialog(Window parent, SaveParams params) {
        SaveDialog sd = new SaveDialog(parent);
        params.setAllowPatientRef(false);    // 患者の参照
        params.setAllowClinicRef(false);     // 診療履歴のある医療機関
        sd.setValue(params);
        return sd;
    }
}
