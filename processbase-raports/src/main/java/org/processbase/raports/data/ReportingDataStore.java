package org.processbase.raports.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bonitasoft.console.client.ItemUpdates;
import org.bonitasoft.console.client.ReportFilter;
import org.bonitasoft.console.client.SimpleFilter;
import org.bonitasoft.console.client.exception.ConsoleException;
import org.bonitasoft.console.client.reporting.ReportScope;
import org.bonitasoft.console.client.reporting.ReportUUID;
import org.bonitasoft.console.client.reporting.exception.ReportNotFoundException;
import org.bonitasoft.console.client.users.UserProfile;
import org.bonitasoft.console.security.client.privileges.RuleType;
import org.bonitasoft.console.security.server.accessor.PreferencesProperties;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.identity.User;
import org.ow2.bonita.util.AccessorUtil;
import org.processbase.raports.birt.generator.ReportItem;
import org.processbase.raports.util.ReportException;
import org.processbase.ui.core.BPMModule;


public class ReportingDataStore {
	 protected final Logger LOGGER = Logger.getLogger(ReportingDataStore.class.getName());

	    private static final ReportingDataStore INSTANCE = new ReportingDataStore();

	    public static final String REPORTS_LOCATION_PATH = "/Reports/";
	    public static final String LIBS_LOCATION_PATH = REPORTS_LOCATION_PATH + "lib";
	    protected static final String CSV_SEPARATOR = ",";
	    protected static final String INDEX_FILENAME = "report.idx";
	    protected static final String REPORTS_DIRNAME = "Reports";
	    protected static final String UUID_KEY = "reportUUID";
	    protected static final String FILENAME_KEY = "reportFileName";
	    protected static final String TYPE_KEY = "reportType";
	    protected static final String SCOPE_KEY = "reportScope";
	    protected static final String DESCRIPTION_KEY = "reportDescription";
	    protected static final String PARAMETER_LIST_KEY = "reportInputParameters";

	    private static final String GLOBAL_REPORTING_ACTIVATION_STATE = "user_xp_global_reporting_enable";
	    private static final String USER_REPORTING_ACTIVATION_STATE = "user_xp_user_reporting_enable";
	    private static final String USER_REPORTING_AUTOREFRESH_FREQUENCY = "user_xp_user_reporting_refresh_frequency";

	    private static final int DEFAULT_REFRESH_FREQUENCY_IN_MINUTES = 5 * 60 * 1000;

	    private static final String REPORT_UUID_SEPARATOR = CSV_SEPARATOR;
	    private static final String DESIGN_LIST_FOR_ADMIN = "userXP.admin.reports";

	    /**
	     * @return the instance
	     */
	    public static ReportingDataStore getInstance() {
	        return INSTANCE;
	    }

	    /**
	     * Get the reporting configuration based on the metadata stored in the D.B. of the engine.
	     * 
	     * @param aUserUUID
	     * @return
	     */
	    public ReportingConfiguration getReportingConfiguration(User aUserUUID) {

	        if (aUserUUID == null || aUserUUID.getUUID() == null || aUserUUID.getUUID().equals("")) {
	            throw new IllegalArgumentException("Invalid user.");
	        }
	        
	        final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();

	        ReportingConfiguration theResult = new ReportingConfiguration();
/*	        String theState = null;
	        // Get the metadata for the global settings.
	        theState = managementAPI.getMetaData(GLOBAL_REPORTING_ACTIVATION_STATE);
	        if (!"false".equalsIgnoreCase(theState)) {
	            // Empty metadata means true.
	            theResult.setGlobalReportingEnabled(true);
	        }
	        // Get the metadata for the user settings.
	        theState = managementAPI.getMetaData(USER_REPORTING_ACTIVATION_STATE);
	        if (!"false".equalsIgnoreCase(theState)) {
	            // Empty metadata means true.
	            theResult.setUserReportingEnabled(true);

	            // Get the metadata for the user auto-refresh frequency (only if the
	            // user reporting is enabled).
	            // Empty metadata means DEFAULT_REFRESH_FREQUENCY_IN_MINUTES.
	            theState = managementAPI.getMetaData(USER_REPORTING_AUTOREFRESH_FREQUENCY);
	            if (theState != null) {
	                try {
	                    theResult.setDashBoardRefreshFrequency(Integer.parseInt(theState));
	                } catch (Exception theE) {
	                    LOGGER.equals("Invalid value stored in metadata " + USER_REPORTING_AUTOREFRESH_FREQUENCY + " found " + theState + " but only numerical value are allowed!");
	                    theResult.setDashBoardRefreshFrequency(DEFAULT_REFRESH_FREQUENCY_IN_MINUTES);
	                }
	            } else {
	                theResult.setDashBoardRefreshFrequency(DEFAULT_REFRESH_FREQUENCY_IN_MINUTES);
	            }

	        }*/

	        //theResult.setRemainingDaysForAtRiskSteps(PreferencesDataStore.getInstance().getIntegerValue(PreferencesDataStore.REMAINING_DAYS_FOR_STEP_ATRISK_KEY, 0));

	        return theResult;
	    }

	    /**
	     * Update the reporting configuration. Data are stored in the D.B. of the engine as global metadata.
	     * 
	     * @param aUserUUID
	     * @param aConfiguration
	     * @throws ConsoleException
	     */
	    public void updateReportingConfiguration(User aUserUUID, ReportingConfiguration aConfiguration) throws Exception {
	        if (aUserUUID == null || aUserUUID.getUUID() == null || aUserUUID.getUUID().equals("")) {
	            throw new IllegalArgumentException("Invalid user.");
	        }

	        if (aConfiguration == null) {
	            throw new IllegalArgumentException("Invalid reporting configuration.");
	        }

	        final ManagementAPI managementAPI = AccessorUtil.getManagementAPI();
	        if (aConfiguration.isGlobalReportingEnabled()) {
	            // Empty metadata means true.
	            managementAPI.deleteMetaData(GLOBAL_REPORTING_ACTIVATION_STATE);
	        } else {
	            managementAPI.addMetaData(GLOBAL_REPORTING_ACTIVATION_STATE, "false");
	        }

	        if (aConfiguration.isUserReportingEnabled()) {
	            // Empty metadata means true.
	            managementAPI.deleteMetaData(USER_REPORTING_ACTIVATION_STATE);
	        } else {
	            managementAPI.addMetaData(USER_REPORTING_ACTIVATION_STATE, "false");
	        }

	        if (aConfiguration.getDashBoardRefreshFrequency() == DEFAULT_REFRESH_FREQUENCY_IN_MINUTES) {
	            // Empty metadata means true.
	            managementAPI.deleteMetaData(USER_REPORTING_AUTOREFRESH_FREQUENCY);
	        } else {
	            managementAPI.addMetaData(USER_REPORTING_AUTOREFRESH_FREQUENCY, String.valueOf(aConfiguration.getDashBoardRefreshFrequency()));
	        }
/*
	        try {
	            PreferencesDataStore.getInstance().setIntegerPreference(PreferencesDataStore.REMAINING_DAYS_FOR_STEP_ATRISK_KEY, aConfiguration.getRemainingDaysForAtRiskSteps());
	        } catch (IOException e) {
	            e.printStackTrace();
	            LOGGER.log(Level.SEVERE, "Unable to persist the preference: " + e.getMessage());
	            throw new ConsoleException();
	        }
*/
	    }

	    public File getProvidedReportDir(ServletContext servletContext) throws ReportException {
	        if (servletContext != null) {
	            final File theResult = new File(servletContext.getRealPath(REPORTS_LOCATION_PATH));
	            
	                LOGGER.debug("Provided reports directory: " + theResult.getAbsolutePath());
	            return theResult;
	        } else {
	            throw new ReportException("Unable to determine in which folder to store the reports. Please specify the system property 'BONITA_HOME'.", null);
	        }
	    }

	    protected File getProvidedLibDir(String servletRealPath) throws ReportException {
	        if (servletRealPath != null) {
	            final File theResult = new File(servletRealPath, LIBS_LOCATION_PATH);
	            LOGGER.debug("Provided libs directory: " + theResult.getAbsolutePath());
	            if (theResult.exists()) {
	                return theResult;
	            } else {
	                return null;
	            }
	        } else {
	            throw new ReportException("Unable to determine in which folder to store the reports. Please specify the system property 'BONITA_HOME'.", null);
	        }
	    }

	    public ReportItem getItem(String anItemUUID, ReportUUID reportUUID, SimpleFilter aFilter, ServletContext aServletContext) throws Exception {
	        // Check for a provided report
	        final File theProvidedReportsFolder = getProvidedReportDir(aServletContext);
	        File theIndexFile = new File(theProvidedReportsFolder, anItemUUID + File.separator + INDEX_FILENAME);
	        if (theIndexFile.exists()) {
	            return loadReportItemFromFile(theIndexFile, false);
	        } else {
	            // throw new RuntimeException(theIndexFile.getAbsolutePath());
	            throw new Exception(anItemUUID+" report not found");
	        }
	    }

	    protected ReportItem loadReportItemFromFile(File anIndexFile, boolean editable) throws ReportException {
	        if (anIndexFile == null || !anIndexFile.exists()) {
	            throw new ReportException("Unable to load a report from an non existing file.", new FileNotFoundException());
	        }
	        final Properties theProperties = new Properties();
	        try {
	            theProperties.load(new FileInputStream(anIndexFile));
	            final String theID = theProperties.getProperty(UUID_KEY);
	            final String theFilename = theProperties.getProperty(FILENAME_KEY);
	            final String theDescription = theProperties.getProperty(DESCRIPTION_KEY);
	            final String theReportTypeStr = theProperties.getProperty(TYPE_KEY);
	            final String theReportScopeStr = theProperties.getProperty(SCOPE_KEY);
	            final String theReportType = theReportTypeStr;
	            final ReportScope theReportScope = ReportScope.valueOf(theReportScopeStr);
	            final String theReportInputParameters = theProperties.getProperty(PARAMETER_LIST_KEY);
	            final ReportItem theReportItem = new ReportItem(theID, theFilename, theDescription, theReportType, theReportScope, editable);
	            if (theReportInputParameters != null) {
	                final String[] theParameters = theReportInputParameters.split(",");
	                final List<String> theParameterList = Arrays.asList(theParameters);
	                final Set<String> theNewConfiguration = new HashSet<String>(theParameterList);
	                theReportItem.setConfigurationElements(theNewConfiguration);
	            }
	            return theReportItem;
	        } catch (FileNotFoundException e) {
	            throw new ReportException("Unable to load a report from an non existing file.", e);
	        } catch (IOException e) {
	            throw new ReportException("Unable to load a report from file.", e);
	        }
	    }

	    /**
	     * @param aUserProfile
	     * @param aServletContext
	     * @param aAnItemFilter
	     * @throws ConsoleException
	     * @throws ReportException 
	     */
	    public ItemUpdates<ReportItem> getItems(UserProfile aUserProfile, ReportFilter anItemFilter, ServletContext aServletContext) throws ConsoleException, ReportException {

	        File theReportDir = getProvidedReportDir(aServletContext);
	        final List<ReportItem> theReportItemList = new ArrayList<ReportItem>();
	        listReportsFromDirectory(theReportDir, theReportItemList, false);
	        //Collections.sort(theReportItemList);
	        final ItemUpdates<ReportItem> theResult = filterReportsBasedOnFilter(theReportItemList, anItemFilter, aUserProfile);
	        return theResult;
	    }

	    private void listReportsFromDirectory(File theReportDir,
				List<ReportItem> theReportItemList, boolean b) {
			// TODO Auto-generated method stub
			
		}

		/**
	     * @param aResult
	     * @param aUserProfile
	     * @param aAnItemFilter
	     * @return
	     * @throws ConsoleException
	     */
	    protected ItemUpdates<ReportItem> filterReportsBasedOnFilter(List<ReportItem> aReportList, ReportFilter anItemFilter, UserProfile aUserProfile) throws ConsoleException {
	        if (anItemFilter == null) {
	            throw new ConsoleException("Invalid report filter!", null);
	        }
	        if (aReportList.size() == 0) {
	            return new ItemUpdates<ReportItem>(aReportList, 0);
	        }
	        if (anItemFilter.getStartingIndex() >= aReportList.size()) {
	            return new ItemUpdates<ReportItem>(new ArrayList<ReportItem>(), 0);
	        }

	        int theUpperBound = anItemFilter.getStartingIndex() + anItemFilter.getMaxElementCount();

	        if (anItemFilter.isWithAdminRights()) {
	            // Admin UI
	            if (aUserProfile.isAdmin()) {
	                if (theUpperBound > aReportList.size()) {
	                    theUpperBound = aReportList.size();
	                }
	                return new ItemUpdates<ReportItem>(aReportList.subList(anItemFilter.getStartingIndex(), theUpperBound), aReportList.size());
	            } else {
	                final List<ReportItem> theManageableListOfReports = new ArrayList<ReportItem>();
	                for (ReportItem theReportItem : aReportList) {
	                    if (aUserProfile.isAllowed(RuleType.REPORT_MANAGE, theReportItem.getUUID().getValue())) {
	                        if (anItemFilter.getScope() == null || anItemFilter.getScope() == ReportScope.ALL || theReportItem.getScope() == anItemFilter.getScope()) {
	                            theManageableListOfReports.add(theReportItem);
	                        } else {
	                            
	                                LOGGER.debug("Report filtered due to scope conflict");
	                            
	                        }
	                    }
	                }
	                if (anItemFilter.getStartingIndex() >= theManageableListOfReports.size()) {
	                    return new ItemUpdates<ReportItem>(new ArrayList<ReportItem>(), 0);
	                }
	                if (theUpperBound > theManageableListOfReports.size()) {
	                    theUpperBound = theManageableListOfReports.size();
	                }
	                return new ItemUpdates<ReportItem>(theManageableListOfReports.subList(anItemFilter.getStartingIndex(), theUpperBound), theManageableListOfReports.size());
	            }
	        } else {
	            // User UI
	            final List<ReportItem> theReadableListOfReports = new ArrayList<ReportItem>();
	            for (ReportItem theReportItem : aReportList) {
	                if (aUserProfile.isAllowed(RuleType.REPORT_VIEW, theReportItem.getUUID().getValue())) {
	                    if (anItemFilter.getScope() == null || (theReportItem.getScope() == anItemFilter.getScope())) {
	                        theReadableListOfReports.add(theReportItem);
	                    }
	                }
	            }
	            if (anItemFilter.getStartingIndex() >= theReadableListOfReports.size()) {
	                return new ItemUpdates<ReportItem>(new ArrayList<ReportItem>(), 0);
	            }
	            if (theUpperBound > theReadableListOfReports.size()) {
	                theUpperBound = theReadableListOfReports.size();
	            }
	            return new ItemUpdates<ReportItem>(theReadableListOfReports.subList(anItemFilter.getStartingIndex(), theUpperBound), theReadableListOfReports.size());
	        }
	    }

	    
	    public List<ReportItem> listReportsFromDirectory(File aReportDir,  boolean editable) throws ReportException {
	        if (aReportDir.exists() && aReportDir.isDirectory()) {
	        	List<ReportItem> aReportItemList=new ArrayList<ReportItem>();
	            String[] children = aReportDir.list();
	            File theChild;
	            File theIndexFile;
	            ReportItem theReport;
	            for (int i = 0; i < children.length; i++) {
	                theChild = new File(aReportDir, children[i]);
	                if (theChild.isDirectory() /*
	                                            * &&(theFile.getName().endsWith( "rpttemplate") || theFile.getName()
	                                            * .endsWith("rptdesign"))
	                                            */) {
	                    theIndexFile = new File(theChild, INDEX_FILENAME);
	                    
	                    if (theIndexFile.exists()) {
	                        theReport = loadReportItemFromFile(theIndexFile, editable);
	                        theReport.setReportPath(theChild);
	                        aReportItemList.add(theReport);
	                    }
	                }
	            }
	            return aReportItemList;
	        }
			return null;
	    }

	    /**
	     * List items.
	     * 
	     * @param aUserProfile
	     * @param aAnItemSelection
	     * @param aFilter
	     * @param aServletContext
	     * @throws ConsoleException
	     * @throws ReportNotFoundException
	     */
	   public List<ReportItem> getItems(UserProfile aUserProfile, List<ReportUUID> anItemSelection, SimpleFilter aFilter, ServletContext aServletContext) throws ConsoleException, ReportNotFoundException {
	        List<ReportItem> theResult = new ArrayList<ReportItem>();
	        for (ReportUUID theReportUUID : anItemSelection) {
	            theResult.add(getItem(aUserProfile, theReportUUID, aFilter, aServletContext));
	        }
	        return theResult;
	    }

	    private ReportItem getItem(UserProfile aUserProfile,
				ReportUUID theReportUUID, SimpleFilter aFilter,
				ServletContext aServletContext) throws ReportNotFoundException {
			// TODO Auto-generated method stub
			return null;
		}

		public File getReportFile(String anItemUUID, ServletContext aServletContext) throws ReportException {
	        // Check for a provided report
	        final File theProvidedReportsFolder = getProvidedReportDir(aServletContext);
	        File theIndexFile = new File(theProvidedReportsFolder, anItemUUID + File.separator + INDEX_FILENAME);
	        ReportItem theReportItem;
	        if (theIndexFile.exists()) {
	            theReportItem = loadReportItemFromFile(theIndexFile, false);
	        } else {
	            throw new ReportException("Report not found " + anItemUUID);
	        }
	        File theReportFile = new File(theIndexFile.getParentFile(), theReportItem.getFilename());
	        if (theReportFile.exists()) {
	            return theReportFile;
	        } else {
	            throw new ReportException("Report not found " + anItemUUID);
	        }
	    }

	    // Monitoring settings
	    public List<ReportItem> listDesignToDisplayInMonitoringView(UserProfile aUserProfile, SimpleFilter aFilter, ServletContext aServletContext) throws ReportNotFoundException, ConsoleException {

	        String theListInString = PreferencesProperties.getInstance().getProperty(DESIGN_LIST_FOR_ADMIN);
	        LOGGER.debug("Reading property (" + DESIGN_LIST_FOR_ADMIN + ") : " + theListInString);
	        List<ReportItem> theResult = new ArrayList<ReportItem>();
	        if (theListInString == null || theListInString.length() == 0) {
	            return theResult;
	        }
	        String[] theReportIds = theListInString.split(REPORT_UUID_SEPARATOR);
	        ReportItem theReportItem;
	        for (String theReportId : theReportIds) {
	            try {
	                theReportItem = getItem(aUserProfile, new ReportUUID(theReportId), aFilter, aServletContext);
	                theResult.add(theReportItem);
	            } catch (ReportNotFoundException e) {
	                // Report has been deleted.
	                // Remove report from the list.
	            	LOGGER.error("Report '" + theReportId + "' has not been found while it is referenced to be displayed in Monitoring view. The report will be ignored.", e);
	                
	            }
	        }
	        return theResult;
	    }

	    public void setDesignToDisplayInMonitoringView(List<ReportUUID> aNewList) throws IOException {
	        if (aNewList == null || aNewList.isEmpty()) {
	            PreferencesProperties.getInstance().removeProperty(DESIGN_LIST_FOR_ADMIN);
	            LOGGER.info("Property removed: " + DESIGN_LIST_FOR_ADMIN);
	            return;
	        }

	        StringBuilder theListInString = new StringBuilder();
	        for (ReportUUID theReport : aNewList) {
	            theListInString.append(theReport);
	            theListInString.append(REPORT_UUID_SEPARATOR);
	        }
	        String theNewValue = theListInString.substring(0, theListInString.lastIndexOf(REPORT_UUID_SEPARATOR));
	        PreferencesProperties.getInstance().setProperty(DESIGN_LIST_FOR_ADMIN, theNewValue);
	        
	        LOGGER.info("Property updated (" + DESIGN_LIST_FOR_ADMIN + ") : " + theNewValue);
	        
	    }

}
