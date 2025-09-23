package com.hcl.lconn.automation.framework.config;

import java.lang.reflect.Method;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.xml.XmlTest;

import com.hcl.lconn.automation.framework.utils.TestAPIConfigCustom;
import com.ibm.atmn.waffle.base.BaseSetup;
import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.atmn.waffle.utils.Utils;

public class SetupMethodsAPI extends BaseSetup {
	
	private static Logger log = LoggerFactory.getLogger(SetupMethodsAPI.class);
	
	@BeforeMethod(alwaysRun=true)
	@Override
	public void beforeMethod(ITestContext context, Method method) {
		
		LogManager.startTestLogging(context.getSuite().getName()+"-"+context.getName()+"-"+method.getName());
		super.beforeMethod(context, method);
		Utils.initialValue();
		TestAPIConfigCustom.load(context, testConfig);
	}
	
	@BeforeClass(alwaysRun=true)
	@Override
	public void beforeClass(ITestContext context) {

		super.beforeClass(context);
		TestAPIConfigCustom.load(context, testConfig);
	}
	
	@AfterClass(alwaysRun=true)
	@Override
	public void afterClass(XmlTest test) {

		super.afterClass(test);
		TestAPIConfigCustom.getInstance().getUserAllocator().checkInAllUsersWithToken(this);
	}
	
	@AfterSuite(alwaysRun = true)
	public void afterSuite(XmlTest test, ITestContext context) throws Exception {
		super.afterSuite(test);
	}
	
	@AfterMethod(alwaysRun=true)
	public void afterMethod(ITestResult result, Method method, ITestContext context) {
		TestAPIConfigCustom.getInstance().getUserAllocator().checkInAllUsers();
		LogManager.stopTestLogging();
	}
	
		
	
	
	
	public String startTest() {
		String testName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
		log.info("INFO: ********** Beginning of test " + testName + " at " + new Date() + " **********");
		return testName;
	}
	
	public void endTest() {
		String testName = new Throwable().fillInStackTrace().getStackTrace()[1].getMethodName();
		log.info("INFO: ********** End of test " + testName + " at " + new Date() + " **********");
	}
	
	
	
}
