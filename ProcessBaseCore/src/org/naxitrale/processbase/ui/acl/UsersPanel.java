/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.acl;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pbuser;
import org.naxitrale.processbase.ui.template.TableExecButton;
import org.naxitrale.processbase.ui.template.TableExecButtonBar;
import org.naxitrale.processbase.ui.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class UsersPanel extends TablePanel {

    private Button addBtn = new Button("Новый", this);
    private HibernateUtil hutil = new HibernateUtil();

    public UsersPanel() {
        super();
        buttonBar.addComponent(addBtn, 0);
        refreshTable();
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            table.addContainerProperty("name", String.class, null, "Имя пользователя", null, null);
            table.addContainerProperty("lastName", String.class, null, "Имя роли", null, null);
            table.addContainerProperty("firstName", String.class, null, "Имя", null, null);
            table.addContainerProperty("middleName", String.class, null, "Отчество", null, null);
            table.addContainerProperty("birthday", String.class, null, "Дата рождения", null, null);
            table.addContainerProperty("email", String.class, null, "E-mail", null, null);
            table.addContainerProperty("position", String.class, null, "должность", null, null);
            table.addContainerProperty("operation", TableExecButtonBar.class, null, "Операции", null, null);
            List<Pbuser> users = hutil.findAllPbusers("APP");
            for (Pbuser user : users) {
                Item woItem = table.addItem(user);
                woItem.getItemProperty("name").setValue(user.getUsername());
                woItem.getItemProperty("lastName").setValue(user.getLastname());
                woItem.getItemProperty("firstName").setValue(user.getFirstname());
                woItem.getItemProperty("middleName").setValue(user.getMiddlename());
                woItem.getItemProperty("birthday").setValue(String.format("%tF", new Object[]{user.getBirthdate()}));
                woItem.getItemProperty("email").setValue(user.getEmail());
                woItem.getItemProperty("position").setValue(user.getPosition());
                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(new TableExecButton("Редактировать", "icons/Edit.gif", user, this));
                tebb.addButton(new TableExecButton("Удалить", "icons/Delete.png", user, this));
                woItem.getItemProperty("operation").setValue(tebb);
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            Logger.getLogger(UsersPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            getWindow().showNotification("Ошибка", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        try {
            if (event.getButton().equals(addBtn)) {
                UserWindow userWindow = new UserWindow(null);
                userWindow.exec();
                userWindow.addListener((Window.CloseListener) this);
                getApplication().getMainWindow().addWindow(userWindow);
            } else if (event.getButton() instanceof TableExecButton && event.getButton().getDescription().equalsIgnoreCase("Удалить")) {
                Pbuser u = (Pbuser) ((TableExecButton) event.getButton()).getTableValue();
                hutil.delete(u);
                refreshTable();
            } else if (event.getButton() instanceof TableExecButton && event.getButton().getDescription().equalsIgnoreCase("Редактировать")) {
                Pbuser u = (Pbuser) ((TableExecButton) event.getButton()).getTableValue();
                UserWindow userWindow = new UserWindow(u);
                userWindow.addListener((Window.CloseListener) this);
                userWindow.exec();
                getApplication().getMainWindow().addWindow(userWindow);
            }
        } catch (Exception ex) {
            Logger.getLogger(UsersPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            getWindow().showNotification("Ошибка", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    @Override
    public void windowClose(CloseEvent e) {
        super.windowClose(e);
    }
}

