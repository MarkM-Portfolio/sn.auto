package com.ibm.conn.auto.lcapi.test;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage.PageType;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.WikiRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiComment;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

public class APIWikisTest extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(APIWikisTest.class);
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
		
		ServiceEntry communities = config.getService("communities");
		assert(communities != null);

		ServiceEntry wikis = config.getService("wikis");
		assert(wikis != null);

		Utils.addServiceAdminCredentials(communities, client);
				
	}
	
	@Test(groups = {"apitest"})
	public void test_CreateWiki_PublicWiki() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone public wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki wiki = wikiOwner.createWiki(baseWiki);
		
		assert wiki != null : "ERROR: There was a problem with creating the wiki using the API";
		assert wiki.getId() != null : "ERROR: The ID of the wiki was not set correctly using the API";
		assert wiki.getTitle().equals(baseWiki.getName()) == true : "ERROR: The wiki title was not set correctly using the API";
		assert wiki.getSummary().trim().equals(baseWiki.getDescription()) == true : "ERROR: The wiki description / summary was not set correctly using the API";
		assert wiki.getTags().size() == 1 : "ERROR: The wiki tags list was empty when a single tag was expected";
		assert wiki.getMembers().size() == 2 : "ERROR: The public wiki members list was not correctly set to two default members using the API";
		assert wiki.getSelfLink() != null : "ERROR: The wiki self link was not set correctly using the API";
		assert wiki.getEditLink() != null : "ERROR: The wiki edit link was not set correctly using the API";
		assert wiki.getAlternateLink() != null : "ERROR: The wiki alternate link was not set correctly using the API";
		assert wiki.getRepliesLink() != null : "ERROR: The wiki replies link was not set correctly using the API";
		
		log.info("INFO: Perform clean up now that the API test has completed");
		wikiOwner.deleteWiki(wiki);
	}
	
	@Test(groups = {"apitest"})
	public void test_CreateWiki_PrivateWiki_NoMembers() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone public wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Private_NoMembers(testName);
		Wiki wiki = wikiOwner.createWiki(baseWiki);
		
		assert wiki != null : "ERROR: There was a problem with creating the wiki using the API";
		assert wiki.getId() != null : "ERROR: The ID of the wiki was not set correctly using the API";
		assert wiki.getTitle().equals(baseWiki.getName()) == true : "ERROR: The wiki title was not set correctly using the API";
		assert wiki.getSummary().trim().equals(baseWiki.getDescription()) == true : "ERROR: The wiki description / summary was not set correctly using the API";
		assert wiki.getTags().size() == 1 : "ERROR: The wiki tags list was empty when a single tag was expected";
		assert wiki.getMembers() == null : "ERROR: The private wiki members list was not set to null as expected using the API";
		assert wiki.getSelfLink() != null : "ERROR: The wiki self link was not set correctly using the API";
		assert wiki.getEditLink() != null : "ERROR: The wiki edit link was not set correctly using the API";
		assert wiki.getAlternateLink() != null : "ERROR: The wiki alternate link was not set correctly using the API";
		assert wiki.getRepliesLink() != null : "ERROR: The wiki replies link was not set correctly using the API";
		
		log.info("INFO: Perform clean up now that the API test has completed");
		wikiOwner.deleteWiki(wiki);
	}
	
	@Test(groups = {"apitest"})
	public void test_CreateWiki_PrivateWiki_OneMember() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone public wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Private_OneMember(testName, testUser2, testUser2Profile);
		Wiki wiki = wikiOwner.createWiki(baseWiki);
		
		assert wiki != null : "ERROR: There was a problem with creating the wiki using the API";
		assert wiki.getId() != null : "ERROR: The ID of the wiki was not set correctly using the API";
		assert wiki.getTitle().equals(baseWiki.getName()) == true : "ERROR: The wiki title was not set correctly using the API";
		assert wiki.getSummary().trim().equals(baseWiki.getDescription()) == true : "ERROR: The wiki description / summary was not set correctly using the API";
		assert wiki.getTags().size() == 1 : "ERROR: The wiki tags list was empty when a single tag was expected";
		assert wiki.getMembers().size() == 1 : "ERROR: The private wiki members list did not contain a single member as expected using the API";
		assert wiki.getSelfLink() != null : "ERROR: The wiki self link was not set correctly using the API";
		assert wiki.getEditLink() != null : "ERROR: The wiki edit link was not set correctly using the API";
		assert wiki.getAlternateLink() != null : "ERROR: The wiki alternate link was not set correctly using the API";
		assert wiki.getRepliesLink() != null : "ERROR: The wiki replies link was not set correctly using the API";
		
		log.info("INFO: Perform clean up now that the API test has completed");
		wikiOwner.deleteWiki(wiki);
	}
	
	@Test(groups={"apitest"})
	public void test_EditCommunityWiki() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler apiCommunitiesHandler = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler apiWikiHandler = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = createBaseCommunity_PublicCommunity(testName);
		Community community = apiCommunitiesHandler.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki widget to the community which will default-create the community wiki");
		apiCommunitiesHandler.addWidget(community, BaseWidget.WIKI);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now update the community wiki");
		Wiki wiki = apiWikiHandler.editCommunityWiki(community);
		
		assert wiki != null : "ERROR: There was a problem with editing the community wiki using the API";
		assert wiki.getTitle().trim().equals(baseCommunity.getName().trim()) == true : 
				"ERROR: The title of the community wiki was not set correctly after updating the community wiki using the API";
		assert wiki.getSummary().trim().equals(baseCommunity.getDescription().trim()) == true : 
				"ERROR: The content / summary of the community wiki was not set correctly after updating the community wiki using the API";
		assert wiki.getSelfLink() != null : "ERROR: The wiki self link was not set correctly after updating the community wiki using the API";
		assert wiki.getEditLink() != null : "ERROR: The wiki edit link was not set correctly after updating the community wiki using the API";
		assert wiki.getAlternateLink() != null : "ERROR: The wiki alternate link was not set correctly after updating the community wiki using the API";
		assert wiki.getRepliesLink() != null : "ERROR: The wiki replies link was not set correctly after updating the community wiki using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		apiCommunitiesHandler.deleteCommunity(community);
	}
	
	@Test(groups={"apitest"})
	public void test_DeleteCommunityWiki() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Community " + testName);
		
		BaseCommunity baseCommunity = createBaseCommunity_PublicCommunity(testName);

		APICommunitiesHandler apiCommunitiesHandler = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler apiWikiHandler = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		log.info("INFO: API user: " + testUser.getDisplayName());
		
		Community community = apiCommunitiesHandler.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki widget to the community which will default-create the community wiki");
		apiCommunitiesHandler.addWidget(community, BaseWidget.WIKI);
		
		boolean deleted = apiWikiHandler.deleteCommunityWiki(community);
		
		assert deleted != false : "ERROR: There was a problem with deleting the community wiki using the API";
	}

	@Test(groups={"apitest"})
	public void test_CreateFollow_StandaloneWiki() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone public wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki wiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow this standalone public wiki");
		boolean followSuccessful = wikiFollower.createFollow(wiki);
		
		assert followSuccessful == true : "ERROR: There was a problem with following the Wiki using the API";
		
		log.info("INFO: Perform clean up now that the API test has completed");
		wikiOwner.deleteWiki(wiki);
	}
	
	@Test(groups = {"apitest"})
	public void test_CreateFollow_PublicCommunityWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = createBaseCommunity_PublicCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment");
		baseCommunity.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now follow the community wiki");
		boolean followed = wikiOwner.createFollowCommunityWiki(communityWiki);
		
		assert followed == true : "ERROR: There was a problem with following the community wiki using the API";
		
		log.info("INFO: Perform clean up now that the API test has completed");
		wikiOwner.deleteCommunityWiki(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void test_CreateFollow_RestrictedCommunityWiki_AsAMember() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a restricted community");
		BaseCommunity baseCommunity = createBaseCommunity_RestrictedCommunity(testName);
		Community restrictedCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment");
		baseCommunity.addWidgetAPI(restrictedCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add " + testUser2.getDisplayName() + " to the community as a member");
		communityOwner.addMemberToCommunity(testUser2, restrictedCommunity, Role.MEMBER);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(restrictedCommunity);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the community wiki");
		boolean followed = wikiFollower.createFollowCommunityWiki(communityWiki);
		
		assert followed == true : "ERROR: There was a problem with following the restricted community wiki using the API";
		
		log.info("INFO: Perform clean up now that the API test has completed");
		wikiOwner.deleteCommunityWiki(restrictedCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void test_CreateFollow_RestrictedCommunityWiki_NotAMember() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a restricted community");
		BaseCommunity baseCommunity = createBaseCommunity_RestrictedCommunity(testName);
		Community restrictedCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment");
		baseCommunity.addWidgetAPI(restrictedCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(restrictedCommunity);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the community wiki");
		boolean followed = wikiFollower.createFollowCommunityWiki(communityWiki);
		
		assert followed == false : "ERROR: There user could follow the restricted community wiki using the API despite not being a member";
		
		log.info("INFO: Perform clean up now that the API test has completed");
		wikiOwner.deleteCommunityWiki(restrictedCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateWikiPage_StandaloneWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki publicWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page");
		BaseWikiPage basePage = createBaseWikiPage(testName);
		WikiPage wikiPage = wikiOwner.createWikiPage(basePage, publicWiki);
		
		assert wikiPage != null : "ERROR: There was a problem with creating the wiki page using the API";
		assert wikiPage.getTitle().trim().equals(basePage.getName()) == true : "ERROR: The wiki page title was set incorrectly using the API";
		assert wikiPage.getSummary().trim().equals(basePage.getDescription()) == true : "ERROR: The wiki page description / summary was set incorrectly using the API";
		assert wikiPage.getSelfLink() != null : "ERROR: The wiki page self link was not set correctly using the API";
		assert wikiPage.getEditLink() != null : "ERROR: The wiki page edit link was not set correctly using the API";
		assert wikiPage.getAlternateLink() != null : "ERROR: The wiki page alternate link was not set correctly using the API";
		assert wikiPage.getRepliesLink() != null : "ERROR: The wiki page replies link was not set correctly using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		wikiOwner.deleteWiki(publicWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateWikiPage_CommunityWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = createBaseCommunity_PublicCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment");
		baseCommunity.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		WikiPage communityWikiPage = wikiOwner.createWikiPage(baseWikiPage, communityWiki);
		
		assert communityWikiPage != null : "ERROR: There was a problem with creating the wiki page using the API";
		assert communityWikiPage.getTitle().trim().equals(baseWikiPage.getName()) == true : "ERROR: The wiki page title was set incorrectly using the API";
		assert communityWikiPage.getSummary().trim().equals(baseWikiPage.getDescription()) == true : "ERROR: The wiki page description / summary was set incorrectly using the API";
		assert communityWikiPage.getSelfLink() != null : "ERROR: The wiki page self link was not set correctly using the API";
		assert communityWikiPage.getEditLink() != null : "ERROR: The wiki page edit link was not set correctly using the API";
		assert communityWikiPage.getAlternateLink() != null : "ERROR: The wiki page alternate link was not set correctly using the API";
		assert communityWikiPage.getRepliesLink() != null : "ERROR: The wiki page replies link was not set correctly using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateFollowWikiPage_Standalone_PublicWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki publicWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page");
		BaseWikiPage basePage = createBaseWikiPage(testName);
		WikiPage wikiPage = wikiOwner.createWikiPage(basePage, publicWiki);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the public wiki");
		wikiFollower.createFollow(publicWiki);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the wiki page");
		boolean followed = wikiFollower.createFollowWikiPage(wikiPage);
		
		assert followed == true : "ERROR: There was a problem with following the wiki page in the public standalone wiki using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		wikiOwner.deleteWiki(publicWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateFollowWikiPage_Standalone_PrivateWiki_AsAMember() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Private_OneMember(testName, testUser2, testUser2Profile);
		Wiki privateWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page");
		BaseWikiPage basePage = createBaseWikiPage(testName);
		WikiPage wikiPage = wikiOwner.createWikiPage(basePage, privateWiki);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the private wiki");
		wikiFollower.createFollow(privateWiki);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the wiki page");
		boolean followed = wikiFollower.createFollowWikiPage(wikiPage);
		
		assert followed == true : "ERROR: There was a problem with following the wiki page in the private standalone wiki (as a member) using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		wikiOwner.deleteWiki(privateWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateFollowWikiPage_Standalone_PrivateWiki_NotAMember() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Private_NoMembers(testName);
		Wiki privateWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page");
		BaseWikiPage basePage = createBaseWikiPage(testName);
		WikiPage wikiPage = wikiOwner.createWikiPage(basePage, privateWiki);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the wiki page");
		boolean followed = wikiFollower.createFollowWikiPage(wikiPage);
		
		assert followed == false : "ERROR: The follow wiki page request was successful despite the user not being a member of the private wiki using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		wikiOwner.deleteWiki(privateWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateFollowWikiPage_PublicCommunity() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = createBaseCommunity_PublicCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment"); 
		baseCommunity.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		WikiPage communityWikiPage = wikiOwner.createWikiPage(baseWikiPage, communityWiki);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the community wiki");
		wikiFollower.createFollowCommunityWiki(communityWiki);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the community wiki page");
		boolean followed = wikiFollower.createFollowWikiPage(communityWikiPage);
		
		assert followed == true : "ERROR: There was a problem with following the public community wiki page using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateFollowWikiPage_RestrictedCommunity_AsAMember() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a restricted community");
		BaseCommunity baseCommunity = createBaseCommunity_RestrictedCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " is now adding " + testUser2.getDisplayName() + " to the community as a member");
		communityOwner.addMemberToCommunity(testUser2, publicCommunity, Role.MEMBER);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment"); 
		baseCommunity.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		WikiPage communityWikiPage = wikiOwner.createWikiPage(baseWikiPage, communityWiki);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the community wiki");
		wikiFollower.createFollowCommunityWiki(communityWiki);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the community wiki page");
		boolean followed = wikiFollower.createFollowWikiPage(communityWikiPage);
		
		assert followed == true : "ERROR: There was a problem with following the restricted community wiki page using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateFollowWikiPage_RestrictedCommunity_NotAMember() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a restricted community");
		BaseCommunity baseCommunity = createBaseCommunity_RestrictedCommunity(testName);
		Community restrictedCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment"); 
		baseCommunity.addWidgetAPI(restrictedCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(restrictedCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		WikiPage communityWikiPage = wikiOwner.createWikiPage(baseWikiPage, communityWiki);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the community wiki page");
		boolean followed = wikiFollower.createFollowWikiPage(communityWikiPage);
		
		assert followed == false : "ERROR: The user was able to follow the wiki page in the restricted community using the API despite not being a member";
		
		log.info("INFO: Perform clean up now that the test has completed");
		communityOwner.deleteCommunity(restrictedCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_EditWikiPage_StandaloneWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki publicWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page");
		BaseWikiPage basePage = createBaseWikiPage(testName);
		WikiPage wikiPage = wikiOwner.createWikiPage(basePage, publicWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now edit the wiki page");
		WikiPage updatedWikiPage = wikiOwner.editWikiPage(wikiPage);
		
		assert updatedWikiPage != null : "ERROR: There was a problem with editing the wiki page in the public standalone wiki using the API";
		assert updatedWikiPage.getTitle().trim().equals(wikiPage.getTitle().trim()) == true : "ERROR: The updated wiki page title was set incorrectly using the API";
		assert updatedWikiPage.getSummary().trim().equals(wikiPage.getSummary().trim()) == true : "ERROR: The updated wiki page description / summary was set incorrectly using the API";
		assert updatedWikiPage.getSelfLink() != null : "ERROR: The updated wiki page self link was not set correctly using the API";
		assert updatedWikiPage.getEditLink() != null : "ERROR: The updated wiki page edit link was not set correctly using the API";
		assert updatedWikiPage.getAlternateLink() != null : "ERROR: The updated wiki page alternate link was not set correctly using the API";
		assert updatedWikiPage.getRepliesLink() != null : "ERROR: The updated wiki page replies link was not set correctly using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		wikiOwner.deleteWiki(publicWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_EditWikiPage_CommunityWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = createBaseCommunity_PublicCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment"); 
		baseCommunity.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		WikiPage wikiPage = wikiOwner.createWikiPage(baseWikiPage, communityWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now edit the community wiki page");
		WikiPage updatedWikiPage = wikiOwner.editWikiPage(wikiPage);
		
		assert updatedWikiPage != null : "ERROR: There was a problem with editing the wiki page in the community wiki using the API";
		assert updatedWikiPage.getTitle().trim().equals(wikiPage.getTitle().trim()) == true : "ERROR: The updated wiki page title was set incorrectly using the API";
		assert updatedWikiPage.getSummary().trim().equals(wikiPage.getSummary().trim()) == true : "ERROR: The updated wiki page description / summary was set incorrectly using the API";
		assert updatedWikiPage.getSelfLink() != null : "ERROR: The updated wiki page self link was not set correctly using the API";
		assert updatedWikiPage.getEditLink() != null : "ERROR: The updated wiki page edit link was not set correctly using the API";
		assert updatedWikiPage.getAlternateLink() != null : "ERROR: The updated wiki page alternate link was not set correctly using the API";
		assert updatedWikiPage.getRepliesLink() != null : "ERROR: The updated wiki page replies link was not set correctly using the API";
		
		log.info("INFO: Perform clean up now that the test has completed");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_LikeWikiPage_CommunityWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = createBaseCommunity_PublicCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment");
		baseCommunity.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		WikiPage communityWikiPage = wikiOwner.createWikiPage(baseWikiPage, communityWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now like the community wiki page");
		String likeLink = wikiOwner.likeWikiPage(communityWikiPage);
		
		assert likeLink != null : "ERROR: There was a problem with liking the community wiki page using the API";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_LikeWikiPage_StandaloneWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki publicWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page");
		BaseWikiPage basePage = createBaseWikiPage(testName);
		WikiPage wikiPage = wikiOwner.createWikiPage(basePage, publicWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now like the wiki page");
		String likeLink = wikiOwner.likeWikiPage(wikiPage);
		
		assert likeLink != null : "ERROR: There was a problem with liking the wiki page using the API";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		wikiOwner.deleteWiki(publicWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_UnlikeWikiPage_CommunityWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = createBaseCommunity_PublicCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment");
		baseCommunity.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		WikiPage communityWikiPage = wikiOwner.createWikiPage(baseWikiPage, communityWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now like the community wiki page");
		String likeLink = wikiOwner.likeWikiPage(communityWikiPage);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now unlike the community wiki page");
		boolean unliked = wikiOwner.unlikeWikiPage(likeLink);
		
		assert unliked == true : "ERROR: There was a problem with unliking the community wiki page using the API";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_UnlikeWikiPage_StandaloneWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki publicWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page");
		BaseWikiPage basePage = createBaseWikiPage(testName);
		WikiPage wikiPage = wikiOwner.createWikiPage(basePage, publicWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now like the wiki page");
		String likeLink = wikiOwner.likeWikiPage(wikiPage);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now unlike the wiki page");
		boolean unliked = wikiOwner.unlikeWikiPage(likeLink);
		
		assert unliked == true : "ERROR: There was a problem with unliking the wiki page using the API";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		wikiOwner.deleteWiki(publicWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_CommentOnCommunityWikiPage() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = createBaseCommunity_PublicCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment");
		baseCommunity.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		WikiPage communityWikiPage = wikiOwner.createWikiPage(baseWikiPage, communityWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now comment on the community wiki page");
		String commentToBePosted = Data.getData().commonComment + Helper.genStrongRand();
		
		WikiComment wikiComment = wikiOwner.addCommentToWikiPage(communityWikiPage, commentToBePosted);
		
		assert wikiComment != null : "ERROR: There was a problem with posting a comment to the community wiki page using the API";
		assert wikiComment.getSelfLink() != null : "ERROR: The self link of the WikiComment instance was not set correctly by the API";
		assert wikiComment.getEditLink() != null : "ERROR: The edit link of the WikiComment instance was not set correctly by the API";
		assert wikiComment.getAlternateLink() != null : "ERROR: The alternate link of the WikiComment instance was not set correctly by the API";
		assert wikiComment.getContent().trim().equals(commentToBePosted) == true : "ERROR: The summary of the WikiComment instance was not set correctly by the API";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_CommentOnStandaloneWikiPage() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki publicWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page");
		BaseWikiPage basePage = createBaseWikiPage(testName);
		WikiPage wikiPage = wikiOwner.createWikiPage(basePage, publicWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now comment on the standalone wiki page");
		String commentToBePosted = Data.getData().commonComment + Helper.genStrongRand();
		
		WikiComment wikiComment = wikiOwner.addCommentToWikiPage(wikiPage, commentToBePosted);
		
		assert wikiComment != null : "ERROR: There was a problem with posting a comment to the wiki page using the API";
		assert wikiComment.getSelfLink() != null : "ERROR: The self link of the WikiComment instance was not set correctly by the API";
		assert wikiComment.getEditLink() != null : "ERROR: The edit link of the WikiComment instance was not set correctly by the API";
		assert wikiComment.getAlternateLink() != null : "ERROR: The alternate link of the WikiComment instance was not set correctly by the API";
		assert wikiComment.getContent().trim().equals(commentToBePosted) == true : "ERROR: The summary of the WikiComment instance was not set correctly by the API";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		wikiOwner.deleteWiki(publicWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_EditCommentOnCommunityWikiPage() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = createBaseCommunity_PublicCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment");
		baseCommunity.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		WikiPage communityWikiPage = wikiOwner.createWikiPage(baseWikiPage, communityWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now comment on the community wiki page");
		String commentToBePosted = Data.getData().commonComment + Helper.genStrongRand();
		WikiComment wikiComment = wikiOwner.addCommentToWikiPage(communityWikiPage, commentToBePosted);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now edit the comment posted to the community wiki page");
		String commentEdited = Data.getData().commonComment + Helper.genStrongRand();
		WikiComment updatedComment = wikiOwner.editCommentOnWikiPage(wikiComment, commentEdited); 
		
		assert updatedComment != null : "ERROR: There was a problem with editing the comment posted to the community wiki page using the API";
		assert updatedComment.getSelfLink() != null : "ERROR: The self link of the WikiComment instance was not set correctly by the API";
		assert updatedComment.getSelfLink().equals(wikiComment.getSelfLink()) == true : "ERROR: The self link was changed unexpectedly after editing the comment using the API";
		assert updatedComment.getEditLink() != null : "ERROR: The edit link of the WikiComment instance was not set correctly by the API";
		assert updatedComment.getEditLink().equals(wikiComment.getEditLink()) == true : "ERROR: The edit link was changed unexpectedly after editing the comment using the API";
		assert updatedComment.getAlternateLink() != null : "ERROR: The alternate link of the WikiComment instance was not set correctly by the API";
		assert updatedComment.getAlternateLink().equals(wikiComment.getAlternateLink()) == true : "ERROR: The alternate link was changed unexpectedly after editing the comment using the API";
		assert updatedComment.getContent().trim().equals(commentToBePosted) == false : "ERROR: The comment after editing was not updated correctly in the WikiComment instance by the API";
		assert updatedComment.getContent().trim().equals(commentEdited) == true : "ERROR: The comment of the WikiComment instance was not set correctly by the API";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_EditCommentOnStandaloneWikiPage() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki publicWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page");
		BaseWikiPage basePage = createBaseWikiPage(testName);
		WikiPage wikiPage = wikiOwner.createWikiPage(basePage, publicWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now comment on the standalone wiki page");
		String commentToBePosted = Data.getData().commonComment + Helper.genStrongRand();
		WikiComment wikiComment = wikiOwner.addCommentToWikiPage(wikiPage, commentToBePosted);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now edit the comment posted to the wiki page");
		String commentEdited = Data.getData().commonComment + Helper.genStrongRand();
		WikiComment updatedComment = wikiOwner.editCommentOnWikiPage(wikiComment, commentEdited); 
		
		assert updatedComment != null : "ERROR: There was a problem with editing the comment posted to the wiki page using the API";
		assert updatedComment.getSelfLink() != null : "ERROR: The self link of the WikiComment instance was not set correctly by the API";
		assert updatedComment.getSelfLink().equals(wikiComment.getSelfLink()) == true : "ERROR: The self link was changed unexpectedly after editing the comment using the API";
		assert updatedComment.getEditLink() != null : "ERROR: The edit link of the WikiComment instance was not set correctly by the API";
		assert updatedComment.getEditLink().equals(wikiComment.getEditLink()) == true : "ERROR: The edit link was changed unexpectedly after editing the comment using the API";
		assert updatedComment.getAlternateLink() != null : "ERROR: The alternate link of the WikiComment instance was not set correctly by the API";
		assert updatedComment.getAlternateLink().equals(wikiComment.getAlternateLink()) == true : "ERROR: The alternate link was changed unexpectedly after editing the comment using the API";
		assert updatedComment.getContent().trim().equals(commentToBePosted) == false : "ERROR: The comment after editing was not updated correctly in the WikiComment instance by the API";
		assert updatedComment.getContent().trim().equals(commentEdited) == true : "ERROR: The comment of the WikiComment instance was not set correctly by the API";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		wikiOwner.deleteWiki(publicWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_DeleteWikiPageComment_CommunityWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = createBaseCommunity_PublicCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunity);
		
		log.info("INFO: Add Wiki's widget to OnPrem environment");
		baseCommunity.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		WikiPage communityWikiPage = wikiOwner.createWikiPage(baseWikiPage, communityWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now comment on the community wiki page");
		String commentToBePosted = Data.getData().commonComment + Helper.genStrongRand();
		WikiComment wikiComment = wikiOwner.addCommentToWikiPage(communityWikiPage, commentToBePosted);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now delete the comment posted to the community wiki page");
		boolean deleted = wikiOwner.deleteWikiPageComment(wikiComment);
		
		assert deleted == true : "ERROR: There was a problem with deleting the comment posted to the community wiki page using the API";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_DeleteWikiPageComment_StandaloneWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki publicWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page");
		BaseWikiPage basePage = createBaseWikiPage(testName);
		WikiPage wikiPage = wikiOwner.createWikiPage(basePage, publicWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now comment on the wiki page");
		String commentToBePosted = Data.getData().commonComment + Helper.genStrongRand();
		WikiComment wikiComment = wikiOwner.addCommentToWikiPage(wikiPage, commentToBePosted);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now delete the comment posted to the wiki page");
		boolean deleted = wikiOwner.deleteWikiPageComment(wikiComment);
		
		assert deleted == true : "ERROR: There was a problem with deleting the comment posted to the wiki page using the API";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		wikiOwner.deleteWiki(publicWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateWikiPageWithMentions_PublicWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String beforeMentionsText = Data.getData().buttonCancel + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonSave + Helper.genStrongRand();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone public wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki publicWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page with mentions on the public wiki");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		Mentions mentions = createMentions(testUser2, testUser2Profile, beforeMentionsText, afterMentionsText);
		
		WikiPage publicWikiPage = wikiOwner.createWikiPageWithMentions(publicWiki, baseWikiPage, mentions);
		
		String wikiPageContent = publicWikiPage.getSummary().trim();
		assert publicWikiPage != null : "ERROR: There was a problem with creating the public wiki page";
		assert wikiPageContent.indexOf(baseWikiPage.getDescription().trim()) > -1 : "ERROR: The summary of the public wiki page did not contain the base wiki page description as expected";
		assert wikiPageContent.indexOf(beforeMentionsText) > -1 : "ERROR: The summary of the public wiki page did not contain the before mentions text as expected";
		assert wikiPageContent.indexOf(testUser2Profile.getDesplayName()) > -1 : "ERROR: The summary of the public wiki page did not contain the username to be mentioned as expected";
		assert wikiPageContent.indexOf(afterMentionsText) > -1 : "ERROR: The summary of the public wiki page did not contain the after mentions text as expected";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		wikiOwner.deleteWiki(publicWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateWikiPageWithMentions_SharedWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String beforeMentionsText = Data.getData().buttonCancel + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonSave + Helper.genStrongRand();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone shared wiki");
		BaseWiki baseWikiShared = createBaseWiki_Standalone_Private_OneMember(testName, testUser2, testUser2Profile);
		Wiki sharedWiki = wikiOwner.createWiki(baseWikiShared);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page with mentions on the shared wiki");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		Mentions mentions = createMentions(testUser2, testUser2Profile, beforeMentionsText, afterMentionsText);	
		
		WikiPage sharedWikiPage = wikiOwner.createWikiPageWithMentions(sharedWiki, baseWikiPage, mentions);
		
		String wikiPageContent = sharedWikiPage.getSummary().trim();
		assert sharedWikiPage != null : "ERROR: There was a problem with creating the shared wiki page";
		assert wikiPageContent.indexOf(baseWikiPage.getDescription().trim()) > -1 : "ERROR: The summary of the shared wiki page did not contain the base wiki page description as expected";
		assert wikiPageContent.indexOf(beforeMentionsText) > -1 : "ERROR: The summary of the shared wiki page did not contain the before mentions text as expected";
		assert wikiPageContent.indexOf(testUser2Profile.getDesplayName()) > -1 : "ERROR: The summary of the shared wiki page did not contain the username to be mentioned as expected";
		assert wikiPageContent.indexOf(afterMentionsText) > -1 : "ERROR: The summary of the shared wiki page did not contain the after mentions text as expected";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		wikiOwner.deleteWiki(sharedWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateWikiPageWithMentions_PrivateWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String beforeMentionsText = Data.getData().buttonCancel + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonSave + Helper.genStrongRand();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone private wiki");
		BaseWiki baseWikiPriv = createBaseWiki_Standalone_Private_NoMembers(testName);
		Wiki privateWiki = wikiOwner.createWiki(baseWikiPriv);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page with mentions on the private wiki");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		Mentions mentions = createMentions(testUser2, testUser2Profile, beforeMentionsText, afterMentionsText);
		
		WikiPage privateWikiPage = wikiOwner.createWikiPageWithMentions(privateWiki, baseWikiPage, mentions);
		
		String wikiPageContent = privateWikiPage.getSummary().trim();
		assert privateWikiPage != null : "ERROR: There was a problem with creating the private wiki page";
		assert wikiPageContent.indexOf(baseWikiPage.getDescription().trim()) > -1 : "ERROR: The summary of the private wiki page did not contain the base wiki page description as expected";
		assert wikiPageContent.indexOf(beforeMentionsText) > -1 : "ERROR: The summary of the private wiki page did not contain the before mentions text as expected";
		assert wikiPageContent.indexOf(testUser2Profile.getDesplayName()) > -1 : "ERROR: The summary of the private wiki page did not contain the username to be mentioned as expected";
		assert wikiPageContent.indexOf(afterMentionsText) > -1 : "ERROR: The summary of the private wiki page did not contain the after mentions text as expected";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		wikiOwner.deleteWiki(privateWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateWikiPageWithMentions_PublicCommunity() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String beforeMentionsText = Data.getData().buttonCancel + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonSave + Helper.genStrongRand();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunityPub = this.createBaseCommunity_PublicCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunityPub);
		
		log.info("INFO: Add Wiki's widget to public community");
		baseCommunityPub.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the community wiki");
		log.info("INFO: This is essential for the mentions notification to be physically sent to " + testUser2.getDisplayName());
		wikiFollower.createFollowCommunityWiki(communityWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page with mentions on the public community");
		BaseWikiPage baseWikiPage = this.createBaseWikiPage(testName);
		Mentions mentions = createMentions(testUser2, testUser2Profile, beforeMentionsText, afterMentionsText);
		
		WikiPage publicCommunityWikiPage = wikiOwner.createWikiPageWithMentions(communityWiki, baseWikiPage, mentions);
		
		String wikiPageContent = publicCommunityWikiPage.getSummary().trim();
		assert publicCommunityWikiPage != null : "ERROR: There was a problem with creating the public community wiki page";
		assert wikiPageContent.indexOf(baseWikiPage.getDescription().trim()) > -1 : "ERROR: The summary of the public community wiki page did not contain the base wiki page description as expected";
		assert wikiPageContent.indexOf(beforeMentionsText) > -1 : "ERROR: The summary of the public community wiki page did not contain the before mentions text as expected";
		assert wikiPageContent.indexOf(testUser2Profile.getDesplayName()) > -1 : "ERROR: The summary of the public community wiki page did not contain the username to be mentioned as expected";
		assert wikiPageContent.indexOf(afterMentionsText) > -1 : "ERROR: The summary of the public community wiki page did not contain the after mentions text as expected";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateWikiPageWithMentions_ModeratedCommunity() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String beforeMentionsText = Data.getData().buttonCancel + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonSave + Helper.genStrongRand();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a moderated community");
		BaseCommunity baseCommunityMod = createBaseCommunity_ModeratedCommunity(testName);
		Community moderatedCommunity = communityOwner.createCommunity(baseCommunityMod);
		
		log.info("INFO: Add Wiki's widget to moderated community");
		baseCommunityMod.addWidgetAPI(moderatedCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(moderatedCommunity);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the community wiki");
		log.info("INFO: This is essential for the mentions notification to be physically sent to " + testUser2.getDisplayName());
		wikiFollower.createFollowCommunityWiki(communityWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page with mentions on the moderated community");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);
		Mentions mentions = createMentions(testUser2, testUser2Profile, beforeMentionsText, afterMentionsText);
		
		WikiPage moderatedCommunityWikiPage = wikiOwner.createWikiPageWithMentions(communityWiki, baseWikiPage, mentions);
		
		String wikiPageContent = moderatedCommunityWikiPage.getSummary().trim();
		assert moderatedCommunityWikiPage != null : "ERROR: There was a problem with creating the moderated community wiki page";
		assert wikiPageContent.indexOf(baseWikiPage.getDescription().trim()) > -1 : "ERROR: The summary of the moderated community wiki page did not contain the base wiki page description as expected";
		assert wikiPageContent.indexOf(beforeMentionsText) > -1 : "ERROR: The summary of the moderated community wiki page did not contain the before mentions text as expected";
		assert wikiPageContent.indexOf(testUser2Profile.getDesplayName()) > -1 : "ERROR: The summary of the moderated community wiki page did not contain the username to be mentioned as expected";
		assert wikiPageContent.indexOf(afterMentionsText) > -1 : "ERROR: The summary of the moderated community wiki page did not contain the after mentions text as expected";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		communityOwner.deleteCommunity(moderatedCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_CreateWikiPageWithMentions_RestrictedCommunity() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String beforeMentionsText = Data.getData().buttonCancel + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonSave + Helper.genStrongRand();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiFollower = new APIWikisHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a restricted community");
		BaseCommunity baseCommunityRes = createBaseCommunity_RestrictedCommunity(testName);
		Community restrictedCommunity = communityOwner.createCommunity(baseCommunityRes);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add " + testUser2.getDisplayName() + " to the restricted community as a member");
		communityOwner.addMemberToCommunity(testUser2, restrictedCommunity, Role.MEMBER);
		
		log.info("INFO: Add Wiki's widget to restricted community");
		baseCommunityRes.addWidgetAPI(restrictedCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(restrictedCommunity);
		
		log.info("INFO: " + testUser2.getDisplayName() + " will now follow the community wiki");
		log.info("INFO: This is essential for the mentions notification to be physically sent to " + testUser2.getDisplayName());
		wikiFollower.createFollowCommunityWiki(communityWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page with mentions on the restriced community");
		BaseWikiPage baseWikiPage = createBaseWikiPage(testName);		
		Mentions mentions = createMentions(testUser2, testUser2Profile, beforeMentionsText, afterMentionsText);

		WikiPage restrictedCommunityWikiPage = wikiOwner.createWikiPageWithMentions(communityWiki, baseWikiPage, mentions);
		
		String wikiPageContent = restrictedCommunityWikiPage.getSummary().trim();
		assert restrictedCommunityWikiPage != null : "ERROR: There was a problem with creating the public community wiki page";
		assert wikiPageContent.indexOf(baseWikiPage.getDescription().trim()) > -1 : "ERROR: The summary of the restricted community wiki page did not contain the base wiki page description as expected";
		assert wikiPageContent.indexOf(beforeMentionsText) > -1 : "ERROR: The summary of the restricted community wiki page did not contain the before mentions text as expected";
		assert wikiPageContent.indexOf(testUser2Profile.getDesplayName()) > -1 : "ERROR: The summary of the restricted community wiki page did not contain the username to be mentioned as expected";
		assert wikiPageContent.indexOf(afterMentionsText) > -1 : "ERROR: The summary of the restricted community wiki page did not contain the after mentions text as expected";
		
		log.info("INFO: Perform clean up now that the API test has finished");
		communityOwner.deleteCommunity(restrictedCommunity);
	}
	
	@Test(groups={"apitest"})
	public void test_AddMentionCommentWikiPage_StandaloneWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String beforeMentionsText = Data.getData().buttonPost + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonRemove + Helper.genStrongRand();
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a standalone public wiki");
		BaseWiki baseWiki = createBaseWiki_Standalone_Public(testName);
		Wiki publicWiki = wikiOwner.createWiki(baseWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a wiki page on the public wiki");
		BaseWikiPage basePage = createBaseWikiPage(testName);
		WikiPage publicWikiPage = wikiOwner.createWikiPage(basePage, publicWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add a comment to the wiki page and mention " + testUser2.getDisplayName());
		Mentions mentions = createMentions(testUser2, testUser2Profile, beforeMentionsText, afterMentionsText);
		WikiComment mentionComment = wikiOwner.addMentionCommentToWikiPage(publicWikiPage, mentions);
		
		assert mentionComment != null : "ERROR: There was a problem with posting the comment with mention to " + testUser2.getDisplayName() + " using the API";
		assert mentionComment.getContent().trim().indexOf(beforeMentionsText) > -1 :
										"ERROR: The text posted before the mention was not set in the wiki comment as expected";
		assert mentionComment.getContent().trim().indexOf(afterMentionsText) > -1 :
										"ERROR: The text posted after the mention was not set in the wiki comment as expected";
		assert mentionComment.getContent().trim().indexOf(testUser2Profile.getDesplayName()) > -1 :
										"ERROR: The user name to be mentioned was not set in the wiki comment as expected";
		assert mentionComment.getContent().trim().indexOf(testUser2Profile.getUUID()) > -1 :
										"ERROR: The UUID of the user name to be mentioned was not set in the wiki comment as expected";
		
		log.info("INFO: Cleaning up now that the test has completed");
		wikiOwner.deleteWiki(publicWiki);
	}
	
	@Test(groups={"apitest"})
	public void test_AddMentionCommentWikiPage_CommunityWiki() {
		
		// Configurations for this test
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		String beforeMentionsText = Data.getData().buttonPost + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonRemove + Helper.genStrongRand();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIWikisHandler wikiOwner = new APIWikisHandler(testURL, testUser.getUid(), testUser.getPassword());
		APIProfilesHandler testUser2Profile = new APIProfilesHandler(testURL, testUser2.getUid(), testUser2.getPassword());
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunityPub = createBaseCommunity_PublicCommunity(testName);
		Community publicCommunity = communityOwner.createCommunity(baseCommunityPub);
		
		log.info("INFO: Add Wiki's widget to public community");
		baseCommunityPub.addWidgetAPI(publicCommunity, communityOwner, BaseWidget.WIKI);
		
		log.info("INFO: Retrieve the Wiki instance of the community wiki");
		Wiki communityWiki = wikiOwner.getCommunityWiki(publicCommunity);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now create a community wiki page on the public community");
		BaseWikiPage baseWikiPagePub = createBaseWikiPage(testName);
		WikiPage publicCommunityWikiPage = wikiOwner.createWikiPage(baseWikiPagePub, communityWiki);
		
		log.info("INFO: " + testUser.getDisplayName() + " will now add a comment to the wiki page and mention " + testUser2.getDisplayName());
		Mentions mentions = createMentions(testUser2, testUser2Profile, beforeMentionsText, afterMentionsText);
		WikiComment mentionComment = wikiOwner.addMentionCommentToWikiPage(publicCommunityWikiPage, mentions);
		
		assert mentionComment != null : "ERROR: There was a problem with posting the comment with mention to " + testUser2.getDisplayName() + " using the API";
		assert mentionComment.getContent().trim().indexOf(beforeMentionsText) > -1 :
										"ERROR: The text posted before the mention was not set in the wiki comment as expected";
		assert mentionComment.getContent().trim().indexOf(afterMentionsText) > -1 :
										"ERROR: The text posted after the mention was not set in the wiki comment as expected";
		assert mentionComment.getContent().trim().indexOf(testUser2Profile.getDesplayName()) > -1 :
										"ERROR: The user name to be mentioned was not set in the wiki comment as expected";
		assert mentionComment.getContent().trim().indexOf(testUser2Profile.getUUID()) > -1 :
										"ERROR: The UUID of the user name to be mentioned was not set in the wiki comment as expected";
		
		log.info("INFO: Cleaning up now that the test has completed");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	/**
	 * Creates a BaseWiki object for a public standalone wiki
	 * 
	 * @param testName - The testName assigned in the test case (used for the name of the wiki)
	 * @return - The created BaseWiki instance
	 */
	private BaseWiki createBaseWiki_Standalone_Public(String testName) {
		BaseWiki baseWiki = new BaseWiki.Builder(testName + Helper.genStrongRand())
										.readAccess(ReadAccess.All)
										.editAccess(EditAccess.AllLoggedIn)
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.description(Data.getData().commonDescription + Helper.genStrongRand())
										.build();
		return baseWiki;
	}
	
	/**
	 * Creates a BaseWiki object for a private standalone wiki
	 * This wiki will not have any additional members
	 * 
	 * @param testName - The testName assigned in the test case (used for the name of the wiki)
	 * @return - The created BaseWiki instance
	 */
	private BaseWiki createBaseWiki_Standalone_Private_NoMembers(String testName) {
		BaseWiki baseWiki = new BaseWiki.Builder(testName + Helper.genStrongRand())
										.readAccess(ReadAccess.WikiOnly)
										.editAccess(EditAccess.EditorsAndOwners)
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.description(Data.getData().commonDescription + Helper.genStrongRand())
										.build();
		return baseWiki;
	}
	
	/**
	 * Creates a BaseWiki object for a private standalone wiki
	 * This wiki will have one additional member
	 * 
	 * @param testName - The testName assigned in the test case (used for the name of the wiki)
	 * @param user - The User instance of the user to be added to the wiki as a member
	 * @param userProfile - The APIProfilesHandler instance of the user to be added to the wiki as a member
	 * @return - The created BaseWiki instance
	 */
	private BaseWiki createBaseWiki_Standalone_Private_OneMember(String testName, User user, APIProfilesHandler userProfile) {
		BaseWiki baseWiki = new BaseWiki.Builder(testName + Helper.genStrongRand())
										.readAccess(ReadAccess.WikiOnly)
										.editAccess(EditAccess.EditorsAndOwners)
										.tags(Data.getData().commonTag + Helper.genStrongRand())
										.description(Data.getData().commonDescription + Helper.genStrongRand())
										.addMember(new Member(WikiRole.EDITOR, user, userProfile.getUUID()))
										.build();
		return baseWiki;
	}
	
	/**
	 * Creates a BaseCommunity object for a public community
	 * 
	 * @param testName - The testName assigned in the test case (used for the name of the community)
	 * @return - The created BaseCommunity instance
	 */
	private BaseCommunity createBaseCommunity_PublicCommunity(String testName) {
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genStrongRand())
	 	   												.tags(Data.getData().commonTag + Helper.genStrongRand())
	 	   												.access(Access.PUBLIC)
	 	   												.description(Data.getData().commonDescription + Helper.genStrongRand())
	 	   												.build();
		return baseCommunity;
	}
	
	/**
	 * Creates a BaseCommunity object for a moderated community
	 * 
	 * @param testName - The testName assigned in the test case (used for the name of the community)
	 * @return - The created BaseCommunity instance
	 */
	private BaseCommunity createBaseCommunity_ModeratedCommunity(String testName) {
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genStrongRand())
	 	   												.tags(Data.getData().commonTag + Helper.genStrongRand())
	 	   												.access(Access.MODERATED)
	 	   												.description(Data.getData().commonDescription + Helper.genStrongRand())
	 	   												.build();
		return baseCommunity;
	}
	
	/**
	 * Creates a BaseCommunity object for a restricted community
	 * 
	 * @param testName - The testName assigned in the test case (used for the name of the community)
	 * @return - The created BaseCommunity instance
	 */
	private BaseCommunity createBaseCommunity_RestrictedCommunity(String testName) {
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genStrongRand())
														.tags(Data.getData().commonTag + Helper.genStrongRand())
														.access(Access.RESTRICTED)
														.shareOutside(false)
														.description(Data.getData().commonDescription + Helper.genStrongRand())
														.build();
		return baseCommunity;
	}
	
	/**
	 * Creates a BaseWikiPage object for creating a Wiki Page
	 * 
	 * @param testName - The testName assigned in the test case (used for the name of the wiki page)
	 * @return - The created BaseWikiPage instance
	 */
	private BaseWikiPage createBaseWikiPage(String testName) {
		BaseWikiPage baseWikiPage = new BaseWikiPage.Builder(testName + Helper.genStrongRand(), PageType.Peer)
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.description(Data.getData().commonDescription + Helper.genStrongRand())
													.build();
		return baseWikiPage;
	}
	
	/**
	 * Creates a Mentions object for adding mentions to another user into a wiki page
	 * 
	 * @param user - The User instance of the user to be mentioned
	 * @param userProfile - The APIProfilesHandler instance of the user to be mentioned
	 * @param beforeMentionsText - The text to appear before the mention
	 * @param afterMentionsText - The text to appear after the mention
	 * @return - The created Mentions instance
	 */
	private Mentions createMentions(User user, APIProfilesHandler userProfile, String beforeMentionsText, String afterMentionsText) {
		Mentions mentions = new Mentions.Builder(user, userProfile.getUUID())
										.browserURL(testURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		return mentions;
	}
}