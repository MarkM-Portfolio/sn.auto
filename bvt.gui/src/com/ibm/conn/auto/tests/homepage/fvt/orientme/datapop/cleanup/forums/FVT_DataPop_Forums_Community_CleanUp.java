package com.ibm.conn.auto.tests.homepage.fvt.orientme.datapop.cleanup.forums;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.lcapi.APICommunitiesHandler;
import com.ibm.conn.auto.tests.homepage.fvt.orientme.configs.DataPopCleanUp;

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

public class FVT_DataPop_Forums_Community_CleanUp extends DataPopCleanUp {

	private APICommunitiesHandler communitiesAPIUser1;
	private User testUser1;
	
	@BeforeClass(alwaysRun = true)
	public void setUpClass() {
		
		// Initialise the configuration
		setFilename(getClass().getSimpleName());
		readTestCaseDataFromFile();
		
		// Initialise all relevant users
		createListOfUsersFromJsonContent(1);
		
		testUser1 = getListOfUsers().get(0);

		communitiesAPIUser1 = initialiseAPICommunitiesHandlerUser(testUser1);
		
		// Initialise all communities to be deleted
		createListOfCommunitiesToBeDeletedFromJsonContent();
	}
	
	@Test
	public void cleanUp_DataPop_Forums_Community() {
		
		log.info("INFO: Now deleting the community created during the data population class");
		communitiesAPIUser1.deleteCommunity(getListOfCommunities().get(0));
	}
}