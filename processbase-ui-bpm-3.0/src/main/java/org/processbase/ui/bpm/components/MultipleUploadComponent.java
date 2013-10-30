package org.processbase.ui.bpm.components;

import java.util.Collection;
import java.util.Date;

import org.ow2.bonita.facade.runtime.AttachmentInstance;
import org.ow2.bonita.facade.runtime.Document;
import org.ow2.bonita.facade.runtime.impl.AttachmentInstanceImpl;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.processbase.ui.bpm.generator.view.TaskField;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.bonita.forms.Widget;
import org.processbase.ui.core.template.ImmediateUpload;

import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FinishedEvent;

public class MultipleUploadComponent extends com.vaadin.ui.VerticalLayout {
	public MultipleUploadComponent(TaskField field, final ProcessInstanceUUID processInstanceUUID, final Widget widget) {
		super();

		setSpacing(false);
		setCaption(widget.getLabel());

		String fileName;
		for (Object object : (Collection) field.getValue()) {
			
				AttachmentInstance attachment = (AttachmentInstance) object;
				fileName = attachment.getName();
				
				ImmediateUpload immediateUpload = new ImmediateUpload(attachment.getProcessInstanceUUID().toString(), 
						attachment.getName(), field.getVariableBound(), fileName, true, widget.isReadonly(), 
						ProcessbaseApplication.getCurrent().getPbMessages(), true);
				addComponent(immediateUpload);
			
		}
		if(!widget.isReadonly())
			addComponent(createUploadControl(processInstanceUUID, widget, field));
	}

	private ImmediateUpload createUploadControl(final ProcessInstanceUUID processInstanceUUID, final Widget widget, final TaskField field) {
		final ImmediateUpload component = new ImmediateUpload(processInstanceUUID.toString(), ProcessbaseApplication.getString("addFileBtn", "Add file"), field.getVariableBound(), "", false, widget.isReadonly(),ProcessbaseApplication.getCurrent().getPbMessages(), true);
		component.addListener(new Upload.FinishedListener() {
			public void uploadFinished(FinishedEvent event) {
				if(event instanceof Upload.SucceededEvent){
					try {
						BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
						java.util.ArrayList variable = (java.util.ArrayList) bpmModule.getProcessInstanceVariable(processInstanceUUID,field.getVariableBound());
						Document document = bpmModule.addDocument(processInstanceUUID, event.getFilename(),event.getFilename(), event.getMIMEType(),component.getFileBody());
						AttachmentInstanceImpl attachment = new AttachmentInstanceImpl(document.getUUID(), event.getFilename(),processInstanceUUID, ProcessbaseApplication.getCurrent().getUserName(), new Date());
						variable.add(attachment);
	
						bpmModule.setProcessInstanceVariable(processInstanceUUID,widget.getVariableBound(), variable);
						// after uploading add another upload control
						addComponent(createUploadControl(processInstanceUUID,widget, field));
					} catch (Exception e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					}
				}
			}
		});
		return component;
	}
}
