package com.ibm.conn.auto.tests.metrics_elasticSearch;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class CommunitiesDataPop extends SetUpMethods2 {

private static Logger log = LoggerFactory.getLogger(CommunitiesDataPop.class);
private TestConfigCustom cfg;
private CommunitiesUI ui;	
private BlogsUI blogsUI;
private FilesUI filesUI;
private WikisUI wikisUI;	
private ActivitiesUI activitiesUI;
private APICommunitiesHandler apiOwner;
private APIForumsHandler apiForumsOwner;
private APIActivitiesHandler apiActOwner;
private APIWikisHandler apiWikisOwner;
private String serverURL;
private BaseCommunity community;
private User adminUser, testUser1, testUser2, testUser3, testUser4;
private GatekeeperConfig gkc;


@BeforeClass(alwaysRun=true)
public void setUpClass() {

	cfg = TestConfigCustom.getInstance();
	ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
	blogsUI = BlogsUI.getGui(cfg.getProductName(), driver);
	filesUI = FilesUI.getGui(cfg.getProductName(), driver);
	wikisUI = WikisUI.getGui(cfg.getProductName(), driver);
	activitiesUI = ActivitiesUI.getGui(cfg.getProductName(), driver);

	//Load Users
	testUser1 = cfg.getUserAllocator().getUser();
	testUser2 = cfg.getUserAllocator().getUser(); 
	testUser3 = cfg.getUserAllocator().getUser();
	testUser4 = cfg.getUserAllocator().getUser();
	adminUser = cfg.getUserAllocator().getAdminUser();
	
	serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	apiForumsOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	apiActOwner = new APIActivitiesHandler(cfg.getProductName(), serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	apiWikisOwner = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	
	gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
	URLConstants.setServerURL(serverURL);
	
		
}	
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population - Public Community: Add & Remove user(s)</li>
	 *<li><B>Step:</B> Create public community with (2) additional users via the API</li>
	 *<li><B>Info:</B> (1)additional user has 'Member' access & the other has 'Owner' access</li>
	 *<li><B>Step:</B> Navigate to the full Members page</li>
	 *<li><B>Step:</B> Remove the user with 'Owner' access from the community</li>  
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void publicCommRemoveAdditionalOwner() {
		
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		Member member1 = new Member(CommunityRole.OWNERS, testUser3);
		
		community = new BaseCommunity.Builder("publicCommRemoveAdditionalOwner " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Create a Public community.  Add (1) user with 'Member' access & (1) with 'Owner' access. Remove user with 'Owner' access. ")
                                      .addMember(member)
                                      .addMember(member1)
                                      .build();
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
				
		log.info("INFO: Open the community");
		community.navViaUUID(ui);
		
		log.info("INFO: Navigate to the Members full widget page");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		log.info("INFO: Remove the member with 'Owner' access"); 
		ui.removeMemberCommunity(member1);
	
		ui.logout();
		ui.close(cfg);	
		ui.endTest();
	
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population - Moderated Community: Add & Remove user(s)</li>
	 *<li><B>Step:</B> Create a moderated community with (2) additional users via the API</li>
	 *<li><B>Info:</B> (1)additional user has 'Member' access & the other has 'Owner' access</li>
	 *<li><B>Step:</B> Navigate to the full Members page</li>
	 *<li><B>Step:</B> Remove the user with 'Member' access from the community</li> 
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void moderatedCommRemoveMember() {
		
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		Member member1 = new Member(CommunityRole.OWNERS, testUser3);
		
		community = new BaseCommunity.Builder("moderatedCommRemoveMember " + Helper.genDateBasedRandVal())
                                      .access(Access.MODERATED)
                                      .description("Create a Moderated community.  Add (1) user with 'Member' access & (1) with 'Owner' access. Remove user with 'Member' access")
                                      .addMember(member)
                                      .addMember(member1)
                                      .build();
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
				
		log.info("INFO: Open the community");
		community.navViaUUID(ui);
		
		log.info("INFO: Navigate to the Members full widget page");		
		Community_LeftNav_Menu.MEMBERS.select(ui);
		
		log.info("INFO: Remove the member with 'Member' access"); 
		ui.removeMemberCommunity(member);
	
		ui.logout();
		ui.close(cfg);	
		ui.endTest();
	
	}
	
		
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population - Restricted Community with an additional 'Member'</li>
	 *<li><B>Step:</B> Create a restricted community with (1) additional users via the API</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void restrictedCommMemberAdded() {
		
		Member member = new Member(CommunityRole.MEMBERS, testUser2);		
		
		community = new BaseCommunity.Builder("restrictedCommMemberAdded " + Helper.genDateBasedRandVal())
	                                  .access(Access.RESTRICTED)
	                                  .description("Create a Restricted community.  Additional member with 'Member' access is added. ")
	                                  .addMember(member)
	                                  .shareOutside(false)
	                                  .build();
		
		log.info("INFO: Create community using API");
		community.createAPI(apiOwner);
		
		ui.endTest();
	
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population - (2) Users join a public community</li>
	 *<li><B>Step:</B> A Public community is created via the API</li>
	 *<li><B>Step:</B> UserA joins the community</li>
	 *<li><B>Step:</B> UserB joins the community</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void usersJoinPublicCommunity() {
		
		community = new BaseCommunity.Builder("usersJoinPublicComm " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Create a Public community. (2) users join the Public community. ")
                                      .build();
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		log.info("INFO: Log into Communities as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
				
		log.info("INFO: Open the community");
		community.navViaUUID(ui);
		
		log.info("INFO: Click on the Join this Community link");
		ui.clickLinkWait(CommunitiesUIConstants.Join_the_Community);
				
		log.info("INFO: Logout as: " + testUser2.getDisplayName());
		ui.logout();
		
		log.info("INFO: Log into Communities as: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser3);	

		log.info("INFO: Open the community");
		community.navViaUUID(ui);

		log.info("INFO: Click on the Join this Community link");
		ui.clickLinkWait(CommunitiesUIConstants.Join_the_Community);

		ui.logout();
		ui.close(cfg);	
		ui.endTest();

	}
	
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population - Additional Members Leave Restricted Community</li>
	 *<li><B>Step:</B> Restricted community with (2) additional members is created via the API</li>
	 *<li><B>Info:</B> (1)additional user has 'Member' access & the other has 'Owner' access</li>
	 *<li><B>Step:</B> UserA leaves the community</li>
	 *<li><B>Step:</B> UserB leaves the community</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void usersLeaveRestrictedCommunity() {
		
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		Member member1 = new Member(CommunityRole.OWNERS, testUser3);
		
		community = new BaseCommunity.Builder("usersLeaveRestrictedComm " + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description("Create a Restricted community. Add (1) user with 'Member' access & (1) with 'Owner' access. Both users leave the community.")                                  
                                      .addMember(member)
                                      .addMember(member1)
                                      .shareOutside(false)
                                      .build();
		
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		log.info("INFO: Log into Communities as user with 'Member' access: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
				
		log.info("INFO: Open the community");
		community.navViaUUID(ui);
		
		log.info("INFO: Select Leave Community from the community actions menu");
		Com_Action_Menu.LEAVE.select(ui);
		
		log.info("INFO: Click the OK button on the leave community message");
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
		ui.clickLinkWait(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
		
		log.info("INFO: Log out user: " + testUser2.getDisplayName());
		ui.logout();
		
		log.info("INFO: Log into Communities as user with 'Owner' access: " + testUser3.getDisplayName());		
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser3);
		
		log.info("INFO: Open the community");
		community.navViaUUID(ui);
		
		log.info("INFO: Select Leave Community from the community actions menu");
		Com_Action_Menu.LEAVE.select(ui);
		
		log.info("INFO: Click the OK button on the leave community message");
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
		ui.clickLinkWait(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
		
		ui.logout();
		ui.close(cfg);		
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population -  Public Community with (2) Status Updates entry</li>
	 *<li><B>Step:</B> Public community is created via the API</li>
	 *<li><B>Step:</B> (2) entries with text only are posted to the Status Updates page</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void publicCommWithStatusUpdatesEntry(){
		
		community = new BaseCommunity.Builder("publicCommWithStatusUpdatesEntry " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Create a Public community.  Post(2)text only entrries to Status Updates page.")
                                      .build();		
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		log.info("INFO: Click on Status Updates ");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);

		log.info("INFO: Type the Status messge");
		ui.typeMessageInShareBox(Data.getData().commonStatusUpdate.trim(), true);

		log.info("INFO: Scroll to the top of the page");
		driver.clickAt(0, 0);

		log.info("INFO: Posting of Status Message");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);

		log.info("INFO: Verify the Message has been posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
				"ERROR: The successfull message is not posted");
		
		log.info("INFO: Type the Status messge");
		ui.typeMessageInShareBox(Data.getData().UpdateStatus.trim(), true);

		log.info("INFO: Scroll to the top of the page");
		driver.clickAt(0, 0);

		log.info("INFO: Posting of Status Message");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);

		log.info("INFO: Verify the Message has been posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
				"ERROR: The successfull message is not posted");

		ui.logout();
		ui.close(cfg);	
		ui.endTest();	

	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population - Restricted Community: Owner edits the community & Member adds a bookmark</li>
	 *<li><B>Step:</B> A Restricted community with (2) additional members is created via the API</li>
	 *<li><B>Info:</B> (1)additional user has 'Member' access & the other has 'Owner' access</li>
	 *<li><B>Step:</B> The additional Owner edits the community - adds (2) tags</li>
	 *<li><B>step:</B> The Member adds a Bookmark</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void ownerEditsCommMemberAddsBookmark() {
		
		Member member = new Member(CommunityRole.MEMBERS, testUser2);
		Member member1 = new Member(CommunityRole.OWNERS, testUser3);
		
		community = new BaseCommunity.Builder("ownerEditsCommMemberAddsBookmark " + Helper.genDateBasedRandVal())
                                      .access(Access.RESTRICTED)
                                      .description("Create a Restricted community. Add (1) user with 'Member' access & (1) with 'Owner' access. Users edit community & add a bookmark. ")
                                      .addMember(member)
                                      .addMember(member1)
                                      .shareOutside(false)
                                      .build();
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		log.info("INFO: Log into Communities as user with 'Member' access: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
				
		log.info("INFO: Open the community");
		community.navViaUUID(ui);

		log.info("INFO: Click on the Add Your First Bookmark link");
		ui.clickLinkWait(CommunitiesUIConstants.AddYourFirsBookMark);

		log.info("INFO: Enter a bookmark URL");
		driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkURL).type(Data.getData().BookmarkURL);
		
		log.info("INFO: Enter a bookmark name");
		driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkName).type(Data.getData().BookmarkName);
		
		log.info("INFO: Enter a description for the bookmark");
		driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkDescription).type(Data.getData().BookmarkDesc);
		
		log.info("INFO: Enter a tag for the bookmark");
		driver.getFirstElement(CommunitiesUIConstants.EnterBookmarkTag).type(Data.getData().BookmarkTag);
		
		log.info("INFO: Click the Save button");
		driver.getFirstElement(CommunitiesUIConstants.SaveButtonEntry).click();

		log.info("INFO: Log out user with 'Member' access: " + testUser2.getDisplayName());
		ui.logout();
		
		log.info("INFO: Log into Communities as user with 'Owner' access: " + testUser3.getDisplayName());		
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser3);
		
		log.info("INFO: Open the community");
		community.navViaUUID(ui);
		
		log.info("INFO: Select Edit Community from the community actions menu");
		Com_Action_Menu.EDIT.select(ui);
		
		log.info("INFO: Edit the community by adding a tag");
		driver.getSingleElement(CommunitiesUIConstants.CommunityTag).clear();
		driver.getSingleElement(CommunitiesUIConstants.CommunityTag).type(Data.getData().MultiFeedsTag);
		
		log.info("INFO: Click Save button");
		ui.clickLinkWait(CommunitiesUIConstants.editCommunitySaveButton);
		
		ui.logout();
		ui.close(cfg);	
		ui.endTest();

	}
		
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population - Public Community: Multiple users follow the community</li>
	 *<li><B>Step:</B> A Public community is created via the API</li>
	 *<li><B>Step:</B> UserA logs into Communities and follows the community</li>
	 *<li><B>Step:</B> UserA logs out</li>
	 *<li><B>Step:</B> UserB logs into Communities and follows the community</li>
	 *<li><B>Step:</B> UserB logs out</li>
	 *<li><B>Step:</B> UserC logs into Communities and follows the community</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void multipleUsersFollowCommunity() {
		
		community = new BaseCommunity.Builder("multipleUsersFollowCommunity " + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("Create a Public community.  (3) different users follow the community.")
                                      .build();
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		log.info("INFO: UserA logs into Communities: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
				
		log.info("INFO: Open the community");
		community.navViaUUID(ui);
		
		log.info("INFO: Click on the 'Follow this Community' link");
		ui.clickLinkWait(CommunitiesUIConstants.FollowThisCommunity);
		
		log.info("Logout as UserA: " + testUser2.getDisplayName());
		ui.logout();
		
		log.info("UserB logs into Communities: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser3);
		
		log.info("INFO: Open the community");
		community.navViaUUID(ui);
		
		log.info("INFO: Click on the 'Follow this Community' link");
		ui.clickLinkWait(CommunitiesUIConstants.FollowThisCommunity);
		
		log.info("Logout as UserB: " + testUser3.getDisplayName());
		ui.logout();
		
		log.info("UserC logs into Communities: " + testUser4.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUser4);
		
		log.info("INFO: Open the community");
		community.navViaUUID(ui);
		
		log.info("INFO: Click on the 'Follow this Community' link");
		ui.clickLinkWait(CommunitiesUIConstants.FollowThisCommunity);
		
		ui.logout();
		ui.close(cfg);	
		ui.endTest();
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population - Community with Special Characters </li>
	 *<li><B>Step:</B> Create a Public community with special characters in the title using API</li>
	 *<li><B>Step:</B> Using the API, add the following widgets: Blogs, Ideation Blogs, Wikis</li>
	 *<li><B>Step:</B> As the community owner (UserA), edit the community tag</li>
	 *<li><B>Step:</B> As UserA navigate to the Blogs full widget page & post a new blog entry</li>  
	 *<li><B>Step:</B> As UserA navigate to Ideation Blogs full widget page & post a new idea</li>
	 *<li><B>Step:</B> Log out as UserA & log in as UserB</li>
	 *<li><B>Step:</B> As UserB navigate to Wikis & follow the wiki</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"} , enabled=false )
	public void communityWithSpecialChars() {
		
		String testName = ui.startTest();
				
		community = new BaseCommunity.Builder(testName + " " + Data.getData().specialCharacter + " " + Helper.genDateBasedRandVal())
                                     .access(Access.PUBLIC)
                                     .tags(Data.getData().BlogsNewEntryTag)
                                     .description("Community & some widgets with special characters in the name.")
                                     .build();
		
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("Blog entry " + Data.getData().specialCharacter + Helper.genDateBasedRandVal())
		                                         .tags("blogTag" + Helper.genDateBasedRand())
		                                         .content("Test blog entry ")
		                                         .build();	
		
		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("Ideation Blog entry " + Data.getData().specialCharacter + Helper.genDateBasedRandVal())
		                                                 .tags("IdeaTag" + Helper.genDateBasedRand())
		                                                 .content("ideation blog new idea test ")
		                                                 .build();	
				
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Add the Blogs widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
		
		log.info("INFO: Add the Ideation Blogs widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.WIKI);
		}
	
		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
				
		log.info("INFO: Open the community");
		community.navViaUUID(ui);
		
		//This section edits the community tag		
		log.info("INFO: Select Edit Community from the Community Actions menu");
		Com_Action_Menu.EDIT.select(ui);
		
		log.info("INFO: Edit the community tag");		
		driver.getSingleElement(CommunitiesUIConstants.CommunityTag).clear();
		driver.getSingleElement(CommunitiesUIConstants.CommunityTag).type(Data.getData().editedData + Data.getData().BlogsNewEntryTag);
		
		log.info("INFO: Save the change made to the community");
		ui.clickLinkWait(CommunitiesUIConstants.editCommunitySaveButton);
		
		log.info("INFO: Check for the Stop following Communities button to make sure page is loaded");
		ui.isElementPresent(CommunitiesUIConstants.StopFollowingThisCommunity);
		
		log.info("INFO: Verify the updated community tag displays");
		System.out.println("link=" + Data.getData().editedData .toLowerCase() + Data.getData().BlogsNewEntryTag .toLowerCase());
		Assert.assertTrue(ui.isElementPresent("link=" + Data.getData().editedData .toLowerCase() + Data.getData().BlogsNewEntryTag .toLowerCase()),
				"ERROR: The updated community tag does not appear");
				
		//This section adds an entry to Blogs
		log.info("INFO: Select Blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.BLOG.select(ui);
		
		log.info("INFO: Select New Entry button");
		ui.fluentWaitElementVisibleOnce(BlogsUIConstants.blogsNewEntryMenuItem);
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		log.info("INFO: Add a new entry to the blog");
		blogEntry.create(blogsUI);

		log.info("INFO: Verify that the new blog entry exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(blogEntry.getTitle()), 
				"ERROR: New blog entry is not found");
		
		//This section adds an idea to Ideation Blogs
		log.info("INFO: Select Ideation blogs from the navigation menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui);
		
		log.info("INFO: Click on the Contribute an Idea button");
		ui.clickLinkWait(BlogsUIConstants.contributeAnIdeaButton);
		
		log.info("INFO: Create a new idea");
		ideationBlogEntry.create(blogsUI);
				
		log.info("INFO: Verify that the new idea exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(ideationBlogEntry.getTitle()), 
				"ERROR: New ideation entry is not found"); 
		
		log.info("INFO: Log out as user: " + testUser1.getDisplayName());
		ui.logout();
		
		//In this section a user follows the wiki 		
		log.info("INFO: Log into Communities as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser2);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);		
		
		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(ui);
		
		log.info("INFO: Click on the Following Actions menu");
		ui.clickLinkWait(WikisUIConstants.Follow_Button);
		
		log.info("INFO: Click on the Follow this Wiki link");
		ui.clickLinkWait(WikisUIConstants.Start_Following_this_wiki);
		
		log.info("INFO: Verify the follow wiki confirmation message displays");
		Assert.assertTrue(driver.isTextPresent(WikisUIConstants.Follow_Wiki_Message),
				"ERROR: The follow wiki confirmation message does not display");
		
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population - Community with Lots of Content</li>
	 *<li><B>Info:</B> Populate community with enough data so it can be used to test many of the Community level Metrics reports</li>
	 *<li><B>Step:</B> Create a Public community with (2) members using API</li>
	 *<li><B>Step:</B> Using the API, add the following widgets: Blogs, Ideation Blogs, Wikis, and Activities</li>
	 *<li><B>Step:</B> Create & edit a Blog entry</li>
	 *<li><B>Step:</B> Create, edit & graduate an Ideation Blog entry</li>  
	 *<li><B>Step:</B> Upload & edit a file</li>
	 *<li><B>Step:</B> Create & edit a forum topic</li>
	 *<li><B>Step:</B> Add & edit a bookmark</li>
	 *<li><B>Step:</B> Create (2) community activities</li>
	 *<li><B>Step:</B> Create & edit a wiki page</li>
	 *<li><B>Step:</B> A community member leaves the community</li>
	 *</ul>
	 */	
	
	@Test(groups = { "regression", "regressioncloud"} , enabled=false )
	public void communityWithLotsOfContent(){
    	
    	String testName = ui.startTest();
    	
    	Member member = new Member(CommunityRole.MEMBERS, testUser2);
		Member member1 = new Member(CommunityRole.OWNERS, testUser3);
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .addMember(member)
                                      .addMember(member1)
                                      .description("Public community with content added to Blogs, Ideation Blogs,Bookmarks, Files, Forums, Wikis & Activities. ")
                                      .build();
					
		BaseBlogPost blogEntry = new BaseBlogPost.Builder("Blog entry " + Helper.genDateBasedRandVal())
														 .tags("blogTag" + Helper.genDateBasedRand())
														 .content("Test blog entry ")
														 .build();	
		
		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("edit ideation entry " + Helper.genDateBasedRandVal())
		                                                 .tags("IdeaTag" + Helper.genDateBasedRand())
		                                                 .content("edit this ideation blog entry ")
		                                                 .build();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
                                    .comFile(true)
                                    .extension(".jpg")
                                    .build();
			
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
				
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Add the Blogs widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.BLOG);
		
		log.info("INFO: Add the Ideation Blog widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.WIKI);
		}
		
		log.info("INFO: Add the 'Activities' widget to the Community using API");
        community.addWidgetAPI(comAPI, apiOwner, BaseWidget.ACTIVITIES);
		
		log.info("INFO: Login to Communities as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);	

		//BLOGS:  this section will add & edit a blog entry
		log.info("INFO: Select Blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.BLOG.select(ui);
		
		log.info("INFO: Select New Entry button");
		ui.fluentWaitElementVisibleOnce(BlogsUIConstants.blogsNewEntryMenuItem);
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		log.info("INFO: Add a new entry to the blog");
		blogEntry.create(blogsUI);

		log.info("INFO: Verify that the new blog entry exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(blogEntry.getTitle()), 
				"ERROR: Blog entry not found");
		
		log.info("INFO: Click on the Edit link");
		ui.clickLinkWait(BlogsUIConstants.BlogsEditEntry);
		
		log.info("INFO: Clear the title field");
		ui.clearText(BlogsUIConstants.BlogsNewEntryTitle);
		
		log.info("INFO: Enter the new title");
		ui.typeTextWithDelay(BlogsUIConstants.BlogsNewEntryTitle, Data.getData().SearchScopeEntries);
		
		log.info("INFO: Save the entry");
		ui.clickLinkWait(BlogsUIConstants.blogPostEntryID);
		
		log.info("INFO: Verify that the new blog entry exists");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().SearchScopeEntries), 
				"ERROR: Updated Blog entry not found");
		
		//IDEATION BLOG:  this section will add & edit the ideation entry & 'Graduate' the entry
		log.info("INFO: Select Ideation blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(ui);

		log.info("INFO: Select the default ideation blog link");
		ui.clickLinkWait(blogsUI.getCommIdeationBlogLink(community));
		
		log.info("INFO: Select New Idea button");
		ui.clickLinkWait(BlogsUIConstants.NewIdea);
		
		log.info("INFO: Creating a new idea");
		ideationBlogEntry.create(blogsUI);
		
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
		
		log.info("INFO: Clicking on 'Graduate' to graduate the idea");
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduate);
		
		log.info("INFO: Clicking 'OK' in the pop up dialog box to confirm idea graduation");
		ui.clickLinkWait(BlogsUIConstants.BlogsGraduateOK);
		
		log.info("INFO: Verify that the graduation successful message is displayed");
		ui.fluentWaitTextPresent(Data.getData().IdeaGraduatedMsg);
		
		//FILES:  this section will upload & edit a file
		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui);
		
		log.info("INFO: Add a new file: " + file.getName());
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
			file.upload(filesUI,gkc);
		else
			file.upload(filesUI);		
		
		log.info("INFO: Validate file upload message is present");
		if (!ui.fluentWaitTextPresent(Data.getData().UploadMessage)){
			log.info("INFO: Message not present, clicking upload link and checking for message again");
			filesUI.reClickUploadLink(file, gkc);
			ui.fluentWaitTextPresent(Data.getData().UploadMessage);
		}
								
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
				
		log.info("INFO: Validate that the file is visible");
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectFile(file)),
						  "ERROR: Unable to find the file " + file.getName());
		
		log.info("INFO: Click on the More link for the uploaded file");
		ui.clickLinkWait(FilesUIConstants.moreLink);
		
		log.info("INFO: Click on the More Actions link");
		ui.clickLinkWait(FilesUIConstants.filesMoreActionsBtn);
		
		log.info("INFO: Click on Edit Properties");
		ui.clickLinkWait(FilesUIConstants.EditPropertiesOption);

		ui.fluentWaitTextPresent(Data.getData().editPropertiesDialogBoxTitle);
		ui.clearText(FilesUIConstants.editPropertiesName);
		ui.typeText(FilesUIConstants.editPropertiesName, Data.getData().editedFileName);
		ui.clickButton(Data.getData().buttonSave);
		
		log.info("INFO: Verify the updated file name appears");
		Assert.assertTrue(driver.isTextPresent(Data.getData().editedFileName + Data.getData().jpgExtension + Data.getData().EditFilePropertyMsg),
				"ERROR: The updated file name does not appear");
		
		//FORUMS:  this section creates & edits a forum topic
		String commUUID = apiOwner.getCommunityUUID(comAPI);		
		Forum apiForum = apiForumsOwner.getDefaultCommForum(ForumsUtils.getCommunityUUID(commUUID), community.getName());

		BaseForumTopic forumTopic = new BaseForumTopic.Builder(Data.getData().ForumTopicTitle)
		                                              .tags(Data.getData().MultiFeedsTag2)
		                                              .description(Data.getData().commonDescription)
		                                              .partOfCommunity(community)
		                                              .parentForum(apiForum)
		                                              .build();

		BaseForumTopic newForumTopic = new BaseForumTopic.Builder(Data.getData().EditForumTopicTitle)
		                                                 .tags(Data.getData().ForumTopicTag)
		                                                 .description(Data.getData().EditForumTopicContent)
		                                                 .partOfCommunity(community)
		                                                 .parentForum(apiForum)
		                                                 .build();

		log.info("create Topic using API");
		ForumTopic apiForumTopic = forumTopic.createAPI(apiForumsOwner);	
		
		log.info("INFO: Select Forums from left navigation menu");
		Community_TabbedNav_Menu.FORUMS.select(ui);
		
		log.info("INFO: Open the forum topic");
		ui.clickLinkWait("link=" + apiForumTopic.getTitle());

		log.info("INFO: Start to edit the topic");
		ui.clickLinkWait(ForumsUIConstants.EditTopic);

		log.info("INFO: Edit forum topic title");
		ui.clearText(ForumsUIConstants.Start_A_Topic_InputText_Title);
		ui.typeText(ForumsUIConstants.Start_A_Topic_InputText_Title, newForumTopic.getTitle());

		log.info("INFO: Edit forum topic contents");
		ui.typeInCkEditor(newForumTopic.getDescription());

		log.info("INFO: Save the forum topic changes");
		ui.clickSaveButton();
		
		log.info("INFO: Verify the updated forum topic title exists");
		Assert.assertTrue(driver.isTextPresent(Data.getData().EditForumTopicTitle),
				"ERROR: The Topic title " + Data.getData().EditForumTopicTitle + " does not appear.");
		
		log.info("INFO: Verfiy the updated forum topic content exists");
		Assert.assertTrue(driver.isTextPresent(Data.getData().EditForumTopicContent),
				"ERROR: The Topic title " + Data.getData().EditForumTopicContent + " does not appear.");
		
		//BOOKMARKS: this section adds & edits a bookmark
		log.info("INFO: Click on the Overview tab on the top nav. menu");
		Community_TabbedNav_Menu.BOOKMARK.select(ui);
		
		log.info("INFO: Click on the Add Your First Bookmark link");
		ui.clickLinkWait(CommunitiesUIConstants.AddBookmarkButton);

		log.info("INFO: Input Bookmarks URL ");
		driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkURL).type(Data.getData().BookmarkURL);

		log.info("INFO: Input Bookmark Name");
		driver.getSingleElement(CommunitiesUIConstants.EnterBookmarkName).type(Data.getData().BookmarkName);

		log.info("INFO: Save the bookmark");
		driver.getFirstElement(CommunitiesUIConstants.SaveButtonEntry).click();
		
		log.info("INFO: Verify the bookmark was added");
		Assert.assertTrue(driver.isElementPresent("link="+Data.getData().BookmarkName),
				"ERROR: Bookmark: " + Data.getData().BookmarkName + " was not found");
		
		log.info("INFO: Click on More link");
		ui.clickLinkWait(CommunitiesUIConstants.firstBookmarksMoreLink);
		
		log.info("INFO: Click on More Edit Bookmark");
		ui.clickLinkWait(CommunitiesUIConstants.EditLink);

		log.info("INFO: Start editing the bookmark URL & Name");			
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkURL).clear();
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkURL).type(Data.getData().EditBookmarkURL);
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkName).clear();
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkName).type(Data.getData().EditBookmarkName);
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkDescription).clear();
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkDescription).type(Data.getData().commonDescription);

		log.info("INFO: Save the changes in Bookmark");
		ui.clickLink(CommunitiesUIConstants.SaveButtonEntry);

		log.info("INFO: Verify the edited bookmark name appears");
		ui.fluentWaitTextPresent(Data.getData().EditBookmarkName);
		Assert.assertTrue(driver.isElementPresent("link="+Data.getData().EditBookmarkName),
				"ERROR: Bookmark: " + Data.getData().EditBookmarkName + " was not found");
		
		
		//COMMUNITY ACTIVITY:  this section adds (2)community activities
		BaseActivity activity1 = new BaseActivity.Builder("test community activity 1 " + Helper.genDateBasedRand())
		                                         .goal("1st community activity added to community: createCommAddCommActivity ")
		                                         .community(community)
		                                         .build();

        BaseActivity activity2 = new BaseActivity.Builder("test community activity 2 " + Helper.genDateBasedRand())
                                                 .goal("2nd community activity added to community: createCommAddCommActivity ")
                                                 .community(community)
                                                 .build();

        log.info("INFO: Add the 1st community activity using the API");
        activity1.createAPI(apiActOwner, community);

        log.info("INFO: Add the 2nd community activity using the API");
        activity2.createAPI(apiActOwner, community);
		
		
		//WIKIS:  this section adds & edits a wiki page
        BaseWikiPage wikiPage = new BaseWikiPage.Builder(testName + Helper.genDateBasedRand(), PageType.Peer)
                                                .tags("tag1, tag2")
                                                .description("this is a test description for creating a Peer wiki page")
                                                .build();

        BaseWikiPage newWikiPage = new BaseWikiPage.Builder(Data.getData().editedData + wikiPage.getName(), PageType.Peer)
                                                   .tags("updated_tag1, updated_tag2")
                                                   .description("Updating the wiki page with new content")
                                                   .build();
                		
		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(ui);
		
		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(wikisUI);

		log.info("INFO: Edit the current Wiki Page");
		wikisUI.editWikiPage(newWikiPage);

		log.info("INFO: Validate that the wiki page name has been edited");
		Assert.assertTrue(ui.fluentWaitTextPresent(newWikiPage.getName()),
							"ERROR: The edited page name does not appear");
		
		
		//MEMBERS:  in this section a community member leaves the community
		log.info("INFO: Log out user: " + testUser1.getDisplayName());
		ui.logout();
		
		log.info("INFO: Log into Communities as user with 'Owner' access: " + testUser2.getDisplayName());		
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser2);
		
		log.info("INFO: Open the community");
		community.navViaUUID(ui);
		
		log.info("INFO: Select Leave Community from the community actions menu");
		Com_Action_Menu.LEAVE.select(ui);
		
		log.info("INFO: Click the OK button on the leave community message");
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
		ui.clickLinkWait(CommunitiesUIConstants.CommunityLeavingWarningOkButton);
	
		ui.endTest();
    }
}