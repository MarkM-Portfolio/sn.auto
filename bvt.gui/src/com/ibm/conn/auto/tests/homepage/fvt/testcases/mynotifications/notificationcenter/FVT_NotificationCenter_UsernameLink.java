package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.notificationcenter;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2017                              		 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Notification Center Flyout] FVT UI Automation for Story 140633
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/143012
 * @author Patrick Doherty
 */

public class FVT_NotificationCenter_UsernameLink extends SetUpMethodsFVT {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private CommunitiesUI uiCo;
	private Community publicCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		communityBlogsAPIUser1 = initialiseAPICommunityBlogsHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public community and will add the Blogs widget to it
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest(){
	
		uiCo = CommunitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun=true)
	public void performCleanUp() {
		
		// Delete the community created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}

	/**
	* test_NotificationCenter_EventLink() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Communities</B></li>
	*<li><B>Step: User 1 add a blog entry and notify User 2 of the entry</B></li>
	*<li><B>Step: User 2 log into Homepage</B></li>
	*<li><B>Step: User 2 open the notification center flyout</B></li>
	*<li><B>Step: User 2 look at the username in the event in the flyout - verification point 1</B></li>
	*<li><B>Step: User 2 click on the name - verification point 2</B></li>
	*<li><B>Verify: Verify that User 1's name is a clickable link</B></li>
	*<li><B>Verify: Verify that it opens User 1's profile in a new tab / window</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/40051E9F6FF4830E85257DCC0051F689">TTT - NOTIFICATION CENTER FLYOUT - 00032 - USERNAME IS A CLICKABLE LINK</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void test_NotificationCenter_UsernameLink(){

		String testName = ui.startTest();
		
		// User 1 will now add a blog entry to the community blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost communityBlogEntry = CommunityBlogEvents.createBlogPost(testUser1, communityBlogsAPIUser1, baseBlogPost, publicCommunity);
		
		boolean preserveInstance = false;
		if(isOnPremise) {
			// User 1 will now notify User 2 about the newly created blog entry using the API
			CommunityBlogEvents.notifyUserAboutBlogEntry(communityBlogEntry, testUser1, communityBlogsAPIUser1, profilesAPIUser2);
		} else {
			// User 1 will now notify User 2 about the newly created blog entry using the UI (the API does NOT work for Smart Cloud)
			CommunityBlogEvents.loginAndNotifyUserAboutBlogEntryUsingUI(publicCommunity, baseCommunity, communityBlogEntry, ui, uiCo, testUser1, communitiesAPIUser1, profilesAPIUser2, isOnPremise, preserveInstance);
			preserveInstance = true;
		}
		// User 2 will now log in to Homepage
		LoginEvents.loginToHomepage(ui, testUser2, preserveInstance);
		
		// User 2 will now open the Notification Center flyout
		UIEvents.openNotificationCenter(ui);
		
		// Create the news story which contains the blog entry link
		String notifiedAboutEntryEvent = CommunityBlogNewsStories.getNotifiedYouAboutTheBlogEntryNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Retrieve the main browser window handle before clicking on the link
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
				
		// User 2 will now click on User 1's username in the notification news story in the Notification Center flyout
		UIEvents.clickLinkInNotificationCenterFlyout(ui, notifiedAboutEntryEvent, testUser1.getDisplayName());
				
		// Switch focus to the newly opened 'Profiles' browser window
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
				
		// Verify that User 2 has been re-directed to User 1's profile
		HomepageValid.verifyProfilesUIIsDisplayed_ViewingUsersProfileAsAnotherUser(ui, testUser1);
				
		// Close the Activities UI browser window and switch focus back to the main window
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
				
		ui.endTest();
	}
}