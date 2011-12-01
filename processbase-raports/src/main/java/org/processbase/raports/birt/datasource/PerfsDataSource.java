package org.processbase.raports.birt.datasource;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.bonitasoft.console.server.persistence.PreferencesDataStore;
import org.bonitasoft.console.server.persistence.PreferencesDataStore;
import org.ow2.bonita.facade.BAMAPI;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.Misc;


public class PerfsDataSource {

	protected static final Logger LOGGER = Logger.getLogger(PerfsDataSource.class.getName());

  private static final int DEFAULT_REMAINING_DAYS = 0;

	private BAMAPI bamAPI;
	
	private synchronized BAMAPI getBamAPI() {
	  if (bamAPI == null) {
	    bamAPI = AccessorUtil.getBAMAPI();
	  }
	  return bamAPI;
	}
	@SuppressWarnings("unchecked")
	public Vector readPerf() {
		// [Priority, done, todo]
		Vector<Object[]> rtnV = new Vector<Object[]>();
		try {
			Date theSinceDate = getFirstDayOfMonth();

			rtnV
					.add(new Object[] { Misc.getActivityPriority(0, Locale.ENGLISH), getBamAPI().getNumberOfFinishedSteps(0, theSinceDate),
					    getBamAPI().getNumberOfOpenSteps(0) });
			rtnV
					.add(new Object[] { Misc.getActivityPriority(1, Locale.ENGLISH), getBamAPI().getNumberOfFinishedSteps(1, theSinceDate),
					    getBamAPI().getNumberOfOpenSteps(1) });
			rtnV
					.add(new Object[] { Misc.getActivityPriority(2, Locale.ENGLISH), getBamAPI().getNumberOfFinishedSteps(2, theSinceDate),
					    getBamAPI().getNumberOfOpenSteps(2) });
		} catch (Exception theE) {
			theE.printStackTrace();
			if (LOGGER.isLoggable(Level.SEVERE)) {
				LOGGER.severe(theE.getMessage());
			}

		}
		return rtnV;

	}

	@SuppressWarnings("unchecked")
	public Vector readUserPerf() {
		Vector<Object[]> rtnV = new Vector<Object[]>();
		// [Priority, done, todo]
		try {
			Date theSinceDate = getFirstDayOfMonth();

			rtnV.add(new Object[] { Misc.getActivityPriority(0, Locale.ENGLISH), getBamAPI().getNumberOfUserFinishedSteps(0, theSinceDate),
			    getBamAPI().getNumberOfUserOpenSteps(0) });
			rtnV.add(new Object[] { Misc.getActivityPriority(1, Locale.ENGLISH), getBamAPI().getNumberOfUserFinishedSteps(1, theSinceDate),
			    getBamAPI().getNumberOfUserOpenSteps(1) });
			rtnV.add(new Object[] { Misc.getActivityPriority(2, Locale.ENGLISH), getBamAPI().getNumberOfUserFinishedSteps(2, theSinceDate),
			    getBamAPI().getNumberOfUserOpenSteps(2) });
		} catch (Exception theE) {
			theE.printStackTrace();
			if (LOGGER.isLoggable(Level.SEVERE)) {
				LOGGER.severe(theE.getMessage());
			}

		}
		return rtnV;

	}

	private Date getFirstDayOfMonth() {
		Calendar today = Calendar.getInstance();
		today.set(Calendar.DAY_OF_MONTH, 1);
		return today.getTime();
	}

	@SuppressWarnings("unchecked")
	public Vector readUserStepsRepartition() {
		Vector<Object[]> rtnV = new Vector<Object[]>();
		try {
		  final int theNbOfRemainingDays = PreferencesDataStore.getInstance().getIntegerValue(PreferencesDataStore.REMAINING_DAYS_FOR_STEP_ATRISK_KEY, DEFAULT_REMAINING_DAYS);
			int theAtRiskSteps = getBamAPI().getNumberOfUserStepsAtRisk(theNbOfRemainingDays);
			int theOverDueTasks = getBamAPI().getNumberOfUserOverdueSteps();
			int theOpenSteps = getBamAPI().getNumberOfUserOpenSteps();
			if (theOpenSteps > 0) {
				rtnV.add(new Object[] { "at risk", theAtRiskSteps });
				rtnV.add(new Object[] { "on track", (theOpenSteps - theOverDueTasks - theAtRiskSteps) });
				rtnV.add(new Object[] { "overdue", theOverDueTasks });
			}
		} catch (Exception theE) {
			theE.printStackTrace();
			if (LOGGER.isLoggable(Level.SEVERE)) {
				LOGGER.severe("Reporting error: " + theE.getMessage());
			}
		}
		return rtnV;

	}

	@SuppressWarnings("unchecked")
	public Vector readStepsRepartition() {
		Vector<Object[]> rtnV = new Vector<Object[]>();
		try {
		  final int theNbOfRemainingDays = PreferencesDataStore.getInstance().getIntegerValue(PreferencesDataStore.REMAINING_DAYS_FOR_STEP_ATRISK_KEY, DEFAULT_REMAINING_DAYS);
			int theAtRiskSteps = getBamAPI().getNumberOfStepsAtRisk(theNbOfRemainingDays);
			int theOverDueTasks = getBamAPI().getNumberOfOverdueSteps();
			int theOpenSteps = getBamAPI().getNumberOfOpenSteps();

			if (theOpenSteps > 0) {
				rtnV.add(new Object[] { "at risk", theAtRiskSteps });
				rtnV.add(new Object[] { "on track", (theOpenSteps - theOverDueTasks - theAtRiskSteps) });
				rtnV.add(new Object[] { "overdue", theOverDueTasks });
			}
		} catch (Exception theE) {
			theE.printStackTrace();
			if (LOGGER.isLoggable(Level.SEVERE)) {
				LOGGER.severe("Reporting error: " + theE.getMessage());
			}
		}
		return rtnV;

	}

	@SuppressWarnings("unchecked")
	public Vector readFinishedCases() {
		Vector<Object[]> theResult = new Vector<Object[]>();
		try {
			boolean dataExists = false;
			int thePeriod = 14;
			List<Integer> theValues = getBamAPI().getNumberOfFinishedCasesPerDay(getDay(-thePeriod));
			for (int i = 0; i <= thePeriod; i++) {
				// FIXME make the date format static (see if it is possible with the locale management).
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern("MM/dd");
				theResult.add(new Object[] { sdf.format(getDay(-(thePeriod - i))), theValues.get(i) });
				if (theValues.get(i) > 0) {
					dataExists = true;
				}
			}
			if (!dataExists) {
				// return an empty vector if all the data are 0.
				theResult.clear();
			}
		} catch (Exception theE) {
			theE.printStackTrace();
			if (LOGGER.isLoggable(Level.SEVERE)) {
				LOGGER.severe("Reporting error: " + theE.getMessage());
			}
		}
		return theResult;

	}

	protected static Date getDay(int anOffset) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, anOffset);
		return cal.getTime();
	}

	@SuppressWarnings("unchecked")
	public Vector readActiveItems() {
		Vector<Object[]> theResult = new Vector<Object[]>();
		try {
			boolean dataExists = false;
			int thePeriod = 3;
			// List<Integer> theProcessValues = getBamAPI().getNumberOf(getDay(-thePeriod));
			List<Integer> theCasesValues = getBamAPI().getNumberOfExecutingCasesPerDay(getDay(-thePeriod));
			List<Integer> theStepsValues = getBamAPI().getNumberOfOpenStepsPerDay(getDay(-thePeriod));
			for (int i = 0; i < theCasesValues.size(); i++) {
				// FIXME make the date format static (see if it is possible with the locale management).
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern("MM/dd");
				theResult.add(new Object[] { sdf.format(getDay(-(thePeriod - i))), 0, theCasesValues.get(i), theStepsValues.get(i) });
				if ((theCasesValues.get(i) > 0) || (theStepsValues.get(i) > 0)) {
					dataExists = true;
				}
			}

			if (!dataExists) {
				// return an empty vector if all the data are 0.
				theResult.clear();
			}
		} catch (Exception theE) {
			theE.printStackTrace();
			if (LOGGER.isLoggable(Level.SEVERE)) {
				LOGGER.severe("Reporting error: " + theE.getMessage());
			}
		}
		return theResult;

	}

}

