/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.gwt.client.ui.AlignmentInfo.Bits;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 
 * @author mgubaidullin
 */
public class LoginWindow extends Window {

    private GridLayout grid = new GridLayout(3, 1);
    private Panel panel = new Panel();
    public FormLayout form = new FormLayout();
    private Button btnLogin = new Button("Войти");
    private TextField username = new TextField("Имя пользователя");
    private TextField password = new TextField("Пароль");
    private Label labelLeft = new Label("");
    private Label labelRight = new Label("");
    private Locale locale = null;
    private ResourceBundle messages = null;

    public LoginWindow(Locale locale) {
        super("ProcessBase");
        this.locale = locale;
        this.messages = ResourceBundle.getBundle("resources/MessagesBundle", this.locale);
        setName("ProcessBase");
        initUI();
    }

    private void initUI() {
        setTheme("processbase");
        addComponent(grid);
        grid.setWidth("100%");
        grid.setHeight("100%");
        grid.addComponent(labelLeft, 0, 0);
        grid.addComponent(labelRight, 2, 0);
        grid.addComponent(panel, 1, 0);
        grid.setComponentAlignment(panel, new Alignment(Bits.ALIGNMENT_VERTICAL_CENTER | Bits.ALIGNMENT_HORIZONTAL_CENTER));
        panel.setWidth("300px");

        panel.addComponent(form);
        form.setCaption(messages.getString("loginWindowCaption"));
        password.setSecret(true);
        username.setCaption(messages.getString("loginWindowUsername"));
        form.addComponent(username);
        password.setCaption(messages.getString("loginWindowPassword"));
        form.addComponent(password);
        btnLogin.setCaption(messages.getString("btnLoginCaption"));
        form.addComponent(btnLogin);

        btnLogin.addListener(new Button.ClickListener() {

            public void buttonClick(Button.ClickEvent event) {
                btnLogintClicked();
            }
        });
        username.focus();
    }

    @SuppressWarnings("static-access")
    private void btnLogintClicked() {
        try {
            ((ProcessBase) getApplication()).getCurrent().authenticate((String) username.getValue(), (String) password.getValue());
            open(new ExternalResource(((ProcessBase) getApplication()).getCurrent().getURL()));
        } catch (Exception e) {
            showNotification(messages.getString("exceptionCaption"), e.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
    }
}

