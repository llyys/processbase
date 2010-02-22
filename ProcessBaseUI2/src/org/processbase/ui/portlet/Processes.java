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
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.processbase.ui.template.PbWindow;
import org.processbase.ui.worklist.ProcessesPanel;

/**
 *
 * @author mgubaidullin
 */
public class Processes extends ProcessBaseApplication implements PortletListener {

    private PbWindow taskListWindow;
    private ProcessesPanel processesPanel;

    @Override
    public void init() {
        super.init();
        taskListWindow = new PbWindow(this.portletApplicationContext2);
        ((Layout) taskListWindow.getContent()).setMargin(false);
        taskListWindow.setSizeFull();
        taskListWindow.setStyleName("white");
        this.setMainWindow(taskListWindow);
    }

    @Override
    public void createApplication(RenderRequest request, RenderResponse response) {
        processesPanel = new ProcessesPanel(this.portletApplicationContext2);
        taskListWindow.addComponent(processesPanel);
        processesPanel.refreshTable();
    }
}
