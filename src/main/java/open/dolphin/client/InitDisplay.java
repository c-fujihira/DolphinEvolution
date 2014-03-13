/*
 * Copyright (C) 2013 SANDI
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
package open.dolphin.client;

import static java.awt.Toolkit.getDefaultToolkit;

/**
 *
 * @author hellmaster
 */
public class InitDisplay {

    public void InitDisplay() {
    }

    public static void main(String[] args) {
        System.setProperty("javafx.macosx.embedded", "true");
        getDefaultToolkit();
        final String[] trace = args;
        final Evolution ev = new Evolution();
        ev.main(trace);
    }
}
