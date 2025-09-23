package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

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
/* Copyright IBM Corp. 2010, 2015, 2016                              */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */
/**
 * [Legacy Mentions replaced with CKEditor] FVT UI Automation for Story 139553 and Story 139607
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139666
 * @author Patrick Doherty
 */

public class FVT_SU_PrivateImageFile extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterSU };
	
	private APIFileHandler filesAPIUser1;
	private BaseFile baseFile;
	private FileEntry privateFile;
	private User testUser1, testUser2;
	
	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		// User 1 will now create a private file
		baseFile = FileBaseBuilder.buildBaseFile(Data.getData().file1, ".jpg", ShareLevel.NO_ONE);
		privateFile = FileEvents.addFile(baseFile, testUser1, filesAPIUser1);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
	
		// Delete the file created during the test
		filesAPIUser1.deleteFile(privateFile);
	}

	/**
	* addStatusUpdate_privateImageFileAttached() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 log into Connections</B></li>
	*<li><B>Step: User 1 upload a private image file</B></li>
	*<li><B>Step: User 1 go to Homepage / Updates / I'm Following / All</B></li>
	*<li><B>Step: User 1 add a status update and select "attach a file"</B></li>
	*<li><B>Step: User 1 select the private file uploaded from point 2 in "My Files" and click "OK"</B></li>
	*<li><B>Step: User 1 gets a message to say that the file will be made public and click "OK"</B></li>
	*<li><B>Step: User 1 select "Post" - verification point 1</B></li>
	*<li><B>Step: User 1 click on the image from the story in Homepage - verification point 2</B></li>
	*<li><B>Step: User 2 log into Connections</B></li>
	*<li><B>Step: User 2 go to Homepage / Discover / All Updates</B></li>
	*<li><B>Step: User 2 go to the stories from above of the status update and file attachment</B></li>
	*<li><B>Step: User 2 attempt to open the file - verification point 3</B></li>
	*<li><B>Verify: Verify that the status and file attachment has been added to the relevant view and that the file details are shown correctly with an image preview</B></li>
	*<li><B>Verify: Verify that the EE for the story is opened and there is an image preview and the image name is a clickable link.</B></li>
	*<li><B>Verify: Verify that the the story opens in the EE for user2 and there is an image preview and the image name is a clickable link.</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/55766BBB68D56A5985257944005F986A">TTT - Activity Stream Sharebox - 00024 - User should be able to attach a private image file from My Files</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void addStatusUpdate_privateImageFileAttached() {
		
		ui.startTest();
		
		// Log in as User 1 and go to the I'm Following view
		LoginEvents.loginAndGotoImFollowing(ui, testUser1, false);
		
		// User 1 will now post a status update with file attachment from 'My Files'
		String user1StatusUpdate = Data.getData().commonStatusUpdate + Helper.genStrongRand();
		ProfileEvents.addStatusUpdateWithFileAttachmentUsingUI_FromMyFiles(ui, testUser1, baseFile, user1StatusUpdate);
		
		// Create the elements to be verified
		String fileNameLinkEE = "link=" + baseFile.getRename() + baseFile.getExtension();
		
		for(String filter : TEST_FILTERS) {
			// Verify that the status update event is displayed in the AS
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1StatusUpdate}, filter, true);
			
			// Verify that the image name is a clickable link in the AS
			HomepageValid.verifyElementsInAS(ui, driver, new String[]{fileNameLinkEE}, null, true);
		}	
		// User 1 will now open the EE for the status update event
		UIEvents.openEE(ui, user1StatusUpdate);
		
		// Verify that the status update is displayed in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1StatusUpdate}, null, true);
		
		// Verify that the image name is a clickable link in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{fileNameLinkEE, HomepageUIConstants.EE_ImagePreview}, null, true);
		
		// Switch focus back to the top frame
		UIEvents.switchToTopFrame(ui);
		
		// Log out from Connections
		LoginEvents.logout(ui);
		
		// Log in as User 2 and go to the Discover view
		LoginEvents.loginAndGotoDiscover(ui, testUser2, true);
		
		// User 2 will now open the EE for the status update event
		UIEvents.openEE(ui, user1StatusUpdate);
		
		// Verify that the status update is displayed in the EE
		HomepageValid.verifyItemsInAS(ui, driver, new String[]{user1StatusUpdate}, null, true);
		
		// Verify that the image name is a clickable link in the EE
		HomepageValid.verifyElementsInAS(ui, driver, new String[]{fileNameLinkEE, HomepageUIConstants.EE_ImagePreview}, null, true);
		
		ui.endTest();
	}
}