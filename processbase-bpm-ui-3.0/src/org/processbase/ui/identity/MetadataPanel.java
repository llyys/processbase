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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.util.List;
import org.ow2.bonita.facade.identity.ProfileMetadata;
import org.processbase.core.Constants;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.Processbase;
import org.processbase.ui.template.ConfirmDialog;

/**
 *
 * @author marat gubaidullin
 */
public class MetadataPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public MetadataPanel() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("name", TableLinkButton.class, null, ((Processbase)getApplication()).getMessages().getString("tableCaptionName"), null, null);
        table.addContainerProperty("label", String.class, null, ((Processbase)getApplication()).getMessages().getString("tableCaptionLabel"), null, null);
        table.setColumnExpandRatio("label", 1);
        table.addContainerProperty("actions", TableLinkButton.class, null, ((Processbase)getApplication()).getMessages().getString("tableCaptionActions"), null, null);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            List<ProfileMetadata> metadatas = ((Processbase)getApplication()).getBpmModule().getAllProfileMetadata();

            for (ProfileMetadata metadata : metadatas) {
                Item woItem = table.addItem(metadata);
                TableLinkButton teb = new TableLinkButton(metadata.getName(), "", null, metadata, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("label").setValue(metadata.getLabel());
                TableLinkButton tlb = new TableLinkButton(((Processbase)getApplication()).getMessages().getString("btnDelete"), "icons/cancel.png", metadata, this, Constants.ACTION_DELETE);
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
            ProfileMetadata metadata = (ProfileMetadata) execBtn.getTableValue();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                try {
                    removeMetadata(metadata);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                MetadataWindow nmw = new MetadataWindow(metadata);
                nmw.addListener((Window.CloseListener) this);
                getWindow().addWindow(nmw);
                nmw.initUI();
            }

        }
    }

    private void removeMetadata(final ProfileMetadata metadata) {
        ConfirmDialog.show(getApplication().getMainWindow(),
                ((Processbase)getApplication()).getMessages().getString("windowCaptionConfirm"),
                ((Processbase)getApplication()).getMessages().getString("removeMetadata") + "?",
                ((Processbase)getApplication()).getMessages().getString("btnYes"),
                ((Processbase)getApplication()).getMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                ((Processbase)getApplication()).getBpmModule().removeProfileMetadataByUUID(metadata.getUUID());
                                table.removeItem(metadata);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                showError(ex.getMessage());
                            }
                        }
                    }
                });
    }
}
