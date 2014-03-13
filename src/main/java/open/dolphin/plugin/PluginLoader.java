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
 * PluginLoader
 *
 * @author Kazushi Minagawa.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 * @param <S>
 */
public final class PluginLoader<S> implements Iterable<S> {

    private static final String PREFIX = "META-INF/plugins/";

    // ロードするプラグインのインターフェイス
    private final Class<S> plugin;

    // クラスローダ
    private final ClassLoader loader;

    // 生成順のキャッシュプロバイダ
    private final LinkedHashMap<String, S> providers = new LinkedHashMap<>();

    // 現在の遅延lookp 反復子
    private LazyIterator lookupIterator;

    public void reload() {
        providers.clear();
        lookupIterator = new LazyIterator(plugin, loader);
    }

    /**
     * Creates a new instance of PluginLoader
     */
    private PluginLoader(Class<S> plugin, ClassLoader loader) {
        this.plugin = plugin;
        this.loader = loader;
        reload();
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

    private int parseLine(Class plugin, URL u, BufferedReader r, int lc, List<String> names)
            throws IOException, PluginConfigurationError {

        String ln = r.readLine();

        if (ln == null) {
            return -1;
        }

        int ci = ln.indexOf("#");
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }

        ln = ln.trim();
        int n = ln.length();
        if (n != 0) {
            if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0)) {
                fail(plugin, u, lc, "Illegal configuration-file syntax");
            }
            int cp = ln.codePointAt(0);
            if (!Character.isJavaIdentifierStart(cp)) {
                fail(plugin, u, lc, "Illegal provider-class name: " + ln);
            }
            for (int i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
                cp = ln.codePointAt(i);
                if (!Character.isJavaIdentifierPart(cp) && (cp != '.')) {
                    fail(plugin, u, lc, "Illegal provider-class name: " + ln);
                }
            }
            if (!providers.containsKey(ln) && !names.contains(ln)) {
                names.add(ln);
            }
        }

        return lc + 1;
    }

    private Iterator<String> parse(Class plugin, URL u) throws PluginConfigurationError {

        InputStream in = null;
        BufferedReader r = null;
        ArrayList<String> names = new ArrayList<>();

        try {
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = parseLine(plugin, u, r, lc, names)) >= 0) {
            }

        } catch (IOException x) {
            fail(plugin, "Error reading configuration file", x);
        } finally {
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException y) {
                fail(plugin, "Error closing configuration file", y);
            }
        }

        return names.iterator();
    }

    private class LazyIterator implements Iterator<S> {

        Class<S> plugin;
        ClassLoader loader;
        Enumeration<URL> configs;
        Iterator<String> pending;
        String nextName;

        private LazyIterator(Class<S> plugin, ClassLoader loader) {
            this.plugin = plugin;
            this.loader = loader;
        }

        @Override
        public boolean hasNext() {

            if (nextName != null) {
                return true;
            }

            if (configs == null) {
                try {
                    String fullName = PREFIX + plugin.getName();
                    if (loader == null) {
                        configs = ClassLoader.getSystemResources(fullName);
                    } else {
                        configs = loader.getResources(fullName);
                    }

                } catch (IOException x) {
                    fail(plugin, "Error locating configuration files", x);
                }
            }

            while ((pending == null) || (!pending.hasNext())) {
                if (!configs.hasMoreElements()) {
                    return false;
                }
                pending = parse(plugin, configs.nextElement());
            }

            nextName = pending.next();
            return true;
        }

        @Override
        public S next() {

            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            String cn = nextName;
            nextName = null;

            try {
                S p = plugin.cast(Class.forName(cn, true, loader).newInstance());
                providers.put(cn, p);
                return p;

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException x) {
                fail(plugin, "Provider " + cn + " could not be instantiated: " + x);
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

            Iterator<Map.Entry<String, S>> knownProviders
                    = providers.entrySet().iterator();

            @Override
            public boolean hasNext() {
                if (knownProviders.hasNext()) {
                    return true;
                }
                return lookupIterator.hasNext();
            }

            @Override
            public S next() {
                if (knownProviders.hasNext()) {
                    return knownProviders.next().getValue();
                }
                return lookupIterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };
    }

    public LinkedHashMap<String, S> loadAll() {
        reload();
        Iterator<S> iter = iterator();
        while (iter.hasNext()) {
            iter.next();
        }
        return providers;
    }

    public static <S> PluginLoader<S> load(Class<S> plugin, ClassLoader loader) {
        return new PluginLoader<>(plugin, loader);
    }

    public static <S> PluginLoader<S> load(Class<S> plugin) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return PluginLoader.load(plugin, cl);
    }
}
