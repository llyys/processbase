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

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.enterprise.context.SessionScoped;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.vaadin.ui.Window;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.ow2.bonita.facade.identity.User;
import org.ow2.bonita.facade.identity.impl.UserImpl;
import org.processbase.ui.bpm.identity.sync.UserRolesSync;
import org.processbase.ui.core.*;
import org.processbase.ui.osgi.PbPanelModule;
import org.processbase.ui.osgi.PbPanelModuleService;
import org.processbase.ui.osgi.PbPanelModuleServiceListener;

import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.UriFragmentUtility;

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
   // private ApplicationContext context;

	private UriFragmentUtility uriFragment;

	private String currentDomain;
    private PbUser user;

    @Override
    public Object getUser(){
        return user;
    }

//    int type = STANDALONE;

    public PbApplication(PbPanelModuleService panelModuleService) {
    	this.panelModuleService = panelModuleService;
    	 
    }

    private HttpSession getHttpSession() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        String sessionId = httpServletRequest.getParameter("sid");

        if(StringUtils.isBlank(sessionId)) {
            return httpServletRequest.getSession();
        }

        HttpSession session = (HttpSession) httpServletRequest.getAttribute(sessionId);
        return session;
    }

    public void initUI() {
        System.out.println("PbApplication init ");
        try {
        	DOMConfigurator.configure(Constants.getBonitaHomeDir()+"/log4j.xml");        	
        	LOGGER.info("PbApplication initialized");
            WebApplicationContext applicationContext = (WebApplicationContext) this.getContext();
            httpSession = getHttpSession();
            HttpServletRequest servletRequest = getHttpServletRequest();
            setLocale(applicationContext.getBrowser().getLocale());
            setMessages(ResourceBundle.getBundle("MessagesBundle", getLocale()));

            mainWindow = new MainWindow();
            setMainWindow(mainWindow);

            uriFragment = new UriFragmentUtility();
            mainWindow.addComponent(uriFragment);
            
            PbUser authUser=(PbUser) getHttpSession().getAttribute(USER);
            if(authUser!=null)
            {
            	if(bpmModule==null)
            	{
            		BPMModule bpmm = new BPMModule(authUser.username);
            		setBpmModule(bpmm);
        		}
            	//authUser=getBpmModule().authUser(authUser.id);
                if(authenticate(authUser))
                {
                    mainWindow.initUI();
                    return;
                }
            	//return;
            } else {

                String guest_parameter = servletRequest.getParameter(BPMModule.USER_GUEST);
                if(guest_parameter !=null
				    && BPMModule.USER_GUEST.equalsIgnoreCase(guest_parameter))
				{
                    //anonymous user
                    PbUser user=new PbUser();
                    user.username=BPMModule.USER_GUEST;
					user.password=BPMModule.USER_GUEST;
                    user.rememberMe=false;
					user.domain=servletRequest.getParameter("domain");
					if(user.domain==null)
                        user.domain=Constants.BONITA_DOMAIN;

					LOGGER.debug("log in as "+BPMModule.USER_GUEST);
					if(authenticate(user))
                    {
                        mainWindow.initUI();
                        return;
                    }
				}
			}
            mainWindow.initLogin("User not found in system");
            
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }


    private Cookie[] getCookies(){
    	if(getHttpServletRequest()==null || getHttpServletRequest().getCookies()==null)
    		return new Cookie[0];
    	return getHttpServletRequest().getCookies();
    }
    
    @Override
    public void close() {
        panelModuleService.removeListener(this);
        super.close();
    }

    @Override
    public void setUser(Object u) {
        PbUser user= (PbUser) u;
        this.user=user;
    }


    public boolean authenticate(PbUser u) throws Exception {
        try {
            if(u.username==null)
                return false;
            BPMModule bpmm = new BPMModule(u.username, u.domain);

            boolean checkCredentials = PbUser.AuthMethod.regular.equals(u.authMethod);
            if (!checkCredentials || (checkCredentials && bpmm.checkUserCredentials(u.username, u.password)))
            {
                User usr = bpmm.findUserByUserName(u.username);
                u.firstName=usr.getFirstName();
                u.lastName=usr.getLastName();

                String locale = bpmm.getUserMetadata("locale");
                if (locale != null) {
                    setLocale(new Locale(locale));
                    setMessages(ResourceBundle.getBundle("MessagesBundle", getLocale()));
                }
                if(u.rememberMe){
                    Cookie cookie = new Cookie("username", u.username);
                    cookie.setMaxAge(3600); // One hour
                    getHttpServletResponse().addCookie(cookie);
                    System.out.println("Set cookie.");
                }
                setBpmModule(bpmm);
                setUser(u);


    //            try {
    //				// Sync user roles
    //				new UserRolesSync().updateUser(bpmm.findUserByUserName(userName));
    //			} catch (Exception e) {
    //				e.printStackTrace();
    //			}
    //
                //mainWindow.initUI();
                return true;
            }
            setUser(null);
        } catch (Exception e) {
			e.printStackTrace();

		}
        setUser(null);
        return false;
    }

    public void setSessionAttribute(String name, Object value) {
        httpSession.setAttribute("PROCESSBASE_SHARED_" + name, value);
    }

    public void removeSessionAttribute(String name) {
        httpSession.removeAttribute("PROCESSBASE_SHARED_" + name);
    }

    public Object getSessionAttribute(String name) {
        return httpSession.getAttribute("PROCESSBASE_SHARED_" + name);
    }

    public String getUserName() {
        return user.username;
    }


    public BPMModule getBpmModule() {
    	if(bpmModule==null)
    		bpmModule = new BPMModule(BPMModule.USER_GUEST);		
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

    /*public byte[] getFileBody(String processUUID, String name) throws Exception {
        return bpmModule.getDo(processUUID, name);
    }*/

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

/**
 * URI fragment utility allows to controlling url # tags in this case for navigating panels

 * @return
 */    
	public UriFragmentUtility getUriFragmentUtility() {
		return uriFragment;
	}

    public void showMainWindow() {
        mainWindow.initUI();
    }

    public void showNotification(String message) {
        mainWindow.showNotification(message);
    }

    public void showError(String message) {
        mainWindow.showNotification("Error", message, Window.Notification.TYPE_ERROR_MESSAGE);
    }
}
