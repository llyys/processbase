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
import org.processbase.ui.util.Constants;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import org.processbase.ui.worklist.DefaultTaskWindow;

/**
 *
 * @author mgubaidullin
 */
public class TaskDefault extends ProcessBaseApplication {

    private DefaultTaskWindow defaultTaskWindow;

    @Override
    public void init() {
        super.init();
        setLogoutURL(Constants.TASKLIST_PAGE_URL);
        defaultTaskWindow = new DefaultTaskWindow(this.portletApplicationContext2);
        this.setMainWindow(defaultTaskWindow);
    }

    @Override
    public void createApplication(RenderRequest request, RenderResponse response) {
        if (!defaultTaskWindow.setTaskInfo(null)) {
            this.close();
        } else {
            response.setTitle(defaultTaskWindow.getTask().getActivityLabel());
            defaultTaskWindow.exec();
        }
    }
}
