package com.ibm.conn.auto.lcapi.test;

import java.net.URISyntaxException;
import java.text.ParseException;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.lcapi.common.APIUtils;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.communities.nodes.Calendar;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommentToEvent;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

public class APICalendarTest extends SetUpMethods2 {
	
	private static Abdera abdera;
	private static AbderaClient client;
	private static Logger log = LoggerFactory.getLogger(APICalendarTest.class);
	private static ServiceConfig config;

	private APICalendarHandler calendarOwner;
	private APICommunitiesHandler communityOwner;
	private APIProfilesHandler testUser2Profile;
	private String serverURL;
	private TestConfigCustom cfg;	
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		serverURL = APIUtils.formatBrowserURLForAPI(testConfig.getBrowserURL());
		
		testUser1 = cfg.getUserAllocator().getUser();
		testUser2 = cfg.getUserAllocator().getUser();
		
		calendarOwner = new APICalendarHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
		communityOwner = new APICommunitiesHandler(serverURL, testUser1.getAttribute(cfg.getLoginPreference()), testUser1.getPassword());
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
	public void testAPI_AddCalendarEvent() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseCom = buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communityOwner.createCommunity(baseCom);
		
		log.info("INFO: Set UUID of community");
		baseCom.setCommunityUUID(calendarOwner.getCommunityUUID(publicCommunity));
		
		log.info("INFO: Adding the Events widget to the community");
		communityOwner.addWidget(publicCommunity, BaseWidget.EVENTS);
		
		log.info("INFO: Adding a calendar event to the community");
		BaseEvent baseEvent = buildBaseCalendarEvent(testName + Helper.genStrongRand(), false);
		Calendar calendarEvent = calendarOwner.addCalendarEvent(publicCommunity, baseEvent);
		
		assert calendarEvent != null: "ERROR: There was a problem with adding the calendar event to the community using the API";
		assert calendarEvent.getTitle().trim().equals(baseEvent.getName()) == true : "ERROR: The calendar event title was not set correctly using the API";
		assert calendarEvent.getTags().toString().indexOf(baseEvent.getTags()) > -1 : "ERROR: The calendar event tags were not set correctly using the API";
		assert calendarEvent.getContent().trim().equals(baseEvent.getDescription().trim()) == true : "ERROR: The calendar event description was not set correctly using the API";
		assert calendarEvent.getSelfLink() != null : "ERROR: The self link of the calendar event was not set correctly using the API";
		assert calendarEvent.getEditLink() != null : "ERROR: The edit link of the calendar event was not set correctly using the API";
		assert calendarEvent.getLinks().get("follow") != null : "ERROR: The follow link of the calendar event was not set correctly using the API";
		assert calendarEvent.getLinks().get("attend") != null : "ERROR: The attend link of the calendar event was not set correctly using the API";
		assert calendarEvent.getLinks().get("instances") != null : "ERROR: The instances link of the calendar event was not set correctly using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_AddCalendarEventWithMentions() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseCom = buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communityOwner.createCommunity(baseCom);
		
		log.info("INFO: Adding the Events widget to the community");
		communityOwner.addWidget(publicCommunity, BaseWidget.EVENTS);
		
		log.info("INFO: Adding a calendar event with mentions to the community");
		
		// Create the base event
		BaseEvent baseEvent = buildBaseCalendarEvent(testName + Helper.genStrongRand(), false);
		
		// Create the Mentions instance to be used to mention User 2 in the calendar event description
		String beforeMentionsText = Data.getData().buttonOK + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonSend + Helper.genStrongRand();
		
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, testUser2Profile, serverURL, beforeMentionsText, afterMentionsText);
		Calendar calendarEvent = calendarOwner.addCalendarEventWithMentions(publicCommunity, baseEvent, mentions);
		
		assert calendarEvent != null: "ERROR: The calendar event was not added to the community";
		assert calendarEvent.getTitle().trim().equals(baseEvent.getName()) == true : "ERROR: The calendar event title was not set correctly using the API";
		
		String content = calendarEvent.getContent().trim();
		
		assert content.indexOf(baseEvent.getDescription().trim()) > -1 : "ERROR: The calendar event description did not contain the base event description as expected using the API";
		assert content.indexOf(mentions.getBeforeMentionText()) > -1 : "ERROR: The calendar event description did not contain the before mentions text as expected using the API";
		assert content.indexOf(mentions.getAfterMentionText()) > -1 : "ERROR: The calendar event description did not contain the after mentions text as expected using the API";
		assert content.indexOf("@" + testUser2.getDisplayName()) > -1 : "ERROR: The calendar event description did not contain the username for User 2 as expected using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_EditCalendarEventDescription_EntireSeries() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseCom = buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communityOwner.createCommunity(baseCom);
		
		log.info("INFO: Adding the Events widget to the community");
		communityOwner.addWidget(publicCommunity, BaseWidget.EVENTS);
		
		log.info("INFO: Adding a calendar event to the community");
		BaseEvent baseEvent = buildBaseCalendarEvent(testName + Helper.genStrongRand(), true);
		Calendar calendarEvent = calendarOwner.addCalendarEvent(publicCommunity, baseEvent);
		
		log.info("INFO: Now updating the calendar event to include the edited description");
		String editedDescription = testName + Helper.genStrongRand();
		Calendar editedCalendarEvent = calendarOwner.editCalendarEventDescription(calendarEvent, editedDescription);
		
		assert editedCalendarEvent != null : "ERROR: The calendar event was not updated successfully";
		assert editedCalendarEvent.getTitle().equals(calendarEvent.getTitle()) == true : "ERROR: The title of the calendar event changed with the description update";
		assert editedCalendarEvent.getContent().trim().equals(editedDescription) == true : "ERROR: The content of the calendar event did not update as expected";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_CommentOnCalendarEvent() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseCom = buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communityOwner.createCommunity(baseCom);
		
		log.info("INFO: Adding the Events widget to the community");
		communityOwner.addWidget(publicCommunity, BaseWidget.EVENTS);
		
		log.info("INFO: Adding a calendar event to the community");
		BaseEvent baseEvent = buildBaseCalendarEvent(testName + Helper.genStrongRand(), false);
		Calendar calendarEntry = calendarOwner.addCalendarEvent(publicCommunity, baseEvent);
		
		log.info("INFO: Now commenting on the calendar event");
		CommentToEvent commented = calendarOwner.commentOnCalendarEvent(calendarEntry, "Test Comment");
		
		assert commented != null: "ERROR: The comment was not added to the calendar event in the community";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_CommentWithMentionsOnCalendarEvent() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseCom = buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communityOwner.createCommunity(baseCom);
		
		log.info("INFO: Adding the Events widget to the community");
		communityOwner.addWidget(publicCommunity, BaseWidget.EVENTS);
		
		log.info("INFO: Adding a calendar event to the community");
		BaseEvent baseEvent = buildBaseCalendarEvent(testName + Helper.genStrongRand(), false);
		Calendar calendarEvent = calendarOwner.addCalendarEvent(publicCommunity, baseEvent);
		
		log.info("INFO: " + testUser1.getDisplayName() + " comment on the event mentioning " + testUser2.getDisplayName());
		String beforeMentionsText = Data.getData().buttonOK + Helper.genStrongRand();
		String afterMentionsText = Data.getData().buttonSend + Helper.genStrongRand();
		
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, testUser2Profile, serverURL, beforeMentionsText, afterMentionsText);
		CommentToEvent commentWithMentions = calendarOwner.commentWithMentionsOnCalendarEvent(calendarEvent, mentions);
		
		assert commentWithMentions != null : "ERROR: There was a problem with posting the comment with mentions to the calendar event using the API";
		assert commentWithMentions.getSelfLink() != null : "ERROR: There was a problem with setting the SELF link of the comment using the API";
		assert commentWithMentions.getEditLink() != null : "ERROR: There was a problem with setting the EDIT link of the comment using the API";
		
		String commentContent = commentWithMentions.getContent().trim();
		
		assert commentContent.indexOf(mentions.getBeforeMentionText().trim()) > -1 : "ERROR: The text before the mention was not set as expected in the content of the comment using the API";
		assert commentContent.indexOf(mentions.getAfterMentionText().trim()) > -1 : "ERROR: The text after the mention was not set as expected in the content of the comment using the API";
		assert commentContent.indexOf("@" + testUser2.getDisplayName()) > -1 : "ERROR: The mentioned user was not set as expected in the content of the comment using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_EditCalendarEventDescription_FirstInstance() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseCom = buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communityOwner.createCommunity(baseCom);
		
		log.info("INFO: Adding the Events widget to the community");
		communityOwner.addWidget(publicCommunity, BaseWidget.EVENTS);
		
		log.info("INFO: Adding a calendar event to the community");
		BaseEvent baseEvent = buildBaseCalendarEvent(testName + Helper.genStrongRand(), true);
		Calendar calendarEvent = calendarOwner.addCalendarEvent(publicCommunity, baseEvent);
		
		log.info("INFO: Now updating the first instance of the calendar event to include the edited description");
		String editedDescription = testName + Helper.genStrongRand();
		boolean updated = calendarOwner.editCalendarEventDescriptionFirstSingleInstance(calendarEvent, editedDescription);
		
		assert updated == true : "ERROR: There was a problem with updating the description for the first instance event in the calendar event using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_DeleteCommentOnCalendarEvent() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseCom = buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communityOwner.createCommunity(baseCom);
		
		log.info("INFO: Adding the Events widget to the community");
		communityOwner.addWidget(publicCommunity, BaseWidget.EVENTS);
		
		log.info("INFO: Adding a calendar event to the community");
		BaseEvent baseEvent = buildBaseCalendarEvent(testName + Helper.genStrongRand(), false);
		Calendar calendarEntry = calendarOwner.addCalendarEvent(publicCommunity, baseEvent);
		
		log.info("INFO: Now commenting on the calendar event");
		CommentToEvent commented = calendarOwner.commentOnCalendarEvent(calendarEntry, "Test Comment");
		
		assert commented != null: "ERROR: The comment was not added to the calendar event in the community";
		
		log.info("INFO: Now deleting the comment posted to the calendar event");
		boolean deleted = calendarOwner.deleteCommentOnCalendarEvent(commented);
		
		assert deleted == true : "ERROR: There was a problem with deleting the comment posted to a calendar event using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_DeleteCalendarEvent_FirstInstance() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseCom = buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communityOwner.createCommunity(baseCom);
		
		log.info("INFO: Adding the Events widget to the community");
		communityOwner.addWidget(publicCommunity, BaseWidget.EVENTS);
		
		log.info("INFO: Adding a calendar event to the community");
		BaseEvent baseEvent = buildBaseCalendarEvent(testName + Helper.genStrongRand(), true);
		Calendar calendarEvent = calendarOwner.addCalendarEvent(publicCommunity, baseEvent);
		
		log.info("INFO: Now deleting the first instance event of the calendar event");
		boolean deleted = calendarOwner.deleteCalendarEventFirstSingleInstance(calendarEvent);
		
		assert deleted == true : "ERROR: There was a problem with deleting the first instance event in the calendar event using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	@Test(groups = {"apitest"})
	public void api_DeleteCalendarEvent() {
		
		String testName = Thread.currentThread().getStackTrace()[2].getMethodName();
		
		log.info("INFO: Now creating a new public community");
		BaseCommunity baseCom = buildBaseCommunity(testName + Helper.genStrongRand(), Access.PUBLIC);
		Community publicCommunity = communityOwner.createCommunity(baseCom);
		
		log.info("INFO: Adding the Events widget to the community");
		communityOwner.addWidget(publicCommunity, BaseWidget.EVENTS);
		
		log.info("INFO: Adding a calendar event to the community");
		BaseEvent baseEvent = buildBaseCalendarEvent(testName + Helper.genStrongRand(), false);
		Calendar calendarEvent = calendarOwner.addCalendarEvent(publicCommunity, baseEvent);
		
		log.info("INFO: Now deleting the calendar event from the community");
		boolean deleted = calendarOwner.deleteCalendarEvent(calendarEvent);
		
		assert deleted == true : "ERROR: There was a problem with deleting the calendar event using the API";
		
		log.info("INFO: API test completed - cleaning up");
		communityOwner.deleteCommunity(publicCommunity);
	}
	
	/**
	 * Creates a BaseCommunity instance from which Community instances can be created
	 * 
	 * @param communityName - The String name of the community
	 * @param access - The Access instance of the access rights to the community (ie. PUBLIC, MODERATED, RESTRICTED)
	 * @return - The BaseCommunity instance
	 */
	private BaseCommunity buildBaseCommunity(String communityName, BaseCommunity.Access access){
			
		BaseCommunity baseCommunity;
		if (access.equals(Access.PUBLIC) || access.equals(Access.MODERATED)){
			baseCommunity = new BaseCommunity.Builder(communityName)
											.access(access)
											.tags(Data.getData().commonTag + Helper.genStrongRand())
											.description(Data.getData().commonDescription + Helper.genStrongRand())
											.build();
		}
		else {
			baseCommunity = new BaseCommunity.Builder(communityName)
											.access(access)
											.shareOutside(false)
											.tags(Data.getData().commonTag + Helper.genStrongRand())
											.description(Data.getData().commonDescription + Helper.genStrongRand())
											.build();

		}
		return baseCommunity;
	}
	
	/**
	 * Creates a BaseEvent instance of a calendar event
	 * 
	 * @param calendarEventName - The name to be assigned to the calendar event (ie. testName)
	 * @param repeatingEvent - True if the calendar event is to be a repeating eventt, false if the calendar event is not a repeating event
	 * @return - The BaseEvent instance of the calendar event
	 */
	private BaseEvent buildBaseCalendarEvent(String calendarEventName, boolean repeatingEvent) {
		
		BaseEvent baseEvent;
		try {
			baseEvent = new BaseEvent.Builder(calendarEventName + Helper.genStrongRand())
									 .tags(Data.getData().commonTag + Helper.genStrongRand())
									 .description(Data.getData().commonDescription + Helper.genStrongRand())
									 .repeat(repeatingEvent)
									 .build();
		} catch(ParseException pe) {
			pe.printStackTrace();
			baseEvent = null;
		}
		return baseEvent;
	}
}