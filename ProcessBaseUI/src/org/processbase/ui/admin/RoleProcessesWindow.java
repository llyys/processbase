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
import com.vaadin.ui.Window;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.Constants;
import org.processbase.bpm.WorklistModule;
import org.processbase.acl.persistence.HibernateUtil;
import org.processbase.acl.persistence.Pbgroup;
import org.processbase.acl.persistence.Pbrole;
import org.processbase.acl.persistence.Pbuser;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.TableExecButton;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.processbase.ProcessBase;

/**
 *
 * @author mgubaidullin
 */
public class RoleProcessesWindow extends PbWindow implements ClickListener {

    private Pbrole role = null;
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button cancelBtn = new Button(messages.getString("btnClose"), this);
    private Button applyBtn = new Button(messages.getString("btnAdd"), this);
    private Table membersTable = new Table();
    private Label selectLabel = new Label(messages.getString("candidates"));
    private Select memberSelector = new Select();
    private HibernateUtil hutil = new HibernateUtil();
    protected WorklistModule worklistModule = ((ProcessBase)getApplication()).getCurrent().getWorklistModule();

    public RoleProcessesWindow(Pbrole role) {
        super();
        this.role = role;
    }

    public void exec() {
        try {
            setCaption(messages.getString("captionAvailableProcesses") + " \"" + role.getRolename() + "\"");
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
            memberSelector.setItemCaptionPropertyId("processID");
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
            Logger.getLogger(RoleProcessesWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }

    }

    public void refreshMemberSelector() throws Exception {
        memberSelector.removeAllItems();
        memberSelector.setValue(null);
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("processID", String.class, null);

        Set<ProcessDefinition> pds = worklistModule.getProcessDefinitions();
        for (ProcessDefinition pd : pds) {
            Item item = container.addItem(pd);
            item.getItemProperty("processID").setValue(pd.getName() + " " + pd.getVersion());
        }
        container.sort(new Object[]{"processID"}, new boolean[]{true});
        memberSelector.setContainerDataSource(container);
    }

    public void refreshTable() {
        try {
            membersTable.removeAllItems();
            membersTable.addContainerProperty("processID", String.class, null, messages.getString("tableCaptionProcessID"), null, null);
            membersTable.addContainerProperty("proccessName", String.class, null, messages.getString("tableCaptionProcessName"), null, null);
            membersTable.addContainerProperty("version", String.class, null, messages.getString("tableCaptionVersion"), null, null);
            membersTable.addContainerProperty("actions", Button.class, null, messages.getString("tableCaptionActions"), null, null);

            Set<String> processes = hutil.getRoleProcesses(role);
            for (String process : processes) {
                ProcessDefinitionUUID pdUUID = new ProcessDefinitionUUID(process);
                ProcessDefinition pd = worklistModule.getProcessDefinition(pdUUID);
                Item woItem = membersTable.addItem(pd);
                woItem.getItemProperty("processID").setValue(pd.getProcessDefinitionUUID());
                woItem.getItemProperty("proccessName").setValue(pd.getName());
                woItem.getItemProperty("version").setValue(pd.getVersion());
                woItem.getItemProperty("actions").setValue(new TableExecButton(messages.getString("btnDelete"), "icons/Delete.png", pd, this, Constants.ACTION_DELETE));
            }
            membersTable.setSortContainerPropertyId("processID");
            membersTable.setSortAscending(false);
            membersTable.sort();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                String pdUUID = ((ProcessDefinition) memberSelector.getValue()).getUUID().toString();
                hutil.addProcessToRole(role, pdUUID);
                refreshTable();
                refreshMemberSelector();
            } else if (event.getButton() instanceof TableExecButton) {
                TableExecButton execBtn = (TableExecButton) event.getButton();
                if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                    String pdUUID = ((ProcessDefinition) execBtn.getTableValue()).getUUID().toString();
                    hutil.deleteProcessFromRole(role, pdUUID);
                    refreshTable();
                    refreshMemberSelector();
                }
            } else {
                close();
            }
        } catch (Exception ex) {
            Logger.getLogger(RoleProcessesWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }
}
