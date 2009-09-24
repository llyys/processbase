/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.preferences;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.naxitrale.processbase.persistence.controller.HibernateUtil;
import org.naxitrale.processbase.persistence.entity.Pbuser;
import org.naxitrale.processbase.util.PasswordService;

/**
 *
 * @author mgubaidullin
 */
public class ChangePasswordWindow extends Window implements ClickListener {

    private Pbuser user = null;
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button cancelBtn = new Button("Отменить", this);
    private Button applyBtn = new Button("OK", this);
    private BeanItem userBean = null;
    private HibernateUtil hutil = new HibernateUtil();
    private TextField currentPassword = new TextField("Текущий пароль");
    private TextField newPassword1 = new TextField("Новый пароль");
    private TextField newPassword2 = new TextField("Повтор нового пароля");
    private FormLayout formLayout = new FormLayout();

    public ChangePasswordWindow(Pbuser user) {
        super();
        this.user = user;
    }

    public void exec() {
        try {
            setCaption("Смена пароля");
            setContent(formLayout);
            formLayout.setMargin(true);
            formLayout.setSpacing(true);
            formLayout.setSizeUndefined();
            formLayout.addComponent(currentPassword);
            formLayout.addComponent(newPassword1);
            formLayout.addComponent(newPassword2);

            currentPassword.setSecret(true);
            newPassword1.setSecret(true);
            newPassword2.setSecret(true);

            buttons.setSpacing(true);
            buttons.addComponent(applyBtn);
            buttons.addComponent(cancelBtn);
            formLayout.addComponent(buttons);
            setResizable(false);
            setModal(true);
        } catch (Exception ex) {
            Logger.getLogger(ChangePasswordWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            getWindow().showNotification("Ошибка", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }

    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(cancelBtn)) {
            } else {
                if (user.getPassword().equals(PasswordService.encrypt(currentPassword.getValue().toString()))) {
                    if (!currentPassword.getValue().toString().equals(newPassword1.getValue().toString())) {
                        if (newPassword1.getValue().toString().equals(newPassword2.getValue().toString())) {
                            user.setPassword(newPassword1.getValue().toString());
                            hutil.mergeUser(user);
                        } else {
                            throw new Exception("Новый и повтор пароля должны совпадать!");
                        }
                    } else {
                        throw new Exception("Старый и новый пароль не должны совпадать!");
                    }
                } else {
                    throw new Exception("Неверный текущий пароль!");
                }
            }
            close();
        } catch (Exception ex) {
            Logger.getLogger(ChangePasswordWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            getWindow().showNotification("Ошибка", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }

    }
}
