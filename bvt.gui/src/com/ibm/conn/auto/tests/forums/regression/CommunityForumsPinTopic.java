package com.ibm.conn.auto.tests.forums.regression;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.ForumsUIConstants;
import org.testng.Assert;

import org.apache.abdera.model.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseForum;
import com.ibm.conn.auto.appobjects.base.BaseForumTopic;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIForumsHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_TabbedNav_Menu;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.ForumsUI;
import com.ibm.lconn.automation.framework.services.common.nodes.ForumTopic;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.forums.nodes.Forum;

public class CommunityForumsPinTopic extends SetUpMethods2 {
	private static Logger log = LoggerFactory.getLogger(CommunityForumsPinTopic.class);
	private ForumsUI ui;
	private CommunitiesUI comui;
	private TestConfigCustom cfg;
	private APICommunitiesHandler apiOwner;
	private APIForumsHandler apiForumsOwner;
	
	private User testUser1;
	private User testUser2;

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		cfg = TestConfigCustom.getInstance();
		// Load Users
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();

		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
		apiForumsOwner = new APIForumsHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()),
				testUser1.getPassword());
	}
	
	
	
	@BeforeMethod(alwaysRun = true)
	public void setUp() throws Exception {
		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ForumsUI.getGui(cfg.getProductName(), driver);
		comui = CommunitiesUI.getGui(cfg.getProductName(), driver);

	}
	
	/**
	 * return the exact community UUID
	 * @param commUUID -- like  "communityUuid=c122a3d7-f577-49dd-9911-294bcb822c13"
	 * @return -- c122a3d7-f577-49dd-9911-294bcb822c13
	 */
	private String getUUID(String commUUID){
		int start = commUUID.indexOf("=");
		if(start!=-1){
			commUUID = commUUID.substring(start +1);
		}
		log.info("commUUID : "+ commUUID);
		return commUUID;	
	}
	/**
	 * it returns the forums object(forum or topic)'s exact id, bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3
	 * @param id -- a forum/topic's id, like urn:lsid:ibm.com:forum:bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3
	 * @return string like 'bb3ba41b-c8d8-49b0-a0c7-e3c6d86353d3'
	 */
	public String getForumUUID(String id){
		log.info("id "+id);
		return id.substring(id.indexOf("forum")+6);
	}
	
	/**
	 * the element which is the tr whose class is pinnedTopic
	 * @param el -- the tr which is the tr which includes the topic
	 * @return true, if the css class contains pinnedTopic; false if not.
	 */
	private boolean isPinned(Element el){
		String cssClass = el.getAttribute("class");
		if(cssClass.contains(ForumsUIConstants.PinnedTopicCssClass))
			return true;
		else
			return false;
	}
	/**
	 * the element which is the tr whose class is lotusPinnedRow
	 * @param el -- the tr which is the tr which includes the topic
	 * @return true, if the css class contains lotusPinnedRow; false if not
	 */
	private boolean isHighlighted(Element el){
		String cssClass = el.getAttribute("class");
		if(cssClass.contains(ForumsUIConstants.HighlightedCssClass))
			return true;
		else
			return false;
	}
	/**
	 * 
	 * @param apiTopic
	 * @return true if the topic is at the top of UI; false if not.
	 */
	private boolean isAtTheTopOfUIView(ForumTopic apiTopic){
		log.info("VERIFY: the topic " + apiTopic.getTitle() + " is the first one :" + apiTopic.getId().toString());
		List<Element> topicList = driver.getElements(ForumsUIConstants.TopicTr);
		Assert.assertTrue(topicList.size()>0);
		Element firstElement = topicList.get(0);
		
		log.info("first Element is " + firstElement.getAttribute("uuid") +" "+ firstElement.getText());
		if(firstElement.getAttribute("uuid").equals(getForumUUID(apiTopic.getId().toString())))
			return true;
		else
			return false;
	}
	/**
	 * 
	 * @param commUUID -- "communityUuid=c122a3d7-f577-49dd-9911-294bcb822c13"
	 * @param apiTopicTitle
	 * @return
	 */
	private boolean isAtTheTopOfAllForumsFeedView(String commUUID, String apiTopicTitle){
		String url = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
						+ "/forums/atom/topics?"+commUUID;
		Entry firstEntry = apiForumsOwner.getTopicsFeed(url).get(0);
		if(firstEntry.getTitle().equals(apiTopicTitle))
			return true;
		else 
			return false;
	}
	/**
	 * it compare the first topic in the forum's topic feed with the one your provided,
	 * and return true if they are same; false if not.
	 * @param forumUUID --bc4fc65b-2373-4ee4-9323-73b4a9cb24e6
	 * @param apiTopicTitle -- topic's title
	 * @return true, if it's the first topic in a forum's topic feed
	 */
	private boolean isAtTheTopOfOneForumFeedView(String forumUUID, String apiTopicTitle){
		String url = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
						+ "/forums/atom/topics?forumUuid="+forumUUID;
		Entry firstEntry = apiForumsOwner.getTopicsFeed(url).get(0);
		if(firstEntry.getTitle().equals(apiTopicTitle))
			return true;
		else 
			return false;
	}
	 
	/**
	 * it gets all topics <tr> on current pages, and compare the uuid with the given one's uuid,
	 * and returns the sequence number in the topic list.
	 * it returns -1 if the topic is not found.
	 * @param apiTopic
	 * @return the sequence number on the UI list. it returns -1 if the topic is not found.
	 */
	private int getUIPosition(ForumTopic apiTopic){
		log.info("get the position of the topic " + apiTopic.getTitle() + ", whose ID:" + apiTopic.getId().toString());
		List<Element> topicList = driver.getElements(ForumsUIConstants.TopicTr);
		Assert.assertTrue(topicList.size()>0);
		int position = -1;
		for(int i=0; i<topicList.size(); i++){
			if(topicList.get(i).getAttribute("uuid").equals(getForumUUID(apiTopic.getId().toString()))){
				position = i;
				break;
			}
			
		}
		log.info("the position is " + position);
		Assert.assertTrue(position!=-1, "ERROR: can not find the topic: " + apiTopic.getTitle());
		return position;
	}
	/**
	 * 
	 * @param commUUID
	 * @param apiTopic
	 * @return
	 */
	private int getFeedPosition(String commUUID, ForumTopic apiTopic){
		
			String url = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+ "/forums/atom/topics?"+commUUID;
			List<Entry> entryList = apiForumsOwner.getTopicsFeed(url);
			Assert.assertTrue(entryList.size()>0);
			int position = -1;
			for(int i=0; i<entryList.size(); i++){
				if(entryList.get(i).getTitle().equals(apiTopic.getTitle())){
					position = i;
					break;
				}
				
			}
			Assert.assertTrue(position!=-1, "ERROR: can not find the topic: " + apiTopic.getTitle());
			return position;
		
	}
	/**
	 * 
	 * @param apiTopicList
	 */
	private void verifyAllTopicsOnUI(ArrayList<ForumTopic> apiTopicList){
		for(int i=0; i<apiTopicList.size(); i++){
			ForumTopic apiTopic = apiTopicList.get(i);
			String uuid = getForumUUID(apiTopic.getId().toString());
			boolean isTopicExists = driver.isElementPresent(ForumsUI.getTopicTr(uuid));
			Assert.assertTrue(isTopicExists, 
					"ERROR: the topic is not shown up: " + apiTopic.getTitle() +" : "+ apiTopic.getId().toString());
		}
	}
	/**
	 * 
	 * @param overviewURL -- url for the community's overview page
	 */
	private void navigateToForums(String overviewURL){
		log.info("use url to navigate to this community's overview page");
		driver.navigate().to(overviewURL);
		log.info("click Forums from the left navigation bar");
		Community_TabbedNav_Menu.FORUMS.select(comui,2);
		log.info("wait till the Answered Questions tab is shown up");
		ui.fluentWaitPresent(ForumsUIConstants.AnsweredQuestionsTab);
		
	}
	/**
	 * a random index, but not the latest one.
	 * @param totalNumber
	 * @return
	 */
	private int getPinnedIndex(int totalNumber){
		int index = (int)(Math.random()*totalNumber);
		if(index==totalNumber)
			index = index - 2;
		else if(index==totalNumber-1)
			index = index - 1;
		return index;
	}
	/**
	 * verify the topic is pinned, but not highlighted
	 * @param apiPinnedTopicID
	 */
	private void verifyTopicNotHighlighted(String apiPinnedTopicID){
		log.info("VERIFY: pinned topics is pinned, but NOT highlighted,");
		String uuid = getForumUUID(apiPinnedTopicID);
		Element pinnedTopicElement=driver.getFirstElement(ForumsUI.getTopicTr(uuid));
		Assert.assertTrue(isPinned(pinnedTopicElement),"ERROR: it's not pinned");
		Assert.assertFalse(isHighlighted(pinnedTopicElement),"ERROR: it's not highlighted");

	}
	/**
	 * verify the topic is pinned, and highlighted
	 * @param apiPinnedTopic
	 */
	private void verifyTopicHighlighted(String apiPinnedTopicID){
		log.info("VERIFY: pinned topics is pinned, and highlighted,");
		String uuid = getForumUUID(apiPinnedTopicID);
		Element pinnedTopicElement=driver.getFirstElement(ForumsUI.getTopicTr(uuid));
		Assert.assertTrue(isPinned(pinnedTopicElement),"ERROR: it's not pinned");
		Assert.assertTrue(isHighlighted(pinnedTopicElement),"ERROR: it's not highlighted");

	}
	/**
	 * verify the message shows up on the Topics tab
	 * @param expectedMsg
	 */
	private void verifyMessageOnTopicsTab(String expectedMsg){
		String actualMsg = driver.getSingleElement(ForumsUIConstants.MessageDivOnTopicsTab).getText();
		log.info("current message is" + actualMsg);
		Assert.assertEquals(actualMsg, expectedMsg);
	}
	/*
	 * verify there is no message on the Topics tab
	 */
	private void verifyNoMessageOnTopicsTab(){
		log.info("VERIFY: No messages shows under the Topics tab");
		Assert.assertFalse(driver.isElementPresent(ForumsUIConstants.MessageDivOnTopicsTab),
				"ERROR: there should not be any message");
	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - UI -- CommunityA (Including only one community forum and some pinned topics)
	 * <ul>
	 * <li><B>Info: </B>On-premise test only</li>
	 * <li><B>Info: </B>Verify UI for pin topics in a public community(including 1 forum),  as Anonymously. It's 1 of 3 tests for CommunityA</li>
	 * 
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to create 10 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Open browser, ANONYMOUSLY </li>
	 * <li><B>Steps: </B>Navigate to the community's overview page</li>
	 * 
	 * <li><B>Verify: </B>Public Community: 1 forum: Anonymous: Verify all community topics show up on the Topics tab</li>
	 * <li><B>Verify: </B>Public Community: 1 forum: Anonymous: Verify pinned topics is highlighted, and at the top of topic list.</li>
	 * <li><B>Verify: </B>Public Community: 1 forum: Anonymous: Verify no other messages shows under the Topics tab, except 'Join this community to start a topic.'</li>
	 * </ul>
	 */ 
	@Test(groups={"regression"})
	public void test1ForumAnonymousUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: prepare data: There is a public community(CommunityA) including only one community forum and some pinned topics");
		
		BaseCommunity communityA = new BaseCommunity.Builder("CommunityA " + testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.build();
		//create community	
		logger.strongStep("Use API to create community A");
		log.info("INFO: use API to create community A");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForumA = apiForumsOwner.getDefaultCommForum(getUUID(commUUIDA), communityA.getName());
		
		ArrayList<ForumTopic> apiTopicListA = new ArrayList<ForumTopic>();
		int topicNumberA = 10;
		
		logger.strongStep("Use API to create " + topicNumberA + " topics");
		log.info("INFO: use API to create " + topicNumberA + " topics");
		for(int i=0; i<topicNumberA; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )
														.tags(Data.getData().commonTag)
			  											.parentForum(apiForumA)
			  											.build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			
			apiTopicListA.add(apiTopic);
		}
		
		int topicIndexA = getPinnedIndex(topicNumberA);	
		ForumTopic apiPinnedTopic = apiTopicListA.get(topicIndexA);
		
		logger.strongStep("Use API to pin one of topics");
		log.info("INFO: use API to pin one of topics");
		apiForumsOwner.pinTopic(apiPinnedTopic);
		
		log.info("INFO: end with preparing communityA's data");
			
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+"/communities/service/html/communityoverview?" + commUUIDA;
		
		logger.strongStep("Open browser, ANANYMOUSLY");
		log.info("INFO: open browser, ANANYMOUSLY");
		ui.loadComponent(Data.getData().ComponentCommunities);	
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(apiCommunityA, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);	
		
		logger.strongStep("ANONYMOUS: Verify all community topics show up");
		log.info("INFO: ANONYMOUS: verify all community topics show up");
		verifyAllTopicsOnUI(apiTopicListA);
		
		logger.strongStep("ANONYMOUS: Verify pinned topics is highlighted, and at the top of topic list.");
		log.info("INFO: ANONYMOUS: verify pinned topics is highlighted, and at the top of topic list.");
		verifyTopicHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("ANONYMOUS: Verify the pinned topic is at the top in UI");
		log.info("INFO: ANONYMOUS: verify the pinned topic is at the top in UI");
		Assert.assertTrue(isAtTheTopOfUIView(apiPinnedTopic), 
				"ERROR: The pinned topic is not at the top: " + apiPinnedTopic.getTitle());
		
		String expectedMsg = "Log in to start a topic.";
		
		logger.strongStep("ANONYMOUS: Verify No other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		log.info("INFO: ANONYMOUS: verify No other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		verifyMessageOnTopicsTab(expectedMsg);
			
		ui.endTest();
		
	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - UI -- CommunityA (Including only one community forum and some pinned topics)
	 * <ul>
	 * <li><B>Info: </B>Verify UI for pin topics in a public community(including 1 forum),  as a Non_community-Member. It's 2 of 3 tests for CommunityA</li>
	 * 
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to create 10 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Open browser, Login as Non Community Member, </li>
	 * <li><B>Steps: </B>Navigate to the community's overview page</li>
	 * 
	 * <li><B>Verify: </B>Public Community: Non Community Member: Verify all community topics show up on the Topics tab</li>
	 * <li><B>Verify: </B>Public Community: Non Community Member: Verify pinned topics is highlighted, and at the top of topic list.</li>
	 * <li><B>Verify: </B>Public Community: Non Community Member: Verify no other messages shows under the Topics tab, except 'Log in to start a topic.'</li>
	 * </ul> 
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void test1ForumNonMemberAUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: prepare data: There is a public community(CommunityA) including only one community forum and some pinned topics");
		
		BaseCommunity communityA = new BaseCommunity.Builder("CommunityA " + testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.build();
		//create community
		logger.strongStep("Use API to create community A");
		log.info("INFO: use API to create community A");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForumA = apiForumsOwner.getDefaultCommForum(getUUID(commUUIDA), communityA.getName());
		
		ArrayList<ForumTopic> apiTopicListA = new ArrayList<ForumTopic>();
		int topicNumberA = 10;
		
		logger.strongStep("Use API to create " + topicNumberA + " topics");
		log.info("INFO: use API to create " + topicNumberA + " topics");
		for(int i=0; i<topicNumberA; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )	
														  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
			  											  .parentForum(apiForumA)
			  											  .build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			
			apiTopicListA.add(apiTopic);
		}
		
		int topicIndexA = getPinnedIndex(topicNumberA);	
		ForumTopic apiPinnedTopic = apiTopicListA.get(topicIndexA);
		
		logger.strongStep("Use API to pin one of topics");
		log.info("INFO: use API to pin one of topics");
		apiForumsOwner.pinTopic(apiPinnedTopic);
		
		log.info("INFO: end with preparing communityA's data");
			
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+"/communities/service/html/communityoverview?" + commUUIDA;
		
		//Load component and login 
		logger.strongStep("Open browser and login to Community as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Community as NON CommunityA member");
		ui.loadComponent(Data.getData().ComponentCommunities);			
		ui.login(testUser2);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(apiCommunityA, StartPageApi.OVERVIEW);
		}

		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		logger.strongStep("NON Community Member: Verify All community topics show up");
		log.info("INFO: NON Community Member: verify All community topics show up");
		verifyAllTopicsOnUI(apiTopicListA);
		
		logger.strongStep("NON Community Member: Verify pinned topics is highlighted, and at the top of topic list.");
		log.info("INFO: NON Community Member: verify pinned topics is highlighted, and at the top of topic list.");
		verifyTopicHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("NON Community Member: Verify the pinned topic is at the top in UI");
		log.info("INFO: NON Community Member: verify the pinned topic is at the top in UI");
		Assert.assertTrue(isAtTheTopOfUIView(apiPinnedTopic), 
				"ERROR: The pinned topic is not at the top: " + apiPinnedTopic.getTitle());
		
		String expectedMsg = "Join this community to start a topic.";
		
		logger.strongStep("NON Community Member: verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		log.info("INFO: NON Community Member: verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		verifyMessageOnTopicsTab(expectedMsg);
				
		ui.endTest();
		
	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - UI -- CommunityA (Including only one community forum and some pinned topics)
	 * <ul>	 
	 * <li><B>Info: </B>Verify UI for pin topics in a public community(including 1 forum),  as Community Member). It's 3 of 3 tests for CommunityA</li>
	 * 
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to create 10 topics, and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Open browser, Login as Non Community Member, </li>
	 * <li><B>Steps: </B>navigate to Overview page</li>
	 * <li><B>Steps: </B>Select Join this Community</li>
	 * <li><B>Steps: </B>click Forums from left navigation bar</li>
	 * 
	 * <li><B>Verify: </B>Public Community: Member: Verify all community topics show up on the Topics tab</li>
	 * <li><B>Verify: </B>Public Community: Member: Verify the pinned topic is highlighted, and at the top of topic list.</li>
	 * <li><B>Verify: </B>Public Community: Member: Verify no messages shows under the Topics tab</li>		
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void test1ForumAfterJoinCommunityUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: prepare data: There is a public community(CommunityA) including only one community forum and some pinned topics");
		
		BaseCommunity communityA = new BaseCommunity.Builder("CommunityA " + testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.build();
		//create community
		logger.strongStep("Use API to create community A");
		log.info("use API to create community A");
		Community apiCommunityA = communityA.createAPI(apiOwner);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		String commUUIDA = apiOwner.getCommunityUUID(apiCommunityA);
		
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForumA = apiForumsOwner.getDefaultCommForum(getUUID(commUUIDA), communityA.getName());
		
		ArrayList<ForumTopic> apiTopicListA = new ArrayList<ForumTopic>();
		int topicNumberA = 10;
		
		logger.strongStep("Use API to create " + topicNumberA + " topics");
		log.info("INFO: use API to create " + topicNumberA + " topics");
		for(int i=0; i<topicNumberA; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )
														.tags(Data.getData().commonTag + Helper.genDateBasedRand())
			  											.parentForum(apiForumA)
			  											.build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			
			apiTopicListA.add(apiTopic);
		}
		
		int topicIndexA = getPinnedIndex(topicNumberA);	
		ForumTopic apiPinnedTopic = apiTopicListA.get(topicIndexA);
		
		logger.strongStep("Use API to pin one of topics");
		log.info("INFO: use API to pin one of topics");
		apiForumsOwner.pinTopic(apiPinnedTopic);
		
		log.info("INFO: end with preparing communityA's data");			
		
		//Load component and login
		logger.strongStep("Open browser and log in to Community as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and log in to Community as NON CommunityA member");
		ui.loadComponent(Data.getData().ComponentCommunities);		
		ui.login(testUser2);

		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())  
							+ "/communities/service/html/communityoverview?" + commUUIDA;
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(apiCommunityA, StartPageApi.OVERVIEW);
		}

		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		logger.strongStep("Select join this community");
		log.info("INFO: select join this community");
		try{
			comui.joinCommunity(communityA.getName());
		}catch(Exception e){
			Assert.assertTrue(false,"ERROR: Failed to click join this community: "+communityA.getName());
			e.printStackTrace();
		}
		
		logger.strongStep("Click Forums from the left navigation bar");
		log.info("INFO: click Forums from the left navigation bar");
		Community_TabbedNav_Menu.FORUMS.select(comui,2);
		
		logger.strongStep("Verify after Join this Community, All community topics show up");
		log.info("INFO: verify after Join this Community, All community topics show up");
		verifyAllTopicsOnUI(apiTopicListA);
		
		logger.strongStep("Verify after Join this Community, pinned topics is highlighted, and at the top of topic list.");
		log.info("INFO: verify after Join this Community, pinned topics is highlighted, and at the top of topic list.");
		verifyTopicHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("Verify after Join this Community, the pinned topic is at the top in UI");
		log.info("INFO: verify after Join this Community, the pinned topic is at the top in UI");
		Assert.assertTrue(isAtTheTopOfUIView(apiPinnedTopic), 
				"ERROR: The pinned topic is not at the top: " + apiPinnedTopic.getTitle());
		
		logger.strongStep("Verify after Join this Community, No other messages shows under the Topics tab");
		log.info("INFO: verify after Join this Community, No other messages shows under the Topics tab");
		verifyNoMessageOnTopicsTab();
		
		ui.endTest();
		
	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - UI -- CommunityB(a pulic community including more than one community forums and some pinned topics)
	 * <ul>
	 * <li><B>Info: </B>On-premise test only</li>
	 * <li><B>Info: </B>Verify UI for pin topics in a public community(including 2 forums), as anonymous user. It's 1 of 3 tests </li>
	 * 
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum and add it to the Forum list</li>
	 * <li><B>Steps: </B>Use API to create a new community forum and add it to the Forum list</li>
	 * <li><B>Steps: </B>Use API to create 10 topics in the new community forum and add it to the Topic list</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Open browser, ANONYMOUSLY </li>
	 * <li><B>Steps: </B>Navigate to the community's overview page</li>
	 * 
	 * <li><B>Verify: </B>Public Community: Anonymous: Verify all community topics show up on the Topics tab</li>
	 * <li><B>Verify: </B>Public Community: Anonymous: Verify the pinned topic is NOT highlighted, </li>
	 * <li><B>Verify: </B>Public Community: Anonymous: Verify the pinned topic is NOT at the top of topic list.</li>
	 * <li><B>Verify: </B>Public Community: Anonymous: Verify no other messages shows under the Topics tab, except 'Displaying topics from 2 forums. Log in to start a topic.'</li>
	 * </ul>
	 */
	@Test(groups={"regression"})
	public void test2ForumsAnonymousUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: There is a pulic community(CommunityB) including more than one community forums and some pinned topics");
		
		BaseCommunity communityB = new BaseCommunity.Builder("CommunityB " + testName + Helper.genDateBasedRand())
									.tags(Data.getData().commonTag + Helper.genDateBasedRand())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									.access(Access.PUBLIC)
									.description("Test description for testcase " + testName + Helper.genDateBasedRand())									
									.build();
		//create community
		logger.strongStep("Use API to create CommuityB");
		log.info("INFO: use API to create CommuityB");
		Community apiCommunityB = communityB.createAPI(apiOwner);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		String commUUIDB = apiOwner.getCommunityUUID(apiCommunityB);		
		
		ArrayList<Forum> apiForumListB = new ArrayList<Forum>();
		
		logger.strongStep("Get default community forum Using API and add it to the Forum list");
		log.info("INFO: get default community forum Using API and add it to the Forum list");
		apiForumListB.add(apiForumsOwner.getDefaultCommForum(getUUID(commUUIDB), communityB.getName()));
		
		String urlToCreateCommunityForums = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUIDB;		
		int forumNumber = 2;	
		
		logger.strongStep("Use API to create " + forumNumber + "community forum and add it to the Forum list");
		log.info("INFO: use API to create " + forumNumber + "community forum and add it to the Forum list");
		for(int i=1; i<forumNumber; i++){
			BaseForum forum2 = new BaseForum.Builder("forum "+ i)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.description("testing")
											.build();
			apiForumListB.add(apiForumsOwner.createCommunityForum(urlToCreateCommunityForums, forum2));
		}
		
		logger.strongStep("Select the latest created forum to start creating topics");
		log.info("INFO: select the latest created forum to start creating topics");
		int forumIndexB = forumNumber-1;
		Forum apiForumB = apiForumListB.get(forumIndexB);
		
		ArrayList<ForumTopic> apiTopicListB = new ArrayList<ForumTopic>();
		int topicNumberB = 10;
		
		logger.strongStep("Use API to create " + topicNumberB + " topics and add it to the topic list");
		log.info("INFO: use API to create " + topicNumberB + " topics and add it to the topic list");
		for(int i=0; i<topicNumberB; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )	
														  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
														  .parentForum(apiForumB)
														  .build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			apiTopicListB.add(apiTopic);
		}
		
		int topicIndex = getPinnedIndex(topicNumberB);		
		ForumTopic apiPinnedTopic = apiTopicListB.get(topicIndex);
		
		logger.strongStep("Use API to pin one of topics");
		log.info("INFO: use API to pin one of topics");
		apiForumsOwner.pinTopic(apiPinnedTopic);
		
		log.info("INFO: end with preparing communityB's data");
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+ "/communities/service/html/communityoverview?" + commUUIDB;
		
		logger.strongStep("Open browser, ANANYMOUSLY");
		log.info("INFO: open browser, ANANYMOUSLY");
		ui.loadComponent(Data.getData().ComponentCommunities);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(apiCommunityB, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);		
		
		logger.strongStep("Anonymous: Verify all community topics show up");
		log.info("INFO: Anonymous: verify all community topics show up");
		verifyAllTopicsOnUI(apiTopicListB);
		
		logger.strongStep("Anonymous: Verify pinned topics is not highlighted, and not at the top of topic list.");
		log.info("INFO: Anonymous: verify pinned topics is not highlighted, and not at the top of topic list.");
		verifyTopicNotHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("Anonymous: Verify the pinned topic is not at the top in UI");
		log.info("INFO: Anonymous: verify the pinned topic is not at the top in UI");
		Assert.assertFalse(isAtTheTopOfUIView(apiPinnedTopic), 
				"ERROR: The pinned topic is at the top: " + apiPinnedTopic.getTitle());
		
		String expectedMsg = "Displaying topics from "+ forumNumber +" forums. Log in to start a topic.";
		
		logger.strongStep("Anonymous: Verify No other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		log.info("INFO: Anonymous: verify No other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		verifyMessageOnTopicsTab(expectedMsg);
				
		ui.endTest();
		
	
	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - UI -- CommunityB(a pulic community including more than one community forums and some pinned topics)
	 * <ul>
	 * <li><B>Info: </B>Verify UI for pin topics in a public community(including 2 forums), as a Non community member. It's 2 of 3 tests </li>
	 * 
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum and add it to the Forum list</li>
	 * <li><B>Steps: </B>Use API to create a new community forum and add it to the Forum list</li>
	 * <li><B>Steps: </B>Use API to create 10 topics in the new community forum and add it to the Topic list</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Open browser, Log in as NON-Community-Member </li>
	 * <li><B>Steps: </B>Navigate to the community's overview page</li>
	 * 
	 * <li><B>Verify: </B>Public Community: Non Community Member: Verify all community topics show up on the Topics tab</li>
	 * <li><B>Verify: </B>Public Community: Non Community Member: Verify the pinned topic is NOT highlighted, </li>
	 * <li><B>Verify: </B>Public Community: Non Community Member: Verify the pinned topic is NOT at the top of topic list.</li>
	 * <li><B>Verify: </B>Public Community: Non Community Member: Verify no other messages shows under the Topics tab, except 'Displaying topics from 2 forums. Join this community to start a topic.'</li>	
	 * </ul>	
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void test2ForumsNonMemberUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: There is a pulic community(CommunityB) including more than one community forums and some pinned topics");

		BaseCommunity communityB = new BaseCommunity.Builder("CommunityB " + testName + Helper.genDateBasedRand())
									.tags(Data.getData().commonTag + Helper.genDateBasedRand())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									.access(Access.PUBLIC)
									.description("Test description for testcase " + testName + Helper.genDateBasedRand())									
									.build();
		//create community	
		logger.strongStep("Use API to create CommuityB");
		log.info("INFO: use API to create CommuityB");
		Community apiCommunityB = communityB.createAPI(apiOwner);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		String commUUIDB = apiOwner.getCommunityUUID(apiCommunityB);		
		
		ArrayList<Forum> apiForumListB = new ArrayList<Forum>();	

		logger.strongStep("Get default community forum Using API and add it to the forum list");
		log.info("INFO: get default community forum Using API add it to the forum list");
		apiForumListB.add(apiForumsOwner.getDefaultCommForum(getUUID(commUUIDB), communityB.getName()));
		
		String urlToCreateCommunityForums = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUIDB;		
		int forumNumber = 2;
		
		logger.strongStep("Use API to create " + forumNumber + "community forum and add it to the Forum list");
		log.info("INFO: use API to create " + forumNumber + "community forum and add it to the Forum list");
		for(int i=1; i<forumNumber; i++){
			BaseForum forum2 = new BaseForum.Builder("forum "+ i)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.description("testing")
											.build();
			apiForumListB.add(apiForumsOwner.createCommunityForum(urlToCreateCommunityForums, forum2));
		}
		
		logger.strongStep("Select the latest created forum to start creating topics");
		log.info("INFO: select the latest created forum to start creating topics");
		int forumIndexB = forumNumber-1;
		Forum apiForumB = apiForumListB.get(forumIndexB);
		
		ArrayList<ForumTopic> apiTopicListB = new ArrayList<ForumTopic>();
		int topicNumberB = 10;
		
		logger.strongStep("Use API to create " + topicNumberB + " topics and add it to the Topic list");
		log.info("INFO: use API to create " + topicNumberB + " topics and add it to the Topic list");
		for(int i=0; i<topicNumberB; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )	
														  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
														  .parentForum(apiForumB)
														  .build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			apiTopicListB.add(apiTopic);
		}
		
		int topicIndex = getPinnedIndex(topicNumberB);		
		ForumTopic apiPinnedTopic = apiTopicListB.get(topicIndex);
		
		logger.strongStep("Use API to pin one of topics");
		log.info("INFO: use API to pin one of topics");
		apiForumsOwner.pinTopic(apiPinnedTopic);
		
		log.info("INFO: end with preparing communityB's data");
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+ "/communities/service/html/communityoverview?" + commUUIDB;
		
		//Load component and login
		logger.strongStep("Open browser and login to Community as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Community as non CommunityA member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(apiCommunityB, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		logger.strongStep("NON Community Member: Verify all community topics show up");
		log.info("INFO: NON Community Member: verify all community topics show up");
		verifyAllTopicsOnUI(apiTopicListB);
		
		logger.strongStep("NON Community Member: verify pinned topics is highlighted, and at the top of topic list.");
		log.info("INFO: NON Community Member: verify pinned topics is highlighted, and at the top of topic list.");
		verifyTopicNotHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("NON Community Member: Verify the pinned topic is not at the top in UI");
		log.info("INFO: NON Community Member: verify the pinned topic is not at the top in UI");
		Assert.assertFalse(isAtTheTopOfUIView(apiPinnedTopic), "ERROR: The pinned topic is at the top: " + apiPinnedTopic.getTitle());
		
		String expectedMsg = "Displaying topics from "+ forumNumber +" forums. Join this community to start a topic.";
		
		logger.strongStep("NON Community Member: verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		log.info("INFO: NON Community Member: verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		verifyMessageOnTopicsTab(expectedMsg);
		
				
		ui.endTest();
		
	
	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - UI -- CommunityB(a pulic community including more than one community forums and some pinned topics)
	 * <ul>
	 * <li><B>Info: </B>Verify UI for pin topics in a public community(including 2 forums), as a member. It's 3 of 3 tests </li>
	 * 
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum and add it to the Forum list</li>
	 * <li><B>Steps: </B>Use API to create a new community forum and add it to the Forum list</li>
	 * <li><B>Steps: </B>Use API to create 10 topics in the new community forum and add it to the Topic list</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Open browser, Log in NON-Community-Member </li>
	 * <li><B>Steps: </B>navigate to Overview page</li>
	 * <li><B>Steps: </B>Click Join this Community</li>
	 * <li><B>Steps: </B>click Forums from left navigation</li>
	 * 
	 * <li><B>Verify: </B>Public Community: Member: Verify all community topics show up on the Topics tab</li>
	 * <li><B>Verify: </B>Public Community: Member: Verify the pinned topic is NOT highlighted, </li>
	 * <li><B>Verify: </B>Public Community: Member: Verify the pinned topic is NOT at the top of topic list.</li>
	 * <li><B>Verify: </B>Public Community: Member: Verify no other messages shows under the Topics tab, except 'Displaying topics from 2 forums.'</li>
     * </ul>	 
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void test2ForumsAfterJoinsCommunityUI3(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: There is a pulic community(CommunityB) including more than one community forums and some pinned topics");
		
		BaseCommunity communityB = new BaseCommunity.Builder("CommunityB " + testName + Helper.genDateBasedRand())
									.tags(Data.getData().commonTag + Helper.genDateBasedRand())
									.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									.access(Access.PUBLIC)
									.description("Test description for testcase " + testName + Helper.genDateBasedRand())									
									.build();
		//create community
		logger.strongStep("Use API to create CommuityB");
		log.info("INFO: use API to create CommuityB");
		Community apiCommunityB = communityB.createAPI(apiOwner);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		String commUUIDB = apiOwner.getCommunityUUID(apiCommunityB);		
		
		ArrayList<Forum> apiForumListB = new ArrayList<Forum>();

		logger.strongStep("Get default community forum Using API and add it to the Forum list");
		log.info("INFO: get default community forum Using API and add it to the Forum list");
		apiForumListB.add(apiForumsOwner.getDefaultCommForum(getUUID(commUUIDB), communityB.getName()));
		
		String urlToCreateCommunityForums = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUIDB;		
		int forumNumber = 2;
		
		logger.strongStep("Use API to create " + forumNumber + "community forum and add it to the Forum list");
		log.info("INFO: use API to create " + forumNumber + "community forum and add it to the Forum list");
		for(int i=1; i<forumNumber; i++){
			BaseForum forum2 = new BaseForum.Builder("forum "+ i)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.description("testing")
											.build();
			apiForumListB.add(apiForumsOwner.createCommunityForum(urlToCreateCommunityForums, forum2));
		}
		
		logger.strongStep("Select the latest created forum to start creating topics");
		log.info("INFO: select the latest created forum to start creating topics");
		int forumIndexB = forumNumber-1;
		Forum apiForumB = apiForumListB.get(forumIndexB);
		
		ArrayList<ForumTopic> apiTopicListB = new ArrayList<ForumTopic>();
		int topicNumberB = 10;
		
		logger.strongStep("Use API to create " + topicNumberB + " topics and add it to the Topic list");
		log.info("INFO: use API to create " + topicNumberB + " topics and add it to the Topic list");
		for(int i=0; i<topicNumberB; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )	
														  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
														  .parentForum(apiForumB)
														  .build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			apiTopicListB.add(apiTopic);
		}
		
		int topicIndex = getPinnedIndex(topicNumberB);		
		ForumTopic apiPinnedTopic = apiTopicListB.get(topicIndex);
		
		logger.strongStep("Use API to pin one of topics");
		log.info("INFO: use API to pin one of topics");
		apiForumsOwner.pinTopic(apiPinnedTopic);
		
		log.info("INFO: end with preparing communityB's data");
		
		//Load component and login		
		logger.strongStep("Open browser and login to Community as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Community as non CommunityA member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())  
				+ "/communities/service/html/communityoverview?" + commUUIDB;
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);

		logger.strongStep("Select join this community");
		log.info("INFO: select join this community");
		try{
			comui.joinCommunity(communityB.getName());
		}catch(Exception e){
			Assert.assertTrue(false,"ERROR: Failed to click join this community");
			e.printStackTrace();
		}
		
		logger.strongStep("Click Forums from the left navigation bar");
		log.info("INFO: click Forums from the left navigation bar");
		Community_TabbedNav_Menu.FORUMS.select(comui,2);
		
		logger.strongStep("Verify after Join this Community, All community topics show up");
		log.info("INFO: verify after Join this Community, All community topics show up");
		verifyAllTopicsOnUI(apiTopicListB);
		
		logger.strongStep("Verify after Join this Community, pinned topics is NOT highlighted, and NOT at the top of topic list.");
		log.info("INFO: verify after Join this Community, pinned topics is NOT highlighted, and NOT at the top of topic list.");
		verifyTopicNotHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("Verify after Join this Community, the pinned topic is not at the top in UI");
		log.info("INFO: verify after Join this Community, the pinned topic is not at the top in UI");
		Assert.assertFalse(isAtTheTopOfUIView(apiPinnedTopic), "ERROR: The pinned topic is not at the top: " + apiPinnedTopic.getTitle());
		
		String expectedMsg = "Displaying topics from "+ forumNumber +" forums.";
		
		logger.strongStep("Verify after Join this Community, No other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		log.info("INFO: verify after Join this Community, No other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		verifyMessageOnTopicsTab(expectedMsg);
		
		ui.endTest();
			
	}
	/**
	 * 
	 * TEST CASE: COMMUNITY PIN TOPICS - UI -- CommunityC(a restricted community, including only one community forum and some pinned topics)
	 * <ul>
	 * <li><B>Info: </B>Verify UI for pin topics in a restricted community(including 1 forum), as a member</li>
	 * 
	 * <li><B>Steps: </B>Use API to create a community(having a community Member)</li>
	 * <li><B>Steps: </B>Use API to get default community forum</li>
	 * <li><B>Steps: </B>Use API to create 10 topics  and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Open browser, Log in as Community-Member </li>
	 * <li><B>Steps: </B>navigate to Overview page</li>
	 * <li><B>Steps: </B>click Forums from left navigation bar</li>
	 * 
	 * <li><B>Verify: </B>Restricted Community: Member: Verify all community topics show up on the Topics tab</li>
	 * <li><B>Verify: </B>Restricted Community: Member: Verify the pinned topic is highlighted, </li>
	 * <li><B>Verify: </B>Restricted Community: Member: Verify the pinned topic is at the top of topic list.</li>
	 * <li><B>Verify: </B>Restricted Community: Member: Verify no messages shows under the Topics tab</li>	
	 * </ul>
	 */	 
	@Test(groups={"regression", "regressioncloud"})	
	public void testCommunityCMemberUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: prepare data: There is a restricted community(CommunityC) including only one community forum and some pinned topics");
		
		BaseCommunity communityC = new BaseCommunity.Builder("CommunityC " + testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.RESTRICTED)
													.shareOutside(false)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.addMember(new Member(CommunityRole.MEMBERS, testUser2))
													.build();
		//create community
		logger.strongStep("Use API to create community C");
		log.info("INFO: use API to create community C");
		Community apiCommunityC = communityC.createAPI(apiOwner);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		String commUUIDC = apiOwner.getCommunityUUID(apiCommunityC);
		
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForumC = apiForumsOwner.getDefaultCommForum(getUUID(commUUIDC), communityC.getName());
		
		ArrayList<ForumTopic> apiTopicListC = new ArrayList<ForumTopic>();
		int topicNumberC = 10;
		
		logger.strongStep("Use API to create " + topicNumberC + " topics and add it to the topic list");
		log.info("INFO: use API to create " + topicNumberC + " topics and add it to the topic list");
		for(int i=0; i<topicNumberC; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		  											.parentForum(apiForumC)
		  											.build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			apiTopicListC.add(apiTopic);
		}
		
		int topicIndex = getPinnedIndex(topicNumberC);		
		ForumTopic apiPinnedTopic = apiTopicListC.get(topicIndex);
		
		logger.strongStep("Use API to pin one of topics");
		log.info("INFO: use API to pin one of topics");
		apiForumsOwner.pinTopic(apiPinnedTopic);
		
		log.info("INFO: end with preparing communityC's data");
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())  
							+ "/communities/service/html/communityoverview?" + commUUIDC;
		
		//Load component and login
		logger.strongStep("Open browser and login to Community C as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Community C as a member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(apiCommunityC, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
			
		logger.strongStep("Verify all community topics show up");
		log.info("INFO: verify all community topics show up");
		verifyAllTopicsOnUI(apiTopicListC);
		
		logger.strongStep("Verify pinned topics is highlighted, and at the top of topic list.");
		log.info("INFO: verify pinned topics is highlighted, and at the top of topic list.");
		verifyTopicHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("Verify the pinned topic is at the top in UI");
		log.info("INFO: verify the pinned topic is at the top in UI");
		Assert.assertTrue(isAtTheTopOfUIView(apiPinnedTopic), "ERROR: The pinned topic is not at the top: " + apiPinnedTopic.getTitle());
		
		logger.strongStep("Verify no other messages shows under the Topics tab");
		log.info("INFO: verify no other messages shows under the Topics tab");
		verifyNoMessageOnTopicsTab();
		
		ui.endTest();
		
	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - UI -- CommunityD
	 * <ul>
	 * <li><B>Info: </B>Verify UI for pin topics in a restricted community(including 3 forums), as a member</li>

	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum and add it to the Forum list</li>
	 * <li><B>Steps: </B>Use API to create a new community forum and add it to the Forum list</li>
	 * <li><B>Steps: </B>Use API to create 10 topics in the new community forum and add it to the Topic list</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Open browser, Log in as Community-Member </li>
	 * <li><B>Steps: </B>navigate to Overview page</li>
	 * <li><B>Steps: </B>click Forums from left navigation bar</li>
	 * 	
	 * <li><B>Verify: </B>Restricted Community: Member:  Verify all community topics show up on the Topics tab</li>
	 * <li><B>Verify: </B>Restricted Community: Member:  Verify the pinned topic is NOT highlighted, </li>
	 * <li><B>Verify: </B>Restricted Community: Member:  Verify the pinned topic is NOT at the top of topic list.</li>
	 * <li><B>Verify: </B>Restricted Community: Member:  Verify no other messages shows under the Topics tab, except 'Displaying topics from 2 forums.'</li>	
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})	
	public void testCommunityDMemberUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		log.info("INFO: There is a restricted community(CommunityD) including more than one community forums and some pinned topics");
		
		BaseCommunity communityD = new BaseCommunity.Builder("CommunityD "+ testName + Helper.genDateBasedRand())
												.tags(Data.getData().commonTag + Helper.genDateBasedRand())
												.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
												.access(Access.RESTRICTED)
												.shareOutside(false)
												.description("Test description for testcase " + testName + Helper.genDateBasedRand())
												.addMember(new Member(CommunityRole.MEMBERS, testUser2))
												.build();
		
		//create community	
		logger.strongStep("Use API to create community");
		log.info("INFO: use API to create community");
		Community apiCommunityD = communityD.createAPI(apiOwner);
		
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		String commUUIDD = apiOwner.getCommunityUUID(apiCommunityD);
		
		ArrayList<Forum> apiForumListD = new ArrayList<Forum>();
		
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		apiForumListD.add(apiForumsOwner.getDefaultCommForum(getUUID(commUUIDD), communityD.getName()));
		
		String urlD = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUIDD;		
		int forumNumberD = 3;
		
		logger.strongStep("Use API to create "+forumNumberD+" community forums and add it to the Forum list");
		log.info("INFO: use API to create "+forumNumberD+" community forums and add it to the Forum list");
		for(int i=1; i<forumNumberD; i++){
			BaseForum forum2 = new BaseForum.Builder("forum "+ i)
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.description("testing")
										.build();
			apiForumListD.add(apiForumsOwner.createCommunityForum(urlD, forum2));
		}
		
		logger.strongStep("Select the latest created forum to create topics");
		log.info("INFO: select the latest created forum to create topics");
		Forum apiForumD = apiForumListD.get(forumNumberD-1);//get the latest created forum to create the topics
		
		ArrayList<ForumTopic> apiTopicListD = new ArrayList<ForumTopic>();
		int topicNumberD = 10; // all numbers that will be post
		
		logger.strongStep("Use API to create " + topicNumberD + " topics and add it to the Topic list");
		log.info("INFO: use API to create " + topicNumberD + " topics and add it to the Topic list");
		for(int i=0; i<topicNumberD; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )	
														  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
														  .parentForum(apiForumD)
														  .build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);			
			apiTopicListD.add(apiTopic);
		}
		
		int topicIndex = getPinnedIndex(topicNumberD);		
		ForumTopic apiPinnedTopic = apiTopicListD.get(topicIndex);
		
		logger.strongStep("Use API to pin one of topics");
		log.info("INFO: use API to pin one of topics");
		apiForumsOwner.pinTopic(apiPinnedTopic);
		
		log.info("INFO: end with preparing communityC's data");
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())  
							+ "/communities/service/html/communityoverview?" + commUUIDD;
		
		//Load component and login
		logger.strongStep("Open browser and login to Community as: " + testUser2.getDisplayName());
		log.info("INFO: Open browser and login to Community C as a member");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser2);
		
		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comui.isHighlightDefaultCommunityLandingPage();
		if (flag) {
			apiOwner.editStartPage(apiCommunityD, StartPageApi.OVERVIEW);
		}
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		logger.strongStep("Verify all community topics show up");
		log.info("INFO: verify all community topics show up");
		verifyAllTopicsOnUI(apiTopicListD);
		
		logger.strongStep("Verify pinned topics is not highlighted, and at the top of topic list.");
		log.info("INFO: verify pinned topics is not highlighted, and at the top of topic list.");
		verifyTopicNotHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("Verify the pinned topic is not at the top in UI");
		log.info("INFO: verify the pinned topic is not at the top in UI");
		Assert.assertFalse(isAtTheTopOfUIView(apiPinnedTopic), "ERROR: The pinned topic is at the top: " + apiPinnedTopic.getTitle());
		
		String expectedMsg = "Displaying topics from "+ forumNumberD +" forums.";
		
		logger.strongStep("verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		log.info("INFO: verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		verifyMessageOnTopicsTab(expectedMsg);
		
		ui.endTest();
	
		
	}
	/**
	 * basic testcase for pin/unpin topics
	 * <ul>
	 * <li><B>Info: </B>Basic UI test for pin a topic and unpin a topic</li>
	 * <li><B>Steps: </B>use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum</li>
	 * <li><B>Steps: </B>Use API to create a topic</li>
	 * <li><B>Steps: </B>open a browser and Login</li>
	 * <li><B>Steps: </B>Navigate to the topic by the topic's url</li>
	 * <li><B>Steps: </B>click "Pin This Topic"</li>
	 * <li><B>Verify: </B>the successful pinned message is shown up</li>
	 * <li><B>Verify: </B>the Unpin This Topic link is shown up</li>
	 * 
	 * <li><B>Steps: </B>Click "Unpin This Topic"</li>
	 * <li><B>Verify: </B>the successful unpinned message is shown up</li>
	 * <li><B>Verify: </B>the Pin This Topic link is shown up</li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testPinUnPinUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
			
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
		
		BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("pin this topic" )
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.parentForum(apiForum)
													.build();
		
		logger.strongStep("Use API to create the topic");
		log.info("INFO: use API to create the topic");
		ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
		
		//Load component and login
		logger.strongStep("Open browser and login to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the topic");
		log.info("INFO: navigate to the topic");
		driver.navigate().to(apiTopic.getAlternateLink());
		
		logger.strongStep("Pin topic " + forumTopic1.getTitle());
		log.info("INFO: pin topic " + forumTopic1.getTitle());		
		ui.clickLinkWait(ForumsUIConstants.PinTopicLink);
		
		logger.strongStep("Verify the successful pinned message is shown up");
		log.info("INFO: verify the successful pinned message is shown up");		
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.PinnedSuccessfulMsg),
				"ERROR: the successful pinned message is not shown up");
		
		logger.strongStep("Verify Unpin This Topic link is shown up");
		log.info("INFO: verify Unpin This Topic link is shown up");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.UnPinTopicLink),
				"ERROR: the Unpin This Topic link is not shown up");
				
		logger.strongStep("Click Unpin this Topic link");
		log.info("INFO: click Unpin this Topic link");
		ui.clickLinkWait(ForumsUIConstants.UnPinTopicLink);
		
		logger.strongStep("Verify the successful unpinned message is shown up");
		log.info("INFO: verify the successful unpinned message is shown up");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.UnpinnedSuccessfulMsg),
				"ERROR: the successful unpinned message is not shown up");
		
		logger.strongStep("Verify Pin This Topic link is shown up");
		log.info("INFO: verify Pin This Topic link is shown up");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.PinTopicLink),
				"ERROR: the Pin This Topic link is not shown up");
		
		ui.endTest();
	}
	
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - MORE THAN ONE PAGE
	 * <ul>
	 * <li><B>Info: </B>It's 1 of 4 tests for More than one page.</li>
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum</li>
	 * <li><B>Steps: </B>Use API to create 26 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin the oldest topic</li>
	 * <li><B>Steps: </B>Open browser,and Login </li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>
	 * <li><B>Verify: </B>Verify the pinned topic is  at the top from UI.</li>
	 * <li><B>Verify: </B>Verify the pinned topic is highlighted.</li>
	 * <li><B>Verify: </B>Verify other topics are on more than one page.(there is a paging bar).</li>
	 * <li><B>Verify: </B>Verify the pinned topic is the first topic in the feed.</li>
	 * </ul> 
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testMoreThanOnePage_PinIstheFirstAndHighlighted(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
		
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
		
		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumber = 26;
		
		logger.strongStep("Use API to create 26 topics(default page size is 25, 26 will be on two pages) and add it to Topic list");
		log.info("INFO: use API to create 26 topics(default page size is 25, 26 will be on two pages) and add it to Topic list");
		for(int i=0; i<topicNumber; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )				  
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		  											.parentForum(apiForum)
		  											.build();
			apiTopicList.add(forumTopic1.createAPI(apiForumsOwner));
		}
				
		ForumTopic pinnedOldestTopic = apiTopicList.get(0);
		log.info("INFO: the oldest topic is " + pinnedOldestTopic.getTitle() + ", which will be pinned");
		
		ForumTopic latestTopic = apiTopicList.get(topicNumber -1);
		log.info("INFO: the latest topic is " + latestTopic.getTitle());

		logger.strongStep("use API to pin the oldest topic " + pinnedOldestTopic.getTitle());
		log.info("INFO: use API to pin the oldest topic " + pinnedOldestTopic.getTitle());
		apiForumsOwner.pinTopic(pinnedOldestTopic);
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+"/communities/service/html/communityoverview?"+commUUID;

		//Load component and login
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		logger.strongStep("Verify the pinned topic is at the top in UI");
		log.info("INFO: verify the pinned topic is at the top in UI");
		Assert.assertTrue(isAtTheTopOfUIView(pinnedOldestTopic), 
				"ERROR: The pinned topic is not at the top: " + pinnedOldestTopic.getTitle());		
		
		logger.strongStep("Verify the pinned topic is highlighted");
		log.info("INFO: verify the pinned topic is highlighted");		
		verifyTopicHighlighted(pinnedOldestTopic.getId().toString());
		
		logger.strongStep("verify other topics are on more than one page");
		log.info("INFO: verify other topics are on more than one page");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.PagingBar), "ERROR: Paging bar not shown");
		
		logger.strongStep("Verify the pinned topic is the first topic in the feed");
		log.info("INFO: verify the pinned topic is the first topic in the feed");
		Assert.assertTrue(isAtTheTopOfAllForumsFeedView(commUUID,pinnedOldestTopic.getTitle()),
				 "ERROR: in feed, pinned topic is not at the top");
			
		ui.endTest();
		
		
	}

	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - MORE THAN ONE PAGE
	 * <ul>
	 * <li><B>Info: </B>It's 2 of 4 tests for More than one page.</li>
	 
	 * <li><B>Steps: </B>Use API to create a community </li>
	 * <li><B>Steps: </B>Use API to get default community forum</li>
	 * <li><B>Steps: </B>Use API to create 26 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin the oldest topic</li>
	 * <li><B>Steps: </B>Use API to create another community forum.</li>
	 * <li><B>Steps: </B>Open browser and Login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>	
	 * <li><B>Verify: </B>TWO forums: Verify message 'Displaying topics from 2 forums' shown under the Topics tab."</li>
	 * <li><B>Verify: </B>TWO forums: Verify the latest created topic is at the top on the Topics tab</li>
	 * <li><B>Verify: </B>TWO forums: Verify the latest created topic is at the top of Feed</li>
	 * <li><B>Steps: </B>click Page 2 from paging bar</li>
	 * <li><B>Verify: </B>TWO forums: Verify the pinned topic is NOT highlighted.</li>	 	
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testMoreThanOnePage_2Forums_PinnedButNotLightedNotFirst(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
		
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
		
		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumber = 26;
		
		logger.strongStep("Use API to create 26 topics(default page size is 25, 26 will be on two pages) and add it to Topic list");
		log.info("INFO: use API to create 26 topics(default page size is 25, 26 will be on two pages) and add it to Topic list");		
		for(int i=0; i<topicNumber; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )				  
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		  											.parentForum(apiForum)
		  											.build();
			apiTopicList.add(forumTopic1.createAPI(apiForumsOwner));
		}
				
		ForumTopic pinnedOldestTopic = apiTopicList.get(0);
		log.info("INFO: the oldest topic is " + pinnedOldestTopic.getTitle() + ", which will be pinned");
		
		ForumTopic latestTopic = apiTopicList.get(topicNumber -1);
		log.info("INFO: the latest tpic is " + latestTopic.getTitle());

		logger.strongStep("Use API to pin the oldest topic " + pinnedOldestTopic.getTitle());
		log.info("INFO: use API to pin the oldest topic " + pinnedOldestTopic.getTitle());
		apiForumsOwner.pinTopic(pinnedOldestTopic);
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+"/communities/service/html/communityoverview?"+commUUID;

		BaseForum forum2 = new BaseForum.Builder("second forum")
				 						.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.description("testing")
										.build();
		String forumsURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUID;
		
		logger.strongStep("Use API to create the second forum");
		log.info("INFO: use API to create the second forum");
		apiForumsOwner.createCommunityForum(forumsURL, forum2);

		//Load component and login
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		String expectedMsg = "Displaying topics from 2 forums.";
		
		logger.strongStep("Verify message 'Displaying topics from 2 forums' shown under the Topics tab.");
		log.info("INFO: verify message 'Displaying topics from 2 forums' shown under the Topics tab.");
		verifyMessageOnTopicsTab(expectedMsg);
		
		logger.strongStep("Verify the latest created topic is at the top on the Topics tab");
		log.info("INFO: verify the latest created topic is at the top on the Topics tab");
		Assert.assertTrue(isAtTheTopOfUIView(latestTopic), 
							"ERROR: the created latest topic is not at the top on the Topics tab");
		
		logger.strongStep("Verify the latest created topic is at the top of Feed");
		log.info("INFO: verify the latest created topic is at the top of Feed");
		Assert.assertTrue(isAtTheTopOfAllForumsFeedView(commUUID,latestTopic.getTitle()),
							"ERROR: the created lastest topic is not at the top on the Feed");
		
		log.info("click to go to the second page");
		driver.getFirstElement(ForumsUIConstants.Page2Link).click();
		
		logger.strongStep("Verify the topic is pinned but NOT highlighted");
		log.info("INFO: verify the topic is pinned but NOT highlighted");
		verifyTopicNotHighlighted(pinnedOldestTopic.getId().toString());	
						
		ui.endTest();		
		
	}

	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - MORE THAN ONE PAGE
	 * <ul>
	 * <li><B>Info: </B>It's 3 of 4 tests for More than one page.</li>
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum</li>
	 * <li><B>Steps: </B>Use API to create 26 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin the oldest topic</li>
	 * <li><B>Steps: </B>Use API to create another community forum.</li>
	 * <li><B>Steps: </B>use API to pin another topic which is not latest created</li>
	 * <li><B>Steps: </B>Open browser, and login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>
	 * <li><B>Steps: </B>Get the second topic which will be pinned and its UI position</li>
	 * <li><B>Verify: </B>TWO forums: Verify the new pinned topic is pinned but NOT highlighted</li>
	 * <li><B>Verify: </B>TWO forums: Verify the new pinned topic is still at the previous seat of the topic list.</li>	
	 * <li><B>Verify: </B>TWO forums: Verify Some messages show under the Topics tab, "Displaying topics from 2 forums."</li>
	 * <li><B>Verify: </B>TWO forums: Verify the latest created topic is at the top on the Topics tab</li>
	 * <li><B>Verify: </B>TWO forums: Verify the latest created topic is at the top of All Feed</li>	
	 * <li><B>Verify: </B>TWO forums: Verify the new pinned topic which is not the latest created, is not at the top of all Feed.</li>	 	
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testMoreThanOnePage_2PinnedTopics2Forums(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
		
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
		
		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumber = 26;
		
		logger.strongStep("use API to create 26 topics(default page size is 25, 26 will be on two pages) and add it to Topic list");
		log.info("INFO: use API to create 26 topics(default page size is 25, 26 will be on two pages) and add it to Topic list");
		for(int i=0; i<topicNumber; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		  											.parentForum(apiForum)
		  											.build();
			apiTopicList.add(forumTopic1.createAPI(apiForumsOwner));
		}
				
		ForumTopic pinnedOldestTopic = apiTopicList.get(0);
		log.info("INFO: the oldest topic is " + pinnedOldestTopic.getTitle() + ", which will be pinned");
		
		ForumTopic latestTopic = apiTopicList.get(topicNumber -1);
		log.info("INFO: the latest tpic is " + latestTopic.getTitle());

		logger.strongStep("Use API to pin the oldest topic " + pinnedOldestTopic.getTitle());
		log.info("INFO: use API to pin the oldest topic " + pinnedOldestTopic.getTitle());
		apiForumsOwner.pinTopic(pinnedOldestTopic);
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+"/communities/service/html/communityoverview?"+commUUID;
		
		BaseForum forum2 = new BaseForum.Builder("second forum")
										.description("testing")
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.build();
		String forumsURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUID;
		
		logger.strongStep("Use API to create the second forum");
		log.info("INFO: use API to create the second forum");
		apiForumsOwner.createCommunityForum(forumsURL, forum2);

		//Load component and login
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community ");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
				
		logger.strongStep("Get the second topic which will be pinned and its UI position");
		log.info("INFO: get the second topic which will be pinned and its UI position"); 
		ForumTopic apiSecondPinnedTopic = apiTopicList.get(topicNumber -2);
		int originalPosition = getUIPosition(apiSecondPinnedTopic);
					
		logger.strongStep("Use API to pin another topic which is not latest created");
		log.info("INFO: use API to pin another topic which is not latest created");		
		apiForumsOwner.pinTopic(apiSecondPinnedTopic);
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		logger.strongStep("Verify new pinned topic is pinned but NOT highlighted");
		log.info("INFO: verify new pinned topic is pinned but NOT highlighted");
		verifyTopicNotHighlighted(apiSecondPinnedTopic.getId().toString());
		
		logger.strongStep("verify the new pinned topic is still at the previous seat of the topic list.");		
		log.info("INFO: verify the new pinned topic is still at the previous seat of the topic list.");
		int newUIPosition = getUIPosition(apiSecondPinnedTopic);
		Assert.assertEquals(newUIPosition, originalPosition,
				"ERROR: the new pinned topic's position has been changed, not at the previous seat of the topic list");

				
		String expectedMsg = "Displaying topics from 2 forums.";
		
		logger.strongStep("Verify message'Displaying topics from 2 forums' shown under the Topics tab");
		log.info("INFO: verify message'Displaying topics from 2 forums' shown under the Topics tab");
		verifyMessageOnTopicsTab(expectedMsg);
		
		logger.strongStep("Verify the latest created topic is at the top of the Topics tab");
		log.info("INFO: verify the latest created topic is at the top of the Topics tab");
		Assert.assertTrue(isAtTheTopOfUIView(latestTopic), 
				"ERROR: the created latest topic is not at the top on the Topics tab");
		
		logger.strongStep("Verify the latest created topic is at the top of all Feed");
		log.info("INFO: verify the latest created topic is at the top of all Feed");
		Assert.assertTrue(isAtTheTopOfAllForumsFeedView(commUUID,latestTopic.getTitle()),
				"ERROR: the created lastest topic is not at the top of the Feed");
		
		logger.strongStep("Verify the new pinned topic which is not the latest created, is not at the top of all Feed");
		log.info("INFO: verify the new pinned topic which is not the latest created, is not at the top of all Feed");
		Assert.assertFalse(isAtTheTopOfAllForumsFeedView(commUUID,apiSecondPinnedTopic.getTitle()),
				"ERROR: the new pinned topic is not at the top on the Feed");
		
			
		ui.endTest();
				
	}

	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - MORE THAN ONE PAGE
	 * <ul>
	 * <li><B>Info: </B>It's 4 of 4 tests for More than one page.</li>
	 * <li><B>Info: </B>After delete other forum till one left, the pinned topic ui is as same as there is only one forum</li>
	 * <li><B>Steps: </B>Use API to create a community </li>
	 * <li><B>Steps: </B>Use API to get default community forum</li>
	 * <li><B>Steps: </B>Use API to create 26 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin the oldest topic</li>
	 * <li><B>Steps: </B>Use API to create another community forum.</li>
	 * <li><B>Steps: </B>Get the second topic which will be pinned and its UI position</li>
	 * <li><B>Steps: </B>use API to pin another topic which is not latest created</li>
	 * <li><B>Steps: </B>use API to delete the new forum</li>
	 * <li><B>Steps: </B>Open browser,and Login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>	
	 * <li><B>Verify: </B>Verify After delete the new forum, there is only one forum and the new pinned topic at the top from UI</li>
	 * <li><B>Verify: </B>Verify After delete the new forum, there is only one forum and the new pinned topic is highlighted</li>
	 * <li><B>Verify: </B>Verify After delete the new forum, there is only one forum and other topics are on more than one page.(paging bar with 2 pages)</li>
	 * <li><B>Verify: </B>Verify After delete the new forum, there is only one forum and the new pinned topic is the first topic in the feed</li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testMoreThanOnePage_1ForumLeft(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
		
		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
		
		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumber = 26;
		
		logger.strongStep("Use API to create 26 topics(default page size is 25, 26 will be on two pages) and add it to topic list");
		log.info("INFO: use API to create 26 topics(default page size is 25, 26 will be on two pages) and add it to topic list");		
		for(int i=0; i<topicNumber; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )				  
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
		  											.parentForum(apiForum)
		  											.build();
			apiTopicList.add(forumTopic1.createAPI(apiForumsOwner));
		}
				
		ForumTopic pinnedOldestTopic = apiTopicList.get(0);
		log.info("INFO: the oldest topic is " + pinnedOldestTopic.getTitle() + ", which will be pinned");
		
		ForumTopic latestTopic = apiTopicList.get(topicNumber -1);
		log.info("INFO: the latest tpic is " + latestTopic.getTitle());

		logger.strongStep("Use API to pin the oldest topic " + pinnedOldestTopic.getTitle());
		log.info("INFO: use API to pin the oldest topic " + pinnedOldestTopic.getTitle());
		apiForumsOwner.pinTopic(pinnedOldestTopic);
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+"/communities/service/html/communityoverview?"+commUUID;
	
		
		BaseForum forum2 = new BaseForum.Builder("second forum")
										.description("testing")
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.build();
		String forumsURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUID;
		
		logger.strongStep("Use API to create the second forum");
		log.info("INFO: use API to create the second forum");
		Forum apiForum2= apiForumsOwner.createCommunityForum(forumsURL, forum2);

		logger.strongStep("Get the second topic which will be pinned and its UI position");
		log.info("INFO: get the second topic which will be pinned and its UI position"); 
		ForumTopic apiSecondPinnedTopic = apiTopicList.get(topicNumber -2);
		
		logger.strongStep("Use API to pin another topic which is not latest created");
		log.info("INFO: use API to pin another topic which is not latest created");		
		apiForumsOwner.pinTopic(apiSecondPinnedTopic);
			
		logger.strongStep("Use API to delete the new forum");
		log.info("INFO: use API to delete the new forum");
		apiForumsOwner.deleteForum(apiForum2);
		
		//Load component and login
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community ");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		logger.strongStep("Verify after delete the new forum, there is only one forum and the new pinned topic at the top in UI");
		log.info("INFO: verify after delete the new forum, there is only one forum: the new pinned topic at the top in UI");
		Assert.assertTrue(isAtTheTopOfUIView(apiSecondPinnedTopic), 
				"ERROR: The new pinned topic is not at the top: " + apiSecondPinnedTopic.getTitle());		
		
		logger.strongStep("Verify after delete the new forum, there is only one forum and the new pinned topic is highlighted");
		log.info("INFO: verify after delete the new forum, there is only one forum: the new pinned topic is highlighted");
		verifyTopicHighlighted( apiSecondPinnedTopic.getId().toString());
		
		logger.strongStep("Verify after delete the new forum, there is only one forum and other topics are on more than one page");
		log.info("INFO: verify after delete the new forum, there is only one forum: other topics are on more than one page");
		Assert.assertTrue(driver.isElementPresent(ForumsUIConstants.PagingBar),
				"ERROR: Paging bar not shown");

		logger.strongStep("Verify after delete the new forum, there is only one forum and the new pinned topic is the first topic in the feed");
		log.info("INFO: verify after delete the new forum, there is only one forum:  the new pinned topic is the first topic in the feed");
		Assert.assertTrue(isAtTheTopOfAllForumsFeedView(commUUID, apiSecondPinnedTopic.getTitle()),
				 "ERROR: in feed, pinned topic is not at the top");

		
		ui.endTest();
				
	}

	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - MORE THAN ONE COMMUNITY FORUM
	 * <ul>
	 * <li><B>Info: </B>It's 1 of 5 tests for More than one community forum.</li>
	 * <li><B>Info: </B>verify pinned UI with 2 forums</li>
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum and add it to the Forum list</li>
	 * <li><B>Steps: </B>Use API to create 10 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Use API to create the second forum.</li>
	 * <li><B>Steps: </B>Open browser, and Login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>
	 
	 * <li><B>Verify: </B>2 Forums: Verify the pinned topic is NOT at the top from UI.</li>
	 * <li><B>Verify: </B>2 Forums: Verify the pinned topic is NOT highlighted.</li>
	 * <li><B>Verify: </B>2 Forums: Verify no other messages shows under the Topics tab, except "Displaying topics from 2 forums.".</li>
	 * <li><B>Verify: </B>2 Forums: Verify the pinned topic is not at the top from All Feed</li>	
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testMoreThanOneCommunityForum_2Forums(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>();
		
		logger.strongStep("Get default community forum Using API and add it to the Forum list");
		log.info("INFO: get default community forum Using API and add it to the Forum list");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
		apiForumList.add(apiForum);
		
		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumberD = 10;// all numbers that will be post
		
		logger.strongStep("Use API to create " + topicNumberD + " topics and add it to the Topic list");
		log.info("use API to create " + topicNumberD + " topics and add it to the Topic list");
		for(int i=0; i<topicNumberD; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )
														.tags(Data.getData().commonTag + Helper.genDateBasedRand())
														.parentForum(apiForum)
														.build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			
			apiTopicList.add(apiTopic);
		}
		
		int topicIndex = getPinnedIndex(topicNumberD);		
		ForumTopic apiPinnedTopic = apiTopicList.get(topicIndex);
		
		logger.strongStep("Use API to pin one of topics");
		log.info("use API to pin one of topics");
		apiForumsOwner.pinTopic(apiPinnedTopic);
							
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())  
							+ "/communities/service/html/communityoverview?" + commUUID;
		

		BaseForum forum2 = new BaseForum.Builder("second forum")
										.description("testing")
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.build();
		String urlToCreateCommunityForum = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUID;
		
		logger.strongStep("Use API to create the second community forum");
		log.info("use API to create the second community forum");
		apiForumsOwner.createCommunityForum(urlToCreateCommunityForum, forum2);
		
		//Load component and login 
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		logger.strongStep("Verify after create 2nd forum, the pinned topic is not highlighted, and not at the top of topic list.");
		log.info("INFO: verify after create 2nd forum, the pinned topic is not highlighted, and not at the top of topic list.");
		verifyTopicNotHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("Verify after create the 2nd forum, the pinned topic is not at the top in UI");
		log.info("INFO: verify after create the 2nd forum, the pinned topic is not at the top in UI");
		Assert.assertFalse(isAtTheTopOfUIView(apiPinnedTopic), 
				"ERROR: The pinned topic is not at the top: " + apiPinnedTopic.getTitle());
		
		String expectedMsg = "Displaying topics from 2 forums.";
		
		logger.strongStep("Verify after create the 2nd forum, no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");		
		log.info("INFO: verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		verifyMessageOnTopicsTab(expectedMsg);
		
		logger.strongStep("Verify after create the 2nd forum, the pinned topic is not at the top from All Feed");
		log.info("INFO: verify the pinned topic is not at the top from All Feed");
		Assert.assertFalse(isAtTheTopOfAllForumsFeedView(commUUID, apiPinnedTopic.getTitle()), 
				"ERROR: The pinned topic is at the top: " + apiPinnedTopic.getTitle());
		
		ui.endTest();	
		
	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - MORE THAN ONE COMMUNITY FORUM
	 * <ul>
	 * <li><B>Info: </B>It's 2 of 5 tests for More than one community forum.</li>
	 * <li><B>Info: </B>verify the updated topic is staying at the original position </li>
	 * <li><B>Steps: </B>Use API to create a community and create 10 topics.</li>
	 * <li><B>Steps: </B>Use API to get default community forum and add it to the Forum list</li>
	 * <li><B>Steps: </B>Use API to create a community and create 10 topics and add it to the Topic list</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Create the second forum in the community</li>
	 * <li><B>Steps: </B>Use API to create the second forum.</li>
	 * <li><B>Steps: </B>Update some topics in the default forum</li>
	 * <li><B>Steps: </B>Open browser, and Login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>
	 * <li><B>Verify: </B>2 Forums: Verify the pinned topic is NOT highlighted and not at the top of topic list</li>
	 * <li><B>Verify: </B>2 Forums: Verify the pinned topic is  NOT at the top from UI.</li>
	 * <li><B>Verify: </B>2 Forums: Verify no other messages shows under the Topics tab, except "Displaying topics from 2 forums.".</li>
	 * <li><B>Verify: </B>2 forums: Verify all Topics are sorted by Latest Posts -- the updated topic is staying at the original position</li>
	 * <li><B>Verify: </B>2 Forums: Verify the pinned topic is not at the top from All Feed</li>	
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testMoreThanOneCommunityForum_UpdateTopicInTheFirstForum(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>();
		
		logger.strongStep("Get default community forum Using API and add it to the Forum list");
		log.info("INFO: get default community forum Using API and add it to the Forum list");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
		apiForumList.add(apiForum);
		
		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumberD = 10;// all numbers that will be post
		
		logger.strongStep("Use API to create " + topicNumberD + " topics and add it to the Topic list");
		log.info("INFO: use API to create " + topicNumberD + " topics and add it to the Topic list");
		for(int i=0; i<topicNumberD; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )
														.tags(Data.getData().commonTag + Helper.genDateBasedRand())
														.parentForum(apiForum)
														.build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			
			apiTopicList.add(apiTopic);
		}
		
		int topicIndex = getPinnedIndex(topicNumberD);		
		ForumTopic apiPinnedTopic = apiTopicList.get(topicIndex);
		
		logger.strongStep("Step 2: Use API to pin one of topics");
		log.info("INFO: Step 2: use API to pin one of topics");
		apiForumsOwner.pinTopic(apiPinnedTopic);
							
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())  
							+ "/communities/service/html/communityoverview?" + commUUID;
		
		BaseForum forum2 = new BaseForum.Builder("second forum")
										.description("testing")
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.build();
		String urlToCreateCommunityForum = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUID;
		
		logger.strongStep("Step 4: Create the second forum in the community");
		log.info("INFO: Step 4: create the second forum in the community");
		apiForumsOwner.createCommunityForum(urlToCreateCommunityForum, forum2);

		logger.strongStep("Step 5: Update some topics in the previous forum");
		log.info("INFO: Step 5: Update some topics in the previous forum");
		int editIndex = getPinnedIndex(topicNumberD);
		if(editIndex==topicIndex)
			if(topicIndex==topicNumberD-1)
				editIndex = topicIndex -1;
			else
				editIndex = topicIndex + 1;
		ForumTopic apiEditTopic1 = apiTopicList.get(editIndex);
		
		//Load component and login 
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		int originalPosition = getUIPosition(apiEditTopic1);
		log.info("INFO: the current position of the updated topic is " + originalPosition);
		
		logger.strongStep("Use API to update the topic: "+ apiEditTopic1.getTitle());
		log.info("INFO: use API to update the topic: "+ apiEditTopic1.getTitle());
		apiForumsOwner.editTopic(apiEditTopic1, "edit in step 5");
		
		log.info("refresh the forums data after API operations");	
		
		
		logger.strongStep("Step 5: verify pinned topics is not highlighted, and not at the top of topic list.");
		log.info("INFO: Step 5: verify pinned topics is not highlighted, and not at the top of topic list.");
		verifyTopicNotHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("Verify the pinned topic is not at the top in UI");
		log.info("INFO: verify the pinned topic is not at the top in UI");
		Assert.assertFalse(isAtTheTopOfUIView(apiPinnedTopic), 
				"ERROR: The pinned topic is at the top: " + apiPinnedTopic.getTitle());
		
		String expectedMsg = "Displaying topics from 2 forums.";
		
		logger.strongStep("Verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		log.info("INFO: verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		verifyMessageOnTopicsTab(expectedMsg);
		
		logger.strongStep("Verify all Topics are sorted by Latest Posts -- the updated topic is staying at the original position");
		log.info("INFO: verify all Topics are sorted by Latest Posts -- the updated topic is staying at the original position");
		int newUIPosition = getUIPosition(apiEditTopic1);
		Assert.assertEquals(newUIPosition, originalPosition,
				"ERROR: the new pinned topic's position has been changed, not at the previous seat of the topic list");
		
		logger.strongStep("Step 5: verify the pinned topic is not at the top from Feed");
		log.info("INFO: Step 5: verify the pinned topic is not at the top from Feed");
		Assert.assertFalse(isAtTheTopOfAllForumsFeedView(commUUID, apiPinnedTopic.getTitle()), 
				"ERROR: The pinned topic is at the top: " + apiPinnedTopic.getTitle());
		
						
		ui.endTest();
						
	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - MORE THAN ONE COMMUNITY FORUM
	 * <ul>
	 * <li><B>Info: </B>It's 3 of 5 tests for More than one community forum.</li>
	 * <li><B>Info: </B>verify with 2 forums: the new created topic(not the pinned topic) is at the top.</li>
	 * <li><B>Steps: </B>Use API to create a community </li>
	 * <li><B>Steps: </B>Use API to get default community forum and add it to the Forum List</li>
	 * <li><B>Steps: </B>Use API to create 10 topics add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Use API to create the second forum.</li>
	 * <li><B>Steps: </B>Update some topics in the default forum</li>
	 * <li><B>Steps: </B>use API to create a topic in the new forum</li>
	 * <li><B>Steps: </B>Open browser, and Login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>
	 * <li><B>Verify: </B>2 Forums: Verify the pinned topic is NOT highlighted, and not at the top of topic list</li>	 
	 * <li><B>Verify: </B>2 Forums: Verify the pinned topic is  NOT at the top from UI.</li>
	 * <li><B>Verify: </B>2 Forums: Verify no other messages shows under the Topics tab, except "Displaying topics from 2 forums.".</li>
	 * <li><B>Verify: </B>2 forums: Verify all Topics are sorted by Latest Posts -- the new created topic is at the top.</li>
	 * <li><B>Verify: </B>2 Forums: Verify the pinned topic is not at the top from All Feed</li>		
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testMoreThanOneCommunityForum_CreateTopicInNewForum(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>();
		
		logger.strongStep("Get default community forum Using API and add it to the Forum List");
		log.info("INFO: get default community forum Using API and add it to the Forum List");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
		apiForumList.add(apiForum);
		
		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumberD = 10;// all numbers that will be post
		
		logger.strongStep("Use API to create " + topicNumberD + " topics and add it to the Topic List");
		log.info("INFO: use API to create " + topicNumberD + " topics and add it to the Topic List");
		for(int i=0; i<topicNumberD; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )
														.tags(Data.getData().commonTag + Helper.genDateBasedRand())
														.parentForum(apiForum)
														.build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			
			apiTopicList.add(apiTopic);
		}
		
		int topicIndex = getPinnedIndex(topicNumberD);		
		ForumTopic apiPinnedTopic = apiTopicList.get(topicIndex);
		
		logger.strongStep("Step 2: use API to pin one of topics");
		log.info("INFO: Step 2: use API to pin one of topics");
		apiForumsOwner.pinTopic(apiPinnedTopic);
						
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())  
							+ "/communities/service/html/communityoverview?" + commUUID;
				
		BaseForum forum2 = new BaseForum.Builder("second forum")
										.description("testing")
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.build();
		String urlToCreateCommunityForum = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUID;
		
		logger.strongStep("Step 4: use API to create the second community forum");
		log.info("INFO: Step 4: use API to create the second community forum");
		Forum apiForum2= apiForumsOwner.createCommunityForum(urlToCreateCommunityForum, forum2);
				
		logger.strongStep("Step 5:Update some topics in the previous forum");
		log.info("INFO: Step 5:Update some topics in the previous forum");
		int editIndex = getPinnedIndex(topicNumberD);
		if(editIndex==topicIndex)
			if(topicIndex==topicNumberD-1)
				editIndex = topicIndex -1;
			else
				editIndex = topicIndex + 1;
		ForumTopic apiEditTopic1 = apiTopicList.get(editIndex);
		
		logger.strongStep("Use API to update the topic: "+ apiEditTopic1.getTitle());
		log.info("INFO: use API to update the topic: "+ apiEditTopic1.getTitle());
		apiForumsOwner.editTopic(apiEditTopic1, "edit in step 5");
				
		BaseForumTopic forumTopic21 = new BaseForumTopic.Builder("topic 1 in the 2nd forum")		  											  
													  .parentForum(apiForum2)
													  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
													  .build();
		
		logger.strongStep("Step 6: use API to create a topic in the new forum");
		log.info("INFO: Step 6: use API to create a topic in the new forum");
		ForumTopic apiPinnedTopic21 = forumTopic21.createAPI(apiForumsOwner);

		//Load component and login
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		logger.strongStep("Step 6: verify pinned topics is not highlighted, and not at the top of topic list.");
		log.info("INFO: Step 6: verify pinned topics is not highlighted, and not at the top of topic list.");
		verifyTopicNotHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("Step 6: verify the pinned topic is not at the top in UI");
		log.info("INFO: Step 6: verify the pinned topic is not at the top in UI");
		Assert.assertFalse(isAtTheTopOfUIView(apiPinnedTopic), 
				"ERROR: The pinned topic is at the top: " + apiPinnedTopic.getTitle());
		
		String expectedMsg = "Displaying topics from 2 forums.";
		
		logger.strongStep("Step 6: verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		log.info("INFO: Step 6: verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		verifyMessageOnTopicsTab(expectedMsg);
		
		logger.strongStep("Step 6: verify all Topics are sorted by Latest Posts -- the new created topic is at the top");
		log.info("INFO: Step 6: verify all Topics are sorted by Latest Posts -- the new created topic is at the top");
		Assert.assertTrue(isAtTheTopOfUIView(apiPinnedTopic21), 
				"ERROR: the latest created topic is NOT at the top");
		
		logger.strongStep("Step 6: verify the pinned topic is not at the top from Feed");
		log.info("INFO: Step 6: verify the pinned topic is not at the top from Feed");
		Assert.assertFalse(isAtTheTopOfAllForumsFeedView(commUUID, apiPinnedTopic.getTitle()), 
				"ERROR: The pinned topic is at the top: " + apiPinnedTopic.getTitle());
		
		
		ui.endTest();
			
	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - MORE THAN ONE COMMUNITY FORUM
	 * <ul>
	 * <li><B>Info: </B>It's 4 of 5 tests for More than one community forum.</li>
	 * <li><B>Info: </B>even there are 2 forums, the latest created topic is at the top</li>
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum and add it to the Forum List</li>
	 * <li><B>Steps: </B>Use API to create a community and create 10 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Use API to create the second forum.</li>
	 * <li><B>Steps: </B>Update some topics in the default forum</li>
	 * <li><B>Steps: </B>use API to create a topic in the new forum</li>
	 * <li><B>Steps: </B>use API to create another topic in the new forum</li>
	 * <li><B>Steps: </B>Use API to pin the first topic in the new forum</li>
	 * <li><B>Steps: </B>Open browser,and Login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>	 
	 
	 * <li><B>Verify: </B>2 Forums: Verify the first pinned topic(in the default forum) is not highlighted</li>
	 * <li><B>Verify: </B>2 Forums: Verify the second pinned topic(in the new forum) is not highlighted</li>
	 * <li><B>Verify: </B>2 Forums: Verify the first pinned topic(in the default forum) is not at the top of UI.</li>
	 * <li><B>Verify: </B>2 Forums: Verify the second pinned topic(in the new forum) is not at the top of UI.</li>
	 * <li><B>Verify: </B>2 Forums: Verify No other messages shows under the Topics tab, except "Displaying topics from 2 forums."</li>
	 * <li><B>Verify: </B>2 Forums: Verify All Topics are sorted by Latest Posts -- the latest created topic is at the top</li>
	 * <li><B>Verify: </B>2 Forums: Verify the first pinned topic is not at the top from All Feed</li>
	 * <li><B>Verify: </B>2 Forums: Verify the second pinned topic is not at the top from All Feed</li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testMoreThanOneCommunityForum_CreatePin(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>();
		
		logger.strongStep("Get default community forum Using API and add it to the Forum List");
		log.info("INFO: get default community forum Using API and add it to the Forum List");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
		apiForumList.add(apiForum);
		
		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumberD = 10;// all numbers that will be post
		
		logger.strongStep("Use API to create " + topicNumberD + " topics and add it to the Topic List");
		log.info("INFO: use API to create " + topicNumberD + " topics and add it to the Topic List");
		for(int i=0; i<topicNumberD; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )
														.tags(Data.getData().commonTag + Helper.genDateBasedRand())
														.parentForum(apiForum)
														.build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			
			apiTopicList.add(apiTopic);
		}
		
		logger.strongStep("Step 2: use API to pin one of topics");
		log.info("INFO: Step 2: use API to pin one of topics");
		int topicIndex = getPinnedIndex(topicNumberD);		
		ForumTopic apiPinnedTopic = apiTopicList.get(topicIndex);
		apiForumsOwner.pinTopic(apiPinnedTopic);
		
					
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())  
							+ "/communities/service/html/communityoverview?" + commUUID;
		
		
		BaseForum forum2 = new BaseForum.Builder("second forum")
										.description("testing")
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.build();
		String urlToCreateCommunityForum = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUID;
		
		logger.strongStep("Step 4: use API to create the second community forum");
		log.info("INFO: Step 4: use API to create the second community forum");
		Forum apiForum2= apiForumsOwner.createCommunityForum(urlToCreateCommunityForum, forum2);
				
		logger.strongStep("Step 5: Update some topics in the previous forum");
		log.info("INFO: Step 5: Update some topics in the previous forum");
		int editIndex = getPinnedIndex(topicNumberD);
		if(editIndex==topicIndex)
			if(topicIndex==topicNumberD-1)
				editIndex = topicIndex -1;
			else
				editIndex = topicIndex + 1;
		ForumTopic apiEditTopic1 = apiTopicList.get(editIndex);
		
		logger.strongStep("Use API to update the topic: "+ apiEditTopic1.getTitle());
		log.info("INFO: use API to update the topic: "+ apiEditTopic1.getTitle());
		apiForumsOwner.editTopic(apiEditTopic1, "edit in step 5");
			
		BaseForumTopic forumTopic21 = new BaseForumTopic.Builder("topic 1 in the 2nd forum")		  											  
													  .parentForum(apiForum2)
													  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
													  .build();
		
		logger.strongStep("Step 6: use API to create a topic in the new forum");
		log.info("INFO: Step 6: use API to create a topic in the new forum");
		ForumTopic apiPinnedTopic21 = forumTopic21.createAPI(apiForumsOwner);
			
		BaseForumTopic forumTopic22 = new BaseForumTopic.Builder("topic 2 in the 2nd forum")		  											  
													  .parentForum(apiForum2)
													  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
													  .build();
		
		logger.strongStep("Step 7: use API to create another topic in the new forum");
		log.info("INFO: Step 7: use API to create another topic in the new forum");
		ForumTopic apiTopic22 = forumTopic22.createAPI(apiForumsOwner);

		logger.strongStep("Pin the first topic in the new forum");
		log.info("INFO: pin the first topic in the new forum");
		apiForumsOwner.pinTopic(apiPinnedTopic21);
		
		//Load component and login
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community ");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);		
		
		log.info("INFO: verify the first pinned topic(in the default forum) is not highlighted");
		verifyTopicNotHighlighted(apiPinnedTopic.getId().toString());
		
		log.info("INFO: verify the second pinned topic(in the new forum) is not highlighted");
		verifyTopicNotHighlighted(apiPinnedTopic21.getId().toString());
		
		logger.strongStep("Verify the pinned topics are not at the top in UI");
		log.info("INFO: verify the pinned topics are not at the top in UI");
		Assert.assertFalse(isAtTheTopOfUIView(apiPinnedTopic), 
				"ERROR: The pinned topic is at the top: " + apiPinnedTopic.getTitle());
		Assert.assertFalse(isAtTheTopOfUIView(apiPinnedTopic21), 
				"ERROR: The pinned topic is at the top: " + apiPinnedTopic.getTitle());
		
		String expectedMsg = "Displaying topics from 2 forums.";
		
		logger.strongStep("Verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		log.info("INFO: verify no other messages shows under the Topics tab, except \""+ expectedMsg +"\"");
		verifyMessageOnTopicsTab(expectedMsg);
		
		logger.strongStep("Verify all Topics are sorted by Latest Posts -- the lastest created topic is at the top");
		log.info("INFO: verify all Topics are sorted by Latest Posts -- the lastest created topic is at the top");
		Assert.assertTrue(isAtTheTopOfUIView(apiTopic22), "ERROR: the new updated topic is at the top");
		
		logger.strongStep("Step 7: verify the pinned topics are not at the top from Feed");
		log.info("INFO: Step 7: verify the pinned topics are not at the top from Feed");
		Assert.assertFalse(isAtTheTopOfAllForumsFeedView(commUUID, apiPinnedTopic.getTitle()), 
				"ERROR: The pinned topic is at the top: " + apiPinnedTopic.getTitle());
		Assert.assertFalse(isAtTheTopOfAllForumsFeedView(commUUID, apiPinnedTopic21.getTitle()), 
				"ERROR: The pinned topic is at the top: " + apiPinnedTopic21.getTitle());
						
		ui.endTest();
			
	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - MORE THAN ONE COMMUNITY FORUM
	 * <ul>
	 * <li><B>Info: </B>It's 5 of 5 tests for More than one community forum.</li>
	 * <li><B>Info: </B>Verify even if there are 2 forums, in each forum, pinned topics UI is as same as there is totally 1 forum.</li>
	 
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum and add it to the Forum List</li>
	 * <li><B>Steps: </B>Use API to create a community and create 10 topics and add it to the Topic list</li>
	 * <li><B>Steps: </B>Use API to pin one topic(not the latest created)</li>
	 * <li><B>Steps: </B>Use API to create the second forum.</li>
	 * <li><B>Steps: </B>Update some topics in the default forum</li>
	 * <li><B>Steps: </B>use API to create a topic in the new forum</li>
	 * <li><B>Steps: </B>use API to create another topic in the new forum</li>
	 * <li><B>Steps: </B>Use API to pin the first topic in the new forum</li>
	 * <li><B>Steps: </B>Open browser,and login</li>	 
	 * <li><B>Steps: </B>Navigate to the default by its url</li>		 
	 * <li><B>Verify: </B>2 Forums: In default forum: Verify pinned topics is highlighted</li>
	 * <li><B>Verify: </B>2 Forums: In default forum: Verify no messages shows under the Topics tab</li>
	 * <li><B>Verify: </B>2 Forums: In default forum: Verify the pinned topic at the top from this Forum's UI.</li>
	 * <li><B>Verify: </B>2 Forums: In default forum: Verify the pinned topic is at the top from this forum's Feed.</li>
	 * <li><B>Steps: </B>Navigate to the new forum by url</li>
	 * <li><B>Verify: </B>2 Forums: In new forum: Verify the pinned topic is highlighted</li>
	 * <li><B>Verify: </B>2 Forums: In new forum: Verify no messages shows under the Topics tab</li>
	 * <li><B>Verify: </B>2 Forums: In new forum: Verify the pinned topic at the top from this Forum's UI.</li>
	 * <li><B>Verify: </B>2 Forums: In new forum: Verify the pinned topic is at the top from this forum's Feed.</li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testMoreThanOneCommunityForum_EachForum(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>();
		
		logger.strongStep("Get default community forum Using API and add it to the Forum List");
		log.info("INFO: get default community forum Using API and add it to the Forum List");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName());
		apiForumList.add(apiForum);
		
		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumberD = 10;// all numbers that will be post
		
		logger.strongStep("Use API to create " + topicNumberD + " topics and add it to the Topic list");
		log.info("INFO: use API to create " + topicNumberD + " topics and add it to the Topic list");
		for(int i=0; i<topicNumberD; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )
														.tags(Data.getData().commonTag + Helper.genDateBasedRand())
														.parentForum(apiForum)
														.build();
			ForumTopic apiTopic = forumTopic1.createAPI(apiForumsOwner);
			
			apiTopicList.add(apiTopic);
		}
		
		logger.strongStep("Step 2: use API to pin one of topics");
		log.info("INFO: Step 2: use API to pin one of topics");
		int topicIndex = getPinnedIndex(topicNumberD);		
		ForumTopic apiPinnedTopic = apiTopicList.get(topicIndex);
		apiForumsOwner.pinTopic(apiPinnedTopic);
			
		BaseForum forum2 = new BaseForum.Builder("second forum")
										.description("testing")
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.build();
		String urlToCreateCommunityForum = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUID;
		
		logger.strongStep("Step 4: use API to create the second community forum");
		log.info("INFO: Step 4: use API to create the second community forum");
		Forum apiForum2= apiForumsOwner.createCommunityForum(urlToCreateCommunityForum, forum2);
				
		logger.strongStep("Step 5: Update some topics in the previous forum");
		log.info("INFO: Step 5: Update some topics in the previous forum");
		int editIndex = getPinnedIndex(topicNumberD);
		if(editIndex==topicIndex)
			if(topicIndex==topicNumberD-1)
				editIndex = topicIndex -1;
			else
				editIndex = topicIndex + 1;
		ForumTopic apiEditTopic1 = apiTopicList.get(editIndex);
		
		logger.strongStep("Use API to update the topic: "+ apiEditTopic1.getTitle());
		log.info("INFO: use API to update the topic: "+ apiEditTopic1.getTitle());
		apiForumsOwner.editTopic(apiEditTopic1, "edit in step 5");
			
		BaseForumTopic forumTopic21 = new BaseForumTopic.Builder("topic 1 in the 2nd forum")		  											  
													  .parentForum(apiForum2)
													  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
													  .build();
		
		logger.strongStep("Step 6: use API to create a topic in the new forum");
		log.info("INFO: Step 6: use API to create a topic in the new forum");
		ForumTopic apiPinnedTopic21 = forumTopic21.createAPI(apiForumsOwner);
	
		BaseForumTopic forumTopic22 = new BaseForumTopic.Builder("topic 2 in the 2nd forum")		  											  
													  .parentForum(apiForum2)
													  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
													  .build();
		
		logger.strongStep("Step 7: use API to create another topic in the new forum");		
		log.info("INFO: Step 7: use API to create another topic in the new forum");
		forumTopic22.createAPI(apiForumsOwner);

		logger.strongStep("Pin the first topic in the new forum");
		log.info("INFO: pin the first topic in the new forum");
		apiForumsOwner.pinTopic(apiPinnedTopic21);

		//Load component and login
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		logger.strongStep("Navigate to the forum by its url");
		log.info("INFO: navigate to the forum by its url");
		driver.navigate().to(apiForum.getAlternateLink());
		
		logger.strongStep("In default forum: verify pinned topics is highlighted");
		log.info("INFO: In default forum: verify pinned topics is highlighted");
		verifyTopicHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("In default forum: verify no messages shows under the Topics tab");
		log.info("INFO: In default forum: verify no messages shows under the Topics tab");
		verifyNoMessageOnTopicsTab();
		
		logger.strongStep("In default forum: verify the pinned topic is at the top from One Forum's UI");
		log.info("INFO: In default forum: verify the pinned topic is at the top from One Forum's UI");
		Assert.assertTrue(isAtTheTopOfUIView(apiPinnedTopic), 
				"ERROR: The pinned topic is not at the top: " + apiPinnedTopic.getTitle());
		
		logger.strongStep("In default forum: verify the pinned topic is at the top from One Forum's Feed");
		log.info("INFO: In default forum: verify the pinned topic is at the top from One Forum's Feed");
		Assert.assertTrue(isAtTheTopOfOneForumFeedView(getForumUUID(apiForum.getId().toString()), apiPinnedTopic.getTitle()),
				"ERROR: the pinned topic is not at the top: " + apiPinnedTopic.getTitle());
		
		logger.strongStep("Navigate to the 2nd forum by its url");
		log.info("INFO: Navigate to the 2nd forum by its url");
		driver.navigate().to(apiForum2.getAlternateLink());
		
		logger.strongStep("In new forum: verify pinned topics is highlighted");
		log.info("INFO: In new forum: verify pinned topics is highlighted");		
		verifyTopicHighlighted(apiPinnedTopic21.getId().toString());
		
		logger.strongStep("In new forum: verify No other messages shows under the Topics tab");
		log.info("INFO: In new forum: verify No other messages shows under the Topics tab");
		verifyNoMessageOnTopicsTab();
		
		logger.strongStep("In new forum: verify the pinned topic at the top from One Forum's UI");
		log.info("INFO: In new forum: verify the pinned topic at the top from One Forum's UI");
		Assert.assertTrue(isAtTheTopOfUIView(apiPinnedTopic21), 
				"ERROR: The pinned topic is not at the top: " + apiPinnedTopic21.getTitle());
		
		logger.strongStep("In new forum: verify the pinned topic at the top from One Forum's Feed");
		log.info("INFO: In new forum: verify the pinned topic at the top from One Forum's Feed");
		Assert.assertTrue(isAtTheTopOfOneForumFeedView(getForumUUID(apiForum2.getId().toString()), apiPinnedTopic21.getTitle()),
				"ERROR: the pinned topic is not at the top: " + apiPinnedTopic21.getTitle());
			
		ui.endTest();
		
	}
	
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - DELETE COMMUNITY FORUMS EXCEPT ONE
	 * <ul>
	 * <li><B>Info: </B>It's 1 of 2 tests for delete community forums except one</li>
	 * <li><B>Info: </B>verify UI if there are 3 forums</li>
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum and add it to the Forum List.</li>
	 * <li><B>Steps: </B>Use API to create 3 community forums and add it to the Forum List</li>
	 * <li><B>Steps: </B>use API to create 10 topics in the second forum and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin a topic which is not the latest one</li>
	 * <li><B>Steps: </B>Open browser,and Login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>
	 * <li><B>Verify: </B>3 Forums: Verify the pinned topic is pinned, but not highlighted</li>
	 * <li><B>Verify: </B>3 Forums: Verify no other messages shows under the Topics tab, except "Displaying topics from 3 forums." </li>
	 * <li><B>Verify: </B>3 Forums: Verify the latest topic is at the top on the Topics tab</li>
	 * <li><B>Verify: </B>3 Forums: Verify the latest topic is at the top from All Feed</li>
	 * <li><B>Verify: </B>3 Forums: Verify the pinned topic is  NOT at the top from UI.</li>
	 * <li><B>Verify: </B>3 Forums: Verify the pinned topic is NOT at the top from All Feed.</li>	 	
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void test3ForumsUI(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>();
		
		logger.strongStep("Get default community forum Using API and add it to the Forum List");
		log.info("INFO: get default community forum Using API and add it to the Forum List");
		apiForumList.add(apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName()));
		
		String urlToCreateCommunityForum = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUID;
		int forumNumber = 3;
		
		logger.strongStep("Use API to create " + forumNumber + " community forums and add it to the Forum List");
		log.info("INFO: use API to create " + forumNumber + " community forums and add it to the Forum List");
		for(int i=1; i<forumNumber; i++){
			BaseForum forum2 = new BaseForum.Builder("forum "+ i)
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.description("testing")
										.build();
			apiForumList.add(apiForumsOwner.createCommunityForum(urlToCreateCommunityForum, forum2));
		}
		
		logger.strongStep("Select the second forum to create topics");
		log.info("INFO: select the second forum to create topics");
		int forumIndex = 1;
		Forum apiForum = apiForumList.get(forumIndex);
		
		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumber = 10;// all numbers that will be post
		
		logger.strongStep("Use API to create " + topicNumber + " topics and add it to the Topic List");
		log.info("INFO: use API to create " + topicNumber + " topics and add it to the Topic List");
		for(int i=0; i<topicNumber; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )				  
		  											  .parentForum(apiForum)
		  											  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
		  											  .build();
			apiTopicList.add(forumTopic1.createAPI(apiForumsOwner));
		}
		
		logger.strongStep("Select a topic to pin it");
		log.info("INFO: select a topic to pin it");
		int topicIndex = this.getPinnedIndex(topicNumber);
		ForumTopic apiPinnedTopic = apiTopicList.get(topicIndex);
	
		ForumTopic latestTopic = apiTopicList.get(topicNumber -1);
		log.info("INFO: the latest topic is " + latestTopic.getTitle());

		logger.strongStep("Use API to pin the topic " + apiPinnedTopic.getTitle());
		log.info("INFO: use API to pin the topic " + apiPinnedTopic.getTitle());
		apiForumsOwner.pinTopic(apiPinnedTopic);
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+ "/communities/service/html/communityoverview?"+commUUID;

		//Load component and login
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);		
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);
		
		logger.strongStep("Verify the pinned topic is pinned but NOT highlighted");
		log.info("INFO: verify the pinned topic is pinned but NOT highlighted");
		verifyTopicNotHighlighted(apiPinnedTopic.getId().toString());
		
		logger.strongStep("Verify message 'Displaying topics from "+ forumNumber +" forums 'shown under the Topics tab");
		log.info("INFO: verify message 'Displaying topics from "+ forumNumber +" forums 'shown under the Topics tab");
		Assert.assertTrue(driver.isTextPresent("Displaying topics from "+ forumNumber+" forums"),
				"ERROR: message for "+ forumNumber +" forums on Topics tab is not shown up");
		
		logger.strongStep("Verify latest topic is at the top on the Topics tab");
		log.info("INFO: verify latest topic is at the top on the Topics tab");
		Assert.assertTrue(isAtTheTopOfUIView(latestTopic), 
				"ERROR: the created latest topic is not at the top on the Topics tab");
		
		logger.strongStep("Verify the latest topic is at the top of all feed");
		log.info("INFO: verify the latest topic is at the top of all feed");
		Assert.assertTrue(isAtTheTopOfAllForumsFeedView(commUUID,latestTopic.getTitle()),
				"ERROR: the created lastest topic is not at the top on the Feed");
		
		logger.strongStep("Verify the pinned topic is not at the top in UI");
		log.info("INFO: verify pinned topic is not at the top in UI");
		Assert.assertFalse(isAtTheTopOfUIView(apiPinnedTopic), 
			"ERROR: the pinned topic is at the top on the Topics tab");
		
		logger.strongStep("Verify the pinned topic is not at the top from Feed");
		log.info("INFO: verify the pinned topic is not at the top from Feed");		
		Assert.assertFalse(isAtTheTopOfAllForumsFeedView(commUUID,apiPinnedTopic.getTitle()),
			"ERROR: the pinned topic is not at the top on the Feed");
						
		ui.endTest();

	}
	/**
	 * TEST CASE: COMMUNITY PIN TOPICS - DELETE COMMUNITY FORUMS EXCEPT ONE
	 * <ul>
	 * <li><B>Info: </B>It's 2 of 2 tests for delete community forums except one</li>
	 * <li><B>Info: </B>Verify the pinned UI after delete other forums except one</li>	
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum and add it to the Forum list</li>
	 * <li><B>Steps: </B>Use API to create 3 community forums and add it to the Forum List</li>
	 * <li><B>Steps: </B>use API to create 10 topics in the second forum and add it to the Topic List</li>
	 * <li><B>Steps: </B>Use API to pin a topic which is not the latest one</li>
	 * <li><B>Steps: </B>use API to delete all forums except the one which the pinned Topic is in</li>	 
	 * <li><B>Steps: </B>Open browser, and Login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>
	 * <li><B>Verify: </B>Verify After delete, there is only one forum and the new pinned topic at the top from UI</li>
	 * <li><B>Verify: </B>Verify After delete, there is only one forum and the new pinned topic is highlighted</li>
	 * <li><B>Verify: </B>Verify After delete, there is only one forum and the new pinned topic is the first topic in the feed</li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testDeleteCommunityForumsExceptOne(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())													
													.build();
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));
		
		String commUUID = apiOwner.getCommunityUUID(comAPI);
		
		ArrayList<Forum> apiForumList = new ArrayList<Forum>();
		
		logger.strongStep("Get default community forum Using API and add it to the Forum list");
		log.info("INFO: get default community forum Using API and add it to the Forum list");
		apiForumList.add(apiForumsOwner.getDefaultCommForum(getUUID(commUUID), community.getName()));
		
		String urlToCreateCommunityForum = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())+"/forums/atom/forums?"+ commUUID;	
		int forumNumber = 3;
		
		logger.strongStep("Use API to create " + forumNumber + " community forums and add it to the Forum List");
		log.info("INFO: use API to create " + forumNumber + " community forums and add it to the Forum List");
		for(int i=1; i<forumNumber; i++){
			BaseForum forum2 = new BaseForum.Builder("forum "+ i)
										.tags(Data.getData().commonTag + Helper.genDateBasedRand())
										.description("testing")
										.build();
			apiForumList.add(apiForumsOwner.createCommunityForum(urlToCreateCommunityForum, forum2));
		}
		
		logger.strongStep("Select the second forum to create topics");
		log.info("INFO: select the second forum to create topics");
		int forumIndex = 1;
		Forum apiForum = apiForumList.get(forumIndex);
		
		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumber = 10;// all numbers that will be post
		
		logger.strongStep("Use API to create " + topicNumber + " topics and add it to the Topic List");
		log.info("INFO: use API to create " + topicNumber + " topics and add it to the Topic List");
		for(int i=0; i<topicNumber; i++){
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic " + i )				  
													  .tags(Data.getData().commonTag + Helper.genDateBasedRand())
		  											  .parentForum(apiForum)
		  											  .build();
			apiTopicList.add(forumTopic1.createAPI(apiForumsOwner));
		}
		
		logger.strongStep("Select a topic to pin it");
		log.info("INFO: select a topic to pin it");
		int topicIndex = this.getPinnedIndex(topicNumber);
		ForumTopic apiPinnedTopic = apiTopicList.get(topicIndex);
		
		ForumTopic latestTopic = apiTopicList.get(topicNumber -1);
		log.info("INFO: the latest topic is " + latestTopic.getTitle());

		logger.strongStep("Use API to pin the topic " + apiPinnedTopic.getTitle());
		log.info("INFO: use API to pin the topic " + apiPinnedTopic.getTitle());
		apiForumsOwner.pinTopic(apiPinnedTopic);
	
		logger.strongStep("Use API to delete all forums except the one which the pinned Topic is in");
		log.info("INFO: use API to delete all forums except the one which the pinned Topic is in");		
		for(int i=0; i<forumNumber; i++){
			if(i!=forumIndex){
				apiForumsOwner.deleteForum(apiForumList.get(i));
			}		
		}
		
		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+ "/communities/service/html/communityoverview?"+commUUID;

		//Load component and login 
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);		
		
		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);

		logger.strongStep("1 Forum left: verify the pinned topic is at the top in UI");
		log.info("INFO: 1 Forum left: verify the pinned topic is at the top in UI");
		Assert.assertTrue(isAtTheTopOfUIView(apiPinnedTopic), 
				"ERROR: The  pinned topic is not at the top: " + apiPinnedTopic.getTitle());		
		
		logger.strongStep("1 Forum left: verify the pinned topic is highlighted");
		log.info("INFO: 1 Forum left:  verify the pinned topic is highlighted");
		verifyTopicHighlighted(apiPinnedTopic.getId().toString());		
		
		logger.strongStep("1 Forum left: verify the pinned topic is the first topic in the feed");
		log.info("INFO: 1 Forum left: verify the pinned topic is the first topic in the feed");
		Assert.assertTrue(isAtTheTopOfAllForumsFeedView(commUUID,apiPinnedTopic.getTitle()),
				 "ERROR: in feed, pinned topic is not at the top");

		
		ui.endTest();
		
	
	}
	/**
	 * COMMUNITY PIN TOPICS - ONLY ONE COMMUNITY FORUM
	 * <ul>
	 * <li><B>Info: </B>It's 1 of 4 tests for Only one community forum.</li>
	 * <li><B>Info: </B>Verify UI when only one topic is pinned</li>
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum</li>
	 * <li><B>Steps: </B>Use API to create 10 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>use API to pin one topic(not the latest one)</li>
	 * <li><B>Steps: </B>Open browser,and Login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>
	 * <li><B>Verify: </B>Verify the pinned topic is at the top from UI</li>
	 * <li><B>Verify: </B>Verify the pinned topic is highlighted</li>
	 * <li><B>Verify: </B>Verify the pinned topic is the first topic in the feed</li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testOnlyOneForum1(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.build();
		// create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));

		String commUUID = apiOwner.getCommunityUUID(comAPI);

		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID),community.getName());

		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumber = 10;
		
		logger.strongStep("Use API to create " + topicNumber + " topics and add it to the Topic List");
		log.info("INFO: use API to create " + topicNumber + " topics and add it to the Topic List");
		for (int i = 0; i < topicNumber; i++) {
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic "+ i)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.parentForum(apiForum)
											.build();
			apiTopicList.add(forumTopic1.createAPI(apiForumsOwner));
		}

		ForumTopic apiPinnedOldestTopic = apiTopicList.get(0);

		logger.strongStep("Use API to pin the oldest topic"+ apiPinnedOldestTopic.getTitle());
		log.info("INFO: use API to pin the oldest topic"+ apiPinnedOldestTopic.getTitle());
		apiForumsOwner.pinTopic(apiPinnedOldestTopic);

		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+ "/communities/service/html/communityoverview?" + commUUID;

		//Load component and login 
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);

		logger.strongStep("Verify the pinned topic is at the top in UI");
		log.info("INFO: verify the pinned topic is at the top in UI");
		Assert.assertTrue(isAtTheTopOfUIView(apiPinnedOldestTopic),
				"ERROR: The pinned topic is not at the top: " + apiPinnedOldestTopic.getTitle());

		logger.strongStep("Verify the pinned topic is highlighted");
		log.info("INFO: verify the pinned topic is highlighted");
		verifyTopicHighlighted(apiPinnedOldestTopic.getId().toString());

		logger.strongStep("Verify the pinned topic is the first topic in the feed");
		log.info("INFO: verify the pinned topic is the first topic in the feed");
		Assert.assertTrue(isAtTheTopOfAllForumsFeedView(commUUID, apiPinnedOldestTopic.getTitle()),
				"ERROR: in feed, pinned topic is not at the top");
		
		ui.endTest();
				
	}
	/**
	 * COMMUNITY PIN TOPICS - ONLY ONE COMMUNITY FORUM
	 * <ul>
	 * <li><B>Info: </B>It's 2 of 4 tests for Only one community forum.</li>
	 * <li><B>Info: </B>Verify pinned topic is highlighted and at the top, even after other topic is updated</li>
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum</li>
	 * <li><B>Steps: </B>Use API to create 10 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>use API to pin one topic(not the latest one)</li>	 
	 * <li><B>Steps: </B>Use API to Update some other topics and back to Community Topic view </li>
	 * <li><B>Steps: </B>Open browser,and Login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>
	 * <li><B>Verify: </B>Verify after other topic is updated, the pinned topic is still at the top from UI</li>
	 * <li><B>Verify: </B>Verify after other topic is updated, the pinned topic is still highlighted</li>
	 * <li><B>Verify: </B>Verify after other topic is updated, the pinned topic is still the first topic in the feed</li>
	 
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testOnlyOneForum2(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.build();
		// create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));

		String commUUID = apiOwner.getCommunityUUID(comAPI);

		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID),community.getName());

		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumber = 10;
		
		logger.strongStep("Use API to create " + topicNumber + " topics and add it to the Topic list");
		log.info("INFO: use API to create " + topicNumber + " topics and add it to the Topic list");
		for (int i = 0; i < topicNumber; i++) {
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic "+ i)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.parentForum(apiForum)
											.build();
			apiTopicList.add(forumTopic1.createAPI(apiForumsOwner));
		}

		ForumTopic apiPinnedOldestTopic = apiTopicList.get(0);

		logger.strongStep("Use API to pin the oldest topic"	+ apiPinnedOldestTopic.getTitle());
		log.info("INFO: use API to pin the oldest topic"	+ apiPinnedOldestTopic.getTitle());
		apiForumsOwner.pinTopic(apiPinnedOldestTopic);

		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+ "/communities/service/html/communityoverview?" + commUUID;

		logger.strongStep("Use API to Update some other topics and back to Community Topic view");
		log.info("INFO: use API to Update some other topics and back to Community Topic view");
		ForumTopic editTopic1 = apiTopicList.get(1);
		apiForumsOwner.editTopic(editTopic1, "edit in step 2");

		//Load component and login
		logger.strongStep("Open browser and login to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and login to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);

		logger.strongStep("Verify after other topic is updated, the pinned topic is still at the top in UI");
		log.info("INFO: verify after other topic is updated, the pinned topic is still at the top in UI");
		Assert.assertTrue(isAtTheTopOfUIView(apiPinnedOldestTopic),
				"ERROR: The pinned topic is not at the top: " + apiPinnedOldestTopic.getTitle());

		logger.strongStep("Verify after other topic is updated, the pinned topic is still highlighted");
		log.info("INFO: verify after other topic is updated, the pinned topic is still highlighted");
		verifyTopicHighlighted(apiPinnedOldestTopic.getId().toString());

		logger.strongStep("Verify after other topic is updated, the pinned topic is still the first topic in the feed");
		log.info("INFO: verify after other topic is updated, the pinned topic is still the first topic in the feed");
		Assert.assertTrue(isAtTheTopOfAllForumsFeedView(commUUID, apiPinnedOldestTopic.getTitle()),
				"ERROR: in feed, pinned topic is not at the top");

		ui.endTest();
				
	}
	/**
	 * COMMUNITY PIN TOPICS - ONLY ONE COMMUNITY FORUM
	 * <ul>
	 * <li><B>Info: </B>It's 3 of 4 tests for Only one community forum.</li>
	 * <li><B>Info: </B>Verify UI when 2 topics are pinned</li>
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum</li>
	 * <li><B>Steps: </B>Use API to create 10 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>use API to pin one topic(not the latest one)</li>	
	 * <li><B>Steps: </B>use API to Update some other topics </li>	
	 * <li><B>Steps: </B>use API to pin another topic</li>
	 * <li><B>Steps: </B>Open browser,and login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li>
	 * <li><B>Verify: </B>Verify all pinned topics are on the top of the view</li>
	 * <li><B>Verify: </B>Verify all pinned topics are on the top of the Feed</li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testOnlyOneForum3(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.build();
		// create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));

		String commUUID = apiOwner.getCommunityUUID(comAPI);

		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID),community.getName());

		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumber = 10;
		
		logger.strongStep("Use API to create " + topicNumber + " topics and add it to the Topic list");
		log.info("INFO: use API to create " + topicNumber + " topics and add it to the Topic list");
		for (int i = 0; i < topicNumber; i++) {
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic "+ i)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.parentForum(apiForum)
											.build();
			apiTopicList.add(forumTopic1.createAPI(apiForumsOwner));
		}

		ForumTopic apiPinnedOldestTopic = apiTopicList.get(0);

		logger.strongStep("Use API to pin the oldest topic"+ apiPinnedOldestTopic.getTitle());
		log.info("INFO: use API to pin the oldest topic"+ apiPinnedOldestTopic.getTitle());
		apiForumsOwner.pinTopic(apiPinnedOldestTopic);

		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+ "/communities/service/html/communityoverview?" + commUUID;

		logger.strongStep("Use API to Update some other topics and back to Community Topic view");
		log.info("INFO: use API to Update some other topics and back to Community Topic view");
		ForumTopic editTopic1 = apiTopicList.get(1);
		apiForumsOwner.editTopic(editTopic1, "edit in step 2");

		logger.strongStep("Use API to pin another topic");
		log.info("INFO: use API to pin another topic");
		ForumTopic apiSecondPinnedTopic = apiTopicList.get(topicNumber - 2);
		apiForumsOwner.pinTopic(apiSecondPinnedTopic);

		//Load component and login
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);

		logger.strongStep("Verify all pinned topics are on the top of the view");
		log.info("INFO: verify all pinned topics are on the top of the view");
		Assert.assertTrue(getUIPosition(apiSecondPinnedTopic) == 0,
				"ERROR: the new pinned topics is not at the top");
		Assert.assertTrue(getUIPosition(apiPinnedOldestTopic) == 1,
				"ERROR: the first pinned topics is at the top");

		logger.strongStep("Verify all pinned topics are on the top of the Feed");
		log.info("INFO: verify all pinned topics are on the top of the Feed");
		Assert.assertTrue(getFeedPosition(commUUID, apiSecondPinnedTopic) == 0,
				"ERROR: the new pinned topics is not at the top");
		Assert.assertTrue(getFeedPosition(commUUID, apiPinnedOldestTopic) == 1,
				"ERROR: the first pinned topics is at the top");


		ui.endTest();
				
	}
	/**
	 * COMMUNITY PIN TOPICS - ONLY ONE COMMUNITY FORUM
	 * <ul>
	 * <li><B>Info: </B>It's 4 of 4 tests for Only one community forum.</li>
	 * <li><B>Info: </B>Verify UI when some topic is updated after pinned 2 topics</li>
	 * <li><B>Steps: </B>Use API to create a community</li>
	 * <li><B>Steps: </B>Use API to get default community forum</li>
	 * <li><B>Steps: </B>Use API to create 10 topics and add it to the Topic List</li>
	 * <li><B>Steps: </B>use API to pin one topic(not the latest one)</li>
	 * <li><B>Steps: </B>use API to Update some other topics </li>	
	 * <li><B>Steps: </B>use API to pin another topic</li>	 	
	 * <li><B>Steps: </B>use API to update another topic</li>
	 * <li><B>Steps: </B>Open browser,and Login</li>
	 * <li><B>Steps: </B>Navigate to Overview page</li> 
	 * <li><B>Verify: </B>Verify even if other topic is updated after pinned, All pinned topics are on the top of the view</li>
	 * <li><B>Verify: </B>Verify even if other topic is updated after pinned, All pinned topics are on the top of the Feed</li>
	 * </ul>
	 */
	@Test(groups={"regression", "regressioncloud"})
	public void testOnlyOneForum4(){
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		//Start of the test
		String testName = ui.startTest();
				
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
													.tags(Data.getData().commonTag + Helper.genDateBasedRand())
													.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
													.access(Access.PUBLIC)
													.description("Test description for testcase " + testName + Helper.genDateBasedRand())
													.build();
		// create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);

		// add the UUID to community
		logger.strongStep("Get UUID of community");
		log.info("INFO: get UUID of community");
		community.setCommunityUUID(apiOwner.getCommunityUUID(comAPI));

		String commUUID = apiOwner.getCommunityUUID(comAPI);

		logger.strongStep("Get default community forum Using API");
		log.info("INFO: get default community forum Using API");
		Forum apiForum = apiForumsOwner.getDefaultCommForum(getUUID(commUUID),community.getName());

		ArrayList<ForumTopic> apiTopicList = new ArrayList<ForumTopic>();
		int topicNumber = 10;
		
		logger.strongStep("Use API to create " + topicNumber + " topics and add it to the Topic List");
		log.info("INFO: use API to create " + topicNumber + " topics and add it to the Topic List");
		for (int i = 0; i < topicNumber; i++) {
			BaseForumTopic forumTopic1 = new BaseForumTopic.Builder("topic "+ i)
											.tags(Data.getData().commonTag + Helper.genDateBasedRand())
											.parentForum(apiForum)
											.build();
			apiTopicList.add(forumTopic1.createAPI(apiForumsOwner));
		}

		ForumTopic apiPinnedOldestTopic = apiTopicList.get(0);

		logger.strongStep("Use API to pin the oldest topic"+ apiPinnedOldestTopic.getTitle());
		log.info("INFO: use API to pin the oldest topic"+ apiPinnedOldestTopic.getTitle());
		apiForumsOwner.pinTopic(apiPinnedOldestTopic);

		String overview = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL())
							+ "/communities/service/html/communityoverview?" + commUUID;

		logger.strongStep("Use API to Update some other topics and back to Community Topic view");
		log.info("INFO: use API to Update some other topics and back to Community Topic view");
		ForumTopic editTopic1 = apiTopicList.get(1);
		apiForumsOwner.editTopic(editTopic1, "edit in step 2");

		logger.strongStep("Use API to pin another topic");
		log.info("INFO: use API to pin another topic");
		ForumTopic apiSecondPinnedTopic = apiTopicList.get(topicNumber - 2);
		apiForumsOwner.pinTopic(apiSecondPinnedTopic);

		logger.strongStep("Use API to update another topic");
		log.info("INFO: use API to update another topic");
		ForumTopic editTopic2 = apiTopicList.get(2);
		apiForumsOwner.editTopic(editTopic2, "edit in step 4");

		//Load component and login
		logger.strongStep("Open browser and log in to Community as: " + testUser1.getDisplayName());
		log.info("INFO: Open browser and log in to Community");
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser1);

		logger.strongStep("Navigate to the Forum's overview page");
		log.info("INFO: navigate to the Forum's overview page");
		navigateToForums(overview);

		logger.strongStep("Verify even if other topic is updated after pinned, All pinned topics are on the top of the view");
		log.info("INFO: verify even if other topic is updated after pinned, All pinned topics are on the top of the view");
		Assert.assertTrue(getUIPosition(apiSecondPinnedTopic) == 0,
				"ERROR: the new pinned topics is not at the top");
		Assert.assertTrue(getUIPosition(apiPinnedOldestTopic) == 1,
				"ERROR: the first pinned topics is at the top");

		logger.strongStep("Verify even if other topic is updated after pinned, All pinned topics are on the top of the Feed");
		log.info("INFO: verify even if other topic is updated after pinned, All pinned topics are on the top of the Feed");
		Assert.assertTrue(getFeedPosition(commUUID, apiSecondPinnedTopic) == 0,
				"ERROR: the new pinned topics is not at the top");
		Assert.assertTrue(getFeedPosition(commUUID, apiPinnedOldestTopic) == 1,
				"ERROR: the first pinned topics is at the top");

		ui.endTest();
				
	}
}
