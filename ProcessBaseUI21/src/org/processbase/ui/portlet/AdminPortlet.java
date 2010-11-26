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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.admin.ActivityInstancesPanel;
import org.processbase.ui.admin.ProcessDefinitionsPanel;
import org.processbase.ui.admin.ProcessInstancesPanel;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.worklist.ProcessesPanel;

/**
 *
 * @author mgubaidullin
 */
public class AdminPortlet extends InternalApplication implements Button.ClickListener {

    private BPMModule bpmModule = null;
    private ResourceBundle messages = null;
    private PbWindow userWindow;
    private VerticalLayout mainLayout = new VerticalLayout();
    private ButtonBar buttonBar = new ButtonBar();
    private ProcessDefinitionsPanel processDefinitionsPanel;
    private ProcessInstancesPanel processInstancesPanel;
    private ActivityInstancesPanel activityInstancesPanel;
    private Button refreshBtn = null;
    private Button processDefinitionBtn = null;
    private Button processInstancesBtn = null;
    private Button activityInstancesBtn = null;
    private HashMap<Button, TablePanel> panels = new HashMap<Button, TablePanel>();

    @Override
    public void init() {
        super.init();
        this.setTheme("processbase");

        mainLayout.setMargin(false);

        userWindow = new PbWindow(getPortletApplicationContext2());
        userWindow.setContent(mainLayout);
        userWindow.setSizeFull();

        this.setMainWindow(userWindow);
    }

    public void createApplication(RenderRequest request, RenderResponse response) {
        try {
            this.messages = ResourceBundle.getBundle("resources/MessagesBundle", getCurrentLocale());
            this.bpmModule = new BPMModule(this.getCurrentUser().getScreenName());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        prepareButtonBar();
        mainLayout.addComponent(buttonBar, 0);

        processDefinitionsPanel = new ProcessDefinitionsPanel(this.portletApplicationContext2, bpmModule, messages);
        panels.put(processDefinitionBtn, processDefinitionsPanel);
        mainLayout.addComponent(processDefinitionsPanel, 1);
        processDefinitionsPanel.refreshTable();

        processInstancesPanel = new ProcessInstancesPanel(this.portletApplicationContext2, bpmModule, messages);
        panels.put(processInstancesBtn, processInstancesPanel);

        activityInstancesPanel = new ActivityInstancesPanel(this.portletApplicationContext2, bpmModule, messages);
        panels.put(activityInstancesBtn, activityInstancesPanel);

    }

    private void setCurrentPanel(TablePanel tablePanel) {
        mainLayout.replaceComponent(mainLayout.getComponent(1), tablePanel);
        tablePanel.refreshTable();
    }

    private void prepareButtonBar() {
        // prepare myProcessesBtn button
        processDefinitionBtn = new Button(this.messages.getString("processDefinitionBtn"), this);
        processDefinitionBtn.setStyleName("special");
        processDefinitionBtn.setEnabled(false);
        
        buttonBar.addComponent(processDefinitionBtn, 0);
        buttonBar.setComponentAlignment(processDefinitionBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskListBtn button
        processInstancesBtn = new Button(this.messages.getString("processInstancesBtn"), this);
        processInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(processInstancesBtn, 1);
        buttonBar.setComponentAlignment(processInstancesBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskArchiveBtn button
        activityInstancesBtn = new Button(this.messages.getString("activityInstancesBtn"), this);
        activityInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(activityInstancesBtn, 2);
        buttonBar.setComponentAlignment(activityInstancesBtn, Alignment.MIDDLE_LEFT);

        // prepare help button
        refreshBtn = new Button(this.messages.getString("btnRefresh"), this);
        buttonBar.addComponent(refreshBtn, 3);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);
        buttonBar.setExpandRatio(refreshBtn, 1);


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
            ((TablePanel)mainLayout.getComponent(1)).refreshTable();
        } else {
            activateButtons();
            event.getButton().setStyleName("special");
            event.getButton().setEnabled(false);
            setCurrentPanel(panel);
        }
    }

    private void activateButtons(){
        processDefinitionBtn.setStyleName(Reindeer.BUTTON_LINK);
        processDefinitionBtn.setEnabled(true);
        processInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        processInstancesBtn.setEnabled(true);
        activityInstancesBtn.setStyleName(Reindeer.BUTTON_LINK);
        activityInstancesBtn.setEnabled(true);
    }
}
