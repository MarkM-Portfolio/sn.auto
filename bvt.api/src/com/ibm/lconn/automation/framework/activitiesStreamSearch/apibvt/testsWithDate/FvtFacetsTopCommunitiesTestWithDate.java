package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;

import java.net.URLEncoder;
import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.DateFilter;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonResponse;

public class FvtFacetsTopCommunitiesTestWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/@all?";
	private static String facetQuery = "facetRequests=";
	private static String facetParam = "[{'communities':2}]";
	private static String queryFilter = "query=";
	private static String queryParam = "community_name";
	private static ArrayList<String> entriesList;
	private static ArrayList<String> labelesList;
	private static ArrayList<Double> scoresList;

	@Test
	public void checkTopCommunities() throws Exception {
		LOGGER.fine("ASS: checkTopCommunities");

		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFacet(urlPrefix, searchString, facetQuery,
				facetParam);
		LOGGER.fine("Request to send: " + requestToExecute);
		JsonResponse js = FVTUtilsWithDate.getJsonResponse(requestToExecute);
		assertNotNull("Response is NULL", js);
		entriesList = FVTUtilsWithDate.getJsonResponseValuesList(js, PopStringConstantsAS.TOP_COMMUNITIES_LOCATION);

		if (entriesList != null) {

			labelesList = js.getAllValues(
					PopStringConstantsAS.TOP_COMMUNITIES_LOCATION + ".." + PopStringConstantsAS.FACETS_LABEL_LOCATION);
			scoresList = js.getAllValuesDouble(
					PopStringConstantsAS.TOP_COMMUNITIES_LOCATION + ".." + PopStringConstantsAS.FACETS_SCORE_LOCATION);

		}
	}

	@Test
	public void checkTopCommunitiesCount() throws Exception {
		ArrayList<String> receivedEntries = new ArrayList<String>();
		LOGGER.fine("ASS: checkTopCommunitiesCount");

		assertNotNull("label list is NULL", labelesList);
		assertNotNull("scores list is NULL", scoresList);
		for (int i = 0; i < labelesList.size(); i++) {
			requestToExecute = urlPrefix + searchString + queryFilter + queryParam + "&count="
					+ PopStringConstantsAS.RECEIVED_PAGE_SIZE
					+ FVTUtilsWithDate.createDateFilterString(DateFilter.instance().getDateFilterParam());
			requestToExecute = requestToExecute.replace("community_name",
					URLEncoder.encode(labelesList.get(i), "UTF-8"));
			LOGGER.fine("Request to execute: " + requestToExecute);
			receivedEntries = FVTUtilsWithDate.getJsonResponseEntriesListNotContainWord(requestToExecute,
					PopStringConstantsAS.SUB_COMMUNITY_SUFFIX);

			assertEquals(
					"Test Failed, Community title: " + labelesList.get(i) + " Expected number of entries: "
							+ scoresList.get(i).intValue() + " Received number of entries: " + receivedEntries.size(),
					scoresList.get(i).intValue(), receivedEntries.size());

		}
	}

}
