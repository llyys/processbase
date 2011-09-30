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
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import org.processbase.ui.bam.panel.BAMConfigurationPanel;
import org.processbase.ui.bam.panel.BPMMonitoringPanel;

import org.processbase.ui.bpm.panel.BPMConfigurationPanel;
import org.processbase.ui.bpm.panel.TaskListPanel;
import org.processbase.ui.bpm.panel.IdentityPanel;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.osgi.PbPanelModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mgubaidullin
 */
public class PbPortlet extends ProcessbaseApplication implements PortletRequestListener {

    PbWindow mainWindow;
    PortletApplicationContext2 portletApplicationContext2 = null;
    PortletSession portletSession = null;
    BPMModule bpmModule = null;
    ResourceBundle messages = null;
    PortalDocumentLibrary documentLibrary = null;
    String userName = null;
//    User portalUser = null;
    Locale locale = null;
    int type = LIFERAY_PORTAL;
    final Logger logger = LoggerFactory.getLogger(PbPortlet.class);

    private boolean inited = false;

    public void initUI() {
        logger.debug("PbPortlet init ");
        //setTheme("processbase");
        setLogoutURL(Constants.TASKLIST_PAGE_URL);
        setPortletApplicationContext2((PortletApplicationContext2) getContext());
        PortletConfig config = getPortletApplicationContext2().getPortletConfig();
        mainWindow = new PbWindow("Processbase User Portlet");
        mainWindow.setSizeFull();
        setMainWindow(mainWindow);
        if(userName!=null)
        {
	        if (config.getInitParameter("ui").equalsIgnoreCase("ConsolePanel")) {
	            TaskListPanel ui = new TaskListPanel();	            
	            mainWindow.setContent(ui);
	            ui.initUI();
	        }
	        else if (config.getInitParameter("ui").equalsIgnoreCase("AdminPanel")) {
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
        
    }

    public void onRequestStart(PortletRequest request, PortletResponse response) {
    	logger.debug("PbPortlet onRequestStart ");
    	
        if (!inited) {
            try {
                User user = PortalUtil.getUser(request);
//                setPortalUser(user);
                if(user!=null)
                {                	
	                setUserName(user.getScreenName());
	                setBpmModule(new BPMModule(user.getScreenName()));	               
                }
                else{
                	
                	setUserName(BPMModule.USER_GUEST);
                	setBpmModule(new BPMModule(BPMModule.USER_GUEST));
                }
                try {
					org.ow2.bonita.facade.identity.User user2 = getBpmModule().findUserByUserName(user.getScreenName());
					if(user2==null){
						setUserName(BPMModule.USER_GUEST);
	                	setBpmModule(new BPMModule(BPMModule.USER_GUEST));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					setUserName(BPMModule.USER_GUEST);
                	setBpmModule(new BPMModule(BPMModule.USER_GUEST));
				}
                setLocale(request.getLocale());
                setMessages(ResourceBundle.getBundle("MessagesBundle", getLocale()));
                Constants.APP_SERVER="LIFERAY";
                
                //setDocumentLibrary(new PortalDocumentLibrary(user));
                setPortletSession(request.getPortletSession());
               // initUI();

            } catch (PortalException e) {
            	logger.error("onRequestStart", e);
                e.printStackTrace();
            } catch (SystemException e) {
            	logger.error("onRequestStart", e);
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

    public Object getSessionAttribute(String name) {
        return getPortletSession().getAttribute("PROCESSBASE_SHARED_" + name, PortletSession.APPLICATION_SCOPE);

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
    	logger.debug("User:"+userName);
        this.userName = userName;
    }

    public BPMModule getBpmModule() {
        return bpmModule;
    }

    public void setBpmModule(BPMModule bpmModule) {
        this.bpmModule = bpmModule;
    }

    public ResourceBundle getPbMessages() {
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

    public PortalDocumentLibrary getDocumentLibrary() {
        return documentLibrary;
    }

    public void setDocumentLibrary(PortalDocumentLibrary documentLibrary) {
        this.documentLibrary = documentLibrary;
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

    public void saveFile(String processUUID, String name, String fileName, byte[] fileBody) throws Exception{
        this.getDocumentLibrary().saveFile(processUUID, name, fileName, fileBody);
    }

    public byte[] getFileBody(String processUUID, String name) throws Exception {
        return getDocumentLibrary().getFileBody(processUUID, name);
    }

    public PbPanelModuleService getPanelModuleService() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ResourceBundle getCustomMessages() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setCustomMessages(ResourceBundle customMessages) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Map<String, String> getFileList(String processUUID) throws Exception {
        return getDocumentLibrary().getFileList(processUUID);
    }
}
