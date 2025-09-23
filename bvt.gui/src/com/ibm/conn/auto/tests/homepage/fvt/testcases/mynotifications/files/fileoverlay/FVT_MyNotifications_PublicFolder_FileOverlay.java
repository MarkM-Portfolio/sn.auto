package com.ibm.conn.auto.tests.homepage.fvt.testcases.mynotifications.files.fileoverlay;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.appobjects.base.BaseFile;
import com.ibm.conn.auto.appobjects.base.BaseFile.ShareLevel;
import com.ibm.conn.auto.config.SetUpMethodsFVT;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.lcapi.APIProfilesHandler;
import com.ibm.conn.auto.tests.homepage.HomepageValid;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.baseBuilder.FileBaseBuilder;
import com.ibm.conn.auto.util.eventBuilder.files.FileEvents;
import com.ibm.conn.auto.util.eventBuilder.login.LoginEvents;
import com.ibm.conn.auto.util.eventBuilder.ui.UIEvents;
import com.ibm.conn.auto.util.newsStoryBuilder.files.FileNewsStories;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Role;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2016, 2017                          			 */
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

public class FVT_MyNotifications_PublicFolder_FileOverlay extends SetUpMethodsFVT {
	
	private final String[] TEST_FILTERS = { HomepageUIConstants.FilterAll, HomepageUIConstants.FilterFiles };

	private APIFileHandler filesAPIUser1;
	private APIProfilesHandler profilesAPIUser2;
	private BaseFile baseFolder;
	private FileEntry publicFolder;
	private User testUser1, testUser2;
								   
	@BeforeClass(alwaysRun=true)
	public void setUpClass(){
	
		// Initialise the configuration
		setUserCheckoutToken(getClass().getSimpleName() + Helper.genStrongRand());
		
		setListOfStandardUsers(2);
		testUser1 = listOfStandardUsers.get(0);
		testUser2 = listOfStandardUsers.get(1);
		
		filesAPIUser1 = initialiseAPIFileHandlerUser(testUser1);
		
		profilesAPIUser2 = initialiseAPIProfilesHandlerUser(testUser2);
		
		// User 1 will now create a public folder which will be shared with User 2
		baseFolder = FileBaseBuilder.buildBaseFile(getClass().getSimpleName() + Helper.genStrongRand(), "", ShareLevel.EVERYONE, profilesAPIUser2);
		publicFolder = FileEvents.createFolder(testUser1, filesAPIUser1, baseFolder, Role.ALL);
	}
	
	@AfterClass(alwaysRun = true)
	public void performCleanUp() {
		
		// Delete the folder created during the test
		filesAPIUser1.deleteFolder(publicFolder);
	}
	
	/**
	* fileOverlayOpens_PublicFolderShared() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: User 1 shares a public folder with User 2</B></li>
	*<li><B>Step: User 2 go to Homepage / My Notifications / For Me / All & Files, selects the story</B></li>
	*<li><B>Verify: EE launches</B></li>
	*<li><a HREF="Notes://Parallan/85257863004CBF81/C07DF89F1C990C3A8525722C004A1BD4/CB653C890A1F4FB685257E920041E5AF">TTT - AS - My Notifications - For Me - 00030 - File Details overlay</a></li>
	*</ul>
	*/
	@Test (groups={"fvtonprem", "fvtcloud"})
	public void fileOverlayOpens_PublicFolderShared() {

		ui.startTest();
		
		// Log in as User 2 and go to the My Notifications view
		LoginEvents.loginAndGotoMyNotifications(ui, testUser2, false);
		
		// Create the news story to be used to open the EE
		String madeEditorOfFolderEvent = FileNewsStories.getMadeEditorOfAFolderNewsStory_You(ui);
		
		for(String filter : TEST_FILTERS) {
			// Filter the UI by the relevant filter
			UIEvents.filterBy(ui, filter);
			
			// Open the EE for the news story
			UIEvents.openEE(ui, madeEditorOfFolderEvent);
			
			// Switch focus to the EE
			UIEvents.switchToEEFrame(ui);
			
			// Verify that the folder name is displayed in the EE
			HomepageValid.verifyItemsInAS(ui, driver, new String[]{baseFolder.getName()}, null, true);
			
			// Switch focus back to the top frame
			UIEvents.switchToTopFrame(ui);
		}
		ui.endTest();
	}
}