package org.processbase.openesb.monitor.ui;

import org.processbase.openesb.monitor.BpelInstanceWindow;
import com.sun.caps.management.api.bpel.BPELManagementService.BPInstanceInfo;
import com.sun.caps.management.api.bpel.BPELManagementService.BPInstanceQueryResult;
import com.sun.caps.management.api.bpel.BPELManagementService.BPStatus;
import com.sun.caps.management.api.bpel.BPELManagementService.SortColumn;
import com.sun.caps.management.api.bpel.BPELManagementService.SortOrder;
import com.sun.caps.management.common.ManagementRemoteException;
import com.sun.enterprise.tools.admingui.util.AMXUtil;
import com.sun.jbi.ui.common.JBIAdminCommands;
import com.sun.jbi.ui.common.ServiceAssemblyInfo;
import com.sun.jbi.ui.common.ServiceUnitInfo;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;
import java.util.ArrayList;
import java.util.List;
import org.processbase.openesb.monitor.POEM;
import org.processbase.openesb.monitor.POEMConstants;
import org.processbase.openesb.monitor.ui.template.TableExecButton;
import org.processbase.openesb.monitor.ui.template.TableExecButtonBar;


import org.processbase.openesb.monitor.ui.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class BpelProcessesPanel extends TablePanel implements Property.ValueChangeListener {

    private NativeSelect statusSelect = new NativeSelect("Status");
    private TextField rowCount = new TextField("Row count", "10");
    private NativeSelect suSelect = new NativeSelect("Service Unit");
    private NativeSelect piSelect = new NativeSelect("Process ID");
    private CheckBox isPersistenceEnabled = new CheckBox("Persistence");
    private CheckBox isMonitoringEnabled = new CheckBox("Monitoring");
    private CheckBox isMonitoringVariableEnabled = new CheckBox("Variables");
    private GridLayout infoPanel = new GridLayout(7, 4);
    private NativeSelect clusterSelect = new NativeSelect("Cluster");
    private NativeSelect saSelect = new NativeSelect("Service Assembly");
    private NativeSelect sortColumnSelect = new NativeSelect("Sort Column");
    private NativeSelect sortOrderSelect = new NativeSelect("Sort Order");
    private Button refreshClustersBtn = new Button();
    private Button refreshAssembliesBtn = new Button();
    private List<ServiceAssemblyInfo> serveceAssembliesInfoList;
    public IndexedContainer biContainer = new IndexedContainer();

    public BpelProcessesPanel() {
        super("BPEL Service Engine");
        buttonBar.setHeight("100px");

        if (POEM.getCurrent().isClusterSupported) {
            clusterSelect.setWidth("100px");
            clusterSelect.setNullSelectionAllowed(false);
            clusterSelect.setImmediate(true);
            clusterSelect.addListener(new Property.ValueChangeListener() {

                public void valueChange(ValueChangeEvent event) {
//                    checkBPELSEState();
                    refreshServiceAssembliesData();
                }
            });

            infoPanel.addComponent(clusterSelect, 0, 0);
            refreshClustersBtn.setStyleName(Button.STYLE_LINK);
            refreshClustersBtn.setDescription("Refresh clusters");
            refreshClustersBtn.setIcon(new ThemeResource("icons/reload.png"));
            refreshClustersBtn.addListener((Button.ClickListener) this);
            infoPanel.addComponent(refreshClustersBtn, 1, 0);
            infoPanel.setComponentAlignment(refreshClustersBtn, Alignment.MIDDLE_LEFT);
        }

//        infoPanel.addComponent(isPersistenceEnabled, 0, 1);
//        infoPanel.setComponentAlignment(isPersistenceEnabled, Alignment.MIDDLE_LEFT);
//        infoPanel.addComponent(isMonitoringEnabled, 0, 2);
//        infoPanel.setComponentAlignment(isMonitoringEnabled, Alignment.MIDDLE_LEFT);
//        infoPanel.addComponent(isMonitoringVariableEnabled, 0, 3);
//        infoPanel.setComponentAlignment(isMonitoringVariableEnabled, Alignment.MIDDLE_LEFT);

        saSelect.setWidth("200px");
        saSelect.setNullSelectionAllowed(false);
        saSelect.setImmediate(true);
        saSelect.addListener(new Property.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                refreshBpelServiceUnits();
                refreshBtn.setStyleName(Reindeer.BUTTON_DEFAULT);
            }
        });
        infoPanel.addComponent(saSelect, 2, 0);
        refreshAssembliesBtn.setStyleName(Button.STYLE_LINK);
        refreshAssembliesBtn.setDescription("Refresh service assemblies");
        refreshAssembliesBtn.setIcon(new ThemeResource("icons/reload.png"));
        refreshAssembliesBtn.addListener((Button.ClickListener) this);
        infoPanel.addComponent(refreshAssembliesBtn, 3, 0);
        infoPanel.setComponentAlignment(refreshAssembliesBtn, Alignment.MIDDLE_LEFT);

        suSelect.setWidth("200px");
        suSelect.setNullSelectionAllowed(false);
        suSelect.setImmediate(true);
        suSelect.addListener(new Property.ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                refreshBpelProcessIds();
                refreshBtn.setStyleName(Reindeer.BUTTON_DEFAULT);
            }
        });
        infoPanel.addComponent(suSelect, 4, 0);

        piSelect.setWidth("440px");
        piSelect.setNullSelectionAllowed(true);
        piSelect.setImmediate(true);
        piSelect.addListener((Property.ValueChangeListener) this);
        infoPanel.addComponent(piSelect, 2, 1, 4, 1);

        sortColumnSelect.setWidth("100px");
        sortColumnSelect.setNullSelectionAllowed(false);
        sortColumnSelect.setImmediate(true);
        sortColumnSelect.addListener((Property.ValueChangeListener) this);
        for (SortColumn sortColumn : SortColumn.values()) {
            sortColumnSelect.addItem(sortColumn);
        }
        sortColumnSelect.setValue(sortColumnSelect.getItemIds().toArray()[0]);
        infoPanel.addComponent(sortColumnSelect, 5, 0);

        sortOrderSelect.setWidth("100px");
        sortOrderSelect.setNullSelectionAllowed(false);
        sortOrderSelect.setImmediate(true);
        sortOrderSelect.addListener((Property.ValueChangeListener) this);
        for (SortOrder sortOrder : SortOrder.values()) {
            sortOrderSelect.addItem(sortOrder);
        }
        sortOrderSelect.setValue(SortOrder.DESC);
        infoPanel.addComponent(sortOrderSelect, 5, 1);

        statusSelect.addItem(BPStatus.RUNNING);
        statusSelect.addItem(BPStatus.COMPLETED);
        statusSelect.addItem(BPStatus.FAULTED);
        statusSelect.addItem(BPStatus.SUSPENDED);
        statusSelect.addItem(BPStatus.TERMINATED);
        statusSelect.setNullSelectionAllowed(true);
        statusSelect.setImmediate(true);
        statusSelect.addListener((Property.ValueChangeListener) this);
        infoPanel.addComponent(statusSelect, 6, 0);

        rowCount.addValidator(new IntegerValidator("Row count must be a number between 1 and 1000"));
        rowCount.setWidth("100px");
        rowCount.setImmediate(true);
        rowCount.addListener((Property.ValueChangeListener) this);
        infoPanel.addComponent(rowCount, 6, 1);

        infoPanel.setMargin(false);
        infoPanel.setSpacing(true);
        buttonBar.addComponent(infoPanel, 0);

        // should be after UI definition
        if (POEM.getCurrent().isClusterSupported) {
            refreshClusters();
        } else {
            refreshServiceAssembliesData();
        }

        initContainer();
        initTableUI();
    }

    public void initContainer() {
        biContainer.addContainerProperty("id", TableExecButton.class, null);
        biContainer.addContainerProperty("bpelId", String.class, null);
        biContainer.addContainerProperty("startTime", String.class, null);
        biContainer.addContainerProperty("endTime", String.class, null);
        biContainer.addContainerProperty("lasted", String.class, null);
        biContainer.addContainerProperty("status", String.class, null);
        biContainer.addContainerProperty("actions", TableExecButtonBar.class, null);
    }

    private void refreshClusters() {
        IndexedContainer clustersContainer = new IndexedContainer();
        for (String cluster : AMXUtil.getDomainConfig().getClusterConfigMap().keySet()) {
            clustersContainer.addItem(cluster);
        }
        clusterSelect.setContainerDataSource(clustersContainer);
        clusterSelect.setValue(clusterSelect.getItemIds().toArray()[0]);
    }

    private void refreshServiceAssembliesData() {
        IndexedContainer serveceAssembliesContainer = new IndexedContainer();
        try {
            if (POEM.getCurrent().isClusterSupported && clusterSelect.getValue() != null) {
                String xmlQueryResults = POEM.getCurrent().jbiAdminCommands.listServiceAssemblies(clusterSelect.getValue().toString());
                serveceAssembliesInfoList = ServiceAssemblyInfo.readFromXmlTextWithProlog(xmlQueryResults);
            } else if (!POEM.getCurrent().isClusterSupported) {
                String xmlQueryResults = POEM.getCurrent().jbiAdminCommands.listServiceAssemblies(JBIAdminCommands.DOMAIN_TARGET_KEY);
                serveceAssembliesInfoList = ServiceAssemblyInfo.readFromXmlTextWithProlog(xmlQueryResults);
            }
            for (ServiceAssemblyInfo saInfo : serveceAssembliesInfoList) {
                serveceAssembliesContainer.addItem(saInfo.getName());
            }
            saSelect.setContainerDataSource(serveceAssembliesContainer);
            saSelect.setValue(serveceAssembliesContainer.getIdByIndex(0));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshBpelServiceUnits() {
        IndexedContainer serveceUnitsContainer = new IndexedContainer();
        if (saSelect.getValue() != null) {
            for (ServiceAssemblyInfo saInfo : serveceAssembliesInfoList) {
                if (saInfo.getName().equals(saSelect.getValue().toString())) {
                    List<ServiceUnitInfo> suInfoList = saInfo.getServiceUnitInfoList();
                    for (ServiceUnitInfo suInfo : suInfoList) {
                        if (suInfo.getDeployedOn().equalsIgnoreCase("sun-bpel-engine")) {
                            serveceUnitsContainer.addItem(suInfo.getName());
                        }
                    }
                }
            }
            suSelect.setContainerDataSource(serveceUnitsContainer);
            suSelect.setValue(suSelect.getItemIds().size() > 0 ? suSelect.getItemIds().toArray()[0] : null);
        }
    }

    private void refreshBpelProcessIds() {
        IndexedContainer bpelProcessIdsContainer = new IndexedContainer();
        try {
            List<String> bpelProcessIds = new ArrayList<String>();
            if (POEM.getCurrent().isClusterSupported && clusterSelect.getValue() != null && suSelect.getValue() != null) {
                bpelProcessIds.addAll(POEM.getCurrent().bpelManagementService.getBPELProcessIds(
                        suSelect.getValue().toString(), clusterSelect.getValue().toString()));
            } else if (!POEM.getCurrent().isClusterSupported && suSelect.getValue() != null) {
                bpelProcessIds.addAll(POEM.getCurrent().bpelManagementService.getBPELProcessIds(
                        suSelect.getValue().toString(), null));
            }

            for (String processId : bpelProcessIds) {
                bpelProcessIdsContainer.addItem(processId);
            }
            piSelect.setContainerDataSource(bpelProcessIdsContainer);
            piSelect.setValue(piSelect.getItemIds().size() > 0 ? piSelect.getItemIds().toArray()[0] : null);
        } catch (ManagementRemoteException ex) {
            ex.printStackTrace();
            getWindow().showNotification("Error", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }

    }

    private void checkBPELSEState() {
        try {
            isPersistenceEnabled.setReadOnly(false);
            isMonitoringEnabled.setReadOnly(false);
            isMonitoringVariableEnabled.setReadOnly(false);
            isPersistenceEnabled.setValue(POEM.getCurrent().bpelManagementService.isPersistenceEnabled(POEM.getCurrent().isClusterSupported ? (String) clusterSelect.getValue() : null));
            isMonitoringEnabled.setValue(POEM.getCurrent().bpelManagementService.isMonitoringEnabled(POEM.getCurrent().isClusterSupported ? (String) clusterSelect.getValue() : null));
            isMonitoringVariableEnabled.setValue(POEM.getCurrent().bpelManagementService.isMonitoringVariableEnabled(POEM.getCurrent().isClusterSupported ? (String) clusterSelect.getValue() : null));
            isPersistenceEnabled.setReadOnly(true);
            isMonitoringEnabled.setReadOnly(true);
            isMonitoringVariableEnabled.setReadOnly(true);
        } catch (ManagementRemoteException ex) {
            ex.printStackTrace();
            getWindow().showNotification("Error", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    @Override
    public void initTableUI() {
        table.setContainerDataSource(biContainer);
        table.setColumnExpandRatio("bpelId", 1);
//        table.setSortContainerPropertyId("startTime");
//        table.setSortAscending(false);
//        table.sort();
    }

    public void refreshProcessesData() {
        biContainer.removeAllItems();
        try {
            BPInstanceQueryResult instances =
                    POEM.getCurrent().bpelManagementService.getBPELInstances(
                    piSelect.getValue() != null ? piSelect.getValue().toString() : null,
                    (BPStatus) statusSelect.getValue(),
                    null,
                    new Integer(rowCount.getValue().toString()),
                    (SortColumn) sortColumnSelect.getValue(),
                    (SortOrder) sortOrderSelect.getValue(),
                    POEM.getCurrent().isClusterSupported ? clusterSelect.getValue().toString() : null);
            for (BPInstanceInfo info : instances.bpInstnaceList) {
                Item woItem = biContainer.addItem(info);
                woItem.getItemProperty("id").setValue(new TableExecButton(info.id, info.id, null, info.id, this, POEMConstants.ACTION_INFO));
                woItem.getItemProperty("bpelId").setValue(info.bpelId);
                woItem.getItemProperty("startTime").setValue(info.startTime);
                woItem.getItemProperty("endTime").setValue(info.endTime);
                woItem.getItemProperty("lasted").setValue(info.lasted);
                woItem.getItemProperty("status").setValue(info.status);

                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(getExecBtn("Resume", "icons/start.png", info, POEMConstants.ACTION_RESUME));
                tebb.addButton(getExecBtn("Suspend", "icons/pause.png", info, POEMConstants.ACTION_SUSPEND));
                tebb.addButton(getExecBtn("Terminate", "icons/cancel.png", info, POEMConstants.ACTION_TERMINATE));
                woItem.getItemProperty("actions").setValue(tebb);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        refreshBtn.setStyleName("reindeermods");
    }

    public TableExecButton getExecBtn(String description, String iconName, Object t, int action) {
        TableExecButton execBtn = new TableExecButton(description, iconName, t, this, action);
        execBtn.setEnabled(false);
        BPInstanceInfo info = (BPInstanceInfo) t;
        if (execBtn.getAction() == POEMConstants.ACTION_RESUME && info.status.equals(BPStatus.SUSPENDED)) {
            execBtn.setEnabled(true);
        } else if (execBtn.getAction() == POEMConstants.ACTION_SUSPEND && info.status.equals(BPStatus.RUNNING)) {
            execBtn.setEnabled(true);
        } else if (execBtn.getAction() == POEMConstants.ACTION_TERMINATE && info.status.equals(BPStatus.RUNNING)) {
            execBtn.setEnabled(true);
        } else if (execBtn.getAction() == POEMConstants.ACTION_TERMINATE && info.status.equals(BPStatus.SUSPENDED)) {
            execBtn.setEnabled(true);
        }
        return execBtn;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        try {

            if (event.getButton().equals(refreshBtn)) {
                refreshProcessesData();
            } else if (event.getButton().equals(refreshClustersBtn)) {
                refreshClusters();
                refreshBtn.setStyleName(Reindeer.BUTTON_DEFAULT);
            } else if (event.getButton().equals(refreshAssembliesBtn)) {
                refreshServiceAssembliesData();
                refreshBtn.setStyleName(Reindeer.BUTTON_DEFAULT);
            } else if (event.getButton() instanceof TableExecButton) {
                TableExecButton teb = (TableExecButton) event.getButton();


                if (POEM.getCurrent().isClusterSupported && clusterSelect.getValue() != null) {
                    switch (teb.getAction()) {
                        case POEMConstants.ACTION_INFO:
                            addBpelInstanceWindow(teb.getTableValue().toString(), clusterSelect.getValue().toString());
                            break;
                        case POEMConstants.ACTION_TERMINATE:
                            if (POEM.getCurrent().bpelManagementService.terminateInstance(((BPInstanceInfo) teb.getTableValue()).id, clusterSelect.getValue().toString())) {
                                refreshInstance((BPInstanceInfo) teb.getTableValue(), BPStatus.TERMINATED);
                            }
                            break;
                        case POEMConstants.ACTION_RESUME:
                            if (POEM.getCurrent().bpelManagementService.resumeInstance(((BPInstanceInfo) teb.getTableValue()).id, clusterSelect.getValue().toString())) {
                                refreshInstance((BPInstanceInfo) teb.getTableValue(), BPStatus.RUNNING);
                            }
                            break;
                        case POEMConstants.ACTION_SUSPEND:
                            if (POEM.getCurrent().bpelManagementService.suspendInstance(((BPInstanceInfo) teb.getTableValue()).id, clusterSelect.getValue().toString())) {
                                refreshInstance((BPInstanceInfo) teb.getTableValue(), BPStatus.SUSPENDED);
                            }
                            break;
                    }
                } else if (!POEM.getCurrent().isClusterSupported) {
                    switch (teb.getAction()) {
                        case POEMConstants.ACTION_INFO:
                            addBpelInstanceWindow(teb.getTableValue().toString(), null);
                            break;
                        case POEMConstants.ACTION_TERMINATE:
                            if (POEM.getCurrent().bpelManagementService.terminateInstance(((BPInstanceInfo) teb.getTableValue()).id, null)) {
                                refreshInstance((BPInstanceInfo) teb.getTableValue(), BPStatus.TERMINATED);
                            }
                            break;
                        case POEMConstants.ACTION_RESUME:
                            if (POEM.getCurrent().bpelManagementService.resumeInstance(((BPInstanceInfo) teb.getTableValue()).id, null)) {
                                refreshInstance((BPInstanceInfo) teb.getTableValue(), BPStatus.RUNNING);
                            }
                            break;
                        case POEMConstants.ACTION_SUSPEND:
                            if (POEM.getCurrent().bpelManagementService.suspendInstance(((BPInstanceInfo) teb.getTableValue()).id, null)) {
                                refreshInstance((BPInstanceInfo) teb.getTableValue(), BPStatus.SUSPENDED);
                            }
                            break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            getWindow().showNotification("Error", ex.getMessage(), Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void refreshInstance(BPInstanceInfo info, BPStatus status) {
        Item woItem = biContainer.getItem(info);
        woItem.getItemProperty("status").setValue(status);
    }

    private void addBpelInstanceWindow(String instanceId, String target) {
        BpelInstanceWindow bpelInstanceWindow = null;
        bpelInstanceWindow = new BpelInstanceWindow(instanceId, target);
        bpelInstanceWindow.setWidth("90%");
        bpelInstanceWindow.setHeight("90%");
        bpelInstanceWindow.setResizable(false);
        getWindow().addWindow(bpelInstanceWindow);
    }

    public void valueChange(ValueChangeEvent event) {
        refreshBtn.setStyleName(Reindeer.BUTTON_DEFAULT);
    }
}
