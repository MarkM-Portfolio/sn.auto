package com.ibm.conn.auto.tests.homepage.fvt.testcases.im_Following.blogs;



import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class FVT_ImFollowing_PublicBlogs extends SetUpMethods2{
	
	/*
	 * This is a functional test for the Homepage Activity Stream (I'm Following) Component of IBM Connections
	 * Created By: Hugh Caren.
	 * Date: 26/02/2014
	 */
	
	private static Logger log = LoggerFactory.getLogger(LogManager.class);
	
	private TestConfigCustom cfg;	
	private CommunitiesUI ui;
	private HomepageUI uiH;
	
	private User testUser1;
	private User testUser2;
		
	
	
	public String serverURL;
	public APICommunitiesHandler commsAPI;
	public APIBlogsHandler blogsAPI;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		uiH = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		commsAPI = new APICommunitiesHandler(serverURL,testUser1.getUid(),testUser1.getPassword());
		blogsAPI = new APIBlogsHandler(serverURL,testUser1.getUid(),testUser1.getPassword());
		 
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_AddBlogWidget_PublicCommunity()</li>
	 * <li><B>How:</B> test_AddBlogWidget_PublicCommunity() creates a public community, adds another user as owner (to allow follow) and adds a blog widget to the community </li> 
	 * <li><B>Verify:</B> blog widget added event appears as expected in the homepage AS (Im Following) filtered by communities and blogs views.
	 * @author Hugh Caren
	 * @throws Exception
	 */	
	
	@Test (groups={"level3"})
	public void test_AddBlogWidget_PublicCommunity(){

		//Data for this test
		String testName=ui.startTest();
		
		
		//Build the community to be created later
		log.info("Creating Community");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.PUBLIC)
		   										   .description("Test description for testcase " + testName)
		   										   .addMember(new Member(CommunityRole.OWNERS, testUser1))
		   										   .build();
		
		//Community created
		Community community = baseCom.createAPI(commsAPI);
		log.info(testName + "Community created");
		
		
		//GUI add user2 as an owner to the Moderated Community
		log.info("Loading communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//open community
		log.info("INFO: Select community");
		ui.clickLinkWait("link="+ baseCom.getName());
		
		//select leftNav
		log.info("INFO: Select Members in left navigation menu");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavMembers);
		
		//select add member to existing community
		log.info("INFO: Select add member to existing community");
		ui.clickLinkWait(CommunitiesUIConstants.AddMembersToExistingCommunity);
		
		//may have common code here to select user from dropdown
		driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).useAsDropdown().selectOptionByVisibleText("Owners");
		ui.typeText(CommunitiesUIConstants.AddMembersToExistingTypeAhead, testUser2.getLastName() + " ");
		driver.getSingleElement(CommunitiesUIConstants.addMember_Typeahead_SelectUser).click();

		ui.clickLinkWait(CommunitiesUIConstants.CommunityMemebersPageNewMembersSaveButton);
		
		ui.logout();
		driver.close();
		
		
		
		
		
		//blog widget added to community
		log.info("Blog Widget added to community");
		baseCom.addWidgetAPI(community, commsAPI, BaseWidget.BLOG);
		
		//log User 2 in through GUI to check story in AS (Im Following)
		log.info("Log in User 2 to verify story");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser2);
		
		
		//verify story in all filter
		log.info("INFO: Validate Community is present in the Im Following (All filter) view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " created the " + baseCom.getName() +  " blog."));
		
		//verify story in communities filter
		uiH.filterBy("Communities");
		log.info("INFO: Validate Community is present in the Im Following (communities filter) view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " created the " + baseCom.getName() +  " blog."));
		
		//verify story in blogs filter
		uiH.filterBy("Blogs");
		log.info("INFO: Validate Community is present in the Im Following (Blogs filter) view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " created the " + baseCom.getName() +  " blog."));
		
		ui.endTest();
			
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_AddBlogEntry_PublicCommunity()</li>
	 * <li><B>How:</B> test_AddBlogEntry_PublicCommunity() creates a public community, adds another user as owner (to allow follow) and adds a blog widget to the community </li> 
	 * <li><B>How:</B>test_AddBlogEntry_PublicCommunity() creates a blog post in the public Community.
	 * <li><B>Verify:</B> Blog Post event appears in Hompeage Activity Stream (Im following) in the all, communities and blogs filters.
	 * @author Hugh Caren
	 * @throws Exception
	 */	
	
	@Test (groups={"level3"})
	public void test_AddBlogEntry_PublicCommunity(){

		//Data for this test
		String testName=ui.startTest();
		
		
		//Build the community to be created later
		log.info("Creating Community");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.PUBLIC)
		   										   .description("Test description for testcase " + testName)
		   										   .addMember(new Member(CommunityRole.OWNERS, testUser1))
		   										   .build();
		
		//Community created
		Community community = baseCom.createAPI(commsAPI);
		log.info(testName + "Community created");
		
		
		//GUI add user2 as an owner to the Moderated Community
		log.info("Loading communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//open community
		log.info("INFO: Select community");
		ui.clickLinkWait("link="+ baseCom.getName());
		
		//select leftNav
		log.info("INFO: Select Members in left navigation menu");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavMembers);
		
		//select add member to existing community
		log.info("INFO: Select add member to existing community");
		ui.clickLinkWait(CommunitiesUIConstants.AddMembersToExistingCommunity);
		
		//may have common code here to select user from dropdown
		driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).useAsDropdown().selectOptionByVisibleText("Owners");
		ui.typeText(CommunitiesUIConstants.AddMembersToExistingTypeAhead, testUser2.getLastName() + " ");
		driver.getSingleElement(CommunitiesUIConstants.addMember_Typeahead_SelectUser).click();

		ui.clickLinkWait(CommunitiesUIConstants.CommunityMemebersPageNewMembersSaveButton);
		
		ui.logout();
		driver.close();
		
		//blog widget added to community
		log.info("Adding blog widget to community");
		baseCom.addWidgetAPI(community, commsAPI, BaseWidget.BLOG);
		

		//create base blog to be used
		BaseBlog newBaseBlog = new BaseBlog.Builder(testName, "test")
										   .blogAddress("test")
										   .description("desc")
										   .build();
		
		
		//Build the Base Blog Post to be created later.
		BaseBlogPost newBaseBlogPost= new BaseBlogPost.Builder(testName +Helper.genDateBasedRand())
													.tags("testTags"+Helper.genDateBasedRand()).content("content" + Helper.genDateBasedRand()).allowComments(true)
													.numDaysCommentsAllowed(5).complete(true)
													.build();
		
		//Blog Object created Code for creating Blog Entry
		log.info("Adding blog to Community");
		//Blog newBlog = newBaseBlog.createAPI(blogsAPI, baseCom, newCommunity);
		Blog newBlog = newBaseBlog.createAPI(blogsAPI);
		
		
		//API Code for creating BlogPost in Community
		log.info("Adding Blog Post to Community");
		newBaseBlogPost.createAPI(blogsAPI, newBlog);
				
				
	

		//log User 2 in through GUI to check story in AS (Im Following)
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		//select homepage icon
		log.info("INFO: Selecting Homepage icon");
		ui.clickLinkWait(HomepageUIConstants.HomeIcon);
		
		//Select updates link
		log.info("INFO: Selecting updates from left nav menu");
		ui.clickLinkWait(HomepageUIConstants.Updates);
		
		//verify story in all filter
		log.info("INFO: Validate Community is present in the Im Following (All filter) view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " created a blog entry named " + newBaseBlogPost.getTitle() + " in the "+ baseCom.getName() + " blog."));
		
		//verify story in communities filter
		uiH.filterBy("Communities");
		log.info("INFO: Validate Community is present in the Im Following (communities filter) view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " created a blog entry named " + newBaseBlogPost.getTitle() + " in the "+ baseCom.getName() + " blog."));
		
		//verify story in blogs filter
		uiH.filterBy("Blogs");
		log.info("INFO: Validate Community is present in the Im Following (Blogs filter) view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " created a blog entry named " + newBaseBlogPost.getTitle() + " in the "+ baseCom.getName() + " blog."));
			
		ui.endTest();
		
	}
	
	/**
	 * <ul>
	 * <li><B>Name:</B> test_AddComment_PublicCommunity()</li>
	 * <li><B>How:</B> test_AddComment_PublicCommunity() creates a public community, adds another user as owner (to allow follow) and adds a blog widget to the community </li> 
	 * <li><B>How:</B>test_AddComment_PublicCommunity() creates a blog post in the public Community and adds a comment to the blog post.
	 * <li><B>Verify:</B> Comment on Blog Post event appears in Hompeage Activity Stream (Im following) in the all, communities and blogs filters.
	 * @author Hugh Caren
	 * @throws Exception
	 */	
	
	@Test(groups = {"level3"})
	public void test_AddComment_PublicCommunity(){
		

		//Data for this test
		String testName=ui.startTest();
		
		
		//Build the community to be created later
		log.info("Creating Community");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.PUBLIC)
		   										   .description("Test description for testcase " + testName)
		   										   .addMember(new Member(CommunityRole.OWNERS, testUser1))
		   										   .build();
		
		//Community created
		Community community = baseCom.createAPI(commsAPI);
		log.info(testName + "Community created");
		
		
		//GUI add user2 as an owner to the Moderated Community
		log.info("adding user2 to community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//open community
		log.info("INFO: Select community");
		ui.clickLinkWait("link="+ baseCom.getName());
		
		//select leftNav
		log.info("INFO: Select Members in left navigation menu");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavMembers);
		
		//select add member to existing community
		log.info("INFO: Select add member to existing community");
		ui.clickLinkWait(CommunitiesUIConstants.AddMembersToExistingCommunity);
		
		//may have common code here to select user from dropdown
		driver.getSingleElement(CommunitiesUIConstants.CommunityMembersDropdown).useAsDropdown().selectOptionByVisibleText("Owners");
		ui.typeText(CommunitiesUIConstants.AddMembersToExistingTypeAhead, testUser2.getLastName() + " ");
		driver.getSingleElement(CommunitiesUIConstants.addMember_Typeahead_SelectUser).click();

		ui.clickLinkWait(CommunitiesUIConstants.CommunityMemebersPageNewMembersSaveButton);
		
		ui.logout();
		driver.close();
		
		
		
		
		
		//blog widget added to community
		baseCom.addWidgetAPI(community, commsAPI, BaseWidget.BLOG);
		

		//create base blog to be used
		BaseBlog newBaseBlog = new BaseBlog.Builder(testName, "test").blogAddress("test").description("desc").build();
		
		
		//Build the Base Blog Post to be created later.
		BaseBlogPost newBaseBlogPost= new BaseBlogPost.Builder(testName +Helper.genDateBasedRand())
													.tags("testTags"+Helper.genDateBasedRand()).content("content" + Helper.genDateBasedRand()).allowComments(true)
													.numDaysCommentsAllowed(5).complete(true)
													.build();
		
		//Blog Object created Code for creating Blog Entry
		//Blog newBlog = newBaseBlog.createAPI(blogsAPI, baseCom, newCommunity);
		Blog newBlog = newBaseBlog.createAPI(blogsAPI);
		
		//API Code for creating BlogPost in Community
		log.info("Adding Blog Post to Community");
		BlogPost newBlogPost = newBaseBlogPost.createAPI(blogsAPI, newBlog);
				
						
		//Add comment to Blog Post using API
		log.info("Adding Comment to Blog Entry");

		//BlogComment newBlogComment=blogsAPI.CreateBlogComment("Comment for " +testName, newBlogPost);		
	//	log.info(newBlogComment.getContent() + " Comment added to " + baseCom.getName());
	

		//log User 2 in through GUI to check story in AS (Im Following)
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(testUser2);
		
		
		//verify story in all filter
		log.info("INFO: Validate Community is present in the Im Following (All filter) view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " commented on their own " + newBlogPost.getTitle() + " blog entry in the " +baseCom.getName() + " blog."));
		
		//verify story in communities filter
		uiH.filterBy("Communities");
		log.info("INFO: Validate Community is present in the Im Following (communities filter) view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " commented on their own " + newBlogPost.getTitle() + " blog entry in the " +baseCom.getName() + " blog."));
		
		//verify story in blogs filter
		uiH.filterBy("Blogs");
		log.info("INFO: Validate Community is present in the Im Following (Blogs filter) view");
		Assert.assertTrue(driver.isTextPresent(testUser1.getDisplayName() + " commented on their own " + newBlogPost.getTitle() + " blog entry in the " +baseCom.getName() + " blog."));
			
		ui.endTest();
	
	
	}
	
	
	
	
	
	
}