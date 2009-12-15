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
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window.CloseListener;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.processbase.ui.template.PbColumnGenerator;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.processbase.ui.template.TaskWindow;
import org.processbase.util.Constants;
import org.processbase.MainWindow;
import org.processbase.ProcessBase;
import org.processbase.bpm.BPMModule;
import org.processbase.util.ProcessBaseClassLoader;
import org.processbase.util.db.HibernateUtil;
import org.processbase.util.db.PbActivityUi;

/**
 *
 * @author mgubaidullin
 */
public class TasksToDoPanel extends TablePanel implements Button.ClickListener {

    protected BPMModule bpmModule = ((ProcessBase) getApplication()).getCurrent().getBpmModule();

    public TasksToDoPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
        table.addContainerProperty("accepted", ThemeResource.class, null);
        table.setItemIconPropertyId("accepted");
        table.addContainerProperty("name", Component.class, null, messages.getString("tableCaptionTask"), null, null);
        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("customID", String.class, null, messages.getString("tableCaptionCustomId"), null, null);
        table.setColumnWidth("customID", 150);
        table.addContainerProperty("createdDate", Date.class, null, messages.getString("tableCaptionCreatedDate"), null, null);
        table.addGeneratedColumn("createdDate", new PbColumnGenerator());
        table.setColumnWidth("createdDate", 100);
//        table.addContainerProperty("dueDate", Date.class, null, messages.getString("tableCaptionDueDate"), null, null);
//        table.addGeneratedColumn("dueDate", new PbColumnGenerator());
//        table.setColumnWidth("dueDate", 100);
        table.addContainerProperty("startedDate", Date.class, null, messages.getString("tableCaptionStartedDate"), null, null);
        table.addGeneratedColumn("startedDate", new PbColumnGenerator());
        table.setColumnWidth("startedDate", 100);
//        table.addContainerProperty("candidates", String.class, null, messages.getString("tableCaptionCandidates"), null, null);
//        table.addContainerProperty("startedBy", String.class, null, "Выполняет", null, null);
//        table.addContainerProperty("taskUser", String.class, null, messages.getString("tableCaptionTaskUser"), null, null);
        table.addContainerProperty("state", String.class, null, messages.getString("tableCaptionState"), null, null);
        table.setColumnWidth("state", 90);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 95);
        table.setVisibleColumns(new Object[]{"name", "customID", "createdDate", "startedDate", "state", "actions"});
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            Collection<ActivityInstance<TaskInstance>> tasks = bpmModule.getActivities(ActivityState.READY);
            tasks.addAll(bpmModule.getActivities(ActivityState.EXECUTING));
            tasks.addAll(bpmModule.getActivities(ActivityState.SUSPENDED));
//        tasks.addAll(bpmModule.getActivities(ActivityState.INITIAL));
            for (ActivityInstance<TaskInstance> task : tasks) {
                Item woItem = table.addItem(task);
                String taskName = bpmModule.getProcessActivity(task.getProcessDefinitionUUID(), task.getActivityId()).getName();
                String name = taskName != null ? taskName : task.getActivityId();
                woItem.getItemProperty("accepted").setValue(task.getBody().isTaskAssigned() ? new ThemeResource("icons/accept.png") : new ThemeResource("icons/empty.png"));
                woItem.getItemProperty("name").setValue(getTaskLink(name, messages.getString("btnOpen"), task, Constants.ACTION_OPEN));
                Map<String, Object> processVars = bpmModule.getProcessInstanceVariables(task.getProcessInstanceUUID(), false);
                woItem.getItemProperty("customID").setValue(processVars.containsKey("customID") ? processVars.get("customID").toString() : "");
//                woItem.getItemProperty("candidates").setValue(task.getBody().getTaskCandidates().toString());
                woItem.getItemProperty("createdDate").setValue(task.getBody().getCreatedDate());
                woItem.getItemProperty("startedDate").setValue(task.getBody().getStartedDate());
//                woItem.getItemProperty("dueDate").setValue(task.getBody().getDueDate());
                woItem.getItemProperty("state").setValue(messages.getString(task.getBody().getState().toString()));
//                woItem.getItemProperty("startedBy").setValue(task.getBody().getStartedBy());
//                woItem.getItemProperty("taskUser").setValue(task.getBody().isTaskAssigned() ? task.getBody().getTaskUser() : "");
                TableExecButtonBar tebb = new TableExecButtonBar();
                if (!task.getBody().isTaskAssigned()) {
                    tebb.addButton(getExecBtn(messages.getString("btnAccept"), "icons/accept.png", task, Constants.ACTION_ACCEPT));
                } else {
                    tebb.addButton(getExecBtn(messages.getString("btnReturn"), "icons/return.png", task, Constants.ACTION_RETURN));
                }
                tebb.addButton(getExecBtn(messages.getString("btnExecute"), "icons/start.png", task, Constants.ACTION_START));
                tebb.addButton(getExecBtn(messages.getString("btnSuspend"), "icons/pause.png", task, Constants.ACTION_SUSPEND));
                tebb.addButton(getExecBtn(messages.getString("btnOpen"), "icons/document-txt.png", task, Constants.ACTION_OPEN));
                tebb.addButton(getExecBtn(messages.getString("btnHelp"), "icons/help.png", task, Constants.ACTION_HELP));
                woItem.getItemProperty("actions").setValue(tebb);
            }
        } catch (Exception ex) {
            Logger.getLogger(TasksToDoPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        table.setSortContainerPropertyId("createdDate");
        table.setSortAscending(false);
        table.sort();

    }

    @Override
    public TableExecButton getExecBtn(String description, String iconName, Object t, String action) {
        TableExecButton execBtn = new TableExecButton(description, iconName, t, this, action);
        execBtn.setEnabled(false);
        ActivityInstance<TaskInstance> task = (ActivityInstance<TaskInstance>) t;
        if (execBtn.getAction().equals(Constants.ACTION_START) && task.getBody().isTaskAssigned() && (task.getBody().getState().equals(ActivityState.READY) || task.getBody().getState().equals(ActivityState.SUSPENDED))) {
            execBtn.setEnabled(true);
        } else if (execBtn.getAction().equals(Constants.ACTION_OPEN) && task.getBody().getState().equals(ActivityState.EXECUTING) && task.getBody().isTaskAssigned()) {
            execBtn.setEnabled(true);
        } else if (execBtn.getAction().equals(Constants.ACTION_ACCEPT) || execBtn.getAction().equals(Constants.ACTION_HELP) || execBtn.getAction().equals(Constants.ACTION_RETURN)) {
            execBtn.setEnabled(true);
        } else if (execBtn.getAction().equals(Constants.ACTION_SUSPEND) && task.getBody().getState().equals(ActivityState.EXECUTING) && task.getBody().isTaskAssigned()) {
            execBtn.setEnabled(true);
        }
        return execBtn;
    }

    public Component getTaskLink(String caption, String description, Object t, String action) {
        ActivityInstance<TaskInstance> ti = (ActivityInstance<TaskInstance>) t;
        if (ti.getBody().isTaskAssigned() && ti.getBody().getState().equals(ActivityState.EXECUTING)) {
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
                if (execBtn.getAction().equals(Constants.ACTION_ACCEPT)) {
                    bpmModule.assignTask(task.getBody().getUUID(), ((ProcessBase) getApplication()).getUser().getUid());
                    refreshTable();
//                    table.setImmediate(true);
//                    table.getContainerDataSource().getItem(task).getItemProperty("accepted").setValue(new ThemeResource("icons/accept.png"));
//                    showWarning("" + table.getContainerDataSource().getItem(task));
                } else if (execBtn.getAction().equals(Constants.ACTION_RETURN)) {
                    bpmModule.unassignTask(task.getBody().getUUID());
                    refreshTable();
                } else if (execBtn.getAction().equals(Constants.ACTION_START) && task.getBody().getState().equals(ActivityState.READY)) {
                    bpmModule.startTask(task.getBody().getUUID(), true);
                    TaskWindow taskWindow = getTaskWindow(task);
                    taskWindow.addListener((CloseListener) this);
                    getApplication().getMainWindow().addWindow(taskWindow);
                    refreshTable();
                } else if (execBtn.getAction().equals(Constants.ACTION_START) && task.getBody().getState().equals(ActivityState.SUSPENDED)) {
                    bpmModule.resumeTask(task.getBody().getUUID(), true);
                    TaskWindow taskWindow = getTaskWindow(task);
                    taskWindow.addListener((CloseListener) this);
                    getApplication().getMainWindow().addWindow(taskWindow);
                    refreshTable();
                } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                    TaskWindow taskWindow = getTaskWindow(task);
                    taskWindow.addListener((CloseListener) this);
                    getApplication().getMainWindow().addWindow(taskWindow);
                } else if (execBtn.getAction().equals(Constants.ACTION_SUSPEND)) {
                    bpmModule.suspendTask(task.getBody().getUUID(), true);
                    refreshTable();
                } else if (execBtn.getAction().equals(Constants.ACTION_HELP)) {
                    String activityDefinitionUUID = bpmModule.getProcessActivity(task.getProcessDefinitionUUID(), task.getActivityId()).getUUID().toString();
                    ((MainWindow) getWindow()).getWorkPanel().getHelpPanel().setHelp(activityDefinitionUUID);
                }
            } catch (Exception ex) {
                Logger.getLogger(TasksToDoPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                showError(ex.toString());
            }
        }
    }

    public TaskWindow getTaskWindow(ActivityInstance<TaskInstance> task) {
        ProcessDefinition procd = null;
        try {
            procd = bpmModule.getProcessDefinition(task.getProcessDefinitionUUID());
            HibernateUtil hutil = new HibernateUtil();
            PbActivityUi pbActivityUi = hutil.findPbActivityUi(bpmModule.getTaskDefinition(task).getUUID().toString());
            Class b = ProcessBaseClassLoader.getCurrent().loadClass(pbActivityUi.getUiClass());
            TaskWindow taskWindow = (TaskWindow) b.newInstance();
            taskWindow.setTaskInfo(procd, task);
            taskWindow.exec();
            return taskWindow;
        } catch (Exception ex) {
            Logger.getLogger(TasksToDoPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            DefaultTaskWindow defaultTaskWindow = new DefaultTaskWindow(procd, task);
            defaultTaskWindow.exec();
            return defaultTaskWindow;
        }
    }
}
