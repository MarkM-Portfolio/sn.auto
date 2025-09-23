package com.ibm.lconn.automation.framework.search.rest.api.tests.quickresults;

import static org.testng.AssertJUnit.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPILoggerUtil;
import com.ibm.lconn.automation.framework.search.rest.api.Verifier;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.search.request.quickresults.QuickResultsSearchRequest;
import com.ibm.lconn.automation.framework.services.search.response.QuickResultsResponse;
import com.ibm.lconn.automation.framework.services.search.response.QuickResultsResponse.Page;
import com.ibm.lconn.automation.framework.services.search.service.QuickResultsService;
import com.ibm.lconn.automation.framework.services.search.service.SearchService;

import org.apache.abdera.protocol.Response.ResponseType;
import org.apache.abdera.protocol.client.ClientResponse;
import org.testng.annotations.BeforeMethod;

public class QuickResultsTest {

	private RestAPIUser quickResultsUser;

	protected QuickResultsService quickResultsService = null;

	public Logger logger = SearchRestAPILoggerUtil.getInstance().getQuickResultsLogger();

	@BeforeMethod
	public void setUp() throws Exception {
		logger.fine("Start Initializing QuickResultsTest setUp");
		quickResultsUser = new RestAPIUser(UserType.QUICKRESULTS);
		ServiceEntry search = quickResultsUser.getService("search");

		assertTrue("Search is not available", search != null);

		quickResultsService = new QuickResultsService(quickResultsUser.getAbderaClient(), search);

	}

	public QuickResultsResponse executeAndVerifyClientResponse(String query) throws UnsupportedEncodingException {

		QuickResultsSearchRequest quickResultsSearchRequest = new QuickResultsSearchRequest(query);
		logger.fine("QuickResults request: " + quickResultsSearchRequest.toString());
		ClientResponse clientResponse = quickResultsService.typeAhead(quickResultsSearchRequest);
		QuickResultsResponse quickResultsResponse = new QuickResultsResponse(clientResponse);
		Verifier.verifyResponseTypeAndStatusCode(ResponseType.SUCCESS, 200, clientResponse);
		return quickResultsResponse;
	}

	public void verifyQuickResultsResponseTitle(QuickResultsResponse quickResultsResponse, String title) {
		assertTrue("Response is empty", quickResultsResponse.getNumResultsInCurrentPage() != 0);
		Boolean titleFound = false;
		String titleInPage = "";
		title = title.toLowerCase();
		String[] words = { title };
		if (title.contains(" ")) {
			words = title.split("\\s+");
		}

		if (quickResultsResponse.getNumResultsInCurrentPage() != 0) {
			for (QuickResultsResponse.Page entry : quickResultsResponse.getPages()) {
				titleInPage = entry.getTitle();
				String titleInPageToLowerCase = titleInPage.toLowerCase();
				for (String word : words) {

					if (!titleInPageToLowerCase.contains(word)) {
						titleFound = false;
						break;
					}
					titleFound = true;
				}
				if (!titleFound) {
					break;
				}
			}

		}
		assertTrue("This title " + titleInPage + " doesn't fit query " + title, titleFound);
	}

	public void verifyQuickResultsResponseItemType(QuickResultsResponse quickResultsResponse, String itemType) {
		assertTrue("Response is empty", quickResultsResponse.getNumResultsInCurrentPage() != 0);

		String itemTypeToCompare = "";
		String sourceToCompare = "";
		if (itemType.contains("ACTIVITY")) {
			itemTypeToCompare = "ACTIVITIES_ACTIVITY";
			sourceToCompare = "ACTIVITIES";
		}
		if (itemType.contains("ENTRY")) {
			itemTypeToCompare = "ACTIVITIES_ENTRY";
			sourceToCompare = "ACTIVITIES";
		}
		if (itemType.contains("FILE")) {
			itemTypeToCompare = "FILES_FILE";
			sourceToCompare = "FILES";
		}
		if (itemType.contains("FOLDER")) {
			itemTypeToCompare = "FILES_FOLDER";
			sourceToCompare = "FILES";
		}
		Boolean isItemType = false;

		if (quickResultsResponse.getNumResultsInCurrentPage() != 0) {
			for (QuickResultsResponse.Page entry : quickResultsResponse.getPages()) {

				if (itemTypeToCompare.equalsIgnoreCase(entry.getItemType())
						&& sourceToCompare.equalsIgnoreCase(entry.getSource())) {
					isItemType = true;
					break;
				}

			}

		}

		assertTrue("This itemType " + itemTypeToCompare + " doesn't found in response "
				+ quickResultsResponse.getQuickResultsResponseStr(), isItemType);
	}
}
