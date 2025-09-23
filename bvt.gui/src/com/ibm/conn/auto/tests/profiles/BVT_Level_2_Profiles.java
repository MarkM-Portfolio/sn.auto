/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.profiles;

import java.io.UnsupportedEncodingException;
import java.util.List;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.openqa.selenium.By;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseContact;
import com.ibm.conn.auto.appobjects.base.BaseContact.contactNameOrder;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.MyContacts_LeftNav_Menu;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cloud.ProfilesUICloud;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.lconn.automation.framework.services.common.SearchAdminService;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class BVT_Level_2_Profiles extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Profiles.class);
	private ProfilesUI ui;
	private FilesUI fUI;
	private TestConfigCustom cfg;
	private User guestUser;
	private com.ibm.atmn.waffle.utils.Assert  cnxAssert;
	
	@BeforeClass(alwaysRun = true)
	public void beforeClass(ITestContext context) {
		super.beforeClass(context);
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		fUI = FilesUI.getGui(cfg.getProductName(), driver);
		cnxAssert = new com.ibm.atmn.waffle.utils.Assert(log);
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
		SearchAdminService adminService = new SearchAdminService();
		User adminUser = cfg.getUserAllocator().getAdminUser();
		try {
			adminService.indexNow("profiles", adminUser.getAttribute(cfg.getLoginPreference()), adminUser.getPassword());
		} catch (UnsupportedEncodingException e) {
			Assert.fail("IndexNow failed for profiles.", e);
		} catch (AssertionError e) {
			log.info("INFO: IndexNow failed to run. Ignoring.");
		}
	}
		

	/**
	*<ul>
	*<li><B>Info:</B> Editing and verifying a profile using Edit</li>
	*<li><B>Step:</B> Click on the My Profile tab</li>
	*<li><B>Step:</B> Click the "Edit My Profile" button</li>
	*<li><B>Step:</B> Update the user's profile</li>
	*<li><B>Step:</B> Log out and log back in</li>
	*<li><B>Step:</B> Go back to the My Profile tab</li>
	*<li><B>Step:</B> Click on the Edit My Profile button </li>
	*<li><B>Verify:</B> The user's profile details are present</li> 
	*<li><B>Step:</B> Perform a search for the user</li>
	*<li><B>Verify:</B> A search for the user returns the right info</li>
	**<li><B>This test case is failing on cpbvt server only and below defect has raised for the same</li>
	*<li><B>https://jira.cwp.pnp-hcl.com/browse/CNXSERV-12530</li>
	*</ul>
	*/
	@Test(groups = { "level2", "cnx8ui-level2","bvt", "smokeonprem", "bvtcloud", "smokecloud", "regressioncloud", "icStageSkip","cnx8ui-cplevel2" })
	public void profilesEditVerifyUsingEdit() throws Exception {
		String uniqueId = Helper.genDateBasedRandVal();
		User testUser = cfg.getUserAllocator().getUser();
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();

		//Load the component, login and toggle
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		//load the My Profile view
		logger.strongStep("Load 'My Profile' view");
		ui.myProfileView();

		//Click on the Edit Profile button
		logger.strongStep("Click on the 'Edit Profile' button");
		ui.editMyProfile();

		//Update the Users Profile
		logger.strongStep("Update the user's profile");
		log.info("INFO: Update the Users Profile");
		ui.updateProfile(uniqueId);
		ui.waitForPageLoaded(driver);
		// It's been observed that logging out too fast would result in json being rendered after logging back in.
		// The login url has an extra tabinst=Updates at the end when it happens so adding an artificial wait before 
		// logout to see if it helps
		ui.sleep(5000);

		//Log out the user
		logger.strongStep("Logout as the user");
		log.info("INFO: Log out the user");
		ui.logout();
		//ui.close(cfg); 	This line is commented to maintain single session in BS

		//Load the component and login
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentProfiles,true);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		//load the My Profile view
		logger.strongStep("Load 'My Profile' view");
		ui.myProfileView();
		
		//Click on the Edit Profile button
		logger.strongStep("Click on the 'Edit Profile' button");
		ui.editMyProfile();
		
		//Verify User
		logger.weakStep("Verify the user");
		ui.verifyUserProfile(uniqueId);
		
		//Search for the user
		logger.strongStep("Search for the user");
		ui.searchForUser(testUser);

		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: </B>Adding a tag then searching</li>
	*<li><B>Step: </B>Add a tag with a username included</li>
	*<li><B>Step: </B>Search for the user with the tag</li>
	*<li><B>Verify: </B>Verify tag is added successfully</li>
	*<li><B>Verify: </B>Verify search is performed and returned correctly</li>
	*</ul>
	*/
	@Test(groups = { "level2", "bvt", "regressioncloud", "icStageSkip","cnx8ui-cplevel2" , "cnx8ui-level2"})
	public void profilesAddTagThenSearch() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();
		ui.startTest();

		//Load the component and login
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		//load the My Profile view
		logger.strongStep("Load 'My Profile' view");
		ui.myProfileView();
		
		//add a tag
		logger.strongStep("Add a tag");
		ui.profilesAddATag(testUser, Helper.genDateBasedRand());

		//Search for the user
		logger.strongStep("Search for the user");
		ui.searchForUser(testUser);

		ui.endTest();
	}
	/**
	*<ul>
	*<li><B>Info: </B>Add and remove a link</li>
	*<li><B>Step: </B>Add a link in the my links sections</Li>
	*<li><B>Step: </B>Click on the new link and a new window should be opened</Li>
	*<Li><B>Step: </B>close the window</li>
	*<Li><B>Step: </B>Delete the link</li>
	*<li><B>Verify: </B>Verify that a new link can be added</li>
	*<li><B>Verify: </B>Verify when clicked it opens a new window</li>
	*<li><B>Verify: </B>Verify that the link can be deleted</li>
	*</ul>
	*/
	@Test(groups = { "cplevel2", "level2", "bvt", "regressioncloud", "icStageSkip","cnx8ui-cplevel2", "cnx8ui-level2" })
	public void profilesAddRemoveLink() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();
		ui.startTest();

		//Load the component and login
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		//load the My Profile view
		logger.strongStep("Load 'My Profile' view");
		ui.myProfileView();
		
		//Add link 
		logger.strongStep("Add a link");
		ui.profilesAddLink(Data.getData().commonLinkName, Data.getData().commonURL);

		//Verify that clicked link is opened in a new window
		logger.weakStep("Verify that clicked link opens in a new window");
		ui.verifyNewWindow(Data.getData().commonLinkName, "Google");
		
		//delete link
		logger.strongStep("Delete the link");
		ui.profilesDeleteLink();

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Viewing recent updates</li>
	 *<li><B>Step: </B>Go to My Profile page</li>
	 *<li><B>Step: </B>Go to Recent Updates tab</li>
	 *<li><B>Verify: </B>Status update status text area present</li>
	 *<li><B>Step: </B>Go to Contact Information</li>
	 *<li><B>Verify: </B>Name and Office Email displays</li>
	 *<li><B>Step: </B>Go to About me</li>
	 *<li><B>Verify: </B>Text on all pages</li>
	 
	 *</ul>
	 */
	@Test(groups = { "level2", "bvt", "regressioncloud", "bvtcloud","cnx8ui-cplevel2", "cnx8ui-level2" })
	public void recentUpdates() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();
		ui.startTest();
		
		//Load the component, login and toggle UI.
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName()+ " and toggle to New UI as " + cfg.getUseNewUI());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		//Verify title of page
		logger.weakStep("Verify the page title");
		ui.checkPageTitle();
		
		//go to My Profile tab
		logger.strongStep("Load 'My Profile' view");
		log.info("INFO: Load the My Profile view");
		ui.myProfileView();
		
		//go to Recent updates tab
		logger.strongStep("Switch to the 'Recent Updates' tab");
		log.info("INFO: Switching to the Recent updates tab");
		ui.gotoRecentUpdates();
		
		//Verify update status text area is present
		logger.weakStep("Validate the the 'Updates' text area exists");
		log.info("INFO: Validate that the Updates Text Area exists");
		ui.verifyUpdatesTextArea();
		
		//go to the contact information tab
		logger.strongStep("Switch to the 'Contacts' tab");
		log.info("INFO: Switching to the Contacts tab");
		ui.gotoContactInformation();
		
		//Verify fields show up in contact information
		logger.weakStep("Validate that certain fields exist on the Contacts tab");
		log.info("INFO: Validate that some fields exists on Contacts tab");
		ui.verifyContactInfomationText();
		
		//go to the about me tab
		logger.strongStep("Switch to the 'About Me' tab");
		log.info("INFO: Switching to the About Me tab");
		ui.gotoAboutMe();

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Posting a status update message</li>
	 *<li><B>Step: </B>Go to My Profile > Recent Updates tab</li>
	 *<li><B>Step: </B>Type an message and post it</li>
	 *<li><B>Verify: </B>An alert displays message was posted successfully</li>
	 *<li><B>Verify: </B>The status displays in the stream</li>
	 *</ul>
	 */
	@Test(groups = { "cplevel2", "level2", "bvt", "regressioncloud","cnx8ui-cplevel2", "cnx8ui-level2" })
	public void statusUpdateMessage() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();
		ui.startTest();
		
		//Load the component and login
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);		
		ui.loginAndToggleUI(testUser,cfg.getUseNewUI());
		
		//go to My Profile tab
		logger.strongStep("Load 'My Profile' view");
		log.info("INFO: Load the My Profile view");
		ui.myProfileView();
		
		//Type status update message and click post
		logger.strongStep("Type and post status update");
		log.info("INFO: Type and post status update message");
		ui.updateProfileStatus(Data.getData().ProfileStatusUpdate);
		
		//Verify update was posted
		logger.weakStep("Validate that the status update was posted");
		log.info("INFO: Verify the alert message displays message was posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage), 
				"Alert stating the message was successfully posted was not found");
		
		logger.weakStep("Validate that the status update displays in the stream");
		log.info("Verify status displays in the stream: " + Data.getData().ProfileStatusUpdate);
		Assert.assertTrue(driver.isTextPresent(Data.getData().ProfileStatusUpdate), 
				         "Status does not display in the stream: '" + Data.getData().ProfileStatusUpdate + "'");
	
		ui.endTest();
		
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Check that a user has correct profile settings</li>
	 *<li><B>Step:</B> Switch to My Profile view </li> 
	 *<li><B>Verify:</B> Left panel's tag section is present </li> 
	 *<li><B>Verify:</B> Right side Network section is present </li> 
	 *<li><B>Verify:</B> Right side My Links section is present </li> 
	 *</ul>
	 */
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud", "smokeonprem"} )
	public void checkProfileSetting() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();

		ui.startTest();

		//Load the component and login
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		//load the My Profile view
		logger.strongStep("Load 'My Profile' view");
		log.info("INFO: Switch to my Profile view");
		ui.myProfileView();

		// Check left panel Tag section
		logger.weakStep("Validate the left panel 'Tag' section is present");
		log.info("INFO: Check left panel Tag section");
		Assert.assertTrue(ui.fluentWaitPresent(ProfilesUICloud.SocialTags), 
				  "ERROR: Profile's Tags section is not present");
	
		// Check right side Network section
		logger.weakStep("Validate the right side 'Network' section is present");
		log.info("INFO: Check right side Network section");
		Assert.assertTrue(ui.fluentWaitPresent(ProfilesUICloud.FriendsLink), 
				  "ERROR: Profile's Network section title is not present");
	
		// Check right side My Links section
		logger.weakStep("Validate the right side 'My Links' section is present");
		log.info("INFO: Check right side My Links section");
		Assert.assertTrue(ui.fluentWaitPresent(ProfilesUICloud.MyLinks), 
				"ERROR: Profile's My Links section title is not present");
		ui.endTest();
	}
	
	
	/**********************************************************************************************************************************
	 * This is the beginning of the test cases from BVT_Cloud.All these test cases are deprecated as IBM Cloud is no longer supported *
	 **********************************************************************************************************************************/
	/**
	 *<ul>
	 *<li><B>Info:</B> Test that a social contact can be created </li>
	 *<li><B>Step:</B> Go to My Contacts </li>
	 *<li><B>Step:</B> Create a new contact </li>
	 *<li><B>Step:</B> Search for the message that the contact has been successfully created </li>
	 *<li><B>Verify:</B> The message is present and that the contact was created </li>
	 *<li><B>Verify:</B> The contact's name is correct </li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud"} )
	public void createSocialContact(){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		BaseContact contact = new BaseContact.Builder("Duck", "McQuackers" + Helper.genDateBasedRand())
											 .title("Dr.")
											 .jobTitle("Outer Space Duck Commander")
											 .org("IBM")
											 .primEmail("ducky@outerspace.ibm.com")
											 .primTele("1 999 555-0100")
											 .address("IBM, Littleton, Massachusetts")
											 .notes("This is the top duck of all time, plus he's in outer space!")
											 .information("Information for test " + testName)
											 .nameOrder(contactNameOrder.LAST_NAME_FIRST)
											 .build();
		
		
		//Loading and login 
		logger.strongStep("Load Social Contacts and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentSocialContacts);
		ui.login(testUser);
		/*
		 * title is the prefix (such as "Dr." or "Mr."
		 * The first parameter should be the first name
		 * The second parameter should be the last name
		 * UUID is included in last name so that this field is visible after contact creation (before going onto the edit page)
		 */
		//Call the method to create the contact
		logger.strongStep("Create contact through UI");
		log.info("INFO: Creating Contact through UI");
		contact.create(ui);
		
		//Search for the message that the contact has been successfully created
		logger.strongStep("Look for system confirmation message");
		log.info("INFO: Looking for system confirmation message");
		ui.fluentWaitPresent(ProfilesUIConstants.informationMessage);
		

		log.info("INFO: Message found, getting text");
		String systemMessage = driver.getSingleElement(ProfilesUIConstants.informationMessage).getText();
		log.info("INFO: System Message: "+systemMessage);
		
		logger.weakStep("Validate that the contact was created");
		log.info("INFO: Verification 1: Verifying that the contact was created");
		Assert.assertTrue(systemMessage.contains(ProfilesUIConstants.ContactCreationSuccessful),
							"ERROR: Message confirming that the contact was created was not found");
		
		logger.weakStep("Veify that teh contact's name is correct");
		log.info("INFO: Verification 2: Verifying that the contact's name is correct");
		Assert.assertTrue(systemMessage.contains(contact.getAppearName()), 
						"ERROR: The name does not match what it should appear as (name may not be present, name may be incorrectly displayed, or another issue)");
		
		logger.strongStep("Delete social contact");
		log.info("INFO: Removing contact for test clean up");
		ui.deleteSocialContact(contact);
		
		ui.endTest();

	} 
	/**
	 * <ul>
	 * <li><B>Info:</B> Test that a social contact can be edited </li>
	 * <li><B>Step:</B> Go to My Contacts </li>
	 * <li><B>Step:</B> Create a new contact </li>
	 * <li><B>Step:</B> Wait for confirmation text </li>
	 * <li><B>Step:</B> Find contact and select Edit</li>
	 * <li><B>Step:</B> Edit the contact</li>
	 * <li><B>Verify:</B> Contact has been updated</li>
	 * </ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud"} )
	public void editSocialContact() {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();
		BaseContact contact = new BaseContact.Builder("Gordon", "Freeman" + Helper.genDateBasedRand())
								.title("Dr.")
								.jobTitle("Scientist")
								.org("Black Mesa")
								.primEmail("gfreeman1@blackmesa.com")
								.primTele("+1 999 555-0100")
								.address("None Known")
								.notes("Carries a trusty crowbar at all times")
								.information("Information for test " + testName)
								.build();
		
		//Loading and login 
		logger.strongStep("Load Social Contacts and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentSocialContacts);
		ui.login(testUser);
		  
		//Create the contact
		logger.strongStep("Create contact through UI");
		log.info("INFO: Creating contact through UI");
		contact.create(ui);

		//Wait until we get a confirmation that the contact was created - this disappears on page reload
		logger.strongStep("Wait for text confirmation that contact was created");
		log.info("INFO: Waiting for confirmation text");
		ui.fluentWaitPresent(ProfilesUIConstants.informationMessage+":contains(Successfully created the following)");
			
		logger.strongStep("Find contact and select edit");
		log.info("INFO: Find contact and select edit");
		ui.openEditSocialContact(contact);
		
		//First set the Name Details
		contact.setTitle("");
		contact.setGiven("G-");
		contact.setSurname("Man" + Helper.genDateBasedRand());
		contact.setNameOrder(contactNameOrder.LAST_NAME_FIRST);
		//Then set everything else
		contact.setJobTitle("Overseer");
		contact.setOrg("Gov");
		contact.setPrimEmail("");
		contact.setPrimTele("");
		contact.setInformation("None Known");
		contact.setNotes("None Known");
		
		contact.edit(ui);
		
		//Look for updated contact
		logger.weakStep("Validate that contact has succesfully been updated");
		log.info("INFO: Validate contact has been updated");
		Assert.assertTrue(ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.contactSelector + contact.getAppearName()+")"),
				          "Failed to find updated contact");
		
		logger.strongStep("Delete contact");
		log.info("INFO: Removing contact for cleanup");
		ui.deleteSocialContact(contact);
		
	}
	
	/**
	 * <ul>
	 * <li><B>Info:</B> Test that a contact can be deleted </li>
	 * <li><B>Step:</B> Go to My Contacts </li>
	 * <li><B>Step:</B> Create a contact </li>
	 * <li><B>Step:</B> Delete the contact </li>
	 * <li><B>Verify:</B> Remove Contact message appears </li>
	 * <li><B>Verify:</B> The contact has been deleted by checking the existing contacts </li>
	 * <li><B>Verify:</B> The deleted contact's name doesn't appear on the page after refresh </li>
	 * </ul>
	 * @throws Exception 
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud"} )
	public void deleteSocialContact() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();

		String testName = ui.startTest();

		BaseContact contact = new BaseContact.Builder("Happy", "Gilmore" + Helper.genDateBasedRand())
								 .title("Mr.")
								 .jobTitle("Golfer")
								 .org("Hockey Golfing")
								 .primEmail("supershot@golfgreens.com")
								 .primTele("+1 999 555-0101")
								 .address("1 Nobogeys Street, USA")
								 .notes("Loves hockey, incredible golfer")
								 .information("Information for test " + testName)
								 .build();
		
		
		// Load the component and login as a user
		logger.strongStep("Load Social Contacts and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentSocialContacts);
		ui.login(testUser);
		
		//Create contact in the UI
		logger.strongStep("Create contact through UI");
		log.info("INFO: Creating contact through the UI");
		contact.create(ui);
		
		//call the method used to delete contacts
		//Logging is added in the deleteContact method
		logger.strongStep("Delete contact");
		ui.deleteSocialContact(contact);
		
		logger.strongStep("Wait for selection box to close");
		log.info("INFO: Waiting until the selection box has been closed.");
		ui.fluentWaitTextNotPresentWithoutRefresh("Are you sure you want to remove this contact");
		
		logger.strongStep("Collect visible elements");
		log.info("INFO: Collect visible elements");
		List <Element> elementList = driver.getVisibleElements(ProfilesUIConstants.contactSelector + contact.getAppearName() + ")" );
		
		logger.weakStep("Validate there are no contact boxes with the name contained in them");
		log.info("INFO: Validate that there are no contact boxes with the name contained in them");
		Assert.assertTrue(elementList.size() == 0, 
						  "ERROR: There was a contact found with the specified name");
		
		logger.weakStep("Validate that the contacts name doesn't appear on the page after a refresh");
		log.info("INFO: Validate that the contacts name doesn't appear on the page after a refresh");
		Assert.assertTrue(ui.fluentWaitTextNotPresent(contact.getAppearName()), 
						  "ERROR: Contact name was found in page");
		
		log.info("INFO: The contact was not found on the page, test successful.");
		ui.endTest();
	} 
	

	
	/**<ul>
	 *<li><B>Info:</B> Test that a changes to a user profile take</li>
	 *<li><B>Step:</B> Edit user profile</li>
	 *<li><B>Step:</B> Change Job Title, Telephone Number, Mobile Phone, Fax and Address</li>
	 *<li><B>Step:</B> logout</li>
	 *<li><B>Step:</B> log back in</li>
	 *<li><B>Verify:</B> Validate the changes to title happened to the business card</li>
	 *<li><B>Verify:</B> Validate the changes to phone number happened to the business card</li>
	 *</ul>
	 */
	@Deprecated
	@Test(groups = {"regressioncloud", "bvtcloud"})
	public void businessCardChanges() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();

		String uniqueId = Helper.genDateBasedRandVal();
		
		ui.startTest();

		//Load the component and login
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		//load the My Profile view
		logger.strongStep("Switch to my profile view");
		log.info("INFO: Switch to my Profile view");
		ui.myProfileView();

		//Click on the Edit Profile button
		logger.strongStep("Click on the 'Edit Profile' button");
		log.info("INFO: Select the edit my profile button");
		ui.editMyProfile();

		//Update the Users Profile
		logger.strongStep("Update the user's profile");
		log.info("INFO: Update the Users Profile");
		ui.updateProfile(uniqueId);

		logger.strongStep("Collect business card details");
		log.info("INFO: Collect Business card details");
		Element bCard = driver.getSingleElement("css=div[id='businessCardDetails']");
		
		logger.weakStep("Validate that the title has been updated");
		log.info("INFO: Validate title has been updated");
		Assert.assertTrue(bCard.getText().contains(Data.getData().profJobTitle + uniqueId),
						 "ERROR: Expected to find " + Data.getData().profJobTitle + uniqueId + "inside the business card details");
		
		logger.weakStep("Validate that the 'Telephone' has been updated");
		log.info("INFO: Validate 'Telephone' has been updated");
		Assert.assertTrue(bCard.getText().contains(Data.getData().profTelephone + uniqueId),
						 "ERROR: Expected to find " + Data.getData().profTelephone + uniqueId + "inside the business card details");
		
		ui.endTest();
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info: Test case to test that you can create a new contact</B></li>
	 *<li><B>Step: Load the contacts page.</B></li> 
	 *<li><B>Step: Open the contacts tab.</B></li> 
	 *<li><B>Step: Add a new contact</B></li>
	 *<li><B>Verify: Validate that the contact was added</B></li>
	 *<li><B>CleanUp: Delete all contacts.</B></li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void contactCreation() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();
		
		ui.startTest();

		BaseContact contact = new BaseContact.Builder("Randy", "Cont"+ Helper.genDateBasedRand())
		 									.middle("Fredric")
		 									.jobTitle("Accountant")
		 									.org("Acme")
		 									.title("Mr")
		 									.suffix("Jr")
		 									.primEmail("rcont@acme.com")
		 									.primTele("978-555-1212")
		 									.relation("Peer")
		 									.build();

		//GUI
		//Login and load the component
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		logger.strongStep("Open the People Megu Menu");
		log.info("INFO: Open the People Mega Menu");
		ui.clickLinkWait(ProfilesUIConstants.People);
		
		logger.strongStep("Select 'My Contacts' Menu Item");
		log.info("INFO: Select My Contacts Menu item");
		ui.clickLinkWait(ProfilesUIConstants.MyContactsMenuItem);
		
		//Add new contact
		logger.strongStep("Add a new contact");
		log.info("INFO: Add a new contact");
		contact.create(ui);
		
		//Assert that there is one contact that contains the name we are searching for
		logger.weakStep("Validate that there is one contact with the test search name");
		log.info("INFO: Verify that there is exactly one contact that has the same name");
		Assert.assertTrue(driver.getElements(ProfilesUIConstants.contactSelector+ contact.getAppearName() + ")").size() == 1,
						  "ERROR: Contact with a matching name was not found");
		
		//Delete only the contact we created to prevent parallelization issues.
		logger.strongStep("Delete the created contact");
		log.info("INFO: Delete the contact we created");
		ui.deleteSocialContact(contact);
		
		ui.endTest();
	}


	/**
	 *<ul>
	 *<li><B>Info: Test case to test that you can edit a contact</B></li>
	 *<li><B>Step: Load the dashboard.</B></li> 
	 *<li><B>Step: Verify the mega menu for the Profiles links.</B></li> 
	 *<li><B>Step: Add a contact</B></li>
	 *<li><B>Step: Edit the contact</B></li>
	 *<li><B>Verify: Validate that the contact was added</B></li>
	 *<li><B>Verify: Validate that the contact was edited</B></li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void editingContact() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();

		ui.startTest();
		
		BaseContact contact = new BaseContact.Builder("Randy", "Cont"+ Helper.genDateBasedRand())
											.middle("Fredric")
											.jobTitle("Accountant")
											.org("Acme")
											.title("Mr")
											.suffix("Jr")
											.primEmail("rcont@acme.com")
											.primTele("978-555-1212")
											.relation("Peer")
											.build();

		//GUI
		//Login
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);

		logger.strongStep("Open the People Megu Menu");
		log.info("INFO: Open the People Mega Menu");
		ui.clickLinkWait(ProfilesUIConstants.People);
		
		logger.strongStep("Select 'My Contacts' Menu Item");
		log.info("INFO: Select My Contacts Menu item");
		ui.clickLinkWait(ProfilesUIConstants.MyContactsMenuItem);
		
		//Add a contact
		logger.strongStep("Add a new contact");
		log.info("INFO: Add a contact");
		contact.create(ui);

		logger.strongStep("Reset Web page Element through refresh");
		log.info("INFO: Resetting web page element names through refresh");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.contactSelector + contact.getAppearName()+")");
		
		logger.strongStep("Select the contact");
		log.info("INFO: Select the contact");
		ui.clickLinkWait(ProfilesUIConstants.contactSelector + contact.getAppearName() + ") ");
		
		logger.strongStep("Find the contact that was just created and navigate to the edit page");
		log.info("INFO: Finding the contact we just generated, and navigating to the edit page");
		ui.clickLinkWait(ProfilesUIConstants.contactSelector + contact.getAppearName() + ") " + ProfilesUIConstants.iconSelectorEdit);
		
		//Edit contact details
		logger.strongStep("Edit the contact's details");
		contact.setGiven("Tony");
		contact.setSurname("Mozz"+Helper.genDateBasedRand());
		contact.setNameOrder(contactNameOrder.FIRST_NAME_FIRST);
		contact.setJobTitle("Pizza king");
		contact.setOrg("Pizzas/R/Us");

		//Commit changes via UI
		logger.strongStep("Edit contact information");
		log.info("INFO: Edit contact information");
		contact.edit(ui);
		
		//Validate contact was edited
		logger.weakStep("Verify that the contact was edited");
		log.info("INFO: Verify that contact was edited");
		Assert.assertTrue(driver.getElements(ProfilesUIConstants.contactSelector + contact.getAppearName() +")").size() == 1,
						  "ERROR: The contact was not edited.");

		ui.endTest();
	}


	/**
	 *<ul>
	 *<li><B>Info: Test case to test that you can create a contact via the Photo Import Contact</B></li>
	 *<li><B>Step: Add a contact with Photo Import Contact via a file.</B></li> 
	 *<li><B>Verify: Validate the contact was created</B></li>
	 *<li><B>CleanUp: Delete all contacts.</B></li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void photoImportContactCreation() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();

		BaseFile photo = new BaseFile.Builder(Data.getData().file1)
									.build();
		
		
		BaseContact contact = new BaseContact.Builder("Harry", "Potter")
											 .photo(photo)
											 .build();

		//GUI
		//Login
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		logger.strongStep("Open the People Megu Menu");
		log.info("INFO: Open the People Mega Menu");
		ui.clickLinkWait(ProfilesUIConstants.People);
	    
		logger.strongStep("Select 'My Contacts' Menu Item");
		log.info("INFO: Select My Contacts Menu item");
		ui.clickLinkWait(ProfilesUIConstants.MyContactsMenuItem);
				
		//Add a contact
		logger.strongStep("Add a new contact with a photo");
		log.info("INFO: Add a contact with a photo");
		contact.create(ui);
		
		//Checks whether the contact was actually saved or not
		logger.weakStep("Validate that the contact saved");
		log.info("INFO: Checking that the contact was saved");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.contactSelector + contact.getAppearName() + ")"),
						  "ERROR: Contact was not created");
				
		//Delete all contacts
		logger.strongStep("Delete the contact");
		log.info("INFO: Delete the contact that we created");
		ui.deleteSocialContact(contact);
		
		ui.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: Tests that you can upload a photo to a profile</B></li>
	 *<li><B>Step: Edit profile.</B></li> 
	 *<li><B>Step: Upload photo.</B></li> 
	 *<li><B>Verify: Validate the photo was uploaded</B></li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud"} )
	public void uploadingPhotoToProfile() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		User testUser = cfg.getUserAllocator().getUser();

		BaseFile file = new BaseFile.Builder(Data.getData().file1)
									.build();
		
		//GUI
		//Login
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName());
		log.info("INFO: Login");
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		logger.strongStep("Click on Profile Picture");
		ui.clickLinkWait("css=img[id='imgProfilePhoto']");
		
		logger.strongStep("Check if SameTime is enabled, if it is allow time for it to load");
		ui.waitForSameTime();

		logger.strongStep("Click on photo to upload");
		ui.clickLinkWait("css=input[id='photoUploadFileSelected']");
		
		fUI.setLocalFileDetector();
		driver.getSingleElement("css=input[id='photoUploadFileSelected']").typeFilePath(FilesUI.getFileUploadPath(file.getName(), cfg));
		
		log.info("INFO: File try to upload: " + file.getName());
		fUI.fileToUpload(file.getName(),"css=input[id='photoUploadFileSelector']");
		
		
		//Upload photo
		logger.weakStep("Verify that photo uploaded");
		log.info("INFO: Verifying that file was uploaded");
		ui.clickLinkWait(ProfilesUIConstants.saveProfileChangesReg);
		
		//Shouldn't be present because it is the default image when no profile icon is selected
		logger.weakStep("Verify that former profile picture is not present");
		log.info("INFO: Verifying that the default image is not present");
		
		logger.strongStep("Refresh page");
		driver.navigate().refresh();
		Assert.assertFalse(ui.isElementPresent(ProfilesUIConstants.defaultImageReg));

		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info:</B> Check Profile as a guest user </li>
	 *<li><B>Step:</B> Log in as a guest user </li>
	 *<li><B>Step:</B> Switch to My Profile view </li>
	 *<li><B>Verify:</B> The People text is present </li>
	 *<li><B>Verify:</B> The Edit My Profile button is present </li>
	 *</ul>
	 */
	@Deprecated
	@Test (groups = {"regressioncloud", "bvtcloud", "smokecloud"} )
	public void checkGuestProfile(){
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		guestUser = cfg.getUserAllocator().getGuestUser();
		
		//Loading and login 
		logger.strongStep("Load Profiles and login: " +guestUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentSocialContacts);
		ui.login(guestUser);
	
		//Call the method to create the contact
		logger.strongStep("Check profile as guest user");
		log.info("INFO: Check Profile as a guest user");
		
		log.info("Verify that 'People' link text is present");
		ui.fluentWaitPresent(ProfilesUICloud.People);
		Assert.assertTrue(driver.isTextPresent(ProfilesUICloud.PeopleText), 
				"ERROR: People link does not show");
		log.info("Got " + ProfilesUICloud.PeopleText);
		
		// move to My profile
		ui.navigatetoProfiles(guestUser);
		
		//end test
		ui.endTest();

	} 
	
	/**
	*<ul>
	*<li><B>Profile_Share_a_File</B></li>
	*<li><B>Info:</B> The Another User's Profile page's Share a File button function</li>
	*<li><B>Step:</B> Open another user's Profile page</li>
	*<li><B>Step:</B> Click the Share a File button.</li>
	*<li><B>Verify:</B> The "Select Files" dialog is displayed with the current User's files displayed.</li>
	*<li><B>Step:</B> Select a file and click OK</li>
	*<li><B>Verify:</B> The message " You have successfully shared a file "< file  name > with <user's name >"</li>
	*<li><B>Step :</B> Open the User's profile that we shared a file with and the Files tab - Shared with me</li>
	*<li><B>Verify:</B> The file that was just shared displays in the list.</li>
	*<li><B>Step:</B> Click the More Actions - Download VCard button.</li>
	*<li><B>Verify:</B> The "ExportvCard" dialog is presented.</li>
	*</ul>
	*/
	@Test(groups={"level2"})
	public void shareAFileWithAnotherUserProfile() throws Exception {
		
		User testUser1, testUser2; //test Users
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start Test
		ui.startTest();
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
		//File to upload
		BaseFile baseFileImage = new BaseFile.Builder(Data.getData().file1)
											 .extension(".jpg")
											 .shareLevel(ShareLevel.EVERYONE)
											 .rename(fUI.reName(Data.getData().file1))
											 .build();

		//Load component and login
		log.info("INFO: Load component and login");	
		logger.strongStep("Load component and login");
		ui.loadComponent(Data.getData().ComponentFiles);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//Verify that the UI is available
		log.info("INFO: Verify the UI is Available");
		logger.strongStep("Verify the UI is Available");
		ui.waitForPageLoaded(driver);
				
		//Upload file
		log.info("INFO: Upload file");	
		logger.strongStep("Upload file");
		baseFileImage.upload(fUI);
		
		// change the view list format  & logout
		ui.clickLinkWait(FilesUIConstants.DisplayList);
		ui.logout();

		//Load the component and login as below user
		logger.strongStep("Load the component and login as:"+testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//Open testUser2 profile
		log.info("INFO: Open" + testUser2.getDisplayName() + " Profile page" );
		logger.strongStep("Open" + testUser2.getDisplayName() + " Profile page" );
		ui.openAnotherUserProfile(testUser2);
		
		//Click on Share a File button
		log.info("INFO: Click on Share a File button");
		logger.strongStep("Click on Share a File button");
		clickOnShareLink();
		
		//Verify the "Select Files" dialog is displayed
		log.info("INFO: Select Files dialog is displayed");
		logger.strongStep("Select Files dialog is displayed");
		cnxAssert.assertTrue(ui.isDailogDisplayed("Select Files"), "Select Files dialog is isplayed");
		
		//Click Cancel button
		log.info("INFO: Click cancel button");
		logger.strongStep("Click cancel button");
		ui.clickCancelButton();
		
		//Share a File
		log.info("INFO: Start Sharing a File");
		logger.strongStep("Start Sharing a File");
		ui.shareAFile("Recent Files", baseFileImage.getRename() + baseFileImage.getExtension());
		
		//Successfully shared a file message is appropriate
		log.info("INFO: Successfully Share a File message is appropriate");
		logger.strongStep("Successfully Share a File message is appropriate");
		cnxAssert.assertTrue(ui.fluentWaitTextPresent(Data.getData().shareAFileMsg.replaceAll("USERID", testUser2.getDisplayName()).replaceAll("FILENAME", baseFileImage.getRename() + baseFileImage.getExtension())),"Successfully Share a File message is displayed");

		//Click on More Actions
		log.info("INFO: Click on More Actions");
		logger.strongStep("Click on More Actions");
		if(driver.isElementPresent(ProfilesUIConstants.MoreActionButton) && driver.getFirstElement(ProfilesUIConstants.MoreActionButton).isVisible())
			driver.getFirstElement(ProfilesUIConstants.MoreActionButton).doubleClick();
		
		//Click Download Vcard
		log.info("INFO: Click Download Vcard menu item");
		logger.strongStep("Click Download Vcard menu item");
		ui.clickLink(ProfilesUIConstants.DownloadVCardMenuItem);
		
		//Verify the "Export vCard" dialog is displayed
		log.info("INFO: Verify the Export vCard dialog is displayed");
		logger.strongStep("Verify the Export vCard dialog is displayed");
		cnxAssert.assertTrue(driver.getFirstElement(ProfilesUIConstants.ExportVcardDialog).isDisplayed(),
				"The Export vCard dialog is displayed");
		
		//End test
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Profile_Invite_To_My_Network</B></li>
	*<li><B>Info:</B> Invite user to my network by sending invitation and follow him</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Search User and click on User name in the popup</li>
	*<li><B>Step:</B> Click on Invite to my Network</li>
	*<li><B>Verify:</B> The "Invite to My Network" dialog is displayed</li>
	*<li><B>Verify:</B> The static text in the dialog and "Also Follow" check box is checked.</li>
	*<li><B>Step:</B> Add a message to the message field</li> 
	*<li><B>Step:</B> Add a Tag to the tag field and Click Send Invitation button</li>
	*<li><B>Verify:</B> The message " <user's name> has been invited to your network is displayed</li>
	*<li><B>Verify:</B> The static text message "Pending Invitation ..." is displayed next to the user's name.</li>
	*<li><B>Verify:</B> The "Stop following" button is now displayed, the "Invite to My Network"  & "Follow" buttons are now hidden</li>
	*<li><B>Step:</B> Click People - My Network - Following</li> 
	*<li><B>Verify:</B> The user who was just invited with Following checked appears in the view</li>
	*<li><B>Verify:</B> The message text for that users, shows "Network Invitation Sent"</li>
	*</ul>
	*/
	@Test(groups={"level2","cnx8ui-level2"})
	public void inviteToMyNetwork() throws Exception {
		
		//Start Test
		ui.startTest();
		User testUser1, testUser2; //Declare test users
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		logger.strongStep("Load component and login");	
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//Search for the testUser2
		log.info("INFO: Search for the " + testUser2.getDisplayName());
		logger.strongStep("Search for the " + testUser2.getDisplayName());
		ui.openAnotherUserProfile(testUser2);
		
		//Remove an existing network relationship if already exists
		log.info("INFO: Remove an existing network relationship if already exists");
		logger.strongStep("Remove an existing network relationship if already exists");
		ui.removeExistingNetworkRelation(testUser1, testUser2);
		
		//Click the Invite to My Network button
		log.info("INFO: Click on Invite to My Network button");
		logger.strongStep("Click on Invite to My Network button");
		ui.clickLinkWithJavascript(ProfilesUIConstants.Invite_OnPrem);
		
		//The "Invite to My Network" dialog is displayed
		log.info("INFO: Verify Invite to My Network dailog is displayed");
		logger.strongStep("Verify Invite to My Network dailog is displayed");
		ui.fluentWaitElementVisible(ProfilesUIConstants.InviteToMyNetworkDailog);
		cnxAssert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.InviteToMyNetworkDailog).isVisible(),"Invite to My Network dailog is displayed");
				
		//The static text in the dialog
		log.info("INFO: Verify the static text in the dialog");
		logger.strongStep("Verify the static text in the dialog");
		cnxAssert.assertTrue(ui.fluentWaitTextPresent("Invite " + testUser2.getDisplayName() +" to be your network contact"), "Static text in the dialog is displayed");
		
		//Also Follow checkbox is checked
		log.info("INFO: Verify Also Follow checkbox is checked");
		logger.strongStep("Verify Also Follow checkbox is checked");
		cnxAssert.assertTrue(driver.getFirstElement(ProfilesUIConstants.AlsoFollowCheckBox).isSelected(),"Also Follow checkbox is checked ");
		
		//Add message in text field
		log.info("INFO: Add message in the text field");
		logger.strongStep("Add message in the text field");
		ui.getFirstVisibleElement(ProfilesUIConstants.InvitationMsgField).clear();
		ui.typeText(ProfilesUIConstants.InvitationMsgField,Data.MY_NOTIFICATIONS_NETWORK_INVITE_FOR_ME.replaceAll("USER", testUser1.getDisplayName()));
		
		//Add a tag
		log.info("INFO: Add a tag to the tag field");
		logger.strongStep("Add a tag to the tag field");
		ui.typeTextWithDelay(ProfilesUIConstants.InvitationTagsField, Data.TAG_NAME);
		
		//click "send invitation"
		log.info("INFO: Click on Send invitation");
		logger.strongStep("Click on Send invitation");
		ui.clickLinkWithJavascript(ProfilesUIConstants.SendInvite);
		
		//The message " <user's name> has been invited to your network
		log.info("INFO: Verify the message " + testUser2.getDisplayName() + ProfilesUIConstants.InvitedMessage);
		logger.strongStep("Verify the message " + testUser2.getDisplayName() + ProfilesUIConstants.InvitedMessage);
		cnxAssert.assertTrue(ui.fluentWaitTextPresent(testUser2.getDisplayName() + ProfilesUIConstants.InvitedMessage),testUser2.getDisplayName() + ProfilesUIConstants.InvitedMessage + " is displayed");

		// The text message "Pending Invitation ..." is displayed
		log.info("INFO: Verify static text message " + Data.getData().networkInvitationStatus + " is displayed");
		logger.strongStep("Verify static text message " + Data.getData().networkInvitationStatus + " is displayed");
		cnxAssert.assertTrue(ui.fluentWaitTextPresent(Data.getData().networkInvitationStatus),"Text message " + Data.getData().networkInvitationStatus + " is displayed");
		
		verifyStopfollowingLink();

		// The "Invite to My Network" button is now hidden
		log.info("INFO: Verify 'Invite to My Network' button is hidden");
		logger.strongStep(" Verify 'Invite to My Network' button is hidden");
		driver.turnOffImplicitWaits(); 
		cnxAssert.assertFalse(ui.isElementPresent(ProfilesUIConstants.Invite_OnPrem),"'Invite to My Network' button is hidden");

		// The "Follow" button is now hidden
		log.info("INFO: Verify 'Follow' button is hidden");
		logger.strongStep("Verify 'Follow' button is hidden");
		cnxAssert.assertFalse(ui.isElementPresent(ProfilesUIConstants.FollowPerson),"'Follow' button is hidden");
		driver.turnOnImplicitWaits();

		// Click Profile -> My Network -> Following
		log.info("INFO: Click on Profile -> My Network -> Following");
		logger.strongStep("Click on Profile -> My Network -> Following");
		ui.gotoMyNetwork();
		MyContacts_LeftNav_Menu.FOLLOWING.open(ui);

		// The testUser2 appears in the view.
		log.info("INFO: Verify " + testUser2.getDisplayName() + " appears in Following view");
		logger.strongStep("Verify " + testUser2.getDisplayName() + " appears in Following view");
		cnxAssert.assertTrue(ui.fluentWaitTextPresent(testUser2.getDisplayName()),
				testUser2.getDisplayName() + "appear in Following view");

		ui.endTest();

	}
	
	/**
	*<ul>
	*<li><B>Profile_Accept_Invitation</B></li>
	*<li><B>Info:</B> Invited User Accepts Invitation and verifies new network contact is displayed</li>
	*<li><B>Step:</B> Login as the Invited User, Open their Profiles - My Network page</li>
	**<li><B>Verify:</B> the invitation is displayed in the view.</li>
	*<li><B>Step:</B> Click the Accept button.</li>
	*<li><B>Verify:</B> The Invite is removed from the view.</li>
	*<li><B>Step:</B> Open the My network - My Network Contacts view</li>
	*<li><B>Verify:</B> The Invited user in now in the view</li>
	*</ul>
	*/
	@Test(groups={"level2", "cnx8ui-level2"})
	public void acceptNetworkInvite() throws Exception {
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start Test
		ui.startTest();
		User testUser1, testUser2; //Declare test users
		
		//Get User
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();

		//Load the component and login as below user
		logger.strongStep("Load component and login as: "+testUser1.getDisplayName());	
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.loginAndToggleUI(testUser1,cfg.getUseNewUI());
		
		//Search for the testUser2
		log.info("INFO: Search for the " + testUser2.getDisplayName());
		logger.strongStep("Search for the "+testUser1.getDisplayName());
		ui.openAnotherUserProfile(testUser2);
				
		//Remove an existing network relationship if already exists
		log.info("INFO: Remove an existing network relationship if already exists");
		logger.strongStep("Remove an existing network relationship if already exists");
		ui.removeExistingNetworkRelation(testUser1, testUser2);
				
		//Invite User to My Network
		logger.strongStep("Invite User to My Network");
		ui.inviteUserToMyNetwork(testUser2);
		
		//logout User
		logger.strongStep("logout User");
		ui.logout();
		
		//Load the component and login as below user
		logger.strongStep("Load component and login as: "+testUser2.getDisplayName());	
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		ui.loginAndToggleUI(testUser2,cfg.getUseNewUI());
		
		//Open My Profile
		log.info("INFO: Open My Profile page");
		logger.strongStep("Open My Profile");
		ui.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
		ui.gotoMyProfile();
		
		//Click the "Accept" button
		log.info("INFO: Click on Accept button");
		logger.strongStep("Click on Accept button");
		ui.acceptUserInvite(testUser1);
		
		//The testUser1 is removed from the view.
		log.info("INFO: Verify the "+testUser1.getDisplayName()+" is removed from the view.");
		logger.strongStep("Verify the "+testUser1.getDisplayName()+" is removed from the view.");
		driver.turnOffImplicitWaits(); 
		cnxAssert.assertFalse(driver.isElementPresent("link="+ testUser1.getDisplayName()),testUser1.getDisplayName() + " invite is removed from the view");
		driver.turnOnImplicitWaits();
		
		//Invitee opens their Profile - My Network page.
		log.info("INFO: Invitee opens their Profile - My Network page");
		logger.strongStep("Invitee opens their Profile - My Network page");
		MyContacts_LeftNav_Menu.MY_NETWORK.open(ui);
		
		//testUser1 is displayed in My Network page
		log.info("INFO: Verify " + testUser1.getDisplayName() +" is displayed in the My Network view");
		logger.strongStep("Verify " + testUser1.getDisplayName() +" is displayed in the My Network view");
		cnxAssert.assertTrue(ui.fluentWaitTextPresent(testUser1.getDisplayName()),testUser1.getDisplayName() +" is displayed in the My Network view");
		
		ui.endTest();
		
	}
	/**
	 * This method will verify Stop Following option in more action dropdown in CNX8
	 * or will verify Stop Following link in CNX7
	 */
	public void verifyStopfollowingLink()
	{
		CommonUICnx8 commonUI = new CommonUICnx8(driver);
		if (cfg.getUseNewUI()) {
			if (commonUI.isComponentPackInstalled())
			{
				log.info("INFO: Click on More action link");
				driver.getFirstElement(ProfilesUIConstants.MoreActionButton).doubleClick();

				log.info("INFO: Verify Stop following button is now displayed");
				cnxAssert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.stopFollowingLinkNew).isDisplayed(),
						"Stop following button is displayed");

			} 
			else
			{

				log.info("INFO: Verify Stop following button is now displayed");
				cnxAssert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.stopFollowingIcon).isDisplayed(),
						"Stop following button is displayed");
			}
		} 
		else {
			log.info("INFO: Verify Stop following button is now displayed");
			cnxAssert.assertTrue(ui.getFirstVisibleElement(ProfilesUIConstants.Unfollow).isDisplayed(),
					"Stop following button is displayed");
		}
	}

	/**
	 * This method will click on more action and share file option from dropdown in CNX8
	 * or will click on Share a file link in CNX7
	 */
	public void clickOnShareLink()
	{
		if(cfg.getUseNewUI())
		{
			log.info("INFO: Click on More action link");
			ui.clickLinkWaitWd(By.xpath(ProfilesUIConstants.MoreActionButton),5,"click on more action link");	

			log.info("INFO: Click on Share option in drodown");
			ui.clickLinkWaitWd(By.xpath(ProfilesUIConstants.ShareFileButtonCnx8),5,"click on more action link");	
		}
		else
		{
			log.info("INFO: Click on Share a File button");
			ui.clickLinkWithJavascript(ProfilesUIConstants.ShareaFileButton);
		}
	}

}
