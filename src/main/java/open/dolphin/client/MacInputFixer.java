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

import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.CharacterIterator;
import javax.swing.text.JTextComponent;

/**
 * マックの上下キー問題 2013/06/24
 * @author kazushi
 */
public final class MacInputFixer  {
    
    private boolean typing;
    
    public MacInputFixer() {
    }
    
    public void fix(JTextComponent textComponent) {
        
        textComponent.addInputMethodListener(new InputMethodListener() {

            @Override
            public void inputMethodTextChanged(InputMethodEvent e) {
                if (e.getCommittedCharacterCount() > 0) {
                    typing = false; // 確定
                }
                else if (e.getText().first() == CharacterIterator.DONE) {
                    typing = false; // キャンセル
                }
                else {
                    typing = true; // 入力中
                }
                JTextComponent tcm = (JTextComponent)e.getSource();
                // 入力中はキャレットを非表示
                tcm.getCaret().setVisible(!typing);
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {
            }
        });
        textComponent.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                if (typing) {
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (typing) {
                    e.consume();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (typing) {
                    e.consume();
                }
            }
        });
        textComponent.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (typing) {
                    e.consume();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (typing) {
                    e.consume();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (typing) {
                    e.consume();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
}
