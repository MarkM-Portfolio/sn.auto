package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.visitormodel.sharedextheader;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.DogearBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.EventBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FeedBaseBuilder;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.Calendar;
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
 * @author Patrick Doherty
 */

public class FVT_VisitorModel_SharedExternallyHeader_CommunityEvents_NoVisitor extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(FVT_VisitorModel_SharedExternallyHeader_CommunityEvents_NoVisitor.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1, testUser3;
	private APICalendarHandler calendarAPIUser1;
	private APICommunitiesHandler communityAPIUser1, communityAPIUser3;
	private String serverURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		do {
			testUser3 = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA);
		} while(testUser1.getDisplayName().equals(testUser3.getDisplayName()));
		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		communityAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communityAPIUser3 = new APICommunitiesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		calendarAPIUser1 = new APICalendarHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword()); 
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		// Initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_communityCreated() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/19C5E119D90ED69385257C8A00356A94">TTT - VISITORS - ACTIVITY STREAM - 00047 - SHARED EXTERNALLY HEADER - COMMUNITY EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_communityCreated() {
		
		String testName = ui.startTest();
		
		// Create the restricted community with all setup steps completed
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = executeCommonRestrictedCommunitySetupSteps(baseCommunity);
		
		// Log in as User 3 and navigate to I'm Following
		executeCommonUser3LoginSteps();
		
		// Assign the news story to be verified
		String newsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME, restrictedCommunity.getTitle(), null, testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are NOT displayed
		verifySharedExternallyIsNotDisplayedInAllFilter(newsStory, baseCommunity.getDescription());
		
		// Assign the news story to be verified
		newsStory = ui.replaceNewsStory(Data.COMMUNITY_ADD_MEMBER, testUser3.getDisplayName(), restrictedCommunity.getTitle(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are NOT displayed
		verifySharedExternallyIsNotDisplayedInCommunitiesFilter(newsStory, baseCommunity.getDescription());
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_bookmarkAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add a bookmark to this community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / Communities - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/19C5E119D90ED69385257C8A00356A94">TTT - VISITORS - ACTIVITY STREAM - 00047 - SHARED EXTERNALLY HEADER - COMMUNITY EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_bookmarkAdded() {
		
		String testName = ui.startTest();
		
		// Create the restricted community with all setup steps completed
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = executeCommonRestrictedCommunitySetupSteps(baseCommunity);
		
		// Add the bookmark to the community
		BaseDogear baseBookmark = DogearBaseBuilder.buildCommunityBaseDogear(testName, Data.getData().IbmURL, baseCommunity);
		communityAPIUser1.createBookmark(baseBookmark);
		
		// Log in as User 3 and navigate to I'm Following
		executeCommonUser3LoginSteps();
				
		// Assign the news story to be verified
		String newsStory = ui.replaceNewsStory(Data.ADD_COMMUNITY_BOOKMARK, baseBookmark.getTitle(), restrictedCommunity.getTitle(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are NOT displayed
		verifySharedExternallyIsNotDisplayedInAllFilter(newsStory, baseBookmark.getDescription());
		verifySharedExternallyIsNotDisplayedInCommunitiesFilter(newsStory, baseBookmark.getDescription());
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_feedAdded() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add a feed to this community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/19C5E119D90ED69385257C8A00356A94">TTT - VISITORS - ACTIVITY STREAM - 00047 - SHARED EXTERNALLY HEADER - COMMUNITY EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_feedAdded() {
		
		String testName = ui.startTest();
		
		// Create the restricted community with all setup steps completed
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = executeCommonRestrictedCommunitySetupSteps(baseCommunity);
		
		// Add the feed to the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(testName);
		communityAPIUser1.createFeed(restrictedCommunity, baseFeed);
		
		// Log in as User 3 and navigate to I'm Following
		executeCommonUser3LoginSteps();
		
		// Assign the news story to be verified
		String newsStory = ui.replaceNewsStory(Data.ADD_COMMUNITY_FEED, baseFeed.getTitle(), restrictedCommunity.getTitle(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are NOT displayed
		verifySharedExternallyIsNotDisplayedInAllFilter(newsStory, baseFeed.getDescription());
		verifySharedExternallyIsNotDisplayedInCommunitiesFilter(newsStory, baseFeed.getDescription());
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	
	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_communityCreated_linkDesc() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community with a link and formatting in the main body/description adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/19C5E119D90ED69385257C8A00356A94">TTT - VISITORS - ACTIVITY STREAM - 00047 - SHARED EXTERNALLY HEADER - COMMUNITY EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_communityCreated_linkDesc() {
		
		String testName = ui.startTest();
		
		// Create the restricted community with all setup steps completed
		String link = Data.getData().skyNewsURL;
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genStrongRand())
														.access(Access.RESTRICTED)
														.tags(Data.getData().commonTag + Helper.genStrongRand())
														.description(Data.getData().commonDescription + " " + link)
														.shareOutside(false)
														.build();
		Community restrictedCommunity = executeCommonRestrictedCommunitySetupSteps(baseCommunity);
		
		// Log in as User 3 and navigate to I'm Following
		executeCommonUser3LoginSteps();
		
		// Assign the news story to be verified
		String newsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME, restrictedCommunity.getTitle(), null, testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are NOT displayed
		verifySharedExternallyIsNotDisplayedInAllFilter(newsStory, baseCommunity.getDescription());
		
		// Verify that the link is displayed correctly in the community description
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{"link=" + link}, null, true);
		
		// Assign the news story to be verified
		newsStory = ui.replaceNewsStory(Data.COMMUNITY_ADD_MEMBER, testUser3.getDisplayName(), restrictedCommunity.getTitle(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are NOT displayed
		verifySharedExternallyIsNotDisplayedInCommunitiesFilter(newsStory, baseCommunity.getDescription());
		
		// Verify that the link is displayed correctly in the community description
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{"link=" + link}, null, true);
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_relatedCommunity() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 add a related community to this community</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/19C5E119D90ED69385257C8A00356A94">TTT - VISITORS - ACTIVITY STREAM - 00047 - SHARED EXTERNALLY HEADER - COMMUNITY EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_relatedCommunity() {
		
		String testName = ui.startTest();
		
		// Create the restricted community with all setup steps completed
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = executeCommonRestrictedCommunitySetupSteps(baseCommunity);
		
		// Add the related community widget to the restricted community
		communityAPIUser1.addWidget(restrictedCommunity, BaseWidget.RELATED_COMMUNITIES);
		
		// Log in as User 3 and navigate to I'm Following
		executeCommonUser3LoginSteps();
		
		// Assign the news story to be verified
		String newsStory = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_ADD_MEMBER_FOR_ME, restrictedCommunity.getTitle(), null, testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are NOT displayed
		verifySharedExternallyIsNotDisplayedInAllFilter(newsStory, baseCommunity.getDescription());
		
		// Assign the news story to be verified
		newsStory = ui.replaceNewsStory(Data.COMMUNITY_ADD_MEMBER, testUser3.getDisplayName(), restrictedCommunity.getTitle(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are NOT displayed
		verifySharedExternallyIsNotDisplayedInCommunitiesFilter(newsStory, baseCommunity.getDescription());
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_standaloneEvent() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 creates an event</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/19C5E119D90ED69385257C8A00356A94">TTT - VISITORS - ACTIVITY STREAM - 00047 - SHARED EXTERNALLY HEADER - COMMUNITY EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_standaloneEvent() {
		
		String testName = ui.startTest();
		
		// Create the restricted community with all setup steps completed
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = executeCommonRestrictedCommunitySetupSteps(baseCommunity);
		
		// Add the Events widget to the restricted community
		communityAPIUser1.addWidget(restrictedCommunity, BaseWidget.EVENTS);
		
		// Create a single event for this restricted community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, false);
		Calendar calendarEvent = calendarAPIUser1.addCalendarEvent(restrictedCommunity, baseEvent);
		
		// Log in as User 3 and navigate to I'm Following
		executeCommonUser3LoginSteps();
		
		// Assign the news story to be verified
		String newsStory = ui.replaceNewsStory(Data.CREATE_COMMUNITY_CALENDAR_EVENT, baseEvent.getName(), restrictedCommunity.getTitle(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are NOT displayed
		verifySharedExternallyIsNotDisplayedInAllFilter(newsStory, baseEvent.getDescription());
		verifySharedExternallyIsNotDisplayedInCommunitiesFilter(newsStory, baseEvent.getDescription());
				
		log.info("INFO: Perform clean-up now that the test has completed");
		calendarAPIUser1.deleteCalendarEvent(calendarEvent);
		communityAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}

	/**
	* visitor_sharedExternallyHeader_privateCommunity_noVisitor_repeatingEvent() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 log in to Communities</B></li>
	*<li><B>Step: testUser1 creates a private community adding testUser3 as a member</B></li>
	*<li><B>Step: testUser3 follow the community</B></li>
	*<li><B>Step: testUser1 creates a repeating event</B></li>
	*<li><B>Step: testUser3 log into Homepage / Updates / I'm Following / All - verification point</B></li>
	*<li><B>Verify: 'Shared externally' header in a yellow background DOES NOT appears beside the event title</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/19C5E119D90ED69385257C8A00356A94">TTT - VISITORS - ACTIVITY STREAM - 00047 - SHARED EXTERNALLY HEADER - COMMUNITY EVENTS- PRIVATE COMMUNITY - VISITOR NOT ADDED</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtvisitor"})
	public void visitor_sharedExternallyHeader_privateCommunity_noVisitor_repeatingEvent() {
		
		String testName = ui.startTest();
		
		// Create the restricted community with all setup steps completed
		BaseCommunity baseCommunity = CommunityBaseBuilder.buildBaseCommunity(testName + Helper.genStrongRand(), Access.RESTRICTED);
		Community restrictedCommunity = executeCommonRestrictedCommunitySetupSteps(baseCommunity);
		
		// Add the Events widget to the restricted community
		communityAPIUser1.addWidget(restrictedCommunity, BaseWidget.EVENTS);
		
		// Create a single event for this restricted community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(testName, true);
		Calendar calendarEvent = calendarAPIUser1.addCalendarEvent(restrictedCommunity, baseEvent);
		
		// Log in as User 3 and navigate to I'm Following
		executeCommonUser3LoginSteps();
		
		// Assign the news story to be verified
		String newsStory = ui.replaceNewsStory(Data.CREATE_COMMUNITY_CALENDAR_REPEATING_EVENT, baseEvent.getName(), restrictedCommunity.getTitle(), testUser1.getDisplayName());
		
		// Verify that all news story components are displayed and all 'Shared Externally' components are NOT displayed
		verifySharedExternallyIsNotDisplayedInAllFilter(newsStory, baseEvent.getDescription());
		verifySharedExternallyIsNotDisplayedInCommunitiesFilter(newsStory, baseEvent.getDescription());
				
		log.info("INFO: Perform clean-up now that the test has completed");
		calendarAPIUser1.deleteCalendarEvent(calendarEvent);
		communityAPIUser1.deleteCommunity(restrictedCommunity);
		ui.endTest();
	}
	
	/**
	 * Performs the commonly used procedural steps for each test case that invokes it
	 * 
	 * Step 1: User 1 creates a restricted community
	 * Step 2: User 1 adds User 3 to the restricted community
	 * Step 3: User 3 follows the restricted community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @return - The Community instance of the restricted community
	 */
	private Community executeCommonRestrictedCommunitySetupSteps(BaseCommunity baseCommunity) {
		
		log.info("INFO: " + testUser1.getDisplayName() + " creates a private community");
		Community community = communityAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add " + testUser3.getDisplayName() + " to the community as a member");
		communityAPIUser1.addMemberToCommunity(testUser3, community, StringConstants.Role.MEMBER);
		
		log.info("INFO: " + testUser3.getDisplayName() + " will now follow the restricted community");
		communityAPIUser3.followCommunity(community);
		
		return community;
	}
	
	/**
	 * Performs the common steps required to login as User 3 and navigate to the I'm Following view
	 */
	private void executeCommonUser3LoginSteps() {
		
		log.info("INFO: " + testUser3.getDisplayName() + " log into Connections");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser3);
		
		log.info("INFO: " + testUser3.getDisplayName() + " go to Homepage / Updates / I'm Following / All");
		ui.gotoImFollowing();
	}
	
	/**
	 * Executes all required verifications for the 'Shared Externally' component NOT displayed
	 * 
	 * @param newsStoryContent - The news story with which the 'Shared Externally' component should not be displayed
	 * @param newsStoryDescription - The description displayed with the news story (null if no description displayed)
	 */
	private void verifySharedExternallyIsNotDisplayedInAllFilter(String newsStoryContent, String newsStoryDescription) {
				
		// Verify that the news story is displayed in the All filter
		verifyNewsStoryComponentsAreDisplayed(HomepageUIConstants.FilterAll, newsStoryContent, newsStoryDescription);
		
		// Verify that the Shared Externally message is not displayed in the All Filter
		verifySharedExternallyIsNotDisplayed(newsStoryContent, null);
	}
	
	/**
	 * Executes all required verifications for the 'Shared Externally' component NOT displayed
	 * 
	 * @param newsStoryContent - The news story with which the 'Shared Externally' component should not be displayed
	 * @param newsStoryDescription - The description displayed with the news story (null if no description displayed)
	 */
	private void verifySharedExternallyIsNotDisplayedInCommunitiesFilter(String newsStoryContent, String newsStoryDescription) {
		
		// Verify that the news story is displayed in the Communities filter
		verifyNewsStoryComponentsAreDisplayed(HomepageUIConstants.FilterCommunities, newsStoryContent, newsStoryDescription);
		
		// Verify that the Shared Externally message is not displayed in the Communities Filter
		verifySharedExternallyIsNotDisplayed(newsStoryContent, null);
	}
	
	/**
	 * Verifies all news story components are displayed - including all comments
	 * 
	 * @param newsStoryContent - The news story content to be verified as displayed
	 * @param newsStoryDescription - The description of the news story event to be verified (null if no verification required)
	 * @param comment1 - The first comment posted to the news story event to be verified (null if no verification required)
	 * @param comment2 - The second comment posted to the news story event to be verified (null if no verification required)
	 */
	private void verifyNewsStoryComponentsAreDisplayed(String filter, String newsStoryContent, String newsStoryDescription) {
		
		if(newsStoryDescription == null) {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryContent}, filter, true);
		} else  {
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{newsStoryContent, newsStoryDescription}, filter, true);
		}
	}
	
	/**
	 * Verifies that the Shared Externally components are NOT displayed
	 * 
	 * @param newsStoryContent - The news story with which the 'Shared Externally' component should not be displayed
	 */
	private void verifySharedExternallyIsNotDisplayed(String newsStoryContent, String filter) {
		
		// Create the CSS selectors for the icon and the message
		String iconCSSSelector = HomepageUIConstants.SharedExternally_Icon.replace("PLACEHOLDER", newsStoryContent);
		String messageCSSSelector = HomepageUIConstants.SharedExternally_Message.replace("PLACEHOLDER", newsStoryContent);
		
		// Verify that both selectors are NOT displayed in the UI
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{iconCSSSelector, messageCSSSelector}, filter, false);
	}
}