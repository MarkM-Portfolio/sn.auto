package com.ibm.lconn.automation.framework.services.files;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import static org.testng.AssertJUnit.assertEquals;

import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Component;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.UserPerspective;
import com.ibm.lconn.automation.framework.services.common.UsersEnvironment;
import com.ibm.lconn.automation.framework.services.gatekeeper.GateKeeperService;

public class FilesGateKeeperServiceWrapper
{
  protected static GateKeeperService gateKeeperService;

  private final static String OnPremOrgId = "00000000-0000-0000-0000-000000000000";

  protected static Map<String, Boolean> gateKeeperSettings = new HashMap<String, Boolean>();

  protected static FilesGateKeeperServiceWrapper instance;

  public static synchronized FilesGateKeeperServiceWrapper getInstance()
  {
    if (null == instance)
      instance = new FilesGateKeeperServiceWrapper();
    return instance;

  }

  public boolean getGateKeeperSetting(String gateKeeperName) throws IOException
  {
	// this method is only working with OnPrem server
	if (StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)  return false;
	
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
