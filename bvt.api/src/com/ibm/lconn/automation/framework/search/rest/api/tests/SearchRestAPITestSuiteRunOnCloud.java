package com.ibm.lconn.automation.framework.search.rest.api.tests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeTest;

import com.ibm.lconn.automation.framework.search.rest.api.population.FacetingPopulator;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.QuickResultsPostsCreator;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.TestNGEnv;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class SearchRestAPITestSuiteRunOnCloud {

	protected final static Logger LOGGER = LoggerFactory.getLogger(SearchRestAPITestSuiteRunOnCloud.class.getName());

	@BeforeTest
	public static void setUp() throws Exception {
		LOGGER.debug("SearchRestAPITestSuiteRunOnCloud setup");
		// if SERVER_URL not set, use default value in testEnv
		if (URLConstants.SERVER_URL.equalsIgnoreCase("")) {
			TestNGEnv.setTestEnv();
		}

		ProfileData admin = ProfileLoader.getProfile(0);
		StringConstants.ADMIN_USER_NAME = admin.getUserName();
		StringConstants.ADMIN_USER_EMAIL = admin.getEmail();
		StringConstants.ADMIN_USER_REALNAME = admin.getRealName();
		StringConstants.ADMIN_USER_PASSWORD = admin.getPassword();

		ProfileData user = ProfileLoader.getProfile(2);
		StringConstants.USER_NAME = user.getUserName();
		StringConstants.USER_EMAIL = user.getEmail();
		StringConstants.USER_REALNAME = user.getRealName();
		StringConstants.USER_PASSWORD = user.getPassword();
		Populator.populationForCloudAndOnPremise();
		Populator.populationForCloudOnly();
		FacetingPopulator.populate();

		new SearchAdminService().indexNowOnCloud();

		// for QuickResults
		LOGGER.debug("QuickResults Run setup");

		new QuickResultsPostsCreator();

	}

}
