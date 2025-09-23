package com.ibm.conn.auto.tests.homepage.fvt.testcases.discover.files.fileoverlay;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.profile.ProfileEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015, 2016                                	 */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [FiDO replacing Files EE in Activity Streams] FVT UI Automation for Story 154776
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/165016
 * @author Patrick Doherty
 */

public class FVT_Discover_SU_FileAttached_FileOverlay extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	
	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser1;
	private BaseFile baseFile;
	private FileEntry publicFile;
	private String statusUpdate, statusUpdateId;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser1 = initialiseAPIProfilesHandlerUser(testUser1);
		
		// User 1 will now create a public file
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.EVERYONE);
		publicFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
		
		// User 1 will now post a status update with file attachment (using the public file)
		statusUpdate = Data.getData().commonComment + Helper.genStrongRand();
		statusUpdateId = ProfileEvents.postStatusUpdateWithFileAttachment(profilesAPIUser1, statusUpdate, publicFile);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the status update with file attachment created during the test
		profilesAPIUser1.deleteBoardMessage(statusUpdateId);
		
		// Delete the file created during the test
		filesAPIUser1.deleteFile(publicFile);
	}
	
	/**
	* SU_FileAttached_Discover_EE() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Connections</B></li>
	*<li><B>Step: User 1 creates a status update with a file attached from the local machine</B></li>
	*<li><B>Step: User 2 go to Homepage / Updates / Discover / All & Files, selects the story</B></li>
	*<li><B>Verify: File Detail Overly does NOT launch, legacy Files EE launches</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/8510E57080264D8E85257E90004733F7">TTT - DISC - FILES - 00110 - File Details overlay</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void SU_FileAttached_Discover_EE() {
		
		ui.startTest();
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, false);
		
		for(String filter : TEST_FILTERS) {
			// Set the filter to be tested
			UIEvents.filterBy(ui, filter);
			
			// Open the EE for the news story - the success of using this method proves that the file details overlay did NOT launch when the news story was clicked
			UIEvents.openEE(ui, statusUpdate);
			
			// Verify that the status update and file name are displayed in the EE
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{statusUpdate, (baseFile.getRename() + baseFile.getExtension())}, null, true);
			
			// Switch focus back to the top frame
			UIEvents.switchToTopFrame(ui);
		}
		ui.endTest();
	}
}