package com.ibm.conn.auto.webui.cloud;

import java.util.Iterator;
import java.util.List;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.testng.Assert;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration.BrowserType;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseContact;
import com.ibm.conn.auto.appobjects.base.BaseProfile;
import com.ibm.conn.auto.appobjects.base.BaseProfile.profileInfo;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.menu.MyContacts_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Profile_View_Menu;
import com.ibm.conn.auto.webui.ProfilesUI;

public class ProfilesUICloud extends ProfilesUI {
	
	/** Start of selectors section*/
	public static String PeopleText = "People";
	
	public static String People = "css=a[id='networkMenu_btn']:contains(People), div[id='lotusBanner'] div[class^='people']";
	public static String MyProfile = "css=td[class='lotusNowrap'] a:contains(My Profile), div[id='lotusBanner'] a[class^='myprofile']";
	public static String EditMyProfileBtn = "css=input[id='editProfileButton']";
	public static String SocialTags = "css=span[id='socialTagsId']";
	public static String FriendsLink = "css=span[id='friendsId']";
	public static String MyLinks = "css=a[id='addLinkLinkRollButton']";
	public static String DownloadVCard = "css=button[id='btn_actn__personCardDownloadVCard']";
	
	public static String RecentUpdates = "css=a[class='_linkHolder lotusLeft']:contains(Recent Updates)";
	public String ContactInfo = "css=a[class='_linkHolder lotusLeft']:contains(Contact Information)";
	public String AboutMe = "css=a[class='_linkHolder lotusLeft']:contains(Background)";
	public static String FirstProfileTag = "//div[@id='tagCloud']/div/ul/li/a";
	public static String HelpWindowTitle = "IBM Connections";
	public String MessageOfContact = "css=div div.lotusInactive.lotusFirst";
	public String BusinessCardMenuDropIcon = "css=div#businessCardContent > div.vcard.lotusHeading.lotusLeft > a.menu_drop_icon";
	public static String MoreActionsLink = "css=a#javlinActionMore";
	public static String StartAnActivityLink = "css=table#javlinActionsExpanded a:contains('Start an Activity')";
	public static String ActivityName = "css=input#startform_titleInput";
	public static String ActivityTags = "css=input#startformtagz";
	public static String ActivityGoal = "css=textarea#startform_descriptionInput";
	public static String ActivityDueDate = "css=input#startformduedate";
	public static String ActivitySaveButton = "css=input[value='Save']";
	public static String BusinessCardWindow = "css=div.personMenu";
	public static String VcardProfileLink = "css=ul.topActionHolder a:contains('Profile')";
	public static String VcardFilesLink = "css=ul.topActionHolder a:contains('Files')";
	public static String VcardChatLink = "css=a#javlinFooterActionChat";
	public static String VcardBusinessCardDetails = "css=div.businessCard > ul";
	public static String ChatMenu= "css=a#chatMenu_btn";
	public static String EditFirstOtherInformationField = "css=[class^='lotusText'][id='address2']";
	public static String EditSecondOtherInformationField = "css=[class^='lotusText'][id='address3']";
	public static String EditThirdOtherInformationField = "css=[class^='lotusText'][id='address4']";
	public static String FirstOtherDropDown = "css=select[id='phone1.label']";
	public static String SecondOtherDropDown = "css=select[id='phone2.label']";
	public static String ThirdOtherDropDown = "css=select[id='phone3.label']";
	public static String FirstOtherTextField = "css=[class^='lotusText'][id='phone1']";
	public static String SecondOtherTextField = "css=[class^='lotusText'][id='phone2']";
	public static String ThirdOtherTextField = "css=[class^='lotusText'][id='phone3']";
	public static String InviteToAMeetingLink = "css=table#javlinActionsExpanded a:contains('Invite to a Meeting')";
	public static String VcardCloseButton = "css=ul.topActionHolder a[title='Close']";
	
	//selectors in account setting tab
	public static String accountsetting ="css=div[id ='bss-usersMenu']";
	public static String userprofile ="css=ul > li > a[class='userprofile']";
	public static String guestuser = "css=a.myprofile";
	public static String EditMyProfile = "css=#editProfileButton";
			
	public ProfilesUICloud(RCLocationExecutor driver) {
		super(driver);
	}

	@Override
	protected String getMyProfileSelector() {
		return MyProfile;
	}
	
	@Override
	public String getInviteToMyNetwork(){
		return ProfilesUIConstants.inviteToMyNetwork;
	}

	@Override
	public String getAcceptInviteToMyNetwork(){
		return ProfilesUIConstants.acceptToMyNetwork;
	}
	
	@Override
	public String getUserInviteMessageInFollowingView(){
		return MessageOfContact;
	}
	
	@Override
	public void gotoMyNetwork() {
		Profile_View_Menu.MY_NETWORK.select(this);
	}
	
	@Override
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
	
	@Override
	public void myProfileView(){
		log.info("INFO: Cloud does not use My profiles tab at top");
	}

	@Override
	public void editMyProfile(){
		log.info("INFO: Click on Edit My Profile Button");
		
		// wait page be fully loaded
		fluentWaitPresent(DownloadVCard);
		clickLinkWait(ProfilesUIConstants.MyICProfileCloud);
	}
	
	@Override
	public void updateProfile(String uniqueId){

		waitForSameTime();
		
		//Update a users Profile
		fluentWaitPresent(ProfilesUIConstants.profileJobTitle);
		clearText(ProfilesUIConstants.profileJobTitle);
		typeText(ProfilesUIConstants.profileJobTitle, Data.getData().profJobTitle + uniqueId);
		
		fluentWaitPresent(ProfilesUIConstants.profileUserTele);
		clearText(ProfilesUIConstants.profileUserTele);
		typeText(ProfilesUIConstants.profileUserTele, Data.getData().profTelephone + uniqueId);
		
		fluentWaitPresent(ProfilesUIConstants.profileUserMobile);
		clearText(ProfilesUIConstants.profileUserMobile);
		typeText(ProfilesUIConstants.profileUserMobile, Data.getData().profMobile + uniqueId);
		
		fluentWaitPresent(ProfilesUIConstants.profileUserFax);
		clearText(ProfilesUIConstants.profileUserFax);
		typeText(ProfilesUIConstants.profileUserFax, Data.getData().profFax + uniqueId);

		fluentWaitPresent(ProfilesUIConstants.profileUserAddress);
		clearText(ProfilesUIConstants.profileUserAddress);
		typeText(ProfilesUIConstants.profileUserAddress, Data.getData().profAddress + uniqueId);
		
		clickLinkWait("css=input[value='Save and Close']");
        clickLinkWait(ProfilesUIConstants.MyICProfileCloud);
        log.info("INFO: Profile information updated and Navigated to 'my profiles' page");
	} 
	
	public void updateICPhoneNumber(){

		waitForSameTime();
		
		clearText(ProfilesUIConstants.profileUserMobile);
		typeText(ProfilesUIConstants.profileUserMobile, Data.getData().profTelephone);
		
		driver.getFirstElement(ProfilesUIConstants.SaveCloseBtn).click();

	}

	@Override
	public void verifyUserProfile(String uniqueId){

		waitForSameTime();
		
		log.info("INFO: Validate the Job Title was updated");
		fluentWaitPresent(ProfilesUIConstants.profileJobTitle);
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.profileJobTitle).getAttribute("value").equals(Data.getData().profJobTitle + uniqueId),
						  "ERROR: User Profile 'Job Title' is not equal to: " + Data.getData().profJobTitle + uniqueId);
		
		log.info("INFO: Validate the Telephone was updated");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.profileUserTele).getAttribute("value").equals(Data.getData().profTelephone + uniqueId),
						  "ERROR: User Profile 'Telephone' is not equal to: " + Data.getData().profTelephone + uniqueId);
		
		log.info("INFO: Validate the Mobile was updated");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.profileUserMobile).getAttribute("value").equals(Data.getData().profMobile + uniqueId),
					      "ERROR: User Profile 'Mobile' is not equal to: " + Data.getData().profMobile + uniqueId);
		
		log.info("INFO: Validate the Fax was updated");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.profileUserFax).getAttribute("value").equals(Data.getData().profFax + uniqueId),
						  "ERROR: User Profile 'Fax' is not equal to: " + Data.getData().profFax + uniqueId);
		
		log.info("INFO: Validate the Adress was updated");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.profileUserAddress).getAttribute("value").equals(Data.getData().profAddress + uniqueId),
						  "ERROR: User Profile 'Address' is not equal to: " + Data.getData().profAddress + uniqueId);

	
		driver.getFirstElement(ProfilesUIConstants.profileUserCancel).click();

	}
	
	@Override
	public void profilesAddATag(User testUser, String tag){
		fluentWaitPresent(ProfilesUIConstants.ProfilesTagTypeAhead);
		typeText(ProfilesUIConstants.ProfilesTagTypeAhead, tag);
		clickLink(ProfilesUIConstants.ProfilesAddTag);
		fluentWaitTextPresent(tag);
		
		clickLink(FirstProfileTag);
		clickLink(ProfilesUIConstants.Show100PerPage);
		fluentWaitTextPresent(testUser.getDisplayName());
		
	}
	
	@Override
	public void gotoRecentUpdates() {
		log.info("Go to Recent Updates");
		fluentWaitPresent(RecentUpdates);
		clickLink(RecentUpdates);
		log.info("Clicked on Recent Updates");
	}
	
	@Override
	public void checkPageTitle() {
		log.info("Looking for text 'People'");
		Assert.assertTrue(driver.isTextPresent(Data.getData().People), "'People' not found on page.");
		log.info("Found 'People' on page");
	}
	
	@Override
	public void verifyUpdatesTextArea() {
		log.info("Verify Update text area precent.");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.UpdatesTextBox),
							"Update status text area not precent.");
		switchToTopFrame();
	}
	
	@Override
	public void gotoContactInformation() {
		log.info("Go to Contact information tab");
		fluentWaitPresent(ContactInfo);
		clickLink(ContactInfo);
		log.info("Clicked on Contact Information tab");
	}

	@Override
	public void verifyContactInfomationText() {
		log.info("Verify Contact information text present.");
		Assert.assertTrue(driver.isTextPresent("Email"), 
							"Email in contact info is not precent.");
		Assert.assertTrue(driver.isTextPresent("Name"), 
					      "Name in contact info is not precent.");
		log.info("Contact information verified");
	}
	
	@Override
	public void gotoAboutMe() {
		log.info("Go to About Me tab");
		fluentWaitPresent(AboutMe);
		clickLink(AboutMe);
		log.info("Clicked on About Me tab");
	}
	
	@Override
	public void updateProfileStatus(String status) {
		log.info("Updating profile status");
		fluentWaitPresent(ProfilesUIConstants.UpdatesTextBox);
		typeText(ProfilesUIConstants.UpdatesTextBox, status);
		clickLink(ProfilesUIConstants.PostStatus);
		log.info("Posted update '"+status+"'");
	}

	/**
	 * Support Social Contact: Add SC contact
	 * click Add Contact, and then add contact page will show up
	 * */
	public void clickAddContact() {
		
		fluentWaitPresent(ProfilesUIConstants.AddNewContactButton);
		driver.getSingleElement(ProfilesUIConstants.AddNewContactButton).click();

		driver.getVisibleElements(ProfilesUIConstants.button_Add).iterator().next()
				.click();
	}
	
	public void saveContact() {

		log.info("INFO: Save Contact ");	
	
		if (cfg.getTestConfig().browserIs(BrowserType.IE)) {
			log.info("INFO: Got IE ");
			driver.getVisibleElements(ProfilesUIConstants.button_Save).iterator()
					.next().type("\n");
		}
		else {
			log.info("INFO: Got FF ");
			fluentWaitPresent(ProfilesUIConstants.button_Save);
			driver.getVisibleElements(ProfilesUIConstants.button_Save).iterator()
					.next().click();
		}
		
		if (cfg.getTestConfig().browserIs(BrowserType.CHROME)) {
			log.info("INFO: Got CHROME ");
			driver.navigate().refresh();
		}
		log.info("INFO: Passed to save a contact ");	
	}
	
	/**
	 * SC Find the Edit button by contactName, and click it.
	 * 
	 * @param contactName
	 */
	public void clickEdit(BaseContact contact) {
		
		log.info("INFO: Edit Contact: " + contact.getGiven());
		
		String title = "css=span[title='" + contact.getGiven() +"']";
		log.info("INFO: Title: " + title);
		
		fluentWaitPresent(title);
		driver.getSingleElement(title).click();

		// click Edit button
		fluentWaitPresent("css=button[title='Edit contact information']");
		driver.getSingleElement("css=button[title='Edit contact information']").click();
	}


	/**
	 * Get contact handle in My contacts -> Following
	 * @param userName - Profile User
	 * @return Element - Contact Widget
	 */
	public Element getContactInFollowingView(String userName){

		log.info("INFO: To get Contact handle in My contacts -> Following view");

		//collect all the visible contacts as web elements
		List<Element> visibleContacts = driver.getElements("css=div.sccontact div.lotusLeft.lotusFloatContent");
		log.info("INFO: visible tags are " + visibleContacts.size());

		Element visibleContact = null;
		try {
			//Search for the contact and break the loop
			for(Element visContact : visibleContacts){
				String user = visContact.getSingleElement("css=div a[class^='lotusPerson'] span.bidiAware").getText();
				log.info("INFO: Compare the User Name");
				if(user.equalsIgnoreCase(userName)){
					log.info("INFO: Found tag - " + userName);
					visibleContact = visContact;
					break;
				}
			}
		} catch (Exception e){
			throw new AssertionError("ERROR: This User is not found in Following view " + userName);
		}
		return visibleContact;

	}
	
	/*
	 * Open Profile BussinessCard 
	 * @Param None 
	 * @return None
	 */
	public void openProfileBusinessVcard() {
		//Click on Business card
		log.info("INFO: open Business card menu drop icon");
		this.waitForPageLoaded(driver);
		this.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		this.clickLinkWithJavascript(BusinessCardMenuDropIcon);
		driver.getSingleElement(BusinessCardWindow).hover();
	}
	
	/*
	 * Create an Activity 
	 * @Param uniqueId = RandomNumber 
	 * @return None
	 */
	public void createAnActivity(String uniqueId) {
		log.info("INFO: open Business card menu drop icon and Start an Activity");

		//Get original window handle
		String originalWindow = driver.getWindowHandle();
				
		//Click on Business card
		log.info("INFO: open Business card menu drop icon");
		openProfileBusinessVcard();

		//Click on More Actions
		log.info("INFO: Click on More Actions");
		this.clickLinkWait(MoreActionsLink);
		
		//Click Start an Activity action
		log.info("INFO: Click on Start and Activity");
		this.clickLink(StartAnActivityLink);
		
		//Switch to Start an Activity window
		log.info("INFO: Switch to Start an Activity Window");
		driver.switchToFirstMatchingWindowByPageTitle(Data.getData().ComponentActivitiesKeyText);
		
		//Enter Activity name
		log.info("INFO: Enter Activity name");
		this.typeText(ActivityName, "");
		this.typeText(ActivityName, Data.getData().Start_An_Activity_InputText_Name_Data + uniqueId);
		
		//Enter tag Name
		log.info("INFO: Enter tag Name");
		this.typeText(ActivityTags, Data.getData().Start_A_Entry_InputText_Tags_Data + uniqueId);
		
		//Enter Activity goal
		log.info("INFO: Enter Activity goal");
		this.typeText(ActivityGoal, Data.getData().Section_InputText_Title_Data + uniqueId);
		
		//Enter due date
		log.info("INFO: Enter due date");
		this.getFirstVisibleElement(ActivityDueDate).clear();
		pickRandomDojoDate(ActivityDueDate, true); 
		
		//Save Activity
		log.info("INFO: Save Activity");
		this.clickLink(ActivitySaveButton);
		
		//Switch to original window
		driver.switchToWindowByHandle(originalWindow);
	}
	
	/* To update profile information
	 * @param - BaseProfile profile
	 * @return None
	 */
	public void editProfile(BaseProfile profile) {
		
		log.info("Starting to edit input fields");
		editMyProfile();
		Iterator<profileInfo> iterator = profile.getEdits().iterator();
		while(iterator.hasNext()){
			switch (iterator.next()){
			case ADDRESS:
				//To edit address field
				log.info("INFO: To edit address field");
				clearText(ProfilesUIConstants.profileUserAddress);
				typeText(ProfilesUIConstants.profileUserAddress, profile.getAddress());
				break;
			case FIRSTOTHERINFO:
				//To edit First Other information field
				log.info("INFO: To edit First Other information field");
				clearText(EditFirstOtherInformationField);
				typeText(EditFirstOtherInformationField, profile.getFirstOtherInfo());
				break;
			case SECONDOTHERINFO:
				//To edit Second Other information field
				log.info("INFO: To edit Second Other information field");
				clearText(EditSecondOtherInformationField);
				typeText(EditSecondOtherInformationField, profile.getSecondOtherInfo());
				break;
			case THIRDOTHERINFO:
				//To edit Third Other information field
				log.info("INFO: To edit Third Other information field");
				clearText(EditThirdOtherInformationField);
				typeText(EditThirdOtherInformationField, profile.getThirdOtherInfo());
				break;
			case OFFNUM:
				//To edit office number field
				log.info("INFO: To edit office number field");
				clearText(ProfilesUIConstants.profileUserTele);
				typeText(ProfilesUIConstants.profileUserTele, profile.getOfficeNum());
				break;
			case MOBNUM:
				//To edit mobile number field
				log.info("INFO: To edit mobile number field");
				clearText(ProfilesUIConstants.profileUserMobile);
				typeText(ProfilesUIConstants.profileUserMobile, profile.getMobileNum());
				break;
			case FAXNUM:
				//To edit fax number field
				log.info("INFO: To edit fax number field");
				clearText(ProfilesUIConstants.profileUserFax);
				typeText(ProfilesUIConstants.profileUserFax, profile.getFaxNum());
				break;
			case FIRSTOTHER:
				//To edit First Other field
				log.info("INFO: To edit First Other field");
				driver.getSingleElement(FirstOtherDropDown).useAsDropdown().selectOptionByVisibleText(profile.getFirstOther().getMenuItem());
				clearText(FirstOtherTextField);
				typeText(FirstOtherTextField, profile.getFirstOther().getMenuItemText());	
				break;
			case SECONDOTHER:
				//To edit Second Other field
				log.info("INFO: To edit Second Other field");
				driver.getSingleElement(SecondOtherDropDown).useAsDropdown().selectOptionByVisibleText(profile.getSecondOther().getMenuItem());
				clearText(SecondOtherTextField);
				typeText(SecondOtherTextField, profile.getSecondOther().getMenuItemText());		
				break;
			case THIRDOTHER:
				//To edit Third Other field
				log.info("INFO: To edit Third Other field");
				driver.getSingleElement(ThirdOtherDropDown).useAsDropdown().selectOptionByVisibleText(profile.getThirdOther().getMenuItem());
				clearText(ThirdOtherTextField);
				typeText(ThirdOtherTextField, profile.getThirdOther().getMenuItemText());	
				break;
			case JOBTITLE:
				//To edit jobTitle field
				log.info("INFO: To edit jobTitle field");
				clearText(ProfilesUIConstants.profileJobTitle);
				typeText(ProfilesUIConstants.profileJobTitle, profile.getJobTitle());
				break;
			default:
				break;
			 }
		}
		
		log.info("INFO: Save & Close edit profile");
		driver.getSingleElement(ProfilesUIConstants.SaveAndCloseBtn).click();
		
	}
	
	/*
	 * Open another User's profile
	 * return welcome text
	 */
	public boolean isHelpBannerTextPresent() {
		return this.isTextPresent("Reasons to Update Your Profile\n" +
				"Be visible. Be found. Be amazing.\n" +
				"Post a picture of yourself so others can get to know you.\n" +
				"Tag yourself so others can tap into your knowledge.\n" +
				"Describe yourself so others can find you easily.");
	}
	
	/*
	 * open another User's profile URL
	 *  return get UserProfile URL
	 */
	public String getUserProfileUrl()
	{
		return Data.getData().myProfileUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", driver.getCurrentUrl().split("userid=")[1]);
		
	}
	/*
	 * open another User's profile URL
	 *  return get networkViewLink  URL
	 */
	public String getMyNetworkUrl()
	{
		return Data.getData().networkViewAllLinkUrl.replaceAll("SERVER", cfg.getServerURL());
	}

	@Override
	public String getNetworkUserUrl() {
		return  Data.getData().userProfilePageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", driver.getCurrentUrl().split("userid=")[1].split("&")[0]);
	}
	
	@Override
	public void openBusinessCardOfUser(User testUser)
	{
		log.info("INFO: Click on image " + testUser.getDisplayName());
		this.fluentWaitTextPresent(Data.getData().feedFooter);
		this.clickLinkWait("css=a[title='Business card for " + testUser.getDisplayName() + "']");
		
	}
	
	@Override
	public String openViewLinkUrl() {
		 return Data.getData().networkViewAllLinkUrl.replaceAll("SERVER", cfg.getServerURL());
	}
	
	/*To verify following links are present in Business Card(Files, Profiles, Chat and More Actions -> Start an Activity)
	 * @param - None
	 * @return - None
	 */
	public void verifyLinksInBusinessCard() {
		log.info("INFO: To verify following links are present in Business Card(Files, Profiles, Chat and More Actions -> Start an Activity)");
		
		//Profile link present
		log.info("INFO: Verify card contains Profile link");
		Assert.assertTrue(this.isElementPresent(ProfilesUICloud.VcardProfileLink),
						 "ERROR: Business card do not contain Profile link");
		
		//Files link present
		log.info("INFO: Verify card contains Files link");
		Assert.assertTrue(this.isElementPresent(ProfilesUICloud.VcardFilesLink),
						 "ERROR: Business card do not contain Files link");
		
		//Chat link present
		log.info("INFO: Verify card contains Chat link");
		Assert.assertTrue(this.isElementPresent(ProfilesUICloud.VcardChatLink),
						 "ERROR: Business card do not contain Chat link");
				
		//More Actions link present
		log.info("INFO: Verify card contains More Actions link");
		Assert.assertTrue(this.isElementPresent(ProfilesUICloud.MoreActionsLink),
						 "ERROR: Business card do not contain More Actions link");
		
		//Start an Activity link present
		log.info("INFO: Verify card contains More Actions -> Start an Activity link");
		this.clickLinkWait(ProfilesUICloud.MoreActionsLink);
		Assert.assertTrue(this.isElementPresent(ProfilesUICloud.MoreActionsLink),
						 "ERROR: Business card do not contain More Actions -> Start an Activity link");
	}
	
	/*To verify profile data is present in Business Card(Name, JobTitle, Address, Email Address, office phone# and Chat status etc
	 * @param - None
	 * @return - None
	 */
	public void verifyProfileDataInBusinessCard(User testUser1, String uniqueId, String orgName) {
		log.info("INFO: To verify profile data is present in Business Card");
		
		//Collect Business card details
		log.info("INFO: Collect Business card details");
		String bCardDetails = driver.getSingleElement(VcardBusinessCardDetails).getText();
				
		//User Name
		log.info("INFO: Validate User's Name is present");
		Assert.assertTrue(bCardDetails.contains(testUser1.getDisplayName()),
						 "ERROR: Expected to find " + testUser1.getDisplayName() + "inside the business card details");
		
		//Job Title
		log.info("INFO: Validate Job title has been updated");
		Assert.assertTrue(bCardDetails.contains(Data.getData().profJobTitle),
						 "ERROR: Expected to find " + Data.getData().profJobTitle + uniqueId + "inside the business card details");
		
		//User's Organization Name
		log.info("INFO: Validate User's Organization Name is present");
		Assert.assertTrue(bCardDetails.contains(orgName),
						 "ERROR: Expected to find " + orgName + "inside the business card details");
		
		//User's Address
		log.info("INFO: Validate User's Address has been updated");
		Assert.assertTrue(bCardDetails.contains(Data.getData().profAddress),
						 "ERROR: Expected to find " + Data.getData().profAddress + uniqueId + "inside the business card details");
		
		//User's email address
		log.info("INFO: Validate User's email address is present");
		Assert.assertTrue(bCardDetails.contains(testUser1.getEmail()),
						 "ERROR: Expected to find " + testUser1.getEmail() + "inside the business card details");
		
		//User's office phone
		log.info("INFO: Validate User's office phone has been updated");
		Assert.assertTrue(bCardDetails.contains(Data.getData().profTelephone),
						 "ERROR: Expected to find " + Data.getData().profTelephone + uniqueId + "inside the business card details");
		
		
		//Same time Chat status
		log.info("INFO: Validate Sametime Chat status has been updated");
		if(cfg.isSametimeEnabled()) {
			String sameTimeStatus = driver.getFirstElement(ProfilesUICloud.ChatMenu).getAttribute("title").replaceAll("Chat - ", "").trim();
			Assert.assertTrue(bCardDetails.contains(sameTimeStatus),
							 "ERROR: Expected to find " + sameTimeStatus + "inside the business card details");
		}
	}
	
	/**
	 *Info: Validation of recent updates filter by dropdown
	 *@param: none
	 *@return: none
	 */
	 public void recentUpdatesFilterBy() {
		   Assert.assertEquals(driver.getSingleElement(ProfilesUIConstants.recentupdatesFilterby).getText(), "All\nStatus Updates\nActivities\nBlogs\nCommunities\nFiles\nForums\nWikis\nSurveys\nContacts\nEvents\nDocs",
					"ERROR: The Filter is not having correct data");
	
	 }
	  
	 /**
		 *Info: validating dyk help topic on prem
		 *@param: none
		 *@return: none
		 */
	 public void helptopicDYKwidget(){
	 log.info("INFO:Verifying the help topic");
		Assert.assertTrue(fluentWaitTextPresent(Data.getData().dykHelpMsg),
				  "ERROR: help topic is not present");
	 }
	 
	 /**
		 *Info: validating userprofile page url 
		 *@param: none
		 *@return: URL
		 */
	 public String userProfilePageUrl() {
		 return Data.getData().userProfilePageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", driver.getCurrentUrl().split("userid=")[1].split("&")[0]);
				 		 
	 }
	 
	 /**
		 *Info: validating Person card popup in on Prem servers. 
		 *@param: none
		 *@return: none
		 */
	 public  void dykPersoncardpopup(){
		 clickLink(ProfilesUIConstants.DoyouknowUserBizcard);
		 Assert.assertTrue(fluentWaitElementVisible(ProfilesUICloud.BusinessCardWindow),
					"ERROR: The User business card  is not opened");
	 }
	 
	 /**
		 * Accept network Invite
		 * @param inviterUser - Invited User 
		 * @return void
		 */
		public void acceptUserInvite(User inviterUser) {
			
			//Click on 'new invitation'
			log.info("INFO: Click on 'new invitation'");
			this.fluentWaitElementVisible(ProfilesUIConstants.networkViewAllLink);
			this.clickLinkWithJavascript(ProfilesUIConstants.networkViewAllLink);
			MyContacts_LeftNav_Menu.INVITATIONS.open(this);
			
			//Click the (Inviter's) User's name link in the invitation, to open their Profile page
			log.info("INFO: Click on " + inviterUser.getDisplayName() + " to open their profile page");
			this.fluentWaitTextPresent(inviterUser.getDisplayName());
			this.clickLinkWithJavascript("link="+ inviterUser.getDisplayName());
			
			//Click the "Accept Invitation" button
			log.info("INFO: Click on Accept Invitation button");
			this.clickLinkWithJavascript(ProfilesUIConstants.AcceptInvite_OnPrem);
			this.fluentWaitTextPresent(inviterUser.getDisplayName() + ProfilesUIConstants.AcceptedMessage);
		}
		
		/* Ignore User from My Network
		 * @param - User
		 * @return - none
		 */
		public void ignoreUserInvite(User invitedUser) {
			
			//Click on 'new invitation'
			log.info("INFO: Click on 'new invitation'");
			this.fluentWaitElementVisible(ProfilesUIConstants.networkViewAllLink);
			this.clickLinkWithJavascript(ProfilesUIConstants.networkViewAllLink);
			MyContacts_LeftNav_Menu.INVITATIONS.open(this);
			
			//Click on "Ignore Invitation" button
			log.info("INFO: Click on Ignore Invitation button");
			getInvitationRecordOfUser(invitedUser).getSingleElement(ProfilesUIConstants.IgnoreButton).click();
			this.fluentWaitTextPresent("Successfully ignored " + invitedUser.getDisplayName());
			
		}
		
	@Override
	public void verifySearchUserContents() {		
		//Verify directory is the default selection in the search control
		log.info("INFO:Verifying directory is the default selection in the search control");
		driver.getSingleElement(ProfilesUIConstants.ICAutoDirectory).isSelected();
	
		//Click on directory
		log.info("INFO:Clicking on directory");
	    this.clickLink(ProfilesUIConstants.Orgdirdefaultsearch);
	    
	    //Verify directory present in search drop down
	    log.info("INFO:Verifying directory present in search dropdown");
	    Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.Directory),
				"ERROR:Directory option is not present");
	    
	    //Verify my contacts present in search drop down
	    log.info("INFO:Verifying my contacts present in search dropdown");
	    Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.Directorycontact),
				"ERROR:my contacts option is not present");
	    
	    //Verify Guests present in search drop down
	    log.info("INFO:Verifying Guests present in search dropdown");
	    Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.Guests),
				"ERROR:Guests option is not present");
	    	    
	    //Verify Organizations present in search drop down
	    log.info("INFO:Verifying directory present in search dropdown");
	    Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.Organizations),
				"ERROR:Organizations option is not present");
	    
	    //Verify All Content present in search drop down
	    log.info("INFO:Verifying All Content present in search dropdown");
	    Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.AllContent),
				"ERROR:All Content option is not present");
	    
	    //Verify advanced option present in search drop down
	    log.info("INFO:Verifying advanced option present in search dropdown");
	    Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.Advanced),
				"ERROR:Organizations option is not present");
		
	}

	@Override
	public String getProfileUrl() {
		return Data.getData().userProfilePageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", driver.getCurrentUrl().split("userid=")[1].split("&")[0]);
	}

	@Override
	public void getDirectoryPageUrl(User testUser) {
		//Verify My organization directory page is opened with correct URL:
			log.info("INFO:My organization directory page is opened with correct URL");
			fluentWaitTextPresent("Looking For an Expert?");
			Assert.assertEquals(driver.getCurrentUrl(), Data.getData().anotheruserOrgDirectoryUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("SEARCHUSER", testUser.getDisplayName().replaceAll(" ", "%20")),		
				     "ERROR:My organization directory is opened with incorrect URL"); 
	}

	@Override
	public void VerifyDirectoryPageText() {
		//Verify browser tab has correct text Directory - Profiles
	    log.info("INFO:Browser tab has correct text Directory - Profiles");
	    Assert.assertEquals(driver.getTitle(), "Directory - Profiles",
	  		     "ERROR: Browser tab has incorrect text for Directory - Profiles");
		
	}
}
