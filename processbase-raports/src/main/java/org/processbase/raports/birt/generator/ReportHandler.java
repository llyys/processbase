package org.processbase.raports.birt.generator;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.security.auth.login.Configuration;

import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.URLClassLoader;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IPageHandler;
import org.eclipse.birt.report.engine.api.IReportDocumentInfo;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.model.api.IDesignEngine;
import org.eclipse.birt.report.model.api.IDesignEngineFactory;
import org.processbase.raports.birt.BirtEngine;
import org.processbase.ui.core.Constants;

@SuppressWarnings("unchecked")
public class ReportHandler {
	
	/**
	 * This is an internal utility class that handles page ready events from a
	 * progrsssive viewing rendering of a report.
	 * 
	 * @author John Ward
	 * 
	 */
	public class MultiPageHandler implements IPageHandler {
		// Define local variables for the callback class
		String viewerID;
		
		public MultiPageHandler(String newViewerID) {
			viewerID = newViewerID;
		}

		/**
		 * void onPage
		 * 
		 * @param pageNumber
		 *            - the page number that is currently be called for event
		 * @param readyForViewing
		 *            - is this event a Check POint event
		 * @param reportDocument
		 *            - instance to the report document
		 */
		public void onPage(int pageNumber, boolean readyForViewing,
				IReportDocumentInfo reportDocument) {
			// we only want to do something if this is a checkpoint event
			if (readyForViewing) {
				String tempName = "";
				try {
					tempName = reportDocument.openReportDocument()
							.getReportRunnable().getReportName();
				} catch (BirtException e) {
					e.printStackTrace();
				}

				ReportRenderStorage rrs = (ReportRenderStorage) concurrentReportInfo.get(viewerID);
				// We need to notify the main app that page X of Y has been
				// reached. The user
				// will take care of calling that on their own
				// lastCheckpoint = pageNumber;
				// currentlyRenderingReport = reportDocument;
				// renderComplete = reportDocument.isComplete();
				rrs.setLastCheckpoint(pageNumber);
				rrs.setRenderComplete(reportDocument.isComplete());
				rrs.setDocInfo(reportDocument);
			}

		}
	}
	
	/**
     * Constructor, initializes the hashmap used for concurrent report information
     */
    public ReportHandler() {
            super();
            
            concurrentReportInfo = new HashMap();
            
            //initialize the random number generator
            rand = new Random();
            Date d = new Date();
            rand.setSeed(d.getTime());
            
            workFolder = "C:/TEMP/";
            
            //create the global design engine
            initPlatform();           
    }


	// constants, for BIRT home and output file location
	private static String OUTPUT_FILE_LOCATION = "C:/TEMP/";
	private static String REPORT_FOLDER = "reports/";
	protected static Logger log = Logger.getLogger(ReportHandler.class.getName());

	public static String getOUTPUT_FILE_LOCATION() {
		return OUTPUT_FILE_LOCATION;
	}

	public static void setOUTPUT_FILE_LOCATION(String output_file_location) {
		OUTPUT_FILE_LOCATION = output_file_location;
	}

	String imageDirectory = "reports/";

	int lastCheckpoint = 0; // used for progressive viewing, stores the last
							// available checkpoint

	boolean renderComplete; // stores the status of the render, true if
							// complete, false if not
	IReportDocumentInfo currentlyRenderingReport; // The information on the currently rendering report used in progressive viewing
	private Map concurrentReportInfo; // store concurrent report information
	IReportEngineFactory reportFactory; // create a new report engine factory
	IDesignEngineFactory designFactory; // create a new design engine factory

	private static IReportEngine reportEngine; // only 1 report engine per instance. This is
								// thread safe
	IDesignEngine designEngine;

	// get list of files, this will, at some point, be moved out into a seperate
	// module, handled by some sort
	// of file getter plugin so we can use repositories
	private List fileList;

	private String logLevel;
	private String logLocation;
	private String workFolder;
	private String tempPrefix = "BIRTTEMPPRFX";
	private String tempSuffix = "BRTTMP";

	// for random file names
	Random rand;
	private EngineConfig config;

	/**
	 * Create the report engine prerequisite: set BIRT_HOME
	 */
	
	
	
	
	/**
	 * This method will take in a map of report parameters and execute the
	 * report prerequisite: run platform start and create report engine, set
	 * image directory
	 * 
	 * @param reportName
	 * @param params
	 * @return String result
	 */
	public String executeHTMLReport(ReportItem reportItem, Map params) {
		 

		try {
			if (BirtEngine.getInstance().isBirtEnabled()) {
			// create the open report object and the render task
			File reportDesignFile = new File(reportItem.getRealPath().getAbsolutePath(),reportItem.getFilename());
			
			IReportRunnable design;
	            // Open report design
	        design = reportEngine.openReportDesign(reportDesignFile.getAbsolutePath());
	        
			IRunAndRenderTask task = reportEngine.createRunAndRenderTask(design);
			
			ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
			if (contextClassLoader != null && !contextClassLoader.equals(Platform.getContextClassLoader())) {
				 Platform.setContextClassLoader(contextClassLoader);
				 task.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, contextClassLoader);
			}
			
			// Set rendering options - such as file or stream output,
			// output format, whether it is embedded, etc
			
			HTMLRenderOption options = new HTMLRenderOption();
			options.setImageDirectory(imageDirectory);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(); // used instead of a temporary file
																		
			options.setOutputStream(bos);
			//options.setOutputFormat(HTMLRenderOption.HTML);
			options.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
			/*options.setImageHandler(new HTMLServerImageHandler());
			
            options.setBaseImageURL(aRequest.getContextPath() + "/images");
            options.setImageDirectory(aServletContext.getRealPath("/images"));
            options.setLayoutPreference(HTMLRenderOption.LAYOUT_PREFERENCE_AUTO);
            options.setEmbeddable(true);
            */
			task.setRenderOption(options);

			// set the parameters and run the report
			if (params != null) {
				task.setParameterValues(params);
			}

			task.run();

			String toReturn = bos.toString();
			bos.close();
			task.close();

			// reportEngine.destroy();
			return toReturn;
			}
			else{
				return "Birt is deactivated";
			}
		} catch (EngineException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * This method will open a report and create a IReportRunnable object
	 * 
	 * @param reportName
	 * @return IReportRunnable
	 */
	public IReportRunnable openReportDesign(IReportEngine engine, String reportName) {
		try {
			IReportRunnable design = engine.openReportDesign(reportName);

			return design;
		} catch (EngineException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Initialize the platform prerequisite: set BIRT_HOME
	 */
	public void initPlatform() {
		if(reportEngine!=null) return;
		config = new EngineConfig();
		File birtHome=new File(System.getProperty("BIRT_HOME"));
		//config.setBIRTHome(birtHome.getAbsolutePath());
		
		List<URL> urls=new ArrayList<URL>();
		File libs=new File(birtHome.getAbsolutePath(), "plugins");		
		for (File lib : libs.listFiles()) {
			try {
				if(lib.isFile())
					urls.add(lib.toURI().toURL());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		URL[] a = urls.toArray(new URL[0]);
		URLClassLoader birtLoader=new URLClassLoader(a, Thread.currentThread().getContextClassLoader());
		
		config.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, birtLoader);
		
		try {
			Platform.startup(config);
		} catch (BirtException e) {
			e.printStackTrace();
		}
		// create a new report engine factory
		reportFactory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
		designFactory = (IDesignEngineFactory) Platform.createFactoryObject(IDesignEngineFactory.EXTENSION_DESIGN_ENGINE_FACTORY);
		
		reportEngine = reportFactory.createReportEngine(config);
	}

	/**
	 * Given reportname, this method will return the Reports Title
	 * 
	 * @param reportName
	 * @return
	 */
	public String getReportTitle(String reportName) {
		// IReportEngine reportEngine = createReportEngine();
		String reportTitle = null;

		try {
			// open the report design, and get the propery under TITLE
			IReportRunnable design = reportEngine.openReportDesign(reportName);

			reportTitle = (String) design.getProperty(IReportRunnable.TITLE);
		} catch (EngineException e) {
			e.printStackTrace();
		}

		// reportEngine.destroy();
		return reportTitle;
	}

}

