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
package org.processbase.ui.bpm.worklist;

import com.vaadin.data.Item;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import java.util.Collection;
import java.util.Date;
import javax.management.InstanceNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.bonita.forms.XMLProcessDefinition;
import org.processbase.ui.core.bonita.forms.XMLTaskDefinition;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.ui.bpm.generator.GeneratedWindow;

/**
 *
 * @author mgubaidullin
 */
public class TaskCompleted extends TablePanel {

    public TaskCompleted() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("processName", Component.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionProcess"), null, null);
        table.addContainerProperty("taskName", Label.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionTask"), null, null);
        table.setColumnExpandRatio("taskName", 1);
        table.addContainerProperty("lastUpdate", Date.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionLastUpdatedDate"), null, null);
        table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
        table.setColumnWidth("lastUpdate", 110);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            Collection<LightTaskInstance> tasks = ((Processbase)getApplication()).getBpmModule().getLightTaskList(ActivityState.FINISHED);
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
        LightProcessDefinition lpd = ((Processbase)getApplication()).getBpmModule().getLightProcessDefinition(task.getProcessDefinitionUUID());
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
                LightTaskInstance newTask = ((Processbase)getApplication()).getBpmModule().getTaskInstance(task.getUUID());
                openTaskPage(task);
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
            }
        }
    }

    public void openTaskPage(LightTaskInstance task) {
        try {
            String url = ((Processbase)getApplication()).getBpmModule().getProcessMetaData(task.getProcessDefinitionUUID()).get(task.getActivityDefinitionUUID().toString());
            ((Processbase)getApplication()).removeSessionAttribute("TASKINSTANCE");
            ((Processbase)getApplication()).setSessionAttribute("TASKINSTANCE", task.getUUID().toString());
            if (url != null && !url.isEmpty() && url.length() > 0) {
                this.getWindow().open(new ExternalResource(url));
            } else {
                XMLProcessDefinition xmlProcess = ((Processbase)getApplication()).getBpmModule().getXMLProcessDefinition(task.getProcessDefinitionUUID());
                XMLTaskDefinition taskDef = xmlProcess.getTasks().get(task.getActivityName());
                if (!taskDef.isByPassFormsGeneration() && taskDef.getForms() == null) {
                    showError(((Processbase)getApplication()).getPbMessages().getString("ERROR_UI_NOT_DEFINED"));
                } else if (!taskDef.isByPassFormsGeneration() && taskDef.getForms().size() > 0) {
                    GeneratedWindow genWindow = new GeneratedWindow(task.getActivityLabel());
                    genWindow.setTask(((Processbase) getApplication()).getBpmModule().getTaskInstance(task.getUUID()));
                    genWindow.setXMLProcess(xmlProcess);
                    this.getApplication().getMainWindow().addWindow(genWindow);
                    genWindow.initUI();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
}
