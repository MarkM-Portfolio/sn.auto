package com.ibm.lconn.automation.framework.services.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import com.ibm.lconn.automation.framework.services.profiles.nodes.ProfilePerspective;

public class SetAdminId {

	static public boolean instance_flag = false;

	static public void SetAdminIdOnce() {

		try {
			ProfilePerspective adminP = new ProfilePerspective(0, true);

			ProfileData adminD = ProfileLoader.PROFILES_ARRAY.get(0);
			adminD.setUserId(adminP.getUserId());
			ProfileLoader.PROFILES_ARRAY.set(0, adminD);

			instance_flag = true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
