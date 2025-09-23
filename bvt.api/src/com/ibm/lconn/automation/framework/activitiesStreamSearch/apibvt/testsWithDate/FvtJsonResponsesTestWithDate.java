package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonResponse;

public class FvtJsonResponsesTestWithDate extends ActivityStreamSearchTest {

	int maxEntriesCount = 3;

	@Test
	public void requestMeAllStatus() throws Exception {
		requestSimple(REQUEST_ME_ALL_STATUS, "");
	}

	@Test
	public void requestMeAllCommunities() throws Exception {
		requestSimple(REQUEST_ME_ALL_COMMUNITIES, "");
	}

	@Test
	public void requestMeAllPeople() throws Exception {
		requestSimple(REQUEST_ME_ALL_PEOPLE, "");
	}

	@Test
	public void requestMeAllTags() throws Exception {
		requestSimple(REQUEST_ME_ALL_TAGS, "");
	}

	@Test
	public void requestMeAllActivities() throws Exception {
		requestSimple(REQUEST_ME_ALL_ACTIVITIES, "");
	}

	@Test
	public void requestMeAllBookmarks() throws Exception {
		requestSimple(REQUEST_ME_ALL_BOOKMARKS, "");
	}

	@Test
	public void requestMeAllBlogs() throws Exception {
		requestSimple(REQUEST_ME_ALL_BLOGS, "");
	}

	@Test
	public void requestMeAllFiles() throws Exception {
		requestSimple(REQUEST_ME_ALL_FILES, "");
	}

	@Test
	public void requestMeAllForums() throws Exception {
		requestSimple(REQUEST_ME_ALL_FORUMS, "");
	}

	@Test
	public void requestMeAllWikis() throws Exception {
		requestSimple(REQUEST_ME_ALL_WIKIS, "");
	}

	// Tests with privacy @public
	@Test
	public void requestPublicAllAll() throws Exception {
		requestSimple(REQUEST_PUBLIC_ALL_ALL, "");
	}

	@Test
	public void requestPublicAllAllWithFilter() throws Exception {
		requestSimple(REQUEST_PUBLIC_ALL_ALL, "&broadcast=true");
	}

	@Test
	public void requestPublicAllStatus() throws Exception {
		requestSimple(REQUEST_PUBLIC_ALL_STATUS, "");
	}

	@Test
	public void requestPublicAllCommunities() throws Exception {
		requestSimple(REQUEST_PUBLIC_ALL_COMMUNITIES, "");
	}

	@Test
	public void requestPublicAllProfiles() throws Exception {
		requestSimple(REQUEST_PUBLIC_ALL_PROFILES, "");
	}

	@Test
	public void requestPublicAllActivities() throws Exception {
		requestSimple(REQUEST_PUBLIC_ALL_ACTIVITIES, "");
	}

	@Test
	public void requestPublicAllBookmarks() throws Exception {
		requestSimple(REQUEST_PUBLIC_ALL_BOOKMARKS, "");
	}

	@Test
	public void requestPublicAllBlogs() throws Exception {
		requestSimple(REQUEST_PUBLIC_ALL_BLOGS, "");
	}

	@Test
	public void requestPublicAllFiles() throws Exception {
		requestSimple(REQUEST_PUBLIC_ALL_FILES, "");
	}

	@Test
	public void requestPublicAllForums() throws Exception {
		requestSimple(REQUEST_PUBLIC_ALL_FORUMS, "");
	}

	@Test
	public void requestPublicAllWikis() throws Exception {
		requestSimple(REQUEST_PUBLIC_ALL_WIKIS, "");
	}

	@Test
	public void requestMeSelfStatus() throws Exception {
		requestSimple(REQUEST_ME_SELF_STATUS, "");
	}

	@Test
	public void requestUidInvolvedAll() throws Exception {
		requestWithInvolvedParameter(userIDLong, REQUEST_INVOLVED_ALL, "");
	}

	@Test
	public void requestUidInvolvedAllWithFilter() throws Exception {
		requestWithInvolvedParameter(userIDLong, REQUEST_INVOLVED_ALL, "&broadcast=true");
	}

	@Test
	public void requestUidInvolvedCommunities() throws Exception {
		requestWithInvolvedParameter(userIDLong, REQUEST_INVOLVED_COMMUNITIES, "");
	}

	@Test
	public void requestUidInvolvedProfiles() throws Exception {
		requestWithInvolvedParameter(userIDLong, REQUEST_INVOLVED_PROFILES, "");
	}

	@Test
	public void requestUidInvolvedActivities() throws Exception {
		requestWithInvolvedParameter(userIDLong, REQUEST_INVOLVED_ACTIVITIES, "");
	}

	@Test
	public void requestUidInvolvedBookmarks() throws Exception {
		requestWithInvolvedParameter(userIDLong, REQUEST_INVOLVED_BOOKMARKS, "");
	}

	@Test
	public void requestUidInvolvedBlogs() throws Exception {
		requestWithInvolvedParameter(userIDLong, REQUEST_INVOLVED_BLOGS, "");
	}

	@Test
	public void requestUidInvolvedFiles() throws Exception {
		requestWithInvolvedParameter(userIDLong, REQUEST_INVOLVED_FILES, "");
	}

	@Test
	public void requestUidInvolvedForums() throws Exception {
		requestWithInvolvedParameter(userIDLong, REQUEST_INVOLVED_FORUMS, "");
	}

	@Test
	public void requestUidInvolvedWikis() throws Exception {
		requestWithInvolvedParameter(userIDLong, REQUEST_INVOLVED_WIKIS, "");
	}

	// ####################################################################################################

	private void requestWithInvolvedParameter(String userId, String requestUrl, String advancedFilter)
			throws Exception {

		if (!userId.equals("")) {
			if (advancedFilter == "") {
				requestToExecute = FVTUtilsWithDate.createRequestUrlWithUid(urlPrefix, userId, requestUrl);
			} else {
				requestToExecute = FVTUtilsWithDate.createRequestUrlWithUidWithFilter(urlPrefix, userId, requestUrl,
						advancedFilter);
			}
			LOGGER.fine("URL: " + requestToExecute);
			JsonResponse js = FVTUtilsWithDate.getJsonResponse(requestToExecute);
			assertNotNull("Response is NULL", js);

		}
	}

	private void requestSimple(String requestUrl, String advancedFilter) throws Exception {

		if (advancedFilter == "") {
			requestToExecute = FVTUtilsWithDate.createRequestUrl(urlPrefix, requestUrl);
		} else {
			requestToExecute = FVTUtilsWithDate.createRequestUrlWithFilter(urlPrefix, requestUrl, advancedFilter);
		}
		JsonResponse js = FVTUtilsWithDate.getJsonResponse(requestToExecute);
		assertNotNull("Response is NULL", js);

	}

}
