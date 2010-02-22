/**
 * Copyright (C) 2010 PROCESSBASE
 * PROCESSBASE Ltd, Almaty, Kazakhstan
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
package org.processbase.ui.portlet;

import org.processbase.ui.template.ProcessBaseApplication;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2.PortletListener;
import com.vaadin.ui.Layout;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.processbase.ui.admin.ProcessInstancesPanel;
import org.processbase.ui.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class ProcessInstances extends ProcessBaseApplication implements PortletListener {

    private PbWindow processDefinitionWindow;
    private ProcessInstancesPanel processInstancesPanel;

    @Override
    public void init() {
        super.init();
        processDefinitionWindow = new PbWindow(this.getPortletApplicationContext2());
        ((Layout) processDefinitionWindow.getContent()).setMargin(false);
        processDefinitionWindow.setSizeFull();
        processDefinitionWindow.setStyleName("white");
        this.setMainWindow(processDefinitionWindow);
    }

    @Override
    public void createApplication(RenderRequest request, RenderResponse response) {
        processInstancesPanel = new ProcessInstancesPanel(getPortletApplicationContext2());
        processInstancesPanel.refreshTable();
        processDefinitionWindow.addComponent(processInstancesPanel);

    }
}
