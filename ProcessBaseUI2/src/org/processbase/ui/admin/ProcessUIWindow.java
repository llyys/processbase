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
import java.util.HashMap;
import java.util.UUID;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.ByteArraySource;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.PbTableFieldFactory;
import org.processbase.ui.util.XMLManager;

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
    private Table activitiesTable = new Table();
    private File file;
    private String filename;
    private String originalFilename;
    private String fileExt;
    private BPMModule bpmModule;

    public ProcessUIWindow(ProcessDefinition processDefinition, PortletApplicationContext2 portletApplicationContext2) {
        super(portletApplicationContext2);
        this.processDefinition = processDefinition;
        cancelBtn = new Button(messages.getString("btnCancel"), this);
        applyBtn = new Button(messages.getString("btnSave"), this);
        upload = new Upload("", (Upload.Receiver) this);
        downloadBtn = new Button(messages.getString("btnDownload"), this);
        bpmModule = new BPMModule(this.getCurrentUser().getScreenName());
        initTableUI();
    }

    public void exec() {
        try {
            setCaption(messages.getString("captionActivityUI") + " \"" + processDefinition.getLabel() + "\" " + processDefinition.getVersion());
            setModal(true);
            VerticalLayout layout = (VerticalLayout) this.getContent();
            layout.setMargin(true);
            layout.setSpacing(true);
            layout.setStyleName("white");
//            layout.setSizeUndefined();
            refreshTable();
            activitiesTable.setPageLength(10);
            activitiesTable.setWidth("100%");
            addComponent(activitiesTable);
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
        activitiesTable.addContainerProperty("activityUUID", String.class, null, "UUID", null, null);
        activitiesTable.setColumnWidth("activityUUID", 0);
        activitiesTable.addContainerProperty("activityLabel", String.class, null, messages.getString("tableCaptionActivityName"), null, null);
        activitiesTable.addContainerProperty("url", String.class, null, messages.getString("tabCaptionTaskURL"), null, null);
        activitiesTable.setColumnWidth("url", 300);
        activitiesTable.setTableFieldFactory(new PbTableFieldFactory());
        activitiesTable.setEditable(true);
        activitiesTable.setImmediate(true);
    }

    public void refreshTable() {
        try {
            activitiesTable.removeAllItems();
            for (ActivityDefinition activityDefinition : processDefinition.getActivities()) {
                if (activityDefinition.isTask()) {
                    Item woItem = activitiesTable.addItem(activityDefinition);
                    woItem.getItemProperty("activityUUID").setValue(activityDefinition.getUUID().toString());
                    woItem.getItemProperty("activityLabel").setValue(activityDefinition.getLabel());
                    String url = processDefinition.getAMetaData(activityDefinition.getUUID().toString());
                    woItem.getItemProperty("url").setValue(url != null ? url : new String());
                }
            }
            activitiesTable.setSortContainerPropertyId("activityLabel");
            activitiesTable.setSortAscending(true);
            activitiesTable.sort();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void buttonClick(ClickEvent event) {
        try {
            if (event.getButton().equals(applyBtn)) {
                save();
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
                    XMLManager.createXML("java.util.HashMap", getCurrentTableValues()).getBytes("UTF-8"));
            StreamResource streamResource = new StreamResource(bas, processDefinition.getLabel() + "_" + processDefinition.getVersion() + "_ui.xml", getApplication());
            streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
            streamResource.setMIMEType("mime/xml");
            getWindow().getWindow().open(streamResource, "_new");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HashMap<String, String> getCurrentTableValues() {
        HashMap<String, String> urlMap = new HashMap<String, String>();
        for (Object object : activitiesTable.getContainerDataSource().getItemIds()) {
            ActivityDefinition activityDefinition = (ActivityDefinition) object;
            if (activitiesTable.getItem(object).getItemProperty("url") != null && activitiesTable.getItem(object).getItemProperty("url").toString().length() > 0) {
                urlMap.put(activityDefinition.getUUID().toString(), activitiesTable.getItem(object).getItemProperty("url").toString());
            }
        }
        return urlMap;
    }

    private void save() throws Exception {
        for (Object object : activitiesTable.getContainerDataSource().getItemIds()) {
            ActivityDefinition activityDefinition = (ActivityDefinition) object;
            if (activitiesTable.getItem(object).getItemProperty("url") != null && activitiesTable.getItem(object).getItemProperty("url").toString().length() > 0) {
                bpmModule.addProcessMetaData(processDefinition.getUUID(), activityDefinition.getUUID().toString(), activitiesTable.getItem(object).getItemProperty("url").toString());
            }
        }
    }

    public void uploadSucceeded(SucceededEvent event) {
        try {
            byte[] readData = new byte[new Long(event.getLength()).intValue()];
            FileInputStream fis = new FileInputStream(file);
            int i = fis.read(readData);
            HashMap<String, String> urlMap = (HashMap<String, String>) XMLManager.createObject(new String(readData, "UTF-8"));
            for (String key : urlMap.keySet()) {
                bpmModule.addProcessMetaData(processDefinition.getUUID(), key, urlMap.get(key));
            }
            fis.close();
            file.delete();
            refreshTable();
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
