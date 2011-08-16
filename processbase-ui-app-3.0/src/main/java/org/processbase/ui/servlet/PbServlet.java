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
package org.processbase.ui.servlet;

import java.io.BufferedWriter;
import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;
import com.vaadin.ui.Window;

import org.processbase.ui.core.util.SpringContextHelper;
import org.processbase.ui.osgi.PbPanelModuleService;
import org.processbase.ui.osgi.impl.PbPanelModuleServiceImpl;

public class PbServlet extends AbstractApplicationServlet {

//    @Inject
//    private PbApplication application;
    //@Resource(mappedName = "org.processbase.ui.osgi.PbPanelModuleService")
    PbPanelModuleService panelModuleService;

    @Override
    protected void writeAjaxPageHtmlVaadinScripts(Window window,
            String themeName, Application application, BufferedWriter page,
            String appUrl, String themeUri, String appId,
            HttpServletRequest request) throws ServletException, IOException {
        page.write("<script type=\"text/javascript\">\n");
        page.write("//<![CDATA[\n");
        page.write("document.write(\"<script language='javascript' src='./scripts/jquery-1.6.2.min.js'><\\/script>\");\n");
        page.write("document.write(\"<script language='javascript' src='./scripts/highcharts.js'><\\/script>\");\n");
        page.write("//]]>\n</script>\n");
        super.writeAjaxPageHtmlVaadinScripts(window, themeName, application,
                page, appUrl, themeUri, appId, request);
    }

    @Override
    protected Class<? extends Application> getApplicationClass()
            throws ClassNotFoundException {
        return PbApplication.class;
    }

    @Override
    protected Application getNewApplication(HttpServletRequest request) throws ServletException {
    	if(panelModuleService==null)
    		panelModuleService=new PbPanelModuleServiceImpl();
    	
    	/*String configFilename=getApplicationProperty("log4jConfigLocation");
    	String real_path =  getServletContext().getRealPath("/");
    	org.apache.log4j.PropertyConfigurator.configure(real_path+configFilename);*/
        return new PbApplication(panelModuleService);
    }
}
