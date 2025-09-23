package com.ibm.lconn.automation.framework.search.rest.api.tests.quickresults;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.population.creators.QuickResultsPostsCreator;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsActivityEntryViewedPostRequest;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsActivityViewedPostRequest;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsFileFolderViewedPostRequest;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsFileViewedPostRequest;
import com.ibm.lconn.automation.framework.services.search.response.QuickResultsResponse;

public class QuickResultsItemTypeTests extends QuickResultsTest {
	@Test
	public void testQuickResultsQueryActivityViewedItemType() throws Exception {
		logger.fine("Test testQuickResultsQueryActivityViewedItemType");
		String query = QuickResultsPostsCreator.ACTIVITY_STANDALONE_TITLE_TODAY;

		QuickResultsResponse quickResultsResponse = this.executeAndVerifyClientResponse(query);
		logger.fine("Test testQuickResultsQueryActivityViewedItemType: number of responses : "
				+ quickResultsResponse.getTotalResults());
		verifyQuickResultsResponseItemType(quickResultsResponse,
				QuickResultsActivityViewedPostRequest.QUICK_RESULTS_ITYPE_ACTIVITY);
	}

	@Test
	public void testQuickResultsQueryActivityEntryViewedItemType() throws Exception {
		logger.fine("Test testQuickResultsQueryActivityEntryViewedItemType");
		String query = QuickResultsPostsCreator.ACTIVITY_ENTRY_STANDALONE_TITLE_TODAY;

		QuickResultsResponse quickResultsResponse = this.executeAndVerifyClientResponse(query);
		logger.fine("Test testQuickResultsQueryActivityEntryViewedItemType: number of responses : "
				+ quickResultsResponse.getTotalResults());
		verifyQuickResultsResponseItemType(quickResultsResponse,
				QuickResultsActivityEntryViewedPostRequest.QUICK_RESULTS_ITYPE_ACTIVITY_ENTRY);
	}

	@Test
	public void testQuickResultsQueryFileViewedItemType() throws Exception {
		logger.fine("Test testQuickResultsQueryFileViewedItemType");
		String query = QuickResultsPostsCreator.FILE_STANDALONE_TITLE_TODAY;

		QuickResultsResponse quickResultsResponse = this.executeAndVerifyClientResponse(query);
		logger.fine("Test testQuickResultsQueryFileViewedItemType: number of responses : "
				+ quickResultsResponse.getTotalResults());
		verifyQuickResultsResponseItemType(quickResultsResponse,
				QuickResultsFileViewedPostRequest.QUICK_RESULTS_ITYPE_FILE);
	}

	@Test
	public void testQuickResultsQueryFileCreatedItemType() throws Exception {
		logger.fine("Test testQuickResultsQueryFileCreatedItemType");
		String query = QuickResultsPostsCreator.FILE_STANDALONE_TITLE_TODAY;

		QuickResultsResponse quickResultsResponse = this.executeAndVerifyClientResponse(query);
		logger.fine("Test testQuickResultsQueryFileCreatedItemType: number of responses : "
				+ quickResultsResponse.getTotalResults());
		verifyQuickResultsResponseItemType(quickResultsResponse,
				QuickResultsFileViewedPostRequest.QUICK_RESULTS_ITYPE_FILE);
	}

	@Test
	public void testQuickResultsQueryFileJMSItemType() throws Exception {
		logger.fine("Test testQuickResultsQueryFileJMSItemType");
		String query = QuickResultsPostsCreator.FILE_STANDALONE_TITLE_JMS;

		QuickResultsResponse quickResultsResponse = this.executeAndVerifyClientResponse(query);
		logger.fine("Test testQuickResultsQueryFileJMSItemType: number of responses : "
				+ quickResultsResponse.getTotalResults());
		verifyQuickResultsResponseItemType(quickResultsResponse,
				QuickResultsFileViewedPostRequest.QUICK_RESULTS_ITYPE_FILE);
	}

	@Test
	public void testQuickResultsQueryFileFolderItemType() throws Exception {
		logger.fine("Test testQuickResultsQueryFileFolderItemType");
		String query = QuickResultsPostsCreator.FILE_FOLDER_STANDALONE_TITLE_TODAY;

		QuickResultsResponse quickResultsResponse = this.executeAndVerifyClientResponse(query);
		logger.fine("Test testQuickResultsQueryFileFolderItemType: number of responses : "
				+ quickResultsResponse.getTotalResults());
		verifyQuickResultsResponseItemType(quickResultsResponse,
				QuickResultsFileFolderViewedPostRequest.QUICK_RESULTS_ITYPE_FILE_FOLDER);
	}
}
