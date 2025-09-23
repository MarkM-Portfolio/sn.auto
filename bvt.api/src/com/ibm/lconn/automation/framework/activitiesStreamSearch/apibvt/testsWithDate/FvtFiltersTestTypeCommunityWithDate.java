package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

/*
 * 12.12.2013 Ina Sh    - added test checkWithPrivateCommunityIdandTag for checking defect 106746 
 * 26.06.2012 - Finished to get each community id (Community id is equals to connections.containerid value in event community.created)
 * test1:
 * send request filter:community with received communityid value from getCommunitiesIdsForTest method
 * then check for each entry that connections.communityId exists at first, if exists then if it contain communityid value, if yes passCount++
 * if not failedCount++
 * if connections.communityid not exists check if connections.containerid equal to communityid and eventType equal to community.created
 * if both true - passCount++, if one is fail failCount++
 * if failCount >0 test failed, if failCount==0 test passed.
 * also need to take all eventsid from filter:community requests to arraylist for second test
 * 

 */

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population.ASCommunitiesPopulationHelper;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonResponse;

public class FvtFiltersTestTypeCommunityWithDate extends ActivityStreamSearchTest {

	private static String searchStringPublicFilter = "/@public/@all/@all?filters=";
	private static String searchStringPrivateFilter = "/@me/@all/@all?filters=";
	private static String searchStringPrivateCommunity = "/urn:lsid:lconn.ibm.com:communities.community:community_id/@all/@all?filters=";
	private static String searchStringPublic = "/@public/@all/@all" + PopStringConstantsAS.INDEX_SEARCH_PARAMETER;
	private static String searchStringPrivate = "/@me/@all/@all" + PopStringConstantsAS.INDEX_SEARCH_PARAMETER;
	private static String filter = "[{'type':'community','values':['community_id']}]";
	private static String filter_tag = "[{'type':'tag','values':['tag2']}]";

	private static final String REQUEST_TO_GET_COMMUNITY_ID = "/@me/@all/@all?query=";
	ArrayList<String> entriesList = new ArrayList<String>();
	ArrayList<String> requestResults = new ArrayList<String>();
	ArrayList<String> eventIdsFromFilterRequests = new ArrayList<String>();

	@Test
	public void checkWithModeratedCommunityId() throws Exception {

		String COMMUNITY_ID_TO_SEARCH = "";
		Boolean testResultForSearchIdTest;

		LOGGER.fine("ASS: checkWithModeratedCommunityId");
		COMMUNITY_ID_TO_SEARCH = ASCommunitiesPopulationHelper.getModeratedCommunity().getUuid();
		LOGGER.fine("Moderated community container Id: " + COMMUNITY_ID_TO_SEARCH);
		testResultForSearchIdTest = checkFilterCommunityRequest(COMMUNITY_ID_TO_SEARCH, urlPrefix,
				searchStringPublicFilter);

		assertTrue("Result for Moderated Community ID check: " + testResultForSearchIdTest, testResultForSearchIdTest);
	}

	@Test
	public void checkWithPrivateCommunityId() throws Exception {
		String COMMUNITY_ID_TO_SEARCH = "";
		Boolean testResultForSearchIdTest;

		LOGGER.fine("ASS: checkWithPrivateCommunityId");
		COMMUNITY_ID_TO_SEARCH = ASCommunitiesPopulationHelper.getPrivateCommunity().getUuid();
		LOGGER.fine("Private community container Id: " + COMMUNITY_ID_TO_SEARCH);

		testResultForSearchIdTest = checkFilterCommunityRequest(COMMUNITY_ID_TO_SEARCH, urlPrefix,
				searchStringPrivateFilter);

		assertTrue("Result for Community ID check: " + testResultForSearchIdTest, testResultForSearchIdTest);
	}

	@Test
	public void checkWithPrivateCommunityIdandTag() throws Exception {

		Boolean testResultForSearchCommunityIdTest = false;
		String COMMUNITY_ID_TO_SEARCH = "";
		LOGGER.fine("ASS: checkWithPrivateCommunityIdandTag");
		COMMUNITY_ID_TO_SEARCH = ASCommunitiesPopulationHelper.getPrivateCommunity().getUuid();
		LOGGER.fine("Private community container Id: " + COMMUNITY_ID_TO_SEARCH);

		testResultForSearchCommunityIdTest = checkFilterTagCommunityRequest(COMMUNITY_ID_TO_SEARCH, urlPrefix,
				searchStringPrivateCommunity, filter_tag);

		assertTrue("checkWithPrivateCommunityIdandTag: " + testResultForSearchCommunityIdTest,
				testResultForSearchCommunityIdTest);
	}

	@Test
	public void checkWithPublicCommunityId() throws Exception {
		Boolean testResultForSearchIdTest;
		String COMMUNITY_ID_TO_SEARCH = "";
		LOGGER.fine("ASS: checkWithPublicCommunityId ");
		COMMUNITY_ID_TO_SEARCH = ASCommunitiesPopulationHelper.getPublicCommunity().getUuid();
		LOGGER.fine("Public community container Id: " + COMMUNITY_ID_TO_SEARCH);
		testResultForSearchIdTest = checkFilterCommunityRequest(COMMUNITY_ID_TO_SEARCH, urlPrefix,
				searchStringPublicFilter);

		assertTrue("Result for Community ID check: " + testResultForSearchIdTest, testResultForSearchIdTest);
	}

	// ******************************************************************************************************************************
	// ***************************************************Working
	// methods************************************************************
	// ******************************************************************************************************************************

	public Boolean checkFilterCommunityRequest(String communityIdToCheck, String urlPrefix, String requestToSend)
			throws Exception {
		int passCount = 0;
		int failCount = 0;
		boolean testTotalResult = false;
		String communityIdValue;
		String requestToExecute1;
		int index = 2;

		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, requestToSend, filter);

		requestToExecute1 = requestToExecute.replace("community_id", communityIdToCheck);
		LOGGER.fine("URL: " + requestToExecute1);
		JsonResponse js = FVTUtilsWithDate.getJsonResponse(requestToExecute1);
		int numberOfEntries = FVTUtilsWithDate.getJsonResponseEntriesCount(js);
		if (numberOfEntries > 0) {
			for (int i = 0; i < numberOfEntries; i++) {
				eventIdsFromFilterRequests
						.add(js.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY
								+ "[" + i + "]." + PopStringConstantsAS.EVENT_ID_LOCATION));
				communityIdValue = js
						.findValue((PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY + "["
								+ i + "]." + PopStringConstantsAS.COMMUNITY_ID_LOCATION));
				if (communityIdValue == null) {
					LOGGER.fine("Element connections.communityid not found");
					String foundEventContainerId = js
							.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY
									+ "[" + i + "]." + PopStringConstantsAS.COMMUNITY_CONTAINER_ID_LOCATION);
					if (foundEventContainerId.equals(communityIdToCheck)) {
						LOGGER.fine("Index: " + i + " Community id is equal to searched community ID: "
								+ communityIdToCheck);
						passCount++;
					} else {
						LOGGER.fine("Index: " + i + " Community id is not equal to searched community ID: "
								+ communityIdToCheck);
						failCount++;
					}
				} else {
					LOGGER.fine("Element connections.communityid found");
					LOGGER.fine("Community id value: " + communityIdValue);
					if (communityIdValue.contains(communityIdToCheck)) {
						LOGGER.fine("Index: " + i + " Community id found");
						passCount++;
					} else {
						LOGGER.fine("Index: " + i + " Community id not found");
						failCount++;
					}
				}
			}
		} else {
			failCount++;
		}

		if (failCount > 0) {
			testTotalResult = false;
		} else {
			testTotalResult = true;
		}

		return testTotalResult;
	}

	public Boolean checkFilterTagCommunityRequest(String communityIdToCheck, String urlPrefix, String requestToSend,
			String filterTag) throws Exception {
		int passCount = 0;
		int failCount = 0;
		Boolean testTotalResult = false;
		String requestToExecute1;
		String communityIdValue;
		int index = 2;

		requestToExecute = FVTUtilsWithDate.createRequestToSendWithFilter(urlPrefix, requestToSend, filterTag);
		requestToExecute1 = requestToExecute.replace("community_id", communityIdToCheck);
		LOGGER.fine("URL: " + requestToExecute1);
		JsonResponse js = FVTUtilsWithDate.getJsonResponse(requestToExecute1);
		int numberOfEntries = FVTUtilsWithDate.getJsonResponseEntriesCount(js);
		if (numberOfEntries > 0) {
			for (int i = 0; i < numberOfEntries; i++) {
				eventIdsFromFilterRequests
						.add(js.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY
								+ "[" + i + "]." + PopStringConstantsAS.EVENT_ID_LOCATION));
				communityIdValue = js
						.findValue((PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY + "["
								+ i + "]." + PopStringConstantsAS.COMMUNITY_ID_LOCATION));
				if (communityIdValue == null) {
					LOGGER.fine("Element connections.communityid not found");
					String foundEventContainerId = js
							.findValue(PopStringConstantsAS.JSON_ROOT_ELEMENT + PopStringConstantsAS.JSON_ROOT_ENTRY
									+ "[" + i + "]." + PopStringConstantsAS.COMMUNITY_CONTAINER_ID_LOCATION);
					if (foundEventContainerId.equals(communityIdToCheck)) {
						LOGGER.fine("Index: " + i + " Community id is equal to searched community ID: "
								+ communityIdToCheck);
						passCount++;
					} else {
						LOGGER.fine("Index: " + i + " Community id is not equal to searched community ID: "
								+ communityIdToCheck);
						failCount++;
					}
				} else {
					LOGGER.fine("Element connections.communityid found");
					LOGGER.fine("Community id value: " + communityIdValue);
					if (communityIdValue.contains(communityIdToCheck)) {
						LOGGER.fine("Index: " + i + " Community id found");
						passCount++;
					} else {
						LOGGER.fine("Index: " + i + " Community id not found");
						failCount++;
					}
				}
			}
		} else {
			failCount++;
		}

		if (failCount > 0) {
			testTotalResult = false;
		} else {
			testTotalResult = true;
		}

		LOGGER.fine("checkFilterTagCommunityRequest result: " + testTotalResult);
		return testTotalResult;
	}

	public Boolean checkEventExistance(String eventIdToSearch, ArrayList<String> listOfEventsToSearchIn) {
		Boolean eventFound = false;
		for (int i = 0; i < listOfEventsToSearchIn.size(); i++) {
			if (listOfEventsToSearchIn.get(i).equals(eventIdToSearch)) {
				eventFound = true;
				LOGGER.fine("Event: " + eventIdToSearch + "has been found.");
				break;
			}
		}
		LOGGER.fine("Event search result: " + eventFound);
		return eventFound;
	}

}
