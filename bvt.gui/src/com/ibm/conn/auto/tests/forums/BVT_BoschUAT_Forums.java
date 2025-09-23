/* ***************************************************************** */
/*                                                                   */
/* HCL Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright HCL Technologies Limited 2019,2020                      */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.forums;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.ForumBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.forums.ForumEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class BVT_BoschUAT_Forums extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_BoschUAT_Forums.class);
	private ForumsUI ui;
	private TestConfigCustom cfg;
	private User testUser;
	private  APIForumsHandler forumsAPIUser;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private BaseForum baseForum;
	private Forum standaloneForum;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
				
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		forumsAPIUser = new APIForumsHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		// test user will now create a standalone forum
		baseForum = ForumBaseBuilder.buildBaseForum(getClass().getSimpleName() + Helper.genStrongRand());
		standaloneForum = ForumEvents.createForum(testUser, forumsAPIUser, baseForum);
		
	  
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
		CommunitiesUI.getGui(cfg.getProductName(), driver);

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify HTML editor in Forums Topic</li>
	 *<li><B>Step:</B> [API] Create Forums topic using API</li>
	 *<li><B>Step:</B> Login to Forums component</li>
	 *<li><B>Step:</B> Select and edit Forums topic</li>
	 *<li><B>Step:</B> Select HTML source tab</li>
	 *<li><B>Step:</B> Enter a HTML tag with text</li>
	 *<li><B>Step:</B> Switch to Rich Text tab</li>
	 *<li><B>Verify:</B> Verify that entered text should be displayed correctly in Rich Text editor</li>
	 *<li><B>Step:</B> Switch back to HTML source tab</li>
	 *<li><B>Verify:</B>Verify that entered text should be displayed correctly in HTML editor</li>
	 *</ul>
	 */
	@Test(groups = { "regression" })
	public void verifyHTMLEditorInForumsTopic() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String HTMLText = "<p dir =\"ltr\">This is test</p>";
		String RichText = "This is test";
		String testName = ui.startTest();
		
		// Create Forums topic using API
		logger.strongStep("Create Forums topic using API");
		log.info("Create Forums topic using API");
		BaseForumTopic baseForumTopic = ForumBaseBuilder.buildBaseForumTopic(testName + Helper.genStrongRand(), standaloneForum);
		ForumEvents.createForumTopic(testUser, forumsAPIUser, baseForumTopic);
		
		// Login to Forums component
		logger.strongStep("Login to Forums component");
		log.info("Login to Forums component");
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);
		
		// Select and edit Forums topic's page
		logger.strongStep("Select and edit Forums topic's page");
		log.info("Select and edit Forums topic's page");
		ui.clickLinkWait(ui.getTopicSelector(baseForumTopic));
		ui.clickLinkWait(ForumsUIConstants.EditLink);
		
		// Select HTML Source tab
		logger.strongStep("Select HTML source tab");
		log.info("INFO: Select HTML source tab");
		ui.clickLinkWait(BlogsUIConstants.HTML_Source_Tab);
		
		// Enter HTML tag with text
		logger.strongStep("Enter a HTML tag with text");
		log.info("INFO: Enter a HTML tag with text: " + HTMLText);
		driver.getSingleElement(BlogsUIConstants.HTMTTextArea).clear();
		ui.typeText(BlogsUIConstants.HTMTTextArea, HTMLText);
		
		// Switch to Rich Text tab
		logger.strongStep("Switch to Rich Text tab");
		log.info("INFO: Switch to Rich Text tab");
		ui.clickLinkWait(BlogsUIConstants.Rich_Text_Tab);
		
		// Verify entered text in Rich Text Tab
		logger.strongStep("Verify that entered text should be displayed correctly in Rich Text editor");
		log.info("INFO: Verify that entered text should be displayed correctly in Rich Text editor: " + RichText);
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		Assert.assertEquals(driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).getText(),RichText);
		
		// Switch back to HTML source tab
		logger.strongStep("Switch back to HTML source tab");
		log.info("INFO: Switch back to HTML source tab");
		ui.switchToTopFrame();
		ui.clickLinkWait(BlogsUIConstants.HTML_Source_Tab);
		
		// Verify entered text in HTML Source Tab
		logger.strongStep("Verify that entered text should be displayed correctly in HTML editor");
		log.info("INFO: Verify that entered text should be displayed correctly in HTML editor: " + HTMLText);
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.HTMTTextArea).getAttribute("value").trim(),"<p dir=\"ltr\">This is test</p>");
		
		forumsAPIUser.deleteForum(standaloneForum);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Forums Tags View on Cloud and List</li>
	*<li><B>Step: </B>Load component and login</li>
	*<li><B>Step: </B>Navigate to the 'Owned Fourms' view"/li>
	*<li><B>Step: </B>Create a new Forum</li>
	*<li><B>Step: </B>Create a new Forum topic</li>
	*<li><B>Verify: </B>View the added Tag in Cloud section</li>
	*<li><B>Verify: </B>View the added Tag in List section</li>
	*</ul>
	*/
	@Test(groups = { "regression"})
	public void forumsTagsonCloudandListView() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName=ui.startTest();

		BaseForum forum = new BaseForum.Builder(testName + Helper.genDateBasedRandVal()).tags(Data.getData().commonTag)
				.description(Data.getData().commonDescription).build();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag).description(Data.getData().commonDescription).build();

		//Load the component
		logger.strongStep("Load Forums and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentForums);
		ui.login(testUser);
		
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
		
		logger.strongStep("View the added Tag in Cloud section");
		log.info("INFO: View the added Tag in Cloud section");
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.cloudTagView));
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(topic.getTags()),"ERROR: Added Tag is not dispalyed");
		
		logger.strongStep("View the added Tag in List section");
		log.info("INFO: View the added Tag in List section");
		ui.clickLink(BaseUIConstants.listTagView);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(topic.getTags()),"ERROR: Added Tag is not dispalyed");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify copy and paste image in Forums Topic</li>
	 *<li><B>Step:</B> [API] Create Community using API</li>
	 *<li><B>Step:</B> Login to Communities component</li>
	 *<li><B>Step:</B> Go to communities -> My communities </li>
	 *<li><B>Step:</B> Select I'm Owner option from the left filter menu </li>
	 *<li><B>Step:</B> Click on the community created with API</li>
	 *<li><B>Step:</B> Go to Forums</li>
	 *<li><B>Step:</B> Click on start a Topic button</li>
	 *<li><B>Step:</B> Enter Topic description</li>
	 *<li><B>Step:</B> Open image url in new tab and copy image in clipboard</li>
	 *<li><B>Step:</B> Paste copied image in Forum Topic description</li>
	 *<li><B>Step:</B> Click on save button to save Forum Topic with image</li>
	 *<li><B>Verify: Verify url of the same image in forum topic post
	 *</li>
	 *</ul>
	 */
	@Test(groups = { "regression" })
	public void verifyCopyPasteImageInForumsTopic() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseForumTopic topic = new BaseForumTopic.Builder(testName + "_topic" + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag).description(Data.getData().commonDescription).build();

		log.info("INFO: " + testUser.getDisplayName() + " creating a new community using the API");

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).build();

		Community publicCommunity = community.createAPI(apiOwner);

		// Add the UUID to community
		log.info("INFO: Set UUID of community");
		community.setCommunityUUID(community.getCommunityUUID_API(apiOwner, publicCommunity));

		// Login to connections Homepage
		logger.strongStep("Login to Homepage");
		log.info("Login to Homepage");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// Select Communities->My Communities
		log.info("Select Communities->My Communities");
		logger.strongStep("Select Communities->My Communities");
		ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
		ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuMyCommunities);
		ui.clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView);

		// Click on 'Refine this view icon'-> Select I'm member view
		log.info("Click on 'Refine this view icon'-> Select I'm Owner view");
		logger.strongStep("Click on 'Refine this view icon'-> Select I'm Owner view");
		ui.clickLinkWait(CommunitiesUI.getCommunityView(Community_View_Menu.IM_AN_OWNER));

		// Perform refresh at least 5 times if the community created with api is
		// available in my communities
		log.info("Verify if community created with API is present");
		logger.strongStep("Verify if community created with API is present");
		ui.fluentWaitPresentWithRefresh("css=div[aria-label=\'" + community.getName() + "\']");

		// open community via link
		logger.strongStep("INFO: Select community");
		log.info("INFO: Select community");
		ui.clickLink("css=div[aria-label=\'" + community.getName() + "\']");

		// navigate to community forums
		logger.strongStep("Navigate to the community forums");
		log.info("INFO: navigate to the community forums");
		Community_LeftNav_Menu.FORUMS.select(ui);

		// Start a Topic
		log.info("INFO: Click on Start a topic button");
		logger.strongStep("INFO: Click on Start a topic button");
		ui.clickLinkWait(ForumsUIConstants.Start_A_Topic);

		// Add title
		log.info("INFO: Entering title of new forum topic");
		logger.strongStep("INFO: Entering title of new forum topic");
		this.driver.getSingleElement(ForumsUIConstants.Start_A_Topic_InputText_Title).type(topic.getTitle());

		// Copy an image from an image url and copy it in clipboard
		log.info("INFO: Copy an image from an image url and copy it in clipboard");
		logger.strongStep("INFO: Copy an image from an image url and copy it in clipboard");
		String expectedImageUrl = ui.loadUrlInNewtAndCopyImage();

		log.info("INFO: Click in ckEditor");
		logger.strongStep("INFO: Click in ckEditor");
		ui.clickInCkEditor();

		log.info("INFO: Paste an image in ckEditor");
		logger.strongStep("INFO: Paste an image in ckEditor");
		ui.pasteFromClipboard();

		// Select Save
		log.info("INFO: Save new topic");
		logger.strongStep("INFO: Save new topic");
		ui.scrolltoViewElement(
				(WebElement) (ui.getFirstVisibleElement(ForumsUIConstants.Save_Forum_Topic_Button)).getBackingObject(),
				(WebDriver) driver.getBackingObject());
		ui.clickLink(ForumsUIConstants.Save_Forum_Topic_Button);

		String actualImageUrl = driver.getFirstElement(ForumsUIConstants.forumTopicImage).getAttribute("src");

		log.info("INFO: Verify saved image");
		logger.strongStep("INFO: Verify saved image");
		Assert.assertEquals(actualImageUrl, expectedImageUrl);

		log.info("INFO: Delete community created with API");
		logger.strongStep("INFO: Delete community created with API");
		apiOwner.deleteCommunity(apiOwner.getCommunity(community.getCommunityUUID()));

		ui.endTest();
	}
	
}
