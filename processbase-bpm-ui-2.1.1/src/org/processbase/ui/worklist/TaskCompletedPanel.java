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
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import java.util.Collection;
import java.util.Date;
import javax.management.InstanceNotFoundException;
import javax.portlet.PortletSession;
import org.processbase.ui.template.PbColumnGenerator;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.bpm.forms.XMLProcessDefinition;
import org.processbase.bpm.forms.XMLTaskDefinition;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.core.Constants;
import org.processbase.ui.generator.FormGenerator;

/**
 *
 * @author mgubaidullin
 */
public class TaskCompletedPanel extends TablePanel {

    public TaskCompletedPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        table.addContainerProperty("processName", Component.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionProcess"), null, null);
        table.addContainerProperty("taskName", Label.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionTask"), null, null);
        table.setColumnExpandRatio("taskName", 1);
        table.addContainerProperty("lastUpdate", Date.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionLastUpdatedDate"), null, null);
        table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
        table.setColumnWidth("lastUpdate", 100);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            Collection<LightTaskInstance> tasks = PbPortlet.getCurrent().bpmModule.getLightTaskList(ActivityState.FINISHED);
            for (LightTaskInstance task : tasks) {
                addTableRow(task);
            }
            this.rowCount = tasks.size();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        table.setSortContainerPropertyId("lastUpdate");
        table.setSortAscending(false);
        table.sort();

    }

    private void addTableRow(LightTaskInstance task) throws InstanceNotFoundException, Exception {
        Item woItem = table.addItem(task);
        LightProcessDefinition lpd = PbPortlet.getCurrent().bpmModule.getLightProcessDefinition(task.getProcessDefinitionUUID());
        String processName = lpd.getLabel() != null ? lpd.getLabel() : lpd.getName();
        String processInstanceUUID = task.getProcessInstanceUUID().toString();
        TableLinkButton teb = new TableLinkButton(processName + "  #" + processInstanceUUID.substring(processInstanceUUID.lastIndexOf("--") + 2), lpd.getDescription(), null, task, this, Constants.ACTION_OPEN);
        woItem.getItemProperty("processName").setValue(teb);
        String taskTitle = task.getDynamicLabel() != null ? task.getDynamicLabel() : task.getActivityLabel();
        String taskDescription = task.getDynamicDescription() != null ? (" - " + task.getDynamicDescription()) : "";
        woItem.getItemProperty("taskName").setValue(new Label("<b>" + taskTitle + "</b><i>" + taskDescription + "</i>", Label.CONTENT_XHTML));
        woItem.getItemProperty("lastUpdate").setValue(task.getLastUpdateDate());

    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            try {
                LightTaskInstance task = (LightTaskInstance) ((TableLinkButton) event.getButton()).getTableValue();
                LightTaskInstance newTask = PbPortlet.getCurrent().bpmModule.getTaskInstance(task.getUUID());
                openTaskPage(task);
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
            }
        }
    }

    public void openTaskPage(LightTaskInstance task) {
        try {
            String url = PbPortlet.getCurrent().bpmModule.getProcessMetaData(task.getProcessDefinitionUUID()).get(task.getActivityDefinitionUUID().toString());
            PbPortlet.getCurrent().portletSession.removeAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE);
            PbPortlet.getCurrent().portletSession.setAttribute("PROCESSBASE_SHARED_TASKINSTANCE", task.getUUID().toString(), PortletSession.APPLICATION_SCOPE);
            if (url != null && !url.isEmpty() && url.length() > 0) {
                this.getWindow().open(new ExternalResource(url));
            } else {
                XMLProcessDefinition xmlProcess = PbPortlet.getCurrent().bpmModule.getXMLProcessDefinition(task.getProcessDefinitionUUID());
                XMLTaskDefinition taskDef = xmlProcess.getTasks().get(task.getActivityName());
                if (!taskDef.isByPassFormsGeneration() && taskDef.getForms() == null) {
                    showError(PbPortlet.getCurrent().messages.getString("ERROR_UI_NOT_DEFINED"));
                } else if (!taskDef.isByPassFormsGeneration() && taskDef.getForms().size() > 0) {
                    FormGenerator fg = new FormGenerator(task, xmlProcess);
                    this.getApplication().getMainWindow().addWindow(fg.getWindow());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
}
