/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import java.util.Set;
import org.naxitrale.processbase.Constants;
import org.naxitrale.processbase.ui.template.TableExecButton;
import org.naxitrale.processbase.ui.template.TableExecButtonBar;
import org.naxitrale.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.def.majorElement.PackageDefinition;
import org.ow2.bonita.facade.uuid.PackageDefinitionUUID;

/**
 *
 * @author mgubaidullin
 */
public class PackageDefinitionsPanel extends TablePanel implements Button.ClickListener, Window.CloseListener {

    Button deployBtn = new Button(messages.getString("btnUpload"));

    public PackageDefinitionsPanel() {
        super();
        buttonBar.addComponent(deployBtn, 1);
        buttonBar.setComponentAlignment(deployBtn, Alignment.MIDDLE_LEFT);
        deployBtn.addListener(this);
        initTableUI();
        refreshTable();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("UUID", String.class, null, "UUID", null, null);
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionProcessName"), null, null);
        table.addContainerProperty("version", String.class, null, messages.getString("tableCaptionVersion"), null, null);
        table.addContainerProperty("author", String.class, null, messages.getString("tableCaptionAuthor"), null, null);
        table.addContainerProperty("desc", String.class, null, messages.getString("tableCaptionDescription"), null, null);
        table.addContainerProperty("status", String.class, null, messages.getString("tableCaptionStatus"), null, null);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        Set<PackageDefinition> pds = adminModule.getPackageDefinitions();
        for (PackageDefinition pd : pds) {
            Item woItem = table.addItem(pd);
            woItem.getItemProperty("UUID").setValue(pd.getPackageDefinitionUUID());
            woItem.getItemProperty("name").setValue(pd.getName());
            woItem.getItemProperty("version").setValue(pd.getVersion());
            woItem.getItemProperty("author").setValue(pd.getAuthor());
            woItem.getItemProperty("desc").setValue(pd.getDescription());
            woItem.getItemProperty("status").setValue(pd.getState());
            TableExecButtonBar tebb = new TableExecButtonBar();
//            tebb.addButton((TableExecButton) addResourceButton(pd));
            tebb.addButton(new TableExecButton(messages.getString("btnDeleteInstances"), "icons/cross.png", pd, this, Constants.ACTION_DELETE_INSTANCES));
            tebb.addButton(new TableExecButton(messages.getString("btnDeteleProcessAndInstances"), "icons/Delete.png", pd, this, Constants.ACTION_DELETE_PROCESS_AND_INSTANCES));

            woItem.getItemProperty("actions").setValue(tebb);
        }
        table.setSortContainerPropertyId("name");
        table.setSortAscending(false);
        table.sort();

    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton().equals(deployBtn)) {
            UploadPackageWindow uploadPackageWindow = new UploadPackageWindow(null);
            uploadPackageWindow.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(uploadPackageWindow);
        } else if (event.getButton() instanceof TableExecButton) {
            TableExecButton execBtn = (TableExecButton) event.getButton();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE_PROCESS_AND_INSTANCES)) {
                try {
                    PackageDefinition pd = (PackageDefinition) execBtn.getTableValue();
                    PackageDefinitionUUID pdUUID = pd.getUUID();
                    adminModule.deletePackage(pdUUID);
                    refreshTable();
                    getWindow().showNotification("", messages.getString("deletedSuccessfull"), Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception ex) {
                    showError(ex.toString());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_DELETE_INSTANCES)) {
                try {
                    PackageDefinition pd = (PackageDefinition) execBtn.getTableValue();
                    PackageDefinitionUUID pdUUID = pd.getUUID();
                    adminModule.deleteAllProcessInstances(pdUUID);
                    refreshTable();
                    getWindow().showNotification("", messages.getString("deletedSuccessfull"), Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception ex) {
                    showError(ex.toString());
                }
            }
        }
    }
}
