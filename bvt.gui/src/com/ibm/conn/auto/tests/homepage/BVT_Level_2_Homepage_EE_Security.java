package com.ibm.conn.auto.tests.homepage;


import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_Level_2_Homepage_EE_Security extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Homepage_EE.class);
	private HomepageUI ui;
	private CommunitiesUI cUI;
	private BlogsUI bUI;
	private FilesUI fUI;
	private TestConfigCustom cfg;	
	private User commFollower, commOwner, commMember;
	private BaseFile file1;
	private APICommunitiesHandler apiOwner, apiFollower;
	private APIFileHandler fileHandler;
	private String homepageURI;	
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		
		//Load Users
		commFollower = cfg.getUserAllocator().getUser();
		commOwner = cfg.getUserAllocator().getUser();		
		commMember = cfg.getUserAllocator().getUser();

		file1 = new BaseFile.Builder(Data.getData().file1)
							.comFile(true)
							.extension(".jpg")
							.build();
		
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, commOwner.getUid(), commOwner.getPassword());
		apiFollower = new APICommunitiesHandler(serverURL, commFollower.getUid(), commFollower.getPassword());
		fileHandler = new APIFileHandler(serverURL, commOwner.getUid(), commOwner.getPassword());
		
		homepageURI = Data.getData().ComponentHomepage.split("/")[0];
	}

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {

		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		cUI = CommunitiesUI.getGui(cfg.getProductName(), driver);		
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		bUI = BlogsUI.getGui(cfg.getProductName(), driver);
		fUI = FilesUI.getGui(cfg.getProductName(), driver);
		
	}
	
	/**
	*<ul>
	*<li><B>Info: Check Embedded Experience fields form for Blogs</B></li>
	*<li><B>Step: Create a community, add a blog. As a different user follow this blog. As the first user login
	*and then open the EE (homepage) and validate the fields and 'Read More' link.</B> </li>
	*<li><B>Verify: Verify that EE Popup opens validate the fields in the popup and that the read more link works</B> </li>
	*</ul>
	*/
	@Deprecated
	@Test(groups = {"bvtSecurity"} )
	public void Blogs_EE_Basic() throws Exception {

		String testName = ui.startTest();

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
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);
		
		//get uuid
		community.getCommunityUUID_API(apiOwner, commAPI);

		//add widget
		log.info("INFO: Add blog widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.BLOG);
		
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities );
		ui.login(commMember);

		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	
		
		
		// Click on Blogs
		Community_LeftNav_Menu.BLOG.select(cUI);
		
		//select New Entry button
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		log.info("INFO: Add a new entry to the blog");
		blogPost.create(bUI);
		
		ui.logout();
		ui.close(cfg);
		
		//Follow community with a different user
		log.info("INFO: Follow community using API");
		community.followAPI(commAPI, apiFollower, apiOwner);

		//GUI START
		//Load component and login with community follower
		ui.loadComponent(homepageURI);
		ui.login(commFollower);
		
		//Open Update menu item
		ui.clickLinkWait(HomepageUIConstants.Updates);
		
		//Select following tab
		log.info("Select Following tab");
		ui.clickLinkWait(HomepageUIConstants.HomepageImFollowing);
		
		//find feeds for these Entries
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		
		//Click on the dropdown and choose to filter with Blogs
		log.info("INFO: Filter by Blogs");
		ui.filterBy("Blogs");
			
		log.info("INFO: Select news story and open EE ");
		log.info("INFO: Select " + blogPost.getTitle());
		ui.filterNewsItemOpenEE("created a blog entry named " + blogPost.getTitle() + " in the " + community.getName() + " blog.");

		//Verify the objects in the EE
		log.info("INFO: Validate the Embedded Experience");

		//Verify the EE widget
		log.info("INFO: Verify the EE Title");
		ui.fluentWaitTextPresent(blogPost.getTitle());
		
		//Verify tag
		log.info("INFO: Verify the EE Tags");
		ui.fluentWaitTextPresent(blogPost.getTags());

		//Verify like
		log.info("INFO: Verify the EE Like");
		ui.fluentWaitPresent(HomepageUIConstants.EELike);
		
		//Verify Description
		log.info("INFO: Verify the EE Description");
		ui.fluentWaitTextPresent(blogPost.getContent());
		
		//Verify Read more
		log.info("INFO: Verify the EE Read More link");
		ui.fluentWaitPresent(HomepageUIConstants.EEReadmore);
		
		//Verify the Tab for Comments
		log.info("INFO: Verify the EE Comment Tab");
		ui.fluentWaitPresent(HomepageUIConstants.EECommentsTab);
		
		//Verify the Tab for Recent Updates
		log.info("INFO: Verify the EE Recent Updates Tab");
		ui.fluentWaitPresent(HomepageUIConstants.EEHistoryTab);
		
		//Click on Read more and verify that a new tab is opened
		log.info("INFO: Validate Read more option");
		ui.clickLinkWithJavascript(HomepageUIConstants.EEReadmore);
		
		//switch to new window
		log.info("INFO: Switch to new window");
		ui.switchToNewTabByName(community.getName());
		Assert.assertTrue(driver.isTextPresent(""));
	
		//close the new tab
		log.info("INFO: Close new window");
		ui.close(cfg);

		//switch back to main window
		ui.switchToNewTabByName("IBM Connections Home Page - Updates");
		
		
		ui.endTest();

	}

	/**
	*<ul>
	*<li><B>Info: Check Embedded Experience Likes for Blogs</B></li>
	*<li><B>Step: Create a community, add a blog. As a different user follow this blog. As the first user login
	*and then open the EE (homepage), like and comment on this blog. As the second user login and open EE and like and then unlike.</B> </li>
	*<li><B>Verify: Verify that EE is working and the actions like, comments are working as expected</B> </li>
	*</ul>
	*/
	@Deprecated
	@Test(groups = {"bvtSecurity"} )
	public void Blogs_EE_Likes() throws Exception {

		String testName = ui.startTest();

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
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);

		//get uuid
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//add widget
		log.info("INFO: Add blog widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.BLOG);
		
		//Follow community with a different user
		log.info("INFO: Follow community using API");
		community.followAPI(commAPI, apiFollower, apiOwner);
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(commOwner);

		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	
		
		// Click on Blogs
		Community_LeftNav_Menu.BLOG.select(cUI);
		
		//select New Entry button
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		log.info("INFO: Add a new entry to the blog");
		blogPost.create(bUI);
		
		ui.logout();
		ui.close(cfg);

		//GUI START
		//Load component and login with community follower
		ui.loadComponent(homepageURI);
		ui.login(commFollower);
		
		//Open Update menu item
		ui.clickLinkWait(HomepageUIConstants.Updates);
		
		//Select following tab
		log.info("Select Following tab");
		ui.clickLinkWait(HomepageUIConstants.HomepageImFollowing);
		
		//find feeds for these Entries
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		
		//Click on the dropdown and choose to filter with Blogs
		log.info("INFO: Filter by Blogs");
		ui.filterBy("Blogs");
			
		log.info("INFO: Select news story and open EE ");
		log.info("INFO: Select " + blogPost.getTitle());
		ui.filterNewsItemOpenEE("created a blog entry named " + blogPost.getTitle() + " in the " + community.getName() + " blog.");
			
		//Like EE 
		log.info("INFO: Like the Blog Post");
		ui.clickLinkWait(HomepageUIConstants.EELike);
			
		//Logout
		ui.logout();
		ui.close(cfg);

		//Load component and login with community owner
		ui.loadComponent(homepageURI);
		ui.login(commOwner);
		
		//Open Update menu item
		log.info("Select updates from the left menu");		
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Select following tab
		log.info("Select Following tab");		
		ui.clickLink(HomepageUIConstants.HomepageImFollowing);
		
		//Click on the dropdown and choose to filter with Blogs
		log.info("INFO: Filter by Blogs");
		ui.filterBy("Blogs");
			
		//Click on the dropdown and choose to filter with Blogs
		log.info("INFO: Select " + blogPost.getTitle());		
		ui.filterNewsItemOpenEE("liked your " + blogPost.getTitle() + " blog entry in the " + community.getName() + " blog.","liked your blog entry " + blogPost.getTitle() + " in the " + community.getName() + " blog.");
				
		//Like the blog and then verify the users that have liked this
		log.info("INFO: Like the blog");
		
		//Like EE 
		log.info("INFO: Like the Blog Post");
		ui.clickLinkWithJavascript(HomepageUIConstants.EELike);
		
		//click on the like count with correct value
		log.info("INFO: Select like count " + 2);
		ui.clickLinkWait(HomepageUI.getLikeCountLink(2));   
		
		//Find users who like blog
		log.info("INFO: Validating people who like the blog.");
		Assert.assertTrue(ui.findPersonInLikeList(commFollower));		
		Assert.assertTrue(ui.findPersonInLikeList(commOwner));

		//Like user popup
		ui.clickLink(HomepageUIConstants.EEUserPopupClose);
		
		//Unlike the blog entry in EE and verify that the like appears again
		log.info("INFO: Unlike the blog");
		ui.clickLink(HomepageUIConstants.EELikeUndo);
		
		//Switch back to the main frame
		ui.switchToTopFrame();
		
		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info: Check Embedded Experience Comments for Blogs</B></li>
	*<li><B>Step: Create a community, add a blog. As a different user follow this blog. As the first user login
	*and then open the EE (homepage), like and comment on this blog. As the second user login and open EE and like and then unlike.</B> </li>
	*<li><B>Verify: Verify that EE is working and the actions like, comments are working as expected</B> </li>
	*</ul>
	*/
	@Deprecated
	@Test(groups = {"bvtSecurity"} )
	public void Blogs_EE_Comments() throws Exception {

		String testName = ui.startTest();

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
		log.info("INFO: Create community using API");
		Community commAPI = community.createAPI(apiOwner);

		//get uuid
		community.getCommunityUUID_API(apiOwner, commAPI);
		
		//add widget
		log.info("INFO: Add blog widget to community using API");
		community.addWidgetAPI(commAPI, apiOwner, BaseWidget.BLOG);

		
		//Follow community with a different user
		log.info("INFO: Follow community using API");
		community.followAPI(commAPI, apiFollower, apiOwner);
		
		
		//GUI
		//Load component and login
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(commOwner);

		//Navigate to owned communities
		log.info("INFO: Navigate to the owned communtiy views");
		ui.clickLinkWait(CommunitiesUIConstants.OwnerCommunitiesView);
		
		//navigate to the API community
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(cUI);	
		
		
		// Click on Blogs
		Community_LeftNav_Menu.BLOG.select(cUI);
		
		//select New Entry button
		log.info("INFO: Select New Entry button");
		ui.clickLinkWait(BlogsUIConstants.blogsNewEntryMenuItem);
		
		//Add an Entry
		log.info("INFO: Add a new entry to the blog");
		blogPost.create(bUI);
		
		ui.logout();
		ui.close(cfg);

		//GUI START
		//Load component and login with community follower
		ui.loadComponent(homepageURI);
		ui.login(commFollower);
		
		//Open Update menu item
		ui.clickLinkWait(HomepageUIConstants.Updates);
		
		//Select following tab
		log.info("Select Following tab");
		ui.clickLinkWait(HomepageUIConstants.HomepageImFollowing);
		
		//find feeds for these Entries
		ui.fluentWaitTextPresent(Data.getData().feedsForTheseEntries);
		
		//Click on the dropdown and choose to filter with Blogs
		log.info("INFO: Filter by Blogs");
		ui.filterBy("Blogs");
			
		log.info("INFO: Select news story and open EE ");
		log.info("INFO: Select " + blogPost.getTitle());
		ui.filterNewsItemOpenEE("created a blog entry named " + blogPost.getTitle() + " in the " + community.getName() + " blog");

		//Add a comment
		log.info("INFO: Add a comment to the Blog Post");
		ui.addEEComment("This is the test comment for " + testName);

		ui.fluentWaitPresent(HomepageUIConstants.findOneBlogEntry_Comment);
		
		//Logout
		ui.logout();
		ui.close(cfg);

		//Load component and login with community owner
		ui.loadComponent(homepageURI);
		ui.login(commOwner);
		
		//Open Update menu item
		log.info("Select updates from the left menu");		
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Select following tab
		log.info("Select Following tab");		
		ui.clickLink(HomepageUIConstants.HomepageImFollowing);
		
		//Click on the dropdown and choose to filter with Blogs
		log.info("INFO: Filter by Blogs");
		ui.filterBy("Blogs");
		
		log.info("INFO: Select " + blogPost.getTitle());		
		ui.filterNewsItemOpenEE("commented on your " + blogPost.getTitle() + " blog entry in the " + community.getName() + " blog");		

		//validate that the comment has incremented
		ui.clickLinkWait(HomepageUI.getCommentBlogsCountLink(1));
		
		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: Check Embedded Experience fields form for Files</B></li>
	*<li><B>Step: Create a community, add a file. As a different user follow this file. As the first user login
	*and then open the EE (homepage), like and comment on this file. As the second user login and open EE and like and then unlike.</B> </li>
	*<li><B>Verify: Verify that EE is working and the actions like, comments are working as expected</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Deprecated
	@Test(groups = {"bvtSecurity"} )
	public void Files_EE_Basic() throws Exception {
		
		ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
									.description(Data.getData().commonDescription)
									.addMember(new Member(CommunityRole.MEMBERS, commMember))
									.build();
		
		Files_Setup_p1(community);
		
		ui.loadComponent(homepageURI);
		ui.login(commFollower);
		
		//Open Update menu item
		log.info("Select updates from the left menu");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Select following tab
		log.info("Select following tab");
		ui.clickLinkWait(HomepageUIConstants.HomepageImFollowing);
		
		//Click on the dropdown and choose to filter with Files
		log.info("INFO: Filter by files");
		ui.filterBy("Files");
		
		//Click on the dropdown and choose to filter with Files
		log.info("INFO: " + commFollower + " looking for " + community.getName());
		ui.filterNewsItemOpenEE("shared a file with the community " + community.getName());

		//Switch back to the main frame
		log.info("INFO: Switch to main frame");
		ui.switchToTopFrame();
				
		//click discover tab
		log.info("Select discover tab");
		ui.clickLink(HomepageUIConstants.discoverTab);
		
		//Click on the dropdown and choose to filter with Files
		log.info("INFO: Filter by files");
		ui.filterBy("Files");

		log.info("INFO: Select news story and open EE ");
		ui.filterNewsItemOpenEE(commOwner.getDisplayName()+" shared a file with the community " + community.getName() + ".");

		//Switch back to the main frame
		log.info("INFO: Switch back to main frame");
		ui.switchToTopFrame();
		
		//Logout
		ui.logout();
		ui.close(cfg);

		//Load component and login with community owner
		ui.loadComponent(homepageURI);
		ui.login(commOwner);
		
		//Open Update menu item
		log.info("Select updates from the left menu");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Select following tab
		log.info("Select following tab");
		ui.clickLinkWait(HomepageUIConstants.HomepageImFollowing);
		
		//Click on the dropdown and choose to filter with Files
		log.info("INFO: Filter by files");
		ui.filterBy("Files");
		
		//Click on the dropdown and choose to filter with Files
		log.info("INFO: Select Files in the dropdown menu ");
		ui.filterNewsItemOpenEE(commOwner.getDisplayName()+ " shared a file with the community " + community.getName());

		//Verify the EE widget file name
		log.info("INFO: Verify the file name");
		ui.fluentWaitTextPresent(file1.getName());
				
		//Verify the Like link
		log.info("INFO: Verify the 'Like' link is present");
		ui.fluentWaitPresent(HomepageUIConstants.EELike);
		
		//Verify the Preview link
		log.info("INFO: Verify the 'Preview' link is present");
		ui.fluentWaitPresent(HomepageUIConstants.Preview);
		
		//Verify the Download link
		log.info("INFO: Verify the 'Download' link is present");
		ui.fluentWaitPresent(HomepageUIConstants.Download);

		//Verify the Stop Following link
		log.info("INFO: Verify the 'Stop Following' link is present");
		ui.fluentWaitPresent(HomepageUIConstants.StopFollowFileButton);

		//Verify the Tab Comments
		log.info("INFO: Verify the 'Comment' tab is present");
		ui.fluentWaitPresent(HomepageUIConstants.EEFilesCommentsTab);
		
		//Verify the Tab Recent Updates
		log.info("INFO: Verify the 'Recent Updates' tab is present");
		ui.fluentWaitPresent(HomepageUIConstants.EEFilesHistoryTab);

		//Switch back to the main frame
		ui.switchToTopFrame();
		
		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info: Check Embedded Experience Likes for Files</B></li>
	*<li><B>Step: Create a community, add a file. As a different user follow this file. As the first user login
	*and then open the EE (homepage), like and comment on this file. As the second user login and open EE and like and then unlike.</B> </li>
	*<li><B>Verify: Verify that EE is working and the actions like, comments are working as expected</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Deprecated
	@Test(groups = {"bvtSecurity"} )
	public void Files_EE_Likes() throws Exception {
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
									.description(Data.getData().commonDescription)
									.addMember(new Member(CommunityRole.MEMBERS, commMember))
									.build();
		//get uuid
			//	community.getCommunityUUID_API(apiOwner, commAPI);
		
		Files_Setup_p1(community);
		
		//Follow community with a different user
		log.info("INFO: Follow community using API");
		Files_Setup_p2(community);

		ui.loadComponent(homepageURI);
		ui.login(commFollower);
		
		//Open Update menu item
		log.info("Select updates from the left menu");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Select following tab
		log.info("Select following tab");
		ui.clickLinkWait(HomepageUIConstants.HomepageImFollowing);
				
		//Click on the dropdown and choose to filter with Files
		log.info("INFO: Filter by files");
		ui.filterBy("Files");
			
		log.info("INFO: Select news story and open EE ");
		log.info("INFO: " + commFollower + " looking for " + community.getName());
		ui.filterNewsItemOpenEE("shared a file with the community " + community.getName());
	
		//Like EE 
		log.info("INFO: Like the file");
		ui.clickLink(HomepageUIConstants.EELike);
		
		//Logout
		ui.logout();
		ui.close(cfg);

		//Load component and login with community owner
		ui.loadComponent(homepageURI);
		ui.login(commOwner);
		
		//Open Update menu item
		log.info("Select updates from the left menu");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Select following tab
		log.info("Select following tab");
		ui.clickLinkWait(HomepageUIConstants.HomepageImFollowing);
				
		//Click on the dropdown and choose to filter with Files
		log.info("INFO: Filter by files");
		ui.filterBy("Files");
		
		//Click on the dropdown and choose to filter with Files
		log.info("INFO: Select Files in the dropdown menu ");
		ui.filterNewsItemOpenEE(commFollower.getDisplayName()+" liked a file.");

		//Like EE 
		log.info("INFO: Like the Blog Post");
		ui.clickLinkWait(HomepageUIConstants.EELike);
		
		//click on the like count with correct value
		log.info("INFO: Select like count " + 2);
		ui.clickLinkWait(HomepageUI.getLikeCountLink(2));  
		
		//Find users who like blog
		log.info("INFO: Validating people who like the blog.");
		Assert.assertTrue(ui.findPersonInLikeList(commFollower));		
		Assert.assertTrue(ui.findPersonInLikeList(commOwner));
		
		//Close like user popup
		ui.clickLink(HomepageUIConstants.EEUserPopupClose);
		
		//Unlike the file in EE and verify that the like appears again
		log.info("INFO: Unlike the file");
		ui.clickLink(HomepageUIConstants.EELikeUndo);
		
		ui.endTest();
		
	}

	/**
	*<ul>
	*<li><B>Info: Check Embedded Experience Comments for Files</B></li>
	*<li><B>Step: Create a community, add a file. As a different user follow this file. As the first user login
	*and then open the EE (homepage), like and comment on this file. As the second user login and open EE and like and then unlike.</B> </li>
	*<li><B>Verify: Verify that EE is working and the actions like, comments are working as expected</B> </li>
	*</ul>
	*@throws Exception
	*/
	@Deprecated
	@Test(groups = {"bvtSecurity"} )
	public void Files_EE_Comments() throws Exception {

		
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(Data.getData().commonName + Helper.genDateBasedRandVal())
									.access(Access.PUBLIC)
									.tags(Data.getData().commonTag + Helper.genDateBasedRandVal())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal())
									.description(Data.getData().commonDescription)
									.addMember(new Member(CommunityRole.MEMBERS, commMember))
									.build();
		
		//create community
		log.info("INFO: Create community");
		Community commAPI = community.createAPI(apiOwner);
		
		log.info("INFO: Add file to community");
		community.addFileAPI(commAPI, file1, apiOwner, fileHandler);
		
		//Follow community with a different user
		log.info("INFO: Follow community using API");
		community.followAPI(commAPI, apiFollower, apiOwner);

		ui.loadComponent(homepageURI);
		ui.login(commFollower);
		
		//Open Update menu item
		log.info("Select updates from the left menu");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Select following tab
		log.info("Select following tab");
		ui.clickLinkWait(HomepageUIConstants.HomepageImFollowing);
				
		//Click on the dropdown and choose to filter with Files
		log.info("INFO: Filter by files");
		ui.filterBy("Files");
			
		log.info("INFO: Select news story and open EE ");
		log.info("INFO: " + commFollower + " looking for " + community.getName());
		ui.filterNewsItemOpenEE("shared a file with the community " + community.getName());

		//Add a comment
		log.info("INFO: Add a comment to the file");
		ui.addEEComment("This is the test comment for " + testName);
		
		//Logout
		ui.logout();
		ui.close(cfg);

		//Load component and login with community owner
		ui.loadComponent(homepageURI);
		ui.login(commOwner);
		
		//Open Update menu item
		log.info("Select updates from the left menu");
		ui.clickLink(HomepageUIConstants.Updates);
		
		//Select following tab
		log.info("Select following tab");
		ui.clickLinkWait(HomepageUIConstants.HomepageImFollowing);
		
		//Click on the dropdown and choose to filter with Files
		log.info("INFO: Filter by files");
		ui.filterBy("Files");
		
		//Click on the dropdown and choose to filter with Files
		log.info("INFO: Select Files in the dropdown menu ");
		ui.filterNewsItemOpenEE(commFollower.getDisplayName() + " commented on your file.");

		//validate that the comment has incremented
		ui.clickLinkWait(HomepageUI.getCommentFileCountLink(1));
		
		ui.endTest();
		
	}
	
	
	private void Files_Setup_p1(BaseCommunity com)
	{
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(commOwner);
		
		ui.waitForPageLoaded(driver);
		
		com.create(cUI);
		ui.waitForPageLoaded(driver);
		/*//Navigate to files within community.
		Community_LeftNav_Menu.FILES.select(cUI);
		ui.waitForPageLoaded(driver);
		Community_LeftNav_Menu.OVERVIEW.select(cUI);
		ui.waitForPageLoaded(driver);
		Community_LeftNav_Menu.FILES.select(cUI);
		ui.waitForPageLoaded(driver);*/
		
		//upload file
		fUI.upload(file1);
		
		ui.logout();
		ui.close(cfg);
	}
	
	private void Files_Setup_p2(BaseCommunity com)
	{
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(commFollower);
		
		ui.waitForPageLoaded(driver);
		
		com.follow(cUI);
		
		ui.logout();
		ui.close(cfg);
	}
}
