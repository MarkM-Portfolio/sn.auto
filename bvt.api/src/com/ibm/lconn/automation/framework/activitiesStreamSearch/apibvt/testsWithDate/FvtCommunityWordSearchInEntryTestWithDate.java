package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;

public class FvtCommunityWordSearchInEntryTestWithDate extends ActivityStreamSearchTest {

	ArrayList<String> entriesList = new ArrayList<String>();
	private static String queryStringOne1 = "/@public/@all/@all?query=one";
	private static String queryStringOne2 = "/@public/@all/@communities?query=one";
	private static String queryStringThree1 = "/@public/@all/@all?query=three";
	private static String queryStringThree2 = "/@public/@all/@communities?query=three";
	private static String queryStringEverybody1 = "/@public/@all/@all?query=Everybody";
	private static String queryStringEverybody2 = "/@public/@all/@communities?query=Everybody";
	private static String queryStringManager1 = "/@public/@all/@all?query=Manager";
	private static String queryStringManager2 = "/@public/@all/@communities?query=Manager";
	private static String queryStringTiger1 = "/@public/@all/@all?query=tiger";
	private static String queryStringTiger2 = "/@public/@all/@communities?query=tiger";
	private static String queryStringAuction1 = "/@public/@all/@all?query=auction";
	private static String queryStringAuction2 = "/@public/@all/@communities?query=auction";
	private static String queryStringSubway1 = "/@public/@all/@all?query=subway";
	private static String queryStringSubway2 = "/@public/@all/@communities?query=subway";
	private static boolean wordFound;

	@Test
	public void publicAllAllOne() throws Exception {
		checkSingleWordInEntry("one", queryStringOne1);

	}

	@Test
	public void publicAllCommunitiesOne() throws Exception {
		checkSingleWordInEntry("one", queryStringOne2);
	}

	@Test
	public void publicAllAllThree() throws Exception {
		checkSingleWordInEntry("three", queryStringThree1);
	}

	@Test
	public void publicAllCommunitiesThree() throws Exception {
		checkSingleWordInEntry("three", queryStringThree2);
	}

	@Test
	public void publicAllAllEverybody() throws Exception {
		checkSingleWordInEntry("Everybody", queryStringEverybody1);
	}

	@Test
	public void publicAllCommunitiesEverybody() throws Exception {
		checkSingleWordInEntry("Everybody", queryStringEverybody2);
	}

	@Test
	public void publicAllAllManager() throws Exception {
		checkSingleWordInEntry("Manager", queryStringManager1);
	}

	@Test
	public void publicAllCommunitiesManager() throws Exception {
		checkSingleWordInEntry("Manager", queryStringManager2);
	}

	@Test
	public void publicAllAllTiger() throws Exception {
		checkSingleWordInEntry("tiger", queryStringTiger1);
	}

	@Test
	public void publicAllCommunitiesTiger() throws Exception {
		checkSingleWordInEntry("tiger", queryStringTiger2);
	}

	@Test
	public void publicAllAllSubway() throws Exception {
		checkSingleWordInEntry("subway", queryStringSubway1);
	}

	@Test
	public void publicAllCommunitiesSubway() throws Exception {
		checkSingleWordInEntry("subway", queryStringSubway2);
	}

	// #########################################Working
	// methods#############################################################################################

	public void checkSingleWordInEntry(String searchWord, String requestUrl) throws Exception {
		requestToExecute = FVTUtilsWithDate.createRequestToSend(urlPrefix, requestUrl);
		int searchWordFoundCount = 0;
		LOGGER.fine("URL: " + requestToExecute);
		entriesList = FVTUtilsWithDate.getJsonResponseEntriesList(requestToExecute);
		for (int i = 0; i < entriesList.size(); i++) {
			String title = FVTUtilsWithDate.readJSONTitle(entriesList.get(i));
			wordFound = FVTUtilsWithDate.searchWordInEntry(entriesList.get(i), searchWord);
			if (wordFound || title.contains("</a> community blog.")) {
				searchWordFoundCount++;
			}
}
		boolean testResult = FVTUtilsWithDate.checkTestResult1(entriesList.size(), searchWordFoundCount);
		testResult = testResult && (searchWordFoundCount != 0);
		assertTrue(requestToExecute + " Test failed, Number of entries: " + entriesList.size() + " ,Word: " + searchWord
				+ " not found ", testResult);

	}

}
