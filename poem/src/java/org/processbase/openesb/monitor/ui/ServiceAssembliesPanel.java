package org.processbase.openesb.monitor.ui;

import com.sun.enterprise.tools.admingui.util.AMXUtil;
import com.sun.jbi.ui.common.JBIAdminCommands;
import com.sun.jbi.ui.common.ServiceAssemblyInfo;
import com.sun.jbi.ui.common.ServiceUnitInfo;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Window.Notification;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.openesb.monitor.POEM;
import org.processbase.openesb.monitor.POEMConstants;
import org.processbase.openesb.monitor.ui.template.TableExecButton;
import org.processbase.openesb.monitor.ui.template.TableExecButtonBar;
import org.processbase.openesb.monitor.ui.template.TreeTablePanel;

/**
 *
 * @author mgubaidullin
 */
public class ServiceAssembliesPanel extends TreeTablePanel {

    private NativeSelect clusterSelect = new NativeSelect("Cluster");
    private HierarchicalContainer saContainer = new HierarchicalContainer();
    private Button refreshClustersBtn = new Button();

    public ServiceAssembliesPanel() {
        super("Service Assemblies");
        buttonBar.setHeight("40px");
        if (POEM.getCurrent().isClusterSupported) {
            clusterSelect.setNullSelectionAllowed(false);
            clusterSelect.setImmediate(true);
            buttonBar.addComponent(clusterSelect, 0);
            buttonBar.setComponentAlignment(clusterSelect, Alignment.MIDDLE_LEFT);
            refreshClusters();

            refreshClustersBtn.setStyleName(Button.STYLE_LINK);
            refreshClustersBtn.setDescription("Refresh clusters");
            refreshClustersBtn.setIcon(new ThemeResource("icons/reload.png"));
            refreshClustersBtn.addListener((Button.ClickListener) this);
            buttonBar.addComponent(refreshClustersBtn, 1);
            buttonBar.setComponentAlignment(refreshClustersBtn, Alignment.MIDDLE_LEFT);
        }
        saContainer.addContainerProperty("name", String.class, null);
        saContainer.addContainerProperty("description", String.class, null);
        saContainer.addContainerProperty("state", String.class, null);
        saContainer.addContainerProperty("deployedon", String.class, null);
        saContainer.addContainerProperty("actions", TableExecButtonBar.class, null);
        saContainer.addContainerProperty("icon", ThemeResource.class, null);
        saContainer.addContainerProperty("type", String.class, null);

        initTableUI();
    }

    private void refreshClusters() {
        for (String cluster : AMXUtil.getDomainConfig().getClusterConfigMap().keySet()) {
            clusterSelect.addItem(cluster);
        }
        clusterSelect.setValue(clusterSelect.getItemIds().toArray()[0]);
    }

    @Override
    public void initTableUI() {
        treeTable.setContainerDataSource(saContainer);
//        treeTable.setColumnWidth("actions", 70);
//        treeTable.setColumnExpandRatio("name", 1);
//        treeTable.setColumnExpandRatio("deployedon", 1);
        treeTable.setColumnHeader("deployedon", "Deployed on");
        treeTable.setItemIconPropertyId("icon");
        treeTable.setVisibleColumns(new Object[]{"name", "description", "state", "deployedon", "actions"});
    }

    public void refreshServiceAssembliesData() {
        saContainer.removeAllItems();
        try {

            String xmlQueryResults = POEM.getCurrent().jbiAdminCommands.listServiceAssemblies(
                    POEM.getCurrent().isClusterSupported ? clusterSelect.getValue().toString() : JBIAdminCommands.DOMAIN_TARGET_KEY);
            List<ServiceAssemblyInfo> saInfoList = ServiceAssemblyInfo.readFromXmlTextWithProlog(xmlQueryResults);

            for (ServiceAssemblyInfo saInfo : saInfoList) {
                Item woItem = saContainer.addItem(saInfo.getName());
                woItem.getItemProperty("name").setValue(saInfo.getName());
                woItem.getItemProperty("description").setValue(saInfo.getDescription());
                woItem.getItemProperty("state").setValue(saInfo.getState().toUpperCase());
                woItem.getItemProperty("icon").setValue(new ThemeResource("icons/JBIServiceAssembly.gif"));
                woItem.getItemProperty("type").setValue("sa");

                List<ServiceUnitInfo> suInfoList = saInfo.getServiceUnitInfoList();

                for (ServiceUnitInfo suInfo : suInfoList) {
                    Item item = saContainer.addItem(suInfo.getName());
                    saContainer.setParent(suInfo.getName(), saInfo.getName());
                    saContainer.setChildrenAllowed(suInfo.getName(), false);
                    item.getItemProperty("name").setValue(suInfo.getName());
                    item.getItemProperty("description").setValue(suInfo.getDescription());
                    item.getItemProperty("state").setValue(suInfo.getState().toUpperCase());
                    item.getItemProperty("deployedon").setValue(suInfo.getDeployedOn());
                    item.getItemProperty("icon").setValue(new ThemeResource("icons/JBISU.gif"));
                    item.getItemProperty("type").setValue("su");
                }

                TableExecButtonBar tebb = new TableExecButtonBar();
                tebb.addButton(getExecBtn("Start", "icons/start.png", saInfo, POEMConstants.ACTION_START));
                tebb.addButton(getExecBtn("Stop", "icons/pause.png", saInfo, POEMConstants.ACTION_STOP));
                tebb.addButton(getExecBtn("Shutdown", "icons/cancel.png", saInfo, POEMConstants.ACTION_SHUT_DOWN));
                woItem.getItemProperty("actions").setValue(tebb);
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public TableExecButton getExecBtn(String description, String iconName, Object t, int action) {
        TableExecButton execBtn = new TableExecButton(description, iconName, t, this, action);
        execBtn.setEnabled(false);
        ServiceAssemblyInfo saInfo = (ServiceAssemblyInfo) t;
        if (execBtn.getAction() == POEMConstants.ACTION_START && !saInfo.getState().equals("Started")) {
            execBtn.setEnabled(true);
        } else if (execBtn.getAction() == POEMConstants.ACTION_STOP && saInfo.getState().equals("Started")) {
            execBtn.setEnabled(true);
        } else if (execBtn.getAction() == POEMConstants.ACTION_SHUT_DOWN && !saInfo.getState().equals("Shutdown")) {
            execBtn.setEnabled(true);
        }
        return execBtn;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton() instanceof TableExecButton) {
            try {
                TableExecButton execBtn = (TableExecButton) event.getButton();
                ServiceAssemblyInfo saInfo = (ServiceAssemblyInfo) ((TableExecButton) event.getButton()).getTableValue();
                if (execBtn.getAction() == POEMConstants.ACTION_START) {
                    POEM.getCurrent().jbiAdminCommands.startServiceAssembly(saInfo.getName(), JBIAdminCommands.SERVER_TARGET_KEY);
                } else if (execBtn.getAction() == POEMConstants.ACTION_STOP) {
                    POEM.getCurrent().jbiAdminCommands.stopServiceAssembly(saInfo.getName(), JBIAdminCommands.SERVER_TARGET_KEY);
                } else if (execBtn.getAction() == POEMConstants.ACTION_SHUT_DOWN) {
                    POEM.getCurrent().jbiAdminCommands.shutdownServiceAssembly(saInfo.getName(), JBIAdminCommands.SERVER_TARGET_KEY);
                }

            } catch (Exception ex) {
                Logger.getLogger(POEM.class.getName()).log(Level.SEVERE, ex.getMessage());
                getWindow().showNotification(ex.toString(), Notification.TYPE_ERROR_MESSAGE);
            }
        } else if (event.getButton().equals(refreshClustersBtn)) {
            refreshClusters();
        }
        refreshServiceAssembliesData();
    }
}
