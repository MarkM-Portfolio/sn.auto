package com.ibm.conn.auto.util.eventBuilder.community;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;

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
 * Date:	8th March 2016
 */

public class CommunityEvents {

	private static Logger log = LoggerFactory.getLogger(CommunityEvents.class);
	
	/**
	 * Creates a new community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param communityUserAPI - The APICommunitiesHandler instance of the user creating the community
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunity(BaseCommunity baseCommunity, User userCreatingCommunity, APICommunitiesHandler communityAPIUser) {
		
		String accessType;
		if(baseCommunity.getAccess().equals(Access.PUBLIC)) {
			accessType = "public";
		} else if(baseCommunity.getAccess().equals(Access.MODERATED)) {
			accessType = "moderated";
		} else {
			accessType = "restricted";
		}
		
		log.info("INFO: " + userCreatingCommunity.getDisplayName() + " will now create a new " + accessType + " community");
		Community newCommunity = communityAPIUser.createCommunity(baseCommunity);
		
		log.info("INFO: Verify that the new community has been created successfully");
		Assert.assertNotNull(newCommunity, 
								"ERROR: The new community was NOT created successfully and was returned as null");
		return newCommunity;
	}
	
	/**
	 * Allows the specified user to follow the community
	 * 
	 * @param community - The Community instance of the community to be followed
	 * @param userToFollowCommunity - The User instance of the user to follow the community
	 * @param communityAPIUser - The APICommunitiesHandler instance of the user to follow the community
	 */
	public static void followCommunitySingleUser(Community community, User userToFollowCommunity, APICommunitiesHandler communityAPIUser) {
		
		log.info("INFO: " + userToFollowCommunity.getDisplayName() + " will now follow the community with title: " + community.getTitle());
		communityAPIUser.followCommunity(community);
	}
	
	/**
	 * Allows multiple users to follow the community
	 * 
	 * @param community - The Community instance of the community to be followed
	 * @param usersToFollowCommunity - The Array of User instances of the users to follow the community
	 * @param apiUsersToFollowCommunity - The Array of APICommunitiesHandler instances of the users to follow the community
	 */
	public static void followCommunityMultipleUsers(Community community, User[] usersToFollowCommunity, APICommunitiesHandler[] apiUsersToFollowCommunity) {
		
		int index = 0;
		while(index < usersToFollowCommunity.length) {
			// Have the specified user follow the community
			followCommunitySingleUser(community, usersToFollowCommunity[index], apiUsersToFollowCommunity[index]);
			
			index ++;
		}
	}
	
	/**
	 * Creates a community and adds a single follower to that community 
	 *
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param userFollowingCommunity - The User instance of the user to follow the community
	 * @param communityAPIFollower - The APICommunitiesHandler instance of the user to follow the community
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param communityAPIUser - The APICommunitiesHandler instance of the user creating the community
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunityWithOneFollower(BaseCommunity baseCommunity, User userFollowingCommunity, APICommunitiesHandler communityAPIFollower, User userCreatingCommunity, APICommunitiesHandler communityAPICreator) {
		
		// Create the community
		Community newCommunity = createNewCommunity(baseCommunity, userCreatingCommunity, communityAPICreator);
		
		// Have the single user follow the community
		followCommunitySingleUser(newCommunity, userFollowingCommunity, communityAPIFollower);
		
		return newCommunity;
	}
	
	/**
	 * Adds a widget to a community
	 * 
	 * @param community - The Community instance of the community to which the widget will be added
	 * @param baseWidget - The BaseWidget which will be added to the community
	 * @param userAddingWidget - The User instance of the user adding the widget to the community
	 * @param communityAPIUser - The APICommunitiesHandler instance of the user adding the widget to the community
	 * @return - The Widget instance of the newly added Widget
	 */
	public static Widget addWidget(Community community, BaseWidget baseWidget, User userAddingWidget, APICommunitiesHandler communityAPIUser){
		
		Widget communityWidget = null;
		
		log.info("INFO: " + userAddingWidget.getDisplayName() + " will now add the " + baseWidget.getTitle() + " widget to community: " + community.getTitle());
		communityWidget = communityAPIUser.addWidget(community, baseWidget);
		
		log.info("INFO: Verify that the " + baseWidget.getTitle() + " widget was added successfully");
		Assert.assertNotNull(communityWidget, 
								"ERROR: The " + baseWidget.getTitle() + " widget could not be added and was returned as null");
		return communityWidget;
	}
	
	/**
	 * Adds the Wikis widget to a community (only possible if it is an On Premise test)
	 * 
	 * @param community - The Community instance of the community to which the wiki widget will be added
	 * @param userAddingWidget - The User instance of the user adding the wiki widget to the community
	 * @param communityAPIUser - The APICommunitiesHandler instance of the user adding the wiki widget to the community
	 * @param isOnPremise - Boolean to decide whether the test is running in On Premise (true) or on Smart Cloud (false)
	 * @return - The Widget instance of the newly added Widget
	 */
	public static Widget addWikiWidget(Community community, User userAddingWidget, APICommunitiesHandler communityAPIUser, boolean isOnPremise){
		
		Widget wikiWidget = null;
		if(isOnPremise) {
			wikiWidget = addWidget(community, BaseWidget.WIKI, userAddingWidget, communityAPIUser);			
		} else {
			log.info("INFO: No need to add the wiki widget since it is added automatically to Smart Cloud communities");
		}
		return wikiWidget;
	}
	
	/**
	 * Deletes / removes the specified widget from the specified community
	 * 
	 * @param community - The Community instance of the community which contains the widget to be deleted / removed
	 * @param baseWidgetToBeDeleted - The BaseWidget instance of the widget to be deleted / removed from the community
	 * @param userDeletingWidget - The User instance of the user deleting / removing the widget from the community
	 * @param apiUserDeletingWidget - The APICommunitiesHandler instance of the user deleting / removing the widget from the community
	 * @return - True if all actions are completed successfully
	 */
	public static boolean deleteWidget(Community community, BaseWidget baseWidgetToBeDeleted, User userDeletingWidget, APICommunitiesHandler apiUserDeletingWidget) {
		
		log.info("INFO: " + userDeletingWidget.getDisplayName() + " will now remove the " + baseWidgetToBeDeleted.getTitle() + " widget from the community");
		boolean deletedWidget = apiUserDeletingWidget.deleteWidget(community, baseWidgetToBeDeleted);
		
		log.info("INFO: Verify that the community widget was removed successfully");
		Assert.assertTrue(deletedWidget, 
							"ERROR: There was a problem with removing the " + baseWidgetToBeDeleted.getTitle() + " widget from the community");
		return true;
	}
	
	/**
	 * Adds a single member to the community
	 * 
	 * @param community - The Community instance of the community to which the member is to be added
	 * @param userAddingMember - The User instance of the user adding the member to the community
	 * @param apiUserAddingMember - The APICommunitiesHandler instance of the user adding the member to the community
	 * @param userBeingAdded - The User instance of the user to be added to the community
	 * @return - The Member instance of the new community member
	 */
	public static Member addMemberSingleUser(Community community, User userAddingMember, APICommunitiesHandler apiUserAddingMember, User userBeingAdded) {
		
		log.info("INFO: " + userAddingMember.getDisplayName() + " will now add " + userBeingAdded.getDisplayName() + " to the community as a member");
		Member newMember = apiUserAddingMember.addMemberToCommunity(userBeingAdded, community, Role.MEMBER);
		
		log.info("INFO: Verify that the new member has been added successfully");
		Assert.assertNotNull(newMember, "ERROR: The member was NOT added to the community and was returned as null");
		
		return newMember;
	}
	
	/**
	 * Adds multiple members to the community
	 * 
	 * @param community - The Community instance of the community to which the members are to be added
	 * @param userAddingMember - The User instance of the user adding the members to the community
	 * @param apiUserAddingMember - The APICommunitiesHandler instance of the user adding the members to the community
	 * @param usersBeingAdded - An array of User instances of the users to be added to the community
	 * @return - The array of Member instances of the new community members
	 */
	public static Member[] addMemberMultipleUsers(Community community, User userAddingMember, APICommunitiesHandler apiUserAddingMember, User[] usersBeingAdded) {
		
		// Create the array of members to be returned
		Member listOfMembers[] = new Member[usersBeingAdded.length];
		
		// Add all users to the community
		int index = 0;
		while(index < usersBeingAdded.length) {
			User userToBeAdded = usersBeingAdded[index];
			
			Member memberAdded = addMemberSingleUser(community, userAddingMember, apiUserAddingMember, userToBeAdded);
			listOfMembers[index] = memberAdded;
			index ++;
		}
		return listOfMembers;
	}
	
	/**
	 * Navigates to a community in Communities UI
	 * 
	 * @param baseCommunity - A BaseCommunity instance of the community to which the user will navigate
	 * @param communityAPIUser - An APICommunitiesHandler instance of the user who owns the community
	 * @param community - A Community instance of the community to which the user is navigating
	 * @param uiCo - A CommunitiesUI instance required for the navViaUUid(uiCo) method
	 */
	public static void navigateToCommunity(BaseCommunity baseCommunity, APICommunitiesHandler communityAPIUser, Community community, CommunitiesUI uiCo){

		log.info("INFO: Set the UUID of community to the BaseCommunity");
		baseCommunity.setCommunityUUID(communityAPIUser.getCommunityUUID(community)); 
		
		log.info("INFO: Navigate to the community using the community UUID");
		baseCommunity.navViaUUID(uiCo);

		log.info("INFO: Wait for the navigation panel to load");
		uiCo.fluentWaitElementVisible(BaseUIConstants.Community_Actions_Button);
	}
	
	/**
	 * Logs in as the specified user and navigates to the community in Communities UI
	 * 
	 * @param community - The Community instance of the community to which the user will navigate
	 * @param baseCommunity - The BaseCommunity instance of the community to which the user will navigate
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param userLoggingIn - The User instance of the user to be logged in
	 * @param apiUserLoggingIn - The APICommunitiesHandler instance of the user to be logged in
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToCommunity(Community community, BaseCommunity baseCommunity, HomepageUI ui, CommunitiesUI uiCo, User userLoggingIn, 
													APICommunitiesHandler apiUserLoggingIn, boolean preserveInstance) {
		// Log into Communities UI
		LoginEvents.loginToCommunities(ui, userLoggingIn, preserveInstance);
		
		// Navigate to the communities UI screen
		navigateToCommunity(baseCommunity, apiUserLoggingIn, community, uiCo);
	}
	
	/**
	 * Posts a status update with URL to a community (expects that you are already in the 'Status Updates' filter of the CommunitiesUI screen for the community)
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @param urlPreviewGeneratesThumbnailImage - True if the URL preview widget generated has a thumbnail image, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean addStatusUpdateWithURLUsingUI(HomepageUI ui, String statusMessageBeforeURL, String url, boolean urlPreviewGeneratesThumbnailImage) {
		
		// Post the status message with URL to the community
		UIEvents.postStatusWithURL(ui, statusMessageBeforeURL, url, urlPreviewGeneratesThumbnailImage);
		
		// Create the CSS selectors to be verified before proceeding
		String uniqueStatusMessage = statusMessageBeforeURL + " " + url;
		String urlPreviewWidget = ui.replaceNewsStory(CommunitiesUIConstants.URLPreview_AfterPostingSU, uniqueStatusMessage, url, null);
		String thumbnailImage = ui.replaceNewsStory(CommunitiesUIConstants.URLPreview_AfterPostingSU_ThumbnailImage, uniqueStatusMessage, url, null);
		
		log.info("INFO: Verify that the URL preview widget is displayed in the Communities UI after posting the community status update with URL");
		Assert.assertTrue(ui.isElementPresent(urlPreviewWidget), 
							"ERROR: The URL preview widget was NOT displayed in Communities UI after posting the community status update with URL");
		
		if(urlPreviewGeneratesThumbnailImage) {
			log.info("INFO: Verify that the thumbnail image is displayed with the URL preview widget");
			Assert.assertTrue(ui.isElementVisible(thumbnailImage), 
								"ERROR: The thumbnail image was NOT displayed with the URL preview widget");
		} else {
			log.info("INFO: Verify that the thumbnail image is NOT displayed with the URL preview widget");
			Assert.assertFalse(ui.isElementVisible(thumbnailImage), 
								"ERROR: The thumbnail image was displayed with the URL preview widget");
		}
		return true;
	}
	
	/**
	 * Posts a comment with URL to the specified community status update - also verifies that the URL preview widget did NOT appear
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param statusUpdateToBeCommentedOn - The String content of the status update to which to post the comment
	 * @param commentBeforeURL - The String content of the comment text to appear before the URL
	 * @param url - The URL to be posted with the comment content
	 * @return - True if all actions are completed successfully
	 */
	public static boolean addStatusUpdateCommentWithURLUsingUI(HomepageUI ui, String statusUpdateToBeCommentedOn, String commentBeforeURL, String url) {
		
		// Post the comment to the community status update - verify that the URL preview widget was NOT displayed
		return UIEvents.postStatusUpdateCommentWithURL(ui, statusUpdateToBeCommentedOn, commentBeforeURL, url);
	}
	
	/**
	 * Logs in to Communities UI, navigates to the specified community and then posts a status update with URL to that community
	 * 
	 * @param community - The Community instance of the community to which the user will navigate
	 * @param baseCommunity - The BaseCommunity instance of the community to which the user will navigate
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param userLoggingIn - The User instance of the user to be logged in
	 * @param apiUserLoggingIn - The APICommunitiesHandler instance of the user to be logged in
	 * @param statusMessageBeforeURL - The String content of any text to appear before the URL
	 * @param url - The URL to be posted with the status message content
	 * @param urlPreviewGeneratesThumbnail - True if the URL preview widget generated has a thumbnail image, false otherwise
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 * @return - True if all actions are completed successfully
	 */
	public static boolean loginAndNavigateToCommunityAndAddStatusUpdateWithURL(Community community, BaseCommunity baseCommunity, HomepageUI ui, CommunitiesUI uiCo, User userLoggingIn, APICommunitiesHandler apiUserLoggingIn, 
																				 String statusMessageBeforeURL, String url, boolean urlPreviewGeneratesThumbnail, boolean preserveInstance) {
		// Log in to Communities UI and navigate to the community																		
		loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userLoggingIn, apiUserLoggingIn, preserveInstance);
		
		// Navigate to status updates
		selectStatusUpdatesFromLeftNavigationMenu(uiCo);
		
		// Post the status update with URL to the community
		return addStatusUpdateWithURLUsingUI(ui, statusMessageBeforeURL, url, urlPreviewGeneratesThumbnail);
	}
	
	/**
	 * Logs in to Communities UI, navigates to the specified community and then posts a status update with multiple mentions to that community
	 * 
	 * @param community - The Community instance of the community to which the user will navigate
	 * @param baseCommunity - The BaseCommunity instance of the community to which the user will navigate
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param userLoggingIn - The User instance of the user to be logged in
	 * @param apiUserLoggingIn - The APICommunitiesHandler instance of the user to be logged in
	 * @param usersToBeMentioned - The Array of Mentions instances of all users to be mentioned in the status update
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 */
	public static void loginAndNavigateToCommunityAndAddStatusUpdateWithMultipleMentions(Community community, BaseCommunity baseCommunity, HomepageUI ui, RCLocationExecutor driver, CommunitiesUI uiCo, User userLoggingIn,
																							APICommunitiesHandler apiUserLoggingIn, Mentions[] usersToBeMentioned, boolean preserveInstance) {
		// Log in to Communities UI and navigate to the community																		
		loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userLoggingIn, apiUserLoggingIn, preserveInstance);
		
		// Navigate to status updates
		selectStatusUpdatesFromLeftNavigationMenu(uiCo);
		
		// Switch focus to the status update input field
		UIEvents.switchToStatusUpdateFrame(ui);
		
		String multipleMentionsText = "";
		for(Mentions mentions : usersToBeMentioned) {
			// Append the current user to be mentioned to the multiple mentions text (used for validations later)
			multipleMentionsText += "@" + mentions.getUserToMention().getDisplayName() + " ";
			
			// Enter the mentions to the current user into the status update input field
			UIEvents.typeMentionsOrPartialMentions(ui, mentions, mentions.getUserToMention().getDisplayName().length());
			
			// Wait for the typeahead to load
			UIEvents.waitForTypeaheadMenuToLoad(ui);
			
			// Get the list of menu items from the typeahead menu and select the appropriate user
			UIEvents.getTypeaheadMenuItemsListAndSelectUser(ui, driver, mentions);
			
			// Switch focus back to the status update input field again
			UIEvents.switchToStatusUpdateFrame(ui);
			
			// Verify that the link to the mentioned user is displayed
			UIEvents.verifyMentionsLinkIsDisplayed(ui, mentions.getUserToMention().getDisplayName());
			
			// Add a space at the end of the mentions link
			UIEvents.typeStringWithNoDelay(ui, " ");
		}
		// Post the status update with multiple mentions now that all users have been mentioned
		ProfileEvents.postStatusUpdateUsingUI(ui);
		
		log.info("INFO: Verify that the status message with multiple mentions is now displayed in Communties UI");
		multipleMentionsText = multipleMentionsText.trim();
		Assert.assertTrue(ui.fluentWaitTextPresent(multipleMentionsText), 
							"ERROR: The status message with multiple mentions was NOT displayed in Communities UI");
	}
	
	/**
	 * Creates a new community and adds a widget to it
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param baseWidget - The BaseWidget instance of the widget to be added to the community after it has been created
	 * @param isOnPremise - True if the test case is being executed On Premise, false otherwise
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param apiUserCreatingCommunity - The APICommunitiesHandler instance of the user creating the community
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunityAndAddWidget(BaseCommunity baseCommunity, BaseWidget baseWidget, boolean isOnPremise, User userCreatingCommunity, APICommunitiesHandler apiUserCreatingCommunity) {
		
		// Create the new community
		Community newCommunity = createNewCommunity(baseCommunity, userCreatingCommunity, apiUserCreatingCommunity);
		
		// Add the widget to the community
		addCommunityWidget(newCommunity, baseWidget, userCreatingCommunity, apiUserCreatingCommunity, isOnPremise);
		
		return newCommunity;
	}
	
	/**
	 * Creates a new community and adds a single additional member to that community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param apiUserCreatingCommunity - The APICommunitiesHandler instance of the user creating the community
	 * @param userToAddAsMember - The User instance of the user to be added to the community as a member
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunityWithOneMember(BaseCommunity baseCommunity, User userCreatingCommunity, APICommunitiesHandler apiUserCreatingCommunity, User userToAddAsMember) {
	
		// Create the new community
		Community newCommunity = createNewCommunity(baseCommunity, userCreatingCommunity, apiUserCreatingCommunity);
		
		// Add the member to the community
		addMemberSingleUser(newCommunity, userCreatingCommunity, apiUserCreatingCommunity, userToAddAsMember);
		
		return newCommunity;
	}
	
	/**
	 * Creates a new community and adds multiple members to that community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param apiUserCreatingCommunity - The APICommunitiesHandler instance of the user creating the community
	 * @param usersToAddAsMembers - An array of User instances representing all of the users to be added as members
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunityWithMultipleMembers(BaseCommunity baseCommunity, User userCreatingCommunity, APICommunitiesHandler apiUserCreatingCommunity, User[] usersToAddAsMembers) {
		
		// Create the new community
		Community newCommunity = createNewCommunity(baseCommunity, userCreatingCommunity, apiUserCreatingCommunity);
		
		// Add all members to the community
		addMemberMultipleUsers(newCommunity, userCreatingCommunity, apiUserCreatingCommunity, usersToAddAsMembers);
		
		return newCommunity;
	}
	
	/**
	 * Creates a new community with multiple members added to that community and then adds the specified widget to that community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param baseWidget - The BaseWidget instance of the widget to be added to the community after it has been created
	 * @param isOnPremise - True if the test case is being executed On Premise, false otherwise
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param apiUserCreatingCommunity - The APICommunitiesHandler instance of the user creating the community
	 * @param usersToAddAsMembers - An array of User instances representing all of the users to be added as members
	 * @return
	 */
	public static Community createNewCommunityWithMultipleMembersAndAddWidget(BaseCommunity baseCommunity, BaseWidget baseWidget, boolean isOnPremise, User userCreatingCommunity, APICommunitiesHandler apiUserCreatingCommunity, User[] usersToAddAsMembers) {
		
		// Create the new community with multiple members added
		Community newCommunity = createNewCommunityWithMultipleMembers(baseCommunity, userCreatingCommunity, apiUserCreatingCommunity, usersToAddAsMembers);
		
		// Add the widget to the community
		addCommunityWidget(newCommunity, baseWidget, userCreatingCommunity, apiUserCreatingCommunity, isOnPremise);
		
		return newCommunity;
	}
	/**
	 * Creates a new community, adds multiple members to that community and has a single user follow that community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param apiUserCreatingCommunity - The APICommunitiesHandler instance of the user creating the community
	 * @param usersToAddAsMembers - An array of User instances representing all of the users to be added as members
	 * @param userToFollowCommunity - The User instance of the user who is to follow the community
	 * @param apiUserToFollowCommunity - The APICommunitiesHandler instance of the user who is to follow the community
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunityWithMultipleMembersAndOneFollower(BaseCommunity baseCommunity, User userCreatingCommunity, APICommunitiesHandler apiUserCreatingCommunity, User[] usersToAddAsMembers,
																					User userToFollowCommunity, APICommunitiesHandler apiUserToFollowCommunity) {
		// Create the new community with all members added to the community
		Community newCommunity = createNewCommunityWithMultipleMembers(baseCommunity, userCreatingCommunity, apiUserCreatingCommunity, usersToAddAsMembers);
		
		// Have the specified user follow the community
		followCommunitySingleUser(newCommunity, userToFollowCommunity, apiUserToFollowCommunity);
		
		return newCommunity;
	}
	
	/**
	 * Creates a new community, adds multiple members to that community, allows a single user to follow the community and then adds a widget to the community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param apiUserCreatingCommunity - The APICommunitiesHandler instance of the user creating the community
	 * @param usersToAddAsMembers - An array of User instances representing all of the users to be added as members
	 * @param userToFollowCommunity - The User instance of the user who is to follow the community
	 * @param apiUserToFollowCommunity - The APICommunitiesHandler instance of the user who is to follow the community
	 * @param baseWidget - The BaseWidget instance of the widget to be added to the community after it has been created
	 * @param isOnPremise - True if the test case is being executed On Premise, false otherwise
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunityWithMultipleMembersAndOneFollowerAndAddWidget(BaseCommunity baseCommunity, User userCreatingCommunity, APICommunitiesHandler apiUserCreatingCommunity, User[] usersToAddAsMembers,
																							User userToFollowCommunity, APICommunitiesHandler apiUserToFollowCommunity, BaseWidget baseWidget, boolean isOnPremise) {
		// Create the new community with all members and followers added
		Community newCommunity = createNewCommunityWithMultipleMembersAndOneFollower(baseCommunity, userCreatingCommunity, apiUserCreatingCommunity, usersToAddAsMembers, userToFollowCommunity, apiUserToFollowCommunity);
		
		// Add the widget to the community
		addCommunityWidget(newCommunity, baseWidget, userCreatingCommunity, apiUserCreatingCommunity, isOnPremise);
				
		return newCommunity;
	}
	
	/**
	 * Creates a new community, adds multiple followers to the community and then adds a widget to the community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param baseWidget - The BaseWidget instance of the widget to be added to the community after it has been created
	 * @param isOnPremise - True if the test case is being executed On Premise, false otherwise
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param apiUserCreatingCommunity - The APICommunitiesHandler instance of the user creating the community
	 * @param usersToAddAsFollowers - The Array of User instances of all users to follow the community
	 * @param apiUsersToAddAsFollowers - The Array of APICommunitiesHandler instances of all users to follow the community
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunityWithMultipleFollowersAndAddWidget(BaseCommunity baseCommunity, BaseWidget baseWidget, boolean isOnPremise, User userCreatingCommunity, APICommunitiesHandler apiUserCreatingCommunity, User[] usersToAddAsFollowers, APICommunitiesHandler[] apiUsersToAddAsFollowers) {
		
		// Create the new community
		Community newCommunity = createNewCommunity(baseCommunity, userCreatingCommunity, apiUserCreatingCommunity);
		
		// Add the multiple followers to the community
		followCommunityMultipleUsers(newCommunity, usersToAddAsFollowers, apiUsersToAddAsFollowers);
		
		// Add the widget to the community
		addCommunityWidget(newCommunity, baseWidget, userCreatingCommunity, apiUserCreatingCommunity, isOnPremise);
		
		return newCommunity;
	}
	
	/**
	 * Creates a new community, adds one additional member to that community and then adds a widget to the community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param baseWidget - The BaseWidget instance of the widget to be added to the community after it has been created
	 * @param userToAddAsMember - The User instance of the user to be added to the community as a member
	 * @param isOnPremise - True if the test case is being executed On Premise, false otherwise
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param apiUserCreatingCommunity - The APICommunitiesHandler instance of the user creating the community
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunityWithOneMemberAndAddWidget(BaseCommunity baseCommunity, BaseWidget baseWidget, User userToAddAsMember, boolean isOnPremise,
																		User userCreatingCommunity, APICommunitiesHandler apiUserCreatingCommunity) {
		// Create the new community and add the user as a member
		Community newCommunity = createNewCommunityWithOneMember(baseCommunity, userCreatingCommunity, apiUserCreatingCommunity, userToAddAsMember);
		
		// Add the widget to the community
		addCommunityWidget(newCommunity, baseWidget, userCreatingCommunity, apiUserCreatingCommunity, isOnPremise);
		
		return newCommunity;
	}
	
	/**
	 * Creates a new community, has a single user follow that community and then adds a widget to the community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param userToFollowCommunity - The User instance of the user who is to follow the community
	 * @param apiUserToFollowCommunity - The APICommunitiesHandler instance of the user who is to follow the community
	 * @param baseWidget - The BaseWidget instance of the widget to be added to the community after it has been created
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param apiUserCreatingCommunity - The APICommunitiesHandler instance of the user creating the community
	 * @param isOnPremise - True if the test case is being executed On Premise, false otherwise
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunityWithOneFollowerAndAddWidget(BaseCommunity baseCommunity, User userToFollowCommunity, APICommunitiesHandler apiUserToFollowCommunity, 
																			BaseWidget baseWidget, User userCreatingCommunity, APICommunitiesHandler apiUserCreatingCommunity, 
																			boolean isOnPremise) {
		// Create the new community
		Community newCommunity = createNewCommunity(baseCommunity, userCreatingCommunity, apiUserCreatingCommunity);
		
		// Have the user follow the community
		followCommunitySingleUser(newCommunity, userToFollowCommunity, apiUserToFollowCommunity);
		
		// Add the widget to the community
		addCommunityWidget(newCommunity, baseWidget, userCreatingCommunity, apiUserCreatingCommunity, isOnPremise);
		
		return newCommunity;
	}
	
	/**
	 * Creates a new community and has a single user be added to the community as a member and then follow that community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param memberAndFollower - The User instance of the user to be added to the community as a member and then follow the community
	 * @param apiMemberAndFollower - The APICommunitiesHandler instance of the user to be added to the community as a member and then follow the community
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param apiUserCreatingCommunity - The APICommunitiesHandler instance of the user creating the community
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunityWithOneMemberAndOneFollower(BaseCommunity baseCommunity, User memberAndFollower, APICommunitiesHandler apiMemberAndFollower,
																			User userCreatingCommunity, APICommunitiesHandler apiUserCreatingCommunity) {
		// Create the new community and add the user as a member
		Community newCommunity = createNewCommunityWithOneMember(baseCommunity, userCreatingCommunity, apiUserCreatingCommunity, memberAndFollower);
		
		// Have the user follow the community
		followCommunitySingleUser(newCommunity, memberAndFollower, apiMemberAndFollower);
		
		return newCommunity;
	}
	
	/**
	 * Creates a new community with one member and follower and adds a widget to that community
	 * 
	 * @param baseCommunity - The BaseCommunity instance of the community to be created
	 * @param memberAndFollower - The User instance of the user to be added to the community as a member and then follow the community
	 * @param apiMemberAndFollower - The APICommunitiesHandler instance of the user to be added to the community as a member and then follow the community
	 * @param userCreatingCommunity - The User instance of the user creating the community
	 * @param apiUserCreatingCommunity - The APICommunitiesHandler instance of the user creating the community
	 * @param baseWidget - The BaseWidget instance of the widget to be added to the community after it has been created
	 * @param isOnPremise - True if the test case is being executed On Premise, false otherwise
	 * @return - The Community instance of the newly created community
	 */
	public static Community createNewCommunityWithOneMemberAndOneFollowerAndAddWidget(BaseCommunity baseCommunity, User memberAndFollower, APICommunitiesHandler apiMemberAndFollower,
																						User userCreatingCommunity, APICommunitiesHandler apiUserCreatingCommunity, BaseWidget baseWidget, boolean isOnPremise) {
		// Create the new community with one member and follower
		Community newCommunity = createNewCommunityWithOneMemberAndOneFollower(baseCommunity, memberAndFollower, apiMemberAndFollower, userCreatingCommunity, apiUserCreatingCommunity);
	
		// Add the widget to the community
		addCommunityWidget(newCommunity, baseWidget, userCreatingCommunity, apiUserCreatingCommunity, isOnPremise);
		
		return newCommunity;
	}
	
	/**
	 * Sends a request from a user to join the specified community
	 * 
	 * @param communityToJoin - The Community instance of the community to which the request to join will be sent
	 * @param userRequestingToJoin - The User instance of the user requesting to join the community
	 * @param apiUserRequestingToJoin - The APICommunitiesHandler instance of the user requesting to join the community
	 * @return - The String instance of the request message sent with the community request
	 */
	public static String requestToJoinACommunity(Community communityToJoin, User userRequestingToJoin, APICommunitiesHandler apiUserRequestingToJoin) {
		
		log.info("INFO: " + userRequestingToJoin.getDisplayName() + " is now sending a request to join the community with title: " + communityToJoin.getTitle());
		
		String requestMessage = userRequestingToJoin.getDisplayName() + " sending request to join " + communityToJoin.getTitle();
		boolean requestSent = apiUserRequestingToJoin.requestToJoinCommunity(communityToJoin, requestMessage);
		
		log.info("INFO: Verify that the request to join the community was successfully sent");
		Assert.assertTrue(requestSent, "ERROR: The request to join the community was NOT sent successfully");
		
		return requestMessage;
	}
	
	/**
	 * Sends an invitation to a user to join a community
	 * 
	 * @param communityToInvite - The Community instance of the community to which the invitation will be to join
	 * @param userSendingInvite - The User instance of the user sending the invitation
	 * @param apiUserSendingInvite - The APICommunitiesHandler instance of the user sending the invitation
	 * @param apiUserReceivingInvite - The APIProfilesHandler instance of the user receiving the invitation
	 * @return - The Invitation instance of the sent invitation
	 */
	public static Invitation inviteUserToJoinCommunity(Community communityToInvite, User userSendingInvite, APICommunitiesHandler apiUserSendingInvite, APIProfilesHandler apiUserReceivingInvite) {
		
		log.info("INFO: " + userSendingInvite.getDisplayName() + " will now send an invitation to " + apiUserReceivingInvite.getDesplayName() + " to join the community with title: " + communityToInvite.getTitle());
		Invitation communityInvite = apiUserSendingInvite.inviteUserToJoinCommunity(communityToInvite, apiUserReceivingInvite);
		
		log.info("INFO: Verify that the invitation to join the community was sent successfully");
		Assert.assertNotNull(communityInvite, "ERROR: The invitation to join the community was NOT sent successfully and was returned as null");
		
		return communityInvite;
	}
	
	/**
	 * Posts a status update to a community
	 * 
	 * @param community - The Community instance of the community in which the status update is to be posted
	 * @param apiCommunityUser - The APICommunitiesHandler instance of the user posting the status update
	 * @param apiProfileUser - The APIProfilesHandler instance of the user posting the status update
	 * @param statusUpdateContent - The String content of the status update to be posted
	 * @return - The ID of the created status update
	 */
	public static String addStatusUpdate(Community community, APICommunitiesHandler apiCommunityUser, APIProfilesHandler apiProfileUser, String statusUpdateContent) {
		
		log.info("INFO: " + apiProfileUser.getDesplayName() + " will now post a community status update with content: " + statusUpdateContent);
		return apiProfileUser.post_Message_Community(community.getUuid(), statusUpdateContent);
	}
	
	/**
	 * Posts a status update with mention to a community
	 * 
	 * @param community - The Community instance of the community in which the status update is to be posted
	 * @param apiCommunityUser - The APICommunitiesHandler instance of the user posting the status update
	 * @param apiProfileUser - The APIProfilesHandler instance of the user posting the status update
	 * @param mention - The Mentions instance of the user to be mentioned
	 * @return - The ID of the created status update
	 */
	public static String addCommStatusUpdateWithMentions(Community community, APICommunitiesHandler apiCommunityUser, APIProfilesHandler apiProfileUser,Mentions mentions) {
		log.info("INFO: " + apiProfileUser.getDesplayName() + " will now post a community mentions status update with a mentions to: " + mentions.getUserToMention().getDisplayName());
		return apiProfileUser.addMentionsStatusUpdate(community.getUuid(), mentions);
	}
	
	/**
	 * Posts a status update with 2 mention to a community
	 * 
	 * @param community - The Community instance of the community in which the status update is to be posted
	 * @param apiCommunityUser - The APICommunitiesHandler instance of the user posting the status update
	 * @param apiProfileUser - The APIProfilesHandler instance of the user posting the status update
	 * @param mention1 - The Mentions instance of the user to be mentioned
	 * @param mention2 - The Mentions instance of the user to be mentioned
	 * @return - The ID of the created status update
	 */
	public static String addCommStatusUpdateWithTwoMentions(Community community, APICommunitiesHandler apiCommunityUser, APIProfilesHandler apiProfileUser,Mentions mentions1,Mentions mentions2) {
		log.info("INFO: " + apiProfileUser.getDesplayName() + " will now post a community mentions status update with a mentions to: " + mentions1.getUserToMention().getDisplayName() +  " and " + mentions2.getUserToMention().getDisplayName());
		return apiProfileUser.addTwoMentionsStatusUpdate(community.getUuid(), mentions1, mentions2);
	}
	
	/**
	 * Posts a comment to a community status update
	 * 
	 * @param apiUserPostingComment - The APIProfilesHandler instance of the user posting the comment
	 * @param statusUpdateId - The ID of the community status update to which the comment is to be posted
	 * @param commentToBePosted - The String content of the comment to be posted
	 * @return - The ID of the created comment
	 */
	public static String addStatusUpdateComment(APIProfilesHandler apiUserPostingComment, String statusUpdateId, String commentToBePosted) {
		
		log.info("INFO: " + apiUserPostingComment.getDesplayName() + " will now post a comment to the status update with content: " + commentToBePosted);
		return apiUserPostingComment.postCommunityComment(statusUpdateId, commentToBePosted);
	}
	
	/**
	 * Likes / recommends a status update posted to a community
	 * 
	 * @param apiUserLikingStatusUpdate - The APIProfilesHandler instance of the user liking / recommending the community status update
	 * @param statusUpdateId - The ID of the status update to be liked / recommended
	 */
	public static void likeStatusUpdate(APIProfilesHandler apiUserLikingStatusUpdate, String statusUpdateId) {
		
		// Like / recommend the status update posted to the community
		ProfileEvents.likeStatusUpdate(apiUserLikingStatusUpdate, statusUpdateId);
	}
	
	/**
	 * Allows multiple users to like / recommend a status update posted to a community
	 * 
	 * @param apiUsersLikingStatusUpdate - An array of APIProfilesHandler instances of the users liking / recommending the community status update
	 * @param statusUpdateId - The ID of the status update to be liked / recommended
	 */
	public static void likeStatusUpdateMultipleUsers(APIProfilesHandler[] apiUsersLikingStatusUpdate, String statusUpdateId) {
		
		// Have all users like / recommend the status update posted to the community
		for(APIProfilesHandler apiUserLikingStatusUpdate : apiUsersLikingStatusUpdate) {
			likeStatusUpdate(apiUserLikingStatusUpdate, statusUpdateId);
		}
	}
	
	/**
	 * Likes / recommends a comment posted to a community status update
	 * 
	 * @param apiUserLikingComment - The APIProfilesHandler instance of the user liking / recommending the comment
	 * @param commentId - The ID of the comment to be liked / recommended
	 */
	public static void likeStatusUpdateComment(APIProfilesHandler apiUserLikingComment, String commentId) {
		
		// Like / recommend the comment posted to the status update
		ProfileEvents.likeComment(apiUserLikingComment, commentId);
	}
	
	/**
	 * Allows multiple users to like / recommend a comment posted to a community status update
	 * 
	 * @param apiUsersLikingComment - An array of APIProfilesHandler instances of the users liking / recommending the comment
	 * @param commentId - The ID of the comment to be liked / recommended
	 */
	public static void likeStatusUpdateCommentMultipleUsers(APIProfilesHandler[] apiUsersLikingComment, String commentId) {
		
		// Have all users like / recommend the comment posted to the status update
		ProfileEvents.likeCommentMultipleUsers(apiUsersLikingComment, commentId);
	}
	
	/**
	 * Unlikes a status update comment with the specified ID
	 * 
	 * @param apiUserUnlikingComment - The APIProfilesHandler instance of the user unliking the status update
	 * @param commentId - The ID of the comment to be unliked
	 */
	public static void unlikeStatusUpdateComment(APIProfilesHandler apiUserUnlikingComment, String commentId) {
		
		log.info("INFO: " + apiUserUnlikingComment.getDesplayName() + " will now unlike a comment on a community status update");
		boolean unlikedComment = apiUserUnlikingComment.unlike(commentId);
		
		log.info("INFO: Verify that the comment was unliked successfully");
		Assert.assertTrue(unlikedComment, 
							"ERROR: The comment was NOT unliked - the API returned a negative response");
	}
	
	/**
	 * Posts a comment to a community status update and likes / recommends the comment
	 * 
	 * @param apiUserPostingComment - The APIProfilesHandler instance of the user posting the comment
	 * @param apiUserLikingComment - The APIProfilesHandler instance of the user liking / recommending the comment
	 * @param statusUpdateId - The ID of the community status update to which the comment is to be posted
	 * @param commentToBePosted - The String content of the comment to be posted
	 * @return - The ID of the created comment
	 */
	public static String addStatusUpdateCommentAndLikeComment(APIProfilesHandler apiUserPostingComment, APIProfilesHandler apiUserLikingComment, String statusUpdateId, String commentToBePosted) {
		
		// Post the comment to the community status update
		String commentId = addStatusUpdateComment(apiUserPostingComment, statusUpdateId, commentToBePosted);
		
		// Like / recommend the comment
		likeStatusUpdateComment(apiUserLikingComment, commentId);
		
		return commentId;
	}
	
	/**
	 * Posts a comment to a community status update and allows multiple users to like / recommend the comment
	 * 
	 * @param apiUserPostingComment - The APIProfilesHandler instance of the user posting the comment
	 * @param apiUsersLikingComment - An array of APIProfilesHandler instances of the users liking / recommending the comment
	 * @param statusUpdateId - The ID of the community status update to which the comment is to be posted
	 * @param commentToBePosted - The String content of the comment to be posted
	 * @return - The ID of the created comment
	 */
	public static String addStatusUpdateCommentAndLikeCommentMultipleUsers(APIProfilesHandler apiUserPostingComment, APIProfilesHandler[] apiUsersLikingComment, String statusUpdateId, String commentToBePosted) {
		
		// Post the comment to the community status update
		String commentId = addStatusUpdateComment(apiUserPostingComment, statusUpdateId, commentToBePosted);
		
		// Have all required users like / recommend the comment
		likeStatusUpdateCommentMultipleUsers(apiUsersLikingComment, commentId);
		
		return commentId;
	}
	
	/**
	 * Posts a status update with mentions from the communities status update input box
	 * 
	 * @param baseCommunity - The BaseCommunity instance to which the status update with mentions will be posted
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userPostingStatus - The User instance of the user posting the status update with mentions
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param warningMessageIsDisplayed - True if the warning message for mentioned different org users is to be verified as displayed, false otherwise
	 * @return - True if all actions are completed successfully 
	 */
	public static boolean addStatusUpdateWithMentionsUsingUI(BaseCommunity baseCommunity, HomepageUI ui, RCLocationExecutor driver, User userPostingStatus, Mentions mentions, boolean warningMessageIsDisplayed) {
		
		log.info("INFO: Now entering a status update for the community with title: " + baseCommunity.getName());
		
		log.info("INFO: Now switching focus to the status update input field");
		ui.getStatusUpdateElement();
		
		log.info("INFO: " + userPostingStatus.getDisplayName() + " will now post a community status update with mentions to user: " + mentions.getUserToMention().getDisplayName());
		UIEvents.typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUser(ui, driver, mentions);
		
		// Verify whether the warning message is displayed / not displayed as expected in the UI
		verifyWarningMessageForStatusUpdateOrCommentWithMentions(ui, driver, baseCommunity, warningMessageIsDisplayed);
		
		log.info("INFO: Now switching focus back to the status update input field again");
		ui.getStatusUpdateElement();
		
		// Verify whether a valid / invalid mentions link is displayed as expected in the UI
		UIEvents.verifyValidOrInvalidMentionsLink(ui, mentions.getUserToMention().getDisplayName(), warningMessageIsDisplayed);
		
		// Enter the remainder of the mentions text into the status update input field
		UIEvents.typeAfterMentionsText(ui, mentions);
		
		// Post the status update with mentions
		ProfileEvents.postStatusUpdateUsingUI(ui);
		
		// Verify that all of the mentions text components are displayed in the UI after posting
		UIEvents.verifyMentionsTextIsDisplayedInUI(ui, mentions, !warningMessageIsDisplayed);
		
		return true;
	}
	
	/**
	 * Posts a status update comment with mentions to an existing community status update using the UI
	 * 
	 * @param baseCommunity - The BaseCommunity instance to which the status update comment with mentions will be posted
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param statusUpdate - The String content of the community status update to which the comment will be posted
	 * @param userPostingComment - The User instance of the user posting the status update comment with mentions
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param warningMessageIsDisplayed - True if the warning message for mentioned different org users is to be verified as displayed, false otherwise
	 * @return
	 */
	public static boolean addStatusUpdateCommentWithMentionsUsingUI(BaseCommunity baseCommunity, HomepageUI ui, RCLocationExecutor driver, String statusUpdate, 
																		User userPostingComment, Mentions mentions, boolean warningMessageIsDisplayed) {
		// Click on the 'Comment' link for this status update
		ui.clickStatusUpdateCommentLink(statusUpdate);
		
		// Switch focus to the now visible comment frame
		UIEvents.switchToStatusUpdateCommentFrame(ui, statusUpdate);
		
		log.info("INFO: " + userPostingComment.getDisplayName() + " will now post a comment with mentions to user: " + mentions.getUserToMention().getDisplayName());
		UIEvents.typeBeforeMentionsTextAndTypeMentionsAndSelectMentionedUser(ui, driver, mentions);
		
		// Verify whether the warning message is displayed / not displayed as expected in the UI
		verifyWarningMessageForStatusUpdateOrCommentWithMentions(ui, driver, baseCommunity, warningMessageIsDisplayed);
		
		log.info("INFO: Now switching focus back to the comment input field again");
		UIEvents.switchToStatusUpdateCommentFrame(ui, statusUpdate);
		
		// Verify whether a valid / invalid mentions link is displayed as expected in the UI
		UIEvents.verifyValidOrInvalidMentionsLink(ui, mentions.getUserToMention().getDisplayName(), warningMessageIsDisplayed);
		
		// Enter the remainder of the mentions text into the comment input field
		UIEvents.typeAfterMentionsText(ui, mentions);
		
		// Switch focus back to the top frame again
		UIEvents.switchToTopFrame(ui);
		
		// Post the comment with mentions
		ProfileEvents.postStatusUpdateCommentUsingUI(ui, statusUpdate, null, false);
		
		// Verify that all of the mentions text components are displayed in the UI after posting
		UIEvents.verifyMentionsTextIsDisplayedInUI(ui, mentions, !warningMessageIsDisplayed);
		
		return true;
	}
	
	/**
	 * Log in to Communities UI, navigate to the specified community and then post a status update with mentions to that community
	 * 
	 * @param community - The Community instance of the community to navigate to and add a status update to in the UI
	 * @param baseCommunity - The BaseCommunity instance of the community to navigate to and add a status update to in the UI
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param userLoggingIn - The User instance of the user to be logged in
	 * @param apiUserLoggingIn - The APICommunitiesHandler instance of the user to be logged in
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 * @param warningMessageIsDisplayed - True if the warning message for mentioned different org users is to be verified as displayed, false otherwise
	 * @return - True if all actions are completed successfully 
	 */
	public static boolean loginAndNavigateToCommunityAndAddStatusUpdateWithMentions(Community community, BaseCommunity baseCommunity, HomepageUI ui, RCLocationExecutor driver, CommunitiesUI uiCo, User userLoggingIn,
																					APICommunitiesHandler apiUserLoggingIn, Mentions mentions, boolean preserveInstance, boolean warningMessageIsDisplayed) {
		// Log in to Communities UI and navigate to the community																		
		loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userLoggingIn, apiUserLoggingIn, preserveInstance);
		
		// Navigate to status updates
		selectStatusUpdatesFromLeftNavigationMenu(uiCo);
		
		// Post a status update with mentions
		return addStatusUpdateWithMentionsUsingUI(baseCommunity, ui, driver, userLoggingIn, mentions, warningMessageIsDisplayed);
	}
	
	/**
	 * Log in to Communities UI, navigate to the specified community and then post a status update comment with mentions to the specified community status update
	 * 
	 * @param community - The Community instance of the community to navigate to and add a status update to in the UI
	 * @param baseCommunity - The BaseCommunity instance of the community to navigate to and add a status update to in the UI
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 * @param statusUpdate - The String content of the community status update to which the comment will be posted
	 * @param userLoggingIn - The User instance of the user to be logged in
	 * @param apiUserLoggingIn - The APICommunitiesHandler instance of the user to be logged in
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @param preserveInstance - True if the browser instance is to be preserved, false otherwise
	 * @param warningMessageIsDisplayed - True if the warning message for mentioned different org users is to be verified as displayed, false otherwise
	 * @return - True if all actions are completed successfully 
	 */
	public static boolean loginAndNavigateToCommunityAndAddStatusUpdateCommentWithMentions(Community community, BaseCommunity baseCommunity, HomepageUI ui, RCLocationExecutor driver, CommunitiesUI uiCo, String statusUpdate,
																				User userLoggingIn, APICommunitiesHandler apiUserLoggingIn, Mentions mentions, boolean preserveInstance, boolean warningMessageIsDisplayed) {
		// Log in to Communities UI and navigate to the community																		
		loginAndNavigateToCommunity(community, baseCommunity, ui, uiCo, userLoggingIn, apiUserLoggingIn, preserveInstance);
		
		// Navigate to status updates
		selectStatusUpdatesFromLeftNavigationMenu(uiCo);
		
		// Post a status update with mentions
		return addStatusUpdateCommentWithMentionsUsingUI(baseCommunity, ui, driver, statusUpdate, userLoggingIn, mentions, warningMessageIsDisplayed);
	}
	
	/**
	 * Verifies that the Communities UI screen is displayed correctly in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 */
	public static void verifyCommunitiesUIIsDisplayed(HomepageUI ui) {
		
		log.info("INFO: Now verifying that the I'm Following view is displayed in the UI");
		
		log.info("INFO: Waiting for the Communities UI screen to load");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().feedsForTheseCommunities), 
							"ERROR: The Communities UI screen did NOT load correctly / in time with visible text: " + Data.getData().feedsForTheseCommunities);
		
		log.info("INFO: Verify that the Communities UI heading is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().NewToCommunities_Heading), 
							"ERROR: The Communities UI heading was NOT displayed in Communities UI");
		
		log.info("INFO: Verify that the Comunities UI description text is displayed");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().CommunitiesText), 
							"ERROR: The Communities UI description text was NOT displayed in Communities UI");
	}
	
	/**
	 * Verifies that all components of a community invitation are displayed in the My Notifications view
	 * 
	 * @param ui- The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param userThatSentInvite - The User instance of the user that sent the community invite
	 * @param community - The Community instance of the community to which the user was invited to join
	 * @return - True if all actions are completed successfully
	 */
	public static boolean verifyCommunityInviteIsDisplayedInMyNotifications(HomepageUI ui, RCLocationExecutor driver, User userThatSentInvite, Community community) {
	
		log.info("INFO: Now verifying that the community invite is displayed correctly in the UI");
		
		log.info("INFO: Waiting for the My Notifications screen to load");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries),
							"ERROR: The My Notifications screen did NOT load correctly / in time with visible text: " + Data.getData().feedsForTheseEntries);
		
		// Create the news story to be verified
		String communityInviteEvent = ui.replaceNewsStory(Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_FOR_ME, community.getTitle(), null, userThatSentInvite.getDisplayName());
		
		log.info("INFO: Verify that the community invite event news story is displayed in the My Notifications view");
		Assert.assertTrue(ui.fluentWaitTextPresent(communityInviteEvent), 
							"ERROR: The community invite event news story was NOT displayed in the My Notifications view");
		
		log.info("INFO: Verify that the community description / content is displayed in the My Notifications view");
		Assert.assertTrue(ui.fluentWaitTextPresent(community.getContent().trim()), 
							"ERROR: The community description / content was NOT displayed in the My Notifications view");
		
		// Create the CSS selectors for all of the links
		String joinLink = HomepageUIConstants.CommunityInvite_Join_Link.replaceAll("PLACEHOLDER", communityInviteEvent);
		String declineLink = HomepageUIConstants.CommunityInvite_Decline_Link.replaceAll("PLACEHOLDER", communityInviteEvent);
		String saveLink = HomepageUIConstants.CommunityInvite_SaveThis_Link.replaceAll("PLACEHOLDER", communityInviteEvent);
		
		log.info("INFO: Verify that the join community link is displayed with the community invite news story in the My Notifications view");
		Assert.assertTrue(ui.fluentWaitPresent(joinLink), 
							"ERROR: The join community link was NOT displayed with the community invite news story in the My Notifications view");
		
		log.info("INFO: Verify that the join community link contains the text: " + Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_JOIN_LINK_TEXT);
		Element joinLinkElement = driver.getSingleElement(joinLink);
		Assert.assertTrue(joinLinkElement.getText().trim().contains(Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_JOIN_LINK_TEXT), 
							"ERROR: The join community link did NOT contain the expected text: " + Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_JOIN_LINK_TEXT);
		
		log.info("INFO: Verify that the decline invitation link is displayed with the community invite news story in the My Notifications view");
		Assert.assertTrue(ui.fluentWaitPresent(declineLink), 
							"ERROR: The decline invitation link was NOT displayed with the community invite news story in the My Notifications view");
		
		log.info("INFO: Verify that the decline invitation link contains the text: " + Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_DECLINE_LINK_TEXT);
		Element declineLinkElement = driver.getSingleElement(declineLink);
		Assert.assertTrue(declineLinkElement.getText().trim().contains(Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_DECLINE_LINK_TEXT), 
							"ERROR: The decline invitation link did NOT contain the expected text: " + Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_DECLINE_LINK_TEXT);
		
		log.info("INFO: Verify that the save link is displayed with the community invite news story in the My Notifications view");
		Assert.assertTrue(ui.fluentWaitPresent(saveLink), 
							"ERROR: The save link was NOT displayed with the community invite news story in the My Notifications view");
		
		log.info("INFO: Verify that the save link contains the text: " + Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_SAVE_LINK_TEXT);
		Element saveLinkElement = driver.getSingleElement(saveLink);
		Assert.assertTrue(saveLinkElement.getText().trim().contains(Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_SAVE_LINK_TEXT), 
							"ERROR: The save link did NOT contain the expected text: " + Data.MY_NOTIFICATIONS_COMMUNITY_INVITE_SAVE_LINK_TEXT);
		
		log.info("INFO: Verify that the join community link is displayed to the left of the decline invitation link");
		Assert.assertTrue(joinLinkElement.getLocation().x < declineLinkElement.getLocation().x, 
							"ERROR: The join community link was NOT displayed to the left of the decline invitation link");
		
		log.info("INFO: Verify that the decline invitation link is displayed to the left of the save link");
		Assert.assertTrue(declineLinkElement.getLocation().x < saveLinkElement.getLocation().x,
							"ERROR: The decline invitation link was NOT displayed to the left of the save link");
		return true;
	}
	
	/**
	 * Revokes a community invitation
	 * 
	 * @param inviteToBeRevoked - The Invitation instance of the community invitation to be revoked
	 * @param userRevokingInvitation - The User instance of the user revoking the invitation
	 * @param apiUserRevokingInvitation - The APICommunitiesHandler instance of the user revoking the invitation
	 */
	public static void revokeCommunityInvitation(Invitation inviteToBeRevoked, User userRevokingInvitation, APICommunitiesHandler apiUserRevokingInvitation) {
		
		log.info("INFO: " + userRevokingInvitation.getDisplayName() + " will now revoke the community invitation with title: " + inviteToBeRevoked.getTitle());
		boolean inviteRevoked = apiUserRevokingInvitation.revokeCommunityInvitation(inviteToBeRevoked);
		
		log.info("INFO: Verify that the community invitation has been successfully revoked");
		Assert.assertTrue(inviteRevoked, 
							"ERROR: The community invitation could NOT be revoked - the API method returned a negative (false) response");
	}
	
	/**
	 * Deletes the specified community
	 * 
	 * @param communityToBeDeleted - The Community instance of the community to be deleted
	 * @param userDeletingCommunity - The User instance of the user deleting the community
	 * @param apiUserDeletingCommunity - The APICommunitiesHandler instance of the user deleting the community
	 */
	public static void deleteCommunity(Community communityToBeDeleted, User userDeletingCommunity, APICommunitiesHandler apiUserDeletingCommunity) {
		
		log.info("INFO: " + userDeletingCommunity.getDisplayName() + " will now delete the community with title: " + communityToBeDeleted.getTitle());
		boolean communityDeleted = apiUserDeletingCommunity.deleteCommunity(communityToBeDeleted);
		
		log.info("INFO: Verify that the community has been successfully deleted");
		Assert.assertTrue(communityDeleted, 
							"ERROR: The community could NOT be deleted - the API method returned a negative (false) response");
	}
	
	/**
	 * Accepts any community invitation by clicking on the 'Join This Community' link in the invite
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityToJoin - The Community instance of the community to be joined
	 * @param invitationNewsStoryContent - The String content of the news story relating to the invitation (ie. 'UserName invited you to join the CommunityName community.')
	 * @param userAcceptingInvitation - The User instance of the user accepting the community invitation
	 */
	public static void acceptCommunityInviteUsingUI(HomepageUI ui, Community communityToJoin, String invitationNewsStoryContent, User userAcceptingInvitation) {
		
		log.info("INFO: " + userAcceptingInvitation.getDisplayName() + " will now accept a community invitation for the community with title: " + communityToJoin.getTitle());
		
		// Retrieve the handle for the main browser window before accepting the invite
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
		
		// Set the CSS selector for the accept community invite link
		String communityInviteLink = HomepageUIConstants.NewsStoryJoinThisCommunityLink.replaceAll("PLACEHOLDER", invitationNewsStoryContent);
		
		// Click on the accept link - this will redirect the user to a new tab / browser window
		ui.clickLinkWait(communityInviteLink);
		
		// Switch focus to the new window that has just opened
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		log.info("INFO: Verify that the success message for joining the community is displayed in the UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.Communities_Joined_NoFollow),
							"ERROR: The success message for joining the community was NOT displayed as expected after accepting the community invite");
		
		// Close the additional window now that all verifications are complete and switch focus back to the main window again
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
	}
	
	/**
	 * Accepts any request to join a community by clicking on the 'Add member' link in the request and saving the changes
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param communityToJoin - The Community instance of the community to be joined
	 * @param requestToJoinNewsStoryContent - The String content of the news story relating to the request to join (ie. 'UserName has requested to join your CommunityName community.')
	 * @param userAcceptingRequest - The User instance of the user accepting the request to join the community
	 */
	public static void acceptRequestToJoinCommunityUsingUI(HomepageUI ui, Community communityToJoin, String requestToJoinNewsStoryContent, User userAcceptingRequest, User userThatSentRequest) {
		
		log.info("INFO: " + userAcceptingRequest.getDisplayName() + " will now accept a request to join the community with title: " + communityToJoin.getTitle());
		
		// Retrieve the handle for the main browser window before accepting the invite
		String mainBrowserWindowHandle = UIEvents.getCurrentBrowserWindowHandle(ui);
		
		// Set the CSS selector for the 'Add member' link in the request to join the community
		String communityAddMemberLink = HomepageUIConstants.NewsStoryAddMemberLink.replaceAll("PLACEHOLDER", requestToJoinNewsStoryContent);
		
		// Click on the add member link - this will redirect the user to a new tab / browser window
		ui.clickLinkWait(communityAddMemberLink);
		
		// Switch focus to the new window that has just opened
		UIEvents.switchToNextOpenBrowserWindowByHandle(ui, mainBrowserWindowHandle);
		
		// Click on the 'Save' button to add this user to the community
		ui.clickLinkWait(CommunitiesUIConstants.AddMembers_SaveButton);
		
		// Create the success message to be verified
		String addedMembersMessage = Data.getData().ImportMemberMsg + userThatSentRequest.getDisplayName();
		
		log.info("INFO: Verify that the success message for adding " + userThatSentRequest.getDisplayName() + " to the community is displayed in the UI");
		Assert.assertTrue(ui.fluentWaitTextPresent(addedMembersMessage), 
							"ERROR: The success message for adding " + userThatSentRequest.getDisplayName() + " to the community was NOT displayed in the UI");
		
		// Close the additional window now that all verifications are complete and switch focus back to the main window again
		UIEvents.closeCurrentBrowserWindowAndSwitchToWindowByHandle(ui, mainBrowserWindowHandle);
	}
	
	/**
	 * Deletes a comment posted to a community status update
	 * 
	 * @param statusUpdateId - The String content of the ID for the status update which contains the comment to be deleted
	 * @param commentId - The String content of the ID for the comment to be deleted
	 * @param userDeletingComment - The User instance of the user deleting the comment
	 * @param apiUserDeletingComment - The APICommunitiesHandler instance of the user deleting the comment
	 */
	public static void deleteStatusUpdateComment(String statusUpdateId, String commentId, User userDeletingComment, APICommunitiesHandler apiUserDeletingComment) {
		
		log.info("INFO: " + userDeletingComment.getDisplayName() + " will now delete the community status update comment with ID: " + commentId);
		boolean commentDeleted = apiUserDeletingComment.deleteStatusComment(statusUpdateId, commentId);
		
		log.info("INFO: Verify that the comment was deleted successfully");
		Assert.assertTrue(commentDeleted, 
							"ERROR: The comment was NOT deleted from the community status update - API returned a negative response");
	}
	
	/**
	 * Handles which addWidget() method to call depending on which community widget is requested
	 * Private access for now since there is no requirement for this method to be used externally
	 * 
	 * @param community - The Community instance of the community to which the widget is to be added
	 * @param baseWidget - The BaseWidget instance of the widget to be added to the community
	 * @param userAddingWidget - The User instance of the user adding the widget to the community
	 * @param apiUserAddingWidget - The APICommuntiesHandler instance of the user adding the widget to the community
	 * @param isOnPremise - True if the test case is being executed On Premise, false otherwise
	 */
	public static void addCommunityWidget(Community community, BaseWidget baseWidget, User userAddingWidget, APICommunitiesHandler apiUserAddingWidget, boolean isOnPremise) {
		
		if(baseWidget.getTitle().equals(BaseWidget.WIKI.getTitle())) {
			addWikiWidget(community, userAddingWidget, apiUserAddingWidget, isOnPremise);
		} else {
			addWidget(community, baseWidget, userAddingWidget, apiUserAddingWidget);
		}
	}
	
	/**
	 * Selects the "Status Updates" option from the left-side navigation menu in Communities UI
	 * 
	 * @param uiCo - The CommunitiesUI instance to invoke all relevant methods
	 */
	private static void selectStatusUpdatesFromLeftNavigationMenu(CommunitiesUI uiCo) {
		
		log.info("INFO: Select 'Status Updates' from the left navigation menu");
		Community_LeftNav_Menu.STATUSUPDATES.select(uiCo);
	}
	
	/**
	 * Verifies that the warning message for a mentioned user who is not a member of the restricted community / is a visitor is displayed / absent in the UI
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param verifyIsDisplayed - True if the warning message is to be verified as displayed, false if it is to be verified as absent
	 */
	private static void verifyNonMemberWarningMessageIsDisplayed(HomepageUI ui, RCLocationExecutor driver, boolean verifyIsDisplayed) {
		
		String warningMessage = Data.getData().mentionErrorMsgGlobalSharebox;
		
		if(verifyIsDisplayed) {
			log.info("INFO: Verify that the non member of restricted community / visitor model warning message is displayed: " + warningMessage);
			Assert.assertTrue(ui.fluentWaitTextPresent(warningMessage), 
								"ERROR: The warning message was NOT displayed with content: " + warningMessage);
		} else {
			log.info("INFO: Verify that the non member of restricted community / visitor model warning message is NOT displayed: " + warningMessage);
			Assert.assertTrue(driver.isTextNotPresent(warningMessage), 
								"ERROR: The warning message was displayed with content: " + warningMessage);
		}
	}
	
	/**
	 * Performs all verifications around the warning messages that are displayed when entering a community status update or commenting on
	 * a community status update with mentions
	 * 
	 * @param ui - The HomepageUI instance to invoke all relevant methods
	 * @param driver - The RCLocationExecutor instance to invoke all relevant methods
	 * @param baseCommunity - The BaseCommunity instance of the community to which the status update or comment with mentions is being posted
	 * @param warningMessageIsDisplayed - True if the warning message is to be verified as displayed, false if it is to be verified as absent
	 */
	private static void verifyWarningMessageForStatusUpdateOrCommentWithMentions(HomepageUI ui, RCLocationExecutor driver, BaseCommunity baseCommunity, boolean warningMessageIsDisplayed) {
		
		if(baseCommunity.getAccess().equals(Access.RESTRICTED)) {
			// Verify warning message is displayed / is not displayed for a restricted community status update including a mentioned user
			verifyNonMemberWarningMessageIsDisplayed(ui, driver, warningMessageIsDisplayed);
		} else {
			// Verify warning message is displayed / is not displayed for a public / moderated community status update including a mentioned user
			ProfileEvents.verifyDifferentOrgWarningMessageIsDisplayed(ui, driver, warningMessageIsDisplayed);
		}
	}
}