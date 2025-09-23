package com.ibm.conn.auto.lcapi.test;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;

public class APIBlogsTest extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(APIBlogsTest.class);
	private TestConfigCustom cfg;	
	private User testUser, testUser2;
	private String testURL;
	
	private static Abdera abdera;
	private static AbderaClient client;
	private static ServiceConfig config;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {

		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
		testURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());

		// Initialize Abdera
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		
		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();
		
		// Get service config for server, assert that it was retrieved and contains the activities service information
		config = new ServiceConfig(client, testURL, true);
		
		ServiceEntry blogs = config.getService("blogs");
		assert(blogs != null);

		Utils.addServiceAdminCredentials(blogs, client);
				
	}

	@Test (groups = {"apitest"})
	public void deleteBlogPost(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIBlogsHandler apiHandler = new APIBlogsHandler(testURL, testUser.getUid(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		BaseBlog baseBlog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
									.tags("Tag for "+testName  + Helper.genDateBasedRand())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost baseBlogPost = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand())
													.blogParent(baseBlog)
													.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
													.content("Test description for testcase " + testName)
													.build();
		
		log.info("INFO: Create blog for blog entry to attach to");
		Blog blog = apiHandler.createBlog(baseBlog);
		
		log.info("INFO: Create blog entry");
		BlogPost blogPost = apiHandler.createBlogEntry(baseBlogPost, blog);
		
		log.info("INFO: Delete blog entry");
		boolean deleted = apiHandler.deleteBlogPost(blogPost);
		
		assert deleted == true:"Blog post deletion failed";
				
	}

	@Test (groups = {"apitest"})
	public void addBlogPostMentionsComment(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIBlogsHandler apiHandler = new APIBlogsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		
		BaseBlog baseBlog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
									.tags("Tag for "+testName  + Helper.genDateBasedRand())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost baseBlogPost = new BaseBlogPost.Builder(testName  + Helper.genDateBasedRandVal())
													.blogParent(baseBlog)
													.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
													.content(Data.getData().commonDescription + Helper.genDateBasedRand())
													.build();

		String beforeMentionsText = Helper.genDateBasedRandVal();
		String afterMentionsText = Helper.genMonthDateBasedRandVal();

		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create blog for blog entry to attach to");
		Blog blog = apiHandler.createBlog(baseBlog);
		
		log.info("INFO: " + testUser.getDisplayName() + " create blog entry");
		BlogPost blogPost = apiHandler.createBlogEntry(baseBlogPost, blog);
		
		log.info("INFO: " + testUser.getDisplayName() + " create a blog entry comment with a mentions to " + testUser2.getDisplayName());
		BlogComment blogComment = apiHandler.addBlogCommentMentionAPI(blogPost, mentions);
		
		assert blogComment != null: "Creation of Blog post comment with mentions failed";
				
	}

	@Test (groups = {"apitest"})
	public void addBlogPostMentions(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIBlogsHandler apiHandler = new APIBlogsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		APIProfilesHandler profilesAPI = new APIProfilesHandler(testURL, testUser2.getEmail(), testUser2.getPassword());
		
		BaseBlog baseBlog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
									.tags(Data.getData().commonTag + Helper.genDateBasedRand())
									.description(Data.getData().commonDescription + Helper.genDateBasedRand())
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		String beforeMentionsText = Helper.genDateBasedRandVal();
		String afterMentionsText = Helper.genMonthDateBasedRandVal();

		Mentions mentions = new Mentions.Builder(testUser2, profilesAPI.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		
		log.info("INFO: " + testUser.getDisplayName() + " create blog for blog entry to attach to");
		Blog blog = apiHandler.createBlog(baseBlog);
		
		log.info("INFO: " + testUser.getDisplayName() + " create blog entry with a mentions to " + testUser2.getDisplayName());
		BlogPost blogPost = apiHandler.addMention_BlogEntryAPI(blog, mentions);
		
		assert blogPost != null: "Creation of Blog post with mention failed";
				
	}
	
	@Test (groups = {"apitest"})
	public void editBlogPostComment(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIBlogsHandler apiHandler = new APIBlogsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		BaseBlog baseBlog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
									.tags("Tag for "+testName  + Helper.genDateBasedRand())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost baseBlogPost = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand())
													.blogParent(baseBlog)
													.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
													.content("Test description for testcase " + testName)
													.build();
		
		log.info("INFO: Create blog for blog entry to attach to");
		Blog blog = apiHandler.createBlog(baseBlog);
		
		log.info("INFO: Create blog entry");
		BlogPost blogPost = apiHandler.createBlogEntry(baseBlogPost, blog);
		
		String comment = Data.getData().StatusComment + Helper.genMonthDateBasedRandVal();
		
		log.info("INFO " + testUser.getDisplayName() + " adding Comment to Blog Entry");
		BlogComment blogComment = apiHandler.createBlogComment(comment, blogPost);
		
		String blogCommentEdit = "EDITED";

		log.info("INFO " + testUser.getDisplayName() + " editing Comment to Blog Entry");
		String edited = apiHandler.editComment(blogComment, blogCommentEdit);
		
		assert edited.equals(blogCommentEdit) == true:"Blog post comment edit failed";
				
	}

	@Test (groups = {"apitest"})
	public void unlikeBlogPost(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIBlogsHandler apiHandler = new APIBlogsHandler(testURL, testUser.getEmail(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		BaseBlog baseBlog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
									.tags("Tag for "+testName  + Helper.genDateBasedRand())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		BaseBlogPost baseBlogPost = new BaseBlogPost.Builder("BlogEntry"  + Helper.genDateBasedRand())
													.blogParent(baseBlog)
													.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
													.content("Test description for testcase " + testName)
													.build();
		
		log.info("INFO: Create blog for blog entry to attach to");
		Blog blog = apiHandler.createBlog(baseBlog);
		
		log.info("INFO: Create blog entry");
		BlogPost blogPost = apiHandler.createBlogEntry(baseBlogPost, blog);
		
		log.info("INFO " + testUser.getDisplayName() + " liking Blog Entry");
		apiHandler.like(blogPost);

		log.info("INFO " + testUser.getDisplayName() + " unliking Blog Entry");
		boolean deleted = apiHandler.unlike(blogPost);
		
		assert deleted == true:"Blog post unlike action failed";
				
	}
	
	@Test (groups = {"apitest"})
	public void flagBlogEntry(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIBlogsHandler apiHandler = new APIBlogsHandler(testURL, testUser.getUid(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		BaseBlog baseBlog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
									.tags("Tag for "+testName  + Helper.genDateBasedRand())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		String entryTitle = "BlogEntry"  + Helper.genDateBasedRand();
		BaseBlogPost baseBlogPost = new BaseBlogPost.Builder(entryTitle)
													.blogParent(baseBlog)
													.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
													.content("Test description for testcase " + testName)
													.build();
		
		log.info("INFO: Create blog for blog entry to attach to");
		Blog blog = apiHandler.createBlog(baseBlog);
		
		log.info("INFO: Create blog entry");
		apiHandler.createBlogEntry(baseBlogPost, blog);
		
		log.info("INFO: Flag blog entry");
		boolean flag = apiHandler.flagBlogEntry(entryTitle);
		
		assert flag == true:"Blog post flagging failed";
	}
	
	@Test (groups = {"apitest"})
	public void flagBlogComments(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		//Instantiate APIHandler
		APIBlogsHandler apiHandler = new APIBlogsHandler(testURL, testUser.getUid(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());

		BaseBlog baseBlog = new BaseBlog.Builder(testName + Helper.genDateBasedRand(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
									.tags("Tag for "+testName  + Helper.genDateBasedRand())
									.description("Test description for testcase " + testName)
									.timeZone(Time_Zone.Europe_London)
									.theme(Theme.Blog_with_Bookmarks)
									.build();
		
		String entryTitle = "BlogEntry"  + Helper.genDateBasedRand();
		BaseBlogPost baseBlogPost = new BaseBlogPost.Builder(entryTitle)
													.blogParent(baseBlog)
													.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
													.content("Test description for testcase " + testName)
													.build();
		
		log.info("INFO: Create blog for blog entry to attach to");
		Blog blog = apiHandler.createBlog(baseBlog);
		
		log.info("INFO: Create blog entry");
		BlogPost blogPost = apiHandler.createBlogEntry(baseBlogPost, blog);
		
		log.info("INFO: Add blog comment");
		String blogCommentContent = "blogComment"+Helper.genDateBasedRand();
		apiHandler.createBlogComment(blogCommentContent, blogPost);
		
		log.info("INFO: Flag blog comment");
		boolean flag = apiHandler.flagBlogComments(entryTitle);
		
		assert flag == true:"Blog comments flagging failed";
				
	}
}
