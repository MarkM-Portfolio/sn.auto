package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;

public class FvtFilesCheckContentUpdateWithDate extends ActivityStreamSearchTest {

	ArrayList<String> entriesList = new ArrayList<String>();
	private static String queryString = "/@public/@all/@all?query=";

	@Test
	public void checkExistenceOfFileContentUpdateEvent() throws Exception {
		Boolean updateWordFound = true;
		int wordNotFound = 0;
		String errorMessage = "";
		LOGGER.fine("ASS: checkExistenceOfFileContentUpdateEvent");

		requestToExecute = FVTUtilsWithDate.createRequestURL(urlPrefix, queryString,
				PopStringConstantsAS.FILE_CONTENT_UPDATE_WORD);
		LOGGER.fine("Request to execute: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesElementList(requestToExecute,
				"." + PopStringConstantsAS.EVENT_SUMMARY_LOCATION);
		LOGGER.fine("entries are: " + entriesList);
		LOGGER.fine("Received " + entriesList.size() + " entries");
		if (entriesList.size() > 0) {
			for (int i = 0; i < entriesList.size(); i++) {
				if (!entriesList.get(i).contains(PopStringConstantsAS.FILE_CONTENT_UPDATE_WORD)) {
					wordNotFound++;
				} else {
					LOGGER.fine("Entry " + i + " contains word: " + PopStringConstantsAS.FILE_CONTENT_UPDATE_WORD);
				}
			}
		} else {
			updateWordFound = false;
			errorMessage = "Test Failed, No events found with word: " + PopStringConstantsAS.FILE_CONTENT_UPDATE_WORD;
		}
		if (wordNotFound > 0) {
			updateWordFound = false;
			errorMessage = "Test Failed, Word: " + PopStringConstantsAS.FILE_CONTENT_UPDATE_WORD
					+ " not found in all events of file update";
		}
		assertTrue(errorMessage, updateWordFound);
	}

}
