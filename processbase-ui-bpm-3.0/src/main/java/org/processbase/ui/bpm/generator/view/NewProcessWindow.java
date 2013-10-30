package org.processbase.ui.bpm.generator.view;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.core.template.PbWindow;

import com.vaadin.ui.CustomLayout;
/**
 * Process pool dialog that can have multiple UserTaskPanels
 * 
 * @author lauri
 */
public class NewProcessWindow extends PbWindow{

	protected ProcessController processController=new ProcessController();

	/**
	 * When user opens new process panel
	 * @param task
	 * @throws Exception 
	 */
	
	
	public void initProcess(LightProcessDefinition process) throws Exception {
		processController.initProcess(process);
	}
	
	public ProcessManager getProcessManager(){
		return processController.getProcessManager();
	}
	
	
	
	public void initUI() {
		
		try {
			setWidth("845px");
			setHeight("90%");
			setResizable(true);
			processController.setWindow(this);
			processController.initUI();
						
			
			setModal(true);
			center();
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}


	
	
}
