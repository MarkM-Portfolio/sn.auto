package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

import static org.testng.AssertJUnit.assertEquals;

import java.util.logging.Logger;

import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClassPopulation;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Comment;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Message;
import com.ibm.lconn.automation.framework.services.profiles.nodes.TagsEntry;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;

public class ASProfilesPopulationHelper {
	
	private static ProfilesService service;
	private RestAPIUser profileUser;
	
	protected static Logger LOGGER = FvtMasterLogsClassPopulation.LOGGER;

	
	
	
	public ASProfilesPopulationHelper() throws Exception {
		profileUser = new RestAPIUser(UserType.ASSEARCH);
		ServiceEntry profilesServiceEntry = profileUser.getService("profiles");
		profileUser.addCredentials(profilesServiceEntry);
		service = new ProfilesService(profileUser.getAbderaClient(),
				profilesServiceEntry);
		}

	

	public void addTagsToUserProfile() {
		if (service != null) {
			addProfileTags(PopStringConstantsAS.PROFILE_TAG1,
					PopStringConstantsAS.PROFILE_TAG2);
		}
	}

	public void createBoardMessageReply() {
		if (service != null) {
			String MyUpdate = PopStringConstantsAS.PROFILE_STATUS_UPDATE_1
					+ " " + PopStringConstantsAS.eventIdent;
			addBoardMessage(MyUpdate);

			addBoardMessageReply(
					PopStringConstantsAS.PROFILE_STATUS_UPDATE_1_REPLAY,
					MyUpdate);
		}
	}

	public void createProfileStatus() {
		if (service != null) {
			String MyUpdate = PopStringConstantsAS.PROFILE_STATUS_UPDATE_2
					+ " " + PopStringConstantsAS.eventIdent;
			addBoardMessage(MyUpdate);

			addBoardMessageReply(
					PopStringConstantsAS.PROFILE_STATUS_UPDATE_2_REPLAY,
					MyUpdate);
		}
	}

	public void populate() {
		try {
			
			addTagsToUserProfile();
			createBoardMessageReply();
			createProfileStatus();
		} catch (Exception e) {
			LOGGER.fine("Exception in StatusUpdates population: "
					+ e.getMessage());
		}

	}

	// *******************************************************************************************************************
	// *******************************************************************************************************************
	// Working functions
	// *******************************************************************************************************************
	// *******************************************************************************************************************

	public void addProfileTags(String tag1, String tag2) {

		VCardEntry vCard = service.getUserVCard();
		Categories tags = service.getProfileTags(
				profileUser.getProfData().getEmail(), null); 

		TagsEntry test = new TagsEntry(tags);
		test.addTag(tag1);
		test.addTag(tag2);

		if (!profileUser.getConfigService().isEmailHidden()) {
			String profileEmail = vCard.getVCardFields().get(
					StringConstants.VCARD_EMAIL);
			if (service.setProfileTags(test, profileEmail, null, profileEmail,
					null) == null) {
				LOGGER.fine("Adding tags to profile failed ");
			}

		} else {
			String profileKey = vCard.getVCardFields().get(
					StringConstants.VCARD_PROFILE_KEY);
			if (service
					.setProfileTags(test, null, profileKey, null, profileKey) == null) {
				LOGGER.fine("Adding tags to profile failed ");

			}
		}
	}

	public void addBoardMessage(String newBoardMessage) {

		Message newMessage = new Message(newBoardMessage);
		if (service.addBoardMessage(newMessage) == null) {
			LOGGER.fine("Adding message to profile failed ");
		}

	}

	public void addBoardMessageReply(String messageReply, String MyMessage) {
		ExtensibleElement boardMessages = service.getBoardMessages();
		if (boardMessages != null){
		

			for (Entry entry : ((Feed) boardMessages).getEntries()) {

			String replyLink = entry.getLink("replies").getHref().toString();
			String title = entry.getTitle();
			if (title.contains(MyMessage)) {
				Comment newComment = new Comment(messageReply);
				if (service.addBoardMessageReply(newComment, replyLink) == null) {
					LOGGER.fine("Adding reply to profile failed ");
				}
				break;
			}
			}
		}
	}

}
