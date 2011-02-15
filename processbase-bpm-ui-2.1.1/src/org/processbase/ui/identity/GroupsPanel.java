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

/**
 *
 * @author marat gubaidullin
 */
public class GroupsPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public GroupsPanel() {
        super();
        initTableUI();
    }


    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("name", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionName"), null, null);
        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("label", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionLabel"), null, null);
        table.addContainerProperty("description", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionDescription"), null, null);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            List<Group> groups = PbPortlet.getCurrent().bpmModule.getAllGroups();

            for (Group group : groups) {
                Item woItem = table.addItem(group);
                TableLinkButton teb = new TableLinkButton(group.getName(), "", null, group, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("label").setValue(group.getLabel());
                woItem.getItemProperty("description").setValue(group.getDescription());
            }
            table.setSortContainerPropertyId("name");
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
                if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                try {
                    CategoryWindow categoryWindow = new CategoryWindow((Category) execBtn.getTableValue());
                    categoryWindow.exec();
                    getApplication().getMainWindow().addWindow(categoryWindow);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            }
        }
    }

    
}
