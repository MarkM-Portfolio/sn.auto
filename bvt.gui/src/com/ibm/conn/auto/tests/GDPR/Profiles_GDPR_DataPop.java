package com.ibm.conn.auto.tests.GDPR;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.ICBaseUI;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.lconn.automation.framework.services.common.URLConstants;

@Deprecated //The GDPR component is obsolete now, hence this class has been deprecated
public class Profiles_GDPR_DataPop extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Profiles_GDPR_DataPop.class);
	private TestConfigCustom cfg; ICBaseUI ui;
	private HomepageUI hpUI;
	private ProfilesUI profilesUI;
	private String serverURL;
	private User testUser1, testUser2;
	private APIProfilesHandler profilesAPIUser1;
	

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		hpUI = HomepageUI.getGui(cfg.getProductName(),driver);
		profilesUI = ProfilesUI.getGui(cfg.getProductName(), driver);

		//Load Users		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser2 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
						
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		URLConstants.setServerURL(serverURL);
					
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		profilesAPIUser1 = new APIProfilesHandler(serverURL, testUser1.getEmail(), testUser1.getPassword());
				
		
	}
			
		/**
		*<ul>
		*<li><B>Info:</B> Data Population: Post Recent Updates Entry</li>
		*<li><B>Step:</B> UserA posts a recent updates entry via API</li>
		*</ul>
		*/
		@Test(groups = {"regression","regressioncloud"}, enabled=false)
		public void userAPostsMessageToRecentUpdates() {
			
			String testName = profilesUI.startTest();
			
			String statusMessage = "GDPR Profiles data pop - " + testName + Helper.genDateBasedRandVal(); 

			log.info("INFO: " + testUser1.getDisplayName() + " posts a message to Recent Updates using API method");
			profilesAPIUser1.postStatusUpdate(statusMessage);
			
			profilesUI.endTest();
		
	}

		
		/**
		*<ul>
		*<li><B>Info:</B> Data Population: UserB Reposts Status Update Entry Posted By UserA</li>
		*<li><B>Step:</B> UserA posts a status updates entry via API</li>
		*<li><B>Step:</B> UserB reposts the status updates entry</li>
		*</ul>
		*/
		@Test(groups = {"regression","regressioncloud"}, enabled=false)
		public void userBRepostsEntryPostedByUserA() {
			
			String testName = hpUI.startTest();
			
			String statusMessage = "GDPR Profiles data pop - " + testName + Helper.genDateBasedRandVal(); 
		
			log.info("INFO: " + testUser1.getDisplayName() + " posts a recent updates message using API method");
			profilesAPIUser1.postStatusUpdate(statusMessage);
			
			log.info("INFO: Log into Profiles as UserB " + testUser2.getDisplayName());
			hpUI.loadComponent(Data.getData().ComponentHomepage);
			hpUI.login(testUser2);
			
			log.info("INFO: Click on the Discover tab");
			hpUI.clickLinkWait(HomepageUIConstants.DiscoverTab);
				
			log.info("INFO: UserB reposts UserA's status update entry");
			hpUI.moveToClick(HomepageUI.getStatusUpdateMesage(statusMessage), HomepageUIConstants.RepostAction);

			log.info("INFO: Verify that the status update was successfully reposted");
			hpUI.fluentWaitTextPresent(Data.getData().RepostedUpdateMessage);
					
			log.info("INFO: Logout UserB: " + testUser2.getDisplayName());
			hpUI.logout();
			hpUI.close(cfg);

			hpUI.endTest();
		}
		
		/**
		*<ul>
		*<li><B>Info:</B> Data Population: UserA Adds a Tag to Their Own Profile</li>
		*<li><B>Step:</B> UserA logs into Profiles</li>
		*<li><B>Step:</B> UserA adds a tag to their profile</li>
		*</ul>
		*/
		@Test(groups={"regression","regressioncloud"}, enabled=false)
		public void userAAddsTagToOwnProfile() {
						
			profilesUI.startTest();

			log.info("INFO: Log into Profiles as User1: " + testUser1.getDisplayName());
			profilesUI.loadComponent(Data.getData().ComponentProfiles);
			profilesUI.login(testUser1);
			
			log.info("INFO: Navigate to the 'My Profile' page");
			profilesUI.fluentWaitPresentWithRefresh(ProfilesUIConstants.People);

			log.info("INFO: Enter new tag and Click Enter");
			profilesUI.addProfileTagUsingKeyboard(Data.getData().profileTag + Helper.genDateBasedRand());
			
			log.info("INFO: Logout UserA: " + testUser1.getDisplayName());
			profilesUI.logout();
			profilesUI.close(cfg);

			profilesUI.endTest();
			
		}
		
		/**
		*<ul>
		*<li><B>Info:</B>Data Population: UserA Follows UserB</li>
		*<li><B>Step:</B> UserA logs into Profiles </li>
		*<li><B>Step:</B> UserA does a user search on UserB</li>
		*<li><B>Step:</B> UserA opens UserB's profile</li>
		*<li><B>Step:</B> UserA follows UserB</li>
		*</ul>
		*/
		@Test(groups = {"regression", "regressioncloud"}, enabled=false)
		public void userAFollowsUserB(){
			
			profilesUI.startTest();
	        
			log.info("INFO: Log into Profiles as UserA: " + testUser1.getDisplayName());
			profilesUI.loadComponent(Data.getData().ComponentProfiles);
			profilesUI.login(testUser1);

			log.info("INFO: Search for UserB: " + testUser2.getDisplayName());
			profilesUI.openAnotherUserProfile(testUser2);
			
			log.info("INFO: If the 'Follow' link appears, click it; otherwise, do nothing.");
			if(!profilesUI.isElementPresent(ProfilesUIConstants.FollowPerson)){
				log.info("INFO: " + testUser2.getDisplayName() + " is already being followed");
			}else {
				log.info("INFO: Follow link appears - click link");
				profilesUI.clickLinkWait(ProfilesUIConstants.FollowPerson);
			}
			
			log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
			profilesUI.logout();
			profilesUI.close(cfg);						
			
			profilesUI.endTest();
		}
		
		/**
		*<ul>
		*<li><B>Info:</B> Data Population: Recent Update that Mentions UserB</li>
		*<li><B>Step:</B> UserA posts a recent updates entry that mentions UserB</li>
		*</ul>
		*/
		@Test(groups = {"regression","regressioncloud"}, enabled=false)
		public void userAPostsRecentUpdateEntryThatMentionsUserB() {
			
			profilesUI.startTest();
			
			APIProfilesHandler user2= new APIProfilesHandler(serverURL, testUser2.getEmail(), testUser2.getPassword());
						
			Mentions mentions = new Mentions.Builder(testUser2, user2.getUUID())
			                                .browserURL(serverURL)
			                                .beforeMentionText("GDPR recent updates data pop ")
			                                .afterMentionText("check this out")
			                                .build();

			log.info("INFO: Log into Homepage as UserA " + testUser1.getDisplayName());
			hpUI.loadComponent(Data.getData().ComponentProfiles);
			hpUI.login(testUser1);

			log.info("INFO: Create status update entry with an @mention about UserB: " + testUser2.getEmail());			
			profilesAPIUser1.addMentionsStatusUpdate(mentions);
			
			log.info("INFO: Logout as UserA: " + testUser1.getDisplayName());
			profilesUI.logout();
			profilesUI.close(cfg);
						
			profilesUI.endTest();		
		}
		
}
