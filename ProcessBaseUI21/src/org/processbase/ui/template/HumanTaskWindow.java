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

import com.liferay.portal.service.persistence.UserUtil;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.HashMap;
import java.util.ResourceBundle;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.processbase.bpm.BPMModule;
import org.processbase.core.Constants;

/**
 *
 * @author marat
 */
public class HumanTaskWindow extends PbWindow implements Button.ClickListener {

    protected VerticalLayout layout = new VerticalLayout();
    protected HashMap<String, Object> procVariables = new HashMap<String, Object>();
    protected TaskInstance task;
    protected BPMModule bpmModule = null;
    protected HorizontalLayout buttonBar = new HorizontalLayout();
    protected HorizontalLayout topBar = new HorizontalLayout();
    protected MenuBar actorMenubar = new MenuBar();
    protected MenuBar.MenuItem actor = null;
    protected MenuBar priorityMenubar = new MenuBar();
    protected MenuBar.MenuItem priority = null;
    protected MenuBar.MenuItem priority0 = null;
    protected MenuBar.MenuItem priority1 = null;
    protected MenuBar.MenuItem priority2 = null;
    protected Panel taskPanel = new Panel();
    protected Button acceptBtn = null;
    protected Button suspendBtn = null;
    protected Button closeBtn = null;

    public HumanTaskWindow(String caption, PortletApplicationContext2 portletApplicationContext2) {
        super(caption, portletApplicationContext2);
    }

    public void initUI() {
        layout.setMargin(false);
        layout.setSpacing(false);
        layout.setStyleName("blue");
//        layout.setSizeFull();
        this.setContent(layout);
        this.center();
        this.setSizeUndefined();

        prepareTopBar();
        preparePanel();
        prepareButtonBar();
        this.setModal(true);
    }

    private void prepareTopBar() {
        topBar.setMargin(false);
        topBar.setSpacing(false);
        topBar.setWidth("100%");

        actorMenubar.setWidth("100%");

//        UserUtil.findByC_SN(Constants.COMPANY_NAME, task.getTaskUser());
        String actorText = messages.getString("taskAssignedBy") + (task.isTaskAssigned() ? ": " + task.getTaskUser() : ": ?");
        actor = actorMenubar.addItem(actorText, new ThemeResource("icons/user.png"), null);
        actor.setStyleName("actor");

        String priorityText = null;
        ThemeResource priorityIcon = null;
        priority = priorityMenubar.addItem("", null);
        priority0 = priority.addItem(messages.getString("PRIORITY_NORMAL"), new ThemeResource("icons/arrow_right_normal.png"), null);
        priority1 = priority.addItem(messages.getString("PRIORITY_HIGH"), new ThemeResource("icons/arrow_right_high.png"), null);
        priority1.setStyleName("red");
        priority2 = priority.addItem(messages.getString("PRIORITY_URGENT"), new ThemeResource("icons/arrow_right_urgent.png"), null);
        priority2.setStyleName("red-bold");
        switch (task.getPriority()) {
            case 0:
                priorityText = messages.getString("priority") + ": " + messages.getString("PRIORITY_NORMAL");
                priorityIcon = new ThemeResource("icons/arrow_right_normal.png");
                priority0.setEnabled(false);
                break;
            case 1:
                priorityText = messages.getString("priority") + ": " + messages.getString("PRIORITY_HIGH");
                priorityIcon = new ThemeResource("icons/arrow_right_high.png");
                priority1.setEnabled(false);
                break;
            case 2:
                priorityText = messages.getString("priority") + ": " + messages.getString("PRIORITY_URGENT");
                priorityIcon = new ThemeResource("icons/arrow_right_urgent.png");
                priority2.setEnabled(false);
                break;
        }
        priority.setText(priorityText);
        priority.setIcon(priorityIcon);

        topBar.addComponent(actorMenubar, 0);
        topBar.setExpandRatio(actorMenubar, 1);
        topBar.addComponent(priorityMenubar, 1);
        layout.addComponent(topBar, 0);
    }

    private void preparePanel() {
        VerticalLayout vl = new VerticalLayout();
        vl.setWidth("100%");
        vl.setMargin(true, true, true, true);
        vl.setSpacing(false);
        vl.addComponent(taskPanel);
        vl.setComponentAlignment(taskPanel, Alignment.MIDDLE_CENTER);
        layout.addComponent(vl, 1);
        vl.setStyleName("black");

    }

    private void prepareButtonBar() {
        buttonBar.setMargin(true);
        buttonBar.setSpacing(true);
        buttonBar.setWidth("100%");
        buttonBar.setStyleName("buttonbar");

        acceptBtn = new Button(messages.getString("btnAccept"), this);
        buttonBar.addComponent(acceptBtn, 0);
        buttonBar.setComponentAlignment(acceptBtn, Alignment.MIDDLE_RIGHT);
        buttonBar.setExpandRatio(acceptBtn, 1);
        acceptBtn.setEnabled(!task.isTaskAssigned());

        String suspendBtnCaption = task.getState() == ActivityState.EXECUTING ? messages.getString("btnSuspend") : messages.getString("btnResume");
        suspendBtn = new Button(suspendBtnCaption, this);
        buttonBar.addComponent(suspendBtn, 1);
        buttonBar.setComponentAlignment(suspendBtn, Alignment.MIDDLE_RIGHT);
        suspendBtn.setEnabled(task.isTaskAssigned());

        closeBtn = new Button(messages.getString("btnClose"), this);
        buttonBar.addComponent(closeBtn, 2);
        buttonBar.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);

        layout.addComponent(buttonBar, 2);
    }

    public void buttonClick(ClickEvent event) {
        Button btn = event.getButton();
        try {
            if (btn.equals(acceptBtn)) {
                task = bpmModule.assignTask(task.getUUID(), this.getCurrentUser().getScreenName());
                actor.setText(messages.getString("taskAssignedBy") + ": " + task.getTaskUser());
                acceptBtn.setEnabled(false);
            } else if (btn.equals(closeBtn)) {
                this.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
}
