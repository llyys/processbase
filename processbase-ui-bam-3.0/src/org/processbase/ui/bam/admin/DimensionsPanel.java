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
package org.processbase.ui.bam.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.util.bam.metadata.HibernateUtil;
import org.processbase.util.bam.metadata.MetaDim;

/**
 *
 * @author marat gubaidullin
 */
public class DimensionsPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public DimensionsPanel() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("id", String.class, null, ((Processbase)getApplication()).getMessages().getString("id"), null, null);
//        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("code", String.class, null, ((Processbase)getApplication()).getMessages().getString("code"), null, null);
        table.addContainerProperty("name", String.class, null, ((Processbase)getApplication()).getMessages().getString("name"), null, null);
        table.addContainerProperty("valueType", String.class, null, ((Processbase)getApplication()).getMessages().getString("valueType"), null, null);
        table.addContainerProperty("length", String.class, null, ((Processbase)getApplication()).getMessages().getString("length"), null, null);
        table.addContainerProperty("actions", TableLinkButton.class, null, ((Processbase)getApplication()).getMessages().getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 100);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            HibernateUtil hutil = new HibernateUtil();

            ArrayList<MetaDim> metaDims = hutil.getAllMetaDim();

            for (MetaDim metaDim : metaDims) {
                Item woItem = table.addItem(metaDim);
                woItem.getItemProperty("id").setValue(metaDim.getId());
                woItem.getItemProperty("code").setValue(metaDim.getCode());
                woItem.getItemProperty("name").setValue(metaDim.getName());
                woItem.getItemProperty("valueType").setValue(metaDim.getValueType());
                woItem.getItemProperty("length").setValue(metaDim.getValueLength());
                TableLinkButton tlb = new TableLinkButton(((Processbase)getApplication()).getMessages().getString("btnDelete"), "icons/cancel.png", metaDim, this, Constants.ACTION_DELETE);
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
            MetaDim metaDim = (MetaDim) execBtn.getTableValue();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                try {
                    removeMetaDim(metaDim);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                DimentionWindow ndw = new DimentionWindow(metaDim);
                ndw.addListener((Window.CloseListener) this);
                getWindow().addWindow(ndw);
                ndw.initUI();
            }
        }
    }

    private void removeMetaDim(final MetaDim metaDim) {
        ConfirmDialog.show(getApplication().getMainWindow(),
                ((Processbase)getApplication()).getMessages().getString("windowCaptionConfirm"),
                ((Processbase)getApplication()).getMessages().getString("removeDimension") + "?",
                ((Processbase)getApplication()).getMessages().getString("btnYes"),
                ((Processbase)getApplication()).getMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                HibernateUtil hutil = new HibernateUtil();
                                hutil.deleteMetaDim(metaDim);
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
