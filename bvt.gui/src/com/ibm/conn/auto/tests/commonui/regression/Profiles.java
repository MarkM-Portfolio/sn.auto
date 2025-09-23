package com.ibm.conn.auto.tests.commonui.regression;

import com.ibm.conn.auto.webui.constants.ProfilesUIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ProfilesUI;
import com.ibm.conn.auto.webui.cloud.ProfilesUICloud;

public class Profiles extends SetUpMethods2 {
	
	private static Logger log = LoggerFactory.getLogger(Profiles.class);
	private ProfilesUI ui;
	private TestConfigCustom cfg;		
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		
		//initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ProfilesUI.getGui(cfg.getProductName(), driver);
		
	}
	
	/**
	* createNsearchTags()
	*<ul>
	*<li><B>Info::</B> Profile Create and Search Tags</li>
	*<li><B>Step:</B> Open People -> My Profile page</li>
	*<li><B>Step:</B> Enter new tag and click on enter key</li>
	*<li><B>Verify:</B> The new tag is added to the widget</li>
	*<li><B>Step:</B> Enter new tag and click the plus sign</li>
	*<li><B>Verify:</B> The new tag is added to the widget</li>
	*<li><B>Step:</B> Click on the first tag from the Cloud view</li>
	*<li><B>Verify:</B> The "Search - Profiles" page is opened</li>
	*<li><B>Verify:</B> There is at least 1 matching result is shown</li>
	*<li><B>Step:</B> Type a related tag in the search box and click on Search button</li>
	*<li><B>Verify: </B>Verify search is performed and returned correctly</li>
	 *<li><a HREF="Notes://Parallan/85257863004CBF81/A3B1F5A7FAF7FB158525703C006F870C/B568243C8AE5645C85257F38005A7B2D">TTT - Common UI - Cross Application Regression/Tags on People/Profiles</a></li>
	 *</ul>
	*/
	@Test (groups = {"regression", "regressioncloud"} )
	public void createNsearchTags() throws Exception {
		
		//Unique number
		String uniqueId = Helper.genDateBasedRandVal();
		String tagName = Data.getData().profileTag + uniqueId;
		
		//Get user
		User testUser = cfg.getUserAllocator().getUser();
		
		//Start test
		ui.startTest();

		//Load the component and login as below user
		ui.loadComponent(Data.getData().ComponentProfiles);
		ui.login(testUser);

		//Enter new tag and click Enter
		log.info("INFO: Enter new tag and click Enter");
		ui.addProfileTagUsingKeyboard(Data.getData().commonTag + uniqueId);
		
		//Verify tag is added to the widget
		log.info("INFO: Verify the new tag is added to the widget");
		Assert.assertTrue(ui.fluentWaitTextPresent(Data.getData().commonTag + uniqueId), 
				"ERROR: " + Data.getData().commonTag + uniqueId + " tag is not added to Tags widget");
		
		//Enter new tag and click the plus sign
		log.info("INFO: Enter new tag and click the plus sign");
		ui.addProfileTag(tagName);
		
		//Verify tag is added to the widget
		log.info("INFO: Verify the new tag is added to the widget");
		Assert.assertTrue(ui.fluentWaitTextPresent(tagName), 
				"ERROR: " + tagName + " tag is not added to Tags widget");
		
		//Click on the first tag from the Cloud view
		log.info("INFO: Click on the first tag from the Cloud view");
		ui.clickLink(ProfilesUICloud.FirstProfileTag);
		
		//Verify Search Profile page is opened
		log.info("INFO: Verify Search Profile page is opened");
		Assert.assertTrue(driver.getTitle().contains(Data.getData().searchProfileTitle),
				"ERROR:- Search Profile page is not displayed");
		
		//Verify at least one matching profile user is displayed in search results
		log.info("INFO: Verify " + testUser.getDisplayName() +" is displayed in search results");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser.getDisplayName()),
				"ERROR:- " + testUser.getDisplayName() +" is not displayed in search results");

		//Locate the Search Related Tags typeahead
		log.info("Verify that Search Related Tags typeahead exists");	
		ui.fluentWaitPresent(ProfilesUIConstants.RelatedTagsSearchTypeAhead);
		
		//Enter content of related tag - 'tagName'
		log.info("Enter related tag - " + tagName);
		ui.typeTextWithDelay(ProfilesUIConstants.RelatedTagsSearchTypeAhead, tagName);
						
		//Click on Search button
		log.info("INFO: Click on Search button");		
		ui.clickLink(ProfilesUIConstants.RelatedTagsSearchButton);
		
		//Verify at least one matching profile user is displayed in search results
		log.info("INFO: Verify " + testUser.getDisplayName() +" is displayed in search results");
		Assert.assertTrue(ui.fluentWaitTextPresent(testUser.getDisplayName()),
				"ERROR:- " + testUser.getDisplayName() +" is not displayed in search results");
		
		ui.endTest();		
	}

}
