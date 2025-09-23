package com.ibm.conn.auto.webui.multi;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseContact;
import com.ibm.conn.auto.appobjects.base.BaseProfile;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.webui.ProfilesUI;

public class ProfilesUIMulti extends ProfilesUI{
	
	protected static Logger log = LoggerFactory.getLogger(ProfilesUIMulti.class);

	public static String inviteToMyNetwork = "css=button[id='btn_actn__personCardAddAsMyColleagues']";
	public static String acceptToMyNetwork = "css=button#btn_actn__personCardAcceptInv";
	public static String profCourtesyTitle = "css=input[id='courtesyTitle']";
	public static String profDeptNo = "css=input[id=deptNumber]"; 
	public static String recentUpdates = "css=a[class='_linkHolder lotusLeft']:contains('Recent Updates')";
	public static String contactInfo = "css=a[class='_linkHolder lotusLeft']:contains('Contact Information')";
	
	public ProfilesUIMulti(RCLocationExecutor driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void myProfileView(){		
		clickLink(ProfilesUIConstants.MyProfile);
		fluentWaitTextPresent("Reasons to Update Your Profile");
		
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
	public void editMyProfile() {
		log.info("INFO: Click on Edit My Profile Button");
		this.clickButton("Edit My Profile");
		
	}

	@Override
	public void updateProfile(String uniqueId) {
		log.info("INFO: Edit user Job title");
		clearText(profCourtesyTitle);
		typeText(profCourtesyTitle, Data.getData().profJobTitle + uniqueId);
		
		log.info("INFO: Edit user Department number");
		clearText(profDeptNo);
		typeText(profDeptNo, (Data.getData().profBuilding + uniqueId).substring(0, 15));
		
		log.info("INFO: Edit user office");
		clearText(ProfilesUIConstants.EditProfileOffice);
		typeText(ProfilesUIConstants.EditProfileOffice, Data.getData().profOffice + uniqueId);
		
		log.info("INFO: Edit user Telephone number");
		clearText(ProfilesUIConstants.profileUserTele);
		typeText(ProfilesUIConstants.profileUserTele, Data.getData().profTelephone + uniqueId);
		
		log.info("INFO: Edit user Mobile number");
		clearText(ProfilesUIConstants.EditProfileMobileNo);
		typeText(ProfilesUIConstants.EditProfileMobileNo, Data.getData().profMobile + uniqueId);
		
		log.info("INFO: Edit user Fax number");
		clearText(ProfilesUIConstants.profileUserFax);
		typeText(ProfilesUIConstants.profileUserFax, Data.getData().profFax + uniqueId);

		log.info("INFO: Click 'Save and Close' button");
		clickLinkWait("css=input[value='Save and Close']");
	}

	@Override
	public void updateICPhoneNumber() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void verifyUserProfile(String uniqueId) {
		fluentWaitPresent(profCourtesyTitle);
		
		Assert.assertTrue(driver.getSingleElement(profCourtesyTitle).getAttribute("value").equals(Data.getData().profJobTitle + uniqueId), 
							"VerifyTextFieldCourtesyTitle is not equal to: " + Data.getData().profJobTitle + uniqueId);

		Assert.assertTrue(driver.getSingleElement(profDeptNo).getAttribute("value").equals((Data.getData().profBuilding + uniqueId).substring(0, 15)), 
							"VerifyTextFieldDepartmentNo is not equal to: " + Data.getData().profBuilding + uniqueId);

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.EditProfileOffice).getAttribute("value").equals(Data.getData().profOffice + uniqueId),
							"VerifyTextFieldOffice is not equal to: " + Data.getData().profOffice + uniqueId);

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.profileUserTele).getAttribute("value").equals(Data.getData().profTelephone + uniqueId),
							"VerifyTextFieldTelephoneNo is not equal to: " + Data.getData().profTelephone + uniqueId);

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.EditProfileMobileNo).getAttribute("value").equals(Data.getData().profMobile + uniqueId),
							"VerifyTextFieldMobileNo is not equal to: " + Data.getData().profMobile + uniqueId);

		Assert.assertTrue(driver.getSingleElement(ProfilesUIConstants.profileUserFax).getAttribute("value").equals(Data.getData().profFax + uniqueId),
							"VerifyTextFieldFaxNo is not equal to: " + Data.getData().profFax + uniqueId);
		
		driver.getFirstElement(ProfilesUIConstants.profileUserCancel).click();
		
	}

	@Override
	public void profilesAddATag(User testUser, String tag) {
		fluentWaitPresent(ProfilesUIConstants.ProfilesTagTypeAhead);
		typeText(ProfilesUIConstants.ProfilesTagTypeAhead, tag);
		clickLink(ProfilesUIConstants.ProfilesAddTag);
		fluentWaitTextPresent(tag);
		clickLink("//div[@id='tagCloud']/div/ul/li/a");
		clickLink(ProfilesUIConstants.Show100PerPage);
		fluentWaitTextPresent(testUser.getDisplayName());

	}

	@Override
	public void checkPageTitle() {
		log.info("Looking for text 'Profiles'");
		Assert.assertTrue(driver.isElementPresent("css=span[class='lotusText']:contains(Profiles)"), 
						   "'Profiles' not found on page.");
		log.info("Found 'Profiles' on page");
		
	}

	@Override
	protected String getMyProfileSelector() {
		return ProfilesUIConstants.MyProfile;
	}

	@Override
	public void gotoRecentUpdates() {
		log.info("Go to Recent Updates");
		fluentWaitPresent(recentUpdates);
		clickLink(recentUpdates);
		log.info("Clicked on Recent Updates");
	}

	@Override
	public void verifyUpdatesTextArea() {
		log.info("Verify Update text area present.");
		Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.UpdatesTextBox),
							"Update status text area not present.");
		switchToTopFrame();
		
	}

	@Override
	public void gotoContactInformation() {
		log.info("Go to Contact information tab");
		fluentWaitPresent(contactInfo);
		clickLink(contactInfo);
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
		log.info("INFO: The About Me tab is not supported on Multi");
		
	}

	@Override
	public void updateProfileStatus(String status) {
		log.info("Updating profile status");
		fluentWaitPresent(ProfilesUIConstants.UpdatesTextBox);
		typeText(ProfilesUIConstants.UpdatesTextBox, status);
		clickLink(ProfilesUIConstants.PostStatus);
		log.info("Posted update '"+status+"'");
		
	}

	@Override
	public void createContact(BaseContact contact) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
	}
	
	/*
	 * Create an Activity 
	 * @Param uniqueId = RandomNumber 
	 * @return None
	 */
	public void createAnActivity(String uniqueId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void editProfile(BaseProfile profile) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isHelpBannerTextPresent() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getUserProfileUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMyNetworkUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNetworkUserUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openBusinessCardOfUser(User testUser) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void verifyLinksInBusinessCard() {
		// TODO Auto-generated method stub
		
	}	
	@Override
	public String openViewLinkUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void verifyProfileDataInBusinessCard(User testUser1,
			String uniqueId, String orgName) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 *Info: Validation of recent updates filter by dropdown
	 *@param: none
	 *@return: none
	 */
	 public void recentUpdatesFilterBy() {
		//Not implemented
      }

	 /**
		 *Info: validating dyk help topic on prem
		 *@param: none
		 *@return: none
		 */
	 public void helptopicDYKwidget(){
		 //Not implemented
	 }
	 
	 /**
	 *Info: validating userprofile page url 
	 *@param: none
	 *@return: URL
	 */
	 public String userProfilePageUrl() {
		 return null;
		 //Not implemented	 
	 }
	 
	 /**
		 *Info: validating Person card popup in on Prem servers. 
		 *@param: none
		 *@return: none
		 */
	 public  void dykPersoncardpopup(){
		//Not omplemented
	 }
	 
		@Override
		public void gotoMyNetwork() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void acceptUserInvite(User inviterUser) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void ignoreUserInvite(User invitedUser) {
			// TODO Auto-generated method stub
			
		}
	
	@Override
	public void verifySearchUserContents() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getProfileUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getDirectoryPageUrl(User testUser) {
		// TODO Auto-generated method stub
	}

	@Override
	public void VerifyDirectoryPageText() {
		// TODO Auto-generated method stub
		
	}
}
