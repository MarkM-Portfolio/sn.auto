package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

import java.net.URLEncoder;
import java.util.logging.Logger;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClassPopulation;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.profiles.admin.ProfilesAdminService;
import com.ibm.lconn.automation.framework.services.ublogs.UblogsService;

/*
 * need to find a way to get user external ID for @Mentions population and testing
 */

public class ASUblogsPopulationHelper {

	
	private static UblogsService uBlogsService;
	private static ProfilesAdminService profileService;
	
	private static String sendToMySelf;
	private static String sendToOther;
	static String userMySelfId;
	static String userOtherId;
     private RestAPIUser restAPIUser;
	protected static Logger LOGGER = FvtMasterLogsClassPopulation.LOGGER;
	

	private String ublog_entry = "{\"content\":\"" + "ublog_string" + "\"}";

	
	
	
	public ASUblogsPopulationHelper() throws Exception {
		restAPIUser = new RestAPIUser(UserType.ASSEARCH);
		ServiceEntry profileServiceEntry = restAPIUser.getService("microblogging");
		restAPIUser.addCredentials(profileServiceEntry);
		uBlogsService = new UblogsService(restAPIUser.getAbderaClient(),
				profileServiceEntry);
		ServiceEntry profiles = restAPIUser.getConfigService().getService("profiles");
		if (profiles != null) {
			Utils.addServiceAdminCredentials(profiles, restAPIUser.getAbderaClient());
			
			profileService = new ProfilesAdminService(restAPIUser.getAbderaClient(), profiles);
			sendToMySelf = PopStringConstantsAS.SERVER_URL
					+ "/connections/opensocial/basic/rest/ublog/@me/@all";
			sendToOther = PopStringConstantsAS.SERVER_URL
					+ "/connections/opensocial/basic/rest/ublog/userIdtoSend/@all";

			if (profileService.isFoundService()) {

				userMySelfId = profileService.getUserID(URLEncoder.encode(
						restAPIUser.getProfData().getRealName(), "UTF-8"));
				userOtherId = profileService.getUserID(URLEncoder.encode(
						PopStringConstantsAS.MENTIONS_OTHER_USER, "UTF-8"));
			} else {
				LOGGER.fine("Profiles service initialization failed");
			}
		} else {
			LOGGER.fine("Profiles service doesn't exist");
		}
		
	}

	

	public void createMentionsMessagesToMySelf() {

		if ((uBlogsService != null) && (profileService != null)) {
			String ublogString = createUblogContentString(
					PopStringConstantsAS.MENTIONS_MESSAGE_1,
					PopStringConstantsAS.MENTIONS_EVENT_CREATION_STRING, "@"
							+ restAPIUser.getProfData().getRealName(),
					userMySelfId);
			String ublog_entry1 = new String(ublog_entry);
			ublog_entry1 = ublog_entry1.replace("ublog_string", ublogString);

			uBlogsService.createMentionsEntry(sendToMySelf, ublog_entry1);
			LOGGER.fine("createMentions1 HTTP Post response code : "
					+ uBlogsService.getRespStatus());
			ublogString = createUblogContentString(
					PopStringConstantsAS.MENTIONS_MESSAGE_2,
					PopStringConstantsAS.MENTIONS_EVENT_CREATION_STRING, "@"
							+ restAPIUser.getProfData().getRealName(),
					userMySelfId);
			String ublog_entry2 = new String(ublog_entry);
			ublog_entry2 = ublog_entry2.replace("ublog_string", ublogString);

			uBlogsService.createMentionsEntry(sendToMySelf, ublog_entry2);
			LOGGER.fine("createMentions2 HTTP Post response code: "
					+ uBlogsService.getRespStatus());
			ublogString = createUblogContentString(
					PopStringConstantsAS.MENTIONS_MESSAGE_3,
					PopStringConstantsAS.MENTIONS_EVENT_CREATION_STRING, "@"
							+ restAPIUser.getProfData().getRealName(),
					userMySelfId);
			String ublog_entry3 = new String(ublog_entry);
			ublog_entry3 = ublog_entry3.replace("ublog_string", ublogString);

			uBlogsService.createMentionsEntry(sendToMySelf, ublog_entry3);
			LOGGER.fine("createMentions3 HTTP Post response code: "
					+ uBlogsService.getRespStatus());
		}
	}

	public void createMentionsMessagesToOther() {
		if ((uBlogsService != null) && (profileService != null)) {
			String ublogString = createUblogContentString(
					PopStringConstantsAS.MENTIONS_MESSAGE_1,
					PopStringConstantsAS.MENTIONS_EVENT_CREATION_STRING, "@"
							+ PopStringConstantsAS.MENTIONS_OTHER_USER,
					userOtherId);
			String ublog_entry4 = new String(ublog_entry);
			ublog_entry4 = ublog_entry4.replace("ublog_string", ublogString);
			String sendToOther2 = new String(sendToOther);
			sendToOther2 = sendToOther2.replace("userIdtoSend", userOtherId);

			uBlogsService.createMentionsEntry(sendToOther2, ublog_entry4);
			LOGGER.fine("HTTP Post createMentionsToOther1 response code: "
					+ uBlogsService.getRespStatus());

			ublogString = createUblogContentString(
					PopStringConstantsAS.MENTIONS_MESSAGE_2,
					PopStringConstantsAS.MENTIONS_EVENT_CREATION_STRING, "@"
							+ PopStringConstantsAS.MENTIONS_OTHER_USER,
					userOtherId);

			String ublog_entry5 = new String(ublog_entry);
			ublog_entry5 = ublog_entry5.replace("ublog_string", ublogString);
			String sendToOther3 = new String(sendToOther);
			sendToOther3 = sendToOther3.replace("userIdtoSend", userOtherId);

			uBlogsService.createMentionsEntry(sendToOther3, ublog_entry5);
			LOGGER.fine("HTTP Post createMentionsToOther2 response code: "
					+ uBlogsService.getRespStatus());

			ublogString = createUblogContentString(
					PopStringConstantsAS.MENTIONS_MESSAGE_3,
					PopStringConstantsAS.MENTIONS_EVENT_CREATION_STRING, "@"
							+ PopStringConstantsAS.MENTIONS_OTHER_USER,
					userOtherId);
			String ublog_entry6 = new String(ublog_entry);
			ublog_entry6 = ublog_entry6.replace("ublog_string", ublogString);
			String sendToOther4 = new String(sendToOther);
			sendToOther4 = sendToOther4.replace("userIdtoSend", userOtherId);
			uBlogsService.createMentionsEntry(sendToOther4, ublog_entry6);
			LOGGER.fine("HTTP Post createMentionsToOther3 response code: "
					+ uBlogsService.getRespStatus());
		}
	}

	public void createMentionsMessagesToMySelfAndOther() {
		try {
			
			createMentionsMessagesToMySelf();
			createMentionsMessagesToOther();
		} catch (Exception e) {
			LOGGER.fine("Exception in Mentions population: "
					+ e.getMessage());
		}
	}

	private String createUblogContentString(String message,
			String mentionsString, String userName, String userId) {
		String ublogString = new String(message);
		ublogString = ublogString.replace("mentioned_user", mentionsString);

		ublogString = ublogString.replace("userName", userName);

		ublogString = ublogString.replace("userId", userId);

		return ublogString;
	}

}
