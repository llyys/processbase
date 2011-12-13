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
package org.processbase.ui.bam.panel;

import com.vaadin.ui.Window.CloseEvent;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import java.util.Locale;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;
import org.processbase.ui.bam.dashboard.DashboardPerformerTaskPanel;
import org.processbase.ui.bam.dashboard.DashboardProcessesPanel;
import org.processbase.ui.bam.dashboard.DashboardUserTaskPanel;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.DashboardPanel;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.osgi.PbPanelModule;

/** 
 *
 * @author mgubaidullin
 */
public class BPMMonitoringPanel extends PbPanelModule
        implements Button.ClickListener, Window.CloseListener {

    private ButtonBar buttonBar = new ButtonBar();
    private DashboardPerformerTaskPanel dashboardPerformerTaskPanel;
    private DashboardProcessesPanel dashboardProcessesPanel;
    private DashboardUserTaskPanel dashboardUserTaskPanel;
    private Button refreshBtn = null;
    private Button dashboardPerformersBtn = null;
    private Button dashboardUsersBtn = null;
    private Button dashboardProcessBtn = null;
    private HashMap<Button, DashboardPanel> panels = new HashMap<Button, DashboardPanel>();

   public void initUI() {
        setMargin(false);

        prepareButtonBar();
        addComponent(buttonBar, 0);

        dashboardProcessesPanel = new DashboardProcessesPanel();
        panels.put(dashboardProcessBtn, dashboardProcessesPanel);
        addComponent(dashboardProcessesPanel, 1);
        setExpandRatio(dashboardProcessesPanel, 1);
        dashboardProcessesPanel.initUI();
        dashboardProcessesPanel.refresh();

        dashboardPerformerTaskPanel = new DashboardPerformerTaskPanel();
        panels.put(dashboardPerformersBtn, dashboardPerformerTaskPanel);

        dashboardUserTaskPanel = new DashboardUserTaskPanel();
        panels.put(dashboardUsersBtn, dashboardUserTaskPanel);

    }

    private void setCurrentPanel(DashboardPanel dashboardPanel) {
        replaceComponent(getComponent(1), dashboardPanel);
        setExpandRatio(dashboardPanel, 1);
        if (!dashboardPanel.isInitialized()){
            dashboardPanel.initUI();
        }
        dashboardPanel.refresh();
    }

    private void prepareButtonBar() {
        // prepare dashboardProcessBtn button
        dashboardProcessBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("startedProcesses"), this);
        dashboardProcessBtn.setStyleName("special");
        dashboardProcessBtn.setEnabled(false);
        buttonBar.addComponent(dashboardProcessBtn, 0);
        buttonBar.setComponentAlignment(dashboardProcessBtn, Alignment.MIDDLE_LEFT);

        // prepare dashboardPerformersBtn button
        dashboardPerformersBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("taskByPerformers"), this);
        dashboardPerformersBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(dashboardPerformersBtn, 1);
        buttonBar.setComponentAlignment(dashboardPerformersBtn, Alignment.MIDDLE_LEFT);

        // prepare dashboardUsersBtn button
        dashboardUsersBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("taskByUser"), this);
        dashboardUsersBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(dashboardUsersBtn, 2);
        buttonBar.setComponentAlignment(dashboardUsersBtn, Alignment.MIDDLE_LEFT);

        // add expand label
        Label expandLabel = new Label("");
        buttonBar.addComponent(expandLabel, 3);
        buttonBar.setExpandRatio(expandLabel, 1);


        // prepare refresh button
        refreshBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnRefresh"), this);
        buttonBar.addComponent(refreshBtn, 4);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);

        buttonBar.setWidth("100%");
    }

    public void buttonClick(ClickEvent event) {
        DashboardPanel panel = panels.get(event.getButton());
        if (event.getButton().equals(refreshBtn)) {
            ((DashboardPanel) getComponent(1)).refresh();
        } else {
            activateButtons();
            event.getButton().setStyleName("special");
            event.getButton().setEnabled(false);
            setCurrentPanel(panel);
        }

    }

    private void activateButtons() {
        dashboardPerformersBtn.setStyleName(Reindeer.BUTTON_LINK);
        dashboardPerformersBtn.setEnabled(true);
        dashboardProcessBtn.setStyleName(Reindeer.BUTTON_LINK);
        dashboardProcessBtn.setEnabled(true);
        dashboardUsersBtn.setStyleName(Reindeer.BUTTON_LINK);
        dashboardUsersBtn.setEnabled(true);
    }

    public void windowClose(CloseEvent e) {
        ((DashboardPanel) getComponent(1)).refresh();
    }

	@Override
	public String getTitle(Locale locale) {
		// TODO Auto-generated method stub
		return "Monitoring";
	}
}
