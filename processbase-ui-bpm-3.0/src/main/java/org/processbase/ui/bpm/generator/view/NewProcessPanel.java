package org.processbase.ui.bpm.generator.view;

import java.util.List;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.TaskInstance;
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
public class NewProcessPanel extends PbWindow{

	private ProcessManager processManager;

	/**
	 * When user opens new process panel
	 * @param task
	 * @throws Exception 
	 */
	public NewProcessPanel(LightProcessDefinition process) throws Exception{
		processManager=new ProcessManager(process, null);
		
	}
	
	
	public void initUI() {
		
		try {
			setWidth("845px");
			setHeight("90%");
			setResizable(true);
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
	
}
