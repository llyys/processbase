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
package org.processbase.ui.bam;

import org.processbase.ui.identity.*;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.util.List;
import org.ow2.bonita.facade.identity.User;
import org.processbase.core.Constants;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.ConfirmDialog;

/**
 *
 * @author marat gubaidullin
 */
public class SchemesPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public SchemesPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("username", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionUsername"), null, null);
//        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("lastname", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionLastname"), null, null);
        table.addContainerProperty("firstname", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionFirstname"), null, null);
        table.addContainerProperty("email", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionEmail"), null, null);
        table.addContainerProperty("actions", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionActions"), null, null);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            List<User> users = PbPortlet.getCurrent().bpmModule.getAllUsers();

            for (User user : users) {
                Item woItem = table.addItem(user);
                TableLinkButton teb = new TableLinkButton(user.getUsername(), "", null, user, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("username").setValue(teb);
                woItem.getItemProperty("lastname").setValue(user.getLastName());
                woItem.getItemProperty("firstname").setValue(user.getFirstName());
                woItem.getItemProperty("email").setValue(user.getProfessionalContactInfo() != null ? user.getProfessionalContactInfo().getEmail() : "");
                TableLinkButton tlb = new TableLinkButton(PbPortlet.getCurrent().messages.getString("btnDelete"), "icons/cancel.png", user, this, Constants.ACTION_DELETE);
                woItem.getItemProperty("actions").setValue(tlb);
            }
            table.setSortContainerPropertyId("username");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableLinkButton) {
            TableLinkButton execBtn = (TableLinkButton) event.getButton();
            User user = (User) execBtn.getTableValue();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                try {
                    removeUser(user);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                UserWindow nuw = new UserWindow(user);
                nuw.exec();
                nuw.addListener((Window.CloseListener) this);
                getWindow().addWindow(nuw);
            }
        }
    }

    private void removeUser(final User user) {
        ConfirmDialog.show(PbPortlet.getCurrent().getMainWindow(),
                PbPortlet.getCurrent().messages.getString("windowCaptionConfirm"),
                PbPortlet.getCurrent().messages.getString("removeUser") + "?",
                PbPortlet.getCurrent().messages.getString("btnYes"),
                PbPortlet.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                PbPortlet.getCurrent().bpmModule.removeUserByUUID(user.getUUID());
                                table.removeItem(user);
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
}
