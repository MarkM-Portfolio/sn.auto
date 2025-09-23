package com.ibm.lconn.automation.framework.search.rest.api.population;

import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.abdera.model.Categories;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.nodes.TagsEntry;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;

public class Profile2TagUpdater {

	private RestAPIUser profileUser;

	ProfilesService profilesService;

	public Profile2TagUpdater() throws FileNotFoundException, IOException,
			URISyntaxException {

		profileUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry profilesServiceEntry = profileUser.getService("profiles");

		profileUser.addCredentials(profilesServiceEntry);

		try {
			profilesService = new ProfilesService(profileUser.getAbderaClient(),
					profilesServiceEntry);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assert (profilesService.isFoundService());

	}

	public void update() {
		VCardEntry vCard = profilesService.getUserVCard();
		Categories tagsCategories = profilesService.getProfileTags(profileUser
				.getProfData().getEmail(), null); // Get existing tags for user

		String tag1 = String.valueOf(System.currentTimeMillis() / 2
				+ System.currentTimeMillis());
		// String tag2 =
		// SearchRestAPIUtils.generateTagValue(Purpose.PEOPLE_FINDER);

		TagsEntry test = new TagsEntry(tagsCategories);

		test.addTag(SearchRestAPIUtils.getExecId(Purpose.TEMP));
		test.addTag(SearchRestAPIUtils.generateTagValue(Purpose.TEMP));

		if (!profileUser.getConfigService().isEmailHidden()) {
			String profileEmail = vCard.getVCardFields().get(
					StringConstants.VCARD_EMAIL);
			assertTrue(profilesService.setProfileTags(test, profileEmail, null,
					profileEmail, null) != null);
		} else {
			String profileKey = vCard.getVCardFields().get(
					StringConstants.VCARD_PROFILE_KEY);
			assertTrue(profilesService.setProfileTags(test, null, profileKey,
					null, profileKey) != null);
		}
		ArrayList<String> tags = new ArrayList<String>();
		tags.add(tag1);

	}

}
