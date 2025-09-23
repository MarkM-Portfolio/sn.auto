package com.ibm.conn.auto.tests.files.regression;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
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
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.FilesUI.FilesListView;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class Community_Files_Regression extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Community_Files_Regression.class);
	private FilesUI ui;
	private CommunitiesUI comUI;
	private TestConfigCustom cfg;
	private String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();

		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		comUI = CommunitiesUI.getGui(cfg.getProductName(), driver);		
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Upload a file and delete it as a user besides the owner</li>
	 * <li><B>Step:</B> Login with userA 
	 * <li><B>Step:</B> Create a community 
	 * <li><B>Step:</B> Logout and login with userB 
	 * <li><B>Step:</B> Navigate to the community 
	 * <li><B>Step:</B> Add a file with the userB.
	 * <li><B>Step:</B> Delete the file uploaded by userB
	 * <li><B>Step:</B> Attempt to delete the file from userA
	 * <li><B>Verify:</B> Validate that userB is unable to delete the file uploaded by userA
	 * </ul>
	 */
	@Test(groups = { "regression", "regressioncloud"} , enabled=true )
	public void communityFilesTrashMember() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		User testUserA = cfg.getUserAllocator().getUser();
		User testUserB = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUserB))
													.build();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		 							 .comFile(true)
									 .extension(".jpg")
									 .build();
		
		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
		 							 .comFile(true)
									 .extension(".jpg")
		 							 .build();		

		//create community
		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("INFO: Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Component and Log In as: " + testUserA.getDisplayName());
		log.info("Load Component and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);

		//navigate to the API community
		logger.strongStep("INFO: Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUI);	

		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		//load file
		logger.strongStep("INFO: Upload a file to the community");
		log.info("INFO: Upload a file to the community");
		fileA.upload(ui);
		
		//Logout
		ui.logout();
		
		//ui.login as second user
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUserB);
		
		//go to the community im a member
		logger.strongStep("INFO: Select I'm a Member from left communities view menu");
		log.info("INFO: Select I'm a Member from left communities view menu");
		ui.clickLinkWait(CommunitiesUI.getCommunityView(Community_View_Menu.IM_A_MEMBER));
		
		//open community via link
		logger.strongStep("INFO: Select community");
		log.info("INFO: Select community");
		ui.clickLink("css=div[aria-label=\'"+community.getName()+"\']");
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		log.info("INFO: Upload file" + fileB.getName());
		fileB.upload(ui);
	
		//Select the list view
		ui.clickLinkWait(FilesListView.LIST.getActivateSelector());
		ui.fluentWaitPresent(FilesListView.LIST.getIsActiveSelector());

		logger.strongStep("INFO: Move "+fileB+" to trash");
		log.info("INFO: Move "+fileB+" to trash");
		fileB.trash(ui);
		
		logger.strongStep("INFO: Try to move "+fileA+" to trash");
		log.info("INFO: Try to Move "+fileA+" to trash");
		boolean result = fileA.trashNotPresent(ui);
				
		if(result){
			log.error("ERROR: "+ testUserB.getDisplayName() +" is not able to delete files uploaded by "+testUserA.getDisplayName());
		}
		
		Assert.assertTrue(result, "User "+testUserB.getDisplayName()+" can delete files created by"+testUserA.getDisplayName());

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Using the community and files from {@link #communityFilesTrashSetup()}, delete and restore both files as the owner</li>
	 * <li><B>Step:</B> Log in as the second user</li>
	 * <li><B>Step:</B> Select the community</li>
	 * <li><B>Step:</B> Choose to load the file widget</li>
	 * <li><B>Step:</B> As userA (owner) delete both files</li>
	 * <li><B>Verify:</B> Verify that they have being moved to trash</li>
	 * <li><B>Step:</B> As userA (owner) restore both files</li>
	 * <li><B>Verify:</B> Verify that they are no longer in the trash view</li>
	 * </ul>
	 */
	@Test(groups = {"regression", "regressioncloud",} , enabled=true )
	public void communityFilesTrashOwnerRestore() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		User testUserA = cfg.getUserAllocator().getUser();
		User testUserB = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUserB))
													.build();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();
		
		//create community
		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("INFO: Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		// Load the component and login
		logger.strongStep("Load Component and Log In as: " + testUserB.getDisplayName());
		log.info("Load Component and Log In as: " + testUserB.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserB);

		//navigate to the API community
		logger.strongStep("INFO: Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUI);
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		logger.strongStep("INFO: Upload file "+fileA.getName());
		log.info("INFO: Upload file "+fileA.getName());
		fileA.upload(ui);

		logger.strongStep("INFO: Upload file "+fileB.getName());
		log.info("INFO: Upload file "+fileB.getName());
		fileB.upload(ui);
				
		logger.strongStep("Log Out as: " + testUserB.getDisplayName());
		log.info("Log Out as: " + testUserB.getDisplayName());
		ui.logout();
				
		//ui.login as first user
		logger.strongStep("Load Component and Log In as: " + testUserA.getDisplayName());
		log.info("Load Component and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUserA);	//ui.login as community owner
		
		//open community via link
		logger.strongStep("INFO: Select community");
		log.info("INFO: Select community");
		ui.clickLink("css=div[aria-label=\'"+community.getName()+"\']");
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		//Select the list view
		ui.clickLinkWait(FilesListView.LIST.getActivateSelector());
		ui.fluentWaitPresent(FilesListView.LIST.getIsActiveSelector());
		
		//Move file A & file B to the trash and verify that this has being moved to this view
		logger.strongStep("INFO: Move "+fileA+" to trash");
		log.info("INFO: Move "+fileA+" to trash");
		fileA.trash(ui);
		
		logger.strongStep("INFO: Move "+fileB+" to trash");
		log.info("INFO: Move "+fileB+" to trash");
		fileB.trash(ui);
		
		//Restore the file(s) to the community and verify they are present
		logger.strongStep("INFO: Restore "+fileB);
		log.info("INFO: Restore "+fileB);
		fileB.restore(ui);
		
		logger.strongStep("INFO: Restore "+fileA);
		log.info("INFO: Restore "+fileA);
		fileA.restore(ui);
			

		ui.endTest();
	}
	
	/**
	 *<ul>
	 * <li><B>Info:</B> Using the community and files from {@link #communityFilesTrashSetup()}, permanently delete both files as the owner</li>
	 * <li><B>Step:</B> Select the community from the correct view</li>
	 * <li><B>Step:</B> Choose to load the file widget</li>
	 * <li><B>Step:</B> As userA (owner) delete both files</li>
	 * <li><B>Verify:</B> Verify that they have being moved to trash</li> 
	 * <li><B>Step:</B> As userA (owner) delete both files</li>
	 * <li><B>Verify:</B> Verify that they are no longer in the trash view</li>
	 *</ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void communityFilesTrashOwnerDeleteIndividuals() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		User testUserA = cfg.getUserAllocator().getUser();
		User testUserB = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUserB))
													.build();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		//create community
		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("INFO: Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Component and Log In as: " + testUserA.getDisplayName());
		log.info("Load Component and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);

		//navigate to the API community
		logger.strongStep("INFO: Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUI);	
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
				
		logger.strongStep("INFO: Uploading"+fileA.getName());
		log.info("INFO: Uploading"+fileA.getName());
		fileA.upload(ui);
	
		logger.strongStep("INFO: Uploading"+fileB.getName());
		log.info("INFO: Uploading"+fileB.getName());
		fileB.upload(ui);
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);	
		
		logger.strongStep("Log Out as: " + testUserA.getDisplayName());
		log.info("Log Out as: " + testUserA.getDisplayName());
		ui.logout();
		
		//ui.login as community owner
		logger.strongStep("Load Component and Log In as: " + testUserA.getDisplayName());
		log.info("Load Component and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUserA);	
			
		//open community via link
		logger.strongStep("INFO: Select community");
		log.info("INFO: Select community");
		ui.clickLink("css=div[aria-label=\'"+community.getName()+"\']");
					
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		//Select the list view
		logger.strongStep("INFO: Click and verify on list view of Files is present");
		log.info("INFO: Click and verify on list view of Files is present");
		ui.clickLinkWait(FilesListView.LIST.getActivateSelector());
		ui.fluentWaitPresent(FilesListView.LIST.getIsActiveSelector());

		//Move file B to the trash and verify that this has being moved to this view
		logger.strongStep("INFO: Move "+fileA.getName()+" to trash");
		log.info("INFO: Move "+fileA.getName()+" to trash");
		fileA.trash(ui);
		
		logger.strongStep("INFO: Move "+fileB.getName()+" to trash");
		log.info("INFO: Move "+fileB.getName()+" to trash");
		fileB.trash(ui);
		
		//delete the files from the community and verify that they no longer exists
		logger.strongStep("INFO: Delete "+fileB.getName());
		log.info("INFO: Delete "+fileB.getName());
		fileB.delete(ui);
		
		logger.strongStep("INFO: Delete "+fileA.getName());
		log.info("INFO: Delete "+fileA.getName());
		fileA.delete(ui);
	
		ui.endTest();
	}
	/**
	 * <ul>
	 * <li><B>Info:</B> Using the community and files from {@link #communityFilesTrashSetup()}, delete both files as the community owner</li>
	 * <li><B>Step:</B> Select the community from the correct view</li>
	 * <li><B>Step:</B> Choose to load the file widget</li>
	 * <li><B>Step:</B> As userA (owner) delete both files</li>
	 * <li><B>Verify:</B> Verify that they have being moved to trash</li>
	 * <li><B>Step:</B> As userA (owner) delete both files</li>
	 * <li><B>Verify:</B> Verify that they are no longer in the trash view</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void communityFilesTrashOwnerEmptyTrash() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		User testUserA = cfg.getUserAllocator().getUser();
		User testUserB = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUserB))
													.build();
	
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		
		//create community
		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("INFO: Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Component and Log In as: " + testUserA.getDisplayName());
		log.info("Load Component and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);

		//navigate to the API community
		logger.strongStep("INFO: Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUI);	
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		logger.strongStep("INFO: Uploading "+fileA.getName());
		log.info("INFO: Uploading "+fileA.getName());
		fileA.upload(ui);
		
		logger.strongStep("INFO: Uploading "+fileB.getName());
		log.info("INFO: Uploading "+fileB.getName());
		fileB.upload(ui);
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		ui.logout();
		
		//ui.login as community owner
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUserA);	
		
		//open community via link
		logger.strongStep("INFO: Select community");
		log.info("INFO: Select community");
		ui.clickLink("css=div[aria-label=\'"+community.getName()+"\']");
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		//Select the list view
		ui.clickLinkWait(FilesListView.LIST.getActivateSelector());
		ui.fluentWaitPresent(FilesListView.LIST.getIsActiveSelector());

		//Move file B to the trash and verify that this has being moved to this view
		logger.strongStep("INFO: Move "+fileA.getName()+" to trash");
		log.info("INFO: Move "+fileA.getName()+" to trash");
		fileA.trash(ui);
		
		logger.strongStep("INFO: Move "+fileB.getName()+" to trash");
		log.info("INFO: Move "+fileB.getName()+" to trash");
		fileB.trash(ui);

		//go to view trash link
		ui.clickLink(FilesUIConstants.CommunityViewTrashLink);
				
		//empty the trash
		ui.clickLink(FilesUIConstants.EmptyTrash);
		
		ui.clickButton(Data.getData().buttonOK);

		ui.fluentWaitTextPresent("All files in the trash have been permanently deleted");
				
		//verify files are deleted
		Assert.assertTrue(driver.isTextNotPresent(fileA.getName()));
		Assert.assertTrue(driver.isTextNotPresent(fileB.getName()));

		ui.endTest();
	}

	/**
	 *<ul>
	 * <li><B>Info:</B> Test that selected files are viewable on the page after selection</li>
	 * <li><B>Step:</B>Create a community with title, tag, handle and description</li>
	 * <li><B>Step:</B>Add a member</li>
	 * <li><B>Step:</B>Upload 12 files to this community</li>
	 * <li><B>Step:</B>Select to view only 10 files per page</li>
	 * <li><B>Verify:</B>Verify that there are only 10 files on the first page</li>
	 * <li><B>Step:</B>Click on the next link</li>
	 * <li><B>Verify:</B>Verify that the remaining 2 files are present on the second page </li>
	 * <li><B>Step:</B>Click on previous link</li>
	 * <li><B>Verify:</B>Verify the webpage navigates to the previous link</li>
	 * Note:- Disabling this test case as the paging feature for files has been removed
	 * </ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=false )
	public void communityFilesTrashPaging() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		User testUserA = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		BaseFile[] file = new BaseFile[15];

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.description("Test description for testcase " + testName)
													.build();
		
		//create community
		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("INFO: Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Component and Log In as: " + testUserA.getDisplayName());
		log.info("Load Component and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);

		//navigate to the API community
		logger.strongStep("INFO: Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUI);
			
		//go to the community files
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		for(int x = 1; x < 13; x = x+1) {
			
			file[x] = new BaseFile.Builder(Data.getData().file2)
			 					  .extension(".jpg")
			 					  .rename(x + Helper.genDateBasedRand())
			 					  .comFile(true)
			 					  .build();
			
			//upload file
			logger.strongStep("INFO: Upload file " + file[x].getRename());
			log.info("INFO: Upload file " + file[x].getRename());
			file[x].upload(ui);
		}
				
		//click on 10 per page
		logger.strongStep("INFO: Select to display only 10 files");
		log.info("INFO: Select to display only 10 files");	
		Element tenPerPage = driver.getSingleElement(FilesUIConstants.selectShow10);
		if(tenPerPage.getAttribute("aria-pressed").contentEquals("false")){
			logger.strongStep("INFO: 10 per page is not selected so selecting it");
			log.info("INFO: 10 per page is not selected so selecting it");
			tenPerPage.click();
		}

		//Verify that all files are appearing in both the first page and the second page
		for(int i=3; i<13; i++){
			logger.strongStep("INFO: Find file name " + file[i].getRename());
			log.info("INFO: Find file name " + file[i].getRename());
			Assert.assertTrue(driver.isTextPresent(file[i].getRename()), 
							  "ERROR: File " + file[i].getRename() + " not found");
		}
		
		//Validate that both the top and bottom next page links are enabled
		driver.isElementPresent(FilesUIConstants.nextLinkTop);
		driver.isElementPresent(FilesUIConstants.nextLinkBottom);
		
		//Validate on the next page
		logger.strongStep("INFO: Select next link");
		log.info("INFO: Select next link");
		ui.clickLinkWait(FilesUIConstants.nextLinkTop);
		
		//Validate the two files on next page
		logger.strongStep("INFO: Validate the two remaining files");
		log.info("INFO: Validate the two remaining files");
		Assert.assertTrue(driver.isTextPresent(file[1].getRename()), 
						  "ERROR: File " + file[1].getRename() + " not found");
		Assert.assertTrue(driver.isTextPresent(file[2].getRename()), 
						  "ERROR: File " + file[2].getRename() + " not found");
		
		//Validate on the previous page
		logger.strongStep("INFO: Select previous link");
		log.info("INFO: Select previous link");
		ui.clickLinkWait(FilesUIConstants.previousLinkTop);

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Upload 3 files</li>
	 * <li><B>Step:</B> Create a community with title, tag, handle and description</li>
	 * <li><B>Step:</B> Upload 3 files to this community</li>
	 * <li><B>Verify:</B> Verify the files have being uploaded</li>
	 * </ul>
	 */
	@Test(groups = {"regression", "regressioncloud"})
	public void communitiesThreeFiles() throws Exception{
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		User testUserA = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .description("Test description for testcase " + testName)
												   .build();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
									 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		BaseFile fileC = new BaseFile.Builder(Data.getData().file3)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		//create community
		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("INFO: Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Component and Log In as: " + testUserA.getDisplayName());
		log.info("Load Component and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);

		//navigate to the API community
		logger.strongStep("INFO: Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUI);
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);

		logger.strongStep("INFO: Upload file"+fileA.getName());
		log.info("INFO: Upload file"+fileA.getName());
		fileA.upload(ui);

		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		logger.strongStep("INFO: Upload file"+fileB.getName());
		log.info("INFO: Upload file"+fileB.getName());
		fileB.upload(ui);

		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		logger.strongStep("INFO: Upload file"+fileC.getName());
		log.info("INFO: Upload file"+fileC.getName());
		fileC.upload(ui);	

		
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Using the community and files from {@link #createCommunityWithFiles()}, and download them as another user</li>
	 *<li><B>Step:</B> Log in as first user</li>
	 *<li><B>Step:</B> Upload multiple as first user</li>
	 *<li><B>Step:</B> Logout as first user</li>
	 * <li><B>Step:</B> Log in as the second user</li>
	 * <li><B>Step:</B> SetupDirectory() will delete any files in the directory to ensure that the files in this directory are from this test</li>
	 * <li><B>Step:</B> Select the community from the correct view</li>
	 * <li><B>Step:</B> Choose to load the file widget</li>
	 * <li><B>Step:</B> Select from detailed view</li>
	 * <li><B>Step:</B> Download the selected file</li>
	 * <li><B>Verify:</B> Verify that the directory contains the correct file</li>
	 * </ul>
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void communityFilesMultipleUploadAndSingleDownloadInDetailedView() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		User testUserA = cfg.getUserAllocator().getUser();
		User testUserB = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
												   .description("Test description for testcase " + testName)
												   .addMember(new Member(CommunityRole.MEMBERS, testUserB))
												   .build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		//create community
		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("INFO: Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Component and Log In as: " + testUserA.getDisplayName());
		log.info("Load Component and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);

		//navigate to the API community
		logger.strongStep("INFO: Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUI);	
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		logger.strongStep("INFO: Uploading"+fileA.getName());
		log.info("INFO: Uploading"+fileA.getName());
		fileA.upload(ui);
		
		logger.strongStep("INFO: Uploading"+fileB.getName());
		log.info("INFO: Uploading"+fileB.getName());
		fileB.upload(ui);
		
		logger.strongStep("Log Out as: " + testUserA.getDisplayName());
		log.info("Log Out as: " + testUserA.getDisplayName());
		ui.logout();
		
		//Load the component
		logger.strongStep("Load Component");
		log.info("Load Component");
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		
		//ui.login as userA
		logger.strongStep("Log In as: " + testUserB.getDisplayName());
		log.info("Log In as: " + testUserB.getDisplayName());
		ui.login(testUserB);

		//Set the directory for the download and ensure that it is empty
		if (testConfig.serverIsLegacyGrid())  {
			ui.setupDirectory();
		}
		
		//go to the community im a member
		logger.strongStep("INFO: Select I'm a Member from left communities view menu");
		log.info("INFO: Select I'm a Member from left communities view menu");
		ui.clickLinkWait(CommunitiesUI.getCommunityView(Community_View_Menu.IM_A_MEMBER));
		
		//open community via link
		logger.strongStep("INFO: Select community");
		log.info("INFO: Select community");
		ui.clickLink("css=div[aria-label=\'"+community.getName()+"\']");
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		//select view and then select the file to download
		ui.changeViewAndSelectFile("Detail");
		
		//Now perform a download
		logger.strongStep("INFO: Downaloading "+fileA.getName());
		log.info("INFO: Downaloading "+fileA.getName());
		fileA.download(ui);
		
		//Verify the file has being downloaded - localhost currently
		logger.strongStep("INFO: Verify downloaded "+fileA.getName());
		log.info("INFO: Verify downloaded "+fileA.getName());
		ui.verifyFileDownloaded(fileA.getName());
				
		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Using the community and files from {@link #createCommunityWithFiles()}, 
	 * <li><B>Step:</B> Login as the first user</li>
	 * <li><B>Step:</B> Upload two files as the first user</li>
	 * <li><B>Step:</B> Logout as the first user</li>
	 * <li><B>Step:</B> Login as the second user</li>
	 * <li><B>Step:</B> SetupDirectory() will delete any files in the directory to ensure that the files in this directory are from this test</li>
	 * <li><B>Step:</B> Select the community from the correct view</li>
	 * <li><B>Step:</B> Choose to load the file widget</li>
	 * <li><B>Step:</B> Perform a file upload</li>
	 * <li><B>Step:</B> Select from list view</li>
	 * <li><B>Step:</B> Download the selected file</li>
	 * <li><B>Verify:</B> Verify that the directory contains the correct file</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void communityFilesMultipleUploadAndSingleDownloadInListView() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		User testUserA = cfg.getUserAllocator().getUser();
		User testUserB = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUserB))
													.build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .build();

		
		//create community
		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("INFO: Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Component and Log In as: " + testUserA.getDisplayName());
		log.info("Load Component and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);

		//navigate to the API community
		logger.strongStep("INFO: Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUI);	
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
		
		logger.strongStep("INFO: Uploading"+fileA.getName());
		log.info("INFO: Uploading"+fileA.getName());
		fileA.upload(ui);
		
		logger.strongStep("INFO: Uploading"+fileB.getName());
		log.info("INFO: Uploading"+fileB.getName());
		fileB.upload(ui);
		
		logger.strongStep("Log Out as: " + testUserA.getDisplayName());
		log.info("Log Out as: " + testUserA.getDisplayName());
		ui.logout();
		
		//Load the component
		logger.strongStep("Load Component");
		log.info("Load Component");
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		
		//ui.login as userA
		logger.strongStep("Log In as: " + testUserB.getDisplayName());
		log.info(" Log In as: " + testUserB.getDisplayName());
		ui.login(testUserB);
		
		//Set the directory for the download and ensure that it is empty
		if (testConfig.serverIsLegacyGrid())  {
			ui.setupDirectory();
		}
		
		//go to the community im a member
		logger.strongStep("INFO: Select I'm a Member from left communities view menu");
		log.info("INFO: Select I'm a Member from left communities view menu");
		ui.clickLinkWait(CommunitiesUI.getCommunityView(Community_View_Menu.IM_A_MEMBER));
		
		//open community via link
		logger.strongStep("INFO: Select community");
		log.info("INFO: Select community");
		ui.clickLink("css=div[aria-label=\'"+community.getName()+"\']");
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
						
		//select view and then select the file to download
		logger.strongStep("select view and then select the file to download");
		log.info("select view and then select the file to download");
		ui.changeViewAndSelectFile("List");
		
		//Now perform a download
		logger.strongStep("INFO: Downaloading "+fileA.getName());
		log.info("INFO: Downaloading "+fileA.getName());
		fileA.download(ui);
		
		//Verify the file has being downloaded - localhost currently
		logger.strongStep("INFO: Verify downloaded "+fileA.getName());
		log.info("INFO: Verify downloaded "+fileA.getName());
		ui.verifyFileDownloaded(fileA.getName());
				
		ui.endTest();
	}
	
	/**
	 *<ul>
	 * <li><B>Info:</B> Using the community and files from {@link #createCommunityWithMultipleFilesUploaded()}, and download them as a Zip file and extract another user</li>
	 * <li><B>Step:</B> Log in as the second user</li>
	 * <li><B>Step:</B> SetupDirectory() will delete any files in the directory to ensure that the files in this directory are from this test</li>
	 * <li><B>Step:</B> Select the community from the correct view</li>
	 * <li><B>Step:</B> Choose to load the file widget</li>
	 * <li><B>Step:</B> Multiple file upload was performed in the setup</li>
	 * <li><B>Step:</B> Select all files in the detailed view</li>
	 * <li><B>Step:</B> Download the selected files as a compressed file</li>
	 * <li><B>Step:</B> Extracted the compressed file into a new directory</li>
	 * <li><B>Verify:</B> Verify that the extracted zip contains file</li>
	 *</ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void communityFilesBulkDownloadAllZipDetailedView() throws Exception{
		communityFilesBulkDownloadZip("communityFilesBulkDownloadAllZipDetailedView",
				true, FilesListView.DETAILS);
	}
	
	/**
	 *<ul>
	 * <li><B>Info:</B> Using the community and files from {@link #createCommunityWithMultipleFilesUploaded()}, and download them as a Zip file and extract another user</li>
	 * <li><B>Step:</B> Log in as the second user</li>
	 * <li><B>Step:</B> SetupDirectory() will delete any files in the directory to ensure that the files in this directory are from this test</li>
	 * <li><B>Step:</B> Select the community from the correct view</li>
	 * <li><B>Step:</B> Choose to load the file widget</li>
	 * <li><B>Step:</B> Multiple file upload was performed in the setup</li>
	 * <li><B>Step:</B> Select two files in the detailed view</li>
	 * <li><B>Step:</B> Download the selected files as a compressed file</li>
	 * <li><B>Step:</B> Extracted the compressed file into a new directory</li>
	 * <li><B>Verify:</B> Verify that the extracted zip contains file</li>
	 *</ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void communityFilesBulkDownloadZipDetailedView() throws Exception{
		communityFilesBulkDownloadZip("communityFilesBulkDownloadZipDetailedView",
				false, FilesListView.DETAILS);
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Using the community and files from  {@link #createCommunityWithMultipleFilesUploaded()}, download zipped files and extract them</li>
	 * <li><B>Step:</B> Login as the second user</li>
	 * <li><B>Step:</B> SetupDirectory() will delete any files in the directory to ensure that the files in this directory are from this test</li>
	 * <li><B>Step:</B> Select the community from the correct view</li>
	 * <li><B>Step:</B> Choose to load the file widget</li>
	 * <li><B>Step:</B> Multiple file upload was performed in the setup</li>
	 * <li><B>Step:</B> Select all files in the list view </li>
	 * <li><B>Step:</B> Download the selected files as a compressed file</li>
	 * <li><B>Step:</B> Extract the compressed file into a new directory</li>
	 * <li><B>Verify:</B> Verify that the extracted zip contains file</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void communityFilesBulkDownloadAllZipListView() throws Exception{
		communityFilesBulkDownloadZip("communityFilesBulkDownloadAllZipDetailedView",
				true, FilesListView.LIST);
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Using the community and files from  {@link #createCommunityWithMultipleFilesUploaded()}, download zipped files and extract them</li>
	 * <li><B>Step:</B> Login as the second user</li>
	 * <li><B>Step:</B> SetupDirectory() will delete any files in the directory to ensure that the files in this directory are from this test</li>
	 * <li><B>Step:</B> Select the community from the correct view</li>
	 * <li><B>Step:</B> Choose to load the file widget</li>
	 * <li><B>Step:</B> Multiple file upload was performed in the setup</li>
	 * <li><B>Step:</B> Select two files in the list view </li>
	 * <li><B>Step:</B> Download the selected files as a compressed file</li>
	 * <li><B>Step:</B> Extract the compressed file into a new directory</li>
	 * <li><B>Verify:</B> Verify that the extracted zip contains file</li>
	 * </ul>
	 * @throws Exception
	 */
	@Test(groups = {"regression", "regressioncloud"} , enabled=true )
	public void communityFilesBulkDownloadZipListView() throws Exception{
		communityFilesBulkDownloadZip("communityFilesBulkDownloadAllZipDetailedView",
				false, FilesListView.LIST);
	}
	
	public void communityFilesBulkDownloadZip(String testName, boolean downloadAll, FilesListView view) throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		User testUserA = cfg.getUserAllocator().getUser();
		User testUserB = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());
		
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.description("Test description for testcase " + testName)
													.addMember(new Member(CommunityRole.MEMBERS, testUserB))
													.build();

		BaseFile fileA = new BaseFile.Builder(Data.getData().file1)
		 							 .comFile(true)
		 							 .tags(testName + "_" + Helper.genDateBasedRand())
		 							 .extension(".jpg")
		 							 .rename(Helper.genStrongRand())
		 							 .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file7)
		 							 .comFile(true)
		 							 .extension(".jpg")
		 							 .rename(Helper.genStrongRand())
		 							 .build();
		
		BaseFile fileC = new BaseFile.Builder(Data.getData().file2)
									.comFile(true)
									.extension(".jpg")
									.rename(Helper.genStrongRand())
									.build();
		
		String zippedFile = "files.zip";
		
		//create community
		logger.strongStep("INFO: Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("INFO: Get UUID of community");
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load Component and Log In as: " + testUserA.getDisplayName());
		log.info("Load Component and Log In as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);

		//navigate to the API community
		logger.strongStep("INFO: Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUI);	
		
		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);
				
		if (!downloadAll) {		
			ui.multipleFileUpload(fileA, fileB, fileC);
		} else {
			ui.multipleFileUpload(fileA, fileB);
		}
		
		logger.strongStep("Log Out as: " + testUserA.getDisplayName());
		log.info("Log Out as: " + testUserA.getDisplayName());
		ui.logout();
		
		//Load the component
		logger.strongStep("Load Component and Log In as: " + testUserB.getDisplayName());
		log.info("Load Component and Log In as: " + testUserB.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities, true);
		ui.login(testUserB);

		//Set the directory for the download and ensure that it is empty
		if (testConfig.serverIsLegacyGrid())  {
			ui.setupDirectory();
		}

		//go to the community im a member
		logger.strongStep("INFO: Select I'm a Member from left communities view menu");
		log.info("INFO: Select I'm a Member from left communities view menu");
		ui.clickLinkWait(CommunitiesUI.getCommunityView(Community_View_Menu.IM_A_MEMBER));
		
		//open community via link
		logger.strongStep("INFO: Select community");
		log.info("INFO: Select community");
		ui.clickLink("css=div[aria-label=\'"+community.getName()+"\']");

		//Select Files from left menu
		logger.strongStep("INFO: Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_LeftNav_Menu.FILES.select(comUI);

		if (downloadAll) {
			//select view and then select the file to download
			ui.changeViewAndSelectAllFiles(view);

			//click on the download button and then agree to compress the files into a zip
			ui.downloadAllAsCompressedFile();
		} else {
					
			//Select the list view
			ui.clickLinkWait(view.getActivateSelector());
			ui.clickLinkWait(view.getIsActiveSelector());
			ui.fluentWaitPresent(view.getIsActiveSelector());

			//Select file A and B but not C
			ui.selectFileCheckmark(fileA);
			ui.selectFileCheckmark(fileB);

			//click on the download button and then agree to compress the files into a zip
			ui.downloadAsCompressedFile();
		}
		
		//Verify the file has being downloaded - localhost currently
		ui.verifyFileDownloaded(zippedFile);
		
		//unzip the downloaded zipped file to a new folder and then verify the contains
		if(testConfig.getServerHost().contains("localhost")){
			ui.unzipFileAndVerify(zippedFile, Data.getData().localoutputFolder);
		}else{
			ui.unzipFileAndVerify(zippedFile, Data.getData().outputFolder);
		}
		
		ui.endTest();
	}

	
}
