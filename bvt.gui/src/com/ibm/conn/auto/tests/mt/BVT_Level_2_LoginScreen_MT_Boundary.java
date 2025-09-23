package com.ibm.conn.auto.tests.mt;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.ibm.conn.auto.webui.constants.BaseUIConstants;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;
import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.util.DefectLogger;
import com.ibm.conn.auto.util.ReadExcel;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.ActivitiesUI;

public class BVT_Level_2_LoginScreen_MT_Boundary extends SetUpMethods2 implements ITest {

	private static Logger log = LoggerFactory.getLogger(BVT_Level_2_LoginScreen_MT_Boundary.class);
	private ActivitiesUI ui;
	private TestConfigCustom cfg;
	private String serverURL_MT_orgA;
	private static ReadExcel rexl;
	private ThreadLocal<String> testName = new ThreadLocal<>();

	private String view, componentURL;

	@Factory(dataProvider = "loginScreen")
	public BVT_Level_2_LoginScreen_MT_Boundary(String view, String componentURL) {
		this.view = view;
		this.componentURL = componentURL;

	}

	@BeforeClass(alwaysRun = true)
	public void setUpClass() {

		cfg = TestConfigCustom.getInstance();
		serverURL_MT_orgA = testConfig.useBrowserUrl_Mt_OrgA();
	}

	@BeforeMethod(alwaysRun = true)
	public void setUp(Method method, ITestContext context) {

		testName.set(method.getName() + "_" + this.view);

		// initialize the configuration
		cfg = TestConfigCustom.getInstance();
		ui = ActivitiesUI.getGui(cfg.getProductName(), driver);

		// Test Logging starts for data driven test
		String[] groups = context.getIncludedGroups();
		if (Arrays.asList(groups).contains("datadriven")) {
			context.setAttribute("testName", testName.get());
			LogManager.startTestLogging(context.getSuite().getName() + "-" + context.getName() + "-"+ context.getAttribute("testName").toString());
			ui.startTest(context.getSuite().getName() + "-" + context.getName() + "-"+ context.getAttribute("testName").toString());
		}
	}

	@DataProvider(name = "loginScreen")
	public static Object[][] readExcelDataLoginScreen() throws InvalidFormatException, IOException {

		// Instantiate read excel object
		rexl = new ReadExcel();

		// Read data from excel and store it into object array
		Object[][] componentUrls = rexl.readExcel("resources//URL.xlsx", "loginScreen", 2);
		return componentUrls;
	}

	/**
	 * <ul>
	 * <li><B>Info: </B> Test that user prompted with login screen after launching  from excel</li>
	 * <li><B>Step: </B> Launch different url's provided in excel sheet</li>
	 * <li><B>Verify: </B>Verify that user is prompted with login screen</li>
	 * </ul>
	 */

	@Test(groups = { "mtlevel2", "datadriven" })
	public void validateLoginScreen() {
		DefectLogger logger = dlog.get(Thread.currentThread().getId());

		// Load the component
		log.info("Load the component: " + this.componentURL);
		logger.strongStep("Load the component: " + this.componentURL);
		ui.loadComponent(serverURL_MT_orgA, this.componentURL);

		// Verify that login screen is displayed
		log.info("Verify that user is prompted with login screen");
		logger.strongStep("Verify that user is prompted with login screen");
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.USERNAME_FIELD), "Error: User name field is not displayed");
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Password_FIELD), "Error: Password field is not displayed");
		Assert.assertTrue(driver.isElementPresent(BaseUIConstants.Login_Button), "Error: Login button is not displayed");

		ui.endTest();
	}

	@Override
	public String getTestName() {
		return testName.get();
	}

}
