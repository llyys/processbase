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
package org.processbase.ui.worklist;

import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import java.util.Iterator;
import java.util.Set;
import org.processbase.ui.template.TaskWindow;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ParticipantNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.processbase.ui.template.AttachmentsPanel;

/**
 *
 * @author mgubaidullin
 */
public class DefaultTaskWindow extends TaskWindow implements Button.ClickListener {

    private Form form = new Form();
    protected AttachmentsPanel attachmentsPanel = null;

    public DefaultTaskWindow(PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
    }

    @Override
    public void exec() {
        try {
            setCaption(messages.getString("defaultTaskWindowCaption2") + " " + task.getActivityLabel());
            buttons.setSpacing(true);
            applyBtn.addListener(this);
            cancelBtn.addListener(this);
            buttons.addComponent(applyBtn);
            buttons.addComponent(cancelBtn);
            createForm();
            form.getLayout().addComponent(buttons);

            attachmentsPanel = new AttachmentsPanel(portletApplicationContext2, this.task.getProcessInstanceUUID().toString(), null, true, true, true);
            attachmentsPanel.setCaption("Сканированные документы");
            attachmentsPanel.setMargin(true, false, false, false);
            attachmentsPanel.refreshTable();

            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(false);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);
            layout.setWidth("100%");
            this.setSizeFull();
            layout.addComponent(attachmentsPanel);
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }

    }

    public void createForm() throws ProcessNotFoundException, VariableNotFoundException, InstanceNotFoundException, ProcessNotFoundException, ActivityNotFoundException, ParticipantNotFoundException, Exception {
        form.setWriteThrough(false); // we want explicit 'apply'
        form.setInvalidCommitted(false);
        if (this.isNew) {
            dfds = bpmModule.getProcessDataFields(processDefinition.getUUID());
            for (DataFieldDefinition dfd : dfds) {
                addField(dfd, dfd.getInitialValue());
            }
        } else {
            dfds = bpmModule.getProcessDataFields(task.getProcessDefinitionUUID());
            processVars = bpmModule.getProcessInstanceVariables(task.getProcessInstanceUUID());
            for (DataFieldDefinition dfd : dfds) {
                addField(dfd, processVars.get(dfd.getName()));
            }
            addParticipantInfo();
        }
        addComponent(form);
    }

    public void apply() {
        try {
            form.commit();
            for (DataFieldDefinition dfd : dfds) {
                Object fieldValue = null;
                if (!dfd.isEnumeration()) {
                    if (dfd.getDataTypeClassName().equals("java.lang.Long")) {
                        fieldValue = new Long(form.getField(dfd.getName()).getValue().toString());
                    } else if (dfd.getDataTypeClassName().equals("java.lang.Double")) {
                        fieldValue = new Double(form.getField(dfd.getName()).getValue().toString());
                    } else if (dfd.getDataTypeClassName().equals("java.util.Date")) {
                        fieldValue = ((PopupDateField) form.getField(dfd.getName())).getValue();
                    } else if (dfd.getDataTypeClassName().equals("java.lang.String")) {
                        fieldValue = form.getField(dfd.getName()).getValue().toString();
                    } else if (dfd.getDataTypeClassName().equals("java.lang.Boolean")) {
                        fieldValue = ((CheckBox) form.getField(dfd.getName())).booleanValue();
                    }
                } else if (dfd.isEnumeration()) {
                    fieldValue = form.getField(dfd.getName()).getValue();
                }
                if (fieldValue != null) {
                    processVars.put(dfd.getName(), fieldValue);
                }
            }
            if (this.isNew) {
                ProcessInstanceUUID piUUID = bpmModule.startNewProcess(processDefinition.getUUID(), processVars);
//                attachmentBar.save(piUUID.toString(), piUUID.toString());
            } else {
//                attachmentBar.save(task.getProcessInstanceUUID().toString(), task.getUUID().toString());
                bpmModule.finishTask(task, true, processVars);
            }
            close();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
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
                field = new TextField(dfd.getLabel() + " (" + dfd.getDataTypeClassName() + ")");
//                field.setValue(dfd.getDataTypeClassName());
                field.setEnabled(false);
            }
        }
        field.setDescription(dfd.getDescription() != null ? dfd.getDescription() : "");
        form.addField(dfd.getName(), field);
    }

    public void addParticipantInfo() {
        try {
            Set<String> participantNames = bpmModule.getProcessActivity(task.getProcessDefinitionUUID(), task.getActivityName()).getPerformers();
//            ParticipantDefinition participantDefinition = bpmModule.getProcessParticipant(processDefinition.getUUID(), participantName);
//            TextField performerField = new TextField(participantDefinition.getParticipantType().name() + " = ", participantDefinition.getName());
            TextField performerField = new TextField("PARTICIPANT", participantNames.toString());
            performerField.setReadOnly(true);
            form.addField("performerRoleName", performerField);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(cancelBtn)) {
            form.discard();
        } else if (event.getButton().equals(applyBtn)) {
            apply();
        }
        close();
    }
}
