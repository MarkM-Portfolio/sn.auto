package com.ibm.conn.auto.tests.homepage.fvt.orientme.datapop.events;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseEvent;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICalendarHandler;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopSetup;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.Mentions;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.EventBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.MentionsBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityCalendarEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.lconn.automation.framework.services.communities.nodes.Community;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2017                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * @author 	Anthony Cox
 * Date:	28th February 2017
 */

public class FVT_DataPop_Events_Community extends DataPopSetup {

	private APICalendarHandler calendarAPIUser1;
	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		setFilename(getClass().getSimpleName());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		getTestCaseData().addUserAssignmentData(listOfStandardUsers);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		calendarAPIUser1 = initialiseAPICalendarHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public community with the Events widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.EVENTS, isOnPremise, testUser1, communitiesAPIUser1);
		getTestCaseData().addCreateCommunityData(publicCommunity, baseCommunity);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Event_Single_Create() {
		
		// User 1 will now create a single event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(getClass().getSimpleName() + Helper.genStrongRand(), false);
		CommunityCalendarEvents.addCalendarEvent(publicCommunity, baseEvent, testUser1, calendarAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Event_Single_Updated() {
		
		// User 1 will now create a single event in the community and will update the description for the event
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(getClass().getSimpleName() + Helper.genStrongRand(), false);
		CommunityCalendarEvents.addCalendarEventAndEditFirstInstanceDescription(publicCommunity, baseEvent, editedDescription, testUser1, calendarAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Event_Single_Commented() {
		
		// User 1 will now create a single event in the community and will comment on the event
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(getClass().getSimpleName() + Helper.genStrongRand(), false);
		CommunityCalendarEvents.addCalendarEventAndAddComment(publicCommunity, baseEvent, user1Comment, testUser1, calendarAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Event_Single_WithMentions() {
		
		// User 1 will now create a single event in the community with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(getClass().getSimpleName() + Helper.genStrongRand(), false);
		CommunityCalendarEvents.addCalendarEventWithMentions(publicCommunity, baseEvent, mentions, testUser1, calendarAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Event_Series_Create() {
		
		// User 1 will now create a series event in the community
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(getClass().getSimpleName() + Helper.genStrongRand(), true);
		CommunityCalendarEvents.addCalendarEvent(publicCommunity, baseEvent, testUser1, calendarAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Event_Series_Updated() {
		
		// User 1 will now create a series event in the community and will update the description for the event
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(getClass().getSimpleName() + Helper.genStrongRand(), true);
		CommunityCalendarEvents.addCalendarEventAndEditSeriesDescription(publicCommunity, baseEvent, editedDescription, testUser1, calendarAPIUser1);	
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Event_Series_Instance_Updated() {
		
		// User 1 will now create a series event in the community and will update the description of the first instance of the series event
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(getClass().getSimpleName() + Helper.genStrongRand(), true);
		CommunityCalendarEvents.addCalendarEventAndEditFirstInstanceDescription(publicCommunity, baseEvent, editedDescription, testUser1, calendarAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Event_Series_Commented() {
		
		// User 1 will now create a series event in the community and will comment on the event
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(getClass().getSimpleName() + Helper.genStrongRand(), true);
		CommunityCalendarEvents.addCalendarEventAndAddComment(publicCommunity, baseEvent, user1Comment, testUser1, calendarAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Event_Series_WithMentions() {
		
		// User 1 will now create a series event in the community with mentions to User 2
		Mentions mentions = MentionsBaseBuilder.buildBaseMentions(testUser2, profilesAPIUser2, serverURL, Helper.genStrongRand(), Helper.genStrongRand());
		BaseEvent baseEvent = EventBaseBuilder.buildBaseCalendarEvent(getClass().getSimpleName() + Helper.genStrongRand(), true);
		CommunityCalendarEvents.addCalendarEventWithMentions(publicCommunity, baseEvent, mentions, testUser1, calendarAPIUser1);
	}
}