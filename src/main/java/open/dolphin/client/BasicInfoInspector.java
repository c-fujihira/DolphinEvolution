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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import open.dolphin.infomodel.IInfoModel;
import open.dolphin.infomodel.SimpleAddressModel;
import open.dolphin.project.Project;
import open.dolphin.util.AgeCalculater;

/**
 *
 * @author Kazushi Minagawa.
 */
public class BasicInfoInspector {
    
    private JPanel basePanel; // このクラスのパネル
    private JLabel nameLabel;
    private JLabel addressLabel;
    private Color maleColor;
    private Color femaleColor;
    private Color unknownColor;
    
    // Context このインスペクタの親コンテキスト
    private final ChartImpl context;


    /**
     * BasicInfoInspectorオブジェクトを生成する。
     */
    public BasicInfoInspector(ChartImpl context) {
        this.context = context;
        initComponent();
        update();
    }

    /**
     * レイウアトのためにこのインスペクタのコンテナパネルを返す。
     * @return コンテナパネル
     */
    public JPanel getPanel() {
        return basePanel;
    }

    /**
     * 患者の基本情報を表示する。
     */
    private void update() {

        StringBuilder sb = new StringBuilder();
        sb.append(context.getPatient().getFullName());
        sb.append("  ");
        String age = AgeCalculater.getAgeAndBirthday(context.getPatient().getBirthday(), Project.getInt("ageToNeedMonth", 6));
        sb.append(age);
        nameLabel.setText(sb.toString());

        SimpleAddressModel address = context.getPatient().getSimpleAddressModel();
        if (address != null) {
            String adr = address.getAddress();
            if (adr != null) {
                adr = adr.replaceAll("　", " ");
            }
            addressLabel.setText(adr);
        } else {
            addressLabel.setText("　");
        }

        String gender = context.getPatient().getGenderDesc();

        Color color;
        switch (gender) {
            case IInfoModel.MALE_DISP:
                color = maleColor;
                break;
            case IInfoModel.FEMALE_DISP:
                color = femaleColor;
                break;
            default:
                color = unknownColor;
                break;
        }
        nameLabel.setBackground(color);
        addressLabel.setBackground(color);
        basePanel.setBackground(color);
    }

    /**
     * GUI コンポーネントを初期化する。
     */
    private void initComponent() {
        
        // 性別によって変えるパネルのバックグランドカラー
        Color foreground = ClientContext.getColor("patientInspector.basicInspector.foreground"); // new
        maleColor = ClientContext.getColor("color.male"); // Color.CYAN;
        femaleColor = ClientContext.getColor("color.female"); // Color.PINK;
        unknownColor = ClientContext.getColor("color.unknown"); // Color.LIGHT_GRAY;
        //int[] size = ClientContext.getIntArray("patientInspector.basicInspector.size");
        
        nameLabel = new JLabel("　");
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setForeground(foreground);
        nameLabel.setOpaque(true);
        nameLabel.setMinimumSize(new Dimension(220, 15));
        nameLabel.setMaximumSize(new Dimension(220, 15));
        nameLabel.setMaximumSize(new Dimension(220, 15));
        
        addressLabel = new JLabel("　");
        addressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        addressLabel.setForeground(foreground);
        addressLabel.setOpaque(true);
        addressLabel.setMinimumSize(new Dimension(220, 15));
        addressLabel.setMaximumSize(new Dimension(220, 15));
        addressLabel.setMaximumSize(new Dimension(220, 15));


        basePanel = new JPanel(new BorderLayout(0, 1));
        basePanel.setMinimumSize(new Dimension(220, 15));
        basePanel.setMaximumSize(new Dimension(220, 15));
        basePanel.setPreferredSize(new Dimension(220, 15));
        basePanel.add(nameLabel, BorderLayout.NORTH);
        basePanel.add(addressLabel, BorderLayout.SOUTH);
    }
}
