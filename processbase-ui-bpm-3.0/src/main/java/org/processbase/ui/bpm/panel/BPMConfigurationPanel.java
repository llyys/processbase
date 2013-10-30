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

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.bpm.admin.AdminCaseList;
import org.processbase.ui.bpm.admin.AdminTaskList;
import org.processbase.ui.bpm.admin.CategoriesPanel;
import org.processbase.ui.bpm.admin.DisabledProcessDefinitionsPanel;
import org.processbase.ui.bpm.admin.NewCategoryWindow;
import org.processbase.ui.bpm.admin.NewProcessDefinitionWindow;
import org.processbase.ui.bpm.admin.ProcessDefinitionsPanel;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.IPbTable;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.core.template.TablePanel;
import org.processbase.ui.core.template.WorkPanel;
import org.processbase.ui.osgi.PbPanelModule;

import com.vaadin.data.Item;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;

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
    private AdminCaseList processInstancesPanel;
    private AdminTaskList activityInstancesPanel;
    private Button refreshBtn = null;
    private Button btnAdd = null;
    private Button disabledProcessDefinitionBtn = null;
    private Button processDefinitionBtn = null;
    private Button processInstancesBtn = null;
    private Button activityInstancesBtn = null;
    private Button categoriesBtn = null;
    private HashMap<Button, WorkPanel> panels = new HashMap<Button, WorkPanel>();
	
    private CheckBox showFinished = null;
    private TextField additionalFilter = null;
   
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
        
        
		if (ProcessbaseApplication.STANDALONE == ProcessbaseApplication
				.getCurrent().getApplicationType()) {
			processInstancesPanel = new AdminCaseList();
			panels.put(processInstancesBtn, processInstancesPanel);

			activityInstancesPanel = new AdminTaskList();
			panels.put(activityInstancesBtn, activityInstancesPanel);
		}

        categoriesPanel = new CategoriesPanel();
        categoriesPanel.setBpmConfigurationPanel(this);
        panels.put(categoriesBtn, categoriesPanel);

        
        if(activityInstancesPanel != null){
        	activityInstancesPanel.setShowFinished(showFinished);
        	activityInstancesPanel.setAdditionalFilter(additionalFilter);
        }
        if(processInstancesPanel != null){
        	processInstancesPanel.setShowFinished(showFinished);
        	processInstancesPanel.setAdditionalFilter(additionalFilter);
        }
    }

    private void setCurrentPanel(WorkPanel panel) {
        replaceComponent(getComponent(1), panel);
        setExpandRatio(panel, 1);
        
        if (!panel.isInitialized()){
            panel.initUI();
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

       
        
        //Process repository button
        disabledProcessDefinitionBtn=new Button(ProcessbaseApplication.getString("disabledProcesses", "Teenuste hoidla"), this);
        disabledProcessDefinitionBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(disabledProcessDefinitionBtn, btnCnt++);
        buttonBar.setComponentAlignment(disabledProcessDefinitionBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskListBtn button
        if (ProcessbaseApplication.STANDALONE == ProcessbaseApplication
				.getCurrent().getApplicationType()) {
	        processInstancesBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("processInstancesBtn"), this);
	        processInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
	        buttonBar.addComponent(processInstancesBtn, btnCnt++);
	        buttonBar.setComponentAlignment(processInstancesBtn, Alignment.MIDDLE_LEFT);
        }
        
        // prepare myTaskArchiveBtn button
        if (ProcessbaseApplication.STANDALONE == ProcessbaseApplication
				.getCurrent().getApplicationType()) {
	        activityInstancesBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("activityInstancesBtn"), this);
	        activityInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
	        buttonBar.addComponent(activityInstancesBtn, btnCnt++);
	        buttonBar.setComponentAlignment(activityInstancesBtn, Alignment.MIDDLE_LEFT);
        }

        
        
        // add expand label
        Label expandLabel = new Label("");
        buttonBar.addComponent(expandLabel, btnCnt++);
        buttonBar.setExpandRatio(expandLabel, 1);

        //Additional filters
        additionalFilter = new TextField();
        additionalFilter.setVisible(false);
        buttonBar.addComponent(additionalFilter, btnCnt++);
        buttonBar.setComponentAlignment(additionalFilter, Alignment.MIDDLE_RIGHT);
        
        showFinished = new CheckBox(ProcessbaseApplication.getString("chkboxShowFinished"));
        showFinished.setVisible(false);
        buttonBar.addComponent(showFinished, btnCnt++);
        buttonBar.setComponentAlignment(showFinished, Alignment.MIDDLE_RIGHT);

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
    	WorkPanel panel = panels.get(event.getButton());
        if (event.getButton().equals(refreshBtn)) {
            if (getComponent(1).equals(processInstancesPanel)) {
                processInstancesPanel.refreshTable();
            } else if (getComponent(1).equals(activityInstancesPanel)) {
                activityInstancesPanel.refreshTable();
            }else{
            	((TablePanel) getComponent(1)).refreshTable();
            }
           
        } else if (event.getButton().equals(btnAdd)) {
            if (getComponent(1) instanceof CategoriesPanel) {
                NewCategoryWindow ncw = new NewCategoryWindow();
                ncw.addListener((Window.CloseListener) this);
                getApplication().getMainWindow().addWindow(ncw);
                ncw.initUI();
                ncw.addListener(new Window.CloseListener() {
					
					public void windowClose(CloseEvent e) {
						categoriesPanel.refreshTable();
					}
				});
            } else if (getComponent(1) instanceof ProcessDefinitionsPanel) {
                NewProcessDefinitionWindow npdw = new NewProcessDefinitionWindow();
                npdw.addListener((Window.CloseListener) this);
                getApplication().getMainWindow().addWindow(npdw);
                npdw.initUI();
                npdw.addListener(new Window.CloseListener() {
					
					public void windowClose(CloseEvent e) {
						processDefinitionsPanel.refreshTable();
					}
				});
            }
          
        } else {
            activateButtons();
            event.getButton().setStyleName("special");
            event.getButton().setEnabled(false);
            setCurrentPanel(panel);
            if(panel instanceof IPbTable){
            	((IPbTable)panel).refreshTable();
            }
            if (ProcessbaseApplication.STANDALONE == ProcessbaseApplication
    				.getCurrent().getApplicationType()) {
	            if (event.getButton().equals(processInstancesBtn) || event.getButton().equals(activityInstancesBtn)) {
	                btnAdd.setVisible(false);
	                additionalFilter.setVisible(true);
	            } else {
	                additionalFilter.setVisible(false);
	            }
            }
        }

    }

    private void activateButtons() {
        processDefinitionBtn.setStyleName(Reindeer.BUTTON_LINK);
        processDefinitionBtn.setEnabled(true);
        
        disabledProcessDefinitionBtn.setStyleName(Reindeer.BUTTON_LINK);
        disabledProcessDefinitionBtn.setEnabled(true);
        
        if (ProcessbaseApplication.STANDALONE == ProcessbaseApplication
				.getCurrent().getApplicationType()) {
	        processInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
	        processInstancesBtn.setEnabled(true);
	        
	        activityInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
	        activityInstancesBtn.setEnabled(true);
	        
	        showFinished.setVisible(true);
        }
        
        categoriesBtn.setStyleName(Reindeer.BUTTON_LINK);
        categoriesBtn.setEnabled(true);
        
        btnAdd.setVisible(true);
        
        additionalFilter.setVisible(false);
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
