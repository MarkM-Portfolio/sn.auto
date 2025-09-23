package com.ibm.conn.auto.webui.production;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.FilesUI;

public class FilesUIProduction extends FilesUI {

	public FilesUIProduction(RCLocationExecutor driver) {
		super(driver);
	}

	/**
	 * 
	 * @param file name
	 * */
	public void searchFile(String name) {
		log.info("INFO: searchFile by file name is Cloud only variable skipping for Production");
	}
	
	/**
	 * 
	 * @param fileDate
	 * */
	public long getFileCreatedTime(String fileDate) {
		log.info("INFO: getFileCreatedTime is Cloud only variable skipping for Production");
		
		return 0;
	}
	
}
