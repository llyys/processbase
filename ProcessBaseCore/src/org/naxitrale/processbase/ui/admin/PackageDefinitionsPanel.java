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
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import java.util.Set;
import org.naxitrale.processbase.ui.template.TableExecButton;
import org.naxitrale.processbase.ui.template.TableExecButtonBar;
import org.naxitrale.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.def.majorElement.PackageDefinition;
import org.ow2.bonita.facade.uuid.PackageDefinitionUUID;

/**
 *
 * @author mgubaidullin
 */
public class PackageDefinitionsPanel extends TablePanel {

    Button deployBtn = new Button("Загрузить");

    public PackageDefinitionsPanel() {
        super();
        buttonBar.addComponent(deployBtn, 1);
        buttonBar.setComponentAlignment(deployBtn, Alignment.MIDDLE_LEFT);
        deployBtn.addListener(new Button.ClickListener() {

            public void buttonClick(Button.ClickEvent event) {
                UploadPackageWindow uploadPackageWindow = new UploadPackageWindow(null);
                uploadPackageWindow.addListener(new Window.CloseListener() {

                    public void windowClose(CloseEvent e) {
                        refreshTable();
                    }
                });

                getApplication().getMainWindow().addWindow(uploadPackageWindow);

            }
        });
        refreshTable();
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        table.addContainerProperty("UUID", String.class, null, "UUID", null, null);
        table.addContainerProperty("name", String.class, null, "Имя процесса", null, null);
        table.addContainerProperty("version", String.class, null, "Версия", null, null);
        table.addContainerProperty("author", String.class, null, "Автор", null, null);
        table.addContainerProperty("desc", String.class, null, "Описание", null, null);
        table.addContainerProperty("status", String.class, null, "Статус", null, null);
        table.addContainerProperty("operation", TableExecButtonBar.class, null, "Операции", null, null);

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
            tebb.addButton((TableExecButton) deleteInstancesButton(pd));
            tebb.addButton((TableExecButton) deleteAllButton(pd));
            woItem.getItemProperty("operation").setValue(tebb);
        }
        table.setSortContainerPropertyId("name");
        table.setSortAscending(false);
        table.sort();

    }

    private Button deleteAllButton(Object tableValue) {
        TableExecButton startB = new TableExecButton("Удалить шаблон и все экземпляры", "icons/Delete.png", tableValue, new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                try {
                    PackageDefinition pd = (PackageDefinition) ((TableExecButton) event.getButton()).getTableValue();
                    PackageDefinitionUUID pdUUID = pd.getUUID();
                    adminModule.deletePackage(pdUUID);
                    refreshTable();
                    getWindow().showNotification("Внимание", "Удаление завершено успешно", Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception ex) {
                    getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        return startB;
    }

    private Button deleteInstancesButton(Object tableValue) {
        TableExecButton startB = new TableExecButton("Удалить все экземпляры", "icons/cross.png", tableValue, new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                try {
                    PackageDefinition pd = (PackageDefinition) ((TableExecButton) event.getButton()).getTableValue();
                    PackageDefinitionUUID pdUUID = pd.getUUID();
                    adminModule.deleteAllProcessInstances(pdUUID);
                    refreshTable();
                    getWindow().showNotification("Внимание", "Удаление завершено успешно", Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception ex) {
                    getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });

        return startB;
    }

    private Button addResourceButton(Object tableValue) {
        TableExecButton startB = new TableExecButton("Добавить ресурс", "icons/Edit.gif", tableValue, new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                PackageDefinition pd = (PackageDefinition) ((TableExecButton) event.getButton()).getTableValue();
                PackageDefinitionUUID pdUUID = pd.getUUID();
                UploadPackageWindow uploadPackageWindow = new UploadPackageWindow(pdUUID);
                uploadPackageWindow.addListener(new Window.CloseListener() {

                    public void windowClose(CloseEvent e) {
                        refreshTable();
                    }
                });

                getApplication().getMainWindow().addWindow(uploadPackageWindow);
            }
        });
        return startB;
    }
}
