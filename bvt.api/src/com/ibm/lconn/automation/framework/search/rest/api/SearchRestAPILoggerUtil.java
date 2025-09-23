package com.ibm.lconn.automation.framework.search.rest.api;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import com.ibm.lconn.automation.framework.services.common.Utils;

public class SearchRestAPILoggerUtil {
	private static SearchRestAPILoggerUtil instance = null;

	private Logger searchServiceTestsLogger;

	private Logger recommendServiceTestsLogger;

	private Logger pathServiceTestsLogger;

	private Logger networkServiceTestsLogger;

	private Logger indexWaiterLogger;

	private Logger searchPopulatorLogger;

	private SearchRestAPILoggerUtil() {
	}

	public static SearchRestAPILoggerUtil getInstance() {
		if (instance == null) {
			instance = new SearchRestAPILoggerUtil();
		}
		return instance;
	}

	public Logger getSearchServiceLogger() {
		if (searchServiceTestsLogger == null) {
			searchServiceTestsLogger = Logger.getLogger("SearchServiceTest");
			FileHandler fh = null;
			try {
				DateFormat logDateFormatter = (DateFormat) Utils.logDateFormatter
						.clone();
				fh = new FileHandler("logs/"
						+ logDateFormatter.format(new Date())
						+ "_SearchServiceTest.log", false);
				searchServiceTestsLogger.addHandler(fh);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return searchServiceTestsLogger;
	}

	public Logger getRecommendServiceLogger() {
		if (recommendServiceTestsLogger == null) {
			recommendServiceTestsLogger = Logger
					.getLogger("RecommendServiceTest");
			FileHandler fh = null;
			try {
				DateFormat logDateFormatter = (DateFormat) Utils.logDateFormatter
						.clone();
				fh = new FileHandler("logs/"
						+ logDateFormatter.format(new Date())
						+ "_RecommendServiceTest.log", false);
				recommendServiceTestsLogger.addHandler(fh);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return recommendServiceTestsLogger;
	}

	public Logger getPathServiceLogger() {
		if (pathServiceTestsLogger == null) {
			pathServiceTestsLogger = Logger.getLogger("PathServiceTest");
			FileHandler fh = null;
			try {
				DateFormat logDateFormatter = (DateFormat) Utils.logDateFormatter
						.clone();
				fh = new FileHandler("logs/"
						+ logDateFormatter.format(new Date())
						+ "_PathServiceTest.log", false);
				pathServiceTestsLogger.addHandler(fh);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return pathServiceTestsLogger;
	}

	public Logger getNetworkServiceLogger() {
		if (networkServiceTestsLogger == null) {
			networkServiceTestsLogger = Logger.getLogger("NetwokServiceTest");
			FileHandler fh = null;
			try {
				DateFormat logDateFormatter = (DateFormat) Utils.logDateFormatter
						.clone();
				fh = new FileHandler("logs/"
						+ logDateFormatter.format(new Date())
						+ "_NetworkServiceTest.log", false);
				networkServiceTestsLogger.addHandler(fh);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return networkServiceTestsLogger;
	}

	public Logger getIndexWaiterLogger() {
		if (indexWaiterLogger == null) {
			indexWaiterLogger = Logger.getLogger("IndexWaiter");
			FileHandler fh = null;
			try {
				DateFormat logDateFormatter = (DateFormat) Utils.logDateFormatter
						.clone();
				fh = new FileHandler("logs/"
						+ logDateFormatter.format(new Date())
						+ "_IndexWaiter.log", false);
				indexWaiterLogger.addHandler(fh);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return indexWaiterLogger;
	}

	public Logger getPeopleFinderLogger() {
		return getLogger("PeopleFinder", searchServiceTestsLogger);

	}

	public Logger getSearchPopulatorLogger() {
		if (searchPopulatorLogger == null) {
			searchPopulatorLogger = Logger.getLogger("SearchPopulator");
			FileHandler fh = null;
			try {
				DateFormat logDateFormatter = (DateFormat) Utils.logDateFormatter
						.clone();
				fh = new FileHandler("logs/"
						+ logDateFormatter.format(new Date())
						+ "_SearchPopulator.log", false);
				searchPopulatorLogger.addHandler(fh);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return searchPopulatorLogger;
	}

	public Logger getQuickResultsLogger() {
		return getLogger("QuickResults", searchServiceTestsLogger);
	}
	
	public Logger getActivityStreamStackingLogger() {
		return getLogger("ActivityStreamStacking", searchServiceTestsLogger);
	}

	private Logger getLogger(String service, Logger logger) {
		if (logger == null) {
			logger = Logger.getLogger(service);
			FileHandler fh = null;
			try {
				DateFormat logDateFormatter = (DateFormat) Utils.logDateFormatter
						.clone();
				fh = new FileHandler("logs/"
						+ logDateFormatter.format(new Date())
						+ "_SearchServiceTest.log", false);
				logger.addHandler(fh);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return logger;
	}
}
