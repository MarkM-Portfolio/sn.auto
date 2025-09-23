package com.ibm.conn.auto.util.eventBuilder.community;

import com.ibm.conn.auto.webui.constants.WikisUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.wikis.WikiEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.wikis.nodes.Wiki;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiComment;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

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
 * @author 	Anthony Cox
 * Date:	30th March 2016
 */

public class CommunityWikiEvents {
	
	private static Logger log = LoggerFactory.getLogger(CommunityWikiEvents.class);

	/**
	 * Creates a wiki page in a community
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which the wiki page will be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPage(Wiki communityWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage) {
		
		// Create the wiki page in the community wiki
		return WikiEvents.createWikiPage(communityWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
	}
	
	/**
	 * Creates a community wiki page and then updates the wiki page
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which the wiki page is to be created and updated
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created and updated
	 * @param userCreatingWikiPage - The User instance of the user creating and updating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating and updating the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageAndEditWikiPage(Wiki communityWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage) {
		
		// Create the wiki page and update the description of the wiki page
		return WikiEvents.createWikiPageAndEditWikiPage(communityWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
	}
	
	/**
	 * Creates a community wiki page and then likes / recommends the wiki page
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which the wiki page is to be created and liked
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created and liked / recommended
	 * @param userCreatingWikiPage - The User instance of the user creating and liking the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating and liking the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageAndLikeWikiPage(Wiki communityWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage) {
		
		// Create the community wiki page and like / recommend the wiki page
		return WikiEvents.createWikiPageAndLikeWikiPage(communityWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
	}
	
	/**
	 * Creates a community wiki page and then adds a comment to the wiki page
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which the wiki page is to be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created and commented on
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page and posting the comment
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page and posting the comment
	 * @param commentToBePosted - The String content of the comment to be posted to the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageAndAddComment(Wiki communityWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage, String commentToBePosted) {
		
		// Create the community wiki page and post the comment to the wiki page
		return WikiEvents.createWikiPageAndAddComment(communityWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, commentToBePosted);
	}
	
	/**
	 * Creates a community wiki page and then adds a comment to the wiki page and edits the comment
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which the wiki page is to be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created and commented on
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page and posting / updating the comment
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page and posting / updating the comment
	 * @param commentToBePosted - The String content of the comment to be posted to the wiki page
	 * @param updatedComment - The String content of the updated comment
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageAndAddCommentAndEditComment(Wiki communityWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage, String commentToBePosted, String updatedComment) {
		
		// Create the community wiki page, post the comment to the wiki page and then edit / update the comment
		return WikiEvents.createWikiPageAndAddCommentAndEditComment(communityWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, commentToBePosted, updatedComment);
	}
	
	/**
	 * Creates a community wiki page with the specified user as a follower
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which the wiki page is to be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithOneFollower(Wiki communityWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage,
															User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage) {
		
		// Create the community wiki page and have the specified user follow the wiki page
		return WikiEvents.createWikiPageWithOneFollower(communityWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, userFollowingWikiPage, apiUserFollowingWikiPage);
	}
	
	/**
	 * Creates a community wiki page with the specified user as a follower and then updates the wiki page
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which the wiki page is to be created and updated
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created and updated
	 * @param userCreatingWikiPage - The User instance of the user creating and updating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating and updating the wiki page
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithOneFollowerAndEditWikiPage(Wiki communityWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage, 
																		User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage) {
		
		// Create the community wiki page with specified follower and edit / update the wiki page
		return WikiEvents.createWikiPageWithOneFollowerAndEditWikiPage(communityWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, userFollowingWikiPage, apiUserFollowingWikiPage);
	}
	
	/**
	 * Creates a community wiki page with the specified user as a follower and then likes / recommends the wiki page
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which the wiki page is to be created and liked / recommended
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created and liked / recommended
	 * @param userCreatingWikiPage - The User instance of the user creating and liking / recommending the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating and liking / recommending the wiki page
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithOneFollowerAndLikeWikiPage(Wiki communityWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage, 
																		User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage) {
		
		// Create the community wiki page with specified follower and like / recommend the wiki page
		return WikiEvents.createWikiPageWithOneFollowerAndLikeWikiPage(communityWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, userFollowingWikiPage, apiUserFollowingWikiPage);
	}
	
	/**
	 * Creates a community wiki page with the specified user as a follower and then posts a comment to the wiki page
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which the wiki page is to be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param userCreatingWikiPage - The User instance of the user creating and posting a comment to the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating and posting a comment to the wiki page
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 * @param commentToBePosted - The String content of the comment to be posted to the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithOneFollowerAndAddComment(Wiki communityWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage, 
																		User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage, String commentToBePosted) {
		
		// Create the community wiki page with specified follower and post the comment to the wiki page
		return WikiEvents.createWikiPageWithOneFollowerAndAddComment(communityWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, userFollowingWikiPage, apiUserFollowingWikiPage, commentToBePosted);
	}
	
	/**
	 * Creates a community wiki page with the specified user as a follower, posts a comment to the wiki page and then edits / updates the comment
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which the wiki page is to be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page and posting and editing the comment on the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page and posting and editing the comment on the wiki page
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 * @param commentToBePosted - The String content of the comment to be posted to the wiki page
	 * @param updatedCommentContent - The String content of the updated comment
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithOneFollowerAndAddCommentAndEditComment(Wiki communityWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage, 
																		User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage, String commentToBePosted, String updatedCommentContent) {
		
		// Create the community wiki page with specified follower, post the comment to the wiki page and then edit / update the comment
		return WikiEvents.createWikiPageWithOneFollowerAndAddCommentAndEditComment(communityWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, userFollowingWikiPage, apiUserFollowingWikiPage, commentToBePosted, updatedCommentContent);
	}
	
	/**
	 * Posts a comment with mentions to a user to a community wiki page
	 * 
	 * @param wikiPage - The WikiPage instance of the community wiki page to which the comment is to be posted
	 * @param mentions - The Mentions instance of the user to be mentioned in the comment
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param apiUserPostingComment - The APIWikisHandler instance of the user posting the comment
	 * @return - The WikiComment instance
	 */
	public static WikiComment addCommentWithMentions(WikiPage wikiPage, Mentions mentions, User userPostingComment, APIWikisHandler apiUserPostingComment) {
		
		// Post the comment with mentions to the wiki page
		return WikiEvents.addCommentWithMentions(wikiPage, mentions, userPostingComment, apiUserPostingComment);
	}
	
	/**
	 * Creates a community wiki page and then posts a comment with mentions to the wiki page
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which the wiki page is to be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param mentions - The Mentions instance of the user to be mentioned in the comment
	 * @param userCreatingPageAndComment - The User instance of the user creating the wiki page and posting the comment
	 * @param apiUserCreatingPageAndComment - The APIWikisHandler instance of the user creating the wiki page and posting the comment
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageAndAddCommentWithMentions(Wiki communityWiki, BaseWikiPage baseWikiPage, Mentions mentions, User userCreatingPageAndComment, APIWikisHandler apiUserCreatingPageAndComment) {
		
		// Create the wiki page and post the comment with mentions to the wiki page
		return WikiEvents.createWikiPageAndAddCommentWithMentions(communityWiki, baseWikiPage, mentions, userCreatingPageAndComment, apiUserCreatingPageAndComment);
	}
	
	/**
	 * Allows a user to follow a community wiki
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki which is to be followed
	 * @param userToFollowWiki - The User instance of the user to follow the wiki
	 * @param apiUserToFollowWiki - The APIWikisHandler instance of the user to follow the wiki
	 */
	public static void addWikiFollowerSingleUser(Wiki communityWiki, User userToFollowWiki, APIWikisHandler apiUserToFollowWiki) {
		
		log.info("INFO: " + userToFollowWiki + " will now follow the wiki in the community with title: " + communityWiki.getTitle());
		boolean followed = apiUserToFollowWiki.createFollowCommunityWiki(communityWiki);
		
		log.info("INFO: Verify that the community wiki was followed successfully");
		Assert.assertTrue(followed, "ERROR: There was a problem with following the community wiki");
	}
	
	/**
	 * Creates a community wiki page with mentions to another user in the wiki page content / description
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki to add the wiki page to
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param mentions - The Mentions instance of the user to be mentioned in the wiki page description
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithMentions(Wiki communityWiki, BaseWikiPage baseWikiPage, Mentions mentions, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage) {
		
		// Create the community wiki page with mentions to the specified user in the wiki page content / description
		return WikiEvents.createWikiPageWithMentions(communityWiki, baseWikiPage, mentions, userCreatingWikiPage, apiUserCreatingWikiPage);
	}
	
	/**
	 * Allows a user being mentioned to follow the communities parent wiki and then creates a wiki page which includes a mention to that user
	 * PLEASE NOTE: It is critical that the user being mentioned is also a follower of the communities parent wiki or else the mentions will NOT work correctly
	 * 
	 * @param communityWiki - The Wiki instance of the community wiki in which to add the wiki page
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param mentions - The Mentions instance of the user to be mentioned in the wiki page description
	 * @param apiUserToFollowWiki - The APIWikisHandler instance of the user to be mentioned who MUST be following the parent wiki
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithMentionsToOneWikiFollower(Wiki communityWiki, BaseWikiPage baseWikiPage, Mentions mentions, APIWikisHandler apiUserToFollowWiki, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage) {
		
		// Have the user to be mentioned follow the wiki (critical step for the mentions aspect to work correctly)
		addWikiFollowerSingleUser(communityWiki, mentions.getUserToMention(), apiUserToFollowWiki);
		
		// Create the wiki page with mentions
		return createWikiPageWithMentions(communityWiki, baseWikiPage, mentions, userCreatingWikiPage, apiUserCreatingWikiPage);
	}
	
	/**
	 * Likes / recommends a community wiki page
	 * 
	 * @param wikiPage - The WikiPage instance which will be liked
	 * @param userLikingWiki - The User instance of the user who will like the wiki page
	 * @param apiUserLikingWiki - The APIWikisHandler instance of the user who will like the wiki page
	 * @return - The String content of the URL corresponding to the like event for this wiki page (necessary for unliking the wiki page again)
	 */
	public static String likeWikiPage(WikiPage wikiPage, User userLikingWiki, APIWikisHandler apiUserLikingWiki){

		// Like / recommend the community wiki page
		return WikiEvents.likeWikiPage(wikiPage, userLikingWiki, apiUserLikingWiki);
	}
	
	/**
	 * Allows a user to follow a community wiki page
	 * 
	 * @param communityWikiPage - The WikiPage instance of the community wiki page to be followed
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 */
	public static void addWikiPageFollowerSingleUser(WikiPage communityWikiPage, User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage) {
		
		// Have the specified user follow the wiki page
		WikiEvents.addWikiPageFollowerSingleUser(communityWikiPage, userFollowingWikiPage, apiUserFollowingWikiPage);
	}
	
	/**
	 * Updates a community wiki page
	 * 
	 * @param communityWikiPage - The WikiPage instance of the community wiki page to be updated
	 * @param userUpdatingWikiPage - The User instance of the user updating the community wiki page
	 * @param apiUserUpdatingWikiPage - The APIWikisHandler instance of the user updating the community wiki page
	 * @return - The updated WikiPage instance
	 */
	public static WikiPage editWikiPage(WikiPage communityWikiPage, User userUpdatingWikiPage, APIWikisHandler apiUserUpdatingWikiPage) {
		
		// Edit / update the community wiki page
		return WikiEvents.editWikiPage(communityWikiPage, userUpdatingWikiPage, apiUserUpdatingWikiPage);
	}
	
	/**
	 * Posts a comment to the specified community wiki page
	 * 
	 * @param wikiPage - The WikiPage instance of the community wiki page to which the comment is to be posted
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param apiUserPostingComment - The APIWikisHandler instance of the user posting the comment
	 * @param commentToBePosted - The String content of the comment to be posted
	 * @return - The WikiComment instance
	 */
	public static WikiComment addCommentToWikiPage(WikiPage wikiPage, User userPostingComment, APIWikisHandler apiUserPostingComment, String commentToBePosted) {
		
		// Post the comment to the community wiki page
		return WikiEvents.addCommentToWikiPage(wikiPage, userPostingComment, apiUserPostingComment, commentToBePosted);
	}
	
	/**
	 * Posts multiple comments to the specified community wiki page (all comments are posted by the same user)
	 * 
	 * @param wikiPage - The WikiPage instance of the wiki page to which the comments are to be posted
	 * @param userPostingComments - The User instance of the user posting the comments
	 * @param apiUserPostingComments - The APIWikisHandler instance of the user posting the comments
	 * @param commentsToBePosted - The String array of all comments to be posted
	 * @return - An array of WikiComment instances
	 */
	public static WikiComment[] addMultipleCommentsToWikiPage(WikiPage wikiPage, User userPostingComments, APIWikisHandler apiUserPostingComments, String[] commentsToBePosted) {
		
		// Post all of the comments to the community wiki page
		return WikiEvents.addMultipleCommentsToWikiPage(wikiPage, userPostingComments, apiUserPostingComments, commentsToBePosted);
	}
	
	/**
	 * Updates a comment posted to a wiki page
	 * 
	 * @param wikiComment - The WikiComment instance of the comment to be updated
	 * @param userUpdatingComment - The User instance of the user updating the comment
	 * @param apiUserUpdatingComment - The APIWikisHandler instance of the user updating the comment
	 * @param updatedCommentContent - The String content of the updated comment
	 * @return - The updated WikiComment instance
	 */
	public static WikiComment editCommentOnWikiPage(WikiComment wikiComment, User userUpdatingComment, APIWikisHandler apiUserUpdatingComment, String updatedCommentContent) {
		
		// Edit / update the comment posted to the wiki page
		return WikiEvents.editCommentOnWikiPage(wikiComment, userUpdatingComment, apiUserUpdatingComment, updatedCommentContent);
	}
	
	/**
	 * Posts a comment to the specified community wiki page and then updates the comment
	 * 
	 * @param wikiPage - The WikiPage instance of the community wiki page to which the comment is to be posted
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param apiUserPostingComment - The APIWikisHandler instance of the user posting the comment
	 * @param commentToBePosted - The String content of the comment to be posted
	 * @param updatedCommentContent - The String content of the updated comment
	 * @return - The updated WikiComment instance
	 */
	public static WikiComment addCommentToWikiPageAndEditComment(WikiPage wikiPage, User userPostingComment, APIWikisHandler apiUserPostingComment, String commentToBePosted, String updatedCommentContent) {
		
		// Post the comment to the wiki page
		WikiComment wikiComment = addCommentToWikiPage(wikiPage, userPostingComment, apiUserPostingComment, commentToBePosted);
		
		// Update the comment posted on the wiki page
		return editCommentOnWikiPage(wikiComment, userPostingComment, apiUserPostingComment, updatedCommentContent);
	}
	
	/**
	 * Deletes a comment posted to a community wiki page
	 * 
	 * @param wikiComment - The WikiComment instance of the comment to be deleted
	 * @param userDeletingComment - The User instance of the user deleting the comment
	 * @param apiUserDeletingComment - The APIWikisHandler instance of the user deleting the comment
	 */
	public static void deleteCommentOnWikiPage(WikiComment wikiComment, User userDeletingComment, APIWikisHandler apiUserDeletingComment) {
		
		// Delete the comment posted to the community wiki page
		WikiEvents.deleteCommentOnWikiPage(wikiComment, userDeletingComment, apiUserDeletingComment);
	}
	
	/**
	 * Navigates to the community wiki page in the UI - assumes that you have already navigated to the community in the UI beforehand
	 * 
	 * @param wikiPage - The WikiPage instance of the wiki page to be navigated to in the UI
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiWiki - The WikisUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 */
	public static void navigateToCommunityWikiPage(WikiPage wikiPage, HomepageUI ui, WikisUI uiWiki, CommunitiesUI uiCo) {
		
		// Navigate to wikis
		selectWikiFromLeftNavigationMenu(uiCo);
		
		// Wait for the wiki page to load in the UI
		waitForCommunityWikiUIPageToLoad(ui);
		
		// Click on the wiki page title to be followed in the Wikis UI left-side menu
		uiWiki.clickWikiPageTitleElement(ui, wikiPage.getTitle().trim());
		
		// Wait for the wiki page to load in the UI
		waitForCommunityWikiUIPageToLoad(ui);
	}
	
	/**
	 * Follows a wiki page using the UI - assumes that you have already navigated to the wiki page in the UI beforehand
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @return - True if all actions are completed successfully
	 */
	public static boolean followCommunityWikiPageUsingUI(HomepageUI ui) {
		
		// Reset the view back to the very top - makes the next step more robust as sometimes the 'Following Actions' link is scrolled off the screen
		ui.resetASToTop();
		
		boolean followPageLinkIsDisplayed = false;
		int numberOfClicks = 0;
		do {
			log.info("INFO: Now clicking on the 'Following Actions' button in the UI");
			ui.clickLinkWait(WikisUIConstants.Following_Actions_Button);
			
			log.info("Verify that the drop down menu containing 'Follow this Page' has appeared correctly after clicking on the 'Following Actions' button");
			followPageLinkIsDisplayed = ui.isElementVisible(WikisUIConstants.Follow_This_Page);
			
			numberOfClicks ++;
		} while (followPageLinkIsDisplayed == false && numberOfClicks < 3);
		
		if(followPageLinkIsDisplayed == false) {
			log.info("ERROR: The wiki page could NOT be followed using the UI after 3 attempts");
			return false;
		}
		
		log.info("INFO: Now selecting the 'Follow this Page' option from the drop down menu");
		ui.clickLinkWait(WikisUIConstants.Follow_This_Page);
		
		log.info("INFO: Verify that the success message for following the wiki page is displayed in Community Wikis UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.Follow_Wiki_Page_Message), 
							"ERROR: The success message for following the wiki page was NOT displayed in Community Wikis UI");
		return true;
	}
	
	/**
	 * Navigates to the wiki page in the UI and then follows the wiki page - assumes that you have already navigated to the community in the UI beforehand
	 *  
	 * @param wikiPage - The WikiPage instance of the wiki page to be navigated to and then followed using the UI
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiWiki - The WikisUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @return - True if all actions are completed successfully
	 */
	public static boolean navigateToCommunityWikiPageAndFollowWikiPage(WikiPage wikiPage, HomepageUI ui, WikisUI uiWiki, CommunitiesUI uiCo) {
		
		// Navigate to the wiki page
		navigateToCommunityWikiPage(wikiPage, ui, uiWiki, uiCo);
		
		// Follow the community wiki page
		return followCommunityWikiPageUsingUI(ui);
	}
	
	/**
	 * Logs in as the specified user, navigates to the community in the UI, navigates to the specified wiki page and then follows the wiki page
	 * 
	 * @param community - The Community instance of the community to be navigated to in the UI
	 * @param baseCommunity - The BaseCommunity instance of the community to be navigates to in the UI
	 * @param wikiPage - The WikiPage instance of the wiki page to be navigated to and then followed using the UI
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiWiki - The WikisUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param userLoggingIn - The User instance of the user logging in and navigating to and following the wiki page
	 * @param apiUserLoggingIn - The APICommunitiesHandler instance of the user logging in and navigating to and following the wiki page
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean loginAndNavigateToCommunityAndFollowCommunityWikiPage(Community community, BaseCommunity baseCommunity, WikiPage wikiPage, HomepageUI ui, WikisUI uiWiki, CommunitiesUI uiCo,
																				User userLoggingIn, APICommunitiesHandler apiUserLoggingIn, boolean preserveInstance) {
		// Log in to Communities UI and navigate to the community																		
		CommunityEvents.loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userLoggingIn, apiUserLoggingIn, preserveInstance);
		
		// Navigate to the wiki page and follow the wiki page
		return navigateToCommunityWikiPageAndFollowWikiPage(wikiPage, ui, uiWiki, uiCo);
	}
	
	/**
	 * Retrieves the Wiki instance of the default-created Wiki in the specified community
	 * 
	 * @param community - The Community instance of the community whose Wiki is to be retrieved
	 * @param apiCommunityWikiOwner - The APIWikisHandler instance of the user who owns the community wiki
	 * @return - The Wiki instance of the community wiki if all operations are successful
	 */
	public static Wiki getCommunityWiki(Community community, APIWikisHandler apiCommunityWikiOwner) {
		
		int numberOfTries = 1;
		boolean communityWikiRetrieved = false;
		Wiki communityWiki = null;
		do {
			log.info("INFO: Attempt " + numberOfTries + " of 3 - Retrieving the community Wiki instance");
			communityWiki = apiCommunityWikiOwner.getCommunityWiki(community);
			
			if(communityWiki == null) {
				log.info("ERROR: Could not retrieve the community Wiki instance during this attempt");
			} else {
				log.info("INFO: The community Wiki instance has been successfully retrieved");
				communityWikiRetrieved = true;
			}
			numberOfTries ++;
		} while(numberOfTries <= 3 && communityWikiRetrieved == false);
		
		log.info("INFO: Verify that the community wiki has been retrieved successfully");
		Assert.assertTrue(communityWikiRetrieved, 
							"ERROR: The community Wiki instance could NOT be retrieved after 3 successive attempts");
		return communityWiki;
	}
	
	/**
	 * Handles the specified user unliking the specified community wiki page
	 * 
	 * @param wikiPageBeingUnliked - The WikiPage instance of the wiki page to be unliked
	 * @param likeWikiPageURL - The URL returned from the API method for liking the wiki page (used to unlike the wiki page again)
	 * @param userUnlikingWikiPage - The User instance of the user to unlike the wiki page
	 * @param apiUserUnlikingWikiPage - The APIWikisHandler instance of the user to unlike the wiki page
	 */
	public static void unlikeWikiPage(WikiPage wikiPageBeingUnliked, String likeWikiPageURL, User userUnlikingWikiPage, APIWikisHandler apiUserUnlikingWikiPage) {
		
		// Have the specified user unlike the wiki page
		WikiEvents.unlikeWikiPage(wikiPageBeingUnliked, likeWikiPageURL, userUnlikingWikiPage, apiUserUnlikingWikiPage);
	}
	
	/**
	 * Selects the "Wiki" option from the left-side navigation menu in Communities UI
	 * 
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 */
	private static void selectWikiFromLeftNavigationMenu(CommunitiesUI uiCo) {
		
		log.info("INFO: Select 'Wiki' from the left navigation menu");
		Community_LeftNav_Menu.WIKI.select(uiCo);
	}
	
	/**
	 * Waits for the Wiki page in Communities UI to load by ensuring the 'Feed for this Page' text is displayed
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	private static void waitForCommunityWikiUIPageToLoad(HomepageUI ui) {
		
		log.info("INFO: Waiting for the Community Wiki UI page to load");
		ui.fluentWaitPresent(WikisUIConstants.Feed_For_This_Page_Link);
	}
}
