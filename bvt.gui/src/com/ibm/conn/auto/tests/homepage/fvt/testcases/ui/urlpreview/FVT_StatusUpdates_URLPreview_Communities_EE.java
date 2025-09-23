package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.urlpreview;

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
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016		                                     */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author Patrick Doherty DOHERTYP@ie.ibm.com
 */

public class FVT_StatusUpdates_URLPreview_Communities_EE extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private BaseCommunity baseModeratedCommunity, basePublicCommunity, baseRestrictedCommunity;
	private CommunitiesUI uiCo;
	private Community moderatedCommunity, publicCommunity, restrictedCommunity;
	private User testUser1;
		
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		// User 1 will now create a moderated community
		baseModeratedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.MODERATED);
		moderatedCommunity = CommunityEvents.createNewCommunity(baseModeratedCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a public community
		basePublicCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(basePublicCommunity, testUser1, communitiesAPIUser1);
		
		// User 1 will now create a restricted community
		baseRestrictedCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.RESTRICTED);
		restrictedCommunity = CommunityEvents.createNewCommunity(baseRestrictedCommunity, testUser1, communitiesAPIUser1);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(),driver);
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(moderatedCommunity);
		communitiesAPIUser1.deleteCommunity(publicCommunity);
		communitiesAPIUser1.deleteCommunity(restrictedCommunity);
	}
	
	/**
	* urlPreview_StatusUpdates_PublicCommunity_Communities_EE() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Creates a public community </B></li>
	*<li><B>Step: Post a status update containing a valid URL to the community</B></li>
	*<li><B>Step: Log in to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for the status update story</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears correctly in the EE</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C99CB94FFEF6C9D785257C35003BFADE">TTT - URL PREVIEW - EE - 00012 - COMMUNITY UPDATE (PUBLIC) URL PREVIEW APPEARS IN EE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_StatusUpdates_PublicCommunity_Communities_EE() {
		
		ui.startTest();
		
		// User 1 will now log in and post a status update with URL to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(publicCommunity, basePublicCommunity, ui, uiCo, testUser1, communitiesAPIUser1, user1StatusUpdate, url, true, false);
		
		// Return to the Home screen
		UIEvents.gotoHome(ui);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		UIEvents.clickShowMore(ui);
		
		// Open the EE for the story and switch to the EE frame
		String postedCommunityMessageEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, basePublicCommunity.getName(), testUser1.getDisplayName());
		UIEvents.openEE(ui, postedCommunityMessageEvent);
		
		// Create the URL preview widget elements to be verified in the EE
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_EE(url);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(url);
		
		// Verify that the URL preview widget and thumbnail image are displayed in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImage}, null, true);
		
		ui.endTest();
	}
	
	/**
	* urlPreview_StatusUpdates_ModCommunity_Communities_EE() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Creates a moderated community </B></li>
	*<li><B>Step: Post a status update containing a valid URL to the community</B></li>
	*<li><B>Step: Log in to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for the status update story</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears correctly in the EE</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C99CB94FFEF6C9D785257C35003BFADE">TTT - URL PREVIEW - EE - 00012 - COMMUNITY UPDATE (PUBLIC) URL PREVIEW APPEARS IN EE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_StatusUpdates_ModCommunity_Communities_EE() {
		
		ui.startTest();
		
		// User 1 will now log in and post a status update with URL to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(moderatedCommunity, baseModeratedCommunity, ui, uiCo, testUser1, communitiesAPIUser1, user1StatusUpdate, url, true, false);
		
		// Return to the Home screen
		UIEvents.gotoHome(ui);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		UIEvents.clickShowMore(ui);
		
		// Open the EE for the story and switch to the EE frame
		String postedCommunityMessageEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseModeratedCommunity.getName(), testUser1.getDisplayName());
		UIEvents.openEE(ui, postedCommunityMessageEvent);
		
		// Create the URL preview widget elements to be verified in the EE
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_EE(url);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(url);
		
		// Verify that the URL preview widget and thumbnail image are displayed in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImage}, null, true);
		
		ui.endTest();
	}
	
	/**
	* urlPreview_StatusUpdates_PrivateCommunity_Communities_EE() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Creates a private community </B></li>
	*<li><B>Step: Post a status update containing a valid URL to the community</B></li>
	*<li><B>Step: Log in to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: Open the EE for the status update story</B></li>
	*<li><B>Verify: Verify that the news story and the URL Preview for the status update appears correctly in the EE</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/C99CB94FFEF6C9D785257C35003BFADE">TTT - URL PREVIEW - EE - 00012 - COMMUNITY UPDATE (PUBLIC) URL PREVIEW APPEARS IN EE</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void urlPreview_StatusUpdates_PrivateCommunity_Communities_EE() {
		
		ui.startTest();
		
		// User 1 will now log in and post a status update with URL to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String url = Data.getData().skyNewsURL;
		CommunityEvents.loginAndNavigateToCommunityAndAddStatusUpdateWithURL(restrictedCommunity, baseRestrictedCommunity, ui, uiCo, testUser1, communitiesAPIUser1, user1StatusUpdate, url, true, false);
		
		// Return to the Home screen
		UIEvents.gotoHome(ui);
		
		// Navigate to the I'm Following view
		UIEvents.gotoImFollowing(ui);
		UIEvents.clickShowMore(ui);
		
		// Open the EE for the story and switch to the EE frame
		String postedCommunityMessageEvent = CommunityNewsStories.getPostedAMessageNewsStory(ui, baseRestrictedCommunity.getName(), testUser1.getDisplayName());
		UIEvents.openEE(ui, postedCommunityMessageEvent);
		
		// Create the URL preview widget elements to be verified in the EE
		String urlPreviewWidget = CSSBuilder.getURLPreviewWidgetSelector_EE(url);
		String thumbnailImage = CSSBuilder.getURLPreviewWidgetThumbnailImageSelector_EE(url);
		
		// Verify that the URL preview widget and thumbnail image are displayed in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{urlPreviewWidget, thumbnailImage}, null, true);
		
		ui.endTest();
	}	
}