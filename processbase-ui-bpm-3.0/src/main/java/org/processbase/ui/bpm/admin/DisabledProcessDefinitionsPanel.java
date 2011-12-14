package org.processbase.ui.bpm.admin;

import java.util.Date;
import java.util.Set;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.IPbTable;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;

import com.vaadin.data.Item;

public class DisabledProcessDefinitionsPanel extends ProcessDefinitionsPanel
implements IPbTable
{
	 @Override
	    public void initUI() {
	        super.initUI();
	        
	        table.addContainerProperty("state", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionState"), null, null);
	        
	    }
	 
	 @Override
	    public void refreshTable() {
	        try {
	            table.removeAllItems();
	            Set<ProcessDefinition> pds = ProcessbaseApplication.getCurrent().getBpmModule().getProcesses();
	            for (ProcessDefinition pd : pds) {
	            	if(pd.getState()!=ProcessState.ENABLED){
		                Item woItem = table.addItem(pd);
		                TableLinkButton teb = new TableLinkButton(pd.getLabel() != null ? pd.getLabel() : pd.getName(), pd.getDescription(), null, pd, this, Constants.ACTION_OPEN);
		                woItem.getItemProperty("name").setValue(teb);
		                woItem.getItemProperty("version").setValue(pd.getVersion());
		                woItem.getItemProperty("deployedBy").setValue(pd.getDeployedBy());
		                woItem.getItemProperty("state").setValue(pd.getState());
		                woItem.getItemProperty("deployedDate").setValue(pd.getDeployedDate());
	                }
	            }
	            table.setSortContainerPropertyId("name");
	            table.setSortAscending(false);
	            table.sort();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            showError(ex.getMessage());
	        }
	    }

}
