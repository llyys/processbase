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
import org.naxitrale.processbase.bpm.WorklistModule;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pbgroup;
import org.naxitrale.processbase.persistence.entity.Pbrole;
import org.naxitrale.processbase.persistence.entity.Pbuser;
import org.naxitrale.processbase.ui.template.TableExecButton;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;

/**
 *
 * @author mgubaidullin
 */
public class RoleProcessesWindow extends Window implements ClickListener {

    private Pbrole role = null;
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button cancelBtn = new Button("Закрыть", this);
    private Button applyBtn = new Button("Добавить", this);
    private Table membersTable = new Table();
    private Label selectLabel = new Label("Кандидаты");
    private Select memberSelector = new Select();
    private HibernateUtil hutil = new HibernateUtil();
    protected WorklistModule worklistModule = new WorklistModule();

    public RoleProcessesWindow(Pbrole role) {
        super();
        this.role = role;
    }

    public void exec() {
        try {
            setCaption("Процессы доступные роли \"" + role.getRolename() + "\"");
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
            getWindow().showNotification("Ошибка", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }

    }

    public void refreshMemberSelector() {
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
            membersTable.addContainerProperty("processID", String.class, null, "ID процесса", null, null);
            membersTable.addContainerProperty("proccessName", String.class, null, "Наименование", null, null);
            membersTable.addContainerProperty("version", String.class, null, "Версия", null, null);
            membersTable.addContainerProperty("operation", Button.class, null, "Операции", null, null);

            Set<String> processes = hutil.getRoleProcesses(role);
            for (String process : processes) {
                ProcessDefinitionUUID pdUUID = new ProcessDefinitionUUID(process);
                ProcessDefinition pd = worklistModule.getProcessDefinition(pdUUID);
                Item woItem = membersTable.addItem(pd);
                woItem.getItemProperty("processID").setValue(pd.getProcessDefinitionUUID());
                woItem.getItemProperty("proccessName").setValue(pd.getName());
                woItem.getItemProperty("version").setValue(pd.getVersion());
                woItem.getItemProperty("operation").setValue(new TableExecButton("Удалить", "icons/Delete.png", pd, this));
            }
            membersTable.setSortContainerPropertyId("processID");
            membersTable.setSortAscending(false);
            membersTable.sort();
        } catch (Exception ex) {
            getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                String pdUUID = ((ProcessDefinition) memberSelector.getValue()).getUUID().toString();
                hutil.addProcessToRole(role, pdUUID);
                refreshTable();
                refreshMemberSelector();
            } else if (event.getButton() instanceof TableExecButton && event.getButton().getDescription().equalsIgnoreCase("Удалить")) {
                String pdUUID = ((ProcessDefinition) ((TableExecButton) event.getButton()).getTableValue()).getUUID().toString();
                hutil.deleteProcessFromRole(role, pdUUID);
                refreshTable();
                refreshMemberSelector();
            } else {
                close();
            }
        } catch (Exception ex) {
            Logger.getLogger(RoleProcessesWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            getWindow().showNotification("Ошибка", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
    }
}
