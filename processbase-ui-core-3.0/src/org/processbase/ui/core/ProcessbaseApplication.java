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
import java.util.Map;
import java.util.ResourceBundle;
import org.processbase.ui.osgi.PbPanelModuleService;

/**
 *
 * @author mgubaidullin
 */
public abstract class ProcessbaseApplication extends Application implements TransactionListener {

    static ThreadLocal<ProcessbaseApplication> current = new ThreadLocal<ProcessbaseApplication>();
    public static int LIFERAY_PORTAL = 0;
    public static int STANDALONE = 1;

    public abstract int getApplicationType();

    public abstract void setSessionAttribute(String name, String value);

    public abstract void removeSessionAttribute(String name);

    public abstract Object getSessionAttribute(String name);

    public abstract String getUserName();

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
}
