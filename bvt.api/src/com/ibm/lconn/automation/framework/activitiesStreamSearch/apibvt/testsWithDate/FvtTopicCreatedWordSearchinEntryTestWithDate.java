package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;

public class FvtTopicCreatedWordSearchinEntryTestWithDate extends ActivityStreamSearchTest {

	ArrayList<String> entriesList = new ArrayList<String>(); // for all entries
	private static String queryStringManager1 = "/@public/@all/@all?query=Manager";
	private static String queryStringManager2 = "/@public/@all/forums?query=Manager";
	private static String queryStringEverybody1 = "/@public/@all/@all?query=Everybody";
	private static String queryStringEverybody2 = "/@public/@all/forums?query=Everybody";
	private static String queryStringSecond1 = "/@public/@all/@all?query=Second";
	private static String queryStringSecond2 = "/@public/@all/forums?query=Second";
	private static String queryStringItem1 = "/@public/@all/@all?query=Item";
	private static String queryStringItem2 = "/@public/@all/forums?query=Item";
	private static String queryStringSeven1 = "/@public/@all/@all?query=Seven";
	private static String queryStringSeven2 = "/@public/@all/forums?query=Seven";
	private static String queryStringEleven1 = "/@public/@all/@all?query=Eleven";
	private static String queryStringEleven2 = "/@public/@all/forums?query=Eleven";
	private static String queryStringStory1 = "/@public/@all/@all?query=Story";
	private static String queryStringStory2 = "/@public/@all/forums?query=Story";
	private static String queryStringTen1 = "/@public/@all/@all?query=Ten";
	private static String queryStringTen2 = "/@public/@all/forums?query=Ten";
	private static boolean wordFound;

	@Test
	public void publicAllAllManager() throws Exception {
		LOGGER.fine("ASS: publicAllAllManager ");
		checkSingleWord("Manager", queryStringManager1);
	}

	@Test
	public void publicAllForumsManager() throws Exception {
		LOGGER.fine("ASS: publicAllForumsManager ");
		checkSingleWord("Manager", queryStringManager2);
	}

	@Test
	public void publicAllAllEverybody() throws Exception {
		LOGGER.fine("ASS: publicAllAllEverybody ");
		checkSingleWord("Everybody", queryStringEverybody1);
	}

	@Test
	public void publicAllForumsEverybody() throws Exception {
		LOGGER.fine("ASS:publicAllForumsEverybody ");
		checkSingleWord("Everybody", queryStringEverybody2);
	}

	@Test
	public void publicAllAllSecond() throws Exception {
		LOGGER.fine("ASS: publicAllAllSecond ");
		checkSingleWord("Second", queryStringSecond1);
	}

	@Test
	public void publicAllForumsSecond() throws Exception {
		LOGGER.fine("ASS: publicAllForumsSecond ");
		checkSingleWord("Second", queryStringSecond2);
	}

	@Test
	public void publicAllAllItem() throws Exception {
		LOGGER.fine("ASS: publicAllAllItem ");
		checkSingleWord("Item", queryStringItem1);
	}

	@Test
	public void publicAllForumsItem() throws Exception {
		LOGGER.fine("ASS: publicAllForumsItem ");
		checkSingleWord("Item", queryStringItem2);
	}

	@Test
	public void publicAllAllSeven() throws Exception {
		LOGGER.fine("ASS: publicAllAllSeven");
		checkSingleWord("Seven", queryStringSeven1);
	}

	@Test
	public void publicAllForumsSeven() throws Exception {
		LOGGER.fine("ASS: publicAllForumsSeven ");
		checkSingleWord("Seven", queryStringSeven2);
	}

	@Test
	public void publicAllAllEleven() throws Exception {
		LOGGER.fine("ASS:publicAllAllEleven ");
		checkSingleWord("Eleven", queryStringEleven1);
	}

	@Test
	public void publicAllForumsEleven() throws Exception {
		LOGGER.fine("ASS: publicAllForumsEleven ");
		checkSingleWord("Eleven", queryStringEleven2);
	}

	@Test
	public void publicAllAllStory() throws Exception {
		LOGGER.fine("ASS: publicAllAllStory ");
		checkSingleWord("Story", queryStringStory1);
	}

	@Test
	public void publicAllForumsStory() throws Exception {
		LOGGER.fine("ASS: publicAllForumsStory");
		checkSingleWord("Story", queryStringStory2);
	}

	@Test
	public void publicAllAllTen() throws Exception {
		LOGGER.fine("ASS: publicAllForumsTen");
		checkSingleWord("Ten", queryStringTen1);
	}

	@Test
	public void publicAllForumsTen() throws Exception {
		LOGGER.fine("ASS: publicAllForumsTen");
		checkSingleWord("Ten", queryStringTen2);
	}

	// #########################################Working
	// methods#############################################################################################

	private void checkResult(boolean result, int numberOfFoundWord, String searchWord) {

		assertTrue(requestToExecute + " Test failed, Number of entries: " + entriesList.size() + " ,Word " + searchWord
				+ " found " + numberOfFoundWord + " times", result);
	}

	public void checkSingleWord(String searchWord, String searchUrl) throws Exception {
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
