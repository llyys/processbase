/**
 * Copyright (C) 2011 PROCESSBASE
 * PROCESSBASE Ltd, Almaty, Kazakhstan
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
package org.processbase.ui.core;

import java.util.ResourceBundle;
import org.processbase.ui.osgi.PbPanelModuleService;

/**
 *
 * @author mgubaidullin
 */
public interface Processbase {

//    static ThreadLocal<Processbase> currentProcessbase = new ThreadLocal<Processbase>();
    static int LIFERAY_PORTAL = 0;
    static int STANDALONE = 1;

    public int getApplicationType();

    public void setSessionAttribute(String name, String value);

    public void removeSessionAttribute(String name);

    public void getSessionAttribute(String name);

    public String getUserName();

    public void setUserName(String userName);

//    public User getPortalUser();
//
//    public void setPortalUser(User portalUser);

    public BPMModule getBpmModule();

    public void setBpmModule(BPMModule bpmModule);

    public ResourceBundle getMessages();

    public void setMessages(ResourceBundle messages);

    public void saveFile(String processUUID, String name, String fileName, byte[] fileBody) throws Exception;

    public byte[] getFileBody(String processUUID, String name) throws Exception;

    public PbPanelModuleService getPanelModuleService();
}
