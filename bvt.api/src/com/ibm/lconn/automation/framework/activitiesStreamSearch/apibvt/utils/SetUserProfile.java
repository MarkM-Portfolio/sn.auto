package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils;

import java.io.IOException;

import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.SetProfileData;

public class SetUserProfile {

	public static void setUserProfileForLogin() throws IOException {
		if (!SetProfileData.instance_flag) {
			SetProfileData.SetProfileDataOnce();
		}

		ProfileData profData = ProfileLoader.getProfile(2);

		PopStringConstantsAS.setLoginUserName(profData.getEmail());
		PopStringConstantsAS.setLoginUserPwd(profData.getPassword());
		PopStringConstantsAS.setLoginUserRealName(profData.getRealName());
		PopStringConstantsAS.setTestUserName(profData.getUserName());
		PopStringConstantsAS.setTestUserPwd(profData.getPassword());
		
	}

	
}