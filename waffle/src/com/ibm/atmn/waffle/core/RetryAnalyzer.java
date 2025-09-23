package com.ibm.atmn.waffle.core;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {
	
	private static Logger log = LoggerFactory.getLogger(RetryAnalyzer.class);
	public static ConcurrentHashMap<Object, Integer> retryCount = new ConcurrentHashMap<Object, Integer>();
	private int count = 0;

	@Override
	public boolean retry(ITestResult result) {
		int maxCount = 0;
		
		String maxRetries = result.getTestContext().getCurrentXmlTest().getParameter("max_retries");
		if(maxRetries != null) {
			try{
				maxCount = Integer.parseInt(maxRetries);
			} catch(NumberFormatException e) {
				log.warn("Could not parse max_retries parameter, tests will not retry.");
			}
		}
		result.setAttribute("retriesNum", count);
		result.setAttribute("delete", false);
		if (count < maxCount) {
			 count++;
			 String message = Thread.currentThread().getName() + "Error in " + result.getName() + " with status " + result.getStatus() + " Retrying "
					 + count + " times";
			 result.setAttribute("delete", true);
			 retryCount.put(result.getInstanceName()+result.getMethod(), count);
			 log.info(message);
			 return true;
		 }
		return false;
	}

}
