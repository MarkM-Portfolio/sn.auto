package com.ibm.conn.auto.tests.homepage.fvt.orientme.datapop.cleanup.profiles;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopCleanUp;
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

public class FVT_DataPop_Profiles_CleanUp extends DataPopCleanUp {

	private APIProfilesHandler profilesAPIUser1, profilesAPIUser2;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setFilename(getClass().getSimpleName());
		readTestCaseDataFromFile();
		
		// Initialise all relevant users
		createListOfUsersFromJsonContent(2);
		
		testUser1 = getListOfUsers().get(0);
		testUser2 = getListOfUsers().get(1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// Initialise all board messages to be deleted
		createMapOfBoardMessagesToBeDeletedFromJsonContent();
		
		// Initialise all invitations to be deleted
		createMapOfInvitationsToBeDeletedFromJsonContent();
	}
	
	@Test
	public void cleanUp_DataPop_Profile() {
		
		log.info("INFO: Now deleting all of the board messages created during the data population class");
		for(String boardMessageId : getMapOfBoardMessages().keySet()) {
			profilesAPIUser1.deleteBoardMessage(boardMessageId);
		}
		
		// Clean up the invitation by having User 2 accept User 1's network invitation and then having User 1 remove them from their network again
		for(Invitation invitation : getMapOfInvitations().keySet()) {
			log.info("INFO: " + profilesAPIUser2.getDesplayName() + " will now accept the network invitation sent during the data population class");
			profilesAPIUser2.acceptNetworkInvitation(invitation, profilesAPIUser1);
			
			log.info("INFO: " + profilesAPIUser1.getDesplayName() + " will now remove " + profilesAPIUser2.getDesplayName() + " from their network");
			profilesAPIUser1.deleteUserFromNetworkConnections(profilesAPIUser2);
		}
	}
}
