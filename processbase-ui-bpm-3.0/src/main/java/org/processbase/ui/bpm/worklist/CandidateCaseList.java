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
import com.vaadin.data.Property;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.themes.Reindeer;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.caliburn.application.event.IHandle;
import org.hibernate.id.Assigned;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.InstanceState;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.ui.bpm.admin.ProcessDefinitionWindow;
import org.processbase.ui.bpm.admin.ProcessInstanceWindow;
import org.processbase.ui.bpm.generator.view.OpenProcessWindow;
import org.processbase.ui.bpm.panel.events.TaskListEvent;
import org.processbase.ui.bpm.panel.events.TaskListEvent.ActionType;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.IPbTable;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;

/**
 * Kasutaja poolt algatatud ja talle suunatud menetlused. 
 * (Nimekirjast avaneb menetlusjuhtumi vaade - Joonis + läbitud sammud)
 * @author lauri 
 */
public class CandidateCaseList extends TablePanel implements IPbTable,  Button.ClickListener, IHandle<TaskListEvent>{
    

    private Button processesBtn;

	public CandidateCaseList() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("icon", ThemeResource.class, null);
        table.setColumnWidth("icon", 30);
        table.setItemIconPropertyId("icon");
        table.setRowHeaderMode(Table.ROW_HEADER_MODE_ICON_ONLY);
        
        table.addContainerProperty("task", String.class, null);
        table.setColumnExpandRatio("task", 0.7F);
        
        table.addContainerProperty("state", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionState"), null, null);
        table.setColumnWidth("state", 90);
        table.addContainerProperty("name", Component.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionProcessName"), null, null);
        table.setColumnExpandRatio("name", 0.3F);
        
        table.addContainerProperty("started", Date.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionLastUpdate"), null, null);
        table.addGeneratedColumn("started", new PbColumnGenerator());
        table.setColumnWidth("started", 110);
        table.addContainerProperty("lastUpdate", Date.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionLastUpdate"), null, null);
        table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
        table.setColumnWidth("lastUpdate", 110);
        table.setSortDisabled(false);
        
        table.setVisibleColumns(new Object[]{"name", "task", "started", "lastUpdate", "state"});
        
    }

    @Override
    public void refreshTable() {
    	if(!isInitialized())
    		initUI();
    	
        table.removeAllItems();
        try {
        	ProcessbaseApplication application = ProcessbaseApplication.getCurrent();
        	//1. load all tasks assigned to logged user        	
        	Set<String> pids=new HashSet<String>();
        	  Collection<LightTaskInstance> tasks = ProcessbaseApplication.getCurrent().getBpmModule().getUserLightTaskList(application.getUserName(), ActivityState.READY);
              tasks.addAll(ProcessbaseApplication.getCurrent().getBpmModule().getUserLightTaskList(application.getUserName(), ActivityState.EXECUTING));
              tasks.addAll(ProcessbaseApplication.getCurrent().getBpmModule().getUserLightTaskList(application.getUserName(), ActivityState.SUSPENDED));

              for (LightTaskInstance task : tasks) {
                  if(task.isTask()){
                	  try {
						LightProcessInstance process = application.getBpmModule().getLightProcessInstance(task.getProcessInstanceUUID());
						  String pid = task.getProcessInstanceUUID().toString();
						pids.add(pid);//Add to set so we don't show dublicate processes
						  addTableRow(process, true, task);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                  }
              }
        	
            //2. load all instances started by the logged user            
			Set<LightProcessInstance> processInstances = application.getBpmModule().getLightUserInstances();
            for (LightProcessInstance process : processInstances) {
            	
            	String pid = process.getProcessInstanceUUID().toString();
				if(process.getInstanceState()==InstanceState.STARTED 
            			&& pids.contains(pid)==false){ //if this process instance is allready in set, then ignore this step
	                addTableRow(process, false, null);
            	}
            }
            
            this.rowCount = processInstances.size();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        table.setSortContainerPropertyId("lastUpdate");
        table.setSortAscending(false);
        table.sort();

    }

	/**
	 * @param process
	 * @param task 
	 */
	private void addTableRow(LightProcessInstance process, boolean isAssignedToUser, LightTaskInstance task) {
		Item woItem = table.addItem(task==null?process:task);
		
		ProcessDefinitionUUID processDefinitionUUID = process.getProcessDefinitionUUID();
		ThemeResource icon = null;
		if(task!=null)			
			icon = new ThemeResource("icons/document.png");
		else
			icon = new ThemeResource("icons/lock.png");
		
		woItem.getItemProperty("icon").setValue(icon);
		
		woItem.getItemProperty("task").setValue(task==null?"":task.getActivityLabel());
		String pdUUID = processDefinitionUUID.toString();
		// (Nimekirjast avaneb menetlusjuhtumi vaade - Joonis + läbitud sammud)
		TableLinkButton teb = new TableLinkButton(pdUUID.split("--")[0] + "  #" + process.getNb(), null, null, process, this, task==null?"process":"task");
		teb.setTableValue(task==null?process:task);
		woItem.getItemProperty("name").setValue(teb);
		
		woItem.getItemProperty("started").setValue(process.getStartedDate());
		woItem.getItemProperty("lastUpdate").setValue(process.getLastUpdate());
		
		Property stateColumn = woItem.getItemProperty("state");
		stateColumn.setValue(ProcessbaseApplication.getCurrent().getPbMessages().getString(process.getInstanceState().toString()));
	}

    @Override
    public TableLinkButton getExecBtn(String description, String iconName, Object t, String action) {
        TableLinkButton execBtn = new TableLinkButton(description, iconName, t, this, action);
        return execBtn;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            try {
                TableLinkButton execBtn = (TableLinkButton) event.getButton();
                
                if (execBtn.getAction().equals("task")) {
                	//will be able to execute the process
                	LightTaskInstance process = (LightTaskInstance) ((TableLinkButton) execBtn).getTableValue();
                	OpenProcessWindow opw=new OpenProcessWindow();
                	opw.initTask(process);
                	opw.initUI();
                	this.getWindow().addWindow(opw);
                }
                else{
                	LightProcessInstance process = (LightProcessInstance) ((TableLinkButton) execBtn).getTableValue();
                	 ProcessInstanceWindow window = new ProcessInstanceWindow(process, false);
                     this.getWindow().addWindow(window);
                     window.initUI();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
                throw new RuntimeException(ex);
            }
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

	public void setButton(Button processesBtn) {
		this.processesBtn = processesBtn;
		// TODO Auto-generated method stub
		
	}
}
