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
package org.processbase;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import java.util.ResourceBundle;
//import javax.servlet.http.HttpSession;
import org.processbase.ui.preferences.CurrentUserPanel;

/**
 *
 * @author mgubaidullin
 */
public class LogoPanel extends HorizontalLayout implements Button.ClickListener {

    protected ResourceBundle messages = ResourceBundle.getBundle("resources/MessagesBundle", ((ProcessBase) getApplication()).getCurrent().getLocale());
    private ThemeResource themeResource = new ThemeResource("icons/processbase.png");
    private Embedded logo = new Embedded("", themeResource);
    private HorizontalLayout buttonsBar = new HorizontalLayout();
    private Label label = new Label(messages.getString("welcomeLabel") + ", " + ((ProcessBase) getApplication()).getCurrent().getUser().getGivenName() + " " + ((ProcessBase) getApplication()).getCurrent().getUser().getSn());
    private Button profileBtn = new Button(messages.getString("btnProfile"), this);
    private Button logoutBtn = new Button(messages.getString("btnLogout"), this);
//    private Button helpBtn = new Button(messages.getString("btnHelp"), this);

    public LogoPanel() {
        super();
        initUI();
    }

    private void initUI() {
        this.addComponent(logo);
        this.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);
        this.setExpandRatio(logo, 1);
        this.addComponent(buttonsBar);
        this.setComponentAlignment(buttonsBar, Alignment.MIDDLE_RIGHT);
        setHeight("50px");
        setWidth("100%");
        setSpacing(true);

        buttonsBar.addComponent(label);
        buttonsBar.addComponent(new Label("|"));
        buttonsBar.addComponent(profileBtn);
        buttonsBar.addComponent(new Label("|"));
//        buttonsBar.addComponent(helpBtn);
//        buttonsBar.addComponent(new Label("|"));
//        helpBtn.setStyleName(Button.STYLE_LINK);
        buttonsBar.addComponent(logoutBtn);
        logoutBtn.setStyleName(Button.STYLE_LINK);
        profileBtn.setStyleName(Button.STYLE_LINK);
        buttonsBar.setSpacing(true);
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(logoutBtn)) {
            WebApplicationContext applicationContext = (WebApplicationContext) getApplication().getContext();
            getApplication().close();
            applicationContext.getHttpSession().invalidate();
//        } else if (event.getButton().equals(helpBtn)) {
//            ((MainWindow) getWindow()).getWorkPanel().getHelpPanel().setVisible(!((MainWindow) getWindow()).getWorkPanel().getHelpPanel().isVisible());
        } else if (event.getButton().equals(profileBtn)) {
            ((MainWindow) getWindow()).menuTree.unselectAll();
            ((MainWindow) getWindow()).setWorkPanel(new CurrentUserPanel());
        }
    }
}
