package com.ibm.conn.auto.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.atmn.waffle.utils.Utils;
import com.ibm.conn.auto.util.BrowserActions;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.util.OrgConfig;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.util.Video;
import com.ibm.conn.auto.util.browserstack.GetBrowserStackArtifacts;

public class SetUpMethods2 extends BaseSetup {

	protected RCLocationExecutor driver;
	protected List<OrgConfig> orgs = new ArrayList<OrgConfig>();
	protected BrowserActions browser;
	protected ConcurrentHashMap<Long, DefectLogger> dlog = new ConcurrentHashMap<Long, DefectLogger>();
	protected ThreadLocal<String> testName = new ThreadLocal<>();
	
	public static volatile ConcurrentHashMap<Long, ArrayList<Video>> videoMap = new ConcurrentHashMap<Long, ArrayList<Video>>();
	
	
	private static Logger log = LoggerFactory.getLogger(SetUpMethods2.class);
	
	
	@BeforeMethod(alwaysRun=true)
	@Override
	public void beforeMethod(ITestContext context, Method method) {
		
		// Test Logging starts for other than data driven test
		String[] groups = context.getIncludedGroups();
		if (Arrays.asList(groups).contains("datadriven")) {
			context.setAttribute("testName", method.getName());
			LogManager.startTestLogging(context.getSuite().getName() + "-" + context.getName() + "-"+ context.getAttribute("testName").toString());
		} else {
			LogManager.startTestLogging(context.getSuite().getName() + "-" + context.getName() + "-" + method.getName());
		}
		super.beforeMethod(context, method);
		Thread.currentThread().setName(context.getName() + "_" + Thread.currentThread().getId() + "_" + method.getName());
		String className=method.toString().split("."+method.getName())[0].substring(method.toString().split("."+method.getName())[0].lastIndexOf(".")+1);
		Utils.initialValue();
		Utils.setThreadLocalUniqueTestName(className+"."+method.getName() + "_" + Helper.genRandString(4));
		Utils.setThreadLocalImplictWait(testConfig.getImplicitWait());
		TestConfigCustom.load(context, testConfig);
		TestAPIConfigCustom.load(context, testConfig);
		dlog.put(Thread.currentThread().getId(), new DefectLogger());
		
		videoMap.put(Thread.currentThread().getId(), new ArrayList<Video>());
		if (testConfig.serverIsGridHub() && !testConfig.serverIsLegacyGrid() && !testConfig.serverIsBrowserStack() )  {
			log.info("LOG FILE: " + Utils.getThreadLocalUniqueTestName().replace(".", "_") + ".log");
		}
	}
	
	@BeforeClass(alwaysRun=true)
	@Override
	public void beforeClass(ITestContext context) {

		super.beforeClass(context);
		this.driver = (RCLocationExecutor) exec;
		
		Thread.currentThread().setName(context.getName() + "_" + Thread.currentThread().getId());
		TestConfigCustom.load(context, testConfig);
		TestAPIConfigCustom.load(context, testConfig);
		orgs.addAll(OrgConfig.loadOrgs());
		
		testConfig.updateBrowserURL(orgs.get(0).getURI());
		
		browser = BrowserActions.getBrowserAction(driver, testConfig.getBrowser());
	}

	@AfterClass(alwaysRun=true)
	@Override
	public void afterClass(XmlTest test) {

		super.afterClass(test);
		TestConfigCustom.getInstance().getUserAllocator().checkInAllUsersWithToken(this);
	}

	@AfterSuite(alwaysRun = true)
	public void afterSuite(XmlTest test, ITestContext context) throws Exception {
		if(isBrowserStack) {
			if (browserStackProps.getProperty("deleteBSArtifacts").equalsIgnoreCase("true")) {
				GetBrowserStackArtifacts bsobj;
				try 
				{
					bsobj = new GetBrowserStackArtifacts();
					//delete artifacts from Browser Stack
					bsobj.bsDeleteArtifactsFromBrowserStack();
				}
				catch(Exception e)
				{
					log.error("Failed to delete Artifacts from Browser Stack - " + e);
				}
			}
		}
		super.afterSuite(test);
	}

	@AfterMethod(alwaysRun=true)
	public void afterMethod(ITestResult result, Method method, ITestContext context) {
		Helper.recordJSErrors(driver, result);
		Helper.createDefectLog(result, dlog.get(Thread.currentThread().getId()));
		ArrayList<String> vids = new ArrayList<String>();
		int vidNum = 1;
		GetBrowserStackArtifacts test = null;

		boolean bs_artifacts=true;
		
		if (!testConfig.serverIsLegacyGrid() && !testConfig.serverIsBrowserStack() )  {
			super.afterMethod(result, method);
		}
		//For Browser Stack		
		else if(testConfig.serverIsBrowserStack()){
			try {
				super.afterMethod(result, method);
				
				log.info("Update test case status in BrowserStack...");
				test = new GetBrowserStackArtifacts(context);
				if(!result.isSuccess()){					
					test.bsUpdateTestCaseStatusInBrowserStack("FAILED");
				} 
				else {
					test.bsUpdateTestCaseStatusInBrowserStack("PASSED");
				} 
			}
			catch (IOException e) {
				e.printStackTrace();
				log.error("Failed to update test case status at BrowserStack", e);
			}
		} else if(!TestConfigCustom.getInstance().getPushVideos() || result.isSuccess()) {
			super.afterMethod(result, method);
		}
		
		if(TestConfigCustom.getInstance().getPushVideos() && !result.isSuccess())
		{
			if (driver.isLoaded()) {
				// add grid-extra upload video url to videoMap
				// keeping it at the top since the method name suggests it's a common
				// step and we could add more ops if needed in the future
				Helper.endSession(driver, TestConfigCustom.getInstance());
			}
			
			if (!testConfig.serverIsLegacyGrid() && !testConfig.serverIsBrowserStack() )  {
				if(!(Utils.getThreadLocalUniqueTestName().equalsIgnoreCase("No Artifacts")))
				{
					String hub = TestConfigCustom.getInstance().getFileServer().replace("/videos", "");
					String port = TestConfigCustom.getInstance().getFileServerPort();
					String videoUrl = "http://" + hub + ":" + port + "/videos/" + Utils.getThreadLocalUniqueTestName().replace(".", "_") + ".mp4";
					vids.add(videoUrl);
					Helper.createVideoOutput(result, videoUrl, vidNum);
					result.setAttribute("videoURL", vids);
				}
				
			}
			else if(testConfig.serverIsBrowserStack())
			{
				try 
				{
					//download video
					test.bsDownloadExecutionVideo(result);
					//download all screenshots and raw log
					test.bsDownloadBrowserStackRawLogs(result);
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					log.error("Failed to push video", e);
					e.printStackTrace();
					bs_artifacts=false;
				}
				if(bs_artifacts)
				{
					String className = result.getTestClass().getName();
					className = className.substring(className.lastIndexOf(".")+1, className.length());
					String methodName = result.getMethod().getMethodName();
					String location = browserStackProps.getProperty("downloadPath")+File.separator+"videos" + File.separator + 
							browserStackProps.getProperty("buildName") + className + "_" + methodName + ".mp4";
					
					vids.add(location);
					Helper.createVideoOutput(result, location, vidNum);
					result.setAttribute("videoURL", vids);
				}
			}
			else {
				boolean success;
				
				// this needs to be called between Helper.endSession and Helper.getRequestString because
				// a) Helper.endSession initializes the video upload url
				// b) the video file won't be ready until after super.afterMethod
				super.afterMethod(result, method);
				
				log.info("Pushing video");
				for (Video currVid: videoMap.get(Thread.currentThread().getId())){
					// Try pushing each video independently
					try{
						String response = Helper.getRequestString(currVid.getURL());
						log.info(String.format("Response from push video is %s", response));
						success = true;
					}
					catch (Exception e){
						log.error("Failed to push video", e);
						success = false;
					}
					if(success){
						String currURL = Helper.getVideoURL(currVid.getSession(), testConfig);
						// For reportNG
						vids.add(currURL);
						// Add to "videos" directory for Jenkins
						Helper.createVideoOutput(result, currURL, vidNum);
					}
					vidNum++;
				}
				// Set list of vids for reportNG to display
				result.setAttribute("videoURL", vids);
			}
		}
		
		TestConfigCustom.getInstance().getUserAllocator().checkInAllUsers();
		LogManager.stopTestLogging();
	}
}