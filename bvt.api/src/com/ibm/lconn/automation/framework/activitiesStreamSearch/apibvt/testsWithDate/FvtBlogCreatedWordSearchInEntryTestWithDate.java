package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;

public class FvtBlogCreatedWordSearchInEntryTestWithDate extends ActivityStreamSearchTest {

	ArrayList<String> entriesList = new ArrayList<String>(); // for all entries
																// (total list)
																// from request
	private static String queryStringManager1 = "/@public/@all/@all?query=Manager";
	private static String queryStringManager2 = "/@public/@all/blogs?query=Manager";
	private static String queryStringEverybody1 = "/@public/@all/@all?query=Everybody";
	private static String queryStringEverybody2 = "/@public/@all/blogs?query=Everybody";
	private static String queryStringPlace1 = "/@public/@all/@all?query=Place";
	private static String queryStringPlace2 = "/@public/@all/blogs?query=Place";
	private static String queryStringThirty1 = "/@public/@all/@all?query=Thirty";
	private static String queryStringThirty2 = "/@public/@all/blogs?query=Thirty";
	private static boolean wordFound;

	@Test
	public void publicAllAllManager() throws Exception {
		LOGGER.fine("ASS: publicAllAllManager");
		checkQueryWithSingleWord("Manager", queryStringManager1);
	}

	@Test
	public void publicAllBlogsOne() throws Exception {
		LOGGER.fine("ASS: publicAllBlogsOne ");
		checkQueryWithSingleWord("Manager", queryStringManager2);
	}

	@Test
	public void publicAllAllEverybody() throws Exception {
		LOGGER.fine("ASS: publicAllAllEverybody");
		checkQueryWithSingleWord("Everybody", queryStringEverybody1);
	}

	@Test
	public void publicAllBlogsEverybody() throws Exception {
		LOGGER.fine("ASS:publicAllBlogsEverybody ");
		checkQueryWithSingleWord("Everybody", queryStringEverybody2);
	}

	@Test
	public void publicAllAllPlace() throws Exception {
		LOGGER.fine("ASS: publicAllAllPlace ");
		checkQueryWithSingleWord("Place", queryStringPlace1);
	}

	@Test
	public void publicAllBlogsPlace() throws Exception {
		LOGGER.fine("ASS: publicAllBlogsPlace ");
		checkQueryWithSingleWord("Place", queryStringPlace2);

	}

	@Test
	public void publicAllAllThirty() throws Exception {
		LOGGER.fine("ASS: publicAllAllThirty");
		checkQueryWithSingleWord("Thirty", queryStringThirty1);
	}

	@Test
	public void publicAllBlogsThirty() throws Exception {
		LOGGER.fine("ASS: publicAllBlogsThirty ");
		checkQueryWithSingleWord("Thirty", queryStringThirty2);
	}

	// #########################################Working
	// methods#############################################################################################

	private void checkResult(boolean result, int numberOfFoundWord, String searchWord) {

		assertTrue(requestToExecute + " Test failed, Number of entries: " + entriesList.size() + " ,Word: " + searchWord
				+ " found " + numberOfFoundWord + " times", result);
	}

	public void checkQueryWithSingleWord(String searchWord, String urlSuffix) throws Exception {
		requestToExecute = FVTUtilsWithDate.createRequestToSend(urlPrefix, urlSuffix);
		int searchWordFoundCount = 0;
		LOGGER.fine("URL: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesList(requestToExecute);

		for (int i = 0; i < entriesList.size(); i++) {
			wordFound = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), searchWord);
			if (wordFound) {

				searchWordFoundCount++;
			}

		}
		boolean testResult = FVTUtilsWithDate.checkTestResult1(entriesList.size(), searchWordFoundCount);

		checkResult(testResult, searchWordFoundCount, searchWord);
	}

}
