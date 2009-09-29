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
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.AbstractSelect;
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
import org.naxitrale.processbase.Constants;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pbgroup;
import org.naxitrale.processbase.persistence.entity.Pbrole;
import org.naxitrale.processbase.persistence.entity.Pbuser;
import org.naxitrale.processbase.ui.template.PbWindow;
import org.naxitrale.processbase.ui.template.TableExecButton;

/**
 *
 * @author mgubaidullin
 */
public class RoleMembershipWindow extends PbWindow implements ClickListener {

    private Pbrole role = null;
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button cancelBtn = new Button(messages.getString("btnClose"), this);
    private Button applyBtn = new Button(messages.getString("btnAdd"), this);
    private Table membersTable = new Table();
    private Label selectLabel = new Label(messages.getString("candidates"));
    private Select memberSelector = new Select();
    private HibernateUtil hutil = new HibernateUtil();

    public RoleMembershipWindow(Pbrole role) {
        super();
        this.role = role;
    }

    public void exec() {
        try {
            setCaption(messages.getString("roleMembershipWindowCaption") + role.getRolename() + "\"");
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setSizeUndefined();
            refreshTable();
            membersTable.setPageLength(10);
            addComponent(membersTable);
            refreshMemberSelector();
            memberSelector.setFilteringMode(Select.FILTERINGMODE_CONTAINS);
            memberSelector.setNewItemsAllowed(false);
            memberSelector.setItemCaptionPropertyId("membername");
            memberSelector.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);

            buttons.addComponent(selectLabel);
            selectLabel.setWidth("70px");
            buttons.addComponent(memberSelector);
            memberSelector.setWidth("100%");
            buttons.setExpandRatio(memberSelector, 1);
            buttons.addComponent(applyBtn);
            buttons.addComponent(cancelBtn);
            buttons.setSpacing(true);
            buttons.setSizeFull();
            addComponent(buttons);
            setResizable(false);
        } catch (Exception ex) {
            Logger.getLogger(RoleMembershipWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }

    }

    public void refreshMemberSelector() {
        memberSelector.removeAllItems();
        memberSelector.setValue(null);
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("membername", String.class, null);
        container.addContainerProperty("membertype", String.class, null);
        List<Pbuser> users = hutil.getUsersNotInRole(role);
        for (Pbuser user : users) {
            Item item = container.addItem(user);
            item.getItemProperty("membername").setValue(user.toString());
            item.getItemProperty("membertype").setValue(messages.getString("user"));
        }
        List<Pbgroup> groups = hutil.getGroupsNotInRole(role);
        for (Pbgroup group : groups) {
            Item item = container.addItem(group);
            item.getItemProperty("membername").setValue(group.toString());
            item.getItemProperty("membertype").setValue(messages.getString("group"));
        }
        container.sort(new Object[]{"membername"}, new boolean[]{true});
        memberSelector.setContainerDataSource(container);
    }

    public void refreshTable() {
        try {
            membersTable.removeAllItems();
            membersTable.addContainerProperty("membername", String.class, null, messages.getString("participantName"), null, null);
            membersTable.addContainerProperty("membertype", String.class, null, messages.getString("type"), null, null);
            membersTable.addContainerProperty("email", String.class, null, messages.getString("email"), null, null);
            membersTable.addContainerProperty("actions", Button.class, null, messages.getString("tableCaptionActions"), null, null);

            Set<Pbuser> users = hutil.getUsersByRole(role);
            for (Pbuser user : users) {
                Item woItem = membersTable.addItem(user);
                woItem.getItemProperty("membername").setValue(user.toString());
                woItem.getItemProperty("membertype").setValue(messages.getString("user"));
                woItem.getItemProperty("email").setValue(user.getEmail());
                if (!user.getUsername().equalsIgnoreCase("admin")) {
                    woItem.getItemProperty("actions").setValue(new TableExecButton(messages.getString("btnDelete"), "icons/Delete.png", user, this, Constants.ACTION_DELETE));
                }
            }
            Set<Pbgroup> groups = hutil.getGroupsByRole(role);
            for (Pbgroup group : groups) {
                Item woItem = membersTable.addItem(group);
                woItem.getItemProperty("membername").setValue(group.getGroupname());
                woItem.getItemProperty("membertype").setValue(messages.getString("group"));
                woItem.getItemProperty("email").setValue(group.getGroupemail());
                woItem.getItemProperty("actions").setValue(new TableExecButton(messages.getString("btnDelete"), "icons/Delete.png", group, this, Constants.ACTION_DELETE));
            }
            membersTable.setSortContainerPropertyId("membername");
            membersTable.setSortAscending(false);
            membersTable.sort();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                if (memberSelector.getValue() instanceof Pbuser) {
                    hutil.addUserToRole(role, (Pbuser) memberSelector.getValue());
                } else if (memberSelector.getValue() instanceof Pbgroup) {
                    hutil.addGroupToRole(role, (Pbgroup) memberSelector.getValue());
                }
                refreshTable();
                refreshMemberSelector();
            } else if (event.getButton() instanceof TableExecButton) {
                TableExecButton execBtn = (TableExecButton) event.getButton();
                if (execBtn.getAction().equals(Constants.ACTION_DELETE) && execBtn.getTableValue() instanceof Pbuser) {
                    hutil.deleteUserFromRole(role, (Pbuser) ((TableExecButton) event.getButton()).getTableValue());
                } else if (execBtn.getAction().equals(Constants.ACTION_DELETE) && execBtn.getTableValue() instanceof Pbgroup) {
                    hutil.deleteGroupFromRole(role, (Pbgroup) ((TableExecButton) event.getButton()).getTableValue());
                }
                refreshTable();
                refreshMemberSelector();
            } else {
                close();
            }
        } catch (Exception ex) {
            Logger.getLogger(RoleMembershipWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }
}
