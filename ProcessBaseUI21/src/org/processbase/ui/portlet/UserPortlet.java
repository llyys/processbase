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
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.worklist.ProcessesPanel;
import org.processbase.ui.worklist.TaskArchivePanel;
import org.processbase.ui.worklist.TaskListPanel;

/**
 *
 * @author mgubaidullin
 */
public class UserPortlet extends InternalApplication implements Button.ClickListener {

    private BPMModule bpmModule = null;
    private ResourceBundle messages = null;
    private PbWindow userWindow;
    private VerticalLayout mainLayout = new VerticalLayout();
    private ButtonBar buttonBar = new ButtonBar();
    private TaskListPanel taskListPanel;
    private TaskArchivePanel taskArchivePanel;
    private ProcessesPanel processesPanel;
    private Button refreshBtn = null;
    private Button myTaskListBtn = null;
    private Button myTaskArchiveBtn = null;
    private Button myProcessesBtn = null;
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

        taskListPanel = new TaskListPanel(this.portletApplicationContext2, bpmModule, messages);
        panels.put(myTaskListBtn, taskListPanel);
        mainLayout.addComponent(taskListPanel, 1);
        taskListPanel.refreshTable();
        myTaskListBtn.setCaption(this.messages.getString("myTaskListBtn") + " (" + taskListPanel.rowCount + ")");


        taskArchivePanel = new TaskArchivePanel(this.portletApplicationContext2, bpmModule, messages);
        panels.put(myTaskArchiveBtn, taskArchivePanel);

        processesPanel = new ProcessesPanel(this.portletApplicationContext2, bpmModule, messages);
        panels.put(myProcessesBtn, processesPanel);

    }

    private void setCurrentPanel(TablePanel tablePanel) {
        mainLayout.replaceComponent(mainLayout.getComponent(1), tablePanel);
        tablePanel.refreshTable();
    }

    private void prepareButtonBar() {
        // prepare myProcessesBtn button
        myProcessesBtn = new Button(this.messages.getString("myProcessesBtn"), this);
        myProcessesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(myProcessesBtn, 0);
        buttonBar.setComponentAlignment(myProcessesBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskListBtn button
        myTaskListBtn = new Button(this.messages.getString("myTaskListBtn"), this);
        myTaskListBtn.setStyleName("special");
        myTaskListBtn.setEnabled(false);
        buttonBar.addComponent(myTaskListBtn, 1);
        buttonBar.setComponentAlignment(myTaskListBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskArchiveBtn button
        myTaskArchiveBtn = new Button(this.messages.getString("myTaskArchiveBtn"), this);
        myTaskArchiveBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(myTaskArchiveBtn, 2);
        buttonBar.setComponentAlignment(myTaskArchiveBtn, Alignment.MIDDLE_LEFT);

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
            ((TablePanel) mainLayout.getComponent(1)).refreshTable();
        } else {
            activateButtons();
            event.getButton().setStyleName("special");
            event.getButton().setEnabled(false);
            setCurrentPanel(panel);
        }
        if (!myTaskListBtn.isEnabled()) {
            myTaskListBtn.setCaption(this.messages.getString("myTaskListBtn") + " (" + taskListPanel.rowCount + ")");
        } else if (!myProcessesBtn.isEnabled()) {
            myProcessesBtn.setCaption(this.messages.getString("myProcessesBtn") + " (" + processesPanel.rowCount + ")");
        } else if (!myTaskArchiveBtn.isEnabled()) {
            myTaskArchiveBtn.setCaption(this.messages.getString("myTaskArchiveBtn") + " (" + taskArchivePanel.rowCount + ")");
        }
    }

    private void activateButtons() {
        myProcessesBtn.setStyleName(Reindeer.BUTTON_LINK);
        myProcessesBtn.setEnabled(true);
        myProcessesBtn.setCaption(this.messages.getString("myProcessesBtn"));

        myTaskListBtn.setStyleName(Reindeer.BUTTON_LINK);
        myTaskListBtn.setEnabled(true);
        myTaskListBtn.setCaption(this.messages.getString("myTaskListBtn"));

        myTaskArchiveBtn.setStyleName(Reindeer.BUTTON_LINK);
        myTaskArchiveBtn.setEnabled(true);
        myTaskArchiveBtn.setCaption(this.messages.getString("myTaskArchiveBtn"));
    }
}
