package com.ibm.conn.auto.tests.metrics_elasticSearch;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class IdeationBlogDataPop extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(IdeationBlogDataPop.class);
	private TestConfigCustom cfg;
	private CommunitiesUI ui;
	private BlogsUI blogUI;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private User testUser1, testUser2;
	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		blogUI = BlogsUI.getGui(cfg.getProductName(), driver);
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);

		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser(); 
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
				
		URLConstants.setServerURL(serverURL);
	}	
		
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - Ideation Blog: Add New Idea</li>
	*<li><B>Step: </B> Create a Public community using the API</li>
	*<li><B>Step: </B> Add the Ideation Blog widget</li>
	*<li><B>Step: </B> Create a New Idea and save</li>
	*</ul>
	*/ 
	@Test(groups = {"regression", "regressioncloud"})
	public void addNewIdea(){
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                     .access(Access.PUBLIC)
                                     .description("Create a Public community.  Add Ideation Blog widget & a new idea. ")
                                     .build();
					
		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("ideation blog entry " + Helper.genDateBasedRandVal())
														 .tags("IdeaTag" + Helper.genDateBasedRand())
														 .content("ideation blog new idea test ")
														 .build();	

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
				
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Add ideation blog widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: Login to Communities as user: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	

		log.info("INFO: Select Ideation blogs from the navigation menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui);

		log.info("INFO: Select the default ideation blog link");
		ui.clickLinkWait(blogUI.getCommIdeationBlogLink(community));
		
		log.info("INFO: Select New Idea button");
		ui.clickLink(BlogsUIConstants.NewIdea);
		
		log.info("INFO: Creating a new idea");
		ideationBlogEntry.create(blogUI);
				
		log.info("INFO: Verify that the new idea exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(ideationBlogEntry.getTitle()), 
				"ERROR: Entry not found"); 
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - Ideation Blog - Graduate a New Idea</li>
	*<li><B>Step: </B> Create a Public community using the API</li>
	*<li><B>Step: </B> Add the Ideation Blog widget</li>
	*<li><B>Step: </B> Create a New Idea and save</li>
	*<li><B>Step: </B> Graduate the new idea</li>
	*</ul>
	*/ 
	@Test(groups = {"regression", "regressioncloud"})
	public void graduateNewIdea(){
		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Create a Public community.  Add Ideation Blog & new idea. Graduate the idea. ")
                                      .build();
					
		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("graduate ideation entry " + Helper.genDateBasedRandVal())
														 .tags("IdeaTag" + Helper.genDateBasedRand())
														 .content("graduate this ideation blog entry ")
														 .build();	

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
				
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Add ideation blog widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: Login to Communities as user: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
				
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	

		log.info("INFO: Select Ideation blogs from navigation menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui);

		log.info("INFO: Select the default ideation blog link");
		ui.clickLinkWait(blogUI.getCommIdeationBlogLink(community));
		
		log.info("INFO: Select New Idea button");
		ui.clickLinkWait(BlogsUIConstants.NewIdea);
		
		log.info("INFO: Creating a new idea");
		ideationBlogEntry.create(blogUI);
		
		log.info("INFO: Clicking on 'Graduate' to graduate the idea");
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduate);
		
		log.info("INFO: Clicking 'OK' in the pop up dialog box to confirm idea graduation");
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduateOK);
		
		log.info("INFO: Verify that the graduation successful message is displayed");
		ui.fluentWaitTextPresent(Data.getData().IdeaGraduatedMsg);
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - Ideation Blog: Add & Edit New Idea</li>
	*<li><B>Step: </B> Create a Public community using the API</li>
	*<li><B>Step: </B> Add the Ideation Blog widget</li>
	*<li><B>Step: </B> Create a New Idea and save</li>
	*<li><B>Step: </B> Edit the ideation entry</li>
	*</ul>
	*/ 
	@Test(groups = {"regression", "regressioncloud"})
	public void editIdeationEntry(){
		
		BaseCommunity community = new BaseCommunity.Builder("editIdeationEntry " + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("Create a Public community.  Add Ideation Blog. Create an entry & edit it. ")
                                                   .build();
					
		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("edit ideation entry " + Helper.genDateBasedRandVal())
														 .tags("IdeaTag" + Helper.genDateBasedRand())
														 .content("edit this ideation blog entry ")
														 .build();	

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
				
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Add ideation blog widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: Login to Communities as user: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
				
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	

		log.info("INFO: Select Ideation blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui);

		log.info("INFO: Select the default ideation blog link");
		ui.clickLinkWait(blogUI.getCommIdeationBlogLink(community));
		
		log.info("INFO: Select New Idea button");
		ui.clickLinkWait(BlogsUIConstants.NewIdea);
		
		log.info("INFO: Creating a new idea");
		ideationBlogEntry.create(blogUI);
		
		log.info("INFO Edit the ideation entry - click on the Edit link");
		ui.clickLinkWait(BlogsUIConstants.BlogsEditEntry);
		
		log.info("INFO: Clear the title field");
		ui.clearText(BlogsUIConstants.BlogsNewEntryTitle);
		
		log.info("INFO: Enter the new title");
		ui.typeTextWithDelay(BlogsUIConstants.BlogsNewEntryTitle, Data.getData().SearchScopeEntries);
		
		log.info("INFO: Save the entry");
		ui.clickLinkWait(BlogsUIConstants.blogPostEntryID);
		
		log.info("INFO: Verify the updated title " + Data.getData().SearchScopeEntries + " appears");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().SearchScopeEntries),
				"ERROR: Updated ideation blog title does not appear");
				
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - Ideation Blog: Add an Idea & Comment</li>
	*<li><B>Step: </B> Create a Moderated community with a member using the API</li>
	*<li><B>Step: </B> Add the Ideation Blog widget</li>
	*<li><B>Step: </B> As the community member create a New Idea and Submit</li>
	*<li><B>Step: </B> Add a comment to the idea and Submit</li>
	*</ul>
	*/ 
	@Test(groups = {"regression", "regressioncloud"})
	public void addCommentToIdeationEntry(){
		
        Member member = new Member(CommunityRole.MEMBERS, testUser2);		
		
		BaseCommunity community = new BaseCommunity.Builder("addCommentToIdeationEntry " + Helper.genDateBasedRandVal())
                                      .access(Access.MODERATED)
                                      .description("Create a Moderated community.  Add Ideation Blog. Create an entry & add a comment. ")
                                      .addMember(member)
                                      .build();
					
		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("test ideation entry " + Helper.genDateBasedRandVal())
														 .tags("IdeaTag" + Helper.genDateBasedRand())
														 .content("add a comment to this ideation blog entry ")
														 .build();	
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
				
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Add ideation blog widget with api");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: Login to Communities as the community member: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
						
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	

		log.info("INFO: Select Ideation blogs from navigation menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui);

		log.info("INFO: Select the default ideation blog link");
		ui.clickLinkWait(blogUI.getCommIdeationBlogLink(community));
		
		log.info("INFO: Select New Idea button");
		ui.clickLinkWait(BlogsUIConstants.NewIdea);
		
		log.info("INFO: Creating a new idea as the additional Member " + testUser2.getDisplayName());
		ideationBlogEntry.create(blogUI);

		log.info("INFO: Click on the Add a Comment link");
		ui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

		log.info("INFO: Wait for comment text area to be present");
		ui.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrame);

		log.info("INFO: Type the comment into the text area");
		ui.typeInCkEditor(Data.getData().BlogsCommentText);

		log.info("INFO: Submit comment");
		ui.clickLink(BlogsUIConstants.BlogsSubmitButton);

		log.info("INFO: Validate that the comment is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().BlogsCommentText),
				"ERROR: Unable to find comment.");
						
		ui.endTest();
	}

}
