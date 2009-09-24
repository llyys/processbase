/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.template;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.Window;
import java.util.ResourceBundle;
import org.naxitrale.processbase.ProcessBase;

/**
 *
 * @author mgubaidullin
 */
public class PbWindow extends Window {

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
        showNotification(messages.getString("exceptionCaption"), description, Notification.TYPE_ERROR_MESSAGE);
    }
}
