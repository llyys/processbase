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
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2.PortletListener;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Window;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.portlet.PortletSession;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.util.DocumentLibraryUtil;

/**
 *
 * @author mgubaidullin
 */
public abstract class PbPortlet
        extends Application
        implements PortletListener, PortletRequestListener, TransactionListener {

    private static ThreadLocal<PbPortlet> currentPortlet = new ThreadLocal<PbPortlet>();
    public PortletApplicationContext2 portletApplicationContext2;
    public PortletSession portletSession;
    public BPMModule bpmModule = null;
    public ResourceBundle messages = null;
    public DocumentLibraryUtil documentLibraryUtil = null;

    @Override
    public void init() {
        setCurrent(this);
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }

        messages = ResourceBundle.getBundle("resources/MessagesBundle", getLocale());
        bpmModule = new BPMModule(getPortalUser().getScreenName());
        documentLibraryUtil = new DocumentLibraryUtil();

        portletApplicationContext2 = (PortletApplicationContext2) getContext();
        portletSession = portletApplicationContext2.getPortletSession();
        portletApplicationContext2.addPortletListener((Application) this, (PortletListener) this);

        setTheme("processbase");
        setLogoutURL(Constants.TASKLIST_PAGE_URL);

        if (getContext() != null) {
            getContext().addTransactionListener(this);
        }

    }

    public void onRequestStart(PortletRequest request, PortletResponse response) {
        if (getUser() == null) {
            try {
                User user = PortalUtil.getUser(request);
                setUser(user);
                Locale locale = request.getLocale();
                setLocale(locale);
            } catch (PortalException e) {
                e.printStackTrace();
            } catch (SystemException e) {
                e.printStackTrace();
            }
        }
    }

    public void onRequestEnd(PortletRequest request, PortletResponse response) {
    }

    public void handleRenderRequest(RenderRequest request, RenderResponse response, Window window) {

        if (portletSession.getAttribute("PROCESSBASE_PORTLET_CREATED", PortletSession.PORTLET_SCOPE) == null) {
            portletSession.setAttribute("PROCESSBASE_PORTLET_CREATED", "PROCESSBASE_PORTLET_CREATED", PortletSession.PORTLET_SCOPE);
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
        m.setSessionExpiredMessage("Session expired!");
        m.setCommunicationErrorCaption(null);
//        m.setCommunicationErrorMessage("Ошибка соединения!");
        m.setOutOfSyncCaption(null);
        m.setInternalErrorCaption(null);
        m.setInternalErrorMessage("Internal error!");
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

    /**
     * @return the current application instance
     */
    public static PbPortlet getCurrent() {
        return currentPortlet.get();
    }

    /**
     * Set the current application instance
     */
    public static void setCurrent(PbPortlet application) {
        if (getCurrent() == null) {
            currentPortlet.set(application);
        }
    }

    /**
     * Remove the current application instance
     */
    public static void removeCurrent() {
        currentPortlet.remove();
    }

    /**
     * TransactionListener
     */
    public void transactionStart(Application application, Object transactionData) {
        if (application == this) {
            PbPortlet.setCurrent(this);
            // Store current users locale
            setLocale(getLocale());
        }
    }

    public void transactionEnd(Application application, Object transactionData) {
        if (application == this) {
            // Remove locale from the executing thread
            removeCurrent();
        }
    }

    public User getPortalUser() {
        return (User) getUser();
    }
}
