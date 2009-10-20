/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.worklist;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.ui.template.PbColumnGenerator;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;

/**
 *
 * @author mgubaidullin
 */
public class TasksDonePanel extends TablePanel {

    public TasksDonePanel() {
        super();
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
            Collection<ActivityInstance<TaskInstance>> tasks = worklistModule.getActivities(ActivityState.FINISHED);
            for (ActivityInstance<TaskInstance> task : tasks) {

                Item woItem = table.addItem(task);
//            woItem.getItemProperty("UUID").setValue(task.getUUID());
                String taskName = worklistModule.getProcessActivity(task.getProcessDefinitionUUID(), task.getActivityId()).getDescription();
                woItem.getItemProperty("name").setValue(taskName != null ? taskName : task.getActivityId());
                woItem.getItemProperty("candidates").setValue(task.getBody().getTaskCandidates().toString());
                woItem.getItemProperty("createdDate").setValue(task.getBody().getCreatedDate());
                woItem.getItemProperty("dueDate").setValue(task.getBody().getDueDate());
                woItem.getItemProperty("endDate").setValue(task.getBody().getEndedDate());
                woItem.getItemProperty("endedBy").setValue(task.getBody().getEndedBy());
                woItem.getItemProperty("actions").setValue(startButton(task));
            }
        } catch (Exception ex) {
            Logger.getLogger(TasksToDoPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        table.setSortContainerPropertyId("endDate");
        table.setSortAscending(false);
        table.sort();

    }

    private Button startButton(Object tableValue) {
        TableExecButton startB = new TableExecButton(messages.getString("btnInformation"), "icons/Form.gif", tableValue, new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                ActivityInstance<TaskInstance> task = (ActivityInstance<TaskInstance>) ((TableExecButton) event.getButton()).getTableValue();
                DefaultTaskWindow defaultTaskWindow = new DefaultTaskWindow(null, task);
                defaultTaskWindow.exec();
                defaultTaskWindow.addListener(new Window.CloseListener() {

                    public void windowClose(CloseEvent e) {
                        refreshTable();
                    }
                });

                //getApplication().getMainWindow().addWindow(taskWindow);
            }
        });

        return startB;
    }
}
