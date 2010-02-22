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
import com.vaadin.terminal.StreamResource;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
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
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
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

    private ProcessDefinition processDefinition = null;
    private ButtonBar buttons = new ButtonBar();
    private Button cancelBtn = null;
    private Button applyBtn = null;
    private Upload upload = null;
    private Button downloadBtn = null;
    private Table membersTable = new Table();
    private HibernateUtil hutil = new HibernateUtil();
    private File file;
    private String filename;
    private String originalFilename;
    private String fileExt;

    public ProcessUIWindow(ProcessDefinition processDefinition, PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
        this.processDefinition = processDefinition;
        cancelBtn = new Button(messages.getString("btnCancel"), this);
        applyBtn = new Button(messages.getString("btnSave"), this);
        upload = new Upload("", (Upload.Receiver) this);
        downloadBtn = new Button(messages.getString("btnDownload"), this);
        initTableUI();
    }

    public void exec() {
        try {
            setCaption(messages.getString("captionActivityUI") + " \"" + processDefinition.getLabel() + "\" " + processDefinition.getVersion());
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
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void initTableUI() {
        membersTable.addContainerProperty("id", String.class, null, "id", null, null);
        membersTable.setColumnWidth("id", 0);
        membersTable.addContainerProperty("activityUUID", String.class, null, "UUID", null, null);
        membersTable.setColumnWidth("activityUUID", 0);
        membersTable.addContainerProperty("activityLabel", String.class, null, messages.getString("tableCaptionActivityName"), null, null);
        membersTable.addContainerProperty("isStart", String.class, null, messages.getString("tabCaptionIsStart"), null, null);
        membersTable.setColumnWidth("isStart", 30);
        membersTable.addContainerProperty("uiClass", String.class, null, messages.getString("tabCaptionTaskURL"), null, null);
        membersTable.setColumnWidth("uiClass", 300);
        membersTable.addContainerProperty("isMobile", String.class, null, messages.getString("tabCaptionIsMobile"), null, null);
        membersTable.setColumnWidth("isMobile", 30);
        membersTable.addContainerProperty("mobileUiClass", String.class, null, messages.getString("tabCaptionMobileUIClass"), null, null);
        membersTable.setColumnWidth("mobileUiClass", 300);
        membersTable.setTableFieldFactory(new PbTableFieldFactory());
        membersTable.setEditable(true);
        membersTable.setImmediate(true);
    }

    public void refreshTable() {
        try {
            membersTable.removeAllItems();
            ArrayList<PbActivityUi> pbActivityUis = hutil.findProcessUis(processDefinition.getUUID().getValue());
            for (PbActivityUi pbActivityUi : pbActivityUis) {
                Item woItem = membersTable.addItem(pbActivityUi.getActivityUuid());
                woItem.getItemProperty("id").setValue(pbActivityUi.getId());
                woItem.getItemProperty("activityUUID").setValue(pbActivityUi.getActivityUuid());
                woItem.getItemProperty("activityLabel").setValue(pbActivityUi.getActivityLabel());
                woItem.getItemProperty("uiClass").setValue(pbActivityUi.getUiClass());
                woItem.getItemProperty("isStart").setValue(pbActivityUi.getIsStart().equals("T") ? messages.getString("Yes") : messages.getString("No"));
                woItem.getItemProperty("mobileUiClass").setValue(pbActivityUi.getMobileUiClass());
                woItem.getItemProperty("isMobile").setValue(pbActivityUi.getIsMobile().equals("T") ? messages.getString("Yes") : messages.getString("No"));

            }
            membersTable.setSortContainerPropertyId("isStart");
            membersTable.setSortAscending(true);
            membersTable.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                hutil.mergeProcessUi(processDefinition.getUUID().getValue(), getCurrentTableValues());
                close();
            } else if (event.getButton().equals(downloadBtn)) {
                download();
            } else {
                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    private void download() {
        try {
            ByteArraySource bas = new ByteArraySource(
                    XMLManager.createXML("org.processbase.util.db.PbActivityUi", getCurrentTableValues()).getBytes("UTF-8"));
            StreamResource streamResource = new StreamResource(bas, processDefinition.getUUID() + "_ui.xml", getApplication());
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
            pbActivityUi.setProccessUuid(this.processDefinition.getUUID().getValue());
            pbActivityUi.setActivityUuid(membersTable.getItem(object).getItemProperty("activityUUID").toString());
            pbActivityUi.setActivityLabel(membersTable.getItem(object).getItemProperty("activityLabel").toString());
            pbActivityUi.setUiClass(membersTable.getItem(object).getItemProperty("uiClass").toString());
            pbActivityUi.setIsStart(membersTable.getItem(object).getItemProperty("isStart").toString().equals(messages.getString("Yes")) ? "T" : "F");
            pbActivityUi.setMobileUiClass(membersTable.getItem(object).getItemProperty("mobileUiClass").toString());
            pbActivityUi.setIsMobile(membersTable.getItem(object).getItemProperty("isMobile").toString().equals(messages.getString("Yes")) ? "T" : "F");
            pbActivityUis.add(pbActivityUi);
        }
        return pbActivityUis;
    }

    private void setCurrentTableValues(ArrayList<PbActivityUi> pbActivityUis) {
        for (PbActivityUi pbActivityUi : pbActivityUis) {
            membersTable.getItem(pbActivityUi.getActivityUuid()).getItemProperty("uiClass").setValue(pbActivityUi.getUiClass());
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
}
