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
import org.ow2.bonita.facade.identity.ProfileMetadata;
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
public class MetadataPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public MetadataPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("name", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionName"), null, null);
        table.addContainerProperty("label", String.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionLabel"), null, null);
        table.setColumnExpandRatio("label", 1);
        table.addContainerProperty("actions", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionActions"), null, null);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            List<ProfileMetadata> metadatas = PbPortlet.getCurrent().bpmModule.getAllProfileMetadata();

            for (ProfileMetadata metadata : metadatas) {
                Item woItem = table.addItem(metadata);
                woItem.getItemProperty("name").setValue(metadata.getName());
                woItem.getItemProperty("label").setValue(metadata.getLabel());
                TableLinkButton tlb = new TableLinkButton(PbPortlet.getCurrent().messages.getString("btnDelete"), "icons/cancel.png", metadata, this, Constants.ACTION_DELETE);
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
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                try {
                    ProfileMetadata metadata = (ProfileMetadata) execBtn.getTableValue();
                    PbPortlet.getCurrent().bpmModule.removeProfileMetadataByUUID(metadata.getUUID());
                    table.removeItem(metadata);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            }
        }
    }
}
