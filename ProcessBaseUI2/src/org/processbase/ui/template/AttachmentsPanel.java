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
package org.processbase.ui.template;

import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import org.processbase.core.Constants;
import org.processbase.ui.util.DocumentLibraryUtil;

/**
 *
 * @author mgubaidullin
 */
public class AttachmentsPanel extends TablePanel
        implements Button.ClickListener, Upload.SucceededListener, Upload.FailedListener, Upload.Receiver {

    private String processUUID = null;
    private Upload upload = new Upload("", this);
    private File file;
    private String MIMEType;
    private String filename;
    private String[] fileTypes = null;
    private boolean add = true;
    private boolean delete = true;
    private boolean edit = true;
    private boolean types = true;
    private DocumentLibraryUtil docUtil = null;

    public AttachmentsPanel(PortletApplicationContext2 portletApplicationContext2, String processUUID, String[] fileTypes, boolean add, boolean delete, boolean edit, boolean types) {
        super(portletApplicationContext2);
        this.processUUID = processUUID;
        this.fileTypes = fileTypes;
        this.add = add;
        this.delete = delete;
        this.edit = edit;
        this.types = types;
        upload.setButtonCaption(messages.getString("btnUpload"));
        upload.addListener((Upload.SucceededListener) this);
        upload.addListener((Upload.FailedListener) this);
        upload.setImmediate(true);
        if (!add) {
            this.removeComponent(buttonBar);
        } else {
            buttonBar.removeButton(refreshBtn);
            buttonBar.addComponent(upload);
        }
        docUtil = new DocumentLibraryUtil(this.getCurrentUser(), processUUID);
        initTableUI();
    }

    @Override
    public void initTableUI() {
        super.initTableUI();
        table.addContainerProperty("id", String.class, null, "id", null, null);
        table.setColumnWidth("id", 30);
        table.addContainerProperty("type", Component.class, null, messages.getString("tableCaptionSection"), null, null);
        table.setColumnWidth("type", 200);
        table.addContainerProperty("desc", Component.class, null, messages.getString("tableCaptionDescription"), null, null);
        table.addContainerProperty("name", String.class, null, messages.getString("tableCaptionName"), null, null);
        table.setColumnWidth("name", 200);
        table.addContainerProperty("actions", TableExecButtonBar.class, null, messages.getString("tableCaptionActions"), null, null);
        table.setColumnWidth("actions", 50);
        if (types){
            table.setVisibleColumns(new Object[]{"id","type","desc","name","actions"});
        } else{
            table.setVisibleColumns(new Object[]{"id","desc","name","actions"});
        }
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            for (DLFileEntry fileEntry : docUtil.getProcessFiles()) {
                Item woItem = table.addItem(fileEntry);
                TableExecButtonBar tebb = new TableExecButtonBar();
                woItem.getItemProperty("id").setValue(fileEntry.getFileEntryId());
                woItem.getItemProperty("type").setValue(getTypeComponent(fileEntry, false));
                woItem.getItemProperty("name").setValue(fileEntry.getTitleWithExtension());
                woItem.getItemProperty("desc").setValue(getDescComponent(fileEntry, false));
                if (delete) {
                    tebb.addButton(new TableExecButton(messages.getString("btnDelete"), "icons/cancel.png", fileEntry, this, Constants.ACTION_DELETE));
                }
                tebb.addButton(new TableExecButton(messages.getString("btnOpen"), "icons/document.png", fileEntry, this, Constants.ACTION_OPEN));
                woItem.getItemProperty("actions").setValue(tebb);
            }
            table.setSortContainerPropertyId("id");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private Component getTypeComponent(DLFileEntry fileEntry, boolean isNew) {
        if (edit || isNew) {
            TableComboBox fileTypeComboBox = new TableComboBox(fileEntry);
            fileTypeComboBox.addContainerProperty("id", String.class, null);
            fileTypeComboBox.addContainerProperty("name", String.class, null);
            fileTypeComboBox.setContainerDataSource(fileTypes);
            fileTypeComboBox.setItemCaptionPropertyId("name");
            fileTypeComboBox.setNewItemsAllowed(false);
            fileTypeComboBox.setWidth("100%");
            fileTypeComboBox.setValue(fileEntry.getExtraSettingsProperties().getProperty("type"));
            return fileTypeComboBox;
        } else {
            Label label = new Label(fileEntry.getExtraSettingsProperties().getProperty("type"));
            return label;
        }
    }

    private Component getDescComponent(final DLFileEntry fileEntry, boolean isNew) {
        if (edit || isNew) {
            TextField descTextField = new TextField();
            descTextField.setValue(fileEntry.getDescription());
            descTextField.setNullRepresentation("");
            descTextField.setWidth("100%");
            descTextField.addListener(new ValueChangeListener(){

                public void valueChange(ValueChangeEvent event) {
                    fileEntry.setDescription(event.getProperty().getValue().toString());
                    docUtil.updateFile(fileEntry);
                }
            });
            return descTextField;
        } else {
            Label label = new Label(fileEntry.getDescription());
            return label;
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            TableExecButton execBtn = (TableExecButton) event.getButton();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                try {
                    docUtil.deleteFile((DLFileEntry) execBtn.getTableValue());
                    table.removeItem((DLFileEntry) execBtn.getTableValue());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                try {
                    DLFileEntry fileEntry = (DLFileEntry) execBtn.getTableValue();
                    ByteArraySource bas = new ByteArraySource(docUtil.getFileBody(fileEntry));
                    StreamResource streamResource = new StreamResource(bas, fileEntry.getTitleWithExtension(), getApplication());
                    streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
//                    streamResource.setMIMEType(fileEntry.toString());
                    getWindow().getWindow().open(streamResource, "_new");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            }
        }
    }

    private void afterDownload(DLFileEntry fileEntry) {
        Item woItem = table.addItem(fileEntry);
        woItem.getItemProperty("id").setValue(fileEntry.getFileEntryId());
        woItem.getItemProperty("type").setValue(getTypeComponent(fileEntry, true));
        woItem.getItemProperty("name").setValue(fileEntry.getTitleWithExtension());
        woItem.getItemProperty("desc").setValue(getDescComponent(fileEntry, true));
        TableExecButtonBar tebb = new TableExecButtonBar();
        tebb.addButton(new TableExecButton(messages.getString("btnDelete"), "icons/cancel.png", fileEntry, this, Constants.ACTION_DELETE));
        tebb.addButton(new TableExecButton(messages.getString("btnOpen"), "icons/document.png", fileEntry, this, Constants.ACTION_OPEN));
        woItem.getItemProperty("actions").setValue(tebb);
    }

    public void uploadSucceeded(SucceededEvent event) {
        try {
            DLFileEntry fileEntry = docUtil.addFile(filename, filename, file, new String[]{});
            afterDownload(fileEntry);
            file.delete();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void uploadFailed(FailedEvent event) {
        showError(event.getFilename() + " " + event.getReason().getMessage());
    }

    public OutputStream receiveUpload(String filename, String MIMEType) {
        FileOutputStream fos = null;
        try {
            this.MIMEType = MIMEType;
            this.filename = filename;
            file = new File(filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return fos;
    }

    public Table getTable() {
        return table;
    }

    public boolean isAdd() {
        return add;
    }

    public void setAdd(boolean add) {
        this.add = add;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
}
