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
package open.dolphin.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * PluginLister
 *
 * @author Kazushi Minagawa. Digital Globe, inc.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PluginLister<S> {

    private static final String PREFIX = "META-INF/plugins/";

    // ロードするプラグインのインターフェイス
    private Class<S> plugin;

    // クラスローダ
    private ClassLoader loader;

    /**
     * Creates a new instance of PluginLoader
     */
    private PluginLister(Class<S> plugin, ClassLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
    }

    private static void fail(Class plugin, String msg, Throwable cause) throws PluginConfigurationError {
        throw new PluginConfigurationError(plugin.getName() + ": " + msg, cause);
    }

    private static void fail(Class plugin, String msg) throws PluginConfigurationError {
        throw new PluginConfigurationError(plugin.getName() + ": " + msg);
    }

    private static void fail(Class plugin, URL u, int line, String msg) throws PluginConfigurationError {
        fail(plugin, u + ":" + line + ": " + msg);
    }

    public LinkedHashMap<String, String> getProviders() {

        LinkedHashMap<String, String> providers = new LinkedHashMap<String, String>(10);

        try {
            String fullName = PREFIX + plugin.getName();
            Enumeration<URL> configs = loader.getResources(fullName);

            while (configs.hasMoreElements()) {

                URL url = configs.nextElement();
                InputStream in = url.openStream();
                BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line;
                while ((line = r.readLine()) != null) {
                    line = line.trim();
                    Scanner s = new Scanner(line).useDelimiter("\\s*,\\s*");
                    String menu = s.next();
                    String cmd = s.next();
                    String value = s.next();
                    providers.put(cmd, value);
                }

                r.close();
                in.close();
            }

        } catch (IOException x) {
            fail(plugin, "Error reading plugin configuration files", x);
        }

        return providers;
    }

    public static <S> PluginLister<S> list(Class<S> plugin, ClassLoader loader) {
        return new PluginLister<S>(plugin, loader);
    }

    public static <S> PluginLister<S> list(Class<S> plugin) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return PluginLister.list(plugin, cl);
    }
}
