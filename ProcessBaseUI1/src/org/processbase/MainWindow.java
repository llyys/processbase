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

import com.vaadin.terminal.Sizeable;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import com.vaadin.ui.VerticalLayout;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.template.TablePanel;
import org.processbase.ui.template.WorkPanel;

/**
 *
 * @author mgubaidullin
 */
public class MainWindow extends PbWindow implements SelectedTabChangeListener {

    protected HorizontalLayout horizontalLayout = new HorizontalLayout();
    protected VerticalLayout verticalLayout = new VerticalLayout();
    protected final SplitPanel splitPanel = new SplitPanel();
    protected LogoPanel logoPanel = new LogoPanel();
    protected MenuTree menuTree = new MenuTree();
    protected WorkPanel workPanel = null;

    public MainWindow() {
        super("ProcessBase");
        initUI();
    }

    public void initUI() {
        setTheme("processbase");
        setTheme("processbase");
        setSizeFull();
        setCaption("ProcessBase");
        setContent(verticalLayout);
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(false, true, true, false);
        verticalLayout.addComponent(logoPanel);

        verticalLayout.addComponent(splitPanel);
        verticalLayout.setExpandRatio(splitPanel, 1);

        splitPanel.setOrientation(SplitPanel.ORIENTATION_HORIZONTAL);
        splitPanel.setSplitPosition(200, Sizeable.UNITS_PIXELS);
        splitPanel.setStyleName("small");

        splitPanel.addComponent(menuTree);
        this.workPanel = menuTree.getFirst();
        splitPanel.addComponent(this.workPanel);
        if (workPanel instanceof TablePanel) {
            ((TablePanel) workPanel).refreshTable();
        }

    }

    public HorizontalLayout getHorizontalLayout() {
        return horizontalLayout;
    }

    public void selectedTabChange(SelectedTabChangeEvent event) {
        if ((event.getTabSheet().getSelectedTab() instanceof Label)) {
            WebApplicationContext applicationContext = (WebApplicationContext) getApplication().getContext();
            getApplication().close();
            applicationContext.getHttpSession().invalidate();
        }
    }

    public WorkPanel getWorkPanel() {
        return workPanel;
    }

    public void setWorkPanel(WorkPanel workPanel) {
        splitPanel.removeComponent(workPanel);
        this.workPanel = workPanel;
        splitPanel.addComponent(this.workPanel);
    }
}
