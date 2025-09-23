package com.ibm.conn.auto.tests.communities;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Com_Action_Menu;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.PdfExportUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_ExportToPdf_Communities extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_ExportToPdf_Communities.class);
	private CommunitiesUI ui;
	private PdfExportUI pUi;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private User testUser1, testUser2;
	private Member member;
	private String serverURL;
	private List<Community> testCommunities = new ArrayList<Community>();
	private APICommunityBlogsHandler communityBlogsAPIUser1,communityBlogsAPIUser2;
	private FilesUI filesUI;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		member = new Member(CommunityRole.MEMBERS, testUser2);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
		filesUI = FilesUI.getGui(cfg.getProductName(),driver);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() {
		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		pUi = PdfExportUI.getGui(cfg.getProductName(), driver);
		communityBlogsAPIUser1 = new APICommunityBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communityBlogsAPIUser2 = new APICommunityBlogsHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Acceptance test to verify export a community as pdf.</li>
	*<li><B>Step:</B>(API) Create a community as UserA.</li>
	*<li><B>Step:</B>(API) Add the Wikis widget to community.</li>
	*<li><B>Step:</B>Go to the community wiki page and click the Export as PDF button.</li>
	*<li><B>Step:</B>Click the Generate PDF button in the dialog.  Wait for the progress bar to complete.</li>
	*<li><B>Verify:</B>PDF sidebar contains entries for community wiki name, TOC and welcome page name</li>
	*<li><B>Verify:</B>PDF content contains community wiki name, TOC and welcome page name</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*<li><B>Step:</B>Log in as the community member and repeat the same test.</li>
	*</ul>
	 */
	@Test(groups = { "UnitOnAnsible", "PdfExport" })
	public void smokeTestExportCommunityToPdfDefault() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		BaseCommunity community = null;
		Community testComm = null; 
		community = createCommunityWithWikiWidget(community, testComm, logger, testName);
		String communityTitle = community.getName();
		
		// call common smoketest method
		logger.strongStep("Verify PDF export as the community owner.");
		pUi.smokeTest(pUi, logger, communityTitle, "Welcome to " + communityTitle, true);
		
		logger.strongStep("Log out then log in as the community member.");
		log.info("INFO: Log out then log in as the community member " +  member.getUser().getDisplayName());		
		ui.logout();
		
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(member.getUser());
		
		logger.strongStep("Open the community and go to the Wiki page.");
		log.info("INFO: Open the community " + communityTitle + " and go to the Wiki page.");
		ui.navViaUUID(community);
		Community_TabbedNav_Menu.WIKI.select(ui, 2);
		
		// call common smoketest method
		logger.strongStep("Verify PDF export as a community member.");
		pUi.smokeTest(pUi, logger, communityTitle, "Welcome to " + communityTitle, true);
				
		pUi.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Acceptance test to verify disable export PDF access.</li>
	*<li><B>Step:</B>(API) Create a community as UserA.</li>
	*<li><B>Step:</B>(API) Add the Wikis widget to community.</li>
	*<li><B>Step:</B>Go to the community wiki page and click the Edit community button.</li>
	*<li><B>Step:</B>Click tab 'PDF Export Access'.</li>
	*<li><B>Verify:</B>PDF Export Access dialog opens with 2 options - 1. Allowed 2. Not Allowed</li>
	*<li><B>Step:</B>Click on 'Not Allowed' option and Save and Close the dialog.</li>
	*<li><B>Step:</B>Click on Save and Close button on Edit Community page</li>
	*<li><B>Verify:</B>PDF export option is displayed for community owner.</li>
	*<li><B>Step:</B>Log in as community member. </li>
	*<li><B>Verify:</B>PDF export option is disappeared for community member.</li>
	*</ul>
	 */
	@Test(groups = { "UnitOnAnsible", "PdfExport" })
	public void smokeTestDisableExportCommunityToPdf() {
		
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		
		BaseCommunity community = null;
		Community testComm = null; 
		community = createCommunityWithWikiWidget(community, testComm, logger, testName);
		String communityTitle = community.getName();
		
		// call pdfExportAccess method to click on 'Not Allow' option on PDF export access form
		pdfExportAccess(logger, "Not Allowed");
		logger.strongStep("Go to the Wiki page.");
		log.info("INFO: Go to the Wiki page.");
		Community_LeftNav_Menu.WIKI.select(ui);
		
		logger.strongStep("Verify PDF export option is visible for community owner.");
		Assert.assertTrue(ui.isElementPresent(PdfExportUI.pdfExportBtn), 
				"ERROR: PDF Export option is disappeared for community owner");

		logger.strongStep("Log out then log in as the community member.");
		log.info("INFO: Log out then log in as the community member " +  member.getUser().getDisplayName());		
		ui.logout();
		
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(member.getUser());
		
		logger.strongStep("Open the community and go to the Wiki page.");
		log.info("INFO: Open the community " + communityTitle + " and go to the Wiki page.");
		ui.navViaUUID(community);
		Community_TabbedNav_Menu.WIKI.select(ui, 2);
		
		logger.strongStep("Verify members ( " +  member.getUser().getDisplayName() +" ) are not allowed to generate and export PDF documents of Community content.");
		log.info("INFO: Click the members ( " +  member.getUser().getDisplayName() +" ) are not allowed to generate and export PDF documents of Community content.");
		
		driver.changeImplicitWaits(5);
		Assert.assertTrue(!(ui.isElementPresent(PdfExportUI.pdfExportBtn)), 
				"ERROR: PDF Export option is available for community member");
		
		pUi.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify disable export PDF access.</li>
	*<li><B>Step:</B>(API) Create a community and Entry1 for this community via UserA.</li>
	*<li><B>Step:</B>Login to Connection via UserA and open the Blogs widget of community created above.</li>
	*<li><B>Step:</B>Click tab 'PDF Export Access'.</li>
	*<li><B>Step:</B>Select all options from Included Information Section and Click Generate PDF.</li>
	*<li><B>Verify:</B>In Preview of PDF Entry1 name and UserA name should be printed</li>
	*<li><B>Step:</B>Add New UserB as Member to this Community and Logout.</li>
	*<li><B>Step:</B>(API) Create a Entry2 under same community via UserB.</li>
	*<li><B>Step:</B>Login via UserB and open the Blogs widget of community created above.</li>
	*<li><B>Step:</B>Click tab 'PDF Export Access'.</li>
	*<li><B>Step:</B>Select all options from Included Information Section and Click Generate PDF.</li>
	*<li><B>Verify:</B>In Preview of PDF Entry1 name and UserA name should be printed</li>
	*<li><B>Verify:</B>In Preview of PDF Entry2 name and UserB name should be printed</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*<li><B>Step:</B>Logout from Application.</li>
	*</ul>
	 */
	@Test(groups = { "PdfExport", "RegressionOnAnsible"  })
	public void randerPDFMultiUser() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		String Entry1="BlogEntry1_"  + Helper.genDateBasedRand();
		String Entry2="BlogEntry2_"  + Helper.genDateBasedRand();
		Member newMember1=null;
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
				.access(BaseCommunity.Access.PUBLIC)
				.shareOutside(false)
				.build();
		
		//Create a blog base state object
		BaseBlogPost blogEntry = new BaseBlogPost.Builder(Entry1)
		 			.tags("btag1")
		 			.content("Test description for Entry1 of testcase " + testName)
		 			.build();
		
		//Create a blog base state object
		BaseBlogPost blogEntry2 = new BaseBlogPost.Builder(Entry2)
		 			.tags("btag1")
		 			.content("Test description of Entry2 for testcase " + testName)
		 			.build();
		
		//Create a community via API.
		logger.strongStep("Create a community (API)");
		log.info("INFO: Create a community (API) as " + testUser1.getDisplayName());
		Community testComm = community.createAPI(apiOwner);
		communityBlogsAPIUser1.createBlogEntry(testComm,blogEntry);
		
		community.getCommunityUUID_API(apiOwner, testComm);
		testCommunities.add(testComm);
		logger.strongStep("Log in to Communities");
		log.info("Load Communities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Open the community");
		log.info("INFO: Open the community " + testComm.getTitle());
		ui.navViaUUID(community);
		ui.waitForCommunityLoaded();
		
		logger.strongStep("Select Blogs from the navigation menu");
		log.info("INFO: Select Blogs from the navigation menu");
		//Select the Blogs Widget of the Created Community
		Community_TabbedNav_Menu.BLOG.select(ui, 2);
		
		String userid1 = pUi.getFirstVisibleElement("xpath=//td[@class='lotusAlignLeft']/span/a[text()='"+testUser1.getDisplayName()+"']").getAttribute("href_bc_");
		
		//Validate the User1 is present as Author in the Export PDF of this Entry
		pUi.validateInformationIncludeSectionList(pUi, logger);
		
		pUi.SelectInformationIncludeSection(testName);
		
		pUi.clickGeneratePdfWaitToFinish();
		pUi.checkLineExistsOnPage(3,Entry1);
		pUi.checkLocatorExistsOnPage(1, "xpath=//a[@title='"+userid1+"']");
		pUi.checkLocatorExistsOnPage(3, "xpath=//a[@title='"+userid1+"']");
		
		logger.strongStep("Close the PDF Export dialog.");
		log.info("INFO: Close the PDF Export dialog.");
		pUi.clickLinkWait(PdfExportUI.pdfExportDlgClose);
		pUi.waitForExportDialogDisappear();
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		ui.waitForPageLoaded(driver);

		//Add a new Member in the Community
		Community_LeftNav_Menu.MEMBERS.select(ui);
		newMember1 = new Member(CommunityRole.MEMBERS, testUser2);
		logger.strongStep("Added User " + testUser2.getDisplayName() + " as Member to the Community");
		log.info("INFO: Added User " + testUser2.getDisplayName() + " as Member to the Community");	
		try {
			ui.addMemberCommunity(newMember1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ui.clickSaveAddMember();
		logger.strongStep("Log out then log in as the community member.");
		log.info("INFO: Log out then log in as the community member " +  member.getUser().getDisplayName());		
		ui.logout();
		
		communityBlogsAPIUser2.createBlogEntry(testComm,blogEntry2);
		
		//Login with member user to post entry, make sure the membership is draft user
		logger.strongStep("Open Communities and login: " +testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		//Login with other User
		log.info("Log In as " + testUser2.getDisplayName());
		ui.login(testUser2);
				
		logger.strongStep("Open the community");
		log.info("INFO: Open the community " + testComm.getTitle());
		ui.navViaUUID(community);
		
		logger.strongStep("Select Wiki from the navigation menu");
		log.info("INFO: Select Wiki from the navigation menu");
		//Select the Blogs Widget of the Created Community
		Community_TabbedNav_Menu.BLOG.select(ui, 2);
		
		String userid2 = pUi.getFirstVisibleElement("xpath=//td[@class='lotusAlignLeft']/span/a[text()='"+testUser1.getDisplayName()+"']").getAttribute("href_bc_");
		
		pUi.validateInformationIncludeSectionList(pUi, logger);
		pUi.SelectInformationIncludeSection(testName);
		pUi.clickGeneratePdfWaitToFinish();
		
		//Validate the User1 and User2 is present as Author in the Export PDF of this Entry
		pUi.checkLineExistsOnPage(3,Entry1);
		pUi.checkLineExistsOnPage(4,Entry2);
		pUi.checkLocatorExistsOnPage(1, "xpath=//a[@title='"+userid1+"']");
		pUi.checkLocatorExistsOnPage(1, "xpath=//a[@title='"+userid2+"']");
		logger.strongStep("Close the PDF Export dialog.");
		log.info("INFO: Close the PDF Export dialog.");
		pUi.clickLinkWait(PdfExportUI.pdfExportDlgClose);
		
		pUi.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Regression test to verify Image Upload Function in Communities Export PDF.</li>
	*<li><B>Step:</B>(API) Create a community and Blogs's Entry for this community via UserA.</li>
	*<li><B>Step:</B>Login to Connection via UserA and open the Blogs:Entry of community created above.</li>
	*<li><B>Step:</B>Click the Entry and click Edit entry option.</li>
	*<li><B>Step:</B>Upload a Image from Local in ckEditor and Post the Entry.</li>
	*<li><B>Step:</B>Click on tab 'PDF Export Access'.</li>
	*<li><B>Step:</B>Click Generate PDF.. Button</li>
	*<li><B>Verify:</B>In Preview of PDF Entry name should be printed</li>
	*<li><B>Verify:</B>In Preview of PDF Uploaded Image should be printed</li>
	*<li><B>Step:</B>Close the export PDF dialog.</li>
	*<li><B>Verify:</B>Export PDF dialog disappears.</li>
	*<li><B>Step:</B>Logout from Application.</li>
	*</ul>
	 */
	@Test(groups = { "PdfExport", "RegressionOnAnsible" })
	public void ExportPDFUploadImage() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = pUi.startTest();
		String Entry="BlogEntry_"  + Helper.genDateBasedRand();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
				.access(BaseCommunity.Access.PUBLIC)
				.shareOutside(false)
				.build();
		
		//Create a blog base state object
		BaseBlogPost blogEntry = new BaseBlogPost.Builder(Entry)
		 			.tags("btag")
		 			.content("Test description for Entry of testcase " + testName)
		 			.build();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
				.rename(Helper.genStrongRand())
				.extension(".jpg")
				.build();
		
		//Create a community and Blog's Entry via API.
		logger.strongStep("Create a community (API)");
		log.info("INFO: Create a community (API) as " + testUser1.getDisplayName());
		Community testComm = community.createAPI(apiOwner);
		communityBlogsAPIUser1.createBlogEntry(testComm,blogEntry);
		
		community.getCommunityUUID_API(apiOwner, testComm);
		testCommunities.add(testComm);
		logger.strongStep("Log in to Communities");
		log.info("Load Communities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Open the community");
		log.info("INFO: Open the community " + testComm.getTitle());
		ui.navViaUUID(community);
		
		//Select the Blogs Widget of the Created Community
		logger.strongStep("Select Blogs from the navigation menu");
		log.info("INFO: Select Blogs from the navigation menu");
		Community_LeftNav_Menu.BLOG.select(ui);
		
		logger.strongStep("Open the blog entry");
		log.info("INFO: Open blog " + blogEntry.getTitle());
		ui.clickLinkWait("link=" + blogEntry.getTitle());
		
		//Edit the Created Blog's Entry.
		logger.strongStep("INFO: Click on Edit Blog's Entry");
		log.info("INFO: INFO: Click on Edit Blog's Entry");
		ui.fluentWaitElementVisible(BlogsUIConstants.BlogsEditEntry);
		ui.clickLinkWait(BlogsUIConstants.BlogsEditEntry);

		log.info("INFO: Upload the Image in the ckEditor from Local");
		logger.strongStep("INFO: Upload the Image in the ckEditor from Local");
		ui.clearCkEditor();
		ui.fluentWaitPresent(BlogsUIConstants.BlogsCKEInsertImageButton);
		ui.clickLinkWithJavascript(BlogsUIConstants.BlogsCKEInsertImageButton);
		log.info("INFO: Upload file from Local");
		try {
			filesUI.setLocalFileDetector();
			filesUI.fileToUpload(file.getName(), BaseUIConstants.FileInputField2);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
						
		//Click on 'Upload Image' button
		log.info("INFO:Click on 'Upload Image' button");
		ui.clickButton("Upload Image");
					
		//Edit the Created Blog's Entry.
		logger.strongStep("INFO: Click on Post Blog's Entry");
		log.info("INFO: INFO: Click on Post Blog's Entry");
		ui.fluentWaitElementVisible(BlogsUIConstants.BlogsNewEntryPost);
		ui.clickLinkWait(BlogsUIConstants.BlogsNewEntryPost);
		
		//Export PDF validations
		pUi.validateInformationIncludeSectionList(pUi, logger);
		
		pUi.clickGeneratePdfWaitToFinish();
		Assert.assertTrue(pUi.checkLineExistsonPDFPage(3,blogEntry.getTitle()),"Community Blog's Entry is Not Available in the Exported PDF Preview.");
		pUi.checkLocatorExistsOnPage(3, "xpath=//section/a[contains(@title,'"+file.getName()+"')]");
		
		logger.strongStep("Close the PDF Export dialog.");
		log.info("INFO: Close the PDF Export dialog.");
		pUi.clickLinkWait(PdfExportUI.pdfExportDlgClose);
		pUi.waitForExportDialogDisappear();
				
		pUi.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Data population for PDF export test</B></li>
	 *<li><B>testUser1 creates a community (API) and adds a Wiki widget to the Community (API)</B></li>
	 *<li><B>Log in to Communities and Open the community</B></li>
	 *<li><B>Select Wiki from the navigation menu</B></li>
	 *</ul>
	 */
	private BaseCommunity createCommunityWithWikiWidget(BaseCommunity community, Community testComm, DefectLogger logger, String testName) {

		
		community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
				.access(BaseCommunity.Access.RESTRICTED)
				.shareOutside(false)
				.addMember(member)
				.build();
		
		logger.strongStep("Create a community (API)");
		log.info("INFO: Create a community (API) as " + testUser1.getDisplayName());
		testComm = community.createAPI(apiOwner);
		community.getCommunityUUID_API(apiOwner, testComm);
		testCommunities.add(testComm);
		
		logger.strongStep("Add the Wiki widget to the Community (API)");
		if(!apiOwner.hasWidget(testComm, BaseWidget.WIKI)) {
			log.info("INFO: Add the Wiki widget to the Community (API)");
			community.addWidgetAPI(testComm, apiOwner, BaseWidget.WIKI);
		}
		
		logger.strongStep("Log in to Communities");
		log.info("Load Communities and Log In as " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Open the community");
		log.info("INFO: Open the community " + testComm.getTitle());
		ui.navViaUUID(community);
		
		logger.strongStep("Select Wiki from the navigation menu");
		log.info("INFO: Select Wiki from the navigation menu");
		Community_TabbedNav_Menu.WIKI.select(ui, 2);
		return community;
		
	}
	
	/**
	 *<ul>
	 *<li><B> Enable/Disable PDF export access. Depending on this setting community members are allowed/not allowed to generate and export PDF documents of Community content.</B></li>
	 *<li><B> testUser1 open the Community Actions menu & click on 'Edit Community' option </B></li>
	 *<li><B> Click on PDF Export Access 'Not Allowed' option and Save and Close the form </B></li>
	 *</ul>
	 */
	public void pdfExportAccess(DefectLogger logger, String accessOption) {
		logger.strongStep("Open the Community Actions menu & click on 'Edit Community' option");
		log.info("INFO: Open the Community Actions menu & click on 'Edit Community' option");
		Com_Action_Menu.EDIT.select(ui);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify the PDF Export Access option is available on the edit community form");
		log.info("INFO: Verify the PDF Export Access option is available on the edit community form");
		ui.fluentWaitElementVisible(CommunitiesUIConstants.pdfExportAccessLinkHolder);
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.pdfExportAccessLinkHolder),
				"ERROR: PDF Export Access option label is not shown on Comunity Edit page");
		
		logger.strongStep("Click on PDF Export Access link");
		log.info("INFO: Click on PDF Export Access link");
		ui.clickLinkWait(CommunitiesUIConstants.pdfExportAccessLinkHolder);
		
		logger.strongStep("Verify the PDF Export Access form is displayed on the screen");
		log.info("INFO: Verify the PDF Export Access form is displayed on the screen");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.pdfExportAccessDialogFrame),
				"ERROR: PDF Export Access form is not shown on Comunity Edit page");
		
		ui.switchToFrameBySelector(CommunitiesUIConstants.pdfExportAccessDialogFrame);
		
		logger.strongStep("Verify the PDF Export Access form with 2 options Allowed and Not Allowed is displayed on the screen");
		log.info("INFO: Verify the PDF Export Access form with 2 options Allowed and Not Allowed is displayed on the screen");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.pdfExportAccessDialogOptionAllowed)
				&& (driver.isElementPresent(CommunitiesUIConstants.pdfExportAccessDialogOptionNotAllowed)),
				"ERROR: PDF Export Access form with 2 options Allowed and Not Allowed is not shown on Comunity Edit page");
		
		logger.strongStep("Click on PDF Export Access " + accessOption + " option and Save and Close the form");
		log.info("INFO: Click on PDF Export Access " + accessOption + " option and Save and Close the form");
		if(accessOption=="Allowed"){
			ui.clickLinkWait(CommunitiesUIConstants.pdfExportAccessDialogOptionAllowed);
		}
		else{
			ui.clickLinkWait(CommunitiesUIConstants.pdfExportAccessDialogOptionNotAllowed);
		}
		ui.fluentWaitElementVisible(CommunitiesUIConstants.pdfExportAccessDialogSaveAndClose);
		ui.clickLinkWait(CommunitiesUIConstants.pdfExportAccessDialogSaveAndClose);
		ui.switchToTopFrame();
	}

	@AfterClass(alwaysRun=true)
	public void cleanUp()  {
		for (Community community : testCommunities)  {
			apiOwner.deleteCommunity(community);
		}
	}
	
	/**
	    *<ul>
	    *<li><B>Info:</B>Regression test to verify Table Content in Export PDF.</li>
	    *<li><B>Step:</B>Create a community and enter a Blog entry (API)</li>
	    *<li><B>Step:</B>Log in to Communities</li>
	    *<li><B>Step:</B>Open the community</li>
	    *<li><B>Step:</B>Select Blog from the navigation menu</li>
	    *<li><B>Step:</B>Open the Blog entry</li>
	    *<li><B>Step:</B>Click on Edit Blog's Entry</li>
	    *<li><B>Step:</B>Enter a table in the ckEditor from Local</li>
	    *<li><B>Verify:</B>Validate the Table Content</li>
	    *<li><B>Step:</B>Close the PDF Export dialog.</li>
	    *</ul>
	     */
	    @Test(groups = { "PdfExport","RegressionOnAnsible" })
	    public void ExportPdfVerifyTable() {
	        DefectLogger logger = dlog.get(Thread.currentThread().getId());
	        String testName = pUi.startTest();
	        String Entry="BlogEntry_"  + Helper.genDateBasedRand();
	        
	        BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
	                .access(BaseCommunity.Access.PUBLIC)
	                .shareOutside(false)
	                .build();
	        
	        //Create a blog base state object
	        BaseBlogPost blogEntry = new BaseBlogPost.Builder(Entry)
	                     .tags("btag")
	                     .content("Test description for Entry of testcase " + testName)
	                     .build();
	        
	        //Create a community via API.
	        logger.strongStep("Create a community and enter a Blog entry (API)");
	        log.info("INFO: Create a community (API) as " + testUser1.getDisplayName());
	        Community testComm = community.createAPI(apiOwner);
	        communityBlogsAPIUser1.createBlogEntry(testComm,blogEntry);
	        
	        community.getCommunityUUID_API(apiOwner, testComm);
	        testCommunities.add(testComm);
	        logger.strongStep("Log in to Communities");
	        log.info("Load Communities and Log In as " + testUser1.getDisplayName());
	        ui.loadComponent(Data.getData().ComponentCommunities);
	        ui.login(testUser1);
	        
	        logger.strongStep("Open the community");
	        log.info("INFO: Open the community " + testComm.getTitle());
	        ui.navViaUUID(community);
	        
	        //Select the Blogs Widget of the Created Community
	        logger.strongStep("Select Blogs from the navigation menu");
	        log.info("INFO: Select Blogs from the navigation menu");
	        Community_LeftNav_Menu.BLOG.select(ui);
	        
	        logger.strongStep("Open the blog entry");
	        log.info("INFO: Open blog " + blogEntry.getTitle());
	        ui.clickLinkWait("link=" + blogEntry.getTitle());
	        
	        //Edit the Created Blog's Entry.
	        logger.strongStep("INFO: Click on Edit Blog's Entry");
	        log.info("INFO: INFO: Click on Edit Blog's Entry");
	        ui.fluentWaitElementVisible(BlogsUIConstants.BlogsEditEntry);
	        ui.clickLinkWait(BlogsUIConstants.BlogsEditEntry);
	       
	        logger.strongStep("INFO: Enter a table in the ckEditor from Local");
	        log.info("INFO: Enter a table in the ckEditor from Local");
	        ui.clearCkEditor();
	        ui.fluentWaitPresent(BlogsUIConstants.BlogsCKEInsertTableButton);
	        ui.clickLinkWithJavascript(BlogsUIConstants.BlogsCKEInsertTableButton);
	        ui.clickLinkWithJavascript(BlogsUIConstants.BlogsCKEClickOkButton);
	        for(int i=1; i<=6; i++)
	        {
		        driver.typeNative("Entry " +i);
		        driver.typeNative(Keys.TAB);
	        }
	        ui.clickLinkWithJavascript(BlogsUIConstants.BlogsNewEntryPost);
	       
	        pUi.validateInformationIncludeSectionList(pUi, logger);
	        
	        pUi.clickGeneratePdfWaitToFinish();
	        pUi.checkLineExistsOnPage(3,blogEntry.getTitle());
	        
	        logger.strongStep("INFO: Validate the Table Content");
	        log.info("INFO: Validate the Table Content");
	        String inputEntry[] = {"Entry 1","Entry 2", "Entry 3", "Entry 4", "Entry 5", "Entry 6"};
	        pUi.checkLineExistsOnPage(3,inputEntry);
	        
	        logger.strongStep("Close the PDF Export dialog.");
	        log.info("INFO: Close the PDF Export dialog.");
	        pUi.clickLinkWait(PdfExportUI.pdfExportDlgClose);
	        pUi.waitForExportDialogDisappear();
	                
	        pUi.endTest();
	    }
}

