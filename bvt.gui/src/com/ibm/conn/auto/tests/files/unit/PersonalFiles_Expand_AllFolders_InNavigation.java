package com.ibm.conn.auto.tests.files.unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class PersonalFiles_Expand_AllFolders_InNavigation extends FilesUnitBaseSetUp{
	private static Logger log = LoggerFactory.getLogger(PersonalFiles_Expand_AllFolders_InNavigation.class);

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
	*<li><B>Step:</B> click All Folders Navigation Button</li>
	*<li><B>Verify:</B> Click on My Folder to see your folder</li>
	**<li><B>Verify:</B> Click on Folders Shared With Me to see your folder</li>
	*</ul>
	*/
	
	@Test(groups = {"unit"})
	public void testExpandAllFoldersInLeftNavigation() throws Exception {
		
		ui.startTest();
		
		 if(ui.isElementPresent(AllFoldersLeftMenu)) {
			 ui.clickLinkWait(AllFoldersLeftMenu);
	          
			//Click on My Folder to see your folder
		      log.info("INFO: Click on My Folder");
		      ui.clickLinkWait(MyFoldersLeftMenu);
		      
		    //Click on Folders Shared With Me to see your folder
		      log.info("INFO: Click Folders Shared With Me");
		      ui.clickLinkWait(FoldersSharedWithMeLeftMenu);
		      
	      }
		
	      ui.endTest();
		
	}
}
