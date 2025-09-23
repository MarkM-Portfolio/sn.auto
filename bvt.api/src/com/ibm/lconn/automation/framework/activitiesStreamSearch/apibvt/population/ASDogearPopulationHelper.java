package com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.population;

import java.util.logging.Logger;

import org.apache.abdera.model.Entry;

import com.ibm.lconn.automation.framework.activitiesStreamSearch.apibvt.utils.PopStringConstantsAS;
import com.ibm.lconn.automation.framework.activitiesStreamSearch.utils.nodes.FvtMasterLogsClassPopulation;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser;
import com.ibm.lconn.automation.framework.search.rest.api.RestAPIUser.UserType;
import com.ibm.lconn.automation.framework.services.common.ServiceEntry;
import com.ibm.lconn.automation.framework.services.common.nodes.Bookmark;
import com.ibm.lconn.automation.framework.services.dogear.DogearService;

public class ASDogearPopulationHelper {

	
	private static DogearService service;
	private static String bookmarkLink = null;

	
	protected static Logger LOGGER = FvtMasterLogsClassPopulation.LOGGER;

	
	
	
	public ASDogearPopulationHelper() throws Exception {
		RestAPIUser restAPIUser = new RestAPIUser(UserType.ASSEARCH);
		ServiceEntry bookmarkEntry = restAPIUser.getService("dogear");
		restAPIUser.addCredentials(bookmarkEntry);
		service = new DogearService(restAPIUser.getAbderaClient(),
				bookmarkEntry);
		
	}

	

	public void populate() {
		try {
			
			createBookmarkWithPermissions(
					PopStringConstantsAS.PUBLIC_STANDALONE_BOOKMARK_TITLE + " "
							+ PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.PUBLIC_STANDALONE_BOOKMARK_CONTENT,
					"Public",
					PopStringConstantsAS.PUBLIC_STANDALONE_BOOKMARK_TAG);

			createBookmarkWithPermissions(
					PopStringConstantsAS.PRIVATE_STANDALONE_BOOKMARK_TITLE
							+ " " + PopStringConstantsAS.eventIdent,
					PopStringConstantsAS.PRIVATE_STANDALONE_BOOKMARK_CONTENT,
					"Private",
					PopStringConstantsAS.PRIVATE_STANDALONE_BOOKMARK_TAG);
		} catch (Exception e) {
			LOGGER.fine("Exception in Dogear population: " + e.getMessage());
		}
	}

	// *******************************************************************************************************************
	// *******************************************************************************************************************
	// Working functions
	// *******************************************************************************************************************
	// *******************************************************************************************************************

	public void createBookmarkWithPermissions(String bookmarkTitle,
			String bookmarkContent, String bookmarkPermission,
			String bookmarkTags) {
		if (service != null) {
			if (bookmarkPermission.equals("Private")) {
				bookmarkLink = "http://www.private1.com";
			} else if (bookmarkPermission.equals("Public")) {
				bookmarkLink = "http://www.public1.com";
			}

			Bookmark newBookmark = new Bookmark(bookmarkTitle, bookmarkContent,
					bookmarkLink, bookmarkTags);
			if (bookmarkPermission.equals("Private")) {
				newBookmark.setIsPrivate(true);
			}
			Entry result = (Entry) service.createBookmark(newBookmark);
		}
	}
}