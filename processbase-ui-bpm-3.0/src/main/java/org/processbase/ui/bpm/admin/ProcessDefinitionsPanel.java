/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.ui.bpm.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.util.Date;
import java.util.Set;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.processbase.ui.bpm.panel.BPMConfigurationPanel;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;

/**
 *
 * @author marat gubaidullin
 */
public class ProcessDefinitionsPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    private BPMConfigurationPanel bpmConfigurationPanel;

	public ProcessDefinitionsPanel() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        
        table.addContainerProperty("version", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionVersion"), null, null);
        table.setColumnWidth("version", 50);
        
        table.addContainerProperty("name", TableLinkButton.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionProcessName"), null, null);
        table.setColumnExpandRatio("name", 1);
        
        
        table.addContainerProperty("deployedBy", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionDeployedBy"), null, null);
        //table.addContainerProperty("state", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionState"), null, null);
        table.addContainerProperty("deployedDate", Date.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionDeployedDate"), null, null);
        table.addGeneratedColumn("deployedDate", new PbColumnGenerator());
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            Set<ProcessDefinition> pds = ProcessbaseApplication.getCurrent().getBpmModule().getProcesses(ProcessState.ENABLED);
            for (ProcessDefinition pd : pds) {
                Item woItem = table.addItem(pd);
                TableLinkButton teb = new TableLinkButton(pd.getLabel() != null ? pd.getLabel() : pd.getName(), pd.getDescription(), null, pd, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("version").setValue(pd.getVersion());
                woItem.getItemProperty("deployedBy").setValue(pd.getDeployedBy());
                //woItem.getItemProperty("state").setValue(pd.getState());
                woItem.getItemProperty("deployedDate").setValue(pd.getDeployedDate());
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            TableLinkButton execBtn = (TableLinkButton) event.getButton();
            if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                try {
                    ProcessDefinition pd = (ProcessDefinition) execBtn.getTableValue();
                    ProcessDefinition processDefinition = ProcessbaseApplication.getCurrent().getBpmModule().getProcessDefinition(pd);
                    
                    ProcessDefinitionWindow processDefinitionWindow = new ProcessDefinitionWindow(processDefinition);
                    //processDefinitionWindow.addListener((Window.CloseListener) this);
                    
                    getApplication().getMainWindow().addWindow(processDefinitionWindow);
                    processDefinitionWindow.initUI();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                    throw new RuntimeException(ex);
                }
            }
        }
    }

	public void setBpmConfigurationPanel(
			BPMConfigurationPanel bpmConfigurationPanel) {
				this.bpmConfigurationPanel = bpmConfigurationPanel;
		// TODO Auto-generated method stub
		
	}
}
