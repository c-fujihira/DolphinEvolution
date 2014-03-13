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
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.ActionMap;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.event.*;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import javax.swing.undo.UndoManager;

/**
 *
 * @author kazm
 */
public class BaseTextPane implements FocusListener, CaretListener, MouseListener, DocumentListener, UndoableEditListener {
    
    public static final String EDITOR_PROP = "editorProp";
    
    public static final String DIRTY_PROP = "dirtyProp";
    
    public static final String CAN_UNDO = "canUndoProp";
    
    public static final String CAN_REDO = "canRedoProp";
    
    public static final String CAN_CUT_COPY = "canCutCopyProp";
    
    public static final String CAN_PASTE = "canPsteProp";
    
    //private Logger logger = Logger.getLogger(this.getClass().getName());
    
    private JTextPane textPane;
    
    private boolean selected;
    
    private boolean canUndo;
    
    private boolean canRedo;
    
    private boolean canCutCopy;
    
    private boolean canPaste;
    
    private int initialLength;
    
    private boolean dirty;
    
    private PropertyChangeSupport boundSupport;
    
    protected UndoManager undoManager;
    
    
    /** Creates a new instance of SimplTextPane */
    public BaseTextPane() {
        initComponents(new DefaultStyledDocument());
    }
    
    public BaseTextPane(StyledDocument doc) {
        initComponents(doc);
    }
        
    private void initComponents(StyledDocument doc) {
        textPane = new JTextPane(doc);
        textPane.addFocusListener(this);
        textPane.addCaretListener(this);
        textPane.addMouseListener(this);
        textPane.getDocument().addDocumentListener(this);
        textPane.getDocument().addUndoableEditListener(this);
        undoManager = new UndoManager();
        canPaste = true;
        textPane.putClientProperty("baseTextPane", this);
        printActions();
    }
    
    public JTextPane getTextPane() {
        return textPane;
    }
    
    public ActionMap getActionMap() {
        return textPane.getActionMap();
    }
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }
        boundSupport.removePropertyChangeListener(listener);
    }
    
    public void printActions() {
        
        ActionMap map = textPane.getActionMap();
        if (map != null) {
            Object[] keys = map.allKeys();
            if (keys != null) {
                for (Object o : keys) {
                    //logger.info(o.toString());
                }
            } else {
                //logger.info("keys are null");
            }
        } else {
            //logger.info("ActionMap is null");
        }
    }
    
    public boolean isCanUndo() {
        return canUndo;
    }
    
    public void setCanUndo(boolean b) {
        boolean old = this.canUndo;
        this.canUndo = b;
        boundSupport.firePropertyChange(CAN_UNDO, old, this.canUndo);
    }
    
    public boolean isCanRedo() {
        return canRedo;
    }
    
    public void setCanRedo(boolean b) {
        boolean old = this.canRedo;
        this.canRedo = b;
        boundSupport.firePropertyChange(CAN_REDO, old, this.canRedo);
    }
    
    public boolean isCanCutCopy() {
        return canCutCopy;
    }
    
    public void setCanCutCopy(boolean b) {
        boolean old = this.canCutCopy;
        this.canCutCopy = b;
        boundSupport.firePropertyChange(CAN_CUT_COPY, old, this.canCutCopy);
    }
    
    public boolean isCanPaste() {
        return canPaste;
    }
    
    public void setCanPaste(boolean b) {
        boolean old = this.canPaste;
        this.canPaste = b;
        boundSupport.firePropertyChange(CAN_PASTE, old, this.canPaste);
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    private void setSelected(boolean selected) {
        boolean old = this.selected;
        this.selected = selected;
        if (old != this.selected) {
            setCanCutCopy(this.isSelected());
        }
    }
    
    public void undo() {
        undoManager.undo();
        update();
    }
    
    public void redo() {
        undoManager.redo();
        update();
    }
    
    @Override
    public void focusGained(FocusEvent e) {
        //System.err.println("focusGained");
    }
    
    @Override
    public void focusLost(FocusEvent e) {
        //System.err.println("focusGained");
    }
    
    @Override
    public void caretUpdate(CaretEvent e) {
        if (e.getDot() != e.getMark()) {
            //logger.info("selected");
            setSelected(true);
        } else {
            //logger.info("no selection");
            setSelected(false);
        }
    }
    
    protected void popup(Component c, int x, int y) {
        //System.err.println("popup");
    }
    
    private void mabeShowPopup(MouseEvent e) {
        if (e.isPopupTrigger()) {
            popup(e.getComponent(), e.getX(), e.getY());
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        mabeShowPopup(e);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        mabeShowPopup(e);
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        //System.err.println("mouseEntered");
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        //System.err.println("mouseExited");
    }
    
    public boolean isDirty() {
        return dirty;
    }
    
    private void setDirty(boolean dirty) {
        boolean old = this.dirty;
        this.dirty = dirty;
        if (old != this.dirty) {
            boundSupport.firePropertyChange(DIRTY_PROP, old, this.dirty);
        }
    }
    
    protected void changed() {
        int len = textPane.getDocument().getLength();
        boolean d = initialLength != len ? true : false;
        setDirty(d);
    }
    
    @Override
    public void insertUpdate(DocumentEvent e) {
        changed();
    }
    
    @Override
    public void removeUpdate(DocumentEvent e) {
        changed();
    }
    
    @Override
    public void changedUpdate(DocumentEvent e) {
        changed();
    }
    
    private void update() {
        setCanUndo(undoManager.canUndo());
        setCanRedo(undoManager.canRedo());
    }
    
    @Override
    public void undoableEditHappened(UndoableEditEvent e) {
        //logger.info("undoableEditHappened");
        undoManager.addEdit(e.getEdit());
        update();
    }
    
    public boolean isEditable() {
        return textPane.isEditable();
    }
    
    public void setEditable(boolean editable) {
        textPane.setEditable(editable);
    }
    
    public Insets getMargin() {
        return textPane.getMargin();
    }
    
    public void setMargin(Insets margin) {
        textPane.setMargin(margin);
    }
    
    public Color getBackground() {
        return textPane.getBackground();
    }

    public void setBackground(Color color) {
        textPane.setBackground(color);
    }
    
    public TransferHandler getTrasnferHandler() {
        return textPane.getTransferHandler();
    }
    
    public void setTrasnferHandlerTransferHandler(TransferHandler handler) {
        textPane.setTransferHandler(handler);
    }
}
