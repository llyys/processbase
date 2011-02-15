/**
 * Copyright (C) 2010 PROCESSBASE
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
package org.processbase.ui.portlet;

import com.liferay.portal.model.User;
import com.vaadin.data.Item;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import java.util.Locale;
import javax.portlet.PortletSession;
import org.processbase.ui.admin.ActivityInstancesPanel;
import org.processbase.ui.admin.ProcessDefinitionsPanel;
import org.processbase.ui.admin.ProcessInstancesPanel;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import java.util.Collection;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.admin.CategoriesPanel;
import org.processbase.ui.admin.NewCategoryWindow;
import org.processbase.ui.admin.NewProcessDefinitionWindow;

/**
 *
 * @author mgubaidullin
 */
public class AdminPortlet extends PbPortlet
        implements Button.ClickListener, Window.CloseListener {

    private PbWindow adminWindow;
    private VerticalLayout mainLayout = new VerticalLayout();
    private ButtonBar buttonBar = new ButtonBar();
    private ProcessDefinitionsPanel processDefinitionsPanel;
    private CategoriesPanel categoriesPanel;
    private ProcessInstancesPanel processInstancesPanel;
    private ActivityInstancesPanel activityInstancesPanel;
    private Button refreshBtn = null;
    private Button btnAdd = null;
    private Button processDefinitionBtn = null;
    private Button processInstancesBtn = null;
    private Button activityInstancesBtn = null;
    private Button categoriesBtn = null;
    private HashMap<Button, TablePanel> panels = new HashMap<Button, TablePanel>();
    private ComboBox processesComboBox = null;

    @Override
    public void init() {
        super.init();
        this.setTheme("processbase");
        prepareMainWindow();
        refreshProcessDefinitionCombo();
    }

    private void prepareMainWindow() {

        mainLayout.setMargin(false);

        adminWindow = new PbWindow("Processbase Admin Portlet");
        adminWindow.setContent(mainLayout);
        adminWindow.setSizeFull();

        this.setMainWindow(adminWindow);

        prepareButtonBar();
        mainLayout.addComponent(buttonBar, 0);

        processDefinitionsPanel = new ProcessDefinitionsPanel();
        panels.put(processDefinitionBtn, processDefinitionsPanel);
        mainLayout.addComponent(processDefinitionsPanel, 1);
        processDefinitionsPanel.refreshTable();

        processInstancesPanel = new ProcessInstancesPanel();
        panels.put(processInstancesBtn, processInstancesPanel);

        activityInstancesPanel = new ActivityInstancesPanel();
        panels.put(activityInstancesBtn, activityInstancesPanel);

        categoriesPanel = new CategoriesPanel();
        panels.put(categoriesBtn, categoriesPanel);

    }

    private void setCurrentPanel(TablePanel tablePanel) {
        mainLayout.replaceComponent(mainLayout.getComponent(1), tablePanel);
        if (tablePanel.equals(processDefinitionsPanel) || tablePanel.equals(categoriesPanel)) {
            tablePanel.refreshTable();
        }
    }

    private void prepareButtonBar() {
        // prepare categoriesBtn button
        categoriesBtn = new Button(this.messages.getString("categoriesBtn"), this);
        categoriesBtn.setDescription(this.messages.getString("categoriesBtnTooltip"));
        categoriesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(categoriesBtn, 0);
        buttonBar.setComponentAlignment(categoriesBtn, Alignment.MIDDLE_LEFT);

        // prepare myProcessesBtn button
        processDefinitionBtn = new Button(this.messages.getString("processDefinitionBtn"), this);
        processDefinitionBtn.setStyleName("special");
        processDefinitionBtn.setEnabled(false);
        buttonBar.addComponent(processDefinitionBtn, 1);
        buttonBar.setComponentAlignment(processDefinitionBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskListBtn button
        processInstancesBtn = new Button(this.messages.getString("processInstancesBtn"), this);
        processInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(processInstancesBtn, 2);
        buttonBar.setComponentAlignment(processInstancesBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskArchiveBtn button
        activityInstancesBtn = new Button(this.messages.getString("activityInstancesBtn"), this);
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
        processesComboBox.setInputPrompt(PbPortlet.getCurrent().messages.getString("selectProcessDefinition"));
        processesComboBox.setDescription(PbPortlet.getCurrent().messages.getString("selectProcessDefinition"));
        buttonBar.addComponent(processesComboBox, 5);
        buttonBar.setComponentAlignment(processesComboBox, Alignment.MIDDLE_LEFT);
        processesComboBox.setVisible(false);

        // prepare refresh button
        refreshBtn = new Button(this.messages.getString("btnRefresh"), this);
        buttonBar.addComponent(refreshBtn, 6);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);

        // prepare add button
        btnAdd = new Button(this.messages.getString("btnAdd"), this);
        buttonBar.addComponent(btnAdd, 7);
        buttonBar.setComponentAlignment(btnAdd, Alignment.MIDDLE_RIGHT);



        buttonBar.setStyleName("white");
        buttonBar.setWidth("100%");
//        buttonBar.setHeight("48px");
        buttonBar.setMargin(false, true, false, true);
        buttonBar.setSpacing(true);
    }

    public User getCurrentUser() {
        return ((User) portletApplicationContext2.getPortletSession().getAttribute("PROCESSBASE_USER", PortletSession.APPLICATION_SCOPE));
    }

    public Locale getCurrentLocale() {
        return (Locale) portletApplicationContext2.getPortletSession().getAttribute("org.apache.struts.action.LOCALE", PortletSession.APPLICATION_SCOPE);
    }

    public void buttonClick(ClickEvent event) {
        TablePanel panel = panels.get(event.getButton());
        if (event.getButton().equals(refreshBtn)) {
            if (mainLayout.getComponent(1).equals(processInstancesPanel)) {
                processInstancesPanel.setFilter(processesComboBox.getValue() != null ? ((LightProcessDefinition) processesComboBox.getValue()).getUUID() : null);
            } else if (mainLayout.getComponent(1).equals(activityInstancesPanel)) {
                activityInstancesPanel.setFilter(processesComboBox.getValue() != null ? ((LightProcessDefinition) processesComboBox.getValue()).getUUID() : null);
            }
            ((TablePanel) mainLayout.getComponent(1)).refreshTable();
        } else if (event.getButton().equals(btnAdd)) {
            if (mainLayout.getComponent(1) instanceof CategoriesPanel) {
                NewCategoryWindow ncw = new NewCategoryWindow();
                ncw.exec();
                ncw.addListener((Window.CloseListener) this);
                getMainWindow().addWindow(ncw);
            } else if (mainLayout.getComponent(1) instanceof ProcessDefinitionsPanel) {
                NewProcessDefinitionWindow npdw = new NewProcessDefinitionWindow();
                npdw.exec();
                npdw.addListener((Window.CloseListener) this);
                getMainWindow().addWindow(npdw);
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
            Collection<LightProcessDefinition> processes = PbPortlet.getCurrent().bpmModule.getLightProcessDefinitions(ProcessState.ENABLED);
            for (LightProcessDefinition pd : processes) {
                Item woItem = processesComboBox.addItem(pd);
                String caption = pd.getLabel() != null ? pd.getLabel() : pd.getName();
                processesComboBox.setItemCaption(pd, caption + " (version " + pd.getVersion() + ")");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            adminWindow.showError(ex.getMessage());
        }
    }

    public void windowClose(CloseEvent e) {
        ((TablePanel) mainLayout.getComponent(1)).refreshTable();
    }
}
