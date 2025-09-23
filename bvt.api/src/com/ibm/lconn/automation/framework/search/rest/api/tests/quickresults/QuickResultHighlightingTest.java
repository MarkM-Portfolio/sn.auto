package com.ibm.lconn.automation.framework.search.rest.api.tests.quickresults;

import static org.testng.AssertJUnit.assertTrue;

import java.util.List;
import java.util.logging.Logger;

import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.creators.CommunitiesCreatorForQuickResults;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsSearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.QuickResultsResponse;
import com.ibm.lconn.automation.framework.services.search.response.QuickResultsResponse.Page;
import com.ibm.lconn.automation.framework.services.search.service.QuickResultsService;

public class QuickResultHighlightingTest {
	private Logger LOGGER = SearchRestAPILoggerUtil.getInstance().getQuickResultsLogger();

	private QuickResultsService quickResultService;

	private ProfileData profileData;

	private RestAPIUser quickResultsUser;

	private static final String OPEN_HIGHLIGHT_TAG = "<b>";

	private static final String CLOSE_HIGHLIGHT_TAG = "</b>";

	@BeforeMethod
	public void setUp() throws Exception {

		LOGGER.fine("Start Initializing QuickResultHighlightingTest setUp");
		quickResultsUser = new RestAPIUser(UserType.QUICKRESULTS);
		ServiceEntry searchServiceEntry = quickResultsUser.getService("search");
		assert (searchServiceEntry != null);
		quickResultService = new QuickResultsService(quickResultsUser.getAbderaClient(), searchServiceEntry);

	}

	@Test
	public void testQuickResults_CheckHighlighting() throws Exception {
		LOGGER.fine("Test QuickResultHighlightingTest#testQuickResults_CheckHighlighting");
		String query = SearchRestAPIUtils.getExecId(Purpose.QUICK_RESULTS);
		QuickResultsSearchRequest request = new QuickResultsSearchRequest(query);
		request.setHighlight(true);
		request.setPageSize(100);
		ClientResponse response = quickResultService.typeAhead(request);

		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);

		QuickResultsResponse quickResultsResponse = new QuickResultsResponse(response);
		List<Page> pages = quickResultsResponse.getPages();

		String openTag = "";
		String closeTag = "";
		for (Page page : pages) {
			String title = page.getTitle();
			if (title.contains(query)) {
				int queryStart = title.indexOf(query);
				int openTagStartIndex = queryStart - OPEN_HIGHLIGHT_TAG.length();
				if (openTagStartIndex < 0) {
					continue;
				}
				openTag = title.substring(openTagStartIndex, queryStart);

				int closeTagStartIndex = queryStart + query.length();
				int closeTagStopIndex = closeTagStartIndex + CLOSE_HIGHLIGHT_TAG.length();
				closeTag = title.substring(closeTagStartIndex, closeTagStopIndex);
			}
		}

		assertTrue(
				"\"" + openTag + "\" is not equals to \"" + OPEN_HIGHLIGHT_TAG + "\"" + "or " + "\"" + closeTag
						+ "\" is not equeals to \"" + CLOSE_HIGHLIGHT_TAG + "\"",
				openTag.equals(OPEN_HIGHLIGHT_TAG) && closeTag.equals(CLOSE_HIGHLIGHT_TAG));
	}

	@Test
	public void testQuickResults_CheckNoHighlighting() throws Exception {
		LOGGER.fine("Test QuickResultHighlightingTest#testQuickResults_CheckNoHighlighting");
		String query = SearchRestAPIUtils.getExecId(Purpose.QUICK_RESULTS);
		QuickResultsSearchRequest request = new QuickResultsSearchRequest(query);
		request.setHighlight(false);
		request.setPageSize(100);
		ClientResponse response = quickResultService.typeAhead(request);

		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);

		QuickResultsResponse quickResponse = new QuickResultsResponse(response);
		List<Page> pages = quickResponse.getPages();
		for (Page page : pages) {
			String title = page.getTitle();
			assertTrue("title should not contain highlighting",
					!title.contains(OPEN_HIGHLIGHT_TAG) && !title.contains(CLOSE_HIGHLIGHT_TAG));

		}
	}

	@Test
	public void testQuickResults_JapaneseHighlighting() throws Exception {
		LOGGER.fine("Test QuickResultHighlightingTest#testQuickResults_JapaneseHighlighting");
		String execId = SearchRestAPIUtils.getExecId(Purpose.QUICK_RESULTS);
		String query = "\u65b0\u5e74";

		QuickResultsSearchRequest request = new QuickResultsSearchRequest(query);
		request.setHighlight(true);
		request.setPageSize(100);
		ClientResponse response = quickResultService.typeAhead(request);

		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, response);

		QuickResultsResponse quickResultsResponse = new QuickResultsResponse(response);
		List<Page> pages = quickResultsResponse.getPages();
		boolean found = false;
		for (Page page : pages) {
			String title = page.getTitle();
			if (title.contains(execId)) {
				found = true;
				assertTrue(
						"The title is not as expected:"
								+ CommunitiesCreatorForQuickResults.EXPECTED_TITLE_HIGHLIGHT_ON_JAPANISE_WORD,
						title.contains(CommunitiesCreatorForQuickResults.EXPECTED_TITLE_HIGHLIGHT_ON_JAPANISE_WORD));
				break;
			}
		}
		assertTrue("The expected result is not found: " + execId, found);
	}

	// @Test
	// /**
	// * Result: If name and/or email fields includes query string, it should be
	// wrapped into <b> </b> tags
	// */
	// public void testQuickResults_JapaneseHighlighting() throws Exception{
	// LOGGER.fine("Test
	// QuickResultHighlightingTest#testQuickResults_JapaneseHighlighting");
	// String execId = SearchRestAPIUtils.getExecId(Purpose.QUICK_RESULTS);
	// String query = "\u65b0\u5e74";
	//
	// QuickResultsSearchRequest request = new QuickResultsSearchRequest(query);
	// request.setHighlight(true);
	// request.setPageSize(100);
	// ClientResponse response = quickResultService.typeAhead(request);
	//
	// Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200,
	// response);
	//
	// QuickResultsResponse peopleFinderResponse = new
	// QuickResultsResponse(response);
	// List<Page> pages = peopleFinderResponse.getPages();
	// boolean found = false;
	// for (Page page : pages) {
	// String title = page.getTitle();
	// if(title.contains(execId) ) {
	// found = true;
	// assertEquals("The title is not as expected.", execId + " " +
	// CommunitiesCreatorForQuickResults.EXPECTED_TITLE_HIGHLIGHT_ON_JAPANISE_WORD,
	// title);
	// break;
	// }
	// }
	// assertTrue("The expected result is not found", found);
	// }
}
