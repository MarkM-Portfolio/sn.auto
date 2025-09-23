package com.ibm.conn.auto.tests.communities.regression;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.library.FileThumbnailWidget;
import com.ibm.conn.auto.appobjects.library.FolderThumbnailWidget;
import com.ibm.conn.auto.appobjects.library.LibraryWidget;
import com.ibm.conn.auto.appobjects.library.ViewSelector;
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
import com.ibm.conn.auto.util.menu.Widget_Action_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class CommLibraryWidget extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Member.class);
	private CommunitiesUI ui;
	private FilesUI filesUI;
	private TestConfigCustom cfg;	
	private User testUser,testUser1;
	private APICommunitiesHandler apiOwner;
	private BaseFile file1;
	private String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		cfg = TestConfigCustom.getInstance();

		//Load Users
		testUser = cfg.getUserAllocator().getUser();
		testUser1 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());		

		file1 = new BaseFile.Builder(Data.getData().file1)
							.extension(".jpg")
							.build();
		
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
	}

	//Test case is marked as disabled because library api is no more supported after connection 6.5
	/**
	* verifyChangeSharingLink()
	*<ul>
	*<li><B>Info:</B> Sharing panel (Part 1) - Validate link message 'Change Sharing on ...'</li>
	*<li><B>Step:</B> Create a moderated community</li>
	*<li><B>Step:</B> Add Library widget using API</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> Open Library widget full page</li>
	*<li><B>Verify:</B> Link message - Change sharing on Library</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FFBBF324B0C21B1448257F8D001971E7">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - Sharing panel</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	@Test(groups = {"regression"} , enabled=false )
	public void verifyChangeSharingLink() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.MODERATED)	
													.description("Description: " + testName)
													.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Library widget
		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//Load component and login as community owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Choose Library link from the left nav 
		log.info("INFO: Select Library from left nav");
		Community_LeftNav_Menu.LIBRARY.select(ui);
		
		//Validate link message - 'Change sharing on Library'
		log.info("INFO: Validate link message - Change sharing on Library");
		Assert.assertTrue(ui.fluentWaitTextPresent("Change sharing on Library"),
						  "ERROR: Link message 'Change sharing on Library' is not present");

		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}		
	

	/**
	* verifyChangeSharingLinkWithUpdatedLibName()
	*<ul>
	*<li><B>Info:</B> Sharing panel (Part 2) - Validate link message 'Change Sharing on ...' when library name has changed</li>
	*<li><B>Step:</B> Create a moderated community</li>
	*<li><B>Step:</B> Add Library widget using API</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> On Overview page, click on Actions for Library/Change Title</li>
	*<li><B>Step:</B> Clear default widget title string</li>
	*<li><B>Step:</B> Enter new widget title</li>
	*<li><B>Step:</B> Save the change</li>
	*<li><B>Step:</B> Select Library from left nav to open Library full page</li>
	*<li><B>Verify:</B> Link message - Change sharing on UpdatedLibraryName</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FFBBF324B0C21B1448257F8D001971E7">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - Sharing panel</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	//Test case is marked as disabled because library api is no more supported after connection 6.5
	@Test(groups = {"regression"} , enabled=false )
	public void verifyChangeSharingLinkWithUpdatedLibName() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		String newWidgetName = "Library - RENAMED";	
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.MODERATED)	
													.description("Description: " + testName)
													.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Library widget
		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//Load component and login as community owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

        //On Overview page, click on Actions for Library/Change Title
		log.info("INFO: Click on Actions for Library/Change Title");
		ui.performCommWidgetAction(BaseWidget.LIBRARY, Widget_Action_Menu.CHANGETITLE);
								
		//Clear default widget title string
		log.info("INFO: Clear default widget title string");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).clear();
		
		//Enter new widget title
		log.info("INFO: Enter new widget title");
		driver.getFirstElement(CommunitiesUIConstants.widgetChangeTitleInput).type(newWidgetName);
	
		//Save the new widget title
		log.info("INFO: Save the new widget title");
		ui.clickLinkWait(CommunitiesUIConstants.widgetChangeTitleSaveButton);
		
		//Choose Library link from the left nav
		log.info("INFO: Select Library from left nav");
		Community_LeftNav_Menu.LIBRARY.select(ui);
				
		//Validate link message - 'Change sharing on ...'
		log.info("INFO: Validate link message - Change sharing on " + newWidgetName);
		Assert.assertTrue(ui.fluentWaitTextPresent("Change sharing on " + newWidgetName),
						  "ERROR: Link message 'Change sharing on " + newWidgetName + "' is not present");

		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}		

	
	/**
	* NoRolesInEditLibSettingsOnEditCommunity()
	*<ul>
	*<li><B>Info:</B> UI changes in Edit Library Settings (Part 1) - No more Roles section in "Edit" settings mode for the Library widget on Edit Community page</li>
	*<li><B>Step:</B> Create a Public community as owner using API & add one member to the community</li>
	*<li><B>Step:</B> Add Library widget using API</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> Click on Community Actions on Overview page</li>
	*<li><B>Step:</B> Select Edit Community from the menu dropdown list</li>
	*<li><B>Step:</B> Click on Library tab</li>
	*<li><B>Verify:</B> No Roles section</li>
	*<li><B>Verify:</B> Informational message - Access can be set on the sharing panel for the library.</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DB013D0BD3D994EC48257F8D0013E214">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - "Edit" settings mode change</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	//Test case is marked as disabled because library api is no more supported after connection 6.5
	@Test(groups = {"regression"} , enabled=false )
	public void NoRolesInEditLibSettingsOnEditCommunity() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);	
		
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.PUBLIC)	
													.description("Description: " + testName)
													.addMember(member)
													.build();
		
		
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
				
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		log.info("INFO: Click on Community Actions");
		ui.clickLinkWait(BaseUIConstants.Community_Actions_Button);

		log.info("INFO: Click on the Edit Community link");
		ui.clickLinkWait(CommunitiesUIConstants.EditCommunity);

		log.info("INFO: Click on the Edit Community Library tab");
		ui.clickLinkWait(CommunitiesUIConstants.editCommunityLibraryTab);
		
		log.info("INFO: Verify NO Roles section");
		Assert.assertFalse(ui.isTextPresent("Roles:"),
				  "ERROR: Roles section is still present");
		
		log.info("INFO: Validate informational message - Access can be set on the sharing panel for the library.");		
		Assert.assertTrue(ui.fluentWaitTextPresent("Access can be set on the sharing panel for the library."),
				  "ERROR: Message 'Access can be set on the sharing panel for the library.' is not present");
				
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}		
	
	
	/**
	* NoRolesInEditLibSettingsOnOverview()
	*<ul>
	*<li><B>Info:</B> UI changes in Edit Library Settings (Part 2) - No more Roles section in "Edit" settings mode for the Library widget on Overview page</li>
	*<li><B>Step:</B> Create a Public community as owner using API & add one member to the community</li>
	*<li><B>Step:</B> Add Library widget using API</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> Navigate to Library widget on Overview page</li>
	*<li><B>Step:</B> Click on Actions for: Library</li>
	*<li><B>Step:</B> Select Edit from the menu dropdown list</li>
	*<li><B>Verify:</B> No Roles section</li>
	*<li><B>Verify:</B> Informational message - Access can be set on the sharing panel for the library.</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/DB013D0BD3D994EC48257F8D0013E214">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - "Edit" settings mode change</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	//Test case is marked as disabled because library api is no more supported after connection 6.5
	@Test(groups = {"regression"} , enabled=false )
	public void NoRolesInEditLibSettingsOnOverview() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);	
		
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.PUBLIC)	
													.description("Description: " + testName)
													.addMember(member)
													.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Library widget
		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//Load component and login as community owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
        //Open Edit Library Setting by clicking on Actions for Library/Edit
		log.info("INFO: Open Edit Library Setting by clicking on Actions for Library/EditD");
		ui.performCommWidgetAction(BaseWidget.LIBRARY, Widget_Action_Menu.EDIT);

		//Verify No Roles section
		log.info("INFO: Verify NO Roles section");
		Assert.assertFalse(ui.isTextPresent("Roles:"),
				  "ERROR: Roles section is still present");
		
		//Validate informational message - 'Access can be set on the sharing panel for the library.'
		log.info("INFO: Validate informational message - Access can be set on the sharing panel for the library.");		
		Assert.assertTrue(ui.fluentWaitTextPresent("Access can be set on the sharing panel for the library."),
				  "ERROR: Message 'Access can be set on the sharing panel for the library.' is not present");
				
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}	
	
	
	/**
	* verifyFileNotInheritLibAccessAfterFileShare()
	*<ul>
	*<li><B>Info:</B> Inherited user roles (Part 4) - file no longer inherit library access when file makes sharing changes</li>
	*<li><B>Step:</B> Create a Public community as owner using API & add one member to the community</li>
	*<li><B>Step:</B> Add Library widget using API</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> Navigate to Library widget</li>
	*<li><B>Step:</B> Upload a file</li>
	*<li><B>Step:</B> Go to file's details page</li>
	*<li><B>Step:</B> Click on More Actions/Share button</li>
	*<li><B>Step:</B> Share the file with community member as editor</li>
	*<li><B>Verify:</B> Check each role in Sharing panel - community member should be added as editor at file level</li>
	*<li><B>Step:</B> Got back to Library main page</li>
	*<li><B>Verify:</B> Check each role in Sharing panel - community member should not be added as editor at library level</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FB5570138C3E55C348257F8400117DD9">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - inherited access</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	//Test case is marked as disabled because library api is no more supported after connection 6.5
	@Test(groups = {"regression"} , enabled=false )
	public void verifyFileNotInheritLibAccessAfterFileShare() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);	
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.PUBLIC)	
													.description("Description: " + testName)
													.addMember(member)
													.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Library widget
		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//Load component and login as community owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Choose Library link from the left nav
		log.info("INFO: Select Library from left nav");
		Community_LeftNav_Menu.LIBRARY.select(ui);
	
		//Upload a file
		log.info("INFO: Upload a file");
		filesUI.libraryFileUpload(file1.getName());
				
        //Grid View selector is present and active by default
		log.info("INFO: Verify that the Grid View selector is present and active by default");
		LibraryWidget libraryWidget = this.getLibraryWidgetOnFullWidgetPage();
		ViewSelector viewSelector = libraryWidget.getDocMain().getViewSelector();
		Assert.assertTrue(viewSelector.isCurrentView(ViewSelector.View.GRID_VIEW),
						 "ERROR: Unable to find the Library Grid View Selector or it is not the currently active view");
		
		//Go to file's details page
		FileThumbnailWidget fileTW = libraryWidget.getDocMain().getFileThumbnailWidgetByName(file1.getName());
		log.info("INFO: Verify " + fileTW.getId() + " is present");
		Assert.assertNotNull(fileTW.getWidgetElement(),
				"ERROR: Unable to find " + fileTW.getId() + " in the Grid View");
		log.info("INFO: Go to file's details page by click 'View file details' icon on file Thumbnail");
		fileTW.getSummaryActionLink().click();
		
		//Verify file's details page is displayed by checking correct filename is present
		log.info("INFO: Verify file's details page is displayed by checking filename " + file1.getName() + " is present");
		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.LibraryDocSummaryFileTitleWithTextSuffix+"('"+file1.getName()+"')").getText().contains(file1.getName()),
							"ERROR: Unable to find the file's title on details page");

		//Click on More Actions/Share menu item to open the Change Sharing dialog
		log.info("INFO: Click on More Actions/Share menu item to open the Change Sharing dialog");
		ui.clickLink(CommunitiesUIConstants.LibraryFileMoreActions);
		ui.clickLink(CommunitiesUIConstants.LibraryFileShareMenuItem);

		//Clear the typeahead text field
		log.info("INFO: Clear the typeahead text field");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadTextField).clear();

		//Enter member's email address in typeahead text field
		log.info("INFO: Enter member's email address in typeahead text field");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadTextField).type(member.getUser().getEmail());
		
		//Select the result from the typeahead dropdown
		log.info("INFO: Select the result from the typeahead dropdown");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadDropDown).click();
		
		//Click on OK button to close the Share Library dialog
		log.info("INFO: Click on OK button to close the Share Library dialog");		
		ui.clickOKButton();
	
		//Click on Sharing tab
		log.info("INFO: Click on Sharing tab");
		ui.clickLinkWait(CommunitiesUIConstants.LibraryFileSharingTab);
		
		//Verify each role - Readers, Contributors, Editors and Owners
		log.info("INFO: Verify Readers");			
		Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.LibraryEveryoneInMyOrg),
				  "ERROR: Everyone In My Organization is not present");
		log.info("INFO: Verify NO Contributors");
		Assert.assertFalse(ui.isElementPresent(CommunitiesUIConstants.LibraryCommunityMembers),
				  "ERROR: Communitiy Members should not be present");
		log.info("INFO: Verify Editors - " + member.getUser().getDisplayName() + " should be added as editor at file level");	
		Assert.assertTrue(ui.fluentWaitTextPresent(member.getUser().getDisplayName()),
				  "ERROR: Editor member is not present");
		log.info("INFO: Verify Owners");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityOwners),
				  "ERROR: Community Owners is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser.getDisplayName() + " (File creator)"),
				  "ERROR: File creator is not present");		
		
		//Go back to Library Main page
    	log.info("INFO: Go back to Library Main page");
		libraryWidget.getDocSummary().getBreadcrumbs().goBackToLibrary();		
			
		//Verify each role - Readers, Contributor, Editors and Owners
		log.info("INFO: Verify Readers");
		Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.LibraryEveryoneInMyOrg),
				  "ERROR: EveryoneIn My Organization is not present");
		log.info("INFO: Verify Contributors");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityMembers),
				  "ERROR: Communitiy Members is not present");
		log.info("INFO: Verify Editors");
		Assert.assertTrue(ui.fluentWaitTextPresent("None"),
				  "ERROR: None is not present");
		log.info("INFO: Verify " + member.getUser().getDisplayName() + " should not be added as editor at Library level");
		Assert.assertFalse(ui.isTextPresent(member.getUser().getDisplayName()),
				  "ERROR: " + member.getUser().getDisplayName() + " is present");
		log.info("INFO: Verify Owners");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityOwners),
				  "ERROR: Community Owners is not present");
		
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}	
	
	
	/**
	* verifyFolderNFileInheritLibAccessAfterLibShare()
	*<ul>
	*<li><B>Info:</B> Inherited user roles (Part 2) - folder and file inherit library access after changing Library sharing</li>
	*<li><B>Step:</B> Create a Public community as owner using API & add one member to the community</li>
	*<li><B>Step:</B> Add Library widget using API</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> Navigate to Library widget</li>
	*<li><B>Step:</B> Create a folder</li>
	*<li><B>Step:</B> Upload a file</li> 
	*<li><B>Step:</B> At Library level, click on Share button</li>
	*<li><B>Step:</B> Share the library with community member as editor</li>
	*<li><B>Step:</B> Go to folder's details page</li>
	*<li><B>Verify:</B> Check each role in Sharing panel - folder should inherit library access</li>
	*<li><B>Step:</B> Got to file's details page</li>
	*<li><B>Verify:</B> Check each role in Sharing panel - file should inherit library access</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FB5570138C3E55C348257F8400117DD9">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - inherited access</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	//Test case is marked as disabled because library api is no more supported after connection 6.5
	@Test(groups = {"regression"} , enabled=false )
	public void verifyFolderNFileInheritLibAccessAfterLibShare() throws Exception {
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);	
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.PUBLIC)	
													.description("Description: " + testName)
													.addMember(member)
													.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Library widget
		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//Load component and login as community owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Choose Library link from the left nav
		log.info("INFO: Select Library from left nav");
		Community_LeftNav_Menu.LIBRARY.select(ui);
	
		//Create a new Folder	
		log.info("INFO: Create a new Folder");
		ui.createNewFolderForLibrary(Data.getData().FolderName, Data.getData().FolderDescription);	
		
		//Upload a file
		log.info("INFO: Upload a file");
		filesUI.libraryFileUpload(file1.getName());

		//Click on Share button to open the Share Library dialog
		log.info("INFO: Click on Share button to open the Share Library dialog");
		ui.clickLink(CommunitiesUIConstants.LibraryShareButton);

		//Clear the typeahead text field
		log.info("INFO: Clear the typeahead text field");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadTextField).clear();

		//Enter member's email address in typeahead text field
		log.info("INFO: Enter member's email address in typeahead text field");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadTextField).type(member.getUser().getEmail());
		
		//Select the result from the typeahead dropdown
		log.info("INFO: Select the result from the typeahead dropdown");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadDropDown).click();
		
		//Click on OK button to close the Share Library dialog
		log.info("INFO: Click on OK button to close the Share Library dialog");		
		ui.clickOKButton();
		
        //Grid View selector is present and active by default
		log.info("INFO: Verify that the Grid View selector is present and active by default");
		LibraryWidget libraryWidget = this.getLibraryWidgetOnFullWidgetPage();
		ViewSelector viewSelector = libraryWidget.getDocMain().getViewSelector();
		Assert.assertTrue(viewSelector.isCurrentView(ViewSelector.View.GRID_VIEW),
						 "ERROR: Unable to find the Library Grid View Selector or it is not the currently active view");
		
		//Open folder's details page by clicking on folder thumbnail
		log.info("INFO: Open folder's details page by clicking on folder thumbnail");
		FolderThumbnailWidget folderTW = libraryWidget.getDocMain().getFolderThumbnailWidgetByName(Data.getData().FolderName);
		folderTW.getBackSideActionLink().click();

		//Click on Sharing tab
		log.info("INFO: Click on Sharing tab");
		ui.clickLinkWait(CommunitiesUIConstants.LibrarySharingTab);
			
		//Verify each role - Readers, Contributors, Editors and Owners
		log.info("INFO: Verify Readers");			
		Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.LibraryEveryoneInMyOrg),
				  "ERROR: Everyone In My Organization is not present");
		log.info("INFO: Verify Contributors");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityMembers),
				  "ERROR: Communitiy Members is not present");
		log.info("INFO: Verify Editors");	
		Assert.assertTrue(ui.fluentWaitTextPresent(member.getUser().getDisplayName()),
				  "ERROR: Editor member is not present");
		log.info("INFO: Verify Owners");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityOwners),
				  "ERROR: Community Owners is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser.getDisplayName() + " (Folder creator)"),
				  "ERROR: Folder creator is not present");		
		
		//Go back to Library Main page
    	log.info("INFO: Go back to Library Main page");
		libraryWidget.getDocMain().getBreadcrumbs().goBackToLibrary();		
		
		//Go to file's details page
		FileThumbnailWidget fileTW = libraryWidget.getDocMain().getFileThumbnailWidgetByName(file1.getName());
		log.info("INFO: Verify " + fileTW.getId() + " is present");
		Assert.assertNotNull(fileTW.getWidgetElement(),
				"ERROR: Unable to find " + fileTW.getId() + " in the Grid View");
		log.info("INFO: Go to file's details page by click 'View file details' icon on file Thumbnail");
		fileTW.getSummaryActionLink().click();
		
		//Verify file's details page is displayed by checking correct filename is present
		log.info("INFO: Verify file's details page is displayed by checking filename " + file1.getName() + " is present");
		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.LibraryDocSummaryFileTitleWithTextSuffix+"('"+file1.getName()+"')").getText().contains(file1.getName()),
							"ERROR: Unable to find the file's title on details page");
		
		//Click on Sharing tab
		log.info("INFO: Click on Sharing tab");
		ui.clickLinkWait(CommunitiesUIConstants.LibraryFileSharingTab);
 		
		//Verify each role - Readers, Editors and Owners
		log.info("INFO: Verify Readers");
		Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.LibraryEveryoneInMyOrg),
				  "ERROR: EveryoneIn My Organization is not present");
		log.info("INFO: Verify NO Contributors");
		Assert.assertFalse(ui.isElementPresent(CommunitiesUIConstants.LibraryCommunityMembers),
				  "ERROR: Communitiy Members should not be present");
		log.info("INFO: Verify Editors");
		Assert.assertTrue(ui.fluentWaitTextPresent(member.getUser().getDisplayName()),
				  "ERROR: Editor member is not present");
		log.info("INFO: Verify Owners");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityOwners),
				  "ERROR: Community Owners is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser.getDisplayName() + " (File creator)"),
				  "ERROR: File creator is not present");	
				
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}	
	
	
	/**
	* verifyFolderNFileInheritLibAccessByDefault()
	*<ul>
	*<li><B>Info:</B> Inherited user roles (Part 1) - folder and file inherit library access by default</li>
	*<li><B>Step:</B> Create a Public community as owner using API & add one member to the community</li>
	*<li><B>Step:</B> Add Library widget using API</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> Navigate to Library widget</li>
	*<li><B>Step:</B> Create a folder</li>
	*<li><B>Step:</B> Upload a file</li> 
	*<li><B>Step:</B> Go to folder's details page</li>
	*<li><B>Verify:</B> Check each role in Sharing panel - folder should inherit library access</li>
	*<li><B>Step:</B> Got to file's details page</li>
	*<li><B>Verify:</B> Check each role in Sharing panel - file should inherit library access</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FB5570138C3E55C348257F8400117DD9">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - inherited access</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	//Test case is marked as disabled because library api is no more supported after connection 6.5
	@Test(groups = {"regression"} , enabled=false )
	public void verifyFolderNFileInheritLibAccessByDefault() throws Exception {
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);	
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.PUBLIC)	
													.description("Description: " + testName)
													.addMember(member)
													.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Library widget
		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//Load component and login as community owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Choose Library link from the left nav
		log.info("INFO: Select Library from left nav");
		Community_LeftNav_Menu.LIBRARY.select(ui);
	
		//Create a new Folder	
		log.info("INFO: Create a new Folder");
		ui.createNewFolderForLibrary(Data.getData().FolderName, Data.getData().FolderDescription);	
		
		//Upload a file
		log.info("INFO: Upload a file");
		filesUI.libraryFileUpload(file1.getName());

        //Grid View selector is present and active by default
		log.info("INFO: Verify that the Grid View selector is present and active by default");
		LibraryWidget libraryWidget = this.getLibraryWidgetOnFullWidgetPage();
		ViewSelector viewSelector = libraryWidget.getDocMain().getViewSelector();
		Assert.assertTrue(viewSelector.isCurrentView(ViewSelector.View.GRID_VIEW),
						 "ERROR: Unable to find the Library Grid View Selector or it is not the currently active view");
		
		//Open folder's details page by clicking on folder thumbnail
		log.info("INFO: Open folder's details page by clicking on folder thumbnail");
		FolderThumbnailWidget folderTW = libraryWidget.getDocMain().getFolderThumbnailWidgetByName(Data.getData().FolderName);
		folderTW.getBackSideActionLink().click();

		//Click on Sharing tab
		log.info("INFO: Click on Sharing tab");
		ui.clickLinkWait(CommunitiesUIConstants.LibrarySharingTab);
			
		//Verify each role - Readers, Contributors, Editors and Owners
		log.info("INFO: Verify Readers");			
		Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.LibraryEveryoneInMyOrg),
				  "ERROR: Everyone In My Organization is not present");
		log.info("INFO: Verify Contributors");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityMembers),
				  "ERROR: Communitiy Members is not present");
		log.info("INFO: Verify Editors");	
		Assert.assertTrue(ui.fluentWaitTextPresent("None"),
				  "ERROR: None is not present");
		log.info("INFO: Verify Owners");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityOwners),
				  "ERROR: Community Owners is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser.getDisplayName() + " (Folder creator)"),
				  "ERROR: Folder creator is not present");		
		
		//Go back to Library Main page
    	log.info("INFO: Go back to Library Main page");
		libraryWidget.getDocMain().getBreadcrumbs().goBackToLibrary();		
		
		//Go to file's details page
		FileThumbnailWidget fileTW = libraryWidget.getDocMain().getFileThumbnailWidgetByName(file1.getName());
		log.info("INFO: Verify " + fileTW.getId() + " is present");
		Assert.assertNotNull(fileTW.getWidgetElement(),
				"ERROR: Unable to find " + fileTW.getId() + " in the Grid View");
		log.info("INFO: Go to file's details page by click 'View file details' icon on file Thumbnail");
		fileTW.getSummaryActionLink().click();
		
		//Verify file's details page is displayed by checking correct filename is present
		log.info("INFO: Verify file's details page is displayed by checking filename " + file1.getName() + " is present");
		Assert.assertTrue(driver.getSingleElement(CommunitiesUIConstants.LibraryDocSummaryFileTitleWithTextSuffix+"('"+file1.getName()+"')").getText().contains(file1.getName()),
							"ERROR: Unable to find the file's title on details page");
		
		//Click on Sharing tab
		log.info("INFO: Click on Sharing tab");
		ui.clickLinkWait(CommunitiesUIConstants.LibraryFileSharingTab);
 		
		//Verify each role - Readers, Editors and Owners
		log.info("INFO: Verify Readers");
		Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.LibraryEveryoneInMyOrg),
				  "ERROR: EveryoneIn My Organization is not present");
		log.info("INFO: Verify NO Contributors");
		Assert.assertFalse(ui.isElementPresent(CommunitiesUIConstants.LibraryCommunityMembers),
				  "ERROR: Communitiy Members should not be present");
		log.info("INFO: Verify Editors");
		Assert.assertTrue(ui.fluentWaitTextPresent("None"),
				  "ERROR: None is not present");
		log.info("INFO: Verify Owners");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityOwners),
				  "ERROR: Community Owners is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser.getDisplayName() + " (File creator)"),
				  "ERROR: File creator is not present");	
				
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}	

	
	/**
	* verifyFolderNotInheritLibAccessAfterFolderShare()
	*<ul>
	*<li><B>Info:</B> Inherited user roles (Part 3) - folder no longer inherit library access when folders make sharing changes</li>
	*<li><B>Step:</B> Create a Public community as owner using API & add one member to the community</li>
	*<li><B>Step:</B> Add Library widget using API</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> Navigate to Library widget</li>
	*<li><B>Step:</B> Create a folder</li>
	*<li><B>Step:</B> Go to folder's details page</li>
	*<li><B>Step:</B> Click on Share button</li>
	*<li><B>Step:</B> Share the folder with community member as editor</li>
	*<li><B>Verify:</B> Check each role in Sharing panel - community member should be added as editor at folder level</li>
	*<li><B>Step:</B> Got back to Library main page</li>
	*<li><B>Verify:</B> Check each role in Sharing panel - community member should not be added as editor at library level</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FB5570138C3E55C348257F8400117DD9">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - inherited access</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	//Test case is marked as disabled because library api is no more supported after connection 6.5
	@Test(groups = {"regression"} , enabled=false )
	public void verifyFolderNotInheritLibAccessAfterFolderShare() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);	
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.PUBLIC)	
													.description("Description: " + testName)
													.addMember(member)
													.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Library widget
		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//Load component and login as community owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Choose Library link from the left nav
		log.info("INFO: Select Library from left nav");
		Community_LeftNav_Menu.LIBRARY.select(ui);
	
		//Create a new Folder	
		log.info("INFO: Create a new Folder");
		ui.createNewFolderForLibrary(Data.getData().FolderName, Data.getData().FolderDescription);	
				
        //Grid View selector is present and active by default
		log.info("INFO: Verify that the Grid View selector is present and active by default");
		LibraryWidget libraryWidget = this.getLibraryWidgetOnFullWidgetPage();
		ViewSelector viewSelector = libraryWidget.getDocMain().getViewSelector();
		Assert.assertTrue(viewSelector.isCurrentView(ViewSelector.View.GRID_VIEW),
						 "ERROR: Unable to find the Library Grid View Selector or it is not the currently active view");
		
		//Open folder's details page by clicking on folder thumbnail
		log.info("INFO: Open folder's details page by clicking on folder thumbnail");
		FolderThumbnailWidget folderTW = libraryWidget.getDocMain().getFolderThumbnailWidgetByName(Data.getData().FolderName);
		folderTW.getBackSideActionLink().click();

		//Click on Share button to open the Change Sharing dialog
		log.info("INFO: Click on Share button to open the Share Library dialog");
		ui.clickLink(CommunitiesUIConstants.LibraryFolderShareButton);

		//Clear the typeahead text field
		log.info("INFO: Clear the typeahead text field");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadTextField).clear();

		//Enter member's email address in typeahead text field
		log.info("INFO: Enter member's email address in typeahead text field");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadTextField).type(member.getUser().getEmail());
		
		//Select the result from the typeahead dropdown
		log.info("INFO: Select the result from the typeahead dropdown");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadDropDown).click();
		
		//Click on OK button to close the Share Library dialog
		log.info("INFO: Click on OK button to close the Share Library dialog");		
		ui.clickOKButton();
	
		//Click on Sharing tab
		log.info("INFO: Click on Sharing tab");
		ui.clickLinkWait(CommunitiesUIConstants.LibrarySharingTab);
		
		//Verify each role - Readers, Contributors, Editors and Owners
		log.info("INFO: Verify Readers");			
		Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.LibraryEveryoneInMyOrg),
				  "ERROR: Everyone In My Organization is not present");
		log.info("INFO: Verify Contributors");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityMembers),
				  "ERROR: Communitiy Members is not present");
		log.info("INFO: Verify Editors - " + member.getUser().getDisplayName() + " should be added as editor at folder level");	
		Assert.assertTrue(ui.fluentWaitTextPresent(member.getUser().getDisplayName()),
				  "ERROR: Editor member is not present");
		log.info("INFO: Verify Owners");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityOwners),
				  "ERROR: Community Owners is not present");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser.getDisplayName() + " (Folder creator)"),
				  "ERROR: Folder creator is not present");		
		
		//Go back to Library Main page
    	log.info("INFO: Go back to Library Main page");
		libraryWidget.getDocMain().getBreadcrumbs().goBackToLibrary();		
			
		//Verify each role - Readers, Contributor, Editors and Owners
		log.info("INFO: Verify Readers");
		Assert.assertTrue(ui.fluentWaitTextPresent(CommunitiesUIConstants.LibraryEveryoneInMyOrg),
				  "ERROR: EveryoneIn My Organization is not present");
		log.info("INFO: Verify Contributors");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityMembers),
				  "ERROR: Communitiy Members is not present");
		log.info("INFO: Verify Editors");
		Assert.assertTrue(ui.fluentWaitTextPresent("None"),
				  "ERROR: None is not present");
		log.info("INFO: Verify " + member.getUser().getDisplayName() + " should not be added as editor at Library level");
		Assert.assertFalse(ui.isTextPresent(member.getUser().getDisplayName()),
				  "ERROR: " + member.getUser().getDisplayName() + " is present");
		log.info("INFO: Verify Owners");	
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.LibraryCommunityOwners),
				  "ERROR: Community Owners is not present");
					
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}	
	

	/**
	* verifyNonOwnerHasNoSharingRight()
	*<ul>
	*<li><B>Info:</B> Library Sharing panel - non-owner role has no sharing right</li>
	*<li><B>Step:</B> Create a Public community as owner using API & add one member to the community</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> Add Library widget</li>
	*<li><B>Step:</B> Navigate to Library widget</li> 
	*<li><B>Verify:</B> No Share button</li>
	*<li><B>Step:</B> Click on Sharing tab</li>
	*<li><B>Verify:</B> Informational message - 'Only owners can share this library.'</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EA247D324144F37448257F8D00276077">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - Sharing panel - non-owners role</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	//Test case is marked as disabled because library api is no more supported after connection 6.5
	@Test(groups = {"regression"} , enabled=false )
	public void verifyNonOwnerHasNoSharingRight() throws Exception {
		
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.PUBLIC)	
													.description("Description: " + testName)
													.addMember(member)
													.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Library widget
		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//Load component and login as community member
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
				
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Choose Library link from the left nav
		log.info("INFO: Select Library from left nav");
		Community_LeftNav_Menu.LIBRARY.select(ui);

		//Validate no Share button
		log.info("INFO: Validate no Share button");	
		Assert.assertFalse(driver.isElementPresent(CommunitiesUIConstants.LibraryShareButton),
				           "ERROR: Share button is not hidden");		
		
		//Click on Sharing tab
		log.info("INFO: Click on Sharing tab");
		ui.clickLinkWait(CommunitiesUIConstants.LibrarySharingTab);
		
		//Validate informational message - 'Only owners can share this library.'
		log.info("INFO: Validate informational message - 'Only owners can share this library.'");
		Assert.assertTrue(ui.fluentWaitTextPresent("Only owners can share this library."),
						  "ERROR: Message 'Only owners can share this library.' is not present");

		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}
	

	/**
	* verifyResetToDefaultSharingOption()
	*<ul>
	*<li><B>Info:</B> Sharing panel (Part 5) - "Reset to default sharing" option appears in Sharing panel after owner make sharing changes</li>
	*<li><B>Step:</B> Create a Public community as owner using API & add one member to the community</li>
	*<li><B>Step:</B> Add Library widget using API</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> Navigate to Library widget</li>
	*<li><B>Step:</B> At Library level, click on Share button</li>
	*<li><B>Step:</B> Share the library with community member as editor</li>
	*<li><B>Verify:</B> "Reset to default sharing" option appears in Sharing panel</li>
	*<li><B>Step:</B> Click on "Reset to default sharing" option</li>
	*<li><B>Verify:</B> "Reset Library Sharing to Defaults" dialog title</li>
	*<li><B>Verify:</B> Informational message - Are you sure you want to reset sharing on this library? All explicit access will be removed and this cannot be undone.</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FB5570138C3E55C348257F8400117DD9">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - inherited access</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	//Test case is marked as disabled because library api is no more supported after connection 6.5
	@Test(groups = {"regression"} , enabled=false )
	public void verifyResetToDefaultSharingOption() throws Exception {
		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		Member member = new Member(CommunityRole.MEMBERS, testUser1);	
		
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.PUBLIC)	
													.description("Description: " + testName)
													.addMember(member)
													.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Library widget
		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//Load component and login as community owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Choose Library link from the left nav
		log.info("INFO: Select Library from left nav");
		Community_LeftNav_Menu.LIBRARY.select(ui);
	
		//Click on Share button to open the Share Library dialog
		log.info("INFO: Click on Share button to open the Share Library dialog");
		ui.clickLink(CommunitiesUIConstants.LibraryShareButton);

		//Clear the typeahead text field
		log.info("INFO: Clear the typeahead text field");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadTextField).clear();

		//Enter member's email address in typeahead text field
		log.info("INFO: Enter member's email address in typeahead text field");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadTextField).type(member.getUser().getEmail());
		
		//Select the result from the typeahead dropdown
		log.info("INFO: Select the result from the typeahead dropdown");
		driver.getFirstElement(CommunitiesUIConstants.LibraryPeopleTypeAheadDropDown).click();
		
		//Click on OK button to close the Share Library dialog
		log.info("INFO: Click on OK button to close the Share Library dialog");		
		ui.clickOKButton();
	
		//Validate 'Rest to default sharing' link and then click on it to open confirmation dialog
		log.info("INFO: Click on 'Rest to default sharing' link");
		ui.clickLinkWait(CommunitiesUIConstants.LibraryResetToDefaultSharing);
 		
		//Verify dialog title - Reset Library Sharing to Defaults
		log.info("INFO: Verify dialog title - Reset Library Sharing to Defaults");
		Assert.assertTrue(ui.fluentWaitTextPresent("Reset Library Sharing to Defaults"),
				  "ERROR: Dialog title is not present or incorrect");

		//Verify informational message
		log.info("INFO: Verify informational message");
		Assert.assertTrue(ui.fluentWaitTextPresent("Are you sure you want to reset sharing on this library? All explicit access will be removed and this cannot be undone."),
				  "ERROR: Informational message is not present or incorrect");	
		
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}	
	
	
	/**
	* verifyShareLibDlgByClickingChangeSharingLink()
	*<ul>
	*<li><B>Info:</B> Sharing panel (Part 3) - Validate Share Library dialog by clicking on link 'Change Sharing on ...'</li>
	*<li><B>Step:</B> Create a moderated community</li>
	*<li><B>Step:</B> Add Library widget using API</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> Select Library from left nav to open Library full page</li>
	*<li><B>Step:</B> Click on link 'Change Sharing on ...'</li>
	*<li><B>Verify:</B> Share Library dialog title</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FFBBF324B0C21B1448257F8D001971E7">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - Sharing panel</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	//Test case is marked as disabled because library api is no more supported after connection 6.5
	@Test(groups = {"regression"} , enabled=false )
	public void verifyShareLibDlgByClickingChangeSharingLink() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.MODERATED)	
													.description("Description: " + testName)
													.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Library widget
		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//Load component and login as community owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Choose Library link from the left nav 
		log.info("INFO: Select Library from left nav");
		Community_LeftNav_Menu.LIBRARY.select(ui);

		//Click on 'Change Sharing on ...' link
		log.info("INFO: Click on 'Change Sharing on ...' link");
		ui.clickLinkWait(CommunitiesUIConstants.LibraryChangeSharingOnLink);
		
		//Validate dialog title - Share Library
		log.info("INFO: Validate dialog title - Share Library");
		Assert.assertTrue(ui.fluentWaitTextPresent("Share Library"),
						  "ERROR: Dialog title 'Share Library' is not present");
				
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}		
	
	
	/**
	* verifyShareLibDlgByClickingShareButton()
	*<ul>
	*<li><B>Info:</B> Sharing panel (Part 4) - Validate Share Library dialog by clicking on Share button</li>
	*<li><B>Step:</B> Create a moderated community</li>
	*<li><B>Step:</B> Add Library widget using API</li>
	*<li><B>Step:</B> Login to community as owner</li>
	*<li><B>Step:</B> Select Library from left nav to open Library full page</li>
	*<li><B>Step:</B> Click on Share button</li>
	*<li><B>Verify:</B> Share Library dialog title</li>
	*<li><B>Verify:</B> Informational message - Use these settings to modify who can read, edit or contribute to the library.</li>
	*<li><a HREF="Notes://wrangle/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FFBBF324B0C21B1448257F8D001971E7">TTT - Communities - [Allianz] Add sharing tab on library root (143937) - Sharing panel</a></li>
	*</ul>
	*Note: On Prem only	
	*/	
	//Test case is marked as disabled because library api is no more supported after connection 6.5
	@Test(groups = {"regression"} , enabled=false )
	public void verifyShareLibDlgByClickingShareButton() throws Exception {

		String rndNum = Helper.genDateBasedRand();
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + rndNum)
													.access(Access.MODERATED)	
													.description("Description: " + testName)
													.build();
		
		//Create community
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		//Add Library widget
		log.info("INFO: Add Library widget using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.LIBRARY);
		
		//Load component and login as community owner
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
				
		//Navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Choose Library link from the left nav 
		log.info("INFO: Select Library from left nav");
		Community_LeftNav_Menu.LIBRARY.select(ui);

		//Click on Share button to open the Share Library dialog
		log.info("INFO: Click on Share button to open the Share Library dialog");
		ui.clickLink(CommunitiesUIConstants.LibraryShareButton);
		
		//Validate dialog title - Share Library
		log.info("INFO: Validate dialog title - Share Library");
		Assert.assertTrue(ui.fluentWaitTextPresent("Share Library"),
						  "ERROR: Dialog title 'Share Library' is not present");
				
		//Validate informational message - 'Use these settings to modify who can read, edit or contribute to the library.'
		log.info("INFO: Validate information message");
		Assert.assertTrue(ui.fluentWaitTextPresent("Use these settings to modify who can read, edit or contribute to the library."),
						  "ERROR: Information message is not present");
		
		//Clean Up: Delete the community
		log.info("INFO: Delete the community");
		apiOwner.deleteCommunity(comAPI);
		
		ui.endTest();		
	}		
	
	
	/**
	* Check if the LibraryTitle is not present then refreshes the page
	*/		
	private LibraryWidget getLibraryWidgetOnFullWidgetPage() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		logger.strongStep("Get the Library widget on the full widget page");
		//Check if the LibraryTitle is not present and refreshes the page.
		if(!driver.getSingleElement(CommunitiesUIConstants.CommunitiesFullpageWidgetContainer).isElementPresent(CommunitiesUIConstants.LibraryTitle))
			ui.fluentWaitPresentWithRefresh(CommunitiesUIConstants.LibraryTitle);
		return new LibraryWidget(driver.getSingleElement(CommunitiesUIConstants.CommunitiesFullpageWidgetContainer));
	}
	
}
