package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.net.URLEncoder;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtCommunitiyCreatedAccentCharactersQueryTestWithDate extends ActivityStreamSearchTest {

	private static final String REQUEST_PUBLIC_ALL_ALL = "/@public/@all/@all?query=";
	private static final String REQUEST_PUBLIC_ALL_COMMUNITIES = "/@public/@all/@communities?query=";

	private static String filterWord11 = "\u0066\u0072\u0061\u006e\u00e7\u0061\u0069\u0073";

	private static String htmlTypeStringToCompare = "Mon fran&ccedil;ais &eacute;quipes tigres et des g&acirc;teaux";

	@Test
	public void fvtCommunityCreatedAccentCharactersPublicAllAll1Unicode() throws Exception {
		LOGGER.fine("fvtCommunityCreatedAccentCharactersPublicAllAll1Unicode");
		String query = URLEncoder.encode(filterWord11, "UTF-8");
		boolean testTotalResult = executeTest(urlPrefix, REQUEST_PUBLIC_ALL_ALL, query);
		assertTrue("Results not found for Request: " + requestToExecute, testTotalResult);
	}

	@Test
	public void fvtCommunityCreatedAccentCharactersPublicAllCommunities1Unicode() throws Exception {
		LOGGER.fine("fvtCommunityCreatedAccentCharactersPublicAllCommunities1Unicode");
		String query = URLEncoder.encode(filterWord11, "UTF-8");
		boolean testTotalResult2 = executeTest(urlPrefix, REQUEST_PUBLIC_ALL_COMMUNITIES, query);
		assertTrue("Results not found for Request: " + requestToExecute, testTotalResult2);
	}

	// ########################################################################################################
	// ###################################Working
	// methods######################################################
	private boolean executeTest(String urlPrefix, String request, String queryString) throws Exception {

		boolean testTotalResult = false;

		requestToExecute = FVTUtilsWithDate.createRequestURL(urlPrefix, request, queryString);

		int requestResult4 = FVTUtilsWithDate.sendRequestAndGetResult(requestToExecute,
				PopStringConstantsAS.COMMUNITY_CREATION_EVENT_TYPE, PopStringConstantsAS.EVENT_TYPE_LOCATION,
				PopStringConstantsAS.EVENT_TITLE_LOCATION_ALT_1, htmlTypeStringToCompare);

		if (requestResult4 > 0) {
			testTotalResult = true;
		}

		return testTotalResult;
	}

}
