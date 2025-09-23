package com.ibm.conn.auto.tests.communities;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
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
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_BoschUAT_Communities extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(BVT_BoschUAT_Communities.class);
	private CommunitiesUI ui;
	private FilesUI filesUi;
	private TestConfigCustom cfg;	
	private User testUser, testLookAheadUser,testUserA,searchAdmin;
	private Member member;
	private String serverURL;
	private  APICommunitiesHandler comApiOwner; 
	private BaseCommunity.Access defaultAccess;
	private SearchAdminService adminService;
	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();
		// Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUserA = cfg.getUserAllocator().getUser();
		testLookAheadUser = cfg.getUserAllocator().getUser();
		member = new Member(CommunityRole.MEMBERS, testLookAheadUser);
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		comApiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		adminService = new SearchAdminService();
		searchAdmin = cfg.getUserAllocator().getAdminUser();

	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpclass() {
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		filesUi = FilesUI.getGui(cfg.getProductName(), driver);
		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		ui.addOnLoginScript(ui.getCloseTourScript());
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Edit Community: Insert Link to Community Description</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Step:</B>Click on Community Actions Dropdown</li>
	*<li><B>Step:</B>Click on 'Edit Community'link</li>
	*<li><B>Verify:</B>Verify  community 'Name' input field is displayed on Edit Community Form</li>
	*<li><B>Step:</B>Click on  Inserted Link</li>
	*<li><B>Step:</B>Click on  URL Link</li>
	*<li><B>Verify:</B>Verify and Input value for 'URL' and 'Link Text'</li>
	*<li><B>Step:</B>Select 'Open link in new window' check box</li>
	*<li><B>Step:</B>Click 'OK' button</li>
	*<li><B>Step:</B>Click 'Save and Close' button</li>
	*<li><B>Step:</B>Click newly added link on Community Description widget</li>
	*<li><B>Verify:</B>Verify new tab is opened, then Close the new tab and move back to parent window</li>
	*</ul>
	 */
	@Test(groups = {"regression"})
	public void editCommunityInsertLink() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String urlInputValue = "https://www.google.com";
		String linkTextValue = "Google";
		String linkTextLink = "xpath=//p[contains(text(),'Test description for testcase editCommunityInsertL')]//a[contains(text(),'Google')]";

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("Test description for testcase " + testName).addMember(member).build();

		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Create Community
		logger.strongStep("Create a Community");
		log.info("INFO: Create a Community");
		community.createFromDropDown(ui);
		ui.waitForCommunityLoaded();

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			Community communitycom = comApiOwner.getCommunity(community.getCommunityUUID());
			comApiOwner.editStartPage(communitycom, StartPageApi.OVERVIEW);
			ui.loadComponent("communities", true);
			ui.typeText(CommunitiesUIConstants.catalogFilterCardView, community.getName());
			ui.fluentWaitPresentWithRefresh("css=div[aria-label='" + community.getName() + "']");
			ui.clickLinkWait("css=div[aria-label='" + community.getName() + "']");
		}

		logger.strongStep("Click on Community Actions Dropdown");
		log.info("INFO: Click on Community Actions Dropdown");
		ui.clickLink(CommunitiesUIConstants.communityActions);

		logger.strongStep("Click on 'Edit Community'link");
		log.info("INFO: Click on 'Edit Community'link");
		driver.getSingleElement(CommunitiesUIConstants.editCommunity).click();

		logger.strongStep("Verify  community 'Name' input field is displayed on Edit Community Form");
		log.info("INFO: Verify  community 'Name' input field is displayed on Edit Community Form");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.editCommunityNameInputField));

		logger.strongStep("Click on  Inserted Link");
		log.info("INFO: Click on Inserted Link");
		ui.fluentWaitElementVisible(BaseUIConstants.insertLink);
		ui.clickLinkWithJavascript(BaseUIConstants.insertLink);
		
		logger.strongStep("Click on  URL Link");
		log.info("INFO: Click on URL Link");
		ui.switchToFrameBySelector(BaseUIConstants.ckePanelFrame);
		ui.fluentWaitElementVisible(BaseUIConstants.urlLink);
		ui.clickLinkWithJavascript(BaseUIConstants.urlLink);

		logger.strongStep("Verify and Input value for 'URL' and 'Link Text'");
		log.info("INFO: Verify and Input value for 'URL' and 'Link Text'");
		ui.switchToTopFrame();
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.urlInputField));
		ui.typeText(BaseUIConstants.urlInputField, urlInputValue);
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.linkTextInputField));
		ui.typeText(BaseUIConstants.linkTextInputField,linkTextValue );

		logger.strongStep("Select 'Open link in new window' check box");
		log.info("INFO: Select 'Open link in new window' check box");
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.openWindowCheckbox));
		driver.getSingleElement(BaseUIConstants.openWindowCheckbox).click();

		logger.strongStep("Click 'OK' button");
		log.info("INFO: Click 'OK' button");
		ui.clickLinkWait(BaseUIConstants.okButtonURLForm);

		logger.strongStep("Click 'Save and Close' button");
		log.info("INFO: Click 'Save and Close' button");
		ui.clickLinkWait(CommunitiesUIConstants.editCommunitySaveButton);

		logger.strongStep("Click newly added link on Community Description widget");
		log.info("INFO: Click newly added link on Community Description widget");
		driver.executeScript("arguments[0].scrollIntoView(true);",
				driver.getElements(linkTextLink).get(0).getWebElement());
		ui.clickLinkWithJavascript(linkTextLink);

		log.info("INFO: Verify new tab is opened, then Close the new tab and move back to parent window");
		logger.strongStep("Verify new tab is opened, then Close the new tab and move back to parent window");
		Assert.assertTrue(ui.fluentWaitNumberOfWindowsEqual(2));
		ui.closeNewTabAndMoveToParentTab();
		
		ui.endTest();
		
	}


	/**
	*<ul>
	*<li><B>Info:</B>Verify Community is created in which Image for Community Logo is also uploaded</li>
	*<li><B>Step:</B>Navigate to Create community page</li>
	*<li><B>Step:</B>Enter community name</li>
	*<li><B>Step:</B>Upload Image for community logo and click on Save</li>
	*<li><B>Verify:</B>Verify that community is created successfully</li>
	*</ul>
	 */
	@Test(groups = {"regression"})
	public void verifyCreateCommunityWithLogo() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());


		BaseFile file = new BaseFile.Builder(Data.getData().file1)
				.build();


		String testName = ui.startTest();
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.build();

		logger.strongStep("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		logger.strongStep("INFO: Navigate to Create New community Page");
		log.info("INFO: Navigate to Create New community Page");
		ui.clickLinkWait(CommunitiesUIConstants.StartACommunityDropDownCardView);
		ui.clickLinkWait(CommunitiesUIConstants.StartACommunityFromDropDownCardView);
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);

		logger.strongStep("INFO: Enter Communty name value as "+ community.getName());
		log.info("INFO: INFO: Enter Communty name value as "+ community.getName());
		log.info("INFO: Entering community name " + community.getName());
		ui.typeText(CommunitiesUIConstants.CommunityName, community.getName());
		
		logger.strongStep("INFO: Upload image for Community Logo and Click on Save");
		log.info("INFO: Upload image for Community Logo and Click on Save");
		ui.clickLinkWait(CommunitiesUIConstants.uploadImageForCommunityLogo);
		filesUi.setLocalFileDetector();
		driver.getSingleElement(CommunitiesUIConstants.chooseFile).typeFilePath(FilesUI.getFileUploadPath(file.getName(), cfg));
		ui.clickLinkWait(CommunitiesUIConstants.SaveButton);

		logger.strongStep("Verify tat community is created successfully");
		log.info("Verify tat community is created successfully");
		Assert.assertTrue(ui.getElementText(CommunitiesUIConstants.communityName).equalsIgnoreCase(community.getName()));

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify folder creation and file upoad from community's file tab</li>
	 *<li><B>Step:</B> [API] Create community using API where  testUserA(User 2) added as a member </li>
	 *<li><B>Step:</B> User 2 login to homepage</li>
	 *<li><B>Step:</B> Select Communities->My Communities</li>
	 *<li><B>Step:</B> Click on 'Refine this view icon'-> Select I'm member view</li>
	 *<li><B>Verify:</B> Verify that community should be displayed in which user 2 added as a member</li>
	 *<li><B>Step:</B> Select the community name created by user 1 </li>
	 *<li><B>Verify:</B> Verify that community overview page should be displayed</li>
	 *<li><B>Step:</B> Select Files tab on the top nav bar</li>
	 *<li><B>Verify:</B>Verify that 'All Community Files' page should be displayed</li>
	 *<li><B>Step:</B> Upload a file using Add->New Upload option</li>
	 *<li><B>Verify:</B> Verify that the message displays stating the file was successfully uploaded</li>
	 *<li><B>Step:</B> Create a folder using Add->New folder option</li>
	 *<li><B>Verify:</B>Verify that the message displays stating the file was successfully uploaded</li>
	 *<li><B>Step:</B>Select the newly created folder</li>
	 *<li><B>Verify:</B>Verify that folder view is displayed</li>
	 *</ul>
	 */
	@Test(groups = { "regression" ,"mt-exclude"})
	public void communiyFilesAddFolderUploadFile() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUserA);
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(com.ibm.conn.auto.appobjects.base.BaseCommunity.Access.PUBLIC)
				.addMember(member)
				.description("Test description for testcase " + testName).build();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
				 .comFile(true)
				 .rename(Helper.genDateBasedRand())
				 .extension(".jpg")
				 .build();
		
		 BaseFolder folder = new BaseFolder.Builder(Data.getData().FolderName)
					.description(Data.getData().FolderDescription).build();
		
		// Create community using API
		log.info("INFO: Create community using API");
		logger.strongStep(" Create community using API");
		Community comAPI = baseCommunity.createAPI(comApiOwner);
		
		// add the UUID to community
		log.info("INFO: Get UUID of community");
		baseCommunity.getCommunityUUID_API(comApiOwner, comAPI);
		
		// User 2 login to homepage
		log.info("INFO: "+testUserA.getDisplayName()+" login to homepage");
		logger.strongStep("User 2 login to homepage");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUserA);
		
		// Select Communities->My Communities
		log.info("Select Communities->My Communities");
		logger.strongStep("Select Communities->My Communities");
		ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
		ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuMyCommunities);
		ui.clickLinkWait(CommunitiesUIConstants.filterSideBarExpandCardView);

		// Click on 'Refine this view icon'-> Select I'm member view
		log.info("Click on 'Refine this view icon'-> Select I'm member view");
		logger.strongStep("Click on 'Refine this view icon'-> Select I'm member view");
		Community_View_Menu.IM_A_MEMBER.select(ui);
		
		List<Element> communitiesEle = driver.getVisibleElements(CommunitiesUIConstants.ViewCardList);

		// check if created community found in user 2's I'm member view
		boolean found = false;
		for (Element community : communitiesEle) {
			if (community.getAttribute("aria-label").equals(baseCommunity.getName())) {
				found = true;
				break;
			}
		}

		// Verify that community should be displayed in which user 2 added as a member
		log.info("Verify that community should be displayed in which user 2 added as a member");
		logger.strongStep("Verify that community should be displayed in which user 2 added as a member");
		Assert.assertTrue(found, "ERROR: User is not added as a member to community");

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			comApiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		// Select the community name created by user 1
		log.info("INFO: Select the community name "+baseCommunity.getName()+" created by "+testUser.getDisplayName());
		logger.strongStep("Select the community name created by user 1");
		ui.clickLinkWait(CommunitiesUI.getCommunityLinkCardView(baseCommunity));
		ui.waitForPageLoaded(driver);
		
		// Select Files tab on the top nav bar
		log.info("INFO:  Select Files tab on the top nav bar");
		logger.strongStep("Select Files tab on the top nav bar");
		Community_LeftNav_Menu.FILES.select(ui);
		
		// Verify that 'All Community Files' page should be displayed
		log.info("INFO: Verify that 'All Community Files' page should be displayed");
		logger.strongStep("Verify that 'All Community Files' page should be displayed");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.filesFullWidgetPageHeader));

		// Upload a file using Add->New Upload option
		log.info("INFO: Upload a file using Add->New Upload option");
		logger.strongStep(" Upload a file using Add->New Upload option");
		fileA.upload(filesUi);

		// Validate message displays stating file was successfully uploaded
		logger.strongStep("Verify that the message displays stating the file was successfully uploaded");
		log.info("INFO: Verify message displays stating file was successfully uploaded");
		Assert.assertTrue(ui.fluentWaitTextPresent("Successfully uploaded " + fileA.getRename()),
				"ERROR: File was not uploaded");
	   
		// Create a folder using Add->New folder option
		log.info("INFO: Create a folder using Add->New folder option");
		logger.strongStep(" Create a folder using Add->New folder option");
		folder.add(filesUi);
		
		// Verify message folder created successfully 
		logger.strongStep("Verify that the message displays stating the file was successfully uploaded");
		log.info("INFO: Verify message displays stating file was successfully uploaded");
		Assert.assertTrue(ui.fluentWaitTextPresent("Successfully created " + folder.getName() + "."),
				"ERROR: File was not uploaded");

		// Select the newly created folder 
		logger.strongStep("Select the newly created folder");
		log.info("INFO: Select the newly created folder");
		ui.clickLinkWait(CommunitiesUI.selectFolder(folder));
		
		// Verify that folder view is displayed
		logger.strongStep("Verify that folder view is displayed");
		log.info("INFO: Verify that folder view is displayed");
		Assert.assertTrue(driver.getSingleElement(FilesUIConstants.FolderTitleDropdownMenu).getText().equals(folder.getName()));

		// Delete community
		comApiOwner.deleteCommunity(comAPI);
		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify that community created with long name displayed correctly on overview page</li>
	 *<li><B>Step:</B> User login to homepage </li>
	 *<li><B>Step:</B> Go to my communities Communities->My Communities</li>
	 *<li><B>Step:</B> Select 'Create a community' dropdown -> 'Create a new community'</li>
	 *<li><B>Step:</B> Enter a long community name and save</li>
	 *<li><B>Verify:</B> Verify that community name displayed in overview page correctly and without any line breaks</li>
	 *</ul>
	 */
	@Test(groups = { "regression" })
	public void communiyLongName() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		BaseCommunity baseCommunity = new BaseCommunity.Builder("ThisIsALongCommunityNameMoreThanFiftyLetters" + Helper.genDateBasedRandVal())
						.description("Test description for testcase " + testName).build();

		// User login to homepage
		log.info("INFO: " + testUser.getDisplayName() + " login to homepage");
		logger.strongStep("User 1 login to homepage");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser);

		// Go to my communities Communtities->My Communities
		log.info("INFO: Go to my communities Communtities->My Communities");
		logger.strongStep("Go to my communities Communtities->My Communities");
		ui.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);
		ui.clickLinkWait(CommunitiesUIConstants.communitiesMegaMenuMyCommunities);

		// Select 'Create a community' dropdown -> 'Create a new community'
		log.info("INFO: Select 'Create a community' dropdown -> 'Create a new community'");
		logger.strongStep("Select 'Create a community' dropdown -> 'Create a new community'");
		ui.clickLinkWait(CommunitiesUIConstants.StartACommunityDropDown);
		driver.getSingleElement(CommunitiesUIConstants.StartACommunityFromDropDown).click();

		// Enter a long community name, description and save
		logger.strongStep("Enter a long community name and save ");
		log.info("INFO: Entering community name " + baseCommunity.getName() + " and save");
		ui.fluentWaitPresent(CommunitiesUIConstants.CommunityName);
		this.driver.getSingleElement(CommunitiesUIConstants.CommunityName).type(baseCommunity.getName());
		ui.typeInCkEditor(baseCommunity.getDescription());
		Element saveBtn = driver.getSingleElement(CommunitiesUIConstants.SaveButton);
		driver.executeScript("arguments[0].scrollIntoView(true)", saveBtn.getWebElement());
		saveBtn.click();

		// Verify that community name displayed in overview page correctly
		logger.strongStep("Verify that community name displayed in overview page correctly and without any line breaks");
		log.info("INFO: Verify that community name displayed in overview page correctly and without any line breaks");
		ui.waitForPageLoaded(driver);
		log.info("Community name is: " + driver.getSingleElement(CommunitiesUIConstants.tabNavCommunityName).getText());
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.tabNavCommunityName).getText(),baseCommunity.getName());

		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify HTML editor in community Rich content widget</li>
	 *<li><B>Step:</B> User login to homepage </li>
	 *<li><B>Step:</B> Go to my communities Communtities->My Communities</li>
	 *<li><B>Step:</B> Select 'Create a community' dropdown -> 'Create a new community'</li>
	 *<li><B>Step:</B> Enter community name, description and save</li>
	 *<li><B>Step:</B> Click on Rich content widget 'Add Content' link</li>
	 *<li><B>Step:</B> Select HTML source tab</li>
	 *<li><B>Step:</B> Enter a HTML tag with text</li>
	 *<li><B>Step:</B> Switch to Rich Text tab</li>
	 *<li><B>Verify:</B> Verify that entered text should be displayed correctly in Rich Text editor</li>
	 *<li><B>Step:</B> Switch back to HTML source tab</li>
	 *<li><B>Verify:</B>Verify that entered text should be displayed correctly in HTML editor</li>
	 *</ul>
	 */
	
	@Test(groups = { "regression" })
	public void communiyRichContentHTMLEditor() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		String HTMLText = "<p dir =\"ltr\">This is test</p>";
		String RichText = "This is test";
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.description("Test description for testcase " + testName).build();

		logger.strongStep("create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(comApiOwner, comAPI);

		// User login to homepage
		log.info("INFO: " + testUser.getDisplayName() + " login to homepage");
		logger.strongStep("User 1 login to homepage");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			comApiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		logger.strongStep("Naviagate to the Community");
		log.info("INFO: Navigate to the Community using UUID");
		community.navViaUUID(ui);

		// Click on Rich content widget 'Add Content' link
		logger.strongStep("Click on Rich content widget 'Add Content' link");
		log.info("INFO: Click on Rich content widget 'Add Content' link");
		ui.waitForPageLoaded(driver);
		ui.clickLinkWait(CommunitiesUIConstants.rteAddContent);

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
		Assert.assertEquals(driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).getText(), "This is test");

		// Switch back to HTML source tab
		logger.strongStep("Switch back to HTML source tab");
		log.info("INFO: Switch back to HTML source tab");
		ui.switchToTopFrame();
		ui.clickLinkWait(WikisUIConstants.HTML_Source_Tab);

		// Verify entered text in HTML Source Tab
		logger.strongStep("Verify that entered text should be displayed correctly in HTML editor");
		log.info("INFO: Verify that entered text should be displayed correctly in HTML editor: " + HTMLText);
		Assert.assertEquals(driver.getSingleElement(WikisUIConstants.HTMLTextArea).getAttribute("value").trim(),"<p dir=\"ltr\">This is test</p>");

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Verify community search</li>
	 *<li><B>Step:</B> [API] Create community using API</li>
	 *<li><B>Step:</B> Load Communites and log in as test user</li>
	 *<li><B>Step:</B> Select Files tab on the top nav bar</li>
	 *<li><B>Step:</B> Upload a file using Add->New Upload option</li>
	 *<li><B>Step:</B> Click on search icon</li>
	 *<li><B>Verify:</B> Verify that the search text area should be displayed</li>
	 *<li><B>Step:</B> Enter the name of file uploaded to Files widget</li>
	 *<li><B>Step:</B> Click on 'This Community' link</li>
	 *<li><B>Verify:</B> Verify that file added to Files widget should be returned</li>
	 *<li><B>Step:</B> Select 'Back to Overview Page' button</li>
	 *<li><B>Verify:</B> Verify that community overview page should be displayed</li>
	 *<li><B>Step:</B> Click on search icon</li>
	 *<li><B>Step:</B> Enter the tags entered during community creation</li>
	 *<li><B>Step:</B> Click on 'All Content'</li>
	 *<li><B>Verify:</B> Verify the search result returns the communities with matching tag names</li>
	 *</ul>
	 */
	
	@Test(groups = { "regression" })
	public void communitySearch() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		// Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
				.tags(Data.getData().commonTag + testName)
				.access(defaultAccess)
				.description(Data.getData().widgetinsidecommunity + Helper.genStrongRand())
				.build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
				.comFile(true)
				.extension(".jpg")
				.build();

		// create community
		logger.strongStep("Create A Community Via API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(comApiOwner, comAPI);

		// GUI
		// Load component and login
		logger.strongStep("Load Communites and log in as test user");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		// Select Files tab on the top nav bar
		log.info("INFO: Select Files tab on the top nav bar");
		logger.strongStep(" Select Files tab on the top nav bar");
		Community_LeftNav_Menu.FILES.select(ui);
		ui.waitForPageLoaded(driver);

		// Run indexer for communities
		adminService.indexNow("communities", searchAdmin.getAttribute(cfg.getLoginPreference()),searchAdmin.getPassword());

		// Upload a file using Add->New Upload option
		log.info("INFO: Upload a file using Add->New Upload option");
		logger.strongStep(" Upload a file using Add->New Upload option");
		fileA.upload(filesUi);
		ui.fluentWaitTextPresent("Successfully uploaded " + fileA.getName());

		// Run indexer for files
		adminService.indexNow("files", searchAdmin.getAttribute(cfg.getLoginPreference()), searchAdmin.getPassword());

		// Click on search icon
		log.info("INFO: Click on search icon");
		logger.strongStep("Click on search icon");
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(GlobalsearchUI.OpenSearchPanel).get(0).getWebElement());
		ui.clickLinkWait(GlobalsearchUI.OpenSearchPanel);
		
		// Verify that the search text area should be displayed
		log.info("INFO: Verify that the search text area should be displayed");
		logger.strongStep("Verify that the search text area should be displayed");
		Assert.assertTrue(ui.fluentWaitPresent(GlobalsearchUI.TextAreaInPanel));

		// Enter the name of file uploaded to Files widget
		logger.strongStep("Enter the name of file uploaded to Files widget");
		log.info("INFO: Enter the name of file uploaded to Files widget");
		ui.typeText(GlobalsearchUI.TextAreaInPanel, fileA.getName());

		// Click on 'This Community' link
		logger.strongStep("Click on 'This Community' link");
		log.info("INFO: Click on 'This Community' link");
		ui.clickLinkWait(CommunitiesUIConstants.thisCommunitySearchLink);

		// Verify that file added to Files widget should be returned
		logger.strongStep("Verify that file added to Files widget should be returned");
		log.info("INFO: Verify that file added to Files widget should be returned");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.searchResult(fileA.getName())),
				"ERROR: The file added to Files widget does NOT displayed");

		// Select 'Back to Overview Page' button
		logger.strongStep("Select 'Back to Overview Page' button");
		log.info("INFO: Select 'Back to Overview Page' button");
		ui.clickLinkWait(CommunitiesUIConstants.backToOverviewPage);
		ui.waitForPageLoaded(driver);

		// Verify that community overview page should be displayed
		log.info("INFO: Verify that community overview page should be displayed");
		logger.strongStep("Verify that community overview page should be displayed");
		Assert.assertEquals(driver.getTitle(), "Overview - " + community.getName());

		// Click on search icon
		log.info("INFO: Click on search icon");
		logger.strongStep("Click on search icon");
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(GlobalsearchUI.OpenSearchPanel).get(0).getWebElement());
		ui.clickLinkWait(GlobalsearchUI.OpenSearchPanel);

		// Wait for the search text area to display
		ui.fluentWaitPresent(GlobalsearchUI.TextAreaInPanel);

		// Enter the tags entered during community creation
		logger.strongStep("Enter the tags entered during community creation");
		log.info("INFO:  Enter the tags entered during community creation");
		ui.typeText(GlobalsearchUI.TextAreaInPanel, community.getTags());

		// Click on 'All Content'
		logger.strongStep("Click on 'All Content'");
		log.info("INFO: Click on 'All Content'");
		ui.clickLinkWait(GlobalsearchUI.AllContentScope);

		// Verify the search result returns the communities with matching tag names
		logger.strongStep("Verify the search result returns the communities with matching tag names");
		log.info("INFO: Verify the search result returns the communities with matching tag names");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.searchResult(community.getName())),
				"ERROR: The search result does NOT returns the communities with matching tag names");

		// Delete community
		comApiOwner.deleteCommunity(comAPI);
		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Status Updates: Post Entry, Delete/Re-Add Widget</li>
	*<li><B>Step:</B>Load Communities and Log In</li>
	*<li><B>Step:</B>Create a Community</li>
	*<li><B>Step:</B>Navigate to Status Update page</li>
	*<li><B>Verify:</B>Type in 'Status Updates' section</li>
	*<li><B>Step:</B>Verify and Click on 'Post' button</li>
	*<li><B>Step:</B>Click on Widget Action Menu drop-down</li>
	*<li><B>Verify:</B>Select 'Delete' option from drop down</li>
	*<li><B>Step:</B>Verify Delete Status Updates pop box is displayed</li>
	*<li><B>Step:</B>Input 'Confirm Application Name</li>
	*<li><B>Step:</B>Input 'Sign With Your Name</li>
	*<li><B>Step:</B>Click on Ok button</li>
	*<li><B>Step:</B>Verify and Click on Community Actions dropdown</li>
	*<li><B>Step:</B>Click on Add Apps link</li>
	*<li><B>Verify:</B>Re-Add 'Status Update' widget</li>
	*<li><B>Verify:</B>Click on 'Close' icon from add apps window</li>
	*<li><B>Verify:</B>Verify 'Status Update' section is displayed at Top-Navigation bar</li>
	*</ul>
	 */
	@Test(groups = {"regression"})
	public void communityStatusUpdate() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		String postedstatusText = "Posting Status for Community";
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.description("Test description for testcase " + testName).addMember(member).build();

		//Load component and login
		logger.strongStep("Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//Create Community
		logger.strongStep("Create a Community");
		log.info("INFO: Create a Community");
		community.createFromDropDown(ui);
		ui.waitForPageLoaded(driver);

		logger.strongStep("Navigate to Status Update page");
		log.info("INFO: Navigate to Status Update page");
		Community_TabbedNav_Menu.STATUSUPDATES.select(ui);

		logger.strongStep("Type in 'Status Updates' section");
		log.info("INFO: Type in 'Status Updates' section");
		ui.fluentWaitElementVisible("css=li[id^='com_ibm_social_as_item_NoContentItem']");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame+":nth(0)").click();
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame+":nth(0)").type(postedstatusText);
		
		logger.strongStep("Verify and Click on 'Post' button");
		log.info("INFO: Verify and Click on 'Post' button");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.postStatusUpdateBtn),
				"Error: POST button is not Displayed");
		ui.clickLinkWait(CommunitiesUIConstants.postStatusUpdateBtn);
		
		logger.strongStep("Click on Widget Action Menu dropdown");
		log.info("INFO: Click on Widget Action Menu");
		ui.clickLinkWait(CommunitiesUIConstants.widgetActMenu);
		
		logger.strongStep("Select 'Delete' option from drop down");
		log.info("INFO: Select 'Delete' option from drop down");
		ui.clickLinkWait(CommunitiesUIConstants.deleteOptionfromwidgetActMenu);

		logger.strongStep("Verify Delete Status Updates pop box is displayed");
		log.info("INFO: Verify Delete Status Updates pop box is displayed");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.confirmApplicationName),
				"Error: Delete Status Updated Box is not Displayed");

		logger.strongStep("Input 'Confirm Application Name");
		log.info("INFO: Input 'Confirm Application Name");
		String appName = driver.getSingleElement(CommunitiesUIConstants.applicationNameFromDeleteCommStatusWindow).getText();
		ui.typeText(CommunitiesUIConstants.confirmApplicationName, appName);

		logger.strongStep("Input 'Sign With Your Name");
		log.info("INFO: Input 'Confirm Application Name");
		String userName = testUser.getDisplayName();
		ui.typeText(CommunitiesUIConstants.signWithYourName, userName);
		
		logger.strongStep("Click on Ok button");
		log.info("INFO: Click on Ok button");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.deleteStatsUpateOKBtn), "Error: 'Ok' button is not displayed");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.deleteStatsUpateOKBtn);
		ui.waitForPageLoaded(driver);
		
		logger.strongStep("Verify and Click on Community Actions dropdown");
		log.info("INFO: Verify and Click on Community Actions dropdown");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.communityActions));
		
		driver.changeImplicitWaits(5);
		do {
			ui.clickLinkWait(CommunitiesUIConstants.communityActions);
		} while (!ui.isElementVisible("css=#CommunitiesActionsMenuMain"));
		driver.turnOnImplicitWaits();

		logger.strongStep("Click on Add Apps link");
		log.info("INFO: Click on Add Apps link");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.addAppslink);

		logger.strongStep("Re-Add 'Status Update' widget");
		log.info("INFO: Re-Add 'Status Update' widget");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.statusUpdateWidgetAdd);

		logger.strongStep("Click on 'Close' icon from add apps window");
		log.info("INFO: Click on 'Close' icon from add apps window");
		ui.clickLinkWait(CommunitiesUIConstants.closeAddAppsWindow);

		logger.strongStep("Verify 'Status Update' section is displayed at Top-Navigation bar");
		log.info("INFO: Verify 'Status Update' section is displayed at Top-Navigation bar");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.statusUpdateWidgetAdd), "Error: 'Status Update' section is not diaplayed at Top Naigation bar");

		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B>Verify the Mention comments in My community Recent Update</li>
	 *<li><B>Step:</B> Create a Community via API</li> 
	 *<li><B>Step:</B> Login to application and Navigate To created Community</li> 
	 *<li><B>Step:</B> Click On Recent Update</li>
	 *<li><B>Step:</B> Add a comment with Mentions in Recent Update and Post the Comment</li>
	 *<li><B>Verify:</B> Verify the Comment with Mentions is displayed</li>
	 *<li><B>Step:</B> Delete the created community</li>
     *</ul>
	 */
	@Test(groups = { "regression" })
	public void verifyCreateCommentsWithMentionsInRecentUpdates() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();

		// Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.access(Access.PUBLIC)
				.description("Test description for testcase " + testName)
				.build();
		
		logger.strongStep("INFO: Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(comApiOwner);

		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(comApiOwner, comAPI);
		
		//Load component and login
		logger.strongStep("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Communities and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag)
		{
			comApiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("INFO: Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		logger.strongStep("INFO: Fill in the comment form");
		log.info("INFO: Fill in the comment form");
		ui.fluentWaitElementVisible(CommunitiesUIConstants.tabbedNavRecentUpdatesTab);
		ui.clickLinkWithJavascript(CommunitiesUIConstants.tabbedNavRecentUpdatesTab);
		ui.typeMentionInCkEditor("@"+testUserA.getDisplayName());
		
		logger.strongStep("INFO: Click on Post button");
		log.info("INFO: Click on Post button");
        ui.clickLinkWithJavascript(CommunitiesUIConstants.postStatusUpdateBtn);

		//Verify that the comment is present
		logger.strongStep("INFO: Verify that the mention comment is present");
		log.info("INFO: Verify that the mention comment exists");
		Assert.assertTrue(ui.fluentWaitTextPresent("@"+testUserA.getDisplayName()),
						  "ERROR: Comment not found");
		
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.mentionLink.replace("PLACEHOLDER", "@"+testUserA.getDisplayName())),
				  "ERROR: Mention link not present");

		logger.strongStep("INFO : Delete the community");
		log.info("INFO: Delete the community");
		comApiOwner.deleteCommunity(comApiOwner.getCommunity(community.getCommunityUUID()));

		
		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify community owner is able to add image and video in rich content and community member is able to see them correctly</li>
	 * <li><B>Step:</B> [API] Create community using API</li>
	 * <li><B>Step:</B> Load Communites and log in as test user</li>
	 * <li><B>Step:</B> Navigate to the community</li>
	 * <li><B>Step:</B> Select 'Add Content' link from Rich content</li>
	 * <li><B>Step:</B> Click on 'Insert/Edit Image' icon</li>
	 * <li><B>Step:</B> Upload image</li>
	 * <li><B>Verify:</B> Verify the image should be uploaded successfully</li>
	 * <li><B>Step:</B> Select 'Insert Link'-> 'Link to Connections Files'</li>
	 * <li><B>Step:</B> Select 'My Computer' tab</li>
	 * <li><B>Step:</B> Select File to be uploaded from My Computer->Browse</li>
	 * <li><B>Verify:</B> Verify message 'A thumbnail cannot be created for this video. Go to the file details page to add an image to display in previews.'</li>
	 * <li><B>Step:</B> Upload the selected file</li>
	 * <li><B>Verify:</B> Verify the uploaded file name along with 'View Details' link should be displayed</li>
	 * <li><B>Step:</B> Select 'Save' button</li>
	 * <li><B>Step:</B> Logout</li>
	 * <li><B>Step:</B> Login as a community Member</li>
	 * <li><B>Step:</B> Navigate to the community</li>
	 * <li><B>Verify:</B> Verify that uploaded video link and image should be displayed</li>
	 * </ul>
	 */
	@Test(groups = { "regression" })
	public void communityRichContentAddVideoAndImage() throws Exception
	{			
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();
		User testUserB = cfg.getUserAllocator().getUser();
		Member member = new Member(CommunityRole.MEMBERS, testUserB);
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
				.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(com.ibm.conn.auto.appobjects.base.BaseCommunity.Access.PUBLIC)
				.addMember(member)
				.description("Test description for testcase " + testName).build();
		
		BaseFile imageFile = new BaseFile.Builder(Data.getData().file1)
				.extension(".jpg")
				.build();
		
		BaseFile videoFile = new BaseFile.Builder(Data.getData().file4)
				 .extension(".mp4")
				 .build();

		// create community
		logger.strongStep("Create A Community Via API");
		log.info("INFO: Create community using API");
		Community comAPI = baseCommunity.createAPI(comApiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		baseCommunity.getCommunityUUID_API(comApiOwner, comAPI);

		// Load component and login
		logger.strongStep("Load Communites and log in as test user");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		logger.strongStep("Naavigate to community");
		baseCommunity.navViaUUID(ui);

		// Select 'Add Content' link from Rich content
		logger.strongStep("Select 'Add Content' link from Rich content");
		log.info("INFO: Select 'Add Content' link from Rich content");
		ui.waitForPageLoaded(driver);
		ui.clickLinkWait(CommunitiesUIConstants.rteAddContent);

		// Click on 'Insert/Edit Image'
		logger.strongStep("Click on 'Insert/Edit Image' icon");
		log.info("INFO: Click on 'Insert/Edit Image' icon");
		ui.clickLinkWait(CommunitiesUIConstants.insertImageLink);
		
		// Upload image
		logger.strongStep("Upload image");
		log.info("INFO: Upload image");
		try {
			filesUi.fileToUpload(imageFile.getName(), BaseUIConstants.FileInputField2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		ui.clickButton("Upload Image");
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		
		// Verify the image should be uploaded successfully
		logger.strongStep("Verify the image should be uploaded successfully");
		log.info("INFO: Verify the image should be uploaded successfully");
		ui.fluentWaitPresent(CommunitiesUI.imgInRichContent(imageFile));
		Assert.assertTrue(ui.isElementPresent(CommunitiesUI.imgInRichContent(imageFile)));

		// Select 'Insert Link'-> 'Link to Connections Files'
		logger.strongStep("Select 'Insert Link'-> 'Link to Connections Files'");
		log.info("INFO: Select 'Insert Link'-> 'Link to Connections Files'");
		ui.switchToTopFrame();
		ui.clickLinkWait(BaseUIConstants.insertLink);
		ui.switchToFrameBySelector(BaseUIConstants.ckePanelFrame);
		ui.clickLinkWait(BaseUIConstants.linkToConnectionsFiles);

		// Select 'My Computer' tab
		logger.strongStep("Select 'My Computer' tab");
		log.info("INFO: Select 'My Computer' tab");
		ui.switchToTopFrame();
		ui.clickLinkWait(CommunitiesUIConstants.CommunityMyComputer);

		// Select File to be uploaded from My Computer->Browse
		logger.strongStep("Select File to be uploaded from My Computer->Browse");
		log.info("INFO: Select File to be uploaded from My Computer->Browse");
		try {
			filesUi.fileToUpload(videoFile.getName(), BaseUIConstants.FileInputField2);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Verify message
		logger.strongStep("Verify message 'A thumbnail cannot be created for this video. Go to the file details page to add an image to display in previews.'");
		log.info("INFO: Verify message "+ driver.getFirstElement(CommunitiesUIConstants.uploadedVidoeNote).getText());
		Assert.assertEquals(driver.getFirstElement(CommunitiesUIConstants.uploadedVidoeNote).getText(),"A thumbnail cannot be created for this video. Go to the file details page to add an image to display in previews. ");

		// Upload the selected file
		logger.strongStep("Upload the selected file");
		log.info("INFO: Upload the selected file");
		ui.clickButton("Upload");
		
		// Verify the uploaded file name along with 'View Details' link should be displayed
		logger.strongStep("Verify the uploaded file name along with 'View Details' link should be displayed");
		log.info("INFO: Verify the uploaded file name along with 'View Details' link should be displayed");
		driver.switchToFrame().selectSingleFrameBySelector(BaseUIConstants.StatusUpdate_iFrame);
		ui.fluentWaitPresent(CommunitiesUIConstants.viewDetailsLink);
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.videoLink1).getText(), videoFile.getName());
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.viewDetailsLink).getText(), "View Details");

		// Select 'Save' button
		logger.strongStep("Select 'Save' button");
		log.info("INFO: Select 'Save' button");
		ui.switchToTopFrame();
		ui.scrollIntoViewElement(CommunitiesUIConstants.rteSave);
		ui.clickLinkWait(CommunitiesUIConstants.rteSave);
		ui.fluentWaitTextPresent("The page was saved.");
		
		// Logout
		ui.logout();
		
		// Login as a community Member
		logger.strongStep("Login as a community Member");
		log.info("INFO: Login as a community Member");
		ui.loadComponent(Data.getData().ComponentCommunities,true);
		ui.login(testUserB);

		// Navigate to the API community
		logger.strongStep("Navigate to the API community");
		log.info("INFO: Navigate to the community");
		baseCommunity.navViaUUID(ui);
		
		// Verify that uploaded video link and image should be displayed
		ui.fluentWaitPresent(CommunitiesUI.imgInRichContent(imageFile));
		Assert.assertTrue(ui.isElementPresent(CommunitiesUI.imgInRichContent(imageFile)));
		ui.fluentWaitElementVisible(CommunitiesUIConstants.videoLink);
		Assert.assertEquals(driver.getSingleElement(CommunitiesUIConstants.videoLink).getText(), videoFile.getName());
		
		comApiOwner.deleteCommunity(comAPI);
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify that user is able to drag and drop community wigdet from one colunmn to another and rename the app as well</li>
	 * <li><B>Step:</B> [API] Create community using API</li>
	 * <li><B>Step:</B> Load Communities and log in as test user</li>
	 * <li><B>Step:</B> Navigate to the community</li>
	 * <li><B>Step:</B> Add all the widgets to community using Community Action->Add apps</li>
	 * <li><B>Step:</B> Select 'Forums' app and try moving it to other column by dragging and dropping</li>
	 * <li><B>Verify:</B> Verify that the app should be moved to the column successfully</li>
	 * <li><B>Step:</B> Rename the app using 'Change title option'</li>
	 * <li><B>Verify:</B> Verify that user should be able to rename the app successfully</li>
	 * <li><B>Step:</B> Refresh the page</li>
	 * <li><B>Verify:</B> Verify that app name should remain even after the refresh of page</li>
	 * </ul>
	 */
	@Test(groups = { "regression" })
	public void commWidgetDragDrop() throws Exception {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		String testName = ui.startTest();

		// Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand()).access(defaultAccess)
				.description(Data.getData().widgetinsidecommunity + Helper.genStrongRand()).build();

		// create community
		logger.strongStep("Create A Community Via API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(comApiOwner);

		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(comApiOwner, comAPI);

		// Load component and login
		logger.strongStep("Load Communites and log in as test user");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			Community communitycom = comApiOwner.getCommunity(community.getCommunityUUID());
			comApiOwner.editStartPage(communitycom, StartPageApi.OVERVIEW);
		}

		// navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		driver.executeScript("arguments[0].scrollIntoView(true);",driver.getElements(CommunitiesUIConstants.communityActions).get(0).getWebElement());

		// Add all widgets to the community
		logger.strongStep("Add all widgets to the community");
		log.info("INFO: Add all widgets to the community");
		ui.addAllEnabledWigdetsToCommunity();

		String sourceSelector = "css=h2[class='widgetTitle'] span:contains('Forums')";
		Element source = ui.getFirstVisibleElement(sourceSelector);

		String targetSelector = "css=#col1DropZone";
		Element target = ui.getFirstVisibleElement(targetSelector);

		// Select 'Forums' app and try moving it to other column by dragging and dropping
		logger.strongStep("Select 'Forums' app and try moving it to other column by dragging and dropping");
		log.info("INFO: Select 'Forums' app and try moving it to other column by dragging and dropping");
		ui.dragAndDrop(source, target);

		// Verify that the app should be moved to the column successfully
		logger.strongStep("Verify that the app should be moved to the column successfully");
		log.info("INFO: Verify that the app should be moved to the column successfully");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUI.forumPositionInCol("Forums", 1)));

		// Rename the app using 'Change title' option
		logger.strongStep("Rename the app using 'Change title' option");
		log.info("INFO: Rename the app using 'Change title' option");
		ui.clickLinkWait(CommunitiesUI.getMoreAction("Forums"));
		ui.fluentWaitPresent(CommunitiesUIConstants.changeTitleLink);
		ui.clickLinkWait(CommunitiesUIConstants.changeTitleLink);
		ui.fluentWaitPresent(CommunitiesUIConstants.changeTitleDailogue);
		driver.getSingleElement(CommunitiesUIConstants.changeTitleInputField).clear();
		driver.getSingleElement(CommunitiesUIConstants.changeTitleInputField).type("RenameForums");
		ui.clickLinkWait(CommunitiesUIConstants.EditMemberDialogSaveButton);

		// Verify that user should be able to rename the app successfully
		logger.strongStep("Verify that user should be able to rename the app successfully");
		log.info("INFO: Verify that user should be able to rename the app successfully");
		Assert.assertTrue(ui.isElementPresent(CommunitiesUI.appRenameTitle("RenameForums")));

		// Refresh the page
		logger.strongStep("Refresh the page");
		log.info("INFO: Refresh the page");
		driver.navigate().refresh();

		// Verify that app name should remain even after the refresh of page
		logger.strongStep("Verify that app name should remain even after the refresh of page");
		log.info("INFO: Verify that app name should remain even after the refresh of page");
		ui.fluentWaitPresent(CommunitiesUI.appRenameTitle("RenameForums"));
		Assert.assertTrue(ui.isElementPresent(CommunitiesUI.appRenameTitle("RenameForums")));

		// Delete Community
		comApiOwner.deleteCommunity(comAPI);
		ui.endTest();
	}

}
