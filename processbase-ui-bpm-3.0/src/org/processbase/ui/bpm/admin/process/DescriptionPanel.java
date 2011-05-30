package org.processbase.ui.bpm.admin.process;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.processbase.ui.core.template.PbPanel;


import com.vaadin.ui.Label;
/**
*
* @author llyys
*/
public class DescriptionPanel extends PbPanel {
	 @Override
	 public void initUI() {
        
        setMargin(true, false, false, true);
        
        if (processDefinition.getLabel() != null) {
            Label pdLabel = new Label("<b>" + processDefinition.getLabel() + "</b>");
            pdLabel.setContentMode(Label.CONTENT_XHTML);
            addComponent(pdLabel);
        }

        if (processDefinition.getDescription() != null) {
            Label pdDescription = new Label(processDefinition.getDescription());
            pdDescription.setContentMode(Label.CONTENT_XHTML);
            addComponent(pdDescription);
            setExpandRatio(pdDescription, 1);
        }
	 }
	 public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}
	private ProcessDefinition processDefinition = null;
	 
	 
	 
}
