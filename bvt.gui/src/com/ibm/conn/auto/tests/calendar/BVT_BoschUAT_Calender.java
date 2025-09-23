package com.ibm.conn.auto.tests.calendar;

import java.text.ParseException;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import com.ibm.conn.auto.webui.constants.CommunitiesUIConstants;
import org.openqa.selenium.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.StartPageApi;
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
import com.ibm.conn.auto.webui.CalendarUI;
import com.ibm.conn.auto.webui.CommunitiesUI;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class BVT_BoschUAT_Calender extends SetUpMethods2{
	private static Logger log = LoggerFactory.getLogger(BVT_BoschUAT_Calender.class);
	private TestConfigCustom cfg;
	private HomepageUI ui;
	private CommunitiesUI comUi;
	private CalendarUI calendarUi;
	private User testUserA, testUserB, testUserC;
	private String serverURL;
	private APICommunitiesHandler apiOwner;
	private BaseCommunity.Access defaultAccess;
	String appName = "Events";

	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {

		cfg = TestConfigCustom.getInstance();
		testUserA = cfg.getUserAllocator().getUser(this);
		testUserB = cfg.getUserAllocator().getUser();
		testUserC = cfg.getUserAllocator().getUser();

		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		apiOwner = new APICommunitiesHandler(serverURL, testUserA.getAttribute(cfg.getLoginPreference()), testUserA.getPassword());

	}


	@BeforeMethod(alwaysRun=true)
	public void setUp() {

		//initialize the configuration
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		comUi = CommunitiesUI.getGui(cfg.getProductName(), driver);
		calendarUi = CalendarUI.getGui(cfg.getProductName(), driver);

		defaultAccess = CommunitiesUI.getDefaultAccess(cfg.getProductName());

		ui.addOnLoginScript(ui.getCloseTourScript());
	}

	/**
	 *<ul>
	 *<li><B>Info:</B> Verify inserted Image Preview in Calendar Event</li>
	 *<li><B>Step:</B> Login with testUserA and Add Events app</li> 
	 *<li><B>Step:</B> Navigate to Events page</li>
	 *<li><B>Step:</B> Click on Create Event. Fill title and fill insert link text and value.click Save</li>
	 *<li><B>Step:</B> Click on event created</li>
	 *<li><B>Step:</B> Click on link in Event Description</li>
	 *<li><B>Verify:</B> Verify the image preview displayed</li>
	 *</ul>
	 */
	@Test(groups = {"regression"})
	public void verifyInsertImageURLCalendarEvents() throws ParseException {

		DefectLogger logger=dlog.get(Thread.currentThread().getId());

		String urlInputValue = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQw1L39-k24Hyzoi0Sy7Bgg8auce5a8udLnZlFh-7ogvGq1pCB9";
		String urlTextValue = "eventUrl";

		String testName = ui.startTest();

		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
				.tags(Data.getData().commonTag + Helper.genDateBasedRand())
				.access(defaultAccess)
				.commHandle(Data.getData().commonHandle + Helper.genDateBasedRandVal()).access(Access.PUBLIC)
				.build();

		BaseEvent event = new BaseEvent.Builder(testName + "event" +  Helper.genDateBasedRand())  
				.description(Data.getData().commonDescription)
				.build();

		logger.strongStep("Create Community using API");
		log.info("INFO: Create Community using API");
		Community comAPI = community.createAPI(apiOwner);

		logger.strongStep("INFO: Get the UUID of community");
		log.info("INFO: Get the UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);

		log.info("Load Communities and Log In as " + testUserA.getDisplayName());
		logger.strongStep("Load Communities and Log In as " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);

		logger.strongStep("Update Landing Page of Community as Overview, if default is Highlights");
		Boolean flag = comUi.isHighlightDefaultCommunityLandingPage();

		if (flag) {
			apiOwner.editStartPage(comAPI, StartPageApi.OVERVIEW);
		}

		log.info("INFO: Navigate to the Community using UUID");
		logger.strongStep("Naviagate to the Community");
		community.navViaUUID(comUi);
		
		log.info("INFO: Add " + appName + " app to community");
		logger.strongStep("Add " + appName + " app to community");
		comUi.addWidget(BaseWidget.EVENTS);

		log.info("INFO: Navigate to Events Page");
		logger.strongStep("INFO: Navigate to Events Page");
		Community_LeftNav_Menu.EVENTS.select(ui);
		
		log.info("INFO: Click on Create an Event");
		logger.strongStep("INFO: Click on Create an Event");
		calendarUi.clickLinkWait(CalendarUI.CreateEvent);

		log.info("INFO: Adding Title of Event");
		logger.strongStep("INFO: Adding Title of Event");
		driver.getSingleElement(CalendarUI.EventTitle).clear();
		driver.getSingleElement(CalendarUI.EventTitle).type(event.getName());

		logger.strongStep("INFO: Click on  Inserted Link");
		log.info("INFO: Click on Inserted Link");
		calendarUi.clickLinkWait(BaseUIConstants.insertLink);

		log.info("INFO: Click on URL Link");
		logger.strongStep("INFO: Click on  URL Link");
		calendarUi.switchToFrameBySelector(BaseUIConstants.ckePanelFrame);
		calendarUi.clickLinkWait(BaseUIConstants.urlLink);
		calendarUi.switchToTopFrame();

		log.info("INFO: Type Url text and Value and Click OK and Save");
		logger.strongStep("INFO: Type Url text and Value and Click OK and Save");
		calendarUi.typeText(BaseUIConstants.urlInputField, urlInputValue);
		calendarUi.typeText(BaseUIConstants.linkTextInputField,urlTextValue );
		calendarUi.clickLinkWait(CalendarUI.OKBtn);
		calendarUi.clickLinkWait(CalendarUI.EventSave);

		log.info("INFO: Click on Created Event");
		logger.strongStep("INFO: Click on Created Event");
		driver.getFirstElement(CalendarUI.EventNameList.replace("PLACEHOLDER", event.getName())).click();		
		calendarUi.waitForPageLoaded(driver);

		log.info("INFO: Click on Link in Event Description");
		logger.strongStep("INFO:Click on Link in Event Descriptiont");
		driver.getFirstElement(CalendarUI.LinkInEventDescription).click();

		log.info("Verify Image Preview is is displayed");
		logger.strongStep("Verify Image Preview is is displayed");
		Assert.assertTrue(calendarUi.fluentWaitElementVisible(BaseUIConstants.imagePreview));

		ui.endTest();
	}
	
	/**
	*<ul>
	*<li><B>Info: </B>Test the usage of mentions in events.
	*<li><B>Step: </B>Create a community using API.
	*<li><B>Step: </B>Add the Events widget to the community using API.
	*<li><B>Step: </B>Open the communities component and login as UserA.
	*<li><B>Step: </B>Navigate to the Community using UUID.
	*<li><B>Step: </B>Select 'MEMBERS' tab from the navigation menu.
	*<li><B>Step: </B>Click on the 'Add Members' button, 
	*<li><B>Step: </B>Type UserB's name in the 'Type to find person' text box and click on the name in the typeahead.
	*<li><B>Step: </B>Click on the Save button to save the changes.
	*<li><B>Step: </B>Type UserC's name in the 'Type to find person' text box and click on the name in the typeahead.
	*<li><B>Step: </B>Click on the Save button to save the changes.
	*<li><B>Step: </B>Go to the Overview page and add the Events (Calendar) widget to the community.
	*<li><B>Step: </B>Click on the 'Create an Event' button to start creating an event.
	*<li><B>Step: </B>Enter the title and tags for the event.
	*<li><B>Step: </B>Enter some text followed by a mention for UserB in the Description text area.
	*<li><B>Verify: </B>The mention typeahead appears.
	*<li><B>Step: </B>Select UserB's name from the mention typeahead and save the changes.
	*<li><B>Step: </B>Click on the event's link to open it.
	*<li><B>Verify: </B>The mention for UserB appears as a link on the event's page.
	*<li><B>Step: </B>Click on the Edit button to edit the event.
	*<li><B>Step: </B>Append the mention for UserC to the text in the Description text area.
	*<li><B>Verify: </B>The mention typeahead appears.
	*<li><B>Step: </B>Select UserC's name from the mention typeahead and save the changes.
	*<li><B>Verify: </B>The mention for UserC appears as a link on the event's page.
	*@throws Exception
	*</ul>
	*/
	@Test(groups = {"regression"})
	public void eventMentions() throws Exception{
			
		DefectLogger logger=dlog.get(Thread.currentThread().getId());
			
		String testName = ui.startTest();
			
		//Create a community base state object
		BaseCommunity community = new BaseCommunity.Builder(testName + Helper.genDateBasedRand())
		   										   .access(defaultAccess)
									 			   .commHandle(Data.getData().commonHandle + Helper.genDateBasedRand())
									 			   .description("Test Calendar for " + testName)
									 			   .build();
			
		BaseEvent event = new BaseEvent.Builder(testName + "event" +  Helper.genDateBasedRand())
												   .tags(Data.getData().commonTag)
												   .description(Data.getData().commonDescription + " @" + testUserB.getDisplayName())
												   .build();
		
		//Create community
		logger.strongStep("Create a community using API");
		log.info("INFO: Create a community using API");
		Community comAPI = community.createAPI(apiOwner);

		//Add the events widget
		logger.strongStep("Add the Events widget to the community using API");
		log.info("INFO: Add the Events widget to the community using API");
		community.addWidgetAPI(comAPI, apiOwner, BaseWidget.EVENTS);

		//Add the UUID to community
		log.info("INFO: Get UUID of community");
		community.getCommunityUUID_API(apiOwner, comAPI);
		
		//GUI
		//Load component and login
		logger.strongStep("Load the Communities component and login as: " + testUserA.getDisplayName());
		log.info("INFO: Load the Communities component and login as: " + testUserA.getDisplayName());
		ui.loadComponent(Data.getData().ComponentCommunities);
		ui.login(testUserA);
		
		// navigate to the API community
		logger.strongStep("Navigate to the community using UUID");
		log.info("INFO: Navigate to the community using UUID");
		community.navViaUUID(comUi);
		
		logger.strongStep("Select 'MEMBERS' tab from the navigation menu");
		log.info("INFO: Click on 'MEMBERS' tab in the navigation menu");
		Community_LeftNav_Menu.MEMBERS.select(ui);
			
		logger.strongStep("Click on the 'Add Members' button, type " + testUserB.getDisplayName() + "'s name in the 'Type to find person' text box and click on the name in the typeahead");
		log.info("INFO: Click on the 'Add Members' button, type " + testUserB.getDisplayName() + "'s name in the 'Type to find person' text box and click on the name in the typeahead");
		comUi.addMemberCommunity(new Member(CommunityRole.MEMBERS, testUserB));
			
		logger.strongStep("Click on the Save button to save the changes");
		log.info("INFO: Save the changes by clicking on the Save button");
		ui.clickLinkWait(CommunitiesUIConstants.CommunityMemebersPageNewMembersSaveButton);
			
		logger.strongStep("Click on the 'Add Members' button, type " + testUserC.getDisplayName() + "'s name in the 'Type to find person' text box and click on the name in the typeahead");
		log.info("INFO: Click on the 'Add Members' button, type " + testUserC.getDisplayName() + "'s name in the 'Type to find person' text box and click on the name in the typeahead");
		comUi.addMemberCommunity(new Member(CommunityRole.MEMBERS, testUserC));
			
		logger.strongStep("Click on the Save button to save the changes");
		log.info("INFO: Save the changes by clicking on the Save button");
		ui.clickLinkWait(CommunitiesUIConstants.CommunityMemebersPageNewMembersSaveButton);

		logger.strongStep("Select 'EVENTS' tab from the navigation menu");
		log.info("INFO: Click on 'EVENTS' tab in the navigation menu");
		Community_LeftNav_Menu.EVENTS.select(ui);
			
		logger.strongStep("Start creating an event by clicking on the 'Create an Event' button");
		log.info("INFO: Press the 'Create an Event' button to start creating an event");
		ui.clickLinkWait(CalendarUI.CreateEvent);
			
		logger.strongStep("Type '" + event.getName() + "' in the 'Event Title' text box");
		log.info("INFO: Enter '" + event.getName() + "' in the 'Event Title' text box");
		driver.getSingleElement(CalendarUI.EventTitle).type(event.getName());
			
		logger.strongStep("Type '" + Data.getData().commonTag + "' in the 'Tags' text box");
		log.info("INFO: Enter '" + Data.getData().commonTag + "' in the 'Tags' text box");
		driver.getSingleElement(CalendarUI.EventTag).type(Data.getData().commonTag);
			
		logger.strongStep("Type '" + event.getDescription() + "' in the 'Description' text box");
		log.info("INFO: Enter '" + event.getDescription() + "' in the 'Description' text box");
		typeInDescriptionTextArea(event.getDescription());
			
		logger.strongStep("Verify that the mention typeahead appears");
		log.info("INFO: Validate that the mention typeahead appears");
		Assert.assertTrue(driver.isElementPresent(calendarUi.mentionTypeahead()), "ERROR: mention typahead doesn't show");
			
		logger.strongStep("Select " + testUserB.getDisplayName() + "'s name from the mention typeahead");
		log.info("INFO: Select " + testUserB.getDisplayName() + "'s name from the mention typeahead");
		calendarUi.mention_addMember(testUserB.getDisplayName());
			
		logger.strongStep("Click on the Save button to save the changes");
		log.info("INFO: Save the changes by clicking on the Save button");
		ui.clickLinkWait(calendarUi.EventSave);
			
		logger.strongStep("Open the event just created by clicking on its link");
		log.info("INFO: Click on the event's link to open it");
		ui.clickLinkWait("link=" + event.getName());
			
		logger.strongStep("Verify that the mention for " + testUserB.getDisplayName() + " appears as a link");
		log.info("INFO: Validate that the mention for " + testUserB.getDisplayName() + " appears as a link");
		Assert.assertTrue(driver.isElementPresent(calendarUi.getMentionPersonLink(testUserB.getDisplayName())), "ERROR: The link for " + testUserB.getDisplayName() + "'s mention is not visible");
			
		logger.strongStep("Click on the Edit button to edit the event");
		log.info("INFO: Edit the event just created by clicking on the Edit button");
		ui.clickLinkWait(calendarUi.EditLink);
			
		logger.strongStep("Append the mention for " + testUserC.getDisplayName() + " to the text in the Description text area");
		log.info("INFO: Append the mention for " + testUserC.getDisplayName() + " to the text in the Description text area");
		typeInDescriptionTextArea(Keys.END + " @" + testUserC.getDisplayName());
			
		logger.strongStep("Verify that the mention typeahead appears");
		log.info("INFO: Validate that the mention typeahead appears");
		Assert.assertTrue(driver.isElementPresent(calendarUi.mentionTypeahead()), "ERROR: mention typahead doesn't show");
			
		logger.strongStep("Select " + testUserC.getDisplayName() + "'s name from the mention typeahead");
		log.info("INFO: Select " + testUserC.getDisplayName() + "'s name from the mention typeahead");
		calendarUi.mention_addMember(testUserC.getDisplayName());
			
		logger.strongStep("Click on the Save button to save the changes");
		log.info("INFO: Save the changes by clicking on the Save button");
		ui.clickLinkWait(calendarUi.EventSave);
			
		logger.strongStep("Verify that the mention for " + testUserC.getDisplayName() + " appears as a link");
		log.info("INFO: Validate that the mention for " + testUserC.getDisplayName() + " appears as a link");
		Assert.assertTrue(driver.isElementPresent(calendarUi.getMentionPersonLink(testUserC.getDisplayName())), "ERROR: The link for " + testUserC.getDisplayName() + "'s mention is not visible");

		ui.endTest();
			
	}

	private void typeInDescriptionTextArea(String text) {
		ui.switchToFrame(BaseUIConstants.CKEditor_iFrame, BaseUIConstants.StatusUpdate_Body);
		driver.getFirstElement(BaseUIConstants.StatusUpdate_Body).type(text);
		ui.switchToTopFrame();
	}

}
