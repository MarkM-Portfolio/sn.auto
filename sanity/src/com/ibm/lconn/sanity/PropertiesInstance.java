package com.ibm.lconn.sanity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

public class PropertiesInstance {
	
	private static HashMap<String, Properties> propertiesMap = new HashMap<String, Properties>();
	
	private PropertiesInstance(){}
	
	public static Properties getInstance(String fileName) {
		long modifiesDate = (new File(fileName)).lastModified();
		String fileNameDate = fileName + modifiesDate;
		if(propertiesMap.containsKey(fileNameDate)){
			return propertiesMap.get(fileNameDate);
		}
		else {
			Properties prop = new Properties();
			
			try {
				prop.load(new FileInputStream(fileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			propertiesMap.put(fileNameDate, prop);
			return prop;
		}
	}

}
