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
package org.processbase.ui.core.template;

import com.vaadin.ui.Layout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

import org.apache.log4j.Logger;
import org.processbase.ui.core.ProcessbaseApplication;

/**
 *
 * @author mgubaidullin
 */
public class PbWindow extends Window {
	 public static final int TYPE_IMPORTANT_MESSAGE = 5;
    public boolean confirm = false;
    protected static Logger LOGGER = Logger.getLogger(PbWindow.class);
    public PbWindow() {
    }

    

    public PbWindow(String caption) {
        super(caption);
        ((Layout) getContent()).setStyleName(Reindeer.LAYOUT_WHITE);
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

    public void showImportantInformation(String description) {
        showMessage(description, TYPE_IMPORTANT_MESSAGE);
    }

    public void showMessage(String description, int type) {
        StringBuilder desc = new StringBuilder();
        if (description != null) {
            int i = 0;
            for (; description.length() > i + 50; i = i + 50) {
                desc.append("<br/> ").append(description.substring(i, i + 50));
            }
            desc.append("<br/> ").append(description.substring(i));
        } else {
            desc.append("<br/> null");
        }
        Notification notification=null;
        switch (type) {
            case Notification.TYPE_WARNING_MESSAGE:
            	notification=new Notification(ProcessbaseApplication.getString("warningCaption"), desc.substring(0), type);
            	notification.setDelayMsec(-1);//click to hide
                break;
            case Notification.TYPE_HUMANIZED_MESSAGE:
            	notification=new Notification(ProcessbaseApplication.getString("informationCaption"), desc.substring(0), type);
            	notification.setDelayMsec(1000);//wait 1 sec
            	break;
            case TYPE_IMPORTANT_MESSAGE:
            	notification=new Notification(ProcessbaseApplication.getString("informationCaption"), desc.substring(0), Notification.TYPE_ERROR_MESSAGE);
            	notification.setDelayMsec(-1);//click to hide
                break;            
            case Notification.TYPE_ERROR_MESSAGE:
            	notification=new Notification(ProcessbaseApplication.getString("exceptionCaption"), desc.substring(0), type);
            	notification.setDelayMsec(1000);//wait 1 sec
                break;
        }
        super.showNotification(notification);
    }

    @Override
    public void close() {
        super.close();
    }
}
