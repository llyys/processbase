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

import org.processbase.ui.generator.FormGenerator;
import com.vaadin.data.Item;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Logger;
import javax.portlet.PortletSession;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.processbase.ui.template.PbColumnGenerator;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.bpm.forms.XMLProcessDefinition;
import org.processbase.bpm.forms.XMLTaskDefinition;
import org.processbase.core.Constants;
import org.processbase.ui.portlet.PbPortlet;

/**
 *
 * @author mgubaidullin
 */
public class TaskListPanel extends TablePanel implements Button.ClickListener {

    public TaskListPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
        table.addContainerProperty("accepted", ThemeResource.class, null);
        table.setItemIconPropertyId("accepted");
        table.addContainerProperty("processName", Component.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionProcess"), null, null);
        table.addContainerProperty("taskName", Label.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionTask"), null, null);
        table.setColumnExpandRatio("taskName", 1);
        table.addContainerProperty("lastUpdate", Date.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionLastUpdatedDate"), null, null);
        table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
        table.setColumnWidth("lastUpdate", 110);
        table.addContainerProperty("expectedEndDate", Date.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionExpectedEndDate"), null, null);
        table.addGeneratedColumn("expectedEndDate", new PbColumnGenerator());
        table.setColumnWidth("expectedEndDate", 110);
        table.setVisibleColumns(new Object[]{"processName", "taskName", "lastUpdate", "expectedEndDate"});
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            Collection<LightTaskInstance> tasks = PbPortlet.getCurrent().bpmModule.getLightTaskList(ActivityState.READY);
            tasks.addAll(PbPortlet.getCurrent().bpmModule.getLightTaskList(ActivityState.EXECUTING));
            tasks.addAll(PbPortlet.getCurrent().bpmModule.getLightTaskList(ActivityState.SUSPENDED));
//        tasks.addAll(bpmModule.getActivities(ActivityState.INITIAL));
            for (LightTaskInstance task : tasks) {
                addTableRow(task, null);
            }
            this.rowCount = tasks.size();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        table.setSortContainerPropertyId("lastUpdate");
        table.setSortAscending(false);
        table.sort();

    }

    private void addTableRow(LightTaskInstance task, LightTaskInstance previousTask) throws InstanceNotFoundException, Exception {
        Item woItem = previousTask == null ? table.addItem(task) : table.addItemAfter(previousTask, task);

//        woItem.getItemProperty("accepted").setValue(task.isTaskAssigned() ? new ThemeResource("icons/accept.png") : new ThemeResource("icons/email.png"));
        ThemeResource icon = null;
        if (!task.isTaskAssigned()) {
            icon = new ThemeResource("icons/email.png");
        } else if (task.getState().equals(ActivityState.SUSPENDED) && task.getPriority() == 0) {
            icon = new ThemeResource("icons/pause_normal.png");
        } else if (task.getState().equals(ActivityState.SUSPENDED) && task.getPriority() == 1) {
            icon = new ThemeResource("icons/pause_high.png");
        } else if (task.getState().equals(ActivityState.SUSPENDED) && task.getPriority() == 2) {
            icon = new ThemeResource("icons/pause_urgent.png");
        } else if (task.getState().equals(ActivityState.EXECUTING) && task.getPriority() == 0) {
            icon = new ThemeResource("icons/arrow_right_normal.png");
        } else if (task.getState().equals(ActivityState.EXECUTING) && task.getPriority() == 1) {
            icon = new ThemeResource("icons/arrow_right_high.png");
        } else if (task.getState().equals(ActivityState.EXECUTING) && task.getPriority() == 2) {
            icon = new ThemeResource("icons/arrow_right_urgent.png");
        } else if (task.getState().equals(ActivityState.READY) && task.getPriority() == 0) {
            icon = new ThemeResource("icons/arrow_right_normal.png");
        } else if (task.getState().equals(ActivityState.READY) && task.getPriority() == 1) {
            icon = new ThemeResource("icons/arrow_right_high.png");
        } else if (task.getState().equals(ActivityState.READY) && task.getPriority() == 2) {
            icon = new ThemeResource("icons/arrow_right_urgent.png");
        } else {
            icon = new ThemeResource("icons/empty.png");
        }
        woItem.getItemProperty("accepted").setValue(icon);
        LightProcessDefinition lpd = PbPortlet.getCurrent().bpmModule.getLightProcessDefinition(task.getProcessDefinitionUUID());
        String processName = lpd.getLabel() != null ? lpd.getLabel() : lpd.getName();
        TableLinkButton teb = new TableLinkButton(processName + "  #" + task.getProcessInstanceUUID().getInstanceNb(), lpd.getDescription(), null, task, this, Constants.ACTION_OPEN);
        woItem.getItemProperty("processName").setValue(teb);
        String taskTitle = task.getDynamicLabel() != null ? task.getDynamicLabel() : task.getActivityLabel();
        String taskDescription = task.getDynamicDescription() != null ? (" - " + task.getDynamicDescription()) : "";
        woItem.getItemProperty("taskName").setValue(new Label("<b>" + taskTitle + "</b><i>" + taskDescription + "</i>", Label.CONTENT_XHTML));
        woItem.getItemProperty("lastUpdate").setValue(task.getLastUpdateDate());
        woItem.getItemProperty("expectedEndDate").setValue(task.getExpectedEndDate());

    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            try {
                LightTaskInstance task = (LightTaskInstance) ((TableLinkButton) event.getButton()).getTableValue();
                LightTaskInstance newTask = PbPortlet.getCurrent().bpmModule.getTaskInstance(task.getUUID());
                if (newTask == null || newTask.getState().equals(ActivityState.FINISHED) || newTask.getState().equals(ActivityState.ABORTED)) {
                    table.removeItem(task);
                } else {
                    openTaskPage(task);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
            }
        }
    }

    public void openTaskPage(LightTaskInstance task) {
        try {
            String url = PbPortlet.getCurrent().bpmModule.getProcessMetaData(task.getProcessDefinitionUUID()).get(task.getActivityDefinitionUUID().toString());
            if (url != null && !url.isEmpty() && url.length() > 0) {
                PbPortlet.getCurrent().portletSession.removeAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE);
                PbPortlet.getCurrent().portletSession.removeAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE);

                PbPortlet.getCurrent().portletSession.setAttribute("PROCESSBASE_SHARED_TASKINSTANCE", task.getUUID().toString(), PortletSession.APPLICATION_SCOPE);
                this.getWindow().open(new ExternalResource(url));
            } else {
                XMLProcessDefinition xmlProcess = PbPortlet.getCurrent().bpmModule.getXMLProcessDefinition(task.getProcessDefinitionUUID());
                XMLTaskDefinition taskDef = xmlProcess.getTasks().get(task.getActivityName());
                if (taskDef != null && !taskDef.isByPassFormsGeneration() && taskDef.getForms().size() > 0) {
                    FormGenerator fg = new FormGenerator(task, xmlProcess);
                    this.getApplication().getMainWindow().addWindow(fg.getWindow());
                } else if (taskDef != null && taskDef.isByPassFormsGeneration()) {
                    PbPortlet.getCurrent().bpmModule.startTask(task.getUUID(), true);
                    PbPortlet.getCurrent().bpmModule.finishTask(task.getUUID(), true);
                    showImportantInformation(PbPortlet.getCurrent().messages.getString("taskExecuted"));
                } else {
                    showError(PbPortlet.getCurrent().messages.getString("ERROR_UI_NOT_DEFINED"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
}
