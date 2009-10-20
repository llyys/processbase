/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.Constants;
import org.processbase.acl.persistence.HibernateUtil;
import org.processbase.acl.persistence.Pbuser;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.ui.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class JobsPanel extends TablePanel {

    private Button addBtn = new Button(messages.getString("btnAdd"), this);
    private HibernateUtil hutil = new HibernateUtil();

    public JobsPanel() {
        super();
        buttonBar.addComponent(addBtn, 0);
//        initTableUI();
//        refreshTable();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionUsername"), null, null);
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
                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(new TableExecButton(messages.getString("btnEdit"), "icons/Edit.gif", user, this, Constants.ACTION_EDIT));
                tebb.addButton(new TableExecButton(messages.getString("btnDelete"), "icons/Delete.png", user, this, Constants.ACTION_DELETE));
                woItem.getItemProperty("actions").setValue(tebb);
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            Logger.getLogger(JobsPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
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
            Logger.getLogger(JobsPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    @Override
    public void windowClose(CloseEvent e) {
        super.windowClose(e);
    }
}

