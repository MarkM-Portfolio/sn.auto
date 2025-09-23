package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.net.URLEncoder;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
//import org.testng.AssertJUnit;

public class FvtActivityCreatedWordAndWordQueryTestWithDate extends ActivityStreamSearchTest {

	private static String searchString1 = "/@public/@all/@all?query=";
	private static String searchString2 = "/@public/@all/activities?query=";

	@Test
	public void publicAllAllEveryBodyAndTiger() throws Exception {
		checkQueryWithWordAndWord("Everybody  Tiger", searchString1, 1, 0,
				PopStringConstantsAS.ACTIVITY_ENTRY_CREATION_EVENT,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_ENTRY_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_ENTRY_TITLE);
	}

	@Test
	public void publicAllActivitiesEveryBodyAndTiger() throws Exception {
		checkQueryWithWordAndWord("Everybody  Tiger", searchString2, 1, 0,
				PopStringConstantsAS.ACTIVITY_ENTRY_CREATION_EVENT,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_ENTRY_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_ENTRY_TITLE);
	}

	@Test
	public void publicAllAllStandaloneAndAuction() throws Exception {
		checkQueryWithWordAndWord("Standalone  auction", searchString1, 1, 0,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_CREATION_EVENT_TYPE,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TITLE);
	}

	@Test
	public void publicAllActivitiesStandaloneAndAuction() throws Exception {
		checkQueryWithWordAndWord("Standalone  auction", searchString2, 1, 0,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_CREATION_EVENT_TYPE,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TITLE);
	}

	@Test
	public void publicAllAllFirstAndDuty() throws Exception {
		checkQueryWithWordAndWord("first  duty", searchString1, 1, 0, PopStringConstantsAS.ACTIVITY_TODO_CREATION_EVENT,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TODO_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TODO_TITLE);
	}

	@Test
	public void publicAllActivitiesFirstAndDuty() throws Exception {
		checkQueryWithWordAndWord("first  duty", searchString2, 1, 0, PopStringConstantsAS.ACTIVITY_TODO_CREATION_EVENT,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TODO_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TODO_TITLE);
	}

	@Test
	public void publicAllAllThirtyAndSeven() throws Exception {
		checkQueryWithWordAndWord("thirty  seven", searchString1, 1, 0,
				PopStringConstantsAS.ACTIVITY_TODO_CREATION_EVENT,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TODO_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TODO_TITLE);
	}

	@Test
	public void publicAllActivitiesThirtyAndSeven() throws Exception {
		checkQueryWithWordAndWord("thirty  seven", searchString2, 1, 0,
				PopStringConstantsAS.ACTIVITY_TODO_CREATION_EVENT,
				PopStringConstantsAS.STANDALONE_PUBLIC_ACTIVITY_TODO_TITLE,
				PopStringConstantsAS.STANDALONE_PRIVATE_ACTIVITY_TODO_TITLE);
	}

	// ########################################Working
	// methods#############################################################

	public void checkQueryWithWordAndWord(String filterWord, String searchUrl, int expectedResult1, int expectedResult2,
			String activityEvent, String activityPublicEventTitle, String activityPrivateEventTitle) throws Exception {
		boolean test1Result = false;
		boolean test2Result = false;

		requestToExecute = FVTUtilsWithDate.createRequestURL(urlPrefix, searchUrl,
				URLEncoder.encode(filterWord, "UTF-8"));
		LOGGER.fine("Request to execute: " + requestToExecute);

		int requestResult1 = FVTUtilsWithDate.sendRequestAndGetResult(requestToExecute, activityEvent,
				PopStringConstantsAS.EVENT_TYPE_LOCATION, PopStringConstantsAS.EVENT_TITLE_LOCATION,
				activityPublicEventTitle);
		int requestResult2 = FVTUtilsWithDate.sendRequestAndGetResult(requestToExecute, activityEvent,
				PopStringConstantsAS.EVENT_TYPE_LOCATION, PopStringConstantsAS.EVENT_TITLE_LOCATION,
				activityPrivateEventTitle);

		test1Result = FVTUtilsWithDate.checkTestResult(expectedResult1, requestResult1, requestToExecute,
				activityPublicEventTitle);
		test2Result = FVTUtilsWithDate.checkTestResult(expectedResult2, requestResult2, requestToExecute,
				activityPrivateEventTitle);

		assertTrue("Request: " + requestToExecute + " ,Result 1: " + test1Result + " Result 2 " + test2Result,
				((test1Result) && (test2Result)));
	}

}
