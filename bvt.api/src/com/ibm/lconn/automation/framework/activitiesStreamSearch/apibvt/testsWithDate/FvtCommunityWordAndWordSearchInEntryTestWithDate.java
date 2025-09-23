package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;

public class FvtCommunityWordAndWordSearchInEntryTestWithDate extends ActivityStreamSearchTest {

	ArrayList<String> entriesList = new ArrayList<String>();
	private static String queryStringOneAndManager1 = "/@public/@all/@all?query=One%20AND%20Manager";
	private static String queryStringOneAndManager2 = "/@public/@all/@communities?query=One%20AND%20Manager";
	private static String queryStringEverybodyAndTeam1 = "/@public/@all/@all?query=Everybody%20AND%20Team";
	private static String queryStringEverybodyAndTeam2 = "/@public/@all/@communities?query=Everybody%20AND%20team";
	private static String queryStringThreeAndTiger1 = "/@public/@all/@all?query=Three%20AND%20tiger";
	private static String queryStringThreeAndTiger2 = "/@public/@all/@communities?query=Three%20AND%20iger";
	private static String queryStringMyAndTiger1 = "/@public/@all/@all?query=My%20AND%20tiger";
	private static String queryStringMyAndTiger2 = "/@public/@all/@communities?query=My%20AND%20tiger";
	private static String queryStringManagerAndSubway1 = "/@public/@all/@all?query=Manager%20AND%20subway";
	private static String queryStringManagerAndSubway2 = "/@public/@all/@communities?query=Manager%20AND%20subway";
	private static String queryStringMyAndSubway1 = "/@public/@all/@all?query=My%20AND%20subway";
	private static String queryStringMyAndSubway2 = "/@public/@all/@communities?query=My%20AND%20subway";
	private static String queryStringEverybodyAndSubway1 = "/@public/@all/@all?query=Everybody%20AND%20subway";
	private static String queryStringEverybodyAndSubway2 = "/@public/@all/@communities?query=Everybody%20AND%20subway";
	private static boolean wordFound;
	private static boolean word2Found;

	@Test
	public void publicAllAllOneAndManager() throws Exception {
		checkWordAndWord("One", "Manager", queryStringOneAndManager1);
	}

	@Test
	public void publicAllCommunitiesOneAndManager() throws Exception {
		checkWordAndWord("One", "Manager", queryStringOneAndManager2);
	}

	@Test
	public void publicAllAllEverybodyAndTeam() throws Exception {
		checkWordAndWord("Everybody", "Team", queryStringEverybodyAndTeam1);
	}

	@Test
	public void publicAllCommunitiesEverybodyAndTeam() throws Exception {
		checkWordAndWord("Everybody", "Team", queryStringEverybodyAndTeam2);
	}

	@Test
	public void publicAllAllThreeAndTiger() throws Exception {
		checkWordAndWord("Three", "tiger", queryStringThreeAndTiger1);
	}

	@Test
	public void publicAllAllManagerAndSubway() throws Exception {
		checkWordAndWord("Manager", "subway", queryStringManagerAndSubway1);
	}

	@Test
	public void publicAllCommunitiesManagerAndSubway() throws Exception {
		checkWordAndWord("Manager", "subway", queryStringManagerAndSubway2);
	}

	@Test
	public void publicAllAllEverybodyAndSubway() throws Exception {
		checkWordAndWord("Everybody", "subway", queryStringEverybodyAndSubway1);
	}

	@Test
	public void publicAllEverybodyManagerAndSubway() throws Exception {
		checkWordAndWord("Everybody", "subway", queryStringEverybodyAndSubway2);
	}

	// #########################################Working
	// methods#############################################################################################

	public void checkWordAndWord(String searchWord, String searchWord2, String queryUrl) throws Exception {
		requestToExecute = FVTUtilsWithDate.createRequestToSend(urlPrefix, queryUrl);
		int searchWordFoundCount = 0;
		LOGGER.fine("URL: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesList(requestToExecute);

		for (int i = 0; i < entriesList.size(); i++) {
			wordFound = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), searchWord);
			word2Found = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), searchWord2);
			String title = FVTUtilsWithDate.readJSONTitle(entriesList.get(i));
			if (((wordFound) && (word2Found)) || title.contains("</a> community blog.")) {
				searchWordFoundCount++;
			}
		}

		boolean testResult = FVTUtilsWithDate.checkTestResult1(entriesList.size(), searchWordFoundCount);
		testResult = testResult && (searchWordFoundCount != 0);
		assertTrue(requestToExecute + " Test failed, Number of entries: " + entriesList.size() + " ,Words " + searchWord
				+ " and " + searchWord2 + "not found ", testResult);

	}

}
