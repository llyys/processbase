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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import org.processbase.bam.metadata.ScriptGenerator;
import org.processbase.bam.metadata.HibernateUtil;
import org.processbase.bam.metadata.MetaKpi;
import org.processbase.core.Constants;
import org.processbase.ui.template.TableLinkButton;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.Processbase;
import org.processbase.ui.template.ConfirmDialog;

/**
 *
 * @author marat gubaidullin
 */
public class SchemesPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    private GridLayout databaseInfo = new GridLayout(2, 2);
    private Button generateMetaDataBtn = new Button(Processbase.getCurrent().messages.getString("generateMetaData"), this);

    public SchemesPanel() {
        super();
        initTableUI();
        addDatabaseInfo();
    }

    private void addDatabaseInfo() {
        databaseInfo.setMargin(false);
        databaseInfo.setSpacing(true);
        databaseInfo.setWidth("100%");
        databaseInfo.removeAllComponents();
        databaseInfo.addComponent(generateMetaDataBtn, 1, 0);
        generateMetaDataBtn.setVisible(false);
        try {
            HibernateUtil hutil = new HibernateUtil();
            DatabaseMetaData dbmd = hutil.getDatabaseMetadata();
            StringBuilder info = new StringBuilder();
            info.append("<b>").append(dbmd.getDatabaseProductName()).append(" ").append(dbmd.getDatabaseProductVersion()).append("</b>");
            databaseInfo.addComponent(new Label(info.toString(), Label.CONTENT_XHTML), 0, 0);
            info = new StringBuilder();
            info.append("<b>").append(dbmd.getDriverName()).append(" ").append(dbmd.getDriverVersion()).append("</b>");
            databaseInfo.addComponent(new Label(info.toString(), Label.CONTENT_XHTML), 0, 1);
            hutil.validateSchema();
        } catch (Exception ex) {
            ex.printStackTrace();
            generateMetaDataBtn.setVisible(true);
        }
        addComponent(databaseInfo, 0);
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("id", String.class, null, Processbase.getCurrent().messages.getString("id"), null, null);
//        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("code", String.class, null, Processbase.getCurrent().messages.getString("code"), null, null);
        table.addContainerProperty("name", String.class, null, Processbase.getCurrent().messages.getString("name"), null, null);
        table.addContainerProperty("owner", String.class, null, Processbase.getCurrent().messages.getString("owner"), null, null);
        table.addContainerProperty("status", String.class, null, Processbase.getCurrent().messages.getString("State"), null, null);
        table.addContainerProperty("actions", TableLinkButton.class, null, Processbase.getCurrent().messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 100);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            HibernateUtil hutil = new HibernateUtil();

            ArrayList<MetaKpi> metaKpis = hutil.getAllMetaKpiByStatus(MetaKpi.EDITABLE);

            for (MetaKpi metaKpi : metaKpis) {
                Item woItem = table.addItem(metaKpi);
                woItem.getItemProperty("id").setValue(metaKpi.getId());
                woItem.getItemProperty("code").setValue(metaKpi.getCode());
                woItem.getItemProperty("name").setValue(metaKpi.getName());
                woItem.getItemProperty("owner").setValue(metaKpi.getOwner());
                woItem.getItemProperty("status").setValue(metaKpi.getStatus());
                TableLinkButton tlb = new TableLinkButton(Processbase.getCurrent().messages.getString("btnGenerate"), "icons/start.png", metaKpi, this, Constants.ACTION_START);
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
            if (execBtn.getAction().equals(Constants.ACTION_START)) {
                try {
                    generateTable(metaKpi);
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
        } else if (event.getButton().equals(generateMetaDataBtn)) {
            generateMetaDataSchema();
        }
    }

    private void generateMetaDataSchema() {
        ConfirmDialog.show(getApplication().getMainWindow(),
                Processbase.getCurrent().messages.getString("windowCaptionConfirm"),
                Processbase.getCurrent().messages.getString("generateSchema") + "?",
                Processbase.getCurrent().messages.getString("btnYes"),
                Processbase.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                HibernateUtil hutil = new HibernateUtil();
                                hutil.generateSchema();
                                generateMetaDataBtn.setVisible(false);
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void generateTable(final MetaKpi metaKpi) {
        ScriptGenerator srciptor = new ScriptGenerator();
        final ArrayList<String> scripts = srciptor.getCreateTableScript(metaKpi, ScriptGenerator.CREATE_SCRIPT);
        ConfirmDialog.show(getApplication().getMainWindow(),
                Processbase.getCurrent().messages.getString("windowCaptionConfirm"),
                scripts.get(0),
                Processbase.getCurrent().messages.getString("btnYes"),
                Processbase.getCurrent().messages.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                HibernateUtil hutil = new HibernateUtil();
                                hutil.executeScripts(scripts);
                                metaKpi.setStatus(MetaKpi.NOT_EDITABLE);
                                hutil.updateMetaKpi(metaKpi);
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
