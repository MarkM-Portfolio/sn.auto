package com.ibm.conn.auto.tests.GDPR;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Time_Zone;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.BlogRole;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.menu.BlogSettings_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.BlogsUI.EditVia;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

@Deprecated //The GDPR component is obsolete now, hence this class has been deprecated
public class Blogs_GDPR_DataPop extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Blogs_GDPR_DataPop.class);
	private TestConfigCustom cfg;
	private CommunitiesUI commUI;	
	private BlogsUI blogsUI;
	private APICommunitiesHandler apiCommOwner,apiCommOwner2;
	private String serverURL;
	private User testUser1, testUser2;
	private boolean isOnPremise;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		blogsUI = BlogsUI.getGui(cfg.getProductName(), driver);

		//Load Users
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
						
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
				
		URLConstants.setServerURL(serverURL);
		
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		apiCommOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiCommOwner2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: Stand-alone Blog - Add Comment to Blog Entry</li>
	*<li><B>Step:</B> UserA creates a Blog</li>
	*<li><B>Step:</B> UserA creates a Blog entry</li>
	*<li><B>Step:</B> UserA adds a comment to the blog entry</li>
	*</ul>
	*Notes: This test is not supported on the cloud. No stand-alone Blog on cloud.
	*/
	@Test(groups = {"regression"}, enabled=false)
	public void addCommentStandaloneBlogEntry(){
		
		if(isOnPremise){
		String testName = blogsUI.startTest();
		
		BaseBlog blog = new BaseBlog.Builder("GDPR: addCommentToEntry " + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.description("GDPR Blog data pop - " + testName)
									.timeZone(Time_Zone.America_New_York)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("GDPR: BlogEntry " + Helper.genDateBasedRand())
		                                         .blogParent(blog)
												 .content("GDPR blog entry data pop - " + testName)
												 .build();
		
		BaseBlogComment comment = new BaseBlogComment.Builder("GDPR: comment for test: " + testName).build();
				
		createStandaloneBlogEntry (testUser1, blog, blogEntry);
		
		log.info("INFO: Add a new comment to the entry A");
		comment.create(blogsUI);
		
		log.info("INFO: Logout as UserA " + testUser1.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
		
		blogsUI.endTest();
		
		}else {
			log.info("INFO: Cloud environment does not support standalone Blogs - skipping this test");
		}
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: Community Blog: Add a Blog Entry</li>
	*<li><B>Step:</B> UserA creates a Public community via the API</li>
	*<li><B>Step:</B> UserA adds the Blogs widget</li>
	*<li><B>Step:</B> UserA creates a Blog Entry</li>
	*<li><B>Step:</B> UserA adds a comment to the Blog Entry</li>
	*</ul>
	*/ 
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void addCommentToCommBlogEntry(){
		
		String testName = commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("GDPR data pop - Blogs widget with an entry & comment added to entry. ")
                                                   .build();
					
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("Blog entry " + Helper.genDateBasedRandVal())
														 .content("GDPR data pop - Test blog entry ")
														 .build();	
			
		BaseBlogComment comment = new BaseBlogComment.Builder("GDPR: comment for " + testName).build();

		
		createCommunityAndBlogEntry(community, blogEntry, apiCommOwner, testUser1);

		log.info("INFO: Add a new comment to the entry");
		comment.create(blogsUI);
		
		log.info("INFO: Logout as UserA " + testUser1.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
		
		commUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: Community Blogs: Edit Blog Entry</li>
	*<li><B>Step:</B> UserA creates a community</li>
	*<li><B>Step:</B> UserA adds the Blog widget</li>
	*<li><B>Step:</B> UserA creates a Blog Entry</li>
	*<li><B>Step:</B> UserA edits the Blog entry title</li>
	*</ul>
	*/
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void editCommBlogsEntry(){
		
		String testName = commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("GDPR data pop - Public community with Blogs app. ")
                                                   .build();
					
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("GDPR Blog Entry " + Helper.genDateBasedRandVal())
				   								 .content("GDPR data pop - Test blog entry")
				   								 .build();
				
		
		createCommunityAndBlogEntry(community, blogEntry, apiCommOwner, testUser1);
						
		editBlogEntryTitle(blogEntry);
		
		log.info("INFO: Logout as UserA " + testUser1.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
		
		commUI.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: Standalone Blogs: Edit a Blog Entry</li>
	*<li><B>Step:</B> UserA creates a Blog</li>
	*<li><B>Step:</B> UserA creates a Blog Entry</li>
	*<li><B>Step:</B> UserA edits the Blog Entry</li>
	*</ul>
	*Notes: This test is not supported on the cloud. No standalone Blog on cloud.
	*/
	@Test(groups = {"regression"}, enabled=false)
	public void editStandaloneBlogEntry(){
		
		if(isOnPremise){
		String testName = blogsUI.startTest();
		
		BaseBlog blog = new BaseBlog.Builder("GDPR: " + testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.description("GDPR data pop - Description for testcase " + testName)
									.timeZone(Time_Zone.America_New_York)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("GDPR: BlogEntry " + Helper.genDateBasedRand())
		                                         .blogParent(blog)
												 .content("GDPR data pop - Entry description for testcase " + testName)
												 .build();
	
		
		createStandaloneBlogEntry (testUser1, blog, blogEntry);
						
		editBlogEntryTitle(blogEntry);
		
		log.info("INFO: Log out UserA: " + testUser1.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
					
		blogsUI.endTest();
		
		}else {
			log.info("INFO: Cloud environment does not support standalone Blogs - skipping this test");
		}
		
	}	
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: Standalone Blogs: Follow Blog</li>
	*<li><B>Step:</B> UserB creates a Blog</li>
	*<li><B>Step:</B> Logout as UserB, login as UserA</li>
	*<li><B>Step:</B> UserA follows the Blog</li>
	*</ul>
	*Notes: This test is not supported on the cloud. No standalone Blog on cloud.
	*/
	@Test(groups = {"regression"}, enabled=false)
	public void followStandaloneBlog(){
		
		if(isOnPremise){
		String testName = blogsUI.startTest();
		
		BaseBlog blog = new BaseBlog.Builder("GDPR: " + testName + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.description("GDPR data pop - Description for Follow Blog test " + testName)
									.timeZone(Time_Zone.America_New_York)
									.build();
		
		log.info("INFO: Log into Blogs as UserB: " + testUser2.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentBlogs);
		blogsUI.login(testUser2);

		log.info("INFO: Create a new Blog");
		blog.create(blogsUI);

		log.info("INFO: Open Blog");
		blogsUI.clickLinkWithJavascript("link=" + blog.getName());
		
		log.info("INFO: Click on the Follow this Blog link");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.followThisBlogBtn);
		
		log.info("INFO: Verify the follow confirmation message displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().followThisBlogMsg),
				"ERROR: The follow this blog confirmation message does not appear");
		
		log.info("INFO: Log out UserB: " + testUser2.getDisplayName());
		blogsUI.logout();
		
		log.info("INFO: Log into Blogs as UserA: " + testUser1.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentBlogs, true);
		blogsUI.login(testUser1);
		
		log.info("INFO: UserA follows the blog");
		this.followBlog(blog);

		log.info("INFO: Log out UserA: " + testUser1.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
		
		blogsUI.endTest();
		
		}else {
			log.info("INFO: Cloud environment does not support standalone Blogs - skipping this test");
		}
		
	}	
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: Stand-alone Blogs: Edit Entry & Add Comment</li>
	*<li><B>Step:</B> UserA creates a Blog & adds UserB as an Owner</li>
	*<li><B>Step:</B> UserA edits the Blog</li>
	*<li><B>Step:</B> UserA adds a Blog Entry</li>
	*<li><B>Step:</B> UserB edits the entry</li>
	*<li><B>Step:</B> UserB adds a comment to the entry</li>
	*<li><B>Step:</B> UserA adds a reply to UserB's comment</li>
	*</ul>
	*Notes: This test is not supported on the cloud. No stand-alone Blog on cloud.
	*/
	@Test(groups = {"regression"}, enabled=false)
	public void standaloneBlogEditEntryAddReplyAndComment(){
		
		if(isOnPremise){
		String testName = blogsUI.startTest();
		
		Member memberOwner = new Member(BlogRole.OWNER, testUser2);
		
		BaseBlog blog = new BaseBlog.Builder("GDPR: Standalone Blog" + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.description("GDPR data pop - Description for testcase " + testName)
									.timeZone(Time_Zone.America_New_York)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		BaseBlogPost blogEntry1 = new BaseBlogPost.Builder("GDPR: BlogEntry " + Helper.genDateBasedRand()).blogParent(blog)
												 .content("GDPR data pop - Entry description for testcase " + testName)
												 .build();
		
		BaseBlogComment comment = new BaseBlogComment.Builder(Data.getData().BlogsCommentText)
		                                             .build();
		
		BaseBlogComment reply = new BaseBlogComment.Builder(Data.getData().commonComment)
                                                     .build();
						
				
		createStandaloneBlogAddOwner(testUser1, blog, memberOwner);
		
		log.info("INFO: Select Manage Blog");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.blogsSettings);

		log.info("INFO: Edit Blog: add tags and edit the description");		
		blog.setTags(Data.getData().MultiFeedsTag);
		blog.setDescription(Data.getData().editedData + blog.getDescription());
		blog.edit(blogsUI, EditVia.MANAGEBLOG);
		
		log.info("INFO: Click on the Public Blogs tab");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.PublicBlogs);
		
		log.info("INFO: Click on the Blogs Listing link");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.BlogsPublicListing);

		log.info("INFO: Open the Blog");
		blogsUI.clickLinkWithJavascript("link=" + blog.getName());
		
		log.info("INFO: Select New Entry button");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.blogsNewEntryMenuItem);
		
		log.info("INFO: Add a new entry to the Blog");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntry);
		blogEntry1.create(blogsUI);
		
		log.info("INFO: Log out of Blogs as UserA " + testUser1.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
		
		log.info("INFO: Log into Blogs as UserB " + testUser2.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentBlogs);
		blogsUI.login(testUser2);
		
		log.info("INFO: Click on the Public Blogs link");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.PublicBlogs);
		
		log.info("INFO: Open the Blogs Entry");
		blogsUI.clickLinkWithJavascript("link=" + blogEntry1.getTitle());
				
		editBlogEntryTags();
		
		log.info("INFO: UserB " + testUser2.getDisplayName() + " adds a comment to the entry");
		blogsUI.createBlogComment(comment);
		
		log.info("INFO: Log out of Blogs as UserB " + testUser2.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
		
		log.info("INFO: Log into Blogs as UserA " + testUser1.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentBlogs);
		blogsUI.login(testUser1);
		
		log.info("INFO: Click on the Public Blogs link");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.PublicBlogs);
		
		log.info("INFO: Open the Blogs Entry");
		blogsUI.clickLinkWithJavascript("link=" + blogEntry1.getTitle());
				
		log.info("INFO: UserA " + testUser1.getDisplayName() + " replies to UserB's " + testUser2.getDisplayName() + " comment");
       	blogsUI.createBlogReply(reply);
       	
       	log.info("INFO: Log out UserA: " + testUser1.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
				
		blogsUI.endTest();
		
		}else {
			log.info("INFO: Cloud environment does not support standalone Blogs - skipping this test");
		}
	}
		
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: Stand-alone Blogs: Edit & Like Entry, Add Comment</li>
	*<li><B>Step:</B> UserB creates a Blog & adds UserA as Owner</li>
	*<li><B>Step:</B> UserB adds a Blog Entry</li>
	*<li><B>Step:</B> UserA 'likes' the entry</li>
	*<li><B>Step:</B> UserA edits the entry</li>
	*<li><B>Step:</B> UserA adds a comment to the entry</li>
	*</ul>
	*Notes: This test is not supported on the cloud. No stand-alone Blog on cloud.
	*/
	@Test(groups = {"regression"}, enabled=false)
	public void standaloneBlogEditAndLikeEntryAddComment(){
		
		if(isOnPremise){
		String testName = blogsUI.startTest();
		
		Member memberOwner = new Member(BlogRole.OWNER, testUser1);
		
		BaseBlog blog = new BaseBlog.Builder("GDPR: Standalone Blog " + Helper.genDateBasedRandVal(), Data.getData().BlogsAddress1 + Helper.genDateBasedRandVal())
									.description("GDPR data pop - Description for testcase " + testName)
									.timeZone(Time_Zone.America_New_York)
									.theme(Theme.Blog_with_Bookmarks)
									.build();

		BaseBlogPost blogEntry = new BaseBlogPost.Builder("GDPR Blog Entry " + Helper.genDateBasedRand())
		                                         .blogParent(blog)
												 .content("GDPR data pop - Entry description for testcase " + testName)
												 .build();
		
		BaseBlogComment comment = new BaseBlogComment.Builder(Data.getData().BlogsCommentText)
		                                             .build();
		
				
		createStandaloneBlogAddOwner(testUser2, blog, memberOwner);
		
		log.info("INFO: Select New Entry button");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.blogsNewEntryMenuItem);
		
		log.info("INFO: Add a new entry to the Blog");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntry);
		blogEntry.create(blogsUI);
		
		log.info("INFO: Log out of Blogs as UserB " + testUser2.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
		
		log.info("INFO: Log into Blogs as UserA " + testUser1.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentBlogs);
		blogsUI.login(testUser1);
		
		log.info("INFO: Click on the Public Blogs link");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.PublicBlogs);
		
		log.info("INFO: Open the Blogs Entry");
		blogsUI.clickLinkWithJavascript("link=" + blogEntry.getTitle());		
		
		log.info("INFO: Like the Entry");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEntryLike);
				
		editBlogEntryTags();
		
		log.info("INFO: UserA " + testUser1.getDisplayName() + " adds a comment to the entry");
		blogsUI.createBlogComment(comment);
		
		log.info("INFO: Log out UserA: " + testUser1.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
					
        blogsUI.endTest();
        
		}else {
			log.info("INFO: Cloud environment does not support standalone Blogs - skipping this test");
		}
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: Community Blog: Like Entry & Reply to an Entry Comment</li>
	*<li><B>Step:</B> UserA creates a community & adds UserB as a member</li>
	*<li><B>Step:</B> UserA adds the Blogs widget</li>
	*<li><B>Step:</B> UserA creates a Blog Entry</li>
	*<li><B>Step:</B> UserA 'Likes' the entry</li>
	*<li><B>Step:</B> UserB adds a comment to the entry</li>
	*<li><B>Step:</B> UserA adds a reply to UserB's comment</li>
	*</ul>
	*/ 
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void commBlogLikeEntryReplyToComment(){
		
		String testName = commUI.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .addMember(member)
                                                   .description("GDPR data pop - Community with Blogs widget added. ")
                                                   .build();
					
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("GDPR: Blog entry " + Helper.genDateBasedRandVal())
														 .content("GDPR data pop - Blog entry is 'liked' and has a comment & reply to comment ")
														 .build();	
		
		BaseBlogComment comment = new BaseBlogComment.Builder("GDPR: comment for " + testName)
		                                             .build();
		
		BaseBlogComment reply = new BaseBlogComment.Builder("GDPR: reply to comment")
                                                   .build();

		
		createCommunityAndBlogEntry(community, blogEntry, apiCommOwner, testUser1);
		
		log.info("INFO: Like the Entry");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEntryLike);
		
		log.info("INFO: Log out of Communities as UserA " + testUser1.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
		
		log.info("INFO: Log into Communities as UserB " + testUser2.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentCommunities);
		blogsUI.login(testUser2);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);	

		log.info("INFO: Select Blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.BLOG.select(commUI);
		
		log.info("INFO: Open the Blogs Entry");
		blogsUI.clickLinkWithJavascript("link=" + blogEntry.getTitle());	
		
		log.info("INFO: UserB " + testUser2.getDisplayName() + " adds a comment to the entry");
		blogsUI.createBlogComment(comment);
		
		log.info("INFO: Log out of Communities as UserB " + testUser2.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
		
		log.info("INFO: Log into Communities as UserA " + testUser1.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentCommunities);
		blogsUI.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);	

		log.info("INFO: Select Blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.BLOG.select(commUI);
		
		log.info("INFO: Open the Blogs Entry");
		blogsUI.clickLinkWithJavascript("link=" + blogEntry.getTitle());
				
		log.info("INFO: UserA " + testUser1.getDisplayName() + " replies to UserB's " + testUser2.getDisplayName() + " comment");
       	blogsUI.createBlogReply(reply);
       	
       	log.info("INFO: Log out UserA: " + testUser1.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);

		commUI.endTest();
	}

	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population: Community Blog: Edit Entry & Comment on the Entry</li>
	*<li><B>Step:</B> UserB creates a community & adds UserA as an Owner</li>
	*<li><B>Step:</B> UserB adds the Blogs widget</li>
	*<li><B>Step:</B> UserB creates a Blog Entry</li>
	*<li><B>Step:</B> UserA edits the entry</li>
	*<li><B>Step:</B> UserA adds a comment to the entry</li>
	*</ul>
	*/ 
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void commBlogEditEntryAddComment(){
		
		String testName = commUI.startTest();
		
		Member member = new Member(CommunityRole.OWNERS, testUser1);
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .addMember(member)
                                                   .description("GDPR data pop - Community with Blogs widget added. ")
                                                   .build();
					
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("GDPR: Blog entry " + Helper.genDateBasedRandVal())
														 .content("GDPR data pop - UserA will edit the Blog entry & add a comment ")
														 .build();	
		
		BaseBlogComment comment = new BaseBlogComment.Builder("GDPR: comment for test: " + testName)
		                                             .build();
		
				
		createCommunityAndBlogEntry(community, blogEntry, apiCommOwner2, testUser2);
		
		log.info("INFO: Log out of Communities as UserB " + testUser2.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
		
		log.info("INFO: Log into Communities as UserA " + testUser1.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentCommunities);
		blogsUI.login(testUser1);
		
		log.info("INFO: If the Guided Tour dialog displays, close it");
		commUI.closeGuidedTourPopup();
				
		log.info("INFO: Open the Community");	
		commUI.clickLinkWithJavascript("link=" + community.getName());

		log.info("INFO: Select Blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.BLOG.select(commUI);
		
		log.info("INFO: Open the Blogs Entry");
		blogsUI.clickLinkWithJavascript("link=" + blogEntry.getTitle());
		
		editBlogEntryTitle(blogEntry);
		
		log.info("INFO: UserA " + testUser1.getDisplayName() + " adds a comment to the entry");
		blogsUI.createBlogComment(comment);		
		
		log.info("INFO: Log out UserA: " + testUser1.getDisplayName());
		blogsUI.logout();
		blogsUI.close(cfg);
		
		commUI.endTest();
	}
	
	/**
	 * The followBlog method will follow the desired blog: 
	 * @param blog - the blog to be followed
	 */		

	private void followBlog(BaseBlog blog) {
		log.info("INFO: Click on the Public Blogs link");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.PublicBlogs);

		log.info("INFO: Click on the Blogs Listing view");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.BlogsPublicListing);

		log.info("INFO: Click on the blog to be followed");
		blogsUI.clickLinkWithJavascript("link=" + blog.getName());

		log.info("INFO: Click on the Follow this Blog link");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.followThisBlogBtn);

		log.info("INFO: Verify the follow confirmation message displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().followThisBlogMsg),
				"ERROR: The follow this blog confirmation message does not appear");
	}

	/**
	 * The createCommunityAndBlogEntry method will create a community & a blog entry: 
	 * @param community - community to be created
	 * @param blogEntry - blog entry to be created
	 * @param apiCommOwner - api owner to create the community
	 * @param testUser - user to log into communities
	 */	

	private void createCommunityAndBlogEntry(BaseCommunity community,BaseBlogPost blogEntry, APICommunitiesHandler apiCommOwner, User testUser){
		log.info("INFO: Create community using API");
		Community comAPI1 = community.createAPI(apiCommOwner);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner, comAPI1);

		log.info("INFO: Add the Blogs widget using API");
		community.addWidgetAPI(comAPI1, apiCommOwner, BaseWidget.BLOG);

		log.info("INFO: Login to Communities as: " + testUser.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);	

		log.info("INFO: Select Blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.BLOG.select(commUI);

		log.info("INFO: Select New Entry button");
		blogsUI.fluentWaitElementVisibleOnce(BlogsUIConstants.blogsNewEntryMenuItem);
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.blogsNewEntryMenuItem);

		log.info("INFO: Add a new entry to the blog");
		blogEntry.create(blogsUI);
	}
	
	/**
	 * The createStandaloneBlogEntry method will create a standalone blog & blog entry: 
	 * @param testUser - user to log into Blogs
	 * @param blog - blog to be created
	 * @param blogEntry - blog entry to be created
	 */	

	private void createStandaloneBlogEntry(User testUser, BaseBlog blog, BaseBlogPost blogEntry){

		log.info("INFO: Log into Blogs as UserA: " + testUser.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentBlogs);
		blogsUI.login(testUser);

		log.info("INFO: Create a new Blog");
		blog.create(blogsUI);

		log.info("INFO: Open Blog");
		blogsUI.clickLinkWithJavascript("link=" + blog.getName());

		log.info("INFO: Select New Entry button");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.blogsNewEntryMenuItem);

		log.info("INFO: Add a new entry to the Blog");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntry);
		blogEntry.create(blogsUI);

	}
	
	/**
	 * The createStandaloneBlogAddOwner method will create a standalone blog & add a user with Owner access to the blog: 
	 * @param testUser - user to log into Blogs
	 * @param blog - blog to be created
	 * @param memberOwner - user to be added as an Owner to the blog
	 */	

	private void createStandaloneBlogAddOwner(User testUser, BaseBlog blog, Member memberOwner)  {
		log.info("INFO: Log into Blogs as: " + testUser.getDisplayName());
		blogsUI.loadComponent(Data.getData().ComponentBlogs);
		blogsUI.login(testUser);

		log.info("INFO: Create a new Blog");
		blog.create(blogsUI);

		log.info("INFO: Click on the newly created blog link");
		blogsUI.clickLinkWithJavascript("link=" + blog.getName());

		log.info("INFO: Click on the Manage Blog link");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.blogManage);

		log.info("INFO: Click on the link Author");
		BlogSettings_LeftNav_Menu.AUTHORS.select(blogsUI);		

		log.info("INFO: Add the members as an 'Owner' to the Blog");
		blogsUI.addMember(memberOwner);

		log.info("INFO: Click on the Public Blogs tab");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.PublicBlogs);

		log.info("INFO: Click on the Blogs Listing link");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.BlogsPublicListing);

		log.info("INFO: Open the Blog");
		blogsUI.clickLinkWithJavascript("link=" + blog.getName());
	}
	
	/**
	 * The editBlogEntryTags method will edit the entry & add a tag
	 */	

	private void editBlogEntryTags(){
		log.info("INFO: Click on the Edit link");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEditEntry);

		log.info("INFO: Click on the Add or Remove Tags link");
		blogsUI.clickLinkWait(BlogsUIConstants.BlogsNewEntryAddTags);

		log.info("INFO: Add tags to the entry");
		blogsUI.typeTextWithDelay(BlogsUIConstants.BlogsNewEntryAddTagsTextfield, Data.getData().editedData + Data.getData().TagForMyBookmarks);

		log.info("INFO: Click OK to save the new tags");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntryAddTagsOK);

		log.info("INFO: Save the entry");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.blogPostEntryID);

	}
	
	/**
	 * The editBlogEntryTitle method will edit the blog entry title: 
	 * @param blogEntry - entry to be edited
	 */	

	private void editBlogEntryTitle(BaseBlogPost blogEntry){		
		log.info("INFO: Click on the Edit link");
		blogsUI.clickLinkWithJavascript(BlogsUIConstants.BlogsEditEntry);

		log.info("INFO: Clear the Title field");
		blogsUI.clearText(BlogsUIConstants.BlogsNewEntryTitle);

		log.info("INFO: Enter the new title");
		blogsUI.typeTextWithDelay(BlogsUIConstants.BlogsNewEntryTitle, Data.getData().editedData + blogEntry.getTitle());

		log.info("INFO: Save the edit");
		blogsUI.clickLinkWait(BlogsUIConstants.blogPostEntryID);
	}
}
