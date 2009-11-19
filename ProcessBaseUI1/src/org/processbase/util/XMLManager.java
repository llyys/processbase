/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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
