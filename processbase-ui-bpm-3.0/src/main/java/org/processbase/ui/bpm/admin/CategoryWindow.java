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
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.facade.runtime.Category;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.core.template.TableLinkButton;

/**
 *
 * @author mgubaidullin
 */
public class CategoryWindow extends PbWindow implements ClickListener {

    private Category category = null;
    private ButtonBar bar = new ButtonBar();
    private ButtonBar buttons = new ButtonBar();
    private Button deleteBtn;
    private Button cancelBtn;
    private Button saveBtn;
    private Button addBtn;
    private Table table = new Table();
    private ComboBox processesComboBox ;

    public CategoryWindow(Category category) {
        super(category.getName());
        this.category = category;
    }

    public void initUI() {
        try {
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);
            
            processesComboBox = new ComboBox(ProcessbaseApplication.getCurrent().getPbMessages().getString("processToCategory"));
            processesComboBox.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
//            processesComboBox.setItemCaptionPropertyId("name");
            processesComboBox.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_EXPLICIT);
            processesComboBox.setWidth("100%");

            bar.setWidth("100%");
            bar.addComponent(processesComboBox);
            bar.setExpandRatio(processesComboBox, 1);
            
            addBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnAdd"), this);
            bar.addComponent(addBtn);
            bar.setComponentAlignment(addBtn, Alignment.BOTTOM_RIGHT);

            layout.addComponent(bar);
            layout.addComponent(table);

            deleteBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDelete"), this);
            deleteBtn.setDescription(ProcessbaseApplication.getCurrent().getPbMessages().getString("deleteCategory"));
            cancelBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnCancel"), this);
            saveBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnSave"), this);
            buttons.addButton(deleteBtn);
            buttons.setComponentAlignment(deleteBtn, Alignment.MIDDLE_RIGHT);
            buttons.addButton(saveBtn);
            buttons.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(saveBtn,
                    1);
            buttons.addButton(cancelBtn);
            buttons.setComponentAlignment(cancelBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(false);
            buttons.setHeight("30px");
            buttons.setWidth("100%");
            addComponent(buttons);
            setWidth("70%");
//            setHeight("70%");
            setResizable(false);

            table.addContainerProperty("name", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionProcessName"), null, null);
            table.setColumnExpandRatio("name", 1);
            table.addContainerProperty("version", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionVersion"), null, null);
            table.setColumnWidth("version", 50);
            table.addContainerProperty("deployedBy", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionDeployedBy"), null, null);
            table.addContainerProperty("actions", TableLinkButton.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"), null, null);
            table.setColumnWidth("actions", 50);
            table.setSelectable(false);
            table.setImmediate(true);
            table.setWidth("100%");
            table.setPageLength(10);
            refreshTable();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void refreshTable() {
        try {
            table.removeAllItems();
            Collection<LightProcessDefinition> processes = ProcessbaseApplication.getCurrent().getBpmModule().getLightProcessDefinitions(ProcessState.ENABLED);

            for (LightProcessDefinition pd : processes) {
                if (pd.getCategoryNames().contains(category.getName())) {
                    addTableRow(pd);
                } else {
                    Item woItem = processesComboBox.addItem(pd);
                    String caption = pd.getLabel() != null ? pd.getLabel() : pd.getName();
                    processesComboBox.setItemCaption(pd, caption + " (version " + pd.getVersion() + ")");
                }
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(true);
            table.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private void addTableRow(LightProcessDefinition pd) {
        Item woItem = table.addItem(pd);
        woItem.getItemProperty("name").setValue(pd.getLabel());
        woItem.getItemProperty("version").setValue(pd.getVersion());
        woItem.getItemProperty("deployedBy").setValue(pd.getDeployedBy());
        TableLinkButton tlb = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnRemove"), "icons/cancel.png", pd, this);
        woItem.getItemProperty("actions").setValue(tlb);
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(saveBtn)) {
                save();
                close();
            } else if (event.getButton().equals(addBtn)) {
                LightProcessDefinition lpd = (LightProcessDefinition) processesComboBox.getValue();
                if(lpd != null){
                	addTableRow(lpd);
                	processesComboBox.removeItem(lpd);
                }else{
                	showWarning(ProcessbaseApplication.getString("errorNoProcessSelected", "No process selected!"));
                }
            } else if (event.getButton().equals(deleteBtn)) {
                delete();
                
            } else if (event.getButton() instanceof TableLinkButton) {
                LightProcessDefinition lpd = (LightProcessDefinition) ((TableLinkButton) event.getButton()).getTableValue();
                table.removeItem(lpd);
                processesComboBox.addItem(lpd);
                processesComboBox.setItemCaption(lpd, lpd.getLabel() + " (version " + lpd.getVersion() + ")");
            } else {
                close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    private void save() throws Exception {
        for (Object object : table.getContainerDataSource().getItemIds()) {
            LightProcessDefinition lpd = (LightProcessDefinition) object;
            Set<String> cats = lpd.getCategoryNames();
            cats.add(category.getName());
            ProcessbaseApplication.getCurrent().getBpmModule().setProcessCategories(lpd.getUUID(), cats);
        }
        for (Object object : processesComboBox.getItemIds()) {
            LightProcessDefinition lpd = (LightProcessDefinition) object;
            Set<String> cats2 = lpd.getCategoryNames();
            cats2.remove(category.getName());
            ProcessbaseApplication.getCurrent().getBpmModule().setProcessCategories(lpd.getUUID(), cats2);
        }
    }

    private void delete() {
        final BPMModule bpmModule =  ProcessbaseApplication.getCurrent().getBpmModule();
        ConfirmDialog.show(getApplication().getMainWindow(),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("windowCaptionConfirm"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("questionDeleteCategory"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnYes"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
							try {
								for (Object object : table.getContainerDataSource().getItemIds()) {
									LightProcessDefinition lpd = (LightProcessDefinition) object;
									Set<String> cats = lpd.getCategoryNames();
									cats.remove(category.getName());
									ProcessbaseApplication.getCurrent().getBpmModule().setProcessCategories(lpd.getUUID(), cats);
								}

								Set<String> cats = new HashSet<String>();
								cats.add(category.getName());
								bpmModule.deleteCategories(cats);
								close();
							} catch (Exception ex) {
								ex.printStackTrace();
								throw new RuntimeException(ex);
							}
                        }
                    }
                });
    }
}
