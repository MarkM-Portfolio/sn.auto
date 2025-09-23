/* ***************************************************************** */
/*                                                                   */
/* IBM Confidential                                                  */
/*                                                                   */
/* OCO Source Materials                                              */
/*                                                                   */
/* Copyright IBM Corp. 2015                                          */
/*                                                                   */
/* The source code for this program is not published or otherwise    */
/* divested of its trade secrets, irrespective of what has been      */
/* deposited with the U.S. Copyright Office.                         */
/*                                                                   */
/* ***************************************************************** */

package com.ibm.lconn.automation.framework.services.profiles.util;

import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.testng.Assert;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileEntry;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileFeed;
import com.ibm.lconn.automation.framework.services.profiles.model.ProfileService;
import com.ibm.lconn.automation.framework.services.profiles.base.AbstractTest;

public class ServiceDocUtil extends AbstractTest {

	// static ProfilesService userProfilesService, adminProfilesService;

	public static ProfileService getUserServiceDocument(Transport transport) throws Exception {
		LOGGER.debug("inside get User service document method");
		// get the authenticated users profile service document
		String url = urlBuilder.getProfilesServiceDocument();
		//url = URLBuilder.updateLastMod(url);
		// Service service = mainTransport.doAtomGet(Service.class, url, NO_HEADERS, HTTPResponseValidator.OK);
		ProfileService profilesService = ProfileService.parseFrom(transport.doAtomGet(Service.class, url, NO_HEADERS,
				HTTPResponseValidator.OK));

		return profilesService;

	}

	public static ProfileService getAdminServiceDocument(Transport transport) throws Exception {
		LOGGER.debug("inside get admin service document method");
		// get the authenticated users profile service document
		String url = urlBuilder.getProfilesAdminServiceDocument();
		//url = URLBuilder.updateLastMod(url);
		// Service service = mainTransport.doAtomGet(Service.class, url, NO_HEADERS, HTTPResponseValidator.OK);
		ProfileService profilesService = ProfileService.parseFrom(transport.doAtomGet(Service.class, url, NO_HEADERS,
				HTTPResponseValidator.OK));

		return profilesService;

	}

	public static ProfileEntry getUserProfileEntry(Transport transport) throws Exception {

		ProfileService profilesService = getUserServiceDocument(transport);

		Feed rawFeed = transport.doAtomGet(Feed.class, profilesService.getProfileFeedUrl(), NO_HEADERS, HTTPResponseValidator.OK);
		// prettyPrint(rawFeed);

		// get the profile feed and validate the data
		ProfileFeed profileFeed = new ProfileFeed(rawFeed);
		profileFeed.validate();

		// get my profile entry
		Assert.assertEquals(profileFeed.getEntries().size(), 1, "There should be a single profile entry (my own)");
		ProfileEntry profileEntry = profileFeed.getEntries().get(0);

		return profileEntry;

	}

	public static ProfileEntry getAdminProfileEntry(Transport transport) throws Exception {

		ProfileService profilesService = getAdminServiceDocument(transport);

		Feed rawFeed = transport.doAtomGet(Feed.class, profilesService.getProfileFeedUrl(), NO_HEADERS, HTTPResponseValidator.OK);
		// prettyPrint(rawFeed);

		// get the profile feed and validate the data
		ProfileFeed profileFeed = new ProfileFeed(rawFeed);
		profileFeed.validate();

		// get my profile entry
		// assertEquals("There should be a single profile entry (my own)", 1, profileFeed.getEntries().size());
		ProfileEntry profileEntry = profileFeed.getEntries().get(0);

		return profileEntry;
	}

}
