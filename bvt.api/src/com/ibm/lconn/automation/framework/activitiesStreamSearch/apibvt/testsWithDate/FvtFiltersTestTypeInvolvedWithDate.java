package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtFiltersTestTypeInvolvedWithDate extends ActivityStreamSearchTest {

	private static String REQUEST_PUBLIC_ALL_ALL_1 = "/@public/@all/@all?filters=";
	private static String REQUEST_PUBLIC_ALL_ALL_2 = "/@public/@all/@all?";
	private static String filter = "[{'type':'involved','values':['userID']}]";
	private static String filter2 = "filterBy=involved&filterOp=equals&filterValue=userID";
	private static String requestToExecute1 = "";
	private static String requestToExecute2 = "";

	ArrayList<String> idsListForRequest1 = new ArrayList<String>();
	ArrayList<String> idsListForRequest2 = new ArrayList<String>();

	@Test
	public void checkEventsOfInvolvedUserIdLong() throws Exception {
		boolean testResult = false;
		LOGGER.fine("ASS:checkEventsOfInvolvedUserIdLong: " + userIDLong);

		requestToExecute1 = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, REQUEST_PUBLIC_ALL_ALL_1, filter);
		requestToExecute1 = requestToExecute1.replace("userID", userIDLong);
		LOGGER.fine("Request to send to server: " + requestToExecute1);

		requestToExecute2 = FVTUtilsWithDate.createRequestURL(urlPrefix, REQUEST_PUBLIC_ALL_ALL_2, filter2);
		requestToExecute2 = requestToExecute2.replace("userID", userIDLong);
		LOGGER.fine("Request to send to server: " + requestToExecute2);

		idsListForRequest1 = FVTUtilsWithDate.getJsonResponseEntriesElementList(requestToExecute1,
				PopStringConstantsAS.EVENT_ID_LOCATION);
		idsListForRequest2 = FVTUtilsWithDate.getJsonResponseEntriesElementList(requestToExecute2,
				PopStringConstantsAS.EVENT_ID_LOCATION);

		testResult = compareAllIdsLists(idsListForRequest1, idsListForRequest2);
		assertTrue("Test Failed, Event id lists are not equal.", testResult);
	}

	@Test
	public void checkEventsOfInvolvedUserIdShort() throws Exception {
		boolean testResult = false;
		LOGGER.fine("ASS: checkEventsOfInvolvedUserIdShort: " + userIDShort);

		requestToExecute1 = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, REQUEST_PUBLIC_ALL_ALL_1, filter);
		requestToExecute1 = requestToExecute1.replace("userID", userIDShort);
		LOGGER.fine("Request to send to server: " + requestToExecute1);

		requestToExecute2 = FVTUtilsWithDate.createRequestURL(urlPrefix, REQUEST_PUBLIC_ALL_ALL_2, filter2);
		requestToExecute2 = requestToExecute2.replace("userID", userIDShort);
		LOGGER.fine("Request to send to server: " + requestToExecute2);

		idsListForRequest1 = FVTUtilsWithDate.getJsonResponseEntriesElementList(requestToExecute1,
				PopStringConstantsAS.EVENT_ID_LOCATION);
		idsListForRequest2 = FVTUtilsWithDate.getJsonResponseEntriesElementList(requestToExecute2,
				PopStringConstantsAS.EVENT_ID_LOCATION);

		testResult = compareAllIdsLists(idsListForRequest1, idsListForRequest2);
		assertTrue("Test Failed, Event id lists are not equal.", testResult);
	}

	// ############################Working
	// methods##################################################################

	private boolean compareAllIdsLists(ArrayList<String> list1, ArrayList<String> list2) {
		boolean testResult = true;
		LOGGER.fine("List1 size: " + list1.size());
		LOGGER.fine("List2 size: " + list2.size());
		if ((list1.size() == list2.size())) {
			LOGGER.fine("Id's lists are equals");
			for (int i = 0; i < list1.size(); i++) {
				if ((list1.get(i).equals(list2.get(i)))) {
				} else {
					LOGGER.fine("Event Id values at position: " + i + " are different.");
					testResult = false;
				}
			}

		} else {
			LOGGER.fine("Events Id lists are different");
			testResult = false;
		}
		return testResult;
	}

}
