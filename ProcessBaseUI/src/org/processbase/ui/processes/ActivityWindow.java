/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.processes;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.bpm.AdminModule;
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
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.runtime.var.Enumeration;

/**
 *
 * @author mgubaidullin
 */
public class ActivityWindow extends PbWindow implements ClickListener {

    VerticalLayout layout = (VerticalLayout) this.getContent();
    HorizontalLayout hlayout = new HorizontalLayout();
    public ProcessDefinition processDefinition = null;
    public ActivityInstance<TaskInstance> task = null;
    public Map<String, Object> processVars = new HashMap<String, Object>();
    public Set<DataFieldDefinition> dfds = null;
    public AdminModule adminModule = new AdminModule();
    public HorizontalLayout buttons = new HorizontalLayout();
    public Button cancelBtn = new Button(messages.getString("btnCancel"), this);
    public Button applyBtn = new Button(messages.getString("btnOK"), this);
    public Button reassignBtn = new Button(messages.getString("btnReassign"), this);
    private Form variablesForm = new Form();
    private Panel variablesPanel = new Panel(messages.getString("processVariables"));
    private Panel participantPanel = new Panel(messages.getString("taskPerformer"));

    public ActivityWindow(ProcessDefinition pd, ActivityInstance<TaskInstance> t) {
        super();
        try {
            setActivityInfo(pd, t);
            exec();
        } catch (Exception ex) {
            Logger.getLogger(ActivityWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
    }

    public void setActivityInfo(ProcessDefinition pd, ActivityInstance<TaskInstance> t) throws ProcessNotFoundException, InstanceNotFoundException {
        this.processDefinition = pd;
        this.task = t;
        dfds = adminModule.getProcessDataFields(task.getProcessDefinitionUUID());
        processVars = adminModule.getProcessInstanceVariables(task.getProcessInstanceUUID());
    }

    public void exec() {
        try {
            setCaption(messages.getString("defaultTaskWindowCaption2") + " " + task.getActivityId());
            setModal(true);
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setSizeUndefined();
            buttons.setSpacing(true);
            buttons.addComponent(reassignBtn);
            buttons.addComponent(cancelBtn);
            hlayout.setSpacing(true);
            addVariablesInfo();
            hlayout.addComponent(variablesPanel);
            addParticipantInfo();
            hlayout.addComponent(participantPanel);
            layout.addComponent(hlayout);
            layout.addComponent(buttons);
            setResizable(false);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }

    }

    public void addVariablesInfo() throws ProcessNotFoundException, VariableNotFoundException, InstanceNotFoundException, ProcessNotFoundException, ActivityNotFoundException, ParticipantNotFoundException {
        variablesForm.setWriteThrough(false); // we want explicit 'apply'
        variablesForm.setInvalidCommitted(false);
        dfds = adminModule.getProcessDataFields(task.getProcessDefinitionUUID());
        processVars = adminModule.getProcessInstanceVariables(task.getProcessInstanceUUID());
        for (DataFieldDefinition dfd : dfds) {
            addField(variablesForm, dfd, processVars.get(dfd.getDataFieldId()));
        }
        variablesPanel.addComponent(variablesForm);
    }

    public void addField(Form form, DataFieldDefinition dfd, Object value) {
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
        form.addField(fieldId, field);
    }

    public void addParticipantInfo() throws ParticipantNotFoundException, ProcessNotFoundException, ActivityNotFoundException {
        participantPanel.removeAllComponents();
        String participantName = adminModule.getProcessActivity(processDefinition.getProcessDefinitionUUID(), task.getActivityId()).getPerformer();
        ParticipantDefinition participantDefinition = adminModule.getProcessParticipant(processDefinition.getProcessDefinitionUUID(), participantName);
        Label performerField = new Label(participantDefinition.getParticipantType().name() + " = <b>" + participantDefinition.getName() + "</b>");
        performerField.setContentMode(Label.CONTENT_XHTML);
        performerField.setWidth("300px");
        TextField candidatesField = new TextField(messages.getString("candidates"));
        candidatesField.setValue(task.getBody().getTaskCandidates() != null ? task.getBody().getTaskCandidates().toString() : "");
        candidatesField.setRows(2);
        candidatesField.setWidth("300px");
        TextField starterField = new TextField(messages.getString("tableCaptionTaskUser"), task.getBody().getStartedBy());
        starterField.setWidth("300px");
        starterField.setNullRepresentation("");

        participantPanel.setWidth("350px");
        participantPanel.setReadOnly(true);
        participantPanel.addComponent(performerField);
        participantPanel.addComponent(candidatesField);
        participantPanel.addComponent(starterField);
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
        if (event.getButton().equals(cancelBtn)) {
            close();
        } else if (event.getButton().equals(applyBtn)) {
            close();
        } else if (event.getButton().equals(reassignBtn)) {
            try {
                adminModule.unassignTask(task.getBody().getUUID());
                adminModule.assignTask(task.getBody().getUUID());
                addParticipantInfo();
            } catch (Exception ex) {
                Logger.getLogger(ActivityWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
                showError(ex.getMessage());
            }
        }
    }
}
