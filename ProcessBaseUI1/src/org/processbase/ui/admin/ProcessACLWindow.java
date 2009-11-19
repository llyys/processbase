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
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.Constants;
import org.processbase.util.db.HibernateUtil;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ProcessBase;
import org.processbase.util.ldap.User;
import org.processbase.util.db.PbProcessAcl;
import org.processbase.util.ldap.Group;
import org.processbase.util.ldap.LdapUtils;

/**
 *
 * @author mgubaidullin
 */
public class ProcessACLWindow extends PbWindow implements ClickListener {

    private String processUUID = null;
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button cancelBtn = new Button(messages.getString("btnClose"), this);
    private Button applyBtn = new Button(messages.getString("btnAdd"), this);
    private Table membersTable = new Table();
    private Label selectLabel = new Label(messages.getString("candidates"));
    private Select memberSelector = new Select();
    private HibernateUtil hutil = new HibernateUtil();
    
    public ProcessACLWindow(String processUUID) {
        super();
        this.processUUID = processUUID;
    }

    public void exec() {
        try {
            setCaption(messages.getString("captionAvailableProcesses") + " \"" + processUUID + "\"");
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
//            layout.setSizeUndefined();
            refreshTable();
            membersTable.setPageLength(10);
            membersTable.setWidth("100%");
            addComponent(membersTable);
            refreshMemberSelector();
            memberSelector.setFilteringMode(Select.FILTERINGMODE_CONTAINS);
            memberSelector.setNewItemsAllowed(false);
            memberSelector.setItemCaptionPropertyId("groupName");
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
            setWidth("70%");
            setResizable(false);
        } catch (Exception ex) {
            Logger.getLogger(ProcessACLWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }

    }

    public void refreshMemberSelector() throws Exception {
        memberSelector.removeAllItems();
        memberSelector.setNullSelectionAllowed(false);
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("groupName", String.class, null);
        User user = ((ProcessBase) getApplication()).getCurrent().getUser();
        LdapUtils ldapUtils = new LdapUtils(user.getUid(), null, user.getPassword());
        ArrayList<Group> groups = ldapUtils.getGroups();
        for (Group group : groups) {
            if (!isInTable(group.getDn())) {
                Item woItem = container.addItem(group);
                woItem.getItemProperty("groupName").setValue(group.getCn() + " (" + group.getDn() + ")");
            }
        }
        container.sort(new Object[]{"groupName"}, new boolean[]{true});
        memberSelector.setContainerDataSource(container);
    }

    private boolean isInTable(String groupDn) {
        Collection<PbProcessAcl> tableIds = membersTable.getItemIds();
        for (PbProcessAcl pbProcessAcl : tableIds) {
            if (pbProcessAcl.getGroupDn().equals(groupDn)) {
                return true;
            }
        }
        return false;
    }

    public void refreshTable() {
        try {
            membersTable.removeAllItems();
            membersTable.addContainerProperty("groupDN", String.class, null, messages.getString("tabCaptionACLGroups"), null, null);
            membersTable.addContainerProperty("actions", Button.class, null, messages.getString("tableCaptionActions"), null, null);
            membersTable.setColumnWidth("actions", 50);
            ArrayList<PbProcessAcl> pbProcessAcls = hutil.findPbProcessAcl(processUUID);
            for (PbProcessAcl pbProcessAcl : pbProcessAcls) {
                Item woItem = membersTable.addItem(pbProcessAcl);
                woItem.getItemProperty("groupDN").setValue(pbProcessAcl.getGroupDn());
                woItem.getItemProperty("actions").setValue(new TableExecButton(messages.getString("btnDelete"), "icons/Delete.png", pbProcessAcl, this, Constants.ACTION_DELETE));
            }
            membersTable.setSortContainerPropertyId("groupDN");
            membersTable.setSortAscending(false);
            membersTable.sort();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                String groupDN = ((Group) memberSelector.getValue()).getDn().toString();
                hutil.addPbProcessAcl(processUUID, groupDN);
                refreshTable();
                refreshMemberSelector();
            } else if (event.getButton() instanceof TableExecButton) {
                TableExecButton execBtn = (TableExecButton) event.getButton();
                if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                    String groupDN = ((PbProcessAcl) execBtn.getTableValue()).getGroupDn();
                    hutil.deletePbProcessAcl(processUUID, groupDN);
                    refreshTable();
                    refreshMemberSelector();
                }
            } else {
                close();
            }
        } catch (Exception ex) {
            Logger.getLogger(ProcessACLWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }
}
