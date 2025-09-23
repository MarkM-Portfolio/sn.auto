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

public class FvtFacetsTopPeopleTestWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/@all?";
	private static String facetQuery = "facetRequests=";
	private static String facetParam = "[{'people':2}]";
	private static String filterQuery = "filters=";
	private static String filterParam = "[{'type':'actor','values':['user_ID']}]";
	private static ArrayList<String> entriesList;
	private static ArrayList<String> labelesList;
	private static ArrayList<Double> scoresList;
	private static ArrayList<String> idsList;

	@Test
	public void checkTopPeople() throws Exception {
		LOGGER.fine("ASS: checkTopPeople ");
		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFacet(urlPrefix, searchString, facetQuery,
				facetParam);
		LOGGER.fine("Request to send: " + requestToExecute);
		JsonResponse js = FVTUtilsWithDate.getJsonResponse(requestToExecute);
		assertNotNull("Response is NULL", js);
		entriesList = FVTUtilsWithDate.getJsonResponseValuesList(js, PopStringConstantsAS.TOP_PEOPLE_LOCATION);
		if (entriesList != null) {

			labelesList = js.getAllValues(
					PopStringConstantsAS.TOP_PEOPLE_LOCATION + "." + PopStringConstantsAS.FACETS_LABEL_LOCATION);
			scoresList = js.getAllValuesDouble(
					PopStringConstantsAS.TOP_PEOPLE_LOCATION + "." + PopStringConstantsAS.FACETS_SCORE_LOCATION);
			idsList = js.getAllValues(
					PopStringConstantsAS.TOP_PEOPLE_LOCATION + "." + PopStringConstantsAS.FACETS_ID_LOCATION);

		}
		assertNotNull("Top people list is null", entriesList);
	}

	@Test
	public void checkTopPeopleEntriesCount() throws Exception {
		LOGGER.fine("ASS: checkTopPeopleEntriesCount");

		assertNotNull("scores list is null", scoresList);
		assertNotNull("ids list is null", idsList);

		for (int i = 0; i < labelesList.size(); i++) {

			requestToExecute = urlPrefix + searchString + filterQuery + URLEncoder.encode(filterParam, "UTF-8")
					+ "&count=" + PopStringConstantsAS.RECEIVED_PAGE_SIZE + "&query="
					+ PopStringConstantsAS.ASSEARCH_SUFFIX
					+ FVTUtilsWithDate.createDateFilterString(DateFilter.instance().getDateFilterParam());
			requestToExecute = requestToExecute.replace("user_ID", idsList.get(i));
			LOGGER.fine("Request to execute: " + requestToExecute);
			int entriesNumber = FVTUtilsWithDate.getTotalEntriesCount(requestToExecute);

			assertEquals(
					"Test Failed, Person name: " + labelesList.get(i) + " Expected number of entries: "
							+ scoresList.get(i).intValue() + " Received number of entries: " + entriesNumber,
					scoresList.get(i).intValue(), entriesNumber);

		}
	}

}
