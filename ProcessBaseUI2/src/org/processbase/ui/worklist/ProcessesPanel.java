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
package org.processbase.ui.worklist;

import com.vaadin.data.Item;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import java.util.Date;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.processbase.ui.template.PbColumnGenerator;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.light.LightProcessInstance;
import org.processbase.ui.template.PbWindow;
import org.processbase.util.Constants;

/**
 *
 * @author mgubaidullin
 */
public class ProcessesPanel extends TablePanel implements Button.ClickListener {

    public ProcessesPanel(PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionProcessName"), null, null);
        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("version", String.class, null, messages.getString("tableCaptionVersion"), null, null);
        table.setColumnWidth("version", 50);
        table.addContainerProperty("customID", String.class, null, messages.getString("tableCaptionCustomId"), null, null);
        table.setColumnWidth("customID", 150);
        table.addContainerProperty("createdDate", Date.class, null, messages.getString("tableCaptionCreatedDate"), null, null);
        table.addGeneratedColumn("createdDate", new PbColumnGenerator());
        table.setColumnWidth("createdDate", 100);
        table.addContainerProperty("lastUpdate", Date.class, null, messages.getString("tableCaptionLastUpdate"), null, null);
        table.addGeneratedColumn("lastUpdate", new PbColumnGenerator());
        table.setColumnWidth("lastUpdate", 100);
        table.addContainerProperty("endDate", Date.class, null, messages.getString("tableCaptionEndedDate"), null, null);
        table.addGeneratedColumn("endDate", new PbColumnGenerator());
        table.setColumnWidth("endDate", 100);
        table.addContainerProperty("state", String.class, null, messages.getString("tableCaptionState"), null, null);
        table.setColumnWidth("state", 90);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 95);
        table.setVisibleColumns(new Object[]{"name", "version", "customID", "createdDate", "lastUpdate", "endDate", "state", "actions"});
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        try {
            for (LightProcessInstance process : bpmModule.getLightUserInstances()) {
                Item woItem = table.addItem(process);
                ProcessDefinition pd = bpmModule.getProcessDefinition(process.getProcessDefinitionUUID());
                woItem.getItemProperty("customID").setValue(bpmModule.getProcessInstanceVariable(process.getUUID(), "customID"));
                woItem.getItemProperty("name").setValue(pd.getLabel());
                woItem.getItemProperty("version").setValue(pd.getVersion());
                woItem.getItemProperty("createdDate").setValue(process.getStartedDate());
                woItem.getItemProperty("lastUpdate").setValue(process.getLastUpdate());
                woItem.getItemProperty("endDate").setValue(process.getEndedDate());
                woItem.getItemProperty("state").setValue(messages.getString(process.getInstanceState().toString()));
                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(getExecBtn(messages.getString("btnInformation"), "icons/document-txt.png", process, Constants.ACTION_OPEN));
                woItem.getItemProperty("actions").setValue(tebb);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        table.setSortContainerPropertyId("createdDate");
        table.setSortAscending(false);
        table.sort();

    }

    @Override
    public TableExecButton getExecBtn(String description, String iconName, Object t, String action) {
        TableExecButton execBtn = new TableExecButton(description, iconName, t, this, action);
        return execBtn;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            try {
                TableExecButton execBtn = (TableExecButton) event.getButton();
                LightProcessInstance process = (LightProcessInstance) ((TableExecButton) event.getButton()).getTableValue();
                if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                    PbWindow window = new PbWindow(this.getPortletApplicationContext2());
                    TasksPanel tp = new TasksPanel(this.getPortletApplicationContext2(), process.getProcessInstanceUUID());
                    window.setSizeUndefined();
                    window.addComponent(tp);
                    window.setWidth("1000px");
                    window.setCaption(messages.getString("tabCaptionProcessSteps") + " \"" + tp.getProcessDefinition().getLabel() + " " + tp.getProcessDefinition().getVersion() + " \"");
                    window.setModal(true);
                    window.setResizable(false);
                    this.getWindow().addWindow(window);
                    tp.refreshTable();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
            }
        }
    }
}
