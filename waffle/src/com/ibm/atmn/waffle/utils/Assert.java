package com.ibm.atmn.waffle.utils;

import java.util.function.Supplier;

import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.testng.asserts.SoftAssert;


/**
 * Custom Assert class to include logging
 */
public class Assert {
	
	private Logger logger;
	private SoftAssert softAssert;
	
	public Assert(Logger mylogger)  {
		logger = mylogger;
	}
	
	public void assertEquals(Object actual, Object expected, String message) {
		Supplier<Void> tester = () -> { 
			org.testng.Assert.assertEquals(actual, expected, message);
			return null;
		};
		assertCondition(tester, message);
	}
	
	public void assertNotEquals(Object actual, Object expected, String message) {
		Supplier<Void> tester = () -> { 
			org.testng.Assert.assertNotEquals(actual, expected, message);
			return null;
		};
		assertCondition(tester, message);
	}
	
	public void assertTrue(boolean condition, String message) {
		Supplier<Void> tester = () -> { 
			org.testng.Assert.assertTrue(condition, message);
			return null;
		};
		assertCondition(tester, message);
	}
	
	public void assertFalse(boolean condition, String message) {
		Supplier<Void> tester = () -> { 
			org.testng.Assert.assertFalse(condition, message);
			return null;
		};
		assertCondition(tester, message);
	}
	
	public void assertNotNull(WebElement item, String message) {
		Supplier<Void> tester = () -> { 
			org.testng.Assert.assertNotNull(item, message);
			return null;
		};
		assertCondition(tester, message);
	}
	
	/**
	 * softAssertTrue will not fail right away until softAssertAll() is invoked.
	 * @param condition
	 * @param message
	 */
	public void softAssertTrue(boolean condition, String message) {
		getSoftAssert().assertTrue(condition, message);
		logger.info("SOFTASSERT: " + message);
	}
	
	public void softAssertFalse(boolean condition, String message) {
		getSoftAssert().assertFalse(condition, message);
		logger.info("SOFTASSERT: " + message);
	}
	
	public void softAssertAll() {
		try {
			getSoftAssert().assertAll();
		} catch (AssertionError ae) {
			logger.error("FAIL: " + ae.getMessage());
			throw ae;
		}
	}
	
	private SoftAssert getSoftAssert() {
		if (softAssert == null)  {
			softAssert = new SoftAssert();
		}
		return softAssert;
	}
	
	private void assertCondition(Supplier<Void> tester, String message) {
		try {
			tester.get();
			logger.info("PASS: " + message);
		} catch (AssertionError ae) {
			logger.error("FAIL: " + ae.getMessage());
			throw ae;
		}
	}
	
}
