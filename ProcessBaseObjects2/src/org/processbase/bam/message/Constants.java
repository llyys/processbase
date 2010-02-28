/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
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
package org.processbase.bam.message;

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 *
 * @author mgubaidullin
 */
public class Constants {

    public static boolean LOADED = false;
    public static Properties properties = new Properties();
    public static Hashtable EJB_ENV = new Hashtable();

    public static void loadConstants() {
        try {
            File file = new File("processbase2.properties");
            FileInputStream fis = new FileInputStream(file);
            properties.loadFromXML(fis);
            fis.close();
            EJB_ENV.put("java.naming.factory.initial", properties.getProperty("java.naming.factory.initial"));
            EJB_ENV.put("java.naming.factory.url.pkgs", properties.getProperty("java.naming.factory.url.pkgs"));
            EJB_ENV.put("java.naming.factory.state", properties.getProperty("java.naming.factory.state"));
            EJB_ENV.put("java.naming.provider.url", properties.getProperty("java.naming.provider.bamurl"));
            EJB_ENV.put("java.security.auth.login.config", properties.getProperty("java.security.auth.login.config"));
            LOADED = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
