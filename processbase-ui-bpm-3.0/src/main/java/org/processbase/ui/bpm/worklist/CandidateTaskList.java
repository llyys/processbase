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
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.caliburn.application.event.IHandle;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.bonita.forms.XMLProcessDefinition;
import org.processbase.ui.core.bonita.forms.XMLTaskDefinition;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.ui.bpm.generator.GeneratedWindow;
import org.processbase.ui.bpm.generator.view.OpenProcessWindow;
import org.processbase.ui.bpm.panel.events.TaskListEvent;
import org.processbase.ui.bpm.panel.events.TaskListEvent.ActionType;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.FormsDefinition;
import org.processbase.ui.core.bonita.process.BarResource;

/**
 *
 * @author mgubaidullin
 */
public class CandidateTaskList extends TablePanel implements Button.ClickListener, IHandle<TaskListEvent> {

    public CandidateTaskList() {
        super();
    }
    
	private Button processesBtn;
    @Override
    public void initUI() {
    	if(isInitialized()) return;
        super.initUI();
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
        table.addContainerProperty("accepted", ThemeResource.class, null);
        table.setItemIconPropertyId("accepted");
        table.setColumnWidth("accepted", 30);
        
        table.addContainerProperty("processName", Component.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionProcess"), null, null);
        table.addContainerProperty("taskName", Label.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionTask"), null, null);
        table.setColumnExpandRatio("taskName", 1);
        
        table.addContainerProperty("lastUpdate", Date.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionLastUpdatedDate"), null, null);
        
        table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
        table.setColumnWidth("lastUpdate", 110);
        
        table.addContainerProperty("expectedEndDate", Date.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionExpectedEndDate"), null, null);
        table.addGeneratedColumn("expectedEndDate", new PbColumnGenerator());
        table.setColumnWidth("expectedEndDate", 110);
        
        table.setVisibleColumns(new Object[]{"processName", "taskName", "lastUpdate", "expectedEndDate"});
    }

    @Override
    public void refreshTable() {
    	if(!isInitialized())
    		initUI();
    	
        table.removeAllItems();
        try {
            Collection<LightTaskInstance> tasks = ProcessbaseApplication.getCurrent().getBpmModule().getLightTaskList(ActivityState.READY);
            tasks.addAll(ProcessbaseApplication.getCurrent().getBpmModule().getLightTaskList(ActivityState.EXECUTING));
            tasks.addAll(ProcessbaseApplication.getCurrent().getBpmModule().getLightTaskList(ActivityState.SUSPENDED));
//        tasks.addAll(bpmModule.getActivities(ActivityState.INITIAL));
            for (LightTaskInstance task : tasks) {
                addTableRow(task, null);
            }
            this.rowCount = tasks.size();
        } catch (Exception ex) {
        	
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        table.setSortContainerPropertyId("lastUpdate");
        table.setSortAscending(false);
        table.sort();

    }

    private void addTableRow(LightTaskInstance task, LightTaskInstance previousTask) throws InstanceNotFoundException, Exception {
    	Item woItem = previousTask == null ? table.addItem(task) : table.addItemAfter(previousTask, task);
        try {
			

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
			BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
			LightProcessDefinition lpd = bpmModule.getLightProcessDefinition(task.getProcessDefinitionUUID());
			String processName = lpd.getLabel() != null ? lpd.getLabel() : lpd.getName();
			String processInstanceUUID = task.getProcessInstanceUUID().toString();
			TableLinkButton teb = new TableLinkButton(bpmModule.formatProcessName(lpd, task), lpd.getDescription(), null, task, this, Constants.ACTION_OPEN);
			woItem.getItemProperty("processName").setValue(teb);
			String taskTitle = task.getDynamicLabel() != null ? task.getDynamicLabel() : task.getActivityLabel();
			String taskDescription = task.getDynamicDescription() != null ? (" - " + task.getDynamicDescription()) : "";
			woItem.getItemProperty("taskName").setValue(new Label("<b>" + taskTitle + "</b><i>" + taskDescription + "</i>", Label.CONTENT_XHTML));
			woItem.getItemProperty("lastUpdate").setValue(task.getLastUpdateDate());
			woItem.getItemProperty("expectedEndDate").setValue(task.getExpectedEndDate());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Label label = new Label(task.getActivityName());
			label.setComponentError(new UserError(e.getMessage()));
			woItem.getItemProperty("taskName").setValue(label);
			
		}

    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            try {
                LightTaskInstance task = (LightTaskInstance) ((TableLinkButton) event.getButton()).getTableValue();
                openTaskPage(task);

            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
                throw new RuntimeException(ex);
            }
        }
    }

    public void openTaskPage(LightTaskInstance task) {
        try {
        	BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
			LightTaskInstance newTask = bpmModule.getTaskInstance(task.getUUID());
	        if (newTask == null || newTask.getState().equals(ActivityState.FINISHED) || newTask.getState().equals(ActivityState.ABORTED)) {
				table.removeItem(task);
				return;
	        }
            Map<String, String> processMetaData = bpmModule.getProcessMetaData(task.getProcessDefinitionUUID());
            String url =  processMetaData.get(task.getActivityDefinitionUUID().toString());
            if (url != null && !url.isEmpty() && url.length() > 0) {
                ProcessbaseApplication.getCurrent().removeSessionAttribute("PROCESSINSTANCE");
                ProcessbaseApplication.getCurrent().removeSessionAttribute("TASKINSTANCE");

                ProcessbaseApplication.getCurrent().setSessionAttribute("TASKINSTANCE", task.getUUID().toString());
                this.getWindow().open(new ExternalResource(url));
            } else {
            	OpenProcessWindow opw=new OpenProcessWindow();
            	opw.initTask(newTask);
            	opw.initUI();
           	 	this.getApplication().getMainWindow().addWindow(opw);            	
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            //showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

	public void Handle(TaskListEvent message) {
		if(this.processesBtn==message.getButton())
		{
			refreshTable();
		}
		else if(message.getActionType()==ActionType.REFRESH){
			if(!this.processesBtn.isEnabled())
				refreshTable();			
		}
		else {
			this.processesBtn.setEnabled(true);
			this.processesBtn.setStyleName(Reindeer.BUTTON_LINK);
		}
	}

	public void setButton(Button myTaskBtn) {
		this.processesBtn = myTaskBtn;
		
	}
}
