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

import com.liferay.portal.model.User;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.portlet.PortletSession;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.Comment;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.portlet.CustomPortlet;
import org.processbase.ui.portlet.PbPortlet;

/**
 *
 * @author marat
 */
public class HumanTaskWindow extends PbWindow implements MenuBar.Command, Button.ClickListener {

    protected VerticalLayout mainLayout = new VerticalLayout();
    protected VerticalLayout layout = new VerticalLayout();
    protected VerticalLayout commentsLayout = new VerticalLayout();
    protected TaskInstance taskInstance = null;
    protected LightProcessDefinition processDefinition;
    protected VerticalLayout topBar = new VerticalLayout();
    protected MenuBar menubar = new MenuBar();
    protected MenuBar.MenuItem actor = menubar.addItem("", (MenuBar.Command) this);
    protected MenuBar.MenuItem state = menubar.addItem("", null);
    protected MenuBar.MenuItem suspend = state.addItem("", new ThemeResource("icons/pause_normal.png"), (MenuBar.Command) this);
    protected MenuBar.MenuItem resume = state.addItem("", new ThemeResource("icons/arrow_right_normal.png"), (MenuBar.Command) this);
    protected MenuBar.MenuItem readyDate = menubar.addItem("", null);
    protected MenuBar.MenuItem expectedEndDate = menubar.addItem("", null);
    protected MenuBar.MenuItem lastUpdatedDate = menubar.addItem("", null);
    protected MenuBar.MenuItem priority = menubar.addItem("", null);
    protected MenuBar.MenuItem priority0 = null;
    protected MenuBar.MenuItem priority1 = null;
    protected MenuBar.MenuItem priority2 = null;
    protected Panel taskPanel = new Panel();
    protected TabSheet tabSheet = new TabSheet();
    protected RichTextArea commentEditor = null;
    protected Button addCommentBtn = null;
    private boolean custom = false;
    private BPMModule bpmModule = null;
    private ResourceBundle messages = null;
    private User currentUser = null;
    private CustomPortlet customPortlet = null;

    public HumanTaskWindow(String caption, boolean custom) {
        super(caption);
        this.custom = custom;
    }

    protected HumanTaskWindow(String caption) {
        this(caption, false);
    }

    private void prepareCustom() {
        try {
            if (customPortlet.getType() == CustomPortlet.TYPE_START_PROCESS ){
                ProcessDefinition pd = bpmModule.getProcessDefinition(new ProcessDefinitionUUID(customPortlet.processDefinitionUUID));
                setProcessDef(pd);
            } else if (customPortlet.getType() == CustomPortlet.TYPE_TASK ){
                taskInstance = bpmModule.getTaskInstance(new ActivityInstanceUUID(customPortlet.taskInstanceUUID));
            }
            customPortlet.portletSession.removeAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE);
            customPortlet.portletSession.removeAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE);
            customPortlet.setInitialized(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initUI() {
        if (custom) {
            System.out.println("APP = " + getApplication().getClass());
            customPortlet = ((CustomPortlet)getApplication());
            currentUser = customPortlet.getPortalUser();
            bpmModule = customPortlet.bpmModule;
            messages = customPortlet.messages;
            prepareCustom();
        } else {
            currentUser = PbPortlet.getCurrent().getPortalUser();
            bpmModule = PbPortlet.getCurrent().bpmModule;
            messages = PbPortlet.getCurrent().messages;
        }

        commentEditor = new RichTextArea(messages.getString("addComment"));
        addCommentBtn = new Button(messages.getString("btnSave"), (Button.ClickListener) this);

        mainLayout.setMargin(false);
        mainLayout.setSpacing(false);
        mainLayout.setStyleName("white");

        if (!custom) { // set only for generated window
            mainLayout.setSizeFull();
        }

        layout.setSizeFull();
        layout.setMargin(true, true, true, true);
        layout.setSpacing(false);

        if (taskInstance != null) {
            prepareTopBar();
            addDescription();
        }
        mainLayout.addComponent(layout);
        mainLayout.setExpandRatio(layout, 1);

        prepareTabSheet();
        preparePanel();
        if (taskInstance != null) {
            prepareComments();
        }

        setContent(mainLayout);
        setWidth("90%");
        setHeight("90%");
        center();
        setModal(true);
        setResizable(false);
    }

    private void prepareTopBar() {
        topBar.setMargin(false);
        topBar.setSpacing(false);
        topBar.setWidth("100%");
        topBar.setStyleName("menubar");

        repaintActorMenu();
        repaintDatesMenu();
        repaintStateMenu();

        priority0 = priority.addItem(messages.getString("PRIORITY_NORMAL"), new ThemeResource("icons/attention_normal.png"), (MenuBar.Command) this);
        priority1 = priority.addItem(messages.getString("PRIORITY_HIGH"), new ThemeResource("icons/attention_high.png"), (MenuBar.Command) this);
        priority1.setStyleName("red");
        priority2 = priority.addItem(messages.getString("PRIORITY_URGENT"), new ThemeResource("icons/attention_urgent.png"), (MenuBar.Command) this);
        priority2.setStyleName("red-bold");
        repaintPriorityMenu(taskInstance.getPriority());

        topBar.addComponent(menubar, 0);
        topBar.setComponentAlignment(menubar, Alignment.TOP_LEFT);
        topBar.setExpandRatio(menubar, 1);
        mainLayout.addComponent(topBar, 0);
    }

    private void addDescription() {

        String dynLabel = taskInstance.getDynamicLabel();
        String dynDescr = taskInstance.getDynamicDescription();
        StringBuilder text = new StringBuilder();
        if (dynLabel != null) {
            text.append("<b>").append(dynLabel).append("</b>");
        }
        if (dynDescr != null) {
            text.append("<p>").append(dynDescr).append("</p>");
        }
        if (taskInstance.getState() == ActivityState.FINISHED) {
            String dynExec = taskInstance.getDynamicExecutionSummary();
            if (dynExec != null) {
                text.append("<p>").append(dynExec).append("</p>");
            }
        }

        if (text.length() > 0) {
            layout.addComponent(new Label(text.toString(), Label.CONTENT_XHTML));
        }
    }

    private void prepareTabSheet() {
        tabSheet.setSizeFull();
        tabSheet.setStyleName("minimal");
        layout.addComponent(tabSheet);
        layout.setExpandRatio(tabSheet, 1);
        enabletabSheet();
    }

    private void preparePanel() {
        VerticalLayout vl = new VerticalLayout();
        vl.setWidth("100%");
        vl.setMargin(true, true, true, true);
        vl.setSpacing(false);
        vl.addComponent(taskPanel);
        taskPanel.setSizeUndefined();

        vl.setComponentAlignment(taskPanel, Alignment.MIDDLE_CENTER);
        String tabCaption = taskInstance != null
                ? messages.getString("taskDetails")
                : (processDefinition.getLabel() != null ? processDefinition.getLabel() : processDefinition.getName());
        tabSheet.addTab(vl, tabCaption, new ThemeResource("icons/document-txt.png"));
    }

    private void prepareComments() {
        commentsLayout.setWidth("100%");
        commentsLayout.setMargin(true);
        commentsLayout.setSpacing(true);
        commentsLayout.removeAllComponents();
        List<Comment> comments = new ArrayList<Comment>(0);
        try {
            comments = bpmModule.getCommentFeed(taskInstance.getProcessInstanceUUID());
        } catch (Exception ex) {
            Logger.getLogger(HumanTaskWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

        for (Comment comment : comments) {
            commentsLayout.addComponent(getCommentPanel(comment.getDate(), comment.getUserId(), comment.getMessage()));
        }
        commentEditor = new RichTextArea(messages.getString("addComment"));
        commentEditor.setWidth("100%");
        commentEditor.setNullRepresentation("");
        commentsLayout.addComponent(commentEditor);
        commentsLayout.addComponent(addCommentBtn);

        tabSheet.addTab(commentsLayout, messages.getString("comments") + " (" + comments.size() + ")", new ThemeResource("icons/comment_yellow.gif"));
    }

    private Panel getCommentPanel(Date date, String userId, String message) {
        Panel p = new Panel();
        p.setStyleName("minimal");
        ((Layout) p.getContent()).setMargin(false, true, false, true);
        p.addComponent(new Label(
                "<b>" + String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", new Object[]{date})
                + " - " + userId + "</b><p/>"
                + message,
                Label.CONTENT_XHTML));
        return p;
    }

    public void menuSelected(MenuItem selectedItem) {
        try {
            if (selectedItem.equals(priority0)) {
                bpmModule.setActivityInstancePriority(taskInstance.getUUID(), 0);
                repaintPriorityMenu(0);
            } else if (selectedItem.equals(priority1)) {
                bpmModule.setActivityInstancePriority(taskInstance.getUUID(), 1);
                repaintPriorityMenu(1);
            } else if (selectedItem.equals(priority2)) {
                bpmModule.setActivityInstancePriority(taskInstance.getUUID(), 2);
                repaintPriorityMenu(2);
            } else if (selectedItem.equals(suspend)) {
                taskInstance = bpmModule.suspendTask(taskInstance.getUUID(), true);
                repaintStateMenu();
            } else if (selectedItem.equals(resume)) {
                taskInstance = bpmModule.resumeTask(taskInstance.getUUID(), true);
                repaintStateMenu();
            } else if (selectedItem.equals(actor) && !taskInstance.isTaskAssigned()) {
                taskInstance = bpmModule.assignAndStartTask(taskInstance.getUUID(), currentUser.getScreenName());
                repaintActorMenu();
                repaintStateMenu();
                state.setEnabled(true);
            }
            enabletabSheet();
        } catch (Exception ex) {
            ex.printStackTrace();
            this.showError(ex.getMessage());
        }
    }

    private void enabletabSheet() {
        if (taskInstance == null) {
            tabSheet.setEnabled(true);
        } else if (taskInstance != null && taskInstance.isTaskAssigned() && taskInstance.getState() == ActivityState.EXECUTING) {
            tabSheet.setEnabled(true);
        } else {
            tabSheet.setEnabled(false);
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
        priority.setEnabled(taskInstance.isTaskAssigned());
    }

    private void repaintStateMenu() {
        state.setText(messages.getString("State") + ": " + messages.getString(taskInstance.getState().toString()));
        suspend.setText(messages.getString("btnSuspend"));
        resume.setText(messages.getString("btnResume"));
        if (taskInstance.getState() == ActivityState.SUSPENDED) {
            state.setIcon(new ThemeResource("icons/pause_normal.png"));
            suspend.setEnabled(false);
            resume.setEnabled(true);
        } else {
            state.setIcon(new ThemeResource("icons/arrow_right_normal.png"));
            suspend.setEnabled(true);
            resume.setEnabled(false);
        }
        state.setEnabled(taskInstance.isTaskAssigned());
    }

    private void repaintActorMenu() {

        if (taskInstance.isTaskAssigned()) {
            actor.setText(messages.getString("taskAssignedBy") + ": " + taskInstance.getTaskUser());
            actor.setIcon(new ThemeResource("icons/user.png"));
            actor.setStyleName("actor");
        } else {
            actor.setText(messages.getString("btnAccept"));
            actor.setIcon(new ThemeResource("icons/accept.png"));
            actor.setStyleName("actor");
        }
    }

    private void repaintDatesMenu() {
        readyDate.setText(messages.getString("taskReadyDate") + ": " + String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", new Object[]{taskInstance.getReadyDate()}));
        readyDate.setIcon(new ThemeResource("icons/calendar.png"));
//            readyDate.setStyleName("actor");
        if (taskInstance.getExpectedEndDate() != null) {
            expectedEndDate.setText(messages.getString("taskExpectedEndDate") + ": " + String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", new Object[]{taskInstance.getExpectedEndDate()}));
            expectedEndDate.setIcon(new ThemeResource("icons/calendar.png"));
//            readyDate.setStyleName("actor");
        }
        lastUpdatedDate.setText(messages.getString("taskLastUpdateDate") + ": " + String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", new Object[]{taskInstance.getLastUpdateDate()}));
        lastUpdatedDate.setIcon(new ThemeResource("icons/calendar.png"));
//            readyDate.setStyleName("actor");
    }

    public void setTask(TaskInstance task) {
        this.taskInstance = task;
    }

    public Panel getTaskPanel() {
        return taskPanel;
    }

    public void setProcessDef(LightProcessDefinition processDef) {
        this.processDefinition = processDef;
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(addCommentBtn)) {
            try {
                if (!commentEditor.getValue().toString().isEmpty()) {
                    bpmModule.addComment(taskInstance.getUUID(), commentEditor.getValue().toString(), currentUser.getScreenName());
                    prepareComments();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
//                Logger.getLogger(HumanTaskWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
                showError(ex.getMessage());
            }
        }
    }

    public LightProcessDefinition getProcessDefinition() {
        return processDefinition;
    }

    public TaskInstance getTask() {
        return taskInstance;
    }
}
