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
package open.dolphin.letter;

import open.dolphin.client.GUIConst;
import open.dolphin.client.Letter;

/**
 * LetterStateMgr
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class LetterStateMgr {

    private Letter letterImpl;
    private LetterState emptyState = new EmptyState();
    private LetterState cleanState = new CleanState();
//    private StartEditingState startEditingState = new StartEditingState();
    private DirtyState dirtyState = new DirtyState();
    private LetterState currentState;

    public LetterStateMgr(Letter letterImpl) {
        this.letterImpl = letterImpl;
        currentState = emptyState;
    }

    public void processEmptyEvent() {
        currentState = emptyState;
        this.enter();
    }

    public void processCleanEvent() {
        currentState = cleanState;
        this.enter();
    }

//    public void processModifyKarteEvent() {
//        currentState = startEditingState;
//        currentState.enter();
//    }
    public void processSavedEvent() {
        currentState = cleanState;
        currentState.enter();
    }

    public void processDirtyEvent() {
        boolean newDirty = letterImpl.letterIsDirty();
        currentState = newDirty ? dirtyState : emptyState;
        currentState.enter();
    }

    public boolean isDirtyState() {
        return currentState == dirtyState ? true : false;
    }

    public void enter() {
        currentState.enter();
    }

    protected abstract class LetterState {

        public LetterState() {
        }

        public abstract void enter();
    }

    protected final class EmptyState extends LetterState {

        public EmptyState() {
        }

        @Override
        public void enter() {
//minagawa^ LSC Test (新規カルテは
//            boolean canEdit = letterImpl.getContext().isReadOnly() ? false : true;            
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_NEW_KARTE, canEdit);
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_NEW_DOCUMENT, canEdit);
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_SAVE, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_DELETE, false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, false);
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_ASCENDING, false);
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_DESCENDING, false);
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_SHOW_MODIFIED, false);
//minagawa$            
        }
    }

    protected final class CleanState extends LetterState {

        public CleanState() {
        }

        @Override
        public void enter() {
            letterImpl.setEditables(false);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_SAVE, false);
//minagawa^ LSC Test             
            boolean canEdit = letterImpl.getContext().isReadOnly() ? false : true;
            letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, canEdit);   // 修正
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, true);             // Print
//minagawa$
        }
    }

//    class StartEditingState extends LetterState {
//
//        @Override
//        public void enter() {
//            letterImpl.setEditables(true);
//            letterImpl.setListeners();
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_SAVE, false);
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, true);
//            letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
//        }
//    }
    class DirtyState extends LetterState {

        @Override
        public void enter() {
            letterImpl.getContext().enabledAction(GUIConst.ACTION_SAVE, true);
            letterImpl.getContext().enabledAction(GUIConst.ACTION_PRINT, true);
            //letterImpl.getContext().enabledAction(GUIConst.ACTION_MODIFY_KARTE, false);
        }
    }
}
