package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

/*
 * 22.04.2012 - Yakov Vilenchik
 * Currently tests are failed due to assertion error (incorrect entries are returned for request)
 * Need to verify with Ina where source is located
 */

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonResponse;

public class FvtFiltersTestMultiValuesWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/@all?filters=";
	private static String filter1 = "[{'type':'source','values':['blogs','wikis']}]";
	private static String filter2 = "[{'type':'tag','values':['birds','first']}]";
	private static String filterValue1 = "blogs";
	private static String filterValue11 = "wikis";
	private static String filterValue2 = "birds";
	private static String filterValue21 = "first";

	ArrayList<String> entriesList = new ArrayList<String>();
	ArrayList<String> requestResults = new ArrayList<String>();
	private static boolean wordFound1;
	private static boolean wordFound2;
	private static final String SEARCH_PATH1 = "generator.id";
	private static final String SEARCH_PATH2 = "tags.displayName";

	@Test
	public void testMultiValuesFilter() throws Exception {
		LOGGER.fine("ASS: testMultiValuesFilter ");
		searchWordsInEntry(filterValue1, filterValue11, filter1);
	}

	@Test
	public void testMultiValuesSearchFilters() throws Exception {
		LOGGER.fine("ASS: testMultiValuesSearchFilters ");
		searchTagWordInTagsFieldInEntry(filterValue1, filterValue11, filter1, SEARCH_PATH1);
	}

	@Test
	public void testMultiValuesFilter2() throws Exception {
		LOGGER.fine("ASS: testMultiValuesFilter2 ");
		searchWordsInEntry(filterValue2, filterValue21, filter2);
	}

	@Test
	public void testMultiValuesSearchFilters2() throws Exception {
		LOGGER.fine("ASS: testMultiValuesSearchFilters2 ");
		searchTagWordInTagsFieldInEntry(filterValue2, filterValue21, filter2, SEARCH_PATH2);
	}

	// ************************************************************************************
	// **************************** Working methods
	// ***************************************
	// ************************************************************************************

	public void searchWordsInEntry(String word1ToSearch, String word2ToSearch, String filterForRequest)
			throws Exception {
		int searchWordFoundCount = 0;
		boolean testResult = false;

		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, searchString, filterForRequest);
		LOGGER.fine("URL: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesList(requestToExecute);

		if (!entriesList.isEmpty()) {
			for (int i = 0; i < entriesList.size(); i++) {

				wordFound1 = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), word1ToSearch);
				wordFound2 = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), word2ToSearch);

				if ((wordFound1) || (wordFound2)) {
					searchWordFoundCount++;
				}
			}

			testResult = FVTUtilsWithDate.checkTestResult1(entriesList.size(), searchWordFoundCount);

		}
		assertTrue(requestToExecute + " ,Number of entries: " + entriesList.size() + "Words " + word1ToSearch + " or "
				+ word2ToSearch + " found " + searchWordFoundCount + " times", testResult);
	}

	public void searchTagWordInTagsFieldInEntry(String word1ToSearch, String word2ToSearch, String filterForRequest,
			String pathToSearch) throws Exception {
		Boolean testResult = false;

		int passedResult = 0;
		int failedResult = 0;
		int index = 2;

		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, searchString, filterForRequest);
		LOGGER.fine("URL: " + requestToExecute);
		JsonResponse js = FVTUtilsWithDate.getJsonResponse(requestToExecute);
		assertNotNull("Response is NULL", js);
		int entriesNumber = FVTUtilsWithDate.getJsonResponseEntriesCount(js);

		if (entriesNumber > 0) {
			for (int i = 0; i < entriesNumber; i++) {

				String searchPath = PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY + "["
						+ i + "].." + pathToSearch;
				requestResults = FVTUtilsWithDate.getJsonResponseValuesList(js, searchPath);

				for (int j = 0; j < requestResults.size(); j++) {
					if ((requestResults.get(j).equals(word1ToSearch))
							|| (requestResults.get(j).equals(word2ToSearch))) {
						passedResult++;

					}
				}
				if (passedResult == 0) {

					failedResult++;
				}
			}
		} else {

			testResult = true;
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

					String searchPath = PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY
							+ "[" + i + "].." + pathToSearch;
					requestResults = FVTUtilsWithDate.getJsonResponseValuesList(js, searchPath);

					for (int j = 0; j < requestResults.size(); j++) {
						if ((requestResults.get(j).equals(word1ToSearch))
								|| (requestResults.get(j).equals(word2ToSearch))) {
							passedResult++;

						}
					}
					if (passedResult == 0) {

						failedResult++;
					}
				}
			}
			index++;
		}
		assertNotNull("Response is NULL", js);
		if (failedResult > 0) {
			testResult = false;

		} else {
			testResult = true;

		}
		assertTrue("Words: " + word1ToSearch + " and " + word2ToSearch + " has not been found", testResult);
	}

}
