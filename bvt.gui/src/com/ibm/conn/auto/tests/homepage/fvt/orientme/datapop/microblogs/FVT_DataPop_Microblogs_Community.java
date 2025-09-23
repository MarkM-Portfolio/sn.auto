package com.ibm.conn.auto.tests.homepage.fvt.orientme.datapop.microblogs;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.appobjects.base.BaseCommunity.Access;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopSetup;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.CommunityBaseBuilder;
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
 * Date:	2nd March 2017
 */

public class FVT_DataPop_Microblogs_Community extends DataPopSetup {

	private APICommunitiesHandler communitiesAPIUser1;
	private APIProfilesHandler profilesAPIUser1;
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
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will now create a public community
		baseCommunity = CommunityBaseBuilder.buildBaseCommunity(getClass().getSimpleName() + Helper.genStrongRand(), Access.PUBLIC);
		publicCommunity = CommunityEvents.createNewCommunity(baseCommunity, testUser1, communitiesAPIUser1);
		getTestCaseData().addCreateCommunityData(publicCommunity, baseCommunity);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Microblog_CreateStatusUpdate() {
		
		// User 1 will now post a status update to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser1, profilesAPIUser1, user1StatusUpdate);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Microblog_LikeStatusUpdate() {
		
		// User 1 will now post a status update to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser1, profilesAPIUser1, user1StatusUpdate);
		
		// User 1 will now like / recommend the community status update
		CommunityEvents.likeStatusUpdate(profilesAPIUser1, statusUpdateId);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Microblog_CreateStatusUpdateComment() {
		
		// User 1 will now post a status update to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser1, profilesAPIUser1, user1StatusUpdate);
		
		// User 1 will post a comment to the status update
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateComment(profilesAPIUser1, statusUpdateId, user1Comment);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Community_Microblog_LikeStatusUpdateComment() {
		
		// User 1 will now post a status update to the community
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String statusUpdateId = CommunityEvents.addStatusUpdate(publicCommunity, communitiesAPIUser1, profilesAPIUser1, user1StatusUpdate);
		
		// User 1 will post a comment to the status update and will like / recommend the comment
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		CommunityEvents.addStatusUpdateCommentAndLikeComment(profilesAPIUser1, profilesAPIUser1, statusUpdateId, user1Comment);
	}
}
