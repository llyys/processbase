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
import org.processbase.ui.template.HumanTaskWindow;
import org.processbase.ui.util.PortalDocumentLibrary;

/**
 *
 * @author mgubaidullin
 */
public abstract class CustomPortlet extends Application
        implements PortletListener, PortletRequestListener, TransactionListener {

    private static ThreadLocal<CustomPortlet> currentPortlet = new ThreadLocal<CustomPortlet>();
    private PortletApplicationContext2 portletApplicationContext2;
    private PortletSession portletSession;
    private BPMModule bpmModule = null;
    private ResourceBundle messages = null;
    private PortalDocumentLibrary documentLibrary = null;
    private String taskInstanceUUID = null;
    private String processDefinitionUUID = null;
    private int type = 1;
    public static final int TYPE_START_PROCESS = 0;
    public static final int TYPE_TASK = 1;
    private boolean initialized = false;
    private HumanTaskWindow taskWindow;

    public void init() {
//        System.out.println("CustomPortlet init ");
        setCurrent(this);
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }
        messages = ResourceBundle.getBundle("resources/MessagesBundle", getLocale());
        bpmModule = new BPMModule(getPortalUser().getScreenName());
        documentLibrary = new PortalDocumentLibrary(getPortalUser());

        portletApplicationContext2 = (PortletApplicationContext2) getContext();
        portletSession = portletApplicationContext2.getPortletSession();
        portletApplicationContext2.addPortletListener((Application) this, (PortletListener) this);

        setTheme("processbase");
        setLogoutURL(Constants.TASKLIST_PAGE_URL);

        if (getContext() != null) {
            getContext().addTransactionListener(this);
        }
        if (!initialized
                && CustomPortlet.getCurrent().portletSession.getAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE) != null) {
            CustomPortlet.getCurrent().taskInstanceUUID = ((CustomPortlet) CustomPortlet.getCurrent()).portletSession.getAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE).toString();
            CustomPortlet.getCurrent().type = TYPE_TASK;
            initUI();
        } else if (!initialized
                && CustomPortlet.getCurrent().portletSession.getAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE) != null) {
            CustomPortlet.getCurrent().processDefinitionUUID = ((CustomPortlet) CustomPortlet.getCurrent()).portletSession.getAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE).toString();
            CustomPortlet.getCurrent().type = TYPE_START_PROCESS;
            initUI();
        }
    }

    public void initUI() {
        // create main window
//        System.out.println("DEBUG CustomPortlet.initUI---------");
        taskWindow = new HumanTaskWindow("", true);
        setMainWindow(taskWindow);
        taskWindow.initUI();
    }

    @Override
    public void onRequestStart(PortletRequest request, PortletResponse response) {
//        System.out.println("DEBUG CustomPortlet.onRequestStart---------");
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
//        System.out.println("CustomPortlet initialized = " + initialized);
//        System.out.println("PROCESSBASE_SHARED_TASKINSTANCE = " + request.getPortletSession().getAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE));

        if (initialized
                && request.getPortletSession().getAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE) != null
                && taskInstanceUUID.equals(request.getPortletSession().getAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE).toString())) {
            taskInstanceUUID = request.getPortletSession().getAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE).toString();
            type = TYPE_TASK;
            initUI();
        } else if (initialized
                && request.getPortletSession().getAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE) != null
                && !processDefinitionUUID.equals(request.getPortletSession().getAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE).toString())) {
            processDefinitionUUID = request.getPortletSession().getAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE).toString();
            type = TYPE_START_PROCESS;
            initUI();
        }
    }

    public void onRequestEnd(PortletRequest request, PortletResponse response) {
//        System.out.println("PORTLET onRequestEnd ");
    }

    public void handleRenderRequest(RenderRequest request, RenderResponse response, Window window) {
//        System.out.println("PORTLET handleRenderRequest ");
        if (portletSession.getAttribute("PROCESSBASE_PORTLET_CREATED", PortletSession.PORTLET_SCOPE) == null) {
            portletSession.setAttribute("PROCESSBASE_PORTLET_CREATED", "PROCESSBASE_PORTLET_CREATED", PortletSession.PORTLET_SCOPE);
        }
    }

    public void handleActionRequest(ActionRequest request, ActionResponse response, Window window) {
//        System.out.println("PORTLET handleActionRequest ");
    }

    public void handleEventRequest(EventRequest request, EventResponse response, Window window) {
//        System.out.println("PORTLET handleEventRequest ");
    }

    public void handleResourceRequest(ResourceRequest request, ResourceResponse response, Window window) {
//        System.out.println("PORTLET handleResourceRequest ");
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
        this.portletApplicationContext2.getPortletSession().removeAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE);
        this.portletApplicationContext2.getPortletSession().removeAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE);
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
    public static CustomPortlet getCurrent() {
        return currentPortlet.get();
    }

    /**
     * Set the current application instance
     */
    public static void setCurrent(CustomPortlet application) {
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
            CustomPortlet.setCurrent(this);
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

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BPMModule getBpmModule() {
        return bpmModule;
    }

    public void setBpmModule(BPMModule bpmModule) {
        this.bpmModule = bpmModule;
    }

    public PortalDocumentLibrary getDocumentLibrary() {
        return documentLibrary;
    }

    public void setDocumentLibrary(PortalDocumentLibrary documentLibrary) {
        this.documentLibrary = documentLibrary;
    }

    public ResourceBundle getMessages() {
        return messages;
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

    public String getProcessDefinitionUUID() {
        return processDefinitionUUID;
    }

    public void setProcessDefinitionUUID(String processDefinitionUUID) {
        this.processDefinitionUUID = processDefinitionUUID;
    }

    public String getTaskInstanceUUID() {
        return taskInstanceUUID;
    }

    public void setTaskInstanceUUID(String taskInstanceUUID) {
        this.taskInstanceUUID = taskInstanceUUID;
    }

    public HumanTaskWindow getTaskWindow() {
        return taskWindow;
    }

    public void setTaskWindow(HumanTaskWindow taskWindow) {
        this.taskWindow = taskWindow;
    }

    public PortletSession getPortletSession() {
        return this.portletSession;
    }
}
