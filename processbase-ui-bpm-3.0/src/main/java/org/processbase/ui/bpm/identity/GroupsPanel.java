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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.identity.Group;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.PagedTablePanel;
import org.processbase.ui.core.template.TableLinkButton;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;

/**
 *
 * @author marat gubaidullin
 */
public class GroupsPanel extends PagedTablePanel implements
        Button.ClickListener {

    /** Serial version UID. */
	private static final long serialVersionUID = -500420946086725732L;

	public GroupsPanel() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("name", TableLinkButton.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionName"), null, null);
        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("label", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionLabel"), null, null);
        table.setColumnExpandRatio("label", 1);
        table.addContainerProperty("description", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionDescription"), null, null);
        table.setColumnExpandRatio("description", 1);
        table.addContainerProperty("actions", TableLinkButton.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"), null, null);
        table.setImmediate(true);

        setInitialized(true);
    }
    
    @Override
    public int load(int startPosition, int maxResults) {
    	int results = 0;
		try {
            table.removeAllItems();
            List<Group> groups = ProcessbaseApplication.getCurrent().getBpmModule().getGroups(startPosition, maxResults);
            for (Group group : groups) {
      	
            	List<Group> path = new ArrayList<Group>();
            	path.add(group);
            	Group c = group;
            	while(c.getParentGroup() != null){
            		path.add(c.getParentGroup());
            		c = c.getParentGroup(); 
            	}
            	Collections.reverse(path);
            	
            	StringBuilder sb = new StringBuilder();
            	for (Group g : path) {
					sb.append("/").append(g.getName());
				}
            	
                Item woItem = table.addItem(group.getUUID());
                TableLinkButton teb = new TableLinkButton(sb.toString(), "", null, group, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("label").setValue(group.getLabel());
                woItem.getItemProperty("description").setValue(group.getDescription());
				if (!(group.getName() != null && group.getName().equals(IdentityAPI.DEFAULT_GROUP_NAME))
						&& !(group.getDescription() != null && group.getDescription().startsWith("AUTO IMPORTED"))) {
					
					TableLinkButton tlb = new TableLinkButton(
							ProcessbaseApplication.getCurrent().getPbMessages()
									.getString("btnDelete"), "icons/cancel.png", group, this, Constants.ACTION_DELETE);
					woItem.getItemProperty("actions").setValue(tlb);
				}
            }
            results = groups.size();
            
//	            for (Group group : groups) {
//	                if (group.getParentGroup() != null) {
//	                    treeTable.setChildrenAllowed(group.getParentGroup().getUUID(), true);
//	                    treeTable.setCollapsed(group.getParentGroup().getUUID(), false);
//	                    treeTable.setParent(group.getUUID(), group.getParentGroup().getUUID());
//	                }
//	            }

            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		
		return results;
    }

    public void buttonClick(ClickEvent event) {
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
                                table.removeItem(group.getUUID());
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
