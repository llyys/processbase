package org.processbase.openesb.monitor.ui;

import com.sun.caps.management.api.bpel.BPELManagementService;
import com.sun.caps.management.api.bpel.BPELManagementService.ActivityStatus;
import com.sun.caps.management.api.bpel.BPELManagementService.BPInstanceInfo;
import com.sun.caps.management.api.bpel.BPELManagementService.BPInstanceQueryResult;
import com.sun.caps.management.api.bpel.BPELManagementService.BPStatus;
import com.sun.caps.management.api.bpel.BPELManagementService.SortColumn;
import com.sun.caps.management.api.bpel.BPELManagementService.SortOrder;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.themes.Reindeer;
import java.util.List;
import org.processbase.openesb.monitor.POEM;


import org.processbase.openesb.monitor.ui.template.TablePanel;

/**
 *
 * @author mgubaidullin
 */
public class BpelActivitiesPanel extends TablePanel implements Property.ValueChangeListener {

    private String instanceId;
    private String target;
    public IndexedContainer activitiesContainer = new IndexedContainer();

    public BpelActivitiesPanel(String instanceId, String target) {
        super("Activities");
        this.target = target;
        this.instanceId = instanceId;
        buttonBar.setHeight("30px");

        initContainer();
        initTableUI();
        refreshInstanceData();
    }

    public void initContainer() {
        activitiesContainer.addContainerProperty("activityId", String.class, null);
        activitiesContainer.addContainerProperty("activityXpath", String.class, null);
        activitiesContainer.addContainerProperty("iteration", Integer.class, null);
        activitiesContainer.addContainerProperty("startTime", String.class, null);
        activitiesContainer.addContainerProperty("endTime", String.class, null);
        activitiesContainer.addContainerProperty("lasted", String.class, null);
        activitiesContainer.addContainerProperty("status", String.class, null);
    }

    @Override
    public void initTableUI() {
        table.setContainerDataSource(activitiesContainer);
        table.setColumnExpandRatio("activityXpath", 1);
        table.setSortContainerPropertyId("startTime");
        table.setSortAscending(false);
        table.sort();
    }

    public void refreshInstanceData() {
        activitiesContainer.removeAllItems();
        try {
             List<BPELManagementService.ActivityStatus> activities = POEM.getCurrent().bpelManagementService.getBPELInstanceActivityStatus(instanceId, target);
            for (ActivityStatus activity : activities) {
                Item woItem = activitiesContainer.addItem(activity);
                woItem.getItemProperty("activityId").setValue(activity.activityId);
                woItem.getItemProperty("activityXpath").setValue(activity.activityXpath);
                woItem.getItemProperty("iteration").setValue(activity.iteration);
                woItem.getItemProperty("startTime").setValue(activity.startTime);
                woItem.getItemProperty("endTime").setValue(activity.endTime);
                woItem.getItemProperty("lasted").setValue(activity.lasted);
                woItem.getItemProperty("status").setValue(activity.status);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        refreshBtn.setStyleName("reindeermods");
    }

    @Override
    public void buttonClick(ClickEvent event) {

        if (event.getButton().equals(refreshBtn)) {
            refreshInstanceData();
        }
    }

    public void valueChange(ValueChangeEvent event) {
        refreshBtn.setStyleName(Reindeer.BUTTON_DEFAULT);
    }
}
