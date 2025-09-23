package com.ibm.lconn.automation.framework.services.common;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SetProfileData {

	static public boolean instance_flag = false;

	static public void SetProfileDataOnce() {

		System.out.println("=============================================");
		SystemInfo.DisplayJavaVersion();
		System.out.println("=============================================");

		// if SERVER_URL not set, use default value in testEnv
		if (URLConstants.SERVER_URL.equalsIgnoreCase("")) {
			TestNGEnv.setTestEnv();
		}

		try {
			ProfileLoader.getProfiles();
			instance_flag = true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SetUsersId.SetUsersIdOnce();
	}

}
