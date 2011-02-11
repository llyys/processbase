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
package org.processbase.ui.template;

import com.liferay.portal.util.PortalUtil;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.processbase.core.Constants;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2.PortletListener;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Window;
import javax.portlet.PortletSession;

/**
 *
 * @author mgubaidullin
 */
public abstract class TaskApplication extends Application 
        implements PortletListener, PortletRequestListener {

    protected PortletApplicationContext2 portletApplicationContext2;
    protected PortletSession portletSession;
    protected TaskWindow taskWindow;
    Class taskWindowClass;
    protected String processUUID;

    public TaskApplication() {
    }

    public TaskApplication(Class taskWindowClass, String processUUID) {
        this.taskWindowClass = taskWindowClass;
        this.processUUID = processUUID;

    }

    public void onRequestStart(PortletRequest request, PortletResponse response) {
      
    }

    public void onRequestEnd(PortletRequest request, PortletResponse response) {

    }




    @Override
    public void init() {
        portletApplicationContext2 = (PortletApplicationContext2) getContext();
        portletSession = portletApplicationContext2.getPortletSession();

        portletApplicationContext2.addPortletListener((Application) this, (PortletListener) this);
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }
        setTheme("processbase");
        this.setLogoutURL(Constants.TASKLIST_PAGE_URL);

        try {
            this.taskWindow = (TaskWindow) taskWindowClass.getDeclaredConstructor(PortletApplicationContext2.class).newInstance(portletApplicationContext2);
            this.setMainWindow(this.taskWindow);
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
    }

    public void handleRenderRequest(RenderRequest request, RenderResponse response, Window window) {
        try {
            if (portletSession.getAttribute("PROCESSBASE_USER", PortletSession.APPLICATION_SCOPE) == null) {
                portletSession.setAttribute("PROCESSBASE_USER", PortalUtil.getPortal().getUser(request), PortletSession.APPLICATION_SCOPE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (portletSession.getAttribute("PROCESSBASE_PORTLET_CREATED", PortletSession.PORTLET_SCOPE) == null) {
            portletSession.setAttribute("PROCESSBASE_PORTLET_CREATED", "PROCESSBASE_PORTLET_CREATED", PortletSession.PORTLET_SCOPE);
            if (!this.taskWindow.setTaskInfo(processUUID)) {
                this.close();
            }
            this.taskWindow.exec();
        }
    }

    public void handleActionRequest(ActionRequest request, ActionResponse response, Window window) {

    }

    public void handleEventRequest(EventRequest request, EventResponse response, Window window) {

    }

    public void handleResourceRequest(ResourceRequest request, ResourceResponse response, Window window) {

    }

    public PortletApplicationContext2 getPortletApplicationContext2() {
        return this.portletApplicationContext2;
    }

    public static SystemMessages getSystemMessages() {
        CustomizedSystemMessages m = new CustomizedSystemMessages();
        m.setSessionExpiredURL(null);
        m.setSessionExpiredNotificationEnabled(true);
        m.setSessionExpiredCaption(null);
        m.setSessionExpiredMessage("Время действия вашей сессии истекло!");
        m.setCommunicationErrorCaption(null);
//        m.setCommunicationErrorMessage("Ошибка соединения!");
        m.setOutOfSyncCaption(null);
        m.setInternalErrorCaption(null);
        m.setInternalErrorMessage("Внутренняя ошибка!");
        return m;
    }

    @Override
    public void close() {
        this.portletApplicationContext2.getPortletSession().removeAttribute("PROCESSBASE_PORTLET_CREATED", PortletSession.PORTLET_SCOPE);
        super.close();
    }

    public void setPortletApplicationContext2(PortletApplicationContext2 portletApplicationContext2) {
        this.portletApplicationContext2 = portletApplicationContext2;
    }

    public void setPortletSession(PortletSession portletSession) {
        this.portletSession = portletSession;
    }
}
