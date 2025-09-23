package com.ibm.lconn.automation.framework.services.communities.impersonated;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.CommunityBlogsFilesTestBase;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;

/**
 * JUnit Tests via Connections API for Communities Service
 * 
 * @author Ping - wangpin@us.ibm.com
 */
public class CommunityBlogsFilesImpersonateTest extends
		CommunityBlogsFilesTestBase {

	private static UserPerspective impersonateByotherUser;

	private static BlogsService otherUserImService;
	private static CommunitiesService imCommService;

	// fictitious - creation date
	private static Date created = new Date(1409656000); 

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunityBlogsFilesImpersonateTest.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing CommunityBlogsFiles impersonate Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.COMMUNITIES.toString());
		service = user.getCommunitiesService();

		userEnv.getImpersonateEnvironment(StringConstants.ADMIN_USER,
				StringConstants.CURRENT_USER, Component.FILES.toString());
		user = userEnv.getLoginUser();
		filesService = user.getFilesService();
		imUser = userEnv.getImpersonatedUser();
		
		userEnv.getImpersonateEnvironment(StringConstants.ADMIN_USER, StringConstants.CURRENT_USER, Component.COMMUNITIES.toString());
		user = userEnv.getLoginUser();
		imCommService = user.getCommunitiesService();
		
		userEnv.getImpersonateEnvironment(StringConstants.ADMIN_USER, StringConstants.CURRENT_USER, Component.BLOGS.toString());
		user = userEnv.getLoginUser();
		blogsService = user.getBlogsService();
		if (StringConstants.MODERATION_ENABLED) {
			modServiceBlogs = user.getModerationService();
		}

		otherUser = new UserPerspective(StringConstants.RANDOM1_USER,
				Component.BLOGS.toString());
		blogsotherUserService = otherUser.getBlogsService();

		blogHomepageHandle = blogsService.getBlogsHomepageHandle();
		blogHomepageHandle = blogsotherUserService.getBlogsHomepageHandle();

		impersonateByotherUser = new UserPerspective(
				StringConstants.RANDOM1_USER, Component.BLOGS.toString(),
				StringConstants.CURRENT_USER);
		otherUserImService = impersonateByotherUser.getBlogsService();

		connectionsAdminUser = new UserPerspective(
				StringConstants.CONNECTIONS_ADMIN_USER,
				Component.FILES.toString());
		filesAdminService = connectionsAdminUser.getFilesService();
		if (StringConstants.MODERATION_ENABLED) {
			modServiceFiles = connectionsAdminUser.getModerationService();
		}

		LOGGER.debug("Finished Initializing CommunityBlogsFiles impersonate Test");
	}

	@Test
	public void testBlogsImpersonateInvalidUser() throws Exception {
		LOGGER.debug("BEGIN TEST: Test Blogs ImpersonateInvalidUser.");
		
		// 1. Create a private community
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String comName = "Test Blogs ImpersonateInvalidUser "
				+ uniqueNameAddition;

		Community newCommunity = new Community(
				comName,
				"Test Blogs ImpersonateInvalidUser.",
				Permissions.PRIVATE, null);

		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		Community comm = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		// 2 Add blogs widget
		Feed widgetsInitialFeed = (Feed) service.getCommunityWidgets(comm.getUuid());
		boolean blogFound=false;
		for (Entry e : widgetsInitialFeed.getEntries()) {			
			if (e.getTitle().equals("Blog")){
				blogFound=true;
				break;			
			}
		}
		if(blogFound==false){
			Widget widget = new Widget(StringConstants.WidgetID.Blog.toString());
			service.postWidget(comm, widget.toEntry());
			assertEquals(201, service.getRespStatus());
		}
		
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				comm.getRemoteAppsListHref(), true, null, 0, 50, null, null,
				SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);

		String blogUrl = null;
		for (Entry raEntry : remoteAppsFeed.getEntries()) {
			for (Category category : raEntry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("Blog")) {
					for (Link link : raEntry.getLinks()) {
						if (link.getRel()
								.contains("remote-application/publish")) {
							blogUrl = link.getHref().toString();
						}
					}
				}
			}
		}

		String postBlogUrl = null;
		ExtensibleElement blogEE = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			blogEE = service.getAnyFeedWithRedirect(blogUrl);
		} else {
			blogEE = service.getAnyFeed(blogUrl);
		}
		assertTrue(blogEE != null);
		for (Element ele : blogEE.getElements()) {
			if (ele.toString().startsWith("<workspace")) {
				for (Element ele2 : ele.getElements()) {
					if (ele2.toString().startsWith("<collection")) {
						for (QName atrb : ele2.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("href")
									&& ele2.getAttributeValue("href").contains(
											"api/entries")) {
								postBlogUrl = ele2.getAttributeValue("href");
							}
						}
					}
				}
			}
		}
		
		// 3. Add blog entry
		Factory factory = abdera.getFactory();
		ExtensibleElement ee = null;
		
		Entry blogEntry = factory.newEntry();
		blogEntry.setTitle("blogEntry 1");
		blogEntry.setId("some-id-should-be-ignored");
		blogEntry.setContent("blogEntry content");
		blogEntry.addCategory("DataPopBlogEntry");
		
		UserPerspective adminUser = new UserPerspective(StringConstants.ADMIN_USER,
				Component.BLOGS.toString());
		BlogsService blogsAdminUserService = adminUser.getBlogsService();
		
		Map<String, String> requestHeaders = new HashMap<String, String>();
		requestHeaders.put("X-LConn-RunAs", "useremail=noneExistUser@janet.iris.com, fallback=false");
		blogsAdminUserService.setRequestOptions(requestHeaders);

		ee = blogsAdminUserService.postBlogsFeed(postBlogUrl, blogEntry);
		// 4. Validate the invalid user returns ERROR - 412: Precondition Failed
		// SC server return 403 not 412
		// https://swgjazz.ibm.com:8004/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/210182
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD){
			assertEquals("post commuinty blog entry", 403, blogsAdminUserService.getRespStatus());
		} else {
			assertEquals("post commuinty blog entry", HttpServletResponse.SC_PRECONDITION_FAILED, blogsAdminUserService.getRespStatus());
		}
		LOGGER.debug("END TEST: Test Blogs ImpersonateInvalidUser.");
	}
	
	// TODO rewrite this test and remove ideation blog. We don't need more
	// ideation blog tests, but we do need blog tests.
	@Test
	public void commBlogAndIdeationBlog() throws Exception {
		/*
		 * Test process 1. Create a private community 2. Add the blog and
		 * ideationblog widgets. 3. Add blog and ideablog entries 4. Validate
		 * that Community Blog was created.
		 */
		LOGGER.debug("BEGIN TEST: RTC 83984 Private community blog/ideation blog is not returned in feed.");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String comName = "RTC 83984 ideation blog is not in feed "
				+ uniqueNameAddition;

		// Create private community
		Community newCommunity = new Community(
				comName,
				"Private community blog/ideation blog is not returned in feed.",
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

		// Add blogs widget
		Feed widgetsInitialFeed = (Feed) service.getCommunityWidgets(comm.getUuid());
		boolean commBlogFound = false;
		for (Entry e : widgetsInitialFeed.getEntries()) {
			if (e.getTitle().equals("Blog")) {
				commBlogFound = true;
				break;
			}
		}
		if (commBlogFound == false) {
			widget = new Widget(StringConstants.WidgetID.Blog.toString());
			service.postWidget(comm, widget.toEntry());
			assertEquals(201, service.getRespStatus());
		}

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
		String blogUrl = null;
		for (Entry raEntry : remoteAppsFeed.getEntries()) {
			for (Category category : raEntry.getCategories()) {
				// For ideation blog
				if (category.getTerm().equalsIgnoreCase("IdeationBlog")) {
					for (Link link : raEntry.getLinks()) {
						if (link.getRel()
								.contains("remote-application/publish")) {
							ideationBlogUrl = link.getHref().toString();
						}
					}
				}
				// For blog
				if (category.getTerm().equalsIgnoreCase("Blog")) {
					for (Link link : raEntry.getLinks()) {
						if (link.getRel()
								.contains("remote-application/publish")) {
							blogUrl = link.getHref().toString();
						}
					}
				}
			}
		}

		// This call returns a service doc, not a feed.
		String postIdeaBlogUrl = null;
		ExtensibleElement ee = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ee = service.getAnyFeedWithRedirect(ideationBlogUrl);
		} else {
			ee = service.getAnyFeed(ideationBlogUrl);
		}
		assertTrue(ee != null);
		// For ideation blog
		// TODO Use the Service document class to parse. No need to use strings
		// to locate things.
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

		// For Blogs
		String postBlogUrl = null;
		ExtensibleElement blogEE = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			blogEE = service.getAnyFeedWithRedirect(blogUrl);
		} else {
			blogEE = service.getAnyFeed(blogUrl);
		}
		assertTrue(blogEE != null);
		for (Element ele : blogEE.getElements()) {
			if (ele.toString().startsWith("<workspace")) {
				for (Element ele2 : ele.getElements()) {
					if (ele2.toString().startsWith("<collection")) {
						for (QName atrb : ele2.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("href")
									&& ele2.getAttributeValue("href").contains(
											"api/entries")) {
								postBlogUrl = ele2.getAttributeValue("href");
							}
						}
					}
				}
			}
		}

		// Ideation blog entry
		Factory factory = abdera.getFactory();
		Entry entry = factory.newEntry();
		entry.setTitle("RTC83984 IdeaBlog 1");
		entry.setId("some-id-should-be-ignored");
		entry.setContent("idea1 content");
		entry.addCategory("DataPopIdeaBlogEntry");

		// Blog entry
		String blogTitle = "RTC83984 Blog 1 Created in Communities";
		Entry blogEntry = factory.newEntry();
		blogEntry.setTitle(blogTitle);
		blogEntry.setId("some-id-should-be-ignored");
		blogEntry.setContent("blog1 content");
		blogEntry.addCategory("DataPopBlogEntry");

		otherUserImService.postBlogsFeed(postIdeaBlogUrl, entry);
		assertEquals("post commuinty blog", 403,
				otherUserImService.getRespStatus());

		blogEE = blogsService.postBlogsFeed(postIdeaBlogUrl, entry);
		assertEquals("post commuinty blog", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) blogEE).getAuthor().getName());

		// Create the blog
		otherUserImService.postBlogsFeed(postBlogUrl, blogEntry);
		assertEquals("post commuinty blog", 403,
				otherUserImService.getRespStatus());

		blogEE = blogsService.postBlogsFeed(postBlogUrl, blogEntry);
		assertEquals("post commuinty blog", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) blogEE).getAuthor().getName());

		String publishUrl = "";
		String comBlogUrl = "";

		Feed remoteAppsFeed2 = (Feed) service.getCommunityRemoteAPPs(
				comm.getRemoteAppsListHref(), true, null, 0, 50, null, null,
				SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		for (Entry ntry : remoteAppsFeed2.getEntries()) {
			if (ntry.getTitle().equalsIgnoreCase("blog")) {
				for (Link lnk : ntry.getLinks()) {
					if (lnk.getRel().contains("remote-application/publish")) {
						publishUrl = lnk.getHref().toURL().toString();
					}
				}
			}
		}

		if (publishUrl != "") {
			Service srvc = (Service) service.getAnyFeed(publishUrl);
			for (Workspace wrksp : srvc.getWorkspaces()) {
				if (wrksp.getTitle().equalsIgnoreCase(comName)) {
					Collection col = wrksp.getCollection("Weblog Entries");
					IRI lnk = col.getHref();
					comBlogUrl = lnk.toURL().toString();
				}
			}
		}

		// Validation.
		boolean blogFound = false;
		Feed blogFeed = (Feed) service.getAnyFeed(comBlogUrl);
		for (Entry ntry2 : blogFeed.getEntries()) {
			if (ntry2.getTitle().equalsIgnoreCase(blogTitle)) {
				blogFound = true;
			}
		}
		assertEquals("Blog entry not found", true, blogFound);
	
	}
	
	@Test
	public void commBlogAndIdeationBlogWithImpersonate() throws Exception {
		
		
		if(StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD){	
			return;
		}
		
		/*
		* Test process
		* 1. Create a private community
		* 2. Add the blog and ideationblog widgets.
		* 3. Add blog and ideablog entries
		* 4. Validate that Community Blog was created.   
		*/
		LOGGER.debug("BEGIN TEST: Add blogs widgets under impersonation.");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);	
		String comName = "Add blogs widgets under impersonation " + uniqueNameAddition;
		
		//Create private community
		Community newCommunity = new Community(comName, "", Permissions.PRIVATE, null );

		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertEquals(201, service.getRespStatus());
		assertTrue(communityResult != null);
		
		Community comm = new Community((Entry)service.getCommunity(communityResult.getEditLinkResolvedHref().toString()));
		
		//Add ideation blog widget
		Widget widget = new Widget(StringConstants.WidgetID.IdeationBlog.toString() );
		widget.setPublished(created);
		imCommService.postWidget(comm, widget.toEntry());
		assertEquals(200, service.getRespStatus());
		
		//Add blogs widget
		widget = new Widget(StringConstants.WidgetID.Blog.toString() );
		widget.setPublished(created);
		imCommService.postWidget(comm, widget.toEntry());
		assertEquals(200, service.getRespStatus());
				
		
		////////////////////////////////////////////////////////////////////////////////////////////////
		/* This is the start of the code to create an ideation blog via the widget.  The process is:
		 * 1. Get a feed of Community remote apps and get the link for IdeationBlog
		 * 2. GET the link.  This will return a service doc.  Parse the service doc and get the link for the community blogs feed
		 * 3. Ensure created blogs entry has impersonated author
		 */
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(comm.getRemoteAppsListHref(), true, null, 0, 50, null, null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null) ;
		assertTrue(remoteAppsFeed != null);
		
		String ideationBlogUrl = null;
		String blogUrl = null;
		for (Entry raEntry : remoteAppsFeed.getEntries()){
			for (Category category : raEntry.getCategories()){
				// For ideation blog
				if (category.getTerm().equalsIgnoreCase("IdeationBlog")){
					for(Link link : raEntry.getLinks()) {
						if( link.getRel().contains("remote-application/publish") ){
							ideationBlogUrl = link.getHref().toString();
						}						
					}
				}
				// For blog
				if (category.getTerm().equalsIgnoreCase("Blog")){
					for(Link link : raEntry.getLinks()) {
						if( link.getRel().contains("remote-application/publish") ){
							blogUrl = link.getHref().toString();
						}						
					}
				}				
			}
		}
		
		String blogsFeedUrl = null;
		ExtensibleElement ee = service.getAnyFeedWithRedirect(blogUrl);
		
		assertTrue(ee != null);
		Service svc = (Service)ee;
		for(Workspace ws : svc.getWorkspaces()) {
			for(Collection c : ws.getCollections()) {
				String href = c.getHref().toString();
				if(href != null && href.contains("commUuid")) {
					blogsFeedUrl = href;
					break;
				}
			}
		}
		assertTrue(blogsFeedUrl != null);
		
		String ideationBlogsFeedUrl = null;
		ee = service.getAnyFeedWithRedirect(ideationBlogUrl);
		
		assertTrue(ee != null);
		svc = (Service)ee;
		for(Workspace ws : svc.getWorkspaces()) {
			for(Collection c : ws.getCollections()) {
				String href = c.getHref().toString();
				if(href != null && href.contains("commUuid")) {
					ideationBlogsFeedUrl = href;
					break;
				}
			}
		}
		assertTrue(ideationBlogsFeedUrl != null);

		// Fetch blogs feed; verify impersonated author
		ee = service.getAnyFeedWithRedirect(blogsFeedUrl);
		assertEquals(" fetch blogs feed "+service.getDetail(), 200, service.getRespStatus());
		Feed blogsFeed = (Feed) ee;
		List<Entry> blogsEntries = blogsFeed.getEntries();
		assertTrue(blogsEntries != null);
		assertEquals(1, blogsEntries.size());
		
		Entry imBlogEntry = blogsEntries.get(0);
		assertEquals(imUser.getRealName(), imBlogEntry.getAuthor().getName());
		assertEquals("impersonated created time not match",created, imBlogEntry.getPublished()); 
		
		// Fetch ideation blogs feed; verify impersonated author
		Feed ideationBlogsFeed = (Feed) service.getAnyFeedWithRedirect(blogsFeedUrl);
		List<Entry> ideationBlogsEntries = ideationBlogsFeed.getEntries();
		assertTrue("impersonated creator not match", ideationBlogsEntries != null);
		assertEquals(1, ideationBlogsEntries.size());
		
		Entry imIdeationBlogEntry = ideationBlogsEntries.get(0);
		assertEquals("impersonated creator not match", imUser.getRealName(), imIdeationBlogEntry.getAuthor().getName());
		assertEquals("impersonated created time not match",created,imIdeationBlogEntry.getPublished()); 
	}	

	@Test
	public void commBlogAndIdeationBlogEntriesAndCommentsWithImpersonate() throws Exception {
		/*
		 * Test process 1. Create a private community 2. Add the blog and
		 * ideationblog widgets. 3. Add blog and ideablog entries with
		 *  different state & Validate state of ideas/entries were created.
		 *  4. Add comments to blog entries with different state & Validate 
		 *  state of comments were created.
		 */
		LOGGER.debug("BEGIN TEST: RTC 183484 Enhance POST Entry/Comment API to allow different ending state (PENDING/APPROVED).");
		
		// 1. Create a private community
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String comName = "RTC 183484 Enhance POST Entry/Comment API "
				+ uniqueNameAddition;

		Community newCommunity = new Community(
				comName,
				"Private community blog/ideation blog for Enhancement of POST Entry/Comment API.",
				Permissions.PRIVATE, null);

		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		Community comm = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		// 2. Add the blog and ideationblog widgets.
		// 2.1 Add ideation blog widget
		Widget widget = new Widget(
				StringConstants.WidgetID.IdeationBlog.toString());
		service.postWidget(comm, widget.toEntry());
		assertEquals(201, service.getRespStatus());

		// 2.2 Add blogs widget
		Feed widgetsInitialFeed = (Feed) service.getCommunityWidgets(comm.getUuid());
		boolean blogFound = false;
		for (Entry e : widgetsInitialFeed.getEntries()) {
			if (e.getTitle().equals("Blog")) {
				blogFound = true;
				break;
			}
		}
		if (blogFound == false) {
			widget = new Widget(StringConstants.WidgetID.Blog.toString());
			service.postWidget(comm, widget.toEntry());
			assertEquals(201, service.getRespStatus());
		}

		// /////////////////////////////////////////////////////////////////////////
		/*
		 * 1. Get a feed of Community remote apps and get the link for IdeationBlog 
		 * 2. Execute the link. This will return a service doc. Parse the service 
		 * doc and get the link for posting to Blogs/Ideation Blogs
		 */
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				comm.getRemoteAppsListHref(), true, null, 0, 50, null, null,
				SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);

		String ideationBlogUrl = null;
		String blogUrl = null;
		for (Entry raEntry : remoteAppsFeed.getEntries()) {
			for (Category category : raEntry.getCategories()) {
				// For ideation blog
				if (category.getTerm().equalsIgnoreCase("IdeationBlog")) {
					for (Link link : raEntry.getLinks()) {
						if (link.getRel()
								.contains("remote-application/publish")) {
							ideationBlogUrl = link.getHref().toString();
						}
					}
				}
				// For blog
				if (category.getTerm().equalsIgnoreCase("Blog")) {
					for (Link link : raEntry.getLinks()) {
						if (link.getRel()
								.contains("remote-application/publish")) {
							blogUrl = link.getHref().toString();
						}
					}
				}
			}
		}

		// This call returns a service doc, not a feed.
		String postIdeaBlogUrl = null;
		ExtensibleElement ideaEE = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ideaEE = service.getAnyFeedWithRedirect(ideationBlogUrl);
		} else {
			ideaEE = service.getAnyFeed(ideationBlogUrl);
		}
		assertTrue(ideaEE != null);
		// For ideation blog
		// TODO Use the Service document class to parse. No need to use strings
		// to locate things.
		for (Element ele : ideaEE.getElements()) {
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
		
		// For Blogs
		String postBlogUrl = null;
		ExtensibleElement blogEE = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			blogEE = service.getAnyFeedWithRedirect(blogUrl);
		} else {
			blogEE = service.getAnyFeed(blogUrl);
		}
		assertTrue(blogEE != null);
		for (Element ele : blogEE.getElements()) {
			if (ele.toString().startsWith("<workspace")) {
				for (Element ele2 : ele.getElements()) {
					if (ele2.toString().startsWith("<collection")) {
						for (QName atrb : ele2.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("href")
									&& ele2.getAttributeValue("href").contains(
											"api/entries")) {
								postBlogUrl = ele2.getAttributeValue("href");
							}
						}
					}
				}
			}
		}
		
		// 3. Add blog and ideablog entries with different state
		Factory factory = abdera.getFactory();
		ExtensibleElement ee = null;
		// 3.1 Create the Ideation blog idea
		// 3.1.1 idea with PENDING state
		Entry ideaWithPending = factory.newEntry();
		ideaWithPending.setTitle("ideaWithPending 1");
		ideaWithPending.setId("some-id-should-be-ignored");
		ideaWithPending.setContent("ideaWithPending content");
		ideaWithPending.addCategory("DataPopIdeaBlogEntry");
		
		Element elmt = ideaWithPending.addExtension(StringConstants.SNX_MODERATION);
		elmt.setAttributeValue("status", "pending");
		ideaWithPending.addExtension(elmt);
		
		otherUserImService.postBlogsFeed(postIdeaBlogUrl, ideaWithPending);
		assertEquals("post commuinty blog", 403,
				otherUserImService.getRespStatus());

		ee = blogsService.postBlogsFeed(postIdeaBlogUrl, ideaWithPending);
		assertEquals("post commuinty blog", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post ideation blog entry with PENDING state", "pending", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));
		
		// 3.1.2 idea with APPROVED state
		Entry ideaWithApproved = factory.newEntry();
		ideaWithApproved.setTitle("ideaWithApproved 1");
		ideaWithApproved.setId("some-id-should-be-ignored");
		ideaWithApproved.setContent("ideaWithApproved content");
		ideaWithApproved.addCategory("DataPopIdeaBlogEntry");
		
		elmt = ideaWithApproved.addExtension(StringConstants.SNX_MODERATION);
		elmt.setAttributeValue("status", "approved");
		ideaWithApproved.addExtension(elmt);

		ee = blogsService.postBlogsFeed(postIdeaBlogUrl, ideaWithApproved);
		ideaEE = ee; // further comments use
		assertEquals("post commuinty blog", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post ideation blog entry with APPROVED state", "approved", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));
		
		// 3.1.3 idea with WRONG state
		Entry ideaWithWrongState = factory.newEntry();
		ideaWithWrongState.setTitle("ideaWithWrongState 1");
		ideaWithWrongState.setId("some-id-should-be-ignored");
		ideaWithWrongState.setContent("ideaWithWrongState content");
		ideaWithWrongState.addCategory("DataPopIdeaBlogEntry");
		
		elmt = ideaWithWrongState.addExtension(StringConstants.SNX_MODERATION);
		elmt.setAttributeValue("status", "wrongStatus");
		ideaWithWrongState.addExtension(elmt);

		ee = blogsService.postBlogsFeed(postIdeaBlogUrl, ideaWithWrongState);
		assertEquals("post commuinty blog", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post ideation blog entry with APPROVED state", "approved", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));
		
		// 3.1.4 idea with NO state
		Entry ideaWithNoState = factory.newEntry();
		ideaWithNoState.setTitle("ideaWithNoState 1");
		ideaWithNoState.setId("some-id-should-be-ignored");
		ideaWithNoState.setContent("ideaWithNoState content");
		ideaWithNoState.addCategory("DataPopIdeaBlogEntry");

		ee = blogsService.postBlogsFeed(postIdeaBlogUrl, ideaWithNoState);
		assertEquals("post commuinty blog", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post ideation blog entry with APPROVED state", "approved", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));

		// 3.2 Create the blog entry
		// 3.2.1 blogEntry with PENDING state
		Entry blogEntryWithPending = factory.newEntry();
		blogEntryWithPending.setTitle("blogEntryWithPending 1");
		blogEntryWithPending.setId("some-id-should-be-ignored");
		blogEntryWithPending.setContent("blogEntryWithPending content");
		blogEntryWithPending.addCategory("DataPopBlogEntry");
		
		elmt = blogEntryWithPending.addExtension(StringConstants.SNX_MODERATION);
		elmt.setAttributeValue("status", "pending");
		blogEntryWithPending.addExtension(elmt);
		
		otherUserImService.postBlogsFeed(postBlogUrl, blogEntryWithPending);
		assertEquals("post commuinty blog", 403,
				otherUserImService.getRespStatus());

		ee = blogsService.postBlogsFeed(postBlogUrl, blogEntryWithPending);
		assertEquals("post commuinty blog", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post blog entry with PENDING state", "pending", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));
		
		// 3.2.2 blogEntry with APPROVED state
		Entry blogEntryWithApproved = factory.newEntry();
		blogEntryWithApproved.setTitle("blogEntryWithApproved 1");
		blogEntryWithApproved.setId("some-id-should-be-ignored");
		blogEntryWithApproved.setContent("blogEntryWithApproved content");
		blogEntryWithApproved.addCategory("DataPopBlogEntry");
		
		elmt = blogEntryWithApproved.addExtension(StringConstants.SNX_MODERATION);
		elmt.setAttributeValue("status", "approved");
		blogEntryWithApproved.addExtension(elmt);

		ee = blogsService.postBlogsFeed(postBlogUrl, blogEntryWithApproved);
		blogEE = ee; // further comments use
		assertEquals("post commuinty blog", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post blog entry with APPROVED state", "approved", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));
		
		// 3.2.3 blogEntry with WRONG state
		Entry blogEntryWithWrongState = factory.newEntry();
		blogEntryWithWrongState.setTitle("blogEntryWithWrongState 1");
		blogEntryWithWrongState.setId("some-id-should-be-ignored");
		blogEntryWithWrongState.setContent("blogEntryWithWrongState content");
		blogEntryWithWrongState.addCategory("DataPopBlogEntry");
		
		elmt = blogEntryWithWrongState.addExtension(StringConstants.SNX_MODERATION);
		elmt.setAttributeValue("status", "wrongStatus");
		blogEntryWithWrongState.addExtension(elmt);

		ee = blogsService.postBlogsFeed(postBlogUrl, blogEntryWithWrongState);
		assertEquals("post commuinty blog", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post blog entry with APPROVED state", "approved", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));
		
		// 3.2.4 blogEntry with NO state
		Entry blogEntryWithNoState = factory.newEntry();
		blogEntryWithNoState.setTitle("blogEntryWithNoState 1");
		blogEntryWithNoState.setId("some-id-should-be-ignored");
		blogEntryWithNoState.setContent("blogEntryWithNoState content");
		blogEntryWithNoState.addCategory("DataPopIdeaBlogEntry");

		ee = blogsService.postBlogsFeed(postBlogUrl, blogEntryWithNoState);
		assertEquals("post commuinty blog", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post blog entry with APPROVED state", "approved", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));
		
		// 4. Add comments to blog entries with different state
		String postBlogCommentUrl = null;
		String postBlogEntryId = null;
		String postBlogEntryHref = null;
		
		for (Element ele : blogEE.getElements()) {
			if (ele.toString().startsWith("<id"))
				postBlogEntryId = ele.getText();
			for (QName atrb : ele.getAttributes()) {
				if (atrb.toString().equalsIgnoreCase("rel")
						&& ele.getAttributeValue("rel").equalsIgnoreCase("alternate")
						&& ele.getAttributeValue("href").contains("/entry/")) {
					postBlogEntryHref = ele.getAttributeValue("href");
				}
			}
		}
		postBlogCommentUrl = postBlogEntryHref.substring(0, postBlogEntryHref.indexOf("/entry/")) + "/api/comments";
		
		// 4.1 blogComment with PENDING state
		Entry blogCommentWithPending = factory.newEntry();
		blogCommentWithPending.setTitle("some-title-should-be-ignored");
		blogCommentWithPending.setId("some-id-should-be-ignored");
		blogCommentWithPending.setContent("blogCommentWithPending content");
		elmt = blogCommentWithPending.addSimpleExtension(
				"http://purl.org/syndication/thread/1.0", "in-reply-to", "thr",
				"");
		elmt.setAttributeValue("ref", postBlogEntryId);
		elmt.setAttributeValue("href", postBlogEntryHref);

		elmt = blogCommentWithPending.addExtension(StringConstants.SNX_MODERATION);
		elmt.setAttributeValue("status", "pending");
		blogCommentWithPending.addExtension(elmt);
		
		ee = blogsService.postBlogsFeed(postBlogCommentUrl, blogCommentWithPending);
		assertEquals("post commuinty blog comment", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post blog entry with PENDING state", "pending", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));
		
		// 4.2 blogComment with APPROVED state
		Entry blogCommentWithApproved = factory.newEntry();
		blogCommentWithApproved.setTitle("some-title-should-be-ignored");
		blogCommentWithApproved.setId("some-id-should-be-ignored");
		blogCommentWithApproved.setContent("blogCommentWithApproved content");
		elmt = blogCommentWithApproved.addSimpleExtension(
				"http://purl.org/syndication/thread/1.0", "in-reply-to", "thr",
				"");
		elmt.setAttributeValue("ref", postBlogEntryId);
		elmt.setAttributeValue("href", postBlogEntryHref);

		elmt = blogCommentWithApproved.addExtension(StringConstants.SNX_MODERATION);
		elmt.setAttributeValue("status", "approved");
		blogCommentWithApproved.addExtension(elmt);
		
		ee = blogsService.postBlogsFeed(postBlogCommentUrl, blogCommentWithApproved);
		assertEquals("post commuinty blog comment", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post blog entry with APPROVED state", "approved", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));
		
		// 4.3 blogComment with WRONG state
		Entry blogCommentWithWrongState = factory.newEntry();
		blogCommentWithWrongState.setTitle("some-title-should-be-ignored");
		blogCommentWithWrongState.setId("some-id-should-be-ignored");
		blogCommentWithWrongState.setContent("blogCommentWithWrongState content");
		elmt = blogCommentWithWrongState.addSimpleExtension(
				"http://purl.org/syndication/thread/1.0", "in-reply-to", "thr",
				"");
		elmt.setAttributeValue("ref", postBlogEntryId);
		elmt.setAttributeValue("href", postBlogEntryHref);

		elmt = blogCommentWithWrongState.addExtension(StringConstants.SNX_MODERATION);
		elmt.setAttributeValue("status", "wrongState");
		blogCommentWithWrongState.addExtension(elmt);
		
		ee = blogsService.postBlogsFeed(postBlogCommentUrl, blogCommentWithWrongState);
		assertEquals("post commuinty blog comment", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post blog entry with WRONG state", "approved", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));
		
		// 4.4 blogComment with NO state
		Entry blogCommentWithNoState = factory.newEntry();
		blogCommentWithNoState.setTitle("some-title-should-be-ignored");
		blogCommentWithNoState.setId("some-id-should-be-ignored");
		blogCommentWithNoState.setContent("blogCommentWithNoState content");
		elmt = blogCommentWithNoState.addSimpleExtension(
				"http://purl.org/syndication/thread/1.0", "in-reply-to", "thr",
				"");
		elmt.setAttributeValue("ref", postBlogEntryId);
		elmt.setAttributeValue("href", postBlogEntryHref);
		
		ee = blogsService.postBlogsFeed(postBlogCommentUrl, blogCommentWithNoState);
		assertEquals("post commuinty blog comment", 201, blogsService.getRespStatus());
		assertEquals("impersonate User name not match", imUser.getRealName(),
				((Entry) ee).getAuthor().getName());
		// Validate
		assertEquals("Post blog entry with No state", "approved", ee.getExtension(StringConstants.SNX_MODERATION).getAttributeValue("status"));
		
		LOGGER.debug("END TEST: RTC 183484 Enhance POST Entry/Comment API to allow different ending state (PENDING/APPROVED).");
	}
	
	@Test
	public void communityFilesCrossOrg() throws IOException {
		super.communityFilesCrossOrg();
	}

	// TJB 1/30/15 Not operative with Impersonation SC because GET is not
	// supported. We need to use local user, but that will conflict
	// with non-impersonation version of this test. We need to use GET with user
	// where x-lconn-runas header is not set
	// otherwise it changes the content of the feed and causes validation error
	// at the end of the test - see step 8 and
	// defect 143411
	@Test
	public void ideationCanEditComment() throws Exception {
		// super.ideationCanEditComment();
	}

	@Test
	public void privateCommunityBlog() throws Exception {
		super.privateCommunityBlog();
	}

	@Test
	public void removeFileShare() throws FileNotFoundException {
		super.removeFileShare();
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
		filesService.tearDown();
	}

}