package com.ibm.lconn.automation.framework.services.forums;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Service;
import org.apache.abdera.protocol.client.AbderaClient;
import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Filter;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.StringConstants.View;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

/**
 * Forums Service object handles getting/posting data to the Connections Forums service.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ForumsService extends LCService {

	private HashMap<String, String> forumsURLs;
	private HashMap<String, String> moderationURLs;
	
	public ForumsService(AbderaClient client, ServiceEntry service) throws LCServiceException {
		super(client, service);
		
		updateServiceDocument();
	}

	public ForumsService(AbderaClient client, ServiceEntry service, Map<String, String> headers) throws LCServiceException {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}	
		updateServiceDocument();
	}
	
	private void updateServiceDocument() throws LCServiceException {
		ExtensibleElement feed = getFeed(service.getServiceURLString() + URLConstants.FORUMS_SERVICE);
		
		
		if(feed != null) {
			if(getRespStatus() == 200){
				setFoundService(true);
				forumsURLs = getCollectionUrls((Service) feed);
			} else {
				setFoundService(false);
				throw new LCServiceException("Error : Can't get ForumsService Feed, status: " + getRespStatus());
			}
		} else {
			setFoundService(false);
			throw new LCServiceException("Error : Can't get ForumsService Feed, status: " + getRespStatus());
		}
		
		//getForumsModerationService();
	}
	
	public ExtensibleElement createForum(Forum newForum) {
		return postFeed(forumsURLs.get(StringConstants.FORUMS), newForum.toEntry());
	}
	
	public ExtensibleElement getForum(String forumEditLink) {
		return getFeed(forumEditLink);
	}
	
	public ExtensibleElement getForumTopics(String forumUuid){
		return getFeed(service.getServiceURLString() + URLConstants.TOPICS_FORUM + forumUuid);
	}
	
	public ExtensibleElement getForumsModerationService() {
		String url = service.getLink().getHref().toString()+"/atom/moderation/atomsvc";
		ExtensibleElement feed = getFeed(url);
		if(feed != null) {
			//setFoundService(true);
			moderationURLs = getCollectionUrls((Service) feed);
		} else {
			//setFoundService(false);
		}
		return feed;
	}
	
	public ExtensibleElement editForum(String forumEditLink, Forum forum) {
		return putFeed(forumEditLink, forum.toEntry());
	}
	
	public boolean deleteForum(String forumEditLink) {
		return deleteFeed(forumEditLink);
	}
	
	public ExtensibleElement createForumTopic(Forum parentForum, ForumTopic forumTopic) {
		return postFeed(parentForum.getRepliesLink(), forumTopic.toEntry());
	}
	
	public ExtensibleElement createForumTopicWithAttach(Forum parentForum, ForumTopic forumTopic, File file) {
		return postMultipartFeed(parentForum.getRepliesLink(), forumTopic.toEntry(), file);
	}
	
	public ExtensibleElement createForumTopicWithAttachBase64(Forum parentForum, ForumTopic forumTopic, File file) {
		return postMultipartFeedWithBase64(parentForum.getRepliesLink(), forumTopic.toEntry(), file);
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
	
	public ExtensibleElement editForumInReplyTo(String forumReplyEditLink,Entry forumReply,Element newReplyTo) {
			ForumReply fr = new ForumReply(forumReply);
			return putFeed(forumReplyEditLink, fr.toNewInReplyToEntry(forumReply,newReplyTo));
	}
	
	public boolean deleteForumReply(String forumReplyEditLink) {
		return deleteFeed(forumReplyEditLink);
	}
	
	public ExtensibleElement postRecommendEntry(String url, Entry ntry) throws Exception {
		return postFeed(url, ntry);
	}
	
	public void deleteRecommendEntry(String url) throws Exception {
		getApiLogger().debug("Delete Status: "+deleteWithResponseStatus(url));

	}
	

	public ExtensibleElement getAllForums(String communityUuid, String email, int page, int pageSize, Date since, SortBy sortBy, SortOrder sortOrder, String tag, String userid, View view, String searchTerms) {
		return searchForums(service.getLink().getHref().toString() + URLConstants.FORUMS_ALL, 
				communityUuid, null, null, email, page, pageSize, since, sortBy, sortOrder, tag, userid, view, searchTerms);
	}
	
	public ExtensibleElement getPublicForums(){
		return getFeed(forumsURLs.get(StringConstants.FORUMS_PUBLIC));
	}

	// This method searches all forums for story 40524.
	// This API is currently undocumented, it does take common params like pageSize and sortBy, unclear
	// at this time what other params are valid, left them for now in the method, the API *should*
	// ignore invalid ones as long as valid ones are specified
	public ExtensibleElement searchAllForums(String communityUuid, String email, int page, int pageSize, Date since, SortBy sortBy, SortOrder sortOrder, String tag, String userid, View view, String searchTerms) {
		return searchForums(service.getLink().getHref().toString() + URLConstants.FORUMS_SEARCH_ALL, 
				communityUuid, null, null, email, page, pageSize, since, sortBy, sortOrder, tag, userid, view, searchTerms);
	}
	
	public ExtensibleElement getMyForums(String communityUuid, String email, int page, int pageSize, Date since, SortBy sortBy, SortOrder sortOrder, String tag, String userid, View view, String searchTerms) {
		return searchForums(service.getLink().getHref().toString() + URLConstants.FORUMS_MY, 
				communityUuid, null, null, email, page, pageSize, since, sortBy, sortOrder, tag, userid, view, searchTerms);
	}
	
	public ExtensibleElement getForumTags(String communityUuid, Filter filter, int page, int pageSize, Date since, SortBy sortBy, SortOrder sortOrder, View view, String searchTerms) {
		return searchForums(service.getLink().getHref().toString() + URLConstants.FORUMS_TAG_COLLECTION,
				communityUuid, filter, null, null, page, pageSize, since, sortBy, sortOrder, null, null, view, searchTerms);
	}
	
	public ExtensibleElement getMyTopics(String communityUuid, Filter filter, String email, int page, int pageSize, Date since, SortBy sortBy, SortOrder sortOrder, String tag, String userid, View view, String searchTerms) {
		return searchForums(service.getLink().getHref().toString() + URLConstants.TOPICS_MY, 
				communityUuid, filter, null, email, page, pageSize, since, sortBy, sortOrder, tag, userid, view, searchTerms);
	}
	
	public ExtensibleElement getTopicTags(String communityUuid, Filter filter, String forumUuid, int page, int pageSize, Date since, SortBy sortBy, SortOrder sortOrder, View view, String searchTerms) {
		return searchForums(service.getLink().getHref().toString() + URLConstants.TOPICS_TAG_COLLECTION, 
				communityUuid, filter, forumUuid, null, page, pageSize, since, sortBy, sortOrder, null, null, view, searchTerms);
	}
	
	public ExtensibleElement getResourcesFollowed(){
		return getFeed(service.getServiceURLString() + "/follow/atom/resources?source=forums");
	}
	
	public ExtensibleElement getFlagHistoryForumsResults(String uri){
		uri = uri.replace("communities/service/", "forums/");
		uri = uri.replace("community/instance?", "forms/review/entries?")+"&sortOrder=asc";
		return getFeed(uri);
	}
	
	public ExtensibleElement getResourceFollowed(String uri){
		return getFeed(uri);
	}
	
	public ExtensibleElement getApprovalList(){
		getForumsModerationService();
		moderationURLs.get(StringConstants.PRE_MODERATED_LIST);
		return getFeed( moderationURLs.get(StringConstants.PRE_MODERATED_LIST));
	}
	
	public ExtensibleElement postApproval(Entry ntry){
		return postFeed( moderationURLs.get(StringConstants.PRE_MODERATED_EDIT), ntry);
	}
	
	public ExtensibleElement getReviewList(){
		return getFeed( moderationURLs.get(StringConstants.POST_MODERATED_LIST));
	}
	
	public ExtensibleElement postReview(Entry ntry){
		return postFeed( moderationURLs.get(StringConstants.POST_MODERATED_EDIT), ntry);
	}
	
	public ExtensibleElement searchForums(String sourceURL, String communityUuid, Filter filter, String forumUuid, String email, int page, int pageSize, Date since, SortBy sortBy, SortOrder sortOrder, String tag, String userid, View view, String searchTerms) {
		String searchPath = sourceURL;
		
		if(searchPath.lastIndexOf("?") == -1 )
			searchPath += "?";
		else
			searchPath += "&";
		
		if(communityUuid != null && communityUuid.length() != 0)
			searchPath += "communityUuid=" + communityUuid + "&";
		
		if(filter != null)
			searchPath += "filter=" + filter.toString().toLowerCase() + "&";
		
		if(forumUuid != null && forumUuid.length() != 0)
			searchPath += "forumUuid=" + forumUuid + "&";
			
		if(email != null && email.length() != 0)
			searchPath += "email=" + email + "&";
		
		if(page > 0)
			searchPath += "page=" + page + "&";
		
		if(pageSize > 0)
			searchPath += "ps=" + pageSize + "&";
		
		if(since != null)
			searchPath += "since=" + Utils.dateFormatter.format(since) + "&";
		
		if(sortBy != null)
			searchPath += "sortBy=" + sortBy.toString().toLowerCase() + "&";
		
		if(sortOrder != null)
			searchPath += "sortOrder=" + sortOrder.toString().toLowerCase() + "&";
		
		if(tag != null && tag.length() != 0)
			searchPath += "tag=" + tag + "&";
		
		if(userid != null && userid.length() != 0)
			searchPath += "userid=" + userid + "&";
		
		if(view != null)
			searchPath += "view=" + view + "&";
		
		if(searchTerms != null && searchTerms.length() != 0){
			searchPath += "search=" + searchTerms +"&";
			
		}
		return getFeed(searchPath);
	}
	
	public int createForumTopicCRX(Forum parentForum, ForumTopic forumTopic) {
		return postFeedWithCRX(parentForum.getRepliesLink(), forumTopic.toEntry());
	}
	
	public ExtensibleElement getForumsFeed(String url){
		return getFeed(url);
	}
	
	public ExtensibleElement postEntry(String url, Entry entry){
		return postFeed(url, entry);
	}
	
	public ExtensibleElement putEntry(String url, Entry entry){
		return putFeed(url, entry);
	}
	
	public boolean deleteEntry(String url){
		return deleteFeed(url);
	}
	
	public ExtensibleElement getTopicReplies(ForumTopic forumTopic){
		return getFeed(forumTopic.getRepliesLink());
	}
	
	public ExtensibleElement flagCommunityTopic(ForumTopic forumTopic){
		String reportUrl = service.getServiceURLString() + "/atom/reports";
		//IRITopicID - urn:lsid:ibm.com:forum:e687c941-a0c4-46f2-a433-4d15d384ee12
		String IRITopicId = forumTopic.getId().toString();
		String topicId = IRITopicId.substring(IRITopicId.lastIndexOf(":")+1);
		
		String topicSelfLink = service.getServiceURLString() + "/atom/topic?topicUuid="+topicId;
		Entry entry = Abdera.getNewFactory().newEntry();
		entry.addCategory(StringConstants.REL_ISSUE, "001", "Legal");
		entry.addLink(topicSelfLink, StringConstants.REL_REPORT_ITEM);
		entry.setContent("BadEntry");
		// Post the entry
		return postEntry(reportUrl, entry);
	}
	
	public ExtensibleElement flagCommunityReply(ForumReply forumReply){
		String reportUrl = service.getServiceURLString() + "/atom/reports";
		//IRIReplyId =urn:lsid:ibm.com:forum:e687c941-a0c4-46f2-a433-4d15d384ee12
		String IRIReplyId = forumReply.getId().toString();
		String replyId = IRIReplyId.substring(IRIReplyId.lastIndexOf(":")+1);
		String topicSelfLink = service.getServiceURLString() + "/atom/replies?replyUuid="+replyId;

		Entry entry = Abdera.getNewFactory().newEntry();
		entry.addCategory(StringConstants.REL_ISSUE, "001", "Legal");
		entry.addLink(topicSelfLink, StringConstants.REL_REPORT_ITEM);
		entry.setContent("BadEntry");
		// Post the entry
		return postEntry(reportUrl, entry);
	}
	
}
