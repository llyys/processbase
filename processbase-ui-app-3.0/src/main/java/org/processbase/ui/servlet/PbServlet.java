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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.processbase.ui.core.Constants;
import org.processbase.ui.osgi.PbPanelModule;
import org.processbase.ui.osgi.PbPanelModuleService;
import org.processbase.ui.osgi.impl.PbPanelModuleServiceImpl;

import com.google.gson.Gson;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.ApplicationServlet;
import com.vaadin.ui.Window;

public class PbServlet extends ApplicationServlet {

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
        page.write("document.write(\"<script language='javascript' src='SmartBPM/VAADIN/scripts/jquery-1.6.2.min.js'><\\/script>\");\n");
        page.write("document.write(\"<script language='javascript' src='SmartBPM/VAADIN/scripts/highcharts.js'><\\/script>\");\n");
        page.write("//]]>\n</script>\n");
        super.writeAjaxPageHtmlVaadinScripts(window, themeName, application,
                page, appUrl, themeUri, appId, request);
    }

    @Override
    protected Class<? extends Application> getApplicationClass()
            throws ClassNotFoundException {
        return PbApplication.class;
    }
    
    private void InipPanelModuleService(PbPanelModuleService moduleService){
    	 BufferedReader reader = null;
    	 try {
             File file = null;
             Map<String, String> map = new HashMap<String, String>();
             file=new File(Constants.getBonitaHomeDir()+"/processbase.modules");//global configuration can be accessed %BONITA_HOME%\processbase3.properties
             
             if (file.exists()) {
            	 
            	  reader = new BufferedReader(new FileReader(file));
            	  PbModules modules=new Gson().fromJson(reader, PbModules.class);
            	  for (PbModule module : modules.getModules()) {
            		  Class<?> class1 = Class.forName(module.getPanel());
            		  
            		  PbPanelModule panelModule = (PbPanelModule) class1.newInstance();
            		  moduleService.registerModule(panelModule);
            		  
            		  panelModule.setRoles(module.getRoles());
            	  }
            	  /*String line;
            	  while ((line = reader.readLine()) != null)
            	  {
            	    if (line.trim().length()==0) continue;
            	    if (line.charAt(0)=='#') continue;//this is a comment
            	    // assumption here is that proper lines are like "String : http://xxx.yyy.zzz/foo/bar",
            	    // and the ":" is the delimiter
            	    int delimPosition = line.indexOf(":");
            	    String key = line.substring(0, delimPosition-1).trim();
            	    String value = line.substring(delimPosition+1).trim();
            	    
            	    try{
            	    	Class<?> class1 = Class.forName(value);
						if(class1!=null){
	            	    	Object module=class1.newInstance();
	            	    	PbPanelModule panelModule = (PbPanelModule) module;
							moduleService.registerModule(panelModule);
            	    	}            	    	
            	    }
            	    catch(Exception e){
            	    	
            	    }
            	    
            	  }*/            	    
             }
         } catch (Exception ex) { 
             ex.printStackTrace();
         }
         finally{
        	 if(reader!=null)
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
         }
    }
    
    @Override
    protected Application getNewApplication(HttpServletRequest request) throws ServletException {
    	if(panelModuleService==null)
    		panelModuleService=new PbPanelModuleServiceImpl();
    	
    	
    	
    	String configFilename=getServletContext().getRealPath("/")+getApplicationProperty("log4jConfigLocation");
    	
    	if(Constants.getBonitaHomeDir()!=null)
    		configFilename=Constants.getBonitaHomeDir()+"/log4j.xml";
    	
//    	org.apache.log4j.xml.DOMConfigurator.configure(configFilename);    	
    	
    	
        PbApplication pbApplication = new PbApplication(panelModuleService);
        panelModuleService.addListener(pbApplication);
        
        pbApplication.onRequestStart(request, null);
        InipPanelModuleService(panelModuleService);
		return pbApplication;
    }
    
}
