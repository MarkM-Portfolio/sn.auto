package com.ibm.conn.auto.lcapi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.lcapi.common.APIHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Notification;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SharePermission;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.FeedLink;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;
import com.ibm.lconn.automation.framework.services.files.FilesService;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public class APICommunitiesHandler extends APIHandler<CommunitiesService> {
	
	private static final Logger log = LoggerFactory.getLogger(APICommunitiesHandler.class);
	private String userName;

	public APICommunitiesHandler(String serverURL, String username, String password) {

		super("communities", serverURL, username, password);
		userName = username;
		
	}
	
	@Override
	protected CommunitiesService getService(AbderaClient abderaClient, ServiceEntry generalService) {
		try {
			return new CommunitiesService(abderaClient, generalService);
		} catch (LCServiceException e) {
			Assert.fail("Unable create Communities service: " + e.getMessage());
			return null;
		}
	}
	
	/**
	 * createCommunity - Using the BaseCommunity Object to create a community using API
	 * @param commObj
	 * @return Community - api community object
	 */
	public Community createCommunity(BaseCommunity commObj) {

		log.info("API: Creating Community:");
		Community newCommunity = null;

        switch (commObj.getAccess()) {
        
        	case RESTRICTED:  newCommunity = new Community(commObj.getName(), commObj.getDescription(), Permissions.PRIVATE, commObj.getTags());
        			log.info("INFO: Setting Access to Restricted");
        			log.info("INFO: Setting isExternal to " + commObj.getShareOutside());
        			newCommunity.setIsExternal(commObj.getShareOutside());
        			log.info("INFO: Setting Restricted but Listed to " + commObj.getRbl());
        			newCommunity.setListWhenPrivate(commObj.getRbl());
                 break;
        	case PUBLIC:  newCommunity = new Community(commObj.getName(), commObj.getDescription(), Permissions.PUBLIC, commObj.getTags());
        			log.info("INFO: Setting Access to Public");
        			log.info("INFO: Setting isExternal to false");
        			newCommunity.setIsExternal(false);
                 break;
        	case MODERATED:  newCommunity = new Community(commObj.getName(), commObj.getDescription(), Permissions.PUBLICINVITEONLY, commObj.getTags());
        			log.info("INFO: Setting Access to Moderated");
        			log.info("INFO: Setting isExternal to false");
        			newCommunity.setIsExternal(false);
                 break;
        	default: newCommunity = new Community(commObj.getName(), commObj.getDescription(), Permissions.PUBLIC, commObj.getTags());
                 break;
        }
        
        if (commObj.isApprovalRequired()) {
        	newCommunity.setIsPreModeration(true);
        } else {
        	// include the flag even it's false for server that has Moderation
        	// enabled but we don't want it on for this community
        	newCommunity.setIsPreModeration(false);
        }
        
        log.info("Create new community as " + userName);
		Entry communityResult = (Entry) service.createCommunity(newCommunity);
		log.info("Community Headers: " + service.getDetail());
		log.info("Check return code of createCommunity call, it should be 201");
		int responseCode = service.getRespStatus();
		if (responseCode != 201){
			log.info("Community not created successfully through API, User name: " + userName);
			Assert.fail("User: " + userName + " received response: " 
					+ responseCode + "; expected: 201; Community was not created");	
		}
		log.info("Community successfully created through API");
		log.info("Retrieve that community for full info");
		
		newCommunity = new Community((Entry)service.getCommunity(communityResult.getEditLink().getHref().toString()));//.getEditLinkResolvedHref().toString()));
		String editURL = communityResult.getEditLinkResolvedHref().toString();
		newCommunity.setUuid(editURL.split("communityUuid=")[1].split("&")[0]);
		
		log.info("INFO: Checking to see if we need to add members");	
		if(!commObj.getMembers().isEmpty()){
			log.info("INFO: Adding members");
			for (int i = 0; i < commObj.getMembers().size(); i++) {
				Role role = null;
			    User user = commObj.getMembers().get(i).getUser();
			    String userRole = commObj.getMembers().get(i).getRole().toString();
			    if(userRole.contains("Owners")){
			    	log.info("INFO: Adding user " + user.getDisplayName());
			    	log.info("INFO: User is an Owner");
			    	role = Role.OWNER;
			    }else if(userRole.contains("Members")){
			    	log.info("INFO: Adding user " + user.getDisplayName());
			    	log.info("INFO: User is an Member");
			    	role = Role.MEMBER;
			    }
			    addMemberToCommunity(user, newCommunity, role); 
			}
		}

		if (APIUtils.resultSuccess(communityResult, "Communities")) {
			return newCommunity;
		} else {
			return null;
		}
	}

	public Community createBookmark(BaseDogear bmark){
		
		Bookmark bookmark = new Bookmark(bmark.getTitle(), bmark.getDescription(), bmark.getURL(), bmark.getTags());
		
		bookmark.setIsImportant(bmark.getIsImportant());
		
		Feed communitiesFeed = (Feed) service.getMyCommunities(false, null, 0, 0, null, null, null, null, null);
		assertTrue(communitiesFeed != null);
		
		ArrayList<Community> communities = new ArrayList<Community>();
		
		for(Entry communityEntry : communitiesFeed.getEntries()) {
			communities.add(new Community(communityEntry));
		}
		
		for(Community community : communities) {
			if (community.getTitle().equalsIgnoreCase(bmark.getCommunity().getName())){
				Entry bookmarkResponse = (Entry) service.createCommunityBookmark(community, bookmark);
				assertTrue(bookmarkResponse != null);
				log.info("Bookmark: " + bookmark.getTitle() + " successfully created @ " + bookmarkResponse.getEditLinkResolvedHref().toString());
			}
		}
		return null;
	}
	
	/**
	 * Creates a bookmark in a community
	 * 
	 * @param community - The Community instance of the community in which the bookmark is to be created
	 * @param baseBookmark - The BaseDogear instance of the bookmark to be created
	 * @return - The Bookmark instance of the bookmark to be created
	 */
	public Bookmark createBookmark(Community community, BaseDogear baseBookmark) {
		
		log.info("INFO: Now creating a new community bookmark with title: " + baseBookmark.getTitle());
		
		// Create a Bookmark instance of the base bookmark
		Bookmark bookmark = new Bookmark(baseBookmark.getTitle(), baseBookmark.getDescription(), baseBookmark.getURL(), baseBookmark.getTags());
		log.info("INFO: The Entry instance of the new community bookmark has been created:" + bookmark.toEntry().toString());
		
		log.info("INFO: The URL to which the bookmark entry will be POSTed to has been determined: " + community.getBookmarkHref());
		
		// POST the new bookmark to the community bookmark URL
		Entry bookmarkResponse = (Entry) service.createCommunityBookmark(community, bookmark);
		
		if(bookmarkResponse.toString().indexOf("resp:error=\"true\">") > -1) {
			log.info("ERROR: The new community bookmark could NOT be created");
			log.info(bookmarkResponse.toString());
			return null;
		}
		
		// Retrieve the feed for all community bookmarks and get the list of bookmark entries from the feed
		Feed bookmarksFeed = (Feed) service.getCommunityBookmarks(community.getBookmarkHref());
		List<Entry> listOfBookmarkEntries = bookmarksFeed.getEntries();
		
		if(listOfBookmarkEntries.size() == 0) {
			log.info("ERROR: The feed of all bookmarks for the community returned no bookmark entries");
			log.info("ERROR: The bookmark creation response entry was returned as: " + bookmarkResponse.toString());
			log.info("ERROR: The feed of all bookmarks was returned as: " + bookmarksFeed.toString());
			return null;
		} else {
			// Retrieve the new bookmark entry from the list of entries
			Entry bookmarkEntry = null;
			boolean foundNewBookmark = false;
			int index = 0;
			
			while(index < listOfBookmarkEntries.size() && foundNewBookmark == false) {
				Entry currentEntry = listOfBookmarkEntries.get(index);
				
				if(currentEntry.getTitle().trim().equals(baseBookmark.getTitle().trim())) {
					bookmarkEntry = currentEntry;
					foundNewBookmark = true;
				}
				index ++;
			}
			
			if(foundNewBookmark == false) {
				log.info("ERROR: The new community bookmark could NOT be found in the feed of all community bookmarks");
				log.info("ERROR: The title of the new bookmark being searched for was: " + baseBookmark.getTitle());
				log.info("ERROR: The feed of all bookmarks was returned as: " + bookmarksFeed.toString());
				return null;
			}
			
			// Create the Bookmark instance of the created bookmark based on its Entry
			Bookmark newCommunityBookmark = new Bookmark(bookmarkEntry);
			
			// Ensure the bookmark links are NOT lost by re-adding them back into the Bookmark object
			HashMap<String, Link> bookmarkLinks = new HashMap<String, Link>();
			
			Link urlLink = Abdera.getNewFactory().newLink();
			urlLink.setHref(baseBookmark.getURL().trim());
			
			bookmarkLinks.put("", urlLink);
			bookmarkLinks.put(StringConstants.REL_SELF + ":" + StringConstants.MIME_NULL, bookmarkEntry.getSelfLink());
			bookmarkLinks.put(StringConstants.REL_EDIT + ":" + StringConstants.MIME_ATOM_XML, bookmarkEntry.getEditLink());
			
			newCommunityBookmark.setLinks(bookmarkLinks);
			
			return newCommunityBookmark;
		}
	}
	
	/**
	 * Updates / edits the description for a bookmark
	 * 
	 * @param bookmark - The Bookmark instance of the bookmark to be updated
	 * @param newBookmarkDescription - The String content of the new description to be set to the bookmark
	 * @return - The updated Bookmark instance if the operation is successful, the existing Bookmark instance (with no edits) otherwise
	 */
	public Bookmark editBookmarkDescription(Bookmark bookmark, String newBookmarkDescription) {
		
		log.info("INFO: Now updating the description for the community bookmark with title: " + bookmark.getTitle());
		
		// Store the old bookmark description in case of API failure and set the Bookmark instance to contain the new description
		String oldBookmarkDescription = bookmark.getContent().trim();
		bookmark.setContent(newBookmarkDescription);
		log.info("INFO: The Entry for the updated bookmark has been created: " + bookmark.toEntry().toString());
		
		// Create the URL to send the PUT request to
		String putRequestURL = bookmark.getEditLink();
		log.info("INFO: The URL to which the PUT request will be sent has been created: " + putRequestURL);
		
		Entry updateResponse = (Entry) service.putEntry(putRequestURL, bookmark.toEntry());
		
		if(updateResponse.toString().indexOf("resp:error=\"true\">") > -1) {
			log.info("ERROR: The community bookmark could NOT be updated with the new description");
			log.info(updateResponse.toString());
			bookmark.setContent(oldBookmarkDescription);
		} else {
			log.info("INFO: The bookmark description was successfully updated to: " + newBookmarkDescription);
		}
		return bookmark;
	}

	/** 
	 * This will add the feeds widget to an existing Community 
	 * and then add a feed to the community
	 * 
	 */
	public FeedLink createFeed(Community community, BaseFeed feed) {
				
		log.info("Creating Feed Link in Community: " + community.getTitle());

		FeedLink feedLink = new FeedLink(feed.getTitle(), feed.getDescription(), feed.getFeed(), feed.getTags());
		Entry feedLinkResponse = (Entry) service.createFeedLink(getCommunity(community), feedLink);
		
		log.info("Feed Link: " + feedLink.getTitle() + " successfully created @ " + feedLinkResponse.getEditLinkResolvedHref().toString());
		
		// Add the relevant links to the FeedLink instance
		feedLink.addLink(StringConstants.REL_SELF, StringConstants.MIME_NULL, feedLinkResponse.getEditLinkResolvedHref().toString().trim());
		feedLink.addLink(StringConstants.REL_EDIT, StringConstants.MIME_ATOM_XML, feedLinkResponse.getEditLinkResolvedHref().toString().trim());
		
		return feedLink;
	}
	
	/**
	 * Updates the description for a feed
	 * 
	 * @param feedToBeUpdated - The FeedLink instance of the feed to be updated
	 * @param updatedDescription - The String content of the description to which the feed description will be updated to
	 * @return - The updated FeedLink instance if all actions are successful, the original FeedLink instance if the operation fails
	 */
	public FeedLink editFeedDescription(FeedLink feedToBeUpdated, String updatedDescription) {
		
		log.info("INFO: Now updating the description for the feed with title: " + feedToBeUpdated.getTitle());
		
		// Store the old description in case of failure and set the new description
		String oldDescription = feedToBeUpdated.getContent().trim();
		feedToBeUpdated.setContent(updatedDescription);
		log.info("INFO: The updated Entry instance of the FeedLink has been created: " + feedToBeUpdated.toEntry().toString());
		
		// Retrieve the URL to PUT the update request to
		String putRequestURL = feedToBeUpdated.getEditLink();
		log.info("INFO: The URL to send the PUT request to has been retrieved: " + putRequestURL);
		
		Entry putResponse = (Entry) service.putEntry(putRequestURL, feedToBeUpdated.toEntry());
		
		if(putResponse.toString().indexOf("resp:error=\"true\">") > -1) {
			log.info("ERROR: The description for the feed could NOT be updated");
			log.info(putResponse.toString());
			feedToBeUpdated.setContent(oldDescription);
		} else {
			log.info("INFO: The description for the feed was successfully updated to: " + updatedDescription);
		}
		return feedToBeUpdated;
	}
	
	public void editStartPage(Community community, StartPageApi startPage)  {
		
		community.setStartPage(startPage.toString());
		
		// Retrieve the URL to PUT the update request to
		String putRequestURL = community.getEditLink();
		log.info("Update Start Page: User=" + userName + ", URL=" + putRequestURL);
		
		Entry putResponse = (Entry) service.putEntry(putRequestURL, community.toEntry());
		
		if(putResponse.toString().indexOf("resp:error=\"true\">") > -1) {
			log.info("ERROR: Community " + community.getTitle() + "start page cannot be updated to " + startPage);
			log.info(putResponse.toString());
		} else {
			log.info("INFO: Community " + community.getTitle() + " successfully updated to " + startPage);
		}
	}

	public Widget addWidget(Community community, BaseWidget widget){
		
		Widget widgetAPI = new Widget(widget.getTitleAPI().replaceAll("\\s+", ""));
		
		widgetAPI.setWidgetLocation(widget.getColumn());
		service.postWidget(getCommunity(community), widgetAPI.toEntry());
		assertEquals("ERROR: Unable to add "+ widget.getTitle() + " widget.",
				201, service.getRespStatus(), 0);
		return widgetAPI;
	}
	
	/**
	 * Give a Community returns a complete Community returned from the api. Note
	 * that if the object passed does not have the EditLink set then this method
	 * is going to be slow. If you have the UUID, use the other getCommunity method.
	 * @param community
	 * @return
	 */
	public Community getCommunity(Community community) {
		if(community.getEditLink() != null) {
			Community fullCommunity = new Community((Entry)service.getCommunity(community.getEditLink()));
			fullCommunity.setUuid(fullCommunity.getEditLink().split("communityUuid=")[1].split("&")[0]);
			return fullCommunity;
		}
		for(Community c: getCommunities()) {
			if(c.getTitle().equalsIgnoreCase(community.getTitle()))
				return c;
		}
		return null;
	}
	
	/**
	 * Given the UUID of a community, a full community object is created.
	 * @param communityUUID This is expected to have communityUuid= at the start
	 * before the actually UUID
	 * @return
	 */
	public Community getCommunity(String communityUUID){
		String editLinkUrl = service.getServiceURLString() + "/service/atom/community/instance?"+ communityUUID;
		Community fullCommunity = new Community((Entry)service.getCommunity(editLinkUrl));
		fullCommunity.setUuid(communityUUID);
		return fullCommunity;
	}
	
	public ArrayList<Community> getCommunities() {
		ArrayList<Community> communities = new ArrayList<Community>();
		//get communities feed
		Feed communitiesFeed = (Feed) service.getMyCommunities(true, null, 0, 500, null, null, null, null, null);
		for(Entry communityEntry : communitiesFeed.getEntries()) {
			communities.add(new Community(communityEntry));
		}
		
		return communities;
	}
	
	public BlogPost createBlogEntry(BaseBlogPost newPost, Community community) {
		
		log.info("ServiceURLString: " + service.getServiceURLString());
		
		String postBlogUrl = service.getServiceURLString().replace("communities", "blogs/" + community.getUuid() + "/api/entries");
		log.info("postBlogUrl: " + postBlogUrl);
		
		BlogPost blogEntry = new BlogPost(newPost.getTitle(), newPost.getContent(), newPost.getTags(), true, 10);
		
		Entry newEntry = (Entry) service.postBlog(postBlogUrl, blogEntry.toEntry());
		
		if(newEntry == null){
			log.info("newEntry: null");
			return null;
		}	
		else
			log.info("newEntry: " + newEntry.toString());
			return new BlogPost(newEntry);
	}
	
	/**
	 * 
	 * @param newPost - A BaseBlogPost object
	 * @param community - The community (apiOwner.getCommunity(community) can be used as an argument) in which the new blog entry with a mentions will be created.  
	 * @param browserURL - The URL of the server against which the test will be run.  The testConfig.getBrowserURL() method can be given as an argument
	 * @param beforeMentionsText - The text which will appear before the mentions.  Helper.genDateBasedRandVal() can be used to generated a unique value for verification purposes.
	 * @param user2Mention - The user who is to be mentioned
	 * @param userID - The profiles Uuid of the user who is to be mentioned.  The ProfilesAPI method can be used to retrieve this value.
	 * @param afterMentionsText - The text which will appear after the mentions.  Helper.genMonthDateBasedRandVal() can be used to generated a unique value for verification purposes.
	 * @return - A BlogPost object
	 */
	public BlogPost createCommunityBlogEntryMentions(BaseBlogPost newPost, Community community, Mentions mentions) {
		
		BlogPost blogEntry = new BlogPost(newPost.getTitle(), newPost.getContent(), newPost.getTags(), true, 10);
		
		Entry mentionsEntry = (Entry) blogEntry.toEntry();
		mentionsEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "/profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
		
		log.info("API: Post entry with mentions as content:");
		Entry postResult = (Entry)service.postBlog(service.getServiceURLString().replace("communities", "blogs")+ "/" + community.getUuid() + "/api/entries", mentionsEntry);
		
		log.info("API: Return resulting BlogPost object:");
		if(postResult == null)
			return null;
		else
			return new BlogPost(postResult);

	}
	
	public BlogPost createIdea (BaseBlogPost newPost,Community comm){
		
		Entry communityEntry = null;
		String widgetIDExtension = "";
	
		//get widgets feed for the community
		Feed widgetFeed =(Feed)service.getCommunityWidgets(comm.getUuid());
		
		//abstract the entries from feed and place into list
		List <Entry> entries =widgetFeed.getEntries();
		
		//select entry corresponding to Ideation Blog
		for(Entry feedEntry : entries){
			if(feedEntry.toString().contains("Ideation Blog")){
			 communityEntry = feedEntry;
			}
			
			
		}
		
		//abstract extensions from Ideation blog entry and place into list
		List <Element> extensions = communityEntry.getExtensions();
		
		//abstract extension relating to widgetID and place into string
		for(Element elmnt : extensions){
			if(elmnt.toString().startsWith("<snx:widgetInstanceId")){
				widgetIDExtension = elmnt.toString();
				
				
			}
			
		}
		
		
		//manipulate string to extract WidgetID
		String IDConstruct = widgetIDExtension.replace("<snx:widgetInstanceId xmlns:snx=\"http://www.ibm.com/xmlns/prod/sn\">", "");
		String ID = IDConstruct.replace("</snx:widgetInstanceId>", "");
		
		BlogPost blogEntry = new BlogPost(newPost.getTitle(), newPost.getContent(), newPost.getTags(), true, 10);
		
		Entry postResult = (Entry)service.postBlog(service.getServiceURLString().replace("communities", "blogs")+ "/" +ID + "/api/entries",blogEntry.toEntry());
		
		BlogPost result = new BlogPost(postResult);
		
		return result;

	}

	/**
	 * This method has been deprecated - please use the createIdeationBlogIdeaWithMentions() method from APICommunityBlogsHandler class instead
	 * 
	 * @param newPost - A BaseBlogPost object required to create the idea
	 * @param community - A Community object referencing the community in which the idea will be created
	 * @param mentions - A Mentions object which contains the details of the user to be mentioned and additional text
	 * @return postResult - A BlogPost object
	 */
	@Deprecated
	public BlogPost createIdeaMentions(BaseBlogPost newPost, Community community, Mentions mentions){
		
		Entry communityEntry = null;
		String widgetIDExtension = "";
	
		log.info("API: Get widgets feed for the community:");
		Feed widgetFeed = (Feed)service.getCommunityWidgets(community.getUuid());
		
		log.info("API: Abstract the entries from feed and place into list:");
		List <Entry> entries = widgetFeed.getEntries();
		
		log.info("API: Select entry corresponding to Ideation Blog:");
		for(Entry feedEntry : entries){
			if(feedEntry.toString().contains("Ideation Blog")){
			 communityEntry = feedEntry;
			}
				
		}
		
		log.info("API: Abstract extensions from Ideation blog entry and place into list:");
		List <Element> extensions = communityEntry.getExtensions();
		
		log.info("API: Abstract extension relating to widgetID and place into string:");
		for(Element elmnt : extensions){
			if(elmnt.toString().startsWith("<snx:widgetInstanceId")){
				widgetIDExtension = elmnt.toString();		
				
			}
			
		}
		
		log.info("API: Manipulate string to extract WidgetID:");
		String IDConstruct = widgetIDExtension.replace("<snx:widgetInstanceId xmlns:snx=\"http://www.ibm.com/xmlns/prod/sn\">", "");
		String ID = IDConstruct.replace("</snx:widgetInstanceId>", "");
		
		BlogPost blogEntry = new BlogPost(newPost.getTitle(), newPost.getContent(), newPost.getTags(), true, 10);
		
		Entry mentionsEntry = (Entry) blogEntry.toEntry();
		mentionsEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
		
		log.info("API: Post idea with mentions as content:");
		Entry postResult = (Entry)service.postBlog(service.getServiceURLString().replace("communities", "blogs")+ "/" +ID + "/api/entries", mentionsEntry);
		
		log.info("API: Return resulting BlogPost object:");
		if(postResult == null)
			return null;
		else
			return new BlogPost(postResult);
		
	}
	
	/**
	 * Creates a new forum topic in the default-created community forum
	 * 
	 * @param parentCommunity - The Community instance of the community in which to create the forum topic
	 * @param baseForumTopic - The BaseForumTopic instance of the forum topic to be created
	 * @return - The ForumTopic instance of the newly created forum topic if all actions are completed successfully, null otherwise
	 */
	public ForumTopic CreateForumTopic(Community parentCommunity, BaseForumTopic baseForumTopic){
		
		log.info("INFO: Now creating a new community forum topic with title: " + baseForumTopic.getTitle());
		
		// Create the Entry instance of the forum topic to be created and re-add the tags back into it (they are lost otherwise)
		Entry forumTopicEntry = createForumTopicEntryBasedOnBaseForumTopic(baseForumTopic);
		
		// Create the URL to POST the Entry to
		String postRequestURL = service.getServiceURLString() + "/service/atom/community/forum/topics?communityUuid=" + parentCommunity.getUuid();
		log.info("INFO: The URL to POST the Entry to has been created: " + postRequestURL);
		
		// Create the forum topic in the community
		return executeCreateForumTopicRequest(parentCommunity, forumTopicEntry);
	}
	
	/**
	 * Creates a new forum topic with mentions in the topic description in the default-created community forum
	 * 
	 * @param parentCommunity - The community in which the topic will be created
	 * @param baseTopic - The topic in which the mention will be made
	 * @param mentions - A Mentions object containing information about the user who is to be mentioned
	 * @return - The ForumTopic instance of the newly created forum topic if all actions are completed successfully, null otherwise
	 */
	public ForumTopic createForumTopicMentions(Community parentCommunity, BaseForumTopic baseForumTopic, Mentions mentions){
		
		log.info("INFO: Now creating a new community forum topic (with mentions) with title: " + baseForumTopic.getTitle());
		
		// Create the Entry instance of the forum topic to be created and re-add the tags back into it (they are lost otherwise)
		Entry forumTopicEntry = createForumTopicEntryBasedOnBaseForumTopic(baseForumTopic);
		
		// Set the content of the Entry to include the mentions
		forumTopicEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + 
											"/profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + 
											"</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + 
											mentions.getAfterMentionText() + "</p>");
		log.info("INFO: The Entry instance of the new community forum topic (with mentions) has been created: " + forumTopicEntry.toString());
				
		// Create the forum topic in the community
		return executeCreateForumTopicRequest(parentCommunity, forumTopicEntry);
	}
	
	/**
	 * Converts the BaseForumTopic instance of a forum topic into an Entry instance
	 * 
	 * @param baseForumTopic - The BaseForumTopic instance to be converted to an Entry
	 * @return - The Entry instance of the BaseForumTopic
	 */
	private Entry createForumTopicEntryBasedOnBaseForumTopic(BaseForumTopic baseForumTopic) {
		
		// Create a ForumTopic instance of the forum topic to be created based on its BaseForumTopic
		ForumTopic forumTopic = new ForumTopic(baseForumTopic.getTitle(), baseForumTopic.getDescription(), false, false, false, false);
		forumTopic.setTags(baseForumTopic.getTags().trim());
		
		// Create the Entry instance of the forum topic to be created and re-add the tags back into it (they are lost otherwise)
		Entry forumTopicEntry = forumTopic.toEntry();
		forumTopicEntry.addCategory(forumTopic.getTags().get(0));
		log.info("INFO: The Entry instance of the forum topic has been created: " + forumTopicEntry.toString());
		
		return forumTopicEntry;
	}
	
	/**
	 * Executes the POST request to create a new forum topic (based on the Entry provided) in the specified community
	 * 
	 * @param parentCommunity - The Community instance of the community in which the forum topic is to be created
	 * @param forumTopicEntry - The Entry instance of the forum topic to be created
	 * @return - The ForumTopic instance of the newly created forum topic if all actions are completed successfully, null otherwise
	 */
	private ForumTopic executeCreateForumTopicRequest(Community parentCommunity, Entry forumTopicEntry) {
		
		// Create the URL to POST the Entry to
		String postRequestURL = service.getServiceURLString() + "/service/atom/community/forum/topics?communityUuid=" + parentCommunity.getUuid();
		log.info("INFO: The URL to POST the Entry to has been created: " + postRequestURL);
		
		Entry postResult = (Entry) service.postEntry(postRequestURL, forumTopicEntry);
		
		if(postResult.toString().indexOf("resp:error=\"true\">") > -1) {
			log.info("ERROR: The community forum topic could NOT be created");
			log.info(postResult.toString());
			return null;
		} else {
			log.info("INFO: The new community forum topic has been created successfully");
			return new ForumTopic(postResult);
		}
	}
	
	public void followCommunity(Community c) {
		Community communityRetrieved = new Community((Entry)service.getCommunity(c.toEntry().getEditLinkResolvedHref().toString()));
		String id = communityRetrieved.getUuid();
		//Entry creation
		Abdera abdera = new Abdera();
		Entry entry = abdera.getFactory().newEntry();
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/source", "communities", null);
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-type", "community", null);
		entry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-id", id, null);
		service.followCommunity(entry);
	}
	
	public boolean uploadFile(Community community, BaseFile file, FilesService filesService) {
		
		Feed remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(community.getRemoteAppsListHref(), true, null, 0, 50, null, null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null) ;
		if(remoteAppsFeed == null)
			return false;
		String publishLink = getFilesPublishLink(remoteAppsFeed);
		TestConfigCustom cfg = TestConfigCustom.getInstance();
		String filePath = FilesUI.getFileUploadPath(file.getName(), cfg);
		File uploadFile = new File(filePath);
		FileEntry uploadFileEntry = new FileEntry(uploadFile, file.getName(), "", (file.getTags() == null) ? "" : file.getTags(), Permissions.PUBLIC, true, Notification.ON, Notification.ON, null, null, true, true, SharePermission.EDIT, null, null);
		
		// Get the service doc from the publish link, and find the actual link to publish to, and retrieve files from
		Service serviceDoc = (Service)filesService.getUrlFeed(publishLink);
		String documentPublishLink = "";
					
		for(Workspace workspace : serviceDoc.getWorkspaces()) {
			for(Collection collection : workspace.getCollections()) {
				if ((collection.getTitle().equalsIgnoreCase("Documents Feed")) &&
					(workspace.getTitle().contains("Community Library")))
					documentPublishLink = collection.getHref().toString();
			}
		}
		
		// publish to the community
		filesService.createCommunityFileNoInputStream(documentPublishLink,uploadFileEntry);
		int statusCode = filesService.getRespStatus();
		// 409 == conflict, which probably means file already exists
		if(statusCode == 201 || statusCode == 409)
			return true;
		else
			return false;
	}
	
	private String getFilesPublishLink(Feed remoteAppsFeed) {
		for (Entry entry : remoteAppsFeed.getEntries()){
			for (Category category : entry.getCategories()){
				if (category.getTerm().equalsIgnoreCase("Files")) {				

					// get the publish link
					return entry.getLink(StringConstants.REL_PUBLISH).getHref().toString();
				}
			}
		}
		return null;
	}
	
	public Member addMemberToCommunity(User newMember, Community community, Role role){
		log.info("API: " + userName + " adding member " + newMember.getEmail() + " to Community: " + community.getUuid());
		Member member = null;
		member = new Member(newMember.getEmail(), null, Component.COMMUNITIES, role , MemberType.PERSON);
			
		service.addMemberToCommunity(community, member);
		return member;
	}
	
	/**
	 * WARNING the method returns communityUuid=\<uuid\>, not just \<uuid\>
	 * 
	 * @param community
	 * @return
	 */
	public String getCommunityUUID(Community community){
		if(community.getUuid() != null) {
			return "communityUuid=" + community.getUuid();
		} else if(community.getEditLink() != null) {
			return "communityUuid=" + community.getEditLink().split("communityUuid=")[1].split("&")[0];
		}
		String getCommunityID = getCommunity(community).getId().toString();		
		String[] parts = getCommunityID.split("\\?");
		return parts[1];
	}
	
	public String getUserUUID(String serverURL, User testUser){
		String returnBlog[] = this.getService().getResponseString(serverURL + "/profiles/atom/profile.do?email=" + testUser.getEmail()).split("snx:userid");
		return returnBlog[1].replace("<", "").replace("/", "").replace(">", "");
	}
	/**
	 * return widgetID of <snx:widgetInstanceId>W1bbc7d02840e_4389_999c_3cb882695d99</snx:widgetInstanceId>
	 * @param commUUID , like c4f51499-212c-4387-badc-1a89109a4c3c
	 * @param widgetName -- ImportantBookmarks, MembersSummary, StatusUpdates, description,Forum,Bookmarks,Files,Tags
	 * @return W1bbc7d02840e_4389_999c_3cb882695d99
	 */
	public String getWidgetID(String commUUID, String widgetName) {
		Feed widgetFeed = (Feed) service.getCommunityWidget(commUUID, widgetName);
		if (widgetFeed.getEntries().size() < 1) {
			return "";
		} else {
			Entry widgetEntry = widgetFeed.getEntries().get(0);
			List<Element> extensions = widgetEntry.getExtensions();
			String widgetID = "";
			// abstract extension relating to widgetID and place into string
			for (Element elmnt : extensions) {
				if (elmnt.toString().startsWith("<snx:widgetInstanceId")) {
					widgetID = elmnt.getText();

				}

			}
			log.info("ID: " + widgetID);
			return widgetID;
		}
	}

	/**
	 * Determines if a community has a particular widget activated or not
	 * @param community The community to look on
	 * @param widget The widget to look for
	 * @return True if the widget is activated and false otherwise
	 */
	public boolean hasWidget(Community community, BaseWidget widget){
		Feed el = (Feed)service.getCommunityWidget(community.getUuid(), widget.getTitleAPI());
		log.info("INFO: Check if " + widget + " widget is activated within the community");
		// Check and see if any widget was returned
		if(el.getEntries().size() != 1)
			return false;
		// Else return true because only the queried widget will be returned
		return true;
	}
	
	/**
	 * Deletes a given community. This method will never throw on exception.
	 * @param community
	 * @return True if the community was successfully deleted.
	 */
	public boolean deleteCommunity(Community community){
		try{
			return service.deleteCommunity(community.getEditLink());
		} catch (Exception e){
			log.warn(e.getMessage());
			return false;
		}
	}
	
	/**
	 * Posts a status update message to a community
	 * 
	 * @param community - The community in which the status update is to be posted
	 * @param statusMessage - The status message to be posted
	 * @return statusUpdateId - The ID of the newly created status message
	 */
	public String addStatusUpdate(Community community, String statusMessage) {
		
		log.info("INFO: Adding a status message to the community");
		
		// Create the community ID
		String communityId = "urn:lsid:lconn.ibm.com:communities.community:" + community.getUuid();
		
		// Create the URL at which the status update message will be posted
		String updateURL = service.getServiceURLString().replace("communities", "connections/opensocial/rest/ublog/");
		updateURL += communityId + "/@all";
		
		// Set the JSON content string
		String jsonContent = "{\"content\":\"" + statusMessage + "\"}";
		
		log.info("INFO: Posting the status message to the following URL: " + updateURL);
		String response = service.postResponseJSONString(updateURL, jsonContent);
		
		// Extract and return the status update message ID
		String statusUpdateId = response.substring(response.indexOf(":communities.note:") + 18, 
													response.indexOf(":communities.note:") + 54);
		return statusUpdateId;
	}
	
	/**
	 * Posts a comment to a status message within a community
	 * 
	 * @param statusUpdateId - The ID of the status message post to be commented on (retrieved using addStatusUpdate(Community, String) method)
	 * @param comment - The comment to be posted on the status update
	 * @return commentId - The ID of the newly created comment
	 */
	public String commentOnStatusUpdate(String statusUpdateId, String comment) {
		
		log.info("INFO: Adding a comment to the community status message");
		
		// Create the status update message ID
		String statusId = "urn:lsid:lconn.ibm.com:communities.note:" + statusUpdateId;
		
		// Create the URL at which the comment will be posted
		String commentURL = service.getServiceURLString().replace("communities", "connections/opensocial/rest/ublog/@all/@all/");
		commentURL += statusId + "/comments";
		
		// Set the JSON content string
		String jsonContent = "{\"content\":\"" + comment + "\"}";
		
		log.info("INFO: Posting the comment to the following URL: " + commentURL);
		String response = service.postResponseJSONString(commentURL, jsonContent);
		
		// Extract and return the comment ID
		String commentId = response.substring(response.indexOf(":communities.comment:") + 21,
												response.indexOf(":communities.comment:") + 57);
		return commentId;
	}
	
	/**
	 * Causes the user to like a comment that has been posted on a community status update
	 * 
	 * @param user - The user who is to "like" the comment
	 * @param commentPostId - The ID of the comment (retrieved using the commentOnStatusUpdate(String, String) method)
	 */
	public void likeStatusComment(APIProfilesHandler user, String commentPostId) {
		
		log.info("INFO: " + user.getDesplayName() + " will now like the comment");
		
		// Create the required ID's for the URL
		String personId = "urn:lsid:lconn.ibm.com:profiles.person:" + user.getUUID();
		String commentId = "urn:lsid:lconn.ibm.com:communities.comment:" + commentPostId;
		
		// Create the URL which will like the comment for the user
		String likeURL = service.getServiceURLString().replace("communities", "connections/opensocial/rest/ublog/@all/@all/");
		likeURL += commentId + "/likes/" + personId;
		
		log.info("INFO: " + user.getDesplayName() + " is liking the comment via the URL: " + likeURL);
		service.postResponseString(likeURL, "");
	}
	
	/**
	 * Deletes a comment posted to a community status update
	 * 
	 * @param statusUpdateId - The ID of the status update in which the comment to be deleted was posted
	 * @param commentPostId - The ID of the comment to be deleted
	 * @return - Returns true if the delete operation is successful, false otherwise
	 */
	public boolean deleteStatusComment(String statusUpdateId, String commentPostId) {
		
		log.info("INFO: Deleting the commented posted on the status update");
		
		// Create the required ID's for the URL
		String statusId = "urn:lsid:lconn.ibm.com:communities.note:" + statusUpdateId;
		String commentId = "urn:lsid:lconn.ibm.com:communities.comment:" + commentPostId;
		
		// Create the URL which will perform the deletion of the comment
		String deleteURL = service.getServiceURLString().replace("communities", "connections/opensocial/rest/ublog/@me/@all/");
		deleteURL += statusId + "/comments/" + commentId;
		
		log.info("INFO: Deleting the comment posted on the status update with the URL: " + deleteURL);
		ClientResponse response = service.deleteWithResponse(deleteURL);
		
		if(response.getStatus() == 200) {
			log.info("INFO: Comment successfully deleted");
			return true;
		} else {
			log.info("INFO: Comment could not be removed from the status update");
			log.info("INFO: Error Response - " + response.getStatus() + " " + response.getStatusText());
			return false;
		}
	}
	
	/**
	 * Invites the specified user to join the specified community
	 * 
	 * @param community - The community in which the user is invited to join
	 * @param userToInvite - The user to be invited to join the community
	 * @return - Returns the Invitation instance of the invitation if successful, null otherwise
	 */
	public Invitation inviteUserToJoinCommunity(Community community, APIProfilesHandler userToInvite) {
		
		log.info("INFO: Inviting " + userToInvite.getDesplayName() + " to join the community named " + community.getTitle());
		Invitation userInvitation = new Invitation(userToInvite.getEmail(), userToInvite.getUUID(), 
													"Invitation to join " + community.getTitle(),
													"Please consider joining this excellent community.");
		
		Entry invitationEntry = (Entry) service.createInvitation(community, userInvitation);
		
		if(invitationEntry.toString().indexOf("resp:error=\"true\">") > -1) {
			log.info("ERROR: Error with sending the invitation: " + invitationEntry.toString());
			return null;
			
		} else {
			log.info("INFO: Invitation successfully sent to " + userToInvite.getDesplayName());
			Invitation invitationEvent = new Invitation(invitationEntry);
			invitationEvent.addLink(StringConstants.REL_EDIT, StringConstants.MIME_ATOM_XML, invitationEntry.getEditLink().getHref().toString());
			
			return invitationEvent;
		}
	}
	
	/**
	 * Revokes an existing community invitation
	 * 
	 * @param invitationToRevoke - The invitation which is to be revoked
	 * @return - True if the revoke operation was successful, false otherwise
	 */
	public boolean revokeCommunityInvitation(Invitation invitationToRevoke) {
		
		log.info("INFO: Revoking the invitation with ID - " + invitationToRevoke.getId().toString());
		return service.deleteInvitiation(invitationToRevoke.getEditLink());
	}
	
	/**
	 * Sends a request to join a community
	 * 
	 * @param community - The community that the user wishes to join
	 * @param requestMessage - The message that is to be attached to the request
	 * @return - Returns true if the request is successful, false otherwise
	 */
	public boolean requestToJoinCommunity(Community community, String requestMessage) {
		
		log.info("INFO: Sending a request to join the community named " + community.getTitle());
		
		// Create the entry to be used as the request
		Entry communityRequestEntry = Abdera.getInstance().newEntry();
		communityRequestEntry.setTitle("Request to join " + community.getTitle());
		communityRequestEntry.addCategory(StringConstants.SCHEME_COMPONENT, "communities", "Communities");
		communityRequestEntry.setContentAsXhtml(requestMessage);
		
		Entry requestResult = (Entry) service.requestToJoinEntry(community, communityRequestEntry);
		
		if(requestResult.toString().indexOf("resp:error=\"true\">") > -1) {
			log.info("ERROR: Error with sending the request to join community");
			log.info(requestResult.toString());
			return false;
			
		} else {
			log.info("INFO: Request to join the community sent successfully");
			return true;
		}
	}
	
	/**
	 * Removes a current member from the specified community
	 * 
	 * @param community - The Community instance of the community from which the member is to be removed
	 * @param userToBeRemoved - The APIProfilesHandler instance of the member to be removed
	 * @return - True if the member is removed successfully, false otherwise
	 */
	public boolean removeMemberFromCommunity(Community community, APIProfilesHandler userToBeRemoved) {
		
		log.info("INFO: Now removing " + userToBeRemoved.getDesplayName() + " as a member from the community with title: " + community.getTitle());
		
		String deleteMemberURL = service.getServiceURLString() + "/service/atom/forms/community/members"
									+ "?communityUuid=" + community.getUuid() + "&userid=" + userToBeRemoved.getUUID();
		log.info("INFO: The member deletion URL has been created: " + deleteMemberURL);
		
		return service.deleteFeedLink(deleteMemberURL);
	}
	
	/**
	 * Deletes / removes the specified widget from the specified community
	 * 
	 * @param community - The Community instance of the community which contains the widget to be deleted / removed
	 * @param baseWidgetToBeDeleted - The BaseWidget instance of the widget to be deleted / removed
	 * @return - True if the deletion / removal of the widget is successful, false otherwise
	 */
	public boolean deleteWidget(Community community, BaseWidget baseWidgetToBeDeleted) {
		
		log.info("INFO: Now removing the community widget with title: " + baseWidgetToBeDeleted.getTitle());
		
		// Retrieve the Entry instance of the wiki to be deleted
		Entry widgetEntry = retrieveWidgetEntry(community, baseWidgetToBeDeleted);
		
		if(widgetEntry == null) {
			log.info("ERROR: The widget with title: " + baseWidgetToBeDeleted.getTitle() + " could NOT be removed from the community with title: " + community.getTitle());
			return false;
		}
		
		// Retrieve the self link from the entry - this is used to delete the widget
		String widgetSelfLink = widgetEntry.getSelfLink().getHref().toString();
		log.info("INFO: The widget self link has been retrieved: " + widgetSelfLink);
		
		// Delete the widget from the community
		int deleteResponse = service.deleteWithResponse(widgetSelfLink).getStatus();
		
		if(deleteResponse >= 200 && deleteResponse <= 204) {
			log.info("INFO: The widget with title: " + baseWidgetToBeDeleted.getTitle() + " has been successfully removed from the community with title: " + community.getTitle());
			return true;
		} else {
			log.info("ERROR: The widget with title: " + baseWidgetToBeDeleted.getTitle() + " could NOT be removed from the community with title: " + community.getTitle());
			return false;
		}
	}
	
	/**
	 * Retrieves the entry corresponding to the specified widget for the community supplied
	 * Private access for now since there is no requirement for this method to be called externally
	 * 
	 * @param community - The Community instance of the community from which the specified widget entry is to be retrieved
	 * @param baseWidget - The BaseWidget instance of the widget to be retrieved
	 * @return - The Entry instance of the widget if the operation is successful, null otherwise
	 */
	private Entry retrieveWidgetEntry(Community community, BaseWidget baseWidget) {
		
		log.info("INFO: Now retrieving the widget (as an entry) for the widget with title: " + baseWidget.getTitle() + " from the community with title: " + community.getTitle());
		
		// Retrieve the widgets feed for this community
		Feed widgetsFeed = (Feed) service.getCommunityWidgets(community.getUuid());
		
		// Retrieve the list of entries from the feed
		List<Entry> listOfFeedEntries = widgetsFeed.getEntries();
		
		// Retrieve the entry corresponding to the specified widget from the widgets feed
		Entry widgetEntry = null;
		boolean foundWidgetEntry = false;
		int index = 0;
		while(index < listOfFeedEntries.size() && foundWidgetEntry == false) {
			Entry currentEntry = listOfFeedEntries.get(index);
			
			if(currentEntry.toString().indexOf(baseWidget.getTitle().trim()) > -1) {
				log.info("INFO: The widget entry has been retrieved successfully: " + currentEntry.toString().replaceAll("\n", "").trim());
				widgetEntry = currentEntry;
				foundWidgetEntry = true;
			}
			index ++;
		}
		
		if(foundWidgetEntry == false) {
			log.info("ERROR: Could not locate the widget with title: " + baseWidget.getTitle() + " in the community widgets feed");
			log.info(widgetsFeed.toString());
			return null;
		}
		return widgetEntry;
	}
}
