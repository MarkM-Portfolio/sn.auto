package com.ibm.conn.auto.webui.onprem;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.FilesUI;

public class FilesUIOnPrem extends FilesUI {

	public FilesUIOnPrem(RCLocationExecutor driver) {
		super(driver);
	}

	/**
	 * 
	 * @param file name
	 * */
	public void searchFile(String name) {
		log.info("INFO: searchFile by file name is Cloud only variable skipping for Onprem");
	}
	
	/**
	 * 
	 * @param fileDate
	 * */
	public long getFileCreatedTime(String fileDate) {
		log.info("INFO: getFileCreatedTime is Cloud only variable skipping for Onprem");
		
		return 0;
	}
	
	public String getPublicOrgNavLink() {
		return "Public";
	}
	
}
