package com.ibm.lconn.automation.framework.services.communities;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.ModerationService;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommentToModerate;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public abstract class CommunityBlogsFilesTestBase {

	protected static Abdera abdera = new Abdera();
	protected final static Logger LOGGER = LoggerFactory
			.getLogger(CommunityBlogsFilesTestBase.class.getName());
	protected static UserPerspective user, imUser, otherUser, connectionsAdminUser;
	protected static CommunitiesService service, adminService;
	protected static BlogsService blogsService, blogsotherUserService;
	protected static String blogHomepageHandle;
	protected static FilesService filesService, filesAdminService;

	protected static ModerationService modServiceFiles, modServiceBlogs;

	// NOTE: this test requires that the blogs widget has been added to
	// communities
	// this test also requires moderation is enabled on the server
	// however will still create comments and get some testing done if
	// blogs widget is there, but moderation is not
	@Test
	public void moderateBlogComments() throws Exception {

		// tjb 3/28/14 - Blocked on SC because the UserPerspective instantiates
		// Blogs service object, but blogs is unavailable on SC.
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			String timeStamp = Utils.logDateFormatter.format(new Date());

			// add login user blogs service here
			// UserPerspective currentUser = new UserPerspective
			// (StringConstants.CURRENT_USER, Component.BLOGS.toString(),
			// useSSL);

			// TODO enable this test case
			LOGGER.debug("TEST: moderateBlogComments");

			Community newCommunity = new Community("CommBlog" + timeStamp,
					"Commnities Blogs Test", Permissions.PUBLIC,
					"tagCommunities_"
							+ Utils.logDateFormatter.format(new Date()));
			Entry communityResult = (Entry) service
					.createCommunity(newCommunity);
			assertTrue(communityResult != null);

			Community community = new Community(
					(Entry) service.getCommunity(communityResult
							.getEditLinkResolvedHref().toString()));

			Blog regBlog = new Blog(
					community.getTitle(),
					"API Mod Test Blog",
					"This blog is for testing and verifying community blog moderation",
					"tagBlogs_" + Utils.logDateFormatter.format(new Date()),
					true, false, null, null, TimeZone.getDefault(), true, 13,
					true, true, true, 0, -1, null, community.getUuid(), null, 0);
			Entry regBlogEntry = (Entry) blogsService.createBlog(regBlog);
			assertEquals("create community blog", 201,
					blogsService.getRespStatus());
			Blog regBlogResult = new Blog(regBlogEntry);
			assertTrue(regBlogResult != null);

			// create post
			BlogPost blogPost = new BlogPost("API Mod Test Post",
					"Test blog post from API", "first post blog", true, 5);
			Entry parent = (Entry) blogsService.createPost(regBlogResult,
					blogPost);

			// add a comment
			BlogComment blogComment = new BlogComment(
					"API Mod Test Comment - this is a comment from blog owner",
					parent);
			blogsService.createComment(regBlogResult, blogComment);

			// String commentURL =
			// blogsService.getCommentURL(regBlog.getTitle());
			String commentURL = regBlogResult.getAlternateHref()
					+ URLConstants.BLOGS_COMMENTS;

			// another user post comment
			// UserPerspective user2 = new UserPerspective (4,
			// Component.BLOGS.toString(), useSSL);

			BlogComment blogComment1 = new BlogComment(
					"API Comment - please accept", parent);
			blogsotherUserService.postComment(commentURL, blogComment1);

			BlogComment blogComment2 = new BlogComment(
					"API Comment - please reject", parent);
			blogsotherUserService.postComment(commentURL, blogComment2);

			// moderate blog comments
			if (!StringConstants.MODERATION_ENABLED) {
				LOGGER.warn("moderation of blog comments skipped as moderation was not specified as enabled on server.");
			} else {
				assertTrue(handleModeration(community.getUuid(), false));
			}
		}

	}

	// @Test
	public void ideationCanEditComment() throws Exception {
		/**
		 * Owner and member create a comment in ideation blog. Then the blog is
		 * frozen. When member accesses the comment, both
		 * canEditComment/canDeleteComment is false.
		 * 
		 * POST and PUT not supported for updating
		 * canEditComment/canDeleteComment.
		 * 
		 * Process Step 1: Create community Step 2: Add a member who is not an
		 * owner. Step 3: Add the ideation widget. Step 4: Create ideation blog
		 * entry. Step 5: Create comments for owner and member. Step 6: Validate
		 * edit/delete rights before ideation blog is frozen. Access is open for
		 * both users. Step 7: Feeze the ideation blog. Step 8: Validate the
		 * values of canEditComment and canDeleteComment. For non owner - both
		 * canEdit/canDelete are false.
		 * 
		 */

		LOGGER.debug("START TEST: RTC 109274 Add support in blogs feed to indicate whether a blog comment is editable or delete-able");
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String communityName = "RTC 109274 Ideation Blog test "
				+ uniqueNameAddition;
		// UserPerspective testUser = new UserPerspective(5,
		// Component.COMMUNITIES.toString(), useSSL);

		LOGGER.debug("Step 1: Create Community");
		Community newCommunity = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			newCommunity = new Community(communityName,
					"Ideation test for canEditComment and canDeleteComment",
					Permissions.PRIVATE, null);
		} else {
			newCommunity = new Community(communityName,
					"Ideation test for canEditComment and canDeleteComment",
					Permissions.PUBLIC, null);
		}
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		assertTrue(communityResult != null);

		Community comm = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		LOGGER.debug("Step 2: Add a member who is not an owner.");
		Member member = new Member(null, otherUser.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);
		member = new Member(otherUser.getEmail(), otherUser.getUserId(),
				Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);

		service.addMemberToCommunity(comm, member);
		assertEquals(" Add Community Member ", 201, service.getRespStatus());

		LOGGER.debug("Step 3: Add the ideation widget.");
		Widget widget = new Widget(
				StringConstants.WidgetID.IdeationBlog.toString());
		service.postWidget(comm, widget.toEntry());
		assertEquals(201, service.getRespStatus());

		/*
		 * This is the start of the code to create an ideation blog via the
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

		// What we are trying to get is the link used to post to IdeationBlog
		// widget in communities.
		// The blogHandleUrl is used for voting.
		String postIdeaBlogUrl = null;
		String blogHandleUrl = null;
		Service ee = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			ee = (Service) service.getAnyFeedWithRedirect(ideationBlogUrl);
		} else {
			ee = (Service) service.getAnyFeed(ideationBlogUrl);
		}

		for (Workspace ws : ee.getWorkspaces()) {
			for (Collection c : ws.getCollections()) {
				for (QName atrb : c.getAttributes()) {
					if (atrb.toString().equalsIgnoreCase("href")
							&& c.getAttributeValue("href").contains(
									"api/entries")) {
						postIdeaBlogUrl = c.getAttributeValue("href");
					}
					if (atrb.toString().equalsIgnoreCase("href")
							&& c.getAttributeValue("href").contains(
									"blogType=ideationblog")) {
						blogHandleUrl = c.getAttributeValue("href");
					}
				}
			}
		}

		Factory factory = abdera.getFactory();
		Entry entry = factory.newEntry();
		entry.setTitle("RTC 109274 Idea1 " + uniqueNameAddition);
		entry.setId("some-id-should-be-ignored");
		entry.setContent("idea1 content");
		entry.addCategory("DataPopIdeaBlogEntry");

		LOGGER.debug("Step 4: Create ideation blog entry.");
		Entry postResult = (Entry) service.postIdeationBlog(postIdeaBlogUrl,
				entry);
		BlogPost response = new BlogPost(postResult);

		LOGGER.debug("Step 5: Create comments for owner and member.");
		// UserPerspective currentUser = new UserPerspective
		// (StringConstants.CURRENT_USER, Component.BLOGS.toString(), useSSL);
		// UserPerspective memberUser = new UserPerspective (5,
		// Component.BLOGS.toString(), useSSL);
		BlogComment userComment = new BlogComment(
				"This is the blog comment from blog owner - created in communities.",
				postResult);
		// Entry ownerComment = (Entry)
		// currentUser.getBlogsService().createComment(response, userComment);
		Entry ownerComment = (Entry) blogsService.createComment(response,
				userComment);

		String wIdExtra = postIdeaBlogUrl.substring(
				postIdeaBlogUrl.indexOf("blogs/"),
				postIdeaBlogUrl.indexOf("/api"));
		String wId = wIdExtra.substring(wIdExtra.indexOf("/") + 1);

		// RTC 143411 before create the comment, need to force membership sync
		// on UI
		// https://apps.collabservsvt3.swg.usma.ibm.com/blogs/roller-ui/rendering/feed/c3b633bd-e5a0-4070-8afa-06e55863d6de/entries/atom?ps=5&page=0&sortby=0&order=desc&cache=false&fromCommunity=true&lang=en_us&user=40657654&isMember=true&preventCache=1421822093324
		// is requested to do membership sync
		String syncUrl = URLConstants.SERVER_URL
				+ "/blogs/roller-ui/rendering/feed/"
				+ wId
				+ "/entries/atom?ps=5&page=0&sortby=0&order=desc&cache=false&fromCommunity=true&lang=en_us&isMember=true&preventCache=1421822093324";
		ClientResponse cr = blogsotherUserService.getResponse(syncUrl);
		assertEquals("response status", 200, cr.getStatus());
		// end membership sync

		BlogComment memberComment = new BlogComment(
				"Non owner comment - created in communities.", postResult);
		Entry memResponse = (Entry) blogsotherUserService.createComment(
				response, memberComment);
		assertEquals("Create comment", 201,
				blogsotherUserService.getRespStatus());

		Feed feed = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			// postIdeaBlogUrl
			// https://apps.collabservdaily.swg.usma.ibm.com/blogs/W4c9e0c0a8a3b_45de_ace5_74ff33a02eea/api/entries
			String urlIdeationFeedUrl = URLConstants.SERVER_URL
					+ "/blogs/roller-ui/rendering/feed/" + wId
					+ "/entries/atom";

			// need this
			// /blogs/roller-ui/rendering/feed/W7da2e3463740_4c6d_9c18_452f0ff7c7ac/entries/atom?lang=en_us
			feed = (Feed) service.getAnyFeedWithRedirect(urlIdeationFeedUrl);
		}

		String editUrl = "";
		Feed fd = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
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
				if (ibEntry.getTitle().equalsIgnoreCase(communityName)) {
					// Get the edit link that looks like this:
					// https://lc45linux2.swg.usma.ibm.com:443/blogs/Handle03913360811/api/blogs/70241e8a-8658-4d5e-a2db-eb0b83783ab9
					editUrl = ibEntry.getEditLinkResolvedHref().toURL()
							.toString();
				}
			}
		}

		String ownerHref = "";
		for (Element ell1 : ownerComment.getElements()) {
			if (ell1.toString().startsWith("<app:collection")) {
				ownerHref = ell1.getAttributeValue(StringConstants.ATTR_HREF);
			}
		}

		String memHref = "";
		for (Element ell2 : memResponse.getElements()) {
			if (ell2.toString().startsWith("<app:collection")) {
				memHref = ell2.getAttributeValue(StringConstants.ATTR_HREF);
			}
		}

		// Remove "recommend" to get comment link
		String ownerCommentLink = ownerHref.replace("recommend/", "");
		String memCommentLink = memHref.replace("recommend/", "");

		LOGGER.debug("Step 6: Validate edit/delete rights before ideation blog is frozen.");
		// Orig ExtensibleElement ownerEE_test1 =
		// currentUser.getBlogsService().getBlogFeed(ownerCommentLink);
		ExtensibleElement ownerEE_test1 = blogsService
				.getBlogFeedWithRedirect(ownerCommentLink);
		assertEquals(
				true,
				ownerEE_test1.getSimpleExtension(
						StringConstants.SNX_EDIT_COMMENT).equalsIgnoreCase(
						"true"));
		assertEquals(
				true,
				ownerEE_test1.getSimpleExtension(
						StringConstants.SNX_DELETE_COMMENT).equalsIgnoreCase(
						"true"));

		// ExtensibleElement memEE_test1 =
		// memberUser.getBlogsService().getBlogFeed(memCommentLink);
		ExtensibleElement memEE_test1 = blogsotherUserService
				.getBlogFeedWithRedirect(memCommentLink);
		assertEquals(true,
				memEE_test1
						.getSimpleExtension(StringConstants.SNX_EDIT_COMMENT)
						.equalsIgnoreCase("true"));
		assertEquals(
				true,
				memEE_test1.getSimpleExtension(
						StringConstants.SNX_DELETE_COMMENT).equalsIgnoreCase(
						"true"));

		LOGGER.debug("Step 7: Feeze the ideation blog.");
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

		LOGGER.debug("Step 8: Validate the values of canEditComment and canDeleteComment depending on who accesses comment. ");
		// Validations: These are in relation to the comment made by member
		// ExtensibleElement ownerEE_test2 =
		// currentUser.getBlogsService().getBlogFeed(ownerCommentLink);
		ExtensibleElement ownerEE_test2 = blogsService
				.getBlogFeedWithRedirect(ownerCommentLink);
		assertEquals(
				true,
				ownerEE_test2.getSimpleExtension(
						StringConstants.SNX_EDIT_COMMENT).equalsIgnoreCase(
						"false"));
		assertEquals(
				true,
				ownerEE_test2.getSimpleExtension(
						StringConstants.SNX_DELETE_COMMENT).equalsIgnoreCase(
						"true"));

		// Non-owner
		// ExtensibleElement memEE_test2 =
		// memberUser.getBlogsService().getBlogFeed(memCommentLink);
		if (!StringConstants.MODERATION_ENABLED) {
			ExtensibleElement memEE_test2 = blogsotherUserService
					.getBlogFeedWithRedirect(memCommentLink);
			assertEquals(
					true,
					memEE_test2.getSimpleExtension(
							StringConstants.SNX_EDIT_COMMENT).equalsIgnoreCase(
							"false"));
			assertEquals(
					true,
					memEE_test2.getSimpleExtension(
							StringConstants.SNX_DELETE_COMMENT)
							.equalsIgnoreCase("false"));
		}

		LOGGER.debug("END TEST: RTC 109274 Add support in blogs feed to indicate whether a blog comment is editable or delete-able");
	}

	@Test
	public void privateCommunityBlog() throws Exception {
		// tb 9/18/13 this test hangs or breaks the next test on SC. Temp hack
		// to prevent execution on SC deployments.
		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			/*
			 * Test process 
			 * 1. Create a private community 
			 * 2. Add the blog and ideationblog widgets. 
			 * 3. Add blog and ideablog entries 
			 * 4. Generate a homepage feed, validate blog and ideablog entries by
			 * confirming <category> element.
			 */

			LOGGER.debug("BEGIN TEST: RTC 83984 Private community blog/ideation blog is not returned in feed.");
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

			// Create private community
			Community newCommunity = new Community(
					"RTC 83984 ideation blog is not in feed "
							+ uniqueNameAddition,
					"Private community blog/ideation blog is not returned in feed.",
					Permissions.PRIVATE, null);

			Entry communityResult = (Entry) service
					.createCommunity(newCommunity);
			assertTrue(communityResult != null);

			Community comm = new Community(
					(Entry) service.getCommunity(communityResult
							.getEditLinkResolvedHref().toString()));

			// For verify defect 179983
			String updated = comm.getUpdated().toString();
			
			// Add ideation blog widget
			Widget widget = new Widget(
					StringConstants.WidgetID.IdeationBlog.toString());
			service.postWidget(comm, widget.toEntry());
			assertEquals("postWidget"+service.getDetail(), 201, service.getRespStatus());

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
				widget = new Widget(StringConstants.WidgetID.Blog.toString());
				service.postWidget(comm, widget.toEntry());
				assertEquals("postWidget"+service.getDetail(), 201, service.getRespStatus());
			}

			// For verify defect 179983
			Community commAfterAddBlogWidget = new Community(
					(Entry) service.getCommunity(communityResult
							.getEditLinkResolvedHref().toString()));
			String updatedAfterAddBlogWidget = commAfterAddBlogWidget.getUpdated().toString();
			
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
			assertTrue("getCommunityRemoteAPPs"+service.getDetail(), remoteAppsFeed != null);

			String ideationBlogUrl = null;
			String blogUrl = null;
			for (Entry raEntry : remoteAppsFeed.getEntries()) {
				for (Category category : raEntry.getCategories()) {
					// For ideation blog
					if (category.getTerm().equalsIgnoreCase("IdeationBlog")) {
						for (Link link : raEntry.getLinks()) {
							if (link.getRel().contains(
									"remote-application/publish")) {
								ideationBlogUrl = link.getHref().toString();
							}
						}
					}
					// For blog
					if (category.getTerm().equalsIgnoreCase("Blog")) {
						for (Link link : raEntry.getLinks()) {
							if (link.getRel().contains(
									"remote-application/publish")) {
								blogUrl = link.getHref().toString();
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
			ExtensibleElement ee = null;
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				// Document<ExtensibleElement> feed_doc =
				// client.get(ideationBlogUrl).getDocument();
				// ee = feed_doc.getRoot();
				ee = service.getAnyFeedWithRedirect(ideationBlogUrl);
			} else {
				ee = service.getAnyFeed(ideationBlogUrl);
			}
			assertTrue(ee != null);
			// For ideation blog
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
							}
						}
					}
				}
			}

			// For Blogs
			String postBlogUrl = null;
			ExtensibleElement blogEE = null;
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				// Document<ExtensibleElement> feed_doc =
				// client.get(blogUrl).getDocument();
				// blogEE = feed_doc.getRoot();
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
										&& ele2.getAttributeValue("href")
												.contains("api/entries")) {
									postBlogUrl = ele2
											.getAttributeValue("href");
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
			 * xmlns:snx="http://www.ibm.com/xmlns/prod/sn" enabled="yes"
			 * days="0"/> </app:control>
			 */
			// Ideation blog entry
			Factory factory = abdera.getFactory();
			Entry entry = factory.newEntry();
			entry.setTitle("RTC83984 IdeaBlog 1");
			entry.setId("some-id-should-be-ignored");
			entry.setContent("idea1 content");
			entry.addCategory("DataPopIdeaBlogEntry");

			// Blog entry
			Entry blogEntry = factory.newEntry();
			blogEntry.setTitle("RTC83984 Blog 1 Created in Communities");
			blogEntry.setId("some-id-should-be-ignored");
			blogEntry.setContent("blog1 content");
			blogEntry.addCategory("DataPopBlogEntry");

			// Create the ideation blog
			// TB Smart Cloud Test stops here - hangs on next line.
			// Commented-out for now.
			if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
				service.postIdeationBlog(postIdeaBlogUrl, entry);
				assertEquals("post ideation blog", 201, service.getRespStatus());

				// Create the blog
				service.postBlog(postBlogUrl, blogEntry);
				assertEquals("post commuinty blog", 201,
						service.getRespStatus());

				// UserPerspective user2 = new UserPerspective (2,
				// Component.BLOGS.toString(), useSSL);
				// String blogHomepageHandle=
				// user2.getBlogsService().getBlogsHomepageHandle();
				String commLink = comm.getSelfLink().toString();
				String commUuid = commLink.substring(commLink.indexOf("=") + 1,
						commLink.length());

				// For verify defect 179983
				Community commAfterAddBlog = new Community((Entry) service.getCommunity(commLink));
				String updatedAfterAddBlog = commAfterAddBlog.getUpdated().toString();
				// Use this url
				// http://lc45linux2.swg.usma.ibm.com/blogs/Handle04306513424/feed/blogs/atom?commUuid=8332d7da-cc90-4ffe-931b-43b72ceda4b8&lang=en_us

				String server = URLConstants.SERVER_URL;
				// Don't like doing this, but dev specifies this URL in defect.
				// Don't know where/how to get it programmatically.
				String testUrl = server + "/blogs/" + blogHomepageHandle
						+ "/feed/blogs/atom?commUuid=" + commUuid;

				ExtensibleElement eele;
				if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
					// Document<Feed> feed_doc =
					// client.get(testUrl).getDocument();
					// validationFeed = feed_doc.getRoot();
					eele = service.getAnyFeedWithRedirect(testUrl);
				} else {
					// service.getApiLogger().debug(CommunitiesPopulate.class.getName());
					eele = service.getAnyFeed(testUrl);
				}
				assertEquals("get commuinty blogs feed", 200,
						service.getRespStatus());
				Feed validationFeed = (Feed) eele;

				// Validate contents <category term="ideationblog" and <category
				// term="communityblog"
				boolean foundBlog = false;
				boolean foundIdeaBlog = false;
				for (Entry vEntry : validationFeed.getEntries()) {
					if (vEntry.getTitle().equalsIgnoreCase(
							"RTC 83984 ideation blog is not in feed "
									+ uniqueNameAddition)) {
						for (Element ele : vEntry.getElements()) {
							if (ele.toString().startsWith("<category")) {
								for (QName atrb : ele.getAttributes()) {
									if (atrb.toString()
											.equalsIgnoreCase("term")
											&& ele.getAttributeValue("term")
													.equalsIgnoreCase(
															"communityblog")) {
										assertTrue(ele
												.getAttributeValue("term")
												.equalsIgnoreCase(
														"communityblog"));
										foundBlog = true;
									}
									if (atrb.toString()
											.equalsIgnoreCase("term")
											&& ele.getAttributeValue("term")
													.equalsIgnoreCase(
															"ideationblog")) {
										assertTrue(ele
												.getAttributeValue("term")
												.equalsIgnoreCase(
														"ideationblog"));
										foundIdeaBlog = true;
									}
								}
							}
						}
					}
				}

				// Just a final check to make sure both blog and ideation blog
				// entries were found/verified.
				assertTrue(foundBlog && foundIdeaBlog);
				LOGGER.debug("END TEST: RTC 83984 Private community blog/ideation blog is not returned in feed.");
			}// end SmartCloud 'if'
		}// end SmartCloud hack to keep this test from running on SC
	}

	@Test
	public void createCommunityFile() throws FileNotFoundException, IOException {

		LOGGER.debug("create Community File:");

		String timeStamp = Utils.logDateFormatter.format(new Date());
		Community community = getCreatedPrivateCommunity("CommunityFileTest"
				+ timeStamp);

		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				community.getRemoteAppsListHref(), true, null, 0, 50, null,
				null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
		assertTrue(remoteAppsFeed != null);

		/*
		 * String publishLink = ""; for (Entry entry :
		 * remoteAppsFeed.getEntries()){ for (Category category :
		 * entry.getCategories()){ if
		 * (category.getTerm().equalsIgnoreCase("Files")) {
		 * 
		 * // get the publish link publishLink =
		 * entry.getLink(StringConstants.REL_PUBLISH).getHref().toString();
		 * break; } } }
		 */

		String file_path = "/resources/Jellyfish.jpg";
		InputStream is = this.getClass().getResourceAsStream(file_path);
		String testFilename = file_path
				.substring(file_path.lastIndexOf("/") + 1);
		// FileEntry testFileEntry = new FileEntry(null, testFilename, content,
		// "The Bond Car", "Sick Ride!", Permissions.PUBLIC, true,
		// Notification.ON, Notification.ON, null, null, true, true,
		// SharePermission.EDIT);
		FileEntry testFileEntry = new FileEntry(null, is, testFilename,
				"The Jelly Fish", "fish tags", Permissions.PUBLIC, true,
				Notification.ON, Notification.ON, null, null, true, true,
				SharePermission.EDIT, null, null);

		/*
		 * // Get the service doc from the publish link, and find the actual
		 * link to publish to, and retrieve files from Service serviceDoc =
		 * (Service)filesService.getUrlFeed(publishLink); HashMap<String,
		 * String> collectionUrls = new HashMap<String, String>(); String
		 * documentPublishLink = ""; String documentListLink = "";
		 * 
		 * for(Workspace workspace : serviceDoc.getWorkspaces()) {
		 * //LOGGER.debug(workspace.getTitle()); for(Collection collection :
		 * workspace.getCollections()) {
		 * collectionUrls.put(collection.getTitle(),
		 * collection.getHref().toString()); if
		 * ((collection.getTitle().equalsIgnoreCase("Documents Feed")) &&
		 * (workspace.getTitle().contains("Community Library")))
		 * documentPublishLink = collection.getHref().toString(); else if
		 * ((collection.getTitle().equalsIgnoreCase("Documents Feed")) &&
		 * (workspace.getTitle().contains("Community Collection")))
		 * documentListLink = collection.getHref().toString(); } }
		 */

		LOGGER.debug("upload file to Community");
		// Entry result = (Entry)
		// filesService.createCommunityFileNoInputStream(documentPublishLink,testFileEntry);
		Entry result = (Entry) filesService.uploadFileToCommunity(
				testFileEntry, community);

		LOGGER.debug("add comment ");
		String commentLink = result.getLink(StringConstants.REL_REPLIES)
				.getHref().toString();

		// Add a comment as file owner
		FileComment comment = new FileComment(
				"API Mod Test Comment - this is a comment from file owner");
		result = (Entry) service.postFileComment(commentLink, comment);

		assertTrue(commentOnFileAsAnotherUser(commentLink));

		if (!StringConstants.MODERATION_ENABLED) {
			LOGGER.warn("moderation of file comments skipped as moderation was not specified as enabled on server.");
		} else {
			// moderate file comments
			assertTrue(handleModeration(community.getUuid(), true));
		}

	}

	@Test
	public void connectionAdminUser() throws IOException {
		/*
		 * This test only executes on servers where moderation is enabled
		 * 
		 * 1. Create a community as ConnectionAdmin (fvtadmin) (no matter
		 * public/restricted/moderated community) 2. Test Files and Forums
		 * access to Moderation as "fvtadmin" Make sure we don't get this error:
		 * "We are unable to process your request"
		 */
		LOGGER.debug("Beginning test RTC 46306 and 46307: Test Moderation access using ConnectionAdmin user");

		if (StringConstants.MODERATION_ENABLED) {

			// UserPerspective usr3 = new
			// UserPerspective(StringConstants.CONNECTIONS_ADMIN_USER,
			// Component.COMMUNITIES.toString(),useSSL);
			// connectionsAdminUser.getUserName();
			// CommunitiesService adminService =
			// connectionsAdminUser.getCommunitiesService();

			/*
			 * This addition allows multiple executions of this test. Otherwise,
			 * a delete method would be needed to erase the community at the
			 * start of each test.
			 */
			String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

			// create community
			Community newCommunity = new Community(
					"RTC 46306 and 46307 ConnectionAdmin user test "
							+ uniqueNameAddition, "This is a Moderation test",
					Permissions.PUBLIC, null);
			Entry communityResult = (Entry) adminService
					.createCommunity(newCommunity);
			assertTrue(communityResult != null);

			// get communities feed
			Feed communitiesFeed = (Feed) adminService.getMyCommunities(true,
					null, 0, 500, null, null, null, null, null);
			assertTrue(communitiesFeed != null);

			// create community object
			Community comm = null;
			for (Entry communityEntry : communitiesFeed.getEntries()) {
				if (communityEntry.getTitle().equals(
						"RTC 46306 and 46307 ConnectionAdmin user test "
								+ uniqueNameAddition)) {
					comm = new Community(communityEntry);
				}
			}

			// For Files test creating the moderation service. This will fail if
			// user account does not have moderator privs.
			/*
			 * try { Utils.addServiceCredentials(files,
			 * client,StringConstants.CONNECTIONS_ADMIN_USER_NAME
			 * ,StringConstants.CONNECTIONS_ADMIN_USER_PASSWORD); } catch
			 * (URISyntaxException e) { LOGGER.warning(e.toString()); }
			 */

			// ModerationService modServiceFiles = new ModerationService(client,
			// files,"/basic/api/moderation/atomsvc");
			// assert(modServiceFiles.isFoundService());

			/*
			 * For Forums test if there are errors accessing the moderation URL.
			 * Can't use the same approach as Files as the Service Doc urls for
			 * Forums allow access even if the user is not a moderator.
			 */
			// http://c14-32.swg.usma.ibm.com/moderation/communitiesapp?proxyCommunityUuid=e318acc9-9185-48f0-83a6-c5a676ac0f9a#/ca/forums/posts/pending?lang=en
			String forumsModerationUrl = URLConstants.SERVER_URL
					+ "/moderation/communitiesapp?proxyCommunityUuid="
					+ comm.getUuid() + "#/ca/forums/posts/pending?lang=en";
			ExtensibleElement ee = null;
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				Document<ExtensibleElement> feed_doc = modServiceFiles
						.getModFeed(forumsModerationUrl).getDocument();
				ee = feed_doc.getRoot();
			} else {
				ee = adminService.getAnyFeed(forumsModerationUrl);
			}
			// We haven't created any items that require approval, so we're
			// really just verifying that the user has access to the moderation
			// page.
			assertTrue(ee.toString().contains("empty response"));

		} else {
			LOGGER.debug("Moderation not enabled.  This test has been skipped.");
		}

		LOGGER.debug("Ending test RTC 46306 and 46307: Test Moderation access using ConnectionAdmin user");
	}

	@Test
	public void getCommunityLibrary() throws IOException {
		/*
		 * Test the ability to get uploaded files in a community (not the
		 * library widget) NOTE: This test uses blank files. Can't figure out
		 * how to upload. Step 1: Create a community Step 2: Get community
		 * library, verify it's empty Step 3: Create some files Step 4: Get
		 * community library, verify the files are there
		 */
		LOGGER.debug("Beginning Test: Get Community Library");
		String randString = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community("CommunityFileLibrary Test "
				+ randString, "A community with files", Permissions.PRIVATE,
				null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Get community library, verify it's empty");
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		Feed initialLibraryFeed = (Feed) filesService
				.getCommunityLibraryFeed(communityRetrieved.getUuid());
		assertTrue(initialLibraryFeed.getEntries().isEmpty());

		LOGGER.debug("Step 3: Create some files in the community");
		// get publish link
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				communityRetrieved.getRemoteAppsListHref(), false, null, 1, 10,
				null, null, null, null, null, null);
		String publishLink = "";
		for (Entry entry : remoteAppsFeed.getEntries()) {
			for (Category category : entry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("Files")) {
					publishLink = entry.getLink(StringConstants.REL_PUBLISH)
							.getHref().toString();
					break;
				}
			}
		}
		// create a file entry NOTE: mysteriously this file does not actually
		// upload.
		File testFile = new File(this.getClass()
				.getResource("/resources/fish.txt").getFile());
		FileEntry testFileEntry1 = new FileEntry(testFile, "TestFile1"
				+ randString + ".png", "I'm a blank file", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW, "hi shares",
				null, null, "this is a file body");
		FileEntry testFileEntry2 = new FileEntry(testFile, "TestFile2"
				+ randString + ".txt", "I'm a better blank file.", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.EDIT, null, null);

		// Get the service doc from the publish link, and find the actual link
		// to publish to, and retrieve files from
		Service serviceDoc = (Service) filesService.getUrlFeed(publishLink);
		String documentPublishLink = "";

		for (Workspace workspace : serviceDoc.getWorkspaces()) {
			for (Collection collection : workspace.getCollections()) {
				if ((collection.getTitle().equalsIgnoreCase("Documents Feed"))
						&& (workspace.getTitle().contains("Community Library")))
					documentPublishLink = collection.getHref().toString();
			}
		}

		// publish to the community
		filesService.createCommunityFileNoInputStream(documentPublishLink,
				testFileEntry1);
		filesService.createCommunityFileNoInputStream(documentPublishLink,
				testFileEntry2);

		LOGGER.debug("Step 4: Get community library, verify the files are there");
		Feed finalLibraryFeed = (Feed) filesService
				.getCommunityLibraryFeed(communityRetrieved.getUuid());
		boolean foundFile1 = false, foundFile2 = false;
		for (Entry e : finalLibraryFeed.getEntries()) {
			if (e.getTitle().equals(testFileEntry1.getTitle()))
				foundFile1 = true;
			else if (e.getTitle().equals(testFileEntry2.getTitle()))
				foundFile2 = true;
		}
		assertTrue(foundFile1);
		assertTrue(foundFile2);

		LOGGER.debug("Ending test: Get Community Library");
	}

	@Test
	public void getCommunityLibaryInfo() {
		/*
		 * Tests the ability to get community library/files info Step 1: Create
		 * a community Step 2: Get community library info
		 */
		LOGGER.debug("Beginning test: Get community Library info");
		String randString = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community("CommunityLibraryInfo Test "
				+ randString, "I'm so cool", Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		LOGGER.debug("Step 2: Get community library info, verify it's correct");
		Entry libraryInfo = (Entry) filesService
				.getCommunityLibraryInfo(communityRetrieved.getUuid());
		assertEquals(testCommunity.getTitle(), libraryInfo.getTitle());
	}

	@Test
	public void removeFileShare() throws FileNotFoundException {
		/*
		 * Test the ability to remove a shared file from a community Step 1:
		 * Create a community Step 2: Create a file and share it with the
		 * communtiy Step 3: Remove the shared file from the community Step 4:
		 * Verify the file was removed
		 */
		LOGGER.debug("Beginning Test: Remove file share");
		String randString = RandomStringUtils.randomAlphanumeric(15);

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community(
				"CommunityRemoveFileShare Test " + randString,
				"A community with some file to be removed",
				Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Create a file and share it with the communtiy");
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));

		// Create file entry object
		File testFile = new File(this.getClass()
				.getResource("/resources/fish.txt").getFile());
		FileEntry testFileShareEntry = new FileEntry(testFile, "TestFileShare"
				+ randString + ".jpg", "I'm a shared file!", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.EDIT, null, null);
		// Upload file to share
		Entry response = (Entry) filesService.createFile(testFileShareEntry);

		// Generate community entry to use to share the file
		Factory factory = abdera.getFactory();
		Entry communityEntry = factory.newEntry();
		communityEntry.addCategory("tag:ibm.com,2006:td/type", "community",
				"community");
		communityEntry.addSimpleExtension("urn:ibm.com/td", "itemId", null,
				communityRetrieved.getUuid());

		// Create file share
		String documentId = response.getId().toString().split(":td:")[1];
		filesService.shareFileWithCommunity(documentId, communityEntry);

		LOGGER.debug("Step 3: Remove the shared file from the community");
		filesService.removeCommunityFileShare(communityRetrieved.getUuid(),
				documentId);

		LOGGER.debug("Step 4: Verify the file was removed");
		Feed collectionFeed = (Feed) filesService
				.getCommunityCollectionFeed(communityRetrieved.getUuid());
		assertEquals(0, collectionFeed.getEntries().size()); // assert there are
		// no file
		// entries

		LOGGER.debug("Ending test: Remove file share");
	}

	@Test
	public void getCommunityCollection() throws FileNotFoundException {
		/*
		 * Tests the ability to get a community collection (includes uploaded
		 * and shared with files) Step 1: Create a community Step 2: Get
		 * community collection, verify it's empty Step 3: Upload a file to the
		 * community, share a file with the community Step 4: Get community
		 * collection, verify both of the added files are there
		 * 
		 * TJB 4/29/14 This test should be revised. Originally it was uploading
		 * a txt file disguised as a .png, which is not a useful test case. It
		 * should upload one or two true graphic files. Ian wrote some code to
		 * handle uploading real graphic files. I'm not sure this is a valid
		 * test case.
		 */
		LOGGER.debug("Beginning Test: Get community collection");
		String randString = RandomStringUtils.randomAlphanumeric(4);

		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community(
				"CommunityCollection Files Test " + randString,
				"A community with a file collection", Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Get community collection, verify it's empty");
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		Feed initialCollectionFeed = (Feed) filesService
				.getCommunityCollectionFeed(communityRetrieved.getUuid());
		assertTrue(initialCollectionFeed.getEntries().isEmpty());

		LOGGER.debug("Step 3: Upload a file to the community, share a file with the community");
		// get publish link
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
				communityRetrieved.getRemoteAppsListHref(), false, null, 1, 10,
				null, null, null, null, null, null);
		String publishLink = "";
		for (Entry entry : remoteAppsFeed.getEntries()) {
			for (Category category : entry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("Files")) {
					publishLink = entry.getLink(StringConstants.REL_PUBLISH)
							.getHref().toString();
					break;
				}
			}
		}
		// Get the service doc from the publish link, and find the actual link
		// to publish to, and retrieve files from
		Service serviceDoc = (Service) filesService.getUrlFeed(publishLink);
		String documentPublishLink = "";
		for (Workspace workspace : serviceDoc.getWorkspaces()) {
			for (Collection collection : workspace.getCollections()) {
				if ((collection.getTitle().equalsIgnoreCase("Documents Feed"))
						&& (workspace.getTitle().contains("Community Library")))
					documentPublishLink = collection.getHref().toString();
			}
		}

		// Create file entry objects
		File testFile = new File(this.getClass()
				.getResource("/resources/fish.txt").getFile());
		FileEntry testFileShareEntry = new FileEntry(testFile, "TestFileShare"
				+ randString + ".txt", "I'm a shared file!", "",
				Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.VIEW, null, null);
		FileEntry testFileUploadEntry = new FileEntry(testFile,
				"TestFileUpload" + randString + ".txt", "I'm a blank file.",
				"", Permissions.PUBLIC, true, Notification.ON, Notification.ON,
				null, null, true, true, SharePermission.EDIT, null, null);

		// Upload file to share
		// Entry response = (Entry) filesService.createFile(testFileShareEntry);
		Entry response = (Entry) filesService.createCommunityFileNoInputStream(
				documentPublishLink, testFileShareEntry);
		assertEquals("Create Community File", 201, filesService.getRespStatus());
		String documentId = response.getId().toString().split(":td:")[1];

		// Generate community entry to use to share the file
		Factory factory = abdera.getFactory();
		Entry communityEntry = factory.newEntry();
		communityEntry.addCategory("tag:ibm.com,2006:td/type", "community",
				"community");
		communityEntry.addSimpleExtension("urn:ibm.com/td", "itemId", null,
				communityRetrieved.getUuid());

		// publish to community
		filesService.shareFileWithCommunity(documentId, communityEntry); // Create
		// file
		// share
		// assertEquals("shareFileWithCommunity", 201,
		// filesService.getRespStatus());
		filesService.createCommunityFileNoInputStream(documentPublishLink,
				testFileUploadEntry); // Upload file directly
		assertEquals("createCommunityFile", 201, filesService.getRespStatus());

		LOGGER.debug("Step 4: Get community collection, verify both of the added files are there");
		Feed finalCollectionFeed = (Feed) filesService
				.getCommunityCollectionFeed(communityRetrieved.getUuid());
		boolean foundFile1 = false, foundFile2 = false;
		for (Entry e : finalCollectionFeed.getEntries()) {
			if (e.getTitle().equals(testFileShareEntry.getTitle()))
				foundFile1 = true;
			else if (e.getTitle().equals(testFileUploadEntry.getTitle()))
				foundFile2 = true;
		}
		assertTrue(foundFile1);
		assertTrue(foundFile2);

		LOGGER.debug("Ending Test: Get community collection");
	}

	private boolean handleModeration(String communityID, boolean moderateFiles) {

		/*
		 * NOTE: on the test servers blogs are owner moderated and files are
		 * global moderated so for files we need to login as global moderator
		 * first
		 * 
		 * for code simplicity we'll create another instance all the time, but
		 * use normal login for blogs
		 */
		/*
		 * Abdera abdera2 = new Abdera(); AbderaClient client2 = new
		 * AbderaClient(abdera2); ServiceConfig config2 = new
		 * ServiceConfig(client2, URLConstants.SERVER_URL, useSSL);
		 * 
		 * // "parent" service for moderation ServiceEntry serviceToModerate =
		 * null; if (moderateFiles) serviceToModerate =
		 * config2.getService("files"); else serviceToModerate =
		 * config2.getService("blogs");
		 * 
		 * try { if (moderateFiles)
		 * Utils.addServiceCredentials(serviceToModerate,
		 * client2,StringConstants
		 * .MODERATOR_USER_NAME,StringConstants.MODERATOR_USER_PASSWORD); else
		 * Utils.addServiceCredentials(serviceToModerate,
		 * client2,StringConstants.USER_NAME,StringConstants.USER_PASSWORD); }
		 * catch (URISyntaxException e) { return false; }
		 */
		ModerationService modService;
		if (moderateFiles)
			modService = modServiceFiles;// new ModerationService(client2,
		// serviceToModerate,"/basic/api/moderation/atomsvc");
		else
			modService = modServiceBlogs;// new ModerationService(client2,
		// serviceToModerate,"/moderation/atomsvc");
		assert (modService.isFoundService());

		ArrayList<CommentToModerate> commentsToModerate = modService
				.getCommentsAwaitingApproval(communityID);
		if (commentsToModerate == null)
			return false;

		int numberOfCommentsToMod = commentsToModerate.size();
		int numberOfCommentsAccepted = 0;
		int numberOfCommentsRejected = 0;

		// Loop through array, look at content, and set approve or reject - post
		// to service
		for (CommentToModerate commentToModerate : commentsToModerate) {
			if (commentToModerate.getContent().contains("please accept")) {
				commentToModerate.SetApproved(true);
				++numberOfCommentsAccepted;
			} else {
				commentToModerate.SetApproved(false);
				++numberOfCommentsRejected;
			}
			assertTrue(modService.moderateComment(commentToModerate));
		}

		// Make sure we've actually done something
		int numberOfCommentsLeft = 0;
		commentsToModerate = modService
				.getCommentsAwaitingApproval(communityID);
		if (commentsToModerate != null)
			numberOfCommentsLeft = commentsToModerate.size();

		boolean result = (numberOfCommentsLeft == (numberOfCommentsToMod - (numberOfCommentsAccepted + numberOfCommentsRejected)));

		return result;
	}

	// TODO: what is this method for?
	private boolean commentOnFileAsAnotherUser(String commentURL)
			throws FileNotFoundException, IOException {

		// Create another instance of service as the user we're going to comment
		// as
		UserPerspective user2=null;
		try {
			user2 = new UserPerspective(4,
					Component.COMMUNITIES.toString());
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		FileComment fileComment = new FileComment("API Comment - please accept");
		user2.getCommunitiesService().postFileComment(commentURL, fileComment);

		FileComment fileComment2 = new FileComment(
				"API Comment - please reject");
		user2.getCommunitiesService().postFileComment(commentURL, fileComment2);

		return true;
	}

	// duplicated -
	public Community getCreatedPrivateCommunity(String CommunityTitle) {
		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community(CommunityTitle,
				"A community for testing", Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Retrieve that community");
		Community communityRetrieved = new Community(
				(Entry) service.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		return communityRetrieved;

	}

	@Test
	public void communityFilesCrossOrg() throws IOException {
		/*
		 * Test the ability to remove a shared file from a community Step 1:
		 * Create a community Step 2: Create a file and share it with the
		 * communtiy Step 3: Remove the shared file from the community Step 4:
		 * Share a different file using impersonation.
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
			LOGGER.debug("Beginning Test: Community Files Cross Org test");
			String randString = RandomStringUtils.randomAlphanumeric(4);
			String communityName = "Community File Cross Org Test "
					+ randString;

			LOGGER.debug("Step 1: Create a community");
			Community testCommunity = new Community(communityName,
					"Cross Org testing using Files", Permissions.PUBLIC, null);
			Entry communityResult = (Entry) service
					.createCommunity(testCommunity);

			LOGGER.debug("Step 2: Create a file and share it with the communtiy");
			Community communityRetrieved = new Community(
					(Entry) service.getCommunity(communityResult
							.getEditLinkResolvedHref().toString()));

			// Add another user. Just remember, email is not exposed on SC so
			// first param is null.
			Member member = new Member(null, otherUser.getUserId(),
					Component.COMMUNITIES, Role.MEMBER, MemberType.PERSON);

			Entry memberEntry = (Entry) service.addMemberToCommunity(
					communityRetrieved, member);
			assertEquals(" Add Community Member ", 201, service.getRespStatus());

			// Create file entry object
			File testFile = new File(this.getClass()
					.getResource("/resources/fish.txt").getFile());
			FileEntry testFileShareEntry = new FileEntry(testFile,
					"TestFileShareTJB" + randString + ".jpg",
					"I'm a shared file!", "", Permissions.PUBLIC, true,
					Notification.ON, Notification.ON, null, null, true, true,
					SharePermission.EDIT, null, null);
			// Upload file to share
			Entry response = (Entry) filesService
					.createFile(testFileShareEntry);

			// Generate community entry to use to share the file
			Factory factory = abdera.getFactory();
			Entry communityEntry = factory.newEntry();
			communityEntry.addCategory("tag:ibm.com,2006:td/type", "community",
					"community");
			communityEntry.addSimpleExtension("urn:ibm.com/td", "itemId", null,
					communityRetrieved.getUuid());

			// Create file share
			String documentId = response.getId().toString().split(":td:")[1];
			filesService.shareFileWithCommunity(documentId, communityEntry);

			// Setup code for the out of org user:
			int ORG_B_REGULAR_USER_INDEX = 15;
			String impersonationHeaderKey = "X-LConn-RunAs";
			String impersonationHeaderValue_userId = "userId";
			boolean useSSL = true;

			// Org B regular user - Jill White
			UserPerspective orgBRegular=null;
			try {
				orgBRegular = new UserPerspective(
						ORG_B_REGULAR_USER_INDEX, Component.COMMUNITIES.toString(),
						useSSL);
			} catch (LCServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			LOGGER.debug("Before setting the request option, X-LConn-RunAs : "
					+ filesService.getRequestOption("X-LConn-RunAs"));

			// Org A Admin uses Org B regular user for impersonation.
			filesService.addRequestOption(
					impersonationHeaderKey,
					impersonationHeaderValue_userId + "="
							+ orgBRegular.getUserId());
			LOGGER.debug("After setting the request option, X-LConn-RunAs : "
					+ filesService.getRequestOption("X-LConn-RunAs"));

			FileEntry testFileShareEntry2 = new FileEntry(testFile,
					"TestFileShare-CrossOrg" + randString + ".jpg",
					"I'm a shared file!", "", Permissions.PUBLIC, true,
					Notification.ON, Notification.ON, null, null, true, true,
					SharePermission.EDIT, null, null);
			// Upload file to share
			Entry response2 = (Entry) filesService
					.createFile(testFileShareEntry2);

			String documentId2 = response2.getId().toString().split(":td:")[1];
			filesService.shareFileWithCommunity(documentId2, communityEntry);

			LOGGER.debug("Ending Test: Community Files Cross Org test");
		}
	}// end smartcloud if

}
