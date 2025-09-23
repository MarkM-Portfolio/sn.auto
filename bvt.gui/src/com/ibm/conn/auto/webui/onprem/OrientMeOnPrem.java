package com.ibm.conn.auto.webui.onprem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.OrientMeUI;

public class OrientMeOnPrem extends OrientMeUI {

	private static Logger log = LoggerFactory.getLogger(OrientMeOnPrem.class);
	
	public OrientMeOnPrem(RCLocationExecutor driver) {
		super(driver);
	}
	


}
