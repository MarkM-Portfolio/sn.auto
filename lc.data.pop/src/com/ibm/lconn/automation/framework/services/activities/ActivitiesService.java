package com.ibm.lconn.automation.framework.services.activities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.activities.nodes.Activity;
import com.ibm.lconn.automation.framework.services.activities.nodes.ActivityEntry;
import com.ibm.lconn.automation.framework.services.activities.nodes.Todo;
import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.FieldType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.NodeType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Options;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Priority;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.LCEntry;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;

/**
 * Activities Service object handles getting/posting data to the Connections Activities service.
 * 
 * @author Piyush K. Agarwal - pagarwal@us.ibm.com
 */
public class ActivitiesService extends LCService {
	
	protected static Logger LOGGER = LoggerFactory.getLogger(ActivitiesService.class.getName());

	private HashMap<String, String> activityDashboardURLs;
	public HashMap<String, String> getActivityDashboardURLs() {
		return activityDashboardURLs;
	}

	private boolean isOauth = false;
	
	/**
	 * Constructor to create a new Activities Service helper object. 
	 * 
	 * This object contains helper methods for all API calls that are supported by the Activities service.
	 * 
	 * @param client	the authenticated AbderaClient that is used to handle requests to/from server
	 * @param service	the ServiceEntry that contains information about the Activities service from the server ServiceConfigs file
	 * @throws LCServiceException 
	 */
	public ActivitiesService(AbderaClient client, ServiceEntry service) throws LCServiceException {
		super(client, service);	
		updateServiceDocument();
	}
	
	/**
	 * Constructor to create a new Impersonated Activities Service helper object. This constructor is only going to be used in case of testing
	 * impersonation Activities APIs
	 * 
	 * This object contains helper methods for all API calls that are supported by the Activities service.
	 * 
	 * @param client	the authenticated AbderaClient that is used to handle requests to/from server
	 * @param service	the ServiceEntry that contains information about the Activities service from the server ServiceConfigs file
	 * @param headers the headers related to impersonation APIs
	 * @throws LCServiceException 
	 */
	
	public ActivitiesService(AbderaClient client, ServiceEntry service, Map<String, String> headers) throws LCServiceException {
		super(client, service);	
		for(String key : headers.keySet()){		
			this.options.setHeader(key, headers.get(key));
		}	
				
		updateServiceDocument();
	}
	
	/**
	 * Same as ActivitiesService(AbderaClient client, ServiceEntry service) Except can set isOauth = TRUE to use atom/oauth
	 * 
	 * @param client
	 * @param service
	 * @param isOauth
	 * @throws LCServiceException 
	 */
	public ActivitiesService(AbderaClient client, ServiceEntry service, boolean isOauth) throws LCServiceException {
		super(client, service);
		this.isOauth = isOauth;
		updateServiceDocument();
	}
	
	public String getServiceURLString(){
		return service.getServiceURLString();
	}
	
	private void updateServiceDocument() throws LCServiceException {
		String serviceLink = URLConstants.ACTIVITIES_SERVICE;
		
		if(isOauth)
			serviceLink = URLConstants.ACTIVITIES_OAUTH_SERVICE;
		
		ExtensibleElement feed = getFeed(service.getServiceURLString() + serviceLink);
		
		// TJB 10/7/15 Expanded to a 'while' loop - sometimes one 3 min wait is not enough.  
		int count = 1;
		while (getRespStatus() == 500 && (count < 3)){  // time out, system is not ready, wait for 3 mins. 
			LOGGER.debug(" ------activities get service call timed out-----wait for 3 mins and try again ---");
			try {
				Thread.sleep(180000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			count++;
			feed = getFeed(service.getServiceURLString() + serviceLink);
		}
		
		if(getRespStatus() != 200) {
			setFoundService(false);
			throw new LCServiceException("Error : Can't get ActivitiesService Feed"+super.getDetail());
		} else {
			setFoundService(true);
			activityDashboardURLs = getCollectionUrls((Service) feed);
		}
		
				
		/*if(feed != null) {
			if(getRespStatus() == 200) {			
			setFoundService(true);
			activityDashboardURLs = getCollectionUrls((Service) feed);
			} else {
				setFoundService(false);
				throw new LCServiceException("Error : Can't get ActivitiesService Feed"+message);
			}
		} else {
			setFoundService(false);
			throw new LCServiceException("Error : Can't get ActivitiesService Feed");
		}*/
	}

	public ArrayList<Activity> getMyActivities() {
		ArrayList<Activity> myActivities = new ArrayList<Activity>();
		ExtensibleElement activitiesFeed = getFeed(activityDashboardURLs.get(StringConstants.ACTIVITIES_OVERVIEW));
		
		if (activitiesFeed != null) {
			for(Entry activityEntry: ((Feed) activitiesFeed).getEntries()) {
				Activity data = new Activity(activityEntry);
				myActivities.add(data);
			}
		}
		
		return myActivities;
	}
	
	public ArrayList<Activity> getMy75Activities() {
		ArrayList<Activity> myActivities = new ArrayList<Activity>();
		ExtensibleElement activitiesFeed = getFeed(activityDashboardURLs.get(StringConstants.ACTIVITIES_OVERVIEW)+"?ps=75");
		
		if (activitiesFeed != null) {
			for(Entry activityEntry: ((Feed) activitiesFeed).getEntries()) {
				Activity data = new Activity(activityEntry);
				myActivities.add(data);
			}
		}
		
		return myActivities;
	}	
	
	public ArrayList<String> getTagCloudUrls(String Rel) {
		ArrayList<String> connections_urls = new ArrayList<String>();
		ArrayList<String> tagcloud_urls = new ArrayList<String>();
		
		connections_urls.add(activityDashboardURLs.get(StringConstants.ACTIVITIES_OVERVIEW));
		connections_urls.add(activityDashboardURLs.get(StringConstants.ACTIVITIES_COMPLETED));
		connections_urls.add(activityDashboardURLs.get(StringConstants.ACTIVITIES_OVERVIEW)+"?priority=High");
		connections_urls.add(activityDashboardURLs.get(StringConstants.ACTIVITIES_OVERVIEW)+"?priority=Medium");
		
		Iterator<String> itors = connections_urls.iterator();
		while( itors.hasNext()){
			ExtensibleElement activitiesFeed = getFeed(itors.next().toString());
			if (activitiesFeed != null) {
				tagcloud_urls.add(((Feed) activitiesFeed).getLinks(Rel).toString());
			}
		}

		return tagcloud_urls;
	}
	
	public ArrayList<String> getTodoTagCloudUrls(String Rel, String id) {
		ArrayList<String> connections_urls = new ArrayList<String>();
		ArrayList<String> tagcloud_urls = new ArrayList<String>();
		
		String baseUrl = activityDashboardURLs.get(StringConstants.ACTIVITIES_COMPLETED).replace("completed", "forms/todos?");
		connections_urls.add(baseUrl+"completedTodos=only");
		connections_urls.add(baseUrl+"completedTodos=no");
		connections_urls.add(baseUrl+"assignedToUserid="+id);
		connections_urls.add(baseUrl+"createdByUserId="+id);
		
		Iterator<String> itors = connections_urls.iterator();
		while( itors.hasNext()){
			ExtensibleElement activitiesFeed = getFeed(itors.next().toString());
			if (activitiesFeed != null) {
				tagcloud_urls.add(((Feed) activitiesFeed).getLinks(Rel).toString());
			}
		}

		return tagcloud_urls;
	}
	
	public ArrayList<Activity> getMyActivities(String email, NodeType nodeType, Priority priority, int page, int pageSize, Options isPublic, String textSearch, Date since, SortField sortField, SortOrder sortOrder, String tag, Options includeTemplates, String userid, String aclAlias, String[] aliasMemberEmail, String[] aliasMemberUserId) {
		ArrayList<Activity> myActivities = new ArrayList<Activity>();
		ExtensibleElement activitiesFeed = searchActivities(activityDashboardURLs.get(StringConstants.ACTIVITIES_OVERVIEW), email, null, nodeType, priority, page, pageSize, isPublic, textSearch, since, sortField, null, sortOrder, tag, includeTemplates, userid, aclAlias, aliasMemberEmail, aliasMemberUserId);
		
		if (activitiesFeed != null) {
			for(Entry activityEntry: ((Feed) activitiesFeed).getEntries()) {
				Activity data = new Activity(activityEntry);
				myActivities.add(data);
			}
		}

		return myActivities;
	}
	
	public ExtensibleElement getMyActivitiesFeed() {
		// /service/atom2/activities
		return getFeed(service.getServiceURLString() + URLConstants.ACTIVITIES_MY);
	}
	
	public ArrayList<Activity> getCompletedActivities(String email, NodeType nodeType, Priority priority, int page, int pageSize, Options isPublic, String textSearch, Date since, SortField sortField, SortOrder sortOrder, String tag, Options includeTemplates, String userid) {
		ArrayList<Activity> completedActivities = new ArrayList<Activity>();
		ExtensibleElement activitiesFeed = searchActivities(activityDashboardURLs.get(StringConstants.ACTIVITIES_COMPLETED), email, null, nodeType, priority, page, pageSize, isPublic, textSearch, since, sortField, null, sortOrder, tag, includeTemplates, userid, null, null, null);
		
		if (activitiesFeed != null) {
			for(Entry activityEntry: ((Feed) activitiesFeed).getEntries()) {
				Activity data = new Activity(activityEntry);
				completedActivities.add(data);
			}
		}

		return completedActivities;
	}
	
	public ExtensibleElement getFeed(String url){
		return super.getFeed(url);
	}
	
	public ArrayList<Activity> getAllActivities(String email, FieldType fieldType, NodeType nodeType, Priority priority, int page, int pageSize, Options isPublic, String textSearch, Date since, SortField sortField, SortOrder sortOrder, String tag, Options includeTemplates, String userid) {
		ArrayList<Activity> completedActivities = new ArrayList<Activity>();
		ExtensibleElement activitiesFeed = searchActivities(activityDashboardURLs.get(StringConstants.ACTIVITIES_EVERYTHING), email, fieldType, nodeType, priority, page, pageSize, isPublic, textSearch, since, sortField, null, sortOrder, tag, includeTemplates, userid, null, null, null);
		
		if (activitiesFeed != null) {
			for(Entry activityEntry: ((Feed) activitiesFeed).getEntries()) {
				Activity data = new Activity(activityEntry);
				completedActivities.add(data);
			}
		}

		return completedActivities;
	}
	
	public ArrayList<Todo> getAllTodos(String assignedto, String assignedToUserid, String email, FieldType fieldType, Options includeunassigned, NodeType nodeType, Priority priority, int page, int pageSize, Options isPublic, String textSearch, Date since, SortField sortField, SortOrder sortOrder, String tag, Options templates, String userid) {
		Feed todoFeed = (Feed) searchTodos(assignedto, assignedToUserid, email, fieldType, includeunassigned, nodeType, priority, page, pageSize, isPublic, textSearch, since, sortField, sortOrder, tag, templates, userid);
		
		ArrayList<Todo> todoList = new ArrayList<Todo>();
		for(Entry entry : todoFeed.getEntries()) {
			todoList.add(new Todo(entry));
		}
		
		return todoList;
	}
	
	public ArrayList<Category> getAllActivityTags() {
		Categories categoryDocument = (Categories) getFeed(service.getServiceURLString() + URLConstants.ACTIVITIES_TAGS + "?ps=100");

		return new ArrayList<Category>(categoryDocument.getCategories());
	}
	
	public ArrayList<Entry> getAllTemplates() {
		Feed templateFeed = (Feed) searchActivities(activityDashboardURLs.get(StringConstants.ACTIVITIES_EVERYTHING), null, null, null, null, 0, 0, null, null, null, null, null, null, null, Options.ONLY, null, null, null, null);

		//Fix for defect 122490 - add <snx:role/> to contributor element in each template entry returned
		for (Entry e : templateFeed.getEntries()){
			Element contributorElem = null;
			for (Element elem : e.getElements())
				if (elem.getQName().getLocalPart().equals("contributor"))
					contributorElem = elem;
			Element snxRole = new Abdera().getFactory().newExtensionElement(StringConstants.SNX_ROLE, contributorElem);
			snxRole.setText("owner");
		}
		
		ArrayList<Entry> templateList = new ArrayList<Entry>();
		for(Entry entry : templateFeed.getEntries()) {
			templateList.add(entry);
			Activity test = new Activity(entry);
			assert(test != null);
		}
		
		return templateList;
	}
	
	public ExtensibleElement postFeed(String url, ExtensibleElement entry){
		return super.postFeed(url, entry);
	}
	
	public ExtensibleElement getDeletedActivitesFeed(){
		return getFeed(activityDashboardURLs.get(StringConstants.ACTIVITIES_TRASH));
	}
	
	public ExtensibleElement createActivity(Activity activity) {
		return postFeed(activityDashboardURLs.get(StringConstants.ACTIVITIES_OVERVIEW), activity.toEntry());
	}
	
	public ExtensibleElement createCommunityActivity(String publishURL,Activity activity,String communityUuid, String communityURL) {
		return postFeed(publishURL, activity.toCommunityEntry(communityUuid,communityURL));
	}
	
	public ExtensibleElement editActivity(String activityEditURL, Activity activity) {
		return putFeed(activityEditURL, activity.toEntry());
	}
	
	public ExtensibleElement getActivity(String activityEditURL) {
		return getFeed(activityEditURL);
	}
	
	public boolean deleteActivity(String activityEditURL) {
		return deleteFeed(activityEditURL);
	}
	
	public ExtensibleElement updateActivityPriority(String activityId, String priority, Entry activityEntry){
		return postFeed(service.getServiceURLString() + "/service/html/post/changeactivitypriority?uuid=" + activityId + "&priority=" +priority, activityEntry);
	}
	
	public int updateActivityPriorityCRX(String activityId, Entry activityEntry) throws IOException{
		return postFeedWithCRX(service.getServiceURLString() + "/service/atom2/activity?activityUuid=" + activityId, activityEntry);
	}
	
	/**
	 * Gets the trashednode entry, removes the 
	 * <category scheme="http://www.ibm.com/xmlns/prod/sn/flags" term="deleted"/>
	 * flag element from the entry, and calls putFeed to restore it.
	 */
	public ExtensibleElement restoreActivity(String activityId) {
		//The end of the URL to be gotten is is hardcoded in because from what I can see 
		//there is no way to find trashednode programmatically 
		Entry trashedEntry = (Entry) getFeed(service.getServiceURLString()+"/service/atom2/trashednode?activityNodeUuid="+activityId);
		trashedEntry.getCategories().get(2).discard(); //remove the "deleted" flag 
		return putFeed(trashedEntry.getEditLink().getHref().toString(), trashedEntry);
	}
	
	public ExtensibleElement restoreActivityNode(String nodeId) {
		Entry trashedEntry = (Entry) getFeed(service.getServiceURLString()+"/service/atom2/trashednode?activityNodeUuid="+nodeId);
		//remove the "deleted" flag
		List<Category> categories = trashedEntry.getCategories();
		for (Category category : categories) {
			if ("deleted".equalsIgnoreCase(category.getTerm()))
				category.discard();
		}
		return putFeed(trashedEntry.getEditLink().getHref().toString(), trashedEntry);
	}
	
	public ExtensibleElement addNodeToActivity(String activityAppCollectionURL, LCEntry node) {
		return postFeed(activityAppCollectionURL, node.toEntry());
	}

	public ExtensibleElement editNodeInActivity(String nodeEditURL, LCEntry node) {
		return putFeed(nodeEditURL, node.toEntry());
	}

	public ExtensibleElement editNodeInActivity(String nodeEditURL, Entry node) {
		return putFeed(nodeEditURL, node);
	}
	
	public ExtensibleElement updateActivity(String editLink, Entry ntry) {
		return putFeed(editLink, ntry);
	}
	
	/*public ExtensibleElement addMultipartNodeToActivityImpersonated(String url, LCEntry node, File media) {
		return postMultipartFeedImpersonated(url, node.toEntry(), media);
	}*/
	
	public ExtensibleElement addMultipartNodeToActivity(String url, LCEntry node, File media) {
		return postMultipartFeed(url, node.toEntry(), media);
	}
	public ExtensibleElement addMultipartNodeToActivity64(String url, LCEntry node, File media) {
		return postMultipartFeedWithBase64(url, node.toEntry(), media);
	}
	/** 
	* Note: Remove the <category scheme="http://www.ibm.com/xmlns/prod/sn/flags" term="deleted"/> 
	* flag element from the entry before restoring it.
	*/
	public ExtensibleElement restoreNodeInActivity(String trashedNodeEditURL, LCEntry trashedNode) {
		return putFeed(trashedNodeEditURL, trashedNode.toEntry());
	}
	
	public boolean removeNodeFromActivity(String nodeEditURL) {
		return deleteFeed(nodeEditURL);
	}
	
	public ArrayList<Entry> getActivityNodes(String activityAppCollectionURL) {
		ArrayList<Entry> nodes = new ArrayList<Entry>();
		ExtensibleElement nodesFeed = getFeed(activityAppCollectionURL);
		
		if (nodesFeed != null) {
			for(Entry nodeEntry: ((Feed) nodesFeed).getEntries()) {
				nodes.add(nodeEntry);
			}
		}
		
		return nodes;
	}
	
	public ExtensibleElement moveNode(String nodeUuid, String newParentUuid) {
	    String baseUrl = activityDashboardURLs.get(StringConstants.ACTIVITIES_COMPLETED).replace("completed", "moveEntry");
              
        baseUrl = baseUrl.concat("?activityNodeUuid=").concat(nodeUuid).concat("&destNodeUuid=").concat(newParentUuid);
	    ExtensibleElement e = null;
        return postFeed(baseUrl, e);

	} 
	
	public ExtensibleElement addMemberToActivity(String activityACLURL, Member member) {
		return postFeed(activityACLURL, member.toEntry());
	}
	
	public ExtensibleElement addMembersToActivity(String activityUrl, InputStream inputStream){
		return postFeed(activityUrl, inputStream); 
	}
	
	public void removeMembersFromActivity(String activityUrl, String contentBody) throws Exception{
		deleteFeedWithBody(activityUrl,contentBody);
	}
	
	public ExtensibleElement getMemberFromActivity(String activityACLNodeEditURL) {
		return getFeed(activityACLNodeEditURL);
	}
	public ExtensibleElement getNotesMemberFromActivity(String activityACLNodeEditURL) {
		return getNotesFeed(activityACLNodeEditURL);
	}
	public ExtensibleElement updateMemberInActivity(String activityACLNodeEditURL, Member updatedMember) {
		return putFeed(activityACLNodeEditURL, updatedMember.toEntry());
	}
	
	public ExtensibleElement updateMemberInActivity(String activityACLNodeEditURL, ExtensibleElement entry) {
		return putFeed(activityACLNodeEditURL, entry);
	}
	public boolean removeMemberFromActivity(String memberACLURL) {
		return deleteFeed(memberACLURL);
	}
	
	public ExtensibleElement createEntryTemplate(Activity parentActivity, ActivityEntry entry) {
		return postFeed(parentActivity.getAppCollection().getHref().toString(), entry.toEntry());
	}
	
	public ExtensibleElement createEntryTemplate(Entry result, Entry result2, File media) {
		return putMultipartFeed(result.getEditLinkResolvedHref().toString(), result2, media);
	}
	
	public ExtensibleElement editEntryTemplate(String templateURL, ActivityEntry entry) {
		return putFeed(templateURL, entry.toEntry());
	}
	
	public ExtensibleElement editEntryTemplate(String templateURL, ActivityEntry entry, File media) {
		return putMultipartFeed(templateURL, entry.toEntry(), media);
	}
	
	public ExtensibleElement getEntryTemplate(String templateURL) {
		return getFeed(templateURL);
	}
	
	public boolean deleteEntryTemplate(String templateURL) {
		return deleteFeed(templateURL);
	}
	
	public ExtensibleElement getToDoCommentFeed(String activityId){
		return getFeed(service.getServiceURLString() + URLConstants.ACTIVITIES_TODO_CONTENT + "?nodeUuid=" + activityId + "&includeMultiAssignedTodo=yes");
	}
	
	public ExtensibleElement searchActivities(String sourceURL, String email, FieldType fieldType, NodeType nodeType, Priority priority, int page, int pageSize, Options isPublic, String textSearch, Date since, SortField sortField, SortBy sortBy, SortOrder sortOrder, String tag, Options templates, String userid, String aclAlias, String[] memberEmail, String[] memberId) {
		String searchPath = sourceURL;
		
		if(searchPath.lastIndexOf("?") == -1 )
			searchPath += "?";
		else
			searchPath += "&";
		
		if(email != null && email.length() != 0)
			searchPath += "email=" + email + "&";
		
		if(fieldType != null)
			searchPath += "fieldType=" + String.valueOf(fieldType).toLowerCase() + "&";
		
		if(nodeType != null)
			searchPath += "nodeType=" + String.valueOf(nodeType).toLowerCase() + "&";
		
		if(priority != null)
			searchPath += "priority=" + String.valueOf(priority).toLowerCase() + "&";
		
		if(page > 0)
			searchPath += "page=" + page + "&";
		
		if(pageSize > 0)
			searchPath += "ps=" + pageSize + "&";
		
		if(isPublic != null)
			searchPath += "public=" + String.valueOf(isPublic).toLowerCase() + "&";
		
		if(textSearch != null && textSearch.length() != 0)
			searchPath += "search=" + textSearch + "&";
		
		if(since != null)
			searchPath += "since=" + Utils.dateFormatter.format(since) + "&";
		
		if(sortField != null)
			searchPath += "sortfields=" + String.valueOf(sortField).toLowerCase() + "&";
		
		if(sortBy != null)
			searchPath += "sortby=" + String.valueOf(sortBy).toLowerCase() + "&";
		
		if(sortOrder != null)
			searchPath += "sortorder=" + String.valueOf(sortOrder).toLowerCase() + "&";
		
		if(tag != null && tag.length() != 0)
			searchPath += "tag=" + tag + "&";
		
		if(templates != null)
			searchPath += "templates=" + String.valueOf(templates).toLowerCase() + "&";
		
		if(userid != null && userid.length() != 0)
			searchPath += "userid=" + userid + "&";
		
		if(aclAlias != null){
			searchPath += "aclAlias=" + aclAlias + "&";
		}
		
		if(memberEmail != null && memberEmail.length > 0){
			for(String mail:memberEmail){
				searchPath += "email=" + mail + "&";
			}
		}else if(memberId != null && memberId.length > 0){
			for(String id:memberId){
				searchPath += "userid=" + id + "&";
			}
		}
		
		return getFeed(searchPath);
	}
	
	public ExtensibleElement searchTodos(String assignedto, String assignedToUserid, String email, FieldType fieldType, Options includeunassigned, NodeType nodeType, Priority priority, int page, int pageSize, Options isPublic, String textSearch, Date since, SortField sortField, SortOrder sortOrder, String tag, Options templates, String userid) {
		String searchPath = activityDashboardURLs.get(StringConstants.ACTIVITIES_TO_DO_LIST);
		
		if(searchPath.lastIndexOf("?") == -1 )
			searchPath += "?";
		else
			searchPath += "&";

		if(!service.isEmailHidden())
			searchPath += "assignedto=" + assignedto + "&";
		else
			searchPath += "assignedToUserid=" + assignedToUserid + "&";
		
		if(email != null && email.length() != 0)
			searchPath += "email=" + email + "&";
		
		if(fieldType != null)
			searchPath += "fieldType=" + String.valueOf(fieldType).toLowerCase() + "&";
		
		if(includeunassigned != null)
			searchPath += "includeunassigned=" + String.valueOf(includeunassigned).toLowerCase() + "&";
		
		
		if(nodeType != null)
			searchPath += "nodeType=" + String.valueOf(nodeType).toLowerCase() + "&";
		
		if(priority != null)
			searchPath += "priority=" + String.valueOf(priority).toLowerCase() + "&";
		
		if(page > 0)
			searchPath += "page=" + page + "&";
		
		if(pageSize > 0)
			searchPath += "ps=" + pageSize + "&";
		
		if(isPublic != null)
			searchPath += "public=" + String.valueOf(isPublic).toLowerCase() + "&";
		
		if(textSearch != null && textSearch.length() != 0)
			searchPath += "search=" + textSearch + "&";
		
		if(since != null)
			searchPath += "since=" + Utils.dateFormatter.format(since) + "&";
		
		if(sortField != null)
			searchPath += "sortfields=" + String.valueOf(sortField).toLowerCase() + "&";
		
		if(sortOrder != null)
			searchPath += "sortorder=" + String.valueOf(sortOrder).toLowerCase() + "&";
		
		if(tag != null && tag.length() != 0)
			searchPath += "tag=" + tag + "&";
		
		if(templates != null)
			searchPath += "templates=" + String.valueOf(templates).toLowerCase() + "&";
		
		if(userid != null && userid.length() != 0)
			searchPath += "userid=" + userid + "&";
		
		return getFeed(searchPath);
	}
	
	public ExtensibleElement getAPIVersion(){
		return getFeed(service.getServiceURLString() + URLConstants.ACTIVITIES_SERVER + "/version");
	}
	
	public ExtensibleElement getActivitiesMemberInfo(){
		return getFeed(service.getServiceURLString() + URLConstants.ACTIVITIES_SERVER + "/memberprofile?email=" + StringConstants.USER_EMAIL);
	}
	
	public ExtensibleElement getActivityDocument(String activityId){
		return getFeed(service.getServiceURLString() + URLConstants.ACTIVITIES_SERVER + "/trashednode?activityNodeUuid=" + activityId);
	}
	
		
	public ExtensibleElement getRecentUpdatesFeed(){
		return getFeed(service.getServiceURLString() + URLConstants.ACTIVITIES_SERVER + "/history?tunedout=no&public=no&completed=no&count=20");
	}
	
	public ExtensibleElement searchMyActivites(String searchTerm){
		return getFeed(service.getServiceURLString() + "/service/atom2/activities?search=" + encodeURL(searchTerm));
	}
	
	public String encodeURL(String url) {
		String newURL = "";
		if(url.contains(" ")){
			newURL = url.replace(" ", "%20");
			return newURL;
		}
		
		return url;
	}
	
	public void deleteTests(){
		ArrayList<Activity> activity = getMyActivities();
		for(int i = 0; i < activity.size(); i++){
			if(activity.get(i).getTitle().equals("General Survival") || activity.get(i).getTitle().equals("Brogramming") ||  activity.get(i).getTitle().equals("Updated")){
				deleteActivity(activity.get(i).getEditHref());
			}
		}
	}
	
	public Feed getCommunityActivity(Entry communityActivityEntry) {
		String feedHref = null;
		Link l = communityActivityEntry.getLink("http://www.ibm.com/xmlns/prod/sn/remote-application/feed");
		if (l == null)
			return null;
		feedHref = l.getHref().toString();
		ExtensibleElement ee = getFeed(feedHref);
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
	
	public ExtensibleElement getFollowing(){
		//https://hanw510.notesdev.ibm.com/activities/follow/atom/resources?source=activities&type=activity
		return getFeed(service.getServiceURLString() + "/service/atom2/activities?search=" );
	}
	
	public ExtensibleElement moveToActivity(String moveID, String targetActivityId) throws Exception {
		//TODO: nonce id is not support from http client..
		//return doHttpPost(service.getServiceURLString() + "/service/html/post/moveEntry?uuid="+moveID+"&targetUuid="+targetActivityId+"&position=0&dangerousurlnonce=", "");
		return postFeed(service.getServiceURLString() + "/service/html/post/moveEntry?uuid="+moveID+"&targetUuid="+targetActivityId+"&position=0&dangerousurlnonce=", Abdera.getNewFactory().newEntry());
	}

	public ExtensibleElement getActivityDescendants(String activityUuid, Integer page, Integer ps, String sortfields, Integer sortorder){
		String path = service.getServiceURLString() + "/service/atom2/activitydescendants?activityUuid=" + activityUuid;
		if (null != page)
			path += "&page=" + page;
		if (null != ps)
			path += "&ps=" + ps;
		if (null != sortfields)
			path += "&sortfields=" + sortfields;
		if (null != sortorder)
			path += "&sortorder=" + sortorder;

		return getFeed(path);
	}
	public ExtensibleElement getActivityDescendants(String activityUuid, Integer page, Integer ps, String sortfields, Integer sortorder, Long since, Long until, String rangeId){
		String path = service.getServiceURLString() + "/service/atom2/activitydescendants?activityUuid=" + activityUuid;
		if (null != page)
			path += "&page=" + page;
		if (null != ps)
			path += "&ps=" + ps;
		if (null != sortfields)
			path += "&sortfields=" + sortfields;
		if (null != sortorder)
			path += "&sortorder=" + sortorder;
		if (null != since)
			path += "&since=" + since;
		if (null != until)
			path += "&until=" + until;
		if (null != rangeId)
			path += "&rangeId=" + rangeId;

		return getFeed(path);
	}
	
	/**
	 * Get the event logs of an Activity from within a date range.
	 * 
	 * @param nodeUuid Uuid of the Activity.
	 * @param since Date of events returned will be >= this date.
	 * @param until Date of events returned will be strictly < this date.
	 * @return
	 */
	public ExtensibleElement getEventLog(String activityUuid, java.util.Date since, java.util.Date until){
		String url = service.getServiceURLString() + URLConstants.ACTIVITIES_SERVER + "/activity/history?activityUuid=" + activityUuid;
		if(since != null)
			url += "&since=" + since.getTime();
		if(until != null)
			url += "&until=" + until.getTime();
		return getFeed(url);
	}

	/**
	 * Get the event logs of an Activity
	 * 
	 * @param nodeUuid Uuid of the Activity.
	 * @return
	 */
	public ExtensibleElement getEventLog(String activityUuid){
		return getEventLog(activityUuid, null, null);
	}
	
	public ExtensibleElement getNodeChildrenSinceUntil(String activityNodeUuid, Integer page, Integer ps, String sortfields, Integer sortorder, Long since, Long until, String rangeId){
		String path = service.getServiceURLString() + "/service/atom2/nodechildren?activityNodeUuid=" + activityNodeUuid;
		if (null != page)
			path += "&page=" + page;
		if (null != ps)
			path += "&ps=" + ps;
		if (null != sortfields)
			path += "&sortfields=" + sortfields;
		if (null != sortorder)
			path += "&sortorder=" + sortorder;
		if (null != since)
			path += "&since=" + since;
		if (null != until)
			path += "&until=" + until;
		if (null != rangeId)
			path += "&rangeId=" + rangeId;

		return getFeed(path);
	}


	public ExtensibleElement getNodeChildren(
			String parentUuid){
		return getNodeChildren(parentUuid, null, null, null, null);
	}

	public ExtensibleElement getNodeChildren(
			String parentUuid, Integer page, Integer ps, String sortfields, Integer sortorder){
		return getNodeChildren(parentUuid, page, ps, sortfields, sortorder, null, null, null);
	}

	public ExtensibleElement getNodeChildren(
			String parentUuid, Integer page, Integer ps, String sortfields, Integer sortorder, 
			String sinceUuid, String untilUuid, String excludedUuids){
		String path = service.getServiceURLString() + "/service/atom2/nodechildren?nodeUuid=" + parentUuid;
		if (null != page)
			path += "&page=" + page;
		if (null != ps)
			path += "&ps=" + ps;
		if (null != sortfields)
			path += "&sortfields=" + sortfields;
		if (null != sortorder)
			path += "&sortorder=" + sortorder;
		if (null != sinceUuid)
			path += "&sinceUuid=" + sinceUuid;
		if (null != untilUuid)
			path += "&untilUuid=" + untilUuid;
		if (null != excludedUuids)
			path += "&excludedUuids=" + excludedUuids;

		return getFeed(path);
	}

	public String getChildrenLink(Entry entry) {
		Link link = entry.getLink(StringConstants.REL_CHILDREN);
		return (link != null ? link.getHref().toString() : null);
	}
	
	public ExtensibleElement getActivitySections(String activityUuid){
		return getFeed(service.getServiceURLString() + "/service/atom2/threaded?nodeUuid=" + activityUuid + "&nodeType=section");
	}
}