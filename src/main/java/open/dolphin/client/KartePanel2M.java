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

import java.awt.Color;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BoxView;
import javax.swing.text.ComponentView;
import javax.swing.text.Element;
import javax.swing.text.IconView;
import javax.swing.text.LabelView;
import javax.swing.text.ParagraphView;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import open.dolphin.project.Project;

/**
 *
 * @author kazushi Minagawa. Digital Globe, Inc.
 */
public class KartePanel2M extends Panel2 {
    
    private static final int PREFERED_WIDTH  = 320;
    private static final int PREFERED_HEIGHT = 400;
    private static final int MAX_HEIGHT      = 4096;
    
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTextPane pTextPane;
    private javax.swing.JTextPane soaTextPane;
    private javax.swing.JLabel timeStampLabel;
    private final int fontSize;
    private JLabel leftLabel;
    private JLabel rightLabel;

    public KartePanel2M() {
        fontSize = Project.getInt("karte.font.size.default");
        initComponents();
    }

    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        timeStampLabel = new javax.swing.JLabel();
        soaTextPane = new javax.swing.JTextPane();
        pTextPane = new javax.swing.JTextPane();
//masuda^ jdk7 wrap problem 2013/06/06
        soaTextPane.setEditorKit(new WrapEditorKit());
        pTextPane.setEditorKit(new WrapEditorKit());
//masuda$        
        setAutoscrolls(true);

//        jPanel2.setMaximumSize(new java.awt.Dimension(55, 26));

        timeStampLabel.setText("");
//        jPanel2.add(timeStampLabel);
        timeStampLabel.setBackground(new Color(195, 195, 195));
        
        //- 2号用紙 ディテール
        GroupLayout jPanelLayout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanelLayout);

        leftLabel = new JLabel("既往症・原因・主要症状・経過等");
        leftLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        leftLabel.setBackground(new Color(195, 195, 195));
        rightLabel = new JLabel("処方・手術・処置等");
        rightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        rightLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        rightLabel.setBackground(new Color(195, 195, 195));
        timeStampLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        jPanelLayout.setHorizontalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(timeStampLabel, javax.swing.GroupLayout.DEFAULT_SIZE, PREFERED_WIDTH*2+2, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanelLayout.createSequentialGroup()
                        .addComponent(leftLabel, javax.swing.GroupLayout.DEFAULT_SIZE, PREFERED_WIDTH, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rightLabel, javax.swing.GroupLayout.DEFAULT_SIZE, PREFERED_WIDTH, Short.MAX_VALUE)))
                .addGap(0, 0, 0))
        );
        jPanelLayout.setVerticalGroup(
            jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanelLayout.createSequentialGroup()
                .addComponent(timeStampLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(leftLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rightLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0))
        );
        jPanel2.setBackground(new Color(195, 195, 195));
        timeStampLabel.setForeground(new Color(50, 50, 150));

        //soaTextPane.setFont(new java.awt.Font("SansSerif", 0, fontSize));
        soaTextPane.setFont(new java.awt.Font("Lucida Grande", 0, fontSize));
        soaTextPane.setMargin(new java.awt.Insets(0, 10, 10, 10));
        soaTextPane.setPreferredSize(new java.awt.Dimension(PREFERED_WIDTH, PREFERED_HEIGHT));

        //pTextPane.setFont(new java.awt.Font("SansSerif", 0, fontSize));
        pTextPane.setFont(new java.awt.Font("Lucida Grande", 0, fontSize));
        pTextPane.setMargin(new java.awt.Insets(0, 10, 10, 10));
        pTextPane.setPreferredSize(new java.awt.Dimension(PREFERED_WIDTH, PREFERED_HEIGHT));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(soaTextPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, PREFERED_WIDTH, Short.MAX_VALUE)
                .add(0, 2, 2)
                .add(pTextPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, PREFERED_WIDTH, Short.MAX_VALUE))
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, PREFERED_WIDTH*2+2, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(pTextPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, MAX_HEIGHT, Short.MAX_VALUE)
                    .add(soaTextPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, MAX_HEIGHT, Short.MAX_VALUE)))
        );
    }

    private Color Color(int i, double d) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
//masuda^ jdk7 wrap 2013/06/06
    class WrapEditorKit extends StyledEditorKit {
        ViewFactory defaultFactory=new WrapColumnFactory();
        @Override
        public ViewFactory getViewFactory() {
            return defaultFactory;
        }
    }
 
    class WrapColumnFactory implements ViewFactory {
        @Override
        public View create(Element elem) {
            String kind = elem.getName();
            if (kind != null) {
                switch (kind) {
                    case AbstractDocument.ContentElementName:
                        return new WrapLabelView(elem);
                    case AbstractDocument.ParagraphElementName:
                        return new ParagraphView(elem);
                    case AbstractDocument.SectionElementName:
                        return new BoxView(elem, View.Y_AXIS);
                    case StyleConstants.ComponentElementName:
                        return new ComponentView(elem);
                    case StyleConstants.IconElementName:
                        return new IconView(elem);
                }
            }
 
            // default to text display
            return new LabelView(elem);
        }
    }
 
    class WrapLabelView extends LabelView {
        public WrapLabelView(Element elem) {
            super(elem);
        }
 
        @Override
        public float getMinimumSpan(int axis) {
            switch (axis) {
                case View.X_AXIS:
                    return 0;
                case View.Y_AXIS:
                    return super.getMinimumSpan(axis);
                default:
                    throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }
    }
//masuda^ jdk7 wrap          
    public javax.swing.JTextPane getPTextPane() {
        return pTextPane;
    }

    public javax.swing.JTextPane getSoaTextPane() {
        return soaTextPane;
    }

    public javax.swing.JLabel getTimeStampLabel() {
        return timeStampLabel;
    }
    
     public javax.swing.JPanel getTimeStampPanel() {
        return jPanel2;
    }

    public JLabel getLeftLabel() {
        return leftLabel;
    }

    public void setLeftLabel(JLabel leftLabel) {
        this.leftLabel = leftLabel;
    }

    public JLabel getRightLabel() {
        return rightLabel;
    }

    public void setRightLabel(JLabel rightLabel) {
        this.rightLabel = rightLabel;
    }
}
