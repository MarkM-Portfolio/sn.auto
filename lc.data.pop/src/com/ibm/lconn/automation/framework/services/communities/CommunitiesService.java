package com.ibm.lconn.automation.framework.services.communities;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.commons.httpclient.HttpException;

import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.nodes.Calendar;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommentToEvent;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Event;
import com.ibm.lconn.automation.framework.services.communities.nodes.FeedLink;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;
import com.ibm.lconn.automation.framework.services.communities.nodes.Subcommunity;
import com.ibm.lconn.automation.framework.services.files.nodes.FileComment;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Profiles Service object handles getting/posting data to the Connections Profiles service.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class CommunitiesService extends LCService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CommunitiesService.class.getName());
	private HashMap<String, String> communitiesURLs;
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
	
	public CommunitiesService(AbderaClient client, ServiceEntry service) throws LCServiceException {
		super(client, service);
		
		UpdateServiceDocument();
	}

	public CommunitiesService(AbderaClient client, ServiceEntry service, Map<String, String> headers) throws LCServiceException {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}	
		UpdateServiceDocument();
	}

	private void UpdateServiceDocument() throws LCServiceException {
		//just for cookies
		getFeed(getServiceURLString() + URLConstants.COMMUNITIES_MY);
		LOGGER.debug("UpdateServiceDocument : get cookies RespStatus "+getRespStatus());
		
		if(getRespStatus() != 200){			
			setFoundService(false);
			throw new LCServiceException("Error : Can't get My Communities - Status "+getRespStatus());
		}

		
		//Anony access
		ExtensibleElement feed = getFeed(service.getServiceURLString() + URLConstants.COMMUNITIES_SERVICE);
		
		if(feed != null) {
			if(getRespStatus() == 200){			
				setFoundService(true);
				communitiesURLs = getCollectionUrls((Service) feed);
			} else {
				setFoundService(false);
				throw new LCServiceException("Error : Can't get CommunitiesService Feed - Status "+getRespStatus());
			}

		} else {
			setFoundService(false);
			throw new LCServiceException("Error : Can't get CommunitiesService Feed");
		}
	}
		
	public HashMap<String, String> getCommunitiesURLs() {
		return communitiesURLs;
	}	
	public ExtensibleElement createCommunity(Community community) {
		
		return createCommunity(community, true);
	}
	

	public ExtensibleElement createCommunity(Community community, boolean addDefaultWidgets) {
	    ExtensibleElement ee;
	    if (addDefaultWidgets) {
	        ee = postFeed(communitiesURLs.get(StringConstants.COMMUNITIES_MY), community.toEntry());
	    } else {
	        ee = postFeed(communitiesURLs.get(StringConstants.COMMUNITIES_MY) + "?addDefaultWidgets=false", community.toEntry());
	    }
	    return ee;
	}
	
	public ExtensibleElement getCommunity(String communityEditHref) {
		
		return getFeed(communityEditHref);
	}

	public ExtensibleElement editCommunity(String communityEditHref, Community community) {
		
		return putFeed(communityEditHref, community.toEntry());
	}	
	
	public boolean deleteCommunity(String communityEditHref) {
		return deleteFeed(communityEditHref);
	}
	
	/* TJB this method is a hard delete.  The community will not display in the Trash view */
	public boolean purgeCommunity(String communityEditHref) {
		return deleteWithTrashCookie(communityEditHref);
	}	
	
	public ExtensibleElement createSubcommunity(Community parentCommunity, Subcommunity subcommunity) {
		return postFeed(parentCommunity.getSubcommunitiesHref(), subcommunity.toEntry());
	}
	
	public ExtensibleElement getSubcommunity(String subcommunityEditHref) {
		return getFeed(subcommunityEditHref);
	}
	
	public ExtensibleElement editSubcommunity(String subcommunityEditHref, Subcommunity subcommunity) {
		return putFeed(subcommunityEditHref, subcommunity.toEntry());
	}	
	
	public boolean deleteSubcommunity(String subcommunityEditHref) {
		return deleteFeed(subcommunityEditHref);
	}
	
	public ExtensibleElement addMemberToCommunity(Community parentCommunity, Member member) {
		return postFeed(parentCommunity.getMembersListHref(), member.toEntry());
	}
	
	public boolean removeMemberFromCommunity(String memberACLEditURL) {
		return deleteFeed(memberACLEditURL);
	}
	
	public ExtensibleElement updateMemberMailSubscriptionInCommunity(String memberACLEditURL, Member member) {
		return putFeed(memberACLEditURL,  member.toEntry());
	}
	
	public ExtensibleElement createCommunityBookmark(Community parentCommunity, Bookmark bookmark) {
		return postFeed(parentCommunity.getBookmarkHref(), bookmark.toEntry());
	}
	
	public ExtensibleElement getCommunityBookmark(String bookmarkEditHref) {
		return getFeed(bookmarkEditHref);
	}
	
	public ExtensibleElement editCommunityBookmark(String bookmarkEditHref, Bookmark bookmark) {
		
		return putFeed(bookmarkEditHref, bookmark.toEntry());
	}
	
	public boolean deleteCommunityBookmark(String bookmarkEditHref) {
		
		return deleteFeed(bookmarkEditHref);
	}
	
	public ExtensibleElement createFeedLink(Community parentCommunity, FeedLink feedLink) {
		
		return postFeed(parentCommunity.getFeedLinksHref(), feedLink.toEntry());
	}
	
	public ExtensibleElement broadcastMail(Community parentCommunity, Entry entry) {
		
		String link = parentCommunity.getSelfLink();
		String broadcast_url = link.replace("/community/instance?", "/community/broadcasts?");
		return postFeed(broadcast_url, entry);
	}
	
	public ExtensibleElement requestToJoinEntry (Community parentCommunity, Entry entry) {
		
		String link = parentCommunity.getSelfLink();
		String requestToJoin_url = link.replace("/instance?", "/requestsToJoin?");
		return postFeed(requestToJoin_url, entry);
	}
	
	public ExtensibleElement getFeedLink(String feedLinkEditHref) {
		
		return getFeed(feedLinkEditHref);
	}
	
	public ExtensibleElement getCalendarFeed(String feedHref) {
		
		return getFeed(feedHref);
	}	
	
	public ExtensibleElement getCalendarFeedWithRedirect(String feedHref) {
		
		return getFeedWithRedirect(feedHref);
	}
	
	public ExtensibleElement editFeedLink(String feedLinkEditHref, FeedLink feedLink) {
		
		return putFeed(feedLinkEditHref, feedLink.toEntry());
	}
	
	public boolean deleteFeedLink(String feedLinkEditHref) {
		
		return deleteFeed(feedLinkEditHref);
	}
	
	public ExtensibleElement createForumTopic(Community parentCommunity, ForumTopic forumTopic) {
		
		return postFeed(parentCommunity.getForumTopicsLink(), forumTopic.toEntry());
	}
	
	public ExtensibleElement getCommForumTopic(Community parentCommunity) {
		
		return getFeed(parentCommunity.getForumTopicsLink());
	}
	
	public ExtensibleElement getMyCommForumTopic(Community parentCommunity, String filter) {
		
		return getFeed(parentCommunity.getForumTopicsLink()+"&"+filter);
	}
	
	public ExtensibleElement getForumTopic(String forumTopicEditLink) {
		
		return getFeed(forumTopicEditLink);
	}
	
	public ExtensibleElement editForumTopic(String forumTopicEditLink, ForumTopic forumTopic) {
		
		return putFeed(forumTopicEditLink, forumTopic.toEntry());
	}
	
	public boolean deleteForumTopic(String forumTopicEditLink) {
		
		return deleteFeed(forumTopicEditLink);
	}
	
	public ExtensibleElement createForumReply(Entry parentTopic, ForumReply forumReply) {
		
		return postFeed(parentTopic.getLink(StringConstants.REL_REPLIES).getHref().toString(), forumReply.toEntry());
	}
	
	public ExtensibleElement getForumReply(String forumReplyEditLink) {
		
		return getFeed(forumReplyEditLink);
	}
	
	public ExtensibleElement editForumReply(String forumReplyEditLink, ForumReply forumReply) {
		
		return putFeed(forumReplyEditLink, forumReply.toEntry());
	}
	
	public boolean deleteForumReply(String forumReplyEditLink) {
		
		return deleteFeed(forumReplyEditLink);
	}
	
	public ExtensibleElement createInvitation(Community parentCommunity, Invitation invitation) {
		
		return postFeed(parentCommunity.getInvitationsListLink(), invitation.toEntry());
	}
	
	public ExtensibleElement getInvitation(String invitationEditHref) {
		
		return getFeed(invitationEditHref);
	}
	
	
	public boolean deleteInvitiation(String invitationEditHref) {
		
		return deleteFeed(invitationEditHref);
	}
	
	
	public ExtensibleElement getAllCommunities(boolean ascendingOrder, String email, int page, int pageSize, String textSearch, Date since, SortField sortField, String tag, String userid) {
		
		return searchCommunities(service.getLink().getHref().toString() + URLConstants.COMMUNITIES_ALL, 
				ascendingOrder, email, page, pageSize, textSearch, null, since, null, null, sortField, tag, userid);
	}
	
	public ExtensibleElement getOrgCommunities(boolean ascendingOrder, String email, int page, int pageSize, String textSearch, Date since, SortField sortField, String tag, String userid) {
		
		return searchCommunities(service.getLink().getHref().toString() + URLConstants.COMMUNITIES_ORG, 
				ascendingOrder, email, page, pageSize, textSearch, null, since, null, null, sortField, tag, userid);
	}
	
	public ExtensibleElement getOrgCommunitiesTagCloud(boolean ascendingOrder, String email, int page, int pageSize, String textSearch, Date since, SortField sortField, String tag, String userid) {
		
		return searchCommunities(service.getLink().getHref().toString() + URLConstants.COMMUNITIES_ORG_TAG_CLOUD, 
				ascendingOrder, email, page, pageSize, textSearch, null, since, null, null, sortField, tag, userid);
	}
	
	public ExtensibleElement getMyCommunities(boolean ascendingOrder, String email, int page, int pageSize, String textSearch, Date since, SortField sortField, String tag, String userid) {
		
		return searchCommunities(service.getSslLink().getHref().toString() + URLConstants.COMMUNITIES_MY, 
				ascendingOrder, email, page, pageSize, textSearch, null, since, null, null, sortField, tag, userid);
	}
	
	public ExtensibleElement getMyOwnedCommunities(boolean ascendingOrder, String email, int page, int pageSize, String textSearch, Date since, SortField sortField, String tag, String userid) {
		
		return searchCommunities(service.getSslLink().getHref().toString() + URLConstants.COMMUNITIES_MY + "?filterType=owner", 
				ascendingOrder, email, page, pageSize, textSearch, null, since, null, null, sortField, tag, userid);
	}
	
	public ExtensibleElement getMyInvitations(boolean ascendingOrder, int page, int pageSize, Date since, SortField sortField) {
		
		return searchCommunities(communitiesURLs.get(StringConstants.COMMUNITIES_MY_INVITATIONS).toString(), 
				ascendingOrder, null, page, pageSize, null, null, since, null, null, sortField, null, null);
	}
	
	public ExtensibleElement getSubcommunities(String parentCommunityHref, boolean ascendingOrder, int page, int pageSize, Date since, SortBy sortBy, SortOrder sortOrder, SortField sortField) {
		
		return searchCommunities(parentCommunityHref, ascendingOrder, null, page, pageSize, null, null, since, sortBy, sortOrder, sortField, null, null);
	}
	
	public ExtensibleElement getCommunityBookmarks(String communityBookmarksHref) {
		
		return getFeed(communityBookmarksHref);
	}
	
	public ExtensibleElement getCommunityFeedLinks(String communityFeedLinksHref) {
		
		return getFeed(communityFeedLinksHref);
	}
	
	public ExtensibleElement getCommunityCalendarService(String calendarServiceHref) {
		
		return getFeed(calendarServiceHref);
	}
	
	public ExtensibleElement editCommunityCalendarService(String calendarServiceHref, Calendar calendar) {
		
		return putFeed(calendarServiceHref, calendar.toEntry());
	}
	
	public ExtensibleElement postCalendarEvent(String calendarEventHref, Event event) {
		
		return postFeed(calendarEventHref, event.toEntry());
	}
	
	public ExtensibleElement postEventComment(String eventHref, CommentToEvent comment) {
		
		return postFeed(eventHref, comment.toEntry());
	}
	public ExtensibleElement getCommunityMembers(String parentCommunityHref, boolean ascendingOrder, String email, int page, int pageSize, Role role, Date since, SortField sortField, String userid) {
		
		return searchCommunities(parentCommunityHref, ascendingOrder, email, page, pageSize, null, role, since, null, null, sortField, null, userid);
	}

	public ExtensibleElement getCommunityRemoteAPPs(String parentCommunityHref, boolean ascendingOrder, String email, int page, int pageSize, Role role, Date since, SortBy sortBy, SortOrder sortOrder, SortField sortField, String userid) {
		
		return searchCommunities(parentCommunityHref, ascendingOrder, email, page, pageSize, null, role, since, sortBy, sortOrder, sortField, null, userid);
	}
	
	public ExtensibleElement getRelatedCommunitiesFeed(String communityUUID){
		
		return getFeed(service.getServiceURLString() + URLConstants.COMMUNITIES_RELATED_ALL + "?communityUuid=" + communityUUID);
	}
	
	public ExtensibleElement createRelatedCommunity(String communityUuid, Entry comm){
		
		return postFeed(service.getServiceURLString() + URLConstants.COMMUNITIES_RELATED + "?communityUuid=" + communityUuid, comm);
	}
	
	public ExtensibleElement retrieveRelatedCommunity(String relatedCommuntiyUUID){
		
		return getFeed(service.getServiceURLString() + URLConstants.COMMUNITIES_RELATED + "?entryId=" + relatedCommuntiyUUID);
	}
	
	public ExtensibleElement updateRelatedCommunity(String relatedCommunityUUID, Entry updatedCommunityEntry){
		
		return putFeed(service.getServiceURLString() + URLConstants.COMMUNITIES_RELATED + "?entryId=" + relatedCommunityUUID, updatedCommunityEntry);
	}
	
	public boolean deleteRelatedCommunuity(String relatedCommunityUUID){
		
		return deleteFeed(service.getServiceURLString() + URLConstants.COMMUNITIES_RELATED + "?entryId=" + relatedCommunityUUID);
	}
	
	public ExtensibleElement searchCommunities(String sourceURL, boolean ascendingOrder, String email, int page, int pageSize, String textSearch, Role role, Date since, SortBy sortBy, SortOrder sortOrder, SortField sortField, String tag, String userid) {
		String searchPath = sourceURL;
		
		if(searchPath.lastIndexOf("?") == -1 )
			searchPath += "?";
		else
			searchPath += "&";
		
		if(ascendingOrder)
			searchPath += "asc=" + ascendingOrder + "&";
		
		if(email != null && email.length() != 0)
			searchPath += "email=" + email + "&";
		
		if(page > 0)
			searchPath += "page=" + page + "&";
		
		if(pageSize > 0)
			searchPath += "ps=" + pageSize + "&";
		
		if(textSearch != null && textSearch.length() != 0){
			try {
				searchPath += "search=" + URLEncoder.encode(textSearch, "UTF-8") + "&";
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		if(role != null)
			searchPath += "role=" + role.toString().toLowerCase() + "&";
		
		if(since != null)
			searchPath += "since=" + formatter.format(since) + "&";
		
		if(sortBy != null)
			searchPath += "sortBy=" + sortBy.toString().toLowerCase() + "&";
		
		if(sortOrder != null)
			searchPath += "sortOrder=" + sortOrder.toString().toLowerCase() + "&";
		
		if(sortField != null)
			searchPath += "sortField=" + sortField.toString().toLowerCase() + "&";
		
		if(tag != null && tag.length() != 0)
			searchPath += "tag=" + tag + "&";
		
		if(userid != null && userid.length() != 0)
			searchPath += "userid=" + userid + "&";
		
		return getFeed(searchPath);
	}

	public ArrayList<ForumTopic> getCommunityForumTopics(Community community) {
		
		ArrayList<ForumTopic> forumTopics = new ArrayList<ForumTopic>();
		ExtensibleElement topicsFeed = getFeed(community.getForumTopicsLink());
		
		if (topicsFeed != null) {
			for(Entry topicEntry: ((Feed) topicsFeed).getEntries()) {
				ForumTopic data = new ForumTopic(topicEntry);
				forumTopics.add(data);
			}
		}
		
		return forumTopics;
	}

	public ExtensibleElement postFileComment(String commentLink,FileComment comment) {
		
		if (commentLink.startsWith("http"))
			return postFeed(commentLink, comment.toEntry());
		else
			return postFeed(service.getServiceURLString() + commentLink, comment.toEntry());
	}
	
	public ExtensibleElement getRelatedCommunityServiceDoc(String relatedCommuntiyUUID){
		
		return getFeed(service.getServiceURLString() + URLConstants.COMMUNITIES_RELATED_SERVICE + "?communityUuid=" + relatedCommuntiyUUID);
	}

	public ExtensibleElement putCalendarEvent(String calendarEventHref, Event event) {
		
		return putFeed(calendarEventHref, event.toEntry());
	}
	
	public ExtensibleElement followEventInstance(String calendarEventInstHref, Event event) {
		
		return postFeed(calendarEventInstHref, event.toEntry());
	}
	
	public ExtensibleElement postIdeationBlog(String url, Entry ntry) {
		
		return postFeed(url, ntry);
	}
	
	public ExtensibleElement postBlog(String url, Entry ntry) {
		
		return postFeed(url, ntry);
	}	
	
	public ExtensibleElement postIdeationRecommendation(String url) throws HttpException, IOException {
		
		//postIdeationVote(url);
		return postFeed(url, Abdera.getNewFactory().newEntry());
	}
	
	public ExtensibleElement followCommunity(Entry communityFollowEntry){
		
		return postFeed(service.getServiceURLString()+"/follow/atom/resources?source=communities&type=community", communityFollowEntry);
	}
	
	public boolean unfollowCommunity(String communityUuid){
		
		return deleteFeed(service.getServiceURLString()+"/follow/atom/resources/"+communityUuid+
						  "?source=COMMUNITIES&type=COMMUNITY&resource="+communityUuid);
	}
	
	public ExtensibleElement getFollowedCommunities(){
		
		return getFeed(service.getServiceURLString() + "/follow/atom/resources?source=communities&type=community&ps=100");
	}

	public ExtensibleElement putEntry(String href, Entry ntry) {
		
		return putFeed(href, ntry);
	}	
	
	public ExtensibleElement postEntry(String href, Entry entry){
		
		return postFeed(href, entry);
	}
	
	/*There are too many methods that return a feed given a URL.  Instead of creating still more, here's one
	 * that can be used for any given URL.
	 */
	public ExtensibleElement getAnyFeed(String feedHref) {
		
		return getFeed(feedHref);
	}
	
	public ExtensibleElement getAnyFeedWithRedirect(String feedHref) {
		
		return getFeedWithRedirect(feedHref);
	}
	
	public boolean deleteCommunityWidget(String widgetSelfLink){
		
		return deleteFeed(widgetSelfLink);
	}
	
	public ExtensibleElement getCommunityWidgets(String communityUuid){
		
		return getFeed(service.getServiceURLString()+"/service/atom/community/widgets?communityUuid="+communityUuid);
	}
	
	public ExtensibleElement getCommunityWidget(String communityUuid, String widgetDefId){
		return getFeed(service.getServiceURLString()+"/service/atom/community/widgets?communityUuid="+communityUuid+"&widgetDefId="+widgetDefId);
	}
	
	
	/*public int addWidget(Community parentCommunity, String widgetDefId) {
		getApiLogger().debug(CommunitiesService.class.getName()+ " addWidget "+widgetDefId);
		String link = parentCommunity.getSelfLink();
		String widget_url = link.replace("service/atom/community/instance?communityUuid", "addWidget.do?resourceId").concat("&widgetDefId="+widgetDefId+"&uiLocation=col2&unhide=false");

		return postWidget(widget_url, Abdera.getNewFactory().newEntry());	

	}*/
	
	public ExtensibleElement postWidget(Community parentCommunity, Entry entry) {
		getApiLogger().debug(CommunitiesService.class.getName()+ " post Widget ");
		String widget_url = parentCommunity.getWidgetHref();

		return postFeed(widget_url, entry);	

	}
	
	/**
	 * Method to actually upload file content using the file's InputStream
	 * @param fileEntry      FileEntry to upload containing inputstream
	 * @param url 		String value for the PUT
	 * @param mimeType	String value for the mime type
	 * @return server response EE
	 * @throws FileNotFoundException 
	 */
	public ExtensibleElement uploadFile(String url, FileEntry fileEntry, String mimeType) throws FileNotFoundException{
		return putFileWithStream(url, fileEntry.getInputStream(), mimeType);
	}
	
	public Feed getCommunityBlog(Entry communityBlogEntry) {
		String blogFeedHref = null;
		Link l = communityBlogEntry.getLink("http://www.ibm.com/xmlns/prod/sn/remote-application/feed");
		if (l == null)
			return null;
		blogFeedHref = l.getHref().toString();
		ExtensibleElement ee = getFeed(blogFeedHref);
		if (ee != null) {
			Element e = ee.getExtension(StringConstants.API_RESPONSE_CODE);
			if (e != null) {
				int statusCode = Integer.parseInt(e.getText());
				if (statusCode != 200)
					return null;
			}
			
			return (Feed)ee;
		}
		return null;
	}
	public String getServiceURLString(){
		return service.getServiceURLString();
	}
	
	public ExtensibleElement addMemberToActivity(String activityACLURL, Member member) {
		return postFeed(activityACLURL, member.toEntry());
	}

	public ExtensibleElement postFollowFeedForCommunity(Feed feed, String uuid) {
		String followURL = getServiceURLString() + "/follow/atom/resources?source=Communities&type=community&resource=" + uuid;
		return postFeed(followURL, feed);
	}

	public void deleteFollowFeedForCommunity(Feed feed, String uuid) throws Exception {
		String followURL = getServiceURLString() + "/follow/atom/resources?source=Communities&type=community&resource=" + uuid;

		deleteFeedWithBody(followURL, feed.toString());
	}

	public ExtensibleElement postFollowFeedForUser(Feed feed) {
		String followURL = getServiceURLString() + "/follow/atom/resources?source=Communities&type=community";
		return postFeed(followURL, feed);
	}

	public void deleteFollowFeedForUser(Feed feed) throws Exception {
		String followURL = getServiceURLString() + "/follow/atom/resources?source=Communities&type=community";

		deleteFeedWithBody(followURL, feed.toString());
	}
	
	/*public HttpResponse getPermaLink(String permaHref) {
		
		HttpResponse  result = null;
		try {
			result = doHttpGet( permaHref);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}*/
}
