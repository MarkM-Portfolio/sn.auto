package com.ibm.conn.auto.tests.profiles.regression;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Profile_Tags_Action;
import com.ibm.conn.auto.util.menu.Profile_View_Menu;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cloud.ProfilesUICloud;

public class ProfileTags extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(ProfileTags.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;		
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		
	}
	
	/**
	*<ul>
	*<li><B>Profile Create Tags</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 04: My Profile Page - Tags (1 of 4)</li>
	*<li><B>Info:</B> Create new tags and verify existing tags appear while typing</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Enter new tag and Click on enter key</li>
	*<li><B>Verify:</B> The new tag is added to the widget</li>
	*<li><B>Step:</B> Enter new tag and Click the plus sign</li>
	*<li><B>Verify:</B> The new tag is added to the widget</li>
	*<li><B>Step:</B> Enter partial text of already existing tag and then wait for the "matching tag" list box to display.</li>
	*<li><B>Verify:</B> The matching tag can be selected and it is used in the input field</li> 
	*<li><B>Verify:</B> the "matching tag" list box disappears once it's a complete match</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/8136524B90E55F4885257E04006744D9">SC - IC Profiles Regression 04: My Profile Page - Tags</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc","ptc"})
	public void profileCreateVerifyTags() throws Exception {
		
		//Unique number
		String uniqueId = Helper.genDateBasedRandVal();
		String tagName = Data.getData().profileTag + uniqueId;
		int itr = 5; //Iterate 3 times to enter tagName appropriately
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		
		//Start Test
		ui.startTest();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);

		//Enter new tag and Click Enter
		log.info("INFO: Enter new tag and Click Enter");
		ui.addProfileTagUsingKeyboard(Data.getData().commonTag + uniqueId);
		
		//Verify tag is added to the widget
		log.info("INFO: Verify the new tag is added to the widget");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().commonTag + uniqueId), 
				"ERROR: " + Data.getData().commonTag + uniqueId + " tag is not added to Tags widget");
	
		//Enter new tag
		log.info("INFO: Enter new tag");
		ui.addProfileTag(tagName);
		
		//Verify tag is added to the widget
		log.info("INFO: Verify the new tag is added to the widget");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().profileTag + uniqueId), 
				"ERROR: " + Data.getData().profileTag + uniqueId + " tag is not added to Tags widget");
		
		//Enter partial text of existing tag
		log.info("Enter partial text of already existing tag");
		ui.typeTextWithDelay(ProfilesUIConstants.ProfilesTagTypeAhead, Data.getData().profileTag);
		
		//Verify matching tag list box is displayed
		log.info("INFO: Verify matching tag list box is displayed");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.TypeHeadTagDropDown).isVisible(),
				"ERROR: matching tag list box is not displayed");
		
		//Enter remaining partial text of existing tag
		log.info("Enter remaining partial text of already existing tag");
		ui.typeTextWithDelay(ProfilesUIConstants.ProfilesTagTypeAhead, uniqueId);
		
		//Verify matching tag list box is disappeared once it's a complete match
		log.info("INFO: Verify matching tag list box is disappeared once it's a complete match");
		Assert.assertFalse(driver.getSingleElement(ProfilesUIConstants.TypeHeadTagDropDown).isVisible(),
				"ERROR: matching tag list box is still appearing though it's a complete match");
		
		//Enter partial text of existing tag
		log.info("Enter partial text of already existing tag");
		while(itr>0) {
		ui.clearText(ProfilesUIConstants.ProfilesTagTypeAhead);
		ui.typeTextWithDelay(ProfilesUIConstants.ProfilesTagTypeAhead, tagName.substring(0, tagName.length()-1));
		if(driver.getFirstElement(ProfilesUIConstants.ProfilesTagTypeAhead).getAttribute("value").contentEquals(tagName.substring(0, tagName.length()-1)))
			break;
		itr--;
		}
		
		//Verify matching tag can be selected and it is used in input field.
		log.info("INFO: Verify matching tag can be selected and it is used in input field");
		ui.typeaheadSelection(Data.getData().profileTag + uniqueId, ProfilesUIConstants.TypeHeadTagName);
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.ProfilesTagTypeAhead).getAttribute("value").contains(Data.getData().profileTag + uniqueId),
				"ERROR: No matching tag is present in input field");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Profile_Tag_Action</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 04: My Profile Page - Tags (2 of 4)</li>
	*<li><B>Info:</B> Perform Profile tag actions and verify they are working as expected</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Open the Tags drop down menu</li>
	*<li><B>Verify:</B> Menu items include: Minimize, Refresh, Help</li>
	*<li><B>Step:</B> Select the Tags menu pick "Minimize"</li>
	*<li><B>Verify:</B> The tags widget details are hidden</li>
	*<li><B>Step:</B> Select the Tags menu pick "Maximize"</li>
	*<li><B>Verify:</B> The tags widget details are displayed</li>
	*<li><B>Step:</B> Select the Tags menu pick "Help"</li>
	*<li><B>Verify:</B> The Tagging Help topic is open in a new window</li>
	*<li><B>Step:</B> From another browser - User2, opens User1's profile and add a new tag to User1's tags widget.</li>
	*<li><B>Verify:</B> In User1's tag widget the New tag will not yet be displayed until the widget is refreshed</li> 
	*<li><B>Step:</B> User1 Selects the Tags menu pick "Refresh".</li>
	*<li><B>Verify:</B> The "newtag" is now displayed in the widget.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/8136524B90E55F4885257E04006744D9">SC - IC Profiles Regression 04: My Profile Page - Tags</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc","ptc"})
	public void profileTagAction() throws Exception {

		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		
		//Start Test
		ui.startTest();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Open the Tags drop down menu
		log.info("INFO: Open the Tags drop down menu");
		ui.fluentWaitPresent(ProfilesUIConstants.ProfilesTagTypeAhead);
		Profile_Tags_Action.MINIMIZE.open(ui);
		
		//Menu items include: Minimize, Refresh, Help
		log.info("INFO: Verify Minimize menu option is present in Tags Action menu");
		Assert.assertTrue(driver.getSingleElement(Profile_Tags_Action.MINIMIZE.getMenuItemLink()).isDisplayed(),
				"ERROR:- Minimize menu option is not present in Tags Action menu");
		
		log.info("INFO: Verify Refresh menu option is present in Tags Action menu");
		Assert.assertTrue(driver.getSingleElement(Profile_Tags_Action.REFRESH.getMenuItemLink()).isDisplayed(),
				"ERROR:- Refresh menu option is not present in Tags Action menu");
		
		log.info("INFO: Verify Help menu option is present in Tags Action menu");
		Assert.assertTrue(driver.getSingleElement(Profile_Tags_Action.HELP.getMenuItemLink()).isDisplayed(),
				"ERROR:- Help menu option is not present in Tags Action menu");
		
		//Select the Tags menu pick "Minimize"
		log.info("INFO: Select Tags menu and click on Minimize");
		Profile_Tags_Action.MINIMIZE.select(ui);
		
		//The Tags widget details are hidden
		log.info("INFO: Verify the Tags widget details are hidden");
		Assert.assertFalse(driver.getSingleElement(ProfilesUIConstants.ProfilesTagTypeAhead).isVisible(),
				"ERROR:- Tags widget details are not hidden");
		
		//Select the Tags menu pick "Maximize"
		log.info("INFO: Select Tags menu and click on Maximize");
		Profile_Tags_Action.MAXIMIZE.select(ui);
		
		//The tags widget details are displayed
		log.info("INFO: Verify the tags widget details are displayed");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.ProfilesTagTypeAhead).isVisible(),
				"ERROR:- Tags widget details are not displayed");
		
		//Select the Tags menu pick "Help"
		log.info("INFO: Select the Tags menu and click on Help");
		Profile_Tags_Action.HELP.select(ui);
		
		//The Tagging Help topic is open in a new window
		//Get original window handle
		log.info("INFO: Get original window handle");
		driver.getWindowHandle();
		
		//Switch to Help window
		log.info("INFO: Switch to Help Window");
		driver.switchToFirstMatchingWindowByPageTitle(ProfilesUICloud.HelpWindowTitle);
		
		//Verify new window is opened
		log.info("INFO: Verify new window is opened");
		Assert.assertTrue(driver.getTitle().contains(ProfilesUICloud.HelpWindowTitle),
				"ERROR:- New window is not opened for Help");
		
		//Switch to Tagging profile frame
		log.info("INFO: Switch to Tagging profile frame");
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.HelpTagFrame);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.HelpNavFrame);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.HelpViewsFrame);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.HelpTOCIframe);
		driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.HelpTOCViewframe);
		
		//Verify Tagging Help topic is open
		log.info("INFO: Verify Tagging Help topic is open");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.HelpTaggingProfilesOpen),
				"ERROR:- Tagging Help topic is not open");
		
		//TODO : Tag Action - Refresh, Need to find solution to open two different browsers at the same time
		
		//Close Help window
		driver.quit(); 
		
		//End test
		ui.endTest();
				
	}
	
	/**
	*<ul>
	*<li><B>Profile Tag Search</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 04: My Profile Page - Tags (3 of 4)</li>
	*<li><B>Info:</B> Verify default help text in input field and execute a Tag search</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Verify:</B> Help text "Add tag(s) to this profile" is displayed in the Tags input field</li>
	*<li><B>Step:</B> Click on Tags input field.</li>
	*<li><B>Verify:</B> Help text "Add tag(s) to this profile" is NOT displayed in the Tags input field</li>
	*<li><B>Step:</B> Click on existing tag in the widget, If tag is not present create new tag and click on it</li>
	*<li><B>Verify:</B> The "Search - Profiles" page is opened</li>
	*<li><B>Verify:</B> There is at least 1 matching result is shown</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/8136524B90E55F4885257E04006744D9">SC - IC Profiles Regression 04: My Profile Page - Tags</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc","ptc"})
	public void profileTagSearch() throws Exception {
		
		//Unique number
		String uniqueId = Helper.genDateBasedRandVal();
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		
		//Start Test
		ui.startTest();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Default Help text "Add tag(s) to this profile" is displayed in the Tags input field
		log.info("INFO: Default Help text 'Add tag(s) to this profile' is displayed");
		ui.fluentWaitPresent(ProfilesUIConstants.ProfilesTagTypeAhead);
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.ProfilesTagTypeAhead).getAttribute("value").contains(Data.getData().helpTextTagInputField),
				"ERROR: Default Help text 'Add tag(s) to this profile' is not displayed");
		
		//Select Tags input field
		log.info("INFO: Click on Tags input field");
		ui.clickLink(ProfilesUIConstants.ProfilesTagTypeAhead);
		
		//Help text "Add tag(s) to this profile" is NOT displayed in the Tags input field
		log.info("INFO: Help text 'Add tag(s) to this profile' is not displayed");
		Assert.assertFalse(ui.getFirstVisibleElement(ProfilesUIConstants.ProfilesTagTypeAhead).getAttribute("value").contains(Data.getData().helpTextTagInputField),
				"ERROR: Help text 'Add tag(s) to this profile' is still showing up");
		
		//Click on existing tag in the widget, If tag is not present create new tag and click on it
		log.info("INFO: Verify if existing tag is present");
		ui.fluentWaitPresent(ProfilesUIConstants.ProfilesTagTypeAhead);
		if(!driver.getElements(ProfilesUICloud.FirstProfileTag).isEmpty()) {
			
			//Click on existing tag
			log.info("INFO: Click on existing tag");
			ui.clickLink(ProfilesUICloud.FirstProfileTag);
			
			//Click on 100 items per page
			log.info("INFO: Click on 100 items per page");
			ui.clickLink(ProfilesUIConstants.Show100PerPage);
			
		}	
		else {
			
			//Create new tag and Click on it
			log.info("INFO: Create new tag and click on new tag");
			ui.profilesAddATag(testUser1, Data.getData().commonTag + uniqueId);
				
		}
			
		//Verify Search Profile page is opened
		log.info("INFO: Verify Search Profile page is opened");
		Assert.assertTrue(driver.getTitle().contains(Data.getData().searchProfileTitle),
				"ERROR:- Search Profile page is not displayed");
		
		//Verify at least one matching profile user is displayed in search results
		log.info("INFO: Verify " + testUser1.getDisplayName() +" is displayed in search results");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser1.getDisplayName()),
				"ERROR:- " + testUser1.getDisplayName() +" is not displayed in search results");
		
		//End test
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Profile Tags Mode</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 04: My Profile Page - Tags (4 of 4)</li>
	*<li><B>Info:</B> Verify List mode has two sections, frequency of times tag is used, tag creator, delete tag and Cloud mode display</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Click the List mode action button</li>
	*<li><B>Verify:</B> The tags are displayed in list mode</li>
	*<li><B>Verify:</B> The List mode has two sections: "My Tags for this profile" and "Tagged by "x" people"</li>
	*<li><B>Verify:</B> The List mode section "Tagged by "x" people" contains a number for frequency of times tag is used in the Profile</li>
	*<li><B>Step:</B> Click the # number next to a tag in the "Tagged by People" section</li>
	*<li><B>Verify:</B> The tag creator is displayed in popup</li>
	*<li><B>Step:</B> Click the # number next to a tag again</li>
	*<li><B>Verify:</B> The tag creator popup is closed.</li>
	*<li><B>Step:</B> Click the "X" delete tag control next to a tag in the "Tagged by People" section</li>
	*<li><B>Verify:</B> The tag is deleted from the listed tags.</li>
	*<li><B>Step:</B> Select - Click one of the existing tags in the  "Tagged by People" section</li>
	*<li><B>Verify:</B> The "Search - Profiles" page is opened.</li>
	*<li><B>Step:</B> Click the Cloud mode action button</li>
	*<li><B>Verify:</B> The tags are displayed in Cloud mode.</li>
	*<li><B>Verify:</B> That list of tags indicating greater frequency with larger bolder font</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/8136524B90E55F4885257E04006744D9">SC - IC Profiles Regression 04: My Profile Page - Tags</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc", "ptc"})
	public void profileTagsMode() throws Exception {
		
		//Unique number
		String uniqueId = Helper.genDateBasedRandVal();
		String tagName = Data.getData().commonTag.toLowerCase() + uniqueId;
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		User testUser2 = cfg.getUserAllocator().getUser();
		
		//Start Test
		ui.startTest();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Enter new tag
		log.info("INFO: Enter new tag");
		ui.addProfileTag(tagName);
		
		//Click on List mode
		log.info("INFO: Click on List mode");
		driver.turnOffImplicitWaits();
		if(ui.isElementPresent(BaseUIConstants.ListLink))
			ui.clickLink(BaseUIConstants.ListLink);
		driver.turnOnImplicitWaits();
		
		//Tags are displayed in List mode
		log.info("INFO: Verify tags are displayed in List mode");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(ui.isElementPresent(BaseUIConstants.ListLink),
				"ERROR: Tags are not displayed in List mode");
		driver.turnOnImplicitWaits();
		
		//List mode has two sections "My Tags for this profile" and "Tagged by "x" people"
		log.info("INFO: Verify List mode has 'My Tags for this profile' section");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().tagsYouAddedView),
				"ERROR:- 'My Tags for this profile' section is not displayed in List mode");
		
		log.info("INFO: Verify List mode has 'Tagged by x people' section");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().tagsListView),
				"ERROR:- 'Tagged by x people' section is not displayed in List mode");
		
		//Get frequency of times tag is used in the Profile
		log.info("INFO: Verify the frequency of times tag is used in the Profile");
		Assert.assertTrue(ui.getTagCountReference(tagName).getText().contentEquals("1"),
				"ERROR:- New tag frequency count should be 1");
		
		//Click on #number next to a tag
		log.info("INFO: Click on #number next to a tag");
		ui.getTagCountReference(tagName).click();
		
		//Tag creator is displayed in popup
		log.info("INFO: Verify tag creator is displayed in popup");
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.TagCreatorPopup).isTextPresent(testUser1.getDisplayName()),
				"ERROR: Tag creator " + testUser1.getDisplayName() + " is not displayed in popup");
		
		//Click on #number next to a tag again
		log.info("INFO: Click on #number next to a tag");
		ui.getTagCountReference(tagName).click();
		
		//Tag creator popup is closed
		log.info("INFO: Verify tag creator popup is closed");
		Assert.assertTrue(driver.getVisibleElements(ProfilesUIConstants.TagCreatorPopup).isEmpty(),
				"ERROR: Tag creator popup still exists after clicking on tag count");
		
		//Click on existing tag
		log.info("INFO: Click on existing tag");
		ui.clickLink("link="+tagName.toLowerCase());
		
		//The "Search - Profiles" page is opened
		log.info("INFO: Verify Search Profile page is opened");
		ui.fluentWaitPresent(ProfilesUIConstants.Show100PerPage);
		Assert.assertTrue(driver.getTitle().contains(Data.getData().searchProfileTitle),
				"ERROR:- Search Profile page is not displayed");
		
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Click the "X" delete tag
		log.info("INFO: Click the 'X' delete tag");
		ui.fluentWaitPresent(ProfilesUIConstants.ProfilesTagTypeAhead);
		ui.getTagDeleteReference(tagName, ProfilesUIConstants.TagsListTags).click();
		
		//Tag is deleted from the listed tags
		log.info("INFO: Verify the tag is deleted from the listed tags");
		Assert.assertTrue(driver.isTextNotPresent(tagName),
				"ERROR: Tag is not deleted");
		
		
		//Click Cloud mode button
		log.info("INFO: Click on cloud mode button");
		driver.turnOffImplicitWaits();
		if(ui.isElementPresent(BaseUIConstants.CloudLink))
			ui.clickLink(BaseUIConstants.CloudLink);
		driver.turnOnImplicitWaits();
		
		//Tags are displayed in Cloud mode
		log.info("INFO: Verify tags are displayed in Cloud mode");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(driver.isElementPresent(BaseUIConstants.CloudLink),
				"ERROR: Tags are not displayed in Cloud mode");
		driver.turnOnImplicitWaits();
		
		//Enter new tag
		log.info("INFO: Enter new tag");
		ui.addProfileTag(tagName);
		
		//Enter second tag
		log.info("INFO: Enter second tag");
		ui.addProfileTag(uniqueId);
		
		//Logout from testUser1
		ui.logout();
		
		//Load the component and login as testUser2
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		ui.login(testUser2);
		
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Search for the testUser1
		log.info("INFO: Search for the " + testUser1.getDisplayName());
		ui.searchForUser(testUser1);
		
		//Click on testUser1
		log.info("INFO: Click on " + testUser1.getDisplayName());
		ui.clickLinkWait("link="+testUser1.getDisplayName());
		
		//Enter new tag
		log.info("INFO: Enter new tag");
		ui.addProfileTag(tagName);
		
		//Verify Tags widget is opened in Cloud mode, If not click on Cloud mode link
		log.info("INFO: Verify Tags widget is opened in Cloud mode, If not click on Cloud mode link");
		driver.turnOffImplicitWaits();
		if(ui.isElementPresent(BaseUIConstants.CloudLink))
			ui.clickLink(BaseUIConstants.CloudLink);
		driver.turnOnImplicitWaits();
		
		//Verify tag is displayed in cloud mode
		log.info("INFO: Verify the new tag is displayed in cloud mode");
		Assert.assertTrue(ui.fluentWaitTextPresent(tagName), 
				"ERROR: " + tagName + " tag is not displayed in cloud mode");
		
		//Verify tagName indicates greater frequency with larger bolder font
		log.info("INFO: Verify "+ tagName +" indicates greater frequency with larger bolder font");
		Assert.assertTrue(ui.isTagIndicatesGreaterFrequency(tagName),
				"ERROR: " + tagName +" does not indicate greater frequency with larger bolder font");
		
		//End test
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Another User's Profile Page - Tags</B></li>
	*<li><B>Test Scenario:</B> SC - IC Profiles Regression 22: Another User's Profile Page - Tags</li>
	*<li><B>Info:</B> Open another User's profile to Add new tag, verify it is displayed in Cloud and List mode, Mouse over a tag, Directory search based on tag and tag is removed</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Open User2 profile</li>
	*<li><B>Step:</B> Add a new tag to the Tags widget</li>
	*<li><B>Verify:</B>  The new tag is displayed in the Tags widget Cloud mode and in List mode under "My tags for this profile" and "Tagged by x people" section</li>
	*<li><B>Step:</B> Click the "X" Remove tag button, the tag is removed from the list.</li>
	*<li><B>Step:</B>  Mouse over a tag count number in the widget.</li>
	*<li><B>Verify:</B> The message "tag <> was tagged by X person. See who added this tag."</li>
	*<li><B>Step:</B> Click the Number button next to a tag</li>
	*<li><B>Verify:</B> The popup dialog with the User names of person who created the tag, is displayed.</li>
	*<li><B>Step:</B> Click one of the tags in the widget.</li>
	*<li><B>Verify:</B> A Directory Search is executed based on the tag value.</li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/4C008C43B0CF5E9D85257EB5007684D5/A5F163C4B255961585257E1F0049581E">SC - IC Profiles Regression 22: Another User's Profile Page - Tags</a></li>
	*</ul>
	*/
	@Test(groups={"ptcsc","ptc"})
	public void anotherUserProfileTags() throws Exception {
		
		//Unique number
		String uniqueId = Helper.genDateBasedRandVal();
		String tagName = Data.getData().commonTag.toLowerCase() + uniqueId;
		
		//Get User
		User testUser1 = cfg.getUserAllocator().getUser();
		User testUser2 = cfg.getUserAllocator().getUser();
		
		//Start Test
		ui.startTest();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		//Open People -> My Profile
		log.info("INFO: Open People, My Profile page");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		Profile_View_Menu.MY_PROFILE.select(ui);
		
		//Search for the testUser2
		log.info("INFO: Search for the " + testUser2.getDisplayName());
		ui.searchForUser(testUser2);
		
		//Click on testUser2
		log.info("INFO: Click on " + testUser2.getDisplayName());
		ui.clickLink("link="+testUser2.getDisplayName());
		
		//Enter new tag
		log.info("INFO: Enter new tag in Tags widget");
		ui.fluentWaitPresent(ProfilesUIConstants.ProfilesTagTypeAhead);
		ui.typeTextWithDelay(ProfilesUIConstants.ProfilesTagTypeAhead, tagName);
				
		//Click on plus sign
		log.info("INFO: Click on add tag button");
		ui.clickLink(ProfilesUIConstants.ProfilesAddTag);
				
		//Verify Tags widget is opened in Cloud mode, If not click on Cloud mode link
		log.info("INFO: Verify Tags widget is opened in Cloud mode, If not click on Cloud mode link");
		if(ui.isElementPresent(BaseUIConstants.CloudLink))
			ui.clickLink(BaseUIConstants.CloudLink);
		
		//Verify tag is added to the widget
		log.info("INFO: Verify the new tag is added to the widget");
		Assert.assertTrue(ui.fluentWaitTextPresent(tagName), 
				"ERROR: " + tagName + " tag is not added to Tags widget");
		
		//Click on List mode
		log.info("INFO: Click on List mode");
		ui.clickLink(BaseUIConstants.ListLink);
		
		//The new tag is displayed in the "My tags for this profile" section of the list.
		log.info("INFO: The new tag is displayed in the 'My tags for this profile' section of the list.");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.TagsYouAddedList).isElementPresent("link="+tagName),
				"ERROR: New tag is not displayed in the 'My tags for this profile' section");
		
		//The new tag is displayed in the "Tagged by x people" section of the list.
		log.info("INFO: The new tag is displayed in the 'Tagged by x people' section of the list.");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.TagsListTags).isElementPresent("link="+tagName),
				"ERROR: New tag is not displayed in the 'Tagged by x people' section");
		
		//Hover on #number next to a tag again
		log.info("INFO: Hover on #number next to a tag");
		ui.getTagCountReference(tagName).hover();
		
		//The message "tag <> was tagged by X person. See who added this tag."
		log.info("INFO: Verify the message 'tag <> was tagged by X person. See who added this tag.'");
		Assert.assertTrue(ui.getTagCountReference(tagName).getAttribute("title").contains(Data.getData().tagHovermsg.replaceAll("TAGNAME", '"' + tagName+ '"')),
				"ERROR: Tag hover message 'tag <> was tagged by X person. See who added this tag.' is not displayed");
		
		//Click on #number next to a tag
		log.info("INFO: Click on #number next to a tag");
		ui.getTagCountReference(tagName).click();
		
		//Tag creator is displayed in popup
		log.info("INFO: Verify tag creator is displayed in popup");
		Assert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.TagCreatorPopup).isTextPresent(testUser1.getDisplayName()),
				"ERROR: Tag creator " + testUser1.getDisplayName() + " is not displayed in popup");
		
		//Click on existing tag
		log.info("INFO: Click on existing tag");
		ui.clickLink("link="+tagName);
		
		//A Directory Search is executed based on the tag value.
		log.info("INFO: Verify Directory Search is executed based on the tag value");
		ui.fluentWaitPresent(ProfilesUIConstants.Show100PerPage);
		Assert.assertTrue(driver.getCurrentUrl().contains(Data.getData().directorySearchWithTagUrl
				.replaceAll("SERVER", cfg.getServerURL())
				.replaceAll("TAGNAME", tagName)),
				"ERROR:- Directory Search is not executed based on the tag value");
		
		//Click the "X" delete tag from "My tags for this profile"
		log.info("INFO: Click the 'X' delete tag");
		ui.clickLink("link="+testUser2.getDisplayName());
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		ui.getTagDeleteReference(tagName, ProfilesUIConstants.TagsYouAddedList).click();
		
		//Tag is deleted from the listed tags
		log.info("INFO: Verify the tag is deleted from the listed tags");
		Assert.assertFalse(driver.isTextPresent(tagName),
				"ERROR: Tag is not deleted");			
		
		//End test
		ui.endTest();
	}
}
