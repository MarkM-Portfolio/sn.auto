package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtSubCommunityCreatedSentenceQueryTestWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/@communities?query=";
	private static String filterWord = "First%20Manager";

	@Test
	public void fvtCommunityCreatedSentenseQueryTest() throws Exception {
		LOGGER.fine("ASS: fvtCommunityCreatedSentenseQueryTest");
		requestToExecute = FVTUtilsWithDate.createRequestURL(urlPrefix, searchString, filterWord);

		LOGGER.fine("URL: " + requestToExecute);

		int requestResult1 = FVTUtilsWithDate.sendRequestAndGetResult(requestToExecute,
				PopStringConstantsAS.SUBCOMMUNITY_CREATION_EVENT_TYPE, PopStringConstantsAS.EVENT_TYPE_LOCATION,
				PopStringConstantsAS.EVENT_TITLE_LOCATION,
				PopStringConstantsAS.MODERATED_COMMUNITY_TITLE + " " + PopStringConstantsAS.SUB_COMMUNITY_SUFFIX);

		assertTrue("Request not found", (requestResult1 > 0));

	}

}
