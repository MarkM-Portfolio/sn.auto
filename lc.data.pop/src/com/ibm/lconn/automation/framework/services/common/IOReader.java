package com.ibm.lconn.automation.framework.services.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class IOReader {
	
	public static Properties loadExternalProperties(String filePath) {

		FileReader fileReader = null;
		try {
			File confFile = new File(filePath);
			fileReader = new FileReader(confFile);
		} catch (FileNotFoundException e) {
			return null;
		}
		Properties conf = new Properties();
		try {
			conf.load(fileReader);
		} catch (IOException e) {}
		finally{
			if(fileReader != null){
				try {
					fileReader.close();
				} catch (IOException e) {}
			}
		}
		return conf;
	}

}
