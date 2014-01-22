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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ow2.bonita.facade.identity.User;
import org.ow2.bonita.facade.identity.impl.UserImpl;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.uuid.ActivityDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightActivityInstance;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
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
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("name", String.class, null, ProcessbaseApplication.getString("tableCaptionAction"), null, null);
        table.addContainerProperty("type", String.class, null, ProcessbaseApplication.getString("tableCaptionType"), null, null);
        table.addContainerProperty("createdDate", Date.class, null, ProcessbaseApplication.getString("tableCaptionCreatedDate"), null, null);
        table.addGeneratedColumn("createdDate", new PbColumnGenerator());
        table.setColumnWidth("createdDate", 100);
        table.addContainerProperty("startdeDate", Date.class, null, ProcessbaseApplication.getString("tableCaptionStartedDate"), null, null);
        table.addGeneratedColumn("startdeDate", new PbColumnGenerator());
        table.setColumnWidth("startdeDate", 100);
        table.addContainerProperty("endDate", Date.class, null, ProcessbaseApplication.getString("tableCaptionFinishedDate"), null, null);
        table.addGeneratedColumn("endDate", new PbColumnGenerator());
        table.setColumnWidth("endDate", 100);
        table.addContainerProperty("candidates", String.class, null, ProcessbaseApplication.getString("tableCaptionCandidates"), null, null);
        table.addContainerProperty("taskuser", String.class, null, ProcessbaseApplication.getString("tableCaptionTaskUser"), null, null);
        table.addContainerProperty("state", String.class, null, ProcessbaseApplication.getString("tableCaptionState"), null, null);
        table.setColumnWidth("state", 90);
        table.setPageLength(5);
    }

    @Override
    public void refreshTable() {
    	
    	BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
    	Map<String, User> usersMap = new HashMap<String, User>();
    	
        table.removeAllItems();
        try {
            activities = bpmModule.getActivityInstances(processInstanceUUID);
            for (ActivityInstance lai : activities) {
                if (lai.isAutomatic() || lai.isTask()) {
                    Item woItem = table.addItem(lai);
                    woItem.getItemProperty("name").setValue(lai.getActivityLabel());
                    woItem.getItemProperty("type").setValue(lai.isTask() ? ProcessbaseApplication.getString("task") : ProcessbaseApplication.getString("automatic"));
                    woItem.getItemProperty("createdDate").setValue(lai.getReadyDate());
                    woItem.getItemProperty("startdeDate").setValue(lai.getStartedDate());
                    woItem.getItemProperty("endDate").setValue(lai.getEndedDate());
                    woItem.getItemProperty("state").setValue(ProcessbaseApplication.getString(lai.getState().toString()));
                    
                    if (lai.isTask()) {
                    	
                    	User u = null;
                    	
						if (lai.getTask().getTaskUser() != null) {
							u = usersMap.get(lai.getTask().getTaskUser());
							if (u == null) {
								u = bpmModule.findUserByUserName(lai.getTask()
										.getTaskUser());
							}

							StringBuilder sb = new StringBuilder();
							if (u != null) {
								if (u.getLastName() != null) {
									sb.append(u.getFirstName());
								}
								if (u.getLastName() != null) {
									sb.append(" ").append(u.getLastName());
								}
							} else {
								sb.append(lai.getTask().getTaskUser());
							}
							woItem.getItemProperty("taskuser").setValue(
									sb.toString());
						}
                    	
						if (lai.getTask().getTaskCandidates() != null) {

							List<User> l = new ArrayList<User>();

							for (String un : lai.getTask().getTaskCandidates()) {
								u = usersMap.get(un);
								if (u == null) {
									u = bpmModule.findUserByUserName(un);
								}
								if (u == null) {
									u = new UserImpl(un, "");
								}
								l.add(u);
							}

							StringBuilder sb = new StringBuilder();

							for (int i = 0; i < l.size(); i++) {
								if (i > 0) {
									sb.append(", ");
								}
								if (l.get(i).getLastName() != null) {
									sb.append(l.get(i).getFirstName());
								}
								if (l.get(i).getLastName() != null) {
									sb.append(" ").append(
											l.get(i).getLastName());
								}
							}

							woItem.getItemProperty("candidates").setValue(
									sb.toString());
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

    private LightActivityInstance getActivity(ActivityDefinitionUUID activityDefinitionUUID) {
        for (LightActivityInstance lai : activities) {
            if (lai.getActivityDefinitionUUID().equals(activityDefinitionUUID)) {
                return lai;
            }
        }
        return null;
    }
}
