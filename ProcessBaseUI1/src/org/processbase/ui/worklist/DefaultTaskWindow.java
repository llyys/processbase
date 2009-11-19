/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.worklist;

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.ui.template.TaskWindow;
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
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

/**
 *
 * @author mgubaidullin
 */
public class DefaultTaskWindow extends TaskWindow implements Button.ClickListener {

    private Form form = new Form();
    
    public DefaultTaskWindow(ProcessDefinition pd, ActivityInstance<TaskInstance> t) {
        super();
        this.processDefinition = pd;
        this.task = t;
        if (task != null) {
            isNew = Boolean.FALSE;
        }
    }

    @Override
    public void exec() {
        try {
            if (this.isNew) {
                setCaption(messages.getString("defaultTaskWindowCaption1") + " " + (processDefinition.getDescription() != null ? this.processDefinition.getDescription() : processDefinition.getProcessId()));
                attachmentBar.newUpload();
            } else {
                String taskName = bpmModule.getProcessActivity(task.getProcessDefinitionUUID(), task.getActivityId()).getDescription();
                setCaption(messages.getString("defaultTaskWindowCaption2") + " " + (taskName != null ? taskName : task.getActivityId()));
                attachmentBar.load(task.getProcessInstanceUUID().toString(), task.getUUID().toString());
            }
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setSizeUndefined();
            buttons.setSpacing(true);
            applyBtn.addListener(this);
            cancelBtn.addListener(this);
            buttons.addComponent(applyBtn);
            buttons.addComponent(cancelBtn);
            createForm();
            attachmentBar.setSizeFull();
            layout.addComponent(attachmentBar);
            form.getLayout().addComponent(buttons);
            setResizable(false);
        } catch (Exception ex) {
            showError(ex.getMessage());
        }

    }

    public void createForm() throws ProcessNotFoundException, VariableNotFoundException, InstanceNotFoundException, ProcessNotFoundException, ActivityNotFoundException, ParticipantNotFoundException, Exception {
        form.setWriteThrough(false); // we want explicit 'apply'
        form.setInvalidCommitted(false);
        if (this.isNew) {
            dfds = bpmModule.getProcessDataFields(processDefinition.getProcessDefinitionUUID());
            for (DataFieldDefinition dfd : dfds) {
                addField(dfd, dfd.getInitialValue());
            }
        } else {
            dfds = bpmModule.getProcessDataFields(task.getProcessDefinitionUUID());
            processVars = bpmModule.getProcessInstanceVariables(task.getProcessInstanceUUID(),false);
            for (DataFieldDefinition dfd : dfds) {
                addField(dfd, processVars.get(dfd.getDataFieldId()));
            }
            addParticipantInfo();
        }
        addComponent(form);
    }

    public void apply() {
        try {
            form.commit();
            for (DataFieldDefinition dfd : dfds) {
                String fieldId = dfd.getDataFieldId().toString();
                String fieldType = null;
                Object fieldValue = null;
                if (dfd.getDataType().getType().compareTo(DataTypeDefinition.Type.BasicType) == 0) {
                    BasicTypeDefinition btd = ((BasicTypeDefinition) dfd.getDataType().getValue());
                    fieldType = btd.getType().toString();
                    fieldValue = null;
                    if (fieldType.equalsIgnoreCase("INTEGER")) {
                        fieldValue = new Long(form.getField(fieldId).getValue().toString());
                    } else if (fieldType.equalsIgnoreCase("FLOAT")) {
                        fieldValue = new Double(form.getField(fieldId).getValue().toString());
                    } else if (fieldType.equalsIgnoreCase("DATETIME")) {
                        fieldValue = ((PopupDateField) form.getField(fieldId)).getValue();
                    } else if (fieldType.equalsIgnoreCase("STRING")) {
                        fieldValue = form.getField(fieldId).getValue().toString();
                    }
                } else if (dfd.getDataType().getType().compareTo(DataTypeDefinition.Type.EnumerationType) == 0) {
                    EnumerationTypeDefinition etd = ((EnumerationTypeDefinition) dfd.getDataType().getValue());
                    fieldValue = new Enumeration(etd.getEnumerationValues(), form.getField(fieldId).getValue().toString());
                }
                processVars.put(fieldId, fieldValue);
            }
            if (this.isNew) {
                ProcessInstanceUUID piUUID = bpmModule.startNewProcess(processDefinition.getProcessDefinitionUUID(), processVars);
                attachmentBar.save(piUUID.toString(), piUUID.toString());
            } else {
                for (Iterator i = processVars.keySet().iterator(); i.hasNext();) {
                    String varName = i.next().toString();
                    Object varValue = processVars.get(varName);
                    bpmModule.setProcessInstanceVariable(task.getProcessInstanceUUID(), varName, varValue);
                }
                attachmentBar.save(task.getProcessInstanceUUID().toString(), task.getUUID().toString());
                bpmModule.finishTask(task.getBody().getUUID(), true);
            }
            close();
        } catch (Exception ex) {
            showError(ex.getMessage());
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
        form.addField(fieldId, field);
    }

    public void addParticipantInfo() {
        try {
            String participantName = bpmModule.getProcessActivity(processDefinition.getProcessDefinitionUUID(), task.getActivityId()).getPerformer();
            ParticipantDefinition participantDefinition = bpmModule.getProcessParticipant(processDefinition.getProcessDefinitionUUID(), participantName);
            TextField performerField = new TextField(participantDefinition.getParticipantType().name() + " = ", participantDefinition.getName());
            performerField.setReadOnly(true);
            form.addField("performerRoleName", performerField);
        } catch (ProcessNotFoundException ex) {
            Logger.getLogger(DefaultTaskWindow.class.getName()).log(Level.SEVERE, "ProcessNotFoundException " + ex.getMessage());
        } catch (ActivityNotFoundException ex) {
            Logger.getLogger(DefaultTaskWindow.class.getName()).log(Level.SEVERE, "ActivityNotFoundException " + ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(DefaultTaskWindow.class.getName()).log(Level.SEVERE, "Exception " + ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(cancelBtn)) {
            form.discard();
            close();
        } else if (event.getButton().equals(applyBtn)) {
            apply();
        }
    }
}
