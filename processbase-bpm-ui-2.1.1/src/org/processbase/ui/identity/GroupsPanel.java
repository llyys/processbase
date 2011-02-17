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

import org.processbase.ui.admin.*;
import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.util.List;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.User;
import org.processbase.core.Constants;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.runtime.Category;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.ConfirmDialog;
import org.processbase.ui.template.TreeTablePanel;

/**
 *
 * @author marat gubaidullin
 */
public class GroupsPanel extends TreeTablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public GroupsPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        treeTable.addContainerProperty("name", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionName"), null, null);
        treeTable.setColumnExpandRatio("name", 1);
        treeTable.addContainerProperty("label", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionLabel"), null, null);
        treeTable.addContainerProperty("description", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionDescription"), null, null);
        treeTable.addContainerProperty("actions", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionActions"), null, null);
        treeTable.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            treeTable.removeAllItems();
            List<Group> groups = PbPortlet.getCurrent().bpmModule.getAllGroups();
            for (Group group : groups) {
                System.out.println("group = " + group.getName() + " parent = " + group.getParentGroup());
                Item woItem = treeTable.addItem(group.getUUID());
                TableLinkButton teb = new TableLinkButton(group.getName(), "", null, group, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("label").setValue(group.getLabel());
                woItem.getItemProperty("description").setValue(group.getDescription());
                TableLinkButton tlb = new TableLinkButton(PbPortlet.getCurrent().messages.getString("btnDelete"), "icons/cancel.png", group, this, Constants.ACTION_DELETE);
                woItem.getItemProperty("actions").setValue(tlb);
            }

            for (Group group : groups) {
                if (group.getParentGroup() != null) {
                    treeTable.setChildrenAllowed(group.getParentGroup().getUUID(), true);
                    treeTable.setCollapsed(group.getParentGroup().getUUID(), false);
                    treeTable.setParent(group.getUUID(), group.getParentGroup().getUUID());
                }
            }

            treeTable.setSortContainerPropertyId("name");
            treeTable.setSortAscending(false);
            treeTable.sort();
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
            Group group = (Group) execBtn.getTableValue();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                try {
                    removeGroup(group);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                GroupWindow ngw = new GroupWindow(group);
                ngw.exec();
                ngw.addListener((Window.CloseListener) this);
                getWindow().addWindow(ngw);
            }
        }
    }

    private void removeGroup(final Group group) {
        ConfirmDialog.show(PbPortlet.getCurrent().getMainWindow(),
                PbPortlet.getCurrent().messages.getString("windowCaptionConfirm"),
                PbPortlet.getCurrent().messages.getString("removeGroup") + "?",
                PbPortlet.getCurrent().messages.getString("btnYes"),
                PbPortlet.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                PbPortlet.getCurrent().bpmModule.removeGroupByUUID(group.getUUID());
                                treeTable.removeItem(group.getUUID());
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
}
