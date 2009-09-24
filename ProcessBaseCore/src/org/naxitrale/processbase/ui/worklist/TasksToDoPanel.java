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
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.CloseListener;
import com.vaadin.ui.Window.Notification;
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
        table.addContainerProperty("name", Component.class, null, "Задача", null, null);
        table.addContainerProperty("createdDate", Date.class, null, "Создано", null, null);
        table.addGeneratedColumn("createdDate", new PbColumnGenerator());
        table.addContainerProperty("dueDate", Date.class, null, "Срок", null, null);
        table.addGeneratedColumn("dueDate", new PbColumnGenerator());
        table.addContainerProperty("startedDate", Date.class, null, "Начато", null, null);
        table.addGeneratedColumn("startedDate", new PbColumnGenerator());
        table.addContainerProperty("candidates", String.class, null, "Исполнители", null, null);
//        table.addContainerProperty("startedBy", String.class, null, "Выполняет", null, null);
        table.addContainerProperty("taskUser", String.class, null, "Выполняет", null, null);
        table.addContainerProperty("state", String.class, null, "Статус", null, null);
        table.addContainerProperty("operation", TableExecButtonBar.class, null, "Операции", null, null);
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
                woItem.getItemProperty("name").setValue(getTaskLink(name, "Открыть", task));
                woItem.getItemProperty("candidates").setValue(task.getBody().getTaskCandidates().toString());
                woItem.getItemProperty("createdDate").setValue(task.getBody().getCreatedDate());
                woItem.getItemProperty("startedDate").setValue(task.getBody().getStartedDate());
                woItem.getItemProperty("dueDate").setValue(task.getBody().getDueDate());
                woItem.getItemProperty("state").setValue(task.getBody().getState().toString());
//                woItem.getItemProperty("startedBy").setValue(task.getBody().getStartedBy());
                woItem.getItemProperty("taskUser").setValue(task.getBody().isTaskAssigned() ? task.getBody().getTaskUser() : "");
                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(getExecBtn("Начать", "icons/Play.png", task));
                tebb.addButton(getExecBtn("Открыть", "icons/Gear.gif", task));
                tebb.addButton(getExecBtn("Приостановить", "icons/Pause.png", task));
                tebb.addButton(getExecBtn("Продолжить", "icons/Play.png", task));
                woItem.getItemProperty("operation").setValue(tebb);
            } catch (Exception ex) {
                Logger.getLogger(TasksToDoPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            }
        }
        table.setSortContainerPropertyId("createdDate");
        table.setSortAscending(true);
        table.sort();

    }

    public TableExecButton getExecBtn(String description, String iconName, Object t) {
        TableExecButton startBtn = new TableExecButton(description, iconName, t, this);
        startBtn.setEnabled(false);
        ActivityInstance<TaskInstance> ti = (ActivityInstance<TaskInstance>) t;
        if (ti.getBody().getState().equals(ActivityState.READY) && startBtn.getDescription().equalsIgnoreCase("Начать")) {
            startBtn.setEnabled(true);
        } else if (ti.getBody().getState().equals(ActivityState.EXECUTING) && startBtn.getDescription().equalsIgnoreCase("Открыть")) {
            startBtn.setEnabled(true);
        } else if (ti.getBody().getState().equals(ActivityState.EXECUTING) && startBtn.getDescription().equalsIgnoreCase("Приостановить")) {
            startBtn.setEnabled(true);
        } else if (ti.getBody().getState().equals(ActivityState.SUSPENDED) && startBtn.getDescription().equalsIgnoreCase("Продолжить")) {
            startBtn.setEnabled(true);
        }
        return startBtn;
    }

    public Component getTaskLink(String caption, String description, Object t) {
        ActivityInstance<TaskInstance> ti = (ActivityInstance<TaskInstance>) t;
        if (ti.getBody().getState().equals(ActivityState.EXECUTING)) {
            return new TableExecButton(caption, description, null, t, this);
        } else {
            return new Label(caption);
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            try {
                ActivityInstance<TaskInstance> task = (ActivityInstance<TaskInstance>) ((TableExecButton) event.getButton()).getTableValue();
                if (event.getButton().getDescription().equalsIgnoreCase("Начать")) {
                    worklistModule.assignTask(task.getBody().getUUID(), getApplication().getUser().toString());
                    worklistModule.startTask(task.getBody().getUUID(), true);
                } else if (event.getButton().getDescription().equalsIgnoreCase("Открыть")) {
                    TaskWindow taskWindow = worklistModule.getTaskWindow(task);
                    taskWindow.addListener((CloseListener) this);
                    getApplication().getMainWindow().addWindow(taskWindow);
                } else if (event.getButton().getDescription().equalsIgnoreCase("Приостановить")) {
                    worklistModule.suspendTask(task.getBody().getUUID(), true);
                } else if (event.getButton().getDescription().equalsIgnoreCase("Продолжить")) {
                    worklistModule.resumeTask(task.getBody().getUUID(), true);
                }
                refreshTable();
            } catch (Exception ex) {
                Logger.getLogger(TasksToDoPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
            }
        }
    }
}
