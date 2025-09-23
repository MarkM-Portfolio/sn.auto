package com.ibm.conn.auto.webui.production;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.webui.BlogsUI;

public class BlogsUIProduction extends BlogsUI {

	public BlogsUIProduction(RCLocationExecutor driver) {
		super(driver);
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
		return "css=div[widgetid^='lconn_communityblogs_multipleideation_IdeationBlogsView_'] h4[class='lotusBreakWord']>a:contains(" + community.getName() + ")";
	}
	
}
