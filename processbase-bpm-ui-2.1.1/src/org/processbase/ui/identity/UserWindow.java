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
package org.processbase.ui.identity;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.Role;
import org.ow2.bonita.facade.identity.User;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.core.Constants;

/**
 *
 * @author mgubaidullin
 */
public class UserWindow extends PbWindow
        implements ClickListener, TabSheet.SelectedTabChangeListener {

    private User user = null;
    private ButtonBar buttons = new ButtonBar();
    private VerticalLayout userInfofmation = new VerticalLayout();
    private VerticalLayout userMembership = new VerticalLayout();
    private VerticalLayout userMetadata = new VerticalLayout();
    private TabSheet tabSheet = new TabSheet();
    private Button addBtn = new Button(PbPortlet.getCurrent().messages.getString("btnAdd"), this);
    private Button closeBtn = new Button(PbPortlet.getCurrent().messages.getString("btnClose"), this);
    private Button saveBtn = new Button(PbPortlet.getCurrent().messages.getString("btnSave"), this);
    private TextField userFirstName = new TextField(PbPortlet.getCurrent().messages.getString("userFirstName"));
    private TextField userLastName = new TextField(PbPortlet.getCurrent().messages.getString("userLastName"));
    private TextField userName = new TextField(PbPortlet.getCurrent().messages.getString("userName"));
    private TextField userEmail = new TextField(PbPortlet.getCurrent().messages.getString("userEmail"));
    private TextField userJobTitle = new TextField(PbPortlet.getCurrent().messages.getString("userJobTitle"));
    private PasswordField password = new PasswordField(PbPortlet.getCurrent().messages.getString("password"));
    private Table tableMembership = new Table();
    private Table tableMetadata = new Table();

    public UserWindow(User user) {
        super(user == null ? PbPortlet.getCurrent().messages.getString("newUser") : PbPortlet.getCurrent().messages.getString("user"));
        this.user = user;
    }

    public void exec() {
        try {
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            // prepare user information
            userInfofmation.setMargin(true);
            userInfofmation.setSpacing(true);
            userName.setWidth("270px");
            userInfofmation.addComponent(userName);
            password.setWidth("270px");
            userInfofmation.addComponent(password);
            userFirstName.setWidth("270px");
            userInfofmation.addComponent(userFirstName);
            userLastName.setWidth("270px");
            userInfofmation.addComponent(userLastName);
            userEmail.setWidth("270px");
            userInfofmation.addComponent(userEmail);
            userJobTitle.setWidth("270px");
            userInfofmation.addComponent(userJobTitle);


            // prepare user membership
            userMembership.setMargin(true);
            userMembership.setSpacing(true);
            prepareTableMembership();
            userMembership.addComponent(tableMembership);

            // prepare user metadata
            userMetadata.setMargin(true);
            userMetadata.setSpacing(true);


            // prepare tabSheet
            tabSheet.addTab(userInfofmation, PbPortlet.getCurrent().messages.getString("userInfofmation"), null);
            tabSheet.addTab(userMembership, PbPortlet.getCurrent().messages.getString("userMembership"), null);
            tabSheet.addTab(userMetadata, PbPortlet.getCurrent().messages.getString("userMetadata"), null);
            tabSheet.addListener((TabSheet.SelectedTabChangeListener) this);
            tabSheet.setImmediate(true);
            addComponent(tabSheet);

            if (user != null) {
                userFirstName.setValue(user.getFirstName());
                userLastName.setValue(user.getLastName());
                userName.setValue(user.getUsername());
                userEmail.setValue(user.getProfessionalContactInfo().getEmail());
                userJobTitle.setValue(user.getJobTitle());
                password.setValue(user.getPassword());
                refreshTableMembership();
            }

            addBtn.setVisible(false);
            buttons.addButton(addBtn);
            buttons.setComponentAlignment(addBtn, Alignment.MIDDLE_RIGHT);
            buttons.addButton(saveBtn);
            buttons.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(saveBtn, 1);
            buttons.addButton(closeBtn);
            buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(false);
            buttons.setHeight("30px");
            buttons.setWidth("100%");
            addComponent(buttons);
            setWidth("600px");
            setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(saveBtn)) {
                if (user == null) {
                    User userNew = PbPortlet.getCurrent().bpmModule.addUser(
                            userName.getValue().toString(),
                            password.getValue().toString(),
                            userFirstName.getValue().toString(),
                            userLastName.getValue().toString(),
                            "", userJobTitle.getValue() != null ? userJobTitle.getValue().toString() : "",
                            null, new HashMap<String, String>());
                    PbPortlet.getCurrent().bpmModule.updateUserProfessionalContactInfo(
                            userNew.getUUID(), userEmail.getValue().toString(), "",
                            "", "", "", "", "", "", "", "", "", "");
                } else {
                    PbPortlet.getCurrent().bpmModule.updateUserByUUID(
                            user.getUUID(),
                            userName.getValue().toString(),
                            userFirstName.getValue().toString(),
                            userLastName.getValue().toString(),
                            "",
                            userJobTitle.getValue() != null ? userJobTitle.getValue().toString() : "",
                            null, new HashMap<String, String>());
                    PbPortlet.getCurrent().bpmModule.updateUserPassword(
                            user.getUUID(), password.getValue().toString());
                    PbPortlet.getCurrent().bpmModule.updateUserProfessionalContactInfo(
                            user.getUUID(), userEmail.getValue().toString(), "",
                            "", "", "", "", "", "", "", "", "", "");
                }
                close();
            } else if (event.getButton().equals(addBtn)) {
                addTableMembershipRow(null);
            } else if (event.getButton().equals(closeBtn)) {
                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    private void prepareTableMembership() {
        tableMembership.addContainerProperty("group", ComboBox.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionGroup"), null, null);
        tableMembership.addContainerProperty("role", ComboBox.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionRole"), null, null);
        tableMembership.addContainerProperty("actions", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionActions"), null, null);
        tableMembership.setImmediate(true);
        tableMembership.setWidth("100%");
        tableMembership.setPageLength(7);
    }

    private void refreshTableMembership() {
        try {
            tableMembership.removeAllItems();
            for (Membership membership : user.getMemberships()) {
                addTableMembershipRow(membership);
            }
        } catch (Exception ex) {
        }
    }

    private void addTableMembershipRow(Membership membership) throws Exception {
        String uuid = membership != null ? membership.getUUID() : UUID.randomUUID().toString();
        Item woItem = tableMembership.addItem(uuid);

        ComboBox groups = new ComboBox();
        groups.setContainerDataSource(getGroups());
        groups.setItemCaptionPropertyId("path");
        groups.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
        groups.setValue(membership != null ? membership.getGroup().getUUID() : null);
        woItem.getItemProperty("group").setValue(groups);

        ComboBox roles = new ComboBox();
        roles.setContainerDataSource(getRoles());
        roles.setItemCaptionPropertyId("name");
        roles.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
        roles.setValue(membership != null ? membership.getRole().getUUID() : null);
        woItem.getItemProperty("group").setValue(groups);
        woItem.getItemProperty("role").setValue(roles);
        TableLinkButton tlb = new TableLinkButton(PbPortlet.getCurrent().messages.getString("btnDelete"), "icons/cancel.png", uuid, this, Constants.ACTION_DELETE);
        woItem.getItemProperty("actions").setValue(tlb);

    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        if (event.getTabSheet().getSelectedTab().equals(userInfofmation)) {
            addBtn.setVisible(false);
        } else {
            addBtn.setVisible(true);
        }
    }

    public IndexedContainer getGroups() throws Exception {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("name", String.class, null);
        container.addContainerProperty("label", String.class, null);
        container.addContainerProperty("uuid", String.class, null);
        container.addContainerProperty("path", String.class, null);
        List<Group> groups = PbPortlet.getCurrent().bpmModule.getAllGroups();
        for (Group groupX : groups) {
            Item item = container.addItem(groupX.getUUID());
            item.getItemProperty("name").setValue(groupX.getName());
            item.getItemProperty("label").setValue(groupX.getLabel());
            item.getItemProperty("uuid").setValue(groupX.getUUID());
            item.getItemProperty("path").setValue(getGroupPath(groupX));
        }
        container.sort(new Object[]{"name"}, new boolean[]{true});
        return container;
    }

    private String getGroupPath(Group group) {
        StringBuilder result = new StringBuilder(IdentityAPI.GROUP_PATH_SEPARATOR + group.getName() + IdentityAPI.GROUP_PATH_SEPARATOR);
        Group parent = group.getParentGroup();
        while (parent != null) {
            result.insert(0, IdentityAPI.GROUP_PATH_SEPARATOR + parent.getName());
            parent = parent.getParentGroup();
        }
        return result.toString();
    }

    public IndexedContainer getRoles() throws Exception {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("name", String.class, null);
        container.addContainerProperty("label", String.class, null);
        container.addContainerProperty("uuid", String.class, null);
        List<Role> roles = PbPortlet.getCurrent().bpmModule.getAllRoles();
        for (Role roleX : roles) {
            Item item = container.addItem(roleX.getUUID());
            item.getItemProperty("name").setValue(roleX.getName());
            item.getItemProperty("label").setValue(roleX.getLabel());
            item.getItemProperty("uuid").setValue(roleX.getUUID());
        }
        container.sort(new Object[]{"name"}, new boolean[]{true});
        return container;
    }
}
