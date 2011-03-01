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
import org.processbase.bam.db.HibernateUtil;
import org.processbase.bam.db.MetaKpi;
import org.processbase.core.Constants;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.ConfirmDialog;

/**
 *
 * @author marat gubaidullin
 */
public class KPIsPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public KPIsPanel() {
        super();
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("id", String.class, null, PbPortlet.getCurrent().messages.getString("id"), null, null);
//        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("code", String.class, null, PbPortlet.getCurrent().messages.getString("code"), null, null);
        table.addContainerProperty("name", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("name"), null, null);
        table.addContainerProperty("description", String.class, null, PbPortlet.getCurrent().messages.getString("description"), null, null);
        table.addContainerProperty("owner", String.class, null, PbPortlet.getCurrent().messages.getString("owner"), null, null);
        table.addContainerProperty("status", String.class, null, PbPortlet.getCurrent().messages.getString("State"), null, null);
        table.addContainerProperty("actions", TableLinkButton.class, null, PbPortlet.getCurrent().messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 100);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            HibernateUtil hutil = new HibernateUtil();

            ArrayList<MetaKpi> metaKpis = hutil.getAllMetaKpi();

            for (MetaKpi metaKpi : metaKpis) {
                Item woItem = table.addItem(metaKpi);
                woItem.getItemProperty("id").setValue(metaKpi.getId());
                woItem.getItemProperty("code").setValue(metaKpi.getCode());
                TableLinkButton teb = new TableLinkButton(metaKpi.getName(), "", null, metaKpi, this, Constants.ACTION_OPEN);
                woItem.getItemProperty("name").setValue(teb);
                woItem.getItemProperty("description").setValue(metaKpi.getDescription());
                woItem.getItemProperty("owner").setValue(metaKpi.getOwner());
                 woItem.getItemProperty("status").setValue(metaKpi.getStatus());
                TableLinkButton tlb = new TableLinkButton(PbPortlet.getCurrent().messages.getString("btnDelete"), "icons/cancel.png", metaKpi, this, Constants.ACTION_DELETE);
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
                nkw.exec();
                nkw.addListener((Window.CloseListener) this);
                getWindow().addWindow(nkw);
            }
        }
    }

    private void removeMetaKpi(final MetaKpi metaKpi) {
        ConfirmDialog.show(PbPortlet.getCurrent().getMainWindow(),
                PbPortlet.getCurrent().messages.getString("windowCaptionConfirm"),
                PbPortlet.getCurrent().messages.getString("removeKPI") + "?",
                PbPortlet.getCurrent().messages.getString("btnYes"),
                PbPortlet.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                HibernateUtil hutil = new HibernateUtil();
                                hutil.deleteMetaKpi(metaKpi);
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
