package com.ibm.conn.auto.tests.GDPR;

import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.appobjects.role.FilesRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Community_MegaMenu_Menu;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.util.menu.Files_Folder_Dropdown_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ICBaseUI;
import com.ibm.conn.auto.webui.cloud.FilesUICloud;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

@Deprecated //The GDPR component is obsolete now, hence this class has been deprecated
public class Files_GDPR_DataPop extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(Files_GDPR_DataPop.class);
	private TestConfigCustom cfg; ICBaseUI ui;
	private CommunitiesUI commUI;
	private FilesUI filesUI;
	private FileViewerUI fileviewerUI;
	private HomepageUI hUI;
	private APICommunitiesHandler apiCommOwner1,apiCommOwner2;
	private String serverURL;
	private User testUser1, testUser2;
	private boolean isOnPremise;
	private GatekeeperConfig gkc;


	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		commUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
		fileviewerUI = FileViewerUI.getGui(cfg.getProductName(), driver);
		hUI = HomepageUI.getGui(cfg.getProductName(), driver);

		//Load Users		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
						
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		URLConstants.setServerURL(serverURL);
		
		//check to see if environment is on-premises or on the cloud
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
				
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		apiCommOwner1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		apiCommOwner2 = new APICommunitiesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
	}

	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Edit Uploaded File</li>
	 *<li><B>Info:</B> UserA creates a community</li>
	 *<li><B>Info:</B> UserA uploads a file to the community files widget</li>  
	 *<li><B>Info:</B> UserA edits the file name</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAEditsFileInCommFiles() {
		
		String testName = commUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
                                   .comFile(true)
                                   .extension(".jpg")
                                   .rename(Helper.genDateBasedRand())
                                   .build();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                 .access(Access.PUBLIC)
                                                 .description("GDPR data pop: Community Files - edit file name")
                                                 .build(); 
		       
        log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
	
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		this.uploadFileInCommFiles(community, file);
								
		this.editFileProperties();
		
		commUI.endTest();
	
	}
		
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Upload & Download Files</li>
	 *<li><B>Step:</B> UserA creates a Public community</li>
	 *<li><B>Step:</B> UserA uploads a file</li>
	 *<li><B>Step:</B> UserB logs in and downloads the file</li>
	 *</ul>
	 */
	@Test (groups = {"regression","regressioncloud"}, enabled=false)
	public void userBDownloadsFileFromCommFiles() {

		String testName = commUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file3)
                                    .comFile(true)
                                    .extension(".jpg")
                                    .build();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                  .access(Access.PUBLIC)
                                                  .description("GDPR data pop: Community Files - download image file test ")
                                                  .build();
		
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
	
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		this.uploadFileInCommFiles(community, file);
						
		log.info("INFO: Log out as userA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
				
		log.info("INFO: Log in as userB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities, true);
		commUI.login(testUser2);
				
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Select Files from the navigation menu");
		Community_TabbedNav_Menu.FILES.select(commUI);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);
				
		log.info("INFO: Open and download file");
		filesUI.download(file);
				
		log.info("INFO: Log out as userB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);		

		commUI.endTest();	

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - Upload & Download Files</li>
	 *<li><B>Step:</B> UserA uploads a file & makes it public</li>
	 *<li><B>Step:</B> UserB logs in and downloads the file</li>
	 *</ul>
	 */
	@Test (groups = {"regression","regressioncloud"}, enabled=false)
	public void userBDownloadsFilesFromStandaloneFiles() {

		filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file5)
			                         .extension(".jpg")
			                         .rename(Helper.genDateBasedRand())
			                         .build();
		
		log.info("INFO: Log into Files as UserA: " + testUser1.getDisplayName());
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);
		
		this.standaloneFilesUploadFile(file);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);

		log.info("INFO: Make file1 Public - share file with everyone in the organization");
		filesUI.share(file);
		
		log.info("INFO: Log out as userA: " + testUser1.getDisplayName());
		filesUI.logout();
		filesUI.close(cfg);
				
		log.info("INFO: Log in as userB: " + testUser2.getDisplayName());
		filesUI.loadComponent(Data.getData().ComponentFiles, true);
		filesUI.login(testUser2);
		
		this.displayDetailsPublicFilesView();
				
		log.info("INFO: Open and download file");
		filesUI.download(file);
				
		log.info("INFO: Log out as userB: " + testUser2.getDisplayName());
		filesUI.logout();
		filesUI.close(cfg);		

		filesUI.endTest();	

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - Upload & Download Files</li>
	 *<li><B>Step:</B> UserB uploads a file & makes it public</li>
	 *<li><B>Step:</B> UserA logs in and downloads the file</li>
	 *</ul>
	 */
	@Test (groups = {"regression","regressioncloud"}, enabled=false)
	public void userADownloadsFileFromStandaloneFiles() {

		filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file6)
			                         .extension(".jpg")
			                         .rename(Helper.genDateBasedRand())
			                         .build();
		
		log.info("INFO: Log into Files as UserB: " + testUser2.getDisplayName());
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser2);
		
		this.standaloneFilesUploadFile(file);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);

		log.info("INFO: Make file1 Public - share file with everyone in the organization");
		filesUI.share(file);
		
		log.info("INFO: Log out as UserB: " + testUser2.getDisplayName());
		filesUI.logout();
		filesUI.close(cfg);
				
		log.info("INFO: Log in as userA: " + testUser1.getDisplayName());
		filesUI.loadComponent(Data.getData().ComponentFiles, true);
		filesUI.login(testUser1);
		
		this.displayDetailsPublicFilesView();
				
		log.info("INFO: Open and download file");
		filesUI.download(file);
			
		log.info("INFO: Log out as UserA: " + testUser1.getDisplayName());
		filesUI.logout();
		filesUI.close(cfg);		

		filesUI.endTest();	

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - Share File with Everyone in the Organization</li>
	 *<li><B>Step:</B> UserA uploads (2) files</li>
	 *<li><B>Step:</B> UserA shares both files with everyone in the organization</li>
	 *</ul>
	 */
	@Test (groups = {"regression","regressioncloud"}, enabled=false)
	public void shareFileFromStandaloneFiles() {

		filesUI.startTest();
		
		BaseFile fileA = new BaseFile.Builder(Data.getData().file7)
                                    .extension(".jpg")
                                    .rename( filesUI.reName(Data.getData().file7))
                                    .build();

		BaseFile fileB = new BaseFile.Builder(Data.getData().file8)
		                             .extension(".jpg")
		                             .rename(filesUI.reName(Data.getData().file8))
		                             .build();

		log.info("INFO: Log into Files as UserA: " + testUser1.getDisplayName());
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);

		this.standaloneFilesUploadFile(fileA);

		log.info("INFO: Upload fileB: " + fileB.getName());
		fileB.upload(filesUI);

		log.info("INFO: Check for the upload Success message for file: " + filesUI.getUploadFileName(fileB));
		filesUI.fluentWaitPresent(FilesUICloud.fileUploadedSuccessImg);

		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);

		log.info("INFO: Make fileA Public - share file with everyone in the organization");
		filesUI.share(fileA);

		log.info("INFO: Make fileB Public - share file with everyone in the organization");
		filesUI.share(fileB);

		filesUI.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Files - Follow a File</li>
	 *<li><B>Step:</B> UserB adds a file (Public) to the stand-alone Files app</li>
	 *<li><B>Step:</B> UserA follows the file</li>
	 *</ul>
	 */
	@Test(groups = {"regression"}, enabled=false)
	public void userAFollowsFileUploadedByUserB() {
						
		filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file10)
                                    .extension(".jpg")
                                    .shareLevel(ShareLevel.EVERYONE)
                                    .rename(Helper.genDateBasedRand())
                                    .build();
				
		log.info("INFO: Login to Files as UserB: " + testUser2.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser2);
		
		this.standaloneFilesUploadFile(file);
				
		log.info("INFO: Name of the uploaded files is: " + file.getName());
				
		log.info("INFO: UserB " + testUser2.getDisplayName() + " logs out of Files");
		filesUI.logout();
		filesUI.close(cfg);
		
		log.info("INFO: UserA " + testUser1.getDisplayName() + " logs into Files");
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);
		
		this.displayDetailsPublicFilesView();
		
		log.info("INFO: Click on the uploaded file - open in FiDO");
		filesUI.clickLinkWait("link=" + file.getName());
		
		log.info("INFO: Click on the Follow link");
		fileviewerUI.selectAction(FileViewerUI.ToggleFollowingButton);
		
		filesUI.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Files - Create Folder & Upload File as UserA</li>
	 *<li><B>Step:</B> UserA uploads a file</li>
	 *<li><B>Step:</B> UserA creates a folder</li>
	 *<li><B>Step:</B> UserA adds the file to the folder</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userACreatesFolderUploadsFileFromStandaloneFiles() {
						
		filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file11)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRand())
                                    .build();
		
		BaseFolder folder = new BaseFolder.Builder(Data.getData().FolderName + Helper.genDateBasedRand())
		                                  .description(Data.getData().FolderDescription)
		                                  .build();
				
		log.info("INFO: Login to Files as UserA: " + testUser1.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();
		
		log.info("INFO: Upload a file");
		file.upload(filesUI);
		
		log.info("INFO: Click on the All Folders view");
		filesUI.clickLinkWait(FilesUIConstants.AllFoldersLeftMenu);

		log.info("INFO: Click on the My Folders view");
		filesUI.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu);
		
		log.info("INFO: Create a folder");
		filesUI.create(folder);		
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);
		
		log.info("INFO: Add file to folder");
		file.addToFolder(filesUI, folder);
				
		filesUI.endTest();	
		
	}
	

	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Files - Like a File as UserA</li>
	 *<li><B>Step:</B> UserA uploads a file</li>
	 *<li><B>Step:</B> UserA likes the file</li>
	 *<li><B>Step:</B> UserA uploads a 2nd file</li>
	 *<li><B>Step:</B> UserA creates a folder</li>
	 *<li><B>Step:</B> UserA adds the 2nd file to the folder</li>
	 *<li><B>Step:</B> UserA likes the file in the folder</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userALikesFilesFromStandaloneFiles() {
						
		filesUI.startTest();
		
		BaseFile file1 = new BaseFile.Builder(Data.getData().file1)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRand())
                                    .build();
		
		BaseFile file2 = new BaseFile.Builder(Data.getData().file14)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRandVal())
                                    .build();
		
		BaseFolder folder = new BaseFolder.Builder("Like test folder " + Helper.genDateBasedRand())
		                                  .description(Data.getData().FolderDescription)
		                                  .build();
				
		log.info("INFO: Login to Files as UserA: " + testUser1.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();
		
		log.info("INFO: Upload a file");
		file1.upload(filesUI);
		
		log.info("INFO: Open the uploaded file");
		filesUI.clickLinkWithJavascript("link=" + file1.getName());
		
		log.info("INFO: 'Like' the file");
		filesUI.clickLinkWait(FileViewerUI.LikeButton_FiDO);
		
		log.info("INFO: Exit FiDO - click the Close icon");
		filesUI.clickLinkWait(FileViewerUI.CloseButton);
		
		log.info("INFO: Upload a 2nd file");
		file2.upload(filesUI);
		
		log.info("INFO: Click on the All Folders view");
		filesUI.clickLinkWait(FilesUIConstants.AllFoldersLeftMenu);

		log.info("INFO: Click on the My Folders view");
		filesUI.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu);
		
		log.info("INFO: Create a folder");
		filesUI.create(folder);		
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);
		
		log.info("INFO: Add file to folder");
		file2.addToFolder(filesUI, folder);
		
		log.info("INFO: Open the 2nd uploaded file");
		filesUI.clickLinkWithJavascript("link=" + file2.getName());
		
		log.info("INFO: 'Like' the file");
		filesUI.clickLinkWait(FileViewerUI.LikeButton_FiDO);	
		
		log.info("INFO: Exit FiDO - click the Close icon");
		filesUI.clickLinkWait(FileViewerUI.CloseButton);
						
		filesUI.endTest();
				
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Files - Like File Created By Another User</li>
	 *<li><B>Step:</B> UserB uploads a file</li>
	 *<li><B>Step:</B> UserA likes the file</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userALikesFilesUploadedByUserBStandaloneFiles() {
								
		filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file3)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRand())
                                    .shareLevel(ShareLevel.EVERYONE)
                                    .build();
						
		log.info("INFO: Login to Files as UserB: " + testUser2.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser2);
			
		this.standaloneFilesUploadFile(file);
						
		log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
		filesUI.logout();
		filesUI.close(cfg);
		
		log.info("INFO: Login UserA: " + testUser1.getDisplayName());
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);
		
		this.displayDetailsPublicFilesView();
		
		log.info("INFO: Open the file uploaded by UserB: " + testUser2.getDisplayName());
		filesUI.clickLinkWithJavascript("link=" + file.getName());
		
		log.info("INFO: 'Like' the file");
		filesUI.clickLinkWithJavascript(FileViewerUI.LikeButton_FiDO);
		
		log.info("INFO: Exit FiDO - click the Close icon");
		filesUI.clickLinkWait(FileViewerUI.CloseButton);
								
		filesUI.endTest();
		
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Edit Uploaded File</li>
	 *<li><B>Step:</B> UserB creates a Public community & adds UserA as an Owner</li>
	 *<li><B>Step:</B> UserB uploads a file</li>
	 *<li><B>Step:</B> UserA edits the file (More Actions - Edit Properties)</li>
	 *</ul>
	 */
	@Test (groups = {"regression","regressioncloud"}, enabled=false)
	public void userAEditsUserBFileInCommFiles() {

		String testName = commUI.startTest();
		
		Member member = new Member(CommunityRole.OWNERS, testUser1);
		
		BaseFile file = new BaseFile.Builder(Data.getData().file6)
                                    .comFile(true)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRand())
                                    .build();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                  .access(Access.PUBLIC)
                                                  .description("GDPR data pop: Community Files - edit file test ")
                                                  .addMember(member)
                                                  .build();
		
		log.info("INFO: Create a new community with API");
		Community commAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, commAPI);
		
		log.info("INFO: Log into Communities");
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);
				
		this.uploadFileInCommFiles(community, file);
						
		log.info("INFO: Log out as UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
				
		log.info("INFO: Log in as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities, true);
		commUI.login(testUser1);
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
		
		log.info("INFO: Select Files from the navigation menu");
		Community_TabbedNav_Menu.FILES.select(commUI);
		
		this.editFileProperties();
				
		log.info("INFO: Log out as userA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);	

		commUI.endTest();	

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - UserA Uploads a File & a New Version</li>
	 *<li><B>Step:</B> UserA creates a community</li>
	 *<li><B>Step:</B> UserA uploads a file</li>
	 *<li><B>Step:</B> UserA uploads a new version of the file</li>
	 *</ul>
	 */
	@Test (groups = {"regression","regressioncloud"}, enabled=false)
	public void newVersionOfFileUploadedByUserACommFiles() {

		String testName = commUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file7)
                                    .comFile(true)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRand())
                                    .build();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                  .access(Access.PUBLIC)
                                                  .description("GDPR data pop: Community Files - new version of file uploaded by UserA ")
                                                  .build();
		
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
	
		log.info("INFO: Log into Communities");
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		this.uploadFileInCommFiles(community, file);
		
		log.info("INFO: Open the file in FiDO");
		filesUI.getFirstVisibleElement("link=" + file.getName()).click();
				
		this.uploadNewVersion();		
			
		commUI.endTest();	

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - UserB Uploads a File, UserA Uploads New Version</li>
	 *<li><B>Step:</B> UserB creates a community & adds UserA as an Owner</li>
	 *<li><B>Step:</B> UserB uploads a file</li>
	 *<li><B>Step:</B> UserA uploads a new version of the file</li>
	 *</ul>
	 */
	@Test (groups = {"regression","regressioncloud"}, enabled=false)
	public void newVersionOfFileUploadedByUserBCommFiles() {

		String testName = commUI.startTest();
		
		Member member = new Member(CommunityRole.OWNERS, testUser1);
		
		BaseFile file = new BaseFile.Builder(Data.getData().file8)
                                    .comFile(true)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRand())
                                    .build();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                  .access(Access.PUBLIC)
                                                  .description("GDPR data pop: Community Files - UserB uploads a file, UserA uploads a new version ")
                                                  .addMember(member)
                                                  .build();
		
		log.info("INFO: Create community using API");
		Community comAPI1 = community.createAPI(apiCommOwner2);

		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI1);
				
		log.info("INFO: Log into Communities");
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);
		
		this.uploadFileInCommFiles(community, file);
		
		log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Login as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
				
		log.info("INFO: Select Files from the navigation menu");
		Community_TabbedNav_Menu.FILES.select(commUI);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);
		
		log.info("INFO: Open the file in FiDO");
		filesUI.clickLinkWait("link=" + file.getName());
		
		this.uploadNewVersion();	
		
		commUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Files - UserA Uploads a File & a New Version</li>
	 *<li><B>Step:</B> UserA adds a file to the stand-alone Files app</li>
	 *<li><B>Step:</B> UserA uploads a new version of the file</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void newVersionOfFileUploadedByUserAStandaloneFile() {
						
		filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file9)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRand())
                                    .build();
						
		log.info("INFO: Login to Files as user: " + testUser1.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);

		this.standaloneFilesUploadFile(file);

		log.info("INFO: Open the file in FiDO");
		filesUI.clickLinkWithJavascript("link=" + file.getName());

		this.uploadNewVersion();

		filesUI.endTest();

	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Files - UserB Uploads File, UserA Uploads a New Version</li>
	 *<li><B>Step:</B> UserB adds a file to the stand-alone Files app</li>
	 *<li><B>Step:</B> UserB shares the file with UserA (Editor access)</li>
	 *<li><B>Step:</B> UserA uploads a new version of the file</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void newVersionOfFileUploadedByUserBStandaloneFile() {
						
		filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file10)
                                    .extension(".jpg")
                                    .rename(filesUI.reName(Data.getData().file10))
                                    .build();
						
		log.info("INFO: Login to Files as UserB: " + testUser2.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser2);
		
		this.standaloneFilesUploadFile(file);
		
		log.info("INFO: Open the file in FiDO");
		filesUI.getFirstVisibleElement("link=" + file.getName()).click();
		
		log.info("INFO: Click on the Sharing tab");
		fileviewerUI.clickLinkWithJavascript(FileViewerUI.sharingTab);
		
		log.info("INFO: Click on multiple share button");
		fileviewerUI.clickLinkWait(FileViewerUI.MultiShareButton);
		
		log.info("INFO: Add one people as editor into the share queue");
		fileviewerUI.addShare(FilesRole.EDITOR, Data.TypeAheadSelectorValueUser, testUser1.getEmail(), testUser1.getDisplayName());
		
		log.info("INFO: Click on multiple share save button");
		fileviewerUI.clickLinkWait(FileViewerUI.MultiShareSaveButton);
				
		log.info("INFO: Close FiDO");
		fileviewerUI.close();		
		
		log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
		filesUI.logout();
		filesUI.close(cfg);
		
		log.info("INFO: Login as UserA: " + testUser1.getDisplayName());
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();		
		
		log.info("INFO: Click on the All Files view");
		filesUI.clickLinkWait(FilesUIConstants.AllFilesView);

		log.info("INFO: Click on the Shared With Me view");
		filesUI.clickLinkWait(FilesUIConstants.SharedWithMeInNav);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);
		
		log.info("INFO: Open the file in FiDO");
		filesUI.clickLinkWait("link=" + file.getName());
		
		this.uploadNewVersion();
				
		filesUI.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Files - Add Comment To File</li>
	 *<li><B>Step:</B> UserA uploads a file</li>
	 *<li><B>Step:</B> UserA adds a comment to the file</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void addCommentToFileUploadedByUserAStandaloneFile() {
						
		filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file11)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRand())
                                    .build();
											
		log.info("INFO: Login to Files as UserA: " + testUser1.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);
		
		this.standaloneFilesUploadFile(file);
		
		log.info("INFO: Open the file in FiDO");
		filesUI.getFirstVisibleElement("link=" + file.getName()).click();
		
		this.addCommentToFile();		
		
		filesUI.endTest();
		
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Files - Add Comment To File</li>
	 *<li><B>Step:</B> UserB uploads a file</li>
	 *<li><B>Step:</B> UserA adds a comment to the file</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void addCommentToFileUploadedByUserBStandaloneFile() {
						
		filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file12)
                                    .extension(".jpg")
                                    .rename(filesUI.reName(Data.getData().file12))
                                    .build();
											
		log.info("INFO: Login to Files as UserB: " + testUser2.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser2);
		
		this.standaloneFilesUploadFile(file);
		
		log.info("INFO: Open the file in FiDO");
		filesUI.getFirstVisibleElement("link=" + file.getName()).click();		
						
		log.info("INFO: Click on the Sharing tab");
        fileviewerUI.clickLinkWithJavascript(FileViewerUI.sharingTab);
		
		log.info("INFO: Click on multiple share button");
		fileviewerUI.clickLinkWait(FileViewerUI.MultiShareButton);
		
		log.info("INFO: Add one people as editor into the share queue");
		fileviewerUI.addShare(FilesRole.EDITOR, Data.TypeAheadSelectorValueUser, testUser1.getEmail(), testUser1.getDisplayName());
		
		log.info("INFO: Click on multiple share save button");
		fileviewerUI.clickLinkWait(FileViewerUI.MultiShareSaveButton);
		
		log.info("INFO: Close FiDO");
		fileviewerUI.close();		
		
		log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
		filesUI.logout();
		filesUI.close(cfg);
		
		log.info("INFO: Login as UserA: " + testUser1.getDisplayName());
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();		
		
		log.info("INFO: Click on the All Files view");
		filesUI.clickLinkWait(FilesUIConstants.AllFilesView);

		log.info("INFO: Click on the Shared With Me view");
		filesUI.clickLinkWait(FilesUIConstants.SharedWithMeInNav);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);
		
		log.info("INFO: Open the file in FiDO");
		filesUI.clickLinkWait("link=" + file.getName());
		
		this.addCommentToFile();
		
		log.info("INFO: Log out of Files as UserA: " + testUser1.getDisplayName());
		filesUI.logout();
		filesUI.close(cfg);
		
		filesUI.endTest();
		
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Stand-alone Files - All Files: Shared With Me View</li>
	 *<li><B>Step:</B> UserA uploads a file</li>
	 *<li><B>Step:</B> UserA shares the file with UserB</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userASharesFileWithUserBStandaloneFile() {
						
		filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRand())
                                    .build();
											
		log.info("INFO: Login to Files as UserA: " + testUser1.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);
		
		this.standaloneFilesUploadFile(file);
		
		log.info("INFO: Open the file in FiDO");
		filesUI.getFirstVisibleElement("link=" + file.getName()).click();		
						
		log.info("INFO: Click on the Sharing tab");
		fileviewerUI.clickLinkWithJavascript(FileViewerUI.sharingTab);
		
		log.info("INFO: Click on multiple share button");
		fileviewerUI.clickLinkWithJavascript(FileViewerUI.MultiShareButton);
		
		log.info("INFO: Add one people as editor into the share queue");
		fileviewerUI.addShare(FilesRole.EDITOR, Data.TypeAheadSelectorValueUser, testUser2.getEmail(), testUser2.getDisplayName());
		
		log.info("INFO: Click on multiple share save button");
		fileviewerUI.clickLinkWait(FileViewerUI.MultiShareSaveButton);
		
		log.info("INFO: Close FiDO");
		fileviewerUI.close();		
		
		log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
		filesUI.logout();
		filesUI.close(cfg);
						
		filesUI.endTest();
		
	}
	

	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Add Comment to File</li>
	 *<li><B>Info:</B> UserA creates a community</li>
	 *<li><B>Info:</B> UserA uploads a file</li>  
	 *<li><B>Info:</B> UserA adds comment to the file</li>
	 *</ul>
	 *
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAAddsCommentToFileInCommFiles() {
		
		String testName = commUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file2)
                                  .comFile(true)
                                  .extension(".jpg")
                                  .rename(Helper.genDateBasedRand())
                                  .build();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                .access(Access.PUBLIC)
                                                .description("GDPR data pop: Community Files - add comment to file")
                                                .build(); 
		       
        log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
	
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		this.uploadFileInCommFiles(community, file);
								
		log.info("INFO: Open the file in FiDO");
		filesUI.getFirstVisibleElement("link=" + file.getName()).click();
				
		this.addCommentToFile();
		
		log.info("INFO: Click Close icon to exit out of FiDO");
		filesUI.clickLinkWait(FileViewerUI.CloseButton);
		
		log.info("INFO: Logout of Communities");
		commUI.logout();
		commUI.close(cfg);		
		
		commUI.endTest();
	
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Add Comment to File</li>
	 *<li><B>Info:</B> UserB creates a Public community</li>
	 *<li><B>Info:</B> UserB uploads a file</li>  
	 *<li><B>Info:</B> UserA adds comment to the file</li>
	 *</ul>
	 *
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAAddsCommentToUserBFileInCommFiles() {
		
		String testName = commUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file3)
                                 .comFile(true)
                                 .extension(".jpg")
                                 .rename(Helper.genDateBasedRand())
                                 .build();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                               .access(Access.PUBLIC)
                                               .description("GDPR data pop: Community Files - add comment to file")
                                               .build(); 
		       
        log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
	
		log.info("INFO: Log into Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);
		
		this.uploadFileInCommFiles(community, file);
		
		log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Login as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
				
		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(commUI);
								
		log.info("INFO: Open the file in FiDO");
		filesUI.getFirstVisibleElement("link=" + file.getName()).click();
		
		this.addCommentToFile();
		
		log.info("INFO: Click Close icon to exit out of FiDO");
		filesUI.clickLinkWait(FileViewerUI.CloseButton);
		
		log.info("INFO: Logout of Communities");
		commUI.logout();
		commUI.close(cfg);		
		
		commUI.endTest();
	
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Add a File to a Folder</li>
	 *<li><B>Info:</B> UserA creates a Public community</li>
	 *<li><B>Info:</B> UserA creates a folder</li>  
	 *<li><B>Info:</B> UserA adds a file to the folder</li>
	 *</ul>
	 *
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAAddsFileToFolderInCommFiles() {
		
		commUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file5)
                                .comFile(true)
                                .extension(".jpg")
                                .rename(Helper.genDateBasedRand())
                                .build();
		
		BaseCommunity community = new BaseCommunity.Builder("Add file to folder " + Helper.genDateBasedRandVal())
                                              .access(Access.PUBLIC)
                                              .description("GDPR data pop: Community Files - UserA creates a file & folder. Adds file to folder.")
                                              .build(); 
		
		BaseFolder folder = new BaseFolder.Builder("Comm File Folder " + Helper.genDateBasedRand())
                                          .description(Data.getData().FolderDescription)	
                                          .access(com.ibm.conn.auto.appobjects.base.BaseFolder.Access.PeoplGrpComm)
                                          .build();
		       
        log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
	
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		this.communityFilesCreateFolder(community, folder, file);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);
		
		log.info("INFO: Add the file: " + file.getName() + " to the folder: " + folder.getName());
		filesUI.addToComFolder(file, folder);
				
		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);		
		
		commUI.endTest();
	
	}
		
    /**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Add a File to a Folder</li>
	 *<li><B>Info:</B> UserB creates a Public community & adds UserA as a Member</li>
	 *<li><B>Info:</B> UserB creates a folder</li>  
	 *<li><B>Info:</B> UserA adds a file to the folder</li>
	 *</ul>
	 *
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAAddsFileToFolderCreatedByUserBInCommFiles() {
		
		commUI.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		BaseFile file = new BaseFile.Builder(Data.getData().file6)
                               .comFile(true)
                               .extension(".jpg")
                               .rename(Helper.genDateBasedRand())
                               .build();
		
		BaseCommunity community = new BaseCommunity.Builder("Add file to folder test " + Helper.genDateBasedRandVal())
                                             .access(Access.PUBLIC)
                                             .description("GDPR data pop: Community Files - UserA adds a file to a folder created by UserB")
                                             .addMember(member)
                                             .build(); 
		
		BaseFolder folder = new BaseFolder.Builder("Community File Folder " + Helper.genDateBasedRand())
                                         .description(Data.getData().FolderDescription)	
                                         .access(com.ibm.conn.auto.appobjects.base.BaseFolder.Access.PeoplGrpComm)
                                         .build();
		       
        log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
	
		log.info("INFO: Log into Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);
		
		this.communityFilesCreateFolder(community, folder, file);
		
		log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Log into Communities as UserA: " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
						
		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(commUI);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);
		
		log.info("INFO: Add the file: " + file.getName() + " to the folder: " + folder.getName());
		filesUI.addToComFolder(file, folder);
				
		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
		commUI.logout();
		commUI.close(cfg);		
		
		commUI.endTest();
	
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Follow a File</li>
	 *<li><B>Step:</B> UserB creates a Public community & adds UserA as a member</li>
	 *<li><B>Step:</B> UserB uploads a file</li>
	 *<li><B>Step:</B> UserA follows the file</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userAFollowsFileUploadedByUserBInCommFiles() {
						
		commUI.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		BaseFile file = new BaseFile.Builder(Data.getData().file7)
		                            .comFile(true)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRand())
                                    .build();
		
		BaseCommunity community = new BaseCommunity.Builder("Follow file in community files widget " + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .addMember(member)
                                                   .description("GDPR data pop: Community Files - UserA follows a file uploaded by UserB")
                                                   .build(); 
		
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Log into Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);

		this.uploadFileInCommFiles(community, file);

		log.info("INFO: UserB " + testUser2.getDisplayName() + " logs out of Communities");
		commUI.logout();
		commUI.close(cfg);

		log.info("INFO: UserA " + testUser1.getDisplayName() + " logs into Communities");
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);	
		
		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(commUI);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);

		log.info("INFO: Click the More link for the uploaded file " + file.getName());
		filesUI.clickLinkWait(FilesUIConstants.moreLink);
		
		log.info("INFO: Click on the More Actions link");
		filesUI.clickLinkWait(FilesUIConstants.filesMoreActionsBtn);

		log.info("INFO: Click on the Follow link");
		filesUI.clickLinkWait(FilesUIConstants.FollowingOption);

		commUI.endTest();
		
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Download a File</li>
	 *<li><B>Step:</B> UserB creates a Public community & adds UserA as a member</li>
	 *<li><B>Step:</B> UserB uploads a file</li>
	 *<li><B>Step:</B> UserA downloads the file</li>
	 *
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userADownloadsFileUploadedByUserBInCommFiles() {
						
		commUI.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		BaseFile file = new BaseFile.Builder(Data.getData().file8)
		                            .comFile(true)
                                    .extension(".jpg")
                                    .rename(Helper.genDateBasedRand())
                                    .build();
		
		BaseCommunity community = new BaseCommunity.Builder("Download file in community files widget " + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .addMember(member)
                                                   .description("GDPR data pop: Community Files - UserA downloads a file uploaded by UserB")
                                                   .build(); 
		
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);

		log.info("INFO: Log into Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);

		log.info("INFO: Navigate to the community & upload a file");
		this.uploadFileInCommFiles(community, file);

		log.info("INFO: UserB " + testUser2.getDisplayName() + " logs out of Communities");
		commUI.logout();
		commUI.close(cfg);

		log.info("INFO: UserA " + testUser1.getDisplayName() + " logs into Communities");
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);	
		
		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(commUI);
		
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);
		
		log.info("INFO: Click the More link for the uploaded file " + file.getName());
		filesUI.clickLinkWait(FilesUIConstants.moreLink);
		
		log.info("INFO: Click on the Download link");
		filesUI.getFirstVisibleElement(FilesUIConstants.DownloadLink).click();
		commUI.endTest();				
		
	}
	
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Community Files - Like a file</li>
	 *<li><B>Info:</B> UserB creates a community & adds UserA as a member</li>
	 *<li><B>Info:</B> UserB uploads a file</li> 
	 *<li><B>Info:</B> UserB logs out of communities & UserA logs in</li> 
	 *<li><B>Info:</B> UserA uploads a file</li>
	 *<li><B>Info:</B> UserA Likes the file they uploaded</li>
	 *<li><B>Info:</B> UserA opens the file UserB upload and Likes the file</li>
	 *</ul>
	 *
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userALikesFilesInCommFiles() {
		
		String testName = commUI.startTest();
		
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		BaseFile file1 = new BaseFile.Builder(Data.getData().file2)
                                 .comFile(true)
                                 .rename(Helper.genDateBasedRand())
                                 .extension(".jpg")
                                 .build();
		
		BaseFile file2 = new BaseFile.Builder(Data.getData().file3)
		                             .comFile(true)
		                             .rename(Helper.genDateBasedRandVal())
                                     .extension(".jpg")
                                     .build();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                               .access(Access.PUBLIC)
                                               .description("GDPR data pop: Community Files - add comment to file")
                                               .addMember(member)
                                               .build(); 
		       
        log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);
	
		log.info("INFO: Log into Communities as UserB: " + testUser2.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser2);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
				
		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(commUI);
		
		log.info("INFO: Upload file: " + file1.getName());		
		file1.upload(filesUI);
		
		log.info("INFO: Validate file upload message is present");
		if (!filesUI.fluentWaitTextPresent(Data.getData().UploadMessage)){
			log.info("INFO: Message not present, clicking upload link and checking for message again");
			filesUI.reClickUploadLink(file1, gkc);
			filesUI.fluentWaitTextPresent(Data.getData().UploadMessage);
		}
		
		log.info("INFO: Log out UserB " + testUser2.getDisplayName());
		commUI.logout();
		commUI.close(cfg);
		
		log.info("INFO: Log in UserA " + testUser1.getDisplayName());
		commUI.loadComponent(Data.getData().ComponentCommunities);
		commUI.login(testUser1);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);
				
		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(commUI);
		
		log.info("INFO: Upload file: " + file2.getName());		
		file2.upload(filesUI);
		
		log.info("INFO: Validate file upload message is present");
		if (!filesUI.fluentWaitTextPresent(Data.getData().UploadMessage)){
			log.info("INFO: Message not present, clicking upload link and checking for message again");
			filesUI.reClickUploadLink(file2, gkc);
			filesUI.fluentWaitTextPresent(Data.getData().UploadMessage);
		}
		
		log.info("INFO: Open the file in FiDO");
		filesUI.getFirstVisibleElement("link=" + file2.getName()).click();
		
		log.info("INFO: 'Like' the file");
		filesUI.clickLinkWithJavascript(FileViewerUI.LikeButton_FiDO);	
		
		log.info("INFO: Exit FiDO - click the Close icon");
		fileviewerUI.selectAction(FileViewerUI.CloseButton);
								
		log.info("INFO: Open the file uploaded by UserB");
		filesUI.getFirstVisibleElement("link=" + file1.getName()).click();
		
		log.info("INFO: 'Like' the file");
		filesUI.clickLinkWithJavascript(FileViewerUI.LikeButton_FiDO);	
		
		log.info("INFO: Exit FiDO - click the Close icon");
		fileviewerUI.selectAction(FileViewerUI.CloseButton);
			
		commUI.endTest();
		
	}
	
	/**
	 *
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - Add File to Folder</li>
	 *<li><B>Info:</B> UserB creates a folder & adds UserA with 'Editors' role</li>
	 *<li><B>Info:</B> UserA uploads a file</li> 
	 *<li><B>Info:</B> UserA adds the file to the folder created by UserB</li> 
	 *</ul>
	 */		
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBCreatesFolderUserAAddsFileToFolder() {
						
		filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file12)
                                    .extension(".jpg")
                                    .rename(filesUI.reName(Data.getData().file12))
                                    .build();
		
		BaseFolder folder = new BaseFolder.Builder("GDPR Add File To Folder " + Helper.genDateBasedRand())
		                                  .description(Data.getData().FolderDescription)	
		                                  .build();
									
		log.info("INFO: Login to Files as UserB: " + testUser2.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser2);	
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();
		
		log.info("INFO: Create a folder & add UserA: " + testUser1.getDisplayName() + " as an Editor");
		createFolderAddUserAsEditor(folder, testUser1);
		
		log.info("INFO: Logout of Files as UserB: " + testUser2.getDisplayName());	
		filesUI.logout();
		filesUI.close(cfg);
		
		log.info("INFO: Login to Files as UserA: " + testUser1.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);
		
		log.info("INFO: " + testUser1.getDisplayName() + " uploads a file");
		file.upload(filesUI);
		
		log.info("INFO: Click the 'My Files' view");
		filesUI.clickMyFilesView();
		
		log.info("INFO: Switch to Details View");
		filesUI.clickLink(FilesUIConstants.selectViewList);

		log.info("INFO: Select 'More' button for the newly uploaded file");
		filesUI.clickLinkWait(filesUI.fileSpecificMore(file));
		
		log.info("INFO: Select 'Add this file to a folder...' link");
		filesUI.clickLinkWait(FilesUIConstants.addFileToFolder);
		
		log.info("INFO: Select 'Shared with Me' from the menu options");
		filesUI.selectComboValue(FilesUIConstants.pickerMenu, Data.getData().foldersSharedWithMeOption);
		
		log.info("INFO: Select the folder to add the file too");
		filesUI.clickFolderItemInFilePicker(folder.getName(), true);
		
		log.info("INFO: Select the 'Add Here' button");
		filesUI.clickLinkWait(FilesUIConstants.addToFolder);
		
		log.info("INFO: Logout of Files as UserA: " + testUser1.getDisplayName());	
		filesUI.logout();
		filesUI.close(cfg);
				
		filesUI.endTest();
		
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - UserA Shares Folder with UserB</li>
	 *<li><B>Step:</B> UserA creates a folder</li>
	 *<li><B>Step:</B> UserA shares the folder with UserB (adds with 'Editors' role)</li>
	 *</ul>
	 */
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userASharesFolderWithUserBStandaloneFile() {
						
		filesUI.startTest();
		
		BaseFolder folder = new BaseFolder.Builder("GDPR Share Folder " + Helper.genDateBasedRand())
                                          .description(Data.getData().FolderDescription)
                                          .build();
													
		log.info("INFO: Login to Files as UserA: " + testUser1.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();
				
		log.info("INFO: Wait for My Files page to be available");
		filesUI.clickMyFilesView();
		filesUI.fluentWaitPresent(FilesUIConstants.MyFilesTitleLink);
		
		log.info("INFO: Create a folder & add UserB: " + testUser2.getDisplayName() + " as an Editor");
		createFolderAddUserAsEditor(folder, testUser2);
		
		log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
	    filesUI.logout();
	    filesUI.close(cfg);
	    
		filesUI.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - UserA Shares a File with Community Files</li>
	 *<li><B>Step:</B> UserA creates a community using API</li>
	 *<li><B>Step:</B> UserA logs into Files & uploads a file</li>
	 *<li><B>Step:</B> UserA shares the file with everyone in the organization - done so the file can be shared with community files</li>
	 *<li><B>Step:</B> UserA navigates to communities, from mega-menu selects Communities</li>
	 *<li><B>Step:</B> UserA clicks on the I'm an Owner view link & opens the community</li>
	 *<li><B>Step:</B> UserA clicks on the Files tab</li>
	 *<li><B>Step:</B> UserA clicks on Add > Share File with Community</li>
	 *<li><B>Step:</B> UserA selects 'My Files' from the picker</li>
	 *<li><B>Step:</B> UserA selects the file uploaded from standalone files & clicks the Share File button</li>
	 *</ul>
	 */	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userASharesStandaloneFileWithCommFiles() {
						
		String testName = filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file2)
                                    .extension(".jpg")
                                    .rename(filesUI.reName(Data.getData().file2))
                                    .build();
				
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("GDPR data pop: Community Files - share file from standalone files ")
                                                   .build();
		
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);

		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);
		
		log.info("INFO: Login to Files as UserA: " + testUser1.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);	
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();

		log.info("INFO: Upload a file");
		file.upload(filesUI);
		
		log.info("INFO: Switch to Details View");
		filesUI.clickLink(FilesUIConstants.selectViewList);

		log.info("INFO: Share the file with everyone in the organization");
		filesUI.share(file);

		if(isOnPremise){

			log.info("INFO: Click on 'I'm an Owner' option from Community Mega Menu dropdwon");
			Community_MegaMenu_Menu.IM_AN_OWNER.select(commUI);

		}else {
			log.info("INFO: Click on the Communities link on the mega-menu");
			commUI.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);

			log.info("INFO: Click on the I'm an Owner' link from the left nav menu");
			Community_View_Menu.IM_AN_OWNER.select(commUI);
		}
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);			

		log.info("INFO: Click on the Files tab");
		Community_TabbedNav_Menu.FILES.select(commUI);
		
		//NOTE - refresh done to ensure page displays correctly, noticed on occasion page didn't fully load
		log.info("INFO: Refresh the browser");
		UIEvents.refreshPage(driver);

		log.info("INFO: Click on the Add button");
		commUI.clickLinkWait(CommunitiesUIConstants.filesWidgetAddButton);

		log.info("INFO: Click on the 'Share File with Community' link");
        commUI.clickLinkWait(CommunitiesUIConstants.filesWidgetShareFileWithCommunityLink);
		
		log.info("INFO: Select 'My Files' from menu options");
		filesUI.selectComboValue(FilesUIConstants.pickerMenu, Data.getData().ComponentHPFiles);

		filesUI.clickFolderItemInFilePicker(file.getName(),false);

		log.info("INFO: Click on the Share Files button");
		commUI.clickLinkWait(BaseUIConstants.Share_Files_Button);

		log.info("INFO: Logout of Communities as UserA: " + testUser1.getDisplayName());	
		filesUI.logout();
		filesUI.close(cfg);

		filesUI.endTest();


	}
	
	/**	
	 * <ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - UserA Shares a Folder with Community Files</li>
	 *<li><B>Step:</B> UserA creates a community using API</li>
	 *<li><B>Step:</B> UserA logs into Files & creates a folder</li>
	 *<li><B>Step:</B> UserA shares the folder with everyone in the organization - done so the folder can be shared with community files</li>
	 *<li><B>Step:</B> UserA navigates to communities, from mega-menu selects Communities</li>
	 *<li><B>Step:</B> UserA clicks on the I'm an Owner view link & opens the community</li>
	 *<li><B>Step:</B> UserA clicks on the Files tab</li>
	 *<li><B>Step:</B> UserA clicks on Add > Share Folder with Community</li>
	 *<li><B>Step:</B> UserA selects 'My Folders' from the picker</li>
	 *<li><B>Step:</B> UserA selects the file uploaded from standalone files & clicks the Share Folder button</li>
	 *</ul>
	 */	
	
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userASharesStandaloneFolderWithCommFiles() {
						
		String testName = filesUI.startTest();
		
		BaseFolder folder = new BaseFolder.Builder("GDPR Share Folder with Comm " + Helper.genDateBasedRand())
                                          .description(Data.getData().FolderDescription)
                                          .build();
				
		BaseCommunity community = new BaseCommunity.Builder("GDPR " + testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("GDPR data pop: Community Files - share folder from standalone files ")
                                                   .build();
		
		
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner1);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner1, comAPI);	
		
		log.info("INFO: Login to Files as UserA: " + testUser1.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser1);	
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();
		
		log.info("INFO: Create a folder");
		filesUI.create(folder);
		
		log.info("INFO: Click on the folder link");
		filesUI.getFirstVisibleElement("link=" + folder.getName()).click();
		
		log.info("INFO: Select 'Sharing...' from drop-down menu");
		Files_Folder_Dropdown_Menu.SHARE.select(filesUI);
		
		log.info("INFO: Click on the radio button to share folder with everyone in the organization");
		filesUI.getFirstVisibleElement(FilesUIConstants.shareWithEveryone).click();
		
		log.info("INFO: Click the Share button");
		filesUI.getFirstVisibleElement(FilesUIConstants.shareFolderDialogShareButton).click();
		
		if(isOnPremise){
			log.info("INFO: Click on 'I'm an Owner' option from Community Mega Menu dropdwon");
			Community_MegaMenu_Menu.IM_AN_OWNER.select(commUI);

		}else {
			log.info("INFO: Click on the Communities link on the mega-menu");
			commUI.clickLinkWait(CommunitiesUIConstants.megaMenuOptionCommunities);

			log.info("INFO: Click on the I'm an Owner' link from the left nav menu");
			Community_View_Menu.IM_AN_OWNER.select(commUI);
		}
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);	
		
		log.info("INFO: Click on the Files tab");
		Community_TabbedNav_Menu.FILES.select(commUI);
		
		//NOTE - refresh done to ensure page displays correctly, noticed on occasion page didn't fully load
		log.info("INFO: Refresh the browser");
		UIEvents.refreshPage(driver);
		
		log.info("INFO: Click on the Add button");
		commUI.clickLinkWait(CommunitiesUIConstants.filesWidgetAddButton);

		log.info("INFO: Click on the 'Share Folder with Community' link");		
		commUI.clickLinkWait(CommunitiesUIConstants.filesWidgetShareFolderWithCommunityLink);

		log.info("INFO: Select 'My Folders' from menu options");
		filesUI.selectComboValue(FilesUIConstants.pickerMenu, Data.getData().myFoldersOption);

		log.info("INFO: Select folder to be shared with the community");
		filesUI.clickFolderItemInFilePicker(folder.getName(), false);

		log.info("INFO: Click on the Share Folder button");
		filesUI.clickLinkWait(FilesUIConstants.shareFolderDialogShareButton);

		log.info("INFO: Logout of Communities as UserA: " + testUser1.getDisplayName());	
		filesUI.logout();
		filesUI.close(cfg);

		filesUI.endTest();


	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - UserB Uploads File & UserA Shares the File with Community Files</li>
	 *<li><B>Step:</B> UserB creates a community with UserA as a member - done via API</li>
	 *<li><B>Step:</B> UserB logs into Files & creates a file</li>
	 *<li><B>Step:</B> UserB shares the file with everyone in the organization...done so the file can be shared in communities.</li>
	 *<li><B>Step:</B> UserB opens the file in FiDO & shares the file with UserA (added with Editor role)</li>
	 *<li><B>Step:</B> UserB exits fiDO logs out</li> 
	 *<li><B>Step:</B> UserA logs into Communities</li>
	 *<li><B>Step:</B> UserA opens the community</li>
	 *<li><B>Step:</B> UserA clicks on the Files tab</li>
	 *<li><B>Step:</B> UserA clicks on Add > Share File with Community</li>
	 *<li><B>Step:</B> UserA selects 'Shared With Me' from drop-down menu</li>
	 *<li><B>Step:</B> UserA selects the file uploaded by UserB from the list</li>
	 *<li><B>Step:</B> UserA clicks the Share File button</li>
	 *</ul>	 
	 */			
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBUploadsStandaloneFileUserASharesWithCommFiles() {
						
		String testName = filesUI.startTest();
		
		BaseFile file = new BaseFile.Builder(Data.getData().file3)
                                    .extension(".jpg")
                                    .rename(filesUI.reName(Data.getData().file3))
                                    .build();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("GDPR data pop: Community Files - share file & folder from standalone files ")
                                                   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
                                                   .build();
		
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);			
									
		log.info("INFO: Login to Files as UserB: " + testUser2.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser2);	
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();
		
		log.info("INFO: Upload a file");
		file.upload(filesUI);
		
		log.info("INFO: Share the file with everyone in the organization");
		filesUI.share(file);
		
		log.info("INFO: Share the file with UserA: " + testUser1.getDisplayName() + " with Editor access");
		log.info("INFO: Open the file in FiDO");
		filesUI.getFirstVisibleElement("link=" + file.getName()).click();		
						
		log.info("INFO: Click on the Sharing tab");
		fileviewerUI.clickLinkWithJavascript(FileViewerUI.sharingTab);
		
		log.info("INFO: Click on multiple share button");
		fileviewerUI.clickLinkWithJavascript(FileViewerUI.MultiShareButton);
		
		log.info("INFO: Add one people as editor into the share queue");
		fileviewerUI.addShare(FilesRole.EDITOR, Data.TypeAheadSelectorValueUser, testUser1.getEmail(), testUser1.getDisplayName());
		
		log.info("INFO: Click on multiple share save button");
		fileviewerUI.clickLinkWait(FileViewerUI.MultiShareSaveButton);
		
		log.info("INFO: Close FiDO");
		fileviewerUI.close();				
		
		log.info("INFO: Logout of Files as UserB: " + testUser2.getDisplayName());	
		filesUI.logout();
		filesUI.close(cfg);				
		
		log.info("INFO: Login to Communities as UserA: " + testUser1.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentCommunities);
		filesUI.login(testUser1);	
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);		

		log.info("INFO: Click on the Files tab");
		Community_TabbedNav_Menu.FILES.select(commUI);

		log.info("INFO: Click on the Add button");
		commUI.clickLinkWait(CommunitiesUIConstants.filesWidgetAddButton);

		log.info("INFO: Click on the 'Share File with Community' link");
		commUI.clickLinkWait(CommunitiesUIConstants.filesWidgetShareFileWithCommunityLink);

		log.info("INFO: Select 'Shared With Me' from menu options");
		filesUI.selectComboValue(FilesUIConstants.pickerMenu, Data.getData().filesSharedWithMeOption);

		filesUI.clickFolderItemInFilePicker(file.getName(), false);

		log.info("INFO: Click on the Share Files button");
		commUI.clickLinkWait(BaseUIConstants.Share_Files_Button);

		log.info("INFO: Logout of Communities as UserA: " + testUser1.getDisplayName());	
		filesUI.logout();
		filesUI.close(cfg);
				
		filesUI.endTest();
		
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Standalone Files - UserB Uploads Folder & UserA Shares Folder with Community Files</li>
	 *<li><B>Step:</B> UserB creates a community with UserA as a member - done via API</li>
	 *<li><B>Step:</B> UserB logs into Files & creates a folder</li>
	 *<li><B>Step:</B> UserB shares the folder with everyone in the organization...done so the folder can be shared in communities.</li>
	 *<li><B>Step:</B> UserB shares the folder with UserA (added with Owner role)</li>
	 *<li><B>Step:</B> UserB logs out & UserA logs into Communities</li>
	 *<li><B>Step:</B> UserA opens the community</li>
	 *<li><B>Step:</B> UserA clicks on the Files tab</li>
	 *<li><B>Step:</B> UserA clicks on Add > Share Folder with Community</li>
	 *<li><B>Step:</B> UserA selects 'Shared with Me' from drop-down menu</li>
	 *<li><B>Step:</B> UserA selects the folder created by UserB from the list</li>
	 *<li><B>Step:</B> UserA clicks the Share Folder button</li>
	 *</ul>	 
	 */		
	@Test(groups = {"regression","regressioncloud"}, enabled=false)
	public void userBUploadsStandaloneFolderUserASharesWithCommFiles() {
						
		String testName = filesUI.startTest();
				
		BaseFolder folder = new BaseFolder.Builder("GDPR Share Folder with Comm " + Helper.genDateBasedRand())
		                                  .description(Data.getData().FolderDescription)
		                                  .build();
		
		BaseCommunity community = new BaseCommunity.Builder("GDPR: " + testName + Helper.genDateBasedRandVal())
                                                   .access(Access.PUBLIC)
                                                   .description("GDPR data pop: Community Files - share folder from standalone files ")
                                                   .addMember(new Member(CommunityRole.MEMBERS, testUser1))
                                                   .build();
		
		
		log.info("INFO: Create a new Community using API");
		Community comAPI = community.createAPI(apiCommOwner2);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner2, comAPI);	
					
		log.info("INFO: Login to Files as UserB: " + testUser2.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentFiles);
		filesUI.login(testUser2);	
		
		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();
						
		log.info("INFO: Click on the All Folders view");
		filesUI.clickLinkWait(FilesUIConstants.AllFoldersLeftMenu);
		
		log.info("INFO: Create a folder");
		filesUI.create(folder);
		
		log.info("INFO: Click on the folder link");
		filesUI.getFirstVisibleElement("link=" + folder.getName()).click();
		
		log.info("INFO: Select 'Share...' from drop-down menu");
		Files_Folder_Dropdown_Menu.SHARE.select(filesUI);
		
		log.info("INFO: Click on the radio button to share folder with everyone in the organization");
		filesUI.getFirstVisibleElement(FilesUIConstants.shareWithEveryone).click();
		
		log.info("INFO: Click the 'Share' button to save the change");
		filesUI.getFirstVisibleElement(FilesUIConstants.shareFolderDialogShareButton).click();
		
		log.info("INFO: Share folder with UserA: " + testUser1.getDisplayName() + " with Owner access");
		
		log.info("INFO: Click on the 'Sharing' tab");
		filesUI.clickLinkWait(FilesUIConstants.sharingTab);
		
		log.info("INFO: Click the 'Owners' role 'Add' link");
		clickSharingTabAddLink(2);
		
		log.info("INFO: Enter UserA: " + testUser1.getDisplayName() + " into the input field");
		filesUI.typeText(FilesUIConstants.shareFolderDialogPersonInputField, testUser1.getDisplayName());

		log.info("INFO: Select UserA: " + testUser1.getDisplayName() + " from typeahead results");
		hUI.typeaheadSelection(testUser1.getDisplayName(), FilesUIConstants.shareFolderDialogPersonTypeahead);

		log.info("INFO: Click on the Share button");
		filesUI.clickLinkWithJavascript(FilesUIConstants.shareFolderDialogShareButton);
		
		log.info("INFO: Logout of Files as UserB: " + testUser2.getDisplayName());	
		filesUI.logout();
		filesUI.close(cfg);				
		
		log.info("INFO: Login to Communities as UserA: " + testUser1.getDisplayName());	
		filesUI.loadComponent(Data.getData().ComponentCommunities);
		filesUI.login(testUser1);	
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);		

		log.info("INFO: Click on the Files tab");
		Community_TabbedNav_Menu.FILES.select(commUI);

		log.info("INFO: Click on the Add button");
		commUI.clickLinkWait(CommunitiesUIConstants.filesWidgetAddButton);

		log.info("INFO: Click on the 'Share Folder with Community' link");		
		commUI.clickLinkWait(CommunitiesUIConstants.filesWidgetShareFolderWithCommunityLink);
				
		log.info("INFO: Select 'Shared with Me' from menu options");
		filesUI.selectComboValue(FilesUIConstants.pickerMenu, Data.getData().foldersSharedWithMeOption);

		filesUI.clickFolderItemInFilePicker(folder.getName(), false);

		log.info("INFO: Click on the Share Folders button");
		commUI.clickLinkWait(FilesUIConstants.shareFolderDialogShareButton);

		log.info("INFO: Logout of Communities as UserA: " + testUser1.getDisplayName());	
		filesUI.logout();
		filesUI.close(cfg);
				
		filesUI.endTest();
		
		
	}
	
	
	
	
	
	/**
	 *Stand-Alone Files: Upload file
	 *If the Guided tour dialog displays, it will be closed
	 *Method will navigate to My Files view and upload a file
	 * 
	 *@param file - file to upload
	 */
	
	public void standaloneFilesUploadFile(BaseFile file) {

		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();

		log.info("INFO: Wait for My Files view to be available");
		filesUI.clickMyFilesView();
		filesUI.fluentWaitPresent(FilesUIConstants.MyFilesTitleLink);

		log.info("INFO: Upload file: " + file.getName());
		file.upload(filesUI);

		log.info("INFO: Check for the upload Success message for file: " + filesUI.getUploadFileName(file));
		filesUI.fluentWaitPresent(FilesUICloud.fileUploadedSuccessImg);

	}

	
	/**
	 *Community Files: Create Folder, Upload file to folder
	 *This method will create a folder and then upload a file to the folder
	 * 
	 *@param community - community the file will be uploaded to
	 *@param file - file to upload
	 *@param folder - folder to be created
	 */

	public void communityFilesCreateFolder(BaseCommunity community, BaseFolder folder, BaseFile file) {		

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(commUI);

		log.info("INFO: Click on the Add button");
		commUI.clickLinkWait(FilesUIConstants.ComFilesAdd_Button);

		log.info("INFO: Click on the New Folder link");
		commUI.getFirstVisibleElement(FilesUIConstants.ComFilesNewFolder).click();

		log.info("INFO: Complete the fields on the New Folder dialog");
		filesUI.typeText(FilesUIConstants.CreateFolderName, folder.getName());
		if(folder.getDescription() != null){
			filesUI.typeText(FilesUIConstants.CreateFolderDescription, folder.getDescription());
		}

		if(folder.getAccess() != null && Access.PUBLIC.equals(folder.getAccess())){
			filesUI.clickLinkWait(FilesUIConstants.shareWithEveryone);
		}

		log.info("INFO: Click on the Create button");
		filesUI.clickCreateButton();

		log.info("INFO: Upload file: " + file.getName());		
		file.upload(filesUI);

		log.info("INFO: Validate file upload message is present");
		if (!filesUI.fluentWaitTextPresent(Data.getData().UploadMessage)){
			log.info("INFO: Message not present, clicking upload link and checking for message again");
			filesUI.reClickUploadLink(file, gkc);
			filesUI.fluentWaitTextPresent(Data.getData().UploadMessage);
		}
	}
	

	/**
	 *This method navigates user to the community, clicks on the Files tab & uploads a file
	 *@param community - community to upload file to 
	 *@param file - file to upload
	 */

	public void uploadFileInCommFiles(BaseCommunity community, BaseFile file) {

		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(commUI);

		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(commUI);

		log.info("INFO: Upload file: " + file.getName());		
		file.upload(filesUI);

		log.info("INFO: Validate file upload message is present");
		if (!filesUI.fluentWaitTextPresent(Data.getData().UploadMessage)){
			log.info("INFO: Message not present, clicking upload link and checking for message again");
			filesUI.reClickUploadLink(file, gkc);
			filesUI.fluentWaitTextPresent(Data.getData().UploadMessage);
		}
	}



	/**
	 *This method uploads a new version of a file
	 */

	public void uploadNewVersion() {

		if(isOnPremise){
			log.info("INFO: Environment is on-premise");

			log.info("INFO: Upload new version");		
			fileviewerUI.uploadNewVersion();

		}else{
			log.info("INFO: Environment is cloud");

			log.info("INFO: Click on the Version Tab");			
			fileviewerUI.clickLinkWithJavascript(FileViewerUI.versionTab);

			log.info("INFO: Click on the Upload New Version button");
			fileviewerUI.clickLinkWithJavascript(FileViewerUI.UploadNewVersionButton);

			log.info("INFO: Upload the new file");
			Element fileInput = fileviewerUI.getFirstVisibleElement(FileViewerUI.FileInput);

			BaseFile newFile = new BaseFile.Builder(Data.getData().file1)
			                               .extension(".jpg").rename(Helper.genDateBasedRand()).build();

			fileInput.typeFilePath(cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), newFile.getName()));

			log.info("INFO: Click on the Upload button");
			fileviewerUI.clickLinkWait(FileViewerUI.UploadSaveButton);

			log.info("INFO: Check that the version save message appears");
			fileviewerUI.fluentWaitTextPresent("The new version was saved");

		};	
	}

	/**
	 *This method adds a comment to a file
	 */

	public void addCommentToFile() {
		if(isOnPremise){
			log.info("INFO: Environment is on-premise");

			log.info("INFO: Add a comment to the file");
			fileviewerUI.addComments(Data.getData().commonComment);

		}else{
			log.info("INFO: Environment is cloud");

			log.info("INFO: Check to see if the comment box is visible");
			fileviewerUI.fluentWaitElementVisible(FileViewerUI.addACommentBox);

			log.info("INFO: Click the comment box");
			driver.getSingleElement(FileViewerUI.addACommentBox).click();

			log.info("INFO: Add a comment to the file");
			fileviewerUI.addComments(Data.getData().commonComment);

		};		

	}

	
	/**
	 *This method edits the file name in Edit Properties
	 */

	public void editFileProperties() {
		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);		

		log.info("INFO: Click on the More link for the uploaded file");
		filesUI.clickLinkWait(FilesUIConstants.moreLink);

		log.info("INFO: Click on the More Actions link");
		filesUI.clickLinkWait(FilesUIConstants.filesMoreActionsBtn);

		log.info("INFO: Click on Edit Properties");
		filesUI.clickLinkWait(FilesUIConstants.EditPropertiesOption);

		log.info("INFO: Edit the file name");
		filesUI.fluentWaitTextPresent(Data.getData().editPropertiesDialogBoxTitle);
		filesUI.clearText(FilesUIConstants.editPropertiesName);
		filesUI.typeText(FilesUIConstants.editPropertiesName, Data.getData().editedFileName);
		filesUI.clickButton(Data.getData().buttonSave);

		log.info("INFO: Verify the updated file name appears");
		Assert.assertTrue(driver.isTextPresent(Data.getData().editedFileName),
				"ERROR: The updated file name does not appear");
	}
	
	/**
	 * This method will: 
	 * - close Guided tour box if opened
	 * - navigate to the All Files view & then select Public Files view
	 * - click on the icon to display 'Details'
	 */
	public void displayDetailsPublicFilesView() {

		log.info("INFO: If the Guided Tour pop-up displays, close it");
		commUI.closeGuidedTourPopup();

		log.info("INFO: Click on All Files to expand the section");
		filesUI.clickLinkWait(FilesUIConstants.AllFilesView);

		log.info("INFO: Click on the Public Files view");
		filesUI.clickLinkWait(FilesUIConstants.PublicFilesInNav);

		log.info("INFO: Select Details display button");
		Files_Display_Menu.DETAILS.select(filesUI);

	}
	
	/**
	 * This method will click the 'Add' link specified
	 * @param x = the Add link to be selected: 0 = Reader, 1 = Editor, 2 = Owner
	 * 
	 */
	
	private void clickSharingTabAddLink(Integer x){
		List <Element> addLinks;
		addLinks=driver.getVisibleElements(FilesUIConstants.sharingTabTable);
		Element link = addLinks.get(0);
		List <Element> moreLinks=link.getElements(FilesUIConstants.sharingTabLinks);
		Element e=moreLinks.get(x);
		e.click();
	}
	
	/**
	 * This method will:
	 * - create a folder
	 * - add/share folder with a user - Editors role
	 * @param folder - folder to be created
	 * @param testUser - user to be given Editors role
	 * 
	 */
	
	private void createFolderAddUserAsEditor (BaseFolder folder, User testUser){
		log.info("INFO: Click on the All Folders view");
		filesUI.clickLinkWait(FilesUIConstants.AllFoldersLeftMenu);
		
		log.info("INFO: Create a folder");
		filesUI.create(folder);
			
		log.info("INFO: Click the icon to switch to the Details view");
		filesUI.clickLinkWait(FilesUIConstants.DisplayList);
		
		log.info("INFO: Click on the newly created folder link");
		filesUI.getFirstVisibleElement("link=" + folder.getName()).click();		
		
		log.info("INFO: Click on the Sharing tab");
		filesUI.clickLinkWait(FilesUIConstants.sharingTab);
		 
		log.info("INFO: Click the 'Editors' role 'Add' link");
		clickSharingTabAddLink(1);
		
		log.info("INFO: Enter User: " + testUser.getDisplayName() + " into the input field");
		filesUI.typeText(FilesUIConstants.shareFolderDialogPersonInputField, testUser.getDisplayName());

		log.info("INFO: Select User: " + testUser.getDisplayName() + " from typeahead results");
		hUI.typeaheadSelection(testUser.getDisplayName(), FilesUIConstants.shareFolderDialogPersonTypeahead);

		log.info("INFO: Click on the 'Share' button to save change");
		filesUI.clickLinkWithJavascript(FilesUIConstants.shareFolderDialogShareButton);
	}
}
