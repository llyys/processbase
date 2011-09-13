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
package org.processbase.ui.bpm.worklist;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import java.util.Date;
import java.util.Set;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.ui.bpm.admin.ProcessInstanceWindow;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class Processes extends TablePanel implements Button.ClickListener {
    

    public Processes() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("state", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionState"), null, null);
        table.setColumnWidth("state", 90);
        table.addContainerProperty("name", TableLinkButton.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionProcessName"), null, null);
        table.setColumnExpandRatio("name", 1);        
        table.addContainerProperty("version", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionVersion"), null, null);
        table.setColumnWidth("version", 50);
        table.addContainerProperty("lastUpdate", Date.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionLastUpdate"), null, null);
        table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
        table.setColumnWidth("lastUpdate", 110);
        
        table.setVisibleColumns(new Object[]{"name", "version", "lastUpdate", "state"});
        
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            Set<LightProcessInstance> processInstances = ProcessbaseApplication.getCurrent().getBpmModule().getLightUserInstances();
            for (LightProcessInstance process : processInstances) {
                Item woItem = table.addItem(process);
                String pdUUID = process.getProcessDefinitionUUID().toString();
                TableLinkButton teb = new TableLinkButton(pdUUID.split("--")[0] + "  #" + process.getNb(), null, null, process, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("version").setValue(pdUUID.split("--")[1]);
                woItem.getItemProperty("lastUpdate").setValue(process.getLastUpdate());
                Property stateColumn = woItem.getItemProperty("state");
				stateColumn.setValue(ProcessbaseApplication.getCurrent().getPbMessages().getString(process.getInstanceState().toString()));
				
                
            }
             this.rowCount = processInstances.size();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
        table.setSortContainerPropertyId("lastUpdate");
        table.setSortAscending(false);
        table.sort();

    }

    @Override
    public TableLinkButton getExecBtn(String description, String iconName, Object t, String action) {
        TableLinkButton execBtn = new TableLinkButton(description, iconName, t, this, action);
        return execBtn;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            try {
                TableLinkButton execBtn = (TableLinkButton) event.getButton();
                LightProcessInstance process = (LightProcessInstance) ((TableLinkButton) event.getButton()).getTableValue();
                if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                    ProcessInstanceWindow window = new ProcessInstanceWindow(process, false);
                    this.getWindow().addWindow(window);
                    window.initUI();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
                throw new RuntimeException(ex);
            }
        }
    }
}
