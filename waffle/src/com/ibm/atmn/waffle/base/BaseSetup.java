package com.ibm.atmn.waffle.base;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.xml.XmlTest;

import com.browserstack.local.Local;

import com.ibm.atmn.waffle.core.Executor;
import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.core.TestManager;
import com.ibm.atmn.waffle.utils.FileIOHandler;

public class BaseSetup {
	private static final Logger log = LoggerFactory.getLogger(BaseSetup.class);
	private static String timeStamp;
	protected String bsValue;
	protected Local bsLocal;
	protected String bsKey;
	protected HashMap<String, String> bsLocalArgs ;
	protected Executor exec;
	protected TestManager manager;
	protected TestConfiguration testConfig;
	public static Properties browserStackProps;
	public static boolean isBrowserStack = false;

	public BaseSetup() {
		
	}
	public static String getTimestamp() {
		return timeStamp;
	}

	@BeforeSuite(alwaysRun=true)
	public void beforeSuite(ITestContext context) throws Exception{
		
		bsValue=context.getSuite().getParameter("server_is_browserstack");
		if(bsValue != null && bsValue.equalsIgnoreCase("TRUE")){
			isBrowserStack = true;
		}
		if(isBrowserStack) {
			//enabling local testing connection
			File ofile = new File(context.getOutputDirectory()+"/../..");
			browserStackProps = FileIOHandler.loadExternalProperties(ofile.getCanonicalPath() + "/test_config/core/browserstack/browserstackconfig.properties");
			browserStackProps.setProperty("browserStackUser", context.getSuite().getParameter("browserstack_username"));
			browserStackProps.setProperty("browserStackKey", context.getSuite().getParameter("browserstack_key"));
			browserStackProps.setProperty("buildid","");
			
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			Date date = new Date();
            timeStamp=formatter.format(date);
			bsKey=context.getSuite().getParameter("browserstack_key");
			bsLocal = new Local();
			bsLocalArgs = new HashMap<String, String>();
			bsLocalArgs.put("key", bsKey);
            bsLocalArgs.put("localIdentifier", timeStamp);

			bsLocalArgs.put("forcelocal", "true");
			if(!bsLocal.isRunning()){
				log.info("Starting Browserstack local connection");
				try {
					bsLocal.start(bsLocalArgs);
				} catch (Exception e)  {
					log.error("browserstack local connection not enabled:"+e.getMessage());
					throw new SkipException("Skipping test due to BrowserStack cannot be started.");		

				}
			}
		}
	}
	
	@BeforeTest(alwaysRun=true)
	public void beforeTest(ITestContext context) {
		
		TestManager manager = TestManager.getTestManager(context);
		manager.addTestActionListener(new BasePCHListener());
	}
	
	@BeforeGroups(alwaysRun=true)
	public void beforeGroups(ITestContext context) {

	}

	@BeforeClass(alwaysRun=true)
	public void beforeClass(ITestContext context) {
		
		//log.debug("beforeClass running for test: " + context.getName());
		this.manager = TestManager.getTestManager(context);
		this.testConfig = manager.getTestConfig();
		this.exec = manager.getExecutor();
	}
	
	@BeforeMethod(alwaysRun=true)
	public void beforeMethod(ITestContext context, Method method) {

	}

	@AfterMethod
	public void afterMethod(ITestResult result, Method method) {
		try {
			if(exec != null) {
				//Prevents popup when closing browser
				//try{this.exec.executeScript("window.onbeforeunload = null;");}catch(Exception e){log.warn(e.getMessage());}
				this.exec.quit();
			}
		} catch(Exception e)  {
			// need to catch exceptions here otherwise tests in the same class will be skipped
			log.error("Error in afterMethod", e);
		}
	}

	@AfterClass
	public void afterClass(XmlTest test) {

	}

	@AfterGroups
	public void afterGroups(XmlTest test) {

	}
	
	@AfterTest
	public void afterTest(XmlTest test) {

	}
	
	@AfterSuite
	public void afterSuite(XmlTest test) throws Exception {
		if (isBrowserStack) {
			//to disable local testing connection
			log.info("Closing Browserstack local connection");
			try {
				bsLocal.stop();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}
	
	@AfterMethod
	public void afterMethod(ITestResult result, Method method, ITestContext context) {
		// TODO Auto-generated method stub
		
	}
}
