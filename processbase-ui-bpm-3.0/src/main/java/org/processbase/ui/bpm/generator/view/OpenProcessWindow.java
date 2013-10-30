/**
 * 
 */
package org.processbase.ui.bpm.generator.view;

import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.ow2.bonita.light.LightTaskInstance;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;

/**
 * @author lauri
 *
 */
public class OpenProcessWindow extends NewProcessWindow{
	
	public void initTask(LightTaskInstance task) throws Exception{
		BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
		LightProcessDefinition process = bpmModule.getLightProcessDefinition(task.getProcessDefinitionUUID());
		TaskInstance taskInstance=bpmModule.getTaskInstance(task.getUUID());
		processController.setProcessManager(new ProcessManager(process, taskInstance));
	}
	

}
