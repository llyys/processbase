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
import com.vaadin.Application.SystemMessages;
import com.vaadin.service.ApplicationContext.TransactionListener;
import com.vaadin.terminal.DownloadStream;
import com.vaadin.terminal.Terminal;
import com.vaadin.terminal.URIHandler;
import com.vaadin.terminal.gwt.server.HttpServletRequestListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.caliburn.application.event.IEventAggregator;
import org.caliburn.application.event.IHandle;
import org.caliburn.application.event.imp.DefaultEventAggregator;
import org.ow2.bonita.connector.core.configuration.Configuration;
import org.ow2.bonita.facade.identity.User;

import org.processbase.ui.core.template.LazyLoadingLayout;
import org.processbase.ui.osgi.PbPanelModuleService;



/**
 *
 * @author mgubaidullin
 */
public abstract class ProcessbaseApplication extends Application implements TransactionListener, HttpServletRequestListener  {

    static ThreadLocal<ProcessbaseApplication> current = new ThreadLocal<ProcessbaseApplication>();
    public static int LIFERAY_PORTAL = 0;
    public static int STANDALONE = 1;
    public static String AUTH_KEY="AUTH_KEY";
    protected static Logger LOGGER = Logger.getLogger(ProcessbaseApplication.class);
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;
	
	
	public static CustomizedSystemMessages messages = new CustomizedSystemMessages();
	
	 public static SystemMessages getSystemMessages() {
		 
		 messages.setOutOfSyncNotificationEnabled(false);
		 messages.setSessionExpiredNotificationEnabled(false);
		 
        return messages;
    }
	    
    public abstract int getApplicationType();

    public abstract void setSessionAttribute(String name, Object value);

    public abstract void removeSessionAttribute(String name);

    public abstract Object getSessionAttribute(String name);

    public abstract String getUserName();
    
    public abstract void authenticate(String login, String password, boolean rememberMe, String domainName) throws Exception;
    
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

   // public abstract byte[] getFileBody(String processUUID, String name) throws Exception;

    public abstract PbPanelModuleService getPanelModuleService();
    private IEventAggregator events=null;
    @Override
    public void init() {
    	
        setCurrent(this);
        events= new DefaultEventAggregator(); 
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }
        initUI();
        if (getContext() != null) {
            getContext().addTransactionListener(this);
        }
        
        
        getMainWindow().addURIHandler(new ParameterizedResource()); 
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
    
    public static void Publish(Object message) { 
    	
        getCurrent().events.Publish(message);
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
        if("true".equals(Constants.getSetting("HIDE_ERROR_MESSAGES", "false")))
        	return;
        		
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
	/*	LOGGER.debug("[Start of request");
		LOGGER.debug(" Query string: " + request.getQueryString());
		LOGGER.debug(" Path: " + request.getPathInfo());*/
	}

	public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
		//LOGGER.debug(" End of request]");
		this.httpServletResponse=response;
		this.httpServletRequest=null;		
	}
	

	public HttpServletRequest getHttpServletRequest() {
		return httpServletRequest;
	}


	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}

	public static void Register(Object handler) {
		getCurrent().events.Subscribe(handler);		
	}
	/**
     * Creates and starts a new thread. This method must be used instead of <code>new
     * Thread(...)</code> construction when starting server threads if you wish to access toolkit UI
     * data, i18n and be able to get the current application instance.
     *
     * @param task Runnable task to execute in a separate thread
     * @return instance of the created and started thread.
     */
    public Thread invokeLater ( Runnable task )
    {
        Thread thread = new Thread ( new TPTRunnable ( this, task ) );
        thread.start ();
        return thread;
    }
	public void invokeLater(LazyLoadingLayout lazyLoadingLayout) {
		
	}
	private class TPTRunnable implements Runnable
    {

        private ProcessbaseApplication application;
        private Runnable actualTask;

        public TPTRunnable ( ProcessbaseApplication app, Runnable task )
        {
            application = app;
            actualTask = task;
        }

        public void run ()
        {
            try
            {
                application.current.set ( application );
                actualTask.run ();
            }
            catch ( Throwable err )
            {
                //todo: rework possible thread body exceptions handling ?
                err.printStackTrace ();
            }
            finally
            {
                if ( current != null )
                {
                	current.remove ();
                }

                application = null;
                actualTask = null;
            }
        }
    }
	
}
