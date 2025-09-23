package com.ibm.atmn.waffle.base;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openqa.selenium.remote.UnreachableBrowserException;
import org.seleniumhq.jetty9.util.URIUtil;
import org.testng.*;

import com.ibm.atmn.waffle.core.TestManager;


public class BaseTestListener implements ITestListener {

	private static List<ITestResult> processedTestFailures = new ArrayList<ITestResult>();
	public static String getMethodName;

	@Override
	public void onFinish(ITestContext context) {
		Iterator<ITestResult> failedTestCases = context.getFailedTests().getAllResults().iterator();
        while (failedTestCases.hasNext())
        {
            ITestResult failedTestCase = failedTestCases.next();
            ITestNGMethod method = failedTestCase.getMethod();
            if ( context.getFailedTests().getResults(method).size() > 1)
            {
            	if((Boolean) failedTestCase.getAttribute("delete")) {
            		Reporter.log("failed test case remove as dup:" + failedTestCase.getTestClass().toString());
                    failedTestCases.remove(); 
            	}
            }
        }
        
        // since TestNG 6.9.5, initial run of a retried test is marked as Skipped instead of Failed
        // check the skipped tests to see if there is already one in the passed or failed bucket. If found, delete the prior tries.
        // https://github.com/cbeust/testng/issues/1715
		Iterator<ITestResult> skippedTestCases = context.getSkippedTests().getAllResults().iterator();
        while (skippedTestCases.hasNext())
        {
        	ITestResult skippedTestCase = skippedTestCases.next();
        	ITestNGMethod method = skippedTestCase.getMethod();
        	if (context.getPassedTests().getResults(method).size() > 0 || context.getFailedTests().getResults(method).size() > 0 )
        	{
        		if((Boolean) skippedTestCase.getAttribute("delete")) {
        			Reporter.log("skipped test case remove as retry:" + skippedTestCase.getTestClass().toString());
        			skippedTestCases.remove();
        		}
        	}
        }
	}

	@Override
	public void onStart(ITestContext context) {

	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

	}

	@Override
	public void onTestFailure(ITestResult tr) {

		if (!processedTestFailures.contains(tr)) {
			processedTestFailures.add(tr);
			// Take screen shot only for failed test case
			String className = tr.getTestClass().getName();
			className = className.substring(className.lastIndexOf(".")+1, className.length());
			
			// Using getName() instead getMethodName()to generate unique screenshot in data driven test
			String methodName = tr.getName();

			ITestContext context = tr.getTestContext();
			TestManager manager = TestManager.getTestManager(context);
			if (manager.getExecutor().isLoaded()) {
				try{
					File f = manager.getExecutor().saveScreenshotWithFilename(className + "_" + methodName);
					String relativeFilePath, filePath;
					//Screenshots
					if (f != null) {
						relativeFilePath = "../" + context.getSuite().getName() + "/screenshots/" + f.getName();
						filePath = URIUtil.encodePath(relativeFilePath);
						tr.setAttribute("screenShotPath", filePath);
					}
					//Defect Logger
					relativeFilePath = "../" + context.getSuite().getName() + "/defectLog/" + className + "_" + methodName + ".log";
					filePath = URIUtil.encodePath(relativeFilePath);
					tr.setAttribute("defectLogPath", filePath);
				}
				catch(UnreachableBrowserException e) {}
			}
			Reporter.log(" Full name: " + context.getSuite().getName() + " - " + context.getName() + " - " + methodName + "\n has failed");
			File destination = new File("BVTErrorLog.txt");
			try {
				FileWriter fw = new FileWriter(destination,true); //the true will append the new data
			    fw.write(" Full name: " + context.getSuite().getName() + " - " + context.getName() + " - " + methodName + "\n has failed");//appends the string to the file
			    fw.write("\r\n");//adds a carriage return after each line
			    fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onTestSkipped(ITestResult result) {

		String className = result.getTestClass().getName();
		className = className.substring(className.lastIndexOf(".")+1, className.length());
		String methodName = result.getMethod().getMethodName();
		ITestContext context = result.getTestContext();
		TestManager manager = TestManager.getTestManager(context);
		if (manager.getExecutor().isLoaded()) {
			try{
				String skipExceptionMsg = "[Throwable object was null]";
				if(result.getThrowable() != null) {
					skipExceptionMsg = result.getThrowable().getMessage();
				}
				
				File f = manager.getExecutor().saveScreenshotWithFilename(className + "_" + methodName);
				if (f != null)  {
					String relativeFilePath = "../" + context.getSuite().getName() + "/screenshots/" + f.getName();
					String filePath = URIUtil.encodePath(relativeFilePath);
					String linkToImage = "<a href=" + filePath + ">" + "Screenshot for method: " + f.getName() + "</a>";
					result.setThrowable(new SkipException(skipExceptionMsg + "<br />" + linkToImage + " <br />"));
				} else {
					result.setThrowable(new SkipException(skipExceptionMsg + "<br />"));
				}
			}
			catch(UnreachableBrowserException e) {}
		}
	}

	@Override
	/*
	 * this was empty - CP changed to see what effect this has on the log
	 */
	public void onTestStart(ITestResult tr) {//changed from result
		String methodName = tr.getMethod().getMethodName();
		if (tr.getTestContext().getAttribute("testName") != null) {
			// methodName stores the name of data driven test sets from test class 
			methodName = tr.getTestContext().getAttribute("testName").toString();
		}
		
		String className = tr.getTestClass().getRealClass().getSimpleName();
		getMethodName=className+"."+methodName;
		
		ITestContext context = tr.getTestContext();
		Reporter.log(" Test : " + context.getSuite().getName() + " - " + context.getName() + " - " + methodName + "\n has started");
		//HTML logs
		String relPath = "../logs/testcases/" + context.getSuite().getName() + "-" + context.getName() + "-" + methodName + ".html";
		String logPath = URIUtil.encodePath(relPath);
		tr.setAttribute("test_log", logPath);
	}

	@Override
	/*
	 * this was empty - CP changed to see what effect this has on the log
	 */
	public void onTestSuccess(ITestResult tr) {//changed from result
		String methodName = tr.getMethod().getMethodName();
		ITestContext context = tr.getTestContext();
		Reporter.log(" Test : " + context.getSuite().getName() + " - " + context.getName() + " - " + methodName + "\n has passed");
		tr.setAttribute("retriesNum", context.getFailedTests().getResults(tr.getMethod()).size());
		for(ITestResult r: tr.getTestContext().getFailedTests().getResults(tr.getMethod())){
			tr.getTestContext().getFailedTests().removeResult(r.getMethod());
		}
	}

}
