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
import org.processbase.ui.bpm.admin.AdminCaseList;
import org.processbase.ui.bpm.admin.DisabledProcessDefinitionsPanel;
import org.processbase.ui.bpm.admin.ProcessDefinitionsPanel;
import org.processbase.ui.bpm.admin.AdminTaskList;
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
import org.processbase.ui.bpm.admin.NewProcessDefinitionWindow;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.ui.osgi.PbPanelModule;

/**
 *
 * @author mgubaidullin
 */
public class BPMConfigurationPanel extends PbPanelModule
        implements Button.ClickListener, Window.CloseListener {

    private ButtonBar buttonBar = new ButtonBar();
    private ProcessDefinitionsPanel processDefinitionsPanel;
    private DisabledProcessDefinitionsPanel disabledProcessDefinitionsPanel;
    private CategoriesPanel categoriesPanel;
    private AdminTaskList processInstancesPanel;
    private AdminCaseList activityInstancesPanel;
    private Button refreshBtn = null;
    private Button btnAdd = null;
    private Button disabledProcessDefinitionBtn = null;
    private Button processDefinitionBtn = null;
    private Button processInstancesBtn = null;
    private Button activityInstancesBtn = null;
    private Button categoriesBtn = null;
    private HashMap<Button, TablePanel> panels = new HashMap<Button, TablePanel>();
    private ComboBox processesComboBox = null;
	

   
    public void initUI() {
        panels.clear();
        removeAllComponents();
        setMargin(false);

        prepareButtonBar();
        addComponent(buttonBar, 0);

        processDefinitionsPanel = new ProcessDefinitionsPanel();
        processDefinitionsPanel.setBpmConfigurationPanel(this);
        panels.put(processDefinitionBtn, processDefinitionsPanel);
        addComponent(processDefinitionsPanel, 1);
        setExpandRatio(processDefinitionsPanel, 1);
        processDefinitionsPanel.initUI();
        processDefinitionsPanel.refreshTable();

        disabledProcessDefinitionsPanel=new DisabledProcessDefinitionsPanel();
        disabledProcessDefinitionsPanel.setBpmConfigurationPanel(this);
        panels.put(disabledProcessDefinitionBtn, disabledProcessDefinitionsPanel);
        
        processInstancesPanel = new AdminTaskList();
        processInstancesPanel.setBpmConfigurationPanel(this);        
        panels.put(processInstancesBtn, processInstancesPanel);

        activityInstancesPanel = new AdminCaseList();
        activityInstancesPanel.setBpmConfigurationPanel(this);
        panels.put(activityInstancesBtn, activityInstancesPanel);

        categoriesPanel = new CategoriesPanel();
        categoriesPanel.setBpmConfigurationPanel(this);
        panels.put(categoriesBtn, categoriesPanel);

        refreshProcessDefinitionCombo();
    }

    private void setCurrentPanel(TablePanel tablePanel) {
        replaceComponent(getComponent(1), tablePanel);
        setExpandRatio(tablePanel, 1);
        if (!tablePanel.isInitialized()){
            tablePanel.initUI();
        }
        
        /*
        if (tablePanel.equals(processDefinitionsPanel) || tablePanel.equals(categoriesPanel)) {
            tablePanel.refreshTable();
        }*/
    }

    private void prepareButtonBar() {
        buttonBar.removeAllComponents();
        int btnCnt=0;
        // prepare categoriesBtn button
        categoriesBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("categoriesBtn"), this);
        categoriesBtn.setDescription(ProcessbaseApplication.getCurrent().getPbMessages().getString("categoriesBtnTooltip"));
        categoriesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(categoriesBtn, btnCnt++);
        buttonBar.setComponentAlignment(categoriesBtn, Alignment.MIDDLE_LEFT);

        // prepare myProcessesBtn button
        processDefinitionBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("processDefinitionBtn"), this);
        processDefinitionBtn.setStyleName("special");
        processDefinitionBtn.setEnabled(false);
        buttonBar.addComponent(processDefinitionBtn, btnCnt++);
        buttonBar.setComponentAlignment(processDefinitionBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskListBtn button
        processInstancesBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("processInstancesBtn"), this);
        processInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(processInstancesBtn, btnCnt++);
        buttonBar.setComponentAlignment(processInstancesBtn, Alignment.MIDDLE_LEFT);
        
        //Process repository button
        disabledProcessDefinitionBtn=new Button(ProcessbaseApplication.getString("disabledProcesses", "Protsesside hoidla"), this);
        disabledProcessDefinitionBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(disabledProcessDefinitionBtn, btnCnt++);
        buttonBar.setComponentAlignment(disabledProcessDefinitionBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskArchiveBtn button
        activityInstancesBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("activityInstancesBtn"), this);
        activityInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(activityInstancesBtn, btnCnt++);
        buttonBar.setComponentAlignment(activityInstancesBtn, Alignment.MIDDLE_LEFT);

        
        
        // add expand label
        Label expandLabel = new Label("");
        buttonBar.addComponent(expandLabel, btnCnt++);
        buttonBar.setExpandRatio(expandLabel, 1);

        // prepare processesComboBox
        processesComboBox = new ComboBox();
        processesComboBox.setWidth("250px");
        processesComboBox.setInputPrompt(ProcessbaseApplication.getCurrent().getPbMessages().getString("selectProcessDefinition"));
        processesComboBox.setDescription(ProcessbaseApplication.getCurrent().getPbMessages().getString("selectProcessDefinition"));
        buttonBar.addComponent(processesComboBox, btnCnt++);
        buttonBar.setComponentAlignment(processesComboBox, Alignment.MIDDLE_LEFT);
        processesComboBox.setVisible(false);

        // prepare refresh button
        refreshBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnRefresh"), this);
        buttonBar.addComponent(refreshBtn, btnCnt++);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);

        // prepare add button
        btnAdd = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnAdd"), this);
        buttonBar.addComponent(btnAdd, btnCnt++);
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
            } else if (getComponent(1) instanceof ProcessDefinitionsPanel) {
                NewProcessDefinitionWindow npdw = new NewProcessDefinitionWindow();
                npdw.addListener((Window.CloseListener) this);
                getApplication().getMainWindow().addWindow(npdw);
                npdw.initUI();
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
        processDefinitionBtn.setStyleName(Reindeer.BUTTON_LINK);
        processDefinitionBtn.setEnabled(true);
        
        disabledProcessDefinitionBtn.setStyleName(Reindeer.BUTTON_LINK);
        disabledProcessDefinitionBtn.setEnabled(true);
        
        processInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        processInstancesBtn.setEnabled(true);
        
        activityInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        activityInstancesBtn.setEnabled(true);
        
        categoriesBtn.setStyleName(Reindeer.BUTTON_LINK);
        categoriesBtn.setEnabled(true);
        
        btnAdd.setVisible(true);
        
        processesComboBox.setVisible(true);
    }

    public void refreshProcessDefinitionCombo() {
        try {
            processesComboBox.removeAllItems();
            Collection<LightProcessDefinition> processes = ProcessbaseApplication.getCurrent().getBpmModule().getLightProcessDefinitions(ProcessState.ENABLED);
            for (LightProcessDefinition pd : processes) {
                Item woItem = processesComboBox.addItem(pd);
                String caption = pd.getLabel() != null ? pd.getLabel() : pd.getName();
                processesComboBox.setItemCaption(pd, caption + " (version " + pd.getVersion() + ")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ((PbWindow)getApplication().getMainWindow()).showError(ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public void windowClose(CloseEvent e) {
        ((TablePanel) getComponent(1)).refreshTable();
    }

    @Override
    public String getTitle(Locale locale) {
        ResourceBundle rb = ResourceBundle.getBundle("MessagesBundle", locale);
        return rb.getString("bpmAdmin");
    }
}
