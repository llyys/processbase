/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
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

package org.processbase.ui.core.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 *
 * @author marat
 */
public class XMLManager {

    public static String createXML(String classname, Object object) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias(classname, Object.class);
        return xstream.toXML(object);
    }

    public static Object createObject(String xml) {
        XStream xstream = new XStream(new DomDriver());
        xstream.setClassLoader(ClassLoader.getSystemClassLoader());
        return xstream.fromXML(xml);
    }
}
