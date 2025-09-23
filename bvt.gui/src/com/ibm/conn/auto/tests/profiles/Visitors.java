package com.ibm.conn.auto.tests.profiles;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ProfilesUI;

import junit.framework.Assert;

public class Visitors extends SetUpMethods2 {

	private static Logger log = LoggerFactory.getLogger(Visitors.class);

	private ProfilesUI ui;
	private TestConfigCustom cfg;
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
	}
	
	/**
	*<ul>
	*<li><B>Info:</B> Switch to a visiting org</li>
	*<li><B>Step:</B> Select visiting org from dropdown in the top left corner</li>
	*<li><B>Verify:</B> User is switched to the new org</li>
	*</ul>
	*/
	@Test(groups = { "visitor" })
	public void goToVisitingOrg() {
		long threadId = Thread.currentThread().getId();
		User testUser = cfg.getUserAllocator().getGroupUser("orgA_visitor", threadId);
		DefectLogger logger=dlog.get(threadId);
		
		ui.startTest();
		
		//Load the component and login
		logger.strongStep("Load Profiles and login: " +testUser.getDisplayName());
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);
		
		//Get name of visiting org from user file
		String visitingOrg = testUser.getAttribute("visiting_org");
		
		log.info("Visiting Org to select: " + visitingOrg);
		logger.strongStep("Click on visiting Org from dropdown");
		ui.selectVisitingOrg(visitingOrg);
		
		logger.strongStep("Switch to visiting org window.");
		log.info("Switch to visiting org window.");
		//Title of new window is same as current window so not using title to switch
		boolean switched = ui.switchToNextTab();
		Assert.assertTrue("New window did not open.", switched);
		
		logger.weakStep("Verify visiting Org shows up in the search scope dropdown");
		String scope = driver.getSingleElement(BaseUIConstants.GlobalSearchBarDropdownSelection).getText();
		Assert.assertTrue(String.format("Search scope contains '%s' expecting '%s'", scope, visitingOrg), 
				scope.contains(visitingOrg));
	}
}
