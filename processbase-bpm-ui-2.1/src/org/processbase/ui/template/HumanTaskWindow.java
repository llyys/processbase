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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.Comment;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.portlet.PbPortlet;

/**
 *
 * @author marat
 */
public class HumanTaskWindow extends PbWindow implements MenuBar.Command, Button.ClickListener {

    protected VerticalLayout mainLayout = new VerticalLayout();
    protected VerticalLayout layout = new VerticalLayout();
    protected VerticalLayout commentsLayout = new VerticalLayout();
    protected TaskInstance task = null;
    protected LightProcessDefinition processDef;
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
    protected RichTextArea commentEditor = new RichTextArea(PbPortlet.getCurrent().messages.getString("addComment"));
    protected Button addCommentBtn = new Button(PbPortlet.getCurrent().messages.getString("btnSave"), (Button.ClickListener) this);

    public HumanTaskWindow(String caption) {
        super(caption);
    }

    public void initUI() {
        mainLayout.setMargin(false);
        mainLayout.setSpacing(false);
        mainLayout.setStyleName("white");

        mainLayout.setSizeFull();

        layout.setSizeFull();
        layout.setMargin(true, true, true, true);
        layout.setSpacing(false);

        if (task != null) {
            prepareTopBar();
            addDescription();
        }
        mainLayout.addComponent(layout);
        mainLayout.setExpandRatio(layout, 1);

        prepareTabSheet();
        preparePanel();
        if (task != null) {
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

        priority0 = priority.addItem(PbPortlet.getCurrent().messages.getString("PRIORITY_NORMAL"), new ThemeResource("icons/attention_normal.png"), (MenuBar.Command) this);
        priority1 = priority.addItem(PbPortlet.getCurrent().messages.getString("PRIORITY_HIGH"), new ThemeResource("icons/attention_high.png"), (MenuBar.Command) this);
        priority1.setStyleName("red");
        priority2 = priority.addItem(PbPortlet.getCurrent().messages.getString("PRIORITY_URGENT"), new ThemeResource("icons/attention_urgent.png"), (MenuBar.Command) this);
        priority2.setStyleName("red-bold");
        repaintPriorityMenu(task.getPriority());

        topBar.addComponent(menubar, 0);
        topBar.setComponentAlignment(menubar, Alignment.TOP_LEFT);
        topBar.setExpandRatio(menubar, 1);
        mainLayout.addComponent(topBar, 0);
    }

    private void addDescription() {

        String dynLabel = task.getDynamicLabel();
        String dynDescr = task.getDynamicDescription();
        StringBuilder text = new StringBuilder();
        if (dynLabel != null) {
            text.append("<b>").append(dynLabel).append("</b>");
        }
        if (dynDescr != null) {
            text.append("<p>").append(dynDescr).append("</p>");
        }
        if (task.getState() == ActivityState.FINISHED) {
            String dynExec = task.getDynamicExecutionSummary();
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
        vl.setSizeFull();
        vl.setMargin(true, true, true, true);
        vl.setSpacing(false);
        vl.addComponent(taskPanel);
        taskPanel.setSizeUndefined();

        vl.setComponentAlignment(taskPanel, Alignment.MIDDLE_CENTER);
        tabSheet.addTab(vl, PbPortlet.getCurrent().messages.getString("taskDetails"), new ThemeResource("icons/document-txt.png"));
    }

    private void prepareComments() {
        commentsLayout.setWidth("100%");
        commentsLayout.setMargin(true);
        commentsLayout.setSpacing(true);
        commentsLayout.removeAllComponents();
        List<Comment> comments = new ArrayList<Comment>(0);
        try {
            comments = PbPortlet.getCurrent().bpmModule.getCommentFeed(task.getProcessInstanceUUID());
        } catch (Exception ex) {
            Logger.getLogger(HumanTaskWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
        }

        for (Comment comment : comments) {
            commentsLayout.addComponent(getCommentPanel(comment.getDate(), comment.getUserId(), comment.getMessage()));
        }
        commentEditor.setWidth("100%");
        commentEditor.setValue(null);
        commentEditor.setNullRepresentation("");
        commentsLayout.addComponent(commentEditor);
        commentsLayout.addComponent(addCommentBtn);

        tabSheet.addTab(commentsLayout, PbPortlet.getCurrent().messages.getString("comments") + " (" + comments.size() + ")", new ThemeResource("icons/comment_yellow.gif"));
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
                PbPortlet.getCurrent().bpmModule.setActivityInstancePriority(task.getUUID(), 0);
                repaintPriorityMenu(0);
            } else if (selectedItem.equals(priority1)) {
                PbPortlet.getCurrent().bpmModule.setActivityInstancePriority(task.getUUID(), 1);
                repaintPriorityMenu(1);
            } else if (selectedItem.equals(priority2)) {
                PbPortlet.getCurrent().bpmModule.setActivityInstancePriority(task.getUUID(), 2);
                repaintPriorityMenu(2);
            } else if (selectedItem.equals(suspend)) {
                task = PbPortlet.getCurrent().bpmModule.suspendTask(task.getUUID(), true);
                repaintStateMenu();
            } else if (selectedItem.equals(resume)) {
                task = PbPortlet.getCurrent().bpmModule.resumeTask(task.getUUID(), true);
                repaintStateMenu();
            } else if (selectedItem.equals(actor) && !task.isTaskAssigned()) {
                task = PbPortlet.getCurrent().bpmModule.assignAndStartTask(task.getUUID(), ((User) PbPortlet.getCurrent().getUser()).getScreenName());
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
        if (task == null) {
            tabSheet.setEnabled(true);
        } else if (task != null && task.isTaskAssigned() && task.getState() == ActivityState.EXECUTING) {
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
                priorityText = PbPortlet.getCurrent().messages.getString("priority") + ": " + PbPortlet.getCurrent().messages.getString("PRIORITY_NORMAL");
                priorityIcon = new ThemeResource("icons/attention_normal.png");
                priority0.setEnabled(false);
                priority1.setEnabled(true);
                priority2.setEnabled(true);
                break;
            case 1:
                priorityText = PbPortlet.getCurrent().messages.getString("priority") + ": " + PbPortlet.getCurrent().messages.getString("PRIORITY_HIGH");
                priorityIcon = new ThemeResource("icons/attention_high.png");
                priority0.setEnabled(true);
                priority1.setEnabled(false);
                priority2.setEnabled(true);
                break;
            case 2:
                priorityText = PbPortlet.getCurrent().messages.getString("priority") + ": " + PbPortlet.getCurrent().messages.getString("PRIORITY_URGENT");
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
        state.setText(PbPortlet.getCurrent().messages.getString("State") + ": " + PbPortlet.getCurrent().messages.getString(task.getState().toString()));
        suspend.setText(PbPortlet.getCurrent().messages.getString("btnSuspend"));
        resume.setText(PbPortlet.getCurrent().messages.getString("btnResume"));
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
            actor.setText(PbPortlet.getCurrent().messages.getString("taskAssignedBy") + ": " + task.getTaskUser());
            actor.setIcon(new ThemeResource("icons/user.png"));
            actor.setStyleName("actor");
        } else {
            actor.setText(PbPortlet.getCurrent().messages.getString("btnAccept"));
            actor.setIcon(new ThemeResource("icons/accept.png"));
            actor.setStyleName("actor");
        }
    }

    private void repaintDatesMenu() {
        readyDate.setText(PbPortlet.getCurrent().messages.getString("taskReadyDate") + ": " + String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", new Object[]{task.getReadyDate()}));
        readyDate.setIcon(new ThemeResource("icons/calendar.png"));
//            readyDate.setStyleName("actor");
        if (task.getExpectedEndDate() != null) {
            expectedEndDate.setText(PbPortlet.getCurrent().messages.getString("taskExpectedEndDate") + ": " + String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", new Object[]{task.getExpectedEndDate()}));
            expectedEndDate.setIcon(new ThemeResource("icons/calendar.png"));
//            readyDate.setStyleName("actor");
        }
        lastUpdatedDate.setText(PbPortlet.getCurrent().messages.getString("taskLastUpdateDate") + ": " + String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", new Object[]{task.getLastUpdateDate()}));
        lastUpdatedDate.setIcon(new ThemeResource("icons/calendar.png"));
//            readyDate.setStyleName("actor");
    }

    public void setTask(TaskInstance task) {
        this.task = task;
    }

    public Panel getTaskPanel() {
        return taskPanel;
    }

    public void setProcessDef(LightProcessDefinition processDef) {
        this.processDef = processDef;
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(addCommentBtn)) {
            try {
                if (!commentEditor.getValue().toString().isEmpty()) {
                    PbPortlet.getCurrent().bpmModule.addComment(task.getUUID(), commentEditor.getValue().toString(), PbPortlet.getCurrent().getPortalUser().getScreenName());
                    prepareComments();
                }
            } catch (Exception ex) {
                Logger.getLogger(HumanTaskWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
                showError(ex.getMessage());
            }
        }
    }
}
