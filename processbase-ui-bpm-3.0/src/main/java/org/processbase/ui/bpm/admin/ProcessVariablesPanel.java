package org.processbase.ui.bpm.admin;

import java.util.Set;

import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.ActivityNotFoundException;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ParticipantNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.exception.VariableNotFoundException;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.TablePanel;

import com.vaadin.data.Item;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

public class ProcessVariablesPanel extends TablePanel {
	
	private final ProcessInstance processInstance;
	private Set<DataFieldDefinition> dfds;
	public ProcessVariablesPanel(ProcessInstance processUuid){
		this.processInstance = processUuid;
	}
	
	public void initUI(){
		table.addContainerProperty("name", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("variableName"), null, null);
        table.addContainerProperty("label", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("variableLabel"), null, null);
        table.addContainerProperty("type", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("variableType"), null, null);
        table.addContainerProperty("value", Field.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("variableValue"), null, null);

        table.addContainerProperty("description", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("variableDesc"), null, null);

        table.setPageLength(15);
        table.setSizeFull();
        table.setWidth("100%");
        horizontalLayout.setMargin(true);
        horizontalLayout.addComponent(table);
        refreshTable();
	}
	
	public void refreshTable(){
		try {
			dfds = ProcessbaseApplication.getCurrent().getBpmModule().getProcessDataFields(processInstance.getProcessDefinitionUUID());
			 for (DataFieldDefinition dfd : dfds) {
		            Object value = null;
		            try {
		                value = ProcessbaseApplication.getCurrent().getBpmModule().getProcessInstanceVariable(processInstance.getProcessInstanceUUID(), dfd.getName());
		            } catch (Exception ex) {
		                value = "MAYBE CUSTOM CLASS VALUE";
		            }
		            addField(dfd, value);
		        }
		        table.setReadOnly(true);
		} catch (ProcessNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	            try {
					if (dfd.getDataTypeClassName().equals("java.lang.Long")) {
					    field = new TextField(dfd.getLabel());
					    if (value != null) {
					        field.setValue(new Long(value.toString()));
					    }
//	                    field.addValidator(new DoubleValidator("Š—Š½Š°Ń‡ŠµŠ½ŠøŠµ Š´Š¾Š»Š¶Š½Š¾ Š±Ń‹Ń‚Ń� Ń†ŠøŃ„Ń€Š¾Š²Ń‹Š¼"));
					} else if (dfd.getDataTypeClassName().equals("java.lang.Double")) {
					    field = new TextField(dfd.getLabel());
					    if (value != null) {
					        field.setValue(new Double(value.toString()));
					    }
//	                    field.addValidator(new DoubleValidator("Š—Š½Š°Ń‡ŠµŠ½ŠøŠµ Š´Š¾Š»Š¶Š½Š¾ Š±Ń‹Ń‚Ń� Ń†ŠøŃ„Ń€Š¾Š²Ń‹Š¼"));
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
				} catch (Exception e) {
					 field = new TextField(dfd.getLabel());
					 field.setValue(value != null ? value.toString() : "");
				}
	        }
	        field.setDescription(dfd.getDescription() != null ? dfd.getDescription() : "");

	        Item woItem = table.addItem(dfd);
	        woItem.getItemProperty("name").setValue(dfd.getName());
	        woItem.getItemProperty("label").setValue(dfd.getLabel());
	        woItem.getItemProperty("type").setValue(dfd.getDataTypeClassName());
	        woItem.getItemProperty("value").setValue(field);
	    }
}
