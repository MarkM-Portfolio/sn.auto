package com.ibm.conn.auto.tests.webeditors.sharepointiwidget;

import static com.ibm.conn.auto.util.webeditors.fvt.FVT_WebeditorsProperties.*;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.ibm.conn.auto.tests.webeditors.ConnectionsBaseTest;
import com.ibm.conn.auto.webui.SharepointWidgetUI;

public final class ShpIWidgetBuildTest extends ConnectionsBaseTest {
  
  protected SharepointWidgetUI sharepointWidgetUI;

  protected String getComponent() {
  	return CONNECTIONS_BUNDLE_COMPONENT;
  }
  
  @BeforeClass(alwaysRun = true)
  public void beforeClass() {
      //Getting the GUI for SharepointIWidgetUI and CommunitiesUI
      log.info("INFO: TEST CONFIG - Aquiring SharepointWidgetUI GUI instance...");
      sharepointWidgetUI = SharepointWidgetUI.getGui(getProductName(), driver);
  }
  
  /**<ul>iWidgetPresentInBundleTest()
   *<li></li>
   *<li><B>Info: Determines that the widget is placed in Connections bundle.</B></li>
   *<li><B>Step: Loads Connections Bundle.</B></li>
   *<li><B>Verify: Checks if the SharePoint iWidget is present there</B> </li></ul>
   */
  @Test(groups = { "WE_BVT", "SP_BVT", "level2"  }, invocationCount = 1)
  public void iWidgetPresentInBundleTest(){
      log.info("INFO: Loading component '" + getComponent() + "'");
      sharepointWidgetUI.loadComponent( getComponent() );
      
      Assert.assertTrue(sharepointWidgetUI.sharepointExistsInConnectionBundleList(), "ASSERT FAILED: Sharepoint iWidget NOT present in Connections Bundle ");
      log.info("ASSERT PASSED: Sharepoint iWidget present in Connections Bundle");
  }

}
