package com.ibm.conn.auto.util.eventBuilder.wikis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.testng.Assert;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseWiki;
import com.ibm.conn.auto.appobjects.base.BaseWikiPage;
import com.ibm.conn.auto.lcapi.APIWikisHandler;
import com.ibm.conn.auto.util.Mentions;
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

public class WikiEvents {

	private static Logger log = LoggerFactory.getLogger(WikiEvents.class);
	
	/**
	 * Creates a standalone wiki
	 * 
	 * @param baseWiki - The BaseWiki instance of the wiki to be created
	 * @param userCreatingWiki - The User instance of the user creating the wiki
	 * @param apiUserCreatingWiki - The APIWikisHandler instance of the user creating the wiki
	 * @return - The Wiki instance
	 */
	public static Wiki createWiki(BaseWiki baseWiki, User userCreatingWiki, APIWikisHandler apiUserCreatingWiki) {
		
		log.info("INFO: " + userCreatingWiki.getDisplayName() + " will now create a wiki with title: " + baseWiki.getName());
		Wiki wiki = apiUserCreatingWiki.createWiki(baseWiki);
		
		log.info("INFO: Verify that the wiki was created successfully");
		Assert.assertNotNull(wiki, "ERROR: The wiki was NOT created successfully and was returned as null");
		
		return wiki;
	}
	
	/**
	 * Creates a standalone wiki and allows one specified user to follow the wiki
	 * 
	 * @param baseWiki - The BaseWiki instance of the wiki to be created
	 * @param userCreatingWiki - The User instance of the user creating the wiki
	 * @param apiUserCreatingWiki - The APIWikisHandler instance of the user creating the wiki
	 * @param userToFollowWiki - The User instance of the user to follow the wiki
	 * @param apiUserToFollowWiki - The APIWikisHandler instance of the user to follow the wiki
	 * @return - The Wiki instance
	 */
	public static Wiki createWikiWithOneFollower(BaseWiki baseWiki, User userCreatingWiki, APIWikisHandler apiUserCreatingWiki, User userToFollowWiki, APIWikisHandler apiUserToFollowWiki) {
		
		// Create the wiki
		Wiki wiki = createWiki(baseWiki, userCreatingWiki, apiUserCreatingWiki);
		
		// Have the specified user follow the wiki
		addWikiFollowerSingleUser(wiki, userToFollowWiki, apiUserToFollowWiki);
		
		return wiki;
	}
	
	/**
	 * Creates a wiki page in a standalone wiki
	 * 
	 * @param parentWiki - The Wiki instance of the wiki in which the wiki page will be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPage(Wiki parentWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage) {
		
		log.info("INFO: " + userCreatingWikiPage.getDisplayName() + " will now create a wiki page with title: " + baseWikiPage.getName());
		WikiPage wikiPage = apiUserCreatingWikiPage.createWikiPage(baseWikiPage, parentWiki);
		
		log.info("INFO: Verify that the wiki page has been created successfully");
		Assert.assertNotNull(wikiPage, "ERROR: The wiki page was NOT created successfully and was returned as null");
		
		return wikiPage;
	}
	
	/**
	 * Creates a wiki page in a standalone wiki and allows the specified user to follow the wiki page
	 * 
	 * @param parentWiki - The Wiki instance of the wiki in which the wiki page will be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithOneFollower(Wiki parentWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage,
															User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage) {
		// Create the wiki page in the standalone wiki
		WikiPage wikiPage = createWikiPage(parentWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
		
		// Have the specified user follow the wiki page
		addWikiPageFollowerSingleUser(wikiPage, userFollowingWikiPage, apiUserFollowingWikiPage);
		
		return wikiPage;
	}
	
	/**
	 * Creates a wiki page in a standalone wiki, allows the specified user to follow the wiki page and then updates the wiki page
	 * 
	 * @param parentWiki - The Wiki instance of the wiki in which the wiki page will be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created and updated
	 * @param userCreatingWikiPage - The User instance of the user creating and updating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating and updating the wiki page
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithOneFollowerAndEditWikiPage(Wiki parentWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage,
																		User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage) {
		// Create the wiki page in the standalone wiki and have the specified user follow the wiki page
		WikiPage wikiPage = createWikiPageWithOneFollower(parentWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, userFollowingWikiPage, apiUserFollowingWikiPage);
		
		// Edit / update the wiki page
		return editWikiPage(wikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
	}
	
	/**
	 * Creates a wiki page in a standalone wiki, allows the specified user to follow the wiki page and then likes / recommends the wiki page
	 * 
	 * @param parentWiki - The Wiki instance of the wiki in which the wiki page will be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created and updated
	 * @param userCreatingWikiPage - The User instance of the user creating and liking / recommending the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating and liking / recommending the wiki page
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithOneFollowerAndLikeWikiPage(Wiki parentWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage,
																		User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage) {
		// Create the wiki page in the standalone wiki and have the specified user follow the wiki page
		WikiPage wikiPage = createWikiPageWithOneFollower(parentWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, userFollowingWikiPage, apiUserFollowingWikiPage);
		
		// Like / recommend the wiki page
		likeWikiPage(wikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
		
		return wikiPage;
	}
	
	/**
	 * Creates a wiki page in a standalone wiki, allows the specified user to follow the wiki page and then posts a comment to the wiki page
	 * 
	 * @param parentWiki - The Wiki instance of the wiki in which the wiki page will be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created and updated
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page and posting the comment
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page and posting the comment
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 * @param comment - The String content of the comment to be posted to the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithOneFollowerAndAddComment(Wiki parentWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage,
																		User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage, String comment) {
		// Create the wiki page in the standalone wiki and have the specified user follow the wiki page
		WikiPage wikiPage = createWikiPageWithOneFollower(parentWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, userFollowingWikiPage, apiUserFollowingWikiPage);
		
		// Post the comment to the wiki page
		addCommentToWikiPage(wikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, comment);
		
		return wikiPage;
	}
	
	/**
	 * Creates a wiki page in a standalone wiki, allows the specified user to follow the wiki page and then posts a comment to the wiki page and updates the comment
	 * 
	 * @param parentWiki - The Wiki instance of the wiki in which the wiki page will be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created and updated
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page and posting / updating the comment
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page and posting / updating the comment
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 * @param comment - The String content of the comment to be posted to the wiki page
	 * @param updatedComment - The String content of the updated comment to be posted to the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithOneFollowerAndAddCommentAndEditComment(Wiki parentWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage,
																					User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage, String comment, String updatedComment) {
		// Create the wiki page in the standalone wiki and have the specified user follow the wiki page
		WikiPage wikiPage = createWikiPageWithOneFollower(parentWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, userFollowingWikiPage, apiUserFollowingWikiPage);
		
		// Post the comment to the wiki page
		WikiComment wikiComment = addCommentToWikiPage(wikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, comment);
		
		// Edit / update the comment
		editCommentOnWikiPage(wikiComment, userCreatingWikiPage, apiUserCreatingWikiPage, updatedComment);
		
		return wikiPage;
	}
	
	/**
	 * Allows a user to follow a wiki
	 * 
	 * @param wikiToBeFollowed - The Wiki instance of the wiki to be followed
	 * @param userToFollowWiki - The User instance of the user to follow the wiki
	 * @param apiUserToFollowWiki - The APIWikisHandler instance of the user to follow the wiki
	 */
	public static void addWikiFollowerSingleUser(Wiki wikiToBeFollowed, User userToFollowWiki, APIWikisHandler apiUserToFollowWiki) {
		
		log.info("INFO: " + userToFollowWiki + " will now follow the wiki with title: " + wikiToBeFollowed.getTitle());
		boolean followed = apiUserToFollowWiki.createFollow(wikiToBeFollowed);
		
		log.info("INFO: Verify that the wiki was followed successfully");
		Assert.assertTrue(followed, "ERROR: There was a problem with following the wiki");
	}
	
	/**
	 * Allows a user to follow a wiki page
	 * 
	 * @param wikiPageToBeFollowed - The WikiPage instance of the wiki page to be followed
	 * @param userFollowingWikiPage - The User instance of the user to follow the wiki page
	 * @param apiUserFollowingWikiPage - The APIWikisHandler instance of the user to follow the wiki page
	 */
	public static void addWikiPageFollowerSingleUser(WikiPage wikiPageToBeFollowed, User userFollowingWikiPage, APIWikisHandler apiUserFollowingWikiPage) {
		
		log.info("INFO: " + userFollowingWikiPage.getDisplayName() + " will now follow the wiki page with title: " + wikiPageToBeFollowed.getTitle());
		boolean followed = apiUserFollowingWikiPage.createFollowWikiPage(wikiPageToBeFollowed);
		
		log.info("INFO: Verify that the wiki page was followed successfully");
		Assert.assertTrue(followed, "ERROR: There was a problem with following the wiki page with title: " + wikiPageToBeFollowed.getTitle());
	}
	
	/**
	 * Creates a wiki page with mentions to another user in the wiki page content / description
	 * 
	 * @param parentWiki - The Wiki instance of the parent wiki in which the wiki page will be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param mentions - The Mentions instance of the user to be mentioned in the wiki page description
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithMentions(Wiki parentWiki, BaseWikiPage baseWikiPage, Mentions mentions, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage) {
		
		log.info("INFO: " + userCreatingWikiPage + " will now create a wiki page with mentions to " + mentions.getUserToMention().getDisplayName() + " in the page description");
		WikiPage wikiPage = apiUserCreatingWikiPage.createWikiPageWithMentions(parentWiki, baseWikiPage, mentions);
		
		log.info("INFO: Verify that the wiki page was created successfully");
		Assert.assertNotNull(wikiPage, "ERROR: The wiki page was NOT created successfully and was returned as null");
		
		return wikiPage;
	}
	
	/**
	 * Allows a user being mentioned to follow the parent wiki and then creates a wiki page which includes a mention to that user in the wiki page content / description
	 * PLEASE NOTE: It is critical that the user being mentioned is also a follower of the parent wiki or else the mentions will NOT work correctly
	 * 
	 * @param parentWiki - The Wiki instance of the parent wiki in which the wiki page will be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param mentions - The Mentions instance of the user to be mentioned in the wiki page description
	 * @param apiUserToFollowWiki - The APIWikisHandler instance of the user to be mentioned who MUST be following the parent wiki
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageWithMentionsToOneWikiFollower(Wiki parentWiki, BaseWikiPage baseWikiPage, Mentions mentions, APIWikisHandler apiUserToFollowWiki, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage) {
		
		// Have the user to be mentioned follow the wiki (critical step for the mentions aspect to work correctly)
		addWikiFollowerSingleUser(parentWiki, mentions.getUserToMention(), apiUserToFollowWiki);
		
		// Create the wiki page with mentions
		return createWikiPageWithMentions(parentWiki, baseWikiPage, mentions, userCreatingWikiPage, apiUserCreatingWikiPage);
	}
	
	/**
	 * Posts a comment with mentions to a user to a wiki page
	 * 
	 * @param wikiPage - The WikiPage instance of the wiki page to which the comment is to be posted
	 * @param mentions - The Mentions instance of the user to be mentioned in the comment
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param apiUserPostingComment - The APIWikisHandler instance of the user posting the comment
	 * @return - The WikiComment instance
	 */
	public static WikiComment addCommentWithMentions(WikiPage wikiPage, Mentions mentions, User userPostingComment, APIWikisHandler apiUserPostingComment) {
		
		log.info("INFO: " + userPostingComment.getDisplayName() + " will now post a comment to the wiki page with mentions to " + mentions.getUserToMention().getDisplayName());
		WikiComment wikiComment = apiUserPostingComment.addMentionCommentToWikiPage(wikiPage, mentions);
		
		log.info("INFO: Verify that the comment posted to the wiki page successfully");
		Assert.assertNotNull(wikiComment, "ERROR: The wiki comment was NOT created successfully and was returned as null");
		
		return wikiComment;
	}
	
	/**
	 * Creates a wiki page in a standalone wiki and then posts a comment with mentions to the wiki page
	 * 
	 * @param parentWiki - The Wiki instance of the parent wiki in which the wiki page is to be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param mentions - The Mentions instance of the user to be mentioned in the comment
	 * @param userCreatingPageAndComment - The User instance of the user creating the wiki page and posting the comment
	 * @param apiUserCreatingPageAndComment - The APIWikisHandler instance of the user creating the wiki page and posting the comment
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageAndAddCommentWithMentions(Wiki parentWiki, BaseWikiPage baseWikiPage, Mentions mentions, User userCreatingPageAndComment, APIWikisHandler apiUserCreatingPageAndComment) {
		
		// Create a new wiki page in the standalone wiki
		WikiPage wikiPage = createWikiPage(parentWiki, baseWikiPage, userCreatingPageAndComment, apiUserCreatingPageAndComment);
		
		// Post the comment with mentions to the wiki page
		addCommentWithMentions(wikiPage, mentions, userCreatingPageAndComment, apiUserCreatingPageAndComment);
		
		return wikiPage;
	}
	
	/**
	 * Likes / recommends a wiki page
	 * 
	 * @param wikiPage - The WikiPage instance which will be liked
	 * @param userLikingWiki - The User instance of the user who will like the wiki page
	 * @param apiUserLikingWiki - The APIWikisHandler instance of the user who will like the wiki page
	 * @return - The String content of the URL corresponding to the like event for this wiki page (necessary for unliking the wiki page again)
	 */
	public static String likeWikiPage(WikiPage wikiPage, User userLikingWiki, APIWikisHandler apiUserLikingWiki){

		log.info("INFO: " + userLikingWiki.getDisplayName() + " like the wiki page " + wikiPage.getTitle() + " using API method");
		String likeWikiPageURL = apiUserLikingWiki.likeWikiPage(wikiPage);

		log.info("INFO: Verify that the wiki page was liked / recommended successfully");
		Assert.assertNotNull(likeWikiPageURL, 
								"ERROR: The wiki page was NOT liked / recommended as expected and was returned as null");
		return likeWikiPageURL;
	}
	
	/**
	 * Updates a wiki page
	 * 
	 * @param wikiPage - The WikiPage instance of the wiki page to be updated
	 * @param userUpdatingWikiPage - The User instance of the user updating the wiki page
	 * @param apiUserUpdatingWikiPage - The APIWikisHandler instance of the user updating the wiki page
	 * @return - The updated WikiPage instance
	 */
	public static WikiPage editWikiPage(WikiPage wikiPage, User userUpdatingWikiPage, APIWikisHandler apiUserUpdatingWikiPage) {
		
		log.info("INFO: " + userUpdatingWikiPage.getDisplayName() + " will now update the wiki page with title: " + wikiPage.getTitle());
		WikiPage updatedWikiPage = apiUserUpdatingWikiPage.editWikiPage(wikiPage);
		
		log.info("INFO: Verify that the wiki page has been updated successfully");
		Assert.assertNotNull(updatedWikiPage, 
								"ERROR: The wiki page was NOT updated and was returned as null");
		return updatedWikiPage;
	}
	
	/**
	 * Posts a comment to the specified wiki page
	 * 
	 * @param wikiPage - The WikiPage instance of the wiki page to which the comment is to be posted
	 * @param userPostingComment - The User instance of the user posting the comment
	 * @param apiUserPostingComment - The APIWikisHandler instance of the user posting the comment
	 * @param commentToBePosted - The String content of the comment to be posted
	 * @return - The WikiComment instance
	 */
	public static WikiComment addCommentToWikiPage(WikiPage wikiPage, User userPostingComment, APIWikisHandler apiUserPostingComment, String commentToBePosted) {
		
		log.info("INFO: " + userPostingComment.getDisplayName() + " will now post a comment to the wiki page with title: " + wikiPage.getTitle());
		WikiComment wikiComment = apiUserPostingComment.addCommentToWikiPage(wikiPage, commentToBePosted);
		
		log.info("INFO: Verify that the comment has been successfully posted to the wiki page");
		Assert.assertNotNull(wikiComment, 
								"ERROR: The comment was NOT posted to the wiki page and was returned as null");
		return wikiComment;
	}
	
	/**
	 * Posts multiple comments to the specified wiki page (all comments are posted by the same user)
	 * 
	 * @param wikiPage - The WikiPage instance of the wiki page to which the comments are to be posted
	 * @param userPostingComments - The User instance of the user posting the comments
	 * @param apiUserPostingComments - The APIWikisHandler instance of the user posting the comments
	 * @param commentsToBePosted - The String array of all comments to be posted
	 * @return - An array of WikiComment instances
	 */
	public static WikiComment[] addMultipleCommentsToWikiPage(WikiPage wikiPage, User userPostingComments, APIWikisHandler apiUserPostingComments, String[] commentsToBePosted) {
		
		// Initialise the array of WikiComment instances to be returned
		WikiComment[] wikiComments = new WikiComment[commentsToBePosted.length];
		
		int index = 0;
		while(index < commentsToBePosted.length) {
			// Post the comment to the wiki page and store the resulting WikiComment instance in the array
			wikiComments[index] = addCommentToWikiPage(wikiPage, userPostingComments, apiUserPostingComments, commentsToBePosted[index]);
			index ++;
		}
		return wikiComments;
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
		
		WikiComment updatedWikiComment = apiUserUpdatingComment.editCommentOnWikiPage(wikiComment, updatedCommentContent);
		
		log.info("INFO: Verify that the comment has been successfully updated on the wiki page");
		Assert.assertNotNull(updatedWikiComment, 
								"ERROR: The comment was NOT updated as expected and was returned as null");
		return updatedWikiComment;
	}
	
	/**
	 * Deletes a comment posted to a wiki page
	 * 
	 * @param wikiComment - The WikiComment instance of the comment to be deleted
	 * @param userDeletingComment - The User instance of the user deleting the comment
	 * @param apiUserDeletingComment - The APIWikisHandler instance of the user deleting the comment
	 */
	public static void deleteCommentOnWikiPage(WikiComment wikiComment, User userDeletingComment, APIWikisHandler apiUserDeletingComment) {
		
		log.info("INFO: " + userDeletingComment.getDisplayName() + " will now delete the comment posted to the wiki page with content: " + wikiComment.getContent().trim());
		boolean commentDeleted = apiUserDeletingComment.deleteWikiPageComment(wikiComment);
		
		log.info("INFO: Verify that the comment has been successfully deleted from the wiki page");
		Assert.assertTrue(commentDeleted, 
							"ERROR: The comment was NOT deleted from the wiki page as expected");
	}
	
	/**
	 * Creates a wiki page and then edits / updates the wiki page
	 * 
	 * @param parentWiki - The Wiki instance on the parent wiki in which the wiki page is to be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageAndEditWikiPage(Wiki parentWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage) {
		
		// Create the wiki page in the standalone wiki
		WikiPage wikiPage = createWikiPage(parentWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
		
		// Edit / update the wiki page
		return editWikiPage(wikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
	}
	
	/**
	 * Creates a wiki page and then likes / recommends the wiki page
	 * 
	 * @param parentWiki - The Wiki instance on the parent wiki in which the wiki page is to be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param userCreatingWikiPage - The User instance of the user creating and liking the wiki page
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating and liking the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageAndLikeWikiPage(Wiki parentWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage) {
		
		// Create the wiki page in the standalone wiki
		WikiPage wikiPage = createWikiPage(parentWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
		
		// Like / recommend the wiki page
		likeWikiPage(wikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
		
		return wikiPage;
	}
	
	/**
	 * Creates a wiki page and then posts a comment to the wiki page
	 * 
	 * @param parentWiki - The Wiki instance on the parent wiki in which the wiki page is to be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page and posting the comment
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page and posting the comment
	 * @param comment - The String content of the comment to be posted to the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageAndAddComment(Wiki parentWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage, String comment) {
		
		// Create the wiki page in the standalone wiki
		WikiPage wikiPage = createWikiPage(parentWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
		
		// Post the comment to the wiki page
		addCommentToWikiPage(wikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, comment);
		
		return wikiPage;
	}
	
	/**
	 * Creates a wiki page and then posts a comment to the wiki page
	 * 
	 * @param parentWiki - The Wiki instance on the parent wiki in which the wiki page is to be created
	 * @param baseWikiPage - The BaseWikiPage instance of the wiki page to be created
	 * @param userCreatingWikiPage - The User instance of the user creating the wiki page and posting / updating the comment
	 * @param apiUserCreatingWikiPage - The APIWikisHandler instance of the user creating the wiki page and posting / updating the comment
	 * @param comment - The String content of the comment to be posted to the wiki page
	 * @param updatedComment - The String content of the updated comment to be posted to the wiki page
	 * @return - The WikiPage instance
	 */
	public static WikiPage createWikiPageAndAddCommentAndEditComment(Wiki parentWiki, BaseWikiPage baseWikiPage, User userCreatingWikiPage, APIWikisHandler apiUserCreatingWikiPage, String comment, String updatedComment) {
		
		// Create the wiki page in the standalone wiki
		WikiPage wikiPage = createWikiPage(parentWiki, baseWikiPage, userCreatingWikiPage, apiUserCreatingWikiPage);
		
		// Post the comment to the wiki page
		WikiComment wikiComment = addCommentToWikiPage(wikiPage, userCreatingWikiPage, apiUserCreatingWikiPage, comment);
		
		// Edit / update the comment
		editCommentOnWikiPage(wikiComment, userCreatingWikiPage, apiUserCreatingWikiPage, updatedComment);
		
		return wikiPage;
	}
	
	/**
	 * Deletes a standalone wiki
	 * 
	 * @param wikiToBeDeleted - The Wiki instance of the wiki to be deleted
	 * @param userDeletingWiki - The User instance of the user deleting the wiki
	 * @param apiUserDeletingWiki - The APIWikisHandler instance of the user deleting the wiki
	 */
	public static void deleteWiki(Wiki wikiToBeDeleted, User userDeletingWiki, APIWikisHandler apiUserDeletingWiki) {
		
		log.info("INFO: " + userDeletingWiki.getDisplayName() + " will now delete the standalone wiki with title: " + wikiToBeDeleted.getTitle());
		apiUserDeletingWiki.deleteWiki(wikiToBeDeleted);
	}
	
	/**
	 * Deletes a wiki page from a standalone wiki
	 * 
	 * @param parentWiki - The Wiki instance of the parent wiki which contains the wiki page to be deleted
	 * @param wikiPageToBeDeleted - The WikiPage instance of the wiki page to be deleted
	 * @param userDeletingWikiPage - The User instance of the user deleting the wiki page
	 * @param apiUserDeletingWikiPage - The APIWikisHandler instance of the user deleting the wiki page
	 */
	public static void deleteWikiPage(Wiki parentWiki, WikiPage wikiPageToBeDeleted, User userDeletingWikiPage, APIWikisHandler apiUserDeletingWikiPage) {
		
		log.info("INFO: " + userDeletingWikiPage.getDisplayName() + " will now delete the wiki page with title: " + wikiPageToBeDeleted.getTitle());
		apiUserDeletingWikiPage.deleteWikiPage(parentWiki, wikiPageToBeDeleted);
	}
	
	/**
	 * Handles the specified user unliking the specified wiki page
	 * 
	 * @param wikiPageBeingUnliked - The WikiPage instance of the wiki page to be unliked
	 * @param likeWikiPageURL - The URL returned from the API method for liking the wiki page (used to unlike the wiki page again)
	 * @param userUnlikingWikiPage - The User instance of the user to unlike the wiki page
	 * @param apiUserUnlikingWikiPage - The APIWikisHandler instance of the user to unlike the wiki page
	 */
	public static void unlikeWikiPage(WikiPage wikiPageBeingUnliked, String likeWikiPageURL, User userUnlikingWikiPage, APIWikisHandler apiUserUnlikingWikiPage) {
		
		log.info("INFO: " + userUnlikingWikiPage.getDisplayName() + " will now unlike the wiki page with title: " + wikiPageBeingUnliked.getTitle());
		boolean unlikedWikiPage = apiUserUnlikingWikiPage.unlikeWikiPage(likeWikiPageURL);
		
		log.info("INFO: Verify that the wiki page has been unliked successfully");
		Assert.assertTrue(unlikedWikiPage, 
							"ERROR: The wiki page was NOT unliked - a negative response was returned from the API method");
	}
}