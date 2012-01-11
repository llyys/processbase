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
import com.liferay.portal.model.Company;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import java.util.Map;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.portlet.PortletConfig;
import javax.portlet.PortletSession;
import javax.servlet.http.Cookie;

import org.caliburn.application.event.imp.DefaultEventAggregator;
import org.processbase.raports.ui.RaportModule;
import org.processbase.ui.bam.panel.BAMConfigurationPanel;
import org.processbase.ui.bam.panel.BPMMonitoringPanel;

import org.processbase.ui.bpm.admin.AdminCaseList;
import org.processbase.ui.bpm.admin.CategoriesPanel;
import org.processbase.ui.bpm.admin.DisabledProcessDefinitionsPanel;
import org.processbase.ui.bpm.admin.ProcessDefinitionsPanel;
import org.processbase.ui.bpm.admin.AdminTaskList;
import org.processbase.ui.bpm.panel.BPMConfigurationPanel;
import org.processbase.ui.bpm.panel.TaskListPanel;
import org.processbase.ui.bpm.panel.IdentityPanel;
import org.processbase.ui.bpm.panel.events.TaskListEvent;
import org.processbase.ui.bpm.panel.events.TaskListEvent.ActionType;
import org.processbase.ui.bpm.worklist.CandidateCaseList;
import org.processbase.ui.bpm.worklist.CandidateTaskList;
import org.processbase.ui.bpm.worklist.NewProcesses;
import org.processbase.ui.bpm.worklist.UserCaseList;
import org.processbase.ui.bpm.worklist.UserTaskList;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.IPbTable;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.osgi.PbPanelModule;
import org.processbase.ui.osgi.PbPanelModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mgubaidullin
 */
public class PbPortlet extends ProcessbaseApplication implements PortletRequestListener {

    private static final String AUTHENTICATEDUSER = "authenticateduser";
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
	private User portalUser;

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
        	PbPanel ui=null;
	        String initParameter = config.getInitParameter("ui");
			if (initParameter.equalsIgnoreCase("ConsolePanel")) {
	            ui = new TaskListPanel();
	        }
			else if (initParameter.equalsIgnoreCase("NewProcesses")) {
				ui = (PbPanel) new NewProcesses();	         
			}
			else if (initParameter.equalsIgnoreCase("UserTaskList")) {
				ui = (PbPanel) new UserTaskList();	         
			}
			else if (initParameter.equalsIgnoreCase("UserCaseList")) {
				ui = (PbPanel) new UserCaseList();	         
			}
			else if (initParameter.equalsIgnoreCase("CandidateTaskList")) {
	            ui = (PbPanel) new CandidateTaskList();	         
	        }
			else if (initParameter.equalsIgnoreCase("CandidateCaseList")) {
				ui = (PbPanel) new CandidateCaseList();	         
			}
			else if (initParameter.equalsIgnoreCase("Raports")) {
	            ui = (PbPanel) new RaportModule();	         
	        }
			
			else if (initParameter.equalsIgnoreCase("AdminPanel")) {
	            ui = new BPMConfigurationPanel();	         
	        }
			
			else if (initParameter.equalsIgnoreCase("AdminProcessDefinitions")) {
	            ui = new ProcessDefinitionsPanel();	         
	        }
			else if (initParameter.equalsIgnoreCase("AdminDisabledProcessDefinitions")) {
				ui = new DisabledProcessDefinitionsPanel();	         
			}
			else if (initParameter.equalsIgnoreCase("AdminTaskList")) {
				ui = new AdminTaskList();	         
			}
			else if (initParameter.equalsIgnoreCase("AdminCaseList")) {
				ui = new AdminCaseList();	         
			}
			else if (initParameter.equalsIgnoreCase("AdminCategoriesPanel")) {
				ui = new CategoriesPanel();	         
			}
			
			else if (initParameter.equalsIgnoreCase("IdentityPanel")) {
	            ui = new IdentityPanel();	            
	        } else if (initParameter.equalsIgnoreCase("BAMPanel")) {
	            ui = new BAMConfigurationPanel();	            
	        } else if (initParameter.equalsIgnoreCase("MonitoringPanel")) {
	            ui = new BPMMonitoringPanel();	            
	        }
	        
            
            if(ui instanceof IPbTable){
            	
            	//final DefaultEventAggregator e = new DefaultEventAggregator();
            	VerticalLayout bpPanel=new VerticalLayout();
            	
            	Button refresh=new Button("Uuenda", new Button.ClickListener() {
					public void buttonClick(ClickEvent event) {					
						((IPbTable)event.getButton().getData()).refreshTable();
					}
				});
            	refresh.setData(ui);
            	bpPanel.addComponent(refresh);
            	bpPanel.setMargin(true, true, false, false);
            	
            	bpPanel.setComponentAlignment(refresh, Alignment.MIDDLE_RIGHT);
            	bpPanel.addComponent(ui);
            	
            	//e.Subscribe(ui);
            	mainWindow.setContent(bpPanel);
            	
            	bpPanel.setExpandRatio(ui, 1);
            	ui.initUI();
            	((IPbTable)ui).refreshTable();
            }
            else{
            	 mainWindow.setContent(ui);
                 ui.initUI();
            }
           
        }
        
    }

    public void authenticate(String login, String password, boolean rememberMe, String domainName) throws Exception {
        BPMModule bpmm = new BPMModule(login, domainName);
        setBpmModule(bpmm);
        setUserName(login);
       // setSessionAttribute(AUTHENTICATEDUSER, login);
        PortalUser portalUser = new PortalUser(login, login);
                
        org.ow2.bonita.facade.identity.User bonitaUser = bpmModule.authUser(portalUser);
		setSessionAttribute(AUTH_KEY, bonitaUser);
        initUI(); 
              
    }
    
    public void onRequestStart(PortletRequest request, PortletResponse response) {
    	logger.debug("PbPortlet onRequestStart ");
    	
        
            try {
            	if(inited==false)
            	{
            		setPortletSession(request.getPortletSession());
            		inited=true;
            	}
            	else{
            		return;	
            	}       	
            	org.ow2.bonita.facade.identity.User bonitaUser =null;
            	
            	Company company=PortalUtil.getCompany(request);
            	
                portalUser = PortalUtil.getUser(request);
                if(portalUser!=null)
                {           
                	bonitaUser=new PortalUser(portalUser);      
                	setUserName(bonitaUser.getUsername());
                }
                else {
                	//portal user is null
                	setUserName(BPMModule.USER_GUEST);
            		bonitaUser=new PortalUser(userName, userName);            				
                }	                               
                if(bpmModule==null)
                	
            		setBpmModule(new BPMModule(userName, getDomain(company)));
                	bpmModule.checkUserCredentials(BPMModule.USER_GUEST, BPMModule.USER_GUEST);
					bonitaUser=bpmModule.authUser(bonitaUser);
					setSessionAttribute(AUTH_KEY, bonitaUser);					
				
                setLocale(request.getLocale());
                setMessages(ResourceBundle.getBundle("MessagesBundle", getLocale()));
                Constants.APP_SERVER="LIFERAY";
                
                //setDocumentLibrary(new PortalDocumentLibrary(user));                
                //initUI();

            } catch (PortalException e) {
            	logger.error("portal exception onRequestStart", e);
                e.printStackTrace();
            } catch (SystemException e) {
            	logger.error("system exception onRequestStart", e);
                e.printStackTrace();
            } catch (Exception e) {
            	logger.error("onRequestStart", e);
				e.printStackTrace();
			}
        
    }

	/**
	 * @param company
	 * @return
	 * @throws PortalException
	 * @throws SystemException
	 */
	private String getDomain(Company company) throws PortalException,
			SystemException {
		String domain= company==null?Constants.BONITA_DOMAIN: company.getName();
		if(domain.equalsIgnoreCase("liferay"))
			return Constants.BONITA_DOMAIN;
		return domain;
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

    public void setSessionAttribute(String name, Object value) {
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
    	if(messages==null)
    		messages =ResourceBundle.getBundle("MessagesBundle", getLocale());
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

	

	public User getPortalUser() {
		return portalUser;
	}
}
