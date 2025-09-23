/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2014                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

import static org.testng.AssertJUnit.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.client.AbderaClient;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ProfileData;
import com.ibm.lconn.automation.framework.services.common.ProfileLoader;
import com.ibm.lconn.automation.framework.services.common.ServiceConfig;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.SetProfileData;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.DeploymentType;
import com.ibm.lconn.automation.framework.services.common.URLConstants;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;

public abstract class PopulationHelper {

	protected static AbderaClient client;

	protected static ServiceConfig config;

	private static Abdera abdera;

	private static ProfilesService profilesService;

	private static int loggedInUserIndex = -1;

	abstract protected void populate();

	abstract protected void setUpServiceConfig();

	protected void setIndexForUserToLogin(final int loginIndex) {
		loggedInUserIndex = loginIndex;
	}

	protected void setup() throws Exception {
		setUpProfileData();
		setUpAbdera();
		setUpProfilesServiceConfig();
		setUpServiceConfig();
		login();
	}

	protected ProfileData login() throws Exception {
		assertTrue(
				"Failed because User is not logged in. Please login before populate",
				loggedInUserIndex != -1);
		
		
		ProfileData profData = ProfileLoader.getProfile(loggedInUserIndex);
		PopStringConstantsAS.setTestUserName(profData.getUserName());
		PopStringConstantsAS.setTestUserPwd(profData.getPassword());
		
		return profData;
	}

	protected ProfileData getLoggedInProfile() throws FileNotFoundException,
			IOException {
		return ProfileLoader.getProfile(loggedInUserIndex);
	}

	private void setUpProfileData() throws Exception {
		if (!SetProfileData.instance_flag) {
			SetProfileData.SetProfileDataOnce();
		}
		ProfileData profData = ProfileLoader.getProfile(2);
		
		PopStringConstantsAS.setTestUserName(profData.getUserName());
		PopStringConstantsAS.setTestUserPwd(profData.getPassword());
		
		}

	private void setUpAbdera() {
		abdera = new Abdera();
		client = new AbderaClient(abdera);
		AbderaClient.registerTrustManager();
	}

	private static void setUpProfilesServiceConfig() {
		boolean useSSL = true;
		try {
			config = new ServiceConfig(client, URLConstants.SERVER_URL, useSSL);
		} catch (LCServiceException e1) {
			e1.printStackTrace();
			assertTrue(e1.getMessage(), false);
		}
		ServiceEntry profiles = config.getService("profiles");

		assert (profiles != null);

		if (!(StringConstants.DEPLOYMENT_TYPE == DeploymentType.SMARTCLOUD)) {
			try {
				profilesService = new ProfilesService(client, profiles);
			} catch (LCServiceException e) {
				e.printStackTrace();
			}
			assert (profilesService.isFoundService());
		}
	}
}
