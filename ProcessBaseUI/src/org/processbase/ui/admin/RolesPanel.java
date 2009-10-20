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
import org.processbase.Constants;
import org.processbase.acl.persistence.HibernateUtil;
import org.processbase.acl.persistence.Pbrole;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.ui.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class RolesPanel extends TablePanel implements Button.ClickListener, Window.CloseListener {

    Button addBtn = new Button(messages.getString("btnAdd"));

    public RolesPanel() {
        super();
        buttonBar.addComponent(addBtn, 0);
        buttonBar.setComponentAlignment(addBtn, Alignment.MIDDLE_RIGHT);
        addBtn.addListener(this);
        initTableUI();
        refreshTable();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionRoleName"), null, null);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            HibernateUtil hutil = new HibernateUtil();
            List<Pbrole> roles = hutil.findAllPbroles("APP");
            roles.addAll(hutil.findAllPbroles("SYSTEM"));
            for (Pbrole role : roles) {
                Item woItem = table.addItem(role);
                woItem.getItemProperty("name").setValue(role.getRolename());
                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(new TableExecButton(messages.getString("btnParticipants"), "icons/MembershipSelector.gif", role, this, Constants.ACTION_EDIT_PARTICIPANTS));
                tebb.addButton(new TableExecButton(messages.getString("btnProcesses"), "icons/process.gif", role, this, Constants.ACTION_EDIT_PROCESSES));
                if (!role.getPbtype().equalsIgnoreCase("SYSTEM")) {
                    tebb.addButton(new TableExecButton(messages.getString("btnEdit"), "icons/Edit.gif", role, this, Constants.ACTION_EDIT));
                    tebb.addButton(new TableExecButton(messages.getString("btnDelete"), "icons/Delete.png", role, this, Constants.ACTION_DELETE));
                }
                woItem.getItemProperty("actions").setValue(tebb);
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            Logger.getLogger(RolesPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.toString());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton().equals(addBtn)) {
            RoleWindow roleWindow = new RoleWindow(null);
            roleWindow.exec();
            roleWindow.addListener((CloseListener) this);
            getApplication().getMainWindow().addWindow(roleWindow);
        } else if (event.getButton().equals(refreshBtn)) {
            refreshTable();
        } else if (event.getButton() instanceof TableExecButton) {
            try {
                TableExecButton execBtn = (TableExecButton) event.getButton();
                Pbrole r = (Pbrole) ((TableExecButton) event.getButton()).getTableValue();
                if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                    HibernateUtil hutil = new HibernateUtil();
                    hutil.delete(r);
                } else if (execBtn.getAction().equals(Constants.ACTION_EDIT)) {
                    RoleWindow roleWindow = new RoleWindow(r);
                    roleWindow.exec();
                    roleWindow.addListener((CloseListener) this);
                    getApplication().getMainWindow().addWindow(roleWindow);
                } else if (execBtn.getAction().equals(Constants.ACTION_EDIT_PARTICIPANTS)) {
                    RoleMembershipWindow roleMembershipWindow = new RoleMembershipWindow(r);
                    roleMembershipWindow.exec();
                    roleMembershipWindow.addListener((CloseListener) this);
                    getApplication().getMainWindow().addWindow(roleMembershipWindow);
                } else if (execBtn.getAction().equals(Constants.ACTION_EDIT_PROCESSES)) {
                    RoleProcessesWindow roleProcessesWindow = new RoleProcessesWindow(r);
                    roleProcessesWindow.exec();
                    roleProcessesWindow.addListener((CloseListener) this);
                    getApplication().getMainWindow().addWindow(roleProcessesWindow);
                }
                refreshTable();
            } catch (Exception ex) {
                Logger.getLogger(RolesPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                showError(ex.toString());
            }
        }
    }
}
