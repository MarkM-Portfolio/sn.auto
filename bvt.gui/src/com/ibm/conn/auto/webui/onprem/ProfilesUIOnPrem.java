package com.ibm.conn.auto.webui.onprem;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseContact;
import com.ibm.conn.auto.appobjects.base.BaseProfile;
import com.ibm.conn.auto.appobjects.base.BaseProfile.profileInfo;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.menu.MyContacts_LeftNav_Menu;
import com.ibm.conn.auto.webui.ProfilesUI;

public class ProfilesUIOnPrem extends ProfilesUI {
	
	public static final String MyProfile = "css=a[id='aProfileHeader_MyProfile']";
	public String profileUserCancel = "css=input[name='cancelButton']";
	
	//tabs
	public String RecentUpdates = "css=a[class='_linkHolder lotusLeft']:contains(Recent Updates)";
	public String ContactInfo = "css=a[class='_linkHolder lotusLeft']:contains(Contact Information)";
	public String AboutMe = "css=a[class='_linkHolder lotusLeft']:contains(Background)";

	
	public static String inviteToMyNetwork = "css=button[id='btn_actn__personCardAddAsMyColleagues']";
	public static String acceptToMyNetwork = "css=button#btn_actn__personCardAcceptInv";
	public static String BusinessCardVcard = "css=div#businessCardContent > div.vcard.lotusHeading.lotusLeft > a";
	public static String BusinessCardWindow = "css=div#cardDiv";
	public static String VcardProfileLink = "css=tr#cardHeader a:contains(Profile)";
	public static String VcardFilesLink = "css=tr#cardHeader a:contains('Files')";
	public static String VcardWikisLink = "css=tr#cardHeader a:contains('Wikis')";
	public static String VcardCommunitiesLink = "css=tr#cardHeader a:contains('Communities')";
	public static String VcardBlogsLink = "css=tr#cardHeader a:contains('Blogs')";
	public static String VcardBookmarksLink = "css=tr#cardHeader a:contains('Bookmarks')";
	public static String VcardForumsLink = "css=tr#cardHeader a:contains('Forums')";
	public static String VcardActivitiesLink = "css=tr#cardHeader a:contains('Activities')";
	public static String SendEmailLink = "css=div.lotusPersonActions a.email";
	public static String MoreActionsSendEmailLink = "css=li.lotusSendMail a.email";
	public static String MoreActionsDownloadVcardLink = "css=tr#cardFooter a:contains('Download vCard')";
	public static String MoreActionsLink = "css=a:contains(More Actions)";
	public static String VcardBusinessCardDetails = "css=tr#cardBody";
	public static String BusinesscardActions = "css=div#businessCardActions";
	
	TestConfiguration testConfig = cfg.getTestConfig();
	
	public ProfilesUIOnPrem(RCLocationExecutor driver) {
		super(driver);
	}

	@Override
	protected String getMyProfileSelector() {
		return MyProfile;
	}

	@Override
	public String getInviteToMyNetwork(){
		return inviteToMyNetwork;
	}
	
	@Override
	public String getAcceptInviteToMyNetwork(){
		return acceptToMyNetwork;
	}
	
	@Override
	public String getUserInviteMessageInFollowingView(){
		// TODO Auto-generated constructor stub
		return "";
	}
	
	@Override
	public void gotoMyNetwork() {
		this.clickLink(ProfilesUIConstants.MyNetwork);
	}
	
	@Override
	public void createContact(BaseContact contact){
		log.info("INFO: Contacts not supported on Prem");
	}

	@Override
	public void myProfileView(){
		//open the My Profile view
		clickLink(MyProfile);
		if(!cfg.getUseNewUI()) {
			waitForElementVisibleWd(createByFromSizzle(BusinesscardActions), 5);
		}
	}
		
	@Override
	public void editMyProfile(){
		log.info("INFO: Click on Edit My Profile Button");
		if (!cfg.getUseNewUI()) {
			this.clickButton("Edit My Profile");
		} else {
			this.clickButton("Edit Profile");
		}
	}
	@Override
	public void updateProfile(String uniqueId){
		String saveButton="";

		waitForPageLoaded(driver);
		
		//Update a users Profile
		clearText(ProfilesUIConstants.EditProfileBuilding);
		typeText(ProfilesUIConstants.EditProfileBuilding, Data.getData().profBuilding + uniqueId);
		// Floor is not applicable for HCL MT
		if (testConfig.serverIsMT() || testConfig.serverIsMTAsStandalone()) {
			clearText(ProfilesUIConstants.EditProfileFloor);
			typeText(ProfilesUIConstants.EditProfileFloor, Data.getData().profFloor + uniqueId);
		}
		scrollToElementWithJavaScriptWd(By.cssSelector(ProfilesUIConstants.floorLabel));
		clearText(ProfilesUIConstants.EditProfileOffice);
		typeText(ProfilesUIConstants.EditProfileOffice, Data.getData().profOffice + uniqueId);
		clearText(ProfilesUIConstants.profileUserTele);
		typeText(ProfilesUIConstants.profileUserTele, Data.getData().profOfficePhone + uniqueId);
		clearText(ProfilesUIConstants.EditIpTelephoneNumber);
		typeText(ProfilesUIConstants.EditIpTelephoneNumber, Data.getData().profIP_phone + uniqueId);
		clearText(ProfilesUIConstants.EditProfileMobileNo);
		typeText(ProfilesUIConstants.EditProfileMobileNo, Data.getData().profMobile + uniqueId);
		clearText(ProfilesUIConstants.EditPagerNo);
		typeText(ProfilesUIConstants.EditPagerNo, Data.getData().profPager + uniqueId);
		clearText(ProfilesUIConstants.EditAlternateEmail);
		typeText(ProfilesUIConstants.EditAlternateEmail, Data.getData().profAltEmail + uniqueId);
		scrollToElementWithJavaScriptWd(createByFromSizzle(ProfilesUIConstants.EditAlternateEmail));
		clearText(ProfilesUIConstants.EditBlogURL);
		typeText(ProfilesUIConstants.EditBlogURL, Data.getData().profBlogLink + uniqueId);
		clearText(ProfilesUIConstants.EditJobResponsibility);
		typeText(ProfilesUIConstants.EditJobResponsibility, Data.getData().profJobDescription + uniqueId);
		//save the update
		if(cfg.getUseNewUI())
		{
			saveButton="Save & Close";
		}
		else
		{
			saveButton="Save and Close";

		}
		clickButton(saveButton);
	}
	
	public void verifyUserProfile(String uniqueId){

		fluentWaitPresent(ProfilesUIConstants.EditProfileBuilding);

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.EditProfileBuilding).getAttribute("value").equals(Data.getData().profBuilding + uniqueId),
							"VerifyTextFieldBuilding is not equal to: " + Data.getData().profBuilding + uniqueId);
		// Floor is not applicable for HCL MT
		if (testConfig.serverIsMT() || testConfig.serverIsMTAsStandalone()) {
			Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.EditProfileFloor).getAttribute("value").equals(Data.getData().profFloor + uniqueId),
					"VerifyTextFieldFloor is not equal to: " + Data.getData().profFloor + uniqueId);
		}

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.EditProfileOffice).getAttribute("value").equals(Data.getData().profOffice + uniqueId),
							"VerifyTextFieldOffice is not equal to: " + Data.getData().profOffice + uniqueId);

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.EditIpTelephoneNumber).getAttribute("value").equals(Data.getData().profIP_phone + uniqueId),
							"VerifyTextFieldIpTelephoneNo is not equal to: " + Data.getData().profIP_phone + uniqueId);

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.EditProfileMobileNo).getAttribute("value").equals(Data.getData().profMobile + uniqueId),
							"VerifyTextFieldMobileNo is not equal to: " + Data.getData().profMobile + uniqueId);

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.EditPagerNo).getAttribute("value").equals(Data.getData().profPager + uniqueId),
							"VerifyTextFieldPagerNo is not equal to: " + Data.getData().profPager + uniqueId);

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.EditAlternateEmail).getAttribute("value").equals(Data.getData().profAltEmail + uniqueId),
							"VerifyTextFieldAlternateEmail is not equal to: " + Data.getData().profAltEmail + uniqueId);

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.EditBlogURL).getAttribute("value").equals(Data.getData().profBlogLink + uniqueId),
							"VerifyTextFieldBlogURL is not equal to: " + Data.getData().profBlogLink + uniqueId);

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.EditJobResponsibility).getAttribute("value").equals(Data.getData().profJobDescription + uniqueId),
							"VerifyTextFieldJobResponsibility is not equal to: " + Data.getData().profJobDescription + uniqueId);
		
		scrollToElementWithJavaScriptWd(By.cssSelector(ProfilesUIConstants.editProfileUserCancel));
		driver.getFirstElement(profileUserCancel).click();
	}

	@Override
	public void profilesAddATag(User testUser, String tag){

		fluentWaitPresent(ProfilesUIConstants.ProfilesTagTypeAhead);
		typeTextWithDelay(ProfilesUIConstants.ProfilesTagTypeAhead, tag);
		driver.executeScript("window.scrollBy(2000,0)");
		clickLinkWithJavascript(ProfilesUIConstants.ProfilesAddTag);
		fluentWaitTextPresent(tag);
		if(cfg.getUseNewUI()) {
			WebElement ele = findElements(By.xpath(HomepageUIConstants.tags)).get(0);
			scrollToElementWithJavaScriptWd(ele);
		}
		clickLinkWait("//div[@id='tagCloud']/div/ul/li/a[contains(text(),'PLACEHOLDER')]".replace("PLACEHOLDER", tag.toLowerCase()));
		if (cfg.getUseNewUI()) {
			//This scroll method needs to be removed once this application handle the scroll feature on cn8ui
			//https://jira.cwp.pnp-hcl.com/browse/CNXTEST-2436
			driver.executeScript("window.scrollBy(2000,0)");
			clickLinkWd(By.xpath(ProfilesUIConstants.resultPerPageDropdown), "Click on Result per page drop down");
			Select resultPerPageDropdown = new Select(findElement(By.xpath(ProfilesUIConstants.resultPerPageDropdown)));
			resultPerPageDropdown.selectByValue("100");
		} else {
			clickLink(ProfilesUIConstants.Show100PerPage);
		}
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
		log.info("Verifying page title");
		Assert.assertEquals(driver.getTitle(), "My Profile",
						    "Page title 'My Profile' not found on page.");
		log.info("Found Page title 'My Profile'");
	}
	
	@Override
	public void verifyUpdatesTextArea() {
		log.info("Verify Status Update text area is present");
		Assert.assertTrue(driver.isElementPresent("css=div.lotusStreamUpdate"),
							"Update status text area not found");
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
		Assert.assertTrue(driver.isTextPresent("Office email"), 
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
	public void updateProfileStatus(String statusMessage) {
		log.info("Updating profile status");
		
		//Enter and Save a status update
		if(driver.isElementPresent(ProfilesUIConstants.UpdatesTextBox)){
			typeText(ProfilesUIConstants.UpdatesTextBox, statusMessage);
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
			driver.getSingleElement(BaseUIConstants.StatusUpdate_Body).type(statusMessage);
			
			log.info("INFO: Returning to top Frame to click 'Post' button");
			driver.switchToFrame().returnToTopFrame();
		}
		
		clickLink(ProfilesUIConstants.PostStatus);
		fluentWaitTextPresent(Data.getData().postSuccessMessage);
		log.info("Posted status update '"+statusMessage+"'");
	}

	@Override
	public void updateICPhoneNumber(){
		log.info("INFO: IC Profile not supported on Prem");
	}
	
	/**
	 * Get contact handle in My contacts -> Following
	 * @param userName - Profile User
	 * @return Element - Contact Widget
	 */
	@Override
	public Element getContactInFollowingView(String userName){
		// TODO Auto-generated method stub
		return null;
	}
	
	/*
	 * Open Profile BussinessCard 
	 * @Param None 
	 * @return None
	 */
	public void openProfileBusinessVcard() {
		log.info("INFO: open Business card vcard by hovering on User");
		this.waitForPageLoaded(driver);
		this.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		driver.getSingleElement(BusinessCardVcard).hover();
		driver.getSingleElement(BusinessCardWindow).hover();
	}

	@Override
	public void createAnActivity(String uniqueId) {
		// TODO Auto-generated method stub
		
	}

	/* To update profile information
	 * @param - BaseProfile profile
	 * @return None
	 */
	public void editProfile(BaseProfile profile) {
		
		log.info("Starting to edit input fields");
		editMyProfile();
		Set<profileInfo> profiles = profile.getEdits();
		for(profileInfo prof : profiles)
		{
			switch (prof){
			case BUILDING:
				//To edit Building field
				log.info("INFO: To edit Building field");
				clearText(ProfilesUIConstants.EditProfileBuilding);
				typeText(ProfilesUIConstants.EditProfileBuilding, profile.getBuilding());
				break;
			case FLOOR:
				//To edit Floor field
				log.info("INFO: To edit Floor field");
				clearText(ProfilesUIConstants.EditProfileFloor);
				typeText(ProfilesUIConstants.EditProfileFloor, profile.getFloor());
				break;
			case OFFICE:
				//To edit Office field
				log.info("INFO: To edit Office field");
				clearText(ProfilesUIConstants.EditProfileOffice);
				typeText(ProfilesUIConstants.EditProfileOffice, profile.getOffice());
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
			case IPTELEPHONE:
				//To edit IP Telephone number
				log.info("INFO: To edit IP Telephone number");
				clearText(ProfilesUIConstants.EditIpTelephoneNumber);
				typeText(ProfilesUIConstants.EditIpTelephoneNumber, profile.getIPTelephone());
				break;
			case PAGER:
				//To edit Pager number
				log.info("INFO: To edit Pager number");
				clearText(ProfilesUIConstants.EditPagerNo);
				typeText(ProfilesUIConstants.EditPagerNo, profile.getPager());
				break;
			case ALTERNATEEMAIL:
				//To edit Alternate Email field
				log.info("INFO: To edit Alternate Email field");
				clearText(ProfilesUIConstants.EditAlternateEmail);
				typeText(ProfilesUIConstants.EditAlternateEmail, profile.getAlternateEmail());
				break;
			case BLOG:
				//To edit Blog field
				log.info("INFO: To edit Blog field");
				clearText(ProfilesUIConstants.EditBlogURL);
				typeText(ProfilesUIConstants.EditBlogURL, profile.getBlog());
				break;
			case ASSISTANT:
				//To edit Assistant field
				log.info("INFO: To edit Assistant field");
				clearText(ProfilesUIConstants.EditAssistant);
				typeTextWithDelay(ProfilesUIConstants.EditAssistant, profile.getAssistant());
				typeaheadSelection(profile.getAssistant(), ProfilesUIConstants.AssistantTypeAHead);
				break;
			case JOBTITLE:
				//To edit jobTitle field
				log.info("INFO: To edit jobTitle field");
				scrollToElementWithJavaScriptWd(createByFromSizzle(ProfilesUIConstants.profileJobTitle));
				clearText(ProfilesUIConstants.profileJobTitle);
				typeText(ProfilesUIConstants.profileJobTitle, profile.getJobTitle());
				break;
			default:
				break;
			 }
		
		}
		
		log.info("INFO: Save & Close edit profile");
		
		String saveButton = "";
		if(cfg.getUseNewUI())
		{
			saveButton="Save & Close";
		}
		else
		{
			saveButton="Save and Close";

		}
		clickButton(saveButton);
		
	}
	
	@Override 
	public boolean isHelpBannerTextPresent() {
		return this.isTextPresent("Your profile is visible to everyone in the organization. Use it to define who you are and what you do.\n" +
				"Post a message to share an idea or problem that you are working on and get immediate feedback about it from your colleagues.\n" +
				"Click \"Edit My Profile\" to update your contact information, upload a photo, and add information about your work and expertise.");
	}
	
	 /* 
	 *  open another User's profile URL
	 *  return get UserProfile URL
	 */
	public String getUserProfileUrl()
	{
		return Data.getData().myProfilePageUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("KEY", driver.getCurrentUrl().split("key=")[1]);
		
	}

	@Override
	/* 
	 *  open another User's profile URL
	 *  return get networkViewLink  URL
	 */
	public String getMyNetworkUrl() {
		return Data.getData().myNetworkUrl.replaceAll("SERVER", cfg.getServerURL());
	}
	@Override
	/* 
	 *  open another User's profile URL
	 *  return get networkUser  URL
	 */
	public String getNetworkUserUrl()
	{
	  return Data.getData().networkUserUrl.replaceAll("SERVER", cfg.getServerURL());	
	}
	
	/* 
	 *  openBusinessCardOfUser()
	 */
	@Override
	public void openBusinessCardOfUser(User testUser)
	{
	   //Not Implemented
	}
	
	/*To verify following links are present in Business Card(Files, Profile, Wikis, Communities, Blogs, Bookmarks, Forums, Activities and More Actions -> Send Email, Download Vcard)
	 * @param - None
	 * @return - None
	 */
	public void verifyLinksInBusinessCard() {
		log.info("INFO: To verify following links are present in Business Card(Files, Profile, Wikis, Communities, Blogs, Bookmarks, Forums, Activities and More Actions -> Send Email, Download Vcard)");
		
		//Profile link present
		log.info("INFO: Verify card contains Profile link");
		Assert.assertTrue(this.isElementPresent(VcardProfileLink),
						 "ERROR: Business card do not contain Profile link");
		
		//Files link present
		log.info("INFO: Verify card contains Files link");
		Assert.assertTrue(this.isElementPresent(VcardFilesLink),
						 "ERROR: Business card do not contain Files link");
		
		//Wikis link present
		log.info("INFO: Verify card contains Wikis link");
		Assert.assertTrue(this.isElementPresent(VcardWikisLink),
						 "ERROR: Business card do not contain Wikis link");
		
		//Communities link present
		log.info("INFO: Verify card contains Communities link");
		Assert.assertTrue(this.isElementPresent(VcardCommunitiesLink),
						 "ERROR: Business card do not contain Communities link");
		
		//Blogs link present
		log.info("INFO: Verify card contains Blogs link");
		Assert.assertTrue(this.isElementPresent(VcardBlogsLink),
						 "ERROR: Business card do not contain Blogs link");
				
		//Bookmarks link present
		log.info("INFO: Verify card contains Bookmarks link");
		Assert.assertTrue(this.isElementPresent(VcardBookmarksLink),
						 "ERROR: Business card do not contain Bookmarks link");
		
		//Forums link present
		log.info("INFO: Verify card contains Forums link");
		Assert.assertTrue(this.isElementPresent(VcardForumsLink),
						 "ERROR: Business card do not contain Forums link");
		
		//Activities link present
		log.info("INFO: Verify card contains Activities link");
		Assert.assertTrue(this.isElementPresent(VcardActivitiesLink),
						 "ERROR: Business card do not contain Activities link");
				
		//Send Email link present
		if (!cfg.getUseNewUI()) {
			log.info("INFO: Verify card contains Send Email link");
			Assert.assertTrue(this.isElementPresent(SendEmailLink),
					"ERROR: Business card do not contain Send Email link");
		}
				
		//More Actions link present
		if (!cfg.getUseNewUI()) {
			log.info("INFO: Verify card contains More Actions link");
			Assert.assertTrue(this.isElementPresent(MoreActionsLink),
					"ERROR: Business card do not contain More Actions link");
		}
		
		//Send Email link present
		if (!cfg.getUseNewUI()) {
			log.info("INFO: Verify card contains More Actions -> Send Email link");
			this.clickLinkWait(MoreActionsLink);
			Assert.assertTrue(this.isElementPresent(MoreActionsSendEmailLink),
					"ERROR: Business card do not contain More Actions -> SendEmail link");
		}
		
		//Download vCard link present
		if (!cfg.getUseNewUI()) {
			log.info("INFO: Verify card contains More Actions -> Download vCard link");
			Assert.assertTrue(this.isElementPresent(MoreActionsDownloadVcardLink),
					"ERROR: Business card do not contain More Actions -> Download vCard link");
		}
	}

	@Override
	public String openViewLinkUrl() {
		return Data.getData().networkViewLinkUrl.replaceAll("SERVER", cfg.getServerURL());
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
		Assert.assertTrue(bCardDetails.contains(Data.getData().profJobDescription),
						 "ERROR: Expected to find " + Data.getData().profJobDescription + uniqueId + "inside the business card details");
		
		//User's email address
		log.info("INFO: Validate User's email address is present");
		Assert.assertTrue(bCardDetails.contains(testUser1.getEmail()),
						 "ERROR: Expected to find " + testUser1.getEmail() + "inside the business card details");
		
		//User's office phone
		log.info("INFO: Validate User's office phone has been updated");
		Assert.assertTrue(bCardDetails.contains(Data.getData().profOfficePhone),
						 "ERROR: Expected to find " + Data.getData().profOfficePhone + uniqueId + "inside the business card details");
	}
	
	/**
	 *Info: Validation of recent updates filter by dropdown
	 *@param: none
	 *@return: none
	 */
	 public void recentUpdatesFilterBy() {
		   Assert.assertEquals(driver.getSingleElement(ProfilesUIConstants.recentupdatesFilterby).getText(), "All\nStatus Updates\nActivities\nBlogs\nBookmarks\nCommunities\nFiles\nForums\nLibraries\nProfiles\nWikis",
					"ERROR: The Filter is not having correct data");
	 }

	 /**
		 *Info: validating dyk help topic on prem
		 *@param: none
		 *@return: none
		 */
	 public void helptopicDYKwidget(){
		 log.info("INFO:Verifying the help topic");
		 Assert.assertTrue(fluentWaitTextPresent(Data.getData().dykHelpMsgonPrem),
					  "ERROR: help topic is not present");
	 }
	 
	 /**
	 *Info: validating userprofile page url 
	 *@param: none
	 *@return: URL
	 */
	 public String userProfilePageUrl() {
		 return Data.getData().myProfileUrl.replaceAll("SERVER", cfg.getServerURL()).replaceAll("USERID", driver.getCurrentUrl().split("userid=")[1].split("&")[0]);
			 
	 }
	 
	 /**
		 *Info: validating Person card popup in on Prem servers. 
		 *@param: none
		 *@return: none
		 */
	 public  void dykPersoncardpopup(){
		 driver.getFirstElement(ProfilesUIConstants.DYKuser).hover();
		 Assert.assertTrue(fluentWaitElementVisible(ProfilesUIConstants.PersonInfo),
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
		
		//Click on "Accept Invitation" button
		log.info("INFO: Click on Accept Invitation button");
		this.clickLink("css=a[id^='accept_link_'][title='Accept "+ inviterUser.getDisplayName() +"']");
		
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
		this.clickLink("css=a[id^='reject_link_'][title='Ignore "+ invitedUser.getDisplayName() +"']");
		
	}
	
	@Override
	public void verifySearchUserContents() {
		
		//Verify directory is the default selection in the search control
		log.info("INFO:Verifying directory is the default selection in the search control");
		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.Orgdirdefaultsearch).getText().contains("Profiles by Name"),
				"ERROR:directory search control is not present");
	 
		//Click on directory
		log.info("INFO:Clicking on directory");
	    this.clickLink(ProfilesUIConstants.Orgdirdefaultsearch);
	    
	    //Verify ProfileByName present in search dropdown
	    log.info("INFO:Verifying ProfileByName present in search dropdown");
	    Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.ProfileByName),
				"ERROR:Directory option is not present");
	    
	    //Verify ProfileByKeyword present in search dropdown
	    log.info("INFO:Verifying ProfileByKeyword present in search dropdown");
	    Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.ProfileByKeyword),
				"ERROR:Directory option is not present");
	    
	    //Verify AllContent present in search dropdown
	    log.info("INFO:Verifying AllContent present in search dropdown");
	    Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.AllContent),
				"ERROR:Directory option is not present");
	 	    
	    //Verify Advanced present in search dropdown
	    log.info("INFO:Verifying Advanced present in search dropdown");
	    Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.Advanced),
				"ERROR:Directory option is not present");
		
	}

	@Override
	public String getProfileUrl() {
		return Data.getData().networkUserUrl.replaceAll("SERVER", cfg.getServerURL());
	}

	@Override
	public void getDirectoryPageUrl(User testUser) {
		Assert.assertTrue(driver.getCurrentUrl().contains(Data.getData().searchProfileUrl.replaceAll("SERVER", cfg.getServerURL())),
				"ERROR:The page is opned with incorrect url");
	}

	@Override
	public void VerifyDirectoryPageText() {
		//verifying the Search - Profiles tab is opened
		log.info("INFO:verifying the Search - Profiles tab is opened");
		Assert.assertTrue(driver.getTitle().contains(Data.getData().searchProfileTitle),
					"ERROR:The page is opened with different tab");
		
	}	
}

