package com.ibm.lconn.automation.framework.search.rest.api.tests.quickresults;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.population.creators.QuickResultsPostsCreator;
import com.ibm.lconn.automation.framework.services.search.response.QuickResultsResponse;

public class QuickResultsQueryTest extends QuickResultsTest {

	@Test
	public void testQuickResultsQueryActivity() throws Exception {
		logger.fine("Test testQuickResultsQueryActivity");
		String query = QuickResultsPostsCreator.ACTIVITY_STANDALONE_TITLE_TODAY;

		QuickResultsResponse quickResultsResponse = this.executeAndVerifyClientResponse(query);
		logger.fine(
				"Test testQuickResultsQueryActivity: number of responses - " + quickResultsResponse.getTotalResults());
		verifyQuickResultsResponseTitle(quickResultsResponse, query);
	}

	@Test
	public void testQuickResultsQueryFile() throws Exception {
		logger.fine("Test testQuickResultsQueryFile");
		String query = QuickResultsPostsCreator.FILE_STANDALONE_TITLE_TODAY;

		QuickResultsResponse quickResultsResponse = this.executeAndVerifyClientResponse(query);
		logger.fine("Test testQuickResultsQueryFile: number of responses - " + quickResultsResponse.getTotalResults());
		verifyQuickResultsResponseTitle(quickResultsResponse, query);
	}

	@Test
	public void testQuickResultsQueryPrefix() throws Exception {
		logger.fine("Test testQuickResultsQueryPrefix");
		String query = QuickResultsPostsCreator.QR_POPULATION_PREFIX;

		QuickResultsResponse quickResultsResponse = this.executeAndVerifyClientResponse(query);
		logger.fine(
				"Test testQuickResultsQueryPrefix: number of responses - " + quickResultsResponse.getTotalResults());
		verifyQuickResultsResponseTitle(quickResultsResponse, query);
	}

	@Test
	public void testQuickResultsJMSFile() throws Exception {
		logger.fine("Test testQuickResultsJMSFile");
		String query = QuickResultsPostsCreator.FILE_STANDALONE_TITLE_JMS;

		QuickResultsResponse quickResultsResponse = this.executeAndVerifyClientResponse(query);
		logger.fine("Test testQuickResultsJMSFile: number of responses - " + quickResultsResponse.getTotalResults());
		verifyQuickResultsResponseTitle(quickResultsResponse, query);
	}
}
