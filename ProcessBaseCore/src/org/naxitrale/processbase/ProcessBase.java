/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase;

/**
 *
 * @author mgubaidullin
 */
import com.vaadin.service.ApplicationContext.TransactionListener;

import java.util.List;
import org.naxitrale.processbase.persistence.entity.Pbuser;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.security.User;
import org.naxitrale.processbase.util.PasswordService;

public class ProcessBase extends Application implements TransactionListener {

    private static ThreadLocal<ProcessBase> currentApplication = new ThreadLocal<ProcessBase>();
    private ResourceBundle messages = null;

    @Override
    public void init() {
        setTheme("processbase");
        Locale.setDefault(Locale.ENGLISH);
        WebApplicationContext applicationContext = (WebApplicationContext) this.getContext();
        this.setLocale(applicationContext.getBrowser().getLocale());
        this.messages = ResourceBundle.getBundle("resources/MessagesBundle", this.getLocale());
        this.setMainWindow(new LoginWindow(this.getLocale()));
//        setMainWindow(new TestWindow());
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

    public void authenticate(String login, String password) throws Exception {
        HibernateUtil hutil = new HibernateUtil();
        List<Pbuser> userList = hutil.findPbusersByUsername(login);
        if (userList.size() == 1) {
            Pbuser pbuser = (Pbuser) userList.get(0);
            User user = new User(pbuser);
            user.setBpmAdmin(hutil.isUserInRole(pbuser.getUsername(), "BPMAdmin"));
            user.setAclAdmin(hutil.isUserInRole(pbuser.getUsername(), "ACLAdmin"));
            user.setDashboardAdmin(hutil.isUserInRole(pbuser.getUsername(), "DashboardAdmin"));
            this.setUser(user);
//            this.setLocale(new Locale(pbuser.getLanguage()));
        } else {
            throw new Exception(messages.getString("loginWindowException1"));
        }
        if (this.getUser() == null || !(this.getUser().getPbuser()).getPassword().equalsIgnoreCase(PasswordService.encrypt(password))) {
            throw new Exception(messages.getString("loginWindowException2"));
        }
        setMainWindow(this.getPBMainWindow());
    }

    public MainWindow getPBMainWindow() {
        return new MainWindow();
    }

    public static SystemMessages getSystemMessages() {
        CustomizedSystemMessages m = new CustomizedSystemMessages();
        m.setSessionExpiredURL(null);
        m.setSessionExpiredNotificationEnabled(true);
        m.setSessionExpiredCaption(ProcessBase.getCurrent().messages.getString("exceptionCaption"));
        m.setSessionExpiredMessage(ProcessBase.getCurrent().messages.getString("sessionExpired"));
        return m;
    }

    public void logout() {
        currentApplication.get().close();
    }

    @Override
    public User getUser() {
        return (User) super.getUser();
    }
}
