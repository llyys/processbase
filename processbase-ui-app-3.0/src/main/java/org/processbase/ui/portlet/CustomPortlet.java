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
import java.util.Map;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2.PortletListener;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Window;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.portlet.PortletSession;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.HumanTaskWindow;
import org.processbase.ui.osgi.PbPanelModuleService;

/**
 *
 * @author mgubaidullin
 */
public class CustomPortlet extends ProcessbaseApplication
        implements PortletListener, PortletRequestListener, TransactionListener {

    private String taskInstanceUUID = null;
    private String processDefinitionUUID = null;
    private int customtype = 1;
    public static final int TYPE_START_PROCESS = 0;
    public static final int TYPE_TASK = 1;
    private boolean initialized = false;
    private HumanTaskWindow taskWindow;
    private ResourceBundle messages;
    private BPMModule bpmModule;
    private PortalDocumentLibrary documentLibrary;
    private PortletApplicationContext2 portletApplicationContext2;
    private PortletSession portletSession;
    private int type;
    private String userName;

    @Override
    public void init() {
        System.out.println("CustomPortlet init ");
        setCurrent(this);
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }
        portletApplicationContext2 = (PortletApplicationContext2) getContext();
        portletSession = portletApplicationContext2.getPortletSession();
        portletApplicationContext2.addPortletListener((Application) this, (PortletListener) this);

        setTheme("processbase");
        setLogoutURL(Constants.TASKLIST_PAGE_URL);

        if (getContext() != null) {
            getContext().addTransactionListener(this);
        }
        if (!initialized
                && ProcessbaseApplication.getCurrent().getSessionAttribute("TASKINSTANCE") != null) {
            this.taskInstanceUUID = ProcessbaseApplication.getCurrent().getSessionAttribute("TASKINSTANCE").toString();
            this.customtype = TYPE_TASK;
            initUI();
        } else if (!initialized
                && ProcessbaseApplication.getCurrent().getSessionAttribute("PROCESSINSTANCE") != null) {
            this.processDefinitionUUID = ProcessbaseApplication.getCurrent().getSessionAttribute("PROCESSINSTANCE").toString();
            this.customtype = TYPE_START_PROCESS;
            initUI();
        }
    }

    public void initUI() {
        // create main window
        System.out.println("DEBUG CustomPortlet.initUI---------");
        try {
            taskWindow = new HumanTaskWindow("", true);
            setMainWindow(taskWindow);
            if (customtype == CustomPortlet.TYPE_START_PROCESS) {
                ProcessDefinition pd = bpmModule.getProcessDefinition(new ProcessDefinitionUUID(processDefinitionUUID));
                taskWindow.setProcessDef(pd);
            } else if (customtype == CustomPortlet.TYPE_TASK) {
                TaskInstance taskInstance = bpmModule.getTaskInstance(new ActivityInstanceUUID(taskInstanceUUID));
                taskWindow.setTaskInstance(taskInstance);
            }
            ProcessbaseApplication.getCurrent().removeSessionAttribute("PROCESSINSTANCE");
            ProcessbaseApplication.getCurrent().removeSessionAttribute("TASKINSTANCE");
            setInitialized(true);
            taskWindow.initUI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onRequestStart(PortletRequest request, PortletResponse response) {
        System.out.println("DEBUG CustomPortlet.onRequestStart---------");
        if (!initialized) {
            try {
                User user = PortalUtil.getUser(request);
                setUserName(user.getScreenName());
                setLocale(request.getLocale());
                setMessages(ResourceBundle.getBundle("MessagesBundle", getLocale()));
                setBpmModule(new BPMModule(user.getScreenName()));
                setDocumentLibrary(new PortalDocumentLibrary(user));
                setPortletSession(request.getPortletSession());
            } catch (PortalException e) {
                e.printStackTrace();
            } catch (SystemException e) {
                e.printStackTrace();
            }
        }
//        System.out.println("CustomPortlet initialized = " + initialized);
//        System.out.println("PROCESSBASE_SHARED_TASKINSTANCE = " + request.getPortletSession().getAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE));

//        if (initialized
//                && request.getPortletSession().getAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE) != null
//                && taskInstanceUUID.equals(request.getPortletSession().getAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE).toString())) {
//            taskInstanceUUID = request.getPortletSession().getAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE).toString();
//            customtype = TYPE_TASK;
//            initUI();
//        } else if (initialized
//                && request.getPortletSession().getAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE) != null
//                && !processDefinitionUUID.equals(request.getPortletSession().getAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE).toString())) {
//            processDefinitionUUID = request.getPortletSession().getAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE).toString();
//            customtype = TYPE_START_PROCESS;
//            initUI();
//        }
    }

    public void handleRenderRequest(RenderRequest request, RenderResponse response, Window window) {
//        System.out.println("PORTLET handleRenderRequest ");
        if (portletSession.getAttribute("PROCESSBASE_PORTLET_CREATED", PortletSession.PORTLET_SCOPE) == null) {
            portletSession.setAttribute("PROCESSBASE_PORTLET_CREATED", "PROCESSBASE_PORTLET_CREATED", PortletSession.PORTLET_SCOPE);
        }
    }
    public void authenticate(String login, String password, boolean rememberMe) throws Exception {
                
    }
    @Override
    public void close() {
        ProcessbaseApplication.getCurrent().removeSessionAttribute("PROCESSINSTANCE");
        ProcessbaseApplication.getCurrent().removeSessionAttribute("TASKINSTANCE");
        ProcessbaseApplication.getCurrent().removeSessionAttribute("PORTLET_CREATED");
        super.close();
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

    public void setSessionAttribute(String name, Object value) {
        getPortletSession().setAttribute("PROCESSBASE_SHARED_" + name, value, PortletSession.APPLICATION_SCOPE);
    }

    public void removeSessionAttribute(String name) {
        getPortletSession().removeAttribute("PROCESSBASE_SHARED_" + name, PortletSession.APPLICATION_SCOPE);
    }

    public Object getSessionAttribute(String name) {
        return getPortletSession().getAttribute("PROCESSBASE_SHARED_" + name, PortletSession.APPLICATION_SCOPE);

    }

    public ResourceBundle getPbMessages() {
        return messages;
    }

    public void setPortletSession(PortletSession portletSession) {
        this.portletSession = portletSession;
    }

    public PortletApplicationContext2 getPortletApplicationContext2() {
        return portletApplicationContext2;
    }

    public void setPortletApplicationContext2(PortletApplicationContext2 portletApplicationContext2) {
        this.portletApplicationContext2 = portletApplicationContext2;
    }

    public int getApplicationType() {
        return ProcessbaseApplication.LIFERAY_PORTAL;
    }

    public void saveFile(String processUUID, String name, String fileName, byte[] fileBody) throws Exception {
        this.getDocumentLibrary().saveFile(processUUID, fileName, fileName, fileBody);
    }

    public byte[] getFileBody(String processUUID, String name) throws Exception {
        return getDocumentLibrary().getFileBody(processUUID, name);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public ResourceBundle getCustomMessages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setCustomMessages(ResourceBundle customMessages) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PbPanelModuleService getPanelModuleService() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void handleActionRequest(ActionRequest request, ActionResponse response, Window window) {
    }

    public void handleEventRequest(EventRequest request, EventResponse response, Window window) {
    }

    public void handleResourceRequest(ResourceRequest request, ResourceResponse response, Window window) {
    }

    public void onRequestEnd(PortletRequest request, PortletResponse response) {
    }

    @Override
    public Map<String, String> getFileList(String processUUID) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
