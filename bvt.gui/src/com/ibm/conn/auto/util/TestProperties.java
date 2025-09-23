package com.ibm.conn.auto.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.ibm.atmn.waffle.utils.FileIOHandler;

/**
 * Holds properties. Doesn't load same property file more than once.
 * 
 * @author Ilya
 *
 */
public class TestProperties {
	
	private static volatile Map<String, Properties> properties = Collections.synchronizedMap(new HashMap<String, Properties>());
	
	public static synchronized Properties getProperties(String filePath) {
		Properties prop;
		if (properties.containsKey(filePath)) {
			prop = properties.get(filePath);
		} else {
			prop = FileIOHandler.loadExternalProperties(filePath);
			properties.put(filePath, prop);
		}
		return prop;
	}

}
