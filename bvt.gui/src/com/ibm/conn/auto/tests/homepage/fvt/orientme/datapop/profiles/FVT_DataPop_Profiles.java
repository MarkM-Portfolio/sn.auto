package com.ibm.conn.auto.tests.homepage.fvt.orientme.datapop.profiles;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopSetup;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.lconn.automation.framework.services.communities.nodes.Invitation;

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
 * Date:	3rd March 2017
 */

public class FVT_DataPop_Profiles extends DataPopSetup {

	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
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
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Profiles_CreateBoardMessage() {
		
		// User 1 will now post a board message to User 2
		String user1BoardMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1BoardMessageId = ProfileEvents.addBoardMessage(user1BoardMessage, profilesAPIUser1, profilesAPIUser2);
		getTestCaseData().addCreateBoardMessageData(user1BoardMessageId, user1BoardMessage, testUser1, testUser2);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Profiles_LikeBoardMessage() {
		
		// User 1 will now post a board message to User 2
		String user1BoardMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1BoardMessageId = ProfileEvents.addBoardMessage(user1BoardMessage, profilesAPIUser1, profilesAPIUser2);
		getTestCaseData().addCreateBoardMessageData(user1BoardMessageId, user1BoardMessage, testUser1, testUser2);
		
		// User 1 will now like / recommend the board message
		ProfileEvents.likeBoardMessage(profilesAPIUser1, user1BoardMessageId);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Profiles_CommentOnBoardMessage() {
		
		// User 1 will now post a board message to User 2
		String user1BoardMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1BoardMessageId = ProfileEvents.addBoardMessage(user1BoardMessage, profilesAPIUser1, profilesAPIUser2);
		getTestCaseData().addCreateBoardMessageData(user1BoardMessageId, user1BoardMessage, testUser1, testUser2);
		
		// User 1 will now post a comment to the board message
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		ProfileEvents.addBoardMessageComment(user1BoardMessageId, user1Comment, profilesAPIUser1);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Profiles_LikeCommentOnBoardMessage() {
		
		// User 1 will now post a board message to User 2
		String user1BoardMessage = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		String user1BoardMessageId = ProfileEvents.addBoardMessage(user1BoardMessage, profilesAPIUser1, profilesAPIUser2);
		getTestCaseData().addCreateBoardMessageData(user1BoardMessageId, user1BoardMessage, testUser1, testUser2);
		
		// User 1 will now post a comment to the board message
		String user1Comment = Data.getData().commonComment + Helper.genStrongRand();
		String user1CommentId = ProfileEvents.addBoardMessageComment(user1BoardMessageId, user1Comment, profilesAPIUser1);
		
		// User 1 will now like / recommend the comment
		ProfileEvents.likeComment(profilesAPIUser1, user1CommentId);
	}
	
	@Test(groups = {"fvt_orientme_onprem", "fvt_orientme_cloud"})
	public void datapop_Profiles_InviteToJoinNetwork() {
		
		// User 1 will now invite User 2 to join their network
		Invitation networkInvitation = ProfileEvents.inviteUserToJoinNetwork(profilesAPIUser1, profilesAPIUser2);
		getTestCaseData().addCreateInvitationData(networkInvitation, testUser1, testUser2);
	}
}