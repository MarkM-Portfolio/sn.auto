package com.ibm.conn.auto.tests.GDPR;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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

@Deprecated //The GDPR component is obsolete now, hence this class has been deprecated
public class IdeationBlogs_GDPR_DataPop extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(IdeationBlogs_GDPR_DataPop.class);
	private TestConfigCustom cfg;
	private User testUser1, testUser2;
	private CommunitiesUI commUI;
	private BlogsUI blogsUI;
	private APICommunitiesHandler apiCommOwner1,apiCommOwner2;
	private String serverURL;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();	
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		blogsUI = BlogsUI.getGui(cfg.getProductName(), driver);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {		

		apiCommOwner1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiCommOwner2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());

	}
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B>Data Population: UserA Creates & Edits an Idea</li>
	 *<li><B>Step:</B> UserA creates a community & adds the Ideation Blog widget via API</li>
	 *<li><B>Step:</B> UserA creates & edits the idea</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userACreatesAndEditsIdea() {
		
		String testName=commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
		                                           .access(Access.PUBLIC)
		                                           .description("GDPR data pop: UserA creates and edits an idea")
		                                           .build();

		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("GDPR: Test Idea - " + testName + Helper.genDateBasedRandVal())
		                                                 .content("GDPR data pop: This idea will be edited by UserA")
		                                                 .build();	

		userACreatesCommunityAndAddsIdeationWidget(community);

		log.info("INFO: Login to Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		createNewIdea(community, ideationBlogEntry);

		editIdea();
		
		log.info("INFO: Logout of Communities");
		commUI.logout();
		commUI.close(cfg);
	
		commUI.endTest();
}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population: UserA Creates an Idea & Adds a Comment</li>
	 *<li><B>Step:</B> UserA creates an Internal Restricted community & adds the Ideation Blog widget via API</li>
	 *<li><B>Step:</B> UserA creates an idea</li>
	 *<li><B>Step:</B> UserA adds a comment to the idea</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAAddsCommentToIdea() {
		
		String testName=commUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
		                                           .access(Access.RESTRICTED)
		                                           .description("GDPR data pop: UserA creates an idea & comments on the idea")
                                                   .shareOutside(false)                                        
		                                           .build();

		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("GDPR: Test Idea - " + testName + Helper.genDateBasedRandVal())
		                                                 .content("GDPR data pop - UserA will add a comment to this idea")
		                                                 .build();	

		userACreatesCommunityAndAddsIdeationWidget(community);

		log.info("INFO: Login to Communities as the community member: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		createNewIdea(community, ideationBlogEntry);

		addCommentToIdea();
		
		log.info("INFO: Logout of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();
	}
	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population: UserA Creates an Ideation Blog</li>
	 *<li><B>Step:</B> UserA creates a community & adds the Ideation Blog widget via API</li>
	 *<li><B>Step:</B> UserA creates an Ideation Blog</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userACreatesIdeationBlog() {
		
        String testName=commUI.startTest();
        String ideationName="GDPR: Ideation Blog created by UserA " + Helper.genDateBasedRand();
        String ideationDesc="GDPR: test description ";
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                     .access(Access.PUBLIC)
                                     .description("GDPR data pop - UserA creates an ideation blog ")
                                     .build();
					
		

		userACreatesCommunityAndAddsIdeationWidget(community);
		
		log.info("INFO: Login to Communities as user: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);	

		log.info("INFO: Select Ideation blogs from the navigation menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(commUI);

		log.info("INFO: Click on the Start an Ideation Blog button");
		commUI.clickLinkWait("css=span[class='lotusBtn lotusLeft'] a:contains(Start an Ideation Blog)");
		
		log.info("INFO: Input content into the create form & Save");
		driver.getSingleElement(BlogsUIConstants.createIdeaBlogNameField).clear();
		driver.getSingleElement(BlogsUIConstants.createIdeaBlogNameField).type(ideationName);
		driver.getSingleElement(BlogsUIConstants.createIdeaBlogDescField).clear();
		driver.getSingleElement(BlogsUIConstants.createIdeaBlogDescField).type(ideationDesc);
		
		log.info("INFO: Click on the Save button");
		commUI.clickLinkWait(BlogsUIConstants.saveButton);
		
		log.info("INFO: Logout of Communities");
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population: UserB Creates a Community & UserA adds an Idea</li>
	 *<li><B>Step:</B> UserB creates a community & adds the Ideation Blog widget via API</li>
	 *<li><B>Step:</B> UserA creates an idea</li>
	 *</ul>
	 */
	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBCreatesCommunityUserAAddsIdea() {
		
		String testName=commUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
		                                           .access(Access.PUBLIC)
		                                           .description("GDPR data pop: UserB creates a community, UserA adds an ideation entry ")
		                                           .addMember(new Member(CommunityRole.MEMBERS, testUser1))
		                                           .build();

		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("GDPR: Test Idea - " + testName + Helper.genDateBasedRandVal())
		                                                 .content("GDPR data pop - idea created by UserA ")
		                                                 .build();	

		userBCreatesCommunityAndAddsIdeationWidget(community);

		log.info("INFO: Login to Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		createNewIdea(community, ideationBlogEntry);
		
		log.info("INFO: Logout of as UserA");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();
	}

	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - UserB Creates Idea & UserA Edits the Idea</li>
	*<li><B>Step:</B> UserB creates a community & adds UserA as an Owner via the API</li>
	*<li><B>Step:</B> UserB adds the Ideation Blog widget using the API</li>
	*<li><B>Step:</B> UserB creates an idea and Saves</li>
	*<li><B>Step:</B> UserA edits the idea</li>
	*</ul>
	*/ 
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBCreatesIdeaUserAEditsIdea(){
		
		String testName=commUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("GDPR data pop - UserB creates an idea & UserA edits it")
                                                   .addMember(new Member(CommunityRole.OWNERS, testUser1))
                                                   .build();
					
		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("GDPR: Test Idea - " + testName + Helper.genDateBasedRandVal())
														 .content("GDPR data pop - UserA will edit this idea")
														 .build();	

		userBCreatesCommunityAndAddsIdeationWidget(community);
		
		log.info("INFO: Login to Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);
				
		createNewIdea(community, ideationBlogEntry);
		
		log.info("INFO: Log out as UserB " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Log in as UserA " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);	

		log.info("INFO: Select Ideation blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(commUI);

		log.info("INFO: Select the default ideation blog link");
		commUI.clickLinkWait(blogsUI.getCommIdeationBlogLink(community));
		
		log.info("INFO: Click on the idea entry");
		commUI.clickLinkWait("link=" + ideationBlogEntry.getTitle());
		
		editIdea();
				
		log.info("INFO: Logout of Communities");
		commUI.logout();
		commUI.close(cfg);
				
		commUI.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - UserB Creates Idea & UserA Adds Comment to the Idea</li>
	*<li><B>Step:</B> UserB creates a community & adds UserA as an Owner using the API</li>
	*<li><B>Step:</B> UserB adds the Ideation Blog widget using the API</li>
	*<li><B>Step:</B> UserB creates an idea</li>
	*<li><B>Step:</B> UserA adds a comment to the idea</li>
	*</ul>
	*/ 
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBCreatesIdeaUserAAddsAComment(){
		
		String testName=commUI.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                      .access(Access.PUBLIC)
                                      .description("GDPR data pop - UserB creates an idea & UserA adds a comment to the idea.")
                                      .addMember(new Member(CommunityRole.OWNERS, testUser1))
                                      .build();
					
		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("GDPR: Test Idea - " + testName + Helper.genDateBasedRandVal())
														 .content("GDPR data pop - UserA will add a comment to this idea")
														 .build();	
		
		userBCreatesCommunityAndAddsIdeationWidget(community);
		
		log.info("INFO: Login to Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);
						
		createNewIdea(community, ideationBlogEntry);
		
		log.info("INFO: Logout as UserB " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Login to Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);	

		log.info("INFO: Select Ideation blogs from navigation menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(commUI);

		log.info("INFO: Select the default ideation blog link");
		commUI.clickLinkWait(blogsUI.getCommIdeationBlogLink(community));
		
		log.info("INFO: Select the idea created by UserB");
		commUI.clickLinkWait("link=" + ideationBlogEntry.getTitle());

		addCommentToIdea();
		
		log.info("INFO: Logout of Communities");
		commUI.logout();
		commUI.close(cfg);
						
		commUI.endTest();
	}
	
	/*
	 * userACreatesCommunityAndAddsIdeationWidget will do the following as UserA:
	 * - create the community via API
	 * - get the community UUID
	 * - add the Ideation Blog widget to the community
	 */
	
	private void userACreatesCommunityAndAddsIdeationWidget(BaseCommunity community){
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);

		log.info("INFO: Add ideation blog widget with API");
		community.addWidgetAPI(comAPI, apiCommOwner1, BaseWidget.IDEATION_BLOG);

	}
	
	/*
	 * userACreatesCommunityAndAddsIdeationWidget will do the following as UserB:
	 * - create the community via API
	 * - get the community UUID
	 * - add the Ideation Blog widget to the community
	 */
	
	private void userBCreatesCommunityAndAddsIdeationWidget(BaseCommunity community){
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Add ideation blog widget with API");
		community.addWidgetAPI(comAPI, apiCommOwner2, BaseWidget.IDEATION_BLOG);

	}
	
	/*
	 * createNewIdea will:
	 * - open the community
	 * - click on Ideation Blog tab
	 * - click on the default ideation blog link (created by default when community is created)
	 * - click on the New Idea button
	 * - create a new idea
	 */
	
	private void createNewIdea(BaseCommunity community, BaseBlogPost ideationBlogEntry){
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);	

		log.info("INFO: Select Ideation Blogs from the tabbed nav menu");
		Community_TabbedNav_Menu.IDEATIONBLOG.select(commUI);

		log.info("INFO: Select the default ideation blog link");
		commUI.clickLinkWait(blogsUI.getCommIdeationBlogLink(community));

		log.info("INFO: Select New Idea button");
		commUI.clickLinkWait(BlogsUIConstants.NewIdea);

		log.info("INFO: Creating a new idea");
		ideationBlogEntry.create(blogsUI);

	}
	
	/*
	 * editIdea will:
	 * - click the Edit link
	 * - clear the title field & enter new title
	 * - save the edit
	 */
	
	private void editIdea(){
		log.info("INFO Edit the ideation entry - click on the Edit link");
		commUI.clickLinkWait(BlogsUIConstants.BlogsEditEntry);

		log.info("INFO: Clear the title field");
		commUI.clearText(BlogsUIConstants.BlogsNewEntryTitle);

		log.info("INFO: Enter the new title");
		commUI.typeTextWithDelay(BlogsUIConstants.BlogsNewEntryTitle, Data.getData().SearchScopeEntries);

		log.info("INFO: Save the entry");
		commUI.clickLinkWait(BlogsUIConstants.blogPostEntryID);

	}
	
	/*
	 * addCommentToIdea will:
	 * - click the Add a Comment link
	 * - enter comment into the text field
	 * - save the comment
	 */
	
	private void addCommentToIdea(){
		log.info("INFO: Click on the Add a Comment link");
		commUI.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);

		log.info("INFO: Wait for comment text area to be present");
		commUI.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrame);

		log.info("INFO: Type the comment into the text area");
		commUI.typeInCkEditor(Data.getData().BlogsCommentText);

		log.info("INFO: Submit comment");
		commUI.clickLinkWithJavascript(BlogsUIConstants.BlogsSubmitButton);
	}

}
