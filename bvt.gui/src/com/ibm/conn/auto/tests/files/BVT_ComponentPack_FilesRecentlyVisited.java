package com.ibm.conn.auto.tests.files;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;


public class BVT_ComponentPack_FilesRecentlyVisited extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_ComponentPack_FilesRecentlyVisited.class);
	private FilesUI ui;
	private CommunitiesUI comUI;
	private APIFileHandler apiFileOwner;
	private TestConfigCustom cfg;
	private User testUser;
	private String serverURL;
	

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		testUser = cfg.getUserAllocator().getUser();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp() {

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = FilesUI.getGui(cfg.getProductName(), driver);
		comUI = CommunitiesUI.getGui(cfg.getProductName(), driver);
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Verify files recently visited view</li>
	*<li><B>Step:</B> [API] Upload a private PDF file, public jpg file and create new public Folder</li>
	*<li><B>Step:</B> Load files and login</li>
	*<li><B>Verify:</B> Verify that 'Recently Visited' tab is visible and it is the default view</li>
	*<li><B>Verify:</B> Verify that filter input field, label 'Recent' and Recent History view is present and visible</li>
	*<li><B>Verify:</B> Verify the placeholder text Filter by typing the name of a person or recent file or folder'</li>
	*<li><B>Step:</B> Select NEW button</li>
	*<li><B>Verify:</B> Verify that NEW dropdown should contains only 'Folder...','Upload...' options</li>
	*</ul>
	*/

	@Test(groups = { "cplevel2", "mtlevel2" })
	public void recentlyVistedView() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		logger.strongStep("Upload a public jpg file, private PDF file and create new public Folder");
		List<FileEntry> uploadedItems = testDataPop(testUser);

		// Load the component
		logger.strongStep("Load files and login");
		log.info("INFO: Load files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		logger.strongStep("Verify that 'Recently Visited' tab is visible and it is the default view");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.recentView),"Error: 'Recently Visited' tab is not  present");
		Assert.assertTrue(driver.getSingleElement(FilesUIConstants.recentView).getAttribute("aria-label").contains("selected"),"Error: 'Recently Visited' view is not default view");

		logger.strongStep("Verify that filter input field, label 'Recent' and Recent History view is present and visible");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.filterField), "Error: 'Filter/Find field is not present");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.recentLabel), "Error: 'Recent' label is not present");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.recentHistoryView), "Error: Recent view is not present");

		logger.strongStep("Verify the placeholder text 'Filter by typing the name of a person or recent file or folder'");
		log.info("INFO: Verify the placeholder text 'Filter by typing the name of a person or recent file or folder'");
		Assert.assertTrue(driver.getSingleElement(FilesUIConstants.filterField).getAttribute("placeholder").equals("Filter by typing the name of a person or recent file or folder"));

		logger.strongStep("Select NEW button");
		ui.clickLinkWait(FilesUIConstants.GLOBAL_NEW_BUTTON);
		List<Element> newDropdownEle = driver.getVisibleElements(FilesUIConstants.newButtonDropdown);
		List<String> actualNewDropdownItems = new ArrayList<String>();
		List<String> expectedNewDropdownItems = new ArrayList<String>();

		expectedNewDropdownItems.add("Folder...");
		expectedNewDropdownItems.add("Upload...");

		for (Element item : newDropdownEle) {

			String itemText = item.getText();
			log.info("actual dropdown option is: " + itemText);
			actualNewDropdownItems.add(itemText);
		}

		logger.strongStep("Verify that NEW dropdown should contains 'Folder...','Upload...' options");
		log.info("INFO: Verify that NEW dropdown should contains 'Folder...','Upload...' options");
		Assert.assertTrue(actualNewDropdownItems.containsAll(expectedNewDropdownItems));

		// Delete the uploaded file
		logger.strongStep("Delete uploaded file via API");
		log.info("INFO: Delete uploaded file via API");
		for (int i = 0; i < uploadedItems.size(); i++) {
			apiFileOwner.deleteFile(uploadedItems.get(i));
		}

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Verify that user is able to search the files/folders with keyword</li>
	*<li><B>Step:</B> [API] Upload a public jpg file, private PDF file and create new public Folder</li>
	*<li><B>Step:</B> Load files and login</li>
	*<li><B>Step:</B> Click inside input field to find files/folder</li>
	*<li><B>Step:</B> Enter a keyword from title of any of uploaded file/folder</li>
	*<li><B>Verify:</B> Verify that result returned should include file/folder matches</li>
	*
	*</ul>
	*/

	@Test(groups = { "cplevel2", "mtlevel2" })
	public void verifyFileOrFolderSearch() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();
		
		logger.strongStep("Upload a public jpg file, private PDF file and create new public Folder");
		List<FileEntry> uploadedItems=testDataPop(testUser);
	
		logger.strongStep("Load files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Click inside find input field");
		driver.getSingleElement(FilesUIConstants.filterField).clear();
		ui.clickLinkWait(FilesUIConstants.filterField);
		
		logger.strongStep("Enter a keyword from title of any of uploaded file/folder");
		String tittle=uploadedItems.get(0).getTitle();
		String searchString =tittle.substring(0,4);
		driver.getSingleElement(FilesUIConstants.filterField).type(searchString);

		for (FileEntry uploadedItem : uploadedItems) {

			logger.strongStep("Verify that results returned should include files/folders matches");
			log.info("Info: File/Folder name is : " + uploadedItem.getTitle() + " present in results");
			Assert.assertTrue(isFileOrFolderPresent(uploadedItem));
		}

		// Delete the uploaded file
		logger.strongStep("Delete uploaded file via API");
		log.info("INFO: Delete uploaded file via API");
		for (int i = 0; i < uploadedItems.size(); i++) {
			apiFileOwner.deleteFile(uploadedItems.get(i));
		}

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Info:</B> Verify that user is able to search people from directory</li>
	 * <li><B>Step:</B> Load files and login</li>
	 * <li><B>Step:</B> Click inside input field to find person from directory</li>
	 * <li><B>Step:</B> Enter display name of person from directory</li>
	 * <li><B>Verify:</B> Verify the label 'Files Belonging to' is present </li>
	 * <li><B>Verify:</B> Verify that result returned should include person from directory</li>
	 * <li><B>Step:</B> Click inside input field to find people from directory</li>
	 * <li><B>Step:</B> Enter first name of person from directory</li>
	 * <li><B>Verify:</B> Verify that result returned for people from directory should include the searched keyword </li>
	 * </ul>
	 */

	@Test(groups = { "cplevel2", "mtlevel2" })
	public void verifyPeopleSearch() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();

		// Load the component
		logger.strongStep("Load files and login");
		log.info("INFO: Load files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);

		logger.strongStep("Click inside find input field");
		driver.getSingleElement(FilesUIConstants.filterField).clear();
		ui.clickLinkWait(FilesUIConstants.filterField);

		logger.strongStep("Enter first name of person from directory");
		driver.getSingleElement(FilesUIConstants.filterField).type(testUser.getDisplayName());

		logger.strongStep("Verify the label 'Files Belonging to' is present");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.filesBelongingToLabel),"Error: 'Files Belonging to' label is not present");
		
		logger.strongStep("Verify that result returned should include person from directory");
		Assert.assertTrue(isUserPresent(testUser));
		
		logger.strongStep("Click inside find input field");
		driver.getSingleElement(FilesUIConstants.filterField).clear();
		ui.clickLinkWait(FilesUIConstants.filterField);

		logger.strongStep("Enter first name of person from directory");
		driver.getSingleElement(FilesUIConstants.filterField).type(testUser.getFirstName());

		logger.strongStep("Verify the label 'Files Belonging to' is present");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.filesBelongingToLabel),"Error: 'Files Belonging to' label is not present");
		List<Element> peopleResults = driver.getVisibleElements(FilesUIConstants.recentHistoryPersonTitle);

		for (Element result : peopleResults) {

			logger.strongStep("Verify that people result returned should include the searched keyword in name");
			log.info("Info: Person info is  " + result.getText());
			Assert.assertTrue(result.getText().contains(testUser.getFirstName()));
		}

		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info:</B> Verify that each folder/file entry in search result should associated with folder icon/file image, folder/file name link, type of action and timestamp </li>
	*<li><B>Step:</B> [API] Upload a public jpg file, private PDF file and create new public Folder</li>
	*<li><B>Step:</B> Load files and login</li>
	*<li><B>Step:</B> Click inside input field to find files/folder</li>
	*<li><B>Step:</B> Enter a keyword from title of any of uploaded file/folder</li>
	*<li><B>Verify:</B> Verify that folder entry is associated with folder icon and file entry associated with file type/image</li>
	*<li><B>Verify:</B> Verify that folder/file entry associated with file/folder name link</li>
	*<li><B>Verify:</B> Verify that folder/file entry is associated with type of action</li>
	*<li><B>Verify:</B> Verify that each folder/file entry is associated with type of action and time stamp</li>
	*</ul>
	*/

	@Test(groups = { "cplevel2", "mtlevel2" })
	public void verifySearchResultEntries() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		ui.startTest();
		List<FileEntry> uploadedItems=testDataPop(testUser);
	
		// Load the component
		logger.strongStep("Load files and login");
		log.info("INFO: Load files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Click inside find input field");
		driver.getSingleElement(FilesUIConstants.filterField).clear();
		ui.clickLinkWait(FilesUIConstants.filterField);
		
		logger.strongStep("Enter a keyword from title of any of uploaded file/folder");
		String tittle=uploadedItems.get(0).getTitle();
		String searchString =tittle.substring(0,5);
		driver.getSingleElement(FilesUIConstants.filterField).type(searchString);

		List<Element> fileCards = driver.getVisibleElements(FilesUIConstants.recentHistoryFileCards);

		for (int i = 0; i < fileCards.size(); i++) {
			Element fileCard = fileCards.get(i);
			driver.turnOffImplicitWaits();
			if (fileCard.isElementPresent("css=div[class*='-iconFolder']")) {
				logger.strongStep("Verify that folder entry is associated with folder icon");
				log.info("INFO: Verify that folder entry "+fileCards.get(i).getText()+" is associated with folder icon");
				Assert.assertTrue(fileCard.getSingleElement(FilesUIConstants.folderIcon).isVisible());
			} else {
				logger.strongStep("Verify that file entry is associated with file type/image");
				log.info("INFO: Verify that file entry "+fileCards.get(i).getText()+" is associated with file type/image");
				Assert.assertTrue(fileCard.getSingleElement(FilesUIConstants.fileIcon).isVisible());
			}

			driver.turnOnImplicitWaits();
		}

		for (int i = 1; i <= fileCards.size(); i++) {

			logger.strongStep("Verify that folder/file entry associated with file/folder name link");
			log.info("INFO: Verify that folder/file entry "+driver.getSingleElement(FilesUI.recentHistoryFileTitle(i)).getText()+" associated with file/folder name link");
			Assert.assertTrue(driver.getFirstElement(FilesUI.recentHistoryFileTitle(i)).isVisible());
			Assert.assertTrue(driver.getFirstElement(FilesUI.recentHistoryFileTitle(i)).getTagName().equals("a"));
			
			logger.strongStep("Verify that folder/file entry is associated with type of action");
			log.info("INFO: Verify that folder/file entry "+driver.getSingleElement(FilesUI.recentHistoryFileTitle(i)).getText()+" is associated with type of action");
			Assert.assertTrue(driver.getFirstElement(FilesUI.typeOfAction(i)).isVisible());
			
			logger.strongStep("Verify that each folder/file entry is associated with type of action and time stamp");
			log.info("INFO: Verify that folder/file entry "+ driver.getSingleElement(FilesUI.recentHistoryFileTitle(i)).getText()+ " is associated with type of action and time stamp");
			Assert.assertTrue(driver.getFirstElement(FilesUI.typeOfAction(i)).getText().equals("Created 1 minute ago"));

		}

		// Delete the uploaded file
		logger.strongStep("Delete uploaded file via API");
		log.info("INFO: Delete uploaded file via API");
		for (int i = 0; i < uploadedItems.size(); i++) {
			apiFileOwner.deleteFile(uploadedItems.get(i));
		}

		ui.endTest();

	}
	
	/**
	 * <li><B>Info:</B> Verify that random keyword searched which is not present in results should returns 
	 * 					a messege 'Sorry, your entry doesn't match any recent files, folders, or people.' and 'Search All Content' button is visible</li>
	 * <li><B>Step:</B> Load files component and login</li>
	 * <li><B>Step:</B> Select 'Recently Visited' view from folder panel</li>
	 * <li><B>Step:</B> Click inside input field to find files/User</li>
	 * <li><B>Step:</B> Enter some random text in the search field</li>
	 * <li><B>Verify:</B> Verify Error message is present when no matches found</li>
	 * <li><B>Step:</B> Click on Search All Content button</li>
	 * <li><B>Verify:</B> Verify Advanced search page open up after clicking on search all content button</li>
	 * </ul>
	 */
	@Test(groups = { "cplevel2" })
	public void verifySearchNoResult() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		TestConfiguration testConfig = cfg.getTestConfig();

		ui.startTest();

		// Load the component
		logger.strongStep("Load files and login as" + testUser.getDisplayName());
		log.info("INFO: Load files and login as" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Select 'Recently Visited' view from folder panel");
		log.info("INFO: Select 'Recently Visited' view from folder panel");
		ui.clickLinkWait(FilesUIConstants.recentView);

		logger.strongStep("Click inside search input field");
		log.info("INFO: Click inside search input field");
		driver.getSingleElement(FilesUIConstants.filterField).clear();
		ui.clickLinkWait(FilesUIConstants.filterField);

		logger.strongStep("Enter Any random text in search input field");
		log.info("INFO: Enter Any random text in search input field");
		driver.getSingleElement(FilesUIConstants.filterField).typeWithDelay(Helper.genRandString(5));

		String errorMsg = "Sorry, your entry doesn't match any recent files, folders, or people.";
		
		logger.strongStep("Verify the message '" + errorMsg + "' is present on the page");
		log.info("INFO: Verify the message '" + errorMsg + "' is present on the page");
		Assert.assertTrue(ui.fluentWaitTextPresent(errorMsg), "Error:Text '" + errorMsg + "' is not present");
		
		logger.strongStep("Click on 'Search All Content' button");
		log.info("INFO: Click on 'Search All Content' button");
		ui.clickLinkWait(FilesUIConstants.searchAllContentBtn);
		
		if(testConfig.browserIs(BrowserType.FIREFOX)){
			logger.strongStep("Accept alert popup");
			log.info("INFO: Accepting alert popup");
			driver.switchToAlert().accept();
		}else{
			log.info("INFO: Accepting 'Not secure form'");
			ui.clickLinkWait(FilesUIConstants.notSecureForm_accept);
		}
		
		logger.strongStep("Verify that Advanced Search Page opens up");
		log.info("Info: Verify that Advanced Search Page opens up");
		Assert.assertTrue(ui.fluentWaitTextPresent("Advanced Search"), "Error: Advanced Search page is not open");

		ui.endTest();
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Verify that User search returns user's details correctly</li>
	 * <li><B>Step:</B> Load files component and login with a user</li>
	 * <li><B>Step:</B> Select 'Recently Visited' view from folder panel</li>
	 * <li><B>Step:</B> Click inside input field to search a user</li>
	 * <li><B>Step:</B> Enter a different user name in the search field</li>
	 * <li><B>Verify:</B> Verify that profile image, name link and email address are visible correctly</li>
	 * <li><B>Step:</B> Click on Person's name</li>
	 * <li><B>Verify:</B> Verify text User's files is visible</li>
	 * <li><B>Verify:</B> Verify User name is in top header</li>
	 * </ul>
	 */
	@Test(groups = { "cplevel2", "mtlevel2" })
	public void verifyUserDetails() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		User testUser1 = cfg.getUserAllocator().getUser();

		ui.startTest();
		
		// Load the component
		logger.strongStep("Load files component and login as" + testUser.getDisplayName());
		log.info("INFO: Load files component and login as" + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Select 'Recently Visited' view from folder panel");
		log.info("INFO: Select 'Recently Visited' view from folder panel");
		ui.clickLinkWait(FilesUIConstants.recentView);

		logger.strongStep("Click inside search input field");
		log.info("INFO: Click inside search input field");
		driver.getSingleElement(FilesUIConstants.filterField).clear();
		ui.clickLinkWait(FilesUIConstants.filterField);

		logger.strongStep("Enter name of person from directory");
		log.info("INFO: Enter name of person from directory");
		driver.getSingleElement(FilesUIConstants.filterField).type(testUser1.getDisplayName());
		
		logger.strongStep("Verify that profile image, name link and email address and are visible");
		log.info("INFO: Verifying that profile image, name link and email address and are visible");
		Assert.assertTrue(ui.getFirstVisibleElement(FilesUIConstants.userProfileImage).isDisplayed(), "Error: User profile image is not present");
		Assert.assertTrue(ui.getFirstVisibleElement(ui.getUserDetail(testUser1.getDisplayName())).isDisplayed(), "Error: User name is not present");
		Assert.assertTrue(ui.getFirstVisibleElement(ui.getUserDetail(testUser1.getEmail())).isDisplayed(), "Error: User Email address is not present");
		
		logger.strongStep("Click On User's name");
		log.info("INFO: Click On User's name");
		ui.getFirstVisibleElement(ui.getUserDetail(testUser1.getDisplayName())).click();
		
		logger.strongStep("Verify text '" + testUser1.getDisplayName() + "'s Files' is visible");
		log.info("INFO: Verify text '" + testUser1.getDisplayName() + "'s Files' is visible");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser1.getDisplayName()+"'s Files"), "Error: User's Files is not Present");
		
		logger.strongStep("Verify User's name is in top header area");
		log.info("INFO: Verify User's name is in top header area");
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getHeaderName(testUser1.getDisplayName())), "Error: User's name is not in top header area");
				
		ui.endTest();
	}
	
	/**
	 * <li><B>Info:</B> Verify that user is able to view personal file and folder from recently visited view</li>
	 * <li><B>Step:</B> [API] Upload private jpg file</li>
	 * <li><B>Step:</B> [API] Create private folder </li>
	 * <li><B>Step:</B> Load files component and login</li>
	 * <li><B>Step:</B> Click inside find input field</li>
	 * <li><B>Step:</B> Enter a title of uploaded file</li>
	 * <li><B>Step:</B> Select file name link from result</li>
	 * <li><B>Verify:</B> Verify that file opens in FIDO view</li>
	 * <li><B>Step:</B> Close the FIDO view</li>
	 * <li><B>Step:</B> Click inside find input field</li>
	 * <li><B>Step:</B> Enter a title of uploaded folder</li>
	 * <li><B>Step:</B> Select folder name link from result</li>
	 * <li><B>Verify:</B> Verify that it opens folder in Files</li>
	 * 
	 * </ul>
	 */
	
	@Test(groups = { "cplevel2", "mtlevel2" })
	public void viewingPersonalFilesAndFolders() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		apiFileOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	    ui.startTest();

		// Create the BaseFile instance of image file
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags("testImage" + "_" + Helper.genDateBasedRand())
				.rename(Helper.genStrongRand() + "_testImage")
				.build();
		
		// Create the BaseFile instance of folder
		BaseFile baseFolder = new BaseFile.Builder(Helper.genStrongRand() + "_testFolder")
						.build();
		
		logger.strongStep("Upoad private file(.jpg) via API");
		log.info("INFO: Upoad private file(.jpg) via API");
		FileEntry privateFile = FileEvents.addFile(baseFileImage, testUser, apiFileOwner);
		baseFileImage.setName(baseFileImage.getRename() + baseFileImage.getExtension());
		
		logger.strongStep("Create private folder via API");
		log.info("INFO: Create public folder via API");
		FileEntry privateFolder = apiFileOwner.createFolder(baseFolder, Role.READER);
		
		// Load the component
		logger.strongStep("Load files and login");
		log.info("INFO: Load files and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.login(testUser);
		
		logger.strongStep("Click inside find input field");
		log.info("INFO: Click inside find input field");
		driver.getSingleElement(FilesUIConstants.filterField).clear();
		ui.clickLinkWait(FilesUIConstants.filterField);
		
		logger.strongStep("Enter a title of uploaded file");
		log.info("INFO: Enter a title of uploaded file");
		driver.getSingleElement(FilesUIConstants.filterField).type(baseFileImage.getName());
		
		logger.strongStep("Select file name link from result");
		log.info("INFO: Select file name link from result");
		ui.clickLinkWithJavascript(FilesUI.fileNameLinks(baseFileImage.getName()));
		
		logger.strongStep("Verify that file opens in FIDO view");
		log.info("INFO: Verify that file opens in FIDO view");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.fileviewer_previewImage));
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.fileviewer_previewLinkTitle).getText(), baseFileImage.getName());
		
		logger.strongStep("Close the FIDO view");
		log.info("INFO: Close the FIDO view");
		ui.clickLinkWait(FilesUIConstants.fileviewer_close);
		
		logger.strongStep("Click inside find input field");
		log.info("INFO: Click inside find input field");
		driver.getSingleElement(FilesUIConstants.filterField).clear();
		ui.clickLinkWait(FilesUIConstants.filterField);
		
		logger.strongStep("Enter a title of uploaded folder");
		log.info("INFO: Enter a title of uploaded folder");
		driver.getSingleElement(FilesUIConstants.filterField).type(baseFolder.getName());
		
		logger.strongStep("Select folder name link from result");
		log.info("INFO: Select folder name link from result");
		ui.clickLinkWithJavascript(FilesUI.fileNameLinks(baseFolder.getName()));
		
		logger.strongStep("Verify that it opens folder in Files");
		log.info("INFO: Verify that it opens folder in Files");
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getFolderActionMenu(baseFolder.getName())));
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.folderAboutLink));
			
		logger.strongStep("Delete the folder and file");
		log.info("INFO: Delete the folder and file");
		apiFileOwner.deleteFile(privateFile);
		apiFileOwner.deleteFile(privateFolder);
		
		ui.endTest();
	}
	
	/**
	 * <li><B>Info:</B> Verify that user is able to view community file and folder from recently visited view</li>
	 * <li><B>Step:</B> [API] Create community </li>
	 * <li><B>Step:</B> [API] Upload file in above community</li>
	 * <li><B>Step:</B> [API] Upload folder in above community</li>
	 * <li><B>Step:</B> Load community component and login</li>
	 * <li><B>Step:</B> Navigate to the community</li>
	 * <li><B>Step:</B> Select Files from left navigation menu</li>
	 * <li><B>Step:</B> Select/Open uploaded community file so that it will be available in recently visited view of files section</li>
	 * <li><B>Step:</B> Close the file</li>
	 * <li><B>Step:</B> Select/Open created community folder so that it will be available in recently visited view of files section</li>
	 * <li><B>Verify:</B> Load files</li>
	 * <li><B>Step:</B> Click inside find input field</li>
	 * <li><B>Step:</B> Enter a title of uploaded community file</li>
	 * <li><B>Step:</B> Select file name link from result</li>
	 * <li><B>Verify:</B> Verify that it should open in FIDO view</li>
	 * <li><B>Step:</B> Close the FIDO view</li>
	 * <li><B>Step:</B> Click inside find input field</li>
	 * <li><B>Step:</B> Enter a title of community folder</li>
	 * <li><B>Step:</B> Select folder name link from result</li>
	 * <li><B>Verify:</B> Verify that it should go to that community with folder selected</li>
	 * 
	 * </ul>
	 */
	
	@Test(groups = { "cplevel2", "mtlevel2" })
	public void viewingCommFilesAndFolders() throws Exception {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		apiFileOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()),
				testUser.getPassword());
		String testName = ui.startTest();
		APICommunitiesHandler communityOwner = new APICommunitiesHandler(serverURL, testUser.getEmail(),
				testUser.getPassword());

		// Create the BaseCommunity instance
		log.info("INFO: " + testUser.getDisplayName() + " creating a new community using the API");
		BaseCommunity baseCom = new BaseCommunity.Builder(testName + Helper.genStrongRand())
				.tags(Data.getData().commonTag + Helper.genStrongRand()).access(Access.PUBLIC)
				.description(Data.getData().commonDescription + Helper.genStrongRand()).build();

		// Create the BaseFile instance of image file
		log.info("INFO: " + testUser.getDisplayName() + " creating a public file");
		BaseFile baseCommFile = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.shareLevel(ShareLevel.EVERYONE).rename(testName + "_" + Helper.genDateBasedRand())
				.tags(testName + Helper.genStrongRand()).build();

		// Create the BaseFile instance of folder
		BaseFile baseCommFolder = new BaseFile.Builder(Helper.genStrongRand() + "_testFolder")
				.shareLevel(ShareLevel.EVERYONE).build();

		logger.strongStep("Create commnity via API");
		log.info("INFO: Create commnity via API");
		Community publicCommunity = baseCom.createAPI(communityOwner);

		Assert.assertNotNull(apiFileOwner);

		logger.strongStep("Create commnity file via API");
		log.info("INFO: Create commnity file via API");
		FileEntry commFile = FileEvents.addFile(baseCommFile, testUser, apiFileOwner);
		baseCommFile.setName(baseCommFile.getRename() + baseCommFile.getExtension());
		apiFileOwner.shareFileWithCommunity(commFile, publicCommunity, Role.OWNER);

		logger.strongStep("Create commnity folder via API");
		log.info("INFO: Create commnity folder via API");
		FileEntry commFolder = apiFileOwner.createCommunityFolder(publicCommunity, baseCommFolder);

		logger.strongStep("Get UUID of community");
		log.info("INFO: Get UUID of community");
		baseCom.getCommunityUUID_API(communityOwner, publicCommunity);

		logger.strongStep("Load Component and Log In as: " + testUser.getDisplayName());
		log.info("INFO: Load Component and Log In as: " + testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		baseCom.navViaUUID(comUI);

		logger.strongStep("Select Files from left navigation menu");
		log.info("INFO: Select Files from left navigation menu");
		Community_TabbedNav_Menu.FILES.select(comUI);

		logger.strongStep("Select/Open uploaded community file so that it will be available in recently visited view of files section");
		log.info("INFO: Select/Open uploaded community file so that it will be available in recently visited view of files section");
		Files_Display_Menu.DETAILS.select(ui);
		ui.clickLinkWait(FilesUI.selectFile(baseCommFile));

		logger.strongStep("Close the file");
		log.info("INFO: Close the file");
		ui.clickLinkWait(FilesUIConstants.fileviewer_close);

		logger.strongStep("Select/Open created community folder so that it will be available in recently visited view of files section");
		log.info("INFO: Select/Open created community folder so that it will be available in recently visited view of files section");
		ui.clickLinkWait(CommunitiesUI.communityFileOrFolderLink(baseCommFolder));
		ui.fluentWaitElementVisible(CommunitiesUI.getFolderHeading(baseCommFolder.getName()));

		logger.strongStep("Load files");
		log.info("INFO: Load files");
		ui.loadComponent(Data.getData().ComponentFiles, true);

		logger.strongStep("Click inside find input field");
		log.info("INFO: Click inside find input field");
		driver.getSingleElement(FilesUIConstants.filterField).clear();
		ui.clickLinkWait(FilesUIConstants.filterField);

		logger.strongStep("Enter a title of uploaded community file");
		log.info("INFO: Enter a title of uploaded community file");
		driver.getSingleElement(FilesUIConstants.filterField).typeWithDelay(baseCommFile.getName());

		logger.strongStep("Select community file link from result");
		log.info("INFO: Select community file link from result");
		ui.clickLinkWithJavascript(FilesUI.fileNameLinks(baseCommFile.getName()));

		logger.strongStep("Verify that it should open in FIDO view");
		log.info("INFO: Verify that it should open in FIDO view");
		Assert.assertTrue(ui.fluentWaitElementVisible(FilesUIConstants.fileviewer_previewImage));
		Assert.assertEquals(driver.getSingleElement(FilesUIConstants.fileviewer_previewLinkTitle).getText(),baseCommFile.getName());

		logger.strongStep("Close the FIDO view");
		log.info("INFO: Close the FIDO view");
		ui.clickLinkWait(FilesUIConstants.fileviewer_close);

		logger.strongStep("Click inside find input field");
		log.info("INFO: Click inside find input field");
		driver.getSingleElement(FilesUIConstants.filterField).clear();
		ui.clickLinkWait(FilesUIConstants.filterField);

		logger.strongStep("Enter a title of community folder");
		log.info("INFO: Enter a title of community folder");
		driver.getSingleElement(FilesUIConstants.filterField).typeWithDelay(baseCommFolder.getName());

		logger.strongStep("Select community folder link from sresult");
		log.info("INFO: Select community folder link from sresult");
		ui.clickLinkWait(FilesUI.fileNameLinks(baseCommFolder.getName()));

		logger.strongStep("Verify that it should go that community with folder selected");
		log.info("INFO: Verify that it should go that community with folder selected");
		ui.fluentWaitElementVisible(CommunitiesUIConstants.tabNavCommunityName);
		Assert.assertTrue(ui.fluentWaitPresent(CommunitiesUIConstants.tabNavCommunityName));
		Assert.assertTrue(ui.fluentWaitElementVisible(ui.getFolderActionMenu(baseCommFolder.getName())));

		log.info("INFO: Delete file, folder and community");
		apiFileOwner.deleteFile(commFile);
		apiFileOwner.deleteFile(commFolder);
		communityOwner.deleteCommunity(publicCommunity);

		ui.endTest();
	}

	/**
	 * <ul>
	 * <li><B>Data population to test recently visited view and searching files and folder</B></li>
	 * <li>[API] Upload a public jpg file</li>
	 * <li>[API] Upload private PDF file</li>
	 * <li>[API] Create new public Folder</li>
	 * </ul>
	 * @return list of resource created
	 */

	private List<FileEntry> testDataPop(User user) {

		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		List<FileEntry> uploadedfiles = new ArrayList<>();
		String randValue=Helper.genStrongRand();
		apiFileOwner = new APIFileHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
		
		// Create the BaseFile instance of image file
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file1).extension(".jpg")
				.tags("testImage" + "_" + Helper.genDateBasedRand())
				.rename(randValue +"_testImage")
				.shareLevel(ShareLevel.EVERYONE)
				.build();
		
		// Create the BaseFile instance of pdf file
		BaseFile baseFilePDF = new BaseFile.Builder(Data.getData().file19).extension(".pdf")
				.tags("testPDF" + "_" + Helper.genDateBasedRand())
				.rename(randValue+"_testPDF")
				.build();

		// Create the BaseFile instance of folder
		BaseFile baseFolder = new BaseFile.Builder(randValue + "_testFolder")
				.shareLevel(ShareLevel.EVERYONE)
				.build();

		logger.strongStep("Upoad public file(.jpg) via API");
		log.info("INFO: Upoad private file(.jpg) via API");
		FileEntry privateFile = FileEvents.addFile(baseFileImage, user, apiFileOwner);
		baseFileImage.setName(baseFileImage.getRename() + baseFileImage.getExtension());

		logger.strongStep("Upoad private file(.pdf) via API");
		log.info("INFO: Upoad private file(.pdf) via API");
		FileEntry pdfFile = FileEvents.addFile(baseFilePDF, user, apiFileOwner);

		logger.strongStep("Create public folder via API");
		log.info("INFO: Create public folder via API");
		FileEntry publicFolder = apiFileOwner.createFolder(baseFolder, Role.READER);

		uploadedfiles.add(pdfFile);
		uploadedfiles.add(privateFile);
		uploadedfiles.add(publicFolder);
		return uploadedfiles;

	}
	
	/**
	 * <ul>
	 * <li><B>Check if specific file entry is returned in search results</B>
	 * </li>
	 * </ul>
	 * 
	 * @return boolean value
	 */

	private boolean isFileOrFolderPresent(FileEntry fileEntry) {

		List<Element> fileResults = driver.getVisibleElements(FilesUIConstants.recentHistoryFileNames);

		Boolean found = false;
		for (Element result : fileResults) {

			if (result.getText().equals(fileEntry.getTitle())) {
				found = true;
				break;
			}
		}
		return found;
	}

	/**
	 * <ul>
	 * <li><B>Check if specific file entry is returned in search results</B>
	 * </li>
	 * </ul>
	 * 
	 * @return boolean value
	 */

	private boolean isUserPresent(User user) {

		List<Element> peopleResults = driver.getVisibleElements(FilesUIConstants.recentHistoryPersonTitle);

		Boolean found = false;
		for (Element person : peopleResults) {

			if (person.getText().equals(user.getDisplayName())) {
				found = true;
				break;
			}
		}
		return found;
	}

}
