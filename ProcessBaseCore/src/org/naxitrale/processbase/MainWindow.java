/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase;

import org.naxitrale.processbase.ui.portal.MainPanel;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpSession;
import org.naxitrale.processbase.ui.acl.ACLPanel;
import org.naxitrale.processbase.ui.preferences.PreferencesPanel;
import org.naxitrale.processbase.ui.admin.BPMAdminPanel;
import org.naxitrale.processbase.ui.dashboard.DashboardPanel;
import org.naxitrale.processbase.ui.template.PbWindow;
import org.naxitrale.processbase.ui.worklist.WorkListPanel;

/**
 *
 * @author mgubaidullin
 */
public class MainWindow extends PbWindow implements SelectedTabChangeListener {

    private HorizontalLayout horizontalLayout = new HorizontalLayout();
    private TabSheet tabSheet = new TabSheet();
    private WorkListPanel workListPanel = null;
    private BPMAdminPanel adminPanel = null;
    private ACLPanel aclPanel = null;
    private DashboardPanel dashboardPanel = null;
    private MainPanel mainPanel = null;
    private PreferencesPanel preferencesPanel = null;

    public MainWindow() {
        super("ProcessBase");
        initUI();
    }

    public void initUI() {
        setTheme("processbase");
        tabSheet.setSizeFull();
//        mainPanel = new MainPanel();
//        tabSheet.addTab(mainPanel, "ProcessBase", null);
        workListPanel = new WorkListPanel();
        tabSheet.addTab(workListPanel, messages.getString("tabCaptionTasks"), null);
        if (ProcessBase.getCurrent().getUser().isBpmAdmin()) {
            adminPanel = new BPMAdminPanel();
            tabSheet.addTab(adminPanel, messages.getString("tabCaptionProcesses"), null);
        }
        if (ProcessBase.getCurrent().getUser().isDashboardAdmin()) {
            dashboardPanel = new DashboardPanel();
            tabSheet.addTab(dashboardPanel, messages.getString("tabCaptionAnalytics"), null);
        }
        if (ProcessBase.getCurrent().getUser().isAclAdmin()) {
            aclPanel = new ACLPanel();
            tabSheet.addTab(aclPanel, messages.getString("tabCaptionAdministration"), null);
        }
//        tabSheet.addTab(new VerticalLayout(), "Документация", null);
        preferencesPanel = new PreferencesPanel();
        tabSheet.addTab(preferencesPanel, messages.getString("tabCaptionPreferences"), null);
        tabSheet.addTab(new Label("Exit"), messages.getString("tabCaptionExit"), null);

        horizontalLayout.setSizeFull();
        horizontalLayout.setMargin(false);
        horizontalLayout.addComponent(tabSheet);
        horizontalLayout.setExpandRatio(tabSheet, 1);
        horizontalLayout.setComponentAlignment(tabSheet, Alignment.TOP_LEFT);
        setContent(horizontalLayout);
        tabSheet.addListener((SelectedTabChangeListener) this);
    }

    public HorizontalLayout getHorizontalLayout() {
        return horizontalLayout;
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        if ((event.getTabSheet().getSelectedTab() instanceof Label)) {
            WebApplicationContext applicationContext = (WebApplicationContext) getApplication().getContext();
            HttpSession session = applicationContext.getHttpSession();
            getApplication().close();
            session.invalidate();
        }
    }
}
