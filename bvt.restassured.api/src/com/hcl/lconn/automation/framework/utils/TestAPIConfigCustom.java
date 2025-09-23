package com.hcl.lconn.automation.framework.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;

import com.ibm.atmn.waffle.core.TestConfiguration;
import com.ibm.atmn.waffle.extensions.user.UserAllocation;

public class TestAPIConfigCustom {

	private static Logger log = LoggerFactory.getLogger(TestAPIConfigCustom.class);
	
	private volatile static Map<String, TestAPIConfigCustom> instance = new ConcurrentHashMap<String, TestAPIConfigCustom>();
	private TestConfiguration testConfig;
	
	private String serverURL;
	private UserAllocation userAllocator;
	
	
	private TestAPIConfigCustom(ITestContext context, TestConfiguration testConfig) {
		this.testConfig = testConfig;
		
		setUserAllocator(context.getCurrentXmlTest().getParameter(CustomParameterNames.DEFAULT_USERS_PROPERTIES.toString().toLowerCase()));
		setServerURL(testConfig.getBrowserURL());
	}
		
	public static TestAPIConfigCustom getInstance() {
		Thread thread = Thread.currentThread();
		TestAPIConfigCustom tcc = instance.get(thread.getName() + thread.getId());
		if (tcc != null) {
			return tcc;
		} else {
			String keys = "";
			for (Map.Entry<String,TestAPIConfigCustom> entry : instance.entrySet()) {
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
			instance.put(thread.getName() + thread.getId(), new TestAPIConfigCustom(context, testConfig));
	}
	
	public enum CustomParameterNames {
		DEFAULT_USERS_PROPERTIES("test_config/extensions/user/UsersProperties.properties");
		
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
	
	public UserAllocation getUserAllocator() {
		return userAllocator;
	}

	private void setUserAllocator(String propFile) {
		propFile = propFile != null ? propFile : CustomParameterNames.DEFAULT_USERS_PROPERTIES.getDefaultValue();
		this.userAllocator =  UserAllocation.getUserAllocation(propFile);
	}
	
	private void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}
	
	public String getServerURL() {
		return serverURL;
	}
	
	public TestConfiguration getTestConfig() {
		return testConfig;
	}
}
