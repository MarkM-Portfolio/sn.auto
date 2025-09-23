package com.ibm.conn.auto.config;

import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.xml.XmlTest;
import org.testng.annotations.AfterSuite;

import com.ibm.atmn.waffle.base.BaseSetup;
import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.atmn.waffle.extensions.user.UserAllocation;
import com.ibm.atmn.waffle.log.LogManager;
import com.ibm.lconn.automation.framework.services.common.StringGenerator;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.HttpsURLConnection;

@Deprecated
public class SetUpMethods extends BaseSetup {
	
	protected static UserAllocation userAllocator;
	protected RCLocationExecutor driver;

	private boolean isPreBVT = false;
	protected boolean isSpecialCharacters = false;
	protected StringGenerator sg = StringGenerator.getInstance();

	public static String genDateBasedRandVal() {

		SimpleDateFormat tmformat = new SimpleDateFormat("DDDHHmmss");
		return tmformat.format(new Date())+ Thread.currentThread().getId();
	}
	
	public static String genDateBasedRand() {

		SimpleDateFormat tmformat = new SimpleDateFormat("DDDHHmmss");
		return tmformat.format(new Date())+ (int)(Math.random() * (100 - 1) );
	}
	
	public static String stamp(String start) {

		return start + genDateBasedRandVal();
	}

	public static void sleep(int duration) {

		try {
			Thread.sleep(duration);
		} catch (InterruptedException e) {
			return;
		}
	}

	public enum CustomParameterNames {
		ROOT_FOLDER_NAME("SeleniumServer"), UPLOAD_FILES_FOLDER_NAME("TestFiles"), USERS_LOGIN_PREFERENCE("email"), USERS_TYPEAHEAD_PREFERENCE("Display name"), BVT_OPTION("preBVT"), SPECIAL_CHARACTERS("false"),
		DEFAULT_USERS_PROPERTIES("UsersProperties.properties"), DEFAULT_APP_PROPERTIES("app.properties"), PRODUCT_NAME("onprem"), FLUENT_WAIT_TIMEOUT("40");

		private final String defaultValue;

		CustomParameterNames() {

			this.defaultValue = null;
		}

		CustomParameterNames(String defaultValue) {

			this.defaultValue = defaultValue;
		}

		public String getDefaultValue() {

			return this.defaultValue;
		}
	}

	/*
	 * Files identify purposed directories on browser machine. Folder names should be common to all suites but paths will depend on browserEnvironmentOS.
	 */
	private String browserEnvironmentBaseDir;
	private String uploadFilesDir;
	@SuppressWarnings("unused")
	private String appPropFile;
	@SuppressWarnings("unused")
	private String productProp;
	@SuppressWarnings("unused")
	private String fluentwaittime;
	public static String PRODUCT_NAME;
	protected static Map<CustomParameterNames, String> customParameters = new EnumMap<CustomParameterNames, String>(CustomParameterNames.class);

	public SetUpMethods() {

	}

	@BeforeClass(alwaysRun=true)
	@Override
	public void beforeClass(ITestContext context) {

		super.beforeClass(context);
		this.driver = (RCLocationExecutor) exec;
		
		gatherCustomParameters(context);

		// Absolute paths to folders on browser machine
		setBrowserEnvironmentBaseDir(testConfig.getBrowserEnvironment().constructAbsolutePathToDirectoryFromRoot(customParameters.get(CustomParameterNames.ROOT_FOLDER_NAME)));
		setUploadFilesDir(testConfig.getBrowserEnvironment().constructAbsolutePathToDirectoryFromRoot(getBrowserEnvironmentBaseDir(),
				customParameters.get(CustomParameterNames.UPLOAD_FILES_FOLDER_NAME)));
		if (getBVTOption() == null || !getBVTOption().equalsIgnoreCase("BVT"))
			isPreBVT = true;
		isSpecialCharacters = isSpecialCharacters();
		userAllocator = UserAllocation.getUserAllocation(customParameters.get(CustomParameterNames.DEFAULT_USERS_PROPERTIES));
		setDefaultAppProperties(customParameters.get(CustomParameterNames.DEFAULT_APP_PROPERTIES));
		setProductName(customParameters.get(CustomParameterNames.PRODUCT_NAME));
		setFluentWait(customParameters.get(CustomParameterNames.FLUENT_WAIT_TIMEOUT));
		PRODUCT_NAME=customParameters.get(CustomParameterNames.PRODUCT_NAME);
	}

	@AfterClass(groups = { "level1", "level2", "level3", "specialCharacters", "test", "smoke", "bvt", "regression", "failing", "inprogress" })
	@Override
	public void afterClass(XmlTest test) {

		super.afterClass(test);
		userAllocator.checkInAllUsersWithToken(this);
	}

	@AfterSuite(alwaysRun = true)
	@Override
	public void afterSuite(XmlTest test) throws Exception {
		
		super.afterSuite(test);
		if (isPreBVT())
			sendStatistic();
	}

	@AfterMethod(alwaysRun=true)
	@Override
	public void afterMethod(ITestResult result, Method method) {
			
		super.afterMethod(result, method);
		userAllocator.checkInAllUsers();
		LogManager.stopTestLogging();
	}
	
	@BeforeMethod(alwaysRun=true)
	@Override
	public void beforeMethod(ITestContext context, Method method) {
		
		LogManager.startTestLogging(context.getSuite().getName()+"-"+context.getName()+"-"+method.getName());
		super.beforeMethod(context, method);
	}
	
	private void sendStatistic() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
		}

		HttpURLConnection connection = null;
		URL serverAddress = null;

		try {
			serverAddress = new URL("https://lc30linux3.swg.usma.ibm.com/cgi-bin/prebvt-stat");
			connection = (HttpURLConnection) serverAddress.openConnection();
			connection.setRequestMethod("GET");
			connection.setReadTimeout(10000);
			connection.connect();
			connection.getInputStream();
		} catch (Exception e) {
		} finally {
			if(connection != null) connection.disconnect();
		}
	}

	private void gatherCustomParameters(ITestContext context) {

		String param = null;
		for (CustomParameterNames paramName : CustomParameterNames.values()) {
			param = context.getCurrentXmlTest().getParameter(paramName.toString().toLowerCase());
			if (param != null) {
				SetUpMethods.customParameters.put(paramName, param);
			} else if (paramName.getDefaultValue() != null) {
				SetUpMethods.customParameters.put(paramName, paramName.getDefaultValue());
			} else {
				throw new InvalidParameterException("Required parameter not found in testng .xml config file: " + paramName.toString().toLowerCase());
			}
		}
	}

	protected void setUploadFilesDir(String uploadFilesDir) {

		this.uploadFilesDir = uploadFilesDir;
	}

	protected String getUploadFilesDir() {

		return uploadFilesDir;
	}

	protected String getBrowserEnvironmentBaseDir() {

		return browserEnvironmentBaseDir;
	}

	protected void setBrowserEnvironmentBaseDir(String browserEnvironmentBaseDir) {

		this.browserEnvironmentBaseDir = browserEnvironmentBaseDir;
	}

	protected String getUsersLoginPreference() {
		//changed from using this
		return SetUpMethods.customParameters.get(CustomParameterNames.USERS_LOGIN_PREFERENCE);
	}

	private String getBVTOption() {

		return SetUpMethods.customParameters.get(CustomParameterNames.BVT_OPTION);
	}
	
	private String getSpecialCharacters() {
		return SetUpMethods.customParameters.get(CustomParameterNames.SPECIAL_CHARACTERS);
	}
	
	protected boolean isSpecialCharacters() {
		return getSpecialCharacters().equals("true");
	}
	
	protected boolean isPreBVT() {
		 
		return this.isPreBVT;
	}
	
	@SuppressWarnings("unused")
	private String getDefaultProperties() {

		return SetUpMethods.customParameters.get(CustomParameterNames.DEFAULT_USERS_PROPERTIES);
	}
	
	protected void setDefaultAppProperties(String appPropFile) {

		this.appPropFile = appPropFile;
	}
	
	@SuppressWarnings("unused")
	private String getDefaultAppProperties() {

		return SetUpMethods.customParameters.get(CustomParameterNames.DEFAULT_APP_PROPERTIES);
	}
	
	private void setProductName(String productProp) {
		
		this.productProp = productProp;
	}
	
	@SuppressWarnings("unused")
	private String getProductName() {
		return SetUpMethods.customParameters.get(CustomParameterNames.PRODUCT_NAME);
	}
	
	private void setFluentWait(String  fluentwaittime) {
		this.fluentwaittime = fluentwaittime;
	}
	
	@SuppressWarnings("unused")
	private String getfluentWait() {
		return SetUpMethods.customParameters.get(CustomParameterNames.FLUENT_WAIT_TIMEOUT);
	}
	
}
