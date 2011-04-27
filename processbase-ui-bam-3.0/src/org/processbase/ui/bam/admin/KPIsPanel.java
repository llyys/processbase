/**
 * Copyright (C) 2011 PROCESSBASE Ltd.
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
import org.processbase.engine.bam.command.DeleteMetaKpi;
import org.processbase.engine.bam.command.GetAllMetaKpi;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.engine.bam.metadata.MetaKpi;
import org.processbase.ui.core.ProcessbaseApplication;

/**
 *
 * @author marat gubaidullin
 */
public class KPIsPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public KPIsPanel() {
        super();
    }

    @Override
        public void initUI() {
        super.initUI();
        table.addContainerProperty("id", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("id"), null, null);
//        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("code", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("code"), null, null);
        table.addContainerProperty("name", TableLinkButton.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("name"), null, null);
        table.addContainerProperty("description", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("description"), null, null);
        table.addContainerProperty("owner", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("owner"), null, null);
        table.addContainerProperty("status", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("State"), null, null);
        table.addContainerProperty("actions", TableLinkButton.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 100);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            ArrayList<MetaKpi> metaKpis = ProcessbaseApplication.getCurrent().getBpmModule().execute(new GetAllMetaKpi());

            for (MetaKpi metaKpi : metaKpis) {
                Item woItem = table.addItem(metaKpi);
                woItem.getItemProperty("id").setValue(metaKpi.getId());
                woItem.getItemProperty("code").setValue(metaKpi.getCode());
                TableLinkButton teb = new TableLinkButton(metaKpi.getName(), "", null, metaKpi, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("description").setValue(metaKpi.getDescription());
                woItem.getItemProperty("owner").setValue(metaKpi.getOwner());
                 woItem.getItemProperty("status").setValue(metaKpi.getStatus());
                TableLinkButton tlb = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDelete"), "icons/cancel.png", metaKpi, this, Constants.ACTION_DELETE);
                tlb.setEnabled(metaKpi.getStatus().equals("EDITABLE"));
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
            MetaKpi metaKpi = (MetaKpi) execBtn.getTableValue();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                try {
                    removeMetaKpi(metaKpi);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                KPIWindow nkw = new KPIWindow(metaKpi);
                nkw.addListener((Window.CloseListener) this);
                getWindow().addWindow(nkw);
                nkw.initUI();
            }
        }
    }

    private void removeMetaKpi(final MetaKpi metaKpi) {
        ConfirmDialog.show(getApplication().getMainWindow(),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("windowCaptionConfirm"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("removeKPI") + "?",
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnYes"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                ProcessbaseApplication.getCurrent().getBpmModule().execute(new DeleteMetaKpi(metaKpi));
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
