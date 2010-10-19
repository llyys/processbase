package org.processbase.openesb.monitor;

import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;
import org.processbase.openesb.monitor.ui.BpelActivitiesPanel;
import org.processbase.openesb.monitor.ui.BpelVariablesPanel;

/**
 *
 * @author marat
 */
public class BpelInstanceWindow extends Window {

    private TabSheet tabs = new TabSheet();
    private VerticalLayout mainLayout;

    public BpelInstanceWindow(String instanceId, String target) {
        super("Process Instance");
        mainLayout = (VerticalLayout) getContent();
        mainLayout.setMargin(true);
        mainLayout.setStyleName(Reindeer.LAYOUT_WHITE);
        mainLayout.setSizeFull();
        setModal(true);

        tabs.setSizeFull();
        tabs.setStyleName(Reindeer.TABSHEET_MINIMAL);
        mainLayout.addComponent(tabs);
        mainLayout.setExpandRatio(tabs, 1);

        tabs.addComponent(new BpelActivitiesPanel(instanceId, target));
        tabs.addComponent(new BpelVariablesPanel(instanceId, null, target));
    }



}
