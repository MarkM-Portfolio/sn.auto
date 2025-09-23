/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential */
/*                                                                   */
/* OCO Source Materials */
/*                                                                   */
/* Copyright IBM Corp. 2010 */
/*                                                                   */
/* The source code for this program is not published or otherwise */
/* divested of its trade secrets, irrespective of what has been */
/* deposited with the U.S. Copyright Office. */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.conn.auto.tests.globalsearch;

import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIActivitiesHandler;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.GlobalsearchUI;

public class BVT_Level_2_Global_Search_MT_Boundary extends SetUpMethods2 {


	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_Global_Search_MT_Boundary.class);
	private TestConfigCustom cfg;
	private GlobalsearchUI gui;
	private User testUser_orgA, testUser_orgB;
	private String serverURL_MT_orgA;
	
	
	//private static boolean indexerFailed = false;
	
	@BeforeClass(alwaysRun = true)
	public void beforeClass(ITestContext context)  {

		super.beforeClass(context);
		
		cfg = TestConfigCustom.getInstance();
		gui = GlobalsearchUI.getGui(cfg.getProductName(), driver);

		cfg = TestConfigCustom.getInstance();

		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		testUser_orgB = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgB, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();

	}
	
	/**
	 *<ul>
	 *<li><B>Info: </B>Test that orgA user is not able to search users from orgB </li>
	 *<li><B>Step: </B> Go to Homepage</li>
	 *<li><B>Step: </B> Search for orgB user </li>
	 *<li><B>Verify: </B>Verify that orgA user should not able to search users from orgB</li>
	 *</ul>
	 */

	
	@Test(groups = {"mtlevel2"})
	public void searchOrgBUser() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());
		
		//Load HomePage component and login with orgA user 
		logger.strongStep("Load Activity and login in as: " +testUser_orgA.getDisplayName());
		log.info("INFO: Searching for Activity via UI");
		gui.loadComponent(serverURL_MT_orgA,Data.getData().ComponentHomepage);
		gui.login(testUser_orgA);
		
		//search for user from orgB 
		log.info("INFO: Open common search panel");
		logger.strongStep("Open common search panel and search for orgB user:"+testUser_orgB.getDisplayName());
		gui.clickLinkWait(GlobalsearchUI.OpenSearchPanel);
		gui.fluentWaitPresent(GlobalsearchUI.TextAreaInPanelMT);	
		gui.typeTextWithDelay(GlobalsearchUI.TextAreaInPanelMT, testUser_orgB.getDisplayName());
		gui.clickLink(GlobalsearchUI.SearchButtonInPanel);
			
		//Verify no result found for user from orgB
		log.info("INFO: Verify that No results found message should be displayed");
		logger.weakStep("Validate that No results found message should be displayed");
		Element ele = driver.getFirstElement(GlobalsearchUI.NoResultFound);
		log.info("Message is:"+ele.getText());
		Assert.assertEquals(driver.getFirstElement(GlobalsearchUI.NoResultFound).getText(),
				"No results were found for that search");


		gui.endTest();

		
	}
		
}
