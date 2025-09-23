package com.ibm.conn.auto.tests.homepage.fvt.orientme.datapop.feeds;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.BaseWidget;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.appobjects.base.BaseFeed;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopSetup;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
import com.ibm.conn.auto.util.baseBuilder.FeedBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityEvents;
import com.ibm.conn.auto.util.eventBuilder.community.CommunityFeedEvents;
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

public class FVT_DataPop_Feeds_Community extends DataPopSetup {

	private APICommunitiesHandler communitiesAPIUser1;
	private BaseCommunity baseCommunity;
	private Community publicCommunity;
	private User testUser1;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		setFilename(getClass().getSimpleName());
		
		setListOfStandardUsers(1);
		testUser1 = listOfStandardUsers.get(0);
		getTestCaseData().addUserAssignmentData(listOfStandardUsers);
		
		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		// User 1 will now create a public community with the Feeds widget added
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunityAndAddWidget(baseCommunity, BaseWidget.FEEDS, isOnPremise, testUser1, communitiesAPIUser1);
		getTestCaseData().addCreateCommunityData(publicCommunity, baseCommunity);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Feed_Create() {
		
		// User 1 will now create a new feed in the community
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(getClass().getSimpleName() + Helper.genStrongRand());
		CommunityFeedEvents.createFeed(publicCommunity, baseFeed, testUser1, communitiesAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Feed_Update() {
		
		// User 1 will now create a new feed in the community and will then update the description of the feed
		String editedDescription = Data.getData().commonDescription + Helper.genStrongRand();
		BaseFeed baseFeed = FeedBaseBuilder.buildBaseFeed(getClass().getSimpleName() + Helper.genStrongRand());
		CommunityFeedEvents.createFeedAndEditFeedDescription(publicCommunity, baseFeed, editedDescription, testUser1, communitiesAPIUser1);
	}
}
