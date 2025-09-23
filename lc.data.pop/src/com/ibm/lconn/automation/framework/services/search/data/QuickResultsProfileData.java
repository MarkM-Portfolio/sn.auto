package com.ibm.lconn.automation.framework.services.search.data;

import com.ibm.lconn.automation.framework.services.common.ProfileData;

public class QuickResultsProfileData extends ProfileData {
	private boolean isQuickResults = false;

	public QuickResultsProfileData(String username, String userpassword,
			String useremail, String userid, String userkey, String realName,
			boolean useSSL, boolean isAdmin, boolean isCurrentUser,
			boolean isModerator, boolean isConnectionsAdmin,
			boolean enabledQuickResults, String orgname) {
		super(username, userpassword, useremail, userid, userkey, realName,
				useSSL, isAdmin, isCurrentUser, isModerator,
				isConnectionsAdmin, orgname);
		isQuickResults = enabledQuickResults;
		// TODO Auto-generated constructor stub
	}

	public QuickResultsProfileData(ProfileData profileData) {

		super(profileData.getUserName(), profileData.getPassword(), profileData
				.getEmail(), profileData.getUserId(), profileData.getKey(),
				profileData.getRealName(), true, profileData.isAdmin(),
				profileData.isCurrentUser(), profileData.isModerator(),
				profileData.isConnectionsAdmin(), profileData.getOrg());

	}

	public boolean isQuickResults() {
		return isQuickResults;
	}

	public void setIsQuickResults(boolean isEnabled) {
		isQuickResults = isEnabled;

	}

}
