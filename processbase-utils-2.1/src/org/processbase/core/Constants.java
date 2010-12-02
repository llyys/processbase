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
package org.processbase.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 *
 * @author mgubaidullin
 */
public class Constants {

    public static final String ACTION_ACCEPT = "ACTION_ACCEPT";
    public static final String ACTION_RETURN = "ACTION_RETURN";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_OPEN = "ACTION_OPEN";
    public static final String ACTION_SUSPEND = "ACTION_SUSPEND";
    public static final String ACTION_RESUME = "ACTION_RESUME";
    public static final String ACTION_ADD = "ACTION_ADD";
    public static final String ACTION_DELETE = "ACTION_DELETE";
    public static final String ACTION_EDIT = "ACTION_EDIT";
    public static final String ACTION_EDIT_PROCESSES = "ACTION_EDIT_PROCESSES";
    public static final String ACTION_ADD_UI = "ACTION_ADD_UI";
    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_RESTART = "ACTION_RESTART";
    public static final String ACTION_DELETE_PROCESS_AND_INSTANCES = "ACTION_DELETE_PROCESS_AND_INSTANCES";
    public static final String ACTION_DELETE_INSTANCES = "ACTION_DELETE_INSTANCES";
    public static final String ACTION_DELETE_PROCESS_INSTANCE = "ACTION_DELETE_PROCESS_INSTANCE";
    public static boolean LOADED = false;
    public static String TASKLIST_PAGE_URL;
    public static String TASKDEFAULT_PAGE_URL;
    public static Properties properties = new Properties();
    public static Hashtable BONITA_EJB_ENV = new Hashtable();
    public static String COMPANY_NAME = null;
    public static String DL_GROUP = null;
    public static String BAM_MQ_AddressList;
    public static String BAM_MQ_User;
    public static String BAM_MQ_Password;
    public static String BAM_MQ_DefaultDestination;
    public static String ESB_MQ_AddressList;
    public static String ESB_MQ_User;
    public static String ESB_MQ_Password;
    public static String ESB_MQ_DefaultDestination;

    public static void loadConstants() {
        try {
            File file = new File("processbase2.properties");
            FileInputStream fis = new FileInputStream(file);      
            properties.loadFromXML(fis);
            fis.close();
            TASKLIST_PAGE_URL = properties.getProperty("TASKLIST_PAGE_URL");
            TASKDEFAULT_PAGE_URL = properties.getProperty("TASKDEFAULT_PAGE_URL");
            System.setProperty("org.ow2.bonita.api-type", "EJB3");
            BONITA_EJB_ENV.put("org.ow2.bonita.api-type", "EJB3");
            BONITA_EJB_ENV.put("java.naming.factory.initial", properties.getProperty("java.naming.factory.initial"));
            BONITA_EJB_ENV.put("java.naming.factory.url.pkgs", properties.getProperty("java.naming.factory.url.pkgs"));
            BONITA_EJB_ENV.put("java.naming.factory.state", properties.getProperty("java.naming.factory.state"));
            BONITA_EJB_ENV.put("java.naming.provider.url", properties.getProperty("java.naming.provider.bonitaurl"));
            BONITA_EJB_ENV.put("java.security.auth.login.config", properties.getProperty("java.security.auth.login.config"));

            BAM_MQ_AddressList = properties.getProperty("BAM_MQ_AddressList");
            BAM_MQ_User = properties.getProperty("BAM_MQ_User");
            BAM_MQ_Password = properties.getProperty("BAM_MQ_Password");
            BAM_MQ_DefaultDestination = properties.getProperty("BAM_MQ_DefaultDestination");
            ESB_MQ_AddressList = properties.getProperty("ESB_MQ_AddressList");
            ESB_MQ_User = properties.getProperty("ESB_MQ_User");
            ESB_MQ_Password = properties.getProperty("ESB_MQ_Password");
            ESB_MQ_DefaultDestination = properties.getProperty("ESB_MQ_DefaultDestination");
            COMPANY_NAME = properties.getProperty("COMPANY_NAME");
            DL_GROUP = properties.containsKey("DL_GROUP") ? properties.getProperty("DL_GROUP") : "PROCESSBASE";
            LOADED = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
