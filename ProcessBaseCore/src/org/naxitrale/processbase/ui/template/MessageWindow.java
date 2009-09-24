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

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

/**
 *
 * @author mgubaidullin
 */
public class MessageWindow extends Window implements Button.ClickListener {

    private ButtonBar buttonBar = new ButtonBar();
    private Button okButton = new Button("ОК", this);
    private Button cancelButton = new Button("Отмена", this);
    private Button yesButton = new Button("Да", this);
    private Button noButton = new Button("Нет", this);
    private Label textLabel = null;
    private String message = null;
    private int windowStyle = 0;
    public static int INFO_STYLE = 0;
    public static int CONFIRM_STYLE = 1;
    public static int ERROR_STYLE = 2;

    public MessageWindow(String message, int windowStyle) {
        super();
        this.windowStyle = windowStyle;
        this.message = message;
        initUI();
    }

    public void initUI() {
        textLabel = new Label("<span>" + message + "</span>", Label.CONTENT_XHTML);
        addComponent(textLabel);
        if (windowStyle == INFO_STYLE) {
            setCaption("Информация");
            buttonBar.addButton(okButton);
        } else if (windowStyle == CONFIRM_STYLE) {
            setCaption("Подтвердите");
            buttonBar.addButton(yesButton);
            buttonBar.addButton(noButton);
        } else if (windowStyle == ERROR_STYLE) {
            setCaption("Ошибка");
            buttonBar.addButton(okButton);
        }
        addComponent(buttonBar);
        setModal(true);
        setWidth("300px");
        setHeight("200px");
        setResizable(false);
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(okButton)) {
            close();
        } else if (event.getButton().equals(yesButton)) {
            close();
        } else if (event.getButton().equals(noButton)) {
            close();
        } else if (event.getButton().equals(cancelButton)) {
            close();
        }
    }
}
