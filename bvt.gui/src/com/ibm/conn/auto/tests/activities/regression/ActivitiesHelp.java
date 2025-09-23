package com.ibm.conn.auto.tests.activities.regression;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;

public class ActivitiesHelp extends SetUpMethods2{

	private static Logger log = LoggerFactory.getLogger(ActivitiesHelp.class);
	private ActivitiesUI ui;
	private TestConfigCustom cfg;
	
	String ActivityHelpTitle = "IBM SmartCloud for Social Business";
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() {
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
		
	}
	
	public void checkHelpMenu() {
		log.info("INFO: check Activities help menu");
		
		String helpMenu = "css=a[id='bsscom-helpMenu_btn']";
		String helpItem = "css=td a[id='svcHelp']";

		ui.clickLinkWait( helpMenu);		
		driver.getFirstElement(helpItem).click();
		
		driver.saveScreenshot("menu");
		String url = null;
		
		Set<String> test = driver.getWindowHandles();
		String wHelpWindow = null;
		
		for (String a:test){
			log.info(a.toString());
			driver.switchToWindowByHandle(a.toString());
			
			log.info(driver.getTitle());
			
			if (driver.getTitle().equalsIgnoreCase(ActivityHelpTitle)){
		
				wHelpWindow = a.toString();
				log.info("Got help window " + wHelpWindow);
				
				url = driver.getCurrentUrl();
				log.info("current URL = " + url);
			
				driver.saveScreenshot("ActivitiesHelp");
				break;
			}	
		}			
		      
		Assert.assertTrue(url.contains("cloud.activities.doc"));
		driver.saveScreenshot("help");
	}

	
	/**
	 *<ul>
	 *<li><B>Info: Create a new Activity</B></li>
	 *<li><B>Steps: </B></li>
	 *<li><B>Create a new Activity. Activity Type: (Tags, Random Due Date (past or present), Goal, and a Member(Type: person Role: Owner)</B></li>
	 *<li><B>Verify: Verify that Activity has being created successfully</B> </li>
	 *</ul>
	 *@throws Exception
	 */
	@Test(groups = {"fvtcloud", "regressioncloud"})
	public void CheckActivityHelp() {
		User testUser1 = cfg.getUserAllocator().getUser();
		
		ui.startTest();
		
		// Load the component
		log.info("INFO: Load component and login");
		ui.loadComponent(Data.getData().ComponentActivities);
		ui.login(testUser1);

		//Start an activity
		log.info("INFO: Start an activity");
		
		checkHelpMenu();
	
		ui.endTest();
	}
	
}
