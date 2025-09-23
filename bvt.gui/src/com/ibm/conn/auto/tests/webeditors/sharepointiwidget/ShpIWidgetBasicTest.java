package com.ibm.conn.auto.tests.webeditors.sharepointiwidget;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.tests.webeditors.ShpIWidgetBaseTest;
import com.ibm.conn.auto.webui.SharepointWidgetUI.Widget;

public final class ShpIWidgetBasicTest extends ShpIWidgetBaseTest {

	/**
	 * Determines that the widget is in place - checks if the widget's iframe is present.
	 * 
	 */
	@Test(groups = { "SP_FVT", "WE_FVT" }, invocationCount = 1, priority = 10 )
	public void iframeIsPresentTest() {
		loginAndNavigateToCommunity();
		
		log.info("INFO: navigating to Sharepoint Files widget fullpage mode");
		sharepointWidgetUI.navigateToWidgetFullpageMode(TITLE_ON_CONNECTIONS); // data comes from properties; not the hardcoded defId

		log.info("INFO: asserting the baseCommunity name '" + baseCommunity.getName() + "' is present");
		Assert.assertTrue(sharepointWidgetUI.fluentWaitTextPresent(baseCommunity.getName()),
				"The comunity's title '" + baseCommunity.getName() + "' was not found!");

		log.info("INFO: asserting the widget's frame is present");
		Assert.assertTrue(driver.getElements(Widget.IFRAME).size() == 1,
				"There was a problem while searching the Sharepoint Files widget's frame. (Selector:'" + Widget.IFRAME + "') ");
	}
	
	/**
	 * Determines that the widget is in place and that the contained elements visibility is properly set. 
	 * @throws URISyntaxException 
	 * @throws MalformedURLException 
	 * 
	 * @throws ExecutionException
	 */
	@Test(groups = { "SP_FVT", "WE_FVT" }, invocationCount = 1, priority = 30 )
	public void iframeContentCorrectnessTest() throws MalformedURLException, URISyntaxException {
		loginAndNavigateToCommunity();

		executeContentTest();
	}

	/**
	 * 174797: Determines that the widget is in place and that it's source field is properly set. 
	 * 
	 * @throws URISyntaxException 
	 * @throws MalformedURLException 
	 * 
	 * @throws ExecutionException
	 */
	@Test(groups = { "SP_FVT", "WE_FVT" }, invocationCount = 1, priority = 20 )
	public void iframeSourceCorrectnessTest() throws MalformedURLException, URISyntaxException {
		loginAndNavigateToCommunity();

		log.info("INFO: Configure the widget and switch to it's fullpage view...");
		URL targetSharepointURL = sharepointWidgetUI.performWidgetConfigurationViaUI();
		
		log.info("INFO: asserting the widget's iFrame source field is correct.");
		Element iframeElement = driver.getSingleElement(Widget.IFRAME);
		String iframeSource = iframeElement.getAttribute("src");
		Assert.assertTrue(iframeSource.startsWith(targetSharepointURL.toString()), "The widget's iFrame source field is '" + iframeSource + "'. This field's proper value starts with '" + targetSharepointURL + "'."); 
	}

}
