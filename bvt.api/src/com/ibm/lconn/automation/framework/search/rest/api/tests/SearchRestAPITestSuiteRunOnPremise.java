package com.ibm.lconn.automation.framework.search.rest.api.tests;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;

import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.QuickResultsPostsCreator;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService.FileContentIndexStatus;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService.FileExtractStatus;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService.IndexNowStatus;
import com.ibm.lconn.automation.framework.services.common.TestNGEnv;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class SearchRestAPITestSuiteRunOnPremise {
	protected final static Logger LOGGER = LoggerFactory.getLogger(SearchRestAPITestSuiteRunOnPremise.class.getName());

	@BeforeTest
	public static void setUp() throws IOException, Exception {
		LOGGER.debug("SearchRestAPITestSuiteRunOnPremise setup BEGIN");
		// if SERVER_URL not set, use default value in testEnv
		if (URLConstants.SERVER_URL.equalsIgnoreCase("")) {
			TestNGEnv.setTestEnv();
		}
		SearchAdminService searchAdminService = new SearchAdminService();

		Populator.populationForRunOnPremise();

		doIndexNow(searchAdminService);
		LOGGER.debug("QuickResults Run setup");
		new QuickResultsPostsCreator();
		LOGGER.debug("SearchRestAPITestSuiteRunOnPremise setup STOP");
	}

	private static void doIndexNow(SearchAdminService searchAdminService) throws UnsupportedEncodingException {
		String components = "profiles, dogear, communities, activities, blogs, forums, wikis, people_finder, files, status_updates";
		if (searchAdminService.indexNow(components) == IndexNowStatus.UNKNOWN) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			searchAdminService.indexNow(components);
		}

		if (searchAdminService.indexNow(components) == IndexNowStatus.UNKNOWN) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			searchAdminService.indexNow(components);
		}
		if (searchAdminService.fileExtractNow("files,activities") == FileExtractStatus.FAILED) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			searchAdminService.fileExtractNow("files,activities");
		}
		if (searchAdminService.fileContentIndexNow() == FileContentIndexStatus.FAILED) {
			try {
				Thread.sleep(30000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			searchAdminService.fileContentIndexNow();
		}
		searchAdminService.sandIndexNow();

	}

}
