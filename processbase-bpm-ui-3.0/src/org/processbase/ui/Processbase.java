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
package org.processbase.ui;

import com.liferay.portal.model.User;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.util.DocumentLibraryUtil;
import org.processbase.core.Constants;

/**
 *
 * @author mgubaidullin
 */
public class Processbase {

    private static ThreadLocal<Processbase> currentProcessbase = new ThreadLocal<Processbase>();
    public PortletApplicationContext2 portletApplicationContext2;
    private PortletSession portletSession;
    public HttpSession httpSession;
    public BPMModule bpmModule = null;
    public ResourceBundle messages = null;
    public DocumentLibraryUtil documentLibraryUtil = null;
    public String userName = null;
    public User portalUser;
    public Locale locale;
    public static int LIFERAY_PORTAL = 0;
    public static int STANDALONE = 1;
    public int type = LIFERAY_PORTAL;

    public Processbase() {
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }
    }

    public void setSessionAttribute(String name, String value) {
        if (this.type == LIFERAY_PORTAL) {
            Processbase.getCurrent().portletSession.setAttribute("PROCESSBASE_SHARED_" + name, value, PortletSession.APPLICATION_SCOPE);
        } else if (this.type == STANDALONE) {
            httpSession.setAttribute(value, name);
        }
    }

    public void removeSessionAttribute(String name) {
        if (this.type == LIFERAY_PORTAL) {
            Processbase.getCurrent().portletSession.removeAttribute("PROCESSBASE_SHARED_" + name, PortletSession.APPLICATION_SCOPE);
        } else if (this.type == STANDALONE) {
            httpSession.removeAttribute(name);
        }
    }

    public void getSessionAttribute(String name) {
        if (this.type == LIFERAY_PORTAL) {
            Processbase.getCurrent().portletSession.getAttribute("PROCESSBASE_SHARED_" + name, PortletSession.APPLICATION_SCOPE);
        } else if (this.type == STANDALONE) {
            httpSession.getAttribute(name);
        }
    }

    /**
     * @return the current application instance
     */
    public static Processbase getCurrent() {
        return currentProcessbase.get();
    }

    /**
     * Set the current application instance
     */
    public static void setCurrent(Processbase application) {
        if (getCurrent() == null) {
            currentProcessbase.set(application);
        }
    }

    /**
     * Remove the current application instance
     */
    public static void removeCurrent() {
        currentProcessbase.remove();
    }

    public BPMModule getBpmModule() {
        return bpmModule;
    }

    public void setBpmModule(BPMModule bpmModule) {
        this.bpmModule = bpmModule;
    }

    public DocumentLibraryUtil getDocumentLibraryUtil() {
        return documentLibraryUtil;
    }

    public void setDocumentLibraryUtil(DocumentLibraryUtil documentLibraryUtil) {
        this.documentLibraryUtil = documentLibraryUtil;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public ResourceBundle getMessages() {
        return messages;
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

    public PortletApplicationContext2 getPortletApplicationContext2() {
        return portletApplicationContext2;
    }

    public void setPortletApplicationContext2(PortletApplicationContext2 portletApplicationContext2) {
        this.portletApplicationContext2 = portletApplicationContext2;
    }

    public PortletSession getPortletSession() {
        return portletSession;
    }

    public void setPortletSession(PortletSession portletSession) {
        this.portletSession = portletSession;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public User getPortalUser() {
        return portalUser;
    }

    public void setPortalUser(User portalUser) {
        this.setUserName(portalUser.getScreenName());
        this.portalUser = portalUser;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
