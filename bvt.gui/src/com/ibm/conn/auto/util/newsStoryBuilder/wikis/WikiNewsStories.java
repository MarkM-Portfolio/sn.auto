package com.ibm.conn.auto.util.newsStoryBuilder.wikis;

import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.newsStoryBuilder.BaseNewsStoryBuilder;
import com.ibm.conn.auto.webui.HomepageUI;

public class WikiNewsStories extends BaseNewsStoryBuilder {

	/**
	 * Retrieves the news story corresponding the the 'Added You To Wiki As An Editor' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiTitle - The String content containing the title of the wiki to which the user has been added as an editor
	 * @param userNameCommentingOnWikiPage - The String content containing the user name of the user who added the user to the wiki as a member
	 * @return - The String content containing the 'Added You To Wiki As An Editor' news story
	 */
	public static String getAddedYouAsAnEditorNewsStory(HomepageUI ui, String wikiTitle, String userNameAddedUserAsMember) {
		return createNewsStory(ui, Data.ADDED_YOU_AS_EDITOR, wikiTitle, null, userNameAddedUserAsMember);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Comment On Their Own Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been commented on
	 * @param wikiTitle - The String content containing the title of the wiki in which the wiki page has been commented on
	 * @param userNameCommentingOnWikiPage - The String content containing the user name of the user who has commented on the wiki page
	 * @return - The String content containing the 'Comment On Their Own Wiki Page' news story
	 */
	public static String getCommentOnTheirOwnWikiPageNewsStory(HomepageUI ui, String wikiPageTitle, String wikiTitle, String userNameCommentingOnWikiPage) {
		return createNewsStory(ui, Data.COMMENT_BY_USER_THEIR_OWN_WIKI_PAGE, wikiPageTitle, wikiTitle, userNameCommentingOnWikiPage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 and User 2 Commented On Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserCommenting - The String content containing the user name of the current user to comment on the wiki page
	 * @param previousUserCommenting - The String content containing the user name of the last / previous user to comment on the wiki page
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been commented on
	 * @param wikiTitle - The String content containing the title of the wiki in which the wiki page has been commented on
	 * @return - The String content containing the 'User 1 and User 2 Commented On Your Wiki Page' news story
	 */
	public static String getCommentOnYourWikiPageNewsStory_TwoUsers(HomepageUI ui, String currentUserCommenting, String previousUserCommenting, String wikiPageTitle, String wikiTitle) {
		return createNewsStory(ui, Data.COMMENT_YOUR_WIKI_PAGE_TWO_COMMENTERS, previousUserCommenting, wikiPageTitle, wikiTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Commented On Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been commented on
	 * @param wikiTitle - The String content containing the title of the wiki in which the wiki page has been commented on
	 * @param userNameCommentingOnWikiPage - The String content containing the user name of the user who has commented on the wiki page
	 * @return - The String content containing the 'User Commented On Your Wiki Page' news story
	 */
	public static String getCommentOnYourWikiPageNewsStory_User(HomepageUI ui, String wikiPageTitle, String wikiTitle, String userNameCommentingOnWikiPage) {
		return createNewsStory(ui, Data.COMMENT_YOUR_WIKI_PAGE, wikiPageTitle, wikiTitle, userNameCommentingOnWikiPage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'UserName and X Others Commented On Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserCommenting - The String content containing the user name of the current user to comment on the wiki page
	 * @param numberOfOtherUsersCommented - The String content containing the number of other users who have commented on the wiki page
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been commented on
	 * @param wikiTitle - The String content containing the title of the wiki in which the wiki page has been commented on
	 * @return - The String content containing the 'UserName and X Others Commented On Your Wiki Page' news story
	 */
	public static String getCommentOnYourWikiPageNewsStory_UserAndMany(HomepageUI ui, String currentUserCommenting, String numberOfOtherUsersCommented, String wikiPageTitle, String wikiTitle) {
		return createNewsStory(ui, Data.COMMENT_YOUR_WIKI_PAGE_MANY, numberOfOtherUsersCommented, wikiPageTitle, wikiTitle, currentUserCommenting);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Commented On Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been commented on
	 * @param wikiTitle - The String content containing the title of the wiki in which the wiki page has been commented on
	 * @param userNameCommentingOnWikiPage - The String content containing the user name of the user who has commented on the wiki page
	 * @return - The String content containing the 'User And You Commented On Your Wiki Page' news story
	 */
	public static String getCommentOnYourWikiPageNewsStory_UserAndYou(HomepageUI ui, String wikiPageTitle, String wikiTitle, String userNameCommentingOnWikiPage) {
		return createNewsStory(ui, Data.COMMENT_YOUR_WIKI_PAGE_YOU_OTHER, wikiPageTitle, wikiTitle, userNameCommentingOnWikiPage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Commented On Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been commented on
	 * @param wikiTitle - The String content containing the title of the wiki in which the wiki page has been commented on
	 * @return - The String content containing the 'You Commented On Your Wiki Page' news story
	 */
	public static String getCommentOnYourWikiPageNewsStory_You(HomepageUI ui, String wikiPageTitle, String wikiTitle) {
		return createNewsStory(ui, Data.COMMENT_YOUR_WIKI_PAGE_YOU, wikiPageTitle, wikiTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Commented On Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherUsersCommented - The String content containing the number of other users who have commented on the wiki page
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been commented on
	 * @param wikiTitle - The String content containing the title of the wiki in which the wiki page has been commented on
	 * @return - The String content containing the 'You And X Others Commented On Your Wiki Page' news story
	 */
	public static String getCommentOnYourWikiPageNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherUsersCommented, String wikiPageTitle, String wikiTitle) {
		return createNewsStory(ui, Data.COMMENT_YOUR_WIKI_PAGE_YOU_MANY, numberOfOtherUsersCommented, wikiPageTitle, wikiTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Create Wiki' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiTitle - The String content containing the title of the wiki that has been created
	 * @param userNameCreatingWiki - The String content containing the user name of the user who has created the wiki
	 * @return - The String content containing the 'Create Wiki' news story
	 */
	public static String getCreateWikiNewsStory(HomepageUI ui, String wikiTitle, String userNameCreatingWiki) {
		return createNewsStory(ui, Data.CREATE_WIKI, wikiTitle, null, userNameCreatingWiki);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Create Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been created
	 * @param wikiTitle - The String content containing the title of the wiki in which the wiki page has been created
	 * @param userNameCreatingWikiPage - The String content containing the user name of the user who has created the wiki page
	 * @return - The String content containing the 'Create Wiki Page' news story
	 */
	public static String getCreateWikiPageNewsStory(HomepageUI ui, String wikiPageTitle, String wikiTitle, String userNameCreatingWikiPage) {
		return createNewsStory(ui, Data.CREATE_WIKI_PAGE, wikiPageTitle, wikiTitle, userNameCreatingWikiPage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Create Wiki Welcome Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiTitle - The String content containing the title of the wiki in which the welcome page has been created
	 * @param userNameCreatingWikiPage - The String content containing the user name of the user who has created the wiki welcome page
	 * @return - The String content containing the 'Create Wiki Welcome Page' news story
	 */
	public static String getCreateWikiWelcomePageNewsStory(HomepageUI ui, String wikiTitle, String userNameCreatingWikiPage) {
		return createNewsStory(ui, Data.WIKI_WELCOME_PAGE_CREATED, wikiTitle, wikiTitle, userNameCreatingWikiPage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Like Their Own Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been liked
	 * @param wikiTitle - The String content containing the title of the wiki which contains the liked wiki page
	 * @param userNameLikingWikiPage - The String content containing the user name of the user who has liked the wiki page
	 * @return - The String content containing the 'Like Their Own Wiki Page' news story
	 */
	public static String getLikeTheirOwnWikiPageNewsStory(HomepageUI ui, String wikiPageTitle, String wikiTitle, String userNameLikingWikiPage) {
		return createNewsStory(ui, Data.LIKE_THEIR_OWN_WIKI_PAGE, wikiPageTitle, wikiTitle, userNameLikingWikiPage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User 1 and User 2 Like Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking -  The String content containing the user name of the current user to like the wiki page
	 * @param previousUserLiking -  The String content containing the user name of the last / previous user to like the wiki page
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been liked
	 * @param wikiTitle - The String content containing the title of the wiki which contains the liked wiki page
	 * @return - The String content containing the 'User 1 and User 2 Like Your Wiki Page' news story
	 */
	public static String getLikeYourWikiPageNewsStory_TwoUsers(HomepageUI ui, String currentUserLiking, String previousUserLiking, String wikiPageTitle, String wikiTitle) {
		return createNewsStory(ui, Data.LIKE_YOUR_WIKI_PAGE_TWO_LIKES, previousUserLiking, wikiPageTitle, wikiTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User Liked Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been liked
	 * @param wikiTitle - The String content containing the title of the wiki which contains the liked wiki page
	 * @param userNameLikingWikiPage - The String content containing the user name of the user who has liked the wiki page
	 * @return - The String content containing the 'User Liked Your Wiki Page' news story
	 */
	public static String getLikeYourWikiPageNewsStory_User(HomepageUI ui, String wikiPageTitle, String wikiTitle, String userNameLikingWikiPage) {
		return createNewsStory(ui, Data.LIKE_YOUR_WIKI_PAGE, wikiPageTitle, wikiTitle, userNameLikingWikiPage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'UserName and X Others Like Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param currentUserLiking -  The String content containing the user name of the current user to like the wiki page
	 * @param numberOfOtherUsersLiked - The String content containing the number of other users who have liked the wiki page
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been liked
	 * @param wikiTitle - The String content containing the title of the wiki which contains the liked wiki page
	 * @return - The String content containing the 'UserName and X Others Like Your Wiki Page' news story
	 */
	public static String getLikeYourWikiPageNewsStory_UserAndMany(HomepageUI ui, String currentUserLiking, String numberOfOtherUsersLiked,  String wikiPageTitle, String wikiTitle) {
		return createNewsStory(ui, Data.LIKE_YOUR_WIKI_PAGE_MANY, numberOfOtherUsersLiked, wikiPageTitle, wikiTitle, currentUserLiking);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'User And You Liked Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been liked
	 * @param wikiTitle - The String content containing the title of the wiki which contains the liked wiki page
	 * @param userLikingWikiPage - The String content of the user name who has liked the wiki page
	 * @return - The String content containing the 'User And You Liked Your Wiki Page' news story
	 */
	public static String getLikeYourWikiPageNewsStory_UserAndYou(HomepageUI ui, String wikiPageTitle, String wikiTitle, String userLikingWikiPage) {
		return createNewsStory(ui, Data.LIKE_YOUR_WIKI_PAGE_YOU_OTHER, wikiPageTitle, wikiTitle, userLikingWikiPage);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You Liked Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been liked
	 * @param wikiTitle - The String content containing the title of the wiki which contains the liked wiki page
	 * @return - The String content containing the 'You Liked Your Wiki Page' news story
	 */
	public static String getLikeYourWikiPageNewsStory_You(HomepageUI ui, String wikiPageTitle, String wikiTitle) {
		return createNewsStory(ui, Data.LIKE_YOUR_WIKI_PAGE_YOU, wikiPageTitle, wikiTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'You And X Others Liked Your Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param numberOfOtherUsersLiked - The String content containing the number of other users who have liked the wiki page
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been liked
	 * @param wikiTitle - The String content containing the title of the wiki which contains the liked wiki page
	 * @return - The String content containing the 'User And You Liked Your Wiki Page' news story
	 */
	public static String getLikeYourWikiPageNewsStory_YouAndMany(HomepageUI ui, String numberOfOtherUsersLiked, String wikiPageTitle, String wikiTitle) {
		return createNewsStory(ui, Data.LIKE_YOUR_WIKI_PAGE_YOU_MANY, numberOfOtherUsersLiked, wikiPageTitle, wikiTitle, null);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In A Comment On The Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page which included the comment with mentions
	 * @param wikiTitle - The String content containing the title of the wiki which contains the wiki page
	 * @param userNameWhoPostedMentions - The String content containing the user name of the user who has posted the comment with mentions
	 * @return - The String content containing the 'Mentioned You In A Comment On The Wiki Page' news story
	 */
	public static String getMentionedYouInACommentOnTheWikiPageNewsStory(HomepageUI ui, String wikiPageTitle, String wikiTitle, String userNameWhoPostedMentions) {
		return createNewsStory(ui, Data.MENTIONED_YOU_WIKIPAGE_COMMENT, wikiPageTitle, wikiTitle, userNameWhoPostedMentions);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Mentioned You In The Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page which included the mentions
	 * @param wikiTitle - The String content containing the title of the wiki which contains the wiki page
	 * @param userNameWhoPostedMentions - The String content containing the user name of the user who has posted the mentions
	 * @return - The String content containing the 'Mentioned You In The Wiki Page' news story
	 */
	public static String getMentionedYouInTheWikiPageNewsStory(HomepageUI ui, String wikiPageTitle, String wikiTitle, String userNameWhoPostedMentions) {
		return createNewsStory(ui, Data.MENTIONED_YOU_WIKIPAGE, wikiPageTitle, wikiTitle, userNameWhoPostedMentions);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Update Their Comment On Their Own Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page
	 * @param wikiTitle - The String content containing the title of the wiki which contains the wiki page
	 * @param userNameUpdatingComment - The String content containing the user name of the user who has updated the comment on the wiki page
	 * @return - The String content containing the 'Update Their Comment On Their Own Wiki Page' news story
	 */
	public static String getUpdateCommentOnTheirOwnWikiPageNewsStory(HomepageUI ui, String wikiPageTitle, String wikiTitle, String userNameUpdatingComment) {
		return createNewsStory(ui, Data.UPDATE_THEIR_COMMENT_ON_THEIR_OWN_WIKIPAGE, wikiPageTitle, wikiTitle, userNameUpdatingComment);
	}
	
	/**
	 * Retrieves the news story corresponding the the 'Update Wiki Page' event
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param wikiPageTitle - The String content containing the title of the wiki page that has been updated
	 * @param wikiTitle - The String content containing the title of the wiki in which the wiki page has been updated
	 * @param userNameUpdatingWikiPage - The String content containing the user name of the user who has updated the wiki page
	 * @return - The String content containing the 'Update Wiki Page' news story
	 */
	public static String getUpdateWikiPageNewsStory(HomepageUI ui, String wikiPageTitle, String wikiTitle, String userNameUpdatingWikiPage) {
		return createNewsStory(ui, Data.EDIT_WIKI_PAGE, wikiPageTitle, wikiTitle, userNameUpdatingWikiPage);
	}
}
