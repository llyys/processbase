/**
 * Copyright (C) 2009  Marat Gubaidullin.
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 **/
package org.processbase.ui.template;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.GridLayout;
import org.processbase.ProcessBase;
import org.processbase.bpm.AnalyticModule;
import org.processbase.bpm.BPMModule;

/**
 *
 * @author mgubaidullin
 */
public class AnalyticPanel extends WorkPanel {

    protected GridLayout grid = new GridLayout(3, 3);
    protected BPMModule bpmModule = new BPMModule();
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
