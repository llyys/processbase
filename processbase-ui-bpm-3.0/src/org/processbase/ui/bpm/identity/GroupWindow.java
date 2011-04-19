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
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.List;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.identity.Group;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class GroupWindow extends PbWindow implements ClickListener {

    private Group group = null;
    private ButtonBar buttons = new ButtonBar();
    private Button cancelBtn;
    private Button applyBtn;
    private TextField groupName;
    private TextField groupLabel;
    private TextArea groupDescription;
    private ComboBox parentGroup;

    public GroupWindow(Group group) {
        super();
        this.group = group;
    }

    public void initUI() {
        try {
            if (group == null) {
                setCaption(((Processbase) getApplication()).getPbMessages().getString("newGroup"));
            } else {
                setCaption(((Processbase) getApplication()).getPbMessages().getString("group"));
            }
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);

            cancelBtn = new Button(((Processbase) getApplication()).getPbMessages().getString("btnCancel"), this);
            applyBtn = new Button(((Processbase) getApplication()).getPbMessages().getString("btnSave"), this);
            groupName = new TextField(((Processbase) getApplication()).getPbMessages().getString("groupName"));
            groupLabel = new TextField(((Processbase) getApplication()).getPbMessages().getString("groupLabel"));
            groupDescription = new TextArea(((Processbase) getApplication()).getPbMessages().getString("groupDescription"));
            parentGroup = new ComboBox(((Processbase) getApplication()).getPbMessages().getString("groupParent"));

            parentGroup.setWidth("270px");
            parentGroup.setContainerDataSource(getGroups());
            parentGroup.setItemCaptionPropertyId("path");
            parentGroup.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
            addComponent(parentGroup);
            groupName.setWidth("270px");
            addComponent(groupName);
            groupLabel.setWidth("270px");
            addComponent(groupLabel);
            groupDescription.setWidth("270px");
            addComponent(groupDescription);

            if (group != null) {
                groupName.setValue(group.getName());
                groupLabel.setValue(group.getLabel());
                groupDescription.setValue(group.getDescription());
                if (group.getParentGroup() != null) {
                    parentGroup.setValue(group.getParentGroup().getUUID());
                }
            }


            buttons.addButton(applyBtn);
            buttons.setComponentAlignment(applyBtn, Alignment.MIDDLE_RIGHT);
            buttons.setExpandRatio(applyBtn, 1);
            buttons.addButton(cancelBtn);
            buttons.setComponentAlignment(cancelBtn, Alignment.MIDDLE_RIGHT);
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
                if (group == null) {
                    ((Processbase) getApplication()).getBpmModule().addGroup(
                            groupName.getValue().toString(),
                            groupLabel.getValue().toString(),
                            groupDescription.getValue().toString(),
                            parentGroup.getValue() != null ? parentGroup.getValue().toString() : null);
                } else {
                    ((Processbase) getApplication()).getBpmModule().updateGroupByUUID(
                            group.getUUID(),
                            groupName.getValue().toString(),
                            groupLabel.getValue().toString(),
                            groupDescription.getValue().toString(),
                            parentGroup.getValue() != null ? parentGroup.getValue().toString() : null);
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

    public IndexedContainer getGroups() throws Exception {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("name", String.class, null);
        container.addContainerProperty("label", String.class, null);
        container.addContainerProperty("uuid", String.class, null);
        container.addContainerProperty("path", String.class, null);
        List<Group> groups = ((Processbase) getApplication()).getBpmModule().getAllGroups();
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
}
