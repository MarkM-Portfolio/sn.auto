package com.ibm.conn.auto.tests.homepage.fvt.orientme.datapop.cleanup.communities;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopCleanUp;
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
 * Date:	27th February 2017
 */

public class FVT_DataPop_Communities_CleanUp extends DataPopCleanUp {

	private APICommunitiesHandler communitiesAPIUser1, communitiesAPIUser2;
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

		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		communitiesAPIUser2 = initialiseAPICommunitiesHandlerUser(testUser2);
		
		// Initialise all communities to be deleted
		createListOfCommunitiesToBeDeletedFromJsonContent();
	}
	
	@Test
	public void cleanUp_DataPop_Communities() {
		
		// Delete all of the communities created during the test
		for(Community community : getListOfCommunities()) {
			APICommunitiesHandler apiUserToDeleteCommunity;
			if(community.getAuthors().get(0).getName().trim().equals(testUser1.getDisplayName().trim())) {
				apiUserToDeleteCommunity = communitiesAPIUser1;
			} else {
				apiUserToDeleteCommunity = communitiesAPIUser2;
			}
			log.info("INFO: Now deleting the community created during the data population class");
			apiUserToDeleteCommunity.deleteCommunity(community);
		}
	}
}
