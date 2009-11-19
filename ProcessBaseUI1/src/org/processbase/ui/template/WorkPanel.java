/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.template;

import org.processbase.ui.help.HelpPanel;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import java.util.ResourceBundle;
import org.processbase.ProcessBase;

/**
 *
 * @author mgubaidullin
 */
public class WorkPanel extends VerticalLayout implements Button.ClickListener, Window.CloseListener {

    protected ResourceBundle messages = ResourceBundle.getBundle("resources/MessagesBundle", ((ProcessBase) getApplication()).getCurrent().getLocale());
    protected HorizontalLayout horizontalLayout = new HorizontalLayout();
    protected ButtonBar buttonBar = new ButtonBar();
    protected Button refreshBtn = new Button(messages.getString("btnRefresh"), this);

    protected HelpPanel helpPanel = new HelpPanel();

    public WorkPanel() {
        super();
        // prepare help button
        buttonBar.addComponent(refreshBtn);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_LEFT);

        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("gradient");
        horizontalLayout.addComponent(helpPanel);
        horizontalLayout.setComponentAlignment(helpPanel, Alignment.TOP_RIGHT);
        helpPanel.setVisible(false);

        setSizeFull();
        addComponent(buttonBar);
        setStyleName("gradient");
        addComponent(horizontalLayout);
        setExpandRatio(horizontalLayout, 1);
    }

    public void buttonClick(ClickEvent event) {
    }

    public void windowClose(CloseEvent e) {
    }

    public void showError(String errorMessage) {
        getWindow().showNotification(messages.getString("exceptionCaption"), errorMessage, Notification.TYPE_ERROR_MESSAGE);
    }

    public void showMessageWindow(String message, int windowStyle) {
        if (getWindow() instanceof PbWindow) {
            ((PbWindow) getApplication().getMainWindow()).showMessageWindow(message, windowStyle);
        }
    }

    public boolean showConfirmMessageWindow(String message, int windowStyle) {
        boolean result = false;
        showMessageWindow(message, windowStyle);
        result = ((PbWindow) getApplication().getMainWindow()).confirmResult;
        return result;
    }

    public HelpPanel getHelpPanel() {
        return helpPanel;
    }

    public void setHelpPanel(HelpPanel helpPanel) {
        this.helpPanel = helpPanel;
    }

    
}
