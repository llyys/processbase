/**
 * Copyright (C) 2010 PROCESSBASE Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    protected BPMModule bpmModule = new BPMModule(ProcessBase.getCurrent().getUser().getUid());
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
