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
package org.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.util.Date;
import java.util.Set;
import org.processbase.core.Constants;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.PbColumnGenerator;

/**
 *
 * @author marat gubaidullin
 */
public class ProcessDefinitionsPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public ProcessDefinitionsPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("name", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionProcessName"), null, null);
        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("version", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionVersion"), null, null);
        table.setColumnWidth("version", 50);
        table.addContainerProperty("deployedBy", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionDeployedBy"), null, null);
        table.addContainerProperty("state", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionState"), null, null);
        table.addContainerProperty("deployedDate", Date.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionDeployedDate"), null, null);
        table.addGeneratedColumn("deployedDate", new PbColumnGenerator());
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            Set<ProcessDefinition> pds = PbPortlet.getCurrent().bpmModule.getProcesses();
            for (ProcessDefinition pd : pds) {
                Item woItem = table.addItem(pd);
                TableLinkButton teb = new TableLinkButton(pd.getLabel() != null ? pd.getLabel() : pd.getName(), pd.getDescription(), null, pd, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("version").setValue(pd.getVersion());
                woItem.getItemProperty("deployedBy").setValue(pd.getDeployedBy());
                woItem.getItemProperty("state").setValue(pd.getState());
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
                    ProcessDefinition processDefinition = PbPortlet.getCurrent().bpmModule.getProcessDefinition(pd);
                    ProcessDefinitionWindow processDefinitionWindow = new ProcessDefinitionWindow(processDefinition);
                    processDefinitionWindow.exec();
                    processDefinitionWindow.addListener((Window.CloseListener) this);
                    getApplication().getMainWindow().addWindow(processDefinitionWindow);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            }
        }
    }
}
