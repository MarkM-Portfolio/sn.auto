package com.ibm.conn.auto.tests.webeditors.wopi;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ibm.conn.auto.tests.webeditors.O365BaseTest;
import com.ibm.conn.auto.webui.OfficeOnlineUI.FileSet;

public final class WopiInvalidUrlTest extends O365BaseTest {

	/**
	 * <ul>
	 * <li><B>Office Online BVT Test - O365_EP_2: Confirm an error page is shown when an invalid WOPI URL is requested</b></li>
	 * <li>this test asserts whether the WOPI server is capable of determining that the file type supplied by getComponent() is suited to be edited by Online Office</li>
	 * <br/>
	 * <li><B>Step:</B>Login on Connections</li>
	 * <li><B>Step:</B>Open an invalid WOPI ppt url</li>
	 * <li><B>Assert:</B>Verify if error page is up, showing WOPI is up</li>
	 * </ul>
	 */
	@Test(groups = {"WE_BVT", "OO_BVT", "OO_BUILD_BVT"}, invocationCount = 1)
	public void invalidWopiUrlTest() {

		Assert.assertEquals(driver.getTitle(), "Page Not Found");
		log.info("ASSERT PASSED: WOPI is Up!");
	}

	@Override
	protected String getComponent() {
		return String.format(INVALID_WOPI_URL, "ppt"); // as defined in "O365_EP_2: Confirm an error page is shown when an invalid WOPI URL is requested"
	}

	@Override
	protected FileSet getFileSet() {
		return FileSet.NONE;
	}

}
