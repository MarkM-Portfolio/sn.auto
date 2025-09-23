package com.ibm.conn.auto.tests.webeditors.officeonline;

import org.apache.commons.io.FilenameUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.ibm.conn.auto.tests.webeditors.O365BaseTest;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.webui.OfficeOnlineUI;
import com.ibm.conn.auto.webui.OfficeOnlineUI.FileSet;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

public final class O365EditFileTest extends O365BaseTest {

	@Override
	protected FileSet getFileSet() {
		return FileSet.BASIC;
	}

	/**
	 * <ul>
	 * <li><B>Info:</B>Edit in Office Online TestCase: verifyButtonExistenceTest</li>
	 * <li></li>
	 * <li><B>Objective:</B>To verify if the new button on the FileViewer shows up for a given set of files, depending on their type, and also if they are not shown for the rest set of files</li>
	 * <li><B>Step:</B>Login on Connections using a random user </li>
	 * <li><B>Step:</B>Open File page (wait so the Files page loads)</li>
	 * <li><B>Step:</B>Change the view type to Display mode</li>
	 * <li><B>Loop:</B>For each test file, do :</li>
	 * <li><B>Step:</B>1. Click the file so it opens on the File Viewer view</li>
	 * <li><B>Verify:</B>2. Check if the file is among the supported type of files for edition in Office Online, and if so:</li>
	 * <li><B>Assert:</B>2.1. The edit button should exist inside the drop down menu 'More Options'</li>
	 * <li><B>Verify:</B>3. If the file is not among the supported types</li>
	 * <li><B>Assert:</B>3.1. The edit button should not exist</li>
	 * <li><B>Step:</B>4. Close the viewer</li>
	 * <li><a HREF="">TTT Link to this test</a></li>
	 * </ul>*/
	@Test(groups = {"WE_atomic_FVT_test","button_existence"})
	public void verifyButtonExistenceTest() throws Exception{

		//Making sure it is on the correct component		
		officeOnlineUI.fluentWaitTextPresent("My Files");
		
		//Changing the view to Details View
		Files_Display_Menu.DETAILS.select(officeOnlineUI);
			
		for (FileEntry fileEntry: officeOnlineUI.testFiles){
			String fileName = fileEntry.getTitle();
				
			log.info("INFO: Performing Button Existence Verification test for "+fileName);
		
			//Click the selected file
			officeOnlineUI.clickLinkWait(officeOnlineUI.getFileNameDetailsView(fileName));
	
			//Checks if the file is among the files supported for Office Online Edit mode
			if (officeOnlineUI.isFileEditable(fileName)){
				//ASSERT: Checks if the button inside the drop down exists
				Assert.assertTrue(officeOnlineUI.doesButtonExist(), "ERROR: Edit in Office Online Button was NOT found");
				log.info("ASSERT PASSED: Edit in Office Online Button found for file "+fileName);
			
			} else { 
				//ASSERT: Not existence of the Edit Button
				Assert.assertTrue(!officeOnlineUI.doesButtonExist(), "ERROR: Edit in Office Online Button was Found, but should not");
				log.info("ASSERT PASSED: Edit in Office Online Button NOT found - Correctly - for file "+fileName);
			}
			//Close the File Viewer using a method of FileViewerUI
            log.info("INFO: navigating back to Connections Files UI");
  		    driver.navigate().to(getIcComponentUrl());
		}
	}
	
	
	/**
	 * <ul>
	 * <li><B>Info:</B>Edit in Office Online TestCase: verifyButtonLabelTest
	 * <li><B>Objective:</B>To verify if the new button on the FileViewer shows the correct label </li>
	 * <li></li>
	 * <li><B>Step:</B>Login on Connections using a random user </li>
	 * <li><B>Step:</B>Open File page (wait so the Files page loads)</li>
	 * <li><B>Step:</B>Change the view type to Display mode</li>
	 * <li><B>Loop:</B>For each test file, do:</li>
	 * <li><B>Verify:</B>1. Check if the file is among the supported type of files for edition in Office Online, and if so:</li>
	 * <li><B>Step:</B>1.1. Click the file so it opens on the File Viewer view</li>
	 * <li><B>Assert:</B>1.2. The label of the edit button should match OfficeOnlineUI.EditInOfficeOnlineButtonLabel</li>
	 * <li><B>Step:</B>1.3. Close the viewer</li>
	 * <li><a HREF="">TTT Link to this test</a></li>
	 * </ul>*/
	@Test(groups = {"WE_atomic_FVT_test","button_label"})
	public void verifyButtonLabelTest() throws Exception{

		//Making sure it is on the correct component		
		officeOnlineUI.fluentWaitTextPresent("My Files");
		//Changing the view to Details View
		Files_Display_Menu.DETAILS.select(officeOnlineUI);
		
		for (FileEntry fileEntry: officeOnlineUI.testFiles){
			String fileName = fileEntry.getTitle();
			
			//Checks if the file is among the files supported for Office Online Edit mode
			if (officeOnlineUI.isFileEditable(fileName)){
				
				log.info("INFO: Performing Button Verification Test for "+fileName);
				
				//Click the selected file
				officeOnlineUI.clickLinkWait(officeOnlineUI.getFileNameDetailsView(fileName));
		
				//ASSERT: The label on the button on the drop down has to match the constant EditInOfficeOnlineButtonLabel, and an error message should be displayed if not 
				Assert.assertEquals(officeOnlineUI.getEditButtonContent(), OfficeOnlineUI.EditInOfficeOnlineButtonLabel,"ERROR: Button Label do not match the desired - Actual Content of Button:"+ officeOnlineUI.getEditButtonContent());
				log.info("ASSERT PASSED: Edit in Office Online Button content is correct: "+officeOnlineUI.getEditButtonContent());
							
				//Close the File Viewer using a method of FileViewerUI
	            log.info("INFO: navigating back to Connections Files UI");
	  		    driver.navigate().to(getIcComponentUrl());
			}	
		}	
	}

	
		
	/**
	 * <ul>
	 * <li><B>Info:</B>Edit in Office Online TestCase: openInOfficeWebAppTest
	 * <li><B>Objective:</B>To verify if the files which have the Edit button open in Office Online</li>
	 * <li></li>
	 * <li><B>Step:</B>Login on Connections using a random user </li>
	 * <li><B>Step:</B>Open File page (wait so the Files page loads)</li>
	 * <li><B>Step:</B>Change the view type to Display mode</li>
	 * <li><B>Loop:</B>For each test file, do:</li>
	 * <li><B>Verify:</B>1. Check if the file is among the supported type of files for edition in Office Online, and if so:</li>
	 * <li><B>Step:</B>1.1. Click the file so it opens on the File Viewer view</li>
	 * <li><B>Step:</B>1.2. Update the ID variables on the UI, fetching the ids from the URL and the Download button</li>
	 * <li><B>Assert:</B>1.3. Verify if the ID from the URL and the Download button match</li>
	 * <li><B>Verify:</B>1.4. Check if the edit button exists, and if so:</li>
	 * <li><B>Step:</B>1.4.1. Click the Edit in Office Online button, so the page opens in another tab</li>
	 * <li><B>Step:</B>1.4.2. Handles the OAuth to grant access, if this is the first file to be opened</li>
	 * <li><B>Step:</B>1.4.3. Wait for the page to load</li>
	 * <li><B>Verify:</B>1.4.4. Check if the file is not among special cases (currently DocX and ODT), and if so:</li>
	 * <li><B>Assert:</B>1.4.4.1. Verify if the name of the file shows up as page title</li>
	 * <li><B>Assert:</B>1.4.4.2. Verify if the ID from the Office Online URL and one from Connections match</li>
	 * <li><B>Verify:</B>1.4.5. Check if the file is one of the special cases (currently DocX and ODT), and if so:</li>
	 * <li><B>Assert:</B>1.4.5.1. Verify if the name of the file do not show up as page title</li>
	 * <li><B>Step:</B>1.5.Close the viewer</li>
	 * <li><a HREF="">TTT Link to this test</a></li>
	 * </ul>*/
	@Test(groups = {"WE_atomic_FVT_test","file_test"})
	public void openInOfficeWebAppTest() throws Exception{
		
		//Making sure it is on the correct component		
		
		officeOnlineUI.fluentWaitTextPresent("My Files");
		//Changing the view to Details View
		Files_Display_Menu.DETAILS.select(officeOnlineUI);

		for (FileEntry fileEntry: officeOnlineUI.testFiles){
			String fileName = fileEntry.getTitle();
			
			if (officeOnlineUI.isFileEditable(fileName)){
				
				log.info("INFO: Performing Opening in Office Web Apps test for "+fileName);
			
				//Click the selected file
				officeOnlineUI.clickLinkWait(officeOnlineUI.getFileNameDetailsView(fileName));
		
				//Checks the fileIDs and updates the FileID variables so an assetion test can be performed
				officeOnlineUI.updateFileIDVariables();
				
				//ASSERT: The ID shown on the FileViewer URL and the Download Link has to match 
				Assert.assertEquals(officeOnlineUI.getCurrentFileIDFromViewerURL(), officeOnlineUI.getCurrentFileIDFromDownloadLink(), "ERROR: File ID between Viewer URL and Download Link do not match");						
				log.info("ASSERT PASSED: First set of File ID's Match \nID from URL: "+officeOnlineUI.getCurrentFileIDFromViewerURL()+"\nID from DWL: "+officeOnlineUI.getCurrentFileIDFromDownloadLink()+"");
							
				// Checks if the button inside the drop down exists and is present
				if (officeOnlineUI.doesButtonExist()){
					
					//Click the Edit Button
					officeOnlineUI.clickLinkWait(OfficeOnlineUI.EditInO365Button);
					
					//Calls a method which changes the page handle to the new tab (which contains the OAuth Screen or the Office Online). It also stores the Connections Handler so it can come back 
					String connectionsPageHandler = officeOnlineUI.switchToChildTab();
					
					//Call a method which handles the OAuth, if it shows 
					officeOnlineUI.oAuthHandler();
					
					//Wait for the title to change to be different than the OAuth page
					officeOnlineUI.fluentWaitTitleChange(OfficeOnlineUI.OAuthPageTitle);
										
					//Wait for the Office Web Apps tab to open. As the elements are iFramed, there is not yet a better way of waiting
					Thread.sleep(10000);
					
					//If the file is not a DOCX or ODT type (which gets different treatment, showing an error message)
					if (!FilenameUtils.getExtension(fileName).equalsIgnoreCase("docx") && !FilenameUtils.getExtension(fileName).equalsIgnoreCase("odt")){
				    
						log.info("INFO: File is NOT among Special Cases: File name is "+fileName+" and title is "+ driver.getTitle());
						
			    		//ASSERT: The file name has to be shown as the title of the page in Office Web Apps 
						Assert.assertEquals(fileName, driver.getTitle());
						log.info("ASSERT PASSED: Tab title is the name of the file opened in Connections");
						
						//ASSERT: The file opened on Office Online has to have the same ID from the one opened in the File Viewer
						Assert.assertEquals(officeOnlineUI.getCurrentFileIDFromViewerURL(), officeOnlineUI.returnsIDFromOfficeWebAppFile(driver.getCurrentUrl()));
						log.info("ASSERT PASSED: Second set of File ID's match: \nCurrent File ID from IBM Connections: "+ officeOnlineUI.getCurrentFileIDFromViewerURL()+"\nCurrent File ID from Office Online  : "+ officeOnlineUI.returnsIDFromOfficeWebAppFile(driver.getCurrentUrl()));
			        
			        } else {
			        	//If the file is a Docx or Odt file, it gets a special treatment
			        	log.info("Filename is Special case: File name is "+fileName+" and page title is "+ driver.getTitle());
			        	
			        	//ASSERT: The file name for the special files is not shown as the page title 
			        	Assert.assertNotEquals(fileName, driver.getTitle());
			        	log.info("ASSERT PASSED: Tab title is NOT the name of the file opened in Connections");
			        }
			        //Close the Office Web Apps tab and change tabs back to Connections using the Connections page handler stored
			        officeOnlineUI.switchBackToParentTab(connectionsPageHandler);
				} 
				//Close the File Viewer using a method of FileViewerUI
	            log.info("INFO: navigating back to Connections Files UI");
	  		    driver.navigate().to(getIcComponentUrl());
			}
		}
	}
	

	/**<ul><li><B>Info:</B>Edit in Office Online TestCase: passingInOAuthOnlyOnceTest
	 * <li><B>Objective:</B>To verify if the OAuth page shows up once, and only once, before opening file in Office Web Apps</li>
	 * <li></li>
	 * <li><B>Step:</B>Login on Connections using a random user </li>
	 * <li><B>Step:</B>Open File page (wait so the Files page loads)</li>
	 * <li><B>Step:</B>Change the view type to Display mode</li>
	 * <li><B>Loop:</B>For each test file, do:</li>
	 * <li><B>Verify:</B>1. Check if the file is among the supported type of files for edition in Office Online, and if so:</li>
	 * <li><B>Step:</B>1.1. Click the file so it opens on the File Viewer view</li>
	 * <li><B>Verify:</B>1.2. Check if the edit button is present, and if so:</li>
	 * <li><B>Step:</B>1.2.1. Click the Edit in Office Online button, so the page opens in another tab</li>
	 * <li><B>Verify:</B>1.2.2. Verify if the OAuth page did not show up yet, and if so:</li>
	 * <li><B>Assert:</B>1.2.2.1. Attest that the OAuth page is showing up 
	 * <li><B>Step:</B>1.2.2.2. Handles the OAuth to grant access, if this is the first file to be opened</li>
	 * <li><B>Step:</B>1.2.2.3. Wait for the page to load</li>
	 * <li><B>Step:</B>1.2.2.4. Close the viewer</li>
	 * <li><B>Assert:</B> Verify if the OAuth page was called only once</li>
	 * <li><a HREF="">TTT Link to this test</a></li> 
	 * </ul>*/
	@Test(groups = {"WE_atomic_FVT_test","oauth"}, invocationCount = 1)
	public void passingInOAuthOnlyOnceTest() throws Exception{
		
		//Making sure it is on the correct component		
		officeOnlineUI.fluentWaitTextPresent("My Files");
		
		//Changing the view to Details View
		Files_Display_Menu.DETAILS.select(officeOnlineUI);
		
		//Restart the oAuth counter to zero
		officeOnlineUI.restartOAuthCounter();			
			
		for (FileEntry fileEntry: officeOnlineUI.testFiles){
			String fileName = fileEntry.getTitle();
			
			if (officeOnlineUI.isFileEditable(fileName)){
				
				log.info("INFO: Performing OAuth test for "+fileName);
			
				//Click the selected file
				officeOnlineUI.clickLinkWait(officeOnlineUI.getFileNameDetailsView(fileName));
		
				// Checks if the button inside the drop down exists and is present
				if (officeOnlineUI.doesButtonExist()){
					//Click the Edit Button
					officeOnlineUI.clickLinkWait(OfficeOnlineUI.EditInO365Button);
					
					//Calls a method which changes the page handle to the new tab (which contains the OAuth Screen or the Office Online). It also stores the Connections Handler so it can come back 
					String connectionsPageHandler = officeOnlineUI.switchToChildTab();
					
					//If the OAuth counter is null, it checks if the page opened is the OAuth
					if(officeOnlineUI.getOAuthCounter()==0){
						
						officeOnlineUI.fluentWaitTextPresent(OfficeOnlineUI.ConnectionsToOfficeOnlineBridgeText);
						//ASSERT: The OAuth page has to be shown only in the first time an editable file is opened in Connections
						Assert.assertTrue(driver.getTitle().equalsIgnoreCase(OfficeOnlineUI.OAuthPageTitle) , "ERROR: Did not pass OAuth while opening the first editable file in Connections");						
						log.info("ASSERT PASSED: Passing on OAuth if Office File was not opened before");
					}
					
					//Call a method which handles the OAuth, if it shows 
					officeOnlineUI.oAuthHandler();
					
					//Wait for the title to change to be different than the OAuth page
					officeOnlineUI.fluentWaitTitleChange(OfficeOnlineUI.OAuthPageTitle);
					
					//Wait for the Office Web Apps tab to open. As the elements are iFramed, there is not yet a better way of waiting
					Thread.sleep(10000);
					
					log.info("INFO: File is opened Office Web Apps, and page title is: "+driver.getTitle());
					
					//Close the Office Web Apps tab and change tabs back to Connections using the Connections page handler stored
			        officeOnlineUI.switchBackToParentTab(connectionsPageHandler);
				}
				//Close the File Viewer using a method of FileViewerUI
	            log.info("INFO: navigating back to Connections Files UI");
	  		    driver.navigate().to(getIcComponentUrl());
			}
		}
		//ASSERT: the OAuth screen has to be shown once and only once
		Assert.assertEquals(officeOnlineUI.getOAuthCounter(), 1, "ERROR: Passed in OAuth more than once");
		log.info("ASSERT PASSED: OAuth happened only once");
		
	}

	
	
	/**<ul><li><B>Info:</B>Edit in Office Online TestCase: verifyOfficeOnlineComplete
	 * <li><B>Objective:</B>To verify, in one shot, every test regarding Office Online</li>
	 * <li></li>
	 * <li><B>Step:</B>Login on Connections using a random user </li>
	 * <li><B>Step:</B>Open File page (wait so the Files page loads)</li>
	 * <li><B>Step:</B>Change the view type to Display mode</li>
	 * <li><B>Loop:</B>For each test file, do:</li>
	 * <li><B>Step:</B>1. Click the file so it opens on the File Viewer view</li>
	 * <li><B>Step:</B>2. Update the ID variables on the UI, fetching the IDs from the URL and the Download button</li>
	 * <li><B>Assert:</B>3. Verify if the ID from the URL and the Download button match</li>
	 * <li><B>Verify:</B>4. Check if the file is among the supported type of files for edition in Office Online, and if so:</li>
	 * <li><B>Verify:</B>4.1 Check if the edit button exists, and if so:</li>
	 * <li><B>Assert:</B>4.1.1. The edit button should exist inside the drop down menu 'More Options'</li>
	 * <li><B>Assert:</B>4.1.2. The label of the edit button should match OfficeOnlineUI.EditInOfficeOnlineButtonLabel</li>
	 * <li><B>Step:</B>4.1.3. Click the Edit in Office Online button, so the page opens in another tab</li>
	 * <li><B>Verify:</B>4.1.4. Verify if the OAuth page did not show up yet, and if so:</li>
	 * <li><B>Assert:</B>4.1.4.1. Attest that the OAuth page is showing up 
	 * <li><B>Step:</B>4.1.5. Handles the OAuth to grant access, if this is the first file to be opened</li>
	 * <li><B>Step:</B>4.1.6. Wait for the page to load</li>
	 * <li><B>Verify:</B>4.1.7. Check if the file is not among special cases (currently DocX and ODT), and if so:</li>
	 * <li><B>Assert:</B>4.1.7.1. Verify if the name of the file shows up as page title</li>
	 * <li><B>Assert:</B>4.1.7.2. Verify if the ID from the Office Online URL and one from Connections match</li>
	 * <li><B>Verify:</B>4.1.8. Check if the file is one of the special cases (currently DocX and ODT), and if so:</li>
	 * <li><B>Assert:</B>4.1.8.1. Verify if the name of the file do not show up as page title</li>
	 * <li><B>Verify:</B>5. If the file is not among the supported types</li>
	 * <li><B>Assert:</B>5.1. The edit button should not exist</li>
	 * <li><B>Step:</B>6. Close the viewer</li>
	 * <li><B>Assert:</B> Verify if the OAuth page was called only once</li> 
	 * <li><a HREF="">TTT Link to this test</a></li>
	 * </ul>*/
	@Test(groups = {"WE_complete_FVT_test"})
	public void verifyOfficeOnlineComplete() throws Exception{

		//Making sure it is on the correct component		
		officeOnlineUI.fluentWaitTextPresent("My Files");
		
		//Changing the view to Details View
		Files_Display_Menu.DETAILS.select(officeOnlineUI);
		
		//Restart the oAuth counter to zero
		officeOnlineUI.restartOAuthCounter();			
			
		for (FileEntry fileEntry: officeOnlineUI.testFiles){
			String fileName = fileEntry.getTitle();
				
			log.info("Performing tests for "+fileName);
		
			//Click the selected file
			officeOnlineUI.clickLinkWait(officeOnlineUI.getFileNameDetailsView(fileName));
	
			//Checks the fileIDs and updates the FileID variables so an assetion test can be performed
			officeOnlineUI.updateFileIDVariables();
			
			//ASSERT:: The ID shown on the FileViewer URL and the Download Link has to match 
			Assert.assertEquals(officeOnlineUI.getCurrentFileIDFromViewerURL(), officeOnlineUI.getCurrentFileIDFromDownloadLink(), "ERROR: File ID between Viewer URL and Download Link do not match");						
			log.info("ASSERT PASSED: First set of File ID's Match \nID from URL: "+officeOnlineUI.getCurrentFileIDFromViewerURL()+"\nID from DWL: "+officeOnlineUI.getCurrentFileIDFromDownloadLink()+"");
						
			if (officeOnlineUI.isFileEditable(fileName)){
				// Checks if the button inside the drop down exists
				if (officeOnlineUI.doesButtonExist()){
					officeOnlineUI.clickLinkWait(OfficeOnlineUI.dropDownButtonNextToUpload);

					//ASSERT: Existence of the Edit Button
					Assert.assertTrue(officeOnlineUI.isElementPresent(OfficeOnlineUI.EditInO365Button), "ERROR: Edit in Office Online Button was NOT found");
					log.info("ASSERT PASSED: Edit in Office Online Button found for file "+fileName);
					
					//ASSERT: The label on the button on the drop down has to match the constant EditInOfficeOnlineButtonLabel, and an error message should be displayed if not 
					Assert.assertEquals(officeOnlineUI.getEditButtonContent(), OfficeOnlineUI.EditInOfficeOnlineButtonLabel,"ERROR: Button Label do not match the desired - Actual Content of Button:"+ officeOnlineUI.getEditButtonContent());
					log.info("ASSERT PASSED: Edit in Office Online Button content is correct: "+officeOnlineUI.getEditButtonContent());
					
					//Click the Edit Button
					officeOnlineUI.clickLinkWait(OfficeOnlineUI.EditInO365Button);
					
					//Calls a method which changes the page handle to the new tab (which contains the OAuth Screen or the Office Online). It also stores the Connections Handler so it can come back 
					String connectionsPageHandler = officeOnlineUI.switchToChildTab();
					
					//If the OAuth counter is null, it checks if the page opened is the OAuth
					if(officeOnlineUI.getOAuthCounter()==0){
						//ASSERT: The OAuth page has to be shown only in the first time an editable file is opened in Connections
						Assert.assertTrue(driver.getTitle().equalsIgnoreCase(OfficeOnlineUI.OAuthPageTitle) , "ERROR: Did not pass OAuth while opening the first editable file in Connections");						
						log.info("ASSERT PASSED: Passing on OAuth if Office File was not opened before");
					}
					
					//Call a method which handles the OAuth, if it shows 
					officeOnlineUI.oAuthHandler();
					
					//Wait for the title to change to be different than the OAuth page
					officeOnlineUI.fluentWaitTitleChange(OfficeOnlineUI.OAuthPageTitle);
					
					//Wait for the Office Web Apps tab to open. As the elements are iFramed, there is not yet a better way of waiting
					Thread.sleep(10000);
					
					//If the file is not a DOCX or ODT type (which gets different treatment, showing an error message)
			        if (!FilenameUtils.getExtension(fileName).equalsIgnoreCase("docx") && !FilenameUtils.getExtension(fileName).equalsIgnoreCase("odt")){
			    		log.info("Filename is NOT among Special Cases: "+fileName+" and title is "+ driver.getTitle());
						//ASSERT: The file name has to be shown as the title of the page in Office Web Apps 
						Assert.assertEquals(fileName, driver.getTitle());
						log.info("ASSERT PASSED: Tab title is the name of the file opened in Connections");
						
						//ASSERT: The file opened on Office Online has to have the same ID from the one opened in the File Viewer
						Assert.assertEquals(officeOnlineUI.getCurrentFileIDFromViewerURL(), officeOnlineUI.returnsIDFromOfficeWebAppFile(driver.getCurrentUrl()));
						log.info("ASSERT PASSED: Second set of File ID's match: \nCurrent File ID from IBM Connections: "+ officeOnlineUI.getCurrentFileIDFromViewerURL()+"\nCurrent File ID from Office Online  : "+ officeOnlineUI.returnsIDFromOfficeWebAppFile(driver.getCurrentUrl()));
			        
			        } else {
			        	//If the file is a Docx or Odt file, it gets a special treatment
			        	log.info("Filename is Special case: "+fileName+" and page title is "+ driver.getTitle());
			        	
			        	//ASSERT: The file name for the special files is not shown as the page title 
			        	Assert.assertNotEquals(fileName, driver.getTitle());
			        	log.info("ASSERT PASSED: Tab title is NOT the name of the file opened in Connections");
			        }
			        //Close the Office Web Apps tab and change tabs back to Connections using the Connections page handler stored
			        officeOnlineUI.switchBackToParentTab(connectionsPageHandler);
				} 
			
			}else{
				//ASSERT: Not existence of the Edit Button
				Assert.assertTrue(!officeOnlineUI.doesButtonExist(), "ERROR: Edit in Office Online Button was Found, but should not");
				log.info("ASSERT PASSED: Edit in Office Online Button NOT found - Correctly - for file "+fileName);
			}
			//Close the File Viewer using a method of FileViewerUI
            log.info("INFO: navigating back to Connections Files UI");
  		    driver.navigate().to(getIcComponentUrl());
		}
		//ASSERT: the OAuth screen has to be shown once and only once
		Assert.assertEquals(officeOnlineUI.getOAuthCounter(), 1, "ERROR: Passed in OAuth more than once");
		log.info("ASSERT PASSED: OAuth happened only once");
		
	}

}