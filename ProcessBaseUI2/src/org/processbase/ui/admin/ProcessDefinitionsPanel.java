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
package org.processbase.ui.admin;

import com.vaadin.data.Item;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
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
import java.util.Date;
import java.util.Set;
import java.util.UUID;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.processbase.ui.util.Constants;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.processbase.ui.template.PbColumnGenerator;

/**
 *
 * @author marat gubaidullin
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
    public static String FILE_BAR = "FILE_BAR";
    public static String FILE_JAR = "FILE_JAR";
    private String fileType = null;

    public ProcessDefinitionsPanel(PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
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
        table.setColumnWidth("version", 50);
        table.addContainerProperty("deployedBy", String.class, null, messages.getString("tableCaptionDeployedBy"), null, null);
        table.addContainerProperty("deployedDate", Date.class, null, messages.getString("tableCaptionDeployedDate"), null, null);
        table.addGeneratedColumn("deployedDate", new PbColumnGenerator());
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 85);
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            Set<ProcessDefinition> pds = bpmModule.getProcesses();
            for (ProcessDefinition pd : pds) {
                Item woItem = table.addItem(pd);
                woItem.getItemProperty("UUID").setValue(pd.getUUID());
                woItem.getItemProperty("name").setValue(pd.getLabel());
                woItem.getItemProperty("version").setValue(pd.getVersion());
                woItem.getItemProperty("deployedBy").setValue(pd.getDeployedBy());
                woItem.getItemProperty("deployedDate").setValue(pd.getDeployedDate());
                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(new TableExecButton(messages.getString("btnUI"), "icons/settings.png", pd, this, Constants.ACTION_ADD_UI));
                tebb.addButton(new TableExecButton(messages.getString("btnDeleteInstances"), "icons/folder-delete.png", pd, this, Constants.ACTION_DELETE_INSTANCES));
                tebb.addButton(new TableExecButton(messages.getString("btnDeteleProcessAndInstances"), "icons/cancel.png", pd, this, Constants.ACTION_DELETE_PROCESS_AND_INSTANCES));
                woItem.getItemProperty("actions").setValue(tebb);
            }
            table.setSortContainerPropertyId("name");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            TableExecButton execBtn = (TableExecButton) event.getButton();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE_PROCESS_AND_INSTANCES)) {
                try {
                    ProcessDefinition pd = (ProcessDefinition) execBtn.getTableValue();
                    bpmModule.deleteProcess(pd);
                    refreshTable();
                    getWindow().showNotification("", messages.getString("deletedSuccessfull"), Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_DELETE_INSTANCES)) {
                try {
                    ProcessDefinition pd = (ProcessDefinition) execBtn.getTableValue();
                    bpmModule.deleteAllProcessInstances(pd);
                    refreshTable();
                    getWindow().showNotification("", messages.getString("deletedSuccessfull"), Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_ADD_UI)) {
                try {
                    ProcessDefinition pd = (ProcessDefinition) execBtn.getTableValue();
                    ProcessDefinition processDefinition = bpmModule.getProcessDefinition(pd);
                    ProcessUIWindow processUIWindow = new ProcessUIWindow(processDefinition, getPortletApplicationContext2());
                    processUIWindow.exec();
                    getApplication().getMainWindow().addWindow(processUIWindow);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
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
            if (this.fileType.equals(FILE_BAR)) {
                BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(file);
                ProcessDefinition deployResult = bpmModule.deploy(businessArchive);
                showWarning(messages.getString("processUploaded") + ": " + deployResult.getLabel());
            } else if (this.fileType.equals(FILE_JAR)) {
                bpmModule.deployJar(originalFilename, readData);
                showWarning(messages.getString("jarUploaded") + ": " + originalFilename);
            }
            file.delete();
            refreshTable();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage().substring(0, 1000));
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
        } else if (fileExt.equalsIgnoreCase("jar")) {
            this.fileType = FILE_JAR;
        }
        FileOutputStream fos = null;
        file = new File(this.filename);
        try {
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException ex) {
           ex.printStackTrace();
            return null;
        }
        return fos;
    }
}
