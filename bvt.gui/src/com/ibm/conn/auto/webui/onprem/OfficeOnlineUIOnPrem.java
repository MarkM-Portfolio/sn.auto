package com.ibm.conn.auto.webui.onprem;

import com.ibm.atmn.waffle.core.RCLocationExecutor;
import com.ibm.conn.auto.webui.OfficeOnlineUI;

public class OfficeOnlineUIOnPrem extends OfficeOnlineUI{

  private static final String expectedOnPremPageTitle = "Application is not available";
  
  public OfficeOnlineUIOnPrem(RCLocationExecutor driver) {
    super(driver);
  }

  @Override
  public String getExpectedPageTitle() {
    return expectedOnPremPageTitle;
  }
}
