/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.PackageNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.processbase.ProcessBase;
import org.processbase.bpm.BPMModule;
import org.processbase.util.Constants;

/**
 *
 * @author mgubaidullin
 */
public class ActivityInstancesPanel extends TablePanel implements Button.ClickListener {

    protected BPMModule bpmModule = new BPMModule(ProcessBase.getCurrent().getUser().getUid());

    public ActivityInstancesPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("UUID", String.class, null, "UUID", null, null);
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionActivityName"), null, null);
        table.addContainerProperty("type", String.class, null, messages.getString("tableCaptionType"), null, null);
//            table.addContainerProperty("performer", String.class, null, "Исполнитель", null, null);
        table.addContainerProperty("readyDate", Date.class, null, messages.getString("tableCaptionCreatedDate"), null, null);
        table.addContainerProperty("startedDate", Date.class, null, messages.getString("tableCaptionStartedDate"), null, null);
        table.addContainerProperty("endDate", Date.class, null, messages.getString("tableCaptionFinishedDate"), null, null);
//            table.addContainerProperty("iteration", String.class, null, "Итерация", null, null);
        table.addContainerProperty("state", String.class, null, messages.getString("tableCaptionState"), null, null);
        table.addContainerProperty("actions", TableExecButton.class, null, messages.getString("tableCaptionActions"), null, null);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            Set<ActivityInstance> ais = bpmModule.getActivityInstances();
            for (ActivityInstance ai : ais) {
                Item woItem = table.addItem(ai);
                woItem.getItemProperty("UUID").setValue(ai.getUUID().toString());
                woItem.getItemProperty("name").setValue(ai.getActivityId());
                woItem.getItemProperty("readyDate").setValue(ai.getBody().getReadyDate());
                woItem.getItemProperty("startedDate").setValue(ai.getBody().getStartedDate());
                woItem.getItemProperty("endDate").setValue(ai.getBody().getEndedDate());
                woItem.getItemProperty("state").setValue(ai.getBody().getState());
                ActivityDefinition activityDefinition = bpmModule.getProcessActivityDefinition(ai);
                woItem.getItemProperty("type").setValue(activityDefinition.getPerformer() == null ? messages.getString("automatic") : messages.getString("task"));
                if (!activityDefinition.isRoute()) {
                    woItem.getItemProperty("actions").setValue(new TableExecButton(messages.getString("btnOpen"), "icons/document-txt.png", ai, this, Constants.ACTION_OPEN));
                }
            }
            table.setSortContainerPropertyId("readyDate");
            table.setSortAscending(false);
            table.sort();
            table.setColumnWidth("UUID", 30);
        } catch (Exception ex) {
            showError(ex.toString());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            try {
                ActivityInstance<ActivityBody> activity = (ActivityInstance<ActivityBody>) ((TableExecButton) event.getButton()).getTableValue();
                ActivityInstance<TaskInstance> task = null;
                if (bpmModule.getProcessActivityDefinition(activity).getPerformer() != null) {
                    task = (ActivityInstance<TaskInstance>) ((TableExecButton) event.getButton()).getTableValue();
                }
                ActivityWindow activityWindow = getActivityWindow(activity, task);
//                activityWindow.addListener((Window.CloseListener) this);
                getApplication().getMainWindow().addWindow(activityWindow);
            } catch (Exception ex) {
                Logger.getLogger(ActivityInstancesPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                showError(ex.toString());
            }
        }
    }

    public ActivityWindow getActivityWindow(ActivityInstance<ActivityBody> activity, ActivityInstance<TaskInstance> task) throws ProcessNotFoundException, PackageNotFoundException, Exception {
        ProcessDefinition procd = bpmModule.getProcessDefinition(activity.getProcessDefinitionUUID());
        ActivityWindow activityWindow = new ActivityWindow(procd, activity, task);
        return activityWindow;
    }
}
