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

/**
 * IRpView
 *
 * @author kazushi
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public interface IRpView {

    /**
     * @return the clearBtn
     */
    public javax.swing.JButton getClearBtn();

    /**
     * @return the countField
     */
    public javax.swing.JTextField getCountField();

    /**
     * @return the deleteBtn
     */
    public javax.swing.JButton getDeleteBtn();

    /**
     * @return the inRadio
     */
    public javax.swing.JRadioButton getInRadio();

    /**
     * @return the infoLabel
     */
    public javax.swing.JLabel getInfoLabel();

    /**
     * @return the medicineCheck
     */
    public javax.swing.JCheckBox getMedicineCheck();

    /**
     * @return the okBtn
     */
    public javax.swing.JButton getOkBtn();

    /**
     * @return the okCntBtn
     */
    public javax.swing.JButton getOkCntBtn();

    /**
     * @return the outRadio
     */
    public javax.swing.JRadioButton getOutRadio();

    /**
     * @return the rtCheck
     */
    public javax.swing.JCheckBox getRtCheck();

    /**
     * @return the searchResultTable
     */
    public javax.swing.JTable getSearchResultTable();

    /**
     * @return the searchTextField
     */
    public javax.swing.JTextField getSearchTextField();

    /**
     * @return the setTable
     */
    public javax.swing.JTable getSetTable();

    /**
     * @return the stampNameField
     */
    public javax.swing.JTextField getStampNameField();

    /**
     * @return the usageCheck
     */
    public javax.swing.JCheckBox getUsageCheck();

    /**
     * @return the usageCombo
     */
    public javax.swing.JComboBox getUsageCombo();

    public javax.swing.JCheckBox getTonyoChk();

    /**
     * @return the partialChk
     */
    public javax.swing.JCheckBox getPartialChk();

    public javax.swing.JCheckBox getTemporalChk();

}
