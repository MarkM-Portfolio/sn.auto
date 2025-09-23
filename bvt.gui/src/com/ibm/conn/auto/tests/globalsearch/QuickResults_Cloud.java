package com.ibm.conn.auto.tests.globalsearch;

import java.util.List;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import com.ibm.conn.auto.webui.constants.FilesUIConstants;
import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseActivity;
import com.ibm.conn.auto.appobjects.base.BaseActivityEntry;
import com.ibm.conn.auto.appobjects.base.BaseActivityTemplate;
import com.ibm.conn.auto.appobjects.base.BaseActivityToDo;
import com.ibm.conn.auto.appobjects.base.BaseBlogPost;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.appobjects.base.BaseFolder;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseSubCommunity;
import com.ibm.conn.auto.appobjects.member.ActivityMember;
import com.ibm.conn.auto.appobjects.role.ActivityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.webui.ActivitiesUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.FilesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.conn.auto.webui.GlobalsearchUI;
import com.ibm.conn.auto.webui.WikisUI;
import com.ibm.conn.auto.webui.FilesUI.FilesListView;
import com.ibm.lconn.automation.framework.services.blogs.nodes.BlogPost;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Event;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

/**
 * Test cases:
 * quickResultsCommunities
 * quickResultsFileFolder
 * quickResultsActivities
 * quickResultsActivityTemplate
 * quickResultsFileFolder
 * 
 * These tests verifies that view action for communities, activities, activities templates, file, folder, communities affect search results in quick results. 
 * Tests should run on Cloud server.
 * 
 * General Steps:
 * 
 * Population
 * ----------
 * 1. login as user1
 * 2. Create and share Activity/ Activity Template/ Files/ folder/ Community
 * 3. Creation includes all options for these components (for example, activity entry, folder in community, etc)
 * 4. Logout user1 and login with user2
 * 5. View created items
 * 
 * Verification
 * ------------
 * 6. Type the first 3 letters of item names in the search box
 * 5. Verify search results includes names from section 2
 * 6. Cleanup - delete and logout
 * 
 * Note: These tests are for SC only
 * 
 * @author etsadok
 *
 */

public class QuickResults_Cloud extends SetUpMethods2 {
	
	protected static Logger log = LoggerFactory.getLogger(QuickResults_Cloud.class);
	private TestConfigCustom cfg;
	private ActivitiesUI activitiesUi;
	private CommunitiesUI commUi;
	private FilesUI filesUi;
	private ForumsUI forumUI;
	private WikisUI wikisUi;
	private String testName;
	private GlobalsearchUI searchUi;
	private String testURL;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		testURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		commUi = CommunitiesUI.getGui(cfg.getProductName(), driver);
		searchUi = GlobalsearchUI.getGui(cfg.getProductName(), driver);
		filesUi = FilesUI.getGui(cfg.getProductName(), driver);
		wikisUi = WikisUI.getGui(cfg.getProductName(), driver);
		forumUI = ForumsUI.getGui(cfg.getProductName(), driver);
		activitiesUi = ActivitiesUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	 * Test quick results for view action in communities.
	 * Test steps are:
	 * 1. Population - login as user1 and create community, sub community, related community, etc
	 * 2. View - login as user2 and click on the items in order to simulate view action
	 * 3. Search - type 3 first letters of the item in the search box and verify expected result appears
	 * 4. Cleanup - login as user1 and delete the created data
	 * 
	 * @throws Exception
	 */
	@Test(groups = {"regressioncloud"} , enabled=false )
	public void quickResultsCommunities() throws Exception{
		
		testName = commUi.startTest();
		User testUser1 = cfg.getUserAllocator().getUser();
		User testUser2 = cfg.getUserAllocator().getUser();
		APICommunitiesHandler apiCommHandler = new APICommunitiesHandler(testURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		APIFileHandler fileHandler = new APIFileHandler(testURL, testUser1.getEmail(), testUser1.getPassword());
		APIForumsHandler apiForumHandler = new APIForumsHandler(testURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		APICalendarHandler apiCalendarHandler = new APICalendarHandler(testURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		testName = commUi.startTest();
		
		BaseCommunity baseCommunity = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
	 	   										.tags("testTags"+ Helper.genDateBasedRand())
	 	   										.access(Access.PUBLIC)
	 	   										.description("Test description for quickResults test " + Helper.genDateBasedRand())
	 	   										.build();
		
		BaseDogear bookmark = new BaseDogear.Builder("Bookmark " + testName + Helper.genDateBasedRand() , Data.getData().BookmarkURL)
												.community(baseCommunity)
												.tags(Data.getData().BookmarkTag)
												.description(Data.getData().BookmarkDesc)
												.build();
		String relatedCommName = "Related" + Helper.genDateBasedRand();
		
		BaseCommunity baseRelatedCommunity = new BaseCommunity.Builder(relatedCommName)
												.tags("testTags"+ Helper.genDateBasedRand())
												.access(Access.PUBLIC)
												.description("Test description for testcase " + "APITest" + Helper.genDateBasedRand())
												.build();
		
		BaseForum forum = new BaseForum.Builder("Forum" + testName + Helper.genDateBasedRandVal())
		   										.tags(Data.getData().commonTag)
		   										.description(Data.getData().commonDescription).build();
		
		BaseForumTopic forumTopic = new BaseForumTopic.Builder("forumTopic" + testName + Helper.genDateBasedRandVal())
											.tags(Data.getData().ForumTopicTag)
											.description("QuickResults Test")
											.partOfCommunity(baseCommunity)
											.build();
		
		BaseActivity baseActivity = new BaseActivity.Builder("activity" + Helper.genDateBasedRandVal())
												.community(baseCommunity)
												.goal(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												.build();
		
		BaseActivityEntry baseActivityEntry = BaseActivityEntry.builder("ActivityEntry" + Helper.genMonthDateBasedRandVal())
												.description(Data.getData().commonDescription + Helper.genMonthDateBasedRandVal())
												.build();
		
		BaseActivityToDo baseTodo = BaseActivityToDo.builder("toDo" + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												.build();
		
		BaseBlogPost baseBlogPost = new BaseBlogPost.Builder("blog entry " + testName  + Helper.genDateBasedRandVal())
												.tags(Data.getData().commonAddress + Helper.genDateBasedRand())
												.content(Data.getData().commonDescription + Helper.genDateBasedRand())
												.build();
		
		BaseBlogPost ideationBlogEntry = new BaseBlogPost.Builder("IdeationBlog " + Helper.genDateBasedRandVal())
												.tags("testTags"+Helper.genDateBasedRand())
												.content("content" + Helper.genDateBasedRand()).allowComments(true)
												.numDaysCommentsAllowed(5).complete(true)
												.build();
		
		BaseEvent baseEvent = new BaseEvent.Builder("Event " + Helper.genDateBasedRand())
												.tags(Data.getData().commonTag)
												.description(Data.getData().commonDescription)
												.build();
	
		/*
		 * Create 2 communities: main community and community that will be used as related community
		 */
		log.info("Create main and related communities");
		Community mainCommunity = apiCommHandler.createCommunity(baseCommunity);
		baseCommunity.setCommunityUUID(apiCommHandler.getCommunityUUID(mainCommunity)); 
		Community relatedCommunity = apiCommHandler.createCommunity(baseRelatedCommunity);
		String commName = mainCommunity.getTitle();
		
		baseCommunity.addWidgetAPI(mainCommunity, apiCommHandler, BaseWidget.BLOG);
		baseCommunity.addWidgetAPI(mainCommunity, apiCommHandler, BaseWidget.ACTIVITIES);
		baseCommunity.addWidgetAPI(mainCommunity, apiCommHandler, BaseWidget.EVENTS);
		baseCommunity.addWidgetAPI(mainCommunity, apiCommHandler, BaseWidget.IDEATION_BLOG);
		baseCommunity.addWidgetAPI(mainCommunity, apiCommHandler, BaseWidget.RELATED_COMMUNITIES);
		
		/*
		 * Event
		 */
		Event event = apiCalendarHandler.createEvent(baseEvent, mainCommunity);
		log.info("Event " + event.getTitle() + " created");
		
		/*
		 * Blog, blog entry, ideation blog
		 */
		BlogPost blogPost = apiCommHandler.createBlogEntry(baseBlogPost, mainCommunity);
		BlogPost idea = apiCommHandler.createIdea(ideationBlogEntry, mainCommunity);
		
		/*
		 * Create bookmark
		 */
		bookmark.createAPI(apiCommHandler);
		
		/*
		 * Add file to community
		 */
		BaseFile file = new BaseFile.Builder(Data.getData().file1)
							.extension(".jpg")
							.rename(Helper.genDateBasedRand())
							.build();
		
		log.info("Add file to community");
		apiCommHandler.uploadFile(mainCommunity, file, fileHandler.getService());
		
		
		/*
		 * Rename file and create sub community
		 */
		log.info("Create sub community");
		commUi.loadComponent(Data.getData().ComponentCommunities, true);
		commUi.login(testUser1);
		String fileName = "File" + Helper.genDateBasedRand();
		
		commUi.clickLink("link=" + commName);
		commUi.clickLink("css=a[class^='lotusAction'][class$='lconnFontNormalNarrow']:contains('View All')");
		commUi.clickLinkWait(FilesListView.DETAILS.getActivateSelector());
		commUi.clickLinkWait("css=img[class^='lconnSprite'][class$='lconnSprite-iconContext']");
		commUi.clickLinkWait("css=td[id='dijit_MenuItem_141_text']");
		commUi.clearText(FilesUIConstants.editPropertiesName);
		commUi.typeText(FilesUIConstants.editPropertiesName, fileName);
		commUi.clickButton(Data.getData().buttonSave);
		
		backToOverview();
		
		BaseSubCommunity subCommunity = new BaseSubCommunity.Builder("SubCommunity" + Helper.genDateBasedRand())
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.access(BaseSubCommunity.Access.PUBLIC)
										.description("Test description for testcase " + testName).build();
		commUi.createSubCommunity(subCommunity);
		String subcommName = subCommunity.getName();
		
		/*
		 * Add related community
		 */
		log.info("Add related community to main community");
		commUi.clickLink("css=a[title^='Click here to see'][role='button']");
		commUi.clickLink("css=a[title='Add a Community'][dojoattachevent='onclick:showAddDialog']");
		
		String commUUIDA = apiCommHandler.getCommunityUUID(relatedCommunity);
		String overview = testURL
				+"/communities/service/html/communityoverview?" + commUUIDA;
		
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityURL).type(overview);
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityName).type(relatedCommunity.getTitle());
		driver.getSingleElement(ForumsUIConstants.RelatedCommunityDesc).type("Related Communities");
		
		commUi.clickSaveButton();
		log.info("Related community created");
		
		/*
		 * forum api
		 */
		String mainCommUUID = apiCommHandler.getCommunityUUID(mainCommunity);
		String mainCommUrl = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ mainCommUUID;
		Forum forumApi = apiForumHandler.createCommunityForum(mainCommUrl, forum);
		
		/* 
		 * Create folder 
		 */
		BaseFolder folder = new BaseFolder.Builder("folder" + testName + Helper.genDateBasedRand())
								.description(Data.getData().FolderDescription)
								.build();
		
		// Select Files from left menu
		commUi.clickLink(commUi.getCommunitiesMegaMenu());
		commUi.clickLink("link=I'm an Owner");
		commUi.clickLink("link=" + commName);
		commUi.clickLink(CommunitiesUIConstants.foldersTab);
		folder.add(filesUi);
		
		/*
		 * Create forum topic
		 */
		backToOverview();
		commUi.fluentWaitTextPresent("Start the First Topic");
		forumTopic.create(forumUI);
		
		/*
		 * activity, entry, todo
		 */
		backToOverview();
		baseActivity.createInCommunity(activitiesUi);
		baseTodo.create(activitiesUi);
		baseActivityEntry.create(activitiesUi);

		commUi.logout();
		

		/*
		 * View communities
		 */

		commUi.loadComponent(Data.getData().ComponentCommunities, true);
		commUi.login(testUser2);
		
		String orgName = driver.getSingleElement("css=ul[class='lotusInlinelist lotusLinks'] li a[href*='orgprofiles/partnerPage']").getText();
		log.info("orgname is: " + orgName);
		
		log.info("INFO: View community elements");
		view(orgName + " Communities");
		view(commName);
		view(relatedCommName);
		commUi.clickLinkWait(commUi.getCommunitiesMegaMenu());
		view(orgName + " Communities");
		view(subcommName);
		driver.getFirstElement("link=" + commName).click();
		view(fileName + ".jpg");
		closePreview();
		commUi.clickLinkWait(CommunitiesUIConstants.foldersTab);
		view(folder.getName());
		backToOverview();
		view("Welcome to " + commName);
		backToOverview();
		view(forumTopic.getTitle());
		view("Forum: " + forumApi.getTitle());
		backToOverview();
		view(baseActivity.getName());
		String todoRootUUID = activitiesUi.getEntryUUID(baseTodo);
		activitiesUi.expandEntry(todoRootUUID);
		String entryRootUUID = activitiesUi.getEntryUUID(baseActivityEntry);
		activitiesUi.expandEntry(entryRootUUID);
		backToOverview();
		view(blogPost.getTitle());
		view(commName);
		backToOverview();
		view(idea.getTitle());
		backToOverview();
		view(event.getTitle());
		backToOverview();
		view(bookmark.getTitle());
		
		commUi.loadComponent(Data.getData().ComponentCommunities, true);
		
	/*
	 * Search community
	 */
		// Wait 40 seconds in order to allow data indexed which is mandatory in order to get the expected results in the search 
		commUi.sleep(40000);
		commUi.selectGlobalSearch(orgName + " Communities");	
		
		log.info("Quick search for community name");
		Assert.assertTrue(searchUi.quickResultsSearch(commName.substring(0,3), commName, "Community"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(commName));
		
		log.info("Quick search for subcommunity name");
		Assert.assertTrue(searchUi.quickResultsSearch(subcommName.substring(0,3), subcommName, "Community"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(subcommName));
		
		log.info("Quick search for related community name");
		driver.getFirstElement("link=" + commName).click();
		Assert.assertTrue(searchUi.quickResultsSearch(relatedCommName.substring(0,3), relatedCommName, "Community"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(relatedCommName));
		
		log.info("Quick search for file in community");
		Assert.assertTrue(searchUi.quickResultsSearch(fileName.substring(0,3), file.getName(), "File"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(file.getName()));
		
		log.info("Quick search for folder in community");
		closePreview();
		Assert.assertTrue(searchUi.quickResultsSearch(folder.getName().substring(0,3), folder.getName(), "File"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(folder.getName()));
		
		log.info("Quick search for wiki page in community");
		Assert.assertTrue(searchUi.quickResultsSearch(commName.substring(0,3), "Welcome to " + commName, "Wiki"));
		Assert.assertTrue(commUi.fluentWaitTextPresent("Welcome to " + commName));
		
		log.info("Quick search for forum in community");
		backToOverview();
		Assert.assertTrue(searchUi.quickResultsSearch(forum.getName().substring(0,3), forum.getName(), "Forum"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(forum.getName()));
		
		log.info("Quick search for forum topic in community");
		backToOverview();
		Assert.assertTrue(searchUi.quickResultsSearch(forumTopic.getTitle().substring(0,3), forumTopic.getTitle(), "Forum"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(forumTopic.getTitle()));
		
		log.info("Quick search for activity in community");
		backToOverview();
		Assert.assertTrue(searchUi.quickResultsSearch(baseActivity.getName().substring(0,3), baseActivity.getName(), "Activity"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(baseActivity.getGoal()));
		
		log.info("Quick search for activity to do in community");
		Assert.assertTrue(searchUi.quickResultsSearch(baseTodo.getTitle().substring(0,3), baseTodo.getTitle(), "Activity"));
		Assert.assertTrue(activitiesUi.fluentWaitTextPresent(baseTodo.getTitle()));
		
		log.info("Quick search for activity entry in community");
		Assert.assertTrue(searchUi.quickResultsSearch(baseActivityEntry.getTitle().substring(0,3), baseActivityEntry.getTitle(), "Activity"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(baseActivityEntry.getTitle()));
		
		backToOverview();
		log.info("Quick search for blog in community");
		Assert.assertTrue(searchUi.quickResultsSearch(commName.substring(0,3), commName, "Blog"));
		
		log.info("Quick search for blog entry in community");
		backToOverview();
		Assert.assertTrue(searchUi.quickResultsSearch(blogPost.getTitle().substring(0,3), blogPost.getTitle(), "Blog"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(blogPost.getTitle()));
		
		log.info("Quick search for ideation blog in community");
		backToOverview();
		Assert.assertTrue(searchUi.quickResultsSearch(idea.getTitle().substring(0,3), idea.getTitle(), "Blog"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(idea.getTitle()));
		
		log.info("Quick search for event in community");
		backToOverview();
		Assert.assertTrue(searchUi.quickResultsSearch(baseEvent.getName().substring(0,3), baseEvent.getName(), "Event"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(baseEvent.getName()));
		
		log.info("Quick search for bookmark in community");
		backToOverview();
		Assert.assertTrue(searchUi.quickResultsSearch(bookmark.getTitle().substring(0,3), bookmark.getTitle(), "Bookmark"));
		Assert.assertTrue(commUi.fluentWaitTextPresent(bookmark.getTitle()));
		
		commUi.logout();
		
	/*
	 * Cleanup
	 */

		commUi.loadComponent(Data.getData().ComponentCommunities, true);
		commUi.login(testUser1);
		
		log.info("INFO: Deleting community");
		
		apiCommHandler.deleteCommunity(mainCommunity);
		apiCommHandler.deleteCommunity(relatedCommunity);
		
		log.info("INFO: Deleted the community");
		commUi.endTest();
	
	}
	
	
	/**
	 * Test quick results for view action in files.
	 * Test steps are:
	 * 1. Population - login as user1 and create file, folder and file inside a folder.
	 * 2. View - login as user2 and click on the items in order to simulate view action
	 * 3. Search - type 3 first letters of the item in the search box and verify expected result appears
	 * 4. Cleanup - login as user1 and delete the created data
	 */
	@Test(groups = {"regressioncloud"} , enabled=false )
	public void quickResultsFileFolder(){
	
		User testUser1 = cfg.getUserAllocator().getUser();
		User testUser2 = cfg.getUserAllocator().getUser();
		testName = filesUi.startTest();
		
		/*
		 * Create file folder
		 */
		
		//Login user1
		filesUi.loadComponent(Data.getData().ComponentFiles);
		filesUi.login(testUser1);
		
		// New file1
		BaseFile file1 = new BaseFile.Builder(Data.getData().file1)
								.extension(".jpg")
								.rename("File1" + Helper.genDateBasedRand())
								.shareLevel(ShareLevel.EVERYONE)
								.build();
				
		// New file2
		BaseFile file2 = new BaseFile.Builder(Data.getData().file2)
								.extension(".jpg")
								.rename("File2" + Helper.genDateBasedRand())
								.shareLevel(ShareLevel.EVERYONE)
								.build();
						
		//Upload file
		log.info("INFO: Upload files");	
		file1.upload(filesUi);
		file2.upload(filesUi);
		
		String file1Title = "link=" + file1.getName();
		String file2Title = "link=" + file2.getName();

		//New folder
		BaseFolder folder = new BaseFolder.Builder("Folder " + testName + Helper.genDateBasedRand())
							   		 .description(Data.getData().FolderDescription)
							   		 .access(com.ibm.conn.auto.appobjects.base.BaseFolder.Access.PUBLIC)
							   		 .build();
		//Create new folder
		log.info("INFO: Create new folder");
		folder.create(filesUi);
				
		String folderTitle = "link=" + folder.getName();
				
		//add file to folder
		log.info("INFO: Add file to folder");
		file2.addToFolder(filesUi, folder);
		
		/*
		 * View file folder
		 */
		filesUi.logout();
		filesUi.loadComponent(Data.getData().ComponentFiles, true);
		filesUi.login(testUser2);
		filesUi.fluentWaitTextPresent("Files");
		String orgName = driver.getSingleElement("css=ul[class='lotusInlinelist lotusLinks'] li a[href*='orgprofiles/partnerPage']").getText();
		log.info("orgname is: " + orgName);
		filesUi.clickLinkWait("link=" + orgName + " Folders");
		driver.getFirstElement(folderTitle).click();
		filesUi.clickLink(FilesUIConstants.selectViewList);
		filesUi.clickLinkWait(file2Title);
		filesUi.fluentWaitElementVisible(FilesUIConstants.fileviewer_previewImage);
		closePreview();
		filesUi.clickLinkWait("link=" + orgName + " Files");
		filesUi.clickLinkWait(FilesUIConstants.selectViewList);
		filesUi.clickLinkWait(file1Title);
		filesUi.fluentWaitElementVisible(FilesUIConstants.fileviewer_previewImage);
		closePreview();
		
		/*
		 * Search file folder
		 */

		// Wait 40 seconds in order to allow data indexed which is mandatory in order to get the expected results in the search 
		filesUi.sleep(40000);
		
		searchUi.selectGlobalSearch("All Files");
		Assert.assertTrue(searchUi.quickResultsSearch(file1.getName().substring(0,3), file1.getName(), "File"));
		
		
		String selector = CommunitiesUIConstants.closeViewer;
		filesUi.fluentWaitElementVisible(selector);
		Assert.assertTrue(filesUi.fluentWaitTextPresent(file1.getName()), " Could not find file: " + file1.getName());
		
		closePreview();

		Assert.assertTrue(searchUi.quickResultsSearch(file2.getName().substring(0,3), file2.getName(), "File"));
		
		filesUi.fluentWaitElementVisible(selector);
		Assert.assertTrue(filesUi.fluentWaitTextPresent(file2.getName()), " Could not find file: " + file2.getName());
		closePreview();
		
		Assert.assertTrue(searchUi.quickResultsSearch(folder.getName().substring(0,3), folder.getName(), "File"));
		
		Assert.assertTrue(driver.isTextPresent(folder.getName()),
				" Found folder: " + folder.getName());
		
		/*
		 * Cleanup
		 */
		filesUi.logout();
		filesUi.loadComponent(Data.getData().ComponentFiles, true);
		filesUi.login(testUser1);
		
		log.info("Cleanup");
		
		filesUi.clickLinkWait(FilesUIConstants.MyFoldersLeftMenu);
		filesUi.fluentWaitTextPresent("Folders that you have created");
		driver.getFirstElement(folderTitle).click();
		filesUi.fluentWaitTextPresent(folder.getName());
		filesUi.clickLinkWait("css=button[id^='lconn_files_action_more']");
		driver.getFirstElement("css=td[id^='lconn_files_action_deletecollection_']").click();
		filesUi.fluentWaitTextPresent("Are you sure you want to delete this folder");
		filesUi.clickButton(Data.getData().buttonOK);
		filesUi.fluentWaitTextPresent("The folder was deleted");
		
		filesUi.clickLinkWait("link=My Files");
		
		filesUi.trash(file2);
		filesUi.trash(file1);
		filesUi.delete(file2);
		
		log.info("End Test " + testName);
	
		filesUi.endTest();
	}
	
	/**
	 * Test quick results for view action in Activities.
	 * 
	 * Test steps are:
	 * 1. Population - login as user1 and create activity, activity entry. activity to do.
	 * 2. View - login as user2 and click on the items in order to simulate view action
	 * 3. Search - type 3 first letters of the item in the search box and verify expected result appears
	 * 4. Cleanup - login as user1 and delete the created data
	 */
	@Test(groups = {"regressioncloud"} , enabled=false )
	public void quickResultsActivities() {
		
		User testUser1 = cfg.getUserAllocator().getUser();
		User testUser2 = cfg.getUserAllocator().getUser();
		String activitiesTab = "css=div ul li a[aria-label='Activities']";
		testName = activitiesUi.startTest();
		/**
		 * Create Activity
		 */
		ActivityMember member2 = new ActivityMember(ActivityRole.AUTHOR, testUser2, ActivityMember.MemberType.PERSON);
		
		BaseActivity activity = new BaseActivity.Builder(testName + Helper.genDateBasedRand())
												.tags(Data.getData().Start_An_Activity_InputText_Tags_Data + Helper.genDateBasedRand())
												.goal(Data.getData().commonDescription + testName)
												.addMember(member2)
												.build();
		
		activitiesUi.loadComponent(Data.getData().ComponentActivities);
		activitiesUi.login(testUser1);

		log.info("INFO: Creating new activity " + activity.getName());
		activitiesUi.clickLinkWait(ActivitiesUIConstants.activityTab);
		activity.create(activitiesUi);
		String activityTitle = "link=" + activity.getName();
		
		/**
		 * Create ToDo
		 */
		BaseActivityToDo toDo = BaseActivityToDo.builder("toDo" + testName + Helper.genDateBasedRandVal())
										.tags(Helper.genDateBasedRandVal())
										.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
										.addPerson(testUser2)
										.build();

		log.info("INFO: Create ToDo item");
		toDo.create(activitiesUi);
		
		/**
		 * Create Entry
		 */
		BaseActivityEntry entry = BaseActivityEntry.builder("entry" + testName + Helper.genDateBasedRandVal())
										.tags(Helper.genDateBasedRandVal())
										.description(Data.getData().commonDescription + testName)
										.build();
		
		log.info("INFO: Create Entry");
		activitiesUi.createEntry(entry);
		activitiesUi.logout();
		
		String activityTab = "css=li[id='myActivitiesTab'] a[class='lotusTab']";
		//Load the component and login as userB
		activitiesUi.loadComponent(Data.getData().ComponentActivities, true);
		activitiesUi.login(testUser2);
		activitiesUi.clickLinkWait(activityTab);
				
		/**
		 * View Activity
		 */
		log.info("INFO: Check if user B can see activity");
		driver.getFirstElement(activityTitle).click();
		
		/**
		 * View ToDo
		 */
		log.info("INFO: Check if user B can see activity to do");
		String todoRootUUID = activitiesUi.getEntryUUID(toDo);
		activitiesUi.expandEntry(todoRootUUID);
		
		/**
		 * View Entry
		 */
		log.info("INFO: Check if user B can see activity entry");
		String entryRootUUID = activitiesUi.getEntryUUID(entry);
		activitiesUi.expandEntry(entryRootUUID);
		
		/**
		 * Search activity
		 */
		
		// Wait 40 seconds in order to allow data indexed which is mandatory in order to get the expected results in the search 
		activitiesUi.sleep(40000);
		activitiesUi.selectGlobalSearch("All Activities");	
		
		log.info("Quick search for activity name");
		Assert.assertTrue(searchUi.quickResultsSearch(activity.getName().substring(0,3), activity.getName(), "Activity"));
		Assert.assertTrue(activitiesUi.fluentWaitTextPresent(activity.getName()),
				"ERROR: activity not present");

		log.info("Quick search for activity to do");
		Assert.assertTrue(searchUi.quickResultsSearch(toDo.getTitle().substring(0,3), toDo.getTitle(), "Activity"));		
		Assert.assertTrue(activitiesUi.fluentWaitTextPresent(toDo.getTitle()),
				"ERROR: todo not present");

		log.info("Quick search for activity entry");
		Assert.assertTrue(searchUi.quickResultsSearch(entry.getTitle().substring(0,3), entry.getTitle(), "Activity"));
		Assert.assertTrue(activitiesUi.fluentWaitTextPresent(entry.getTitle()),
				"ERROR: entry not present");
		
		activitiesUi.logout();
		
		/**
		 * Cleanup
		 */
		activitiesUi.loadComponent(Data.getData().ComponentActivities, true);
		activitiesUi.login(testUser1);
		
		log.info("INFO: Deleting activity");
		activitiesUi.clickLinkWait(activitiesTab);
		
		activity.delete(activitiesUi);
		
		log.info("INFO: Deleted the activity");
 		
		log.info("End Test " + testName);
		activitiesUi.logout();
		
		activitiesUi.endTest();
	}
	
	
	/**
	 * Test quick results for view action in Activity template.
	 * Test steps are:
	 * 1. Population - login as user1 and create activity template, activity template entry, activity template to do.
	 * 2. View - login as user2 and click on the items in order to simulate view action
	 * 3. Search - type 3 first letters of the item in the search box and verify expected result appears
	 * 4. Cleanup - login as user1 and delete the created data
	 */
	@Test(groups = {"regressioncloud"} , enabled=false )
	public void quickResultsActivityTemplate() {
	
		User testUser1 = cfg.getUserAllocator().getUser();
		User testUser2 = cfg.getUserAllocator().getUser();
		String TemplateTab = "css=div ul li a[aria-label='Activity Templates']";
		testName = activitiesUi.startTest();
		/**
		 * Create Activity Template
		 */
		BaseActivityTemplate template = new BaseActivityTemplate.Builder(testName + Helper.genDateBasedRand())
											.tags(Data.getData().Start_An_Activity_Template_InputText_Tags_Data + Helper.genDateBasedRand())
											.description("Description for " + testName + Helper.genDateBasedRand())
											.addMember(new ActivityMember(ActivityRole.OWNER, testUser2, ActivityMember.MemberType.PERSON))
											.build();
		activitiesUi.loadComponent(Data.getData().ComponentActivities);
		activitiesUi.login(testUser1);
		log.info("INFO: Create a new template");
		template.create(activitiesUi);
		
		String activityTemplateTitle = "link=" + template.getName();
		
		/**
		 * Create Activity Template ToDo
		 */
		BaseActivityToDo toDo = BaseActivityToDo.builder("toDo" + testName + Helper.genDateBasedRandVal())
												.tags(Helper.genDateBasedRandVal())
												.description(Data.getData().commonDescription + Helper.genDateBasedRandVal())
												.build();
		log.info("INFO: Create ToDo item");
		toDo.create(activitiesUi);
		activitiesUi.fluentWaitPresent(ActivitiesUIConstants.More_Actions);
		log.info("INFO: Select more link of the todo");
		List<Element> entries =  driver.getVisibleElements(ActivitiesUIConstants.moreLink);
		entries.get(0).click();
		
		/**
		 * Create Activity Template Entry
		 */
		BaseActivityEntry entry = BaseActivityEntry
				.builder("entry" + testName + Helper.genDateBasedRandVal())
				.tags(Helper.genDateBasedRandVal())
				.dateRandom()
				.description(Data.getData().commonDescription + testName)
				.addPerson(testUser2)
				.notifyMessage(Data.getData().commonComment).build();
		
		log.info("INFO: Create Entry");
		activitiesUi.createEntry(entry);
		
		//log out as user A
		activitiesUi.logout();

		/**
		 * View Activity Template 
		 */
		
		//Load the component and login as userB
		activitiesUi.loadComponent(Data.getData().ComponentActivities, true);
		activitiesUi.login(testUser2);
		activitiesUi.clickLinkWait(TemplateTab);
				
		/**
		 * View Activity Template
		 */
		log.info("INFO: Check if user B can see activity");
		driver.getFirstElement(activityTemplateTitle).click();
		
		/**
		 * View ToDo
		 */
		log.info("INFO: Check if user B can see activity to do");
		String todoRootUUID = activitiesUi.getEntryUUID(toDo);
		activitiesUi.expandEntry(todoRootUUID);
		
		/**
		 * View Entry
		 */
		log.info("INFO: Check if user B can see activity entry");
		String entryRootUUID = activitiesUi.getEntryUUID(entry);
		activitiesUi.expandEntry(entryRootUUID);
		
		/**
		 * Search Activity Template
		 */
		activitiesUi.selectGlobalSearch("All Activities");	
		
		// Wait 40 seconds in order to allow data indexed which is mandatory in order to get the expected results in the search 
		activitiesUi.sleep(40000);
		
		log.info("Quick search for activity template name");
		Assert.assertTrue(searchUi.quickResultsSearch(template.getName().substring(0,3), template.getName(), "Activity"));
		Assert.assertTrue(activitiesUi.fluentWaitTextPresent(template.getName()),
				"ERROR: activity not present");
		
		log.info("Quick search for activity template to do");
		Assert.assertTrue(searchUi.quickResultsSearch(toDo.getTitle().substring(0,3), toDo.getTitle(), "Activity"));
		Assert.assertTrue(activitiesUi.fluentWaitTextPresent(toDo.getTitle()),
				"ERROR: activity template to do not present");

		log.info("Quick search for activity entry");
		Assert.assertTrue(searchUi.quickResultsSearch(entry.getTitle().substring(0,3), entry.getTitle(), "Activity"));
		Assert.assertTrue(activitiesUi.fluentWaitTextPresent(entry.getTitle()),
				"ERROR: activity not present");
		
		//logout userB
		activitiesUi.logout();
		
		/**
		 * Cleanup
		 */

		activitiesUi.loadComponent(Data.getData().ComponentActivities, true);
		activitiesUi.login(testUser1);
		
		log.info("INFO: Deleting activity template");
		activitiesUi.clickLinkWait(TemplateTab);
		activitiesUi.deleteTemplate(template);
		
		activitiesUi.endTest();
	}
	
	
	private void view (String element){
		commUi.clickLinkWait("link=" + element);
		log.info(element + " viewed");
	}
	
	private void backToOverview(){
		Community_LeftNav_Menu.OVERVIEW.select(commUi);
	}
	
	private void closePreview() {
		String selector = CommunitiesUIConstants.closeViewer;
		
		if (!driver.isElementPresent(selector)) {
			selector = CommunitiesUIConstants.closeThumbnail;
		}
		
		commUi.getFirstVisibleElement(selector).click();
	}
	
}
	