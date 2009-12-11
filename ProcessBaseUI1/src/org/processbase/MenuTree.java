/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase;

import org.processbase.util.ProcessBaseClassLoader;
import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.processbase.ui.admin.GroupsPanel;
import org.processbase.ui.admin.JarsPanel;
import org.processbase.ui.admin.UsersPanel;
import org.processbase.ui.dashboard.ProcessDashboardPanel;
import org.processbase.ui.admin.ActivityInstancesPanel;
import org.processbase.ui.admin.ProcessDefinitionsPanel;
import org.processbase.ui.admin.ProcessInstancesPanel;
import org.processbase.ui.template.AnalyticPanel;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.template.WorkPanel;
import org.processbase.ui.worklist.ProcessesToStartPanel;
import org.processbase.ui.worklist.TasksDonePanel;
import org.processbase.ui.worklist.TasksToDoPanel;

/**
 *
 * @author mgubaidullin
 */
public class MenuTree extends VerticalLayout implements ItemClickEvent.ItemClickListener {

    protected ResourceBundle messages = ResourceBundle.getBundle("resources/MessagesBundle", ((ProcessBase) getApplication()).getCurrent().getLocale());
    protected Tree tree = new Tree();
    protected HierarchicalContainer treeContainer = new HierarchicalContainer();
    protected HierarchicalContainer startProcessTreeContainer = new HierarchicalContainer();
    protected Tree startProcessTree = new Tree();
    protected HorizontalLayout horizontalLayout = new HorizontalLayout();

    public MenuTree() {
        super();
        prepareTrees();
        startProcessTree.setContainerDataSource(startProcessTreeContainer);
        startProcessTree.setItemCaptionPropertyId("name");
        startProcessTree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        startProcessTree.setImmediate(true);
        startProcessTree.addListener(this);
        addComponent(startProcessTree);
        tree.setContainerDataSource(treeContainer);
        tree.setItemCaptionPropertyId("name");
        tree.setItemCaptionMode(AbstractSelect.ITEM_CAPTION_MODE_PROPERTY);
        tree.setImmediate(true);
        tree.addListener(this);
        addComponent(tree);
        setExpandRatio(tree, 1);
        horizontalLayout.setHeight("25px");
        setSizeFull();
        setSpacing(true);
        setMargin(false, false, false, true);
    }

    public void prepareTrees() {
        startProcessTreeContainer.addContainerProperty("name", String.class, null);
        PanelMenuItem panelMenuItem = new PanelMenuItem(messages.getString("tabCaptionTaskNew"), ProcessesToStartPanel.class, null);
        Item item = startProcessTreeContainer.addItem(panelMenuItem);
        item.getItemProperty("name").setValue(panelMenuItem.getName());
        startProcessTreeContainer.setChildrenAllowed(panelMenuItem, false);

        treeContainer.addContainerProperty("name", String.class, null);
        PanelMenuItem workList = new PanelMenuItem(messages.getString("tabCaptionWorkList"), null, null);
        addTreeItem(workList, "", true);
        tree.expandItem(workList);
        addTreeItem(new PanelMenuItem(messages.getString("tabCaptionTaskInbox"), TasksToDoPanel.class, new TasksToDoPanel()), workList, false);
        addTreeItem(new PanelMenuItem(messages.getString("tabCaptionTaskFinished"), TasksDonePanel.class, null), workList, false);

        if (((ProcessBase) getApplication()).getCurrent().getUser().isBpmAdmin()) {
            PanelMenuItem administration = new PanelMenuItem(messages.getString("tabCaptionAdministration"), null, null);
            addTreeItem(administration, "", true);
            tree.expandItem(administration);
            addTreeItem(new PanelMenuItem(messages.getString("tabCaptionProcesses"), ProcessDefinitionsPanel.class, null), administration, false);
            addTreeItem(new PanelMenuItem(messages.getString("tabCaptionJars"), JarsPanel.class, null), administration, false);
//            addTreeItem(new PanelMenuItem(messages.getString("tabCaptionACLUsers"), UsersPanel.class, null), administration, false);
//            addTreeItem(new PanelMenuItem(messages.getString("tabCaptionACLGroups"), GroupsPanel.class, null), administration, false);
//            PanelMenuItem monitoring = new PanelMenuItem(messages.getString("tabCaptionMonitoring"), null, null);
//            addTreeItem(monitoring, "", true);
//            tree.expandItem(monitoring);
            addTreeItem(new PanelMenuItem(messages.getString("tabCaptionProcessInstances"), ProcessInstancesPanel.class, null), administration, false);
            addTreeItem(new PanelMenuItem(messages.getString("tabCaptionProcessActivities"), ActivityInstancesPanel.class, null), administration, false);
        }
        if (((ProcessBase) getApplication()).getCurrent().getUser().isDashboardAdmin()) {
            PanelMenuItem analytics = new PanelMenuItem(messages.getString("tabCaptionAnalytics"), null, null);
            addTreeItem(analytics, "", true);
            tree.expandItem(analytics);
            addTreeItem(new PanelMenuItem(messages.getString("tabCaptionProcesses"), ProcessDashboardPanel.class, null), analytics, false);
            addTreeItem(new PanelMenuItem(messages.getString("tabCaptionWorkList"), ProcessDashboardPanel.class, null), analytics, false);
        }
    }

    public void addTreeItem(PanelMenuItem panelMenuItem, Object parent, boolean childrenAllowed) {
        Item item = treeContainer.addItem(panelMenuItem);
        item.getItemProperty("name").setValue(panelMenuItem.getName());
        treeContainer.setChildrenAllowed(panelMenuItem, childrenAllowed);
        treeContainer.setParent(panelMenuItem, parent);
    }

    public void itemClick(ItemClickEvent event) {
        if (event.getSource() == tree) {
            startProcessTree.setNullSelectionAllowed(true);
            startProcessTree.select(null);
        } else if (event.getSource() == startProcessTree) {
            tree.setNullSelectionAllowed(true);
            tree.select(null);
        }
        Object itemId = event.getItemId();
        if (itemId instanceof PanelMenuItem) {
            PanelMenuItem panelMenuItem = (PanelMenuItem) itemId;
            if (panelMenuItem.getPanelClass() != null && panelMenuItem.getWorkPanel() == null) {
                try {
                    Class b = ProcessBaseClassLoader.getCurrent().loadClass(panelMenuItem.getPanelClass().getName());
                    panelMenuItem.setWorkPanel((WorkPanel) b.newInstance());
                } catch (Exception ex) {
                    Logger.getLogger(MenuTree.class.getName()).log(Level.SEVERE, ex.getMessage());
                }
            } else if (panelMenuItem.getPanelClass() == null) {
                Logger.getLogger(MenuTree.class.getName()).log(Level.SEVERE, "itemId = " + itemId);
                tree.select(null);
            }
            if (panelMenuItem.getWorkPanel() != null) {
                ((MainWindow) getWindow()).setWorkPanel(panelMenuItem.getWorkPanel());
            }
            if (panelMenuItem.getWorkPanel() instanceof TablePanel) {
                ((TablePanel) panelMenuItem.getWorkPanel()).refreshTable();
            } else if (panelMenuItem.getWorkPanel() instanceof AnalyticPanel) {
                ((AnalyticPanel) panelMenuItem.getWorkPanel()).refreshDashboard();
            }
        }
    }

    public WorkPanel getFirst() {
        WorkPanel result = null;
        for (Object o : tree.getItemIds()) {
            if (o instanceof PanelMenuItem) {
                if (((PanelMenuItem) o).getWorkPanel() != null) {
                    tree.select(o);
                    return ((PanelMenuItem) o).getWorkPanel();
                }
            }
        }
        return result;
    }

    public void unselectAll() {
        tree.select(null);
        startProcessTree.select(null);
    }
}
