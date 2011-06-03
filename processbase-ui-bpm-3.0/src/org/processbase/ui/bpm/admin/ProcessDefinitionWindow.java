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
package org.processbase.ui.bpm.admin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.UUID;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.util.Misc;
import org.processbase.ui.bpm.admin.process.CustomUiPanel;
import org.processbase.ui.bpm.admin.process.DescriptionPanel;
import org.processbase.ui.bpm.admin.process.ProcessAccessPanel;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.ByteArraySource;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.PbWindow;

import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;

/**
 *
 * @author mgubaidullin
 */
public class ProcessDefinitionWindow extends PbWindow implements
        ClickListener,
        Upload.SucceededListener,
        Upload.FailedListener,
        Upload.Receiver,
        TabSheet.SelectedTabChangeListener {

    private ProcessDefinition processDefinition = null;
   
    private ButtonBar buttons = new ButtonBar();
    private Button closeBtn;
    private Button applyBtn;
    private Button saveAccessBtn;
    private Upload upload = new Upload("", (Upload.Receiver) this);
    private Button deleteAllBtn;
    private Button deleteInstancesBtn;
    private Button downloadBtn;
    private CheckBox enableBtn;
    private Button archiveBtn;
    private Table activitiesTable = new Table();
    private File file;
    private String filename;
    private String originalFilename;
    private String fileExt;
    
    private TabSheet tabSheet = new TabSheet();
    private DescriptionPanel descPanel=new DescriptionPanel();
    private CustomUiPanel uiPanel=new CustomUiPanel();
    private ProcessAccessPanel accessPanel=new ProcessAccessPanel();
    
    
    
    

    public ProcessDefinitionWindow(ProcessDefinition processDefinition) {
        super(processDefinition.getLabel());
        this.processDefinition = processDefinition;
    }

    public void initUI() {
        try {
            String caption = processDefinition.getLabel() != null ? processDefinition.getLabel() : processDefinition.getName();
            setCaption(caption + " (v." + processDefinition.getVersion() + ")");
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName(Reindeer.LAYOUT_WHITE);
            descPanel.setProcessDefinition(processDefinition);
            descPanel.initUI();
            
            uiPanel.setProcessDefinition(processDefinition);
            uiPanel.initUI();
            
            accessPanel.setProcessDefinition(processDefinition);
            accessPanel.initUI();
//            v1.setMargin(true, false, false, false);
//            v1.setSizeFull();
//
//            if (processDefinition.getLabel() != null) {
//                Label pdLabel = new Label("<b>" + processDefinition.getLabel() + "</b>");
//                pdLabel.setContentMode(Label.CONTENT_XHTML);
//                v1.addComponent(pdLabel);
//            }
//
//            if (processDefinition.getDescription() != null) {
//                Label pdDescription = new Label(processDefinition.getDescription());
//                pdDescription.setContentMode(Label.CONTENT_XHTML);
//                v1.addComponent(pdDescription);
//                v1.setExpandRatio(pdDescription, 1);
//            }

//            tabSheet.addTab(v1, ProcessbaseApplication.getCurrent().getPbMessages().getString("tabDescription"), null);
            tabSheet.addTab(descPanel, ProcessbaseApplication.getCurrent().getPbMessages().getString("tabDescription"), null);
            tabSheet.addTab(uiPanel, ProcessbaseApplication.getCurrent().getPbMessages().getString("tabCustomUI"), null);

            tabSheet.addTab(accessPanel, ProcessbaseApplication.getCurrent().getPbMessages().getString("processAccess"), null);
            

            tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);
            tabSheet.setSizeFull();
            tabSheet.addListener((TabSheet.SelectedTabChangeListener) this);
            layout.addComponent(tabSheet);
            layout.setExpandRatio(tabSheet, 1);

            closeBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnClose"), this);
            applyBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnSaveCustomUI"), this);
            saveAccessBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnSaveProcessAccess"), this);
            deleteAllBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDeleteAll"), this);
            deleteInstancesBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDeleteInstances"), this);
            downloadBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDownload"), this);
            enableBtn = new CheckBox(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnEnable"), this);
            archiveBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnArchive"), this);

            
            buttons.addButton(downloadBtn);
            buttons.setComponentAlignment(downloadBtn, Alignment.MIDDLE_RIGHT);
            
            deleteAllBtn.setDescription(ProcessbaseApplication.getCurrent().getPbMessages().getString("deleteProcessDefinition"));
            buttons.addButton(deleteAllBtn);
            buttons.setComponentAlignment(deleteAllBtn, Alignment.MIDDLE_RIGHT);
            
            deleteInstancesBtn.setDescription(ProcessbaseApplication.getCurrent().getPbMessages().getString("deleteProcessInstances"));
            buttons.addButton(deleteInstancesBtn);
            buttons.setComponentAlignment(deleteInstancesBtn, Alignment.MIDDLE_RIGHT);

            Label expand = new Label("");
            buttons.addComponent(expand);
            buttons.setExpandRatio(expand, 1);

            enableBtn.setValue(processDefinition.getState().equals(ProcessState.ENABLED));
            buttons.addButton(enableBtn);
            buttons.setComponentAlignment(enableBtn, Alignment.MIDDLE_RIGHT);

            buttons.addButton(archiveBtn);
            buttons.setComponentAlignment(archiveBtn, Alignment.MIDDLE_RIGHT);

            applyBtn.setVisible(false);
            buttons.addButton(applyBtn);
            buttons.setComponentAlignment(applyBtn, Alignment.MIDDLE_RIGHT);
            buttons.addButton(saveAccessBtn);
            saveAccessBtn.setVisible(false);
            buttons.setComponentAlignment(saveAccessBtn, Alignment.MIDDLE_RIGHT);
            buttons.addButton(closeBtn);
            buttons.setComponentAlignment(closeBtn, Alignment.MIDDLE_RIGHT);
            buttons.setMargin(false);
            buttons.setHeight("30px");
            buttons.setWidth("100%");
            layout.addComponent(buttons);
            layout.setWidth("800px");
            layout.setHeight("400px");
            setResizable(false);
            setModal(true);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }
    

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                uiPanel.save();
                close();
            } else if (event.getButton().equals(downloadBtn)) {
                download();
            } else if (event.getButton().equals(deleteAllBtn)) {
                deleteAll();
                close();
            } else if (event.getButton().equals(deleteInstancesBtn)) {
                deleteInstances();
            } else if (event.getButton().equals(enableBtn)) {
                enableProcess();
            } else if (event.getButton().equals(archiveBtn)) {
                archiveProcess();
            } else if (event.getButton().equals(saveAccessBtn)) {
                accessPanel.saveProcessAccess();
                close();
            } else {
                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    private void enableProcess() {
        final ProcessbaseApplication processbase = ProcessbaseApplication.getCurrent();
        final PbWindow mainWindow = (PbWindow) getApplication().getMainWindow();
        ConfirmDialog.show(getApplication().getMainWindow(),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("windowCaptionConfirm"),
                enableBtn.booleanValue()
                ? ProcessbaseApplication.getCurrent().getPbMessages().getString("questionEnableProcessDefinition")
                : ProcessbaseApplication.getCurrent().getPbMessages().getString("questionDisableProcessDefinition"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnYes"),
                ProcessbaseApplication.getCurrent().getPbMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                if (enableBtn.booleanValue()) {
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
                            try {
                                processbase.getBpmModule().archiveProcessDefinitions(processDefinition.getUUID());
                                mainWindow.showInformation(processbase.getPbMessages().getString("executedSuccessfully"));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                mainWindow.showError(ex.getMessage());
                            }
                        }
                    }
                });
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
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                mainWindow.showError(ex.getMessage());
                            }
                        }
                    }
                });
    }

    private void download() {
        try {
        	byte[] bytes = ProcessbaseApplication.getCurrent().getBpmModule().getBusinessArchiveFile(processDefinition.getUUID());
        	
        	ByteArraySource bas = new ByteArraySource(bytes);
        	
			StreamResource streamResource = new StreamResource(bas, processDefinition.getLabel() + "_" + processDefinition.getVersion() + ".bar", getApplication());
			streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
			streamResource.setMIMEType("application/octet-stream");
			getWindow().getWindow().open(streamResource, "_new");
        	
        } catch (Exception e) {
        	showError(e);
            
        }
    }
    
  

   

    public void uploadSucceeded(SucceededEvent event) {
        try {
//            byte[] readData = new byte[new Long(event.getLength()).intValue()];
//            FileInputStream fis = new FileInputStream(file);
//            int i = fis.read(readData);
//            HashMap<String, String> urlMap = (HashMap<String, String>) XMLManager.createObject(new String(readData, "UTF-8"));
//            for (String key : urlMap.keySet()) {
//                PbPortlet.getCurrent().bpmModule.addProcessMetaData(processDefinition.getUUID(), key, urlMap.get(key));
//            }
//            fis.close();
//            file.delete();
//            refreshTable();
        } catch (Exception ex) {
            ex.printStackTrace();
            getWindow().showNotification(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void uploadFailed(FailedEvent event) {
        event.getReason().printStackTrace();
        showError(event.getReason().getMessage());
    }

    public OutputStream receiveUpload(
            String filename, String MIMEType) {
        this.originalFilename = filename;
        this.filename = UUID.randomUUID().toString();
        FileOutputStream fos = null;
        try {
            file = new File(this.filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return fos;
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        if (event.getTabSheet().getSelectedTab().equals(descPanel)) {
            saveAccessBtn.setVisible(false);
            applyBtn.setVisible(false);
            deleteAllBtn.setVisible(true);
            deleteInstancesBtn.setVisible(true);
            downloadBtn.setVisible(true);
            enableBtn.setVisible(true);
            archiveBtn.setVisible(true);
        } else if (event.getTabSheet().getSelectedTab().equals(uiPanel)) {
            applyBtn.setVisible(true);
            saveAccessBtn.setVisible(false);
            deleteAllBtn.setVisible(false);
            deleteInstancesBtn.setVisible(false);
            downloadBtn.setVisible(false);
            enableBtn.setVisible(false);
            archiveBtn.setVisible(false);
        } else if (event.getTabSheet().getSelectedTab().equals(accessPanel)) {
            saveAccessBtn.setVisible(true);
            applyBtn.setVisible(false);
            deleteAllBtn.setVisible(false);
            deleteInstancesBtn.setVisible(false);
            downloadBtn.setVisible(false);
            enableBtn.setVisible(false);
            archiveBtn.setVisible(false);
        }
    }

    /*private void removeProcessAccess(){
    	ProcessbaseApplication.getCurrent().getBpmModule().removeRuleFromEntities(rule.getUUID(), null, null, null, deletedMembership, null);
    }*/

    
    
    private void showError(Exception ex)
    {
    	ex.printStackTrace();
        getWindow().showNotification(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
    }
}
