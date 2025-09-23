package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertTrue;

import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonClient;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class FvtJsonResponseFriendsWithDate extends ActivityStreamSearchTest {

	int maxEntriesCount = 3;

	// Story 90474 Block @friends AS feed on SC
	@Test
	public void requestMeFriendsStatus() throws Exception {
		requestSimple(REQUEST_ME_FRIENDS_STATUS, "");
	}

	// ####################################################################################################

	private void requestSimple(String requestUrl, String advancedFilter) throws Exception {

		if (advancedFilter == "") {
			requestToExecute = com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate
					.createRequestUrl(urlPrefix, requestUrl);
		} else {
			requestToExecute = com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate
					.createRequestUrlWithFilter(urlPrefix, requestUrl, advancedFilter);
		}
		LOGGER.fine("URL: " + requestToExecute);
		ProfileData profData = null;
		try {
			profData = ProfileLoader.getProfile(2);

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JsonClient _jclient = new JsonClient(profData.getEmail(), profData.getPassword(), URLConstants.SERVER_URL);
		boolean jsonTestResult = _jclient.executeJson(requestToExecute);
		assertTrue("Test failed, Request has not a valid respond: " + requestToExecute, jsonTestResult);

	}

}
