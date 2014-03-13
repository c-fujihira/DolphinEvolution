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
import java.awt.Toolkit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * RegexConstrainedDocument
 */
public final class RegexConstrainedDocument extends PlainDocument {

    private static final long serialVersionUID = 4066321190740323979L;
    
    boolean beep;
    boolean debug;
	
    Pattern pattern;
    Matcher matcher;
    
//s.oh^ 2013/09/12 PDF印刷文字サイズ
    int textLength;
//s.oh$
    
    public RegexConstrainedDocument () { 
    	super(); 
    }
    
    public RegexConstrainedDocument (AbstractDocument.Content c) {
    	super(c); 
    }
    
    public RegexConstrainedDocument (AbstractDocument.Content c, String p) {
        super (c);
        setPatternByString (p);
    }
    
    public RegexConstrainedDocument (String p) {
        super();
        setPatternByString (p);
    }
    
//s.oh^ 2013/09/12 PDF印刷文字サイズ
    public RegexConstrainedDocument (String p, int length) {
        super();
        setPatternByString (p);
        textLength = length;
    }
//s.oh$

    public void setPatternByString (String p) {
        Pattern lpattern = Pattern.compile (p);
        // check the document against the new pattern
        // and removes the content if it no longer matches
        try {
            matcher = lpattern.matcher (getText(0, getLength()));
            debug("matcher reset to " + getText (0, getLength()));
            if (! matcher.matches()) {
                debug ("does not match");
                remove (0, getLength());
            }
        } catch (BadLocationException ble) {
            ble.printStackTrace(System.err); // impossible?
        }
    }

    public Pattern getPattern() { 
    	return pattern; 
    }

    @Override
    public void insertString (int offs, String s, AttributeSet a) throws BadLocationException {
        // consider whether this insert will match
        //String proposedInsert = getText (0, offs) + s + getText (offs, getLength() - offs);
        String proposedInsert = getText (0, getLength()) + s ;
    	//String proposedInsert = s ;
        debug("proposing to change to: " + proposedInsert);
        if (matcher != null) {
            matcher.reset (proposedInsert);
            debug("matcher reset");
            if (! matcher.matches()) {
            	beep();
                debug("insert doesn't match");
                return;
            }
        }
//s.oh^ 2013/09/12 PDF印刷文字サイズ
        //super.insertString (offs, s, a);
        if(textLength <= 0) {
            super.insertString (offs, s, a);
        }else{
            if(this.getLength() + s.length() <= textLength) {
                super.insertString (offs, s, a);
            }
        }
//s.oh$
    }
    
    private void beep() {
    	if (beep) {
    		Toolkit.getDefaultToolkit().beep();
    	}
    }
    
    private void debug(String msg) {
    	if (debug) {
    		System.out.println(msg);
    	}
    }

}
