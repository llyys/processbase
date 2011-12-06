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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.ui.bpm.generator.GeneratedWindow;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.CollectionHelper;
import org.processbase.ui.core.CollectionHelper.GroupKey;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.XMLProcessDefinition;
import org.processbase.ui.core.bonita.forms.XMLTaskDefinition;
import org.processbase.ui.core.bonita.process.BarResource;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TreeTablePanel;

import com.vaadin.data.Item;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;

/**
 *
 * @author mgubaidullin
 */
public class TaskCompleted extends TreeTablePanel {

    public TaskCompleted() {
        super();
    }
    
    private Hashtable<String, LightTaskInstance> parentTask=new Hashtable<String, LightTaskInstance>();
    
    
    
    @Override
    public void initUI() {
        super.initUI();
        treeTable.addContainerProperty("processName", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionProcess"), null, null);
        treeTable.addContainerProperty("taskName", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionTask"), null, null);
        treeTable.setColumnExpandRatio("taskName", 1);
        treeTable.addContainerProperty("lastUpdate", Date.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionLastUpdatedDate"), null, null);
        treeTable.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
        treeTable.setColumnWidth("lastUpdate", 110);
    }

    @Override
    public void refreshTable() {
    	treeTable.removeAllItems();
    	List<TaskAndProcess> taskList=new ArrayList<TaskAndProcess>();
        try {
            Collection<LightTaskInstance> tasks = ProcessbaseApplication.getCurrent().getBpmModule().getLightTaskList(ActivityState.FINISHED);
            
            for (LightTaskInstance task : tasks) {
            	LightProcessDefinition lpd = ProcessbaseApplication.getCurrent().getBpmModule().getLightProcessDefinition(task.getProcessDefinitionUUID());
            	//addTableRow(task, lpd);
            	taskList.add(new TaskAndProcess(task, lpd));
            }
            
            Map<String, List<TaskAndProcess>> processGroups=new CollectionHelper<TaskAndProcess>().groupBy(taskList, new GroupKey<TaskCompleted.TaskAndProcess>() {
				public String key(TaskAndProcess source) {
					return source.process.getLabel() != null ? source.process.getLabel() : source.process.getName();
				}
			});
            
            for(String key:processGroups.keySet()){
            	Item node = treeTable.addItem(key);
            	treeTable.setChildrenAllowed(key, true);
            	treeTable.setCollapsed(key, true);
            	node.getItemProperty("processName").setValue(new Label(key));
            	List<TaskAndProcess> taskGroups = processGroups.get(key);
            	
            	Map<String, List<TaskAndProcess>> stepGroups=new CollectionHelper<TaskAndProcess>().groupBy(taskGroups, new GroupKey<TaskCompleted.TaskAndProcess>() {
      				public String key(TaskAndProcess source) {
      					String processInstanceUUID = source.task.getProcessInstanceUUID().toString();
            	        String processInstanceId = processInstanceUUID.substring(processInstanceUUID.lastIndexOf("--") + 2);
            	        String name=source.process.getLabel() != null ? source.process.getLabel() : source.process.getName();
            	        return name + " #" + processInstanceId;      					
      				}
      			});
            	
            	for(String taskKey:stepGroups.keySet())
            	{
            		Item taskNode=treeTable.addItem(taskKey);
            		taskNode.getItemProperty("processName").setValue(new Label( taskKey));
            		treeTable.setParent(taskKey, key);
            		treeTable.setChildrenAllowed(taskKey, true);
            		treeTable.setCollapsed(taskKey, false);
            		List<TaskAndProcess> steps = stepGroups.get(taskKey);
            		
            		for (TaskAndProcess tp : steps) {
                		Item stepNode = treeTable.addItem(tp);
                		treeTable.setParent(tp, taskKey);
                		treeTable.setChildrenAllowed(tp, false);
                		String taskTitle = tp.getTask().getDynamicLabel() != null ? tp.getTask().getDynamicLabel() : tp.getTask().getActivityLabel();
            	        String taskDescription = tp.getTask().getDynamicDescription() != null ? (" - " + tp.getTask().getDynamicDescription()) : "";
            	        //TableLinkButton teb = new TableLinkButton(taskTitle , taskDescription, null, tp.getTask(), this, Constants.ACTION_OPEN);
            	        stepNode.getItemProperty("processName").setValue(new Label( taskTitle));
            	        stepNode.getItemProperty("taskName").setValue(new Label( taskDescription));
            	        //stepNode.getItemProperty("taskName").setValue(teb);
            	        //stepNode.getItemProperty("taskName").setValue(new Label(" <b>" + taskTitle + "</b><i>" + taskDescription + "</i>", Label.CONTENT_XHTML));
            	        stepNode.getItemProperty("lastUpdate").setValue(tp.getTask().getLastUpdateDate());
    				}
            	}
			}
            
            this.rowCount = taskList.size();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
        //treeTable.setSortContainerPropertyId("lastUpdate");
        //treeTable.setSortAscending(false);
        //treeTable.sort();

    }
    


    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            try {
                LightTaskInstance task = (LightTaskInstance) ((TableLinkButton) event.getButton()).getTableValue();
                LightTaskInstance newTask = ProcessbaseApplication.getCurrent().getBpmModule().getTaskInstance(task.getUUID());
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
			String url = bpmModule.getProcessMetaData(task.getProcessDefinitionUUID()).get(task.getActivityDefinitionUUID().toString());
            ProcessbaseApplication.getCurrent().removeSessionAttribute("TASKINSTANCE");
            ProcessbaseApplication.getCurrent().setSessionAttribute("TASKINSTANCE", task.getUUID().toString());
            if (url != null && !url.isEmpty() && url.length() > 0) {
                this.getWindow().open(new ExternalResource(url));
            } else {
                BarResource barResource = BarResource.getBarResource(task.getProcessDefinitionUUID());
                ProcessDefinition processDefinition = bpmModule.getProcessDefinition(task.getProcessDefinitionUUID());
                 
                XMLProcessDefinition xmlProcess = barResource.getXmlProcessDefinition(processDefinition.getName());
                XMLTaskDefinition taskDef = xmlProcess.getTasks().get(task.getActivityName());
                if (!taskDef.isByPassFormsGeneration() /*check that forms is not defined*/) {
                    showError(ProcessbaseApplication.getCurrent().getPbMessages().getString("ERROR_UI_NOT_DEFINED"));
                } else if (!taskDef.isByPassFormsGeneration() /*check that forms is defined*/) {
                    GeneratedWindow genWindow = new GeneratedWindow(task.getActivityLabel());
                    genWindow.setTask(bpmModule.getTaskInstance(task.getUUID()));
                    genWindow.setBarResource(barResource);
                    this.getApplication().getMainWindow().addWindow(genWindow);
                    genWindow.initUI();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }
    
    
    
    class TaskAndProcess {
    	private LightTaskInstance task;
    	private LightProcessDefinition process;
		public TaskAndProcess(LightTaskInstance task,
				LightProcessDefinition lpd) {
			this.task=task;
			this.process=lpd;
		}
		public void setTask(LightTaskInstance task) {
			this.task = task;
		}
		public LightTaskInstance getTask() {
			return task;
		}
		public void setProcess(LightProcessDefinition process) {
			this.process = process;
		}
		public LightProcessDefinition getProcess() {
			return process;
		} 
    }
}
