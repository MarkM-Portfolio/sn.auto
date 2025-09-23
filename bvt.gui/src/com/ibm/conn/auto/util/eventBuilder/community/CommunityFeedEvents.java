package com.ibm.conn.auto.util.eventBuilder.community;

import org.testng.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.FeedLink;

public class CommunityFeedEvents {
	
	private static Logger log = LoggerFactory.getLogger(CommunityFeedEvents.class);
	
	/**
	 * Creates a new feed in the specified community
	 * 
	 * @param community - The Community instance of the community in which the feed is to be created
	 * @param baseFeed - The BaseFeed instance of the feed to be created
	 * @param userCreatingFeed - The User instance of the user creating the community feed
	 * @param apiUserCreatingFeed - The APICommunitiesHandler instance of the user creating the community feed
	 * @return - The FeedLink instance of the created feed
	 */
	public static FeedLink createFeed(Community community, BaseFeed baseFeed, User userCreatingFeed, APICommunitiesHandler apiUserCreatingFeed) {
		
		log.info("INFO: " + userCreatingFeed.getDisplayName() + " will now create a feed in the community with title: " + community.getTitle());
		return apiUserCreatingFeed.createFeed(community, baseFeed);
	}
	
	/**
	 * Creates a new feed in the specified community and then updates the description of the feed
	 * 
	 * @param community - The Community instance of the community in which the feed is to be created
	 * @param baseFeed - The BaseFeed instance of the feed to be created and updated
	 * @param editedDescription - The String content of the new description to be set to the feed
	 * @param userCreatingFeed - The User instance of the user creating the community feed
	 * @param apiUserCreatingFeed - The APICommunitiesHandler instance of the user creating the community feed
	 * @return - The FeedLink instance of the updated feed
	 */
	public static FeedLink createFeedAndEditFeedDescription(Community community, BaseFeed baseFeed, String editedDescription, User userCreatingFeed, APICommunitiesHandler apiUserCreatingFeed) {
		
		// Create a feed in the specified community
		FeedLink communityFeed = createFeed(community, baseFeed, userCreatingFeed, apiUserCreatingFeed);
		
		// Update the description of the feed
		return editFeedDescription(communityFeed, editedDescription, userCreatingFeed, apiUserCreatingFeed);
	}
	
	/**
	 * Updates the description for a feed
	 * 
	 * @param feedToBeUpdated - The FeedLink instance of the feed to be updated
	 * @param editedDescription - The String content of the new description to be set to the feed
	 * @param userUpdatingFeed - The User instance of the user who is updating the feed
	 * @param apiUserUpdatingFeed - The APICommunityHandler instance of the user who is updating the feed
	 * @return - The updated FeedLink instance
	 */
	public static FeedLink editFeedDescription(FeedLink feedToBeUpdated, String editedDescription, User userUpdatingFeed, APICommunitiesHandler apiUserUpdatingFeed) {
		
		log.info("INFO: " + userUpdatingFeed.getDisplayName() + " will now update the description for the community feed with title: " + feedToBeUpdated.getTitle());
		FeedLink updatedFeed = apiUserUpdatingFeed.editFeedDescription(feedToBeUpdated, editedDescription);
		
		log.info("INFO: Verify that the description for the feed was updated successfully");
		Assert.assertTrue(updatedFeed.getContent().trim().equals(editedDescription), 
							"ERROR: The description for the feed was NOT updated as expected to the new content: " + editedDescription);
		return updatedFeed;
	}
}
