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

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Window;
import java.util.ResourceBundle;
import org.processbase.ProcessBase;

/**
 *
 * @author mgubaidullin
 */
public class PbWindow extends Window {

    protected boolean confirmResult = false;
    protected ResourceBundle messages = ResourceBundle.getBundle("resources/MessagesBundle", ((ProcessBase) getApplication()).getCurrent().getLocale());

    public PbWindow(String caption, ComponentContainer content) {
        super(caption, content);
    }

    public PbWindow(String caption) {
        super(caption);
    }

    public PbWindow() {
    }

    public void showMessageWindow(String message, int windowStyle) {
        MessageWindow messageWindow = new MessageWindow(message, windowStyle);
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
}
