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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.ui.template.TableExecButton;
import org.naxitrale.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;

/**
 *
 * @author mgubaidullin
 */
public class ActivityInstancesPanel extends TablePanel {

    public ActivityInstancesPanel() {
        super();
    }

    public void refreshTable() {
        try {
            table.removeAllItems();
            table.addContainerProperty("UUID", String.class, null, "UUID", null, null);
            table.addContainerProperty("name", String.class, null, "Имя шага", null, null);
            table.addContainerProperty("type", String.class, null, "Тип", null, null);
//            table.addContainerProperty("performer", String.class, null, "Исполнитель", null, null);
            table.addContainerProperty("readyDate", Date.class, null, "Дата создания", null, null);
            table.addContainerProperty("startedDate", Date.class, null, "Дата начала", null, null);
            table.addContainerProperty("endDate", Date.class, null, "Дата завершения", null, null);
//            table.addContainerProperty("iteration", String.class, null, "Итерация", null, null);
            table.addContainerProperty("status", String.class, null, "Статус", null, null);
            table.addContainerProperty("operation", Button.class, null, "Операции", null, null);

            Set<ActivityInstance<ActivityBody>> ais = adminModule.getActivityInstances();
            for (ActivityInstance<ActivityBody> ai : ais) {
                Item woItem = table.addItem(ai);
                woItem.getItemProperty("UUID").setValue(ai.getUUID().toString());
                woItem.getItemProperty("name").setValue(ai.getActivityId());
                woItem.getItemProperty("readyDate").setValue(ai.getBody().getReadyDate());
                woItem.getItemProperty("startedDate").setValue(ai.getBody().getStartedDate());
                woItem.getItemProperty("endDate").setValue(ai.getBody().getEndedDate());
                woItem.getItemProperty("status").setValue(ai.getBody().getState());
//                woItem.getItemProperty("iteration").setValue(ai.);
                woItem.getItemProperty("type").setValue(adminModule.getProcessActivityDefinition(ai).getPerformer() == null ? "Автомат" : "Задача");
                woItem.getItemProperty("operation").setValue(doSomething("Открыть", "icons/Gear2.gif", ai));
            }
            table.setSortContainerPropertyId("readyDate");
            table.setSortAscending(false);
            table.sort();
            table.setColumnWidth("UUID", 30);
        } catch (Exception ex) {
            getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
        }

    }

    public TableExecButton doSomething(String description, String iconName, Object t) {
        TableExecButton startBtn = new TableExecButton(description, iconName, t, new ClickListener() {

            public void buttonClick(ClickEvent event) {
                try {
                    ActivityInstance<TaskInstance> task = (ActivityInstance<TaskInstance>) ((TableExecButton) event.getButton()).getTableValue();
                    ActivityWindow activityWindow = adminModule.getActivityWindow(task);
                    activityWindow.addListener(new Window.CloseListener() {

                        public void windowClose(CloseEvent e) {
                            refreshTable();
                        }
                    });
                    getApplication().getMainWindow().addWindow(activityWindow);

                    refreshTable();
                } catch (Exception ex) {
                    Logger.getLogger(ActivityInstancesPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                    getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        return startBtn;
    }
}
