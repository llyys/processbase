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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import java.util.Collection;
import java.util.Set;
import javax.portlet.PortletSession;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.processbase.ui.template.TableLinkButton;
import org.ow2.bonita.facade.runtime.Category;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.bpm.forms.XMLProcessDefinition;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.TreeTablePanel;
import org.processbase.ui.util.CategoryAndProcessDefinition;

/**
 *
 * @author mgubaidullin
 */
public class NewProcessesPanel extends TreeTablePanel implements Button.ClickListener {

    public NewProcessesPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        treeTable.addContainerProperty("category", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionCategory"), null, null);
        treeTable.addContainerProperty("processName", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionProcess"), null, null);
        treeTable.setColumnExpandRatio("processName", 1);
        treeTable.addContainerProperty("processDescription", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionDescription"), null, null);
        treeTable.setColumnExpandRatio("processDescription", 1);
        treeTable.addContainerProperty("version", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionVersion"), null, null);
        treeTable.setVisibleColumns(new Object[]{"category", "processName", "processDescription", "version"});
    }

    @Override
    public void refreshTable() {
        treeTable.removeAllItems();
        try {
            Set<Category> categories = PbPortlet.getCurrent().bpmModule.getAllCategories();
            Collection<LightProcessDefinition> processes = PbPortlet.getCurrent().bpmModule.getLightProcessDefinitions(ProcessState.ENABLED);

            for (Category category : categories) {
                CategoryAndProcessDefinition capParent = new CategoryAndProcessDefinition(category, null);
                addTableRow(capParent, null);
                for (LightProcessDefinition process : processes) {
                    if (process.getCategoryNames().contains(category.getName())) {
                        CategoryAndProcessDefinition cap = new CategoryAndProcessDefinition(category, process);
                        addTableRow(cap, capParent);
                    }
                }
            }

            for (Object id : treeTable.getItemIds()) {
                if (treeTable.getParent(id) == null && !treeTable.hasChildren(id)) {
                    treeTable.removeItem(id);
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

    private void addTableRow(CategoryAndProcessDefinition item, CategoryAndProcessDefinition parent) throws InstanceNotFoundException, Exception {

        Item woItem = treeTable.addItem(item);
        if (parent == null) {
            treeTable.setChildrenAllowed(item, true);
            treeTable.setCollapsed(item, false);
            woItem.getItemProperty("category").setValue(new Label(item.getCategory().getName()));
        } else {
            treeTable.setChildrenAllowed(item, false);
            treeTable.setParent(item, parent);
            TableLinkButton teb = new TableLinkButton(item.getProcessDef().getLabel() != null ? item.getProcessDef().getLabel() : item.getProcessDef().getName(), item.getProcessDef().getDescription(), null, item.getProcessDef(), this, Constants.ACTION_OPEN);
            woItem.getItemProperty("processName").setValue(teb);
            woItem.getItemProperty("processDescription").setValue(item.getProcessDef().getDescription());
            woItem.getItemProperty("version").setValue(item.getProcessDef().getVersion());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            try {
                LightProcessDefinition process = (LightProcessDefinition) ((TableLinkButton) event.getButton()).getTableValue();
                LightProcessDefinition refreshProcess = PbPortlet.getCurrent().bpmModule.getLightProcessDefinition(process.getUUID());
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
            String url = PbPortlet.getCurrent().bpmModule.getProcessMetaData(process.getUUID()).get(process.getUUID().toString());

            PbPortlet.getCurrent().portletSession.removeAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE);
            PbPortlet.getCurrent().portletSession.removeAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE);

            PbPortlet.getCurrent().portletSession.setAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", process.getUUID(), PortletSession.APPLICATION_SCOPE);
            if (url != null && !url.isEmpty() && url.length() > 0) {
                this.getWindow().open(new ExternalResource(url));
            } else {
                XMLProcessDefinition xmlProcess = PbPortlet.getCurrent().bpmModule.getXMLProcessDefinition(process.getUUID());
                if (!xmlProcess.isByPassFormsGeneration() && xmlProcess.getForms() == null) {
                    showError(PbPortlet.getCurrent().messages.getString("ERROR_UI_NOT_DEFINED"));
                } else if (!xmlProcess.isByPassFormsGeneration() && xmlProcess.getForms().size() > 0) {
                    FormGenerator fg = new FormGenerator(process, xmlProcess);
                    this.getApplication().getMainWindow().addWindow(fg.getWindow());
                } else if (xmlProcess.isByPassFormsGeneration()) {
                    PbPortlet.getCurrent().bpmModule.startNewProcess(process.getUUID());
                    showImportantInformation(PbPortlet.getCurrent().messages.getString("processStarted"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
}
