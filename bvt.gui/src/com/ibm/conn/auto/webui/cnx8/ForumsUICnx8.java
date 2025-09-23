package com.ibm.conn.auto.webui.cnx8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseForum;

public class ForumsUICnx8 extends HCBaseUI {
	
	protected static Logger log = LoggerFactory.getLogger(ForumsUICnx8.class);
	
	public ForumsUICnx8(RCLocationExecutor driver) {
		super(driver);
	}
	
	/**
	 * getForumLink -
	 * @param forum
	 * @return
	 */
	public static String getForumLink(BaseForum forum){
		return "//table[@class='lotusTable']//a[text()='"+forum.getName()+"']";
	}
}
