package com.ibm.lconn.automation.framework.services.wikis;

import static org.testng.AssertJUnit.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.gatekeeper.GateKeeperService;

public class WikisGateKeeperServiceWrapper
{
  protected static GateKeeperService gateKeeperService;

  private final static String OnPremOrgId = "00000000-0000-0000-0000-000000000000";

  protected static Map<String, Boolean> gateKeeperSettings = new HashMap<String, Boolean>();

  protected static WikisGateKeeperServiceWrapper instance;

  public static synchronized WikisGateKeeperServiceWrapper getInstance()
  {
    if (null == instance)
      instance = new WikisGateKeeperServiceWrapper();
    return instance;

  }

  public boolean getGateKeeperSetting(String gateKeeperName) throws IOException
  {
    UsersEnvironment userEnv = new UsersEnvironment();
    UserPerspective profilesAdminUser = userEnv.getLoginUserEnvironment(StringConstants.ADMIN_USER,
        Component.PROFILES.toString());
gateKeeperService = profilesAdminUser.getGateKeeperService();
    if (gateKeeperSettings.get(gateKeeperName) != null)
    {
      return gateKeeperSettings.get(gateKeeperName);
    }
    else
    {
      String gkSetting = gateKeeperService.getGateKeeperSetting(OnPremOrgId, gateKeeperName);
      assertEquals("Get gatekeeper setting" + gateKeeperService.getDetail(), 200, gateKeeperService.getRespStatus());
      gateKeeperSettings.put(gateKeeperName, gkSetting.contains("\"value\": true"));
      return gkSetting.contains("\"value\": true");
    }
  }
}
