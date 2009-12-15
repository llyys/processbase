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
import com.vaadin.terminal.StreamResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.ByteArraySource;
import org.processbase.util.db.HibernateUtil;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.PbTableFieldFactory;
import org.processbase.util.XMLManager;
import org.processbase.util.db.PbActivityUi;

/**
 *
 * @author mgubaidullin
 */
public class ProcessUIWindow extends PbWindow implements
        ClickListener,
        Upload.SucceededListener,
        Upload.FailedListener,
        Upload.Receiver {

    private String processUUID = null;
    private ButtonBar buttons = new ButtonBar();
    private Button cancelBtn = new Button(messages.getString("btnCancel"), this);
    private Button applyBtn = new Button(messages.getString("btnSave"), this);
    private Upload upload = new Upload("", (Upload.Receiver) this);
    private Button downloadBtn = new Button(messages.getString("btnDownload"), this);
    private Table membersTable = new Table();
    private HibernateUtil hutil = new HibernateUtil();
    private File file;
    private String filename;
    private String originalFilename;
    private String fileExt;

    public ProcessUIWindow(String processUUID) {
        super();
        this.processUUID = processUUID;
        initTableUI();
    }

    public void exec() {
        try {
            setCaption(messages.getString("captionActivityUI") + " \"" + processUUID + "\"");
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
//            layout.setSizeUndefined();
            refreshTable();
            membersTable.setPageLength(10);
            membersTable.setWidth("100%");
            addComponent(membersTable);
            upload.setButtonCaption(messages.getString("btnUpload"));
            upload.setImmediate(true);
            upload.addListener((Upload.SucceededListener) this);
            upload.addListener((Upload.FailedListener) this);
            buttons.addComponent(upload);
            buttons.setComponentAlignment(upload, Alignment.TOP_RIGHT);
            buttons.addButton(downloadBtn);
            buttons.setComponentAlignment(downloadBtn, Alignment.TOP_RIGHT);
            buttons.addButton(applyBtn);
            buttons.setComponentAlignment(applyBtn, Alignment.TOP_RIGHT);
            buttons.addButton(cancelBtn);
            buttons.setComponentAlignment(cancelBtn, Alignment.TOP_RIGHT);
            buttons.setMargin(false);
            addComponent(buttons);
            setWidth("70%");
            setResizable(false);
        } catch (Exception ex) {
            Logger.getLogger(ProcessUIWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    public void initTableUI() {
        membersTable.addContainerProperty("id", String.class, null, "id", null, null);
        membersTable.setColumnWidth("id", 0);
        membersTable.addContainerProperty("activityUUID", String.class, null, "UUID", null, null);
        membersTable.setColumnWidth("activityUUID", 0);
        membersTable.addContainerProperty("activityDescription", String.class, null, messages.getString("tableCaptionActivityName"), null, null);
        membersTable.addContainerProperty("uiClass", String.class, null, messages.getString("tabCaptionUIClass"), null, null);
        membersTable.setColumnWidth("uiClass", 300);
        membersTable.setTableFieldFactory(new PbTableFieldFactory());
        membersTable.setEditable(true);
        membersTable.setImmediate(true);
    }

    public void refreshTable() {
        try {
            membersTable.removeAllItems();
            ArrayList<PbActivityUi> pbActivityUis = hutil.findProcessUis(processUUID);
            for (PbActivityUi pbActivityUi : pbActivityUis) {
                Item woItem = membersTable.addItem(pbActivityUi.getActivityUuid());
                woItem.getItemProperty("id").setValue(pbActivityUi.getId());
                woItem.getItemProperty("activityUUID").setValue(pbActivityUi.getActivityUuid());
                woItem.getItemProperty("activityDescription").setValue(pbActivityUi.getActivityDescription());
                woItem.getItemProperty("uiClass").setValue(pbActivityUi.getUiClass());
            }
//            membersTable.setSortContainerPropertyId("activityUUID");
//            membersTable.setSortAscending(false);
//            membersTable.sort();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                hutil.mergeProcessUi(processUUID, getCurrentTableValues());
                close();
            } else if (event.getButton().equals(downloadBtn)) {
                download();
            } else {
                close();
            }
        } catch (Exception ex) {
            Logger.getLogger(ProcessUIWindow.class.getName()).log(Level.SEVERE, ex.getMessage());
            showError(ex.getMessage());
        }
    }

    private void download() {
        try {
            ByteArraySource bas = new ByteArraySource(
                    XMLManager.createXML("org.processbase.util.db.PbActivityUi", getCurrentTableValues()).getBytes("UTF-8"));
            StreamResource streamResource = new StreamResource(bas, this.processUUID + "_ui.xml", getApplication());
            streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
            streamResource.setMIMEType("mime/xml");
            getWindow().getWindow().open(streamResource, "_new");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<PbActivityUi> getCurrentTableValues() {
        ArrayList<PbActivityUi> pbActivityUis = new ArrayList<PbActivityUi>();
        for (Object object : membersTable.getContainerDataSource().getItemIds()) {
            PbActivityUi pbActivityUi = new PbActivityUi();
            pbActivityUi.setProccessUuid(this.processUUID);
            pbActivityUi.setActivityUuid(membersTable.getItem(object).getItemProperty("activityUUID").toString());
            pbActivityUi.setActivityDescription(membersTable.getItem(object).getItemProperty("activityDescription").toString());
            pbActivityUi.setUiClass(membersTable.getItem(object).getItemProperty("uiClass").toString());
            pbActivityUis.add(pbActivityUi);
        }
        return pbActivityUis;
    }

    private void setCurrentTableValues(ArrayList<PbActivityUi> pbActivityUis) {
        for (PbActivityUi pbActivityUi : pbActivityUis) {
            membersTable.getItem(pbActivityUi.getActivityUuid())
                    .getItemProperty("uiClass").setValue(pbActivityUi.getUiClass());
        }
    }

    public void uploadSucceeded(SucceededEvent event) {
        try {
            byte[] readData = new byte[new Long(event.getLength()).intValue()];
            FileInputStream fis = new FileInputStream(file);
            int i = fis.read(readData);
            ArrayList<PbActivityUi> pbActivityUis = (ArrayList<PbActivityUi>) XMLManager.createObject(new String(readData, "UTF-8"));
            setCurrentTableValues(pbActivityUis);
            fis.close();
            file.delete();
        } catch (Exception ex) {
            Logger.getLogger(ProcessDefinitionsPanel.class.getName()).log(Level.SEVERE, ex.getMessage());
            getWindow().showNotification(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    public void uploadFailed(FailedEvent event) {
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
            Logger.getLogger(ProcessDefinitionsPanel.class.getName()).log(Level.SEVERE, e.getMessage());
            return null;
        }
        return fos;
    }
}
