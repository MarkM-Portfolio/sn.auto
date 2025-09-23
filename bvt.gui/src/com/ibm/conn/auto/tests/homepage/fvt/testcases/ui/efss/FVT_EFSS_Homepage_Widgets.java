package com.ibm.conn.auto.tests.homepage.fvt.testcases.ui.efss;

/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2010, 2014                                    */
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
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;

/**
 * [EFSS UI Automation - Changes to the UI for EFSS user] FVT Automation for Story 139173
 * https://swgjazz.ibm.com:8001/jazz/resource/itemName/com.ibm.team.workitem.WorkItem/139173
 * @author Patrick Doherty
 */
public class FVT_EFSS_Homepage_Widgets extends SetUpMethods2{
	
	private static Logger log = LoggerFactory.getLogger(FVT_EFSS_Homepage_Widgets.class);
	
	private HomepageUI ui;
	private TestConfigCustom cfg;	
	private User testUser1;

	@BeforeClass(alwaysRun=true)
	public void setUpClass() throws Exception {
	
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
		testUser1 = cfg.getUserAllocator().getUser(this);
		
	}

	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
	
		ui = HomepageUI.getGui(cfg.getProductName(),driver);
		
	}

	/**
	* efss_homepage_widgets() 
	*<ul>
	*<li><B>Info: It can be assumed that each testcase starts with login and ends with logout and the browser being closed</B></li>
	*<li><B>Step: testUser1 login to SC environment as an EFSS user (initially this means any user in an EFSS Org)</B></li>
	*<li><B>Step: testUser1 navigate to Homepage / Updates</B></li>
	*<li><B>Step: testUser1 check the widget(s) displayed in the right hand column</B></li>
	*<li><B>Verify: Verify that testUser1 has access to the Recommendations widget in the page</B></li>
	*<li><B>Verify: Verify that the Meetings widget is limited to the "Join" option only</B></li>
	*<li><a HREF="Notes://CAMDB01/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/2FF1CF566FE24BB785257D7800509E9C">TTT - EFSS - 00200 - HOMEPAGE WIDGETS - EFSS USER DOES NOT GET EVENTS WIDGET AND GETS LIMITED MEETINGS WIDGET</a></li>
	*</ul>
	*/
	@Test(groups = {"fvtefss"})
	public void efss_homepage_widgets(){
		
		ui.startTest();

		log.info("INFO: Logging in with " + testUser1.getDisplayName() + " to verify widgets are correct on Homepage");
		ui.loadComponent(Data.getData().ComponentHomepage);
		ui.login(testUser1);
		
		log.info("INFO: Navigate to Im Following");
		ui.gotoImFollowing();

		log.info("INFO: Verify the 'Recommendations' widget is available for an EFSS user");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.recommendationsWidet),
				"ERROR: 'Recommendations' widget is NOT available for an EFSS user");

		log.info("INFO: Verify the 'Recommendations' widget content is available for an EFSS user");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.recommendationsWidgetContent),
				"ERROR: 'Recommendations' widget content is NOT available for an EFSS user");

		log.info("INFO: Verify the 'Meetings' widget is available for an EFSS user");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.meetingsWidget),
				"ERROR: 'Meetings' widget is NOT available for an EFSS user");
		
		ui.switchToFrame(HomepageUIConstants.meetingsWidgetIframe, HomepageUIConstants.joinMeetingBtn);
		
		log.info("INFO: Verify the 'Meetings' widget's 'Join' button is available for an EFSS user");
		Assert.assertTrue(ui.fluentWaitPresent(HomepageUIConstants.joinMeetingBtn),
				"ERROR: 'Meetings' widget's 'Join' button is NOT available for an EFSS user");
		
		//The "Host" button seems to be present on the page.  It is sufficient to verify that it is NOT visible
		log.info("INFO: Verify the 'Meetings' widget's 'Host' button is NOT available for an EFSS user");
		Assert.assertTrue(!driver.getSingleElement(HomepageUIConstants.hostMeetingBtn).isVisible(),
				"ERROR: 'Meetings' widget's 'Host' button is available for an EFSS user");

		ui.endTest();
		
	}

}
