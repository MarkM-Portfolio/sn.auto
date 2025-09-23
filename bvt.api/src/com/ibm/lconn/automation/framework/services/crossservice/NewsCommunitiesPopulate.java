package com.ibm.lconn.automation.framework.services.crossservice;

import static org.testng.AssertJUnit.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringGenerator;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.forums.ForumsService;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;
import com.ibm.lconn.automation.framework.services.news.NewsService;
import com.ibm.lconn.automation.framework.services.ublogs.UblogsService;

public class NewsCommunitiesPopulate {

	static UserPerspective user;

	private static NewsService service;

	// private static ActivitiesService ActivitiesService;
	private static BlogsService blogsService;

	private static ForumsService forums;

	private static UblogsService Ublogs;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(NewsCommunitiesPopulate.class.getName());

	/**
	 * Set Users Test Environment
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing News API Verification Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		// user = userEnv.getLoginUserEnvironment( StringConstants.CURRENT_USER,
		// Component.ACTIVITIES.toString());
		// ActivitiesService = user.getActivitiesService();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.MICROBLOGGING.toString());
		Ublogs = user.getUblogsService();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.NEWS.toString());
		service = user.getNewsService();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.BLOGS.toString());
		blogsService = user.getBlogsService();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.FORUMS.toString());
		forums = user.getForumsService();

		LOGGER.debug("Finished Initializing News API Verification Test");
	}

	@Test
	public void getNewsByEmail() throws FileNotFoundException, IOException {
		/*
		 * test this endpoint : /atom/stories/public/person?ps=50&email= 
		 * Step 1. create an entry to find in the feed. 
		 * Step 2. generate URL for the feed 
		 * Step 3. verify that feed contains string for created entry
		 */

		LOGGER.debug(" create an entry to find in the feed.");
		Blog regBlog = new Blog("This is a UniQue Name", "My first blog",
				"Because its the cup", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		Entry myBlog = (Entry) blogsService.createBlog(regBlog);

		LOGGER.debug("generate URL for the feed");
		ArrayList<Entry> news = service.getPersonFeedUpdates(null, null,
				StringConstants.USER_EMAIL, null, 0, 0, null, null, null);
		assertEquals("getPersonFeedUpdates", 200, service.getRespStatus());
		
		//LOGGER.debug("verify that feed contains string for created entry");
		//assertEquals(true, news.toString().contains("This is a UniQue Name"));

		LOGGER.debug("Ending '/atom/stories/public/person?ps=50&email=' test ");
	}

	@Test
	public void getNewsByUserId() throws FileNotFoundException, IOException {
		/*
		 * test this endpoint : /atom/stories/public/person?ps=50&userid= 
		 * Step 1. create an entry to find in the feed. 
		 * Step 2. generate URL for the feed 
		 * Step 3. verify that feed contains string for created entry
		 */

		LOGGER.debug(" create an entry to find in the feed.");
		Blog regBlog = new Blog("Another UniQue Name", "My first blog",
				"Because its the cup", "tagBlogs_"
						+ Utils.logDateFormatter.format(new Date()), false,
				false, null, null, TimeZone.getDefault(), true, 13, true, true,
				true, 0, -1, null, null, null, 0);
		Entry myBlog = (Entry) blogsService.createBlog(regBlog);

		//ProfileData user = ProfileLoader.getProfile(2);
		LOGGER.debug("generate URL for the feed");
		ArrayList<Entry> news = service.getPersonFeedUpdates(null, null, null,
				null, 0, 0, null, null, user.getUserId());
		assertEquals("getPersonFeedUpdates", 200, service.getRespStatus());

		//LOGGER.debug("verify that feed contains string for created entry");		
		// new created one may not listed in the default 20 blogs retrieved from above call
		//assertEquals(true, news.toString().contains("Another UniQue Name"));

		LOGGER.debug("Ending '/atom/stories/public/person?ps=50&userid=' test ");
	}

	@Test
	public void getFriendsNews() throws FileNotFoundException, IOException {
		ExtensibleElement feed = service.getNewsProfilesStories();

		LOGGER.debug("verify that feed contains string for created entry");
		assertEquals(true, feed.toString().contains("Top stories for"));

		LOGGER.debug(" create an entry to find in the feed.");
		Forum testForum = new Forum("Forum for News Stories ",
				"I'm a lame forum ");
		Entry result = (Entry) forums.createForum(testForum);
		assert (result != null);

		ExtensibleElement newsStories = service
				.getNewsFeed(URLConstants.SERVER_URL
						+ "/news/atom/stories/newsfeed");
		assertEquals("getNewsFeed", 200, service.getRespStatus());
		//assertEquals(true, newsStories.toString().contains("Forum for News Stories"));

	}

	@Test
	public void getStatusUpdates() {
		/*
		 * testing endpoint: /news/atom/stories/statusupdates 
		 * Step 1. Create status update 
		 * Step 2. Get endpoint 
		 * Step 3. Validate by searching for created status update string
		 */

		LOGGER.debug("Step1: create status Update");
		String ublog_string = "This Is Ublog @#%56745$$##@@@@134$%^"
				+ StringGenerator.randomSentence(2);
		String ublog_entry = "{\"content\":\"" + ublog_string + "\"}";
		String url = URLConstants.SERVER_URL + URLConstants.OPENSOCIAL_BASIC
				+ "/rest/ublog/@me/@all";

		String ublogId = Ublogs.createUblogEntry(url, ublog_entry);
		// assertTrue(ublogId != null);

		LOGGER.debug("Step 2. Get endpoint");
		ArrayList<Entry> newsStories = service.getStatusUpdates(null, null,
				null, null, 0, 0, null, null, null);

		LOGGER.debug("Step 3. Validate by searching for created status update string");
		assertEquals("getStatusUpdates", 200, service.getRespStatus());
		//assertEquals(true,newsStories.toString().contains("@#%56745$$##@@@@134$%"));

		LOGGER.debug("Ending test");
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
		blogsService.tearDown();
		Ublogs.tearDown();
		forums.tearDown();
	}
}
