package com.ibm.conn.auto;

import com.ibm.atmn.waffle.extensions.user.User;
import com.ibm.conn.auto.config.SetUpMethods2;
import com.ibm.conn.auto.data.Data;
import com.ibm.conn.auto.util.TestConfigCustom;
import com.ibm.conn.auto.webui.HomepageUI;
import com.ibm.conn.auto.webui.onprem.HomepageUIOnPrem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Sample Test/Template , can run with com.ibm.conn.auto.util.RunTests
 * to help with tests under development
 */
public class TestExample extends SetUpMethods2 {
    private static Logger log = LoggerFactory.getLogger(TestExample.class);

    private HomepageUI ui;
    private User testUser;
    private TestConfigCustom cfg;

    @BeforeMethod(alwaysRun=true)
    public void beforeMethod(){
        cfg = TestConfigCustom.getInstance();
    }

    @Test
    public void postMicroBlog(ITestContext context){
        ui = new HomepageUIOnPrem(driver);

//        driver.load(context.getCurrentXmlTest().getParameter("browser_url") + Data.getData().ComponentActivities);
        driver.load(cfg.getTestConfig().getBrowserURL() + Data.getData().ComponentActivities);
        testUser = cfg.getUserAllocator().getUser();
        ui.login(testUser);
    }
}
