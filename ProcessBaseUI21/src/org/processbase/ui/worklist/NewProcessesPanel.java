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

import org.processbase.core.Constants;
import org.processbase.ui.generator.FormGenerator;
import com.vaadin.data.Item;
import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.Set;
import javax.portlet.PortletSession;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.processbase.ui.template.TableExecButton;
import org.ow2.bonita.facade.runtime.Category;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.bpm.BPMModule;
import org.processbase.bpm.forms.XMLFormDefinition;
import org.processbase.ui.template.TreeTablePanel;

/**
 *
 * @author mgubaidullin
 */
public class NewProcessesPanel extends TreeTablePanel implements Button.ClickListener {

    public NewProcessesPanel(PortletApplicationContext2 portletApplicationContext2, BPMModule bpmModule, ResourceBundle messages) {
        super(portletApplicationContext2, bpmModule, messages);
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        treeTable.addContainerProperty("category", String.class, null, messages.getString("tableCaptionCategory"), null, null);
        treeTable.addContainerProperty("processName", Component.class, null, messages.getString("tableCaptionProcess"), null, null);
        treeTable.addContainerProperty("version", Label.class, null, messages.getString("tableCaptionVersion"), null, null);
        treeTable.setVisibleColumns(new Object[]{"category", "processName", "version"});
    }

    @Override
    public void refreshTable() {
        treeTable.removeAllItems();
        try {
            Set<Category> categories = bpmModule.getAllCategories();
            Collection<LightProcessDefinition> processes = bpmModule.getLightProcessDefinitions(ProcessState.ENABLED);

            for (Category category : categories) {
                addTableRow(category, null);
                for (LightProcessDefinition process : processes) {
                    addTableRow(category, process);
                }
            }
            this.rowCount = processes.size();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        treeTable.setSortContainerPropertyId("category");
        treeTable.setSortAscending(true);
        treeTable.sort();

    }

    private void addTableRow(Category category, LightProcessDefinition process) throws InstanceNotFoundException, Exception {

        if (process == null) {
            Item woItem = treeTable.addItem(category);
            treeTable.setChildrenAllowed(category, true);
            woItem.getItemProperty("category").setValue(category.getName());

        } else {
            Item woItem = treeTable.addItem(process);
            treeTable.setChildrenAllowed(process, false);
            treeTable.setParent(process, category);
            TableExecButton teb = new TableExecButton(process.getLabel(), process.getDescription(), null, process, this, Constants.ACTION_OPEN);
            woItem.getItemProperty("processName").setValue(teb);
            woItem.getItemProperty("version").setValue(process.getVersion());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            try {
                LightProcessDefinition process = (LightProcessDefinition) ((TableExecButton) event.getButton()).getTableValue();
                LightProcessDefinition refreshProcess = bpmModule.getLightProcessDefinition(process.getUUID());
                if (refreshProcess.getState() != ProcessState.ENABLED) {
                    treeTable.removeItem(process);
                } else {
                    openStartPage(process);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.toString());
            }
        }
    }

    public void openStartPage(LightProcessDefinition process) {
        try {
            String url = bpmModule.getProcessMetaData(process.getUUID()).get(process.getUUID().toString());
            getPortletApplicationContext2().getPortletSession().removeAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE);
            getPortletApplicationContext2().getPortletSession().setAttribute("PROCESSBASE_SHARED_TASKINSTANCE", "NEW_PROCESS", PortletSession.APPLICATION_SCOPE);
            if (url != null && !url.isEmpty() && url.length() > 0) {
                this.getWindow().open(new ExternalResource(url));
            } else {
                ArrayList<XMLFormDefinition> forms = bpmModule.getXMLFormDefinition(process.getUUID(), process.getName());
                if (forms == null) {
                    showError(messages.getString("ERROR_UI_NOT_DEFINED"));
                } else if (forms.size() > 0) {
                    FormGenerator fg = new FormGenerator(process, forms, bpmModule, messages, portletApplicationContext2);
                    this.getApplication().getMainWindow().addWindow(fg.getWindow());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
}
