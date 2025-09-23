package com.ibm.conn.auto.tests.homepage.fvt.finalisation.ee;

import java.util.ArrayList;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;
import com.ibm.conn.auto.util.baseBuilder.BlogBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityBlogEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.community.CommunityBlogNewsStories;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016			                                 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/*
 * Author:	Anthony Cox
 * Date:	13th October 2016
 */

public class FVT_EE_IdeationBlogEvents_Typeahead extends SetUpMethods2 {
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3;
	private BaseCommunity baseCommunity;
	private boolean isOnPremise;
	private Community publicCommunity;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		// Ensure that 3 unique users are chosen from the CSV file
		ArrayList<User> listOfUsers = new ArrayList<User>();
		do {
			User currentUser = cfg.getUserAllocator().getUser(this);
			int index = 0;
			boolean userAlreadyChosen = false;
			while(index < listOfUsers.size() && userAlreadyChosen == false) {
				if(listOfUsers.get(index).getDisplayName().equals(currentUser.getDisplayName())) {
					userAlreadyChosen = true;
				}
				index ++;
			}
			if(userAlreadyChosen == false) {
				listOfUsers.add(currentUser);
			}
		} while(listOfUsers.size() < 3);
		
		testUser1 = listOfUsers.get(0);
		testUser2 = listOfUsers.get(1);
		testUser3 = listOfUsers.get(2);
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		communityBlogsAPIUser1 = new APICommunityBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		
		if(cfg.getProductName().toString().equalsIgnoreCase(CustomParameterNames.PRODUCT_NAME.getDefaultValue())) {
			isOnPremise = true;
		} else {
			isOnPremise = false;
		}
		
		// User 1 will now create a public community with the Ideation Blogs widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.IDEATION_BLOG, isOnPremise, testUser1, communitiesAPIUser1);
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUpTest() {
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete all of the communities created during the test
		communitiesAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	* test_PublicCommunity_EE_Mentions_Typeahead_SelectingUsers() 
	*<ul>
	*<li><B>1: Go to a community you own and add an Ideation Blog</b></li>
	*<li><B>2: Create an idea in the Ideation Blog</B></li>
	*<li><B>3: Go to Homepage Activity Stream</B></li>
	*<li><b>4: Open the EE for the story of the idea</b></li>
	*<li><b>5: Click in to add a comment and start typing "@xx" (make sure the letters are in a name in the system)</b></li>
	*<li><B>6: When the typeahead appears click on a name - verification point 1</B></li>
	*<li><b>7: Start typing another name with "@xx"</B></li>
	*<li><b>8: When typeahead appears use the up/down arrows to select a user and click "Enter" when you get the user you want - verification point 2</b></li>
	*<li><b>9: Attempt to edit either of the names that have been selected - verification point 3</b></li>
	*<li><B>Verification Point 1: Verify that you can scroll though the name with the mouse and when click the users name is entered in the embedded sharebox</B></li>
	*<li><b>Verification Point 2: Verify that you can scroll through the names with the up/down arrow and when you press enter the users name is entered in the embedded sharebox</B></li>
	*<li><b>Verification Point 3: Verify that the user cannot edit either of the names in the sharebox that were selected</b></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/6F3F8D34C946EED385257A7C0046FC61">@Mentions - EE - Blog Comment - 00002 - User can selected from typeahead</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_PublicCommunity_EE_Mentions_Typeahead_SelectingUsers() {
	
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdea(publicCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create the news story to be used to open the EE
		String createIdeaEvent = CommunityBlogNewsStories.getCreateIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		// Type in a partial mention, select any user with the mouse and verify that the mention has posted correctly
		Mentions user2Mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, "", "");
		String selectedUserWithMouse = UIEvents.openEEAndTypeBeforeMentionsTextAndTypePartialMentionsAndSelectFirstTypeaheadMenuItem(ui, driver, createIdeaEvent, user2Mentions, 2, true);
				
		// Verify that the selected user with mouse has the partial mention string included in their user name
		HomepageValid.verifyStringContainsSubstring(selectedUserWithMouse, testUser2.getDisplayName().substring(0, 2));
				
		// Switch back to the comments / replies frame of the EE
		UIEvents.switchToEECommentOrRepliesFrame(ui, true);
		
		// Type in a second partial mention, select any user with the arrow keys and verify that the mention has posted correctly
		Mentions user3Mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, "", "");
		String selectedUserWithArrowKeys = UIEvents.typePartialMentionInEEAndSelectTypeaheadUserWithArrowKeys(ui, user3Mentions, 2, true);
				
		// Verify that the selected user with arrow keys has the partial mention string included in their user name
		HomepageValid.verifyStringContainsSubstring(selectedUserWithArrowKeys, testUser3.getDisplayName().substring(0, 2));
		
		// Delete both of the mentions links using the backspace key - verifies that the delete key removes the entire mentions link (ie. the mentions link cannot be edited)
		boolean allMentionsDeleted = UIEvents.deleteTwoMentionsLinksWithBackspaceKey(driver, ui, selectedUserWithArrowKeys, selectedUserWithMouse);
		
		// Verify that all mentioned users were deleted successfully
		HomepageValid.verifyBooleanValuesAreEqual(allMentionsDeleted, true);
		
		ui.endTest();
	}
}