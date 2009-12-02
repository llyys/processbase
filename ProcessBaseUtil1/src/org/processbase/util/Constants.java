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

import java.io.File;
import java.io.FileInputStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mgubaidullin
 */
public class Constants {

    public static final String ACTION_ACCEPT = "ACTION_ACCEPT";
    public static final String ACTION_RETURN = "ACTION_RETURN";
    public static final String ACTION_HELP = "ACTION_HELP";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_OPEN = "ACTION_OPEN";
    public static final String ACTION_SUSPEND = "ACTION_SUSPEND";
    public static final String ACTION_RESUME = "ACTION_RESUME";
    public static final String ACTION_ADD = "ACTION_ADD";
    public static final String ACTION_DELETE = "ACTION_DELETE";
    public static final String ACTION_EDIT = "ACTION_EDIT";
    public static final String ACTION_EDIT_PROCESSES = "ACTION_EDIT_PROCESSES";
    public static final String ACTION_EDIT_PARTICIPANTS = "ACTION_EDIT_PARTICIPANTS";
    public static final String ACTION_ADD_UI = "ACTION_ADD_UI";
    public static final String ACTION_DELETE_PROCESS_AND_INSTANCES = "ACTION_DELETE_PROCESS_AND_INSTANCES";
    public static final String ACTION_DELETE_INSTANCES = "ACTION_DELETE_INSTANCES";
    public static final String ACTION_DELETE_PROCESS_INSTANCE = "ACTION_DELETE_PROCESS_INSTANCE";
    public static boolean LOADED = false;
    public static String INITIAL_CONTEXT_FACTORY;
    public static String DN_NAMIND_ATTRIBUTE;
    public static String BASE_DN;
    public static String BASE_PEOPLE_DN;
    public static String BASE_GROUP_DN;
    public static String LDAP_ADMIN_USERNAME;
    public static String LDAP_ADMIN_PASSWORD;
    public static String LDAP_HOST;
    public static String LDAP_PORT;
    public static String UI_LIBS_PATH;
    public static Properties properties = new Properties();
    public static Hashtable EJB_ENV = new Hashtable();

    public static void loadConstants() {
        try {
            File file = new File("processbase1.properties");
            FileInputStream fis = new FileInputStream(file);
            properties.loadFromXML(fis);
            fis.close();
            BASE_DN = properties.getProperty("BASE_DN");
            BASE_PEOPLE_DN = properties.getProperty("BASE_PEOPLE_DN");
            BASE_GROUP_DN = properties.getProperty("BASE_GROUP_DN");
            LDAP_ADMIN_USERNAME = properties.getProperty("LDAP_ADMIN_USERNAME");
            LDAP_ADMIN_PASSWORD = properties.getProperty("LDAP_ADMIN_PASSWORD");
            LDAP_HOST = properties.getProperty("LDAP_HOST");
            LDAP_PORT = properties.getProperty("LDAP_PORT");
            UI_LIBS_PATH = properties.getProperty("UI_LIBS_PATH");
            INITIAL_CONTEXT_FACTORY = properties.getProperty("INITIAL_CONTEXT_FACTORY");
            DN_NAMIND_ATTRIBUTE = properties.getProperty("DN_NAMIND_ATTRIBUTE");
            EJB_ENV.put("java.naming.factory.initial", properties.getProperty("java.naming.factory.initial"));
            EJB_ENV.put("java.naming.factory.url.pkgs", properties.getProperty("java.naming.factory.url.pkgs"));
            EJB_ENV.put("java.naming.factory.state", properties.getProperty("java.naming.factory.state"));
            EJB_ENV.put("java.naming.provider.url", properties.getProperty("java.naming.provider.url"));
            EJB_ENV.put("java.security.auth.login.config", properties.getProperty("java.security.auth.login.config"));
            LOADED = true;
        } catch (Exception ex) {
            Logger.getLogger(Constants.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }
}
