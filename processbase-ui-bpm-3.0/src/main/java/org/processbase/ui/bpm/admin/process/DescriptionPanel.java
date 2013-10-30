package org.processbase.ui.bpm.admin.process;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.ow2.bonita.env.EnvConstants;
import org.ow2.bonita.env.Environment;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.util.Command;
import org.processbase.ui.bpm.admin.ProcessDefinitionWindow;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ByteArraySource;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.ITabsheetPanel;
import org.processbase.ui.core.template.PbPanel;
import org.processbase.ui.core.template.PbWindow;

import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window.Notification;

import ee.kovmen.data.LegislationData;

/**
*
* @author llyys
*/
public class DescriptionPanel extends PbPanel implements ITabsheetPanel, ClickListener{
	
	private Button btnDownload;
	private Button btnDeleteAll;
	private Button btnDeleteInstances;
	private CheckBox chbEnable;
	//private Button btnArchive;
	 private Embedded processImage = null;
	
	private ProcessDefinitionWindow parentWindow;
	private ProcessDefinition processDefinition = null;
	
	private TextField processName;
	private TextField processDescription;
	private Button btnSave;
	
	
	 @Override
	 public void initUI() {
        
        setMargin(true, false, false, true);
        
        btnSave = new Button(ProcessbaseApplication.getString("btnSave"), this);
        getParentWindow().getButtons().addButton(btnSave);
        getParentWindow().getButtons().setComponentAlignment(btnSave, Alignment.MIDDLE_RIGHT);
        
        processName = new TextField(ProcessbaseApplication.getString("tableCaptionProcessName"));
        processName.setValue(processDefinition.getLabel());
        processName.setRequired(true);
        processName.setRequiredError(ProcessbaseApplication.getString("tableCaptionProcessName")+ 
        		ProcessbaseApplication.getString("fieldRequired"));
        processName.setWidth("100%");
        processName.setNullRepresentation("");
        addComponent(processName);
        
        processDescription = new TextField(ProcessbaseApplication.getString("tableCaptionDescription"));
        processDescription.setValue(processDefinition.getDescription());
        processDescription.setRequired(true);
        processDescription.setRequiredError(ProcessbaseApplication.getString("tableCaptionDescription")+ 
        		ProcessbaseApplication.getString("fieldRequired"));
        processDescription.setWidth("100%");
        processDescription.setNullRepresentation("");
        addComponent(processDescription);
        
        
        btnDownload = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDownload"), this);
        getParentWindow().getButtons().addButton(btnDownload);
        getParentWindow().getButtons().setComponentAlignment(btnDownload, Alignment.MIDDLE_RIGHT);
       
        btnDeleteAll = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDeleteAll"), this);
        btnDeleteAll.setDescription(ProcessbaseApplication.getCurrent().getPbMessages().getString("deleteProcessDefinition"));
        getParentWindow().getButtons().addButton(btnDeleteAll);
        getParentWindow().getButtons().setComponentAlignment(btnDeleteAll, Alignment.MIDDLE_RIGHT);
        
        btnDeleteInstances = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDeleteInstances"), this);
        btnDeleteInstances.setDescription(ProcessbaseApplication.getCurrent().getPbMessages().getString("deleteProcessInstances"));
        getParentWindow().getButtons().addButton(btnDeleteInstances);
        getParentWindow().getButtons().setComponentAlignment(btnDeleteInstances, Alignment.MIDDLE_RIGHT);
       
        chbEnable = new CheckBox(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnEnable"), this);
        chbEnable.setValue(processDefinition.getState().equals(ProcessState.ENABLED));
        getParentWindow().getButtons().addButton(chbEnable);
        getParentWindow().getButtons().setComponentAlignment(chbEnable, Alignment.MIDDLE_RIGHT);

//        btnArchive = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnArchive"), this);
//        getParentWindow().getButtons().addButton(btnArchive);
//        getParentWindow().getButtons().setComponentAlignment(btnArchive, Alignment.MIDDLE_RIGHT);

        getParentWindow().setResizable(true);
        
  
//        if (processDefinition.getLabel() != null) {
//            Label pdLabel = new Label("<b>" + processDefinition.getLabel() + "</b>");
//            pdLabel.setContentMode(Label.CONTENT_XHTML);
//            addComponent(pdLabel);
//        }
//
//        if (processDefinition.getDescription() != null) {
//            Label pdDescription = new Label(processDefinition.getDescription());
//            pdDescription.setContentMode(Label.CONTENT_XHTML);
//            addComponent(pdDescription);
//            setExpandRatio(pdDescription, 1);
//        }
        
        ByteArraySource bas;
		try {
			bas = new ByteArraySource(ProcessbaseApplication.getCurrent().getBpmModule().getProcessDiagramm(processDefinition.getUUID()));
			StreamResource imageResource = new StreamResource(bas, "processInstance.png", ProcessbaseApplication.getCurrent());
		    imageResource.setCacheTime(1000);
		    processImage = new Embedded("", imageResource);
		    addComponent(processImage);
		} catch (Exception e) {
			e.printStackTrace();
		}
      
	 }
	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}
		
	private void download() {
        try {
        	byte[] bytes = ProcessbaseApplication.getCurrent().getBpmModule().getBusinessArchiveFile(processDefinition.getUUID());        	
        	
        	ByteArraySource bas = new ByteArraySource(bytes);
        	
			StreamResource streamResource = new StreamResource(bas, processDefinition.getLabel() + "_" + processDefinition.getVersion() + ".bar", getApplication());
			streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
			streamResource.setMIMEType("application/octet-stream");
			getWindow().getWindow().open(streamResource, "_blank");
        	
        } catch (Exception e) {
        	showError(e);            
        }
    }
	
	private void showError(Exception ex)
    {
    	ex.printStackTrace();
        getWindow().showNotification(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
    }
	
	@Override
	public void onActivate(boolean isActive) {
		btnDeleteAll.setVisible(isActive);
		btnDownload.setVisible(isActive);
		btnDeleteInstances.setVisible(isActive);
		//btnArchive.setVisible(isActive);
		chbEnable.setVisible(isActive);
		btnSave.setVisible(isActive);
	}
	 
	 private void deleteAll() {
	        final ProcessbaseApplication processbase = ProcessbaseApplication.getCurrent();
	        final PbWindow mainWindow = (PbWindow) getApplication().getMainWindow();
	        ConfirmDialog.show(mainWindow,
	                ProcessbaseApplication.getCurrent().getPbMessages().getString("windowCaptionConfirm"),
	                ProcessbaseApplication.getCurrent().getPbMessages().getString("questionDeleteProcessAndInstances"),
	                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnYes"),
	                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnNo"),
	                new ConfirmDialog.Listener() {

	                    public void onClose(ConfirmDialog dialog) {
	                        if (dialog.isConfirmed()) {
	                            try {
	                                processbase.getBpmModule().deleteProcess(processDefinition);
	                                mainWindow.showInformation(processbase.getPbMessages().getString("executedSuccessfully"));
	                                getParentWindow().close();
	                            } catch (org.ow2.bonita.facade.exception.UndeletableInstanceException ex){
	                            		mainWindow.showError("Menetlust "+ex.getProcessInstanceUUID() +" ei saa kustutada, sest see on seotud aktiivse "+ ex.getParentInstanceUUID()+" menetlusega");
	                            } catch (Exception ex) {
	                                ex.printStackTrace();
	                                throw new RuntimeException(ex);
	                            }
	                        }
	                    }
	                });
	    }
	 
	public void setParentWindow(ProcessDefinitionWindow parentWindow) {
		this.parentWindow = parentWindow;
	}
	
	public ProcessDefinitionWindow getParentWindow() {
		return parentWindow;
	}
	 
	private void enableProcess() {
        final ProcessbaseApplication processbase = ProcessbaseApplication.getCurrent();
        final PbWindow mainWindow = (PbWindow) getApplication().getMainWindow();
        ConfirmDialog.show(getApplication().getMainWindow(),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("windowCaptionConfirm"),
                chbEnable.booleanValue()
                ? ProcessbaseApplication.getCurrent().getPbMessages().getString("questionEnableProcessDefinition")
                : ProcessbaseApplication.getCurrent().getPbMessages().getString("questionDisableProcessDefinition"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnYes"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                if (chbEnable.booleanValue()) {
                                    processbase.getBpmModule().enableProcessDefinitions(processDefinition.getUUID());
                                } else {
                                    processbase.getBpmModule().disableProcessDefinitions(processDefinition.getUUID());
                                }
                                mainWindow.showInformation(processbase.getPbMessages().getString("executedSuccessfully"));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                mainWindow.showError(ex.getMessage());
                            }
                        }
                    }
                });
    }
	
	private void deleteInstances() {
        final ProcessbaseApplication processbase = ProcessbaseApplication.getCurrent();
        final PbWindow mainWindow = (PbWindow) getApplication().getMainWindow();
        ConfirmDialog.show(mainWindow,
                ProcessbaseApplication.getCurrent().getPbMessages().getString("windowCaptionConfirm"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("questionDeleteInstances"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnYes"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                processbase.getBpmModule().deleteAllProcessInstances(processDefinition);
                                mainWindow.showInformation(processbase.getPbMessages().getString("executedSuccessfully"));
                                getParentWindow().close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                mainWindow.showError(ex.getMessage());
                            }
                        }
                    }
                });
    }
	
	private void archiveProcess() {
        final ProcessbaseApplication processbase = ProcessbaseApplication.getCurrent();
        final PbWindow mainWindow = (PbWindow) getApplication().getMainWindow();
        ConfirmDialog.show(getApplication().getMainWindow(),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("windowCaptionConfirm"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("questionArchiveProcessDefinition"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnYes"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                        	
                			//Archive proccess definition
                            try {
                                processbase.getBpmModule().archiveProcessDefinitions(processDefinition.getUUID());
                                mainWindow.showInformation(processbase.getPbMessages().getString("executedSuccessfully"));
                                getParentWindow().close();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                mainWindow.showError(ex.getMessage());
                            }
                        }
                    }
                });
    }

	
	public void buttonClick(ClickEvent event) {
		try {
			if (event.getButton().equals(btnDownload)) {
	            download();
	            return;
	        }
			if (event.getButton().equals(btnDeleteInstances)) {
               deleteInstances();
               return;
            }
            if (event.getButton().equals(chbEnable)) {
            	enableProcess();
                return;
            }            
//            if (event.getButton().equals(btnArchive)) {
//            	archiveProcess();
//                return;
//            }            
            if (event.getButton().equals(btnDeleteAll)) {
            	deleteAll();
			    return;
            }
            
            if(event.getButton().equals(btnSave)){
            	saveProcessDescription();
            }
            
        } catch (Exception ex) {
            showError(ex);
        }
		
	}
	
	private void saveProcessDescription(){
		
		// Validate fields
		try {
			processName.validate();
			processDescription.validate();
		} catch (Exception e) {
			return;
		}

		String label = (String) processName.getValue();
		String description = (String) processDescription.getValue();
		
		try {
			Session s = LegislationData.getCurrent().getSession();

			Transaction t = s.beginTransaction();
			
			SQLQuery q = s.createSQLQuery("update bn_proc_def set label_='"
					+ label + "', label_or_name_='" + label
					+ "',  description_='" + description
					+ "' where proc_uuid_='" + processDefinition.getUUID()
					+ "' ");
			q.executeUpdate();
			t.commit();
			
			getParentWindow().close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	 
}
