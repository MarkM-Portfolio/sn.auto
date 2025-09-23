package com.ibm.lconn.automation.framework.services.communities.test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.Date;
import java.util.List;

import org.apache.abdera.Abdera;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Category;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.ExtensibleElement;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortBy;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortField;
import com.ibm.lconn.automation.framework.services.common.StringConstants.SortOrder;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.communities.nodes.CommentToEvent;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;
import com.ibm.lconn.automation.framework.services.communities.nodes.Event;
import com.ibm.lconn.automation.framework.services.communities.nodes.Widget;

public class CommunitiesCalendarOrgAdminAPITest {
	protected static Abdera abdera = new Abdera();
	// Users index in i1 ProfileData_apps.collabservintegration.properties
	final static int ORGADMIN = 0; // OrgA-admin
	final static int USER2 = 2; // OrgA user2
	final static int ORGBUSER = 15; // OrgB user
	final static int ORGCUSER = 17;
	final static int ORGCADMIN = 16;

	// index 0 is orgA org admin, index 15 is orgB org-admin, index 16 is orgC org-admin, 
	// index 2 is orgA normal user, index 17 is orgC normal user. 
	private UserPerspective admin, user2, user15, user17, user16;
	private CommunitiesService commAdminService, comm2Service, commOrgbService, commOrgcService, commOrgcAdminService;

	protected final static Logger LOGGER = LoggerFactory.getLogger(CommunitiesOrgAdminAPITest.class.getName());


	@BeforeClass
	public void setUp() throws Exception {

		// set up multiple users testing environment
		LOGGER.debug("Start Initializing Test");

		UsersEnvironment userEnv = new UsersEnvironment();
		admin = userEnv.getLoginUserEnvironment(ORGADMIN, Component.COMMUNITIES.toString());
		commAdminService = admin.getCommunitiesService();

		user2 = userEnv.getLoginUserEnvironment(USER2, Component.COMMUNITIES.toString());
		comm2Service = user2.getCommunitiesService();

		user17 = userEnv.getLoginUserEnvironment(ORGCUSER, Component.COMMUNITIES.toString());
		commOrgcService = user17.getCommunitiesService();
		
		user16 = userEnv.getLoginUserEnvironment(ORGCADMIN, Component.COMMUNITIES.toString());
		commOrgcAdminService = user16.getCommunitiesService();

		user15 = userEnv.getLoginUserEnvironment(ORGBUSER, Component.COMMUNITIES.toString());
		commOrgbService = user15.getCommunitiesService();

		LOGGER.debug("Finished Initializing Test");
	}
	
	public Community getCreatedPrivateCommunity(String CommunityTitle, String content, CommunitiesService service, CommunitiesService assignedService) {
		LOGGER.debug("Step 1: Create a community");
		Community testCommunity = new Community(CommunityTitle,
				"A community for testing", Permissions.PRIVATE, null);
		Entry communityResult = (Entry) service.createCommunity(testCommunity);

		LOGGER.debug("Step 2: Retrieve that community");
		Community communityRetrieved = new Community(
				(Entry) assignedService.getCommunity(communityResult
						.getEditLinkResolvedHref().toString()));
		assertEquals(200, service.getRespStatus());
		
		return communityRetrieved;
	}
	
	public Feed getRemoteAppsFeed(Community community, CommunitiesService service){

		Feed remoteAppsFeed = null;
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {

			LOGGER.debug("Step 3: Add widget into community ");
			Widget widget = new Widget(StringConstants.WidgetID.Calendar.toString());
			service.postWidget(community, widget.toEntry());
			assertEquals(201, service.getRespStatus());
			
			LOGGER.debug("Step 4: community owner can Retrieve Calendar ");
			remoteAppsFeed = (Feed) service.getCommunityRemoteAPPs(
					community.getRemoteAppsListHref(), true, null, 0, 50, null,
					null, SortBy.NAME, SortOrder.ASC, SortField.NAME, null);
			assertTrue(remoteAppsFeed != null);		
			
		}
		return remoteAppsFeed;
	}
	
	public String getCalendarFeedLink(Feed remoteAppsFeed, String linkRels){
		
		String entryLink = null;
		for (Entry entry : remoteAppsFeed.getEntries()) {
			for (Category category : entry.getCategories()) {
				if (category.getTerm().equalsIgnoreCase("calendar")) {
					for (Link link: entry.getLinks()) {
						if (link.getRel().contains(linkRels)) {
							entryLink = link.getHref().toString();
						}
					}
				}
			}
		}
		return entryLink;
	}
	
	@Test
	public void testCommunityOrgAdminDisabledCalendarEvent(){
		/*
		 * RTC 195200 As an org admin, I can use calendar existing APIs to do CRUD operations
		 * When Org-Admin GK disabled, orgAdmin has not access to do CRUD operations for his own org-content
		 * 
		 * Step 0: User prepare community data - commOrgc
		 * Step 1: create a community commOrgc
		 * Step 2: Retrieve that community
		 * Step 3: add widget into community
		 * Step 4: user Retrieve Calendar
		 * Step 5: org-admin cannot retrieve Calendar
		 * Step 6: user post event
		 * Step 7: org-admin has no access to post event 
		 * Step 8: org-admin has no access to retrieve the event 
		 * Step 9: org-admin has no access to update the event 
		 * Step 10: org-admin has no access to post comment 
		 * Step 11: user post comment 
		 * Step 12: org-admin has no access to retieve comment 
		 * Step 13: org-admin has no access to delete event 
		 * Step 14: user delete event 
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {

			LOGGER.debug("Step 0: user2 prepare community data - commOrgc ");
			String timeStamp = Utils.logDateFormatter.format(new Date());
			String comName = "Calendar_user20_" + timeStamp;
			Community community = getCreatedPrivateCommunity(comName, "Test Org-admin disabled calendar API.", commOrgcService, commOrgcService);			

			Feed remoteAppsFeed = null;
			String service_url = null;
		    String event_url=null;

		    remoteAppsFeed = getRemoteAppsFeed(community, commOrgcService);
		    service_url = getCalendarFeedLink(remoteAppsFeed, StringConstants.REL_REMOTEAPPLICATION_PUBLISH);
			LOGGER.debug("Service URL : " + service_url);

			// get calendar service doc
			ExtensibleElement serviceFeed = commOrgcService
					.getCommunityCalendarService(service_url);
			assertTrue(serviceFeed != null);
			
			// user can retrieve calendar
			LOGGER.debug("user20 Getting Community Calendar:");
			String calendar_url = service_url.replace(
					"calendar/service?", "calendar?");
			Entry calendar = (Entry) commOrgcService
					.getCommunityCalendarService(calendar_url);
			assertTrue(calendar != null);
			
			LOGGER.debug("Step 5: org-admin cannot retrieve Calendar");
			LOGGER.debug("org-admin Getting Community Calendar:");
			Entry calendar_orgAdmin = (Entry) commOrgcAdminService
					.getCommunityCalendarService(calendar_url);
			assertEquals("org-admin retrieve Calendar", 403, commOrgcAdminService.getRespStatus());
			
			LOGGER.debug("Step 6: user post event ");
			// post calendar event
			Event event = new Event("My event",
					StringConstants.STRING_YES_LOWERCASE, null,
					null, null, "2022-08-23T02:00:00.000Z",
					"2022-08-23T03:00:00.000Z", 0);
			event_url = getCalendarFeedLink(remoteAppsFeed, StringConstants.REL_REMOTEAPPLICATION_FEED);
			String eventUrl = event_url.replace(
					"type=event&calendarUuid", "calendarUuid");
			ExtensibleElement eventFeed = commOrgcService
					.postCalendarEvent(eventUrl, event);
			assertTrue(eventFeed != null);
								
			LOGGER.debug("Step 7: org-admin has no access to post event ");
			Event event_org = new Event("Org-Admin events",
					StringConstants.STRING_NO_LOWERCASE,
					StringConstants.STRING_WEEKLY, "1",
					"2022-10-24T02:00:00.000Z",
					"2022-08-24T02:00:00.000Z",
					"2022-08-24T03:00:00.000Z", 0);
			ExtensibleElement eventFeed_org = commOrgcAdminService
					.postCalendarEvent(eventUrl, event_org);
			assertEquals("org-admin post event", 403, commOrgcAdminService.getRespStatus());
			
			// retrieve calendar events
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				eventFeed = (Feed) commOrgcService
						.getCalendarFeedWithRedirect(eventUrl
								+ "&startDate=2022-08-23T00:00:00+08:00&endDate=2022-12-06T00:00:00+08:00");
			} else {
				eventFeed = (Feed) commOrgcService
						.getCalendarFeed(eventUrl
								+ "&startDate=2022-08-23T00:00:00+08:00&endDate=2022-12-06T00:00:00+08:00");
			}
			assertTrue(eventFeed != null);
			assertEquals(200, commOrgcService.getRespStatus());
				
			for (Entry eEntry : ((Feed) eventFeed).getEntries()) {
				String selfUrl = eEntry.getSelfLinkResolvedHref()
						.toString();
				
				LOGGER.debug("Step 8: org-admin has no access to retrieve the event ");
				commOrgcAdminService.getCalendarFeed(selfUrl);
				assertEquals("Org-admin retrieve event", 403, commOrgcAdminService.getRespStatus());
				
				LOGGER.debug("Step 9: org-admin has no access to update the event ");
				commOrgcAdminService.putCalendarEvent(selfUrl, event);
				assertEquals("Org-admin update event", 403, commOrgcAdminService.getRespStatus());
				
				// create comment on event
				LOGGER.debug("Step 10: org-admin has no access to post comment ");	
				eventFeed = commOrgcService.getCalendarFeed(selfUrl);
				String commentUrl = eEntry.getLinkResolvedHref(
						StringConstants.REL_EDIT).toString();
				
				commentUrl = commentUrl.replace("/event?",
						"/event/comment?");
				CommentToEvent comment = new CommentToEvent(
						"My comment");
				eventFeed = commOrgcAdminService.postEventComment(commentUrl,
						comment);
				assertEquals("org-admin post comment", 403, commOrgcAdminService.getRespStatus());
				
				LOGGER.debug("Step 11: user post comment ");
				eventFeed = commOrgcService.postEventComment(commentUrl,
						comment);
				assertEquals("user post comment", 201, commOrgcService.getRespStatus());
				
				LOGGER.debug("Retrieve the comment ");
				//retrieve the comment
				String mycommentUrl = ((Entry) eventFeed)
						.getSelfLinkResolvedHref().toString();
				ExtensibleElement commentFeed = commOrgcService.getCalendarFeed(mycommentUrl);
				assertTrue(commentFeed != null);
				
				LOGGER.debug("Step 12: org-admin has no access to retieve comment ");
				commOrgcAdminService.getCalendarFeed(mycommentUrl);
				assertEquals("org-admin retrieve comment", 403, commOrgcAdminService.getRespStatus());
				// Found
				LOGGER.debug("user can still find the comment");
				assertTrue(commentFeed != null);
				assertTrue(((Entry) commentFeed).getContent()
						.equalsIgnoreCase("My comment"));
				
				LOGGER.debug("Step 13: org-admin has no access to delete event ");
				commOrgcAdminService.deleteFeedLink(selfUrl);
				assertEquals("org-admin delete comment", 403, commOrgcAdminService.getRespStatus());
				
				LOGGER.debug("Step 14: user delete event ");
				assertEquals(true, commOrgcService.deleteFeedLink(selfUrl));
				assertEquals("user delete comment", 204, commOrgcService.getRespStatus());			
				
				break;
			}
					
			LOGGER.debug("Finished OrgAdmin disabled for Community Calendar event...");
		}
				
	}
	
	@Test
	public void testCommunityOrgAdminEnabledCalendarEvent(){
		/*
		 * RTC 195200 As an org admin, I can use calendar existing APIs to do CRUD operations
		 * When ORG-ADMIN GK enabled, org admin has CRUD operations to his own org content
		 * 
		 * Step 0: user2 prepare community data - comm6
		 * Step 1: create a community comm6
		 * Step 2: Retrieve that community
		 * Step 3: add widget into community
		 * Step 4: user2 Retrieve Calendar
		 * Step 5: org-admin can retrieve Calendar
		 * Step 6: user2 post event
		 * Step 7: org-admin has access to post event 
		 * Step 8: org-admin has access to retrieve the event 
		 * Step 9: org-admin has access to update the event 
		 * Step 10: org-admin has access to post comment 
		 * Step 11: user2 post comment 
		 * Step 12: org-admin has access to retieve comment 
		 * Step 13: org-admin has access to delete event 
		 * Step 14: user2 retrieve event 
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {

			LOGGER.debug("Step 0: user2 prepare community data - comm6 ");
			String timeStamp = Utils.logDateFormatter.format(new Date());
			String comName = "Enable orgAdmin Calendar_user2_" + timeStamp;
			Community community = getCreatedPrivateCommunity(comName, "Test Org-admin enable calendar API.", comm2Service, comm2Service);			

			String service_url = null;
		    String event_url=null;

		    Feed remoteAppsFeed = getRemoteAppsFeed(community, comm2Service);
		    service_url = getCalendarFeedLink(remoteAppsFeed, StringConstants.REL_REMOTEAPPLICATION_PUBLISH);
			LOGGER.debug("Service URL : " + service_url);
			
			// get calendar service doc
			ExtensibleElement serviceFeed = comm2Service
					.getCommunityCalendarService(service_url);
			assertTrue(serviceFeed != null);
			
			// user2 can retrieve calendar
			LOGGER.debug("user2 Getting Community Calendar:");
			String calendar_url = service_url.replace(
					"calendar/service?", "calendar?");
			Entry calendar = (Entry) comm2Service
					.getCommunityCalendarService(calendar_url);
			assertTrue(calendar != null);
			
			LOGGER.debug("Step 5: org-admin can retrieve Calendar");
			LOGGER.debug("org-admin Getting Community Calendar:");
			Entry calendar_orgAdmin = (Entry) commAdminService
					.getCommunityCalendarService(calendar_url);
			assertTrue(calendar_orgAdmin != null);
			assertEquals("org-admin retrieve Calendar", 200, commAdminService.getRespStatus());
			
			LOGGER.debug("Step 6: user2 post event ");
			// post calendar event
			Event event = new Event("My event",
					StringConstants.STRING_YES_LOWERCASE, null,
					null, null, "2022-08-23T02:00:00.000Z",
					"2022-08-23T03:00:00.000Z", 0);
			event_url = getCalendarFeedLink(remoteAppsFeed, StringConstants.REL_REMOTEAPPLICATION_FEED);
			String eventUrl = event_url.replace(
					"type=event&calendarUuid", "calendarUuid");
			ExtensibleElement eventFeed = comm2Service
					.postCalendarEvent(eventUrl, event);
			assertTrue(eventFeed != null);
								
			LOGGER.debug("Step 7: org-admin can post event ");
			Event event_org = new Event("Org-Admin events",
					StringConstants.STRING_NO_LOWERCASE,
					StringConstants.STRING_WEEKLY, "1",
					"2022-10-24T02:00:00.000Z",
					"2022-08-24T02:00:00.000Z",
					"2022-08-24T03:00:00.000Z", 0);
			ExtensibleElement eventFeed_org = commAdminService
					.postCalendarEvent(eventUrl, event_org);
			assertTrue(eventFeed_org != null);
			assertEquals("org-admin post event", 201, commAdminService.getRespStatus());
			
			// retrieve calendar events
			LOGGER.debug("Step 8: org-admin has access to retrieve user2's event");
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				eventFeed = (Feed) commAdminService
						.getCalendarFeedWithRedirect(eventUrl
								+ "&startDate=2022-08-23T00:00:00+08:00&endDate=2022-12-06T00:00:00+08:00");
			} else {
				eventFeed = (Feed) commAdminService
						.getCalendarFeed(eventUrl
								+ "&startDate=2022-08-23T00:00:00+08:00&endDate=2022-12-06T00:00:00+08:00");
			}
			assertTrue(eventFeed != null);
			assertEquals(200, commAdminService.getRespStatus());
			
			for (Entry eEntry : ((Feed) eventFeed).getEntries()) {
				String selfUrl = eEntry.getSelfLinkResolvedHref()
						.toString();
				LOGGER.debug("org-admin retrieve user2's event");
				commAdminService.getCalendarFeed(selfUrl);
				assertEquals("Org-admin retrieve event", 200, commAdminService.getRespStatus());
				
				//get eventInstUuid
				String eventInstUuid = eEntry
						.getSimpleExtension(StringConstants.SNX_EVENT_INST_UUID);
				System.out.println("eventInstUuid***"+ eventInstUuid);
				
				for (Category eCategory : eEntry.getCategories()) {
					//update event
					LOGGER.debug("Step 9: org-admin has access to update the event ");
					Event updateEvent = new Event("My event",
							StringConstants.STRING_YES_LOWERCASE, null,
							null, null, "2022-08-23T02:00:00.000Z",
							"2022-08-23T03:00:00.000Z", 0);
					updateEvent.setId(new IRI(
							"urn:lsid:ibm.com:calendar:event:"
									.concat(eventInstUuid)));			
					updateEvent.setIsEvent(eCategory
							.setTerm("event-instance"));

					ExtensibleElement updateEventFeed_org = commAdminService.putCalendarEvent(selfUrl, updateEvent);
					assertTrue(updateEventFeed_org != null);
					assertEquals("Org-admin update event", 200, commAdminService.getRespStatus());
				}
				// create comment on event
				LOGGER.debug("Step 10: org-admin has access to post comment ");	
				String commentUrl = eEntry.getLinkResolvedHref(
						StringConstants.REL_EDIT).toString();
				
				commentUrl = commentUrl.replace("/event?",
						"/event/comment?");
				CommentToEvent comment = new CommentToEvent(
						"My comment");
				eventFeed = commAdminService.postEventComment(commentUrl,
						comment);
				assertEquals("org-admin post comment", 201, commAdminService.getRespStatus());
				
				LOGGER.debug("Step 11: user2 post comment ");
				eventFeed = comm2Service.postEventComment(commentUrl,
						comment);
				assertEquals("user2 post comment", 201, comm2Service.getRespStatus());
				
				LOGGER.debug("Retrieve the comment ");
				//retrieve the comment
				String mycommentUrl = ((Entry) eventFeed)
						.getSelfLinkResolvedHref().toString();
				ExtensibleElement commentFeed = comm2Service.getCalendarFeed(mycommentUrl);
				assertTrue(commentFeed != null);
				
				LOGGER.debug("Step 12: org-admin has access to retieve comment ");
				commAdminService.getCalendarFeed(mycommentUrl);
				assertEquals("org-admin retrieve comment", 200, commAdminService.getRespStatus());
				
				LOGGER.debug("Step 13: org-admin has access to delete event ");
				commAdminService.deleteFeedLink(selfUrl);
				assertEquals("org-admin delete comment", 204, commAdminService.getRespStatus());
				
				LOGGER.debug("Step 14: user2 retrieve event ");
				comm2Service.getCalendarFeed(selfUrl);
				assertEquals("user2 found comment", 404, comm2Service.getRespStatus());			
				
				break;
				
			}
			
		}
		LOGGER.debug("Finished OrgAdmin enabled for Community Calendar event...");
	}
	
	@Test
	public void testCommunity3rdOrgAdminEnabledCalendarEvent(){
		/*
		 * RTC 195200 As an org admin, I can use calendar existing APIs to do CRUD operations
		 * orgB's org-admin cannot access to orgA's content.
		 * 
		 * Step 0: user2 prepare community data - comm6
		 * Step 1: create a community comm6
		 * Step 2: Retrieve that community
		 * Step 3: add widget into community
		 * Step 4: user2 Retrieve Calendar
		 * Step 5: orgB's org-admin cannot retrieve Calendar
		 * Step 6: user2 post event
		 * Step 7: orgB's org-admin has no access to post event 
		 * Step 8: orgB's org-admin has no access to retrieve the event 
		 * Step 9: orgB's org-admin has no access to update the event 
		 * Step 10: orgB's org-admin has no access to post comment 
		 * Step 11: user2 post comment 
		 * Step 12: orgB's org-admin has no access to retieve comment 
		 * Step 13: orgB's org-admin has no access to delete event 
		 * Step 14: user2 delete event 
		 */
		if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {

			LOGGER.debug("Step 0: user2 prepare community data - comm6 ");
			String timeStamp = Utils.logDateFormatter.format(new Date());
			String comName = "Calendar_user2_" + timeStamp;
			Community community = getCreatedPrivateCommunity(comName, "Test Org-admin disabled calendar API.", comm2Service, comm2Service);			

			Feed remoteAppsFeed = null;
			String service_url = null;
		    String event_url=null;

		    remoteAppsFeed = getRemoteAppsFeed(community, comm2Service);
		    service_url = getCalendarFeedLink(remoteAppsFeed, StringConstants.REL_REMOTEAPPLICATION_PUBLISH);
			LOGGER.debug("Service URL : " + service_url);

			// get calendar service doc
			ExtensibleElement serviceFeed = comm2Service
					.getCommunityCalendarService(service_url);
			assertTrue(serviceFeed != null);
			
			// user2 can retrieve calendar
			LOGGER.debug("user2 Getting Community Calendar:");
			String calendar_url = service_url.replace(
					"calendar/service?", "calendar?");
			Entry calendar = (Entry) comm2Service
					.getCommunityCalendarService(calendar_url);
			assertTrue(calendar != null);
			
			LOGGER.debug("Step 5: org-admin cannot retrieve Calendar");
			LOGGER.debug("org-admin Getting Community Calendar:");
			Entry calendar_orgAdmin = (Entry) commOrgbService
					.getCommunityCalendarService(calendar_url);
			assertEquals("org-admin retrieve Calendar", 403, commOrgbService.getRespStatus());
			
			LOGGER.debug("Step 6: user2 post event ");
			// post calendar event
			Event event = new Event("My event",
					StringConstants.STRING_YES_LOWERCASE, null,
					null, null, "2022-08-23T02:00:00.000Z",
					"2022-08-23T03:00:00.000Z", 0);
			event_url = getCalendarFeedLink(remoteAppsFeed, StringConstants.REL_REMOTEAPPLICATION_FEED);
			String eventUrl = event_url.replace(
					"type=event&calendarUuid", "calendarUuid");
			ExtensibleElement eventFeed = comm2Service
					.postCalendarEvent(eventUrl, event);
			assertTrue(eventFeed != null);
								
			LOGGER.debug("Step 7: org-admin has no access to post event ");
			Event event_org = new Event("Org-Admin events",
					StringConstants.STRING_NO_LOWERCASE,
					StringConstants.STRING_WEEKLY, "1",
					"2022-10-24T02:00:00.000Z",
					"2022-08-24T02:00:00.000Z",
					"2022-08-24T03:00:00.000Z", 0);
			ExtensibleElement eventFeed_org = commOrgbService
					.postCalendarEvent(eventUrl, event_org);
			assertEquals("org-admin post event", 403, commOrgbService.getRespStatus());
			
			// retrieve calendar events
			if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD) {
				eventFeed = (Feed) comm2Service
						.getCalendarFeedWithRedirect(eventUrl
								+ "&startDate=2022-08-23T00:00:00+08:00&endDate=2022-12-06T00:00:00+08:00");
			} else {
				eventFeed = (Feed) comm2Service
						.getCalendarFeed(eventUrl
								+ "&startDate=2022-08-23T00:00:00+08:00&endDate=2022-12-06T00:00:00+08:00");
			}
			assertTrue(eventFeed != null);
			assertEquals(200, comm2Service.getRespStatus());
				
			for (Entry eEntry : ((Feed) eventFeed).getEntries()) {
				String selfUrl = eEntry.getSelfLinkResolvedHref()
						.toString();
				
				LOGGER.debug("Step 8: org-admin has no access to retrieve the event ");
				commOrgbService.getCalendarFeed(selfUrl);
				assertEquals("Org-admin retrieve event", 403, commOrgbService.getRespStatus());
				
				LOGGER.debug("Step 9: org-admin has no access to update the event ");
				commOrgbService.putCalendarEvent(selfUrl, event);
				assertEquals("Org-admin update event", 403, commOrgbService.getRespStatus());
				
				// create comment on event
				LOGGER.debug("Step 10: org-admin has no access to post comment ");	
				eventFeed = comm2Service.getCalendarFeed(selfUrl);
				String commentUrl = eEntry.getLinkResolvedHref(
						StringConstants.REL_EDIT).toString();
				
				commentUrl = commentUrl.replace("/event?",
						"/event/comment?");
				CommentToEvent comment = new CommentToEvent(
						"My comment");
				eventFeed = commOrgbService.postEventComment(commentUrl,
						comment);
				assertEquals("org-admin post comment", 403, commOrgbService.getRespStatus());
				
				LOGGER.debug("Step 11: user2 post comment ");
				eventFeed = comm2Service.postEventComment(commentUrl,
						comment);
				assertEquals("user2 post comment", 201, comm2Service.getRespStatus());
				
				LOGGER.debug("Retrieve the comment ");
				//retrieve the comment
				String mycommentUrl = ((Entry) eventFeed)
						.getSelfLinkResolvedHref().toString();
				ExtensibleElement commentFeed = comm2Service.getCalendarFeed(mycommentUrl);
				assertTrue(commentFeed != null);
				
				LOGGER.debug("Step 12: org-admin has no access to retieve comment ");
				commOrgbService.getCalendarFeed(mycommentUrl);
				assertEquals("org-admin retrieve comment", 403, commOrgbService.getRespStatus());
				// Found
				LOGGER.debug("user2 can still find the comment");
				assertTrue(commentFeed != null);
				assertTrue(((Entry) commentFeed).getContent()
						.equalsIgnoreCase("My comment"));
				
				LOGGER.debug("Step 13: org-admin has no access to delete event ");
				commOrgbService.deleteFeedLink(selfUrl);
				assertEquals("org-admin delete comment", 403, commOrgbService.getRespStatus());
				
				LOGGER.debug("Step 14: user2 delete event ");
				assertEquals(true, comm2Service.deleteFeedLink(selfUrl));
				assertEquals("user2 delete comment", 204, comm2Service.getRespStatus());			
				
				break;
			}
					
			LOGGER.debug("Finished 3rdOrgAdmin disabled for Community Calendar event...");
		}
				
	}
}

