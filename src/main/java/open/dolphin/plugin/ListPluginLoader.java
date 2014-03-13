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
import java.util.*;

/**
 * ListPluginLoader
 *
 * @author Kazushi Minagawa.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 * @param <S>
 */
public class ListPluginLoader<S> implements Iterable<S> {

    private static final String PREFIX = "META-INF/plugins/";

    // ロードするプラグインのインターフェイス
    private final Class<S> plugin;

    // クラスローダ
    private final ClassLoader loader;

    // プロバイダキャッシュ
    private final HashMap<String, S> providers;

    // 実際のプラグイン反復子
    private final ActualIterator actualIterator;

    /**
     * Creates a new instance of PluginLoader
     */
    private ListPluginLoader(Class<S> plugin, ClassLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        providers = new HashMap<>();
        actualIterator = new ActualIterator(plugin, loader);
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

    protected class IdValuePair {

        private final String id;
        private final String value;

        public IdValuePair(String id, String value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * プラグイン反復子の実際の機能を提供する内部クラス。
     */
    private class ActualIterator implements Iterator<S> {

        Class<S> plugin;
        ClassLoader loader;
        Enumeration<URL> configs;
        Iterator<IdValuePair> iterator;

        private ActualIterator(Class<S> plugin, ClassLoader loader) {

            this.plugin = plugin;
            this.loader = loader;

            try {
                String fullName = PREFIX + plugin.getName();
                configs = loader.getResources(fullName);

            } catch (Exception x) {
                fail(plugin, "Error locating plugin configuration files", x);
            }

            try {

                ArrayList<IdValuePair> allPlugins = new ArrayList<>();

                while (configs.hasMoreElements()) {

                    URL url = configs.nextElement();
                    try (InputStream in = url.openStream(); BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"))) {
                        String line;
                        while ((line = r.readLine()) != null) {
                            line = line.trim();
                            int index = line.indexOf("=");
                            if (index > 0) {
                                String id = line.substring(0, index++);
                                String value = line.substring(index);
                                allPlugins.add(new IdValuePair(id, value));
                            }
                        }
                    }
                }

                iterator = allPlugins.iterator();

            } catch (IOException x) {
                fail(plugin, "Error reading plugin configuration files", x);
            }
        }

        @Override
        public boolean hasNext() {

            return iterator.hasNext();
        }

        @Override
        public S next() {

            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            IdValuePair idValue = iterator.next();
            String className = idValue.getValue();
            String id = idValue.getId();

            try {
                S p = plugin.cast(Class.forName(className, true, loader).newInstance());
                providers.put(id, p);
                return p;

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException x) {
                fail(plugin, "Provider " + className + " could not be instantiated: " + x, x);
            }

            throw new Error();		// This cannot happen
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<S> iterator() {

        return new Iterator<S>() {

            @Override
            public boolean hasNext() {
                return actualIterator.hasNext();
            }

            @Override
            public S next() {
                return actualIterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public HashMap<String, S> getProviders() {
        return providers;
    }

    public static <S> ListPluginLoader<S> load(Class<S> plugin, ClassLoader loader) {
        return new ListPluginLoader<>(plugin, loader);
    }

    public static <S> ListPluginLoader<S> load(Class<S> plugin) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return ListPluginLoader.load(plugin, cl);
    }
}
