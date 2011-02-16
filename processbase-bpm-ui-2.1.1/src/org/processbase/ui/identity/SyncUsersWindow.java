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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.List;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.ConfirmDialog;
import com.liferay.portal.service.UserLocalServiceUtil;
import java.util.ArrayList;
import java.util.HashMap;
import org.processbase.core.Constants;

/**
 *
 * @author mgubaidullin
 */
public class SyncUsersWindow extends PbWindow implements ClickListener {

    private ButtonBar buttons = new ButtonBar();
    private Button closeBtn = new Button(PbPortlet.getCurrent().messages.getString("btnClose"), this);
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

            layout.addComponent(table);

            refreshTable();

            buttons.addButton(closeBtn);
            buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
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
        table.addContainerProperty("liferayStatus", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionLiferayStatus"), null, null);
        table.addContainerProperty("bonitaStatus", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionBonitaStatus"), null, null);
//        table.addContainerProperty("state", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionState"), null, null);
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
//        woItem.getItemProperty("state").setValue(usersMap.get(userName));
        if (usersMap.get(userName).equals("BONITA")) {
            woItem.getItemProperty("lastname").setValue(getBonitaUser(userName).getLastName());
            woItem.getItemProperty("firstname").setValue(getBonitaUser(userName).getFirstName());
            woItem.getItemProperty("bonitaStatus").setValue("ACTIVE");
            woItem.getItemProperty("liferayStatus").setValue("ABSENT");
            TableLinkButton tlb = new TableLinkButton(PbPortlet.getCurrent().messages.getString("deleteFromBonita"), "icons/cancel.png", userName, this, Constants.ACTION_DELETE);
            woItem.getItemProperty("actions").setValue(tlb);
        } else if (usersMap.get(userName).equals("LIFERAY")) {
            woItem.getItemProperty("lastname").setValue(getLiferayUser(userName).getLastName());
            woItem.getItemProperty("firstname").setValue(getLiferayUser(userName).getFirstName());
            woItem.getItemProperty("bonitaStatus").setValue("ABSENT");
            woItem.getItemProperty("liferayStatus").setValue(getLiferayUser(userName).isActive() ? "ACTIVE" : "DISABLED");
            TableLinkButton tlb = new TableLinkButton(PbPortlet.getCurrent().messages.getString("addToBonita"), "icons/accept.png", userName, this, Constants.ACTION_ADD);
            woItem.getItemProperty("actions").setValue(tlb);
        } else if (usersMap.get(userName).equals("BOTH")) {
            woItem.getItemProperty("lastname").setValue(getLiferayUser(userName).getLastName());
            woItem.getItemProperty("firstname").setValue(getLiferayUser(userName).getFirstName());
            woItem.getItemProperty("bonitaStatus").setValue("ACTIVE");
            woItem.getItemProperty("liferayStatus").setValue(getLiferayUser(userName).isActive() ? "ACTIVE" : "DISABLED");
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(closeBtn)) {
                close();
            } else if (event.getButton() instanceof TableLinkButton) {
                TableLinkButton tlb = (TableLinkButton) event.getButton();
                if (tlb.getAction().equals(Constants.ACTION_ADD)) {
                    addToBonita(tlb.getTableValue().toString());
                } else if (tlb.getAction().equals(Constants.ACTION_DELETE)) {
                    deleteFromBonita(tlb.getTableValue().toString());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    private com.liferay.portal.model.User getLiferayUser(String userName) {
        for (com.liferay.portal.model.User user : liferayUsers) {
            if (user.getScreenName().equals(userName)) {
                return user;
            }
        }
        return null;
    }

    private org.ow2.bonita.facade.identity.User getBonitaUser(String userName) {
        for (org.ow2.bonita.facade.identity.User user : bonitaUsers) {
            if (user.getUsername().equals(userName)) {
                return user;
            }
        }
        return null;
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

    private void addToBonita(final String userName) {
        ConfirmDialog.show(PbPortlet.getCurrent().getMainWindow(),
                PbPortlet.getCurrent().messages.getString("windowCaptionConfirm"),
                PbPortlet.getCurrent().messages.getString("addToBonita") + "?",
                PbPortlet.getCurrent().messages.getString("btnYes"),
                PbPortlet.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                com.liferay.portal.model.User liferayUser = getLiferayUser(userName);
                                org.ow2.bonita.facade.identity.User user = PbPortlet.getCurrent().bpmModule.addUser(liferayUser.getScreenName(),
                                        "", liferayUser.getFirstName(), liferayUser.getLastName(),
                                        "", liferayUser.getJobTitle(), null, new HashMap<String, String>());
                                Item woItem = table.getItem(userName);
                                woItem.getItemProperty("bonitaStatus").setValue("ACTIVE");
                                woItem.getItemProperty("actions").setValue(null);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void deleteFromBonita(final String userName) {
        ConfirmDialog.show(PbPortlet.getCurrent().getMainWindow(),
                PbPortlet.getCurrent().messages.getString("windowCaptionConfirm"),
                PbPortlet.getCurrent().messages.getString("deleteFromBonita") + "?",
                PbPortlet.getCurrent().messages.getString("btnYes"),
                PbPortlet.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                PbPortlet.getCurrent().bpmModule.removeUserByUUID(getBonitaUser(userName).getUUID());
                                table.removeItem(userName);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
}
