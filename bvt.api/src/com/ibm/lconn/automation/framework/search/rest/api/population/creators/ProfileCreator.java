package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.abdera.model.Categories;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.admin.ProfilesAdminService;
import com.ibm.lconn.automation.framework.services.profiles.admin.nodes.Profile;
import com.ibm.lconn.automation.framework.services.profiles.admin.nodes.ProfileAttribute;
import com.ibm.lconn.automation.framework.services.profiles.nodes.TagsEntry;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;

public class ProfileCreator {

	public static String ADDED_USER_NAME = "PeopleFinder"; // user - no index

	// from properties

	public static String ADDED_USER_PASSWORD = "passw0rd";

	public static String ADDED_USER_REALNAME = "People Finder";

	public static String ADDED_USER_EMAIL1 = "peoplefinder";

	public static String ADDED_USER_EMAIL2 = "@peoplefinder.com";

	public static String ADDED_USER_JOB_RESP1 = "Finder";

	public static String ADDED_USER_JOB_RESP2 = " job responsibility";

	public static String ADDED_USER_PHONE_NUMBER = "1800809080";

	public static String ADDED_USER_GIVEN_NAME = "PF";

	public static String ADDED_USER_TAG = "people_finder_test_user";

	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;

	private String targetKey = null;

	private RestAPIUser profileUser;

	private ProfilesAdminService profilesAdminService;

	ProfilesService profilesService;

	public ProfileCreator() throws FileNotFoundException, IOException,
			URISyntaxException {

		profileUser = new RestAPIUser(UserType.ADMIN);
		ServiceEntry profilesServiceEntry = profileUser.getService("profiles");

		profileUser.addCredentials(profilesServiceEntry);

		try {
			profilesService = new ProfilesService(profileUser.getAbderaClient(),
					profilesServiceEntry);
			profilesAdminService = new ProfilesAdminService(
					profileUser.getAbderaClient(), profilesServiceEntry);

		} catch (LCServiceException e) {
			LOGGER.log(Level.WARNING, "Profile service problem: " + " LCServiceException: "+e.toString());
			assertTrue("Profile service problem ",false);
		}

		
	}

	public void addJobResp() throws FileNotFoundException, IOException {
		ProfileData profData = profileUser.getProfData();
		String email = profData.getEmail();
		Profile profile = getUserProfile(email);
		profile.setAttribute("com.ibm.snx_profiles.base.jobResp",
				new ProfileAttribute("com.ibm.snx_profiles.base.jobResp",
						StringConstants.FieldType.TEXT.toString(),
						"this is a job responsibilities"));
		if (profilesAdminService != null) {
			profilesAdminService
					.updateProfile(profile, email, null, null, null);
		} else {
			LOGGER.log(Level.WARNING, "The Profile Admin service is NULL.");
			assertTrue("Profile Admin service problem ",false);
		}
	}

	public void addTags() {
		if (profilesService != null) {
			VCardEntry vCard = profilesService.getUserVCard();
			Categories tagsCategories = profilesService.getProfileTags(
					profileUser.getProfData().getEmail(), null); // Get existing
			// tags for
			// user

			String tag1 = SearchRestAPIUtils.getExecId(Purpose.PEOPLE_FINDER);
			String tag2 = SearchRestAPIUtils
					.generateTagValue(Purpose.PEOPLE_FINDER);

			TagsEntry test = new TagsEntry(tagsCategories);

			test.addTag(SearchRestAPIUtils.getExecId(Purpose.PEOPLE_FINDER));
			test.addTag(SearchRestAPIUtils
					.generateTagValue(Purpose.PEOPLE_FINDER));

			if (!profileUser.getConfigService().isEmailHidden()) {
				String profileEmail = vCard.getVCardFields().get(
						StringConstants.VCARD_EMAIL);
				assertTrue(profilesService.setProfileTags(test, profileEmail,
						null, profileEmail, null) != null);
			} else {
				String profileKey = vCard.getVCardFields().get(
						StringConstants.VCARD_PROFILE_KEY);
				assertTrue(profilesService.setProfileTags(test, null,
						profileKey, null, profileKey) != null);
			}
			ArrayList<String> tags = new ArrayList<String>();
			tags.add(tag1);
			tags.add(tag2);

			PopulatedData.getInstance().setPeopleFinderProfileTags(profileUser,
					tags);
			PopulatedData.getInstance().setPopulatedLcEntry(vCard,
					Permissions.PUBLIC);
		} else {
			LOGGER.log(Level.WARNING, "The Profiles service is NULL.");
			assertTrue("Profile service problem ",false);
		}
	}

	public void createUserProfiles() throws FileNotFoundException, IOException {
		String emailT = ProfileLoader.getProfile(2).getEmail();
		if (profilesAdminService != null) {
			Profile profileT = profilesAdminService.getFirstProfile(
					URLEncoder.encode(emailT, "UTF-8"), null, null, null);
			assertTrue("An user with " + emailT + "email does not exit",
					profileT != null);
			ProfileAttribute existingPA = profileT
					.getAttribute("com.ibm.snx_profiles.base.tenantKey");

			int result;

			result = createOrUpdateUser(getEmail('A'), 'A', getRealName('A'),
					existingPA);
			result = createOrUpdateUser(getEmail('B'), 'B', getRealName('B'),
					existingPA);
			result = createOrUpdateUser(getEmail('C'), 'C', getRealName('C'),
					existingPA);
			result = createOrUpdateUser(getEmail('D'), 'D', getRealName('D'),
					existingPA);
			// NOTE: need to check status code, get 500 on create user if we
			// already exist
			// // which might happen if delete fails, perhaps call the delete
			// test
			// // and try again
			// // For now assume if its there we're OK to pass
			assertTrue(
					"create profile failed, assuming it might exist and trying to proceed",
					result == 200);
		} else {
			LOGGER.log(Level.WARNING, "The Profiles Admin service is NULL.");
			assertTrue("Profile service problem ",false);
		}
	}

	private int createOrUpdateUser(String email, char ch, String realName,
			ProfileAttribute existingPA) throws UnsupportedEncodingException {
		int result = 0;
		if (profilesAdminService != null) {
			Profile profile = profilesAdminService.getFirstProfile(
					URLEncoder.encode(email, "UTF-8"), null, null, null);
			if (profile == null) {
				profile = new Profile(profilesAdminService.getEditableFields());
				setAttributesForCreation(profile, ch, existingPA);
				result = profilesAdminService.createProfile(targetKey, profile);

			} else {
				setAttributesForUpdate(profile, ch);
				result = profilesAdminService.updateProfile(profile, email,
						null, URLEncoder.encode(realName, "UTF-8"), null);
			}
		}
		return result;

	}

	private void setAttibutesForUpdate(Profile profile, String phoneNumber,
			String givenName, String jobResp) {
		profile.setAttribute("com.ibm.snx_profiles.base.telephoneNumber",
				new ProfileAttribute(
						"com.ibm.snx_profiles.base.telephoneNumber",
						StringConstants.FieldType.TEXT.toString(), phoneNumber));
		profile.setAttribute("com.ibm.snx_profiles.base.givenName",
				new ProfileAttribute("com.ibm.snx_profiles.base.givenName",
						StringConstants.FieldType.TEXT.toString(), givenName));
		profile.setAttribute("com.ibm.snx_profiles.base.jobResp",
				new ProfileAttribute("com.ibm.snx_profiles.base.jobResp",
						StringConstants.FieldType.TEXT.toString(), jobResp));
		profile.setAttribute("com.ibm.snx_profiles.base.workLocationCode",
				new ProfileAttribute(
						"com.ibm.snx_profiles.base.workLocationCode",
						StringConstants.FieldType.TEXT.toString(),
						"This is work Location"));

	}

	private void setAttributesForUpdate(Profile profile, char ch) {
		String phone = getPhoneNumber(ch);
		String givenName = getGivenName(ch);
		String jobResp = getJobResp(ch);
		setAttibutesForUpdate(profile, phone, givenName, jobResp);
	}

	public static String getJobResp(char ch) {
		if (ch == 'A') {
			return ADDED_USER_JOB_RESP1 + ch
					+ SearchRestAPIUtils.getExecId(Purpose.PEOPLE_FINDER)
					+ ADDED_USER_JOB_RESP2;
		} else {
			return ADDED_USER_JOB_RESP1 + 'B'
					+ SearchRestAPIUtils.getExecId(Purpose.PEOPLE_FINDER)
					+ ADDED_USER_JOB_RESP2;
		}
	}

	public static String getPhoneNumber(char ch) {
		return ADDED_USER_PHONE_NUMBER;
	}

	public static String getGivenName(char ch) {
		return ADDED_USER_GIVEN_NAME + ch
				+ SearchRestAPIUtils.getExecId(Purpose.PEOPLE_FINDER);
	}

	private void setAttributesForCreation(Profile profile, String email,
			String realName, String name, ProfileAttribute existingPA) {
		// make it ours
		profile.setAttribute("com.ibm.snx_profiles.base.email",
				new ProfileAttribute("com.ibm.snx_profiles.base.email",
						StringConstants.FieldType.TEXT.toString(), email));
		profile.setAttribute("com.ibm.snx_profiles.base.uid",
				new ProfileAttribute("com.ibm.snx_profiles.base.uid",
						StringConstants.FieldType.TEXT.toString(), realName));
		profile.setAttribute("com.ibm.snx_profiles.base.surname",
				new ProfileAttribute("com.ibm.snx_profiles.base.surname",
						StringConstants.FieldType.TEXT.toString(), name));
		profile.setAttribute("com.ibm.snx_profiles.base.displayName",
				new ProfileAttribute("com.ibm.snx_profiles.base.displayName",
						StringConstants.FieldType.TEXT.toString(), realName));
		profile.setAttribute(
				"com.ibm.snx_profiles.base.distinguishedName",
				new ProfileAttribute(
						"com.ibm.snx_profiles.base.distinguishedName",
						StringConstants.FieldType.TEXT.toString(),
						"cn="
								+ realName
								+ ",cn=Users,l=WestfordFVT,st=Massachusetts,c=US,ou=Lotus,o=Software Group,dc=ibm,dc=com"));
		// generate uids
		profile.setAttribute("com.ibm.snx_profiles.base.guid",
				new ProfileAttribute("com.ibm.snx_profiles.base.guid",
						StringConstants.FieldType.TEXT.toString(),
						java.util.UUID.randomUUID().toString()));
		profile.setAttribute("com.ibm.snx_profiles.base.key",
				new ProfileAttribute("com.ibm.snx_profiles.base.key",
						StringConstants.FieldType.TEXT.toString(),
						java.util.UUID.randomUUID().toString()));

		// Copy tenant key from other profile
		profile.setAttribute("com.ibm.snx_profiles.base.tenantKey", existingPA);
	}

	private void setAttributesForCreation(Profile profile, char ch,
			ProfileAttribute existingPA) {
		String email = getEmail(ch);
		String realName = getRealName(ch);
		String name = getName(ch);
		setAttributesForCreation(profile, email, realName, name, existingPA);
		setAttributesForUpdate(profile, ch);

	}

	public static String getName(char ch) {
		return ADDED_USER_NAME + ch
				+ SearchRestAPIUtils.getExecId(Purpose.PEOPLE_FINDER);
	}

	public static String getRealName(char ch) {
		return ADDED_USER_REALNAME + ch
				+ SearchRestAPIUtils.getExecId(Purpose.PEOPLE_FINDER);
	}

	public static String getEmail(char ch) {
		return ADDED_USER_EMAIL1 + ch
				+ SearchRestAPIUtils.getExecId(Purpose.PEOPLE_FINDER)
				+ ADDED_USER_EMAIL2;
	}

	private Profile getUserProfile(String email) throws FileNotFoundException,
			IOException {

		Profile profile = profilesAdminService.getFirstProfile(email, null,
				null, null);
		System.out.println(profile);
		return profile;
	}

	public void createUsersForPeopleFinder() throws Exception {

		// Create profiles without tags
		try {
			addJobResp();
			createUserProfiles();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		addTags();

	}
}
