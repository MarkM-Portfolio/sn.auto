package com.ibm.conn.auto.lcapi.test;

import java.net.URISyntaxException;
import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogComment;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunityBlogsHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogComment;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class APICommunityBlogsTest extends SetUpMethods2 {
	
	private static Abdera abdera;
	private static AbderaClient client;
	private static Logger log = LoggerFactory.getLogger(APICalendarTest.class);
	private static ServiceConfig config;
	
	private APICommunityBlogsHandler communityBlogsAPIUser1;
	private APIProfilesHandler testUser2Profile;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configurations
		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
		communityBlogsAPIUser1 = new APICommunityBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		
		testUser2Profile = new APIProfilesHandler(serverURL, testUser2.getAttribute(cfg.getLoginPreference()), testUser2.getPassword());
		
		// Initialize Abdera
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		
		// Register SSL / Add credentials for user
		AbderaClient.registerTrustManager();
		
		// Get service config for server, assert that it was retrieved and contains the activities service information
		try {
			config = new ServiceConfig(client, serverURL, true);
		} catch(LCServiceException lcse) {
			log.info("ERROR: LCServiceException thrown when initialising config");
			lcse.printStackTrace();
		}
		
		ServiceEntry communities = config.getService("communities");
		assert(communities != null);

		try {
			Utils.addServiceAdminCredentials(communities, client);	
		} catch(URISyntaxException use) {
			log.info("INFO: URISyntaxException thrown when adding service admin credentials");
			use.printStackTrace();
		}
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_CreateBlogEntry() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a blog entry for the community blog");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = communityBlogsAPIUser1.createBlogEntry(publicCommunity, baseBlogPost);
		
		// Verify that the blog entry was created successfully
		assert blogPost != null : "ERROR: There was a problem with creating the community blog entry using the API";
		assert blogPost.getTitle().equals(baseBlogPost.getTitle()) == true : "ERROR: The title of the created blog entry was different from the title set in its base instance";
		assert blogPost.getContent().equals(baseBlogPost.getContent()) == true : "ERROR: The blog entry content was different from the content set in its base instance";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_EditBlogEntryDescription() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a blog entry for the community blog");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = communityBlogsAPIUser1.createBlogEntry(publicCommunity, baseBlogPost);
		
		// Verify that the blog entry was created successfully
		assert blogPost != null : "ERROR: There was a problem with creating the community blog entry using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now edit the description of the blog entry");
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		blogPost = communityBlogsAPIUser1.editDescription(blogPost, editedDescription);
		
		// Verify that the blog entry description was updated successfully
		assert blogPost.getContent().equals(baseBlogPost.getContent()) == false : "ERROR: The description of the blog entry was NOT changed using the API";
		assert blogPost.getContent().equals(editedDescription) == true : "ERROR: The description of the updated blog entry did NOT match the edited description sent to the API method";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_CreateBlogPostComment() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a blog entry for the community blog");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = communityBlogsAPIUser1.createBlogEntry(publicCommunity, baseBlogPost);
		
		// Verify that the blog entry was created successfully
		assert blogPost != null : "ERROR: There was a problem with creating the community blog entry using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a comment on the blog entry");
		BaseBlogComment baseBlogComment = buildBaseBlogComment();
		BlogComment blogComment = new BlogComment(baseBlogComment.getContent(), blogPost.toEntry());
		BlogComment postedBlogComment = communityBlogsAPIUser1.createComment(blogPost, blogComment);
		
		// Verify that the comment was successfully posted to the blog
		assert postedBlogComment != null : "ERROR: There was a problem with creating the blog comment using the API";
		assert postedBlogComment.getContent().equals(baseBlogComment.getContent()) == true : "ERROR: The content of the blog comment did NOT match the content set in its base instance";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_LikeBlogPost() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a blog entry for the community blog");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = communityBlogsAPIUser1.createBlogEntry(publicCommunity, baseBlogPost);
		
		// Verify that the blog entry was created successfully
		assert blogPost != null : "ERROR: There was a problem with creating the community blog entry using the API";
				
		log.info("INFO: " + testUser1.getDisplayName() + " will now like the blog entry");
		blogPost = communityBlogsAPIUser1.likeOrVote(blogPost);
		
		// Verify that the like operation completed successfully
		assert blogPost != null : "ERROR: There was a problem with liking / recommending the blog entry using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_UnlikeBlogPost() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a blog entry for the community blog");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = communityBlogsAPIUser1.createBlogEntry(publicCommunity, baseBlogPost);
		
		// Verify that the blog entry was created successfully
		assert blogPost != null : "ERROR: There was a problem with creating the community blog entry using the API";
				
		log.info("INFO: " + testUser1.getDisplayName() + " will now like the blog entry");
		blogPost = communityBlogsAPIUser1.likeOrVote(blogPost);
		
		// Verify that the like operation completed successfully
		assert blogPost != null : "ERROR: There was a problem with liking / recommending the blog entry using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now unlike the blog entry");
		boolean unlikedEntry = communityBlogsAPIUser1.unlikeOrRemoveVote(blogPost);
		
		// Verify that the unlike operation completed successfully
		assert unlikedEntry == true : "ERROR: There was a problem with unliking the blog entry using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_LikeBlogPostComment() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a blog entry for the community blog");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = communityBlogsAPIUser1.createBlogEntry(publicCommunity, baseBlogPost);
		
		// Verify that the blog entry was created successfully
		assert blogPost != null : "ERROR: There was a problem with creating the community blog entry using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a comment on the blog entry");
		BaseBlogComment baseBlogComment = buildBaseBlogComment();
		BlogComment blogComment = new BlogComment(baseBlogComment.getContent(), blogPost.toEntry());
		BlogComment postedBlogComment = communityBlogsAPIUser1.createComment(blogPost, blogComment);
		
		// Verify that the comment was successfully posted to the blog
		assert postedBlogComment != null : "ERROR: There was a problem with creating the blog comment using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now like the blog comment");
		postedBlogComment = communityBlogsAPIUser1.likeComment(postedBlogComment);
		
		// Verify that the like operation completed successfully
		assert postedBlogComment != null : "ERROR: There was a problem with liking / recommending the blog comment using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_CreateIdeationBlogIdea() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with ideation blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create an ideation blog idea");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = communityBlogsAPIUser1.createIdeationBlogIdea(publicCommunity, baseBlogPost);
		
		// Verify that the idea was created successfully
		assert idea != null : "ERROR: There was a problem with creating an ideation blog idea using the API";
		assert idea.getTitle().equals(baseBlogPost.getTitle()) == true : "ERROR: The title of the created idea was different from the title set in its base instance";
		assert idea.getContent().equals(baseBlogPost.getContent()) == true : "ERROR: The content of the created idea was different from the content set in its base instance";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_EditIdeationBlogIdeaDescription() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with ideation blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create an ideation blog idea");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = communityBlogsAPIUser1.createIdeationBlogIdea(publicCommunity, baseBlogPost);
		
		// Verify that the idea was created successfully
		assert idea != null : "ERROR: There was a problem with creating an ideation blog idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now edit the description of the ideation blog idea");
		String oldIdeaDescription = baseBlogPost.getContent().trim();
		String newIdeaDescription = Data.getData().commonDescription + Helper.genStrongRand();
		idea = communityBlogsAPIUser1.editDescription(idea, newIdeaDescription);
		
		// Verify that the idea description has been updated successfully
		assert idea.getContent().trim().equals(oldIdeaDescription) == false : "ERROR: The description for the idea was returned with the old / existing description using the API";
		assert idea.getContent().trim().equals(newIdeaDescription) == true : "ERROR: The description for the idea did NOT update to the new description as expected using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_VoteForIdea() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with ideation blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create an ideation blog idea");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = communityBlogsAPIUser1.createIdeationBlogIdea(publicCommunity, baseBlogPost);
		
		// Verify that the idea was created successfully
		assert idea != null : "ERROR: There was a problem with creating an ideation blog idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now vote for the idea");
		idea = communityBlogsAPIUser1.likeOrVote(idea);
		
		// Verify that the idea was voted for successfully
		assert idea != null : "ERROR: There was a problem with voting for the ideation blog idea using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_RemoveVoteOnIdea() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with ideation blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create an ideation blog idea");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = communityBlogsAPIUser1.createIdeationBlogIdea(publicCommunity, baseBlogPost);
		
		// Verify that the idea was created successfully
		assert idea != null : "ERROR: There was a problem with creating an ideation blog idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now vote for the idea");
		idea = communityBlogsAPIUser1.likeOrVote(idea);
		
		// Verify that the idea was voted for successfully
		assert idea != null : "ERROR: There was a problem with voting for the ideation blog idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now remove their vote on the idea");
		boolean removedVote = communityBlogsAPIUser1.unlikeOrRemoveVote(idea);
		
		// Verify that the vote was removed successfully
		assert removedVote == true : "ERROR: There was a problem with removing a vote from the ideation blog idea using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_CreateIdeaComment() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with ideation blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create an ideation blog idea");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = communityBlogsAPIUser1.createIdeationBlogIdea(publicCommunity, baseBlogPost);
		
		// Verify that the idea was created successfully
		assert idea != null : "ERROR: There was a problem with creating an ideation blog idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now post a comment to the idea");
		BaseBlogComment baseBlogComment = buildBaseBlogComment();
		BlogComment blogComment = new BlogComment(baseBlogComment.getContent(), idea.toEntry());
		BlogComment ideaComment = communityBlogsAPIUser1.createComment(idea, blogComment);
		
		// Verify that the comment was successfully posted to the idea
		assert ideaComment != null : "ERROR: There was a problem with posting a comment to an ideation blog idea using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_EditIdeaComment() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with ideation blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create an ideation blog idea");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = communityBlogsAPIUser1.createIdeationBlogIdea(publicCommunity, baseBlogPost);
		
		// Verify that the idea was created successfully
		assert idea != null : "ERROR: There was a problem with creating an ideation blog idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now post a comment to the idea");
		BaseBlogComment baseBlogComment = buildBaseBlogComment();
		BlogComment blogComment = new BlogComment(baseBlogComment.getContent(), idea.toEntry());
		BlogComment ideaComment = communityBlogsAPIUser1.createComment(idea, blogComment);
		
		// Verify that the comment was successfully posted to the idea
		assert ideaComment != null : "ERROR: There was a problem with posting a comment to an ideation blog idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now edit the content of the idea comment");
		BaseBlogComment editedBaseBlogComment = buildBaseBlogComment();
		ideaComment = communityBlogsAPIUser1.editComment(ideaComment, editedBaseBlogComment);
		
		// Verify that the comment was updated successfully
		assert ideaComment.getContent().trim().equals(baseBlogComment.getContent().trim()) == false : "ERROR: The comment posted to the idea was returned with the old / existing comment content using the API";
		assert ideaComment.getContent().trim().equals(editedBaseBlogComment.getContent().trim()) == true : "ERROR: The comment posted to the idea did NOT update to the new comment content as expected using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_LikeIdeaComment() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with ideation blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create an ideation blog idea");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = communityBlogsAPIUser1.createIdeationBlogIdea(publicCommunity, baseBlogPost);
		
		// Verify that the idea was created successfully
		assert idea != null : "ERROR: There was a problem with creating an ideation blog idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now post a comment to the idea");
		BaseBlogComment baseBlogComment = buildBaseBlogComment();
		BlogComment blogComment = new BlogComment(baseBlogComment.getContent(), idea.toEntry());
		BlogComment ideaComment = communityBlogsAPIUser1.createComment(idea, blogComment);
		
		// Verify that the comment was successfully posted to the idea
		assert ideaComment != null : "ERROR: There was a problem with posting a comment to an ideation blog idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now like the comment");
		ideaComment = communityBlogsAPIUser1.likeComment(ideaComment);
		
		// Verify that the like operation was successful
		assert ideaComment != null : "ERROR: There was a problem with liking / recommending a comment posted to an ideation blog idea using the API";
		
		log.info("INFO: Perform clean-up now that the test has completed");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_CreateIdeationBlogIdeaWithMentions(){
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with ideation blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create the mentions to " + testUser2.getDisplayName());
		String beforeMentionsText = Helper.genStrongRand();
		String afterMentionsText = Helper.genStrongRand();
		Mentions mentions = buildMentions(testUser2, testUser2Profile.getUUID(), beforeMentionsText, afterMentionsText);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create an ideation blog idea with mentions");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost ideaWithMentions = communityBlogsAPIUser1.createIdeationBlogIdeaWithMentions(baseBlogPost, publicCommunity, mentions);

		assert ideaWithMentions != null: "ERROR: There was a problem with creating an ideation blog idea with mentions using the API";
		assert ideaWithMentions.getTitle().equals(baseBlogPost.getTitle()) == true : "ERROR: The title of the created idea was different from the title set in its base instance";
		assert ideaWithMentions.getContent().contains(beforeMentionsText) == true : "ERROR: The content of the created blog entry did not contain the before mentions text";
		assert ideaWithMentions.getContent().contains("@" + testUser2.getDisplayName()) == true : "ERROR: The content of the created blog entry did not contain the mentions to " + testUser2.getDisplayName();
		assert ideaWithMentions.getContent().contains(afterMentionsText) == true : "ERROR: The content of the created blog entry did not contain the after mentions text";
		
		log.info("INFO: API test completed - cleaning up");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_CreateCommentWithMentions_Blog() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with ideation blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a blog entry for the community blog");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = communityBlogsAPIUser1.createBlogEntry(publicCommunity, baseBlogPost);
		
		// Verify that the blog entry was created successfully
		assert blogPost != null : "ERROR: There was a problem with creating the community blog entry using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create the mentions to " + testUser2.getDisplayName());
		String beforeMentionsText = Helper.genStrongRand();
		String afterMentionsText = Helper.genStrongRand();
		Mentions mentions = buildMentions(testUser2, testUser2Profile.getUUID(), beforeMentionsText, afterMentionsText);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now post a comment with mentions to " + testUser2.getDisplayName());
		BlogComment blogComment = communityBlogsAPIUser1.createCommentWithMentions(blogPost, mentions);
		
		assert blogComment != null : "ERROR: There was a problem with posting a comment with mentions to a community blog entry using the API";
		assert blogComment.getContent().contains(beforeMentionsText) == true : "ERROR: The before mentions text was not set in the content of the comment with mentions";
		assert blogComment.getContent().contains("@" + testUser2.getDisplayName()) == true : "ERROR: The mentions to " + testUser2.getDisplayName() + " was not included in the content of the comment with mentions";
		assert blogComment.getContent().contains(afterMentionsText) == true : "ERROR: The after mentions text was not set in the content of the comment with mentions";
		
		log.info("INFO: API test completed - cleaning up");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_CreateCommentWithMentions_IdeationBlog() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with ideation blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create an ideation blog idea");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = communityBlogsAPIUser1.createIdeationBlogIdea(publicCommunity, baseBlogPost);
		
		// Verify that the idea was created successfully
		assert idea != null : "ERROR: There was a problem with creating an ideation blog idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create the mentions to " + testUser2.getDisplayName());
		String beforeMentionsText = Helper.genStrongRand();
		String afterMentionsText = Helper.genStrongRand();
		Mentions mentions = buildMentions(testUser2, testUser2Profile.getUUID(), beforeMentionsText, afterMentionsText);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now post a comment with mentions to " + testUser2.getDisplayName());
		BlogComment blogComment = communityBlogsAPIUser1.createCommentWithMentions(idea, mentions);
		
		assert blogComment != null : "ERROR: There was a problem with posting a comment with mentions to a community ideation blog idea using the API";
		assert blogComment.getContent().contains(beforeMentionsText) == true : "ERROR: The before mentions text was not set in the content of the comment with mentions";
		assert blogComment.getContent().contains("@" + testUser2.getDisplayName()) == true : "ERROR: The mentions to " + testUser2.getDisplayName() + " was not included in the content of the comment with mentions";
		assert blogComment.getContent().contains(afterMentionsText) == true : "ERROR: The after mentions text was not set in the content of the comment with mentions";
		
		log.info("INFO: API test completed - cleaning up");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_DeleteComment_BlogPost() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a blog entry for the community blog");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = communityBlogsAPIUser1.createBlogEntry(publicCommunity, baseBlogPost);
		
		// Verify that the blog entry was created successfully
		assert blogPost != null : "ERROR: There was a problem with creating the community blog entry using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a comment on the blog entry");
		BaseBlogComment baseBlogComment = buildBaseBlogComment();
		BlogComment blogComment = new BlogComment(baseBlogComment.getContent(), blogPost.toEntry());
		BlogComment postedBlogComment = communityBlogsAPIUser1.createComment(blogPost, blogComment);
		
		// Verify that the comment was successfully posted to the blog
		assert postedBlogComment != null : "ERROR: There was a problem with creating the blog comment using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now delete the comment posted to the blog entry using the API");
		boolean deletedComment = communityBlogsAPIUser1.deleteComment(postedBlogComment);
		
		// Verify that the comment was deleted successfully
		assert deletedComment == true : "ERROR: There was a problem with deleting the comment posted to the blog post using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_DeleteComment_Idea() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with ideation blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.IDEATION_BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create an ideation blog idea");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost idea = communityBlogsAPIUser1.createIdeationBlogIdea(publicCommunity, baseBlogPost);
		
		// Verify that the idea was created successfully
		assert idea != null : "ERROR: There was a problem with creating an ideation blog idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now post a comment to the idea");
		BaseBlogComment baseBlogComment = buildBaseBlogComment();
		BlogComment blogComment = new BlogComment(baseBlogComment.getContent(), idea.toEntry());
		BlogComment ideaComment = communityBlogsAPIUser1.createComment(idea, blogComment);
		
		// Verify that the comment was successfully posted to the idea
		assert ideaComment != null : "ERROR: There was a problem with posting a comment to an idea using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now delete the comment posted to the idea using the API");
		boolean deletedComment = communityBlogsAPIUser1.deleteComment(ideaComment);
		
		// Verify that the comment was deleted successfully
		assert deletedComment == true : "ERROR: There was a problem with deleting the comment posted to the idea using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void testAPI_NotifyUserAboutBlogEntry() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		// Create a public community with blogs widget added
		Community publicCommunity = executeCommonCreatePublicCommunitySteps(testName, BaseWidget.BLOG);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a blog entry for the community blog");
		BaseBlogPost baseBlogPost = buildBaseBlogPost(testName + Helper.genStrongRand());
		BlogPost blogPost = communityBlogsAPIUser1.createBlogEntry(publicCommunity, baseBlogPost);
		
		// Verify that the blog entry was created successfully
		assert blogPost != null : "ERROR: There was a problem with creating the community blog entry using the API";
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now notify " + testUser2.getDisplayName() + " about the blog entry");
		boolean notificationSent = communityBlogsAPIUser1.notifyUserAboutBlogEntry(blogPost, testUser2Profile);
		
		// Verify that the notification was sent successfully
		assert notificationSent == true : "ERROR: There was a problem with notifying another user about the blog entry using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communityBlogsAPIUser1.deleteCommunity(publicCommunity);
	}
	
	/**
	 * Performs the common steps required in some of the unit tests in this class
	 * 
	 * Step: Creates a new public community
	 * Step: Adds the Blogs widget to the community
	 * 
	 * @param testName - The current name of the unit test being executed
	 * @return - The Community instance of the created community
	 */
	private Community executeCommonCreatePublicCommunitySteps(String testName, BaseWidget baseWidget) {
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now create a public community");
		BaseCommunity baseCommunity = buildBasePublicCommunity(testName + Helper.genStrongRand());
		Community publicCommunity = communityBlogsAPIUser1.createCommunity(baseCommunity);
		
		log.info("INFO: " + testUser1.getDisplayName() + " will now add the blogs widget to the community");
		communityBlogsAPIUser1.addWidget(publicCommunity, baseWidget);
		
		return publicCommunity;
	}
	
	/**
	 * Creates a BaseCommunity instance of a public community to be created
	 * 
	 * @param communityName - The name to be given to the community
	 * @return - The BaseCommunity instance of the community
	 */
	private BaseCommunity buildBasePublicCommunity(String communityName) {
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(communityName)
														.access(Access.PUBLIC)
														.tags(Data.getData().commonTag + Helper.genStrongRand())
														.description(Data.getData().commonDescription + Helper.genStrongRand())
														.build();
		return baseCommunity;
	}
	
	/**
	 * Creates a BaseBlogPost instance of a blog entry to be created
	 * 
	 * @param blogPostName - The name to be given to the blog entry
	 * @return - The BaseBlogPost instance of the blog entry
	 */
	private BaseBlogPost buildBaseBlogPost(String blogPostName) {
		
		BaseBlogPost baseBlogPost = new BaseBlogPost.Builder(blogPostName)
													.tags(Data.getData().commonTag + Helper.genStrongRand())
													.content(Data.getData().commonDescription + Helper.genStrongRand())
													.allowComments(true)
													.numDaysCommentsAllowed(5)
													.complete(true)
													.build();
		return baseBlogPost;		
	}
	
	/**
	 * Creates a BaseBlogComment instance of a blog comment to be created
	 * 
	 * @return - The BaseBlogComment instance of the comment
	 */
	private BaseBlogComment buildBaseBlogComment() {
		
		BaseBlogComment baseBlogComment = new BaseBlogComment.Builder(Data.getData().commonComment + Helper.genStrongRand())
	     													 .build();
		return baseBlogComment;
	}
	
	/**
	 * Creates a Mentions instance of a user to be mentioned
	 * 
	 * @param userBeingMentioned - The User instance of the user to be mentioned
	 * @param userUUID - The UUID of the user to be mentioned
	 * @param beforeMentionsText - The text to appear before the mentions text
	 * @param afterMentionsText - The text to appear after the mentions text
	 * @return - The Mentions instance of the user to be mentioned
	 */
	private Mentions buildMentions(User userBeingMentioned, String userUUID, String beforeMentionsText, String afterMentionsText) {
		Mentions mentions = new Mentions.Builder(userBeingMentioned, userUUID)
										.browserURL(serverURL)
										.beforeMentionText(beforeMentionsText)
										.afterMentionText(afterMentionsText)
										.build();
		return mentions;
	}
}
