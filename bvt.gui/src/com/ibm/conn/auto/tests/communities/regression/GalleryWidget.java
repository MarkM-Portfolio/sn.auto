package com.ibm.conn.auto.tests.communities.regression;

import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
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
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.cloud.FilesUICloud;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class GalleryWidget extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(GalleryWidget.class);
	private CommunitiesUI ui;
	private FilesUI fUI;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private Community comAPI1,comAPI2,comAPI3,comAPI4,comAPI5,comAPI6,comAPI7, comAPI8, comAPI9;
	private BaseCommunity community1,community2,community3,community4,community5,community6,community7, community8, community9;

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		fUI = FilesUI.getGui(cfg.getProductName(), driver);
				
	}
	
	@BeforeClass(alwaysRun=true )
	public void setUpClass(){

		cfg = TestConfigCustom.getInstance();
		
		//Load Users		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());

		//Test communities
		community1 = new BaseCommunity.Builder("addGalleryWidget " + Helper.genDateBasedRandVal())
				                      .access(Access.PUBLIC)
				                      .description("Add Gallery widget to the community.")
				                      .build();
		
		community2 = new BaseCommunity.Builder("noFilesToDisplayInGallery " + Helper.genDateBasedRand()) 
		                              .access(Access.PUBLIC)
		                              .description("No files are uploaded, gallery widget is empty.")
		                              .build();
		
		community3 = new BaseCommunity.Builder("galleryWidgetWithOneFile " + Helper.genDateBasedRand()) 
                                      .access(Access.PUBLIC)
                                      .description("Gallery widget displays one file.")
                                      .build();
		
		community4 = new BaseCommunity.Builder("galleryWidgetViewAllLink " + Helper.genDateBasedRand()) 
                                      .access(Access.PUBLIC)
                                      .description("Gallery widget 'View All' link test.")
                                      .build();
		
		community5 = new BaseCommunity.Builder("viewGalleryWidgetAsNonCommMember " + Helper.genDateBasedRand()) 
                                      .access(Access.PUBLIC)
                                      .description("view Gallery widget as a user who is not a member of the community.")
                                      .build();
		
		community6 = new BaseCommunity.Builder("setupGalleryWithEmptyCommFolder " + Helper.genDateBasedRand()) 
                                      .access(Access.PUBLIC)
                                      .description("Configure Gallery widget to point to an empty community files folder.")
                                      .build();
		
		community7 = new BaseCommunity.Builder("setupGalleryWithCommFolderOneFile " + Helper.genDateBasedRand())
		                              .access(Access.PUBLIC)
		                              .description("Configure Gallery widget to point to a community files folder with one file uploaded.")
                                      .build();
		
		community8 = new BaseCommunity.Builder("galleryWidgetWithMultipleFiles " + Helper.genDateBasedRand())
		                              .access(Access.PUBLIC)
		                              .description("Gallery widget displays multiple files. ")
		                              .build();
		
		community9 = new BaseCommunity.Builder("removeUploadedFile " + Helper.genDateBasedRand())
		                              .access(Access.PUBLIC)
                                      .description("Test to make sure a deleted file no longer appears in the Gallery widget. ")
                                      .build();
			
		
		log.info("INFO: create communities via the API");
		comAPI1 = community1.createAPI(apiOwner);
		comAPI2 = community2.createAPI(apiOwner);
		comAPI3 = community3.createAPI(apiOwner);
		comAPI4 = community4.createAPI(apiOwner);
		comAPI5 = community5.createAPI(apiOwner);
		comAPI6 = community6.createAPI(apiOwner);
		comAPI7 = community7.createAPI(apiOwner);
		comAPI8 = community8.createAPI(apiOwner);
		comAPI9 = community9.createAPI(apiOwner);
	}
	
	@AfterClass(alwaysRun=true)
	public void cleanUpNetwork() {

		log.info("INFO: Cleanup - delete communities");
		apiOwner.deleteCommunity(comAPI1);
		apiOwner.deleteCommunity(comAPI2);
		apiOwner.deleteCommunity(comAPI3);
		apiOwner.deleteCommunity(comAPI4);
		apiOwner.deleteCommunity(comAPI5);
		apiOwner.deleteCommunity(comAPI6);
		apiOwner.deleteCommunity(comAPI7);
		apiOwner.deleteCommunity(comAPI8);
		apiOwner.deleteCommunity(comAPI9);

	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Add Gallery Widget</li>
	 *<li><B>Info:</B> Test verifies the Gallery widget can be successfully added to Communities</li>
	 *<li><B>Step:</B> Create a Public community</li>
	 *<li><B>Step:</B> Open the add widget palette:  Community Actions -> Add Apps</li>
	 *<li><B>Step:</B> Select Gallery from the Add Apps palette</li> 
	 *<li><B>Verify:</B> The Gallery widget was added ok</li>
	 */
	@Deprecated
	@Test(groups = {"regression", "regressioncloud"})
	public void addGalleryWidget(){

		boolean found = false;

		ui.startTest();

		log.info("INFO: Get the UUID of the community");
		community1.getCommunityUUID_API(apiOwner, comAPI1);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI1, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using the UUID");
		community1.navViaUUID(ui);

		log.info("INFO: Add the Gallery widget");
		ui.addWidget(BaseWidget.GALLERY);

		log.info("INFO: Verify the Gallery Widget was added");
		List<Element> elements = driver.getElements(CommunitiesUIConstants.rightsideGalleryWidget);
		for (Element element : elements){
			if(element.getText().contentEquals("Gallery")){
				found = true;
				log.info("INFO: The " + element.getText() + " widget was found");
				break;
			}
		}
		Assert.assertTrue(found, 
				"ERROR: The 'Gallery' widget was not found");	

		log.info("INFO: Verify the text: '" + Data.getData().defaultGalleryWidgetText + "' appears");
		Assert.assertTrue(driver.isTextPresent(Data.getData().defaultGalleryWidgetText),
				"ERROR: The text: '" + Data.getData().defaultGalleryWidgetText + "' appears");

		log.info("INFO: Verify the 'Set up the Gallery' link appears");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.setupGalleryLink),
				"ERROR: The link 'Set up the Gallery' does not appear");
		
		log.info("INFO: Verify the Gallery widget title is: " + Data.getData().appGallery);
		Assert.assertEquals(getGalleryTitle(comAPI1), Data.getData().appGallery,
				"INFO: The Gallery widget title is not: " + Data.getData().appGallery);

		ui.endTest();

	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Setup Gallery widget with no file to display</li>
	 *<li><B>Info:</B> Test verifies the default text message and Set up the Gallery link appear</li>
	 *<li><B>Step:</B> Create a Public community</li>
	 *<li><B>Step:</B> Add the Gallery widget via the API</li>
	 *<li><B>Step:</B> Refresh the page so the Gallery widget appears</li> 
	 *<li><B>Step:</B> Click on the 'Set up the Gallery' link</li>
	 *<li><B>Verify:</B> The 'Set up the Gallery' dialog displays</li>
	 *<li><B>Step:</B> Click on the 'Cancel' button</li>
	 *<li><B>Verify:</B> The 'Set up the Gallery' link still appears</li>
	 *<li><B>Step:</B> Click on the 'Set up the Gallery' link again</li>
	 *<li><B>Step:</B> Click on the 'Set as Gallery' button</li>
	 *<li><B>Verify:</B> The text 'There are no files to display in this Gallery.' displays</li>
	 */

	@Test(groups = {"regression", "regressioncloud","cnx8ui-regression"})
	public void noFilesToDisplayInGallery(){
		ui.startTest();

		log.info("INFO: Get the UUID of the community");
		community2.getCommunityUUID_API(apiOwner, comAPI2);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.loginAndToggleUI(testUser1, cfg.getUseNewUI());

		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(comAPI2, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using the UUID");
		community2.navViaUUID(ui);

		log.info("INFO: Add the Gallery widget to communities via the API");
		community2.addWidgetAPI(comAPI2, apiOwner, BaseWidget.GALLERY);

		log.info("INFO: Refresh the browser so the Gallery widget appears");
		UIEvents.refreshPage(driver);

		log.info("INFO: Verify the 'Set up the Gallery' link appears");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.setupGalleryLink),
				"ERROR: 'Set up the Gallery' link does not appear");

		log.info("INFO: Click on the 'Set up the Gallery' link");
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);
		
		log.info("INFO: Verify the 'Set up the Gallery' dialog box displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.setupTheGalleryDialogHeader),
				"ERROR: The 'Set up the Gallery' dialog box does not appear");

		log.info("INFO: Click on the Cancel button");
		ui.clickLinkWithJavascript(CommunitiesUIConstants.MemberCancelButton);
		
		//NOTE: step needed for Cloud otherwise the test will fail because it can't find 'Set up the Gallery' link
		log.info("INFO: Scroll down the page so Gallery summary widget displays");
		driver.executeScript("scroll(250,250);");

		log.info("INFO: Verify the 'Set up the Gallery' link still displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.setupGalleryLink),
				"ERROR: 'Set up the Gallery' link does not appear");

		log.info("INFO: Click on the 'Set up the Gallery' link again");
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);

		log.info("INFO: Click on the 'Set as Gallery' button");
		ui.clickLinkWait(CommunitiesUIConstants.setAsGalleryButton);

		log.info("INFO: Verify the text 'There are no files to display in this Gallery.' displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.noFilesToDisplay),
				"ERROR: Gallery does not show the message 'There are no files to display in this Gallery");
		
		log.info("INFO: Verify the Gallery widget title is: " + community2.getName());
		Assert.assertEquals(getGalleryTitle(comAPI2), community2.getName(),
				"INFO: The Gallery widget title is not: " + community2.getName());

		ui.endTest();

	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Upload File to Community Files</li>
	 *<li><B>Info:</B> Test verifies the gallery widget displays a thumbnail for an uploaded file</li>
	 *<li><B>Step:</B> Create a Public community</li>
	 *<li><B>Step:</B> Upload an image file to Community Files</li>
	 *<li><B>Verify:</B> The file upload success message displays</li>
	 *<li><B>Step:</B> Add the Gallery widget via the API</li>
	 *<li><B>Step:</B> Refresh the page so the Gallery widget appears</li> 
	 *<li><B>Step:</B> Click on the 'Set up the Gallery' link</li>
	 *<li><B>Step:</B> Click on the 'Set as Gallery' button</li>
	 *<li><B>Verify:</B> The thumbnail for the file appears in the Gallery widget</li>
	 *<li><B>CNX8UI_Defect</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-12889</li>
	 */

	@Test(groups = {"regression", "regressioncloud"})
	public void galleryWidgetWithOneFile(){

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		                             .comFile(true)
		                             .extension(".jpg")
		                             .build();

		ui.startTest();

		log.info("INFO: Get the UUID of the community");
		community3.getCommunityUUID_API(apiOwner, comAPI3);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI3, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using the UUID");
		community3.navViaUUID(ui);

		log.info("INFO: Click on the Files tab");
		Community_TabbedNav_Menu.FILES.select(ui);

		log.info("INFO: Upload fileA");
		fileA.upload(fUI);

		log.info("INFO: Verify the file upload success message displays");
		Assert.assertTrue(driver.isTextPresent(Data.getData().UploadMessage + fileA.getName()),
				"ERROR: The successfully uploaded message does not appear");

		log.info("INFO: Return to the Overview page");
		Community_TabbedNav_Menu.OVERVIEW.select(ui);

		log.info("INFO: Add the Gallery widget to communities via the API");
		community3.addWidgetAPI(comAPI3, apiOwner, BaseWidget.GALLERY);

		log.info("INFO: Refresh the browser so the Gallery widget appears");
		UIEvents.refreshPage(driver);

		log.info("INFO: Click on the 'Set up the Gallery' link");
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);

		log.info("INFO: Click on the 'Set as Gallery' button");
		ui.clickLinkWait(CommunitiesUIConstants.setAsGalleryButton);

		log.info("INFO: Verify the Gallery widget title is the same as the community name");
		Assert.assertEquals(getGalleryTitle(comAPI3), community3.getName(),
				"ERROR: Gallery widget title does not match the name of the community");

		log.info("INFO: Verify thumbnail for fileA is shown in the Gallery summary widget");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getFileThumbnail(fileA)),
				"ERROR: Thumbnail for fileA is not shown in Gallery");

		ui.endTest();

	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Gallery widget 'View All' link</li>
	 *<li><B>Info:</B> Test verifies clicking on the 'View All' link brings up the Gallery full widget page</li>
	 *<li><B>Step:</B> Create a Public community</li>
	 *<li><B>Step:</B> Upload an image file to Community Files</li>
	 *<li><B>Verify:</B> The file upload success message displays</li>
	 *<li><B>Step:</B> Add the Gallery widget via the API</li>
	 *<li><B>Step:</B> Refresh the page so the Gallery widget appears</li> 
	 *<li><B>Step:</B> Click on the 'Set up the Gallery' link</li>
	 *<li><B>Step:</B> Click on the 'Set as Gallery' button</li>
	 *<li><B>Verify:</B> The View All link appears</li>
	 *<li><B>Step:</B> Click on the View All link</li>
	 *<li><B>Verify:</B> The Files full widget page displays</li>
	 *<li><B>CNX8UI_Defect</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-12889</li>
	 */

	@Test(groups = {"regression", "regressioncloud"})
	public void galleryWidgetViewAllLink(){

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		                             .comFile(true)
		                             .extension(".jpg")
		                             .build();

		ui.startTest();

		log.info("INFO: Get the UUID of the community");
		community4.getCommunityUUID_API(apiOwner, comAPI4);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI4, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using the UUID");
		community4.navViaUUID(ui);

		log.info("INFO: Click on the Files tab");
		Community_TabbedNav_Menu.FILES.select(ui);

		log.info("INFO: Upload fileA");
		fileA.upload(fUI);

		log.info("INFO: Looking for file upload success message");
		Assert.assertTrue(driver.isTextPresent(Data.getData().UploadMessage + fileA.getName()),
				"ERROR: The successfully uploaded message does not appear");

		log.info("INFO: Return to the Overview page");
		Community_TabbedNav_Menu.OVERVIEW.select(ui);

		log.info("INFO: Add the Gallery widget to communities via the API");
		community4.addWidgetAPI(comAPI4, apiOwner, BaseWidget.GALLERY);

		log.info("INFO: Refresh the browser so the Gallery widget appears");
		UIEvents.refreshPage(driver);

		log.info("INFO: Click on the 'Set up the Gallery' link");
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);

		log.info("INFO: Click on the 'Set as Gallery' button");
		ui.clickLinkWait(CommunitiesUIConstants.setAsGalleryButton);
		
		log.info("INFO: Verify the Gallery widget 'View All' link displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.galleryViewAllLink),
				"ERROR: The View All link does not appear");

		log.info("INFO: Click on the Gallery widget View All link");
		ui.clickLinkWait(CommunitiesUI.getViewAllLink(1));

		log.info("INFO: Verify clicking the View All link brings up the Files full widget page");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.filesFullWidgetPageHeader),
				"ERROR: The full widget page does not appear");

		ui.endTest();

	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> View Gallery widget as user who is not a member of the community</li>
	 *<li><B>Info:</B> Test verifies that a non-community member sees the correct gallery widget message</li>
	 *<li><B>Step:</B> Create a Public community as UserA</li>
	 *<li><B>Step:</B> Add the Gallery widget via the API</li>
	 *<li><B>Step:</B> Logout of Communities as UserA</li> 
	 *<li><B>Step:</B> Login as UserB & navigate to the Public communities catalog view</li>
	 *<li><B>Step:</B> Open the community created by UserA</li>
	 *<li><B>Verify:</B> The message that the gallery is not set up yet displays</li>
	 */
	//disabled test case because we do not have Public communities catalog view on my community page now
	@Test(groups = {"regression", ("regressioncloud")},enabled =false)
	public void viewGalleryWidgetAsNonCommMember(){

		ui.startTest();

		log.info("INFO: Add the Gallery widget to communities via the API");
		community5.addWidgetAPI(comAPI5, apiOwner, BaseWidget.GALLERY);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);	
		
		log.info("INFO: Navigate to the Public Communities catalog view");
		Community_View_Menu.PUBLIC_COMMUNITIES.select(ui);

		log.info("INFO: Verify the community appears in the Public communities catalog view");
		ui.fluentWaitPresentWithRefresh("link=" + community5.getName());		
		Assert.assertTrue(driver.isElementPresent("link=" + community5.getName()),
				"ERROR: The community '" + community5.getName() + "' is not listed in the public catalog view");

		log.info("INFO: Select the community");
		ui.clickLinkWait("link=" + community5.getName());

		log.info("INFO: Verify the message '" + Data.getData().galleryIsNotSetupText + "' appears");
		Assert.assertTrue(driver.isTextPresent(Data.getData().galleryIsNotSetupText),
				"ERROR: The Gallery message '" + Data.getData().galleryIsNotSetupText + "' does not appear");

		ui.endTest();

	}

	/**
	 * <ul>
	 * <li><B>Test Scenario:</B> Configure the Gallery widget to point to an empty folder</li>
	 * <li><B>Info:</B>When the widget is configured to point to a folder, the widget title is the same as the folder name</li>
	 * <li><B>Step:</B>Create a community</li>
	 * <li><B>Step:</B>Add the Gallery widget via the API</li>
	 * <li><B>Step:</B>Create a folder in Community Files</li>
	 * <li><B>Step:</B>Setup the gallery widget to point to the community files folder</li>
	 * <li><B>Verify:</B>Gallery widget title reflects the name of the folder</li>
	 *<li><B>CNX8UI_Defect</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-12889</li>
	 * </ul>
	 */
	@Test(groups = { "regression", "regressioncloud"})
	public void setupGalleryWithEmptyCommFolder(){

		BaseFolder folder = new BaseFolder.Builder("CommFileFolder" + Helper.genDateBasedRand())
		                                  .description(Data.getData().FolderDescription)
		                                  .build();

		String folderName = folder.getName();

		ui.startTest();

		log.info("INFO: Add the gallery widget to the community via the API");
		community6.addWidgetAPI(comAPI6, apiOwner, BaseWidget.GALLERY);

		log.info("INFO: Get the UUID of the community");
		community6.getCommunityUUID_API(apiOwner, comAPI6);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI6, StartPageApi.OVERVIEW);
		}
		
		log.info("INFO: Navigate to the community using the UUID");
		community6.navViaUUID(ui);

		log.info("INFO: Select Files from the tabbed nav menu");
		Community_TabbedNav_Menu.FILES.select(ui);

		log.info("INFO: Create a folder");
		folder.add(fUI);

		log.info("INFO: Select Overview from the tabbed nav menu");
		Community_TabbedNav_Menu.OVERVIEW.select(ui);

		log.info("INFO: Click on the Set up the gallery link");
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);

		log.info("INFO: Select the folder to add the file too");
		ui.clickLinkWait(CommunitiesUI.selectFolder(folder));

		log.info("INFO: Click on the 'Set as Gallery' button");
		ui.clickLinkWait(CommunitiesUIConstants.setAsGalleryButton);

		log.info("INFO: Verify the Gallery widget title is the same as the folder name");
		Assert.assertEquals(getGalleryTitle(comAPI6), folderName,
				"INFO: The Gallery widget title does not match the folder name ");				

		log.info("INFO: Verify the text 'There are no files to display in this Gallery.' displays");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUIConstants.noFilesToDisplay),
				"ERROR: Gallery does not show the message 'There are no files to display in this Gallery");

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Test Scenario: Configure the Gallery widget to point to a folder with one file</li>  
	 * <li><B>Info:</B>Configure the Gallery widget to point to a folder with one file</li>
	 * <li><B>Step:</B>Create a public community</li>
	 * <li><B>Step:</B>Add the Gallery widget via the API</li>
	 * <li><B>Step:</B>Create a folder in Community Files widget</li>
	 * <li><B>Step:</B>Upload a file to Community Files widget</li>
	 * <li><B>Step:</B>Add the file to the folder</li>
	 * <li><B>Step:</B>Setup the gallery widget to point to the Files folder</li>
	 * <li><B>Verify:</B>Gallery widget title reflects the name of the folder</li>
	 * <li><B>Verify:</B>A thumbnail displays for the uploaded file</li>
	 *<li><B>CNX8UI_Defect</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-12889</li>
	 * </ul>
	 */
	@Test(groups = { "regression", "regressioncloud"})
	public void setupGalleryWithCommFolderOneFile(){

		BaseFolder folder = new BaseFolder.Builder("CommFileFolder" + Helper.genDateBasedRand())
		                                  .description(Data.getData().FolderDescription)
		                                  .build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		                             .comFile(true)
		                             .extension(".txt")
		                             .build();

		String folderName = folder.getName();

		ui.startTest();

		log.info("INFO: Add the gallery widget to the community via API");
		community7.addWidgetAPI(comAPI7, apiOwner, BaseWidget.GALLERY);

		log.info("INFO: Get the UUID of the community");
		community7.getCommunityUUID_API(apiOwner, comAPI7);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI7, StartPageApi.OVERVIEW);
		}
		
		log.info("INFO: Navigate to the community using the UUID");
		community7.navViaUUID(ui);

		log.info("INFO: Select Files from the tabbed nav menu");
		Community_TabbedNav_Menu.FILES.select(ui);

		log.info("INFO: Create a folder");
		folder.add(fUI);

		log.info("INFO: Upload a file");
		fileA.upload(fUI);

		log.info("INFO: Add the file to the folder");
		fileA.addToFolder(fUI, folder);

		log.info("INFO: Select Overview from the tabbed nav menu");
		Community_TabbedNav_Menu.OVERVIEW.select(ui);
		
		//NOTE: step needed for Cloud otherwise the test will fail because it can't find 'Set up the Gallery' link
		log.info("INFO: Scroll down the page so Gallery summary widget displays");
		driver.executeScript("scroll(250,250);");

		log.info("INFO: Click on the Set up the Gallery link");
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);

		log.info("INFO: Select the folder that the file was added to");
		ui.clickLinkWait(CommunitiesUI.selectFolder(folder));

		log.info("INFO: Click on the 'Set as Gallery' button");
		ui.clickLinkWait(CommunitiesUIConstants.setAsGalleryButton);

		log.info("INFO: Verify the Gallery widget title is the same as the folder name");
		Assert.assertEquals(getGalleryTitle(comAPI7), folderName,
				"INFO: The Gallery widget title does not match the folder name ");			

		log.info("INFO: Verify thumbnail for fileA is shown in the Gallery summary widget");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getFileThumbnail(fileA)),
				"ERROR: Thumbnail for fileA is not shown in Gallery");

		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Upload multiple files to the Files widget</li>
	 *<li><B>Info:</B> Test verifies that a thumbnail for each of the uploaded files appears in the gallery widget</li>
	 *<li><B>Step:</B> Create a Public community</li>
	 *<li><B>Step:</B> Upload (3) image files to Community Files</li>
	 *<li><B>Verify:</B> Each file uploads successfully</li>
	 *<li><B>Step:</B> Add the Gallery widget via using the API</li>
	 *<li><B>Step:</B> Refresh the page so the Gallery widget appears on the page</li> 
	 *<li><B>Step:</B> Click on the 'Set up the Gallery' link</li>
	 *<li><B>Step:</B> Click on the 'Set as Gallery' button</li>
	 *<li><B>Verify:</B> A thumbnail for each file appears in the Gallery widget</li>
	 *<li><B>CNX8UI_Defect</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-12889</li>
	 */

	@Test(groups = {"regression", "regressioncloud"})
	public void galleryWidgetWithMultipleFiles(){

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		                             .comFile(true)
		                             .extension(".jpg")
		                             .rename(Helper.genStrongRand())
		                             .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file2)
		                             .comFile(true)
		                             .extension(".jpg")
		                             .rename(Helper.genStrongRand())
		                             .build();

		BaseFile fileC = new BaseFile.Builder(Data.getData().file3)
		                             .comFile(true)
		                             .extension(".jpg")
		                             .rename(Helper.genStrongRand())
		                             .build();

		ui.startTest();

		log.info("INFO: Get the UUID of the community");
		community8.getCommunityUUID_API(apiOwner, comAPI8);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI8, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using the UUID");
		community8.navViaUUID(ui);

		log.info("INFO: Select Files from the tabbed nav menu");
		Community_TabbedNav_Menu.FILES.select(ui);

		log.info("INFO: Upload fileA");
		fileA.upload(fUI);

		log.info("INFO: Look for the file upload success message");
		Assert.assertTrue(driver.isTextPresent(Data.getData().UploadMessage + fileA.getName()),
				"ERROR: The successfully uploaded message does not appear for " + fileA.getName());
		
		log.info("INFO: Upload fileB");
		fileB.upload(fUI);

		log.info("INFO: Look for the file upload success message");
		Assert.assertTrue(driver.isTextPresent(Data.getData().UploadMessage + fileB.getName()),
				"ERROR: The successfully uploaded message does not appear for " + fileB.getName());
		
		log.info("INFO: Upload fileC");
		fileC.upload(fUI);

		log.info("INFO: Look for the file upload success message");
		Assert.assertTrue(driver.isTextPresent(Data.getData().UploadMessage + fileC.getName()),
				"ERROR: The successfully uploaded message does not appear for " + fileC.getName());

		log.info("INFO: Select Overview from the tabbed nav menu");
		Community_TabbedNav_Menu.OVERVIEW.select(ui);

		log.info("INFO: Add the Gallery widget to communities via the API");
		community8.addWidgetAPI(comAPI8, apiOwner, BaseWidget.GALLERY);

		log.info("INFO: Refresh the browser so the Gallery widget appears");
		UIEvents.refreshPage(driver);
		
		//NOTE: step needed to slow down automation to ensure the 'scroll' step is done
		log.info("INFO: Verify the Gallery widget title is: " + Data.getData().appGallery);
		Assert.assertEquals(getGalleryTitle(comAPI8), Data.getData().appGallery,
				"INFO: The Gallery widget title is not: " + Data.getData().appGallery);
		
		//NOTE: step needed for Cloud otherwise the test will fail because it can't find 'Set up the Gallery' link
		log.info("INFO: Scroll down the page so Gallery summary widget displays");
		driver.executeScript("scroll(250,250);");

		log.info("INFO: Click on the 'Set up the Gallery' link");
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);

		log.info("INFO: Click on the 'Set as Gallery' button");
		ui.clickLinkWait(CommunitiesUIConstants.setAsGalleryButton);

		log.info("INFO: Verify the Gallery widget title is the same as the community name");
		Assert.assertEquals(getGalleryTitle(comAPI8), community8.getName(),
				"ERROR: Gallery does not have correct title, should be the name of the community");

		log.info("INFO: Verify thumbnail for "  + fileA.getName() + " is shown in the Gallery summary widget");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getFileThumbnail(fileA)),
				"ERROR: Thumbnail for " + fileA.getName() + " is not shown in Gallery");
		
		log.info("INFO: Verify thumbnail for " + fileB.getName() + " is shown in the Gallery summary widget");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getFileThumbnail(fileB)),
				"ERROR: Thumbnail for " + fileB.getName() + " is not shown in Gallery");
		
		log.info("INFO: Verify thumbnail for " + fileC.getName() + " is shown in the Gallery summary widget");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getFileThumbnail(fileC)),
				"ERROR: Thumbnail for " + fileC.getName() + " is not shown in Gallery");

		ui.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Test Scenario:</B> Remove uploaded file - thumbnail no longer displays</li>
	 *<li><B>Info:</B> Test verifies thumbnail no longer appears for a removed/deleted file</li>
	 *<li><B>Step:</B> Create a Public community</li>
	 *<li><B>Step:</B> Upload an image file to Community Files</li>
	 *<li><B>Step:</B> Add the Gallery widget via the API</li>
	 *<li><B>Step:</B> Refresh the page so the Gallery widget appears on the page</li> 
	 *<li><B>Step:</B> Click on the 'Set up the Gallery' link</li>
	 *<li><B>Step:</B> Click on the 'Set as Gallery' button</li>
	 *<li><B>Verify:</B> The Gallery widget title is the same as community name</li>
	 *<li><B>Verify:</B> The thumbnail for the file appears in the Gallery widget</li>
	 *<li><B>Step:</B> Delete the file from the community files widget</li>
	 *<li><B>Step:</B> Return to the Overview page</li>
	 *<li><B>Verify:</B> The thumbnail for the deleted file not longer appears in the gallery widget</li>
	 *<li><B>CNX8UI_Defect</B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-12889</li>
	 */

	@Test(groups = {"regression", "regressioncloud"})
	public void removeUploadedFile(){

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		                             .comFile(true)
		                             .extension(".jpg")
		                             .rename(Helper.genStrongRand())
		                             .build();

		ui.startTest();

		log.info("INFO: Get the UUID of the community");
		community9.getCommunityUUID_API(apiOwner, comAPI9);

		log.info("INFO: Load Communities & login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		log.info("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = ui.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI9, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the community using the UUID");
		community9.navViaUUID(ui);

		log.info("INFO: Select Files from the tabbed nav menu");
		Community_TabbedNav_Menu.FILES.select(ui);

		log.info("INFO: Upload fileA");
		fileA.upload(fUI);

		log.info("INFO: Return to the Overview page");
		Community_TabbedNav_Menu.OVERVIEW.select(ui);

		log.info("INFO: Add the Gallery widget to communities via the API");
		community9.addWidgetAPI(comAPI9, apiOwner, BaseWidget.GALLERY);

		log.info("INFO: Refresh the browser so the Gallery widget appears");
		UIEvents.refreshPage(driver);

		log.info("INFO: Click on the 'Set up the Gallery' link");
		ui.clickLinkWait(CommunitiesUIConstants.setupGalleryLink);

		log.info("INFO: Click on the 'Set as Gallery' button");
		ui.clickLinkWait(CommunitiesUIConstants.setAsGalleryButton);

		log.info("INFO: Verify the Gallery widget title is the same as the community name");
		Assert.assertEquals(getGalleryTitle(comAPI9), community9.getName(),
				"ERROR: Gallery does not have correct title, should be the name of the community");

		log.info("INFO: Verify thumbnail for fileA is shown in the Gallery summary widget");
		Assert.assertTrue(driver.isElementPresent(CommunitiesUI.getFileThumbnail(fileA)),
				"ERROR: Thumbnail for fileA is not shown in Gallery");
		
		log.info("INFO: Delete the file uploaded to the community files widget");
		log.info("INFO: Select Files from the tabbed nav menu");
		Community_TabbedNav_Menu.FILES.select(ui);
		
		log.info("INFO: Change to the List view");
		ui.clickLinkWait(FilesUICloud.listView);
		
		log.info("INFO: Click on the More link");
		ui.clickLinkWait(FilesUIConstants.moreLink);
		
		log.info("INFO: Click on More Actions");
		ui.clickLinkWait(CommunitiesUIConstants.filesMoreActionsButton);
		
		log.info("INFO: Click the Move to Trash link");
		ui.clickLinkWait(CommunitiesUIConstants.filesMoveToTrash);
		
		log.info("INFO: Click on the Move to Trash OK button");
		ui.clickLinkWait(FilesUIConstants.okButton);
		
		log.info("INFO: Return to the Overview page");
		Community_TabbedNav_Menu.OVERVIEW.select(ui);
		
		ui.waitForPageLoaded(driver);	

		log.info("INFO: Verify that the file no longer appears in the gallery widget");
		Assert.assertFalse(driver.isElementPresent(CommunitiesUI.getFileThumbnail(fileA)),
           "ERROR: File still appears in the Gallery widget");
		
		ui.endTest();

	}


	private String getGalleryTitle(Community commAPI) {

		String commUUID = apiOwner.getCommunityUUID(commAPI);

		log.info("INFO: commUID is " + commUUID);

		String widgetID = apiOwner.getWidgetID(ForumsUtils.getCommunityUUID(commUUID),"Gallery");

		log.info("INFO: Gallery id is " + widgetID);

		String galleryName = driver.getSingleElement(CommunitiesUI.getWidgetTitle(widgetID)).getText();

		log.info("INFO: Gallery name is " + galleryName);

		return galleryName;	
	}
}
