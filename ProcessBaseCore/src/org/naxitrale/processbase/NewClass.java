/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 *
 * @author mgubaidullin
 */
public class NewClass {

    public static void main(String[] arg) throws FileNotFoundException, ClassNotFoundException, Exception {
        System.out.println(ResourceBundle.getBundle("resources/MessagesBundle", new Locale("en_US")).getString("loginWindowCaption"));
        System.out.println(ResourceBundle.getBundle("resources/MessagesBundle", new Locale("ru")).getString("loginWindowCaption"));
    }

    public static String createXML(String classname, Object object) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias(classname, Object.class);
        return xstream.toXML(object);
    }
}
