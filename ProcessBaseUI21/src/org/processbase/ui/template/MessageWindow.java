/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.processbase.ui.template;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 * @author mgubaidullin
 */
public class MessageWindow extends PbWindow implements Button.ClickListener, Window.CloseListener {

    private HorizontalLayout buttonBar = new HorizontalLayout();
    private Button okButton = new Button(messages.getString("btnOK"), this);
    private Button cancelButton = new Button(messages.getString("btnCancel"), this);
    private Button yesButton = new Button(messages.getString("btnYes"), this);
    private Button noButton = new Button(messages.getString("btnNo"), this);
    private Label textLabel = null;
    private String message = null;
    private int windowStyle = 0;
    public static int INFO_STYLE = 0;
    public static int CONFIRM_STYLE = 1;
    public static int ERROR_STYLE = 2;
    private boolean result = false;

    public MessageWindow(String message, int windowStyle, PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
        this.windowStyle = windowStyle;
        this.message = message;
        initUI();
    }

    public void initUI() {
        textLabel = new Label("<span>" + message + "</span>", Label.CONTENT_XHTML);
        addComponent(textLabel);
        if (windowStyle == INFO_STYLE) {
            setCaption("Информация");
            buttonBar.addComponent(okButton);
        } else if (windowStyle == CONFIRM_STYLE) {
            setCaption("Подтвердите");
            buttonBar.addComponent(yesButton);
            buttonBar.addComponent(noButton);
        } else if (windowStyle == ERROR_STYLE) {
            setCaption("Ошибка");
            buttonBar.addComponent(okButton);
        }
        buttonBar.setSpacing(true);
        addComponent(buttonBar);
        ((VerticalLayout) getContent()).setComponentAlignment(buttonBar, "center");
        ((VerticalLayout) getContent()).setSpacing(true);
        setModal(true);
        setStyleName(Reindeer.WINDOW_BLACK);
        setWidth("260px");
        setResizable(false);
        setClosable(false);
        setDraggable(false);
        setCloseShortcut(KeyCode.ESCAPE, null);
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(okButton)) {
            result = true;
        } else if (event.getButton().equals(yesButton)) {
            result = true;
        } else if (event.getButton().equals(noButton)) {
            result = false;
        } else if (event.getButton().equals(cancelButton)) {
            result = false;
        }
        close();
    }

    public void windowClose(CloseEvent e) {
        ((PbWindow) getParent()).confirm = result;
    }
}
