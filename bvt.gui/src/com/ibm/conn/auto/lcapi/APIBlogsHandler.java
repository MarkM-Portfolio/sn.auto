package com.ibm.conn.auto.lcapi;

import java.util.TimeZone;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.lcapi.common.APIHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.CommunityBlogPermissions;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;


public class APIBlogsHandler extends APIHandler<BlogsService> {

	private static final Logger log = LoggerFactory.getLogger(APIBlogsHandler.class);
	private String userName;

	public APIBlogsHandler(String serverURL, String username, String password) {

		super("blogs", serverURL, username, password);
		userName = username;
		
	}
	
	@Override
	protected BlogsService getService(AbderaClient abderaClient, ServiceEntry generalService) {
		BlogsService service = null;
		try {
			service = new BlogsService(abderaClient, generalService);
		} catch (LCServiceException e) {
			Assert.fail("Unable create Blogs service: " + e.getMessage());
		}
		return service;
	}
	
	public Blog createBlog(BaseBlog blog) {
		log.info("INFO: Creating Blog:");
				
		Blog testBlog = new Blog(blog.getName(), blog.getBlogAddress(), blog.getDescription(), blog.getTags(), 
								false, false, null, null, TimeZone.getDefault(), blog.getAllowComments(), 13, true, blog.getModerateComments(), true, 0, -1, null, null, null, 0);
		
		Entry blogResult = (Entry) service.createBlog(testBlog);
		log.info("Blog Headers: " + service.getDetail());
		log.info("Check return code of createBlog call, it should be 201");
		int responseCode = service.getRespStatus();
		if (responseCode != 201){
			log.info("Blog not created successfully through API, User name: " + userName);
			Assert.fail("User: " + userName + " received response: " 
					+ responseCode + "; expected: 201; Blog was not created");		
		}
		log.info("Blog successfully created through API");
		log.info("Retrieve that Blog for full info");
		Blog result = new Blog (blogResult);

		if (APIUtils.resultSuccess(blogResult, "Blogs")) {
			return result;
		} else {
			return null;
		}
	}
	
	
	public Blog createBlog(BaseBlog blogObj, Community community) {

		String blogContainerID = community.getSelfLink();
		String blogCommunityUUID = community.getSelfLink();
		
		log.info("INFO: Creating Blog:");

		Blog testBlog = new Blog(blogObj.getName(), blogObj.getBlogAddress(), blogObj.getDescription(), blogObj.getTags(), true, false, CommunityBlogPermissions.PUBLIC, 
				null, TimeZone.getDefault(), true, 13, true, true, true, 0, -1, blogContainerID, blogCommunityUUID, null, 0);
		
		Entry blogResult = (Entry) service.createBlog(testBlog);
		
		testBlog = new Blog (blogResult);

		if (APIUtils.resultSuccess(blogResult, "Blogs")) {
			return testBlog;
		} else {
			return null;
		}
	}
	
	
	public BlogPost createBlogEntry(BaseBlogPost newPost, Blog blogs){
		
		log.info("INFO: Creating Blog Post:");

		//create entry in a blog
		BlogPost post = new BlogPost(newPost.getTitle(), newPost.getContent(), newPost.getTags(), 
									 newPost.getAllowComments(), newPost.getNumDaysCommentsAllowed());
		
		
	
		Entry postResult = (Entry) ((BlogsService) service).createPost(blogs, post);

		
		BlogPost response = new BlogPost(postResult);
		log.info("INFO: Title"+response.getTitle());
		log.info("INFO: Created entry to blog");

		return response;
		}
	
	public BlogComment createBlogComment(String blogCommentContent, BlogPost blogpost) {

		BlogComment comment = new BlogComment(blogCommentContent, blogpost.toEntry());
		Entry postResult = (Entry) service.createComment(blogpost, comment);
		BlogComment commentResult = new BlogComment(postResult);

		log.info("INFO: blog comment" + postResult.getTitle());
		log.info("INFO: Created blog post comment");
		
		return commentResult;
	}
	
	public void editBlog(Blog blog,String content){
		
		blog.setContent(content);
		Entry newEntry = blog.toEntry();
		newEntry.setContent(content);
		service.putEntry(blog.getEditHref(), newEntry);
		
		
	}
	
	public void editPost(BlogPost post , String content){
		
		post.setContent(content);
		String URL = post.getEditLink();
		
		service.editPost(URL, post);
			
	}
	
	public String editComment(BlogComment comment, String content){
		
		comment.setContentType("text");
		comment.setContent(content);
		
		service.putEntry(comment.getEditLink().replace("api", "api/comments"), comment.toEntry());
		
		//Return the edited content of the comment
		return comment.getContent();
		
	}
	
	public void createFollow(Blog blog){
		
		Entry newEntry = Abdera.getNewFactory().newEntry();
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/source", "blogs", "blogs");
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-type", "blog", "blog");
		newEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/resource-id", blog.getId().toString().substring(28), blog.getId().toString().substring(28));
		newEntry.addCategory(StringConstants.SCHEME_FLAGS, "following", "Following");
		
		
		service.postBlogsFeed(service.getServiceURLString() + "/follow/atom/resources", newEntry);
		
		
		
	}
	
	/**
	 * 
	 * @param blogs - A Blog object
	 * @param mentions - A Mentions object containing information about the user who will be mentioned
	 * @return response - A BlogPost object
	 */
	public BlogPost addMention_BlogEntryAPI(Blog blogs, Mentions mentions){
		
		log.info("INFO: Creating Blog Post:");
		BlogPost post = new BlogPost("Test for Blogs Entry Mention" + Helper.genDateBasedRand(), "content", "tags", 
									 true, 10);

		log.info("INFO: Creating Entry");
		Entry blogEntry = post.toEntry();
		blogEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
		BlogPost post2 = new BlogPost(blogEntry);
		
		Entry postResult = (Entry) service.createPost(blogs, post2);

		
		BlogPost response = new BlogPost(postResult);
		log.info("INFO: Title: " + response.getTitle());
		log.info("INFO: Created entry to blog");

		return response;
			
	}
	
	/**
	 * This method is being deprecated in favour of addBlogCommentMentionAPI (see directly below)
	 * @param blogpost
	 * @param mentions
	 * @return
	 */
	@Deprecated
	public BlogComment addMention_BlogComment( BlogPost blogpost, User user2Mention, String userID){
		
		BlogComment comment = new BlogComment("content", blogpost.toEntry());
		Entry commentEntry = comment.toEntry();
		commentEntry.setContentAsHtml("<p dir='ltr'><span class='vcard'><a class='fn url' href='https://dubxpcvm080.mul.ie.ibm.com:9444/profiles/html/profileView.do?userid=" + userID + "'>@" + user2Mention.getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + userID + "</span></span></p>");
		BlogComment comment2 = new BlogComment(commentEntry);
		
		Entry postResult = (Entry) service.createComment(blogpost, comment2);
		BlogComment commentResult = new BlogComment(postResult);

		log.info("INFO: blog comment" + postResult.getTitle());
		log.info("INFO: Created blog post");
		
		return commentResult;
		
	}
	
	/**
	 * 
	 * @param blogpost - The blog post on which the comment will be made
	 * @param mentions - The mentions object which will be used to mention the user
	 * 						The mentions.getBeforeMentionText() and mentions.getAfterMentionText() methods should return a unique String
	 * 						to make verification easier
	 * @return commentResult - A BlogComment object
	 */
	public BlogComment addBlogCommentMentionAPI(BlogPost blogPost, Mentions mentions){
		
		BlogComment comment = new BlogComment("content", blogPost.toEntry());
		Entry commentEntry = comment.toEntry();
		commentEntry.setContentAsHtml("<p dir='ltr'>" + mentions.getBeforeMentionText() + " <span class='vcard'><a class='fn url' href='" + mentions.getBrowserURL() + "profiles/html/profileView.do?userid=" + mentions.getUserUUID() + "'>@" + mentions.getUserToMention().getDisplayName() + "</a><span class='x-lconn-userid' style='display : none'>" + mentions.getUserUUID() + "</span></span> " + mentions.getAfterMentionText() + "</p>");
		BlogComment comment2 = new BlogComment(commentEntry);
		
		Entry postResult = (Entry) service.createComment(blogPost, comment2);
		BlogComment commentResult = new BlogComment(postResult);

		log.info("INFO: blog comment" + postResult.getTitle());
		log.info("INFO: Created blog post");
		
		return commentResult;
		
	}
	
	public void like(BlogPost post){
		
		Entry recommendEntry = Abdera.getNewFactory().newEntry();
		recommendEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "recommendation", null);
		recommendEntry.setTitle("like");
		
		String URL = post.getEditLink().replace("/api/", "/api/recommend/");
		
		
		service.postBlogsFeed(URL, recommendEntry);
		
		
		
	}
	
	public boolean unlike(BlogPost post){

		String URL = post.getEditLink().replace("/api/", "/api/recommend/");
		
		boolean deleted = service.deleteBlogsFeed(URL);
		
		return deleted;
	}
	
	public void like(BlogComment comment){
			
			Entry recommendEntry = Abdera.getNewFactory().newEntry();
			recommendEntry.addCategory("http://www.ibm.com/xmlns/prod/sn/type", "recommendation", null);
			recommendEntry.setTitle("like");
			
			String URL = comment.getEditLink().replace("/api/", "/api/recommend/comments/");
			
			service.postBlogsFeed(URL, recommendEntry);
		
		
	}
	
	public void deleteBlog(Blog blogs){
		
		log.info("INFO: Deleting blog");
		service.deleteBlog(blogs.getEditLink());
	}
	
	/**
	 * Deletes a blog comment posted to a blog entry
	 * 
	 * @param blogComment - The BlogComment instance of the comment to be deleted
	 * @return - True if the delete operation is successful, false otherwise
	 */
	public boolean deleteComment(BlogComment blogComment){
		
		log.info("INFO: Now deleting the comment posted to the blog entry with content: " + blogComment.getContent().trim());
		int deleteResponse = service.deleteWithResponse(blogComment.getEditLink().replace("/api/", "/api/comments/")).getStatus();
		
		if(deleteResponse >= 200 && deleteResponse <= 204) {
			log.info("INFO: The blog comment was successfully deleted");
			return true;
		} else {
			log.info("ERROR: Blog comment deletion returned a status code of " + deleteResponse);
			return false;
		}
	}
	
	public boolean  deleteBlogPost(BlogPost blogPost){

		log.info("INFO: Deleting blog post");
		return service.deletePost(blogPost.getEditLink());
	}

	/**
	 * Method to flag one blog entry 
	 * @param entryTitle - the title of entry
	 */
	public boolean flagBlogEntry(String entryTitle){
		log.info("INFO: Flag one blog entry");

		Feed entriesFeed = (Feed) service.getEntriesFeed();
		String entrySelfLink = null;

		for (Entry e : entriesFeed.getEntries())
			if (e.getTitle().equals(entryTitle))
				entrySelfLink = e.getSelfLinkResolvedHref().toString();
	
		Entry reportEntry = Abdera.getNewFactory().newEntry();
		reportEntry.addCategory(StringConstants.REL_ISSUE,
				"001", "Legal issue");
		reportEntry.addLink(entrySelfLink, "related");
		reportEntry.setContent("Crazy serious issue here.");

		boolean flag = false;
		try {
			service.flagBlogPost(reportEntry);
			flag = true;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * Method to flag all comments of one blog entry
	 * @param entryTitle - the entry title
	 */
	public boolean flagBlogComments(String entryTitle){
		log.info("INFO: Flag all comments of one blog entry");
		
		Feed commentsFeed = (Feed) service.getCommentsFeed(null);
		String comment2SelfLink = null;
	
		boolean flag = false;
		for(Entry entry : commentsFeed.getEntries()){
			if(entry.getTitle().contains(entryTitle)){
				comment2SelfLink = entry.getSelfLinkResolvedHref().toString();
				Entry reportEntry = Abdera.getNewFactory().newEntry();
				reportEntry.addCategory(StringConstants.REL_ISSUE,
					"001", "Legal issue");
				reportEntry.addLink(comment2SelfLink, "related");
				reportEntry.setContent("Crazy serious issue here.");
			
				try {
					service.flagBlogComment(reportEntry);
					flag = true;
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
}
