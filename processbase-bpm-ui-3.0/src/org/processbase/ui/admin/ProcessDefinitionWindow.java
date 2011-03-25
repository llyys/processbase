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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.Processbase;
import org.processbase.ui.template.ButtonBar;
import org.processbase.ui.template.ByteArraySource;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.PbTableFieldFactory;
import org.processbase.ui.template.ConfirmDialog;

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
    private VerticalLayout v1 = new VerticalLayout();
    private VerticalLayout v2 = new VerticalLayout();

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

            v1.setMargin(true, false, false, false);
            v1.setSizeFull();

            if (processDefinition.getLabel() != null) {
                Label pdLabel = new Label("<b>" + processDefinition.getLabel() + "</b>");
                pdLabel.setContentMode(Label.CONTENT_XHTML);
                v1.addComponent(pdLabel);
            }

            if (processDefinition.getDescription() != null) {
                Label pdDescription = new Label(processDefinition.getDescription());
                pdDescription.setContentMode(Label.CONTENT_XHTML);
                v1.addComponent(pdDescription);
                v1.setExpandRatio(pdDescription, 1);
            }

            tabSheet.addTab(v1, ((Processbase) getApplication()).getMessages().getString("tabDescription"), null);

            activitiesTable.setSizeFull();

            v2.setMargin(true, false, false, false);
            v2.addComponent(activitiesTable);
            v2.setSizeFull();
            tabSheet.addTab(v2, ((Processbase) getApplication()).getMessages().getString("tabCustomUI"), null);

            tabSheet.setStyleName(Reindeer.TABSHEET_MINIMAL);
            tabSheet.setSizeFull();
            tabSheet.addListener((TabSheet.SelectedTabChangeListener) this);
            layout.addComponent(tabSheet);
            layout.setExpandRatio(tabSheet, 1);

            closeBtn = new Button(((Processbase) getApplication()).getMessages().getString("btnClose"), this);
            applyBtn = new Button(((Processbase) getApplication()).getMessages().getString("btnSaveCustomUI"), this);
            deleteAllBtn = new Button(((Processbase) getApplication()).getMessages().getString("btnDeleteAll"), this);
            deleteInstancesBtn = new Button(((Processbase) getApplication()).getMessages().getString("btnDeleteInstances"), this);
            downloadBtn = new Button(((Processbase) getApplication()).getMessages().getString("btnDownload"), this);
            enableBtn = new CheckBox(((Processbase) getApplication()).getMessages().getString("btnEnable"), this);
            archiveBtn = new Button(((Processbase) getApplication()).getMessages().getString("btnArchive"), this);

            deleteAllBtn.setDescription(((Processbase) getApplication()).getMessages().getString("deleteProcessDefinition"));
            buttons.addButton(deleteAllBtn);
            buttons.setComponentAlignment(deleteAllBtn, Alignment.MIDDLE_RIGHT);
            deleteInstancesBtn.setDescription(((Processbase) getApplication()).getMessages().getString("deleteProcessInstances"));
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

//        activitiesTable.addContainerProperty("activityUUID", String.class, null, "UUID", null, null);
//        activitiesTable.setColumnWidth("activityUUID", 0);
            activitiesTable.addContainerProperty("activityLabel", String.class, null, ((Processbase) getApplication()).getMessages().getString("tableCaptionActivityName"), null, null);
            activitiesTable.addContainerProperty("url", String.class, null, ((Processbase) getApplication()).getMessages().getString("tabCaptionTaskURL"), null, null);
            activitiesTable.setColumnWidth("url", 300);
            activitiesTable.setTableFieldFactory(new PbTableFieldFactory());
            activitiesTable.setEditable(true);
            activitiesTable.setImmediate(true);

            refreshTable();
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    public void refreshTable() {
        try {
            activitiesTable.removeAllItems();
            // process level Custom UI
            Item woItem = activitiesTable.addItem(processDefinition);
            woItem.getItemProperty("activityLabel").setValue(processDefinition.getLabel() != null ? processDefinition.getLabel() : processDefinition.getName());
            String url = processDefinition.getAMetaData(processDefinition.getUUID().toString());
            woItem.getItemProperty("url").setValue(url != null ? url : new String());
            // activity level Custom UI
            for (ActivityDefinition activityDefinition : processDefinition.getActivities()) {
                if (activityDefinition.isTask()) {
                    woItem = activitiesTable.addItem(activityDefinition);
//                    woItem.getItemProperty("activityUUID").setValue(activityDefinition.getUUID().toString());
                    woItem.getItemProperty("activityLabel").setValue(activityDefinition.getLabel());
                    url = processDefinition.getAMetaData(activityDefinition.getUUID().toString());
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
            } else if (event.getButton().equals(deleteAllBtn)) {
                deleteAll();
                close();
            } else if (event.getButton().equals(deleteInstancesBtn)) {
                deleteInstances();
            } else if (event.getButton().equals(enableBtn)) {
                enableProcess();
            } else if (event.getButton().equals(archiveBtn)) {
                archiveProcess();
            } else {
                close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showError(ex.getMessage());
        }
    }

    private void enableProcess() {
        final Processbase processbase = ((Processbase) getApplication());
        final PbWindow mainWindow = (PbWindow) getApplication().getMainWindow();
        ConfirmDialog.show(getApplication().getMainWindow(),
                ((Processbase) getApplication()).getMessages().getString("windowCaptionConfirm"),
                enableBtn.booleanValue()
                ? ((Processbase) getApplication()).getMessages().getString("questionEnableProcessDefinition")
                : ((Processbase) getApplication()).getMessages().getString("questionDisableProcessDefinition"),
                ((Processbase) getApplication()).getMessages().getString("btnYes"),
                ((Processbase) getApplication()).getMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                if (enableBtn.booleanValue()) {
                                    processbase.getBpmModule().enableProcessDefinitions(processDefinition.getUUID());
                                } else {
                                    processbase.getBpmModule().disableProcessDefinitions(processDefinition.getUUID());
                                }
                                mainWindow.showInformation(processbase.getMessages().getString("executedSuccessfully"));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                mainWindow.showError(ex.getMessage());
                            }
                        }
                    }
                });
    }

    private void archiveProcess() {
        final Processbase processbase = ((Processbase) getApplication());
        final PbWindow mainWindow = (PbWindow) getApplication().getMainWindow();
        ConfirmDialog.show(getApplication().getMainWindow(),
                ((Processbase) getApplication()).getMessages().getString("windowCaptionConfirm"),
                ((Processbase) getApplication()).getMessages().getString("questionArchiveProcessDefinition"),
                ((Processbase) getApplication()).getMessages().getString("btnYes"),
                ((Processbase) getApplication()).getMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                processbase.getBpmModule().archiveProcessDefinitions(processDefinition.getUUID());
                                mainWindow.showInformation(processbase.getMessages().getString("executedSuccessfully"));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                mainWindow.showError(ex.getMessage());
                            }
                        }
                    }
                });
    }

    private void deleteAll() {
        final Processbase processbase = ((Processbase) getApplication());
        final PbWindow mainWindow = (PbWindow) getApplication().getMainWindow();
        ConfirmDialog.show(mainWindow,
                ((Processbase) getApplication()).getMessages().getString("windowCaptionConfirm"),
                ((Processbase) getApplication()).getMessages().getString("questionDeleteProcessAndInstances"),
                ((Processbase) getApplication()).getMessages().getString("btnYes"),
                ((Processbase) getApplication()).getMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                processbase.getBpmModule().deleteProcess(processDefinition);
                                mainWindow.showInformation(processbase.getMessages().getString("executedSuccessfully"));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                mainWindow.showError(ex.getMessage());
                            }
                        }
                    }
                });
    }

    private void deleteInstances() {
        final Processbase processbase = ((Processbase) getApplication());
        final PbWindow mainWindow = (PbWindow) getApplication().getMainWindow();
        ConfirmDialog.show(mainWindow,
                ((Processbase) getApplication()).getMessages().getString("windowCaptionConfirm"),
                ((Processbase) getApplication()).getMessages().getString("questionDeleteInstances"),
                ((Processbase) getApplication()).getMessages().getString("btnYes"),
                ((Processbase) getApplication()).getMessages().getString("btnNo"),
                new ConfirmDialog.Listener() {

                    public void onClose(ConfirmDialog dialog) {
                        if (dialog.isConfirmed()) {
                            try {
                                processbase.getBpmModule().deleteAllProcessInstances(processDefinition);
                                mainWindow.showInformation(processbase.getMessages().getString("executedSuccessfully"));
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
//            ByteArraySource bas = new ByteArraySource(
//                    XMLManager.createXML("java.util.HashMap", getCurrentTableValues()).getBytes("UTF-8"));
//            StreamResource streamResource = new StreamResource(bas, processDefinition.getLabel() + "_" + processDefinition.getVersion() + "_ui.xml", getApplication());
//            streamResource.setCacheTime(50000); // no cache (<=0) does not work with IE8
//            streamResource.setMIMEType("mime/xml");
//            getWindow().getWindow().open(streamResource, "_new");
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
            if (object instanceof ProcessDefinition) { // process level Custom UI
                ProcessDefinition pd = (ProcessDefinition) object;
                if (activitiesTable.getItem(object).getItemProperty("url") != null && !activitiesTable.getItem(object).getItemProperty("url").toString().isEmpty()) {
                    ((Processbase) getApplication()).getBpmModule().addProcessMetaData(processDefinition.getUUID(), pd.getUUID().toString(), activitiesTable.getItem(object).getItemProperty("url").toString());
                } else if (activitiesTable.getItem(object).getItemProperty("url") != null && activitiesTable.getItem(object).getItemProperty("url").toString().isEmpty()) {
                    ((Processbase) getApplication()).getBpmModule().deleteProcessMetaData(processDefinition.getUUID(), pd.getUUID().toString());
                }

            } else if (object instanceof ActivityDefinition) { // activity level Custom UI
                ActivityDefinition activityDefinition = (ActivityDefinition) object;
                if (activitiesTable.getItem(object).getItemProperty("url") != null && !activitiesTable.getItem(object).getItemProperty("url").toString().isEmpty()) {
                    ((Processbase) getApplication()).getBpmModule().addProcessMetaData(processDefinition.getUUID(), activityDefinition.getUUID().toString(), activitiesTable.getItem(object).getItemProperty("url").toString());
                } else if (activitiesTable.getItem(object).getItemProperty("url") != null && activitiesTable.getItem(object).getItemProperty("url").toString().isEmpty()) {
                    ((Processbase) getApplication()).getBpmModule().deleteProcessMetaData(processDefinition.getUUID(), activityDefinition.getUUID().toString());
                }
            }
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
        if (event.getTabSheet().getSelectedTab().equals(v2)) {
            applyBtn.setVisible(true);
        } else {
            applyBtn.setVisible(false);
        }
    }
}
