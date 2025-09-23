package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;

public class FvtBlogsCreatedWordAndWirdSearchInEntryWithDate extends ActivityStreamSearchTest {

	ArrayList<String> entriesList = new ArrayList<String>(); // for all entries
																// (total list)
																// from request
	private static String queryStringManagerAndTiger1 = "/@public/@all/@all?query=Manager%20AND%20tiger";
	private static String queryStringManagerAndTiger2 = "/@public/@all/blogs?query=Manager%20AND%20tiger";
	private static String queryStringEverybodyAndStory1 = "/@public/@all/@all?query=Everybody%20AND%20Story";
	private static String queryStringEverybodyAndStory2 = "/@public/@all/blogs?query=Everybody%20AND%20Story";
	private static String queryStringTeamAndTiger1 = "/@public/@all/@all?query=Team%20AND%20tiger";
	private static String queryStringTeamAndTiger2 = "/@public/@all/blogs?query=Team%20AND%20tiger";
	private static String queryStringFortyAndThree1 = "/@public/@all/@all?query=Forty%20AND%20three";
	private static String queryStringFortyAndThree2 = "/@public/@all/blogs?query=Forty%20AND%20three";
	private static boolean wordFound;
	private static boolean word2Found;

	@Test
	public void publicAllAllManagerAndTiger() throws Exception {
		LOGGER.fine("ASS: publicAllAllManagerAndTiger ");
		checkWordAndWordInEntry("Manager", "tiger", queryStringManagerAndTiger1);
	}

	@Test
	public void publicAllBlogsManagerAndTiger() throws Exception {
		LOGGER.fine("ASS: publicAllBlogsManagerAndTiger ");
		checkWordAndWordInEntry("Manager", "tiger", queryStringManagerAndTiger2);
	}

	@Test
	public void publicAllAllEverybodyAndStory() throws Exception {
		LOGGER.fine("ASS:publicAllAllEverybodyAndStory");
		checkWordAndWordInEntry("Everybody", "Story", queryStringEverybodyAndStory1);
	}

	@Test
	public void publicAllBlogsEverybodyAndStory() throws Exception {
		LOGGER.fine("ASS: publicAllBlogsEverybodyAndStory ");
		checkWordAndWordInEntry("Everybody", "Story", queryStringEverybodyAndStory2);
	}

	@Test
	public void publicAllAllTeamAndTiger() throws Exception {
		LOGGER.fine("ASS: publicAllAllTeamAndTiger ");
		checkWordAndWordInEntry("Team", "tiger", queryStringTeamAndTiger1);
	}

	@Test
	public void publicAllBlogsTeamAndTiger() throws Exception {
		LOGGER.fine("ASS: publicAllBlogsTeamAndTiger ");
		checkWordAndWordInEntry("Team", "tiger", queryStringTeamAndTiger2);
	}

	@Test
	public void publicAllAllFortyAndThree() throws Exception {
		LOGGER.fine("ASS: publicAllAllFortyAndThree");
		checkWordAndWordInEntry("Forty", "three", queryStringFortyAndThree1);
	}

	@Test
	public void publicAllBlogsFortyAndThree() throws Exception {
		LOGGER.fine("ASS: publicAllBlogsFortyAndThree ");
		checkWordAndWordInEntry("Forty", "three", queryStringFortyAndThree2);
	}

	// #########################################Working
	// methods#############################################################################################

	private void checkResult(boolean result, int numberOfFoundWord, String searchWord, String searchWord2) {

		assertTrue(requestToExecute + " Test failed, Number of entries: " + entriesList.size() + " ,Words " + searchWord
				+ " and " + searchWord2 + " found " + numberOfFoundWord + " times", result);
	}

	public void checkWordAndWordInEntry(String searchWord, String searchWord2, String searchUrl) throws Exception {
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
