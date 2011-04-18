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
package org.processbase.ui.bpm.development;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaadin.data.Item;
import java.io.IOException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Properties;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.ui.osgi.PbPanelModuleService;

/**
 *
 * @author marat gubaidullin
 */
public class ModulesTabPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public ModulesTabPanel() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("order", String.class, null, "Order", null, null);
        table.addContainerProperty("moduleName", TableLinkButton.class, null, "Class Name", null, null);
        table.addContainerProperty("title", String.class, null, "Title", null, null);
        table.addContainerProperty("inMetadata", String.class, null, "PB Metadata", null, null);
        table.addContainerProperty("inOSGI", String.class, null, "OSGI", null, null);
        table.addContainerProperty("actions", TableLinkButton.class, null, ((Processbase) getApplication()).getMessages().getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 80);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            LinkedHashSet<String> moduleNames = new LinkedHashSet<String>();
            GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            Gson gson = gb.create();
            LinkedList<String> metadataList = new LinkedList<String>();
            String metaDataString = ((Processbase) getApplication()).getBpmModule().getMetaData("PROCESSBASE_TABSHEETS_LIST");
            if (metaDataString != null) {
                String[] savedTabList = gson.fromJson(metaDataString, String[].class);
                if (savedTabList != null && savedTabList.length > 0) {
                    metadataList.addAll(Arrays.asList(savedTabList));
                }
            }
            moduleNames.addAll(metadataList);
            PbPanelModuleService pms = ((Processbase) getApplication()).getPanelModuleService();
            moduleNames.addAll(pms.getModules().keySet());
            int i = 1;
            for (String moduleName : moduleNames) {
                Item woItem = table.addItem(moduleName);
                TableLinkButton teb = new TableLinkButton(moduleName, moduleName, null, moduleName, this, Constants.ACTION_OPEN);
                Locale locale = getApplication().getLocale();
                String title = pms.getModules().containsKey(moduleName) ? pms.getModules().get(moduleName).getTitle(locale) : "";
                woItem.getItemProperty("order").setValue(String.valueOf(i));
                woItem.getItemProperty("moduleName").setValue(teb);
                woItem.getItemProperty("title").setValue(title);
                woItem.getItemProperty("inOSGI").setValue(pms.getModules().containsKey(moduleName));
                woItem.getItemProperty("inMetadata").setValue(metadataList.contains(moduleName));
                TableLinkButton tlb = new TableLinkButton(((Processbase) getApplication()).getMessages().getString("btnDelete"), "icons/cancel.png", moduleName, this, Constants.ACTION_DELETE);
                woItem.getItemProperty("actions").setValue(tlb);
                i++;
            }
            table.setSortContainerPropertyId("order");
            table.sort();
            table.setSortDisabled(true);
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
                    String fileName = (String) execBtn.getTableValue();
                    removeJar(fileName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            }
        }
    }

    private void removeJar(final String fileName) {
        ConfirmDialog.show(getApplication().getMainWindow(),
                ((Processbase) getApplication()).getMessages().getString("windowCaptionConfirm"),
                ((Processbase) getApplication()).getMessages().getString("removeUser") + "?",
                ((Processbase) getApplication()).getMessages().getString("btnYes"),
                ((Processbase) getApplication()).getMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                Properties p = System.getProperties();
                                String instanceRoot = p.getProperty("com.sun.aas.instanceRootURI");
                                String fileSeparator = p.getProperty("file.separator");
                                File f = new File(new URI(instanceRoot + Constants.CUSTOM_UI_JAR_PATH + fileSeparator + fileName));
                                f.delete();
                                saveJarInfo(fileName);
                                table.removeItem(fileName);
                            } catch (Exception ex) {
                                showError(ex.getMessage());
                                ex.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void removeJarFile(String name) throws IOException {
        try {
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void saveJarInfo(String name) {
        try {
            BPMModule bpm = ((Processbase) getApplication()).getBpmModule();
            // save metadata
            GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            Gson gson = gb.create();
            Type collectionType = new TypeToken<LinkedList<String>>() {
            }.getType();
            LinkedList<String> jarList = new LinkedList<String>();
            String metaDataString = bpm.getMetaData("PROCESSBASE_UI_JAR_LIST");
            if (metaDataString != null) {
                LinkedList<String> savedJarList = gson.fromJson(metaDataString, collectionType);
                if (!savedJarList.isEmpty()) {
                    jarList.addAll(savedJarList);
                }
            }
            jarList.remove(name);
            metaDataString = gson.toJson(jarList, collectionType);
            bpm.addMetaData("PROCESSBASE_UI_JAR_LIST", metaDataString);
            // create rule
//            Rule rule = bpm.createRule(name, name, name, RuleType.CUSTOM);
//            Set<CustomUUID> uis = new HashSet<CustomUUID>(1);
//            uis.add(new CustomUUID(name));
//            bpm.addExceptionsToRuleByUUID(rule.getUUID(), uis);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
