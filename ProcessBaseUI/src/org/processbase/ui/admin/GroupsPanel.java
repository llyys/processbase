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
import com.vaadin.ui.Window;
import java.util.List;
import org.processbase.Constants;
import org.processbase.acl.persistence.HibernateUtil;
import org.processbase.acl.persistence.Pbgroup;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.ui.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class GroupsPanel extends TablePanel implements Button.ClickListener {

    Button addBtn = new Button(messages.getString("btnAdd"));

    public GroupsPanel() {
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
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionGroupname"), null, null);
        table.addContainerProperty("email", String.class, null, messages.getString("tableCaptionEmail"), null, null);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            HibernateUtil hutil = new HibernateUtil();
            List<Pbgroup> groups = hutil.findAllGroups("APP");
            for (Pbgroup group : groups) {
                Item woItem = table.addItem(group);
                woItem.getItemProperty("name").setValue(group.getGroupname());
                woItem.getItemProperty("email").setValue(group.getGroupemail());
                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(new TableExecButton(messages.getString("btnParticipants"), "icons/MembershipSelector.gif", group, this, Constants.ACTION_EDIT_PARTICIPANTS));
                tebb.addButton(new TableExecButton(messages.getString("btnEdit"), "icons/Edit.gif", group, this, Constants.ACTION_EDIT));
                tebb.addButton(new TableExecButton(messages.getString("btnDelete"), "icons/Delete.png", group, this, Constants.ACTION_DELETE));
                woItem.getItemProperty("actions").setValue(tebb);
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

   @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton().equals(addBtn)) {
            GroupWindow groupWindow = new GroupWindow(null);
            groupWindow.exec();
            groupWindow.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(groupWindow);
        } else if (event.getButton() instanceof TableExecButton) {
            TableExecButton execBtn = (TableExecButton) event.getButton();
            Pbgroup g = (Pbgroup) execBtn.getTableValue();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                HibernateUtil hutil = new HibernateUtil();
                hutil.delete(g);
            } else if (execBtn.getAction().equals(Constants.ACTION_EDIT)) {
                GroupWindow groupWindow = new GroupWindow(g);
                groupWindow.exec();
                groupWindow.addListener((Window.CloseListener) this);
                getApplication().getMainWindow().addWindow(groupWindow);
            } else if (execBtn.getAction().equals(Constants.ACTION_EDIT_PARTICIPANTS)) {
                GroupMembershipWindow groupMembershipWindow = new GroupMembershipWindow(g);
                groupMembershipWindow.exec();
                groupMembershipWindow.addListener((Window.CloseListener) this);
                getApplication().getMainWindow().addWindow(groupMembershipWindow);
            }
            refreshTable();
        }
    }
}
