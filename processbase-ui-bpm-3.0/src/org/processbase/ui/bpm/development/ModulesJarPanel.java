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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Window;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.LinkedList;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.TableLinkButton;
import org.processbase.ui.core.template.TablePanel;

/**
 *
 * @author marat gubaidullin
 */
public class ModulesJarPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener {

    public ModulesJarPanel() {
        super();
    }

    @Override
    public void initUI() {
        super.initUI();
        table.addContainerProperty("fileName", TableLinkButton.class, null, "File Name", null, null);
//        table.setColumnExpandRatio("fileName", 1);
        table.addContainerProperty("bungleName", String.class, null, "Bundle-Name", null, null);
        table.addContainerProperty("bungleVersion", String.class, null, "Bundle-Version", null, null);
        table.setColumnWidth("bungleVersion", 100);
        table.addContainerProperty("actions", TableLinkButton.class, null, ((Processbase) getApplication()).getMessages().getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 80);
        table.setImmediate(true);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            Gson gson = gb.create();
            Type collectionType = new TypeToken<LinkedList<String>>() {
            }.getType();
            LinkedList<String> jarList = new LinkedList<String>();
            String metaDataString = ((Processbase) getApplication()).getBpmModule().getMetaData("PROCESSBASE_UI_JAR_LIST");
            if (metaDataString != null) {
                LinkedList<String> savedJarList = gson.fromJson(metaDataString, collectionType);
                if (!savedJarList.isEmpty()) {
                    jarList.addAll(savedJarList);
                }
            }
            for (String name : jarList) {
                Item woItem = table.addItem(name);
                TableLinkButton teb = new TableLinkButton(name, name, null, name, this, Constants.ACTION_OPEN);


                Attributes mainAttributes = getManifestAttributes(name);
                String bungleVersion = "";
                String bungleName = "MANIFEST ATTRIBUTES NOT FOUND";
                if (mainAttributes != null) {
                    bungleVersion = mainAttributes.getValue("Bundle-Version");
                    bungleName = mainAttributes.getValue("Bundle-Name");
                } else {
                    teb.setEnabled(false);
                }
                woItem.getItemProperty("fileName").setValue(teb);
                woItem.getItemProperty("bungleName").setValue(bungleName);
                woItem.getItemProperty("bungleVersion").setValue(bungleVersion);
                TableLinkButton tlb = new TableLinkButton(((Processbase) getApplication()).getMessages().getString("btnDelete"), "icons/cancel.png", name, this, Constants.ACTION_DELETE);
                woItem.getItemProperty("actions").setValue(tlb);
            }
            table.setSortContainerPropertyId("fileName");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    private Attributes getManifestAttributes(String fileName) throws URISyntaxException, FileNotFoundException, IOException {
        Properties p = System.getProperties();
        String instanceRoot = p.getProperty("com.sun.aas.instanceRootURI");
        String fileSeparator = p.getProperty("file.separator");
        File file = new File(new URI(instanceRoot + Constants.CUSTOM_UI_JAR_PATH + fileSeparator + fileName));
        byte[] bytes = new byte[Integer.parseInt(Long.toString(file.length()))];
        FileInputStream fis = new FileInputStream(file);
        final JarInputStream jis = new JarInputStream(fis);
        Manifest manifest = jis.getManifest();
        if (manifest != null) {
            Attributes mainAttributes = manifest.getMainAttributes();
            return mainAttributes;
        }
        return null;
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
                ((Processbase) getApplication()).getMessages().getString("deleteJar") + "?",
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
