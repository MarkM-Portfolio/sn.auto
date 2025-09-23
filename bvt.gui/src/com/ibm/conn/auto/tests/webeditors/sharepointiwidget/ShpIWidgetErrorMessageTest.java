package com.ibm.conn.auto.tests.webeditors.sharepointiwidget;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;

import org.testng.annotations.Test;

import com.ibm.conn.auto.tests.webeditors.ShpIWidgetBaseTest;

public final class ShpIWidgetErrorMessageTest extends ShpIWidgetBaseTest {

	/**
	 * 174797: Determines that the widget's "No configuration" error message is properly displayed when the widget is not configured.
	 * 
	 */
	@Test(groups = { "SP_FVT", "WE_FVT" }, invocationCount = 1, priority = 10 )
	public void fullPageNoConfigurationErrorMsg() {
		loginAndNavigateToCommunity();
		
		log.info("INFO: navigating to Sharepoint Files widget fullpage mode");
		sharepointWidgetUI.navigateToWidgetFullpageMode(TITLE_ON_CONNECTIONS); // data comes from properties; not the hardcoded defId
		
		log.info("INFO: asserting the No Configuration Error is present");
		sharepointWidgetUI.assertNoConfigurationErrorIsDisplayed();
	}
	
	/**
	 * 174896: Verify that the Widget edit mode display is actually shown (in widget view)
	 * 
	 */
	@Test(groups = { "SP_FVT", "WE_FVT" }, invocationCount = 1, priority = 20 )
	public void assertEditModeElementsArePresent() {
		loginAndNavigateToCommunity();
		
		log.info("INFO: checking if the the widget configuration web UI is active");
		sharepointWidgetUI.assertConfigurationUIisActive(); 
	}

	/**
	 * 174897: Verify that the widget does not save any data if an invalid URL is written by a user in it's input in edit mode (verified by red error message)
	 * 174897: Verify that the widget does not save any data if an invalid URL is written by a user in it's input in edit mode (verified by red error message)
	 * 
	 */
	@Test(groups = { "SP_FVT", "WE_FVT" }, invocationCount = 1, priority = 30 )
	public void assertWidgetVerifiesURLValidity() {
		loginAndNavigateToCommunity();
		
		log.info("INFO: checking if the the widget configuration web UI is active");
		sharepointWidgetUI.assertConfigUiVerifiesUrlCorrectness(); 
	}

}
