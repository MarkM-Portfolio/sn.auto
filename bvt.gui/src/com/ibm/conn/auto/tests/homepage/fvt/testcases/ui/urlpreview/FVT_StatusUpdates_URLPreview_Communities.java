package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.urlpreview;

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
/* Copyright IBM Corp. 2010, 2014, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty DOHERTYP@ie.ibm.com
 */

public class FVT_StatusUpdates_URLPreview_Communities extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private CommunitiesUI uiCo;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		// User 1 will now create a moderated community with User 2 added as a member
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseModeratedCommunity, testUser1, communitiesAPIUser1, testUser2);
		
		// User 1 will now create a public community with User 2 added as a member
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityWithOneMember(basePublicCommunity, testUser1, communitiesAPIUser1, testUser2);
		
		// User 1 will now create a restricted community with User 2 added as a member
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunityWithOneMember(baseRestrictedCommunity, testUser1, communitiesAPIUser1, testUser2);
	}
	
	@BeforeMethod(alwaysRun = true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* urlPreview_StatusUpdates_PublicCommunity_Communities() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a public community with testUser2 as a member (Community ownership automatically makes testUser1 a follower of the community)</B></li>
	*<li><B>Step: testUser2 posts the status update containing a valid URL to the community</B></li>
	*<li><B>Step: testUser1 logs in to Homepage / Status Updates / All & Communities and verifies that the status update and the correct URL preview appears in both filters in the Status Update view</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears in both filters in the Status Update view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/3670324FB895638C85257BC9004FEA4B">TTT - STATUS UPDATES - COMMUNITIES - 00030 - STATUS UPDATE ADDED - PUBLIC - URL PREVIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_StatusUpdates_PublicCommunity_Communities() {
		
		ui.startTest();
		
		// Log in as User 2, navigate to the community and post a status update with URL to that community
		String statusMessageBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(publicCommunity, basePublicCommunity, ui, uiCo, testUser2, communitiesAPIUser2, statusMessageBeforeURL, url, true, false);
		
		// Return to home and logout
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Log in as User 1 and navigate to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, true);
		
		// Create the news story and elements to be verified
		String postedCommunityMessageEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, basePublicCommunity.getName(), testUser2.getDisplayName());
		String communityMessage = statusMessageBeforeURL + " " + url;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, postedCommunityMessageEvent, url);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, postedCommunityMessageEvent, url);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the posted community message event and status update with URL are displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{postedCommunityMessageEvent, communityMessage}, filter, true);
			
			// Verify that the URL preview widget and thumbnail image are displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImage}, null, true);
		}
		ui.endTest();
	}
	
	/**
	* urlPreview_StatusUpdates_ModCommunity_Communities() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a moderated community with testUser2 as a member (Community ownership automatically makes testUser1 a follower of the community)</B></li>
	*<li><B>Step: testUser2 posts the status update containing a valid URL to the community</B></li>
	*<li><B>Step: testUser1 logs in to Homepage / Status Updates / All & Communities and verifies that the status update and the correct URL preview appears in both filters in the Status Update view</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears in both filters in the Status Update view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B354D8FEAA7B18F585257BC9005047F4">TTT - STATUS UPDATES - COMMUNITIES - 00031 - STATUS UPDATE ADDED - MODERATED - URL PREVIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_StatusUpdates_ModCommunity_Communities() {
		
		ui.startTest();
		
		// Log in as User 2, navigate to the community and post a status update with URL to that community
		String statusMessageBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(moderatedCommunity, baseModeratedCommunity, ui, uiCo, testUser2, communitiesAPIUser2, statusMessageBeforeURL, url, true, false);
		
		// Return to home and logout
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Log in as User 1 and navigate to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, true);
		
		// Create the news story and elements to be verified
		String postedCommunityMessageEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseModeratedCommunity.getName(), testUser2.getDisplayName());
		String communityMessage = statusMessageBeforeURL + " " + url;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, postedCommunityMessageEvent, url);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, postedCommunityMessageEvent, url);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the posted community message event and status update with URL are displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{postedCommunityMessageEvent, communityMessage}, filter, true);
			
			// Verify that the URL preview widget and thumbnail image are displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImage}, null, true);
		}
		ui.endTest();
	}
	
	/**
	* urlPreview_StatusUpdates_PrivateCommunity_Communities() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 creates a private community with testUser2 as a member (Community ownership automatically makes testUser1 a follower of the community)</B></li>
	*<li><B>Step: testUser2 posts the status update containing a valid URL to the community</B></li>
	*<li><B>Step: testUser1 logs in to Homepage / Status Updates / All & Communities and verifies that the status update and the correct URL preview appears in both filters in the Status Update view</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears in both filters in the Status Update view</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8282C1119A3D67F785257BC900507DDF">TTT - STATUS UPDATES - COMMUNITIES - 00032 - STATUS UPDATE ADDED - PRIVATE - URL PREVIEW</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_StatusUpdates_PrivateCommunity_Communities() {
		
		ui.startTest();
		
		// Log in as User 2, navigate to the community and post a status update with URL to that community
		String statusMessageBeforeURL = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(restrictedCommunity, baseRestrictedCommunity, ui, uiCo, testUser2, communitiesAPIUser2, statusMessageBeforeURL, url, true, false);
		
		// Return to home and logout
		LoginEvents.gotoHomeAndLogout(ui);
		
		// Log in as User 1 and navigate to the Status Updates view
		LoginEvents.loginAndGotoStatusUpdates(ui, testUser1, true);
		
		// Create the news story and elements to be verified
		String postedCommunityMessageEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseRestrictedCommunity.getName(), testUser2.getDisplayName());
		String communityMessage = statusMessageBeforeURL + " " + url;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, postedCommunityMessageEvent, url);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, postedCommunityMessageEvent, url);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the posted community message event and status update with URL are displayed
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{postedCommunityMessageEvent, communityMessage}, filter, true);
			
			// Verify that the URL preview widget and thumbnail image are displayed
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImage}, null, true);
		}
		ui.endTest();
	}	
}