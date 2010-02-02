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

import com.vaadin.data.validator.DoubleValidator;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Select;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.processbase.ProcessBase;
import org.processbase.bpm.BPMModule;

/**
 *
 * @author mgubaidullin
 */
public class StartProcessWindow extends Window {

    private ProcessDefinition processDefinition = null;
    private Set<DataFieldDefinition> dfds = null;
    protected BPMModule bpmModule = ((ProcessBase) getApplication()).getCurrent().getBpmModule();
    private Form form = new Form();

    public StartProcessWindow(ProcessDefinition pd) {
        super("Новый процесс");
        this.setCaption("Новый процесс : " + pd.getLabel());
        this.processDefinition = pd;
        initUI();
    }

    private void initUI() {
        try {
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setSizeUndefined();
            addForm();
            this.setResizable(false);
        } catch (Exception ex) {
            ex.printStackTrace();
            getWindow().showNotification("Ошибка", ex.toString(), Notification.TYPE_ERROR_MESSAGE);
        }

    }

    private void addForm() throws ProcessNotFoundException, VariableNotFoundException, Exception {
        form.setWriteThrough(false); // we want explicit 'apply'
        form.setInvalidCommitted(false); // no invalid values in datamodel
        Set<ActivityDefinition> ads = bpmModule.getProcessActivities(processDefinition.getUUID());
        for (Iterator<ActivityDefinition> i = ads.iterator(); i.hasNext();) {
            ActivityDefinition ad = i.next();
        }
        dfds = bpmModule.getProcessDataFields(processDefinition.getUUID());
        for (DataFieldDefinition dfd : dfds) {
            String fieldId = dfd.getName();
            String fieldType = null;
            Field field = null;
//            if (dfd.getDataType().getType().compareTo(DataTypeDefinition.Type.BasicType) == 0) {
//                BasicTypeDefinition btd = ((BasicTypeDefinition) dfd.getDataType().getValue());
//                fieldType = btd.getType().toString();
//                if (fieldType.equalsIgnoreCase("INTEGER")) {
//                    field = new TextField(fieldId);
//                    field.addValidator(new IntegerValidator("Должно быть целочисленное значение"));
//                } else if (fieldType.equalsIgnoreCase("FLOAT")) {
//                    field = new TextField(fieldId);
//                    field.addValidator(new DoubleValidator("Значение должно быть цифровым"));
//                } else if (fieldType.equalsIgnoreCase("DATETIME")) {
//                    field = new PopupDateField(fieldId);        // Set the value of the PopupDateField to current date
//                    field.setValue(new java.util.Date());        // Set the correct resolution
//                    ((PopupDateField) field).setResolution(PopupDateField.RESOLUTION_DAY);        // Add valuechangelistener
//
//                } else if (fieldType.equalsIgnoreCase("STRING")) {
//                    field = new TextField(fieldId);
//                }
//            } else if (dfd.getDataType().getType().compareTo(DataTypeDefinition.Type.EnumerationType) == 0) {
//                EnumerationTypeDefinition etd = ((EnumerationTypeDefinition) dfd.getDataType().getValue());
//                field = new Select(fieldId, etd.getEnumerationValues());
//                ((Select) field).setFilteringMode(AbstractSelect.Filtering.FILTERINGMODE_CONTAINS);
//            }
            form.addField(fieldId, field);
        }
        this.addComponent(form);
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setSpacing(true);
        Button discardChanges = new Button("Отменить", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                form.discard();
                close();
            }
        });
        buttons.addComponent(discardChanges);
        Button apply = new Button("Отправить", new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                try {
                    form.commit();
                    Map<String, Object> vals = new HashMap<String, Object>();
//                    for (DataFieldDefinition dfd : dfds) {
//                        String fieldId = dfd.getDataFieldId().toString();
//                        String fieldType = null;
//                        Object fieldValue = null;
//                        if (dfd.getDataType().getType().compareTo(DataTypeDefinition.Type.BasicType) == 0) {
//                            BasicTypeDefinition btd = ((BasicTypeDefinition) dfd.getDataType().getValue());
//                            fieldType = btd.getType().toString();
//                            fieldValue = null;
//                            if (fieldType.equalsIgnoreCase("INTEGER")) {
//                                fieldValue = new Long(form.getField(fieldId).getValue().toString());
//                            } else if (fieldType.equalsIgnoreCase("FLOAT")) {
//                                fieldValue = new Double(form.getField(fieldId).getValue().toString());
//                            } else if (fieldType.equalsIgnoreCase("DATETIME")) {
//                                fieldValue = ((PopupDateField) form.getField(fieldId)).getValue();
//                            } else if (fieldType.equalsIgnoreCase("STRING")) {
//                                fieldValue = form.getField(fieldId).getValue().toString();
//                            }
//                        } else if (dfd.getDataType().getType().compareTo(DataTypeDefinition.Type.EnumerationType) == 0) {
//                            EnumerationTypeDefinition etd = ((EnumerationTypeDefinition) dfd.getDataType().getValue());
//                            fieldValue = ((Select) form.getField(fieldId)).getValue();
//                        }
//                        vals.put(fieldId, fieldValue);
//                        ((VerticalLayout) form.getParent()).addComponent(new Label(fieldId + " = " + fieldValue + " ( " + fieldValue.getClass().toString()));
//                    }
                    bpmModule.startNewProcess(processDefinition.getUUID(), vals);
                    close();
                } catch (Exception e) {
                    getWindow().showNotification("Ошибка", e.toString(), Notification.TYPE_ERROR_MESSAGE);
                }
            }
        });
        buttons.addComponent(apply);
        form.getLayout().addComponent(buttons);
    }
}
