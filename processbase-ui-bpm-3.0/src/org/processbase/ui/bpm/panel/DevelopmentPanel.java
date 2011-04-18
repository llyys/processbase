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
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import java.util.Collection;
import java.util.ResourceBundle;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.bpm.admin.CategoriesPanel;
import org.processbase.ui.bpm.admin.NewCategoryWindow;
import org.processbase.ui.bpm.development.ModulesJarPanel;
import org.processbase.ui.bpm.development.ModulesTabPanel;
import org.processbase.ui.bpm.development.NewJarWindow;
import org.processbase.ui.core.PbResourceBundle;
import org.processbase.ui.core.Processbase;
import org.processbase.ui.core.template.ButtonBar;
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
    private ComboBox processesComboBox = null;

   
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

        refreshProcessDefinitionCombo();
    }

    private void setCurrentPanel(TablePanel tablePanel) {
        replaceComponent(getComponent(1), tablePanel);
        setExpandRatio(tablePanel, 1);
        if (!tablePanel.isInitialized()){
            tablePanel.initUI();
        }
        if (tablePanel.equals(modulesJarPanel) || tablePanel.equals(modulesTabPanel)) {
            tablePanel.refreshTable();
        }
    }

    private void prepareButtonBar() {
        buttonBar.removeAllComponents();
        // prepare JarFilesBtn button
        modulesJarBtn = new Button(((Processbase)getApplication()).getMessages().getString("modulesJarBtn"), this);
        modulesJarBtn.setStyleName("special");
        modulesJarBtn.setEnabled(false);
        buttonBar.addComponent(modulesJarBtn, 0);
        buttonBar.setComponentAlignment(modulesJarBtn, Alignment.MIDDLE_LEFT);

        // prepare modulesTabBtn button
        modulesTabBtn = new Button(((Processbase)getApplication()).getMessages().getString("modulesTabBtn"), this);
        modulesTabBtn.setDescription(((Processbase)getApplication()).getMessages().getString("modulesTabBtn"));
        modulesTabBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(modulesTabBtn, 1);
        buttonBar.setComponentAlignment(modulesTabBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskListBtn button
        processInstancesBtn = new Button(((Processbase)getApplication()).getMessages().getString("processInstancesBtn"), this);
        processInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(processInstancesBtn, 2);
        buttonBar.setComponentAlignment(processInstancesBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskArchiveBtn button
        activityInstancesBtn = new Button(((Processbase)getApplication()).getMessages().getString("activityInstancesBtn"), this);
        activityInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(activityInstancesBtn, 3);
        buttonBar.setComponentAlignment(activityInstancesBtn, Alignment.MIDDLE_LEFT);

        // add expand label
        Label expandLabel = new Label("");
        buttonBar.addComponent(expandLabel, 4);
        buttonBar.setExpandRatio(expandLabel, 1);

        // prepare processesComboBox
        processesComboBox = new ComboBox();
        processesComboBox.setWidth("250px");
        processesComboBox.setInputPrompt(((Processbase)getApplication()).getMessages().getString("selectProcessDefinition"));
        processesComboBox.setDescription(((Processbase)getApplication()).getMessages().getString("selectProcessDefinition"));
        buttonBar.addComponent(processesComboBox, 5);
        buttonBar.setComponentAlignment(processesComboBox, Alignment.MIDDLE_LEFT);
        processesComboBox.setVisible(false);

        // prepare refresh button
        refreshBtn = new Button(((Processbase)getApplication()).getMessages().getString("btnRefresh"), this);
        buttonBar.addComponent(refreshBtn, 6);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);

        // prepare add button
        btnAdd = new Button(((Processbase)getApplication()).getMessages().getString("btnAdd"), this);
        buttonBar.addComponent(btnAdd, 7);
        buttonBar.setComponentAlignment(btnAdd, Alignment.MIDDLE_RIGHT);
        buttonBar.setWidth("100%");
    }

    public void buttonClick(ClickEvent event) {
        TablePanel panel = panels.get(event.getButton());
        if (event.getButton().equals(refreshBtn)) {
            if (getComponent(1).equals(processInstancesPanel)) {
                processInstancesPanel.setFilter(processesComboBox.getValue() != null ? ((LightProcessDefinition) processesComboBox.getValue()).getUUID() : null);
            } else if (getComponent(1).equals(activityInstancesPanel)) {
                activityInstancesPanel.setFilter(processesComboBox.getValue() != null ? ((LightProcessDefinition) processesComboBox.getValue()).getUUID() : null);
            }
            ((TablePanel) getComponent(1)).refreshTable();
        } else if (event.getButton().equals(btnAdd)) {
            if (getComponent(1) instanceof CategoriesPanel) {
                NewCategoryWindow ncw = new NewCategoryWindow();
                ncw.addListener((Window.CloseListener) this);
                getApplication().getMainWindow().addWindow(ncw);
                ncw.initUI();
            } else if (getComponent(1) instanceof ModulesJarPanel) {
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
            if (event.getButton().equals(processInstancesBtn) || event.getButton().equals(activityInstancesBtn)) {
                btnAdd.setVisible(false);
            } else {
                processesComboBox.setVisible(false);
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
        processesComboBox.setVisible(true);
    }

    public void refreshProcessDefinitionCombo() {
        try {
            processesComboBox.removeAllItems();
            Collection<LightProcessDefinition> processes = ((Processbase)getApplication()).getBpmModule().getLightProcessDefinitions(ProcessState.ENABLED);
            for (LightProcessDefinition pd : processes) {
                Item woItem = processesComboBox.addItem(pd);
                String caption = pd.getLabel() != null ? pd.getLabel() : pd.getName();
                processesComboBox.setItemCaption(pd, caption + " (version " + pd.getVersion() + ")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ((PbWindow)getApplication().getMainWindow()).showError(ex.getMessage());
        }
    }

    public void windowClose(CloseEvent e) {
        ((TablePanel) getComponent(1)).refreshTable();
    }

    @Override
    public String getTitle(Locale locale) {
        ResourceBundle rb = PbResourceBundle.getBundle("resources/MessagesBundle", locale);
        return rb.getString("bpmDevelopment");
    }
}
