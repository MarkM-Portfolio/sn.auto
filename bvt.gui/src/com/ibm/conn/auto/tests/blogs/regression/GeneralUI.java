package com.ibm.conn.auto.tests.blogs.regression;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseBlog;
import com.ibm.conn.auto.appobjects.base.BaseBlog.Theme;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIBlogsHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.Blog;

public class GeneralUI extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(GeneralUI.class);
	private TestConfigCustom cfg;
	private BlogsUI ui;
	private User testUser1, testUser2;
	private APIBlogsHandler apiOwner;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APIBlogsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = BlogsUI.getGui(cfg.getProductName(), driver);
	}
	
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests that one can sort the entries in Public Blogs by 'Likes/Votes' and can also retrieve all entries that have been liked or voted by the user.
	 *<li><B>Step: </B>Create a blog and then create a post in that blog.
	 *<li><B>Step: </B>Open the Blogs component and login.
	 *<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	 *<li><B>Verify: </B>It is possible to sort the blog entries by 'Likes/Votes'.
	 *<li><B>Verify: </B>'My Likes/Votes' is one of the entries in the Public Blogs toolbar.
	 *<li><B>Step: </B>Click on the 'My Likes/Votes' link.
	 *<li><B>Verify: </B>The title of the page that is opened is 'My Likes/Votes'.
	 *</ul>
	 */
	@Test(groups = {"regression"} )
	public void likesLabels() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
											.tags(Data.getData().commonTag + rand)
											.description(Data.getData().commonDescription)
											.theme(Theme.Blog_with_Bookmarks)
											.build();
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT blogEntry"+ rand)
												.blogParent(blog)
												.tags(Data.getData().commonAddress + rand)
												.content(Data.getData().commonDescription)
												.build();		

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		Blog blogAPI = blog.createAPI(apiOwner);
		
		logger.strongStep("Create blog post using API");
		log.info("INFO: Create blog post for blog");
		blogPost.createAPI(apiOwner, blogAPI);
		
		//GUI
		//Load the component and ui.login
		logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		//Click on the Public Blogs tab
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		//Verify that it is possible to sort the blog entries by Likes/Votes
		logger.strongStep("Verify 'Likes/Votes' appears as one of the ways in which the entries can be sorted");
		log.info("INFO: Validate that it is possible to sort the blog entries by Likes/Votes");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsPublicSortByLikes),
						  "ERROR: unable to find link to sort public blogs by Likes/Votes");
		
		//Verify that "My Likes/Votes" is one of the entries in the Public Blogs toolbar
		logger.strongStep("Verify 'My Likes/Votes' is one of the entries in the Public Blogs toolbar");
		log.info("INFO: Validate that 'My Likes/Votes' is one of the entries in the Public Blogs toolbar");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsPublicLikes),
						  "ERROR: unable to find My Likes/Votes link in public blogs toolbar");
		
		//Click "My Likes/Votes"
		logger.strongStep("Click on the 'My Likes/Votes' link");
		log.info("INFO: Select the link 'My Likes/Votes'");
		ui.clickLinkWait(BlogsUIConstants.BlogsPublicLikes);
		
		//Verify that the title of the current page is "My Likes/Votes"
		logger.strongStep("Verify the title of the page that is opened is 'My Likes/Votes'");
		log.info("INFO: Validate that the title of current page is 'My Likes/Votes'");
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.PublicBlogsMyLikesVotesHeader).getText(), Data.getData().like_votePageTitle,
							"ERROR: The title of the current page is not 'My Likes/Votes'");
		
		ui.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests the like and unlike functions of the likes widget.
	 *<li><B>Step: </B>Open Blogs and login as testUser1.
	 *<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	 *<li><B>Verify: </B>Verify that the number of likes does not appear if the entry hasn't been liked yet.
	 *<li><B>Step: </B>Like the entry.
	 *<li><B>Verify: </B>Verify that the number of likes changed to one.
	 *<li><B>Verify: </B>Verify that the Like widget displays that the current user has liked this entry.
	 *<li><B>Step: </B>Logout and Login to testUser2.
	 *<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	 *<li><B>Step: </B>Click on the blog that the previous user created.
	 *<li><B>Verify: </B>The widget is displayed -- smiley bubble and number of likes, and the like button.
	 *<li><B>Step: </B>Click on the Likes widget.
	 *<li><B>Verify: </B>The previous user who created and liked the entry appears in the list of users who liked this entry.
	 *<li><B>Step: </B>Get the number of likes for this entry.
	 *<li><B>Step: </B>Like the entry.
	 *<li><B>Step: </B>Click on the Likes widget to open popup.
	 *<li><B>Verify: </B>Both users appear in the list of users who like this entry.
	 *<li><B>Step: </B>Unlike the entry.
	 *<li><B>Step: </B>Click on the Likes widget to open popup.
	 *<li><B>Verify: </B>The current user who unliked this entry no longer appears in list of users who like the entry.
	 *<li><B>Verify: </B>The previous user still appears in the list of users who like this entry.
	 *<li><B>Verify: </B>The number of likes was decremented.
	 *<li><B>Step: </B>Logout of the session but let the browser stay open.
	 *<li><B>Verify: </B>The Log In button appears after logging out.
	 *<li><B>Step: </B>Click on Public Blogs link to navigate to Public Blogs tab.
	 *<li><B>Step: </B>Click on the blog that the previous user created.
	 *<li><B>Verify: </B>The Likes widget is present.
	 *</ul>
	 */
	@Deprecated //This test case has been disabled and deprecated because the bug https://jira.cwp.pnp-hcl.com/browse/BLOG-11 has not been fixed yet.
	@Test(groups = {"regression"}, enabled=false)
	public void likesWidget() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
											.tags(Data.getData().commonTag + rand)
											.description(Data.getData().commonDescription)
											.theme(Theme.Blog_with_Bookmarks)
											.build();
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT blogEntry"+ Helper.genDateBasedRandVal())
												.blogParent(blog)
												.tags(Data.getData().commonAddress + Helper.genDateBasedRandVal())
												.content(Data.getData().commonDescription)
												.build();		

		logger.strongStep("Create blog using API");
		log.info("INFO: Create blog using API");
		Blog blogAPI = blog.createAPI(apiOwner);
		
		logger.strongStep("Create blog post using API");
		log.info("INFO: Create blog post for blog");
		blogPost.createAPI(apiOwner, blogAPI);
		
		//GUI
		//Load the component
		logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		logger.strongStep("Click on the link for the blog created previously");
		log.info("INFO: Select blog " + blog.getName());
		ui.clickLinkWait("link=" + blog.getName());
		
		//Verify that the number of likes does not appear if the entry hasn't been liked yet
		logger.strongStep("Verify the like count does not appear since the entry hasn't been liked yet");
		log.info("INFO: Validate that the number of likes does not appear as the entry hasn't been liked yet");
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.BlogsEntryLikeCount).getText(), " ",
							"ERROR: Number of likes does not equal ' '");
		
		//Like the entry
		logger.strongStep("Click on the Like button to like the entry");
		log.info("INFO: Like the Entry");
		ui.clickLinkWait(BlogsUIConstants.BlogsEntryLike);

		
		//Verify that the number of likes changed to one
		logger.strongStep("Verify that the like count is now 1");
		log.info("INFO: Validate that the number of likes changed to one");
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.BlogsEntryLikeCount).getText(), "1",
							"ERROR: The number of likes does not equal 1");	
		
		//Verify that the Like widget displays that the current user has liked this entry
		logger.strongStep("Confirm that the Like widget reads that the current user has liked this entry");
		log.info("INFO: Validate that the Like widget displays that the current user has liked this entry");
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.BlogsEntryLikeDescription).getText(), Data.getData().LikeMessage,
							"ERROR: The blog entry does not display the message that the user likes it");
		
		//Logout
		logger.strongStep("Log out of and close the session");
		ui.logout();
		driver.quit();
		
		//Load the component
		logger.strongStep("Open Blogs and login: " +testUser2.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser2);
		
		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
				
		//Go to Public Blogs
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select the Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		//Click on the blog that the previous user created
		logger.strongStep("Click on the link for the blog post created by the previous user");
		log.info("INFO: Select the previous users blog post");
		ui.clickLinkWait(BlogsUI.getBlogPost(blogPost));

		//Verify that the widget is displayed -- smiley bubble and number of likes, and the like button
		logger.strongStep("Verify the Likes widget is available");
		log.info("INFO: Validate likes widget is present");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEntryLikesWidget),
						  "ERROR: likes widget was not present in entry details view");
		
		//Click on the Likes widget
		logger.strongStep("Click on the heart shaped icon to get the list of people who have liked the Blog");
		log.info("INFO: Select the heart shaped icon to get the list of people who have liked the Blog");
		ui.clickLinkWait(BlogsUIConstants.BlogsEntryLikeCount);
		
		//Verify that the user who created and liked the entry appears in the list of users who liked this entry
		logger.strongStep("Verify that the user who created and liked the entry previously appears in the list of users who liked this entry");
		log.info("INFO: Verify that the user who created and liked the entry previously appears in the list of users who liked this entry");
		Assert.assertTrue(driver.isElementPresent((BlogsUIConstants.BlogsEntryLikePopup + " a:contains(" + testUser1.getDisplayName() + ")")),
						  "ERROR: [" + testUser1.getDisplayName() + "] who liked the entry was not found in the Like widget");
		
		//Get the number of likes for this entry
		int oldNumberOfLikes = Integer.parseInt(driver.getSingleElement(BlogsUIConstants.BlogsEntryLikeCount).getText());
		
		//Like the entry
		logger.strongStep("Click on the Like button");
		log.info("INFO: Like the entry by clicking on the Like button");
		ui.clickLinkWait(BlogsUIConstants.BlogsEntryLike);
		
		//Click on the Likes widget to open popup
		logger.strongStep("Click on the heart shaped icon to get the list of people who have liked the Blog");
		log.info("INFO: Select the heart shaped icon to get the list of people who have liked the Blog");
		ui.clickLinkWait(BlogsUIConstants.BlogsEntryLikeCount);
		
		//Verify that both users appear in the list of users who like this entry
		logger.strongStep("Verify that the previous as well as the current users appear in the list of users who liked this entry");
		log.info("INFO: Verify that the previous as well as the current users appear in the list of users who liked this entry");
		Assert.assertTrue(driver.isElementPresent((BlogsUIConstants.BlogsEntryLikePopup + " a:contains(" + testUser1.getDisplayName() + ")")),
						 "ERROR: [" + testUser1.getDisplayName() + "] who liked the entry was not found in the Like widget");
		Assert.assertTrue(driver.isElementPresent((BlogsUIConstants.BlogsEntryLikePopup + " a:contains(" + testUser2.getDisplayName() + ")")),
						 "ERROR: [" + testUser2.getDisplayName() + "] who liked the entry was not found in the Like widget");
		
		//Verify that the number of likes was incremented
		int newNumberOfLikes = Integer.parseInt(driver.getSingleElement(BlogsUIConstants.BlogsEntryLikeCount).getText());
		int actualNumberOfLikes = newNumberOfLikes;
		int expectedNumberOfLikes = oldNumberOfLikes+1;
		
		logger.strongStep("Verify the number of likes is equal to " + expectedNumberOfLikes);
		log.info("INFO: Verify the number of likes is equal to " + expectedNumberOfLikes);
		Assert.assertEquals(actualNumberOfLikes, expectedNumberOfLikes);

		//Unlike the entry
		logger.strongStep("Click on the Unlike button");
		log.info("INFO: Click on the Unlike button");
		ui.clickLinkWait(BlogsUIConstants.BlogsEntryUnlike);
		
		//Click on the Likes widget to open popup
		logger.strongStep("Click on the heart shaped icon to get the list of people who have liked the Blog");
		log.info("INFO: Select the heart shaped icon to get the list of people who have liked the Blog");
		ui.clickLinkWait(BlogsUIConstants.BlogsEntryLikeCount);

		//Verify that the user who unliked this entry no longer appears in list of users who like the entry
		logger.strongStep("Verify that the current user does not appear in the list of users who liked this entry");
		log.info("INFO: Verify that the current user does not appear in the list of users who liked this entry");
		Assert.assertFalse(driver.isElementPresent((BlogsUIConstants.BlogsEntryLikePopup + " a:contains(" + testUser2.getDisplayName() + ")")),
						  "ERROR: the user [" + testUser2.getDisplayName() + "] who unliked the entry still appears in the Like widget list");
		
		//Verify that the other user still appears in the list of users who like this entry
		logger.strongStep("Verify that the previous user still appears in the list of users who liked this entry");
		log.info("INFO: Verify that the previous user still appears in the list of users who liked this entry");
		Assert.assertTrue(driver.isElementPresent((BlogsUIConstants.BlogsEntryLikePopup + " a:contains(" + testUser1.getDisplayName() + ")")),
						  "ERROR: the user [" + testUser1.getDisplayName() + "] who likes the entry does not appear in the Like widget list");
		
		//Verify that the number of likes was decremented
		newNumberOfLikes = Integer.parseInt(driver.getSingleElement(BlogsUIConstants.BlogsEntryLikeCount).getText());
		
		logger.strongStep("Verify the number of likes is equal to " + oldNumberOfLikes);
		log.info("INFO: Validate that the number of likes is equal to " + oldNumberOfLikes);
		Assert.assertTrue(newNumberOfLikes == oldNumberOfLikes, 
						 "ERROR: the number of likes was not decremented");
		
		//Logout
		logger.strongStep("Log out of the session");
		ui.logout();
		
		logger.strongStep("Confirm the presence of the Log In button");
		log.info("INFO: Verify the Log In button appears after logging out");
		ui.fluentWaitTextPresent("Log In");

		//Go to Public Blogs
		logger.strongStep("Click on Public Blogs link to navigate to Public Blogs tab");
		log.info("INFO: Select Public Blogs tab");
		ui.clickLinkWait(BlogsUIConstants.PublicBlogs);
		
		//Click on the blog that the previous user created
		logger.strongStep("Click on the link of the blog post");
		log.info("INFO: Select the blog post");
		ui.clickLinkWait(BlogsUI.getBlogPost(blogPost));
		
		//Verify that the Likes widget is present
		logger.strongStep("Verify the Likes widget is visible");
		log.info("INFO: Validate that the Likes widget is present");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsEntryLikesWidget),
						 "ERROR: The likes widget was not found");
		
		ui.endTest();
	}

	/**
	*<ul>
	*<li><B>Info: </B>Test for a comment to be added as the track back entry in blogs.
	*<li><B>Step: </B>Create a blog.
	*<li><B>Step: </B>Create a blog post for the blog.
	*<li><B>Step: </B>Create another blog.
	*<li><B>Step: </B>Open Blogs and login.
	*<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	*<li><B>Step: </B>Go to the first blog and then go to the blog post.
	*<li><B>Step: </B>Add a comment for the first blog.
	*<li><B>Step: </B>Select the checkbox 'Add this as a new entry/idea in my blog'.
	*<li><B>Verify: </B>The drop down contains both blogs.
	*<li><B>Step: </B>Select the second blog in the drop down menu.
	*<li><B>Step: </B>Submit comment.
	*<li><B>Verify: </B>The comment can be found in both blogs.
	*</ul>
	*/ 
	@Test(groups = {"regression"} )
	public void commentBlog() throws Exception {
	
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand().substring(1, 7);
		
		BaseBlog blog1 = new BaseBlog.Builder(testName + "1" + rand, testName + "1" + rand)
											.tags(Data.getData().commonTag + rand)
											.description(Data.getData().commonDescription)
											.theme(Theme.Blog_with_Bookmarks)
											.build();
		
		BaseBlog blog2 = new BaseBlog.Builder(testName + "2" + rand, testName + "2" + rand)
									 .tags(Data.getData().commonTag + Helper.genDateBasedRand())
									 .description(Data.getData().commonDescription)
									 .build();
	
		//create a blog entry base state object
		BaseBlogPost blogPost = new BaseBlogPost.Builder("blogEntry"+ rand)
											    .blogParent(blog1)
												.tags(Data.getData().commonAddress + rand)
												.content(Data.getData().commonDescription)
												.build();
		
		String BlogsComment = "This is the test for the comment to be added to the entry in blogs";
		
		logger.strongStep("Create a blog using API");
		log.info("INFO: Create blog 1 using API");
		Blog blogAPI = blog1.createAPI(apiOwner);
		
		logger.strongStep("Create a blog post for this blog");
		log.info("INFO: Create a blog post for blog 1");
		blogPost.createAPI(apiOwner, blogAPI);
		
		logger.strongStep("Create another blog using API");
		log.info("INFO: Create blog 2 using API");
		blog2.createAPI(apiOwner);
		
		//GUI
		//Load the component
		logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
	
		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		logger.strongStep("Click on the link for the first blog");
		log.info("INFO: Select blog " + blog1.getName());
		ui.clickLinkWait("link=" + blog1.getName());

		logger.strongStep("Click on the link for the blog post");
		log.info("INFO: Select blog post " + blogPost.getTitle());
		ui.clickLinkWait("link=" + blogPost.getTitle());

		//Create comment
		logger.strongStep("Click on Add a Comment link");
		log.info("INFO: Select Add a Comment link ");
		ui.clickLinkWait(BlogsUIConstants.BlogsAddACommentLink);
		
		logger.strongStep("Verify the comment text area appears");
		log.info("INFO: Wait for comment text area to be present");
		ui.fluentWaitPresent(BaseUIConstants.StatusUpdate_iFrame);
		
		logger.strongStep("Type " + BlogsComment + " in the comment text area");
		log.info("INFO: Type the comment into the text area");
		ui.typeInCkEditor(BlogsComment);

		logger.strongStep("Select the checkbox 'Add this as a new entry/idea in my blog'");
		log.info("INFO: Click on the checkbox to add track back to another blog");
		ui.clickLinkWait(BlogsUIConstants.BlogCommentTrackbackCheckBox);

		logger.strongStep("Click on the 'Select Blog:' drop down");
		log.info("INFO: Select the blogs comment track drop down menu");
		ui.clickLinkWait(BlogsUIConstants.BlogCommentTrackDropDown);
		
		//check drop down contains both blogs
		logger.strongStep("Verify that the drop contains two options - " + blog1.getName() + " and " + blog2.getName());
		log.info("INFO: Validate that the blog comment tracking drop down contains two options - " + blog1.getName() + " and " + blog2.getName());
		Assert.assertTrue(ui.dropdownContains(BlogsUIConstants.BlogCommentTrackDropDown, blog1.getName()),
						  "ERROR: Blog: " + blog1.getName() + " not in drop down list");
		Assert.assertTrue(ui.dropdownContains(BlogsUIConstants.BlogCommentTrackDropDown, blog2.getName()),
						  "ERROR: Blog: " + blog2.getName() + " not in drop down list");		

		//Select the second blog in the drop down menu
		logger.strongStep("Select the second blog in the drop down menu");
		log.info("INFO: Select the second blog in the drop down menu");
		driver.getSingleElement(BlogsUIConstants.BlogCommentTrackDropDown).useAsDropdown().selectOptionByValue(blog2.getBlogAddress());
		
		//submit comment
		logger.strongStep("Click on Submit button");
		log.info("INFO: Submit comment");
		ui.clickLink(BlogsUIConstants.BlogsSubmitButton);
		
		logger.strongStep("Validate that the comment appears");
		log.info("INFO: Validate that the comment is present");
		Assert.assertTrue(ui.fluentWaitTextPresent(BlogsComment),
						  "ERROR: Unable to find comment.");
		
		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		logger.strongStep("Click on the link for the first blog");
		log.info("INFO: Select the first blog");
		ui.clickLinkWait("link=" + blog1.getName());
		
		logger.strongStep("Click on the link for the blog post");
		log.info("INFO: Select the blog post");
		ui.clickLinkWait("link=" + blogPost.getTitle());
		
		logger.strongStep("Wait till the comment divider appears");
		log.info("INFO: Wait for comment divider to be present");
		ui.fluentWaitPresent(BlogsUIConstants.BlogsCommentDiv);
		
		logger.strongStep("Validate the comment text can be seen");
		log.info("INFO: Validate the comment text was found");
		Assert.assertTrue(driver.isTextPresent(BlogsComment), 
						  "ERROR: comment [" + BlogsComment + "] was not found in blog [" + blog1.getName() + "] when it was posted in [" + blog2.getName() + "]");
		
		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		logger.strongStep("Click on the link for the second blog");
		log.info("INFO: Select the second blog");
		ui.clickLinkWait("link=" + blog2.getName());
		
		logger.strongStep("Verify the track back entry is found in the blog");
		log.info("INFO: Validate track back entry was found");
		Assert.assertTrue(driver.isElementPresent("link=" + "Re: " + blogPost.getTitle()), 
						  "ERROR: The trackback entry was not found in blog [" + blog2.getName());
		
		logger.strongStep("Verify the comment is visible");
		log.info("INFO: Validate the comment appears");
		Assert.assertTrue(driver.isTextPresent(BlogsComment), 
						  "ERROR: comment [" + BlogsComment + "] was not found in blog [" + blog2.getName() + "] where it was posted to");
		
		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info: </B>Tests the creation of two blogs and addition of an entry to one of the blogs.
	*<li><B>Step: </B>Create 2 blogs.
	*<li><B>Step: </B>Click on My Blogs link to navigate to My Blogs tab.
	*<li><B>Step: </B>Click on the "Add Entry" link for the first blog. 
	*<li><B>Step: </B>Add an Entry.
	*<li><B>Step: </B>Go back to My Blogs.
	*<li><B>Verify: </B>Blog post does NOT exist in Blog 2.
	*<li><B>Step: </B>Go back to My Blogs.
	*<li><B>Verify: </B>The blog post exists in blog 1.
	*</ul>
	*/ 
	@Test(groups = {"regression"} )
	public void addEntryToBlog() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		
		BaseBlog blog1 = new BaseBlog.Builder(testName + rand, testName + rand)
											.tags(Data.getData().commonTag + rand)
											.description(Data.getData().commonDescription)
											.theme(Theme.Blog_with_Bookmarks)
											.build();	
		
		//Create a blog base state object
		BaseBlog blog2 = new BaseBlog.Builder("BVT Blog " + rand, Data.getData().BlogsAddress2 + rand)
									 .tags(Data.getData().commonTag + rand)
									 .description(Data.getData().commonDescription)
									 .build();
		
		//create a blog entry base state object
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT Moderation blogEntry" + rand)
												.blogParent(blog1)
												.tags(Data.getData().commonAddress + rand)
												.content(Data.getData().commonDescription)
												.build();

		logger.strongStep("Create the first blog using API");
		log.info("INFO: Create first blog using API");
		blog1.createAPI(apiOwner);
		
		logger.strongStep("Create the second blog using API");
		log.info("INFO: Create second blog using API");
		blog2.createAPI(apiOwner);
		
		//GUI
		//Load the component
		logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);
		
		//Go back to My Blogs
		logger.strongStep("Switch to My Blog tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Click on the "Add Entry" link for the first blog
		logger.strongStep("Click on the New Entry link for the first blog's post");
		log.info("INFO: Click on the New Entry link for the first blog's post");
		ui.clickLinkWait(BlogsUI.newEntryForSpecificBlog(blog1));
		
		//Add an Entry
		logger.strongStep("Create a new entry and submit");
		log.info("INFO: Add an Entry");
		blogPost.create(ui);
		
		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		logger.strongStep("Click on the second blog's link");
		ui.clickLinkWait("link=" + blog2.getName());
		Assert.assertFalse(driver.isTextPresent(blogPost.getTitle()), 
						   "ERROR: entry with name [" + blogPost.getTitle() + "] was found in blog [" + blog2.getName() + "] when it was posted in [" + blog1.getName() + "]");

		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		logger.strongStep("Click on the first blog's link");
		log.info("INFO: Click on the link for the first blog");
		ui.clickLinkWait("link=" + blog1.getName());

		logger.strongStep("Verify the entry: " + blogPost.getTitle() + " is found in the first blog");
		log.info("INFO: Verify the entry: " + blogPost.getTitle() + " is found in blog: " + blog1.getName());
		Assert.assertTrue(driver.isTextPresent(blogPost.getTitle()), 
						  "ERROR: entry with name [" + blogPost.getTitle() + "] was not found in blog [" + blog1.getName() + "] where it was posted");
		
		ui.endTest();
		
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if an invalid tag can be filtered out.
	 *<li><B>Step: </B>Navigate to blog.
	 *<li><B>Step: </B>Add an Entry.
	 *<li><B>Verify: </B>Verify that the invalid tag is in the tag cloud.
	 *<li><B>Step: </B>Click the invalid tag in the tag cloud.
	 *<li><B>Verify: </B>Verify the entry can be filtered out.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void invalidTagBlogPost() throws Exception{

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		String rand = Helper.genDateBasedRand();
		
		BaseBlog blog = new BaseBlog.Builder(testName + rand, testName + rand)
											.tags(Data.getData().commonTag + rand)
											.description(Data.getData().commonDescription)
											.theme(Theme.Blog_with_Bookmarks)
											.build();
		
		String invalidTag = "tag~@#$%^*)_+=-`{}|:\"<>?[]\\;'./";
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder("BVT blogEntry"+ Helper.genDateBasedRandVal()).blogParent(blog)
															.tags("tag~@#$%^*)_+=-`{}|:\"<>?[]\\;'./")
															.content(Data.getData().commonDescription).build();
		
		logger.strongStep("Create a blog using API");
		log.info("INFO: Create a blog using API");
		blog.createAPI(apiOwner);
		
		//GUI
		//Load the component
		logger.strongStep("Open Blogs and login: " +testUser1.getDisplayName());
		ui.loadComponent(Data.getData().ComponentBlogs);
		ui.login(testUser1);

		//Go back to My Blogs
		logger.strongStep("Click on My Blogs link to navigate to My Blogs tab");
		log.info("INFO: Navigate to My Blogs");
		ui.clickLinkWait(BlogsUIConstants.MyBlogs);
		
		//Navigate to blog
		logger.strongStep("Click on New Entry link for the current blog");
		log.info("INFO: Navigate to the blog & click New Entry");
		ui.clickLinkWait(BlogsUI.newEntryForSpecificBlog(blog));
		
		//Add an Entry
		logger.strongStep("Create a new entry and click on Post button");
		log.info("INFO: Create a new entry and submit");
		blogPost.create(ui);
		
		//Verify that the invalid tag is in the tag cloud
		logger.strongStep("Verify that there is a tag available in My Blogs toolbar when Cloud view is selected");
		log.info("INFO: Verify that there is a tag in My Blogs toolbar for tag cloud view");
		Assert.assertTrue(driver.isElementPresent(BlogsUIConstants.BlogsTagCloud),
						  "ERROR: invalid tag is not in the cloud");
		
		logger.strongStep("Verify the invalid tag is displayed as the tag cloud");
		log.info("INFO: Verify that the invalid tag is in the tag cloud");
		Assert.assertEquals(driver.getSingleElement(BlogsUIConstants.BlogsTagCloudView).getText(), invalidTag,
							"ERROR: invalid tag is not in the cloud");

		//Click the invalid tag in the tag cloud, verify the entry can be filtered out
		logger.strongStep("Click on the invalid tag in the tag cloud");
		log.info("INFO: Click the invalid tag in the tag cloud");
		ui.clickLinkWait(BlogsUIConstants.BlogsTagCloudView);
		
		logger.strongStep("Verify the blog post appears");
		log.info("INFO: Validate blog post is present");
		Assert.assertTrue(driver.isTextPresent(blogPost.getTitle()),
						  "ERROR: Unable to locate blog post");
		
		ui.endTest();
	}
	
}
