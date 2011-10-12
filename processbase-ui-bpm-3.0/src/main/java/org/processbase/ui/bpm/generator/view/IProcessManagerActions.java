package org.processbase.ui.bpm.generator.view;

import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;

public interface IProcessManagerActions {
	
	void onStartSubProcess(ProcessDefinitionUUID processDefinitionUUID, ActivityInstanceUUID activityInstanceUUID);
	void onFinishProcess(ProcessDefinitionUUID processDefinitionUUID);
	void onTaskFinished(TaskInstance taskInstance);
	void onViewUpdated(TaskInstance taskInstance);
	 
	public void setParent(Object parent); 
}