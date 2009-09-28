/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.worklist;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window.CloseListener;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.ui.template.PbColumnGenerator;
import org.naxitrale.processbase.ui.template.TableExecButton;
import org.naxitrale.processbase.ui.template.TableExecButtonBar;
import org.naxitrale.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.naxitrale.processbase.ui.template.TaskWindow;
import org.naxitrale.processbase.Constants;

/**
 *
 * @author mgubaidullin
 */
public class TasksToDoPanel extends TablePanel implements Button.ClickListener {

    public TasksToDoPanel() {
        super();
        initTableUI();
        refreshTable();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("name", Component.class, null, messages.getString("tableCaptionTask"), null, null);
        table.addContainerProperty("createdDate", Date.class, null, messages.getString("tableCaptionCreatedDate"), null, null);
        table.addGeneratedColumn("createdDate", new PbColumnGenerator());
        table.setColumnWidth("createdDate", 100);
        table.addContainerProperty("dueDate", Date.class, null, messages.getString("tableCaptionDueDate"), null, null);
        table.addGeneratedColumn("dueDate", new PbColumnGenerator());
        table.setColumnWidth("dueDate", 100);
        table.addContainerProperty("startedDate", Date.class, null, messages.getString("tableCaptionStartedDate"), null, null);
        table.addGeneratedColumn("startedDate", new PbColumnGenerator());
        table.setColumnWidth("startedDate", 100);
        table.addContainerProperty("candidates", String.class, null, messages.getString("tableCaptionCandidates"), null, null);
//        table.addContainerProperty("startedBy", String.class, null, "Выполняет", null, null);
        table.addContainerProperty("taskUser", String.class, null, messages.getString("tableCaptionTaskUser"), null, null);
        table.addContainerProperty("state", String.class, null, messages.getString("tableCaptionState"), null, null);
        table.setColumnWidth("state", 75);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 75);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        Collection<ActivityInstance<TaskInstance>> tasks = worklistModule.getActivities(ActivityState.READY);
        tasks.addAll(worklistModule.getActivities(ActivityState.EXECUTING));
        tasks.addAll(worklistModule.getActivities(ActivityState.SUSPENDED));
        tasks.addAll(worklistModule.getActivities(ActivityState.INITIAL));
        for (ActivityInstance<TaskInstance> task : tasks) {
            try {
                Item woItem = table.addItem(task);
                String taskName = worklistModule.getProcessActivity(task.getProcessDefinitionUUID(), task.getActivityId()).getDescription();
                String name = taskName != null ? taskName : task.getActivityId();
                woItem.getItemProperty("name").setValue(getTaskLink(name, messages.getString("btnOpen"), task, Constants.ACTION_OPEN));
                woItem.getItemProperty("candidates").setValue(task.getBody().getTaskCandidates().toString());
                woItem.getItemProperty("createdDate").setValue(task.getBody().getCreatedDate());
                woItem.getItemProperty("startedDate").setValue(task.getBody().getStartedDate());
                woItem.getItemProperty("dueDate").setValue(task.getBody().getDueDate());
                woItem.getItemProperty("state").setValue(task.getBody().getState().toString());
//                woItem.getItemProperty("startedBy").setValue(task.getBody().getStartedBy());
                woItem.getItemProperty("taskUser").setValue(task.getBody().isTaskAssigned() ? task.getBody().getTaskUser() : "");
                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(getExecBtn(messages.getString("btnStart"), "icons/Play.png", task, Constants.ACTION_START));
                tebb.addButton(getExecBtn(messages.getString("btnOpen"), "icons/Gear.gif", task, Constants.ACTION_OPEN));
                tebb.addButton(getExecBtn(messages.getString("btnSuspend"), "icons/Pause.png", task, Constants.ACTION_SUSPEND));
                tebb.addButton(getExecBtn(messages.getString("btnResume"), "icons/Play.png", task, Constants.ACTION_RESUME));
                woItem.getItemProperty("actions").setValue(tebb);
            } catch (Exception ex) {
                Logger.getLogger(TasksToDoPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
        table.setSortContainerPropertyId("createdDate");
        table.setSortAscending(true);
        table.sort();

    }

    @Override
    public TableExecButton getExecBtn(String description, String iconName, Object t, String action) {
        TableExecButton execBtn = new TableExecButton(description, iconName, t, this, action);
        execBtn.setEnabled(false);
        ActivityInstance<TaskInstance> ti = (ActivityInstance<TaskInstance>) t;
        if (ti.getBody().getState().equals(ActivityState.READY) && execBtn.getAction().equals(Constants.ACTION_START)) {
            execBtn.setEnabled(true);
        } else if (ti.getBody().getState().equals(ActivityState.EXECUTING) && execBtn.getAction().equals(Constants.ACTION_OPEN)) {
            execBtn.setEnabled(true);
        } else if (ti.getBody().getState().equals(ActivityState.EXECUTING) && execBtn.getAction().equals(Constants.ACTION_SUSPEND)) {
            execBtn.setEnabled(true);
        } else if (ti.getBody().getState().equals(ActivityState.SUSPENDED) && execBtn.getAction().equals(Constants.ACTION_RESUME)) {
            execBtn.setEnabled(true);
        }
        return execBtn;
    }

    public Component getTaskLink(String caption, String description, Object t, String action) {
        ActivityInstance<TaskInstance> ti = (ActivityInstance<TaskInstance>) t;
        if (ti.getBody().getState().equals(ActivityState.EXECUTING)) {
            return new TableExecButton(caption, description, null, t, this, action);
        } else {
            return new Label(caption);
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            try {
                TableExecButton execBtn = (TableExecButton) event.getButton();
                ActivityInstance<TaskInstance> task = (ActivityInstance<TaskInstance>) ((TableExecButton) event.getButton()).getTableValue();
                if (execBtn.getAction().equals(Constants.ACTION_START)) {
                    worklistModule.assignTask(task.getBody().getUUID(), getApplication().getUser().toString());
                    worklistModule.startTask(task.getBody().getUUID(), true);
                } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                    TaskWindow taskWindow = worklistModule.getTaskWindow(task);
                    taskWindow.addListener((CloseListener) this);
                    getApplication().getMainWindow().addWindow(taskWindow);
                } else if (execBtn.getAction().equals(Constants.ACTION_SUSPEND)) {
                    worklistModule.suspendTask(task.getBody().getUUID(), true);
                } else if (execBtn.getAction().equals(Constants.ACTION_RESUME)) {
                    worklistModule.resumeTask(task.getBody().getUUID(), true);
                }
                refreshTable();
            } catch (Exception ex) {
                Logger.getLogger(TasksToDoPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                showError(ex.toString());
            }
        }
    }
}
