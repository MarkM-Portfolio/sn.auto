package com.ibm.lconn.automation.framework.search.rest.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHandler {
	private Properties _testProperties;

	private static PropertiesHandler _instance;

	private PropertiesHandler() {
		InputStream ins = ClassLoader
				.getSystemResourceAsStream("com/ibm/lconn/automation/framework/search/rest/api/searchAPI.properties");
		_testProperties = new Properties();
		try {
			_testProperties.load(ins);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static PropertiesHandler getInstance() {
		if (_instance == null) {
			_instance = new PropertiesHandler();
		}

		return _instance;
	}

	public String getProperty(String key) {
		return _testProperties.getProperty(key);
	}
}
