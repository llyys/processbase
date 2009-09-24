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
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.Window.Notification;
import java.util.List;
import java.util.Set;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pborg;
import org.naxitrale.processbase.persistence.entity.Pbuser;
import org.naxitrale.processbase.ui.template.WorkPanel;

/**
 *
 * @author mgubaidullin
 */
public class OrgUnitsPanel extends WorkPanel implements ClickListener, Window.CloseListener, ValueChangeListener {

    private VerticalLayout orgDetailLayout = new VerticalLayout();
    private Tree tree = new Tree();
    private HierarchicalContainer hierContainer = new HierarchicalContainer();
    private Button addBtn = new Button("Новое", this);
    private Button updateBtn = new Button("Изменить", this);
    private Table employeeTable = new Table();
    private HibernateUtil hutil = new HibernateUtil();
    private Label managerField = new Label();

    public OrgUnitsPanel() {
        super();
        initUI();
    }

    public void initUI() {
        buttonBar.addComponent(addBtn, 0);
        buttonBar.addComponent(updateBtn, 0);

        tree.setSizeFull();
        employeeTable.setSizeFull();
        managerField.setSizeFull();
        managerField.setContentMode(Label.CONTENT_XHTML);

//        grid.setSizeFull();
        orgDetailLayout.addComponent(managerField);
        orgDetailLayout.addComponent(employeeTable);
        orgDetailLayout.setSizeFull();

        horizontalLayout.addComponent(tree);
        horizontalLayout.setComponentAlignment(tree, Alignment.TOP_LEFT);
        horizontalLayout.setExpandRatio(tree, 1);

        horizontalLayout.addComponent(orgDetailLayout);
        horizontalLayout.setComponentAlignment(orgDetailLayout, Alignment.TOP_LEFT);
        horizontalLayout.setExpandRatio(orgDetailLayout, 2);
    }

    public void refreshTree() {
        try {
            tree.removeAllItems();
            tree.setImmediate(true);
            tree.addListener(this);
            hierContainer.addContainerProperty("orgunit", String.class, null);
            List<Pborg> orgs = hutil.findAllOrgUnits();
            for (Pborg org : orgs) {
                Item item = hierContainer.addItem(org);
                item.getItemProperty("orgunit").setValue(org);
                if (org.getPborgs() != null) {
                    hierContainer.setParent(org, org.getPborgs());
                }
                if (org.getPborgses().size() > 0) {
                    hierContainer.setChildrenAllowed(org, true);
                } else {
                    hierContainer.setChildrenAllowed(org, false);
                }
            }
            tree.setContainerDataSource(hierContainer);
            tree.setItemCaptionPropertyId("orgunit");
            tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
            for (Object id : tree.rootItemIds()) {
                tree.expandItemsRecursively(id);
            }
            tree.setValue(orgs.get(0));
        } catch (Exception ex) {
            getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void refreshOrgDetails() {
        if (tree.getValue() != null) {
            try {
                managerField.setValue("Руководитель <b>" + ((Pborg) tree.getValue()).getPbusers() + "</b>");
                employeeTable.removeAllItems();
                employeeTable.addContainerProperty("username", String.class, null, "Имя пользователя", null, null);
                employeeTable.addContainerProperty("lastname", String.class, null, "Фамилия", null, null);
                employeeTable.addContainerProperty("firstname", String.class, null, "Имя", null, null);
                employeeTable.addContainerProperty("email", String.class, null, "Email", null, null);
                Set<Pbuser> users = hutil.getUsersByOrg((Pborg) tree.getValue());
                for (Pbuser user : users) {
                    Item woItem = employeeTable.addItem(user);
                    woItem.getItemProperty("username").setValue(user.getUsername());
                    woItem.getItemProperty("lastname").setValue(user.getLastname());
                    woItem.getItemProperty("firstname").setValue(user.getFirstname());
                    woItem.getItemProperty("email").setValue(user.getEmail());
                }
                employeeTable.setSortContainerPropertyId("username");
                employeeTable.setSortAscending(false);
                employeeTable.sort();
            } catch (Exception ex) {
                getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton().equals(refreshBtn)) {
            refreshTree();
        } else if (event.getButton().equals(addBtn) && tree.getValue() != null) {
            OrgWindow orgWindow = new OrgWindow(null, (Pborg) tree.getValue());
            orgWindow.exec();
            orgWindow.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(orgWindow);
        } else if (event.getButton().equals(updateBtn) && tree.getValue() != null) {
            OrgWindow orgWindow = new OrgWindow((Pborg) tree.getValue(), ((Pborg) tree.getValue()).getPborgs());
            orgWindow.exec();
            orgWindow.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(orgWindow);
        }
    }

    @Override
    public void windowClose(CloseEvent e) {
        refreshTree();
    }

    public void valueChange(ValueChangeEvent event) {
        refreshOrgDetails();
    }
}
