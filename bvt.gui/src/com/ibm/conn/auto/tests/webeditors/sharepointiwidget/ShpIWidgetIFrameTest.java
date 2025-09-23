package com.ibm.conn.auto.tests.webeditors.sharepointiwidget;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.core.Element;
import com.ibm.conn.auto.tests.webeditors.ShpIWidgetBaseTest;

import static com.ibm.conn.auto.webui.SharepointWidgetUI.Widget.*;
//Importing the constants related to the sharepoint configuration inside a properties file
import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.TITLE_ON_CONNECTIONS;

import java.net.URI;
import java.net.URISyntaxException;
import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.BVT_SHAREPOINT_SERVER;

public final class ShpIWidgetIFrameTest extends ShpIWidgetBaseTest {
	
	private URI targetSharepoint;
	
	
	/**
	 * beforeMethod() - sets up the environment
	 * 
	 * Method which configures the test environment by instantiating the configuration, allocating a user, the necessary GUI's and also 
	 * creating a Test Community in Connections, also adding a Sharepoint Files widget to it. It calls the super class beforeClass method so it can run
	 * before the one from this code. In the end, it calls a method which performs the rest of the configuration using the UI inside the newly created Connections Community widget.
	 * @throws URISyntaxException 
	 * */
	@BeforeMethod(alwaysRun = true)
	public void beforeMethodBCSW() throws URISyntaxException {
		//Call a method which sets up Connections for the tests 
		setupConnections();
	}
	
	
	/**
	 * setupConnections()
	 * 
	 * Method which configures the environment created by login in on the SP site (so login prompts are not present on the tests),
	 * and configuring the iWidget in the Connections Community created. By configuration, we mean modifying the name of the iWidget and
	 * inserting the SP site URL, which is necessary for the following tests. This method is called once, before the tests are ran.
	 * @throws URISyntaxException 
	 * */
	private void setupConnections() throws URISyntaxException {
		
		loginAndNavigateToCommunity();
		
		this.targetSharepoint = new URI( BVT_SHAREPOINT_SERVER );
		
		//Configuring the iWidget with the URL from the Sharepoint site
		log.info("INFO: CONNECTIONS CONFIG - Configuring the " + TITLE_ON_CONNECTIONS + " widget to use the resources at '" + BVT_SHAREPOINT_SERVER + "'");
		sharepointWidgetUI.configureSharepointWidget(TITLE_ON_CONNECTIONS, targetSharepoint);
	}
	
	/**<ul>iWidgetPresenceTest()
	*<li></li>
	*<li><B>Info: Determines that the widget is in place - checks if the widget's iframe is present.</B></li>
	*<li><B>Step: loads Connections and navigate to the community. Then, use the drop down menu to navigate to the full page</B></li>
	*<li><B>Verify: Checks if the iframe is there, as the widget is in place</B> </li>
	*</ul>*/
	@Test(groups = { "SP_BVT", "darklaunch"  }, invocationCount = 1)
	public void iWidgetPresenceTest() {
		
		//Use the drop down menu to navigate to the Sharepoint iWidget full page mode
		log.info("INFO: Navigating to widget full page on '" + TITLE_ON_CONNECTIONS + "'");
		sharepointWidgetUI.navigateToWidgetFullpageMode(TITLE_ON_CONNECTIONS);

		//Performing the verification of existence of the iFrame
		log.info("ASSERT: Asserting the widget's frame is present");
		Assert.assertTrue(driver.getElements(IFRAME).size() == 1,
				"ASSERT FAILED: There was a problem while searching the Sharepoint Files widget's frame. (Selector:'" + IFRAME + "') ");
		log.info("ASSERT PASSED: iFrame Exists");
	}
	
	/**<ul>iFrameContentTest()
	*<li></li>
	*<li><B>Info: Determines that the content iFramed is the correct one</B></li>
	*<li><B>Step: loads Connections and navigate to the community. Then, use the drop down menu to navigate to the full page</B></li>
	*<li><B>Verify: Checks if the iframe is there, as the widget is in place</B> </li>
	*</ul>*/
	@Test(groups = { "SP_BVT", "darklaunch" }, invocationCount = 1) 
	public void iFrameContentTest() {
		
		//Use the drop down menu to navigate to the Sharepoint iWidget full page mode
		log.info("INFO: Navigating to widget full page on '" + TITLE_ON_CONNECTIONS + "'");
		sharepointWidgetUI.navigateToWidgetFullpageMode(TITLE_ON_CONNECTIONS);

		//Fetching the iFrame on the page
		Element iframeElement = driver.getSingleElement(IFRAME);
		//Getting the source of the content of the iFrame
		String iframeSource = iframeElement.getAttribute("src");
		//Performing the verification of the content of the iFrame
		log.info("ASSERT: Asserting the widget's iFrame source field is correct.");
		Assert.assertTrue(iframeSource.startsWith(targetSharepoint.toString()), 
				"ASSERT FAILED: The widget's iFrame source field is '" + iframeSource + "'. This field's proper value starts with '" + targetSharepoint.toString() + "'."); 
		log.info("ASSERT PASSED: The source inside the iFrame is the same inserted on the Configuration");
		
		//Going inside the iFrame and returning to Connections afterwards
		log.info("INFO: Locating the frame with the Sharepoint generated content, which is part of the Sharepoint Files widget");
		driver.switchToFrame().selectSingleFrameBySelector(IFRAME);
		log.info("INFO: Returning focus to the top frame (Connections web page)...");
		driver.switchToFrame().returnToTopFrame();
	};
	
}