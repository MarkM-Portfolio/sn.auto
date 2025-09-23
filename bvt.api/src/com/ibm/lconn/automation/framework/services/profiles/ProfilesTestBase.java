package com.ibm.lconn.automation.framework.services.profiles;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import com.ibm.json.java.JSONObject;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.ProfileFormat;
import com.ibm.lconn.automation.framework.services.common.StringConstants.ProfileOutput;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Connection;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Profile;
import com.ibm.lconn.automation.framework.services.profiles.nodes.ProfilePerspective;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Status;
import com.ibm.lconn.automation.framework.services.profiles.nodes.TagsEntry;
import com.ibm.lconn.automation.framework.services.profiles.nodes.VCardEntry;
import com.ibm.lconn.automation.framework.services.ublogs.UblogsService;

public abstract class ProfilesTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(ProfilesTestBase.class.getName());

	static ServiceConfig config;

	static UserPerspective user, imUser, otherUser, visitor, extendedEmployee, adminUser;

	static ProfilesService service, visitorService, extendedEmpService, adminUserService;

	static boolean useSSL = true;
	
	@Test
	public void getJsonProfileKeyByLoginId() {
		LOGGER.debug("START TEST: getJsonProfileKeyByLoginId()");
		//look up by login name 'ajones'
		String url1 = service.getServiceURLString() + "/json/profileKey.do?login=" + StringConstants.USER_LOGIN_1;
		String jsonResponse1 = service.getResponseString (url1);
		String url2 = service.getServiceURLString() + "/json/profileKey.do?login=" + StringConstants.USER_LOGIN_2;
		String jsonResponse2 = service.getResponseString (url2);
		try {
			JSONObject obj = JSONObject.parse(jsonResponse1);
	
			// 'profile_key' is a 36 character string
			assertTrue(obj.get("profile_key").toString().length() == 36);
			// Current user profile email ends with 'iris.com'
			assertTrue(obj.get("email").toString().endsWith("iris.com"));
			// validate first name and last name
			assertTrue(obj.get("firstName").toString().startsWith("Amy"));
			assertTrue(obj.get("lastName").toString().startsWith("Jones"));
			assertTrue(obj.get("displayName").toString().startsWith("Amy Jones"));
			
			
			obj = JSONObject.parse(jsonResponse2);
			
			// 'profile_key' is a 36 character string
			assertTrue(obj.get("profile_key").toString().length() == 36);
			// Current user profile email ends with 'iris.com'
			assertTrue(obj.get("email").toString().endsWith("iris.com"));
			// validate first name and last name
			assertTrue(obj.get("firstName").toString().startsWith("Amy"));
			assertTrue(obj.get("lastName").toString().startsWith("Jones"));
			assertTrue(obj.get("displayName").toString().startsWith("Amy Jones"));
			
		} catch (Exception e) {
			// Invalid HTTP JSON response
			LOGGER.debug("getJsonProfileKeyByLoginId() - Failed to parse JSON response.");
		}
		LOGGER.debug("COMPLETED TEST: getJsonProfileKeyByLoginId()");
	}

	@Test
	public void getMyProfile() {
		LOGGER.debug("START TEST: getMyProfile()");
		VCardEntry vCard = service.getUserVCard();
		assert (vCard != null);

		LOGGER.debug("Getting Profile Perma Page:");
		LinkedHashMap<String, String> maps = vCard.getVCardFields();
		String uid = maps.get("X_LCONN_USERID");
		service.getPermaLink(uid);
		assertEquals(200, service.getRespStatus());

		// defect73857 legacy Profiles board API test
		// Legacy News APIs are disabled on SC, skipping this next test.
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("TEST: get legacy BoardMessages()");
			Feed boardMessages = (Feed) service.getLegacyBoardMessages(uid);
			assertEquals("Get Legacy Board Messages: headers " + service.getDetail(), 200, service.getRespStatus());
			// /System.out.println(boardMessages.toString());
			assert (boardMessages != null);
		}

		// VModel check
		Feed userProfile = (Feed) service.getUserProfile();
		List<Person> listPerson = userProfile.getEntries().get(0)
				.getContributors();
		assertNotNull("There is no contributors in response", listPerson);
		for (Person person : listPerson) {
			String isExternal = person
					.getSimpleExtension(StringConstants.SNX_ISEXTERNAL);
			if (isExternal == null || isExternal.isEmpty()) {
				assertTrue("No element snx:IsExternal in author", false);
			} else {
				assertEquals("isExternal", "false", isExternal);
				LOGGER.debug("Getting isExternal = false ");
			}
		}

		if (StringConstants.VMODEL_ENABLED) {
			userProfile = (Feed) visitorService.getUserProfile();
			listPerson = userProfile.getEntries().get(0).getContributors();
			assertNotNull("There is no contributors in response", listPerson);
			for (Person person : listPerson) {
				String isExternal = person
						.getSimpleExtension(StringConstants.SNX_ISEXTERNAL);
				if (isExternal == null || isExternal.isEmpty()) {
					assertTrue("No element snx:IsExternal in author", false);
				} else {
					assertEquals("isExternal", "true", isExternal);
					LOGGER.debug("Getting isExternal = true ");
				}
			}

			vCard = visitorService.getUserVCard();
			assertEquals(200, visitorService.getRespStatus());
		}
		LOGGER.debug("END TEST: getMyProfile()");
	}

	// For story 35415
	@Test
	public void checkGenerator() {
		LOGGER.debug("TEST: checkGenerator()");
		String gen = service.getGenerator();
		assertTrue(gen.compareTo("IBM Connections - Profiles") == 0);
	}

	@Test
	public void searchGroupProfiles() throws UnsupportedEncodingException {
		LOGGER.debug("TEST: search group Profiles");
		// should verify name search searches all columns:
		// first, last name from the SURNAME and GIVEN_NAME tables, as well as
		// all names columns in the EMPLOYEE table,
		// including these columns:
		// PROF_PREFERRED_FIRST_NAME
		// PROF_PREFERRED_LAST_NAME
		// PROF_NATIVE_FIRST_NAME
		// PROF_NATIVE_LAST_NAME
		// PROF_ALTERNATE_LAST_NAME
		// PROF_DISPLAY_NAME
		ArrayList<Profile> profiles = service.getAllProfiles(
				ProfileOutput.VCARD, false, null, null, null, null, null, null,
				URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"), null,
				0, 0, null, null, null, null, null, null);
		for (Profile profile : profiles) {
			// /System.out.println(profile.getTitle());
			assert (profile != null);
		}
	}

	// for Defect 57088
	@Test
	public void searchAllProfilesPercent() throws UnsupportedEncodingException {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("TEST: searchAllProfilesPercent()");
			int totalResults = service.getSearchTotalResults(
					ProfileOutput.VCARD, false, null, null, null, null, null,
					null, "%", null, 0, 0, null, null, null, null, null, null);

			assertTrue(totalResults == 0);
		}
	}

	@Test
	public void updateMyProfile() {
		LOGGER.debug("BEGIN TEST: updateMyProfile()");
		VCardEntry vCard = service.getUserVCard();
		assert (vCard != null);
		vCard.setGroupwareEmail("api_test@us.ibm.com");
		vCard.setTelephoneNumber("1-978-399-0000");

		boolean test = service.updateProfile(vCard);

		assertTrue(test);
		LOGGER.debug("END TEST: updateMyProfile()");
	}

	@Test
	public void updateMyProfileByVisitor() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("TEST: updateMyProfile by visitor");
			VCardEntry vCard = visitorService.getUserVCard();
			assert (vCard != null);
			vCard.setGroupwareEmail("wangpin@us.ibm.com");
			vCard.setTelephoneNumber("1-978-399-0000");

			visitorService.updateProfile(vCard);
			assertEquals("Visitor update Profiles", 200,
					visitorService.getRespStatus());
			LinkedHashMap<String, String> map = vCard.getVCardFields();
			assertEquals("vCard updated GroupwareEmail", "wangpin@us.ibm.com",
					map.get("EMAIL;X_GROUPWARE_MAIL"));
		}
	}

	// For Story 56979
	@Test
	public void getProfileType() {
		boolean success = false;
		LOGGER.debug("TEST: getProfileType()");
		ExtensibleElement profileType = service.getProfileType("default");
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			/*
			 * See defect RTC 130082: On SC the correct return code is HTTP 502.
			 */
			assertEquals("Response status should be HTTP 501", true,
					service.getRespStatus() == 501);
		} else {
			if (profileType != null) {

				String childText = profileType.getFirstChild().getText();
				// LOGGER.debug(childText);
				if (childText.equals("snx:person")) {
					// test for invalid (expect failure)
					profileType = service.getProfileType("invalid");
					if (profileType != null) {
						childText = profileType.getFirstChild().getText();
						// LOGGER.debug(childText);
						if (childText.equals("404"))
							success = true;
					}
				}
			}
			assertTrue(success);
		}
	}

	@Test
	// Updated to check if the status was posted correctly
	public void setProfileStatus() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("TEST: setProfileStatus()");
			Status newStatus = new Status("This is a test status update!");
			service.setProfileStatus(newStatus);
			Entry status = (Entry) service.getProfileStatus();
			if (status.getTitle().equals(newStatus.getContent())) {
				assertTrue(true);
				LOGGER.debug("Test Successful: Status was made");
			} else {
				LOGGER.debug("Test failed: Status was not made");
				assertTrue(false);
			}
		}
	}

	@Test
	public void getProfileStatus() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("TEST: getProfileStatus()");
			Entry statusEntry = (Entry) service.getProfileStatus();
			assertTrue(statusEntry != null);
		}
	}

	@Test
	public void testProfileTagsLinkAndExtension()
			throws UnsupportedEncodingException {
		LOGGER.debug("TEST: getProfileTags()");
		ArrayList<Profile> profiles = service.getAllProfiles(
				ProfileOutput.VCARD, false, null, null, null, null,
				ProfileFormat.FULL, null,
				URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"), null,
				0, 0, null, null, null, null, null, null);
		int randomProfileNum = RandomUtils.nextInt(profiles.size());
		Person user = profiles.get(randomProfileNum).getContributors().get(0);
		VCardEntry vcard = new VCardEntry(profiles.get(randomProfileNum)
				.getContent(), null);
		Categories tagsResult;

		// Use email
		if (!config.isEmailHidden())
			tagsResult = service.getProfileTags(user.getEmail(), null);
		// Use key
		else
			tagsResult = service.getProfileTags(null, vcard.getVCardFields()
					.get(StringConstants.VCARD_PROFILE_KEY));
		// /System.out.println("Tags for: " + user.getName() + "\n" +
		// tagsResult.toString());
		assert (tagsResult != null);

		LOGGER.debug("TEST: setProfileTags()");
		VCardEntry vCard = service.getUserVCard();
		Categories tags = service.getProfileTags(
				StringConstants.ADMIN_USER_EMAIL, null);

		TagsEntry test = new TagsEntry(tags);
		test.addTag("test");
		test.addTag("hello");
		test.addTag("newTag tagProfiles_"
				+ Utils.logDateFormatter.format(new Date()));

		if (!config.isEmailHidden()) {
			String profileEmail = vCard.getVCardFields().get(
					StringConstants.VCARD_EMAIL);
			assertTrue(service.setProfileTags(test, profileEmail, null,
					profileEmail, null) != null);
		} else {
			String profileKey = vCard.getVCardFields().get(
					StringConstants.VCARD_PROFILE_KEY);
			assertTrue(service.setProfileTags(test, null, profileKey, null,
					profileKey) != null);
		}

		// for https://swgjazz.ibm.com:8004/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/213233
		if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD){ 
			LOGGER.debug("TEST: profile link and extension");
			Feed profile = (Feed) service.getUserProfile();
			assertEquals("Get its own Profile", 200, service.getRespStatus());
	
			// Fix for : https://jira.cwp.pnp-hcl.com/browse/AUTOMATION-13
			String extensionUrl = null;
			List<Link> extensionUrls = profile.getLinks("http://www.ibm.com/xmlns/prod/sn/ext-attr");
			for (Link extension : extensionUrls){
				if ( extension.toString().contains("profileLinks") )
				     extensionUrl = extension.getAttributeValue("href");
			}
			
			String link = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<linkroll xmlns=\"http://www.ibm.com/xmlns/prod/sn/profiles/ext/profile-links\" xmlns:snx=\"http://www.ibm.com/xmlns/prod/sn/profiles/ext/profile-links\">"
					+ "<link name=\"atom spec\" url=\"http://www.ietf.org/rfc/rfc4287\" /></linkroll>";
	
			ClientResponse response = service.putResponse(extensionUrl, link);
			assertEquals("Add link", 204, response.getStatus());
	
			String result = service.getResponseString(extensionUrl);
			assertEquals("Get link", 200, service.getRespStatus());
			assertEquals("Get link value", true,result.contains("http://www.ietf.org/rfc/rfc4287"));
		}

	}

	@Test
	public void testProfileTagsLinkAndExtensionByVisitor()
			throws UnsupportedEncodingException {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("TEST: GetProfiles by Visitor");
			ArrayList<Profile> profiles = visitorService.getAllProfiles(
					ProfileOutput.VCARD, false, null, null, null, null,
					ProfileFormat.FULL, null,
					URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"),
					null, 0, 0, null, null, null, null, null, null);
			assertEquals("Visitor can't get Profiles", null, profiles);

			LOGGER.debug("TEST: Profile Link and Extension by Visitor");
			Feed profile = (Feed) visitorService.getUserProfile();
			assertEquals("Visitor get its own Profile", 200,
					visitorService.getRespStatus());

			String extensionUrl = profile.getLinkResolvedHref(
					"http://www.ibm.com/xmlns/prod/sn/ext-attr").toString();
			String link = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<linkroll xmlns=\"http://www.ibm.com/xmlns/prod/sn/profiles/ext/profile-links\" xmlns:snx=\"http://www.ibm.com/xmlns/prod/sn/profiles/ext/profile-links\">"
					+ "<link name=\"atom spec\" url=\"http://www.ietf.org/rfc/rfc4287\" /></linkroll>";

			ClientResponse response = visitorService.putResponse(extensionUrl,
					link);
			assertEquals("Add link", 204, response.getStatus());

			String result = visitorService.getResponseString(extensionUrl);
			assertEquals("Get link", 200, visitorService.getRespStatus());
			assertEquals("Get link value", true,
					result.contains("http://www.ietf.org/rfc/rfc4287"));

			LOGGER.debug("TEST: getProfileTags by Visitor");
			VCardEntry vcard = visitorService.getUserVCard();
			assertEquals("Visitor get vcard", 200,
					visitorService.getRespStatus());

			Categories tagsResult;
			// Use email
			if (!config.isEmailHidden())
				tagsResult = visitorService.getProfileTags(
						StringConstants.EXTERNAL_USER_EMAIL, null);
			// Use key
			else
				tagsResult = visitorService.getProfileTags(null, vcard
						.getVCardFields()
						.get(StringConstants.VCARD_PROFILE_KEY));
			// /System.out.println("Tags for: " + user.getName() + "\n" +
			// tagsResult.toString());
			assertEquals("Visitor get Profiles Tags", 200,
					visitorService.getRespStatus());
			assert (tagsResult != null);

			LOGGER.debug("TEST: setProfileTags by Visitor");
			VCardEntry vCard = visitorService.getUserVCard();
			Categories tags = visitorService.getProfileTags(
					StringConstants.ADMIN_USER_EMAIL, null);

			TagsEntry test = new TagsEntry(tags);
			test.addTag("test");
			test.addTag("hello");
			test.addTag("newTag tagProfiles_"
					+ Utils.logDateFormatter.format(new Date()));

			if (!config.isEmailHidden()) {
				String profileEmail = vCard.getVCardFields().get(
						StringConstants.VCARD_EMAIL);
				assertTrue(visitorService.setProfileTags(test, profileEmail,
						null, profileEmail, null) != null);
			} else {
				String profileKey = vCard.getVCardFields().get(
						StringConstants.VCARD_PROFILE_KEY);
				assertTrue(visitorService.setProfileTags(test, null,
						profileKey, null, profileKey) != null);
			}
		}

	}

	// TODO @Test
	public void setTagForBVTTest() throws FileNotFoundException, IOException {
		LOGGER.debug("TEST: setTagForBVTTest()");
		TagsEntry test = new TagsEntry("profilessearchtag1234");

		ProfilePerspective profile=null;
		try {
			profile = new ProfilePerspective(0, useSSL);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// ProfileData profile = ProfileLoader.getProfile(0);
		String profileEmail = profile.getEmail();
		String profileKey = profile.getKey();

		if (!config.isEmailHidden()) {
			assertTrue(service.setProfileTags(test, profileEmail, null,
					StringConstants.USER_EMAIL, null) != null);
		} else {
			assertTrue(service.setProfileTags(test, null, profileKey, null,
					profileKey) != null);
		}
	}

	//slated for removal - this api is part of the profiles suite 
	/*@Test
	public void getBoardMessages() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("TEST: getBoardMessages()");
			Feed boardMessages = getBoardMessageFeed();
			// /System.out.println(boardMessages.toString());
			assert (boardMessages != null);
		}
	}

	@Test
	public void addBoardMessages() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("TEST: addBoardMessages()");
			Message newMessage = new Message(
					"This is my second post to the profile message board.");
			Message newMessage1 = new Message(
					"This is my third post to the profile message board.");
			Message newMessage2 = new Message(
					"This is my forth post to the profile message board.");

			assertTrue(service.addBoardMessage(newMessage) != null);
			assertTrue(service.addBoardMessage(newMessage1) != null);
			assertTrue(service.addBoardMessage(newMessage2) != null);
		}
	}

	// defect 69883
	@Test
	public void testBoardMessages() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("TEST: testBoardMessages()");
			String testSt1 = "test1..<img src=\"\"></img>";
			String testSt2 = "http://www.google.com";

			Message newMessage1 = new Message(testSt1);
			Message newMessage2 = new Message(testSt2);

			assertTrue(service.addBoardMessage(newMessage1) != null);
			assertTrue(service.addBoardMessage(newMessage2) != null);

			Feed boardMessages = getBoardMessageFeed();
			for (Entry e : boardMessages.getEntries()) {
				String title = e.getTitle();
				if (title.contains("test1..")) {
					assertTrue(title.equalsIgnoreCase(testSt1));
				}
			}
		}
	}

	private Feed getBoardMessageFeed() {
		ExtensibleElement boardMessages = service.getBoardMessages();
		assertEquals("getBoardMessage", 200, service.getRespStatus());
		return (Feed) boardMessages;
	}

	@Test
	public void addBoardReply() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("TEST: addBoardReply()");
			Feed boardMessages = getBoardMessageFeed();

			for (Entry entry : boardMessages.getEntries()) {
				String replyLink = entry.getLink("replies").getHref()
						.toString();
				Comment newComment = new Comment(
						"This is a test comment that will be added to all messages.");
				assertTrue(service.addBoardMessageReply(newComment, replyLink) != null);
			}
		}
	}

	@Test
	public void addStatusReply() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("TEST: addStatusReply()");
			Entry entry = (Entry) service.getProfileStatus();
			String replyLink = entry.getLink("replies").getHref().toString();
			Comment newComment = new Comment(
					"This is a test comment that will be added to the users current status.");
			assertTrue(service.addBoardMessageReply(newComment, replyLink) != null);
		}
	}
	*/

	@Test
	public void inviteColleague() throws FileNotFoundException, IOException {
		LOGGER.debug("TEST: inviteColleague()");
		// ArrayList<Profile> profiles =
		// service.getAllProfiles(ProfileOutput.VCARD, false, null, null, null,
		// null, ProfileFormat.FULL, null,
		// URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"), null, 0, 0,
		// null, null, null, null, null, null);
		// int profileIndex = RandomUtils.nextInt(profiles.size());

		// SES ticket 273887 - Ajones100 is broken due to LDAP changes.
		// Temporarily switching to ajones101
		// ProfilePerspective profile2 = new ProfilePerspective (1, useSSL);
		ProfilePerspective profile2=null;
		try {
			profile2 = new ProfilePerspective(6, useSSL);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Connection newConnection = new Connection(
				"Please be my new colleague connection!");

		String currentEmail = null;
		String targetEmail = null;
		String currentKey = null;
		String targetKey = null;

		if (!config.isEmailHidden()) {
			// targetEmail =
			// profiles.get(profileIndex).getContributors().get(0).getEmail();
			targetEmail = profile2.getEmail();
			currentEmail = StringConstants.USER_EMAIL;
		} else {
			// VCardEntry targetUserCard = new
			// VCardEntry(profiles.get(profileIndex).getContent(), null);
			VCardEntry currentUserCard = service.getUserVCard();

			// targetKey =
			// targetUserCard.getVCardFields().get(StringConstants.VCARD_PROFILE_KEY);
			targetKey = profile2.getKey();
			currentKey = currentUserCard.getVCardFields().get(
					StringConstants.VCARD_PROFILE_KEY);
		}

		Entry colleagueStatusEntry = (Entry) service.checkColleagueStatus(
				currentEmail, currentKey, targetEmail, targetKey);

		if (colleagueStatusEntry.getAttributeValue(StringConstants.API_ERROR) != null) {
			assertTrue(service.inviteColleague(newConnection, targetEmail,
					targetKey) != null);
			// Make sure to check again for the status so tests below still work
			colleagueStatusEntry = (Entry) service.checkColleagueStatus(
					currentEmail, currentKey, targetEmail, targetKey);
		} else {
			String status = colleagueStatusEntry
					.getCategories(StringConstants.SCHEME_STATUS).get(0)
					.getTerm();
			LOGGER.warn("Colleague Connection exists - status: " + status);
		}

		// content should be empty here - we should not be able to see the
		// connection request
		Profile profilesSelf = service.getSelfFeed(colleagueStatusEntry);
		Profile profilesEdit = service.getEditFeed(colleagueStatusEntry);
		assertTrue(profilesSelf.getContent().isEmpty());
		assertTrue(profilesEdit.getContent().isEmpty());

		// Create another instance of service as the user we're asking to be our
		// colleague
		/*
		 * Abdera abdera2 = new Abdera(); AbderaClient client2 = new
		 * AbderaClient(abdera2); ServiceConfig config2 = new
		 * ServiceConfig(client2, URLConstants.SERVER_URL, useSSL); ServiceEntry
		 * profiles2 = config2.getService("profiles"); String sUser = null; if
		 * (targetEmail == null) { String sEmail =
		 * profiles.get(profileIndex).getContributors().get(0).getEmail(); sUser
		 * = sEmail.substring(0, sEmail.indexOf('@')); } else sUser =
		 * targetEmail.substring(0, targetEmail.indexOf('@')); String sPW =
		 * sUser.substring(1); try { Utils.addServiceCredentials(profiles2,
		 * client2,sUser,sPW); } catch (URISyntaxException e) {
		 * assertTrue(true); return; } ProfilesService service2 = new
		 * ProfilesService(client2, profiles2);
		 */

		Entry myStatusEntry = (Entry) profile2.getService()
				.checkColleagueStatus(targetEmail, targetKey, currentEmail,
						currentKey);
		Profile profilesTheirSelf = profile2.getService().getSelfFeed(
				myStatusEntry);
		Profile profilesTheirEdit = profile2.getService().getEditFeed(
				myStatusEntry);
		// Self should be empty because it does not have inclMessage
		assertTrue(profilesTheirSelf.getContent().isEmpty());
		// Edit does however and should have our request content
		assertTrue(!profilesTheirEdit.getContent().isEmpty());

	}

	@Test
	public void searchHCARD() throws UnsupportedEncodingException {
		LOGGER.debug("TEST: searchHCARD()");

		boolean success = false;

		// Test HCARD format, and explicitly check VCARD even though other tests
		// assume it
		ArrayList<Profile> profiles = service.getAllProfiles(
				ProfileOutput.VCARD, false, null, null, null, null, null, null,
				URLEncoder.encode(StringConstants.RANDOM1_USER_REALNAME,
						"UTF-8"), null, 0, 0, null, null, null, null, null,
				null);
		for (Profile profile : profiles) {
			String con = profile.getContent();
			if (con.toUpperCase().contains("BEGIN:VCARD")) {
				success = true;
			}
		}
		profiles = service.getAllProfiles(ProfileOutput.HCARD, false, null,
				null, null, null, null, null, URLEncoder.encode(
						StringConstants.RANDOM1_USER_REALNAME, "UTF-8"), null,
				0, 0, null, null, null, null, null, null);
		for (Profile profile : profiles) {
			String con = profile.getContent();
			if (!con.toLowerCase().contains("class=\"vcard\"")) {
				success = false;
			}
		}
		assertTrue(success);
	}

	@Test
	/*
	 * Somewhat risky test as there is no guarantee that the Vcard will have
	 * populated data.
	 */
	public void searchProfileFormat() throws UnsupportedEncodingException {
		LOGGER.debug("TEST: searchProfileFormat()");
		boolean foundInFull = false;
		boolean foundInLite = false;

		ArrayList<Profile> profiles = service.getAllProfiles(
				ProfileOutput.VCARD, false, null, null, null, null,
				ProfileFormat.FULL, null,
				URLEncoder.encode(StringConstants.USER_REALNAME, "UTF-8"),
				null, 0, 0, null, null, null, null, null, null);
		for (Profile profile : profiles) {
			VCardEntry vcard = service.vcardFromProfile(profile);
			String phoneNumber = vcard.getVCardFields().get(
					StringConstants.VCARD_TELEPHONE_NUMBER);
			if ((phoneNumber != null) && (!phoneNumber.isEmpty()))
				foundInFull = true;
		}

		// Test the Lite version of the card for the same data.
		profiles = service.getAllProfiles(ProfileOutput.VCARD, false, null,
				null, null, null, ProfileFormat.LITE, null,
				URLEncoder.encode(StringConstants.USER_REALNAME, "UTF-8"),
				null, 0, 0, null, null, null, null, null, null);
		for (Profile profile : profiles) {
			VCardEntry vcard = service.vcardFromProfile(profile);
			String phoneNumber = vcard.getVCardFields().get(
					StringConstants.VCARD_TELEPHONE_NUMBER);
			if ((phoneNumber != null) && (!phoneNumber.isEmpty()))
				foundInLite = true;
		}
		assertEquals(
				"Telephone number value not found in both Full and Lite version of Vcard",
				true, foundInLite && foundInFull);
	}

	// TODO @Test
	public void searchEmail() throws UnsupportedEncodingException {
		LOGGER.debug("TEST: searchEmail()");
		boolean success = false;

		ArrayList<Profile> profiles = service.getAllProfiles(
				ProfileOutput.VCARD, false, null, null, null,
				URLEncoder.encode(StringConstants.RANDOM1_USER_EMAIL, "UTF-8"),
				null, null, null, null, 0, 0, null, null, null, null, null,
				null);
		if (profiles.size() > 0)
			success = true;
		assertTrue(success);
	}

	@Test
	public void searchPaging() throws UnsupportedEncodingException {
		LOGGER.debug("TEST: searchPaging()");
		boolean success = true;

		// keep track of the first page of results
		int pageSize = 5;
		int minimumSize = 10;
		String firstResults[] = new String[pageSize];
		
		ArrayList<Profile> profileCheck = service.getAllProfiles(
				ProfileOutput.VCARD, false, null, null, null, null, null, null,
				URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"), null,
				1, 10, null, null, null, null, null, null);

		// TJB 5/4/16 This test requires a user set of 10 or more users.  
		// Sandbox SC may not have 10 users.  Check user count first.
		if (profileCheck.size() >= minimumSize) {
			ArrayList<Profile> profiles = service.getAllProfiles(
					ProfileOutput.VCARD, false, null, null, null, null, null, null,
					URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"), null,
					1, pageSize, null, null, null, null, null, null);
			// verify we only got 5 entries
			if (profiles.size() != 5)
				success = false;
			int index = 0;
			for (Profile profile : profiles) {
				firstResults[index] = profile.getTitle();
			}

			profiles = service.getAllProfiles(ProfileOutput.VCARD, false, null,
					null, null, null, null, null,
					URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"), null,
					2, pageSize, null, null, null, null, null, null);
			// verify we only got 5 (different) entries
			if (profiles.size() != 5)
				success = false;
			for (Profile profile : profiles) {
				String thisOne = profile.getTitle();
				for (index = 0; index < pageSize; ++index) {
					if (thisOne.equals(firstResults[index]))
						success = false;
				}
			}
			assertTrue(success);			
			
		} else {
			LOGGER.debug("Skipping test as the current org does not have enough users.");
		}

	}

	// TODO @Test
	public void searchPhoneNumber() throws UnsupportedEncodingException {
		LOGGER.debug("TEST: searchPhoneNumber()");
		boolean success = true;

		// Telephone Number
		ArrayList<Profile> profiles = service.getAllProfiles(
				ProfileOutput.VCARD, false, null, null, null, null, null, null,
				null, null, 0, 0, URLEncoder.encode("1-978-399-0000", "UTF-8"),
				null, null, null, null, null);
		if (profiles.size() == 0)
			success = false;

		// Fax Number - hard-coded work on BVT user
		/*
		 * profiles = service.getAllProfiles(ProfileOutput.VCARD, false, null,
		 * null, null, null,null, null, null,null, 0, 0,
		 * URLEncoder.encode("1-978-399-1111", "UTF-8"), null, null, null, null,
		 * null); if (profiles.size() == 0) success = false;
		 */

		/*
		 * these need data added to the profile to pass // Mobile Number
		 * profiles = service.getAllProfiles(ProfileOutput.VCARD, false, null,
		 * null, null,null, null, null,null, 0, 0,
		 * URLEncoder.encode("1-978-399-2222", "UTF-8"), null, null, null, null,
		 * null); if (profiles.size() == 0) success = false;
		 * 
		 * // Pager Number profiles =
		 * service.getAllProfiles(ProfileOutput.VCARD, false, null, null, null,
		 * null,null, null, null,null, 0, 0, URLEncoder.encode("1-978-399-3333",
		 * "UTF-8"), null, null, null, null, null); if (profiles.size() == 0)
		 * success = false;
		 */

		assertTrue(success);
	}

	// @Test Tag need serch index, move to search population
	public void searchProfileTags() throws UnsupportedEncodingException {
		LOGGER.debug("TEST: searchProfileTags()");
		boolean success = false;

		ArrayList<Profile> profiles = service.getAllProfiles(
				ProfileOutput.VCARD, false, null, null, null, null, null, null,
				null, null, 0, 0, null,
				URLEncoder.encode("test,hello", "UTF-8"), null, null, null,
				null);
		if (profiles.size() > 0)
			success = true;
		assertTrue(success);
	}

	// TODO @Test
	public void searchTextSearch() throws UnsupportedEncodingException {
		LOGGER.debug("TEST: searchTextSearch()");
		boolean success = false;

		// Well-formed full text search query.
		// Performs a text search of the Profile Tags, About Me, and Background
		// fields of all the profiles
		ArrayList<Profile> profiles = service.getAllProfiles(
				ProfileOutput.VCARD, false, null, null, null, null, null, null,
				null, null, 0, 0, null, null, null,
				URLEncoder.encode("interested", "UTF-8"), null, null);
		if (profiles.size() > 0)
			success = true;
		assertTrue(success);
	}

	@Test
	public void searchUserID() throws UnsupportedEncodingException {
		LOGGER.debug("TEST: searchUserID()");
		boolean success = false;

		ArrayList<Profile> profiles = service.getAllProfiles(
				ProfileOutput.VCARD, false, null, null, null, null,
				ProfileFormat.FULL, null,
				URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"), null,
				0, 1, null, null, null, null, null, null);
		Profile p = null;
		for (Profile profile : profiles) {
			LOGGER.debug(profile.getTitle());
			p = profile;
			break;
		}

		if (p != null) {

			VCardEntry vCard = service.vcardFromProfile(p);
			LinkedHashMap<String, String> maps = vCard.getVCardFields();
			String uid = maps.get("X_LCONN_USERID");

			profiles = service.getAllProfiles(ProfileOutput.VCARD, false, null,
					null, null, null, null, null, null, null, 0, 0, null, null,
					null, null, uid, null);
			if (profiles.size() > 0)
				success = true;
			assertTrue(success);
		}
	}

	@Test
	public void searchInvalid() throws UnsupportedEncodingException {
		LOGGER.debug("TEST: searchTextSearch()");
		boolean success = false;

		// invalid will return 'Invalid request' UNLESS other valid parameters
		// are provided
		ArrayList<Profile> profiles = service.getAllProfiles(
				ProfileOutput.VCARD, false, null, null, null, null, null, null,
				URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"), null,
				0, 0, null, null, null, null, null,
				URLEncoder.encode("invalid", "UTF-8"));
		if (profiles.size() > 0)
			success = true;

		profiles = service.getAllProfiles(ProfileOutput.VCARD, false, null,
				null, null, null, null, null, null, null, 0, 0, null, null,
				null, null, null, URLEncoder.encode("invalid", "UTF-8"));
		if (profiles.size() > 0)
			success = false;
		assertTrue(success);
	}

	/*
	 * these next four should pass, however they return no results on our
	 * servers will debug further in the future
	 * 
	 * @Test public void searchCity() throws UnsupportedEncodingException {
	 * LOGGER.debug("TEST: searchCity()"); boolean success = false;
	 * 
	 * ArrayList<Profile> profiles = service.getAllProfiles(ProfileOutput.VCARD,
	 * false,URLEncoder.encode("Westford", "UTF-8"), null, null, null, null,
	 * null, null, null, 0, 0, null, null, null, null, null, null); if
	 * (profiles.size() > 0) success = true; assertTrue(success); }
	 * 
	 * @Test public void searchCountry() throws UnsupportedEncodingException {
	 * LOGGER.debug("TEST: searchCountry()"); boolean success = false;
	 * 
	 * ArrayList<Profile> profiles = service.getAllProfiles(ProfileOutput.VCARD,
	 * false, null, null, URLEncoder.encode("US", "UTF-8"),null, null, null,
	 * null, null, 0, 0, null, null, null, null, null, null); if
	 * (profiles.size() > 0) success = true; assertTrue(success); }
	 * 
	 * @Test public void searchJobTitle() throws UnsupportedEncodingException {
	 * LOGGER.debug("TEST: searchJobTitle()"); boolean success = false;
	 * ArrayList<Profile> profiles = service.getAllProfiles(ProfileOutput.VCARD,
	 * false, null, null, null, null,null, URLEncoder.encode("IBM Employee",
	 * "UTF-8"),null, null, 0, 0, null, null, null, null, null, null); if
	 * (profiles.size() > 0) success = true; assertTrue(success); }
	 * 
	 * @Test public void searchOrganization() throws
	 * UnsupportedEncodingException { LOGGER.debug("TEST: searchOrganization()");
	 * boolean success = false;
	 * 
	 * ArrayList<Profile> profiles = service.getAllProfiles(ProfileOutput.VCARD,
	 * false, null, null, null, null,null, null,
	 * null,URLEncoder.encode("IBM Software Group", "UTF-8"), 0, 0, null, null,
	 * null, null, null, null); if (profiles.size() > 0) success = true;
	 * assertTrue(success); }
	 */

	// for defect 56137
	// TODO @Test
	public void checkPeopleManaged() throws UnsupportedEncodingException {
		boolean success = true;

		LOGGER.debug("TEST: checkPeopleManaged()");

		String peopleManagedAPI = service.getPeopleManagedAPI(URLEncoder
				.encode(StringConstants.ADMIN_USER_REALNAME, "UTF-8"));
		if (peopleManagedAPI == null) {
			success = false;
		} else {

			// keep track of the first page of results
			int pageSize = 5;
			String firstResults[] = new String[pageSize];

			ArrayList<Profile> profiles = service.getPeopleManaged(
					peopleManagedAPI, 1, pageSize);
			// verify we only got 5 entries
			if (profiles.size() != 5)
				success = false;
			int index = 0;
			for (Profile profile : profiles) {
				firstResults[index] = profile.getTitle();
			}

			profiles = service.getPeopleManaged(peopleManagedAPI, 2, pageSize);
			// verify we only got 5 (different) entries
			if (profiles.size() != 5)
				success = false;
			for (Profile profile : profiles) {
				String thisOne = profile.getTitle();
				for (index = 0; index < pageSize; ++index) {
					if (thisOne.equals(firstResults[index]))
						success = false;
				}
			}
		}
		assertTrue(success);
	}

	@Test
	public void findingColleaguesShared() throws FileNotFoundException,
			IOException, InterruptedException {

		LOGGER.debug("TEST: Finding Shared Colleague between two Profiles");

		// Data for user 1 (current user) ------------------------------
		String user1Email = StringConstants.USER_EMAIL;
		VCardEntry user1Card = service.getUserVCard();
		String user1Key = user1Card.getVCardFields().get(
				StringConstants.VCARD_PROFILE_KEY);
		String user1Name = user1Card.getVCardFields().get(
				StringConstants.VCARD_FULL_NAME);

		// Data for user 2 (alternate user) -----------------------------
		int profileIndex = 5;
		boolean profileFound = false;
		ProfilePerspective user2 = null;
		while (!profileFound) {
			// some how any jones 100 had issue, used too much??
			try {
				user2 = new ProfilePerspective(profileIndex, useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Checks the user does not have a pending network invite
			// If a pending invite exists, moves to the next profile
			Entry colleagueStatusEntry = (Entry) service.checkColleagueStatus(
					null, user1Key, null, user2.getKey());
			if (colleagueStatusEntry
					.getAttributeValue(StringConstants.API_ERROR) != null
					|| colleagueStatusEntry
							.getCategories(StringConstants.SCHEME_STATUS)
							.get(0).getTerm().equals("accepted")) {
				profileFound = true;
			}
			profileIndex++;
		}

		// Data for user 3 (alternate user) -----------------------------
		profileFound = false;
		ProfilePerspective user3 = null;
		while (!profileFound) {
			try {
				user3 = new ProfilePerspective(profileIndex, useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// Checks the user does not have a pending network invite
			// If a pending invite exists, moves to the next profile
			Entry colleagueStatusEntry = (Entry) service.checkColleagueStatus(
					null, user1Key, null, user3.getKey());
			if (colleagueStatusEntry
					.getAttributeValue(StringConstants.API_ERROR) != null
					|| colleagueStatusEntry
							.getCategories(StringConstants.SCHEME_STATUS)
							.get(0).getTerm().equals("accepted")) {
				profileFound = true;
			}
			profileIndex++;
		}

		// Check to ensure that two valid users were found.
		assert (user2 != null && user3 != null);

		// Establish a connection
		// Check if user 1 and user 2 are colleagues
		Entry colleagueStatusEntry = (Entry) service.checkColleagueStatus(null,
				user1Key, null, user2.getKey());
		if (colleagueStatusEntry.getAttributeValue(StringConstants.API_ERROR) != null) {
			LOGGER.warn("No Colleague Connection found --404 Response was sent");
			// Connect user 1 to user 2
			Connection newConnection = new Connection("Connecting 242 to 10x");
			Entry inviteResultEntry = (Entry) service.inviteColleague(
					newConnection, user2.getEmail(), user2.getKey());
			Entry myStatusEntry = (Entry) user2.getService()
					.checkColleagueStatus(user2.getEmail(), user2.getKey(),
							user1Email, user1Key);
			myStatusEntry.getCategories(StringConstants.SCHEME_STATUS).get(0)
					.setTerm("accepted");
			user2.getService().acceptInvite(
					inviteResultEntry.getEditLink().getHref().toString(),
					myStatusEntry);
		} else {
			String status = colleagueStatusEntry
					.getCategories(StringConstants.SCHEME_STATUS).get(0)
					.getTerm();
			LOGGER.warn("Colleague Connection exists - status: " + status);
		}

		// Check if user 1 and user 3 are colleagues
		colleagueStatusEntry = (Entry) service.checkColleagueStatus(null,
				user1Key, null, user3.getKey());
		if (colleagueStatusEntry.getAttributeValue(StringConstants.API_ERROR) != null) {
			LOGGER.warn("No Colleague Connection found --404 Response was sent");
			// Connect user 1 to user 3
			Connection newConnection_2 = new Connection("Connection 242 to 10x");
			Entry inviteResultEntry_2 = (Entry) service.inviteColleague(
					newConnection_2, user3.getEmail(), user3.getKey());
			Entry myStatusEntry_2 = (Entry) user3.getService()
					.checkColleagueStatus(user3.getEmail(), user3.getKey(),
							user1Email, user1Key);
			myStatusEntry_2.getCategories(StringConstants.SCHEME_STATUS).get(0)
					.setTerm("accepted");
			user3.getService().acceptInvite(
					inviteResultEntry_2.getEditLink().getHref().toString(),
					myStatusEntry_2);
		} else {
			String status = colleagueStatusEntry
					.getCategories(StringConstants.SCHEME_STATUS).get(0)
					.getTerm();
			LOGGER.warn("Colleague Connection exists - status: " + status);
		}

		// Checks to see if the currentUser is a mutual contact of the two
		// alternate users.
		Feed sharedConnectionsFeed = (Feed) service.getSharedConnectionsFeed(
				user2.getKey(), user3.getKey());
		// Feed currUserFeed =(Feed) service.getStatus(user1Key);

		boolean sharedColleages = false;
		for (Entry sharedConnectionsEntry : sharedConnectionsFeed.getEntries()) {

			// if(sharedConnectionsEntry.getTitle().equals(currUserFeed.getEntries().get(0).getAuthor().getName())){
			if (sharedConnectionsEntry.getTitle().equals(user1Name)) {
				LOGGER.debug("SHARED COLLEAGUES TEST PASSED");
				sharedColleages = true;
			}
		}
		if (!sharedColleages) {
			LOGGER.debug("TEST FAILED");
		}
		assertTrue(sharedColleages);

	}

	@Test
	public void findingColleaguesSharedInDepth() throws FileNotFoundException,
			IOException {
		LOGGER.debug("BEGINNING IN DEPTH TEST: Finding Shared Colleagues between two accounts");

		LOGGER.debug("Step1 : Get current user info - as Shared user1");
		String sharedUser1Email = StringConstants.USER_EMAIL;
		VCardEntry sharedUser1Card = service.getUserVCard();
		String sharedUser1Key = sharedUser1Card.getVCardFields().get(
				StringConstants.VCARD_PROFILE_KEY);

		LOGGER.debug("Step2 : Remove all current network connections from current user");
		Feed colleagesFeed = (Feed) service.getColleagueFeed(sharedUser1Email,
				sharedUser1Key);
		for (Entry e : colleagesFeed.getEntries()) {
			service.removeContact(e.getEditLink().getHref().toString());
		}

		LOGGER.debug("Step3 : Start user from index5 - as Shared User2");
		int profileIndex = 5;
		ProfilePerspective sharedUser2=null;
		try {
			sharedUser2 = new ProfilePerspective(profileIndex++, useSSL);
		} catch (LCServiceException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		LOGGER.debug("Step4 : Remove all current network connections from user2");
		colleagesFeed = (Feed) service.getColleagueFeed(sharedUser2.getEmail(),
				sharedUser2.getKey());
		for (Entry e : colleagesFeed.getEntries()) {
			sharedUser2.getService().removeContact(
					e.getEditLink().getHref().toString());
		}

		LOGGER.debug("Step5 : Find two available profiles-user3,4 that does not have a pending connection request from Shared User 1 or 2");
		boolean profile3Found = false;
		ProfilePerspective user3 = null;
		while (!profile3Found) {
			try {
				user3 = new ProfilePerspective(profileIndex, useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Entry colleagueStatusEntry1 = (Entry) service.checkColleagueStatus(
					null, sharedUser1Key, null, user3.getKey());
			Entry colleagueStatusEntry2 = (Entry) sharedUser2.getService()
					.checkColleagueStatus(null, sharedUser2.getKey(), null,
							user3.getKey());
			if (colleagueStatusEntry1
					.getAttributeValue(StringConstants.API_ERROR) != null
					&& colleagueStatusEntry2
							.getAttributeValue(StringConstants.API_ERROR) != null) {
				profile3Found = true;
			}
			profileIndex++;
		}

		// Find Another available profile that does not have a pending
		// connection request from shared User 1 or 2
		boolean profile4Found = false;
		ProfilePerspective user4 = null;
		while (!profile4Found) {
			try {
				user4 = new ProfilePerspective(profileIndex, useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Entry colleagueStatusEntry1 = (Entry) service.checkColleagueStatus(
					null, sharedUser1Key, null, user4.getKey());
			Entry colleagueStatusEntry2 = (Entry) sharedUser2.getService()
					.checkColleagueStatus(null, sharedUser2.getKey(), null,
							user4.getKey());
			if (colleagueStatusEntry1
					.getAttributeValue(StringConstants.API_ERROR) != null
					&& colleagueStatusEntry2
							.getAttributeValue(StringConstants.API_ERROR) != null) {
				profile4Found = true;
			}
			profileIndex++;
		}

		// assert that one shared profile and two other profiles were found
		assertTrue(sharedUser2 != null && user3 != null && user4 != null);

		LOGGER.debug("Step6 : Connect shared-user 1 to user 3 and 4");
		Connection newConnection = new Connection("Connect user 1 to user 3");
		Entry inviteResultEntry = (Entry) service.inviteColleague(
				newConnection, user3.getEmail(), user3.getKey());
		Entry myStatusEntry = (Entry) user3.getService().checkColleagueStatus(
				user3.getEmail(), user3.getKey(), sharedUser1Email,
				sharedUser1Key);
		myStatusEntry.getCategories(StringConstants.SCHEME_STATUS).get(0)
				.setTerm("accepted");
		user3.getService().acceptInvite(
				inviteResultEntry.getEditLink().getHref().toString(),
				myStatusEntry);

		newConnection = new Connection("Connect user 1 to user 4");
		inviteResultEntry = (Entry) service.inviteColleague(newConnection,
				user4.getEmail(), user4.getKey());
		myStatusEntry = (Entry) user4.getService().checkColleagueStatus(
				user4.getEmail(), user4.getKey(), sharedUser1Email,
				sharedUser1Key);
		myStatusEntry.getCategories(StringConstants.SCHEME_STATUS).get(0)
				.setTerm("accepted");
		user4.getService().acceptInvite(
				inviteResultEntry.getEditLink().getHref().toString(),
				myStatusEntry);

		LOGGER.debug("Step7 : Connect shared-user2 to user 3 and 4");
		newConnection = new Connection("Connect user 2 to user 3");
		inviteResultEntry = (Entry) sharedUser2.getService().inviteColleague(
				newConnection, user3.getEmail(), user3.getKey());
		myStatusEntry = (Entry) user3.getService().checkColleagueStatus(
				user3.getEmail(), user3.getKey(), sharedUser2.getEmail(),
				sharedUser2.getKey());
		myStatusEntry.getCategories(StringConstants.SCHEME_STATUS).get(0)
				.setTerm("accepted");
		user3.getService().acceptInvite(
				inviteResultEntry.getEditLink().getHref().toString(),
				myStatusEntry);

		newConnection = new Connection("Connect user 2 to user 4");
		inviteResultEntry = (Entry) sharedUser2.getService().inviteColleague(
				newConnection, user4.getEmail(), user4.getKey());
		myStatusEntry = (Entry) user4.getService().checkColleagueStatus(
				user4.getEmail(), user4.getKey(), sharedUser2.getEmail(),
				sharedUser2.getKey());
		myStatusEntry.getCategories(StringConstants.SCHEME_STATUS).get(0)
				.setTerm("accepted");
		user4.getService().acceptInvite(
				inviteResultEntry.getEditLink().getHref().toString(),
				myStatusEntry);

		LOGGER.debug("Step8 : Checks to see if the currentUser is a mutual contact of the two alternate users.");
		String paramsToAppend = "outputType=profile&format=full&sortBy=displayName&sortOrder=asc&output=vcard";
		Feed sharedConnectionsFeed = (Feed) service.getSharedConnectionsFeed(
				user3.getKey(), user4.getKey(), paramsToAppend);

		// check that the order returned is correct
		String email1 = "";
		String email2 = "";
		String name1 = "";
		String name2 = "";
		// tjb 7/17/14 email not available on SC, so we can't retrieve email
		// address for comparison. Using name instead.
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			name1 = sharedConnectionsFeed.getEntries().get(0).getContributors()
					.get(0).getName();
			name2 = sharedConnectionsFeed.getEntries().get(1).getContributors()
					.get(0).getName();
			if (StringConstants.USER_REALNAME.compareTo(sharedUser2
					.getRealName()) > 0) {
				if (name1.equals(sharedUser2.getRealName())
						&& name2.equals(StringConstants.USER_REALNAME)) {
					LOGGER.debug("PASS: Colleagues were found in asc order by display name");
					assertTrue(true);
				} else {
					LOGGER.debug("ERROR: Colleague entires were not retrieved in asc order by display name");
					assertTrue(false);
				}
			} else {
				if (name2.equals(sharedUser2.getRealName())
						&& name1.equals(StringConstants.USER_REALNAME)) {
					LOGGER.debug("PASS: Colleagues were found in asc order by display name");
					assertTrue(true);
				} else {
					LOGGER.debug("ERROR: Colleague entires were not retrieved in asc order by display name");
					assertTrue(false);
				}
			}
		} else {
			email1 = sharedConnectionsFeed.getEntries().get(0)
					.getContributors().get(0).getEmail();
			email2 = sharedConnectionsFeed.getEntries().get(1)
					.getContributors().get(0).getEmail();
			if (sharedUser1Email.compareTo(sharedUser2.getEmail()) > 0) {
				if (email1.equals(sharedUser2.getEmail())
						&& email2.equals(sharedUser1Email)) {
					LOGGER.debug("PASS: Colleagues were found in asc order by display name");
					assertTrue(true);
				} else {
					LOGGER.debug("ERROR: Colleague entires were not retrieved in asc order by display name");
					assertTrue(false);
				}
			} else {
				if (email2.equals(sharedUser2.getEmail())
						&& email1.equals(sharedUser1Email)) {
					LOGGER.debug("PASS: Colleagues were found in asc order by display name");
					assertTrue(true);
				} else {
					LOGGER.debug("ERROR: Colleague entires were not retrieved in asc order by display name");
					assertTrue(false);
				}
			}
		}
		// check that a full profile was provided
		if (sharedConnectionsFeed.getEntries().get(0).getElements().size() > 12) {
			LOGGER.debug("PASS: Full profile was provided");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Full profile was not provided");
			assertTrue(false);
		}
		if (sharedConnectionsFeed.getEntries().get(0).getContent()
				.indexOf("BEGIN:VCARD") == 1) {
			LOGGER.debug("PASS: VCARD Data was found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: VCARD Data was not found");
			assertTrue(false);
		}

		// Check for output of Connection Entries (not Profile Entries)
		paramsToAppend = "outputType=connection&sortBy=displayName&sortOrder=asc";
		sharedConnectionsFeed = (Feed) service.getSharedConnectionsFeed(
				user3.getKey(), user4.getKey(), paramsToAppend);
		if (sharedConnectionsFeed.getEntries().get(0).getElements().size() == 9) {
			LOGGER.debug("PASS: Connection Entry output was found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Connection Entry output was not found");
			assertTrue(false);
		}

		LOGGER.debug("COMPLETED IN DEPTH TEST: Finding Shared Colleagues between two accounts");
	}

	@Test
	public void getStatusFromNetworkConnections() throws FileNotFoundException,
			IOException, InterruptedException {
		LOGGER.debug("BEGINNING TEST FOR COLLEAGUE STATUS");

		// Get current user info
		String userEmail = StringConstants.USER_EMAIL;
		VCardEntry userCard = service.getUserVCard();
		String userKey = userCard.getVCardFields().get(
				StringConstants.VCARD_PROFILE_KEY);

		// Remove all current network connections
		Feed colleagesFeed = (Feed) service
				.getColleagueFeed(userEmail, userKey);
		for (Entry e : colleagesFeed.getEntries()) {
			service.removeContact(e.getEditLink().getHref().toString());
		}

		// Gather all profiles
		// ArrayList<Profile> profiles =
		// service.getAllProfiles(ProfileOutput.VCARD, false, null, null,null,
		// null, ProfileFormat.FULL, null,
		// URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"), null, 0, 0,
		// null, null, null, null, null, null);
		int profileIndex = 3;

		// Find an available profile that does have a pending connection request
		// from the current user
		boolean profileFound = false;
		ProfilePerspective user2 = null;
		while (!profileFound) {
			try {
				user2 = new ProfilePerspective(profileIndex, useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Entry colleagueStatusEntry = (Entry) service.checkColleagueStatus(
					null, userKey, null, user2.getKey());
			if (colleagueStatusEntry
					.getAttributeValue(StringConstants.API_ERROR) != null) {
				profileFound = true;
			} else {
				profileIndex++;
			}
		}

		// Connect current user to user 2
		Connection newConnection = new Connection(
				"Creating new contact for test");
		Entry inviteResultEntry = (Entry) service.inviteColleague(
				newConnection, user2.getEmail(), user2.getKey());
		Entry myStatusEntry = (Entry) user2.getService().checkColleagueStatus(
				user2.getEmail(), user2.getKey(), userEmail, userKey);
		myStatusEntry.getCategories(StringConstants.SCHEME_STATUS).get(0)
				.setTerm("accepted");
		user2.getService().acceptInvite(
				inviteResultEntry.getEditLink().getHref().toString(),
				myStatusEntry);

		String randString = RandomStringUtils.randomAlphanumeric(3);
		String statusContent = "Android is for squares " + randString;

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			// tjb 7/22/14 /theboard api is obsolete and disabled on SC, so
			// opensocial api must be used for population and validation
			String ublog_entry = "{\"content\":\"" + statusContent + "\"}";
			String statusURL = URLConstants.SERVER_URL
					+ URLConstants.OPENSOCIAL_BASIC
					+ "/rest/ublog/@me/@all/?count=1";

			UserPerspective user2Ublogs=null;
			try {
				user2Ublogs = new UserPerspective(profileIndex,
						StringConstants.Component.MICROBLOGGING.toString(), useSSL);
			} catch (LCServiceException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			UblogsService user2UBlogsService = user2Ublogs.getUblogsService();

			String ublogId = user2UBlogsService.createUblogEntry(statusURL,
					ublog_entry);
			assertEquals("Create my uBlog", 200,
					user2UBlogsService.getRespStatus());

			/*
			 * TJB Fix to RTC #139638 AS/News team, "Agreggation into the AS is
			 * asynchronous by nature, so there is not (and has never been) a
			 * guarantee to return events immediately after they are posted.
			 * Suggest to insert a sleep in the API test to match reality." Not
			 * sure i agree.
			 */
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// For the retrieval
			String statusFeedUrl = URLConstants.SERVER_URL
					+ URLConstants.OPENSOCIAL_BASIC
					+ "/rest/activitystreams/urn:lsid:lconn.ibm.com:profiles.person:"
					+ user2.getUserId()
					+ "/@involved/@all?format=atom&broadcast=true";
			Feed statusFeed = (Feed) service.getAnyFeed(statusFeedUrl);
			assertEquals("Get ActivityStream Feed: headers " + service.getDetail(), 200, service.getRespStatus());

			// Validate
			boolean correctEntryFound = false;
			for (Entry ntry : statusFeed.getEntries()) {
				if (ntry.getTitle().equals(
						user2.getRealName() + " " + statusContent)) {
					correctEntryFound = true;
				}
			}
			assertEquals("Correct Entry not found", true, correctEntryFound);

		} else {
			// Post a status with user 2
			Status newStatus = new Status(statusContent);
			user2.getService().setProfileStatus(newStatus);

			// allow event to be found via GET.
			// try 10 times/seconds to allow the processing of events
			
			for (int second = 0; second <=10; second++) {
				
				// Checks to see if the most recent status in the feed matches
				// 'statusContent'
				Feed colleageStatusFeed = (Feed) service
						.getColleaguesStatusFeed(user2.getEmail());
				assertEquals("Get Colleague Status Feed: headers " + service.getDetail(), 200, service.getRespStatus());
				if (colleageStatusFeed.getEntries().get(0).getContent()
						.equals(statusContent)) {
					LOGGER.debug("TEST SUCCEEDED: Correct Colleage Status Found");
					break;
				} else {
					Thread.sleep(1000);
					//second++;
					LOGGER.debug("processing of events : " + second);
					if (second > 9) {
						LOGGER.debug("TEST FAILED: Incorrect Colleage Status Found");
						assertEquals(statusContent, colleageStatusFeed.getEntries().get(0)
								.getContent());
						break;
					}
				}
			}
			

			// Checks to see if the most recent status in the feed matches
			// 'statusContent'
			Feed colleageStatusFeed = (Feed) service
					.getColleaguesStatusFeed(userEmail);
			assertEquals("Get Colleague Status Feed: headers " + service.getDetail(), 200, service.getRespStatus());

		}

	}

	// for 61146 - however bug still open and this will fail
	// @Test
	// public void inactiveUsers() throws UnsupportedEncodingException {
	// LOGGER.debug("TEST: inactiveUsers()");
	// int totalActive = service.getSearchTotalResults(ProfileOutput.VCARD,
	// true, null, null, null, null, null, "Ian+Inactive", null, 0, 0,
	// null, null, null, null, null, null,null);
	// int totalInactive = service.getSearchTotalResults(ProfileOutput.VCARD,
	// false, null, null, null, null, null, "Ian+Inactive", null, 0, 0,
	// null, null, null, null, null, null,null);
	//
	// assertTrue(totalActive != totalInactive);
	// }

	// TODO @Test
	// Test if common status list is found between two people based on the
	// authors
	public void getStatusFromList() throws FileNotFoundException, IOException {
		LOGGER.debug("Starting Test: Find statuses of people on a list");
		// ArrayList<Profile> profiles =
		// service.getAllProfiles(ProfileOutput.VCARD, false, null, null, null,
		// null, ProfileFormat.FULL, null,
		// URLEncoder.encode(StringConstants.GROUP_NAME, "UTF-8"), null, 0, 0,
		// null, null, null, null, null, null);

		// set up 3 profiles
		ProfilePerspective profile1=null,profile2=null,profile3=null;
		try {
			profile1 = new ProfilePerspective(1, useSSL);
			profile2 = new ProfilePerspective(3, useSSL);
			profile3 = new ProfilePerspective(0, useSSL);
		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// get their respective services
		ProfilesService profile1Service = profile1.getService();
		ProfilesService profile2Service = profile2.getService();
		ProfilesService profile3Service = profile3.getService();

		// set up a status message on each account
		Status newStatus1 = new Status("Frank West");
		Status newStatus2 = new Status("Chuck Greene");
		Status newStatus3 = new Status("Jill Valentine");

		// post the message
		LOGGER.debug("Posting as three different accounts");
		profile1Service.setProfileStatus(newStatus1);
		profile2Service.setProfileStatus(newStatus2);
		profile3Service.setProfileStatus(newStatus3);

		// get the feeds so the author can be found later
		Feed profile1feed = (Feed) profile1Service.getStatus(profile1.getKey());
		Feed profile2feed = (Feed) profile2Service.getStatus(profile2.getKey());

		// get the feed of the messages of profile 1 and 2
		Feed statusList = (Feed) service.getStatusFromMult(profile1.getKey(),
				profile2.getKey());

		for (Entry e : statusList.getEntries()) {
			// if the author of the post is the same as the the author from feed
			// one or two then the statuses were filtered
			if (e.getAuthor()
					.getName()
					.equals(profile1feed.getEntries().get(0).getAuthor()
							.getName())
					|| e.getAuthor()
							.getName()
							.equals(profile2feed.getEntries().get(0)
									.getAuthor().getName())) {
				assertTrue(true);
			} else {
				LOGGER.debug("Test Failed: Statuses were not filtered correctly");
				assertTrue(false);
			}

		}
		LOGGER.debug("Test complete: Statuses filtered correctly");
	}
//slated for removal - this api is part of the profiles suite 
/*	@Test
	// Delete the first comment from a messageboard and check if it was deleted
	public void DeleteMsgBoardComments() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("Post a message board");
			Message newMessage = new Message("Star Wars");
			assertTrue(service.addBoardMessage(newMessage) != null);

			LOGGER.debug("Add replies to the message board");

			Feed boardMessages = getBoardMessageFeed();
			Entry boardEntry = boardMessages.getEntries().get(0);
			String replyLink = boardEntry.getLink("replies").getHref()
					.toString();

			Comment newComment = new Comment("Return of the Jedi");
			Comment newComment2 = new Comment("Empire Strikes Back");

			assertTrue(service.addBoardMessageReply(newComment, replyLink) != null);
			assertTrue(service.addBoardMessageReply(newComment2, replyLink) != null);

			// get the first board message
			LOGGER.debug("Delete the first comment");
			Feed msgBoard = getBoardMessageFeed();
			Entry entry = msgBoard.getEntries().get(0);
			// find the comments from the board message
			String commentsUrl = entry.getLink("replies").getHref().toString();
			// get the feed for the comments
			Feed commentList = (Feed) service.getUrlFeed(commentsUrl);
			// remove the first comment
			Entry remove = commentList.getEntries().get(0);
			String removeUrl = remove.getLink("self").getHref().toString();
			String removedComment = remove.getContent();
			service.deleteUrlFeed(removeUrl);
			// refresh the comment feed
			commentList = (Feed) service.getUrlFeed(commentsUrl);
			// check if the deleted comment is still there
			for (Entry e : commentList.getEntries()) {
				if (e.getContent() == removedComment) {
					LOGGER.debug("Test failed: The comment was not deleted");
					assertTrue(false);
				}
			}
			LOGGER.debug("Test passed: comment was deleted");
			assertTrue(true);
		}
	}*/

	@Test
	public void getProfileServiceConfigurations() {
		LOGGER.debug("BEGINNING TEST: Get Profile Service Configurations");

		Feed serviceConfigs = (Feed) service.getProfileServiceConfigurations();

		if (serviceConfigs.getTitle().equals(
				"IBM Connections Service Configurations")) {
			LOGGER.debug("SUCCESS: Service Configurations Found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Service Configurations Not Found");
			assertTrue(false);
		}

		LOGGER.debug("COMPLETED TEST: Get Profile Service Configurations");
	}

	@Test
	public void getProfileConnections() {
		LOGGER.debug("BEGINNING TEST: Get Feed Connections for a Profile");

		//String email = StringConstants.USER_EMAIL;
		VCardEntry user1Card = service.getUserVCard();
		String key = user1Card.getVCardFields().get(
				StringConstants.VCARD_PROFILE_KEY);
		String email = user1Card.getVCardFields().get(
				StringConstants.VCARD_EMAIL);
		String realname = user1Card.getVCardFields().get(
				StringConstants.VCARD_DISPLAY_NAME);

		Feed colleaguesFeed = (Feed) service.getColleagueFeed(email, key);
		System.out.println(colleaguesFeed.getTitle());
		if (colleaguesFeed.getTitle().equalsIgnoreCase(
				"Connections of " + realname)) {
			LOGGER.debug("SUCCESS: List of Connections for currect profile was found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: List of Connections for Current User was not found");
			assertTrue(false);
		}

		LOGGER.debug("Completed Test: Got Feed of Connections for current user");
	}

	@Test
	public void getProfileBuzzFile() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("BEGGINNING TEST: Get Profile Buzz File");

			// set current user status
			String statusContent = "Go Celtics!";
			Status newStatus = new Status(statusContent);
			service.setProfileStatus(newStatus);

			Entry buzzEntry = (Entry) service
					.getProfileBuzzFile(StringConstants.USER_EMAIL);
			assertEquals(200, service.getRespStatus());
			assertEquals(statusContent, buzzEntry.getTitle());

			LOGGER.debug("COMPLETED TEST: Get Profile Buzz");
		}
	}

	@Test
	public void getProfilePhoto() {
		LOGGER.debug("BEGINNING TEST: Get Profile Photo");

		// get current user key
		VCardEntry user1Card = service.getUserVCard();
		String key = user1Card.getVCardFields().get(
				StringConstants.VCARD_PROFILE_KEY);

		// get current user photo
		Entry photoResponse = (Entry) service.getProfilePhoto(key);
		String contentType = "";
		ExtensibleElement ext = null;

		// TJB 10/2/14 - content type header name can be mixed case
		ext = photoResponse
				.getExtension(StringConstants.CONTENT_TYPE_LOWERCASE);
		if (ext != null) {
			contentType = photoResponse.getExtension(
					StringConstants.CONTENT_TYPE_LOWERCASE).getText();
		} else {
			contentType = photoResponse.getExtension(
					StringConstants.CONTENT_TYPE).getText();
		}

		// validate that response was of correct file type
		if (contentType.equals("image/png") || contentType.equals("image/jpg")
				|| contentType.equals("image/gif")
				|| contentType.equals("image/jpeg")) {
			LOGGER.debug("SUCCESS: Profile Image was found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Profile Image was not found");
			assertTrue(false);
		}

		LOGGER.debug("COMPLETED TEST: Get Profile Photo");
	}


	@Test
	public void getFollowedProfiles() throws Exception {
		LOGGER.debug("Starting Test: Get feed of followed profiles");

		ProfilePerspective profileTarget = new ProfilePerspective(1, useSSL);
		ProfilePerspective profileSource = new ProfilePerspective(2, useSSL);

		Abdera abdera = new Abdera();
		Factory factory = abdera.getFactory();
		Entry follow = factory.newEntry();
		follow.declareNS("http://www.w3.org/2007/app", "app");
		follow.declareNS("http://www.ibm.com/xmlns/prod/sn", "snx");
		follow.addCategory(StringConstants.SCHEME_TYPE, "resource-follow", null);
		follow.addCategory(StringConstants.SCHEME_SOURCE, "profiles", null);
		follow.addCategory(StringConstants.SCHEME_RESOURCE_TYPE, "profile",
				null);
		follow.addCategory(StringConstants.SCHEME_RESOURCE_ID,
				profileTarget.getUserId(), null);

		// Establish the follow relationship
		ExtensibleElement followFeed = service.createFollow(follow);
		assertEquals("Create follow failed " + service.getDetail(), 200, service.getRespStatus());

		Feed profileFollow = (Feed) service.getFollow();
		assertEquals("Get follow result: headers " + service.getDetail(), 200, service.getRespStatus());
		boolean profileFollowed = false;
		for (Entry e : profileFollow.getEntries()) {
			if (e.getTitle().equals(profileTarget.getRealName())) {
				LOGGER.debug("Test Successful: Found the followed target");
				profileFollowed = true;
				break;
			}
		}
		if (!profileFollowed) {
			LOGGER.debug("Test Failed: Could not find the followed profile in the feed");
		}
		assertTrue(profileFollowed);

		String editUrl = "";
		for (Entry ntry : ((Feed)followFeed).getEntries()) {
			editUrl = ntry.getEditLinkResolvedHref().toString();
		}

		boolean result = service.deleteFollow(editUrl);
		assertEquals("Stop follow operation was not successful. ", true, result);
	}

	// for defect 73896 - use legacy call to retrieve our status
	@Test
	public void getLegacyStatus() {
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("BEGINNING TEST: getLegacyStatus");

			String title = service.getLegacyStatusTitle();

			// NOTE: allow empty status as long as call works
			if (title == null) {
				LOGGER.debug("ERROR: Legacy Status Not Found");
				assertTrue(false);
			} else {
				LOGGER.debug("SUCCESS: Legacy Status found");
				assertTrue(true);
			}

			LOGGER.debug("COMPLETED TEST: getLegacyStatus");
		}
	}

	@Test
	public void phoneNumberFormat() throws IOException, Exception {
		/*
		 * RTC 82347. Test Process: 1. Update user's phone number via vcard. 2.
		 * Allow search index process to complete. 3. Search for phone number by
		 * generating search feed 4. Verify the phone number is formatted using
		 * hyphens. In the defect, the phone numbers returned were missing these
		 * and were incorrectly displayed as 14121110000.
		 */
		LOGGER.debug("BEGINNING TEST: RTC 82347 'normalized' phone numbers are indexed with the 'real' phone number.");
		VCardEntry vCard = service.getUserVCard();
		assert (vCard != null);
		vCard.setTelephoneNumber("1-412-111-0000");

		boolean test = service.updateProfile(vCard);
		assertTrue(test);

		/*
		 * Move to SearchPopulate -> save indexWaiter time - up to 15 mins on
		 * BVT server new IndexWaiter().waitForIndexingToCompleteTest();
		 * 
		 * Feed result = (Feed) service.doBasicSearch("profiles", "1-412-111");
		 * String content =""; for (Entry fEntry : result.getEntries()) {
		 * Content cntnt = fEntry.getContentElement(); content =
		 * cntnt.toString(); }
		 * 
		 * assertTrue(content.contains("1-412-111-0000")); LOGGER.debug(
		 * "ENDING TEST: RTC 82347 'normalized' phone numbers are indexed with the 'real' phone number."
		 * );
		 */
		LOGGER.debug("ENDING TEST for RTC 82347 population, verify later in SearchPopulate");
	}

	@Test
	public void getReportToChain() {
		/*
		 * Tests the ability to get a user's report-to chain NOTE: Will not pass
		 * if the user does not have a manager Step 1: Get the profile feed of
		 * the user to test Step 2: Extract the name of the user's manager from
		 * the feed Step 3: Get report-to chain, verify the user and manager are
		 * on there correctly
		 * 
		 * tjb 7/25/14 Prevent this test from running on SC until this Epic is
		 * resolved: RTC 130628
		 */
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			LOGGER.debug("Beginning test: Get Report To Chain");

			LOGGER.debug("Step 1: Get the profile feed of the user to test");
			Feed profileFeed = (Feed) service.getProfileFeed(user.getEmail(),
					null, user.getKey());

			LOGGER.debug("Step 2: Extract the name of the user's manager from the feed");
			String managerName = null;
			for (Entry en : profileFeed.getEntries()) {
				for (Element el : en.getElements()) {
					if (el.toString().startsWith("<content")) {
						try {
							managerName = el
									.toString()
									.split("<div class=\"x-manager-uid\" style=\"display:none\">")[1]
									.split("</div>")[0];
						} catch (Exception e) {
							LOGGER.debug("ERROR: This user does not have a manager, report-to chain cannot be tested. Ending test.");
							assertTrue(false);
						}
					}
				}
			}

			LOGGER.debug("Step 3: Get report-to chain, verify the user and manager are there in correct order");
			Feed reportToChain = (Feed) service.getReportingChain(
					StringConstants.USER_EMAIL, null, null);
			String myUserName = reportToChain.getEntries().get(0).getTitle();
			String firstReportToRealName = reportToChain.getEntries().get(1)
					.getTitle();
			String firstReportToUserName = reportToChain.getEntries().get(1)
					.getContributors().get(0).getEmail().split("@")[0];

			assertTrue(myUserName.equals(StringConstants.USER_REALNAME)
					|| myUserName.equals(StringConstants.USER_NAME));
			assertTrue(firstReportToRealName.equals(managerName)
					|| firstReportToUserName.equals(managerName));

			LOGGER.debug("Ending test: Get Report To Chain");
		}
	}

	@Test
	public void uploadProfilePicture() throws IOException {
		/*
		 * Tests the ability to upload a profile picture - tests PUT, GET, and
		 * DELETE PUT request w/ binary data on endpoint
		 * /profiles/photo.do?key=<key>&lastMod=<lastMod> Also validates fix for
		 * defect 119749 Step 1: GET user profile feed Step 2: Extract the
		 * profile picture link from the feed Step 3: Upload testProfPicSmall
		 * (small file size) with PUT request Step 4: GET profilePicture, save
		 * content length Step 5: Upload testProfPicLarge (large file size) with
		 * PUT request Step 6: GET profilePicture, verify content length is
		 * greater than the saved content length of testProfPicSmall Step 7: GET
		 * profilePicture thumbnail, verify content length is less than the
		 * saved content length of testProfPicLarge Step 8: DELETE
		 * profilePicture Step 9: GET profilePicture, verify content length is
		 * less than the saved content length of testProfPicLarge
		 */
		LOGGER.debug("BEGINNING TEST: Upload profile picture");
		LOGGER.debug("Step 1... Get user profile feed");
		Feed profileFeed = (Feed) service.getProfileFeed(
				StringConstants.USER_EMAIL, null, user.getKey());

		LOGGER.debug("Step 2... Extract the profile picture link from the feed");
		Entry profileEntry = profileFeed.getEntries().get(0); // Grab the user
		// entry (should
		// be the only
		// entry in the
		// feed)
		String profPicLink = profileEntry.getLinkResolvedHref(
				StringConstants.REL_IMAGE).toString();

		LOGGER.debug("Step 3... Upload testProfPicSmall (small file size) with PUT request");
		InputStream testProfPicSmall = this.getClass().getResourceAsStream(
				"/resources/small_filesize_profile_pic.png");
		String mimeType = Utils.getMimeType(new File(this.getClass()
				.getResource("/resources/small_filesize_profile_pic.png")
				.getFile()));
		service.uploadProfilePicture(profPicLink, testProfPicSmall, mimeType);

		LOGGER.debug("Step 4... GET profilePicture, save content length");
		Entry profPicEntry = (Entry) service.getAnyFeed(profPicLink);
		int smallPicContentLength = 0;

		ExtensibleElement ext = profPicEntry
				.getExtension(StringConstants.CONTENT_LENGTH_LOWERCASE);
		if (ext != null) {
			smallPicContentLength = Integer.parseInt(profPicEntry.getExtension(
					StringConstants.CONTENT_LENGTH_LOWERCASE).getText());
		} else {
			smallPicContentLength = Integer.parseInt(profPicEntry.getExtension(
					StringConstants.CONTENT_LENGTH).getText());
		}

		LOGGER.debug("Step 5... Upload testProfPicLarge (large file size) with PUT request");
		InputStream testProfPicLarge = this.getClass().getResourceAsStream(
				"/resources/lamborghini_murcielago_lp640.jpg");
		mimeType = Utils.getMimeType(new File(this.getClass()
				.getResource("/resources/lamborghini_murcielago_lp640.jpg")
				.getFile()));
		service.uploadProfilePicture(profPicLink, testProfPicLarge, mimeType);

		LOGGER.debug("Step 6... GET profilePicture, verify content length is greater than the saved content length of testProfPicSmall");
		profPicEntry = (Entry) service.getAnyFeed(profPicLink);
		int largePicContentLength = 0;

		ExtensibleElement ext2 = profPicEntry
				.getExtension(StringConstants.CONTENT_LENGTH_LOWERCASE);
		if (ext2 != null) {
			largePicContentLength = Integer.parseInt(profPicEntry.getExtension(
					StringConstants.CONTENT_LENGTH_LOWERCASE).getText());
		} else {
			largePicContentLength = Integer.parseInt(profPicEntry.getExtension(
					StringConstants.CONTENT_LENGTH).getText());
		}
		assertEquals(
				"largePic content-length should be > smallPic content-length",
				true, largePicContentLength > smallPicContentLength);

		// Validate fix for defect 119749
		LOGGER.debug("Step 7... GET profilePicture thumbnail, verify content length is less than the saved content length of testProfPicLarge");
		profPicEntry = (Entry) service.getAnyFeed(profPicLink + "&small=true"); // &small=true
		// makes
		// it
		// return
		// the
		// thumbnail
		int largePicThumbnailContentLength = 0;
		ExtensibleElement ext3 = profPicEntry
				.getExtension(StringConstants.CONTENT_LENGTH_LOWERCASE);
		if (ext3 != null) {
			largePicThumbnailContentLength = Integer.parseInt(profPicEntry
					.getExtension(StringConstants.CONTENT_LENGTH_LOWERCASE)
					.getText());
		} else {
			largePicThumbnailContentLength = Integer.parseInt(profPicEntry
					.getExtension(StringConstants.CONTENT_LENGTH).getText());
		}
		assertEquals(
				"largePicThumbnail content-length should be < largePic content-length",
				true, largePicThumbnailContentLength < largePicContentLength);

		LOGGER.debug("Step 8... DELETE profilePicture");
		assertTrue(service.deleteAnyFeed(profPicLink)); // Resets the profile
		// picture back to the
		// default image

		LOGGER.debug("Step 9... GET profilePicture, verify content length is less than the saved content length of testProfPicLarge");
		profPicEntry = (Entry) service.getAnyFeed(profPicLink);
		int defaultPicContentLength = 0;
		ExtensibleElement ext4 = profPicEntry
				.getExtension(StringConstants.CONTENT_LENGTH_LOWERCASE);
		if (ext4 != null) {
			defaultPicContentLength = Integer.parseInt(profPicEntry
					.getExtension(StringConstants.CONTENT_LENGTH_LOWERCASE)
					.getText());
		} else {
			defaultPicContentLength = Integer.parseInt(profPicEntry
					.getExtension(StringConstants.CONTENT_LENGTH).getText());
		}
		assertEquals(
				"defaultPic content-length should be < largePic content-length",
				true, defaultPicContentLength < largePicContentLength);

		LOGGER.debug("ENDING TEST: Upload profile picture");
	}
	
	//@Test
	public void uploadPictureAsOrgAdmin() throws IOException {
		/*
		 * Tests the ability to upload a profile picture - tests PUT, GET, and
		 * DELETE PUT request w/ binary data on endpoint /profiles/photo.do?key=<key>&lastMod=<lastMod> 
		 * Step 1: GET user profile feed 
		 * Step 2: Extract the profile picture link from the feed  
		 * Step 3: Upload testProfPicLarge (large file size) with PUT request 
		 * Step 4: GET profilePicture, verify content length is
		 * greater than the saved content length of testProfPicSmall 
		 * Step 5: DELETE profilePicture 
		 * Step 6: GET profilePicture, verify content length is
		 * less than the saved content length of testProfPicLarge
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD){
			LOGGER.debug("BEGINNING TEST: Upload profile picture as org admin");
			LOGGER.debug("Step 1... Get user profile feed");
			Feed profileFeed = (Feed) adminUserService.getProfileFeed(StringConstants.USER_EMAIL, null, user.getKey());

			LOGGER.debug("Step 2... Extract the profile picture link from the feed");
			// Grab the user entry (should be the only one in the feed).
			Entry profileEntry = profileFeed.getEntries().get(0); 

			String profPicLink = profileEntry.getLinkResolvedHref(StringConstants.REL_IMAGE).toString();

			LOGGER.debug("Step 3... Upload testProfPicLarge (large file size) with PUT request");
			InputStream testProfPicLarge = this.getClass().getResourceAsStream("/resources/lamborghini_murcielago_lp640.jpg");
			String mimeType = Utils.getMimeType(new File(this.getClass()
				.getResource("/resources/lamborghini_murcielago_lp640.jpg")
				.getFile()));
			adminUserService.uploadProfilePicture(profPicLink, testProfPicLarge, mimeType);

			LOGGER.debug("Step 4... GET profilePicture, verify content length is greater than the saved content length of testProfPicSmall");
			Entry profPicEntry = (Entry) adminUserService.getAnyFeed(profPicLink);
			int largePicContentLength = 0;

			ExtensibleElement ext2 = profPicEntry
				.getExtension(StringConstants.CONTENT_LENGTH_LOWERCASE);
			if (ext2 != null) {
			largePicContentLength = Integer.parseInt(profPicEntry.getExtension(
					StringConstants.CONTENT_LENGTH_LOWERCASE).getText());
			} else {
					largePicContentLength = Integer.parseInt(profPicEntry.getExtension(
					StringConstants.CONTENT_LENGTH).getText());
			}
			assertEquals("largePic content-length should be > 0", true, largePicContentLength > 0);


			LOGGER.debug("Step 5... DELETE profilePicture");
			// Resets the profile picture back to the default image
			assertTrue(adminUserService.deleteAnyFeed(profPicLink));

			LOGGER.debug("Step 6... GET profilePicture, verify content length is less than the saved content length of testProfPicLarge");
			profPicEntry = (Entry) adminUserService.getAnyFeed(profPicLink);
			int defaultPicContentLength = 0;
			ExtensibleElement ext4 = profPicEntry
				.getExtension(StringConstants.CONTENT_LENGTH_LOWERCASE);
			if (ext4 != null) {
				defaultPicContentLength = Integer.parseInt(profPicEntry
					.getExtension(StringConstants.CONTENT_LENGTH_LOWERCASE)
					.getText());
			} else {
				defaultPicContentLength = Integer.parseInt(profPicEntry
					.getExtension(StringConstants.CONTENT_LENGTH).getText());
			}
			assertEquals(
				"defaultPic content-length should be < largePic content-length",
				true, defaultPicContentLength < largePicContentLength);

			LOGGER.debug("ENDING TEST: Upload profile picture as org admin.");
		}
	}

	@Test
	public void uploadPronunciationFile() throws FileNotFoundException {
		/*
		 * Tests the ability to upload a pronunciation audio file PUT request w/
		 * binary data on endpoint
		 * /profiles/audio.do?key=<key>&lastMod=<lastMod> Step 1: GET user
		 * profile feed Step 2: Extract the pronunciation link from the feed
		 * Step 3: Clear the pronunciation file using an HTTP DELETE Step 4: GET
		 * pronunciation file, verify HTTP status code 204 (no content) Step 5:
		 * Upload pronunciation file with PUT request Step 6: GET pronunciation
		 * file, verify content length is greater than 0 bytes
		 */
		LOGGER.debug("BEGINNING TEST: Upload profile picture");
		LOGGER.debug("Step 1... Get user profile feed");
		Feed profileFeed = (Feed) service.getProfileFeed(
				StringConstants.USER_EMAIL, null, user.getKey());

		LOGGER.debug("Step 2... Extract the pronunciation link from the feed");
		Entry profileEntry = profileFeed.getEntries().get(0); // Grab the user
		// entry (should
		// be the only
		// entry in the
		// feed)
		String pronunciationLink = profileEntry.getLinkResolvedHref(
				StringConstants.REL_PRONUNCIATION).toString();

		LOGGER.debug("Step 3... Clear the pronunciation file using an HTTP DELETE");
		assertTrue(service.deleteAnyFeed(pronunciationLink));

		LOGGER.debug("Step 4... GET pronunciation file, verify HTTP status code 204 (no content)");
		service.getAnyFeed(pronunciationLink);
		assertEquals("Response code should be 204 (no content)", 204,
				service.getRespStatus());

		LOGGER.debug("Step 5... Upload pronunciation file with PUT request");
		InputStream audioStream = this.getClass().getResourceAsStream(
				"/resources/pronunciation.wav");
		String mimeType = Utils.getMimeType(new File(this.getClass()
				.getResource("/resources/pronunciation.wav").getFile()));
		service.uploadPronunciationFile(pronunciationLink, audioStream,
				mimeType);

		LOGGER.debug("Step 6... GET pronunciation file, verify content length is greater than 0 bytes");
		Entry pronunciationEntry = (Entry) service
				.getAnyFeed(pronunciationLink);
		int audioContentLength = 0;
		ExtensibleElement ext = pronunciationEntry
				.getExtension(StringConstants.CONTENT_LENGTH_LOWERCASE);
		if (ext != null) {
			audioContentLength = Integer.parseInt(pronunciationEntry
					.getExtension(StringConstants.CONTENT_LENGTH_LOWERCASE)
					.getText());
		} else {
			audioContentLength = Integer.parseInt(pronunciationEntry
					.getExtension(StringConstants.CONTENT_LENGTH).getText());
		}
		assertEquals("Content length should be > 0", true,
				audioContentLength > 0);

		LOGGER.debug("ENDING TEST: Upload profile picture");
	}

	/*
	 * RTC 136554 APIs accepting multiple keys do not support space character in
	 * URL.
	 * 
	 * API for retrieving profiles should support the space character. For
	 * example:
	 * https://lc45linux1.swg.usma.ibm.com/profiles/admin/atom/profiles.
	 * do?key=[key 1],%20[key 2]
	 * 
	 * Process: 1. Request the profiles of 3 separate users in one request using
	 * key param. Each key is separated by a comma and one or more spaces. 2.
	 * Parse through the feed. Validate the expected users and entry count.
	 * 
	 * There is a similar test in the admin test class - it has minor
	 * differences in the API tested.
	 */
	@Test
	public void multipleKeyLookUp() throws FileNotFoundException, IOException {
		LOGGER.debug("BEGINNING TEST: RTC 116042 Multiple key lookup with spaces. ");
		int USER_INDEX_1 = 2;
		int USER_INDEX_2 = 3;
		int USER_INDEX_3 = 5;
		int NUM_OF_PROFILES = 3;
	
		UserPerspective user1=null,user2=null,user3=null;
		try {
			user1 = new UserPerspective(USER_INDEX_1,
					Component.PROFILES.toString(), useSSL);
			user2 = new UserPerspective(USER_INDEX_2,
					Component.PROFILES.toString(), useSSL);
			user3 = new UserPerspective(USER_INDEX_3,
					Component.PROFILES.toString(), useSSL);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		String userKey1 = user1.getKey();
		String userKey2 = user2.getKey();
		String userKey3 = user3.getKey();
		String userName1 = user1.getRealName();
		String userName2 = user2.getRealName();
		String userName3 = user3.getRealName();
		boolean found1 = false;
		boolean found2 = false;
		boolean found3 = false;
		int entryCount = 0;

		// Include spaces in the request string.
		String keyString = userKey1 + ",%20" + userKey2 + "%20,%20" + userKey3;
		Feed fd = (Feed) service.getAnyFeed(URLConstants.SERVER_URL
				+ "/profiles/atom/profile.do?key=" + keyString);

		for (Entry ntry : fd.getEntries()) {
			if (ntry.getTitle().equalsIgnoreCase(userName1)) {
				found1 = true;
			} else if (ntry.getTitle().equalsIgnoreCase(userName2)) {
				found2 = true;
			} else if (ntry.getTitle().equalsIgnoreCase(userName3)) {
				found3 = true;
			}
			entryCount++;
		}

		assertEquals("Incorrect number of profiles returned.", true,
				entryCount == NUM_OF_PROFILES);
		assertEquals("Wrong names returned in the feed", true, found1 && found2
				&& found3);

		LOGGER.debug("ENDING TEST: RTC 116042 Multiple key lookup with spaces.");
	}

	/*
	 * RTC 140111 SC: Email should be exposed in Profile Feeds
	 * 
	 * Verify that email element is included in Profiles feeds For example:
	 * <email>ajones242@bluebox.lotus.com</email>
	 * 
	 * Process: 1. Get a Feed of user's profile 2. Make sure the email element
	 * is there.
	 * 
	 * This test is for smartcloud only.
	 */

	@Test
	public void emailInFeed() {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			String email = "";
			LOGGER.debug("BEGINNING TEST: RTC 140111");
			String EMAIL_IS_BLANK = ""; // Since this is SC email is not
			// exposed. Using the key to get the
			// profile instead.

			LOGGER.debug("Step 1: Get the profile feed of the user to test");
			Feed profileFeed = (Feed) service.getProfileFeed(EMAIL_IS_BLANK,
					null, user.getKey());

			LOGGER.debug("Step 2: Validate that the email element is available and populated.");
			for (Entry ntry : profileFeed.getEntries()) {
				for (Person contributor : ntry.getContributors()) {
					email = contributor.getEmail();
				}
			}
			assertEquals("Email address not available", true,
					email.contains("@") && email.length() > 0);

			LOGGER.debug("ENDING TEST: RTC 140111");
		}
	}

	/*
	 * RTC 139084 SC: Add 'profiles' back to the serviceconfigs for Files
	 * 
	 * Verify that the link for profiles is included in the serviceconfig for
	 * files. For example:
	 * http://apps.acdev1.swg.usma.ibm.com/files/serviceconfigs
	 * 
	 * Process: 1. Get a Feed of the Files serviceconfigs document 2. Look for
	 * the Profiles entry 3. Make sure the application URLs are available
	 * 
	 * This test is for smartcloud only.
	 */
	@Test
	public void serviceConfigLinkTest() throws FileNotFoundException,
			IOException, URISyntaxException {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("BEGINNING TEST: RTC 139084 Profiles link in Files.");

			String endpointString = "/files/serviceconfigs";
			String url = URLConstants.SERVER_URL + endpointString;
			String profilesUrl = URLConstants.SERVER_URL + "/profiles";
			boolean foundProfilesUrl = false;

			Feed feed = (Feed) service.getAnyFeed(url);
			Link link;

			for (Entry ntry : feed.getEntries()) {
				if (ntry.getTitle().equalsIgnoreCase("profiles")) {
					List<Link> links = ntry.getLinks();
					Iterator<Link> lnk = links.listIterator();
					while (lnk.hasNext()) {
						link = lnk.next();
						if (link.getHref().toString()
								.equalsIgnoreCase(profilesUrl)) {
							foundProfilesUrl = true;
						}
					}
				}
			}

			assertEquals("Profiles URL not found", true, foundProfilesUrl);

			LOGGER.debug("ENDING TEST: RTC 139084 Profiles link in Files.");
		}
	}
}
