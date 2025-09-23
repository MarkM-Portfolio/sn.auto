package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertEquals;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtASSearchInvolvedUserTestWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/userID/@involved/@all";

	private static ArrayList<String> entriesListDB = new ArrayList<String>();
	private static ArrayList<String> entriesListIndex = new ArrayList<String>();
	private static String requestToExecuteDB;
	private static String requestToExecuteIndex;

	@Test
	public void checkInvolvedUserWithLongId() throws Exception {
		int differentEventId = 0;
		LOGGER.fine("ASS: checkInvolvedUserWithLongId: " + userIDLong);

		requestToExecuteDB = FVTUtilsWithDate.createRequestURL(urlPrefix, searchString,
				PopStringConstantsAS.DB_SEARCH_PARAMETER);
		requestToExecuteDB = requestToExecuteDB.replace("userID", userIDLong);
		entriesListDB = FVTUtilsWithDate.getJsonResponseEntriesElementList(requestToExecuteDB,
				"." + PopStringConstantsAS.EVENT_ID_LOCATION);

		requestToExecuteIndex = FVTUtilsWithDate.createRequestURL(urlPrefix, searchString,
				PopStringConstantsAS.INDEX_SEARCH_PARAMETER);
		requestToExecuteIndex = requestToExecuteIndex.replace("userID", userIDLong);
		entriesListIndex = FVTUtilsWithDate.getJsonResponseEntriesElementList(requestToExecuteIndex,
				"." + PopStringConstantsAS.EVENT_ID_LOCATION);

		if ((entriesListDB.size() > 0) && (entriesListIndex.size() > 0)) {
			if (entriesListDB.size() == entriesListIndex.size()) {
				for (int i = 0; i < entriesListDB.size(); i++) {
					if (!entriesListDB.get(i).equals(entriesListIndex.get(i))) {
						differentEventId++;
					}
				}
			}
		}
		assertEquals("Some events from DB request are not equal to events from Index request: "
				+ PopStringConstantsAS.USER_DISPLAY_NAME, 0, differentEventId);
	}

	@Test
	public void checkInvolvedUserWithShortId() throws Exception {
		int differentEventId = 0;
		LOGGER.fine("checkInvolvedUserWithShortId: " + userIDLong);

		requestToExecuteDB = FVTUtilsWithDate.createRequestURL(urlPrefix, searchString,
				PopStringConstantsAS.DB_SEARCH_PARAMETER);
		requestToExecuteDB = requestToExecuteDB.replace("userID", userIDShort);
		entriesListDB = FVTUtilsWithDate.getJsonResponseEntriesElementList(requestToExecuteDB,
				"." + PopStringConstantsAS.EVENT_ID_LOCATION);

		requestToExecuteIndex = FVTUtilsWithDate.createRequestURL(urlPrefix, searchString,
				PopStringConstantsAS.INDEX_SEARCH_PARAMETER);
		requestToExecuteIndex = requestToExecuteIndex.replace("userID", userIDShort);
		entriesListIndex = FVTUtilsWithDate.getJsonResponseEntriesElementList(requestToExecuteIndex,
				"." + PopStringConstantsAS.EVENT_ID_LOCATION);

		if ((entriesListDB.size() > 0) && (entriesListIndex.size() > 0)) {
			if (entriesListDB.size() == entriesListIndex.size()) {
				for (int i = 0; i < entriesListDB.size(); i++) {
					if (!entriesListDB.get(i).equals(entriesListIndex.get(i))) {
						differentEventId++;
					}
				}
			}
		}
		assertEquals("Some events from DB request are not equal to events from Index request: "
				+ PopStringConstantsAS.USER_DISPLAY_NAME, 0, differentEventId);
	}

}
