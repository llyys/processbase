/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 *
 * @author mgubaidullin
 */
public class XMLManager {

    public static String createXML(String classname, Object object) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias(classname, Object.class);
        return xstream.toXML(object);
    }

    public static Object createObject(String xml) {
        XStream xstream = new XStream(new DomDriver());
        xstream.setClassLoader(ProcessBaseClassLoader.getCurrent());
        return xstream.fromXML(xml);
    }
}
