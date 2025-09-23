package com.ibm.conn.auto.tests.wikis;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki.EditAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki.ReadAccess;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.WikiBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.wikis.WikiEvents;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;

public class BVT_BoschUAT_Wikis extends  SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_BoschUAT_Wikis.class);
	private WikisUI ui;
	private TestConfigCustom cfg;
	private String serverURL;
	private User testUserA,testUserB;
	private  APIWikisHandler wikisAPIUser;
	private Wiki publicWiki;
	private BaseWiki  basePublicWiki;

	
	
	@BeforeClass(alwaysRun=true)
	public void SetUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUserA = cfg.getUserAllocator().getUser(this);
		testUserB = cfg.getUserAllocator().getUser(this);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		wikisAPIUser = new APIWikisHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		// testUserA create a public wiki
		basePublicWiki = WikiBaseBuilder.buildBaseWiki(getClass().getSimpleName() + Helper.genStrongRand(), EditAccess.AllLoggedIn, ReadAccess.All);
		publicWiki = WikiEvents.createWiki(basePublicWiki, testUserA, wikisAPIUser);
		
	
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = WikisUI.getGui(cfg.getProductName(), driver);
	}
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify HTML editor in wiki page</li>
	 *<li><B>Step:</B> [API] Create Wiki Page using API</li>
	 *<li><B>Step:</B> Login to Wikis component</li>
	 *<li><B>Step:</B> Select and edit Wiki page</li>
	 *<li><B>Step:</B> Select HTML source tab</li>
	 *<li><B>Step:</B> Enter a HTML tag with text</li>
	 *<li><B>Step:</B> Switch to Rich Text tab</li>
	 *<li><B>Verify:</B> Verify that entered text should be displayed correctly in Rich Text editor</li>
	 *<li><B>Step:</B> Switch back to HTML source tab</li>
	 *<li><B>Verify:</B>Verify that entered text should be displayed correctly in HTML editor</li>
	 *</ul>
	 */
	@Test(groups = { "regression" })
	public void verifyHTMLEditorInWikis() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String HTMLText = "<p dir =\"ltr\">This is test</p>";
		String RichText = "This is test";
		String testName = ui.startTest();
		
		// Create Wiki Page using API
		logger.strongStep("Create Wiki Page using API");
		log.info("Create Wiki Page using API");
		BaseWikiPage baseWikiPage = WikiBaseBuilder.buildBaseWikiPage(testName + Helper.genStrongRand());
		WikiEvents.createWikiPage(publicWiki, baseWikiPage, testUserA, wikisAPIUser);
		
		// Login to Wikis component
		logger.strongStep("Login to Wikis component");
		log.info("Login to Wikis component");
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUserA);

		// Select and edit Wiki page
		logger.strongStep("Select and edit Wiki page");
		log.info("Select and edit Wiki page");
		ui.clickLinkWait(WikisUI.getWiki(basePublicWiki));
		ui.clickLinkWait(WikisUIConstants.Edit_Button);
		
		// Select HTML Source tab
		logger.strongStep("Select HTML source tab");
		log.info("INFO: Select HTML source tab");
		ui.clickLinkWait(WikisUIConstants.HTML_Source_Tab);
		
		// Enter HTML tag with text
		logger.strongStep("Enter a HTML tag with text");
		log.info("INFO: Enter a HTML tag with text: " + HTMLText);
		driver.getSingleElement(WikisUIConstants.HTMLTextArea).clear();
		ui.typeText(WikisUIConstants.HTMLTextArea, HTMLText);
		
		// Switch to Rich Text tab
		logger.strongStep("Switch to Rich Text tab");
		log.info("INFO: Switch to Rich Text tab");
		ui.clickLinkWait(WikisUIConstants.Rich_Text_Tab);
		
		// Verify entered text in Rich Text Tab
		logger.strongStep("Verify that entered text should be displayed correctly in Rich Text editor");
		log.info("INFO: Verify that entered text should be displayed correctly in Rich Text editor: " + RichText);
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		log.info(driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).getText());
		Assert.assertEquals(driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).getText(),"This is test");
		
		// Switch back to HTML source tab
		logger.strongStep("Switch back to HTML source tab");
		log.info("INFO: Switch back to HTML source tab");
		ui.switchToTopFrame();
		ui.clickLinkWait(WikisUIConstants.HTML_Source_Tab);
		
		// Verify entered text in HTML Source Tab
		logger.strongStep("Verify that entered text should be displayed correctly in HTML editor");
		log.info("INFO: Verify that entered text should be displayed correctly in HTML editor: " + HTMLText);
		Assert.assertEquals(driver.getSingleElement(WikisUIConstants.HTMLTextArea).getAttribute("value").trim(),"<p dir=\"ltr\">This is test</p>");
		
		wikisAPIUser.deleteWiki(publicWiki);
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Wikis Tags View on Cloud and List</li>
	*<li><B>Step: </B>Load component and login</li>
	*<li><B>Step: </B>Create a new Forum</li>
	*<li><B>Step: </B>Add a new tag to the Wiki and validate that the tag was added</li>
	*<li><B>Verify: </B>View the added Tag in Cloud section</li>
	*<li><B>Verify: </B>View the added Tag in List section</li>
	*</ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression"})
	public void wikisTagsonCloudandListView() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		//Allocate user
		User testUserA = cfg.getUserAllocator().getUser();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand()).description("Description for test " + testName).build();
		
		//Load the component and login
		logger.strongStep("Load Wikis and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUserA);

		//create a new wiki
		logger.strongStep("Create a new Wiki");
		log.info("INFO: Create a new Wiki");
		wiki.create(ui);

		// Add new tag to page and verify that the tag is added
		logger.weakStep("Add a new tag to the Wiki and validate that the tag was added");
		log.info("INFO: Add a new tag to the Wiki and validate that the tag was added");
		ui.addWikiTag(Data.getData().Tag_For_Public_Wiki);

		logger.strongStep("View the added Tag in Cloud section");
		log.info("INFO: View the added Tag in Cloud section");
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.cloudTagView));
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(wiki.getTags()),"ERROR: Added Tag is not dispalyed");
		
		logger.strongStep("View the added Tag in List section");
		log.info("INFO: View the added Tag in List section");
		ui.clickLink(BaseUIConstants.listTagView);
		Assert.assertTrue(ui.fluentWaitTextNotPresentWithoutRefresh(wiki.getTags()),"ERROR: Added Tag is not dispalyed");

		//Logout of Wiki
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify the Mention comments in Wiki</li>
	 *<li><B>Step:</B> Login to application and Create a Activity</li> 
	 *<li><B>Step:</B> Create Wiki Page</li>
	 *<li><B>Step:</B> Add a comment with Mentions to the created Wiki and Save the Comment</li>
	 *<li><B>Verify:</B> Verify the Comment with Mentions is displayed</li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void verifyCreateCommentsWithMentionsInWiki() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();

		BaseWiki wiki = new BaseWiki.Builder(testName + Helper.genDateBasedRand())
				.tags("tag" + Helper.genDateBasedRand()).description("Description for test " + testName).build();
		
		//Load the component and login
		logger.strongStep("INFO: Load Wikis and Log In as: " + testUserA.getDisplayName());
		log.info("INFO: Load Wikis and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentWikis);
		ui.login(testUserA);

		logger.strongStep("INFO: Create a new Wiki");
		log.info("INFO: Create a new Wiki");
		wiki.create(ui);
		
		logger.strongStep("INFO: Click on Add Coment link in Wiki page");
		log.info("INFO: Click on Add Coment link in Wiki page");
		ui.clickLinkWait(WikisUIConstants.Add_Comment_Link);
	
		logger.strongStep("INFO: Fill in the comment form");
		log.info("INFO: Fill in the comment form");
		ui.typeMentionInCkEditor("Hello @"+ testUserB.getDisplayName());
		
		logger.strongStep("INFO: Click on Save button");
		log.info("INFO: Click on Save button");
		ui.clickLinkWait(BaseUIConstants.SaveButton);
		
		logger.strongStep("INFO: Verify that the mention comment is present");
		log.info("INFO: Verify that the mention comment exists");
		Assert.assertTrue(ui.fluentWaitTextPresent("Hello @"+testUserB.getDisplayName()),
						  "ERROR: Comment not found");
		
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.mentionLink.replace("PLACEHOLDER", "@"+testUserB.getDisplayName())),
				  "ERROR: Mention link not present");

		ui.endTest();	
		
	}
}
