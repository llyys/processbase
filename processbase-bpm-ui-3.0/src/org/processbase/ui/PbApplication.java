/**
 * Copyright (C) 2011 PROCESSBASE
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
package org.processbase.ui;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.User;
import com.liferay.portal.util.PortalUtil;
import javax.portlet.PortletRequest;
import javax.portlet.PortletResponse;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.PortletApplicationContext2;
import com.vaadin.terminal.gwt.server.PortletRequestListener;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.portlet.PortletConfig;
import org.processbase.bpm.BPMModule;
import org.processbase.ui.util.DocumentLibraryUtil;
import org.processbase.core.Constants;
import org.processbase.ui.Processbase;
import org.processbase.ui.panel.AdminPanel;
import org.processbase.ui.panel.BAMPanel;
import org.processbase.ui.panel.ConsolePanel;
import org.processbase.ui.panel.IdentityPanel;
import org.processbase.ui.panel.MonitoringPanel;
import org.processbase.ui.template.PbWindow;

/**
 *
 * @author mgubaidullin
 */
public class PbApplication extends Application {

    PbWindow mainWindow;

    @Override
    public void init() {
        System.out.println("PbPortlet init ");
        setTheme("processbase");
        setLogoutURL(Constants.TASKLIST_PAGE_URL);
        try {
            Processbase.setCurrent(new Processbase());
//            User user = PortalUtil.getUser(request);
//            Processbase.getCurrent().setPortalUser(user);
//            Locale locale = getgetLocale();
//            setLocale(locale);
            Processbase.getCurrent().messages = ResourceBundle.getBundle("resources/MessagesBundle", getLocale());
            Processbase.getCurrent().bpmModule = new BPMModule(Processbase.getCurrent().getUserName());

        } catch (Exception e) {
            e.printStackTrace();
        }

        mainWindow = new PbWindow("Processbase User Portlet");
        mainWindow.setSizeFull();
        ConsolePanel ui = new ConsolePanel();
        mainWindow.setContent(ui);
        ui.initUI();
//        } else if (config.getInitParameter("ui").equalsIgnoreCase("AdminPanel")){
//            AdminPanel ui = new AdminPanel();
//            mainWindow.setContent(ui);
//            ui.initUI();
//        } else if (config.getInitParameter("ui").equalsIgnoreCase("IdentityPanel")){
//            IdentityPanel ui = new IdentityPanel();
//            mainWindow.setContent(ui);
//            ui.initUI();
//        } else if (config.getInitParameter("ui").equalsIgnoreCase("BAMPanel")){
//            BAMPanel ui = new BAMPanel();
//            mainWindow.setContent(ui);
//            ui.initUI();
//        } else if (config.getInitParameter("ui").equalsIgnoreCase("MonitoringPanel")){
//            MonitoringPanel ui = new MonitoringPanel();
//            mainWindow.setContent(ui);
//            ui.initUI();
//        }
        setMainWindow(mainWindow);
    }
}
