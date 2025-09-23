package com.ibm.conn.auto.tests.homepage.fvt.finalisation.ee;

import java.util.ArrayList;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
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

public class FVT_EE_IdeationBlogEvents_MultipleMentions extends SetUpMethods2 {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterBlogs, HomepageUIConstants.FilterCommunities };
	
	private APICommunitiesHandler communitiesAPIUser1;
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APIProfilesHandler profilesAPIUser2, profilesAPIUser3, profilesAPIUser4, profilesAPIUser5, profilesAPIUser6, profilesAPIUser7;
	private BaseCommunity baseCommunity;
	private boolean isOnPremise;
	private Community publicCommunity;
	private HomepageUI ui;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2, testUser3, testUser4, testUser5, testUser6, testUser7;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialize the configuration
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		// Ensure that 7 unique users are chosen from the CSV file
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
		} while(listOfUsers.size() < 7);
		
		testUser1 = listOfUsers.get(0);
		testUser2 = listOfUsers.get(1);
		testUser3 = listOfUsers.get(2);
		testUser4 = listOfUsers.get(3);
		testUser5 = listOfUsers.get(4);
		testUser6 = listOfUsers.get(5);
		testUser7 = listOfUsers.get(6);
		
		communitiesAPIUser1 = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		communityBlogsAPIUser1 = new APICommunityBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		profilesAPIUser2 = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		profilesAPIUser3 = new APIProfilesHandler(serverURL, testUser3.getAttribute(cfg.getLoginPreference()), testUser3.getPassword());
		profilesAPIUser4 = new APIProfilesHandler(serverURL, testUser4.getAttribute(cfg.getLoginPreference()), testUser4.getPassword());
		profilesAPIUser5 = new APIProfilesHandler(serverURL, testUser5.getAttribute(cfg.getLoginPreference()), testUser5.getPassword());
		profilesAPIUser6 = new APIProfilesHandler(serverURL, testUser6.getAttribute(cfg.getLoginPreference()), testUser6.getPassword());
		profilesAPIUser7 = new APIProfilesHandler(serverURL, testUser7.getAttribute(cfg.getLoginPreference()), testUser7.getPassword());
		
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
	* test_PublicCommunity_EE_MultipleMentions() 
	*<ul>
	*<li><B>1: Go to a community you own and add an Ideation Blog</b></li>
	*<li><B>2: Create an idea in the Ideation Blog</B></li>
	*<li><B>3: Vote an idea in the Ideation Blog</B></li>
	*<li><b>4: Go to Homepage Activity Stream</b></li>
	*<li><b>5: Open the EE for the story of the idea voted</b></li>
	*<li><B>6: Add a comment to the entry with more than 5 user @mentioned</B></li>
	*<li><B>Verify: Verify that the all the @mentioned appear correctly and all get notified about it in there Homepage Activity Stream</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/5AA91F533A79396485257A8B0052BE06/58940B366F0B8ED285257A7C004709C7">@Mentions - EE - Blog Comment - 00003 - User can add multiple @mentions</a></li>
	*</ul>
	*/
	@Test(groups = {"fvt_final_onprem", "fvt_final_cloud"})
	public void test_PublicCommunity_EE_MultipleMentions() {
		
		String testName = ui.startTest();
		
		// User 1 will now create an idea in the community ideation blog and will vote for the idea
		BaseBlogPost baseBlogPost = BlogBaseBuilder.buildBaseBlogPost(testName + Helper.genStrongRand());
		CommunityBlogEvents.createIdeaAndVoteForIdea(publicCommunity, baseBlogPost, testUser1, communityBlogsAPIUser1, testUser1, communityBlogsAPIUser1);
		
		// Log in as User 1 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser1, false);
		
		// Create all of the Mentions instances for all of the users to be mentioned
		Mentions user2Mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, "", "");
		Mentions user3Mentions = MentionsBaseBuilder.buildBaseMentions(testUser3, profilesAPIUser3, serverURL, "", "");
		Mentions user4Mentions = MentionsBaseBuilder.buildBaseMentions(testUser4, profilesAPIUser4, serverURL, "", "");
		Mentions user5Mentions = MentionsBaseBuilder.buildBaseMentions(testUser5, profilesAPIUser5, serverURL, "", "");
		Mentions user6Mentions = MentionsBaseBuilder.buildBaseMentions(testUser6, profilesAPIUser6, serverURL, "", "");
		Mentions user7Mentions = MentionsBaseBuilder.buildBaseMentions(testUser7, profilesAPIUser7, serverURL, "", "");
		Mentions[] allUserMentions = { user2Mentions, user3Mentions, user4Mentions, user5Mentions, user6Mentions, user7Mentions };
				
		// Create the news story to be used to open the EE
		String voteForIdeaEvent = CommunityBlogNewsStories.getVotedForYourIdeaNewsStory_You(ui, baseBlogPost.getTitle(), baseCommunity.getName());
		
		// Open the EE for the blogs news story and post a comment with mentions to all users using the EE
		CommunityBlogEvents.openEEAndPostCommentWithMultipleMentions(ui, driver, voteForIdeaEvent, allUserMentions);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		// Create the news stories to be verified
		String commentOnIdeaEvent = CommunityBlogNewsStories.getCommentOnYourIdeaNewsStory_You(ui, baseBlogPost.getTitle(), baseCommunity.getName());
		String mentionsText = "";
		for(Mentions mentions : allUserMentions) {
			mentionsText += "@" + mentions.getUserToMention().getDisplayName() + " ";
		}
		mentionsText = mentionsText.trim();
		
		// Verify that the comment on idea event, the idea description and the comment with multiple mentions are displayed in all views
		HomepageValid.verifyItemsInASUsingMultipleFilters(ui, driver, new String[]{commentOnIdeaEvent, baseBlogPost.getContent(), mentionsText}, TEST_FILTERS, true);
		
		// Log out of Connections
		LoginEvents.logout(ui);
		
		// Create the mentions event to be verified for all other users
		String mentionedYouEvent = CommunityBlogNewsStories.getMentionedYouInACommentOnIdeaNewsStory(ui, baseBlogPost.getTitle(), baseCommunity.getName(), testUser1.getDisplayName());
		
		User[] usersToLogin = { testUser2, testUser3, testUser4, testUser5, testUser6, testUser7 };
		for(User userToLogin : usersToLogin) {
			// Log in as the specified user and go to the Mentions view
			LoginEvents.loginAndGotoMentions(ui, userToLogin, true);
			
			// Verify that the mentions event, idea description and mentions text are displayed in the Mentions view
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{mentionedYouEvent, baseBlogPost.getContent(), mentionsText}, null, true);
			
			// Return to the Home screen and log out
			LoginEvents.gotoHomeAndLogout(ui);
		}
		ui.endTest();
	}
}