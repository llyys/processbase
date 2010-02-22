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
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import javax.portlet.PortletSession;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.processbase.ui.template.PbColumnGenerator;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.processbase.util.Constants;
import org.processbase.util.db.HibernateUtil;
import org.processbase.util.db.PbActivityUi;

/**
 *
 * @author mgubaidullin
 */
public class TaskListPanel extends TablePanel implements Button.ClickListener {

    public TaskListPanel(PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
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
            Collection<TaskInstance> tasks = bpmModule.getTaskList(ActivityState.READY);
            tasks.addAll(bpmModule.getTaskList(ActivityState.EXECUTING));
            tasks.addAll(bpmModule.getTaskList(ActivityState.SUSPENDED));
//        tasks.addAll(bpmModule.getActivities(ActivityState.INITIAL));
            for (TaskInstance task : tasks) {
                addTableRow(task, null);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        table.setSortContainerPropertyId("createdDate");
        table.setSortAscending(false);
        table.sort();

    }

    private void addTableRow(TaskInstance task, TaskInstance previousTask) throws InstanceNotFoundException, Exception {
        Item woItem = previousTask == null ? table.addItem(task) : table.addItemAfter(previousTask, task);
        woItem.getItemProperty("accepted").setValue(task.isTaskAssigned() ? new ThemeResource("icons/accept.png") : new ThemeResource("icons/empty.png"));
        woItem.getItemProperty("name").setValue(getTaskLink(task.getActivityLabel(),task.getActivityDescription(), task, Constants.ACTION_OPEN));
        woItem.getItemProperty("customID").setValue(bpmModule.getProcessInstanceVariable(task.getProcessInstanceUUID(), "customID"));
//                woItem.getItemProperty("candidates").setValue(task.getBody().getTaskCandidates().toString());
        woItem.getItemProperty("createdDate").setValue(task.getCreatedDate());
        woItem.getItemProperty("startedDate").setValue(task.getStartedDate());
//                woItem.getItemProperty("dueDate").setValue(task.getBody().getDueDate());
        woItem.getItemProperty("state").setValue(messages.getString(task.getState().toString()));
//                woItem.getItemProperty("startedBy").setValue(task.getBody().getStartedBy());
//                woItem.getItemProperty("taskUser").setValue(task.getBody().isTaskAssigned() ? task.getBody().getTaskUser() : "");
        TableExecButtonBar tebb = new TableExecButtonBar();
        if (!task.isTaskAssigned()) {
            tebb.addButton(getExecBtn(messages.getString("btnAccept"), "icons/accept.png", task, Constants.ACTION_ACCEPT));
        } else {
            tebb.addButton(getExecBtn(messages.getString("btnReturn"), "icons/return.png", task, Constants.ACTION_RETURN));
        }
        tebb.addButton(getExecBtn(messages.getString("btnExecute"), "icons/start.png", task, Constants.ACTION_START));
        tebb.addButton(getExecBtn(messages.getString("btnSuspend"), "icons/pause.png", task, Constants.ACTION_SUSPEND));
        tebb.addButton(getExecBtn(messages.getString("btnOpen"), "icons/document-txt.png", task, Constants.ACTION_OPEN));
        woItem.getItemProperty("actions").setValue(tebb);
    }

    @Override
    public TableExecButton getExecBtn(String description, String iconName, Object t, String action) {
        TableExecButton execBtn = new TableExecButton(description, iconName, t, this, action);
        execBtn.setEnabled(false);
        TaskInstance task = (TaskInstance) t;
        if (execBtn.getAction().equals(Constants.ACTION_START) && task.isTaskAssigned() && (task.getState().equals(ActivityState.READY) || task.getState().equals(ActivityState.SUSPENDED))) {
            execBtn.setEnabled(true);
        } else if (execBtn.getAction().equals(Constants.ACTION_OPEN) && task.getState().equals(ActivityState.EXECUTING) && task.isTaskAssigned()) {
            execBtn.setEnabled(true);
        } else if (execBtn.getAction().equals(Constants.ACTION_ACCEPT) || execBtn.getAction().equals(Constants.ACTION_RETURN)) {
            execBtn.setEnabled(true);
        } else if (execBtn.getAction().equals(Constants.ACTION_SUSPEND) && task.getState().equals(ActivityState.EXECUTING) && task.isTaskAssigned()) {
            execBtn.setEnabled(true);
        }
        return execBtn;
    }

    public Component getTaskLink(String caption, String description, Object t, String action) {
        TaskInstance ti = (TaskInstance) t;
        if (ti.isTaskAssigned() &&
                (ti.getState().equals(ActivityState.EXECUTING) || ti.getState().equals(ActivityState.READY))) {
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
                TaskInstance task = (TaskInstance) ((TableExecButton) event.getButton()).getTableValue();
                if (execBtn.getAction().equals(Constants.ACTION_ACCEPT)) {
                    TaskInstance newTask = bpmModule.assignTask(task.getUUID(), this.getCurrentUser().getScreenName());
                    if (newTask != null) {
                        addTableRow(newTask, task);
                    }
                    table.removeItem(task);
                } else if (execBtn.getAction().equals(Constants.ACTION_RETURN)) {
                    bpmModule.unassignTask(task.getUUID());
                    refreshTable();
                } else if (execBtn.getAction().equals(Constants.ACTION_START) && task.getState().equals(ActivityState.READY)) {
                    TaskInstance newTask = bpmModule.startTask(task.getUUID(), true);
                    if (newTask != null) {
                        addTableRow(newTask, task);
                    }
                    table.removeItem(task);
                    openTaskPage(newTask);
                } else if (execBtn.getAction().equals(Constants.ACTION_START) && task.getState().equals(ActivityState.SUSPENDED)) {
                    bpmModule.resumeTask(task.getUUID(), true);
                    openTaskPage(task);
                } else if (execBtn.getAction().equals(Constants.ACTION_OPEN) && task.getState().equals(ActivityState.READY)) {
                    TaskInstance newTask = bpmModule.startTask(task.getUUID(), true);
                    if (newTask != null) {
                        addTableRow(newTask, task);
                    }
                    table.removeItem(task);
                    openTaskPage(newTask);
                } else if (execBtn.getAction().equals(Constants.ACTION_OPEN) && !task.getState().equals(ActivityState.READY)) {
                    openTaskPage(task);
                } else if (execBtn.getAction().equals(Constants.ACTION_SUSPEND)) {
                    bpmModule.suspendTask(task.getUUID(), true);
                    refreshTable();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
            }
        }
    }

    public void openTaskPage(TaskInstance task) {
        try {
            HibernateUtil hutil = new HibernateUtil();
            PbActivityUi pbActivityUi = hutil.findPbActivityUi(bpmModule.getTaskDefinition(task).getUUID().toString());
            getPortletApplicationContext2().getPortletSession().removeAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE);
            getPortletApplicationContext2().getPortletSession().setAttribute("PROCESSBASE_SHARED_TASKINSTANCE", task.getUUID().toString(), PortletSession.APPLICATION_SCOPE);
            if (pbActivityUi.getUiClass() != null && !pbActivityUi.getUiClass().isEmpty()) {
                this.getWindow().open(new ExternalResource(pbActivityUi.getUiClass()));
            } else {
                this.getWindow().open(new ExternalResource(Constants.TASKDEFAULT_PAGE_URL));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
}
