package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

/*
 * 09.04.2012 - This script is working with build connectionsi401 only (previous images are not good for this script)
 * query="tiger%20OR%20auction" has a problem and currently removed from queries list - need further investigation
 */

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;

public class FvtCommunityWordOrWordSearchInEntryTestWithDate extends ActivityStreamSearchTest {

	ArrayList<String> entriesList = new ArrayList<String>();
	private static String queryStringOneOrManager1 = "/@public/@all/@all?query=one%20OR%20manager";
	private static String queryStringOneOrManager2 = "/@public/@all/@communities?query=one%20OR%20manager";
	private static String queryStringThreeOrEverybody1 = "/@public/@all/@all?query=three%20OR%20everybody";
	private static String queryStringThreeOrEverybody2 = "/@public/@all/@communities?query=three%20OR%20everybody";
	private static String queryStringMyOrTeam1 = "/@public/@all/@all?query=my%20OR%20team";
	private static String queryStringMyOrTeam2 = "/@public/@all/@communities?query=my%20OR%20team";
	private static String queryStringManagerOrSubway1 = "/@public/@all/@all?query=manager%20OR%20subway";
	private static String queryStringManagerOrSubway2 = "/@public/@all/@communities?query=manager%20OR%20subway";
	private static String queryStringMyOrSubway1 = "/@public/@all/@all?query=my%20OR%20subway";
	private static String queryStringMyOrSubway2 = "/@public/@all/@communities?query=my%20OR%20subway";
	private static String queryStringEverybodyOrSubway1 = "/@public/@all/@all?query=Everybody%20OR%20subway";
	private static String queryStringEverybodyOrSubway2 = "/@public/@all/@communities?query=Everybody%20OR%20subway";
	private static boolean wordFound;
	private static boolean word2Found;

	@Test
	public void publicAllAllOneOrManager() throws Exception {
		checkWordOrWord("one", "manager", queryStringOneOrManager1);
	}

	@Test
	public void publicAllCommunitiesOneOrmanager() throws Exception {
		checkWordOrWord("one", "manager", queryStringOneOrManager2);
	}

	@Test
	public void publicAllAllThreeOrEverybody() throws Exception {
		checkWordOrWord("three", "everybody", queryStringThreeOrEverybody1);
	}

	@Test
	public void publicAllCommunitiesThreeOrEverybody() throws Exception {
		checkWordOrWord("three", "everybody", queryStringThreeOrEverybody2);
	}

	@Test
	public void publicAllAllMyOrTeam() throws Exception {
		checkWordOrWord("my", "team", queryStringMyOrTeam1);
	}

	@Test
	public void publicAllCommunitiesMyOrTeam() throws Exception {
		checkWordOrWord("my", "team", queryStringMyOrTeam2);
	}

	@Test
	public void publicAllAllManagerOrSubway() throws Exception {
		checkWordOrWord("manager", "subway", queryStringManagerOrSubway1);
	}

	@Test
	public void publicAllCommunitiesManagerOrSubway() throws Exception {
		checkWordOrWord("manager", "subway", queryStringManagerOrSubway2);
	}

	@Test
	public void publicAllAllMyOrSubway() throws Exception {
		checkWordOrWord("my", "subway", queryStringMyOrSubway1);
	}

	@Test
	public void publicAllCommunitiesMyOrSubway() throws Exception {
		checkWordOrWord("my", "subway", queryStringMyOrSubway2);
	}

	@Test
	public void publicAllAllEverybodyOrSubway() throws Exception {
		checkWordOrWord("everybody", "subway", queryStringEverybodyOrSubway1);
	}

	@Test
	public void publicAllCommunitiesEverybodyrOrSubway() throws Exception {
		checkWordOrWord("everybody", "subway", queryStringEverybodyOrSubway2);
	}

	// #########################################Working
	// methods#############################################################################################

	public void checkWordOrWord(String searchWord, String searchWord2, String queryUrl) throws Exception {
		requestToExecute = FVTUtilsWithDate.createRequestToSend(urlPrefix, queryUrl);
		int searchWordFoundCount = 0;
		LOGGER.fine("URL: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesList(requestToExecute);

		for (int i = 0; i < entriesList.size(); i++) {
			wordFound = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), searchWord);
			word2Found = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), searchWord2);

			if ((wordFound) || (word2Found)) {
				searchWordFoundCount++;
			}
		}

		boolean testResult = FVTUtilsWithDate.checkTestResult1(entriesList.size(), searchWordFoundCount);
		testResult = testResult && (searchWordFoundCount != 0);
		assertTrue(requestToExecute + " Test failed, Number of entries: " + entriesList.size() + "Words " + searchWord
				+ " or " + searchWord2 + "not found ", testResult);

	}

}
