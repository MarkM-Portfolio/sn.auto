package com.ibm.conn.auto.tests.webeditors;

import java.lang.reflect.Method;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.lcapi.APIFileHandler;
import com.ibm.conn.auto.webui.OfficeOnlineUI;
import com.ibm.conn.auto.webui.OfficeOnlineUI.FileSet;

public abstract class O365BaseTest extends ConnectionsBaseTest {

  //UI's from webUI used on the tests
  protected OfficeOnlineUI officeOnlineUI;
  
  protected abstract FileSet getFileSet();

  /**
   * Sets up the UI instances specific to BVT_EditInOfficeOnline and derived classes
   * It also calls a method which creates the test files, setting up the test environment
   */
  @BeforeClass(alwaysRun=true)
  public void beforeClassEiOO(final ITestContext context) {
 
	log.info("INFO: getting the OfficeOnlineUI instance");
    officeOnlineUI = OfficeOnlineUI.getGui( getProductName(), driver );
    
    setupTestFiles();
  }
  
/**
   * setupTestFiles is called during a @BeforeClass event. It should be overridden if no test files need to be uploaded to the Connections test server.
   */
  protected void setupTestFiles() {
	if(getFileSet() == FileSet.NONE)
	  return; // no test files are necessary; skip test file setup
	
    log.info("INFO: Create the files necessary to perform the tests on the server");
	APIFileHandler apiHandler = getApiFileHandler();
	officeOnlineUI.createMultipleTestFiles(apiHandler, getFileSet());
  }
  
  @BeforeMethod(alwaysRun = true)
  public void beforeMethodEiOO(final ITestContext testContext, final Method testMethod) {
    officeOnlineUI.startTest(testMethod, testContext.getName());
    
	log.info("INFO: loading component '" + getComponent() + "' with browser '" + testContext.getCurrentXmlTest().getParameter("browser_start_command") + "'");
    officeOnlineUI.loadComponent(getComponent());
    
    login(officeOnlineUI);
  }
  
  protected String getComponent() {
    return Data.getData().ComponentFiles;
  }
  
  @AfterMethod(alwaysRun = true)
  public void afterMethodEiOO(final ITestContext testContext, final Method testMethod) {
      officeOnlineUI.endTest(testMethod, testContext.getName());
  }

  @AfterClass(alwaysRun = true)
  public void afterClassEiOO(final ITestContext testContext) {
	  deleteTestFiles(testContext);
  }
  
  /**
   * deleteTestFiles is responsible for deleting the test files from the Connections server. It should be overridden if no test files are used.
   * @param testContext supplied by TestNG; check https://examples.javacodegeeks.com/enterprise-java/testng/testng-beforemethod-example/ for details.
   */
  protected void deleteTestFiles(final ITestContext testContext) {
      if(getFileSet() == FileSet.NONE)
	    return; // no test files are necessary; skip test file deletion

  	  log.info("INFO: delete test files using the files API");
  	  APIFileHandler apiHandler = getApiFileHandler();
      officeOnlineUI.deleteAllTestFiles(apiHandler);
      
      if(!driver.isLoaded()) {
	  	  log.info("INFO: loading component '" + getComponent() + "' with browser '" + testContext.getCurrentXmlTest().getParameter("browser_start_command") + "'");
		  officeOnlineUI.loadComponent(getComponent());

		  login(officeOnlineUI);
      }
      
      log.info("INFO: Loading trash component");
      driver.navigate().to( getConfiguredBrowserURL() + OfficeOnlineUI.TrashComponent);
      
      log.info("INFO: Call method dismissFilesFromTrash to delete the files in trash");
      try {
    	  officeOnlineUI.dismissFilesFromTrash();
      } catch(IllegalStateException ex) {
    	  log.info("INFO: the trashcan reported no files deleted, therefore the empty trash button is not visible");  
      }
      
      log.info("INFO: Closing browser");
      exec.quit();
  }
  
}
