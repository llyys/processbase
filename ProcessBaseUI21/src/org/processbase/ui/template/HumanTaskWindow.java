/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
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
package org.processbase.ui.template;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import java.util.HashMap;
import java.util.ResourceBundle;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.bpm.BPMModule;

/**
 *
 * @author marat
 */
public class HumanTaskWindow extends PbWindow implements MenuBar.Command {

    protected VerticalLayout layout = new VerticalLayout();
    protected HashMap<String, Object> procVariables = new HashMap<String, Object>();
    protected TaskInstance task;
    protected LightProcessDefinition processDef;
    protected BPMModule bpmModule = null;
    protected HorizontalLayout topBar = new HorizontalLayout();
    protected MenuBar actorMenubar = new MenuBar();
    protected MenuBar.MenuItem actor = actorMenubar.addItem("", (MenuBar.Command) this);
    protected MenuBar.MenuItem state = actorMenubar.addItem("", null);
    protected MenuBar.MenuItem suspend = state.addItem("", new ThemeResource("icons/pause_normal.png"), (MenuBar.Command) this);
    protected MenuBar.MenuItem resume = state.addItem("", new ThemeResource("icons/arrow_right_normal.png"), (MenuBar.Command) this);
    protected MenuBar priorityMenubar = new MenuBar();
    protected MenuBar.MenuItem priority = priorityMenubar.addItem("", null);
    protected MenuBar.MenuItem priority0 = null;
    protected MenuBar.MenuItem priority1 = null;
    protected MenuBar.MenuItem priority2 = null;
    protected Panel taskPanel = new Panel();
    protected boolean initial = true;

    public HumanTaskWindow(String caption, PortletApplicationContext2 portletApplicationContext2) {
        super(caption, portletApplicationContext2);
    }

    public void initUI() {
        initial = task == null ? true : false;
        layout.setMargin(false);
        layout.setSpacing(false);
        layout.setStyleName("white");
        this.setContent(layout);
        layout.setSizeUndefined();

        if (!initial) {
            prepareTopBar();
        }
        preparePanel();

        // min width
        HorizontalLayout x = new HorizontalLayout();
        x.setWidth("600px");
        x.setHeight("0px");
        this.addComponent(x);
        this.center();
        this.setModal(true);
        this.setResizable(false);
    }

    private void prepareTopBar() {
        topBar.setMargin(false);
        topBar.setSpacing(false);
        topBar.setWidth("100%");
        topBar.setStyleName("menubar");

        repaintActorMenu();
        repaintStateMenu();

        priority0 = priority.addItem(messages.getString("PRIORITY_NORMAL"), new ThemeResource("icons/attention_normal.png"), (MenuBar.Command) this);
        priority1 = priority.addItem(messages.getString("PRIORITY_HIGH"), new ThemeResource("icons/attention_high.png"), (MenuBar.Command) this);
        priority1.setStyleName("red");
        priority2 = priority.addItem(messages.getString("PRIORITY_URGENT"), new ThemeResource("icons/attention_urgent.png"), (MenuBar.Command) this);
        priority2.setStyleName("red-bold");
        repaintPriorityMenu(task.getPriority());

        topBar.addComponent(actorMenubar, 0);
        topBar.setComponentAlignment(actorMenubar, Alignment.TOP_LEFT);
        topBar.setExpandRatio(actorMenubar, 1);
        topBar.addComponent(priorityMenubar, 1);
        topBar.setComponentAlignment(priorityMenubar, Alignment.TOP_RIGHT);
        layout.addComponent(topBar, 0);
    }

    private void preparePanel() {
        VerticalLayout vl = new VerticalLayout();
        vl.setSizeFull();
        vl.setMargin(true, true, true, true);
        vl.setSpacing(false);
        vl.addComponent(taskPanel);
        taskPanel.setSizeUndefined();

        vl.setComponentAlignment(taskPanel, Alignment.MIDDLE_CENTER);
        layout.addComponent(vl);
        layout.setComponentAlignment(vl, Alignment.MIDDLE_CENTER);
        layout.setExpandRatio(vl, 1);

        enableTaskPanel();
    }

    public void menuSelected(MenuItem selectedItem) {
        try {
            if (selectedItem.equals(priority0)) {
                bpmModule.setActivityInstancePriority(task.getUUID(), 0);
                repaintPriorityMenu(0);
            } else if (selectedItem.equals(priority1)) {
                bpmModule.setActivityInstancePriority(task.getUUID(), 1);
                repaintPriorityMenu(1);
            } else if (selectedItem.equals(priority2)) {
                bpmModule.setActivityInstancePriority(task.getUUID(), 2);
                repaintPriorityMenu(2);
            } else if (selectedItem.equals(suspend)) {
                task = bpmModule.suspendTask(task.getUUID(), true);
                repaintStateMenu();
            } else if (selectedItem.equals(resume)) {
                task = bpmModule.resumeTask(task.getUUID(), true);
                repaintStateMenu();
            } else if (selectedItem.equals(actor) && !task.isTaskAssigned()) {
                task = bpmModule.assignAndStartTask(task.getUUID(), this.getCurrentUser().getScreenName());
                repaintActorMenu();
                repaintStateMenu();
                state.setEnabled(true);
            }
            enableTaskPanel();
        } catch (Exception ex) {
            ex.printStackTrace();
            this.showError(ex.getMessage());
        }
    }

    private void enableTaskPanel() {
        if (initial){
            taskPanel.setEnabled(true);
        } else if (!initial && task.isTaskAssigned() && task.getState() == ActivityState.EXECUTING) {
            taskPanel.setEnabled(true);
        } else {
            taskPanel.setEnabled(false);
        }
    }

    private void repaintPriorityMenu(int pri) {
        String priorityText = null;
        String priorityStyle = null;
        ThemeResource priorityIcon = null;
        switch (pri) {
            case 0:
                priorityText = messages.getString("priority") + ": " + messages.getString("PRIORITY_NORMAL");
                priorityIcon = new ThemeResource("icons/attention_normal.png");
                priority0.setEnabled(false);
                priority1.setEnabled(true);
                priority2.setEnabled(true);
                break;
            case 1:
                priorityText = messages.getString("priority") + ": " + messages.getString("PRIORITY_HIGH");
                priorityIcon = new ThemeResource("icons/attention_high.png");
                priority0.setEnabled(true);
                priority1.setEnabled(false);
                priority2.setEnabled(true);
                break;
            case 2:
                priorityText = messages.getString("priority") + ": " + messages.getString("PRIORITY_URGENT");
                priorityIcon = new ThemeResource("icons/attention_urgent.png");
                priority0.setEnabled(true);
                priority1.setEnabled(true);
                priority2.setEnabled(false);
                break;
        }
        priority.setText(priorityText);
        priority.setIcon(priorityIcon);
        priority.setStyleName(priorityStyle);
        priority.setEnabled(task.isTaskAssigned());
    }

    private void repaintStateMenu() {
        state.setText(messages.getString("State") + ": " + messages.getString(task.getState().toString()));
        suspend.setText(messages.getString("btnSuspend"));
        resume.setText(messages.getString("btnResume"));
        if (task.getState() == ActivityState.SUSPENDED) {
            state.setIcon(new ThemeResource("icons/pause_normal.png"));
            suspend.setEnabled(false);
            resume.setEnabled(true);
        } else {
            state.setIcon(new ThemeResource("icons/arrow_right_normal.png"));
            suspend.setEnabled(true);
            resume.setEnabled(false);
        }
        state.setEnabled(task.isTaskAssigned());
    }

    private void repaintActorMenu() {

        if (task.isTaskAssigned()) {
            actor.setText(messages.getString("taskAssignedBy") + ": " + task.getTaskUser());
            actor.setIcon(new ThemeResource("icons/user.png"));
            actor.setStyleName("actor");
        } else {
            actor.setText(messages.getString("btnAccept"));
            actor.setIcon(new ThemeResource("icons/accept.png"));
            actor.setStyleName("actor");
        }
    }

    public void setBpmModule(BPMModule bpmModule) {
        this.bpmModule = bpmModule;
    }

    public BPMModule getBpmModule() {
        return bpmModule;
    }

    public void setTask(TaskInstance task) {
        this.task = task;
    }

    public Panel getTaskPanel() {
        return taskPanel;
    }

    public void setMessages(ResourceBundle messages) {
        this.messages = messages;
    }

    public void setPortletApplicationContext2(PortletApplicationContext2 portletApplicationContext2) {
        this.portletApplicationContext2 = portletApplicationContext2;
    }

    public void setProcessDef(LightProcessDefinition processDef) {
        this.processDef = processDef;
    }
}
