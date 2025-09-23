package com.ibm.conn.auto.webui.cnx8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseBlog;

public class BlogsUICnx8 extends HCBaseUI {
	
	protected static Logger log = LoggerFactory.getLogger(BlogsUICnx8.class);
	
	public BlogsUICnx8(RCLocationExecutor driver) {
		super(driver);
	}
	
	/**
	 * getBlogLink - 
	 * @param blog
	 * @return
	 */
	public static String getBlogLink(BaseBlog blog) {
		return "//div[@aria-label='Blog list']//div[@class='lotusBoard blogsBoard']//a[@aria-label='"+blog.getName()+"']";
	}
}
