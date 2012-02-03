package org.processbase.ui.bpm.generator.view;

import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.core.template.PbWindow;

import com.vaadin.ui.Window;

public class ProcessController {
	private ProcessManager processManager;
	//when running subtasks we need a process stack
	protected Stack<ProcessManager> processManagerStack = new Stack<ProcessManager>();
	private PbWindow window;
	
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
			pm.setIsSubProcess(processManagerStack.size()>0);
			getWindow().setContent(pm);
			pm.reloadTask();
			//replaceComponent(processManager, pm);
			pm.setWindow(getWindow());
			getWindow().setCaption(getWindow().getCaption().replaceAll("\\s\\>\\s"+processManager.getLabel(), ""));
			
			processManager=pm;
		}
		else{
			if(processManager!=null && processManager.getTaskManager()!=null && StringUtils.isNotBlank(processManager.getTaskManager().getConfirmationMessage()))
				getWindow().getParent().showNotification(processManager.getTaskManager().getConfirmationMessage());
			else
				getWindow().getParent().showNotification("Process completed");
			getWindow().close();
			 
		}
	}


	public void startSubProcess(ProcessManager pm){
		processManagerStack.push(processManager);
		getWindow().setContent(pm);
		pm.setIsSubProcess(true);
		getWindow().setCaption(getWindow().getCaption()+" > "+ pm.getLabel());
		//updateCaption(processManagerStack);
		pm.setWindow(getWindow());		
		pm.initUI();
		processManager=pm;
		
	}
	
	public void initUI() {
		
		try {
			getWindow().setWidth("845px");
			getWindow().setHeight("90%");
			getWindow().setResizable(true);
			
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
				
				ProcessController parent=null;
				public void setParent(Object parent) {
					this.parent = (ProcessController) parent;
				}
			};
			
			processManager.setActions(actions);
			actions.setParent(this);			
			
			getWindow().setContent(processManager);
			processManager.setWindow(getWindow());
			
			getWindow().setModal(true);
			getWindow().center();
			
			processManager.initUI();
			getWindow().setCaption(processManager.getLabel());
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}


	public ProcessManager getProcessManager() {
		return processManager;
	}


	public void setWindow(PbWindow window) {
		this.window = window;
	}


	public PbWindow getWindow() {
		return window;
	}


	public void setProcessManager(ProcessManager processManager) {
		this.processManager = processManager;
	}

}
