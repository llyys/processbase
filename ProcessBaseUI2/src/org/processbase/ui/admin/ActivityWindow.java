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
package org.processbase.ui.admin;

import com.liferay.portal.model.User;
import com.vaadin.data.Item;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.Date;
import java.util.Set;
import javax.portlet.PortletSession;
import org.processbase.ui.template.PbWindow;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ParticipantNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.AssignUpdate;
import org.ow2.bonita.facade.runtime.StateUpdate;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.light.LightActivityInstance;
import org.processbase.bpm.BPMModule;

/**
 *
 * @author mgubaidullin
 */
public class ActivityWindow extends PbWindow implements ClickListener, TabSheet.SelectedTabChangeListener {

    private VerticalLayout layout = (VerticalLayout) this.getContent();
    public ProcessDefinition processDefinition = null;
    private TaskInstance task = null;
    private LightActivityInstance lightActivity = null;
    private Set<DataFieldDefinition> dfds = null;
    protected BPMModule bpmModule = null;
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button closeBtn = new Button(messages.getString("btnClose"), this);
    private Button reassignBtn = new Button(messages.getString("btnReassign"), this);
    private Button unAssignBtn = new Button(messages.getString("btnUnassign"), this);
    private Button assignBtn = new Button(messages.getString("btnAssign"), this);
    private ListSelect candidatesField = new ListSelect(messages.getString("taskParticipantCandidates"));
    private TabSheet tabSheet = new TabSheet();
    private VerticalLayout variablesLayout = new VerticalLayout();
    private VerticalLayout participantLayout = new VerticalLayout();
    private VerticalLayout stateLayout = new VerticalLayout();
    protected Table assignUpdatesTable = new Table();
    protected Table stateUpdatesTable = new Table();

    public ActivityWindow(LightActivityInstance lightActivity, PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
        try {
            bpmModule = new BPMModule(((User) this.portletApplicationContext2.getPortletSession().getAttribute("PROCESSBASE_USER", PortletSession.APPLICATION_SCOPE)).getLogin());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            this.lightActivity = lightActivity;
            this.processDefinition = bpmModule.getProcessDefinition(lightActivity.getProcessDefinitionUUID());
            if (lightActivity.isTask()) {
                task = bpmModule.getTaskInstance(lightActivity.getUUID());
            }
            this.dfds = bpmModule.getProcessDataFields(lightActivity.getProcessDefinitionUUID());
            exec();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void exec() throws ParticipantNotFoundException, ProcessNotFoundException, VariableNotFoundException, InstanceNotFoundException, ActivityNotFoundException, Exception {
        setCaption(messages.getString("defaultTaskWindowCaption2") + " " + lightActivity.getActivityName());
        tabSheet.addListener((TabSheet.SelectedTabChangeListener) this);
        tabSheet.setSizeFull();
        if (task != null) {
            addParticipantInfo();
            participantLayout.setSizeFull();
            tabSheet.addTab(participantLayout, messages.getString("taskPerformer"), null);
        }
        try {
            addVariablesInfo();
            variablesLayout.setSizeFull();
            tabSheet.addTab(variablesLayout, messages.getString("processVariables"), null);
            addStateInfo();
            stateLayout.setSizeFull();
            tabSheet.addTab(stateLayout, messages.getString("taskStateUpdates"), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);
        buttons.setSpacing(true);
        buttons.addComponent(closeBtn);
        buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
//        buttons.setSizeFull();
        layout.addComponent(tabSheet);
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.addComponent(buttons);
        layout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
        layout.setStyleName(Reindeer.LAYOUT_WHITE);
        setModal(true);
        setResizable(false);
        setWidth("800px");
        setHeight("600px");
    }

    public void addVariablesInfo() throws ProcessNotFoundException, VariableNotFoundException, InstanceNotFoundException, ProcessNotFoundException, ActivityNotFoundException, ParticipantNotFoundException, Exception {
        for (DataFieldDefinition dfd : dfds) {
            Object value = null;
            try {
                value = bpmModule.getProcessInstanceVariable(lightActivity.getProcessInstanceUUID(), dfd.getName());
            } catch (Exception ex) {
                value = "MAYBE CUSTOM CLASS VALUE";
            }
            addField(dfd, value);
        }
    }

    public void addField(DataFieldDefinition dfd, Object value) {
        Field field = null;
        if (dfd.isEnumeration()) {
            field = new Select(dfd.getName(), dfd.getEnumerationValues());
            ((Select) field).setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
            ((Select) field).setMultiSelect(false);
            if (value instanceof java.lang.String) {
                field.setValue(value);
            } else {
                field.setValue(dfd.getInitialValue());
            }
        } else {
            if (dfd.getDataTypeClassName().equals("java.lang.Long")) {
                field = new TextField(dfd.getLabel());
                if (value != null) {
                    field.setValue(new Long(value.toString()));
                }
//                    field.addValidator(new DoubleValidator("Значение должно быть цифровым"));
            } else if (dfd.getDataTypeClassName().equals("java.lang.Double")) {
                field = new TextField(dfd.getLabel());
                if (value != null) {
                    field.setValue(new Double(value.toString()));
                }
//                    field.addValidator(new DoubleValidator("Значение должно быть цифровым"));
            } else if (dfd.getDataTypeClassName().equals("java.util.Date")) {
                field = new PopupDateField(dfd.getLabel());
                if (value != null && value instanceof java.util.Date) {
                    field.setValue(value);
                } else {
                    field.setValue(new java.util.Date());
                }
                ((PopupDateField) field).setResolution(PopupDateField.RESOLUTION_DAY);
            } else if (dfd.getDataTypeClassName().equals("java.lang.String")) {
                field = new TextField(dfd.getLabel());
                field.setValue(value != null ? value.toString() : "");
            } else if (dfd.getDataTypeClassName().equals("java.lang.Boolean")) {
                field = new CheckBox(dfd.getLabel());
                field.setValue(value != null ? value : Boolean.FALSE);
            } else {
                field = new TextField(dfd.getLabel());
                field.setValue(value != null ? value.toString() : "");
            }
        }
        field.setDescription(dfd.getDescription() != null ? dfd.getDescription() : "");

        field.setWidth("300px");
        variablesLayout.addComponent(field);
    }

    public void addParticipantInfo() throws ParticipantNotFoundException, ProcessNotFoundException, ActivityNotFoundException, ProcessNotFoundException, Exception {
        participantLayout.removeAllComponents();
        Panel participantDefinitionPanel = new Panel(messages.getString("taskPerformerDefinition"), new FormLayout());
        ((FormLayout) participantDefinitionPanel.getContent()).setMargin(true);
        ((FormLayout) participantDefinitionPanel.getContent()).setSpacing(true);
        participantDefinitionPanel.setWidth("100%");
        Set<String> participantNames = bpmModule.getProcessActivity(task.getProcessDefinitionUUID(), task.getActivityName()).getPerformers();//        ParticipantDefinition participantDefinition = bpmModule.getProcessParticipant(processDefinition.getProcessDefinitionUUID(), participantName);
        TextField participantTypeField = new TextField(messages.getString("taskParticipantType"), participantNames.toString());
        participantTypeField.setWidth("300px");
        participantDefinitionPanel.addComponent(participantTypeField);

        // Participant Form
        Panel participantPanel = new Panel(messages.getString("taskPerformer"), new FormLayout());
        ((FormLayout) participantPanel.getContent()).setMargin(true);
        ((FormLayout) participantPanel.getContent()).setSpacing(true);
        participantPanel.setWidth("100%");
        if (task.getTaskCandidates() != null && task.getTaskCandidates().size() > 0) {
            for (String candidate : task.getTaskCandidates()) {
                candidatesField.addItem(candidate);
            }
        }
        candidatesField.setNullSelectionAllowed(false);
        candidatesField.setWidth("300px");
        candidatesField.setRows(6);

        Button isAssignedBtn = new Button(messages.getString("taskIsAssigned"));
        isAssignedBtn.setSwitchMode(true);
        isAssignedBtn.setValue(task.isTaskAssigned());

        TextField taskUserField = new TextField(messages.getString("taskUser"));
        if (task.isTaskAssigned()) {
            taskUserField.setValue(task.getTaskUser());
        }
        taskUserField.setWidth("300px");
        taskUserField.setNullRepresentation("");
        participantPanel.addComponent(candidatesField);
        participantPanel.addComponent(isAssignedBtn);
        participantPanel.addComponent(taskUserField);

        participantLayout.addComponent(participantDefinitionPanel);
        participantLayout.addComponent(participantPanel);

        assignUpdatesTable.removeAllItems();
        assignUpdatesTable.addContainerProperty("UpdatedDate", Date.class, null, messages.getString("taskUpdatedDate"), null, null);
//        assignUpdatesTable.addGeneratedColumn("UpdatedDate", new PbColumnGenerator());
        assignUpdatesTable.addContainerProperty("UpdatedBy", String.class, null, messages.getString("taskUpdatedBy"), null, null);
        assignUpdatesTable.setColumnWidth("UpdatedBy", 150);
        assignUpdatesTable.addContainerProperty("Candidates", String.class, null, messages.getString("taskCandidates"), null, null);
        assignUpdatesTable.setColumnWidth("Candidates", 300);
        assignUpdatesTable.addContainerProperty("AssignedUserId", String.class, null, messages.getString("taskAssignedUserId"), null, null);
        assignUpdatesTable.setColumnWidth("AssignedUserId", 150);
        for (AssignUpdate assignUpdate : task.getAssignUpdates()) {
            Item woItem = assignUpdatesTable.addItem(assignUpdate);
            woItem.getItemProperty("UpdatedDate").setValue(assignUpdate.getUpdatedDate());
            woItem.getItemProperty("UpdatedBy").setValue(assignUpdate.getUpdatedBy());
            woItem.getItemProperty("AssignedUserId").setValue(assignUpdate.getAssignedUserId());
            woItem.getItemProperty("Candidates").setValue(assignUpdate.getCandidates());
        }
        assignUpdatesTable.setSortContainerPropertyId("UpdatedDate");
        assignUpdatesTable.setSortAscending(false);
        assignUpdatesTable.sort();
        assignUpdatesTable.setPageLength(7);
        assignUpdatesTable.setCaption(messages.getString("taskAssignUpdates"));
        assignUpdatesTable.setSizeFull();
        participantLayout.addComponent(assignUpdatesTable);
        participantLayout.setWidth("100%");
    }

    public void addStateInfo() {
        stateLayout.removeAllComponents();
        stateUpdatesTable.removeAllItems();
        stateUpdatesTable.addContainerProperty("UpdatedDate", Date.class, null, messages.getString("taskUpdatedDate"), null, null);
        stateUpdatesTable.addContainerProperty("UpdatedBy", String.class, null, messages.getString("taskUpdatedBy"), null, null);
        stateUpdatesTable.setColumnWidth("UpdatedBy", 150);
        stateUpdatesTable.addContainerProperty("InitialState", String.class, null, messages.getString("taskInitialState"), null, null);
        stateUpdatesTable.setColumnWidth("InitialState", 150);
        stateUpdatesTable.addContainerProperty("ActivityState", String.class, null, messages.getString("taskActivityState"), null, null);
        stateUpdatesTable.setColumnWidth("ActivityState", 150);
        for (StateUpdate stateUpdate : task.getStateUpdates()) {
            Item woItem = stateUpdatesTable.addItem(stateUpdate);
            woItem.getItemProperty("UpdatedDate").setValue(stateUpdate.getUpdatedDate());
            woItem.getItemProperty("UpdatedBy").setValue(stateUpdate.getUpdatedBy());
            woItem.getItemProperty("InitialState").setValue(stateUpdate.getInitialState().toString());
            woItem.getItemProperty("ActivityState").setValue(stateUpdate.getActivityState().toString());
        }
        stateUpdatesTable.setSortContainerPropertyId("UpdatedDate");
        stateUpdatesTable.setSortAscending(false);
        stateUpdatesTable.sort();
        stateUpdatesTable.setPageLength(5);
        stateUpdatesTable.setCaption(messages.getString("taskAssignUpdates"));
        stateLayout.addComponent(stateUpdatesTable);
        stateUpdatesTable.setSizeFull();
        stateLayout.setWidth("100%");
    }

    public DataFieldDefinition getDataFieldDefinition(String name) {
        for (DataFieldDefinition dfd : dfds) {
            if (dfd.getName().equalsIgnoreCase(name)) {
                return dfd;
            }
        }
        return null;
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(closeBtn)) {
            close();
        } else {
            try {
                if (event.getButton().equals(unAssignBtn)) {
                    bpmModule.unassignTask(task.getUUID());
                } else if (event.getButton().equals(reassignBtn)) {
                    bpmModule.assignTask(task.getUUID());
                } else if (event.getButton().equals(assignBtn) && candidatesField.getValue() != null) {
                    bpmModule.assignTask(task.getUUID(), candidatesField.getValue().toString());
                }
                this.task = bpmModule.getTaskInstance(task.getUUID());
                addParticipantInfo();
                addStateInfo();
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.getMessage());
            }
        }
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        if (event.getTabSheet().getSelectedTab().equals(participantLayout) &&
                (task.getState().equals(ActivityState.READY) || task.getState().equals(ActivityState.EXECUTING) || task.getState().equals(ActivityState.SUSPENDED))) {
            buttons.addComponent(unAssignBtn, 0);
            buttons.addComponent(reassignBtn, 0);
            buttons.addComponent(assignBtn, 0);
        } else {
            buttons.removeComponent(reassignBtn);
            buttons.removeComponent(assignBtn);
            buttons.removeComponent(unAssignBtn);
        }
    }
}
