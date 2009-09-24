/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.acl;

import com.vaadin.data.Item;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pbgroup;
import org.naxitrale.processbase.ui.template.TableExecButton;
import org.naxitrale.processbase.ui.template.TableExecButtonBar;
import org.naxitrale.processbase.ui.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class GroupsPanel extends TablePanel {

    Button addBtn = new Button("Новый");

    public GroupsPanel() {
        super();
        buttonBar.addComponent(addBtn, 0);
        buttonBar.setComponentAlignment(addBtn, Alignment.MIDDLE_RIGHT);
        addBtn.addListener(new Button.ClickListener() {

            public void buttonClick(Button.ClickEvent event) {
                GroupWindow groupWindow = new GroupWindow(null);
                groupWindow.exec();
                groupWindow.addListener(new Window.CloseListener() {

                    public void windowClose(CloseEvent e) {
                        refreshTable();
                    }
                });

                getApplication().getMainWindow().addWindow(groupWindow);

            }
        });
        refreshTable();
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            table.addContainerProperty("name", String.class, null, "Имя группы", null, null);
            table.addContainerProperty("email", String.class, null, "Email", null, null);
            table.addContainerProperty("operation", TableExecButtonBar.class, null, "Операции", null, null);

            HibernateUtil hutil = new HibernateUtil();
            List<Pbgroup> groups = hutil.findAllGroups("APP");
            for (Pbgroup group : groups) {
                Item woItem = table.addItem(group);
                woItem.getItemProperty("name").setValue(group.getGroupname());
                woItem.getItemProperty("email").setValue(group.getGroupemail());
                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(getExecBtn("Участники", "icons/MembershipSelector.gif", group));
                tebb.addButton(getExecBtn("Редактировать", "icons/Edit.gif", group));
                tebb.addButton(getExecBtn("Удалить", "icons/Delete.png", group));
                woItem.getItemProperty("operation").setValue(tebb);
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public TableExecButton getExecBtn(String description, String iconName, Pbgroup group) {
        TableExecButton execBtn = new TableExecButton(description, iconName, group, new ClickListener() {

            public void buttonClick(ClickEvent event) {
                try {
                    Pbgroup g = (Pbgroup) ((TableExecButton) event.getButton()).getTableValue();
                    if (event.getButton().getDescription().equalsIgnoreCase("Удалить")) {
                        HibernateUtil hutil = new HibernateUtil();
                        hutil.delete(g);
                    } else if (event.getButton().getDescription().equalsIgnoreCase("Редактировать")) {
                        GroupWindow groupWindow = new GroupWindow(g);
                        groupWindow.exec();
                        groupWindow.addListener(new Window.CloseListener() {

                            public void windowClose(CloseEvent e) {
                                refreshTable();
                            }
                        });
                        getApplication().getMainWindow().addWindow(groupWindow);
                    } else if (event.getButton().getDescription().equalsIgnoreCase("Участники")) {
                        GroupMembershipWindow groupMembershipWindow = new GroupMembershipWindow(g);
                        groupMembershipWindow.exec();
                        groupMembershipWindow.addListener(new Window.CloseListener() {

                            public void windowClose(CloseEvent e) {
                                refreshTable();
                            }
                        });
                        getApplication().getMainWindow().addWindow(groupMembershipWindow);
                    }
                    refreshTable();
                } catch (Exception ex) {
                    Logger.getLogger(GroupsPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
                    getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        return execBtn;
    }
}
