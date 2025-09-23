package com.ibm.lconn.automation.framework.services.communities.test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;


import java.awt.image.BufferedImage;
import java.io.File;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.StringConstants.CommunityBlogPermissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.IdeationStatus;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;

/**
 * Blog org admin api support test cases for Cisco org admin API requirement implementation, May 2018
 * 
 * These cases can run in both Cloud and On-Premise, but for on-premise environment, orgB validation step
 * will not execute.
 * 
 */

public class CommunityBlogsOrgAdminAPITests {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunityBlogsOrgAdminAPITests.class.getName());
	
	static Abdera abdera = new Abdera();
	
	protected static UserPerspective orgAUser; 
	
	protected static CommunitiesService orgAUserCommService;
	
	protected static String communityUuid = null;
	protected static String communityIdeationBlogHandle = null;
	protected static String communityBlogHandle = null;
	protected static String blogHomepageHandle = "homepage";
	
	protected static UserPerspective orgAUser2, orgAAdmin2; 
	protected static UserPerspective orgBAdmin2;
	
	protected static BlogsService orgAUser2BlogService, orgAAdmin2BlogService;
	protected static BlogsService orgBAdmin2BlogService;
	
	protected static UserPerspective orgCUser, orgCUser2, orgCAdmin2;
	protected static CommunitiesService orgCUserCommService;
	protected static BlogsService orgCUser2BlogService, orgCAdmin2BlogService;
	
	protected static boolean useSSL = true;
	
	protected static final String org_admin_gk1 = "CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES";
	protected static final String org_admin_gk2 = "CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES_EXTENDED";
	
	protected static boolean gk1_modified = false, gk2_modified = false;
	
	/**
	 * The convention for test environment settings is as following:
	 * 1. orgAUser is a normal user whose organization enabled CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES_EXTENDED and CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES
	 * 2. orgAUser is for using community service
	 * 3. orgAUser2 is the same user as orgAUser, but for using blog service
	 * 4. orgAAdmin2 is the org-admin role in orgA, for blog service
	 * 5. orgBAdmin2 is the org-admin role in orgB, orgB enabled CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES_EXTENDED and CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES
	 * 6. orgCUser and orgCUser2 is the same person for community service and blog service respectively, orgC disabled CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES
	 * 7. orgCAdmin2 is the org-admin role in orgC, for blog service
	 * @throws Exception
	 */
	
	@BeforeMethod
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Blogs OrgAdmin API Test Cases.");
		
		// i1 profile data convention: 
		// - 0 is orgA org-admin, 2 is orgA normal user
		// - 15 is orgB org-admin
		// - 16 is orgC org-admin, 17 is orgC normal user
		// orgA and orgB enabled org-admin gatekeepers, orgC is not.
		orgAUser = new UserPerspective(2, Component.COMMUNITIES.toString());
		orgAUserCommService = orgAUser.getCommunitiesService();
		
		orgAUser2 = new UserPerspective(2, Component.BLOGS.toString());
		orgAUser2BlogService = orgAUser2.getBlogsService();
		blogHomepageHandle = orgAUser2BlogService.getBlogsHomepageHandle();
		
		orgAAdmin2 = new UserPerspective(0, Component.BLOGS.toString());
		orgAAdmin2BlogService = orgAAdmin2.getBlogsService();
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("Target environment is Cloud, so will run scenarios that orgB admin CRUD blog artifacts, the negitive paths.");
			orgBAdmin2 = new UserPerspective(15, Component.BLOGS.toString()); // According to LC 195400, profile data with index=15 is the orgB admin user.
			orgBAdmin2BlogService = orgBAdmin2.getBlogsService();
			
			orgCUser = new UserPerspective(17, Component.COMMUNITIES.toString());
			orgCUserCommService = orgCUser.getCommunitiesService();
			
			orgCUser2 = new UserPerspective(17, Component.BLOGS.toString());
			orgCUser2BlogService = orgCUser2.getBlogsService();
			
			orgCAdmin2 = new UserPerspective(16, Component.BLOGS.toString());
			orgCAdmin2BlogService = orgCAdmin2.getBlogsService();
		}
		
		gk1_modified = checkAndEnableGatekeeper(org_admin_gk1);
		gk2_modified = checkAndEnableGatekeeper(org_admin_gk2);

		LOGGER.debug("Finished Initializing Blogs OrgAdmin API Test Cases.");
	}
	
	private static boolean checkAndEnableGatekeeper(String featureName)
			throws Exception {
		boolean isModified = false;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			try {
				//String json_isDefault_true = "\"isDefault\": true";
				//String json_isDefault_false = "\"isDefault\": false";
				String json_value_true = "\"value\": true";
				String json_value_false = "\"value\": false";

				String url = URLConstants.SERVER_URL
						+ "/connections/config/rest/gatekeeper/00000000-0000-0000-0000-000000000000/"
						+ featureName;
				String response = orgAAdmin2BlogService.getResponseString(url);
				
				//System.out.println("response1: " + response);
				// have meaningful content return.
				if (response.indexOf(featureName) != -1) {
					if (-1 == response.indexOf(json_value_true)) {
						// need to enable the feature
						String request = response;
						request = request.replace(json_value_false,
								json_value_true);
						//System.out.println("beforePost2: " + request);
						response = orgAAdmin2BlogService.postResponseString(url, request);
						//System.out.println("afterPost3: " + response);
						if (-1 != response.indexOf(json_value_true)) {
							isModified = true;
							LOGGER.debug("Feature " + featureName + " is updated to ON!");
						}
					} else {
						isModified = false;
						LOGGER.debug("Feature "+ featureName + " is already enabled before entering this testing.");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isModified;
	}
	
	private static boolean resetGatekeeper(String featureName)
			throws Exception {
		boolean doneReset = false;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.ON_PREMISE) {
			try {
				//String json_isDefault_true = "\"isDefault\": true";
				//String json_isDefault_false = "\"isDefault\": false";
				String json_value_true = "\"value\": true";
				String json_value_false = "\"value\": false";

				String url = URLConstants.SERVER_URL
						+ "/connections/config/rest/gatekeeper/00000000-0000-0000-0000-000000000000/"
						+ featureName;
				String response = orgAAdmin2BlogService.getResponseString(url);
				
				//System.out.println("response1: " + response);
				// have meaningful content return.
				if (response.indexOf(featureName) != -1) {
					if (-1 != response.indexOf(json_value_true)) {
						// need to reset the feature
						String request = response;
						request = request.replace(json_value_true,
								json_value_false);
						//System.out.println("beforePost2: " + request);
						response = orgAAdmin2BlogService.postResponseString(url, request);
						//System.out.println("afterPost3: " + response);
						if (-1 != response.indexOf(json_value_false)) {
							doneReset = true;
							LOGGER.debug("Feature " + featureName + " is reset to the original status after this testing.");
						}
					} else {
						doneReset = false;
						LOGGER.debug("Feature " + featureName + " has no need to reset.");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return doneReset;
	}
	
	

	
	/*
	 * Data population for org admin API requirement testings
	 * - Create a private community, keep communityUuid
	 * - Add Blog and Ideation Blog widget to the community
	 * 
	 * API covered:
	 * POST /communities/service/atom/communities/my
	 * POST /communities/service/atom/community/widgets?communityUuid=<commUuid>
	 */
	@Test(priority = 0)
	public void prepareAPrivateCommunityWithBlogsWidgetsAdded() throws Exception {
		
		LOGGER.debug("BEGIN TEST: prepareAPrivateCommunityWithBlogsWidgetsAdded()");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

		// Create private community
		String privateCommTitle = "Test community created by " + orgAUser.getUserName() + " randomNameAddition "
				+ uniqueNameAddition;
		Community newCommunity = new Community(
				privateCommTitle,
				"Private community with blog widget added.",
				Permissions.PRIVATE, null);

		Entry communityResult = (Entry) orgAUserCommService.createCommunity(newCommunity);
		assertTrue(communityResult != null);
		LOGGER.debug("Private community '" + privateCommTitle + "' created successfully by user '" + orgAUser.getUserName() + "'.");
		String responseLocation = orgAUserCommService.getRespLocation();
		LOGGER.debug("postFeed response location=" + responseLocation);

		Community comm = new Community( (Entry)orgAUserCommService
				.getCommunity(communityResult.getEditLinkResolvedHref().toString())
				);
		// communityUuid is the community blog's handle by default,
		// So save it for composing community blog API URLs. But community ideation blog has different handle.
		communityUuid = comm.getUuid();

		LOGGER.debug("Private community '" + communityUuid + "' created at " + comm.getPublished().toString());
		LOGGER.debug("Private community '" + communityUuid + "' updated at " + comm.getUpdated().toString());
		
		// Add ideation blog widget
		Widget widget = new Widget(StringConstants.WidgetID.IdeationBlog.toString());
		Entry ideationBlog = (Entry)orgAUserCommService.postWidget(comm, widget.toEntry());
		assertEquals(orgAUserCommService.getRespStatus(), 201, "postWidget"+orgAUserCommService.getDetail());
		
		// community ideation blog use widget instance ID as the blog handle by default.
		if (ideationBlog != null) {
			communityIdeationBlogHandle = ideationBlog.getSimpleExtension(StringConstants.SNX_WIDGET_INSTANCID);
		}
		LOGGER.debug(ideationBlog!= null ? ideationBlog.toString() : "IdeationBlog widget fails to add!");
		LOGGER.debug("IdeationBlog added to private community '" + communityUuid + "', widget addition postFeed response location=" + orgAUserCommService.getRespLocation());

		// Add blogs widget
		Entry communityBlog = (Entry) orgAUserCommService.postWidget(comm, widget.toEntry());
		Feed widgetsInitialFeed = (Feed) orgAUserCommService.getCommunityWidgets(comm.getUuid());
		boolean blogFound = false;		
		for (Entry e : widgetsInitialFeed.getEntries()) {
			if (e.getTitle().equals("Blog")) {
				blogFound = true;
				break;
			}
		}
		if (blogFound == false) {
			widget = new Widget(StringConstants.WidgetID.Blog.toString());
			communityBlog = (Entry) orgAUserCommService.postWidget(comm, widget.toEntry());
			assertEquals(orgAUserCommService.getRespStatus(), 201, "postWidget"+orgAUserCommService.getDetail());
		}
		
		// communityUuid is the community blog's handle by default.
		if (communityBlog != null) {
			communityBlogHandle = communityUuid;
		}
		LOGGER.debug(communityBlog != null ? communityBlog.toString() : "CommunityBlog widget fails to add!");
		LOGGER.debug("CommunityBlog added to private community '" + communityUuid + "', widget addition postFeed response location=" + orgAUserCommService.getRespLocation());

		Community commAfterAddBlogWidget = new Community(
				(Entry) orgAUserCommService.getCommunity(communityResult
						.getEditLinkResolvedHref().toString())
				);
		LOGGER.debug("Private community '" + communityUuid + "' updated at " + commAfterAddBlogWidget.getUpdated().toString());
		
	}
	
	/**
	 * Test scenario: orgA admin can CRUD blog entries in orgA, orgB admin cannot CRUD blog entries in orgA.
	 * 
	 * Process:
	 * 1.1 Call prepare community method to prepare a private community with blog widgets added
	 * 1.2 OrgA user create a blog entry, verify if orgA admin can get the blog entry, and verify if orgB admin can not
	 * 1.2.1 Verify if orgA admin can get the blog entries feed via FeedServlet URL, but orgB admin can not
	 * 1.3 Verify if orgA admin can modify the blog entry, and orgB admin can not
	 * 1.4 Verify if orgB admin can not delete the blog entry, and orgA admin can
	 * 1.5 Verify if orgA admin create a blog entry, but orgB admin can not
	 * 
	 * API URL pattern covered in this test:
	 * - POST /blogs/<communityBlogHandle>/api/entries 
	 * - GET  /blogs/<communityBlogHandle>/api/entries/<entryId>
	 * - GET  /blogs/<communityBlogHandle>/feed/entries/atom
	 * - PUT  /blogs/<communityBlogHandle>/api/entries/<entryId>
	 * - DELETE /blogs/<communityBlogHandle>/api/entries/<entryId>
	 */
	//Added priority = 2 to the test case so that all dependent test cases run at the end and have some extra time after prepareAPrivateCommunityWithBlogsWidgetsAdded is run
	@Test(dependsOnMethods = { "prepareAPrivateCommunityWithBlogsWidgetsAdded" }, priority = 2) // step 1.1
	public void testCRUDBlogEntriesWithOrgAdmin() throws Exception {
		
		boolean doAssert = true;
		
		// for community blog
		String entryPostURL = orgAUser2BlogService.getServiceURLString() + 
				"/" + communityBlogHandle + "/api/entries";
		
		Factory factory = abdera.getFactory();
		Entry entry = factory.newEntry();
		String blogEntryString = "Community " + communityUuid + " blog entry1";
		entry.setTitle("Title of " + blogEntryString);
		entry.setSummary("Summary of " + blogEntryString);
		entry.setContent("Content of " + blogEntryString);
		
		// step 1.2 start
		Entry entryPostedResult = (Entry) orgAUser2BlogService.postBlogsFeed(entryPostURL, entry);
		if (entryPostedResult != null) {
			LOGGER.debug(entryPostedResult.toString());
		}
		
		String postedBlogEntryItself = entryPostedResult.getEditLinkResolvedHref().toString();
		Entry orgAAdminGotEntry = (Entry) orgAAdmin2BlogService.getPost(postedBlogEntryItself);
		if (doAssert) {
			assertEquals(orgAAdmin2BlogService.getRespStatus(), 200,
					"orgA admin got the blog entry feed created by " + orgAUser2.getUserName());
		}
		int respStatus = 0;
		ExtensibleElement orgBAdminGotEntry = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgBAdminGotEntry = orgBAdmin2BlogService.getPost(postedBlogEntryItself);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
						"orgB admin can not get the blog entry feed created by orgA user" + orgAUser2.getUserName());
			}
		}
		
		// step 1.2.1
		String blogEntriesFeedURL = orgAUser2BlogService.getServiceURLString() + "/" + communityBlogHandle + "/feed/entries/atom";
		Feed orgAAdminGotFeed = (Feed) orgAAdmin2BlogService.getBlogFeedWithRedirect(blogEntriesFeedURL);
		boolean found = false;
		for (Entry blogentry : orgAAdminGotFeed.getEntries()) {
			if( blogentry.getTitle().contains(blogEntryString) ) {
				found = true;
				break;
			}
		}
		if (doAssert) {
			assertTrue(found);
		}
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgBAdmin2BlogService.getBlogFeedWithRedirect(blogEntriesFeedURL);
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 403,
						"orgB admin can not get orgA's community blog entries feed.");
			}
		}
		
		// step 1.3 
		BlogPost editEntry = new BlogPost("Admin modified title of " + blogEntryString, 
				"Admin modified content of " + blogEntryString, "AdminAddedTag", true, 0);
		Entry orgAAdminModifiedEntry = (Entry) orgAAdmin2BlogService.editPost(postedBlogEntryItself, editEntry);
		respStatus = orgAAdmin2BlogService.getRespStatus();
		// though orgA admin can post/put blog entry, the posted entry's visibility is 2, means AUTHOR_LIMITED, 
		// means the community owner need to approve it before the blog entry can be seen.
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 200, "orgA admin modifies blog entry successfully!");
		
		boolean isDeleted = false;
		Entry orgBAdminModifiedEntry = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			editEntry = new BlogPost("orgB Admin modified title of " + blogEntryString,
					"orgB Admin modified content of " + blogEntryString, "OrgBAdminAddedTag", true, 0);
			orgBAdminModifiedEntry = (Entry) orgBAdmin2BlogService.editPost(postedBlogEntryItself, editEntry);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401, "orgB admin can not edit orgA blog entry!");

			// step 1.4
			isDeleted = orgBAdmin2BlogService.deletePost(postedBlogEntryItself);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401, "orgB admin can not delete orgA blog entry!");
		}
		
		isDeleted = orgAAdmin2BlogService.deletePost(postedBlogEntryItself);
		respStatus = orgAAdmin2BlogService.getRespStatus();
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 204, "orgA admin deleted the blog entry!");
		
		// step 1.5
		entry.setTitle("orgA Admin Title of " + blogEntryString);
		entry.setSummary("orgA Admin Summary of " + blogEntryString);
		entry.setContent("orgA Admin Content of " + blogEntryString);
		entryPostedResult = (Entry) orgAAdmin2BlogService.postBlogsFeed(entryPostURL, entry);
		if (entryPostedResult != null) {
			LOGGER.debug(entryPostedResult.toString());
		}
		respStatus = orgAAdmin2BlogService.getRespStatus();
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 201, "orgA admin can post blog entry!");
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			entry.setTitle("orgB Admin Title of " + blogEntryString);
			entry.setSummary("orgB Admin Summary of " + blogEntryString);
			entry.setContent("orgB Admin Content of " + blogEntryString);
			entryPostedResult = (Entry) orgBAdmin2BlogService.postBlogsFeed(entryPostURL, entry);
			if (entryPostedResult != null) {
				LOGGER.debug(entryPostedResult.toString());
			}
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401, "orgB admin can not post blog entry in orgA!");
		}
		// community blog test end
		
		// community ideation blog start
		entryPostURL = orgAUser2BlogService.getServiceURLString() + 
				"/" + communityIdeationBlogHandle + "/api/entries";
		
		entry = factory.newEntry();
		blogEntryString = "Community " + communityUuid + " ideation blog entry1";
		entry.setTitle("Title of " + blogEntryString);
		entry.setSummary("Summary of " + blogEntryString);
		entry.setContent("Content of " + blogEntryString);
		
		// step 1.2 start
		entryPostedResult = (Entry) orgAUser2BlogService.postBlogsFeed(entryPostURL, entry);
		if (entryPostedResult != null) {
			LOGGER.debug(entryPostedResult.toString());
		}
		
		postedBlogEntryItself = entryPostedResult.getEditLinkResolvedHref().toString();
		orgAAdminGotEntry = (Entry) orgAAdmin2BlogService.getPost(postedBlogEntryItself);
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 200, "orgA admin got the ideation blog entry feed created by " + orgAUser2.getUserName());
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgBAdminGotEntry = orgBAdmin2BlogService.getPost(postedBlogEntryItself);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
					"orgB admin can not get the ideation blog entry feed created by orgA user"
							+ orgAUser2.getUserName());
		}
		
		// step 1.2.1
		String ideaEntriesFeedURL = orgAUser2BlogService.getServiceURLString() + "/" + communityIdeationBlogHandle
				+ "/feed/entries/atom";
		orgAAdminGotFeed = (Feed) orgAAdmin2BlogService.getBlogFeedWithRedirect(ideaEntriesFeedURL);
		found = false;
		for (Entry blogentry : orgAAdminGotFeed.getEntries()) {
			if (blogentry.getTitle().contains(blogEntryString)) {
				found = true;
				break;
			}
		}
		if (doAssert) {
			assertTrue(found);
		}

		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgBAdmin2BlogService.getBlogFeedWithRedirect(ideaEntriesFeedURL);
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 403,
						"orgB admin can not get orgA's community ideation blog entries feed.");
			}
		}
		
		// step 1.3 
		editEntry = new BlogPost("Admin modified title of " + blogEntryString, 
				"Admin modified content of " + blogEntryString, "AdminAddedTag", true, 0);
		orgAAdminModifiedEntry = (Entry) orgAAdmin2BlogService.editPost(postedBlogEntryItself, editEntry);
		respStatus = orgAAdmin2BlogService.getRespStatus();
		// though orgA admin can post/put blog entry, the posted entry's visibility is 2, means AUTHOR_LIMITED, 
		// means the community owner need to approve it before the blog entry can be seen.
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 200, "orgA admin modifies ideation blog entry successfully!");
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			editEntry = new BlogPost("orgB Admin modified title of " + blogEntryString,
					"orgB Admin modified content of " + blogEntryString, "OrgBAdminAddedTag", true, 0);
			orgBAdminModifiedEntry = (Entry) orgBAdmin2BlogService.editPost(postedBlogEntryItself, editEntry);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
					"orgB admin can not edit orgA ideation blog entry!");

			// step 1.4
			isDeleted = orgBAdmin2BlogService.deletePost(postedBlogEntryItself);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
					"orgB admin can not delete orgA ideation blog entry!");
		}
		
		isDeleted = orgAAdmin2BlogService.deletePost(postedBlogEntryItself);
		respStatus = orgAAdmin2BlogService.getRespStatus();
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 204, "orgA admin deleted the ideation blog entry!");
		
		// step 1.5
		entry.setTitle("orgA Admin Title of " + blogEntryString);
		entry.setSummary("orgA Admin Summary of " + blogEntryString);
		entry.setContent("orgA Admin Content of " + blogEntryString);
		entryPostedResult = (Entry) orgAAdmin2BlogService.postBlogsFeed(entryPostURL, entry);
		if (entryPostedResult != null) {
			LOGGER.debug(entryPostedResult.toString());
		}
		respStatus = orgAAdmin2BlogService.getRespStatus();
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 201, "orgA admin can post an ideation blog entry!");
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			entry.setTitle("orgB Admin Title of " + blogEntryString);
			entry.setSummary("orgB Admin Summary of " + blogEntryString);
			entry.setContent("orgB Admin Content of " + blogEntryString);
			entryPostedResult = (Entry) orgBAdmin2BlogService.postBlogsFeed(entryPostURL, entry);
			if (entryPostedResult != null) {
				LOGGER.debug(entryPostedResult.toString());
			}
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
					"orgB admin can not post ideation blog entry in orgA!");
		}
	}
	
	/**
	 * Test scenario: orgA admin can CRUD blog comments in orgA, orgB admin cannot do the same in orgA.
	 * 
	 * Process:
	 * 1.1 Call prepare community method to prepare a private community with blog widgets added
	 * 1.2 OrgA user create a blog entry and rely a comment to that entry, verify if orgA admin can get the comment entry, but orgB admin can not
	 * 1.2.1 Verify if orgA admin can get the comment feed via FeedServlet path
	 * 1.3 Verify if orgA admin can modify the blog comment, and orgB admin can not
	 * 1.4 Verify if orgB admin can not delete the blog comment, and orgA admin can
	 * 1.5 Verify if orgA admin create a blog comment, but orgB admin can not
	 * 1.6 Repeat for ideation blog comment CRUD
	 * 
	 * API URL pattern covered in this test:
	 * - POST /blogs/<communityBlogHandle>/api/comments
	 * - GET  /blogs/<communityBlogHandle>/api/comments/<commentEntryId>
	 * - GET  /blogs/<communityBlogHandle>/feed/comments/atom
	 * - GET  /blogs/<communityBlogHandle>/feed/entrycomments/<slug>/atom
	 * - PUT  /blogs/<communityBlogHandle>/api/comments/<commentEntryId>
	 * - DELETE /blogs/<communityBlogHandle>/api/comments/<commentEntryId>
	 */
	//Added priority = 2 to the test case so that all dependent test cases run at the end and have some extra time after prepareAPrivateCommunityWithBlogsWidgetsAdded is run
	@Test(dependsOnMethods = { "prepareAPrivateCommunityWithBlogsWidgetsAdded" }, priority = 2, enabled = false) // step 1.1
	public void testCRUDBlogCommentsWithOrgAdmin() throws Exception {
		boolean doAssert = true;
		
		// for community blog
		// step 1.2
		String entryPostURL = orgAUser2BlogService.getServiceURLString() + 
				"/" + communityBlogHandle + "/api/entries";
		
		Factory factory = abdera.getFactory();
		Entry entry = factory.newEntry();
		String blogEntryString = "Community " + communityUuid + " blog entry1";
		entry.setTitle("Title of " + blogEntryString);
		entry.setSummary("Summary of " + blogEntryString);
		entry.setContent("Content of " + blogEntryString);
		
		Entry entryPostedResult = (Entry) orgAUser2BlogService.postBlogsFeed(entryPostURL, entry);
		// /blogs/<bloghandle>/feed/entrycomments/<slug>/atom?lang=en_us
		String entryRepliesURL = entryPostedResult.getLinkResolvedHref("replies").toString();
		
		
		BlogPost createdEntry = new BlogPost(entryPostedResult);
		
		
		entry = factory.newEntry();
		String commentEntryString = "Community " + communityUuid + " blog comment1";
		entry.setContent("Content of " + commentEntryString);
		BlogComment aComment = new BlogComment(entry);
		
		
		Entry commentPostedResult = (Entry) orgAUser2BlogService.createComment(createdEntry, aComment);
		if (commentPostedResult != null) {
			LOGGER.debug(commentPostedResult.toString());
		}
		
		String postedCommentItself = commentPostedResult.getEditLinkResolvedHref().toString();
		String respLocation = orgAUser2BlogService.getRespLocation();
		Entry orgAAdminGotEntry = (Entry) orgAAdmin2BlogService.getComment(respLocation);
		if (doAssert) {
			assertEquals(orgAAdmin2BlogService.getRespStatus(), 200,
					"orgA admin got the blog entry feed created by " + orgAUser2.getUserName());
		}
		int respStatus = 0;
		ExtensibleElement orgBAdminGotComment = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgBAdminGotComment = orgBAdmin2BlogService.getComment(respLocation);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
						"orgB admin can not get the blog comment feed created by orgA user" + orgAUser2.getUserName());
			}
		}
		
		// step 1.2.1
		String commentsFeedURL = orgAUser2BlogService.getServiceURLString() + 
				"/" + communityBlogHandle + "/feed/comments/atom";
		Feed orgAAdminGotFeed = (Feed) orgAAdmin2BlogService.getBlogFeedWithRedirect(commentsFeedURL);
		boolean found = false;
		for (Entry commententry : orgAAdminGotFeed.getEntries()) {
			if( commententry.getContent().contains(commentEntryString) ) {
				found = true;
				break;
			}
		}
		if (doAssert) {
			assertTrue(found);
		}
		
		orgAAdminGotFeed = (Feed) orgAAdmin2BlogService.getBlogFeedWithRedirect(entryRepliesURL);
		found = false;
		for (Entry commententry : orgAAdminGotFeed.getEntries()) {
			if( commententry.getContent().contains(commentEntryString) ) {
				found = true;
				break;
			}
		}
		if (doAssert) {
			assertTrue(found);
		}
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgBAdmin2BlogService.getBlogFeedWithRedirect(commentsFeedURL);
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 403,
						"orgB admin can not get orgA's community blog comments feed.");
			}
			orgBAdmin2BlogService.getBlogFeedWithRedirect(entryRepliesURL);
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 403,
						"orgB admin can not get orgA's community blog comments feed.");
			}
		}
		
		// step 1.3 
		entry.setContent("Admin modified title of " + commentEntryString);
		Entry orgAAdminModifiedEntry = (Entry) orgAAdmin2BlogService.putEntry(postedCommentItself, entry); // use postedCommentItself to validate the fix in LC 195616
		respStatus = orgAAdmin2BlogService.getRespStatus();
		// though orgA admin can post/put blog entry, the posted entry's visibility is 2, means AUTHOR_LIMITED, 
		// means the community owner need to approve it before the blog entry can be seen.
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 200, "orgA admin modifies blog comment successfully!");
		
		boolean isDeleted = false;
		Entry orgBAdminModifiedEntry = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			entry.setContent("orgB Admin modified title of " + commentEntryString);
			orgBAdminModifiedEntry = (Entry) orgBAdmin2BlogService.putEntry(respLocation, entry);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401, "orgB admin can not edit orgA blog entry!");

			// step 1.4
			isDeleted = orgBAdmin2BlogService.deletePost(respLocation);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401, "orgB admin can not delete orgA blog comment!");
		}
		
		isDeleted = orgAAdmin2BlogService.deletePost(respLocation);
		respStatus = orgAAdmin2BlogService.getRespStatus();
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 204, "orgA admin deleted the blog comment!");
		
		// step 1.5
		entry = factory.newEntry();
		blogEntryString = "Community " + communityUuid + " blog entry2";
		entry.setTitle("Title of " + blogEntryString);
		entry.setSummary("Summary of " + blogEntryString);
		entry.setContent("Content of " + blogEntryString);
		
		Entry adminPostedEntryResult = (Entry) orgAAdmin2BlogService.postBlogsFeed(entryPostURL, entry);
		String adminPostedEntryId = adminPostedEntryResult.getId().toString();
		String adminPostedEntryAlternate = adminPostedEntryResult.getAlternateLink().getAttributeValue("href");
		
		entry = factory.newEntry();
		Element replyToExt = entry.addExtension(new QName("http://purl.org/syndication/thread/1.0", "in-reply-to", "thr"));
		replyToExt.setAttributeValue("ref", adminPostedEntryId);
		replyToExt.setAttributeValue("type", "text/html");
		replyToExt.setAttributeValue("href", adminPostedEntryAlternate);
		entry.setContent("orgA Admin Content of " + commentEntryString);
		
		String commentPostURL = orgAUser2BlogService.getServiceURLString() + 
				"/" + communityBlogHandle + "/api/comments";
		
		commentPostedResult = (Entry) orgAAdmin2BlogService.postBlogsFeed(commentPostURL, entry); // use direct way to create comment
		if (commentPostedResult != null) {
			LOGGER.debug(commentPostedResult.toString());
		}
		respStatus = orgAAdmin2BlogService.getRespStatus();
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 201, "orgA admin can post blog comment!");
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			entry.setContent("orgB Admin Content of " + commentEntryString);
			commentPostedResult = (Entry) orgBAdmin2BlogService.postBlogsFeed(commentPostURL, entry);
			if (commentPostedResult != null) {
				LOGGER.debug(commentPostedResult.toString());
			}
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401, "orgB admin can not post blog comment in orgA!");
		}
		// community blog test end
		
		// community ideation blog start
		
		// step 1.2
		entryPostURL = orgAUser2BlogService.getServiceURLString() + 
				"/" + communityIdeationBlogHandle + "/api/entries";
		
		entry = factory.newEntry();
		blogEntryString = "Community " + communityUuid + " ideation blog entry1";
		entry.setTitle("Title of " + blogEntryString);
		entry.setSummary("Summary of " + blogEntryString);
		entry.setContent("Content of " + blogEntryString);
		
		entryPostedResult = (Entry) orgAUser2BlogService.postBlogsFeed(entryPostURL, entry);
		entryRepliesURL = entryPostedResult.getLinkResolvedHref("replies").toString();
		
		if (entryPostedResult != null) {
			LOGGER.debug(entryPostedResult.toString());
		}
		
		createdEntry = new BlogPost(entryPostedResult);
		
		
		entry = factory.newEntry();
		commentEntryString = "Community " + communityUuid + " ideation blog comment1";
		entry.setContent("Content of " + commentEntryString);
		aComment = new BlogComment(entry);
		
		commentPostedResult = (Entry) orgAUser2BlogService.createComment(createdEntry, aComment);
		if (commentPostedResult != null) {
			LOGGER.debug(commentPostedResult.toString());
		}
		
		postedCommentItself = commentPostedResult.getEditLinkResolvedHref().toString();
		respLocation = orgAUser2BlogService.getRespLocation();
		orgAAdminGotEntry = (Entry) orgAAdmin2BlogService.getComment(respLocation);
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 200, "orgA admin got the ideation blog comment feed created by " + orgAUser2.getUserName());
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgBAdminGotComment = orgBAdmin2BlogService.getComment(respLocation);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
					"orgB admin can not get the ideation blog comment feed created by orgA user"
							+ orgAUser2.getUserName());
		}
		
		// step 1.2.1
		commentsFeedURL = orgAUser2BlogService.getServiceURLString() + "/" + communityIdeationBlogHandle
				+ "/feed/comments/atom";
		orgAAdminGotFeed = (Feed) orgAAdmin2BlogService.getBlogFeedWithRedirect(commentsFeedURL);
		found = false;
		for (Entry commententry : orgAAdminGotFeed.getEntries()) {
			if (commententry.getContent().contains(commentEntryString)) {
				found = true;
				break;
			}
		}
		if (doAssert) {
			assertTrue(found);
		}
		
		orgAAdminGotFeed = (Feed) orgAAdmin2BlogService.getBlogFeedWithRedirect(entryRepliesURL);
		found = false;
		for (Entry commententry : orgAAdminGotFeed.getEntries()) {
			if (commententry.getContent().contains(commentEntryString)) {
				found = true;
				break;
			}
		}
		if (doAssert) {
			assertTrue(found);
		}
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgBAdmin2BlogService.getBlogFeedWithRedirect(commentsFeedURL);
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 403,
						"orgB admin can not get orgA's community ideation blog comments feed.");
			}
			orgBAdmin2BlogService.getBlogFeedWithRedirect(entryRepliesURL);
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 403,
						"orgB admin can not get orgA's community ideation blog comments feed.");
			}
		}
		
		// step 1.3 
		entry.setContent("Admin modified content of " + commentEntryString);
		orgAAdminModifiedEntry = (Entry) orgAAdmin2BlogService.putEntry(respLocation, entry);
		respStatus = orgAAdmin2BlogService.getRespStatus();
		// though orgA admin can post/put blog entry, the posted entry's visibility is 2, means AUTHOR_LIMITED, 
		// means the community owner need to approve it before the blog entry can be seen.
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 200, "orgA admin modifies ideation blog comment successfully!");
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			entry.setContent("orgB admin modified content of " + commentEntryString);
			orgBAdminModifiedEntry = (Entry) orgBAdmin2BlogService.putEntry(respLocation, entry);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
					"orgB admin can not edit orgA ideation blog comment!");

			// step 1.4
			isDeleted = orgBAdmin2BlogService.deletePost(respLocation);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
					"orgB admin can not delete orgA ideation blog comment!");
		}
		
		isDeleted = orgAAdmin2BlogService.deletePost(respLocation);
		respStatus = orgAAdmin2BlogService.getRespStatus();
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 204, "orgA admin deleted the ideation blog comment!");
		
		// step 1.5
		entry = factory.newEntry();
		blogEntryString = "Community " + communityUuid + " ideation blog entry2";
		entry.setTitle("Title of " + blogEntryString);
		entry.setSummary("Summary of " + blogEntryString);
		entry.setContent("AdminPosted Content of " + blogEntryString);
		
		adminPostedEntryResult = (Entry) orgAAdmin2BlogService.postBlogsFeed(entryPostURL, entry);
		adminPostedEntryId = adminPostedEntryResult.getId().toString();
		adminPostedEntryAlternate = adminPostedEntryResult.getAlternateLink().getAttributeValue("href");
		
		entry = factory.newEntry();
		replyToExt = entry.addExtension(new QName("http://purl.org/syndication/thread/1.0", "in-reply-to", "thr"));
		replyToExt.setAttributeValue("ref", adminPostedEntryId);
		replyToExt.setAttributeValue("type", "text/html");
		replyToExt.setAttributeValue("href", adminPostedEntryAlternate);
		entry.setContent("orgA Admin Content of " + commentEntryString);
		
		commentPostURL = orgAUser2BlogService.getServiceURLString() + 
				"/" + communityIdeationBlogHandle + "/api/comments";
		
		commentPostedResult = (Entry) orgAAdmin2BlogService.postBlogsFeed(commentPostURL, entry);
		if (commentPostedResult != null) {
			LOGGER.debug(commentPostedResult.toString());
		}
		respStatus = orgAAdmin2BlogService.getRespStatus();
		assertEquals(orgAAdmin2BlogService.getRespStatus(), 201, "orgA admin can post an ideation blog comment!");
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			entry.setContent("orgB Admin Content of " + commentEntryString);
			commentPostedResult = (Entry) orgBAdmin2BlogService.postBlogsFeed(commentPostURL, entry);
			if (commentPostedResult != null) {
				LOGGER.debug(commentPostedResult.toString());
			}
			respStatus = orgBAdmin2BlogService.getRespStatus();
			assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
					"orgB admin can not post ideation blog comment in orgA!");
		}

    }
	
	/**
	 * Test scenario: orgA admin can CRD blog media in orgA, orgB admin cannot do the same in orgA.
	 * 
	 * Process:
	 * 1.1 Call prepare community method to prepare a private community with blog widgets added
	 * 1.2 OrgA user add a picture as media to that blog
	 * 1.3 Verify if orgA admin can get the blog media, and orgB admin can not
	 * 1.3.1 Verify if orgA admin can get the blog media feed via FeedServlet, but orgB admin can not
	 * 1.4 Verify if orgB admin can not delete the blog media, and orgA admin can
	 * 1.5 Verify if orgA admin create a blog media, but orgB admin can not
	 * 
	 * API URL pattern covered in this test:
	 * - POST /blogs/<communityBlogHandle>/api/media
	 * - GET  /blogs/<communityBlogHandle>/api/media/<filename>
	 * - GET  /blogs/<communityBlogHandle>/feed/media/atom
	 * - DELETE /blogs/<communityBlogHandle>/api/media/<filename>
	 */
	//Added priority = 2 to the test case so that all dependent test cases run at the end and have some extra time after prepareAPrivateCommunityWithBlogsWidgetsAdded is run
	@Test(dependsOnMethods = { "prepareAPrivateCommunityWithBlogsWidgetsAdded" }, priority = 2 , enabled = false) // step 1.1
	public void testCRDBlogMediasWithOrgAdmin() throws Exception {
		
		boolean doAssert = true; // for debug purpose only
		
		String mediaPostURL = orgAUser2BlogService.getServiceURLString() + 
				"/" + communityBlogHandle + "/api/media";
		
		BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream(
                "/resources/lamborghini_murcielago_lp640.jpg"));
        File file = File.createTempFile("lambo", ".jpg");
        ImageIO.write(image, "jpg", file);
        
        Entry postedMediaEntry = null;
        int respStatus = 0;
        String postedMediaEditURL = null;
        String respLocation = null;
        String mediaTitle = null;
        
        try {
        	postedMediaEntry = (Entry) orgAUser2BlogService.postFile(mediaPostURL, file); // step 1.2
        	mediaTitle = postedMediaEntry.getTitle();
        	respStatus = orgAUser2BlogService.getRespStatus();
        	postedMediaEditURL = postedMediaEntry.getEditLinkResolvedHref().toString();
        	respLocation = orgAUser2BlogService.getRespLocation();
        	if (doAssert) {
        		assertEquals(respStatus, 201, "Post file failed " + orgAUser2BlogService.getDetail()); 
        	}
        } catch (Exception e) {
            e.printStackTrace();
            if (doAssert) {
            	assertTrue(false);
            }
        }
        
        // step 1.3
        Entry orgAdminGotMediaFeed = (Entry) orgAAdmin2BlogService.getBlogsFeed(respLocation); 
        respStatus = orgAAdmin2BlogService.getRespStatus();
        if (doAssert) {
        	assertEquals(respStatus, 200, "orgA admin got media feed successfully!");
        }
        
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgAdminGotMediaFeed = (Entry) orgBAdmin2BlogService.getBlogsFeed(respLocation);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			if (doAssert) {
				assertEquals(respStatus, 403, "orgB admin can not get orgA media feed.");
			}
		}
		
		// step 1.3.1 
		String mediaFeedURL = orgAUser2BlogService.getServiceURLString() + 
				"/" + communityBlogHandle + "/feed/media/atom";
		Feed mediaFeedGot = (Feed) orgAAdmin2BlogService.getBlogFeedWithRedirect(mediaFeedURL);
		boolean found = false;
		for (Entry mediaEntry : mediaFeedGot.getEntries()) {
			if( mediaEntry.getTitle().equalsIgnoreCase(mediaTitle) ) {
				found = true;
				break;
			}
		}
		if (doAssert) {
			assertTrue(found);
		}
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgBAdmin2BlogService.getBlogFeedWithRedirect(mediaFeedURL);
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 403,
						"orgB admin can not get orgA's community blog comments feed.");
			}
		}
        // Blog media resource has no way to modify, once uploaded, can do only GET and DELETE.
        
        /*
        BufferedImage newImage = ImageIO.read(this.getClass().getResourceAsStream("/resources/Jellyfish.jpg"));
        File newFile = File.createTempFile("jellyfish", ".jpg");
        ImageIO.write(newImage, "jpg", newFile);
        
        Entry modifiedMediaEntry = (Entry) orgAAdmin2BlogService.putFile(postedMediaEditURL, newFile);
        respStatus = orgAAdmin2BlogService.getRespStatus();
        String modifiedMediaEditURL = modifiedMediaEntry.getEditLinkResolvedHref().toString();
        respLocation = orgAAdmin2BlogService.getRespLocation();
        if (doAssert) {
        	assertEquals(respStatus, 201, "orgA admin modified the media.");
        }
        
        modifiedMediaEntry = (Entry) orgBAdmin2BlogService.putFile(postedMediaEditURL, newFile);
        respStatus = orgAAdmin2BlogService.getRespStatus();
        if (doAssert) {
        	assertEquals(respStatus, 401, "orgB admin can not modify the media in orgA.");
        }
        */
        
        // step 1.4
		boolean isDeleted;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			isDeleted = orgBAdmin2BlogService.deletePost(postedMediaEditURL);
			respStatus = orgBAdmin2BlogService.getRespStatus();
			if (doAssert) {
				assertEquals(respStatus, 401, "orgB admin can not delete orgA media resource!");
			}
		}
		
		isDeleted = orgAAdmin2BlogService.deletePost(postedMediaEditURL);
		respStatus = orgAAdmin2BlogService.getRespStatus();
		if (doAssert) {
			assertEquals(respStatus, 204, "orgA admin deleted the blog media resource!");
		}
		
		// step 1.5
		try {
        	postedMediaEntry = (Entry) orgAAdmin2BlogService.postFile(mediaPostURL, file); 
        	respStatus = orgAAdmin2BlogService.getRespStatus();
        	postedMediaEditURL = postedMediaEntry.getEditLinkResolvedHref().toString();
        	respLocation = orgAAdmin2BlogService.getRespLocation();
        	if (doAssert) {
        		assertEquals(respStatus, 201, "Post file failed " + orgAAdmin2BlogService.getDetail()); 
        	}
        } catch (Exception e) {
            e.printStackTrace();
            if (doAssert) {
            	assertTrue(false);
            }
        }
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			try {
				postedMediaEntry = (Entry) orgBAdmin2BlogService.postFile(mediaPostURL, file);
				respStatus = orgBAdmin2BlogService.getRespStatus();
				if (doAssert) {
					assertEquals(respStatus, 401, "Post file failed " + orgBAdmin2BlogService.getDetail());
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (doAssert) {
					assertTrue(false);
				}
			}
		}
		
	}
	
	/*
	 * Blog container APIs do not fall into Cisco org-admin API requirement scope, because 
	 * there is no meaning for org-admin to operate on Blog container artifact. Org admin 
	 * cares about blog content artifacts - blog entry, blog comment, blog media.
	 * 
	 * However, for testing coverage purpose, create this test case to tell what is doable, 
	 * what is not, for community blog container artifact.
	 * 
	 * Process:
	 * 1.1 Call prepare community method to prepare a private community with blog widgets added
	 * 1.2 Verify that neither orgA admin nor OrgB admin can add additional ideation blog to the private community
	 * 1.3 Common user create an additional ideation blog
	 * 1.4 verify if orgA admin can get the specific blog container feed, orgB admin can not
	 * 1.5 Verify if orgA admin can modify the ideation blog, orgB admin can not
	 * 1.6 Verify if orgB admin can not delete the additional ideation blog, orgA admin can
	 * 
	 * API URL pattern covered in this test:
	 * - POST /blogs/<blogHomepageHandle>/api/blogs?lang=en_us&commUuid=<communityUuid>
	 * - PUT  /blogs/<blogHomepageHandle>/api/blogs/<blogId>
	 * - GET  /blogs/<blogHomepageHandle>/api/blogs/<blogId>
	 * - DELETE /blogs/<blogHomepageHandle>/api/blogs/<blogId>
	 */
	//Added priority = 2 to the test case so that all dependent test cases run at the end and have some extra time after prepareAPrivateCommunityWithBlogsWidgetsAdded is run
	@Test(dependsOnMethods = { "prepareAPrivateCommunityWithBlogsWidgetsAdded" }, priority = 2) // step 1.1
	public void testCRUDCommunityIdeationBlogWithOrgAdmin() throws Exception {
		boolean doAssert = true;
		
		Entry feedAfterIdeationblogCreated = null;
		ExtensibleElement creationResult = null;
		ExtensibleElement retrieveBlogFeed = null;
		
		Blog anotherIdeationBlog = new Blog("Community IdeaBlog Container 2", // blog title
				"CommunityIdeaBlogContainer2handle", // blog handel
				"Summary of Community IdeaBlog Container 2", // blog summary
				"tagIdeationBlog", // tag string
				false, //isCommunityBlog
				true, //isIdeationBlog
				CommunityBlogPermissions.PRIVATE,
				IdeationStatus.OPEN,
				TimeZone.getDefault(), 
				true, // allowComments
				365, // numDaysCommentsAllowed
				true, // emailComments
				false, // commentModerated
				false, // allowCoEdit
				0, // recommendationRank
				-1, // containerType
				null, //containerId, null means new creation
				communityUuid, // the belonging community
				null, // roleMap
				0 // voteAvailable
				);
		
		String blogsCreationURL = orgAUser2BlogService.getServiceURLString()+"/"+blogHomepageHandle+"/api/blogs?lang=en_us&commUuid=" + communityUuid;
		// step 1.2
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			creationResult = orgBAdmin2BlogService.postBlogsFeed(blogsCreationURL, anotherIdeationBlog.toEntry());
			LOGGER.debug("orgB admin does postBlogsFeed result: " + creationResult.toString());
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 403,
						"orgB admin should not be allowed to create ideation blog in orgA community");
			}
		}
		
		creationResult = orgAAdmin2BlogService.postBlogsFeed(blogsCreationURL, anotherIdeationBlog.toEntry());
		LOGGER.debug("orgA admin does postBlogsFeed result: " + creationResult.toString());
		if (doAssert) {
			assertEquals(orgAAdmin2BlogService.getRespStatus(), 403, "orgA admin is not the community owner, so not allow to add additional ideation blog container.");
		}
		
		// step 1.3
		feedAfterIdeationblogCreated = (Entry) orgAUser2BlogService.postBlogsFeed(blogsCreationURL, anotherIdeationBlog.toEntry());
		String blogEditURL = feedAfterIdeationblogCreated.getEditLinkResolvedHref().toString();
		
		if (doAssert) {
			assertEquals(orgAUser2BlogService.getRespStatus(), 201, "orgA community owner can create additional ideation blog in orgA community.");
		}
		
		// step 1.4
		retrieveBlogFeed = orgAAdmin2BlogService.getBlogFeedWithRedirect(blogEditURL);
		if (doAssert) {
			assertEquals(orgAAdmin2BlogService.getRespStatus(), 200, "orgA admin can retrieve any private community blog feed.");
		}
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			retrieveBlogFeed = orgBAdmin2BlogService.getBlogFeed(blogEditURL);
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
						"orgB admin can not retrieve any private community blog feed in orgA.");
			}
		}
		
		// step 1.5
		Entry adminModifiedEntry = feedAfterIdeationblogCreated;
		adminModifiedEntry.setTitle("orgA admin modified Community IdeaBlog Container 2");
		retrieveBlogFeed = orgAAdmin2BlogService.putBlogsFeed(blogEditURL, adminModifiedEntry);
		if (doAssert) {
			assertEquals(orgAAdmin2BlogService.getRespStatus(), 200, "orgA admin can modify additional ideation blog container.");
		}
		
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			adminModifiedEntry.setTitle("orgB admin modified community ideablog container 2");
			retrieveBlogFeed = orgBAdmin2BlogService.putBlogsFeed(blogEditURL, adminModifiedEntry);
			if (doAssert) {
				assertEquals(orgBAdmin2BlogService.getRespStatus(), 401,
						"orgB admin should not be allowed to create ideation blog in orgA community");
			}
		}
		
		// step 1.6
		boolean isDeleted;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			isDeleted = orgBAdmin2BlogService.deleteBlog(blogEditURL);
			if (doAssert) {
				assertTrue(!isDeleted);
			}
		}
		
		isDeleted = orgAAdmin2BlogService.deleteBlog(blogEditURL);
		if (doAssert) {
			assertTrue(isDeleted);
		}
		
		
	}
	
	/*
	 * 
	 * Test scenario: orgC disabled org admin per org gatekeeper CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES, so orgC admin can not CRUD blog entries in orgC.
	 * 
	 * Process:
	 * 1.1 OrgC user prepare a private community with blog widget added
	 * 1.2 OrgC user create a blog entry, verify if orgC admin can not get the blog entry
	 * 1.3 Verify if orgC admin can not modify the blog entry
	 * 1.4 Verify if orgC admin can not delete the blog entry
	 * 1.5 Verify if orgC admin can not create a blog entry
	 * 
	 * API URL pattern covered in this test:
	 * - POST /blogs/<communityBlogHandle>/api/entries 
	 * - GET  /blogs/<communityBlogHandle>/api/entries/<entryId>
	 * - PUT  /blogs/<communityBlogHandle>/api/entries/<entryId>
	 * - DELETE /blogs/<communityBlogHandle>/api/entries/<entryId>
	 */
	//Added priority = 1 to the test case so that all dependent test cases run at the end and have some extra time after prepareAPrivateCommunityWithBlogsWidgetsAdded is run
	@Test(priority = 1) // step 1.1
	public void testCRUDWithOrgAdminInDisabledOrg() throws Exception {
		
		// only run this case in Cloud environment
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			boolean doAssert = true;

			LOGGER.debug("BEGIN TEST: testCRUDWithOrgAdminInDisabledOrg");

			// step 1.1 prepare community
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

			// Create private community
			String privateCommTitle = "Test community created by " + orgCUser.getUserName() + " randomNameAddition "
					+ uniqueNameAddition;
			Community newCommunity = new Community(privateCommTitle, "Private community with blog widget added.",
					Permissions.PRIVATE, null);

			Entry communityResult = (Entry) orgCUserCommService.createCommunity(newCommunity);
			assertTrue(communityResult != null);
			LOGGER.debug("Private community '" + privateCommTitle + "' created successfully by user '"
					+ orgCUser.getUserName() + "'.");
			String responseLocation = orgCUserCommService.getRespLocation();
			LOGGER.debug("postFeed response location=" + responseLocation);

			Community comm = new Community(
					(Entry) orgCUserCommService.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
			// communityUuid is the community blog's handle by default,
			// So save it for composing community blog API URLs. But community
			// ideation blog has different handle.
			String communityUuid2 = comm.getUuid();

			LOGGER.debug("Private community '" + communityUuid + "' created at " + comm.getPublished().toString());
			LOGGER.debug("Private community '" + communityUuid + "' updated at " + comm.getUpdated().toString());

			// Add ideation blog widget
			Widget widget = new Widget(StringConstants.WidgetID.IdeationBlog.toString());
			Entry ideationBlog = (Entry) orgCUserCommService.postWidget(comm, widget.toEntry());
			assertEquals(orgCUserCommService.getRespStatus(), 201, "postWidget" + orgCUserCommService.getDetail());

			// community ideation blog use widget instance ID as the blog handle
			// by default.
			String communityIdeationBlogHandle2 = null;
			if (ideationBlog != null) {
				communityIdeationBlogHandle2 = ideationBlog.getSimpleExtension(StringConstants.SNX_WIDGET_INSTANCID);
			}
			LOGGER.debug(ideationBlog != null ? ideationBlog.toString() : "IdeationBlog widget fails to add!");
			LOGGER.debug("IdeationBlog added to private community '" + communityUuid2
					+ "', widget addition postFeed response location=" + orgCUserCommService.getRespLocation());

			// Add blogs widget
			widget = new Widget(StringConstants.WidgetID.Blog.toString());
			Entry communityBlog = (Entry) orgCUserCommService.postWidget(comm, widget.toEntry());
			assertEquals(orgCUserCommService.getRespStatus(), 201, "postWidget" + orgCUserCommService.getDetail());

			// communityUuid is the community blog's handle by default.
			String communityBlogHandle2 = null;
			if (communityBlog != null) {
				communityBlogHandle2 = communityUuid2;
			}
			LOGGER.debug(communityBlog != null ? communityBlog.toString() : "CommunityBlog widget fails to add!");
			LOGGER.debug("CommunityBlog added to private community '" + communityUuid2
					+ "', widget addition postFeed response location=" + orgCUserCommService.getRespLocation());

			Community commAfterAddBlogWidget = new Community(
					(Entry) orgCUserCommService.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
			LOGGER.debug("Private community '" + communityUuid + "' updated at "
					+ commAfterAddBlogWidget.getUpdated().toString());

			// step 1.2 start
			// for community blog
			String entryPostURL = orgCUser2BlogService.getServiceURLString() + "/" + communityBlogHandle2
					+ "/api/entries";

			Factory factory = abdera.getFactory();
			Entry entry = factory.newEntry();
			String blogEntryString = "Community " + communityUuid2 + " blog entry1";
			entry.setTitle("Title of " + blogEntryString);
			entry.setSummary("Summary of " + blogEntryString);
			entry.setContent("Content of " + blogEntryString);

			// step 1.2 start
			Entry entryPostedResult = (Entry) orgCUser2BlogService.postBlogsFeed(entryPostURL, entry);
			if (entryPostedResult != null) {
				LOGGER.debug(entryPostedResult.toString());
			}

			String postedBlogEntryItself = entryPostedResult.getEditLinkResolvedHref().toString();
			Entry orgCAdminGotEntry = (Entry) orgCAdmin2BlogService.getPost(postedBlogEntryItself);
			if (doAssert) {
				assertEquals(orgCAdmin2BlogService.getRespStatus(), 401,
						"orgC admin cant not get the blog entry feed created by " + orgCUser2.getUserName());
			}

			int respStatus = 0;

			// step 1.3
			BlogPost editEntry = new BlogPost("Admin modified title of " + blogEntryString,
					"Admin modified content of " + blogEntryString, "AdminAddedTag", true, 0);
			Entry orgCAdminModifiedEntry = (Entry) orgCAdmin2BlogService.editPost(postedBlogEntryItself, editEntry);
			respStatus = orgCAdmin2BlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgCAdmin2BlogService.getRespStatus(), 401, "orgC admin can not modify blog entry.");
			}
			boolean isDeleted = false;
			Entry orgBAdminModifiedEntry = null;
			

			// step 1.4
			isDeleted = orgCAdmin2BlogService.deletePost(postedBlogEntryItself);
			respStatus = orgCAdmin2BlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgCAdmin2BlogService.getRespStatus(), 401, "orgC admin can not delete the blog entry.");
			}
			// step 1.5
			entry.setTitle("orgC Admin Title of " + blogEntryString);
			entry.setSummary("orgC Admin Summary of " + blogEntryString);
			entry.setContent("orgC Admin Content of " + blogEntryString);
			entryPostedResult = (Entry) orgCAdmin2BlogService.postBlogsFeed(entryPostURL, entry);
			if (entryPostedResult != null) {
				LOGGER.debug(entryPostedResult.toString());
			}
			respStatus = orgCAdmin2BlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgCAdmin2BlogService.getRespStatus(), 401, "orgC admin can not post blog entry!");
			}
			// community blog test end

			// community ideation blog start
			entryPostURL = orgCUser2BlogService.getServiceURLString() + "/" + communityIdeationBlogHandle2
					+ "/api/entries";

			entry = factory.newEntry();
			blogEntryString = "Community " + communityUuid2 + " ideation blog entry1";
			entry.setTitle("Title of " + blogEntryString);
			entry.setSummary("Summary of " + blogEntryString);
			entry.setContent("Content of " + blogEntryString);

			// step 1.2 start
			entryPostedResult = (Entry) orgCUser2BlogService.postBlogsFeed(entryPostURL, entry);
			if (entryPostedResult != null) {
				LOGGER.debug(entryPostedResult.toString());
			}

			postedBlogEntryItself = entryPostedResult.getEditLinkResolvedHref().toString();
			orgCAdminGotEntry = (Entry) orgCAdmin2BlogService.getPost(postedBlogEntryItself);
			if (doAssert) {
				assertEquals(orgCAdmin2BlogService.getRespStatus(), 401,
						"orgC admin can not get the ideation blog entry feed created by " + orgCUser2.getUserName());
			}

			// step 1.3
			editEntry = new BlogPost("Admin modified title of " + blogEntryString,
					"Admin modified content of " + blogEntryString, "AdminAddedTag", true, 0);
			orgCAdminModifiedEntry = (Entry) orgCAdmin2BlogService.editPost(postedBlogEntryItself, editEntry);
			respStatus = orgCAdmin2BlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgCAdmin2BlogService.getRespStatus(), 401,
						"orgC admin can not modify ideation blog entry.");
			}

			isDeleted = orgCAdmin2BlogService.deletePost(postedBlogEntryItself);
			respStatus = orgCAdmin2BlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgCAdmin2BlogService.getRespStatus(), 401,
						"orgC admin can not delete the ideation blog entry.");
			}
			// step 1.5
			entry.setTitle("orgC Admin Title of " + blogEntryString);
			entry.setSummary("orgC Admin Summary of " + blogEntryString);
			entry.setContent("orgC Admin Content of " + blogEntryString);
			entryPostedResult = (Entry) orgCAdmin2BlogService.postBlogsFeed(entryPostURL, entry);
			if (entryPostedResult != null) {
				LOGGER.debug(entryPostedResult.toString());
			}
			respStatus = orgCAdmin2BlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgCAdmin2BlogService.getRespStatus(), 401,
						"orgC admin can not post an ideation blog entry.");
			}
		}
	}
	
	/* Communities ATOM API does not support add external user as community member, always return 400, 
	 * Though the user can do it via Communities membership UI:
	 *   POST https://apps.basesandbox07.swg.usma.ibm.com/communities/service/html/community/members/add
	 *   
	 * This scenario is verified manually via Communities membership management UI.  
	 * 
	 * Test scenario: orgA and orgB enabled org admin per orgadmin gatekeeper CONNECTIONS_ORG_ADMIN_FULL_PRIVILEGES, orgB admin user is added to
	 *                orgA community as an external member. Verify if orgB admin can CRUD blog entry.
	 * 
	 * Process:
	 * 1.1 OrgA user prepare a private community with blog widget added
	 * 1.2 OrgA add orgB admin as a member of the created community
	 * 1.3 Verify if orgB admin can create a blog entry
	 * 1.4 Verify if orgB admin can get the blog entry
	 * 1.5 Verify if orgB admin can modify the blog entry
	 * 1.6 Verify if orgB admin can delete the blog entry
	 * 
	 * API URL pattern covered in this test:
	 * - POST /blogs/<communityBlogHandle>/api/entries 
	 * - GET  /blogs/<communityBlogHandle>/api/entries/<entryId>
	 * - PUT  /blogs/<communityBlogHandle>/api/entries/<entryId>
	 * - DELETE /blogs/<communityBlogHandle>/api/entries/<entryId>
	 */
	
	/*@Test
	public void testCRUDWithOrgBOrgAdminAsExternalUserInOrgA() throws Exception {
		
		// only run this case in Cloud environment
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			boolean doAssert = false;

			LOGGER.debug("BEGIN TEST: testCRUDWithOrgAdminInDisabledOrg");

			// step 1.1 prepare community
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

			// Create private community
			String privateCommTitle = "Test community created by " + orgCUser.getUserName() + " randomNameAddition "
					+ uniqueNameAddition;
			Community newCommunity = new Community(privateCommTitle, "Private community with blog widget added.",
					Permissions.PRIVATE, null);

			Entry communityResult = (Entry) orgAUserCommService.createCommunity(newCommunity);
			assertTrue(communityResult != null);
			LOGGER.debug("Private community '" + privateCommTitle + "' created successfully by user '"
					+ orgAUser.getUserName() + "'.");
			String responseLocation = orgAUserCommService.getRespLocation();
			LOGGER.debug("postFeed response location=" + responseLocation);

			Community comm = new Community(
					(Entry) orgAUserCommService.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
			// communityUuid is the community blog's handle by default,
			// So save it for composing community blog API URLs. But community
			// ideation blog has different handle.
			String communityUuid2 = comm.getUuid();

			LOGGER.debug("Private community '" + communityUuid + "' created at " + comm.getPublished().toString());
			LOGGER.debug("Private community '" + communityUuid + "' updated at " + comm.getUpdated().toString());

			// Add ideation blog widget
			Widget widget = new Widget(StringConstants.WidgetID.IdeationBlog.toString());
			Entry ideationBlog = (Entry) orgAUserCommService.postWidget(comm, widget.toEntry());
			assertEquals(orgAUserCommService.getRespStatus(), 201, "postWidget" + orgAUserCommService.getDetail());

			// community ideation blog use widget instance ID as the blog handle
			// by default.
			String communityIdeationBlogHandle2 = null;
			if (ideationBlog != null) {
				communityIdeationBlogHandle2 = ideationBlog.getSimpleExtension(StringConstants.SNX_WIDGET_INSTANCID);
			}
			LOGGER.debug(ideationBlog != null ? ideationBlog.toString() : "IdeationBlog widget fails to add!");
			LOGGER.debug("IdeationBlog added to private community '" + communityUuid2
					+ "', widget addition postFeed response location=" + orgAUserCommService.getRespLocation());

			// Add blogs widget
			widget = new Widget(StringConstants.WidgetID.Blog.toString());
			Entry communityBlog = (Entry) orgAUserCommService.postWidget(comm, widget.toEntry());
			assertEquals(orgAUserCommService.getRespStatus(), 201, "postWidget" + orgAUserCommService.getDetail());

			// communityUuid is the community blog's handle by default.
			String communityBlogHandle2 = null;
			if (communityBlog != null) {
				communityBlogHandle2 = communityUuid2;
			}
			LOGGER.debug(communityBlog != null ? communityBlog.toString() : "CommunityBlog widget fails to add!");
			LOGGER.debug("CommunityBlog added to private community '" + communityUuid2
					+ "', widget addition postFeed response location=" + orgAUserCommService.getRespLocation());

			Community commAfterAddBlogWidget = new Community(
					(Entry) orgAUserCommService.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
			LOGGER.debug("Private community '" + communityUuid + "' updated at "
					+ commAfterAddBlogWidget.getUpdated().toString());

			// step 1.2 start, add orgB admin as a community member
			UserPerspective orgBAdmin = new UserPerspective(15, Component.COMMUNITIES.toString());
			Member member = new Member(orgBAdmin.getEmail(), orgBAdmin.getUserId(),
					Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
			orgAUserCommService.addMemberToCommunity(commAfterAddBlogWidget, member);
			if (doAssert) {
				assertEquals(orgAUserCommService.getRespStatus(), 400, "Owner - add External user");
			}
			
			UserPerspective orgAUser2 = new UserPerspective(3, Component.COMMUNITIES.toString());
			Member member2 = new Member(orgAUser2.getEmail(), orgAUser2.getUserId(), Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
			orgAUserCommService.addMemberToCommunity(commAfterAddBlogWidget, member2);
			int status = orgAUserCommService.getRespStatus();
			if (doAssert) {
				assertEquals(status, 201, "Owner - add External user");
			}
			BlogsService orgBAdminBlogService = orgBAdmin.getBlogsService();
			
			// step 1.3
			// for community blog
			String entryPostURL = orgBAdminBlogService.getServiceURLString() + "/" + communityBlogHandle2
					+ "/api/entries";

			Factory factory = abdera.getFactory();
			Entry entry = factory.newEntry();
			String blogEntryString = "Community " + communityUuid2 + " blog entry1";
			entry.setTitle("Title of " + blogEntryString);
			entry.setSummary("Summary of " + blogEntryString);
			entry.setContent("Content of " + blogEntryString);

			Entry entryPostedResult = (Entry) orgBAdminBlogService.postBlogsFeed(entryPostURL, entry);
			if (doAssert) {
				assertEquals(orgBAdminBlogService.getRespStatus(), 201,
						"orgB admin as external user can create the blog entry.");
			}
			if (entryPostedResult != null) {
				LOGGER.debug(entryPostedResult.toString());
			}
            // step 1.4
			String postedBlogEntryItself = entryPostedResult.getEditLinkResolvedHref().toString();
			Entry orgBAdminGotEntry = (Entry) orgBAdminBlogService.getPost(postedBlogEntryItself);
			if (doAssert) {
				assertEquals(orgBAdminBlogService.getRespStatus(), 200,
						"orgB admin can get the blog entry feed.");
			}

			int respStatus = 0;

			// step 1.5
			BlogPost editEntry = new BlogPost("Admin modified title of " + blogEntryString,
					"Admin modified content of " + blogEntryString, "AdminAddedTag", true, 0);
			Entry orgBAdminModifiedEntry = (Entry) orgBAdminBlogService.editPost(postedBlogEntryItself, editEntry);
			respStatus = orgBAdminBlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgBAdminBlogService.getRespStatus(), 201, "orgB admin as external user can modify blog entry.");
			}
			boolean isDeleted = false;
			

			// step 1.6
			isDeleted = orgBAdminBlogService.deletePost(postedBlogEntryItself);
			respStatus = orgBAdminBlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgBAdminBlogService.getRespStatus(), 204, "orgB admin as external user can delete the blog entry.");
			}
			
			// community blog test end

			// community ideation blog start
			entryPostURL = orgBAdminBlogService.getServiceURLString() + "/" + communityIdeationBlogHandle2
					+ "/api/entries";

			entry = factory.newEntry();
			blogEntryString = "Community " + communityUuid2 + " ideation blog entry1";
			entry.setTitle("Title of " + blogEntryString);
			entry.setSummary("Summary of " + blogEntryString);
			entry.setContent("Content of " + blogEntryString);

			// step 1.3
			entryPostedResult = (Entry) orgBAdminBlogService.postBlogsFeed(entryPostURL, entry);
			if (doAssert) {
				assertEquals(orgBAdminBlogService.getRespStatus(), 201,
						"orgB admin as external user can create an ideation blog entry.");
			}
			if (entryPostedResult != null) {
				LOGGER.debug(entryPostedResult.toString());
			}
            // step 1.4
			postedBlogEntryItself = entryPostedResult.getEditLinkResolvedHref().toString();
			orgBAdminGotEntry = (Entry) orgBAdminBlogService.getPost(postedBlogEntryItself);
			if (doAssert) {
				assertEquals(orgBAdminBlogService.getRespStatus(), 200,
						"orgB admin can get the ideation blog entry feed.");
			}

			// step 1.5
			editEntry = new BlogPost("Admin modified title of " + blogEntryString,
					"Admin modified content of " + blogEntryString, "AdminAddedTag", true, 0);
			orgBAdminModifiedEntry = (Entry) orgBAdminBlogService.editPost(postedBlogEntryItself, editEntry);
			respStatus = orgBAdminBlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgBAdminBlogService.getRespStatus(), 201,
						"orgB admin as external user can modify the ideation blog entry.");
			}

			// step 1.6
			isDeleted = orgBAdminBlogService.deletePost(postedBlogEntryItself);
			respStatus = orgBAdminBlogService.getRespStatus();
			if (doAssert) {
				assertEquals(orgBAdminBlogService.getRespStatus(), 204,
						"orgC admin can not delete the ideation blog entry.");
			}
			
		}
	}*/

	@AfterMethod
	public static void tearDown() throws Exception {
		if (gk1_modified)  {
			resetGatekeeper(org_admin_gk1);
		}
		if (gk2_modified) {
			resetGatekeeper(org_admin_gk2);
		}
		orgAUserCommService.tearDown();
		orgAUser2BlogService.tearDown();
		orgAAdmin2BlogService.tearDown();
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgBAdmin2BlogService.tearDown();
			orgCUserCommService.tearDown();
			orgCUser2BlogService.tearDown();
			orgCAdmin2BlogService.tearDown();
		}
	}

}
