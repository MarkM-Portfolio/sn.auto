package com.ibm.lconn.automation.framework.search.rest.api.tests;

import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Logger;

import org.testng.annotations.BeforeTest;

import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.QuickResultsPostsCreator;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.search.data.QuickResultsProfileData;
import com.ibm.lconn.automation.framework.services.search.data.QuickResultsProfileLoader;

public class QuickResultsTestsForSolrRun {

	private final static Logger logger = Populator.LOGGER_POPUILATOR;

	@BeforeTest
	public static void run() throws FileNotFoundException, IOException {
		ProfileData profData = QuickResultsProfileLoader
				.getQuickResultsProfile();

		if (profData == null) {
			profData = QuickResultsProfileLoader.getQuickResultsProfile(2);
		}
		QuickResultsProfileData quickResultsProfData = (QuickResultsProfileData) profData;
		if (quickResultsProfData.isQuickResults()) {
			new QuickResultsPostsCreator();
		} else {
			logger.fine("QuickResultsPosts: QuickResults are not enabled");
		}
		assertTrue(quickResultsProfData.isQuickResults());
	}

}
