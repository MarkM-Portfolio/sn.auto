package com.ibm.conn.auto.webui.production;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseContact;
import com.ibm.conn.auto.appobjects.base.BaseProfile;
import com.ibm.conn.auto.webui.ProfilesUI;

public class ProfilesUIProduction extends ProfilesUI {
	
	private static Logger log = LoggerFactory.getLogger(ProfilesUIProduction.class);

	
	public static String inviteToMyNetwork = "css=input#inputProfileActionAddColleague.lotusBtn";
	public static String acceptToMyNetwork  = "css=input#profileView_acceptInvitationButton.lotusBtn";
	
	public ProfilesUIProduction(RCLocationExecutor driver) {
		super(driver);
	}

	@Override
	public void myProfileView(){
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub

	}

	@Override
	public void updateProfile(String uniqueId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateICPhoneNumber() {
		// TODO Auto-generated method stub

	}

	@Override
	public void verifyUserProfile(String uniqueId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void profilesAddATag(User testUser, String tag) {
		// TODO Auto-generated method stub

	}

	@Override
	public void checkPageTitle() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getMyProfileSelector() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void gotoRecentUpdates() {
		// TODO Auto-generated method stub

	}

	@Override
	public void verifyUpdatesTextArea() {
		log.info("Verify Update text area precent.");
		if(driver.isElementPresent("css=*[title='"+ ProfilesUIConstants.UpdatesFrameTitle+"']")) {
			switchToFrameByTitle(ProfilesUIConstants.UpdatesFrameTitle);
			Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.UpdatesTextBox),
							"Update status text area not precent.");
			switchToTopFrame();
		} else if(driver.isElementPresent(ProfilesUIConstants.UpdatesStatusFrame)) {
			driver.switchToFrame().selectSingleFrameBySelector(ProfilesUIConstants.UpdatesStatusFrame);
			Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.UpdatesTextBox),
							"Update status text area not precent.");
			switchToTopFrame();
		} else {
			Assert.assertTrue(driver.isElementPresent(ProfilesUIConstants.UpdatesTextBox),
					"Update status text area not precent.");
		}
	}

	@Override
	public void gotoContactInformation() {
		// TODO Auto-generated method stub

	}

	@Override
	public void verifyContactInfomationText() {
		// TODO Auto-generated method stub

	}

	@Override
	public void gotoAboutMe() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateProfileStatus(String status) {
		// TODO Auto-generated method stub

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

	@Override
	public void openProfileBusinessVcard() {
		// TODO Auto-generated method stub
		
	}

	@Override
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
	public void verifyProfileDataInBusinessCard(User testUser1,
			String uniqueId, String orgName) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String openViewLinkUrl() {
		// TODO Auto-generated method stub
		return null;
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

}
