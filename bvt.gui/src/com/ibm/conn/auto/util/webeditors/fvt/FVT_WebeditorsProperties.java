package com.ibm.conn.auto.util.webeditors.fvt;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import com.ibm.atmn.waffle.utils.FileIOHandler;
import com.ibm.conn.auto.util.TestConfigCustom.CustomParameterNames;

public class FVT_WebeditorsProperties {
	
	private static final Logger log = LoggerFactory.getLogger(FVT_WebeditorsProperties.class);
	private static final String propertiesPath = CustomParameterNames.SHAREPOINT_FILES_WIDGET_PROPERTIES.getDefaultValue();
	private static final Properties properties;
	
	static {
		properties = FileIOHandler.loadExternalProperties(propertiesPath);

		final String noPropertiesLoadedErrMsg = "No properties were loaded for the Sharepoint Files iWidget! Path to properties is: " + propertiesPath;
		if(properties.size() == 0) log.error(noPropertiesLoadedErrMsg);
		Assert.assertFalse(properties.size() == 0, noPropertiesLoadedErrMsg);

		TITLE_ON_CONNECTIONS = getProperty("TITLE_ON_CONNECTIONS");
		
		SHAREPOINT_URL_SCHEME = getProperty("SHAREPOINT_URL_SCHEME");
		SHAREPOINT_COLLECTION_PATH = getProperty("SHAREPOINT_COLLECTION_PATH");
		SHAREPOINT_CONTENT_PATH = getProperty("SHAREPOINT_CONTENT_PATH");
		SHAREPOINT_CONTENT_TITLE = getProperty("SHAREPOINT_CONTENT_TITLE");
		
		BVT_SHAREPOINT_SERVER = getProperty("BVT_SHAREPOINT_SERVER");
		CONNECTIONS_BUNDLE_COMPONENT = getProperty("CONNECTIONS_BUNDLE_COMPONENT");
		SHAREPOINT_BUNDLE_NAME = getProperty("SHAREPOINT_BUNDLE_NAME");
		
		SHAREPOINT_SERVER_NAME = getProperty("SHAREPOINT_SERVER_NAME");
		SHAREPOINT_USERNAME = getProperty("SHAREPOINT_USERNAME");
		SHAREPOINT_PASSWORD = getProperty("SHAREPOINT_PASSWORD");
		
		SHAREPOINT_WEBSITE_TIMEOUT_SEC = tryParseInt(getProperty("SHAREPOINT_WEBSITE_TIMEOUT_SEC"), 10); // 10 is just a default value
		SHAREPOINT_SERVER_PORT = tryParseInt(getProperty("SHAREPOINT_SERVER_PORT"), -1); // URL port javadoc: "Specifying a port number of -1 indicates that the URL should use the default port for the protocol."

		WIDGET_NO_CONFIG_ERROR_MSG = getProperty("WIDGET_NO_CONFIG_ERROR_MSG"); 
		
		BVT_WOPI_COMPONENT = getProperty("BVT_WOPI_COMPONENT");
        BVT_WOPI_EXCEL = getProperty("BVT_WOPI_EXCEL");
        BVT_WOPI_WORD = getProperty("BVT_WOPI_WORD");
        BVT_WOPI_PPT = getProperty("BVT_WOPI_PPT");
        BVT_WOPI_MOCK = getProperty("BVT_WOPI_MOCK");
        INVALID_WOPI_URL = getProperty("INVALID_WOPI_URL");
        
        final String propertiesNotEmptyErrMsg = "Not all properties were assigned! Remaining unassigned properties located at '" + propertiesPath + "': " + properties.keySet();
		if(!properties.isEmpty()) log.error(propertiesNotEmptyErrMsg);
		Assert.assertFalse(!properties.isEmpty(), propertiesNotEmptyErrMsg);
	}

	private static String getProperty(String key) {
		final String propertyNotFoundErrMsg = "Property '"+ key +"' was not found in the properties file '" + propertiesPath +"'.";
		if(!properties.containsKey(key)) log.error(propertyNotFoundErrMsg);
		Assert.assertFalse(!properties.containsKey(key), propertyNotFoundErrMsg);
		
		return (String) properties.remove(key);
	}

	// any fields found in this class will also need to be in the prop file indicated in "CustomParameterNames.SHAREPOINT_FILES_WIDGET_PROPERTIES"

	public static final String
		TITLE_ON_CONNECTIONS,
		SHAREPOINT_URL_SCHEME, SHAREPOINT_COLLECTION_PATH, SHAREPOINT_CONTENT_PATH, SHAREPOINT_CONTENT_TITLE,
		BVT_SHAREPOINT_SERVER,
		CONNECTIONS_BUNDLE_COMPONENT,
		SHAREPOINT_BUNDLE_NAME,
		SHAREPOINT_SERVER_NAME,
		SHAREPOINT_USERNAME, SHAREPOINT_PASSWORD,
		WIDGET_NO_CONFIG_ERROR_MSG,
		BVT_WOPI_COMPONENT,
		BVT_WOPI_EXCEL,
		BVT_WOPI_WORD,
		BVT_WOPI_PPT,
		BVT_WOPI_MOCK,
		INVALID_WOPI_URL
		;

	public static final int 
		SHAREPOINT_WEBSITE_TIMEOUT_SEC, SHAREPOINT_SERVER_PORT;

	private static int tryParseInt(String integerStr, int defaultValue) {
		int returnValue = defaultValue;
		try {
			if (integerStr != null && !integerStr.isEmpty()) {
				returnValue = Integer.parseInt(integerStr);
		    }	
		}
		finally {
			// nothing to do; just return the defaultValue
		}
		return returnValue;
	}

}
