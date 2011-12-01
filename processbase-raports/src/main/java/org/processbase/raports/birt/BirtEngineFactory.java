package org.processbase.raports.birt;
import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;

import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformFileContext;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.data.engine.api.DataEngine;
import org.eclipse.birt.report.engine.api.*;
import org.apache.log4j.Logger;

import com.vaadin.Application;

public class BirtEngineFactory {
	private static Logger log = Logger.getLogger(BirtEngineFactory.class);

    private static IReportEngineFactory factory = null;
    private static EngineConfig engineConfig;
    private static IReportEngine birtEngine = null;

    public static synchronized IReportEngine init() {
       
        	
        	//HTMLServerImageHandler imageHandler = new HTMLServerImageHandler();
    		//imageHandler.
    		//HTMLActionHandler actionHandler = new GrailsHTMLActionHandler(baseURL, defaultFormat)
    		HTMLRenderOption renderOption = new HTMLRenderOption();
            //renderOption.setImageHandler(imageHandler);
    		
    		renderOption.setImageHandler(new HTMLServerImageHandler());
    		String catalinaHome=System.getProperty( "catalina.home");
    		
    		renderOption.setBaseImageURL(".VAADIN/raport");
    		File imgFolder=new File(catalinaHome+"\\webapps\\SmartBPM\\VAADIN\\raport");
    		if(imgFolder.exists()==false)
    			imgFolder.mkdir();
    		renderOption.setImageDirectory(imgFolder.getAbsolutePath());
    		renderOption.setLayoutPreference(HTMLRenderOption.LAYOUT_PREFERENCE_AUTO);
    		renderOption.setEmbeddable(true);
            
    		
            //renderOption.setActionHandler(actionHandler);
            
            HashMap appContext = new HashMap();
            appContext.put(DataEngine.MEMORY_BUFFER_SIZE, 100);
            
            engineConfig = new org.eclipse.birt.report.engine.api.EngineConfig();
    		engineConfig.setAppContext(appContext );
    		
            engineConfig.setPlatformContext(new PlatformFileContext(engineConfig));
           
            engineConfig.setEmitterConfiguration("html", renderOption);
            if(catalinaHome!=null)
            {
            	engineConfig.setLogConfig(catalinaHome+"\\logs", Level.ALL);
            	engineConfig.setLogFile("birt.log");
            	engineConfig.setLogRollingSize(10);
            	engineConfig.setLogMaxBackupIndex(10);
            	        	
            }
            
            
			System.setProperty( "RUN_UNDER_ECLIPSE", "false" ); 
			//Platform.startup(config);
			factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
			if(factory!=null)
				return factory.createReportEngine(engineConfig);
			
			return new ReportEngine(engineConfig);
			//birtEngine = factory.createReportEngine(engineConfig);
       
    }

    public static synchronized IReportEngine getEngine() {
    	if(birtEngine==null)
    		birtEngine = init();
        return birtEngine;
    }

    public static synchronized void destroy() {
        if (birtEngine != null) birtEngine.destroy();
        Platform.shutdown();
        engineConfig=null;
        factory = null;
        birtEngine = null;
    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public IRenderOption createRenderOption() {
        return new RenderOption();
    }
}
