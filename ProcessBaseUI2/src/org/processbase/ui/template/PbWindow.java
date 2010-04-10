/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
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
package org.processbase.ui.template;

import com.liferay.portal.model.User;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Window;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.portlet.PortletSession;

/**
 *
 * @author mgubaidullin
 */
public class PbWindow extends Window {

    protected boolean confirmResult = false;
    protected ResourceBundle messages = null;
    protected PortletApplicationContext2 portletApplicationContext2 = null;

    public PbWindow(String caption, PortletApplicationContext2 portletApplicationContext2) {
        super(caption);
        this.portletApplicationContext2 = portletApplicationContext2;
        messages = ResourceBundle.getBundle("resources/MessagesBundle", getCurrentLocale());
        ((Layout) getContent()).setStyleName("white");
    }

    public PbWindow(PortletApplicationContext2 portletApplicationContext2) {
        this("", portletApplicationContext2);
    }

    public void showMessageWindow(String message, int windowStyle) {
        MessageWindow messageWindow = new MessageWindow(message, windowStyle, getPortletApplicationContext2());
        getApplication().getMainWindow().addWindow(messageWindow);
    }

    public void showError(String description) {
        showMessage(description, Notification.TYPE_ERROR_MESSAGE);
    }

    public void showInformation(String description) {
        showMessage(description, Notification.TYPE_HUMANIZED_MESSAGE);
    }

    public void showWarning(String description) {
        showMessage(description, Notification.TYPE_WARNING_MESSAGE);
    }

    public void showMessage(String description, int type) {
        StringBuffer desc = new StringBuffer();
        if (description != null) {
            int i = 0;
            for (; description.length() > i + 50; i = i + 50) {
                desc.append("<br/> " + description.substring(i, i + 50));
            }
            desc.append("<br/> " + description.substring(i));
        } else {
            desc.append("<br/> null");
        }
        switch (type) {
            case Notification.TYPE_WARNING_MESSAGE:
                showNotification(messages.getString("warningCaption"), desc.substring(0), type);
                break;
            case Notification.TYPE_HUMANIZED_MESSAGE:
                showNotification(messages.getString("informationCaption"), desc.substring(0), type);
                break;
            case Notification.TYPE_ERROR_MESSAGE:
                showNotification(messages.getString("exceptionCaption"), desc.substring(0), type);
                break;
        }
    }

    public ResourceBundle getMessages() {
        return messages;
    }

    public PortletApplicationContext2 getPortletApplicationContext2() {
        return portletApplicationContext2;
    }

    public User getCurrentUser() {
        return ((User) portletApplicationContext2.getPortletSession().getAttribute("PROCESSBASE_USER", PortletSession.APPLICATION_SCOPE));
    }

    public Locale getCurrentLocale() {
        return (Locale) portletApplicationContext2.getPortletSession().getAttribute("org.apache.struts.action.LOCALE", PortletSession.APPLICATION_SCOPE);
    }

    @Override
    public void close() {
        super.close();
    }


}
