package com.ibm.conn.auto.webui.cnx8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseWiki;

public class WikisUICnx8 extends HCBaseUI {
	
	protected static Logger log = LoggerFactory.getLogger(WikisUICnx8.class);
	
	public WikisUICnx8(RCLocationExecutor driver) {
		super(driver);
	}
	
	/**
	 * getWiki - 
	 * @param wiki
	 * @return
	 */
	public static String getWiki(BaseWiki wiki){
		return "//table[@class='lotusTable']//a[text()='"+wiki.getName()+"']";
	}
}
