/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.preferences;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Form;
import com.vaadin.ui.TextField;
import java.util.Vector;
import org.processbase.ProcessBase;
import org.processbase.acl.persistence.Pbuser;
import org.processbase.ui.template.ACLFieldFactory;
import org.processbase.ui.template.WorkPanel;

/**
 *
 * @author mgubaidullin
 */
public class CurrentUserPanel extends WorkPanel {

    private Vector order = new Vector();
    private Vector fields = new Vector();
    private Form userForm = new Form();
    private BeanItem userBean = null;
    private Pbuser user = ((ProcessBase) getApplication()).getCurrent().getUser().getPbuser();
    private Button changePwdBtn = new Button(messages.getString("btnChangePassword"), this);

    public CurrentUserPanel() {
        super();
        initUI();
    }

    public void initUI() {
        // prepare UserForm
        order.add("username");
        order.add("lastname");
        order.add("firstname");
        order.add("middlename");
        order.add("birthdate");
        order.add("email");
        order.add("position");
        order.add("pborgs");
//        order.add("language");
        fields.addAll(order);
        userForm.setReadOnly(true);
        userBean = new BeanItem(user, fields);
        userForm.setItemDataSource(userBean);
        userForm.setFormFieldFactory(new ACLFieldFactory(messages));
        userForm.setVisibleItemProperties(order);
        ((TextField) userForm.getField("pborgs")).setValue(user.getPborgs().getOrgName());
        userForm.setReadOnly(true);
        // add user panel
        horizontalLayout.setMargin(false, true, true, true);
//        horizontalLayout.setSpacing(true);
        horizontalLayout.addComponent(userForm);
        horizontalLayout.setComponentAlignment(userForm, Alignment.TOP_LEFT);
        horizontalLayout.setExpandRatio(userForm, 1);

        buttonBar.removeButton(refreshBtn);
        buttonBar.addButton(changePwdBtn, 0);
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton().equals(changePwdBtn)) {
            ChangePasswordWindow changePasswordWindow = new ChangePasswordWindow(user);
            changePasswordWindow.exec();
            getApplication().getMainWindow().addWindow(changePasswordWindow);
        }
    }
}
