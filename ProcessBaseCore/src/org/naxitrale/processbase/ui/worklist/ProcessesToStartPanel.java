/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.worklist;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.ui.template.TableExecButton;
import org.naxitrale.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.naxitrale.processbase.ui.template.TaskWindow;
import java.util.*;
import org.naxitrale.processbase.ProcessBase;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;

/**
 *
 * @author mgubaidullin
 */
public class ProcessesToStartPanel extends TablePanel implements Button.ClickListener {

    private HibernateUtil hutil = new HibernateUtil();

    public ProcessesToStartPanel() {
        super();
        initTableUI();
//        refreshTable();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
//        table.addContainerProperty("ID", String.class, null, "ID", null, null);
        table.addContainerProperty("name", String.class, null, "Имя процесса", null, null);
        table.addContainerProperty("desc", String.class, null, "Описание", null, null);
        table.addContainerProperty("version", String.class, null, "Версия", null, null);
        table.addContainerProperty("author", String.class, null, "Автор", null, null);
        table.addContainerProperty("state", String.class, null, "Состояние", null, null);
//        table.addContainerProperty("status", String.class, null, "Статус", null, null);
        table.addContainerProperty("operation", Button.class, null, "Операции", null, null);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();

        Set<String> processesUUIDs = hutil.getUserProcesses(((ProcessBase) getApplication()).getUser().getPbuser());
        for (String processUUID : processesUUIDs) {
            try {
                ProcessDefinitionUUID pdUUID = new ProcessDefinitionUUID(processUUID);
                ProcessDefinition pd = worklistModule.getProcessDefinition(pdUUID);
                Item woItem = table.addItem(pd);
//            woItem.getItemProperty("ID").setValue(pd.getProcessId());
                woItem.getItemProperty("name").setValue(pd.getName());
                woItem.getItemProperty("version").setValue(pd.getVersion());
                woItem.getItemProperty("author").setValue(pd.getAuthor());
                woItem.getItemProperty("desc").setValue(pd.getDescription());
                woItem.getItemProperty("state").setValue(pd.getState());
//            woItem.getItemProperty("status").setValue(pd.getPublicationStatus());
                woItem.getItemProperty("operation").setValue(new TableExecButton("Старт", "icons/Play.png", pd, this));
            } catch (Exception ex) {
                Logger.getLogger(ProcessesToStartPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                getWindow().showNotification("Ошибка", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
            }
        }
        table.setSortContainerPropertyId("name");
        table.setSortAscending(false);
        table.sort();

    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton && event.getButton().getDescription().equals("Старт")) {
            try {
                ProcessDefinition procd = (ProcessDefinition) ((TableExecButton) event.getButton()).getTableValue();
                TaskWindow taskWindow = worklistModule.getStartWindow(procd);
                getApplication().getMainWindow().addWindow(taskWindow);
            } catch (Exception ex) {
                Logger.getLogger(ProcessesToStartPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                getWindow().showNotification("Ошибка", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
            }
        }
    }
}
