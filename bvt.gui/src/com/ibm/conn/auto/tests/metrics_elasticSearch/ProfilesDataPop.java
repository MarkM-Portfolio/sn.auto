package com.ibm.conn.auto.tests.metrics_elasticSearch;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

public class ProfilesDataPop extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(ProfilesDataPop.class);
	private TestConfigCustom cfg;
	private ProfilesUI ui;
	private String serverURL;
	private User testUser1, testUser2, testUser3, testUser4;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		
		//Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		testUser3 = cfg.getUserAllocator().getUser();
		testUser4 = cfg.getUserAllocator().getUser();
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		URLConstants.setServerURL(serverURL);
		
}
	/**
	 *<ul>
	 *<li><B>Info:</B> Data Population: Profiles - Post a Status Updates Message</li>
	 *<li><B>Step:</B> Go to My Profile > Recent Updates tab</li>
	 *<li><B>Step:</B> Enter a status update message and click Post</li>
	 *</ul>
	 */
	@Test(groups = { "regression", "regressioncloud"})
	public void profilesStatusUpdateMessage() {
		
		ui.startTest();		
		
		log.info("INFO: Log into Profiles as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);		
		ui.login(testUser1);
		
		log.info("INFO: Load the My Profile view");
		ui.myProfileView();
		
		log.info("INFO: Enter a status update message & Post it");
		ui.updateProfileStatus(Data.getData().ProfileStatusUpdate);
		
		log.info("INFO: Verify the alert message displays message was posted successfully");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().postSuccessMessage), 
				"Alert stating the message was successfully posted was not found");
		
		ui.endTest();
		
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - Edit User Profile</li>
	*<li><B>Step:</B> Navigate to the My Profile tab</li>
	*<li><B>Step:</B> Click the "Edit My Profile" button</li>
	*<li><B>Step:</B> Update various user profile fields & Save</li>
	*</ul>
	*/
	@Test(groups = { "regression", "regressioncloud" })
	public void editUserProfile(){
		
		String uniqueId = Helper.genDateBasedRandVal();
		
		ui.startTest();
        
		log.info("INFO: Log into Profiles as: " + testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to the 'My Profile' view");
		ui.myProfileView();

		log.info("INFO: Click on the 'Edit Profile' button");
		ui.editMyProfile();

		log.info("INFO: Update the profile");
		ui.updateProfile(uniqueId);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Data Population - Follow User</li>
	*<li><B>Info:</B> (1) user follows UserA & (2) users follow UserB
	*<li><B>Step:</B> Log in as UserB & follow UserA</li>
	*<li><B>Step:</B> Log in as UserC & follow UserB</li>
	*<li><B>Step:</B> Log in as UserD & follow UserB</li>
	*</ul>
	*/
	@Test(groups = { "regression", "regressioncloud" } , enabled=false )
	public void followUsers(){
		
		ui.startTest();
        
		log.info("INFO: Log into Profiles as: " + testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser2);

		log.info("INFO: Search for: " + testUser1.getDisplayName());
		ui.openAnotherUserProfile(testUser1);

		log.info("INFO: Click on the Follow link");
		ui.clickLinkWait(ProfilesUIConstants.FollowPerson);
		
		log.info("INFO: Logout as: " + testUser2.getDisplayName());
		ui.logout();
		
		log.info("INFO: Login as: " + testUser3.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		ui.login(testUser3);
		
		log.info("INFO: Search for: " + testUser2.getDisplayName());
		ui.openAnotherUserProfile(testUser2);

		log.info("INFO: Click on the Follow link");
		ui.clickLinkWait(ProfilesUIConstants.FollowPerson);
		
		log.info("INFO: Logout as: " + testUser3.getDisplayName());
		ui.logout();
		
		log.info("INFO: Login as: " + testUser4.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles, true);
		ui.login(testUser4);
		
		log.info("INFO: Search for: " + testUser2.getDisplayName());
		ui.openAnotherUserProfile(testUser2);

		log.info("INFO: Click on the Follow link");
		ui.clickLinkWait(ProfilesUIConstants.FollowPerson);
		
		ui.endTest();
	}
}
	
