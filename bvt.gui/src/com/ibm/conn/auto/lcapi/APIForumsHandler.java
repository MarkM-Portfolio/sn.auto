package com.ibm.conn.auto.lcapi;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;


import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.protocol.client.AbderaClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.lcapi.common.APIHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.ForumsUtils;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumReply;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.forums.ForumsService;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class APIForumsHandler extends APIHandler<ForumsService>{

	
	private static final Logger log = LoggerFactory.getLogger(APIForumsHandler.class);
	
	
	public APIForumsHandler(String serverURL, String username, String password) {
		super("forums", serverURL, username, password);
	}


	@Override
	protected ForumsService getService(AbderaClient abderaClient, ServiceEntry generalService) {
		try {
			return new ForumsService(abderaClient, generalService);
		} catch (LCServiceException e) {
			Assert.fail("Unable create Forums service: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Creates a standalone forum
	 * 
	 * @param forum - The BaseForum instance of the standalone forum to be created
	 * @return - The Forum instance of the new standalone forum
	 */
	public Forum createForum(BaseForum forum){
		
		log.info("INFO: Now creating a new standalone forum with title: " + forum.getName());
		
		// Create the Forum instance of the new standalone forum - also set the forum tags
		Forum apiForum = new Forum(forum.getName(), forum.getDescription());
		apiForum.setTags(forum.getTags().trim());
		
		// Create the forum using a POST request
		Entry forumResult = (Entry) service.createForum(apiForum);
		
		// Create a Forum instance based on the newly created standalone forum
		Forum result = new Forum (forumResult);
		
		log.info("INFO: Verify that the standalone forum has been created successfully");
		assertTrue(forumResult != null);

		if (APIUtils.resultSuccess(forumResult, "Forums")) {
			log.info("INFO: Finished creating the new standalone forum with title: " + apiForum.getTitle());
			return result;
		} else {
			log.info("ERROR: The new standalone forum could NOT be created");
			log.info(forumResult.toString());
			return null;
		}
	}
	
	/**
	 * Creates a new forum topic in the BaseForumTopics specified parent forum
	 * 
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic to be created
	 * @return - The ForumTopic instance of the forum topic if all actions are successful, null otherwise
	 */
	public ForumTopic createForumTopic(BaseForumTopic baseForumTopic) {
		
		log.info("INFO: Now creating a new forum topic with title: " + baseForumTopic.getTitle());
		
		// Create a ForumTopic instance based on all relevant parameters set in the BaseForumTopic instance
		ForumTopic forumTopic = new ForumTopic(baseForumTopic.getTitle(), baseForumTopic.getDescription(), false, false, baseForumTopic.getMarkAsQuestion(), false);
		forumTopic.setTags(baseForumTopic.getTags().trim());
		
		// Convert the ForumTopic instance to an Entry and re-add the tags (otherwise the tags are lost)
		Entry forumTopicEntry = forumTopic.toEntry();
		forumTopicEntry.addCategory(forumTopic.getTags().get(0));
		log.info("INFO: The Entry to represent the new forum topic has been created: " + forumTopicEntry.toString());
		
		// Create the URL to POST the Entry to
		String postRequestURL = baseForumTopic.getParentForum_API().getRepliesLink();
		log.info("INFO: The URL to POST the Entry to has been created: " + postRequestURL);
		
		Entry createForumTopicResult = (Entry) service.postEntry(postRequestURL, forumTopicEntry);
		
		if(createForumTopicResult.toString().indexOf("resp:error=\"true\">") > -1) {
			log.info("ERROR: The forum topic response could NOT be created");
			log.info(createForumTopicResult.toString());
			return null;
		} else {
			log.info("INFO: The forum topic was created successfully");
			return new ForumTopic(createForumTopicResult);
		}
	}
	
	public ForumReply createForumReply(ForumTopic parent, String content){
		
		Entry parentEntry = parent.toEntry();
		parentEntry.setId(parent.getId().toString());
		for(java.util.Map.Entry<String, Link> link : parent.getLinks().entrySet()) {
			parentEntry.addLink(link.getValue());
		}
		ForumReply reply = new ForumReply("Topic Reply",content,parentEntry,false);
		
		
		Entry postResult = (Entry) service.createForumReply(parentEntry, reply);
		
		ForumReply result  = new ForumReply(postResult);
		return result;
		
		
		
	}
	public void editForum(Forum forum, String content){
		
		forum.setContent(content);
		service.editForum(forum.getEditLink(), forum);
		
	}
	public void editTopic(ForumTopic topic, String content){
		
		topic.setContent(content);
		
		service.editForumTopic(topic.getEditLink(), topic);
			
	}
	public void editReply(ForumReply reply,String content){
		
		reply.setContent(content);
		
		service.editForumReply(reply.getEditLink(), reply);
		
	}
	public void createFollow(Forum forum){
		
		
		Entry newEntry = Abdera.getNewFactory().newEntry();
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/source", "forums", "forums");
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-type", "forum", "forum");
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-id", forum.getId().toString().substring(23), forum.getId().toString().substring(23));

		String URLConstruct=forum.getEditLink();
		String URLConstruct2 = URLConstruct.substring(0, URLConstruct.lastIndexOf("/atom/"));
		String URL = URLConstruct2 + "/follow/atom/resources";
		
		ExtensibleElement test =service.postEntry(URL, newEntry);
		
		if(test!=null){
		log.info("INFO: Following Forum action complete");
		}else{
			log.info("ERROR: Following Forum action not complete due to error");
		}
		
		}
	public void createFollow(ForumTopic topic){
		
		Entry newEntry = Abdera.getNewFactory().newEntry();
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/source", "forums", "forums");
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-type", "forum_topic", "forum_topic");
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-id", topic.getId().toString().substring(23), topic.getId().toString().substring(23));
		newEntry.setId(topic.getId().toString());
		
		String URLConstruct=topic.getEditLink();
		String URLConstruct2 = URLConstruct.substring(0, URLConstruct.lastIndexOf("/atom/"));
		String URL = URLConstruct2 + "/follow/atom/resources/"+topic.getId().toString().substring(23);
		
		ExtensibleElement postResult = service.postEntry(URL, newEntry);
		
		if(postResult!=null)
		{
		log.info("INFO: Following Topic action complete");
		}else{
			
			log.info("Error: Following Topic action not completed due to error");
		}
		
	}
	public String like(ForumTopic topic){
				
		Entry topicEntry = topic.toEntry();
		topicEntry.setId(topic.getId().toString());
		for(java.util.Map.Entry<String, Link> link : topic.getLinks().entrySet()) {
			topicEntry.addLink(link.getValue());
		}
		
		Entry recommendEntry = Abdera.getNewFactory().newEntry();
		recommendEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "recommendation", null);
		recommendEntry.setTitle("like");
		
		String URL = topicEntry.getLink(StringConstants.RECOMMENDATIONS).getHref().toString();
		log.info("INFO: URL = " + URL);
		
		try {
			service.postRecommendEntry(URL, recommendEntry);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return URL;
	}
	
	/**
	 * 
	 * @param URL - The URL is returned by the like(ForumTopic topic) method (above)
	 * @return deleted - A boolean that is set to true if service.deleteRecommendEntry(URL) is successful
	 */
	public boolean unlike(String URL) {
		
		boolean deleted = false;
		
		try {
			service.deleteRecommendEntry(URL);
			deleted = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return deleted;
	}
	
	public void like(ForumReply reply) throws Exception{
		
		Entry replyEntry = reply.toEntry();
		replyEntry.setId(reply.getId().toString());
		for(java.util.Map.Entry<String, Link> link : reply.getLinks().entrySet()) {
			replyEntry.addLink(link.getValue());
		}
		
		Entry recommendEntry = Abdera.getNewFactory().newEntry();
		recommendEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "recommendation", null);
		recommendEntry.setTitle("like");
		
		String URL = replyEntry.getLink(StringConstants.RECOMMENDATIONS).getHref().toString();
		service.postRecommendEntry(URL, recommendEntry);
		
		
	}
	
	@Deprecated
	/**
	 * Deprecated due to introduction of
	 * apiCreateTopicMention(BaseForumTopic baseTopic, Mentions mentions)
	 * below
	 */
	public ForumTopic createTopicMention(User testUser,String userID,BaseForumTopic baseTopic){
		
		ForumTopic newForumTopic = new ForumTopic(baseTopic.getTitle(),baseTopic.getDescription(),false,false,false,false);
		Entry postEntry = newForumTopic.toEntry();
		String serverURL = URLConstants.SERVER_URL;
		postEntry.setContentAsHtml("<p dir='ltr'><span class='vcard'><a class='fn url' href='" + serverURL + "/profiles/html/profileView.do?userid=" + userID + "'>@" + testUser.getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + userID + "</span></span></p>");
		
		Entry newEntry = (Entry) service.postEntry(baseTopic.getParentForum_API().getRepliesLink(), postEntry);
		
		ForumTopic result = new ForumTopic(newEntry);
		return result;

	}
	
	/**
	 * 
	 * @param baseTopic - The topic in which the mention will be made
	 * @param mentions - The Mentions object which contains all the necessary information about the user who will be mentioned
	 * @return result - A ForumReply object
	 */
	public ForumTopic apiCreateTopicMention(BaseForumTopic baseTopic, Mentions mentions){
		
		ForumTopic newForumTopic = new ForumTopic(baseTopic.getTitle(),baseTopic.getDescription(),false,false,false,false);
		Entry postEntry = newForumTopic.toEntry();
		
		postEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "/profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
		
		Entry newEntry = (Entry) service.postEntry(baseTopic.getParentForum_API().getRepliesLink(), postEntry);
		
		ForumTopic result = new ForumTopic(newEntry);
		return result;

	}
	
	public ForumReply createTopicReplyMention(ForumTopic parent, Mentions mentions){
		
		Entry parentEntry = parent.toEntry();
		parentEntry.setId(parent.getId().toString());
		
		for(java.util.Map.Entry<String, Link> link : parent.getLinks().entrySet()) {
			parentEntry.addLink(link.getValue());
		}
		
		ForumReply reply = new ForumReply("Topic Reply","string", parentEntry, false);
		
		Entry postEntry =reply.toEntry();
		
		postEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "/profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
		
		Entry postResult = (Entry) service.postEntry(parent.getRepliesLink(), postEntry);
		
		ForumReply result  = new ForumReply(postResult);
		return result;
		
	}
	
	public void deleteForum(Forum forum){
		
		log.info("INFO: Deleting the forum");
		service.deleteForum(forum.getEditLink());
	}
	
	/**
	 * return the forum whose name is the community name
	 * @param communityIID -- like c122a3d7-f577-49dd-9911-294bcb822c13
	 * @param forumName
	 * @return
	 */
	public Forum getDefaultCommForum(String communityUUID, String forumName) {
		for(Forum c: getForumsByComm(communityUUID)) {
			if(c.getTitle().equalsIgnoreCase(forumName))
				return c;
		}
		return null;
	}
	/**
	 * return the forums list by the community UUID
	 * @param communityUUID -- like c122a3d7-f577-49dd-9911-294bcb822c13
	 * @return
	 */
	public ArrayList<Forum> getForumsByComm(String communityUUID) {
		
					
			Feed feed =(Feed)service.getAllForums(communityUUID, null, 0, 0, 
												null, null, null, null, null, null,null);
			ArrayList<Forum> forums = new ArrayList<Forum>();
			for(Entry ForumEntry : feed.getEntries()) {
				forums.add(new Forum(ForumEntry));
			}
			return forums;
			
		
	}
	/**
	 * accepts this reply as anwser
	 * @param apiReply - will be accepted as Answer
	 * @return -- true, if successful; false, it failed
	 * @throws Exception
	 */
	public boolean acceptAsAnswer(ForumReply apiReply) throws Exception{
		apiReply.setAnswer(true);
		
		Entry answerEntry = apiReply.toEntry();
		for(java.util.Map.Entry<String, Link> link : apiReply.getLinks().entrySet()) {
			answerEntry.addLink(link.getValue());
		}
		
		answerEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/flags","anwser",null);
		answerEntry.setTitle("anwser");
		
		Entry acceptResult = (Entry)service.putEntry(apiReply.getEditLink(), answerEntry);
		if (APIUtils.resultSuccess(acceptResult, "Forums")) {			
			return true;
		} else {			
			return false;
		}
		
	}
	
	/**
	 * pin the topic
	 * @param apiTopic
	 */
	public void pinTopic(ForumTopic apiTopic){
		Entry topicEntry = apiTopic.toEntry();
		topicEntry.setId(apiTopic.getId().toString());
		for(java.util.Map.Entry<String, Link> link : apiTopic.getLinks().entrySet()) {
			topicEntry.addLink(link.getValue());
		}
		topicEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/flags","pinned",null);
		
		Entry resultEntry = (Entry)service.putEntry(apiTopic.getEditLink(), topicEntry);
		assertTrue(APIUtils.resultSuccess(resultEntry, "Forums"));
	}
	/**
	 * lock the topic
	 * @param apiTopic
	 */
	public void lockTopic(ForumTopic apiTopic){
		Entry topicEntry = apiTopic.toEntry();
		topicEntry.setId(apiTopic.getId().toString());
		for(java.util.Map.Entry<String, Link> link : apiTopic.getLinks().entrySet()) {
			topicEntry.addLink(link.getValue());
		}
		topicEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/flags","locked",null);
		Entry resultEntry = (Entry)service.putEntry(apiTopic.getEditLink(), topicEntry);
		assertTrue(APIUtils.resultSuccess(resultEntry, "Forums"));
	
	}
	/**
	 * get topic entries from feed
	 * @param url - feed's url
	 * @return get topic entries from feed
	 */
	public List<Entry> getTopicsFeed(String url){
		Feed feed = (Feed)service.getForumTopic(url);
		
		return feed.getEntries();
	}
	
	
	/**
	 * Creates a new forum in a community
	 * 
	 * @param url - The String content of the url required to create the community forum
	 * @param forum - The BaseForum instance of the forum to be created
	 * @return - The Forum instance of the newly created community forum
	 */
	public Forum createCommunityForum(String url, BaseForum forum) {
		
		log.info("INFO: Now creating a new community forum with title: " + forum.getName());
		
		log.info("INFO: The URL required to create the forum has been received by the API method: " + url);
		
		// Create a Forum instance of the forum to be created - also set the tags for the new forum
		Forum apiForum = new Forum(forum.getName(),forum.getDescription());
		apiForum.setTags(forum.getTags().trim());
		
		// Convert the Forum instance of the forum to be created to an Entry
		Entry forumEntry = apiForum.toEntry();
		log.info("INFO: The Entry instance of the new community forum has been created: " + forumEntry.toString());
	
		// POST the Entry instance of the new community forum to the URL
		Entry entry = (Entry) service.postEntry(url, forumEntry);
		
		log.info("INFO: Verify that the new community forum has been created successfully");
		assertTrue(APIUtils.resultSuccess(entry, "Forums"));
		
		return new Forum(entry);
	}
	
	/**
	 * This method add the given user to the target forum as an owner.
	 * @param apiForum -- the target forum
	 * @param testuser -- the user which will be added to the target forum as an owner
	 */
	public void addOwnertoForum(Forum apiForum, User testuser){

		
		
		Factory factory = Abdera.getNewFactory();
		Entry entry = factory.newEntry();
		entry.setAttributeValue("xmlns:app", "http://www.w3.org/2007/app");
		entry.setAttributeValue("xmlns:snx", "http://www.ibm.com/xmlns/prod/sn");
		entry.setAttributeValue("xmlns:thr", "http://purl.org/syndication/thread/1.0");
	
		entry.addContributor(null,testuser.getEmail(),null);

		entry.addCategory(StringConstants.SCHEME_TYPE,"person",null);		

		Element role = factory.newElement(new QName("http://www.ibm.com/xmlns/prod/sn", "snx:role"));
		role.setAttributeValue("component", "http://www.ibm.com/xmlns/prod/sn/forums");
		role.setText("owner");
		entry.addExtension(role);			
		
		log.info(entry.toString());
		
		String aclLink = apiForum.getLinks().get("http://www.ibm.com/xmlns/prod/sn/member-list:application/atom+xml").getHref().toString();
		
	
		Entry resultEntry = (Entry)service.postEntry(aclLink, entry);
		assertTrue(APIUtils.resultSuccess(resultEntry, "Forums"));
	}
	
	

	/**
	 * add a community(whose url is  CommunityUrl) to the target community(whose url is targetURL)
	 * @param targetURL -- the target community's url.
	 * @param communityName -- the related community's name.
	 * @param communityURL -- the related community's url.
	 */
	public Entry addRelatedCommunity(String targetURL, String communityName, String communityURL, String communityDesc){
		log.info("communityUrl: "+ communityURL);
		
		Factory factory = Abdera.getNewFactory();
		Entry entry = factory.newEntry();
		entry.setTitle(communityName);
		entry.setContent(communityDesc);		

		
		entry.addCategory(StringConstants.SCHEME_TYPE,"relatedCommunity",null);
		entry.addLink(communityURL, "http://www.ibm.com/xmlns/prod/sn/related-community","text/html",null,null,100);
		
		log.info("entry: " + entry.toString());
		Entry resultEntry = (Entry)service.postEntry(targetURL, entry);
		assertTrue(APIUtils.resultSuccess(resultEntry, "Forums"));
		
		return resultEntry;
		
	}
	
	/**
	 * create a topic with an attachment
	 * @param baseTopic
	 * @param file
	 * @return the topic having an attachment
	 */
	public ForumTopic createForumTopicWithAttach(BaseForumTopic baseTopic, File file){		
		
		ForumTopic newForumTopic = createForumTopic(baseTopic);
		
		Entry newEntry = (Entry) service.createForumTopicWithAttach(baseTopic.getParentForum_API(), newForumTopic,file);
		ForumTopic result = new ForumTopic(newEntry);
		return result;
	}
	/**
	 * stop following the given forum
	 * @param forum
	 */
	public void stopFollowing(Forum forum){
		
		
		String URLConstruct=forum.getEditLink();		
		String URLConstruct2 = URLConstruct.substring(0, URLConstruct.lastIndexOf("/atom/"));
		String uuid = ForumsUtils.getForumUUID(forum);
		String URL = URLConstruct2 + "/follow/atom/resources/"+ uuid +"?source=FORUMS&type=FORUM&resource="+ uuid;
		log.info("following edit URL: "+ URL);
		boolean successFlag = service.deleteEntry(URL);
		if(successFlag){
			log.info("INFO: Stop Following Forum action complete");
		}else{
			log.info("ERROR: Stop Following Forum action not complete due to error");
		}
		assertTrue(successFlag);
	}
	/**
	 * stop following the given topic
	 * @param topic
	 */
	public void stopFollowing(ForumTopic topic){
		
		String URLConstruct=topic.getEditLink();
		
		String URLConstruct2 = URLConstruct.substring(0, URLConstruct.lastIndexOf("/atom/"));
		String uuid = ForumsUtils.getTopicUUID(topic);
		String URL = URLConstruct2 + "/follow/atom/resources/"+ uuid +"?source=FORUMS&type=FORUM_TOPIC&resource="+ uuid;
		log.info("Following edit URL: "+ URL);
		boolean successFlag = service.deleteEntry(URL);
		
		if(successFlag){
			log.info("INFO: Stop Following topic action complete");
		}else{
			log.info("ERROR: Stop Following topic action not complete due to error");
		}
		assertTrue(successFlag);
	}
	
	/**
	 * Method to flag one community forum topic as inappropriate 
	 * @param forumTopic - the forum topic created by API
	 * @return true is flagging succeeds
	 */
	public boolean flagCommunityTopic(ForumTopic forumTopic){
		boolean flag = false;
		try {
			log.info("INFO: flag community topic as inappropriate");
			service.flagCommunityTopic(forumTopic);
			flag = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * Method to flag one community reply as inappropriate
	 * @param forumReply - the forumReply created by API
	 * @return true is flagging succeeds
	 */
	public boolean flagCommunityReply(ForumReply forumReply){
		boolean flag = false;
		try {
			log.info("INFO: flag community reply as inappropriate");
			service.flagCommunityReply(forumReply);
			flag = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
}
