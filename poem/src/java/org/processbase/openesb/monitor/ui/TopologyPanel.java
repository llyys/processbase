package org.processbase.openesb.monitor.ui;

import com.sun.appserv.management.config.ClusterConfig;
import com.sun.appserv.management.config.ClusteredServerConfig;
import com.sun.appserv.management.config.NodeAgentConfig;
import com.sun.appserv.management.config.StandaloneServerConfig;
import com.sun.appserv.management.j2ee.J2EEServer;
import com.sun.appserv.management.j2ee.StateManageable;
import com.sun.enterprise.tools.admingui.util.JMXUtil;
import com.sun.enterprise.admin.servermgmt.RuntimeStatus;
import com.sun.enterprise.tools.admingui.util.AMXUtil;
import com.sun.enterprise.tools.admingui.util.GuiUtil;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button.ClickEvent;
import java.util.Map;
import org.processbase.openesb.monitor.POEM;
import org.processbase.openesb.monitor.ui.template.TreeTablePanel;

/**
 *
 * @author mgubaidullin
 */
public class TopologyPanel extends TreeTablePanel {

    public HierarchicalContainer topologyContainer = new HierarchicalContainer();

    public TopologyPanel() {
        super("Servers topology");
        topologyContainer.addContainerProperty("name", String.class, null);
        topologyContainer.addContainerProperty("type", String.class, null);
        topologyContainer.addContainerProperty("information", String.class, null);
        topologyContainer.addContainerProperty("status", String.class, null);
        topologyContainer.addContainerProperty("restartneeded", String.class, null);
        topologyContainer.addContainerProperty("icon", ThemeResource.class, null);
        refreshTopologyData();
        initTableUI();
    }

    public void refreshTopologyData() {
        topologyContainer.removeAllItems();
        try {
            if (POEM.getCurrent().isClusterSupported) {
                refreshClusterData();
                refreshNodeAgentsData();
                refreshStandaloneData();
            } else {
                refreshStandaloneData();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void initTableUI() {
        treeTable.setContainerDataSource(topologyContainer);
        treeTable.setColumnExpandRatio("name", 1);
        treeTable.setColumnWidth("type", 70);
        treeTable.setColumnWidth("status", 70);
        treeTable.setColumnWidth("restartneeded", 90);
        treeTable.setColumnHeader("restartneeded", "restart needed");
        treeTable.setColumnHeader("node", "node agent");
//        treeTable.setRowHeaderMode(TreeTable.ROW_HEADER_MODE_ICON_ONLY);
        treeTable.setItemIconPropertyId("icon");
        treeTable.setVisibleColumns(new Object[]{"name", "type", "information", "status", "restartneeded"});
    }

    private void refreshStandaloneData() {
        Map<String, StandaloneServerConfig> servers = AMXUtil.getDomainConfig().getStandaloneServerConfigMap();

        for (String serverName : servers.keySet()) {
            StandaloneServerConfig serverConfig = servers.get(serverName);
            Item woItem = topologyContainer.addItem(serverName);
            topologyContainer.setChildrenAllowed(serverName, false);
            woItem.getItemProperty("name").setValue(serverName);
            woItem.getItemProperty("type").setValue("server");
            woItem.getItemProperty("icon").setValue(new ThemeResource("icons/instance.gif"));

            J2EEServer j2eeServer = AMXUtil.getJ2EEDomain().getJ2EEServerMap().get(serverName);
            String status = "";
            switch (j2eeServer.getstate()) {
                case StateManageable.STATE_FAILED:
                    status = "FAILED";
                    break;
                case StateManageable.STATE_RUNNING:
                    status = "RUNNING";
                    break;
                case StateManageable.STATE_STARTING:
                    status = "STARTING";
                    break;
                case StateManageable.STATE_STOPPED:
                    status = "STOPPED";
                    break;
                case StateManageable.STATE_STOPPING:
                    status = "STOPPING";
                    break;
            }
            woItem.getItemProperty("status").setValue(status);

            Boolean restartRequired = (Boolean) JMXUtil.getAttribute("com.sun.appserv:j2eeType=J2EEServer,name=" + serverName + ",category=runtime", "restartRequired");
            woItem.getItemProperty("restartneeded").setValue(restartRequired.toString().toUpperCase());

        }
    }

    private void refreshClusterData() {
        Map<String, ClusterConfig> clusters = AMXUtil.getDomainConfig().getClusterConfigMap();

        for (String clusterName : clusters.keySet()) {
            ClusterConfig clusterConfig = clusters.get(clusterName);
            Item woItem = topologyContainer.addItem(clusterName);
            woItem.getItemProperty("name").setValue(clusterName);
            woItem.getItemProperty("type").setValue("cluster");
            woItem.getItemProperty("icon").setValue(new ThemeResource("icons/cluster.gif"));
            woItem.getItemProperty("information").setValue("HeartbeatEnabled = " + clusterConfig.getHeartbeatEnabled()
                    + ", HeartbeatAddress = " + clusterConfig.getHeartbeatAddress()
                    + ", HeartbeatPort = " + clusterConfig.getHeartbeatPort());

            Map<String, ClusteredServerConfig> instances = AMXUtil.getDomainConfig().getClusterConfigMap().get(clusterConfig.getName()).getClusteredServerConfigMap();
            for (String instanceName : instances.keySet()) {
                ClusteredServerConfig clusteredServerConfig = instances.get(instanceName);
                Item item = topologyContainer.addItem(instanceName);
                topologyContainer.setParent(instanceName, clusterName);
                topologyContainer.setChildrenAllowed(instanceName, false);
                item.getItemProperty("name").setValue(clusteredServerConfig.getName());
                item.getItemProperty("type").setValue("instance");
                item.getItemProperty("icon").setValue(new ThemeResource("icons/instance.gif"));

                RuntimeStatus rs = JMXUtil.getRuntimeStatus(instanceName);
                item.getItemProperty("information").setValue("node agent = " + clusteredServerConfig.getReferencedNodeAgentName());
                item.getItemProperty("status").setValue(rs.getStatus().getStatusString().toUpperCase());
                item.getItemProperty("restartneeded").setValue(String.valueOf(rs.isRestartNeeded()).toUpperCase());
            }
        }
    }

    private void refreshNodeAgentsData() {
        Map<String, NodeAgentConfig> clusters = AMXUtil.getDomainConfig().getNodeAgentConfigMap();

        Item woItem = topologyContainer.addItem("Node Agents");
        woItem.getItemProperty("name").setValue("Node Agents");
        woItem.getItemProperty("type").setValue("");
        woItem.getItemProperty("icon").setValue(new ThemeResource("icons/nodeagents.gif"));

        for (String nodeName : clusters.keySet()) {
            NodeAgentConfig nodeAgentConfig = clusters.get(nodeName);
            Item item = topologyContainer.addItem(nodeName);
            topologyContainer.setParent(nodeName, "Node Agents");
            topologyContainer.setChildrenAllowed(nodeName, false);
            item.getItemProperty("name").setValue(nodeAgentConfig.getName());
            item.getItemProperty("type").setValue("node agent");
            item.getItemProperty("icon").setValue(new ThemeResource("icons/nodeagent.gif"));

            Map<String, String> props = nodeAgentConfig.getProperties();
            String agentStatus = nodeAgentConfig.getPropertyValue("rendezvousOccurred");
            String hostName = GuiUtil.getMessage("nodeAgent.UnknownHost");
            String status = null;
            if (agentStatus.equalsIgnoreCase("true")) {
                hostName = nodeAgentConfig.getJMXConnectorConfig().getPropertyValue("client-hostname");
                String objName = "com.sun.appserv:type=node-agent,name=" + nodeName + ",category=config";
                RuntimeStatus rs = (RuntimeStatus) JMXUtil.invoke(objName, "getRuntimeStatus", null, null);
                status = rs.getStatus().getStatusString().toUpperCase();
            } else {
                status = "Awaiting Initial Sync";
            }

            item.getItemProperty("information").setValue("hostName = " + nodeAgentConfig.getJMXConnectorConfig().getPropertyValue("client-hostname"));
            item.getItemProperty("status").setValue(status);
        }
    }

    @Override
    public void buttonClick(ClickEvent event) {
        if (event.getButton().equals(refreshBtn)) {
            refreshTopologyData();
        }
    }
}
