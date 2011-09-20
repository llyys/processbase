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
package org.processbase.ui.core;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.ow2.bonita.facade.identity.User;
import org.processbase.ui.osgi.PbPanelModuleService;



/**
 *
 * @author mgubaidullin
 */
public abstract class ProcessbaseApplication extends Application implements TransactionListener, HttpServletRequestListener  {

    static ThreadLocal<ProcessbaseApplication> current = new ThreadLocal<ProcessbaseApplication>();
    public static int LIFERAY_PORTAL = 0;
    public static int STANDALONE = 1;
    protected static Logger LOGGER = Logger.getLogger(ProcessbaseApplication.class);
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	    
    public abstract int getApplicationType();

    public abstract void setSessionAttribute(String name, String value);

    public abstract void removeSessionAttribute(String name);

    public abstract Object getSessionAttribute(String name);

    public abstract String getUserName();
    
    
    
    public User getCurrentUser(){
    	try {
			return getBpmModule().findUserByUserName(getUserName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return null;
		}
    }

    public abstract void setUserName(String userName);

    public abstract BPMModule getBpmModule();

    public abstract void setBpmModule(BPMModule bpmModule);

    public abstract ResourceBundle getPbMessages();

    public abstract ResourceBundle getCustomMessages();

    public abstract void setCustomMessages(ResourceBundle customMessages);

    public abstract void setMessages(ResourceBundle messages);

    public abstract void saveFile(String processUUID, String name, String fileName, byte[] fileBody) throws Exception;
    
    public abstract Map<String, String> getFileList(String processUUID) throws Exception;

    public abstract byte[] getFileBody(String processUUID, String name) throws Exception;

    public abstract PbPanelModuleService getPanelModuleService();

    @Override
    public void init() {
    	
        setCurrent(this);
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }
        initUI();
        if (getContext() != null) {
            getContext().addTransactionListener(this);
        }
    }

    public abstract void initUI();

    /**
     * @return the current application instance
     */
    public static ProcessbaseApplication getCurrent() {
        return current.get();
    }
    
    public static String getString(String key){
    	ResourceBundle messages = getCurrent().getPbMessages();
    	if(messages==null || messages.containsKey(key)==false)
    		return key;
    	return messages.getString(key);
    }
    
    /**
     * Translate using resources
     * @param key
     * @param defaultValue if resource not found
     * @return translated value
     */
    public static String getString(String key, String defaultValue){
    	ResourceBundle messages = getCurrent().getPbMessages();
    	if(messages==null || messages.containsKey(key)==false)
    		return defaultValue;
    	return messages.getString(key);
    }

    /**
     * Set the current application instance
     */
    public static void setCurrent(ProcessbaseApplication application) {
        if (getCurrent() == null) {
            current.set(application);
        }
    }

    /**
     * Remove the current application instance
     */
    public static void removeCurrent() {
        current.remove();
    }

    /**
     * TransactionListener
     */
    public void transactionStart(Application application, Object transactionData) {
        if (application == this) {
            ProcessbaseApplication.setCurrent(this);
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
    
   /* public static String getStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        t.printStackTrace(pw);
        pw.flush();
        sw.flush();
        return sw.toString();
    }*/
    @Override
    public void terminalError(Terminal.ErrorEvent event) {
        // Call the default implementation.
        super.terminalError(event);
        LOGGER.error("RuntimeError", event.getThrowable());
        // Some custom behaviour.
        if (getMainWindow() != null) {
           /* getMainWindow().showNotification(
                    "An unchecked exception occured!",
                    getStackTrace(event.getThrowable()),
                    Notification.TYPE_ERROR_MESSAGE);*/
        	Window  errwindow = new Window("Error");
        	//errwindow.setModal(true);
        	errwindow.setWidth("80%");
        	errwindow.setHeight("80%");
        	VerticalLayout layout=new VerticalLayout();
        	layout.setSpacing(true);
        	Throwable[] exceptionlist = ExceptionUtils.getThrowables(event.getThrowable());
        	for (Throwable throwable : exceptionlist) {
        		String error=ExceptionUtils.getStackTrace(throwable);        		
				layout.addComponent(new Label(error.replaceAll("\n","<BR />"), Label.CONTENT_XHTML));
			}
        	
        	errwindow.addComponent(layout);
        	getMainWindow().addWindow(errwindow);
        }
    }
    
    public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
    	this.httpServletRequest = request;
    	this.httpServletResponse=response;
		LOGGER.debug("[Start of request");
		LOGGER.debug(" Query string: " + request.getQueryString());
		LOGGER.debug(" Path: " + request.getPathInfo());
	}

	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		LOGGER.debug(" End of request]");
		this.httpServletResponse=response;
		this.httpServletRequest=null;		
	}
	

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}


	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}
}
