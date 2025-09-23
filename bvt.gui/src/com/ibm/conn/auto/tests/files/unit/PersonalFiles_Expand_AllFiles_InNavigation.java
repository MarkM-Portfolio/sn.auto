package com.ibm.conn.auto.tests.files.unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PersonalFiles_Expand_AllFiles_InNavigation extends FilesUnitBaseSetUp{
	private static Logger log = LoggerFactory.getLogger(PersonalFiles_Expand_AllFiles_InNavigation.class);

	@BeforeClass(alwaysRun=true)
	public void SetUpClass(){
		personalFilesSetUpClass();
	}
	
	
	@BeforeMethod(alwaysRun=true)
	public void setUp() throws Exception {
		personalFilesSetUp();
	}
	
	/**
	*<ul>SAMPLE TEST TO DEMONSTRATE SUCCESS 
	*<li><B>Step:</B> click Sync Navigation Button</li>
	*<li><B>Verify:</B> Open the My Files view </li>
	**<li><B>Verify:</B> Open the Shared With Me view </li>
	**<li><B>Verify:</B> Open the Community Files view </li>
	**<li><B>Verify:</B> Open the Public Files view </li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testExpandAllFilesInLeftNavigation() throws Exception {
		
		ui.startTest();
		
		 if(ui.isElementPresent(AllFilesView)) {
	          ui.clickLinkWait(AllFilesView);
	          
	          //Open the My Files view
		      log.info("INFO: Open the My Files view");
		      ui.clickLinkWait(openMyFilesView);
		      
		      //Open the Shared With Me view
		      log.info("INFO: Open the Shared With Me view");
		      ui.clickLinkWait(openSharedWithMeView);
		      
		      if(ui.isElementPresent(openCommunityFilesView)) {
		    	 //Open the Community Files view
			     log.info("INFO: Open the Community Files view");
			     ui.clickLinkWait(openCommunityFilesView);
		      }

		      if(ui.isElementPresent(openPublicFilesView)) {
		    	//Open the Public Files view
			      log.info("INFO: Open the Public Files view");
			      ui.clickLinkWait(openPublicFilesView);
		      }
	      }
		
	      ui.endTest();
		
	}
}
