package com.ibm.conn.auto.tests.homepage.fvt.testcases.mentions.blogs.communities;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * @author Patrick Doherty
 */

public class FVT_Mentions_Blog_PrivateCommunity_GuestAndExternal extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_Mentions_Blog_PrivateCommunity_GuestAndExternal.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;
	private BaseCommunity baseCom;
	private BaseBlogPost newBaseBlogPost;
	private User testUser1, testUser2;
	private String blogComment = "";
	private String serverURL = "";
	private String testName = "";

	private APICommunitiesHandler commsAPI;
	private APIBlogsHandler blogsAPI;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL().replaceFirst("9080", "9443"));
		commsAPI = new APICommunitiesHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		blogsAPI = new APIBlogsHandler(serverURL, testUser1.getUid(), testUser1.getPassword());
		 
	}

	/**
	* mention_privateCommunity_blogEntry_mentionsView_guestMember() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a private community adding a guest user as a member</B></li>
	*<li><B>Step: testUser2 is a guest of the organization</B></li>
	*<li><B>Step: testUser1 customize and add the Blogs widget</B></li>
	*<li><B>Step: testUser1 add an entry mentioning testUser2</B></li>
	*<li><B>Step: testUser1 add a comment to the entry mentioning testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates</B></li>
	*<li><B>Verify: Verify that a message appears saying guest user cannot be mentioned and the '@' is dropped</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/524F5D7EC5A9FBC985257CAC004D50DB">TTT - @MENTIONS - 150 - MENTIONS DIRECTED TO YOU IN A BLOG ENTRY - GUEST USER - SC ONLY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtcloud"})
	public void mention_privateCommunity_blogEntry_mentionsView_guestMember() throws Exception{

		testName = ui.startTest();
		
		//Build Community
		baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.RESTRICTED)
		   										   .description("Test description for testcase " + testName)
		   										   .build();
		
		//Build the Base Blog Post to be created later.
		newBaseBlogPost= new BaseBlogPost.Builder(baseCom.getName())
													.tags("testTags"+Helper.genDateBasedRand()).content("content" + Helper.genDateBasedRand()).allowComments(true)
													.numDaysCommentsAllowed(5).complete(true)
													.build();
		
		log.info( "Creating Community");
		Community newCommunity = baseCom.createAPI(commsAPI);
		
		log.info("INFO: Adding testUser2 (" + testUser2.getDisplayName() + ") to community");
		commsAPI.addMemberToCommunity(testUser2, newCommunity, StringConstants.Role.MEMBER);
		
		log.info("INFO: Adding blog widget to community");
		baseCom.addWidgetAPI(newCommunity, commsAPI, BaseWidget.BLOG);
			
		/*
		 * Login testUser1 who will add a blog entry
		 * containing an @mentions to testUser2
		 */
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser1);
		
		//If the community name is not visible refresh the page by clicking the "I'm an Owner" link
		if(!driver.isElementPresent("link=" + baseCom.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);
		ui.clickLinkWait(CommunitiesUIConstants.leftNavBlogs);
		ui.fluentWaitPresent(BlogsUIConstants.BlogsNewEntry);
		
		//Title
		ui.clickLinkWait(BlogsUIConstants.BlogsNewEntryTitle);
		ui.typeText(BlogsUIConstants.BlogsNewEntryTitle, newBaseBlogPost.getTitle());

		//Check if post has tag and add it if so
		if (!newBaseBlogPost.getTags().isEmpty()){
			log.info("INFO: Adding Post Tag");
			ui.clickLinkWait(BlogsUIConstants.BlogsNewEntryAddTags);
			ui.typeText(BlogsUIConstants.BlogsNewEntryAddTagsTextfield, newBaseBlogPost.getTags());
			ui.clickLinkWait(BlogsUIConstants.BlogsNewEntryAddTagsOK);
		}
		
		//Check if post has Content and add it if so
		if (!newBaseBlogPost.getContent().isEmpty()){
			log.info("INFO: Adding Post Content");
			ui.typeNativeInCkEditor(newBaseBlogPost.getContent() + " @" + testUser2.getDisplayName());
			//focus on the typeahead
			driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
			
			//click on the appropriate user
			driver.getSingleElement(HomepageUIConstants.typeAheadBox + HomepageUI.selectUserFromTypeAhead(testUser2.getDisplayName())).click();
			
		}

		log.info("INFO: Verify the warning message is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent("This user cannot be mentioned"),
						 "ERROR: Warning message is NOT displayed");

		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser2.getDisplayName()),
						 "ERROR: @mentions is displayed");
		
		//Save the entry unless user requests it incomplete state
		if(newBaseBlogPost.getComplete()){
			log.info("INFO: Posting Entry");
			ui.fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
			driver.getSingleElement(BlogsUIConstants.BlogsNewEntryPost).click();
		}
		
		//check a second time to prevent transition timing issue with saving image
		if(!driver.isTextPresent(newBaseBlogPost.getTitle())&& newBaseBlogPost.getComplete()){
			log.warn("WARNING: Potential transition issue attempting a second time to Post Entry");
			ui.fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
			driver.getSingleElement(BlogsUIConstants.BlogsNewEntryPost).click();
			
		}

		log.info("INFO: Go to the entry");
		ui.clickLinkWait("link=" + newBaseBlogPost.getTitle());
		ui.fluentWaitPresent(BlogsUIConstants.BlogsAddACommentLink);
		
		log.info("INFO: Add a comment to the entry");
		//Open the Comment Editor
		ui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

		// Fill in the comment form
		driver.getSingleElement(HomepageUIConstants.EnterMentionsStatusUpdate).click();
		ui.typingTextUsingRobotClass(blogComment + " @" + testUser2.getDisplayName());

		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		driver.getSingleElement(HomepageUIConstants.typeAheadBox + HomepageUI.selectUserFromTypeAhead(testUser2.getDisplayName())).click();

		log.info("INFO: Verify the warning message is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent("This user cannot be mentioned"),
						 "ERROR: Warning message is NOT displayed");

		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser2.getDisplayName()),
						 "ERROR: @mentions is displayed");
				
		// Submit comment
		ui.clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);
		
		ui.logout();
		
		/*
		 * Login testUser2 who will go to the "I'm Following" view
		 * on Homepage and verify that the @mentions to testUser2 does NOT appear
		 */

		ui.loadComponent(Data.getData().HomepageImFollowing, true);
		ui.waitForPageLoaded(driver);

		log.info("INFO: Verify the blog entry comment is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(blogComment),
						 "ERROR: blog entry Comment is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser2.getDisplayName()),
						 "ERROR: @mentions is displayed");
		
		ui.endTest();
	}

	/**
	* mention_privateCommunity_blogEntry_mentionsView_externalUser() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 start a private community adding a external user as a member</B></li>
	*<li><B>Step: testUser1 customize and add the Blogs widget</B></li>
	*<li><B>Step: testUser1 add an entry mentioning testUser2</B></li>
	*<li><B>Step: testUser1 add a comment to the entry mentioning testUser2</B></li>
	*<li><B>Step: testUser2 log into Homepage / Updates</B></li>
	*<li><B>Verify: Verify that a message appears saying guest user cannot be mentioned and the '@' is dropped</B></li>
	*<li><B>Verify: Verify that testUser2 CANNOT see the mentions event in the filters in the I'm Following view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/A7410A28F897F52485257CAC004D50DC">TTT - @MENTIONS - 151 - MENTIONS DIRECTED TO YOU IN A BLOG ENTRY - EXTERNAL USER - SC ONLY</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtcloud"})
	public void mention_privateCommunity_blogEntry_mentionsView_externalUser() throws Exception{
		
		blogComment = Data.getData().StatusComment + Helper.genDateBasedRandVal();
		
		testName = ui.startTest();
		
		//Build Community
		baseCom = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .tags("testTags"+ Helper.genDateBasedRand())
		   										   .access(Access.RESTRICTED)
		   										   .description("Test description for testcase " + testName)
		   										   .build();
		
		//create base blog to be used
		BaseBlog newBaseBlog = new BaseBlog.Builder(baseCom.getName(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
										   .description("Test description for testcase " + testName)
										   .build();
		
		//Build the Base Blog Post to be created later.
		newBaseBlogPost= new BaseBlogPost.Builder(baseCom.getName())
													.tags("testTags"+Helper.genDateBasedRand()).content("content" + Helper.genDateBasedRand()).allowComments(true)
													.numDaysCommentsAllowed(5).complete(true)
													.build();
		
		log.info( "Creating Community");
		Community newCommunity = baseCom.createAPI(commsAPI);
		
		log.info("INFO: Adding testUser2 (" + testUser2.getDisplayName() + ") to community");
		commsAPI.addMemberToCommunity(testUser2, newCommunity, StringConstants.Role.MEMBER);
		
		log.info("INFO: Adding blog widget to community");
		baseCom.addWidgetAPI(newCommunity, commsAPI, BaseWidget.BLOG);
		 
		log.info("INFO: Adding blog to Community");
		newBaseBlog.createAPI(blogsAPI, newCommunity);
			
		/*
		 * Login testUser1 who will add a comment to the blog entry
		 * containing an @mentions to testUser2
		 */
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser1);
		//If the community name is not visible refresh the page by clicking the "I'm an Owner" link
		if(!driver.isElementPresent("link=" + baseCom.getName())){
			ui.clickLinkWait(BaseUIConstants.Im_Owner);
			ui.waitForPageLoaded(driver);
		}
		ui.clickLinkWait(CommunitiesUIConstants.leftNavOverview);
		ui.clickLinkWait(CommunitiesUIConstants.leftNavBlogs);
		ui.fluentWaitPresent(BlogsUIConstants.BlogsNewEntry);

		//Title
		ui.clickLinkWait(BlogsUIConstants.BlogsNewEntryTitle);
		ui.typeText(BlogsUIConstants.BlogsNewEntryTitle, newBaseBlogPost.getTitle());

		//Check if post has tag and add it if so
		if (!newBaseBlogPost.getTags().isEmpty()){
			log.info("INFO: Adding Post Tag");
			ui.clickLinkWait(BlogsUIConstants.BlogsNewEntryAddTags);
			ui.typeText(BlogsUIConstants.BlogsNewEntryAddTagsTextfield, newBaseBlogPost.getTags());
			ui.clickLinkWait(BlogsUIConstants.BlogsNewEntryAddTagsOK);
		}
		
		//Check if post has Content and add it if so
		if (!newBaseBlogPost.getContent().isEmpty()){
			log.info("INFO: Adding Post Content");
			ui.typeNativeInCkEditor(newBaseBlogPost.getContent() + " @" + testUser2.getDisplayName());
			//focus on the typeahead
			driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
			
			//click on the appropriate user
			driver.getSingleElement(HomepageUIConstants.typeAheadBox + HomepageUI.selectUserFromTypeAhead(testUser2.getDisplayName())).click();
			
		}

		log.info("INFO: Verify the warning message is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent("This user cannot be mentioned"),
						 "ERROR: Warning message is NOT displayed");

		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser2.getDisplayName()),
						 "ERROR: @mentions is displayed");
		
		//Save the entry unless user requests it incomplete state
		if(newBaseBlogPost.getComplete()){
			log.info("INFO: Posting Entry");
			ui.fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
			driver.getSingleElement(BlogsUIConstants.BlogsNewEntryPost).click();
		}
		
		//check a second time to prevent transition timing issue with saving image
		if(!driver.isTextPresent(newBaseBlogPost.getTitle())&& newBaseBlogPost.getComplete()){
			log.warn("WARNING: Potential transition issue attempting a second time to Post Entry");
			ui.fluentWaitPresent(BlogsUIConstants.BlogsNewEntryPost);
			driver.getSingleElement(BlogsUIConstants.BlogsNewEntryPost).click();
			
		}
		
		log.info("INFO: Go to the entry");
		ui.clickLinkWait("link=" + newBaseBlogPost.getTitle());
		ui.fluentWaitPresent(BlogsUIConstants.BlogsAddACommentLink);
		
		log.info("INFO: Add a comment to the entry");
		//Open the Comment Editor
		ui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

		// Fill in the comment form
		driver.getSingleElement(HomepageUIConstants.EnterMentionsStatusUpdate).click();
		ui.typingTextUsingRobotClass(blogComment + " @" + testUser2.getDisplayName());

		//focus on the typeahead
		driver.getSingleElement(HomepageUIConstants.typeAheadBox).hover();
		
		//click on the appropriate user
		driver.getSingleElement(HomepageUIConstants.typeAheadBox + HomepageUI.selectUserFromTypeAhead(testUser2.getDisplayName())).click();

		log.info("INFO: Verify the warning message is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent("This user cannot be mentioned"),
						 "ERROR: Warning message is NOT displayed");

		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser2.getDisplayName()),
						 "ERROR: @mentions is displayed");
				
		// Submit comment
		ui.clickLinkWait(BlogsUIConstants.BlogsCommentSubmit);
		
		ui.logout();
		
		/*
		 * Login testUser2 who will go to the "I'm Following" views
		 * on Homepage and verify that the @mentions to testUser2 does NOT appear
		 */
		ui.loadComponent(Data.getData().HomepageImFollowing, true);
		ui.waitForPageLoaded(driver);

		log.info("INFO: Verify the blog entry comment is NOT displayed");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(blogComment),
						 "ERROR: blog entry Comment is displayed");
		
		log.info("INFO: Verify the @mentions is NOT displayed");
		Assert.assertTrue(!ui.fluentWaitElementVisible("link=" + "@" + testUser2.getDisplayName()),
						 "ERROR: @mentions is displayed");
		
		ui.endTest();
	}

}
