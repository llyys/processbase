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
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TreeMap;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
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
        table.addContainerProperty("moduleName", Component.class, null, "Class Name", null, null);
        table.addContainerProperty("title", String.class, null, "Title", null, null);
        table.addContainerProperty("inMetadata", String.class, null, "PB Metadata", null, null);
        table.addContainerProperty("inOSGI", String.class, null, "OSGI", null, null);
        table.addContainerProperty("actions", TableLinkButton.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 80);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            LinkedHashSet<String> moduleNames = new LinkedHashSet<String>();
            GsonBuilder gb = new GsonBuilder();
            Gson gson = gb.create();
            Type collectionType = new TypeToken<LinkedHashMap<Integer, String>>() {
            }.getType();
            TreeMap<Integer, String> tabList = new TreeMap<Integer, String>();
            String metaDataString = ProcessbaseApplication.getCurrent().getBpmModule().getMetaData("PROCESSBASE_TABSHEETS_LIST");
            if (metaDataString != null) {
                LinkedHashMap<Integer, String> tabs2 = gson.fromJson(metaDataString, collectionType);
                if (!tabs2.isEmpty()) {
                    tabList.putAll(tabs2);
                }
            }
            moduleNames.addAll(tabList.values());
            PbPanelModuleService pms = ProcessbaseApplication.getCurrent().getPanelModuleService();
            moduleNames.addAll(pms.getModules().keySet());
            int i = 1;
            for (String moduleName : moduleNames) {
                Item woItem = table.addItem(moduleName);
                Locale locale = getApplication().getLocale();
                String title = pms.getModules().containsKey(moduleName) ? pms.getModules().get(moduleName).getTitle(locale) : "";
                Component teb = null;
                if (pms.getModules().containsKey(moduleName) && tabList.containsValue(moduleName)) {
                    teb = new TableLinkButton(moduleName, moduleName, null, moduleName, this, Constants.ACTION_OPEN);
                } else if (!pms.getModules().containsKey(moduleName) && tabList.containsValue(moduleName)) {
                    teb = new TableLinkButton(moduleName, moduleName, null, moduleName, this, Constants.ACTION_OPEN);
                    TableLinkButton tlb = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDeleteFromMetadta"), "icons/cancel.png", moduleName, this, Constants.ACTION_DELETE);
                    woItem.getItemProperty("actions").setValue(tlb);
                } else if (pms.getModules().containsKey(moduleName) && !tabList.containsValue(moduleName)) {
                    teb = new Label(moduleName);
                    TableLinkButton tlb = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnAddToMetadata"), "icons/accept.png", moduleName, this, Constants.ACTION_ADD);
                    woItem.getItemProperty("actions").setValue(tlb);
                }
                woItem.getItemProperty("order").setValue(String.valueOf(i));
                woItem.getItemProperty("moduleName").setValue(teb);
                woItem.getItemProperty("title").setValue(title);
                woItem.getItemProperty("inOSGI").setValue(pms.getModules().containsKey(moduleName));
                woItem.getItemProperty("inMetadata").setValue(tabList.containsValue(moduleName));
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
            String moduleName = (String) execBtn.getTableValue();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                try {
//                    String fileName = (String) execBtn.getTableValue();
//                    removeJar(fileName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                try {
                    System.out.println("----------------------------------------------");
                    System.out.println(Class.forName(moduleName));
                    System.out.println(Class.forName(moduleName).getClassLoader().getClass().getCanonicalName());
                    System.out.println(Class.forName(moduleName).getAnnotations().length);
                    System.out.println(Class.forName(moduleName).getDeclaredAnnotations());
                    System.out.println("----------------------------------------------");
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void saveTabsheetMetadata(String name) {
        try {
            BPMModule bpm = ProcessbaseApplication.getCurrent().getBpmModule();
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
