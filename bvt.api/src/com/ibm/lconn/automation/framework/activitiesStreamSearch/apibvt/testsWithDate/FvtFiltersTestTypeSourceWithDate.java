package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonResponse;

public class FvtFiltersTestTypeSourceWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/@all?filters=";
	private static String filter1 = "[{'type':'source','values':['wikis']}]";
	private static String filter2 = "[{'type':'source','values':['files']}]";
	private static String filter3 = "[{'type':'source','values':['blogs']}]";
	private static String filter4 = "[{'type':'source','values':['communities']}]";
	private static String filter5 = "[{'type':'source','values':['activities']}]";
	private static String filter6 = "[{'type':'source','values':['bookmarks']}]";
	private static String filter7 = "[{'type':'source','values':['forums']}]";
	private static String filterValue1 = "wikis";
	private static String filterValue2 = "files";
	private static String filterValue3 = "blogs";
	private static String filterValue4 = "communities";
	private static String filterValue5 = "activities";
	private static String filterValue6 = "bookmarks";
	private static String filterValue7 = "forums";

	ArrayList<String> entriesList = new ArrayList<String>();
	ArrayList<String> requestResults = new ArrayList<String>();
	private static boolean wordFound;
	private static final String SEARCH_PATH = "generator.id";

	@Test
	public void testWithSourceAsFilterValueWikis() throws Exception {
		testWithSourceAsFilterValueApp(filter1, filterValue1);
		LOGGER.fine("ASS: testWithSourceAsFilterValueWikis ");
	}

	@Test
	public void testWithSourceAsFilterValusFiles() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterValusFiles ");
		testWithSourceAsFilterValueApp(filter2, filterValue2);
	}

	@Test
	public void testWithSourceAsFilterValueBlogs() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterValueBlogs");
		testWithSourceAsFilterValueApp(filter3, filterValue3);
	}

	@Test
	public void testWithSourceAsFilterValueCommunities() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterValueCommunities ");
		testWithSourceAsFilterValueApp(filter4, filterValue4);
	}

	@Test
	public void testWithSourceAsFilterValueActivities() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterValueActivities ");
		testWithSourceAsFilterValueApp(filter5, filterValue5);
	}

	@Test
	public void testWithSourceAsFilterValueBookmarks() throws Exception {
		LOGGER.fine("ASS:testWithSourceAsFilterValueBookmarks ");
		testWithSourceAsFilterValueApp(filter6, filterValue6);
	}

	@Test
	public void testWithSourceAsFilterValueForums() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterValueForums");
		testWithSourceAsFilterValueApp(filter7, filterValue7);
	}

	@Test
	public void testWithSourceAsFilterSearchEntryValueWikis() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterSearchEntryValueWikis ");
		testWithSourceAsFilterSearchEntryValueApp(filter1, filterValue1);
	}

	@Test
	public void testWithSourceAsFilterSearchEntryValueFiles() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterSearchEntryValueFiles ");
		testWithSourceAsFilterSearchEntryValueApp(filter2, filterValue2);
	}

	@Test
	public void testWithSourceAsFilterSearchEntryValueBlogs() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterSearchEntryValueBlogs ");
		testWithSourceAsFilterSearchEntryValueApp(filter3, filterValue3);
	}

	@Test
	public void testWithSourceAsFilterSearchEntryValueCommunity() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterSearchEntryValueCommunity ");
		testWithSourceAsFilterSearchEntryValueApp(filter4, filterValue4);
	}

	@Test
	public void testWithSourceAsFilterSearchEntryValueActivities() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterSearchEntryValueActivities");
		testWithSourceAsFilterSearchEntryValueApp(filter5, filterValue5);
	}

	@Test
	public void testWithSourceAsFilterSearchEntryValueBookmarks() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterSearchEntryValueBookmarks ");
		testWithSourceAsFilterSearchEntryValueApp(filter6, filterValue6);
	}

	@Test
	public void testWithSourceAsFilterSearchEntryValueForums() throws Exception {
		LOGGER.fine("ASS: testWithSourceAsFilterSearchEntryValueForums ");
		testWithSourceAsFilterSearchEntryValueApp(filter7, filterValue7);
	}

	// ##########################################################################################################
	// ##############################Working
	// methods#############################################################

	public void testWithSourceAsFilterValueApp(String filterQuery, String appToSearch) throws Exception {
		int searchWordFoundCount = 0;
		boolean testResult = false;

		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, searchString, filterQuery);
		LOGGER.fine("URL: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesList(requestToExecute);

		if (entriesList.size() > 0) {
			for (int i = 0; i < entriesList.size(); i++) {

				wordFound = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), appToSearch);
				if (wordFound) {

					searchWordFoundCount++;
				}
			}
		}
		testResult = FVTUtilsWithDate.checkTestResult1(entriesList.size(), searchWordFoundCount);

		assertTrue(requestToExecute + " ,Number of entries: " + entriesList.size() + "Word: " + appToSearch + " found "
				+ searchWordFoundCount + " times", testResult);
	}

	public void testWithSourceAsFilterSearchEntryValueApp(String filterQuery, String appToSearch) throws Exception {
		Boolean testResult = false;
		int filterWordFound = 0;
		int passedResult = 0;
		int failedResult = 0;
		int index = 2;

		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, searchString, filterQuery);

		requestToExecute = requestToExecute.replace("filter_value", appToSearch);
		LOGGER.fine("URL: " + requestToExecute);
		JsonResponse js = FVTUtilsWithDate.getJsonResponse(requestToExecute);
		assertNotNull("Response is NULL", js);
		int entriesNumber = FVTUtilsWithDate.getJsonResponseEntriesCount(js);

		if (entriesNumber > 0) {
			for (int i = 0; i < entriesNumber; i++) {
				String searchPath = PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY + "["
						+ i + "].." + SEARCH_PATH;
				requestResults = FVTUtilsWithDate.getJsonResponseValuesList(js, searchPath);

				for (int j = 0; j < requestResults.size(); j++) {
					if (requestResults.get(j).equals(appToSearch)) {
						passedResult++;

					}
					if (passedResult == 0) {

						failedResult++;
					}
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
							+ "[" + i + "].." + SEARCH_PATH;
					requestResults = FVTUtilsWithDate.getJsonResponseValuesList(js, searchPath);

					for (int j = 0; j < requestResults.size(); j++) {
						if (requestResults.get(j).equals(appToSearch)) {
							passedResult++;

						}
						if (passedResult == 0) {

							failedResult++;
						}
					}
				}
			}
			index++;
		}
		assertNotNull("Next Response is NULL", js);
		if (failedResult > 0) {
			testResult = false;

		} else {
			testResult = true;

		}
		assertTrue(testResult);
	}

}
