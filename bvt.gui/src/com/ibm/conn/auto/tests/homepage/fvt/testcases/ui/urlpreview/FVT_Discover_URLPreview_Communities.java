package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.urlpreview;

import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.cssBuilder.CSSBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016		                             */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty
 */

public class FVT_Discover_URLPreview_Communities extends SetUpMethodsFVT {

	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private ArrayList<Community> listOfCommunities = new ArrayList<Community>();
	private CommunitiesUI uiCo;
	private User testUser1, testUser2;
								   
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
	}
	   
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the tests
		for(Community community : listOfCommunities) {
			communitiesAPIUser1.deleteCommunity(community);
		}
	}
	
	/**
	* urlPreview_Discover_StatusUpdate_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 types a status update with valid URL in a public community</B></li>
	*<li><B>Step: testUser1 posts the status update</B></li>
	*<li><B>Step: testUser2 logs in to Homepage / Discover / All & Status Updates and verifies that the correct URL preview appears in both filters in the Discover view</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears in both filters in Discover</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/36BFDCC67EFAEED985257BCC00475304">TTT - DISCOVER - MICROBLOGGING - 00102 - PUBLIC COMMUNITY STATUS UPDATE ADDED WITH URL PREVIEW- SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_Discover_StatusUpdate_PublicCommunity() {
		
		String testName = ui.startTest();
		
		// User 1 will now create a public community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		listOfCommunities.add(publicCommunity);
		
		// User 1 will now log in to Communities UI and will post a status update with URL to the community - the URL will NOT generate a thumbnail in this case
		String statusUpdateBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateURL = Data.getData().bbcURL;
		boolean urlPreviewIsDisplayed = CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(publicCommunity, baseCommunity, ui, uiCo, testUser1, communitiesAPIUser1, statusUpdateBeforeURL, statusUpdateURL, false, false);
		
		// Verify that the URL preview widget was displayed during the posting of the status update
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsDisplayed, true);
		
		// User 1 will now return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// User 2 will now log into Connections and will navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the elements to be verified
		String addCommunityStatusEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String communityStatusUpdate = statusUpdateBeforeURL + " " + statusUpdateURL;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, addCommunityStatusEvent, statusUpdateURL);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, addCommunityStatusEvent, statusUpdateURL);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the add community status update event and the status update content are both displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{addCommunityStatusEvent, communityStatusUpdate}, filter, true);
			
			// Verify that the URL preview widget is displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget}, null, true);
			
			// Verify that the thumbnail image is NOT displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{thumbnailImage}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* urlPreview_Discover_Comment_PublicCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 types a status update with valid URL in a public community</B></li>
	*<li><B>Step: testUser1 posts the status update</B></li>
	*<li><B>Step: testUser1 navigates to Homepage and adds a comment containing a valid URL to the status update</B></li>
	*<li><B>Step: testUser2 logs in to Homepage / Discover / All & Status Updates and verifies that the correct URL preview appears in both filters in the Discover view</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears in both filters in Discover</B></li>
	*<li><B>Verify: Verify that the comment appears, but the URL Preview for the comment does not appear in either filter in Discover</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/88E62FA5D0C9477A85257BD500331B21">TTT - DISCOVER - MICROBLOGGING - 00122 - PUBLIC COMMUNITY STATUS UPDATE ADDED WITH URL PREVIEW- SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_Discover_Comment_PublicCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a public community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		listOfCommunities.add(publicCommunity);
		
		// User 1 will now log in to Communities UI and will post a status update with URL to the community - the URL will generate a thumbnail in this case
		String statusUpdateBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateURL = Data.getData().skyNewsURL;
		boolean urlPreviewIsDisplayed = CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(publicCommunity, baseCommunity, ui, uiCo, testUser1, communitiesAPIUser1, statusUpdateBeforeURL, statusUpdateURL, true, false);
		
		// Verify that the URL preview widget was displayed during the posting of the status update
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsDisplayed, true);
				
		// User 1 will now post a comment with URL to the status update
		String messagePostedEvent = Data.POSTED_A_MESSAGE_GENERIC.replace("USER", testUser1.getDisplayName());
		String commentBeforeURL = Data.getData().commonComment + Helper.genStrongRand();
		String commentURL = Data.getData().FacebookURL;
		boolean urlPreviewIsNotDisplayed = CommunityEvents.addStatusUpdateCommentWithURLUsingUI(ui, messagePostedEvent, commentBeforeURL, commentURL);
		
		// Verify that the URL preview widget was NOT displayed during the posting of the comment
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsNotDisplayed, true);
		
		// User 1 will now return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// User 2 will now log into Connections and will navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the elements to be verified
		String statusUpdateWithURL = statusUpdateBeforeURL + " " + statusUpdateURL;
		String commentOnCommunityStatusEvent = CommunityNewsStories.getCommentOnTheirOwnMessageNewsStory_User(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String commentWithURL = commentBeforeURL + " " + commentURL;
		String urlPreviewStatusUpdate = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, commentOnCommunityStatusEvent, statusUpdateURL);
		String thumbnailImageStatusUpdate = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, commentOnCommunityStatusEvent, statusUpdateURL);
		String urlPreviewComment = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, commentOnCommunityStatusEvent, commentURL);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on community status event, the community status update content and the comment content are all displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnCommunityStatusEvent, statusUpdateWithURL, commentWithURL}, filter, true);
			
			// Verify that the status update URL preview widget and thumbnail image are both displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewStatusUpdate, thumbnailImageStatusUpdate}, null, true);
			
			// Verify that the comment URL preview widget is NOT displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewComment}, null, false);
		}
		ui.endTest();
	}

	/**
	* urlPreview_Discover_StatusUpdate_ModCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 types a status update with valid URL in a moderated community</B></li>
	*<li><B>Step: testUser1 posts the status update</B></li>
	*<li><B>Step: testUser2 logs in to Homepage / Discover / All & Status Updates and verifies that the correct URL preview appears in both filters in the Discover view</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears in both filters in Discover</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8886CD3C5B84682185257BCC0047A829">TTT - DISCOVER - MICROBLOGGING - 00103 - MODERATED COMMUNITY STATUS UPDATE ADDED WITH URL PREVIEW- SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_Discover_StatusUpdate_ModCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a moderated community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.MODERATED);
		Community moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		listOfCommunities.add(moderatedCommunity);
		
		// User 1 will now log in to Communities UI and will post a status update with URL to the community - the URL will NOT generate a thumbnail in this case
		String statusUpdateBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateURL = Data.getData().bbcURL;
		boolean urlPreviewIsDisplayed = CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(moderatedCommunity, baseCommunity, ui, uiCo, testUser1, communitiesAPIUser1, statusUpdateBeforeURL, statusUpdateURL, false, false);
		
		// Verify that the URL preview widget was displayed during the posting of the status update
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsDisplayed, true);
				
		// User 1 will now return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// User 2 will now log into Connections and will navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the elements to be verified
		String addCommunityStatusEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String communityStatusUpdate = statusUpdateBeforeURL + " " + statusUpdateURL;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, addCommunityStatusEvent, statusUpdateURL);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, addCommunityStatusEvent, statusUpdateURL);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the add community status update event and the status update content are both displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{addCommunityStatusEvent, communityStatusUpdate}, filter, true);
			
			// Verify that the URL preview widget is displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget}, null, true);
			
			// Verify that the thumbnail image is NOT displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{thumbnailImage}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* urlPreview_Discover_Comment_ModCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 types a status update with valid URL in a moderated community</B></li>
	*<li><B>Step: testUser1 posts the status update</B></li>
	*<li><B>Step: testUser1 navigates to Homepage and adds a comment containing a valid URL to the status update</B></li>
	*<li><B>Step: testUser2 logs in to Homepage / Discover / All & Status Updates and verifies that the correct URL preview appears in both filters in the Discover view</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears in both filters in Discover</B></li>
	*<li><B>Verify: Verify that the comment appears, but the URL Preview for the comment does not appear in either filter in Discover</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/004C98120E0107C785257BD5003B2369">TTT - DISCOVER - MICROBLOGGING - 00123 - MODERATED COMMUNITY STATUS UPDATE ADDED WITH URL PREVIEW- SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_Discover_Comment_ModCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a moderated community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.MODERATED);
		Community moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		listOfCommunities.add(moderatedCommunity);
		
		// User 1 will now log in to Communities UI and will post a status update with URL to the community - the URL will generate a thumbnail in this case
		String statusUpdateBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateURL = Data.getData().skyNewsURL;
		boolean urlPreviewIsDisplayed = CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(moderatedCommunity, baseCommunity, ui, uiCo, testUser1, communitiesAPIUser1, statusUpdateBeforeURL, statusUpdateURL, true, false);
		
		// Verify that the URL preview widget was displayed during the posting of the status update
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsDisplayed, true);
				
		// User 1 will now post a comment with URL to the status update
		String messagePostedEvent = Data.POSTED_A_MESSAGE_GENERIC.replace("USER", testUser1.getDisplayName());
		String commentBeforeURL = Data.getData().commonComment + Helper.genStrongRand();
		String commentURL = Data.getData().FacebookURL;
		boolean urlPreviewIsNotDisplayed = CommunityEvents.addStatusUpdateCommentWithURLUsingUI(ui, messagePostedEvent, commentBeforeURL, commentURL);
		
		// Verify that the URL preview widget was NOT displayed during the posting of the comment
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsNotDisplayed, true);
		
		// User 1 will now return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// User 2 will now log into Connections and will navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the elements to be verified
		String statusUpdateWithURL = statusUpdateBeforeURL + " " + statusUpdateURL;
		String commentOnCommunityStatusEvent = CommunityNewsStories.getCommentOnTheirOwnMessageNewsStory_User(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String commentWithURL = commentBeforeURL + " " + commentURL;
		String urlPreviewStatusUpdate = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, commentOnCommunityStatusEvent, statusUpdateURL);
		String thumbnailImageStatusUpdate = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, commentOnCommunityStatusEvent, statusUpdateURL);
		String urlPreviewComment = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, commentOnCommunityStatusEvent, commentURL);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on community status event, the community status update content and the comment content are all displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnCommunityStatusEvent, statusUpdateWithURL, commentWithURL}, filter, true);
			
			// Verify that the status update URL preview widget and thumbnail image are both displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewStatusUpdate, thumbnailImageStatusUpdate}, null, true);
			
			// Verify that the comment URL preview widget is NOT displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewComment}, null, false);
		}
		ui.endTest();
	}

	/**
	* urlPreview_Discover_StatusUpdate_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 types a status update with valid URL in a private community</B></li>
	*<li><B>Step: testUser1 posts the status update</B></li>
	*<li><B>Step: testUser2 logs in to Homepage / Discover / All & Status Updates and verifies that the correct URL preview appears in both filters in the Discover view</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update do not appear in either filter in Discover</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FDE56587E31B4FEA85257BCC004852CF">TTT - DISCOVER - MICROBLOGGING - 00104 - PRIVATE COMMUNITY STATUS UPDATE ADDED WITH URL PREVIEW- SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_Discover_StatusUpdate_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a moderated community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		listOfCommunities.add(restrictedCommunity);
		
		// User 1 will now log in to Communities UI and will post a status update with URL to the community - the URL will NOT generate a thumbnail in this case
		String statusUpdateBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateURL = Data.getData().bbcURL;
		boolean urlPreviewIsDisplayed = CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(restrictedCommunity, baseCommunity, ui, uiCo, testUser1, communitiesAPIUser1, statusUpdateBeforeURL, statusUpdateURL, false, false);
		
		// Verify that the URL preview widget was displayed during the posting of the status update
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsDisplayed, true);
				
		// User 1 will now return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// User 2 will now log into Connections and will navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the elements to be verified
		String addCommunityStatusEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String communityStatusUpdate = statusUpdateBeforeURL + " " + statusUpdateURL;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, addCommunityStatusEvent, statusUpdateURL);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, addCommunityStatusEvent, statusUpdateURL);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the add community status update event and the status update content are NOT displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{addCommunityStatusEvent, communityStatusUpdate}, filter, false);
			
			// Verify that the URL preview widget and thumbnail image are NOT displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImage}, null, false);
		}
		ui.endTest();
	}
	
	/**
	* urlPreview_Discover_Comment_PrivateCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 types a status update with valid URL in a private community</B></li>
	*<li><B>Step: testUser1 posts the status update</B></li>
	*<li><B>Step: testUser1 navigates to Homepage and adds a comment containing a valid URL to the status update</B></li>
	*<li><B>Step: testUser2 logs in to Homepage / Discover / All & Status Updates and verifies that the correct URL preview appears in both filters in the Discover view</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update does not appear in either filter in Discover</B></li>
	*<li><B>Verify: Verify that the comment appears, but the URL Preview for the comment does not appear in either filter in Discover</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/20680167DD1A495485257BD5003BE603">TTT - DISCOVER - MICROBLOGGING - 00124 - PRIVATE COMMUNITY STATUS UPDATE COMMENT ADDED WITH URL PREVIEW- SAME ORGANISATION</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_Discover_Comment_PrivateCommunity(){
		
		String testName = ui.startTest();
		
		// User 1 will now create a restricted community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		listOfCommunities.add(restrictedCommunity);
		
		// User 1 will now log in to Communities UI and will post a status update with URL to the community - the URL will generate a thumbnail in this case
		String statusUpdateBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateURL = Data.getData().skyNewsURL;
		boolean urlPreviewIsDisplayed = CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(restrictedCommunity, baseCommunity, ui, uiCo, testUser1, communitiesAPIUser1, statusUpdateBeforeURL, statusUpdateURL, true, false);
		
		// Verify that the URL preview widget was displayed during the posting of the status update
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsDisplayed, true);
				
		// User 1 will now post a comment with URL to the status update
		String messagePostedEvent = Data.POSTED_A_MESSAGE_GENERIC.replace("USER", testUser1.getDisplayName());
		String commentBeforeURL = Data.getData().commonComment + Helper.genStrongRand();
		String commentURL = Data.getData().FacebookURL;
		boolean urlPreviewIsNotDisplayed = CommunityEvents.addStatusUpdateCommentWithURLUsingUI(ui, messagePostedEvent, commentBeforeURL, commentURL);
		
		// Verify that the URL preview widget was NOT displayed during the posting of the comment
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewIsNotDisplayed, true);
		
		// User 1 will now return to the Home screen and log out
		LoginEvents.gotoHomeAndLogout(ui);
		
		// User 2 will now log into Connections and will navigate to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// Create the elements to be verified
		String statusUpdateWithURL = statusUpdateBeforeURL + " " + statusUpdateURL;
		String commentOnCommunityStatusEvent = CommunityNewsStories.getCommentOnTheirOwnMessageNewsStory_User(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String commentWithURL = commentBeforeURL + " " + commentURL;
		String urlPreviewStatusUpdate = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, commentOnCommunityStatusEvent, statusUpdateURL);
		String thumbnailImageStatusUpdate = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, commentOnCommunityStatusEvent, statusUpdateURL);
		String urlPreviewComment = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, commentOnCommunityStatusEvent, commentURL);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the comment on community status event, the community status update content and the comment content are NOT displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{commentOnCommunityStatusEvent, statusUpdateWithURL, commentWithURL}, filter, false);
			
			// Verify that the status update URL preview widget, thumbnail image and comment URL preview are NOT displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewStatusUpdate, thumbnailImageStatusUpdate, urlPreviewComment}, null, false);
		}
		ui.endTest();
	}
}