package org.processbase.ui.bpm.admin;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.services.Document;
import org.processbase.ui.bpm.generator.view.ProcessManager;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ByteArraySource;
import org.processbase.ui.core.template.DownloadStreamResource;
import org.processbase.ui.core.template.TablePanel;

import com.vaadin.data.Item;
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;

public class ProcessVariablesPanel extends TablePanel {
	
	private final ProcessInstance processInstance;
	private Set<DataFieldDefinition> dfds;
	public ProcessVariablesPanel(ProcessInstance processUuid){
		this.processInstance = processUuid;
	}
	
	public void initUI(){
		table.addContainerProperty("name", String.class, null, ProcessbaseApplication.getString("variableName"), null, null);
        table.addContainerProperty("label", String.class, null, ProcessbaseApplication.getString("variableLabel"), null, null);
        table.addContainerProperty("type", String.class, null, ProcessbaseApplication.getString("variableType"), null, null);
        table.addContainerProperty("value", Component.class, null, ProcessbaseApplication.getString("variableValue"), null, null);

        table.addContainerProperty("description", String.class, null, ProcessbaseApplication.getString("variableDesc"), null, null);

        table.setPageLength(15);
        table.setSizeFull();
        table.setWidth("100%");
        horizontalLayout.setMargin(true);
        horizontalLayout.addComponent(table);
        refreshTable();
	}
	
	public void refreshTable(){
		try {
			BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
			dfds = bpmModule.getProcessDataFields(processInstance.getProcessDefinitionUUID());
			
				
			for (DataFieldDefinition dfd : dfds) {
	            Object value = null;
	            try {
	                value = ProcessbaseApplication.getCurrent().getBpmModule().getProcessInstanceVariable(processInstance.getProcessInstanceUUID(), dfd.getName());
	            } catch (Exception ex) {
	                value = "MAYBE CUSTOM CLASS VALUE";
	            }
	            addField(dfd, value);
		    }
			
			List<AttachmentInstance> attachments = bpmModule.getLastAttachments(processInstance.getProcessInstanceUUID());
			if(attachments != null){
				for (final AttachmentInstance attachment : attachments) {
					if(attachment.getFileName() != null && 
							!attachment.getName().equals(attachment.getFileName())){
						
						Item woItem = table.addItem(attachment);
				        woItem.getItemProperty("name").setValue(attachment.getName());
				        woItem.getItemProperty("label").setValue(attachment.getName());
				        woItem.getItemProperty("type").setValue("org.ow2.bonita.facade.runtime.AttachmentInstance");
				        
				        if(attachment.getFileName() != null){
					        Button b = new Button(attachment.getFileName());
							b.setStyleName(Reindeer.BUTTON_LINK);
							
							b.addListener(new Button.ClickListener() {
								public void buttonClick(ClickEvent event) {
									byte[] bytes; 
									try {
										BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
											
										Document document = bpmModule.getDocument(processInstance.getProcessInstanceUUID(),
												attachment.getName());
										
										bytes = bpmModule.getDocumentBytes(document);
										ByteArraySource bas = new ByteArraySource(bytes);

										DownloadStreamResource streamResource = new DownloadStreamResource(bas, 
						                		document.getContentFileName(), getApplication());
						                streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
						            
						                streamResource.setMIMEType("application/octet-stream");
						                streamResource.setParameter("Content-Disposition", "attachment; filename=\"" + document.getContentFileName()+"\"");
						                streamResource.setParameter("Cache-Control", "private, max-age=86400"); 
						                streamResource.setParameter("X-Content-Type-Options", "nosniff");
						                
						                getApplication().getMainWindow().open(streamResource);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							});
					        
					        woItem.getItemProperty("value").setValue(b);
				        }
					}
				}
			 
			}
			
		    table.setReadOnly(true);
		} catch (ProcessNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	 public void addField(DataFieldDefinition dfd, Object value) {
	        Field field = null;
	        Component component = null;
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
					} else if (dfd.getDataTypeClassName().equals("java.util.List")) { 
						List list = (List)value;
						if(list.size() > 0 && list.get(0) instanceof AttachmentInstance ){
							HorizontalLayout hl = new HorizontalLayout();
							hl.setSpacing(true);
							component = hl;
							for (Object o : list) {
								try{
									final AttachmentInstance attachment = (AttachmentInstance) o;
							        Button b = new Button(attachment.getName());
									b.setStyleName(Reindeer.BUTTON_LINK);
								
									b.addListener(new Button.ClickListener() {
										public void buttonClick(ClickEvent event) {
											byte[] bytes; 
											try {
												BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
													
												Document document = bpmModule.getDocument(attachment.getProcessInstanceUUID(),
														attachment.getName());
												
												bytes = bpmModule.getDocumentBytes(document);
												ByteArraySource bas = new ByteArraySource(bytes);

												DownloadStreamResource streamResource = new DownloadStreamResource(bas, 
								                		document.getContentFileName(), getApplication());
								                streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
								              
								                streamResource.setMIMEType("application/octet-stream");
								                streamResource.setParameter("Content-Disposition", "attachment; filename=\"" + document.getContentFileName()+"\"");
								                streamResource.setParameter("Cache-Control", "private, max-age=86400"); 
								                streamResource.setParameter("X-Content-Type-Options", "nosniff");
								                
								                getApplication().getMainWindow().open(streamResource);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									});
									hl.addComponent(b);
								
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
						} else {
						    field = new TextField(dfd.getLabel());
						    field.setValue(value != null ? value.toString() : "");
						}
					} else {
					    field = new TextField(dfd.getLabel());
					    field.setValue(value != null ? value.toString() : "");
					}
				} catch (Exception e) {
					 field = new TextField(dfd.getLabel());
					 field.setValue(value != null ? value.toString() : "");
				}
	        }
	        if(field != null){
	        	field.setDescription(dfd.getDescription() != null ? dfd.getDescription() : "");
	        }

	        Item woItem = table.addItem(dfd);
	        woItem.getItemProperty("name").setValue(dfd.getName());
	        woItem.getItemProperty("label").setValue(dfd.getLabel());
	        woItem.getItemProperty("type").setValue(dfd.getDataTypeClassName());
	        if(component != null){
	        	woItem.getItemProperty("value").setValue(component);
	        }else{
	        	woItem.getItemProperty("value").setValue(field);
	        }
	    }
}
