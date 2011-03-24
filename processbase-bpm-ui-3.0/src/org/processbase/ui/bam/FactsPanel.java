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

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import org.processbase.bam.metadata.HibernateUtil;
import org.processbase.bam.metadata.MetaDim;
import org.processbase.bam.metadata.MetaFact;
import org.processbase.core.Constants;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.Processbase;
import org.processbase.ui.template.ConfirmDialog;

/**
 *
 * @author marat gubaidullin
 */
public class FactsPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public FactsPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("id", String.class, null, Processbase.getCurrent().messages.getString("id"), null, null);
//        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("code", String.class, null, Processbase.getCurrent().messages.getString("code"), null, null);
        table.addContainerProperty("name", String.class, null, Processbase.getCurrent().messages.getString("name"), null, null);
        table.addContainerProperty("actions", TableLinkButton.class, null, Processbase.getCurrent().messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 100);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            HibernateUtil hutil = new HibernateUtil();

            ArrayList<MetaFact> metaFacts = hutil.getAllMetaFact();

            for (MetaFact metaFact : metaFacts) {
                Item woItem = table.addItem(metaFact);
                woItem.getItemProperty("id").setValue(metaFact.getId());
                woItem.getItemProperty("code").setValue(metaFact.getCode());
                woItem.getItemProperty("name").setValue(metaFact.getName());
                TableLinkButton tlb = new TableLinkButton(Processbase.getCurrent().messages.getString("btnDelete"), "icons/cancel.png", metaFact, this, Constants.ACTION_DELETE);
                woItem.getItemProperty("actions").setValue(tlb);
            }
            table.setSortContainerPropertyId("id");
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
            MetaFact metaFact = (MetaFact) execBtn.getTableValue();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                try {
                    removeMetaFact(metaFact);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                FactWindow nfw = new FactWindow(metaFact);
                nfw.exec();
                nfw.addListener((Window.CloseListener) this);
                getWindow().addWindow(nfw);
            }
        }
    }

    private void removeMetaFact(final MetaFact metaFact) {
        ConfirmDialog.show(getApplication().getMainWindow(),
                Processbase.getCurrent().messages.getString("windowCaptionConfirm"),
                Processbase.getCurrent().messages.getString("removeFact") + "?",
                Processbase.getCurrent().messages.getString("btnYes"),
                Processbase.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                HibernateUtil hutil = new HibernateUtil();
                                hutil.deleteMetaFact(metaFact);
                                refreshTable();
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }
}
