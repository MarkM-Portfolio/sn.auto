package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtActivityCreatedWordQueryTestWithDate extends ActivityStreamSearchTest {

	private static String searchString2 = "/@public/@all/activities?query=";

	@Test
	public void publicAllActivitiesAuction() throws Exception {
		checkQueryWithSingleWord("Auction", searchString2, 1,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_CREATION_EVENT_TYPE,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TITLE);
	}

	@Test
	public void publicAllActivitiesThirty() throws Exception {
		checkQueryWithSingleWord("Thirty", searchString2, 1, PopStringConstantsAS.ACTIVITY_REPLY_CREATION_EVENT,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_ENTRY_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_ENTRY_TITLE);
	}

	@Test
	public void publicAllActivitiesTwenty() throws Exception {
		checkQueryWithSingleWord("Twenty", searchString2, 1, PopStringConstantsAS.ACTIVITY_ENTRY_CREATION_EVENT,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_ENTRY_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_ENTRY_TITLE);
	}

	@Test
	public void publicAllActivitiesDuty() throws Exception {
		checkQueryWithSingleWord("Duty", searchString2, 1, PopStringConstantsAS.ACTIVITY_TODO_CREATION_EVENT,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TODO_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TODO_TITLE);
	}

	// ########################################Working
	// methods#############################################################

	public void checkQueryWithSingleWord(String filterWord, String searchUrl, int expectedResult1, String activityEvent,
			String activityPublicEventTitle, String activityPrivateEventTitle) throws Exception {

		requestToExecute = FVTUtilsWithDate.createRequestURL(urlPrefix, searchUrl, filterWord);
		LOGGER.fine("Request to execute: " + requestToExecute);

		int requestResult1 = FVTUtilsWithDate.sendRequestAndGetResult(requestToExecute, activityEvent,
				PopStringConstantsAS.EVENT_TYPE_LOCATION, PopStringConstantsAS.EVENT_TITLE_LOCATION,
				activityPublicEventTitle);

		assertTrue("Request: " + requestToExecute + "requestResult: " + requestResult1, (requestResult1 > 0));
	}

}
