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



public class CommunityBlogsCrossOrgDataAPITests {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunityBlogsCrossOrgDataAPITests.class.getName());
	
	static Abdera abdera = new Abdera();
	
	
	
	protected static String orgAcommunityUuid = null, orgCcommunityUuid = null;
	protected static String orgAcommunityIdeationBlogHandle = null, orgCcommunityIdeationBlogHandle = null;
	protected static String orgAcommunityBlogHandle = null, orgCcommunityBlogHandle = null;
	protected static String blogHomepageHandle = "homepage";
	
	protected static UserPerspective orgAUser, orgAUser2; 
	protected static CommunitiesService orgAUserCommService;
	
	protected static BlogsService orgAUser2BlogService;
	
	
	protected static UserPerspective orgCUser, orgCUser2;
	protected static CommunitiesService orgCUserCommService;
	protected static BlogsService orgCUser2BlogService;
	
	protected static boolean useSSL = true;
	
	/**
	 * The convention for test environment settings is as following:
	 * 1. orgAUser is a normal user, using community service
	 * 2. orgAUser2 is the same user as orgAUser, but for using blog service
	 * 3. orgCUser and orgCUser2 is the same person for community service and blog service respectively
	 * @throws Exception
	 */
	
	@BeforeMethod
	public static void setUp() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("Start Initializing Community Blogs Cross-Org Data API Test Cases.");

			// i1 profile data convention:
			// - 2 is orgA normal user
			// - 17 is orgC normal user
			orgAUser = new UserPerspective(2, Component.COMMUNITIES.toString());
			orgAUserCommService = orgAUser.getCommunitiesService();

			orgAUser2 = new UserPerspective(2, Component.BLOGS.toString());
			orgAUser2BlogService = orgAUser2.getBlogsService();
			blogHomepageHandle = orgAUser2BlogService.getBlogsHomepageHandle();

			LOGGER.debug("Target environment is Cloud, so will run cross-org data API testings.");

			orgCUser = new UserPerspective(17, Component.COMMUNITIES.toString());
			orgCUserCommService = orgCUser.getCommunitiesService();

			orgCUser2 = new UserPerspective(17, Component.BLOGS.toString());
			orgCUser2BlogService = orgCUser2.getBlogsService();
			LOGGER.debug("Finished Initializing Community Blogs Cross-Org Data API Test Cases.");
		}
	}

	/**
	 * Blogs blocks most of aggregation feed APIs for Cloud environments to avoid any potential data leak issue.
	 * 
	 * However, there is one API unblocked for Mobile blogs dashboard function, which is a favorite feature of 
	 * Connections Mobile client.
	 * 
	 * - /blogs/homepage/feed/entries/atom
	 * 
	 * This test case is to verify there is no data leak on that API.
	 * 
	 * Process:
	 * 1.1 Login as orgA user to prepare a public community with blog widgets added
	 * 1.2 Add a blog entry in orgA public community blogs, including ideation blog
	 * 1.3 Login as orgC user to prepare a public community with blog widgets added
	 * 1.4 Add a blog entry in orgC public community blogs, including ideation blog
	 * 1.5 Use orgA user to retrieve blog entries from the blogs entries aggregation API, 
	 *     verify orgA entry is there and orgC entry is not there.
	 * 1.6 Use orgC user to retrieve blog entries from the same API, verify orgC entry is there and 
	 *     orgA entry is not there.
	 */
	@Test
	public void testBlogEntriesAggregationFeedAPI() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) { // this case only run in Cloud env
			
			boolean doAssert = true;

			LOGGER.debug("BEGIN TEST: prepareAPublicCommunityWithBlogsWidgetsAdded()");
			
			// step 1.1
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

			// Create public community of orgA
			String publicCommTitle = "Test community created by " + orgAUser.getUserName() + " randomNameAddition "
					+ uniqueNameAddition;
			Community newCommunity = new Community(publicCommTitle, "Public community with blog widget added.",
					Permissions.PUBLIC, null);

			Entry communityResult = (Entry) orgAUserCommService.createCommunity(newCommunity);
			if (doAssert) {
				assertTrue(communityResult != null);
			}
			LOGGER.debug("Public community '" + publicCommTitle + "' created successfully by user '"
					+ orgAUser.getUserName() + "'.");
			String responseLocation = orgAUserCommService.getRespLocation();
			LOGGER.debug("postFeed response location=" + responseLocation);

			Community comm = new Community(
					(Entry) orgAUserCommService.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
			// communityUuid is the community blog's handle by default,
			// So save it for composing community blog API URLs. But community
			// ideation blog has different handle.
			orgAcommunityUuid = comm.getUuid();

			LOGGER.debug("Public community '" + orgAcommunityUuid + "' created at " + comm.getPublished().toString());

			// Add ideation blog widget
			Widget widget = new Widget(StringConstants.WidgetID.IdeationBlog.toString());
			Entry ideationBlog = (Entry) orgAUserCommService.postWidget(comm, widget.toEntry());
			if (doAssert) {
				assertEquals(orgAUserCommService.getRespStatus(), 201, "postWidget" + orgAUserCommService.getDetail());
			}
			// community ideation blog use widget instance ID as the blog handle by default.
			if (ideationBlog != null) {
				orgAcommunityIdeationBlogHandle = ideationBlog.getSimpleExtension(StringConstants.SNX_WIDGET_INSTANCID);
			}
			LOGGER.debug(ideationBlog != null ? ideationBlog.toString() : "IdeationBlog widget fails to add!");
			LOGGER.debug("IdeationBlog added to Public community '" + orgAcommunityUuid
					+ "', widget addition postFeed response location=" + orgAUserCommService.getRespLocation());

			// Add blogs widget
			widget = new Widget(StringConstants.WidgetID.Blog.toString());
			Entry communityBlog = (Entry) orgAUserCommService.postWidget(comm, widget.toEntry());
			if (doAssert) {
				assertEquals(orgAUserCommService.getRespStatus(), 201, "postWidget" + orgAUserCommService.getDetail());
			}
			// communityUuid is the community blog's handle by default.
			if (communityBlog != null) {
				orgAcommunityBlogHandle = orgAcommunityUuid;
			}
			LOGGER.debug(communityBlog != null ? communityBlog.toString() : "CommunityBlog widget fails to add!");
			LOGGER.debug("CommunityBlog added to Public community '" + orgAcommunityUuid
					+ "', widget addition postFeed response location=" + orgAUserCommService.getRespLocation());

			Community commAfterAddBlogWidget = new Community(
					(Entry) orgAUserCommService.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
			LOGGER.debug("Public community '" + orgAcommunityUuid + "' updated at "
					+ commAfterAddBlogWidget.getUpdated().toString());

			// step 1.2 
			// for community blog
			String entryPostURL = orgAUser2BlogService.getServiceURLString() + "/" + orgAcommunityBlogHandle
					+ "/api/entries";

			Factory factory = abdera.getFactory();
			Entry entry = factory.newEntry();
			String orgAblogEntryString = "orgA Community " + orgAcommunityUuid + " blog entry1";
			entry.setTitle("Title of " + orgAblogEntryString);
			entry.setSummary("Summary of " + orgAblogEntryString);
			entry.setContent("Content of " + orgAblogEntryString);

			Entry entryPostedResult = (Entry) orgAUser2BlogService.postBlogsFeed(entryPostURL, entry);
			if (entryPostedResult != null) {
				LOGGER.debug(entryPostedResult.toString());
			}
			// for community ideation blog
			entryPostURL = orgAUser2BlogService.getServiceURLString() + 
					"/" + orgAcommunityIdeationBlogHandle + "/api/entries";
			
			entry = factory.newEntry();
			String orgAIdeationBlogEntryString = "orgA Community " + orgAcommunityUuid + " ideation blog entry1";
			entry.setTitle("Title of " + orgAIdeationBlogEntryString);
			entry.setSummary("Summary of " + orgAIdeationBlogEntryString);
			entry.setContent("Content of " + orgAIdeationBlogEntryString);
			
			entryPostedResult = (Entry) orgAUser2BlogService.postBlogsFeed(entryPostURL, entry);
			if (entryPostedResult != null) {
				LOGGER.debug(entryPostedResult.toString());
			}
			
			//step 1.3
			uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

			// Create public community of orgA
			String orgCpublicCommTitle = "Test community created by " + orgCUser.getUserName() + " randomNameAddition "
					+ uniqueNameAddition;
			Community orgCnewCommunity = new Community(orgCpublicCommTitle, "Public community with blog widget added.",
					Permissions.PUBLIC, null);

			Entry orgCcommunityResult = (Entry) orgCUserCommService.createCommunity(orgCnewCommunity);
			if (doAssert) {
				assertTrue(orgCcommunityResult != null);
			}
			LOGGER.debug("Public community '" + orgCpublicCommTitle + "' created successfully by user '"
					+ orgCUser.getUserName() + "'.");
			String orgCresponseLocation = orgCUserCommService.getRespLocation();
			LOGGER.debug("postFeed response location=" + orgCresponseLocation);

			Community orgCcomm = new Community(
					(Entry) orgCUserCommService.getCommunity(orgCcommunityResult.getEditLinkResolvedHref().toString()));
			// communityUuid is the community blog's handle by default,
			// So save it for composing community blog API URLs. But community
			// ideation blog has different handle.
			orgCcommunityUuid = orgCcomm.getUuid();

			LOGGER.debug("Public community '" + orgCcommunityUuid + "' created at " + orgCcomm.getPublished().toString());

			// Add ideation blog widget
			Widget orgCwidget = new Widget(StringConstants.WidgetID.IdeationBlog.toString());
			Entry orgCideationBlog = (Entry) orgCUserCommService.postWidget(orgCcomm, orgCwidget.toEntry());
			if (doAssert) {
				assertEquals(orgCUserCommService.getRespStatus(), 201, "postWidget" + orgCUserCommService.getDetail());
			}
			// community ideation blog use widget instance ID as the blog handle by default.
			if (orgCideationBlog != null) {
				orgCcommunityIdeationBlogHandle = orgCideationBlog.getSimpleExtension(StringConstants.SNX_WIDGET_INSTANCID);
			}
			LOGGER.debug(orgCideationBlog != null ? orgCideationBlog.toString() : "IdeationBlog widget fails to add!");
			LOGGER.debug("IdeationBlog added to Public community '" + orgCcommunityUuid
					+ "', widget addition postFeed response location=" + orgCUserCommService.getRespLocation());

			// Add blogs widget
			orgCwidget = new Widget(StringConstants.WidgetID.Blog.toString());
			Entry orgCcommunityBlog = (Entry) orgCUserCommService.postWidget(orgCcomm, orgCwidget.toEntry());
			if (doAssert) {
				assertEquals(orgCUserCommService.getRespStatus(), 201, "postWidget" + orgCUserCommService.getDetail());
			}
			// communityUuid is the community blog's handle by default.
			if (orgCcommunityBlog != null) {
				orgCcommunityBlogHandle = orgCcommunityUuid;
			}
			LOGGER.debug(orgCcommunityBlog != null ? orgCcommunityBlog.toString() : "CommunityBlog widget fails to add!");
			LOGGER.debug("CommunityBlog added to Public community '" + orgCcommunityUuid
					+ "', widget addition postFeed response location=" + orgCUserCommService.getRespLocation());

			Community orgCcommAfterAddBlogWidget = new Community(
					(Entry) orgCUserCommService.getCommunity(orgCcommunityResult.getEditLinkResolvedHref().toString()));
			LOGGER.debug("Public community '" + orgAcommunityUuid + "' updated at "
					+ orgCcommAfterAddBlogWidget.getUpdated().toString());
			
			// step 1.4
			// for community blog
			String orgCentryPostURL = orgCUser2BlogService.getServiceURLString() + "/" + orgCcommunityBlogHandle
					+ "/api/entries";

			Factory orgCfactory = abdera.getFactory();
			Entry orgCentry = orgCfactory.newEntry();
			String orgCblogEntryString = "orgC Community " + orgCcommunityUuid + " blog entry1";
			orgCentry.setTitle("Title of " + orgCblogEntryString);
			orgCentry.setSummary("Summary of " + orgCblogEntryString);
			orgCentry.setContent("Content of " + orgCblogEntryString);

			Entry orgCentryPostedResult = (Entry) orgCUser2BlogService.postBlogsFeed(orgCentryPostURL, orgCentry);
			if (orgCentryPostedResult != null) {
				LOGGER.debug(orgCentryPostedResult.toString());
			}
			// for community ideation blog
			orgCentryPostURL = orgCUser2BlogService.getServiceURLString() + "/" + orgCcommunityIdeationBlogHandle
					+ "/api/entries";

			orgCentry = orgCfactory.newEntry();
			String orgCIdeationblogEntryString = "orgC Community " + orgCcommunityUuid + " ideation blog entry1";
			orgCentry.setTitle("Title of " + orgCIdeationblogEntryString);
			orgCentry.setSummary("Summary of " + orgCIdeationblogEntryString);
			orgCentry.setContent("Content of " + orgCIdeationblogEntryString);

			orgCentryPostedResult = (Entry) orgCUser2BlogService.postBlogsFeed(orgCentryPostURL, orgCentry);
			if (orgCentryPostedResult != null) {
				LOGGER.debug(orgCentryPostedResult.toString());
			}

			// step 1.5
			String blogEntriesAggregationFeedURL = orgAUser2BlogService.getServiceURLString() + "/" + blogHomepageHandle
					+ "/feed/entries/atom";
			Feed orgAGotFeed = (Feed) orgAUser2BlogService.getBlogFeedWithRedirect(blogEntriesAggregationFeedURL);
			boolean found1 = false;
			boolean found2 = false;
			boolean found3 = false;
			boolean found4 = false;
			for (Entry blogentry : orgAGotFeed.getEntries()) {
				if (blogentry.getTitle().contains(orgAblogEntryString)) {
					found1 = true;
				}
				if(blogentry.getTitle().contains(orgAIdeationBlogEntryString)) {
					found2 = true;
				}
				if (blogentry.getTitle().contains(orgCblogEntryString)) {
					found3 = true;
				}
				if (blogentry.getTitle().contains(orgCIdeationblogEntryString)) {
					found4 = true;
				}
			}
			if (doAssert) {
				assertTrue(found1 && found2 && !found3 && !found4);
			}
			
			// step 1.6
			String orgCblogEntriesAggregationFeedURL = orgCUser2BlogService.getServiceURLString() + "/" + blogHomepageHandle
					+ "/feed/entries/atom";
			Feed orgCGotFeed = (Feed) orgCUser2BlogService.getBlogFeedWithRedirect(orgCblogEntriesAggregationFeedURL);
			boolean found11 = false;
			boolean found21 = false;
			boolean found31 = false;
			boolean found41 = false;
			for (Entry blogentry : orgCGotFeed.getEntries()) {
				if (blogentry.getTitle().contains(orgAblogEntryString)) {
					found11 = true;
				}
				if (blogentry.getTitle().contains(orgAIdeationBlogEntryString)) {
					found21 = true;
				}
				if (blogentry.getTitle().contains(orgCblogEntryString)) {
					found31 = true;
				}
				if (blogentry.getTitle().contains(orgCIdeationblogEntryString)) {
					found41 = true;
				}
			}
			if (doAssert) {
				assertTrue(!found11 && !found21 && found31 && found41);
			}

		}
		
	}
	

	@AfterMethod
	public static void tearDown() throws Exception {
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			orgAUserCommService.tearDown();
			orgAUser2BlogService.tearDown();
			orgCUserCommService.tearDown();
			orgCUser2BlogService.tearDown();
		}
	}

}