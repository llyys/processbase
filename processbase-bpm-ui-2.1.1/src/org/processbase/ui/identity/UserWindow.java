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

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import java.util.List;
import org.ow2.bonita.facade.identity.User;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class UserWindow extends PbWindow implements ClickListener {

    private User user = null;
    private ButtonBar buttons = new ButtonBar();
    private VerticalLayout userInfofmation = new VerticalLayout();
    private VerticalLayout userMembership = new VerticalLayout();
    private TabSheet tabSheet = new TabSheet();
    private Button closeBtn = new Button(PbPortlet.getCurrent().messages.getString("btnClose"), this);
    private Button applyBtn = new Button(PbPortlet.getCurrent().messages.getString("btnSave"), this);
    private TextField userFirstName = new TextField(PbPortlet.getCurrent().messages.getString("userFirstName"));
    private TextField userLastName = new TextField(PbPortlet.getCurrent().messages.getString("userLastName"));
    private TextField userName = new TextField(PbPortlet.getCurrent().messages.getString("userName"));
    private TextField userEmail = new TextField(PbPortlet.getCurrent().messages.getString("userEmail"));
    private TextField userJobTitle = new TextField(PbPortlet.getCurrent().messages.getString("userJobTitle"));
    private PasswordField password = new PasswordField(PbPortlet.getCurrent().messages.getString("password"));

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

            if (user != null) {
                userFirstName.setValue(user.getFirstName());
                userLastName.setValue(user.getLastName());
                userName.setValue(user.getUsername());
                userEmail.setValue(user.getProfessionalContactInfo().getEmail());
                userJobTitle.setValue(user.getJobTitle());
                password.setValue(user.getPassword());
            }

            // prepare user membership
            userMembership.setMargin(true);
            userMembership.setSpacing(true);


            // prepare tabSheet
            tabSheet.addComponent(userInfofmation);
            tabSheet.addComponent(userMembership);

            addComponent(tabSheet);

            buttons.addButton(applyBtn);
            buttons.setComponentAlignment(applyBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(applyBtn, 1);
            buttons.addButton(closeBtn);
            buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(false);
            buttons.setHeight("30px");
            buttons.setWidth("100%");
            addComponent(buttons);
            setWidth("310px");
            setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                if (user == null) {
                    User userNew = PbPortlet.getCurrent().bpmModule.addUser(
                            userName.getValue().toString(),
                            password.getValue().toString(),
                            userFirstName.getValue().toString(),
                            userLastName.getValue().toString(),
                            "", userJobTitle.getValue().toString(), null, new HashMap<String, String>());
                    PbPortlet.getCurrent().bpmModule.updateUserProfessionalContactInfo(
                            userNew.getUUID(), userEmail.getValue().toString(), "",
                            "", "", "", "", "", "", "", "", "", "");
                } else {
                    PbPortlet.getCurrent().bpmModule.updateUserByUUID(
                            user.getUUID(),
                            userName.getValue().toString(),
                            userFirstName.getValue().toString(),
                            userLastName.getValue().toString(),
                            "", userJobTitle.getValue().toString(), null, new HashMap<String, String>());
                    PbPortlet.getCurrent().bpmModule.updateUserPassword(
                            user.getUUID(), password.getValue().toString());
                    PbPortlet.getCurrent().bpmModule.updateUserProfessionalContactInfo(
                            user.getUUID(), userEmail.getValue().toString(), "",
                            "", "", "", "", "", "", "", "", "", "");
                }
                close();
            } else {
                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public IndexedContainer getUserParent() throws Exception {
        IndexedContainer container = new IndexedContainer();
//        container.addContainerProperty("name", String.class, null);
//        container.addContainerProperty("label", String.class, null);
//        container.addContainerProperty("uuid", String.class, null);
//        container.addContainerProperty("path", String.class, null);
//        List<User> users = PbPortlet.getCurrent().bpmModule.getAllUsers();
//        for (User userX : users) {
//            Item item = container.addItem(userX.getUUID());
//            item.getItemProperty("name").setValue(userX.getName());
//            item.getItemProperty("label").setValue(userX.getLabel());
//            item.getItemProperty("uuid").setValue(userX.getUUID());
//            item.getItemProperty("path").setValue(getUserPath(userX));
//        }
//        container.sort(new Object[]{"name"}, new boolean[]{true});
        return container;
    }

    private String getUserPath(User user) {
        StringBuilder result = new StringBuilder("/" + user + "/");
//        User parent = user.getParentUser();
//        while (parent != null) {
//            result.insert(0, "/" + parent.getName());
//            parent = parent.getParentUser();
//        }
        return result.toString();
    }
}
