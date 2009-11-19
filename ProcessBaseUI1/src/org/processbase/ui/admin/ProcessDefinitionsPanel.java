/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.Constants;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.def.majorElement.PackageDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.processbase.ProcessBase;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.template.MessageWindow;
import org.processbase.util.ProcessBaseClassLoader;

/**
 *
 * @author mgubaidullin
 */
public class ProcessDefinitionsPanel extends TablePanel implements
        Button.ClickListener,
        Window.CloseListener,
        Upload.SucceededListener,
        Upload.FailedListener,
        Upload.Receiver {

//    Button deployBtn = new Button(messages.getString("btnUpload"));
    private Upload upload = new Upload("", (Upload.Receiver) this);
    private File file;
    private String filename;
    private String originalFilename;
    private String fileExt;
    public static String FILE_XPDL = "FILE_XPDL";
    public static String FILE_BAR = "FILE_BAR";
    public static String FILE_JAR = "FILE_JAR";
    private String fileType = null;
    protected BPMModule bpmModule = ((ProcessBase) getApplication()).getCurrent().getBpmModule();

    public ProcessDefinitionsPanel() {
        super();
        upload.setButtonCaption(messages.getString("btnUpload"));
        upload.setImmediate(true);
        upload.addListener((Upload.SucceededListener) this);
        upload.addListener((Upload.FailedListener) this);
        buttonBar.addComponent(upload, 1);
        buttonBar.setComponentAlignment(upload, Alignment.MIDDLE_LEFT);
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("UUID", String.class, null, "UUID", null, null);
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionProcessName"), null, null);
        table.addContainerProperty("version", String.class, null, messages.getString("tableCaptionVersion"), null, null);
        table.addContainerProperty("author", String.class, null, messages.getString("tableCaptionAuthor"), null, null);
        table.addContainerProperty("desc", String.class, null, messages.getString("tableCaptionDescription"), null, null);
        table.addContainerProperty("status", String.class, null, messages.getString("tableCaptionStatus"), null, null);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
    }

    @Override
    public void refreshTable() {
        table.removeAllItems();
        Set<PackageDefinition> pds = bpmModule.getPackageDefinitions();
        for (PackageDefinition pd : pds) {
            Item woItem = table.addItem(pd);
            woItem.getItemProperty("UUID").setValue(pd.getPackageDefinitionUUID());
            woItem.getItemProperty("name").setValue(pd.getName());
            woItem.getItemProperty("version").setValue(pd.getVersion());
            woItem.getItemProperty("author").setValue(pd.getAuthor());
            woItem.getItemProperty("desc").setValue(pd.getDescription());
            woItem.getItemProperty("status").setValue(pd.getState());
            TableExecButtonBar tebb = new TableExecButtonBar();
//            tebb.addButton((TableExecButton) addResourceButton(pd));
            tebb.addButton(new TableExecButton(messages.getString("btnParticipants"), "icons/users.png", pd, this, Constants.ACTION_EDIT_PARTICIPANTS));
            tebb.addButton(new TableExecButton(messages.getString("btnUI"), "icons/settings.png", pd, this, Constants.ACTION_ADD_UI));
            tebb.addButton(new TableExecButton(messages.getString("btnDeleteInstances"), "icons/document-delete.png", pd, this, Constants.ACTION_DELETE_INSTANCES));
            tebb.addButton(new TableExecButton(messages.getString("btnDeteleProcessAndInstances"), "icons/cancel.png", pd, this, Constants.ACTION_DELETE_PROCESS_AND_INSTANCES));
            woItem.getItemProperty("actions").setValue(tebb);
        }
        table.setSortContainerPropertyId("name");
        table.setSortAscending(false);
        table.sort();
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            TableExecButton execBtn = (TableExecButton) event.getButton();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE_PROCESS_AND_INSTANCES)) {
                try {
                    PackageDefinition pd = (PackageDefinition) execBtn.getTableValue();
                    bpmModule.deletePackage(pd);
                    refreshTable();
                    getWindow().showNotification("", messages.getString("deletedSuccessfull"), Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception ex) {
                    showError(ex.toString());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_DELETE_INSTANCES)) {
                try {
                    PackageDefinition pd = (PackageDefinition) execBtn.getTableValue();
                    bpmModule.deleteAllProcessInstances(pd);
                    refreshTable();
                    getWindow().showNotification("", messages.getString("deletedSuccessfull"), Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_EDIT_PARTICIPANTS)) {
                try {
                    PackageDefinition pd = (PackageDefinition) execBtn.getTableValue();
                    ProcessDefinition processDefinition = bpmModule.getProcessDefinition(pd);
                    ProcessACLWindow processACLWindow = new ProcessACLWindow(processDefinition.getUUID().toString());
                    processACLWindow.exec();
                    getApplication().getMainWindow().addWindow(processACLWindow);
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_ADD_UI)) {
                try {
                    PackageDefinition pd = (PackageDefinition) execBtn.getTableValue();
                    ProcessDefinition processDefinition = bpmModule.getProcessDefinition(pd);
                    ProcessUIWindow processUIWindow = new ProcessUIWindow(processDefinition.getUUID().toString());
                    processUIWindow.exec();
                    getApplication().getMainWindow().addWindow(processUIWindow);
                } catch (Exception ex) {
                    showMessageWindow(ex.getMessage(), MessageWindow.ERROR_STYLE);
                }
            }
        }
    }

    public void uploadSucceeded(SucceededEvent event) {
        try {
            byte[] readData = new byte[new Long(event.getLength()).intValue()];
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            int i = fis.read(readData);
            fis.close();
            Map<String, ProcessDefinition> deployResult = new HashMap();
            if (this.fileType.equals(FILE_BAR)) {
                deployResult = bpmModule.deploy(readData, bpmModule.BAR);
            } else if (this.fileType.equals(FILE_XPDL)) {
                deployResult = bpmModule.deploy(readData, bpmModule.XPDL);
            } else if (this.fileType.equals(FILE_JAR)) {
                FileOutputStream fos = new FileOutputStream(new File(Constants.UI_LIBS_PATH, this.originalFilename));
                fos.write(readData);
                fos.close();
                ProcessBaseClassLoader.getCurrent().addFile(Constants.UI_LIBS_PATH + File.separator + this.originalFilename);
            }
            file.delete();
            for (ProcessDefinition pd : deployResult.values()) {
                getWindow().showNotification("Загружен : ", pd.getDescription(), Notification.TYPE_HUMANIZED_MESSAGE);
            }
            refreshTable();
        } catch (Exception ex) {
            Logger.getLogger(ProcessDefinitionsPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            showMessageWindow(ex.getMessage(), MessageWindow.ERROR_STYLE);
        }
    }

    public void uploadFailed(FailedEvent event) {
        showError(event.getReason().getMessage());
    }

    public OutputStream receiveUpload(
            String filename, String MIMEType) {
        this.originalFilename = filename;
        this.filename = UUID.randomUUID().toString();
        String[] fileNameParts = originalFilename.split("\\.");
        this.fileExt = fileNameParts.length > 0 ? fileNameParts[fileNameParts.length - 1] : null;
        if (fileExt.equalsIgnoreCase("bar")) {
            this.fileType = FILE_BAR;
        } else if (fileExt.equalsIgnoreCase("xpdl")) {
            this.fileType = FILE_XPDL;
        } else if (fileExt.equalsIgnoreCase("jar")) {
            this.fileType = FILE_JAR;
        }
        FileOutputStream fos = null;
        file = new File(this.filename);
        try {
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            Logger.getLogger(ProcessDefinitionsPanel.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
        return fos;
    }
}
