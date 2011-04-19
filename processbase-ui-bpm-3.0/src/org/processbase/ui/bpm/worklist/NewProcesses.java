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
import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import java.util.Collection;
import java.util.Set;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.runtime.Category;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.bonita.forms.XMLProcessDefinition;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TreeTablePanel;
import org.processbase.ui.core.util.CategoryAndProcessDefinition;
import org.processbase.ui.bpm.generator.GeneratedWindow2;
import org.processbase.ui.core.bonita.forms.FormsDefinition;

/**
 *
 * @author mgubaidullin
 */
public class NewProcesses extends TreeTablePanel implements Button.ClickListener {

    public NewProcesses() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        treeTable.addContainerProperty("category", String.class, null, ((Processbase) getApplication()).getPbMessages().getString("tableCaptionCategory"), null, null);
        treeTable.addContainerProperty("processName", TableLinkButton.class, null, ((Processbase) getApplication()).getPbMessages().getString("tableCaptionProcess"), null, null);
        treeTable.setColumnExpandRatio("processName", 1);
        treeTable.addContainerProperty("processDescription", String.class, null, ((Processbase) getApplication()).getPbMessages().getString("tableCaptionDescription"), null, null);
        treeTable.setColumnExpandRatio("processDescription", 1);
        treeTable.addContainerProperty("version", String.class, null, ((Processbase) getApplication()).getPbMessages().getString("tableCaptionVersion"), null, null);
        treeTable.setVisibleColumns(new Object[]{"category", "processName", "processDescription", "version"});
    }

    @Override
    public void refreshTable() {
        treeTable.removeAllItems();
        try {
            Set<Category> categories = ((Processbase) getApplication()).getBpmModule().getAllCategories();
            Collection<LightProcessDefinition> processes = ((Processbase) getApplication()).getBpmModule().getAllowedLightProcessDefinitions();

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
                LightProcessDefinition refreshProcess = ((Processbase) getApplication()).getBpmModule().getLightProcessDefinition(process.getUUID());
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
            String url = ((Processbase) getApplication()).getBpmModule().getProcessMetaData(process.getUUID()).get(process.getUUID().toString());

            if (url != null && !url.isEmpty() && url.length() > 0) {
                ((Processbase) getApplication()).removeSessionAttribute("PROCESSINSTANCE");
                ((Processbase) getApplication()).removeSessionAttribute("TASKINSTANCE");
                ((Processbase) getApplication()).setSessionAttribute("PROCESSINSTANCE", process.getUUID().toString());
                this.getWindow().open(new ExternalResource(url));
            } else {
                XMLProcessDefinition xmlProcess = ((Processbase) getApplication()).getBpmModule().getXMLProcessDefinition(process.getUUID());
                FormsDefinition formsDefinition = ((Processbase) getApplication()).getBpmModule().getFormsDefinition(process.getUUID());
                if (!xmlProcess.isByPassFormsGeneration() && xmlProcess.getForms() == null) {
                    showError(((Processbase) getApplication()).getPbMessages().getString("ERROR_UI_NOT_DEFINED"));
                } else if (!xmlProcess.isByPassFormsGeneration() && xmlProcess.getForms().size() > 0) {
                    GeneratedWindow2 genWindow = new GeneratedWindow2(process.getLabel());
                    genWindow.setProcessDef(process);
                    genWindow.setFormsDefinition(formsDefinition);
                    this.getApplication().getMainWindow().addWindow(genWindow);
                    genWindow.initUI();
                } else if (xmlProcess.isByPassFormsGeneration()) {
                    ((Processbase) getApplication()).getBpmModule().startNewProcess(process.getUUID());
                    showImportantInformation(((Processbase) getApplication()).getPbMessages().getString("processStarted"));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
}
