package org.processbase.ui.bpm.generator.view;

import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.ui.bpm.generator.BarResource;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.XMLProcessDefinition;
import org.processbase.ui.core.bonita.forms.XMLTaskDefinition;
import org.processbase.ui.core.bonita.forms.PageFlow.Pages.Page;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.PbWindow;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
/**
 * Process pool dialog that can have multiple UserTaskPanels
 * 
 * @author lauri
 */
public class NewProcessWindow extends PbWindow{

	protected ProcessManager processManager;
	//when running subtasks we need a process stack
	protected Stack<ProcessManager> processManagerStack = new Stack<ProcessManager>();

	/**
	 * When user opens new process panel
	 * @param task
	 * @throws Exception 
	 */
	
	
	public void initProcess(LightProcessDefinition process) throws Exception {
		processManager=new ProcessManager(process, null);
	}
	
	
	protected void finishProcess() {
		// TODO Auto-generated method stub
		if(processManagerStack.size()>0){
			ProcessManager pm=processManagerStack.pop();	
			setContent(pm);
			pm.reloadTask();
			//replaceComponent(processManager, pm);
			pm.setWindow(this);
			setCaption(pm.getLabel());
			processManager=pm;
		}
		else{
			if(processManager!=null && processManager.getTaskManager()!=null && StringUtils.isNotBlank(processManager.getTaskManager().getConfirmationMessage()))
				this.getParent().showNotification(processManager.getTaskManager().getConfirmationMessage());
			else
				this.getParent().showNotification("Process completed");
			this.close();
			 
		}
	}


	public void startSubProcess(ProcessManager pm){
		processManagerStack.push(processManager);
		setContent(pm);
		setCaption(pm.getLabel());
		pm.setWindow(this);		
		pm.initUI();
		processManager=pm;
	}
	
	public void initUI() {
		
		try {
			setWidth("845px");
			setHeight("90%");
			setResizable(true);
			
			IProcessManagerActions actions = new IProcessManagerActions() {
				
				public void onViewUpdated(TaskInstance taskInstance) {}				
				public void onTaskFinished(TaskInstance taskInstance) {}				
				public void onFinishProcess(ProcessDefinitionUUID processDefinitionUUID) {					
					parent.finishProcess();
				}

				public void onStartSubProcess( ProcessDefinitionUUID processDefinitionUUID, ActivityInstanceUUID activityInstanceUUID) {
					try {
						ProcessManager pm=new ProcessManager(processDefinitionUUID, activityInstanceUUID);
						pm.setActions(this);
						parent.startSubProcess(pm);
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
				
				NewProcessWindow parent=null;
				public void setParent(Object parent) {
					this.parent = (NewProcessWindow) parent;
				}
			};
			
			processManager.setActions(actions);
			actions.setParent(this);			
			
			this.setContent(processManager);
			processManager.setWindow(this);
			
			setModal(true);
			center();
			
			processManager.initUI();
			setCaption(processManager.getLabel());
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}


	public ProcessManager getProcessManager() {
		return processManager;
	}
	
}
