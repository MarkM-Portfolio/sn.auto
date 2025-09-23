package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

// Community Blogs creation doesn't work, further investigation required
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClassPopulation;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.blogs.BlogsService;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants.CommunityBlogPermissions;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class ASBlogsPopulationHelper {
	
	private static BlogsService service;
		
	protected static Logger LOGGER = FvtMasterLogsClassPopulation.LOGGER;

	
	
	
	public ASBlogsPopulationHelper() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.ASSEARCH);
		ServiceEntry blogsServiceEntry = restAPIUser.getService("blogs");
		restAPIUser.addCredentials(blogsServiceEntry);
		service = new BlogsService(restAPIUser.getAbderaClient(),
				blogsServiceEntry);
		
	}

	

	public void createCommunityBlogs() {

		Community community = null;

		community = ASCommunitiesPopulationHelper.getPrivateCommunity();
		if ((community != null) && (service != null)) {

			createCommunityBlog(community,
					PopStringConstantsAS.PRIVATE_COMMUNITY_BLOG_TITLE + " "
							+ PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.PRIVATE_COMMUNITY_BLOG_CONTENT,
					"Private", PopStringConstantsAS.PRIVATE_COMMUNITY_BLOG_TAG,
					true,
					PopStringConstantsAS.PRIVATE_COMMUNITY_BLOG_ENTRY_TITLE,
					PopStringConstantsAS.PRIVATE_COMMUNITY_BLOG_ENTRY_CONTENT,
					PopStringConstantsAS.PRIVATE_COMMUNITY_BLOG_ENTRY_TAG);

		}
		community = null;
		community = ASCommunitiesPopulationHelper.getModeratedCommunity();
		if ((community != null) && (service != null)) {

			createCommunityBlog(
					community,
					PopStringConstantsAS.MODERATED_COMMUNITY_BLOG_TITLE + " "
							+ PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.MODERATED_COMMUNITY_BLOG_CONTENT,
					"Moderated",
					PopStringConstantsAS.MODERATED_COMMUNITY_BLOG_TAG,
					true,
					PopStringConstantsAS.MODERATED_COMMUNITY_BLOG_ENTRY_TITLE,
					PopStringConstantsAS.MODERATED_COMMUNITY_BLOG_ENTRY_CONTENT,
					PopStringConstantsAS.MODERATED_COMMUNITY_BLOG_ENTRY_TAG);
		}
		community = null;
		community = ASCommunitiesPopulationHelper.getPublicCommunity();
		if ((community != null) && (service != null)) {

			createCommunityBlog(community,
					PopStringConstantsAS.PUBLIC_COMMUNITY_BLOG_TITLE + " "
							+ PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.PUBLIC_COMMUNITY_BLOG_CONTENT,
					"Public", PopStringConstantsAS.PUBLIC_COMMUNITY_BLOG_TAG,
					true,
					PopStringConstantsAS.PUBLIC_COMMUNITY_BLOG_ENTRY_TITLE,
					PopStringConstantsAS.PUBLIC_COMMUNITY_BLOG_ENTRY_CONTENT,
					PopStringConstantsAS.PUBLIC_COMMUNITY_BLOG_ENTRY_TAG);
		}

	}

	public void createStandaloneBlogs() {

		createStandaloneBlog(PopStringConstantsAS.STANDALONE_BLOG_TITLE + " "
				+ PopStringConstantsAS.eventIdent,
				PopStringConstantsAS.STANDALONE_BLOG_CONTENT,
				PopStringConstantsAS.STANDALONE_BLOG_TAG, true,
				PopStringConstantsAS.STANDALONE_BLOG_ENTRY_TITLE,
				PopStringConstantsAS.STANDALONE_BLOG_ENTRY_CONTENT,
				PopStringConstantsAS.STANDALONE_BLOG_ENTRY_TAG);
	}

	public void createStandaloneBlogsForHighlightTC() throws Exception {

		createStandaloneBlog(PopStringConstantsAS.HIGHLIGHT_TC_BLOG_TITLE + " "
				+ PopStringConstantsAS.eventIdent,
				PopStringConstantsAS.HIGHLIGHT_TC_BLOG_TITLE,
				PopStringConstantsAS.STANDALONE_BLOG_TAG, false, null, null,
				null);
		createStandaloneBlog(PopStringConstantsAS.HIGHLIGHT_TC_BLOG_TITLE_2
				+ " " + PopStringConstantsAS.eventIdent,
				PopStringConstantsAS.HIGHLIGHT_TC_BLOG_TITLE_2,
				PopStringConstantsAS.STANDALONE_BLOG_TAG, false, null, null,
				null);
		createStandaloneBlog(PopStringConstantsAS.HIGHLIGHT_TC_BLOG_TITLE_3
				+ " " + PopStringConstantsAS.eventIdent,
				PopStringConstantsAS.HIGHLIGHT_TC_BLOG_TITLE_3,
				PopStringConstantsAS.STANDALONE_BLOG_TAG, false, null, null,
				null);
	}

	public void populate() {
		try {
			
			createCommunityBlogs();
			createStandaloneBlogs();
			createStandaloneBlogsForHighlightTC();
		} catch (Exception e) {
			LOGGER.fine("Exception in Blogs population: " + e.getMessage());
		}
	}

	// *******************************************************************************************************************
	// *******************************************************************************************************************
	// Working functions
	// *******************************************************************************************************************
	// *******************************************************************************************************************

	public void createCommunityBlog(Community blogCommunity, String blogTitle,
			String blogContent, String blogPermissions, String blogsTags,
			Boolean createBlogEntry, String blogEntryTitle,
			String blogEntryContent, String blogEntryTags) {
		boolean communityBlog = true;
		CommunityBlogPermissions communityBlogPermissions = null;
		String blogContainerID = blogCommunity.getSelfLink();
		String blogCommunityUUID = blogCommunity.getSelfLink();

		if (blogPermissions.equals("Moderated")) {
			communityBlogPermissions = CommunityBlogPermissions.MODERATED;
		} else if (blogPermissions.equals("Private")) {
			communityBlogPermissions = CommunityBlogPermissions.PRIVATE;
		} else if (blogPermissions.equals("Public")) {
			communityBlogPermissions = CommunityBlogPermissions.PUBLIC;
		} else if (blogPermissions.equals(null)) {
			communityBlogPermissions = null;
		}

		Blog regBlog = new Blog(blogTitle, blogContent, blogContent, blogsTags,
				communityBlog, false, communityBlogPermissions, null,
				TimeZone.getDefault(), true, 13, true, true, true, 0, -1,
				blogContainerID, blogCommunityUUID, null, 0);
		Entry regBlogEntry = (Entry) service.createBlog(regBlog);
		Blog regBlogResult = new Blog(regBlogEntry);
		if (regBlogResult != null) {

			if (createBlogEntry) {

				BlogPost post = new BlogPost(blogEntryTitle, blogEntryContent,
						blogEntryTags, true, 10);
				Entry blogCommentCreationResult = (Entry) service.createPost(
						regBlogResult, post);
				BlogPost commentCreationResponse = new BlogPost(
						blogCommentCreationResult);
			}
		} else {
			LOGGER.fine("Community Blog creation failed");
		}

	}

	public void createStandaloneBlog(String blogTitle, String blogContent,
			String blogTags, Boolean createBlogEntry, String blogEntryTitle,
			String blogEntryContent, String blogEntryTags) {
		boolean communityBlog = false;
		String blogContainerID = null;
		String blogCommunityUUID = null;

		if (service != null) {
			Blog regBlog = new Blog(blogTitle, blogContent, blogContent,
					blogTags, communityBlog, false, null, null,
					TimeZone.getDefault(), true, 13, true, true, true, 0, -1,
					blogContainerID, blogCommunityUUID, null, 0);
			Entry regBlogEntry = (Entry) service.createBlog(regBlog);
			Blog regBlogResult = new Blog(regBlogEntry);
			if (regBlogResult != null) {

				if (createBlogEntry) {

					BlogPost post = new BlogPost(blogEntryTitle,
							blogEntryContent, blogEntryTags, true, 10);
					Entry blogCommentCreationResult = (Entry) service
							.createPost(regBlogResult, post);
					BlogPost commentCreationResponse = new BlogPost(
							blogCommentCreationResult);
				}
			} else {
				LOGGER.fine("Standalone blog creation failed");
			}

		} else {
			LOGGER.fine("Standalone blog creation failed");
		}

	}

}
