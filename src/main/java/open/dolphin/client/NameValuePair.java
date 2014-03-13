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

/**
 * @author Kazushi Minagawa Digital Globe, Inc.
 *
 */
public final class NameValuePair {
    
    private String value;
    private String name;
    
    public static int getIndex(NameValuePair test, NameValuePair[] cnArray) {
        int index = 0;
        for (int i = 0; i < cnArray.length; i++) {
            if (test.equals(cnArray[i])) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    public static int getIndex(String test, NameValuePair[] cnArray) {
        int index = 0;
        for (int i = 0; i < cnArray.length; i++) {
            if (test.equals(cnArray[i].getValue())) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    public NameValuePair() {
    }
    
    public NameValuePair(String name, String value) {
        this();
        setName(name);
        setValue(value);
    }
    
    public void setValue(String code) {
        this.value = code;
    }
    
    public String getValue() {
        return value;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public int hashCode() {
        return value.hashCode() + 15;
    }
    
    @Override
    public boolean equals(Object other) {
        if (other != null && getClass() == other.getClass()) {
            String otherValue = ((NameValuePair)other).getValue();
            return value.equals(otherValue);
        }
        return false;
    }
    
    public int compareTo(Object other) {
        if (other != null && getClass() == other.getClass()) {
            String otherValue = ((NameValuePair)other).getValue();
            return value.compareTo(otherValue);
        }
        return -1;
    }
}
