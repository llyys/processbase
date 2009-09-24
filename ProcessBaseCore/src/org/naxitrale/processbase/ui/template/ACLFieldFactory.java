/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.template;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pborg;
import org.naxitrale.processbase.persistence.entity.Pbuser;

/**
 *
 * @author mgubaidullin
 */
public class ACLFieldFactory implements FormFieldFactory {

    private Form currentForm = null;
    private HibernateUtil hutil = new HibernateUtil();

    public Field createField(Item item, Object propertyId, Component uiContext) {
        if (uiContext instanceof Form) {
            currentForm = (Form) uiContext;
        }
        String pid = (String) propertyId;
        if (pid.equals("lastname")) {
            return getTextField("Фамилия", "", true);
        } else if (pid.equals("middlename")) {
            return getTextField("Отчество", "", false);
        } else if (pid.equals("firstname")) {
            return getTextField("Имя", "", true);
        } else if (pid.equals("username")) {
            return getTextField("Имя пользователя", "", true);
        } else if (pid.equals("password")) {
            TextField password = getTextField("Пароль", "", true);
            password.setSecret(true);
            return password;
        } else if (pid.equals("birthdate")) {
            return getDateField("Дата рождения", "", true);
        } else if (pid.equals("email")) {
            return getTextField("Email", "", true);
        } else if (pid.equals("groupemail")) {
            return getTextField("Email", "", true);
        } else if (pid.equals("groupname")) {
            return getTextField("Название группы", "", true);
        } else if (pid.equals("rolename")) {
            return getTextField("Название роли", "", true);
        } else if (pid.equals("position")) {
            return getTextField("Должность", "", false);
        } else if (pid.equals("orgName")) {
            return getTextField("Название подразделения", "", true);
        } else if (pid.equals("pbusers")) {
            return getUserSelector("Руководитель", true);
        } else if (pid.equals("pborgs")) {
            return getOrgSelector("Подразделение", item, true);
        }
        return null;
    }

    private TextField getTextField(String caption, String value, boolean required) {
        TextField tf = new TextField(caption, value);
        tf.setNullRepresentation("");
        tf.setWidth("250px");
        if (required) {
            tf.setRequired(true);
            tf.setRequiredError("Обязательное поле!");
        }
        return tf;
    }

    private Field getDateField(String caption, String value, boolean required) {
        if (currentForm != null && !currentForm.isReadOnly()) {
            PopupDateField df = new PopupDateField((caption));
            df.setResolution(DateField.RESOLUTION_DAY);
            df.setValue(new Date());
            df.setRequired(true);
            df.setRequiredError("Обязательное поле!");
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
                userSelector.setRequiredError("Обязательное поле!");
            }
            userSelector.removeAllItems();
            List<Pbuser> users = hutil.findAllPbusers("APP");
            for (Pbuser user : users) {
                userSelector.addItem(user);
            }
            return userSelector;
        } else {
            TextField tf = new TextField(caption);
            tf.setNullRepresentation("");
            tf.setWidth("250px");
            return tf;
        }
    }

    private Field getOrgSelector(String caption, Item item, boolean required) {
        if (currentForm != null && !currentForm.isReadOnly()) {
            Select orgSelector = new Select(caption);
            orgSelector.setFilteringMode(Select.FILTERINGMODE_CONTAINS);
            orgSelector.setNewItemsAllowed(false);
            orgSelector.setNullSelectionAllowed(false);
            orgSelector.setWidth("250px");
            if (required) {
                orgSelector.setRequired(true);
                orgSelector.setRequiredError("Обязательное поле!");
            }
            Pborg currentOrg = null;
            if (((BeanItem) item).getBean() instanceof Pbuser) {
                Pbuser user = (Pbuser) ((BeanItem) item).getBean();
                if (user.getPborgs() != null) {
                    currentOrg = user.getPborgs();
                }
            }
            orgSelector.removeAllItems();
            List<Pborg> orgs = hutil.findAllOrgUnits();
            for (Pborg org : orgs) {
                orgSelector.addItem(org);
                if (currentOrg != null && currentOrg.getId() == org.getId()) {
                    orgSelector.setValue(org);
                }
            }
            return orgSelector;
        } else {
            TextField tf = new TextField(caption);
            tf.setNullRepresentation("");
            tf.setWidth("250px");
            return tf;
        }
    }
}

