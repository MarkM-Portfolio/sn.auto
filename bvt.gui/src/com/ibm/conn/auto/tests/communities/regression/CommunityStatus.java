package com.ibm.conn.auto.tests.communities.regression;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommunityStatus extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(CommunityStatus.class);
	private CommunitiesUI ui;
	private TestConfigCustom cfg;	
	private User testUser,testUser1, member;
	private APICommunitiesHandler apiOwner;
	private FilesUI filesUI;
	private HomepageUI hUI;
    private String gk_flag;
    private boolean value;
    private Community comAPI1;
	private BaseCommunity community1;
	private BaseFile file1;
	private String serverURL;
	
	/**
	 * PTC_VerifyAbilityToShareStatusMessage
	 * PTC_VerifyStatusUpdateWidget
	 */
	
	
	@BeforeMethod(alwaysRun=true )
	public void setUp() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(),driver);
		hUI = HomepageUI.getGui(cfg.getProductName(),driver);

	}
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		member = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());	
	
	
				community1 = new BaseCommunity.Builder("Global share public community" + Helper.genDateBasedRandVal())
															.access(Access.PUBLIC)	
															.description("Test Sharing file with a community")
															.addMember(new Member(CommunityRole.MEMBERS, member))
															.build();
								
				file1 = new BaseFile.Builder(Data.getData().file5)
									.tags(Data.getData().commonTag + Helper.genDateBasedRandVal() )
									.rename(Helper.genStrongRand())
									.extension(".jpg")
									.build();
				
				log.info("INFO: Create communities via the API");
				comAPI1 = community1.createAPI(apiOwner);
	
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUpNetwork() {
		
		log.info("INFO: Cleanup - delete communities");
		apiOwner.deleteCommunity(comAPI1);
	
		
	}

	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part2: Verify Ability to Share a Status Message with a community (1 of Test 1)</li>
	 *<li><B>Info:</B> Verify a status message posted via the global sharebox shows up in Recent Updates widget</li>
	 *<li><B>Step:</B> Create a Public community as owner using API & from the Mega Menu Share - Share a status message with Community</li>
	 *<li><B>Verify:</B> Status message is successfully posted</li>
	 *<li><B>Step:</B> Go to Status updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message is coming in status updates</li>
	 *<li><B>Step:</B> Go to Recent updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message & the EE pop-up both display the correct meta-data </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4878EF66F46DAB3785257C90005C1BE9">TTT-GLOBAL SHAREBOX PART2: VERIFY ABILITY TO SHARE A STATUS MESSAGE WITH A COMMUNITY (On-Prem only!)</a></li>
	 *</ul>
	 */
	//Test case is failing. Need to work on its fix
	@Test(groups = {"regression"},enabled=false)
	public void shareStatusUpdateMsgInPublicCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		//status update description
		String shareboxStatus = "Status Update" + rndNum;
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.addMember(new Member(CommunityRole.MEMBERS, member))
													.description("Test share a status message with public community " + testName)
													.build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);
		
		//Share Box Status Message
		log.info("INFO: Share status message");
		shareStatusMsgAndFileWithCommunity(community, null, shareboxStatus);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		// Go to Status updates
		log.info("INFO:Go to Status updates");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
		
		// Test that Status message posted is coming in status updates
		log.info("INFO: Test that Status message posted is coming in status updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
													"Error: Posted message is not coming in Status updates");
		
		// Go to Recent updates
		log.info("INFO:Go to Recent updates");
		Community_LeftNav_Menu.RECENT_UPDATES.select(ui);
		
		//Test that Status message posted is coming in Recent updates
		log.info("INFO: Test that Status message posted is coming in Recent updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
															"Error: Posted message is not coming in Recent updates");

		//Select Recent update post and open EE
		log.info("INFO: Select Recent update post and open EE ");
		hUI.filterNewsItemOpenEE(shareboxStatus);

		//Wait to load frame
		log.info("INFO: Wait to load frame");
		hUI.waitForAndSwitchToEEFrame(HomepageUIConstants.GenericEEFrame, HomepageUIConstants.EEStatus, 3);
				
		//Test newsItem meta data on EE dialog
		log.info("INFO: Verify the newsItem status is displayed in EE dailog");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), shareboxStatus,
								"ERROR: Status is not displayed in EE dailog");

		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
		
		//logout as Member
		log.info("INFO: logout as Member");
		ui.logout();

		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part2: Verify Ability to Share a Status Message with a community (2 of Test 1)</li>
	 *<li><B>Info:</B> Verify a status message with a file attachment created via global sharebox can be shared with a Public community</li>
	 *<li><B>Step:</B> Create a Public community as owner using API & from the Mega Menu Share - Share a status message + attach a file with Community</li>
	 *<li><B>Verify:</B> Status message is successfully posted with the file as attachment</li>
	 *<li><B>Step:</B> Go to Status updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message & file appear is coming in status updates</li>
	 *<li><B>Verify:</B> Click on the entry & verify that the message & file appear in the EE pop-up dialog box</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4878EF66F46DAB3785257C90005C1BE9">TTT-GLOBAL SHAREBOX PART2: VERIFY ABILITY TO SHARE A STATUS MESSAGE WITH A COMMUNITY (On-Prem only!)</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void shareStatusUpdateFileInPublicCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		//status update description
		String shareboxStatus = "Status Update" + rndNum;
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.addMember(new Member(CommunityRole.MEMBERS, member))
													.description("Test share a status message with public community " + testName)
													.build();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file6)
		                    .rename(Helper.genStrongRand())
		                    .extension(".jpg")
							.build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);
		
		//Share status message and File
		log.info("INFO: Share message and file");
		shareStatusMsgAndFileWithCommunity(community, file, shareboxStatus);
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
				
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
				
		//Navigate to the I'm a Member view
		log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
		ui.goToMemberView(isCardView);
		
		//Open the community 
		log.info("INFO: Open the community");
		ui.clickLinkWait(communityLink);
		
		
		// Go to Status updates
		log.info("INFO:Go to Status updates");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
		
		//Test that Status message posted is coming in status updates
		log.info("INFO: Test that Status message posted is coming in status updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
													"Error: Posted message is not coming in Status updates");
		
		//Test file meta data
		log.info("INFO: Verify the file name is displayed");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateFileContent).getAttribute("title"), file.getName(),
								"ERROR: File uploaded is not displayed in Recent Updates view");

		//Select File post and open EE
		log.info("INFO: Select File post and open EE ");
		hUI.filterNewsItemOpenEE(shareboxStatus);
	
		//Wait to load frame
		log.info("INFO: Wait to load frame");
		hUI.waitForAndSwitchToEEFrame(HomepageUIConstants.GenericEEFrame, HomepageUIConstants.EEStatus, 3);
		
		//Test file meta data on EE dialog
		log.info("INFO: Verify the file name is displayed in EE dailog");
		Assert.assertTrue(ui.fluentWaitPresent("link="+ file.getName()),
								"ERROR: File uploaded is not displayed in EE dailog");
				
		//Test newsItem meta data on EE dialog
		log.info("INFO: Verify the newsItem status is displayed in EE dailog");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), shareboxStatus,
								"ERROR: Status is not displayed in EE dailog");

		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
		
		//logout as Member
		log.info("INFO: logout as Member");
		ui.logout();

		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
				
		ui.endTest();
					
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part2: Verify Ability to Share a Status Message with a community (1 of Test 2)</li>
	 *<li><B>Info:</B> Verify a status message posted via the global sharebox shows up in Recent Updates widget</li>
	 *<li><B>Step:</B> Create a Moderated community as owner using API & from the Mega Menu Share - Share a status message with Community</li>
	 *<li><B>Verify:</B> Status message is successfully posted</li>
	 *<li><B>Step:</B> Go to Status updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message is coming in status updates</li>
	 *<li><B>Step:</B> Go to Recent updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message & the EE pop-up both display the correct meta-data </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4878EF66F46DAB3785257C90005C1BE9">TTT-GLOBAL SHAREBOX PART2: VERIFY ABILITY TO SHARE A STATUS MESSAGE WITH A COMMUNITY (On-Prem only!)</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void shareStatusUpdateMsgInModerateCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		//status update description
		String shareboxStatus = "Status Update" + rndNum;
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.MODERATED)	
													.tags(Data.getData().commonTag + rndNum )
													.addMember(new Member(CommunityRole.MEMBERS, member))
													.description("Test share a status message with Moderate community " + testName)
													.build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);
		
		//Share Box Status Message
		log.info("INFO: Share status message");
		shareStatusMsgAndFileWithCommunity(community, null, shareboxStatus);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		// Go to Status updates
		log.info("INFO:Go to Status updates");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
		
		//Test that Status message posted is coming in status updates
		log.info("INFO: Test that Status message posted is coming in status updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
													"Error: Posted message is not coming in Status updates");
		
		// Go to Recent updates
		log.info("INFO:Go to Recent updates");
		Community_LeftNav_Menu.RECENT_UPDATES.select(ui);
		
		ui.fluentWaitElementVisible(CommunitiesUIConstants.recentUpdatesHeader);
		
		//Test that Status message posted is coming in Recent updates
		log.info("INFO: Test that Status message posted is coming in Recent updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
															"Error: Posted message is not coming in Recent updates");

		//Select Recent update post and open EE
		log.info("INFO: Select Recent update post and open EE ");
		hUI.filterNewsItemOpenEE(shareboxStatus);

		//Wait to load frame
		log.info("INFO: Wait to load frame");
		hUI.waitForAndSwitchToEEFrame(HomepageUIConstants.GenericEEFrame, HomepageUIConstants.EEStatus, 3);
				
		//Test newsItem meta data on EE dialog
		log.info("INFO: Verify the newsItem status is displayed in EE dailog");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), shareboxStatus,
								"ERROR: Status is not displayed in EE dailog");

		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
		
		//logout as Member
		log.info("INFO: logout as Member");
		ui.logout();

		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part2: Verify Ability to Share a Status Message with a community (2 of Test 2)</li>
	 *<li><B>Info:</B> Verify a status message with a file attachment created via global sharebox can be shared with a Moderate community</li>
	 *<li><B>Step:</B> Create a Moderate community as owner using API & from the Mega Menu Share - Share a status message + attach a file with Community</li>
	 *<li><B>Verify:</B> Status message is successfully posted with the file as attachment</li>
	 *<li><B>Step:</B> Go to Status updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message & file appear is coming in status updates</li>
	 *<li><B>Verify:</B> Click on the entry & verify that the message & file appear in the EE pop-up dialog box</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4878EF66F46DAB3785257C90005C1BE9">TTT-GLOBAL SHAREBOX PART2: VERIFY ABILITY TO SHARE A STATUS MESSAGE WITH A COMMUNITY (On-Prem only!)</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void shareStatusUpdateFileInModerateCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		//status update description
		String shareboxStatus = "Status Update" + rndNum;
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.MODERATED)	
													.tags(Data.getData().commonTag + rndNum )
													.addMember(new Member(CommunityRole.MEMBERS, member))
													.description("Test share a status message with Moderate community " + testName)
													.build();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file7)
							.rename(Helper.genStrongRand())
							.extension(".jpg")
							.build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);
		
		//Share status message and File
		log.info("INFO: Share message and file");
		shareStatusMsgAndFileWithCommunity(community, file, shareboxStatus);
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
			
		//Navigate to the I'm a Member view
		log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
		ui.goToMemberView(isCardView);

		//Open the community
		log.info("INFO: Open the community");
		ui.clickLinkWait(communityLink);
		
		// Go to Status updates
		log.info("INFO:Go to Status updates");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
		
		//Test that Status message posted is coming in status updates
		log.info("INFO: Test that Status message posted is coming in status updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
													"Error: Posted message is not coming in Status updates");
		
		//Test file meta data
		log.info("INFO: Verify the file name is displayed");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateFileContent).getAttribute("title"), file.getName(),
								"ERROR: File uploaded is not displayed in Recent Updates view");

		//Select File post and open EE
		log.info("INFO: Select File post and open EE ");
		hUI.filterNewsItemOpenEE(shareboxStatus);
	
		//Wait to load frame
		log.info("INFO: Wait to load frame");
		hUI.waitForAndSwitchToEEFrame(HomepageUIConstants.GenericEEFrame, HomepageUIConstants.EEStatus, 3);
				
		//Test file meta data on EE dialog
		log.info("INFO: Verify the file name is displayed in EE dailog");
		Assert.assertTrue(ui.fluentWaitPresent("link="+ file.getName()),
								"ERROR: File uploaded is not displayed in EE dailog");
				
		//Test newsItem meta data on EE dialog
		log.info("INFO: Verify the newsItem status is displayed in EE dailog");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), shareboxStatus,
								"ERROR: Status is not displayed in EE dailog");

		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
		
		//logout as Member
		log.info("INFO: logout as Member");
		ui.logout();

		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
				
		ui.endTest();
					
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part2: Verify Ability to Share a Status Message with a community (1 of Test 3)</li>
	 *<li><B>Info:</B> Verify a status message posted via the global sharebox shows up in Recent Updates widget</li>
	 *<li><B>Step:</B> Create a Restricted community as owner using API & from the Mega Menu Share - Share a status message with Community</li>
	 *<li><B>Verify:</B> Status message is successfully posted</li>
	 *<li><B>Step:</B> Go to Status updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message is coming in status updates</li>
	 *<li><B>Step:</B> Go to Recent updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message & the EE pop-up both display the correct meta-data </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4878EF66F46DAB3785257C90005C1BE9">TTT-GLOBAL SHAREBOX PART2: VERIFY ABILITY TO SHARE A STATUS MESSAGE WITH A COMMUNITY (On-Prem only!)</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression"} , enabled=true )
	public void shareStatusUpdateMsgInRestrictedCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		//status update description
		String shareboxStatus = "Status Update" + rndNum;
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.RESTRICTED)	
													.tags(Data.getData().commonTag + rndNum )
													.addMember(new Member(CommunityRole.MEMBERS, member))
													.description("Test share a status message with Restricted community " + testName)
													.shareOutside(false)
													.build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);
		
		//Share Box Status Message
		log.info("INFO: Share status message");
		shareStatusMsgAndFileWithCommunity(community, null, shareboxStatus);
		
		// check if catalog_card_view GK enabled
        boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
			
		//Navigate to the I'm a Member view
		log.info("INFO:  Click on the I'm a Member catalog view");
		ui.goToMemberView(isCardView);
		
		log.info("INFO: Open community");
		ui.clickLinkWait(communityLink);
		
		// Go to Status updates
		log.info("INFO:Go to Status updates");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
		
		//Test that Status message posted is coming in status updates
		log.info("INFO: Test that Status message posted is coming in status updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
													"Error: Posted message is not coming in Status updates");
		
		// Go to Recent updates
		log.info("INFO:Go to Recent updates");
		Community_LeftNav_Menu.RECENT_UPDATES.select(ui);
		
		ui.fluentWaitElementVisible(CommunitiesUIConstants.recentUpdatesHeader);

		//Test that Status message posted is coming in Recent updates
		log.info("INFO: Test that Status message posted is coming in Recent updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
															"Error: Posted message is not coming in Recent updates");

		//Select Recent update post and open EE
		log.info("INFO: Select Recent update post and open EE ");
		hUI.filterNewsItemOpenEE(shareboxStatus);

		//Wait to load frame
		log.info("INFO: Wait to load frame");
		hUI.waitForAndSwitchToEEFrame(HomepageUIConstants.GenericEEFrame, HomepageUIConstants.EEStatus, 3);
				
		//Test newsItem meta data on EE dialog
		log.info("INFO: Verify the newsItem status is displayed in EE dailog");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), shareboxStatus,
								"ERROR: Status is not displayed in EE dailog");

		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
		
		//logout as Member
		log.info("INFO: logout as Member");
		ui.logout();

		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part2: Verify Ability to Share a Status Message with a community (2 of Test 3)</li>
	 *<li><B>Info:</B> Verify a status message with a file attachment created via global sharebox can be shared with a Restricted community</li>
	 *<li><B>Step:</B> Create a Restricted community as owner using API & from the Mega Menu Share - Share a status message + attach a file with Community</li>
	 *<li><B>Verify:</B> Status message is successfully posted with the file as attachment</li>
	 *<li><B>Step:</B> Go to Status updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message & file appear is coming in status updates</li>
	 *<li><B>Verify:</B> Click on the entry & verify that the message & file appear in the EE pop-up dialog box</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4878EF66F46DAB3785257C90005C1BE9">TTT-GLOBAL SHAREBOX PART2: VERIFY ABILITY TO SHARE A STATUS MESSAGE WITH A COMMUNITY (On-Prem only!)</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void shareStatusUpdateFileInRestrictedCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		//status update description
		String shareboxStatus = "Status Update" + rndNum;
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.RESTRICTED)	
													.tags(Data.getData().commonTag + rndNum )
													.addMember(new Member(CommunityRole.MEMBERS, member))
													.description("Test share a status message with Restricted community " + testName)
													.shareOutside(false)
													.build();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file8)
							.rename(Helper.genStrongRand())
							.extension(".jpg")
							.build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);
		
		//Share status message and File
		log.info("INFO: Share message and file");
		shareStatusMsgAndFileWithCommunity(community, file, shareboxStatus);
		
		// check if catalog_card_view GK enabled
	    boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
	
		//Navigate to the I'm a Member view
		log.info("INFO: Clicking on the I'm Member link from the LeftNavigation");
		ui.goToMemberView(isCardView);

		//Open the community
		log.info("INFO: Open the community");
		ui.clickLinkWait(communityLink);
		
		log.info("Execute the test if GateKeeper setting for Tabbed Navigation is enabled");
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Click on Status Updates from the tabbed nav");
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			Community_TabbedNav_Menu.STATUSUPDATES.select(ui);

		}else { 

			// Go to Status updates
			log.info("INFO:Go to Status updates");
			Community_LeftNav_Menu.STATUSUPDATES.select(ui);
		}
				
		//Test that Status message posted is coming in status updates
		log.info("INFO: Test that Status message posted is coming in status updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
													"Error: Posted message is not coming in Status updates");
		
		//Test file meta data
		log.info("INFO: Verify the file name is displayed");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateFileContent).getAttribute("title"), file.getName(),
								"ERROR: File uploaded is not displayed in Recent Updates view");

		//Select File post and open EE
		log.info("INFO: Select File post and open EE ");
		hUI.filterNewsItemOpenEE(shareboxStatus);
	
		//Wait to load frame
		log.info("INFO: Wait to load frame");
		hUI.waitForAndSwitchToEEFrame(HomepageUIConstants.GenericEEFrame, HomepageUIConstants.EEStatus, 3);
				
		//Test file meta data on EE dialog
		log.info("INFO: Verify the file name is displayed in EE dailog");
		Assert.assertTrue(ui.fluentWaitPresent("link="+ file.getName()),
								"ERROR: File uploaded is not displayed in EE dailog");
				
		//Test newsItem meta data on EE dialog
		log.info("INFO: Verify the newsItem status is displayed in EE dailog");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), shareboxStatus,
								"ERROR: Status is not displayed in EE dailog");

		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
		
		//logout as Member
		log.info("INFO: logout as Member");
		ui.logout();

		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
				
		ui.endTest();
					
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part2: Verify Ability to Share a Status Message with a community (1 of Test 4)</li>
	 *<li><B>Info:</B> Verify a status message posted via the global sharebox shows up in Recent Updates widget</li>
	 *<li><B>Step:</B> Create a Public community as owner using API, Create SubCommunity & from the Mega Menu Share - Share a status message with SubCommunity</li>
	 *<li><B>Verify:</B> Status message is successfully posted</li>
	 *<li><B>Step:</B> Go to Status updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message is coming in status updates</li>
	 *<li><B>Step:</B> Go to Recent updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message & the EE pop-up both display the correct meta-data </li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4878EF66F46DAB3785257C90005C1BE9">TTT-GLOBAL SHAREBOX PART2: VERIFY ABILITY TO SHARE A STATUS MESSAGE WITH A COMMUNITY (On-Prem only!)</a></li>
	 *</ul>
	 */
	//Test case is failing. Need to work on its fix
	@Test(groups = {"regression"} , enabled=false )
	public void shareStatusUpdateMsgInSubCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		//status update description
		String shareboxStatus = "Status Update" + rndNum;
				
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.addMember(new Member(CommunityRole.MEMBERS, member))
													.description("Test share a status message with Public community " + testName)
													.shareOutside(false)
													.build();
		//Create a sub-community base state object 		
		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("Global share Moderated SubCommunity" + rndNum)
															.access(com.ibm.conn.auto.appobjects.base.BaseSubCommunity.Access.MODERATED)
														    .description("Test Sharing file with a SubCommunity")
														    .UseParentmembers(true)
														    .useActionMenu(true)
														    .build();
				
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Create SubCommunity
		log.info("INFO: Create SubCommunity");
		subCommunity.create(ui);
		
		//logout as Member
		log.info("INFO: logout as Owner");
		ui.logout();
		ui.close(cfg);
		
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);
		
		//click on the Share link
		log.info("INFO: click on the Share link");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);
		
		//Switch to the Sharebox frame
		log.info("INFO: switch to sharebox frame");
		ui.fluentWaitPresent(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		
		//Select post updates to drop down
		log.info("INFO: Select post updates to drop down");
		driver.getSingleElement(CommunitiesUIConstants.ShareBoxPostType).useAsDropdown().selectOptionByVisibleText(Data.getData().communityOption);
		
		//Select the community created earlier
		log.info("INFO: Select the community created earlier");
		ui.typeText(CommunitiesUIConstants.ShareBoxCommunityPickerTextBox, subCommunity.getName());
		hUI.typeaheadSelection(subCommunity.getName(), CommunitiesUIConstants.ShareBoxCommunityPickerPopup);
		
		//Enter the description
		log.info("INFO: Enter the description");
		ui.typeMessageInShareBox(shareboxStatus, false);
		
		//Post the status
		log.info("INFO: Post the status");
		ui.clickButton("Post");
		
		//Switch back to the main frame
		log.info("INFO: Switch back to the main frame  ");
		ui.switchToTopFrame();
		ui.waitForPageLoaded(driver);
		
		//Test the status message
		log.info("INFO: Test that status message is successfully posted ");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().statusSuccessMsg),
										"Error : Success message is not displayed");
		
		// check if catalog_card_view GK enabled
		boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
			
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getSubCommunityLinkCardView(subCommunity) : "link=" + subCommunity.getName();
				
		//Open I'm a Member
		log.info("INFO:  Click on the I'm a Member catalog view");
		ui.goToMemberView(isCardView);
		
		//Open the community
		log.info("INFO: Click on the community");
		ui.fluentWaitPresent(communityLink);
		ui.clickLinkWait(communityLink);
		
		// Go to Status updates
		log.info("INFO:Go to Status updates");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
		
		//Test that Status message posted is coming in status updates
		log.info("INFO: Test that Status message posted is coming in status updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
													"Error: Posted message is not coming in Status updates");
		
		// Go to Recent updates
		log.info("INFO:Go to Recent updates");
		Community_LeftNav_Menu.RECENT_UPDATES.select(ui);
		
		//Test that Status message posted is coming in Recent updates
		log.info("INFO: Test that Status message posted is coming in Recent updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
															"Error: Posted message is not coming in Recent updates");

		//Select Recent update post and open EE
		log.info("INFO: Select Recent update post and open EE ");
		hUI.filterNewsItemOpenEE(shareboxStatus);

		//Wait to load frame
		log.info("INFO: Wait to load frame");
		hUI.waitForAndSwitchToEEFrame(HomepageUIConstants.GenericEEFrame, HomepageUIConstants.EEStatus, 3);
				
		//Test newsItem meta data on EE dialog
		log.info("INFO: Verify the newsItem status is displayed in EE dailog");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), shareboxStatus,
								"ERROR: Status is not displayed in EE dailog");

		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
		
		//logout as Member
		log.info("INFO: logout as Member");
		ui.logout();

		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
				
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part2: Verify Ability to Share a Status Message with a community (2 of Test 4)</li>
	 *<li><B>Info:</B> Verify a status message with a file attachment created via global sharebox can be shared with a Subcommunity</li>
	 *<li><B>Step:</B> Create a Public community as owner using API ,Create a SubCommunity & from the Mega Menu Share - Share a status message + attach a file with SubCommunity</li>
	 *<li><B>Verify:</B> Status message is successfully posted with the file as attachment</li>
	 *<li><B>Step:</B> Go to Status updates to check the message posted</li>
	 *<li><B>Verify:</B> Status message & file appear is coming in status updates</li>
	 *<li><B>Verify:</B> Click on the entry & verify that the message & file appear in the EE pop-up dialog box</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4878EF66F46DAB3785257C90005C1BE9">TTT-GLOBAL SHAREBOX PART2: VERIFY ABILITY TO SHARE A STATUS MESSAGE WITH A COMMUNITY (On-Prem only!)</a></li>
	 *</ul>
	 */
	//Test case is failing. Need to work on its fix
	@Test(groups = {"regression"} , enabled=false )
	public void shareStatusUpdateFileInSubCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		//status update description
		String shareboxStatus = "Status Update" + rndNum;
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum)
													.access(Access.PUBLIC)	
													.tags(Data.getData().commonTag + rndNum )
													.addMember(new Member(CommunityRole.MEMBERS, member))
													.description("Test share a status message with Restricted community " + testName)
													.shareOutside(false)
													.build();
		
		//Create a sub-community base state object 		
		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("Global share Moderated SubCommunity" + rndNum)
															.access(com.ibm.conn.auto.appobjects.base.BaseSubCommunity.Access.MODERATED)
														    .description("Test Sharing file with a SubCommunity")
														    .UseParentmembers(true)
														    .useActionMenu(true)
														    .build();
		//File
		BaseFile file = new BaseFile.Builder(Data.getData().file2)
							.tags(Data.getData().commonTag + rndNum )
							.rename(Helper.genStrongRand())
							.extension(".jpg")
							.build();
		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//GUI
		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		// check if catalog_card_view GK enabled
        boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
		
		//Navigate to default catalog view
		ui.goToDefaultCatalogView();

		//Open the community
		log.info("INFO: Open the community");
		ui.clickLinkWait(communityLink);
		
		//Create SubCommunity
		log.info("INFO: Create SubCommunity");
		subCommunity.create(ui);
		
		//logout as Member
		log.info("INFO: logout as Owner");
		ui.logout();
		ui.close(cfg);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);
		
		//click on the Share link
		log.info("INFO: click on the Share link");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);
		
		//Switch to the Sharebox frame
		log.info("INFO: switch to sharebox frame");
		ui.fluentWaitPresent(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		
		//Select post updates to drop down
		log.info("INFO: Select post updates to drop down");
		driver.getSingleElement(CommunitiesUIConstants.ShareBoxPostType).useAsDropdown().selectOptionByVisibleText(Data.getData().communityOption);
		
		//Select the community created earlier
		log.info("INFO: Select the community created earlier");
		ui.typeText(CommunitiesUIConstants.ShareBoxCommunityPickerTextBox, subCommunity.getName());
		hUI.typeaheadSelection(subCommunity.getName(), CommunitiesUIConstants.ShareBoxCommunityPickerPopup);
		
		//Enter the description
		log.info("INFO: Enter the description");
		ui.typeMessageInShareBox(shareboxStatus, false);

		//Add a file
		//Click on Add file link
		log.info("INFO: Click on Add file link");
		ui.clickLinkWait(CommunitiesUIConstants.AddFileLink);
					
		//Upload file from Sharebox
		log.info("INFO: Upload file from Sharebox");
		filesUI.fileToUpload(file.getName(), CommunitiesUIConstants.ShareBoxFileInput);
		
		//Rename the file
		renameFile(file);
		
		//Click on Ok button
		log.info("INFO:Click on Ok button");
		ui.clickLinkWait(CommunitiesUIConstants.OkButtonFileUpload);
		
		//Verify the file name appears below the text message box
		log.info("Verify the file name appears below the text message box");
		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.ShareUpdateAttachmentFile).getText().contains(file.getName()),
							"ERROR: The Attached file is not present");
		
		//Verify there is an "X" icon
		log.info("INFO: Verify there is an Remove icon ");
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.ShareUpdateRemoveLink),
							"ERROR: Remove icon is not present");
		
		//Post the status to the private community
		log.info("INFO:Post the status");
		ui.clickButton("Post");
		
		//Switch back to the main frame
		log.info("INFO: Switch back to the main frame  ");
		ui.switchToTopFrame();
		ui.waitForPageLoaded(driver);
		
		//Test status message
		log.info("Test that status message is posted with the File attached to it");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.PostStatusMessage).getText(),Data.getData().statusSuccessMsg,
										"Error : Message is not shown properly");
		
			
		//Open I'm a Member
		log.info("Click on I'm a Member");
		ui.goToMemberView(isCardView);
		
		
		//Open the community
		log.info("INFO: Click on the community");
		ui.fluentWaitPresent(communityLink);
		ui.clickLinkWait(communityLink);
		
		// Go to Status updates
		log.info("INFO:Go to Status updates");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
		
		//Test that Status message posted is coming in status updates
		log.info("INFO: Test that Status message posted is coming in status updates");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(),member.getDisplayName()+"\n"+shareboxStatus,
													"Error: Posted message is not coming in Status updates");
		
		//Test file meta data
		log.info("INFO: Verify the file name is displayed");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateFileContent).getAttribute("title"), file.getName(),
								"ERROR: File uploaded is not displayed in Recent Updates view");

		//Select File post and open EE
		log.info("INFO: Select File post and open EE ");
		hUI.filterNewsItemOpenEE(shareboxStatus);
	
		//Wait to load frame
		log.info("INFO: Wait to load frame");
		hUI.waitForAndSwitchToEEFrame(HomepageUIConstants.GenericEEFrame, HomepageUIConstants.EEStatus, 3);
				
		//Test newsItem meta data on EE dialog
		log.info("INFO: Verify the newsItem status is displayed in EE dailog");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), shareboxStatus,
								"ERROR: Status is not displayed in EE dailog");
				
		//Test file meta data on EE dialog
		log.info("INFO: Verify the file name is displayed in EE dailog");
		Assert.assertTrue(ui.fluentWaitPresent("link="+ file.getName()),
								"ERROR: File uploaded is not displayed in EE dailog");

		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
		
		//logout as Member
		log.info("INFO: logout as Member");
		ui.logout();

		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));
				
		ui.endTest();
					
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part1: Verify Ability to Share a File with a community (Test 1)</li>
	 *<li><B>Info:</B> Verify the ability to share a file with a Public community via the global sharebox</li>
	 *<li><B>Step:</B> Create a Public community as owner using API & add a member to the community</li>
	 *<li><B>Step:</B> Login as member & share a file with Public community</li>
	 *<li><B>Verify:</B> File is successfully shared</li>
	 *<li><B>Step:</B> Go to community's File Widget</li>
	 *<li><B>Verify:</B> Verify that the file is listed & tag is added in Files view</li>
	 *<li><B>Step:</B> Go to the Recent Updates widget</li>
	 *<li><B>Verify:</B> Verify that both the entry in the Recent Updates widget & the EE pop-up both display the correct meta-data for the file being added</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6CCB4145DCE5FD2B85257C90005ACFC4">TTT-GLOBAL SHAREBOX PART1: VERIFY ABILITY TO SHARE A FILE WITH A COMMUNITY (On-prem only!)</a></li>
	 *</ul>
	 */
	//Test case is failing. Need to work on its fix
	@Test(groups = {"regression"} , enabled=false )
	public void testsharingFileWithPublicCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
			
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder("Global share public community" + rndNum)
													.access(Access.PUBLIC)	
													.description("Test Sharing file with a community " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, member))
													.build();
		//File
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
							.tags(Data.getData().commonTag + rndNum )
							.rename(Helper.genStrongRand())
							.extension(".jpg")
							.build();

		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);

		//Share file with community
		log.info("INFO: To Share file with community");
		shareFileWithCommunityAndValidate(community, file);

		//logout as Member
		log.info("INFO: logout as Member");
		ui.logout();

		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//delete community
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
				
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part1: Verify Ability to Share a File with a community (Test 2)</li>
	 *<li><B>Info:</B> Verify the ability to share a file with a Moderated community via the global sharebox</li>
	 *<li><B>Step:</B> Create a Moderated community as owner using API & add a member to the community</li>
	 *<li><B>Step:</B> Login as member & share a file with Moderated community</li>
	 *<li><B>Verify:</B> File is successfully shared</li>
	 *<li><B>Step:</B> Go to community's File Widget</li>
	 *<li><B>Verify:</B> Verify that the file is listed & tag is added in Files view</li>
	 *<li><B>Step:</B> Go to the Recent Updates widget</li>
	 *<li><B>Verify:</B> Verify that both the entry in the Recent Updates widget & the EE pop-up both display the correct meta-data for the file being added</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6CCB4145DCE5FD2B85257C90005ACFC4">TTT-GLOBAL SHAREBOX PART1: VERIFY ABILITY TO SHARE A FILE WITH A COMMUNITY (On-prem only!)</a></li>
	 *</ul>
	 */
	//Test case is failing. Need to work on its fix
	@Test(groups = {"regression"} , enabled=false )
	public void testsharingFileWithModeratedCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
			
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder("Global share moderated community" + rndNum)
													.access(Access.MODERATED)	
													.description("Test Sharing file with a community " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, member))
													.build();
		//File
		BaseFile file = new BaseFile.Builder(Data.getData().file2)
							.tags(Data.getData().commonTag + rndNum )
							.rename(Helper.genStrongRand())
							.extension(".jpg")
							.build();
		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);

		//Share file with community
		log.info("INFO: To Share file with community");
		shareFileWithCommunityAndValidate(community, file);

		//logout as Member
		log.info("INFO: logout as Member");
		ui.logout();

		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//delete community
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
				
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part1: Verify Ability to Share a File with a community (Test 3)</li>
	 *<li><B>Info:</B> Verify the ability to share a file with a Restricted community via the global sharebox</li>
	 *<li><B>Step:</B> Create a Restricted community as owner using API & add a member to the community</li>
	 *<li><B>Step:</B> Login as member & share a file with Restricted community</li>
	 *<li><B>Verify:</B> File is successfully shared</li>
	 *<li><B>Step:</B> Go to community's File Widget</li>
	 *<li><B>Verify:</B> Verify that the file is listed & tag is added in Files view</li>
	 *<li><B>Step:</B> Go to the Recent Updates widget</li>
	 *<li><B>Verify:</B> Verify that both the entry in the Recent Updates widget & the EE pop-up both display the correct meta-data for the file being added</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6CCB4145DCE5FD2B85257C90005ACFC4">TTT-GLOBAL SHAREBOX PART1: VERIFY ABILITY TO SHARE A FILE WITH A COMMUNITY (On-prem only!)</a></li>
	 *</ul>
	 */
	//Test case is failing. Need to work on its fix
	@Test(groups = {"regression"} , enabled=false )
	public void testsharingFileWithRestrictedCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
			
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder("Global share restricted community" + rndNum)
													.access(Access.RESTRICTED)	
													.description("Test Sharing file with a community " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, member))
													.shareOutside(false)
													.build();
		//File
		BaseFile file = new BaseFile.Builder(Data.getData().file3)
							.tags(Data.getData().commonTag + rndNum )
							.rename(Helper.genStrongRand())
							.extension(".jpg")
							.build();
		
		//create community
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
	
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, commAPI);
				
		//GUI
		//Load component and login as Member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);

		//Share file with community
		log.info("INFO: To Share file with community");
		shareFileWithCommunityAndValidate(community, file);

		//logout as Member
		log.info("INFO: logout as Member");
		ui.logout();

		//Load component and login as Owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//delete community
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
				
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Global Sharebox Part1: Verify Ability to Share a File with a community (Test 4)</li>
	 *<li><B>Info:</B> Verify the ability to share a file with a Moderated SubCommunity via the global sharebox</li>
	 *<li><B>Step:</B> Create a Public community as owner using API & add a member to the community</li>
	 *<li><B>Step:</B> Create a Moderated SubCommunity as owner using API & add a member to the SubCommunity</li>
	 *<li><B>Step:</B> Login as member & share a file with SubCommunity</li>
	 *<li><B>Verify:</B> File is successfully shared</li>
	 *<li><B>Step:</B> Go to community's File Widget</li>
	 *<li><B>Verify:</B> Verify that the file is listed & tag is added in Files view</li>
	 *<li><B>Step:</B> Go to the Recent Updates widget</li>
	 *<li><B>Verify:</B> Verify that both the entry in the Recent Updates widget & the EE pop-up both display the correct meta-data for the file being added</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/6CCB4145DCE5FD2B85257C90005ACFC4">TTT-GLOBAL SHAREBOX PART1: VERIFY ABILITY TO SHARE A FILE WITH A COMMUNITY (On-prem only!)</a></li>
	 *</ul>
	 */
	//Test case is failing. Need to work on its fix
	@Test(groups = {"regression"} , enabled=false )
	public void testsharingFileWithModeratedSubCommunity(){
		
		String rndNum = Helper.genDateBasedRand();
		
		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("Global share Moderated SubCommunity" + rndNum)
															.access(com.ibm.conn.auto.appobjects.base.BaseSubCommunity.Access.MODERATED)
														    .description("Test Sharing file with a SubCommunity")
														    .UseParentmembers(true)
														    .useActionMenu(true)
														    .build();
		
		log.info("INFO: Get UUID of community");
		community1.getCommunityUUID_API(apiOwner, comAPI1);
		
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community1.navViaUUID(ui);
		
		log.info("INFO: Create SubCommunity");
		subCommunity.create(ui);
		
		log.info("INFO: Check for the community name should not be empty message");
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
		
		log.info("INFO: logout as Owner");
		ui.logout();
		ui.close(cfg);
		
		log.info("INFO: Log into Communities as the Member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(member);
		
		log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
		if(ui.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
            
			log.info("INFO: Close Guided Tour dialog");
			ui.closeGuidedTourPopup();
		}

		log.info("INFO: Click on the Share link");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);
		
		log.info("INFO: Select Files tab");
		ui.clickLinkWait(CommunitiesUIConstants.ShareBoxFilesTab);
		
		log.info("INFO: Switch to the Sharebox frame");
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxFileFrameIdentifer);
		
		log.info("INFO: Upload file from Sharebox");
		try {
			filesUI.fileToUpload(file1.getName(), CommunitiesUIConstants.ShareBoxFileInput);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		renameFile(file1);
		
		log.info("INFO: Click on the Additional Options link");
		driver.getFirstElement(CommunitiesUIConstants.additionalOptionsLink).click();

		log.info("INFO: Enter the tag details");
		ui.typeText(FilesUIConstants.UploadFiles_Tag, file1.getTags());
		 
		log.info("INFO: Select People or Communities");
		ui.clickLinkWait(FilesUIConstants.shareWithPeople);
		
		log.info("INFO: Select Share with community from drop down");
		driver.getSingleElement(FilesUIConstants.PersonCommunityDropDown).useAsDropdown().selectOptionByVisibleText(Data.getData().communityOption);
		
		log.info("INFO: Select as Reader from drop down");
		driver.getSingleElement(FilesUIConstants.ReaderEditorDropDown).useAsDropdown().selectOptionByVisibleText(Data.getData().ReaderOption);
		
		log.info("INFO: Enter community name and type ahead kicks community");
		ui.typeText(FilesUIConstants.shareFileDialogCommunityInputBox, subCommunity.getName());
		hUI.typeaheadSelection(subCommunity.getName(), FilesUIConstants.TypeHeadCommunity);
		
		log.info("INFO: Select Allow others to share these files check box");
		if(!driver.getSingleElement(FilesUIConstants.AllowOthersToShareFilesCheckBox).getAttribute("value").contains("true"))
			ui.clickLinkWait(FilesUIConstants.AllowOthersToShareFilesCheckBox);
		
		log.info("INFO:Click on Upload button");
		ui.clickButton("Upload");

		log.info("INFO: Switch to Main window");
		ui.switchToTopFrame();

		log.info("INFO: ");
		ui.fluentWaitTextPresent("Successfully uploaded " + file1.getName());
		
		log.info("INFO: Test that we see the File successfully uploaded message");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.PostStatusMessage).getText(),"Successfully uploaded "+file1.getName()+ ".",
							"Error : Message is not shown properly");
		
		// check if catalog_card_view GK enabled
        boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		// get the community link
		String subcommunityLink = isCardView ? CommunitiesUI.getSubCommunityLinkCardView(subCommunity) : "link=" + subCommunity.getName();
		
		log.info("Click on I'm a Member");
		ui.goToMemberView(isCardView);
		
		log.info("INFO: Click on the community");
		ui.fluentWaitPresent(subcommunityLink);
		ui.clickLinkWait(subcommunityLink);
		
		log.info("INFO: Go to Files widget");
		Community_LeftNav_Menu.FILES.select(ui);
		
		log.info("INFO: Select Details display button");
		ui.fluentWaitPresent(Files_Display_Menu.DETAILS.getMenuItemLink());
		Files_Display_Menu.DETAILS.select(ui);
		
		log.info("INFO: Verify the file uploaded is present in Files view");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUI.selectFile(file1)),
							"ERROR: File uploaded is not present in Files view");
		
		log.info("INFO: Select more link of the file");
		filesUI.selectMoreLinkByFile(file1);
		
		log.info("INFO: Verify Tag is displayed for File uploaded");
		Assert.assertTrue(ui.isElementPresent("link="+ file1.getTags().toLowerCase()),
							"ERROR: Tag is not displayed for File");
		
		log.info("INFO: Go to Recent updated view");
		Community_LeftNav_Menu.RECENT_UPDATES.select(ui);
		
		log.info("INFO: Verify file is getting listed in recent updates");
		Assert.assertTrue(hUI.findNewsItem("shared a file with the community "+subCommunity.getName()+".").isVisible(),
								"Error: File shared is not displayed in Recent updates view");
		
		log.info("INFO: Verify the file name is displayed");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.RecentUpdateFileContent).getAttribute("title"), file1.getName(),
								"ERROR: File uploaded is not displayed in Recent Updates view");
		
		log.info("INFO: Verify the file tags is displayed");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.RecentUpdateTags).getText(), "Tags: " + file1.getTags().toLowerCase(),
								"ERROR: Uploaded File tag is not displayed in Recent Updates view");
		
		log.info("INFO: Select File post and open EE ");
		hUI.filterNewsItemOpenFileOverlay("shared a file with the community "+subCommunity.getName()+".");

		log.info("INFO: Verify the image preview displays on the file overlay page");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileOverlayImagePreview),
				"ERROR: File name does not appear on the file overlay");
				
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 **<li><B>Test Scenario:</B> Status Updates Widget: Verify the Status Update widget (1 of 5)</li>
	 *<li><B>Info:</B> Verify the ability to post a message in Status Updates widget</li>
	 *<li><B>Step:</B> Create a Public community as owner using API </li>
	 *<li><B>Step:</B> Login as owner & then open community & then go & post a status message</li>
	 *<li><B>Verify:</B> After typing the status message & save it</li> 
	 *<li><B>Verify:</B> After posting the status is visible & click on the "chevron icon"</li>
	 *<li><B>Verify:</B> EE pop-up (Embedded Experience)dialog box appears on the right side, and contains the text message</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1244829F6675E25185257C8D005B4C24">TTT-STATUS UPDATES WIDGET: Verify the Status Update widget</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void testStatusUpdatesInCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
								
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + rndNum )
										.description("Test Status update for community" + testName ).build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
			
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
			
		//Login as Owner
		ui.login(testUser);
			
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("Execute the test if GateKeeper setting for Tabbed Navigation is enabled");
		if(ui.checkGKSetting(Data.getData().commTabbedNav)){
			log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
			log.info("INFO: Click on Status Updates from the tabbed nav");				
			Community_TabbedNav_Menu.STATUSUPDATES.select(ui);

		}else { 

			//Test presence of Overview in Community card
			log.info("INFO: Verify presence of Overview in Community card");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.leftNavOverview),
					"Error : Overview link is not present");

			//hover over the Overview button on the left nav
			log.info("INFO: Hover over the Overview button on the left nav");
			driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

			//Test presence of Status Updates in Community card
			log.info("INFO: Verify presence of Status Updates in Community card");
			Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavStatusUpdates),
					"Error : Status Updates link is not present");

			//Click on Status Update
			log.info("INFO: Click on Status Updates ");
			ui.clickLinkWait(CommunitiesUIConstants.leftNavStatusUpdates);
		}
				
			
		//Type Status message
		log.info("INFO: Type the Status messge");
		ui.typeMessageInShareBox(Data.getData().UpdateStatus.trim(), true);
		
		//Ensures that the test is executed from the top of the page
		driver.executeScript("scroll(0, -250);");
			
		//Click on Post
		log.info("INFO: Posting of Status Message");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
			
		//Test that Message has been posted successfully
		log.info("INFO: Verify the Message has been posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
							"ERROR: The successfull message is not posted");
		
		//Test the Status Message is getting displayed
		log.info("INFO: Verify Status message is saved");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains(Data.getData().UpdateStatus.trim()),
							"Error: Status message is not getting displayed");
		
		//Select Status update active stream and open EE
		log.info("INFO: Select Status update post and open EE ");
		hUI.filterNewsItemOpenEE(Data.getData().UpdateStatus.trim());

		//Test newsItem meta data on EE dialog
		log.info("INFO: Verify the newsItem status is displayed in EE dailog");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), Data.getData().UpdateStatus.trim(),
								"ERROR: Status is not displayed in EE dailog");
		
		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
		
		//delete community
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
			
		ui.endTest();
			
	}
	
	/**
	 *<ul>
	 **<li><B>Test Scenario:</B> Status Updates Widget: Verify the Status Update widget (2 of 5)</li>
	 *<li><B>Info:</B> Verify that a status message with attached file can be created</li>
	 *<li><B>Step:</B> Create a Public community as owner using API </li>
	 *<li><B>Step:</B> Login as owner & then open community & then go & post a status message</li>
	 *<li><B>Verify:</B> After typing the status message, Add a file & save it</li> 
	 *<li><B>Verify:</B> After posting the status is visible & contains attached file</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1244829F6675E25185257C8D005B4C24">TTT-STATUS UPDATES WIDGET: Verify the Status Update widget</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void testStatusUpdatesAddFileInCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
								
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + rndNum )
										.description("Test Status update for community" + testName ).build();

		BaseFile file = new BaseFile.Builder(Data.getData().file1)
							.rename(Helper.genStrongRand())
							.extension(".jpg")
							.build();
		
		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
			
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
			
		//Login as Owner
		ui.login(testUser);
			
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
						
		//Click on Status Update
		log.info("INFO: Click on Status Updates ");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
			
		//Type Status message
		log.info("INFO: Type the Status messge");
		ui.typeMessageInShareBox(Data.getData().UpdateStatus.trim(), true);
		
		//Add a file
		//Click on Add file link
		log.info("INFO: Click on Add file link");
		ui.clickLinkWait(CommunitiesUIConstants.AddFileLink);
		
		//Upload file from Sharebox
		log.info("INFO: Upload file from Sharebox");
		filesUI.fileToUpload(file.getName(), BaseUIConstants.FileInputField2);
		
		//Rename the file
		renameFile(file);

		//Click on Ok button
		log.info("INFO:Click on Ok button");
		ui.clickLinkWait(CommunitiesUIConstants.OkButtonFileUpload);
		
		//Verify the file name appears below the text message box
		log.info("Verify the file name appears below the text message box");
		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.ShareUpdateAttachmentFile).getText().contains(file.getName()),
							"ERROR: The Attached file is not present");
		
		//Click on Post
		log.info("INFO: Posting of Status Message");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
			
		//Test that Message has been posted successfully
		log.info("INFO: Verify the Message has been posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
							"ERROR: The successfull message is not posted");
		
		//Test the Status Message is getting displayed
		log.info("INFO: Verify Status message is saved");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains(Data.getData().UpdateStatus.trim()),
							"Error: Status message is not getting displayed");
		
		//Test file in active stream
		log.info("INFO: Verify the file name is displayed");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateFileContent).getAttribute("title"), file.getName(),
								"ERROR: File uploaded is not displayed in active stream");
		
		//delete community
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
			
		ui.endTest();
			
	}
	
	/**
	 *<ul>
	 **<li><B>Test Scenario:</B> Status Updates Widget: Verify the Status Update widget (3 of 5)</li>
	 *<li><B>Info:</B>Verify the ability to add a hashtag to a status updates message</li>
	 *<li><B>Step:</B> Create a Public community as owner using API</li>
	 *<li><B>Step:</B> Login as owner & then open community & then go & post a status message which contains #Tag methods</li>
	 *<li><B>Verify:</B> After Status message with #Tag is visible</li> 
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1244829F6675E25185257C8D005B4C24">TTT-STATUS UPDATES WIDGET: Verify the Status Update widget</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void testStatusUpdatesWithHashInCommunity() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + rndNum )
										.description("Test Status update for community" + testName ).build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
			
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
			
		//Login as Owner
		ui.login(testUser);
			
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
			
		//Click on Status Update
		log.info("INFO: Click on Status Updates ");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
			
		//Type Status message
		log.info("INFO: Type the Status message with #Tag");							
		ui.typeMessageInShareBox(Data.getData().StatusHashMentionMsg, true);
		
		//This is to ensure the test is executed from the top of the page
		//Post button is hidden by the tabbed nav mini navigation
		driver.executeScript("scroll(0,-250)");
			
		//Click on Post
		log.info("INFO: Posting of Status Message");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
			
		//Test that Message has been posted successfully
		log.info("INFO: Verify the Message has been posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
							"ERROR: The successfull message is not posted");
		
		//Test the Status Message is getting displayed
		log.info("INFO: Verify Status message is saved");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText(), testUser.getDisplayName()+"\n"+Data.getData().StatusHashMentionMsg,
																	"Error: Status message is not getting displayed");
		//Test hashtag1 as a link
		log.info("INFO: Verify hashtag1 as a link");
		Assert.assertTrue(ui.fluentWaitPresent("link="+Data.getData().hashTag1),
							"ERROR: HashTag1 is not a link");
		
		//Test hashtag2 as a link
		log.info("INFO: Verify hashtag2 as a link");
		Assert.assertTrue(ui.fluentWaitPresent("link="+Data.getData().hashTag2),
							"ERROR: HashTag2 is not a link");
				
		//delete community
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
				
		ui.endTest();	
			
	}
	
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Status Updates Widget: Verify the Status Update widget (4 of 5)</li>
	 *<li><B>Info:</B> Verify the ability to add a comment to a status updates message</li>
	 *<li><B>Step:</B> Create a Public community as owner using API</li>
	 *<li><B>Step:</B> Login as owner & then open community & then go & post a status message</li>
	 *<li><B>Step:</B> Post a comment on the Status message</li>
	 *<li><B>Verify:</B> Comment is successfully posted</li> 
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1244829F6675E25185257C8D005B4C24">TTT-STATUS UPDATES WIDGET: Verify the Status Update widget</a></li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void testCommentsOnStatusMessage(){
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
								
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + rndNum )
										.description("Test Status update for community" + testName ).build();

		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: Click on Status Updates ");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
		
		log.info("INFO: Type the Status messge");
		ui.typeMessageInShareBox(Data.getData().UpdateStatus.trim(), true);
		
		log.info("INFO: Scroll to the top of the page");
		driver.clickAt(0, 0);
			
		log.info("INFO: Posting of Status Message");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
		
		log.info("INFO: Verify the Message has been posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
							"ERROR: The successfull message is not posted");

		log.info("INFO: Verify Status message is saved");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains(Data.getData().UpdateStatus.trim()),
							"Error: Status message is not getting displayed");
		
		log.info("INFO: Click on comment");
		ui.clickLinkWait(CommunitiesUIConstants.StatusComment);
		
		log.info("INFO: Enter the comment");
		List<Element> frames = driver.getVisibleElements(CommunitiesUIConstants.ShareBoxComment_iFrame);
		int frameCount = 0;
		for(Element frame : frames){
			frameCount++;
			log.info("INFO: Frame toString: " + frame.toString());
			log.info("INFO: Frame location: " + frame.getLocation());
			//The first CK Editor iframe will be for the embedded sharebox
			if(frameCount == 1){
				log.info("INFO: Switching to Frame: " + frameCount);
				driver.switchToFrame().selectFrameByElement(frame);
			}
		}			
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
		driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(Data.getData().commonComment);
		
		log.info("INFO: Returning to parent frame to click 'Post' button");
		driver.switchToFrame().returnToTopFrame();
		
		log.info("INFO: Scroll to the top of the page");
		driver.clickAt(0,0);
		
		log.info("INFO: Click on Post");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
		
		log.info("INFO: Verify the comment is posted");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.CommentOnStatus).getText(),Data.getData().commonComment,
							"Error: The coment is not posted properly");
		
		log.info("INFO:Cleanup - Removing community");
		community.delete(ui, testUser);
			
		ui.endTest();
			
	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Status Updates Widget: Verify the Status Update widget (5 of 5)</li>
	 *<li><B>Info:</B> Verify that a status message can be "Liked" & "Unliked" </li>
	 *<li><B>Step:</B> Create a Public community as owner using API</li>
	 *<li><B>Step:</B> Login as owner & then open community & then go & post a status message</li>
	 *<li><B>Step:</B> Like a comment & verify it changes to Unlike</li>
	 *<li><B>Verify:</B>Verify that the number "1" now appears before the "Unlike" link</li> 
	 *<li><B>Step:</B> Unlike a comment & verify it changes to Like</li>
	 *<Li><B>Verify:</B>Verify that the number "1" no longer appears before the "Unlike" link</li>
	 *<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/1244829F6675E25185257C8D005B4C24">TTT-STATUS UPDATES WIDGET: Verify the Status Update widget</a></li>
	 *</ul>
	 */
	//Test case is failing. Need to work on its fix
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void testLikeUnlikeOnStatusMessage() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
								
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + rndNum )
										.access(Access.PUBLIC)
										.tags(Data.getData().commonTag + rndNum )
										.description("Test Status update for community" + testName ).build();

		//create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
			
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
			
		//Login as Owner
		ui.login(testUser);
			
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
			
		//Click on Status Update
		log.info("INFO: Click on Status Updates ");
		Community_LeftNav_Menu.STATUSUPDATES.select(ui);
										
		//Type Status message
		log.info("INFO: Type the Status messge");
		ui.typeMessageInShareBox(Data.getData().UpdateStatus.trim(), true);
		
		//Ensures that the test is executed from the top of the page
		driver.executeScript("scroll(0, -250);");
			
		//Click on Post
		log.info("INFO: Posting of Status Message");
		ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
			
		//Test that Message has been posted successfully
		log.info("INFO: Verify the Message has been posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
							"ERROR: The successfull message is not posted");

		//Test the Status Message is getting displayed
		log.info("INFO: Verify Status message is saved");
		Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains(Data.getData().UpdateStatus.trim()),
							"Error: Status message is not getting displayed");
				
		//Like a comment
		log.info("INFO: Click on Like");
		ui.clickLinkWait(HomepageUIConstants.EELike);
		
		//Test it changes to UnLike
		log.info("INFO: Verify UnLike is visible");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.EELikeUndo),
							"ERROR: UnLike is not visible");
			
		//Test number of likes is 1
		log.info("INFO: Verify that the number 1 now appears before the Unlike link");
		Assert.assertEquals(Integer.parseInt(driver.getSingleElement(HomepageUIConstants.EELikeCount).getText()), 1,
								"ERROR: The number of Like's is not equal to 1");
		
		//UnLike a comment
		log.info("INFO: Click on UnLike");
		ui.clickLinkWait(HomepageUIConstants.EELikeUndo);
		
		//Test it changes to Like
		log.info("INFO: Verify Like is visible");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.EELike),
							"ERROR: Like is not visible");
		
		//Test number of likes does not exists
		log.info("INFO: Verify that the number 1 no longer appears");
		Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EELikeCount).getText(), " ",
							"ERROR: The number of Like's is still showing up");
				
		//delete community
		log.info("INFO: Removing community");
		community.delete(ui, testUser);
			
		ui.endTest();
			
	}
	
	/**
	*Info: To share a file with community and Validate in Files/Recent Updates view
	* @param: community, file
	* @return: None
	* @throws Exception 
	*/
	private void shareFileWithCommunityAndValidate(BaseCommunity community, BaseFile file) throws Exception {
		//To share a file with community and Validate in Files/Recent Updates view
		log.info("INFO: To share a file with community and Validate in Files/Recent Updates view");
		
		//click on the Share link
		log.info("INFO: Click on the Share link");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);
		
		//select files tab
		log.info("INFO: Select Files tab");
		ui.clickLinkWait(CommunitiesUIConstants.ShareBoxFilesTab);
		
		//Switch to the Sharebox frame
		log.info("INFO: Switch to the Sharebox frame");
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxFileFrameIdentifer);
		
		//Upload file from Sharebox
		log.info("INFO: Upload file from Sharebox");
		filesUI.fileToUpload(file.getName(), CommunitiesUIConstants.ShareBoxFileInput);
		
		//Rename file
		renameFile(file);
		
		//Click on Additional Options link
		log.info("INFO: Click on the Additional Options link");
		driver.getFirstElement(CommunitiesUIConstants.additionalOptionsLink).click();
		
		//Enter Tag
		log.info("INFO: Enter the tag details");
		ui.typeText(FilesUIConstants.UploadFiles_Tag, file.getTags());
		
		//Select Share with field 
		log.info("INFO: Select People or Communities");
		ui.clickLinkWait(FilesUIConstants.shareWithPeople);
		
		//Select Share with community
		log.info("INFO: Select Share with community from drop down");
		driver.getSingleElement(FilesUIConstants.PersonCommunityDropDown).useAsDropdown().selectOptionByVisibleText(Data.getData().communityOption);
		
		//Select as Reader
		log.info("INFO: Select as Reader from drop down");
		driver.getSingleElement(FilesUIConstants.ReaderEditorDropDown).useAsDropdown().selectOptionByVisibleText(Data.getData().ReaderOption);
		
		//Enter community name
		log.info("INFO: Enter community name and type ahead kicks community");
		ui.typeText(FilesUIConstants.shareFileDialogCommunityInputBox, community.getName());
		hUI.typeaheadSelection(community.getName(), FilesUIConstants.TypeHeadCommunity);
		
		//Select the checkbox 
		log.info("INFO: Select Allow others to share these files check box");
		if(!driver.getSingleElement(FilesUIConstants.AllowOthersToShareFilesCheckBox).getAttribute("value").contains("true"))
			ui.clickLinkWait(FilesUIConstants.AllowOthersToShareFilesCheckBox);
		
		//Click on upload button
		log.info("INFO:Click on Upload button");
		ui.clickButton("Upload");

		//Switch back to the main frame
		log.info("INFO: Switch to Main window");
		ui.switchToTopFrame();

		//Confirm that the file was uploaded
		log.info("INFO: ");
		ui.fluentWaitTextPresent("Successfully uploaded " + file.getName());
		
		//File successfully uploaded message
		log.info("INFO: Test that we see the File successfully uploaded message");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.PostStatusMessage).getText(),"Successfully uploaded "+file.getName()+ ".",
							"Error : Message is not shown properly");
		
		// check if catalog_card_view GK enabled
        boolean isCardView = ui.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		// get the community link
		String communityLink = isCardView ? CommunitiesUI.getCommunityLinkCardView(community) : CommunitiesUI.getCommunityLink(community);
			
		//Open I'm a Member
		log.info("Click on I'm a Member");
		ui.goToMemberView(isCardView);
		
		//Open the community
		log.info("INFO: Click on the community");
		ui.fluentWaitPresent(communityLink);
		ui.clickLinkWait(communityLink);
		
		//Go to Files Widget
		log.info("INFO: Go to Files widget");
		Community_LeftNav_Menu.FILES.select(ui);
		
		//Switch the display from default Tile to Details
		log.info("INFO: Select Details display button");
		ui.fluentWaitPresent(Files_Display_Menu.DETAILS.getMenuItemLink());
		Files_Display_Menu.DETAILS.select(ui);
		
		//Select File uploaded
		log.info("INFO: Verify the file uploaded is present in Files view");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUI.selectFile(file)),
							"ERROR: File uploaded is not present in Files view");
		
		//Select more link
		log.info("INFO: Select more link of the file");
		filesUI.selectMoreLinkByFile(file);
		
		//Test Tags
		log.info("INFO: Verify Tag is displayed for File uploaded");
		Assert.assertTrue(ui.isElementPresent("link="+ file.getTags().toLowerCase()),
							"ERROR: Tag is not displayed for File");
		
		//Go to Recent Updates view
		log.info("INFO: Go to Recent updated view");
		Community_LeftNav_Menu.RECENT_UPDATES.select(ui);
		
		//Test addition of shared file is getting listed in recent updates
		log.info("INFO: Verify file is getting listed in recent updates");
		Assert.assertTrue(hUI.findNewsItem("shared a file with the community "+community.getName()+".").isVisible(),
								"Error: File shared is not displayed in Recent updates view");
		
		//Test file meta data
		log.info("INFO: Verify the file name is displayed");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.RecentUpdateFileContent).getAttribute("title"), file.getName(),
								"ERROR: File uploaded is not displayed in Recent Updates view");
		//Test file tags
		log.info("INFO: Verify the file tags is displayed");
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.RecentUpdateTags).getText(), "Tags: " + file.getTags().toLowerCase(),
								"ERROR: Uploaded File tag is not displayed in Recent Updates view");
		
		//Select File post and open EE
		log.info("INFO: Select File post and open EE ");
		hUI.filterNewsItemOpenFileOverlay("shared a file with the community "+community.getName()+".");
		
		//Verify the image preview displays on the file overlay page
		log.info("INFO: Verify the image preview displays on the file overlay page");
		Assert.assertTrue(driver.isElementPresent(FilesUIConstants.fileOverlayImagePreview),
				"ERROR: File name does not appear on the file overlay");

		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
		
	}
	
	/**
	*Info: Rename an uploaded file
	* @param: File
	* @return: None
	* @throws Exception 
	*/
	private void renameFile(BaseFile uploadedFile) {
		//Rename the file
		if(uploadedFile.getRename() != null) {
			ui.clickLinkWait(filesUI.renameFileSelector(uploadedFile.getName()));
			//Wait so that the webpage has time to respond to the click.
			ui.fluentWaitElementVisible(FilesUIConstants.UploadFiles_Name);
			ui.clearText(FilesUIConstants.UploadFiles_Name);
			ui.typeTextWithDelay(FilesUIConstants.UploadFiles_Name, uploadedFile.getRename());
			
			// Click on the uploaded file icon to take focus off the file input and effect the file name change
			driver.getSingleElement(CommunitiesUIConstants.uploadedFileIcon).click();
			
			//change the file name inside the object
			log.info("INFO: Change the name of the file");
			uploadedFile.setName(uploadedFile.getRename()+ uploadedFile.getExtension());
			
			ui.fluentWaitElementVisible(FilesUIConstants.fileRenameMsg);
		}
	}
	
	/**
	*Info: To share a Status message And a File if needed with community
	* @param: community, File
	* @return: None
	* @throws Exception 
	*/
	private void shareStatusMsgAndFileWithCommunity(BaseCommunity community, BaseFile file, String shareboxStatus) throws Exception {
		log.info("INFO: To Share status Msg and File with community");
		
		//click on the Share link
		log.info("INFO: click on the Share link");
		ui.clickLinkWait(CommunitiesUIConstants.ShareLink);
		
		//Switch to the Sharebox frame
		log.info("INFO: switch to sharebox frame");
		ui.fluentWaitPresent(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		driver.switchToFrame().selectSingleFrameBySelector(CommunitiesUIConstants.ShareBoxStatusFrameIdentifer);
		
		//Select post updates to drop down
		log.info("INFO: Select post updates to drop down");
		driver.getSingleElement(CommunitiesUIConstants.ShareBoxPostType).useAsDropdown().selectOptionByVisibleText(Data.getData().communityOption);
		
		//Select the community created earlier
		log.info("INFO: Select the community created earlier");
		ui.typeText(CommunitiesUIConstants.ShareBoxCommunityPickerTextBox, community.getName());
		hUI.typeaheadSelection(community.getName(), CommunitiesUIConstants.ShareBoxCommunityPickerPopup);
		
		//Enter the description
		log.info("INFO: Enter the description");
		ui.typeMessageInShareBox(shareboxStatus, false);

		//Add a file
		log.info("INFO: Validate if file needs to be attached");
		if(file != null ) {
			//Click on Add file link
			log.info("INFO: Click on Add file link");
			ui.clickLinkWait(CommunitiesUIConstants.AddFileLink);
						
			//Upload file from Sharebox
			log.info("INFO: Upload file from Sharebox");
			filesUI.fileToUpload(file.getName(), CommunitiesUIConstants.ShareBoxFileInput);
			
			//Rename file
			renameFile(file);
			
			//Click on Ok button
			log.info("INFO:Click on Ok button");
			ui.clickLinkWait(CommunitiesUIConstants.OkButtonFileUpload);
			
			//Verify the file name appears below the text message box
			log.info("Verify the file name appears below the text message box");
			Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.ShareUpdateAttachmentFile).getText().contains(file.getName()),
								"ERROR: The Attached file is not present");
			
			//Verify there is an "X" icon
			log.info("INFO: Verify there is an Remove icon ");
			Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.ShareUpdateRemoveLink),
								"ERROR: Remove icon is not present");
		}
		
		//Post the status to the private community
		log.info("INFO:Post the status");
		ui.clickButton("Post");
		
		//Switch back to the main frame
		log.info("INFO: Switch back to the main frame  ");
		ui.switchToTopFrame();
		ui.waitForPageLoaded(driver);
		
		//Test status message
		log.info("Test that status message is posted with the File attached to it");
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.PostStatusMessage).getText(),Data.getData().statusSuccessMsg,
										"Error : Message is not shown properly");
	}
	
	
	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Quick Smoketest for Communities on the Cloud </li>
	 * <li><B>Info:</B> This test will verify an Activity Stream entry is posted in Recent Updates for: </li>
	 * <li><B>Info:</B> community create, wikis, blogs, ideation blog and forums </li>
	 * <li><B>Step:</B> Community is created via the API </li>
	 * <li><B>Step:</B> Log into Communities </li>
	 * <li><B>Step:</B> Add Blogs, Ideation Blog, and if on-prem add the Wiki widget </li>
	 * <li><B>Step:</B> Navigate to the Recent Updates view </li>
	 * <li><B>Verify:</B> Verify there is an AS entry for the community create, forums, community wiki, wiki, blogs & ideation blogs </li>
	 * <li><B>Cleanup:</B> Delete the communities </li> 
	 * <li><a HREF="Notes://wrangle/85257863004CBF81/8B3C604EE244C1CB85257F93004B7FDA/E584B3CA657BBCC985257F470059ACEC">TTT - QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
	 *</ul>
	 */			

@Test(groups = {"regression", "regressioncloud"})
public void recentUpdatesASEntries() throws Exception {
	
	String rndNum = Helper.genDateBasedRand();
	String testName = ui.startTest();
	String product = cfg.getProductName();
	BaseWidget widget = BaseWidget.BLOG;
	BaseWidget widget1 = BaseWidget.IDEATION_BLOG;
	BaseWidget widget2 = BaseWidget.WIKI;
	
	BaseCommunity community = new BaseCommunity.Builder(testName + rndNum )
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag + rndNum )
									.description("Recent Updates activity stream entry test " + testName ).build();
	
	//Create community
	log.info("INFO: Create community using API");
	Community comAPI = community.createAPI(apiOwner);

	//Add the UUID to community
	log.info("INFO: Get UUID of community");
	community.getCommunityUUID_API(apiOwner, comAPI);

	//Load component and login
	log.info("INFO: Load Commnities");
	ui.loadComponent(Data.getData().ComponentCommunities);

	log.info("INFO: Log into communities");
	ui.login(testUser);
	
	//Navigate to the API community
	log.info("INFO: Navigate to the community using UUID");
	community.navViaUUID(ui);

	log.info("INFO: Add Blog widget");
	if (apiOwner.getWidgetID(comAPI.getUuid(), "Blog").isEmpty()) 
	{
		community.addWidgetAPI(comAPI, apiOwner, widget);
	}

	//Add widget - Ideation Blog
	log.info("INFO: Add Ideation Blog widget");
	community.addWidgetAPI(comAPI, apiOwner, widget1);

	log.info("INFO: Add Wiki widget");

	if(product.equalsIgnoreCase("onprem")){
		log.info("INFO: Add wiki widget");
		if (apiOwner.getWidgetID(comAPI.getUuid(), "Wiki").isEmpty()) 
		{
			community.addWidgetAPI(comAPI, apiOwner, widget2);	
		}
	}
	
	log.info("Execute the test if GateKeeper setting for Tabbed Navigation is enabled");
	if(ui.checkGKSetting(Data.getData().commTabbedNav)){
		log.info("INFO: Click on Recent Updates from the tabbed nav");
		log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
		Community_TabbedNav_Menu.RECENT_UPDATES.select(ui,2);

	}else { 
		//hover over the Overview button on the left nav
		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		//Verify Recent Updates is listed on the left nav
		log.info("INFO: Verify Recent Updates is listed on the left nav");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavRecentUpdates),
				"Error : Recent Updates link is not listed on the left nav");

		//Click on the Recent Updates link on the left nav
		log.info("INFO: Click on the Recent Updates link on the left nav");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavRecentUpdates);
	}

	//Verify there is an Activity Stream (AS) entry for community created
	log.info("INFO: Verify there is an Activity Stream entry for community created");
	Assert.assertTrue(driver.isTextPresent(testUser.getDisplayName() + " created the community."),
			"ERROR: There is no community created entry.");

	//Verify there is an Activity Stream entry for forums
	log.info("INFO: Verify there is an Activity Stream entry for Forums");
	Assert.assertTrue(driver.isTextPresent(testUser.getDisplayName() + " created the " + community.getName() + " forum."),
			"ERROR: There is no created forum entry");
	
	//Verify there is an Activity Stream entry for community wiki
	log.info("INFO: Verify there is an Activity Stream entry for community Wikis");
	Assert.assertTrue(driver.isTextPresent(testUser.getDisplayName() + " created a wiki named " + community.getName() + "." ),
			"ERROR: There is no created community wiki entry");
	
	//Verify there is an Activity Stream entry for wikis
	log.info("INFO: Verify there is an Activity Stream entry for Wikis");
	Assert.assertTrue(driver.isTextPresent(testUser.getDisplayName() + " created a wiki page named Welcome to " + community.getName() + " in the " + community.getName() + " wiki." ),
			"ERROR: There is no created wiki entry");

	//Verify there is an Activity Stream entry for blogs
	log.info("INFO: Verify there is an Activity Stream entry for Blogs");
	Assert.assertTrue(driver.isTextPresent(testUser.getDisplayName() + " added the " + community.getName() + " community blog."),
			"ERROR: There is no created blog entry");

	//Verify there is an Activity Stream entry for ideation blogs
	log.info("INFO: Verify there is an Activity Stream entry for Ideation Blogs");
	Assert.assertTrue(driver.isTextPresent(testUser.getDisplayName() + " added the " + community.getName() + " community Ideation Blog."),
			"ERROR: There is no created ideation blog entry");
	
	//Delete community
	log.info("INFO: Removing community");
	community.delete(ui, testUser);
		
	ui.endTest();
		
}

/**
 * <ul>
 * <li><B>Test Scenario:</B> Quick Smoketest for Communities on the Cloud </li>
 * <li><B>Info:</B> This test will verify hashtag, @mention & image in a status updates entry. </li>
 * <li><B>Info:</B> This test will also cover comments, likes, and EE pop-up. </li>
 * <li><B>Step:</B> Create community using the API </li>
 * <li><B>Step:</B> Log into Communities </li>
 * <li><B>Step:</B> Open the Community created using the API </li>
 * <li><B>Step:</B> Click on Status Updates link from left nav </li>
 * <li><B>Step:</B> Post an entry with a hashtag, mention and image file attached </li>
 * <li><B>Verify:</B> Verify the entry was successfully posted </li>
 * <li><B>Step:</B> Lke the entry </li>
 * <li><B>Verify:</B> Verify the Unlike link now displays </li>
 * <li><B>Step:</B> Create a comment with an @mention </li>
 * <li><B>Step:</B> Unlike the comment from EE pop-up dialog </li>
 * <li><B>Cleanup:</B> Delete the communities </li> 
 * <li><a HREF="Notes://wrangle/85257863004CBF81/8B3C604EE244C1CB85257F93004B7FDA/E584B3CA657BBCC985257F470059ACEC">TTT - QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
 *</ul>
 */			

//disabled test case as its failing due to some iframe issue
@Test(groups = {"regression", "regressioncloud"} , enabled=false )
public void statusUpdateWithHashtagMentionImageComment() {

    String rndNum = Helper.genDateBasedRand();
    String testName = ui.startTest();
    String statusUpdate = " take a look at this message with hashtag #test " + Helper.genDateBasedRand();
    String statusComment = "this comment contains a mention & hashtag #comment " + Helper.genDateBasedRand() + " ";
    User testUser2 = cfg.getUserAllocator().getUser();
						
   BaseCommunity community = new BaseCommunity.Builder(testName + rndNum )
								.access(Access.PUBLIC)
								.tags(Data.getData().commonTag + rndNum )
								.description("Community Status Updates entry with hashtag, mention & image." + testName ).build();

   BaseFile file = new BaseFile.Builder(Data.getData().file1)
                                         .rename(Helper.genStrongRand())
                                         .extension(".jpg")
                                         .build();

   
     log.info("INFO: Create community using API");
     Community comAPI = community.createAPI(apiOwner);

     log.info("INFO: Get UUID of community");
     community.getCommunityUUID_API(apiOwner, comAPI);

     log.info("INFO: Log into Communities");
     ui.loadComponent(Data.getData().ComponentCommunities);
     ui.login(testUser);

     log.info("INFO: Navigate to the community using UUID");
     community.navViaUUID(ui);

     log.info("INFO: Verify presence of Overview link");
     Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.tabbedNavOverviewTab),
		  "Error : Overview link is not present");

     log.info("INFO: Hover over the Overview link");
     driver.getSingleElement(CommunitiesUIConstants.tabbedNavOverviewTab).hover();

     log.info("INFO: Verify presence of Status Updates link");
     Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavStatusUpdates),
		  "Error : Status Updates link is not present");

     log.info("INFO: Click on Status Updates ");
     ui.clickLinkWait(CommunitiesUIConstants.leftNavStatusUpdates);

     log.info("INFO: Create status update entry with an @mention: " + testUser1.getEmail());
     hUI.postAtMentionUserUpdate(testUser1, statusUpdate);

     log.info("INFO: Click on the Add a File link");
     ui.clickLinkWait(CommunitiesUIConstants.AddFileLink);

     log.info("INFO: Add a file to the status updates entry.  Enter the file name and path");
     try {
		filesUI.fileToUpload(file.getName(), BaseUIConstants.FileInputField2);
	} catch (Exception e) {
		e.printStackTrace();
	}

     log.info("INFO: Click OK button to upload file");
     ui.clickLinkWait(CommunitiesUIConstants.OKButton);

     log.info("INFO: Posting of Status Message");
     ui.clickLinkWait(CommunitiesUIConstants.StatusPost);

     log.info("INFO: Verify the message that the entry has been successfully posted displays");
     Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage),
    		 "ERROR: The success message did not display");
    
	 hUI.scrollIntoViewElement(HomepageUI.getStatusUpdateMesage(statusUpdate));
	
	 hUI.scrollIntoViewElement(FilesUIConstants.PopupLikeFile);

     log.info("INFO: Click on the 'Like' link for the status updates entry with content: " + statusComment);
     hUI.moveToClick(HomepageUI.getStatusUpdateMesage(statusUpdate), FilesUIConstants.PopupLikeFile);

     log.info("INFO: Verify the 'Unlike' link now dislays");
     Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.EELikeUndo),
    		 "ERROR: The Unlike link does not appear for the status updates entry");

     log.info("INFO: Post a comment with a mention");
     ui.postCommunityUpdateCommentWithAtMentions(statusUpdate, statusComment, testUser2);    

     log.info("INFO: Open the EE for the status updates entry");
     hUI.filterNewsItemOpenEE(statusUpdate);

     log.info("INFO: Click on UnLike");
     ui.clickLinkWait(HomepageUIConstants.EELikeUndo);

     log.info("INFO: Switch to main frame");
     ui.switchToTopFrame();

     log.info("INFO: Cleanup - Removing community");
     community.delete(ui, testUser);

     ui.endTest();

}

/**
 * <ul>
 * <li><B>Test Scenario:</B> Quick Smoketest for Communities on the Cloud</li>
 * <li><B>Info:</B> Quick test for Recent Updates </li>
 * <li><B>Step:</B> Create a Public Community using the API </li>
 * <li><B>Step:</B> Log into the Community as the community owner </li>
 * <li><B>Step:</B> Open the community using UUID </li>
 * <li><B>Step:</B> Hover over the left nav </li>
 * <li><B>Verify:</B> Verify Recent Updates appears on the left nav </li>
 * <li><B>Step:</B> Click on the Recent Updates link </li>
 * <li><B>Step:</B> Post a Recent Updates entry with an @mention & some text </li>
 * <li><B>Step:</B> Click on the Recent Updates entry to open the EE pop-up </li>
 * <li><B>Step:</B> Post a comment with an @mention in the EE pop-up </li>
 * <li><B>Verify:</B> Verify the recent updates message appears in the EE pop-up </li>
 * <li><B>Verify:</B> Verify the comment posted in the EE pop-up appears </li>
 * <li><B>Step:</B> From the EE pop-up, click on the 'Like' link for the status updates entry </li>
 * <li><B>Verify:</B> Verify an 'Unlike' link now displays </li>
 * <li><B>Step:</B> Return to the Recent Updates page </li>
 * <li><B>Verify:</B> Verify the 'Unlike' link appears for the status updates entry </li>
 * <li><B>Verify:</B> Verify the 'Like' count for the status updates entry is 1 </li>
 * <li><B>Cleanup:</B> Delete the communities </li> 
 * <li><a HREF="Notes://wrangle/85257863004CBF81/8B3C604EE244C1CB85257F93004B7FDA/E584B3CA657BBCC985257F470059ACEC">TTT - QUICK SMOKETEST FOR COMMUNITIES ON CLOUD</a></li>
 *</ul>
 */			

@Test(groups = {"regression", "regressioncloud"} , enabled=true )
public void recentUpdatesPostMessageAndComment() throws Exception {

	String rndNum = Helper.genDateBasedRand();
	String testName = ui.startTest();
	String statusComment = "this comment contains a mention & hashtag #comment " + Helper.genDateBasedRand();
	String mention = Character.toString('@');

	BaseCommunity community = new BaseCommunity.Builder(testName + rndNum )
	                                           .access(Access.PUBLIC)
	                                           .tags(Data.getData().commonTag + rndNum )
	                                           .description("Community Recent Updates entry with hashtag, mention & image." + testName ).build();

	//Create community
	log.info("INFO: Create community using API");
	Community comAPI = community.createAPI(apiOwner);

	//Add the UUID to community
	log.info("INFO: Get UUID of community");
	community.getCommunityUUID_API(apiOwner, comAPI);

	//Load component and login
	ui.loadComponent(Data.getData().ComponentCommunities);

	//Login as Owner
	ui.login(testUser);

	//Navigate to the API community
	log.info("INFO: Navigate to the community using UUID");
	community.navViaUUID(ui);

	log.info("Execute the test if GateKeeper setting for Tabbed Navigation is enabled");
	if(ui.checkGKSetting(Data.getData().commTabbedNav)){
		log.info("INFO: Click on Recent Updates from the tabbed nav");
		log.info("INFO: Gatekeeper flag " + gk_flag + " is set to " + value + " so expect the Tabbed Nav Bar");
		Community_TabbedNav_Menu.RECENT_UPDATES.select(ui);

	}else { 

		//hover over the Overview button on the left nav
		log.info("INFO: Hover over the Overview button on the left nav");
		driver.getSingleElement(CommunitiesUIConstants.leftNavOverviewButton) .hover();

		//Test presence of Recent Updates in Community card
		log.info("INFO: Verify presence of Recent Updates in Community card");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.leftNavRecentUpdates),
				"Error : Recent Updates link is not present");

		//Click on Recent Update
		log.info("INFO: Click on Recent Updates ");
		ui.clickLinkWait(CommunitiesUIConstants.leftNavRecentUpdates);
	}

	//Create a status update entry with an @mention & some text
	log.info("INFO: Create recent update entry with an @mention and some text: " + testUser1.getEmail());
	ui.typeMessageInShareBox(Data.getData().UpdateStatus.trim(), true);
	
	//Ensures that the test is executed from the top of the page
	driver.executeScript("scroll(0, -250);");

	//Click Post button to save the status update message
	log.info("INFO: Post of Recent Updates message - click Post button");
	ui.clickLinkWait(CommunitiesUIConstants.StatusPost);
	
	//Test the Status Message is getting displayed
	log.info("INFO: Verify Status message is saved");
	Assert.assertTrue(driver.getFirstElement(CommunitiesUIConstants.RecentUpdateMessageList).getText().contains(Data.getData().UpdateStatus.trim()),
			"Error: Status message is not getting displayed");			
			
	//Select Status update active stream and open EE
	log.info("INFO: Select Status update post and open EE ");
	hUI.filterNewsItemOpenEE(Data.getData().UpdateStatus.trim());

	//Test the meta data on EE dialog
	log.info("INFO: Verify the status update data displays in the EE dialog");
	Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), Data.getData().UpdateStatus.trim(),
			"ERROR: Status update data does not display in the EE dialog");

	//Click on the 'Like' link for the status message in the EE
	log.info("INFO: Click on the Like link for the status message in the EE");
	driver.getFirstElement(HomepageUIConstants.EELike).click();

	//Post a comment in EE
	log.info("INFO: Post a comment in EE");
	ui.clickLinkWait(HomepageUIConstants.EECommentsTab);
	log.info("INFO: Switching to comments frame");
	Element commentframe = driver.getSingleElement(HomepageUIConstants.StatusUpdateFrame);
	driver.switchToFrame().selectFrameByElement(commentframe);

	log.info("INFO: Enter some text into the EE comment field");
	ui.fluentWaitElementVisible(HomepageUIConstants.StatusUpdateTextField);
	Element inputField = driver.getSingleElement(HomepageUIConstants.StatusUpdateTextField);
	inputField.click();
	inputField.type(statusComment);

	//Enter the @mention into the EE pop-up
	log.info("INFO: Enter the @mention into the EE pop-up");
	driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).typeWithDelay(" " + mention + testUser1.getDisplayName());

	//Select the user from the typeahead list
	log.info("INFO: Select the option from the mentions typeahead suggestion list");
	selectTypeaheadUserInEEUsingArrowKeys();	
	hUI.switchToEEFrame();

	//Click on the Post button
	log.info("INFO: Click the Post button");
	ui.clickLinkWait(HomepageUIConstants.OpenEEPostCommentButton);
	
	//Test the meta data on EE dialog
	log.info("INFO: Verify the status update data displays in the EE dialog");
	Assert.assertEquals(driver.getSingleElement(HomepageUIConstants.EEStatus).getText(), Data.getData().UpdateStatus.trim(),
							"ERROR: Status update data does not display in the EE dialog");

	//Verify the comment is displayed in the EE
	log.info("INFO: Verify the comment is displayed in the EE");
	Assert.assertTrue(ui.fluentWaitTextPresent(statusComment),
			"ERROR: Comment is not displayed in the EE");

	//Verify the the 'Unlike' link now displays in the EE
	log.info("INFO: Verify the 'Unlike' link now dislays");
	Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.EELikeUndo),
			"ERROR: The Unlike link does not appear for the status updates entry");

	//Go to the Activity Stream and verify the comment appears
	log.info("INFO: " + testUser1.getDisplayName() + " go to the Activity Stream and verify the comment appears");
	ui.switchToTopFrame();

	//Verify that the 'Unlike' link also displays on the Recent Updates page
	log.info("INFO: Verify the 'Unlike' link displays on the Recent updates page");
	Assert.assertTrue(driver.isElementPresent(HomepageUIConstants.EELikeUndo),
			"ERROR: The Unlike link does not appear for the status updates entry");

	//Verify the number of Likes is 1
	log.info("INFO: Verify that the number 1 now appears before the 'Unlike' link");
	Assert.assertEquals(Integer.parseInt(driver.getFirstElement(HomepageUIConstants.EELikeCount).getText()), 1,
			"ERROR: The number of Like's is not equal to 1");	

	//Delete community
	log.info("INFO: Removing community");
	community.delete(ui, testUser);

	ui.endTest();
		
}

public String selectTypeaheadUserInEEUsingArrowKeys() {
	
	log.info("INFO: Now pressing the UP ARROW key a number of times to move up the list of typeahead menu items");
	for(int index = 0; index < 7; index ++) {
		driver.switchToActiveElement().type(Keys.ARROW_UP);
	}
	
	hUI.waitForEETypeaheadMenuToLoad();
	
	log.info("INFO: Retrieve all menu items to verify which one is about to be selected");
	List<Element> menuItemElements = hUI.getTypeaheadMenuItemsList(false);
	
	String selectedMenuItem = null;
	if(menuItemElements.size() > 5) {
		log.info("INFO: The fifth last user from the typeahead menu is now being selected");
		selectedMenuItem = menuItemElements.get(menuItemElements.size() - 5).getText();
		
	} else if(menuItemElements.size() <= 5 && menuItemElements.size() > 1) {
		log.info("INFO: The second user from the typeahead menu is now being selected");
		selectedMenuItem = menuItemElements.get(1).getText();
		
	} else {
		log.info("INFO: The first user from the typeahead menu is now being selected");
		selectedMenuItem = menuItemElements.get(0).getText();
	}
	log.info("INFO: Now pressing the ENTER key to select the highlighted user in the typeahead menu");
	driver.switchToActiveElement().type(Keys.ENTER);
	
	return selectedMenuItem;
}
	
}
