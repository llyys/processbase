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
import java.util.Date;
import java.util.Set;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.PbColumnGenerator;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class ProcessInstancesPanel extends TablePanel implements Button.ClickListener {

    private ProcessDefinitionUUID filter = null;

    public ProcessInstancesPanel() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("name", TableLinkButton.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionProcessName"), null, null);
        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("version", String.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionVersion"), null, null);
        table.setColumnWidth("version", 50);
        table.addContainerProperty("startedDate", Date.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionStartedDate"), null, null);
        table.addGeneratedColumn("startedDate", new PbColumnGenerator());
        table.setColumnWidth("startedDate", 100);
        table.addContainerProperty("lastUpdate", Date.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionLastUpdate"), null, null);
        table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
        table.setColumnWidth("lastUpdate", 100);
        table.addContainerProperty("state", String.class, null, ((Processbase)getApplication()).getPbMessages().getString("tableCaptionState"), null, null);
        table.setColumnWidth("state", 90);
        table.setVisibleColumns(new Object[]{"name", "version", "startedDate", "lastUpdate", "state"});

    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            Set<LightProcessInstance> pis = null;
            if (filter != null){
                pis = ((Processbase)getApplication()).getBpmModule().getLightProcessInstances(filter);
            } else {
                pis = ((Processbase)getApplication()).getBpmModule().getLightProcessInstances();
            }
            for (LightProcessInstance pi : pis) {
                Item woItem = table.addItem(pi);
                String pdUUID = pi.getProcessDefinitionUUID().toString();
                TableLinkButton teb = new TableLinkButton(pdUUID.split("--")[0] + "  #" + pi.getNb(), null, null, pi, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("startedDate").setValue(pi.getStartedDate());
                woItem.getItemProperty("version").setValue(pdUUID.split("--")[1]);
                woItem.getItemProperty("lastUpdate").setValue(pi.getLastUpdate());
                woItem.getItemProperty("state").setValue(pi.getInstanceState());
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
            try {
                TableLinkButton execBtn = (TableLinkButton) event.getButton();
                LightProcessInstance process = (LightProcessInstance) ((TableLinkButton) event.getButton()).getTableValue();
                if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                    ProcessInstanceWindow window = new ProcessInstanceWindow(process, true);
                    this.getWindow().addWindow(window);
                    window.initUI();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
            }
        }
    }

    public void setFilter(ProcessDefinitionUUID filter) {
        this.filter = filter;
    }

    
}
