package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.urlpreview;

import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * 	@author	 	Anthony Cox
 * 	Date		29th January 2016
 */

public class FVT_GlobalSharebox_URLPreview extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private ArrayList<Community> listOfCommunities = new ArrayList<Community>();
	private User testUser1;
									   
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the tests
		for(Community community : listOfCommunities) {
			communitiesAPIUser1.deleteCommunity(community);
		}
	}
	
	/**
	* urlPreview_globalSharebox_beforePosting() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Login to Homepage and open the global sharebox</B></li>
	*<li><B>Step: Select "Everyone" from the dropdown options</B></li>
	*<li><B>Step: Type a new status update which contains a valid URL in the Homepage global sharebox and press space after it</B></li>
	*<li><B>Verify: Verify that URL Preview appears before the status update is posted</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/5FFF314DADBB7E9485257BD5003C5559">TTT - URL PREVIEW - GLOBAL SHAREBOX - 00001 - URL PREVIEW APPEARS WHEN USER PRESSES SPACE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void urlPreview_globalSharebox_beforePosting() {

		ui.startTest();
		
		// Create the URL status message to be entered into the global sharebox - this URL will NOT generate a thumbnail
		String statusMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().bbcURL;
		
		// Log in as User 1
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// Open the global sharebox in the UI and verify that all components are displayed correctly
		UIEvents.openGlobalShareboxAndVerifyAllComponents(ui);
		
		// Type the URL into the global sharebox in order to generate the URL Preview widget
		boolean urlPreviewDisplayed = UIEvents.typeStatusWithURLInGlobalSharebox(ui, statusMessage, url, false);
		
		// Verify that the URL preview widget and thumbnail image were displayed as expected
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewDisplayed, true);
		
		ui.endTest();
	}
	
	/**
	* urlPreview_globalSharebox_beforePosting_modCommunity_thumbnail() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Create a moderated community</B></li>
	*<li><B>Step: Go to Homepage and open the global sharebox</B></li>
	*<li><B>Step: Select "a Community" from the dropdown options and enter the name of the moderated community</B></li>
	*<li><B>Step: Type a new status update which contains a valid URL in the Homepage global sharebox and press space after it</B></li>
	*<li><B>Verify: Verify that URL Preview and thumbnail appear before the status update is posted</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/4DDE74B645CA141185257BD5003C554E">TTT - URL PREVIEW - GLOBAL SHAREBOX - 00012 - URL PREVIEW WIDGET DISPLAYS THUMBNAIL</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void urlPreview_globalSharebox_beforePosting_modCommunity_thumbnail() {

		String testName = ui.startTest();
		
		// User 1 will now create a moderated community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.MODERATED);
		Community moderatedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		listOfCommunities.add(moderatedCommunity);
		
		// Create the URL status message to be entered into the global sharebox - this URL will generate a thumbnail
		String statusMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		
		// Log in as User 1
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// Open the global sharebox in the UI and verify that all components are displayed correctly
		UIEvents.openGlobalShareboxAndVerifyAllComponents(ui);
		
		// Select the moderated community and then type the URL into the global sharebox in order to generate the URL Preview widget
		boolean urlPreviewDisplayed = UIEvents.typeCommunityStatusWithURLInGlobalSharebox(ui, moderatedCommunity, statusMessage, url, true);
		
		// Verify that the URL preview widget and thumbnail image were displayed as expected
		HomepageValid.verifyBooleanValuesAreEqual(urlPreviewDisplayed, true);
		
		ui.endTest();
	}

	/**
	* urlPreview_globalSharebox_publicCommunity_thumbnailRemoved() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Create a public community</B></li>
	*<li><B>Step: Go to Homepage and open the global sharebox</B></li>
	*<li><B>Step: Select "a Community" from the dropdown options and enter the name of the public community</B></li>
	*<li><B>Step: Type a new status update which contains a valid URL in the Homepage global sharebox and press space after it</B></li>
	*<li><B>Step: When the URL preview appears check the checkbox to remove the thumbnail</B></li>
	*<li><B>Step: Post the status update</B></li>
	*<li><B>Verify: Verify that the thumbnail is removed from the URL preview before the status update is posted</B></li>
	*<li><B>Verify: Verify that the URL preview appears correctly without a thumbnail</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/EC6264FFF8741E3885257BD5003C5560">TTT - URL PREVIEW - GLOBAL SHAREBOX - 00021 - URL PREVIEW CHECKBOX CHECKED TO REMOVE THUMBNAIL</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void urlPreview_globalSharebox_publicCommunity_thumbnailRemoved() {

		String testName = ui.startTest();
		
		// User 1 will now create a public community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		listOfCommunities.add(publicCommunity);
		
		// Create the URL status message to be entered into the global sharebox - this URL will generate a thumbnail
		String statusMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		
		// Log in as User 1
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// Open the global sharebox in the UI and verify that all components are displayed correctly
		UIEvents.openGlobalShareboxAndVerifyAllComponents(ui);
		
		// Select the public community, type the URL in order to generate the URL Preview widget, remove the thumbnail image and then post the status message
		UIEvents.postCommunityStatusWithURLInGlobalShareboxAndRemoveThumbnail(ui, publicCommunity, statusMessage, url);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Create the news stories and elements to be verified
		String postedCommunityMessageEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String postedCommunityMessage = statusMessage + " " + url;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, postedCommunityMessageEvent, url);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, postedCommunityMessageEvent, url);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the posted community status message event and message content are displayed in all filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{postedCommunityMessageEvent, postedCommunityMessage}, filter, true);
			
			// Verify that the URL preview widget is displayed in all filters
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget}, null, true);
			
			// Verify that the thumbnail image is NOT displayed in all filters
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{thumbnailImage}, null, false);
		}
		ui.endTest();
	}

	/**
	* urlPreview_globalSharebox_publicCommunity_thumbnail() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Create a public community</B></li>
	*<li><B>Step: Go to Homepage and open the global sharebox</B></li>
	*<li><B>Step: Select "a Community" from the dropdown options and enter the name of the public community</B></li>
	*<li><B>Step: Type a new status update which contains a valid URL in the Homepage global sharebox and press space after it</B></li>
	*<li><b>Step: When the URL preview appears make sure the checkbox to remove the thumbnail is unchecked - verification point 1</b></li>
	*<li><B>Step: Post the status update - verification point 2</B></li>
	*<li><B>Verify: Verify the thumbnail is not removed from the preview</B></li>
	*<li><B>Verify: Verify the updates posts and there is a thumbnail in the URL preview</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/FBA04FB0D2DAF71A85257BD5003C5561">TTT - URL PREVIEW - GLOBAL SHAREBOX - 00022 - URL PREVIEW CHECKBOX UNCHECKED TO REMOVE THUMBNAIL</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void urlPreview_globalSharebox_publicCommunity_thumbnail() {

		String testName = ui.startTest();
		
		// User 1 will now create a public community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		listOfCommunities.add(publicCommunity);
		
		// Create the URL status message to be entered into the global sharebox - this URL will generate a thumbnail
		String statusMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		
		// Log in as User 1
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// Open the global sharebox in the UI and verify that all components are displayed correctly
		UIEvents.openGlobalShareboxAndVerifyAllComponents(ui);
		
		// Select the public community, type the URL in order to generate the URL Preview widget with thumbnail image and then post the status update
		UIEvents.postCommunityStatusWithURLInGlobalSharebox(ui, publicCommunity, statusMessage, url, true);
		
		// Navigate to the Discover view
		UIEvents.gotoDiscover(ui);
		
		// Create the news stories and elements to be verified
		String postedCommunityMessageEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		String postedCommunityMessage = statusMessage + " " + url;
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_NewsFeed(ui, postedCommunityMessageEvent, url);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_NewsFeed(ui, postedCommunityMessageEvent, url);
		
		for(String filter : TEST_FILTERS) {
			// Verify that the posted community status message event and message content are displayed in all filters
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{postedCommunityMessageEvent, postedCommunityMessage}, filter, true);
			
			// Verify that the URL preview widget and thumbnail image are displayed in all filters
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImage}, null, true);
		}
		ui.endTest();
	}

	/**
	* urlPreview_globalSharebox_privateCommunity_EE() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Create a private community</B></li>
	*<li><B>Step: Go to Homepage and open the global sharebox</B></li>
	*<li><B>Step: Select "a Community" from the dropdown options and enter the name of the private community</B></li>
	*<li><B>Step: Type a new status update which contains a valid URL in the Homepage global sharebox and press space after it</B></li>
	*<li><B>Step: Post the status update</B></li>
	*<li><B>Step: Go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for the story</B></li>
	*<li><B>Verify: Verify that the URL preview appears correctly in the EE</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B48ED58F466DEDC185257BD5003C5567">TTT - URL PREVIEW - GLOBAL SHAREBOX - 00028 - URL PREVIEW OPENS CORRECTLY IN THE EE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem"})
	public void urlPreview_globalSharebox_privateCommunity_EE() {

		String testName = ui.startTest();
		
		// User 1 will now create a restricted community
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		listOfCommunities.add(restrictedCommunity);
		
		// Create the URL status message to be entered into the global sharebox - this URL will generate a thumbnail
		String statusMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		
		// Log in as User 1
		LoginEvents.loginToHomepage(ui, testUser1, false);
		
		// Open the global sharebox in the UI and verify that all components are displayed correctly
		UIEvents.openGlobalShareboxAndVerifyAllComponents(ui);
		
		// Select the public community, type the URL in order to generate the URL Preview widget with thumbnail image and then post the status update
		UIEvents.postCommunityStatusWithURLInGlobalSharebox(ui, restrictedCommunity, statusMessage, url, true);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		
		// Open the EE for the story and switch to the EE frame
		String postedCommunityMessageEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseCommunity.getName(), testUser1.getDisplayName());
		UIEvents.openEE(ui, postedCommunityMessageEvent);
		
		// Create the URL preview widget elements to be verified in the EE
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_EE(url);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(url);
		
		// Verify that the URL preview widget and thumbnail image are displayed in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImage}, null, true);
		
		ui.endTest();
	}
}