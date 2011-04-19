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
package org.processbase.ui.bpm.admin;

import com.vaadin.data.Item;
import java.util.Date;
import java.util.Set;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.uuid.ActivityDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightActivityInstance;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class ActivitiesPanel extends TablePanel {

    private ProcessInstanceUUID processInstanceUUID;
//    private ProcessDefinition processDefinition;
    private Set<ActivityInstance> activities;

    public ActivitiesPanel(ProcessInstanceUUID processInstanceUUID) {
        super();
        try {
            this.processInstanceUUID = processInstanceUUID;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("name", String.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionTask"), null, null);
        table.addContainerProperty("type", String.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionTask"), null, null);
        table.addContainerProperty("createdDate", Date.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionCreatedDate"), null, null);
        table.addGeneratedColumn("createdDate", new PbColumnGenerator());
        table.setColumnWidth("createdDate", 100);
        table.addContainerProperty("startdeDate", Date.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionStartedDate"), null, null);
        table.addGeneratedColumn("startdeDate", new PbColumnGenerator());
        table.setColumnWidth("startdeDate", 100);
        table.addContainerProperty("endDate", Date.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionFinishedDate"), null, null);
        table.addGeneratedColumn("endDate", new PbColumnGenerator());
        table.setColumnWidth("endDate", 100);
        table.addContainerProperty("candidates", String.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionCandidates"), null, null);
        table.addContainerProperty("taskuser", String.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionTaskUser"), null, null);
        table.addContainerProperty("state", String.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionState"), null, null);
        table.setColumnWidth("state", 90);
        table.setPageLength(5);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            activities = ((Processbase)getApplication()).getBpmModule().getActivityInstances(processInstanceUUID);
            for (ActivityInstance lai : activities) {
                if (lai.isAutomatic() || lai.isTask()) {
                    Item woItem = table.addItem(lai);
                    woItem.getItemProperty("name").setValue(lai.getActivityLabel());
                    woItem.getItemProperty("type").setValue(lai.isTask() ? ((Processbase)getApplication()).getPbMessages().getString("task") : ((Processbase)getApplication()).getPbMessages().getString("automatic"));
                    woItem.getItemProperty("createdDate").setValue(lai.getReadyDate());
                    woItem.getItemProperty("startdeDate").setValue(lai.getStartedDate());
                    woItem.getItemProperty("endDate").setValue(lai.getEndedDate());
                    woItem.getItemProperty("state").setValue(((Processbase)getApplication()).getPbMessages().getString(lai.getState().toString()));
                    if (lai.isTask()) {
                        woItem.getItemProperty("candidates").setValue(lai.getTask().getTaskCandidates());
                        woItem.getItemProperty("taskuser").setValue(lai.getTask().getTaskUser());
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

    private LightActivityInstance getActivity(ActivityDefinitionUUID activityDefinitionUUID) {
        for (LightActivityInstance lai : activities) {
            if (lai.getActivityDefinitionUUID().equals(activityDefinitionUUID)) {
                return lai;
            }
        }
        return null;
    }
}
