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
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.processbase.util.Constants;
import org.processbase.ui.template.TableExecButton;
import org.processbase.ui.template.TableExecButtonBar;
import org.processbase.ui.template.TablePanel;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.processbase.ProcessBase;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.template.MessageWindow;
import org.processbase.ui.template.PbColumnGenerator;
import org.processbase.ui.template.TableComboBox;
import org.processbase.util.ProcessBaseClassLoader;
import org.processbase.util.db.HibernateUtil;
import org.processbase.util.db.PbSection;

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
    protected BPMModule bpmModule = ((ProcessBase) getApplication()).getCurrent().getBpmModule();
    private ArrayList<PbSection> pbSections = null;
    private HibernateUtil hutil = new HibernateUtil();

    public ProcessDefinitionsPanel() {
        super();
        upload.setButtonCaption(messages.getString("btnUpload"));
        upload.setImmediate(true);
        upload.addListener((Upload.SucceededListener) this);
        upload.addListener((Upload.FailedListener) this);
        buttonBar.addComponent(upload, 1);
        buttonBar.setComponentAlignment(upload, Alignment.MIDDLE_LEFT);
        pbSections = hutil.findPbSections();
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
        table.addContainerProperty("section", TableComboBox.class, null, messages.getString("tableCaptionSection"), null, null);
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
                woItem.getItemProperty("section").setValue(getProcessSection(pd));
                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(new TableExecButton(messages.getString("btnParticipants"), "icons/users.png", pd, this, Constants.ACTION_EDIT_PARTICIPANTS));
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
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_DELETE_INSTANCES)) {
                try {
                    ProcessDefinition pd = (ProcessDefinition) execBtn.getTableValue();
                    bpmModule.deleteAllProcessInstances(pd);
                    refreshTable();
                    getWindow().showNotification("", messages.getString("deletedSuccessfull"), Notification.TYPE_HUMANIZED_MESSAGE);
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_EDIT_PARTICIPANTS)) {
                try {
                    ProcessDefinition pd = (ProcessDefinition) execBtn.getTableValue();
                    ProcessDefinition processDefinition = bpmModule.getProcessDefinition(pd);
                    ProcessACLWindow processACLWindow = new ProcessACLWindow(processDefinition.getUUID().toString());
                    processACLWindow.exec();
                    getApplication().getMainWindow().addWindow(processACLWindow);
                } catch (Exception ex) {
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_ADD_UI)) {
                try {
                    ProcessDefinition pd = (ProcessDefinition) execBtn.getTableValue();
                    ProcessDefinition processDefinition = bpmModule.getProcessDefinition(pd);
                    ProcessUIWindow processUIWindow = new ProcessUIWindow(processDefinition);
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
            if (this.fileType.equals(FILE_BAR)) {
                BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(file);
                ProcessDefinition deployResult = bpmModule.deploy(businessArchive);
                showWarning(messages.getString("processUploaded") + ": " + deployResult.getLabel());
            } else if (this.fileType.equals(FILE_JAR)) {
                FileOutputStream fos = new FileOutputStream(new File(Constants.UI_LIBS_PATH, this.originalFilename));
                fos.write(readData);
                fos.close();
                ProcessBaseClassLoader.getCurrent().addFile(Constants.UI_LIBS_PATH + File.separator + this.originalFilename);
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

    private TableComboBox getProcessSection(final ProcessDefinition pd) {
        TableComboBox result = new TableComboBox(pd);
        result.addContainerProperty("id", Long.class, null);
        result.addContainerProperty("name", String.class, null);
        try {
            final ProcessDefinition processDefinition = bpmModule.getProcessDefinition(pd);
            PbSection currentSection = hutil.findPbSection(processDefinition.getUUID().toString());
            for (PbSection section : pbSections) {
                Item item = result.addItem(section);
                item.getItemProperty("id").setValue(section.getId());
                item.getItemProperty("name").setValue(section.getSectionName() + " - " + section.getSectionDesc());
                if (currentSection != null && currentSection.getId() == section.getId()) {
                    result.setValue(section);
                }
            }
            result.setItemCaptionPropertyId("name");
            result.setNewItemsAllowed(false);
            result.setWidth("100%");
            result.setImmediate(true);
            result.addListener(new Property.ValueChangeListener() {

                public void valueChange(ValueChangeEvent event) {
                    hutil.setPbProcessSection(processDefinition.getUUID().toString(), (PbSection) event.getProperty().getValue());
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result;
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
        } catch (final java.io.FileNotFoundException e) {
            Logger.getLogger(ProcessDefinitionsPanel.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
        return fos;
    }
}
