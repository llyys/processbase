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
		processManager.setWindow(this);
		setModal(true);
	}
	
	
	public void initUI() {
		
		List<Component> components;
		try {
			components = processManager.openStartTask();
			setCaption(processManager.getTaskInstance().getActivityLabel());
			if(components.size()==1){
				this.addComponent(components.get(0));
			}
			else {
				Accordion accordionLayout=new Accordion();
				accordionLayout.setSizeFull();
				
				for (Component component : components) {
					accordionLayout.addTab(component, component.getCaption(), null);
				}
				this.addComponent(accordionLayout);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
}
