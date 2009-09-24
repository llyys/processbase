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

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pborg;
import org.naxitrale.processbase.persistence.entity.Pbuser;
import org.naxitrale.processbase.ui.template.ACLFieldFactory;

/**
 *
 * @author mgubaidullin
 */
public class OrgWindow extends Window implements ClickListener {

    public Pborg org = null;
    public Pborg parent = null;
    public HorizontalLayout buttons = new HorizontalLayout();
    public Button cancelBtn = new Button("Отменить", this);
    public Button applyBtn = new Button("OK", this);
    public boolean isNew = true;
    public BeanItem orgBean = null;
    private Vector order = new Vector();
    private Vector fields = new Vector();
    private Form form = new Form();
    private HibernateUtil hutil = new HibernateUtil();
    private Select managerSelector = new Select("Руководитель");

    public OrgWindow(Pborg org, Pborg parent) {
        super();
        this.org = org;
        if (this.org != null) {
            this.isNew = false;
        }
        this.parent = parent;
    }

    public void exec() {
        try {
            if (isNew) {
                org = new Pborg();
                org.setPborgs(parent);
                setCaption("Новое подразделение");
                addComponent(new Label("в подразделении \"" + parent.getOrgName() + "\""));
            } else {
                setCaption("Подразделение");
            }
            fields.add("orgName");
            orgBean = new BeanItem(org, fields);
//            fileds.add("pbusers");
            form.setItemDataSource(orgBean);
            order.add("orgName");
//            order.add("pbusers");
            form.setFormFieldFactory(new ACLFieldFactory());
            form.setVisibleItemProperties(order);
            form.setWriteThrough(false);
            form.setInvalidCommitted(false);
            managerSelector.setFilteringMode(Select.FILTERINGMODE_CONTAINS);
            managerSelector.setNewItemsAllowed(false);
            managerSelector.setNullSelectionAllowed(false);
            managerSelector.setWidth("250px");
            managerSelector.setRequired(true);
            managerSelector.setRequiredError("Обязательное поле!");
            managerSelector.removeAllItems();
            List<Pbuser> users = hutil.findAllPbusers("APP");
            for (Pbuser user : users) {
                managerSelector.addItem(user);
                if (!isNew && org.getPbusers() != null && user.getId() == org.getPbusers().getId()) {
                    managerSelector.setValue(user);
                }
            }
            form.getLayout().addComponent(managerSelector);
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setSizeUndefined();
            buttons.setSpacing(true);
            buttons.addComponent(applyBtn);
            buttons.addComponent(cancelBtn);
            form.getLayout().addComponent(buttons);

            addComponent(form);
            setResizable(false);
        } catch (Exception ex) {
            Logger.getLogger(OrgWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            getWindow().showNotification("Ошибка", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }

    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(cancelBtn)) {
            form.discard();
        } else {
            form.commit();
            org.setPbusers((Pbuser) managerSelector.getValue());
            hutil.merge(org);
        }
        close();
    }
}
