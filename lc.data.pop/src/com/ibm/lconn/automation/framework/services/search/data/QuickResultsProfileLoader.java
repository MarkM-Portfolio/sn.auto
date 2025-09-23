package com.ibm.lconn.automation.framework.services.search.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;


import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;


public class QuickResultsProfileLoader extends ProfileLoader {

	private static boolean _dataLoaded = false;
	private static ArrayList<QuickResultsProfileData> QR_PROFILES_ARRAY  ;
	protected final static Logger logger = Logger
			.getLogger(QuickResultsProfileLoader.class.getName());
	private static void loadData() throws FileNotFoundException, IOException {

		loadProfileProperties();
		QR_PROFILES_ARRAY = new ArrayList<QuickResultsProfileData> ();
		String[] profileNames = _profileProperties
				.getProperty("profilesToLoad").split(",");
		
		for (String profile : profileNames) {
			ProfileData profileData = createProfileData(profile);
			
			QuickResultsProfileData qrProfileData = new QuickResultsProfileData(
					profileData);
			qrProfileData.setIsQuickResults( true);
			
			QR_PROFILES_ARRAY.add(qrProfileData);
		}

		_dataLoaded = true;
	}

	public static QuickResultsProfileData getQuickResultsProfile()
			throws FileNotFoundException, IOException {

		if (!_dataLoaded) {
			QuickResultsProfileLoader.loadData();
		}
		return QR_PROFILES_ARRAY.get(2);

	}

	public static QuickResultsProfileData getQuickResultsProfile(int index)
			throws FileNotFoundException, IOException {
		if (!_dataLoaded) {
			QuickResultsProfileLoader.loadData();
		}
		return QR_PROFILES_ARRAY.get(index);
	}
}
