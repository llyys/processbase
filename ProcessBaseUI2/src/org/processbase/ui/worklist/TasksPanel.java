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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import java.util.Date;
import java.util.Set;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.uuid.ActivityDefinitionUUID;
import org.processbase.ui.template.PbColumnGenerator;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.processbase.ui.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class TasksPanel extends TablePanel {

    private ProcessInstanceUUID processInstanceUUID;
    private ProcessDefinition processDefinition;
    private Set<ActivityInstance> activities;
    private Button closeBtn;

    public TasksPanel(PortletApplicationContext2 portletApplicationContext2, ProcessInstanceUUID processInstanceUUID) {
        super(portletApplicationContext2);
        try {
            this.processInstanceUUID = processInstanceUUID;
            this.processDefinition = bpmModule.getProcessDefinition(processInstanceUUID.getProcessDefinitionUUID());
            this.activities = bpmModule.getActivityInstances(processInstanceUUID);
            initTableUI();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.removeComponent(this.buttonBar);
        this.addComponent(this.buttonBar);
        this.buttonBar.removeAllComponents();
        closeBtn = new Button(messages.getString("btnClose"), this);
        this.buttonBar.addButton(closeBtn);
        this.buttonBar.setWidth("100%");
        this.buttonBar.setMargin(true, true, true, false);
        this.buttonBar.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
    }

    @Override
    public void initTableUI() {
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionTask"), null, null);
        table.addContainerProperty("type", String.class, null, messages.getString("tableCaptionTask"), null, null);
        table.addContainerProperty("createdDate", Date.class, null, messages.getString("tableCaptionCreatedDate"), null, null);
        table.addGeneratedColumn("createdDate", new PbColumnGenerator());
        table.setColumnWidth("createdDate", 100);
        table.addContainerProperty("startdeDate", Date.class, null, messages.getString("tableCaptionStartedDate"), null, null);
        table.addGeneratedColumn("startdeDate", new PbColumnGenerator());
        table.setColumnWidth("startdeDate", 100);
        table.addContainerProperty("endDate", Date.class, null, messages.getString("tableCaptionFinishedDate"), null, null);
        table.addGeneratedColumn("endDate", new PbColumnGenerator());
        table.setColumnWidth("endDate", 100);
        table.addContainerProperty("candidates", String.class, null, messages.getString("tableCaptionCandidates"), null, null);
        table.addContainerProperty("taskuser", String.class, null, messages.getString("tableCaptionTaskUser"), null, null);
        table.addContainerProperty("state", String.class, null, messages.getString("tableCaptionState"), null, null);
        table.setColumnWidth("state", 90);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            for (ActivityDefinition ad : processDefinition.getActivities()) {
                if (ad.getType().equals(ActivityDefinition.Type.Automatic) || ad.getType().equals(ActivityDefinition.Type.Human)) {
                    Item woItem = table.addItem(ad);
                    woItem.getItemProperty("name").setValue(ad.getLabel());
                    woItem.getItemProperty("type").setValue(ad.isTask() ? messages.getString("task") : messages.getString("automatic"));
                    ActivityInstance ai = getActivity(ad.getUUID());
                    if (ai != null) {
                        woItem.getItemProperty("createdDate").setValue(ai.getReadyDate());
                        woItem.getItemProperty("startdeDate").setValue(ai.getStartedDate());
                        woItem.getItemProperty("endDate").setValue(ai.getEndedDate());
                        woItem.getItemProperty("state").setValue(messages.getString(ai.getState().toString()));
                        if (ai.isTask()) {
                            woItem.getItemProperty("candidates").setValue(ai.getTask().getTaskCandidates());
                            woItem.getItemProperty("taskuser").setValue(ai.getTask().getTaskUser());
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        table.setSortContainerPropertyId("createdDate");
        table.setSortAscending(false);
        table.sort();
    }

    private ActivityInstance getActivity(ActivityDefinitionUUID activityDefinitionUUID) {
        for (ActivityInstance act : activities) {
            if (act.getActivityDefinitionUUID().equals(activityDefinitionUUID)) {
                return act;
            }
        }
        return null;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton().equals(closeBtn)) {
            ((PbWindow) this.getWindow()).close();
        }
    }

    public ProcessDefinition getProcessDefinition() {
        return processDefinition;
    }
}
