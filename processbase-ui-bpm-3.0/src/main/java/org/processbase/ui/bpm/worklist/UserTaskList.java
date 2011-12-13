package org.processbase.ui.bpm.worklist;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.caliburn.application.event.IHandle;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.ui.bpm.generator.view.OpenProcessWindow;
import org.processbase.ui.bpm.panel.events.TaskListEvent;
import org.processbase.ui.bpm.panel.events.TaskListEvent.ActionType;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.IPbTable;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;

import com.vaadin.data.Item;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
/**
 * Panel shows all processes that user has initialized, but now the process needs some user interaction 
 * @author lauri
 *
 */
public class UserTaskList extends TablePanel implements IPbTable, Button.ClickListener, IHandle<TaskListEvent> {

		private Button menuBtn;
		private void InitMenuButton(){
			 menuBtn = new Button(ProcessbaseApplication.getString("myProcessesBtn"), this);
			 menuBtn.setStyleName(Reindeer.BUTTON_LINK);
		     
		}
		
	    public UserTaskList() {
	        super();
	        InitMenuButton();
	    }
	    private boolean isInitialized=false;
		private Button processesBtn;
	    @Override
	    public void initUI() {
	    	if(isInitialized) return;
	        super.initUI();
	        
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
	            Collection<LightTaskInstance> tasks = ProcessbaseApplication.getCurrent().getBpmModule().getUserLightTaskList(ProcessbaseApplication.getCurrent().getCurrentUser().getUUID(), ActivityState.READY);
	            
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
				LightProcessDefinition lpd = ProcessbaseApplication.getCurrent().getBpmModule().getLightProcessDefinition(task.getProcessDefinitionUUID());
				String processName = lpd.getLabel() != null ? lpd.getLabel() : lpd.getName();
				String processInstanceUUID = task.getProcessInstanceUUID().toString();
				TableLinkButton teb = new TableLinkButton(processName + "  #" + processInstanceUUID.substring(processInstanceUUID.lastIndexOf("--") + 2), lpd.getDescription(), null, task, this, Constants.ACTION_OPEN);
				woItem.getItemProperty("processName").setValue(teb);
				String taskTitle = task.getDynamicLabel() != null ? task.getDynamicLabel() : task.getActivityLabel();
				String taskDescription = task.getDynamicDescription() != null ? (" - " + task.getDynamicDescription()) : "";
				woItem.getItemProperty("taskName").setValue(new Label("<b>" + taskTitle + "</b><i>" + taskDescription + "</i>", Label.CONTENT_XHTML));
				woItem.getItemProperty("lastUpdate").setValue(task.getLastUpdateDate());
				woItem.getItemProperty("expectedEndDate").setValue(task.getExpectedEndDate());
			} catch (Exception e) {

				e.printStackTrace();
				Label label = new Label(task.getActivityName());
				label.setComponentError(new UserError(e.getMessage()));
				woItem.getItemProperty("taskName").setValue(label);				
			}

	    }

	    @Override
	    public void buttonClick(ClickEvent event) {
	        super.buttonClick(event);
	        if(event.getButton() instanceof Button){
	        	
	        }
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
	            throw new RuntimeException(ex);
	        }
	    }

		public Button getMenuBtn() {
			return menuBtn;
		}

		public void Handle(TaskListEvent message) {
			if(this.processesBtn==message.getButton())
			{
				refreshTable();
			}
			else if(message.getActionType()==ActionType.REFRESH){
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
