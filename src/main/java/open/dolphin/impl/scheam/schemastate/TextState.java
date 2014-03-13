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
package open.dolphin.impl.scheam.schemastate;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import javax.swing.JOptionPane;
import open.dolphin.impl.scheam.SchemaCanvasDialog2;
import open.dolphin.impl.scheam.SchemaEditorImpl;

/**
 * TextState
 *
 * @author pns
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class TextState extends AbstractState {

    private String inputText;
    private final int[] fontSizeList = {12, 16, 20, 24, 28};

    public TextState(SchemaEditorImpl context) {
        super(context);
    }

    @Override
    public void mouseDown(Point p) {

        TextPanel textPanel = new TextPanel();
        textPanel.setFontSizeList(fontSizeList);
        textPanel.setFontSize(properties.getFontSize());

        SchemaCanvasDialog2 dialog = new SchemaCanvasDialog2(context.getCanvasView(), true);
        dialog.addContent(textPanel);
        dialog.setTitle("テキスト入力");
        dialog.setVisible(true);

        int result = dialog.getResult();
        inputText = textPanel.getText();
        properties.setFontSize(textPanel.getFontSize());

        if (result == JOptionPane.OK_OPTION && inputText != null && !inputText.equals("")) {
            start = p;
            end = null;
            canvas.repaint();

        } else {
            start = null;
            end = null;
        }
    }

    @Override
    public void mouseDragged(Point p) {
    }

    @Override
    public void mouseUp(Point p) {
    }

    @Override
    public void draw(Graphics2D g2d) {

        if (inputText != null && start != null) {
            undoMgr.storeDraw();

            FontRenderContext ctx = g2d.getFontRenderContext();
            Font f = properties.getFont();

            TextLayout layout = new TextLayout(inputText, f, ctx);
            AffineTransform trans = AffineTransform.getTranslateInstance(start.getX(), start.getY());
            Shape outLine = layout.getOutline(trans);

            g2d.setStroke(properties.getTextStroke());
            g2d.setPaint(properties.getTextColor());
            g2d.fill(outLine);

            addTextShape(outLine);

            inputText = null;
        }
    }
}
