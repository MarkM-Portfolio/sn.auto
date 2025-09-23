package com.ibm.lconn.automation.framework.services.blogs.impersonated;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.commons.lang.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.BlogsTestBase;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;

import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;

//
/**
 * JUnit Tests via Connections API for Blogs Service
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class BlogsImpersonatedTest extends BlogsTestBase {

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(BlogsImpersonatedTest.class.getName());
	private static UserPerspective impersonateByotherUser,
			impersonateByotherOrgAdmin;
	private static BlogsService otherUserService, otherOrgAdminService;

	// protected static FileHandler fh;

	@BeforeMethod
	public static void setUp() throws Exception {

		// fh = new FileHandler("logs/" + Utils.logDateFormatter.format(new
		// Date()) + "_BlogsImpersonatedTest.xml", false);
		// LOGGER.addHandler(fh);

		LOGGER.debug("Start Initializing Blogs impersonation Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		userEnv.getImpersonateEnvironment(StringConstants.ADMIN_USER,
				StringConstants.CURRENT_USER, Component.BLOGS.toString());
		
		
		user = userEnv.getLoginUser();
		service = user.getBlogsService();//all test cases will be run against this user's service		
		
		imUser = userEnv.getImpersonatedUser();
		impersonateService = imUser.getBlogsService();

		LOGGER.debug("all test cases will be run against this user's service:" 
				+ ProfileLoader.getProfile(StringConstants.ADMIN_USER).getUserName()
				+ " with email: " + user.getEmail());
		LOGGER.debug("all test cases will be run as this user:"
				+ ProfileLoader.getProfile(StringConstants.CURRENT_USER).getUserName()
				+ " with email: " + imUser.getEmail());
			
	
		otherUser = userEnv.getLoginUserEnvironment(
				StringConstants.RANDOM1_USER, Component.BLOGS.toString());

		impersonateByotherUser = new UserPerspective(
				StringConstants.RANDOM1_USER, Component.BLOGS.toString(),
				useSSL, StringConstants.CURRENT_USER);
		otherUserService = impersonateByotherUser.getBlogsService();
		
		//clean up
		/*ArrayList<Blog> myBlogs = impersonateService.getMyBlogs(null, null, 0, 100, null,
				null, null, null, BlogsType.BLOG, null, null);
		for (Blog blog : myBlogs) {
			impersonateService.deleteBlog(blog.getEditHref());
			LOGGER.debug("Deleted blog " + blog.getTitle());
		}*/

		LOGGER.debug("Finished Initializing Blogs impersonate Test");
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
		Blog blog = new Blog(blogName, handle, "Because its the cup",
				"tagBlogs_" + Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		LOGGER.debug("createBlogEntry handle ======= " + handle);
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

	@Test
	public void getFeaturedBlogPosts() {
		super.getFeaturedBlogPosts();
	}

	@Test
	public void blogsSortOrder() {
		super.blogsSortOrder();
	}
	
	@Test
	public void getMyVotesFeed() {
		super.getMyVotesFeed();
	}
	
	@Test
	public void blogsFollowsService() throws FileNotFoundException, IOException {
		super.blogsFollowsService();
	}
	
	@Test
	public void deleteComment() {
		super.deleteComment();
	}
	
	@Test
	public void deleteRecommendedAPI() {
		super.deleteRecommendedAPI();
	}
	
	@Test
	public void verifyBlogPageReturn() {
		super.verifyBlogPageReturn();
	}
	
	@Test
	public void incorrectTagUrl() {
		super.incorrectTagUrl();
	}
	
	@Test
	public void createBlog() throws Exception {
		super.createBlog();
	}

	@AfterMethod
	public static void tearDown() {
		service.tearDown();
		otherUserService.tearDown();
	}
}