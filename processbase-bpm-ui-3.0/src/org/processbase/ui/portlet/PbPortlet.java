/**
 * Copyright (C) 2010 PROCESSBASE
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
package org.processbase.ui.portlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.util.DocumentLibraryUtil;
import org.processbase.core.Constants;
import org.processbase.ui.Processbase;
import org.processbase.ui.panel.BPMConfigurationPanel;
import org.processbase.ui.panel.BAMConfigurationPanel;
import org.processbase.ui.panel.ConsolePanel;
import org.processbase.ui.panel.IdentityPanel;
import org.processbase.ui.panel.BPMMonitoringPanel;
import org.processbase.ui.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class PbPortlet extends Application implements Processbase, PortletRequestListener {

    PbWindow mainWindow;
    PortletApplicationContext2 portletApplicationContext2 = null;
    PortletSession portletSession = null;
    BPMModule bpmModule = null;
    ResourceBundle messages = null;
    DocumentLibraryUtil documentLibraryUtil = null;
    String userName = null;
//    User portalUser = null;
    Locale locale = null;
    int type = LIFERAY_PORTAL;
    private boolean inited = false;

    @Override
    public void init() {
//        System.out.println("PbPortlet init ");
        setTheme("processbase");
        setLogoutURL(Constants.TASKLIST_PAGE_URL);
        setPortletApplicationContext2((PortletApplicationContext2) getContext());
        PortletConfig config = getPortletApplicationContext2().getPortletConfig();
        mainWindow = new PbWindow("Processbase User Portlet");
        mainWindow.setSizeFull();
        setMainWindow(mainWindow);
        if (config.getInitParameter("ui").equalsIgnoreCase("ConsolePanel")) {
            ConsolePanel ui = new ConsolePanel();
            mainWindow.setContent(ui);
            ui.initUI();
        } else if (config.getInitParameter("ui").equalsIgnoreCase("AdminPanel")) {
            BPMConfigurationPanel ui = new BPMConfigurationPanel();
            mainWindow.setContent(ui);
            ui.initUI();
        } else if (config.getInitParameter("ui").equalsIgnoreCase("IdentityPanel")) {
            IdentityPanel ui = new IdentityPanel();
            mainWindow.setContent(ui);
            ui.initUI();
        } else if (config.getInitParameter("ui").equalsIgnoreCase("BAMPanel")) {
            BAMConfigurationPanel ui = new BAMConfigurationPanel();
            mainWindow.setContent(ui);
            ui.initUI();
        } else if (config.getInitParameter("ui").equalsIgnoreCase("MonitoringPanel")) {
            BPMMonitoringPanel ui = new BPMMonitoringPanel();
            mainWindow.setContent(ui);
            ui.initUI();
        }
        
    }

    public void onRequestStart(PortletRequest request, PortletResponse response) {
//        System.out.println("PbPortlet onRequestStart ");
        if (!inited) {
            try {
                User user = PortalUtil.getUser(request);
//                setPortalUser(user);
                setUserName(user.getScreenName());
                setLocale(request.getLocale());
                setMessages(ResourceBundle.getBundle("resources/MessagesBundle", getLocale()));
                setBpmModule(new BPMModule(user.getScreenName()));
                setDocumentLibraryUtil(new DocumentLibraryUtil(user));
                setPortletSession(request.getPortletSession());

            } catch (PortalException e) {
                e.printStackTrace();
            } catch (SystemException e) {
                e.printStackTrace();
            }
        }
    }

    public void onRequestEnd(PortletRequest request, PortletResponse response) {
    }

//    /**
//     * @return the current application instance
//     */
//    public static Processbase getCurrent() {
//        return currentProcessbase.get();
//    }
//
//    /**
//     * Set the current application instance
//     */
//    public static void setCurrent(Processbase application) {
//        if (getCurrent() == null) {
//            currentProcessbase.set(application);
//        }
//    }
//
//    /**
//     * Remove the current application instance
//     */
//    public static void removeCurrent() {
//        currentProcessbase.remove();
//    }

    public void setSessionAttribute(String name, String value) {
        getPortletSession().setAttribute("PROCESSBASE_SHARED_" + name, value, PortletSession.APPLICATION_SCOPE);
    }

    public void removeSessionAttribute(String name) {
        getPortletSession().removeAttribute("PROCESSBASE_SHARED_" + name, PortletSession.APPLICATION_SCOPE);
    }

    public void getSessionAttribute(String name) {
        getPortletSession().getAttribute("PROCESSBASE_SHARED_" + name, PortletSession.APPLICATION_SCOPE);

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public BPMModule getBpmModule() {
        return bpmModule;
    }

    public void setBpmModule(BPMModule bpmModule) {
        this.bpmModule = bpmModule;
    }

    public ResourceBundle getMessages() {
        return messages;
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

//    public User getPortalUser() {
//        return portalUser;
//    }
//
//    public void setPortalUser(User portalUser) {
//        this.portalUser = portalUser;
//    }

    public PortletSession getPortletSession() {
        return portletSession;
    }

    public void setPortletSession(PortletSession portletSession) {
        this.portletSession = portletSession;
    }

    public DocumentLibraryUtil getDocumentLibraryUtil() {
        return documentLibraryUtil;
    }

    public void setDocumentLibraryUtil(DocumentLibraryUtil documentLibraryUtil) {
        this.documentLibraryUtil = documentLibraryUtil;
    }

    public PortletApplicationContext2 getPortletApplicationContext2() {
        return portletApplicationContext2;
    }

    public void setPortletApplicationContext2(PortletApplicationContext2 portletApplicationContext2) {
        this.portletApplicationContext2 = portletApplicationContext2;
    }
}
