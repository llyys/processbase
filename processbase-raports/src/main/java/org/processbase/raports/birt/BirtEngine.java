package org.processbase.raports.birt;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.bonitasoft.console.security.server.accessor.PreferencesProperties;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.IPlatformContext;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.core.framework.PlatformServletContext;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;

public class BirtEngine {
	 private final String DEFAULT_LOGGING_LEVEL = "WARNING";

	    private final String DEFAULT_LOGGING_DIRECTORY = "../logs";

	    protected final Logger LOGGER = Logger.getLogger(BirtEngine.class.getName());

	    private IReportEngine birtEngine = null;

	    private Properties configProps = new Properties();

	    private final String configFile = "BirtConfig.properties";

	    private static BirtEngine INSTANCE = null;

	    public static synchronized BirtEngine getInstance() {
	        if (INSTANCE == null) {
	            INSTANCE = new BirtEngine();
	        }
	        return INSTANCE;
	    }

	    public synchronized void initBirtConfig() {
	        loadEngineProps();
	    }

	    public boolean isBirtEnabled() {
	        if (LOGGER.isLoggable(Level.FINE)) {
	            LOGGER.fine("Detecting os and deployment architecture...");
	        }
	        String os = System.getProperty("os.name", "unknown");
	        String deactivateBirt = System.getProperty("deactivate.birt", "false");
	        String javaVersion = System.getProperty("java.version", "999");
	        boolean isOnMac = os.startsWith("Mac OS");
	        boolean isInEclipse = true;
	        try {
	            Class.forName("org.bonitasoft.studio.console.Activator");
	        } catch (ClassNotFoundException e) {
	            isInEclipse = false;
	        }
	        boolean deactivate = !deactivateBirt.equalsIgnoreCase("false");
	        boolean startBirt = !deactivate && !(isOnMac && isInEclipse && (javaVersion.compareTo("1.5.0_17") >= 0) && (javaVersion.compareTo("1.5.0_20") <= 0));

	        if (LOGGER.isLoggable(Level.FINE)) {
	            LOGGER.fine("deactivate=" + deactivate + ", os=" + os + ", isOnMac=" + isOnMac + ", isInEclipse=" + isInEclipse + " => startBirt=" + startBirt);
	        }
	        return startBirt;
	    }

	    @SuppressWarnings("unchecked")
	    public synchronized IReportEngine getBirtEngine(ServletContext sc, ClassLoader anOriginalClassLoader) {

	        if (birtEngine == null && isBirtEnabled()) {

	            initBirtConfig();

	            EngineConfig config = new EngineConfig();
	            if (configProps != null) {
	                String logLevel = configProps.getProperty("logLevel");
	                Level level = Level.OFF;
	                if ("SEVERE".equalsIgnoreCase(logLevel)) {
	                    level = Level.SEVERE;
	                } else if ("WARNING".equalsIgnoreCase(logLevel)) {
	                    level = Level.WARNING;
	                } else if ("INFO".equalsIgnoreCase(logLevel)) {
	                    level = Level.INFO;
	                } else if ("CONFIG".equalsIgnoreCase(logLevel)) {
	                    level = Level.CONFIG;
	                } else if ("FINE".equalsIgnoreCase(logLevel)) {
	                    level = Level.FINE;
	                } else if ("FINER".equalsIgnoreCase(logLevel)) {
	                    level = Level.FINER;
	                } else if ("FINEST".equalsIgnoreCase(logLevel)) {
	                    level = Level.FINEST;
	                } else if ("OFF".equalsIgnoreCase(logLevel)) {
	                    level = Level.OFF;
	                }

	                config.setLogConfig(configProps.getProperty("logDirectory"), level);
	            }

	            config.setEngineHome("");
	            IPlatformContext context = new PlatformServletContext(sc);
	            config.setPlatformContext(context);

	            try {

	                if (anOriginalClassLoader != null) {
	                    LOGGER.info("Using custom class loader for the OSGi platform.");
	                    config.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY, anOriginalClassLoader);
	                } else {
	                    if (LOGGER.isLoggable(Level.FINE)) {
	                        LOGGER.warning("Using default class loader for the OSGi platform. (Custom reports may not be available.)");
	                    }
	                }

	                LOGGER.info("Starting the OSGi Platform...");
	                Platform.startup(config);
	                LOGGER.info("OSGi Platform started.");

	            } catch (BirtException e) {
	                /*
	                 * try to shutdown the platform if it doesn't start correctly to
	                 * avoid memory leak
	                 */
	                Platform.shutdown();
	                e.printStackTrace();
	                LOGGER.severe("Unable to start the OSGi Platform: " + e.getMessage());
	            }

	            IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
	            if (factory != null) {
	                birtEngine = factory.createReportEngine(config);
	            } else {
	                LOGGER.severe("Unable to build the factory object.");
	                /* Shutdown the platform if we can't get a BirtEngine */
	                Platform.shutdown();
	                throw new RuntimeException("Unable to build the BIRT report engine factory.");
	            }

	        }

	        return birtEngine;
	    }

	    public synchronized void destroyBirtEngine() {
	        if (birtEngine == null) {
	            return;
	        }
	        birtEngine.destroy();

	        LOGGER.info("Stopping the OSGi Platform...");
	        Platform.shutdown();
	        LOGGER.info("OSGi Platform stopped.");
	        /* Liberate all resources and the static ones!! */
	        configProps = null;
	        birtEngine = null;
	        INSTANCE = null;
	    }

	    public Object clone() throws CloneNotSupportedException {
	        throw new CloneNotSupportedException();
	    }

	    private void loadEngineProps() {
	        InputStream in = null;
	        try {
	            final String bonita_home = System.getProperty("BONITA_HOME");
	            final File theConfDir = PreferencesProperties.getInstance().getXPConfFolder(); 
	            final File theConfigFile = new File(theConfDir, configFile);

	            if (theConfigFile.exists()) {
	                in = new FileInputStream(theConfigFile);
	                configProps.load(in);
	                String theOutputDir = (String)configProps.get("logDirectory"); 
	                if(theOutputDir!=null) {
	                    theOutputDir = theOutputDir.replace("$BONITA_HOME", bonita_home);
	                    theOutputDir = theOutputDir.replace("%BONITA_HOME%", bonita_home);
	                    configProps.put("logDirectory", theOutputDir);
	                }
	                LOGGER.info("Configuration was updated with content of file: " + theConfigFile.getAbsolutePath());
	            } else {
	                LOGGER.warning("Config file not found: " + theConfigFile.getAbsolutePath() + "\n" + "Using default values for logDirectory: " + DEFAULT_LOGGING_DIRECTORY + "\n"
	                        + "Using default values for logLevel: " + DEFAULT_LOGGING_LEVEL);
	                configProps.put("logDirectory", DEFAULT_LOGGING_DIRECTORY);
	                configProps.put("logLevel", DEFAULT_LOGGING_LEVEL);
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
	        }

	    }

}
