package com.ibm.lconn.automation.framework.services.wikis.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;

import java.util.Date;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.MemberType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.common.nodes.Member;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;
import com.ibm.lconn.automation.framework.services.wikis.WikisService;
import com.ibm.lconn.automation.framework.services.wikis.nodes.WikiPage;

/**
 *  Connections API test
 */
public class WikisOrgAdminAPITest {
	
	protected final static Logger LOGGER = LoggerFactory.getLogger(WikisOrgAdminAPITest.class.getName());
	
	//Users index in ProfileData_*.properties
	static final int USER0 = 0;   	// OrgA-admin
	static final int USER3 = 3;		// OrgA user
	static final int USER5 = 5;		// OrgA user
	static final int USER6 = 6;		// OrgA user
	
	private static UserPerspective user0, user3, user5, user6;      	
	private static CommunitiesService comm0Service, comm3Service, comm5Service, comm6Service;
	private static WikisService wikis0Service, wikis3Service, wikis5Service, wikis6Service;

	private static String community_1_UUID;
	
	private static String wiki_1_UUID;
	
	private static String page_1_UUID, page_2_UUID, page_2_1_UUID, page_2_2_UUID, page_2_1_1_UUID;
	
	private static String draft_1_UUID, pageDraft_1_UUID;
	
	@BeforeClass
	public static void setUp() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("WikisOrgAdminAPITest");
		LOGGER.debug("===========================================");
        
		//  set up multiple users testing environment
		LOGGER.debug("Start Initializing Wikis Cisco Test");
		
		UsersEnvironment userEnv = new UsersEnvironment();
		
		//For communities
		user0 = userEnv.getLoginUserEnvironment(USER0, Component.COMMUNITIES.toString());
		comm0Service = user0.getCommunitiesService();
				
		user3 = userEnv.getLoginUserEnvironment(USER3, Component.COMMUNITIES.toString());
		comm3Service = user3.getCommunitiesService();
		
		user5 = userEnv.getLoginUserEnvironment(USER5, Component.COMMUNITIES.toString());
		comm5Service = user5.getCommunitiesService();
		
		user6 = userEnv.getLoginUserEnvironment(USER6, Component.COMMUNITIES.toString());
		comm6Service = user6.getCommunitiesService();
				
		// for wikis
		user0 = userEnv.getLoginUserEnvironment(USER0, Component.WIKIS.toString());
		wikis0Service = user0.getWikisService();
		
		user3 = userEnv.getLoginUserEnvironment(USER3, Component.WIKIS.toString());
		wikis3Service = user3.getWikisService();
		
		user5 = userEnv.getLoginUserEnvironment(USER5, Component.WIKIS.toString());
		wikis5Service = user5.getWikisService();
		
		user6 = userEnv.getLoginUserEnvironment(USER6, Component.WIKIS.toString());
		wikis6Service = user6.getWikisService();
		
		// create wikis data for test
		populateWikisData();

		LOGGER.debug("Finished Initializing Test");
	}
	
	public static void populateWikisData(){
		// NOTE:
		// org admin just exists on cloud server and there is NO standalone wiki on cloud, so all the cases will try community wiki
		// when to create a community, there would be an embeded wiki created automatically.		
		
		String timeStamp = Utils.logDateFormatter.format(new Date());
		
		
		LOGGER.debug("Step 1: User3 creates community comm3 and gets its embeded wiki");
		// create a community
		String comName = "Comunity_user3_"+ timeStamp;
		Community newCommunity = new Community(comName, "Private community test.", Permissions.PRIVATE, null);
		Entry communityResult = (Entry) comm3Service.createCommunity(newCommunity);
		assertEquals("create comm3 faied", 201, comm3Service.getRespStatus());

		Community comm3 = new Community((Entry) comm3Service.getCommunity(
				communityResult.getEditLinkResolvedHref().toString()));
		community_1_UUID = comm3.getUuid();
		
		// get wiki uuid
		Entry wikiEntry = (Entry) wikis3Service.getWikiOfCommunity(community_1_UUID);
		if(wikis3Service.getRespStatus() == 404){
			LOGGER.debug("wiki is not created in community directly, add wiki app in community...");	
			Widget widget = new Widget(StringConstants.WidgetID.Wiki.toString());
			comm3Service.postWidget(comm3, widget.toEntry());
			assertEquals(201, comm3Service.getRespStatus());

			wikiEntry = (Entry) wikis3Service.getWikiOfCommunity(community_1_UUID);
		} 
		wiki_1_UUID = wikiEntry.getExtension(StringConstants.TD_UUID).getText();
		
		
		LOGGER.debug("Step 2: User3 adds user5 as member to community_1_UUID");
		// make sure user5 and user3 are in the same org
		Member member = null;
		member = new Member(user5.getEmail(), user5.getUserId(), Component.COMMUNITIES, Role.MEMBER , MemberType.PERSON);
		ExtensibleElement memberEntry =comm3Service.addMemberToCommunity(comm3, member);
		assertEquals(201, comm3Service.getRespStatus());
		LOGGER.debug("memberEntry: " + memberEntry.toString());
		
		
		LOGGER.debug("Step 3: User3 creates a page under the embeded wiki in community_1_UUID");
		String pageLabel = "WIKI PAGE" + timeStamp;
		WikiPage newWikiPage = new WikiPage(pageLabel,
				"<p>This is James's wiki page.</p>",
				"wikipagetag1 wikipagetag2 wikipagetag3");
		pageLabel = newWikiPage.getTitle();
		ExtensibleElement pageEntry = wikis3Service.createWikiPageInCommunity(community_1_UUID, newWikiPage);
		assertEquals("create page", 201, wikis3Service.getRespStatus());		
		page_1_UUID = pageEntry.getExtension(StringConstants.TD_UUID).getText();		
		
		
		LOGGER.debug("Step 4: User3 creates a draft directly in the embeded wiki");
		String draftname = "draft_user3_" + timeStamp;
		String content = "draft_content_" + timeStamp;		
		ExtensibleElement draftEntryResult = wikis3Service.createWikiDraftInCommunity(community_1_UUID, "&source=external", draftname, content);
		draft_1_UUID = draftEntryResult.getExtension(StringConstants.TD_UUID).getText();
		assertEquals("create draft", 201, wikis3Service.getRespStatus());		
		
		
		LOGGER.debug("Step 5: User3 creates a draft for a page in the embeded wiki");
		draftname = "page_draft_user3_" + timeStamp;
		content = "page_draft_content_" + timeStamp;		
		draftEntryResult = wikis3Service.createWikiDraftForPageInCommunity(wiki_1_UUID, page_1_UUID, "&source=external", draftname, content);
		pageDraft_1_UUID = draftEntryResult.getExtension(StringConstants.TD_UUID).getText();
		assertEquals("create draft", 200, wikis3Service.getRespStatus());
	}
	
	//Temporarily disabling the existing fail test case 
	@Test(enabled = false)
	public void createDraftInCommunityWikiTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("createDraftInCommunityWikiTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- create any draft in my organization
		- POST /wiki/{communityId}/feed?draft=true&source=external
		- PUT /wiki/{wikiId}/page/{pageId}/entry?draft=true&source=external
		expecting:
		a) org admin "Amy Jones1" can create a draft in its org
        */
		String timeStamp = Utils.logDateFormatter.format(new Date());

		LOGGER.debug("CASE 1: general user creates draft under a page"); 
		LOGGER.debug("Step 1: User3 creates a page under the embeded wiki in community_1_UUID");
		// only one draft can be created for a page, so create a new page for this test
		String pageLabel = "WIKI PAGE" + timeStamp;
		WikiPage newWikiPage = new WikiPage(pageLabel, 
				"<p>This is James's wiki page.</p>",
				"");
		pageLabel = newWikiPage.getTitle();
		ExtensibleElement pageEntry = wikis3Service.createWikiPageInCommunity(community_1_UUID, newWikiPage);
		assertEquals("create page", 201, wikis3Service.getRespStatus());		
		String page_UUID = pageEntry.getExtension(StringConstants.TD_UUID).getText();	
		
		LOGGER.debug("Step 2:  Run with user3, creates a draft for page"); 
		String draftname = "draft_page_user3_" + timeStamp;
		String content = "draft_page_user3_content_" + timeStamp;		
		ExtensibleElement draftEntryResult = wikis3Service.createWikiDraftForPageInCommunity(wiki_1_UUID, page_UUID, "&source=external", draftname, content);
		String draftId = draftEntryResult.getExtension(StringConstants.TD_UUID).getText();
		assertEquals("create draft", 200, wikis3Service.getRespStatus());		
		
		
		LOGGER.debug("Step 3: User3 can access the draft");
		String url = wikis3Service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wiki_1_UUID
				+ "/draft/" + draftId 
				+ "/entry";
		String result = wikis3Service.getResponseString(url);
		assertEquals("get resource", 200, wikis3Service.getRespStatus());
		assertTrue("verify resource", result.contains(draftId));	
		
		
		LOGGER.debug("Step 4: orgAdmin can access the draft");
		result = wikis0Service.getResponseString(url);
		assertEquals("get resource", 200, wikis0Service.getRespStatus());
		assertTrue("verify resource", result.contains(draftId));
		
		
		LOGGER.debug("CASE 2: orgAdmin user creates draft under a page");
		LOGGER.debug("Step 1:  Run with ORG-ADMIN, creates a draft for page1"); 
		draftname = "draft_page_user0_" + timeStamp;
		content = "draft_page_user0_content_" + timeStamp;		
		draftEntryResult = wikis0Service.createWikiDraftForPageInCommunity(wiki_1_UUID, page_UUID, "&source=external", draftname, content);
		draftId = draftEntryResult.getExtension(StringConstants.TD_UUID).getText();
		assertEquals("create draft", 200, wikis0Service.getRespStatus());		
		
		
		LOGGER.debug("Step 2: User3 can access the draft");
		url = wikis3Service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wiki_1_UUID
				+ "/draft/" + draftId 
				+ "/entry";
		result = wikis3Service.getResponseString(url);
		assertEquals("get resource", 200, wikis3Service.getRespStatus());
		assertTrue("verify resource", result.contains(draftId));		
		
		
		LOGGER.debug("Step 3: ORG-ADMIN can access the draft");
		result = wikis0Service.getResponseString(url);
		assertEquals("get resource", 200, wikis0Service.getRespStatus());
		assertTrue("verify resource", result.contains(draftId));
		
		
		LOGGER.debug("CASE 3: general user creates draft directly in wiki");
		LOGGER.debug("Step 1:  Run with user5, creates a draft directly in wiki_1_UUID of community_1_UUID"); 
		draftname = "draft_user5_" + timeStamp;
		content = "draft_content_" + timeStamp;		
		draftEntryResult = wikis5Service.createWikiDraftInCommunity(community_1_UUID, "&source=external", draftname, content);
		draftId = draftEntryResult.getExtension(StringConstants.TD_UUID).getText();
		assertEquals("create draft", 201, wikis5Service.getRespStatus());

		
		LOGGER.debug("Step 2: User5 can access the draft");
 		url = wikis5Service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wiki_1_UUID
				+ "/draft/" + draftId 
				+ "/entry";
 		result = wikis5Service.getResponseString(url);
		assertEquals("get resource", 200, wikis5Service.getRespStatus());
		assertTrue("verify resource", result.contains(draftId));		
		
		LOGGER.debug("Step 3: User3 can NOT access the draft, it is private to user5");
 		result = wikis3Service.getResponseString(url);
		assertEquals("get resource", 403, wikis3Service.getRespStatus());		
		
		
		LOGGER.debug("Step 4: ORG-ADMIN can access the draft");
 		result = wikis0Service.getResponseString(url);
		assertEquals("get resource", 200, wikis0Service.getRespStatus());
		assertTrue("verify resource", result.contains(draftId));
		
		
		LOGGER.debug("CASE 4: orgAdmin user creates draft directly in wiki");
		LOGGER.debug("Step 1:  Run with ORG-ADMIN, creates a draft directly in wiki_1_UUID of community_1_UUID"); 
		draftname = "draft_user3_" + timeStamp;
		content = "draft_content_" + timeStamp;		
		draftEntryResult = wikis0Service.createWikiDraftInCommunity(community_1_UUID, "&source=external", draftname, content);
		draftId = draftEntryResult.getExtension(StringConstants.TD_UUID).getText();
		assertEquals("create draft", 201, wikis0Service.getRespStatus());		
		
		
		LOGGER.debug("Step 2: User3 can NOT access the draft");
 		url = wikis3Service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wiki_1_UUID
				+ "/draft/" + draftId 
				+ "/entry";
 		result = wikis3Service.getResponseString(url);
		assertEquals("get resource", 403, wikis3Service.getRespStatus());
		
		
		LOGGER.debug("Step 3: ORG-ADMIN can access the draft");
		result = wikis0Service.getResponseString(url);
		assertEquals("get resource", 200, wikis0Service.getRespStatus());
		assertTrue("verify resource", result.contains(draftId));
	}
	
	@Test
	public void getDraftInCommunityWikiTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getDraftInCommunityWikiTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- access any draft in my organization
		- GET /wiki/{wiki-id}/draft/{draft-id}/entry
		expecting:
		a) org admin "Amy Jones1" can access a draft in its org
        */

		LOGGER.debug("Step 1: ORG-ADMIN can access the draft for page");
		String url = wikis0Service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wiki_1_UUID
				+ "/draft/" + pageDraft_1_UUID 
				+ "/entry";
		String result = wikis0Service.getResponseString(url);
		assertEquals("get resource", 200, wikis0Service.getRespStatus());
		assertTrue("verify resource", result.contains(pageDraft_1_UUID));
		
		LOGGER.debug("Step 2: ORG-ADMIN can access the direct draft");
 		url = wikis0Service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wiki_1_UUID
				+ "/draft/" + draft_1_UUID 
				+ "/entry";
 		result = wikis0Service.getResponseString(url);
		assertEquals("get resource", 200, wikis0Service.getRespStatus());
		assertTrue("verify resource", result.contains(draft_1_UUID));
	}	
	
	@Test
	public void getPageDraftsInCommunityWikiTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getPageDraftsInCommunityWikiTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- access resources in my organization
		- GET /wiki/{wiki-label}/page/{page-label}/drafts/feed
		expecting:
		a) org admin "Amy Jones1" can access resources in its org
        */

		LOGGER.debug("Step 1: ORG-ADMIN can access the drafts for page");
		String url = wikis0Service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wiki_1_UUID
				+ "/page/" + page_1_UUID 
				+ "/drafts/feed";
		String result = wikis0Service.getResponseString(url);
		assertEquals("get resource", 200, wikis0Service.getRespStatus());
		assertTrue("verify resource", result.contains(pageDraft_1_UUID));
	}		
	
	@Test
	public void updateDraftInCommunityWikiTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("updateDraftInCommunityWikiTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- access resources in my organization
		- PUT /wiki/{wiki-label}/draft/{draft-id}/entry
		expecting:
		a) org admin "Amy Jones1" can access resources in its org
        */

		LOGGER.debug("Step 1: ORG-ADMIN can update the draft");
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String newTitle = "updated_draft_title_" + timeStamp;
		Entry updatedDraftEntry = Abdera.getNewFactory().newEntry();
		updatedDraftEntry.addCategory("tag:ibm.com,2006:td/type", "draft", "draft");
		updatedDraftEntry.setTitle(newTitle);
		Entry result = (Entry) wikis0Service.updateDraft(wiki_1_UUID, draft_1_UUID, updatedDraftEntry);
		assertEquals("get resource", 200, wikis0Service.getRespStatus());
		assertEquals("get resource", newTitle, result.getTitle());
		
		LOGGER.debug("Step 2: User3 can access the draft");
 		String url = wikis3Service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wiki_1_UUID
				+ "/draft/" + draft_1_UUID 
				+ "/entry";
 		String getResult = wikis3Service.getResponseString(url);
		assertEquals("get resource", 200, wikis3Service.getRespStatus());
		assertTrue("verify resource", getResult.contains(newTitle));	
	}		
	
	@Test
	public void deleteDraftInCommunityWikiTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("deleteDraftInCommunityWikiTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- access resources in my organization
		- DELETE /wiki/{wiki-label}/draft/{draft-id}/entry
		expecting:
		a) org admin "Amy Jones1" can access resources in its org
        */

		LOGGER.debug("Step 1:  Run with user3, creates a draft directly in wiki_1_UUID of community_1_UUID"); 
		String timeStamp = Utils.logDateFormatter.format(new Date());
		String draftname = "draft_user3_" + timeStamp;
		String content = "draft_content_" + timeStamp;		
		ExtensibleElement draftEntryResult = wikis3Service.createWikiDraftInCommunity(community_1_UUID, "&source=external", draftname, content);
		String draftId = draftEntryResult.getExtension(StringConstants.TD_UUID).getText();
		assertEquals("create draft", 201, wikis3Service.getRespStatus());
		
		LOGGER.debug("Step 2: User3 can access the draft");
 		String url = wikis3Service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wiki_1_UUID
				+ "/draft/" + draftId 
				+ "/entry";
 		String getResult = wikis3Service.getResponseString(url);
		assertEquals("get resource", 200, wikis3Service.getRespStatus());
		assertTrue("verify resource", getResult.contains(draftname));	
		
		LOGGER.debug("Step 3: ORG-ADMIN can delete the draft");
		assertEquals("get resource", 200, wikis0Service.deleteItem(url));
		
		LOGGER.debug("Step 4: User3 can NOT access the draft");
 		getResult = wikis3Service.getResponseString(url);
		assertEquals("get resource", 404, wikis3Service.getRespStatus());
	}	
	
	@Test
	public void getWikiPageBreadcrumbsTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("getWikiPageBreadcrumbsTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- access resources in my organization
		- GET /wiki/{wikiId}/navigation/{pageId}/entry
		- structure of case:
		PAGE_2
		  |
		  |- PAGE_2_1
		        |
		        |- PAGE_2_1_1
		  |- PAGE_2_2
		expecting:
		a) org admin "Amy Jones1" can access resources in its org
        */

		LOGGER.debug("Step 1: User3 creates another page PAGE_2 under the embeded wiki in community_1_UUID");
		WikiPage newWikiPage = new WikiPage("WIKI_PAGE_2_",
				"<p>This is James's wiki page.</p>",
				"wikipagetag1 wikipagetag2 wikipagetag3");
		ExtensibleElement page2Entry = wikis3Service.createWikiPageInCommunity(community_1_UUID, newWikiPage);
		assertEquals("create page", 201, wikis3Service.getRespStatus());
		page_2_UUID = page2Entry.getExtension(StringConstants.TD_UUID).getText();
		ExtensibleElement ee = page2Entry.getExtension(StringConstants.TD_LIBRARY);
		String wiki_library_id = ee.getExtension(StringConstants.TD_LIBRARY_UUID).getText();		
		
		
		LOGGER.debug("Step 2: User3 creates page PAGE_2_1 under PAGE_2");
		newWikiPage = new WikiPage("WIKI_PAGE_2_1_",
				"<p>This is James's wiki page.</p>",
				"wikipagetag1 wikipagetag2 wikipagetag3");
		ExtensibleElement page2_1_Entry = wikis3Service.createChildPageUnderWikiPage(wiki_library_id, page_2_UUID, newWikiPage);
		assertEquals("create page: page2_1_Entry: ", 201, wikis3Service.getRespStatus());		
		page_2_1_UUID = page2_1_Entry.getExtension(StringConstants.TD_UUID).getText();				
		
		
		LOGGER.debug("Step 3: User3 creates page PAGE_2_2 under PAGE_2");
		newWikiPage = new WikiPage("WIKI_PAGE_2_2_",
				"<p>This is James's wiki page.</p>",
				"wikipagetag1 wikipagetag2 wikipagetag3");
		ExtensibleElement page2_2_Entry = wikis3Service.createChildPageUnderWikiPage(wiki_library_id, page_2_UUID, newWikiPage);
		assertEquals("create page: page2_2_Entry: ", 201, wikis3Service.getRespStatus());		
		page_2_2_UUID = page2_2_Entry.getExtension(StringConstants.TD_UUID).getText();		
		
		
		LOGGER.debug("Step 4: User3 creates page PAGE_2_1_1 under PAGE_2_1");
		newWikiPage = new WikiPage("WIKI_PAGE_2_1_1_",
				"<p>This is James's wiki page.</p>",
				"wikipagetag1 wikipagetag2 wikipagetag3");
		ExtensibleElement page2_1_1_Entry = wikis3Service.createChildPageUnderWikiPage(wiki_library_id, page_2_1_UUID, newWikiPage);
		assertEquals("create page: page2_1_Entry: ", 201, wikis3Service.getRespStatus());		
		page_2_1_1_UUID = page2_1_1_Entry.getExtension(StringConstants.TD_UUID).getText();
		
		LOGGER.debug("Step 5: User3 can access PAGE_2_1_1 WIHTOU the breadcrumb by default");
		String url = wikis3Service.getServiceURLString()  
 				+ URLConstants.WIKI_PAGE_URL_PREFIX + "/" + wiki_library_id
				+ "/navigation/" + page_2_1_1_UUID 
				+ "/entry";				
		String getResult = wikis3Service.getResponseString(url);
		assertEquals("get resource", 200, wikis3Service.getRespStatus());
		assertNotNull("get getResult", getResult);
		assertTrue("verify breadcrumbs", !getResult.contains("breadcrumb"));			
		
		LOGGER.debug("Step 6:  User3 can access the breadcrumb of PAGE_2_1_1 in xml format by default");
 		getResult = wikis3Service.getResponseString(url + "?includeBreadcrumbs=true");
		assertEquals("get resource", 200, wikis3Service.getRespStatus());
		assertNotNull("get getResult", getResult);		
		assertTrue("verify breadcrumbs", getResult.contains("breadcrumb"));	
		assertTrue("verify PAGE_2_1_1", getResult.contains(page_2_1_1_UUID));
		assertTrue("verify PAGE_2_1", getResult.contains(page_2_1_UUID));
		assertTrue("verify PAGE_2", getResult.contains(page_2_UUID));	
		assertTrue("verify page_2_2_UUID", !getResult.toString().contains(page_2_2_UUID));			
		
		LOGGER.debug("Step 7:  User3 can access the breadcrumb of PAGE_2_1_1 in JSON ");
 		getResult = wikis3Service.getResponseString(url + "?includeBreadcrumbs=true&format=json");
		assertEquals("get resource", 200, wikis3Service.getRespStatus());
		assertNotNull("get getResult", getResult);		
		JSONObject result = new JSONObject(getResult);
		JSONArray breadcrumbs= result.getJSONArray("breadcrumbs");
		assertNotNull("get breadcrumbs", breadcrumbs);
		assertEquals("verify length", breadcrumbs.length(), 3);	
		assertTrue("verify page_2_UUID", breadcrumbs.toString().contains(page_2_UUID));	
		assertTrue("verify page_2_1_UUID", breadcrumbs.toString().contains(page_2_1_UUID));	
		assertTrue("verify page_2_1_1_UUID", breadcrumbs.toString().contains(page_2_1_1_UUID));	
		assertTrue("verify page_2_2_UUID", !breadcrumbs.toString().contains(page_2_2_UUID));				

		LOGGER.debug("Step 8: ORG-ADMIN can access the breadcrumb of PAGE_2_1_1");
		getResult = wikis0Service.getResponseString(url+ "?includeBreadcrumbs=true&format=json");
		assertEquals("get resource", 200, wikis0Service.getRespStatus());	
		assertNotNull("get getResult", getResult);
		result = new JSONObject(getResult);
		breadcrumbs= result.getJSONArray("breadcrumbs");
		assertNotNull("get breadcrumbs", breadcrumbs);
		assertEquals("verify length", breadcrumbs.length(), 3);	
		assertTrue("verify page_2_UUID", breadcrumbs.toString().contains(page_2_UUID));	
		assertTrue("verify page_2_1_UUID", breadcrumbs.toString().contains(page_2_1_UUID));	
		assertTrue("verify page_2_1_1_UUID", breadcrumbs.toString().contains(page_2_1_1_UUID));	
		assertTrue("verify page_2_2_UUID", !breadcrumbs.toString().contains(page_2_2_UUID));	
	}
	
	public void createAndDeleteWikiPageLockTest() throws Exception {
		LOGGER.debug("===========================================");
		LOGGER.debug("createAndDeleteWikiPageLockTest()");
		LOGGER.debug("===========================================");
		/* 
		TEST CASE:
		when to call API below by org admin:
		- access resources in my organization
		- PATCH /wiki/{wikiId}/page/{pageId}/entry
		expecting:
		a) org admin "Amy Jones1" can create/delete lock in its org
        */
		
		LOGGER.debug("Step 1: User3 can create lock for page_1_UUID");
		wikis3Service.patchPageLock(wiki_1_UUID, page_1_UUID, "hard");
		assertEquals("create lock", 204, wikis3Service.getRespStatus());
		
		LOGGER.debug("Step 1.1: User3 can create lock again for page_1_UUID");
		wikis3Service.patchPageLock(wiki_1_UUID, page_1_UUID, "hard");
		assertEquals("create lock", 204, wikis3Service.getRespStatus());			
		
		LOGGER.debug("Step 2: User3 can delete lock for page_1_UUID");
		wikis3Service.patchPageLock(wiki_1_UUID, page_1_UUID, "none");
		assertEquals("delete lock", 204, wikis3Service.getRespStatus());			
		
		LOGGER.debug("Step 2.1: User3 can delete lock again for page_1_UUID");
		wikis3Service.patchPageLock(wiki_1_UUID, page_1_UUID, "none");
		assertEquals("delete lock", 204, wikis3Service.getRespStatus());	
		

		LOGGER.debug("Step 3: ORG-ADMIN can create lock for page_1_UUID");
		wikis0Service.patchPageLock(wiki_1_UUID, page_1_UUID, "hard");
		assertEquals("create lock", 204, wikis0Service.getRespStatus());		

		LOGGER.debug("Step 4: ORG-ADMIN can delete lock for page_1_UUID");
		wikis0Service.patchPageLock(wiki_1_UUID, page_1_UUID, "none");
		assertEquals("delete lock", 204, wikis0Service.getRespStatus());
		

		LOGGER.debug("Step 5: User5 can create lock for page_1_UUID");
		wikis5Service.patchPageLock(wiki_1_UUID, page_1_UUID, "hard");
		assertEquals("create lock", 204, wikis5Service.getRespStatus());			
		
		LOGGER.debug("Step 6: User5 can delete lock for page_1_UUID");
		wikis5Service.patchPageLock(wiki_1_UUID, page_1_UUID, "none");
		assertEquals("delete lock", 204, wikis5Service.getRespStatus());
		

		LOGGER.debug("Step 7: User6 can NOT create lock for page_1_UUID");
		wikis6Service.patchPageLock(wiki_1_UUID, page_1_UUID, "hard");
		assertEquals("create lock", 403, wikis6Service.getRespStatus());			
		
		LOGGER.debug("Step 8: User6 can NOT delete lock for page_1_UUID");
		wikis6Service.patchPageLock(wiki_1_UUID, page_1_UUID, "none");
		assertEquals("delete lock", 403, wikis6Service.getRespStatus());
	}
	
	@AfterClass
	public static void tearDown() {
		comm0Service.tearDown();
		comm3Service.tearDown();
		comm5Service.tearDown();
		wikis0Service.tearDown();
		wikis3Service.tearDown();
		wikis5Service.tearDown();
	}

}
