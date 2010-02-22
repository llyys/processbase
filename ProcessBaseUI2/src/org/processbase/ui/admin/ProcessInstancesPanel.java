/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import java.util.Date;
import java.util.Set;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.processbase.util.Constants;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.ui.template.PbColumnGenerator;

/**
 *
 * @author mgubaidullin
 */
public class ProcessInstancesPanel extends TablePanel implements Button.ClickListener {

    public ProcessInstancesPanel(PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("UUID", String.class, null, "UUID", null, null);
        table.addContainerProperty("label", String.class, null, messages.getString("tableCaptionProcessName"), null, null);
        table.addContainerProperty("startedDate", Date.class, null, messages.getString("tableCaptionStartedDate"), null, null);
        table.addGeneratedColumn("startedDate", new PbColumnGenerator());
        table.addContainerProperty("endDate", Date.class, null, messages.getString("tableCaptionEndedDate"), null, null);
        table.addGeneratedColumn("endDate", new PbColumnGenerator());
        table.addContainerProperty("initiator", String.class, null, messages.getString("tableCaptionInitiator"), null, null);
        table.addContainerProperty("status", String.class, null, messages.getString("tableCaptionStatus"), null, null);
        table.addContainerProperty("actions", Button.class, null, messages.getString("tableCaptionActions"), null, null);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            Set<LightProcessInstance> pis = bpmModule.getLightProcessInstances();
            for (LightProcessInstance pi : pis) {
                Item woItem = table.addItem(pi);
                woItem.getItemProperty("UUID").setValue(pi.getProcessInstanceUUID());
                ProcessDefinition pd = bpmModule.getProcessDefinition(pi.getProcessDefinitionUUID());
                woItem.getItemProperty("label").setValue(pd.getLabel());
                woItem.getItemProperty("startedDate").setValue(pi.getStartedDate());
                woItem.getItemProperty("endDate").setValue(pi.getEndedDate());
                woItem.getItemProperty("initiator").setValue(pi.getStartedBy());
                woItem.getItemProperty("status").setValue(pi.getInstanceState());
                woItem.getItemProperty("actions").setValue(new TableExecButton(messages.getString("btnDeleteProcessInstance"), "icons/document-delete.png", pi, this, Constants.ACTION_DELETE_PROCESS_INSTANCE));
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            TableExecButton execBtn = (TableExecButton) event.getButton();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE_PROCESS_INSTANCE)) {
                try {
                    LightProcessInstance pi = (LightProcessInstance) execBtn.getTableValue();
                    ProcessInstanceUUID piUUID = pi.getUUID();
                    bpmModule.deleteProcessInstance(piUUID);
                    refreshTable();
                    getWindow().showNotification("", messages.getString("deletedSuccessfull"), Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            }
        }
    }
}
