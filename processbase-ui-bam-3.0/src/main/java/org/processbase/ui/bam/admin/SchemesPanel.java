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
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import java.util.ArrayList;
import java.util.HashMap;
import org.processbase.engine.bam.command.ExecuteScripts;
import org.processbase.engine.bam.command.GenerateSchema;
import org.processbase.engine.bam.command.GetCreateTableScript;
import org.processbase.engine.bam.command.GetDatabaseMetadata;
import org.processbase.engine.bam.command.GetMetaKpiByStatus;
import org.processbase.engine.bam.command.UpdateMetaKpi;
import org.processbase.engine.bam.command.ValidateSchema;
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
public class SchemesPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    private GridLayout databaseInfo = new GridLayout(2, 2);
    private Button generateMetaDataBtn;

    public SchemesPanel() {
        super();
    }

    private void addDatabaseInfo() {
        databaseInfo.setMargin(false);
        databaseInfo.setSpacing(true);
        databaseInfo.setWidth("100%");
        databaseInfo.removeAllComponents();
        generateMetaDataBtn = new Button(ProcessbaseApplication.getString("generateMetaData"), this);
        databaseInfo.addComponent(generateMetaDataBtn, 1, 0);
        generateMetaDataBtn.setVisible(false);
        try {
            HashMap<String, String> dbmd = ProcessbaseApplication.getCurrent().getBpmModule().execute(new GetDatabaseMetadata());
            StringBuilder info = new StringBuilder();
            info.append("<b>").append(dbmd.get("DatabaseProductName")).append(" ").append(dbmd.get("DatabaseProductVersion")).append("</b>");
            databaseInfo.addComponent(new Label(info.toString(), Label.CONTENT_XHTML), 0, 0);
            info = new StringBuilder();
            info.append("<b>").append(dbmd.get("DriverName")).append(" ").append(dbmd.get("DriverVersion")).append("</b>");
            databaseInfo.addComponent(new Label(info.toString(), Label.CONTENT_XHTML), 0, 1);
            ProcessbaseApplication.getCurrent().getBpmModule().execute(new ValidateSchema());
        } catch (Exception ex) {
            ex.printStackTrace();
            generateMetaDataBtn.setVisible(true);
        }
        addComponent(databaseInfo, 0);
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("id", String.class, null, ProcessbaseApplication.getString("id"), null, null);
//        table.setColumnExpandRatio("name", 1);
        table.addContainerProperty("code", String.class, null, ProcessbaseApplication.getString("code"), null, null);
        table.addContainerProperty("name", String.class, null, ProcessbaseApplication.getString("name"), null, null);
        table.addContainerProperty("owner", String.class, null, ProcessbaseApplication.getString("owner"), null, null);
        table.addContainerProperty("status", String.class, null, ProcessbaseApplication.getString("State"), null, null);
        table.addContainerProperty("actions", TableLinkButton.class, null, ProcessbaseApplication.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 100);
        table.setImmediate(true);

        addDatabaseInfo();
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            ArrayList<MetaKpi> metaKpis = ProcessbaseApplication.getCurrent().getBpmModule().execute(new GetMetaKpiByStatus(MetaKpi.EDITABLE));

            for (MetaKpi metaKpi : metaKpis) {
                Item woItem = table.addItem(metaKpi);
                woItem.getItemProperty("id").setValue(metaKpi.getId());
                woItem.getItemProperty("code").setValue(metaKpi.getCode());
                woItem.getItemProperty("name").setValue(metaKpi.getName());
                woItem.getItemProperty("owner").setValue(metaKpi.getOwner());
                woItem.getItemProperty("status").setValue(metaKpi.getStatus());
                TableLinkButton tlb = new TableLinkButton(ProcessbaseApplication.getString("btnGenerate"), "icons/start.png", metaKpi, this, Constants.ACTION_START);
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
                nkw.addListener((Window.CloseListener) this);
                getWindow().addWindow(nkw);
                nkw.initUI();
            }
        } else if (event.getButton().equals(generateMetaDataBtn)) {
            generateMetaDataSchema();
        }
    }

    private void generateMetaDataSchema() {
        ConfirmDialog.show(getApplication().getMainWindow(),
                ProcessbaseApplication.getString("windowCaptionConfirm"),
                ProcessbaseApplication.getString("generateSchema") + "?",
                ProcessbaseApplication.getString("btnYes"),
                ProcessbaseApplication.getString("btnNo"),
                new ConfirmDialog.Listener() { 

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                ProcessbaseApplication.getCurrent().getBpmModule().execute(new GenerateSchema());
                                generateMetaDataBtn.setVisible(false);
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void generateTable(final MetaKpi metaKpi) throws Exception {
        final ArrayList<String> scripts = ProcessbaseApplication.getCurrent().getBpmModule().execute(new GetCreateTableScript(metaKpi));
        ConfirmDialog.show(getApplication().getMainWindow(),
                ProcessbaseApplication.getString("windowCaptionConfirm"),
                scripts.get(0),
                ProcessbaseApplication.getString("btnYes"),
                ProcessbaseApplication.getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                ProcessbaseApplication.getCurrent().getBpmModule().execute(new ExecuteScripts(scripts));
                                metaKpi.setStatus(MetaKpi.NOT_EDITABLE);
                                ProcessbaseApplication.getCurrent().getBpmModule().execute(new UpdateMetaKpi(metaKpi));
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
