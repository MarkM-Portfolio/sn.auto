package com.ibm.conn.auto.webui.multi;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.webui.BlogsUI;

public class BlogsUIMulti extends BlogsUI{

	public BlogsUIMulti(RCLocationExecutor driver) {
		super(driver);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setBlogAsPrimary(String BlogsName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void selectBlogsDropdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeAdminSettings() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void typeCommentForm(String comment) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCommIdeationBlogLink(BaseCommunity community){
		return "css=h4[class='lotusBreakWord']>a:contains(" + community.getName() + ")";
	}
	
	
}
