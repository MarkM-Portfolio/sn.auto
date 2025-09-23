package com.ibm.conn.auto.tests.calendar.regression;

import java.util.ArrayList;
import java.util.List;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.member.Member;
import com.ibm.conn.auto.appobjects.role.CommunityRole;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.menu.Community_LeftNav_Menu;
import com.ibm.conn.auto.util.menu.Community_View_Menu;
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class Comments extends SetUpMethods2{

	private static final Logger log = LoggerFactory.getLogger(Comments.class);
	private TestConfigCustom cfg;	
	private CommunitiesUI ui;
	private CalendarUI calUI;
	private User testUser;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity.Access defaultAccess;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
		
		// Initialise the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);
		String serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		//Load User
		testUser = cfg.getUserAllocator().getUser();
		log.info("INFO: Using test user: " + testUser.getDisplayName());

		apiOwner = new APICommunitiesHandler(serverURL, testUser.getAttribute(cfg.getLoginPreference()), testUser.getPassword());
	   
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = CommunitiesUI.getGui(cfg.getProductName(), driver);
		calUI = CalendarUI.getGui(cfg.getProductName(), driver);

		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());
		
	}	
	
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if an email can be used as a mention in a comment.
	 *<li><B>Step: </B>Create a community with default access.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Step: </B>Add an @mention using the user's email to the comment field and select the user from the mention typeahead.
	 *<li><B>Step: </B>Add some other text to the comment editor and save the changes.
	 *<li><B>Verify: </B>The email mention and the text appears as a comment for the event.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void Mention_Basic_Email() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		String sEmail = testUser.getEmail();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				
		
		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		logger.strongStep("Add an @mention using the user's email to the comment field and verify the mention typeahead appears");
		log.info("INFO: Start input @mention using the user's email and verify the mention typeahead appears");
		ui.fluentWaitElementVisibleOnce(BaseUIConstants.CKEditor_iFrame);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay("@"+ sEmail.substring(0, 2) );
		Assert.assertTrue(driver.isElementPresent(calUI.mentionTypeahead()), "ERROR: mention typahead doesn't show");
		
		logger.strongStep("Add the remaining characters from the email to the mention and select the user from the mention typeahead");
		log.info("INFO: Input the remaining characters from the email to the mention and select the user from the mention typeahead");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay(sEmail.substring(2));
		calUI.mention_addMember(sEmail);
		String actualText = getCKEditorContents();
		
		logger.strongStep("Enter some text in the editor, verify the text area contains the email and then save the changes");
		log.info("INFO: Add a text to the comment text area, verify the editor contains the email and then save the changes");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).type("test");
		Assert.assertTrue(actualText.contentEquals("@" + sEmail), "ERROR: mention text is not correct. Expected: <@" + sEmail + "> but was: <" + actualText + ">");
		calUI.clickLinkWait(CalendarUI.AddCommentSaveButton);
		
		logger.strongStep("Verify the email mention and the text appears as a comment for the event");
		log.info("INFO: Verify the email mention and the text appears as a comment for the event");
		actualText = driver.getSingleElement(CalendarUI.CommentContent).getText();
		Assert.assertTrue(actualText.contentEquals("@" + sEmail + "test"), "ERROR: mention content is incorrect. " + "Expected: <@" + sEmail + "test> but was: <" + actualText + ">");
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if multiple mentions work.
	 *<li><B>Step: </B>Create a community and add two members to it.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Step: </B>Add the @mention for both members of the community.
	 *<li><B>Verify: </B>Both mentions should be links in the Comments area for the event.
	 *<li><B>Step: </B>Hover over the mention link for member1.
	 *<li><B>Verify: </B>The business card should appear.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void MultipleMention() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		User user1 = cfg.getUserAllocator().getUser();
		User user2 = cfg.getUserAllocator().getUser();
		
		String sDisplayName1 = user1.getDisplayName();	
		String sDisplayName2 = user2.getDisplayName();

		List<Member> members;
		members = new ArrayList<Member>();	
		members.add(new Member(CommunityRole.MEMBERS, user1));
		members.add(new Member(CommunityRole.MEMBERS, user2));
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)														
														 .addMembers(members)
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() + " widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);
		
		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				
		
		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));		
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		logger.strongStep("Add the @mention for member1 and then member2");
		log.info("INFO: Start input @mention and mention member1 and member2");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay("@"+ sDisplayName1 );
		calUI.mention_addMember(sDisplayName1);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay(" @"+ sDisplayName2 );
		calUI.mention_addMember(sDisplayName2);
		
		logger.strongStep("Save the mentions and verify the links for both user profiles appear as a comment for the event");
		log.info("INFO: Click on Save button and verify the links for both user profiles appear as a comment for the event");
		calUI.clickLinkWait(CalendarUI.AddCommentSaveButton);
		Assert.assertTrue(driver.getSingleElement(CalendarUI.CommentContent).getText().equals("@" + sDisplayName1 + " " + "@" + sDisplayName2), "ERROR: mention content is incorrect");
		
		Assert.assertTrue(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName1)), "ERROR: mentioned member is not a link");
		Assert.assertTrue(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName2)), "ERROR: mentioned member is not a link");
		
		logger.strongStep("Hover over the mention link for member1 and verify his business card appears");
		log.info("INFO: Move the mouse over the mention link for member1 and verify his business card appears");
		driver.getSingleElement(calUI.getMentionPersonLink(sDisplayName1)).hover();
		calUI.verifyBizCard();
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if one can delete the mentions from a comment.
	 *<li><B>Step: </B>Create a community and add two members to it.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Step: </B>Add the @mention for both members of the community along with some other text.
	 *<li><B>Verify: </B>Both mentions should be links in the Comments area for the event.
	 *<li><B>Step: </B>Click on the Delete button in the Comments area.
	 *<li><B>Verify: </B>None of the mentions should appear anymore.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void DeleteMentionComment() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		User user1 = cfg.getUserAllocator().getUser();
		User user2 = cfg.getUserAllocator().getUser();
		
		String sDisplayName1 = user1.getDisplayName();	
		String sDisplayName2 = user2.getDisplayName();
		
		List<Member> members;
		members = new ArrayList<Member>();	
		members.add(new Member(CommunityRole.MEMBERS, user1));
		members.add(new Member(CommunityRole.MEMBERS, user2));
		
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														 .addMembers(members)
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				
		
		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));	
		calUI.fluentWaitElementVisible(CalendarUI.AddAComment);
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		logger.strongStep("Add the @mention for member1 followed by some text and then the mention for member2");
		log.info("INFO: Mention member1 followed by some text and then mention member2");
		calUI.fluentWaitElementVisibleOnce(BaseUIConstants.CKEditor_iFrame);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay("@"+ sDisplayName1);
		calUI.mention_addMember(sDisplayName1);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay(" test @"+ sDisplayName2);
		calUI.mention_addMember(sDisplayName2);
		
		logger.strongStep("Save the mentions and verify the links for both user profiles appear as a comment for the event");
		log.info("INFO: Click on Save button and verify the links for both user profiles appear as a comment for the event");
		calUI.clickLinkWait(CalendarUI.AddCommentSaveButton);
		Assert.assertEquals(driver.getSingleElement(CalendarUI.CommentContent).getText(), "@" + sDisplayName1 + " test " + "@" + sDisplayName2
							, "ERROR: mention content is incorrect");
		
		Assert.assertTrue(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName1)), "ERROR: mentioned member is not a link");
		Assert.assertTrue(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName2)), "ERROR: mentioned member is not a link");
		
		logger.strongStep("Click on the Delete button in the Comments area and verify that none of the mentions appear anymore");
		log.info("INFO: Delete the comment and verify that none of the mentions appear anymore");
		driver.getFirstElement("link=Delete").click();
		calUI.clickLinkWait(CalendarUI.ConfirmDialogDeleteButton);
		calUI.fluentWaitTextNotPresent(sDisplayName1);
		driver.turnOffImplicitWaits();
		Assert.assertFalse(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName1)), "ERROR: mentioned member still exists");
		Assert.assertFalse(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName2)), "ERROR: mentioned member still exists");
		driver.turnOnImplicitWaits();
		
		calUI.endTest();
	}

	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if one can delete a mention in a comment using Backspace key once.
	 *<li><B>Step: </B>Create a community and add two members to it.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Step: </B>Add some text and mentions in the comment editor.
	 *<li><B>Step: </B>Use Backspace key to delete mentions.
	 *<li><B>Verify: </B>The mentions can be deleted successfully.
	 *<li><B>Step: </B>Save the changes.
	 *<li><B>Verify: </B>The deleted mention should not appear in the Comments area of the event.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void DeleteMentionText_Backspace() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		User user1 = cfg.getUserAllocator().getUser();
		User user2 = cfg.getUserAllocator().getUser();
		
		String sDisplayName1 = user1.getDisplayName();	
		String sDisplayName2 = user2.getDisplayName();		

		List<Member> members;
		members = new ArrayList<Member>();	
		members.add(new Member(CommunityRole.MEMBERS, user1));
		members.add(new Member(CommunityRole.MEMBERS, user2));
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														 .addMembers(members)
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				
		
		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));	
		calUI.fluentWaitElementVisible(CalendarUI.AddAComment);
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		logger.strongStep("Enter some text in the Comments text area");
		log.info("INFO: Add a text to the editor");
		calUI.fluentWaitElementVisible(BaseUIConstants.CKEditor_iFrame);
		driver.typeNative("test delete mentions function ");
		
		//check backspace during input
		logger.strongStep("Add the @mention for member1 then delete all characters from the mention except the first one and "
				+ "verify the mention typeahead does not appear");
		log.info("INFO: Input the @mention for member1 then delete all characters from the mention except the first one and "
				+ "verify the mention typeahead does not appear");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay("@"+ sDisplayName1);
		for(int i=0;i<sDisplayName1.length()-1;i++) driver.typeNative(Keys.BACK_SPACE);
		calUI.fluentWaitTextNotPresentWithoutRefresh(sDisplayName1);
		List<Element> visibleTypeaheads = driver.getVisibleElements(calUI.mentionTypeahead());
		Assert.assertEquals(visibleTypeaheads.size(), 0, "ERROR: the mention typeahead shows up when only input one character");
		
		logger.strongStep("Delete the last remaining character from the mention and then add the mention back again");
		log.info("INFO: Delete the final character from the mention and then add the mention back again");
		driver.typeNative(Keys.BACK_SPACE);
		driver.typeNative(sDisplayName1);
		calUI.mention_addMember(sDisplayName1);
		logger.strongStep("Add some text to the comment editor followed by the mention for member2");
		log.info("INFO: Enter some text in the comment editor followed by the mention for member2");
		driver.typeNative(" test ");
		driver.typeNative("@" + sDisplayName2);
		calUI.mention_addMember(sDisplayName2);
		String s1 = "test delete mentions function " + "@" + sDisplayName1 + " test " + "@" + sDisplayName2;
		String s2 = "test delete mentions function " + "@" + sDisplayName1 + " test";
		String actualText = getCKEditorContents();
		logger.strongStep("Verify that the comments editor contains the mentions and the text");
		log.info("INFO: Check that the comments editor contains the mentions and the text");
		Assert.assertEquals(s1, actualText
				, "ERROR: mention content is incorrect, it should be " + s1);
		logger.strongStep("Delete the mention for member2 with a single press of the Backspace key and verify "
				+ "the mention for member2 does not appear in the comments text area anymore");
		log.info("INFO: Press the Backspace key once and verify the mention for member2 is removed from the comments text area");
		driver.typeNative(Keys.BACK_SPACE);
		actualText = getCKEditorContents();
		Assert.assertEquals(s2, actualText
				, "ERROR: mention content is incorrect, it should be " + s2);
		logger.strongStep("Save the changes and verify the mention for member1 appears as a comment for the event and the "
				+ "mention for member2 does not appear in the Comments area");
		log.info("INFO: Click on the Save button and verify the mention for member1 appears as a comment for the event and the "
				+ "mention for member2 does not appear in the Comments area");
		calUI.clickLinkWait(CalendarUI.AddCommentSaveButton);		
		Assert.assertTrue(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName1)), "ERROR: mentioned member doesn't exists");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName2)), "ERROR: mentioned member still exists");
		driver.turnOnImplicitWaits();
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if one can delete the entire content of a comment including mentions.
	 *<li><B>Step: </B>Create a community and add two members to it.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Step: </B>Add some text and mentions in the comment editor.
	 *<li><B>Verify: </B>The text and mentions appear in the comments text area.
	 *<li><B>Step: </B>Clear everything in the comments test area.
	 *<li><B>Verify: </B>The mentions can be deleted successfully.
	 *<li><B>Step: </B>Try to save the comment.
	 *<li><B>Verify: </B>The empty comment error message should be thrown and the comment is not saved.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void DeleteSelectedMentionText() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		User user1 = cfg.getUserAllocator().getUser();
		User user2 = cfg.getUserAllocator().getUser();
		
		String sDisplayName1 = user1.getDisplayName();	
		String sDisplayName2 = user2.getDisplayName();

		List<Member> members;
		members = new ArrayList<Member>();	
		members.add(new Member(CommunityRole.MEMBERS, user1));
		members.add(new Member(CommunityRole.MEMBERS, user2));
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														 .addMembers(members)
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() + " widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//Choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);

		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));	
		calUI.fluentWaitElementVisible(CalendarUI.AddAComment);
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		logger.strongStep("Enter some text in the Comments text area");
		log.info("INFO: Add a text to the editor");
		ui.fluentWaitElementVisibleOnce(BaseUIConstants.CKEditor_iFrame);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).type("test delete mentions function ");
		
		logger.strongStep("Enter the mention for member1 followed by the mention for member2 in the comments text area");
		log.info("INFO: Input the mention for member1 then the mention for member2 in the comments editor");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).type("@"+ sDisplayName1 );
		calUI.mention_addMember(sDisplayName1);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).type(" test ");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).type("@"+ sDisplayName2 );
		calUI.mention_addMember(sDisplayName2);

		logger.strongStep("Verify the text and both mentions appear in the comments text area");
		log.info("INFO: Check that the text and both mentions appear in the comments editor");
		String s1 = "test delete mentions function " + "@" + sDisplayName1 + " test " + "@" + sDisplayName2;
		String actualText = getCKEditorContents();
		Assert.assertEquals(s1, actualText, "ERROR: mention content is incorrect, it should be " + s1);
		
		logger.strongStep("Clear everything in the comments test area");
		log.info("INFO:Clear everything in the comments test area");
		ui.switchToFrame(BaseUIConstants.CKEditor_iFrame, BaseUIConstants.StatusUpdate_Body);
		ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_Body).clear();
		ui.switchToTopFrame();
		actualText = getCKEditorContents();
		
		logger.strongStep("Verify that the comments text area does not contain anything");
		log.info("Make sure that the comments editor has not contents anymore");
		Assert.assertEquals("", actualText, "ERROR: mention content is incorrect, it should be empty");
		
		logger.strongStep("Try to save the comment and verify the empty comment error message is thrown");
		log.info("INFO: Click on the Save button and verify the empty comment error message is received");
		calUI.clickLinkWait(CalendarUI.AddCommentSaveButton);		
		Assert.assertTrue(driver.isTextPresent(Data.getData().EmtpyCommentErrorMsg), "ERROR: no empty comment msg");		
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if one can delete a mention in a comment using the left arrow key followed by the Delete key.
	 *<li><B>Step: </B>Create a community and add two members to it.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Step: </B>Add some text and mentions in the comment editor.
	 *<li><B>Verify: </B>The text and mentions appear in the comments text area.
	 *<li><B>Step: </B>Press the left arrow key and then the Delete key.
	 *<li><B>Verify: </B>The mention for member2 should be deleted.
	 *<li><B>Step: </B>Save the comment.
	 *<li><B>Verify: </B>The mention for member1 appears in the Comments area of the event.
	 *<li><B>Verify: </B>The mention for member2 is not a part of the comment.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void DeleteMentionText_LeftDel() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		User user1 = cfg.getUserAllocator().getUser();
		User user2 = cfg.getUserAllocator().getUser();
		
		String sDisplayName1 = user1.getDisplayName();	
		String sDisplayName2 = user2.getDisplayName();
		

		List<Member> members;
		members = new ArrayList<Member>();	
		members.add(new Member(CommunityRole.MEMBERS, user1));
		members.add(new Member(CommunityRole.MEMBERS, user2));
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														 .addMembers(members)
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
		
		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);			
		
		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));	
		calUI.fluentWaitElementVisible(CalendarUI.AddAComment);
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		logger.strongStep("Enter some text in the Comments text area");
		log.info("INFO: Add a text to the editor");
		ui.fluentWaitElementVisibleOnce(BaseUIConstants.CKEditor_iFrame);
		driver.typeNative("test delete mentions function ");
		
		logger.strongStep("Enter the mention for member1 followed by the mention for member2 in the comments text area");
		log.info("INFO: Input the mention for member1 then the mention for member2 in the comments editor");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay("@"+ sDisplayName1);
		calUI.mention_addMember(sDisplayName1);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay(" test @"+ sDisplayName2);
		calUI.mention_addMember(sDisplayName2);

		logger.strongStep("Verify the text and both mentions appear in the comments text area");
		log.info("INFO: Check that the text and both mentions appear in the comments editor");
		String s1 = "test delete mentions function " + "@" + sDisplayName1 + " test " + "@" + sDisplayName2;
		String s2 = "test delete mentions function " + "@" + sDisplayName1 + " test";
		String actualText = getCKEditorContents();
		Assert.assertEquals(s1, actualText
				, "ERROR: mention content is incorrect, it should be " + s1);
		
		logger.strongStep("Press the left arrow key and then the Delete key to delete the mention for user2");
		log.info("INFO: Hit the left arrow key and then the Delete key to delete the mention for user2");
		driver.typeNative(Keys.LEFT, Keys.DELETE);
		actualText = getCKEditorContents();
		
		logger.strongStep("Verify the mention for member2 disappears and save the changes");
		log.info("INFO: Check that the mention for member2 does not appear anymore and click on the Save button");
		Assert.assertEquals(s2, actualText
				, "ERROR: mention content is incorrect, it should be " + s2);
		calUI.clickLinkWait(CalendarUI.AddCommentSaveButton);	
		
		logger.strongStep("Verify that the mention for member1 appears in the Comments area of the event and the mention for "
				+ "member2 is not a part of the comment");
		log.info("INFO: Confirm that the mention for member1 appears in the Comments area of the event and the mention for "
				+ "member2 is not a part of the comment");
		Assert.assertTrue(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName1)), "ERROR: mentioned member doesn't exists for " + sDisplayName1);
		driver.turnOffImplicitWaits();
		Assert.assertFalse(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName2)), "ERROR: mentioned member still exists for " +sDisplayName2 );		
		driver.turnOnImplicitWaits();		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if a mention will link to profile.
	 *<li><B>Step: </B>Create a community and add one member to it.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Step: </B>Add mention for the member in the comment editor and save the changes.
	 *<li><B>Step: </B>Click on the mention from the Comments area of the event. 
	 *<li><B>Verify: </B>The profile of the member opens up.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void MentionLinktoProfile() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		User user1 = cfg.getUserAllocator().getUser();
		String sDisplayName1 = user1.getDisplayName();	
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														 .addMember(new Member(CommunityRole.MEMBERS, user1))
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() + " widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
	
		//Choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);			
		
		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));	
		calUI.fluentWaitElementVisible(CalendarUI.AddAComment);
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		logger.strongStep("Add the mention for the member and save the changes");
		log.info("INFO: Input @mention for the member and click on the Save button");
		ui.fluentWaitElementVisibleOnce(BaseUIConstants.CKEditor_iFrame);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).type("@"+ sDisplayName1 );
		calUI.mention_addMember(sDisplayName1);
		calUI.clickLinkWait(CalendarUI.AddCommentSaveButton);
		
		logger.strongStep("Click on the mention from the Comments area of the event and verify the profile of the member opens up");
		log.info("INFO: Confirm that clicking on the mention from the Comments area of the event opens the profile of the member");
		calUI.clickLink(calUI.getMentionPersonLink(sDisplayName1));		
		calUI.fluentWaitTextPresent(sDisplayName1);
		Assert.assertTrue(driver.getTitle().contains("Profile"), "ERROR: people link doesn't redirect to Profiles");
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if you can remove mention style</li>
	 *<li><B>Step: </B>Create a community.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Step: </B>Add a partial mention to the comments editor.
	 *<li><B>Verify: </B>The mention typeahead should contain no results.
	 *<li><B>Step: </B>Use space bar to remove the mention typeahead.
	 *<li><B>Step: </B>Add a complete mention to the comments editor.
	 *<li><B>Verify: </B>The mention typeahead appears with proper results.
	 *<li><B>Step: </B>Use Escape key to remove mention typeahead.
	 *<li><B>Step: </B>Save the changes.
	 *<li><B>Verify: </B>The partial and complete mentions should not appear as links in the Comments area of the event.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void RemoveMentionStyle() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		User user1 = cfg.getUserAllocator().getUser();
		String sDisplayName1 = user1.getDisplayName();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() + " widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);				
		
		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));	
		calUI.fluentWaitElementVisible(CalendarUI.AddAComment);
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		//type in a partial mention
		logger.strongStep("Add a partial mention and verify the mention typeahead does not contain any results");
		log.info("INFO: Input a partial mention and verify there are no results in the mention typeahead");
		log.info("INFO: start input @mention");
		calUI.fluentWaitElementVisibleOnce(BaseUIConstants.CKEditor_iFrame);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay("@ww");
		Assert.assertTrue(driver.isElementPresent(calUI.mentionTypeahead()), "ERROR: mention typeahead doesn't show");
		
		//dismiss partial mention with space bar
		logger.strongStep("Dismiss the partial mention with space bar and verify that the mention typeahead does not appear");
		log.info("INFO: Hit the space bar to dismiss the partial mention and verify that the mention typeahead does not appear");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay(" ");
		driver.turnOffImplicitWaits();
		Assert.assertFalse(driver.getSingleElement(calUI.mentionTypeahead()).isVisible(), "ERROR: mention typeahead shows");
		driver.turnOnImplicitWaits();
		
		//type in a full mention
		logger.strongStep("Add a complete mention now and verify that the mention typeahead is displayed");
		log.info("INFO: Input a full mention now and verify that the mention typeahead is displayed");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).type("@"+ sDisplayName1 );
		Assert.assertTrue(driver.isElementPresent(calUI.mentionTypeahead()), "ERROR: mention typeahead doesn't show");
		
		//dismiss full mention with escape
		logger.strongStep("Dismiss the mention typeahead by hitting the Escape key");
		log.info("INFO: Close the mention typeahead by hitting the Escape key");
		driver.typeNative(Keys.ESCAPE);
		driver.turnOffImplicitWaits();
		Assert.assertFalse(driver.getSingleElement(calUI.mentionTypeahead()).isVisible(), "ERROR: mention typeahead shows");
		driver.turnOnImplicitWaits();
		
		//save comment and verify
		logger.strongStep("Save the changes and verify that the partial and complete mentions don't appear as links");
		log.info("INFO: Click on the Save button and verify that the partial and complete mentions don't appear as links");
		calUI.clickLinkWait(CalendarUI.AddCommentSaveButton);		
		driver.turnOffImplicitWaits();
		Assert.assertFalse(driver.isElementPresent(calUI.getMentionPersonLink("zz")), "ERROR: mention shows @zz as a link");
		Assert.assertFalse(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName1)), "ERROR: mention people shows for "  + sDisplayName1);
		driver.turnOnImplicitWaits();
		Assert.assertTrue(driver.isTextPresent("@ww @" + sDisplayName1),
				"ERROR: mention content is incorrect: expected to contain: \"@zz @" + "sDisplayName1" +
				"\", actual: \"" + driver.getFirstElement(CalendarUI.CommentContent).getText() + "\"");
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to confirm that a non-member can't be mentioned and a warning message is thrown if one tries to do so.
	 *<li><B>Step: </B>Create a private community and add one member to it.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Step: </B>Add the @mention for the member followed by the non-member in the comments editor.
	 *<li><B>Verify: </B>The mention error message appears for the non-member as the user is not a member of the community.
	 *<li><B>Step: </B>Save the changes.
	 *<li><B>Verify: </B>The mention link should appear for the member but not for the non-member in the Comments area of the event.
	 *<li><B>Step: </B>Hover over the mention text for the non-member.
	 *<li><B>Verify: </B>The business card of the user does not appear.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void Mentionin_nonMem() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		User member1 = cfg.getUserAllocator().getUser();	
		User member2 = cfg.getUserAllocator().getUser();
		
		String sDisplayName1 = member1.getDisplayName();	
		String sDisplayName2 = member2.getDisplayName();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(Access.RESTRICTED)
														.addMember(new Member(CommunityRole.MEMBERS, member1))
														.description("Test " + testName)
														.shareOutside(false)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() + " widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
	
		//Choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);

		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));	
		calUI.fluentWaitElementVisible(CalendarUI.AddAComment);
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		logger.strongStep("Add the @mention for the member followed by the non-member in the comments editor");
		log.info("INFO: Input @mention for the member and then @mention for the non-member");
		ui.fluentWaitElementVisibleOnce(BaseUIConstants.CKEditor_iFrame);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay("@"+ sDisplayName1 );
		calUI.mention_addMember(sDisplayName1);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay(" test @"+ sDisplayName2 );
		calUI.mention_addMember(sDisplayName2);
		
		logger.strongStep("Verify the mention error message appears for the non-member");
		log.info("INFO: Confirm that the mention error message appears for non-member");
		Assert.assertTrue(driver.isElementPresent(CalendarUI.mentionErrorMsgDiv), "ERROR: mention error msg doesn't show");
		Assert.assertTrue(driver.isTextPresent(Data.getData().mentionErrorMsg), "ERROR: Mention text shows incorrectly, expected to contain: \"" + Data.getData().mentionErrorMsg + "\"");
		
		logger.strongStep("Save the changes and verify that the mention link appears for the member but not for the non-member in the comments area of the event");
		log.info("INFO: Click on the Save button and verify that the mention link appears for the member but not for the non-member in the comments area of the event");
		calUI.clickLinkWithJavascript(CalendarUI.AddCommentSaveButton);		
		Assert.assertTrue(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName1)), "ERROR: mention people link doesn't show for" + sDisplayName1);
		Assert.assertFalse(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName2)), "ERROR: mention people link shows for" + sDisplayName2);
		
		logger.strongStep("Hover over the mention text for the non-member and verify the business card of the user does not appear");
		log.info("INFO: Move the mouse over the mention text for the non-member and verify the business card of the user does not appear");
		driver.getSingleElement("link=" + sDisplayName2).hover();
		calUI.verifyBizCard();
		
		calUI.endTest();
	}
	
	/**
	 *<B>Note: </B>Make sure the member and the guest are network contacts.
	 *<ul>
	 *<li><B>Info: </B>Tests to see if an owner can mention a guest and a guest can mention an owner.
	 *<li><B>Step: </B>Create a private community with a guest user as the member.
	 *<li><B>Step: </B>Login as the owner.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Step: </B>Mention the guest member.
	 *<li><B>Verify: </B>The mention link appears for the guest member in the Comments area of the event.
	 *<li><B>Step: </B>Login as the guest member.
	 *<li><B>Step: </B>Add a comment to the same event using the Add a comment link.
	 *<li><B>Step: </B>Mention the owner.
	 *<li><B>Verify: </B>The mention link appears for the owner in the Comments area of the event.
	 *</ul>
	 */
	@Deprecated //The sc_regression tag is related to smart cloud which is now obsolete.
	@Test(groups = {"sc_regression"}, enabled=false)
	public void MentionGuest() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		User member1 = cfg.getUserAllocator().getGroupUser("guest");
		testUser = cfg.getUserAllocator().getGroupUser("guestContact");
		String sDisplayName1 = testUser.getDisplayName();
		String sDisplayName2 = member1.getDisplayName();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.description("Test " + testName)
														.access(Access.RESTRICTED)
														.addexMember(new Member(CommunityRole.MEMBERS, member1))
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
	
		//Choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);

		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));	
		calUI.fluentWaitElementVisible(CalendarUI.AddAComment);	
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		logger.strongStep("Add the mention for the guest member");
		log.info("INFO: Input @mention for the guest member of the community");
		calUI.fluentWaitElementVisible(CalendarUI.AddCommentTextField);
		driver.getSingleElement(CalendarUI.AddCommentTextField).click();
		driver.getSingleElement(CalendarUI.AddCommentTextField).type("@" + sDisplayName2);
		calUI.mention_addMember(sDisplayName2);
		
		logger.strongStep("Save the changes and verify the mention link appears for the guest member in the Comments area "
				+ "of the event");
		log.info("INFO: Click on the Save button and verify the mention link appears for the guest member in the Comments area "
				+ "of the event");
		calUI.clickLinkWait(CalendarUI.AddCommentSaveButton);
		Assert.assertTrue(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName2)), "ERROR: mention people link doesn't show for" + sDisplayName2);
		calUI.close(cfg);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +member1.getDisplayName());
		calUI.loadComponent(Data.getData().ComponentCommunities);
		calUI.login(member1);
		
		Community_View_Menu.IM_A_MEMBER.select(ui);
		ui.clickLinkWait("link=" + community.getName());
		
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);	
		
		logger.strongStep("Open the event and add a comment to it");
		log.info("INFO: Opening the event and adding a comment to it");
		ui.clickLinkWait(calUI.getEventSelector(Event1));		
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		logger.strongStep("Add the mention for the owner of the community");
		log.info("INFO: Input @mention for the member of the community");
		ui.fluentWaitElementVisibleOnce(BaseUIConstants.CKEditor_iFrame);
		ui.typeNativeInCkEditor("@" + sDisplayName1, "0");
		calUI.mention_addMember(sDisplayName1);
		
		logger.strongStep("Save the changes and verify the mention link appears for the owner in the Comments area of the event");
		log.info("INFO: Click on the Save button and verify the mention link appears for owner in the Comments area of the event");
		calUI.clickLinkWait(CalendarUI.AddCommentSaveButton);
		Assert.assertTrue(driver.isElementPresent(calUI.getMentionPersonLink(sDisplayName1)), "ERROR: mention people link doesn't show for" + sDisplayName1);
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if a display name can be used to enter a mention in a comment.
	 *<li><B>Step: </B>Create a community.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Verify: </B>The mention typeahead appears.
	 *<li><B>Step: </B>Add a mention in the comments text area along with some text.
	 *<li><B>Verify: </B>The mention text appears correctly in the editor.
	 *<li><B>Step: </B>Save the changes. 
	 *<li><B>Verify: </B>The mention and the text should appear correctly in the Comments area of the event.
	 *</ul>
	 *
	 */
	@Test(groups = {"regression"})
	public void Mention_Basic_DisplayName() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		String sDisplayName = testUser.getDisplayName();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Normal Event " + dateTime).build();
		
		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() + " widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);
	
		//Choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);

		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));	
		calUI.fluentWaitElementVisible(CalendarUI.AddAComment);
		driver.getSingleElement(CalendarUI.AddAComment).click();
		
		logger.strongStep("Add the mention by only using the first two letters of the name");
		log.info("INFO: Input @mention by only using the first two letters of the name");
		ui.fluentWaitElementVisibleOnce(BaseUIConstants.CKEditor_iFrame);
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay("@"+ sDisplayName.substring(0, 2) );
		
		logger.strongStep("Verify that the mention typeahead appears");
		log.info("INFO: Verify that the mention typeahead appears");
		Assert.assertTrue(driver.isElementPresent(calUI.mentionTypeahead()), "ERROR: mention typahead doesn't show");
		
		logger.strongStep("Add the remaining characters from the name to the mention");
		log.info("INFO: Append the remaining characters from the name to the mention");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).typeWithDelay(sDisplayName.substring(2));
		calUI.mention_addMember(sDisplayName);
		
		logger.strongStep("Verify that the mention text appears correctly in the editor");
		log.info("INFO: Verify that the mention text appears correctly in the editor");
		Assert.assertEquals(getCKEditorContents(), "@" + sDisplayName, "ERROR: mention text is not correct.");
		
		logger.strongStep("Add some text to the comments text area, save the changes");
		log.info("INFO: Input some text in the comments text area, save the changes");
		driver.getSingleElement(BaseUIConstants.CKEditor_iFrame).type("test");
		ui.getFirstVisibleElement(CalendarUI.SaveButton).click();
		ui.fluentWaitPresent(CalendarUI.CommentContent);
		
		logger.strongStep("Verify the mention and the text appear correctly in the Comments area of the event");
		log.info("INFO: Verify the mention and the text appear correctly in the Comments area of the event");
		Assert.assertEquals(driver.getSingleElement(CalendarUI.CommentContent).getText(), "@" + sDisplayName + "test", "ERROR: mention content is incorrect");
		
		calUI.endTest();
	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Tests to see if an empty comment will throw an error message.
	 *<li><B>Step: </B>Create a community.
	 *<li><B>Step: </B>Add the event widget.
	 *<li><B>Step: </B>Create an event.
	 *<li><B>Step: </B>Add a comment to the event using the Add a comment link.
	 *<li><B>Step: </B>Input nothing in the comments editor.
	 *<li><B>Step: </B>Save the changes.
	 *<li><B>Verify: </B>The empty comment error message should show up.
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void EmptyCommentShowsError() throws Exception{
		
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
		
		String testName = calUI.startTest();		
		String dateTime = Helper.genDateBasedRandVal();
		
		BaseCommunity community = new BaseCommunity.Builder(testName + dateTime)
														.commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
														.access(defaultAccess)
														.description("Test " + testName)
														.build();		
				
		BaseEvent Event1 = new BaseEvent.Builder("Event " + dateTime).build();

		//create community
		logger.strongStep("Create community using API");
		log.info("INFO: Create community using API");
		Community comAPI = community.createAPI(apiOwner);
		
		//add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		logger.strongStep("Add the events widget");
		log.info("INFO: Adding the " + BaseWidget.EVENTS.getTitle() +
				" widget to community " + community.getName() + " using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);
		
		//GUI
		//Load component and login
		logger.strongStep("Open Communities and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUser);

		//navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(ui);

		//Choose events in left nav menu
		logger.strongStep("Click on the Events link in the navigation menu");
		log.info("INFO: Select Events from the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(calUI);
		
		//Verify the calendar view
		logger.strongStep("Create a new event and add a comment to it");
		log.info("INFO: Creating an event and adding a comment to it");
		calUI.fluentWaitPresent(CalendarUI.CreateEvent);
		calUI.create(Event1);
		calUI.clickLinkWait(calUI.getEventSelector(Event1));
		calUI.clickLinkWait(CalendarUI.AddAComment);
		
		logger.strongStep("Type nothing in the editor, save the changes and verify the empty comment error message is thrown");
		log.info("INFO: Enter nothing in the editor, save the changes and verify the empty comment error message is thrown");
		driver.typeNative("");
		log.info("INFO: Save comment");
		ui.clickLinkWait(CalendarUI.AddCommentSaveButton);
		Assert.assertTrue(driver.isTextPresent(Data.getData().EmtpyCommentErrorMsg), "ERROR:  empty comment error msg doesn't show up");
		
		calUI.endTest();
	}
	
	private String getCKEditorContents() {
		ui.switchToFrame(BaseUIConstants.CKEditor_iFrame, BaseUIConstants.StatusUpdate_Body);
		String actualText = ui.getFirstVisibleElement(BaseUIConstants.StatusUpdate_Body).getText();
		ui.switchToTopFrame();
		return actualText;
	}
	
}
