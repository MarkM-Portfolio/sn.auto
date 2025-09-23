package com.ibm.lconn.automation.framework.services.communities;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.protocol.client.AbderaClient;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommentToModerate;

public class ModerationService extends LCService {

/*
    NOTE: class can be expanded as needed to handle other moderation, currently just
    using it for comments, could handle these URLs as well:
    
		entries-moderation-approval-action
		entries-moderation-review-content
		entries-moderation-review-action
		comments-moderation-review-content
		comments-moderation-review-action
			
		global moderator has review entries/comments actions available
		
		for file moderation, entries links will return documentation URLs, so class
		can be generic in handling both
 */
	
	private HashMap<String, String> moderateURLs;
	
	public ModerationService(AbderaClient client, ServiceEntry service , String moderationServiceDoc) throws LCServiceException {
		super(client, service);
		
		ExtensibleElement feed = getFeed(service.getServiceURLString() + moderationServiceDoc);
		
		if(feed != null && getRespStatus()==200) {
			setFoundService(true);
			moderateURLs = getCollectionUrlsUsingTerm((Service) feed);
		} else {
			//TJB 9/28/15.  Change from 9.22 broke tests on SC where getting the service doc returns redirect (302).
			if (getRespStatus()==302) {
				ExtensibleElement feed2 = getFeedWithRedirect(service.getServiceURLString() + moderationServiceDoc);
				setFoundService(true);
				moderateURLs = getCollectionUrlsUsingTerm((Service) feed2);
			} else {
				setFoundService(false);
				throw new LCServiceException("Error : Can't get ModerationService Feed");
			}
		}
	}
	
	/* Not currently needed
	public ExtensibleElement getEntriesAwaitingApproval(String communityID) {
		// NOTE: this request takes other parameters to limit results returned
		// currently this does not interest me

		// TODO: traverse doc and pull out content waiting for approval
		int totalResults = -1; // assume failure
		ExtensibleElement modFeed = getFeed(moderateURLs.get("entries-moderation-approval-content") + "?community=" + communityID); 
		
		if (modFeed != null) {
			Element e = modFeed.getFirstChild(StringConstants.OPENSEARCH_TOTALRESULTS); 
			if (e != null)
				{
					try {
						totalResults = Integer.parseInt(e.getText());
					} catch (NumberFormatException nfe) {
						totalResults = -1;
					}
				}
			}

		//LOGGER.debug("Total items pending approval: " + totalResults);
		
		return null;//getFeed(moderateURLs.get("entries-moderation-approval-content"));
	}
	*/

	public ArrayList<CommentToModerate> getCommentsAwaitingApproval(String communityID) {
		// NOTE: this request takes other parameters to limit results returned
		// currently this does not interest me
		ArrayList<CommentToModerate> commentToModerate = new ArrayList<CommentToModerate>();
		String feedURL = moderateURLs.get("comments-moderation-approval-content");
		if (feedURL == null) {
			feedURL = moderateURLs.get("Listing of pre-moderated forum content ");
		}
		ExtensibleElement modFeed = getFeed(feedURL + "?ps=500&");//community=" + communityID); 
		
		if (modFeed != null) {
			Element e = modFeed.getExtension(StringConstants.API_RESPONSE_CODE);
			if (e != null) {
				int statusCode = Integer.parseInt(e.getText());
				if (statusCode == 400)
					return commentToModerate;
			}
			
			for(Entry modEntry: ((Feed) modFeed).getEntries()) {
				commentToModerate.add(new CommentToModerate(modEntry));
			}
		}
	
		return commentToModerate;
	}

	public boolean moderateComment(CommentToModerate commentToModerate) {
		String feedURL = moderateURLs.get("comments-moderation-approval-action");
		if (feedURL == null) {
			feedURL = moderateURLs.get("Change pre-moderated forum content approval status ");
		}
		ExtensibleElement result = postFeed(feedURL, commentToModerate.toEntry());

		if (result != null) {
			Element e = result.getExtension(StringConstants.API_RESPONSE_CODE);
			if (e != null) {
				int statusCode = Integer.parseInt(e.getText());
				if (statusCode != 200)
					return false;
			}
		}
		return true;
	}
	
	public ExtensibleElement getModFeed(String feedLinkEditHref) {
		return getFeed(feedLinkEditHref);
	}
	
}

