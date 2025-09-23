package com.ibm.conn.auto.util;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;

import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.core.webdriver.WebDriverExecutor;
import com.ibm.atmn.waffle.extensions.user.UserAllocation;

/**
 * Holds test configuration. One instance per thread.
 * Current options:
 * 1. bvtOption
 * 2. isSpecialCharacters
 * 3. appProperties
 * 4. fluentwaittime
 * 5. productName
 * 6. loginType
 * 7. browserEnvironmentBaseDir
 * 8. uploadFilesDir
 * 9. loginPreference
 * 10. typeaheadPreference
 * 11. userAllocator
 * 12. testConfig
 * 13. isSametimeEnabled
 * 14. isFilenetEnabled
 * 15. ftpPort
 * 16. ftpUsername
 * 17. ftpPassword
 * 18. ftpDownloadsDir
 * 19. ftpUploadsDir
 * 20. pushVideos
 * 21. fileServer
 * 22. useNewUI
 * 23. iskudosboardenabled
 * 
 * @author Ilya
 *
 */
public class TestConfigCustom {
	
	private static Logger log = LoggerFactory.getLogger(WebDriverExecutor.class);
	private volatile static Map<String, TestConfigCustom> instance = new ConcurrentHashMap<String, TestConfigCustom>();
	private TestConfiguration testConfig;
	
	private String bvtOption;
	private boolean isSpecialCharacters = false;
	private Properties appProperties;
	private String org_xml_path; 
	private String fluentwaittime;
	private String productName;
	private String loginType;
	private String browserEnvironmentBaseDir;
	private String uploadFilesDir;
	private String loginPreference;
	private String typeaheadPreference;
	private UserAllocation userAllocator;
	private boolean isSametimeEnabled;
	private boolean isBlueboxEnabled;
	private boolean isFilenetEnabled;
	private String serverURL;
	private boolean recordPerformance;
	private int ftpPort;
	private String ftpUsername;
	private String ftpPassword;
	private String ftpDownloadsDir;
	private String ftpUploadsDir;
	private String securityType;
	private boolean pushVideos;
	private String fileServer;
	private String fileServerPort;
	private boolean useNewUI;
	private boolean iskudosboardenabled;
	
	
	private TestConfigCustom(ITestContext context, TestConfiguration testConfig) {
		this.testConfig = testConfig;
		
		setBvtOption(context.getCurrentXmlTest().getParameter(CustomParameterNames.BVT_OPTION.toString().toLowerCase()));
		setSpecialCharacters(context.getCurrentXmlTest().getParameter(CustomParameterNames.SPECIAL_CHARACTERS.toString().toLowerCase()));
		setAppProperties(context.getCurrentXmlTest().getParameter(CustomParameterNames.DEFAULT_APP_PROPERTIES.toString().toLowerCase()));
		setProductName(context.getCurrentXmlTest().getParameter(CustomParameterNames.PRODUCT_NAME.toString().toLowerCase()));
		setLoginType(context.getCurrentXmlTest().getParameter(CustomParameterNames.LOGIN_TYPE.toString().toLowerCase()));
		setFluentwaittime(context.getCurrentXmlTest().getParameter(CustomParameterNames.FLUENT_WAIT_TIMEOUT.toString().toLowerCase()));
		setBrowserEnvironmentBaseDir(context.getCurrentXmlTest().getParameter(CustomParameterNames.ROOT_FOLDER_NAME.toString().toLowerCase()));
		setUploadFilesDir(context.getCurrentXmlTest().getParameter(CustomParameterNames.UPLOAD_FILES_FOLDER_NAME.toString().toLowerCase()));
		setLoginPreference(context.getCurrentXmlTest().getParameter(CustomParameterNames.USERS_LOGIN_PREFERENCE.toString().toLowerCase()));
		setTypeaheadPreference(context.getCurrentXmlTest().getParameter(CustomParameterNames.USERS_TYPEAHEAD_PREFERENCE.toString().toLowerCase()));
		setUserAllocator(context.getCurrentXmlTest().getParameter(CustomParameterNames.DEFAULT_USERS_PROPERTIES.toString().toLowerCase()));
		setSametimeEnabled(context.getCurrentXmlTest().getParameter(CustomParameterNames.SAMETIME_ENABLED.toString().toLowerCase()));
		setBlueboxEnabled(context.getCurrentXmlTest().getParameter(CustomParameterNames.BLUEBOX_ENABLED.toString().toLowerCase()));
		setFilenetEnabled(context.getCurrentXmlTest().getParameter(CustomParameterNames.FILENET_ENABLED.toString().toLowerCase()));
		setOrgXML_Path(context.getCurrentXmlTest().getParameter(CustomParameterNames.DEFAULT_ORG_XML.toString().toLowerCase()));
		setRecordPerformance(context.getCurrentXmlTest().getParameter(CustomParameterNames.RECORD_PERFORMANCE.toString().toLowerCase()));
		setFtpPort(context.getCurrentXmlTest().getParameter(CustomParameterNames.FTP_PORT.toString().toLowerCase()));
		setFtpUsername(context.getCurrentXmlTest().getParameter(CustomParameterNames.FTP_USERNAME.toString().toLowerCase()));
		setFtpPassword(context.getCurrentXmlTest().getParameter(CustomParameterNames.FTP_PASSWORD.toString().toLowerCase()));
		setFtpDownloadsDir(context.getCurrentXmlTest().getParameter(CustomParameterNames.FTP_DOWNLOADS_DIR.toString().toLowerCase()));
		setFtpUploadsDir(context.getCurrentXmlTest().getParameter(CustomParameterNames.FTP_UPLOADS_DIR.toString().toLowerCase()));
		setServerURL(testConfig.getBrowserURL());
		setSecurityType(context.getCurrentXmlTest().getParameter(CustomParameterNames.SECURITY_TYPE.toString().toLowerCase()));
		setPushVideos(context.getCurrentXmlTest().getParameter(CustomParameterNames.PUSH_VIDEOS.toString().toLowerCase()));
		setFileServer(context.getCurrentXmlTest().getParameter(CustomParameterNames.FILE_SERVER.toString().toLowerCase()));
		setFileServerPort(context.getCurrentXmlTest().getParameter(CustomParameterNames.FILE_SERVER_PORT.toString().toLowerCase()));
		setUseNewUI(context.getCurrentXmlTest().getParameter(CustomParameterNames.USE_NEW_UI.toString().toLowerCase()));
		setIsKudosboardEnabled(context.getCurrentXmlTest().getParameter(CustomParameterNames.IS_KUDOSBOARD_ENABLED.toString().toLowerCase()));
	}
	


	public static TestConfigCustom getInstance() {
		Thread thread = Thread.currentThread();
		TestConfigCustom tcc = instance.get(thread.getName() + thread.getId());
		if (tcc != null) {
			return tcc;
		} else {
			String keys = "";
			for (Map.Entry<String,TestConfigCustom> entry : instance.entrySet()) {
				  keys = keys + (entry.getKey()+",");
			}
			log.error("TestConfiguration Instance has not been set for executing thread. " +
					"\n		Instance key for TestConfig that wasn't found in map: " + thread.getName() + thread.getId() + 
					"\n		Instance keys in map: " + keys);
			throw new RuntimeException(
					"TestConfiguration instance has not been set for executing thread. \nMake sure #load(ITestContext) has been called in your @BeforeMethod before you attempt to use TestConfiguration. Instance key for TestConfig: " + thread.getName() + thread.getId());
		}
	}
	
	public static void load(ITestContext context, TestConfiguration testConfig) {
		Thread thread = Thread.currentThread();
		if(instance.get(thread.getName() + thread.getId()) == null)
			instance.put(thread.getName() + thread.getId(), new TestConfigCustom(context, testConfig));
	}

	public enum CustomParameterNames {
		ROOT_FOLDER_NAME("SeleniumServer"), 
		UPLOAD_FILES_FOLDER_NAME("TestFiles"), 
		USERS_LOGIN_PREFERENCE("email"), 
		USERS_TYPEAHEAD_PREFERENCE("display name"), 
		BVT_OPTION("preBVT"), 
		SPECIAL_CHARACTERS("false"),
		DEFAULT_USERS_PROPERTIES("test_config/extensions/user/UsersProperties.properties"), 
		DEFAULT_APP_PROPERTIES("test_config/app/app.properties"), 
		PRODUCT_NAME("onprem"), 
		LOGIN_TYPE("onprem"),
		SAMETIME_ENABLED("false"),
		BLUEBOX_ENABLED("false"),
		FLUENT_WAIT_TIMEOUT("40"),
		DEFAULT_ORG_XML("test_config/app/org.xml"),
		FILENET_ENABLED("true"),
		RECORD_PERFORMANCE("false"),
		FTP_PORT("8021"),
		FTP_USERNAME("automation"),
		FTP_PASSWORD("conn@uto"),
		FTP_DOWNLOADS_DIR("/downloads/"),
		FTP_UPLOADS_DIR("/"),
		SECURITY_TYPE("false"),
		SHAREPOINT_FILES_WIDGET_PROPERTIES("test_config/app/FVT_Webeditors.properties"),
		PUSH_VIDEOS("false"),
		FILE_SERVER(""),
		FILE_SERVER_PORT("8008"),
		USE_NEW_UI("false"),
		IS_KUDOSBOARD_ENABLED("false");
		

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
	
	public String getBvtOption() {
		return bvtOption;
	}

	private void setBvtOption(String bvtOption) {
		//Set to default if null is passed in
		this.bvtOption = bvtOption != null ? bvtOption : CustomParameterNames.BVT_OPTION.getDefaultValue();
	}
	
	public UserAllocation getUserAllocator() {
		return userAllocator;
	}

	private void setUserAllocator(String propFile) {
		propFile = propFile != null ? propFile : CustomParameterNames.DEFAULT_USERS_PROPERTIES.getDefaultValue();
		this.userAllocator =  UserAllocation.getUserAllocation(propFile);
	}

	public boolean isSpecialCharacters() {
		return isSpecialCharacters;
	}

	private void setSpecialCharacters(String value) {
		value = value != null ? value : CustomParameterNames.SPECIAL_CHARACTERS.getDefaultValue();
		this.isSpecialCharacters = value.toLowerCase().equals("true");
	}

	private void setOrgXML_Path(String path) {
		org_xml_path = path != null ? path : CustomParameterNames.DEFAULT_ORG_XML.getDefaultValue();
	}
	
	public String getOrgXML_Path() {
		return org_xml_path;
	}
	
	public Properties getAppProperties() {
		return appProperties;
	}

	private void setAppProperties(String appPropFile) {
		appPropFile = appPropFile != null ? appPropFile : CustomParameterNames.DEFAULT_APP_PROPERTIES.getDefaultValue();
		this.appProperties = TestProperties.getProperties(appPropFile);
	}

	public String getFluentwaittime() {
		return fluentwaittime;
	}

	private void setFluentwaittime(String fluentwaittime) {
		this.fluentwaittime = fluentwaittime != null ? fluentwaittime : CustomParameterNames.FLUENT_WAIT_TIMEOUT.getDefaultValue();
	}

	public String getProductName() {
		return productName;
	}

	private void setProductName(String productName) {
		this.productName = productName != null ? productName : CustomParameterNames.PRODUCT_NAME.getDefaultValue();
	}
	
	public String getServerURL() {
		return serverURL;
	}
		
	private void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	public String getLoginType() {
		return loginType;
	}

	private void setLoginType(String loginType) {
		this.loginType = loginType != null ? loginType : CustomParameterNames.LOGIN_TYPE.getDefaultValue();
	}

	public String getBrowserEnvironmentBaseDir() {
		return browserEnvironmentBaseDir;
	}

	private void setBrowserEnvironmentBaseDir(String browserEnvironmentBaseDir) {
		browserEnvironmentBaseDir = browserEnvironmentBaseDir != null ? browserEnvironmentBaseDir : CustomParameterNames.ROOT_FOLDER_NAME.getDefaultValue();
		this.browserEnvironmentBaseDir = testConfig.getBrowserEnvironment().constructAbsolutePathToDirectoryFromRoot(browserEnvironmentBaseDir);
	}

	public String getUploadFilesDir() {
		return uploadFilesDir;
	}

	private void setUploadFilesDir(String uploadFilesDir) {
		uploadFilesDir = uploadFilesDir != null ? uploadFilesDir : CustomParameterNames.UPLOAD_FILES_FOLDER_NAME.getDefaultValue();
		this.uploadFilesDir = testConfig.getBrowserEnvironment().constructAbsolutePathToDirectoryFromRoot(getBrowserEnvironmentBaseDir(), uploadFilesDir);
	}
	
	public String getLoginPreference() {
		return loginPreference;
	}

	private void setLoginPreference(String loginPreference) {
		this.loginPreference = loginPreference != null ? loginPreference : CustomParameterNames.USERS_LOGIN_PREFERENCE.getDefaultValue();
	}
	
	public String getTypeaheadPreference() {
		return typeaheadPreference;
	}

	private void setTypeaheadPreference(String typeaheadPreference) {
		this.typeaheadPreference = typeaheadPreference != null ? typeaheadPreference : CustomParameterNames.SAMETIME_ENABLED.getDefaultValue();
	}

	public boolean isSametimeEnabled() {
		return isSametimeEnabled;
	}
	
	private void setSametimeEnabled(String value) {
		value = value != null ? value : CustomParameterNames.SAMETIME_ENABLED.getDefaultValue();
		this.isSametimeEnabled = value.toLowerCase().equals("true");
	}

	public boolean isFilenetEnabled() {
		return isFilenetEnabled;
	}

	private void setFilenetEnabled(String isFilenetEnabled) {
		isFilenetEnabled = isFilenetEnabled != null ? isFilenetEnabled : CustomParameterNames.FILENET_ENABLED.getDefaultValue();
		this.isFilenetEnabled = isFilenetEnabled.toLowerCase().equals("true");
	}

	public boolean isRecordPerformance() {
		return recordPerformance;
	}

	private void setRecordPerformance(String recordPerformance) {
		recordPerformance = recordPerformance != null ? recordPerformance : CustomParameterNames.RECORD_PERFORMANCE.getDefaultValue();
		this.recordPerformance = recordPerformance.toLowerCase().equals("true");
	}

	public int getFtpPort() {
		return ftpPort;
	}

	public void setFtpPort(String ftpPort) {
		int port = -1;
		try {
			if (ftpPort != null) {
				port = Integer.parseInt(ftpPort);
				if (port < 1 || port > 65535) {
					port = -1;
				}
			}
		} catch (NumberFormatException e) {}
		try {
			if (port == -1) {
				port = Integer.parseInt(CustomParameterNames.FTP_PORT.getDefaultValue());
			}
		} catch (NumberFormatException e) {}
		this.ftpPort = port;
	}

	public String getFtpUsername() {
		return ftpUsername;
	}

	public void setFtpUsername(String ftpUsername) {
		ftpUsername = ftpUsername != null ? ftpUsername : CustomParameterNames.FTP_USERNAME.getDefaultValue();
		this.ftpUsername = ftpUsername;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public void setFtpPassword(String ftpPassword) {
		ftpPassword = ftpPassword != null ? ftpPassword : CustomParameterNames.FTP_PASSWORD.getDefaultValue();
		this.ftpPassword = ftpPassword;
	}

	public String getFtpDownloadsDir() {
		return ftpDownloadsDir;
	}

	public void setFtpDownloadsDir(String ftpDownloadsDir) {
		ftpDownloadsDir = ftpDownloadsDir != null ? ftpDownloadsDir : CustomParameterNames.FTP_DOWNLOADS_DIR.getDefaultValue();
		this.ftpDownloadsDir = ftpDownloadsDir;
	}

	public String getFtpUploadsDir() {
		return ftpUploadsDir;
	}

	public void setFtpUploadsDir(String ftpUploadsDir) {
		ftpUploadsDir = ftpUploadsDir != null ? ftpUploadsDir : CustomParameterNames.FTP_UPLOADS_DIR.getDefaultValue();
		this.ftpUploadsDir = ftpUploadsDir;
	}

	/**
	 * Method to switch to organization desired
	 * @param org
	 */
	public void switchToOrg(OrgConfig org){		
		log.info("INFO: Switching to organization: " + org.getName());
		this.getTestConfig().updateBrowserURL(org.getURI());
		
	}

	public void resetOrg(){
		log.info("INFO: Reset Organization");
		this.getTestConfig().updateBrowserURL(getServerURL());
	}
	
	public TestConfiguration getTestConfig() {
		return testConfig;
	}

	public boolean isBlueboxEnabled() {
		return isBlueboxEnabled;
	}
	
	private void setBlueboxEnabled(String value) {
		value = value != null ? value : CustomParameterNames.BLUEBOX_ENABLED.getDefaultValue();
		this.isBlueboxEnabled = value.toLowerCase().equals("true");
	}
	
	// for security ivt deployment types
	public String getSecurityType() {
		return securityType;
	}


	private void setSecurityType(String securityType) {
		//Set to default if null is passed in
		this.securityType = securityType == null ? CustomParameterNames.SECURITY_TYPE.getDefaultValue() : securityType ;
	}
	
	public boolean getPushVideos() {
		return pushVideos;
	}
	
	public void setPushVideos(String value) {
		value = value != null ? value : CustomParameterNames.PUSH_VIDEOS.getDefaultValue();
		this.pushVideos = value.toLowerCase().equals("true");
	}
	
	public String getFileServer() {
		return fileServer;
	}
	
	public String getFileServerPort() {
		return fileServerPort;
	}
	
	public void setFileServer(String fileServer) {
		this.fileServer = fileServer == null ? CustomParameterNames.FILE_SERVER.getDefaultValue() : fileServer;
	}
	
	public void setFileServerPort(String fileServer) {
		this.fileServerPort = fileServerPort == null ? CustomParameterNames.FILE_SERVER_PORT.getDefaultValue() : fileServerPort;
	}
	
	public boolean getUseNewUI() {
		return useNewUI;
	}
	
	public void setUseNewUI(String value) {
		value = value != null ? value : CustomParameterNames.USE_NEW_UI.getDefaultValue();
		this.useNewUI = value.toLowerCase().equals("true");
	}
	
	public void setIsKudosboardEnabled(String value) {
		value = value != null ? value : CustomParameterNames.IS_KUDOSBOARD_ENABLED.getDefaultValue();
		this.iskudosboardenabled = value.toLowerCase().equals("true");
	}
	
	public boolean getIsKudosboardEnabled() {
		return iskudosboardenabled;
	}
	
	
	
}
