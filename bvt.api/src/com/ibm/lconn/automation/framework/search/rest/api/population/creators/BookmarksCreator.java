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
import com.ibm.lconn.automation.framework.search.rest.api.SearchRestAPIUtils.Purpose;
import com.ibm.lconn.automation.framework.search.rest.api.population.PopulatedData;
import com.ibm.lconn.automation.framework.search.rest.api.population.Populator;
import com.ibm.lconn.automation.framework.services.common.LCServiceException;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.StringConstants;
import com.ibm.lconn.automation.framework.services.common.StringConstants.Permissions;
import com.ibm.lconn.automation.framework.services.common.Utils;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.communities.CommunitiesService;
import com.ibm.lconn.automation.framework.services.dogear.DogearService;
import com.ibm.lconn.automation.framework.services.search.data.Application;

public class BookmarksCreator {
	private final static Logger LOGGER = Populator.LOGGER_POPUILATOR;

	private DogearService dogerService;

	public BookmarksCreator() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.LOGIN);
		ServiceEntry bookmarkEntry = restAPIUser.getService("dogear");
		restAPIUser.addCredentials(bookmarkEntry);
		try {
			dogerService = new DogearService(restAPIUser.getAbderaClient(), bookmarkEntry);
		} catch (LCServiceException e) {

			LOGGER.log(Level.WARNING, "The bookmark is not created" + " LCServiceException: " + e.toString());
			assertTrue("Dogear service problem, bookmark is not created", false);
		}

	}

	public void createBookmark(Permissions permission) throws IOException {
		String title = SearchRestAPIUtils.generateTitle(permission, Application.bookmark);
		String tag = SearchRestAPIUtils.generateTagValue(Purpose.SEARCH); // tag
		// is
		// inverted
		// execId
		String bookmarkDescription = SearchRestAPIUtils.generateDescription(title);// Content is inverted title

		Bookmark newBookmark = new Bookmark(title.toString(), bookmarkDescription,
				"http://" + tag + permission + ".com", tag);
		if (permission.equals(Permissions.PRIVATE)) {
			newBookmark.setIsPrivate(true);
		}
		LOGGER.fine("Create bookmark: " + newBookmark.toString());

		if (dogerService != null) {
			ExtensibleElement response = dogerService.createBookmark(newBookmark);
			Element codeElement = response.getExtension(new QName("api", "code"));
			if (codeElement != null) {
				LOGGER.log(Level.WARNING, "The bookmark is not created");
				assertTrue("Dogear service problem, bookmark is not created", false);
			} else {
				PopulatedData.getInstance().setPopulatedLcEntry(newBookmark, permission);
				LOGGER.fine("Bookmark created: " + response.toString());
			}
		} else {
			LOGGER.log(Level.WARNING, "The Bookmark service is NULL.");
			assertTrue("Dogear service problem, bookmark is not created", false);
		}
	}

}
