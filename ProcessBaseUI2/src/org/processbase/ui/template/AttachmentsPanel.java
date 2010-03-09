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

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.vaadin.data.Item;
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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import org.processbase.core.Constants;
import org.processbase.ui.util.DocumentLibraryFile;
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
    private String filename;
    private String[] fileTypes = null;
    private boolean add = true;
    private boolean delete = true;
    private boolean edit = true;
    private boolean types = true;
    private DocumentLibraryUtil docUtil = null;
    private ArrayList<DocumentLibraryFile> filesToDelete = new ArrayList<DocumentLibraryFile>();

    public AttachmentsPanel(PortletApplicationContext2 portletApplicationContext2, String processUUID, String[] fileTypes, boolean add, boolean delete, boolean edit) {
        super(portletApplicationContext2);
        this.processUUID = processUUID;
        this.fileTypes = fileTypes;
        this.add = add;
        this.delete = delete;
        this.edit = edit;
        this.types = (fileTypes != null && fileTypes.length > 0);
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
        if (types) {
            table.setVisibleColumns(new Object[]{"id", "type", "desc", "name", "actions"});
        } else {
            table.setVisibleColumns(new Object[]{"id", "desc", "name", "actions"});
        }
    }

    @Override
    public void refreshTable() {
        try {
            table.removeAllItems();
            for (DLFileEntry fileEntry : docUtil.getProcessFiles()) {
                DocumentLibraryFile dlf = new DocumentLibraryFile(fileEntry.getFileEntryId(),
                        fileEntry.getTitleWithExtension(), null, fileEntry.getDescription(), null, false);
                Item woItem = table.addItem(dlf);
                woItem.getItemProperty("id").setValue(dlf.getId());
                if (types) {
                    woItem.getItemProperty("type").setValue(getTypeComponent(dlf, false));
                }
                woItem.getItemProperty("name").setValue(dlf.getName());
                woItem.getItemProperty("desc").setValue(getDescComponent(dlf, false));
                TableExecButtonBar tebb = new TableExecButtonBar();
                if (delete) {
                    tebb.addButton(new TableExecButton(messages.getString("btnDelete"), "icons/cancel.png", dlf, this, Constants.ACTION_DELETE));
                }
                tebb.addButton(new TableExecButton(messages.getString("btnOpen"), "icons/document.png", dlf, this, Constants.ACTION_OPEN));
                woItem.getItemProperty("actions").setValue(tebb);
            }
            table.setSortContainerPropertyId("id");
            table.setSortAscending(false);
            table.sort();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    private Component getTypeComponent(DocumentLibraryFile dlf, boolean isNew) {
        if (edit || isNew) {
            TableComboBox fileTypeComboBox = new TableComboBox(dlf);
            fileTypeComboBox.addContainerProperty("id", String.class, null);
            fileTypeComboBox.addContainerProperty("name", String.class, null);
            fileTypeComboBox.setContainerDataSource(fileTypes);
            fileTypeComboBox.setItemCaptionPropertyId("name");
            fileTypeComboBox.setNewItemsAllowed(false);
            fileTypeComboBox.setWidth("100%");
            fileTypeComboBox.setValue(dlf.getType());
            return fileTypeComboBox;
        } else {
            Label label = new Label(dlf.getType());
            return label;
        }
    }

    private Component getDescComponent(final DocumentLibraryFile dlf, boolean isNew) {
        if (edit || isNew) {
            TextField descTextField = new TextField();
            descTextField.setValue(dlf.getDescription());
            descTextField.setNullRepresentation("");
            descTextField.setWidth("100%");
            return descTextField;
        } else {
            Label label = new Label(dlf.getDescription());
            return label;
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        if (event.getButton() instanceof TableExecButton) {
            TableExecButton execBtn = (TableExecButton) event.getButton();
            DocumentLibraryFile dlf = (DocumentLibraryFile) execBtn.getTableValue();
            if (execBtn.getAction().equals(Constants.ACTION_DELETE)) {
                try {
                    if (!dlf.isTemp()) {
                        filesToDelete.add(dlf);
                    }
                    table.removeItem(dlf);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            } else if (execBtn.getAction().equals(Constants.ACTION_OPEN)) {
                try {
                    byte[] b = null;
                    if (dlf.isTemp()) {
                        FileInputStream fis = new FileInputStream(dlf.getFile());
                        b = new byte[Long.valueOf(dlf.getFile().length()).intValue()];
                        fis.read(b);
                    } else {
                        b = docUtil.getFileBody(dlf.getId());
                    }
                    ByteArraySource bas = new ByteArraySource(b);
                    StreamResource streamResource = new StreamResource(bas, dlf.getName(), getApplication());
                    streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
                    getWindow().getWindow().open(streamResource, "_new");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showError(ex.getMessage());
                }
            }
        }
    }

    private void afterDownload(DocumentLibraryFile dlf) {
        Item woItem = table.addItem(dlf);
        woItem.getItemProperty("id").setValue(dlf.getId());
        if (types) {
            woItem.getItemProperty("type").setValue(getTypeComponent(dlf, true));
        }
        woItem.getItemProperty("name").setValue(dlf.getName());
        woItem.getItemProperty("desc").setValue(getDescComponent(dlf, true));
        TableExecButtonBar tebb = new TableExecButtonBar();
        tebb.addButton(new TableExecButton(messages.getString("btnDelete"), "icons/cancel.png", dlf, this, Constants.ACTION_DELETE));
        tebb.addButton(new TableExecButton(messages.getString("btnOpen"), "icons/document.png", dlf, this, Constants.ACTION_OPEN));
        woItem.getItemProperty("actions").setValue(tebb);
    }

    public void uploadSucceeded(SucceededEvent event) {
        try {
            DocumentLibraryFile dlf = new DocumentLibraryFile(0, filename, null, filename, file, true);
            afterDownload(dlf);
//            file.delete();
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
            this.filename = filename;
            file = new File(filename);
            fos = new FileOutputStream(file);
        } catch (final java.io.FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return fos;
    }

    public void save(String processUUID) {
        docUtil = new DocumentLibraryUtil(this.getCurrentUser(), processUUID);
        try {
            // save files
            for (Object object : table.getItemIds()) {
                DocumentLibraryFile dlf = (DocumentLibraryFile) object;
                if (dlf.isTemp()) {
                    dlf.setDescription(table.getItem(object).getItemProperty("desc").toString());
                    docUtil.addFile(dlf.getName(), dlf.getDescription(), dlf.getFile(), new String[]{});
                } else {
                    if (!dlf.getDescription().equals(table.getItem(object).getItemProperty("desc").toString())) {
                        DLFileEntry fileEntry = docUtil.getFileEntry(dlf.getId());
                        fileEntry.setDescription(dlf.getDescription());
                        docUtil.updateFileEntry(fileEntry);
                    }
                }
            }
            // delete files from Document library
            for (Object object : filesToDelete) {
                DocumentLibraryFile dlf = (DocumentLibraryFile) object;
                docUtil.deleteFile(dlf.getId());
            }
        } catch (PortalException ex) {
            ex.printStackTrace();
        } catch (SystemException ex) {
            ex.printStackTrace();
        }
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
