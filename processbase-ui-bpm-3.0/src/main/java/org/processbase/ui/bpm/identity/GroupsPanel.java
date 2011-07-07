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
package org.processbase.ui.bpm.identity;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.util.List;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.identity.Group;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TreeTablePanel;

/**
 *
 * @author marat gubaidullin
 */
public class GroupsPanel extends TreeTablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public GroupsPanel() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        treeTable.addContainerProperty("name", TableLinkButton.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionName"), null, null);
        treeTable.setColumnExpandRatio("name", 1);
        treeTable.addContainerProperty("label", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionLabel"), null, null);
        treeTable.addContainerProperty("description", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionDescription"), null, null);
        treeTable.addContainerProperty("actions", TableLinkButton.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"), null, null);
        treeTable.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            treeTable.removeAllItems();
            List<Group> groups = ProcessbaseApplication.getCurrent().getBpmModule().getAllGroups();
            for (Group group : groups) {
//                System.out.println("group = " + group.getName() + " parent = " + group.getParentGroup());
                Item woItem = treeTable.addItem(group.getUUID());
                TableLinkButton teb = new TableLinkButton(group.getName(), "", null, group, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("label").setValue(group.getLabel());
                woItem.getItemProperty("description").setValue(group.getDescription());
                if (!group.getName().equals(IdentityAPI.DEFAULT_GROUP_NAME)) {
                    TableLinkButton tlb = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDelete"), "icons/cancel.png", group, this, Constants.ACTION_DELETE);
                    woItem.getItemProperty("actions").setValue(tlb);
                }
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
            throw new RuntimeException(ex);
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
                    throw new RuntimeException(ex);
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                GroupWindow ngw = new GroupWindow(group);
                ngw.addListener((Window.CloseListener) this);
                getWindow().addWindow(ngw);
                ngw.initUI();
            }
        }
    }

    private void removeGroup(final Group group) {
        ConfirmDialog.show(getApplication().getMainWindow(),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("windowCaptionConfirm"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("removeGroup") + "?",
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnYes"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                ProcessbaseApplication.getCurrent().getBpmModule().removeGroupByUUID(group.getUUID());
                                treeTable.removeItem(group.getUUID());
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                });
    }
}
