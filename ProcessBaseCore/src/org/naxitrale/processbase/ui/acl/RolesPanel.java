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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pbrole;
import org.naxitrale.processbase.ui.template.TableExecButton;
import org.naxitrale.processbase.ui.template.TableExecButtonBar;
import org.naxitrale.processbase.ui.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class RolesPanel extends TablePanel {

    Button addBtn = new Button("Новая");

    public RolesPanel() {
        super();
        buttonBar.addComponent(addBtn, 0);
        buttonBar.setComponentAlignment(addBtn, Alignment.MIDDLE_RIGHT);
        addBtn.addListener(new Button.ClickListener() {

            public void buttonClick(Button.ClickEvent event) {
                RoleWindow roleWindow = new RoleWindow(null);
                roleWindow.exec();
                roleWindow.addListener(new Window.CloseListener() {

                    public void windowClose(CloseEvent e) {
                        refreshTable();
                    }
                });

                getApplication().getMainWindow().addWindow(roleWindow);

            }
        });
        refreshTable();
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            table.addContainerProperty("name", String.class, null, "Имя роли", null, null);
            table.addContainerProperty("operation", TableExecButtonBar.class, null, "Операции", null, null);
            HibernateUtil hutil = new HibernateUtil();
            List<Pbrole> roles = hutil.findAllPbroles("APP");
            roles.addAll(hutil.findAllPbroles("SYSTEM"));
            for (Pbrole role : roles) {
                Item woItem = table.addItem(role);
                woItem.getItemProperty("name").setValue(role.getRolename());
                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(new TableExecButton("Участники", "icons/MembershipSelector.gif", role, this));
                tebb.addButton(new TableExecButton("Процессы", "icons/process.gif", role, this));
                if (!role.getPbtype().equalsIgnoreCase("SYSTEM")) {
                    tebb.addButton(new TableExecButton("Редактировать", "icons/Edit.gif", role, this));
                    tebb.addButton(new TableExecButton("Удалить", "icons/Delete.png", role, this));
                }
                woItem.getItemProperty("operation").setValue(tebb);
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            Logger.getLogger(RolesPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton().equals(refreshBtn)) {
            refreshTable();
        } else if (event.getButton() instanceof TableExecButton) {
            try {
                Pbrole r = (Pbrole) ((TableExecButton) event.getButton()).getTableValue();
                if (event.getButton().getDescription().equalsIgnoreCase("Удалить")) {
                    HibernateUtil hutil = new HibernateUtil();
                    hutil.delete(r);
                } else if (event.getButton().getDescription().equalsIgnoreCase("Редактировать")) {
                    RoleWindow roleWindow = new RoleWindow(r);
                    roleWindow.exec();
                    roleWindow.addListener((CloseListener) this);
                    getApplication().getMainWindow().addWindow(roleWindow);
                } else if (event.getButton().getDescription().equalsIgnoreCase("Участники")) {
                    RoleMembershipWindow roleMembershipWindow = new RoleMembershipWindow(r);
                    roleMembershipWindow.exec();
                    roleMembershipWindow.addListener((CloseListener) this);
                    getApplication().getMainWindow().addWindow(roleMembershipWindow);
                } else if (event.getButton().getDescription().equalsIgnoreCase("Процессы")) {
                    RoleProcessesWindow roleProcessesWindow = new RoleProcessesWindow(r);
                    roleProcessesWindow.exec();
                    roleProcessesWindow.addListener((CloseListener) this);
                    getApplication().getMainWindow().addWindow(roleProcessesWindow);
                }
                refreshTable();
            } catch (Exception ex) {
                Logger.getLogger(RolesPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
            }
        }
    }
}
