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

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.HashMap;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.template.TreeTablePanel;
import org.processbase.ui.template.WorkPanel;
import org.processbase.ui.worklist.NewProcessesPanel;
import org.processbase.ui.worklist.ProcessesPanel;
import org.processbase.ui.worklist.TaskCompletedPanel;
import org.processbase.ui.worklist.TaskListPanel;

/**
 *
 * @author mgubaidullin
 */
public class UserPortlet extends PbPortlet implements Button.ClickListener {

    
    private PbWindow userWindow;
    private VerticalLayout mainLayout = new VerticalLayout();
    private ButtonBar buttonBar = new ButtonBar();
    private TaskListPanel taskListPanel;
    private TaskCompletedPanel taskCompletedPanel;
    private ProcessesPanel processesPanel;
    private NewProcessesPanel newProcessesPanel;
    private Button refreshBtn = null;
    private Button myTaskListBtn = null;
    private Button myTaskCompletedBtn = null;
    private Button myProcessesBtn = null;
    private Button myNewProcessesBtn = null;
    private HashMap<Button, WorkPanel> panels = new HashMap<Button, WorkPanel>();

    @Override
    public void init() {
        this.setTheme("processbase");
        super.init();
        prepareMainWindow();
    }

    private void prepareMainWindow(){
        mainLayout.setMargin(false);

        userWindow = new PbWindow("Processbase User Portlet");
        userWindow.setContent(mainLayout);
        userWindow.setSizeFull();

        this.setMainWindow(userWindow);
  
        prepareButtonBar();
        mainLayout.addComponent(buttonBar, 0);

        taskListPanel = new TaskListPanel();
        panels.put(myTaskListBtn, taskListPanel);
        mainLayout.addComponent(taskListPanel, 1);
        taskListPanel.refreshTable();
        myTaskListBtn.setCaption(this.messages.getString("myTaskListBtn") + " (" + taskListPanel.rowCount + ")");


        taskCompletedPanel = new TaskCompletedPanel();
        panels.put(myTaskCompletedBtn, taskCompletedPanel);

        processesPanel = new ProcessesPanel();
        panels.put(myProcessesBtn, processesPanel);

        newProcessesPanel = new NewProcessesPanel();
        panels.put(myNewProcessesBtn, newProcessesPanel);

    }

    private void setCurrentPanel(WorkPanel workPanel) {
        mainLayout.replaceComponent(mainLayout.getComponent(1), workPanel);
        if (workPanel instanceof TablePanel){
            ((TablePanel)workPanel).refreshTable();
        } else if (workPanel instanceof TreeTablePanel){
            ((TreeTablePanel)workPanel).refreshTable();
        }
        
    }

    private void prepareButtonBar() {

        // prepare myNewProcessesBtn button
        myNewProcessesBtn = new Button(this.messages.getString("myNewProcessesBtn"), this);
        myNewProcessesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(myNewProcessesBtn, 0);
        buttonBar.setComponentAlignment(myNewProcessesBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskListBtn button
        myTaskListBtn = new Button(this.messages.getString("myTaskListBtn"), this);
        myTaskListBtn.setStyleName("special");
        myTaskListBtn.setEnabled(false);
        buttonBar.addComponent(myTaskListBtn, 1);
        buttonBar.setComponentAlignment(myTaskListBtn, Alignment.MIDDLE_LEFT);

        // prepare myProcessesBtn button
        myProcessesBtn = new Button(this.messages.getString("myProcessesBtn"), this);
        myProcessesBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(myProcessesBtn, 2);
        buttonBar.setComponentAlignment(myProcessesBtn, Alignment.MIDDLE_LEFT);

        // prepare myTaskCompletedBtn button
        myTaskCompletedBtn = new Button(this.messages.getString("myTaskCompletedBtn"), this);
        myTaskCompletedBtn.setStyleName(Reindeer.BUTTON_LINK);
        buttonBar.addComponent(myTaskCompletedBtn, 3);
        buttonBar.setComponentAlignment(myTaskCompletedBtn, Alignment.MIDDLE_LEFT);

        // prepare help button
        refreshBtn = new Button(this.messages.getString("btnRefresh"), this);
        buttonBar.addComponent(refreshBtn, 4);
        buttonBar.setComponentAlignment(refreshBtn, Alignment.MIDDLE_RIGHT);
        buttonBar.setExpandRatio(refreshBtn, 1);


        buttonBar.setStyleName("white");
        buttonBar.setWidth("100%");
//        buttonBar.setHeight("48px");
        buttonBar.setMargin(false, true, false, true);
        buttonBar.setSpacing(true);
    }

    public void buttonClick(ClickEvent event) {
        WorkPanel panel = panels.get(event.getButton());
        if (event.getButton().equals(refreshBtn) && (mainLayout.getComponent(1) instanceof TablePanel)) {
            ((TablePanel) mainLayout.getComponent(1)).refreshTable();
        } else if (event.getButton().equals(refreshBtn) && (mainLayout.getComponent(1) instanceof TreeTablePanel)) {
            ((TreeTablePanel) mainLayout.getComponent(1)).refreshTable();
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
        } else if (!myTaskCompletedBtn.isEnabled()) {
            myTaskCompletedBtn.setCaption(this.messages.getString("myTaskCompletedBtn") + " (" + taskCompletedPanel.rowCount + ")");
        } else if (!myNewProcessesBtn.isEnabled()) {
            myNewProcessesBtn.setCaption(this.messages.getString("myNewProcessesBtn"));
        }
    }

    private void activateButtons() {
        myProcessesBtn.setStyleName(Reindeer.BUTTON_LINK);
        myProcessesBtn.setEnabled(true);
        myProcessesBtn.setCaption(this.messages.getString("myProcessesBtn"));

        myTaskListBtn.setStyleName(Reindeer.BUTTON_LINK);
        myTaskListBtn.setEnabled(true);
        myTaskListBtn.setCaption(this.messages.getString("myTaskListBtn"));

        myTaskCompletedBtn.setStyleName(Reindeer.BUTTON_LINK);
        myTaskCompletedBtn.setEnabled(true);
        myTaskCompletedBtn.setCaption(this.messages.getString("myTaskCompletedBtn"));

        myNewProcessesBtn.setStyleName(Reindeer.BUTTON_LINK);
        myNewProcessesBtn.setEnabled(true);
        myNewProcessesBtn.setCaption(this.messages.getString("myNewProcessesBtn"));
    }
}
