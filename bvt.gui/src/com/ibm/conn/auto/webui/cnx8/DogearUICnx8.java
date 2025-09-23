package com.ibm.conn.auto.webui.cnx8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseDogear;
import com.ibm.conn.auto.appobjects.base.BaseForum;

public class DogearUICnx8 extends HCBaseUI {
	
	protected static Logger log = LoggerFactory.getLogger(DogearUICnx8.class);
	
	public DogearUICnx8(RCLocationExecutor driver) {
		super(driver);
	}
	
	/**
	 * getBookmarkLink -
	 * @param bookmark
	 * @return
	 */
	public static String getBookmarkLink(BaseDogear bookmark){
		return "//table[@class='lotusTable dogearTableFix']//a[contains(text(),'"+bookmark.getTitle()+"')]";
	}
}
