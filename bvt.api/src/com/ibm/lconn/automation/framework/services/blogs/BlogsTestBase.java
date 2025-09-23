package com.ibm.lconn.automation.framework.services.blogs;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.imageio.ImageIO;
import javax.xml.namespace.QName;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
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
import org.apache.abdera.protocol.client.ClientResponse;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;
import com.ibm.lconn.automation.framework.services.blogs.impersonated.BlogsImpersonatedTest;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.BlogsField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.BlogsType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;

public abstract class BlogsTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(BlogsTestBase.class.getName());
	static Abdera abdera = new Abdera();
	protected static UserPerspective user, imUser, otherUser;// , visitor,
													// extendedEmployee;
	protected static BlogsService service, impersonateService; // , visitorService, extendedEmpService;
	protected static boolean useSSL = true;

	@Test
	public void validateBlog() {
		ArrayList<Blog> blogs = service.getMyBlogs(null, null, 0, 0, null,
				null, null, null, BlogsType.BLOG, null, null);

		for (Blog blog : blogs) {
			// /System.out.println(blog.getTitle() + " : " +
			// blog.getBlogType());
			assert (blog != null);

		}
	}

	@Test
	public void createBlog() throws Exception {
		Blog regBlog = new Blog("Test API Regular Blog19", "APIRegularBlog19",
				"This blog is for testing and verifying regular blog creation",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		Entry regBlogEntry = (Entry) service.createBlog(regBlog);
		assertEquals(" Create Blog", 201, service.getRespStatus());
		Blog regBlogResult = new Blog(regBlogEntry);

		// Verify the blog activitystream
		String blogId = regBlogResult.getId().toString();

		blogId = blogId.substring(blogId.indexOf("-") + 1);
		blogId = "urn:lsid:lconn.ibm.com:blogs.blog:" + blogId;
		
		// try 10 times/seconds to allow the processing of events
		
		for (int second = 0; second <= 10; second++) {
						
			String result = service.getResponseString(URLConstants.SERVER_URL
							+ "/connections/opensocial/rest/activitystreams/@public/@all/blogs?filterBy=object&filterOp=equals&filterValue="
							+ blogId);
			assertEquals("Get /activitystreams/@public/@all/blogs?filterBy=object&filterOp=equals&filterValue="
							+ blogId, 200, service.getRespStatus());


			JSONObject js = JSONObject.parse(result);
			if (this.getClass().equals(BlogsImpersonatedTest.class)) {
				break;
			} else if (js.get("itemsPerPage").equals(1l)) {
				assertEquals("itemsPerPage", 1l, js.get("itemsPerPage"));
				assertEquals("list", 1, ((JSONArray) js.get("list")).size());
				break;
			} else {
				Thread.sleep(1000);
				//second++;
				LOGGER.debug("processing of events : " + second);
				if (second > 9) {
					assertEquals("get itemsPerPage from event failed  after 10 seconds", 1l, js.get("itemsPerPage"));
					assertEquals("list", 1, ((JSONArray) js.get("list")).size());
					break;
				}
			}

		}

	}

	@Test
	public void createBlogForBVT() {
		Blog regBlog = new Blog("BVT Search Test Blog", "BVTSearchBlog",
				"This blog is for testing blog search", "blogssearchtag1234",
				false, false, null, null, TimeZone.getDefault(), true, 13,
				true, true, true, 0, -1, null, null, null, 0);
		Entry regBlogEntry = (Entry) service.createBlog(regBlog);
		assertEquals(" Create Blog", 201, service.getRespStatus());
		Blog regBlogResult = new Blog(regBlogEntry);

		assert (regBlogResult != null);
	}

	@Test
	public void createBlogOver2kDescLimit() throws IOException {
		boolean firstLine = true;
		String str = null;
		StringBuffer buffer = new StringBuffer();
		BufferedReader in = new BufferedReader(new InputStreamReader(this
				.getClass().getResourceAsStream("/resources/blogDesc_en.txt"),
				"UTF-8"));
		while ((str = in.readLine()) != null) {
			if (firstLine) {
				str = str.startsWith("\uFEFF") ? str.substring(1) : str;
				firstLine = false;
			}
			buffer.append(str);
		}
		in.close();

		Blog regBlog = new Blog("Title: Test API createBlogOver2kDescLimit",
				"createBlogOver2kDescLimit", buffer.toString(), "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		Entry regBlogEntry = (Entry) service.createBlog(regBlog);
		assertEquals(" Create Blog", 201, service.getRespStatus());
		Blog regBlogResult = new Blog(regBlogEntry);
		assert (regBlogResult != null);
		// Assert that the blog description has been truncated to 2000
		// characters (limit)
		assertEquals(" Description 2000 character truncation", 2000,
				regBlogResult.getSummary().length());
	}

	// TODO: This test needs to be moved to one where both the blogs +
	// communities services are available since this test
	// relies on the capability of retrieving a community first which the
	// BlogsPopulate tool is not meant to test or handle.
	// @Test
	// public void createCommunityBlog() {
	// Blog commBlog = new Blog("Test API Community Blog", "APICommunityBlog",
	// "This blog is for testing and verifying community blog creation",
	// "api verification test", true, false, CommunityBlogPermissions.PRIVATE,
	// null, TimeZone.getDefault(), false, 13, false, false, false, 0, 0,
	// "6522103f-fa7b-41e0-a520-d0aa16397baf",
	// "6522103f-fa7b-41e0-a520-d0aa16397baf", Role.AUTHOR, 0);
	// Entry commBlogEntry = (Entry) service.createBlog(commBlog);
	// Blog commBlogResult = new Blog(commBlogEntry);
	//
	// System.out.println("Result: " + commBlog.equals(commBlogResult));
	// }

	// TODO: Need to fix, not working/not documented in public API.
	// @Test
	// public void createIdeationBlog() {
	// Blog ideationBlog = new Blog("Test API ideation Blog", "APIideationBlog",
	// "This blog is for testing and verifying ideation blog creation",
	// "api verification test", false, true, null, IdeationStatus.OPEN,
	// TimeZone.getDefault(), false, 0, false, false, false, 0, 0,
	// "6522103f-fa7b-41e0-a520-d0aa16397baf",
	// "6522103f-fa7b-41e0-a520-d0aa16397baf", Role.AUTHOR, 8);
	// Entry ideationBlogEntry = (Entry) service.createBlog(ideationBlog);
	// Blog ideationBlogResult = new Blog(ideationBlogEntry);
	//
	// System.out.println("Result: " + ideationBlog.equals(ideationBlogResult));
	// }

	@Test
	public void createBlogEntries() throws URISyntaxException {
		ArrayList<Blog> blogs = service.getMyBlogs(null, null, 0, 5, null,
				null, null, null, BlogsType.BLOG, null, null);
		for (Blog blog : blogs) {
			try {
				service.createMediaResource(
						blog,
						new File(
								this.getClass()
										.getResource(
												"/resources/lamborghini_murcielago_lp640.jpg")
										.getFile()),
						this.getClass().getResourceAsStream(
								"/resources/lamborghini_murcielago_lp640.jpg"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BlogPost post = new BlogPost("First Post",
					"Cool blog post from API", "first post blog", true, 5);
			Entry result = (Entry) service.createPost(blog, post);
			assertEquals(" Create BlogPost", 201, service.getRespStatus());
			BlogPost response = new BlogPost(result);
			// /System.out.println(post.equals(response));

			if (!blog.isIdeationBlog())
				assertTrue(response.getTitle() != null);
		}
	}

	/*
	 * @Test public void deleteAllBlogs(){ ArrayList<Blog> blogs =
	 * service.getMyBlogs(null, null, 0, 0, null, null, null, null,
	 * BlogsType.BLOG, null, null);
	 * 
	 * for(Blog blog : blogs) {
	 * System.out.println(service.deleteBlog(blog.getEditHref())); } }
	 */

	@Test
	public void addBlogEntryComments() {
		LOGGER.debug("Start Test: Create a comment to a blog entry");

		// delete all the pervious tests used
		service.deleteTests();
		LOGGER.debug("Method : addBlogEntryComments homepage Handle" + service.getBlogsHomepageHandle());

		String blogHandle = "BionicArm";
		String entryTitle = "BionicLancer";

		// variable to see if the correct result was shown
		boolean found = false;

		// create a blog to test with
		Blog regBlog = new Blog("Spencer", blogHandle,
				"Soldier that lost his arm in the war.", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals(" Create Blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		// create entry in a blog
		BlogPost post = new BlogPost(entryTitle,
				"Spencer uses his mechanical arm", "BIONIC", true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, post);
		BlogPost response = new BlogPost(postResult);
		LOGGER.debug("Created entry to blog");

		// create a comment to the entry
		BlogComment comment = new BlogComment("bionic ARMMM", postResult);
		Entry checkComment = (Entry) service.createComment(response, comment);
		if (checkComment == null) {
			LOGGER.debug("Comment was not made successfully");
			assertTrue(false);
		}
		LOGGER.debug("Created comment to blog entry");

		// test to see if the comment was successfully made
		Feed commentFeed = (Feed) service
				.getComment(response.getCommentsHref());
		for (Entry e : commentFeed.getEntries()) {
			if (e.getTitle().substring(e.getTitle().indexOf(" ") + 1)
					.equals(entryTitle)) {
				LOGGER.debug("Test Successful: Comment successfully created");
				found = true;
				assertTrue(true);
				break;
			}
		}

		if (!found) {
			LOGGER.debug("Test Failed: Comment not found");
			assertTrue(false);
		}
	}

	/**
	 * Tests ability to get the feed of comments (for a blog)
	 * 
	 * @see /blogs/{handle}/feed/comments/atom
	 */
	@Test
	public void getBlogCommentFeed() {
		/**
		 * Step 1: Create a blog Step 2: Get a current list of comments in the
		 * blog Step 3: Add an entry to the blog Step 4: Add a comment to the
		 * entry Step 5: Count the comments that already match the comment we
		 * are adding Step 6: Get the comment through the endpoint Step 7:
		 * Verify the endpoint has the added comment
		 */

		LOGGER.debug("Beginning Test: GET getBlogCommentFeed");
		String timeStamp = Utils.logDateFormatter.format(new Date());

		// Initializing Variables...
		String myblogTitle = "Sprite" + timeStamp;
		String handle = "Soda" + timeStamp;
		String blogEntryTitle = "I love Sprite" + timeStamp;
		String commentString = "What about Coca Cola? It's my favorite drink!";

		LOGGER.debug("Step 1: Create a blog");
		LOGGER.debug("getBlogCommentFeed handle ======== " + handle);
		Blog spriteBlog = new Blog(
				myblogTitle,
				handle,
				"A soda with a mix of lemon and lime flavoring.",
				"bev beverage beverages drink drinks sugar green lime lemon yellow",
				false, false, null, null, TimeZone.getDefault(), true, 10,
				true, true, true, 0, -1, null, null, null, 0);
		service.createBlog(spriteBlog);
		assertEquals("Create blog", 201, service.getRespStatus());

		LOGGER.debug("Step 2: Get a current list of comments in the blog");
		// PRE-LIST: Not updated! Need to do after the blog has been posted to
		// avoid 404
		String urlToTest = service.getURLString() + "/" + handle
				+ "/feed/comments/atom";
		List<Entry> blogCommentEntries = ((Feed) service.getBlogFeed(urlToTest))
				.getEntries();

		LOGGER.debug("Step 3: Add an entry to the blog");
		BlogPost spriteEntry = new BlogPost(blogEntryTitle,
				"sugarsugarsugargivememoresugarsugarsugar", "sugar", true, 90);
		Entry spriteEntryResponse = (Entry) service.postBlogsFeed(
				service.getURLString() + "/" + handle + "/api/entries",
				spriteEntry.toEntry());

		LOGGER.debug("Step 4: Add a comment to the entry");
		BlogComment blogComment = new BlogComment(commentString,
				spriteEntryResponse);
		service.postBlogsFeed(service.getURLString()
				+ URLConstants.BLOGS_SERVICES + handle + "/comments",
				blogComment.toEntry());

		LOGGER.debug("Step 5: Count the comments that already match the comment we are adding");
		int matches = 0; // in the for loop, get the number of matches before
		// the test, then refresh the feed
		for (int i = 0; i < blogCommentEntries.size(); i++) {
			if (blogCommentEntries.get(i).getContent().equals(commentString)
					&& blogCommentEntries.get(i).getTitle()
							.equals("Re: " + blogEntryTitle))
				;
			matches++;
		}

		LOGGER.debug("Step 6: Get the comment through the endpoint");
		urlToTest = service.getURLString() + "/" + handle
				+ "/feed/comments/atom";
		blogCommentEntries = ((Feed) service.getBlogFeed(urlToTest))
				.getEntries();

		LOGGER.debug("Step 7: Verify the endpoint has the added comment (and ensure the total number of matches has increased by 1)"); // to
		// avoid
		// bad
		// assertTrue
		// statements
		int secondMatchGroup = 0;
		for (int i = 0; i < blogCommentEntries.size(); i++) {
			if (blogCommentEntries.get(i).getContent().equals(commentString)
					&& blogCommentEntries.get(i).getTitle()
							.equals("Re: " + blogEntryTitle))
				;
			secondMatchGroup++;
		}
		assertTrue(secondMatchGroup == matches + 1);

		// RTC118282 @Mention test
		String mention = " &lt;span class=\"vcard\"&gt;&lt;span class=\"fn\"&gt;@"
				+ otherUser.getRealName()
				+ "&lt;/span&gt;&lt;span class=\"x-lconn-userid\"&gt;"
				+ otherUser.getUserId() + "&lt;/span&gt;&lt;/span&gt;";
		blogComment = new BlogComment(commentString + mention,
				spriteEntryResponse);
		service.postBlogsFeed(service.getURLString()
				+ URLConstants.BLOGS_SERVICES + handle + "/comments",
				blogComment.toEntry());
		assertEquals("comment with @Mention", 201, service.getRespStatus());

		ExtensibleElement comments = service.getCommentsFeed("html");
		assertEquals("get Blogs failed", 200, service.getRespStatus());
		assertEquals("@Mention html contentType", "HTML", ((Feed) comments)
				.getEntries().get(0).getContentType().toString());
		comments = service.getCommentsFeed("raw");
		assertEquals("get Blogs failed", 200, service.getRespStatus());
		assertEquals("@Mention raw contentType", "TEXT", ((Feed) comments)
				.getEntries().get(0).getContentType().toString());

		LOGGER.debug("Test Completed");
	}

	@Test
	public void getBlogEntryComments() {
		LOGGER.debug("Start Test: Get a comment appart of a blog entry");

		// delete all the pervious tests used
		service.deleteTests();

		String blogHandle = "BionicArm";
		String entryTitle = "BionicLancer";

		// variable to see if the correct result was shown
		boolean found = false;

		// create a blog to test with
		Blog regBlog = new Blog("Spencer", blogHandle,
				"Soldier that lost his arm in the war.", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		// create entry in a blog
		BlogPost post = new BlogPost(entryTitle,
				"Spencer uses his mechanical arm", "BIONIC", true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, post);
		BlogPost response = new BlogPost(postResult);
		LOGGER.debug("Created entry to blog");

		// create a comment to the entry
		String commentTitle = "<a href=\"http://www.google.com\">test</a>";
		BlogComment comment = new BlogComment(commentTitle, postResult);
		Entry checkComment = (Entry) service.createComment(response, comment);
		if (checkComment == null) {
			LOGGER.debug("Comment was not made successfully");
			assertTrue(false);
		}
		LOGGER.debug("Created comment to blog entry");

		// test to see if the comment was successfully made
		Feed commentFeed = (Feed) service
				.getComment(response.getCommentsHref());
		for (Entry e : commentFeed.getEntries()) {
			if (e.getTitle().substring(e.getTitle().indexOf(" ") + 1)
					.equals(entryTitle)) {
				LOGGER.debug("Test Successful: Comment successfully retrieved from the feed");
				found = true;
				assertTrue(true);

				// for 74891 lc4.0-text, lc4.5-html
				String type = e.getContentType().toString();
				assertEquals("text", type.toLowerCase().trim());

				break;
			}
		}

		// for 74889
		String altlink = postResult.getLink(StringConstants.REL_ALTERNATE)
				.getHref().toString();
		altlink = altlink
				.replace("/blogs/", "/blogs/roller-ui/rendering/feed/");
		altlink = altlink.replace("/entry/", "/entrycomments/")
				+ "/atom?lang=en";
		commentFeed = (Feed) service.getComment(altlink);
		for (Entry e : commentFeed.getEntries()) {
			if (e.getTitle().substring(e.getTitle().indexOf(" ") + 1)
					.equals(entryTitle)) {
				LOGGER.debug("Test Successful: Comment successfully retrieved from the feed");
				assertTrue(true);

				String type = e.getContentType().toString();
				LOGGER.debug(e.getContent());
				assertEquals("text", type.toLowerCase());

				break;
			}
		}

		if (!found) {
			LOGGER.debug("Test Failed: Comment not found");
			assertTrue(false);
		}
	}

	@Test
	public void getAllBlogs() {
		LOGGER.debug("BEGINNING TEST: Get all Blogs");
		service.deleteTests();

		// create blog
		String blogTitle = "JamesBlog";
		Blog regBlog = new Blog(blogTitle, "APIRegularBlog19",
				"This blog is for testing and verifying regular blog creation",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());

		// retrieve all blogs
		ArrayList<Blog> allBlogs = service.getAllBlogs(null, null, 0, 0, null,
				null, null, null, BlogsType.BLOG, null, null);
		if (allBlogs.get(0).getTitle().equals(blogTitle)) {
			LOGGER.debug("SUCCESS: Feed of all blogs was found, first blog in list matched new blog");
		} else if (allBlogs.get(1).getTitle().equals(blogTitle)) {
			LOGGER.debug(" -- First blog : " + allBlogs.get(0).getTitle()
					+ " in list did not match new blog : " + blogTitle);
			LOGGER.debug("SUCCESS: Feed of all blogs was found, second blog in list matched new blog");

		} else if (allBlogs.get(2).getTitle().equals(blogTitle)) {
			LOGGER.debug(" -- Second blog : " + allBlogs.get(1).getTitle()
					+ " in list did not match new blog : " + blogTitle);
			LOGGER.debug("SUCCESS: Feed of all blogs was found, third blog in list matched new blog");
		} else {
			assertEquals(blogTitle, allBlogs.get(3).getTitle());
		}

		LOGGER.debug("COMPLETED TEST: Get All Blogs");
	}

	public ArrayList<Blog> getAllBlogs(String email, BlogsField field,
			int page, int pageSize, String textSearch, java.sql.Date since,
			SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags,
			String userid) {
		ArrayList<Blog> allBlogs = new ArrayList<Blog>();
		ExtensibleElement allBlogsFeed = service.searchBlogs(
				service.getServiceURLString() + "/"
						+ service.getBlogsHomepageHandle()
						+ URLConstants.BLOGS_ALL, email, field, page, pageSize,
				textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("search to get Blogs failed", 200, service.getRespStatus());

		if (allBlogsFeed != null) {
			for (Entry blog : ((Feed) allBlogsFeed).getEntries()) {
				Blog data = new Blog(blog);
				allBlogs.add(data);
			}
		}

		return allBlogs;
	}

	// 69039: Roll back paging of subscription feed api from 1-based to 0-based
	@Test
	public void verifyBlogPageReturn() {
		LOGGER.debug("BEGINNING TEST: Verify Blog Page Return");
		service.deleteTests();

		// need at least two blogs to get the second page of the listing
		// create blog 1
		Blog regBlog1 = new Blog("JamesBlog", "APIRegularBlog19",
				"This blog is for testing and verifying regular blog creation",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		service.createBlog(regBlog1);
		assertEquals("Create blog", 201, service.getRespStatus());

		Blog regBlog2 = new Blog("Spencer", "APIRegularBlog19",
				"This blog is for testing and verifying regular blog creation",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		service.createBlog(regBlog2);
		assertEquals("Create blog", 201, service.getRespStatus());

		// retrieve the feed
		Feed secondBlogFeed = service.getAllBlogsFeed(null, null, 1, 1, null,
				null, null, null, BlogsType.BLOG, null, null);
		assertEquals("getAllBlogsFeed", 200, service.getRespStatus());

		// should only return 1 -- check to see if <link rel="next" has page=3
		Link myLink = secondBlogFeed.getLink("next");
		String query = myLink.getHref().getQuery();

		if (query.contains("sortby=0&page=2")) {
			LOGGER.debug("SUCCESS: Feed returned the proper page of Blog listings");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Feed returned wrong page of Blog listings");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Verify Blog Page Return");
	}

	// 69794: Add entry's edit link in each entry item of blogs subscription
	// feed
	@Test
	public void verifyBlogEntryEditLinkExists() {
		LOGGER.debug("BEGINNING TEST: Verify Blog Entry Contains Edit Link");
		service.deleteTests();
		LOGGER.debug("Method : verifyBlogEntryEditLinkExists bloghomepage handle" + service.getBlogsHomepageHandle());

		Blog regBlog = new Blog("Spencer", "APIRegularBlog19",
				"This blog is for testing and verifying regular blog creation",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);
		assertEquals("BlogCreate", "Spencer", regBlog.getTitle());

		// create entry in a blog
		BlogPost post = new BlogPost("editLinkTest",
				"Spencer uses his mechanical arm", "BIONIC", true, 4);
		ExtensibleElement eblogPost = service.createPost(regBlog, post);
		LOGGER.debug("Created entry to blog");
		post = new BlogPost((Entry) eblogPost);
		assertTrue("Verify Blog Post EditLink", post.getEditLink() != null);

		/*
		 * ArrayList<BlogPost> blogPosts =
		 * service.getBlogEntries(regBlog.getTitle(), null, null, 0, 0, null,
		 * null, null, null, BlogsType.BLOG, null, null);
		 * 
		 * if (blogPosts.get(0).getEditLink() != null) {
		 * LOGGER.debug("SUCCESS: Blog Entry Contains Edit Link");
		 * assertTrue(true); } else {
		 * LOGGER.debug("ERROR: Blog Entry Does Not Contains Edit Link: " +
		 * blogPosts.get(0).toString()); assertTrue(false); }
		 */
		LOGGER.debug("COMPLETED TEST: Verify Blog Entry Contains Edit Link");
	}

	@Test
	public void getBlogEntries() {
		LOGGER.debug("BEGINNING TEST: Get Blog Entries");
		service.deleteTests();

		// create blog
		String blogTitle = "JamesBlog";
		Blog regBlog = new Blog(blogTitle, "APIRegularBlog19",
				"This blog is for testing and verifying regular blog creation",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);
		assertEquals("BlogCreate", blogTitle, regBlog.getTitle());

		// create blog entry
		BlogPost post = new BlogPost("JamesPost", "Cool blog post from API",
				"first post blog", true, 5);
		Entry result = (Entry) service.createPost(regBlog, post);
		BlogPost postResponse = new BlogPost(result);
		assertEquals("BlogPost Create", "JamesPost", postResponse.getTitle());

		// get all blog entires for regBlog
		/*
		 * ArrayList<BlogPost> blogPosts =
		 * service.getBlogEntries(regBlog.getTitle(), null, null, 0, 0, null,
		 * null, null, null, BlogsType.BLOG, null, null);
		 * 
		 * //validate
		 * if(blogPosts.get(0).getTitle().equals(postResponse.getTitle())){
		 * LOGGER.debug("SUCCESS: Blog Entry was found"); assertTrue(true); }
		 * else{ LOGGER.debug("ERROR: Blog Entry was not found");
		 * assertTrue(false); }
		 */
		LOGGER.debug("COMPELTED TEST: Get Blog Entries");
	}

	@Test
	public void verifyBlogEntryWithAmpersand() {
		LOGGER.debug("Start Test: Verify Blog Entry with Ampersand");

		// delete all the pervious tests used
		service.deleteTests();

		String blogHandle = "BionicArm";
		String entryTitle = "BionicLancer&";

		String escTitle = StringEscapeUtils.escapeXml(entryTitle);

		// variable to see if the correct result was shown
		boolean found = false;

		// create a blog to test with
		Blog regBlog = new Blog("Spencer", blogHandle,
				"Soldier that lost his arm in the war.", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		// create entry in a blog
		BlogPost post = new BlogPost(entryTitle,
				"Spencer uses his mechanical arm", "BIONIC", true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, post);
		BlogPost response = new BlogPost(postResult);
		LOGGER.debug("Created entry to blog");

		// create a comment to the entry
		BlogComment comment = new BlogComment("bionic ARMMM", postResult);
		Entry checkComment = (Entry) service.createComment(response, comment);
		if (checkComment == null) {
			LOGGER.debug("Comment was not made successfully");
			assertTrue(false);
		}
		LOGGER.debug("Created comment to blog entry");

		// test to see if the comment was successfully made
		Feed commentFeed = (Feed) service
				.getComment(response.getCommentsHref());
		for (Entry e : commentFeed.getEntries()) {
			String eTS = e.toString();
			String s = "<title type=\"text\">Re: " + escTitle;

			if (eTS.contains(s)) {
				LOGGER.debug("Test Successful: Blog Entry Escaped Properly");
				found = true;
				assertTrue(true);
				break;
			}
		}

		if (!found) {
			LOGGER.debug("Test Failed: Blog Entry not Escaped Properly");
			assertTrue(false);
		}
		LOGGER.debug("Complete Test: Verify Blog Entry with Ampersand");
	}

	@Test
	public void getAllBlogPostTags() {
		service.deleteTests();
		LOGGER.debug("BEGINNING TEST: Get List of All Entry Tags");

		ArrayList<Category> tags = service.getAllBlogsTags();

		if (tags != null) {
			LOGGER.debug("SUCCESS: List of tags was obtained");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: List of tags was not obtained");
			assertTrue(false);
		}
		LOGGER.debug("COMPELTED TEST: Get List of All Entry Tags");
	}

	@Test
	public void getTagsForSingleBlog() {
		LOGGER.debug("BEGINNING TEST: Get List of Tags for a Blog");
		service.deleteTests();

		// create blog
		String blogTitle = "JamesBlog";
		Blog regBlog = new Blog(blogTitle, "TheBostonCeltics",
				"This blog is for testing and verifying regular blog creation",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		// create blog entry with tag
		String tag = "RONDO";
		BlogPost post = new BlogPost("JamesPost", "Cool blog post from API",
				tag, true, 5);
		service.createPost(regBlog, post);

		// get tags for the new blog
		ArrayList<Category> tags = service.getBlogTags(regBlog
				.getHandleElement().getText());
		ArrayList<String> tagTerms = new ArrayList<String>();
		for (Category c : tags)
			tagTerms.add(c.getTerm());

		// validate that the tag was found
		if (tagTerms.contains(tag.toLowerCase())) {
			LOGGER.debug("SUCCESS: New Tag was found in list of all tags");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: New tag was not found in list of all tags");
			assertTrue(false);
		}
		LOGGER.debug("COMPELTED TEST: Get Tags for a Blog");
	}

	@Test
	public void getFeaturedBlogs() {
		LOGGER.debug("BEGINNING TEST: Get Feed of Featured Blogs");

		ArrayList<Blog> featuredBlogs = service.getFeaturedBlogs(null, null, 0,
				0, null, null, null, null, BlogsType.BLOG, null, null);
		if (featuredBlogs != null) {
			LOGGER.debug("SUCCESS: Feed of featured blogs was successfully found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Feed of featured blogs was not found");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get Feed of Featured Blogs");
	}

	@Test
	public void searchBlogs() {
		LOGGER.debug("Start Test: Searching for a blog");
		// check to make sure the address has a valid feed
		if (service.searchBlogsFeed("Spencer") != null) {
			LOGGER.debug("Test Successful: Found a valid feed for search");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: No valid feed found for search");
			assertTrue(false);
		}

	}

	@Test
	public void getFeaturedBlogPosts() {
		LOGGER.debug("BEGINNING TEST: Get feed of featured blog posts");
		ArrayList<BlogPost> featuredBlogPosts = getFeaturedBlogs(null,
				BlogsField.ALL, 0, 0, null, null, null, null, BlogsType.BLOG,
				null, null);
		if (featuredBlogPosts != null) {
			LOGGER.debug("SUCCESS: Feed of featured blog posts was successfully found");
			assertTrue(true);
		} else {
			LOGGER.debug("ERROR: Feed of featured blog posts was not found");
			assertTrue(false);
		}
		LOGGER.debug("COMPELTE TEST: Get feed of featured blog posts");
	}

	public ArrayList<BlogPost> getFeaturedBlogs(String email, BlogsField field,
			int page, int pageSize, String textSearch, java.sql.Date since,
			SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags,
			String userid) {
		ArrayList<BlogPost> featuredPosts = new ArrayList<BlogPost>();
		// Feed featuredPostsFeed = (Feed)
		// searchBlogs(service.getServiceURLString() + "/" +
		// getBlogsHomepageHandle() +
		// URLConstants.BLOGS_FEATURED_POSTS, email, field, page, pageSize,
		// textSearch, since, sortBy, sortOrder, type, tags, userid);
		ExtensibleElement featuredPostsFeed = service.searchBlogs(
				service.getServiceURLString() + "/"
						+ service.getBlogsHomepageHandle()
						+ URLConstants.BLOGS_FEATURED_POSTS, email, field,
				page, pageSize, textSearch, since, sortBy, sortOrder, type,
				tags, userid);

		if (service.getRespStatus() == 404) {
			LOGGER.debug("Blogs getFeaturedPosts is not indexed");
			return featuredPosts;
		}
		assertTrue("Blogs getFeaturedPosts is not indexed",
				!(service.getRespStatus() == 404));
		assertEquals("Blogs getFeaturedBlogs failed", 200,
				service.getRespStatus());

		if (featuredPostsFeed != null) {
			for (Entry blogPostEntry : ((Feed) featuredPostsFeed).getEntries()) {
				BlogPost data = new BlogPost(blogPostEntry);
				featuredPosts.add(data);
			}
		}
		return featuredPosts;
	}

	@Test
	// can not do accurate test because of 10 minute cache, so just check that
	// it exists
	public void getTagsForTypeahead() {
		LOGGER.debug("Beginning Test: Find the tages for typeahead");
		ExtensibleElement typeAhead = service.getTypeaheadTags();

		if (typeAhead != null) {
			LOGGER.debug("Test Successful: Found the tags for typeahead");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: No tags found for typeahead");
			assertTrue(false);
		}
	}

	@Test
	public void getSubscriptionAllBlogsPage() throws Exception {
		LOGGER.debug("Beginning Test: Test Subscription feed to see if it is base 0");
		service.deleteTests();
		boolean found = true;
		int index = 0;
		String nextPage;

		String blogHandle = "BionicArm";
		String entryTitle = "BionicLancer";

		BlogPost post = null;
		Entry postResult = null;
		BlogPost response = null;

		// create a blog to test with
		Blog regBlog = new Blog("Spencer", blogHandle,
				"Soldier that lost his arm in the war.", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		LOGGER.debug("Created Blog");

		// create entry in a blog
		for (int i = 0; i < 3; i++) {
			post = new BlogPost(entryTitle + i,
					"Spencer uses his mechanical arm", "BIONIC", true, 4);
			postResult = (Entry) service.createPost(regBlog, post);
			response = new BlogPost(postResult);
			LOGGER.debug("Created entry to blog");
			service.postRecommend(postResult.getExtension(
					StringConstants.APP_COLLECTION).getAttributeValue("href"));
		}

		// create a comment to the entry
		for (int i = 0; i < 3; i++) {
			BlogComment comment = new BlogComment("BionicArm" + i, postResult);
			Entry checkComment = (Entry) service.createComment(response,
					comment);
			if (checkComment == null) {
				LOGGER.debug("Comment was not made successfully");
				assertTrue(false);
			}
		}
		LOGGER.debug("Created comment to blog entry");

		// -------------------------------------------------------------------------------------------------------------------------------------------
		Feed allBlogs = service.getAllBlogsFeed(null, null, 0, 2, null, null,
				null, null, null, null, null);
		assertEquals("getAllBlogsFeed", 200, service.getRespStatus());

		if (allBlogs.getLinkResolvedHref("next").toString() != null) {
			index = allBlogs.getLinkResolvedHref("next").toString()
					.indexOf("page=");
			nextPage = allBlogs.getLinkResolvedHref("next").toString()
					.substring(index + 5);
			if (nextPage.startsWith("1")) {
				LOGGER.debug("PASS: All Blogs is base 0");
			} else {
				LOGGER.debug("Failed: All Blogs is not base 0");
				found = false;
			}
		}
		// -------------------------------------------------------------------------------------------------------------------------------------------
		Feed latestPosts = service.getLatestPostsFeed(null, null, 0, 2, null,
				null, null, null, null, null, null);

		if (latestPosts.getLinkResolvedHref("next").toString() != null) {
			index = latestPosts.getLinkResolvedHref("next").toString()
					.indexOf("page=");
			nextPage = latestPosts.getLinkResolvedHref("next").toString()
					.substring(index + 5);
			if (nextPage.startsWith("1")) {
				LOGGER.debug("PASS: Latest Posts is base 0");
			} else {
				LOGGER.debug("Failed: Latest Posts is not base 0");
				found = false;
			}
		}

		// -------------------------------------------------------------------------------------------------------------------------------------------
		Feed latestComments = service.getLatestCommentsFeed(null, null, 0, 2,
				null, null, null, null, null, null, null);

		if (latestComments.getLinkResolvedHref("next").toString() != null) {
			index = latestComments.getLinkResolvedHref("next").toString()
					.indexOf("page=");
			nextPage = latestComments.getLinkResolvedHref("next").toString()
					.substring(index + 5);
			if (nextPage.startsWith("1")) {
				LOGGER.debug("PASS: Latest Comments is base 0");
			} else {
				LOGGER.debug("Failed: Latest Comments is not base 0");
				found = false;
			}
		}
		// -------------------------------------------------------------------------------------------------------------------------------------------
		Feed recommendedBlogs = service.getRecommendedPostsFeed(null, null, 0,
				2, null, null, null, null, null, null, null);

		if (recommendedBlogs.getLinkResolvedHref("next").toString() != null) {
			index = recommendedBlogs.getLinkResolvedHref("next").toString()
					.indexOf("page=");
			nextPage = recommendedBlogs.getLinkResolvedHref("next").toString()
					.substring(index + 5);
			if (nextPage.startsWith("1")) {
				LOGGER.debug("PASS: Recommended Blogs is base 0");
			} else {
				LOGGER.debug("Failed: Recommended Blogs is not base 0");
				found = false;
			}
		}
		// -------------------------------------------------------------------------------------------------------------------------------------------
		/*
		 * Feed mediaEntries = service.getMediaEntriesFeed(blogHandle, 0, 2);
		 * 
		 * if(mediaEntries.getLinkResolvedHref("next").toString() != null){
		 * index =
		 * mediaEntries.getLinkResolvedHref("next").toString().indexOf("page=");
		 * nextPage =
		 * mediaEntries.getLinkResolvedHref("next").toString().substring
		 * (index+5); if(nextPage.startsWith("1")){
		 * LOGGER.debug("PASS: Media Entries is base 0"); } else{
		 * LOGGER.debug("Failed: Media Entries is not base 0"); found = false; }
		 * }
		 */
		// -------------------------------------------------------------------------------------------------------------------------------------------
		/*
		 * Feed defaultBlog = service.getDefaultBlogFeed(blogHandle, userId, 0,
		 * 2, null, null, null, null);
		 * 
		 * if(defaultBlog.getLinkResolvedHref("next").toString() != null){ index
		 * =
		 * defaultBlog.getLinkResolvedHref("next").toString().indexOf("page=");
		 * nextPage =
		 * defaultBlog.getLinkResolvedHref("next").toString().substring
		 * (index+5); if(nextPage.startsWith("1")){
		 * LOGGER.debug("PASS: Default Blog is base 0"); } else{
		 * LOGGER.debug("Failed: Default Blogs is not base 0"); found = false; }
		 * }
		 */
		// -------------------------------------------------------------------------------------------------------------------------------------------
		Feed commentsAddedBlogs = service.getCommentsAddedBlogsFeed(blogHandle,
				0, 2);

		if (commentsAddedBlogs.getLinkResolvedHref("next") != null) {
			index = commentsAddedBlogs.getLinkResolvedHref("next").toString()
					.indexOf("page=");
			nextPage = commentsAddedBlogs.getLinkResolvedHref("next")
					.toString().substring(index + 5);
			if (nextPage.startsWith("1")) {
				LOGGER.debug("PASS: Comments Added to Blog is base 0");
			} else {
				LOGGER.debug("Failed: Comments Added to Blog is not base 0");
				found = false;
			}
		}
		// -------------------------------------------------------------------------------------------------------------------------------------------
		Feed recentPostsBlogs = service.getRecentPostsBlogsFeed(blogHandle, 0,
				2, null, null, null, null, null);

		if (recentPostsBlogs.getLinkResolvedHref("next") != null) {
			index = recentPostsBlogs.getLinkResolvedHref("next").toString()
					.indexOf("page=");
			nextPage = recentPostsBlogs.getLinkResolvedHref("next").toString()
					.substring(index + 5);
			if (nextPage.startsWith("1")) {
				LOGGER.debug("PASS: Recent Blog Posts is base 0");
			} else {
				LOGGER.debug("Failed: Recent Blog Posts is not base 0");
				found = false;
			}
		}

		assertTrue(found);
	}

	@Test
	public void incorrectTagUrl() {
		/*
		 * Test Process: 1. Get the handle of the homepage blog.
		 * 
		 * 2. Use this url to get a feed:
		 * http://<host>:<port>/blogs/roller-ui/rendering
		 * /feed/<homepage>/blogs/atom?lang=en. (Replace <homepage> with the
		 * handle of frontpage blog from step #1.)
		 * 
		 * 3. Check the result feed, find the <app:categories>, for example:
		 * <app:categories href=
		 * "https://bbcdev.cn.ibm.com:9444/blogs/homepage/feed/blogtags/atom?lang=en"
		 * />
		 * 
		 * 4. Check the href attribute of this element, the url pattern should
		 * be
		 * "http://<host>:<port>/blogs/<homepage>/feed/blogtags/atom?lang=xx".
		 * The important fragment is "feed/blogtags/atom"
		 */

		LOGGER.debug("BEGINNING TEST: RTC 86527 Incorrect Blog tag URL.");
		String hompageHandle = service.getBlogsHomepageHandle();
		// Typically, it is preferable to programmatically get URLs from the
		// application. In this case the developer provided this url in the
		// defect.
		// Not sure where this URL can be found in application feeds/server doc.
		String urlToRetrieveFeed = URLConstants.SERVER_URL
				+ "/blogs/roller-ui/rendering/feed/" + hompageHandle
				+ "/blogs/atom?lang=en";

		ExtensibleElement eelement = service.getBlogFeed(urlToRetrieveFeed);
		assertEquals("getBlogs failed "+service.getDetail(), 200, service.getRespStatus());

		Feed blogFeed = ((Feed)eelement).getAsFeed();

		List<Element> lmnt = blogFeed.getElements();
		for (Element e : lmnt) {
			if (e.toString().contains("app:categories")) {
				for (QName atrb : e.getAttributes()) {
					if (atrb.toString().equalsIgnoreCase("href")) {
						assertTrue(e.getAttributeValue("href").contains(
								"feed/blogtags/atom"));
					}
				}
			}
		}
		LOGGER.debug("ENDING TEST: RTC 86527 Incorrect Blog tag URL.");
	}

	@Test
	public void blogsCsrf() throws FileNotFoundException, IOException {
		// TTT DB Blogs - not an RTC defect/story
		LOGGER.debug("BEGINNING TEST: Blogs - Connections core API CSRF fix.");
		// 1. Create blog with an entry
		// 2. POST a comment with Origin header
		// 3. Validate 403 return.

		Blog blog = new Blog("Connections core API CSRF fix.", "API_CSRF_Blog",
				"Connections core API CSRF fix.", null, false, false, null,
				null, TimeZone.getDefault(), true, 13, true, true, true, 0, -1,
				null, null, null, 0);
		Entry blogEntry = (Entry) service.createBlog(blog);
		assertEquals("Create blog", 201, service.getRespStatus());
		Blog blogResult = new Blog(blogEntry);

		assert (blogResult != null);

		BlogPost post = new BlogPost("CSRF test", "Post from API blogsCfrs()",
				"first post blog", true, 5);
		Entry result = (Entry) service.createPost(blogResult, post);
		BlogPost response = new BlogPost(result);
		LOGGER.debug("Created an entry to blog");

		// create a comment to the entry
		BlogComment comment = new BlogComment("bionic ARMMM", result);
		service.createComment(response, comment);

		int returnCode = service.createCommentCrx(blogResult, comment);

		if (returnCode == 403) {
			LOGGER.debug("Correct response returned: " + returnCode
					+ ".  This test should fail.");
		} else {
			LOGGER.debug("Incorrect response returned: " + returnCode
					+ ".  Instead the correct response is HTTP 403");
		}
		LOGGER.debug("COMPLETED TEST: Blogs - Connections core API CSRF fix.");
	}

	@Test
	public void blogsFollowsService() throws FileNotFoundException, IOException {

		/*
		 * Process: 1. Create a blog 2. Create a blog post 3. Set up a separate
		 * blogs 'service' as another user 4. Got URL for blogs follow service
		 * 5. Validate follow count as for current user and other user 6. Create
		 * entry for follow service, execute as current user and another user 7.
		 * Validate follow count increment 8. Remove follow(s) 9. Validate
		 * follow count is 0 again.
		 */
		LOGGER.debug("BEGINNING TEST: Blogs Follows Service.  From TTT db - not RTC Story or defect.");
		UserPerspective usr5=null;
		try {
			usr5 = new UserPerspective(5,
					Component.BLOGS.toString(), useSSL);
		} catch (LCServiceException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}

		Blog blog = new Blog("Test Blogs Follow Service", "Blog_Follow",
				"Follow Service test.", null, false, false, null, null,
				TimeZone.getDefault(), true, 13, true, true, true, 0, -1, null,
				null, null, 0);
		String[] ownerEmails = { usr5.getEmail() };
		Entry blogEntry = (Entry) service.createBlogWithMembers(blog,
				ownerEmails, null);
		Blog blogResult = new Blog(blogEntry);

		assert (blogResult != null);

		// Parse blogResult - get the id.
		String blogEditUrl = blogResult.getEditLink();
		String id = blogEditUrl.substring(blogEditUrl.lastIndexOf("/") + 1);

		// Create blog post as default user
		BlogPost post = new BlogPost("Follow Post1", "Follow Post1 from API",
				"Follow_first_post", true, 5);
		Entry result = (Entry) service.createPost(blogResult, post);
		BlogPost response = new BlogPost(result);
		LOGGER.debug("Created an entry to blog");

		// Log in as another user
		BlogsService srvc = usr5.getBlogsService();

		// Get the follow service url.
		// http://lc45linux1.swg.usma.ibm.com/blogs/follow/atom/service
		String followUrl = "";
		ExtensibleElement ee1 = service.getBlogFeed(URLConstants.SERVER_URL
				+ "/blogs/follow/atom/service");
		for (Element e1 : ee1.getElements()) {
			if (e1.toString().startsWith("<workspace")) {
				for (Element e2 : e1.getElements()) {
					if (e2.toString().startsWith("<collection")) {
						for (QName atrb : e2.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("href")) {
								assertTrue(e2.getAttributeValue("href")
										.contains("follow/atom/resources"));
								followUrl = e2.getAttributeValue("href");
							}
						}
					}
				}
			}
		}

		// Get a feed of the follow service, validate follow count for both
		// users.
		ExtensibleElement ee = service.getBlogFeed(followUrl);
		ExtensibleElement ee2 = srvc.getBlogFeed(followUrl);
		int followCounts = Integer.parseInt(ee.getExtension(
				StringConstants.OPENSEARCH_TOTALRESULTS).getText());
		int followCounts2 = Integer.parseInt(ee2.getExtension(
				StringConstants.OPENSEARCH_TOTALRESULTS).getText());
		LOGGER.debug("Start follow count for " + StringConstants.USER_REALNAME
				+ " : " + followCounts);
		LOGGER.debug("Start follow count for " + usr5.getRealName() + " : "
				+ followCounts);

		// Create an entry to execute a POST event. This will increment follow
		// count.
		Factory factory = abdera.getFactory();
		Entry entry = factory.newEntry();
		entry.setTitle("Blogs Follow");
		entry.setId("ignored");
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/source", "blogs",
				"");
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-type",
				"blog", "");
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-id", id,
				"");
		entry.toString();

		// POST follow as another user, not the default user.
		service.postBlogsFeed(followUrl, entry);
		srvc.postBlogsFeed(followUrl, entry);
		/*
		 * try { int ret = srvc.doPostAtom(followUrl, entry.toString()); } catch
		 * (Exception e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		// sleep added for lc45linux2
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			LOGGER.debug(e.getMessage());
		}

		// Validate follow count 1+
		ee = service.getBlogFeed(followUrl);
		ee2 = srvc.getBlogFeed(followUrl);
		assertEquals(String.valueOf(++followCounts),
				ee.getExtension(StringConstants.OPENSEARCH_TOTALRESULTS)
						.getText());
		assertEquals(String.valueOf(++followCounts2),
				ee2.getExtension(StringConstants.OPENSEARCH_TOTALRESULTS)
						.getText());
		LOGGER.debug("After add the follow count for "
				+ StringConstants.USER_REALNAME + " : " + followCounts);
		LOGGER.debug("After add the follow count for " + usr5.getRealName()
				+ " : " + followCounts);

		// Remove the follow with DELETE
		// Use the follow URL to generate feed, get the edit href. This link is
		// used for the DELETE operation to un-follow.
		// We're using a loop to remove all follow entries.
		ExtensibleElement ee3 = service.getBlogFeed(followUrl);
		for (Element e1 : ee3.getElements()) {
			if (e1.toString().startsWith("<entry")) {
				for (Element e2 : e1.getElements()) {
					if (e2.toString().startsWith("<link")) {
						for (QName atrb : e2.getAttributes()) {
							if (atrb.toString().equalsIgnoreCase("rel")
									&& e2.getAttributeValue("rel")
											.equalsIgnoreCase("edit")) {
								service.deletePost(e2.getAttributeValue("href"));
								srvc.deletePost(e2.getAttributeValue("href"));
							}
						}
					}
				}
			}
		}

		// Validate count - Should be 0 at this point.
		ee = service.getBlogFeed(followUrl);
		ee2 = srvc.getBlogFeed(followUrl);

		// start count
		assertEquals(String.valueOf(--followCounts),
				ee.getExtension(StringConstants.OPENSEARCH_TOTALRESULTS)
						.getText());
		assertEquals(String.valueOf(--followCounts2),
				ee2.getExtension(StringConstants.OPENSEARCH_TOTALRESULTS)
						.getText());

		LOGGER.debug("ENDING TEST: Blogs Following Service Test.");
	}

	@Test
	public void deleteComment() {
		/*
		 * Tests ability to delete comments 
		 * step 1. create blog, check 
		 * step 2. create entry in blog, check 
		 * step 3. create comment in entry, check
		 * step 4. delete comment, check
		 */
		LOGGER.debug("beggining test... deleteComments");
		LOGGER.debug("beggining step 1...create comment, check");

		// create a blog to test with
		Blog regBlog = new Blog("NHL Stanley Cup", "My first blog",
				"Because its the cup", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog"+service.getDetail(), 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);
		// Entry myBlog = (Entry) service.createBlog(regBlog);
		// LOGGER.debug("Created Blog");
		// assertTrue(myBlog.getId()!= null); //check to see if blog was made

		LOGGER.debug("beggining step 2...create entry in blog");

		// create entry in a blog
		BlogPost post = new BlogPost("Champion Predictions",
				"Who I think will win the cup", "HOCKEY", true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, post);
		assertTrue(postResult.getId() != null);

		LOGGER.debug("begining step 3...create comment in entry");
		// create a comment to the entry
		String commentTitle = "GO BRUINS!";
		BlogComment comment = new BlogComment(commentTitle, postResult);
		Entry theComment = (Entry) service.createComment(regBlog, comment);
		assertTrue(theComment.getId() != null);// check if comment was made

		LOGGER.debug("begining step 4...delete comment");
		Link commentLink = theComment.getEditLink();
		String commentEditUrl = commentLink.getHref().toString();

		// fix the edit link url by adding the comments path:
		// the edit link populated from the create comment is wrong in its
		// returned object.
		// it is missing the "/comments/" path;
		// Samples:
		// good edit link url:
		// https://lc45linux1.swg.usma.ibm.com:443/blogs/Myfirstblog2/api/comments/d60a3b44-3dc3-4687-9c3a-552313fb7435
		// bad edit link url:
		// https://lc45linux1.swg.usma.ibm.com:443/blogs/Myfirstblog2/api/d60a3b44-3dc3-4687-9c3a-552313fb7435
		int i = commentEditUrl.indexOf("/api/");
		String fixedEditUrl = commentEditUrl.substring(0, i);
		fixedEditUrl += "/api/comments/";
		fixedEditUrl += commentEditUrl.substring(i + 5);

		boolean bResult = service.deleteComment(fixedEditUrl);
		assertTrue("delete comment failed", bResult);
		LOGGER.debug("ending test");

	}

	@Test
	public void deleteEntry() {
		/*
		 * Tests ability to delete an entry step 1. create blog, check step 2.
		 * create entry in blog, check step 3. delete entry, check
		 */

		LOGGER.debug("beggining test...step 1...create blog");
		// create a blog to test with
		Blog regBlog = new Blog("NHL Stanley Cup", "My first blog",
				"Because its the cup", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);
		// Entry myBlog = (Entry) service.createBlog(regBlog);
		// LOGGER.debug("Created Blog");
		// assertTrue(myBlog.getId()!= null); //check to see if blog was made

		LOGGER.debug("beggining step 2...create entry in blog");

		// create entry in a blog
		BlogPost post = new BlogPost("Blackhawks vs Bruins",
				"Which team will win the cup?", "HOCKEY"
						+ Utils.logDateFormatter.format(new Date()), true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, post);
		assertTrue(postResult.getId() != null);
		LOGGER.debug("created entry");

		LOGGER.debug("beggining step 3...delete entry in blog");
		Link editLink = postResult.getEditLink();
		String editUrl = editLink.getHref().toString();

		boolean bresult = service.deletePost(editUrl);
		assertTrue(bresult);// checks if post was deleted

		Entry verifyPost = (Entry) service.getPost(editUrl);
		assertTrue(verifyPost.getId() == null);// double check if post is
		// deleted

		LOGGER.debug("ending test....");
	}

	@Test
	public void getRecommendedCommentsNOTDONE() throws Exception {
		/*
		 * tests ability to get recommended commentss1 create blog, checks2
		 * create entry(s), checks3 create reccomend comment, checks4 get
		 * reccomended comment, check
		 */
		LOGGER.debug("beggining test...step 1...create blog");
		// create a blog to test with
		Blog regBlog = new Blog("E3", "My next blog", "Video Game Convention",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		Entry myBlog = (Entry) service.createBlog(regBlog);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);
		// LOGGER.debug("Created Blog");
		// assertTrue(myBlog.getId()!= null); //check to see if blog was made

		LOGGER.debug("step 2... create a blog entry / upload pic");
		BlogPost newPost = new BlogPost("PS4 Games", "Games to look out for",
				"Gaming" + Utils.logDateFormatter.format(new Date()), true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, newPost);
		assertTrue(postResult.getId() != null);// check
		LOGGER.debug("created entry");

		LOGGER.debug("begining step 3...create rec. comment in entry");
		// create a comment(s) to the entry
		String commentTitle = "These games are awesome!";
		String commentCall = "I cant wait for this!";
		BlogComment comment = new BlogComment(commentTitle, postResult);
		Entry theComment = (Entry) service.createComment(regBlog, comment);
		BlogComment nextComment = new BlogComment(commentCall, postResult);
		Entry myComment = (Entry) service.createComment(regBlog, nextComment);
		assertTrue(myComment.getId() != null);// check if comment was made
		assertTrue(theComment.getId() != null);// check if comment was made

		// LOGGER.debug("Getting Rec Comments Feed");
		// Feed commentFeed =
		// (Feed)service.getCommentsAddedBlogsFeed("Handle16213314712", 1, 10);
		// Link commentsFeed = theComment.getEditLink();
		// String commentLink = commentsFeed.getHref().toString();

		LOGGER.debug("checking if feed is retrieved.");
		// assertTrue(commentFeed != null);
		String commentIs = "The recommend comment is this.";
		BlogComment recComment = new BlogComment(commentIs, postResult);
		Entry commentsR = (Entry) service.createComment(regBlog, recComment);

		String recPost = "";
		for (Element ele : commentsR.getElements()) {
			if (ele.toString().startsWith("<app:collection")) {
				recPost = ele.getAttributeValue("href");
			}
		}

		// Link commentR = commentsR.getEditLink();

		LOGGER.debug("creating recommend comment");
		// String recPost = commentR.getHref().toString();
		service.postRecommend(recPost);
		ExtensibleElement bresult = service.getComment(recPost);
		assertTrue(bresult != null);

		LOGGER.debug("Ending test...");

	}

	@Test
	public void editBlog() {
		/*
		 * Tests ability to edit a blog S1. create blog S2.call edit blog,
		 * verify
		 */

		LOGGER.debug("beggining test...step 1...create blog");
		// create a blog to test with
		Blog regBlog = new Blog("NHL Stanley Cup", "My first blog",
				"Because its the cup", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		Entry myBlog = (Entry) service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		LOGGER.debug("Created Blog");
		assertTrue(myBlog.getId() != null); // check to see if blog was made

		Link editLink = myBlog.getEditLink();
		String editUrl = editLink.getHref().toString();
		LOGGER.debug("Starting S2....");
		ExtensibleElement bresult = service.editBlog(editUrl, regBlog);
		assertTrue(bresult != null);

		LOGGER.debug("Ending test...");
	}

	@Test
	public void getDefaultBlogScreen() {
		if (!StringConstants.MODERATION_ENABLED) {
			/*
			 * Tests ability to get users default blog screen s1. create blog
			 * s2. get blog screen s4. verify
			 */
			LOGGER.debug("beggining test...step 1...create blog");
			// create a blog to test with
			Blog regBlog = new Blog("NHL Stanley Cup", "My first blog",
					"Because its the cup", "tagBlogs_"
							+ Utils.logDateFormatter.format(new Date()), false,
					false, null, null, TimeZone.getDefault(), true, 13, true,
					true, true, 0, -1, null, null, null, 0);
			Entry myBlog = (Entry) service.createBlog(regBlog);
			assertEquals("Create blog", 201, service.getRespStatus());

			LOGGER.debug("Created Blog");
			assertTrue(myBlog.getId() != null); // check to see if blog was made
			LOGGER.debug("begin s2. get blog screen");
			String theHandle = service.getBlogsHomepageHandle();

			LOGGER.debug("returning homepage url");
			String homepageUrl = service.getHompageUrl();
			ExtensibleElement theFeed = service.getBlogFeed(homepageUrl);
			assertEquals("get Blogs failed "+service.getDetail(), 200, service.getRespStatus());

			service.getDefaultBlogFeed(theHandle, StringConstants.USER_EMAIL,
					0, 10, null, null, null, null);

			LOGGER.debug("Ending test...");
		}
	}

	@Test
	public void getMyVotesFeed() {
		/*
		 * Tests endpoint: blogs/<homepageHandle>/feed/myvotes/atom 
		 * s1. create test blog... 
		 * s2. get my votes feed, check
		 */
		LOGGER.debug("beggining test...step 1...create blog");
		// create a blog to test with
		Blog regBlog = new Blog("NHL Stanley Cup", "My first blog",
				"Because its the cup", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		LOGGER.debug("step 2... create a blog entry / upload pic");
		BlogPost newPost = new BlogPost("PS4 Games", "Games to look out for",
				"Gaming" + Utils.logDateFormatter.format(new Date()), true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, newPost);
		assertTrue(postResult.getId() != null);// check
		LOGGER.debug("created entry");

		LOGGER.debug("begining s2");
		Feed myVotes = service.getMyVotesFeed(StringConstants.USER_EMAIL, null,
				0, 10, null, null, null, null, null, null, null);
		assertTrue(myVotes != null);

		LOGGER.debug("Ending test...");
	}

	@Test
	public void verifyUser() {
		/*
		 * Tests ability for LC to verfiy a user.... S1. call verify user S2.
		 * verify
		 */
		LOGGER.debug("Starting S1....");
		ExtensibleElement verUser = service.verifyUser();
		LOGGER.debug("Starting S2....");
		assertEquals(true, verUser.toString().contains("<userid>"));
		LOGGER.debug("Ending Test...");
	}

	@Test
	public void getACLTokens() {
		/*
		 * Test to get ACL S1. call ACL tokens S2. verify
		 */
		LOGGER.debug("Starting S1....");
		ExtensibleElement ACLToks = service.getACLTokens();
		LOGGER.debug("Starting S2....");
		assertEquals(true, ACLToks.toString().contains("<groupsforuser>"));
		LOGGER.debug("Ending test...");
	}

	/**
	 * Double Test:
	 * <ol>
	 * <li>Tests the ability to get a blogs entries tags
	 * <li>Tests the ability to get a blogs tags
	 * </ol>
	 * <ul>
	 * <li>uniqueTag (1) is "beatles_taxman_z06"</li>
	 * <li>uniqueTag (2) is "beatles_pennylane_z06"</li>
	 * </ul>
	 * - Please do not use outside of the SearchPopulate getBlogTags() test
	 * method
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 * 
	 * @see /blogs/{handle}/feed/tags/atom <br>
	 *      /blogs/{handle}/feed/blogtags/atom
	 * 
	 */
	// @Test
	public void getBlogTags() throws UnsupportedEncodingException {
		/**
		 * Step 1. Search the feed for blog entries that exist with the uniqueId
		 * and delete any entries in which the uniqueId is found in. Step 2.
		 * Verify that there are no entries that have the uniqueId in the
		 * current feed. Step 3. Create the entry with the uniqueId tag Step 4.
		 * Return to this test in "SearchPopulate.java" after re-indexing has
		 * occurred. Step 4.1: Get the document containing tags Step 4.2: Verify
		 * the tag appears in the document that contains tags
		 */

		LOGGER.debug("Beginning Test: getBlogTags() ");
		LOGGER.debug("Step 1. Search the feed for blog entries that exist with the uniqueId and delete any entries in which the uniqueId is found in.");

		String uniqueTag = "beatles_taxman_z06"
				+ Utils.dateFormatter.format(new Date());
		String secondUniqueTag = "beatles_pennylane_z06"
				+ Utils.dateFormatter.format(new Date());

		/*
		 * //Find entries with the uniqueTag and delete them. Feed entriesFeed =
		 * (Feed) (service.getBlogsFeed(service.getURLString() +
		 * "/roller-ui/rendering/feed/" + service.getBlogsHomepageHandle() +
		 * "/entries/atom")); Feed blogsFeed = (Feed)
		 * (service.getBlogFeed(service.getURLString() + "/" +
		 * service.getBlogsHomepageHandle() + "/feed/blogs/atom"));
		 * 
		 * //Blogs feed code block start List blogs = blogsFeed.getEntries();
		 * for (int i = 0 ; i<blogs.size(); i++){ Blog aBlog = new Blog((Entry)
		 * blogs.get(i)); String originalId = aBlog.getId().toString(); String
		 * id = originalId.substring(originalId.indexOf("blogs:blog-")+11,
		 * originalId.length()); String linkToBlog = service.getURLString()
		 * +"/"+ service.getBlogsHomepageHandle()+ "/api/blogs/" +id ; List
		 * tagList = aBlog.getTags(); if
		 * (tagList.toString().contains(secondUniqueTag))
		 * service.deletePost(linkToBlog); } //Blogs feed code block ends
		 * //Entries feed code block starts List entries =
		 * entriesFeed.getEntries(); for (int i = 0 ; i<entries.size(); i++){
		 * BlogPost postOnBlog = new BlogPost((Entry) entries.get(i)); String
		 * originalId = postOnBlog.getId().toString(); String id =
		 * originalId.substring(originalId.indexOf("entry-")+6,
		 * originalId.length()); String linkToEntry = service.getURLString()
		 * +"/"+ service.getBlogsHomepageHandle()+ "/api/entries/" +id ; List
		 * tagList = postOnBlog.getTags(); if
		 * (tagList.toString().contains(uniqueTag))
		 * service.deletePost(linkToEntry); } //Entries feed code block end
		 * 
		 * LOGGER.debug(
		 * "Step 2. Verify that there are no entries that have the uniqueId in the current feed."
		 * ); //Refresh the feed, then search for entries with the uniqueTag
		 * again and fail the test if they are still found blogsFeed = (Feed)
		 * (service.getBlogFeed(service.getURLString() + "/" +
		 * service.getBlogsHomepageHandle() + "/feed/blogs/atom")); entriesFeed
		 * = (Feed) (service.getBlogsFeed(service.getURLString() +
		 * "/roller-ui/rendering/feed/" + service.getBlogsHomepageHandle() +
		 * "/entries/atom"));
		 * 
		 * //Entries code block entries = entriesFeed.getEntries(); for (int i =
		 * 0 ; i<entries.size(); i++){ BlogPost postOnBlog = new
		 * BlogPost((Entry) entries.get(i)); List tagList =
		 * postOnBlog.getTags(); if (tagList.toString().contains(uniqueTag)){
		 * LOGGER
		 * .debug("Test results invalidated: Entries still exist with the uniqueTag."
		 * ); assertTrue(false); } } //Blogs code block blogs =
		 * blogsFeed.getEntries(); for (int i = 0 ; i<entries.size(); i++){ Blog
		 * postOnBlog = new Blog((Entry) entries.get(i)); List tagList =
		 * postOnBlog.getTags(); if
		 * (tagList.toString().contains(secondUniqueTag)){ LOGGER.debug(
		 * "Test results invalidated: Blog(s) still exist with the secondUniqueTag."
		 * ); assertTrue(false); } }
		 */
		LOGGER.debug("Step 3: Create the entry with the uniqueId tag");
		// Create the blog entry with the tag to search for
		Blog regBlog = new Blog("The Beatles", "Songs",
				"Information about some of their songs", secondUniqueTag,
				false, false, null, null, TimeZone.getDefault(), true, 10,
				true, true, true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		BlogPost entryInTheBlog = new BlogPost(
				"Taxman",
				"The Beatles not-so-happy response to taxes people have to pay",
				uniqueTag, true, 100);
		service.createPost(regBlog, entryInTheBlog);

		/**
		 * Test continuing from SearchBlogsSetup.java class Step 4.1: Get the
		 * document containing tags Step 4.2: Verify the tag appears in the
		 * document that contains tags
		 * 
		 */
		SearchAdminService searchAdminService = new SearchAdminService();
		searchAdminService.indexNow("blogs");
		searchAdminService.indexNow();

		LOGGER.debug("Step 4.1: Get the document containing the list of tags");
		String documentToGetEntryTagsFrom = service.getURLString() + "/"
				+ service.getBlogsHomepageHandle() + "/feed/tags/atom";
		ExtensibleElement entriesTagDoc = service
				.getBlogFeed(documentToGetEntryTagsFrom);

		String documentToGetBlogTagsFrom = service.getURLString() + "/"
				+ service.getBlogsHomepageHandle() + "/feed/blogtags/atom";
		ExtensibleElement blogsTagDoc = service
				.getBlogFeed(documentToGetBlogTagsFrom);

		LOGGER.debug("Step 4.2: Verify the tag appears in the document that contains tags");
		List entryTags = entriesTagDoc.getElements();
		List blogTags = blogsTagDoc.getElements();

		boolean blogsTagNotFound = true;
		for (int i = 0; i < blogTags.size(); i++) { // i=1 to skip the
													// atom:generator element(as
													// opposed to the category
													// element)
			String toRead = blogTags.get(i).toString();
			boolean tagFoundThisIteration;

			// Get the actual tag itself
			int beginIndexTag = toRead.indexOf("term=\"") + 6; // size=6
			int endIndexTag = toRead.indexOf("\"", beginIndexTag);
			String tag = toRead.substring(beginIndexTag, endIndexTag);
			System.out.println(tag);
			// tagFoundThisIteration is set.
			if (tag.equalsIgnoreCase(secondUniqueTag)) {
				if (blogsTagNotFound) { // if tag hasn't been found already, set
										// that the tag has been found and that
										// a tag was found this iteration
					blogsTagNotFound = false;
					tagFoundThisIteration = true;
				} else {
					assertTrue(false); // if tag was already found AND it
					// matches again, fail the test
					tagFoundThisIteration = true; // required so you don't have
					// to initialize variable
					// when it's declared
				}
				// Check that there is only one occurrence of the uniqueId.
				int beginIndexFrequency = toRead.indexOf("frequency=\"") + 11;
				int endIndexFrequency = toRead.indexOf("\"",
						beginIndexFrequency);
				int numbOccurrences = Integer.parseInt(toRead.substring(
						beginIndexFrequency, endIndexFrequency));

				// If the tag appears more than once, the test is invalidated
				// since there may only be one occurrence of the tag
				// delete and add again, could make the snx:frequency=2
				if (tagFoundThisIteration && numbOccurrences > 1) {
					assertTrue(false);
				}
				break;
			}

		}

		boolean entriesTagNotFound = true;
		// The tags variable contains more than just the tag string, so we need
		// to do string operations
		for (int i = 1; i < entryTags.size(); i++) { // i=1 to skip the
														// atom:generator
														// element(as opposed to
														// the category element)
			String toRead = entryTags.get(i).toString();
			boolean tagFoundThisIteration;

			// Get the actual tag itself
			int beginIndexTag = toRead.indexOf("term=\"") + 6; // size=6
			int endIndexTag = toRead.indexOf("\"", beginIndexTag);
			String tag = toRead.substring(beginIndexTag, endIndexTag);
			System.out.println("---" + tag);
			// tagFoundThisIteration is set.
			if (tag.equalsIgnoreCase(uniqueTag)) {
				if (entriesTagNotFound) { // if tag hasn't been found already,
											// set that the tag has been found
											// and that a tag was found this
											// iteration
					entriesTagNotFound = false;
					tagFoundThisIteration = true;
				} else {
					assertTrue(false); // if tag was already found AND it
					// matches again, fail the test
					tagFoundThisIteration = true; // required so you don't have
					// to initialize variable
					// when it's declared
				}
				// Check that there is only one occurrence of the uniqueId.
				int beginIndexFrequency = toRead.indexOf("frequency=\"") + 11;
				int endIndexFrequency = toRead.indexOf("\"",
						beginIndexFrequency);
				int numbOccurrences = Integer.parseInt(toRead.substring(
						beginIndexFrequency, endIndexFrequency));

				// If the tag appears more than once, the test is invalidated
				// since there may only be one occurrence of the tag
				if (tagFoundThisIteration && numbOccurrences > 1) {
					assertTrue(false);
				}
				break;
			} else {
				tagFoundThisIteration = false; // if the tag isn't equal to the
				// one we're looking for, it
				// wasn't found this loop
			}
		}

		assertTrue("entriesTagNotFound", entriesTagNotFound == false);
		assertTrue("blogsTagNotFound", blogsTagNotFound == false);

	}

	@Test
	public void getEntryRecommendations() {
		/*
		 * tests endpoint blogs/<identifier>/feed/entryrecommendations/<id>/atom
		 * S1. create test blog + blog entry S2. get enpoint
		 */
		LOGGER.debug("Starting S1....create test blog + blog entry");
		Blog regBlog = new Blog("NHL Stanley Cup", "the_Handle",
				"Because its the cup", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);
		;

		BlogPost newPost = new BlogPost("PS4 Games", "Games to look out for",
				"Gaming" + Utils.logDateFormatter.format(new Date()), true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, newPost);
		assertTrue(postResult.getId() != null);// check
		LOGGER.debug("created entry");

		LOGGER.debug("Starting S2....get endpoint");
		Feed myFeed = (Feed) service.getBlogFeed(URLConstants.SERVER_URL
				+ "/blogs/roller-ui/rendering/feed/"
				+ regBlog.getHandleElement().getText() + "/entries/atom");
		// gets the recommenders id number from myFeed
		IRI iri = myFeed.getLinkResolvedHref("edit");
		int ndx = iri.toString().lastIndexOf('/');
		String number = iri.toString().substring(ndx + 1,
				iri.toString().indexOf('?'));

		// gets the necessary endpoint
		Feed theFeed = (Feed) service.getBlogFeed(URLConstants.SERVER_URL
				+ "/blogs/" + regBlog.getHandleElement().getText() + "/feed/"
				+ "entryrecommendations/" + number + "/atom");
		LOGGER.debug("got feed url");
		assertEquals(true, theFeed.toString().contains("Entry Recommended By"));

	}

	@Test
	public void getCommentRecommendations() {
		/*
		 * tests endpoint
		 * blogs/<identifier>/feed/commentrecommendations/<id>/atom S1. create
		 * test blog + blog entry S2. get enpoint
		 */
		LOGGER.debug("Starting S1....create test blog + blog entry");
		Blog regBlog = new Blog("NHL Stanley Cup", "the_Handle",
				"Because its the cup", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		BlogPost newPost = new BlogPost("PS4 Games", "Games to look out for",
				"Gaming" + Utils.logDateFormatter.format(new Date()), true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, newPost);
		assertTrue(postResult.getId() != null);// check
		LOGGER.debug("created entry");

		LOGGER.debug("Starting S2....get endpoint");
		Feed myFeed = (Feed) service.getBlogFeed(URLConstants.SERVER_URL
				+ "/blogs/roller-ui/rendering/feed/"
				+ regBlog.getHandleElement().getText() + "/entries/atom");
		// gets the recommenders id number from myFeed
		IRI iri = myFeed.getLinkResolvedHref("edit");
		int ndx = iri.toString().lastIndexOf('/');
		String number = iri.toString().substring(ndx + 1,
				iri.toString().indexOf('?'));

		// gets the necessary endpoint
		Feed theFeed = (Feed) service
				.getBlogFeed(URLConstants.SERVER_URL + "/blogs/"
						+ regBlog.getHandleElement().getText()
						+ URLConstants.BLOGS_COMMENT_RECOMMENDATIONS + number
						+ "/atom");
		LOGGER.debug("got feed url");
		assertEquals(true, theFeed.toString()
				.contains("Comment Recommended By"));

	}

	@Test
	public void getRecommendAPI() {
		/*
		 * tests endpoint blogs/<identifier/api/recommend/entries S1. create
		 * test blog + blog entry S2. get enpoint
		 */
		LOGGER.debug("Starting S1....create test blog + blog entry");
		Blog regBlog = new Blog("NHL Stanley Cup", "the_Handle",
				"Because its the cup", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		BlogPost newPost = new BlogPost("PS4 Games", "Games to look out for",
				"Gaming" + Utils.logDateFormatter.format(new Date()), true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, newPost);
		assertTrue(postResult.getId() != null);// check
		LOGGER.debug("created entry");

		LOGGER.debug("Starting S2....get endpoint");
		String entryId = postResult.getId().toString()
				.substring(postResult.getId().toString().indexOf('-') + 1);
		String handle = regBlog.getHandleElement().getText();
		Feed myFeed = (Feed) service
				.getBlogFeed(URLConstants.SERVER_URL
						+ "/blogs/roller-ui/rendering/feed/" + handle
						+ "/entries/atom");
		// gets the recommenders id number from myFeed
		// IRI iri = myFeed.getLinkResolvedHref("edit");
		// int ndx = iri.toString().lastIndexOf('/');
		// String number = iri.toString().substring(ndx + 1,
		// iri.toString().indexOf('?'));

		LOGGER.debug("Starting S2....getting the endpoint");
		ExtensibleElement theTarget = service
				.getBlogFeed(URLConstants.SERVER_URL + "/blogs/" + handle
						+ "/api/recommend/entries/" + entryId);
		assertEquals(true, theTarget.toString()
				.contains("Entry Recommended By"));
	}

	@Test
	public void commentReply() {
		/*
		 * Test blogs feature of creating reply to comments. step 1. create blog
		 * step 2. create entry in blog step 3. create comment in entry step 4.
		 * create a reply to the comment step 5. create a reply to the reply
		 * step 6. create a another reply. There are 3 nested replies in all
		 * step 7. update the text of the first reply (HTTP PUT) step 8.
		 * validate feed containing all entries.
		 */
		LOGGER.debug("Beggining test for RTC 93707 Blogs Comment Replies");
		LOGGER.debug("Step 1...create blog.");

		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String blogName = "Test_Comment_replies";

		// create a blog
		Blog regBlog = new Blog("RTC 93707 Blogs Comment Replies"
				+ uniqueNameAddition, blogName, "RTC 93707", null, false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		LOGGER.debug("step 2...create entry in blog");

		// create entry
		BlogPost post = new BlogPost("Entry for RTC 93707", "Test Test Test",
				"Entry", true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, post);
		assertTrue(postResult.getId() != null);

		LOGGER.debug("step 3...create comment in entry");
		// create a comment
		String commentTitle = "Comment for the Entry";
		BlogComment comment = new BlogComment(commentTitle, postResult);
		Entry theComment = (Entry) service.createComment(regBlog, comment);
		assertTrue(theComment.getId() != null);

		// Build the entry for the commment reply.
		Factory factory = abdera.getFactory();
		Entry entry = factory.newEntry();
		entry.setContent("test reply");
		Element elmt = entry.addSimpleExtension(
				"http://purl.org/syndication/thread/1.0", "in-reply-to", "thr",
				"");
		elmt.setAttributeValue("ref", theComment.getId().toString());

		// This is a sample of the URL and the entry to be executed.
		// POST
		// http://bbcdev.cn.ibm.com:9081/blogs/second/api/entrycomments/58db8c7e-20f1-45c7-b90b-61539be252b1
		// <?xml version="1.0"?>
		// <entry xmlns="http://www.w3.org/2005/Atom"
		// xmlns:thr="http://purl.org/syndication/thread/1.0" >
		// <content>test reply</content>
		// <thr:in-reply-to
		// ref="urn:lsid:ibm.com:blogs:comment-acd257e0-939b-460b-bac5-f3baa71e9dfc"></thr:in-reply-to>
		// </entry>

		Link commentLink = theComment.getEditLink();
		String commentEditUrl = commentLink.getHref().toString();

		int ndx = commentEditUrl.indexOf("/api/");
		String entryUrl = commentEditUrl.substring(0, ndx);
		entryUrl += "/api/entries";
		LOGGER.debug("locate post url from feed generated by " + entryUrl);
		Feed fd = (Feed) service.getBlogFeed(entryUrl);
		String postUrl = "";
		for (Entry ntry : fd.getEntries()) {
			for (Element el : ntry.getElements()) {
				if (el.toString().startsWith("<app:collection")) {
					for (QName atrb : el.getAttributes()) {
						if (atrb.toString().equals("href")
								&& el.getAttributeValue("href").contains(
										"entrycomments")) {
							postUrl = el.getAttributeValue("href");
						}
					}
				}
			}
		}
		// Post the reply to the comment
		LOGGER.debug("step 4...create comment reply");
		Entry replyEntry = (Entry) service.postBlogsFeed(postUrl, entry);

		// Build and entry for the next reply. Get the reply id from the first
		// reply and use it for the nested reply.

		String replyId = replyEntry.getId().toString();
		Entry entry2 = factory.newEntry();
		entry2.setContent("test reply 2");
		Element elmt2 = entry2.addSimpleExtension(
				"http://purl.org/syndication/thread/1.0", "in-reply-to", "thr",
				"");
		elmt2.setAttributeValue("ref", replyId);

		// Post the reply to the reply
		LOGGER.debug("step 5...create reply to reply");
		Entry replyEe2 = (Entry) service.postBlogsFeed(postUrl, entry2);

		// Create and post another reply - the 3rd.
		String replyId2 = replyEe2.getId().toString();
		Entry entry3 = factory.newEntry();
		entry3.setContent("test reply 3. Created with API");
		Element elmt3 = entry3.addSimpleExtension(
				"http://purl.org/syndication/thread/1.0", "in-reply-to", "thr",
				"");
		elmt3.setAttributeValue("ref", replyId2);

		LOGGER.debug("step 6...create reply to reply to reply");
		Entry replyEe3 = (Entry) service.postBlogsFeed(postUrl, entry3);
		assertTrue(replyEe3 != null);

		// Edit the text content of the first reply

		String reply1Id = replyEntry.getId().toString();
		String justTheId = reply1Id.substring(reply1Id.indexOf('-') + 1);
		// Create a new entry for the update
		Entry updateEntry = factory.newEntry();
		updateEntry.setContent("This is a test of updating a reply via API");

		String putUrl = postUrl.replace("entrycomments", "comments");
		String updatedUrl = putUrl.substring(0, putUrl.lastIndexOf('/'));
		updatedUrl += "/" + justTheId;
		LOGGER.debug("step 7...update first reply content");
		service.putEntry(updatedUrl, updateEntry);

		/*
		 * Validate. The feed should contain the following in <content> elements
		 * 
		 * 1.Comment for the Entry 2.test reply 2 3.test reply 3. Created with
		 * API 4.This is a test of updating a reply via API
		 */

		Feed entryFeed = (Feed) service.getBlogFeed(postUrl);
		int count = 0;
		for (Entry ntry : entryFeed.getEntries()) {
			for (Element el : ntry.getElements()) {
				if (el.toString().startsWith("<content")) {
					if (el.toString().contains("Comment for the Entry")
							|| el.toString().contains("test reply 2")
							|| el.toString().contains(
									"test reply 3. Created with API")
							|| el.toString()
									.contains(
											"This is a test of updating a reply via API")) {
						count += 1;
					}
				}
			}
		}
		LOGGER.debug("step 8...Validate feed has correct content.");
		assertEquals(4, count);
		LOGGER.debug("Ending test RTC 93707 Blogs Comment Replies");
	}

	@Test
	public void deleteRecommendedAPI() {
		/*
		 * tests endpoint blogs/<identifier/api/recommend/entries S1. create
		 * test blog + blog entry S2. get enpoint
		 */

		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);

		LOGGER.debug("Starting S1....create test blog + blog entry");
		Blog regBlog = new Blog("BlogNameT" + uniqueNameAddition,
				"BlogHandleT", "Blog_Name_DescriptionT", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog"+service.getDetail(), 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		BlogPost newPost = new BlogPost("Blog_EntryT", "Entry Entry EntryT",
				"Gaming" + Utils.logDateFormatter.format(new Date()), true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, newPost);
		assertEquals("Create blogPost"+service.getDetail(), 201, service.getRespStatus());
		assertTrue(postResult.getId() != null);// check
		LOGGER.debug("created entry");

		String recommendUrl = "";
		for (Element el : postResult.getElements()) {
			if (el.toString().startsWith("<app:collection")) {
				for (QName atrb : el.getAttributes()) {
					if (atrb.toString().equals("href")
							&& el.getAttributeValue("href").contains(
									"api/recommend/entries")) {
						recommendUrl = el.getAttributeValue("href");
					}
				}
			}
		}

		// Post recommendation
		service.postRecommendation(recommendUrl);
		assertEquals("post blog recommendation"+service.getDetail(), 200, service.getRespStatus());
		String entryId = postResult.getId().toString()
				.substring(postResult.getId().toString().indexOf('-') + 1);
		String handle = regBlog.getHandleElement().getText();
		ExtensibleElement theTarget = service
				.getBlogFeed(URLConstants.SERVER_URL + "/blogs/" + handle
						+ "/api/recommend/entries/" + entryId);
		assertEquals(true, theTarget.toString().contains("1</os:totalResults>"));

		// Remove recommendation
		service.deletePost(recommendUrl);
		ExtensibleElement theDelete = service
				.getBlogFeed(URLConstants.SERVER_URL + "/blogs/" + handle
						+ "/api/recommend/entries/" + entryId);
		assertEquals(true, theDelete.toString().contains("0</os:totalResults>"));

	}

	@Test
	public void blogsSortOrder() {

		/*
		 * Process: Endpoint tested {homepage}/feed/blogs/atom?sortBy=title&ps=5
		 * 1. Create 5 blogs in non-sorted order 
		 * 2. Generate feed without sorting and print entries in sequential order using /feed. 
		 * 3. Generate feed with sorting, print entries and validate correct order
		 * using /feed.
		 */
		LOGGER.debug("BEGINNING TEST: RTC 94397 Sort options defect test.");

		String A = "00 A RTC 94397 Sort";
		String B = "00 B RTC 94397 Sort";
		String C = "00 C RTC 94397 Sort";
		String D = "00 D RTC 94397 Sort";
		String E = "00 E RTC 94397 Sort";
		String description = "This blog is for testing sorting";

		// Create sorted list of titles. We'll need this later for validation
		String[] sortedList = { A, B, C, D, E };

		// Delete the blogs if they exist, otherwise the test is not re-runable.
		ArrayList<Blog> myBlogs = impersonateService.getMyBlogs(null, null, 0, 1000, null,
				null, SortBy.TITLE, null, BlogsType.BLOG, null, null);
		for (Blog blog : myBlogs) {
			if (blog.getTitle().equals(A) || blog.getTitle().equals(B)
					|| blog.getTitle().equals(C) || blog.getTitle().equals(D)
					|| blog.getTitle().equals(E)) {
				impersonateService.deleteBlog(blog.getEditHref());
				LOGGER.debug("Deleted blog " + blog.getTitle());
				LOGGER.debug("Deleted blog HANDLE = " + blog.getHandleElement().getText());
			}
		}

		LOGGER.debug("Starting Test S1....create 5 blogs without entries");
		// Create blogs in non-sorted order
		Blog blogE = new Blog(E, "RTC94397_E", description, "", false, false,
				null, null, TimeZone.getDefault(), true, 13, true, true, true,
				0, -1, null, null, null, 0);
		Entry blogEntryE = (Entry) service.createBlog(blogE);
		assertEquals("Create blog", 201, service.getRespStatus());

		Blog resultE = new Blog(blogEntryE);
		assertTrue(resultE != null);

		Blog blogC = new Blog(C, "RTC94397_C", description, "", false, false,
				null, null, TimeZone.getDefault(), true, 13, true, true, true,
				0, -1, null, null, null, 0);
		Entry blogEntryC = (Entry) service.createBlog(blogC);
		Blog resultC = new Blog(blogEntryC);
		assertTrue(resultC != null);

		Blog blogA = new Blog(A, "RTC94397_A", description, "", false, false,
				null, null, TimeZone.getDefault(), true, 13, true, true, true,
				0, -1, null, null, null, 0);
		Entry blogEntryA = (Entry) service.createBlog(blogA);
		Blog resultA = new Blog(blogEntryA);
		assertTrue(resultA != null);

		Blog blogD = new Blog(D, "RTC94397_D", description, "", false, false,
				null, null, TimeZone.getDefault(), true, 13, true, true, true,
				0, -1, null, null, null, 0);
		Entry blogEntryD = (Entry) service.createBlog(blogD);
		Blog resultD = new Blog(blogEntryD);
		assertTrue(resultD != null);

		Blog blogB = new Blog(B, "RTC94397_B", description, "", false, false,
				null, null, TimeZone.getDefault(), true, 13, true, true, true,
				0, -1, null, null, null, 0);
		Entry blogEntryB = (Entry) service.createBlog(blogB);
		Blog resultB = new Blog(blogEntryB);
		assertTrue(resultB != null);

		String[] unsortedList = { B, D, A, C, E };

		LOGGER.debug("Step 2....generate feed of unsorted blogs using /feed.");
		String nonSortedUrl = URLConstants.SERVER_URL + "/blogs/"
				+ service.getBlogsHomepageHandle() + "/feed/blogs/atom?ps=10";
		Feed beforeNtry = (Feed) service.getBlogsFeed(nonSortedUrl);
		LOGGER.debug("Entry title contents before using sort parameter:");
		int ndx = 0;
		for (Entry ntry : beforeNtry.getEntries()) {
			LOGGER.debug("     " + ntry.getTitle());
			if (unsortedList[ndx].equalsIgnoreCase(ntry.getTitle()))
				ndx++;
			if (ndx > 4)
				break;
		}
		assertEquals("Verify random match number ", 5, ndx);

		LOGGER.debug("Step 3....generate feed with sorting params and validate order of blog entries using /feed.");
		// Validate. Use the sort parameter for title and ensure entries are
		// sorted correctly.
		// Need to validate both publishing and feed access. The only difference
		// is request endpoint. One is ".../feed/...", the other is
		// ".../api/..."
		String sortedUrl = URLConstants.SERVER_URL + "/blogs/"
				+ service.getBlogsHomepageHandle()
				+ "/feed/blogs/atom?ps=5&sortBy=title";
		Feed afterNtry = (Feed) service.getBlogsFeed(sortedUrl);
		LOGGER.debug("Entry title contents after using sort parameter:");
		ndx = 0;
		for (Entry ntry : afterNtry.getEntries()) {
			LOGGER.debug("     " + ntry.getTitle());
			assertEquals("Verify sorted result ",sortedList[ndx], ntry.getTitle());
			ndx++;
		}
		
		//Clean up.  Delete all the blogs created in this test.
		ArrayList<Blog> myBlogs2 = service.getMyBlogs(null, null, 0, 100, null,
				null, null, null, BlogsType.BLOG, null, null);
		for (Blog blog : myBlogs2) {
			if (blog.getTitle().equals(A) || blog.getTitle().equals(B)
					|| blog.getTitle().equals(C) || blog.getTitle().equals(D)
					|| blog.getTitle().equals(E)) {
				service.deleteBlog(blog.getEditHref());
				LOGGER.debug("Deleted blog " + blog.getTitle());
				LOGGER.debug("Deleted blog HANDLE = " + blog.getHandleElement().getText());
			}
		}
		

		LOGGER.debug("ENDING TEST: RTC 94397 Sort options defect test.");
	}

	@Test
	public void blogsEntryModeration() throws FileNotFoundException,
			IOException {
		/*
		 * Tests the functionality of blogs moderation 
		 * Step 0(Initialize): Delete blogs, create instances of other users, 
		 * get needed links from the blogs moderation service document 
		 * Step 1: Create a blog with second member 
		 * Step 2: Create an entry in the blog as the owner 
		 * Step 3: Flag entry as the author 
		 * Step 4: Create two blog entries as the author 
		 * Step 5: As moderator, get feed of entry approvals, verify it contains the two entries created 
		 * Step 6: As moderator, get feed of rejected approvals, verify it doesn't contain the entries created
		 * Step 7: As moderator, approve testPost1 and reject testPost2 
		 * Step 8: As moderator, get feed of entry reviews, verify it contains the flagged entry 
		 * Step 9: As moderator, quarantine the flagged entry 
		 * Step 10: As moderator, get feed of quarantined entries, verify it contains the quarantined entry 
		 * Step 11: As moderator, restore the flagged entry 
		 * Step 12: As moderator, get feed of quarantined entries, verify it doesn't contain the restored entry 
		 * Step 13: Update testPost1 entry as author 
		 * Step 14: Flag the restored entry again as author 
		 * Step 15: As moderator, get feed of entry reviews, verify it once again contains the flagged entry 
		 * Step 16: As moderator, get entry flaghistory feeds of the flagged entry, verify they're correct
		 */
		if (StringConstants.MODERATION_ENABLED) {

			LOGGER.debug("BEGINNING TEST: Blogs moderation");

			LOGGER.debug("Step 0 (Initialize)... Delete blogs, create instances of other users, get needed links from the blogs moderation service document");
			String randString = RandomStringUtils.randomAlphanumeric(15);
			service.deleteAllBlogs(); // Clear blogs
			assertEquals("Method : blogsEntryModeration", true, service.getBlogsHomepageHandle().equalsIgnoreCase("homepage"));
			LOGGER.debug("Check if homepage handle is still existent = " + service.getBlogsHomepageHandle());

			UserPerspective user5=null, moderator=null;
			try {
				user5 = new UserPerspective(5,
						Component.BLOGS.toString(), useSSL);
				moderator = new UserPerspective(3,
						Component.BLOGS.toString(), useSSL); // Blogs moderator

			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // Second blog member that will have the role of author


			// Get needed links from blogs moderation service doc
			String entryApprovalsLink = null, approvalActionLink = null, entryReviewsLink = null, reviewsActionLink = null;
			Service moderationServiceDoc = (Service) moderator
					.getBlogsService().getModerationServiceDoc();
			for (Workspace workspace : moderationServiceDoc.getWorkspaces()) {
				if (workspace.toString().contains("entries-moderation")) {
					for (Collection collection : workspace.getCollections()) {
						if (collection.toString().contains("approval-content"))
							entryApprovalsLink = collection.getHref()
									.toString(); // Get the approvals link
						else if (collection.toString().contains(
								"approval-action"))
							approvalActionLink = collection.getHref()
									.toString(); // Get the approval action link
						else if (collection.toString().contains(
								"review-content"))
							entryReviewsLink = collection.getHref().toString(); 
							// Get the entry reviews link
						else if (collection.toString()
								.contains("review-action"))
							reviewsActionLink = collection.getHref().toString(); 
							// Get the reviews action link
					}
					break;
				}
			}

			LOGGER.debug("Step 1... Create a blog with second member");
			Blog testBlog = new Blog("API Test Moderation Blog " + randString,
					"BlogHandle" + randString, "Summary", "", false, false,
					null, null, TimeZone.getDefault(), true, 50, false, true,
					true, 0, -1, null, null, null, 0);
			LOGGER.debug("");
			String[] authorEmails = { user5.getEmail() };
			ExtensibleElement eblog = service.createBlogWithMembers(testBlog,
					null, authorEmails);
			testBlog = new Blog((Entry) eblog);

			LOGGER.debug("Step 2... Create an entry in the blog as the owner");
			BlogPost testPostFlag = new BlogPost("Test Entry Flag "
					+ randString, "I'm about to be modded", "", true, 50);
			service.createPost(testBlog, testPostFlag);

			LOGGER.debug("Step 3... Flag entry as the author");
			// Get the blog post self link
			Feed entriesFeed = (Feed) service.getRecentPostsBlogsFeed(testBlog
					.getHandleElement().getText(), 0, 0, null, null, null,
					null, null);
			String blogPostSelfLink = entriesFeed.getEntries().get(0)
					.getSelfLinkResolvedHref().toString();

			// Generate atom entry which represents the report
			Factory factory = abdera.getFactory();
			Entry reportEntry = factory.newEntry();
			reportEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/issue",
					"001", "Legal issue");
			reportEntry.addLink(blogPostSelfLink, "related");
			reportEntry.setContent("Violates company policy");

			user5.getBlogsService().flagBlogPost(reportEntry);

			LOGGER.debug("Step 4... Create two blog entries as the author");
			BlogPost testPost1 = new BlogPost("Test Entry Approve "
					+ randString,
					"So here's the idea.\n Make it easier for the customer.",
					"test", true, 50);
			Entry testPost1Response = (Entry) user5.getBlogsService()
					.createPostManually(testBlog.getHandleElement().getText(),
							testPost1);
			BlogPost testPost2 = new BlogPost(
					"Test Entry Reject " + randString,
					"Everyone should join my cult take over the world and enslave the human race",
					"", true, 50);
			user5.getBlogsService().createPostManually(
					testBlog.getHandleElement().getText(), testPost2);

			LOGGER.debug("Step 5... As moderator, get feed of entry approvals, verify it contains the two entries created ");
			Feed entryApprovals = (Feed) moderator.getBlogsService()
					.getBlogsFeed(entryApprovalsLink + "?sortOrder=desc");
			boolean foundEntry1 = false, foundEntry2 = false;
			int size = entryApprovals.getEntries().size();
			for (Entry e : entryApprovals.getEntries()) {
				if (e.getTitle().equals(testPost1.getTitle()))
					foundEntry1 = true;
				else if (e.getTitle().equals(testPost2.getTitle()))
					foundEntry2 = true;
			}
			assertEquals(true, foundEntry1 && foundEntry2);

			LOGGER.debug("Step 6... As moderator, get feed of rejected approvals, verify it doesn't contain the entries created");
			Feed rejectedEntryApprovals = (Feed) moderator.getBlogsService()
					.getBlogsFeed(entryApprovalsLink + "?status=rejected");
			for (Entry e : rejectedEntryApprovals.getEntries()) {
				if (e.getTitle().equals(testPost1.getTitle())
						|| e.getTitle().equals(testPost2.getTitle()))
					assertTrue(false);
			}

			LOGGER.debug("Step 7... As moderator, approve testPost1 and reject testPost2");
			// Get the individual entries' approval history link
			String approvalHistoryLink1 = null;
			String approvalHistoryLink2 = null;
			for (Entry e : entryApprovals.getEntries()) {
				if (e.getTitle().equals(testPost1.getTitle()))
					approvalHistoryLink1 = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
				else
					approvalHistoryLink2 = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
			}

			// Generate atom entry which represents the approval for testPost1
			Entry approvalEntry = factory.newEntry();
			approvalEntry.addLink(approvalHistoryLink1, "related");
			approvalEntry.addSimpleExtension(StringConstants.SNX_MODERATION,
					null).setAttributeValue("action", "approve");
			approvalEntry.setContent("Entry is acceptable");

			// Generate atom entry which represents the rejection for testPost2
			Entry rejectionEntry = factory.newEntry();
			rejectionEntry.addLink(approvalHistoryLink2, "related");
			rejectionEntry.addSimpleExtension(StringConstants.SNX_MODERATION,
					null).setAttributeValue("action", "reject");
			rejectionEntry.setContent("Unacceptable content.");

			// Send the approvals/rejections
			moderator.getBlogsService().postBlogsFeed(approvalActionLink,
					approvalEntry);
			moderator.getBlogsService().postBlogsFeed(approvalActionLink,
					rejectionEntry);

			LOGGER.debug("Step 8... As moderator, get feed of entry reviews, verify it contains the flagged entry");
			Feed entryReviews = (Feed) moderator.getBlogsService()
					.getBlogsFeed(entryReviewsLink);
			boolean foundEntry = false;
			String reviewHistoryLink = null; // Use this step to also get the
			// history link for step 9
			for (Entry e : entryReviews.getEntries())
				if (e.getTitle().equals(testPostFlag.getTitle())) {
					foundEntry = true;
					reviewHistoryLink = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
				}
			assertEquals(true, foundEntry);

			LOGGER.debug("Step 9... As moderator, quarantine the flagged entry");
			// Generate atom entry which represents the review/quarantine
			Entry quarantineEntry = factory.newEntry();
			quarantineEntry.addLink(reviewHistoryLink, "related");
			quarantineEntry.addSimpleExtension(StringConstants.SNX_MODERATION,
					null).setAttributeValue("action", "quarantine");
			quarantineEntry
					.setContent("Blog entry is unacceptable according to company guidelines.");

			// Send the review
			moderator.getBlogsService().postBlogsFeed(reviewsActionLink,
					quarantineEntry);

			LOGGER.debug("Step 10... As moderator, get feed of quarantined entries, verify it contains the quarantined entry");
			Feed quarantinedEntries = (Feed) moderator.getBlogsService()
					.getBlogsFeed(entryReviewsLink + "?status=quarantined");
			foundEntry = false;
			for (Entry e : quarantinedEntries.getEntries())
				if (e.getTitle().equals(testPostFlag.getTitle()))
					foundEntry = true;
			assertEquals(true, foundEntry);

			LOGGER.debug("Step 11... As moderator, restore the flagged entry");
			// Generate atom entry which represents the restore
			Entry restoreEntry = factory.newEntry();
			restoreEntry.addLink(reviewHistoryLink, "related");
			restoreEntry.addSimpleExtension(StringConstants.SNX_MODERATION,
					null).setAttributeValue("action", "restore");
			restoreEntry.setContent("Blog post is now acceptable");

			// Send the restore
			moderator.getBlogsService().postBlogsFeed(reviewsActionLink,
					restoreEntry);

			LOGGER.debug("Step 12... As moderator, get feed of quarantined entries, verify it doesn't contain the restored entry");
			quarantinedEntries = (Feed) moderator.getBlogsService()
					.getBlogsFeed(entryReviewsLink + "?status=quarantined");
			for (Entry e : quarantinedEntries.getEntries())
				if (e.getTitle().equals(testPostFlag.getTitle()))
					assertTrue("restored entry should not be found", false);

			LOGGER.debug("Step 13... Update testPost1 entry as author");
			testPost1
					.setContent("Update: What we really should be doing is thinking about the big picture");
			user5.getBlogsService().editPost(
					testPost1Response.getEditLinkResolvedHref().toString(),
					testPost1);

			LOGGER.debug("Step 14... Flag the restored entry again as author");
			Entry newReportEntry = (Entry) reportEntry.clone();
			newReportEntry.setContent("Still a dangerous post");
			user5.getBlogsService().flagBlogPost(newReportEntry);

			LOGGER.debug("Step 15... As moderator, get feed of entry reviews, verify it once again contains the flagged entry");
			entryReviews = (Feed) moderator.getBlogsService().getBlogsFeed(
					entryReviewsLink);
			foundEntry = false;
			for (Entry e : entryReviews.getEntries())
				if (e.getTitle().equals(testPostFlag.getTitle()))
					foundEntry = true;
			assertEquals(true, foundEntry);

			LOGGER.debug("Step 16... As moderator, get entry flaghistory feeds of the flagged entry, verify they're correct");
			// Feed of raised flags - verify contains the second flag
			Feed reviewHistoryFeed = (Feed) moderator.getBlogsService()
					.getBlogsFeed(reviewHistoryLink + "?filter=flagIsRaised");
			assertEquals(1, reviewHistoryFeed.getEntries().size());
			assertEquals(newReportEntry.getContent(), reviewHistoryFeed
					.getEntries().get(0).getContent());

			// Feed of dismissed flags - verify contains the first flag
			reviewHistoryFeed = (Feed) moderator
					.getBlogsService()
					.getBlogsFeed(reviewHistoryLink + "?filter=flagIsDismissed");
			assertEquals(1, reviewHistoryFeed.getEntries().size());
			assertEquals(reportEntry.getContent(), reviewHistoryFeed
					.getEntries().get(0).getContent());

			// Feed of all flags - verify contains both flags
			reviewHistoryFeed = (Feed) moderator.getBlogsService()
					.getBlogsFeed(
							reviewHistoryLink + "?filter=flag&sortOrder=asc");
			assertEquals(2, reviewHistoryFeed.getEntries().size());
			assertEquals(reportEntry.getContent(), reviewHistoryFeed
					.getEntries().get(0).getContent());
			assertEquals(newReportEntry.getContent(), reviewHistoryFeed
					.getEntries().get(1).getContent());

			LOGGER.debug("ENDING TEST: Blogs moderation");
		}
	}

	@Test
	public void blogsCommentModeration() throws FileNotFoundException,
			IOException {
		/*
		 * Test the functionality of blogs comment moderation Step 0
		 * (Initialize): Delete blogs, create instances of other users, get
		 * needed links from the blogs moderation service document Step 1:
		 * Create a blog with members Step 2: Create an entry in the blog Step
		 * 3: Create an entry in the blog as user1 Step 4: Create four comments
		 * on the entry from step 2 (testPost) as user1 Step 5: Flag comment1 as
		 * user5 Step 6: Create an entry in the blog as user5 Step 7: Create
		 * four comments on the entry (testPost3) as user5 Step 8: Flag
		 * comment2, comment3, and comment4 as user6 Step 9: Get list of blog
		 * entries in the test blog as user6, verify it contains the 2 entries
		 * created by owners Step 10: As moderator, approve testPost3 Step 11:
		 * As moderator, get feed of comment approvals, verify it contains the 4
		 * comments submitted by user5 Step 12: As moderator, approve comment5
		 * and reject comment6 Step 13: As moderator, get feed of comment
		 * reviews, verify it contains the 4 flagged comments Step 14: As
		 * moderator, dismiss comment1, quarantine and then restore comment2,
		 * quarantine comment4 Step 15: As moderator, get feed of quarantined
		 * comments, verify it contains comment4 Step 16: Get feed of comments
		 * in testBlog, verify it contains comment1, comment2, comment3, and
		 * comment5
		 */
		if (StringConstants.MODERATION_ENABLED) {

			LOGGER.debug("BEGINNING TEST: Blogs comment moderation");
			LOGGER.debug("Step 0 (Initialize)... Delete blogs, create instances of other users, get needed links from the blogs moderation service document");
			String randString = RandomStringUtils.randomAlphanumeric(15);
			service.deleteAllBlogs();
			assertEquals("Method : blogsCommentModeration", true, service.getBlogsHomepageHandle().equalsIgnoreCase("homepage"));
			LOGGER.debug("Check if homepage handle is still existent = " + service.getBlogsHomepageHandle());

			// TB 10/29/13 SES Ticket 273887 - Need to temporarily change user.
			// Ajones100 is broken
			// UserPerspective user1 = new UserPerspective(1,
			// component.BLOGS.toString(), useSSL);
			UserPerspective user1=null,user5=null,user6=null,moderator=null;
			try {
				user1 = new UserPerspective(7,
						Component.BLOGS.toString(), useSSL);
				user5 = new UserPerspective(5,
						Component.BLOGS.toString(), useSSL);
				user6 = new UserPerspective(6,
						Component.BLOGS.toString(), useSSL);
				moderator = new UserPerspective(3,
						Component.BLOGS.toString(), useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// Get needed links from blogs moderation service doc
			String entryApprovalsLink = null, approvalActionLink = null;
			String commentApprovalsLink = null, commentApprovalsActionLink = null, commentReviewsLink = null, commentReviewsActionLink = null;
			Service moderationServiceDoc = (Service) moderator
					.getBlogsService().getModerationServiceDoc();
			for (Workspace workspace : moderationServiceDoc.getWorkspaces()) {
				if (workspace.toString().contains("entries-moderation")) {
					for (Collection collection : workspace.getCollections()) {
						if (collection.toString().contains("approval-content"))
							entryApprovalsLink = collection.getHref()
									.toString(); // Get the approvals link
						else if (collection.toString().contains(
								"approval-action"))
							approvalActionLink = collection.getHref()
									.toString(); // Get the approval action link
					}
				} else if (workspace.toString().contains("comments-moderation")) {
					for (Collection collection : workspace.getCollections()) {
						if (collection.toString().contains("approval-content"))
							commentApprovalsLink = collection.getHref()
									.toString(); // Get the comment approvals
						// link
						else if (collection.toString().contains(
								"approval-action"))
							commentApprovalsActionLink = collection.getHref()
									.toString(); // Get the comment approvals
						// action link
						else if (collection.toString().contains(
								"review-content"))
							commentReviewsLink = collection.getHref()
									.toString(); // Get the comment reviews link
						else if (collection.toString()
								.contains("review-action"))
							commentReviewsActionLink = collection.getHref()
									.toString(); // Get the comment reviews
						// action link
					}
				}
			}

			LOGGER.debug("Step 1... Create a blog with members");
			LOGGER.debug("blogsCommentModeration ========" + "BlogHandle" + randString);
			Blog testBlog = new Blog("API Test Comment Moderation Blog "
					+ randString, "BlogHandle" + randString, "Summary", "",
					false, false, null, null, TimeZone.getDefault(), true, 50,
					false, true, true, 0, -1, null, null, null, 0);
			String[] ownerEmails = { user1.getEmail() };
			String[] authorEmails = { user5.getEmail(), user6.getEmail() };
			ExtensibleElement eblog = service.createBlogWithMembers(testBlog,
					ownerEmails, authorEmails);
			testBlog = new Blog((Entry) eblog);

			LOGGER.debug("Step 2... Create an entry in the blog");
			BlogPost testPost = new BlogPost("Test Entry with Comments "
					+ randString, "Revolutionary Idea.", null, true, 50);
			Entry result = (Entry) service.createPost(testBlog, testPost);
			BlogPost blogPostRetrieved = new BlogPost(result);

			LOGGER.debug("Step 3... Create an entry in the blog as second owner user1");
			BlogPost testPost2 = new BlogPost("Test Entry by second owner "
					+ randString, "Let's start a protest.", null, true, 50);
			user1.getBlogsService().createPostManually(
					testBlog.getHandleElement().getText(), testPost2);

			LOGGER.debug("Step 4... Create four comments on the entry from step 2 (testPost) as user1");
			BlogComment comment1 = new BlogComment("Screw you Amy, get lost",
					null);
			Entry response = (Entry) user1.getBlogsService().createComment(
					blogPostRetrieved, comment1);
			BlogComment comment2 = new BlogComment("Interesting ideas Amy.",
					null);
			user1.getBlogsService().createComment(blogPostRetrieved, comment2);
			BlogComment comment3 = new BlogComment("Nice post", null);
			user1.getBlogsService().createComment(blogPostRetrieved, comment3);
			BlogComment comment4 = new BlogComment(
					"Terrible, lame, you suck %@@#*$$#*", null);
			user1.getBlogsService().createComment(blogPostRetrieved, comment4);

			LOGGER.debug("Step 5... Flag comment1 as user5");
			// Get the comments' self links
			Feed commentsFeed = (Feed) service.getCommentsAddedBlogsFeed(
					testBlog.getHandleElement().getText(), 0, 0);
			String comment1SelfLink = commentsFeed.getEntries().get(3)
					.getSelfLinkResolvedHref().toString();

			// Generate atom entries which represent the flags
			Factory factory = abdera.getFactory();
			Entry reportEntry1 = factory.newEntry();
			reportEntry1.addCategory("http://www.ibm.com/xmlns/prod/sn/issue",
					"001", "Legal issue");
			reportEntry1.addLink(comment1SelfLink, "related");
			reportEntry1.setContent("Inappropriate language");

			user5.getBlogsService().flagBlogComment(reportEntry1);

			LOGGER.debug("Step 6... Create an entry in the blog as user5");
			BlogPost testPost3 = new BlogPost("Test Entry by author "
					+ randString, "Nuclear power plant.", null, true, 50);
			Entry result3 = (Entry) user5.getBlogsService().createPostManually(
					testBlog.getHandleElement().getText(), testPost3);

			LOGGER.debug("Step 7... Create four comments on the entry (testPost3) as user5");
			BlogComment comment5 = new BlogComment("Isn't this great?", result3);
			user5.getBlogsService().createCommentManually(
					testBlog.getHandleElement().getText(), comment5);
			BlogComment comment6 = new BlogComment(
					"It could be 10 times more efficient using customer data",
					result3);
			user5.getBlogsService().createCommentManually(
					testBlog.getHandleElement().getText(), comment6);
			BlogComment comment7 = new BlogComment("Lost is a cool show",
					result3);
			user5.getBlogsService().createCommentManually(
					testBlog.getHandleElement().getText(), comment7);
			BlogComment comment8 = new BlogComment(
					"Talk to Gordon Freeman about this. He's a scientist",
					result3);
			user5.getBlogsService().createCommentManually(
					testBlog.getHandleElement().getText(), comment8);

			LOGGER.debug("Step 8... Flag comment2, comment3, and comment4 as user6");
			// Get the comments' self links
			String comment2SelfLink = commentsFeed.getEntries().get(2)
					.getSelfLinkResolvedHref().toString();
			String comment3SelfLink = commentsFeed.getEntries().get(1)
					.getSelfLinkResolvedHref().toString();
			String comment4SelfLink = commentsFeed.getEntries().get(0)
					.getSelfLinkResolvedHref().toString();

			// Generate atom entries which represent the flags
			Entry reportEntry2 = factory.newEntry();
			reportEntry2.addCategory("http://www.ibm.com/xmlns/prod/sn/issue",
					"002", "HR problem");
			reportEntry2.addLink(comment2SelfLink, "related");
			reportEntry2.setContent("Bad personel");

			Entry reportEntry3 = factory.newEntry();
			reportEntry3.addCategory("http://www.ibm.com/xmlns/prod/sn/issue",
					"002", "HR issue");
			reportEntry3.addLink(comment3SelfLink, "related");
			reportEntry3.setContent("Serious issue here.");

			Entry reportEntry4 = factory.newEntry();
			reportEntry4.addCategory("http://www.ibm.com/xmlns/prod/sn/issue",
					"001", "Legal issue");
			reportEntry4.addLink(comment4SelfLink, "related");
			reportEntry4
					.setContent("Peter... what's happening. Yeah if you could go ahead and get those TPS reports out ASAP, that'd be great.");

			// Send the flags
			user6.getBlogsService().flagBlogComment(reportEntry3);
			user6.getBlogsService().flagBlogComment(reportEntry4);
			user6.getBlogsService().flagBlogComment(reportEntry2);

			LOGGER.debug("Step 9... Get list of blog entries in the test blog as user6, verify it contains the 2 entries created by owners");
			Feed blogEntries = (Feed) user6.getBlogsService()
					.getBlogEntriesFeed(testBlog.getHandleElement().getText());
			assertEquals(2, blogEntries.getEntries().size());

			boolean foundPost1 = false, foundPost2 = false;
			for (Entry e : blogEntries.getEntries()) {
				if (e.getTitle().equals(testPost.getTitle()))
					foundPost1 = true;
				else if (e.getTitle().equals(testPost2.getTitle()))
					foundPost2 = true;
			}
			assertEquals(true, foundPost1 && foundPost2);

			LOGGER.debug("Step 10... As moderator, approve testPost3");
			// Get the feed of entry approvals
			Feed entryApprovals = (Feed) moderator.getBlogsService()
					.getBlogsFeed(entryApprovalsLink);
			String approvalHistoryLink = null;
			for (Entry e : entryApprovals.getEntries()) {
				if (e.getTitle().equals(testPost3.getTitle()))
					approvalHistoryLink = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
			}

			// Generate atom entry which represents the approval for testPost3
			Entry approvalEntry = factory.newEntry();
			approvalEntry.addLink(approvalHistoryLink, "related");
			approvalEntry.addSimpleExtension(StringConstants.SNX_MODERATION,
					null).setAttributeValue("action", "approve");
			approvalEntry.setContent("A fine blog post");

			// Send the approval
			moderator.getBlogsService().postBlogsFeed(approvalActionLink,
					approvalEntry);

			LOGGER.debug("Step 11... As moderator, get feed of comment approvals, verify it contains the 4 comments submitted by user5");
			Feed commentApprovals = (Feed) moderator.getBlogsService()
					.getBlogsFeed(commentApprovalsLink + "?sortOrder=asc");
			boolean foundComment5 = false, foundComment6 = false, foundComment7 = false, foundComment8 = false;
			String comment5ApprovalLink = null, comment6ApprovalLink = null; // also
			// get
			// approval
			// history
			// links
			// for
			// step
			// 12
			for (Entry e : commentApprovals.getEntries()) {
				if (e.getContent().equals(comment5.getContent())) {
					foundComment5 = true;
					comment5ApprovalLink = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
				} else if (e.getContent().equals(comment6.getContent())) {
					foundComment6 = true;
					comment6ApprovalLink = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
				} else if (e.getContent().equals(comment7.getContent()))
					foundComment7 = true;
				else if (e.getContent().equals(comment8.getContent()))
					foundComment8 = true;
			}
			assertEquals(true, foundComment5 && foundComment6 && foundComment7
					&& foundComment8);

			LOGGER.debug("Step 12... As moderator, approve comment5 and reject comment6");
			// Generate atom entry which represents the approval for comment5
			Entry approvalEntry5 = factory.newEntry();
			approvalEntry5.addLink(comment5ApprovalLink, "related");
			approvalEntry5.addSimpleExtension(StringConstants.SNX_MODERATION,
					null).setAttributeValue("action", "approve");
			approvalEntry5.setContent("Acceptable comment content");
			// Generate atom entry which represents the rejection for comment6
			Entry rejectionEntry6 = factory.newEntry();
			rejectionEntry6.addLink(comment6ApprovalLink, "related");
			rejectionEntry6.addSimpleExtension(StringConstants.SNX_MODERATION,
					null).setAttributeValue("action", "reject");
			rejectionEntry6.setContent("Unacceptable content here folks");

			// Send the approval and rejection
			moderator.getBlogsService().postBlogsFeed(
					commentApprovalsActionLink, approvalEntry5);
			moderator.getBlogsService().postBlogsFeed(
					commentApprovalsActionLink, rejectionEntry6);

			LOGGER.debug("Step 13... As moderator, get feed of comment reviews, verify it contains the 4 flagged comments");
			Feed commentReviews = (Feed) moderator.getBlogsService()
					.getBlogsFeed(commentReviewsLink);
			boolean foundComment1 = false, foundComment2 = false, foundComment3 = false, foundComment4 = false;
			String comment1ReviewLink = null, comment2ReviewLink = null, comment3ReviewLink = null, comment4ReviewLink = null;
			// ^ while going through the for loop, get the comments' review
			// history links for step 14
			for (Entry e : commentReviews.getEntries()) {
				if (e.getContent().equals(comment1.getContent())) {
					foundComment1 = true;
					comment1ReviewLink = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
				} else if (e.getContent().equals(comment2.getContent())) {
					foundComment2 = true;
					comment2ReviewLink = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
				} else if (e.getContent().equals(comment3.getContent()))
					foundComment3 = true;
				else if (e.getContent().equals(comment4.getContent())) {
					foundComment4 = true;
					comment4ReviewLink = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
				}
			}
			assertEquals(true, foundComment1 && foundComment2 && foundComment3
					&& foundComment4);

			LOGGER.debug("Step 14... As moderator, dismiss comment1, quarantine and then restore comment2, quarantine comment4");
			// Generate entries representing the comment reviews
			Entry review1 = factory.newEntry();
			review1.addLink(comment1ReviewLink, "related");
			review1.addSimpleExtension(StringConstants.SNX_MODERATION, null)
					.setAttributeValue("action", "dismiss");
			review1.setContent("Comment does not need to be removed");

			Entry review2 = factory.newEntry();
			review2.addLink(comment2ReviewLink, "related");
			review2.addSimpleExtension(StringConstants.SNX_MODERATION, null)
					.setAttributeValue("action", "quarantine");
			review2.setContent("Comment violates business guidelines");

			Entry review2_restore = factory.newEntry();
			review2_restore.addLink(comment2ReviewLink, "related");
			review2_restore.addSimpleExtension(StringConstants.SNX_MODERATION,
					null).setAttributeValue("action", "restore");
			review2_restore.setContent("Comment has been fixed");

			Entry review4 = factory.newEntry();
			review4.addLink(comment4ReviewLink, "related");
			review4.addSimpleExtension(StringConstants.SNX_MODERATION, null)
					.setAttributeValue("action", "quarantine");
			review4.setContent("Comment does not need to be removed");

			// Send the reviews
			moderator.getBlogsService().postBlogsFeed(commentReviewsActionLink,
					review1);
			moderator.getBlogsService().postBlogsFeed(commentReviewsActionLink,
					review2);
			moderator.getBlogsService().postBlogsFeed(commentReviewsActionLink,
					review2_restore);
			moderator.getBlogsService().postBlogsFeed(commentReviewsActionLink,
					review4);

			LOGGER.debug("Step 15... As moderator, get feed of quarantined comments, verify it contains comment4");
			Feed quarantinedComments = (Feed) moderator.getBlogsService()
					.getBlogsFeed(commentReviewsLink + "?status=quarantined");
			foundComment4 = false;
			for (Entry e : quarantinedComments.getEntries())
				if (e.getContent().equals(comment4.getContent()))
					foundComment4 = true;
			assertEquals(true, foundComment4);

			LOGGER.debug("Step 16... Get feed of comments in testBlog, verify it contains comment1, comment2, comment3, and comment5");
			commentsFeed = (Feed) service.getCommentsAddedBlogsFeed(testBlog
					.getHandleElement().getText(), 0, 0);
			assertEquals(4, commentsFeed.getEntries().size());
			foundComment1 = false;
			foundComment2 = false;
			foundComment3 = false;
			foundComment5 = false;
			for (Entry e : commentsFeed.getEntries()) {
				if (e.getTitle().contains(testPost.getTitle())) {
					if (e.getContent().equals(comment1.getContent()))
						foundComment1 = true;
					else if (e.getContent().equals(comment2.getContent()))
						foundComment2 = true;
					else if (e.getContent().equals(comment3.getContent()))
						foundComment3 = true;
				} else if (e.getTitle().contains(testPost3.getTitle()))
					if (e.getContent().equals(comment5.getContent()))
						foundComment5 = true;
			}
			assertEquals(true, foundComment1 && foundComment2 && foundComment3
					&& foundComment5);

			LOGGER.debug("ENDING TEST: Blogs comment moderation");
		}
	}

	@Test
	public void blogsModerationDeletion() throws FileNotFoundException,
			IOException {
		/*
		 * Test the abilities of the moderator to delete entries and comments
		 * Step 0 (Initialize): Delete blogs, create instances of other users,
		 * get needed links from the blogs moderation service document Step 1:
		 * Create a blog with members Step 2: Create an entry (ownerEntry1) in
		 * the blog Step 3: Create an entry (ownerEntry2) in the blog as second
		 * owner user1 Step 4: Create a comment (ownerComment1) on ownerEntry1
		 * as second owner user1 Step 5: Create a comment (authorComment2) on
		 * ownerEntry1 as user6 Step 6: Create an entry (authorEntry3) in the
		 * blog as user7 Step 7: Flag ownerComment1 as user7 Step 8: Flag
		 * ownerEntry2 as user7 Step 9: Get feed of entries in the blog, verify
		 * it contains ownerEntry1 and ownerEntry2 Step 10: As moderator, get
		 * feed of entry approvals, very it contains authorEntry3 Step 11: As
		 * moderator, delete authorEntry3 Step 12: As moderator, get feed of
		 * entry approvals, verify it doesn't contain authorEntry3 Step 13: As
		 * moderator, quarantine and then delete ownerEntry2 Step 14: As
		 * moderator, get feed of quarantined entry reviews, verify it doesn't
		 * contain ownerEntry2 Step 15: As moderator, delete authorComment2 Step
		 * 16: As moderator, get feed of comment approvals, verify it doesn't
		 * contain authorComment2 Step 17: As moderator, quarantine and then
		 * delete ownerComment1 Step 18: As moderator, get feed of quarantined
		 * comment reviews, verify it doesn't contain ownerComment1
		 */
		if (StringConstants.MODERATION_ENABLED) {

			LOGGER.debug("BEGINNING TEST: Blogs moderation deletion");
			LOGGER.debug("Step 0 (Initialize)... Delete blogs, create instances of other users, get needed links from the blogs moderation service document");
			String randString = RandomStringUtils.randomAlphanumeric(15);
			service.deleteAllBlogs();
			assertEquals("Method : blogsModerationDeletion", true, service.getBlogsHomepageHandle().equalsIgnoreCase("homepage"));
			LOGGER.debug("Check if homepage handle is still existent = " + service.getBlogsHomepageHandle());

			// TB 10/29/13 SES Ticket 273887 - Need to temporarily change user.
			// Ajones100 is broken
			// UserPerspective user1 = new UserPerspective(1,
			// Component.BLOGS.toString(), useSSL);
			UserPerspective user1=null,user6=null,user7=null,moderator=null;
			try {
				user1 = new UserPerspective(5,
						Component.BLOGS.toString(), useSSL);
				user6 = new UserPerspective(6,
						Component.BLOGS.toString(), useSSL);
				user7 = new UserPerspective(7,
						Component.BLOGS.toString(), useSSL);
				moderator = new UserPerspective(3,
						Component.BLOGS.toString(), useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Get needed links from blogs moderation service doc
			String entryApprovalsLink = null, entryReviewsLink = null, reviewsActionLink = null;
			String commentApprovalsLink = null, commentReviewsLink = null, commentReviewsActionLink = null;
			Service moderationServiceDoc = (Service) moderator
					.getBlogsService().getModerationServiceDoc();
			for (Workspace workspace : moderationServiceDoc.getWorkspaces()) {
				if (workspace.toString().contains("entries-moderation")) {
					for (Collection collection : workspace.getCollections()) {
						if (collection.toString().contains("approval-content"))
							entryApprovalsLink = collection.getHref()
									.toString(); // Get the approvals link
						else if (collection.toString().contains(
								"review-content"))
							entryReviewsLink = collection.getHref().toString(); // Get
						// the
						// entry
						// reviews
						// link
						else if (collection.toString()
								.contains("review-action"))
							reviewsActionLink = collection.getHref().toString(); // Get
						// the
						// reviews
						// action
						// link
					}
				} else if (workspace.toString().contains("comments-moderation")) {
					for (Collection collection : workspace.getCollections()) {
						if (collection.toString().contains("approval-content"))
							commentApprovalsLink = collection.getHref()
									.toString(); // Get the comment approvals
						// link
						else if (collection.toString().contains(
								"review-content"))
							commentReviewsLink = collection.getHref()
									.toString(); // Get the comment reviews link
						else if (collection.toString()
								.contains("review-action"))
							commentReviewsActionLink = collection.getHref()
									.toString(); // Get the comment reviews
						// action link
					}
				}
			}

			LOGGER.debug("Step 1... Create a blog with members");
			Blog testBlog = new Blog("API Test Moderation Deletion Blog "
					+ randString, "BlogHandle" + randString, "Summary", "",
					false, false, null, null, TimeZone.getDefault(), true, 50,
					false, true, true, 0, -1, null, null, null, 0);
			LOGGER.debug("blogsModerationDeletion =========== BlogHandle" + randString);
			String[] ownerEmails = { user1.getEmail() };
			String[] authorEmails = { user6.getEmail(), user7.getEmail() };
			ExtensibleElement eblog = service.createBlogWithMembers(testBlog,
					ownerEmails, authorEmails);
			testBlog = new Blog((Entry) eblog);

			LOGGER.debug("Step 2... Create an entry (ownerEntry1) in the blog");
			BlogPost ownerEntry1 = new BlogPost("Owner Entry " + randString,
					"One cool trick to lose that belly fat", null, true, 50);
			Entry result = (Entry) service.createPost(testBlog, ownerEntry1);
			BlogPost postRetrieved = new BlogPost(result);

			LOGGER.debug("Step 3... Create an entry (ownerEntry2) in the blog as second owner user1");
			BlogPost ownerEntry2 = new BlogPost("Second owner entry "
					+ randString, "The key to success is staying motivated",
					null, true, 50);
			user1.getBlogsService().createPostManually(
					testBlog.getHandleElement().getText(), ownerEntry2);

			LOGGER.debug("Step 4... Create a comment (ownerComment1) on ownerEntry1 as second owner user1");
			BlogComment ownerComment1 = new BlogComment(
					"Really? Wow take my money.", null);
			user1.getBlogsService().createComment(postRetrieved, ownerComment1);

			LOGGER.debug("Step 5... Create a comment (authorComment2) on ownerEntry1 as user6");
			BlogComment authorComment2 = new BlogComment(
					"All right now, we all know this is fake. Diet and exercise.",
					null);
			user6.getBlogsService()
					.createComment(postRetrieved, authorComment2);

			LOGGER.debug("Step 6... Create an entry (authorEntry3) in the blog as user7");
			BlogPost authorEntry3 = new BlogPost("Author Entry " + randString,
					"Welcome to the Black Mesa Research Facility", null, true,
					50);
			user7.getBlogsService().createPostManually(
					testBlog.getHandleElement().getText(), authorEntry3);

			LOGGER.debug("Step 7... Flag ownerComment1 as user7");
			// Get the comment's self link
			Feed commentsFeed = (Feed) user7.getBlogsService()
					.getCommentsAddedBlogsFeed(
							testBlog.getHandleElement().getText(), 0, 0);
			String comment1SelfLink = commentsFeed.getEntries().get(0)
					.getSelfLinkResolvedHref().toString();

			// Generate atom entry which represents the flag
			Factory factory = abdera.getFactory();
			Entry comment1Flag = factory.newEntry();
			comment1Flag.addCategory("http://www.ibm.com/xmlns/prod/sn/issue",
					"002", "HR issue");
			comment1Flag.addLink(comment1SelfLink, "related");
			comment1Flag.setContent("Violates the laws of the universe.");

			// Send the flag
			user7.getBlogsService().flagBlogComment(comment1Flag);

			LOGGER.debug("Step 8... Flag ownerEntry2 as user7");
			// Get the entry's self link
			Feed entriesFeed = (Feed) user7.getBlogsService()
					.getRecentPostsBlogsFeed(
							testBlog.getHandleElement().getText(), 0, 0, null,
							null, null, null, null);
			String entry2SelfLink = null;
			for (Entry e : entriesFeed.getEntries())
				if (e.getTitle().equals(ownerEntry2.getTitle()))
					entry2SelfLink = e.getSelfLinkResolvedHref().toString();

			// Generate atom entry which represents the flag
			Entry entry2Flag = factory.newEntry();
			entry2Flag.addCategory("http://www.ibm.com/xmlns/prod/sn/issue",
					"001", "Legal issuueee");
			entry2Flag.addLink(entry2SelfLink, "related");
			entry2Flag.setContent("Crazy serious issue here.");

			// Send the flag
			user7.getBlogsService().flagBlogPost(entry2Flag);

			LOGGER.debug("Step 9... Get feed of entries in the blog as user7, verify it contains ownerEntry1 and ownerEntry2");
			entriesFeed = (Feed) user7.getBlogsService()
					.getRecentPostsBlogsFeed(
							testBlog.getHandleElement().getText(), 0, 0, null,
							null, null, null, null);
			assertEquals(2, entriesFeed.getEntries().size());
			boolean foundEntry1 = false, foundEntry2 = false;
			for (Entry e : entriesFeed.getEntries()) {
				if (e.getTitle().equals(ownerEntry1.getTitle()))
					foundEntry1 = true;
				else if (e.getTitle().equals(ownerEntry2.getTitle()))
					foundEntry2 = true;
			}
			assertEquals(true, foundEntry1 && foundEntry2);

			LOGGER.debug("Step 10... As moderator, get feed of entry approvals, very it contains authorEntry3");
			Feed entryApprovals = (Feed) moderator.getBlogsService()
					.getBlogsFeed(entryApprovalsLink);
			boolean foundEntry3 = false;
			String entry3EditLink = null; // Get the entry's edit link for step
			// 11
			for (Entry e : entryApprovals.getEntries())
				if (e.getTitle().equals(authorEntry3.getTitle())) {
					foundEntry3 = true;
					entry3EditLink = e.getEditLinkResolvedHref().toString();
				}
			assertEquals(true, foundEntry3);

			LOGGER.debug("Step 11... As moderator, delete authorEntry3");
			moderator.getBlogsService().deleteBlogsFeed(entry3EditLink);

			LOGGER.debug("Step 12... As moderator, get feed of entry approvals, verify it doesn't contain authorEntry3");
			entryApprovals = (Feed) moderator.getBlogsService().getBlogsFeed(
					entryApprovalsLink);
			for (Entry e : entryApprovals.getEntries())
				if (e.getTitle().equals(authorEntry3.getTitle()))
					assertTrue("Feed should not contain authorEntry3", false);

			LOGGER.debug("Step 13... As moderator, quarantine and then delete ownerEntry2");
			// Get the entry's review history link as well as its edit link (for
			// the deletion)
			Feed entryReviews = (Feed) moderator.getBlogsService()
					.getBlogsFeed(entryReviewsLink);
			String reviewHistoryLink = null, entry2EditLink = null;
			for (Entry e : entryReviews.getEntries())
				if (e.getTitle().equals(ownerEntry2.getTitle())) {
					reviewHistoryLink = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
					entry2EditLink = e.getEditLinkResolvedHref().toString();
				}

			// Generate atom entry which represents the review/quarantine
			Entry entry2Quarantine = factory.newEntry();
			entry2Quarantine.addLink(reviewHistoryLink, "related");
			entry2Quarantine.addSimpleExtension(StringConstants.SNX_MODERATION,
					null).setAttributeValue("action", "quarantine");
			entry2Quarantine
					.setContent("Entry is too dangerous for these waters.");

			// Send the quarantine
			moderator.getBlogsService().postBlogsFeed(reviewsActionLink,
					entry2Quarantine);

			// Delete ownerEntry2
			moderator.getBlogsService().deleteBlogsFeed(entry2EditLink);

			LOGGER.debug("Step 14... As moderator, get feed of quarantined entry reviews, verify it doesn't contain ownerEntry2");
			entryReviews = (Feed) moderator.getBlogsService().getBlogsFeed(
					entryReviewsLink + "?status=quarantined");
			for (Entry e : entryReviews.getEntries())
				if (e.getTitle().equals(ownerEntry2.getTitle()))
					assertTrue("Feed should not contain ownerEntry2", false);

			LOGGER.debug("Step 15... As moderator, delete authorComment2");
			// Get the comment's edit link from the feed of comment reviews
			Feed commentApprovals = (Feed) moderator.getBlogsService()
					.getBlogsFeed(commentApprovalsLink);
			String comment2EditLink = null;
			for (Entry e : commentApprovals.getEntries())
				if (e.getContent().equals(authorComment2.getContent()))
					comment2EditLink = e.getEditLinkResolvedHref().toString();

			// Delete the comment
			moderator.getBlogsService().deleteBlogsFeed(comment2EditLink);

			LOGGER.debug("Step 16... As moderator, get feed of comment approvals, verify it doesn't contain authorComment2");
			commentApprovals = (Feed) moderator.getBlogsService().getBlogsFeed(
					commentApprovalsLink);
			for (Entry e : commentApprovals.getEntries())
				if (e.getContent().equals(authorComment2.getContent()))
					assertTrue("Feed should not contain authorComment2", false);

			LOGGER.debug("Step 17... As moderator, quarantine and then delete ownerComment1");
			// Get the comment's review history link as well as its edit link
			// (for the deletion)
			Feed commentReviews = (Feed) moderator.getBlogsService()
					.getBlogsFeed(commentReviewsLink);
			String comment1ReviewHistoryLink = null, comment1EditLink = null;
			for (Entry e : commentReviews.getEntries())
				if (e.getContent().equals(ownerComment1.getContent())) {
					comment1ReviewHistoryLink = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
					comment1EditLink = e.getEditLinkResolvedHref().toString();
				}

			// Generate atom entry which represents the review/quarantine
			Entry comment1Quarantine = factory.newEntry();
			comment1Quarantine.addLink(comment1ReviewHistoryLink, "related");
			comment1Quarantine.addSimpleExtension(
					StringConstants.SNX_MODERATION, null).setAttributeValue(
					"action", "quarantine");
			comment1Quarantine
					.setContent("That comment is wayyyy too bad for this blog. GET OUT.");

			// Send the quarantine
			moderator.getBlogsService().postBlogsFeed(commentReviewsActionLink,
					comment1Quarantine);

			// Delete ownerComment1
			moderator.getBlogsService().deleteBlogsFeed(comment1EditLink);

			LOGGER.debug("Step 18... As moderator, get feed of quarantined comment reviews, verify it doesn't contain ownerComment1");
			commentReviews = (Feed) moderator.getBlogsService().getBlogsFeed(
					commentReviewsLink + "?status=quarantined");
			for (Entry e : commentReviews.getEntries())
				if (e.getContent().equals(ownerComment1.getContent()))
					assertTrue("Feed should not contain ownerComment1", false);

			LOGGER.debug("ENDING TEST: Blogs moderation deletion");
		}
	}

	@Test
	public void createBlogEntry() {
		/*
		 * Testing enpoint :
		 * /blogs/roller-ui/rendering/api/<blogname>/api/entries POST a blog
		 * entry using above endpoint Step 1... create test blog to comment on,
		 * verify Step 2... post on the test blog, verify Step 3... get feed of
		 * blog entries, look for the posted comment for extra verification.
		 */

		LOGGER.debug("Step 1... create test blog to comment on, verify");
		String blogName = "NHL_Stanley_Cup"
				+ RandomStringUtils.randomAlphanumeric(7);
		String handle = "the_Handle" + RandomStringUtils.randomAlphanumeric(7);
		LOGGER.debug("createBlogEntry - Marking handle_data ====== " + handle);
		Blog blog = new Blog(blogName, handle, "Because its the cup",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		Entry blogPost = (Entry) service.createBlog(blog);
		assertEquals("Create blog", 201, service.getRespStatus());
		assertTrue("Get blog back", blogPost.getId() != null);

		LOGGER.debug("Step 2... post on the test blog, verify");
		BlogPost post = new BlogPost("First Post", "Cool blog post from API",
				"first post blog", true, 5);
		Entry result = (Entry) service.postBlogsFeed(URLConstants.SERVER_URL
				+ "/blogs/roller-ui/rendering/api/" + handle + "/api/entries",
				post.toEntry());
		BlogPost response = new BlogPost(result);
		assertTrue(response.getId() != null);

		LOGGER.debug("Step 3... get feed of blog entries, look for the posted comment for extra verification.");
		ExtensibleElement myFeed = service.getBlogFeed(URLConstants.SERVER_URL
				+ "/blogs/roller-ui/rendering/api/" + handle + "/api/entries");
		assertTrue(myFeed.toString().contains("Cool blog post from API"));

		LOGGER.debug("Ending Test");

	}

	// @Test
	public void blogsModerationQuarantine() throws FileNotFoundException,
			IOException {
		/*
		 * Tests quarantining of a blog entry by a moderator Step 0
		 * (Initialize): Delete blogs, create instances of other users, get
		 * needed links from the blogs moderation service document Step 1:
		 * Create a blog with members Step 2: Create an entry (ownerEntry1) in
		 * the blog Step 3: Create an entry (ownerEntry2) in the blog as second
		 * owner user1 Step 4: Create an entry (authorEntry3) in the blog as
		 * user5 Step 5: Flag ownerEntry1 as user5 Step 6: Create a comment on
		 * ownerEntry1 (comment1) and ownerEntry2 (comment2) as user7 Step 7:
		 * Flag ownerEntry1 as user7 Step 8: Create another comment on
		 * ownerEntry1 (comment3) and ownerEntry2 (comment4) as user7 Step 9:
		 * Get feed of entries in the blog, verify it contains ownerEntry1 and
		 * ownerEntry2 Step 10: As moderator, get feed of entry approvals,
		 * verify it contains authorEntry3 Step 11: As moderator, get feed of
		 * comment approvals, verify it contains all 4 comments created Step 12:
		 * As moderator, get feed of entry reviews, verify it contains the
		 * flagged ownerEntry1 Step 13: As moderator, quarantine ownerEntry1
		 * Step 14: As moderator, get feed of comment approvals, verify it now
		 * doesn't contain comment1 & comment3 Step 15: As moderator, restore
		 * ownerEntry1 Step 16: As moderator, get feed of comment approvals,
		 * verify it contains all 4 comments again
		 */
		if (StringConstants.MODERATION_ENABLED) {
			LOGGER.debug("BEGINNING TEST: Blogs moderation quarantine");

			LOGGER.debug("Step 0 (Initialize): Delete blogs, create instances of other users, get needed links from the blogs moderation service document");
			String randString = RandomStringUtils.randomAlphanumeric(10);
			service.deleteAllBlogs();
			assertEquals("Method : blogsModerationQuarantine", true, service.getBlogsHomepageHandle().equalsIgnoreCase("homepage"));
			LOGGER.debug("Check if homepage handle is still existent = " + service.getBlogsHomepageHandle());

			UserPerspective user1=null,user5=null,user7=null,moderator=null;
			try {
				user1 = new UserPerspective(6,
						Component.BLOGS.toString(), useSSL);
				user5 = new UserPerspective(5,
						Component.BLOGS.toString(), useSSL);
				user7 = new UserPerspective(7,
						Component.BLOGS.toString(), useSSL);
				moderator = new UserPerspective(3,
						Component.BLOGS.toString(), useSSL);
			} catch (LCServiceException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Get needed links from blogs moderation service doc
			String entryApprovalsLink = null, entryReviewsLink = null, reviewsActionLink = null;
			String commentApprovalsLink = null;
			Service moderationServiceDoc = (Service) moderator
					.getBlogsService().getModerationServiceDoc();
			for (Workspace workspace : moderationServiceDoc.getWorkspaces()) {
				if (workspace.toString().contains("entries-moderation")) {
					for (Collection collection : workspace.getCollections()) {
						if (collection.toString().contains("approval-content"))
							entryApprovalsLink = collection.getHref()
									.toString(); // Get the approvals link
						else if (collection.toString().contains(
								"review-content"))
							entryReviewsLink = collection.getHref().toString(); // Get
						// the
						// entry
						// reviews
						// link
						else if (collection.toString()
								.contains("review-action"))
							reviewsActionLink = collection.getHref().toString(); // Get
						// the
						// reviews
						// action
						// link
					}
				} else if (workspace.toString().contains("comments-moderation")) {
					for (Collection collection : workspace.getCollections()) {
						if (collection.toString().contains("approval-content"))
							commentApprovalsLink = collection.getHref()
									.toString(); // Get the comment approvals
						// link
					}
				}
			}

			LOGGER.debug("Step 1... Create a blog with members");
			Blog testBlog = new Blog("API Test Moderation Quarantine Blog "
					+ randString, "BlogHandle" + randString, "Summary", "",
					false, false, null, null, TimeZone.getDefault(), true, 50,
					false, true, true, 0, -1, null, null, null, 0);
			LOGGER.debug("blogsModerationQuarantine =========== BlogHandle" + randString);
			String[] ownerEmails = { user1.getEmail() };
			String[] authorEmails = { user5.getEmail(), user7.getEmail() };
			ExtensibleElement eblog = service.createBlogWithMembers(testBlog,
					ownerEmails, authorEmails);
			testBlog = new Blog((Entry) eblog);

			LOGGER.debug("Step 2... Create an entry (ownerEntry1) in the blog");
			BlogPost ownerEntry1 = new BlogPost("Owner Entry 1 -" + randString,
					"Integrate our business into the mickey mouse API project",
					null, true, 50);
			Entry result1 = (Entry) service.createPost(testBlog, ownerEntry1);

			LOGGER.debug("Step 3... Create an entry (ownerEntry2) in the blog as second owner user1");
			BlogPost ownerEntry2 = new BlogPost(
					"Owner Entry 2 -" + randString,
					"Just been working on the cognos repository dependecy stabilization runtime deployment pipeline checkpoint validation success failure status log",
					null, true, 50);
			Entry result2 = (Entry) user1.getBlogsService().createPostManually(
					testBlog.getHandleElement().getText(), ownerEntry2);

			LOGGER.debug("Step 4... Create an entry (authorEntry3) in the blog as user5");
			BlogPost authorEntry3 = new BlogPost(
					"Author Entry 3 -" + randString,
					"Listen up, here's what we should do. Validate the FVT admin failure kit RPM on-premise deployment power outage waffle project plugin server pool jenkins issue API BVT LDAP TDI JVM bug SC Linux Pipeline",
					null, true, 50);
			user5.getBlogsService().createPostManually(
					testBlog.getHandleElement().getText(), authorEntry3);

			LOGGER.debug("Step 5... Flag ownerEntry1 as user5");
			// Get the entry's self link
			Feed entriesFeed = (Feed) user5.getBlogsService()
					.getRecentPostsBlogsFeed(
							testBlog.getHandleElement().getText(), 0, 0, null,
							null, null, null, null);
			String entry1SelfLink = null;
			for (Entry e : entriesFeed.getEntries())
				if (e.getTitle().equals(ownerEntry1.getTitle()))
					entry1SelfLink = e.getSelfLinkResolvedHref().toString();

			// Generate atom entry which represents the flag
			Factory factory = abdera.getFactory();
			Entry entry1Flag = factory.newEntry();
			entry1Flag.addCategory("http://www.ibm.com/xmlns/prod/sn/issue",
					"001", "Legal issuueee");
			entry1Flag.addLink(entry1SelfLink, "related");
			entry1Flag.setContent("SVT WAS CI BVT planning stream issue");

			// Send the flag
			user5.getBlogsService().flagBlogPost(entry1Flag);

			LOGGER.debug("Step 6... Create a comment on ownerEntry1 (comment1) and ownerEntry2 (comment2) as user7");
			BlogComment comment1 = new BlogComment(
					"This idea is way ahead of its time", result1);
			user7.getBlogsService().createCommentManually(
					testBlog.getHandleElement().getText(), comment1);
			BlogComment comment2 = new BlogComment(
					"Have you deployed the POC UI Jenkins Janet websphere installation?",
					result2);
			user7.getBlogsService().createCommentManually(
					testBlog.getHandleElement().getText(), comment2);

			LOGGER.debug("Step 7... Flag ownerEntry1 as user7");
			// Generate atom entry which represents the flag
			Entry entry1Flag_2 = factory.newEntry();
			entry1Flag_2.addCategory("http://www.ibm.com/xmlns/prod/sn/issue",
					"002", "HR issuueee");
			entry1Flag_2.addLink(entry1SelfLink, "related");
			entry1Flag_2
					.setContent("Completely innappropriate, idea is way too revolutionary");

			// Send the flag
			user7.getBlogsService().flagBlogPost(entry1Flag_2);

			LOGGER.debug("Step 8... Create another comment on ownerEntry1 (comment3) and ownerEntry2 (comment4) as user7");
			BlogComment comment3 = new BlogComment(
					"Amy, lets have a meeting to discuss these possibilities",
					result1);
			user7.getBlogsService().createCommentManually(
					testBlog.getHandleElement().getText(), comment3);
			BlogComment comment4 = new BlogComment(
					"Moderation deployment documentation pass plugin DB log",
					result2);
			user7.getBlogsService().createCommentManually(
					testBlog.getHandleElement().getText(), comment4);

			LOGGER.debug("Step 9... Get feed of entries in the blog, verify it contains ownerEntry1 and ownerEntry2");
			entriesFeed = (Feed) user7.getBlogsService()
					.getRecentPostsBlogsFeed(
							testBlog.getHandleElement().getText(), 0, 0, null,
							null, null, null, null);
			assertEquals(2, entriesFeed.getEntries().size());
			boolean foundEntry1 = false, foundEntry2 = false;
			for (Entry e : entriesFeed.getEntries()) {
				if (e.getTitle().equals(ownerEntry1.getTitle()))
					foundEntry1 = true;
				else if (e.getTitle().equals(ownerEntry2.getTitle()))
					foundEntry2 = true;
			}
			assertEquals(true, foundEntry1 && foundEntry2);

			LOGGER.debug("Step 10... As moderator, get feed of entry approvals, verify it contains authorEntry3");
			Feed entryApprovals = (Feed) moderator.getBlogsService()
					.getBlogsFeed(entryApprovalsLink);
			boolean foundEntry3 = false;
			for (Entry e : entryApprovals.getEntries())
				if (e.getTitle().equals(authorEntry3.getTitle()))
					foundEntry3 = true;
			assertEquals(true, foundEntry3);

			LOGGER.debug("Step 11... As moderator, get feed of comment approvals, verify it contains all 4 comments created");
			Feed commentApprovals = (Feed) moderator.getBlogsService()
					.getBlogsFeed(commentApprovalsLink);
			boolean foundComment1 = false, foundComment2 = false, foundComment3 = false, foundComment4 = false;
			for (Entry e : commentApprovals.getEntries()) {
				if (e.getContent().equals(comment1.getContent()))
					foundComment1 = true;
				else if (e.getContent().equals(comment2.getContent()))
					foundComment2 = true;
				else if (e.getContent().equals(comment3.getContent()))
					foundComment3 = true;
				else if (e.getContent().equals(comment4.getContent()))
					foundComment4 = true;
			}
			assertEquals(true, foundComment1 && foundComment2 && foundComment3
					&& foundComment4);

			LOGGER.debug("Step 12... As moderator, get feed of entry reviews, verify it contains the flagged ownerEntry1");
			Feed entryReviews = (Feed) moderator.getBlogsService()
					.getBlogsFeed(entryReviewsLink);
			foundEntry1 = false;
			String reviewHistoryLink = null; // use this step to also get the
			// entry's history link for step
			// 13
			for (Entry e : entryReviews.getEntries())
				if (e.getTitle().equals(ownerEntry1.getTitle())) {
					foundEntry1 = true;
					reviewHistoryLink = e.getLinkResolvedHref(
							"http://www.ibm.com/xmlns/prod/sn/history")
							.toString();
				}
			assertEquals(true, foundEntry1);

			LOGGER.debug("Step 13... As moderator, quarantine ownerEntry1");
			// Generate atom entry which represents the quarantine
			Entry entry1Quarantine = factory.newEntry();
			entry1Quarantine.addLink(reviewHistoryLink, "related");
			entry1Quarantine.addSimpleExtension(StringConstants.SNX_MODERATION,
					null).setAttributeValue("action", "quarantine");
			entry1Quarantine.setContent("Idea is too good");

			// Send the quarantine - this will prevent ownerEntry1's pending
			// comments from displaying
			moderator.getBlogsService().postBlogsFeed(reviewsActionLink,
					entry1Quarantine);

			LOGGER.debug("Step 14... As moderator, get feed of comment approvals, verify it now doesn't contain comment1 & comment3");
			commentApprovals = (Feed) moderator.getBlogsService().getBlogsFeed(
					commentApprovalsLink);
			foundComment1 = false;
			foundComment2 = false;
			foundComment3 = false;
			foundComment4 = false;
			for (Entry e : commentApprovals.getEntries()) {
				if (e.getContent().equals(comment1.getContent()))
					foundComment1 = true;
				else if (e.getContent().equals(comment2.getContent()))
					foundComment2 = true;
				else if (e.getContent().equals(comment3.getContent()))
					foundComment3 = true;
				else if (e.getContent().equals(comment4.getContent()))
					foundComment4 = true;
			}
			// Should only find comment 2 and comment 4, since they were not
			// posted for quarantined ownerEntry1
			assertEquals(true, !foundComment1 && foundComment2
					&& !foundComment3 && foundComment4);

			LOGGER.debug("Step 15... As moderator, restore ownerEntry1");
			// Generate atom entry which represents the restore
			Entry entry1Restore = factory.newEntry();
			entry1Restore.addLink(reviewHistoryLink, "related");
			entry1Restore.addSimpleExtension(StringConstants.SNX_MODERATION,
					null).setAttributeValue("action", "restore");
			entry1Restore.setContent("Idea is now of acceptable goodness");

			// Send the quarantine
			moderator.getBlogsService().postBlogsFeed(reviewsActionLink,
					entry1Restore);

			LOGGER.debug("Step 16... As moderator, get feed of comment approvals, verify it contains all 4 comments again");
			commentApprovals = (Feed) moderator.getBlogsService().getBlogsFeed(
					commentApprovalsLink);
			foundComment1 = false;
			foundComment2 = false;
			foundComment3 = false;
			foundComment4 = false;
			for (Entry e : commentApprovals.getEntries()) {
				if (e.getContent().equals(comment1.getContent()))
					foundComment1 = true;
				else if (e.getContent().equals(comment2.getContent()))
					foundComment2 = true;
				else if (e.getContent().equals(comment3.getContent()))
					foundComment3 = true;
				else if (e.getContent().equals(comment4.getContent()))
					foundComment4 = true;
			}
			// Should now contain all comments since ownerEntry1 was restored
			assertEquals(true, foundComment1 && foundComment2 && foundComment3
					&& foundComment4);

			LOGGER.debug("ENDING TEST: Blogs moderation quarantine");
		}
	}

	@Test
	public void getBlogEntriesRollerUI() {
		/*
		 * RTC 116651 Tests the ability to get blog entries using the endpoint
		 * "/blogs/roller-ui/rendering/api/<blogname>/api/entries" Step 1:
		 * Create a blog Step 2: Get blog entries, verify it's empty Step 3:
		 * Create entries in the blog Step 4: Get blog entries, verify it
		 * contains only the entries created
		 */
		LOGGER.debug("BEGINNING TEST: Get Blog Entries Roller UI");
		String rand = RandomStringUtils.randomAlphanumeric(5);

		LOGGER.debug("Step 1... Create a blog");
		Blog testBlog = new Blog("API Test Blog " + rand, "TestHandle" + rand,
				"Blog summary", "", false, false, null, null,
				TimeZone.getDefault(), true, 50, false, true, true, 0, -1,
				null, null, null, 0);

		ExtensibleElement eblog = service.createBlog(testBlog);
		assertEquals(" Create Blog", 201, service.getRespStatus());
		testBlog = new Blog((Entry) eblog);
		// TODO: add wait here to make sure get blog back

		LOGGER.debug("Step 2... Get blog entries, verify it's empty");
		Feed blogEntries = (Feed) service.getBlogEntriesRollerUI(testBlog
				.getHandleElement().getText());
		assertEquals("Size of blog entries feed should be 0", 0, blogEntries
				.getEntries().size());

		LOGGER.debug("Step 3... Create entries in the blog");
		BlogPost testPost1 = new BlogPost("TestPost1_" + rand, "Post1 content",
				null, true, 50);
		BlogPost testPost2 = new BlogPost("TestPost2_" + rand, "Post2 content",
				null, true, 50);
		service.createPost(testBlog, testPost1);
		service.createPost(testBlog, testPost2);

		LOGGER.debug("Step 4... Get blog entries, verify it contains only the entries created");
		blogEntries = (Feed) service.getBlogEntriesRollerUI(testBlog
				.getHandleElement().getText());
		assertEquals("Size of blog entries feed should be 2", 2, blogEntries
				.getEntries().size());
		boolean foundPost1 = false, foundPost2 = false;
		for (Entry e : blogEntries.getEntries()) {
			if (e.getTitle().equals(testPost1.getTitle()))
				foundPost1 = true;
			else if (e.getTitle().equals(testPost2.getTitle()))
				foundPost2 = true;
		}
		assertEquals(
				"Test post1 and test post2 should have been found in the feed",
				true, foundPost1 && foundPost2);

		LOGGER.debug("ENDING TEST: Get Blog Entries Roller UI");
	}

	// RTC 120171 - first call to get Hit Count will take about a mins
	@Test
	public void testHitCounts() {
		/*
		 * tests endpoint blogs/<identifier/api/recommend/entries 
		 * S1. create test blog + blog entry 
		 * S2. get enpoint 
		 * S3. check hit counts
		 */
		LOGGER.debug("Starting S1....create test blog + blog entry");
		Blog regBlog = new Blog("HitCount" + Utils.uniqueString, "the_Handle",
				"Because its the cup", "tagBlogs_" + Utils.uniqueString, false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		Entry myBlog = (Entry) service.createBlog(regBlog);
		assertEquals(" Create Blog"+service.getDetail(), 201, service.getRespStatus());
		
		regBlog = new Blog((Entry) myBlog);
		BlogPost newPost = new BlogPost("Hit Counts", "Games to look out for",
				"Gaming" + Utils.uniqueString, true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, newPost);
		assertEquals(" Create Blog Post"+service.getDetail(), 201, service.getRespStatus());
		assertTrue(postResult.getId() != null);// check

		LOGGER.debug("Starting S2....get endpoint");
		String handle = regBlog.getHandleElement().getText();
		 
		String hitUrl = postResult.getAlternateLinkResolvedHref().toString();
		String selfUrl = postResult.getEditLinkResolvedHref().toString();

		LOGGER.debug("Starting S3....check hit counts");
		service.getBlogFeed(hitUrl);
		myBlog = (Entry) service.getBlogFeed(selfUrl);
		Blog blog = new Blog(myBlog);
		String rank = blog.getHitRank().getText();
		LOGGER.debug("rank....check hit counts:"+rank);

		service.getBlogFeed(hitUrl);
		myBlog = (Entry) service.getBlogFeed(selfUrl);
		blog = new Blog(myBlog);
		String rank2 = blog.getHitRank().getText();
		LOGGER.debug("rank2....check hit counts:"+rank2);

		service.getBlogFeed(hitUrl);
		myBlog = (Entry) service.getBlogFeed(selfUrl);
		blog = new Blog(myBlog);
		String rank3 = blog.getHitRank().getText();
		LOGGER.debug("rank3....check hit counts:"+rank3);

		assertEquals("Hit Rank", Integer.parseInt(rank3),
				Integer.parseInt(rank) + 2);

	}

	@Test
	public void postBookMarkinBlogs() {
		LOGGER.debug("Starting Test: Post Bookmark into a blog");
		// delete old blog tests before testing dogear
		service.deleteTests();

		// create a blog to test with
		Blog regBlog = new Blog("Spencer", "BionicArm",
				"Soldier that lost his arm in the war.", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		Bookmark newBookmark = new Bookmark("Google",
				"This is a bookmark for the Google search engine.",
				"http://www.google.com", "tagDogear_"
						+ Utils.logDateFormatter.format(new Date()));
		Entry result = newBookmark.toEntry();
		assertTrue(result != null);
		service.createBookmarkPost(regBlog, result);

		// get all blog entires for regBlog
		ArrayList<BlogPost> blogPosts = service.getBlogEntries(regBlog, null,
				null, 0, 0, null, null, null, null, BlogsType.BLOG, null, null);

		// validate
		if (blogPosts.get(0).getTitle().equals(result.getTitle())) {
			LOGGER.debug("Test Succesful: Found bookmark posted into a blog");
			assertTrue(true);
		} else {
			LOGGER.debug("Test Failed: Bookmark was not posted into a blog");
			assertTrue(false);
		}

	}

	// @Test TODO: following 6 methods run error on impersonate
	public void getMyBlogs() {
		LOGGER.debug("BEGINNING TEST: Get My Blogs");
		service.deleteTests();

		// create blog
		String blogTitle = "JamesBlog";
		Blog regBlog = new Blog(blogTitle, "APIRegularBlog19",
				"This blog is for testing and verifying regular blog creation",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		Entry regBlogEntry = (Entry) service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());

		// retrieve all blogs
		ArrayList<Blog> myBlogs = service.getMyBlogs(null, null, 0, 0, null,
				null, null, null, BlogsType.BLOG, null, null);

		// validate
		if (myBlogs.get(0).getTitle().equals(regBlogEntry.getTitle())) {
			LOGGER.debug("SUCCESS: Feed of my blogs was found, first blog in list matched new blog");
			assertTrue(true);

			// RTC 113372
			List<Person> persons = myBlogs.get(0).getAuthors();
			assertNotNull("No authero ", persons);
			for (Person person : persons) {
				String isExternal = person
						.getSimpleExtension(StringConstants.SNX_ISEXTERNAL);
				assertNotNull("isExternal should be there", isExternal);
			}
		} else {
			LOGGER.debug("ERROR: Feed of my blogs not found, first blog in list did not match new blog");
			assertTrue(false);
		}
		LOGGER.debug("COMPLETED TEST: Get My Blogs");
	}

    // @Test
    public void getMedia() throws IOException {
        /*
         * Tests ability to retrieve blogs media Step 1: Create a blog Step 2:
         * Check to see if an image is present already, if so, delete it. Step
         * 3: Upload an image Step 4: Verify that the image is there (Will be
         * re-named to {handleName}-{Timestamp}) Step 5:  Verify Cache-Control 
         * header of Blogs attachment download API
         */

        LOGGER.debug("Beginning test...\nINFO: Step 1...create a blog");
        String randomString = RandomStringUtils.randomAlphanumeric(5);
        String handle = "mediaFile";
        Blog regBlog = new Blog("Media Test Blog" + randomString, handle,
                "Epic Blog Description", "tagYoureIt", false, false, null,
                null, TimeZone.getDefault(), true, 13, true, true, true, 0, -1,
                null, null, null, 0);
        try {
            service.createBlog(regBlog);
            assertEquals("Create blog failed "+service.getDetail(), 201, service.getRespStatus());
        } catch (Exception e) {
            LOGGER.debug("A blog already exists here; skipping step 1");// unless
            // createBlog(newBlog)
            // isn't
            // working
            // properly
        }

        LOGGER.debug("Step 1 Completed");
        LOGGER.debug("Step 2: Check to see if images are present. If so, delete them");
        // get the handle without spaces (they're removed when the url is
        // created)
        String[] handleArray = regBlog.getHandleElement().getText().split(" ");
        String handleNoSpace = "";
        for (String i : handleArray) {
            handleNoSpace += i;
        }

        // Now get the feed where existing images would be
        Feed mediaFeed = null;
        try {
            mediaFeed = (Feed) service.getBlogFeed(service
                    .getBlogsMediaURLString(handleNoSpace));
            assertEquals("Get blog feed failed "+service.getDetail(), 200, service.getRespStatus());
        } catch (Exception e) {
        }
        Entry entry;
        if (mediaFeed != null)
            for (int i = 0; i < mediaFeed.getEntries().size(); i++) {
                entry = (mediaFeed.getEntries().get(i));
                if (!entry.getTitle().equals(null)) {
                    service.deletePost(entry.getEditLinkResolvedHref()
                            .toString());
                    LOGGER.debug("An old post has been deleted");
                }
            }
        LOGGER.debug("Step 2 Completed");
        LOGGER.debug("Step 3: Post/Upload an image to a blog's media");

        BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream(
                "/resources/lamborghini_murcielago_lp640.jpg"));
        File file = File.createTempFile("lambo", ".jpg");
        ImageIO.write(image, "jpg", file);

        try {
            service.postFile(service.getBlogsMediaURLString(handleNoSpace),
                    file);
            assertEquals("Post file failed "+service.getDetail(), 201, service.getRespStatus());
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false); // file didn't get posted because some error
            // occurred
        }
        LOGGER.debug("Step 3 Completed");
        LOGGER.debug("Step 4: Verify that the image was posted");
        try {
            mediaFeed = (Feed) service.getBlogFeed(service
                    .getBlogsMediaURLString(handleNoSpace));
            assertEquals("Get blog feed failed "+service.getDetail(), 200, service.getRespStatus());
            entry = (mediaFeed.getEntries().get(0)); // 0 because it should be
            // the first element
            // since it just posted
            assertTrue(entry.getTitle() != null);
            LOGGER.debug("Step 4 Completed");

            LOGGER.debug("Step 5: : Verify Cache-Control header of Blogs attachment download API");
            ClientResponse cr = service.getResponse(service
                    .getBlogsMediaURLString(handleNoSpace) + "/" + entry.getTitle());
            assertEquals("Getting media failed "+service.getDetail(), 200, service.getRespStatus());
            
            String[] headerNames = cr.getHeaderNames();
            for (int i=headerNames.length-1; i>=0; i--){
                String headerName = headerNames[i];
//                LOGGER.debug("header" + i + ": " + headerName + ":" + cr.getHeader(headerName));
                if("Cache-Control".equals(headerName))
                {
                    assertEquals(cr.getHeader(headerName), "public, max-age=5, s-maxage=5");
                    break;
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
            assertTrue(false);
        }
        LOGGER.debug("Step 5 Completed");
        LOGGER.debug("Test Completed");
    }

	
	// @Test
	public void publishingAPISortOrder() throws MalformedURLException,
			URISyntaxException, InterruptedException {
		/*
		 * Process: 1. Create blog and 5 entries in non-sorted order 2. Generate
		 * feed sorted by "commented" and validate 3. Generate feed sorted by
		 * "commentcount" and validate. Sort order tested too. 4. Generate feed
		 * sorted by "created" and validate. Sort order tested too.
		 */

		LOGGER.debug("BEGINNING TEST: RTC 94408 Sort options defect test.");
		LOGGER.debug("Step 1...create blog.");

		String A = "A Entry RTC 94408 Sort";
		String B = "B Entry RTC 94408 Sort";
		String C = "C Entry RTC 94408 Sort";
		String D = "D Entry RTC 94408 Sort";
		String E = "E Entry RTC 94408 Sort";
		String description = "This blog is for testing sorting";

		String[] sortedList = { A, B, C, D, E };
		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String blogName = "Test_publishing_sort_" + uniqueNameAddition;
		String blogTitle = "RTC 94408 Blogs publishing api sort "
				+ uniqueNameAddition;

		// create a blog
		Blog regBlog = new Blog(blogTitle, blogName, "RTC 94408", null, false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		ExtensibleElement eblog = service.createBlog(regBlog);
		assertEquals("Create blog", 201, service.getRespStatus());
		regBlog = new Blog((Entry) eblog);

		LOGGER.debug("step 2...create entry C");
		// create entry
		BlogPost postC = new BlogPost(C, "Test C", "Entry", true, 4);
		Entry postResultC = (Entry) service.createPost(regBlog, postC);
		assertTrue(postResultC.getId() != null);
		Thread.sleep(1000); // To stop the entries from being posted too close
		// together in time - which makes the sortOrder
		// validation fail

		LOGGER.debug("step 3...create entry B");
		// create entry
		BlogPost postB = new BlogPost(B, "Test B", "Entry", true, 4);
		Entry postResultB = (Entry) service.createPost(regBlog, postB);
		assertTrue(postResultB.getId() != null);
		Thread.sleep(1000);

		LOGGER.debug("step 4...create comment in entry");
		// create a comment in entry B
		String commentTitleB = "Comment for the Entry B";
		BlogComment commentB = new BlogComment(commentTitleB, postResultB);
		Entry theCommentB = (Entry) service.createComment(regBlog, commentB);
		assertTrue(theCommentB.getId() != null);

		// create another comment in entry B
		LOGGER.debug("step 5...create another comment in entry");
		String commentTitleB2 = "2nd comment for the Entry B";
		BlogComment commentB2 = new BlogComment(commentTitleB2, postResultB);
		Entry theCommentB2 = (Entry) service.createComment(regBlog, commentB2);
		assertTrue(theCommentB2.getId() != null);

		LOGGER.debug("step 6...create entry A in blog");
		// create entry
		BlogPost postA = new BlogPost(A, "Test A", "Entry", true, 4);
		Entry postResultA = (Entry) service.createPost(regBlog, postA);
		assertTrue(postResultA.getId() != null);
		Thread.sleep(1000);

		LOGGER.debug("step 7...create entry E");
		// create entry
		BlogPost postE = new BlogPost(E, "Test E", "Entry", true, 4);
		Entry postResultE = (Entry) service.createPost(regBlog, postE);
		assertTrue(postResultE.getId() != null);
		Thread.sleep(1000);

		LOGGER.debug("step 8...create entry D");
		// create entry
		BlogPost postD = new BlogPost(D, "Test D", "Entry", true, 4);
		Entry postResultD = (Entry) service.createPost(regBlog, postD);
		assertTrue(postResultD.getId() != null);
		Thread.sleep(1000);

		LOGGER.debug("step 9...create comment in entry D");
		// create a comment in entry D
		String commentTitleD = "Comment for the Entry D";
		BlogComment commentD = new BlogComment(commentTitleD, postResultD);
		Entry theCommentD = (Entry) service.createComment(regBlog, commentD);
		assertTrue(theCommentD.getId() != null);

		// Use this to sort.
		// https://lc45linux1.swg.usma.ibm.com/blogs/Test_publishing_sort/api/entries?sortBy=commentcount

		// Get /api/entries from service doc. Important! ! ! ! !
		String entriesUrl = "";
		Service ee = (Service) service.getBlogsFeed(URLConstants.SERVER_URL
				+ "/blogs/api");
		for (Workspace wrkSp : ee.getWorkspaces()) {
			if (wrkSp.getTitle().equalsIgnoreCase(blogTitle)) {
				for (Collection coll : wrkSp.getCollections()) {
					if (coll.getTitle().equalsIgnoreCase("Weblog Entries")) {
						IRI href = coll.getHref();
						entriesUrl = href.toURL().toString();
					}
				}
			}
		}

		LOGGER.debug("Step 10....generate feed of unsorted blogs using /api.");
		Feed beforeNtry2 = (Feed) service.getBlogsFeed(entriesUrl);

		LOGGER.debug("Entry list before using sort parameter:");
		for (Entry ntry : beforeNtry2.getEntries()) {
			LOGGER.debug("     " + ntry.getTitle());
		}

		String[] commentedSort = { B, D, E, A, C };
		LOGGER.debug("Step 11....generate feed with 'commented' sorting and validate.");
		String sortedUrl2 = entriesUrl + "?sortBy=commented";
		Feed afterNtry2 = (Feed) service.getBlogsFeed(sortedUrl2);
		LOGGER.debug("Entry title contents after using sort parameter:");
		int ndx2 = 0;
		for (Entry ntry : afterNtry2.getEntries()) {
			LOGGER.debug("     " + ntry.getTitle());
			assertEquals(commentedSort[ndx2++], ntry.getTitle());
		}

		String[] commentCountSort = { E, A, C, D, B };
		LOGGER.debug("Step 12....generate feed with 'commentcount' sorting and validate.  Sort order is asc");
		String sortedUrl3 = entriesUrl + "?sortBy=commentcount&sortOrder=asc";
		Feed afterNtry3 = (Feed) service.getBlogsFeed(sortedUrl3);
		LOGGER.debug("Entry title contents after using sort parameter:");
		int ndx3 = 0;
		for (Entry ntry : afterNtry3.getEntries()) {
			LOGGER.debug("     " + ntry.getTitle());
			assertEquals(commentCountSort[ndx3++], ntry.getTitle());
		}

		String[] createdSort = { D, E, A, B, C };
		LOGGER.debug("Step 13....generate feed with 'created' sorting and validate.  Sort order is desc.");
		String sortedUrl4 = entriesUrl + "?sortBy=created&sortOrder=desc";
		Feed afterNtry4 = (Feed) service.getBlogsFeed(sortedUrl4);
		LOGGER.debug("Entry title contents after using sort parameter:");
		int ndx4 = 0;
		for (Entry ntry : afterNtry4.getEntries()) {
			LOGGER.debug("     " + ntry.getTitle());
			assertEquals(createdSort[ndx4++], ntry.getTitle());
		}
		LOGGER.debug("ENDING TEST: RTC 94408 Sort options defect test.");
	}

	/**
	 * Limitation: POST and PUT request methods are not supported for updating
	 * the value of canEditComment and canDeleteComment.
	 * 
	 * This test validates the value of elements canEditComment and
	 * canDeleteComment. However, these values change based on who wrote the
	 * comment and who is accessing the comment.
	 * 
	 * Steps 1. Create a Blog and add 2 other members 2. Create an entry 3.
	 * Create a comment by a member 1. 4. Get comment edit link, and perform GET
	 * using 3 separate members (owner, member1 and member2). 5. Based on user,
	 * validate the values of canEditComment and canDeleteComment. The values
	 * change based on who is accessing the comment. a. comment author should
	 * have edit and delete privs. b. blog owner should not have edit rights but
	 * should have delete rights. c. other members, who are not owners, should
	 * have neither edit or delete privs. 6. Finally, try to edit and delete the
	 * comment as different users. Validate the results.
	 * 
	 */
	// @Test
	public void enableDisableBlogEdits() throws FileNotFoundException,
			IOException {
		LOGGER.debug("START TEST: RTC 109274 Add support in blogs feed to indicate whether a blog comment is editable or delete-able");
		String randString = RandomStringUtils.randomAlphanumeric(4);

		String blogHandle = "RTC109274_" + randString;
		LOGGER.debug("enableDisableBlogEdits blogHandle ======= " + blogHandle);
		String entryTitle = "Entry Title RTC 109274";
		String updatedContent = "This is the blog comment from blog owner - AFTER the edit.";

		UserPerspective owner=null,member=null,member2=null;
		// Add a member who is an owner.
		// Add another member, member 2, who is not an owner.
		try {
			owner = new UserPerspective(2,
					Component.BLOGS.toString(), useSSL);
			member = new UserPerspective(5,
					Component.BLOGS.toString(), useSSL);
			member2 = new UserPerspective(6,
					Component.BLOGS.toString(), useSSL);
		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		BlogsService memberService = member.getBlogsService();
		BlogsService member2Service = member2.getBlogsService();

		String[] ownerEmail = { owner.getEmail(), member.getEmail() };
		String[] memberEmail = { member2.getEmail() };

		LOGGER.debug("Step 1: Created Blog.  Add two other members but not as owners.");
		Blog regBlog = new Blog("RTC 109274 " + randString, blogHandle,
				"Support for editing or deleting blog comments", null, false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		Entry blogEntry = (Entry) service.createBlogWithMembers(regBlog,
				ownerEmail, memberEmail);
		assertEquals(" Create Blog", 201, service.getRespStatus());
		regBlog = new Blog(blogEntry);

		LOGGER.debug("Step 2: Create entry to blog");
		BlogPost post = new BlogPost(entryTitle, "Content", null, true, 4);
		Entry postResult = (Entry) service.createPost(regBlog, post);
		BlogPost response = new BlogPost(postResult);

		LOGGER.debug("Step 3: Create a comment by the other blog member (author role).");
		BlogComment memberComment = new BlogComment(
				"This is the blog comment from blog owner - before the edit.",
				postResult);
		Entry checkMemComment = (Entry) memberService.createComment(response,
				memberComment);

		String memhref = "";
		for (Element ell : checkMemComment.getElements()) {
			if (ell.toString().startsWith("<app:collection")) {
				memhref = ell.getAttributeValue(StringConstants.ATTR_HREF);
			}
		}

		// Manually create the entry for the comment update
		Factory factory = abdera.getFactory();
		Entry entry = factory.newEntry();
		entry.setContent(updatedContent);

		// Remove "recommend" to get comment link
		String memCommentLink = memhref.replace("recommend/", "");
		// String ownerCommentLink = href.replace("recommend/", "");

		LOGGER.debug("Step 4: Validate the values of canEditComment and canDeleteComment depending on who accesses comment. ");
		// Validations: These are in relation to the comment made by member
		// 1. Member should be able to edit and delete
		ExtensibleElement memEE = memberService.getBlogFeed(memCommentLink);
		assertEquals(true,
				memEE.getSimpleExtension(StringConstants.SNX_EDIT_COMMENT)
						.equalsIgnoreCase("true"));
		assertEquals(true,
				memEE.getSimpleExtension(StringConstants.SNX_DELETE_COMMENT)
						.equalsIgnoreCase("true"));

		// 2. Owner should not be able to edit but able to delete
		ExtensibleElement ownerEE = service.getBlogFeed(memCommentLink);
		assertEquals(true,
				ownerEE.getSimpleExtension(StringConstants.SNX_EDIT_COMMENT)
						.equalsIgnoreCase("false"));
		assertEquals(true,
				ownerEE.getSimpleExtension(StringConstants.SNX_DELETE_COMMENT)
						.equalsIgnoreCase("true"));

		// 3. Member2 should have neither edit or delete privs.
		ExtensibleElement mem2EE = member2Service.getBlogFeed(memCommentLink);
		assertEquals(true,
				mem2EE.getSimpleExtension(StringConstants.SNX_EDIT_COMMENT)
						.equalsIgnoreCase("false"));
		assertEquals(true,
				mem2EE.getSimpleExtension(StringConstants.SNX_DELETE_COMMENT)
						.equalsIgnoreCase("false"));

		// Now try to actually edit comment. First as member (who initially
		// wrote the comment).
		memberService.putEntry(memCommentLink, entry);
		Entry editEntry = (Entry) memberService.getBlogFeed(memCommentLink);
		String content = editEntry.getContent();
		// Validate
		assertEquals(true, content.equalsIgnoreCase(updatedContent));

		// Try to update the comment as owner - who did not write the initial
		// comment. This should fail.
		Entry editResult2 = (Entry) service.putEntry(memCommentLink, entry);
		String errorMsg = editResult2
				.getSimpleExtension(StringConstants.API_RESPONSE_MSG);
		assertEquals(true, errorMsg.equalsIgnoreCase("Unauthorized"));

		// Try to delete the comment as member2. This should also fail.
		boolean deleteResult = member2Service.deleteBlog(memCommentLink);
		assertEquals(false, deleteResult);

		LOGGER.debug("END TEST: RTC 109274  Add support in blogs feed to indicate whether a blog comment is editable or delete-able");
	}

	// @Test
	public void startIndexAndItemsPerPage() throws MalformedURLException,
			URISyntaxException {
		/*
		 * Process: 1. Create blog and 5 entries in sorted order. 2. Generate
		 * feed. 3. Validate that elements \"startIndex\" and \"itemsPerPage\"
		 * exist 4. Generate another feed. Use params ps=2 and page=2. 5.
		 * Validate that elements startIndex = 2 and itemsPerPage = 2 based on
		 * param values page=2 and ps=2 6. Generate a third feed. Use params
		 * ps=1 and page=4. 7. Validate that elements startIndex = 3 and
		 * itemsPerPage = 1 based on param values page=4 and ps=1
		 * 
		 * Predicting the value of startIndex is a little tricky. First, it's a
		 * 0-based index, so the maximum value of startIndex is one less than
		 * the total number of items (totalResults - 1).
		 * 
		 * Also, page size (itemsPerPage) is important in understanding how
		 * startIndex works. So if there are 5 items, and page size is 3 and the
		 * 'page' param is 2, the value of startIndex will be 3, as items 0 - 2
		 * will be on page 1, and page 2 will have items 3 and 4.
		 */

		LOGGER.debug("BEGINNING TEST: RTC 112403 ItemsPerPage and startIndex test.");

		String A = "A Entry RTC 112403";
		String B = "B Entry RTC 112403";
		String C = "C Entry RTC 112403";
		String D = "D Entry RTC 112403";
		String E = "E Entry RTC 112403";

		String uniqueNameAddition = RandomStringUtils.randomAlphanumeric(4);
		String blogName = "RTC112403";
		String blogTitle = "RTC 112403 ItemsPerPage and startIndex test "
				+ uniqueNameAddition;

		LOGGER.debug("Step 1...create blog.");
		Blog regBlog = new Blog(blogTitle, blogName, "RTC 112403", null, false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		Entry myBlog = (Entry) service.createBlog(regBlog);
		assertEquals(" Create Blog", 201, service.getRespStatus());
		LOGGER.debug("Created Blog");
		assertTrue(myBlog.getId() != null);
		regBlog = new Blog((Entry) myBlog);

		LOGGER.debug("step 2...create entry A in blog");
		// create entry
		BlogPost postA = new BlogPost(A, "Test A", "Entry", true, 4);
		Entry postResultA = (Entry) service.createPost(regBlog, postA);
		assertTrue(postResultA.getId() != null);

		LOGGER.debug("step 3...create entry B");
		// create entry
		BlogPost postB = new BlogPost(B, "Test B", "Entry", true, 4);
		Entry postResultB = (Entry) service.createPost(regBlog, postB);
		assertTrue(postResultB.getId() != null);

		LOGGER.debug("step 4...create entry C");
		// create entry
		BlogPost postC = new BlogPost(C, "Test C", "Entry", true, 4);
		Entry postResultC = (Entry) service.createPost(regBlog, postC);
		assertTrue(postResultC.getId() != null);

		LOGGER.debug("step 5...create entry D");
		// create entry
		BlogPost postD = new BlogPost(D, "Test D", "Entry", true, 4);
		Entry postResultD = (Entry) service.createPost(regBlog, postD);
		assertTrue(postResultD.getId() != null);

		LOGGER.debug("step 6...create entry E");
		// create entry
		BlogPost postE = new BlogPost(E, "Test E", "Entry", true, 4);
		Entry postResultE = (Entry) service.createPost(regBlog, postE);
		assertTrue(postResultE.getId() != null);

		String entriesUrl = "";
		Service ee = (Service) service.getBlogsFeed(URLConstants.SERVER_URL
				+ "/blogs/api");
		for (Workspace wrkSp : ee.getWorkspaces()) {
			if (wrkSp.getTitle().equalsIgnoreCase(blogTitle)) {
				for (Collection coll : wrkSp.getCollections()) {
					if (coll.getTitle().equalsIgnoreCase("Weblog Entries")) {
						IRI href = coll.getHref();
						entriesUrl = href.toURL().toString();
					}
				}
			}
		}

		LOGGER.debug("Step 7....generate feed.");
		Feed fd = (Feed) service.getBlogsFeed(entriesUrl);
		String startIndex = fd.getExtension(StringConstants.OS_STARTINDEX)
				.toString();
		String itemsPerPage = fd
				.getExtension(StringConstants.OS_ITEMS_PER_PAGE).toString();

		LOGGER.debug("Step 8....validate that elements \"startIndex\" and \"itemsPerPage\" exist");
		assertEquals(true, startIndex.startsWith("<os:startIndex"));
		assertEquals(true, itemsPerPage.startsWith("<os:itemsPerPage"));

		LOGGER.debug("Step 9....generate another feed. Use params ps=2 and page=2.");
		Feed fd2 = (Feed) service.getBlogsFeed(entriesUrl + "?ps=2&page=2");
		String startIndexText_1 = fd2.getExtension(
				StringConstants.OS_STARTINDEX).getText();
		String itemsPerPageText_1 = fd2.getExtension(
				StringConstants.OS_ITEMS_PER_PAGE).getText();

		LOGGER.debug("Step 10....validate that elements startIndex = 2 and itemsPerPage = 2 based on param values page=2 and ps=2");
		assertEquals("2", startIndexText_1);
		assertEquals("2", itemsPerPageText_1);

		LOGGER.debug("Step 11....generate a third feed. Use params ps=1 and page=4.");
		Feed fd3 = (Feed) service.getBlogsFeed(entriesUrl + "?ps=1&page=4");
		String startIndexText_2 = fd3.getExtension(
				StringConstants.OS_STARTINDEX).getText();
		String itemsPerPageText_2 = fd3.getExtension(
				StringConstants.OS_ITEMS_PER_PAGE).getText();

		LOGGER.debug("Step 12....validate that elements startIndex = 3 and itemsPerPage = 1 based on param values page=4 and ps=1");
		assertEquals("3", startIndexText_2);
		assertEquals("1", itemsPerPageText_2);

		LOGGER.debug("ENDING TEST: RTC 112403 ItemsPerPage and startIndex test.");

	}

	// @Test
	public void getAtomBlogPages() throws Exception {
		LOGGER.debug("Beginning Test: Test Subscription feed to see if it is base 0");
		service.deleteTests();
		boolean found = true;
		int index = 0;
		String nextPage;
		String[] blogHandle = new String[3];
		blogHandle[0] = "Finn";
		blogHandle[1] = "JamesBlog";
		blogHandle[2] = "Spencer";

		String entryTitle = "BionicLancer";

		Blog regBlog = null;

		BlogPost post = null;
		Entry postResult = null;
		BlogPost response = null;

		// create a blog to test with
		for (int i = 0; i < 3; i++) {
			regBlog = new Blog(blogHandle[i], blogHandle[i],
					"Blog used to test and such", "tagBlogs_"
							+ Utils.logDateFormatter.format(new Date()), false,
					false, null, null, TimeZone.getDefault(), true, 13, true,
					true, true, 0, -1, null, null, null, 0);
			ExtensibleElement eblog = service.createBlog(regBlog);
			assertEquals("Create blog", 201, service.getRespStatus());
			regBlog = new Blog((Entry) eblog);
		}

		LOGGER.debug("Created Blog");

		// create entry in a blog
		for (int i = 0; i < 3; i++) {
			post = new BlogPost(entryTitle + i,
					"Spencer uses his mechanical arm", "BIONIC", true, 4);
			postResult = (Entry) service.createPost(regBlog, post);
			response = new BlogPost(postResult);
			LOGGER.debug("Created entry to blog");
			service.postRecommend(postResult.getExtension(
					StringConstants.APP_COLLECTION).getAttributeValue("href"));
		}

		// create a comment to the entry
		for (int i = 0; i < 3; i++) {
			BlogComment comment = new BlogComment("BionicArm" + i, postResult);
			Entry checkComment = (Entry) service.createComment(response,
					comment);
			if (checkComment == null) {
				LOGGER.debug("Comment was not made successfully");
				assertTrue(false);
			}
		}
		LOGGER.debug("Created comment to blog entry");

		// -------------------------------------------------------------------------------------------------------------------------------------------
		Feed blogsApi = service.getBlogsApiFeed(null, null, 0, 2, null, null,
				null, null, null, null, null);

		if (blogsApi.getLinkResolvedHref("next").toString() != null) {
			index = blogsApi.getLinkResolvedHref("next").toString()
					.indexOf("page=");
			nextPage = blogsApi.getLinkResolvedHref("next").toString()
					.substring(index + 5);
			if (nextPage.startsWith("2")) {
				LOGGER.debug("PASS: Blogs Api is base 1");
			} else {
				LOGGER.debug("Failed: Blogs Api is not base 0");
				found = false;
			}
		}
		// -------------------------------------------------------------------------------------------------------------------------------------------
		Feed entriesApi = service.getEntriesApiFeed(blogHandle[2], null, null,
				0, 2, null, null, null, null, null, null, null);

		if (entriesApi.getLinkResolvedHref("next").toString() != null) {
			index = entriesApi.getLinkResolvedHref("next").toString()
					.indexOf("page=");
			nextPage = entriesApi.getLinkResolvedHref("next").toString()
					.substring(index + 5);
			if (nextPage.startsWith("2")) {
				LOGGER.debug("PASS: Entries Api is base 1");
			} else {
				LOGGER.debug("Failed: Entries Api is not base 0");
				found = false;
			}
		}
		// -------------------------------------------------------------------------------------------------------------------------------------------
		Feed commentsApi = service.getCommentsApiFeed(blogHandle[2], null,
				null, 0, 2, null, null, null, null, null, null, null);

		if (commentsApi.getLinkResolvedHref("next").toString() != null) {
			index = commentsApi.getLinkResolvedHref("next").toString()
					.indexOf("page=");
			nextPage = commentsApi.getLinkResolvedHref("next").toString()
					.substring(index + 5);
			if (nextPage.startsWith("2")) {
				LOGGER.debug("PASS: Comments Api is base 1");
			} else {
				LOGGER.debug("Failed: Comments Api is not base 0");
				found = false;
			}
		}
		assertTrue(found);
	}
	
	
	 @Test
	    public void testCorsPreflight_Atom() throws Exception {
	    	
	    	// Test requires CORS configured to trust 'fakedomain.org' - only true for on prem pool
	    	if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.ON_PREMISE) {
	    		return;
	    	}
	    	
	    	String url = service.getServiceURLString() + "/" + service.getBlogsHomepageHandle() + URLConstants.BLOGS_ALL;
	    	
	    	int[] statuses = service.makeCorsPreflightRequests(url);

	    	assertEquals("Untrusted CORS request should fail", 403, statuses[0]);
	    	assertEquals("Trusted CORS request should succeed", 200, statuses[1]);
	    }

}
