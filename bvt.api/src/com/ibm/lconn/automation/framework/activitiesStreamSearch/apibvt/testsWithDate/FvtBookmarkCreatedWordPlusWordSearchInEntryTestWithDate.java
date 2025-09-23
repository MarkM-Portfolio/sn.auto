package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;

public class FvtBookmarkCreatedWordPlusWordSearchInEntryTestWithDate extends ActivityStreamSearchTest {

	ArrayList<String> entriesList = new ArrayList<String>(); // for all entries
																// (total list)
																// from request
	private static String queryStringTigerPlusManager1 = "/@public/@all/@all?query=tiger%20+Manager";
	private static String queryStringTigerPlusManager2 = "/@public/@all/bookmarks?query=tiger%20+Manager";
	private static String queryStringEverybodyPlusTiger1 = "/@public/@all/@all?query=+Everybody%20AND%20tiger";
	private static String queryStringEverybodyPlusTiger2 = "/@public/@all/bookmarks?query=+Everybody%20AND%20tiger";
	private static String queryStringTigerPlusSecond1 = "/@public/@all/@all?query=tiger%20+Second";
	private static String queryStringTigerPlusSecond2 = "/@public/@all/bookmarks?query=tiger%20+Second";
	private static String queryStringEverybodyPlusPage1 = "/@public/@all/@all?query=Everybody+Page";
	private static String queryStringEverybodyPlusPage2 = "/@public/@all/bookmarks?query=Everybody+Page";
	private static String queryStringFlowerPlusFlower1 = "/@public/@all/@all?query=flower+Flower";
	private static String queryStringFlowerPlusFlower2 = "/@public/@all/bookmarks?query=flower+Flower";
	private static String queryStringPagePlusAuction1 = "/@public/@all/@all?query=Page+Auction";
	private static String queryStringPagePlusAuction2 = "/@public/@all/bookmarks?query=Page+Auction";
	private static boolean wordFound;
	private static boolean word2Found;

	@Test
	public void publicAllAllTigerPlusManager() throws Exception {
		LOGGER.fine("ASS:publicAllAllTigerPlusManager ");
		checkWordPlusWordInEntry("tiger", "Manager", queryStringTigerPlusManager1);
	}

	@Test
	public void publicAllBookmarksTigerPlusManager() throws Exception {
		LOGGER.fine("ASS:publicAllBookmarksTigerPlusManager ");
		checkWordPlusWordInEntry("tiger", "Manager", queryStringTigerPlusManager2);
	}

	@Test
	public void publicAllAllEverybodyPlusTiger() throws Exception {
		LOGGER.fine("ASS: publicAllAllEverybodyPlusTiger ");
		checkWordPlusWordInEntry("Everybody", "tiger", queryStringEverybodyPlusTiger1);
	}

	@Test
	public void publicAllBookmarksEverybodyPlusTiger() throws Exception {
		LOGGER.fine("ASS:publicAllBookmarksEverybodyPlusTiger");
		checkWordPlusWordInEntry("Everybody", "tiger", queryStringEverybodyPlusTiger2);
	}

	@Test
	public void publicAllAllTigerPlusSecond() throws Exception {
		LOGGER.fine("ASS: publicAllAllTigerPlusSecond");
		checkWordPlusWordInEntry("tiger", "Second", queryStringTigerPlusSecond1);
	}

	@Test
	public void publicAllBookmarksTigerPlusSecond() throws Exception {
		LOGGER.fine("ASS:publicAllBookmarksTigerPlusSecond");
		checkWordPlusWordInEntry("tiger", "Second", queryStringTigerPlusSecond2);
	}

	@Test
	public void publicAllAllEverybodyPlusPage() throws Exception {
		LOGGER.fine("ASS:publicAllAllEverybodyPlusPage ");
		checkWordPlusWordInEntry("Everybody", "Page", queryStringEverybodyPlusPage1);
	}

	@Test
	public void publicAllBookmarksEverybodyPlusPage() throws Exception {
		LOGGER.fine("ASS: publicAllBookmarksEverybodyPlusPage ");
		checkWordPlusWordInEntry("Everybody", "Page", queryStringEverybodyPlusPage2);
	}

	@Test
	public void publicAllAllFlowerPlusFlower() throws Exception {
		LOGGER.fine("ASS: publicAllAllFlowerPlusFlower ");
		checkWordPlusWordInEntry("flower", "Flower", queryStringFlowerPlusFlower1);
	}

	@Test
	public void publicAllBookmarksFlowerPlusFlower() throws Exception {
		LOGGER.fine("ASS: publicAllBookmarksFlowerPlusFlower ");
		checkWordPlusWordInEntry("flower", "Flower", queryStringFlowerPlusFlower2);
	}

	@Test
	public void publicAllAllPagePlusAuction() throws Exception {
		LOGGER.fine("ASS: publicAllAllPagePlusAuction ");
		checkWordPlusWordInEntry("Page", "Auction", queryStringPagePlusAuction1);
	}

	@Test
	public void publicAllBookmarksPagePlusAuction() throws Exception {
		LOGGER.fine("ASS:publicAllBookmarksPagePlusAuction ");
		checkWordPlusWordInEntry("Page", "Auction", queryStringPagePlusAuction2);
	}

	// #########################################Working
	// methods#############################################################################################

	private void checkResult(boolean result, int numberOfFoundWord, String searchWord, String searchWord2) {

		assertTrue(requestToExecute + " Test failed, Number of entries: " + entriesList.size() + " ,Words " + searchWord
				+ " plus " + searchWord2 + " found " + numberOfFoundWord + " times", result);
	}

	public void checkWordPlusWordInEntry(String searchWord, String searchWord2, String searchUrl) throws Exception {
		requestToExecute = FVTUtilsWithDate.createRequestToSend(urlPrefix, searchUrl);
		int searchWordFoundCount = 0;
		LOGGER.fine("URL: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesList(requestToExecute);

		for (int i = 0; i < entriesList.size(); i++) {
			wordFound = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), searchWord);
			word2Found = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), searchWord2);

			if ((wordFound) && (word2Found)) {
				searchWordFoundCount++;
			}
		}

		boolean testResult = FVTUtilsWithDate.checkTestResult1(entriesList.size(), searchWordFoundCount);

		checkResult(testResult, searchWordFoundCount, searchWord, searchWord2);

	}

}
