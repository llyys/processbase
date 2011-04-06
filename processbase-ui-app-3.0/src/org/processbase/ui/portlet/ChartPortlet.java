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
import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2.PortletListener;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Window;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.portlet.PortletMode;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletSession;
import org.processbase.ui.core.Constants;
import org.processbase.ui.portlet.chart.ChartConfigurationPanel;
import org.processbase.ui.portlet.chart.ChartViewPanel;

/**
 *
 * @author mgubaidullin
 */
public class ChartPortlet extends Application
        implements PortletListener, PortletRequestListener, TransactionListener {

    private static ThreadLocal<ChartPortlet> currentPortlet = new ThreadLocal<ChartPortlet>();
    public static ThreadLocal<PortletPreferences> portletPreferences = new ThreadLocal<PortletPreferences>();
    public PortletApplicationContext2 portletApplicationContext2;
    public PortletSession portletSession;
    private Window mainWindow;
    private ChartViewPanel viewPanel;
    private ChartConfigurationPanel configPanel;
    public String portletId;
    public ResourceBundle messages = null;

    @Override
    public void init() {
        setCurrent(this);
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }
        messages = ResourceBundle.getBundle("resources/MessagesBundle", getLocale());
        portletApplicationContext2 = (PortletApplicationContext2) getContext();
        portletSession = portletApplicationContext2.getPortletSession();
        portletApplicationContext2.addPortletListener((Application) this, (PortletListener) this);
        setTheme("processbase");
        if (getContext() != null) {
            getContext().addTransactionListener(this);
        }
        mainWindow = new Window("Chart Application");
        mainWindow.setSizeFull();
        setMainWindow(mainWindow);

        configPanel = new ChartConfigurationPanel();
        recreateChartView();
    }

    public void recreateChartView(){
         viewPanel = new ChartViewPanel();
    }

    public void onRequestStart(PortletRequest request, PortletResponse response) {
        portletPreferences.set(request.getPreferences());
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
        portletId = PortalUtil.getPortletId(request);

    }

    public void handleActionRequest(ActionRequest request, ActionResponse response, Window window) {
    }

    public void handleEventRequest(EventRequest request, EventResponse response, Window window) {
    }

    public void handleResourceRequest(ResourceRequest request, ResourceResponse response, Window window) {
        if (request.getPortletMode() == PortletMode.EDIT) {
            window.setContent(configPanel);
        } else if (request.getPortletMode() == PortletMode.VIEW) {
            window.setContent(viewPanel);
        }

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

    public void setPortletApplicationContext2(PortletApplicationContext2 portletApplicationContext2) {
        this.portletApplicationContext2 = portletApplicationContext2;
    }

    public void setPortletSession(PortletSession portletSession) {
        this.portletSession = portletSession;
    }

    /**
     * @return the current application instance
     */
    public static ChartPortlet getCurrent() {
        return currentPortlet.get();
    }

    /**
     * Set the current application instance
     */
    public static void setCurrent(ChartPortlet application) {
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
            ChartPortlet.setCurrent(this);
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
