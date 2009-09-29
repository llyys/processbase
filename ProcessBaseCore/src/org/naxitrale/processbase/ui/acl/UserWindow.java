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
import com.vaadin.ui.Select;
import com.vaadin.ui.VerticalLayout;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pborg;
import org.naxitrale.processbase.persistence.entity.Pbuser;
import org.naxitrale.processbase.ui.template.ACLFieldFactory;
import org.naxitrale.processbase.ui.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class UserWindow extends PbWindow implements ClickListener {

    private Pbuser user = null;
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button cancelBtn = new Button(messages.getString("btnCancel"), this);
    private Button applyBtn = new Button(messages.getString("btnOK"), this);
    private boolean isNew = true;
    private BeanItem userBean = null;
    private Vector order = new Vector();
    private Vector fields = new Vector();
    private Form form = new Form();
    private HibernateUtil hutil = new HibernateUtil();

    public UserWindow(Pbuser user) {
        super();
        this.user = user;
        if (this.user != null) {
            this.isNew = false;
        }
    }

    public void exec() {
        try {
            if (isNew) {
                user = new Pbuser();
            }
            setCaption(messages.getString("user"));
            order.add("username");
            order.add("password");
            order.add("lastname");
            order.add("firstname");
            order.add("middlename");
            order.add("birthdate");
            order.add("email");
            order.add("position");
            order.add("pborgs");
            fields.addAll(order);
            userBean = new BeanItem(user, fields);
            form.setItemDataSource(userBean);
            form.setFormFieldFactory(new ACLFieldFactory(messages));
            form.setVisibleItemProperties(order);
            if (user.getPborgs() != null) {
                Collection<Pborg> c = ((Select) form.getField("pborgs")).getItemIds();
                for (Pborg org : c) {
                    if (user.getPborgs().getId() == org.getId()) {
                        ((Select) form.getField("pborgs")).setValue(org);
                    }
                }
            }
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
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }

    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(cancelBtn)) {
                form.discard();
            } else {
                form.commit();
                user.setPbtype("APP");
                user.setLanguage("RU");
                if (isNew) {
                    hutil.saveUser(user);
                } else {
                    hutil.mergeUser(user);
                }
            }
            close();
        } catch (Exception ex) {
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }

    }
}
