package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;

public class FvtWikisPageCreationWordWordSearchInEntryTestWithDate extends ActivityStreamSearchTest {

	ArrayList<String> entriesList = new ArrayList<String>(); // for all entries
																// (total list)
																// from request
	private static String queryStringFiftyTiger1 = "/@public/@all/@all?query=Fifty%20tiger";
	private static String queryStringFiftyTiger2 = "/@public/@all/wikis?query=Fifty%20tiger";
	private static String queryStringFourEverybody1 = "/@public/@all/@all?query=Four%20Everybody";
	private static String queryStringFourEverybody2 = "/@public/@all/wikis?query=Four%20Everybody";
	private static String queryStringStandaloneStandalone1 = "/@public/@all/@all?query=Standalone%20Standalone";
	private static String queryStringStandaloneStandalone2 = "/@public/@all/wikis?query=Standalone%20Standalone";
	private static boolean wordFound;
	private static boolean word2Found;

	@Test
	public void publicAllAllFiftyTiger() throws Exception {
		LOGGER.fine("ASS: publicAllAllFiftyTiger ");
		checkWordWord("Fifty", "tiger", queryStringFiftyTiger1);
	}

	@Test
	public void publicAllWikisFiftyTiger() throws Exception {
		LOGGER.fine("ASS: publicAllWikisFiftyTiger ");
		checkWordWord("Fifty", "tiger", queryStringFiftyTiger2);
	}

	@Test
	public void publicAllAllFourEverybody() throws Exception {
		LOGGER.fine("ASS:publicAllAllFourEverybody ");

		checkWordWord("Four", "Everybody", queryStringFourEverybody1);
	}

	@Test
	public void publicAllWikisFourEverybody() throws Exception {
		LOGGER.fine("ASS: publicAllWikisFourEverybody ");
		checkWordWord("Four", "Everybody", queryStringFourEverybody2);
	}

	@Test
	public void publicAllAllStandaloneStandalone() throws Exception {
		LOGGER.fine("ASS: publicAllAllStandaloneStandalone ");
		checkWordWord("Standalone", "Standalone", queryStringStandaloneStandalone1);
	}

	@Test
	public void publicAllWikisStandaloneStandalone() throws Exception {
		LOGGER.fine("ASS: publicAllWikisStandaloneStandalone ");
		checkWordWord("Standalone", "Standalone", queryStringStandaloneStandalone2);
	}

	// #########################################Working
	// methods#############################################################################################

	private void checkResult(boolean result, int numberOfFoundWord, String searchWord, String searchWord2) {

		assertTrue("Words " + searchWord + " or " + searchWord2 + " found " + numberOfFoundWord + " number of times",
				result);
	}

	public void checkWordWord(String searchWord, String searchWord2, String searchUrl) throws Exception {
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
