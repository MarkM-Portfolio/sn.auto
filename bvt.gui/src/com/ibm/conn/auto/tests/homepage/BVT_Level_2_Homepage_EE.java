package com.ibm.conn.auto.tests.homepage;

import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Homepage_EE extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Homepage_EE.class);
	private HomepageUI ui;
	private FilesUI filesUI;
	private TestConfigCustom cfg;	
	private String serverURL;
	private BaseFile file1; 
	private String homepageURI;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();

		file1 = new BaseFile.Builder(Data.getData().file1).build();		
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		homepageURI = Data.getData().ComponentHomepage.split("/")[0];
		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		filesUI = FilesUI.getGui(cfg.getProductName(), driver);
		
		ui.addOnLoginScript(ui.getCloseTourScript());
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Check Embedded Experience fields form for Blogs</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Step:</B>Add a blog</li>
	*<li><B>Step:</B>As a different user follow this blog</li>
	*<li><B>Step:</B>Login as the first user and then open the EE (homepage)</li>
	*<li><B>Verify:</B>Check that EE Popup opens validate the fields in the popup and that the read more link works</li>
	*</ul>
	*/
	@Test(groups = {"cplevel2", "level2", "bvt", "regressioncloud", "smoke"} )
	public void Blogs_EE_Basic() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//allocate users
		User	commFollower = cfg.getUserAllocator().getUser();
		User	commOwner = cfg.getUserAllocator().getUser();		
		User 	commMember = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, commOwner.getAttribute(cfg.getLoginPreference()), commOwner.getPassword());
		APICommunitiesHandler apiFollower = new APICommunitiesHandler(serverURL, commFollower.getAttribute(cfg.getLoginPreference()), commFollower.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
									.description(Data.getData().commonDescription)
									.addMember(new Member(CommunityRole.MEMBERS, commMember))
									.build();
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder(testName+ Helper.genDateBasedRandVal())
												.tags("testtag")
												.content("This is a test for EE test for blogs").build();	
		//create community
		logger.strongStep("Create a community using API");
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);

		log.info("INFO: Validate the presence of BLOG widget");
		if (apiOwner.getWidgetID(commAPI.getUuid(), "Blog").isEmpty()) {
			logger.strongStep("Add Blog widget");
			log.info("INFO: Add blog widget with api");
			community.addWidgetAPI(commAPI, apiOwner, BaseWidget.BLOG);
		}

		//Add Post to blog
		logger.strongStep("Add a blog post to community using API");
		BlogPost blogPostEntry = apiOwner.createBlogEntry(blogPost, apiOwner.getCommunity(commAPI));
		Assert.assertTrue(blogPostEntry != null, "Failed to create blog post in community using API.");
		
		//Follow community with a different user
		logger.strongStep("Follow community with a different user using API");
		log.info("INFO: Follow community using API");
		community.followAPI(commAPI, apiFollower, apiOwner);

		//GUI START
		//Load component and login with community follower
		logger.strongStep("Load homepage and login as a community follower");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(commFollower);
		
		//find feeds for these Entries
		logger.strongStep("Find feeds for the Entries");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		
		//Click on the dropdown and choose to filter with Blogs
		logger.strongStep("Click on the dropdown and filter by blogs");
		log.info("INFO: Filter by Blogs");
		ui.filterBy("Blogs");
			
		logger.strongStep("Select news story and open EE");
		log.info("INFO: Select news story and open EE ");
		log.info("INFO: Select " + blogPost.getTitle());
		ui.filterNewsItemOpenEE("created a blog entry named " + blogPost.getTitle() + " in the " + community.getName() + " blog.");

		//Verify the objects in the EE
		logger.weakStep("Validate the Embedded Experience");
		log.info("INFO: Validate the Embedded Experience");

		//Verify the EE widget
		logger.weakStep("Verify the embedded experience title");
		log.info("INFO: Verify the EE Title");
		ui.fluentWaitTextPresent(blogPost.getTitle());
		
		//Verify tag
		logger.weakStep("Verify the embedded experience tags");
		log.info("INFO: Verify the EE Tags");
		ui.fluentWaitTextPresent(blogPost.getTags());

		//Verify like
		logger.weakStep("Verify the embedded experience like");
		log.info("INFO: Verify the EE Like");
		ui.fluentWaitPresent(HomepageUIConstants.EELike);
		
		//Verify Description
		logger.weakStep("Verify the embedded experience description");
		log.info("INFO: Verify the EE Description");
		ui.fluentWaitTextPresent(blogPost.getContent());
		
		//Verify Read more
		logger.weakStep("Verify the embedded experience 'read more' link");
		log.info("INFO: Verify the EE Read More link");
		driver.executeScript("arguments[0].scrollIntoView(true);", 
				driver.getElements(HomepageUIConstants.EEReadmore).get(0).getWebElement());
		ui.fluentWaitPresent(HomepageUIConstants.EEReadmore);
		
		//Verify the Tab for Comments
		logger.weakStep("Verify the 'Embedded Experience' Tab comment");
		log.info("INFO: Verify the EE Comment Tab");
		ui.fluentWaitPresent(HomepageUIConstants.EECommentsTab);
		
		//Verify the Tab for Recent Updates
		logger.weakStep("Verify the EE 'Recent Updates' tab");
		log.info("INFO: Verify the EE Recent Updates Tab");
		ui.fluentWaitPresent(HomepageUIConstants.EEHistoryTab);
		
		//Click on Read more and verify that a new tab is opened
		logger.weakStep("Validate the 'Read More' Option");
		log.info("INFO: Validate Read more option");
		ui.clickLink(HomepageUIConstants.EEReadmore);
		
		//switch to new window
		logger.strongStep("Switch to a new window");
		log.info("INFO: Switch to new window");
		ui.switchToNewTabByName(community.getName());
		Assert.assertTrue(driver.isTextPresent(""));
	
		//close the new tab
		logger.strongStep("Close new window");
		log.info("INFO: Close new window");
		ui.close(cfg);

		//switch back to main window
		logger.strongStep("Switch back to main window");
		ui.switchToHomepageTab();

		logger.strongStep("Delete community that was created using API");
		apiOwner.deleteCommunity(commAPI);
		
		
		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info:</B>Check Embedded Experience Likes for Blogs</li>
	*<li><B>Step:</B>Create a community</li>
	*<li><B>Step:</B>Add a blog</li>
	*<li><B>Step:</B>As a different user follow this blog</li>
	*<li><B>Step:</B>Login as the first user</li>
	*<li><B>Step:</B>Open the EE (homepage)</li>
	*<li><B>Step:</B>Like and comment on this blog</li>
	*<li><B>Step:</B>Login as the second user</li>
	*<li><B>Step:</B>Open EE and like and then unlike</li>
	*<li><B>Verify:</B>Check that EE is working and the actions like, comments are working as expected</li>
	*</ul>
	*/
	@Test(groups = {"cplevel2", "level2", "bvt", "regressioncloud", "smoke"} )
	public void Blogs_EE_Likes() throws Exception {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = ui.startTest();
		
		//allocate users
		User	commFollower = cfg.getUserAllocator().getUser();
		User	commOwner = cfg.getUserAllocator().getUser();		
		User 	commMember = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, commOwner.getAttribute(cfg.getLoginPreference()), commOwner.getPassword());
		APICommunitiesHandler apiFollower = new APICommunitiesHandler(serverURL, commFollower.getAttribute(cfg.getLoginPreference()), commFollower.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
									.description(Data.getData().commonDescription)
									.addMember(new Member(CommunityRole.MEMBERS, commMember))
									.build();
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder(testName+ Helper.genDateBasedRandVal())
												.tags("testtag")
												.content("This is a test for EE test for blogs").build();	
		//create community
		logger.strongStep("Create a community using API");
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);

		log.info("INFO: Validate the presence of BLOG widget");
		if (apiOwner.getWidgetID(commAPI.getUuid(), "Blog").isEmpty()) {
			logger.strongStep("Add Blog widget");
			log.info("INFO: Add blog widget with api");
			community.addWidgetAPI(commAPI, apiOwner, BaseWidget.BLOG);
		}

		//Add Post to blog
		logger.strongStep("Add a blog post to the community using API");
		BlogPost blogPostEntry = apiOwner.createBlogEntry(blogPost, apiOwner.getCommunity(commAPI));
		Assert.assertTrue(blogPostEntry != null, "Failed to create blog post in community using API.");
		
		//Follow community with a different user
		logger.strongStep("Follow the community with a different user using API");
		log.info("INFO: Follow community using API");
		community.followAPI(commAPI, apiFollower, apiOwner);

		//GUI START
		//Load component and login with community follower
		logger.strongStep("Load homepage and login as a community follower");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(commFollower);
		
		//find feeds for these Entries
		logger.strongStep("Find feeds for these Entries");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		
		//Click on the dropdown and choose to filter with Blogs
		logger.strongStep("Click on the dropdown menu and choose to filter with blogs");
		log.info("INFO: Filter by Blogs");
		ui.filterBy("Blogs");
		
		logger.strongStep("Select news story and open EE");
		log.info("INFO: Select news story and open EE ");
		log.info("INFO: Select " + blogPost.getTitle());
		ui.filterNewsItemOpenEE("created a blog entry named " + blogPost.getTitle() + " in the " + community.getName() + " blog.");
			
		//Like EE 
		logger.strongStep("Like the Blog Post");
		log.info("INFO: Like the Blog Post");
		ui.clickLinkWait(HomepageUIConstants.EELike);
		//The click will sometimes not register, see Defect 171028
		if (!driver.isElementPresent(HomepageUIConstants.EELikeUndo)) {
			//Retry with Javasript click
			log.info("INFO: No change to Like link after cliking, retrying with JavaScript click");
			ui.clickLinkWithJavascript(HomepageUIConstants.EELike);
			ui.fluentWaitElementVisible(HomepageUIConstants.EELikeUndo);
		}
		ui.switchToTopFrame();	
		//Logout
		logger.strongStep("Logout");
		ui.logout();
		//ui.close(cfg); 	This line is commented to maintain single session in BS

		//Load component and login with community owner
		logger.strongStep("Load homepage and login with community owner");
		ui.loadComponent(Data.getData().HomepageImFollowing,true);
		ui.login(commOwner);
		
		//Click on the dropdown and choose to filter with Blogs
		logger.strongStep("Click on the dropdown meny and choose to filter with Blogs");
		log.info("INFO: Filter by Blogs");
		ui.filterBy("Blogs");
			
		//Open the EE for the news story
		logger.strongStep("Open the embedded experience for the news story");
		log.info("INFO: Select " + blogPost.getTitle());		
		ui.filterNewsItemOpenEE("liked your blog entry " + blogPost.getTitle() + " in the " + community.getName() + " blog.",
				"liked your " + blogPost.getTitle() + " blog entry in the " + community.getName() + " blog.");
						
		//Like the blog via EE 
		logger.strongStep("Like the blog post");
		log.info("INFO: Like the Blog Post via embedded experience");
		ui.clickLinkWait(HomepageUIConstants.EELike);
		//The click will sometimes not register, see Defect 171028
		if (!driver.isElementPresent(HomepageUIConstants.EELikeUndo)) {
			//Retry with Javasript click
			log.info("INFO: No change to Like link after cliking, retrying with JavaScript click");
			ui.clickLinkWithJavascript(HomepageUIConstants.EELike);
			ui.fluentWaitElementVisible(HomepageUIConstants.EELikeUndo);
		}
		
		//Click on the like count with correct value
		logger.strongStep("Select the 'like' count");
		log.info("INFO: Select like count " + 2);
		ui.clickLinkWait(HomepageUI.getLikeCountLink(2));   
		
		//Verify users who like blog
		logger.weakStep("Validate that 2 people like the blog");
		log.info("INFO: Validating people who like the blog.");
		Assert.assertTrue(ui.findPersonInLikeList(commFollower));		
		Assert.assertTrue(ui.findPersonInLikeList(commOwner));

		//Close the Like user popup
		logger.strongStep("Close the 'like' user popup");
		ui.clickLink(HomepageUIConstants.EEUserPopupClose);
		
		//Unlike the blog entry in EE and verify that the like appears again
		logger.strongStep("Unlike the blog");
		log.info("INFO: Unlike the blog");
		ui.clickLink(HomepageUIConstants.EELikeUndo);
		
		//Switch back to the main frame
		logger.strongStep("Switch back to the main frame");
		ui.switchToTopFrame();

		logger.strongStep("Delete the community that was created using API");
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info:</B>Check Embedded Experience Comments for Community Blogs</li>
	*<li><B>Step:</B>[API] Create a community</li>
	*<li><B>Step:</B>[API] Add a blog to the community</li>
	*<li><B>Step:</B>[API] Login as a second user follow the community</li>
	*<li><B>Step:</B>As second user, go to Updates in Homepage </li>
	*<li><B>Step:</B>From I'm Following tab, filter by Blogs</li>
	*<li><B>Step:</B>Open the EE for the Blog Entry and add a comment within the EE container</li>
	*<li><B>Step:</B>Login to Homepage as the community owner</li>
	**<li><B>Step:</B>Go to Updates and from I'm Following tab, filter by Blogs</li>
	*<li><B>Step:</B>Open EE for the comment</li>
	*<li><B>Verify:</B>Count for comment incremented by 1 as expected</li>
	*<li><B>Step:</B>[API] Delete the community</li>
	*</ul>
	*/
	@Test(groups = {"level2", "bvt", "regressioncloud", "smoke"} )
	public void Blogs_EE_Comments() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String testName = ui.startTest();
		
		//allocate users
		User	commFollower = cfg.getUserAllocator().getUser();
		User	commOwner = cfg.getUserAllocator().getUser();		
		User 	commMember = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, commOwner.getAttribute(cfg.getLoginPreference()), commOwner.getPassword());
		APICommunitiesHandler apiFollower = new APICommunitiesHandler(serverURL, commFollower.getAttribute(cfg.getLoginPreference()), commFollower.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
									.description(Data.getData().commonDescription)
									.addMember(new Member(CommunityRole.MEMBERS, commMember))
									.build();
		
		BaseBlogPost blogPost = new BaseBlogPost.Builder(testName+ Helper.genDateBasedRandVal())
												.tags("testtag")
												.content("This is a test for EE test for blogs").build();	
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);

		log.info("INFO: Validate the presence of BLOG widget");
		if (apiOwner.getWidgetID(commAPI.getUuid(), "Blog").isEmpty()) {
			logger.strongStep("Add Blog widget");
			log.info("INFO: Add blog widget with api");
			community.addWidgetAPI(commAPI, apiOwner, BaseWidget.BLOG);
		}

		//Add Post to blog
		logger.strongStep("Add blog post to community using API");
		BlogPost blogPostEntry = apiOwner.createBlogEntry(blogPost, apiOwner.getCommunity(commAPI));
		Assert.assertTrue(blogPostEntry != null, "Failed to create blog post in community using API.");
		
		//Follow community with a different user
		logger.strongStep("Follow community as a different user using API");
		log.info("INFO: Follow community using API");
		community.followAPI(commAPI, apiFollower, apiOwner);

		//GUI START
		//Load component and login with community follower
		logger.strongStep("Load homepage and login as community follower");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(commFollower);
		
		//find feeds for these Entries
		logger.strongStep("Find feeds for these Entries");
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		
		//Click on the dropdown and choose to filter with Blogs
		logger.strongStep("Click on the dropdown menu and choose to filter with Blogs");
		log.info("INFO: Filter by Blogs");
		ui.filterBy("Blogs");
			
		logger.strongStep("Select news story and open EE");
		log.info("INFO: Select news story and open EE ");
		log.info("INFO: Select " + blogPost.getTitle());
		ui.filterNewsItemOpenEE("created a blog entry named " + blogPost.getTitle() + " in the " + community.getName() + " blog");

		//Add a comment
		logger.strongStep("Add a comment to the Blog Post");
		log.info("INFO: Add a comment to the Blog Post");
		ui.addEEComment("This is the test comment for " + testName);

		ui.fluentWaitPresent(HomepageUIConstants.findOneBlogEntry_Comment);
		ui.switchToTopFrame();
		//Logout
		logger.strongStep("Logout");
		ui.logout();
		//ui.close(cfg); 	This line is commented to maintain single session in BS

		//Load component and login with community owner
		logger.strongStep("Load homepage and login as community owner");
		ui.loadComponent(Data.getData().HomepageImFollowing,true);
		ui.login(commOwner);
		
		//Click on the dropdown and choose to filter with Blogs
		logger.strongStep("Click on the dropdown and choose to filter with blogs");
		log.info("INFO: Filter by Blogs");
		ui.filterBy("Blogs");
		
		logger.strongStep("Select the blog post");
		log.info("INFO: Select " + blogPost.getTitle());		
		ui.filterNewsItemOpenEE("commented on your " + blogPost.getTitle() + " blog entry in the " + community.getName() + " blog");		

		//validate that the comment has incremented
		logger.weakStep("Validate that the comment appears");
		ui.clickLinkWait(HomepageUI.getCommentBlogsCountLink(1));

		logger.strongStep("Delete community that was created using API");
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info:</B>Check Embedded Experience fields form for Files</li>
	*<li><B>Step:</B>Create a community and add a file via the API</li>
	*<li><B>Step:</B>Login as a different userB (follower) and follow this file via API</li>
	*<li><B>Step:</B>In Homepage, go to Following tab and filter by Files</li>
	*<li><B>Step:</B>Find the news story and open the EE</li>
	*<li><B>Verify:</B>the file name and the Stop Following link displays</li>
	*<li><B>Step:</B>Login as userA (owner)</li>
	*<li><B>Step:</B>Find the news story and open the EE</li>
	*<li><B>Verify:</B>The file name, Like control, download link and Stop Following link displays</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"level2", "bvt", "regressioncloud"} )
	public void Files_EE_Basic() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		//allocate users
		User	commFollower = cfg.getUserAllocator().getUser();
		User	commOwner = cfg.getUserAllocator().getUser();		
		User 	commMember = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, commOwner.getAttribute(cfg.getLoginPreference()), commOwner.getPassword());
		APICommunitiesHandler apiFollower = new APICommunitiesHandler(serverURL, commFollower.getAttribute(cfg.getLoginPreference()), commFollower.getPassword());
		APIFileHandler	fileHandler = new APIFileHandler(serverURL, commOwner.getAttribute(cfg.getLoginPreference()), commOwner.getPassword());
	
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
									.description(Data.getData().commonDescription)
									.addMember(new Member(CommunityRole.MEMBERS, commMember))
									.build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community");
		Community commAPI = community.createAPI(apiOwner);
		
		logger.strongStep("Add a file to the community using API");
		log.info("INFO: Add file to community");
		community.addFileAPI(commAPI, file1, apiOwner, fileHandler);
		
		//Follow community with a different user
		logger.strongStep("Follow community with a different user using API");
		log.info("INFO: Follow community using API");
		community.followAPI(commAPI, apiFollower, apiOwner);

		logger.strongStep("Load homepage and login as userB (follower)");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(commFollower);
		
		//Click on the dropdown and choose to filter by Files
		logger.strongStep("Click on the dropdown and choose to filter by Files");
		log.info("INFO: Filter by files");
		ui.filterBy("Files");
		
		logger.strongStep("Select news story and open EE");
		log.info("INFO: Select news story and open EE ");
		log.info("INFO: " + commFollower + " looking for " + community.getName());
		ui.openNewsStoryFileDetailsOverlay("shared a file with the community " + community.getName());
		
		//Verify the file name displays
		logger.weakStep("Verify the file name displays");
		log.info("INFO: Verify the file name");
		Assert.assertTrue(ui.fluentWaitTextPresent(file1.getName()),
							"ERROR: File name not found");
				
		//Verify the Stop Following link displays
		ui.clickLinkWait(FilesUIConstants.FileOverlayMoreActions);
		logger.weakStep("Verify the 'Stop Following' link displays");
		log.info("INFO: Verify the 'Stop Following' link is present");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.FileOverlayMoreActionsFollow),
							"ERROR: Stop Following link not found");

		//Close File Overlay
		logger.strongStep("Close EE");
		log.info("INFO: Select Close in FileOverlay");
		ui.clickLinkWait(FilesUIConstants.FileOverlayClose);

		//click discover tab
		logger.strongStep("Select the 'Discover' tab");
		log.info("Select discover tab");
		ui.clickLink(HomepageUIConstants.discoverTab);
		
		//Click on the dropdown and choose to filter with Files
		logger.strongStep("Click on the dropdown and choose to filter by Files");
		log.info("INFO: Filter by files");
		ui.filterBy("Files");

		logger.strongStep("Select news story and open EE");
		log.info("INFO: Select news story and open EE ");
		ui.openNewsStoryFileDetailsOverlay(commOwner.getDisplayName()+" shared a file with the community " + community.getName() + ".");

		//Verify the file name displays
		logger.weakStep("Verify the file name displays");
		log.info("INFO: Verify the file name");
		Assert.assertTrue(ui.fluentWaitTextPresent(file1.getName()),
							"ERROR: File name not found");
		
		//Close File Overlay
		log.info("INFO: Select Close in FileOverlay");
		ui.clickLinkWait(FilesUIConstants.FileOverlayClose);
		
		//Logout
		logger.strongStep("Logout");
		ui.logout();
		//ui.close(cfg); 	This line is commented to maintain single session in BS

		//Load component and login with community owner
		logger.strongStep("Load homepage and login as community owner");
		ui.loadComponent(Data.getData().HomepageImFollowing,true);
		ui.login(commOwner);

		//Click on the dropdown and choose to filter with Files
		logger.strongStep("Select the dropdown menu and choose to filter with Files");
		log.info("INFO: Filter by files");
		ui.filterBy("Files");
		
		//Click on the dropdown and choose to filter with Files
		logger.strongStep("Select Files in the dropdown menu");
		log.info("INFO: Select Files in the dropdown menu ");
		ui.openNewsStoryFileDetailsOverlay(commOwner.getDisplayName()+ " shared a file with the community " + community.getName());

		//Verify the file name displays
		logger.weakStep("Verify the file name displays");
		log.info("INFO: Verify the file name");
		Assert.assertTrue(ui.fluentWaitTextPresent(file1.getName()),
							"ERROR: File name not found");
				
		//Verify the Like link displays
		logger.weakStep("Verify the 'Like' link displays ");
		log.info("INFO: Verify the 'Like' link is present");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.FileOverlayLike),
							"ERROR: Like link not found");	
		
		//Verify the Download link displays
		logger.weakStep("Verify the 'Download' link displays");
		log.info("INFO: Verify the 'Download' link is present");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.FileOverlayDownload),
							"ERROR: Download link not found");

		//Verify the Stop Following link displays
		ui.clickLinkWait(FilesUIConstants.FileOverlayMoreActions);
		logger.weakStep("Verify the 'Stop Following' link displays");
		log.info("INFO: Verify the 'Stop Following' link is present");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.FileOverlayMoreActionsFollow),
							"ERROR: Stop Following link not found");
		
		//Close File Overlay
		log.info("INFO: Select Close in FileOverlay");
		ui.clickLinkWait(FilesUIConstants.FileOverlayClose);

		logger.strongStep("Delete community that was created using API");
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
		
	}
	
	
	/**
	*<ul>
	*<li><B>Info:</B>Check Embedded Experience Likes for Files</li>
	*<li><B>Step:</B>Create a community and add a file via the API</li>
	*<li><B>Step:</B>Login as a different userB (follower) and follow this file via API</li>
	*<li><B>Step:</B>In Homepage, go to Following tab and filter by Files </li>
	*<li><B>Step:</B>Find the news story and open the EE, then select Like </li>
	*<li><B>Verify:</B>The file was 'Liked' by follower </li>
	*<li><B>Step:</B>Login as userA (owner)</li>
	*<li><B>Step:</B>Open File EE and like and then unlike</li>
	*<li><B>Verify:</B>The file is liked and unliked accordingly by the owner</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"level2", "bvt", "regressioncloud"} )
	public void Files_EE_Likes() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		ui.startTest();
		
		//allocate users
		User	commFollower = cfg.getUserAllocator().getUser();
		User	commOwner = cfg.getUserAllocator().getUser();		
		User 	commMember = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, commOwner.getAttribute(cfg.getLoginPreference()), commOwner.getPassword());
		APICommunitiesHandler apiFollower = new APICommunitiesHandler(serverURL, commFollower.getAttribute(cfg.getLoginPreference()), commFollower.getPassword());
		APIFileHandler	fileHandler = new APIFileHandler(serverURL, commOwner.getAttribute(cfg.getLoginPreference()), commOwner.getPassword());

		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
									.description(Data.getData().commonDescription)
									.addMember(new Member(CommunityRole.MEMBERS, commMember))
									.build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community");
		Community commAPI = community.createAPI(apiOwner);
		
		logger.strongStep("Add a file to the community using API");
		log.info("INFO: Add file to community");
		community.addFileAPI(commAPI, file1, apiOwner, fileHandler);
		
		//Follow community with a different user
		logger.strongStep("Follow the community with a different user using API");
		log.info("INFO: Follow community using API");
		community.followAPI(commAPI, apiFollower, apiOwner);

		logger.strongStep("Load homepage and login as userB (follower)");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(commFollower);
		
		//Click on the dropdown and choose to filter with Files
		logger.strongStep("Click on the dropdown and choose to filter with files");
		log.info("INFO: Filter by files");
		ui.filterBy(HomepageUIConstants.FilterFiles);
			
		logger.strongStep("Select news story and open EE");
		log.info("INFO: Select news story and open EE ");
		log.info("INFO: " + commFollower + " looking for " + community.getName());
		ui.openNewsStoryFileDetailsOverlay("shared a file with the community " + community.getName());
	
		//Like File in File Overlay 
		logger.strongStep("Like the file");
		log.info("INFO: Like the file");
		ui.clickLinkWait(FilesUIConstants.FileOverlayLike);
		
		//Verify the file was Liked
		logger.weakStep("Verify the file was 'Liked' by follower");
		log.info("INFO: Verify the 'Unlike' link is present");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.FileOverlayUnlike),
							"ERROR: File was not Liked");	
		
		//Close File Overlay
				log.info("INFO: Select Close in FileOverlay");
				ui.clickLinkWait(FilesUIConstants.FileOverlayClose);
				
		//Logout
		logger.strongStep("Logout");
		ui.logout();
		
		if(!(cfg.getSecurityType().equalsIgnoreCase("false"))){
			driver.close();
		}

		//Load component and login with community owner
		logger.strongStep("Load homepage and login as community owner");
		ui.loadComponent(Data.getData().HomepageImFollowing, true);
		ui.login(commOwner);

		//Click on the dropdown and choose to filter with Files
		logger.strongStep("Click on the dropdown menu and choose to filter with Files");
		log.info("INFO: Filter by files");
		ui.filterBy(HomepageUIConstants.FilterFiles);
		
		logger.strongStep("Select news story and open EE");
		log.info("INFO: Select news story and open EE ");
		ui.openNewsStoryFileDetailsOverlay(commFollower.getDisplayName()+" liked your file");

		//Like File in File Overlay 
		logger.strongStep("Like the File");
		log.info("INFO: Like the File");
		ui.clickLinkWait(FilesUIConstants.FileOverlayLike);
		
		//Verify the file was Liked
		logger.weakStep("Verify the file was 'Liked' by owner");
		log.info("INFO: Verify the 'Unlike' link is present");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.FileOverlayUnlike),
							"ERROR: File was not Liked");	
		
		//Unlike the file in File Overlay
		logger.strongStep("Unlike the file");
		log.info("INFO: Unlike the file");
		ui.clickLink(FilesUIConstants.FileOverlayUnlike);
		
		//Verify the file was Unliked
		logger.weakStep("Verify the file was 'Unliked' owner");
		log.info("INFO: Verify the 'Like' link is present");
		Assert.assertTrue(ui.fluentWaitPresent(FilesUIConstants.FileOverlayLike),
							"ERROR: File was not Unliked");	
	
		//Close File Overlay
		log.info("INFO: Select Close in FileOverlay");
		ui.clickLinkWait(FilesUIConstants.FileOverlayClose);

		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info:</B>Check Embedded Experience Comments for Files</li>
	*<li><B>Step:</B>Create a community and add a file via the API</li>
	*<li><B>Step:</B>Login as a different userB (follower) and follow this file via API/li>
	*<li><B>Step:</B>Find the news story and open the EE, then add a comment</li>
	*<li><B>Step:</B>Login as userA (owner)</li>
	*<li><B>Step:</B>Open File EE</li>
	*<li><B>Verify:</B>The comment displays</li>
	*</ul>
	*@throws Exception
	*/
	@Test(groups = {"level2", "bvt", "regressioncloud"} )
	public void Files_EE_Comments() throws Exception {
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String commentText = "This is the test comment for " + ui.startTest();
		
		//allocate users
		User	commFollower = cfg.getUserAllocator().getUser();
		User	commOwner = cfg.getUserAllocator().getUser();		
		User 	commMember = cfg.getUserAllocator().getUser();
		
		//create API handlers
		APICommunitiesHandler apiOwner = new APICommunitiesHandler(serverURL, commOwner.getAttribute(cfg.getLoginPreference()), commOwner.getPassword());
		APICommunitiesHandler apiFollower = new APICommunitiesHandler(serverURL, commFollower.getAttribute(cfg.getLoginPreference()), commFollower.getPassword());
		APIFileHandler	fileHandler = new APIFileHandler(serverURL, commOwner.getAttribute(cfg.getLoginPreference()), commOwner.getPassword());
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
									.description(Data.getData().commonDescription)
									.addMember(new Member(CommunityRole.MEMBERS, commMember))
									.build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community");
		Community commAPI = community.createAPI(apiOwner);
		
		logger.strongStep("Add file to community using API");
		log.info("INFO: Add file to community");
		community.addFileAPI(commAPI, file1, apiOwner, fileHandler);
		
		//Follow community with a different user
		logger.strongStep("Follow community with a different user using API");
		log.info("INFO: Follow community using API");
		community.followAPI(commAPI, apiFollower, apiOwner);

		logger.strongStep("Load homepage and login as userB (follower)");
		ui.loadComponent(Data.getData().HomepageImFollowing);
		ui.login(commFollower);

		//Click on the dropdown and choose to filter with Files
		logger.strongStep("Click on the dropdown and choose to filter with files");
		log.info("INFO: Filter by files");
		ui.filterBy("Files");
		
		logger.strongStep("Select news story and open EE");
		log.info("INFO: Select news story and open EE ");
		logger.strongStep("Look for community that file was shared with");
		log.info("INFO: " + commFollower + " looking for " + community.getName());
		ui.openNewsStoryFileDetailsOverlay("shared a file with the community " + community.getName());

		//Add a comment
		logger.strongStep("Add a comment to the file");
		log.info("INFO: Add a comment to the file");
		filesUI.addFileOverlayComment(commentText);
		
		//Logout
		logger.strongStep("Logout");
		ui.logout();
		//ui.close(cfg); 	This line is commented to maintain single session in BS

		//Load component and login with community owner
		logger.strongStep("Load homepage and login as community owner");
		ui.loadComponent(Data.getData().HomepageImFollowing,true);
		ui.login(commOwner);

		//Click on the dropdown and choose to filter with Files
		logger.strongStep("Click on the dropdown meny and choose to filter with Files");
		log.info("INFO: Filter by files");
		ui.filterBy("Files");
		
		//Click on the dropdown and choose to filter with Files
		
		logger.strongStep("Select news story and open EE");
		log.info("INFO: Select news story and open EE ");
		ui.openNewsStoryFileDetailsOverlay(commFollower.getDisplayName() + " commented on your file.");

		//Verify that the comment displays in the overlay
		logger.weakStep("Validate that the comment displays in the overlay");
		Assert.assertTrue(ui.fluentWaitPresent("css=div.textContainer.bidiAware > span.bidiAware:contains(" +commentText + ")"),
				"ERROR: Comment not found in overlay");

		logger.strongStep("Delete community that was created using API");
		apiOwner.deleteCommunity(commAPI);
		
		ui.endTest();
		
	}
	
}
