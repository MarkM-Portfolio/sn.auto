package com.ibm.conn.auto.lcapi;

import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/**
 * @author 	Anthony Cox
 * Date:	23rd March 2016
 */

public class APICommunityBlogsHandler extends APICommunitiesHandler {

	private static final Logger log = LoggerFactory.getLogger(APICommunityBlogsHandler.class);
	
	public APICommunityBlogsHandler(String serverURL, String username, String password) {
		super(serverURL, username, password);
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
	 * Creates a new community blog entry
	 * 
	 * @param community - The Community instance in which the blog entry is to be created
	 * @param baseBlogPost - The BaseBlogPost instance of the blog entry to be created 
	 * @return - The BlogPost instance of the newly created blog entry if the operation is successful, null otherwise
	 */
	public BlogPost createBlogEntry(Community community, BaseBlogPost baseBlogPost) {
		
		log.info("INFO: Now adding a blog entry to the community blog with title: " + community.getTitle());
		
		// Create the URL to which the blog entry will be POSTed
		String postBlogURL = service.getServiceURLString().replace("communities", "blogs/" + community.getUuid() + "/api/entries");
		log.info("INFO: The URL to which the new blog entry will be POSTed has been created: " + postBlogURL);
		
		BlogPost blogPost = new BlogPost(baseBlogPost.getTitle(), baseBlogPost.getContent(), baseBlogPost.getTags(), true, 10);
		
		Entry blogEntry = (Entry) service.postBlog(postBlogURL, blogPost.toEntry());
		
		if(blogEntry.toString().indexOf("resp:error=\"true\">") == -1) {
			log.info("INFO: The community blog entry was created successfully");
			return new BlogPost(blogEntry);
		} else {
			log.info("ERROR: There was a problem with creating the new blog entry in the community blog");
			log.info(blogEntry.toString());
			return null;
		}
	}
	
	/**
	 * Edits / updates the description for a community blog entry / idea
	 * 
	 * @param blogPost - The BlogPost instance of the community blog entry / idea to be updated
	 * @param newDescription - The new description to which the description will be updated
	 * @return - The updated BlogPost instance if the operation is successful, the existing instance is returned otherwise
	 */
	public BlogPost editDescription(BlogPost blogPost, String newDescription) {
		
		log.info("INFO: Now updating the '" + blogPost.getTitle() + "' blog entry / idea description to be: " + newDescription);
		
		// Retrieve the old description (in case of operation failure) and set the new description
		String oldDescription = blogPost.getContent().trim();
		blogPost.setContent(newDescription);
		
		// Create the URL to which the new description will be PUT
		String putRequestURL = blogPost.getEditLink();
		log.info("INFO: The URL to which the PUT request will be sent has been created: " + putRequestURL);
		
		Entry updateEntry = (Entry) service.putEntry(putRequestURL, blogPost.toEntry());
		
		if(updateEntry.toString().indexOf("resp:error=\"true\">") == -1) {
			log.info("INFO: The community blog entry / idea description was successfully updated");
		} else {
			log.info("ERROR: The blog entry / idea description could not be updated");
			log.info(updateEntry.toString());
			blogPost.setContent(oldDescription);
		}
		return blogPost;
	}
	
	/**
	 * Posts a comment to a blog entry / idea
	 * 
	 * @param blogPost - The BlogPost instance of the blog entry / idea to which the comment will be posted
	 * @param blogComment - The BlogComment instance of the comment to be posted to the blog entry / idea
	 * @return - The new BlogComment instance of the comment if the operation is successful, null otherwise
	 */
	public BlogComment createComment(BlogPost blogPost, BlogComment blogComment) {
		
		log.info("INFO: Now adding a comment to the blog entry / idea with title: " + blogPost.getTitle());
		
		// Create the URL to which the comment will be POSTed
		String postRequestURL = blogPost.getCommentsHref();
		log.info("INFO: The URL to which the POST request will be sent has been created: " + postRequestURL);
		
		Entry commentEntry = (Entry) service.postEntry(postRequestURL, blogComment.toEntry());
		
		if(commentEntry.toString().indexOf("resp:error=\"true\">") == -1) {
			log.info("INFO: The blog entry / idea comment has been posted successfully");
			return new BlogComment(commentEntry);
		} else {
			log.info("ERROR: The blog entry / idea comment could not be posted");
			log.info(commentEntry.toString());
			return null;
		}
	}
	
	/**
	 * Likes / recommends a blog entry / votes for an idea in an ideation blog
	 * 
	 * @param blogPost - The BlogPost instance of the blog entry to be liked / idea to be voted for
	 * @return - The existing BlogPost instance of the blog entry if the operation is successful, null otherwise
	 */
	public BlogPost likeOrVote(BlogPost blogPost) {
		
		log.info("INFO: Now liking the blog entry / voting for the idea with title: " + blogPost.getTitle());
		
		// Create the entry to be used to recommend the blog entry  / vote for the idea
		Entry recommendBlogEntry = Abdera.getNewFactory().newEntry();
		recommendBlogEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "recommendation", null);
		recommendBlogEntry.setTitle("like");
		log.info("INFO: The recommend blog entry instance has been created: " + recommendBlogEntry.toString());
		
		// Create the URL to which the recommend event will be POSTed
		String postRequestURL = blogPost.getEditLink().replace("/api/", "/api/recommend/");
		log.info("INFO: The URL to which the recommend request will be POSTed has been created: " + postRequestURL);
		
		Entry likeResponse = (Entry) service.postEntry(postRequestURL, recommendBlogEntry);
		
		if(likeResponse.toString().indexOf("resp:error=\"true\">") == -1) {
			log.info("INFO: The blog entry was successfully liked / idea was successfully voted for");
			return blogPost;
		} else {
			log.info("ERROR: The blog entry could not be liked / idea could not be voted for");
			log.info(likeResponse.toString());
			return null;
		}
	}
	
	/**
	 * Unlikes an entry / removes a vote on an idea
	 * 
	 * @param blogPostToUnlike - The BlogPost instance of the blog entry / idea to be unliked
	 * @return - True if the unlike operation was successful, false otherwise
	 */
	public boolean unlikeOrRemoveVote(BlogPost blogPostToUnlike) {
		
		log.info("INFO: The blog entry / idea with title '" + blogPostToUnlike.getTitle() + "' will now be unliked");
		
		// Create the URL to be used to unlike the blog entry / idea
		String unlikeURL = blogPostToUnlike.getEditLink().replace("/api/", "/api/recommend/");
		log.info("INFO: The URL to send the DELETE request to has been created: " + unlikeURL);
		
		return service.deleteFeedLink(unlikeURL);
	}
	
	/**
	 * Likes / recommends an entry / idea comment
	 * 
	 * @param blogComment - The BlogComment instance of the blog / idea comment to be liked
	 * @return - The existing BlogComment instance of the comment if the operation is successful, null otherwise
	 */
	public BlogComment likeComment(BlogComment blogComment) {
		
		log.info("INFO: Now recommending / liking the entry / idea comment with content: " + blogComment.getContent());
		
		// Create the entry to be used to recommend the blog entry / idea comment
		Entry recommendCommentEntry = Abdera.getNewFactory().newEntry();
		recommendCommentEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "recommendation", null);
		recommendCommentEntry.setTitle("like");
		log.info("INFO: The recommend comment entry instance has been created: " + recommendCommentEntry.toString());
		
		// Create the URL to which the recommend event will be POSTed
		String postRequestURL = blogComment.getEditLink().replace("/api/", "/api/recommend/comments/");
		log.info("INFO: The URL to which the recommend request will be POSTed has been created: " + postRequestURL);
		
		Entry likeResponse = (Entry) service.postEntry(postRequestURL, recommendCommentEntry);
		
		if(likeResponse.toString().indexOf("resp:error=\"true\">") == -1) {
			log.info("INFO: The entry / idea comment was successfully liked / recommended");
			return blogComment;
		} else {
			log.info("ERROR: The entry / idea comment could not be liked / recommended");
			log.info(likeResponse.toString());
			return null;
		}
	}
	
	/**
	 * Creates an idea in a community ideation blog
	 * 
	 * @param community - The Community in which the ideation blog is location in which the idea is to be created
	 * @param baseBlogPost - The BaseBlogPost instance of the idea to be created
	 * @return - The BlogPost instance of the newly created ideation blog idea if the operation is successful, null otherwise
	 */
	public BlogPost createIdeationBlogIdea(Community community, BaseBlogPost baseBlogPost) {
		
		log.info("INFO: Now creating an ideation blog idea with title: " + baseBlogPost.getTitle());
		
		// Retrieve the ideation blogs widget as an entry
		Entry ideationWidgetEntry = retrieveIdeationBlogsWidgetEntry(community);
		if(ideationWidgetEntry == null) {
			return null;
		}
		
		// Retrieve the String value of the ID for the ideation blogs widget
		String widgetId = retrieveWidgetIdFromEntry(ideationWidgetEntry);
		if(widgetId == null) {
			return null;
		}
		
		// Create the BlogPost instance of the ideation blog idea to be posted
		BlogPost idea = new BlogPost(baseBlogPost.getTitle(), baseBlogPost.getContent(), baseBlogPost.getTags(), true, 10);
		log.info("INFO: The entry to represent the ideation blog idea has been created: " + idea.toEntry().toString());
		
		// Create the URL to POST the create idea request to
		String postRequestURL = service.getServiceURLString().replace("communities", "blogs") + "/" + widgetId + "/api/entries";
		log.info("INFO: The URL to which the POST request will be made has been created: " + postRequestURL);
		
		Entry createIdeaResponse = (Entry) service.postEntry(postRequestURL, idea.toEntry());
		
		if(createIdeaResponse.toString().indexOf("resp:error=\"true\">") == -1) {
			log.info("INFO: The ideation blog idea was created successfully");
			return new BlogPost(createIdeaResponse);
		} else {
			log.info("ERROR: The ideation blog idea could not be created");
			log.info(createIdeaResponse.toString());
			return null;
		}
	}
	
	/**
	 * Edits / updates a comment posted to a blog entry / idea
	 * 
	 * @param ideaComment - The BlogComment instance of the comment to be updated
	 * @param editedComment - The BaseBlogComment instance of the updated comment to be posted
	 * @return - The updated BlogComment instance if the operation is successful, the existing BlogComment instance is returned otherwise
	 */
	public BlogComment editComment(BlogComment blogComment, BaseBlogComment editedComment) {
		
		log.info("INFO: Now updating the blog entry / idea comment with content: " + blogComment.getContent());
		
		// Store the old comment content in case of update failure
		String oldCommentContent = blogComment.getContent();
		
		// Create the URL to send the PUT request to in order to update the comment
		String putRequestURL = blogComment.getEditLink().replace("api", "api/comments");
		log.info("INFO: The URL to which the PUT request will be sent to has been created: " + putRequestURL);
		
		// Set the new comment content
		blogComment.setContentType("text");
		blogComment.setContent(editedComment.getContent());
		log.info("INFO: The entry which will be PUT to the server URL has been created: " + blogComment.toEntry().toString());
		
		Entry updateResponse = (Entry) service.putEntry(putRequestURL, blogComment.toEntry());
		
		if(updateResponse.toString().indexOf("resp:error=\"true\">") == -1) {
			log.info("INFO: The blog entry / idea comment has been updated successfully");
		} else {
			log.info("ERROR: The blog entry / idea comment could not be updated");
			log.info(updateResponse.toString());
			blogComment.setContent(oldCommentContent);
		}
		return blogComment;
	}
	
	/**
	 * Creates an ideation blog idea with mentions in the idea content / description
	 * 
	 * @param baseBlogPost - A BaseBlogPost object required to create the idea
	 * @param community - A Community object referencing the community in which the idea will be created
	 * @param mentions - A Mentions object which contains the details of the user to be mentioned and additional text
	 * @return - The BlogPost instance of the newly created ideation blog idea if the operation is successful, null otherwise
	 */
	public BlogPost createIdeationBlogIdeaWithMentions(BaseBlogPost baseBlogPost, Community community, Mentions mentions){
		
		log.info("INFO: Now creating an ideation blog idea with mentions to " + mentions.getUserToMention().getDisplayName() + " and with title: " + baseBlogPost.getTitle());
		
		// Retrieve the ideation blogs widget as an entry
		Entry ideationWidgetEntry = retrieveIdeationBlogsWidgetEntry(community);
		if(ideationWidgetEntry == null) {
			return null;
		}
		
		// Retrieve the String value of the ID for the ideation blogs widget
		String widgetId = retrieveWidgetIdFromEntry(ideationWidgetEntry);
		if(widgetId == null) {
			return null;
		}
		
		// Create the BlogPost instance of the ideation blog idea to be posted
		BlogPost idea = new BlogPost(baseBlogPost.getTitle(), baseBlogPost.getContent(), baseBlogPost.getTags(), true, 10);
		log.info("INFO: The entry to represent the ideation blog idea has been created: " + idea.toEntry().toString());
		
		// Create the Mentions content as HTML
		String mentionsContent = "<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'>"
									+ "<a class='fn url' href='" + mentions.getBrowserURL() + "profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>"
									+ "@" + mentions.getUserToMention().getDisplayName() + "</a>" + "<span class='x-lconn-userid' style='display : none'>"
									+ mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>";
		log.info("INFO: The Mentions content has been created: " + mentionsContent);
		
		// Create the Entry representation of the idea with mentions
		Entry ideaWithMentionsEntry = idea.toEntry();
		ideaWithMentionsEntry.setContentAsHtml(mentionsContent);
		log.info("INFO: The idea with mentions entry has been created: " + ideaWithMentionsEntry.toString());
		
		// Create the URL to which the POST request will be sent
		String postRequestURL = service.getServiceURLString().replace("communities", "blogs")+ "/" + widgetId + "/api/entries";
		log.info("INFO: The URL to which the POST request will be made has been created: " + postRequestURL);
		
		Entry createIdeaResponse = (Entry) service.postBlog(postRequestURL, ideaWithMentionsEntry);
		
		if(createIdeaResponse.toString().indexOf("resp:error=\"true\">") == -1) {
			log.info("INFO: The ideation blog idea with mentions was created successfully");
			return new BlogPost(createIdeaResponse);
		} else {
			log.info("ERROR: The ideation blog idea with mentions could not be created");
			log.info(createIdeaResponse.toString());
			return null;
		}
	}
	
	/**
	 * Creates a comment with mentions to the specified user on a blog post / idea
	 * 
	 * @param blogPost - The BlogPost instance of the blog post / idea to which the comment with mentions is to be posted
	 * @param mentions - The Mentions instance of the user to be mentioned
	 * @return - The BlogComment instance of the posted comment if the operation is successful, null otherwise
	 */
	public BlogComment createCommentWithMentions(BlogPost blogPost, Mentions mentions) {
		
		log.info("INFO: Now posting a comment with mentions to " + mentions.getUserToMention().getDisplayName());
		
		// Create the entry representation of the comment to be posted
		Entry commentEntry = new BlogComment("content", blogPost.toEntry()).toEntry();
		log.info("INFO: The comment entry (before content has been set) has been created: " + commentEntry.toString());
		
		// Create the mentions content for the comment entry
		String mentionsContent = "<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'>"
									+ "<a class='fn url' href='" + mentions.getBrowserURL() + "profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>"
									+ "@" + mentions.getUserToMention().getDisplayName() + "</a>"
									+ "<span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> "
									+ mentions.getAfterMentionText() + "</p>";
		log.info("INFO: The mentions content for the comment has been created: " + mentionsContent);
		
		// Set the content to the comment entry and convert it to a BlogComment instance
		commentEntry.setContentAsHtml(mentionsContent);
		log.info("INFO: The comment entry (after content has been set) has been created: " + commentEntry.toString());
		
		// Create the URL to which the POST request will be made
		String postRequestURL = blogPost.getCommentsHref();
		log.info("INFO: The URL to which the POST request will be made has been created: " + postRequestURL);
		
		Entry postResponse = (Entry) service.postEntry(postRequestURL, commentEntry);
		
		if(postResponse.toString().indexOf("resp:error=\"true\">") == -1) {
			log.info("INFO: The comment with mentions to '" + mentions.getUserToMention().getDisplayName() + "' was created successfully");
			return new BlogComment(postResponse);
		} else {
			log.info("ERROR: The comment with mentions to '" + mentions.getUserToMention().getDisplayName() + "' could not be created");
			log.info(postResponse.toString());
			return null;
		}
	}
	
	/**
	 * Deletes a comment posted to a blog entry / idea
	 * 
	 * @param blogComment - The BlogComment instance of the blog comment to be deleted
	 * @return - True if the comment is deleted successfully, false otherwise
	 */
	public boolean deleteComment(BlogComment blogComment){
		
		log.info("INFO: Now deleting the comment posted to the blog entry / idea with content: " + blogComment.getContent().trim());
		int deleteResponse = service.deleteWithResponse(blogComment.getEditLink().replace("/api/", "/api/comments/")).getStatus();
		
		if(deleteResponse >= 200 && deleteResponse <= 204) {
			log.info("INFO: The blog comment was successfully deleted");
			return true;
		} else {
			log.info("ERROR: Blog comment deletion returned a status code of " + deleteResponse);
			return false;
		}
	}
	
	/**
	 * Notifies the specified user about the specified blog entry
	 * 
	 * PLEASE NOTE: This API method works On Premise ONLY - it does NOT work on Smart Cloud at present
	 * 
	 * @param blogEntry - The BlogPost instance of the blog entry which the specified user will be notified about
	 * @param apiUserToNotify - The APIProfilesHandler instance of the user to be notified about the blog entry
	 * @return - True if the notification is sent successfully, false otherwise
	 */
	public boolean notifyUserAboutBlogEntry(BlogPost blogEntry, APIProfilesHandler apiUserToNotify) {
		
		log.info("INFO: " + apiUserToNotify.getDesplayName() + " will now be notified about the blog entry with title: " + blogEntry.getTitle());
		
		log.info("INFO: Now retrieving the ID for the blog entry");
		String blogEntryId = blogEntry.getId().toString();
		blogEntryId = blogEntryId.substring(blogEntryId.indexOf('-') + 1);
		log.info("INFO: The ID for the blog entry has been retrieved: " + blogEntryId);
		
		log.info("INFO: Now creating the URL required to retrieve the nonce for notifying " + apiUserToNotify.getDesplayName());
		String notifyURL = service.getServiceURLString().replace("/communities", "/blogs") + "/roller-services/json/notify";
		notifyURL += "?description=" + Data.getData().BlogEntryNotificationMessage.replace(" ", "%20");
		notifyURL += "&entry=" + blogEntryId;
		notifyURL += "&notificationForm_filteringcheckboxnotifyPerson=" + apiUserToNotify.getUUID();
		notifyURL += "&receivers=" + "%3C" + apiUserToNotify.getUUID() + "%3E";
		notifyURL += "&version=250";
		notifyURL += "&lang=en_us";
		log.info("INFO: The URL required to retrieve the nonce value has been created: " + notifyURL);
		
		log.info("INFO: Now POSTing the URL to the server to retrieve the nonce value");
		String nonceValue = service.postResponseJSONString(notifyURL, "{}");
		nonceValue = nonceValue.substring(nonceValue.indexOf("'nonce'") + 9, nonceValue.indexOf("'nonce'") + 45);
		log.info("INFO: The nonce value has been retrieved: " + nonceValue);
		
		log.info("INFO: Now adding the nonce value to the URL in order to send the notification to " + apiUserToNotify.getDesplayName());
		notifyURL += "&dangerousurlnonce=" + nonceValue;
		log.info("INFO: The URL to which the notification request will be sent has been created: " + notifyURL);
		
		Entry postRequestResponse = (Entry) service.postEntry(notifyURL, Abdera.getNewFactory().newEntry());
		
		if(service.getRespStatus() >= 200 && service.getRespStatus() <= 204) {
			log.info("INFO: " + apiUserToNotify.getDesplayName() + " has been successfully notified about the blog entry with title: " + blogEntry.getTitle());
			return true;
		} else {
			log.info("ERROR: " + apiUserToNotify.getDesplayName() + " could NOT be notified about the blog entry");
			log.info("ERROR: " + postRequestResponse.toString());
			return false;
		}
	}
	
	/**
	 * Retrieves the entry corresponding to the ideation blogs widget for the community supplied
	 * Private access for now since there is no requirement for this method to be called externally
	 * 
	 * @param community - The Community instance of the community from which the ideation blog widget entry is to be retrieved
	 * @return - The Entry instance of the ideation blog widget if the operation is successful, null otherwise
	 */
	private Entry retrieveIdeationBlogsWidgetEntry(Community community) {
		
		log.info("INFO: Now retrieving the ideation blogs widget (as an entry) for the community with title: " + community.getTitle());
		
		// Retrieve the widgets feed for this community
		Feed widgetsFeed = (Feed) service.getCommunityWidgets(community.getUuid());
		
		// Retrieve the list of entries from the feed
		List<Entry> listOfFeedEntries = widgetsFeed.getEntries();
		
		// Retrieve the entry corresponding to the ideation blog widget from the widgets feed
		Entry ideationWidgetEntry = null;
		boolean foundIdeationWidgetEntry = false;
		int index = 0;
		while(index < listOfFeedEntries.size() && foundIdeationWidgetEntry == false) {
			Entry currentEntry = listOfFeedEntries.get(index);
			
			if(currentEntry.toString().indexOf("Ideation Blog") > -1) {
				log.info("INFO: The ideation blog widget entry has been retrieved successfully: " + currentEntry.toString().replaceAll("\n", "").trim());
				ideationWidgetEntry = currentEntry;
				foundIdeationWidgetEntry = true;
			}
			index ++;
		}
		
		if(foundIdeationWidgetEntry == false) {
			log.info("ERROR: Could not locate the Ideation Blogs widget in the community widgets feed");
			log.info(widgetsFeed.toString());
			return null;
		}
		return ideationWidgetEntry;
	}
	
	/**
	 * Retrieves the widget ID from the widget entry
	 * Private access for now since there is no requirement for this method to be called externally
	 * 
	 * @param ideationBlogsWidgetEntry - The Entry instance of the widget from which the ID is to be retrieved
	 * @return - The ID of the widget if the operation is successful, null otherwise
	 */
	private String retrieveWidgetIdFromEntry(Entry widgetEntry) {
		
		log.info("INFO: Now retrieving the widget ID from the widget entry");
		
		// Retrieve the list of elements (extensions) from the Ideation Blog entry
		List<Element> listOfElements = widgetEntry.getExtensions();
		
		// Retrieve the widget ID String from the list of elements
		String widgetId = null;
		boolean foundWidgetId = false;
		int index = 0;
		while(index < listOfElements.size() && foundWidgetId == false) {
			Element currentElement = listOfElements.get(index);
			
			if(currentElement.toString().startsWith("<snx:widgetInstanceId")) {
				widgetId = currentElement.toString();
				widgetId = widgetId.replace("<snx:widgetInstanceId xmlns:snx=\"http://www.ibm.com/xmlns/prod/sn\">", "");
				widgetId = widgetId.replace("</snx:widgetInstanceId>", "");
				widgetId = widgetId.trim();
				
				log.info("INFO: The widget ID was successfully retrieved from the widget entry: " + widgetId);
				foundWidgetId = true;
			}
			index ++;
		}
		
		if(foundWidgetId == false) {
			log.info("ERROR: The widget ID could not be retrieved from the widget entry");
			log.info(widgetEntry.toString());
			return null;
		}
		return widgetId;
	}
}