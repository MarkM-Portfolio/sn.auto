package com.ibm.conn.auto.tests.webeditors.wopi;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ibm.conn.auto.tests.webeditors.O365BaseTest;
import com.ibm.conn.auto.webui.OfficeOnlineUI.FileSet;

public final class WopiGKedValidUrlTest extends O365BaseTest {

	/**
	 * <ul>
	 * <li><B>Office Online BVT Test - Verify if WOPI is getting installed and is darklaunched</b></li>
	 * <li>this test evaluates the WOPI server's reaction when faced with a request for a valid file id but the WOPI server itself is darklaunched (GK'ed)</li> <br/>
	 * <li><B>Step:</B>Open an invalid WOPI ppt url</li>
	 * <li><B>Assert:</B>Verify if error page is up, showing WOPI is up</li>
	 * </ul>
	 */
	@Test(groups = {"OO_503_BUILD_BVT", "level2", "darklaunch"}, invocationCount = 1)
	public void darklauchWopiUrlTest() {

		Assert.assertEquals(driver.getTitle(), officeOnlineUI.getExpectedPageTitle());
		log.info("ASSERT PASSED: WOPI is Up but not available - as expected! Message EXP/REC: "+officeOnlineUI.getExpectedPageTitle()+"");
	}

	@Override
	protected String getComponent() {
	  return BVT_WOPI_WORD;
	}

	@Override
	protected FileSet getFileSet() {
		return FileSet.NONE;
	}

}
