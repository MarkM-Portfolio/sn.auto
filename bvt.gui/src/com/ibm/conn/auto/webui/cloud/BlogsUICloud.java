package com.ibm.conn.auto.webui.cloud;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.webui.BlogsUI;
import com.ibm.conn.auto.webui.constants.BlogsUIConstants;

public class BlogsUICloud extends BlogsUI {

	public BlogsUICloud(RCLocationExecutor driver) {
		super(driver);
	}

	@Override
	public void setBlogAsPrimary(String BlogsName) {
		// no implementation

	}

	@Override
	protected void selectBlogsDropdown() {
		clickLink("css=a[aria-owns='blogsDropDownMenu']");
		fluentWaitPresent("css=#dijit_MenuItem_0_text");
		clickLink("css=#dijit_MenuItem_0_text");
	}

	@Override
	public void changeAdminSettings() {
		// Edit the Handle of Blog to server as Blogs Homepage
		clickLink(BlogsUIConstants.blogsSettings);
		fluentWaitPresent(BlogsUIConstants.blogsSettingsDisplay);
		clearText(BlogsUIConstants.blogsSettingsDisplay);
		sleep(500);
		typeText(BlogsUIConstants.blogsSettingsDisplay, "50");
		// Save and verify the change
		clickLink(BlogsUIConstants.blogsSettingsUpdateButton);
		fluentWaitTextPresent("Saved changes to blog settings");
	}

	@Override
	protected void typeCommentForm(String comment) {	
		this.getFirstVisibleElement(BlogsUIConstants.scBlogsCommentTextArea).click();
		typeInCkEditor(comment);		
	}
		
	@Override
	public String getCommIdeationBlogLink(BaseCommunity community){
		return "css=div[widgetid^='lconn_communityblogs_multipleideation_IdeationBlogsView_'] h4[class='lotusBreakWord']>a:contains(" + community.getName() + ")";
	}
	
}
