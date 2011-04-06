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
package org.processbase.ui.bpm.panel;

import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.ui.core.template.TreeTablePanel;
import org.processbase.ui.core.template.WorkPanel;
import org.processbase.ui.bpm.identity.GroupWindow;
import org.processbase.ui.bpm.identity.GroupsPanel;
import org.processbase.ui.bpm.identity.MetadataPanel;
import org.processbase.ui.bpm.identity.MetadataWindow;
import org.processbase.ui.bpm.identity.RoleWindow;
import org.processbase.ui.bpm.identity.RolesPanel;
import org.processbase.ui.bpm.identity.SyncUsersWindow;
import org.processbase.ui.bpm.identity.UserWindow;
import org.processbase.ui.bpm.identity.UsersPanel;

/**
 *
 * @author mgubaidullin
 */
public class IdentityPanel extends PbPanel
        implements Button.ClickListener, Window.CloseListener {

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
    private HashMap<Button, WorkPanel> panels = new HashMap<Button, WorkPanel>();

    public void initUI() {
        setMargin(false);

        prepareButtonBar();
        addComponent(buttonBar, 0);

        usersPanel = new UsersPanel();
        panels.put(usersBtn, usersPanel);
        addComponent(usersPanel, 1);
        setExpandRatio(usersPanel, 1);
        usersPanel.initUI();
        usersPanel.refreshTable();

        rolesPanel = new RolesPanel();
        panels.put(rolesBtn, rolesPanel);

        groupsPanel = new GroupsPanel();
        panels.put(groupsBtn, groupsPanel);

        metadataPanel = new MetadataPanel();
        panels.put(metadataBtn, metadataPanel);
    }

    private void setCurrentPanel(WorkPanel workPanel) {
        replaceComponent(getComponent(1), workPanel);
        setExpandRatio(workPanel, 1);
        if (!workPanel.isInitialized()){
            workPanel.initUI();
        }
        if (workPanel instanceof TablePanel) {
            ((TablePanel) workPanel).refreshTable();
        } else if (workPanel instanceof TreeTablePanel) {
            ((TreeTablePanel) workPanel).refreshTable();
        }
    }

    private void prepareButtonBar() {
        // prepare usersBtn button
        usersBtn = new Button(((Processbase)getApplication()).getMessages().getString("usersBtn"), this);
        usersBtn.setStyleName("special");
        usersBtn.setEnabled(false);
        buttonBar.addComponent(usersBtn, 0);
        buttonBar.setComponentAlignment(usersBtn, Alignment.MIDDLE_LEFT);

        // prepare rolesBtn button
        rolesBtn = new Button(((Processbase)getApplication()).getMessages().getString("rolesBtn"), this);
        rolesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(rolesBtn, 1);
        buttonBar.setComponentAlignment(rolesBtn, Alignment.MIDDLE_LEFT);

        // prepare groupsBtn button
        groupsBtn = new Button(((Processbase)getApplication()).getMessages().getString("groupsBtn"), this);
        groupsBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(groupsBtn, 2);
        buttonBar.setComponentAlignment(groupsBtn, Alignment.MIDDLE_LEFT);

        // prepare metadataBtn button
        metadataBtn = new Button(((Processbase)getApplication()).getMessages().getString("metadataBtn"), this);
        metadataBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(metadataBtn, 3);
        buttonBar.setComponentAlignment(metadataBtn, Alignment.MIDDLE_LEFT);

        // add expand label
        Label expandLabel = new Label("");
        buttonBar.addComponent(expandLabel, 4);
        buttonBar.setExpandRatio(expandLabel, 1);

        // prepare refresh button
        refreshBtn = new Button(((Processbase)getApplication()).getMessages().getString("btnRefresh"), this);
        buttonBar.addComponent(refreshBtn, 5);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);

        // prepare add button
        btnAdd = new Button(((Processbase)getApplication()).getMessages().getString("btnAdd"), this);
        buttonBar.addComponent(btnAdd, 6);
        buttonBar.setComponentAlignment(btnAdd, Alignment.MIDDLE_RIGHT);

        // prepare sync button
        syncBtn = new Button(((Processbase)getApplication()).getMessages().getString("syncBtn"), this);
        syncBtn.setDescription(((Processbase)getApplication()).getMessages().getString("syncBtnDescription"));
        buttonBar.addComponent(syncBtn, 6);
        buttonBar.setComponentAlignment(syncBtn, Alignment.MIDDLE_RIGHT);

        buttonBar.setWidth("100%");
    }

    public void buttonClick(ClickEvent event) {
        WorkPanel panel = panels.get(event.getButton());
        if (event.getButton().equals(refreshBtn) && (getComponent(1) instanceof TablePanel)) {
            ((TablePanel) getComponent(1)).refreshTable();
        } else if (event.getButton().equals(refreshBtn) && (getComponent(1) instanceof TreeTablePanel)) {
            ((TreeTablePanel) getComponent(1)).refreshTable();
        } else if (event.getButton().equals(syncBtn)) {
            synchronizeIdentity();
        } else if (event.getButton().equals(btnAdd)) {
            addIdentity();
        } else {
            activateButtons();
            event.getButton().setStyleName("special");
            event.getButton().setEnabled(false);
            setCurrentPanel(panel);
            if (getComponent(1) instanceof TablePanel) {
                ((TablePanel) getComponent(1)).refreshTable();
            } else if (getComponent(1) instanceof TreeTablePanel) {
                ((TreeTablePanel) getComponent(1)).refreshTable();
            }
            if (!event.getButton().equals(usersBtn)) {
                syncBtn.setVisible(false);
            }
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
        syncBtn.setVisible(true);
    }

    public void windowClose(CloseEvent e) {
        if (getComponent(1) instanceof TablePanel) {
            ((TablePanel) getComponent(1)).refreshTable();
        } else if (getComponent(1) instanceof TreeTablePanel) {
            ((TreeTablePanel) getComponent(1)).refreshTable();
        }
    }

    private void synchronizeIdentity() {
        if (getComponent(1) instanceof UsersPanel) {
            SyncUsersWindow ncw = new SyncUsersWindow();
            ncw.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(ncw);
            ncw.initUI();
        }
    }

    private void addIdentity() {
        if (getComponent(1) instanceof UsersPanel) {
            UserWindow nuw = new UserWindow(null);
            nuw.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(nuw);
            nuw.initUI();
        } else if (getComponent(1) instanceof RolesPanel) {
            RoleWindow nrw = new RoleWindow(null);
            nrw.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(nrw);
            nrw.initUI();
        } else if (getComponent(1) instanceof GroupsPanel) {
            GroupWindow rgw = new GroupWindow(null);
            rgw.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(rgw);
            rgw.initUI();
        } else if (getComponent(1) instanceof MetadataPanel) {
            MetadataWindow nmw = new MetadataWindow(null);
            nmw.addListener((Window.CloseListener) this);
            getApplication().getMainWindow().addWindow(nmw);
            nmw.initUI();
        }
    }
}
