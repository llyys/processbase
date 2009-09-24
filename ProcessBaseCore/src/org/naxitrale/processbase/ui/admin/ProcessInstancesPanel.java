/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window.Notification;
import java.util.Date;
import java.util.Set;
import org.naxitrale.processbase.ui.template.TableExecButton;
import org.naxitrale.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 *
 * @author mgubaidullin
 */
public class ProcessInstancesPanel extends TablePanel {


    public ProcessInstancesPanel() {
        super();
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            table.addContainerProperty("UUID", String.class, null, "UUID", null, null);
            table.addContainerProperty("name", String.class, null, "Имя процесса", null, null);
            table.addContainerProperty("startedDate", Date.class, null, "Дата начала", null, null);
            table.addContainerProperty("endDate", Date.class, null, "Дата завершения", null, null);
            table.addContainerProperty("initiator", String.class, null, "Инициатор", null, null);
            table.addContainerProperty("status", String.class, null, "Статус", null, null);
            table.addContainerProperty("operation", Button.class, null, "Операции", null, null);

            Set<ProcessInstance> pis = adminModule.getProcessInstances();
            for (ProcessInstance pi : pis) {
                Item woItem = table.addItem(pi);
                woItem.getItemProperty("UUID").setValue(pi.getPackageDefinitionUUID());
                woItem.getItemProperty("name").setValue(adminModule.getProcessDefinition(pi.getProcessDefinitionUUID()).getProcessId());
                woItem.getItemProperty("startedDate").setValue(pi.getStartedDate());
                woItem.getItemProperty("endDate").setValue(pi.getEndedDate());
                woItem.getItemProperty("initiator").setValue(pi.getStartedBy());
                woItem.getItemProperty("status").setValue(pi.getInstanceState());
                woItem.getItemProperty("operation").setValue(deleteProcess(pi));
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
        }

    }

    private Button deleteProcess(Object tableValue) {
        TableExecButton startB = new TableExecButton("Удалить экземпляр процесса", "icons/Delete.png", tableValue, new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                try {
                    ProcessInstance pi = (ProcessInstance) ((TableExecButton) event.getButton()).getTableValue();
                    ProcessInstanceUUID piUUID = pi.getUUID();
                    adminModule.deleteProcessInstance(piUUID);
                    refreshTable();
                    getWindow().showNotification("Внимание", "Удаление завершено успешно!", Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception ex) {
                    getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        return startB;
    }
}
