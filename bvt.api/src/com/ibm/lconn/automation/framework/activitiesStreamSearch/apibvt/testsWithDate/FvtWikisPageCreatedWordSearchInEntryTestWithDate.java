package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;

public class FvtWikisPageCreatedWordSearchInEntryTestWithDate extends ActivityStreamSearchTest {

	ArrayList<String> entriesList = new ArrayList<String>();
	private static String queryStringManager1 = "/@public/@all/@all?query=Manager";
	private static String queryStringManager2 = "/@public/@all/wikis?query=Manager";
	private static String queryStringEverybody1 = "/@public/@all/@all?query=Everybody";
	private static String queryStringEverybody2 = "/@public/@all/wikis?query=Everybody";
	private static String queryStringStandalone1 = "/@public/@all/@all?query=Standalone";
	private static String queryStringStandalone2 = "/@public/@all/wikis?query=Standalone";
	private static String queryStringNovel1 = "/@public/@all/@all?query=Novely";
	private static String queryStringNovel2 = "/@public/@all/wikis?query=Novel";
	private static String queryStringFifty1 = "/@public/@all/@all?query=Fifty";
	private static String queryStringFifty2 = "/@public/@all/wikis?query=Fifty";
	private static boolean wordFound;

	@Test
	public void publicAllAllManager() throws Exception {
		LOGGER.fine("ASS:publicAllAllManager");
		checkSingleWord("Manager", queryStringManager1);
	}

	@Test
	public void publicAllWikisOne() throws Exception {
		LOGGER.fine("ASS: publicAllWikisOne ");
		checkSingleWord("Manager", queryStringManager2);
	}

	@Test
	public void publicAllAllEverybody() throws Exception {
		LOGGER.fine("ASS: publicAllAllEverybody");
		checkSingleWord("Everybody", queryStringEverybody1);
	}

	@Test
	public void publicAllWikisEverybody() throws Exception {
		LOGGER.fine("ASS: publicAllWikisEverybody");
		checkSingleWord("Everybody", queryStringEverybody2);
	}

	@Test
	public void publicAllAllStandalone() throws Exception {
		LOGGER.fine("ASS:publicAllAllStandalone ");
		checkSingleWord("Standalone", queryStringStandalone1);
	}

	@Test
	public void publicAllWikisStandalone() throws Exception {
		LOGGER.fine("ASS: publicAllWikisStandalone ");
		checkSingleWord("Standalone", queryStringStandalone2);
	}

	@Test
	public void publicAllAllNovel() throws Exception {
		LOGGER.fine("ASS:publicAllAllNovel ");
		checkSingleWord("Novel", queryStringNovel1);
	}

	@Test
	public void publicAllWikisNovel() throws Exception {
		LOGGER.fine("ASS: publicAllWikisNovel ");
		checkSingleWord("Novel", queryStringNovel2);
	}

	@Test
	public void publicAllAllFifty() throws Exception {
		LOGGER.fine("ASS: publicAllAllFifty ");
		checkSingleWord("Fifty", queryStringFifty1);
	}

	@Test
	public void publicAllWikisFifty() throws Exception {
		LOGGER.fine("ASS: publicAllWikisFifty");
		checkSingleWord("Fifty", queryStringFifty2);
	}

	// #########################################Working
	// methods#############################################################################################

	private void checkResult(boolean result, int numberOfFoundWord, String searchWord) {

		assertTrue(requestToExecute + " ,Number of entries: " + entriesList.size() + "Word: " + searchWord + " found "
				+ numberOfFoundWord + " times", result);
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
