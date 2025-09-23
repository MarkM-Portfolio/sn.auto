package com.ibm.lconn.automation.framework.services.blogs;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Categories;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.blogs.nodes.MediaLink;
import com.ibm.lconn.automation.framework.services.common.LCService;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.BlogsField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.BlogsType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.Utils;

/**
 * Blogs Service object handles getting/posting data to the Connections Blogs service.
 * 
 * @author James Cunningham - jamcunni@ie.ibm.com
 */
public class BlogsService extends LCService {
	protected final static Logger LOGGER = LoggerFactory
			.getLogger(BlogsService.class.getName());
	private String snx_BlogsHomepageHandle;
	private HashMap<String, HashMap<String, String>> blogsURLs;
	
	/**
	 * Constructor to create a new Blogs Service helper object. 
	 * 
	 * This object contains helper methods for all API calls that are supported by the Blogs service.
	 * 
	 * @param client	the authenticated AbderaClient that is used to handle requests to/from server
	 * @param service	the ServiceEntry that contains information about the Blogs service from the server ServiceConfigs file
	 * @throws LCServiceException 
	 */
	public BlogsService(AbderaClient client, ServiceEntry service) throws LCServiceException {
		this(client, service, new HashMap<String, String>());	
	}
	
	public BlogsService(AbderaClient client, ServiceEntry service, Map<String, String> headers) throws LCServiceException {
		super(client, service);
		for(String key : headers.keySet()){			
			this.options.setHeader(key, headers.get(key));			
		}		
		updateServiceDocument();
	}
	
	public String getURLString(){
		return service.getServiceURLString();
	}
	
	private void updateServiceDocument() throws LCServiceException {
		LOGGER.debug("updateServiceDocument is called");
		ExtensibleElement feed;
		if ( StringConstants.DEPLOYMENT_TYPE != DeploymentType.SMARTCLOUD ){
			LOGGER.debug("non smart cloud feed, call getFeed");
			feed = getFeed(service.getServiceURLString() + URLConstants.BLOGS_SERVICE);
		} else {
			LOGGER.debug("smart cloud feed, call getFeedWithRedirect");
			feed = getFeedWithRedirect(service.getServiceURLString() + URLConstants.BLOGS_SERVICE);
		}
		
		LOGGER.debug("feed!=null?"+(feed != null));
		LOGGER.debug("resp Status:"+ getRespStatus());
		if(feed != null) {
			if(getRespStatus() == 200) {	
				LOGGER.debug("feed is not null, and resp status=200");
				setFoundService(true);
				blogsURLs = getBlogsCollectionUrls((Service) feed);
				Element blogsHomepageHandleElement = feed.getExtension(StringConstants.SNX_BLOGS_HOMEPAGE_HANDLE);
				LOGGER.debug("the homepage url will be set as "+blogsHomepageHandleElement.getText());
				if (blogsHomepageHandleElement != null){
					setBlogsHomepageHandle(blogsHomepageHandleElement.getText());
				}
			} else {
				LOGGER.debug("the homepage url will not be set because resp status is not 200");
				setFoundService(false);
				throw new LCServiceException("Error : Can't get BlogsService Feed, status: " + getRespStatus());
			}
		} else {
			LOGGER.debug("the homepage url will not be set because feed is null");
			setFoundService(false);
			throw new LCServiceException("Error : Can't get BlogsService Feed: " + getRespStatus());
		}
	}
	
	protected HashMap<String, HashMap<String, String>> getBlogsCollectionUrls(Service service) {
		HashMap<String, HashMap<String, String>> blogs = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> collectionUrls;
		
		for(Workspace workspace : service.getWorkspaces()) {
			collectionUrls = new HashMap<String, String>();
			for(Collection collection : workspace.getCollections()) {
				collectionUrls.put(collection.getTitle(), collection.getHref().toString());
				getApiLogger().debug("getBlogsCollectionUrls"+collectionUrls.get(collection.getTitle()).toString());
			}
			blogs.put(workspace.getTitle(), collectionUrls);
		}
		
		return blogs;
	}
	
	public ArrayList<Blog> getAllBlogs(String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ArrayList<Blog> allBlogs = new ArrayList<Blog>();
		ExtensibleElement feed;
		
		feed = searchBlogs(service.getServiceURLString() + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_ALL, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		Feed allBlogsFeed = (Feed)feed;
		
		if (allBlogsFeed != null) {
			for(Entry blog : allBlogsFeed.getEntries()) {
				Blog data = new Blog(blog);
				allBlogs.add(data);
			}
		}
		
		return allBlogs;
	}
	
	//BaseURL/blogs/{blogHandlerWITHOUTspaces}/api/media
	public String getBlogsMediaURLString(String handle){
		return (service.getServiceURLString()+ "/" + handle + URLConstants.BLOGS_MEDIA);
	}
	
	public Feed getAllBlogsFeed(String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_ALL, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		Feed allBlogsFeed = (Feed)eelement;
				
		return allBlogsFeed;
	}
	
	public Feed getLatestPostsFeed(String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_ALL_LATEST_POSTS, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		Feed latestBlogsFeed = (Feed)eelement;
		
		return latestBlogsFeed;
	}
	
	public Feed getLatestCommentsFeed(String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_ALL_LATEST_COMMENTS, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		Feed latestCommentsFeed = (Feed) eelement;
		
		return latestCommentsFeed;
	}
	
	//blogs/roller-ui/rendering/feed/<handle>/comments/atom?lang=en_us&contentFormat=html
	public ExtensibleElement getCommentsFeed(String contentFormat) {
		ExtensibleElement eelement = getFeed(service.getServiceURLString() + URLConstants.BLOGS_RENDERING_FEED + getBlogsHomepageHandle() + "/comments/atom?lang=en_us&contentFormat="+contentFormat);
		return eelement;
	}
	
	//blogs/roller-ui/rendering/feed/<handle>/entries/atom?lang=en_us
	public ExtensibleElement getEntriesFeed(){
		ExtensibleElement eelement = getFeed(service.getServiceURLString() + URLConstants.BLOGS_RENDERING_FEED + getBlogsHomepageHandle()+ "/entries/atom?lang=en_us");
		return eelement;
	}
	
	public Feed getRecommendedPostsFeed(String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_RECOMMENDED_POSTS, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		return (Feed)eelement;
	}
	
	public Feed getRecentPostsBlogsFeed(String handle, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, String tags) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + handle + URLConstants.BLOGS_ALL_LATEST_POSTS, null, null, page, pageSize, textSearch, since, sortBy, sortOrder, null, tags, null);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		return (Feed)eelement;
	}
	
	public Feed getMediaEntriesFeed(String handle, int page, int pageSize) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + handle + URLConstants.BLOGS_MEDIA_ENTRIES, null, null, page, pageSize, null, null, null, null, null, null, null);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		return (Feed)eelement;
	}
	
	public Feed getCommentsAddedBlogsFeed(String handle, int page, int pageSize) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + handle + URLConstants.BLOGS_ALL_LATEST_COMMENTS, null, null, page, pageSize, null, null, null, null, null, null, null);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		return (Feed)eelement;
	}
	
	public Feed getDefaultBlogFeed(String handle, String email, int page, int pageSize, String textSearch, SortBy sortBy, SortOrder sortOrder, String tags) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + URLConstants.BLOGS_PERSON_RECENT_POSTS + email, null, null, page, pageSize, textSearch, null, sortBy, sortOrder, null, tags, null);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		return (Feed)eelement;
	}
	
	public ArrayList<BlogPost> getAllBlogsLatestPosts(String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ArrayList<BlogPost> allBlogsLatestPosts = new ArrayList<BlogPost>();
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_ALL_LATEST_POSTS, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		Feed allBlogsLatestPostsFeed = (Feed) eelement;
		
		if (allBlogsLatestPostsFeed != null) {
			for(Entry blogPostEntry : allBlogsLatestPostsFeed.getEntries()) {
				BlogPost data = new BlogPost(blogPostEntry);
				allBlogsLatestPosts.add(data);
			}
		}
		
		return allBlogsLatestPosts;
	}
	
	
	public ArrayList<BlogComment> getAllBlogsLatestComments(String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ArrayList<BlogComment> allBlogsLatestComments = new ArrayList<BlogComment>();
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_ALL_LATEST_COMMENTS, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		Feed allBlogsLatestCommentsFeed = (Feed) eelement;
		
		if (allBlogsLatestCommentsFeed != null) {
			for(Entry commentEntry : allBlogsLatestCommentsFeed.getEntries()) {
				BlogComment data = new BlogComment(commentEntry);
				allBlogsLatestComments.add(data);
			}
		}
		
		return allBlogsLatestComments;
	}
	
	public ArrayList<Category> getAllBlogsTags() {
		ExtensibleElement eelement = getFeed(service.getServiceURLString()  + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_ALL_TAGS);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		Categories categoryDocument = (Categories) eelement;
		
		return new ArrayList<Category>(categoryDocument.getCategories());
	}
	
	public ArrayList<Category> getBlogTags(String handle) {
		ExtensibleElement eelement = getFeed(service.getServiceURLString()  + "/" + handle + URLConstants.BLOGS_ALL_TAGS);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		Categories categoryDocument = (Categories) eelement;
				
		return new ArrayList<Category>(categoryDocument.getCategories());
	}
	
	public ArrayList<Category> getAllBlogsIssueCategories() {
		ExtensibleElement eelement = getFeed(service.getServiceURLString()  + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_ISSUE_CATEGORIES);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		Categories categoryDocument = (Categories) eelement;
		
		return new ArrayList<Category>(categoryDocument.getCategories());
	}
	
	public ArrayList<Blog> getFeaturedBlogs(String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ArrayList<Blog> featuredBlogs = new ArrayList<Blog>();
		ExtensibleElement eelement =  searchBlogs(service.getServiceURLString() + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_FEATURED_BLOGS, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		Feed featuredBlogsFeed = (Feed)eelement;
		
		if (featuredBlogsFeed != null) {
			for(Entry blog: featuredBlogsFeed.getEntries()) {
				Blog data = new Blog(blog);
				featuredBlogs.add(data);
			}
		}
		
		return featuredBlogs;
	}
	


	public ArrayList<Blog> getMyBlogs(String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ArrayList<Blog> myBlogs = new ArrayList<Blog>();
		ExtensibleElement myBlogsFeed =  searchBlogs(blogsURLs.get(StringConstants.BLOGS).get(StringConstants.MY_BLOGS), email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertTrue("Blogs is not indexed", !(getRespStatus()==404));
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		// on cloud server return empty response entry
	
		if (myBlogsFeed != null) {
			for(Entry blog :((Feed)myBlogsFeed).getEntries()) {
				Blog data = new Blog(blog);
				myBlogs.add(data);
				///System.out.println(data.toString());
				getApiLogger().debug(data.toString());
			}
		}
		
		return myBlogs;
	}
	
	// TODO: Get Default Blog Entries
	
	public ArrayList<BlogPost> getBlogEntries(Blog blog, String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ArrayList<BlogPost> blogEntries = new ArrayList<BlogPost>();
		ExtensibleElement blogPostFeed = searchBlogs(blog.getAlternateHref()+URLConstants.BLOGS_ENTRIES, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		
		if (blogPostFeed != null) {
			for(Entry blogPost: ((Feed) blogPostFeed).getEntries()) {
				BlogPost data = new BlogPost(blogPost);
				blogEntries.add(data);
				getApiLogger().debug(data.toString());
			}
		}
		
		return blogEntries;
	}
	
	public ArrayList<BlogComment> getBlogComments(Blog blog, String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ArrayList<BlogComment> blogComments = new ArrayList<BlogComment>();
		ExtensibleElement blogCommentsFeed = searchBlogs(blog.getAlternateHref()+URLConstants.BLOGS_COMMENTS, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		
		if (blogCommentsFeed != null) {
			for(Entry blogComment: ((Feed) blogCommentsFeed).getEntries()) {
				BlogComment data = new BlogComment(blogComment);
				blogComments.add(data);
				getApiLogger().debug(data.toString());
			}
		}
		
		return blogComments;
	}
	
	public ArrayList<MediaLink> getBlogMediaLinks(Blog blog, String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ArrayList<MediaLink> mediaLinks = new ArrayList<MediaLink>();
		ExtensibleElement mediaLinksFeed = searchBlogs(blog.getAlternateHref()+URLConstants.BLOGS_MEDIA, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		
		if (mediaLinksFeed != null) {
			for(Entry mediaLink: ((Feed) mediaLinksFeed).getEntries()) {
				MediaLink data = new MediaLink(mediaLink);
				mediaLinks.add(data);
				getApiLogger().debug(data.toString());
			}
		}
		
		return mediaLinks;
	}

	public ExtensibleElement createBlog(Blog newBlog) {
		ExtensibleElement result = postFeed(blogsURLs.get(StringConstants.BLOGS).get(StringConstants.MY_BLOGS).toString(), newBlog.toEntry());
		//updateServiceDocument();
		
		return result;
	}

	public ExtensibleElement editBlog(String blogEditURL, Blog blog) {
		ExtensibleElement result =  putFeed(blogEditURL, blog.toEntry());
		//updateServiceDocument();
		
		return result;
	}

	public boolean deleteBlog(String blogEditURL) {
		boolean result = deleteFeed(blogEditURL);
		//updateServiceDocument();
		
		return result;
	}

/*	public ExtensibleElement createPost(String blogName, BlogPost blogPost) {
		return postFeed(blogsURLs.get(blogName).get(StringConstants.WEBLOG_ENTRIES), blogPost.toEntry());
	}*/
	
	public ExtensibleElement createPost(Blog blog, BlogPost blogPost) {
		return postFeed(blog.getAlternateHref()+URLConstants.BLOGS_ENTRIES, blogPost.toEntry());
	}
	
	public ExtensibleElement createPost(Blog blog, BlogPost blogPost, File media) {
		return postMultipartFeed(blog.getAlternateHref()+URLConstants.BLOGS_ENTRIES, blogPost.toEntry(), media);
	}
	
	public ExtensibleElement createBookmarkPost(Blog blog, Entry newBookmark) {
		return postFeed(blog.getAlternateHref()+URLConstants.BLOGS_ENTRIES, newBookmark);
	}
	
	public ExtensibleElement editPost(String postEditUrl, BlogPost blogPost) {
		return putFeed(postEditUrl, blogPost.toEntry());
	}
	
	public ExtensibleElement getPost(String postEditUrl) {
		return getFeed(postEditUrl);
	}
	
	public ExtensibleElement postFile(String url, File file) throws FileNotFoundException{
		return (super.postFile(url, file)); //same name as method above it in hierarchy - use super to make sure it uses LCService's postFile method as opposed to a recursive call
	}
	
	public ExtensibleElement postBlogsFeed(String postEditUrl, ExtensibleElement entry) {
		return postFeed(postEditUrl, entry);
	}
	
	public ExtensibleElement putBlogsFeed(String putEditUrl, ExtensibleElement entry) {
		return putFeed(putEditUrl, entry);
	}
	
	public boolean deletePost(String postEditUrl) {
		return deleteFeed(postEditUrl);
	}
	
	/*public ExtensibleElement createComment(String blogName, BlogComment blogComment) {
		return postFeed(blogsURLs.get(blogName).get(StringConstants.COMMENT_ENTRIES), blogComment.toEntry());
	}*/
	
	public ExtensibleElement createComment(Blog blog, BlogComment blogComment) {
		return postFeed(blog.getAlternateHref()+URLConstants.BLOGS_COMMENTS, blogComment.toEntry());
	}
	
	public ExtensibleElement createComment(BlogPost post, BlogComment blogComment) {
		return postFeed(post.getCommentsHref(), blogComment.toEntry());
	}
	
	public ExtensibleElement getComment(String commentEditUrl) {
		return getFeed(commentEditUrl);
	}
	
	public boolean deleteComment(String commentEditUrl) {
		return deleteFeed(commentEditUrl);
	}
	
	public ExtensibleElement recommendEntry(BlogPost blogPost) {
		return postFeed(blogPost.getRecommendationsHref(), Abdera.getInstance().newEntry());
	}
	
	public ExtensibleElement getRecommenders(BlogPost blogPost) {
		return getFeed(blogPost.getRecommendationsHref());
	}
	
	public ExtensibleElement createMediaResource(Blog blog, File file) throws FileNotFoundException {
		return postFile(blog.getAlternateHref()+URLConstants.BLOGS_MEDIA, file);
	}
	
	public ExtensibleElement createMediaResource(Blog blog, File file, InputStream infile) throws FileNotFoundException {
		return postFileStream(blog.getAlternateHref()+URLConstants.BLOGS_MEDIA, file, infile);
	}
	
	// TODO: Finish up Working with Media Resources
	// Waiting on new documentation...
	// createMedia/getMedia/editMedia/deleteMedia functions

	public void setBlogsHomepageHandle(String blogsHomepageHandle) {
		this.snx_BlogsHomepageHandle = blogsHomepageHandle;
	}

	public String getBlogsHomepageHandle() {
		return snx_BlogsHomepageHandle;
	}
	
	public void deleteTests(){
		ArrayList<Blog> myBlogs = getMyBlogs(null, null, 0, 0, null, null, null, null, BlogsType.BLOG, null, null);
		for(Blog blog : myBlogs){
			if(blog.getTitle().equals("Spencer") || blog.getTitle().equals("JamesBlog") || blog.getTitle().equals("Finn")){
				deleteBlog(blog.getEditHref());
			}
		}
	}

	public ExtensibleElement searchBlogs(String sourceURL, String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		String searchPath = sourceURL;
		
		if(searchPath.lastIndexOf("?") == -1 )
			searchPath += "?";
		else
			searchPath += "&";
		
		if(email != null && email.length() != 0)
			searchPath += "email=" + email + "&";
		
		if(field != null)
			searchPath += "f=" + String.valueOf(field).toLowerCase() + "&";
		
		if(page > 0)
			searchPath += "page=" + page + "&";
		
		if(pageSize > 0)
			searchPath += "ps=" + pageSize + "&";
		
		if(textSearch != null && textSearch.length() != 0)
			searchPath += "search=" + textSearch + "&";
		
		if(since != null)
			searchPath += "since=" + Utils.dateFormatter.format(since) + "&";
		
		if(sortBy != null)
			searchPath += "sortby=" + String.valueOf(sortBy).toLowerCase() + "&";
		
		if(sortOrder != null)
			searchPath += "sortorder=" + String.valueOf(sortOrder).toLowerCase() + "&";
		
		if(type != null)
			searchPath += "t=" + String.valueOf(type).toLowerCase() + "&";
		
		if(tags != null && tags.length() != 0)
			searchPath += "tag=" + tags + "&";
		
		if(userid != null && userid.length() != 0)
			searchPath += "userid=" + userid + "&";
		
		return getFeed(searchPath);
	}
	
	
	public void deleteAllBlogs(){
		ArrayList<Blog> myBlogs = getMyBlogs(null, null, 0, 0, null, null, null, null, null, null, null);
		for(Blog blog : myBlogs){			
			deleteBlog(blog.getEditHref());
		}
	}
	
	public ExtensibleElement searchBlogsFeed(String searchString){
		return getFeed(service.getServiceURLString() + URLConstants.BLOGS_RENDERING_FEED + getBlogsHomepageHandle() + "/blogs/atom?search=" + searchString + "&t=blog&f=all&lang=en_us");
	}
	
	/*public String getCommentURL(String blogName) {
		return blogsURLs.get(blogName).get(StringConstants.COMMENT_ENTRIES);
	}*/

	public ExtensibleElement postComment(String commentURL, BlogComment blogComment) {
		return postFeed(commentURL, blogComment.toEntry());
	}
	
	public ExtensibleElement getTypeaheadTags(){
		return getFeed(URLConstants.SERVER_URL + "/homepage/atom/search/facets/tags?query=&component=blogs");
	}
	
	/*public HttpResponse postRecommend(String recommendUrl) throws Exception{
		return doHttpPost(recommendUrl, null);
	}
	*/
	
	public ExtensibleElement postRecommend(String recommendUrl){
		return postFeed(recommendUrl, Abdera.getNewFactory().newEntry());
	}
	
	public Feed getBlogsApiFeed(String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_SERVICE +"/blogs", email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		return (Feed)eelement;
	}
	
	public Feed getEntriesApiFeed(String blogHandle, String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + blogHandle + URLConstants.BLOGS_SERVICE +"/entries", email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		return (Feed)eelement;
	}
	
	public Feed getCommentsApiFeed(String blogHandle, String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + blogHandle + URLConstants.BLOGS_SERVICE +"/comments", email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		return (Feed)eelement;
	}
	
	public ExtensibleElement getBlogFeed(String url){
		return getFeed(url);
	}
	
	public ExtensibleElement getBlogFeedWithRedirect(String url){
		return 	getFeedWithRedirect(url);
	}
	
	public int createCommentCrx(Blog blog, BlogComment blogComment) {
		return postFeedWithCRX(blog.getAlternateHref()+URLConstants.BLOGS_ENTRIES, blogComment.toEntry());
	}

	/**
	 * Creates a blog with the specified users as extra owners or authors
	 * @param newBlog		The blog to create
	 * @param ownerEmails	Array of emails of users who will be the blog owners. Leave null if not wanted.
	 * @param authorEmails	Array of emails of users who will be the blog authors. Leave null if not wanted.
	 * @return Server response
	 */
	public ExtensibleElement createBlogWithMembers(Blog newBlog, String[] ownerEmails, String[] authorEmails) {
		Entry ntry = newBlog.toEntry();
		if (ownerEmails != null){
			for (String ownerEmail : ownerEmails)
				ntry.addAuthor("ignored", ownerEmail, "");
		}
		if (authorEmails != null){
			for (String authorEmail : authorEmails)
				ntry.addContributor("ignored", authorEmail, "");
		}
		ExtensibleElement result = postFeed(blogsURLs.get(StringConstants.BLOGS).get(StringConstants.MY_BLOGS).toString(), ntry);
		//updateServiceDocument();
		
		return result;
	}
	
	public ExtensibleElement getServiceDoc() {
		ExtensibleElement feed = getFeed(service.getServiceURLString()+ URLConstants.BLOGS_SERVICE);
		
		if(feed!=null){
			return feed;
			
	}else{
		return null;
	}
	}
	//Returns URL for Blogs Homepag
	public String getHompageUrl() {
		ExtensibleElement feed = getFeed(service.getServiceURLString() + URLConstants.BLOGS_SERVICE);
		Service srvc = (Service) feed;

		String homepageUrl = "";
		LOGGER.debug("snx_BlogsHomepageHandle="+snx_BlogsHomepageHandle);

		for(Workspace workspace : srvc.getWorkspaces()) {
			for(Collection collection : workspace.getCollections()) {
				if (collection.getHref().toString().contains(snx_BlogsHomepageHandle)) {
					homepageUrl = collection.getHref().toString();	
				}				
			}	
		}
		LOGGER.debug("Blogs homepageUrl="+homepageUrl);
		return homepageUrl;
	}
	
	public Feed getMyVotesFeed(String email, BlogsField field, int page, int pageSize, String textSearch, Date since, SortBy sortBy, SortOrder sortOrder, BlogsType type, String tags, String userid) {
		ExtensibleElement eelement = searchBlogs(service.getServiceURLString() + "/" + getBlogsHomepageHandle() + URLConstants.BLOGS_MYVOTES_POSTS, email, field, page, pageSize, textSearch, since, sortBy, sortOrder, type, tags, userid);
		assertEquals("get Blogs failed "+getDetail(), 200, getRespStatus());
		return (Feed)eelement;
	}
	
	public ExtensibleElement verifyUser(){
		ExtensibleElement verifyUser = getFeed(service.getServiceURLString() + URLConstants.BLOGS_VERIFY_USER);
		return verifyUser;
		
	}
	public ExtensibleElement getACLTokens(){
		ExtensibleElement getACLTokens = getFeed(service.getServiceURLString() + URLConstants.BLOGS_ACL_TOKENS);
		return getACLTokens;
	}
	
	public ExtensibleElement putEntry(String href, Entry ntry) {
		return putFeed(href, ntry);
	}	
	
	public ExtensibleElement getBlogsFeed(String url){
		return getFeed(url);
	}
	
	public boolean deleteBlogsFeed(String url){
		return deleteFeed(url);
	}
	
	public ExtensibleElement getModerationServiceDoc(){
		return getFeed(service.getServiceURLString() + URLConstants.BLOGS_MODERATION_SERVICE);
	}

	/**
	 * Flags a blog post
	 * @param reportEntry  Entry representing the flag. Contains information on the blog post
	 * 					   See API documentation for an example entry.
	 * @return	Server response
	 */
	public ExtensibleElement flagBlogPost(Entry reportEntry) {
		return postFeed(service.getServiceURLString()+"/" + getBlogsHomepageHandle() + URLConstants.BLOGS_REPORTS_ENTRIES, reportEntry);
	}
	
	/**
	 * Flags a blog comment
	 * @param reportEntry  Entry representing the flag. Contains information on the blog comment
	 * 					   See API documentation for an example entry.
	 * @return	Server response
	 */
	public ExtensibleElement flagBlogComment(Entry reportEntry) {
		return postFeed(service.getServiceURLString()+"/" + getBlogsHomepageHandle() + URLConstants.BLOGS_REPORTS_COMMENTS, reportEntry);
	}
	
	/**
	 * Post a blog entry using hardcoded URL.
	 * This method was created because createPost() was not working when posting 
	 * as someone other than the original blog owner.
	 * @param blogHandle
	 * @param blogPost
	 * @return Server response
	 */
	public ExtensibleElement createPostManually(String blogHandle, BlogPost blogPost){
		return postFeed(service.getServiceURLString() + "/" + blogHandle + "/api/entries", blogPost.toEntry());
	}
	
	/**
	 * Post a blog comment using hardcoded URL.
	 * This method was created because createComment() was not working when posting 
	 * as an author (not an owner) on an author's blog.
	 * @param blogHandle
	 * @param blogComment
	 * @return Server response
	 */
	public ExtensibleElement createCommentManually(String blogHandle, BlogComment blogComment){
		return postFeed(service.getServiceURLString() + "/" + blogHandle + "/api/comments", blogComment.toEntry());
	}
	
	/**
	 * Gets a feed of all blog posts in a blog.
	 * @param blogHandle
	 * @return Server response
	 */
	public ExtensibleElement getBlogEntriesFeed(String blogHandle){
		return getFeed(service.getServiceURLString() + "/" + blogHandle + URLConstants.BLOGS_ALL_LATEST_POSTS);
	}
	
	public ExtensibleElement getBlogEntriesRollerUI(String blogHandle){
		return getFeed(service.getServiceURLString() + "/roller-ui/rendering/api/" + blogHandle + "/api/entries");
	}
	public String getServiceURLString(){
		
		return service.getServiceURLString().toString();
	}
}