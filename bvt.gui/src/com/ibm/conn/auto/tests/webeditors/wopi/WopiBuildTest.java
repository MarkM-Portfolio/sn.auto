package com.ibm.conn.auto.tests.webeditors.wopi;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.ibm.conn.auto.tests.webeditors.O365BaseTest;
import com.ibm.conn.auto.webui.OfficeOnlineUI.FileSet;

public final class WopiBuildTest extends O365BaseTest {

	@Override
	protected FileSet getFileSet() {
		return FileSet.NONE;
	}

    /**
    * <ul>
    * <li><B>Office Online BVT Test - wopiBuildMockFileTest
    * <li></li>
    * <li><B>Step:</B>Login on Connections</li>
    * <li><B>Step:</B>Open a WOPI url with no fileID</li>
    * <li><B>Assert:</B>Verify if error page is up, showing WOPI is build and up</li>
    * </ul>*/
    @Test(groups = {"WE_BVT", "OO_BVT", "OO_BUILD_BVT"}, invocationCount = 1)
    public void wopiBuildMockFileTest(){
         
         Assert.assertEquals(driver.getTitle(), "Page Not Found");
         log.info("ASSERT PASSED: WOPI is Up!");
    }
     
    @Override
    protected String getComponent() {
       return BVT_WOPI_MOCK;
    }


}
