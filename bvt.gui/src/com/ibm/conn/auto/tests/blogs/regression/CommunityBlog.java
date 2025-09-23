package com.ibm.conn.auto.tests.blogs.regression;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.MemberRole;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.BlogsUI.EditVia;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunityBlog extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(GeneralUI.class);
	private TestConfigCustom cfg;
	private BlogsUI ui;
	private CommunitiesUI cUI;
	private User testUser1, testUser2;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity.Access defaultAccess;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests if a community can be edited using the Blog widget [<B>Note: Because the membership sync needs 10 minutes, it will not check if the membership sync works correctly</B>].
	*<li><B>Step: </B>Create a Community.
	*<li><B>Step: </B>Add the blog widget using API.
	*<li><B>Step: </B>Login as the community's owner. 
	*<li><B>Step: </B>Post a blog entry as the community's owner.
	*<li><B>Step: </B>Go to the community's Overview page.
	*<li><B>Step: </B>Select the Edit option from the Widget Actions Menu in the Blog widget.
	*<li><B>Step: </B>Edit the Name, Tags and Description fields.
	*<li><B>Step: </B>Also edit the Member Role field and select the Draft option for the Member Role then save the changes.
	*<li><B>Step: </B>Select the Blog link from the navigation menu.
	*<li><B>Verify: </B>The new name should be displayed as the name of the blog.
	*<li><B>Step: </B>Add a new member to this community and logout.
	*<li><B>Step: </B>Login with the new member's credentials.
	*<li><B>Step: </B>Switch to I'm a Member view.
	*<li><B>Verify: </B>The community card should be visible in the current view.
	*<li><B>Step: </B>Go to the community Overview page.
	*<li><B>Verify: </B>The Create Blog Entry link is visible to the member with the role of a Draft.
	*<li><B>Step: </B>Select the entry which was created by the community's owner from the Blog widget.
	*<li><B>Step: </B>Comment on the entry.
	*<li><B>Verify: </B>Can comment on the entry.
	*<li><B>Step: </B>Logout and then login as the owner.
	*<li><B>Step: </B>Delete the community as the owner.
	*<li><B>Verify: </B>The Blog widget can edit the community.
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void editCommBlogsWidgetDraft() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test for " + testName)
									 			   .tags("tag" + Helper.genDateBasedRand())
									 			   .build();
		
		BaseBlog blog = new BaseBlog.Builder(community.getName(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
									.community(community)
									.build();
		
		//for creating the blog
		BaseBlogPost NewEntry1 = new BaseBlogPost.Builder("First Entry " + Helper.genDateBasedRand())
				   								 .tags("Tag" + Helper.genDateBasedRand())
				   								 .content("BVT blog content")
				   								 .build();
		
		BaseBlogComment comment = new BaseBlogComment.Builder("Post a comment to the entry for " + testName).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.setCommunityUUID(community.getCommunityUUID_API(apiOwner, comAPI));
		
		//add the blog widget using API
		logger.strongStep("Add the blogs widget using API");
		log.info("INFO: Add the blogs widget using the API");
		if (apiOwner.getWidgetID(comAPI.getUuid(), "Blog").isEmpty())
		{
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
		}
		
		//Login as a user
		logger.strongStep("Open Communities and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();

		if (flag)
		{
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	
		
		//Post an entry as the community blog owner
		logger.strongStep("Click on the Blog link in the navigation menu");
		log.info("INFO: Select Blog from the navigation menu");
		Community_LeftNav_Menu.BLOG.select(cUI);
	
		logger.strongStep("Click on the New Entry button in the blog page");
		log.info("INFO: Select New Entry button once the blog page opens up");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		logger.strongStep("Create a new entry and click on Post button");
		log.info("INFO: Create a new entry and submit");
		NewEntry1.create(ui);
		
		//Navigate to Overview page
		logger.strongStep("Click on the Community Actions menu");
		log.info("INFO: Select the Community Actions menu"); 
		ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);
		
		logger.strongStep("Select the Go to Community option");
		log.info("INFO: Click on the Go to Community option");
		ui.clickLinkWait(BlogsUIConstants.GoToCommunityOption);
		
		logger.strongStep("Verify the Create Blog Entry link is visible");
		log.info("INFO: Validate that the Create Blog Entry link appears"); 
		ui.fluentWaitElementVisible(BlogsUIConstants.blogsNewEntryLink);
		
		//Click the Blog widget menu edit
		logger.strongStep("Select the Edit option from the Widget Actions Menu in the Blog widget");
		log.info("INFO: Click on the Edit option in the Widget Actions Menu of the Blog widget");
		BaseWidget.BLOG.preformMenuAction(Widget_Action_Menu.EDIT, cUI);
		//cUI.performCommWidgetAction(BaseWidget.BLOG, Widget_Action_Menu.EDIT);
		
		logger.strongStep("Edit the Name field and change the name of the blog to 'New Blog Name'");
		log.info("INFO: Select the Name field and change the name of the blog to 'New Blog Name'");
		blog.setName("New Blog Name");

		logger.strongStep("Edit the Tags field and change it to 'Tag1'");
		log.info("INFO: Click on the Tags field and change it to 'Tag1'");
		blog.setTags("Tag1");

		logger.strongStep("Edit the Description field and change it to 'New description for community blog'");
		log.info("INFO: Choose the Description field and change it to 'New description for community blog'");
		blog.setDescription("New description for community blog");
		
		logger.strongStep("Select the Draft option for the Member Role");
		log.info("INFO: Click on the Draft option under Member Role");
		blog.setComMemberRole(MemberRole.DRAFT);

		logger.strongStep("Click on the Save button");
		log.info("INFO: Select the Save button");
		blog.edit(ui, EditVia.WIDGETMENU);
		
		// Scroll up to the top of page
		driver.executeScript("window.scrollTo(0, 0)");
		
		logger.strongStep("Click on the Blog link in the navigation menu");
		log.info("INFO: Select Blog from the navigation menu"); 
		Community_LeftNav_Menu.BLOG.select(cUI);
		
		logger.strongStep("Verify the new name is displayed as the name of the blog");
		log.info("INFO: Validate the blog name is the new name"); 
		Assert.assertTrue(driver.isTextPresent(blog.getName()), 
						  "Error: Edit community blog widget is failed.");
		
		//Add Member to this community
		logger.strongStep("Click on the Members link in the navigation menu");
		log.info("INFO: Select Members from the navigation menu"); 
		cUI.gotoMembers();
		
		logger.strongStep("Adding " + testUser2.getDisplayName() + " as the member to the community");
		log.info("INFO: Add member " + testUser2.getDisplayName() + "for this community");
		cUI.addMemberCommunity(new Member(CommunityRole.MEMBERS, testUser2));

		logger.strongStep("Click on the Save button");
		log.info("INFO: Select Save button"); 
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);

		logger.strongStep("Log out of and close the session");
		ui.logout();
		ui.close(cfg);
		
		//Login with member user to post entry, make sure the membership is draft user
		logger.strongStep("Open Communities and login: " +testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);

		//Go to I am Member view, to verify the community shows in this view
		logger.strongStep("Switch to I'm a Member view");
		log.info("INFO: Navigate to I'm a Member View.");
		Community_View_Menu.IM_A_MEMBER.select(cUI);
		
		logger.strongStep("Verify the Create a Community drop down appears");
		log.info("INFO: Wait for Create a Community drop down to be visible"); 
		ui.fluentWaitElementVisible(CommunitiesUIConstants.StartACommunityDropDownCardView);
		
		logger.strongStep("Verify the community card is visible in the current view which is I'm a Member");
		log.info("INFO: Validate that the community card is in I'm a Member view");
		Assert.assertTrue(driver.isElementPresent("css=div[aria-label='" + community.getName() + "']"), 
						  "Error: Cannot find the community in I am Member view.");
		
		//Go to the community blog page to post an entry, verify the button is "Submit for review"
		logger.strongStep("Open the community by clicking on its card");
		log.info("INFO: Navigate to the community " + community.getName());
		ui.clickLinkWait("css=div[aria-label='" + community.getName() + "']");
		
		logger.strongStep("Click on the Create Blog Entry link in the Blog widget");
		log.info("INFO: Select Create Blog Entry link from the Blog widget"); 
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryLink);
				
		logger.strongStep("Confirm that the Post button now reads 'Submit for Review' because a draft member can only submit an entry for review and not post it");
		log.info("INFO: Verify the value of the Post button is now 'Submit for Review' because a draft member can only submit an entry for review and not post it");
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.BlogsNewEntryPost).getAttribute("value"), "Submit for Review",
							"ERROR: Blogs New Entry Post web element value does not equal 'Submit for Review'");
		
		logger.strongStep("Click on the Cancel button");
		log.info("INFO: Select the Cancel button in the New Entry form");
		ui.clickLinkWait(BlogsUIConstants.BlogEntryCancelBtn);
		
		//comment on an entry, verify the comment be moderated
		logger.strongStep("Click on the link for the entry in the Blog widget which was created by " +testUser1.getDisplayName());
		log.info("INFO: Select the entry in the Blog widget which was created by " +testUser1.getDisplayName()); 
		ui.clickLinkWait("link=" + NewEntry1.getTitle());		

		logger.strongStep("Add a comment and submit");
		log.info("INFO: Create a comment and click on Submit button");
		comment.create(ui);
		
		logger.strongStep("Log out of and close the session");
		ui.logout();
		ui.close(cfg);
		
		//testUser1 delete the community
		logger.strongStep("Open Communities and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Open the community " + community.getName() + " by clicking on its card");
		log.info("INFO: Navigate to the community " + community.getName());
		ui.clickLinkWait("css=div[aria-label='" + community.getName() + "']");

		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community"); 
		cUI.delete(community, testUser1);

		ui.endTest();		
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests that a member with the role of a Viewer in a blog can't create an entry in the blog [<B>Note: because the membership sync needs 10 minutes, it will not check if the membership sync works correctly</B>].
	*<li><B>Step: </B>Create a Community.
	*<li><B>Step: </B>Add the blog widget using API.
	*<li><B>Step: </B>Login as the community's owner. 
	*<li><B>Step: </B>Post a blog entry as the community's owner.
	*<li><B>Step: </B>Go to the community's Overview page.
	*<li><B>Step: </B>Select the Edit option from the Widget Actions Menu in the Blog widget.
	*<li><B>Step: </B>Edit the Name, Tags and Description fields.
	*<li><B>Step: </B>Also edit the Member Role field and select the Viewer option for the Member Role then save the changes.
	*<li><B>Step: </B>Select the Blog link from the navigation menu.
	*<li><B>Verify: </B>The new name should be displayed as the name of the blog.
	*<li><B>Step: </B>Add a new member to this community and logout.
	*<li><B>Step: </B>Login with the new member's credentials.
	*<li><B>Step: </B>Switch to I'm a Member view.
	*<li><B>Verify: </B>The community card should be visible in the current view.
	*<li><B>Step: </B>Go to the community Overview page.
	*<li><B>Verify: </B>The Create Blog Entry link is not visible to the member with the role of a Viewer.
	*<li><B>Step: </B>Select the entry which was created by the community's owner from the Blog widget.
	*<li><B>Step: </B>Comment on the entry.
	*<li><B>Verify: </B>Can comment on the entry.
	*<li><B>Step: </B>Logout and then login as the owner.
	*<li><B>Step: </B>Delete the community as the owner.
	*</ul>
	*/ 
	@Test(groups = {"regression"})
	public void editCommBlogsWidgetViewer() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test for " + testName)
									 			   .tags("tag" + Helper.genDateBasedRand())
									 			   .build();
		
		BaseBlog blog = new BaseBlog.Builder(community.getName(), Data.getData().BlogsAddress1 + Helper.genDateBasedRand())
									.community(community)
									.build();
		
		//for creating the blog
		BaseBlogPost NewEntry1 = new BaseBlogPost.Builder("First Entry " + Helper.genDateBasedRand())
				   								 .tags("Tag" + Helper.genDateBasedRand())
				   								 .content("BVT blog content")
				   								 .build();
		
		BaseBlogComment comment = new BaseBlogComment.Builder("Post a comment to the entry for " + testName).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//add the blog widget using API
		logger.strongStep("Add the blogs widget using API");
		log.info("INFO: Add the blogs widget using the API");
		if (apiOwner.getWidgetID(comAPI.getUuid(), "Blog").isEmpty()) 
		{
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
  		}
		
		//Login as a user
		logger.strongStep("Open Communities and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();

		if (flag)
		{
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}	

		//Post an entry as the community blog owner
		logger.strongStep("Click on the Blog link in the navigation menu");
		log.info("INFO: Select Blog from the navigation menu");
		Community_LeftNav_Menu.BLOG.select(cUI);
		
		logger.strongStep("Click on the New Entry button in the blog page");
		log.info("INFO: Select New Entry button once the blog page opens up");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		logger.strongStep("Create a new entry and click on Post button");
		log.info("INFO: Create a new entry and submit");
		NewEntry1.create(ui);
		
		//Navigate to Overview page
		logger.strongStep("Go to the Community Overview tab");
		log.info("INFO: Navigate to Community Overview tab"); 
		Com_Action_Menu.GOTOCOMMUNITY.select(cUI);
		//cUI.useCommActionMenu(CommActionMenu.GotoCommunity);
		
		logger.strongStep("Verify the Create Blog Entry link is visible within " + cfg.getFluentwaittime() + " seconds");
		log.info("INFO: Validate that the Create Blog Entry link appears within " + cfg.getFluentwaittime() + " seconds"); 
		ui.fluentWaitElementVisible(BlogsUIConstants.blogsNewEntryLink);
	
		//Click the Blog widget menu edit
		logger.strongStep("Select the Edit option from the Widget Actions Menu in the Blog widget");
		log.info("INFO: Click on the Edit option in the Widget Actions Menu of the Blog widget");
		cUI.performCommWidgetAction(BaseWidget.BLOG, Widget_Action_Menu.EDIT);

		logger.strongStep("Edit the Description field and change it to 'New description for community blog'");
		log.info("INFO: Choose the Description field and change it to 'New description for community blog'");
		blog.setDescription("New description for community blog");
		
		logger.strongStep("Edit the Tags field and change it to 'Tag1'");
		log.info("INFO: Click on the Tags field and change it to 'Tag1'");
		blog.setTags("Tag1");
		
		logger.strongStep("Edit the Name field and change the name of the blog to 'New Blog Name'");
		log.info("INFO: Select the Name field and change the name of the blog to 'New Blog Name'");
		blog.setName("New Blog Name");
		
		logger.strongStep("Select the Viewer option for the Member Role");
		log.info("INFO: Click on the Viewer option under Member Role");
		blog.setComMemberRole(MemberRole.VIEWER);

		logger.strongStep("Click on the Save button");
		log.info("INFO: Select the Save button");
		blog.edit(ui, EditVia.WIDGETMENU);
		
		// Scroll up to the top of page
		driver.executeScript("window.scrollTo(0, 0)");
		
		logger.strongStep("Click on the Blog link in the navigation menu");
		log.info("INFO: Select Blog from the navigation menu");
		Community_LeftNav_Menu.BLOG.select(cUI);
		
		logger.strongStep("Verify the new name is displayed as the name of the blog");
		log.info("INFO: Validate the blog name is the new name");
		Assert.assertTrue(driver.isTextPresent(blog.getName()), 
						  "Error: Edit community blog widget is failed.");
		
		//Add Member to this community
		logger.strongStep("Click on the Members link in the navigation menu");
		log.info("INFO: Select Members from the navigation menu");
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		logger.strongStep("Adding " + testUser2.getDisplayName() + " as the member to the community");
		log.info("INFO: Add member " + testUser2.getDisplayName() + "for this community");
		cUI.addMemberCommunity(new Member(CommunityRole.MEMBERS, testUser2));
		
		logger.strongStep("Click on the Save button");
		log.info("INFO: Select Save button"); 
		ui.clickLink(CommunitiesUIConstants.MemberSaveButton);

		logger.strongStep("Log out of and close the session");
		ui.logout();
		ui.close(cfg);
		
		//Login with member user to post entry, make sure the membership is draft user
		logger.strongStep("Open Communities and login: " +testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		//Go to I am Member view, to verify the community shows in this view
		logger.strongStep("Switch to I'm a Member view");
		log.info("INFO: Navigate to I'm a Member View.");
		Community_View_Menu.IM_A_MEMBER.select(cUI);

		logger.strongStep("Verify the Create a Community drop down appears");
		log.info("INFO: Wait for Create a Community drop down to be visible"); 
		ui.fluentWaitElementVisible(CommunitiesUIConstants.StartACommunityDropDownCardView);
		
		logger.strongStep("Verify the community card is visible in the current view which is I'm a Member");
		log.info("INFO: Validate that the community card is in I'm a Member view");
		Assert.assertTrue(driver.isElementPresent("css=div[aria-label='" + community.getName() + "']"), 
						  "Error: Cannot find the community in I am Member view.");
		
		//Go to the community blog page, verify testUser2 cannot post entry
		logger.strongStep("Open the community by clicking on its card");
		log.info("INFO: Navigate to the community " + community.getName());
		ui.clickLinkWait("css=div[aria-label='" + community.getName() + "']");

		logger.strongStep("Verify the Create Blog Entry link is not visible");
		log.info("INFO: Validate that the Create Blog Entry link does not appear");
		Assert.assertFalse(driver.isElementPresent(BlogsUIConstants.blogsNewEntryLink),
				           "Error: Viewer member can post entry.");
		
		//comment on an entry, verify the comment be moderated
		logger.strongStep("Click on the link for the entry in the Blog widget which was created by " +testUser1.getDisplayName());
		log.info("INFO: Select the entry in the Blog widget which was created by " +testUser1.getDisplayName()); 
		ui.scrollIntoViewElement("link=" + NewEntry1.getTitle());
		ui.clickLinkWait("link=" + NewEntry1.getTitle());

		logger.strongStep("Add a comment and submit");
		log.info("INFO: Create a comment and click on Submit button");
		comment.create(ui);
		
		logger.strongStep("Log out of and close the session");
		ui.logout();
		ui.close(cfg);
		
		//testUser1 delete the community
		logger.strongStep("Open Communities and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
			
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	
		
		logger.strongStep("Delete the community");
		log.info("INFO: Delete the community"); 
		cUI.delete(community, testUser1);

		ui.endTest();		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Tests to verify that a user can't vote for a duplicate idea in an Ideation Blog.
	*<li><B>Step: </B>Login as a user.
	*<li><B>Step: </B>Add Ideation Blog widget to communities.
	*<li><B>Step: </B>Post the first idea for this Ideation Blog.
	*<li><B>Step: </B>Vote for the first idea.
	*<li><B>Verify: </B>The vote number is 1.
	*<li><B>Step: </B>Post the second idea for this ideation blog with duplicate form data from idea 1, except for the name.
	*<li><B>Step: </B>Mark the idea as duplicate.
	*<li><B>Verify: </B>The Mark as Duplicate header appears.
	*<li><B>Step: </B>Enter the name of the first idea in the 'Find the duplicate idea:' text box.
	*<li><B>Verify: </B>The vote number for the duplicate idea is 1.
	*<li><B>Verify: </B>The tag and content of the duplicate idea is the same as the first idea.
	*<li><B>Verify: </B>The user can not vote for the duplicate idea as its Vote button is disabled.
	*<li><B>Step: </B>Click on Save button.
	*<li><B>Verify: </B>The 'Mark as Duplicate' popup appears.
	*<li><B>Step: </B>Click on the OK button.
	*<li><B>Verify: </B>The message 'The idea has been marked as duplicated.' is displayed.
	*</ul>
	*/ 
	@Test(groups = {"regression"})
	public void stopVotinginduplicateidea() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test duplicate ideas for " + testName)
									 			   .build();
		//for creating the ideation blog
		BaseBlogPost NewIdea1 = new BaseBlogPost.Builder("First Idea " + Helper.genDateBasedRand())
				   							.tags("IdeaTag" + Helper.genDateBasedRand())
				   							.content("BVT Ideation blog content").build();
		
		//for creating the ideation blog
		BaseBlogPost NewIdea2 = new BaseBlogPost.Builder("Second Idea " + Helper.genDateBasedRand())
				   							.tags("IdeaTag" + Helper.genDateBasedRand())
				   							.content("BVT Ideation blog content").build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
				
		//Login as a user
		logger.strongStep("Open Communities and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	

		//Customize community - Add ideation blog widget to communities
		logger.strongStep("Add the Ideation Blog widget to the community");
		log.info("INFO: Adding the " + BaseWidget.IDEATION_BLOG.getTitle() + " widget to community: "+ community.getName());
		cUI.addWidget(BaseWidget.IDEATION_BLOG);
		
		//Post the first idea for this ideation blog.
		logger.strongStep("Click on the 'Create Your First Idea' link in the Ideation Blog widget");
		log.info("INFO: Select the 'Create Your First Idea' link from the Ideation Blog widget");
		ui.clickLinkWait(BlogsUIConstants.FirstIdeaLink);
		
		logger.strongStep("Create a new idea and click on Post button");
		log.info("INFO: Create a new idea and submit");
		NewIdea1.create(ui);
		
		//Vote for the first idea
		logger.strongStep("Click on the Vote button");
		log.info("INFO: Select the Vote button");
		ui.clickLinkWait(BlogsUIConstants.VoteBtn);

		//verify the vote number is 1
		logger.strongStep("Validate that the vote count is now equal to 1");
		log.info("INFO: Verify the vote count has changed to 1");
		String sNumber = driver.getSingleElement(BlogsUIConstants.VoteNumber).getText();
		Assert.assertTrue(sNumber.contains("1"), 
						  "Error: the vote number dose not equal 1 after one user votes on it.");	
		
		//Post the second idea for this ideation blog
		logger.strongStep("Click on the New Idea button");
		log.info("INFO: Select the New Idea button");
		ui.clickLinkWait(BlogsUIConstants.Ideation_NewIdea);

		logger.strongStep("Create another idea and click on Post button");
		log.info("INFO: Create another idea and submit");
		NewIdea2.create(ui);
		
		logger.strongStep("Click on the More Actions menu");
		log.info("INFO: Select the More Actions menu");
		ui.clickLinkWait(BlogsUIConstants.MoreActions);

		logger.strongStep("Select the Mark as Duplicate option");
		log.info("INFO: Click on the Mark as Duplicate option");
		ui.clickLinkWait(BlogsUIConstants.MarkAsDuplicate);

		logger.strongStep("Verify the Mark as Duplicate header appears");
		log.info("INFO: Validate that the Mark as Duplicate header is visible");
		ui.fluentWaitElementVisible(BlogsUIConstants.MarkAsDuplicateMsg);
		
		logger.strongStep("Enter the name of the first idea in the 'Find the duplicate idea:' text box");
		log.info("INFO: Input the name of the first idea in the 'Find the duplicate idea:' text box"); 
		ui.clearText(BlogsUIConstants.DuplicateIdeaTextbox);
		ui.typeTextWithDelay(BlogsUIConstants.DuplicateIdeaTextbox, NewIdea1.getTitle());
		
		logger.strongStep("Click on the idea in the typeahead");
		log.info("INFO: Selecting idea from the typeahead");
		ui.fluentWaitElementVisible(BlogsUIConstants.DuplicateIdeaTypeahead);
		ui.clickLinkWait(BlogsUIConstants.DuplicateIdeaTypeahead);
		
		logger.strongStep("Verify " + NewIdea1.getTitle() + " is displayed as a duplicate entry");
		log.info("INFO: Validate " + NewIdea1.getTitle() + " shows as a duplicate entry");
		ui.fluentWaitElementVisible(BlogsUI.duplicateEntryTitle(NewIdea1));
		
		logger.strongStep("Verify the tag of the duplicate idea is the same as the first idea");
		log.info("INFO: Validate the tag of the duplicate idea appears the same as the first idea");
		Assert.assertTrue(driver.isElementPresent(BlogsUI.duplicateEntryTag(NewIdea1)), 
						  "ERROR: the tag for duplicate idea dose not display in duplicate idea's view.");
		
		logger.strongStep("Verify the vote count for the duplicate idea is 1");
		log.info("INFO: Validate the duplicate idea shows as having one vote");
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.ideaVoteNumber).getText(), "1",
							"ERROR: The duplicate idea has the incorrect number of votes");
		
		logger.strongStep("Verify voting is disabled for the duplicate idea");
		log.info("INFO: Validate voting is disabled for the duplicate idea");
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.ideaVoteDisable).getAttribute("aria-pressed"), "true",
							"ERROR: Voting is not disabled for the dupliacte idea");
		
		logger.strongStep("Click on Show Details link");
		log.info("INFO: Select Show Details link"); 
		ui.clickLinkWait(BlogsUIConstants.DuplicateShowDetail);
		
		logger.strongStep("Verify the duplicate idea's content is the same as that of the first idea");
		log.info("INFO: Validate the duplicate idea has the same content as the first idea");
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.ideaContent).getText(), NewIdea1.getContent(),
							"ERROR: The duplicate idea's content is incorrect");
		
		logger.strongStep("Click on Save button");
		log.info("INFO: Select Save button");
		ui.clickLinkWait(BlogsUIConstants.DuplicateIdeaSaveBtn);
		
		logger.strongStep("Verify 'Mark as Duplicate' popup appears");
		log.info("INFO: Validate 'Mark as Duplicate' popup is displayed"); 
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.DuplicateConfirmWin),
						  "ERROR: duplicate idea confirm window is not display after clicking Save button.");
		
		logger.strongStep("Click on the OK button");
		log.info("INFO: Select OK button");
		ui.clickLinkWait(BlogsUIConstants.BlogsNewEntryAddTagsOK);
		
		logger.strongStep("Verify that the message 'The idea has been marked as duplicated.' is displayed");
		log.info("INFO: Validate that the message 'The idea has been marked as duplicated.' appears");
		Assert.assertTrue(driver.isTextPresent("The idea has been marked as duplicated."), 
						  "ERROR: Duplicate idea confirm message dose not display after idea be marked as duplicated.");
		
	}
	
}
