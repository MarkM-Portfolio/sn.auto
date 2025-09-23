package com.ibm.conn.auto.tests.files.unit;

import java.awt.Robot;
import java.awt.event.InputEvent;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FileViewerUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class FilesUnitBaseSetUp extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FilesUnitBaseSetUp.class);
	protected FilesUI ui;
	protected FileViewerUI uiViewer;
	protected TestConfigCustom cfg;	
	protected User testUser, secondUser, adminUser;
	protected APIFileHandler apiFileOwner;
	protected GatekeeperConfig gkc;
	protected CommunitiesUI cUI;
	protected APICommunitiesHandler apiCommOwner;
	protected BaseCommunity community;
	protected Community comAPI;

	protected String SyncView = "css=div[id='lconnSideNavBody'] div[id='menu_fileSync'] a[id='menu_fileSync_label']";
	protected String SyncExpandLink = "css=div[id='lconnSideNavBody'] div[id='menu_fileSync'] a[id='menu_fileSync_expandLink']";
	protected String SyncChildContainer = "css=div[id='lconnSideNavBody'] div[id='menu_fileSync_childContainer']";
	protected String AddToMyDriveMoreLink = "css=div[id='scene-body'] a[role='button'][id^='lconn_files_action_addcollectiontosync_']";
	protected String RemoveFromMyDriveMoreLink = "css=div[id='scene-body'] a[role='button'][id^='lconn_files_action_removecollectionfromsync_']";
	protected String AddToMyDriveTitleMenu = "css=table[role='menu'][id^='dijit_Menu_'] td[class='dijitReset dijitMenuItemLabel'][id^='lconn_files_action_addcollectiontosync_']";
	protected String RemoveFromMyDriveTitleMenu = "css=table[role='menu'][id^='dijit_Menu_'] td[class='dijitReset dijitMenuItemLabel'][id^='lconn_files_action_removecollectionfromsync_']";
	protected String SyncBanner = "css=div[id='scene-body'] div[id='lotusSyncViewWelcomeBox'][style = 'display: block;']";
	protected String AddFoldersLink = "css=div[id='scene-body'] div[id='lotusSyncViewWelcomeBox'] a[id='lotusSyncAddFoldersButton']";
	protected String BulkAddFoldersToSync = "css=button[id ^= 'lconn_files_action_bulkaddcollectionstosync_']";
	protected String BulkrRmoveFromSync = "css=button[id ^= 'lconn_files_action_removefromfilesync_']";
	protected String MoveToMoreLink = "css=div[id='scene-body'] a[role='button'][id^='lconn_files_action_movecollection_']";
	protected String AddFilesMoreLink = "css=div[id='scene-body'] a[role='button'][id^='lconn_files_action_addfilestocollection_']";
	protected String CustomView = "css=div[id='lotusContentSceneActions'] table[id='qkrViewControl'] a[aria-label= 'Customizable']";
	protected String BulkAddToCollection = "css=button[id ^= 'lconn_files_action_bulkaddtocollection']";
	protected String BulkMoveToCollection = "css=button[id ^= 'lconn_files_action_bulkmove']";
	protected String InMyDriveText = "In My Drive";
	protected String MyDriveIndicator = "css=[id ^= 'lconn_files_widget_MyDriveIndicator'] a";
	protected String MyDriveIndicatorItem = "css=[id ^= 'lconn_files_widget_MyDriveIndicator'] td[class *= 'dijitMenuItemLabel']";
	protected int fileSearchTime = 30 * 60 * 1000;
	protected int newFileWaitingTime = 60 * 1000;
    public String openMyFilesView = "css=div[id='lconnSideNavBody'] a[id='myfiles']";
    public String AllFilesView = "css=div[id='lconnSideNavBody'] a[id='menu_allfiles_expandLink'][class*='lotusTwistyClosed']";
    public String MyFoldersLeftMenu= "css=div[id='lconnSideNavBody'] a[id^='menu_myfolders_label']";
    public String AllFoldersLeftMenu= "css=div[id='lconnSideNavBody'] a[id^='menu_allfolders_expandLink'][class*='lotusTwistyClosed']";
    public String PinnedFilesLeftMenu = "css=div[id='lconnSideNavBody'] a[id='pinnedfiles']";
    public String openSharedWithMeView = "css=div[id='lconnSideNavBody'] a[id='withme']";
    public String openCommunityFilesView = "css=div[id='lconnSideNavBody'] a[id='communityFiles']";
    public String openPublicFilesView = "css=div[id='lconnSideNavBody'] a[id='public']";
    public String FoldersSharedWithMeLeftMenu= "css=div[id='lconnSideNavBody'] a[id^='label_sharedfolders_label']";
    public String ShareButtonInDetailsMenu = "css=a[id^=lconn_files_action_sharecollection_]";
    public String ShareCommunityOption = "css=option[value='communities']";
    public String shareFileReaderOption = "css=option[value='reader']";
	public String shareFileEditorOption = "css=option[value='editor']";
	public String shareFolderReaderOption = "css=option[value='reader']";
	public String shareFolderEditorOption = "css=option[value='contributor']";
	public String shareFolderOwnerOption = "css=option[value='manager']";
	public String searchPeopleField = "css=input[id^='lconn_share_widget_MemberInput_']";
	public String searchPeopleResult = "css=div[id='lconn_share_widget_MemberInput_0_people_popup0']";
	public String searchCommunityResult = "css=li[id^='lconn_share_widget_MemberInput_'][role='option']";
	public String shareFolderLink = "css=a[id^='lconn_files_action_sharecollection_']";
	public String shareByLinkItem = "css=td[id^='lconn_files_action_createsharelink'][class*='dijitMenuItemLabel']";
	public String shareByLink = "css=a[id^='lconn_files_action_createsharelink']";
	public String createShareLinkButton = "css=input[value='Create a Link'][class='lotusFormButton'][type='submit']";
	public String shareLinkValue = "css=input[id='shareLinkInput']";
	public String deleteLinkButton = "css=a[class*='deleteLink']";
	public String deleteButton = "css=input[type='submit'][value='Delete'][class='lotusFormButton']";
	public String copyLinkButton = "//a[contains(text(),'Copy')]";
	public String sharePannelInFIDO = "css=li[id='share']";
	public String createShareLinkButtonInFIDO = "css=a[data-dojo-attach-point='createLinkBtn']";
	public String copyShareLinkButtonInFIDO = "css=a[data-dojo-attach-point='copyLinkBtn']";
	public String deleteShareLinkButtonInFIDO = "css=a[data-dojo-attach-point='deleteLinkBtn']";
	public String shareLinkLabelInFIDO = "css=div[class*='linkContent']";
	
	protected void personalFilesSetUpClass(){
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		
		secondUser = cfg.getUserAllocator().getUser();
		
		adminUser = cfg.getUserAllocator().getAdminUser();
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiFileOwner= new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		uiViewer = FileViewerUI.getGui(cfg.getProductName(), driver);
		
	}
	
	
	protected void personalFilesSetUp() {
		
		//initialize the configuration
		//cfg = TestConfigCustom.getInstance();
		//ui = FilesUI.getGui(cfg.getProductName(), driver);

		// Load files and login before every case start test
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		cUI.closeGuidedTourPopup();

	}	

	protected void communityFilesSetUpClass() throws Exception {
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		secondUser = cfg.getUserAllocator().getUser();
		adminUser = cfg.getUserAllocator().getAdminUser();
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiCommOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		gkc = GatekeeperConfig.getInstance(serverURL, adminUser);
		
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		uiViewer = FileViewerUI.getGui(cfg.getProductName(), driver);
		
		createCommunity();
	}
	

	protected void communityFilesSetUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		
		// load communities and login before every case start test
		log.info("INFO: Load communities and login");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
	}
	
	protected void createCommunity() throws Exception {

		community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRand())
					                 .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
					                 .tags(Data.getData().commonTag)
					                 .description("Test description for testcase ").build();
		// create community
		log.info("INFO: Create community using API");
		comAPI = community.createAPI(apiCommOwner);
		
		// add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiCommOwner, comAPI);


	 }
	
	protected String selectFolderinPicker(String folderName)throws Exception {
		String checkbox = "css=div[id ^= 'lconn_share_widget_Dialog_'] div[id ^= 'lconn_files_widget_PersonalFolderPicker_'] " +
				"div[title = '" +  folderName   + "'] input[type = 'checkbox']";
		return checkbox;
	}
	
	protected boolean isGridView()throws Exception {
		String gridView = "css=table[id='qkrViewControl'] a[class ~= 'lotusTileOn']";
		boolean inGridView = ui.isElementPresent(gridView);
		return inGridView;
	}
	
	protected boolean isListView()throws Exception {
		String listView = "css=table[id='qkrViewControl'] a[class ~= 'lotusDetailsOn']";
		boolean inListView = ui.isElementPresent(listView);
		return inListView;
	}
	
	protected void clickMoreButton(String folderName) throws Exception {
		String moreButton = "//tr[descendant::a[@title='" + folderName + "']]//a[text()='More']";
		ui.clickLinkWithJavascript(moreButton);
	}
	
	protected void selectMenuInPicker(String menuName)throws Exception {
		String meun = "css=div[id ^= 'lconn_share_widget_Dialog_'] div[class ='lconnPickerSourceArea']" +
				" option[value = '" + menuName + "']";
		ui.clickLinkWithJavascript(meun);
	}
	
	protected void selectItemsByCheckBox(String name)throws Exception {
        String checkBox = "css=input[type='checkbox'][title='"+ name +"']";
        ui.clickLinkWithJavascript(checkBox);
    }
    
    public void DragAndDrop(Element source, Element target) throws Exception {
        Actions acts = new Actions(source.getWebDriverExecutor().wd());
        try{
          Action action;
          Robot robot = new Robot();
          robot.setAutoDelay(500);
          WebElement dragFrom = source.getWebElement();
          WebElement dragTo = target.getWebElement();

          acts.clickAndHold(dragFrom).moveByOffset(30,30);
          
          Thread.sleep(1000);
          action = acts.build();  
          action.perform();

          acts.moveToElement(dragTo).build().perform();

          Thread.sleep(500);
          robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }
        catch (StaleElementReferenceException se) {
            throw se;
        }
    }
    
    public void clickMyFilesView() {
      try {
        //Open the My Files view
        log.info("INFO: Open the My Files view");
        ui.clickLinkWait(openMyFilesView);
      } catch (TimeoutException te) {
        // Old UI: All Files exists, click on it first to expand
        if(ui.isElementPresent(AllFilesView)) {
           ui.clickLinkWait(AllFilesView);
        }
        log.info("INFO: Open the My Files view");
        ui.clickLinkWait(openMyFilesView);
      }
    }
    
    public void clickMyFoldersView(){
      try {
         //Click on My Folder to see your folder
         log.info("INFO: Click on My Folder");
         ui.clickLinkWait(MyFoldersLeftMenu);
      } catch (TimeoutException te) {
    	 // Old UI:  If All Folders exists, click on it first to expand
        if(ui.isElementPresent(AllFoldersLeftMenu)) {
           ui.clickLinkWait(AllFoldersLeftMenu);
        }
        log.info("INFO: Click on My Folder");
        ui.clickLinkWait(MyFoldersLeftMenu);  
      }
    }
    
	/**
	 * share file to people
	 * @param testuser and share role: Reader and Editor
	 */
	public void shareFileToPeople(User sharedPeople, String role ){
		if (ui.isElementPresent(FilesUIConstants.shareDropDown)) {
			ui.clickLink(FilesUIConstants.shareDropDown);
			ui.clickLink(FilesUIConstants.shareInDropDown);
		}
		else {
			//click share button
			ui.clickLink(FilesUIConstants.shareLink);
		}
		//in share dialog, click share with people radio button
		ui.clickLink(FilesUIConstants.shareWithPeople);
		//select share role
		if(role == "Reader"){
				ui.clickLink(shareFileReaderOption);
		} else if(role == "Editor"){
				ui.clickLink(shareFileEditorOption);
		} else{
			log.error("ERROR: Input role is invalid: " + role);
		}
		//input shared people email
		ui.typeText(searchPeopleField, sharedPeople.getEmail());
		//select searched result
		ui.clickLink(searchPeopleResult);
		//Share
		ui.clickButton("Share");
		//verify the UI message
		ui.isTextPresent("The file was shared successfully.");
		
	}
	
	
	/**
	 * share folder to people
	 * @param shared people and share role: Reader, Editor and Owner
	 */
	public void shareFolderToPeople(User sharedPeople, String role ){
		//click share button
		ui.clickLink(shareFolderLink);
		//in share dialog, click share with people radio button
		ui.clickLink(FilesUIConstants.shareWithPeople);
		//select share role
		if(role == "Reader"){
			ui.clickLink(shareFolderReaderOption);
		} else if(role == "Editor"){
			ui.clickLink(shareFolderEditorOption);
		} else if(role == "Owner"){
			ui.clickLink(shareFolderOwnerOption);
		} else{
			log.error("ERROR: Input role is invalid: " + role);
		}
		//input shared people email
		ui.typeText(searchPeopleField, sharedPeople.getEmail());
		//select searched result
		ui.clickLink(searchPeopleResult);
		//Share
		ui.clickButton("Share");
		//verify the UI message
		ui.isTextPresent("The file was shared successfully.");
		
	}
	
	public void previewFileInFido(String fileName) {
		String fileLink = "css=a[title='" + fileName + "']";
		ui.clickLink(fileLink);
	}
}
