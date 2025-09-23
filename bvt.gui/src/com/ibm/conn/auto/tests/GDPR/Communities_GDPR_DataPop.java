package com.ibm.conn.auto.tests.GDPR;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FeedsUI;
import com.ibm.conn.auto.webui.ICBaseUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

@Deprecated //The GDPR component is obsolete now, hence this class has been deprecated
public class Communities_GDPR_DataPop extends SetUpMethods2{
	

	private static Logger log = LoggerFactory.getLogger(Communities_GDPR_DataPop.class);
	private TestConfigCustom cfg; ICBaseUI ui;
	private CommunitiesUI commUI;
	private FeedsUI fUI;
	private String serverURL;
	private User testUser1, testUser2;
	private APICommunitiesHandler apiCommOwner1,apiCommOwner2;
	private boolean isOnPremise;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		fUI = FeedsUI.getGui(cfg.getProductName(), driver);

		//Load Users		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
						
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		URLConstants.setServerURL(serverURL);
		
		//check environment to see if on-prem or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
					
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {		
		
		apiCommOwner1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiCommOwner2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
			
	}
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population: UserA Deletes a Community</li>
	 *<li><B>Step:</B> UserA creates a Public community via API</li>
	 *<li><B>Step:</B> UserA deletes the community</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userACreatesAndDeletesComm(){

		String testName = commUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRand())
		                                           .access(Access.PUBLIC)	
		                                           .description("GDPR data pop - UserA creates & deletes the community ")
		                                           .build();

		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of the community");
		community.getCommunityUUID_API(apiCommOwner1, commAPI);

		log.info("INFO: Log into Communities as UserA " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Removing community: " + testName );
		community.delete(commUI, testUser1);

		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();
	}


	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population: UserA Deletes Community Created By UserB</li>
	 *<li><B>Step:</B> UserB creates a Public community, with UserA as an additional Owner, via API</li>
	 *<li><B>Step:</B> UserA deletes the community</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBCreatesCommUserADeletesComm(){

		String testName = commUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRand())
		                                           .access(Access.PUBLIC)
		                                           .description("GDPR data pop - UserA deletes community created by UserB ")
		                                           .addMember(new Member(CommunityRole.OWNERS, testUser1))
		                                           .build();

		log.info("INFO: Create community as UserB: " + testUser2.getDisplayName() + " using API");
		Community commAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, commAPI);

		log.info("INFO: Log into Communities as UserA " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Removing community: " + testName );
		community.delete(commUI, testUser1);

		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population: UserA Creates Community And Adds Subcommunity</li>
	 *<li><B>Step:</B> UserA creates a basic Public community via API - no additional users added</li>
	 *<li><B>Step:</B> UserA creates a basic Moderated subcommunity</li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"}, enabled=false)

	public void userACreatesBasicCommAddsSubcomm(){

		String testName = commUI.startTest();

		String rndNum = Helper.genDateBasedRand();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + rndNum)
		                                           .access(Access.PUBLIC)	
		                                           .tags(Data.getData().commonTag + rndNum)
		                                           .description("GDPR data pop - UserA creates community & adds subcommunity")
		                                           .build();

		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("GDPR: SubCommunity: " + testName + rndNum)
		                                                    .access(BaseSubCommunity.Access.MODERATED)
		                                                    .tags(Data.getData().commonTag + rndNum)
		                                                    .description("GDPR data pop - basic moderated subcommunity")
		                                                    .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Creating Subcommunity ");
		subCommunity.create(commUI);

		log.info("INFO: Make sure the Name field is populated");
		checkCommunityNameFieldEmptyMsg(subCommunity);

		log.info("INFO: Validate the presence of the parent community in SubCommunity");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage).getText(), community.getName(),
				"ERROR : Parent Community link is not present");
		
		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();

	}	

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population: UserB Creates a Community, UserA Adds a Subcommunity</li>
	 *<li><B>Step:</B> UserB creates a basic Moderated community, with UserA as an additional Owner, via API </li>
	 *<li><B>Step:</B> UserA creates adds an internal restricted subcommunity to the community</li>
	 *</ul>
	 */

	@Test(groups = {"regression","regressioncloud"}, enabled=false)

	public void userBCreatesCommUserAAddsSubcomm(){

		String testName = commUI.startTest();

		String rndNum = Helper.genDateBasedRand();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + rndNum)
		                                           .access(Access.MODERATED)	
		                                           .tags(Data.getData().commonTag + rndNum)
		                                           .description("GDPR data pop - basic moderated community, UserA is an additional Owner")
		                                           .addMember(new Member(CommunityRole.OWNERS, testUser1))
		                                           .build();

		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("GDPR: SubCommunity " + testName + rndNum)
		                                                    .access(BaseSubCommunity.Access.RESTRICTED)
		                                                    .tags(Data.getData().commonTag + rndNum)
		                                                    .description("GDPR data pop - basic internal restricted subcommunity")
		                                                    .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Creating Subcommunity ");
		subCommunity.create(commUI);

		log.info("INFO: Make sure the Name field is populated");
		checkCommunityNameFieldEmptyMsg(subCommunity);

		log.info("INFO: Validate the presence of the parent community in SubCommunity");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage).getText(), community.getName(),
				"ERROR : Parent Community link is not present");
		
		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();

	}	

	/**
	 *
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population - UserA Creates Community & Edits Community</li>
	 *<li><B>Step:</B> UserA creates an internal restricted community via the API</li>
	 *<li><B>Step:</B> UserA edits the community - adds a Tag</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userACreatesAndEditsCommunity() {

		String testName = commUI.startTest();

		String rndNum = Helper.genDateBasedRand();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + rndNum)
		                                           .access(Access.RESTRICTED)	
		                                           .tags(Data.getData().commonTag + rndNum)
		                                           .description("GDPR data pop - UserA creates & edits the community") 
		                                           .shareOutside(false)
		                                           .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Open the community");
		community.navViaUUID(commUI);

		log.info("INFO: Edit the community");
		editCommunity();

		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();

	}

	/**
	 *
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population - UserA Creates & Edits Bookmark</li>
	 *<li><B>Step:</B> UserA creates a moderated community via API</li>
	 *<li><B>Step:</B> UserA adds a bookmark</li>
	 *<li><B>Step:</B> UserA edits the bookmark</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userACreatesAndEditsBookmark() {

		String testName = commUI.startTest();

		String rndNum = Helper.genDateBasedRand();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + rndNum)
		                                           .access(Access.MODERATED)	
		                                           .tags(Data.getData().commonTag + rndNum)
		                                           .description("GDPR data pop - UserA creates and edits a bookmark")  
		                                           .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Open the community");
		community.navViaUUID(commUI);		

		log.info("INFO: Add Bookmark to the community");
		addBookmarkToCommunity();

		log.info("INFO: Edit the bookmark");
		editBookmark();

		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();

	}

	/**
	 *
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population - UserB Creates Community & Bookmark, UserA Edits Comm & Bookmark</li>
	 *<li><B>Step:</B> UserB creates an internal restricted community with UserA as an additional Owner via the API</li>
	 *<li><B>Step:</B> UserB adds a bookmark</li>
	 *<li><B>Step:</B> UserA edits the community - adds a Tag</li>
	 *<li><B>Step:</B> UserA edits the bookmark</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBCreatesCommAndBookmarkUserAEditsCommAndBookmark() {

		String testName = commUI.startTest();

		String rndNum = Helper.genDateBasedRand();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + rndNum)
		                                           .access(Access.RESTRICTED)	
		                                           .tags(Data.getData().commonTag + rndNum)
		                                           .description("GDPR data pop - UserB creates comm & bookmark, UserA edits the comm & bookmark")  
		                                           .shareOutside(false)
		                                           .addMember(new Member(CommunityRole.OWNERS, testUser1))
		                                           .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Log into Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);

		log.info("INFO: Open the community");
		community.navViaUUID(commUI);		

		log.info("INFO: Add Bookmark to the community");
		addBookmarkToCommunity();

		log.info("INFO: Logout as UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Open the community");
		community.navViaUUID(commUI);

		log.info("INFO: Click on Community Actions");
		commUI.clickLinkWait(BaseUIConstants.Community_Actions_Button);

		log.info("INFO: Edit the community");
		editCommunity();

		log.info("INFO: Edit the bookmark");
		editBookmark();

		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();

	}


	/**
	 *
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population - UserA Follows Community Created By UserB</li>
	 *<li><B>Step:</B> UserB creates a community via the API</li>
	 *<li><B>Step:</B> UserA follows the community</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBCreatesCommunityUserAFollowsComm() {

		String testName = commUI.startTest();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
		                                           .access(Access.PUBLIC)
		                                           .description("GDPR data pop - UserB creates a community, UserA 'Follows' the comm")
		                                           .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Open the community");
		community.navViaUUID(commUI);

		log.info("INFO: Click on the 'Follow this Community' link");
		commUI.clickLinkWait(CommunitiesUIConstants.FollowThisCommunity);

		log.info("Logout as UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);	

		commUI.endTest();
	}

	/**	
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population - UserA Creates a Community and Does a Copy Community</li>
	 *<li><B>Step:</B> UserA creates a moderated community via the API</li>
	 *<li><B>Step:</B> UserA does a Copy Community</li>
	 *</ul>	
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userACreatesCommUsingCopyCommunity() {

		String testName = commUI.startTest();	

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
		                                           .access(Access.MODERATED)
		                                           .description("GDPR data pop - UserA creates a community & then does a Copy Community")		                                          
		                                           .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Open the community");
		community.navViaUUID(commUI);

		log.info("INFO: Copy an existing community");
		copyExistingCommunity(community);
		
		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();	

	}			


	/**	
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population - UserB Creates a Community and UserA Does Copy Community</li>
	 *<li><B>Step:</B> UserB creates a public community, with UserA as a member, via the API</li>
	 *<li><B>Step:</B> UserA does a Copy Community</li>
	 *</ul>	
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBCreatesCommUserACreatesCommUsingCopyCommunity() {

		String testName = commUI.startTest();	

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
		                                           .access(Access.PUBLIC)
		                                           .description("GDPR data pop - UserB creates a community, UserA does Copy Community using UserB's comm.")
		                                           .addMember(new Member(CommunityRole.MEMBERS, testUser1))
		                                           .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Open the community");
		community.navViaUUID(commUI);

		log.info("INFO: Copy an existing community");
		copyExistingCommunity(community);
		
		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();	

	}	


	/**
	 *<ul>	
	 *<li><B>Test Scenario:</B> Data Population - UserA Adds (2) Feeds & Makes Edits to (1) Feed </li>
	 *<li><B>Step:</B> UserA creates a community via API</li>
	 *<li><B>Step:</B> UserA adds a feed via API</li>
	 *<li><B>Step:</B> UserA adds a second feed via API</li>
	 *<li><B>Step:</B> UserA edits the first feed</li>
	 *</ul>
	 *NOTE: On-premises only.  The cloud does not support Feeds
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userACreatesAndEditsFeeds() {

		if(isOnPremise){
		String testName = commUI.startTest();

		String rndNum1 = Helper.genDateBasedRandVal();
		String rndNum2 = Helper.genDateBasedRandVal2();
		String rndNum3 = Helper.genDateBasedRandVal3();

		BaseCommunity community = new BaseCommunity.Builder("GDRP: " + testName + rndNum1)
		                                           .tags(Data.getData().commonTag + rndNum1)
		                                           .access(Access.PUBLIC)
		                                           .description("GDPR data pop - community with feeds " + testName)
		                                           .build();

		BaseFeed firstfeed = new BaseFeed.Builder("GDPR:UserA added: " + Data.getData().FeedsTitle + rndNum1, cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL + rndNum1)
		                                 .description(Data.getData().commonDescription + rndNum1)
		                                 .tags(Data.getData().MultiFeedsTag)
		                                 .build();


		BaseFeed secondfeed = new BaseFeed.Builder(Data.getData().FeedsTitle + rndNum2, cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL + rndNum2)
		                                  .description(Data.getData().commonDescription + rndNum2)
		                                  .tags(Data.getData().MultiFeedsTag1)
		                                  .build();


		BaseFeed editfeed = new BaseFeed.Builder("GDPR:UserA edited: " + Data.getData().FeedsTitle + rndNum3, cfg.getTestConfig().getBrowserURL() + Data.getData().EditedFeedsURL + rndNum3)
		                                .description(Data.getData().EditedTestDescription)
		                                .tags(Data.getData().MultiFeedsTag2)
		                                .build();


		log.info("INFO: Create a Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);

		log.info("INFO: Add Feeds widget to the Community using API");
		community.addWidgetAPI(comAPI, apiCommOwner1, BaseWidget.FEEDS);

		log.info("INFO: Add the first feed via API");
		firstfeed.createAPI(apiCommOwner1, comAPI);

		log.info("INFO: Add the second feed via API");
		secondfeed.createAPI(apiCommOwner1, comAPI);

		log.info("INFO: Log into Communities as User1: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the 'I'm an Owner' view");
		commUI.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Edit feed");
		editFeed(firstfeed, editfeed);

		log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();
		
		}else {
			log.info("INFO: Cloud environment does not support Feeds - skipping this test");
		}
	}

	/**
	 *<ul>	
	 *<li><B>Test Scenario:</B> Data Population - UserB Adds Feed To Comm, UserA Edits Feed & Adds Feeds </li>
	 *<li><B>Step:</B> UserB creates a community, with UserA as an additional Owner, via API</li>
	 *<li><B>Step:</B> UserB adds a feed via API</li>
	 *<li><B>Step:</B> UserA edits the feed created by UserB</li>
	 *<li><B>Step:</B> UserA adds a feed to the community</li>
	 *</ul>
	 *NOTE: On-premises only.  The cloud does not support Feeds
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userBAddsFeedUserAEditsAndAddsFeed() {

		if(isOnPremise){
		String testName = commUI.startTest();

		String rndNum1 = Helper.genDateBasedRandVal();
		String rndNum2 = Helper.genDateBasedRandVal2();
		String rndNum3 = Helper.genDateBasedRandVal3();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + rndNum1)
		                                           .tags(Data.getData().commonTag + rndNum1)
		                                           .access(Access.PUBLIC)
		                                           .description("GDPR data pop - UserB adds a Feed to their community " + testName)
		                                           .addMember(new Member(CommunityRole.OWNERS, testUser1))
		                                           .build();

		BaseFeed firstfeed = new BaseFeed.Builder(Data.getData().FeedsTitle + rndNum1, cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL + rndNum1)
		                                 .description(Data.getData().commonDescription + rndNum1)
		                                 .tags(Data.getData().MultiFeedsTag)
		                                 .build();

		BaseFeed secondfeed = new BaseFeed.Builder("GDPR:UserA added: " + Data.getData().FeedsTitle + rndNum2, cfg.getTestConfig().getBrowserURL() + Data.getData().FeedsURL + rndNum2)
		                                  .description(Data.getData().commonDescription + rndNum2)
		                                  .tags(Data.getData().MultiFeedsTag1)
		                                  .build();

		BaseFeed editfeed = new BaseFeed.Builder("GDPR:UserA edited UserB's feed: " + Data.getData().FeedsTitle + rndNum3, cfg.getTestConfig().getBrowserURL() + Data.getData().EditedFeedsURL + rndNum3)
		                                .description(Data.getData().EditedTestDescription)
		                                .tags(Data.getData().MultiFeedsTag2)
		                                .build();


		log.info("INFO: Create a Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Add Feeds widget to the Community using API");
		community.addWidgetAPI(comAPI, apiCommOwner2, BaseWidget.FEEDS);

		log.info("INFO: Add a feed via API");
		firstfeed.createAPI(apiCommOwner2, comAPI);

		log.info("INFO: Log into Communities as User1: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Edit feed");
		editFeed(firstfeed, editfeed);

		log.info("INFO: UserA adds a second feed to the community via API");
		secondfeed.createAPI(apiCommOwner1, comAPI);

		log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();
		
	}else {
		log.info("INFO: Cloud environment does not support Feeds - skipping this test");
	}
	}


	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population - UserB Creates Community & Invites UserA to Join</li>
	 *<li><B>Step:</B> UserB creates a public community via the API</li>
	 *<li><B>Step:</B> UserB opens the community & clicks on the Members tab</li>
	 *<li><B>Step:</B> UserB clicks on the Invite Members button & enters UserA's name</li>
	 *<li><B>Step:</B> UserB clicks on the Send Invitations button</li>
	 *<li><B>Verify:</B> Success message displays</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBInvitesUserAToJoinTheCommunity() {

		String testName = commUI.startTest();

		Member member = new Member(CommunityRole.MEMBERS, testUser1);

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRand())
		                                           .access(Access.PUBLIC)
		                                           .description("GDPR data pop - UserB invites UserA to join the community")
		                                           .build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);

		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Members from left navigation Menu");		
		Community_LeftNav_Menu.MEMBERS.select(commUI);

		log.info("INFO: Click on the 'Invite Members' button & enter UserA as the person to invite");
		try {
			commUI.inviteMemberCommunity(member);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Click on the SendInviteButton");
		commUI.clickLinkWait(CommunitiesUIConstants.SendInvitesButton);

		log.info("INFO: Validate the success message");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getInviteSuccessMsg(member.getUser())),
				"ERROR: Success message is not present");
		
		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();		
	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Data Population - UserB Creates Community & Adds UserA as a Member</li>
	 *<li><B>Step:</B> UserB creates a public community  & adds UserA as a member - via API</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBAddsUserAAsMember(){


		String testName = commUI.startTest();	

		String rndNum = Helper.genDateBasedRand();

		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + rndNum)
		                                           .access(Access.PUBLIC)	
		                                           .tags(Data.getData().commonTag + rndNum )
		                                           .description("GDPR data pop - UserB adds UserA as a member to the community")
		                                           .addMember(new Member(CommunityRole.MEMBERS, testUser1))
		                                           .build();

		log.info("INFO: UserB: " + testUser2.getDisplayName() + " creates a community with UserA as a Member using API");
		community.createAPI(apiCommOwner2);

		commUI.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Data Population - UserA Creates (2) Communities & Moves a Community to be a Subcommunity</li>
	 * <li><B>Step:</B> UserA creates 2 communities using the API</li>
	 * <li><B>Step:</B> UserA opens one of the communities </li>
	 * <li><B>Step:</B> UserA selects Community Actions > Move Community </li>
	 * <li><B>Step:</B> UserA types the name of the community to make this a subcommunity of</li>
	 * <li><B>Step:</B> UserA selects the community to become the parent from the typeahead results</li>
	 * <li><B>Step:</B> UserA clicks on the Move button </li>
	 *</ul>
	 */			

	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAMovesTopLevelToBeSubcomm() {

		commUI.startTest();

		String rndNum = Helper.genDateBasedRand();

		String comm1 = "GDPR:Parent Comm (created by UserA)";
		String comm2 = "GDPR:Make Me a Subcomm (created by UserA)";

		BaseCommunity community1 = new BaseCommunity.Builder(comm1 + rndNum)
		                                            .access(Access.PUBLIC)	
		                                            .tags(Data.getData().commonTag + rndNum)
		                                            .description("GDPR Data Pop: Move Community - this comm will be the parent community")
		                                            .build();

		BaseCommunity community2 = new BaseCommunity.Builder(comm2 + rndNum)
		                                            .access(Access.PUBLIC)	
		                                            .tags(Data.getData().commonTag + rndNum)
		                                            .description("GDPR Data Pop: Move Community - this top-level comm will become a subcommunity")
		                                            .build();		


		log.info("INFO: Create community using API");
		Community comAPI = community1.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiCommOwner1, comAPI);

		log.info("INFO: Create a 2nd community using API");
		Community comAPI2 = community2.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiCommOwner1, comAPI2);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the 2nd community using UUID");
		community2.navViaUUID(commUI);

		log.info("INFO: Move a top-level community to be a subcommunity");
		moveTopLevelCommToSubcomm(community1);
		
		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();

	}


	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Data Population - UserB Creates (2) Communities, UserA Moves a Community to be a Subcommunity</li>
	 * <li><B>Step:</B> UserB creates 2 communities, with UserA as an additional Owner in each comm, using the API</li>
	 * <li><B>Step:</B> UserA opens one of the communities </li>
	 * <li><B>Step:</B> UserA selects Community Actions > Move Community </li>
	 * <li><B>Step:</B> UserA types the name of the community to make this a subcommunity of</li>
	 * <li><B>Step:</B> UserA selects the community to become the parent from the typeahead results</li>
	 * <li><B>Step:</B> UserA clicks on the Move button </li>
	 *</ul>
	 */			

	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBCreatesCommsUserAMakesCommASubComm() {

		commUI.startTest();

		String rndNum = Helper.genDateBasedRand();

		String comm1 = "GDPR:Parent comm (created by UserB)";
		String comm2 = "GDPR:Make Me a subcomm (created by UserB)";

		BaseCommunity community1 = new BaseCommunity.Builder(comm1 + rndNum)
		                                            .access(Access.PUBLIC)	
		                                            .tags(Data.getData().commonTag + rndNum)
		                                            .description("GDPR Data Pop: Move Community - this comm will be the parent community")
		                                            .addMember(new Member(CommunityRole.OWNERS, testUser1))
		                                            .build();

		BaseCommunity community2 = new BaseCommunity.Builder(comm2 + rndNum)
		                                            .access(Access.PUBLIC)	
		                                            .tags(Data.getData().commonTag + rndNum)
		                                            .description("GDPR Data Pop: Move Community - this top-level comm will become a subcommunity")
		                                            .addMember(new Member(CommunityRole.OWNERS, testUser1))
		                                            .build();		

		log.info("INFO: Create community using API");
		Community comAPI = community1.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Create a 2nd community using API");
		Community comAPI2 = community2.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiCommOwner2, comAPI2);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the 2nd community using UUID");
		community2.navViaUUID(commUI);

		log.info("INFO: Move a top-level community to be a subcommunity");
		moveTopLevelCommToSubcomm(community1);
		
		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Data Population - UserA Moves Subcomm to be Top Level Community </li>
	 * <li><B>Step:</B> UserA creates a community using the API </li>
	 * <li><B>Step:</B> UserA creates a subcommunity </li>
	 * <li><B>Step:</B> From the subcommunity Overview page click on Community Actions > Move Community </li>
	 * <li><B>Step:</B> UserA selects option to move the subcomm to be a top level community</li>
	 * <li><B>Step:</B> Click on the Move button</li>
	 *</ul>
	 */			

	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAMovesSubcommToTopLevelComm() {

		commUI.startTest();		

		String rndNum = Helper.genDateBasedRand();

		String comm1 = "GDPR:Community with subcomm (created by UserA)";
		String comm2 = "GDPR:Move this subcomm to top level (created by UserA)";

		BaseCommunity community = new BaseCommunity.Builder(comm1 + rndNum)
		                                           .access(Access.PUBLIC)	
		                                           .tags(Data.getData().commonTag + rndNum )
		                                           .description("GDPR Data Pop: This community will remain a top-level community.")
		                                           .build();

		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder(comm2 + rndNum)
		                                                    .access(BaseSubCommunity.Access.MODERATED)
		                                                    .tags(Data.getData().commonTag + rndNum)
		                                                    .description("GDPR Data Pop: This subcommunity will become a top level community")
		                                                    .build();


		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);

		log.info("INFO: Log into Communities as UserA " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Creating Subcommunity ");
		subCommunity.create(commUI);

		log.info("INFO: Make sure the name field is populated");
		checkCommunityNameFieldEmptyMsg(subCommunity);
				
		log.info("INFO: Refresh the page");
		UIEvents.refreshPage(driver);

		log.info("INFO: Move a subcommunity to be a top level community");
		moveSubcommToBeTopLevelCommunity();
		
		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Data Population - UserA Moves Subcomm Created By UserB to be Top Level Community </li>
	 * <li><B>Step:</B> UserB creates a community using the API </li>
	 * <li><B>Step:</B> UserB creates a subcommunity </li>
	 * <li><B>Step:</B> From the subcommunity Overview page UserA clicks on Community Actions > Move Community </li>
	 * <li><B>Step:</B> UserA selects option to move the subcomm to be a top level community</li>
	 * <li><B>Step:</B> UserA clicks on the Move button</li>
	 *</ul>
	 */			

	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBCreatesSubcommUserAMovesSubcommToTopLevelComm() {

		commUI.startTest();		

		String rndNum = Helper.genDateBasedRand();

		String comm1 = "GDPR:Community with subcomm (created by UserB)";
		String comm2 = "GDPR:Move this subcomm to top level (created by UserB)";

		BaseCommunity community = new BaseCommunity.Builder(comm1 + rndNum)
		                                           .access(Access.PUBLIC)	
		                                           .tags(Data.getData().commonTag + rndNum )
		                                           .description("GDPR Data Pop: This community will remain a top-level community.")
		                                           .addMember(new Member(CommunityRole.OWNERS, testUser1))
		                                           .build();

		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder(comm2 + rndNum)
		                                                    .access(BaseSubCommunity.Access.MODERATED)
		                                                    .tags(Data.getData().commonTag + rndNum)
		                                                    .description("GDPR Data Pop: This subcommunity will become a top level community")
		                                                    .build();


		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Log into Communities as UserB " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Creating Subcommunity ");
		subCommunity.create(commUI);

		log.info("INFO: Make sure the Name field is populated");
		checkCommunityNameFieldEmptyMsg(subCommunity);
		
		log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);

		log.info("INFO: Log into Communities as UserA " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Refresh browser to make sure subcommunity appears");
		UIEvents.refreshPage(driver);

		log.info("INFO: Select the subcommunity created by UserB from the catalog view");
		commUI.clickLinkWait("link=" + subCommunity.getName());		

		log.info("INFO: Move a subcommunity to be a top level community");
		moveSubcommToBeTopLevelCommunity();
		
		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);
		
		commUI.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Data Population - UserA Moves Subcommunity to a New Parent Community </li>
	 * <li><B>Step:</B> UserA creates a community using the API </li>
	 * <li><B>Step:</B> UserA creates a subcommunity </li>
	 * <li><B>Step:</B> From the subcommunity Overview page UserA clicks on Community Actions > Move Community </li>
	 * <li><B>Step:</B> UserA selects the option: 'Make this a subcommunity of:' </li>
	 * <li><B>Step:</B> UserA starts to enter the name of the community to be the new parent into typeahead </li>
	 * <li><B>Step:</B> UserA selects the community to be the new parent from the typeahead results </li>
	 * <li><B>Step:</B> UserA clicks the Move button </li>
	 * </ul>
	 */			

	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAMovesSubcommToNewParentCommunity() {

		commUI.startTest();

		String rndNum = Helper.genDateBasedRand();

		String comm1 = "GDPR:Comm1 - Original Parent (created by UserA)";
		String comm2 = "GDPR:Comm2 - New Parent (created by UserA)";
		String comm3 = "GDPR:Subcomm to be re-parented (created by UserA)";


		BaseCommunity community1 = new BaseCommunity.Builder(comm1 + rndNum)
		                                            .access(Access.PUBLIC)	
		                                            .tags(Data.getData().commonTag + rndNum )
		                                            .description("GDPR Data Pop: Original parent of the subcommunity")
		                                            .build();

		BaseCommunity community2 = new BaseCommunity.Builder(comm2 + rndNum)
		                                            .access(Access.PUBLIC)	
		                                            .tags(Data.getData().commonTag + rndNum )
		                                            .description("GDPR Data Pop: New parent of the subcommunity")
		                                            .build();		

		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder(comm3 + rndNum)
		                                                    .access(BaseSubCommunity.Access.MODERATED)
		                                                    .tags(Data.getData().commonTag + rndNum)
		                                                    .description("Subcommunity to be moved to a new parent")
		                                                    .build();

		log.info("INFO: Create community using API");
		Community comAPI = community1.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiCommOwner1, comAPI);

		log.info("INFO: Create a 2nd community using API");
		Community comAPI2 = community2.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiCommOwner1, comAPI2);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community1.navViaUUID(commUI);

		log.info("INFO: Creating a subcommunity ");
		subCommunity.create(commUI);

		log.info("INFO: Make sure the Name field is populated");
		checkCommunityNameFieldEmptyMsg(subCommunity);

		log.info("INFO: Verify subcommunity is created");
		Assert.assertTrue(commUI.fluentWaitTextPresent(subCommunity.getName()),
				"ERROR : subcommunity is not created");

		commUI.waitForPageLoaded(driver);

		log.info("INFO: Move subcommunity to new parent community");
		moveSubcommToNewParentCommunity(community2);
		
		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Data Population - UserB Creates Subcommunity, UserA Moves Subcommunity to a New Parent Community </li>
	 * <li><B>Step:</B> UserB creates (2) communities, with UserA as an additional Owner in both, using the API </li>
	 * <li><B>Step:</B> UserB creates a subcommunity for one of the communities </li>
	 * <li><B>Step:</B> From the subcommunity Overview page UserA clicks on Community Actions > Move Community </li>
	 * <li><B>Step:</B> UserA selects the option: 'Make this a subcommunity of:' </li>
	 * <li><B>Step:</B> UserA starts to enter the name of the community to be the new parent into typeahead </li>
	 * <li><B>Step:</B> UserA selects the community to be the new parent from the typeahead results </li>
	 * <li><B>Step:</B> UserA clicks the Move button </li>
	 * </ul>
	 */			

	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAMovesSubcommCreatedByUserBToNewParentCommunity() {

		commUI.startTest();

		String rndNum = Helper.genDateBasedRand();

		String comm1 = "GDPR:Comm1 - Original Parent (created by UserB)";
		String comm2 = "GDPR:Comm2 - New Parent (created by UserB)";
		String comm3 = "GDPR:Subcomm to be re-parented (created by UserB)";


		BaseCommunity community1 = new BaseCommunity.Builder(comm1 + rndNum)
		                                            .access(Access.PUBLIC)	
		                                            .tags(Data.getData().commonTag + rndNum )
		                                            .description("GDPR Data Pop: Original parent of the subcommunity")
		                                            .addMember(new Member(CommunityRole.OWNERS, testUser1))
		                                            .build();

		BaseCommunity community2 = new BaseCommunity.Builder(comm2 + rndNum)
		                                            .access(Access.PUBLIC)	
		                                            .tags(Data.getData().commonTag + rndNum )
		                                            .description("GDPR Data Pop: New parent of the subcommunity")
		                                            .addMember(new Member(CommunityRole.OWNERS, testUser1))
		                                            .build();		

		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder(comm3 + rndNum)
		                                                    .access(BaseSubCommunity.Access.MODERATED)
		                                                    .tags(Data.getData().commonTag + rndNum)
		                                                    .description("Subcommunity to be moved to a new parent")
		                                                    .build();


		log.info("INFO: Create community using API");
		Community comAPI = community1.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Create a 2nd community using API");
		Community comAPI2 = community2.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community2.getCommunityUUID_API(apiCommOwner2, comAPI2);

		log.info("INFO: Log into Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);

		log.info("INFO: Navigate to the community using UUID");
		community1.navViaUUID(commUI);

		log.info("INFO: Creating a subcommunity ");
		subCommunity.create(commUI);

		log.info("INFO: Make sure the Name field is populated");
		checkCommunityNameFieldEmptyMsg(subCommunity);

		log.info("INFO: Verify subcommunity is created");
		Assert.assertTrue(commUI.fluentWaitTextPresent(subCommunity.getName()),
				"ERROR : subcommunity is not created");

		log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);

		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Select the subcommunity created by UserB from the catalog view");
		commUI.clickLinkWait("link=" + subCommunity.getName());

		log.info("INFO: Move subcommunity to new parent community");
		moveSubcommToNewParentCommunity(community2);
		
		log.info("INFO: Log out of Communities");
		commUI.logout();
		commUI.close(cfg);

		commUI.endTest();

	}

	/**
	 * check to make sure the community name field is not empty.  
	 * if the error message appears, re-enter the subcommunity name
	 */

	private void checkCommunityNameFieldEmptyMsg(BaseSubCommunity subCommunity) {
		log.info("INFO: Check for the message that the community name should not be empty");
		if (driver.isTextPresent(Data.getData().communityNameFieldIsEmptyMsg)){
			log.info("INFO: Entering community name " + subCommunity.getName());
			this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).clear();
			this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(subCommunity.getName());

			log.info("INFO: Click on the Access Advanced Features link to expand the section");
			driver.getFirstElement(CommunitiesUIConstants.comAdvancedLink).click();

			if(subCommunity.isUseParentMembers()) {
				log.info("INFO: Select the checkbox to add members from the parent community to the subcommunity");
				this.driver.getFirstElement(CommunitiesUIConstants.AddMemberscheckbox).click();
			}

			log.info("INFO: Saving the sub community " + subCommunity.getName());	
			this.driver.getSingleElement(CommunitiesUIConstants.SaveButton).click();
		}
	}
	
	/**
	 * add bookmark to community:
	 * - click on the link to add your first bookmark
	 * - enter text into each of the fields
	 * - click on the Save button
	 */
	private void addBookmarkToCommunity(){
		log.info("INFO: Click on the Add Your First Bookmark link");
		commUI.clickLinkWait(CommunitiesUIConstants.AddYourFirsBookMark);

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
	}
	
	/**
	 * this method edits a community:
	 * - click on Edit community
	 * - edit the community Tags field
	 * - click on the Save button
	 */
	private void editCommunity(){
		log.info("INFO: Click on Community Actions");
		commUI.clickLinkWait(BaseUIConstants.Community_Actions_Button);

		log.info("INFO: Click on the Edit Community link");
		commUI.clickLinkWait(BaseUIConstants.Menu_Item_Edit);

		log.info("INFO: Edit the community by adding a tag");
		driver.getSingleElement(CommunitiesUIConstants.CommunityTag).clear();
		driver.getSingleElement(CommunitiesUIConstants.CommunityTag).type(Data.getData().MultiFeedsTag);

		log.info("INFO: Click Save button");
		commUI.clickLinkWait(CommunitiesUIConstants.editCommunitySaveButton);
	}
	
	/**
	 * this method edits a bookmark:
	 * - click on Bookmarks tab
	 * - click on the bookmark 'More' link
	 * - click on 'Edit' link
	 * - edit the name of the bookmark
	 * - save the change & verify the new name appears
	 * 
	 */
	private void editBookmark(){
		log.info("INFO: Click on the Bookmarks tab");
		Community_TabbedNav_Menu.BOOKMARK.select(commUI);

		log.info("INFO: Click on the bookmark 'More' link");
		commUI.clickLinkWait(CommunitiesUIConstants.firstBookmarksMoreLink);

		log.info("INFO: Click on the bookmark 'Edit' link");
		commUI.clickLinkWait(CommunitiesUIConstants.EditLink);

		log.info("INFO: Start editing the bookmark Name");	
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkName).clear();
		driver.getFirstElement(CommunitiesUIConstants.EditBookmarkName).type(Data.getData().EditBookmarkName);

		log.info("INFO: Save the changes in Bookmark");
		commUI.clickLinkWithJavascript(CommunitiesUIConstants.SaveButtonEntry);

		log.info("INFO: Verify the edited bookmark name appears");
		commUI.fluentWaitTextPresent(Data.getData().EditBookmarkName);

	}
	
	/**
	 * this method copies an existing community:
	 * - click on Community Actions > Copy Community link
	 * - verify the copy community form appears
	 * - click on Save button
	 * - verify new copy ends with 'Copy'
	 */
	private void copyExistingCommunity(BaseCommunity community){
		log.info("INFO: Click on Community Actions");
		commUI.clickLinkWait(BaseUIConstants.Community_Actions_Button);

		log.info("INFO: Click on the Copy Community link");
		commUI.clickLinkWait(CommunitiesUIConstants.CopyExistingCommOption);

		log.info("INFO: Verify 'Copying an Existing Community' form appears");
		Assert.assertTrue(commUI.fluentWaitTextPresent(CommunitiesUIConstants.CopyStructureFormTitle),
				"ERROR: 'Copying an Existing Community' form does not appear");

		log.info("INFO: Click on Save button");
		commUI.clickLinkWait(CommunitiesUIConstants.SaveButton);

		log.info("INFO: Verify new copy is created and the word 'Copy' is appended at the end of community name");
		Assert.assertTrue(commUI.fluentWaitTextPresent(community.getName() + CommunitiesUIConstants.appendCopy),
				"ERROR: New copy is not created");	
	}
	
	/**
	 * this method clicks on edits a feed:
	 * - click on Feeds tab
	 * - click on the feed 'More' link
	 * - click on 'Edit' link for first feed listed
	 * - edit & save the feed
	 * - verify the updated feed appears
	 * 
	 */
	private void editFeed(BaseFeed firstfeed, BaseFeed editfeed){

		log.info("INFO: Select 'Feeds' from the tabbed nav menu");
		Community_TabbedNav_Menu.FEEDS.select(commUI);

		log.info("INFO: Click More > Edit on first feed");
		fUI.selectMoreLinkByFeed(firstfeed);
		fUI.selectEditLinkByFeed(firstfeed);

		log.info("INFO: Edit the feed and save");

		try {
			fUI.editFeed(editfeed);
		} catch (Exception e) {
			e.printStackTrace();
		}

		log.info("INFO: Validate the updated feed is present in the full page");
		Assert.assertTrue(commUI.fluentWaitElementVisible("link=" + editfeed.getTitle()),
				"ERROR: Feed title is not found");
	}
	
	
	/**
	 * this method moves a top level community to be a subcommunity:
	 * - click on Community Actions drop-down menu & select Move Community
	 * - enter community to be parent community into the input field
	 * - select community from typeahead results
	 * - click on the Move button
	 * - verify the success message appears
	 * 
	 */
	private void moveTopLevelCommToSubcomm(BaseCommunity community1){
		log.info("INFO: Select Move Community from the Community Actions drop-down menu");
		Com_Action_Menu.MOVECOMMUNITY.select(commUI);
		commUI.waitForPageLoaded(driver);

		log.info("INFO: Enter the name of the community to be the parent community");
		commUI.typeTextWithDelay(CommunitiesUIConstants.moveCommToSubcommInputField, (community1.getName()));

		log.info("INFO: Select the community to be the parent from the typeahead results");		
		commUI.typeaheadSelection(community1.getName(), CommunitiesUIConstants.moveCommunityTypeaheadPicker);

		log.info("INFO: Click on the Move button");
		driver.getFirstElement(CommunitiesUIConstants.moveButton).click();

		log.info("INFO: Verify the success message displays after moving the top level community a subcommunity");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moveCommSuccessMsg),
				"ERROR: The success message did not display");

		log.info("INFO: Verify there is a link to the parent community on the breadcrumb");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.bannerBreadcrumbParentCommLinkOnSubcommPage),
				"ERROR: There is no link to the parent community on the breadcrumb");

	}
	
	/**
	 * this method moves a subcommunity to be a top level community:
	 * - click on Community Actions drop-down menu & select Move Community
	 * - verify move dialog displays & the default radio button selection 
	 * - click on the Move button
	 * - verify the success message appears
	 * 
	 */
	private void moveSubcommToBeTopLevelCommunity(){
		log.info("INFO: Click on Community Actions");
		commUI.clickLinkWait(BaseUIConstants.Community_Actions_Button);

		log.info("INFO: Click on the Move Community link");
		commUI.clickLinkWait(BaseUIConstants.Menu_Item_MoveComunity);

		commUI.waitForPageLoaded(driver);

		//NOTE: verifications needed to slow automation down; otherwise, test fails	
		log.info("INFO: Verify the Move Community dialog displays");
		Assert.assertTrue(commUI.isElementPresent(CommunitiesUIConstants.moveSubcommDialogBox),
				"ERROR: The Move Community dialog box did not display");

		log.info("INFO: Verify the option to make the subcommunity a top level community is selected by default");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.makeTopLevelRadioButton).getAttribute("checked").contentEquals("true"),
				"ERROR: The radio button to make the subcomm a top level is not selected by default");

		log.info("INFO: Click on the Move button");
		driver.getFirstElement(CommunitiesUIConstants.moveButton).click();

		log.info("INFO: Verify the success message displays after moving the subcomm to be a top level community");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moveCommSuccessMsg),
				"ERROR: The success message did not display");

	}
	
	/**
	 * this method moves a subcommunity to a new parent community:
	 * - click on Community Actions drop-down menu & select Move Community
	 * - verify move dialog displays & the default radio button selection & input field 
	 * - enter the name of the new parent community
	 * - select the new parent community from typeahead results
	 * - click on the Move button
	 * - verify the success message appears
	 * 
	 */	
	private void moveSubcommToNewParentCommunity(BaseCommunity community2){
		log.info("INFO: Click on Community Actions");
		commUI.clickLinkWait(BaseUIConstants.Community_Actions_Button);

		log.info("INFO: Click on the Move Community link");
		commUI.clickLinkWait(BaseUIConstants.Menu_Item_MoveComunity);

		commUI.waitForPageLoaded(driver);

		//Note: verifications needed to slow automation down; otherwise, test fails
		log.info("INFO: Verify the Move Community dialog displays");
		Assert.assertTrue(commUI.isElementPresent(CommunitiesUIConstants.moveSubcommDialogBox),
				"ERROR: The Move Community dialog box did not display");

		log.info("INFO: Select the radio button 'Make this a subcommunity of:'");
		commUI.clickLinkWait(CommunitiesUIConstants.makeSubcommRadioButton);

		log.info("INFO: Verify the community name input field displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moveCommToSubcommInputField),
				"ERROR: The community name input field does not exist");

		log.info("INFO: Enter the name of the community to be the new parent community");
		commUI.typeTextWithDelay(CommunitiesUIConstants.moveCommToSubcommInputField, (community2.getName()));

		log.info("INFO: Select the community to be the new parent community from the typeahead results");
		commUI.typeaheadSelection(community2.getName(), CommunitiesUIConstants.moveCommunityTypeaheadPicker);

		log.info("INFO: Click on the Move button");
		driver.getFirstElement(CommunitiesUIConstants.moveButton).click();

		log.info("INFO: Verify the success message displays after moving the subcomm to be a new parent community");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.moveCommSuccessMsg),
				"ERROR: The success message did not display");

	}
	
}
