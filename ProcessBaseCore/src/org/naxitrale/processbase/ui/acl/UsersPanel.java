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
import org.naxitrale.processbase.Constants;
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

    private Button addBtn = new Button(messages.getString("btnAdd"), this);
    private HibernateUtil hutil = new HibernateUtil();

    public UsersPanel() {
        super();
        buttonBar.addComponent(addBtn, 0);
        initTableUI();
        refreshTable();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionUsername"), null, null);
        table.addContainerProperty("lastName", String.class, null, messages.getString("tableCaptionLastname"), null, null);
        table.addContainerProperty("firstName", String.class, null, messages.getString("tableCaptionFirstname"), null, null);
        table.addContainerProperty("middleName", String.class, null, messages.getString("tableCaptionMiddlename"), null, null);
        table.addContainerProperty("birthday", String.class, null, messages.getString("tableCaptionBirthdate"), null, null);
        table.addContainerProperty("email", String.class, null, messages.getString("tableCaptionEmail"), null, null);
        table.addContainerProperty("position", String.class, null, messages.getString("tableCaptionPosition"), null, null);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
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
                tebb.addButton(new TableExecButton(messages.getString("btnEdit"), "icons/Edit.gif", user, this, Constants.ACTION_EDIT));
                tebb.addButton(new TableExecButton(messages.getString("btnDelete"), "icons/Delete.png", user, this, Constants.ACTION_DELETE));
                woItem.getItemProperty("actions").setValue(tebb);
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            Logger.getLogger(UsersPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
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
            } else if (event.getButton() instanceof TableExecButton) {
                TableExecButton execBtn = (TableExecButton) event.getButton();
                Pbuser u = (Pbuser) execBtn.getTableValue();
                if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                    hutil.delete(u);
                    refreshTable();
                } else if (execBtn.getAction().equals(Constants.ACTION_EDIT)) {
                    UserWindow userWindow = new UserWindow(u);
                    userWindow.addListener((Window.CloseListener) this);
                    userWindow.exec();
                    getApplication().getMainWindow().addWindow(userWindow);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UsersPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    @Override
    public void windowClose(CloseEvent e) {
        super.windowClose(e);
    }
}

