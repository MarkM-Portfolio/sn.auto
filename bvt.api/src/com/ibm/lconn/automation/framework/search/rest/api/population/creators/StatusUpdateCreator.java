package com.ibm.lconn.automation.framework.search.rest.api.population.creators;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.namespace.QName;

import org.apache.abdera.model.Element;
import org.apache.abdera.model.ExtensibleElement;

import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.profiles.ProfilesService;
import com.ibm.lconn.automation.framework.services.profiles.nodes.Status;
import com.ibm.lconn.automation.framework.services.search.data.Application;

public class StatusUpdateCreator {
	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;

	private ProfilesService profileService;

	public StatusUpdateCreator() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry profileServiceEntry = restAPIUser.getService("profiles");
		try {
			profileService = new ProfilesService(restAPIUser.getAbderaClient(), profileServiceEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING, "The status update is not created" + " LCServiceException: " + e.toString());
			assertTrue("Profiles service problem, status update is not created", false);
		}

	}

	public void createStatusUpdate() throws IOException {
		String update = SearchRestAPIUtils.generateTitle(Permissions.PUBLIC, Application.status_update);
		Status newStatus = new Status(update);

		LOGGER.fine("Create status update: " + newStatus.toString());

		if (profileService != null) {
			ExtensibleElement response = profileService.setProfileStatus(newStatus);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The status update is not created");
				assertTrue("Profiles service problem, status update is not created", false);
			} else {
				PopulatedData.getInstance().setPopulatedLcEntry(newStatus, Permissions.PUBLIC);
				LOGGER.fine("Status update created: " + response.toString());
			}
		} else {
			LOGGER.log(Level.WARNING, "The Profiles service is NULL.");
			assertTrue("Profiles service problem, status update is not created", false);
		}
	}
}
