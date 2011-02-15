/**
 * Copyright (C) 2011 PROCESSBASE
 * PROCESSBASE Ltd, Almaty, Kazakhstan
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
package org.processbase.ui.portlet;

import com.liferay.portal.model.User;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import java.util.Locale;
import javax.portlet.PortletSession;
import org.processbase.ui.admin.ProcessDefinitionsPanel;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.admin.CategoriesPanel;
import org.processbase.ui.admin.NewCategoryWindow;
import org.processbase.ui.admin.NewProcessDefinitionWindow;
import org.processbase.ui.identity.GroupsPanel;
import org.processbase.ui.identity.MetadataPanel;
import org.processbase.ui.identity.RolesPanel;
import org.processbase.ui.identity.SyncUsersWindow;
import org.processbase.ui.identity.UsersPanel;

/**
 *
 * @author mgubaidullin
 */
public class IdentityPortlet extends PbPortlet
        implements Button.ClickListener, Window.CloseListener {

    private PbWindow identityWindow;
    private VerticalLayout mainLayout = new VerticalLayout();
    private ButtonBar buttonBar = new ButtonBar();
    private UsersPanel usersPanel;
    private RolesPanel rolesPanel;
    private GroupsPanel groupsPanel;
    private MetadataPanel metadataPanel;
    private Button refreshBtn = null;
    private Button btnAdd = null;
    private Button usersBtn = null;
    private Button rolesBtn = null;
    private Button groupsBtn = null;
    private Button metadataBtn = null;
    private Button syncBtn = null;
    private HashMap<Button, TablePanel> panels = new HashMap<Button, TablePanel>();

    @Override
    public void init() {
        super.init();
        this.setTheme("processbase");
        prepareMainWindow();
    }

    private void prepareMainWindow() {

        mainLayout.setMargin(false);

        identityWindow = new PbWindow("Processbase Identity Portlet");
        identityWindow.setContent(mainLayout);
        identityWindow.setSizeFull();

        this.setMainWindow(identityWindow);

        prepareButtonBar();
        mainLayout.addComponent(buttonBar, 0);

        usersPanel = new UsersPanel();
        panels.put(usersBtn, usersPanel);
        mainLayout.addComponent(usersPanel, 1);
        usersPanel.refreshTable();

        rolesPanel = new RolesPanel();
        panels.put(rolesBtn, rolesPanel);

        groupsPanel = new GroupsPanel();
        panels.put(groupsBtn, groupsPanel);

        metadataPanel = new MetadataPanel();
        panels.put(metadataBtn, metadataPanel);
    }

    private void setCurrentPanel(TablePanel tablePanel) {
        mainLayout.replaceComponent(mainLayout.getComponent(1), tablePanel);
        if (tablePanel.equals(rolesPanel) || tablePanel.equals(usersPanel)) {
            tablePanel.refreshTable();
        }
    }

    private void prepareButtonBar() {
        // prepare usersBtn button
        usersBtn = new Button(this.messages.getString("usersBtn"), this);
        usersBtn.setStyleName("special");
        usersBtn.setEnabled(false);
        buttonBar.addComponent(usersBtn, 0);
        buttonBar.setComponentAlignment(usersBtn, Alignment.MIDDLE_LEFT);

        // prepare rolesBtn button
        rolesBtn = new Button(this.messages.getString("rolesBtn"), this);
        rolesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(rolesBtn, 1);
        buttonBar.setComponentAlignment(rolesBtn, Alignment.MIDDLE_LEFT);

        // prepare groupsBtn button
        groupsBtn = new Button(this.messages.getString("groupsBtn"), this);
        groupsBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(groupsBtn, 2);
        buttonBar.setComponentAlignment(groupsBtn, Alignment.MIDDLE_LEFT);

        // prepare metadataBtn button
        metadataBtn = new Button(this.messages.getString("metadataBtn"), this);
        metadataBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(metadataBtn, 3);
        buttonBar.setComponentAlignment(metadataBtn, Alignment.MIDDLE_LEFT);

        // add expand label
        Label expandLabel = new Label("");
        buttonBar.addComponent(expandLabel, 4);
        buttonBar.setExpandRatio(expandLabel, 1);

        // prepare refresh button
        refreshBtn = new Button(this.messages.getString("btnRefresh"), this);
        buttonBar.addComponent(refreshBtn, 5);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);

        // prepare add button
        btnAdd = new Button(this.messages.getString("btnAdd"), this);
        buttonBar.addComponent(btnAdd, 6);
        buttonBar.setComponentAlignment(btnAdd, Alignment.MIDDLE_RIGHT);

        // prepare sync button
        syncBtn = new Button(this.messages.getString("syncBtn"), this);
        syncBtn.setDescription(this.messages.getString("syncBtnDescription"));
        buttonBar.addComponent(syncBtn, 6);
        buttonBar.setComponentAlignment(syncBtn, Alignment.MIDDLE_RIGHT);


        buttonBar.setStyleName("white");
        buttonBar.setWidth("100%");
//        buttonBar.setHeight("48px");
        buttonBar.setMargin(false, true, false, true);
        buttonBar.setSpacing(true);
    }

    public User getCurrentUser() {
        return ((User) portletApplicationContext2.getPortletSession().getAttribute("PROCESSBASE_USER", PortletSession.APPLICATION_SCOPE));
    }

    public Locale getCurrentLocale() {
        return (Locale) portletApplicationContext2.getPortletSession().getAttribute("org.apache.struts.action.LOCALE", PortletSession.APPLICATION_SCOPE);
    }

    public void buttonClick(ClickEvent event) {
        TablePanel panel = panels.get(event.getButton());
        if (event.getButton().equals(refreshBtn)) {
            ((TablePanel) mainLayout.getComponent(1)).refreshTable();
        } else if (event.getButton().equals(syncBtn)) {
            synchronizeIdentity();
        } else if (event.getButton().equals(btnAdd)) {
        } else {
            activateButtons();
            event.getButton().setStyleName("special");
            event.getButton().setEnabled(false);
            setCurrentPanel(panel);
        }

    }

    private void activateButtons() {
        usersBtn.setStyleName(Reindeer.BUTTON_LINK);
        usersBtn.setEnabled(true);
        rolesBtn.setStyleName(Reindeer.BUTTON_LINK);
        rolesBtn.setEnabled(true);
        groupsBtn.setStyleName(Reindeer.BUTTON_LINK);
        groupsBtn.setEnabled(true);
        metadataBtn.setStyleName(Reindeer.BUTTON_LINK);
        metadataBtn.setEnabled(true);
        btnAdd.setVisible(true);
    }

    public void windowClose(CloseEvent e) {
        ((TablePanel) mainLayout.getComponent(1)).refreshTable();
    }

    private void synchronizeIdentity() {
        if (mainLayout.getComponent(1) instanceof UsersPanel) {
            SyncUsersWindow ncw = new SyncUsersWindow();
            ncw.exec();
            ncw.addListener((Window.CloseListener) this);
            getMainWindow().addWindow(ncw);
        }
    }
}
