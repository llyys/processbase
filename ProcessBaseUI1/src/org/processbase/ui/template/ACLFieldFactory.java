/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.template;

import com.vaadin.data.Item;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import java.util.Date;
import java.util.ResourceBundle;

/**
 *
 * @author mgubaidullin
 */
public class ACLFieldFactory implements FormFieldFactory {

    protected ResourceBundle messages = null;
    private Form currentForm = null;

    public ACLFieldFactory(ResourceBundle messages) {
        super();
        this.messages = messages;
    }

    public Field createField(Item item, Object propertyId, Component uiContext) {
        if (uiContext instanceof Form) {
            currentForm = (Form) uiContext;
        }
        String pid = (String) propertyId;
        if (pid.equals("sn")) {
            return getTextField(messages.getString("fieldLastname"), "", true);
        } else if (pid.equals("middlename")) {
            return getTextField(messages.getString("fieldMiddlename"), "", false);
        } else if (pid.equals("givenName")) {
            return getTextField(messages.getString("fieldFirstname"), "", true);
        } else if (pid.equals("uid")) {
            return getTextField(messages.getString("fieldUsername"), "", true);
        } else if (pid.equals("password")) {
            TextField password = getTextField(messages.getString("fieldPassword"), "", true);
            password.setSecret(true);
            return password;
        } else if (pid.equals("birthdate")) {
            return getDateField(messages.getString("fieldBirthdate"), "", true);
        } else if (pid.equals("email") | pid.equals("mail")) {
            return getTextField(messages.getString("fieldEmail"), "", true);
        } else if (pid.equals("groupemail")) {
            return getTextField(messages.getString("fieldEmail"), "", true);
        } else if (pid.equals("groupname")) {
            return getTextField(messages.getString("fieldGroupname"), "", true);
        } else if (pid.equals("rolename")) {
            return getTextField(messages.getString("fieldRolename"), "", true);
        } else if (pid.equals("position")) {
            return getTextField(messages.getString("fieldPosition"), "", false);
        } else if (pid.equals("orgName")) {
            return getTextField(messages.getString("fieldOrgUnit"), "", true);
        } else if (pid.equals("pbusers")) {
            return getUserSelector(messages.getString("fieldManager"), true);
        } else if (pid.equals("language")) {
            return getTextField(messages.getString("fieldLanguage"), "", true);
        }
        return null;
    }

    private TextField getTextField(String caption, String value, boolean required) {
        TextField tf = new TextField(caption, value);
        tf.setNullRepresentation("");
        tf.setWidth("250px");
        if (required) {
            tf.setRequired(true);
            tf.setRequiredError(messages.getString("fieldRequired"));
        }
        return tf;
    }

    private Field getDateField(String caption, String value, boolean required) {
        if (currentForm != null && !currentForm.isReadOnly()) {
            PopupDateField df = new PopupDateField((caption));
            df.setResolution(DateField.RESOLUTION_DAY);
            df.setValue(new Date());
            df.setRequired(true);
            df.setRequiredError(messages.getString("fieldRequired"));
            return df;
        } else {
            TextField tf = new TextField(caption, value);
            tf.setNullRepresentation("");
            tf.setWidth("250px");
            return tf;
        }
    }

    private Field getUserSelector(String caption, boolean required) {
        if (currentForm != null && !currentForm.isReadOnly()) {
            Select userSelector = new Select(caption);
            userSelector.setFilteringMode(Select.FILTERINGMODE_CONTAINS);
            userSelector.setNewItemsAllowed(false);
            userSelector.setNullSelectionAllowed(false);
            userSelector.setWidth("250px");
            if (required) {
                userSelector.setRequired(true);
                userSelector.setRequiredError(messages.getString("fieldRequired"));
            }
            userSelector.removeAllItems();
//            List<Pbuser> users = hutil.findAllPbusers("APP");
//            for (Pbuser user : users) {
//                userSelector.addItem(user);
//            }
            return userSelector;
        } else {
            TextField tf = new TextField(caption);
            tf.setNullRepresentation("");
            tf.setWidth("250px");
            return tf;
        }
    }

}

