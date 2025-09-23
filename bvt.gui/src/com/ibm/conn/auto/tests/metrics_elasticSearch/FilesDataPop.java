package com.ibm.conn.auto.tests.metrics_elasticSearch;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.cloud.FilesUICloud;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class FilesDataPop extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(FilesDataPop.class);
	private TestConfigCustom cfg;
	private CommunitiesUI commUI;	
	private FilesUI ui;
	private APICommunitiesHandler apiOwner;
	private String serverURL;
	private User adminUser, testUser1, testUser2, testUser3, testUser4;
	private GatekeeperConfig gkc;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser(); 
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		adminUser = cfg.getUserAllocator().getAdminUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		URLConstants.setServerURL(serverURL);
			
}
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Upload a File</li>
	 *<li><B>Step:</B> Public community is created via the API</li>
	 *<li><B>Step:</B> Add (2) image files to the Files widget</li>  
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void uploadImageFilesToCommFiles() {
		
		String testName = ui.startTest();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		                             .comFile(true)
		                             .extension(".jpg")
		                             .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file2)
		                             .comFile(true)
		                             .extension(".jpg")
		                             .build();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
		                                           .access(Access.PUBLIC)
		                                           .description("Community Files - upload (2) image files to the community Files widget. ")
		                                           .build();

        log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		this.closeGuidedTourBox();
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
				
		log.info("INFO: Select Files from the navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui);
		
		log.info("INFO: Add a new file: " + fileA.getName());
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
			fileA.upload(ui,gkc);
		else
			fileA.upload(ui);
		
		
		log.info("INFO: Validate file upload message is present for fileA");
		if (!ui.fluentWaitTextPresent(Data.getData().UploadMessage)){
			log.info("INFO: Message not present, clicking upload link and checking for message again");
			ui.reClickUploadLink(fileA, gkc);
			ui.fluentWaitTextPresent(Data.getData().UploadMessage);
		}
		
		log.info("INFO: Add a new file: " + fileB.getName());
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
			fileB.upload(ui,gkc);
		else
			fileB.upload(ui);
		
		log.info("INFO: Validate file upload message is present for fileB");
		if (!ui.fluentWaitTextPresent(Data.getData().UploadMessage)){
			
			log.info("INFO: Message not present, clicking upload link and checking for message again");
			ui.reClickUploadLink(fileB, gkc);
			ui.fluentWaitTextPresent(Data.getData().UploadMessage);
		}
				
		ui.logout();
		ui.close(cfg);	
		ui.endTest();
	
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Edit a File</li>
	 *<li><B>Info:</B> Public community is created via the API</li>
	 *<li><B>Info:</B> Add an image files to the Files widget</li>  
	 *<li><B>Info:</B> Make an update to the image file name</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"})
	public void editImageFileInCommFiles() {
		
		String testName = ui.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
                                     .comFile(true)
                                     .extension(".jpg")
                                     .build();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("Community Files - edit image file's name")
                                                   .build(); 
		       
       log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		this.closeGuidedTourBox();
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
				
		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui);
		
		log.info("INFO: Add a new file: " + file.getName());
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
			file.upload(ui,gkc);
		else
			file.upload(ui);
		
		
		log.info("INFO: Validate file upload message is present");
		if (!ui.fluentWaitTextPresent(Data.getData().UploadMessage)){
			log.info("INFO: Message not present, clicking upload link and checking for message again");
			ui.reClickUploadLink(file, gkc);
			ui.fluentWaitTextPresent(Data.getData().UploadMessage);
		}
								
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
				
		log.info("INFO: Validate that the file is visible");
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectFile(file)),
						  "ERROR: Unable to find the file " + file.getName());
		
		log.info("INFO: Click on the More link for the uploaded file");
		ui.clickLinkWait(FilesUIConstants.moreLink);
		
		log.info("INFO: Click on the More Actions link");
		ui.clickLinkWait(FilesUIConstants.filesMoreActionsBtn);
		
		log.info("INFO: Click on Edit Properties");
		ui.clickLinkWait(FilesUIConstants.EditPropertiesOption);

		ui.fluentWaitTextPresent(Data.getData().editPropertiesDialogBoxTitle);
		ui.clearText(FilesUIConstants.editPropertiesName);
		ui.typeText(FilesUIConstants.editPropertiesName, Data.getData().editedFileName);
		ui.clickButton(Data.getData().buttonSave);
		
		log.info("INFO: Verify the updated file name appears");
		Assert.assertTrue(driver.isTextPresent(Data.getData().editedFileName),
				"ERROR: The updated file name does not appear");
			
		ui.endTest();
	
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - Upload a File</li>
	 *<li><B>Step:</B> Add an image file to standalone Files app</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void uploadImageFileToStandaloneFiles() {
		
		
		ui.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file7)
                                     .extension(".jpg")
                                     .build();
				
		log.info("INFO: Login to Files as user: " + testUser1.getDisplayName());	
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser1);
		
		this.closeGuidedTourBox();
		
		log.info("INFO: Wait for My Files scene to be available");
		ui.clickMyFilesView();
		ui.fluentWaitPresent(FilesUIConstants.MyFilesTitleLink);
		
		log.info("INFO: Upload the file " + file.getName());
		file.upload(ui);
		
		log.info("INFO: Validate file upload message is present");
		if (!ui.fluentWaitTextPresent(Data.getData().UploadMessage)){
			
			log.info("INFO: Message not present, clicking upload link and checking for message again");
			ui.reClickUploadLink(file, gkc);
			ui.fluentWaitTextPresent(Data.getData().UploadMessage);
		}
		
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - Edit Image File</li>
	 *<li><B>Step:</B> Add an image file to standalone Files app</li>
	 *<li><B>Step:</B> Edit the file name</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void editImageFileInStandaloneFiles() {
						
		ui.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file6)
                                     .extension(".jpg")
                                     .build();
						
		log.info("INFO: Login to Files as user: " + testUser1.getDisplayName());	
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser4);
		
		this.closeGuidedTourBox();
		
		log.info("INFO: Wait for My Files scene to be available");
		ui.clickMyFilesView();
		ui.fluentWaitPresent(FilesUIConstants.MyFilesTitleLink);
				
		log.info("INFO: Upload a file");	
		file.upload(ui);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
				
		log.info("INFO: Validate the file is visible");
		Assert.assertTrue(driver.isElementPresent(FilesUI.selectFile(file)),
						  "ERROR: Unable to find the file " + file.getName());
		
		log.info("INFO: Click on the More link for the uploaded file");
		ui.clickLinkWait(FilesUIConstants.moreLink);
		
		log.info("INFO: Click on the More Actions link");
		ui.clickLinkWait(FilesUIConstants.filesMoreActionsBtn);
		
		log.info("INFO: Click on Edit Properties");
		ui.clickLinkWait(FilesUIConstants.EditPropertiesOption);

		ui.fluentWaitTextPresent(Data.getData().editPropertiesDialogBoxTitle);
		ui.clearText(FilesUIConstants.editPropertiesName);
		ui.typeText(FilesUIConstants.editPropertiesName, Data.getData().editedFileName);
		ui.clickButton(Data.getData().buttonSave);
		
		log.info("INFO: Verify the updated file name appears");
		Assert.assertTrue(driver.isTextPresent(Data.getData().editedFileName),
				"ERROR: The updated file name does not appear");
				
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - Upload & Download Files</li>
	 *<li><B>Step:</B> UserA uploads (2) image files & makes them public</li>
	 *<li><B>Step:</B> UserB logs in and downloads fileA</li>
	 *<li><B>Step:</B> UserB logs out and UserC logs in</li>
	 *<li><B>Step:</B> UserC downloads fileB</li>
	 *</ul>
	 */
	@Test (groups = {"regression","regressioncloud"} , enabled=false  )
	public void downloadFilesFromStandaloneFiles() {

		ui.startTest();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file9)
			                         .extension(".jpg")
			                         .rename(Helper.genDateBasedRand())
			                         .build();
		
		BaseFile fileB = new BaseFile.Builder(Data.getData().file10)
                                     .extension(".jpg")
                                     .rename(Helper.genDateBasedRand())
                                     .build();

		log.info("INFO: Log into Files");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser1);
		
		this.closeGuidedTourBox();
		
		log.info("INFO: Wait for My Files view to be available");
		ui.clickMyFilesView();
		ui.fluentWaitPresent(FilesUIConstants.MyFilesTitleLink);
		
		log.info("INFO: Upload fileA " + fileA.getName());
		fileA.upload(ui);
		
		log.info("INFO: Check for the upload Success message for file: " + ui.getUploadFileName(fileA));
		ui.fluentWaitPresent(FilesUICloud.fileUploadedSuccessImg);
		
		log.info("INFO: Upload fileB " + fileB.getName());
		fileB.upload(ui);

		log.info("INFO: Check for the upload Success message for file: " + ui.getUploadFileName(fileB));
		ui.fluentWaitPresent(FilesUICloud.fileUploadedSuccessImg);
				
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);

		log.info("INFO: Make file1 Public - share file with everyone in the organization");
		ui.share(fileA);
		
		log.info("INFO: Make file2 Public - share file with everyone in the organization");
		ui.share(fileB);
		
		log.info("INFO: Log out as userA: " + testUser1.getDisplayName());
		ui.logout();
		ui.close(cfg);
				
		log.info("INFO: Log in as userB: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.login(testUser2);
		
		this.closeGuidedTourBox();
		
		log.info("INFO: Click on All Files to expand the section");
		ui.clickLinkWait(FilesUIConstants.AllFilesView);
		
		log.info("INFO: Check if userB can see the file");
		ui.clickLinkWait(FilesUIConstants.PublicFilesInNav);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
				
		log.info("INFO: Open and download fileA");
		ui.download(fileA);
		
		log.info("INFO: Check if file was downloaded");
		try {
			ui.verifyFileDownloaded(fileA.getRename() + fileA.getExtension());
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		log.info("INFO: Log out as userB: " + testUser2.getDisplayName());
		ui.logout();
		ui.close(cfg);
				
		log.info("INFO: Log in as userC: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles, true);
		ui.login(testUser3);
		
		this.closeGuidedTourBox();
				
		log.info("INFO: Click on All Files to expand the section");
		ui.clickLinkWait(FilesUIConstants.AllFilesView);
		
		log.info("INFO: Check if userC can see the file");
		ui.clickLinkWait(FilesUIConstants.PublicFilesInNav);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
				
		log.info("INFO: Open and download file");
		ui.download(fileB);
		
		log.info("INFO: Check if file was downloaded");
		try {
			ui.verifyFileDownloaded(fileB.getRename() + fileB.getExtension());
		} catch (Exception e) {
			e.printStackTrace();
		}

		ui.endTest();	

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - Share Files with Everyone in the Organization</li>
	 *<li><B>Step:</B> Upload (2) image files & makes them public</li>
	 *<li><B>Step:</B> Share both files with everyone in the organization</li>
	 *</ul>
	 */
	@Test (groups = {"regression","regressioncloud"}  , enabled=false )
	public void shareFileFromStandaloneFiles() {

		ui.startTest();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
                                     .extension(".jpg")
                                     .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file2)
		                             .extension(".jpg")
		                             .build();

		log.info("INFO: Log into Files");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser2);
		
		this.closeGuidedTourBox();
		
		log.info("INFO: Wait for My Files scene to be available");
		ui.clickMyFilesView();
		ui.fluentWaitPresent(FilesUIConstants.MyFilesTitleLink);
		
		log.info("INFO: Upload fileA " + fileA.getName());
		fileA.upload(ui);
		
		log.info("INFO: Check for the upload Success message for fileA: " + ui.getUploadFileName(fileA));
		ui.fluentWaitPresent(FilesUICloud.fileUploadedSuccessImg);
		
		log.info("INFO: Upload fileB " + fileB.getName());
		fileB.upload(ui);

		log.info("INFO: Check for the upload Success message for file: " + ui.getUploadFileName(fileB));
		ui.fluentWaitPresent(FilesUICloud.fileUploadedSuccessImg);
				
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);

		log.info("INFO: Make fileA Public - share file with everyone in the organization");
		ui.share(fileA);
		
		log.info("INFO: Verify the file was publicly shared");
		Assert.assertTrue(driver.isTextPresent("File was shared successfully"),
				  			"ERROR: File was not shared");
		
		log.info("INFO: Make fileB Public - share file with everyone in the organization");
		ui.share(fileB);
		
		log.info("INFO: Verify the file was publicly shared");
		Assert.assertTrue(driver.isTextPresent("File was shared successfully"),
				  			"ERROR: File not shared");
		
		ui.endTest();
}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Upload & Download Files</li>
	 *<li><B>Step:</B> UserA creates a Public community
	 *<li><B>Step:</B> UserA uploads an image file</li>
	 *<li><B>Step:</B> UserB logs in and downloads file1</li>
	 *<li><B>Step:</B> UserB logs out and UserC logs in</li>
	 *<li><B>Step:</B> UserC downloads file2</li>
	 *</ul>
	 */
	@Test (groups = {"regression","regressioncloud"} )
	public void downloadFilesFromCommFiles() {

		String testName = ui.startTest();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
                                     .comFile(true)
                                     .extension(".jpg")
                                     .build();
		
		BaseFile fileB = new BaseFile.Builder(Data.getData().file2)
		                             .comFile(true)
                                     .extension(".jpg")
                                     .build();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("Community Files - download image files ")
                                                   .build();
		
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
	
		log.info("INFO: Log into Communities");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		this.closeGuidedTourBox();
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
				
		log.info("INFO: Select Files from the navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui);
				
		log.info("INFO: Add a new file: " + fileA.getName());
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
			fileA.upload(ui,gkc);
		else
			fileA.upload(ui);
		
		log.info("INFO: Add a new file: " + fileB.getName());
		if(!cfg.getSecurityType().equalsIgnoreCase("false"))
			fileB.upload(ui,gkc);
		else
			fileB.upload(ui);
			
				
		log.info("INFO: Log out as userA: " + testUser1.getDisplayName());
		ui.logout();
		ui.close(cfg);
				
		log.info("INFO: Log in as userB: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser2);
		
		this.closeGuidedTourBox();
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Select Files from the navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
				
		log.info("INFO: Open and download file");
		ui.download(fileA);
		
		log.info("INFO: Check if file was downloaded");
		try {
			ui.verifyFileDownloaded(fileA.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		log.info("INFO: Log out as userB: " + testUser2.getDisplayName());
		ui.logout();
		ui.close(cfg);
				
		log.info("INFO: Log in as userC: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUser3);
		
		this.closeGuidedTourBox();
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(ui);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(ui);
				
		log.info("INFO: Open and download file");
		ui.download(fileB);
		
		
		log.info("INFO: Check if file was downloaded");
		try {
			ui.verifyFileDownloaded(fileB.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		ui.endTest();	

	}
	
	private void closeGuidedTourBox(){
		log.info("If GateKeeper setting for Guided Tour is enabled, close the Community Guided Tour popup window to unblock the 'Start from New' menu item");
		if(commUI.checkGKSetting(Data.getData().gk_GuidedTourComm_flag)){
			
			log.info("INFO: If the guided tour pop-up box appears, close it");
			commUI.closeGuidedTourPopup();
		}
	}
}
