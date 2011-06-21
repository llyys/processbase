/**
 * Copyright (C) 2011 PROCESSBASE
 * PROCESSBASE Ltd, Almaty, Kazakhstan
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
package org.processbase.ui.bpm.panel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vaadin.data.Item;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import java.util.Locale;
import org.processbase.ui.bpm.admin.ActivityInstancesPanel;
import org.processbase.ui.bpm.admin.ProcessInstancesPanel;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import java.util.Set;
import org.ow2.bonita.facade.privilege.Rule;
import org.ow2.bonita.facade.privilege.Rule.RuleType;
import org.ow2.bonita.facade.uuid.RuleExceptionUUID;
import org.processbase.ui.bpm.development.ModulesJarPanel;
import org.processbase.ui.bpm.development.ModulesTabPanel;
import org.processbase.ui.bpm.development.NewJarWindow;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.ui.osgi.PbPanelModule;

/**
 *
 * @author mgubaidullin
 */
public class DevelopmentPanel extends PbPanelModule
        implements Button.ClickListener, Window.CloseListener {

    private ButtonBar buttonBar = new ButtonBar();
    private ModulesJarPanel modulesJarPanel;
    private ModulesTabPanel modulesTabPanel;
    private ProcessInstancesPanel processInstancesPanel;
    private ActivityInstancesPanel activityInstancesPanel;
    private Button refreshBtn = null;
    private Button btnAdd = null;
    private Button modulesJarBtn = null;
    private Button processInstancesBtn = null;
    private Button activityInstancesBtn = null;
    private Button modulesTabBtn = null;
    private HashMap<Button, TablePanel> panels = new HashMap<Button, TablePanel>();

    public void initUI() {
        panels.clear();
        removeAllComponents();
        setMargin(false);

        prepareButtonBar();
        addComponent(buttonBar, 0);

        modulesJarPanel = new ModulesJarPanel();
        panels.put(modulesJarBtn, modulesJarPanel);
        addComponent(modulesJarPanel, 1);
        setExpandRatio(modulesJarPanel, 1);
        modulesJarPanel.initUI();
        modulesJarPanel.refreshTable();

        processInstancesPanel = new ProcessInstancesPanel();
        panels.put(processInstancesBtn, processInstancesPanel);

        activityInstancesPanel = new ActivityInstancesPanel();
        panels.put(activityInstancesBtn, activityInstancesPanel);

        modulesTabPanel = new ModulesTabPanel();
        panels.put(modulesTabBtn, modulesTabPanel);

    }

    private void setCurrentPanel(TablePanel tablePanel) {
        replaceComponent(getComponent(1), tablePanel);
        setExpandRatio(tablePanel, 1);
        if (!tablePanel.isInitialized()) {
            tablePanel.initUI();
        }
        if (tablePanel.equals(modulesJarPanel) || tablePanel.equals(modulesTabPanel)) {
            tablePanel.refreshTable();
        }
    }

    private void prepareButtonBar() {
        buttonBar.removeAllComponents();
        // prepare JarFilesBtn button
        modulesJarBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("modulesJarBtn"), this);
        modulesJarBtn.setStyleName("special");
        modulesJarBtn.setEnabled(false);
        buttonBar.addComponent(modulesJarBtn, 0);
        buttonBar.setComponentAlignment(modulesJarBtn, Alignment.MIDDLE_LEFT);

        // prepare modulesTabBtn button
        modulesTabBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("modulesTabBtn"), this);
        modulesTabBtn.setDescription(ProcessbaseApplication.getCurrent().getPbMessages().getString("modulesTabBtn"));
        modulesTabBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(modulesTabBtn, 1);
        buttonBar.setComponentAlignment(modulesTabBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskListBtn button
        processInstancesBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("processInstancesBtn"), this);
        processInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(processInstancesBtn, 2);
        buttonBar.setComponentAlignment(processInstancesBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskArchiveBtn button
        activityInstancesBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("activityInstancesBtn"), this);
        activityInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(activityInstancesBtn, 3);
        buttonBar.setComponentAlignment(activityInstancesBtn, Alignment.MIDDLE_LEFT);

        // add expand label
        Label expandLabel = new Label("");
        buttonBar.addComponent(expandLabel, 4);
        buttonBar.setExpandRatio(expandLabel, 1);

        // prepare refresh button
        refreshBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnRefresh"), this);
        buttonBar.addComponent(refreshBtn, 5);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);

        // prepare add button
        btnAdd = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnAdd"), this);
        buttonBar.addComponent(btnAdd, 6);
        buttonBar.setComponentAlignment(btnAdd, Alignment.MIDDLE_RIGHT);
        buttonBar.setWidth("100%");
    }

    public void buttonClick(ClickEvent event) {
        TablePanel panel = panels.get(event.getButton());
        if (event.getButton().equals(refreshBtn)) {
            ((TablePanel) getComponent(1)).refreshTable();
        } else if (event.getButton().equals(btnAdd)) {
            if (getComponent(1).equals(modulesTabPanel)) {
                saveMetadata();
                modulesTabPanel.refreshTable();
            } else if (getComponent(1).equals(modulesJarPanel)) {
                NewJarWindow njw = new NewJarWindow();
                njw.addListener((Window.CloseListener) this);
                getApplication().getMainWindow().addWindow(njw);
                njw.initUI();
            }
        } else {
            activateButtons();
            event.getButton().setStyleName("special");
            event.getButton().setEnabled(false);
            setCurrentPanel(panel);
            if (getComponent(1).equals(modulesTabPanel)) {
                btnAdd.setCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnAddToMetadata"));
            } else if (getComponent(1).equals(modulesJarPanel)) {
                btnAdd.setCaption(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnAdd"));
            }
        }

    }

    private void activateButtons() {
        modulesJarBtn.setStyleName(Reindeer.BUTTON_LINK);
        modulesJarBtn.setEnabled(true);
        processInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        processInstancesBtn.setEnabled(true);
        activityInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        activityInstancesBtn.setEnabled(true);
        modulesTabBtn.setStyleName(Reindeer.BUTTON_LINK);
        modulesTabBtn.setEnabled(true);
        btnAdd.setVisible(true);
    }

    private void saveMetadata() {
        ConfirmDialog.show(getApplication().getMainWindow(),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("windowCaptionConfirm"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnAddToMetadata") + "?",
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnYes"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                saveTabsheetMetadata();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                ((PbWindow) getApplication().getMainWindow()).showError(ex.getMessage());
                            }
                        }
                    }
                });
    }

    private void saveTabsheetMetadata() {
        try {
            BPMModule bpm = ProcessbaseApplication.getCurrent().getBpmModule();
            // save metadata
            GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            Gson gson = gb.create();
            Type collectionType = new TypeToken<LinkedHashMap<Integer, String>>(){}.getType();
            Collection tableIds = modulesTabPanel.getTable().getItemIds();
            LinkedHashMap<Integer, String> tabs = new LinkedHashMap<Integer, String>();
            int i = 0;
            for (Object o : tableIds) {
                String name = o.toString();
                Item item = modulesTabPanel.getTable().getItem(name);
                Boolean inOSGI = new Boolean(item.getItemProperty("inOSGI").getValue().toString());
                if (inOSGI) {
                    Integer order = new Integer(item.getItemProperty("order").getValue().toString());
                    tabs.put(order, name);
                }
            }
            String metaDataString = gson.toJson(tabs, collectionType);
            bpm.addMetaData("PROCESSBASE_TABSHEETS_LIST", metaDataString);
            // create rule
            for (String name : tabs.values()) {
                try {
                    Rule rule = bpm.findRule(name);
                    if (rule == null) {
                        rule = bpm.createRule(name, name, name, RuleType.CUSTOM);
                    }
                    Set<RuleExceptionUUID> uis = new HashSet<RuleExceptionUUID>(1);
                    uis.add(new RuleExceptionUUID(name));
                    bpm.addExceptionsToRuleByUUID(rule.getUUID(), uis);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void windowClose(CloseEvent e) {
        ((TablePanel) getComponent(1)).refreshTable();
    }

    @Override
    public String getTitle(Locale locale) {
        ResourceBundle rb = ResourceBundle.getBundle("MessagesBundle", locale);
        return rb.getString("bpmDevelopment");
    }
}
