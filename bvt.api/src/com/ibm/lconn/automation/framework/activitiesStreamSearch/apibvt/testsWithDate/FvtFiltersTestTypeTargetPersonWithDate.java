package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtFiltersTestTypeTargetPersonWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/@all?filters=";
	private static String filter = "[{'type':'target_person','values':['user_ID']}]";

	private static ArrayList<String> entriesList = new ArrayList<String>(); // for

	@Test
	public void checkTargetPersonWithUserIdLong() throws Exception {
		int notFound = 0;
		LOGGER.fine("ASS: checkTargetPersonWithUserIdLong: " + userIDLong);

		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, searchString, filter);
		requestToExecute = requestToExecute.replace("user_ID", userIDLong);
		LOGGER.fine("Request to send to server: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesTargetPersonList(requestToExecute,
				PopStringConstantsAS.TITLE_LOCATION_FOR_SEARCH,
				PopStringConstantsAS.TARGET_PERSON_ELEMENT_LOCATION_MENTIONS,
				PopStringConstantsAS.TARGET_PERSON_ELEMENT_OBJECT_TYPE_MENTIONS_LOC,
				PopStringConstantsAS.TARGET_PERSON_ELEMENT_OBJECT_TYPE_MENTIONS);
		LOGGER.fine("Entries: " + entriesList);
		if (entriesList.size() > 0) {
			for (int i = 0; i < entriesList.size(); i++) {
				if (!entriesList.get(i).contains(PopStringConstantsAS.USER_DISPLAY_NAME)) {
					notFound++;
					System.out.println("Not found: " + notFound);
				}
			}
		} else {
			LOGGER.fine("No entries found");
		}
		assertEquals("Test Failed, Target person of some entries is not: " + PopStringConstantsAS.USER_DISPLAY_NAME, 0,
				notFound);
	}

	@Test
	public void checkTargetPersonWithUserIdShort() throws Exception {
		int notFound = 0;
		LOGGER.fine("ASS: checkTargetPersonWithUserIdShort: " + userIDShort);

		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, searchString, filter);
		requestToExecute = requestToExecute.replace("user_ID", userIDShort);
		LOGGER.fine("Request to send to server: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesTargetPersonList(requestToExecute,
				PopStringConstantsAS.TITLE_LOCATION_FOR_SEARCH,
				PopStringConstantsAS.TARGET_PERSON_ELEMENT_LOCATION_MENTIONS,
				PopStringConstantsAS.TARGET_PERSON_ELEMENT_OBJECT_TYPE_MENTIONS_LOC,
				PopStringConstantsAS.TARGET_PERSON_ELEMENT_OBJECT_TYPE_MENTIONS);
		LOGGER.fine("Entries: " + entriesList);
		if (entriesList.size() > 0) {
			for (int i = 0; i < entriesList.size(); i++) {
				if (!entriesList.get(i).contains(PopStringConstantsAS.USER_DISPLAY_NAME)) {
					notFound++;
				}
			}
		} else {
			LOGGER.fine("No entries found");
		}
		assertEquals("Test Failed, Target person of some entries is not: " + PopStringConstantsAS.USER_DISPLAY_NAME, 0,
				notFound);
	}

}
