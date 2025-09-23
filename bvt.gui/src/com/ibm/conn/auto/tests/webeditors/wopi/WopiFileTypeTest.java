package com.ibm.conn.auto.tests.webeditors.wopi;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ibm.conn.auto.tests.webeditors.O365BaseTest;
import com.ibm.conn.auto.webui.OfficeOnlineUI.FileSet;

public final class WopiFileTypeTest extends O365BaseTest {

	/**
	 * <ul>
	 * <li><B>Office Online BVT Test - O365_EP_4: Verify that when a file of an unsupported type is opened in WOPI, then a "File not Found" error is displayed
	 * <li></li>
	 * <li><B>Step:</B>Login on Connections</li>
	 * <li><B>Step:</B>Open an invalid WOPI ppt url</li>
	 * <li><B>Assert:</B>Verify if error page is up, showing WOPI is up</li>
	 * </ul>
	 */
	@Test(groups = {"WE_BVT", "under_development"}, invocationCount = 1)
	public void unsupportedTypeWopiUrlTest() {

		Assert.assertEquals(driver.getTitle(), "Page Not Found");
		log.info("ASSERT PASSED: WOPI is Up!");
	}

	@Override
	protected String getComponent() {
		return String.format(INVALID_WOPI_URL, "pdf"); // as defined in "O365_EP_4: Verify that when a file of an unsupported type is opened in WOPI, then a "File not Found" error is displayed"
	}

	@Override
	protected FileSet getFileSet() {
		return FileSet.NOEDIT;
	}

}
