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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.Vector;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pbgroup;
import org.naxitrale.processbase.ui.template.ACLFieldFactory;

/**
 *
 * @author mgubaidullin
 */
public class GroupWindow extends Window implements ClickListener {

    public Pbgroup group = null;
    public HorizontalLayout buttons = new HorizontalLayout();
    public Button cancelBtn = new Button("Отменить", this);
    public Button applyBtn = new Button("OK", this);
    public boolean isNew = true;
    public BeanItem userBean = null;
    private Vector order = new Vector();
    private Vector fields = new Vector();
    private Form form = new Form();

    public GroupWindow(Pbgroup group) {
        super();
        this.group = group;
        if (this.group != null) {
            this.isNew = false;
        }
    }

    public void exec() {
        try {
            if (isNew) {
                group = new Pbgroup();
            }
            setCaption("Группа");
            order.add("groupname");
            order.add("groupemail");
            fields.addAll(order);
            userBean = new BeanItem(group, fields);
            form.setItemDataSource(userBean);
            form.setFormFieldFactory(new ACLFieldFactory());
            form.setVisibleItemProperties(order);
            form.setWriteThrough(false);
            form.setInvalidCommitted(false);
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
            getWindow().showNotification("Ошибка", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }

    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(cancelBtn)) {
            form.discard();
        } else {
            form.commit();
            group.setPbtype("APP");
            HibernateUtil hutil = new HibernateUtil();
            hutil.merge(group);
        }
        close();
    }
}
