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
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pbuser;

/**
 *
 * @author mgubaidullin
 */
public class NewClass {

    public static void main(String[] arg) throws FileNotFoundException, ClassNotFoundException, Exception {
        Locale.setDefault(Locale.ENGLISH);
        Locale locale = new Locale("fr");

        ResourceBundle messages = ResourceBundle.getBundle("resources/MessagesBundle", locale);
        System.out.println(messages.getString("btnDelete"));
        System.out.println(Locale.getDefault());
        }

    public static String createXML(String classname, Object object) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias(classname, Object.class);
        return xstream.toXML(object);
    }
}
