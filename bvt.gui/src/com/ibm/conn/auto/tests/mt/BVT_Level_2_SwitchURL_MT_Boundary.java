package com.ibm.conn.auto.tests.mt;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.ibm.conn.auto.webui.constants.ActivitiesUIConstants;
import com.ibm.conn.auto.webui.constants.HomepageUIConstants;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.ReadExcel;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;

public class BVT_Level_2_SwitchURL_MT_Boundary extends SetUpMethods2 implements ITest {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_SwitchURL_MT_Boundary.class);
	private ActivitiesUI ui;
	private TestConfigCustom cfg;
	private User testUser_orgA;
	private String serverURL_MT_orgA, serverURL_MT_orgB;
	private ThreadLocal<String> testName = new ThreadLocal<>();
	private static ReadExcel rexl;
	private String view, componentURL;

	@Factory(dataProvider = "data_ComponentURL")
	public BVT_Level_2_SwitchURL_MT_Boundary(String view, String componentURL) {
		this.view = view;
		this.componentURL = componentURL;
	}

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();

		testUser_orgA = cfg.getUserAllocator().getGroupUser(HomepageUIConstants.OrgA, this);
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
		serverURL_MT_orgB = testConfig.useBrowserUrl_Mt_OrgB();
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp(Method method, ITestContext context) {

		testName.set(method.getName() + "_" + this.view);

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);
		String[] groups = context.getIncludedGroups();
		if (Arrays.asList(groups).contains("datadriven")) {
			context.setAttribute("testName", testName.get());
			LogManager.startTestLogging(context.getSuite().getName() + "-" + context.getName() + "-"+ context.getAttribute("testName").toString());
			ui.startTest(context.getSuite().getName() + "-" + context.getName() + "-"+ context.getAttribute("testName").toString());
		}
	}

	@DataProvider(name = "data_ComponentURL")
	public static Object[][] readExcelDataSwitchURL() throws InvalidFormatException, IOException {

		// Instantiate read excel object
		rexl = new ReadExcel();

		// Read data from excel and store it into object array
		Object[][] componentUrls = rexl.readExcel("resources//URL.xlsx", "switchURL", 2);

		return componentUrls;
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that orgA user is not able to switch to orgB url</li>
	 * <li><B>Step: </B> Launch different url's provided in excel sheet</li>
	 * <li><B>Step: </B> Login with orgA user</li>
	 * <li><B>Step: </B> Change orga to orgb from browser URL</li>
	 * <li><B>Step: </B> Hit the URL</li>
	 * <li><B>Verify: </B>Verify that "Access Denied " message should be displayed</li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel3","datadriven" })
	public void switchURL() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Load the component and login
		log.info("Load " + this.componentURL + " and Log In as " + testUser_orgA.getDisplayName());
		logger.strongStep("Load " + this.componentURL + " and Log In as " + testUser_orgA.getDisplayName());
		ui.loadComponent(serverURL_MT_orgA, this.componentURL);
		ui.login(testUser_orgA);

		if (componentURL.contains("activities")) {

			// waiting for activity page to load
			ui.fluentWaitElementVisible(ActivitiesUIConstants.activitiesNavigationBar);
		}

		// switch URL to orgB
		logger.strongStep("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		log.info("Switching URL: " + driver.getCurrentUrl() + " to orgB.");
		ui.switchToOrgBURL(serverURL_MT_orgB);

		if (componentURL.contains("homepage") || componentURL.contains("social")) {

			// Validate error message
			logger.strongStep("Verify access denied error message should be displayed");
			log.info("Verify access denied error message should be displayed");
			ui.validateAccessDenied("We are unable to process your request",
					"Click the browser back button to return to the previous page and try again. If this error persists, report the problem to your administrator.");

		} else {

			// Validate error message
			logger.strongStep("Verify access denied error message should be displayed");
			log.info("Verify access denied error message should be displayed");
			ui.validateAccessDenied("Access Denied", "You do not have permission to access this page.");
		}
		ui.endTest();
	}

	@Override
	public String getTestName() {
		return testName.get();
	}

}
