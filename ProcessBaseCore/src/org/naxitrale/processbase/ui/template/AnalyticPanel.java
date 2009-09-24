/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.naxitrale.processbase.ui.template;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import org.naxitrale.processbase.bpm.AdminModule;
import org.naxitrale.processbase.bpm.AnalyticModule;
import org.naxitrale.processbase.bpm.WorklistModule;

/**
 *
 * @author mgubaidullin
 */
public class AnalyticPanel extends WorkPanel {

    protected GridLayout grid = new GridLayout(3, 3);
    protected AdminModule adminModule = new AdminModule();
    protected WorklistModule worklistModule = new WorklistModule();
    protected AnalyticModule analyticModule = new AnalyticModule();

    public AnalyticPanel() {
        super();
        initUI();
    }

    public void initUI() {
        grid.setSizeFull();
        grid.setSpacing(true);
        horizontalLayout.addComponent(grid);
        horizontalLayout.setComponentAlignment(grid, Alignment.TOP_LEFT);
        horizontalLayout.setExpandRatio(grid, 1);
    }

    public void refreshDashboard() {
    }

    @Override
    public void buttonClick(ClickEvent event) {
        super.buttonClick(event);
        refreshDashboard();
    }
}
