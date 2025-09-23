package com.ibm.conn.auto.webui.onprem;

import com.ibm.conn.auto.webui.constants.BlogsUIConstants;
import org.testng.Assert;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.appobjects.base.BaseCommunity;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.Helper;
import com.ibm.conn.auto.webui.BlogsUI;

public class BlogsUIOnPrem extends BlogsUI {

	public BlogsUIOnPrem(RCLocationExecutor driver) {
		super(driver);
	}

	@Override
	public void setBlogAsPrimary(String BlogsName) {
		// Set as default
		clickLink(BlogsUIConstants.BlogsSetAsPrimaryBlog);
		// Verify the change
		Assert.assertTrue(driver.isTextPresent("You have set [" + BlogsName + "] to be your primary blog"));
	}

	@Override
	protected void selectBlogsDropdown() {
		// cloud only
	}

	@Override
	public void changeAdminSettings() {
		// Edit the Handle of Blog to server as Blogs Homepage
		clickLink(BlogsUIConstants.Administration);
		fluentWaitPresent(BlogsUIConstants.BlogsSiteSettingsHandleOfBlog);
		clearText(BlogsUIConstants.BlogsSiteSettingsHandleOfBlog);
		typeText(BlogsUIConstants.BlogsSiteSettingsHandleOfBlog, Helper.stamp(Data.getData().commonHandle));
		// Save and verify the change
		clickSaveButton();
		fluentWaitTextPresent("Change saved.");
	}

	@Override
	protected void typeCommentForm(String comment) {
		if (driver.isElementPresent(BlogsUIConstants.BlogsCommentTextArea))
			typeText(BlogsUIConstants.BlogsCommentTextArea, comment);
		else
			typeNativeInCkEditor(comment);
			
	}

	@Override
	public String getCommIdeationBlogLink(BaseCommunity community){
		return "css=td[class='lotusFirstCell']>h4[class='lotusBreakWord']>a:contains(" + community.getName() + ")";
	}
	
	
}
