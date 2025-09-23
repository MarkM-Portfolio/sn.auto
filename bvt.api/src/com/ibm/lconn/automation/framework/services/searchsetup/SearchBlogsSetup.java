/**

 * 
 */
package com.ibm.lconn.automation.framework.services.searchsetup;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.BlogsType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;

/**
 * @author David Yogodzinski & James Golin
 */
public class SearchBlogsSetup {

	static UserPerspective user;

	private static BlogsService service;

	protected final static Logger LOGGER = LoggerFactory
			.getLogger(SearchBlogsSetup.class.getName());

	@BeforeClass
	public static void setUp() throws Exception {

		LOGGER.debug("Start Initializing Search Blogs Data Setup Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		user = userEnv.getLoginUserEnvironment(StringConstants.CURRENT_USER,
				Component.BLOGS.toString());
		service = user.getBlogsService();
		assertTrue("Blogs service problem, service is NULL",service != null);
		LOGGER.debug("Finished Initializing Search Blogs Data Setup Test");
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
	 * @throws Exception
	 * 
	 * @see /blogs/{handle}/feed/tags/atom <br>
	 *      /blogs/{handle}/feed/blogtags/atom
	 * 
	 */
	@Test
	public void getBlogTags() {
		/**
		 * Step 1. Search the feed for blog entries that exist with the uniqueId
		 * and delete any entries in which the uniqueId is found in. 
		 * Step 2. Verify that there are no entries that have the uniqueId in the current feed. 
		 * Step 3. Create the entry with the uniqueId tag 
		 * Step 4. Return to this test in "SearchPopulate.java" after re-indexing has occurred. 
		 * Step 4.1: Get the document containing tags 
		 * Step 4.2: Verify the tag appears in the document that contains tags
		 */

		LOGGER.debug("Beginning Test: getBlogTags() ");
		LOGGER.debug("Step 1. Search the feed for blog entries that exist with the uniqueId and delete any entries in which the uniqueId is found in.");

		String uniqueTag = "beatles_taxman_z06";
		String secondUniqueTag = "beatles_pennylane_z06";

		// Find entries with the uniqueTag and delete them.
		Feed entriesFeed = (Feed) (service.getBlogsFeed(service.getURLString()
				+ "/roller-ui/rendering/feed/"
				+ service.getBlogsHomepageHandle() + "/entries/atom"));
		assertEquals("getEntriesFeed", 200, service.getRespStatus());
		
		ExtensibleElement eelement = service.getBlogFeed(service.getURLString()
				+ "/" + service.getBlogsHomepageHandle() + "/feed/blogs/atom");
		assertEquals("getBlogFeed", 200, service.getRespStatus());
		Feed blogsFeed = (Feed)eelement;

		// Blogs feed code block start
		List blogs = blogsFeed.getEntries();
		for (int i = 0; i < blogs.size(); i++) {
			Blog aBlog = new Blog((Entry) blogs.get(i));
			String originalId = aBlog.getId().toString();
			String id = originalId
					.substring(originalId.indexOf("blogs:blog-") + 11,
							originalId.length());
			String linkToBlog = service.getURLString() + "/"
					+ service.getBlogsHomepageHandle() + "/api/blogs/" + id;
			List tagList = aBlog.getTags();
			if (tagList.toString().contains(secondUniqueTag))
				service.deletePost(linkToBlog);
		}
		// Blogs feed code block ends
		// Entries feed code block starts
		List entries = entriesFeed.getEntries();
		for (int i = 0; i < entries.size(); i++) {
			BlogPost postOnBlog = new BlogPost((Entry) entries.get(i));
			String originalId = postOnBlog.getId().toString();
			String id = originalId.substring(originalId.indexOf("entry-") + 6,
					originalId.length());
			String linkToEntry = service.getURLString() + "/"
					+ service.getBlogsHomepageHandle() + "/api/entries/" + id;
			List tagList = postOnBlog.getTags();
			if (tagList.toString().contains(uniqueTag))
				service.deletePost(linkToEntry);
		}
		// Entries feed code block end

		LOGGER.debug("Step 2. Verify that there are no entries that have the uniqueId in the current feed.");
		// Refresh the feed, then search for entries with the uniqueTag again
		// and fail the test if they are still found
		blogsFeed = (Feed) (service.getBlogFeed(service.getURLString() + "/"
				+ service.getBlogsHomepageHandle() + "/feed/blogs/atom"));
		entriesFeed = (Feed) (service.getBlogsFeed(service.getURLString()
				+ "/roller-ui/rendering/feed/"
				+ service.getBlogsHomepageHandle() + "/entries/atom"));

		// Entries code block
		entries = entriesFeed.getEntries();
		for (int i = 0; i < entries.size(); i++) {
			BlogPost postOnBlog = new BlogPost((Entry) entries.get(i));
			List tagList = postOnBlog.getTags();
			if (tagList.toString().contains(uniqueTag)) {
				LOGGER.debug("Test results invalidated: Entries still exist with the uniqueTag.");
				assertTrue(false);
			}
		}
		// Blogs code block
		blogs = blogsFeed.getEntries();
		for (int i = 0; i < entries.size(); i++) {
			Blog postOnBlog = new Blog((Entry) entries.get(i));
			List tagList = postOnBlog.getTags();
			if (tagList.toString().contains(secondUniqueTag)) {
				LOGGER.debug("Test results invalidated: Blog(s) still exist with the secondUniqueTag.");
				assertTrue(false);
			}
		}
		LOGGER.debug("Step 3: Create the entry with the uniqueId tag");
		// Create the blog entry with the tag to search for
		Blog blogBlog = new Blog("The Beatles", "Songs",
				"Information about some of their songs", secondUniqueTag,
				false, false, null, null, TimeZone.getDefault(), true, 10,
				true, true, true, 0, -1, null, null, null, 0);

		ExtensibleElement eblog = service.createBlog(blogBlog);
		blogBlog = new Blog((Entry) eblog);

		BlogPost entryInTheBlog = new BlogPost(
				"Taxman",
				"The Beatles not-so-happy response to taxes people have to pay",
				uniqueTag, true, 100);
		service.createPost(blogBlog, entryInTheBlog);

		LOGGER.debug("Step 4: Halt test temporarily. Finishing test in \"SearchPopulate\" after indexing has occurred. \nINFO: Test Halted.");
	}

	@Test
	public void createBlogToSearch() {
		deleteTests();
		boolean found = false;

		// create a blog to search for later
		Blog regBlog = new Blog(StringConstants.SEARCH_BLOGS_NAME,
				"SearchThisBlog", "This blog is for searching", "carmen",
				false, false, null, null, TimeZone.getDefault(), true, 13,
				true, true, true, 0, -1, null, null, null, 0);
		service.createBlog(regBlog);

		// look for the blog in the public feed to make sure it is there for the
		// test
		ArrayList<Blog> publicFeed = service.getAllBlogs(null, null, 0, 50,
				null, null, null, null, null, null, null);
		for (int i = 0; i < publicFeed.size(); i++) {
			if (publicFeed.get(i).getTitle()
					.equals(StringConstants.SEARCH_BLOGS_NAME)) {
				LOGGER.debug("SUCCESS: Found created blog");
				assertTrue(true);
				found = true;
				break;
			}
		}
		if (!found) {
			LOGGER.debug("ERROR: Could not find created blog");
			assertTrue(false);
		}

	}

	public void deleteTests() {
		// delete the old test to prevent conflicts
		ArrayList<Blog> myBlogs = service.getMyBlogs(null, null, 0, 50, null,
				null, null, null, BlogsType.BLOG, null, null);
		for (Blog blog : myBlogs) {
			if (blog.getTitle().equals(StringConstants.SEARCH_BLOGS_NAME)) {
				service.deleteBlog(blog.getEditHref());
				LOGGER.debug("Search Setup - Deleted blog " + blog.getTitle());
				break;
			}
		}
	}

	@AfterClass
	public static void tearDown() {
		service.tearDown();
	}
}