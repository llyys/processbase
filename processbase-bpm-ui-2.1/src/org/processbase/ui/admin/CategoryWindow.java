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
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.TableLinkButton;
import org.vaadin.dialogs.ConfirmDialog;

/**
 *
 * @author mgubaidullin
 */
public class CategoryWindow extends PbWindow implements ClickListener {

    private Category category = null;
    private ButtonBar bar = new ButtonBar();
    private ButtonBar buttons = new ButtonBar();
    private Button deleteBtn = new Button(PbPortlet.getCurrent().messages.getString("btnDelete"), this);
    private Button cancelBtn = new Button(PbPortlet.getCurrent().messages.getString("btnCancel"), this);
    private Button saveBtn = new Button(PbPortlet.getCurrent().messages.getString("btnSave"), this);
    private Button addBtn = new Button(PbPortlet.getCurrent().messages.getString("btnAdd"), this);
//    private Button addBtn = new Button(ProcessbasePortlet.getCurrent().messages.getString("btnAdd"), this);
    private Table table = new Table();
    private ComboBox processesComboBox = new ComboBox(PbPortlet.getCurrent().messages.getString("processToCategory"));

    public CategoryWindow(Category category) {
        super(category.getName());
        this.category = category;
        initTableUI();
    }

    public void exec() {
        try {
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);


            processesComboBox.setFilteringMode(Filtering.FILTERINGMODE_CONTAINS);
//            processesComboBox.setItemCaptionPropertyId("name");
            processesComboBox.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_EXPLICIT);
            processesComboBox.setWidth("100%");
            bar.setWidth("100%");
            bar.addComponent(processesComboBox);
            bar.setExpandRatio(processesComboBox, 1);
            bar.addComponent(addBtn);
            bar.setComponentAlignment(addBtn, Alignment.BOTTOM_RIGHT);

            layout.addComponent(bar);
            layout.addComponent(table);

            refreshTable();

            deleteBtn.setDescription(PbPortlet.getCurrent().messages.getString("deleteCategory"));
            buttons.addButton(deleteBtn);
            buttons.setComponentAlignment(deleteBtn, Alignment.MIDDLE_RIGHT);
            buttons.addButton(saveBtn);
            buttons.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(saveBtn, 1);
            buttons.addButton(cancelBtn);
            buttons.setComponentAlignment(cancelBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(false);
            buttons.setHeight("30px");
            buttons.setWidth("100%");
            addComponent(buttons);
            setWidth("70%");
//            setHeight("70%");
            setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void initTableUI() {
        table.addContainerProperty("name", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionProcessName"), null, null);
        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("version", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionVersion"), null, null);
        table.setColumnWidth("version", 50);
        table.addContainerProperty("deployedBy", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionDeployedBy"), null, null);
        table.addContainerProperty("actions", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 50);
        table.setSelectable(false);
        table.setImmediate(true);
        table.setWidth("100%");
        table.setPageLength(10);
    }

    public void refreshTable() {
        try {
            table.removeAllItems();
            Collection<LightProcessDefinition> processes = PbPortlet.getCurrent().bpmModule.getLightProcessDefinitions(ProcessState.ENABLED);

            for (LightProcessDefinition pd : processes) {
                if (pd.getCategoryNames().contains(category.getName())) {
                    addTableRow(pd);
                } else {
                    Item woItem = processesComboBox.addItem(pd);
                    processesComboBox.setItemCaption(pd, pd.getLabel() + " (version " + pd.getVersion() + ")");
                }
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(true);
            table.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    private void addTableRow(LightProcessDefinition pd) {
        Item woItem = table.addItem(pd);
        woItem.getItemProperty("name").setValue(pd.getLabel());
        woItem.getItemProperty("version").setValue(pd.getVersion());
        woItem.getItemProperty("deployedBy").setValue(pd.getDeployedBy());
        TableLinkButton tlb = new TableLinkButton(PbPortlet.getCurrent().messages.getString("btnRemove"), "icons/cancel.png", pd, this);
        woItem.getItemProperty("actions").setValue(tlb);
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(saveBtn)) {
                save();
                close();
            } else if (event.getButton().equals(addBtn)) {
                LightProcessDefinition lpd = (LightProcessDefinition) processesComboBox.getValue();
                addTableRow(lpd);
                processesComboBox.removeItem(lpd);
            } else if (event.getButton().equals(deleteBtn)) {
                delete();
                close();
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
        }
    }

    private void save() throws Exception {
        for (Object object : table.getContainerDataSource().getItemIds()) {
            LightProcessDefinition lpd = (LightProcessDefinition) object;
            Set<String> cats = lpd.getCategoryNames();
            cats.add(category.getName());
            PbPortlet.getCurrent().bpmModule.setProcessCategories(lpd.getUUID(), cats);
        }
        for (Object object : processesComboBox.getItemIds()) {
            LightProcessDefinition lpd = (LightProcessDefinition) object;
            Set<String> cats2 = lpd.getCategoryNames();
            cats2.remove(category.getName());
            PbPortlet.getCurrent().bpmModule.setProcessCategories(lpd.getUUID(), cats2);
        }
    }

    private void delete() {
        ConfirmDialog.show(PbPortlet.getCurrent().getMainWindow(),
                PbPortlet.getCurrent().messages.getString("windowCaptionConfirm"),
                PbPortlet.getCurrent().messages.getString("questionDeleteCategory"),
                PbPortlet.getCurrent().messages.getString("btnYes"),
                PbPortlet.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                Set<String> cats = new HashSet<String>();
                                cats.add(category.getName());
                                PbPortlet.getCurrent().bpmModule.deleteCategories(cats);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
}
