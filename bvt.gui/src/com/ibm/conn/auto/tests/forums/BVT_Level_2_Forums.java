/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.forums;


import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.utils.Assert;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.cnx8.ForumsUICnx8;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Forums extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Forums.class);
	private Assert cnxAssert;
	private ForumsUI ui;
	private CommunitiesUI cUI;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private User testUser1;
	private User testUser2;
	private BaseCommunity.Access defaultAccess;
	private ForumsUICnx8 forumsUICnx8;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
				
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	  
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		forumsUICnx8 = new ForumsUICnx8(driver);

		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		cnxAssert = new Assert(log);
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Validate the Mega Menu</li>
	 *<li><B>Step: </B>Click the Apps dropdown Mega Menu</li>
	 *<li><B>Verify: </B>Mega Menu Forums options is in the drop down menu</li>
	 *<li><B>Verify: </B>Mega Menu item for I'm an Owner option is in Apps drop down menu</li>
	 *<li><B>Verify: </B>Mega Menu item for Public Forums option is in Apps drop down menu</li>
	 *</ul>
	 *Note: this is not supported on the cloud
	 */
	@Test(groups = {"level2", "bvt"})
	public void validateMegaMenu() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		// Load the component and login
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//Click Mega Menu item
		logger.strongStep("Select the 'Forums' Mega Menu option");
		log.info("INFO: Select the 'Forums' Mega Menu option");
		ui.clickLinkWait(BaseUIConstants.MegaMenuApps);
		
		//Validate Forums option is contained with in drop down menu
		logger.weakStep("Validate that the 'Forums' option is contained within drop down menu");
		log.info("INFO: Validate that the 'Forums' option is contained within drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		cnxAssert.assertTrue(ui.fluentWaitPresent(ForumsUIConstants.forumsOption),
						  "ERROR: Unable to locate the Mega Menu 'Forums' option in drop down menu");

		//Validate I'm an Owner option is contained with in drop down menu
		logger.weakStep("Validate that the 'I'm an Owner' option is contained within drop down menu");
		log.info("INFO: Validate that the 'I'm an Owner' option is contained within drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		cnxAssert.assertTrue(ui.fluentWaitPresent(ForumsUIConstants.forumsImAnOwner),
						  "ERROR: Unable to locate the Mega Menu 'I'm an Owner' option in drop down menu");

		//Validate Public Forums option is contained with in drop down menu
		logger.weakStep("Validate that the 'Public Forums' option is contained within drop down menu");
		log.info("INFO: Validate that the 'Public Forums' option is contained within drop down menu");
		ui.selectMegaMenu(BaseUIConstants.MegaMenuApps);
		cnxAssert.assertTrue(ui.fluentWaitPresent(ForumsUIConstants.forumsPublicForums),
						  "ERROR: Unable to locate the Mega Menu 'Public Forums' option in drop down menu");

		ui.endTest();
	
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Create a Forum</li>
	*<li><B>Step </B>Go to Apps drop down menu and select Forums</li>
	*<li><B>Step: </B>Click the Start a Forum button to create a forum</li>
	*<li><B>Step: </B>Save the Forum</li>
	*<li><B>Verify: </B>Add Owner displays in the forum</li>
	*<li><B>Verify: </B>Start a topic button displays in the forum</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/ 
	@Test(groups = { "regression", "bvt", "cnx8ui-cplevel2"})
	public void createForum() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();

		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);
		
		// Verify Add Owners link is present
		logger.weakStep("Validate that the 'Add Owners' link is present in the forum");
		log.info("INFO: Validate that the 'Add Owners' link is present in the forum");
		ui.validateAddOwner();
		
		//Verify Start Topic button is present
		logger.weakStep("Validate that the 'Start a topic' button displays in the forum");
		log.info("INFO: Validate that the 'Start a topic' button displays in the forum");
		cnxAssert.assertTrue(driver.isElementPresent(ForumsUIConstants.StartATopic),
				  			"ERROR: Unable to locate the 'Start a topic' button in the forum");
		
		ui.endTest();
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Deleting a forum</li>
	*<li><B>Step: </B>Go to Apps drop down menu and select Forums</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the Start a Forum button to create a forum</li>
	*<li><B>Step: </B>Create a forum</li>
	*<li><B>Verify: </B>Forum has been created</li> 
	*<li><B>Step: </B>Click Forum Actions > Delete Forum</li>
	*<li><B>Verify: </B>Status message states the forum has been successfully deleted</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/ 
	@Test(groups = { "cplevel2", "level2", "bvt", "cnx8ui-cplevel2"})
	public void deleteForum() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();

		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);	
	
		//delete forum
		logger.strongStep("Delete the Forum");
		log.info("INFO: Delete the Forum");
		forum.delete(ui);
		
		logger.weakStep("Validate that the status message stating that the Forum was successfully deleted is displayed");
		log.info("INFO: Validate that the status message stating that the Forum was successfully deleted is displayed");
		cnxAssert.assertTrue(driver.isElementPresent(ForumsUIConstants.forumDeleteMsg),
						  "ERROR: Unable to locate the delete Forum status message");
		
		ui.endTest();
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Add a Topic to Forum</li>
	*<li><B>Step: </B>Go to Apps drop down menu and select Forums</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li>
	*<li><B>Step: </B>Input information of Forum and save</li> 
	*<li><B>Step: </B>Click the Start a Topic button</li>
	*<li><B>Step: </B>Input all information of topic and save</li>
	*<li><B>Step: </B>Go to Apps < Forums < I'm an Owner</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the title link of the created forum</li>
	*<li><B>Verify: </B>Topic displays in the Forum's topic list</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/
	@Test(groups = { "regression", "bvt"})
	public void addForumTopic() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription).build();
		
		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);

		//Create a new topic inside the Forum
		logger.strongStep("Create a new Forum topic");
		log.info("INFO: Create a new Forum topic");
		topic.create(ui);
		
		// Go to main page of the forum created above
		logger.strongStep("Select the 'Forums Header' link");
		log.info("INFO: Select the 'Forums Header' link");
		ui.clickLinkWait(ForumsUIConstants.topComponentForumLink);
		
		logger.strongStep("Select the left menu option 'I'm An Owner'");
		log.info("INFO: Select the left menu option 'I'm An Owner'");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		ui.clickLinkWait(ForumsUIConstants.Centre_Content_Filter_Tabs_Tab1);
		
		// Click link to the page of the forum created above
		logger.strongStep("Select the Forum created earlier");
		log.info("INFO: Select the Forum created earlier");
		ui.clickLinkWait("link=" + forum.getName());

		//Verify the topic exists by clicking it
		logger.weakStep("Validate that the Forum topic exists");
		log.info("INFO: Validate that the Forum topic exists");
		ForumsUI.selectForumTopic(topic);
		cnxAssert.assertTrue(ui.fluentWaitTextPresent(topic.getTitle()), 
						  "ERROR: Failed to find topic");
		
		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Info: </B>Creating a Forum Topic with Image Attachment</li>
	*<li><B>Steps: </B>Go to Apps, click Forums</li>
	*<li><B>Steps: </B>Click "Start a Forum" button</li>
	*<li><B>Steps: </B>Enter topic details, attach an image and save<.</li> 
	*<li><B>Verify: </B>Topic page shows all information and action links</li>
	*<li><B>Verify: </B>The Attachments area displays</li>
	*<li><B>Verify: </B>The attachment thumbnail displays</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	*/
	@Test(groups = { "level2", "bvt", "cnx8ui-cplevel2", "cnx8ui-level2" })
	public void addForumTopicWithImage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
										.tags(Data.getData().commonTag)
										.description(Data.getData().commonDescription)
										.addAttachment(Data.getData().file7).build();
		
		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);

		//Create a new topic inside the Forum
		logger.strongStep("Create a new Forum topic");
		log.info("INFO: Create a new Forum topic");
		topic.create(ui);
		
		logger.weakStep("Validate that the 'Attachements' area displays");
		log.info("INFO: Verify that the 'Attachments' area displays");
		cnxAssert.assertTrue(driver.isElementPresent(ForumsUIConstants.AttachHeader),
						  "ERROR: Unable to locate the 'Attacthment' area");
		
		logger.weakStep("Verify that the 'Attachment thumbnail' displays");
		log.info("INFO: Verify that the 'Attachment thumbnail' displays");
		cnxAssert.assertTrue(driver.isElementPresent(ForumsUIConstants.AttachThumbnail),
						  "ERROR: Unable to locate the 'Attacthment thumbnail'");

		ui.endTest();
	}	
	
	/**
	*<ul>
	*<li><B>Info: </B>Replying to a Forum Topic</li>
	*<li><B>Step: </B>Go to the Apps drop down menu and Click Forums</li>
	*<li><B>Step: </B>Click the Forums Tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li>
	*<li><B>Step: </B>Input Forum details and save</li>
	*<li><B>Step: </B>Click the Start a Topic button</li>
	*<li><B>Step: </B>Input all information of topic and save</li>
	*<li><B>Step: </B>Logout of user one</li>
	*<li><B>Step: </B>Login with user two</li>
	*<li><B>Step: </B>Click the forum title link and forum topic created by user one</li>
	*<li><B>Verify: </B>Click on Like image</li>
	*<li><B>Verify: </B>Validate the presence of Share link only on CNX8UI</li>
	*<li><B>Step: </B>Click reply, input information and save</li>
	*<li><B>Verify: </B>Count of replies to the topic created above is 1</li>
	*</ul>
	* note: this test is not valid in smart cloud environment as smart cloud does not support forums natively
	* Extra validation added to verify https://jira.cwp.pnp-hcl.com/browse/CNXSERV-13819 this defect
	*/
	@Test(groups = { "level2", "bvt", "cnx8ui-cplevel2"})
	public void forumTopicReply() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();
		
		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal())
									   .tags(Data.getData().commonTag)
									   .description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription).build();
		
		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//Navigate to owned Forms
		logger.strongStep("Navigate to the 'Owned Forums' view");
		log.info("INFO: Navigate to the 'Owned Fourms' view");
		ui.clickLinkWait(ForumsUIConstants.Im_An_Owner);
		
		//Create a forum
		logger.strongStep("Create a new Forum");
		log.info("INFO: Create a new Forum");
		forum.create(ui);

		//Create a new topic inside the Forum
		logger.strongStep("Create a new Forum topic");
		log.info("INFO: Create a new Forum topic");
		topic.create(ui);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Like the Forum topic");
		log.info("INFO: Like the Forum topic");
		ui.clickLinkWait(ForumsUIConstants.LikeLink);
		
		//select Forums to return
		logger.strongStep("Select the 'Forums' link in the top left corner");
		log.info("INFO: Select the 'Forums' link in top left corner");
		ui.clickCornerTopLeftForumsLink();
		
		logger.strongStep("Log Out of " + testUser1.getDisplayName());
		log.info("INFO: Log Out of "+testUser1.getDisplayName());
		ui.logout();
		
		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser2.getDisplayName());
		log.info("INFO: Load Forums and Log In as: "+testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums, true);
		ui.loginAndToggleUI(testUser2,cfg.getUseNewUI());
		ui.waitForPageLoaded(driver);
		
		//select public forums
		logger.strongStep("Select 'Public Forums' from left menu");
		log.info("INFO: Select 'Public Forums' from left menu");
		if(!cfg.getUseNewUI()) {
			ui.clickLinkWait(ForumsUIConstants.Public_Forums_Tab);
		}
		else {
			forumsUICnx8.clickLinkWaitWd(By.xpath(ForumsUIConstants.publicForumsLinksCNX8UI), 5, "Click on Public Forums");
		}	
		
		//Select the forum created by testUser1
		logger.strongStep("Select the Forum created by " + testUser1.getDisplayName());
		log.info("INFO: Select the Forum created by " + testUser1.getDisplayName());
		ui.clickLinkWait("link=" + forum.getName());
		
		//Select the forum topic created by testUser1
		logger.strongStep("Select the Forum topic created by " + testUser1.getDisplayName());
		log.info("INFO: Select the Forum topic created by " + testUser1.getDisplayName());
		ui.clickLinkWait("link=" + topic.getTitle());
		ui.waitForPageLoaded(driver);
		
		//Select like button
		logger.strongStep("Click on Like image " );
		log.info("INFO: Click on Like image ");
		WebElement likeImage = forumsUICnx8.findElement(By.xpath(ForumsUIConstants.likeImage));
		forumsUICnx8.mouseHoverAndClickWd(likeImage);
		forumsUICnx8.clickLinkWaitWd(By.xpath(ForumsUIConstants.likedUserpopupCloseIcon), 4,"Click on close icon");
		
		//Validate the presence of Share link
		logger.strongStep("Validate the presence of Share link only on CNX8UI " );
		log.info("INFO: Validate the presence of Share link link only on CNX8UI  ");
		if (cfg.getUseNewUI()) {
			cnxAssert.assertTrue(forumsUICnx8.isElementVisibleWd(By.cssSelector(ForumsUIConstants.shareForumTopic), 4),
					"Validate the presence of Share link");
		}
		
		// Reply to topic
		logger.strongStep("Create a reply to the Forum topic");
		log.info("INFO: Create a reply to the Forum topic");
		ui.replyToTopic(topic);
		
		//Select the forum created by testUser1
		logger.strongStep("Select the Forum created by " + testUser1.getDisplayName());
		log.info("INFO: Select the Forum created by " + testUser1.getDisplayName());
		driver.executeScript("arguments[0].scrollIntoView(true);", 
				driver.getElements(ForumsUIConstants.allForumsLink).get(0).getWebElement());
		ui.clickLinkWait(ForumsUI.getForumLink(forum));
		
		// Verify that the count of replies to the topic created above is 1
		logger.weakStep("Validate that only one reply to the Forum topic was recorded");
		log.info("INFO: Validate that only one reply to the Forum topic was recorded");
		driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		ui.fluentWaitPresent(ForumsUIConstants.First_Topic_Number_of_Replies);
		String repliesNum = driver.getSingleElement(ForumsUIConstants.First_Topic_Number_of_Replies).getText();
		cnxAssert.assertTrue(repliesNum.compareToIgnoreCase("1") == 0,
						  "ERROR: Number of replies is '"+ repliesNum+"', expected '1'");

		ui.endTest();
		
	}
	/**
	*<ul>
	*<li><B>Info: </B>Creating a Community Forum Topic</li>
	*<li><B>Step: </B>Create a community</li>
	*<li><B>Step: </B>Click Forums in the left nav pane</li>
	*<li><B>Step: </B>Click Start the First Topic button</li>
	*<li><B>Step: </B>Input all information of the topic and save</li>
	*<li><B>Verify: </B>Topic page shows all information and action links</li>
	*<li><B>Step: </B>Click Overview in the left nav pane</li>
	*<li><B>Step: </B>Click the existing Forums post</li>
	*<li><B>Verify: </B>Post is there by clicking the Post title link</li>
	*</ul>
	* note: this test is valid in smart cloud environment
	*/
	@Test(groups = { "regression", "bvt", "bvtcloud", "regressioncloud", "cnx8ui-cplevel2" })
	public void comForumTopic() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName=ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
												   .access(defaultAccess)
												   .build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription)
										   		 .partOfCommunity(community)
										   		 .build();
		
		//create community
		logger.strongStep("Create a new Community");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Check to see if the Forum widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Forum widget is enabled. If it is not enabled, then enable it");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FORUM)) {
			log.info("INFO: Add the Forum widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FORUM);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		// get the Catalog Card View GK flag
		boolean isCardView = cUI.checkGKSetting(Data.getData().gk_catalog_card_view);
		
		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communities' view");
		cUI.goToDefaultIamOwnerView(isCardView);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		
		//Create a new topic inside the Forum
		logger.strongStep("Create a new Forum topic");
		log.info("INFO: Create a new Forum topic");
		topic.create(ui);
		
		logger.strongStep("Select 'Overview' from navigation menu");
		log.info("INFO: Select 'Overview' from navigation menu");
		Community_LeftNav_Menu.OVERVIEW.select(cUI);
		
		logger.strongStep("Select 'Forums' from the navigation menu");
		log.info("INFO: Select 'Forums' from the navigation menu");
		cUI.fluentWaitElementVisible(ForumsUIConstants.Start_A_Topic);
		Community_LeftNav_Menu.FORUMS.select(cUI);

		//Validate the post exists by clicking it
		logger.weakStep("Validate that the Forum topic exists");
		log.info("INFO: Validate that the Forum topic exists");
		ui.clickLinkWait("css=a[class='lotusBreakWord bidiAware']:contains(" + topic.getTitle() + ")");
		cnxAssert.assertTrue(ui.fluentWaitTextPresent(topic.getTitle()), 
						  "ERROR: Unable to locate the Forum topic");

		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
	}
	
	
	/**
	*<ul>
	*<li><B>Info: </B>Reply to a Community Forum Topic</li>
	*<li><B>Step: </B>Create a Community</li> 
	*<li><B>Step: </B>Click the Start the First Topic button under Forums on the overview page</li>
	*<li><B>Step: </B>Input the Topic information and save</li>
	*<li><B>Step: </B>Click Forums in the left nav pane</li>
	*<li><B>Step: </B>Click the Forums tab</li>
	*<li><B>Step: </B>Click the Start a Forum button</li> 
	*<li><B>Step: </B>Input all information of topic and save</li> 
	*<li><B>Step: </B>Logout with user one</li>
	*<li><B>Step: </B>Login with user two</li> 
	*<li><B>Step: </B>Go to the community created by user one</li>
	*<li><B>Step: </B>Join the community</li>
	*<li><B>Step: </B>Click the forum topic created by user one</li>
	*<li><B>Step: </B>Click Reply to reply to the Forum topic and then save</li>
	*<li><B>Verify: </B>Reply to the Forum topic is present</li>
	*</ul>
	* note: this test is valid in smart cloud environment
	*/
	@Test(groups = { "level2", "bvt", "regressioncloud" , "cplevel2"})
	public void comForumTopicReply() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
													.addMember(new Member(CommunityRole.OWNERS, testUser2))
													.access(defaultAccess)
													.build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + Helper.genDateBasedRandVal())
										   		 .tags(Data.getData().commonTag)
										   		 .description(Data.getData().commonDescription)
										   		 .partOfCommunity(community)
										   		 .build();
		
		//create community
		logger.strongStep("Create a new Community");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		logger.strongStep("Check to see if the Forum widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Forum widget is enabled. If it is not enabled, then enable it.");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FORUM)) {
			log.info("INFO: Add forum widget to community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FORUM);
		}
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());

		// get the Catalog Card View GK flag
		boolean isCardView = cUI.checkGKSetting(Data.getData().gk_catalog_card_view);
				
		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communities view");
		cUI.goToDefaultIamOwnerView(isCardView);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		
		//Create a new topic inside the Forum
		logger.strongStep("Create a new Forum topic");
		log.info("INFO: Create a new Forum topic");
		topic.create(ui);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Log Out of " + testUser1.getDisplayName());
		log.info("INFO: Log Out of " + testUser1.getDisplayName());
		ui.logout();
		//ui.close(cfg);
		
		//Load the component
		logger.strongStep("Log In as " + testUser2.getDisplayName());
		log.info("INFO: Log In as " + testUser2.getDisplayName());
		cUI.loadComponent(Data.getData().ComponentCommunities,true);
		cUI.loginAndToggleUI(testUser2,cfg.getUseNewUI());
		
		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communities view");
		cUI.goToDefaultIamOwnerView(isCardView);
				
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		flag = cUI.isHighlightDefaultCommunityLandingPage();
		
		if(flag)
		{
		apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		//navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		
		//Select the forum topic created by testUser1
		logger.strongStep("Select the Forum topic created by " + testUser1.getDisplayName());
		log.info("INFO: Select the Forum topic created by " + testUser1.getDisplayName());
		ui.clickLink(ForumsUI.selectForumTopic(topic));

		//Reply to topic
		logger.strongStep("Reply to the Forum topic");
		log.info("INFO: Reply to the Forum topic");
		ui.replyToTopic(topic);
		
		// scroll to the top in case the items in nav bar are all folded into More
		driver.executeScript("window.scrollTo(0, 0)");
		
		//Click Forums link
		logger.strongStep("Go to 'Community Overview'");
		log.info("INFO: Go to 'Community Overview'");
		Community_TabbedNav_Menu.OVERVIEW.select(ui, 2);
		
		//Select the forum topic created by testUser1
		logger.strongStep("Select the Forum topic again");
		log.info("INFO: Select the Forum topic again");
		ui.fluentWaitElementVisible(ForumsUIConstants.Start_A_Topic);
		ui.clickLink(ForumsUI.selectForumTopic(topic));
		
		//Validate the new topic
		logger.weakStep("Validate that the reply to the Forum topic exists");
		log.info("INFO: Validate that the reply to the Forum topic exists");
		cnxAssert.assertTrue(ui.fluentWaitTextPresent("Re: " + topic.getTitle()),
						  "ERROR: Unable to locate the reply to the Forum topic");

		apiOwner.deleteCommunity(comAPI);

		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Creating a community Forum Topic with Image Attachment</li>
	*<li><B>Steps: </B>Create a community</li>
	*<li><B>Steps: </B>Select Start the First Topic in Forums widget</li>
	*<li><B>Steps: </B>Click "Start a Forum" button</li>
	*<li><B>Steps: </B>Enter topic details, attach an image and save</li>
	*<li><B>Verify: </B>The Attachments area displays</li>
	*<li><B>Verify: </B>The attachment thumbnail displays</li>
	*</ul>
	* note: this test is valid in smart cloud environment
	*/
	@Test(groups = { "cplevel2", "level2", "bvt", "smokeonprem", "regressioncloud", "bvtcloud", "smokecloud" , "cnx8ui-cplevel2"})
	public void addComForumTopicWithImage() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
												   .access(defaultAccess)
												   .build();

		BaseForumTopic topic = new BaseForumTopic.Builder("Topic for " + testName)
										   		 .tags(Data.getData().commonTag)
										   		 .description("testing add attachment")
										   		 .addAttachment(Data.getData().file6)
										   		 .partOfCommunity(community)
										   		 .build();
		
		//create community
		logger.strongStep("Create a new Community using API");
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get the UUID of the Community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		logger.strongStep("Check to see if the Forum widget is enabled. If it is not enabled, then enable it");
		log.info("INFO: Checking to see if the Forum widget is enabled. If it is not enabled, then enable it");
		if(!apiOwner.hasWidget(comAPI, BaseWidget.FORUM)) {
			log.info("INFO: Add the Forum widget to the Community using API");
			community.addWidgetAPI(comAPI, apiOwner, BaseWidget.FORUM);
		}
		
		//GUI
		//Load component and login
		logger.strongStep("Load Forums and Log In as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		cUI.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		// get the Catalog Card View GK flag
		boolean isCardView = cUI.checkGKSetting(Data.getData().gk_catalog_card_view);
				
		//Navigate to owned communities
		logger.strongStep("Navigate to the 'Owned Communities' view");
		log.info("INFO: Navigate to the 'Owned Communities' view");
		cUI.goToDefaultIamOwnerView(isCardView);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = cUI.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		logger.strongStep("Navigate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(cUI);
		
		//Wait for all widgets to load
		logger.strongStep("Wait for the page and all widgets to load");
		log.info("INFO: Wait for the page and all widgets to load");
		ui.waitForJQueryToLoad(driver);
		ui.waitForPageLoaded(driver);
		
		//Create a new topic inside the Forum
		logger.strongStep("Create a new Forum topic");
		log.info("INFO: Create a new Forum topic");
		topic.create(ui);
		
		logger.weakStep("Validate that the 'Attachements' area displays");
		log.info("INFO: Validate that the 'Attachments' area displays");
		cnxAssert.assertTrue(driver.isElementPresent(ForumsUIConstants.AttachHeader),
						  "ERROR: Unable to locate the 'Attacthments' area");
		
		logger.weakStep("Validate that the 'Attachment thumbnail' displays");
		log.info("INFO: Validate that the 'Attachment thumbnail' displays");
		cnxAssert.assertTrue(driver.isElementPresent(ForumsUIConstants.AttachThumbnail),
						  "ERROR: Unable to Locate the 'Attatchment thumbnail'");

		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();
	}
}
