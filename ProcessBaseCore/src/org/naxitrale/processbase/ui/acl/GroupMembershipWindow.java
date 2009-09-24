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
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pbgroup;
import org.naxitrale.processbase.persistence.entity.Pbuser;
import org.naxitrale.processbase.ui.template.TableExecButton;

/**
 *
 * @author mgubaidullin
 */
public class GroupMembershipWindow extends Window implements ClickListener {

    private Pbgroup group = null;
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button cancelBtn = new Button("Закрыть", this);
    private Button applyBtn = new Button("Добавить", this);
    private Table membersTable = new Table();
    private Label selectLabel = new Label("Кандидаты");
    private Select userSelector = new Select();
    private HibernateUtil hutil = new HibernateUtil();

    public GroupMembershipWindow(Pbgroup group) {
        super();
        this.group = group;
    }

    public void exec() {
        try {
            setCaption("Участники группы \"" + group.getGroupname() + "\"");
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setSizeUndefined();
            refreshTable();
            membersTable.setPageLength(10);
            addComponent(membersTable);
            refreshUserSelector();
            userSelector.setFilteringMode(Select.FILTERINGMODE_CONTAINS);
            userSelector.setNewItemsAllowed(false);
            userSelector.setNullSelectionAllowed(false);

            buttons.addComponent(selectLabel);
            selectLabel.setWidth("70px");
            buttons.addComponent(userSelector);
            userSelector.setWidth("100%");
            buttons.setExpandRatio(userSelector, 1);
            buttons.addComponent(applyBtn);
            buttons.addComponent(cancelBtn);
            buttons.setSpacing(true);
            buttons.setSizeFull();
            addComponent(buttons);
            setResizable(false);
        } catch (Exception ex) {
            Logger.getLogger(GroupMembershipWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            getWindow().showNotification("Ошибка", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }

    }

    public void refreshUserSelector() {
        userSelector.removeAllItems();
        userSelector.setValue(null);
        List<Pbuser> users = hutil.getUsersNotInGroup(group);
        for (Pbuser user : users) {
            userSelector.addItem(user);
        }
    }

    public void refreshTable() {
        try {
            membersTable.removeAllItems();
            membersTable.addContainerProperty("username", String.class, null, "Имя пользователя", null, null);
            membersTable.addContainerProperty("lastname", String.class, null, "Фамилия", null, null);
            membersTable.addContainerProperty("firstname", String.class, null, "Имя", null, null);
            membersTable.addContainerProperty("email", String.class, null, "Email", null, null);
            membersTable.addContainerProperty("operation", Button.class, null, "Операции", null, null);

            Set<Pbuser> users = hutil.getUsersByGroup(group);
            for (Pbuser user : users) {
                Item woItem = membersTable.addItem(user);
                woItem.getItemProperty("username").setValue(user.getUsername());
                woItem.getItemProperty("lastname").setValue(user.getLastname());
                woItem.getItemProperty("firstname").setValue(user.getFirstname());
                woItem.getItemProperty("email").setValue(user.getEmail());
                woItem.getItemProperty("operation").setValue(new TableExecButton("Удалить", "icons/Delete.png", user, this));
            }
            membersTable.setSortContainerPropertyId("username");
            membersTable.setSortAscending(false);
            membersTable.sort();
        } catch (Exception ex) {
            getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                hutil.addUserToGroup(group, (Pbuser) userSelector.getValue());
                refreshTable();
                refreshUserSelector();
            } else if (event.getButton() instanceof TableExecButton && event.getButton().getDescription().equalsIgnoreCase("Удалить")) {
                Pbuser user = (Pbuser) ((TableExecButton) event.getButton()).getTableValue();
                hutil.deleteUserFromGroup(group, user);
                refreshTable();
                refreshUserSelector();
            } else {
                close();
            }
        } catch (Exception ex) {
            Logger.getLogger(GroupMembershipWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
        }
    }
}
