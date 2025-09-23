package com.ibm.conn.auto.tests.commonui.regression;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;

public class Homepage extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(Homepage.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	private String serverURL = "";
	private APIProfilesHandler profilesAPI;
	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		testUser2 = cfg.getUserAllocator().getUser(this);
	
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		profilesAPI = new APIProfilesHandler(serverURL, testUser1.getEmail(), testUser1.getPassword());
		
	}
	

	/**
	* addStatusUpdateWithMention_AS() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Post a status update with a mention in the sharebox</B></li>
	*<li><B>Verify: Verify that the status update added successfully message appears.</B></li>
	*<li><B>Verify: Verify that the status update appears dynamically in the Activity Stream.</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Homepage</a></li>
	*</ul>
	*/
	@Test(groups = {"regression", "regressioncloud"})
	public void addStatusUpdateWithMention_AS(){
		
		String afterText = Helper.genStrongRand();
				
		ui.startTest();

		//Load Homepage component and log in
		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to post status update with mention");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);

		//Navigate to Status Updates
		log.info("INFO: Navigate to Status Updates");
		ui.gotoStatusUpdates();

		//Locate the Feed for these entries link to ensure the page is loaded
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		
		//Types the user and selects from typeahead
		log.info("INFO: Types the user and selects from typeahead");
		ui.postAtMentionUserUpdate(testUser2, afterText);

		//Post the status update with the mention
		log.info("INFO: Post the status update with the mention");
		ui.clickLinkWait(HomepageUIConstants.PostStatusOld);
		
		//Verify that the update posted correctly
		log.info("INFO: Verify that the update posted correctly");
		ui.fluentWaitTextPresent(Data.getData().postSuccessMessage);
		
		ui.endTest();		
	}

	
	/**
	* postSUComment_EE() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Post a status update using API</B></li>
	*<li><B>Step: Post a comment on the status update in the EE</B></li>
	*<li><B>Verify: Verify that the comment is added successfully in the EE.</B></li>
	*<li><B>Verify: Verify that the comment is added successfully in the AS.</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Homepage</a></li>
	*</ul>
	*/
	@Test(groups = {"regression", "regressioncloud"})
	public void postSUComment_EE(){

		String statusMessage = Data.getData().UpdateStatus + Helper.genMonthDateBasedRandVal();
		String statusComment = Data.getData().buttonSend + Data.getData().specialCharacter;

		ui.startTest();

		//Load Homepage component and log in
		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to post status update and verify successful post");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);

		//Post a status update using API
		log.info("INFO: " + testUser1.getDisplayName() + " posting a status update using API method");
		profilesAPI.postStatusUpdate(statusMessage);

		//Navigate to I'm following view
		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();

		//Locate the status message and open it in EE
		log.info("INFO: " + testUser1.getDisplayName() + " Locates the status message and open it in EE");
		ui.filterNewsItemOpenEE(statusMessage);
	
		//Post a comment in EE
		log.info("INFO: Post a comment in EE");
		ui.addEEComment(statusComment);
		
		//Verify the status update is displayed in the EE
		log.info("INFO: Verify the status update is displayed in the EE");
		Assert.assertTrue(ui.fluentWaitTextPresent(statusMessage),
						 "ERROR: Status update is not displayed in the EE");

		//Verify the comment is displayed in the EE
		log.info("INFO: Verify the comment is displayed in the EE");
		Assert.assertTrue(ui.fluentWaitTextPresent(statusComment),
						 "ERROR: Comment is not displayed in the EE");

		//Go to the Activity Stream and verify the comment appears
		log.info("INFO: " + testUser1.getDisplayName() + " go to the Activity Stream and verify the comment appears");
		ui.switchToTopFrame();

		//Verify the status update is displayed in the I'm Following view / All filter
		log.info("INFO: Verify the status update is displayed in the I'm Following view / All filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(statusMessage),
						 "ERROR: Status update is not displayed in the I'm Following view / All filter");

		//Verify the comment is displayed in the I'm Following view / All filter
		log.info("INFO: Verify the comment is displayed in the I'm Following view / All filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(statusComment),
						 "ERROR: Comment is not displayed in the I'm Following view / All filter");

		//Filter by Status Updates
		log.info("INFO: Filter by Status Updates");
		ui.filterBy(HomepageUIConstants.FilterSU);

		//Clicking the 'Show more' link to make the test case more robust by ensuring the news story has NOT been pushed off the page
		ui.clickIfVisible(HomepageUIConstants.ShowMore);

		//Verify the status update is displayed in the I'm Following / Status Updates filter
		log.info("INFO: Verify the status update is displayed in the I'm Following / Status Updates filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(statusMessage),
						 "ERROR: Status update is not displayed in the I'm Following / Status Updates filter");

		//Verify the comment is displayed in the I'm Following view / Status Updates filter
		log.info("INFO: Verify the comment is displayed in the I'm Following view / Status Updates filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(statusComment),
						 "ERROR: Comment is not displayed in the I'm Following view / Status Updates filter");
	
		ui.endTest();
	}

}
