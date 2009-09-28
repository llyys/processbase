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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.ProcessBase;
import org.naxitrale.processbase.bpm.AdminModule;
import org.naxitrale.processbase.bpm.WorklistModule;
import org.naxitrale.processbase.ui.portal.HelpPanel;

/**
 *
 * @author mgubaidullin
 */
public class WorkPanel extends Panel implements Button.ClickListener, Window.CloseListener {

    protected ResourceBundle messages = ResourceBundle.getBundle("resources/MessagesBundle", ((ProcessBase) getApplication()).getCurrent().getLocale());
    protected VerticalLayout verticalLayout = new VerticalLayout();
    protected HorizontalLayout horizontalLayout = new HorizontalLayout();
    protected ButtonBar buttonBar = new ButtonBar();
    protected Button showHelpBtn = new Button(messages.getString("btnHelp"), this);
    protected Button refreshBtn = new Button(messages.getString("btnRefresh"), this);
    protected AdminModule adminModule = new AdminModule();
    protected WorklistModule worklistModule = new WorklistModule();
//    protected HelpPanel helpPanel = new HelpPanel(
//            "В данном разделе будет указана справочная информация о функциях активного окна, что может существенно облегчить работу пользователей системы",
//            "Самая важная справочная информация может быть выделена цветом.",
//            null,
//            "Дополнительно смотрите Документацию", null);
//
    protected HelpPanel helpPanel = new HelpPanel(
            "This is help information about active Tab. We will add context help documentation here.",
            "The main information can be highlighted to be more visible.",
            null,
            "You can also click here to open full Documentation", null);

    

    public WorkPanel() {
        super();
        // prepare help button
        showHelpBtn.setSwitchMode(true);
        showHelpBtn.setDescription(messages.getString("btnHelpDescription"));
        buttonBar.addComponent(refreshBtn);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_LEFT);
        buttonBar.addComponent(showHelpBtn);
        buttonBar.setComponentAlignment(showHelpBtn, Alignment.MIDDLE_RIGHT);
        buttonBar.setExpandRatio(showHelpBtn, 1);

        horizontalLayout.setSizeFull();
        horizontalLayout.setStyleName("gradient");

        verticalLayout.setSizeFull();
        verticalLayout.addComponent(buttonBar);
        verticalLayout.setStyleName("gradient");
        verticalLayout.addComponent(horizontalLayout);
        verticalLayout.setExpandRatio(horizontalLayout, 1);
        setContent(verticalLayout);
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(showHelpBtn)) {
            if (event.getButton().booleanValue()) {
                horizontalLayout.addComponent(helpPanel);
                horizontalLayout.setComponentAlignment(helpPanel, Alignment.TOP_RIGHT);
            } else {
                horizontalLayout.removeComponent(helpPanel);
            }
        }

    }

    public void windowClose(CloseEvent e) {
    }

    public void showError(String errorMessage){
        getWindow().showNotification(messages.getString("exceptionCaption"), errorMessage, Notification.TYPE_ERROR_MESSAGE);
    }
}
