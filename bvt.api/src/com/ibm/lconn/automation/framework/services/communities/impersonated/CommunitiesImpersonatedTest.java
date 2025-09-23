package com.ibm.lconn.automation.framework.services.communities.impersonated;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.StringGenerator;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.FollowEntry;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesTestBase;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommentToEvent;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Event;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;
import com.ibm.lconn.automation.framework.services.communities.nodes.Subcommunity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;

//
/**
 * JUnit Tests via Connections API for Communities Service
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class CommunitiesImpersonatedTest extends CommunitiesTestBase{

	//private static UserPerspective local_user, impersonateByotherUser, user,imUser, otherUser, admin;
	//private static CommunitiesService assignedService, otherUserImService,service, otherUserService, adminService;
	private static UserPerspective impersonateByotherUser;
	static CommunitiesService otherUserImService;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunitiesImpersonatedTest.class.getName());
	static boolean useSSL = true;
	static Abdera abdera = new Abdera();

	private static Community setupCommunityRetrieved = null;
	private static String setupEditLink = "";
	private static String setupCommunityTitle = "";

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Communities impersonation Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		userEnv.getImpersonateEnvironment(StringConstants.ADMIN_USER,
				StringConstants.CURRENT_USER, Component.COMMUNITIES.toString());
		user = userEnv.getLoginUser();
		service = user.getCommunitiesService();
		imUser = userEnv.getImpersonatedUser();

		otherUser = new UserPerspective(StringConstants.RANDOM1_USER,
				Component.COMMUNITIES.toString(), useSSL);
		otherUserService = otherUser.getCommunitiesService();

		impersonateByotherUser = new UserPerspective(
				StringConstants.RANDOM1_USER, Component.COMMUNITIES.toString(),
				StringConstants.CURRENT_USER);
		otherUserImService = impersonateByotherUser.getCommunitiesService();

		admin = new UserPerspective(0, Component.COMMUNITIES.toString(), useSSL);
		adminService = admin.getCommunitiesService();

		// This is the same user as the impersonated user. To be used for GETs
		// as needed.
		assignedUser = new UserPerspective(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString(), useSSL,
				StringConstants.CURRENT_USER);		
		assignedService = assignedUser.getCommunitiesService();

		
		UserPerspective profilesAdminUser = userEnv.getLoginUserEnvironment(StringConstants.ADMIN_USER,
				Component.PROFILES.toString());
		gateKeeperService = profilesAdminUser.getGateKeeperService();

		
		/****************************************************************
		/* TJB 2/11/15 Create a community to be used for multiple tests */
		/****************************************************************
		/****************************************************************/
		LOGGER.debug("BEGINNING: Communities Impersonation Setup: Create Community");
		String randString = RandomStringUtils.randomAlphanumeric(4);
		setupCommunityTitle = "Impersonation Community " + randString;

		LOGGER.debug("Step 1: Create the community");
		Community testCommunity = new Community(setupCommunityTitle,
				"Validate that Community impersonation is working.",
				Permissions.PUBLIC, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		setupEditLink = communityResult.getEditLinkResolvedHref().toString();

		LOGGER.debug("Step 2: Retrieve that community");
		setupCommunityRetrieved = new Community(
				(Entry) service.getCommunity(setupEditLink));

		LOGGER.debug("Step 3: Validate that the community was created by impersonated user, not the admin user.");
		List<Person> authList = setupCommunityRetrieved.getAuthors();
		String author = authList.get(0).getName();

		assertEquals("Author is not the expected value: ",
				imUser.getRealName(), author);

		LOGGER.debug("ENDING: Communities Impersonation Setup: Create Community");

		LOGGER.debug("Finished Initializing Communities Data Population Test");
	}
	
	
	/*
	 * A simple test to validate that communities impersonation is working
	 * 
	 * This test should only execute as part of the impersonation test suite.
	 */
	@Test
	public void validateCommunityImpersonation() {
		LOGGER.debug("BEGINNING TEST: validate community impersonation support.");

		String communityTitle = "Community Impersonation Test";

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community(communityTitle,
				"Validate that Community impersonation is working.",
				Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Retrieve that community");
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		LOGGER.debug("Step 3: Validate that the community was created by impersonated user, not the admin user.");
		List<Person> authList = communityRetrieved.getAuthors();
		String author = authList.get(0).getName();

		assertEquals("Author is not the expected value: ",
				imUser.getRealName(), author);

		LOGGER.debug("ENDING TEST: validate community impersonation support.");
	}

	/*
	 * The following tests rely on the Community created in the Setup method
	 * 
	 * These must be tested: 
	 * 1. Create and update Community DONE 
	 * 2. Community invite POST DONE 
	 * 3. Community broadcast 
	 * 4. Community member
	 */
	// @Test
	public void updatePut() {
		String randString = RandomStringUtils.randomAlphanumeric(4);
		String communityUpdatedTitle = "Update Test - AFTER the update. "
				+ randString;
		String communityUpdatedDescr = "The description AFTER the update "
				+ randString;

		LOGGER.debug("Step 1: Edit the community.  Get and Entry doc, update the Title, Description and Id elements");
		Entry entryRetrieved = (Entry) service.getCommunity(setupEditLink);

		entryRetrieved.setTitle(communityUpdatedTitle);
		entryRetrieved.setContent(communityUpdatedDescr);

		LOGGER.debug("Step 2: Perform an update operation using PUT");
		Entry result = (Entry) service.putEntry(setupEditLink, entryRetrieved);

		LOGGER.debug("Step 3: Validate the update.");
		assertEquals(true,
				result.getTitle().equalsIgnoreCase(communityUpdatedTitle));
		assertEquals(true,
				result.getContent().equalsIgnoreCase(communityUpdatedDescr));

		List<Person> authList = result.getAuthors();
		String author = authList.get(0).getName();

		LOGGER.debug("Step 4: Validate that the impersonated user performed update.");
		assertEquals("Author is not the expected value: ",
				imUser.getRealName(), author);

		LOGGER.debug("Step 5: Reset Title to its original value");
		entryRetrieved.setTitle(setupCommunityTitle);
		service.putEntry(setupEditLink, entryRetrieved);
		assertEquals(200, service.getRespStatus());

	}

	// @Test
	public void invitePost() {
		LOGGER.debug("Beginning Test: Invitations.");
		LOGGER.debug("Step 1: Create a community invitation for another user.");
		Invitation newInvite = new Invitation(null, otherUser.getUserId(),
				"Join my community!", "I promise there will be cake");
		Entry response = (Entry) service.createInvitation(
				setupCommunityRetrieved, newInvite);
		assertEquals("Invite did not work correctly ", 201,
				service.getRespStatus());

		LOGGER.debug("Step 2: Validate that the impersonated user created the invitation.");
		List<Person> authList = response.getAuthors();
		String author = authList.get(0).getName();
		assertEquals("Author is not the expected impersonated user: ",
				imUser.getRealName(), author);

		LOGGER.debug("Step 4: Verify that the invitation shows up in the user's invitations");
		Feed invitationsFeed = (Feed) otherUserService.getMyInvitations(false,
				1, 10, null, null);
		boolean foundInvitation = false;
		for (Entry invitationEntry : invitationsFeed.getEntries()) {
			if (invitationEntry.getTitle().contains(
					setupCommunityRetrieved.getTitle()))
				foundInvitation = true;
		}
		assertTrue(foundInvitation);

		LOGGER.debug("Ending test: Invitations.");
	}

	/* *******************************************************************
	 * End of tests that rely on the Community created in the Setup method
	 * *******************************************************************
	 * *******************************************************************
	 */

	@Test
	public void testCommunityCalendarImpersonateInvalidUser() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {//set this test case as on_premise only. OCS 210182
		LOGGER.debug("testing Community calendar Event ImpersonateInvalidUser");

		String CommunityTitle = "CommunityCalendarTest"
				+ Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a community");
		Community comm = new Community(CommunityTitle,
				"A community for testing", Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(comm);
		LOGGER.debug("This community service's user is: "+ user.getEmail());

		LOGGER.debug("Step 2: Retrieve that community");
		Community community = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		LOGGER.debug("Step 3: Add Calendar widget.");
		Widget widget = new Widget(
				StringConstants.WidgetID.Calendar.toString(), "col3");
		service.postWidget(community, widget.toEntry());
		assertEquals("add calendar widget", 201, service.getRespStatus());

		LOGGER.debug("Step 4: Get remote apps feed.");
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				community.getRemoteAppsListHref(), true, null, 0, 50, null,
				null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);

		for (Entry entry : remoteAppsFeed.getEntries()) {
			for (Category category : entry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("calendar")) {

					String service_url = null;
					String event_url = null;
					for (Link link : entry.getLinks()) {
						if (link.getRel().contains(
								StringConstants.REL_REMOTEAPPLICATION_PUBLISH)) {
							service_url = link.getHref().toString();
						}
						if (link.getRel().contains(
								StringConstants.REL_REMOTEAPPLICATION_FEED)) {
							event_url = link.getHref().toString();
						}
					}

					LOGGER.debug("Step 5: get calendar service doc.");
					ExtensibleElement serviceFeed = assignedService
							.getCommunityCalendarService(service_url);
					assertTrue(serviceFeed != null);

					LOGGER.debug("Step 6: Getting Community Calendar:");
					String calendar_url = service_url.replace(
							"calendar/service?", "calendar?");
					ExtensibleElement calendarFeed = assignedService
							.getCommunityCalendarService(calendar_url);
					assertTrue(calendarFeed != null);

					String role = calendarFeed.getExtension(
							StringConstants.SNX_MAP_ROLE).getText();

					if (!role.equalsIgnoreCase("reader")) {
						LOGGER.debug("Step 7: post calendar event.");
						Event event = new Event("My event",
								StringConstants.STRING_YES_LOWERCASE, null,
								null, null, "2012-08-23T02:00:00.000Z",
								"2012-08-23T03:00:00.000Z", 0);
						String eventUrl = event_url.replace(
								"type=event&calendarUuid", "calendarUuid");
						Date create = new Date(System.currentTimeMillis() - 3
								* 24 * 60 * 60 * 1000);
						event.setPublished(create);
						
						UserPerspective adminUser = new UserPerspective(StringConstants.ADMIN_USER,
								Component.COMMUNITIES.toString());
						CommunitiesService CommunitiesAdminUserService = adminUser.getCommunitiesService();
						LOGGER.debug("org admin user is: " + adminUser.getEmail());
						
						Map<String, String> requestHeaders = new HashMap<String, String>();						
						requestHeaders.put("X-LConn-RunAs", "useremail=noneExistUser@janet.iris.com, fallback=false");
						LOGGER.debug("The community's owner: " + user.getEmail() +"/"+user.getPassword());
						CommunitiesAdminUserService.setRequestOptions(requestHeaders);
						
						Entry result = (Entry) CommunitiesAdminUserService.postCalendarEvent(
								eventUrl, event);
						LOGGER.debug("Step 8: Validate the invalid user returns ERROR - 412: Precondition Failed.");
						assertEquals("post commuinty calendar event", HttpServletResponse.SC_PRECONDITION_FAILED, CommunitiesAdminUserService.getRespStatus());
					}
				}
			}
		}

		LOGGER.debug("Finished Community Calendar event ImpersonateInvalidUser...");
	}//end of the on_premise if
	}
	
	@Test
	public void testCommunityCalendarEvent() {
		LOGGER.debug("testing Community calendar Event");

		String CommunityTitle = "CommunityCalendarTest"
				+ Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a community");
		Community comm = new Community(CommunityTitle,
				"A community for testing", Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(comm);

		LOGGER.debug("Step 2: Retrieve that community");
		Community community = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		LOGGER.debug("Step 3: Add Calendar widget.");
		Widget widget = new Widget(
				StringConstants.WidgetID.Calendar.toString(), "col3");
		service.postWidget(community, widget.toEntry());
		assertEquals("add calendar widget", 201, service.getRespStatus());

		LOGGER.debug("Step 4: Get remote apps feed.");
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				community.getRemoteAppsListHref(), true, null, 0, 50, null,
				null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);

		for (Entry entry : remoteAppsFeed.getEntries()) {
			for (Category category : entry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("calendar")) {

					String service_url = null;
					String event_url = null;
					for (Link link : entry.getLinks()) {
						if (link.getRel().contains(
								StringConstants.REL_REMOTEAPPLICATION_PUBLISH)) {
							service_url = link.getHref().toString();
						}
						if (link.getRel().contains(
								StringConstants.REL_REMOTEAPPLICATION_FEED)) {
							event_url = link.getHref().toString();
						}
					}
					LOGGER.debug("Service URL : " + service_url);

					// get calendar service doc
					ExtensibleElement serviceFeed = assignedService
							.getCommunityCalendarService(service_url);
					assertTrue(serviceFeed != null);

					// retrieve calendar
					LOGGER.debug("Getting Community Calendar:");
					String calendar_url = service_url.replace(
							"calendar/service?", "calendar?");
					ExtensibleElement calendarFeed = assignedService
							.getCommunityCalendarService(calendar_url);
					assertTrue(calendarFeed != null);

					String role = calendarFeed.getExtension(
							StringConstants.SNX_MAP_ROLE).getText();

					if (!role.equalsIgnoreCase("reader")) {
						// post calendar event
						Event event = new Event("My event",
								StringConstants.STRING_YES_LOWERCASE, null,
								null, null, "2012-08-23T02:00:00.000Z",
								"2012-08-23T03:00:00.000Z", 0);
						String eventUrl = event_url.replace(
								"type=event&calendarUuid", "calendarUuid");
						Date create = new Date(System.currentTimeMillis() - 3
								* 24 * 60 * 60 * 1000);
						event.setPublished(create);
						ExtensibleElement eventFeed;
						Entry result = (Entry) service.postCalendarEvent(
								eventUrl, event);
						assertEquals(
								"create Calendar event impersonation failed",
								201, service.getRespStatus());
						assertEquals(
								"create Calendar event impersonation person failed",
								StringConstants.USER_REALNAME, result
										.getAuthor().getName());
						assertEquals(
								"create Calendar event impersonation time failed",
								create.toString(), result.getPublished()
										.toString());

						LOGGER.debug("POST Calendar Event.");
						event = new Event("My events",
								StringConstants.STRING_NO_LOWERCASE,
								StringConstants.STRING_WEEKLY, "1",
								"2012-10-24T02:00:00.000Z",
								"2012-08-24T02:00:00.000Z",
								"2012-08-24T03:00:00.000Z", 0);
						result = (Entry) service.postCalendarEvent(eventUrl,
								event);
						assertTrue(result != null);
						assertEquals(
								"create Calendar event impersonation person failed",
								StringConstants.USER_REALNAME, result
										.getAuthor().getName());

						LOGGER.debug("GET calendar feed with redirect");
						eventFeed = (Feed) assignedService
								.getCalendarFeedWithRedirect(eventUrl
										+ "&startDate=2012-08-26T00:00:00+08:00&endDate=2012-12-06T00:00:00+08:00");
						assertTrue(eventFeed != null);

						for (Entry eEntry : ((Feed) eventFeed).getEntries()) {
							String selfUrl = eEntry.getSelfLinkResolvedHref()
									.toString();
							eventFeed = assignedService.getCalendarFeed(selfUrl);
							assertTrue(eventFeed != null);

							LOGGER.debug("GET Calendar event.");
							String attendUrl = eEntry.getLinkResolvedHref(
									StringConstants.REL_E_ATTEND).toString();
							eventFeed = assignedService
									.getCalendarFeed(attendUrl
											+ "&startDate=2011-12-26T00:00:00+08:00&endDate=2012-12-06T00:00:00+08:00");
							assertTrue(eventFeed != null);

							LOGGER.debug("GET Follow event.");
							String followUrl = eEntry.getLinkResolvedHref(
									StringConstants.REL_E_FOLLOW).toString();
							eventFeed = assignedService
									.getCalendarFeed(followUrl
											+ "&startDate=2011-12-26T00:00:00+08:00&endDate=2012-12-06T00:00:00+08:00");
							assertTrue(eventFeed != null);

							if (eEntry.getLinkResolvedHref(StringConstants.REL_E_INSTANCES) != null) { 
								// in after create == null here
								String instancesUrl = eEntry
										.getLinkResolvedHref(
												StringConstants.REL_E_INSTANCES)
										.toString();
								LOGGER.debug("GET instance event.");
								eventFeed = assignedService
										.getCalendarFeed(instancesUrl);
								assertTrue(eventFeed != null);
							}
							if (eEntry.getLinkResolvedHref(StringConstants.REL_E_PARENTEVENT) != null) {
								String parentUrl = eEntry.getLinkResolvedHref(
										StringConstants.REL_E_PARENTEVENT)
										.toString();
								LOGGER.debug("GET parent event.");
								eventFeed = assignedService
										.getCalendarFeed(parentUrl);
								assertTrue(eventFeed != null);
							}

							LOGGER.debug("GET create comment on event.");
							String commentUrl = eEntry.getLinkResolvedHref(
									StringConstants.REL_EDIT).toString();
							commentUrl = commentUrl.replace("/event?",
									"/event/comment?");

							CommentToEvent comment = new CommentToEvent(
									"My comment");
							Entry commentResult = (Entry) service
									.postEventComment(commentUrl, comment);
							assertTrue(commentResult != null);
							assertTrue(commentResult.getContent()
									.equalsIgnoreCase("My comment"));
							assertEquals(
									"create Calendar comment impersonation person failed",
									StringConstants.USER_REALNAME, result
											.getAuthor().getName());
							String mycommentUrl = commentResult
									.getSelfLinkResolvedHref().toString();

							eventFeed = assignedService
									.getCalendarFeed(mycommentUrl);
							assertTrue(eventFeed != null);
							assertTrue(((Entry) eventFeed).getContent()
									.equalsIgnoreCase("My comment"));

							break;
						}
					}
				}
			}
		}

		LOGGER.debug("Finished Community Calendar event...");
	}

	@Test
	public void testRelatedCommunitiesByImpersonatedUser() throws Exception {
		/*
		 * Test Related communities 
		 * Step 1: Create two communities for test, one is a community, the other will be the related community. 
		 * Step 2: Establish related community relationship 
		 * Step 3: Validate related community 
		 * Step 4: Update related community Step 5: Validate updated related community
		 */
		LOGGER.debug("BEGINNING TEST: Get Feed of Related Communities");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String commName = "Community1 " + timeStamp;
		String relatedcommName = "Related Community " + timeStamp;

		LOGGER.debug("Step 1: Create two communities for test");
		Community newCommunity = null;
		Community relatedCommunity = null;
		newCommunity = new Community(commName,
				"Official Connections Community of the Boston Celtics",
				Permissions.PUBLIC, "tagCommunities_"
						+ Utils.logDateFormatter.format(new Date()));
		relatedCommunity = new Community(relatedcommName,
				"Official Connections Community of the Boston Bruins",
				Permissions.PUBLIC, "tagCommunities_"
						+ Utils.logDateFormatter.format(new Date()));

		Entry communityResult = (Entry) otherUserService
				.createCommunity(newCommunity);
		assertEquals("create community failed", 201,
				otherUserService.getRespStatus());
		Entry relCommunityResult = (Entry) otherUserService
				.createCommunity(relatedCommunity);
		assertEquals("create community failed", 201,
				otherUserService.getRespStatus());

		// Get both two community entries
		Community comm = new Community(
				(Entry) otherUserService.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		Community relatedComm = new Community(
				(Entry) otherUserService.getCommunity(relCommunityResult
						.getEditLinkResolvedHref().toString()));

		// Get remoteApp feed
		Feed remoteAppsFeed = (Feed) otherUserService.getCommunityRemoteAPPs(
				comm.getRemoteAppsListHref(), true, null, 0, 50, null, null,
				SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);
		// check if the relatedCommunity widget is on the feed
		boolean related_enabled = false;
		for (Entry entry : remoteAppsFeed.getEntries()) {
			for (Category category : entry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("RelatedCommunities")) {
					related_enabled = true;
				}
			}
		}

		// If it is not in the list then add it
		if (!related_enabled) {
			Widget widget = new Widget(
					StringConstants.WidgetID.RelatedCommunities.toString());
			otherUserService.postWidget(comm, widget.toEntry());
			assertEquals(201, otherUserService.getRespStatus());
		}

		// Construct Related Community Entry
		Date createTime = new Date(System.currentTimeMillis() - 3 * 60 * 60
				* 24 * 1000);
		Entry relatedCommEntry = relatedCommunity.toEntry();
		String alternateLink = relatedComm.getAlternateLink();
		relatedCommEntry.addLink(alternateLink,
				"http://www.ibm.com/xmlns/prod/sn/related-community",
				"text/html", null, null, 0);
		relatedCommEntry.setPublished(createTime);

		// set Community term to Related Community
		for (Category c : relatedCommEntry.getCategories()) {
			if (c.getLabel() != null && c.getLabel().equals("Community"))
				c.setTerm("relatedCommunity");
		}

		LOGGER.debug("Step 2: Establish related community relationship");
		Entry result = (Entry) service.createRelatedCommunity(comm.getUuid(),
				relatedCommEntry);
		assertEquals("create related community failed", 201,
				service.getRespStatus());
		assertEquals("create related community impersonation time failed",
				createTime.toString(), result.getPublished().toString());
		assertEquals("create related community impersonation failed",
				imUser.getRealName(), result.getAuthor().getName());

		String idString = result.getId().toString();
		String relatedCommId = idString
				.substring(idString.lastIndexOf(':') + 1);

		// Retrieve feed of Related Communities list
		Feed relatedCommunities = (Feed) service
				.getRelatedCommunitiesFeed(comm.getUuid());

		String resultIdString = relatedCommunities.getEntries().get(0).getId()
				.toString();
		String resultCommId = resultIdString.substring(resultIdString
				.lastIndexOf(':') + 1);

		LOGGER.debug("Step 3: Validate related community");
		if (relatedCommunities.getTitle().equals(
				"Related Communities of " + commName)
				&& resultCommId.equals(relatedCommId)) {
			LOGGER.debug("SUCCESS: Feed of Related Communities Retrieved");
		} else {
			LOGGER.warn("ERROR: Feed of Related Communities Not Found");
			assertTrue("Verify related community failed", false);
		}

		// Retrieve entry of a Related Community - notice this uses the user who
		// created the both communities.
		// Should the user who established the related community also be able to
		// execute this operation?
		// The original version of this test used 'service' not
		// 'otherUserService'.
		Entry relatedCommunityResult = (Entry) otherUserService
				.retrieveRelatedCommunity(relatedCommId);
		resultIdString = relatedCommunityResult.getId().toString();
		String resultId = resultIdString.substring(resultIdString
				.lastIndexOf(':') + 1);

		// validate
		if (relatedCommunityResult.getTitle().equals(relatedcommName)
				&& resultId.equals(relatedCommId)) {
			LOGGER.debug("SUCCESS: Correct related Community Retrieved");
		} else {
			LOGGER.debug("ERROR: Invalid Related Communtiy Retrieved");
			assertTrue("Verify related community failed", false);
		}

		LOGGER.debug("Step 4: Update related community");
		relatedCommEntry.setTitle("New England Patriots");
		Date updateTime = new Date(System.currentTimeMillis() - 2 * 60 * 60
				* 24 * 1000);
		relatedCommEntry.setUpdated(updateTime);
		Entry updateResult = (Entry) service.updateRelatedCommunity(
				relatedCommId, relatedCommEntry);

		String updateIdString = updateResult.getId().toString();
		String updateCommId = updateIdString.substring(updateIdString
				.lastIndexOf(':') + 1);

		assertEquals("Update related community failed", 200,
				service.getRespStatus());
		assertEquals("Update related community impersonation time failed",
				updateTime.toString(), updateResult.getUpdated().toString());
		assertEquals("Update related community impersonation failed",
				imUser.getRealName(), updateResult.getContributors().get(0)
						.getName());

		LOGGER.debug("Step 5: Validate updated related community.");
		if (updateResult.getTitle().equals("New England Patriots")
				&& updateCommId.equals(relatedCommId)) {
			LOGGER.debug("SUCCESS: Related Community was update correctly");
		} else {
			LOGGER.warn("ERROR: Related Community was NOT updated correctly");
			assertTrue("Related Community was NOT updated", false);
		}
	}

	/*
	 * TJB 2.9.15 Once the activities cross org test works, copy it here. This
	 * test is incomplete. Commented out for now.
	 * 
	 * Impersonation cross org test.
	 */
	// @Test
	public void crossOrgTest() throws FileNotFoundException, IOException {
		LOGGER.debug("Beginning test RTC 136834: Cross org operations");
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			String impersonationHeaderKey = "X-LConn-RunAs";
			String impersonationHeaderValue_userId = "userId";
			/*
			 * TJB 2/9/15 We do not need these headers but do not delete these,
			 * we may need them for future testing.
			 * 
			 * String impersonationHeaderValue_userEmail = "userEmail"; String
			 * impersonationHeaderValue_userName = "userName"; String
			 * impersonationHeaderValue_userOrg = "userOrg";
			 */

			int ORG_B_ADMIN_USER_INDEX = 15; // Org b regular user

			UserPerspective orgBAdmin=null;
			try {
				orgBAdmin = new UserPerspective(
						ORG_B_ADMIN_USER_INDEX, Component.COMMUNITIES.toString(),
						useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CommunitiesService orgBAdminService = orgBAdmin
					.getCommunitiesService();

			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
			String communityName = "Admin user community " + uniqueNameAddition;
			ProfileData impersonatedUser = ProfileLoader.getProfile(2); // Org A
																		// regular
																		// user.

			orgBAdminService.addRequestOption(
					impersonationHeaderKey,
					impersonationHeaderValue_userId + "="
							+ impersonatedUser.getUserId());

			Community testCommunity = new Community(communityName,
					"Impersonation test, create community", Permissions.PUBLIC,
					null);
			service.createCommunity(testCommunity);

			// LOGGER.debug(service.getRequestOption("X-LConn-RunAs"));

			LOGGER.debug("End test RTC 136834: Negative test for cross org operations");
		}
	}

	@Test
	public void testForumTopics() {

		LOGGER.debug("Creating, Retrieving and Deleting Forum Topics in user's communties: ");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("Community Forum Test "
				+ uniqueNameAddition);

		String randomTitle = "Community Forum Topic API " + uniqueNameAddition;
		String randomLorem = StringUtils.join(StringConstants.LOREM_1);
		Entry resultFeed;

		LOGGER.debug("1. Pinned Forum Topics in Community: "
				+ community.getTitle());
		// Create Pinned Topic
		ForumTopic newTopicPinned = new ForumTopic(randomTitle, randomLorem,
				true, false, false, false);
		otherUserImService.createForumTopic(community, newTopicPinned);
		assertEquals(" non admin User impersonate create forumTopic ", 403,
				otherUserImService.getRespStatus());

		Entry pinnedResult = (Entry) service.createForumTopic(community,
				newTopicPinned);
		assertEquals(" admin impersonate create forumTopic ", 201,
				service.getRespStatus());
		assertEquals(" ForumsTopic title ", randomTitle,
				pinnedResult.getTitle());
		assertEquals(" ForumsTopic Content ", randomLorem.trim(), pinnedResult
				.getContent().trim());
		assertEquals(" impersonate userName not match ", imUser.getRealName(),
				pinnedResult.getAuthor().getName());
		LOGGER.debug("Created Pinned Topic: " + newTopicPinned.getTitle());

		// Retrieve
		resultFeed = (Entry) service.getForumTopic(pinnedResult.getEditLink()
				.getHref().toString());
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE ||
				StringConstants.ORGADMINGK.equalsIgnoreCase("true")) {
			assertEquals("Get ForumTopic", 200, service.getRespStatus());
		} else {
			assertEquals("Get ForumTopic", 404, service.getRespStatus());
		}

		System.out.println(pinnedResult.getEditLink().getHref().toString());
		assertTrue(resultFeed != null);
		// assertTrue(resultFeed.getTitle().equals(randomTitle)); GET not
		// working on impersonate
		// assertTrue(resultFeed.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Retrieved Pinned Topic: " + newTopicPinned.getTitle());

		// RTC 76544 Pinned Topics must be at the top in community topics
		// listing (API)
		ArrayList<ForumTopic> topics = assignedService
				.getCommunityForumTopics(community);
		for (ForumTopic forumTopic : topics) {
			assertTrue(forumTopic.getTitle().equals(randomTitle));
			assertTrue(forumTopic.getContent().trim()
					.equals(randomLorem.trim()));
			break;
		}

		// Create Pinned Reply
		randomTitle = StringGenerator.randomLorem1Sentence()
				+ StringGenerator.randomSentence(4);
		randomLorem = StringUtils.join(StringConstants.LOREM_1);

		ForumReply pinnedReply = new ForumReply(randomTitle, randomLorem,
				pinnedResult, false);
		Entry pinnedReplyResult = (Entry) service.createForumReply(
				pinnedResult, pinnedReply);
		assertEquals(" reply forum ", 201, service.getRespStatus());
		assertEquals(" impersonate userName not match ", imUser.getRealName(),
				pinnedReplyResult.getAuthor().getName());


		otherUserImService.createForumReply(pinnedResult, pinnedReply);
		assertEquals(" non admin User impersonate reply forum ", 403,
				otherUserImService.getRespStatus());

		assertTrue(pinnedReplyResult.getTitle().equals(randomTitle));
		assertTrue(pinnedReplyResult.getContent().trim()
				.equals(randomLorem.trim()));
		LOGGER.debug("Created Pinned Topic Reply: " + pinnedReply.getTitle());
		// Retrieve
		resultFeed = (Entry) assignedService.getForumTopic(pinnedReplyResult
				.getEditLink().getHref().toString());
		assertTrue(resultFeed != null);
		assertTrue(resultFeed.getTitle().equals(randomTitle));
		assertTrue(resultFeed.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Retrieved Pinned Topic Reply: " + pinnedReply.getTitle());

		// Delete Pinned Reply
		assignedService.deleteForumReply(pinnedReplyResult.getEditLink()
				.getHref().toString());
		resultFeed = (Entry) assignedService.getForumTopic(pinnedReplyResult
				.getEditLink().getHref().toString());
		if (resultFeed.getAttributeValue(StringConstants.API_ERROR) != null) {
			LOGGER.debug("SUCCESS: Topic was deleted");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Delete Failed");
			assertTrue(false);
		}
		LOGGER.debug("Deleted Pinned Topic Reply: " + pinnedReply.getTitle());

		// Delete Pinned Topic
		assignedService.deleteForumTopic(pinnedResult.getEditLink().getHref()
				.toString());
		resultFeed = (Entry) assignedService.getForumTopic(pinnedResult
				.getEditLink().getHref().toString());
		if (resultFeed.getAttributeValue(StringConstants.API_ERROR) != null) {
			LOGGER.debug("SUCCESS: Topic was deleted");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Delete Failed");
			assertTrue(false);
		}
		LOGGER.debug("Deleted Pinned Topic: " + newTopicPinned.getTitle());

		LOGGER.debug("2. Locked Forum Topics in Community: "
				+ community.getTitle());
		// Create Locked Topic
		randomTitle = StringGenerator.randomLorem1Sentence()
				+ StringGenerator.randomSentence(4);
		randomLorem = StringUtils.join(StringConstants.LOREM_1);

		ForumTopic newTopicLocked = new ForumTopic(randomTitle, randomLorem,
				false, true, false, false);
		Entry lockedResult = (Entry) service.createForumTopic(community,
				newTopicLocked);
		assertTrue(lockedResult != null);
		assertTrue(lockedResult.getTitle().equals(randomTitle));
		assertTrue(lockedResult.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Created Locked Topic: " + newTopicLocked.getTitle());
		// Retrieve
		resultFeed = (Entry) assignedService.getForumTopic(lockedResult
				.getEditLink().getHref().toString());
		assertTrue(resultFeed != null);
		assertTrue(resultFeed.getTitle().equals(randomTitle));
		assertTrue(resultFeed.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Retrieved Locked Topic: " + newTopicLocked.getTitle());

		// TODO: need to make sure you are Community owner, other wise may not
		// able to reply to locked forum topic
		// Create Locked Reply
		randomTitle = StringGenerator.randomLorem1Sentence()
				+ StringGenerator.randomSentence(4);
		randomLorem = StringUtils.join(StringConstants.LOREM_1);
		ForumReply lockedReply = new ForumReply(randomTitle, randomLorem,
				lockedResult, false);
		Entry lockedReplyResult = (Entry) service.createForumReply(
				lockedResult, lockedReply);
		assertTrue(lockedReplyResult != null);
		assertTrue(lockedReplyResult.getTitle().equals(randomTitle));
		assertTrue(lockedReplyResult.getContent().trim()
				.equals(randomLorem.trim()));
		LOGGER.debug("Created Locked Topic Reply: " + lockedReply.getTitle());
		// Retrieve
		resultFeed = (Entry) assignedService.getForumTopic(lockedReplyResult
				.getEditLink().getHref().toString());
		assertTrue(resultFeed != null);
		assertTrue(resultFeed.getTitle().equals(randomTitle));
		assertTrue(resultFeed.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Retrieved Locked Topic Reply: " + lockedReply.getTitle());

		// Delete Locked Reply
		assignedService.deleteForumReply(lockedReplyResult.getEditLink()
				.getHref().toString());
		resultFeed = (Entry) assignedService.getForumTopic(lockedReplyResult
				.getEditLink().getHref().toString());
		if (resultFeed.getAttributeValue(StringConstants.API_ERROR) != null) {
			LOGGER.debug("SUCCESS: Topic was deleted");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Delete Failed");
			assertTrue(false);
		}
		LOGGER.debug("Deleted Locked Topic Reply: " + lockedReply.getTitle());

		// Delete Locked Topic
		assignedService.deleteForumTopic(lockedResult.getEditLink().getHref()
				.toString());
		resultFeed = (Entry) assignedService.getForumTopic(lockedResult
				.getEditLink().getHref().toString());
		if (resultFeed.getAttributeValue(StringConstants.API_ERROR) != null) {
			LOGGER.debug("SUCCESS: Topic was deleted");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Delete Failed");
			assertTrue(false);
		}
		LOGGER.debug("Deleted Locked Topic Reply: " + newTopicLocked.getTitle());

		LOGGER.debug("3. Question Forum Topics in Community: "
				+ community.getTitle());
		// Create Question Topic
		randomTitle = StringGenerator.randomLorem1Sentence()
				+ StringGenerator.randomSentence(4);
		randomLorem = StringUtils.join(StringConstants.LOREM_1);

		ForumTopic newTopicQuestion = new ForumTopic(randomTitle, randomLorem,
				false, false, true, false);
		Entry questionResult = (Entry) service.createForumTopic(community,
				newTopicQuestion);
		assertTrue(questionResult != null);
		assertTrue(questionResult.getTitle().equals(randomTitle));
		assertTrue(questionResult.getContent().trim()
				.equals(randomLorem.trim()));
		LOGGER.debug("Created Question Topic: " + newTopicQuestion.getTitle());
		// Retrieve
		resultFeed = (Entry) assignedService.getForumTopic(questionResult
				.getEditLink().getHref().toString());
		assertTrue(resultFeed != null);
		assertTrue(resultFeed.getTitle().equals(randomTitle));
		assertTrue(resultFeed.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Retrieved Question Topic: " + newTopicQuestion.getTitle());

		// Create Question Reply
		randomTitle = StringGenerator.randomLorem1Sentence()
				+ StringGenerator.randomSentence(4);
		randomLorem = StringUtils.join(StringConstants.LOREM_1);

		ForumReply questionReply2 = new ForumReply(randomTitle, randomLorem,
				questionResult, false);
		Entry questionReply2Result = (Entry) service.createForumReply(
				questionResult, questionReply2);
		assertTrue(questionReply2Result != null);
		assertTrue(questionReply2Result.getTitle().equals(randomTitle));
		assertTrue(questionReply2Result.getContent().trim()
				.equals(randomLorem.trim()));
		LOGGER.debug("Created Question Topic Reply 1: "
				+ questionReply2.getTitle());
		// Retrieve
		resultFeed = (Entry) assignedService.getForumTopic(questionReply2Result
				.getEditLink().getHref().toString());
		assertTrue(resultFeed != null);
		assertTrue(resultFeed.getTitle().equals(randomTitle));
		assertTrue(resultFeed.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Retrieved Question Topic Reply 1: "
				+ questionReply2.getTitle());

		randomTitle = StringGenerator.randomLorem1Sentence()
				+ StringGenerator.randomSentence(4);
		randomLorem = StringUtils.join(StringConstants.LOREM_1);

		ForumReply questionReply = new ForumReply(randomTitle, randomLorem,
				questionResult, true);
		Entry questionReplyResult = (Entry) service.createForumReply(
				questionResult, questionReply);
		assertTrue(questionReplyResult != null);
		assertTrue(questionReplyResult.getTitle().equals(randomTitle));
		assertTrue(questionReplyResult.getContent().trim()
				.equals(randomLorem.trim()));
		LOGGER.debug("Created Question Topic Reply 2: "
				+ questionReply.getTitle());

		resultFeed = (Entry) assignedService.getForumTopic(questionReplyResult
				.getEditLink().getHref().toString());
		assertTrue(resultFeed != null);
		assertTrue(resultFeed.getTitle().equals(randomTitle));
		assertTrue(resultFeed.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Retrieved Question Topic Reply 2: "
				+ questionReply.getTitle());

		// Delete Question Reply
		assignedService.deleteForumTopic(questionReply2Result.getEditLink()
				.getHref().toString());
		resultFeed = (Entry) assignedService.getForumTopic(questionReply2Result
				.getEditLink().getHref().toString());
		if (resultFeed.getAttributeValue(StringConstants.API_ERROR) != null) {
			LOGGER.debug("SUCCESS: Topic was deleted");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Delete Failed");
			assertTrue(false);
		}
		LOGGER.debug("Retrieved Question Topic Reply 1: "
				+ questionReply2.getTitle());

		// Delete Question Topic
		assignedService.deleteForumTopic(questionResult.getEditLink().getHref()
				.toString());
		resultFeed = (Entry) assignedService.getForumTopic(questionResult
				.getEditLink().getHref().toString());
		if (resultFeed.getAttributeValue(StringConstants.API_ERROR) != null) {
			LOGGER.debug("SUCCESS: Topic was deleted");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Delete Failed");
			assertTrue(false);
		}
		LOGGER.debug("Deleted Question Topic: " + newTopicQuestion.getTitle());

		LOGGER.debug("COMPLETED TEST FOR FORUM TOPICS");
	}

	@Test
	public void testForumTopicUpdate() throws MalformedURLException,
			URISyntaxException {
		LOGGER.debug("BEGINNING TEST: Updating Community Forum Topic.");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		Community community = getCreatedPrivateCommunity("Community Forum Test for update"
				+ uniqueNameAddition);

		String randomTitle = "Community Forum Topic update test "
				+ uniqueNameAddition;
		String randomLorem = StringUtils.join(StringConstants.LOREM_1);

		LOGGER.debug("1. Pinned Forum Topics in Community: "
				+ community.getTitle());
		// Create Pinned Topic
		ForumTopic newTopicPinned = new ForumTopic(randomTitle, randomLorem,
				true, false, false, false);
		otherUserImService.createForumTopic(community, newTopicPinned);
		assertEquals(" non admin User impersonate create forumTopic ", 403,
				otherUserImService.getRespStatus());
		Entry pinnedResult = (Entry) service.createForumTopic(community,
				newTopicPinned);
		assertTrue(pinnedResult != null);
		assertTrue(pinnedResult.getTitle().equals(randomTitle));
		assertTrue(pinnedResult.getContent().trim().equals(randomLorem.trim()));
		assertEquals(" impersonate userName not match ", imUser.getRealName(),
				pinnedResult.getAuthor().getName());
		LOGGER.debug("Created Pinned Topic: " + newTopicPinned.getTitle());

		// Change the topic title using PUT
		String updatedTitle = "UPDATED Forum Topic API " + uniqueNameAddition;
		newTopicPinned.setTitle(updatedTitle);
		Entry updateResult = (Entry) service.editForumTopic(pinnedResult
				.getEditLinkResolvedHref().toURL().toString(), newTopicPinned);

		// Retrieve and validate. The title should the updated version. Author
		// should be impersonated user.
		Entry resultEntry = (Entry) assignedService.getForumTopic(pinnedResult
				.getEditLink().getHref().toString());
		assertEquals("Updated title not returned ", true, resultEntry
				.getTitle().equalsIgnoreCase(updatedTitle));
		assertEquals(" impersonated user name does not match ",
				imUser.getRealName(), resultEntry.getAuthor().getName());

		LOGGER.debug("ENDING TEST: Updating Community Forum Topic.");

	}

	// @Test
	public void createCommunityActivityCrossOrg() throws Exception {
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		LOGGER.debug("Create Community Activity:");
		ExtensibleElement eEle;

		// create community
		Community newCommunity = null;
		String communityName = "Create Community Activity "
				+ uniqueNameAddition;
		newCommunity = new Community(communityName,
				"Simple test for community based Activity",
				Permissions.PRIVATE, null);

		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		// get created community
		Entry communityEntry = (Entry) service
				.getCommunity(communityResult.getEditLinkResolvedHref()
						.toString());
		Community comm = new Community(communityEntry);

		// created Communities Activities widget
		Widget widget = new Widget(
				StringConstants.WidgetID.Activities.toString());
		eEle = service.postWidget(comm, widget.toEntry());
		assertEquals("Add activity widget", 201, service.getRespStatus());

		String activitiesUrl = "";
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				comm.getRemoteAppsListHref(), true, null, 0, 50, null, null,
				SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);
		for (Entry ntry : remoteAppsFeed.getEntries()) {
			if (ntry.getTitle().equalsIgnoreCase("activities")) {
				for (Element ele : ntry.getElements()) {
					if (ele.getAttributeValue("rel") != null) {
						if (ele.getAttributeValue("rel").contains(
								"remote-application/feed")) {
							activitiesUrl = ele.getAttributeValue("href");
						}
					}
				}
			}
		}

		// Create an Activity using standard impersonation user. This should
		// work
		Factory factory = abdera.getFactory();
		Entry activityEntry = factory.newEntry();
		activityEntry
				.setTitle("Activity created in Communities, API - standard impersonation user.");
		activityEntry.setContent("test simple activity created in communities");
		activityEntry.addCategory(StringConstants.SCHEME_TYPE,
				"community_activity", "Community Activity");

		eEle = service.postEntry(activitiesUrl, activityEntry);

		assertEquals(" add activity ", 201, service.getRespStatus());
		assertEquals(" add activity impersonation person failed",
				StringConstants.USER_REALNAME, ((Entry) eEle).getAuthor()
						.getName());

		// Try to create an activity using regular user from another org.
		// Create an Activity
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			Entry activityEntry2 = factory.newEntry();
			activityEntry2
					.setTitle("Activity created in Communities Cross Org");
			activityEntry2
					.setContent("test simple activity created in communities cross org impersonation.");
			activityEntry2.addCategory(StringConstants.SCHEME_TYPE,
					"community_activity", "Community Activity");

			// Setup code for the out of org user:
			int ORG_B_REGULAR_USER_INDEX = 15;
			String impersonationHeaderKey = "X-LConn-RunAs";
			String impersonationHeaderValue_userId = "userId";
			boolean useSSL = true;

			// Org B regular user - Jill White
			UserPerspective orgBRegular = new UserPerspective(
					ORG_B_REGULAR_USER_INDEX, Component.COMMUNITIES.toString(),
					useSSL);

			LOGGER.debug("Before setting the request option, X-LConn-RunAs : "
					+ service.getRequestOption("X-LConn-RunAs"));

			// Org A Admin uses Org B regular user for impersonation.
			service.addRequestOption(
					impersonationHeaderKey,
					impersonationHeaderValue_userId + "="
							+ orgBRegular.getUserId());
			LOGGER.debug("After setting the request option, X-LConn-RunAs : "
					+ service.getRequestOption("X-LConn-RunAs"));

			// TODO is 500 the expected result here? Or is this a bug?
			Entry activityResult2 = (Entry) service.postEntry(activitiesUrl,
					activityEntry2);
			assertEquals(" add activity ", 500, service.getRespStatus());

		}
	}

	@Test
	public void ideationDuplicate() throws Exception {
		LOGGER.debug("BEGINNING TEST: RTC 89036 API to mark an idea as duplicate");
		/*
		 * Steps: 
		 * 1. Create Community 
		 * 2. Add ideation blog widget 
		 * 3. Create two ideation entries, one will be marked as a duplicate of the other. 
		 * 4. Create an entry with the content that will mark an idea as a duplicate 
		 * 5. Execute the entry using http PUT. 
		 * 6. Validate
		 */

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

			// create community
			Community newCommunity = null;
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				newCommunity = new Community(
						"RTC 89036 Ideation duplicates " + uniqueNameAddition,
						"Test API ability to mark an idea as a duplicate of another",
						Permissions.PRIVATE, null);
			} else {
				newCommunity = new Community(
						"RTC 89036 Ideation duplicates " + uniqueNameAddition,
						"Test API ability to mark an idea as a duplicate of another",
						Permissions.PUBLIC, null);
			}
			Entry communityResult = (Entry) service
					.createCommunity(newCommunity);
			assertTrue(communityResult != null);

			Community comm = new Community(
					(Entry) service.getCommunity(communityResult
							.getEditLinkResolvedHref().toString()));

			// Add ideation blog widget
			Widget widget = new Widget(
					StringConstants.WidgetID.IdeationBlog.toString());
			service.postWidget(comm, widget.toEntry());
			assertEquals(201, service.getRespStatus());
			// assertEquals(200, service.addWidget(comm, "IdeationBlog"));

			// //////////////////////////////////////////////////////////////////////////////////////////////
			/*
			 * This is the start of the code to create an ideadtion blog via the
			 * widget. The process is: 1. Get a feed of Community remote apps
			 * and get the link for IdeationBlog 2. Execute the link. This will
			 * return a service doc. Parse the service doc and get the link for
			 * posting to Ideation Blogs 3. Create the Entry 4. Execute a POST
			 * for the Entry.
			 */
			Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
					comm.getRemoteAppsListHref(), true, null, 0, 50, null,
					null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
			assertTrue(remoteAppsFeed != null);

			String ideationBlogUrl = null;
			for (Entry raEntry : remoteAppsFeed.getEntries()) {
				for (Category category : raEntry.getCategories()) {
					if (category.getTerm().equalsIgnoreCase("IdeationBlog")) {
						for (Link link : raEntry.getLinks()) {
							if (link.getRel().contains(
									"remote-application/publish")) {
								ideationBlogUrl = link.getHref().toString();
							}
						}
					}
				}
			}

			// This call returns a service doc, not a feed. EE doesn't seem to
			// support direct retrieval of workspaces, collections, etc
			// so the code below does the parsing. What we are trying to get is
			// the link used to post to IdeationBlog widget in communities.
			// The blogHandleUrl is used for voting.
			String postIdeaBlogUrl = null;
			// String blogHandleUrl = null;
			ExtensibleElement ee = null;
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				// Document<Feed> feed_doc =
				// client.get(ideationBlogUrl).getDocument();
				// ee = feed_doc.getRoot();
				ee = assignedService.getAnyFeedWithRedirect(ideationBlogUrl);
			} else {
				ee = service.getAnyFeed(ideationBlogUrl);
			}
			assertTrue(ee != null);
			for (Element ele : ee.getElements()) {
				if (ele.toString().startsWith("<workspace")) {
					for (Element ele2 : ele.getElements()) {
						if (ele2.toString().startsWith("<collection")) {
							for (QName atrb : ele2.getAttributes()) {
								if (atrb.toString().equalsIgnoreCase("href")
										&& ele2.getAttributeValue("href")
												.contains("api/entries")) {
									postIdeaBlogUrl = ele2
											.getAttributeValue("href");
								}
								/*
								 * if (atrb.toString().equalsIgnoreCase("href")
								 * && ele2.getAttributeValue("href").contains(
								 * "blogType=ideationblog")){ blogHandleUrl =
								 * ele2.getAttributeValue("href"); }
								 */
							}
						}
					}
				}
			}

			Factory factory = abdera.getFactory();
			Entry entry = factory.newEntry();
			entry.setTitle("RTC 89036 The Idea");
			entry.setId("some-id-should-be-ignored");
			entry.setContent("idea1 content");
			entry.addCategory("DataPopIdeaBlogEntry");
			entry.toString();

			Entry entry2 = factory.newEntry();
			entry2.setTitle("RTC 89036 The Duplicate");
			entry2.setId("some-id-should-be-ignored");
			entry2.setContent("duplicate content");
			entry2.addCategory("DataPopIdeaBlogEntry");

			// These just create the ideation blog only - no votes/recommends
			service.postIdeationBlog(postIdeaBlogUrl, entry);
			service.postIdeationBlog(postIdeaBlogUrl, entry2);

			Feed fd = null;
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				// Document<Feed> feed_doc =
				// client.get(postIdeaBlogUrl).getDocument();
				// fd = feed_doc.getRoot();
				fd = (Feed) assignedService
						.getAnyFeedWithRedirect(postIdeaBlogUrl);
			} else {
				fd = (Feed) assignedService.getAnyFeed(postIdeaBlogUrl);
			}
			String duplicateEditLink = "";
			String ideaEditLink = "";

			// Get the 'edit' urls from feed
			for (Entry ibEntry : fd.getEntries()) {
				if (ibEntry.getTitle().equalsIgnoreCase(
						"RTC 89036 The Duplicate")) {
					for (Element ele : ibEntry.getElements()) {
						for (QName atrb : ele.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("rel")
									&& ele.getAttributeValue("rel")
											.equalsIgnoreCase("edit")) {
								duplicateEditLink = ele
										.getAttributeValue("href");
							}
						}
					}
				}
				if (ibEntry.getTitle().equalsIgnoreCase("RTC 89036 The Idea")) {
					for (Element ele : ibEntry.getElements()) {
						for (QName atrb : ele.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("rel")
									&& ele.getAttributeValue("rel")
											.equalsIgnoreCase("edit")) {
								ideaEditLink = ele.getAttributeValue("href");
							}
						}
					}
				}
			}
			// duplicateEditLink
			// https://lc45linux2.swg.usma.ibm.com:443/blogs/Wece42f8f768a_4a22_be19_c0a8fcfb11ca/api/entries/819a6435-c595-462c-aad2-1954197413bb
			String dupEntryId = duplicateEditLink.substring(
					duplicateEditLink.lastIndexOf("/") + 1,
					duplicateEditLink.length());

			String ideaEntryId = ideaEditLink.substring(
					ideaEditLink.lastIndexOf("/") + 1, ideaEditLink.length());
			String wId = ideaEditLink.substring(ideaEditLink.indexOf("blogs/"),
					ideaEditLink.indexOf("/api/entries/"));
			String server = URLConstants.SERVER_URL;
			String formattedIdeaLink = server + "/" + wId
					+ "/feed/entry/atom?entryid=" + ideaEntryId;

			// Create the entry containing element and attributes to mark an
			// idea as a duplicate.
			Entry entryForDuplicate = factory.newEntry();
			entryForDuplicate.setId("urn:lsid:ibm.com:blogs:entry-"
					+ dupEntryId);
			entryForDuplicate.setTitle("RTC 89036 The Duplicate");
			entryForDuplicate.setContent("duplicate content");
			entryForDuplicate.addLink(formattedIdeaLink,
					"http://www.ibm.com/xmlns/prod/sn/related/duplicateto",
					"application/atom+xml", "", "", 0);

			// TB SmartCloud stops here. 403 forbidden returned on the next
			// line. Comment out for now.
			// if (! StringConstants.DEPLOYMENT_TYPE ==
			// DeploymentType.SMARTCLOUD) {
			ExtensibleElement ee2 = service.putEntry(duplicateEditLink,
					entryForDuplicate);
			assertTrue(ee2 != null);

			// Validation - Get a feed. Make sure the duplicate is not included.
			// This is a UI url, but it's a good way to
			// make sure the duplicate idea is not "visible".
			String validationUrl = server + "/blogs/roller-ui/rendering/feed/"
					+ wId.substring(wId.indexOf('/') + 1) + "/entries/atom";

			Feed fd2 = null;
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				// Document<Feed> feed_doc =
				// client.get(validationUrl).getDocument();
				// fd2 = feed_doc.getRoot();
				fd2 = (Feed) service.getAnyFeedWithRedirect(validationUrl);
			} else {
				fd2 = (Feed) service.getAnyFeed(validationUrl);
			}
			boolean foundExpectedEntry = false;
			boolean didNotFindDuplicateEntry = true;
			for (Entry ibEntry : fd2.getEntries()) {
				if (ibEntry.getTitle().equalsIgnoreCase(
						"RTC 89036 The Duplicate")) {
					// This is an error. This entry should not be in the feed
					assertFalse(ibEntry.getTitle().equalsIgnoreCase(
							"RTC 89036 The Duplicate"));
					didNotFindDuplicateEntry = false;
				}
				if (ibEntry.getTitle().equalsIgnoreCase("RTC 89036 The Idea")) {
					// This is the expected result
					assertTrue(ibEntry.getTitle().equalsIgnoreCase(
							"RTC 89036 The Idea"));
					foundExpectedEntry = true;
				}
			}

			// Final check.
			assertTrue(foundExpectedEntry && didNotFindDuplicateEntry);

			LOGGER.debug("END TEST: RTC 89036 API to mark an idea as duplicate");
		} // End SmartCloud 'if'
			// }// TB 9/16/13
	}

	@Test
	public void ideationAppAccept() throws Exception {
		LOGGER.debug("BEGINNING TEST: RTC 84709 'Voted for' entry recommendation ontains <app:accept/>");
		/*
		 * Steps: 
		 * 1. Create Community 
		 * 2. Add ideation blog widget 
		 * 3. Create ideation blog entries 
		 * 4. Post some votes for some entries, but not all 
		 * 5. Freeze the idea blog 
		 * 6. Validate the <app:accept/> content
		 */

		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

		// create community
		Community newCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community(
					"RTC 84709 Ideation Blog app:accept test"
							+ uniqueNameAddition,
					"'Voted for' entry recommendation section should contain <app:accept>",
					Permissions.PRIVATE, null);
		} else {
			newCommunity = new Community(
					"RTC 84709 Ideation Blog app:accept test"
							+ uniqueNameAddition,
					"'Voted for' entry recommendation section should contain <app:accept>",
					Permissions.PUBLIC, null);
		}
		Entry communityResult = (Entry) service
				.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		Community comm = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		// Add ideation blog widget
		Widget widget = new Widget(
				StringConstants.WidgetID.IdeationBlog.toString());
		service.postWidget(comm, widget.toEntry());
		assertEquals(201, service.getRespStatus());
		// assertEquals(200, service.addWidget(comm, "IdeationBlog"));

		// //////////////////////////////////////////////////////////////////////////////////////////////
		/*
		 * This is the start of the code to create an ideadtion blog via the
		 * widget. The process is: 1. Get a feed of Community remote apps and
		 * get the link for IdeationBlog 2. Execute the link. This will return a
		 * service doc. Parse the service doc and get the link for posting to
		 * Ideation Blogs 3. Create the Entry 4. Execute a POST for the Entry.
		 */
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				comm.getRemoteAppsListHref(), true, null, 0, 50, null, null,
				SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);

		String ideationBlogUrl = null;
		for (Entry raEntry : remoteAppsFeed.getEntries()) {
			for (Category category : raEntry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("IdeationBlog")) {
					for (Link link : raEntry.getLinks()) {
						if (link.getRel()
								.contains("remote-application/publish")) {
							ideationBlogUrl = link.getHref().toString();
						}
					}
				}
			}
		}

		// This call returns a service doc, not a feed. EE doesn't seem to
		// support direct retrieval of workspaces, collections, etc
		// so the code below does the parsing. What we are trying to get is the
		// link used to post to IdeationBlog widget in communities.
		// The blogHandleUrl is used for voting.
		String postIdeaBlogUrl = null;
		String blogHandleUrl = null;
		ExtensibleElement ee = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ee = assignedService.getAnyFeedWithRedirect(ideationBlogUrl);
		} else {
			ee = assignedService.getAnyFeed(ideationBlogUrl);
		}
		assertTrue(ee != null);
		for (Element ele : ee.getElements()) {
			if (ele.toString().startsWith("<workspace")) {
				for (Element ele2 : ele.getElements()) {
					if (ele2.toString().startsWith("<collection")) {
						for (QName atrb : ele2.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("href")
									&& ele2.getAttributeValue("href").contains(
											"api/entries")) {
								postIdeaBlogUrl = ele2
										.getAttributeValue("href");
							}
							if (atrb.toString().equalsIgnoreCase("href")
									&& ele2.getAttributeValue("href").contains(
											"blogType=ideationblog")) {
								blogHandleUrl = ele2.getAttributeValue("href");
							}
						}
					}
				}
			}
		}

		// Create the entries. Compared to Janet tests, the only bit missing
		// from the entry is this:
		/*
		 * <app:control> <app:draft>no</app:draft> <snx:comments
		 * xmlns:snx="http://www.ibm.com/xmlns/prod/sn" enabled="yes" days="0"/>
		 * </app:control>
		 */

		Factory factory = abdera.getFactory();
		Entry entry = factory.newEntry();
		entry.setTitle("RTC84709 Idea1 " + uniqueNameAddition);
		entry.setId("some-id-should-be-ignored");
		entry.setContent("idea1 content");
		entry.addCategory("DataPopIdeaBlogEntry");
		entry.toString();

		Entry entry2 = factory.newEntry();
		entry2.setTitle("RTC84709 Idea2 " + uniqueNameAddition);
		entry2.setId("some-id-should-be-ignored");
		entry2.setContent("idea2 content");
		entry2.addCategory("DataPopIdeaBlogEntry");

		Entry entry3 = factory.newEntry();
		entry3.setTitle("RTC84709 Idea3 " + uniqueNameAddition);
		entry3.setId("some-id-should-be-ignored");
		entry3.setContent("idea3 content");
		entry3.addCategory("DataPopIdeaBlogEntry");

		// These just create the ideation blog only - no votes/recommends
		ExtensibleElement eePost = service.postIdeationBlog(postIdeaBlogUrl,
				entry);
		service.postIdeationBlog(postIdeaBlogUrl, entry2);
		service.postIdeationBlog(postIdeaBlogUrl, entry3);

		/*
		 * This next section of code creates votes. blogHandleUrl was defined
		 * earlier in the code.
		 */
		Feed feed = null;
		String blogDashboardUrl = blogHandleUrl.substring(0,
				blogHandleUrl.indexOf("api/blogs"))
				+ "feed/entries/atom";
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			String wIdExtra = postIdeaBlogUrl.substring(
					postIdeaBlogUrl.indexOf("blogs/"),
					postIdeaBlogUrl.indexOf("/api"));
			String wId = wIdExtra.substring(wIdExtra.indexOf("/") + 1);
			// postIdeaBlogUrl
			// https://apps.collabservdaily.swg.usma.ibm.com/blogs/W4c9e0c0a8a3b_45de_ace5_74ff33a02eea/api/entries
			String urlIdeationFeedUrl = URLConstants.SERVER_URL
					+ "/blogs/roller-ui/rendering/feed/" + wId
					+ "/entries/atom";

			// need this
			// /blogs/roller-ui/rendering/feed/W7da2e3463740_4c6d_9c18_452f0ff7c7ac/entries/atom?lang=en_us
			feed = (Feed) assignedService
					.getAnyFeedWithRedirect(urlIdeationFeedUrl);
		} else {
			feed = (Feed) assignedService.getAnyFeed(blogDashboardUrl);
		}

		String idea1RecommendUrl = null;
		String idea2RecommendUrl = null;
		for (Entry ibEntry : feed.getEntries()) {
			if (ibEntry.getTitle().equalsIgnoreCase(
					"RTC84709 Idea1 " + uniqueNameAddition)) {
				for (Element ele : ibEntry.getElements()) {
					if (ele.toString().startsWith("<app:collection")) {
						for (QName atrb : ele.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("href")
									&& ele.getAttributeValue("href").contains(
											"api/recommend/entries")) {
								idea1RecommendUrl = ele
										.getAttributeValue("href");
							}
						}
					}
				}
			}
			if (ibEntry.getTitle().equalsIgnoreCase(
					"RTC84709 Idea2 " + uniqueNameAddition)) {
				for (Element ele : ibEntry.getElements()) {
					if (ele.toString().startsWith("<app:collection")) {
						for (QName atrb : ele.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("href")
									&& ele.getAttributeValue("href").contains(
											"api/recommend/entries")) {
								idea2RecommendUrl = ele
										.getAttributeValue("href");
							}
						}
					}
				}
			}
		}

		// Post the votes
		ExtensibleElement eer1 = service
				.postIdeationRecommendation(idea1RecommendUrl);
		ExtensibleElement eer2 = service
				.postIdeationRecommendation(idea2RecommendUrl);

		/* This next section of code PUTs a freeze on ideablog. */
		String editUrl = "";
		Feed fd = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			// Document<Feed> feed_doc =
			// client.get(blogHandleUrl).getDocument();
			// fd = feed_doc.getRoot();
			fd = feed;
			for (Element ele : fd.getElements()) {
				if (ele.toString().startsWith("<link")) {
					for (QName atrb : ele.getAttributes()) {
						if (atrb.toString().equalsIgnoreCase("rel")
								&& ele.getAttributeValue("rel")
										.equalsIgnoreCase("edit")) {
							editUrl = ele.getAttributeValue("href");
						}
					}
				}
			}
		} else {
			fd = (Feed) assignedService.getAnyFeed(blogHandleUrl);

			for (Entry ibEntry : fd.getEntries()) {
				if (ibEntry.getTitle().equalsIgnoreCase(
						"RTC 84709 Ideation Blog app:accept test"
								+ uniqueNameAddition)) {
					// Get the edit link that looks like this:
					// https://lc45linux2.swg.usma.ibm.com:443/blogs/Handle03913360811/api/blogs/70241e8a-8658-4d5e-a2db-eb0b83783ab9
					editUrl = ibEntry.getEditLinkResolvedHref().toURL()
							.toString();
				}
			}
		}

		// Get yet another feed.
		ExtensibleElement eeEntry = assignedService.getAnyFeed(editUrl);
		// Change this <category term="open" to this <category term="frozen"
		for (Element ele : eeEntry.getElements()) {
			if (ele.toString().startsWith("<category")
					&& ele.toString().contains("term=\"open\"")) {
				for (QName atrb : ele.getAttributes()) {
					if (atrb.toString().equalsIgnoreCase("term")
							&& ele.getAttributeValue("term").contains("open")) {
						ele.setAttributeValue("term", "frozen");
					}
				}
			}
		}
		// Now, use this entry with the edit link with http PUT. This will
		// freeze the idea blog.
		ExtensibleElement freeze = service.putEntry(editUrl, (Entry) eeEntry);

		// Verify the contents.
		Feed dashboardFeed = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			dashboardFeed = (Feed) assignedService
					.getAnyFeedWithRedirect(blogDashboardUrl);
		} else {
			dashboardFeed = (Feed) assignedService.getAnyFeed(blogDashboardUrl);
		}
		// Feed dashboardFeed = (Feed) bService.getBlogFeed(blogDashboardUrl);
		boolean isRecommendFound = false;
		boolean isRecommendFound2 = false;
		boolean isRecommendFound3 = false;

		// app:accept should exist for idea1 and idea2, it will not have a
		// value.
		for (Entry entryOne : dashboardFeed.getEntries()) {
			if (entryOne.getTitle().equalsIgnoreCase(
					"RTC84709 Idea1 " + uniqueNameAddition)) {
				for (Element ele : entryOne.getElements()) {
					if (ele.toString().startsWith("<app:collection")) {
						for (Element ele2 : ele.getElements()) {
							if (ele2.toString().startsWith("<category"))
								if (ele2.getAttributeValue("term")
										.equalsIgnoreCase("recommend")) {
									isRecommendFound = true;
								}
							if (ele2.toString().startsWith("<app:accept")
									&& isRecommendFound) {
								assertTrue(ele2
										.toString()
										.equalsIgnoreCase(
												"<app:accept xmlns:app=\"http://www.w3.org/2007/app\" />"));
							}
						}
					}
				}
			}
		}

		for (Entry entryTwo : dashboardFeed.getEntries()) {
			if (entryTwo.getTitle().equalsIgnoreCase(
					"RTC84709 Idea2 " + uniqueNameAddition)) {
				for (Element ele : entryTwo.getElements()) {
					if (ele.toString().startsWith("<app:collection")) {
						for (Element ele2 : ele.getElements()) {
							if (ele2.toString().startsWith("<category"))
								if (ele2.getAttributeValue("term")
										.equalsIgnoreCase("recommend")) {
									isRecommendFound2 = true;
								}
							if (ele2.toString().startsWith("<app:accept")
									&& isRecommendFound2) {
								assertTrue(ele2
										.toString()
										.equalsIgnoreCase(
												"<app:accept xmlns:app=\"http://www.w3.org/2007/app\" />"));
							}
						}
					}
				}
			}
		}

		// <app:accept /> should not be included in this entry.
		for (Entry entryThree : dashboardFeed.getEntries()) {
			if (entryThree.getTitle().equalsIgnoreCase(
					"RTC84709 Idea3 " + uniqueNameAddition)) {
				for (Element ele : entryThree.getElements()) {
					if (ele.toString().startsWith("<app:collection")) {
						for (Element ele2 : ele.getElements()) {
							if (ele2.toString().startsWith("<category"))
								if (ele2.getAttributeValue("term")
										.equalsIgnoreCase("recommend")) {
									isRecommendFound3 = true;
								}
							if (ele2.toString().startsWith("<app:accept")
									&& isRecommendFound3) {
								// If execution reaches this point, there's an
								// error.
								assertFalse(ele2.toString().startsWith(
										"<app:accept"));
							}
						}
					}
				}
			}
		}

		// Finally, make sure the recommendations are actually found in the feed
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			assertEquals(true, isRecommendFound);
			assertEquals(true, isRecommendFound2);
			assertEquals(true, isRecommendFound3);
		}

		LOGGER.debug("END TEST: RTC 84709 'Voted for' entry recommendation contains <app:accept/>");
	}

	// @Test
	public void CommunityForumCrossOrg() throws FileNotFoundException,
			IOException {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("BEGINNING Community Forum Cross Org test ");
			String impersonationHeaderKey = "X-LConn-RunAs";
			String impersonationHeaderValue_userId = "userId";

			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

			Community community = getCreatedPublicCommunity("Community Forum Cross Org Test "
					+ uniqueNameAddition);

			String randomTitle = "Topic Cross Org - impersonation should work, created by impersonated user."
					+ uniqueNameAddition;
			String randomTitle2 = "Topic Cross Org part 2 - impersonation should fail, created by admin."
					+ uniqueNameAddition;
			String randomLorem = StringUtils.join(StringConstants.LOREM_1);
			Entry resultFeed;

			LOGGER.debug("1. Pinned Forum Topics in Community: "
					+ community.getTitle());
			// Create Pinned Topic
			ForumTopic newTopicPinned = new ForumTopic(randomTitle,
					randomLorem, true, false, false, false);
			Entry pinnedResult = (Entry) service.createForumTopic(community,
					newTopicPinned);
			assertTrue(pinnedResult != null);
			assertTrue(pinnedResult.getTitle().equals(randomTitle));
			assertTrue(pinnedResult.getContent().trim()
					.equals(randomLorem.trim()));
			LOGGER.debug("Created Pinned Topic: " + newTopicPinned.getTitle());

			// Retrieve
			resultFeed = (Entry) service.getForumTopic(pinnedResult
					.getEditLink().getHref().toString());
			System.out.println(pinnedResult.getEditLink().getHref().toString());
			assertTrue(resultFeed != null);
			assertTrue(resultFeed.getTitle().equals(randomTitle));
			assertTrue(resultFeed.getContent().trim()
					.equals(randomLorem.trim()));
			LOGGER.debug("Retrieved Pinned Topic: " + newTopicPinned.getTitle());

			// Create another topic using Org B user
			LOGGER.debug("Before setting the request option, X-LConn-RunAs : "
					+ service.getRequestOption("X-LConn-RunAs"));

			// Org A Admin uses Org B regular user for impersonation.
			// Org B regular user - Jill White
			int ORG_B_REGULAR_USER_INDEX = 15;
			UserPerspective orgBRegular=null;
			try {
				orgBRegular = new UserPerspective(
						ORG_B_REGULAR_USER_INDEX, Component.COMMUNITIES.toString(),
						useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			service.addRequestOption(
					impersonationHeaderKey,
					impersonationHeaderValue_userId + "="
							+ orgBRegular.getUserId());
			LOGGER.debug("After setting the request option, X-LConn-RunAs : "
					+ service.getRequestOption("X-LConn-RunAs"));

			LOGGER.debug("1. Pinned Forum Topics in Community: "
					+ community.getTitle());
			// Create Pinned Topic
			ForumTopic newTopicPinned2 = new ForumTopic(randomTitle2,
					randomLorem, true, false, false, false);
			Entry pinnedResult2 = (Entry) service.createForumTopic(community,
					newTopicPinned2);
			assertTrue(pinnedResult2 != null);
			assertTrue(pinnedResult2.getTitle().equals(randomTitle2));
			assertTrue(pinnedResult2.getContent().trim()
					.equals(randomLorem.trim()));
			LOGGER.debug("Created Pinned Topic: " + newTopicPinned2.getTitle());

			// Retrieve
			Entry resultEntry = (Entry) service.getForumTopic(pinnedResult2
					.getEditLink().getHref().toString());
			System.out
					.println(pinnedResult2.getEditLink().getHref().toString());
			assertTrue(resultEntry != null);
			assertTrue(resultEntry.getTitle().equals(randomTitle2));
			assertTrue(resultEntry.getContent().trim()
					.equals(randomLorem.trim()));
			LOGGER.debug("Retrieved Pinned Topic: " + newTopicPinned2.getTitle());

			LOGGER.debug("ENDING Community Forum Cross Org test ");
		}// end SC if
	}

	// @Test
	public void ideationCrossOrg() throws Exception {
		LOGGER.debug("BEGINNING TEST: Ideation Cross Org Testing");
		/*
		 * Steps: 1. Create Community 2. Add ideation blog widget 3. Create two
		 * ideation entries, one will be marked as a duplicate of the other. 4.
		 * Create an entry with the content that will mark an idea as a
		 * duplicate 5. Execute the entry using http PUT. 6. Validate
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

			// create community
			Community newCommunity = null;
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				newCommunity = new Community(
						"Ideation Cross Org " + uniqueNameAddition,
						"Test using Org A admin impersonating Org B regular user",
						Permissions.PRIVATE, null);
			} else {
				newCommunity = new Community(
						"Ideation Cross Org " + uniqueNameAddition,
						"Test using Org A admin impersonating Org B regular user",
						Permissions.PUBLIC, null);
			}
			Entry communityResult = (Entry) service
					.createCommunity(newCommunity);
			assertTrue(communityResult != null);

			Community comm = new Community(
					(Entry) service.getCommunity(communityResult
							.getEditLinkResolvedHref().toString()));

			// Add org admin user otherwise the file share at the last step will
			// fail.
			Member member = new Member(null, user.getUserId(),
					Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
			member = new Member(user.getEmail(), user.getUserId(),
					Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);

			Entry memberEntry = (Entry) service.addMemberToCommunity(comm,
					member);
			assertEquals(" Add Community Member ", 201, service.getRespStatus());

			// Add ideation blog widget
			Widget widget = new Widget(
					StringConstants.WidgetID.IdeationBlog.toString());
			service.postWidget(comm, widget.toEntry());
			assertEquals(201, service.getRespStatus());
			// assertEquals(200, service.addWidget(comm, "IdeationBlog"));

			// //////////////////////////////////////////////////////////////////////////////////////////////
			/*
			 * This is the start of the code to create an ideadtion blog via the
			 * widget. The process is: 1. Get a feed of Community remote apps
			 * and get the link for IdeationBlog 2. Execute the link. This will
			 * return a service doc. Parse the service doc and get the link for
			 * posting to Ideation Blogs 3. Create the Entry 4. Execute a POST
			 * for the Entry.
			 */
			Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
					comm.getRemoteAppsListHref(), true, null, 0, 50, null,
					null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
			assertTrue(remoteAppsFeed != null);

			String ideationBlogUrl = null;
			for (Entry raEntry : remoteAppsFeed.getEntries()) {
				for (Category category : raEntry.getCategories()) {
					if (category.getTerm().equalsIgnoreCase("IdeationBlog")) {
						for (Link link : raEntry.getLinks()) {
							if (link.getRel().contains(
									"remote-application/publish")) {
								ideationBlogUrl = link.getHref().toString();
							}
						}
					}
				}
			}

			// This call returns a service doc, not a feed. EE doesn't seem to
			// support direct retrieval of workspaces, collections, etc
			// so the code below does the parsing. What we are trying to get is
			// the link used to post to IdeationBlog widget in communities.
			// The blogHandleUrl is used for voting.
			String postIdeaBlogUrl = null;
			// String blogHandleUrl = null;
			ExtensibleElement ee = null;
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				// Document<Feed> feed_doc =
				// client.get(ideationBlogUrl).getDocument();
				// ee = feed_doc.getRoot();
				ee = assignedService.getAnyFeedWithRedirect(ideationBlogUrl);
			} else {
				ee = service.getAnyFeed(ideationBlogUrl);
			}
			assertTrue(ee != null);
			for (Element ele : ee.getElements()) {
				if (ele.toString().startsWith("<workspace")) {
					for (Element ele2 : ele.getElements()) {
						if (ele2.toString().startsWith("<collection")) {
							for (QName atrb : ele2.getAttributes()) {
								if (atrb.toString().equalsIgnoreCase("href")
										&& ele2.getAttributeValue("href")
												.contains("api/entries")) {
									postIdeaBlogUrl = ele2
											.getAttributeValue("href");
								}
								/*
								 * if (atrb.toString().equalsIgnoreCase("href")
								 * && ele2.getAttributeValue("href").contains(
								 * "blogType=ideationblog")){ blogHandleUrl =
								 * ele2.getAttributeValue("href"); }
								 */
							}
						}
					}
				}
			}

			Factory factory = abdera.getFactory();
			Entry entry = factory.newEntry();
			entry.setTitle("Cross Org: Idea 1");
			entry.setId("some-id-should-be-ignored");
			entry.setContent("idea1 content");
			entry.addCategory("DataPopIdeaBlogEntry");

			Entry entry2 = factory.newEntry();
			entry2.setTitle("Cross Org: Idea 2");
			entry2.setId("some-id-should-be-ignored");
			entry2.setContent("idea2 content");
			entry2.addCategory("DataPopIdeaBlogEntry");

			// Create the ideation blog - this should pass using impersonated
			// user!
			service.postIdeationBlog(postIdeaBlogUrl, entry);
			assertEquals("Wrong HTTP return code", true,
					service.getRespStatus() == 201);

			// Change the run as header to use org b user.
			// Setup code for the out of org user:
			int ORG_B_REGULAR_USER_INDEX = 15;
			String impersonationHeaderKey = "X-LConn-RunAs";
			String impersonationHeaderValue_userId = "userId";
			boolean useSSL = true;

			// Org B regular user - Jill White
			UserPerspective orgBRegular = new UserPerspective(
					ORG_B_REGULAR_USER_INDEX, Component.COMMUNITIES.toString(),
					useSSL);

			LOGGER.debug("Before setting the request option, X-LConn-RunAs : "
					+ service.getRequestOption("X-LConn-RunAs"));

			// Org A Admin uses Org B regular user for impersonation.
			service.addRequestOption(
					impersonationHeaderKey,
					impersonationHeaderValue_userId + "="
							+ orgBRegular.getUserId());
			LOGGER.debug("After setting the request option, X-LConn-RunAs : "
					+ service.getRequestOption("X-LConn-RunAs"));

			// Create the ideation blog - this should pass using impersonated
			// user!
			service.postIdeationBlog(postIdeaBlogUrl, entry2);
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				assertEquals("Wrong HTTP return code", true,
						service.getRespStatus() == 403);
			} else {
				assertEquals("Wrong HTTP return code", true,
						service.getRespStatus() == 201);
			}

			LOGGER.debug("ENDING TEST: Ideation Cross Org Testing");
		}
	}

	/*
	 * RTC 127788 Org user should be able to set a community's business owner
	 * via the API
	 * 
	 * Process: 1) Create community as user x. 2) Add user y as owner. 3) As
	 * org-admin, do PUT to update user y from owner to business owner. 4) Do
	 * GET on members feed, verify user y marked as business owner and user x is
	 * not.
	 * 
	 * Where: a. User x is not the org admin. b. Org admin is not a member of
	 * the community.
	 * 
	 * This test is not supported on-premise.
	 */
	@Test
	public void setBusinessOwner() throws FileNotFoundException, IOException {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("Beginning test RTC 127788: Setting business owner via API.");

			// Second user to be added to the community - not an org admin.
			String businessOwnerRole = "business-owner";
			int SECOND_USER_INDEX = 4;
			UserPerspective secondUser=null;
			try {
				secondUser = new UserPerspective(SECOND_USER_INDEX,
						Component.COMMUNITIES.toString(), useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
			String communityName = "RTC 127788 Set business owner via API "
					+ uniqueNameAddition;

			Community testCommunity = new Community(communityName,
					"Impersonation test, create community", Permissions.PUBLIC,
					null);

			LOGGER.debug("Step 1: Create Community.");
			Entry communityResult = (Entry) otherUserService
					.createCommunity(testCommunity);
			Community community = new Community(
					(Entry) otherUserService.getCommunity(communityResult
							.getEditLinkResolvedHref().toString()));

			LOGGER.debug("Step 2: Add a member who is not org admin.");
			Member member = new Member(null, secondUser.getUserId(),
					Component.COMMUNITIES, Role.OWNER, MemberType.PERSON);

			Entry memberEntry = (Entry) otherUserService.addMemberToCommunity(
					community, member);
			assertEquals(" Add Community Member ", 201,
					otherUserService.getRespStatus());

			LOGGER.debug("Step 3: Org Admin updates user 2's role to business owner.");
			Entry fetchedMember = (Entry) otherUserService
					.getAnyFeed(memberEntry.getEditLinkResolvedHref()
							.toASCIIString());
			fetchedMember.addCategory("http://www.ibm.com/xmlns/prod/sn/type",
					businessOwnerRole, "");

			otherUserService.putEntry(memberEntry.getEditLinkResolvedHref()
					.toASCIIString(), fetchedMember);
			assertEquals("Update Community Member to business owner", 200,
					otherUserService.getRespStatus());

			LOGGER.debug("Step 4: Validate user 2's role as business owner.");
			Feed membershipFeed = (Feed) otherUserService.getAnyFeed(community
					.getMembersListHref());
			boolean bizOwnerCategoryFound = false;
			for (Entry ntry : membershipFeed.getEntries()) {
				if (ntry.getTitle().equalsIgnoreCase(secondUser.getRealName())) {
					List<Category> categories = ntry.getCategories();
					for (Category category : categories) {
						if (category.getTerm().equals(businessOwnerRole)) {
							bizOwnerCategoryFound = true;
						}
					}
				}
			}
			assertEquals("Business owner category not found.",
					bizOwnerCategoryFound, true);

			LOGGER.debug("ENDING test RTC 127788: Setting business owner via API.");
		}
	}

	// @Test
	public void createAdditionalIdeationImpr() throws Exception {
		LOGGER.debug("BEGIN TEST: test create ideation blog via API");
		/*
		 * Steps: 1. Create Community 2. Add ideation blog widget 3. Create
		 * additional ideation blog with API
		 */
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		Community newCommunity = new Community(
				"test create ideation blog via API " + uniqueNameAddition,
				"Create additional ideation blog with API",
				Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);
		Community comm = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		// Add ideation blog widget
		Widget widget = new Widget(
				StringConstants.WidgetID.IdeationBlog.toString());
		service.postWidget(comm, widget.toEntry());
		assertEquals(201, service.getRespStatus());

		// get the API url to create ideation blog
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				comm.getRemoteAppsListHref(), true, null, 0, 50, null, null,
				SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);

		String ideationBlogUrl = null;
		for (Entry raEntry : remoteAppsFeed.getEntries()) {
			for (Category category : raEntry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("IdeationBlog")) {
					for (Link link : raEntry.getLinks()) {
						if (link.getRel()
								.contains("remote-application/publish")) {
							ideationBlogUrl = link.getHref().toString();
						}
					}
				}
			}
		}
		// This call returns a service doc, not a feed. EE doesn't seem to
		// support direct retrieval of workspaces, collections, etc
		// so the code below does the parsing. What we are trying to get is the
		// link used to post to IdeationBlog widget in communities.
		// The blogHandleUrl is used for voting.
		String createIdeationUrl = null;
		ExtensibleElement ee = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ee = assignedService.getAnyFeedWithRedirect(ideationBlogUrl);
		} else {
			ee = assignedService.getAnyFeed(ideationBlogUrl);
		}
		assertTrue(ee != null);
		Service svc = (Service) ee;
		for (Workspace ws : svc.getWorkspaces()) {
			for (Collection c : ws.getCollections()) {
				String href = c.getHref().toString();
				if (href != null && href.contains("commUuid")) {
					createIdeationUrl = href;
					break;
				}
			}
		}

		String[] blogTitles = new String[] { "Test Ideation Blogs with API 1",
				"Test Ideation Blogs with API 2" };
		Blog blog1 = new Blog(blogTitles[0], "Ideation_API_1",
				"Ideation blog createed with API", "tag1 tag2", false, true,
				null, null, TimeZone.getDefault(), true, 13, true, true, true,
				0, -1, null, comm.getUuid(), null, 0);
		blog1.setMapRole(Role.DRAFT);
		service.postBlog(createIdeationUrl, blog1.toEntry());
		assertEquals("response status", 201, service.getRespStatus());

		Blog blog2 = new Blog(blogTitles[1], "Ideation_API_2",
				"Ideation blog createed with API", "tag1 tag2 tag3", false,
				true, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, comm.getUuid(), null, 0);
		blog1.setMapRole(Role.AUTHOR);
		service.postBlog(createIdeationUrl, blog2.toEntry());
		assertEquals("response status", 201, service.getRespStatus());

		// validate the created ideation
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ee = assignedService.getAnyFeedWithRedirect(ideationBlogUrl);
		} else {
			ee = assignedService.getAnyFeed(ideationBlogUrl);
		}
		assertTrue(ee != null);
		svc = (Service) ee;
		Set<String> titles = new HashSet<String>();
		for (Workspace ws : svc.getWorkspaces()) {
			LOGGER.debug("found workspace " + ws.getTitle());
			titles.add(ws.getTitle());
		}
		for (String t : blogTitles) {
			assertTrue("[" + t + "] created", titles.contains(t));
		}

		LOGGER.debug("END TEST: test create ideation blog via API");
	}

	@Test
	public void batchFollowSingleCommunity() throws Exception {
		LOGGER.debug("Test batch follow/unfollow for a single community");
		String randString = RandomStringUtils.randomAlphanumeric(4);
		String communityTitle = "testBatchFollowSingleCommunity"
				+ randString;
		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community(communityTitle,
				"A community for testing", Permissions.PUBLIC, null);
		Entry communityResult = (Entry) service
				.createCommunity(testCommunity);

		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		LOGGER.debug("Step 2 Post follow feed");
		FollowEntry follow = new FollowEntry(
				StringConstants.COMMUNITIES_SOURCE, otherUser.getUserId(),
				StringConstants.COMMUNITY_RESOURCE_TYPE);

		Feed feed = follow.getFactory().newFeed();
		feed.addEntry(follow.toEntry());

		adminService.postFollowFeedForCommunity(feed,
				communityRetrieved.getUuid());
		assertEquals("Batch follow ", 200, adminService.getRespStatus());

		LOGGER.debug("Step 3: Get followed communities as that user");
		Feed followedCommunitiesFeed = (Feed) otherUserService
				.getFollowedCommunities();

		LOGGER.debug("Step 4: Verify that the community exists in followed communities");
		boolean foundCommunity = CommunitiesTestBase.isCommunityFollowed(
				testCommunity, followedCommunitiesFeed);
		assertTrue(foundCommunity);

		LOGGER.debug("Step 5: Do batch delete");

		adminService.deleteFollowFeedForCommunity(feed,
				communityRetrieved.getUuid());
		assertEquals("Batch unfollow ", 200, service.getRespStatus());
	}

	
	@Test
	public void getCommunityCalendar(){
		//super.getCommunityCalendar();
	}
	
	@Test
	public void calendarTimezone() throws Exception  {
		//super.calendarTimezone();
	}
	
	@Test
	public void editCommunity()  {
		//super.editCommunity();
	}
	
	@Test
	public void createCommunityInvitesWithBadData() throws FileNotFoundException, IOException {
		//super.createCommunityInvitesWithBadData();
	}	

	@Test
	public void communityStartPage()  {
		//super.communityStartPage();
	}	
	
	@Test
	public void updateErrors()  {
		//super.updateErrors();
	}	
	
	@Test
	public void badParameter() throws MalformedURLException, URISyntaxException  {
		//super.badParameter();
	}		
	
	@Test
	public void inOrgEmailInSC() throws FileNotFoundException, IOException  {
		//super.inOrgEmailInSC();
	}
	
	@Test
	public void isExternalElement() throws IOException, URISyntaxException  {
		//super.isExternalElement();
	}	
	
	@Test
	public void testBatchFollowMultipleCommunities() throws Exception  {
		super.testBatchFollowMultipleCommunities();
	}	
	
	@Test
	public void memberEmailPrivileges()  {
	//	super.memberEmailPrivileges();
	}
	
	@Test
	public void testCommunityMember() throws FileNotFoundException, IOException  {
		//super.testCommunityMember();
	}	
	
	@Test
	public void createAdditionalIdeation() {
		//super.createAdditionalIdeation();
	}	
	
	@Test
	public void internalOnly() {
		//super.internalOnly();
	}
	
	@Test
	public void unfollowCommunity() {
		//super.unfollowCommunity();
	}	
	
	@Test
	public void missingNsError() throws Exception {
		//super.missingNsError();
	}
	
	@Test
	public void getMediaFromCommunityBlog() throws IOException {
		//super.getMediaFromCommunityBlog();
	}
	
	@Test
	public void testCommunityRequestToJoin() throws Exception {
		super.testCommunityRequestToJoin();
	}
	
	@Test
	public void getOrgCommunities() {
		super.getOrgCommunities();
	}
	
	@Test
	public void testUpdateDeleteCommunityCalendarEvent() {
		super.testUpdateDeleteCommunityCalendarEvent();
	}
	
	@Test
	public void testSubcommunityCreate() {
		LOGGER.debug("testing Subcommunity create");

		String CommunityTitle = "subcommTest Parent"
				+ Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a community");
		Community comm = new Community(CommunityTitle,
				"A community for testing", Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(comm);

		LOGGER.debug("Step 2: Retrieve that community");
		Community community = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		String subommunityTitle = "subcommTest Child"
				+ Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 3: Add current user as owner");
		Member member = new Member(null, user.getUserId(),
				Component.COMMUNITIES, Role.OWNER, MemberType.PERSON);

		service.addMemberToCommunity(community, member);
		assertEquals(" Add Community Member ", 201, service.getRespStatus());
		
		LOGGER.debug("Step 4: Create the subcommunity");
		Subcommunity subcomm = new Subcommunity(subommunityTitle,
				"A community for testing", Permissions.PRIVATE, null);

		Entry subcommResult = (Entry) service.createSubcommunity(community, subcomm);
		
		LOGGER.debug("Step 5: Fetch the subcommunity; ensure it has the valid author");
		Community fetchedSubcomm = new Community(
				(Entry) service.getCommunity(subcommResult
						.getEditLinkResolvedHref().toString()));
		
		List<Person> authList = fetchedSubcomm.getAuthors();
		String author = authList.get(0).getName();

		assertEquals("Author is not the expected value: ", imUser.getRealName(), author);	
	}
	
    @Test
    public void testCommunityNameTypeahead() throws Exception {
     //super.testCommunityNameTypeahead();
    }
    
	@Test
	public void testCommunityMailUnsubscribe() throws FileNotFoundException, IOException {
	 // ignore this test case for Impersonated users.
	}


	
	@AfterClass
	public static void tearDown() {
		service.tearDown();
		otherUserService.tearDown();
		assignedService.tearDown();
		adminService.tearDown();
	}
}