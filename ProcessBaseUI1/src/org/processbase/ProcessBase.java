/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase;

/**
 *
 * @author mgubaidullin
 */
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import org.processbase.util.Constants;
import org.processbase.util.ldap.User;
import com.vaadin.service.ApplicationContext.TransactionListener;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import java.util.Locale;
import java.util.ResourceBundle;
import org.processbase.bpm.BPMModule;
import org.processbase.util.ldap.LdapUtils;

public class ProcessBase extends Application implements TransactionListener {

    private static ThreadLocal<ProcessBase> currentApplication = new ThreadLocal<ProcessBase>();
    private ResourceBundle messages = null;
    private BPMModule bpmModule = null;

    @Override
    public void init() {
        setTheme("processbase");
        Locale.setDefault(Locale.ENGLISH);
        WebApplicationContext applicationContext = (WebApplicationContext) this.getContext();
        this.setLocale(applicationContext.getBrowser().getLocale());
        this.messages = ResourceBundle.getBundle("resources/MessagesBundle", this.getLocale());
        this.setMainWindow(new LoginWindow(this.getLocale()));
        if (!Constants.LOADED) {
            Constants.loadConstants();
        }
        if (getContext() != null) {
            getContext().addTransactionListener(this);
        }
    }

    /**
     * @return the current application instance
     */
    public static ProcessBase getCurrent() {
        return currentApplication.get();
    }

    /**
     * Set the current application instance
     */
    public static void setCurrent(ProcessBase application) {
        if (getCurrent() == null) {
            currentApplication.set(application);
        }
    }

    @Override
    public void transactionEnd(Application application, Object transactionData) {
        currentApplication.remove();
    }

    @Override
    public void transactionStart(Application application, Object transactionData) {
        ProcessBase.setCurrent(this);

    }

    public void authenticate(String login, String password) throws NamingException, Exception {
        LdapUtils ldapUtils = new LdapUtils(login, null, password);
        this.setUser(ldapUtils.authenticate());
        this.bpmModule = new BPMModule(login);
        setMainWindow(this.getPBMainWindow());

    }

    public MainWindow getPBMainWindow() {
        return new MainWindow();
    }

    public static SystemMessages getSystemMessages() {
        CustomizedSystemMessages m = new CustomizedSystemMessages();
        m.setSessionExpiredURL(null);
        m.setSessionExpiredNotificationEnabled(true);
        m.setSessionExpiredCaption(null);
        m.setSessionExpiredMessage("Время действия вашей сессии истекло!");
        m.setCommunicationErrorCaption(null);
        m.setCommunicationErrorMessage("Ошибка соединения!");
        m.setOutOfSyncCaption(null);
        m.setInternalErrorCaption(null);
        m.setInternalErrorMessage("Внутренняя ошибка!");
        return m;
    }

    public void logout() {
        currentApplication.get().close();
    }

    @Override
    public User getUser() {
        return (User) super.getUser();
    }

    public BPMModule getBpmModule() {
        return bpmModule;
    }
}
