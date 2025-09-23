package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

/*
 * Covers test case: AS Search: filter:tag search parameter from TTT.
 */

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonResponse;

public class FvtFiltersTestTypeTagsWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/@all?filters=";
	private static String filter = "[{'type':'tag','values':['filter_value']}]";
	private static String filterValue1 = "tag1";
	private static String filterValue2 = "tag3";
	private static String filterValue3 = "tag5";
	private static String filterValue4 = "tag7";
	private static String filterValue5 = "tag19";
	private static String filterValue6 = "tag21";
	private static String filterValue7 = "tag23";
	private static String filterValue8 = "tag24";
	private static String filterValue9 = "tag25";
	private static String filterValue10 = "tag27";
	private static String filterValue11 = "tag28";
	private static String filterValue12 = "birds";
	private static String filterValue13 = "tag35";
	private static String filterValue14 = "persons";

	private static Boolean tagSearchResult;
	private static Boolean tagSearchInEntryResult;
	ArrayList<String> entriesList = new ArrayList<String>(); // for all entries
	ArrayList<String> requestResults = new ArrayList<String>();
	private static boolean wordFound;
	private static final String SEARCH_PATH = "tags.displayName";

	@Test
	public void countTag1Appearance() throws Exception {
		LOGGER.fine("ASS: countTag1Appearance ");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue1));
	}

	@Test
	public void searchTag1InEntries() throws Exception {
		LOGGER.fine("ASS: searchTag1InEntries ");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue1));
	}

	@Test
	public void countTag3Appearance() throws Exception {
		LOGGER.fine("ASS: countTag3Appearance ");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue2));
	}

	@Test
	public void searchTag3InEntries() throws Exception {
		LOGGER.fine("ASS: searchTag3InEntries ");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue2));
	}

	@Test
	public void countTag5Appearance() throws Exception {
		LOGGER.fine("ASS: countTag5Appearance ");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue3));
	}

	@Test
	public void searchTag5InEntries() throws Exception {
		LOGGER.fine("ASS: searchTag5InEntries");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue3));
	}

	@Test
	public void countTag7Appearance() throws Exception {
		LOGGER.fine("ASS: countTag7Appearance ");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue4));
	}

	@Test
	public void searchTag7InEntries() throws Exception {
		LOGGER.fine("ASS:searchTag7InEntries");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue4));
	}

	@Test
	public void countTag19Appearance() throws Exception {
		LOGGER.fine("ASS:countTag19Appearance ");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue5));
	}

	@Test
	public void searchTag19InEntries() throws Exception {
		LOGGER.fine("ASS: searchTag19InEntries");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue5));
	}

	@Test
	public void countTag21Appearance() throws Exception {
		LOGGER.fine("ASS: countTag21Appearance ");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue6));
	}

	@Test
	public void searchTag21InEntries() throws Exception {
		LOGGER.fine("ASS:searchTag21InEntries ");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue6));
	}

	@Test
	public void countTag23Appearance() throws Exception {
		LOGGER.fine("ASS: countTag23Appearance");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue7));
	}

	@Test
	public void searchTag23InEntries() throws Exception {
		LOGGER.fine("ASS: searchTag23InEntries ");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue7));
	}

	@Test
	public void countTag24Appearance() throws Exception {
		LOGGER.fine("ASS: countTag24Appearance");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue8));
	}

	@Test
	public void searchTag24InEntries() throws Exception {
		LOGGER.fine("ASS: searchTag24InEntries ");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue8));
	}

	@Test
	public void countTag25Appearance() throws Exception {
		LOGGER.fine("ASS: countTag25Appearance");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue9));
	}

	@Test
	public void searchTag25InEntries() throws Exception {
		LOGGER.fine("ASS: searchTag25InEntries ");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue9));
	}

	@Test
	public void countTag27Appearance() throws Exception {
		LOGGER.fine("ASS: testMultiValuesFilter ");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue10));
	}

	@Test
	public void searchTag27InEntries() throws Exception {
		LOGGER.fine("ASS: searchTag27InEntries ");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue10));
	}

	@Test
	public void countTag281Appearance() throws Exception {
		LOGGER.fine("ASS: countTag281Appearance");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue11));
	}

	@Test
	public void searchTag28InEntries() throws Exception {
		LOGGER.fine("ASS: searchTag28InEntries ");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue11));
	}

	@Test
	public void countBirdsAppearance() throws Exception {
		LOGGER.fine("ASS: countBirdsAppearance");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue12));
	}

	@Test
	public void searchBirdsInEntries() throws Exception {
		LOGGER.fine("ASS: searchBirdsInEntries ");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue12));
	}

	@Test
	public void countTag35Appearance() throws Exception {
		LOGGER.fine("ASS: countTag35Appearance ");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue13));
	}

	@Test
	public void searchTag35InEntries() throws Exception {
		LOGGER.fine("ASS: searchTag35InEntries ");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue13));
	}

	@Test
	public void countPersonsAppearance() throws Exception {
		LOGGER.fine("ASS: countPersonsAppearance");
		assertTrue(tagSearchResult = countSearchedTagAppearanceNumber(filterValue14));
	}

	@Test
	public void searchPersonsInEntries() throws Exception {
		LOGGER.fine("ASS: searchPersonsInEntries ");
		assertTrue(tagSearchInEntryResult = searchTagInTagsFieldsInEntry(filterValue14));
	}

	// ************************************************************************************
	// **************************** Working methods
	// ***************************************
	// ************************************************************************************

	public Boolean countSearchedTagAppearanceNumber(String tagToSearch) throws Exception {
		int searchWordFoundCount = 0;

		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, searchString, filter);

		requestToExecute = requestToExecute.replace("filter_value", tagToSearch);
		LOGGER.fine("URL: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesList(requestToExecute);

		if (entriesList.size() > 0) {
			for (int i = 0; i < entriesList.size(); i++) {

				wordFound = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), tagToSearch);
				if (wordFound) {

					searchWordFoundCount++;
				}
			}
		}
		boolean testResult = FVTUtilsWithDate.checkTestResult1(entriesList.size(), searchWordFoundCount);

		return testResult;

	}

	public Boolean searchTagInTagsFieldsInEntry(String tagToSearch) throws Exception {
		int filterWordFound = 0;
		Boolean testResult = false;
		int passedResult = 0;
		int failedResult = 0;
		int index = 2;

		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, searchString, filter);
		requestToExecute = requestToExecute.replace("filter_value", tagToSearch);
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
					if (requestResults.get(j).equals(tagToSearch)) {
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
							+ "[" + i + "].." + SEARCH_PATH;
					requestResults = FVTUtilsWithDate.getJsonResponseValuesList(js, searchPath);

					for (int j = 0; j < requestResults.size(); j++) {
						if (requestResults.get(j).equals(tagToSearch)) {
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
		assertNotNull("Next Response is NULL", js);
		if (failedResult > 0) {
			testResult = false;

		} else {
			testResult = true;

		}
		return testResult;
	}

}
