package com.ibm.lconn.automation.framework.services.dogear;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;

/**
 * JUnit Tests via Connections API for Profiles Service
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class DogearPopulate {

	static UserPerspective user;

	private static DogearService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(DogearPopulate.class.getName());

	private static boolean useSSL = true;

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Dogear Data Population Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.DOGEAR.toString());
		service = user.getDogearService();

		LOGGER.debug("Finished Initializing Dogear Data Population Test");
	}

	@Test
	public void createBookmark() {
		Bookmark newBookmark = new Bookmark("Google",
				"This is a bookmark for the Google search engine.",
				"http://www.google.com", "tagDogear_"
						+ Utils.logDateFormatter.format(new Date()));
		Entry result = (Entry) service.createBookmark(newBookmark);
		assertTrue(result != null);
	}

	@Test
	public void getMyBookmarks() {
		/*
		 * Tests ability to get bookmarks Step 1. create new bookmarks Step 2.
		 * get my bookmarks Step 3. verify that the created bookmarks exist
		 */
		LOGGER.debug("Beginning Test: Get My Bookmarks");
		String dateCode = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: create new bookmarks");
		String[] testBookmarkURLs = { "http://www.testwebsite1.com" + dateCode,
				"http://www.testwebsite2.com" + dateCode,
				"http://www.testwebsite3.com" + dateCode };
		Bookmark testBookMark1 = new Bookmark("Test Bookmark 1", "test 1",
				testBookmarkURLs[0], null);
		// add a private bookmark
		Bookmark testBookMark2 = new Bookmark("Test Bookmark 2", "test 2",
				testBookmarkURLs[1], null);
		testBookMark2.setIsPrivate(true);
		Bookmark testBookMark3 = new Bookmark("Test Bookmark 3", "test 3",
				testBookmarkURLs[2], null);
		service.createBookmark(testBookMark1);
		service.createBookmark(testBookMark2);
		service.createBookmark(testBookMark3);

		LOGGER.debug("Step 2: get my bookmarks");
		Feed feed = (Feed) service.getMyBookmarks();
		// create an arraylist of only the URL's
		ArrayList<String> bookmarkURLs = new ArrayList<String>();
		for (Entry entry : feed.getEntries()) {
			bookmarkURLs.add(entry.getAlternateLink().getHref().toString());
		}

		LOGGER.debug("Step 3: verify that the created bookmarks exist");
		for (String url : testBookmarkURLs) {
			assertTrue(url + " should exist in bookmarks",
					bookmarkURLs.contains(url));
		}

		LOGGER.debug("Ending Test: Get My Bookmarks");
	}

	@Test
	public void getBookmarkDetails() throws Exception {
		// RTC 141481 Private bookmark content cannot be shown in homepage
		// bookmark widget
		LOGGER.debug("Beginning Test: Get bookmark details");

		LOGGER.debug("Step 1: prepare data - create new bookmarks");
		String url1 = "http://detail.testsite1";
		String url2 = "http://detail.testsite2";

		Bookmark testBookMark1 = new Bookmark("Test Bookmark details 1",
				"test details 1", url1, null);
		// add a private bookmark
		Bookmark testBookMark2 = new Bookmark("Test Bookmark details 2",
				"test details 2", url2, null);
		testBookMark2.setIsPrivate(true);
		service.createBookmark(testBookMark1);
		service.createBookmark(testBookMark2);

		LOGGER.debug("Step 2: get bookmark details");
		String atomUrl = service.getServiceURLString() + "/atom";

		Feed detail1 = (Feed) service.getFeedWithRedirect(atomUrl + "?for="
				+ URLEncoder.encode(url1, "utf-8"));
		assertEquals("response status", 200, service.getRespStatus());
		assertNotNull("response feed", detail1);
		assertEquals("number of entries", 1, detail1.getEntries().size());
		Feed detail2 = (Feed) service.getFeedWithRedirect(atomUrl + "?for="
				+ URLEncoder.encode(url2, "utf-8"));
		assertEquals("response status", 200, service.getRespStatus());
		assertNotNull("response feed", detail2);
		assertEquals("number of entries", 1, detail2.getEntries().size());
	}

	@Test
	public void updateBookmark() {
		Feed bookmarksFeed = (Feed) service.getMyBookmarks();
		for (Entry entry : bookmarksFeed.getEntries()) {
			Bookmark updatedBookmark = new Bookmark(entry);
			updatedBookmark.setTitle("API Modified Bookmark:" + Math.random());
			updatedBookmark.setLink("www.google.com/?" + Math.random());

			assertTrue(service.editBookmark(
					entry.getLink(StringConstants.REL_EDIT).getHref()
							.toString(), updatedBookmark) != null);
		}
	}

	@Test
	public void deleteBookmark() {
		/*
		 * Tests ability to Delete bookmarks Step 1. create a new bookmark,
		 * check Step 2. Delete bookmark, check Step 3. retrieve, check
		 */
		LOGGER.debug("Beggining test... deleteBookmark...");
		String dateCode = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1.Creating new...bookmark");

		Bookmark testBookmark = new Bookmark("Facebook",
				"This is a new bookmark for facebook site.",
				"http://www.facebook.com" + dateCode, null);
		Entry myBookmark = (Entry) service.createBookmark(testBookmark);
		assertTrue(myBookmark.getId() != null); // verifies that the bookmark
		// was created

		LOGGER.debug("Step 2.Deleting facebook bookmark");
		boolean bResult = service.deleteBookmark(myBookmark.getEditLink()
				.getHref().toString());
		assertTrue(bResult == true);

		LOGGER.debug("Step 3. Verify bookmark is deleted by attempting to retrieve it");
		myBookmark = (Entry) service.getBookmark(myBookmark.getEditLink()
				.getHref().toString());
		assertTrue(myBookmark.getId() == null); // verifies that target bookmark
		// was deleted

		LOGGER.debug("Ending deleteBookmark test...");

		return;

	}

	@Test
	public void listBookmarkTags() {
		/*
		 * Tests ability to list ALL bookmark tags Step 1. Create a new bookmark
		 * with tags Step 2. Get all bookmark tags Step 3. Verify the tags
		 * created exist in the list of all bookmark tags
		 */
		LOGGER.debug("Beginning test: List bookmark tags");
		String dateCode = Utils.logDateFormatter.format(new Date());
		String bookmarkTitle = "CNN" + dateCode;

		LOGGER.debug("Step 1: Create a bookmark with tags");
		String tagsString = "news_" + dateCode + " media_" + dateCode
				+ " articles_" + dateCode;
		Bookmark testBookmark = new Bookmark(bookmarkTitle,
				"This is a bookmark for CNN.", "http://www.cnn.com", tagsString);
		service.createBookmark(testBookmark);

		LOGGER.debug("Step 2: Get all bookmark tags");
		Categories categoryDoc = (Categories) service.getBookmarkTags(null,
				null, null, null, null, null, null, "http://www.cnn.com", null);
		List<Category> tags = categoryDoc.getCategories();
		List<String> tagNames = new ArrayList<String>();
		LOGGER.debug("tags number: " + tags.size());
		// Create an arraylist of the actual tag names
		for (Category tag : tags)
			tagNames.add(tag.getTerm());

		LOGGER.debug("Step 3: Verify the created tags exist in the list of all bookmark tags");
		for (String testTag : tagsString.split(" ")) {
			assertTrue(testTag + " tag should exist",
					tagNames.contains(testTag));
		}

		LOGGER.debug("Ending test: List bookmark tags");
	}

	@Test
	public void getPopularBookmarks() throws FileNotFoundException, IOException {
		/*
		 * Tests the ability to getPopularBookmarks Step 1: Create a new
		 * bookmark Step 2: Create the same bookmark as a different user (this
		 * makes it popular) Step 3: Get popular bookmarks Step 4: Verify that
		 * the created bookmark is in popular bookmarks
		 */
		LOGGER.debug("Beginning Test: Get popular bookmarks");
		String dateCode = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a new bookmark");
		String testURL = "http://popularsite" + dateCode + ".com";
		Bookmark popularBookmark = new Bookmark("A popular bookmark",
				"This is a popular bookmark", testURL, null);
		service.createBookmark(popularBookmark);

		LOGGER.debug("Step 2: Create the same bookmark as a different user");
		UserPerspective user2=null;
		try {
			user2 = new UserPerspective(5,
					Component.DOGEAR.toString(), useSSL);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DogearService service2 = user2.getDogearService();
		service2.createBookmark(popularBookmark);

		LOGGER.debug("Step 3: Get popular bookmarks");
		Feed feed = (Feed) service.getPopularBookmarks(null, 1, 20, null);
		assertTrue(feed != null);

		LOGGER.debug("Step 4: Verify that the created bookmark is in popular bookmarks");
		boolean foundBookmark = false;
		for (Entry entry : feed.getEntries()) {
			if (entry.getAlternateLink().getHref().toString().equals(testURL))
				foundBookmark = true;
		}
		assertTrue("Verify the popular bookmark is found ", foundBookmark);

		LOGGER.debug("Ending test: Get popular bookmarks");
	}

	@Test
	public void getSentBookmarks() {
		/*
		 * Tests the ability to get bookmarks you've notified others about Step
		 * 1: Create a bookmark Step 2: Notify another user of the bookmark Step
		 * 3: Get sent bookmarks Step 4: Verify that created bookmark is in sent
		 * bookmarks
		 */
		LOGGER.debug("Beginning test: Get sent bookmarks");
		String dateCode = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a bookmark");
		Bookmark testBookmark = new Bookmark("SentBookmark" + dateCode,
				"test bookmark", "http://test" + dateCode + ".com", null);
		Entry result = (Entry) service.createBookmark(testBookmark);

		LOGGER.debug("Step 2: Notify another user of the bookmark");
		sendBookmark(service, result, StringConstants.RANDOM1_USER_EMAIL,
				"Hey check out this website");

		LOGGER.debug("Step 3: Get sent bookmarks");
		Feed feed = (Feed) service.getSentBookmarks(null, 1, 10);

		LOGGER.debug("Step 4: Verify that created bookmark is in sent bookmarks");
		boolean foundBookmark = false;
		for (Entry e : feed.getEntries()) {
			if (e.getTitle().equals(testBookmark.getTitle()))
				foundBookmark = true;
		}
		assertTrue(foundBookmark);

		LOGGER.debug("Ending test: Get sent bookmarks");
	}

	@Test
	public void getNotifiedBookmarks() throws FileNotFoundException,
			IOException {
		/*
		 * Tests the ability to get bookmarks you've been notified about 
		 * Step 1: Create a bookmark a user 
		 * Step 2: Notify another user of that bookmark
		 * Step 3: Get notified bookmarks as the receiving user 
		 * Step 4: Verify that the bookmark created is in notified bookmarks
		 */
		LOGGER.debug("Beginning Test: Get notified bookmarks");
		String dateCode = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1: Create a bookmark as a user");
		// login as another user
		UserPerspective user2=null;
		try {
			user2 = new UserPerspective(5,
					Component.DOGEAR.toString(), useSSL);
		} catch (LCServiceException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		DogearService service2 = user2.getDogearService();
		Bookmark testBookmark = new Bookmark("NotifiedBookmark" + dateCode,
				"test content", "http://notifytest" + dateCode + ".com", null);
		Entry result = (Entry) service2.createBookmark(testBookmark);

		LOGGER.debug("Step 2: Notify another user of that bookmark");
		// create a custom entry
		sendBookmark(service2, result, StringConstants.USER_EMAIL,
				"This is just what we need to take over the world. Muhahaha.");

		LOGGER.debug("Step 3: Get notified bookmarks as the receiving user");
		Feed notifiedFeed = (Feed) service.getNotifiedBookmarks(null, 1, 10);

		LOGGER.debug("Step 4: Verify that the bookmark created is in notified bookmarks");
		boolean foundBookmark = false;
		for (Entry e : notifiedFeed.getEntries()) {
			if (e.getTitle().equals(testBookmark.getTitle()))
				foundBookmark = true;
		}
		assertTrue(foundBookmark);

		LOGGER.debug("Ending test: Get notified bookmarks");
	}

	/**
	 * Function to create a custom entry to notify another user of a bookmark
	 * Precondition: the bookmark has been created and the the result entry from
	 * createBookmark has been acquired
	 */
	public ExtensibleElement sendBookmark(DogearService service,
			Entry bookmarkResult, String userEmailToNotify, String message) {
		Factory factory = new Abdera().getFactory();
		Entry entry = factory.newEntry();
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/type",
				"notification", null);

		Element recipientEmail = entry.addSimpleExtension(
				StringConstants.SNX_RECIPIENT_EMAIL, null);
		recipientEmail.setAttributeValue("email", userEmailToNotify);

		Element linkID = entry.addSimpleExtension(StringConstants.SNX_LINK,
				null);
		String ID = bookmarkResult.getId().toString();
		linkID.setAttributeValue("linkid",
				ID.substring(ID.indexOf("link:") + 5));

		entry.setContent(message);
		return service.sendBookmark(entry);
	}

	// RTC #97662
	@Test
	public void dupBookmarks() {
		/*
		 * Create again, replace the exist bookmarks Step 1. create new
		 * bookmarks Step 2. create same bookmarks with diff title Step 3.
		 * verify that the created bookmarks is replaced
		 */
		LOGGER.debug("Beginning Test: dup Bookmarks test");
		String dateCode = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("Step 1a: create new bookmarks");
		String testBookmarkURLs = "http://www.testwebsite1.com" + dateCode;
		Bookmark testBookMark1 = new Bookmark("dup Bookmark 1", "test 1",
				testBookmarkURLs, null);
		service.createBookmark(testBookMark1);
		LOGGER.debug("Step 1b: verify the bookmarks");
		Feed feed = (Feed) service.getMyBookmarks();
		for (Entry entry : feed.getEntries()) {
			if (testBookmarkURLs.contains(entry.getAlternateLink().getHref()
					.toString().trim())) {
				assertEquals("dup Bookmark 1", entry.getTitle());
				break;
			} else {
				assertTrue("Can't find Bookmark 1", false);
			}
		}

		LOGGER.debug("Step 2: create same bookmarks with diff title");
		Bookmark testBookMark2 = new Bookmark("dup Bookmark 2", "test 2",
				testBookmarkURLs, null);
		service.createBookmark(testBookMark2);

		LOGGER.debug("Step 3: verify that the created bookmarks is replaced");
		feed = (Feed) service.getMyBookmarks();
		for (Entry entry : feed.getEntries()) {
			if (testBookmarkURLs.contains(entry.getAlternateLink().getHref()
					.toString().trim())) {
				assertEquals("dup Bookmark 2", entry.getTitle());
				break;
			} else {
				assertTrue("Can't find dup Bookmark 2", false);
			}
		}

		LOGGER.debug("Ending Test: dup Bookmarks test");
	}
	
	 @Test
	    public void testCorsPreflight_Atom() throws Exception {
	    	
	    	// Test requires CORS configured to trust 'fakedomain.org' - only true for on prem pool
	    	if (StringConstants.DEPLOYMENT_TYPE != DeploymentType.ON_PREMISE) {
	    		return;
	    	}
	    	
	    	String url = service.getServiceURLString() + URLConstants.DOGEAR_SEARCH;
	    	
	    	int[] statuses = service.makeCorsPreflightRequests(url);

	    	assertEquals("Untrusted CORS request should fail", 403, statuses[0]);
	    	assertEquals("Trusted CORS request should succeed", 200, statuses[1]);
	    }

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}