package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtFiltersTestTypeActorWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/@all?";

	private static String filterQuery = "filters=";
	private static String filterParam = "[{'type':'actor','values':['user_ID']}]";
	private static ArrayList<String> entriesList = new ArrayList<String>();

	@Test
	public void checkFilterActorLongId() throws Exception {
		int notFound = 0;
		LOGGER.fine("ASS: checkFilterActorLongId:" + userIDLong);
		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter1(urlPrefix, searchString, filterQuery,
				filterParam);

		requestToExecute = requestToExecute.replace("user_ID", userIDLong);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesElementList(requestToExecute,
				"." + PopStringConstantsAS.ENTRY_USER_DISPLAY_NAME_LOCATION);

		if (entriesList.size() > 0) {
			for (int i = 0; i < entriesList.size(); i++) {

				if (!entriesList.get(i).equals(PopStringConstantsAS.USER_DISPLAY_NAME)) {
					notFound++;
				}
			}
		} else {
			LOGGER.fine("No entries found");
		}
		assertEquals("Test failed, Actor of some entries is not: " + PopStringConstantsAS.USER_DISPLAY_NAME, 0,
				notFound);
	}

	@Test
	public void checkFilterActorShortId() throws Exception {
		int notFound = 0;
		LOGGER.fine("ASS: checkFilterActorShortId: " + userIDShort);
		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter1(urlPrefix, searchString, filterQuery,
				filterParam);

		requestToExecute = requestToExecute.replace("user_ID", userIDShort);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesElementList(requestToExecute,
				"." + PopStringConstantsAS.ENTRY_USER_DISPLAY_NAME_LOCATION);

		if (entriesList.size() > 0) {
			for (int i = 0; i < entriesList.size(); i++) {

				if (!entriesList.get(i).equals(PopStringConstantsAS.USER_DISPLAY_NAME)) {
					notFound++;
				}
			}
		} else {
			LOGGER.fine("No entries found");
		}
		assertEquals("Test failed, Actor of some entries is not: " + PopStringConstantsAS.USER_DISPLAY_NAME, 0,
				notFound);
	}

}
