package com.ibm.conn.auto.webui;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.GlobalSearchUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseContact;
import com.ibm.conn.auto.appobjects.base.BaseContact.contactFields;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseProfile;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.util.GatekeeperConfig;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.menu.Profile_View_Menu;
import com.ibm.conn.auto.webui.cloud.ProfilesUICloud;
import com.ibm.conn.auto.webui.cnx8.AppNavCnx8;
import com.ibm.conn.auto.webui.cnx8.CommonUICnx8;
import com.ibm.conn.auto.webui.cnx8.HCBaseUI;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import com.ibm.conn.auto.webui.multi.ProfilesUIMulti;
import com.ibm.conn.auto.webui.onprem.ProfilesUIOnPrem;
import com.ibm.conn.auto.webui.production.ProfilesUIProduction;

public abstract class ProfilesUI extends HCBaseUI {
	
	public ProfilesUI(RCLocationExecutor driver) {
		super(driver);
	}
	
	
	protected static Logger log = LoggerFactory.getLogger(ProfilesUI.class);


	public String insertLinkAboutMeBAckGround ;
	public String statusUpdateIframeAmoutMeBackground ;

	
	/**
	 * Method used to update the CSS value by appending parent tag for relevant Tiny Editor, if in case screen has 2 Tiny Editor
	 */
	public void updateLocators(String locator) {
		insertLinkAboutMeBAckGround = "xpath=//span[contains(text(),'PLACEHOLDER')]/../../..//following-sibling::tr[1]//span[@class='cke_button_icon cke_button__link_icon']".replace("PLACEHOLDER", locator);
		statusUpdateIframeAmoutMeBackground ="xpath=//span[contains(text(),'PLACEHOLDER')]/../../..//following-sibling::tr[1]//iframe".replace("PLACEHOLDER", locator);
	}
	
	public static String getFollowingUserCheckbox(String name) {
		return "css=input[type='checkbox'][aria-label='"+name+"']";
	}
	
	/**
	 * Get unfollow link for specified user
	 * @param User's display name
	 */
	public static String getUserUnfollowLink(String name) {
		return "#follow_removeContactsDiv a[aria-label='"+name+"']";
	}
	
	
	public static String getAcceptLink(User user){
		return "a[id^='accept_link_'][title='Accept "+ user.getDisplayName() +"']";
	}
	
	public String getContactCheckbox(BaseContact contact) {
		// remove comma suffix
		return contact.getFullname().replace(", " + contact.getSuffix(), " " + contact.getSuffix());

	}
	public static String getPageNumberAt(int pageNumber) {
		return "//ul[@class='lotusInlinelist cnx8-ui-pull-to-center']//li[text()='"+pageNumber+"']";
	}
	
	public void profilesAddLink(String AddLinkName, String AddLinkURL) throws Exception {
		fluentWaitPresent(ProfilesUIConstants.ProfilesAddLink);
		clickLink(ProfilesUIConstants.ProfilesAddLink);
		//Add the name and url
		fluentWaitPresent(ProfilesUIConstants.ProfilesAddLinkName);
		typeText(ProfilesUIConstants.ProfilesAddLinkName, AddLinkName);
		fluentWaitPresent(ProfilesUIConstants.ProfilesAddLinkLinkname);
		typeText(ProfilesUIConstants.ProfilesAddLinkLinkname, AddLinkURL);
		clickButton("Save");
		fluentWaitTextPresent("");

	}
	
	
	public void verifyNewWindow(String AddLinkName, String WindowName) throws Exception {
		
		//Get original window handle
		String originalWindow = driver.getWindowHandle();

		//open link in a new window
		clickLink("link=" + AddLinkName);
		
		//Switch to Help
		driver.switchToFirstMatchingWindowByPageTitle(WindowName);

		driver.getTitle().contains(WindowName);

		this.close(cfg);

		//Switch to original window
		driver.switchToWindowByHandle(originalWindow);
			
	}
	
	public void profilesDeleteLink() throws Exception {
		driver.executeScript("window.scrollBy(2000,0)");
		fluentWaitPresent(ProfilesUIConstants.DeleteLink);
		clickLinkWithJavascript(ProfilesUIConstants.DeleteLink);
		fluentWaitTextPresent("The link has been removed");
		
	}
	
	public void gotoMyProfile() {
		if(!cfg.getUseNewUI())
		{
			log.info("Go to My Profile");
			String myProfileSelector = getMyProfileSelector();
			fluentWaitPresent(myProfileSelector);
			clickLink(myProfileSelector);
			log.info("Clicked on My Profile");	
		}
		else
		{
			log.info("Go to My Profile");
			CommonUICnx8 commonUI  = new CommonUICnx8(driver);
			AppNavCnx8.PROFILE.select(commonUI);	
		}
		
	}
	
	public void deleteAllContacts() {
		
		log.info("INFO: Delete all social contacts");
		clickLinkWait(ProfilesUIConstants.link_SelectAll);
		
		log.info("INFO: Select bulk remove checkbox");
		clickLinkWait(ProfilesUIConstants.button_BulkRemove);
		
		log.info("INFO: Select the 'OK' button");
		clickLinkWait(ProfilesUIConstants.button_OK);
	}
	
	public void getContactTab(){
		fluentWaitPresent("css=a[href='/contacts/contacts/']");
		clickLink("css=a[href='/contacts/contacts/']");
	} 
	

	public void typeInDetailsBox(BaseContact contact){
		
		if (Helper.containsText(contact.getTitle())){
			log.info("INFO: Adding Prefix");
			typeText(ProfilesUIConstants.ContactDetailsTextboxBase+"[name='prefix']", contact.getTitle());
		}
		if (Helper.containsText(contact.getGiven())){
			log.info("INFO: Adding First Name");
			typeText(ProfilesUIConstants.ContactDetailsTextboxBase+"[name='firstName']", contact.getGiven());
		}
		if (Helper.containsText(contact.getMiddle())){
			log.info("INFO: Adding Middle Name");
			typeText(ProfilesUIConstants.ContactDetailsTextboxBase+"[name='middleName']", contact.getMiddle());
		}
		if (Helper.containsText(contact.getSurname())){
			log.info("INFO: Adding Last Name");
			typeText(ProfilesUIConstants.ContactDetailsTextboxBase+"[name='lastName']", contact.getSurname());
		}
		if (Helper.containsText(contact.getSuffix())){
			log.info("INFO: Adding Suffix");
			typeText(ProfilesUIConstants.ContactDetailsTextboxBase+"[name='suffix']", contact.getSuffix());
		}
	}
	
	public void addContactPhoto(BaseContact contact){
		
		//Click button to open profile photo selection/removal box
		log.info("INFO: Clicking on the edit icon on the contact's photo");
		clickLinkWait(ProfilesUIConstants.editButton);
			
		//The file that we're going to use in the test. 

		log.info("INFO: Starting process to upload a file. File to be uploaded=" + contact.getPhoto().getName());
			
		log.info("INFO: Checking that the upload button is present");
		fluentWaitPresent(ProfilesUIConstants.photoUploadButtonReg);
		
		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		Element ele = driver.getFirstElement(ProfilesUIConstants.photoUploadButtonTypeInReg);
		driver.executeScript("arguments[0].setAttribute('class', '');", (WebElement) ele.getBackingObject());
			
		log.info("INFO: Entering file location to upload the file we want to upload");
		ele.typeFilePath(FilesUI.getFileUploadPath(contact.getPhoto().getName(), cfg));

		//Click "OK" and display the contact's image on the contact creation page
		log.info("INFO: Clicking the OK button to save the contact's photo");
		driver.getVisibleElements(ProfilesUIConstants.selectOKPhotoUploadReg).get(0).click();
		
	}
	
	/**
	 * This method enables the user to post a board message on another user's profile page
	 * @param boardMessage - The board message to be posted
	 */
	public void postBoardMessage_OtherUser(String boardMessage){
		
		if(driver.isElementPresent(ProfilesUIConstants.ProfilesTextArea)){
			log.info("INFO: Type the board message in the embedded sharebox for the profile page Activity Stream");
			driver.getFirstElement(ProfilesUIConstants.ProfilesTextArea).type(boardMessage);
		}
		else{
			List<Element> frames = driver.getVisibleElements(BaseUIConstants.StatusUpdate_iFrame);
			int frameCount = 0;
			for(Element frame : frames){
				frameCount++;
				log.info("INFO: Frame toString: " + frame.toString());
				log.info("INFO: Frame location: " + frame.getLocation());
				//The first CK Editor iframe will be for the embedded sharebox
				if(frameCount == 1){
					log.info("INFO: Switching to Frame: " + frameCount);
					driver.switchToFrame().selectFrameByElement(frame);
				}
			}
			
			driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).click();
			driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(boardMessage);
			
			log.info("INFO: Returning to top Frame to click 'Post' button");
			driver.switchToFrame().returnToTopFrame();
		}
		
		log.info("INFO: Post the board message");
		clickLinkWait(HomepageUIConstants.PostComment);
		
		log.info("INFO: Verify that the update posted correctly");
		fluentWaitTextPresent(Data.getData().postSuccessMessage);
		
	}
	
	/**
	 * Creates a contact through the UI from the main contacts page <br>
	 * <ul><li>This enters a contact's name through the pop up menu produced by clicking "Details" on the contact creation page</li></ul> 
	 */
	public void createContact(BaseContact contact){
		waitForSameTime();
		
		//Go from main page to creation page
		navigateMainToCreate();
	
		//open the details box to input information
		log.info("INFO: Opening the \"details\" entry menu");
		clickLink(ProfilesUIConstants.ContactDetailsLink);
		
		//Selects how the name of the contact is displayed 
		log.info("INFO: Select how the name of contact is displayed");
		selectNameOrder(contact);
		
		//Types the details of the contact in the box that hold that information
		log.info("INFO: Open details popup box");
		typeInDetailsBox(contact);
		
		//Click the "OK" button to put the changes in the webpage
		log.info("INFO: Select 'OK' button");
		clickLink(ProfilesUIConstants.OKButton);
		
		log.info("INFO: Add contact photo if you have one");
		if(!contact.getPhoto().getName().isEmpty()){
			addContactPhoto(contact);
		}

		//Type the rest of the contact's information onto the page (all information besides his name)
		log.info("INFO: Edit remaining contact information");
		typeContactMainPage(contact);
		
		//save contact
		log.info("INFO: Saving new contact");
		clickLinkWait(ProfilesUIConstants.SaveContactButton);
	}
	
	/**
	 * Creates a contact the same way as creatContact(BaseContact contact) when starting from
	 * the create new contact page
	 */
	public void createContactWithoutNav(BaseContact contact){
		
		waitForSameTime();
		
		//open the details box to input information
		log.info("INFO: Opening the \"details\" entry menu");
		clickLink(ProfilesUIConstants.ContactDetailsLink);
		
		//Selects how the name of the contact is displayed 
		log.info("INFO: Select how the name of contact is displayed");
		selectNameOrder(contact);
		
		//Types the details of the contact in the box that hold that information
		log.info("INFO: Open details popup box");
		typeInDetailsBox(contact);
		
		//Click the "OK" button to put the changes in the webpage
		log.info("INFO: Select 'OK' button");
		clickLink(ProfilesUIConstants.OKButton);
		
		log.info("INFO: Add contact photo if you have one");
		if(!contact.getPhoto().getName().isEmpty()){
			addContactPhoto(contact);
		}

		//Type the rest of the contact's information onto the page (all information besides his name)
		log.info("INFO: Edit remaining contact information");
		typeContactMainPage(contact);
		
		//save contact
		log.info("INFO: Saving new contact");
		clickLinkWait(ProfilesUIConstants.SaveContactButton);
		
	}
	
	public void editContact(BaseContact contact){

		log.info("Starting to edit input fields");
		//In the editor, we will need to open the "details" section to separate the different fields, otherwise switch isn't right for this
		Iterator<contactFields> iterator = contact.getEdits().iterator();
		while(iterator.hasNext()){
			switch (iterator.next()){
			case TITLE:
				log.info("INFO: Editing Contact: Title");
				clickLinkWait(ProfilesUIConstants.ContactDetailsLink);
				fluentWaitElementVisible(ProfilesUIConstants.ContactTitleField);
				log.info("INFO: Clearing Contact Title Field");
				clearText(ProfilesUIConstants.ContactTitleField);
				log.info("INFO: Typing '"+ contact.getTitle() + "' into Title Field");
				typeText(ProfilesUIConstants.ContactTitleField, contact.getTitle());
				clickLink(ProfilesUIConstants.OKButton);
				log.info("INFO: Finished editing Contact Title ");
				break;
			case NAME_ORDER:
				log.info("INFO: Editing Contact: NameOrder");
				clickLink(ProfilesUIConstants.ContactDetailsLink);
				selectNameOrder(contact);
				clickLink(ProfilesUIConstants.OKButton);
				log.info("INFO: Finished editing Contact NameOrder ");
				break;
			case GIVEN:
				log.info("INFO: Editing Contact: FirstName");
				clickLink(ProfilesUIConstants.ContactDetailsLink);
				fluentWaitElementVisible(ProfilesUIConstants.ContactFirstNameField);
				log.info("INFO: Clearing Contact FirstName Field");
				clearText(ProfilesUIConstants.ContactFirstNameField);
				log.info("INFO: Typing '"+ contact.getGiven() + "' into FirstName Field");
				typeText(ProfilesUIConstants.ContactFirstNameField, contact.getGiven());
				clickLink(ProfilesUIConstants.OKButton);
				log.info("INFO: Finished editing Contact FirstName ");
				break;
			case MIDDLE:
				log.info("INFO: Editing Contact: MiddleName");
				clickLink(ProfilesUIConstants.ContactDetailsLink);
				fluentWaitElementVisible(ProfilesUIConstants.ContactMiddleNameField);
				log.info("INFO: Clearing Contact MiddleName Field");
				clearText(ProfilesUIConstants.ContactMiddleNameField);
				log.info("INFO: Typing '"+ contact.getMiddle() + "' into MiddleName Field");
				typeText(ProfilesUIConstants.ContactMiddleNameField, contact.getMiddle());
				clickLink(ProfilesUIConstants.OKButton);
				log.info("INFO: Finished editing Contact MiddleName ");
				break;
			case SURNAME:
				log.info("INFO: Editing Contact: LastName");
				clickLink(ProfilesUIConstants.ContactDetailsLink);
				fluentWaitElementVisible(ProfilesUIConstants.ContactLastNameField);
				log.info("INFO: Clearing Contact LastName Field");
				clearText(ProfilesUIConstants.ContactLastNameField);
				log.info("INFO: Typing '"+ contact.getSurname() + "' into LastName Field");
				typeText(ProfilesUIConstants.ContactLastNameField, contact.getSurname());
				clickLink(ProfilesUIConstants.OKButton);
				log.info("INFO: Finished editing Contact LastName ");
				break;
			case SUFFIX:
				log.info("INFO: Editing Contact: SuffixName");
				clickLink(ProfilesUIConstants.ContactDetailsLink);
				fluentWaitElementVisible(ProfilesUIConstants.ContactSuffixNameField);
				log.info("INFO: Clearing Contact SuffixName Field");
				clearText(ProfilesUIConstants.ContactSuffixNameField);
				log.info("INFO: Typing '"+ contact.getSuffix() + "' into SuffixName Field");
				typeText(ProfilesUIConstants.ContactSuffixNameField, contact.getSuffix());
				clickLink(ProfilesUIConstants.OKButton);
				log.info("INFO: Finished editing Contact SuffixName ");
				break;
			case JOBTITLE:
				log.info("INFO: Editing Contact: JobTitle");
				fluentWaitPresent(ProfilesUIConstants.ContactJobTitleField);
				log.info("INFO: Clearing Contact Job Title Field");
				clearText(ProfilesUIConstants.ContactJobTitleField);
				log.info("INFO: Typing '" + contact.getJobTitle() + "' into JobTitle Field");
				driver.getSingleElement(ProfilesUIConstants.ContactJobTitleField).type(contact.getJobTitle());
				log.info("INFO: Finished editing Contact JobTitle");
				break;
			case ORG:
				log.info("INFO: Editing Contact: Organization");
				fluentWaitPresent(ProfilesUIConstants.ContactOrganizationField);
				log.info("INFO: Clearing Contact Organization field");
				clearText(ProfilesUIConstants.ContactOrganizationField);
				log.info("INFO: Typing '" + contact.getOrg() + "' into Organization Field");
				driver.getSingleElement(ProfilesUIConstants.ContactOrganizationField).type(contact.getOrg());
				log.info("INFO: Finished editing Contact Organization");
				break;
			case PRIMEMAIL:
				log.info("INFO: Editing Contact: Email");
				fluentWaitPresent(ProfilesUIConstants.ContactEmailField);
				log.info("INFO: Clearing Contact Email field");
				clearText(ProfilesUIConstants.ContactEmailField);
				log.info("INFO: Typing '" + contact.getPrimEmail() + "' into Email Field");
				driver.getFirstElement(ProfilesUIConstants.ContactEmailField).type(contact.getPrimEmail());
				log.info("INFO: Finished editing Contact Email");
				break;
			case PRIMETELE:
				log.info("INFO: Editing Contact: Telephone Number");
				fluentWaitPresent(ProfilesUIConstants.ContactTelephoneField);
				log.info("INFO: Clearing Contact Telehone Number Field");
				clearText(ProfilesUIConstants.ContactTelephoneField);
				log.info("INFO: Typing '" + contact.getPrimTele() + "' into Telephone Number Field");
				driver.getFirstElement(ProfilesUIConstants.ContactTelephoneField).type(contact.getPrimTele());
				log.info("INFO: Finished editing Contact Telephone Number");
				break;
			case ADDRESS:
				log.info("INFO: Editing Contact: Address");
				fluentWaitPresent(ProfilesUIConstants.ContactAddressField);
				log.info("INFO: Clearing Contact Address Field");
				clearText(ProfilesUIConstants.ContactAddressField);
				log.info("INFO: Typing '" + contact.getAddress() + "' into Address Field");
				driver.getFirstElement(ProfilesUIConstants.ContactAddressField).type(contact.getAddress());
				log.info("INFO: Finished editing Contact Address");
				break;
			case INFORMATION:
				log.info("INFO: Editing Contact: Information");
				fluentWaitPresent(ProfilesUIConstants.ContactCustomInfoField);
				log.info("INFO: Clearing Contact Information Field");
				clearText(ProfilesUIConstants.ContactCustomInfoField);
				log.info("INFO: Typing '" + contact.getInformation() + "' into Information Field");
				driver.getFirstElement(ProfilesUIConstants.ContactCustomInfoField).type(contact.getInformation());
				log.info("INFO: Finished editing Contact Information");
				break;
			case NOTES:
				log.info("INFO: Editing Contact: Notes");
				//find the top note
				String noteId = "";
				List<Element> contactNotes = driver.getElements(ProfilesUIConstants.ContactNotes);
				log.info("INFO: Number of Notes " + contactNotes.size());
				noteId = contactNotes.get(0).getAttribute("id");				
				clickLinkWait("css=a[id='" + noteId.replace("_tr", "_editBtn") + "']");		
				//Remove the current notes that are in place 
				log.info("INFO: Clearing Contact Notes Field");
				clearText(ProfilesUIConstants.ContactEditNotesField);
				//If the note is not empty or null, type it in
				log.info("INFO: Typing '" + contact.getNotes() + "' into Notes Field");
				driver.getSingleElement(ProfilesUIConstants.ContactEditNotesField).type(contact.getNotes());
				driver.getSingleElement(ProfilesUIConstants.ContactUpdateBtn).click();
				log.info("INFO: Finished editing Contact Notes");
				break;
			default:
				break;
			 }
		}
		log.info("INFO: Saving contact");
		driver.getSingleElement(ProfilesUIConstants.SaveContactButton).click();
	}
	
	public void typeContactMainPage(BaseContact contact){
		if (Helper.containsText(contact.getJobTitle())){
			log.info("INFO: Adding Title");
			fluentWaitPresent(ProfilesUIConstants.ContactJobTitleField);
			driver.getSingleElement(ProfilesUIConstants.ContactJobTitleField).type(contact.getJobTitle());
		}
		if (Helper.containsText(contact.getOrg())){
			log.info("INFO: Adding Organization");
			fluentWaitPresent(ProfilesUIConstants.ContactOrganizationField);
			driver.getSingleElement(ProfilesUIConstants.ContactOrganizationField).type(contact.getOrg());
		}
		if (Helper.containsText(contact.getPrimEmail())){
			log.info("INFO: Adding Primary Email");
			fluentWaitPresent(ProfilesUIConstants.ContactEmailField);
			driver.getFirstElement(ProfilesUIConstants.ContactEmailField).type(contact.getPrimEmail());
		}
		if (Helper.containsText(contact.getPrimTele())){
			log.info("INFO: Adding Primary telephone number");
			fluentWaitPresent(ProfilesUIConstants.ContactTelephoneField);
			driver.getSingleElement(ProfilesUIConstants.ContactTelephoneField).type(contact.getPrimTele());
		}
		if (Helper.containsText(contact.getAddress())){
			if(driver.isElementPresent(ProfilesUIConstants.ContactAddressField)){
				log.info("INFO: Adding address");
				fluentWaitPresent(ProfilesUIConstants.ContactAddressField);
				driver.getSingleElement(ProfilesUIConstants.ContactAddressField).type(contact.getAddress());
			} else {
				//TODO Perhaps add detail to the other fields in future
				log.info("INFO: Adding street address");
				fluentWaitPresent(ProfilesUIConstants.ContactAddressStreetField);
				driver.getSingleElement(ProfilesUIConstants.ContactAddressStreetField).type(contact.getAddress());
			}
			
		}
		 
		//THIS IS BAD
		if(Helper.containsText(contact.getNotes())){
			log.info("INFO: Adding Custom Information");
			fluentWaitPresent(ProfilesUIConstants.ContactCustomInfoField);
			driver.getSingleElement(ProfilesUIConstants.ContactCustomInfoField).type(contact.getInformation());
		}
	
		if(Helper.containsText(contact.getNotes())){
			log.info("INFO: Adding Notes about the contact");
			fluentWaitPresent(ProfilesUIConstants.ContactInitialNotesField);
			driver.getSingleElement(ProfilesUIConstants.ContactInitialNotesField).type(contact.getNotes());
		}
	}
	
	/**
	 * Sets the name ordering of a contact to what it is set to in the contact <br>
	 * There are two options currently supported:
	 * <ul>
	 * <li>First Name First</li>
	 * <li>Last Name First</li>
	 * </ul>
	 * You must have clicked "details" next to the name entry textbox for a contact for this method to work.
	 * @see ProfilesUICloud.openEdit()
	 * 
	 * @param contact - The BaseContact used to run the test
	 * @author Matt Maffa
	 */
	public void selectNameOrder(BaseContact contact){
		log.info("INFO: Setting name order to \"First name first\" or \"Last name first\"");
		boolean selectionMade=false;
		getFirstVisibleElement(ProfilesUIConstants.contactNameOrderDropdown).click(); //open up the selections
		Iterator<Element> iterator = driver.getVisibleElements(ProfilesUIConstants.contactNameOrderSelections).iterator();
		log.info("INFO: Iterating through possible selections");
		while (iterator.hasNext()&&!selectionMade){
			Element option = (Element) iterator.next();
			log.info("INFO: Name order option: " + option.getText());
			if (contact.getNameOrder() == BaseContact.contactNameOrder.FIRST_NAME_FIRST && option.getText().equals("First name first")){
				option.click();
				selectionMade=true;
				log.info("INFO: Selecting \"First name first\"");
			}
			else if (contact.getNameOrder() == BaseContact.contactNameOrder.LAST_NAME_FIRST && option.getText().equals("Last name first")){
				option.click();
				selectionMade=true;
				log.info("INFO: Selecting \"Last name first\"");
			}
		}
		Assert.assertTrue(selectionMade,"ERROR: Unable to make Name Order selection");
	}
	
	/**
	 * Use this method to navigate from the main page (of profiles) to the form where you create a contact
	 */
	public void navigateMainToCreate(){
		//Click on the add contact button, then create contact on the context menu that pops up
		log.info("INFO: Navigating to the Contact Creation page");
		clickLinkWait(ProfilesUIConstants.AddContactButton);
		
		log.info("INFO: Select Create contact from menu");
		clickLinkWait(ProfilesUIConstants.AddContactButton2);

	}
	
	public void deleteSocialContact(BaseContact contact){
		log.info("INFO: Starting process to delete contact");
		log.info("INFO: Getting page element for delete button");
		
		//contactSelector uses css selectors to select the box. We add the contact's full name, and close the css locator. Then we add the specific reference to the delete icon.
		//Contacts have modified their delete contact button and the following selector has been modified to work with both old and new cases.
		String customDeleteButton = ProfilesUIConstants.contactSelector + contact.getAppearName() + ")" + ProfilesUIConstants.iconSelectorDelete;
		customDeleteButton += ", " + ProfilesUIConstants.contactSelector.substring(4) + contact.getAppearName() + ")" + ProfilesUIConstants.iconSelectorDeleteNew;
		Element contactBox = driver.getSingleElement(customDeleteButton);
		log.info("INFO: Clicking the remove button");
		contactBox.click();

		//confirm delete
		log.info("INFO: Select the confirm delete button");
		clickLinkWait(ProfilesUIConstants.ConfirmDeleteContactButton);
	}

	public void openEditSocialContact(BaseContact contact){
		log.info("INFO: Starting process to edit contact");
		log.info("INFO: Getting page element for edit button");
		
		//contactSelector uses css selectors to select the box. We add the contact's full name, and close the css locator. Then we add the specific reference to the edit icon. 
		Element contactBox = driver.getSingleElement(ProfilesUIConstants.contactSelector + contact.getAppearName() + ")" + ProfilesUIConstants.iconSelectorEdit);
		log.info("INFO: Clicking the edit button");
		contactBox.click();
	}
	
	/**
	 * Search for a User
	 * @param testUser - user to search
	 * @return void
	 */	
	public void searchForUser(User testUser){
		searchForUser(testUser, false);
	}
	
	/**
	 * Search for a User 
	 * @param testUser - user to search
	 * @param useProfileByName - whether to search using the Profile By Name link
	 * @return void
	 */	
	public void searchForUser(User testUser, boolean useProfileByName){
		String gk_flag = "search-history-view-ui";
	
		//GateKeeper check for new search panel vs old search control
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		GatekeeperConfig gkc = GatekeeperConfig.getInstance(driver);
		boolean value = gkc.getSetting(gk_flag);
		log.info("INFO: Gatekeeper flag " + gk_flag + " is " + value );
		if(value){
			log.info("Open common search panel");
			if(!cfg.getUseNewUI()) {
				clickLinkWaitWd(createByFromSizzle(GlobalsearchUI.OpenSearchPanel), 5, "Open Search Panel");
				waitForElementVisibleWd(createByFromSizzle(GlobalsearchUI.TextAreaInPanel), 5);
				typeWithDelayWd(testUser.getDisplayName(), createByFromSizzle(GlobalsearchUI.TextAreaInPanel));
			}else{
				waitForElementVisibleWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5);
				clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.searchTextBox), 5, "GlobalSearchTextBox");
				typeWithDelayWd(testUser.getDisplayName(),By.cssSelector(GlobalSearchUIConstants.searchTextBox));
			}
			if (useProfileByName)  {
				clickLinkWaitWd(createByFromSizzle(ProfilesUIConstants.ProfileByName), 5, "ProfileByName");
			} else {
				if(!cfg.getUseNewUI())
				{
					clickLink(GlobalsearchUI.SearchButtonInPanel);
				}
				else {
					clickLinkWaitWd(By.cssSelector(GlobalSearchUIConstants.searchButton), 5, "globalSearchIcon");
					if(isComponentPackInstalled())
						clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.peopleFilterButton),5, "PeopleFilterBtn");
					else
						clickLinkWaitWd(By.xpath(GlobalSearchUIConstants.profileFilter_oldSearchPage),5, "PeopleFilterBtn");

				}
			}
		} else {
			waitForElementVisibleWd(createByFromSizzle(ProfilesUIConstants.ProfilesSearchForUser), 5);
			typeWithDelayWd(testUser.getDisplayName(), createByFromSizzle(ProfilesUIConstants.ProfilesSearchForUser));
			clickLinkWaitWd(createByFromSizzle(ProfilesUIConstants.ProfileSearch), 5, "GlobalSearchIcon");
		}	
		
		//Uncheck 'Rank my content higher' check box if search result is not visible
		if(!isElementVisible("link=" + testUser.getDisplayName())) {
			log.info("Click on 'Rank my content higher' check box");
			if (!cfg.getUseNewUI()) {
				clickLinkWaitWd(By.xpath("//input[@id='contentContainer_results_FiltersForm_contentBoost']"), 4, "Click 'Rank my content higher'");
			} else {
				// Added below lines to set hidden attribute of 'Rank my content higher' to false and then select it.
				WebDriver wd = (WebDriver) driver.getBackingObject();
				JavascriptExecutor js = (JavascriptExecutor) wd;
				js.executeScript("document.getElementsByClassName('MuiSvgIcon-root MuiSvgIcon-fontSizeSmall')[0].setAttribute('aria-hidden', 'false')");
				findElement(createByFromSizzle(ProfilesUIConstants.RankMyContentCheckBox)).click();
			}
			fluentWaitTextPresent(testUser.getDisplayName());
		}
		this.getFirstVisibleElement("link=" + testUser.getDisplayName());
	}
	
	/**
	 * Search for a User by user email
	 * @param testUser - user to search
	 * @return void
	 */	
	public void searchForUserByEmail(User testUser){
		String gk_flag = "search-history-view-ui";
	
		//GateKeeper check for new search panel vs old search control
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		GatekeeperConfig gkc = GatekeeperConfig.getInstance(driver);
		boolean value = gkc.getSetting(gk_flag);
		log.info("INFO: Gatekeeper flag " + gk_flag + " is " + value );
		if(value){
			log.info("Open common search panel");
			clickLinkWait(GlobalsearchUI.OpenSearchPanel);
			fluentWaitPresent(GlobalsearchUI.TextAreaInPanel);
			typeText(GlobalsearchUI.TextAreaInPanel, testUser.getEmail());
			clickLink(GlobalsearchUI.SearchButtonInPanel);
		
		}else{
			fluentWaitPresent(ProfilesUIConstants.ProfilesSearchForUser);
			typeText(ProfilesUIConstants.ProfilesSearchForUser, testUser.getEmail());
			clickLink(ProfilesUIConstants.ProfileSearch);
		}		
		//fluentWaitTextPresent(testUser.getDisplayName());
		this.getFirstVisibleElement("link=" + testUser.getDisplayName());

	}
	
	//**Attention** - Below piece of code is cloud only features
	public void navigatetoProfiles(User guestUser){
		String gk_flag = "NAVIGATION_REPLACE_COMMUNITIES_MENU_WITH_LINK";
		
		//Gatekeeper check for profiles appearing under people tab Vs account settings tab
		log.info("INFO: Check to see if the Gatekeeper " + gk_flag + " setting is enabled");
		String value = GatekeeperConfig.getFoundationValue(driver, gk_flag);
	    log.info("INFO:Gatekeeper flag " + gk_flag +  " is " + value);
	    if(value == "true"){
	       log.info("Click on account settings");
	       clickLink(ProfilesUICloud.accountsetting);
	       log.info("Click on 'My Profile' under account setting");
	       clickLink(ProfilesUICloud.guestuser);
	       //Verify Edit My Profile button
	       log.info("Validate that 'Edit Button' is present");
			fluentWaitPresent(ProfilesUICloud.EditMyProfile);			
			Assert.assertTrue( driver.isElementPresent(ProfilesUICloud.EditMyProfile),
					"ERROR: Profile Edit button does not show");
			log.info("Got My Profile Edit button.");
			  	
	    } else {
	    //Click on My profile under people menu			
		log.info("Click on 'My Profile' under people menu");
		driver.getSingleElement(ProfilesUICloud.People).click();
		fluentWaitPresent(ProfilesUICloud.guestuser);
		driver.getSingleElement(ProfilesUICloud.guestuser).click();
		//Verify Edit My Profile button
		log.info("Validate that 'Edit Button' is present");
		fluentWaitPresent(ProfilesUICloud.EditMyProfile);			
		Assert.assertTrue( driver.isElementPresent(ProfilesUICloud.EditMyProfile),
				"ERROR: Profile Edit button does not show");
		log.info("Got My Profile Edit button.");
		
	    }
	}
	
	public abstract void profilesAddATag(User testUser, String tag);
	
	public abstract String getInviteToMyNetwork();
	
	public abstract String getAcceptInviteToMyNetwork();

	public abstract void myProfileView();
	
	public abstract void editMyProfile();
	
	public abstract void updateProfile(String uniqueId);

	public abstract void updateICPhoneNumber();
	
	public abstract void verifyUserProfile(String uniqueId);
	
	public abstract void checkPageTitle();
	
	protected abstract String getMyProfileSelector();
	
	public abstract void gotoRecentUpdates();
	
	public abstract void verifyUpdatesTextArea();
	
	public abstract void gotoContactInformation();
	
	public abstract void verifyContactInfomationText();
	
	public abstract void gotoAboutMe();
	
	public abstract void updateProfileStatus(String status);
	
	public abstract Element getContactInFollowingView(String userName);
	
	public abstract String getUserInviteMessageInFollowingView();
	
	public abstract void openProfileBusinessVcard();
	
	public abstract void createAnActivity(String uniqueId);
	
	public abstract boolean isHelpBannerTextPresent();
	
	public abstract String getUserProfileUrl();
	
	public abstract String getMyNetworkUrl();
	
	public abstract String getNetworkUserUrl();
	
	public abstract void openBusinessCardOfUser(User testUser);
	
	public abstract void editProfile(BaseProfile profile);
	
	public abstract void verifyLinksInBusinessCard();
	
	public abstract String openViewLinkUrl();
	
	public abstract void recentUpdatesFilterBy();
	
	public abstract void helptopicDYKwidget();
	
	public abstract String userProfilePageUrl();
	
	public abstract void dykPersoncardpopup();
	
	public abstract void verifyProfileDataInBusinessCard(User testUser1, String uniqueId, String orgName);
	
	public abstract void gotoMyNetwork();
	
	public abstract void acceptUserInvite(User inviterUser);
	
	public abstract void ignoreUserInvite(User invitedUser);
	
	public static ProfilesUI getGui(String product, RCLocationExecutor driver){
		if(product.toLowerCase().equals("cloud")){
			return new  ProfilesUICloud(driver);
		} else if(product.toLowerCase().equals("onprem")) {
			return new  ProfilesUIOnPrem(driver);
		} else if(product.toLowerCase().equals("production")) {
			return new ProfilesUIProduction(driver);
		} else if(product.toLowerCase().equals("vmodel")) {
			return new  ProfilesUIOnPrem(driver);
		} else if(product.toLowerCase().equals("multi")) {
			return new  ProfilesUIMulti(driver);
		} else {
			throw new RuntimeException("Unknown product name: " + product);
		}
	}
	
	/**
	 * Get the profile tag count from Tagged by 'x' person
	 * @param tag - ProfileTag
	 * @return tagCount element
	 */
	public Element getTagCountReference(String tag){

		log.info("INFO: To get the tagCount of Profile tag");

		//collect all the visible tags as web elements
		List<Element> visibleTags = driver.getElements("css=div[id='tagsListTags_div'] > ul > li");
		log.info("INFO: visible tags are " + visibleTags.size());

		Element tagCountRef = null;
		try {
			//Search for the tag and break the loop
			for(Element visTags : visibleTags){
				String tagName = visTags.getSingleElement("css=a[class^='profileTag']").getText();
				log.info("INFO: Compare the tag Name");
				if(tagName.equalsIgnoreCase(tag)){
					log.info("INFO: Found tag - " + tagName);
					tagCountRef = visTags.getSingleElement("css=a[class^='lconnTagCount']");
					break;
				}
			}
		} catch (Exception e){
			throw new AssertionError("ERROR: Tag is not present in Tagged by 'x' person");
		}

		return tagCountRef;
	}
	
	/**
	 * Get the profile tag delete img from Tagged by 'x' person
	 * @param tag - ProfileTag, section - "My tags for this profile" or "Tagged by x people" locator
	 * @return tagDelImg element
	 */
	public Element getTagDeleteReference(String tag, String section){

		log.info("INFO: To get the tag delete image of Profile tag");

		//collect all the visible tags as web elements
		List<Element> visibleTags = driver.getElements(section + " > ul > li");
		log.info("INFO: visible tags are " + visibleTags.size());

		Element tagDelRef = null;
		try {
			//Search for the tag and break the loop
			for(Element visTags : visibleTags){
				String tagName = visTags.getSingleElement("css=a[class^='profileTag']").getText();
				log.info("INFO: Compare the tag Name");
				if(tagName.equalsIgnoreCase(tag)){
					log.info("INFO: Found tag - " + tagName);
					tagDelRef = visTags.getSingleElement("css=a[class='lotusDelete lconnTagCount']>img");
					break;
				}
			}
		} catch (Exception e){
			throw new AssertionError("ERROR: Tag is not present in Tagged by 'x' person");
		}

		return tagDelRef;
	}
	
	/**
	 * Invite and Follow another User
	 * @param inviteeUser - Invitee User 
	 * @return void
	 */
	public void inviteUserToMyNetwork(User inviteeUser) {
		log.info("INFO: To invite and follow another user");
		
		//Click the Invite to My Network button
		log.info("INFO: Click on Invite to My Network button");
		this.clickLinkWithJavascript(ProfilesUIConstants.Invite_OnPrem);
		
		//The "Invite to My Network" dialog is displayed
		log.info("INFO: waiting for Invite to My Network dialog to display");
		this.fluentWaitPresent(ProfilesUIConstants.InviteToMyNetworkDailog);
				
		//click "send invitation"
		log.info("INFO: Wait for Send invitation button to be available"); 
		this.fluentWaitElementVisible(ProfilesUIConstants.SendInvite);
		log.info("INFO: Click Send invitation button"); 
		this.clickLinkWait(ProfilesUIConstants.SendInvite);
		
		//The message " <user's name> has been invited to your network
		log.info("INFO: User has been invited to your network");
		this.fluentWaitTextPresent(inviteeUser.getDisplayName() + ProfilesUIConstants.InvitedMessage);

	}
	
	/**
	 * Scroll down to the end of the page and get the footer message
	 * @param void
	 * @return string
	 */
	public String getFooterMessage()
	{
		driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
		String footerMessage = driver.getSingleElement(ProfilesUIConstants.FooterResultBox).getText();
		
	    while(!(footerMessage.contains("Showing all")))
		{ 
			driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
			footerMessage = driver.getSingleElement(ProfilesUIConstants.FooterResultBox).getText();
		}
		return footerMessage;
	}
	
	/**
	 * Type user name with few letters in directory search and check corrosponding users are displayed
	 * @param User
	 * @return boolean
	 */
	public boolean isDirectorySearchResultsMatching(User userName){
		
    	String SearchUser = userName.getDisplayName().substring(0, userName.getDisplayName().length() - 1);
		driver.getSingleElement(ProfilesUIConstants.DirectorySearch).typeWithDelay(SearchUser);
		boolean check  = true;
		List<Element> widgets = driver.getVisibleElements(ProfilesUIConstants.DirectorySearchResultWidgets);
		for (Element element : widgets) {
			if(!element.isTextPresent(SearchUser)) {
				check = false;
				break; 
			}
		}
        return check;
	} 
	
	/**
	 * Type user name  in directory search and check exact user is displayed
	 * @param User
	 * @return boolean
	 */
	public boolean isDirectorySearchResultExactMatching(User userName)
	{
 	
		String SearchUser = userName.getDisplayName();
		driver.getSingleElement(ProfilesUIConstants.DirectorySearch).typeWithDelay(SearchUser);
		boolean check  = false;
		List<Element> userFullName = driver.getVisibleElements(HomepageUIConstants.userNamelink);

		for (Element uName : userFullName) {
			if(uName.getText().equalsIgnoreCase(SearchUser)) {
				check = true;
				break; 
			}
		}
        return check;
	} 
	
	/*To verify if tagName indicates greater frequency with larger bolder font
	 * @param tagName - Profile tagName
	 * @return - boolean
	 */
	public boolean isTagIndicatesGreaterFrequency(String tagName){
		log.info("INFO: To see tagName indicates greater frequency with larger bolder font");
		return driver.getFirstElement("link="+tagName).getAttribute("title").matches("[^1]");
	}
	
	/*Switch to new window and validate the current URL with expected
	 * @param  clickLocator - locator to Click, windowName - New window, expectedUrl - Expected Url,
	 * @return - boolean
	 */
	public boolean validateCurrentUrlWithExpected( String clickLocator, String windowName, String expectedUrl) {
		log.info("INFO: Switch to new window and validate the current URL with expected");
		boolean check = false; //To check if current URL matches with expected

		//Get original window handle
		String originalWindow = driver.getWindowHandle();

		//Click on the locator
		clickLink(clickLocator);

		//Switch to Help
		driver.switchToFirstMatchingWindowByPageTitle(windowName);

		//To verify the URL
		this.waitForPageLoaded(driver);
		check = driver.getCurrentUrl().toLowerCase().contains(expectedUrl.toLowerCase());

		//Close window
		this.close(cfg);

		//Switch to original window
		driver.switchToWindowByHandle(originalWindow);

		return check;
	}

	/*validate the current URL with expected
	 * @param  clickLocator - locator to Click, expectedUrl - Expected Url
	 * @return - boolean
	 */
	public boolean validateCurrentUrlWithExpected( String clickLocator, String expectedUrl) {
		log.info("INFO: validate the current URL with expected");

		//Click on the locator
		clickLinkWithJavascript(clickLocator);

		//To verify the URL
		this.waitForPageLoaded(driver);
		driver.isTextNotPresent("Loading...");
		return driver.getCurrentUrl().toLowerCase().contains(expectedUrl.toLowerCase());

	} 
	
	/*validate the window page title 
	 * @param  clickLocator - locator to Click, String windowName - window name, boolean isNewWindow - true/false
	 * @return - boolean
	 */
	public boolean validateWindowPageTitle(String clickLocator, String windowName, boolean isNewWindow) throws InterruptedException {
		log.info("INFO: validate the window page title");
		boolean check = false; //To check if page Title matches with expected
		
		if(isNewWindow) {
			//Get original window handle
			String originalWindow = driver.getWindowHandle();
			
			//open link in a new window
			clickLink(clickLocator);
			
			//Switch to new window
			driver.switchToFirstMatchingWindowByPageTitle(windowName);
			
			//To verify the Page title
			this.waitForPageLoaded(driver);
			check = driver.getTitle().toLowerCase().contains(windowName.toLowerCase());
	
			//Close window
			this.close(cfg);
	
			//Switch to original window
			driver.switchToWindowByHandle(originalWindow);
		} else {
			//open link in a new window
			clickLinkWithJavascript(clickLocator);
	
			//To verify the Page title
			this.waitForPageLoaded(driver);
			driver.isTextNotPresent("Loading...");
			check = driver.getTitle().toLowerCase().contains(windowName.toLowerCase());
		}
		
		return check;
			
	}
	
	/**
	   *Info: Entering the frame of RecentUpdate text box, typing the comment and exiting the frame.
	   *@param: none
	   *@return: none
	*/
	public void recentUpdatetypetext(){
	
	                driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.RecentupdateComment_iFrame);
	                driver.getSingleElement(ProfilesUIConstants.UpdatesTextBox).click();
	                driver.getSingleElement(ProfilesUIConstants.updateTextboxwrite).type(Data.getData().ProfileStatusUpdate);
	                driver.switchToFrame().returnToTopFrame();
	}
	
	/* To verify dailog is displayed
	 * @param - String dailogName - dialog title
	 * @return - boolean
	 */
	public boolean isDailogDisplayed(String dailogName) {
		log.info("INFO: Verify " + dailogName +" dailog is displayed");
		String dialog = "css=div[class='lotusDialogHeader'] > h1:contains("+ dailogName + ")";
		
		this.fluentWaitElementVisible(dialog);
		return this.getFirstVisibleElement(dialog).isDisplayed();
	}

	/* To Share a File in My Profile page
	 * @Param leftSideMenu - like Recent Files, fileName - Name of the file
	 * @return void
	 */
	public void shareAFile(String leftSideMenu, String fileName) {
		log.info("INFO: To Share a File in Profile page");
		String checkbox = "css=input[type='checkbox'][aria-label='" + fileName + "'], div.checkbox[title='" + fileName + "']>input[type='checkbox']";
		//Click on Share a File button
		log.info("INFO: Click on Share a File button");
		if(cfg.getUseNewUI())
		{
			this.clickLink(ProfilesUIConstants.MoreActionButton);
			this.clickLink(ProfilesUIConstants.ShareFileButtonCnx8);	
		}
		else
			this.clickLink(ProfilesUIConstants.ShareaFileButton);
		
		//Select the file check box
		log.info("INFO: Select the file checkbox");
		this.fluentWaitElementVisible(checkbox);
		driver.getFirstElement(checkbox).click();
		
		//Click Ok
		log.info("INFO: Click OK");
		this.clickOKButton();
	}
	  
	/* To update About Me and Background info
	 * @param text - To update this text
	 * @return - None
	 */
	public void updateProfileBackground(String text) {
		log.info("INFO: To update About Me and Background info");
		
		//Edit My Profile
		log.info("INFO: Edit My Profile");
		clickLinkWait(ProfilesUIConstants.MyICProfileCloud);
		
		//Go to Background tab
		log.info("Go to Background tab");
		clickLinkWait(ProfilesUIConstants.EditBackgroundTab);
		
		//Enter text in About me section
		log.info("INFO: Enter text in About me section");
		driver.getSingleElement("css=div#description_RTE");
		driver.switchToActiveElement();
		driver.switchToFrame();
		this.typeNativeInCkEditor(text);
		this.switchToTopFrame();
		
		//Enter text in About me section
		log.info("INFO: Enter text in About me section");
		driver.getSingleElement("css=div#experience_RTE");
		driver.switchToActiveElement();
		driver.switchToFrame();
		this.typeNativeInCkEditor(text);
		this.switchToTopFrame();
		
		//Click on Save and Close
		log.info("INFO: Click on Save and Close");
		clickLinkWait(ProfilesUIConstants.SaveAndCloseBtn);
	}
	
	/*To edit and add a Profile photo
	 * @Param - BaseContact contact
	 * @return - None
	 */
	public void addProfilePhoto(BaseFile file) throws Exception{
			
		//start uploading file
		TestConfiguration testConfig = cfg.getTestConfig();
		if (testConfig.getBrowserEnvironment().isLocal() && !testConfig.serverIsBrowserStack()){
			//FileCancelCancelGrid.exe must be running on the grid machines
			Runtime.getRuntime().exec(cfg.getTestConfig().getBrowserEnvironment().getAbsoluteFilePath(cfg.getUploadFilesDir(), "FileUploadCancel.exe"));
		}
		
		//Click the Choose a File button
		log.info("INFO: Click the Choose a File button");
		clickLinkWithJavascript(ProfilesUIConstants.ChooseAFileButton);
		
		//Select a File
		log.info("INFO: Select a File");
		FilesUI fUI = FilesUI.getGui(cfg.getProductName(), driver);
		fUI.setLocalFileDetector();
		Element inputField = driver.getSingleElement(ProfilesUIConstants.PhotoUploadFileSelector);
		driver.executeScript("arguments[0].setAttribute('class', 'lotusText lotusAlignLeft');", (WebElement) inputField.getBackingObject());
		driver.executeScript("arguments[0].setAttribute('style', 'display: block; opacity: 0; position: absolute; left: 0px; top: 0px;');", (WebElement) inputField.getBackingObject());
		inputField.typeFilePath(FilesUI.getFileUploadPath(file.getName(), cfg));
	}
	
	/* Update contact info and save
	 * @Param - String fieldName, String typeText, String expectedMsg
	 * @return boolean
	 */
	public boolean updateContactInfoAndSave(String locator, String typeText, String expectedMsg) {
		log.info("INFO: Update Contact Information, Click Save button and Verify message displayed");
		
		//Type text in field
		log.info("INFO: Type text in field");
		clearText(locator);
		typeText(locator, typeText);
		
		//Click on Save button
		log.info("INFO: Click on Save button");
		clickSaveButton();
		
		//Verify the msg displayed is as expected
		log.info("INFO: Verify the msg displayed is as expected");
		return fluentWaitTextPresent(expectedMsg);
		
	}
	
	/**
	 * To verify all the valid matches are displayed
	 * @param text - text to search
	 * @param typeahead - The typeahead from which the selection will be made
	 * @return boolean
	 */
	public boolean typeaheadAndValidateMatches(String text, String typeahead){

		//Collect all the options
		List<Element> options = driver.getVisibleElements(typeahead);
		
		//Iterate through the list and select each from drop down
		Iterator<Element> iterator = options.iterator();

		while (iterator.hasNext()) {
			Element option = iterator.next();
			if (!option.getText().contains(text)){
				log.info("INFO: Text not found " + text);
				return false;
			}
		}
		return true;
	}
	
	/*
	 * To get contact Image
	 */
	public String getContactImage(User contact) {
		return "css=img[alt='" + contact.getDisplayName() + "']";
	}
			
	/**
	 * Type user name with few letters in search control and check if corresponding user is displayed
	 * @param User
	 * @return boolean
	 */
	public boolean isdefaultDirectorySearchResultsMatching(User userName){
		
		driver.getSingleElement(ProfilesUIConstants.ProfilesSearchForUser).type(userName.getDisplayName());
		clickLink(ProfilesUIConstants.ProfileSearch);
		boolean check  = true;
		List<Element> widgets = driver.getVisibleElements(ProfilesUIConstants.DirectorySearchResultWidgets);
		for (Element element : widgets) {
			if(!element.isTextPresent(userName.getDisplayName())) {
				check = false;
				break; 
			}
		}
        return check;
	}
	
	/*To type profile Tag and Click on Plus sign
	 * @param - tagName - Tag Name
	 * @return - none
	 */
	public void addProfileTag(String tagName) {
		
		log.info("INFO: To add profile tag");
		int itr = 10; //Iterate max 10 times
		
		//Enter new tag
		log.info("INFO: Enter new tag in Tags widget");
		this.fluentWaitElementVisible(ProfilesUIConstants.ProfilesTagTypeAhead);
		
		while(itr>0){	//To iterate till the tagName entered matches
			this.clearText(ProfilesUIConstants.ProfilesTagTypeAhead);
			this.typeTextWithDelay(ProfilesUIConstants.ProfilesTagTypeAhead, tagName);
			if(driver.getFirstElement(ProfilesUIConstants.ProfilesTagTypeAhead).getAttribute("value").contentEquals(tagName))
				break;
			itr--;
		}		
		
		//Click on plus sign
		log.info("INFO: Click on add tag button");
		this.clickLink(ProfilesUIConstants.ProfilesAddTag);
		this.getFirstVisibleElement("link="+ tagName);
	}
	
	/*To type profile Tag and Click Keyboard 'ENTER' key
	 * @param - tagName - Tag Name
	 * @return - none
	 */
	public void addProfileTagUsingKeyboard(String tagName) {
		
		log.info("INFO: To add profile tag and Click Enter");
		int itr = 5; //Iterate max 5 times
		
		//Enter new tag
		log.info("INFO: Enter new tag in Tags widget");
		this.fluentWaitElementVisible(ProfilesUIConstants.ProfilesTagTypeAhead);
		
		while(itr>0){	//To iterate till the tagName entered matches
			this.clearText(ProfilesUIConstants.ProfilesTagTypeAhead);
			this.typeTextWithDelay(ProfilesUIConstants.ProfilesTagTypeAhead, tagName);
			if(driver.getFirstElement(ProfilesUIConstants.ProfilesTagTypeAhead).getAttribute("value").contentEquals(tagName))
				break;
			itr--;
		}		
		
		//Click on keyboard ENTER key
		log.info("INFO: Click on ENTER key from keyboard");
		WebDriver wd = (WebDriver) driver.getBackingObject();	
		WebElement element = wd.findElement(By.cssSelector(ProfilesUIConstants.ProfilesTagInputField));
		element.sendKeys(Keys.ENTER);
	}
	
	/*
	 * Open another User's profile 
	 * @Param User testUser 
	 * @return None
	 */
	public void openAnotherUserProfile(User testUser) {
		log.info("INFO: open another User's Profile page");

		//Search for the another User
		log.info("INFO: Search for the " + testUser.getDisplayName());
		this.searchForUser(testUser);
		
		//Click on testUser	
		Element userLinkInList = this.getFirstVisibleElement("link="+testUser.getDisplayName());
		userLinkInList.doubleClick();
	}
	
	/*
	 * Open another User's profile by user email
	 * @Param User testUser 
	 * @return None
	 */
	public void openAnotherUserProfileByEmail(User testUser) {
		log.info("INFO: open another User's Profile page");

		//Search for the another User
		log.info("INFO: Search for the " + testUser.getDisplayName());
		this.fluentWaitTextPresent(Data.getData().feedFooter);
		this.searchForUserByEmail(testUser);
		
		//Click on testUser
		log.info("INFO: Click on " + testUser.getDisplayName());
		this.clickLink("link="+testUser.getDisplayName());
		this.fluentWaitTextPresent(Data.getData().feedFooter);
	}
	
	/* Get Invitation record of a particular User from Invitation view
	 * @param - User
	 * @return - Invitation record
	 */
	public Element getInvitationRecordOfUser(User testUser) {
		log.info("INFO: To get Invitation record of a particular User from Invitation view");
		
		//collect all the invitations as web elements
		List<Element> invitations = driver.getElements("css=div.invitationsNodeDiv");
		log.info("INFO: visible invitations are " + invitations.size());

		Element invitationRecord = null;
		try {
			//Search for the particular User invitation and break the loop
			for(Element invite : invitations){
				String user = invite.getSingleElement("css=div[class^='lotusPhoto']>img").getAttribute("alt");
				log.info("INFO: Compare the user Name");
				if(testUser.getDisplayName().equalsIgnoreCase(user)){
					log.info("INFO: Found user invitation - " + user);
					invitationRecord = invite;
					break;
				}
			}
			invitationRecord.isDisplayed();
		} catch (Exception e){
			throw new AssertionError("ERROR: Invitation record of a particular user is not present " + testUser.getDisplayName());
		}
		
		return invitationRecord;
	}
	
	/* Remove an existing network relationship and follow
	 * @param - testUser1, testUser2
	 * @return - none
	 */
	public void removeExistingNetworkRelation(User testUser1, User testUser2) {
		log.info("INFO: To remove an existing network realtionship and follow");
		
		//Verify the Invite to My Network button exists, If not Remove from network and invite
		log.info("INFO: Verify if Invite to My Network button exists");
		if(this.isElementPresent(ProfilesUIConstants.Invite_OnPrem) && driver.getFirstElement(ProfilesUIConstants.Invite_OnPrem).isVisible()) {
			//Do nothing
		} else if (this.isElementPresent(ProfilesUIConstants.NetworkRemove) && driver.getFirstElement(ProfilesUIConstants.NetworkRemove).isVisible()) {
			this.clickLink(ProfilesUIConstants.NetworkRemove);
			this.fluentWaitTextPresent(testUser2.getDisplayName() + Data.getData().removedNetworkMessage);
		} else if(this.isElementPresent(ProfilesUIConstants.AcceptInvite_OnPrem) && driver.getFirstElement(ProfilesUIConstants.AcceptInvite_OnPrem).isVisible()) {
			//Ignore Network invitation of testUser1
			log.info("INFO: Ignore Network invitation of " + testUser2.getDisplayName());
			Profile_View_Menu.MY_PROFILE.select(this);
			this.ignoreUserInvite(testUser2);
			
			//Search for the testUser2
			log.info("INFO: Search for the " + testUser2.getDisplayName());
			Profile_View_Menu.MY_PROFILE.select(this);
			this.openAnotherUserProfile(testUser2);
		} else {
			log.info("INFO: login to " + testUser2.getDisplayName()+ " and ignore invitation of " + testUser1.getDisplayName());
			//logout testUser1
			this.logout();
			
			//Load the component and login as below user
			this.loadComponent(Data.getData().ComponentProfiles, true);
			this.login(testUser2);	
			
			//Ignore Network invitation of testUser1
			log.info("INFO: Ignore Network invitation of " + testUser1.getDisplayName());
			this.ignoreUserInvite(testUser1);
			
			//logout of testUser2
			this.logout();
			
			//Load the component and login as below user
			this.loadComponent(Data.getData().ComponentProfiles, true);
			this.login(testUser1);
			
			//Open People -> My Profile
			log.info("INFO: Open People, My Profile page");
			this.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);
			Profile_View_Menu.MY_PROFILE.select(this);	
			
			//Search for the testUser2
			log.info("INFO: Search for the " + testUser2.getDisplayName());
			this.openAnotherUserProfile(testUser2);
			
		}
	}
	
	/* Cisco Integration test: Check Phone call link href if Cisco Jabber is enabled and user has phone number set.
	 * @param - String location - "basicInformation": check basic information phone call link
	 *                            "contactInformation": check contact infromation phone call link(s)
	 * @return - true if phone call links show correctly, false otherwise.
	 */
	public boolean checkJabberPhoneCallLink(String location) {
		String jabberProtocolLink = "sip://${phone}";
		String phoneElementSelector = "";
		if (location.equals("basicInformation")) {
			phoneElementSelector = ProfilesUIConstants.BasicInfoPhoneCallLink;
		} else if (location.equals("contactInformation")) {
			phoneElementSelector = ProfilesUIConstants.ContactInfoPhoneCallLink;
		}
		try {
			    if(driver.getElements(phoneElementSelector).isEmpty() && location.equals("contactInformation") && isPhoneNumberSet()) {
			    	log.info("ERROR: Phone number has set but phone call link is not present.");
			    	return false;
			    }
				for(Iterator<Element> i = driver.getElements(phoneElementSelector).iterator(); i.hasNext();) {
					Element phoneElement = i.next();
					String phoneNo = phoneElement.getText();
					log.info("INFO: User phone number: " + phoneNo);
					log.info("INFO: Check if phone call link match phone no.");
					if (!phoneElement.getAttribute("href").contentEquals(jabberProtocolLink.replace("${phone}", phoneNo))) {
						return false;
					}		
				}
			return true;
		} catch(Exception e) {
			log.info("ERROR: phone call link is not present.");
			return false;
		}
	}
	
	/* Check if the user on the profile page has phone number set by checking Contact Information tab.
	 * @return - true if one or more phone number has set, false otherwise.
	 */
	private boolean isPhoneNumberSet() {
		String selector = "div[id='_contactInfo_profileDetails_widget_container']//*[contains(text(),'${phone_title}')]";
		return (driver.isElementPresent(selector.replace("${phone_title}", "Office Number")) || 
				driver.isElementPresent(selector.replace("${phone_title}", "Mobile number")));
	}
	
    public abstract void verifySearchUserContents();
	
	public abstract String getProfileUrl();
	
	public abstract void getDirectoryPageUrl(User testUser);
	
	public abstract void VerifyDirectoryPageText();
	
	/**
	 * Navigate to the specified users profile page
	 * 
	 * @param apiUserToNavigateTo - The APIProfilesHandler instance of the user whose profile is to be navigated to in the UI
	 */
	public void navigateToUserProfile(APIProfilesHandler apiUserToNavigateTo) {
		
		log.info("INFO: Now navigating to the URL of the profile page for the user with user name: " + apiUserToNavigateTo.getDesplayName());
		driver.navigate().to(cfg.getTestConfig().getBrowserURL() + Data.User_profilePage + apiUserToNavigateTo.getUUID());
	}
	

	/**
	 * Navigate to Created Profile ,navigate to edit profile background and verify Tiny Editor functionality
	 * @param Base profile object
	 * @return String Text present in Description of Tiny Editor.
	 */
	public String verifyTinyEditorInProfile(BaseProfile baseProfile) {

		 TinyEditorUI tDescription = new TinyEditorUI(driver);
		 TinyEditorUI tExperience = new TinyEditorUI(driver);

		 tDescription.updateLocators("description");
		 tDescription.clickOnMoreLink();

		 tExperience.updateLocators("experience");
		 tExperience.clickOnMoreLink();


		log.info("INFO: Entering a description and validating the functionality of Tiny Editor");
		if (baseProfile.getAboutMe() != null && baseProfile.getBackground() != null) {

			String TE_Functionality[] = baseProfile.getTinyEditorFunctionalityToRun().split(",");
			for (String functionality : TE_Functionality) {
				switch (functionality) {
				
				case "verifyParaInTinyEditor":
					log.info("INFO: Validate Paragragh and header functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyParaInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyParaInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyAttributesInTinyEditor":
					log.info("INFO: Validate Attributes functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyAttributesInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyAttributesInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyPermanentPenInTinyEditor":
					log.info("INFO: Validate Permanent Pen functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyPermanentPenInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyPermanentPenInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyUndoRedoInTinyEditor":
					log.info("INFO: Validate Undo and Redo functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyUndoRedoInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyUndoRedoInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyAlignmentInTinyEditor":
					log.info("INFO: Validate Alignment functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyAlignmentInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyAlignmentInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyIndentsInTinyEditor":
					log.info("INFO: Validate Indents functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyIndentsInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyIndentsInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyBulletsAndNumbersInTinyEditor":
					log.info("INFO: Validate Bullets and Numbers functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyBulletsAndNumbersInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyBulletsAndNumbersInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyHorizontalLineInTinyEditor":
					log.info("INFO: Validate Horizontal Line functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyHorizontalLineInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyHorizontalLineInTinyEditor(baseProfile.getBackground());
					break;
				case "verifySpecialCharacterInTinyEditor":
					log.info("INFO: Validate Special character functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifySpecialSymbolsInTinyEditor("SpecialChar");
				    tExperience.updateLocators("experience");
				    tExperience.verifySpecialSymbolsInTinyEditor("SpecialChar");
					break;
				case "verifyEmotionsInTinyEditor":
					log.info("INFO: Validate Emoticons functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifySpecialSymbolsInTinyEditor("Emotions");
				    tExperience.updateLocators("experience");
				    tExperience.verifySpecialSymbolsInTinyEditor("Emotions");
					break;
				case "verifySpellCheckInTinyEditor":
					log.info("INFO: Validate Horizontal Line functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifySpellCheckInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifySpellCheckInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyRowsCoulmnOfTableInTinyEditor":
					log.info("INFO: Validate Rows and Columns of Table in Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyRowsCoulmnOfTableInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyRowsCoulmnOfTableInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyFormatPainterInTinyEditor":
					log.info("INFO: Validate Format Painter in Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyFormatPainterInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyFormatPainterInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyFontInTinyEditor":
					log.info("INFO: Validate font functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyFontInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyFontInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyFontSizeInTinyEditor":
					log.info("INFO: Validate font Size functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyFontSizeInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyFontSizeInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyLinkImageInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyLinkImageInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyRightLeftParagraphInTinyEditor":
					log.info("INFO: Validate Left to Right paragraph functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyRightLeftParagraphInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyRightLeftParagraphInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyOtherTextAttributesAndFullScreenInTinyEditor":
					log.info("INFO: Validate other text attributes functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyOtherTextAttributesAndFullScreenInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyOtherTextAttributesAndFullScreenInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyFindReplaceInTinyEditor":
					log.info("INFO: Validate Find and Replace functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyRightLeftParagraphInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyRightLeftParagraphInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyInsertLinkImageInTinyEditor":
					log.info("INFO: Validate Link Image functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyInsertLinkImageInTinyEditor("description_"+baseProfile.getName());
				    tExperience.updateLocators("experience");
				    tExperience.verifyInsertLinkImageInTinyEditor("experience_"+baseProfile.getName());
					break;
				case "verifyTextColorInTinyEditor":
					log.info("INFO: Validate Font Text Color functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyTextColorInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyTextColorInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyBackGroundColorInTinyEditor":
					log.info("INFO: Validate Font BackGround Color functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyBackGroundColorInTinyEditor(baseProfile.getAboutMe());
					tExperience.updateLocators("experience");
					tExperience.verifyBackGroundColorInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyWordCountInTinyEditor":
					log.info("INFO: Validate Word Count functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyWordCountInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyWordCountInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyUploadImageFromDiskInTinyEditor":
					log.info("INFO: Validate Upload image from Disk functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyUploadImageFromDiskInTinyEditor();
				    tExperience.updateLocators("experience");
				    tExperience.verifyUploadImageFromDiskInTinyEditor();
					break;
				case "verifyBlockQuoteInTinyEditor":
					log.info("INFO: Validate Block quote functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyBlockQuoteInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyBlockQuoteInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyInsertMediaInTinyEditor":
					log.info("INFO: Validate Insert Media functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyInsertMediaInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyInsertMediaInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyLinkToConnectionsFilesInTinyEditor":
					log.info("INFO: Validate Link to connections files from files in Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.addLinkToConnectionsFilesInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.addLinkToConnectionsFilesInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyCodeSampleIntinyEditor":
					log.info("INFO: Validate Code Sample functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyCodeSampleIntinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyCodeSampleIntinyEditor(baseProfile.getBackground());
					break;
				case "verifyInsertiFrameInTinyEditor":
					log.info("INFO: Validate Insert iFrame functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyInsertiFrameInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyInsertiFrameInTinyEditor(baseProfile.getBackground());
					break;
				case "verifyEditDescriptionInTinyEditor":
					log.info("INFO: Validate edit functionality of Tiny Editor");
					tDescription.updateLocators("description");
					tDescription.verifyDefaultCaseInTinyEditor(baseProfile.getAboutMe());
				    tExperience.updateLocators("experience");
				    tExperience.verifyDefaultCaseInTinyEditor(baseProfile.getBackground());
					break;	
				}
			}
		}
		
		log.info("INFO: Get the text from Profile BackGround Tiny Editor body" );
		String description = tDescription.getTextFromTinyEditor();
		
		log.info("INFO: Get the text from Profile AboutMe Tiny Editor body" );
		String experience = tExperience.getTextFromTinyEditor();
		
		// Save the Blog Entry
		log.info("INFO: Saving the profile data ");
		clickLinkWait(ProfilesUIConstants.SaveAndCloseBtn);

		fluentWaitElementVisible(ProfilesUIConstants.BackgroundTab);
		if(!baseProfile.getName().contains("verifyMyProfileTinyEditorInsertLink"))
		Assert.assertEquals(description, experience);
		return description;
	}
	
	public String getBackGroundDescriptionText() {
		this.fluentWaitElementVisible(ProfilesUIConstants.backGroundDescription);
		return this.getFirstVisibleElement(ProfilesUIConstants.backGroundDescription).getText();
	}

	public void verifyInsertedLink(String profile)
	{

		if(profile.equals("backGroundAboutme"))
		{
			clickLinkWithJavascript(ProfilesUIConstants.BackgroundTab);
			clickLinkWithJavascript(ProfilesUIConstants.linkInaboutMe);
			Assert.assertTrue(fluentWaitElementVisible(BaseUIConstants.imagePreview));
			driver.navigate().back();
			waitForPageLoaded(driver);
			fluentWaitElementVisible(ProfilesUIConstants.BackgroundTab);
			clickLinkWithJavascript(ProfilesUIConstants.BackgroundTab);
			clickLinkWithJavascript(ProfilesUIConstants.linkInBackground);
			waitForPageLoaded(driver);
			Assert.assertTrue(fluentWaitElementVisible(BaseUIConstants.imagePreview));
			driver.navigate().back();

		}
		
		else
		{
			TinyEditorUI tui = new TinyEditorUI(driver);
			tui.verifyInsertedLinkinDescription(profile);
		}
	}

	/* Verify Inviter User name in Invited User Invitation section
	 * @param - testUserA ie Inviter user
	 * @return - true
	 */
	public Boolean isInviterUserNameDisplayed(User testUser)
	{
		Boolean flag= false;
		List<Element> inviters = driver.getElements(ProfilesUIConstants.InviterUserNames);
		for(Element inviter : inviters)
		{
			if(inviter.getText().equals(testUser.getDisplayName()));
			flag=true;
			break;
		}
		return flag;
	}
	
	/* Verify User name in MyProfile
	 * @param - testUser
	 * @return - true
	 */
	public Boolean isUserNameDisplayed(User testUser)
	{
		Boolean flag= false;
		Element user = driver.getSingleElement(ProfilesUIConstants.profileName);
		
		if (user.getText().equals(testUser.getDisplayName())) {
			flag = true;
		}
		
		return flag;
	}
	
	/* Verify User Email Address in MyProfile
	 * @param - testUser
	 * @return - true
	 */
	public Boolean isUserEmailDisplayed(User testUser)
	{
		Boolean flag= false;
		Element user = driver.getSingleElement(ProfilesUIConstants.profileEmail);
		
		if (user.getText().equals(testUser.getEmail())) {
			flag = true;
		}
		
		return flag;
	}
	
}
