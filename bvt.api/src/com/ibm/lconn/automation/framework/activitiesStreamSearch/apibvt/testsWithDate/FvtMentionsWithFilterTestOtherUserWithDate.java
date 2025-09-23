package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.testsWithDate;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

import java.net.URLEncoder;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.DateFilter;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.FVTUtilsWithDate;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.json.JsonResponse;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.profiles.admin.ProfilesAdminService;

public class FvtMentionsWithFilterTestOtherUserWithDate extends ActivityStreamSearchTest {

	private static String searchString = "/@public/@all/@all";
	private static String filterQuery = "filters=";
	private static String filterParam = "[{'type':'mentioned_person', 'values':['user_external_id']}]";

	private static String userExtId;
	private static String userExtIdShort;
	private static Abdera abdera;
	private static AbderaClient client;
	private static ServiceConfig config;
	private static UsernamePasswordCredentials credentials;
	private static ProfilesAdminService profileService;

	private void setUpProfileAdmin() throws Exception {

		abdera = new Abdera();
		client = new AbderaClient(abdera);
		credentials = new UsernamePasswordCredentials(PopStringConstantsAS.USER_NAME,
				PopStringConstantsAS.USER_PASSWORD);

		AbderaClient.registerTrustManager();

		client.addCredentials(PopStringConstantsAS.SERVER_URL, StringConstants.AUTH_REALM_PROFILES,
				StringConstants.AUTH_BASIC, credentials);
		client.addCredentials(PopStringConstantsAS.SERVER_URL, StringConstants.AUTH_REALM_FORCED,
				StringConstants.AUTH_BASIC, credentials);

		config = new ServiceConfig(client, PopStringConstantsAS.SERVER_URL, true);

		ServiceEntry profiles = config.getService("profiles");
		if (profiles == null) {
			LOGGER.fine("Profiles entry is null");

		}
		assert (profiles != null);
		Utils.addServiceAdminCredentials(profiles, client);

		profileService = new ProfilesAdminService(client, profiles);
		if (!profileService.isFoundService()) {
			LOGGER.fine("Profiles admin service is not found");

		}
		assert (profileService.isFoundService());
		userExtIdShort = profileService.getUserID(URLEncoder.encode(PopStringConstantsAS.USER_DISPLAY_NAME, "UTF-8"));
		userExtId = PopStringConstantsAS.USER_ID_PREFIX + userExtIdShort;

	}

	@Test
	public void checkMentionsWithQuery() throws Exception {
		LOGGER.fine("ASS:FvtMentionsWithFilterTestOtherUserWithDate: checkMentionsWithQuery");
		setUpProfileAdmin();
		requestToExecute = urlPrefix + searchString + PopStringConstantsAS.DB_SEARCH_PARAMETER + "&" + filterQuery
				+ URLEncoder.encode(filterParam, "UTF-8") + "&count=" + PopStringConstantsAS.RECEIVED_PAGE_SIZE
				+ DateFilter.instance().getDateFilterParam();
		requestToExecute = requestToExecute.replace("user_external_id", userExtId);
		LOGGER.fine("Request to send: " + requestToExecute);
		JsonResponse js = FVTUtilsWithDate.getJsonResponse(requestToExecute);
		assertNotNull("Response is NULL", js);
		int correctMentionsEvents = FVTUtilsWithDate.checkMentionsEventExistanceByEventTypeAndMentionedPersonExtID(js,
				PopStringConstantsAS.MENTIONS_EVENT_TYPE_1, PopStringConstantsAS.MENTIONS_EVENT_TYPE_2,
				PopStringConstantsAS.MENTIONS_EVENT_TYPE_3, PopStringConstantsAS.EVENT_TYPE_LOCATION,
				PopStringConstantsAS.EVENT_SUMMARY_LOCATION, userExtIdShort, "summary");

		assertEquals("Number of received events is not equal to the number of correct mentiones events",
				FVTUtilsWithDate.getJsonResponseEntriesCount(js), correctMentionsEvents);
	}

}
