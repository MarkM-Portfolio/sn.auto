package com.ibm.conn.auto.tests.webeditors.officeonline;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ibm.conn.auto.tests.webeditors.O365BaseTest;
import com.ibm.conn.auto.util.display.Files_Display_Menu;
import com.ibm.conn.auto.webui.OfficeOnlineUI.FileSet;
import com.ibm.lconn.automation.framework.services.files.nodes.FileEntry;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;

public final class O365WopiTest extends O365BaseTest {

	@Override
	protected FileSet getFileSet() {
		return FileSet.BVT;
	} 
  
  
    /**
     * <ul>
     * <li><B>Office Online BVT Test - fileNameAsTitleTest
     * <li></li>
     * <li><B>Step:</B>Login on Connections</li>
     * <li><B>Step:</B>Open Files App on Display mode </li>
     * <li><B>Loop:</B>For each test file, do:</li>
     * <li><B>Step:</B>Click the Edit in Office Online button, so the page opens in another tab</li>
     * <li><B>Assert:</B>Verify if the name of the file shows up as page title</li>
     * </ul>*/
    @Test (groups = {"WE_BVT", "OO_BVT"}, invocationCount = 1) 
    public void fileNameAsTitleTest() throws Exception{
  
        officeOnlineUI.fluentWaitTextPresent("My Files");
        Files_Display_Menu.DETAILS.select(officeOnlineUI);
        
        log.info("INFO: Looping through the files");   
		for (FileEntry fileEntry: officeOnlineUI.testFiles){
			String fileName = fileEntry.getTitle();
           
            log.info("INFO: Clicking in "+fileName+"");       
            officeOnlineUI.clickEditInOfficeButton(fileName);
                  
            String connectionsPageHandler = officeOnlineUI.switchToChildTab();
            
            //Perform Assert #1- Verify the Tab Name = File Name being opened
            Assert.assertEquals(fileName, driver.getTitle());
            log.info("ASSERT PASSED: Tab Title is the SAME as the file opened in Connections");
            
            officeOnlineUI.switchBackToParentTab(connectionsPageHandler);
            log.info("INFO: navigating back to Connections Files UI");
  		    driver.navigate().to(getIcComponentUrl());
        }
    }

    /**
     * <ul>
     * <li><B>Office Online BVT Test - wopiBuildTest
     * <li></li>
     * <li><B>Step:</B>Login on Connections</li>
     * <li><B>Step:</B>Open a given file in WOPI directly</li>
     * <li><B>Assert:</B>Verify that the WopiSrc is defined correctly, matching the Server and the FileId from Connections</li>
     * </ul>*/
    @Test(groups = {"WE_BVT", "OO_BVT"}, invocationCount = 1)
    public void wopiBuildTest() throws Exception{
        String fileID = officeOnlineUI.getFileIDFromFileEntry(officeOnlineUI.testFiles.get(0)); // i don't care which file it is, as long as I can edit it in Office Online
    
        driver.navigate().to(getConfiguredBrowserURL() + BVT_WOPI_WORD + fileID); // Change Data to  include Data.getData().ComponentWopi
        
        verifyWOPISrc();
    }

  
    /**
     * <ul>
     * <li><B>Office Online BVT Test - confirmWOPISourceCorrectOnFormTest
     * <li></li>
     * <li><B>Step:</B>Login on Connections</li>
     * <li><B>Step:</B>Open Files App on Display mode </li>
     * <li><B>Loop:</B>For each test file, do:</li>
     * <li><B>Step:</B>Click the Edit in Office Online button, so the page opens in another tab</li>
     * <li><B>Assert:</B>Verify that the WopiSrc is defined correctly, matching the Server and the FileId from Connections</li>
     * </ul>*/
    @Test (groups = {"WE_BVT", "OO_BVT"}, invocationCount = 1) 
    public void confirmWOPISourceCorrectOnFormTest() throws Exception{
      officeOnlineUI.fluentWaitTextPresent("My Files");
      Files_Display_Menu.DETAILS.select(officeOnlineUI);
      
      log.info("INFO: Looping through the files");   
		for (FileEntry fileEntry: officeOnlineUI.testFiles){
			String fileName = fileEntry.getTitle();
		  
          log.info("INFO: Clicking in "+fileName+"");       
          officeOnlineUI.clickEditInOfficeButton(fileName);
          
          String connectionsPageHandler = officeOnlineUI.switchToChildTab();
          
          verifyWOPISrc();
          
          officeOnlineUI.switchBackToParentTab(connectionsPageHandler);
          log.info("INFO: navigating back to Connections Files UI");
		  driver.navigate().to(getIcComponentUrl());
      }
    }
  
    //Perform Assert #3 - Verify that the <Form/> element has a valid WOPI src attribute to the Connections server with the file ID of the file being opened
    private void verifyWOPISrc(){
    
        Assert.assertEquals(officeOnlineUI.getFileIDFromWopiSrc(officeOnlineUI.getWOPISrc()), officeOnlineUI.getFileIDFromWopiUrl());
        log.info("ASSERT PASSED: Files ID from WopiScr and in the URL are the SAME");
        
        Assert.assertEquals(officeOnlineUI.getServerNameFromURL(officeOnlineUI.getWOPISrc()), officeOnlineUI.getServerNameFromURL(driver.getCurrentUrl()));
        log.info("ASSERT PASSED: Server Name from WopiScr and in the URL are the SAME");
    } 
  
}
