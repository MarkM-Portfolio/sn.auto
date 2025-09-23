package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonResponse;

public class FvtFiltersTestMultiParametersWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/@all?filters=";
	private static String filter = "[{'type':'tag','values':['tag11']}, {'type':'source','values':['activities']}]";
	private static String filterValue1 = "tag11";
	private static String filterValue2 = "activities";

	ArrayList<String> entriesList = new ArrayList<String>();
	ArrayList<String> requestResults1 = new ArrayList<String>();
	ArrayList<String> requestResults2 = new ArrayList<String>();
	private static boolean wordFound1;
	private static boolean wordFound2;
	private static final String SEARCH_PATH1 = "tags.displayName";
	private static final String SEARCH_PATH2 = "generator.id";

	@Test
	public void testMultiParametersFilter() throws Exception {
		int searchWordFoundCount = 0;
		LOGGER.fine("ASS:testMultiParametersFilter ");
		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, searchString, filter);
		LOGGER.fine("URL: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesList(requestToExecute);

		if (!entriesList.isEmpty()) {
			for (int i = 0; i < entriesList.size(); i++) {

				wordFound1 = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), filterValue1);
				wordFound2 = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), filterValue2);

				if ((wordFound1) || (wordFound2)) {
					searchWordFoundCount++;
				}
			}
		}

		boolean testResult = FVTUtilsWithDate.checkTestResult1(entriesList.size(), searchWordFoundCount);

		testResult = testResult && (searchWordFoundCount != 0);

		assertTrue(requestToExecute + " ,Number of entries: " + entriesList.size() + "Words " + filterValue1 + " or "
				+ filterValue2 + "not found ", testResult);

	}

	@Test
	public void testMultiParametersSearchFilters() throws Exception {
		Boolean testResult1 = false;
		Boolean testResult2 = false;
		Boolean totaltestResult = false;
		int index = 2;
		int filterWordFound = 0;
		int passedResult1 = 0;
		int passedResult2 = 0;
		int failedResult1 = 0;
		int failedResult2 = 0;
		LOGGER.fine("ASS:testMultiParametersSearchFilters");
		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, searchString, filter);
		LOGGER.fine("URL: " + requestToExecute);
		JsonResponse js = FVTUtilsWithDate.getJsonResponse(requestToExecute);
		assertNotNull("Response is NULL", js);
		int entriesNumber = FVTUtilsWithDate.getJsonResponseEntriesCount(js);

		if (entriesNumber > 0) {
			for (int i = 0; i < entriesNumber; i++) {
				String searchPath1 = PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY + "["
						+ i + "].." + SEARCH_PATH1;
				String searchPath2 = PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY + "["
						+ i + "].." + SEARCH_PATH2;
				requestResults1 = FVTUtilsWithDate.getJsonResponseValuesList(js, searchPath1);
				requestResults2 = FVTUtilsWithDate.getJsonResponseValuesList(js, searchPath2);

				for (int j = 0; j < requestResults1.size(); j++) {
					if (requestResults1.get(j).equals(filterValue1)) {
						passedResult1++;

					} else {
						failedResult1++;

					}
					if (passedResult1 == 0) {

						failedResult1++;
					}
				}
				for (int k = 0; k < requestResults2.size(); k++) {
					if (requestResults2.get(k).equals(filterValue2)) {
						passedResult2++;

					}
					if (passedResult2 == 0) {

						failedResult2++;
					}
				}
			}
		}

		while (entriesNumber == PopStringConstantsAS.RECEIVED_PAGE_SIZE) {
			js = null;
			String startIndex = "&startIndex=" + index;
			String newRequestURL = FVTUtilsWithDate.createRequestURLWithIndex(requestToExecute, startIndex);
			js = FVTUtilsWithDate.getJsonResponse(newRequestURL);
			if (js == null) {
				break;
			}
			entriesNumber = FVTUtilsWithDate.getJsonResponseEntriesCount(js);
			if (entriesNumber > 0) {
				for (int i = 0; i < entriesNumber; i++) {
					String searchPath1 = PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY
							+ "[" + i + "].." + SEARCH_PATH1;
					String searchPath2 = PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY
							+ "[" + i + "].." + SEARCH_PATH2;
					requestResults1 = FVTUtilsWithDate.getJsonResponseValuesList(js, searchPath1);
					requestResults2 = FVTUtilsWithDate.getJsonResponseValuesList(js, searchPath2);

					for (int j = 0; j < requestResults1.size(); j++) {
						if (requestResults1.get(j).equals(filterValue1)) {
							passedResult1++;

						} else {
							failedResult1++;

						}
						if (passedResult1 == 0) {

							failedResult1++;
						}
					}
					for (int k = 0; k < requestResults2.size(); k++) {
						if (requestResults2.get(k).equals(filterValue2)) {
							passedResult2++;

						}
						if (passedResult2 == 0) {

							failedResult2++;
						}
					}
				}
			}
			index++;
		}
		assertNotNull("Response is NULL", js);
		if ((failedResult1 > 0) || (passedResult1 == 0)) {
			testResult1 = false;

		} else {
			testResult1 = true;

		}

		if ((failedResult2 > 0) || (passedResult2 == 0)) {
			testResult2 = false;

		} else {
			testResult2 = true;

		}

		if ((testResult1) && (testResult2)) {

			totaltestResult = true;
		}
		assertTrue("Test failed. Word " + filterValue1 + " or " + filterValue2 + " doesn't found,entries count:"
				+ entriesNumber, totaltestResult);
	}

}
