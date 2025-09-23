package com.ibm.lconn.automation.framework.services.common;

import static org.testng.AssertJUnit.assertTrue;
import java.io.IOException;

/**
 * JUnit Tests via Connections API Set multiple users test environment
 * 
 * @author Ping Wang - wangpin@us.ibm.com
 */
public class UsersEnvironment {

	UserPerspective admin, loginUser, otherUser1, otherUser2, impersonatedUser,
			visitor, otherOrgAdmin;

	final static int NONE = -1;

	public UsersEnvironment() {

		if (!SetProfileData.instance_flag) {
			SetProfileData.SetProfileDataOnce();
		}
	}

	/*
	 * public static UsersEnvironment getUsersEnvInstance (){ if (null ==
	 * usersEnvInstance){ usersEnvInstance = new UsersEnvironment(); } return
	 * usersEnvInstance; }
	 */

	public void getImpersonateEnvironment(int user1, int user2,
			String componentName) throws IOException {

		// user1-loginUser, normally, adminUser 
		try {
			loginUser = new UserPerspective(user1, componentName, user2);

		} catch (LCServiceException e) {
			e.printStackTrace();
			assertTrue(e.getMessage()+" from user : "+ProfileLoader.getProfile(user1).getUserName(), false);
		}
		// user2-impersonated user 
		try {
			impersonatedUser = new UserPerspective(user2, componentName, NONE);

		} catch (LCServiceException e) {
			e.printStackTrace();
			assertTrue(e.getMessage()+" from user : "+ProfileLoader.getProfile(user2).getUserName(), false);
		}

	}

	public UserPerspective getLoginUserEnvironment(int userIndex,
			String componentName) throws IOException {

		UserPerspective user=null;
		try {
			user = new UserPerspective(userIndex, componentName);
		} catch (LCServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();			
			assertTrue(e.getMessage()+" from user : "+ProfileLoader.getProfile(userIndex).getUserName(), false);
		}
		return user;
	}

	public UserPerspective getImpersonatedUser() {
		return impersonatedUser;
	}

	public UserPerspective getLoginUser() {
		return loginUser;
	}

	/*
	 * public UserPerspective runAsUser(int userIndex) { if (userIndex ==
	 * StringConstants.ADMIN_USER) { return admin; } else if (userIndex ==
	 * StringConstants.RANDOM1_USER) { return otherUser1; } else if (userIndex
	 * == StringConstants.EXTERNAL_USER) { return visitor; } else if (userIndex
	 * == StringConstants.EMPLOYEE_EXTENDED_USER) { return otherUser1; } else{
	 * return null; } }
	 */

}
