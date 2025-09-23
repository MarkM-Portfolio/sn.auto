package com.ibm.conn.auto.tests.GDPR;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
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
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.appobjects.role.WikiRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Wiki_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

@Deprecated //The GDPR component is obsolete now, hence this class has been deprecated
public class Wikis_GDPR_DataPop extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(Wikis_GDPR_DataPop.class);
	private TestConfigCustom cfg;
	private CommunitiesUI commUI;
	private WikisUI wUI;
	private APICommunitiesHandler apiCommOwner1, apiCommOwner2;
	private APIWikisHandler apiWkOwner1, apiWkOwner2;
	private String serverURL;
	private User testUser1, testUser2;
	private List<Member> members;
	private boolean isOnPremise;
	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		wUI = WikisUI.getGui(cfg.getProductName(), driver);

		//Load Users
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiCommOwner1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiCommOwner2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		apiWkOwner1 = new APIWikisHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiWkOwner2 = new APIWikisHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());

		//Create wiki member list
		members = new ArrayList<Member>();	
		
		URLConstants.setServerURL(serverURL);
		
		//check environment to see if on-prem or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
	}	
	

	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Community Wiki: Edit Child Page and Add Comment </li>
	 * <li><B>Step:</B> UserA creates a community using the API </li>
	 * <li><B>Step:</B> UserA adds wiki app - if on-premises </li>
	 * <li><B>Step:</B> UserA adds a child page </li>
	 * <li><B>Step:</B> UserA adds a comment </li>
	 * <li><B>Step:</B> UserA edits the wiki page name </li>
	 * </ul>	 
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAEditAndCommentCommunityWikiChildPage() {

		String testName = wUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR " + testName + Helper.genDateBasedRand())
                                     .description("GDPR data population - Community Wiki: UserA Edits Child Page and Adds Comment")
                                     .access(Access.PUBLIC)
                                     .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR " + testName + Helper.genDateBasedRand(), PageType.Child)
									.tags("tag1, tag2")
									.description("GDPR data population - Community Wiki: UserA Edits Child Page and Adds Comment")
									.build();

		BaseWikiPage newWikiPage = new BaseWikiPage.Builder(Data.getData().editedData + wikiPage.getName(), PageType.Child)
												.tags("updated_tag1, updated_tag2")
												.description("GDPR data population - Updating the child page with new content")
												.build();

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiCommOwner1.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiCommOwner1, BaseWidget.WIKI);
		}
		
		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser1);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(wUI);

		log.info("INFO: Add a comment to wiki page");
		wUI.addComment(Data.getData().Comment_For_Public_Wiki);
		
		log.info("INFO: Edit the current Wiki Page");
		wUI.editWikiPage(newWikiPage);
		
		wUI.endTest();		
	}	
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Community Wiki: Edit Peer Page and Add Comment </li>
	 * <li><B>Step:</B> UserA creates a community using the API </li>
	 * <li><B>Step:</B> UserA adds wiki app - if on-premises </li>
	 * <li><B>Step:</B> UserA adds a peer page </li>
	 * <li><B>Step:</B> UserA adds a comment </li>
	 * <li><B>Step:</B> UserA edits the wiki page name </li>
	 * </ul>	 
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAEditAndCommentCommunityWikiPeerPage() {

		String testName = wUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR " + testName + Helper.genDateBasedRand())
                                     .description("GDPR data population - Community Wiki: UserA Edits Peer Page and Adds Comment")
                                     .access(Access.PUBLIC)
                                     .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR " + testName + Helper.genDateBasedRand(), PageType.Peer)
									.tags("tag1, tag2")
									.description("GDPR data population - Community Wiki: UserA Edits Peer Page and Adds Comment")
									.build();

		BaseWikiPage newWikiPage = new BaseWikiPage.Builder(Data.getData().editedData + wikiPage.getName(), PageType.Peer)
												.tags("updated_tag1, updated_tag2")
												.description("GDPR data population - Updating the peer page with new content")
												.build();

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiCommOwner1.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiCommOwner1, BaseWidget.WIKI);
		}
		
		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser1);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(wUI);

		log.info("INFO: Add a comment to wiki page");
		wUI.addComment(Data.getData().Comment_For_Public_Wiki);
		
		log.info("INFO: Edit the current Wiki Page");
		wUI.editWikiPage(newWikiPage);
		
		wUI.endTest();		
	}	

	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Community Wiki: UserA Edits And Comments Child Page Created By UserB </li>
	 * <li><B>Step:</B> UserB creates a community using the API </li>
	 * <li><B>Step:</B> UserB adds wiki app - if on-premises </li>
	 * <li><B>Step:</B> UserB adds a child page </li>
	 * <li><B>Step:</B> UserA adds a comment </li>
	 * <li><B>Step:</B> UserA edits the wiki page name </li>
	 * </ul>	 
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAEditAndCommentCommunityWikiChildPageCreatedByUserB() {

		String testName = wUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR " + testName + Helper.genDateBasedRand())
                                     .description("GDPR data population - Community Wiki: UserA Edits And Comments Child Page Created By UserB")
                                     .access(Access.PUBLIC)
                                     .addMember(new Member(CommunityRole.MEMBERS, testUser1))
                                     .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR " + testName + Helper.genDateBasedRand(), PageType.Child)
									.tags("tag1, tag2")
									.description("GDPR data population - Community Wiki: UserA Edits And Comments Child Page Created By UserB")
									.build();

		BaseWikiPage newWikiPage = new BaseWikiPage.Builder(Data.getData().editedData + wikiPage.getName(), PageType.Child)
												.tags("updated_tag1, updated_tag2")
												.description("GDPR data population - Updating the child page with new content")
												.build();

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiCommOwner2.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiCommOwner2, BaseWidget.WIKI);
		}
		
		log.info("INFO: Log into Communities as: " + testUser2.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser2);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(wUI);

		log.info("INFO: Community owner logs out");
		wUI.logout();		
		wUI.close(cfg);	
			
		log.info("INFO: Log into Community as: " + testUser1.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser1);		

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Wait for Wiki page header to appear");
		wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
				
		log.info("INFO: Select the wiki page from the left navigation panel");
		driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();		
		
		log.info("INFO: Add a comment to wiki page");
		wUI.addComment(Data.getData().Comment_For_Public_Wiki);
		
		log.info("INFO: Edit the current Wiki Page");
		wUI.editWikiPage(newWikiPage);
		
		wUI.endTest();		
	}		
	

	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Community Wiki: UserA Edits and Comments on Peer Page Created by UserB </li>
	 * <li><B>Step:</B> UserB creates a community using the API </li>
	 * <li><B>Step:</B> UserB adds wiki app - if on-premises </li>
	 * <li><B>Step:</B> UserB adds a peer page </li>
	 * <li><B>Step:</B> UserA adds a comment </li>
	 * <li><B>Step:</B> UserA edits the wiki page name </li>
	 * </ul>	 
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAEditAndCommentCommunityWikiPeerPageCreatedByUserB() {

		String testName = wUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR " + testName + Helper.genDateBasedRand())
                                     .description("GDPR data population - Community Wiki: UserA Edits and Comments on Peer Page Created By UserB")
                                     .access(Access.PUBLIC)
                                     .addMember(new Member(CommunityRole.MEMBERS, testUser1))
                                     .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR " + testName + Helper.genDateBasedRand(), PageType.Peer)
									.tags("tag1, tag2")
									.description("GDPR data population - Community Wiki: UserA Edits and Comments on Peer Page Created By UserB")
									.build();

		BaseWikiPage newWikiPage = new BaseWikiPage.Builder(Data.getData().editedData + wikiPage.getName(), PageType.Peer)
												.tags("updated_tag1, updated_tag2")
												.description("GDPR data population - UserA Edits and Comments on Peer Page Created By UserB")
												.build();

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiCommOwner2.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiCommOwner2, BaseWidget.WIKI);
		}
		
		log.info("INFO: Log into Communities as: " + testUser2.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser2);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(wUI);

		log.info("INFO: Community owner logs out");
		wUI.logout();		
		wUI.close(cfg);	
			
		log.info("INFO: Log into Community as: " + testUser1.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser1);		

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Wait for Wiki page header to appear");
		wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
				
		log.info("INFO: Select the wiki page from the left navigation panel");
		driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();		
		
		log.info("INFO: Add a comment to wiki page");
		wUI.addComment(Data.getData().Comment_For_Public_Wiki);
		
		log.info("INFO: Edit the current Wiki Page");
		wUI.editWikiPage(newWikiPage);
		
		wUI.endTest();		
	}	
	
	
	/**
	 * <ul> 
	 * <li><B>Info:</B> Data Population - Standalone Wikis: Edit Child Page and Add Comment </li>
	 * <li><B>Step:</B> UserA creates a public wiki </li>
	 * <li><B>Step:</B> UserA adds a child page </li>
	 * <li><B>Step:</B> UserA adds a comment </li>
	 * <li><B>Step:</B> UserA edits the wiki page name </li>
	 * </ul>
	 * NOTE: this isn't supported on the cloud, there is no standalone wiki in the cloud
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userAEditAndCommentStandaloneWikiChildPage() {

		if(!isOnPremise){
			throw new SkipException("Environment is Cloud - Standalone Wikis are not supported - skipping tests");
		}
		else
		{
			String testName = wUI.startTest();

			BaseWiki wiki = new BaseWiki.Builder("GDPR " + testName + Helper.genDateBasedRand())
								    	.tags("tag" + Helper.genDateBasedRand())
								    	.description("GDPR data population - Description for test " + testName)
								    	.build();

			BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR Standalone Child Page to be edited " + Helper.genDateBasedRand(), PageType.Child)
													.tags("tag1")
													.description("GDPR data population - Standalone Wikis: UserA Edits Child Page and Adds Comment")
													.build();
		
			log.info("INFO: Create a new Wiki using API");
			wiki.createAPI(apiWkOwner1);
		
			log.info("INFO: Log into Wikis as: " + testUser1.getDisplayName());
			wUI.loadComponent(Data.getData().ComponentWikis);
			wUI.login(testUser1);
		
			log.info("INFO: Click on the Wiki view: I'm an Owner");
			Wiki_LeftNav_Menu.OWNER.select(wUI);
		
			log.info("INFO: Open the Wiki created via API");
			wUI.clickLinkWait(WikisUI.getWiki(wiki));
		
			log.info("INFO: Wait for Wiki page header to appear");
			wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

			log.info("INFO: Create a child page");
			wikiPage.create(wUI);		

			log.info("INFO: Select the wiki page from the left navigation panel");
			driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();

			log.info("INFO: Add a comment to wiki page");
			wUI.addComment(Data.getData().Comment_For_Public_Wiki);
		
			log.info("INFO: Change the name of the Wiki page inside the BaseWikiPage");
			wikiPage.setName(Data.getData().editedData + wikiPage.getName());

			log.info("INFO: Change the description of the Wiki Page inside the BaseWikiPage");
			wikiPage.setDescription(Data.getData().editedData + wikiPage.getDescription());
		
			log.info("INFO: Edit the current Wiki page");
			wUI.editWikiPage(wikiPage);	

			wUI.endTest();	
		}
	}	
	
	
	/**
	 * <ul> 
	 * <li><B>Info:</B> Data Population - Standalone Wikis: Edit Peer Page and Add Comment </li>
	 * <li><B>Step:</B> UserA creates a public wiki </li>
	 * <li><B>Step:</B> UserA adds a peer page </li>
	 * <li><B>Step:</B> UserA adds a comment </li>
	 * <li><B>Step:</B> UserA edits the wiki page name </li>
	 * </ul>
	 * NOTE: this isn't supported on the cloud, there is no standalone wiki in the cloud
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userAEditAndCommentStandaloneWikiPeerPage() {

		if(!isOnPremise){
			throw new SkipException("Environment is Cloud - Standalone Wikis are not supported - skipping tests");
		}
		else
		{
			String testName = wUI.startTest();

			BaseWiki wiki = new BaseWiki.Builder("GDPR " + testName + Helper.genDateBasedRand())
								    	.tags("tag" + Helper.genDateBasedRand())
								    	.description("GDPR data population - Description for test " + testName)
								    	.build();

			BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR Standalone Peer Page to be edited " + Helper.genDateBasedRand(), PageType.Peer)
													.tags("tag1")
													.description("GDPR data population - Standalone Wikis: UserA Edits Peer Page and Adds Comment")
													.build();
				
			log.info("INFO: Create a new Wiki using API");
			Wiki apiWiki = wiki.createAPI(apiWkOwner1);
		
			log.info("INFO: Create a peer page using API");
			wikiPage.createAPI(apiWkOwner1, apiWiki);
		
			log.info("INFO: Log into Wikis as: " + testUser1.getDisplayName());
			wUI.loadComponent(Data.getData().ComponentWikis);
			wUI.login(testUser1);
		
			log.info("INFO: Click on the Wiki view: I'm an Owner");
			Wiki_LeftNav_Menu.OWNER.select(wUI);
		
			log.info("INFO: Open the Wiki created via API");
			wUI.clickLinkWait(WikisUI.getWiki(wiki));
		
			log.info("INFO: Wait for Wiki page header to appear");
			wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
	
			log.info("INFO: Select the wiki page from the left navigation panel");
			driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();

			log.info("INFO: Add a comment to wiki page");
			wUI.addComment(Data.getData().Comment_For_Public_Wiki);
		
			log.info("INFO: Change the name of the Wiki page inside the BaseWikiPage");
			wikiPage.setName(Data.getData().editedData + wikiPage.getName());

			log.info("INFO: Change the description of the Wiki Page inside the BaseWikiPage");
			wikiPage.setDescription(Data.getData().editedData + wikiPage.getDescription());
		
			log.info("INFO: Edit the current Wiki page");
			wUI.editWikiPage(wikiPage);	

			wUI.endTest();	
		}
	}

	
	/**
	 * <ul> 
	 * <li><B>Info:</B> Data Population - Standalone Wikis: UserA Edits and Comments on Child Page Created By UserB </li>
	 * <li><B>Step:</B> UserB creates a public wiki </li>
	 * <li><B>Step:</B> UserB adds a child page </li>
	 * <li><B>Step:</B> UserA adds a comment </li>
	 * <li><B>Step:</B> UserA edits the wiki page name </li>
	 * </ul>
	 * NOTE: this isn't supported on the cloud, there is no standalone wiki in the cloud
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userAEditAndCommentStandaloneWikiChildPageCreatedByUserB() {

		if(!isOnPremise){
			throw new SkipException("Environment is Cloud - Standalone Wikis are not supported - skipping tests");
		}
		else
		{
			String testName = wUI.startTest();
			
			members.add(new Member(WikiRole.OWNER, testUser1, apiWkOwner2.getUserUUID(serverURL, testUser1)));
		
			BaseWiki wiki = new BaseWiki.Builder("GDPR " + testName + Helper.genDateBasedRand())
										.editAccess(EditAccess.EditorsAndOwners)
										.readAccess(ReadAccess.WikiOnly)		
										.addMembers(members)
										.tags("tag" + Helper.genDateBasedRand())
										.description("GDPR data population - Description for test " + testName)
										.build();

			BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR UserA Edits And Comments Standalone Child Page Created by UserB " + Helper.genDateBasedRand(), PageType.Child)
													.tags("tag1")
													.description("GDPR data population - Standalone Wikis: UserA Edits and Comments on Child Page Created By UserB")
													.build();
		
			log.info("INFO: Create a new Wiki using API");
			wiki.createAPI(apiWkOwner2);
		
			log.info("INFO: Log into Wikis as: " + testUser2.getDisplayName());
			wUI.loadComponent(Data.getData().ComponentWikis);
			wUI.login(testUser2);
		
			log.info("INFO: Click on the Wiki view: I'm an Owner");
			Wiki_LeftNav_Menu.OWNER.select(wUI);
		
			log.info("INFO: Open the Wiki created via API");
			wUI.clickLinkWait(WikisUI.getWiki(wiki));
		
			log.info("INFO: Wait for Wiki page header to appear");
			wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

			log.info("INFO: Create a child page");
			wikiPage.create(wUI);		

			log.info("INFO: Wiki owner logs out");
			wUI.logout();		
			wUI.close(cfg);	
			
			log.info("INFO: Log into Wikis as: " + testUser1.getDisplayName());
			wUI.loadComponent(Data.getData().ComponentWikis);
			wUI.login(testUser1);
			
			log.info("INFO: Click on the Wiki view: I'm an Owner");
			Wiki_LeftNav_Menu.OWNER.select(wUI);
		
			log.info("INFO: Open the Wiki");
			wUI.clickLinkWait(WikisUI.getWiki(wiki));
		
			log.info("INFO: Wait for Wiki page header to appear");
			wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
				
			log.info("INFO: Select the wiki page from the left navigation panel");
			driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();

			log.info("INFO: Add a comment to wiki page");
			wUI.addComment(Data.getData().Comment_For_Public_Wiki);
		
			log.info("INFO: Change the name of the Wiki page inside the BaseWikiPage");
			wikiPage.setName(Data.getData().editedData + wikiPage.getName());

			log.info("INFO: Change the description of the Wiki Page inside the BaseWikiPage");
			wikiPage.setDescription(Data.getData().editedData + wikiPage.getDescription());
		
			log.info("INFO: Edit the current Wiki page");
			wUI.editWikiPage(wikiPage);	

			wUI.endTest();		
		}
	}	
	

	/**
	 * <ul> 
	 * <li><B>Info:</B> Data Population - Standalone Wikis: UserA Edits and Comments on Peer Page Created by UserB </li>
	 * <li><B>Step:</B> UserB creates a public wiki </li>
	 * <li><B>Step:</B> UserB adds a peer page </li>
	 * <li><B>Step:</B> UserA adds a comment </li>
	 * <li><B>Step:</B> UserA edits the wiki page name </li>
	 * </ul>
	 * NOTE: this isn't supported on the cloud, there is no standalone wiki in the cloud
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userAEditAndCommentStandaloneWikiPeerPageCreatedByUserB() {

		if(!isOnPremise){
			throw new SkipException("Environment is Cloud - Standalone Wikis are not supported - skipping tests");
		}
		else
		{
			String testName = wUI.startTest();
	
			members.add(new Member(WikiRole.OWNER, testUser1, apiWkOwner2.getUserUUID(serverURL, testUser1)));
		
			BaseWiki wiki = new BaseWiki.Builder("GDPR " + testName + Helper.genDateBasedRand())
										.editAccess(EditAccess.EditorsAndOwners)
										.readAccess(ReadAccess.WikiOnly)		
										.addMembers(members)
										.tags("tag" + Helper.genDateBasedRand())
										.description("GDPR data population - Description for test " + testName)
										.build();

			BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR UserA Edits And Comments Standalone Peer Page Created by UserB " + Helper.genDateBasedRand(), PageType.Peer)												
													.tags("tag1")
													.description("GDPR data population - Standalone Wikis: UserA Edits and Comments on Peer Page Created by UserB")
													.build();
				
			log.info("INFO: Create a new Wiki using API");
			Wiki apiWiki = wiki.createAPI(apiWkOwner2);
		
			log.info("INFO: Create a peer page using API");
			wikiPage.createAPI(apiWkOwner2, apiWiki);
		
			log.info("INFO: Log into Wikis as: " + testUser1.getDisplayName());
			wUI.loadComponent(Data.getData().ComponentWikis);
			wUI.login(testUser1);
		
			log.info("INFO: Click on the Wiki view: I'm an Owner");
			Wiki_LeftNav_Menu.OWNER.select(wUI);
		
			log.info("INFO: Open the Wiki created via API");
			wUI.clickLinkWait(WikisUI.getWiki(wiki));
		
			log.info("INFO: Wait for Wiki page header to appear");
			wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
	
			log.info("INFO: Select the wiki page from the left navigation panel");
			driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();

			log.info("INFO: Add a comment to wiki page");
			wUI.addComment(Data.getData().Comment_For_Public_Wiki);
		
			log.info("INFO: Change the name of the Wiki page inside the BaseWikiPage");
			wikiPage.setName(Data.getData().editedData + wikiPage.getName());

			log.info("INFO: Change the description of the Wiki Page inside the BaseWikiPage");
			wikiPage.setDescription(Data.getData().editedData + wikiPage.getDescription());
		
			log.info("INFO: Edit the current Wiki page");
			wUI.editWikiPage(wikiPage);	

			wUI.endTest();		
		}
	}	
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Community Wiki: UserA Follows Wiki Created by UserB </li>
	 * <li><B>Step:</B> UserB creates a community using the API </li>
	 * <li><B>Step:</B> UserB adds wiki app - if on-premises </li>
	 * <li><B>Step:</B> As UserA, follow the community wiki </li>
	 * </ul>	 
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAFollowCommunityWikiCreatedByUserB() {
		
		String testName = wUI.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder("GDPR " + testName + Helper.genDateBasedRand())
                                     .description("GDPR data population - Community Wiki: Follow community wiki test ")
                                     .access(Access.PUBLIC)
                                     .addMember(new Member(CommunityRole.MEMBERS, testUser1))
                                     .build();

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiCommOwner2.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiCommOwner2, BaseWidget.WIKI);
		}
		
		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser1);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Click on the Following Actions menu");
		wUI.clickLinkWait(WikisUIConstants.Follow_Button);
		
		log.info("INFO: Click on the Follow this Wiki link");
		wUI.clickLinkWait(WikisUIConstants.Start_Following_this_wiki);
				
		wUI.endTest();		
	}

	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Standalone Wiki: UserA Follows Wiki Created By UserB </li>
	 * <li><B>Step:</B> UserB creates a wiki using the API </li>
	 * <li><B>Step:</B> As UserA follows the standalone wiki </li>
	 * </ul>
	 * NOTE: NOTE: this isn't supported on the cloud, there is no standalone wiki in the cloud	 
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userAFollowStandaloneWikiCreatedByUserB() {

		if(!isOnPremise){
			throw new SkipException("Environment is Cloud - Standalone Wikis are not supported - skipping tests");
		}
		else
		{
			String testName = wUI.startTest();
		
			BaseWiki wiki = new BaseWiki.Builder("GDPR " + testName + Helper.genDateBasedRand())
										.tags("tag" + Helper.genDateBasedRand())
										.description("GDPR data population - Description of wiki to be followed")
										.build();		

			log.info("INFO: Create a new Wiki using API");
			wiki.createAPI(apiWkOwner2);
		
			log.info("INFO: Log into Wikis as: " + testUser1.getDisplayName());
			wUI.loadComponent(Data.getData().ComponentWikis);
			wUI.login(testUser1);

			log.info("INFO: Click on the Wiki view: Public Wikis");
			Wiki_LeftNav_Menu.PUBLICWIKIS.select(wUI);
		
			log.info("INFO: Open the Wiki created via API");
			wUI.clickLinkWait(WikisUI.getWiki(wiki));
		
			log.info("INFO: Clicking on the Following Actions menu");
			wUI.fluentWaitPresent(WikisUIConstants.Follow_Button);
			wUI.clickLinkWait(WikisUIConstants.Follow_Button);
		
			log.info("INFO: Click on the Follow this Wiki link");
			wUI.clickLinkWait(WikisUIConstants.Start_Following_this_wiki);
				
			wUI.endTest();		
		}
	}

	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Community Wiki: Like a Child Page </li>
	 * <li><B>Step:</B> UserA creates a community using the API </li>
	 * <li><B>Step:</B> UserA adds wiki app - if on-premises </li>
	 * <li><B>Step:</B> UserA adds a child page </li>
	 * <li><B>Step:</B> UserA likes the child page </li>
	 * </ul>	 
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userALikeCommunityWikiChildPage() {

		String testName = wUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR " + testName + Helper.genDateBasedRand())
                                     .description("GDPR data population - Community Wiki: UserA Likes a Child Page")
                                     .access(Access.PUBLIC)
                                     .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR " + testName + Helper.genDateBasedRand(), PageType.Child)
									.tags("tag1, tag2")
									.description("GDPR data population - Community Wiki: UserA Likes a Child Page")
									.build();

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiCommOwner1.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiCommOwner1, BaseWidget.WIKI);
		}
		
		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser1);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(wUI);

		log.info("INFO: Like the Wiki page");
		wUI.likeUnlikePage("Like");
		
		wUI.endTest();		
	}		

	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Community Wiki: UserA Likes the Child Page Created By UserB </li>
	 * <li><B>Step:</B> UserB creates a community using the API </li>
	 * <li><B>Step:</B> UserB adds wiki app - if on-premises </li>
	 * <li><B>Step:</B> UserB adds a child page </li>
	 * <li><B>Step:</B> UserA likes the wiki page </li>
	 * </ul>	 
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userALikeCommunityWikiChildPageCreatedByUserB() {

		String testName = wUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR " + testName + Helper.genDateBasedRand())
                                     .description("GDPR data population - Community Wiki: UserA Likes the Child Page Created By UserB")
                                     .access(Access.PUBLIC)
                                     .addMember(new Member(CommunityRole.MEMBERS, testUser1))
                                     .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR " + testName + Helper.genDateBasedRand(), PageType.Child)
									.tags("tag1, tag2")
									.description("GDPR data population - Community Wiki: UserA Likes the Child Page Created By UserB")
									.build();

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiCommOwner2.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiCommOwner2, BaseWidget.WIKI);
		}
		
		log.info("INFO: Log into Communities as: " + testUser2.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser2);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Wait for Wiki page header to appear");
		wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
		
		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(wUI);

		log.info("INFO: Community owner logs out");
		wUI.logout();		
		wUI.close(cfg);	
			
		log.info("INFO: Log into Community as: " + testUser1.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser1);		

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Wait for Wiki page header to appear");
		wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
				
		log.info("INFO: Select the wiki page from the left navigation panel");
		driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();		
		
		log.info("INFO: Like the Wiki page");
		wUI.likeUnlikePage("Like");
		
		wUI.endTest();		
	}			

	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Community Wiki: Like a Peer Page </li>
	 * <li><B>Step:</B> UserA creates a community using the API </li>
	 * <li><B>Step:</B> UserA adds wiki app - if on-premises </li>
	 * <li><B>Step:</B> UserA adds a peer page </li>
	 * <li><B>Step:</B> UserA likes the wiki page </li>
	 * </ul>	 
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userALikeCommunityWikiPeerPage() {

		String testName = wUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR " + testName + Helper.genDateBasedRand())
                                     .description("GDPR data population - Community Wiki: UserA Likes a Peer Page")
                                     .access(Access.PUBLIC)
                                     .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR " + testName + Helper.genDateBasedRand(), PageType.Peer)
									.tags("tag1, tag2")
									.description("GDPR data population - Community Wiki: UserA Likes a Peer Page")
									.build();

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiCommOwner1.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiCommOwner1, BaseWidget.WIKI);
		}
		
		log.info("INFO: Log into Communities as: " + testUser1.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser1);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(wUI);

		log.info("INFO: Like the Wiki page");
		wUI.likeUnlikePage("Like");
		
		wUI.endTest();		
	}		

	
	/**
	 * <ul>
	 * <li><B>Info:</B> Data Population - Community Wiki: UserA Likes the Peer Page Created by UserB </li>
	 * <li><B>Step:</B> UserB creates a community using the API </li>
	 * <li><B>Step:</B> UserB adds wiki app - if on-premises </li>
	 * <li><B>Step:</B> UserB adds a peer page </li>
	 * <li><B>Step:</B> UserA likes the wiki page </li>
	 * </ul>	 
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userALikeCommunityWikiPeerPageCreatedByUserB() {

		wUI.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR UserALikeCommWkPeerCrByUserB" + Helper.genDateBasedRand())
                                     .description("GDPR data population - Community Wiki: UserA Likes the Peer Page Created by UserB")
                                     .access(Access.PUBLIC)
                                     .addMember(new Member(CommunityRole.MEMBERS, testUser1))
                                     .build();

		BaseWikiPage wikiPage = new BaseWikiPage.Builder("UserALikePeerCreatedByUserB" + Helper.genDateBasedRand(), PageType.Peer)
									.tags("tag1, tag2")
									.description("GDPR data population - Community Wiki: UserA Likes the Peer Page Created by UserB")
									.build();

		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
		
		log.info("INFO: If the Wiki widget does not already exist, add it to the Community using API");
		if(!apiCommOwner2.hasWidget(comAPI, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community using API");
			community.addWidgetAPI(comAPI, apiCommOwner2, BaseWidget.WIKI);
		}
		
		log.info("INFO: Log into Communities as: " + testUser2.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser2);	

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Create a Wiki Page inside the Wiki");
		wikiPage.create(wUI);

		log.info("INFO: Community owner logs out");
		wUI.logout();		
		wUI.close(cfg);	
			
		log.info("INFO: Log into Community as: " + testUser1.getDisplayName());
		wUI.loadComponent(Data.getData().ComponentCommunities);
		wUI.login(testUser1);		

		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Wikis from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(commUI);

		log.info("INFO: Wait for Wiki page header to appear");
		wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
				
		log.info("INFO: Select the wiki page from the left navigation panel");
		driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();		
		
		log.info("INFO: Like the Wiki page");
		wUI.likeUnlikePage("Like");
		
		wUI.endTest();		
	}		

	
	/**
	 * <ul> 
	 * <li><B>Info:</B> Data Population - Standalone Wikis: Like Child Page </li>
	 * <li><B>Step:</B> UserA creates a public wiki </li>
	 * <li><B>Step:</B> UserA adds a child page </li>
	 * <li><B>Step:</B> UserA likes the wiki page </li>
	 * </ul>
	 * NOTE: this isn't supported on the cloud, there is no standalone wiki in the cloud
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userALikeStandaloneWikiChildPage() {
		if(!isOnPremise){
			throw new SkipException("Environment is Cloud - Standalone Wikis are not supported - skipping tests");
		}
		else
		{			
			String testName = wUI.startTest();

			BaseWiki wiki = new BaseWiki.Builder("GDPR " + testName + Helper.genDateBasedRand())
								    	.tags("tag" + Helper.genDateBasedRand())
								    	.description("GDPR data population - Description for test " + testName)
								    	.build();

			BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR UserA Likes Child Page " + Helper.genDateBasedRand(), PageType.Child)
													.tags("tag1")
													.description("GDPR data population - Standalone Wikis: UserA Likes Child Page")
													.build();
		
			log.info("INFO: Create a new Wiki using API");
			wiki.createAPI(apiWkOwner1);
		
			log.info("INFO: Log into Wikis as: " + testUser1.getDisplayName());
			wUI.loadComponent(Data.getData().ComponentWikis);
			wUI.login(testUser1);
		
			log.info("INFO: Click on the Wiki view: I'm an Owner");
			Wiki_LeftNav_Menu.OWNER.select(wUI);
		
			log.info("INFO: Open the Wiki created via API");
			wUI.clickLinkWait(WikisUI.getWiki(wiki));
		
			log.info("INFO: Wait for Wiki page header to appear");
			wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

			log.info("INFO: Create a child page");
			wikiPage.create(wUI);		

			log.info("INFO: Select the wiki page from the left navigation panel");
			driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();

			log.info("INFO: Like the Wiki page");
			wUI.likeUnlikePage("Like");	

			wUI.endTest();	
		}
	}		

	
	/**
	 * <ul> 
	 * <li><B>Info:</B> Data Population - Standalone Wikis: UserA Likes Child Page Created By UserB </li>
	 * <li><B>Step:</B> UserB creates a public wiki </li>
	 * <li><B>Step:</B> UserB adds a child page </li>
	 * <li><B>Step:</B> UserA likes the wiki page </li>
	 * </ul>
	 * NOTE: this isn't supported on the cloud, there is no standalone wiki in the cloud
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userALikeStandaloneWikiChildPageCreatedByUserB() {

		if(!isOnPremise){
			throw new SkipException("Environment is Cloud - Standalone Wikis are not supported - skipping tests");
		}
		else
		{
			String testName = wUI.startTest();
			
			members.add(new Member(WikiRole.OWNER, testUser1, apiWkOwner2.getUserUUID(serverURL, testUser1)));
		
			BaseWiki wiki = new BaseWiki.Builder("GDPR " + testName + Helper.genDateBasedRand())
										.editAccess(EditAccess.EditorsAndOwners)
										.readAccess(ReadAccess.WikiOnly)		
										.addMembers(members)
										.tags("tag" + Helper.genDateBasedRand())
										.description("GDPR data population - Description for test " + testName)
										.build();

			BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR UserA likes Child Page Created by UserB " + Helper.genDateBasedRand(), PageType.Child)
													.tags("tag1")
													.description("GDPR data population - Standalone Wikis: UserA Likes Child Page Created By UserB")
													.build();
		
			log.info("INFO: Create a new Wiki using API");
			wiki.createAPI(apiWkOwner2);
		
			log.info("INFO: Log into Wikis as: " + testUser2.getDisplayName());
			wUI.loadComponent(Data.getData().ComponentWikis);
			wUI.login(testUser2);
		
			log.info("INFO: Click on the Wiki view: I'm an Owner");
			Wiki_LeftNav_Menu.OWNER.select(wUI);
		
			log.info("INFO: Open the Wiki created via API");
			wUI.clickLinkWait(WikisUI.getWiki(wiki));
		
			log.info("INFO: Wait for Wiki page header to appear");
			wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);

			log.info("INFO: Create a child page");
			wikiPage.create(wUI);		

			log.info("INFO: Wiki owner logs out");
			wUI.logout();		
			wUI.close(cfg);	
			
			log.info("INFO: Log into Wikis as: " + testUser1.getDisplayName());
			wUI.loadComponent(Data.getData().ComponentWikis);
			wUI.login(testUser1);
		
			log.info("INFO: Click on the Wiki view: I'm an Owner");
			Wiki_LeftNav_Menu.OWNER.select(wUI);
		
			log.info("INFO: Open the Wiki");
			wUI.clickLinkWait(WikisUI.getWiki(wiki));
		
			log.info("INFO: Wait for Wiki page header to appear");
			wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
				
			log.info("INFO: Select the wiki page from the left navigation panel");
			driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();

			log.info("INFO: Like the Wiki page");
			wUI.likeUnlikePage("Like");	

			wUI.endTest();	
		}
	}	

	
	/**
	 * <ul> 
	 * <li><B>Info:</B> Data Population - Standalone Wikis: Like Peer Page </li>
	 * <li><B>Step:</B> UserA creates a public wiki </li>
	 * <li><B>Step:</B> UserA adds a peer page </li>
	 * <li><B>Step:</B> UserA likes the wiki page </li>
	 * </ul>
	 * NOTE: this isn't supported on the cloud, there is no standalone wiki in the cloud
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userALikeStandaloneWikiPeerPage() {

		if(!isOnPremise){
			throw new SkipException("Environment is Cloud - Standalone Wikis are not supported - skipping tests");
		}
		else
		{
			String testName = wUI.startTest();

			BaseWiki wiki = new BaseWiki.Builder("GDPR " + testName + Helper.genDateBasedRand())
								    	.tags("tag" + Helper.genDateBasedRand())
								    	.description("GDPR data population - Description for test " + testName)
								    	.build();

			BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR UserA Likes Peer Page " + Helper.genDateBasedRand(), PageType.Peer)
													.tags("tag1")
													.description("GDPR data population - Standalone Wikis: UserA Likes Peer Page")
													.build();
				
			log.info("INFO: Create a new Wiki using API");
			Wiki apiWiki = wiki.createAPI(apiWkOwner1);
		
			log.info("INFO: Create a peer page using API");
			wikiPage.createAPI(apiWkOwner1, apiWiki);
		
			log.info("INFO: Log into Wikis as: " + testUser1.getDisplayName());
			wUI.loadComponent(Data.getData().ComponentWikis);
			wUI.login(testUser1);
		
			log.info("INFO: Click on the Wiki view: I'm an Owner");
			Wiki_LeftNav_Menu.OWNER.select(wUI);
		
			log.info("INFO: Open the Wiki created via API");
			wUI.clickLinkWait(WikisUI.getWiki(wiki));
		
			log.info("INFO: Wait for Wiki page header to appear");
			wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
	
			log.info("INFO: Select the wiki page from the left navigation panel");
			driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();

			log.info("INFO: Like the Wiki page");
			wUI.likeUnlikePage("Like");		

			wUI.endTest();	
		}
	}	

	
	/**
	 * <ul> 
	 * <li><B>Info:</B> Data Population - Standalone Wikis: UserA Likes Peer Page Created by UserB </li>
	 * <li><B>Step:</B> UserB creates a public wiki </li>
	 * <li><B>Step:</B> UserB adds a peer page </li>
	 * <li><B>Step:</B> UserA likes the wiki page </li>
	 * </ul>
	 * NOTE: this isn't supported on the cloud, there is no standalone wiki in the cloud
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userALikeStandaloneWikiPeerPageCreatedByUserB() {

		if(!isOnPremise){
			throw new SkipException("Environment is Cloud - Standalone Wikis are not supported - skipping tests");
		}
		else
		{
			String testName = wUI.startTest();
	
			members.add(new Member(WikiRole.OWNER, testUser1, apiWkOwner2.getUserUUID(serverURL, testUser1)));
		
			BaseWiki wiki = new BaseWiki.Builder("GDPR " + testName + Helper.genDateBasedRand())
										.editAccess(EditAccess.EditorsAndOwners)
										.readAccess(ReadAccess.WikiOnly)		
										.addMembers(members)
										.tags("tag" + Helper.genDateBasedRand())
										.description("GDPR data population - Description for test " + testName)
										.build();

			BaseWikiPage wikiPage = new BaseWikiPage.Builder("GDPR UserA Likes Peer Page Created by UserB " + Helper.genDateBasedRand(), PageType.Peer)												
													.tags("tag1")
													.description("GDPR data population - Standalone Wikis: UserA Likes Peer Page Created by UserB")
													.build();
				
			log.info("INFO: Create a new Wiki using API");
			Wiki apiWiki = wiki.createAPI(apiWkOwner2);
		
			log.info("INFO: Create a peer page using API");
			wikiPage.createAPI(apiWkOwner2, apiWiki);
		
			log.info("INFO: Log into Wikis as: " + testUser1.getDisplayName());
			wUI.loadComponent(Data.getData().ComponentWikis);
			wUI.login(testUser1);
		
			log.info("INFO: Click on the Wiki view: I'm an Owner");
			Wiki_LeftNav_Menu.OWNER.select(wUI);
		
			log.info("INFO: Open the Wiki created via API");
			wUI.clickLinkWait(WikisUI.getWiki(wiki));
		
			log.info("INFO: Wait for Wiki page header to appear");
			wUI.fluentWaitPresent(WikisUIConstants.wikiPageHeader);
	
			log.info("INFO: Select the wiki page from the left navigation panel");
			driver.getFirstElement(wUI.getPageSelector(wikiPage)).click();

			log.info("INFO: Like the Wiki page");
			wUI.likeUnlikePage("Like");	

			wUI.endTest();	
		}
	}		
	
}
