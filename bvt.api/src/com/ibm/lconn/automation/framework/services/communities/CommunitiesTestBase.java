package com.ibm.lconn.automation.framework.services.communities;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Person;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MailSubscription;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
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
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.common.nodes.FollowEntry;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.nodes.Calendar;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommentToEvent;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommunityMember;
import com.ibm.lconn.automation.framework.services.communities.nodes.Event;
import com.ibm.lconn.automation.framework.services.communities.nodes.FeedLink;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;
import com.ibm.lconn.automation.framework.services.communities.nodes.Subcommunity;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import com.ibm.lconn.automation.framework.services.gatekeeper.GateKeeperService;

public abstract class CommunitiesTestBase {

	protected static Abdera abdera = new Abdera();
	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunitiesTestBase.class.getName());

	protected static UserPerspective user, imUser, otherUser, visitor, admin,extendedEmployee, invitor,assignedUser;
	protected static CommunitiesService service, visitorService, extendedEmpService,adminService, otherUserService,assignedService;
	protected static GateKeeperService gateKeeperService;
	protected static boolean useSSL = true;
	
	private final static String OnPremOrgId = "00000000-0000-0000-0000-000000000000";


	//@Test
	public void createCommunityByVisitor() throws FileNotFoundException,
			IOException {
		LOGGER.debug("Creating Community with External user");
		if (StringConstants.VMODEL_ENABLED) {
			Community testCommunity = new Community("CommVModel ",
					"A community", Permissions.PRIVATE, null);
			visitorService.createCommunity(testCommunity);
			assertEquals(
					"Create Community should not allowed with External user",
					403, visitorService.getRespStatus());
		}
	}

	@Test
	public void testCommunityMember() throws FileNotFoundException, IOException {
		/*
		 * Tests add/remove member from a community ( VDModel test with external
		 * member ) Step 1: Create a community Step 2: Retrieve the Community,
		 * add bookmark on public community 403 ( not member ) Step 3: Add a
		 * internal member Step 4: Validate RTC 130878 - Removing a non-existing
		 * user produces a null pointer exception. S/B 400 Step 5: Remove the
		 * member Step 6: Verify the member is no longer in the community Step
		 * 7: Add a external member and verify
		 */
		LOGGER.debug("Beginning test: Community member");
		String dateCode = Utils.logDateFormatter.format(new Date());
		// ProfileData testUser = ProfileLoader.getProfile(5);
		// UserPerspective testUser = new UserPerspective(5,
		// Component.COMMUNITIES.toString(), useSSL);

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD
				|| StringConstants.VMODEL_ENABLED) {
			testCommunity = new Community("TestRemoveMember " + dateCode,
					"A community with a bad member that needs to be removed",
					Permissions.PRIVATE, null, false);
		} else {
			testCommunity = new Community("TestRemoveMember " + dateCode,
					"A community with a bad member that needs to be removed",
					Permissions.PUBLIC, null);
		}
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);
		assertEquals("Create Community failed"+service.getDetail(),
				201, service.getRespStatus());

		LOGGER.debug("Step 2: Other user/External user can't retrieve private Community");
		LOGGER.debug("        Other user/External user can retrieve public Community");
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD
				|| StringConstants.VMODEL_ENABLED) {
			otherUserService.getCommunity(communityEntry
					.getEditLinkResolvedHref().toString());
			assertEquals("Get private Community should not allowed other user",
					403, otherUserService.getRespStatus());
			if (StringConstants.VMODEL_ENABLED) {
				visitorService.getCommunity(communityEntry
						.getEditLinkResolvedHref().toString());
				assertEquals(
						"Get Community should not allowed with External user",
						403, visitorService.getRespStatus());
			}
		} else {
			Entry CommunityEntry = (Entry) otherUserService
					.getCommunity(communityEntry.getEditLinkResolvedHref()
							.toString());
			assertEquals("Get Community ", 200,
					otherUserService.getRespStatus());

			Community communityRetrieved = new Community(CommunityEntry);
			Bookmark testBookmark = new Bookmark(
					"add Bookmark to public community", "not member",
					"http://www.test.com", null);
			otherUserService.createCommunityBookmark(communityRetrieved,
					testBookmark);
			assertEquals("visitor add bookmark title ", 403,
					otherUserService.getRespStatus());

			if (StringConstants.VMODEL_ENABLED) {
				// this is allowed as anonymous
				CommunityEntry = (Entry) visitorService
						.getCommunity(communityEntry.getEditLinkResolvedHref()
								.toString());
				assertEquals("Get Community with External user", 200,
						visitorService.getRespStatus());

				// external
				communityRetrieved = new Community(CommunityEntry);
				testBookmark = new Bookmark("visitor Bookmark", "VModel test",
						"http://www.yahoo.com", null);
				visitorService.createCommunityBookmark(communityRetrieved,
						testBookmark);
				assertEquals("visitor add bookmark title ", 403,
						visitorService.getRespStatus());

			}
		}

		LOGGER.debug("Step 3: Add a internal member");
		Community testCommunityRetrieved = new Community(
				(Entry) assignedService.getCommunity(communityEntry
						.getEditLinkResolvedHref().toString()));
		Member member = new Member(null, otherUser.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		member = new Member(otherUser.getEmail(), otherUser.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);

		Entry memberEntry = (Entry) service.addMemberToCommunity(
				testCommunityRetrieved, member);
		assertEquals(" Add Community Member ", 201, service.getRespStatus());

		// Validate RTC 130878 - Removing a non-existing user produces a null
		// pointer exception
		LOGGER.debug("Step 4: Remove a member with a bad user id number.  RTC 130878.  Should return 400");
		String editLink = memberEntry.getEditLinkResolvedHref().toString();
		String editLinkWithBadUserId = editLink.substring(0,
				editLink.length() - 2) + "$$";
		service.removeMemberFromCommunity(editLinkWithBadUserId);
		assertEquals("Deleting a non-existing user should return HTTP 400.",
				400, service.getRespStatus());

		LOGGER.debug("Step 5: Remove the member");
		assertTrue(assignedService.removeMemberFromCommunity(editLink));

		LOGGER.debug("Step 6: Verify the member is no longer in the community");
		Feed membersFeed = (Feed) assignedService.getCommunityMembers(
				testCommunityRetrieved.getMembersListHref(), false, null, 1,
				10, null, null, null, null);
		boolean foundMember = false;
		for (Entry entry : membersFeed.getEntries()) {
			if (entry.getTitle().equals(otherUser.getRealName()))
				foundMember = true;
		}
		assertFalse(foundMember);

		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Step 7: Add a external member");
			member = new Member(visitor.getEmail(), null,
					Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);

			extendedEmpService.addMemberToCommunity(testCommunityRetrieved,
					member);
			assertEquals("Non owner - extended user add External user", 403,
					extendedEmpService.getRespStatus());

			service.addMemberToCommunity(testCommunityRetrieved, member);
			assertEquals("Owner - add External user", 400,
					service.getRespStatus());

		}

		LOGGER.debug("Ending test: Community member");
	}

	//@Test
	public void testVModelCommunityMember() throws FileNotFoundException, IOException {
		/*
		 * Tests add/remove member from a community ( VDModel test with external
		 * member ) 
		 * Step 1: Create a private community 
		 * Step 2: Retrieve the Community, 
		 * Step 3: VModel test with External member 
		 * step 3a: Retrieve the Community 
		 * Step 3b: Add bookmark 
		 * Step 3c: Add/update feed 
		 * Step 3d: Verify the feed have been updated 
		 * Step 3e: Remove the member 
		 * Step 3f: Verify the member is no longer in the community
		 */
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Beginning test: VModel Community member");
			String dateCode = Utils.logDateFormatter.format(new Date());
			// ProfileData testUser = ProfileLoader.getProfile(5);
			// UserPerspective testUser = new UserPerspective(5,
			// Component.COMMUNITIES.toString(), useSSL);

			LOGGER.debug("Step 1: Create a community");
			Community testCommunity = new Community("TestRemoveMember "
					+ dateCode,
					"A community with a bad member that needs to be removed",
					Permissions.PRIVATE, null, true);
			Entry communityEntry = (Entry) extendedEmpService
					.createCommunity(testCommunity);

			LOGGER.debug("Step 2: retrieve private Community");
			Community testCommunityRetrieved = new Community(
					(Entry) extendedEmpService.getCommunity(communityEntry
							.getEditLinkResolvedHref().toString()));

			Member member = new Member(visitor.getEmail(), null,
					Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
			Entry memberEntry = (Entry) extendedEmpService
					.addMemberToCommunity(testCommunityRetrieved, member);
			assertEquals("extended user add External user", 201,
					extendedEmpService.getRespStatus());

			Feed membersFeed = (Feed) visitorService.getCommunityMembers(
					testCommunityRetrieved.getMembersListHref(), false, null,
					1, 10, null, null, null, null);
			// membersFeed = (Feed)
			// service.getCommunityMembers(testCommunityRetrieved.getMembersListHref(),
			// false, null, 1, 10, null, null, null, null);
			boolean foundMember = false;
			for (Entry entry : membersFeed.getEntries()) {
				if (entry.getTitle().contains(visitor.getRealName()))
					foundMember = true;
			}
			assertTrue("add member " + visitor.getRealName(), foundMember);

			LOGGER.debug("Step 3: VModel test with External member a. retrieve the Community");
			LOGGER.debug("Step 3a: Retrieve the Community");
			Entry CommunityEntry = (Entry) visitorService
					.getCommunity(communityEntry.getEditLinkResolvedHref()
							.toString());
			assertEquals("Get Community allowed with External user", 200,
					visitorService.getRespStatus());

			LOGGER.debug("Step 3b: Add bookmark");
			Community communityRetrieved = new Community(CommunityEntry);
			Bookmark testBookmark = new Bookmark("visitor Bookmark",
					"VModel test", "http://www.test.com", null);
			visitorService.createCommunityBookmark(communityRetrieved,
					testBookmark);
			assertEquals("visitor add bookmark title ", 201,
					visitorService.getRespStatus());

			LOGGER.debug("Step 3c: Add feeds");
			FeedLink testFeedLink1 = new FeedLink("Test Feed1 " + dateCode,
					"The chill feed", "http://www.testfeedlink1.com/" + dateCode,
					"tag");
			FeedLink testFeedLink2 = new FeedLink("Test Feed2 " + dateCode,
					"The weird feed", "http://www.testfeedlink2.com/" + dateCode,
					"tag");
			visitorService.createFeedLink(communityRetrieved, testFeedLink1);
			assertEquals("visitor add feed ", 201,
					visitorService.getRespStatus());
			Entry response2 = (Entry) visitorService.createFeedLink(
					communityRetrieved, testFeedLink2);

			LOGGER.debug("Step 3c: Edit the feed");
			String timeStamp2 = Utils.logDateFormatter.format(new Date());
			Entry feedLink2Entry = (Entry) visitorService.getFeedLink(response2
					.getEditLinkResolvedHref().toString());
			feedLink2Entry.setTitle("I EDITED the Feed " + timeStamp2);
			feedLink2Entry
					.setContent("A lone feed, trapped in the wrath of my community. When will I be free? "
							+ timeStamp2);
			visitorService.putEntry(feedLink2Entry.getEditLinkResolvedHref()
					.toString(), feedLink2Entry);

			LOGGER.debug("Step 3d: Verify the feed have been updated");
			Entry editedEntry = (Entry) visitorService.getFeedLink(response2
					.getEditLinkResolvedHref().toString());
			assertEquals("Edited feed ", "I EDITED the Feed " + timeStamp2,
					editedEntry.getTitle());
			assertEquals("Edited content ",
					"A lone feed, trapped in the wrath of my community. When will I be free? "
							+ timeStamp2, editedEntry.getContent());

			LOGGER.debug("Step 3e: Remove the member");
			assertTrue(extendedEmpService.removeMemberFromCommunity(memberEntry
					.getEditLinkResolvedHref().toString()));

			LOGGER.debug("Step 3f: Verify the member is no longer in the community");
			membersFeed = (Feed) extendedEmpService.getCommunityMembers(
					testCommunityRetrieved.getMembersListHref(), false, null,
					1, 10, null, null, null, null);
			foundMember = false;
			for (Entry entry : membersFeed.getEntries()) {
				if (entry.getTitle().equals(visitor.getRealName()))
					foundMember = true;
			}
			assertFalse("remove member " + visitor.getRealName(), foundMember);
		}

		LOGGER.debug("Ending test: VModel Community member");
	}

	public Community getCreatedPrivateCommunity(String CommunityTitle) {
		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community(CommunityTitle,
				"A community for testing", Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Retrieve that community");
		Community communityRetrieved = new Community(
				(Entry) assignedService.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		return communityRetrieved;

	}

	public Community getCreatedPublicCommunity(String CommunityTitle) {
		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community(CommunityTitle,
				"A community for testing", Permissions.PUBLIC, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Retrieve that community");
		Community communityRetrieved = new Community(
				(Entry) assignedService.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		return communityRetrieved;

	}

	public Community getCreatedPublicInviteOnlyCommunity(String CommunityTitle) {
		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community(CommunityTitle,
				"A community for testing", Permissions.PUBLICINVITEONLY, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Retrieve that community");
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		return communityRetrieved;

	}

	@Test
	public void createCommunityActivity() throws Exception {
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		LOGGER.debug("Create Community Activity:");

		// create community
		Community newCommunity = null;
		String communityName = "Create Community Activity "
				+ uniqueNameAddition;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community(communityName,
					"Simple test for community based Activity",
					Permissions.PRIVATE, null);
		} else {
			newCommunity = new Community(communityName,
					"Simple test for community based Activity",
					Permissions.PUBLIC, null);
		}
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		// get created community
		Entry communityEntry = (Entry) service.getCommunity(communityResult
				.getEditLinkResolvedHref().toString());
		Community comm = new Community(communityEntry);

		// created Communities Activities widget
		Widget widget = new Widget(
				StringConstants.WidgetID.Activities.toString());
		service.postWidget(comm, widget.toEntry());
		assertEquals(201, service.getRespStatus());

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

		// Create an Activity
		Factory factory = abdera.getFactory();
		Entry activityEntry = factory.newEntry();
		activityEntry.setTitle("Activity created in Communities, API");
		activityEntry.setContent("test simple activity created in communities");
		activityEntry.addCategory(StringConstants.SCHEME_TYPE,
				"community_activity", "Community Activity");

		service.postEntry(activitiesUrl, activityEntry);

		// Create Entry
		// <category scheme="http://www.ibm.com/xmlns/prod/sn/type"
		// term="community_activity"
		// label="Community Activity"/>
	}

	@Test
	public void createCommunityBlog() throws Exception {

		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		LOGGER.debug("create Community Blog:");

		// create community
		Community newCommunity = null;
		String communityName = "Create Community Blog " + uniqueNameAddition;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community(communityName,
					"Simple test for community based blogs",
					Permissions.PRIVATE, null);
		} else {
			newCommunity = new Community(communityName,
					"Simple test for community based blogs",
					Permissions.PUBLIC, null);
		}
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertEquals(201, service.getRespStatus());

		// get created community
		Entry communityEntry = (Entry) service.getCommunity(communityResult
				.getEditLinkResolvedHref().toString());
		Community comm = new Community(communityEntry);

		// Add blogs widget
		Feed widgetsInitialFeed = (Feed) service.getCommunityWidgets(comm.getUuid());
		boolean blogFound = false;
		for (Entry e : widgetsInitialFeed.getEntries()) {
			if (e.getTitle().equals("Blog")) {
				blogFound = true;
				break;
			}
		}
		if (blogFound == false) {
			Widget widget = new Widget(StringConstants.WidgetID.Blog.toString());
			service.postWidget(comm, widget.toEntry());
			assertEquals(201, service.getRespStatus());
		}

		LOGGER.debug("End Test: createCommunityBlog");
	}

	public void getMediaFromCommunityBlog() throws IOException {
		
		if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD) {
			/*
			 * Tests ability to retrieve blogs media from a community blog.
			 * Step 1: Create a community 2: add blogs widget
			 * 3: Upload an image Step 4: Verify the image is uploaded correctly.
			 */
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
			LOGGER.debug("create Community Blog:");
	
			// create community
			Community newCommunity = null;
			String communityName = "Create Community Blog " + uniqueNameAddition;
			newCommunity = new Community(communityName,
					"Simple test for community based blogs",
					Permissions.PUBLIC, null);
	
			Entry communityResult = (Entry) service.createCommunity(newCommunity);
			assertEquals(201, service.getRespStatus());
	
			// get created community
			Entry communityEntry = (Entry) service.getCommunity(communityResult
					.getEditLinkResolvedHref().toString());
			Community comm = new Community(communityEntry);
	
			// Add blogs widget
			Feed widgetsInitialFeed = (Feed) service.getCommunityWidgets(comm.getUuid());
			boolean blogFound = false;
			for (Entry e : widgetsInitialFeed.getEntries()) {
				if (e.getTitle().equals("Blog")) {
					blogFound = true;
					break;
				}
			}
			if (blogFound == false) {
				Widget widget = new Widget(StringConstants.WidgetID.Blog.toString());
				service.postWidget(comm, widget.toEntry());
				assertEquals(201, service.getRespStatus());
			}
			
			UserPerspective cup = new UsersEnvironment().getLoginUserEnvironment(
					StringConstants.CURRENT_USER,Component.BLOGS.toString());
			BlogsService blogsService = cup.getBlogsService();
			
			LOGGER.debug("Start upload image to community blog..");
	
			BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream(
					"/resources/lamborghini_murcielago_lp640.jpg"));
			File file = File.createTempFile("lambo", ".jpg");
			ImageIO.write(image, "jpg", file);
			ExtensibleElement element = null ;
			String handleNoSpace = comm.getUuid();
			try {
				element = blogsService.postFile(blogsService.getBlogsMediaURLString(handleNoSpace),
						file);
			} catch (Exception e) {
				e.printStackTrace();
				assertTrue(false); // file didn't get posted because some error
			}
			
			assertNotNull(element);
			assertEquals(handleNoSpace,element.getExtensions(StringConstants.SNX_COMMUNITY_UUID)
					.get(0).getText());
			
			Feed mediaFeed = null;
			Entry entry;
			try {
				mediaFeed = (Feed) blogsService.getBlogFeed(blogsService
						.getBlogsMediaURLString(handleNoSpace));
				entry = (mediaFeed.getEntries().get(0)); // 0 because it should be
				// the first element
				// since it just posted
				assertTrue(entry.getTitle() != null);
			} catch (Exception e) {
				assertTrue(false);
			}
			
			LOGGER.debug("End Test: getMediaFromCommunityBlog");
		}
	}
	
	public void verifyMediaDownloadInCommunityBlog() throws IOException {
		
		if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD) {
			/*
			 * Tests ability to retrieve blogs media from a community blog.
			 * Step 1: Create a community 2: add blogs widget
			 * 3: Upload an image Step 4: Verify the image is uploaded correctly.
			 * Step 5:  Verify Cache-Control header of Blogs attachment download API
			 */
			LOGGER.debug("create Community Blog:");
	
			// create community
			Community[] newCommunity = new Community[2];
			String communityName = "Create Community Blog " + RandomStringUtils.randomAlphanumeric(4);
			
			newCommunity[0] = new Community(communityName,
					"Simple test for public community based blogs",
					Permissions.PUBLIC, null);
			
			communityName = "Create Community Blog " + RandomStringUtils.randomAlphanumeric(4);
			newCommunity[1] = new Community(communityName,
					"Simple test for private community based blogs",
					Permissions.PRIVATE, null);
			
			for(int m = 0; m < newCommunity.length; m++)
			{
				Entry communityResult = (Entry) service.createCommunity(newCommunity[m]);
				assertEquals("Create community failed "+service.getDetail(), 201, service.getRespStatus());
		
				// get created community
				Entry communityEntry = (Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString());
				assertEquals("Get community failed "+service.getDetail(), 200, service.getRespStatus());
				Community comm = new Community(communityEntry);
		
				// Add blogs widget
				Feed widgetsInitialFeed = (Feed) service.getCommunityWidgets(comm.getUuid());
				boolean blogFound = false;
				for (Entry e : widgetsInitialFeed.getEntries()) {
					if (e.getTitle().equals("Blog")) {
						blogFound = true;
						break;
					}
				}
				if (blogFound == false) {
					Widget widget = new Widget(StringConstants.WidgetID.Blog.toString());
					service.postWidget(comm, widget.toEntry());
					assertEquals("Post widget failed "+service.getDetail(), 201, service.getRespStatus());
				}
				
				UserPerspective cup = new UsersEnvironment().getLoginUserEnvironment(
						StringConstants.CURRENT_USER,Component.BLOGS.toString());
				BlogsService blogsService = cup.getBlogsService();
				
				LOGGER.debug("Start upload image to community blog..");
		
				BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream(
						"/resources/lamborghini_murcielago_lp640.jpg"));
				File file = File.createTempFile("lambo", ".jpg");
				ImageIO.write(image, "jpg", file);
				ExtensibleElement element = null ;
				String handleNoSpace = comm.getUuid();
				try {
					element = blogsService.postFile(blogsService.getBlogsMediaURLString(handleNoSpace),
							file);
					assertEquals("Post file failed "+blogsService.getDetail(), 201, blogsService.getRespStatus());
				} catch (Exception e) {
					e.printStackTrace();
					assertTrue(false); // file didn't get posted because some error
				}
				
				assertNotNull(element);
				assertEquals(handleNoSpace,element.getExtensions(StringConstants.SNX_COMMUNITY_UUID)
						.get(0).getText());
				
				Feed mediaFeed = null;
				Entry entry;
				try {
					mediaFeed = (Feed) blogsService.getBlogFeed(blogsService
							.getBlogsMediaURLString(handleNoSpace));
					assertEquals("Get blog feed failed "+blogsService.getDetail(), 200, blogsService.getRespStatus());
					entry = (mediaFeed.getEntries().get(0)); // 0 because it should be
					// the first element
					// since it just posted
					assertTrue(entry.getTitle() != null);
					
					LOGGER.debug("Verify Cache-Control header of Blogs attachment download API");
					ClientResponse cr = blogsService.getResponse(blogsService
							.getBlogsMediaURLString(handleNoSpace) + "/" + entry.getTitle());
					assertEquals("Getting media from community blogs failed "+blogsService.getDetail(),
							200, blogsService.getRespStatus());
					
					String[] headerNames = cr.getHeaderNames();
					for (int i=headerNames.length-1; i>=0; i--){
						String headerName = headerNames[i];
//						LOGGER.debug("header" + i + ": " + headerName + ":" + cr.getHeader(headerName));
						if("Cache-Control".equals(headerName))
						{
							if(newCommunity[m].getCommunityTypeElement().getText().toLowerCase().equals("public"))
								assertEquals(cr.getHeader(headerName), "public, max-age=5, s-maxage=5");
							else if(newCommunity[m].getCommunityTypeElement().getText().toLowerCase().equals("privide"))
								assertEquals(cr.getHeader(headerName), "no-store, no-cache, must-revalidate");
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					assertTrue(false);
				}
			}
			LOGGER.debug("End Test: verifyMediaDownloadInCommunityBlog");
		}
	}
    
	@Test
	public void createCommunityGallery() throws Exception {
		/*
		 * This test only adds the Gallery widget to communities.
		 */
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		LOGGER.debug("create Community Gallery:");

		// create community
		Community newCommunity = null;
		String communityName = "Create Community Gallery " + uniqueNameAddition;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community(communityName,
					"Simple test for community based blogs",
					Permissions.PRIVATE, null);
		} else {
			newCommunity = new Community(communityName,
					"Simple test for community based blogs",
					Permissions.PUBLIC, null);
		}
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertEquals(201, service.getRespStatus());

		// get created community
		Entry communityEntry = (Entry) service.getCommunity(communityResult
				.getEditLinkResolvedHref().toString());
		Community comm = new Community(communityEntry);

		Widget widget = new Widget(StringConstants.WidgetID.Gallery.toString(),
				"col3");
		service.postWidget(comm, widget.toEntry());
		assertEquals(201, service.getRespStatus());

		// assertEquals(200, service.addWidget(comm, "Gallery"));

		LOGGER.debug("End Test: createCommunityGallery");
	}

	@Test
	public void testCommunityCalendarEvent() {
		LOGGER.debug("testing Community calendar Event");

		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityCalendarTest"
				+ timeStamp);

		Widget widget = new Widget(
				StringConstants.WidgetID.Calendar.toString(), "col3");
		service.postWidget(community, widget.toEntry());
		assertEquals("add calendar widget", 201, service.getRespStatus());

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
					ExtensibleElement serviceFeed = service
							.getCommunityCalendarService(service_url);
					assertTrue(serviceFeed != null);

					// retrieve calendar
					LOGGER.debug("Getting Community Calendar:");
					String calendar_url = service_url.replace(
							"calendar/service?", "calendar?");
					ExtensibleElement calendarFeed = service
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
						ExtensibleElement eventFeed = service
								.postCalendarEvent(eventUrl, event);
						assertTrue(eventFeed != null);

						// post calendar events
						event = new Event("My events",
								StringConstants.STRING_NO_LOWERCASE,
								StringConstants.STRING_WEEKLY, "1",
								"2012-10-24T02:00:00.000Z",
								"2012-08-24T02:00:00.000Z",
								"2012-08-24T03:00:00.000Z", 0);
						eventFeed = service.postCalendarEvent(eventUrl, event);
						assertTrue(eventFeed != null);

						// retrieve calendar events
						if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
							eventFeed = (Feed) service
									.getCalendarFeedWithRedirect(eventUrl
											+ "&startDate=2012-08-26T00:00:00+08:00&endDate=2012-12-06T00:00:00+08:00");
						} else {
							eventFeed = (Feed) service
									.getCalendarFeed(eventUrl
											+ "&startDate=2012-08-26T00:00:00+08:00&endDate=2012-12-06T00:00:00+08:00");
						}

						assertTrue(eventFeed != null);

						for (Entry eEntry : ((Feed) eventFeed).getEntries()) {
							String selfUrl = eEntry.getSelfLinkResolvedHref()
									.toString();
							eventFeed = service.getCalendarFeed(selfUrl);
							assertTrue(eventFeed != null);

							// String followed =
							// eEntry.getSimpleExtension(StringConstants.SNX_FOLLOWED);
							// String attended =
							// eEntry.getSimpleExtension(StringConstants.SNX_ATTENDED);

							// get attended event
							String attendUrl = eEntry.getLinkResolvedHref(
									StringConstants.REL_E_ATTEND).toString();
							eventFeed = service
									.getCalendarFeed(attendUrl
											+ "&startDate=2011-12-26T00:00:00+08:00&endDate=2012-12-06T00:00:00+08:00");
							assertTrue(eventFeed != null);

							// get followed event
							String followUrl = eEntry.getLinkResolvedHref(
									StringConstants.REL_E_FOLLOW).toString();
							eventFeed = service
									.getCalendarFeed(followUrl
											+ "&startDate=2011-12-26T00:00:00+08:00&endDate=2012-12-06T00:00:00+08:00");
							assertTrue(eventFeed != null);

							if (eEntry
									.getLinkResolvedHref(StringConstants.REL_E_INSTANCES) != null) { // in
																										// after
																										// create
																										// ==
																										// null
																										// here
								String instancesUrl = eEntry
										.getLinkResolvedHref(
												StringConstants.REL_E_INSTANCES)
										.toString();
								eventFeed = service
										.getCalendarFeed(instancesUrl);
								assertTrue(eventFeed != null);
							}
							if (eEntry
									.getLinkResolvedHref(StringConstants.REL_E_PARENTEVENT) != null) { // in
																										// retrieve
								String parentUrl = eEntry.getLinkResolvedHref(
										StringConstants.REL_E_PARENTEVENT)
										.toString();
								eventFeed = service.getCalendarFeed(parentUrl);
								assertTrue(eventFeed != null);
							}

							// create comment on event
							String commentUrl = eEntry.getLinkResolvedHref(
									StringConstants.REL_EDIT).toString();
							commentUrl = commentUrl.replace("/event?",
									"/event/comment?");

							CommentToEvent comment = new CommentToEvent(
									"My comment");
							eventFeed = service.postEventComment(commentUrl,
									comment);
							assertTrue(eventFeed != null);
							assertTrue(((Entry) eventFeed).getContent()
									.equalsIgnoreCase("My comment"));
							String mycommentUrl = ((Entry) eventFeed)
									.getSelfLinkResolvedHref().toString();

							eventFeed = service.getCalendarFeed(mycommentUrl);
							assertTrue(eventFeed != null);
							assertTrue(((Entry) eventFeed).getContent()
									.equalsIgnoreCase("My comment"));

							// delete the comment
							service.deleteFeedLink(mycommentUrl);
							eventFeed = service.getCalendarFeed(mycommentUrl);
							// after delete couldn't find anymore => 404: Not
							// Found
							String result = eventFeed.getText();
							assertTrue(eventFeed != null);
							assertTrue(result.isEmpty());
							// ((Entry)eventFeed ).getContent(); //null

							// for @mention RTC 118287
							String vcard = "&lt;span class=\"vcard\"&gt;&lt;span class=\"fn\"&gt;@"
									+ otherUser.getRealName()
									+ "&lt;/span&gt;&lt;span class=\"x-lconn-userid\"&gt;"
									+ otherUser.getUserId()
									+ "&lt;/span&gt;&lt;/span&gt;";
							comment = new CommentToEvent("My comment " + vcard,
									"html");
							eventFeed = service.postEventComment(commentUrl,
									comment);
							assertEquals("contentType", "TEXT",
									((Entry) eventFeed).getContentType()
											.toString());
							commentUrl = commentUrl + "&contentFormat=html";
							eventFeed = service.postEventComment(commentUrl,
									comment);
							assertEquals("contentType", "HTML",
									((Entry) eventFeed).getContentType()
											.toString());

							break;
						}

					}
				}
			}

		}

		LOGGER.debug("Finished Community Calendar event...");
	}

	@Test
	public void testUpdateDeleteCommunityCalendarEvent() {
		LOGGER.debug("testing Updating and Deleting Community calendar Event");

		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityCalendarTest"
				+ timeStamp);

		Widget widget = new Widget(StringConstants.WidgetID.Calendar.toString());
		service.postWidget(community, widget.toEntry());
		assertEquals("add calendar widget", 201, service.getRespStatus());

		Feed remoteAppsFeed = (Feed) assignedService.getCommunityRemoteAPPs(
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
						String savedEventUrl = eventUrl;
						ExtensibleElement eventFeed = service
								.postCalendarEvent(eventUrl, event);
						assertTrue(eventFeed != null);

						// post calendar events
						event = new Event("My events",
								StringConstants.STRING_NO_LOWERCASE,
								StringConstants.STRING_WEEKLY, "1",
								"2012-10-24T02:00:00.000Z",
								"2012-08-24T02:00:00.000Z",
								"2012-08-24T03:00:00.000Z", 0);
						eventFeed = service.postCalendarEvent(eventUrl, event);
						ExtensibleElement weeklyEventFeed = eventFeed;
						String weeklyEventeventUrl = eventUrl;

						assertTrue(eventFeed != null);

						// retrieve calendar events
						if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
							eventFeed = (Feed) assignedService
									.getCalendarFeedWithRedirect(eventUrl
											+ "&startDate=2012-08-26T00:00:00+08:00&endDate=2012-12-06T00:00:00+08:00");
						} else {
							eventFeed = (Feed) assignedService
									.getCalendarFeed(eventUrl
											+ "&startDate=2012-08-26T00:00:00+08:00&endDate=2012-12-06T00:00:00+08:00");
						}

						assertTrue(eventFeed != null);

						// retrieve calendar events by Last Created.
						String eventUrlByLastCreated = eventUrl
								.concat("&type=event");
						eventFeed = (Feed) assignedService
								.getCalendarFeed(eventUrlByLastCreated);
						assertTrue(eventFeed != null);

						// retrieve calendar events by userId:
						Person person = community.getAuthors().iterator()
								.next();
						String userId = person
								.getSimpleExtension(StringConstants.SNX_USERID);
						String eventUrlByUser = eventUrl
								.concat("&type=event&userid=" + userId);
						eventFeed = (Feed) assignedService
								.getCalendarFeed(eventUrlByUser);
						assertTrue(eventFeed != null);

						// Update a calendar event (whole series)
						/*
						 * For repeating events, most fields could be updated,
						 * except: - <snx:recurrence> "frequency" attribute -
						 * <snx:recurrence> "interval" attribute -
						 * <snx:recurrence>/<snx:byDay>
						 */
						String eventUuid = weeklyEventFeed
								.getSimpleExtension(StringConstants.SNX_EVENTUUID);
						Event updatedEvent = new Event("My updated events",
								StringConstants.STRING_NO_LOWERCASE,
								StringConstants.STRING_WEEKLY, "1",
								"2012-12-24T02:00:00.000Z",
								"2012-10-24T02:00:00.000Z",
								"2012-10-24T03:00:00.000Z", 0);
						updatedEvent.setId(new IRI(
								"urn:lsid:ibm.com:calendar:event:"
										.concat(eventUuid)));
						int calendarUuidIndex = weeklyEventeventUrl
								.indexOf("calendarUuid");
						String eventUrlWithEventUuidString = weeklyEventeventUrl
								.replace(weeklyEventeventUrl.substring(
										calendarUuidIndex,
										weeklyEventeventUrl.length()),
										"eventUuid=");
						String eventUrlWithEventUuid = eventUrlWithEventUuidString
								.concat(eventUuid);
						eventFeed = service.putCalendarEvent(
								eventUrlWithEventUuid, updatedEvent);
						assertTrue(eventFeed != null);

						// Update a calendar event (a single instance)
						String weeklyEventeventByListUrl = eventUrlWithEventUuid
								.concat("&mode=list");
						eventFeed = (Feed) assignedService
								.getCalendarFeed(weeklyEventeventByListUrl);

						for (Entry eEntry : ((Feed) eventFeed).getEntries()) {
							String selfUrlForUpdate = eEntry
									.getSelfLinkResolvedHref().toString();
							eventFeed = assignedService
									.getCalendarFeed(selfUrlForUpdate);
							String eventInstUuid = eventFeed
									.getSimpleExtension(StringConstants.SNX_EVENT_INST_UUID);
							
							// Check this event after rescheduling is still repeating by Tuesday.
							List<Element> recurrenceElements = eventFeed.getExtension(StringConstants.SNX_RECURRENCE).getElements();
							Iterator<Element> recurrenceElementsIterator = recurrenceElements.iterator();
							while (recurrenceElementsIterator.hasNext()) {
								Element element = recurrenceElementsIterator.next();
								if (StringConstants.SNX_BYDAY.equals(element.getQName())) {
									assertEquals("TU", element.getText().trim());
								}
							}
							
							// Create a single instance of the event,and then
							// assign event instance's ID to this single event.
							Event updatedSingleEvent = new Event("My events",
									StringConstants.STRING_YES_LOWERCASE, null,
									null, null, "2012-09-23T02:00:00.000Z",
									"2012-09-23T03:00:00.000Z", 0);
							updatedSingleEvent.setId(new IRI(
									"urn:lsid:ibm.com:calendar:event:"
											.concat(eventInstUuid)));
							updatedSingleEvent.setIsEvent(category
									.setTerm("event-instance"));

							// Set correct URL format
							String eventUrlWithEventInstUuidString = weeklyEventeventUrl
									.replace(weeklyEventeventUrl.substring(
											calendarUuidIndex,
											weeklyEventeventUrl.length()),
											"eventInstUuid=");
							String eventUrlWithEventInstUuid = eventUrlWithEventInstUuidString
									.concat(eventInstUuid);
							eventFeed = service.putCalendarEvent(
									eventUrlWithEventInstUuid,
									updatedSingleEvent);
							assertTrue(eventFeed != null);

							// Delete an instance of a repeating event
							assignedService.deleteFeedLink(eventUrlWithEventInstUuid);

							break;
						}

						// Delete the whole series of a repeating event
						Event repeatingEvent = new Event(
								"Another repeating events",
								StringConstants.STRING_NO_LOWERCASE,
								StringConstants.STRING_WEEKLY, "1",
								"2012-10-24T02:00:00.000Z",
								"2012-08-24T02:00:00.000Z",
								"2012-08-24T03:00:00.000Z", 0);
						eventFeed = service.postCalendarEvent(savedEventUrl,
								repeatingEvent);
						eventUuid = eventFeed
								.getSimpleExtension(StringConstants.SNX_EVENTUUID);
						eventUrlWithEventUuid = eventUrlWithEventUuidString
								.concat(eventUuid);
						assignedService.deleteFeedLink(eventUrlWithEventUuid);

					}
				}
			}
		}

		LOGGER.debug("Finished Updating and Deleting Community Calendar event...");
	}

	@Test
	public void testFollowRSVPCommunityCalendarEvent() {
		LOGGER.debug("testing Follow and RSVP Community calendar Event");

		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityCalendarTest"
				+ timeStamp);

		Widget widget = new Widget(StringConstants.WidgetID.Calendar.toString());
		service.postWidget(community, widget.toEntry());
		assertEquals(201, service.getRespStatus());

		Feed remoteAppsFeed = (Feed) assignedService.getCommunityRemoteAPPs(
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
						Event event = new Event("My events",
								StringConstants.STRING_NO_LOWERCASE,
								StringConstants.STRING_WEEKLY, "1",
								"2012-10-24T02:00:00.000Z",
								"2012-08-24T02:00:00.000Z",
								"2012-08-24T03:00:00.000Z", 0);
						String eventUrl = event_url.replace(
								"type=event&calendarUuid", "calendarUuid");
						ExtensibleElement eventFeed = service
								.postCalendarEvent(eventUrl, event);
						assertTrue(eventFeed != null);

						// Get a list of Upcoming Event Instances that You are
						// Following
						String followUrl = event_url
								.replace(
										event_url.substring(
												event_url
														.indexOf("calendar/event?type=event&calendarUuid"),
												event_url.length()), "follow?");
						eventFeed = assignedService
								.getCalendarFeed(followUrl
										+ "&startDate=2011-12-26T00:00:00+08:00&endDate=2012-12-06T00:00:00+08:00");
						assertTrue(eventFeed != null);

						for (Entry eEntry : ((Feed) eventFeed).getEntries()) {

							String selfUrlForUpdate = eEntry
									.getSelfLinkResolvedHref().toString();
							Entry eventEntry = null;

							if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
								eventEntry = (Entry) assignedService
										.getCalendarFeedWithRedirect(selfUrlForUpdate);
							} else {
								eventEntry = (Entry) assignedService
										.getCalendarFeed(selfUrlForUpdate);
							}
							String eventInstUuid = eventEntry
									.getSimpleExtension(StringConstants.SNX_EVENT_INST_UUID);

							// Set correct URL format to test Follow&RSVP an
							// Event(Instance)
							int calendarUuidIndex = eventUrl
									.indexOf("calendar/event?calendarUuid");
							String eventUrlWithEventInstUuidString = eventUrl
									.replace(eventUrl.substring(
											calendarUuidIndex,
											eventUrl.length()),
											"follow?eventInstUuid=");
							String eventUrlWithEventInstUuid = eventUrlWithEventInstUuidString
									.concat(eventInstUuid
											.concat("&type=attend"));
							ExtensibleElement followEventInstanceFeed = service
									.followEventInstance(
											eventUrlWithEventInstUuid, event);
							assertTrue(followEventInstanceFeed != null);

							String eventAttendeesUrl = eventUrlWithEventInstUuid
									.replace("follow",
											"calendar/event/attendees");
							ExtensibleElement eventInstanceAttendeesFeed = assignedService
									.getFeedLink(eventAttendeesUrl);
							assertTrue(eventInstanceAttendeesFeed != null);

							// Cancel Follow & RSVP and Event (Instance)
							assignedService.deleteFeedLink(eventUrlWithEventInstUuid);

							break;
						}
					}
				}
			}
		}

		LOGGER.debug("Finished Testing Follow and RSVP Community Calendar event...");
	}

	@Test
	public void getCommunityCalendar() {
		LOGGER.debug("Getting Community calendar: set Role");

		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityCalendarTest"
				+ timeStamp);

		List<Person> persons = community.getAuthors();
		assertNotNull("No authero ", persons);
		for (Person person : persons) {
			String isExternal = person.getSimpleExtension(StringConstants.SNX_ISEXTERNAL);
			// assertNotNull ("isExternal should be there", isExternal);
		}

		Widget widget = new Widget(StringConstants.WidgetID.Calendar.toString());
		service.postWidget(community, widget.toEntry());
		assertEquals(201, service.getRespStatus());

		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				community.getRemoteAppsListHref(), true, null, 0, 50, null,
				null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);

		for (Entry entry : remoteAppsFeed.getEntries()) {
			for (Category category : entry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("calendar")) {

					String service_url = null;
					// String event_url=null;
					for (Link link : entry.getLinks()) {
						if (link.getRel().contains(
								StringConstants.REL_REMOTEAPPLICATION_PUBLISH)) {
							service_url = link.getHref().toString();
						}
						/*
						 * if ( link.getRel().contains(StringConstants.
						 * REL_REMOTEAPPLICATION_FEED) ){ event_url =
						 * link.getHref().toString(); }
						 */
					}
					LOGGER.debug("Service URL : " + service_url);

					// get calendar service doc
					ExtensibleElement serviceFeed = service
							.getCommunityCalendarService(service_url);
					assertTrue(serviceFeed != null);

					// retrieve calendar
					LOGGER.debug("Getting Community Calendar:");
					String calendar_url = service_url.replace(
							"calendar/service?", "calendar?");
					Entry calendar = (Entry) service
							.getCommunityCalendarService(calendar_url);
					assertTrue(calendar != null);

					// check isExternal RTC #113375
					persons = calendar.getAuthors();
					for (Person person : persons) {
						String isExternal = person
								.getSimpleExtension(StringConstants.SNX_ISEXTERNAL);
						assertNotNull("isExternal should be there", isExternal);
					}

					String role = calendar.getExtension(
							StringConstants.SNX_MAP_ROLE).getText();
					LOGGER.debug("Updating Community Calendar Role:");
					// Modify Calendar role
					if (role.equalsIgnoreCase("reader")) {
						// update calendar Role as author
						Calendar calendarEntry = new Calendar("My Calendar",
								StringConstants.Role.AUTHOR, null);
						ExtensibleElement calendarUpdateFeed = service
								.editCommunityCalendarService(calendar_url,
										calendarEntry);
						assertTrue(calendarUpdateFeed != null);
						assertTrue(calendarUpdateFeed
								.getExtension(StringConstants.SNX_MAP_ROLE)
								.getText().equalsIgnoreCase("author"));
					} else {
						// update calendar Role as reader
						Calendar calendarEntry = new Calendar("My Calendar",
								StringConstants.Role.READER, null);
						ExtensibleElement calendarUpdateFeed = service
								.editCommunityCalendarService(calendar_url,
										calendarEntry);
						assertTrue(calendarUpdateFeed != null);
						assertTrue(calendarUpdateFeed
								.getExtension(StringConstants.SNX_MAP_ROLE)
								.getText().equalsIgnoreCase("reader"));
					}

					for (Element element : entry.getElements()) {
						String s1 = element.toString();
						// 46261
						if (s1.contains("opensearch")) {
							assertTrue(true);
							return;
						}
					}
					assertTrue(false);
				}
			}

		}

		LOGGER.debug("Finished getting Community Calendar...");
	}

	@Test
	public void getCommunityPermaLink() {
		LOGGER.debug("Getting Community Perma page:");

		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
				5, null, null, null, null, null);
		assertTrue(communitiesFeed != null);

		ArrayList<Community> communities = new ArrayList<Community>();
		for (Entry communityEntry : communitiesFeed.getEntries()) {
			communities.add(new Community(communityEntry));
		}

		for (Community community : communities) {
			// HttpResponse permaPage =
			// service.getPermaLink(community.getAlternateLink()) ;
			service.getResponseString(community.getAlternateLink());
			assertEquals(200, service.getRespStatus());

			service.getResponseString(community.getAlternateLink()
					+ "#fullpageWidgetId=Members");
			assertEquals(200, service.getRespStatus());
		}

		LOGGER.debug("Finished getting Community Perma Page...");
	}

	@Test
	public void createCommunityCalendar() throws Exception {
		LOGGER.debug("Creating Community Calendar:");

		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityCalendarTest"
				+ timeStamp);

		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				community.getRemoteAppsListHref(), true, null, 0, 50, null,
				null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);

		boolean calendar_exist = false;
		for (Entry entry : remoteAppsFeed.getEntries()) {
			for (Category category : entry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("calendar")) {
					calendar_exist = true;
				}
			}
		}
		if (!calendar_exist) {
			Widget widget = new Widget(
					StringConstants.WidgetID.Calendar.toString());
			service.postWidget(community, widget.toEntry());
			assertEquals("add calendar widget", 201, service.getRespStatus());

		}

		LOGGER.debug("Finished creating Community Calendar...");
	}

	// @Test
	public void testCommunityInvite() throws FileNotFoundException, IOException {
		/*
		 * Tests the ability to create a community invitiation 
		 * Step 1: Create a community 
		 * Step 2: Create a community invitation to another user 
		 * Step 3: Validate title and author elements for RTC 107379 
		 * Step 4: Verify that the invitation shows up in the receiving user's invitations
		 */
		LOGGER.debug("Beginning test: Create community invite.  Validate RTC 107379 - title and author elements.");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		// UserPerspective user2 = new UserPerspective(5,
		// Component.COMMUNITIES.toString(), useSSL);

		LOGGER.debug("Step 1: Create a community");
		// CommunitiesService service2 = user2.getCommunitiesService();
		Community testCommunity = new Community(
				"Test CreateInvite " + timeStamp,
				"A community with invitations to be sent", Permissions.PRIVATE,
				null, false);

		Entry communityEntry = (Entry) otherUserService
				.createCommunity(testCommunity);
		assertEquals("create Community ", 201, otherUserService.getRespStatus());

		LOGGER.debug("Step 2: Create a community invitation for another user.");
		Community communityRetrieved = new Community(
				(Entry) otherUserService.getCommunity(communityEntry
						.getEditLinkResolvedHref().toString()));
		Invitation newInvite = new Invitation(StringConstants.USER_EMAIL, null,
				"Join my community!", "I promise there will be cake");
		Entry response = (Entry) otherUserService.createInvitation(
				communityRetrieved, newInvite);
		assertEquals("Invite Internal user should return ", 201,
				otherUserService.getRespStatus());

		if (StringConstants.VMODEL_ENABLED) {
			newInvite = new Invitation(StringConstants.EXTERNAL_USER_EMAIL,
					null, "Join my community!", "I promise there will be cake");
			otherUserService.createInvitation(communityRetrieved, newInvite);
			assertEquals("Invite External user should return 400", 400,
					otherUserService.getRespStatus());
		}

		// empty userid, name for author.
		// Validate that the title does not contain the word 'null'.
		LOGGER.debug("Step 3: RTC 107379.  Validate title and author elements ");
		assertEquals(false, response.getTitle().contains("null"));

		Person authorElement = response.getAuthor();
		// Validate author element - should contain userid and name values.
		assertEquals(
				true,
				authorElement.getName().equalsIgnoreCase(
						otherUser.getRealName()));
		assertEquals(true,
				authorElement.getSimpleExtension(StringConstants.SNX_USERID)
						.equals(otherUser.getUserId()));

		LOGGER.debug("Step 4: Verify that the invitation shows up in the user's invitations");
		Feed invitationsFeed = (Feed) service.getMyInvitations(false, 1, 10,
				null, null);
		boolean foundInvitation = false;
		for (Entry invitationEntry : invitationsFeed.getEntries()) {
			if (invitationEntry.getTitle().contains(testCommunity.getTitle()))
				foundInvitation = true;
		}
		assertTrue(foundInvitation);

		LOGGER.debug("Ending test: Create community invite. Validate RTC 107379 - title and author elements.");
	}

	// @Test
	public void inviteSelfLinkTest() throws FileNotFoundException, IOException {
		/*
		 * Tests that the userid parameter for community invites supports both
		 * communities user id and profiles user id. Step 1: Create a community
		 * Step 2: Create a community invitation for another user Step 3:
		 * Validate RTC defect 127570 - ensure link ends with "/instance" Step
		 * 4: Get feed (as the user who created the invite), use the Profiles
		 * uid. Step 5: Try to get the invite again, this time use communities
		 * uuid. Step 6: Validate defect 142892. Validate http 400 error instead
		 * of 500 for inviting a non-existing user. Step 7: Delete the
		 * community. Step 8: Validate RTC 130379/110478 - Validate response to
		 * attempting to get invite feed. S/B 404.
		 */
		LOGGER.debug("Beginning test: RTC 107446 Valiate userid parameter with self link to support community uuid and profiles uid");
		LOGGER.debug("Also validation for RTC 130379/110478 - invite feed should return HTTP 404, not 500");
		String randString = RandomStringUtils.randomAlphanumeric(4);
		String communityName = "RTC 107446 " + randString;

		LOGGER.debug("Step 1: Create a community");
		// UserPerspective user2 = new UserPerspective(5,
		// Component.COMMUNITIES.toString(), useSSL);
		// CommunitiesService service2 = user2.getCommunitiesService();
		Community testCommunity = new Community(
				communityName,
				"RTC 107446. Invitation self link's use of userid parameter should support community uuid and profiles uid",
				Permissions.PRIVATE, null);
		Entry communityEntry = (Entry) otherUserService
				.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Create a community invitation for another user.");
		Community communityRetrieved = new Community(
				(Entry) otherUserService.getCommunity(communityEntry
						.getEditLinkResolvedHref().toString()));
		Invitation newInvite = new Invitation(StringConstants.USER_EMAIL, null,
				"Test RTC 107446", "Testing self link userid param.");
		Entry resultEntry = (Entry) otherUserService.createInvitation(
				communityRetrieved, newInvite);

		// CommunitiesPerspective jones242 = new CommunitiesPerspective(2,
		// useSSL);
		String selfLink = resultEntry.getSelfLinkResolvedHref().toString(); //

		Feed myInvitations = (Feed) service.getMyInvitations(false, 1, 10,
				null, null);
		String idValue = myInvitations.getId().toString();
		String[] idTokens = idValue.split("invites-");
		String userId = idTokens[1];

		LOGGER.debug("Step 3: Validate RTC defect 127570");
		String linkValue = "";
		String[] tokens = null;
		for (Entry ntry : myInvitations.getEntries()) {
			Link lnk = ntry
					.getLink("http://www.ibm.com/xmlns/prod/sn/community");
			linkValue = lnk.getAttributeValue("href");

			tokens = linkValue.split("\\?");
			assertEquals("URL does not end with 'instance'", true,
					tokens[0].endsWith("instance"));
		}

		LOGGER.debug("Step 4: Verify that the invitation shows up in the user's invitations - using profiles uid");
		Entry invitationEntry = (Entry) otherUserService.getAnyFeed(selfLink);
		assertEquals(true,
				invitationEntry.getTitle().contains(testCommunity.getTitle()));

		// selfLink2 uses the community user id value
		String selfLink2 = selfLink.substring(0, selfLink.lastIndexOf('='))
				+ "=" + userId;
		LOGGER.debug("Step 5: Verify that the invitation shows up in the user's invitations - using communities uuid");
		Entry invitationEntry2 = (Entry) otherUserService.getAnyFeed(selfLink2);
		assertEquals(true,
				invitationEntry2.getTitle().contains(testCommunity.getTitle()));

		LOGGER.debug("Step 6: Validate defect 142892.  Validate http 400 error instead of 500 when inviting non-existing user.");
		Factory factory = abdera.getFactory();
		Entry badInvite = factory.newEntry();
		Person p = badInvite.addContributor("user_999XXX");
		p.addSimpleExtension(StringConstants.SNX_USERID, "999XXX");
		badInvite.addCategory("http://www.ibm.com/xmlns/prod/sn/type",
				"invite", "");

		otherUserService.postEntry(communityRetrieved.getInvitationsListLink(),
				badInvite);
		assertEquals("Expected HTTP 400.", 400,
				otherUserService.getRespStatus());

		// Now try a delete
		LOGGER.debug("Step 7: Delete the community.");
		assertTrue(otherUserService
				.deleteCommunity(communityRetrieved
						.getLinks()
						.get(StringConstants.REL_EDIT + ":"
								+ StringConstants.MIME_NULL).getHref()
						.toString()));

		// Validation for RTC 130379/110478 - 500 error getting invites feed
		// when community does not exist. S/B 404
		// Need to get invitations based on Community Uuid.
		String invitationsLink = communityRetrieved.getInvitationsListLink();
		Entry result = (Entry) otherUserService.getAnyFeed(invitationsLink);
		boolean found404 = false;
		boolean notFound = false;
		for (Element el : result.getElements()) {
			if (el.toString().contains("404")) {
				assertEquals(true, el.toString().contains("404"));
				found404 = true;
			}
			if (el.toString().contains("Not Found")) {
				assertEquals(true, el.toString().contains("Not Found"));
				notFound = true;
			}
		}
		// Validate that the response entry contains expected HTTP 404.
		assertEquals(true, found404 && notFound);

		LOGGER.debug("ENDING TEST: RTC 107446 Valiate userid parameter with self link to support community uuid and profiles uid");
		LOGGER.debug("Also validation for RTC 130379/110478 - invite feed should return HTTP 404, not 500");
	}

	@Test
	public void inviteErrorMessage() {
		/*
		 * RTC 130862 and 130863 This test validates the following: 1. Rest call
		 * to create invites for a non existant community returns 500 error.
		 * 130862
		 * 
		 * 2. Rest call to delete a community invite passing a wrong community
		 * fails with 500 error 130863
		 * 
		 * In both cases the correct return code should be HTTP 404 - not found.
		 * 
		 * Process: Step 1 Create a community Step 2 Create a community
		 * invitation for another user. Step 3 Change the Community Uid to
		 * something that does not exist. Post the invitation. Step 4 Validate
		 * the error message Step 5 Try to delete using bad community id Step 6
		 * Validate the error message
		 */
		LOGGER.debug("BEGIN TEST: inviteErrorMessage. RTC 130862 and 130863");
		String randString = RandomStringUtils.randomAlphanumeric(4);
		String communityName = "RTC 130862 and 130863 " + randString;

		LOGGER.debug("Step 1: Create a community");
		// reminder: otherUserService is typically ajones101 (TDS 6.2 based
		// deployments only, not SC).
		Community testCommunity = new Community(
				communityName,
				"RTC 130862 and 130863. Invitation using non-existing communities should produce 404",
				Permissions.PRIVATE, null);
		Entry communityEntry = (Entry) otherUserService
				.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Create a community invitation for another user.");
		Community communityRetrieved = new Community(
				(Entry) otherUserService.getCommunity(communityEntry
						.getEditLinkResolvedHref().toString()));
		Invitation newInvite = new Invitation(StringConstants.USER_EMAIL, null,
				"Test RTC 130862 and 130863", "Error message test.");
		String badInvitationLink = communityRetrieved.getInvitationsListLink();

		LOGGER.debug("Step 3: Change the Community Uid to something that does not exist. Post the invitation.");
		// Change the community id by changing the last two chars to $$
		badInvitationLink = badInvitationLink.substring(0,
				badInvitationLink.length() - 2)
				+ "$$";
		otherUserService.postEntry(badInvitationLink, newInvite.toEntry());

		LOGGER.debug("Step 4: Validate the error message");
		String expectedErrorMessage = "The referenced community does not exist.";
		assertEquals("The correct error message was not retrieved",
				expectedErrorMessage, otherUserService.getRespErrorMsg());
		assertEquals("Correct HTTP return code was not retrieved", 404,
				otherUserService.getRespStatus());

		LOGGER.debug("Step 5: Now try to delete using the same damaged link.");
		String badInvitationLinkAndUserId = badInvitationLink + "&userid="
				+ otherUser.getUserId();
		otherUserService.deleteInvitiation(badInvitationLinkAndUserId);

		LOGGER.debug("Step 6: Validate the error message");
		assertEquals("Correct HTTP return code was not retrieved", 404,
				otherUserService.getRespStatus());

		LOGGER.debug("END TEST: inviteErrorMessage. RTC 130862 and 130863");
	}

	/*
	 * @Test public void getMyInvitations() {
	 * LOGGER.debug("Getting My Invitations:");
	 * 
	 * Feed invitations = (Feed) service.getMyInvitations(true, 0, 5, null,
	 * null); assertTrue(invitations != null);
	 * 
	 * for(Entry invitation : invitations.getEntries()) {
	 * LOGGER.debug(invitation.getTitle()); }
	 * 
	 * LOGGER.debug("Finished getting My Invitations..."); }
	 */

	@Test
	public void getAllCommunities() {
		LOGGER.debug("Getting All Communities:");

		Feed communities = (Feed) service.getAllCommunities(true, null, 0, 0,
				null, null, null, null, null);
		assertTrue(communities != null);
		assertEquals("getAllCommunities", 200, service.getRespStatus());

		for (Entry community : communities.getEntries()) {
			Community test = new Community(community);
			LOGGER.debug(test.getTitle());
		}

		LOGGER.debug("Finished getting All Communities...");
	}

	//@Test
	public void getAllCommunitiesByVisitor() {
		LOGGER.debug("Getting All Communities by Visitor:");
		if (StringConstants.VMODEL_ENABLED) {
			ExtensibleElement communities = visitorService.getAllCommunities(
					true, null, 0, 0, null, null, null, null, null);
			assertEquals("getAllCommunities", 403,
					visitorService.getRespStatus());
		}
		LOGGER.debug("Finished getting All Communities by Visitor..");
	}

	@Test
	public void getOrgCommunities() {
		if (isListRestrictedGKEnabled() == false) {
			//TJB 8/14/15 Pipeline Fix
			//throw new SkipException("RBL not Gatekeeper enabled");
			LOGGER.debug("**************************");
			LOGGER.debug("RBL not Gatekeeper enabled");
			LOGGER.debug("**************************");
			LOGGER.debug("**************************");
		} else {
		
			LOGGER.debug("Create RBL community:");
			String timeStamp = Utils.logDateFormatter.format(new Date());
			Community community = getCreatedPrivateCommunity("RBL" + timeStamp);
			community.setListWhenPrivate(true);
			service.editCommunity(community.getEditLink(), community);
			assertEquals("editCommunity", 200, service.getRespStatus());
		
			LOGGER.debug("Getting Org Communities:");

			Feed communities = (Feed) service.getOrgCommunities(false, null, 0, 200,
				null, null, SortField.CREATED, null, null);
			assertTrue(communities != null);
			assertEquals("getOrgCommunities", 200, service.getRespStatus());

			boolean found = false;
			String communityUuid = community.getUuid();
			for (Entry entry : communities.getEntries()) {
				Community test = new Community(entry);
				if (test.getUuid().equals(communityUuid)) {
					found = true;
					break;
				}
			}
			//TJB 9.16.15 re-engaged this validation.
			assertTrue("RBL community not found in My Organization Communities feed", found);
			
			LOGGER.debug("Finished getting Org Communities...");

		}
	}

	@Test
	public void getOrgCommunitiesTagCloud() {
		if (isListRestrictedGKEnabled() == false) {
			//TJB 8/14/15 Pipeline Fix
			//throw new SkipException("RBL not Gatekeeper enabled");
			LOGGER.debug("**************************");
			LOGGER.debug("RBL not Gatekeeper enabled");
			LOGGER.debug("**************************");
			LOGGER.debug("**************************");
		} else {
		
			LOGGER.debug("Getting Org Communities Tag Cloud:");

			Categories tags = (Categories) service.getOrgCommunitiesTagCloud(true, null, 0, 0,
				null, null, null, null, null);
			assertTrue(tags != null);
			assertEquals("getOrgCommunitiesTag Cloud", 200, service.getRespStatus());

			LOGGER.debug("Finished getting Org Communities Tag Cloud.");
		}
	}

	@Test
	public void verifyOrgCommunitiesServiceDocLink() {
		
		LOGGER.debug("verifyOrgCommunitiesServiceDocLink");

		Map<String, String> communitiesURLs = service.getCommunitiesURLs();

		boolean hasLinkByPath = false;
		for (String key : communitiesURLs.keySet()) {
			
			String path = communitiesURLs.get(key);
			if (path.contains("/communities/org")) {
				hasLinkByPath = true;
				
				// ensure string resource parameters got replaced
				assertFalse(key.contains("{"));
				break;
			}
		}
		
		// TJB 9/24/15 RTC 162697 Gatekeeper api that executes in isListRestrictedGKEnabled
		// is not supported on SC.  I've prevented this code from running on SC.
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			if (isListRestrictedGKEnabled()) {
				assertTrue(hasLinkByPath);
			} else {
				assertFalse(hasLinkByPath);
			}
		}

		LOGGER.debug("Finished verifyOrgCommunitiesServiceDocLink.");
	}

	@Test
	public void getMyCommunities() {
		LOGGER.debug("Getting My Communities:");

		Feed communities = (Feed) service.getMyCommunities(true, null, 0, 0,
				null, null, null, null, null);
		assertTrue(communities != null);

		for (Entry community : communities.getEntries()) {
			LOGGER.debug(community.getTitle());
		}

		LOGGER.debug("Finished getting My Communities...");
	}

	//@Test
	public void getMyCommunitiesByVisitor() {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("Visitor Getting My Communities:");

			Feed communities = (Feed) visitorService.getMyCommunities(true,
					null, 0, 0, null, null, null, null, null);
			assertTrue(communities != null);

			for (Entry community : communities.getEntries()) {
				LOGGER.debug(community.getTitle());
			}

			LOGGER.debug("Finished getting My Communities...");
		}
	}

	@Test
	public void editCommunity() {
		/*
		 * Tests the ability to edit a community Step 1: Create a community Step
		 * 2: Edit the community Step 3: Verify the community info has been
		 */
		LOGGER.debug("Beginning test: Edit community");
		String timeStamp = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community("Community to be updated "
				+ timeStamp, "A community that shall be edited",
				Permissions.PRIVATE, null);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Edit the community");
		String timeStamp2 = Utils.logDateFormatter.format(new Date());
		Entry entryRetrieved = (Entry) assignedService.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		entryRetrieved.setTitle("EditedCommunity " + timeStamp2);
		entryRetrieved.setContent("A community that has now been edited");
		service.putEntry(communityEntry.getEditLinkResolvedHref().toString(),
				entryRetrieved);

		LOGGER.debug("Step 3: Verify the community info has been edited");
		Entry entryCheck = (Entry) assignedService.getCommunity(communityEntry
				.getEditLinkResolvedHref().toString());
		assertTrue(entryCheck.getTitle()
				.equals("EditedCommunity " + timeStamp2));
		assertTrue(entryCheck.getContent().equals(
				"A community that has now been edited"));

		LOGGER.debug("Ending test: Edit community");
	}

	@Test
	public void testCommunityBookmarks() {
		/*
		 * Tests the ability to edit a community bookmark Step 1: Create a
		 * community Step 2: Add a bookmark to that community Step 3: Edit the
		 * bookmark Step 4: Verify that the bookmark now contains the edits Step
		 * 5: Add more bookmarks in the community and verify Step 6: Delete a
		 * bookmark an verify
		 */
		LOGGER.debug("Beginning test: Community bookmark");
		String timeStamp = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a community");
		Community communityRetrieved = getCreatedPrivateCommunity("TestCommunityBookmarks"
				+ timeStamp);

		LOGGER.debug("Step 2: Create a bookmark for that community");
		Bookmark testBookmark = new Bookmark("BookmarkThatNeedsEdits"
				+ timeStamp, "I need an edit, bro!",
				"http://www.test.com/" + timeStamp, null);
		Entry bookmarkResult = (Entry) service.createCommunityBookmark(
				communityRetrieved, testBookmark);
		Entry bookmarkEntry = (Entry) service
				.getCommunityBookmark(bookmarkResult.getEditLinkResolvedHref()
						.toString());
		assertEquals("bookmark title ", testBookmark.getTitle(),
				bookmarkEntry.getTitle());
		LOGGER.debug("Author " + bookmarkEntry.getAuthor().getText());
		LOGGER.debug("Impersonate user " + imUser.getRealName() );

		LOGGER.debug("Step 3: Edit the bookmark");
		String timeStamp2 = Utils.logDateFormatter.format(new Date());
		Bookmark editedBookmark = new Bookmark("EditedBookmark" + timeStamp2,
				"I've been edited bro.", "http://www.testedit.com/" + timeStamp2, null);
		service.editCommunityBookmark(bookmarkResult.getEditLinkResolvedHref()
				.toString(), editedBookmark);

		LOGGER.debug("Step 4: Verify that the bookmark now contains the edits");
		bookmarkEntry = (Entry) service.getCommunityBookmark(bookmarkResult
				.getEditLinkResolvedHref().toString());
		assertEquals("bookmark title ", editedBookmark.getTitle(),
				bookmarkEntry.getTitle());
		// assertTrue(bookmarkEntry.getAlternateLinkResolvedHref().toString().equals(editedBookmark.getLinks().get(":").getHref().toString()));

		LOGGER.debug("Step 5: Add more bookmarks in Community: "
				+ communityRetrieved.getTitle());
		for (int i = 0; i < 2; i++) {
			Bookmark bookmark = new Bookmark(StringGenerator.randomSentence(3),
					StringGenerator.randomSentence(4),
					"http://www.google.com/?q="
							+ RandomStringUtils.randomAlphabetic(10),
					"tagDogear_" + Utils.logDateFormatter.format(new Date()));
			service.createCommunityBookmark(communityRetrieved, bookmark);
		}
		Feed bookmarksFeed = (Feed) service
				.getCommunityBookmarks(communityRetrieved.getBookmarkHref());
		assertEquals(" Bookmarks number ", 3, bookmarksFeed.getEntries().size());

		LOGGER.debug("Step 6: Delete the bookmark and verify");
		service.deleteCommunityBookmark(bookmarkEntry.getEditLinkResolvedHref()
				.toString());
		bookmarksFeed = (Feed) service.getCommunityBookmarks(communityRetrieved
				.getBookmarkHref());
		assertEquals(" Bookmarks number ", 2, bookmarksFeed.getEntries().size());
		for (Entry bookmark : bookmarksFeed.getEntries()) {
			assertFalse(editedBookmark.getTitle() + "should be removed",
					bookmark.getTitle().equals(editedBookmark.getTitle()));
		}

		LOGGER.debug("Ending test: Community bookmark");
	}

	// @Test
	public void getFollowedCommunities() throws FileNotFoundException,
			IOException {
		/*
		 * Tests the ability to create a followed community and get followed
		 * communities Step 1: Create a community Step 2: Follow that community
		 * as another user Step 3: Get followed communities as that user Step 4:
		 * Verify that the community created exists in followed communities Step
		 * 5: Private communities will return 403 for following Step 6: clean
		 * up. unfollowing communities
		 */

		LOGGER.debug("Beginning test: Get followed Communities");
		String timeStamp = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a community");
		// UserPerspective user2 = new UserPerspective(5,
		// Component.COMMUNITIES.toString(), useSSL);
		// CommunitiesService service2 = user2.getCommunitiesService();
		Community testCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			testCommunity = new Community("FollowedCommunity" + timeStamp,
					"A community with followers " + timeStamp,
					Permissions.PRIVATE, null);
			// In order for this test to work, the user must be a member of the
			// community before following. Otherwise 403
		} else {
			testCommunity = new Community("FollowedCommunity" + timeStamp,
					"A community with followers " + timeStamp,
					Permissions.PUBLIC, null);
		}
		Entry communityResult = (Entry) otherUserService
				.createCommunity(testCommunity);
		assertEquals("Create Community failed"+otherUserService.getDetail(),
				201, otherUserService.getRespStatus());


		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			// For private communities, the user must be part of the community
			// in order to follow.
			LOGGER.debug("Add a member . . . ");
			ProfileData testUser = ProfileLoader.getProfile(2);

			Community testCommunityRetrieved = new Community(
					(Entry) otherUserService.getCommunity(communityResult
							.getEditLinkResolvedHref().toString()));
			Member member = new Member(null, testUser.getUserId(),
					Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
			Entry memberEntry = (Entry) otherUserService.addMemberToCommunity(
					testCommunityRetrieved, member);
			assertTrue(memberEntry != null);
		}

		LOGGER.debug("Step 2: Follow that community as another user");
		// Get the community info
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		String id = communityRetrieved.getUuid();
		// Entry creation
		Entry entry = abdera.getFactory().newEntry();
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/source",
				"communities", null);
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-type",
				"community", null);
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-id", id,
				null);
		service.followCommunity(entry);

		LOGGER.debug("Step 3: Get followed communities as that user");
		Feed followedCommunitiesFeed = (Feed) service.getFollowedCommunities();

		LOGGER.debug("Step 4: Verify that the community created exists in followed communities");
		boolean foundCommunity = isCommunityFollowed(testCommunity,
				followedCommunitiesFeed);
		assertTrue(foundCommunity);

		Community testCommunity403 = null;
		LOGGER.debug("Step 5: Private communities, without membership, will return 403 for following");
		testCommunity403 = new Community("FollowedCommunity_403_" + timeStamp,
				"A private community with followers " + timeStamp,
				Permissions.PRIVATE, null);
		Entry communityResult403 = (Entry) otherUserService
				.createCommunity(testCommunity403);
		Community communityRetrieved403 = new Community(
				(Entry) otherUserService.getCommunity(communityResult403
						.getEditLinkResolvedHref().toString()));
		id = communityRetrieved403.getUuid();
		// Entry creation
		Entry entry403 = abdera.getFactory().newEntry();
		entry403.addCategory("http://www.ibm.com/xmlns/prod/sn/source",
				"communities", null);
		entry403.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-type",
				"community", null);
		entry403.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-id",
				id, null);
		service.followCommunity(entry403);
		assertEquals(403, service.getRespStatus());

		LOGGER.debug("Step 6: clean up.  unfollowing communities");
		for (Entry en : followedCommunitiesFeed.getEntries()) {
			if (en.getTitle().equals(testCommunity.getTitle())) {
				String followid = en
						.getId()
						.toString()
						.substring("urn:lsid:ibm.com:follow:resource-".length());
				service.unfollowCommunity(followid);

				// Lastly delete the community, otherwise unpopulate script
				// fails (i think because jones242 is a member of a community
				// but not an owner)
				assertTrue(otherUserService.deleteCommunity(communityRetrieved
						.getLinks()
						.get(StringConstants.REL_EDIT + ":"
								+ StringConstants.MIME_NULL).getHref()
						.toString()));
			}
		}

		LOGGER.debug("Ending test: Get followed communities");
	}

	@Test
	public void unfollowCommunity() throws FileNotFoundException, IOException {
		/*
		 * Tests the ability to unfollow a followed community Step 1: Create a
		 * community (the created community becomes followed by default) Step 2:
		 * Unfollow the community Step 3: Verify the community created does not
		 * exist in followed communities
		 */

		LOGGER.debug("BEGINNING TEST: Unfollow Community");
		String randString = RandomStringUtils.randomAlphanumeric(4);
		String communityName = "Unfollowed Community " + randString;

		LOGGER.debug("Step 1: Create a community");
		// UserPerspective user2 = new UserPerspective(5,
		// Component.COMMUNITIES.toString(), useSSL);
		// CommunitiesService service2 = user2.getCommunitiesService();
		Community testCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			testCommunity = new Community(communityName,
					"A community with followers ", Permissions.PRIVATE, null);
			// In order for this test to work, the user must be a member of the
			// community before following. Otherwise 403
		} else {
			testCommunity = new Community(communityName,
					"A community with followers ", Permissions.PUBLIC, null);
		}
		Entry communityResult = (Entry) otherUserService
				.createCommunity(testCommunity);

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			// For private communities, the user must be part of the community
			// in order to follow.
			LOGGER.debug("Add a member . . . ");
			ProfileData testUser = ProfileLoader.getProfile(2);

			Community testCommunityRetrieved = new Community(
					(Entry) otherUserService.getCommunity(communityResult
							.getEditLinkResolvedHref().toString()));
			Member member = new Member(null, testUser.getUserId(),
					Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
			Entry memberEntry = (Entry) otherUserService.addMemberToCommunity(
					testCommunityRetrieved, member);
			assertTrue(memberEntry != null);
		}

		LOGGER.debug("Step 2: Follow that community as another user");
		// Get the community info
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		String id = communityRetrieved.getUuid();
		// Entry creation
		Entry entry = abdera.getFactory().newEntry();
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/source",
				"communities", null);
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-type",
				"community", null);
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-id", id,
				null);
		service.followCommunity(entry);

		LOGGER.debug("Step 3: Get followed communities as that user");
		Feed followedCommunitiesFeed = (Feed) service.getFollowedCommunities();

		LOGGER.debug("Step 4: Verify that the community created exists in followed communities");
		boolean foundCommunity = false;
		for (Entry en : followedCommunitiesFeed.getEntries()) {
			if (en.getTitle().equalsIgnoreCase(communityName))
				foundCommunity = true;
		}
		if (!foundCommunity && followedCommunitiesFeed.getEntries().size() > 99) {
			LOGGER.debug("-: followed comm is outside "
					+ followedCommunitiesFeed.getEntries().size());
			foundCommunity = true;
		}
		assertTrue(foundCommunity);

		LOGGER.debug("Step 5: Unfollow the community");
		service.unfollowCommunity(id);

		LOGGER.debug("Step 6: Verify the community created does not exist in followed communities");
		Feed finalFollowedFeed = (Feed) service.getFollowedCommunities();
		foundCommunity = false;
		for (Entry en : finalFollowedFeed.getEntries()) {
			if (en.getTitle().equals(testCommunity.getTitle()))
				foundCommunity = true;
		}
		assertFalse(foundCommunity);

		// Delete Community, otherwise unpopulate script fails because ajones242
		// is a member, not owner of the community created for this test.
		assertTrue(otherUserService
				.deleteCommunity(communityRetrieved
						.getLinks()
						.get(StringConstants.REL_EDIT + ":"
								+ StringConstants.MIME_NULL).getHref()
						.toString()));
		LOGGER.debug("ENDING TEST: Unfollow Community");
	}

	@Test
	public void removeCommunityWidget() throws Exception {
		/*
		 * Tests the ability to remove a community widget Step 1: Create a
		 * community Step 2: Add a widget Step 3: Remove the widget Step 4:
		 * Verify the widget does not exist in the community
		 */
		LOGGER.debug("Beginning test: Remove community widget");
		String timeStamp = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community("Test WidgetCommunity "
				+ timeStamp, "A community with widgets to be messed with "
				+ timeStamp, Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Add a widget");
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		Widget widget = new Widget(
				StringConstants.WidgetID.Activities.toString());
		service.postWidget(communityRetrieved, widget.toEntry());
		assertEquals(201, service.getRespStatus());
		// assertEquals(200, service.addWidget(communityRetrieved,
		// "Activities"));

		LOGGER.debug("Step 3: Remove the widget");
		Feed widgetsInitialFeed = (Feed) service
				.getCommunityWidgets(communityRetrieved.getUuid());
		String widgetSelfLink = null;
		for (Entry e : widgetsInitialFeed.getEntries()) {
			if (e.getTitle().equals("Blog")){
				// find the blogs widget entry
				widgetSelfLink = e.getSelfLinkResolvedHref().toString();
			}
		}
		service.deleteCommunityWidget(widgetSelfLink);

		LOGGER.debug("Step 4: Verify the widget does not exist in the community");
		Feed widgetsFinalFeed = (Feed) service
				.getCommunityWidgets(communityRetrieved.getUuid());
		boolean foundWidget = false;
		for (Entry e : widgetsFinalFeed.getEntries()) {
			if (e.getTitle().equals("Blog"))
				foundWidget = true;
		}
		assertFalse(foundWidget);

		LOGGER.debug("Ending test: Remove community widget");
	}

	@Test
	public void testFeedLinks() {
		LOGGER.debug("Create/get/delete feed links in user's communties: ");

		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityFeedLinks"
				+ timeStamp);

		LOGGER.debug("Creating Feed Links in Community: " + community.getTitle());
		for (int i = 0; i < 2; i++) {

			// create - checks title, content, and link and makes sure it is the
			// same as the variables given to create the FeedLink
			String randomTitle = StringGenerator.randomLorem1Sentence()
					+ StringGenerator.randomSentence(4);
			String randomContent = RandomStringUtils
					.randomAlphanumeric(RandomUtils.nextInt(100));
			String randomLink = "http://www.google.com/?q="
					+ RandomStringUtils.randomAlphanumeric(10);
			FeedLink feedLink = new FeedLink(randomTitle, randomContent,
					randomLink, "tag1 two three community link");
			Entry feedLinkResponse = (Entry) service.createFeedLink(community,
					feedLink);

			assertTrue(feedLink.getTitle().equals(randomTitle));
			assertTrue(feedLink.getContent().equals(randomContent));
			assertTrue(feedLink.getLinks().get(":").getHref().toString()
					.equals(randomLink));

			LOGGER.debug("Feed Link: " + feedLink.getTitle()
					+ " successfully created @ "
					+ feedLinkResponse.getEditLinkResolvedHref().toString());

			// get - Compare the href attribute of link rel=edit of the created
			// feedlink and the result of the get
			Entry newEntry = (Entry) service.getFeedLink(feedLinkResponse
					.getEditLinkResolvedHref().toString());

			if (newEntry.getEditLinkResolvedHref().equals(
					feedLinkResponse.getEditLinkResolvedHref())) {
				assertTrue(true);
			} else {
				LOGGER.debug("Test failed: Get did not return the correct results");
				assertTrue(false);
			}
			LOGGER.debug("Get Feed Link: " + feedLink.getTitle()
					+ " successfully");

			// delete - check to see if you can find the link that was deleted
			service.deleteFeedLink(feedLinkResponse.getEditLinkResolvedHref()
					.toString());

			newEntry = (Entry) service.getFeedLink(feedLinkResponse
					.getEditLinkResolvedHref().toString());
			if (feedLinkResponse.getAttributeValue(StringConstants.API_ERROR) == null
					&& newEntry.getAttributeValue(StringConstants.API_ERROR)
							.equals("true")) {
				assertTrue(true);
			} else {
				if (feedLinkResponse
						.getAttributeValue(StringConstants.API_ERROR) != null) {
					LOGGER.debug("Test Failed: FeedLink was not successfully recieved");
				} else {
					LOGGER.debug("Test Failed: Deletion was not successfull");
				}
				assertTrue(false);
			}
			LOGGER.debug("Feed Link: " + feedLink.getTitle() + " was deleted");

		}
		LOGGER.debug("Finished creating/getting/deleting Feed Links in user's Communities...");

	}

	@Test
	public void editCommunityFeeds() throws Exception {
		/*
		 * Tests the ability to edit the feeds widget in a community Step 1:
		 * Create a community Step 2: Create some feeds Step 3: Edit the feeds
		 * Step 4: Verify the feeds have been changed
		 */
		LOGGER.debug("Beginning test: Edit community feeds");
		String timeStamp = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community("EditFeeds Test " + timeStamp,
				"A community with feed links to edit", Permissions.PRIVATE,
				null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Create some feeds");
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		FeedLink testFeedLink1 = new FeedLink("Test Feed1 " + timeStamp,
				"The chill feed", "http://www.testfeedlink1.com/" + timeStamp, "tag");
		FeedLink testFeedLink2 = new FeedLink("Test Feed2 " + timeStamp,
				"The weird feed", "http://www.testfeedlink2.com/" + timeStamp, "tag");
		service.createFeedLink(communityRetrieved, testFeedLink1);
		Entry response2 = (Entry) service.createFeedLink(communityRetrieved,
				testFeedLink2);

		LOGGER.debug("Step 3: Edit the feeds");
		String timeStamp2 = Utils.logDateFormatter.format(new Date());
		Entry feedLink2Entry = (Entry) service.getFeedLink(response2
				.getEditLinkResolvedHref().toString());
		System.out.println(feedLink2Entry.getTitle());
		feedLink2Entry.setTitle("EDITED Test Feed " + timeStamp2);
		feedLink2Entry
				.setContent("A lone feed, trapped in the wrath of my community. When will I be free? "
						+ timeStamp2);
		service.putEntry(feedLink2Entry.getEditLinkResolvedHref().toString(),
				feedLink2Entry);

		LOGGER.debug("Step 4: Verify the feeds have been updated");
		Entry editedEntry = (Entry) service.getFeedLink(response2
				.getEditLinkResolvedHref().toString());
		System.out.println(editedEntry.getTitle());
		assertEquals("Feed title ", "EDITED Test Feed " + timeStamp2,
				editedEntry.getTitle());
		assertTrue(editedEntry.getContent().equals(
				"A lone feed, trapped in the wrath of my community. When will I be free? "
						+ timeStamp2));

		LOGGER.debug("Ending test: Edit community feeds");
	}

	@Test
	public void testSubCommunities() throws Exception {
		/*
		 * Tests sub communities Step 1: Create a community Step 2: Add
		 * Subcommunity Step 3: retrieve Subcommunity Step 4: check the
		 * subcommunity created is displayed with admin user
		 */
		String timeStamp = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community("TestSubComm" + timeStamp,
				"sub community testing " + timeStamp, Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);
		Community community = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		String communityId = community.getUuid();

		// get parent community id ( itself )
		String parentUrl = community.getParentcommunityHref();
		if (parentUrl != null) {
			assertTrue("parent communities not match",
					parentUrl.contains(communityId));
		}

		// ---------------------------
		// Get remoteApp feed
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				community.getRemoteAppsListHref(), true, null, 0, 50, null,
				null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);
		// check if the relatedCommunity widget is on the feed
		boolean related_enabled = false;
		for (Entry entry : remoteAppsFeed.getEntries()) {
			for (Category category : entry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("SubCommunities")) {
					related_enabled = true;
				}
			}
		}
		// if it is not on the list then add it
		if (!related_enabled) {
			Widget widget = new Widget(
					StringConstants.WidgetID.SubcommunityNav.toString());
			service.postWidget(community, widget.toEntry());
			assertEquals(201, service.getRespStatus());
		}
		// --------------------------

		if (community.getSubcommunitiesHref() != null) {
			LOGGER.debug("Creating Subcommunities in Community: "
					+ community.getTitle());

			for (int i = 0; i < 2; i++) {
				LOGGER.debug("Step 2: Creating Subcommunities " + i);

				Subcommunity subcomm = new Subcommunity(
						StringGenerator.randomLorem1Sentence()
								+ StringGenerator.randomSentence(4),
						RandomStringUtils.randomAlphanumeric(RandomUtils
								.nextInt(100)), Permissions.PRIVATE,
						"tag1 two three community link");
				Entry subcommResponse = (Entry) service.createSubcommunity(
						community, subcomm);
				assertTrue(subcommResponse != null);
				LOGGER.debug("Subcommunity: " + subcomm.getTitle()
						+ " successfully created @ "
						+ subcommResponse.getEditLinkResolvedHref().toString());

				LOGGER.debug("Step 3: retrieve Subcommunities " + i);
				Entry subCommunity = (Entry) service
						.getSubcommunity(subcommResponse
								.getEditLinkResolvedHref().toString());
				assertEquals(subcomm.getTitle(), subCommunity.getTitle());

				// get parent community id
				parentUrl = new Community(subCommunity)
						.getParentcommunityHref();
				assertTrue("parent communities not match",
						parentUrl.contains(communityId));

				// defect 43938 - SC not support admin
				LOGGER.debug("Step 4: check the subcommunity created is displayed with admin user "
						+ i);
				if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
					// UserPerspective admin = new UserPerspective(0,
					// Component.COMMUNITIES.toString(), useSSL);
					subCommunity = (Entry) adminService
							.getCommunity(subcommResponse
									.getEditLinkResolvedHref().toString());
					assertEquals(subcomm.getTitle(), subCommunity.getTitle());
				}

			}
		}

		LOGGER.debug("Finished testing Subcommunities in user's Communities...");
	}
	

	@Test
	public void subCommunitiesError() throws Exception {
		/* Steps:
		 * Step 1: Create a community
		 * Step 2: Create a second community
		 * Step 4: Create a third subcommunity 
		 * Step 5: retrieve Subcommunity
		 * Step 6: Update the parent link with the link 
		 * from the community 2
		 * Step 7: Validate the error message
		 */
		LOGGER.debug("BEGINNING TEST: subCommunitiesError  RTC 148577");
		String timeStamp = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community("TestSubComm" + timeStamp,
				"sub community testing " + timeStamp, Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);
		Community community = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		String communityId = community.getUuid();

		// get parent community id ( itself )
		String parentUrl = community.getParentcommunityHref();
		if (parentUrl != null) {
			assertTrue("parent communities not match",
					parentUrl.contains(communityId));
		}


		// Get remoteApp feed
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				community.getRemoteAppsListHref(), true, null, 0, 50, null,
				null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);
		// check if the relatedCommunity widget is on the feed
		boolean related_enabled = false;
		for (Entry entry : remoteAppsFeed.getEntries()) {
			for (Category category : entry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("SubCommunities")) {
					related_enabled = true;
				}
			}
		}
		
		// if it is not on the list then add it
		if (!related_enabled) {
			Widget widget = new Widget(
					StringConstants.WidgetID.SubcommunityNav.toString());
			service.postWidget(community, widget.toEntry());
			assertEquals(201, service.getRespStatus());
		}

		if (community.getSubcommunitiesHref() != null) {
			LOGGER.debug("Creating Subcommunities in Community: "
					+ community.getTitle());

			LOGGER.debug("Step 2: Create a subcommunity ");
			
			Subcommunity subcomm = new Subcommunity( StringGenerator.randomLorem1Sentence() +
					  StringGenerator.randomSentence(4),
					  RandomStringUtils.randomAlphanumeric(RandomUtils .nextInt(101)),
					  Permissions.PRIVATE, "tag1 two three community link");
			
			// This next step is where the subcomm is actually created.
			Entry subcommResponse = (Entry) service.createSubcommunity(
					community, subcomm);
			assertTrue(subcommResponse != null);
			LOGGER.debug("Subcommunity: " + subcomm.getTitle()
					+ " successfully created @ "
					+ subcommResponse.getEditLinkResolvedHref().toString());
			
			String secondCommEditLink = subcommResponse.getEditLinkResolvedHref().toString();
			int ndx = secondCommEditLink.indexOf('=');
			String secondCommUuid = secondCommEditLink.substring(ndx+1);
			
			
			LOGGER.debug("Step 3: Create 2nd subcommunity");
			Subcommunity secondSubComm = new Subcommunity( StringGenerator.randomLorem1Sentence() +
					  StringGenerator.randomSentence(4),
					  RandomStringUtils.randomAlphanumeric(RandomUtils .nextInt(101)),
					  Permissions.PRIVATE, "tag1 two three community link");
			// This next step is where the subcomm is actually created.
			Entry secondSubCommResponse = (Entry) service.createSubcommunity(
					community, secondSubComm);
			assertTrue(secondSubCommResponse != null);
			LOGGER.debug("Subcommunity: " + secondSubComm.getTitle()
					+ " successfully created @ "
					+ secondSubCommResponse.getEditLinkResolvedHref().toString());
			
			
			LOGGER.debug("Step 4: retrieve Subcommunity " );
			Entry subCommunity = (Entry) service
					.getSubcommunity(secondSubCommResponse
							.getEditLinkResolvedHref().toString());
			// Make sure we got the right subcommunity (based on the third community)
			assertEquals(subCommunity.getTitle(), secondSubComm.getTitle()); 
			
			LOGGER.debug("Step 5: Update the parent link with the link from the community 2" ); 
			String editLink = subCommunity.getEditLinkResolvedHref().toString();
			int ndx2 = editLink.indexOf('=');
			String parentLink = editLink.substring(0, ndx2+1) + secondCommUuid;
			
			for (Link lnk : subCommunity.getLinks()) {
				if(lnk.getAttributeValue("rel").contains("parentcommunity")){
					lnk.setHref(parentLink);
				}
			}
			
			// Remove the value of snx:handle
			for (Element l : subCommunity.getElements()) {
				if(l.toString().startsWith("<snx:handle")){
					l.setText("");
				}
			}
			
			Subcommunity sc = new Subcommunity(subCommunity);
			
			//This next line should produce the error message.
			service.editSubcommunity(editLink, sc);
			
			LOGGER.debug("Step 6: Validate the error message");
			String expectedErrorMessage = "CLFRM0251E: Cannot add a Subcommunity to another Subcommunity.";
			assertEquals("The correct error message was not retrieved",
					expectedErrorMessage, service.getRespErrorMsg());
			assertEquals("Correct HTTP return code was not retrieved", 400,
					service.getRespStatus());
		}

		LOGGER.debug("ENDING TEST: subCommunitiesError RTC 148577");
	}
	

	@Test
	public void testForumTopics() {
		LOGGER.debug("Creating, Retrieving and Deleting Forum Topics in user's communties: ");

		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityCalendarTest"
				+ timeStamp);

		String randomTitle = StringGenerator.randomLorem1Sentence()
				+ StringGenerator.randomSentence(4);
		String randomLorem = StringUtils.join(StringConstants.LOREM_1);
		Entry resultFeed;

		LOGGER.debug("1. Pinned Forum Topics in Community: "
				+ community.getTitle());
		// Create Pinned Topic
		ForumTopic newTopicPinned = new ForumTopic(randomTitle, randomLorem,
				true, false, false, false);
		Entry pinnedResult = (Entry) service.createForumTopic(community,
				newTopicPinned);
		assertTrue(pinnedResult != null);
		assertTrue(pinnedResult.getTitle().equals(randomTitle));
		assertTrue(pinnedResult.getContent().trim().equals(randomLorem.trim()));
		assertEquals(" impersonate userName not match ", imUser.getRealName(),
				pinnedResult.getAuthor().getName());
		LOGGER.debug("Created Pinned Topic: " + newTopicPinned.getTitle());
		// Retrieve
		resultFeed = (Entry) service.getForumTopic(pinnedResult.getEditLink()
				.getHref().toString());
		System.out.println(pinnedResult.getEditLink().getHref().toString());
		assertTrue(resultFeed != null);
		assertTrue(resultFeed.getTitle().equals(randomTitle));
		assertTrue(resultFeed.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Retrieved Pinned Topic: " + newTopicPinned.getTitle());

		// RTC 76544 Pinned Topics must be at the top in community topics
		// listing (API)
		ArrayList<ForumTopic> topics = service
				.getCommunityForumTopics(community);
		for (ForumTopic forumTopic : topics) {
			assertTrue(forumTopic.getTitle().equals(randomTitle));
			assertTrue(forumTopic.getContent().trim()
					.equals(randomLorem.trim()));
			// assertEquals(" impersonate userName not match ",
			// imUser.getRealName(),
			// forumTopic.getAuthors().pinnedResult.getAuthor().getName());

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
		assertTrue(pinnedReplyResult != null);
		assertTrue(pinnedReplyResult.getTitle().equals(randomTitle));
		assertTrue(pinnedReplyResult.getContent().trim()
				.equals(randomLorem.trim()));
		LOGGER.debug("Created Pinned Topic Reply: " + pinnedReply.getTitle());
		// Retrieve
		resultFeed = (Entry) service.getForumTopic(pinnedReplyResult
				.getEditLink().getHref().toString());
		assertTrue(resultFeed != null);
		assertTrue(resultFeed.getTitle().equals(randomTitle));
		assertTrue(resultFeed.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Retrieved Pinned Topic Reply: " + pinnedReply.getTitle());

		// Delete Pinned Reply
		service.deleteForumReply(pinnedReplyResult.getEditLink().getHref()
				.toString());
		resultFeed = (Entry) service.getForumTopic(pinnedReplyResult
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
		service.deleteForumTopic(pinnedResult.getEditLink().getHref()
				.toString());
		resultFeed = (Entry) service.getForumTopic(pinnedResult.getEditLink()
				.getHref().toString());
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
		resultFeed = (Entry) service.getForumTopic(lockedResult.getEditLink()
				.getHref().toString());
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
		resultFeed = (Entry) service.getForumTopic(lockedReplyResult
				.getEditLink().getHref().toString());
		assertTrue(resultFeed != null);
		assertTrue(resultFeed.getTitle().equals(randomTitle));
		assertTrue(resultFeed.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Retrieved Locked Topic Reply: " + lockedReply.getTitle());

		// Delete Locked Reply
		service.deleteForumReply(lockedReplyResult.getEditLink().getHref()
				.toString());
		resultFeed = (Entry) service.getForumTopic(lockedReplyResult
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
		service.deleteForumTopic(lockedResult.getEditLink().getHref()
				.toString());
		resultFeed = (Entry) service.getForumTopic(lockedResult.getEditLink()
				.getHref().toString());
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
		resultFeed = (Entry) service.getForumTopic(questionResult.getEditLink()
				.getHref().toString());
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
		resultFeed = (Entry) service.getForumTopic(questionReply2Result
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

		resultFeed = (Entry) service.getForumTopic(questionReplyResult
				.getEditLink().getHref().toString());
		assertTrue(resultFeed != null);
		assertTrue(resultFeed.getTitle().equals(randomTitle));
		assertTrue(resultFeed.getContent().trim().equals(randomLorem.trim()));
		LOGGER.debug("Retrieved Question Topic Reply 2: "
				+ questionReply.getTitle());

		// Delete Question Reply
		service.deleteForumTopic(questionReply2Result.getEditLink().getHref()
				.toString());
		resultFeed = (Entry) service.getForumTopic(questionReply2Result
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
		service.deleteForumTopic(questionResult.getEditLink().getHref()
				.toString());
		resultFeed = (Entry) service.getForumTopic(questionResult.getEditLink()
				.getHref().toString());
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
	public void testCommunityBroadcast() {
		LOGGER.debug("test Community Broadcast:");

		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
				0, null, null, null, null, null);
		assertTrue(communitiesFeed != null);

		ArrayList<Community> communities = new ArrayList<Community>();

		for (Entry communityEntry : communitiesFeed.getEntries()) {
			communities.add(new Community(communityEntry));
		}

		for (Community community : communities) {
			LOGGER.debug("Broadcast Community: " + community.getTitle());

			Entry communityBroadcastEntry = Abdera.getInstance().newEntry();

			String subject = "abc";
			communityBroadcastEntry.setTitle(subject);
			String body = "def";
			communityBroadcastEntry.setContent(body);
			Element recipientsElement = Abdera
					.getInstance()
					.getFactory()
					.newExtensionElement(StringConstants.SNX_RECIPIENTS,
							communityBroadcastEntry);
			String recipients = "allMembers"; // "owners";
			recipientsElement.setText(recipients);

			Person person = community.getAuthors().iterator().next();
			// String userName = person.getName();
			String userId = person
					.getSimpleExtension(StringConstants.SNX_USERID);

			String id = "communityBroadcast:" + community.getUuid() + ":"
					+ userId;
			communityBroadcastEntry.setId(id);

			communityBroadcastEntry.addAuthor(person);

			Entry result = (Entry) service.broadcastMail(community,
					communityBroadcastEntry);
			assertTrue(result != null);
		}

		LOGGER.debug("Finished test Community Broadcast...");
	}

	@Test
	public void testCommunityRequestToJoin() throws Exception {
		LOGGER.debug("test Community RequestToJoin:");

		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			Entry requestToJoinEntry = Abdera.getInstance().newEntry();
			String title = "Request for " + otherUser.getUserName() + " to join.";
			requestToJoinEntry.setTitle(title);
			requestToJoinEntry.addCategory(StringConstants.SCHEME_COMPONENT,
					"communities", null); // ApiConstants.SocialNetworking.TERM_COMMUNITIES,
			requestToJoinEntry.setContent("RequestToJoin");

			if (isListRestrictedGKEnabled()) {			
				LOGGER.debug("Private Community RequestToJoin :");
				String timeStamp = Utils.logDateFormatter.format(new Date());
				Community community = getCreatedPrivateCommunity("RequestToJoinRBL"
						+ timeStamp);
		
				LOGGER.debug("RequestToJoin Community: " + community.getTitle());
				// UserPerspective otherUser = new
				// UserPerspective(StringConstants.RANDOM1_USER,
				// Component.COMMUNITIES.toString(), useSSL);
		
				otherUserService.requestToJoinEntry(community, requestToJoinEntry);
				assertEquals("Community RequestToJoin", 403,
						otherUserService.getRespStatus());
				
				community.setListWhenPrivate(true);
				service.editCommunity(community.getEditLink(), community);
		
				otherUserService.requestToJoinEntry(community, requestToJoinEntry);
				assertEquals("Community RequestToJoin", 201,
						otherUserService.getRespStatus());
			}
		
			LOGGER.debug("PublicInviteOnly Community RequestToJoin :");
			String timeStamp = Utils.logDateFormatter.format(new Date());
			Community community = getCreatedPublicInviteOnlyCommunity("RequestToJoin"
					+ timeStamp);
			otherUserService.requestToJoinEntry(community, requestToJoinEntry);
			assertEquals("Community RequestToJoin " + service.getDetail(), 201,
					otherUserService.getRespStatus());

			LOGGER.debug("Public Community RequestToJoin :");
			timeStamp = Utils.logDateFormatter.format(new Date());
			community = getCreatedPublicCommunity("RequestToJoin2" + timeStamp);
			otherUserService.requestToJoinEntry(community, requestToJoinEntry);
			assertEquals("Community RequestToJoin", 400,
					otherUserService.getRespStatus());
		}

		LOGGER.debug("Finished test Community RequestToJoin...");
	}

	/*
	 * private boolean checkCommunityBlogUUID(Community community) { boolean
	 * UUIDChecked = false; Feed remoteAppsFeed = (Feed)
	 * service.getCommunityRemoteAPPs(community.getRemoteAppsListHref(), true,
	 * null, 0, 50, null, null, SortBy.NAME, SortOrder.ASC, SortField.NAME,
	 * null) ; assertTrue(remoteAppsFeed != null);
	 * 
	 * for (Entry entry : remoteAppsFeed.getEntries()){ for (Category category :
	 * entry.getCategories()){ if (category.getTerm().equalsIgnoreCase("blog"))
	 * {
	 * 
	 * Feed blogFeed = service.getCommunityBlog(entry); if (blogFeed != null) {
	 * for(Entry blogEntry : blogFeed.getEntries()) { UUIDChecked = true; String
	 * sComUID =
	 * blogEntry.getFirstChild(StringConstants.SNX_COMMUNITY_UUID).getText();
	 * assertTrue(sComUID != null); } }
	 * 
	 * if (UUIDChecked) return true; } } } return UUIDChecked; }
	 */

	@Test
	public void testRelatedCommunities() throws Exception {

		/*
		 * Test Related communities Step 1: Create two communities for test Step
		 * 2: Enable related community Step 3: create related community Step 4:
		 * retrieve related communities feed Step 5: retrieve related community
		 * entry Step 6: update related community Step 7: delete related
		 * community
		 */
		LOGGER.debug("BEGINNING TEST: Get Feed of Related Communities");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String commName = "Boston Celtics" + timeStamp;
		String relatedcommName = "Boston Bruins" + timeStamp;

		// create two communities
		Community newCommunity = null;
		Community relatedCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community(commName,
					"Official Connections Community of the Boston Celtics",
					Permissions.PRIVATE, "tagCommunities_"
							+ Utils.logDateFormatter.format(new Date()));
			relatedCommunity = new Community(relatedcommName,
					"Official Connections Community of the Boston Bruins",
					Permissions.PRIVATE, "tagCommunities_"
							+ Utils.logDateFormatter.format(new Date()));
		} else {
			newCommunity = new Community(commName,
					"Official Connections Community of the Boston Celtics",
					Permissions.PUBLIC, "tagCommunities_"
							+ Utils.logDateFormatter.format(new Date()));
			relatedCommunity = new Community(relatedcommName,
					"Official Connections Community of the Boston Bruins",
					Permissions.PUBLIC, "tagCommunities_"
							+ Utils.logDateFormatter.format(new Date()));
		}

		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertEquals("create community failed", 201, service.getRespStatus());
		Entry relCommunityResult = (Entry) service
				.createCommunity(relatedCommunity);
		assertEquals("create community failed", 201, service.getRespStatus());

		// get this two community entires
		Community comm = new Community(
				(Entry) assignedService.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		Community relatedComm = new Community(
				(Entry) assignedService.getCommunity(relCommunityResult
						.getEditLinkResolvedHref().toString()));

		// obtain related community service Document
		// TODO: check API doc to confirm, return 400 now..
		ExtensibleElement cs = assignedService
				.getRelatedCommunityServiceDoc(relatedComm.getUuid());
		// assertEquals("Get Service Doc", 200, service.getRespStatus());

		// Get remoteApp feed
		Feed remoteAppsFeed = (Feed) assignedService.getCommunityRemoteAPPs(
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
		// if it is not on the list then add it
		if (!related_enabled) {
			Widget widget = new Widget(
					StringConstants.WidgetID.RelatedCommunities.toString());
			service.postWidget(comm, widget.toEntry());
			assertEquals(201, service.getRespStatus());
		}

		// Construct Related Community Entry
		Entry relatedCommEntry = relatedCommunity.toEntry();

		String alternateLink = relatedComm.getAlternateLink();
		relatedCommEntry.addLink(alternateLink,
				"http://www.ibm.com/xmlns/prod/sn/related-community",
				"text/html", null, null, 0);

		// set Community term to Related Community
		for (Category c : relatedCommEntry.getCategories()) {
			if (c.getLabel() != null && c.getLabel().equals("Community"))
				c.setTerm("relatedCommunity");
		}

		// create related community
		Entry result = (Entry) service.createRelatedCommunity(comm.getUuid(),
				relatedCommEntry);
		assertEquals("create related community failed", 201,
				service.getRespStatus());

		String idString = result.getId().toString();
		String relatedCommId = idString
				.substring(idString.lastIndexOf(':') + 1);

		// Retrieve feed of Related Communities list
		Feed relatedCommunities = (Feed) assignedService.getRelatedCommunitiesFeed(comm
				.getUuid());
		String resultIdString = relatedCommunities.getEntries().get(0).getId()
				.toString();
		String resultCommId = resultIdString.substring(resultIdString
				.lastIndexOf(':') + 1);

		// validate
		if (relatedCommunities.getTitle().equals(
				"Related Communities of " + commName)
				&& resultCommId.equals(relatedCommId)) {
			LOGGER.debug("SUCCESS: Feed of Related Communities Retrieved");
		} else {
			LOGGER.warn("ERROR: Feed of Related Communities Not Found");
			assertTrue("Verify related community failed", false);
		}

		// another API verify
		// Retrieve entry of a Related Community
		Entry relatedCommunityResult = (Entry) assignedService
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

		// update related community
		// Update meta data
		relatedCommEntry.setTitle("New England Patriots");
		Entry updateResult = (Entry) service.updateRelatedCommunity(
				relatedCommId, relatedCommEntry);

		String updateIdString = updateResult.getId().toString();
		String updateCommId = updateIdString.substring(updateIdString
				.lastIndexOf(':') + 1);

		// validate
		if (updateResult.getTitle().equals("New England Patriots")
				&& updateCommId.equals(relatedCommId)) {
			LOGGER.debug("SUCCESS: Related Community was update correctly");
		} else {
			LOGGER.warn("ERROR: Related Community was NOT updated correctly");
			assertTrue("Related Community was NOT updated", false);
		}

		// delete related community
		boolean deleted = assignedService.deleteRelatedCommunuity(relatedCommId);

		// get feed of Related Communities
		relatedCommunities = (Feed) assignedService.getRelatedCommunitiesFeed(comm
				.getUuid());

		// validate
		if (deleted && relatedCommunities.getEntries().size() == 0) {
			LOGGER.debug("SUCCESS: Related Community was deleted");
		} else {
			LOGGER.warn("ERROR: Related Community was NOT deleted");
			assertTrue("Related Community was NOT deleted", false);
		}
	}

	@Test
	public void missingNsError() throws Exception {
		LOGGER.debug("BEGINNING TEST: RTC 79562 Test return code for malformed broadcast entry");

		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
				0, null, null, null, null, null);
		assertTrue(communitiesFeed != null);

		String url = null;
		for (Entry entry : communitiesFeed.getEntries()) {
			entry.toString();
			for (Element lmnts : entry.getElements()) {
				for (QName atrb : lmnts.getAttributes()) {
					if (atrb.toString().equalsIgnoreCase("href")
							&& lmnts.getAttributeValue("href").contains(
									"broadcasts")) {
						url = lmnts.getAttributeValue("href");
					}
				}
			}
		}

		/*
		 * Create Entry for like/recommend. This is the right way to build the
		 * entry but abdera automatically inserts the namespace which allows the
		 * entry to pass, however we want it to fail and return 400.
		 * 
		 * The namespace that should be missing is
		 * xmlns:snx="http://www.ibm.com/xmlns/prod/sn"
		 */
		Factory factory = abdera.getFactory();
		Entry comEntry = factory.newEntry();
		comEntry.setTitle("email subject");
		comEntry.setContent("Not much there");
		comEntry.addSimpleExtension(StringConstants.SNX_RECIPIENTS,
				"[allMembers]|[owners]");
		comEntry.toString();

		String ntry = "<entry xmlns=\"http://www.w3.org/2005/Atom\"><title type=\"text\">email subject</title><content type=\"text\">Not much there</content><snx:recipients>[allMembers]|[owners]</snx:recipients></entry>";
		service.postResponseString(url, ntry);
		// service.postResponseString(url, comEntry.toString());
		// service.postEntry(url, comEntry);
		assertEquals(400, service.getRespStatus());

		LOGGER.debug("END TEST: RTC 79562 Test return code for malformed broadcast entry");
	}

	@Test
	public void createAdditionalIdeationBadRequest() throws Exception {
		LOGGER.debug("BEGIN TEST: test create ideation blog via API bad request");
		/*
		 * Steps: 1. Create Community 2. Add ideation blog widget 3. Create
		 * additional ideation blog with API
		 */
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		Community newCommunity = new Community(
				"test create ideation blog via API bad request "
						+ uniqueNameAddition,
				"Create additional ideation blog with API bad request",
				Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);
		Community comm = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		// Add blogs widget
		Feed widgetsInitialFeed = (Feed) service.getCommunityWidgets(comm.getUuid());
		boolean blogFound = false;
		for (Entry e : widgetsInitialFeed.getEntries()) {
			if (e.getTitle().equals("Blog")) {
				blogFound = true;
				break;
			}
		}
		if (blogFound == false) {
			Widget widget = new Widget(StringConstants.WidgetID.Blog.toString());
			service.postWidget(comm, widget.toEntry());
			assertEquals(201, service.getRespStatus());
		}

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
		String communityBlogUrl = null;
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
				if (category.getTerm().equalsIgnoreCase("Blog")) {
					for (Link link : raEntry.getLinks()) {
						if (link.getRel()
								.contains("remote-application/publish")) {
							communityBlogUrl = link.getHref().toString();
						}
					}
				}
			}
		}

		assertNotNull("communtiy blogs service doc url", communityBlogUrl);
		assertNotNull("ideation blogs service doc url", ideationBlogUrl);

		// This call returns a service doc, not a feed. EE doesn't seem to
		// support direct retrieval of workspaces, collections, etc
		// so the code below does the parsing. What we are trying to get is the
		// link used to post to IdeationBlog widget in communities.
		// The blogHandleUrl is used for voting.
		String createIdeationUrl = null;
		ExtensibleElement ee = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ee = service.getAnyFeedWithRedirect(ideationBlogUrl);
		} else {
			ee = service.getAnyFeed(ideationBlogUrl);
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

		String createBlogUrl = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ee = service.getAnyFeedWithRedirect(communityBlogUrl);
		} else {
			ee = service.getAnyFeed(communityBlogUrl);
		}
		assertTrue(ee != null);
		svc = (Service) ee;
		for (Workspace ws : svc.getWorkspaces()) {
			for (Collection c : ws.getCollections()) {
				String href = c.getHref().toString();
				if (href != null && href.contains("commUuid")) {
					createBlogUrl = href;
					break;
				}
			}
		}
		Blog blog = null;
		// bad request 1: create community blog on blogs API
		blog = new Blog("create community blog on blogs API", "Bad_request_1",
				"create community blog on blogs API", "tag1 tag2", true, false,
				null, null, TimeZone.getDefault(), true, 13, true, true, true,
				0, -1, null, comm.getUuid(), null, 0);
		service.postBlog(createBlogUrl, blog.toEntry());
		// bad request
		assertEquals("response status", 400, service.getRespStatus());

		// bad request 2: create community blog on ideation API
		blog = new Blog("create community blog on ideation API",
				"Bad_request_2", "create community blog on ideation API",
				"tag1 tag2", true, false, null, null, TimeZone.getDefault(),
				true, 13, true, true, true, 0, -1, null, comm.getUuid(), null,
				0);
		service.postBlog(createIdeationUrl, blog.toEntry());
		// bad request
		assertEquals("response status", 400, service.getRespStatus());

		// bad request 3: non-community owner create ideation blog
		blog = new Blog(
				"bad request 3: non-community own create ideation blog",
				"Bad_request_3",
				"bad request 3: non-community own create ideation blog",
				"tag1 tag2", false, true, null, null, TimeZone.getDefault(),
				true, 13, true, true, true, 0, -1, null, comm.getUuid(), null,
				0);
		otherUserService.postBlog(createIdeationUrl, blog.toEntry());
		// forbidden
		assertEquals("response status", 403, otherUserService.getRespStatus());

		uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		newCommunity = new Community(
				"test create ideation blog via API bad request "
						+ uniqueNameAddition,
				"Create additional ideation blog with API bad request",
				Permissions.PRIVATE, null);
		communityResult = (Entry) otherUserService
				.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		blog = new Blog(
				"bad request 3: non-community own create ideation blog",
				"Bad_request_3",
				"bad request 3: non-community own create ideation blog",
				"tag1 tag2", false, true, null, null, TimeZone.getDefault(),
				true, 13, true, true, true, 0, -1, null, comm.getUuid(), null,
				0);
		otherUserService.postBlog(createIdeationUrl, blog.toEntry());
		// forbidden
		assertEquals("response status", 403, otherUserService.getRespStatus());

		LOGGER.debug("END TEST: test create ideation blog via API bad request");
	}

	@Test
	public void createAdditionalIdeation() throws Exception {
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
		// This call returns a service doc, not a feed.
		String createIdeationUrl = null;
		ExtensibleElement ee = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ee = service.getAnyFeedWithRedirect(ideationBlogUrl);
		} else {
			ee = service.getAnyFeed(ideationBlogUrl);
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
			ee = service.getAnyFeedWithRedirect(ideationBlogUrl);
		} else {
			ee = service.getAnyFeed(ideationBlogUrl);
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

	// @Test
	public void ideationAppAccept() throws Exception {
		LOGGER.debug("BEGINNING TEST: RTC 84709 'Voted for' entry recommendation ontains <app:accept/>");
		/*
		 * Steps: 
		 * 1. Create Community 
		 * 2. Add ideation blog widget 
		 * 3. Create ideation blog entries 
		 * 4. Post some votes for some entries, but not all 
		 * 5. Freeze the idea blog 6. Validate the <app:accept/> content
		 * 
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
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertEquals("create comm ",201, service.getRespStatus());
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
			ee = service.getAnyFeedWithRedirect(ideationBlogUrl);
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
		service.postIdeationBlog(postIdeaBlogUrl, entry);
		service.postIdeationBlog(postIdeaBlogUrl, entry2);
		service.postIdeationBlog(postIdeaBlogUrl, entry3);

		// RTC#137931 check href attribute in ideation blog feed
		Feed feed = null;
		String query = communityResult.getEditLinkResolvedHref().getQuery();
		String communityIdeaUrl = blogHandleUrl.substring(0,
				blogHandleUrl.indexOf("api/blogs"))
				+ "feed/ideas/atom?" + query + "&queryType=ideas";
		ExtensibleElement eelement = service.getAnyFeedWithRedirect(communityIdeaUrl);
		assertEquals("getIdeasBlogsFeedWithRedirect failed "+service.getDetail(), 200, service.getRespStatus());
		feed = (Feed) eelement;
		
		List<Entry> entries = feed.getEntries();
		assertEquals(3, entries.size());
		for (int i = 0; i < entries.size(); i++) {
			Entry e = entries.get(i);
			// <link rel="self" type="application/atom+xml" href=""/>
			// <link rel="replies" type="application/atom+xml" href=""
			// thr:count="0"/>
			// <link rel="http://www.ibm.com/xmlns/prod/sn/recommendations"
			// href=""/>
			// <app:collection href="">
			String href = e.getLink("self").getHref().toString();
			assertNotNull(href);
			assertFalse("".equals(href.trim()));
			href = e.getLink("replies").getHref().toString();
			assertNotNull(href);
			assertFalse("".equals(href.trim()));
			href = e.getLink("http://www.ibm.com/xmlns/prod/sn/recommendations")
					.getHref().toString();
			assertNotNull(href);
			assertFalse("".equals(href.trim()));
			Element ele = e.getExtension(new QName(
					"http://www.w3.org/2007/app", "collection", "app"));
			href = ele.getAttributeValue("href");
			assertNotNull(href);
			assertFalse("".equals(href.trim()));
		}

		/*
		 * This next section of code creates votes. blogHandleUrl was defined
		 * earlier in the code.
		 */

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
			feed = (Feed) service.getAnyFeedWithRedirect(urlIdeationFeedUrl);
		} else {
			feed = (Feed) service.getAnyFeed(blogDashboardUrl);
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
		service.postIdeationRecommendation(idea1RecommendUrl);
		service.postIdeationRecommendation(idea2RecommendUrl);

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
			fd = (Feed) service.getAnyFeed(blogHandleUrl);

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
		ExtensibleElement eeEntry = service.getAnyFeed(editUrl);
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
		service.putEntry(editUrl, (Entry) eeEntry);

		// Verify the contents.
		Feed dashboardFeed = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			// TODO API  /blogs/homepage/feed/entries/atom  
			//    in SC return html page instead of xml feed, worked before
			//dashboardFeed = (Feed) service.getAnyFeedWithRedirect(blogDashboardUrl);
			ExtensibleElement ele = service.getAnyFeedWithRedirect(blogDashboardUrl);
			boolean flag = ele.toString().contains("empty response");
			assertFalse(" /blogs/homepage/feed/entries/atom API failed on SC", flag);
			dashboardFeed = (Feed) ele;
			
		} else {
			dashboardFeed = (Feed) service.getAnyFeed(blogDashboardUrl);
		}
		assertEquals("get dashboardFeed "+dashboardFeed+service.getDetail(), 200, service.getRespStatus());
		
		// Feed dashboardFeed = (Feed) bService.getBlogFeed(blogDashboardUrl);
		boolean isRecommendFound = false;
		boolean isRecommendFound2 = false;
		boolean isRecommendFound3 = false;

		// app:accept should exist for idea1 and idea2, it will not have a value.
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
								assertEquals(
										"app:accept ",
										"<app:accept xmlns:app=\"http://www.w3.org/2007/app\" />",
										ele2.toString());
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
	public void ideationVoteLimit() throws Exception {
		LOGGER.debug("BEGINNING TEST: RTC 87763 Feed should contains vote limit number info");
		/*
		 * Steps: 1. Create Community 2. Add ideation blog widget 3. Create
		 * ideation blog entries. 4. Post some votes 5. Validate voting limit
		 * element in the feed. 6. Prgrammatically increase voting limits 7.
		 * Validate voting limit again.
		 */

		/*
		 * This addition allows multiple executions of this test. Otherwise, a
		 * delete method would be needed to erase the community at the start of
		 * each test.
		 */
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

		// create community
		Community newCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community("RTC 87763 Ideation Blog vote limit "
					+ uniqueNameAddition,
					"Feed should contains vote limit number info",
					Permissions.PRIVATE, null);
		} else {
			newCommunity = new Community("RTC 87763 Ideation Blog vote limit "
					+ uniqueNameAddition,
					"Feed should contains vote limit number info",
					Permissions.PUBLIC, null);
		}
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
		// assertEquals(200, service.addWidget(comm, "IdeationBlog"));

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
			ee = service.getAnyFeedWithRedirect(ideationBlogUrl);
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
		entry.setTitle("RTC 87763 Idea1");
		entry.setId("some-id-should-be-ignored");
		entry.setContent("idea1 content");
		entry.addCategory("DataPopIdeaBlogEntry");
		entry.toString();

		Entry entry2 = factory.newEntry();
		entry2.setTitle("RTC 87763 Idea2");
		entry2.setId("some-id-should-be-ignored");
		entry2.setContent("idea2 content");
		entry2.addCategory("DataPopIdeaBlogEntry");

		// These just create the ideation blog only - no votes/recommends
		service.postIdeationBlog(postIdeaBlogUrl, entry);
		service.postIdeationBlog(postIdeaBlogUrl, entry2);

		/*
		 * This next section of code creates votes. blogHandleUrl was defined
		 * earlier in the code.
		 */
		String blogDashboardUrl = blogHandleUrl.substring(0,
				blogHandleUrl.indexOf("api/blogs"))
				+ "feed/entries/atom";
		Feed feed = null;

		String urlIdeationFeedUrl = "";
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			// Document<Feed> feed_doc =
			// client.get(blogDashboardUrl).getDocument();
			// feed = feed_doc.getRoot();

			String wIdExtra = postIdeaBlogUrl.substring(
					postIdeaBlogUrl.indexOf("blogs/"),
					postIdeaBlogUrl.indexOf("/api"));
			String wId = wIdExtra.substring(wIdExtra.indexOf("/") + 1);
			// postIdeaBlogUrl
			// https://apps.collabservdaily.swg.usma.ibm.com/blogs/W4c9e0c0a8a3b_45de_ace5_74ff33a02eea/api/entries
			urlIdeationFeedUrl = URLConstants.SERVER_URL
					+ "/blogs/roller-ui/rendering/feed/" + wId
					+ "/entries/atom";
			// Document<Feed> feed_doc =
			// client.get(urlIdeationFeedUrl).getDocument();
			// feed = feed_doc.getRoot();

			feed = (Feed) service.getAnyFeedWithRedirect(urlIdeationFeedUrl);
		} else {
			feed = (Feed) service.getAnyFeed(blogDashboardUrl);
			feed.toString();
		}

		String idea1RecommendUrl = null;
		String idea2RecommendUrl = null;
		for (Entry ibEntry : feed.getEntries()) {
			if (ibEntry.getTitle().equalsIgnoreCase("RTC 87763 Idea1")) {
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
			if (ibEntry.getTitle().equalsIgnoreCase("RTC 87763 Idea2")) {
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
		service.postIdeationRecommendation(idea1RecommendUrl);
		service.postIdeationRecommendation(idea2RecommendUrl);

		// Validate using Feed Blog Entries API
		String server = URLConstants.SERVER_URL;
		// Don't like building APIs manually. Don't know where/how to get this
		// programmatically.
		String testUrl = server + "/blogs/";
		String id = postIdeaBlogUrl.substring(testUrl.length(),
				postIdeaBlogUrl.indexOf("/api/entries"));
		String entriesUrl = testUrl + "roller-ui/rendering/feed/" + id
				+ "/entries/atom";

		Feed validationFeed = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			validationFeed = (Feed) service.getAnyFeedWithRedirect(entriesUrl);
		} else {
			validationFeed = (Feed) service.getAnyFeed(entriesUrl);
		}

		for (Entry voteEntry : validationFeed.getEntries()) {
			if (voteEntry.getTitle().equalsIgnoreCase("RTC 87763 Idea1")) {
				for (Element ele : voteEntry.getElements()) {
					if (ele.toString().startsWith("<source")) {
						for (Element subE : ele.getElements()) {
							if (subE.toString().startsWith("<voteLimit")) {
								assertTrue(subE
										.toString()
										.equalsIgnoreCase(
												"<voteLimit xmlns=\"http://www.w3.org/2005/Atom\">0</voteLimit>"));
							}
						}
					}
				}
			}
		}

		/*
		 * This next block of code validates the <votingLimit> element again,
		 * but first the voting limit is increased to '5'.
		 */
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
			fd = (Feed) service.getAnyFeed(blogHandleUrl);

			for (Entry ibEntry : fd.getEntries()) {
				if (ibEntry.getTitle().equalsIgnoreCase(
						"RTC 87763 Ideation Blog vote limit "
								+ uniqueNameAddition)) {
					// Get the edit link that looks like this:
					// https://lc45linux2.swg.usma.ibm.com:443/blogs/Handle03913360811/api/blogs/70241e8a-8658-4d5e-a2db-eb0b83783ab9
					editUrl = ibEntry.getEditLinkResolvedHref().toURL()
							.toString();
				}
			}
		}

		ExtensibleElement eeEntry = service.getAnyFeed(editUrl);

		for (Element ele : eeEntry.getElements()) {
			if (ele.toString().startsWith("<app:control")) {
				for (Element subEle : ele.getElements()) {
					if (subEle.toString().startsWith("<snx:voteLimit")) {
						for (QName atrb : subEle.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("enabled")
									&& subEle.getAttributeValue("enabled")
											.equalsIgnoreCase("false")) {
								subEle.setAttributeValue("enabled", "true");
								subEle.setAttributeValue("limit", "5");
							}
						}
					}
				}
			}
		}
		// Now, use this entry with the edit link with http PUT. This will set
		// voting limits.
		service.putEntry(editUrl, (Entry) eeEntry);
		Feed validationFeed2 = null;

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			validationFeed2 = (Feed) service.getAnyFeedWithRedirect(entriesUrl);
		} else {
			validationFeed2 = (Feed) service.getAnyFeed(entriesUrl);
		}

		// Validate "voteLimit" for 2nd ideation blog. This one should be
		// <voteLimit >5</voteLimit>
		for (Entry voteEntry : validationFeed2.getEntries()) {
			if (voteEntry.getTitle().equalsIgnoreCase("RTC 87763 Idea2")) {
				for (Element ele : voteEntry.getElements()) {
					if (ele.toString().startsWith("<source")) {
						for (Element subE : ele.getElements()) {
							if (subE.toString().startsWith("<voteLimit")) {
								assertTrue(subE
										.toString()
										.equalsIgnoreCase(
												"<voteLimit xmlns=\"http://www.w3.org/2005/Atom\">5</voteLimit>"));
							}
						}
					}
				}
			}
		}
		LOGGER.debug("ENDING TEST: RTC 87763 Feed should contains vote limit number info");
	}

	/*
	 * Method to return url values from .xml documents. Needed because of
	 * problems getting feeds\service docs\feeds from smart cloud deployments.
	 */
	/*
	 * private String getUrl (String document, String urlFragment) { String
	 * value = ""; String[] url1 = document.split("href="); for (int ndx=0;
	 * ndx<url1.length; ndx++){ if (url1[ndx].contains(urlFragment)){ value =
	 * url1[ndx].substring(0, url1[ndx].indexOf(">")).replace("&amp;", "&"); } }
	 * return value; }
	 */

	// @Test
	public void ideationDuplicate() throws Exception {
		LOGGER.debug("BEGINNING TEST: RTC 89036 API to mark an idea as duplicate");
		/*
		 * Steps: 1. Create Community 2. Add ideation blog widget 3. Create two
		 * ideation entries, one will be marked as a duplicate of the other. 4.
		 * Create an entry with the content that will mark an idea as a
		 * duplicate 5. Execute the entry using http PUT. 6. Validate
		 */

		// if (! StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
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
		// String blogHandleUrl = null;
		ExtensibleElement ee = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			// Document<Feed> feed_doc =
			// client.get(ideationBlogUrl).getDocument();
			// ee = feed_doc.getRoot();
			ee = service.getAnyFeedWithRedirect(ideationBlogUrl);
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
									&& ele2.getAttributeValue("href").contains(
											"api/entries")) {
								postIdeaBlogUrl = ele2
										.getAttributeValue("href");
							}
							/*
							 * if (atrb.toString().equalsIgnoreCase("href") &&
							 * ele2.getAttributeValue("href").contains(
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
			fd = (Feed) service.getAnyFeedWithRedirect(postIdeaBlogUrl);
		} else {
			fd = (Feed) service.getAnyFeed(postIdeaBlogUrl);
		}
		String duplicateEditLink = "";
		String ideaEditLink = "";

		// Get the 'edit' urls from feed
		for (Entry ibEntry : fd.getEntries()) {
			if (ibEntry.getTitle().equalsIgnoreCase("RTC 89036 The Duplicate")) {
				for (Element ele : ibEntry.getElements()) {
					for (QName atrb : ele.getAttributes()) {
						if (atrb.toString().equalsIgnoreCase("rel")
								&& ele.getAttributeValue("rel")
										.equalsIgnoreCase("edit")) {
							duplicateEditLink = ele.getAttributeValue("href");
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

		// Create the entry containing element and attributes to mark an idea as
		// a duplicate.
		Entry entryForDuplicate = factory.newEntry();
		entryForDuplicate.setId("urn:lsid:ibm.com:blogs:entry-" + dupEntryId);
		entryForDuplicate.setTitle("RTC 89036 The Duplicate");
		entryForDuplicate.setContent("duplicate content");
		entryForDuplicate.addLink(formattedIdeaLink,
				"http://www.ibm.com/xmlns/prod/sn/related/duplicateto",
				"application/atom+xml", "", "", 0);

		// TB SmartCloud stops here. 403 forbidden returned on the next line.
		// Comment out for now.
		// if (! StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
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
			if (ibEntry.getTitle().equalsIgnoreCase("RTC 89036 The Duplicate")) {
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
		// } //End SmartCloud 'if'
		// }// TB 9/16/13
	}

	// @Test
	public void ideationMissingCategoryTerm() throws Exception {
		LOGGER.debug("BEGINNING TEST: RTC 76290 ideadtion blog feed missing category term");
		/*
		 * Steps: 1. Create Community 2. Add ideation blog widget 3. Create
		 * ideation blog 4. Add a topic to the blog 5. Use the
		 * roller-ui/rendering/feed to generate a feed containing the topic 6.
		 * Inside this feed, get the "self" href (but not from <source>). 7.
		 * Excecute the "self" url 8. Validate that the resulting feed contains
		 * <category term="ideationblog"
		 */

		/*
		 * This addition allows multiple executions of this test. Otherwise, a
		 * delete method would be needed to erase the community at the start of
		 * each test.
		 */
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

		// create community
		Community newCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community("RTC 76290 Missing category term "
					+ uniqueNameAddition,
					"ideation blog feed missing category term",
					Permissions.PRIVATE, null);
		} else {
			newCommunity = new Community("RTC 76290 Missing category term "
					+ uniqueNameAddition,
					"ideation blog feed missing category term",
					Permissions.PUBLIC, null);
		}
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
		// assertEquals(200, service.addWidget(comm, "IdeationBlog"));

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
		ExtensibleElement ee = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ee = service.getAnyFeedWithRedirect(ideationBlogUrl);
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
									&& ele2.getAttributeValue("href").contains(
											"api/entries")) {
								postIdeaBlogUrl = ele2
										.getAttributeValue("href");
							}
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

		// Create the ideation blog only - no votes/recommends
		service.postIdeationBlog(postIdeaBlogUrl, entry);

		String entrySubString = postIdeaBlogUrl.replaceFirst("blogs",
				"blogs/roller-ui/rendering/feed");
		String entryUrl = entrySubString.substring(0,
				entrySubString.indexOf("api/entries"));
		entryUrl += "entries/atom";

		Feed ntryFeed = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ntryFeed = (Feed) service.getAnyFeedWithRedirect(entryUrl);
		} else {
			ntryFeed = (Feed) service.getAnyFeed(entryUrl);
		}

		String selfUrl = "";
		for (Entry ntry : ntryFeed.getEntries()) {
			for (Element elmnt : ntry.getElements()) {
				if (elmnt.toString().startsWith("<link")) {
					for (QName atrb : elmnt.getAttributes()) {
						if (atrb.toString().equalsIgnoreCase("rel")
								&& elmnt.getAttributeValue("rel")
										.equalsIgnoreCase("self")) {
							selfUrl = elmnt.getAttributeValue("href");
						}
					}
				}
			}
		}

		ExtensibleElement selfFeed = service.getAnyFeed(selfUrl);

		// Validate that <category term="ideationblog" exists.
		for (Element ele : selfFeed.getElements()) {
			if (ele.toString().startsWith("<source")) {
				for (Element e : ele.getElements()) {
					if (e.toString().startsWith("<category")) {
						for (QName atrb : e.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("term")
									&& e.getAttributeValue("term")
											.equalsIgnoreCase("ideationblog")) {
								assertTrue(e.getAttributeValue("term")
										.equalsIgnoreCase("ideationblog"));
							}
						}
					}
				}
			}
		}

		LOGGER.debug("ENDING TEST: RTC 76290 ideation blog feed missing category term");
	}
 
	@Test
	public void calendarTimezone() throws Exception {
		// This test works on SmartCloud
		LOGGER.debug("BEGINNING TEST: RTC 89066 DST Timezone test");
		/*
		 * Steps: 1. Create Community 2. Add Event(Calendar) widget 3. Create
		 * calendar event and execute. 4. Validate
		 */

		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

		// create community
		Community newCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community("RTC 89066 Calendar Timezone test "
					+ uniqueNameAddition, "Test DST adjustment",
					Permissions.PRIVATE, null);
		} else {
			newCommunity = new Community("RTC 89066 Calendar Timezone test "
					+ uniqueNameAddition, "Test DST adjustment",
					Permissions.PUBLIC, null);
		}
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		/*
		 * //get communities feed Feed communitiesFeed = (Feed)
		 * service.getMyCommunities(true, null, 0, 500, null, null, null, null,
		 * null); assertTrue(communitiesFeed != null);
		 * 
		 * Community comm = null; for(Entry communityEntry :
		 * communitiesFeed.getEntries()) {
		 * if(communityEntry.getTitle().equals("RTC 89066 Calendar Timezone test "
		 * +uniqueNameAddition)){ comm = new Community(communityEntry); } }
		 * 
		 * if (comm == null && communitiesFeed.getEntries().size()> 499){ comm =
		 * new Community((Entry)service.getCommunity(communityResult.
		 * getEditLinkResolvedHref().toString())); }
		 */

		Community comm = new Community(
				(Entry) assignedService.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		// Add calendar widget
		Widget widget = new Widget(StringConstants.WidgetID.Calendar.toString());
		service.postWidget(comm, widget.toEntry());
		assertEquals(201, service.getRespStatus());
		// assertEquals(200, service.addWidget(comm, "Calendar"));

		Feed remoteAppsFeed = (Feed) assignedService.getCommunityRemoteAPPs(
				comm.getRemoteAppsListHref(), true, null, 0, 50, null, null,
				SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);

		// String service_Url = null;
		String event_url = null;
		for (Entry raEntry : remoteAppsFeed.getEntries()) {
			for (Category category : raEntry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("calendar")) {
					for (Link link : raEntry.getLinks()) {
						/*
						 * if( link.getRel().contains(StringConstants.
						 * REL_REMOTEAPPLICATION_PUBLISH) ){ service_Url =
						 * link.getHref().toString(); }
						 */
						if (link.getRel().contains(
								StringConstants.REL_REMOTEAPPLICATION_FEED)) {
							event_url = link.getHref().toString();
						}
					}
				}
			}
		}

		String eventUrl = event_url.replace("type=event&calendarUuid",
				"calendarUuid");

		// post calendar events using timezone
		Event event = new Event("RTC 89066 Timezone Meeting",
				StringConstants.STRING_NO_LOWERCASE, "daily", "",
				"2013-11-11T00:00:00-05:00", "2013-10-31T00:00:00-05:00",
				"2013-10-31T01:00:00-05:00", 0, "America/New_York", "",
				"SU,MO,TU,WE,TH,FR,SA");
		ExtensibleElement eventFeed = service
				.postCalendarEvent(eventUrl, event);
		assertTrue(eventFeed != null);

		// post calendar events using daylight savings
		Event event2 = new Event("RTC 89066 Timezone Meeting 2",
				StringConstants.STRING_NO_LOWERCASE, "daily", "",
				"2013-11-11T00:00:00-05:00", "2013-10-31T12:00:00-05:00",
				"2013-10-31T13:00:00-05:00", 0, "",
				"2013-03-10T11:49:19-07:00/2013-11-03T10:49:19-08:00",
				"SU,MO,TU,WE,TH,FR,SA");
		ExtensibleElement eventFeed2 = service.postCalendarEvent(eventUrl,
				event2);
		assertTrue(eventFeed2 != null);
		assertEquals("Post calendar events", 201, service.getRespStatus());

		// Validation: First get the value of the start and end dates for both
		// meetings
		String startDateMeeting1 = "";
		String endDateMeeting1 = "";
		String startDateMeeting2 = "";
		String endDateMeeting2 = "";
		Feed feedB = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			//Document<Feed> feed_doc = assignedService.getFeedLink(event_url).getDocument();
			Document<Feed> feed_doc = assignedService.getCalendarFeedWithRedirect(event_url)
					.getDocument();
			feedB = feed_doc.getRoot();
		} else {
			feedB = (Feed) assignedService.getAnyFeed(event_url);
		}
		for (Entry ibEntry : feedB.getEntries()) {
			if (ibEntry.getTitle().equalsIgnoreCase(
					"RTC 89066 Timezone Meeting")) {
				for (Element ele : ibEntry.getElements()) {
					if (ele.toString().startsWith("<snx:recurrence")) {
						for (Element ele2 : ele.getElements()) {
							if (ele2.toString().startsWith("<snx:startDate")) {
								startDateMeeting1 = ele2.toString();
							}
							if (ele2.toString().startsWith("<snx:endDate")) {
								endDateMeeting1 = ele2.toString();
							}
						}
					}
				}
			}
			if (ibEntry.getTitle().equalsIgnoreCase(
					"RTC 89066 Timezone Meeting 2")) {
				for (Element ele : ibEntry.getElements()) {
					if (ele.toString().startsWith("<snx:recurrence")) {
						for (Element ele2 : ele.getElements()) {
							if (ele2.toString().startsWith("<snx:startDate")) {
								startDateMeeting2 = ele2.toString();
							}
							if (ele2.toString().startsWith("<snx:endDate")) {
								endDateMeeting2 = ele2.toString();
							}
						}
					}
				}
			}
		}

		// Validate for the correct values;
		// I suspect the timestamps below are the meeting times in relation to
		// GMT. So a meeting setup at 12:00am (midnight) is actually 5:00am GMT.
		// Now, if you look at the calendar entry in the UI, the meeting is
		// actually one hour later (1:00AM), I think this is because the meeting
		// is actually set up during Daylight Savings Time, which is one hour
		// later.
		assertTrue(startDateMeeting1.contains("2013-10-31T05:00:00.000Z"));
		assertTrue(endDateMeeting1.contains("2013-10-31T06:00:00.000Z"));

		assertTrue(startDateMeeting2.contains("2013-10-31T17:00:00.000Z"));
		assertTrue(endDateMeeting2.contains("2013-10-31T18:00:00.000Z"));

		LOGGER.debug("END TEST: RTC 89066 DST Timezone test");
	}

	@Test
	public void isExternalElement() throws IOException, URISyntaxException {
		/*
		 * This test is for Smart Cloud deployments only.
		 * 
		 * Test process: 1. Create an entry for a new community from a custom
		 * entry. 2. Create the community. 3. Add member not in LDAP to
		 * community (this is the external user). 4. Log in as external user,
		 * add bookmark. 5. Remove external user. 6. Set external user to false.
		 * 7. Try to add external user again. Should fail.
		 */

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("Beginning test RTC 90675 and 95435: Test isExternal element and adding external user. Skipped if not SmartCloud");
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

			LOGGER.debug("Step 1: Create Community, isExternal set to true");
			// create community
			Factory factory = abdera.getFactory();
			Entry entryForExtUser = factory.newEntry();
			entryForExtUser.setTitle("RTC 90675 New isExternal element "
					+ uniqueNameAddition);
			entryForExtUser.setContent("RTC 90675 content");
			entryForExtUser.addSimpleExtension(
					StringConstants.SNX_COMMUNITY_TYPE, "private");
			entryForExtUser.addCategory(StringConstants.SCHEME_TYPE,
					"community", "");

			Community newCommunity = new Community(entryForExtUser);
			newCommunity.setIsExternal(true);
			Entry communityResult = (Entry) service
					.createCommunity(newCommunity);
			assertTrue(communityResult != null);
			Link lnk = communityResult.getEditLink();
			String membersLink = lnk.getHref().toString()
					.replace("instance", "members");

			// Contributor element must be in this format, in particular the
			// userid. Visible email address is disabled on
			// apps.collabservdaily, so email can not be used
			// to find user.
			// <contributor>
			// <snx:userid>20006416</snx:userid>
			// <name>Jill White01</name>
			// </contributor>

			// Add member now.
			LOGGER.debug("Step 2: Add a member not in the LDAP (external user)");
			int OUT_OF_ORG_USER = 15; // jill white01
			UserPerspective outOfOrgUser=null;
			try {
				outOfOrgUser = new UserPerspective(OUT_OF_ORG_USER,
						Component.COMMUNITIES.toString(), useSSL);
			} catch (LCServiceException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}

			Entry memberEntry = factory.newEntry();
			Person p = memberEntry.addContributor(outOfOrgUser.getRealName());
			p.addSimpleExtension(StringConstants.SNX_USERID,
					outOfOrgUser.getUserId());
			memberEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/type",
					"person", "");
			Element elmt = memberEntry.addSimpleExtension(
					StringConstants.SNX_ROLE, "owner");
			elmt.setAttributeValue("component",
					"http://www.ibm.com/xmlns/prod/sn/communities");
			memberEntry.addExtension(elmt);

			service.postBlog(membersLink, memberEntry);

			// Log on as external user and create a bookmark
			UserPerspective extUser=null;
			try {
				extUser = new UserPerspective(
						StringConstants.EXTERNAL_USER,
						Component.COMMUNITIES.toString(), useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			CommunitiesService extUserService = extUser.getCommunitiesService();

			// get communities feed -
			Feed communitiesFeed = (Feed) extUserService.getMyCommunities(true,
					null, 0, 500, null, null, null, null, null);
			assertTrue(communitiesFeed != null);

			// create community object
			Community comm = null;
			for (Entry communityEntry : communitiesFeed.getEntries()) {
				if (communityEntry.getTitle().equals(
						"RTC 90675 New isExternal element "
								+ uniqueNameAddition)) {
					comm = new Community(communityEntry);
				}
			}

			// Create Bookmark as external user.
			LOGGER.debug("Step 3: Create a bookmark as the external user community member.");
			Bookmark bookmark = new Bookmark(StringGenerator.randomSentence(3),
					StringGenerator.randomSentence(4),
					"http://www.google.com/", "tagExternalUser");
			Entry bookmarkResponse = (Entry) extUserService
					.createCommunityBookmark(comm, bookmark);
			assertTrue(bookmarkResponse != null);
			LOGGER.debug("Bookmark: " + bookmark.getTitle()
					+ " successfully created @ "
					+ bookmarkResponse.getEditLinkResolvedHref().toString());

			// Remove external user from the community.
			LOGGER.debug("Step 4: Remove the external user from the Community.");
			Feed memberFeed = (Feed) assignedService.getAnyFeed(membersLink);
			for (Entry communityEntry : memberFeed.getEntries()) {
				// if(communityEntry.getTitle().equalsIgnoreCase("Jill White01")){
				if (communityEntry.getTitle().equalsIgnoreCase(
						outOfOrgUser.getRealName())) {
					LOGGER.debug("Step 3: Remove the member");
					assignedService.removeMemberFromCommunity(communityEntry
							.getEditLinkResolvedHref().toString());
				}
			}

			// Update isExternal to false.
			LOGGER.debug("Step 5: Set isExternal to false.");
			String editUrl = "";
			for (Entry communityEntry : communitiesFeed.getEntries()) {
				if (communityEntry.getTitle().equals(
						"RTC 90675 New isExternal element "
								+ uniqueNameAddition)) {
					for (Element e : communityEntry.getElements()) {
						if (e.toString().startsWith("<snx:isExternal")) {
							assertTrue(e.toString().startsWith(
									"<snx:isExternal"));
							if (e.getText().equalsIgnoreCase("true")) {
								e.setText("false");
								editUrl = communityEntry
										.getEditLinkResolvedHref().toString();

								service.putEntry(editUrl, communityEntry);
								break;
							}
						}
					}
				}
			}

			// Now try to add the external user again. This should fail.
			// Error message: The person could not be added to this community
			// because they are outside of the organization.
			LOGGER.debug("Step 6: Try to add the external user again.  This should fail and return HTTP 400");
			ExtensibleElement response = service.postBlog(membersLink,
					memberEntry);
			assertEquals(true, response.toString().contains("<resp:code>400"));

			LOGGER.debug("Ending test RTC 90675 and 95435: Test isExternal element and adding external user. Skipped if not SmartCloud");
		}
	}

	@Test
	public void communityStartPage() {
		/*
		 * RTC 93677 Defining new Communities landing page.
		 * 
		 * 1. Create a community. 2. Update the value of communityStartPage
		 * using communities edit url. Use widgetInstanceId to specify to a new
		 * landing page other than the Overview page. 3. Error test (RTC
		 * 108452). Use a bad widget Id and try to execute PUT. Validate error
		 * code return.
		 * 
		 * Aside from error testing when a bad widget id is used, this test only
		 * tests updating the <snx:communityStartPage> element but it can not
		 * validate that the new landing page in the UI. So some manual testing
		 * is needed to validate that the new landing page works. For this test,
		 * the new landing page is Forums. See RTC 93677/95590.
		 */
		LOGGER.debug("BEGINNING TEST RTC 93677 Community Atom feeds to include the new Community start page field.");
		LOGGER.debug("Also test for error code for bad widget id: RTC 108452");

		/*
		 * This addition allows multiple executions of this test. Otherwise, a
		 * delete method would be needed to erase the community at the start of
		 * each test.
		 */
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

		// create community
		Community newCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community(
					"RTC 93677 Testing communityStartPage "
							+ uniqueNameAddition,
					"Specify a new landing page using communityStartPage",
					Permissions.PRIVATE, null);
		} else {
			newCommunity = new Community(
					"RTC 93677 Testing communityStartPage "
							+ uniqueNameAddition,
					"Specify a new landing page using communityStartPage",
					Permissions.PUBLIC, null);
		}
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		// Use the code to get that community directly
		// Don't get the community from retrieved communities, it will cause
		// null point error, if that community is out side of retrieved ones (
		// here is 500 comms ).

		// Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
		// 500, null, null, null, null, null);
		// assertTrue(communitiesFeed != null);

		Entry communityEntry = (Entry) assignedService.getCommunity(communityResult
				.getEditLinkResolvedHref().toString());
		Community comm = new Community(
				(Entry) assignedService.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		String editLink = "";
		// Entry communityEntry = null;
		// for(Entry ntry : communitiesFeed.getEntries()) {
		// if(ntry.getTitle().equals("RTC 93677 Testing communityStartPage "+uniqueNameAddition)){
		// comm = new Community(ntry);
		// communityEntry = ntry;
		// Validate that <snx:communityStartPage> is in the entry
		for (Element ele : communityEntry.getElements()) {
			if (ele.toString().startsWith("<snx:communityStartPage")) {
				assertEquals(true,
						ele.toString().startsWith("<snx:communityStartPage"));
			}
			for (QName atrb : ele.getAttributes()) {
				if (atrb.toString().equalsIgnoreCase("rel")
						&& ele.getAttributeValue("rel")
								.equalsIgnoreCase("edit")) {
					editLink = ele.getAttributeValue("href");
				}
			}
		}
		// }
		// }

		// Get the URL for widgets feed. Probably should move this into the loop
		// above.
		String widgetUrl = comm.getRemoteAppsListHref().replaceFirst(
				"remoteApplications", "widgets");

		Feed widgetAppFeed = (Feed) assignedService.getAnyFeed(widgetUrl);

		// Get the widgetIntanceId from the feed. Might be a better way to get
		// this.
		String selfLink = "";
		for (Entry remoteAppEntry : widgetAppFeed.getEntries()) {
			if (remoteAppEntry.getTitle().equalsIgnoreCase("Forums")) {
				for (Element ele : remoteAppEntry.getElements()) {
					if (ele.toString().startsWith("<link")) {
						for (QName atrb : ele.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("rel")
									&& ele.getAttributeValue("rel")
											.equalsIgnoreCase("self")) {
								selfLink = ele.getAttributeValue("href");
							}
						}
					}
				}
			}
		}

		String widgetId = selfLink.substring(selfLink
				.indexOf("widgetInstanceId="));
		int index = widgetId.indexOf("=") + 1;

		for (Element ele : communityEntry.getElements()) {
			if (ele.toString().startsWith("<snx:communityStartPage")) {
				ele.setText(widgetId.substring(index));
			}
		}

		// Update the new startpage.
		service.putEntry(editLink, communityEntry);

		// get communities feed
		Feed communitiesFeed2 = (Feed) service.getMyCommunities(true, null, 0,
				500, null, null, null, null, null);
		assertTrue(communitiesFeed2 != null);

		for (Entry ntry : communitiesFeed2.getEntries()) {
			if (ntry.getTitle().equals(
					"RTC 93677 Testing communityStartPage "
							+ uniqueNameAddition)) {
				// Validate that <snx:communityStartPage> is in the entry
				assertEquals(
						true,
						ntry.getExtension(StringConstants.SNX_STARTPAGE)
								.toString()
								.startsWith("<snx:communityStartPage"));
				assertEquals(
						true,
						ntry.getExtension(StringConstants.SNX_STARTPAGE)
								.getText().toString()
								.equalsIgnoreCase(widgetId.substring(index)));
			}
		}

		// Validate error code. RTC 108452
		for (Element ele : communityEntry.getElements()) {
			if (ele.toString().startsWith("<snx:communityStartPage")) {
				ele.setText("W000000000000_0BAD_ID00_000000000000");
			}
		}

		// Update the new startpage with bad widget id. Should return HTTP 400 -
		// bad request.
		LOGGER.debug("Intentionally try to use a bad widget id for the start page.  This should fail: HTTP 400");
		Entry retVal = (Entry) service.putEntry(editLink, communityEntry);
		assertEquals(true,
				retVal.getSimpleExtension(StringConstants.API_RESPONSE_CODE)
						.equals("400"));
		assertEquals(true,
				retVal.getSimpleExtension(StringConstants.API_RESPONSE_MSG)
						.equalsIgnoreCase("Bad Request"));

		LOGGER.debug("ENDING TEST RTC 93677 Community Atom feeds to include the new Community start page field.");
	}

	@Test
	public void createLongTag() {
		/*
		 * This is a test for Community and Community bookmark tags greater than
		 * 64 chars in length Community tag error messages return HTTP 400.
		 */
		LOGGER.debug("BEGINNING TEST: Communities RTC 83878 and 99471 - adding tag longer than maximum via API returns incorrect error message.");
		// This tag has 70 chars
		String tag = "1111111111222222222233333333334444444444555555555566666666667777777777";

		String randString = RandomStringUtils.randomAlphanumeric(4);
		String communityName = "RTC 83878 Bookmark with extra long tag value."
				+ randString;
		String communityName2 = "RTC 99471 Community with extra long tag value."
				+ randString;
		String description = "This test should fail.";

		LOGGER.debug("Testing RTC 83878 first");
		LOGGER.debug("Step 1: Create a community");
		Community newCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community(communityName + randString,
					description, Permissions.PRIVATE, null);
		} else {
			// On-Premise deployment.
			newCommunity = new Community(communityName + randString,
					description, Permissions.PUBLIC, null);
		}

		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);
		LOGGER.debug("Community: " + newCommunity.getTitle()
				+ " was created successfully @ "
				+ communityResult.getEditLinkResolvedHref().toString());
		LOGGER.debug("Finished creating Community...");

		/*
		 * //get communities feed - LOGGER.debug("Step 2: Get Community feed");
		 * Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
		 * 500, null, null, null, null, null); assertTrue(communitiesFeed !=
		 * null);
		 * 
		 * //create community object Community comm = null; for(Entry
		 * communityEntry : communitiesFeed.getEntries()) {
		 * if(communityEntry.getTitle().equals(communityName + randString)){
		 * comm = new Community(communityEntry); } }
		 */

		Community comm = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		// Create bookmark
		LOGGER.debug("Step 3: Create a community bookmark");
		Bookmark bookmark = new Bookmark("RTC83878_Bookmark",
				StringGenerator.randomSentence(4), "http://www.google.com/?q="
						+ randString, tag);

		// Create bookmark
		Entry bookmarkResponse = (Entry) service.createCommunityBookmark(comm,
				bookmark);

		// validate
		LOGGER.debug("Step 4: Validate error message.  Should be HTTP 400.");
		if (bookmarkResponse.toString().contains("<resp:code>400</resp:code>")) {
			LOGGER.debug("Correct response returned: 400.  This test should fail.");
		} else {
			LOGGER.debug("Incorrect response returned. The correct response is HTTP 400");
		}

		// Validate Community Tags //
		LOGGER.debug("Testing RTC 99471 ......");
		LOGGER.debug("Step 5: Create a community with including a tag with too many chars.");
		Community newCommunity2 = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity2 = new Community(communityName2 + randString,
					description, Permissions.PRIVATE, tag);
		} else {
			// On-Premise deployment.
			newCommunity2 = new Community(communityName2 + randString,
					description, Permissions.PUBLIC, tag);
		}

		Entry communityResult2 = (Entry) service.createCommunity(newCommunity2);
		assertTrue(communityResult != null);

		// validate
		LOGGER.debug("Step 6: Validate error message for defect 99471.  Should be HTTP 400.");
		if (communityResult2.toString().contains("<resp:code>400</resp:code>")) {
			LOGGER.debug("This test should fail.  Correct response returned: 400.");
		} else {
			LOGGER.debug("Incorrect response returned. The correct response is HTTP 400");
		}

		LOGGER.debug("ENDING TEST: Communities RTC 83878 and 99471 - adding bookmark tag longer than maximum via API returns incorrect error message.");
	}

	@Test
	public void updateErrors() {
		LOGGER.debug("BEGIN TEST: RTC 102005 - Atom API Update Community Failing.");

		/*
		 * Two problems to validate: 1. Not setting the communityUuid on the
		 * parsed community would result in 404 communityNotFound for all legit
		 * requests Essentially this means if the <id> element is missing or
		 * empty, an update should work as Communities uses the communityUuid
		 * from the URL for the operation, not the id element.
		 * 
		 * 2. Returned atom entry would lack the updated values.
		 * 
		 * Process: 1. Create the community 2. Edit the entry of the community,
		 * update title, description and id 3. Execute the update 4. Validate
		 * the return from the update. Are the values correct?
		 */
		String randString = RandomStringUtils.randomAlphanumeric(4);
		String communityTitle = "RTC 102005: Update Test - before the update. "
				+ randString;
		String communityDescr = "Before the update";
		String communityUpdatedTitle = "RTC 102005: Update Test - AFTER the update. "
				+ randString;
		;
		String communityUpdatedDescr = "The description AFTER the update";

		LOGGER.debug("Step 1: Create a community");

		Community testCommunity = new Community(communityTitle, communityDescr,
				Permissions.PRIVATE, null);
		Entry communityEntry = (Entry) service.createCommunity(testCommunity);

		String editLink = communityEntry.getEditLinkResolvedHref().toString();

		LOGGER.debug("Step 2: Edit the community.  Get and Entry doc, update the Title, Description and Id elements");
		Entry entryRetrieved = (Entry) assignedService.getCommunity(editLink);
		entryRetrieved.setTitle(communityUpdatedTitle);
		entryRetrieved.setContent(communityUpdatedDescr);
		entryRetrieved.setId(""); // This is needed to validate part of the
		// defect. PUT should work without <id>
		// having a value.

		LOGGER.debug("Step 3: Perform an update operation using PUT");
		Entry result = (Entry) service.putEntry(editLink, entryRetrieved);

		LOGGER.debug("Step 4: Validate.  Get a feed of the community.  Are the values there?");
		assertEquals(true,
				result.getTitle().equalsIgnoreCase(communityUpdatedTitle));
		assertEquals(true,
				result.getContent().equalsIgnoreCase(communityUpdatedDescr));

		LOGGER.debug("ENDING TEST: RTC 102005 - Atom API Update Community Failing.");
	}

	@Test
	public void contentElementTest() throws IOException, URISyntaxException {
		/*
		 * Test process: 1. Create an entry for a new community. 2. Create the
		 * community. The content element should be abscent from the entry. 3.
		 * Update the community. Delete the content element. 4. Validate that
		 * the community exists.
		 */

		LOGGER.debug("Beginning test RTC 105503: Community api should not require a content element for community entries");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String communityTitle = "RTC 105503 Content element test "
				+ uniqueNameAddition;
		String communityUpdatedTitle = "RTC 105503 Content element test. UPDATE";

		LOGGER.debug("Step 1: Create Community.  Content element will not be included in the entry.");
		Factory factory = abdera.getFactory();
		Entry entry = factory.newEntry();
		entry.setTitle(communityTitle);
		entry.addSimpleExtension(StringConstants.SNX_COMMUNITY_TYPE, "private");
		entry.addCategory(StringConstants.SCHEME_TYPE, "community", "");

		// Content element is missing, but creating the community is
		// successfull.
		Community newCommunity = new Community(entry);
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		// get communities feed -
		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0,
				500, null, null, null, null, null);
		assertTrue(communitiesFeed != null);

		// validate that the community actually exists.
		for (Entry ntry : communitiesFeed.getEntries()) {
			if (ntry.getTitle().equalsIgnoreCase(communityTitle)) {
				assertEquals(true,
						ntry.getTitle().equalsIgnoreCase(communityTitle));
			}
		}

		// Update the community.
		String editLink = communityResult.getEditLinkResolvedHref().toString();

		LOGGER.debug("Step 2: Edit the community.  Get and Entry doc, update the Title, delete the content element");
		Entry entryRetrieved = (Entry) service.getCommunity(editLink);
		entryRetrieved.setTitle(communityUpdatedTitle);
		// entryRetrieved.setContentElement(null).discard();
		entryRetrieved.setContentElement(null);

		LOGGER.debug("Step 3: Execute the update.");
		Entry result = (Entry) service.putEntry(editLink, entryRetrieved);
		assertTrue(result != null);

		LOGGER.debug("Step 4: Validate.  Get a feed of the updated community. Make sure the community is there.");
		// get communities feed -
		Feed communitiesUpdatedFeed = (Feed) service.getMyCommunities(true,
				null, 0, 500, null, null, null, null, null);
		assertTrue(communitiesFeed != null);

		// validate that the community actually exists.
		for (Entry ntry : communitiesUpdatedFeed.getEntries()) {
			if (ntry.getTitle().equalsIgnoreCase(communityUpdatedTitle)) {
				assertEquals(true,
						ntry.getTitle().equalsIgnoreCase(communityUpdatedTitle));
			}
		}

		LOGGER.debug("ENDING TEST RTC 105503: Community api should not require a content element for community entries");
	}

	@Test
	public void getLibraryWidget() throws Exception {
		/*
		 * Tests the ability to get the Library widget Step 1: Create a
		 * community Step 2: Add the library widget Step 3: Get the library
		 * widget, verify success
		 */
		LOGGER.debug("BEGINNING TEST: Get library widget");
		String randString = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1... Create a community");
		Community testCommunity = new Community("LibraryWidget Test "
				+ randString, "Join here", Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2... Add the library widget");
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		// int response = service.addWidget(communityRetrieved, "Library"); //
		// Attempt to add the widget
		Widget widget = new Widget(StringConstants.WidgetID.Library.toString());
		service.postWidget(communityRetrieved, widget.toEntry());
		// assertEquals("Add Library widget", 201, service.getRespStatus());
		if (service.getRespStatus() != 201) { // Check to make sure library
												// widget was added
			LOGGER.debug("Library widget not avaiable, terminating the test");
			return; // if widget could not be added, assume it is not available,
			// end test
		}

		LOGGER.debug("Step 3... Get the library widget, verify success");
		Feed widgetFeed = (Feed) service.getCommunityWidget(
				communityRetrieved.getUuid(), "Library");
		String title = widgetFeed.getEntries().get(0).getTitle();
		assertEquals("Library", title); // Verify "Library" is the only widget
		// in this feed

		LOGGER.debug("ENDING TEST: Get library widget");
	}

	@Test
	public void tagsWithSpaces() {
		/*
		 * Communities will remove spaces from tag strings. Step 1: Create a
		 * community with tag containing white space Step 2: Add a bookmark with
		 * a tag containing white space Step 3: Get a feed of the tags Step 4:
		 * Validate that the spaces have been removed.
		 */
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String communityTitle = "RTC 107146 Tag space test "
				+ uniqueNameAddition;
		String bookmarkTitle = "Bookmark Title RTC 107146" + uniqueNameAddition;
		String bookmarkTagString = "Tag Without Spaces";
		String bookmarkTagStringSansSpaces = "tagwithoutspaces";
		String communityTagString = "community tag no spaces";
		String communityTagStringSansSpaces = "communitytagnospaces";

		LOGGER.debug("BEGINNING TEST: RTC 107146 Community removes white space in tags.");

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community(communityTitle,
				"A community with a bookmark tag with spaces ",
				Permissions.PRIVATE, null);
		testCommunity.setTagsNoParsing(communityTagString);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Create a bookmark for that community");
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		Bookmark testBookmark = new Bookmark(bookmarkTitle, "RTC 107146",
				"http://www.test.com" + uniqueNameAddition, bookmarkTagString);
		testBookmark.setTagsNoParsing(bookmarkTagString);
		Entry bookmarkResult = (Entry) service.createCommunityBookmark(
				communityRetrieved, testBookmark);
		assertTrue(bookmarkResult != null);

		LOGGER.debug("Step 3: Get a feed of the tags for both community and bookmarks");
		boolean communityTagIsCorrect = false;
		boolean bookmarkTagIsCorrect = false;
		Entry ntry = (Entry) service.getAnyFeed(communityResult
				.getEditLinkResolvedHref().toString());
		for (Category category : ntry.getCategories()) {
			if (category.getTerm().equalsIgnoreCase(
					communityTagStringSansSpaces)) {
				communityTagIsCorrect = true;
			}
		}

		Feed bookmarksFeed = (Feed) service
				.getCommunityBookmarks(communityRetrieved.getBookmarkHref());
		for (Entry bkmrkEntry : bookmarksFeed.getEntries()) {
			for (Category category : bkmrkEntry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase(
						bookmarkTagStringSansSpaces)) {
					bookmarkTagIsCorrect = true;
				}
			}
		}

		LOGGER.debug("Step 4: Validate that both tags are correct");
		assertEquals(true, communityTagIsCorrect && bookmarkTagIsCorrect);

		LOGGER.debug("ENDING TEST: RTC 107146 Community removes white space in tags.");
	}

	// @Test
	/*
	 * Steps: 1. Create a community, add activity widget, add an activity, add a
	 * member with owner privs. 2. Retrieve community by a get REST call like
	 * https
	 * ://w3-connections.ibm.com/communities/service/atom/community/instance
	 * ?communityUuid=2aa3bc7d-ae85-4dca-9b44-856f5d77e613 3. Using Atom entry
	 * from response of #2, make a small update such as changing the title, 4
	 * Update community by doing put REST call to the URL in #2, with content of
	 * #3 but use the member added in step 1. 5. Verify the response is ok. 6.
	 * Check community activity, verify "contributor" and "updated" fields have
	 * not changed since update. Do this by comparing feed before and after
	 * update.
	 */
	public void activityFeed() throws FileNotFoundException, IOException {

		LOGGER.debug("BEGINNING TEST: RTC 109991");

		// Create community
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String communityName = "RTC 109991 Community Activity 'updated by' and 'date/time' fix "
				+ uniqueNameAddition;
		String activityTitle = "RTC 109991 Community Activity, API";
		Community newCommunity = null;

		newCommunity = new Community(communityName,"Simple test for community based Activity",Permissions.PUBLIC, null);

		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		// Add a member as owner.
		LOGGER.debug("Step 2: Add a member");
		Community testCommunityRetrieved = new Community((Entry) service.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
		Member member = new Member(null, otherUser.getUserId(),
				Component.COMMUNITIES, Role.OWNER, MemberType.PERSON);
		Entry memberEntry = (Entry) service.addMemberToCommunity(
				testCommunityRetrieved, member);
		assertTrue(memberEntry != null);

		Community comm = testCommunityRetrieved;
		String selfLink = testCommunityRetrieved.getSelfLink().toString();

		Widget widget = new Widget(
				StringConstants.WidgetID.Activities.toString());
		service.postWidget(comm, widget.toEntry());
		assertEquals(201, service.getRespStatus());

		// Get the activities URL from the remote app feed
		String activitiesUrl = "";
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				comm.getRemoteAppsListHref(), true, null, 0, 1000, null, null,
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

		// Create an Activity
		Factory factory = abdera.getFactory();
		Entry activityEntry = factory.newEntry();
		activityEntry.setTitle(activityTitle);
		activityEntry.setContent("Simple activity created in communities via API");
		activityEntry.addCategory(StringConstants.SCHEME_TYPE,
				"community_activity", "Community Activity");

		service.postEntry(activitiesUrl, activityEntry);

		// Get Activities feed before the update. Pull the values for the
		// contributor and updated elements for comparison later.
		Feed activitiesPre = (Feed) service.getAnyFeed(activitiesUrl);
		String contributorNamePreChange = "";
		Date updatedPreChange = null;
		boolean foundEntry1 = false;
		boolean foundContributor = false;
		String name1 = "";
		for (Entry ntry : activitiesPre.getEntries()) {
			if (ntry.getTitle().equalsIgnoreCase(activityTitle)) {
				List<Person> person = ntry.getContributors();
				Iterator<Person> prsn = person.iterator();
				for(Element p : prsn.next()) {
					name1 = p.getText();
					if (name1.equalsIgnoreCase(user.getRealName())){
						foundContributor = true;
						contributorNamePreChange = name1;
					}
				}
					
				updatedPreChange = ntry.getUpdated();
				foundEntry1 = true;
			}
		}
		
		assertEquals ("Activity entry " + activityTitle + " not found ", true, foundEntry1);
		assertEquals ("Activity contributor " + user.getRealName() + " not found ", true, foundContributor);

		Entry commEntry = (Entry) service.getAnyFeed(selfLink);

		// Change the title
		commEntry.setTitle(communityName + " UPDATED");

		// PUT the new Title back into the Community as the added member
		otherUserService.putEntry(comm.getEditLink().toString(), commEntry);
		
		// TJB 7/30/15 Defect 157474.  Pause needed between update and retrieval.
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Get the activities feed agains.
		String contributorNamePostChange = "";
		Date updatedPostChange = null;
		boolean foundEntry2 = false;
		boolean foundContributor2 = false;
		String name2 = "";
		Feed activitiesPost = (Feed) otherUserService.getAnyFeed(activitiesUrl);
		for (Entry ntry2 : activitiesPost.getEntries()) {
			if (ntry2.getTitle().equalsIgnoreCase(activityTitle)) {
				List<Person> person = ntry2.getContributors();
				Iterator<Person> prsn = person.iterator();
				for(Element p : prsn.next()) {
					 name2 = p.getText();
					if (name2.equalsIgnoreCase(user.getRealName())){
						foundContributor2 = true;
						contributorNamePostChange = name2;
					}
				}			

				updatedPostChange = ntry2.getUpdated();
				foundEntry2 = true;
			}
		}
		
		assertEquals ("Activity entry " + activityTitle + " not found ", true, foundEntry2);		
		assertEquals ("Activity contributor " + user.getRealName() + " not found ", true, foundContributor2);

		// Validate. The Contributor and Updated element values should be the
		// same before and after the update
		assertEquals(contributorNamePreChange, contributorNamePostChange);
		assertEquals(updatedPreChange.toString(), updatedPostChange.toString());

		LOGGER.debug("ENDING TEST: RTC 109991");
	}

	@Test
	public void createCommunityInvitesWithBadData()
			throws FileNotFoundException, IOException {
		/*
		 * RTC 116946 - test error handling when creating community invites w/
		 * bad data. SmartCloud only. Step 1: Create a community Step 2: Attempt
		 * to send a community invite to a non-existent user email, verify error
		 * message & status code 400 Step 3: Attempt to send a community invite
		 * to a non-existent userID, verify error message & status code 400 Step
		 * 4: Attempt to send an incorrect community invite (entry missing
		 * information), verify error message & status code 400 Step 5: Attempt
		 * to send a community invite to an external user, verify error message
		 * & status code 400
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {

			LOGGER.debug("BEGINNING TEST: Create community invite with bad data");
			String rand = RandomStringUtils.randomAlphanumeric(5);
			// Error message strings used in the test to verify against
			String unknownUserErrMsg = "Unknown user.";
			String invalidInvitationErrMsg = "Invalid invitation entry.";
			String externalInvitationErrMsg = "The community does not allow external users.";

			LOGGER.debug("Step 1... Create a community");
			Community testCommunity = new Community("Test CommunityInvite "
					+ rand, "A community with invitations to be sent",
					Permissions.PRIVATE, null);
			Entry communityEntry = (Entry) service
					.createCommunity(testCommunity);

			LOGGER.debug("Step 2... Attempt to send a community invite to a non-existent user email, verify error message & status code 400");
			Community communityRetrieved = new Community(
					(Entry) assignedService.getCommunity(communityEntry
							.getEditLinkResolvedHref().toString()));
			Invitation invite = new Invitation("i_dont_exist@s2js.siw", null,
					"Join my community!", "Do it.");
			service.createInvitation(communityRetrieved, invite);
			// Verify
			assertEquals("Response code should be 400 (bad request)", 400,
					service.getRespStatus());
			assertEquals("Error message verification", true, service
					.getRespErrorMsg().equalsIgnoreCase(unknownUserErrMsg));

			LOGGER.debug("Step 3... Attempt to send a community invite to a non-existent userID, verify error message & status code 400");
			invite = new Invitation(null, "not-a-userid!",
					"Join my community!", "Do it.");
			service.createInvitation(communityRetrieved, invite);
			// Verify
			assertEquals("Response code should be 400 (bad request)", 400,
					service.getRespStatus());
			assertEquals("Error message verification", true, service
					.getRespErrorMsg().equalsIgnoreCase(unknownUserErrMsg));

			LOGGER.debug("Step 4... Attempt to send an incorrect community invite (entry missing information), verify error message & status code 400");
			// POST to correct url, but with empty entry
			service.postEntry(communityRetrieved.getInvitationsListLink(),
					abdera.getFactory().newEntry());
			// Verify
			assertEquals("Response code should be 400 (bad request)", 400,
					service.getRespStatus());
			assertEquals("Error message verification", true, service
					.getRespErrorMsg()
					.equalsIgnoreCase(invalidInvitationErrMsg));

			LOGGER.debug("Step 5... Attempt to send a internal community invite to an external user, verify error message & status code 400");
			// Change testCommunity to be internal
			communityEntry = (Entry) assignedService.getCommunity(communityRetrieved
					.getEditLink());
			communityEntry.getExtension(StringConstants.SNX_ISEXTERNAL)
					.setText("false");
			service.putEntry(communityRetrieved.getEditLink(), communityEntry);
			// Send invitation to external user
			invite = new Invitation(StringConstants.EXTERNAL_USER_EMAIL, null,
					"Join my community!", "Do it.");
			service.createInvitation(communityRetrieved, invite);
			// Verify
			assertEquals("Response code should be 400 (bad request)", 400,
					service.getRespStatus());
			assertEquals(
					"Error message verification",
					true,
					service.getRespErrorMsg().equalsIgnoreCase(
							externalInvitationErrMsg));

			LOGGER.debug("ENDING TEST: Create community invite with bad data");
		}
	}

	/**
	 * RTC 119959 Visitors should not be able to: 1. Become owners. 2. See
	 * Public Communities. 3. See Public Communities tag cloud. 4. Search public
	 * communities - not tested here. See SearchBasicTests -
	 * vmodelVisitorPublicSearch() 5. View an individual public or moderated
	 * community 6. Create communities. Tested in createCommunityByVisitor() 7.
	 * Export members - not tested. No api for this. 8. Owners of internal
	 * communities should not be able to add visitors. 9. Only employees using
	 * employee.extended can extend communities and files to visitors. An
	 * employee not using employee.extended role should not be able to create a
	 * community that allows visitors. J Hewitt
	 * "An employee not in that role should not be able to create a community that is externals allowed."
	 * 
	 * Process Step 1. Create Communities for the test. Step 2. Test 1: Add
	 * external member as owner - should fail. Step 3. Add external member as
	 * member - should pass. Step 4. Visitor tries to create a book mark -
	 * should pass. Step 5. Test 2: External member accesses public communities
	 * - should fail. Step 6. Test 3: External member accesses tag cloud -
	 * should fail. Step 7. Test 5: External member accesses individual public
	 * community where they are not a member - should fail. Step 8. Test 8:
	 * Adding external user to restricted community - should fail. Step 9. Test
	 * 9: Regular user creates community allowing external users - should fail.
	 * Validates RTC 129756
	 * 
	 * @throws IOException
	 * @throws FileNotFoundException
	 * 
	 * 
	 */
	//@Test
	public void vmodelNegativeTests() throws FileNotFoundException, IOException {
		if (StringConstants.VMODEL_ENABLED) {
			LOGGER.debug("BEGIN TEST: RTC 119959: Negative tests for vmodel visitors");
			LOGGER.debug("Step 1: Create a community");
			String randString = RandomStringUtils.randomAlphanumeric(4);
			Community testCommunity = null;
			Community publicCommunity = null;
			String vmodelCommunityName = "VModel Community: visitor access ok. "
					+ randString;
			String publicCommunityName = "Public Community: no visitor access. "
					+ randString;
			String regularUserCommunityName = "Private community created by non-extended user: should not allow visitors"
					+ randString;
			String tag = "visitorshouldnotseethis";

			// employee.extended user is ajones480
			// visitor is ajones494
			// standard, default user is ajones242

			// Have employee.extended user create public community. Visitor will
			// not be invited to this one.
			publicCommunity = new Community(publicCommunityName,
					"Visitor should not be able to access this community.",
					Permissions.PUBLIC, tag);
			Entry publicCommResult = (Entry) extendedEmpService
					.createCommunity(publicCommunity);
			Community publicCommunityRetrieved = new Community(
					(Entry) service.getCommunity(publicCommResult
							.getEditLinkResolvedHref().toString()));

			testCommunity = new Community(vmodelCommunityName,
					"Visitor is a member and should have access.",
					Permissions.PRIVATE, null, true);
			Entry communityResult = (Entry) extendedEmpService
					.createCommunity(testCommunity);
			Community testCommunityRetrieved = new Community(
					(Entry) extendedEmpService.getCommunity(communityResult
							.getEditLinkResolvedHref().toString()));

			LOGGER.debug("Step 2: Add a external member as owner - should fail.");
			Member owner = new Member(visitor.getEmail(), null,
					Component.COMMUNITIES, Role.OWNER, MemberType.PERSON);
			Entry memberEntry = (Entry) extendedEmpService
					.addMemberToCommunity(testCommunityRetrieved, owner);
			assertTrue(memberEntry != null);
			assertEquals(400, extendedEmpService.getRespStatus());

			LOGGER.debug("Step 3: Add a external member as member - should pass.");
			Member member = new Member(visitor.getEmail(), null,
					Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
			memberEntry = (Entry) extendedEmpService.addMemberToCommunity(
					testCommunityRetrieved, member);
			assertTrue(memberEntry != null);
			assertEquals(201, extendedEmpService.getRespStatus());

			Feed communities = (Feed) extendedEmpService.getMyCommunities(
					false, null, 0, 0, null, null, null, null, null);
			assertTrue(communities != null);

			// create community object
			Community comm = null;
			for (Entry communityEntry : communities.getEntries()) {
				if (communityEntry.getTitle().equals(vmodelCommunityName)) {
					comm = new Community(communityEntry);
				}
			}

			LOGGER.debug("Step 4: Create a bookmark as the external user community member - should pass.");
			Bookmark bookmark = new Bookmark(StringGenerator.randomSentence(3),
					StringGenerator.randomSentence(4),
					"http://www.google.com/", "tagExternalUser");
			Entry bookmarkResponse = (Entry) visitorService
					.createCommunityBookmark(comm, bookmark);
			assertEquals(201, visitorService.getRespStatus());

			LOGGER.debug("Step 5: External member gets a feed of public communities - should fail");
			visitorService.getAllCommunities(false, null, 0, 0, null, null,
					null, null, null);
			assertEquals(403, visitorService.getRespStatus());

			LOGGER.debug("Step 6: External member gets a tag feed - should fail.");
			Feed ownersCommunities = (Feed) service.getAllCommunities(false,
					null, 0, 0, null, null, null, null, null);
			assertTrue(ownersCommunities != null);
			String selfLink = "";
			String tagFeedLink = "";
			selfLink = ownersCommunities.getSelfLinkResolvedHref().toString();
			if (!(selfLink.equals(""))) {
				tagFeedLink = selfLink + "?outputType=categories";
			}
			visitorService.getAnyFeed(tagFeedLink);
			assertEquals(403, visitorService.getRespStatus());

			LOGGER.debug("Step 7: External member access to single public community - should fail.");
			// publicCommunity is public and owned by the default user, but the
			// visitor is not a member.
			visitorService.getAnyFeed(publicCommunityRetrieved.getEditLink()
					.toString());
			assertEquals(403, visitorService.getRespStatus());

			// create community. This community should not allow external
			// members. No yellow banner.
			Factory factory = abdera.getFactory();
			Entry entryForExtUser = factory.newEntry();
			entryForExtUser
					.setTitle("This public community that does not allow external users"
							+ randString);
			entryForExtUser.setContent("RTC 119959 content");
			entryForExtUser.addSimpleExtension(
					StringConstants.SNX_COMMUNITY_TYPE, "public");
			entryForExtUser.addCategory(StringConstants.SCHEME_TYPE,
					"community", "");

			Community newCommunity = new Community(entryForExtUser);
			Entry commResult = (Entry) extendedEmpService
					.createCommunity(newCommunity);
			assertTrue(commResult != null);

			Community restrictedCommRetrieved = new Community(
					(Entry) extendedEmpService.getCommunity(commResult
							.getEditLinkResolvedHref().toString()));

			LOGGER.debug("Step 8: Adding external user to restricted community - should fail.");
			extendedEmpService.addMemberToCommunity(restrictedCommRetrieved,
					member);
			assertEquals(400, extendedEmpService.getRespStatus());

			LOGGER.debug("Step 9: Only employees using employee.extended can create communities that allow visitors, not regular users.");
			LOGGER.debug("Regular user creates a community that trys to add external users - should fail.");
			Community regularCommunity = new Community(
					regularUserCommunityName,
					"Regular user tries to create a community allowing visitors.",
					Permissions.PRIVATE, null, true);
			Entry regularCommResult = (Entry) service
					.createCommunity(regularCommunity);
			// Community regUserComm = new
			// Community((Entry)service.getCommunity(regularCommResult.getEditLinkResolvedHref().toString()));
			// service.addMemberToCommunity(regUserComm, owner);
			assertEquals(403, service.getRespStatus());

			LOGGER.debug("END TEST: RTC 119959: Negative tests for vmodel visitors");
		}
	}

	/*
	 * RTC Defect 119745 For deployment with visitor model not enabled. Steps:
	 * 1) Create a community, don't include <snx:isExternal> in the entry. 2)
	 * Get the created community. ensure <snx:isExternal> is false
	 * 
	 * Also validates defect 129757 - Atom API should not use External allowed
	 * as default value if user not allowed to create external communities
	 * (ajones242 shouldn't have that ability).
	 */
	@Test
	public void internalOnly() {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			LOGGER.debug("Beginning test RTC 119745: [VModel] For on premise, Communities created with the Atom API where external allowed is not specified should be internal only");
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
			String communityName = "RTC 119745 isExternal is not included "
					+ uniqueNameAddition;

			LOGGER.debug("Step 1: Create Community, don't include <snx:isExternal>");
			// create community
			Factory factory = abdera.getFactory();
			Entry entry = factory.newEntry();
			entry.setTitle(communityName);
			entry.setContent("RTC 119745 content");
			entry.addSimpleExtension(StringConstants.SNX_COMMUNITY_TYPE,
					"private");
			entry.addCategory(StringConstants.SCHEME_TYPE, "community", "");

			Community newCommunity = new Community(entry);
			Entry communityResult = (Entry) service
					.createCommunity(newCommunity);

			// get communities feed -
			Feed communitiesFeed = (Feed) service.getMyCommunities(false, null,
					0, 500, null, null, null, null, null);
			assertTrue(communitiesFeed != null);

			String isExternalValue = "";
			for (Entry communityEntry : communitiesFeed.getEntries()) {
				if (communityEntry.getTitle().equals(communityName)) {
					isExternalValue = communityEntry
							.getSimpleExtension(StringConstants.SNX_ISEXTERNAL);
				}
			}

			// Validate here:
			LOGGER.debug("Step 2: Validate that <isExternal> is false");
			assertEquals("false", isExternalValue);

			LOGGER.debug("End test RTC 119745: [VModel] For on premise, Communities created with the Atom API where external allowed is not specified should be internal only");
		}
	}

	/*
	 * RTC Defect 131495 Steps: Step 1: Create MODERATED Community, don't
	 * include <snx:isExternal> Step 2: Create PUBLIC Community, don't include
	 * <snx:isExternal> Step 3: Get a feed of my communities. Step 4: Validate
	 * that <isExternal> is false for both Public and Moderated communities.
	 */
	@Test
	public void moderatedInternalOnly() {
		LOGGER.debug("Beginning test RTC 131495: For on premise, public and moderated communities should default isExternal to false.");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String communityName1 = "RTC 131495 isExternal is false for moderated communities"
				+ uniqueNameAddition;
		String communityName2 = "RTC 131495 isExternal is false for public communities"
				+ uniqueNameAddition;

		LOGGER.debug("Step 1: Create MODERATED Community, don't include <snx:isExternal>");
		// create community
		Factory factory = abdera.getFactory();
		Entry entry1 = factory.newEntry();
		entry1.setTitle(communityName1);
		entry1.setContent("RTC 131495 content");
		// "publicInviteOnly" is Moderated.
		entry1.addSimpleExtension(StringConstants.SNX_COMMUNITY_TYPE,
				"publicInviteOnly");
		entry1.addCategory(StringConstants.SCHEME_TYPE, "community", "");

		Community newCommunity1 = new Community(entry1);
		Entry communityResult1 = (Entry) service.createCommunity(newCommunity1);

		LOGGER.debug("Step 2: Create PUBLIC Community, don't include <snx:isExternal>");
		// create community
		Entry entry2 = factory.newEntry();
		entry2.setTitle(communityName2);
		entry2.setContent("RTC 131495 content");
		// "publicInviteOnly" is Moderated.
		entry2.addSimpleExtension(StringConstants.SNX_COMMUNITY_TYPE, "public");
		entry2.addCategory(StringConstants.SCHEME_TYPE, "community", "");

		Community newCommunity2 = new Community(entry2);
		Entry communityResult2 = (Entry) service.createCommunity(newCommunity2);

		LOGGER.debug("Step 3: Get a feed of my communities.");
		Feed communitiesFeed = (Feed) assignedService.getMyCommunities(false, null, 0,
				500, null, null, null, null, null);
		assertTrue(communitiesFeed != null);

		String isExternalValue1 = "";
		String isExternalValue2 = "";
		for (Entry communityEntry : communitiesFeed.getEntries()) {
			if (communityEntry.getTitle().equals(communityName1)) {
				isExternalValue1 = communityEntry
						.getSimpleExtension(StringConstants.SNX_ISEXTERNAL);
			}
			if (communityEntry.getTitle().equals(communityName2)) {
				isExternalValue2 = communityEntry
						.getSimpleExtension(StringConstants.SNX_ISEXTERNAL);
			}
		}

		// Validate here:
		LOGGER.debug("Step 4: Validate that <isExternal> is false for both Public and Moderated communities.");
		// isExternalValue1 is for the Moderated community
		// isExternalValue2 is for the Public community
		assertEquals("false", isExternalValue1);
		assertEquals("false", isExternalValue2);

		LOGGER.debug("End test RTC 131495: For on premise, public and moderated communities should default isExternal to false.");
	}

	/*
	 * This test is for Smart Cloud only.
	 * 
	 * RTC 115864 - Only in-org emails should display in feeds for in-org
	 * members. Feeds from external users should not have access to in-org email
	 * addresses.Process: 1. Create a community 2. Add 2 members: a. A user from
	 * the same org (like ajones100) b. A user from outside the org (like jill
	 * white). 3. Generate a memberhsip feed of the Community. 4. Validate that
	 * the email value is populated for all members of the organization
	 * (ajones242 and ajones100). 5. Instantiate a user object for the non-org
	 * user. Try to add another in-org user (ajones101) using "email" param.
	 * This should fail with HTTP 400. 6. As default user add user ajones2. 7.
	 * As non-org user generate membership feed. 8. Validate that the feed does
	 * not contain email addresses for in-org users.
	 */
	@Test
	public void inOrgEmailInSC() throws FileNotFoundException, IOException {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("BEGIN TEST: RTC 115864: Populate email address for in-org users on feeds in SC");
			String randString = RandomStringUtils.randomAlphanumeric(4);
			Community publicCommunity = null;
			String communityName = "RTC 115864 email visibility in SC entries "
					+ randString;
			int IN_ORG_USER = 1; // ajones100
			int IN_ORG_USER2 = 3; // ajones2
			int OUT_OF_ORG_USER = 15; // jill white01

			LOGGER.debug("Step 1: Create private community");
			publicCommunity = new Community(
					communityName,
					"Entry email address should only be visible by in-org users.",
					Permissions.PRIVATE, null);
			publicCommunity.setIsExternal(true);
			Entry publicCommResult = (Entry) service
					.createCommunity(publicCommunity);

			String editLink = publicCommResult.getEditLinkResolvedHref()
					.toString();
			Community communityRetrieved = new Community(
					(Entry) assignedService.getCommunity(editLink));

			String membersLink = editLink.replace("instance", "members");

			LOGGER.debug("Step 2: Add a member not in the LDAP (non-org user)");
			UserPerspective outOfOrgUser=null;
			try {
				outOfOrgUser = new UserPerspective(OUT_OF_ORG_USER,
						Component.COMMUNITIES.toString(), useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			Factory factory = abdera.getFactory();
			Entry nonOrgMemberEntry = factory.newEntry();
			Person p = nonOrgMemberEntry.addContributor(outOfOrgUser
					.getRealName());
			p.addSimpleExtension(StringConstants.SNX_USERID,
					outOfOrgUser.getUserId());
			nonOrgMemberEntry.addCategory(
					"http://www.ibm.com/xmlns/prod/sn/type", "person", "");
			Element elmt = nonOrgMemberEntry.addSimpleExtension(
					StringConstants.SNX_ROLE, "member");
			elmt.setAttributeValue("component",
					"http://www.ibm.com/xmlns/prod/sn/communities");
			nonOrgMemberEntry.addExtension(elmt);

			service.postEntry(membersLink, nonOrgMemberEntry);

			UserPerspective inOrgUser=null;
			try {
				inOrgUser = new UserPerspective(IN_ORG_USER,
						Component.COMMUNITIES.toString(), useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			LOGGER.debug("Step 3: Add another member from the same org: "
					+ inOrgUser.getEmail());
			Member owner = new Member(inOrgUser.getEmail(), null,
					Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
			Entry memberEntry = (Entry) service.addMemberToCommunity(
					communityRetrieved, owner);

			Feed membersFeed = (Feed) assignedService.getCommunityMembers(
					communityRetrieved.getMembersListHref(), false, null, 1,
					10, null, null, null, null);
			// Validate that the memberFeed has email address for in-org user.
			LOGGER.debug("Step 4: Validate that the in-org user's email is in the feed.");
			for (Entry ntry : membersFeed.getEntries()) {
				for (Person member : ntry.getContributors()) {
					if (member.getName().equalsIgnoreCase(
							inOrgUser.getRealName()))
						assertEquals(
								true,
								member.getEmail().equalsIgnoreCase(
										inOrgUser.getEmail()));
				}
			}

			// Log on as external user and try to add another user to the
			// community.
			UserPerspective extUser=null,inOrgUser2=null;
			try {
				extUser = new UserPerspective(
						StringConstants.EXTERNAL_USER,
						Component.COMMUNITIES.toString(), useSSL);
				inOrgUser2 = new UserPerspective(IN_ORG_USER2,
						Component.COMMUNITIES.toString(), useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			Member user2 = new Member(inOrgUser2.getEmail(), null,
					Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
			Entry inOrgUser2Entry = (Entry) extUser.getCommunitiesService()
					.addMemberToCommunity(communityRetrieved, user2);
			// Validate return of 400
			assertEquals(400, extUser.getCommunitiesService().getRespStatus());

			// try same thing using service object
			inOrgUser2Entry = (Entry) service.addMemberToCommunity(
					communityRetrieved, user2);
			assertEquals(201, service.getRespStatus());

			LOGGER.debug("Step 5: Validate that non-org user can not access emails.  Email elements should be absent from contributor element.");
			Feed extUserMemberFeed = (Feed) extUser.getCommunitiesService()
					.getCommunityMembers(
							communityRetrieved.getMembersListHref(), false,
							null, 1, 10, null, null, null, null);

			for (Entry ntry : extUserMemberFeed.getEntries()) {
				for (Person member : ntry.getContributors()) {
					if (member.getName().equalsIgnoreCase(
							inOrgUser.getRealName()))
						assertEquals(null, member.getEmail());

					if (member.getName().equalsIgnoreCase(
							inOrgUser2.getRealName()))
						assertEquals(null, member.getEmail());
				}
			}

			LOGGER.debug("END TEST: RTC 115864: Populate email address for in-org users on feeds in SC");
		}
	}

	@Test
	public void uploadLogoImage() throws FileNotFoundException, IOException {
		/*
		 * Tests uploading of a community logo image by using an InputStream
		 * Step 1: Create Community Step 2: Get the logo url Step 3: Upload the
		 * image Step 4: Upload a different image Step 5: Validate the image
		 * 
		 * TJB 6/23/14 - Test works fine for on-premise deployments. Fails on
		 * SC. Will need to investigate. In the meantime, it's fine to run this
		 * daily on on-premise.
		 */
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(3);
			LOGGER.debug("BEGINNING TEST: Update Community Logo image");

			LOGGER.debug("Step 1... Create a Community");
			Community newCommunity = null;
			String communityName = "Upload Community image "
					+ uniqueNameAddition;
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				newCommunity = new Community(communityName,
						"Simple test for community logo image",
						Permissions.PRIVATE, null);
			} else {
				newCommunity = new Community(communityName,
						"Simple test for community logo image",
						Permissions.PUBLIC, null);
			}
			Entry communityResult = (Entry) service
					.createCommunity(newCommunity);
			assertEquals(201, service.getRespStatus());

			// get created community
			Entry communityEntry = (Entry) service.getCommunity(communityResult
					.getEditLinkResolvedHref().toString());

			String logoUrl = "";
			LOGGER.debug("Step 2... Get the logo url");
			for (Link link : communityEntry.getLinks()) {
				if (link.getRel().contains(StringConstants.REL_LOGO)) {
					logoUrl = link.getHref().toString();
				}
			}

			LOGGER.debug("Step 3... Upload a file");
			InputStream is = this.getClass().getResourceAsStream(
					"/resources/IBM_logo.jpg");
			FileEntry testFileEntry = new FileEntry(null, is,
					"UploadFileTest_.jpg", "Desciption", "",
					Permissions.PUBLIC, true, Notification.ON, Notification.ON,
					null, null, false, true, null, null, null);
			service.uploadFile(logoUrl, testFileEntry, "image/jpeg");
			service.getRespErrorMsg();

			LOGGER.debug("Step 4... Upload another file");
			InputStream is2 = this.getClass().getResourceAsStream(
					"/resources/lamborghini_murcielago_lp640.jpg");
			FileEntry testFileEntry2 = new FileEntry(null, is2,
					"UploadFileTest_.jpg", "Desciption", "",
					Permissions.PUBLIC, true, Notification.ON, Notification.ON,
					null, null, false, true, null, null, null);
			service.uploadFile(logoUrl, testFileEntry2, "image/jpeg");

			LOGGER.debug("Step 5... Get header information, validate file type and that size is greater than 0.");
			Entry logoEntry = (Entry) service.getAnyFeed(logoUrl);
			int contentLength = new Integer(logoEntry.getSimpleExtension("api",
					"Content-Length", "header")).intValue();
			String contentType = logoEntry.getSimpleExtension("api",
					"Content-Type", "header");

			assertEquals(
					"Content type is not correct for image (jpeg or png)",
					true,
					contentType.equals("image/png")
							|| contentType.equals("image/jpeg")
							|| contentType.equals("image/gif")
							|| contentType.equals("image/jpg"));
			assertEquals("File size is zero.", true, contentLength > 0);
			
			// Validation for defect #125406.  Test for illegal file type error message.
			service.uploadFile(logoUrl, testFileEntry2, "txt"); //Using .txt file type to trigger the error.
			String expectedErrorMsg = "The type of image file you provided is not supported.";
			int retCode = service.getRespStatus();
			String response = service.getRespErrorMsg();
			assertEquals("Expected error code not returned.", 400, retCode);
			assertEquals("Expected error msg not returned.", expectedErrorMsg, response);

			LOGGER.debug("ENDING TEST: Upload image");
		}
	}

	// @Test
	public void setBusinessOwner() throws FileNotFoundException, IOException {
		/*
		 * This test is Not complete.
		 * 
		 * RTC 127788 As an administrative user, I should be able to set a
		 * community's business owner via the API Step 1: Create Community Step
		 * 2: Retrieve community as org admin Step 3: Build the entry, add the
		 * business-owner category Step 4: Admin user to add user to community
		 * as business owner. Step 5: Get a feed. Validate that the business
		 * owner role is set.
		 * 
		 * This is a smart cloud only test.
		 */

		// Use Admin user for the org
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			int COMMUNITIES_ADMIN = 0;
			UserPerspective communitiesAdmin=null;
			try {
				communitiesAdmin = new UserPerspective(
						COMMUNITIES_ADMIN, Component.COMMUNITIES.toString(), useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CommunitiesService communitiesAdminService = communitiesAdmin
					.getCommunitiesService();

			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(3);
			Community testCommunity = null;

			testCommunity = new Community(
					"RTC 127788 Set Business Owner. " + uniqueNameAddition,
					"As an administrative user, I should be able to set a community's business owner via the API",
					Permissions.PUBLIC, null);

			// Step 1. Create a community
			Entry communityEntry = (Entry) communitiesAdminService
					.createCommunity(testCommunity);

			// Step 2. Retrieve community
			Community testCommunityRetrieved = new Community(
					(Entry) communitiesAdminService.getCommunity(communityEntry
							.getEditLinkResolvedHref().toString()));

			// the other user
			Member member = new Member(otherUser.getEmail(),
					otherUser.getUserId(), Component.COMMUNITIES, Role.MEMBER,
					MemberType.PERSON);
			Entry memberEntry = (Entry) communitiesAdminService
					.addMemberToCommunity(testCommunityRetrieved, member);
			assertEquals(" Add Community Member ", 201,
					communitiesAdminService.getRespStatus());

			Feed membersFeedBefore = (Feed) communitiesAdminService
					.getCommunityMembers(
							testCommunityRetrieved.getMembersListHref(), false,
							null, 1, 10, null, null, null, null);

			/*
			 * <entry xmlns="http://www.w3.org/2005/Atom"
			 * xmlns:snx="http://www.ibm.com/xmlns/prod/sn"> 1. <contributor>
			 * <snx:userid>8cbefec0-f6df-1032-9ad5-d02a14283ea9</snx:userid>
			 * </contributor> 2. <category
			 * scheme="http://www.ibm.com/xmlns/prod/sn/type" term="person" />
			 * 
			 * 3. <category term="business-owner"
			 * scheme="http://www.ibm.com/xmlns/prod/sn/type"></category>
			 * 
			 * 4. <snx:role
			 * component="http://www.ibm.com/xmlns/prod/sn/communities"
			 * >member</snx:role> </entry>
			 */

			// Step 3. Build the entry, add the business-owner category
			Factory factory = abdera.getFactory();
			Person contributer = factory.newContributor();
			if (otherUser.getUserId() != null) {
				contributer.addSimpleExtension(StringConstants.SNX_USERID,
						otherUser.getUserId());

				Entry entryForExtUser = factory.newEntry();
				entryForExtUser.addContributor(contributer);
				entryForExtUser.addCategory(StringConstants.SCHEME_TYPE,
						"person", "");
				entryForExtUser.addCategory(StringConstants.SCHEME_TYPE,
						"business-owner", ""); // only on SC!

				Element roleExtension = factory
						.newExtensionElement(StringConstants.SNX_ROLE);
				roleExtension.setAttributeValue("component",
						"http://www.ibm.com/xmlns/prod/sn/" + "communities");
				roleExtension.setText("member");
				entryForExtUser.addExtension(roleExtension);

				// POST works PUT doesn't
				// Step 4. Admin user to add user to community as business
				// owner.
				// Entry memberEntry = (Entry)
				// communitiesAdminService.addMemberToCommunity(testCommunityRetrieved,
				// member);
				communitiesAdminService.putEntry(memberEntry
						.getEditLinkResolvedHref().toString(), entryForExtUser);
				// assertEquals(" Add Community Member ", 201,
				// communitiesAdminService.getRespStatus()); s/b 200?

				// Step 5. Get a feed. Validate that the business owner role is
				// set.
				Feed membersFeed = (Feed) communitiesAdminService
						.getCommunityMembers(
								testCommunityRetrieved.getMembersListHref(),
								false, null, 1, 10, null, null, null, null);
				membersFeed.getContributors();

			}
		}
	}

	/*
	 * RTC Defect 132897 Negative test - A member adding an entry record for
	 * himself should produce HTTP 400 - Bad Request.
	 * 
	 * Process: 1. Create a community. 2. Create a sub community. 3. Non member
	 * attempts to self invite to the subcommunity. Should fail HTTP 400 Bad
	 * Request. 4. Validate error code and message.
	 */
	@Test
	public void selfInviteError() {
		LOGGER.debug("Beginning test RTC 132897: Atom API a non member should not be allowed to self-invite to a sub community.");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String communityName = "RTC 132897 Parent Community name "
				+ uniqueNameAddition;
		String subCommunityName = "RTC 132897 Subcommunity name. "
				+ uniqueNameAddition;
		String errorMsg = "Some members could not be added because they were not members of the parent community.";

		LOGGER.debug("Step 1: Create Community.");
		Community testCommunity = new Community(communityName,
				"Sub-community self invite test", Permissions.PUBLIC, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);
		Community community = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		LOGGER.debug("Step 2: Creating Subcommunity in Community: "
				+ community.getTitle());
		Subcommunity subcomm = new Subcommunity(subCommunityName,
				"SubCommunity content", Permissions.PUBLIC,
				"RTC132897SubCommunity");
		Entry subcommResult = (Entry) service.createSubcommunity(community,
				subcomm);
		LOGGER.debug("Subcommunity: " + subcomm.getTitle()
				+ " successfully created @ "
				+ subcommResult.getEditLinkResolvedHref().toString());
		Community subCommunityRetrieved = new Community(
				(Entry) service.getCommunity(subcommResult
						.getEditLinkResolvedHref().toString()));

		LOGGER.debug("Step 3: As a non-member, try to self invite to the subcommunity. This should fail HTTP 400 - Bad Request.");
		Member member = new Member(otherUser.getEmail(), otherUser.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		Entry memberEntry = (Entry) otherUserService.addMemberToCommunity(
				subCommunityRetrieved, member);

		LOGGER.debug("Step 4: Validate error message and code.");
		assertEquals("Incorrect error message returned", true, otherUserService
				.getRespErrorMsg().equalsIgnoreCase(errorMsg));
		assertEquals("Incorrect HTTP error code returned", true,
				otherUserService.getRespStatus() == 400);

		LOGGER.debug("Beginning test RTC 132897: Atom API a non member should not be allowed to self-invite to a sub community.");
	}

	@Test
	public void memberEmailPrivileges() {

		LOGGER.debug("BEGINNING TEST RTC 135064 - Member email privileges.");

		/*
		 * This addition allows multiple executions of this test. Otherwise, a
		 * delete method would be needed to erase the community at the start of
		 * each test.
		 */
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

		// Create community - value not provided, verify default value returned.
		Community newCommunity = new Community(
				"RTC 135064 - Member email privileges " + uniqueNameAddition,
				"", Permissions.PUBLIC, null);
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		String editUrl = communityResult.getEditLinkResolvedHref().toString();
		Community comm = new Community((Entry) assignedService.getCommunity(editUrl));
		Element memberEmailPrivileges = comm.getMemberEmailPrivileges();
		assertNotNull(memberEmailPrivileges);
		assertEquals(StringConstants.MEMBERS_EMAIL_PRIVILEGES_ENTIRE_COMMUNITY,
				memberEmailPrivileges.getText());

		// Update the community to block member email. Verify update persists
		comm.setMemberEmailPrivilegesValue(StringConstants.MEMBERS_EMAIL_PRIVILEGES_NO_ONE);
		service.putEntry(editUrl, comm.toEntry());
		comm = new Community((Entry) assignedService.getCommunity(editUrl));
		memberEmailPrivileges = comm.getMemberEmailPrivileges();
		assertNotNull(memberEmailPrivileges);
		assertEquals(StringConstants.MEMBERS_EMAIL_PRIVILEGES_NO_ONE,
				memberEmailPrivileges.getText());

		// Update the community with value not provided Verify old value
		// persists
		comm.setMemberEmailPrivileges(null);
		service.putEntry(editUrl, comm.toEntry());
		comm = new Community((Entry) assignedService.getCommunity(editUrl));
		memberEmailPrivileges = comm.getMemberEmailPrivileges();
		assertNotNull(memberEmailPrivileges);
		assertEquals(StringConstants.MEMBERS_EMAIL_PRIVILEGES_NO_ONE,
				memberEmailPrivileges.getText());

		// Create community - with value provided, verify value persists.
		uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		newCommunity = new Community("RTC 135064 - Member email privileges "
				+ uniqueNameAddition, "", Permissions.PUBLIC, null);
		newCommunity
				.setMemberEmailPrivilegesValue(StringConstants.MEMBERS_EMAIL_PRIVILEGES_OWNERS_ONLY);
		communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		editUrl = communityResult.getEditLinkResolvedHref().toString();
		comm = new Community((Entry) assignedService.getCommunity(editUrl));
		memberEmailPrivileges = comm.getMemberEmailPrivileges();
		assertNotNull(memberEmailPrivileges);
		assertEquals(StringConstants.MEMBERS_EMAIL_PRIVILEGES_OWNERS_ONLY,
				memberEmailPrivileges.getText());

		LOGGER.debug("ENDING TEST RTC 135064 - Member email privileges.");
	}

	@Test
	public void listWhenRestrictedPersistence() {

		LOGGER.debug("BEGINNING TEST RTC 154252 - List when restricted.");

		if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD && isListRestrictedGKEnabled()) {			
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
	
			// Create community - value not provided, verify default value returned.
			Community newCommunity = new Community(
					"RTC 154252 - List when restricted " + uniqueNameAddition,
					"", Permissions.PRIVATE, null);
			Entry communityResult = (Entry) service.createCommunity(newCommunity);
			assertTrue(communityResult != null);
	
			String editUrl = communityResult.getEditLinkResolvedHref().toString();
			Community comm = new Community((Entry) assignedService.getCommunity(editUrl));
			Element listWhenPrivate = comm.getListWhenPrivateElement();
			assertNotNull(listWhenPrivate);
			assertEquals("false", listWhenPrivate.getText());
	
			// Update the community to list when private. Verify update persists
			comm.setListWhenPrivate(true);
			service.putEntry(editUrl, comm.toEntry());
			comm = new Community((Entry) assignedService.getCommunity(editUrl));
			listWhenPrivate = comm.getListWhenPrivateElement();
			assertNotNull(listWhenPrivate);
			assertEquals("true", listWhenPrivate.getText());
	
			// Update the community with value not provided Verify old value
			// persists
			comm.setListWhenPrivate(null);
			service.putEntry(editUrl, comm.toEntry());
			comm = new Community((Entry) assignedService.getCommunity(editUrl));
			listWhenPrivate = comm.getListWhenPrivateElement();
			assertNotNull(listWhenPrivate);
			assertEquals("true", listWhenPrivate.getText());
	
			// Create community - with value provided, verify value persists.
			uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
			newCommunity = new Community("RTC 154252 - List when restricted "
					+ uniqueNameAddition, "", Permissions.PRIVATE, null);
			newCommunity.setListWhenPrivate(true);
			communityResult = (Entry) service.createCommunity(newCommunity);
			assertTrue(communityResult != null);
	
			editUrl = communityResult.getEditLinkResolvedHref().toString();
			comm = new Community((Entry) assignedService.getCommunity(editUrl));
			listWhenPrivate = comm.getListWhenPrivateElement();
			assertNotNull(listWhenPrivate);
			assertEquals("true", listWhenPrivate.getText());
		}

		LOGGER.debug("ENDING TEST RTC 154252 - List when restricted.");
	}

	private boolean isListRestrictedGKEnabled() {
		String gkSetting = gateKeeperService.getGateKeeperSetting(OnPremOrgId, "COMMUNITIES_LIST_RESTRICTED");
		
		// Might be better to parse JSON but this should do the trick for this simple JSON response
		return gkSetting.contains("\"value\": true");
	}

	/**
	 * Test monthly Community calendar events by day.
	 * 
	 * Steps: 1. Create Community 2. Add Events (aka Calendar) widget to created
	 * community 3. Create monthly events by day - 31st day of the month - for
	 * the entire year
	 */
	@Test
	public void testCommunityCalendarMonthlyEventsByDay() {
		LOGGER.debug("Testing Community calendar monthly (by day) events ...");

		// Create community
		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityCalendarMonthlyEventsTest"
				+ timeStamp);

		// Add Events (aka Calendar) widget to the created community
		Widget widget = new Widget(
				StringConstants.WidgetID.Calendar.toString(), "col3");
		service.postWidget(community, widget.toEntry());
		assertEquals("add calendar widget", 201, service.getRespStatus());

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
							LOGGER.debug("event_url: " + event_url);
						}
					}
					LOGGER.debug("Service URL: " + service_url);

					// Get calendar service doc
					ExtensibleElement serviceFeed = assignedService
							.getCommunityCalendarService(service_url);
					assertTrue(serviceFeed != null);

					// Retrieve calendar
					LOGGER.debug("Retrieve Community Calendar");
					String calendar_url = service_url.replace(
							"calendar/service?", "calendar?");
					ExtensibleElement calendarFeed = assignedService
							.getCommunityCalendarService(calendar_url);
					assertTrue(calendarFeed != null);

					String role = calendarFeed.getExtension(
							StringConstants.SNX_MAP_ROLE).getText();

					if (!role.equalsIgnoreCase("reader")) {
						// Post monthly 31st day calendar events
						Event event = new Event("My monthly 31st day events",
								StringConstants.STRING_NO_LOWERCASE,
								StringConstants.STRING_MONTHLY, null,
								"2015-12-31T00:00:00.000Z",
								"2015-01-01T00:00:00.000Z",
								"2015-01-01T23:45:00.000Z", 1,
								StringConstants.STRING_MONTHLY_BY_DAY);
						String eventUrl = event_url.replace(
								"type=event&calendarUuid", "calendarUuid");
						ExtensibleElement eventFeed = service
								.postCalendarEvent(eventUrl, event);
						assertTrue(eventFeed != null);

						// Retrieve calendar events
						if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
							eventFeed = (Feed) assignedService
									.getCalendarFeedWithRedirect(eventUrl
											+ "&startDate=2015-01-01T00:00:00Z&endDate=2016-01-01T00:00:00Z");
						} else {
							eventFeed = (Feed) assignedService
									.getCalendarFeed(eventUrl
											+ "&startDate=2015-01-01T00:00:00Z&endDate=2016-01-01T00:00:00Z");
						}

						assertTrue(eventFeed != null);
						// There should be 7 monthly events per calendar year
						// that have the 31st day
						// ie. there are 7 months with 31 days
						assertEquals(7, ((Feed) eventFeed).getEntries().size());
					}
				}
			}
		}

		LOGGER.debug("... finished testing Community calendar monthly (by day) events");
	}

	/**
	 * Test monthly Community calendar events by day (paging).
	 * 
	 * Steps: 1. Create Community 2. Add Events (aka Calendar) widget to created
	 * community 3. Create monthly events by day - 31st day of the month - for
	 * the entire year 4. Retrieve paged events
	 */
	@Test
	public void testCommunityCalendarMonthlyEventsByDayPaging() {
		LOGGER.debug("Testing Community calendar monthly (by day) events (paged) ...");

		// Create community
		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityCalendarMonthlyEventsTest"
				+ timeStamp);

		// Add Events (aka Calendar) widget to the created community
		Widget widget = new Widget(
				StringConstants.WidgetID.Calendar.toString(), "col3");
		service.postWidget(community, widget.toEntry());
		assertEquals("add calendar widget", 201, service.getRespStatus());

		Feed remoteAppsFeed = (Feed) assignedService.getCommunityRemoteAPPs(
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
							LOGGER.debug("event_url: " + event_url);
						}
					}
					LOGGER.debug("Service URL: " + service_url);

					// Get calendar service doc
					ExtensibleElement serviceFeed = assignedService
							.getCommunityCalendarService(service_url);
					assertTrue(serviceFeed != null);

					// Retrieve calendar
					LOGGER.debug("Retrieve Community Calendar");
					String calendar_url = service_url.replace(
							"calendar/service?", "calendar?");
					ExtensibleElement calendarFeed = assignedService
							.getCommunityCalendarService(calendar_url);
					assertTrue(calendarFeed != null);

					String role = calendarFeed.getExtension(
							StringConstants.SNX_MAP_ROLE).getText();

					if (!role.equalsIgnoreCase("reader")) {
						// Monthly 31st day calendar events - 7 items
						Event monthlyEvent = new Event(
								"My monthly 31st day events",
								StringConstants.STRING_NO_LOWERCASE,
								StringConstants.STRING_MONTHLY, null,
								"2015-12-31T00:00:00.000Z",
								"2015-01-31T00:00:00.000Z",
								"2015-01-31T23:45:00.000Z", 1,
								StringConstants.STRING_MONTHLY_BY_DAY);
						// Monthly 1st day calendar events - 12 items
						Event monthlyEvent2 = new Event(
								"My monthly 1st day events",
								StringConstants.STRING_NO_LOWERCASE,
								StringConstants.STRING_MONTHLY, null,
								"2015-12-31T00:00:00.000Z",
								"2015-01-01T00:00:00.000Z",
								"2015-01-01T23:45:00.000Z", 1,
								StringConstants.STRING_MONTHLY_BY_DAY);
						monthlyEvent2.getRecurrenceElement()
								.getFirstChild(StringConstants.SNX_BYDATE)
								.setText("1");
						String eventUrl = event_url.replace(
								"type=event&calendarUuid", "calendarUuid");
						ExtensibleElement eventFeed = service
								.postCalendarEvent(eventUrl, monthlyEvent);
						assertTrue(eventFeed != null);
						eventFeed = service.postCalendarEvent(eventUrl,
								monthlyEvent2);
						assertTrue(eventFeed != null);

						// Retrieve calendar events
						// Default page size is 10
						if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
							eventFeed = (Feed) assignedService
									.getCalendarFeedWithRedirect(eventUrl
											+ "&startDate=2015-01-01T00:00:00Z&endDate=2016-01-01T00:00:00Z&page=1");
						} else {
							eventFeed = (Feed) assignedService
									.getCalendarFeed(eventUrl
											+ "&startDate=2015-01-01T00:00:00Z&endDate=2016-01-01T00:00:00Z&page=1");
						}
						assertTrue(eventFeed != null);
						assertEquals(10, ((Feed) eventFeed).getEntries().size());

						if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
							eventFeed = (Feed) assignedService
									.getCalendarFeedWithRedirect(eventUrl
											+ "&startDate=2015-01-01T00:00:00Z&endDate=2016-01-01T00:00:00Z&page=2");
						} else {
							eventFeed = (Feed) assignedService
									.getCalendarFeed(eventUrl
											+ "&startDate=2015-01-01T00:00:00Z&endDate=2016-01-01T00:00:00Z&page=2");
						}
						assertTrue(eventFeed != null);
						assertEquals(9, ((Feed) eventFeed).getEntries().size());

						// Another test to cover different scenario of the
						// algorithm used in retrieving events
						if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
							eventFeed = (Feed) assignedService
									.getCalendarFeedWithRedirect(eventUrl
											+ "&startDate=2015-01-01T08:00:00Z&endDate=2016-01-01T00:00:00Z&page=1");
						} else {
							eventFeed = (Feed) assignedService
									.getCalendarFeed(eventUrl
											+ "&startDate=2015-01-01T08:00:00Z&endDate=2016-01-01T00:00:00Z&page=1");
						}
						assertTrue(eventFeed != null);
						assertEquals(10, ((Feed) eventFeed).getEntries().size());

						if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
							eventFeed = (Feed) assignedService
									.getCalendarFeedWithRedirect(eventUrl
											+ "&startDate=2015-01-01T08:00:00Z&endDate=2016-01-01T00:00:00Z&page=2");
						} else {
							eventFeed = (Feed) assignedService
									.getCalendarFeed(eventUrl
											+ "&startDate=2015-01-01T08:00:00Z&endDate=2016-01-01T00:00:00Z&page=2");
						}
						assertTrue(eventFeed != null);
						assertEquals(9, ((Feed) eventFeed).getEntries().size());
					}
				}
			}
		}

		LOGGER.debug("... finished testing Community calendar monthly (by day) events (paged)");
	}

	/**
	 * Test monthly Community calendar events by day of week.
	 * 
	 * Steps: 1. Create Community 2. Add Events (aka Calendar) widget to created
	 * community 3. Create monthly events by day of week - last Saturday of the
	 * month - for the entire year
	 */
	@Test
	public void testCommunityCalendarMonthlyEventsByDayOfWeek() {
		LOGGER.debug("Testing Community calendar monthly (by day of week) events ...");

		// Create community
		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityCalendarMonthlyEventsTest"
				+ timeStamp);

		// Add Events (aka Calendar) widget to the created community
		Widget widget = new Widget(
				StringConstants.WidgetID.Calendar.toString(), "col3");
		service.postWidget(community, widget.toEntry());
		assertEquals("add calendar widget", 201, service.getRespStatus());

		Feed remoteAppsFeed = (Feed) assignedService.getCommunityRemoteAPPs(
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
							LOGGER.debug("event_url: " + event_url);
						}
					}
					LOGGER.debug("Service URL: " + service_url);

					// Get calendar service doc
					ExtensibleElement serviceFeed = assignedService
							.getCommunityCalendarService(service_url);
					assertTrue(serviceFeed != null);

					// Retrieve calendar
					LOGGER.debug("Retrieve Community Calendar");
					String calendar_url = service_url.replace(
							"calendar/service?", "calendar?");
					ExtensibleElement calendarFeed = assignedService
							.getCommunityCalendarService(calendar_url);
					assertTrue(calendarFeed != null);

					String role = calendarFeed.getExtension(
							StringConstants.SNX_MAP_ROLE).getText();

					if (!role.equalsIgnoreCase("reader")) {
						// Post monthly 31st day calendar events
						Event event = new Event(
								"My last Sat. of the month monthly events",
								StringConstants.STRING_NO_LOWERCASE,
								StringConstants.STRING_MONTHLY, null,
								"2015-12-31T00:00:00.000Z",
								"2015-01-01T00:00:00.000Z",
								"2015-01-01T23:45:00.000Z", 1,
								StringConstants.STRING_MONTHLY_BY_DAY_OF_WEEK);
						String eventUrl = event_url.replace(
								"type=event&calendarUuid", "calendarUuid");
						ExtensibleElement eventFeed = service
								.postCalendarEvent(eventUrl, event);
						assertTrue(eventFeed != null);

						// Retrieve calendar events
						// Test passing only startDate
						if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
							eventFeed = (Feed) assignedService
									.getCalendarFeedWithRedirect(eventUrl
											+ "&startDate=2015-01-01T00:00:00Z&ps=20");
						} else {
							eventFeed = (Feed) assignedService.getCalendarFeed(eventUrl
									+ "&startDate=2015-01-01T00:00:00Z&ps=20");
						}

						assertTrue(eventFeed != null);
						// There should be 12 monthly events per calendar year
						// (one for each month)
						assertEquals(12, ((Feed) eventFeed).getEntries().size());

						// Test passing only endDate
						if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
							eventFeed = (Feed) assignedService
									.getCalendarFeedWithRedirect(eventUrl
											+ "&endDate=2016-01-01T00:00:00Z&ps=20");
						} else {
							eventFeed = (Feed) assignedService.getCalendarFeed(eventUrl
									+ "&endDate=2016-01-01T00:00:00Z&ps=20");
						}

						assertTrue(eventFeed != null);
						// There should be 12 monthly events per calendar year
						// (one for each month)
						assertEquals(12, ((Feed) eventFeed).getEntries().size());
					}
				}
			}
		}

		LOGGER.debug("... finished testing Community calendar monthly (by day of week) events");
	}

	/*
	 * RTC Defect 112621 Fetching members feed with sortBy set to title should
	 * return HTTP 400
	 * 
	 * Process: 1. Create a community. 2. Add member 3. Execute a URL with a bad
	 * parameter. Should fail HTTP 400 Bad Request. 4. Validate error code and
	 * message.
	 */
	@Test
	public void badParameter() throws MalformedURLException, URISyntaxException {
		LOGGER.debug("Beginning test RTC 112621: Fetching members feed with sortBy set to title should return HTTP 400");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String communityName = "RTC 112621 Parent Community name "
				+ uniqueNameAddition;
		String errorMsg = "The specified sort field is not valid.";

		LOGGER.debug("Step 1: Create Community.");
		Community testCommunity = new Community(communityName,
				"Sub-community self invite test", Permissions.PUBLIC, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);
		Community community = new Community(
				(Entry) assignedService.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		LOGGER.debug("Step 2: Add member.");
		Member member = new Member(null, otherUser.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		Entry memberEntry = (Entry) service.addMemberToCommunity(community,
				member);
		assertEquals("extended user add External user", 201,
				service.getRespStatus());

		String url = memberEntry.getEditLinkResolvedHref().toURL().toString();
		String[] urlTokens = url.split("&");
		String badUrl = urlTokens[0] + "&role=owner&ps=3&sortBy=title";

		LOGGER.debug("Step 3: Execute URL with bad parameter.  This should fail: HTTP 400");
		assignedService.getAnyFeed(badUrl);

		LOGGER.debug("Step 4: Validate error message and code.");
		assertEquals("Incorrect error message returned", true, service
				.getRespErrorMsg().equalsIgnoreCase(errorMsg));
		assertEquals("Incorrect HTTP error code returned", true,
				service.getRespStatus() == 400);

		LOGGER.debug("Ending test RTC 112621: Fetching members feed with sortBy set to title should return HTTP 400");
	}

	@Test
	public void testBatchFollowMultipleCommunities() throws Exception {
		LOGGER.debug("Test betch follow/unfollow for multiple communities");
		LOGGER.debug("Step 1: Create communities");
		String randString = RandomStringUtils.randomAlphanumeric(5);
		String communityTitle1 = "testBatchFollowMultiCommunities" + randString;
		Community testCommunity1 = new Community(communityTitle1,
				"A community for testing", Permissions.PUBLIC, null);
		Entry communityResult1 = (Entry) service
				.createCommunity(testCommunity1);
		Community communityRetrieved1 = new Community(
				(Entry) assignedService.getCommunity(communityResult1
						.getEditLinkResolvedHref().toString()));

		String randString2 = RandomStringUtils.randomAlphanumeric(5);
		String communityTitle2 = "testBatchFollowMultiCommunities"
				+ randString2;
		Community testCommunity2 = new Community(communityTitle2,
				"A community for testing", Permissions.PUBLIC, null);
		Entry communityResult2 = (Entry) service
				.createCommunity(testCommunity2);
		Community communityRetrieved2 = new Community(
				(Entry) assignedService.getCommunity(communityResult2
						.getEditLinkResolvedHref().toString()));

		LOGGER.debug("Step 2 Post follow feed");
		FollowEntry follow1 = new FollowEntry(
				StringConstants.COMMUNITIES_SOURCE,
				communityRetrieved1.getUuid(),
				StringConstants.COMMUNITY_RESOURCE_TYPE);
		FollowEntry follow2 = new FollowEntry(
				StringConstants.COMMUNITIES_SOURCE,
				communityRetrieved2.getUuid(),
				StringConstants.COMMUNITY_RESOURCE_TYPE);

		Feed feed = follow1.getFactory().newFeed();
		feed.addEntry(follow1.toEntry());
		feed.addEntry(follow2.toEntry());

		otherUserService.postFollowFeedForUser(feed);
		assertEquals("Batch follow ", 200, otherUserService.getRespStatus());

		LOGGER.debug("Step 3: Get followed communities as that user");
		Feed followedCommunitiesFeed = (Feed) otherUserService
				.getFollowedCommunities();

		LOGGER.debug("Step 4: Verify that the communities exists in followed communities");
		assertTrue(isCommunityFollowed(testCommunity1, followedCommunitiesFeed));
		assertTrue(isCommunityFollowed(testCommunity2, followedCommunitiesFeed));

		LOGGER.debug("Step 5: Do batch delete");
		otherUserService.deleteFollowFeedForUser(feed);
		assertEquals("Batch unfollow ", 200, otherUserService.getRespStatus());
	}

	public static boolean isCommunityFollowed(Community testCommunity1,
			Feed followedCommunitiesFeed) {
		boolean foundCommunity = false;
		for (Entry en : followedCommunitiesFeed.getEntries()) {
			if (en.getTitle().equals(testCommunity1.getTitle()))
				foundCommunity = true;
		}
		if (!foundCommunity && followedCommunitiesFeed.getEntries().size() > 99) {
			LOGGER.debug("-: followed comm is outside "
					+ followedCommunitiesFeed.getEntries().size());
			// TODO: verify the community is being followed
			foundCommunity = true;
		}
		return foundCommunity;
	}

	/**
	 * Test monthly Community calendar events by day of week, duration 1 hour
	 * 
	 * Steps: 1. Create Community 2. Add Events (aka Calendar) widget to created
	 * community 3. Create monthly events by day of week - starts from 2015.3.5
	 * ends at 2015.9.25 - every first Thursday
	 */
	@Test
	public void testCommunityCalendarMonthlyEventsByDayOfWeekOneHour() {
		LOGGER.debug("Testing Community calendar monthly (by day of week) events, every first Thursday ...");

		// Create community
		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityCalendarMonthlyEventsTest"
				+ timeStamp);

		// Add Events (aka Calendar) widget to the created community
		Widget widget = new Widget(
				StringConstants.WidgetID.Calendar.toString(), "col3");
		service.postWidget(community, widget.toEntry());
		assertEquals("add calendar widget", 201, service.getRespStatus());

		Feed remoteAppsFeed = (Feed) assignedService.getCommunityRemoteAPPs(
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
							LOGGER.debug("event_url: " + event_url);
						}
					}
					LOGGER.debug("Service URL: " + service_url);

					// Get calendar service doc
					ExtensibleElement serviceFeed = assignedService
							.getCommunityCalendarService(service_url);
					assertTrue(serviceFeed != null);

					// Retrieve calendar
					LOGGER.debug("Retrieve Community Calendar");
					String calendar_url = service_url.replace(
							"calendar/service?", "calendar?");
					ExtensibleElement calendarFeed = assignedService
							.getCommunityCalendarService(calendar_url);
					assertTrue(calendarFeed != null);

					String role = calendarFeed.getExtension(
							StringConstants.SNX_MAP_ROLE).getText();

					if (!role.equalsIgnoreCase("reader")) {
						Event event = new Event(
								"My first Thursday of the month monthly events, every first Thursday",
								StringConstants.STRING_NO_LOWERCASE,
								StringConstants.STRING_MONTHLY, null,
								"2015-09-25T00:00:00.000Z",
								"2015-03-05T18:00:00.000Z",
								"2015-03-05T19:00:00.000Z", 1,
								StringConstants.STRING_MONTHLY_BY_DAY_OF_WEEK,
								"1,TH");
						String eventUrl = event_url.replace(
								"type=event&calendarUuid", "calendarUuid");
						ExtensibleElement eventFeed = service
								.postCalendarEvent(eventUrl, event);
						assertTrue(eventFeed != null);

						// Retrieve calendar events
						if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
							eventFeed = (Feed) assignedService
									.getCalendarFeedWithRedirect(eventUrl
											+ "&startDate=2015-01-01T00:00:00&ps=20");
						} else {
							eventFeed = (Feed) assignedService.getCalendarFeed(eventUrl
									+ "&startDate=2015-01-01T00:00:00&ps=20");
						}

						assertTrue(eventFeed != null);
						// There should be 7 monthly events per calendar year
						// (one for each month)
						assertEquals(7, ((Feed) eventFeed).getEntries().size());
					}
				}
			}
		}

		LOGGER.debug("... finished testing Community calendar monthly (by day of week) events, every first Thursday");
	}

	@Test
	public void testCommunitySummaries() throws Exception {
		/*
		 * Tests the ability to create a community invitiation 
		 * Step 1: Create a community 
		 * Step 2: Find the community using the summary api
		 */
		LOGGER.debug("Beginning test: Create community");
		String timeStamp = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a community");
		// CommunitiesService service2 = user2.getCommunitiesService();
		Community testCommunity = new Community(
				"Test communitySummaries " + timeStamp,
				"Find me", Permissions.PRIVATE,
				null, false);

		Entry communityEntry = (Entry) otherUserService
				.createCommunity(testCommunity);
		assertEquals("create Community ", 201, otherUserService.getRespStatus());

		LOGGER.debug("Step 2: Find it");
		Community communityRetrieved = new Community(
				(Entry) otherUserService.getCommunity(communityEntry
						.getEditLinkResolvedHref().toString()));

		String baseUrl = otherUserService.getServiceURLString();
		ClientResponse response = otherUserService.doSearch(baseUrl + "/service/json/v1/community_summaries?name=" + timeStamp);
		assertEquals(200,response.getStatus() );
		
		String text = IOUtils.toString(response.getReader());
		
		// ensure response is valid JSON
		// and has at least one match
		JSONObject obj = JSONObject.parse(text);
		assertTrue(JSONObject.isValidObject(obj));
		JSONArray items = (JSONArray) obj.get("items");
		assertTrue(items.size() > 0);
		
		assertTrue(text.contains(communityRetrieved.getUuid()));
		

		LOGGER.debug("Ending test: community summaries");
	}

    @Test
    public void testCommunityNameTypeahead() throws Exception {
        LOGGER.debug("Beginning CommunityNameTypeahead test: Create communities for communityNameTypeahead");
        String timeStamp = Utils.logDateFormatter.format(new Date());

        LOGGER.debug("Step 1: Create test communities");
        Community testCommunity = new Community("Test communityNameTypeahead - public invite only community - " + timeStamp + "CNTA", "Find me", Permissions.PUBLICINVITEONLY, null, false);
        Community testCommunity2 = new Community("Test communityNameTypeahead - public community - " + timeStamp  + "CNTA", "Find me", Permissions.PUBLIC, null, false);
        Community testCommunity3 = new Community("Test communityNameTypeahead - private community but member - " + timeStamp  + "CNTA", "Find me", Permissions.PRIVATE, null, false);
        Community testCommunity4 = new Community("Test communityNameTypeahead - private community NOT a member - " + timeStamp + "CNTA", "Find me", Permissions.PRIVATE, null, false);
        Entry communityEntry = (Entry) otherUserService.createCommunity(testCommunity);
        assertEquals("create Community ", 201, otherUserService.getRespStatus());
        Entry communityEntry2 = (Entry) otherUserService.createCommunity(testCommunity2);
        assertEquals("create Community2 ", 201, otherUserService.getRespStatus());
        Entry communityEntry3 = (Entry) otherUserService.createCommunity(testCommunity3);
        assertEquals("create Community3 ", 201, otherUserService.getRespStatus());
        Entry communityEntry4 = (Entry) otherUserService.createCommunity(testCommunity4);
        assertEquals("create Community4 ", 201, otherUserService.getRespStatus());
        
        // add the main user to membership of private community
        LOGGER.debug("Step 1a: add user " + StringConstants.USER_EMAIL + " to community " + testCommunity3.getTitle());
        Member member = new Member(StringConstants.USER_EMAIL, null, Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
        Community community = new Community((Entry) otherUserService.getCommunity(communityEntry3.getEditLinkResolvedHref().toString()));
        Entry memberEntry = (Entry) otherUserService.addMemberToCommunity(community, member);
        assertTrue(memberEntry != null);
        assertEquals(201, otherUserService.getRespStatus());        
        

        LOGGER.debug("Step 2: execute /communitynametypeahead and verify we see 3 communities");
        // we specify the unique timestamp in the name filter, and should see 3 of the 4 communities just created:
        // 1) public, 2) publicInviteOnly 3) Private but a member.   We will not see the community4 which is private and
        // not a member
        
        String baseUrl = service.getServiceURLString();
        ClientResponse response = service.doSearch(baseUrl + "/service/json/v1/community/communitynametypeahead?filterValue=" + timeStamp  + "CNTA");
        assertEquals(200, response.getStatus());

        String text = IOUtils.toString(response.getReader());

        // ensure response is valid JSON
        // and has 3 matching entries
        JSONObject obj = JSONObject.parse(text);
        assertTrue(JSONObject.isValidObject(obj));
        JSONArray items = (JSONArray) obj.get("entry");
        assertEquals("Expecting to see 3 communities whose name match '" + timeStamp + "CNTA' but instead we have " + items.size() + ".  Response is " + text,  3,  items.size());

        LOGGER.debug("Success, Ending test for CommunityNameTypeahead");
    }

    @Test
    public void testCreateCommunityWithNoWidgets() throws Exception {
        /*
         * Validate that if the request attribute addDefaultWidgets=false is present when creating a community, no
         * widgets outside those specified in the request payload are added to the Community.
         */
        LOGGER.debug("Beginning testCreateCommunityWithNoWidgets test");
        String timeStamp = Utils.logDateFormatter.format(new Date()) + "-noWidgets";

        Community testCommunityDefaultWidgets = new Community("Test createCommunity with default widgets " + timeStamp, "Find me", Permissions.PRIVATE, null, false);
        Community testCommunityNoWidgets = new Community("Test createCommunity with no default widgets " + timeStamp, "Find me", Permissions.PRIVATE, null, false);

        Entry communityEntryDefaultWidgets = (Entry) service.createCommunity(testCommunityDefaultWidgets);
        assertEquals("create testCommunityDefaultWidgets ", 201, service.getRespStatus());
        
        boolean addDefaultWidgets=false;
        Entry communityEntryNoWidgets = (Entry) service.createCommunity(testCommunityNoWidgets, addDefaultWidgets);
        assertEquals("create testCommunityNoWidgets ", 201, service.getRespStatus());

        Community com1 = new Community((Entry) service.getCommunity(communityEntryDefaultWidgets.getEditLinkResolvedHref().toString()));
        
        Feed widgetsFeed = (Feed) service.getCommunityWidgets(com1.getUuid());
        assertEquals("Error fetching widgets, status=" + service.getRespStatus(), 200, service.getRespStatus());
        int widgetCount = widgetsFeed.getEntries().size();
        assertTrue("Community should have more then zero widgets, widgets found: " + widgetCount + ", content=" + widgetsFeed.toString(), (widgetCount > 0));


        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Community created with default widgets:");
            for (Entry e : widgetsFeed.getEntries()) {
                LOGGER.debug("Widget Title=" + e.getTitle());
            }
        }

        Community com2 = new Community((Entry) service.getCommunity(communityEntryNoWidgets.getEditLinkResolvedHref().toString()));
        widgetsFeed = (Feed) service.getCommunityWidgets(com2.getUuid());
        assertEquals("Can't load Widgets, status=" + service.getRespStatus(), 200, service.getRespStatus());
        widgetCount = widgetsFeed.getEntries().size();
        assertTrue("Community should have zero widgets, widgets found: " + widgetCount+ ", content=" + widgetsFeed.toString(), (widgetCount == 0));    
        
        
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Community created with no default widgets:");
            for (Entry e : widgetsFeed.getEntries()) {
                LOGGER.debug("Widget Title=" + e.getTitle());
            }
        }
            
        LOGGER.debug("Success, Ending test for testCreateCommunityWithNoWidgets");
    }

    
    @Test
    public void testCorsPreflight_Atom() throws Exception {
    	
    	// Test requires CORS configured to trust 'fakedomain.org' - only true for on prem pool
    	if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.ON_PREMISE) {
    		return;
    	}
    	
    	String url = service.getServiceURLString() + URLConstants.COMMUNITIES_ORG;
    	
    	service.makeCorsPreflightRequestsVerifyResponses(url);
    }

    
    @Test
    public void testCorsPreflight_JSON() throws Exception {
    	
    	// Test requires CORS configured to trust 'fakedomain.org' - only true for on prem pool
    	if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.ON_PREMISE) {
    		return;
    	}
    	
    	String url = service.getServiceURLString() + "/service/json/v1/community_summaries";
    	
    	service.makeCorsPreflightRequestsVerifyResponses(url);
    }
    
    private boolean isMailUnsubscribeGKEnabled() {
		String gkSetting = gateKeeperService.getGateKeeperSetting(OnPremOrgId, "COMMUNITIES_MAIL_UNSUBSCRIBE");
		
		// Might be better to parse JSON but this should do the trick for this simple JSON response
		return gkSetting.contains("\"value\": true");
	}

	@Test
	public void testCommunityMailUnsubscribe() throws FileNotFoundException, IOException {
		/*
		 * Test calling MailSubscribe api to unsubscribe from mail for a valid member
		 * Test calling MailSubscribe api to subscribe from mail for a valid member
		 * Test calling MailSubscribe api to subscribe from mail for a valid member as MEMBER
		 * Test calling MailSubscribe api to subscribe from mail for a valid member as VISITOR
		 * Test calling MailSubscribe api to unsubscribe from mail for an invalid member 
		 */
		
		// ONLY called if COMMUNITIES_MAIL_UNSUBSCRIBE GK is enabled
		if (isMailUnsubscribeGKEnabled() == true) {
			
			LOGGER.debug("Beginning test: Community Mail Unsubscribe member");
			String dateCode = Utils.logDateFormatter.format(new Date());
		

			LOGGER.debug("Step 1: Create a community");
			Community testCommunity = null;
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD
				|| StringConstants.VMODEL_ENABLED) {
				testCommunity = new Community("TestMailUnSubscribeMember " + dateCode,
					"A community for testing mail subscribption",
					Permissions.PRIVATE, null, false);
			} else {
				testCommunity = new Community("TestMailUnSubscribeMember " + dateCode,
					"A community for testing mail subscribption",
					Permissions.PUBLIC, null);
			}
			Entry communityEntry = (Entry) service.createCommunity(testCommunity);
			assertEquals("Create Community failed"+service.getDetail(),
				201, service.getRespStatus());


			LOGGER.debug("Step 2: Add an internal member");
			Community testCommunityRetrieved = new Community(
				(Entry) assignedService.getCommunity(communityEntry
						.getEditLinkResolvedHref().toString()));
			CommunityMember member = new CommunityMember(otherUser.getEmail(), otherUser.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);

			Entry memberEntry = (Entry) service.addMemberToCommunity(
				testCommunityRetrieved, member);
			assertEquals(" Add Community Member ", 201, service.getRespStatus());

	
			LOGGER.debug("Step 3: Unsubscribe member from mail");
			String editLink = memberEntry.getEditLinkResolvedHref().toString();
			String editLinkURL = editLink.replace(
				"members?", "members/mailSubscribe?");

			CommunityMember editmember = new CommunityMember(null, otherUser.getUserId(),
					       Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
			editmember.setMailSubscription(MailSubscription.UNSUBSCRIBE);
			
			// call api to unsubscribe member from mail
			service.updateMemberMailSubscriptionInCommunity(editLinkURL, editmember);
			assertEquals("updateMemberMailSubscription should return HTTP 200.",
				          200, service.getRespStatus());
		
			LOGGER.debug("Step 4: subscribe member to mail");
			editmember.setMailSubscription(MailSubscription.SUBSCRIBE);
			
			// call api to subscribe member to mail
			service.updateMemberMailSubscriptionInCommunity(editLinkURL, editmember);
			assertEquals("updateMemberMailSubscription should return HTTP 200.",
				          200, service.getRespStatus());

			LOGGER.debug("Step 5: unsubscribe member to mail as MEMBER user");
			editmember.setMailSubscription(MailSubscription.UNSUBSCRIBE);
			// call api to unsubscribe member to mail
			otherUserService.updateMemberMailSubscriptionInCommunity(editLinkURL, editmember);
			assertEquals("updateMemberMailSubscription should return HTTP 200.",
				          200, otherUserService.getRespStatus());
			
			LOGGER.debug("Step 6: unsubscribe member to mail as VISITOR user");
			if (StringConstants.VMODEL_ENABLED) {
			  // call api to subscribe member to mail as a Visitor with NO access
			  visitorService.updateMemberMailSubscriptionInCommunity(editLinkURL, editmember);
			  assertEquals("updateMemberMailSubscription should return HTTP 403.",
				          403, visitorService.getRespStatus());
			}
			
			LOGGER.debug("Step 7: Remove the member and verify unsubscribe returns 404");
			assertTrue(assignedService.removeMemberFromCommunity(editLink));
		
			// call api to subscribe member to mail
			service.updateMemberMailSubscriptionInCommunity(editLinkURL, editmember);
			
			assertEquals("updateMemberMailSubscription should return HTTP 404.",
				          404, service.getRespStatus());
	
		    LOGGER.debug("Ending test: Community Mail Unsubscribe member");
		}
	}

}
