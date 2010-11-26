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
package org.processbase.ui.worklist;

import com.vaadin.data.Item;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import java.util.Collection;
import java.util.Date;
import java.util.ResourceBundle;
import org.processbase.ui.template.PbColumnGenerator;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.processbase.bpm.BPMModule;

/**
 *
 * @author mgubaidullin
 */
public class TaskArchivePanel extends TablePanel {

    public TaskArchivePanel(PortletApplicationContext2 portletApplicationContext2, BPMModule bpmModule, ResourceBundle messages) {
        super(portletApplicationContext2, bpmModule, messages);
        initTableUI();
    }

    @Override
    public void initTableUI() {
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionTask"), null, null);
        table.addContainerProperty("createdDate", Date.class, null, messages.getString("tableCaptionCreatedDate"), null, null);
        table.addGeneratedColumn("createdDate", new PbColumnGenerator());
        table.addContainerProperty("dueDate", Date.class, null, messages.getString("tableCaptionDueDate"), null, null);
        table.addGeneratedColumn("dueDate", new PbColumnGenerator());
        table.addContainerProperty("endDate", Date.class, null, messages.getString("tableCaptionFinishedDate"), null, null);
        table.addGeneratedColumn("endDate", new PbColumnGenerator());
        table.addContainerProperty("candidates", String.class, null, messages.getString("tableCaptionCandidates"), null, null);
        table.addContainerProperty("endedBy", String.class, null, messages.getString("tableCaptionTaskUser"), null, null);
        table.addContainerProperty("actions", Button.class, null, messages.getString("tableCaptionActions"), null, null);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            Collection<TaskInstance> tasks = bpmModule.getTaskList(ActivityState.FINISHED);
            for (TaskInstance task : tasks) {
                Item woItem = table.addItem(task);
//            woItem.getItemProperty("UUID").setValue(task.getUUID());
                woItem.getItemProperty("name").setValue(task.getActivityLabel());
                woItem.getItemProperty("candidates").setValue(task.getTaskCandidates().toString());
                woItem.getItemProperty("createdDate").setValue(task.getCreatedDate());
                woItem.getItemProperty("dueDate").setValue(task.getExpectedEndDate());
                woItem.getItemProperty("endDate").setValue(task.getEndedDate());
                woItem.getItemProperty("endedBy").setValue(task.getEndedBy());
                woItem.getItemProperty("actions").setValue(startButton(task));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        table.setSortContainerPropertyId("endDate");
        table.setSortAscending(false);
        table.sort();

    }

    private Button startButton(Object tableValue) {
        TableExecButton startB = new TableExecButton(messages.getString("btnInformation"), "icons/document-txt.png", tableValue, this);
        return startB;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
//        TaskInstance task = (TaskInstance) ((TableExecButton) event.getButton()).getTableValue();
//        DefaultTaskWindow defaultTaskWindow = new DefaultTaskWindow(null, task, getPortletApplicationContext2());
//        defaultTaskWindow.exec();
//        defaultTaskWindow.addListener(new Window.CloseListener() {
//
//            public void windowClose(CloseEvent e) {
//                refreshTable();
//            }
//        });
    }
}
