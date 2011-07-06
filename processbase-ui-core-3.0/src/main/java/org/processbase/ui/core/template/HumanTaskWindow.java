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
package org.processbase.ui.core.template;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.Comment;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.light.LightProcessDefinition;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;

/**
 *
 * @author marat
 */
public class HumanTaskWindow extends PbWindow implements MenuBar.Command, Button.ClickListener {

    protected AbstractOrderedLayout mainLayout;
    protected HorizontalLayout layout = new HorizontalLayout();
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
    protected BPMModule bpmModule = null;
    private ResourceBundle messages = null;
    private String currentUserName = null;
//    protected CustomPortlet customPortlet = null;
    protected Map<String, Object> processInstanceVariables = new HashMap<String, Object>();
    protected Map<String, Object> activityInstanceVariables = new HashMap<String, Object>();
    protected Map<String, DataFieldDefinition> processDataFieldDefinitions = new HashMap<String, DataFieldDefinition>();
    protected Map<String, DataFieldDefinition> activityDataFieldDefinitions = new HashMap<String, DataFieldDefinition>();

    public HumanTaskWindow(String caption, boolean custom) {
        super(caption);
        this.custom = custom;
    }

    protected HumanTaskWindow(String caption) {
        this(caption, false);
    }

    private void prepareCustom() {
        try {
//            if (customPortlet.getType() == CustomPortlet.TYPE_START_PROCESS) {
//                ProcessDefinition pd = bpmModule.getProcessDefinition(new ProcessDefinitionUUID(customPortlet.getProcessDefinitionUUID()));
//                setProcessDef(pd);
//            } else if (customPortlet.getType() == CustomPortlet.TYPE_TASK) {
//                taskInstance = bpmModule.getTaskInstance(new ActivityInstanceUUID(customPortlet.getTaskInstanceUUID()));
//            }
//            customPortlet.getPortletSession().removeAttribute("PROCESSBASE_SHARED_PROCESSINSTANCE", PortletSession.APPLICATION_SCOPE);
//            customPortlet.getPortletSession().removeAttribute("PROCESSBASE_SHARED_TASKINSTANCE", PortletSession.APPLICATION_SCOPE);
//            customPortlet.setInitialized(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initUI() {
//        System.out.println("ProcessbaseApplication.getCurrent() = " + ProcessbaseApplication.getCurrent());
//        System.out.println("currentUserName = " + ProcessbaseApplication.getCurrent().getUserName());
        currentUserName = ProcessbaseApplication.getCurrent().getUserName();
        bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
        messages = ProcessbaseApplication.getCurrent().getPbMessages();
        commentEditor = new RichTextArea(messages.getString("addComment"));
        addCommentBtn = new Button(messages.getString("btnSave"), (Button.ClickListener) this);

        if (taskInstance != null) {
            mainLayout = new VerticalLayout();
            prepareTopBar();
            //addDescription();
            layout.setSizeFull();
            layout.setMargin(true, true, true, true);
            layout.setSpacing(false);
            mainLayout.addComponent(layout);
            mainLayout.setExpandRatio(layout, 1.0f);
            mainLayout.setMargin(false);
            mainLayout.setSpacing(false);
        } else {
            mainLayout = new HorizontalLayout();
            mainLayout.setMargin(true);
            mainLayout.setSpacing(true);
        }
        mainLayout.setStyleName("white");

        if (custom) {
            prepareCustom();
        } else if (!custom) { // set only for generated window
            mainLayout.setSizeFull();
        }
        prepareVariables();
        preparePanel();
        if (taskInstance != null) {
            prepareComments();
        }

//        mainLayout.setStyleName(Reindeer.LAYOUT_BLACK);
//        setStyleName(Reindeer.LAYOUT_WHITE);
//        layout.setStyleName(Reindeer.LAYOUT_BLUE);
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

//        topBar.addComponent(menubar, 0);
//        topBar.setComponentAlignment(menubar, Alignment.TOP_LEFT);
//        topBar.setExpandRatio(menubar, 1);

        mainLayout.addComponent(menubar, 0);
//        mainLayout.setComponentAlignment(menubar, Alignment.TOP_LEFT);
//        mainLayout.setExpandRatio(menubar, 1);
    }

    private Label descriptionLabel;
    protected void addDescription(TaskInstance task) {
    	String activytiDesc=task.getActivityDescription();
        String dynLabel = task.getDynamicLabel();
        String dynDescr = task.getDynamicDescription();
        StringBuilder text = new StringBuilder();
        if(activytiDesc!=null)
        {
        	text.append(activytiDesc);
        }
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

        
    	if(descriptionLabel==null)
        {
    		descriptionLabel=new Label(text.length() > 0?text.toString():"", Label.CONTENT_XHTML);
    		layout.addComponent(descriptionLabel);
        }
    	else{
    		descriptionLabel.setValue(text.length() > 0?text.toString():"");
    	}
        
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
        if (taskInstance != null) {
            tabSheet.setSizeFull();
            tabSheet.setStyleName("minimal");
            layout.addComponent(tabSheet);
            layout.setExpandRatio(tabSheet, 1.0f);
            tabSheet.addTab(vl, tabCaption, new ThemeResource("icons/document-txt.png"));
        } else {
//            vl.setSizeUndefined();
            mainLayout.setSizeUndefined();
            mainLayout.setWidth("100%");
            mainLayout.addComponent(vl);
            mainLayout.setExpandRatio(vl, 1.0f);
        }
        enabletabPanel();
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
            ex.printStackTrace();
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
                taskInstance = bpmModule.assignAndStartTask(taskInstance.getUUID(), currentUserName);
                repaintActorMenu();
                repaintStateMenu();
                state.setEnabled(true);
            }
            enabletabPanel();
        } catch (Exception ex) {
            ex.printStackTrace();
            this.showError(ex.getMessage());
        }
    }

    private void enabletabPanel() {
        if (taskInstance == null) {
            tabSheet.setEnabled(true);
            taskPanel.setEnabled(true);
        } else if (taskInstance != null && taskInstance.isTaskAssigned() && taskInstance.getState() == ActivityState.EXECUTING) {
            tabSheet.setEnabled(true);
            taskPanel.setEnabled(true);
        } else {
            tabSheet.setEnabled(false);
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
                    bpmModule.addComment(taskInstance.getUUID(), commentEditor.getValue().toString(), currentUserName);
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

    private void prepareVariables() {
        try {
            if (taskInstance != null) {
                for (DataFieldDefinition dfd : bpmModule.getProcessDataFields(taskInstance.getProcessDefinitionUUID())) {
                    processDataFieldDefinitions.put(dfd.getName(), dfd);
                }
                for (DataFieldDefinition dfd : bpmModule.getActivityDataFields(taskInstance.getActivityDefinitionUUID())) {
                    activityDataFieldDefinitions.put(dfd.getName(), dfd);
                }
                processInstanceVariables.putAll(bpmModule.getProcessInstanceVariables(taskInstance.getProcessInstanceUUID()));
                activityInstanceVariables.putAll(bpmModule.getActivityInstanceVariables(taskInstance.getUUID()));
            } else {
                for (DataFieldDefinition dfd : bpmModule.getProcessDataFields(processDefinition.getUUID())) {
                    processDataFieldDefinitions.put(dfd.getName(), dfd);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Map<String, DataFieldDefinition> getActivityDataFieldDefinitions() {
        return activityDataFieldDefinitions;
    }

    public Map<String, Object> getActivityInstanceVariables() {
        return activityInstanceVariables;
    }

    public String getCurrentUser() {
        return currentUserName;
    }

    public Map<String, DataFieldDefinition> getProcessDataFieldDefinitions() {
        return processDataFieldDefinitions;
    }

    public Map<String, Object> getProcessInstanceVariables() {
        return processInstanceVariables;
    }

    public TabSheet getTabSheet() {
        return tabSheet;
    }

    public VerticalLayout getCommentsLayout() {
        return commentsLayout;
    }

    public void setProcessDefinition(LightProcessDefinition processDefinition) {
        this.processDefinition = processDefinition;
    }

    public void setTaskInstance(TaskInstance taskInstance) {
        this.taskInstance = taskInstance;
    }
    
    
}
