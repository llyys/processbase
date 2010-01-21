/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.ui.template.PbWindow;
import org.ow2.bonita.facade.def.dataType.BasicTypeDefinition;
import org.ow2.bonita.facade.def.dataType.DataTypeDefinition;
import org.ow2.bonita.facade.def.dataType.EnumerationTypeDefinition;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.ParticipantDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ParticipantNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityBody;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.AssignUpdate;
import org.ow2.bonita.facade.runtime.StateUpdate;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.runtime.var.Enumeration;
import org.processbase.ProcessBase;
import org.processbase.bpm.BPMModule;

/**
 *
 * @author mgubaidullin
 */
public class ActivityWindow extends PbWindow implements ClickListener, TabSheet.SelectedTabChangeListener {

    private VerticalLayout layout = (VerticalLayout) this.getContent();
    public ProcessDefinition processDefinition = null;
    private ActivityInstance<TaskInstance> task = null;
    private ActivityInstance<ActivityBody> activity = null;
    private Map<String, Object> processVars = new HashMap<String, Object>();
    private Set<DataFieldDefinition> dfds = null;
    protected BPMModule bpmModule = ((ProcessBase) getApplication()).getCurrent().getBpmModule();
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

    public ActivityWindow(ProcessDefinition pd, ActivityInstance<ActivityBody> activity, ActivityInstance<TaskInstance> task) {
        super();
        try {
            this.processDefinition = pd;
            this.task = task;
            this.activity = activity;
            this.dfds = bpmModule.getProcessDataFields(activity.getProcessDefinitionUUID());
            this.processVars = bpmModule.getProcessInstanceVariables(activity.getProcessInstanceUUID());
            exec();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void exec() throws ParticipantNotFoundException, ProcessNotFoundException, VariableNotFoundException, InstanceNotFoundException, ActivityNotFoundException, Exception {
        setCaption(messages.getString("defaultTaskWindowCaption2") + " " + activity.getActivityId());
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setSizeUndefined();
        buttons.setSpacing(true);
        buttons.addComponent(closeBtn);
        buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
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
        tabSheet.setStyleName("minimal");
        layout.addComponent(tabSheet);
        buttons.setSizeFull();
        layout.addComponent(buttons);
        layout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
        setModal(true);
        setResizable(false);
    }

    public void addVariablesInfo() throws ProcessNotFoundException, VariableNotFoundException, InstanceNotFoundException, ProcessNotFoundException, ActivityNotFoundException, ParticipantNotFoundException, Exception {
        for (DataFieldDefinition dfd : dfds) {
            addField(dfd, processVars.get(dfd.getDataFieldId()));
        }
    }

    public void addField(DataFieldDefinition dfd, Object value) {
        String fieldId = dfd.getDataFieldId().toString();
        String fieldType = null;
        Field field = null;
        if (dfd.getDataType().getType().compareTo(DataTypeDefinition.Type.BasicType) == 0) {
            BasicTypeDefinition btd = ((BasicTypeDefinition) dfd.getDataType().getValue());
            fieldType = btd.getType().toString();
            if (fieldType.equalsIgnoreCase("INTEGER")) {
                field = new TextField(fieldId);
                if (value != null) {
                    field.setValue(new Long(value.toString()));
                }
//                    field.addValidator(new DoubleValidator("Значение должно быть цифровым"));
            } else if (fieldType.equalsIgnoreCase("FLOAT")) {
                field = new TextField(fieldId);
                if (value != null) {
                    field.setValue(new Double(value.toString()));
                }
//                    field.addValidator(new DoubleValidator("Значение должно быть цифровым"));
            } else if (fieldType.equalsIgnoreCase("DATETIME")) {
                field = new PopupDateField(fieldId);
                if (value != null && value instanceof java.util.Date) {
                    field.setValue(value);
                } else {
                    field.setValue(new java.util.Date());
                }
                ((PopupDateField) field).setResolution(PopupDateField.RESOLUTION_DAY);
            } else if (fieldType.equalsIgnoreCase("STRING")) {
                field = new TextField(fieldId);
                field.setValue(value != null ? value.toString() : "");
            }
        } else if (dfd.getDataType().getType().compareTo(DataTypeDefinition.Type.EnumerationType) == 0) {
            EnumerationTypeDefinition etd = ((EnumerationTypeDefinition) dfd.getDataType().getValue());
            field = new Select(fieldId, etd.getEnumerationValues());
            ((Select) field).setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
            ((Select) field).setMultiSelect(false);
            if (value instanceof java.lang.String) {
                field.setValue(value);
            } else {
                field.setValue(((Enumeration) value).getSelectedValue());
            }
        }
        field.setDescription(dfd.getDescription() != null ? dfd.getDescription() : "");
        field.setWidth("300px");
        variablesLayout.addComponent(field);
    }

    public void addParticipantInfo() throws ParticipantNotFoundException, ProcessNotFoundException, ActivityNotFoundException, ProcessNotFoundException, Exception {
        participantLayout.removeAllComponents();
        // Participant Definition Form
        Panel participantDefinitionPanel = new Panel(messages.getString("taskPerformerDefinition"), new FormLayout());
        ((FormLayout) participantDefinitionPanel.getContent()).setMargin(true);
        ((FormLayout) participantDefinitionPanel.getContent()).setSpacing(true);
        participantDefinitionPanel.setWidth("100%");
        String participantName = bpmModule.getProcessActivity(processDefinition.getProcessDefinitionUUID(), task.getActivityId()).getPerformer();
        ParticipantDefinition participantDefinition = bpmModule.getProcessParticipant(processDefinition.getProcessDefinitionUUID(), participantName);
        TextField participantTypeField = new TextField(messages.getString("taskParticipantType"), participantDefinition.getParticipantType().name());
        participantTypeField.setWidth("300px");
        TextField participantNameField = new TextField(messages.getString("taskParticipantName"), participantDefinition.getName());
        participantNameField.setWidth("300px");
        participantDefinitionPanel.addComponent(participantTypeField);
        participantDefinitionPanel.addComponent(participantNameField);

        // Participant Form
        Panel participantPanel = new Panel(messages.getString("taskPerformer"), new FormLayout());
        ((FormLayout) participantPanel.getContent()).setMargin(true);
        ((FormLayout) participantPanel.getContent()).setSpacing(true);
        participantPanel.setWidth("100%");
        if (task.getBody().getTaskCandidates() != null && task.getBody().getTaskCandidates().size() > 0) {
            for (String candidate : task.getBody().getTaskCandidates()) {
                candidatesField.addItem(candidate);
            }
        }
        candidatesField.setNullSelectionAllowed(false);
        candidatesField.setWidth("300px");

        Button isAssignedBtn = new Button(messages.getString("taskIsAssigned"));
        isAssignedBtn.setSwitchMode(true);
        isAssignedBtn.setValue(task.getBody().isTaskAssigned());

        TextField taskUserField = new TextField(messages.getString("taskUser"));
        if (task.getBody().isTaskAssigned()) {
            taskUserField.setValue(task.getBody().getTaskUser());
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
        assignUpdatesTable.addContainerProperty("UpdatedBy", String.class, null, messages.getString("taskUpdatedBy"), null, null);
        assignUpdatesTable.setColumnWidth("UpdatedBy", 150);
        assignUpdatesTable.addContainerProperty("Candidates", String.class, null, messages.getString("taskCandidates"), null, null);
        assignUpdatesTable.setColumnWidth("Candidates", 300);
        assignUpdatesTable.addContainerProperty("AssignedUserId", String.class, null, messages.getString("taskAssignedUserId"), null, null);
        assignUpdatesTable.setColumnWidth("AssignedUserId", 150);
        for (AssignUpdate assignUpdate : task.getBody().getAssignUpdates()) {
            Item woItem = assignUpdatesTable.addItem(assignUpdate);
            woItem.getItemProperty("UpdatedDate").setValue(assignUpdate.getUpdatedDate());
            woItem.getItemProperty("UpdatedBy").setValue(assignUpdate.getUpdatedBy());
            woItem.getItemProperty("AssignedUserId").setValue(assignUpdate.getAssignedUserId());
            woItem.getItemProperty("Candidates").setValue(assignUpdate.getCandidates());
        }
        assignUpdatesTable.setSortContainerPropertyId("UpdatedDate");
        assignUpdatesTable.setSortAscending(false);
        assignUpdatesTable.sort();
        assignUpdatesTable.setPageLength(5);
        assignUpdatesTable.setCaption(messages.getString("taskAssignUpdates"));
        participantLayout.addComponent(assignUpdatesTable);
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
        for (StateUpdate stateUpdate : task.getBody().getStateUpdates()) {
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
    }

    public DataFieldDefinition getDataFieldDefinition(String name) {
        for (DataFieldDefinition dfd : dfds) {
            if (dfd.getDataFieldId().toString().equalsIgnoreCase(name)) {
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
                    bpmModule.unassignTask(task.getBody().getUUID());
                } else if (event.getButton().equals(reassignBtn)) {
                    bpmModule.assignTask(task.getBody().getUUID());
                } else if (event.getButton().equals(assignBtn) && candidatesField.getValue() != null) {
                    bpmModule.assignTask(task.getBody().getUUID(), candidatesField.getValue().toString());
                }
                this.task = bpmModule.getActivityInstance(task.getUUID());
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
                (task.getBody().getState().equals(ActivityState.READY) || task.getBody().getState().equals(ActivityState.EXECUTING) || task.getBody().getState().equals(ActivityState.SUSPENDED))) {
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
