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
package org.processbase.ui.servlet;

import com.vaadin.terminal.gwt.server.WebApplicationContext;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.enterprise.context.SessionScoped;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.processbase.engine.bam.db.HibernateUtil;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.util.SpringContextHelper;
import org.processbase.ui.osgi.PbPanelModule;
import org.processbase.ui.osgi.PbPanelModuleService;
import org.processbase.ui.osgi.PbPanelModuleServiceListener;
import org.processbase.ui.osgi.impl.PbPanelModuleServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 *
 * @author mgubaidullin
 */
@SessionScoped
@SuppressWarnings("serial")
public class PbApplication extends ProcessbaseApplication implements PbPanelModuleServiceListener {

	
    private PbPanelModuleService panelModuleService;
	
    private MainWindow mainWindow;
    private HttpSession httpSession = null;
    private BPMModule bpmModule = null;
    private ResourceBundle messages = null;
    private ResourceBundle customMessages = null;
    private String userName = null;
    private ApplicationContext context;
    
//    int type = STANDALONE;

    public PbApplication(PbPanelModuleService panelModuleService) {
    	this.panelModuleService = panelModuleService;
    	 
    }

    public void initUI() {
    	
        System.out.println("PbApplication init ");
//        if (!Constants.LOADED) {
//            Constants.loadConstants();
//        }
       // setTheme("processbaseruno");
        try {
        	//BasicConfigurator.configure();
        	LOGGER.info("PbApplication initialized with logger");
        	
            WebApplicationContext applicationContext = (WebApplicationContext) this.getContext();
            
            httpSession = applicationContext.getHttpSession();
            ServletContext servletContext = httpSession.getServletContext();
            
            context = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            
            setLocale(applicationContext.getBrowser().getLocale());
            
            setMessages(ResourceBundle.getBundle("MessagesBundle", getLocale()));
            mainWindow = new MainWindow();
            setMainWindow(mainWindow);

            mainWindow.initLogin();
            panelModuleService.addListener(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void close() {
        panelModuleService.removeListener(this);
        super.close();
    }

    public void authenticate(String login, String password) throws Exception {
        BPMModule bpmm = new BPMModule(login);
        if (bpmm.checkUserCredentials(login, password)) {
            setUserName(login);
            String locale = bpmm.getUserMetadata("locale");
            if (locale != null) {
                setLocale(new Locale(locale));
                setMessages(ResourceBundle.getBundle("MessagesBundle", getLocale()));
            }
            setBpmModule(bpmm);
            mainWindow.initUI();
        } else {
            throw new Exception(getPbMessages().getString("loginWindowException2"));
        }
    }

    public void setSessionAttribute(String name, String value) {
        httpSession.setAttribute("PROCESSBASE_SHARED_" + name, value);
    }

    public void removeSessionAttribute(String name) {
        httpSession.removeAttribute("PROCESSBASE_SHARED_" + name);
    }

    public Object getSessionAttribute(String name) {
        return httpSession.getAttribute("PROCESSBASE_SHARED_" + name);
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

    public ResourceBundle getPbMessages() {
        
    	//return ProcessbaseApplication.getCurrent().getPbMessages();
    	return messages;
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

    public int getApplicationType() {
        return ProcessbaseApplication.STANDALONE;
    }

    public void saveFile(String processUUID, String name, String fileName, byte[] fileBody) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public byte[] getFileBody(String processUUID, String name) throws Exception {
        return bpmModule.getAttachmentValue(processUUID, name);
    }

    public void moduleRegistered(PbPanelModuleService source, PbPanelModule module) {
        System.out.println("module registered PbApplication - " + module.getName());
    }

    public void moduleUnregistered(PbPanelModuleService source, PbPanelModule module) {
        System.out.println("module unregistered PbApplication - " + module.getName());
    }

    public PbPanelModuleService getPanelModuleService() {
        return panelModuleService;
    }

    public ResourceBundle getCustomMessages() {
    	return ProcessbaseApplication.getCurrent().getPbMessages();
    	//return customMessages;
    }

    public void setCustomMessages(ResourceBundle customMessages) {
        this.customMessages = customMessages;
    }

    @Override
    public Map<String, String> getFileList(String processUUID) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
