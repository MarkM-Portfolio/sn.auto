package com.ibm.lconn.automation.framework.services.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import com.ibm.lconn.automation.framework.services.profiles.nodes.ProfilePerspective;

public class SetUsersId {

	static public boolean instance_flag = false;

	static int index = 0;

	static int usersNumber = 17;

	static public void SetUsersIdOnce() {
		try {
			for (int i = 0; i < usersNumber; i++) {
				if (i != 13 ) {
					index = i;
					ProfilePerspective user = new ProfilePerspective(i, true);

					ProfileData profile = ProfileLoader.PROFILES_ARRAY.get(i);
					profile.setUserId(user.getUserId());
					profile.setKey(user.getKey());
					profile.setRealName(user.getRealName());
					ProfileLoader.PROFILES_ARRAY.set(i, profile);
					LCService.getApiLogger().debug(
							user.getUserName() + " : " + i + " : "
									+ user.getUserId());
				}
			}

			instance_flag = true;

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("You have bad user in your prop file");
			System.out.println("At userIndex:" + index);
			e.printStackTrace();
		}

	}

}
