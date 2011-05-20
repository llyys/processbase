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

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.Table;
import com.vaadin.ui.Upload;
import com.vaadin.ui.Upload.FailedEvent;
import com.vaadin.ui.Upload.SucceededEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Reindeer;
import com.vaadin.ui.themes.Runo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.ow2.bonita.facade.IdentityAPI;
import org.ow2.bonita.facade.def.majorElement.ActivityDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.facade.identity.Group;
import org.ow2.bonita.facade.identity.Membership;
import org.ow2.bonita.facade.identity.Role;
import org.ow2.bonita.facade.privilege.Rule;
import org.ow2.bonita.facade.privilege.Rule.RuleType;
import org.processbase.ui.core.BPMModule;
import org.processbase.ui.core.Constants;
import org.processbase.ui.core.ProcessbaseApplication;
import org.processbase.ui.core.template.ButtonBar;
import org.processbase.ui.core.template.ConfirmDialog;
import org.processbase.ui.core.template.PbTableFieldFactory;
import org.processbase.ui.core.template.PbWindow;
import org.processbase.ui.core.template.TableLinkButton;

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
    private Button addBtn;
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
    private Table tableMembership = new Table();
    private TabSheet tabSheet = new TabSheet();
    private VerticalLayout v1 = new VerticalLayout();
    private VerticalLayout v2 = new VerticalLayout();
    private VerticalLayout v3 = new VerticalLayout();
    private Set<String> deletedMembership = new HashSet<String>();
    private Rule rule;

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

            tabSheet.addTab(v1, ProcessbaseApplication.getCurrent().getPbMessages().getString("tabDescription"), null);

            activitiesTable.setSizeFull();

            v2.setMargin(false, false, false, false);
            v2.addComponent(activitiesTable);
            v2.setSizeFull();
            tabSheet.addTab(v2, ProcessbaseApplication.getCurrent().getPbMessages().getString("tabCustomUI"), null);

            // prepare membership
            prepareTableMembership();
            addBtn = new Button(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnAdd"), this);
            addBtn.setStyleName(Runo.BUTTON_SMALL);
            v3.setMargin(false, false, false, false);
            v3.setSpacing(true);
            v3.addComponent(addBtn);
            v3.setComponentAlignment(addBtn, Alignment.MIDDLE_RIGHT);
            v3.addComponent(tableMembership);
            v3.setSizeFull();
            tabSheet.addTab(v3, ProcessbaseApplication.getCurrent().getPbMessages().getString("processAccess"), null);
            refreshTableMembership();

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

//        activitiesTable.addContainerProperty("activityUUID", String.class, null, "UUID", null, null);
//        activitiesTable.setColumnWidth("activityUUID", 0);
            activitiesTable.addContainerProperty("activityLabel", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActivityName"), null, null);
            activitiesTable.addContainerProperty("url", String.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tabCaptionTaskURL"), null, null);
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
            } else if (event.getButton().equals(saveAccessBtn)) {
                saveProcessAccess();
                close();
            } else if (event.getButton().equals(addBtn)) {
                addTableMembershipRow(null);
            } else if (event.getButton() instanceof TableLinkButton) {
                TableLinkButton tlb = (TableLinkButton) event.getButton();
                String uuid = (String) tlb.getTableValue();
                tableMembership.removeItem(uuid);
                if (!uuid.startsWith("NEW_MEMBERSHIP_UUID")) {
                    deletedMembership.add(uuid);
                }
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
                    ProcessbaseApplication.getCurrent().getBpmModule().addProcessMetaData(processDefinition.getUUID(), pd.getUUID().toString(), activitiesTable.getItem(object).getItemProperty("url").toString());
                } else if (activitiesTable.getItem(object).getItemProperty("url") != null && activitiesTable.getItem(object).getItemProperty("url").toString().isEmpty()) {
                    ProcessbaseApplication.getCurrent().getBpmModule().deleteProcessMetaData(processDefinition.getUUID(), pd.getUUID().toString());
                }

            } else if (object instanceof ActivityDefinition) { // activity level Custom UI
                ActivityDefinition activityDefinition = (ActivityDefinition) object;
                if (activitiesTable.getItem(object).getItemProperty("url") != null && !activitiesTable.getItem(object).getItemProperty("url").toString().isEmpty()) {
                    ProcessbaseApplication.getCurrent().getBpmModule().addProcessMetaData(processDefinition.getUUID(), activityDefinition.getUUID().toString(), activitiesTable.getItem(object).getItemProperty("url").toString());
                } else if (activitiesTable.getItem(object).getItemProperty("url") != null && activitiesTable.getItem(object).getItemProperty("url").toString().isEmpty()) {
                    ProcessbaseApplication.getCurrent().getBpmModule().deleteProcessMetaData(processDefinition.getUUID(), activityDefinition.getUUID().toString());
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
        if (event.getTabSheet().getSelectedTab().equals(v1)) {
            saveAccessBtn.setVisible(false);
            applyBtn.setVisible(false);
            deleteAllBtn.setVisible(true);
            deleteInstancesBtn.setVisible(true);
            downloadBtn.setVisible(true);
            enableBtn.setVisible(true);
            archiveBtn.setVisible(true);
        } else if (event.getTabSheet().getSelectedTab().equals(v2)) {
            applyBtn.setVisible(true);
            saveAccessBtn.setVisible(false);
            deleteAllBtn.setVisible(false);
            deleteInstancesBtn.setVisible(false);
            downloadBtn.setVisible(false);
            enableBtn.setVisible(false);
            archiveBtn.setVisible(false);
        } else if (event.getTabSheet().getSelectedTab().equals(v3)) {
            saveAccessBtn.setVisible(true);
            applyBtn.setVisible(false);
            deleteAllBtn.setVisible(false);
            deleteInstancesBtn.setVisible(false);
            downloadBtn.setVisible(false);
            enableBtn.setVisible(false);
            archiveBtn.setVisible(false);
        }
    }

    private void prepareTableMembership() {
        tableMembership.addContainerProperty("group", Component.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionGroup"), null, null);
        tableMembership.addContainerProperty("role", Component.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionRole"), null, null);
        tableMembership.addContainerProperty("actions", Component.class, null, ProcessbaseApplication.getCurrent().getPbMessages().getString("tableCaptionActions"), null, null);
        tableMembership.setColumnWidth("actions", 50);
        tableMembership.setImmediate(true);
        tableMembership.setWidth("100%");
        tableMembership.setPageLength(10);
    }

    private void refreshTableMembership() {
        try {
            rule = ProcessbaseApplication.getCurrent().getBpmModule().findRule(processDefinition.getUUID().toString());
            tableMembership.removeAllItems();
            for (String membershipUUID : rule.getMemberships()) {
                Membership membership = ProcessbaseApplication.getCurrent().getBpmModule().getMembershipByUUID(membershipUUID);
                addTableMembershipRow(membership);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void addTableMembershipRow(Membership membership) throws Exception {
        String uuid = membership != null ? membership.getUUID() : "NEW_MEMBERSHIP_UUID_" + UUID.randomUUID().toString();
        Item woItem = tableMembership.addItem(uuid);

        if (membership != null) {
            Label groups = new Label(getGroups().getItem(membership != null ? membership.getGroup().getUUID() : null).getItemProperty("path"));
            woItem.getItemProperty("group").setValue(groups);

            Label roles = new Label(getRoles().getItem(membership != null ? membership.getRole().getUUID() : null).getItemProperty("name"));
            woItem.getItemProperty("role").setValue(roles);

        } else {
            ComboBox groups = new ComboBox();
            groups.setWidth("100%");
            groups.setContainerDataSource(getGroups());
            groups.setItemCaptionPropertyId("path");
            groups.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
            groups.setValue(membership != null ? membership.getGroup().getUUID() : null);
            woItem.getItemProperty("group").setValue(groups);

            ComboBox roles = new ComboBox();
            roles.setWidth("100%");
            roles.setContainerDataSource(getRoles());
            roles.setItemCaptionPropertyId("name");
            roles.setFilteringMode(ComboBox.FILTERINGMODE_CONTAINS);
            roles.setValue(membership != null ? membership.getRole().getUUID() : null);
            woItem.getItemProperty("role").setValue(roles);
            
        }
        TableLinkButton tlb = new TableLinkButton(ProcessbaseApplication.getCurrent().getPbMessages().getString("btnDelete"), "icons/cancel.png", uuid, this, Constants.ACTION_DELETE);
        woItem.getItemProperty("actions").setValue(tlb);
    }

    public IndexedContainer getGroups() throws Exception {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("name", String.class, null);
        container.addContainerProperty("label", String.class, null);
        container.addContainerProperty("uuid", String.class, null);
        container.addContainerProperty("path", String.class, null);
        List<Group> groups = ProcessbaseApplication.getCurrent().getBpmModule().getAllGroups();
        for (Group groupX : groups) {
            String path = getGroupPath(groupX);
            //if (!path.startsWith("/" + IdentityAPI.DEFAULT_GROUP_NAME)) {
                Item item = container.addItem(groupX.getUUID());
                item.getItemProperty("name").setValue(groupX.getName());
                item.getItemProperty("label").setValue(groupX.getLabel());
                item.getItemProperty("uuid").setValue(groupX.getUUID());
                item.getItemProperty("path").setValue(path);
            //}
        }
        container.sort(new Object[]{"name"}, new boolean[]{true});
        return container;
    }
    
   
   

    private String getGroupPath(Group group) {
        StringBuilder result = new StringBuilder(IdentityAPI.GROUP_PATH_SEPARATOR + group.getName() + IdentityAPI.GROUP_PATH_SEPARATOR);
        Group parent = group.getParentGroup();
        while (parent != null) {
            result.insert(0, IdentityAPI.GROUP_PATH_SEPARATOR + parent.getName());
            parent = parent.getParentGroup();
        }
        return result.toString();
    }

    public IndexedContainer getRoles() throws Exception {
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("name", String.class, null);
        container.addContainerProperty("label", String.class, null);
        container.addContainerProperty("uuid", String.class, null);
        List<Role> roles = ProcessbaseApplication.getCurrent().getBpmModule().getAllRoles();
        for (Role roleX : roles) {
            //if (!roleX.getName().equals(IdentityAPI.ADMIN_ROLE_NAME)) {
                Item item = container.addItem(roleX.getUUID());
                item.getItemProperty("name").setValue(roleX.getName());
                item.getItemProperty("label").setValue(roleX.getLabel());
                item.getItemProperty("uuid").setValue(roleX.getUUID());
            //}
        }
        container.sort(new Object[]{"name"}, new boolean[]{true});
        return container;
    }
    
    /*private void removeProcessAccess(){
    	ProcessbaseApplication.getCurrent().getBpmModule().removeRuleFromEntities(rule.getUUID(), null, null, null, deletedMembership, null);
    }*/

    private void saveProcessAccess() {
        try {
            
        	
            BPMModule bpmModule = ProcessbaseApplication.getCurrent().getBpmModule();
            
            Set<String> membershipUUIDs = new HashSet<String>();
            for (Object itemId : tableMembership.getItemIds()) {
                Item woItem = tableMembership.getItem(itemId);
                if (woItem.getItemProperty("group").getValue() instanceof ComboBox && woItem.getItemProperty("role").getValue() instanceof ComboBox) {
                    ComboBox groups = (ComboBox) woItem.getItemProperty("group").getValue();
                    ComboBox roles = (ComboBox) woItem.getItemProperty("role").getValue();
                    Membership membership = bpmModule.getMembershipForRoleAndGroup(roles.getValue().toString(), groups.getValue().toString());
                    membershipUUIDs.add(membership.getUUID());
                }
            }
            if(membershipUUIDs.size()>0) //If threre is no items selected in combo, then there is no point of saveing this
            {
            	if(rule==null)//crete rule for process starting
                	rule=bpmModule.createRule(processDefinition.getUUID().toString(), "ENTITY_PROCESS_START", "Rule to start a process", RuleType.PROCESS_START);
                
            	Set<String> entityUUIDs = new HashSet<String>();
            	entityUUIDs.add(processDefinition.getUUID().toString());            	
                bpmModule.applyRuleToEntities(rule.getUUID(), null, null, null, membershipUUIDs, entityUUIDs);
            }
            
        } catch (Exception ex) {
            showError(ex);
        }
    }
    
    private void showError(Exception ex)
    {
    	ex.printStackTrace();
        getWindow().showNotification(ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
    }
}
