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
package open.dolphin.table;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

/**
 * ListTableModel
 *
 * @author Kazushi Minagawa, Digital Globe, Inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 * @param <T>
 */
public class ListTableModel<T> extends AbstractTableModel {

    public static final String COLLECTION_CHANGED = "collectionChanged";

    private String[] columnNames;

    private int startNumRows;

    private String[] propNames;

    private Class[] columnClasses;

    private List<T> dataProvider = new ArrayList<>();

    private PropertyChangeSupport boundSupport;

    public ListTableModel(String[] columnNames, int startNumRows) {
        super();
        this.columnNames = columnNames;
        this.startNumRows = startNumRows;
    }

    public ListTableModel(String[] columnNames, int startNumRows, String[] propNames, Class[] columnClasses) {
        this(columnNames, startNumRows);
        this.propNames = propNames;
        this.columnClasses = columnClasses;
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {

        if (boundSupport == null) {
            boundSupport = new PropertyChangeSupport(this);
        }

        boundSupport.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {

        if (boundSupport != null) {
            boundSupport.removePropertyChangeListener(l);
        }
    }

    @Override
    public int getRowCount() {
        return (dataProvider != null && dataProvider.size() > startNumRows)
                ? dataProvider.size()
                : startNumRows;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int index) {
        return (columnNames != null && index < columnNames.length)
                ? columnNames[index]
                : null;
    }

    @Override
    public Class getColumnClass(int index) {
        return (columnClasses != null && index < columnClasses.length) ? columnClasses[index]
                : Object.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {

        T object = getObject(rowIndex);

//minagawa^        
        if (propNames == null || propNames[columnIndex] == null) {
            return (Object) object;
        }
//minagawa$        

        if (object != null) {

            try {
                String prop = propNames[columnIndex];

                StringBuilder sb = new StringBuilder();
                if (prop.startsWith("get") || prop.startsWith("is")) {
                    sb.append(prop);
                } else {
                    sb.append("get");
                    sb.append(prop.substring(0, 1).toUpperCase());
                    sb.append(prop.substring(1));
                }
                prop = sb.toString();

                Method targetMethod = object.getClass().getMethod(prop, (Class[]) null);
                return targetMethod.invoke(object, (Object[]) null);

            } catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
                e.printStackTrace(System.err);
            }
        }

        return null;
    }

    public List<T> getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(List<T> dataProvider) {

        List<T> oldList = this.dataProvider;

        if (oldList != null) {
            oldList.clear();
        }

        this.dataProvider = dataProvider;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fireTableDataChanged();
            }
        });

        if (boundSupport != null && oldList != this.dataProvider) {
            boundSupport.firePropertyChange(COLLECTION_CHANGED, oldList, this.dataProvider);
        }
    }

    public void clear() {
        if (dataProvider != null) {
            dataProvider.clear();
            this.fireTableDataChanged();
        }
    }

    public void addObject(T obj) {
        if (dataProvider != null) {
            dataProvider.add(obj);
            this.fireTableDataChanged();
        }
    }

    public void addObject(int row, T obj) {
        if (dataProvider != null) {
            try {
                dataProvider.add(row, obj);
                this.fireTableDataChanged();
            } catch (Exception e) {
            }
        }
    }

    public void addAll(Collection<T> c) {
        if (dataProvider != null) {
            try {
                dataProvider.addAll(c);
                this.fireTableDataChanged();
            } catch (Exception e) {
            }
        }
    }

    public void deleteAt(int row) {
        if (dataProvider != null) {
            try {
                dataProvider.remove(row);
                this.fireTableDataChanged();
            } catch (Exception e) {
            }
        }
    }

    public void delete(T obj) {
        if (dataProvider != null) {
            try {
                dataProvider.remove(obj);
                this.fireTableDataChanged();
            } catch (Exception e) {

            }
        }
    }

    public void moveRow(int from, int to) {
        if (!isValidRow(from) || !isValidRow(to)) {
            return;
        }
        if (from == to) {
            return;
        }
        T move = dataProvider.remove(from);
        dataProvider.add(to, move);
        this.fireTableDataChanged();
    }

    public int getObjectCount() {
        return dataProvider != null ? dataProvider.size() : 0;
    }

    public T getObject(int row) {
        return isValidRow(row) ? dataProvider.get(row) : null;
    }

    public void setColumnName(String columnName, int col) {
        if (col >= 0 && col < columnNames.length) {
            columnNames[col] = columnName;
            this.fireTableStructureChanged();
        }
    }

    public void setColumnNames(String[] newNames) {
        if (newNames != null) {
            this.columnNames = newNames;
            this.fireTableStructureChanged();
        }
    }

    public void setProperty(String prop, int col) {
        if (propNames != null && col >= 0 && col < propNames.length) {
            propNames[col] = prop;
            if (dataProvider != null) {
                this.fireTableDataChanged();
            }
        }
    }

    public boolean isValidRow(int row) {
        return (dataProvider != null
                && (!dataProvider.isEmpty())
                && row >= 0
                && row < dataProvider.size());
    }

//masuda^
    public void setObject(int row, T obj) {
        if (dataProvider != null) {
            try {
                dataProvider.set(row, obj);
                this.fireTableDataChanged();
            } catch (Exception e) {
            }
        }
    }

    public String getProperty(int col) {
        if (propNames != null && col >= 0 && col < propNames.length) {
            return propNames[col];
        }
        return null;
    }
//masuda$

}
