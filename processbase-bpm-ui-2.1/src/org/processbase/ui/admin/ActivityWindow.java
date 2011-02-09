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

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import com.vaadin.ui.themes.Reindeer;
import java.util.Date;
import java.util.Set;
import org.processbase.ui.template.PbWindow;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ParticipantNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.AssignUpdate;
import org.ow2.bonita.facade.runtime.StateUpdate;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.light.LightActivityInstance;
import org.processbase.ui.portlet.PbPortlet;
import org.processbase.ui.template.PbColumnGenerator;

/**
 *
 * @author mgubaidullin
 */
public class ActivityWindow extends PbWindow implements ClickListener, TabSheet.SelectedTabChangeListener {

    private VerticalSplitPanel layout = new VerticalSplitPanel();
    public ProcessDefinition processDefinition = null;
    private TaskInstance task = null;
    private LightActivityInstance lightActivity = null;
    private Set<DataFieldDefinition> dfds = null;
    private HorizontalLayout buttons = new HorizontalLayout();
    private Button closeBtn = new Button(PbPortlet.getCurrent().messages.getString("btnClose"), this);
    private Button reassignBtn = new Button(PbPortlet.getCurrent().messages.getString("btnReassign"), this);
    private CheckBox unAssignBtn = new CheckBox(PbPortlet.getCurrent().messages.getString("btnUnassign"), this);
    private Button assignBtn = new Button(PbPortlet.getCurrent().messages.getString("btnAssign"), this);
    private ListSelect candidatesList = new ListSelect(PbPortlet.getCurrent().messages.getString("candidates"));
    private ListSelect groupList = new ListSelect(PbPortlet.getCurrent().messages.getString("actorsGroups"));
    private TabSheet tabSheet = new TabSheet();
    private VerticalLayout variablesLayout = new VerticalLayout();
    private GridLayout participantLayout = new GridLayout(4, 4);
    private VerticalLayout stateLayout = new VerticalLayout();
    protected Table assignUpdatesTable = new Table();
    protected Table stateUpdatesTable = new Table();
    protected Table variablesTable = new Table(PbPortlet.getCurrent().messages.getString("processVariables"));

    public ActivityWindow(LightActivityInstance lightActivity) {
        super(lightActivity.getActivityLabel());
        try {
            this.lightActivity = lightActivity;
            this.processDefinition = PbPortlet.getCurrent().bpmModule.getProcessDefinition(lightActivity.getProcessDefinitionUUID());
            if (lightActivity.isTask()) {
                task = PbPortlet.getCurrent().bpmModule.getTaskInstance(lightActivity.getUUID());
            }
            this.dfds = PbPortlet.getCurrent().bpmModule.getProcessDataFields(lightActivity.getProcessDefinitionUUID());
            exec();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void exec() throws ParticipantNotFoundException, ProcessNotFoundException, VariableNotFoundException, InstanceNotFoundException, ActivityNotFoundException, Exception {
        this.setContent(layout);
        setCaption(PbPortlet.getCurrent().messages.getString("defaultTaskWindowCaption2") + " " + lightActivity.getActivityName());
        tabSheet.addListener((TabSheet.SelectedTabChangeListener) this);
        tabSheet.setSizeFull();
        if (task != null) {
            addParticipantInfo();
            participantLayout.setSizeFull();
            tabSheet.addTab(participantLayout, PbPortlet.getCurrent().messages.getString("taskPerformer"), null);
        }
        try {
            addVariablesInfo();
            variablesLayout.setSizeFull();
            tabSheet.addTab(variablesLayout, PbPortlet.getCurrent().messages.getString("processVariables"), null);
            addStateInfo();
            stateLayout.setSizeFull();
            tabSheet.addTab(stateLayout, PbPortlet.getCurrent().messages.getString("taskStateUpdates"), null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);

        buttons.setSpacing(true);
        buttons.setMargin(true);
        buttons.addComponent(closeBtn);
        buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
        buttons.setWidth("100%");

        VerticalLayout vl = new VerticalLayout();
        vl.setMargin(true);
        vl.addComponent(tabSheet);
        layout.setFirstComponent(vl);
        layout.setSplitPosition(87);
        layout.setMargin(false);
        layout.setSecondComponent(buttons);
        layout.setStyleName(Reindeer.SPLITPANEL_SMALL);

        setWidth("800px");
        setHeight("600px");
        setModal(true);
        setResizable(false);
    }

    public void addVariablesInfo() throws ProcessNotFoundException, VariableNotFoundException, InstanceNotFoundException, ProcessNotFoundException, ActivityNotFoundException, ParticipantNotFoundException, Exception {
        variablesTable.addContainerProperty("name", String.class, null, PbPortlet.getCurrent().messages.getString("variableName"), null, null);
        variablesTable.addContainerProperty("label", String.class, null, PbPortlet.getCurrent().messages.getString("variableLabel"), null, null);
        variablesTable.addContainerProperty("type", String.class, null, PbPortlet.getCurrent().messages.getString("variableType"), null, null);
        variablesTable.addContainerProperty("value", Field.class, null, PbPortlet.getCurrent().messages.getString("variableValue"), null, null);
        
        variablesTable.addContainerProperty("description", String.class, null, PbPortlet.getCurrent().messages.getString("variableDesc"), null, null);
        
        variablesTable.setPageLength(15);
        variablesTable.setSizeFull();
        variablesTable.setWidth("100%");
        variablesLayout.addComponent(variablesTable);
        for (DataFieldDefinition dfd : dfds) {
            Object value = null;
            try {
                value = PbPortlet.getCurrent().bpmModule.getProcessInstanceVariable(lightActivity.getProcessInstanceUUID(), dfd.getName());
            } catch (Exception ex) {
                value = "MAYBE CUSTOM CLASS VALUE";
            }
            addField(dfd, value);
        }
        variablesTable.setReadOnly(true);
    }

    public void addField(DataFieldDefinition dfd, Object value) {
        Field field = null;
        if (dfd.isEnumeration()) {
            field = new ComboBox(dfd.getName(), dfd.getEnumerationValues());
            ((ComboBox) field).setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
            ((ComboBox) field).setMultiSelect(false);
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

        Item woItem = variablesTable.addItem(dfd);
        woItem.getItemProperty("name").setValue(dfd.getName());
        woItem.getItemProperty("label").setValue(dfd.getLabel());
        woItem.getItemProperty("type").setValue(dfd.getDataTypeClassName());
        woItem.getItemProperty("value").setValue(field);
    }

    public void addParticipantInfo() throws ParticipantNotFoundException, ProcessNotFoundException, ActivityNotFoundException, ProcessNotFoundException, Exception {
        participantLayout.removeAllComponents();
        Set<String> groupNames = PbPortlet.getCurrent().bpmModule.getProcessActivity(task.getProcessDefinitionUUID(), task.getActivityName()).getPerformers();//        ParticipantDefinition participantDefinition = bpmModule.getProcessParticipant(processDefinition.getProcessDefinitionUUID(), participantName);
        for (String name : groupNames) {
            groupList.addItem(name);
        }
//        groupList.setWidth("200px");
        groupList.setNullSelectionAllowed(false);
        groupList.setRows(6);

        // Participant Form
        if (task.getTaskCandidates() != null && task.getTaskCandidates().size() > 0) {
            for (String candidate : task.getTaskCandidates()) {
                candidatesList.addItem(candidate);
            }
        }
        candidatesList.setNullSelectionAllowed(false);
        candidatesList.setWidth("100%");
        candidatesList.setRows(6);

        unAssignBtn.setValue(task.isTaskAssigned());

        TextField taskUserField = new TextField(PbPortlet.getCurrent().messages.getString("taskAssignedBy"));
        if (task.isTaskAssigned()) {
            taskUserField.setValue(task.getTaskUser());
        }
//        taskUserField.setWidth("");
        taskUserField.setNullRepresentation("");
        taskUserField.setEnabled(false);
        taskUserField.setSizeFull();

        participantLayout.addComponent(groupList, 0, 0, 0, 1);
        participantLayout.addComponent(candidatesList, 1, 0, 2, 1);


        participantLayout.addComponent(reassignBtn, 1, 2, 1, 2);
        participantLayout.setComponentAlignment(reassignBtn, Alignment.MIDDLE_LEFT);

        participantLayout.addComponent(assignBtn, 2, 2, 2, 2);
        participantLayout.setComponentAlignment(assignBtn, Alignment.MIDDLE_RIGHT);


        participantLayout.addComponent(taskUserField, 3, 0, 3, 0);
        participantLayout.addComponent(unAssignBtn, 3, 1, 3, 1);

        assignUpdatesTable.removeAllItems();
        assignUpdatesTable.removeGeneratedColumn("UpdatedDate");
        assignUpdatesTable.addContainerProperty("UpdatedDate", Date.class, null, PbPortlet.getCurrent().messages.getString("taskUpdatedDate"), null, null);
        assignUpdatesTable.addGeneratedColumn("UpdatedDate", new PbColumnGenerator());
        assignUpdatesTable.addContainerProperty("UpdatedBy", String.class, null, PbPortlet.getCurrent().messages.getString("taskUpdatedBy"), null, null);
        assignUpdatesTable.addContainerProperty("Candidates", String.class, null, PbPortlet.getCurrent().messages.getString("taskCandidates"), null, null);
        assignUpdatesTable.addContainerProperty("AssignedUserId", String.class, null, PbPortlet.getCurrent().messages.getString("taskAssignedBy"), null, null);
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
        assignUpdatesTable.setPageLength(8);
        assignUpdatesTable.setCaption(PbPortlet.getCurrent().messages.getString("taskAssignUpdates"));
        assignUpdatesTable.setSizeFull();
        participantLayout.addComponent(assignUpdatesTable, 0, 3, 3, 3);
        participantLayout.setWidth("100%");
        participantLayout.setSpacing(true);
        participantLayout.setMargin(true, false, false, false);
    }

    public void addStateInfo() {
        stateLayout.removeAllComponents();
        stateUpdatesTable.removeAllItems();
        stateUpdatesTable.removeGeneratedColumn("UpdatedDate");
        stateUpdatesTable.addContainerProperty("UpdatedDate", Date.class, null, PbPortlet.getCurrent().messages.getString("taskUpdatedDate"), null, null);
        stateUpdatesTable.addGeneratedColumn("UpdatedDate", new PbColumnGenerator());
        stateUpdatesTable.addContainerProperty("UpdatedBy", String.class, null, PbPortlet.getCurrent().messages.getString("taskUpdatedBy"), null, null);
        stateUpdatesTable.setColumnWidth("UpdatedBy", 150);
        stateUpdatesTable.addContainerProperty("InitialState", String.class, null, PbPortlet.getCurrent().messages.getString("taskInitialState"), null, null);
        stateUpdatesTable.setColumnWidth("InitialState", 150);
        stateUpdatesTable.addContainerProperty("ActivityState", String.class, null, PbPortlet.getCurrent().messages.getString("taskActivityState"), null, null);
        stateUpdatesTable.setColumnWidth("ActivityState", 150);
        if (task != null) {
            for (StateUpdate stateUpdate : task.getStateUpdates()) {
                Item woItem = stateUpdatesTable.addItem(stateUpdate);
                woItem.getItemProperty("UpdatedDate").setValue(stateUpdate.getUpdatedDate());
                woItem.getItemProperty("UpdatedBy").setValue(stateUpdate.getUpdatedBy());
                woItem.getItemProperty("InitialState").setValue(stateUpdate.getInitialState().toString());
                woItem.getItemProperty("ActivityState").setValue(stateUpdate.getActivityState().toString());
            }
        }
        stateUpdatesTable.setSortContainerPropertyId("UpdatedDate");
        stateUpdatesTable.setSortAscending(false);
        stateUpdatesTable.sort();
        stateUpdatesTable.setPageLength(5);
        stateUpdatesTable.setCaption(PbPortlet.getCurrent().messages.getString("taskAssignUpdates"));
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
                if (event.getButton().equals(unAssignBtn) && !unAssignBtn.booleanValue()) {
                    PbPortlet.getCurrent().bpmModule.unassignTask(task.getUUID());
                } else if (event.getButton().equals(reassignBtn)) {
                    PbPortlet.getCurrent().bpmModule.assignTask(task.getUUID());
                } else if (event.getButton().equals(assignBtn) && candidatesList.getValue() != null) {
                    PbPortlet.getCurrent().bpmModule.assignTask(task.getUUID(), candidatesList.getValue().toString());
                }
                this.task = PbPortlet.getCurrent().bpmModule.getTaskInstance(task.getUUID());
                addParticipantInfo();
                addStateInfo();
            } catch (Exception ex) {
                ex.printStackTrace();
                showError(ex.getMessage());
            }
        }
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
    }
}
