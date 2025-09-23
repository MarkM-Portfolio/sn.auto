package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;

public class FvtBookmarkCreatedWordSearchInEntryTestWithDate extends ActivityStreamSearchTest {

	ArrayList<String> entriesList = new ArrayList<String>();
	private static String queryStringManager1 = "/@public/@all/@all?query=Manager";
	private static String queryStringManager2 = "/@public/@all/bookmarks?query=Manager";
	private static String queryStringEverybody1 = "/@public/@all/@all?query=Everybody";
	private static String queryStringEverybody2 = "/@public/@all/bookmarks?query=Everybody";
	private static String queryStringSecond1 = "/@public/@all/@all?query=Second";
	private static String queryStringSecond2 = "/@public/@all/bookmarks?query=Second";
	private static String queryStringPage1 = "/@public/@all/@all?query=Page";
	private static String queryStringPage2 = "/@public/@all/bookmarks?query=Page";
	private static String queryStringFlower1 = "/@public/@all/@all?query=Flower";
	private static String queryStringFlower2 = "/@public/@all/bookmarks?query=Flower";
	private static String queryStringAuction1 = "/@public/@all/@all?query=Auction";
	private static String queryStringAuction2 = "/@public/@all/bookmarks?query=Auction";
	private static boolean wordFound;

	@Test
	public void publicAllAllManager() throws Exception {
		LOGGER.fine("ASS:publicAllAllManager");
		checkSingleWordInEntry("Manager", queryStringManager1);
	}

	@Test
	public void publicAllBookmarksOne() throws Exception {
		LOGGER.fine("ASS: publicAllBookmarksOne");
		checkSingleWordInEntry("Manager", queryStringManager2);
	}

	@Test
	public void publicAllAllEverybody() throws Exception {
		LOGGER.fine("ASS: publicAllAllEverybody ");
		checkSingleWordInEntry("Everybody", queryStringEverybody1);
	}

	@Test
	public void publicAllBookmarksEverybody() throws Exception {
		LOGGER.fine("ASS: publicAllBookmarksEverybody ");
		checkSingleWordInEntry("Everybody", queryStringEverybody2);
	}

	@Test
	public void publicAllAllSecond() throws Exception {
		LOGGER.fine("ASS: publicAllAllSecond ");
		checkSingleWordInEntry("Second", queryStringSecond1);
	}

	@Test
	public void publicAllBookmarksSecond() throws Exception {
		LOGGER.fine("ASS: publicAllBookmarksSecond ");
		checkSingleWordInEntry("Second", queryStringSecond2);
	}

	@Test
	public void publicAllAllPage() throws Exception {
		LOGGER.fine("ASS:blicAllAllPage");
		checkSingleWordInEntry("Page", queryStringPage1);
	}

	@Test
	public void publicAllBookmarksPage() throws Exception {
		LOGGER.fine("ASS: publicAllBookmarksPage ");
		checkSingleWordInEntry("Page", queryStringPage2);
	}

	@Test
	public void publicAllAllFlower() throws Exception {
		LOGGER.fine("ASS: publicAllAllFlower ");
		checkSingleWordInEntry("Flower", queryStringFlower1);
	}

	@Test
	public void publicAllBookmarksFlower() throws Exception {
		LOGGER.fine("ASS:publicAllBookmarksFlower ");
		checkSingleWordInEntry("Flower", queryStringFlower2);
	}

	@Test
	public void publicAllAllAuction() throws Exception {
		LOGGER.fine("ASS: publicAllAllAuction ");
		checkSingleWordInEntry("Auction", queryStringAuction1);
	}

	@Test
	public void publicAllBookmarksAuction() throws Exception {
		LOGGER.fine("ASS:publicAllBookmarksAuction ");
		checkSingleWordInEntry("Auction", queryStringAuction2);
	}

	// #########################################Working
	// methods#############################################################################################

	private void checkResult(boolean result, int numberOfFoundWord, String searchWord) {

		assertTrue(requestToExecute + " Test failed, Number of entries: " + entriesList.size() + " ,Word: " + searchWord
				+ " found " + numberOfFoundWord + " times", result);
	}

	public void checkSingleWordInEntry(String searchWord, String searchUrl) throws Exception {
		requestToExecute = FVTUtilsWithDate.createRequestToSend(urlPrefix, searchUrl);
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
