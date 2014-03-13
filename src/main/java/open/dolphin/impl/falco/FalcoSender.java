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
package open.dolphin.impl.falco;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import open.dolphin.client.Chart;
import open.dolphin.client.IKarteSender;
import open.dolphin.exception.DolphinException;
import open.dolphin.infomodel.*;
import open.dolphin.project.Project;

/**
 * FalcoSender
 *
 * @author Kazushi Minagawa. Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public final class FalcoSender implements IKarteSender {

    private static final String SDF_FORMAT = "yyyyMMddHHmmssSSS";
    private Chart context;
    private String insuranceFacilityId;
    private String path;
    private List<BundleDolphin> sendList;
    private String orderNumber;
//s.oh^ 2013/12/12 予定カルテのオーダー対応
    private Date orderDate;
//s.oh$

    private static String createOrderNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append("DL");
        sb.append(new SimpleDateFormat(SDF_FORMAT).format(new Date()));
        return sb.toString();
    }

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

        if (data == null || (!data.getDocInfoModel().isSendLabtest())) {
            return;
        }

        // 保健医療機関コード
        insuranceFacilityId = Project.getString(Project.SEND_LABTEST_FACILITY_ID);
        if (insuranceFacilityId == null || insuranceFacilityId.length() < 10) {
            throw new DolphinException("保険医療機関コードが設定されていません。");
        }
        insuranceFacilityId += "00";

        // 検査オーダーの出力先パス
        path = Project.getString(Project.SEND_LABTEST_PATH);
        if (path == null) {
            throw new DolphinException("検体検査オーダーの出力先パスが設定されていません。");
        }

        // 検体検査オーダーを抽出する
        List<ModuleModel> modules = data.getModules();

        if (modules == null || modules.isEmpty()) {
            return;
        }

        sendList = new ArrayList<BundleDolphin>();
        for (ModuleModel module : modules) {
            ModuleInfoBean info = module.getModuleInfoBean();
            if (info.getEntity().equals(IInfoModel.ENTITY_LABO_TEST)) {
                BundleDolphin send = (BundleDolphin) module.getModel();
                ClaimItem[] items = send.getClaimItem();
                if (items != null && items.length > 0) {
                    sendList.add(send);
                }
            }
        }

        // オーダー番号を docInfo へ設定する
        if (data.getDocInfoModel().getLabtestOrderNumber() == null) {
            orderNumber = createOrderNumber();
            data.getDocInfoModel().setLabtestOrderNumber(orderNumber);
        } else {
            // 修正の場合は設定されている
            orderNumber = data.getDocInfoModel().getLabtestOrderNumber();
        }

//s.oh^ 2013/12/12 予定カルテのオーダー対応
        boolean tmp = IInfoModel.STATUS_TMP.equals(data.getDocInfoModel().getStatus());
        boolean scheduled = data.getDocInfoModel().getFirstConfirmDate().after(data.getDocInfoModel().getConfirmDate());
        if (tmp && scheduled) {
            orderDate = data.getDocInfoModel().getFirstConfirmDate();
        }
//s.oh$
    }

    @Override
    public void send(DocumentModel data) {

        if (data == null
                || (!data.getDocInfoModel().isSendLabtest())
                || sendList.isEmpty()
                || insuranceFacilityId == null
                || path == null) {
            return;
        }

        // 送信する
        PatientModel patient = context.getPatient();
        UserModel user = Project.getUserModel();

        HL7Falco falco = new HL7Falco();
//s.oh^ 2013/12/12 予定カルテのオーダー対応
        //falco.order(patient, user, sendList, insuranceFacilityId, orderNumber, path);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        falco.order(patient, user, sendList, insuranceFacilityId, orderNumber, path, (orderDate == null) ? null : sdf.format(orderDate));
//s.oh$
    }
}
