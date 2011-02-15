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

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.vaadin.data.Item;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.ConfirmDialog;
import com.liferay.portal.service.UserLocalServiceUtil;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author mgubaidullin
 */
public class SyncUsersWindow extends PbWindow implements ClickListener {

    private ButtonBar bar = new ButtonBar();
    private ButtonBar buttons = new ButtonBar();
    private Button deleteBtn = new Button(PbPortlet.getCurrent().messages.getString("btnDelete"), this);
    private Button cancelBtn = new Button(PbPortlet.getCurrent().messages.getString("btnCancel"), this);
    private Button saveBtn = new Button(PbPortlet.getCurrent().messages.getString("btnSave"), this);
    private Button addBtn = new Button(PbPortlet.getCurrent().messages.getString("btnAdd"), this);
    private Table table = new Table();
    private List<org.ow2.bonita.facade.identity.User> bonitaUsers = new ArrayList<org.ow2.bonita.facade.identity.User>();
    private List<com.liferay.portal.model.User> liferayUsers = new ArrayList<com.liferay.portal.model.User>();
    private HashMap<String, String> usersMap = new HashMap<String, String>();

    public SyncUsersWindow() {
        super(PbPortlet.getCurrent().messages.getString("syncUsers"));
        initTableUI();
    }

    public void exec() {
        try {
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            bar.setWidth("100%");
            bar.addComponent(addBtn);
            bar.setComponentAlignment(addBtn, Alignment.BOTTOM_RIGHT);

            layout.addComponent(bar);
            layout.addComponent(table);

            refreshTable();

            deleteBtn.setDescription(PbPortlet.getCurrent().messages.getString("deleteCategory"));
            buttons.addButton(deleteBtn);
            buttons.setComponentAlignment(deleteBtn, Alignment.MIDDLE_RIGHT);
            buttons.addButton(saveBtn);
            buttons.setComponentAlignment(saveBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(saveBtn, 1);
            buttons.addButton(cancelBtn);
            buttons.setComponentAlignment(cancelBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(false);
            buttons.setHeight("30px");
            buttons.setWidth("100%");
            addComponent(buttons);
            setWidth("70%");
//            setHeight("70%");
            setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void initTableUI() {
        table.addContainerProperty("username", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionUsername"), null, null);
        table.addContainerProperty("lastname", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionLastname"), null, null);
        table.addContainerProperty("firstname", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionFirstname"), null, null);
        table.addContainerProperty("state", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionState"), null, null);
        table.addContainerProperty("actions", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 50);
        table.setSelectable(false);
        table.setImmediate(true);
        table.setWidth("100%");
        table.setPageLength(10);
    }

    public void refreshTable() {
        try {
            table.removeAllItems();
            List<org.ow2.bonita.facade.identity.User> bu = PbPortlet.getCurrent().bpmModule.getAllUsers();
            if (!bu.isEmpty()) {
                bonitaUsers.addAll(bu);
            }
            List<com.liferay.portal.model.User> lu = UserLocalServiceUtil.getUsers(0, UserLocalServiceUtil.getUsersCount());
            if (!lu.isEmpty()) {
                liferayUsers.addAll(lu);
            }

            prepareResultMap();

            for (String userName : usersMap.keySet()) {
                addTableRow(userName);
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(true);
            table.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    private void addTableRow(String userName) {
        Item woItem = table.addItem(userName);
        woItem.getItemProperty("username").setValue(userName);
        woItem.getItemProperty("lastname").setValue(userName);
        woItem.getItemProperty("firstname").setValue(userName);
        woItem.getItemProperty("state").setValue(usersMap.get(userName));
//        woItem.getItemProperty("deployedBy").setValue(pd.getDeployedBy());
//        TableLinkButton tlb = new TableLinkButton(PbPortlet.getCurrent().messages.getString("btnRemove"), "icons/cancel.png", pd, this);
//        woItem.getItemProperty("actions").setValue(tlb);
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(saveBtn)) {
                save();
                close();
            } else {
                close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    private void save() throws Exception {
    }

    private void delete() {
        ConfirmDialog.show(PbPortlet.getCurrent().getMainWindow(),
                PbPortlet.getCurrent().messages.getString("windowCaptionConfirm"),
                PbPortlet.getCurrent().messages.getString("questionDeleteCategory"),
                PbPortlet.getCurrent().messages.getString("btnYes"),
                PbPortlet.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void prepareResultMap() {
        for (com.liferay.portal.model.User lu : liferayUsers) {
            if (!lu.isDefaultUser()) {
                usersMap.put(lu.getScreenName(), "LIFERAY");
            }
        }
        for (org.ow2.bonita.facade.identity.User bu : bonitaUsers) {
            if (usersMap.containsKey(bu.getUsername())) {
                usersMap.put(bu.getUsername(), "BOTH");
            } else {
                usersMap.put(bu.getUsername(), "BONITA");
            }
        }
    }
}
