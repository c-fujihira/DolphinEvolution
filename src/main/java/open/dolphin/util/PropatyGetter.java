/*
 * Copyright (C) 2013 S&I Co.,Ltd.
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
package open.dolphin.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PropatyGetter Class
 *
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class PropatyGetter {

    private final Properties config = new Properties();
    String propString;

    public PropatyGetter() {
        try {
            Class c = this.getClass();
            URL url = c.getResource("/resources/config/app.properties");
            InputStream inputStream = url.openStream();
            config.load(inputStream);
        } catch (IOException e) {
            Logger.getLogger(PropatyGetter.class.getName()).
                    log(Level.SEVERE, "Load Error.", e);
        }
    }

    /**
     * 設定情報の読み込み
     *
     * @param configPath
     * @return String
     */
    public String get(String configPath) {
        return config.getProperty(configPath);
    }
}
