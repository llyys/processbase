package org.processbase.raports.birt;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bonitasoft.console.client.exception.ConsoleException;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.reporting.exception.ReportNotFoundException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.EngineException;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.HTMLServerImageHandler;
import org.eclipse.birt.report.engine.api.IRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.PDFRenderOption;

import org.processbase.raports.data.ReportingDataStore;
import org.processbase.raports.util.ReportException;

public class BirtReportGenerator {
	 public static final BirtReportGenerator INSTANCE = new BirtReportGenerator();
	    protected static final ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

	    protected static final Logger LOGGER = Logger.getLogger(BirtReportGenerator.class.getName());

	    protected IReportEngine myBirtReportEngine = null;

	    protected BirtReportGenerator() {

	    }

	    public static BirtReportGenerator getInstance() {
	        return INSTANCE;
	    }

	    /** 
	     * 
	     * @param aRequest
	     * @param aResponse
	     * @param aReportId
	     * @param isCustom
	     * @param aClassLoader
	     * @throws ServletException
	     */
	    public void generateReport(HttpServletRequest aRequest, HttpServletResponse aResponse, String aReportId, ClassLoader aClassLoader) throws ServletException {
	        try {
	            if (BirtEngine.getInstance().isBirtEnabled()) {
	                // launch the engine
	                ServletContext sc = aRequest.getSession().getServletContext();

	                this.myBirtReportEngine = BirtEngine.getInstance().getBirtEngine(sc, originalClassLoader);

	                renderReport(aRequest, aResponse, aReportId, aClassLoader);
	            } else {
	                aResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
	                        "Servlet disabled in Bonita Studio when on MAC OS X with current java version; or when the deactivate.birt system property is set.");
	                LOGGER.warning("Servlet disabled in Bonita Studio when on MAC OS X with current java version; or when the deactivate.birt system property is set.");
	            }
	        } catch (Exception e) {
	            LOGGER.severe("Error occurred while processing the reporting request: " + e.getMessage());
	            e.printStackTrace();
	            throw new ServletException(e);
	        }
	    }

	    protected void renderReport(HttpServletRequest aRequest, HttpServletResponse aResponse, String aReportId, ClassLoader aReportClassLoader) throws EngineException, IOException, ServletException, ReportNotFoundException, ConsoleException {
	        if (aReportId == null || aReportId.length() == 0) {
	            throw new ServletException("No report specified.");
	        }

	        ServletContext theServletContext = aRequest.getSession().getServletContext();

	        try {
	            final ReportUUID theReportUUID = new ReportUUID(aReportId);
	            File reportDesignFile = ReportingDataStore.getInstance().getReportFile(theReportUUID.toString(), theServletContext);
	            if (!reportDesignFile.exists()) {
	                throw new ServletException("File " + reportDesignFile.getPath() + " not found.");
	            }

	            renderReportFromFile(aRequest, aResponse, aReportClassLoader, theServletContext, reportDesignFile);
	        } catch (EngineException e) {
	            LOGGER.severe(e.getMessage());
	            throw e;
	        } catch (IOException e) {
	            LOGGER.severe(e.getMessage());
	            throw e;
	        } catch (ReportException e) {
				// TODO Auto-generated catch block
	        	LOGGER.severe(e.getMessage());
				e.printStackTrace();
			}

	    }

	    /**
	     * @param aRequest
	     * @param aResponse
	     * @param aClassLoader
	     * @param theServletContext
	     * @param reportDesignFile
	     * @throws EngineException
	     * @throws IOException
	     */
	    @SuppressWarnings("unchecked")
	    protected void renderReportFromFile(HttpServletRequest aRequest, HttpServletResponse aResponse, ClassLoader aClassLoader, ServletContext theServletContext, File reportDesignFile)
	            throws EngineException, IOException {

	        Thread.currentThread().setContextClassLoader(aClassLoader);
	        try {
	            // Birt is already up and running.
	            // Check if the classloader is different from the one in use.
	            if (aClassLoader != null && !aClassLoader.equals(Platform.getContextClassLoader())) {
	                // The given classloader is different from the one in use.
	                LOGGER.info("Updating class loader for the OSGi platform.");
	                Platform.setContextClassLoader(aClassLoader);
	                myBirtReportEngine.getConfig().getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, aClassLoader);
	            } else {
	                if (LOGGER.isLoggable(Level.FINE)) {
	                    LOGGER.warning("Keep using previous class loader for the OSGi platform.");
	                }
	            }

	            IReportRunnable design;
	            // Open report design
	            design = myBirtReportEngine.openReportDesign(reportDesignFile.getAbsolutePath());
	            // create task to run and render report
	            final IRunAndRenderTask task = myBirtReportEngine.createRunAndRenderTask(design);
	            task.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, aClassLoader);

	            task.getAppContext().put("BIRT_VIEWER_HTTPSERVLET_REQUEST", aRequest);

	            // Set parameter values and validate
	          //  task.setParameterValue("BonitaCurrentUser", SessionManager.getUserProfile(aRequest).getUsername());
	          //  task.setParameterValue("Locale", SessionManager.getUserProfile(aRequest).getLocale());

	            // Add report parameters
	            task.setParameterValue("StartDate", aRequest.getParameter("StartDate"));
	            task.setParameterValue("EndDate", aRequest.getParameter("EndDate"));
	            task.setParameterValue("TimeUnit", aRequest.getParameter("TimeUnit"));
	            task.setParameterValue("ActivityType", aRequest.getParameter("ActivityType"));
	            task.setParameterValue("ProcessUUID", aRequest.getParameter("ProcessUUID"));
	            task.setParameterValue("ActivityUUID", aRequest.getParameter("ActivityUUID"));
	            task.setParameterValue("User", aRequest.getParameter("User"));

	            task.validateParameters();
	            // Build the output options and set the response content type.
	            IRenderOption options = buildOptions(aRequest, aResponse, theServletContext);

	            task.setRenderOption(options);

	            // run report
	            task.run();
	            task.close();
	        } finally {
	            //Restoring class loaders
	            if (originalClassLoader != null && !originalClassLoader.equals(Platform.getContextClassLoader())) {
	                // The original  classloader is different from the one in use.
	                LOGGER.info("Restoring class loader for the OSGi platform.");
	                Platform.setContextClassLoader(originalClassLoader);
	                myBirtReportEngine.getConfig().getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, originalClassLoader);
	            } else {
	                if (LOGGER.isLoggable(Level.FINE)) {
	                    LOGGER.warning("Keep using previous class loader for the OSGi platform.");
	                }
	            }
	            Thread.currentThread().setContextClassLoader(originalClassLoader);
	        }
	    }

	    protected IRenderOption buildOptions(HttpServletRequest aRequest, HttpServletResponse aResponse, ServletContext aServletContext) throws IOException {
	        IRenderOption theOptions = null;
	        String theOutputFormat = aRequest.getParameter("OutputFormat");
	        if (theOutputFormat != null && !"".equals(theOutputFormat)) {
	            if ("pdf".equalsIgnoreCase(theOutputFormat)) {
	                LOGGER.info("Will use pdf format to render the report as specified in the request.");
	                theOptions = new PDFRenderOption();
	                theOptions.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_PDF);
	                aResponse.setHeader("Content-Disposition", "inline; filename=\"bonita-report.pdf\"");
	                theOptions.setOutputStream(aResponse.getOutputStream());
	                // Set the response content type.
	                aResponse.setContentType("application/pdf");

	            }
	        }
	        if (theOptions == null) {
	            LOGGER.info("Will use HTML format to render the report.");
	            theOptions = new HTMLRenderOption();
	            theOptions.setOutputFormat(HTMLRenderOption.OUTPUT_FORMAT_HTML);
	            theOptions.setOutputStream(aResponse.getOutputStream());
	            theOptions.setImageHandler(new HTMLServerImageHandler());
	            ((HTMLRenderOption) theOptions).setBaseImageURL(aRequest.getContextPath() + "/images");
	            ((HTMLRenderOption) theOptions).setImageDirectory(aServletContext.getRealPath("/images"));
	            ((HTMLRenderOption) theOptions).setLayoutPreference(HTMLRenderOption.LAYOUT_PREFERENCE_AUTO);
	            ((HTMLRenderOption) theOptions).setEmbeddable(true);
	            // Set the response content type.
	            aResponse.setContentType("text/html");
	        }
	        return theOptions;
	    }

	    public void stopBirt() {
	        BirtEngine.getInstance().destroyBirtEngine();
	    }

}
