package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.microblogs;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2015                                    */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;

import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;

/**
 * [Legacy Mentions replaced with CKEditor] FVT UI Automation for Story 139553 and Story 139607
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139666
 * @author Patrick Doherty
 */
public class FVT_SU_ShowMore extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FVT_SU_ShowMore.class);

	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		
	}
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		ui = HomepageUI.getGui(cfg.getProductName(), driver);
		
	}

	/**
	* addStatusUpdate_ShowMore() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: Log into Connections</B></li>
	*<li><B>Step: Click into the embedded sharebox</B></li>
	*<li><B>Step: Add a status update with 4 lines</B></li>
	*<li><B>Step: Add another status update with 10 lines</B></li>
	*<li><B>Step: Refresh - verification point 1</B></li>
	*<li><B>Step: Click "...Show more" - verification point 2</B></li>
	*<li><B>Verify: Verify that the status update with 4 lines does not have the "...Show more" link and the status update with 10 lines does have the "...Show more" link</B></li>
	*<li><B>Verify: Verify that the "...Show more" link disappears when it has been clicked and the user can see the entire status added</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/523A01B90830B7738525799C003EA953">TTT - Microblogs Limit - 00012 - If large status update added a show more link should appear</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtonprem", "fvtcloud"})
	public void addStatusUpdate_ShowMore(){

		String suLine1 = Helper.genStrongRand();
		String suLine2 = Data.getData().buttonOK + Helper.genMonthDateBasedRandVal();
		String suLine3 = Data.getData().buttonPost + Helper.genMonthDateBasedRandVal();
		String suLine4 = Data.getData().buttonSave + Helper.genMonthDateBasedRandVal();
		String suLine5 = Data.getData().buttonSend + Helper.genMonthDateBasedRandVal();
		String suLine6 = Helper.genMonthDateBasedRandVal() + Data.getData().buttonOK;
		String suLine7 = Helper.genMonthDateBasedRandVal() + Data.getData().buttonPost;
		String suLine8 = Helper.genMonthDateBasedRandVal() + Data.getData().buttonSave;
		String suLine9 = Helper.genMonthDateBasedRandVal() + Data.getData().buttonSend;
		String suLine10 = Helper.genMonthDateBasedRandVal() + Data.getData().buttonUpload;
		String statusMessage = suLine1 + "\n" + suLine2 + "\n" + suLine3 + "\n" + suLine4 + "\n" + suLine5 + "\n" + suLine6 + "\n" + suLine7 + "\n" + suLine8 + "\n" + suLine9 + "\n" + suLine10;
		
		ui.startTest();

		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to post status update and verify successful post");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);

		log.info("INFO: Navigate to Status Updates");
		ui.gotoStatusUpdates();

		log.info("INFO: Post a status update");
		ui.postHomepageUpdate(statusMessage);
		
		ui.fluentWaitTextPresent(Data.getData().postSuccessMessage);

		log.info("INFO: Navigate to I'm Following");
		ui.gotoImFollowing();

		log.info("INFO: Verify the status update is displayed in the I'm Following view / All filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(statusMessage),
						 "ERROR: Status update is not displayed in the I'm Following view / All filter");

		log.info("INFO: Verify that '...Show More' link is displayed");
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(suLine1), HomepageUIConstants.ShowMoreSUContent);

		log.info("INFO: Filter by Status Updates");
		ui.filterBy(HomepageUIConstants.FilterSU);

		//Clicking the 'Show more' link to make the test case more robust by ensuring the news story has NOT been pushed off the page
		ui.clickIfVisible(HomepageUIConstants.ShowMore);

		log.info("INFO: Verify the status update is displayed in the I'm Following / Status Updates filter");
		Assert.assertTrue(ui.fluentWaitTextPresent(statusMessage),
						 "ERROR: Status update is not displayed in the I'm Following / Status Updates filter");

		log.info("INFO: Verify that '...Show More' link is displayed");
		ui.moveToClick(HomepageUI.getStatusUpdateMesage(suLine1), HomepageUIConstants.ShowMoreSUContent);

		ui.endTest();
		
	}

}
